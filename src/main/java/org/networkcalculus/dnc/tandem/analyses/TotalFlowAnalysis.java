/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.tandem.analyses;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
import org.networkcalculus.num.Num;

public class TotalFlowAnalysis extends AbstractTandemAnalysis {
    @SuppressWarnings("unused")
    private TotalFlowAnalysis() {
    }

    public TotalFlowAnalysis(ServerGraph server_graph) {
        super.server_graph = server_graph;
        super.configuration = new AnalysisConfig();
        super.result = new TotalFlowResults();
    }

    public TotalFlowAnalysis(ServerGraph server_graph, AnalysisConfig configuration) {
        super.server_graph = server_graph;
        super.configuration = configuration;
        super.result = new TotalFlowResults();
    }

    public void performAnalysis(Flow flow_of_interest) throws Exception {
        performAnalysis(flow_of_interest, flow_of_interest.getPath());
    }

    public void performAnalysis(Flow flow_of_interest, Path path) throws Exception {
        Num delay_bound = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num backlog_bound = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();

        for (Server server : path.getServers()) {
            Pair<Num,Num> min_D_B = deriveBoundsAtServer(server);

            delay_bound = Num.getUtils(Calculator.getInstance().getNumBackend()).add(delay_bound, min_D_B.getFirst());
            backlog_bound = Num.getUtils(Calculator.getInstance().getNumBackend()).max(backlog_bound, min_D_B.getSecond());
        }

        ((TotalFlowResults) result).setDelayBound(delay_bound);
        ((TotalFlowResults) result).setBacklogBound(backlog_bound);
    }

    public Pair<Num,Num> deriveBoundsAtServer(Server server) throws Exception {
        // Here's the difference to SFA:
        // TFA needs the arrival bound of all flows at the server, including the flow of
        // interest.
        Set<ArrivalCurve> alphas_server = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration, server);
        // Although the TFA has a flow of interest, DO NOT call
        // computeArrivalBounds(ServerGraph server_graph, AnalysisConfig configuration, Server
        // 							server, Set<Flow> flows_to_bound, Flow flow_of_interest).
        // Otherwise, we would not get the flow of interest's arrivals at this server.

        Set<Num> delay_bounds_server = new HashSet<Num>();
        Set<Num> backlog_bounds_server = new HashSet<Num>();

        Num delay_bound_s__min = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();
        Num backlog_bound_s__min = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();
        for (ArrivalCurve alpha_candidate : alphas_server) {
            // According to the call of computeOutputBound there's no left-over service
            // curve calculation
            ServiceCurve beta_server = server.getServiceCurve();

            Num backlog_bound_server_alpha = Calculator.getInstance().getDncBackend().getBounds().backlog(alpha_candidate, beta_server);
            backlog_bounds_server.add(backlog_bound_server_alpha);

            if (backlog_bound_server_alpha.leq(backlog_bound_s__min)) {
                backlog_bound_s__min = backlog_bound_server_alpha;
            }

            // Is this a single flow, i.e., does fifo per micro flow hold?
            boolean fifo_per_micro_flow = false;
            if (server_graph.getFlows(server).size() == 1) {
                fifo_per_micro_flow = true;
            }

            Num delay_bound_server_alpha;
            if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO
                    || (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL
                    && server.multiplexing() == Multiplexing.FIFO)
                    || fifo_per_micro_flow) {
                delay_bound_server_alpha = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(alpha_candidate, beta_server);
            } else {
                delay_bound_server_alpha = Calculator.getInstance().getDncBackend().getBounds().delayARB(alpha_candidate, beta_server);
            }
            delay_bounds_server.add(delay_bound_server_alpha);

            if (delay_bound_server_alpha.leq(delay_bound_s__min)) {
                delay_bound_s__min = delay_bound_server_alpha;
            }
        }
        ((TotalFlowResults) result).map__server__alphas.put(server, alphas_server);
        ((TotalFlowResults) result).map__server__D_server.put(server, delay_bounds_server);
        ((TotalFlowResults) result).map__server__B_server.put(server, backlog_bounds_server);

        return new Pair<Num,Num>(delay_bound_s__min, backlog_bound_s__min);
    }

    public Map<Server, Set<Num>> getServerDelayBoundMap() {
        return ((TotalFlowResults) result).map__server__D_server;
    }

    public String getServerDelayBoundMapString() {
        return ((TotalFlowResults) result).getServerDelayBoundMapString();
    }

    public Map<Server, Set<Num>> getServerBacklogBoundMap() {
        return ((TotalFlowResults) result).map__server__B_server;
    }

    public String getServerBacklogBoundMapString() {
        return ((TotalFlowResults) result).getServerBacklogBoundMapString();
    }
}