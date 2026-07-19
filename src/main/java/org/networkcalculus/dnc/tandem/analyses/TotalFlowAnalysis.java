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
//import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
//import org.networkcalculus.dnc.curves.ArrivalCurve;
//import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveUtils;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
//import org.networkcalculus.num.Num;
import org.networkcalculus.dnc.backend.number.Number;

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
        Number delay_bound = Number.ZERO;
        Number backlog_bound = Number.ZERO;

        for (Server server : path.getServers()) {
            Pair<Number, Number> min_D_B = deriveBoundsAtServer(server);

            delay_bound = delay_bound.add(min_D_B.getFirst());
            backlog_bound = backlog_bound.max(min_D_B.getSecond());
        }

        ((TotalFlowResults) result).setDelayBound(delay_bound);
        ((TotalFlowResults) result).setBacklogBound(backlog_bound);
    }

    public Pair<Number, Number> deriveBoundsAtServer(Server server) throws Exception {
        // Here's the difference to SFA:
        // TFA needs the arrival bound of all flows at the server, including the flow of
        // interest.
        Set<Curve> alphas_server = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration, server);
        // Although the TFA has a flow of interest, DO NOT call
        // computeArrivalBounds(ServerGraph server_graph, AnalysisConfig configuration, Server
        // 							server, Set<Flow> flows_to_bound, Flow flow_of_interest).
        // Otherwise, we would not get the flow of interest's arrivals at this server.

        Set<Number> delay_bounds_server = new HashSet<Number>();
        Set<Number> backlog_bounds_server = new HashSet<Number>();

        Number delay_bound_s__min = Number.POSITIVE_INFINITY;
        Number backlog_bound_s__min = Number.POSITIVE_INFINITY;
        for (Curve alpha_candidate : alphas_server) {
            // According to the call of computeOutputBound there's no left-over service
            // curve calculation
            Curve beta_server = server.getServiceCurve();

            Number backlog_bound_server_alpha = CurveUtils.backlog(alpha_candidate, beta_server);
            backlog_bounds_server.add(backlog_bound_server_alpha);

            if (backlog_bound_server_alpha.leq(backlog_bound_s__min)) {
                backlog_bound_s__min = backlog_bound_server_alpha;
            }

            // Is this a single flow, i.e., does fifo per micro flow hold?
            boolean fifo_per_micro_flow = false;
            if (server_graph.getFlows(server).size() == 1) {
                fifo_per_micro_flow = true;
            }

            Number delay_bound_server_alpha;
            if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO
                    || (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL
                    && server.multiplexing() == Multiplexing.FIFO)
                    || fifo_per_micro_flow) {
                delay_bound_server_alpha = CurveUtils.delayFIFO(alpha_candidate, beta_server);
            } else {
                delay_bound_server_alpha = CurveUtils.delayARB(alpha_candidate, beta_server);
            }
            delay_bounds_server.add(delay_bound_server_alpha);

            if (delay_bound_server_alpha.leq(delay_bound_s__min)) {
                delay_bound_s__min = delay_bound_server_alpha;
            }
        }
        ((TotalFlowResults) result).map__server__alphas.put(server, alphas_server);
        ((TotalFlowResults) result).map__server__D_server.put(server, delay_bounds_server);
        ((TotalFlowResults) result).map__server__B_server.put(server, backlog_bounds_server);

        return new Pair<Number, Number>(delay_bound_s__min, backlog_bound_s__min);
    }

    public Map<Server, Set<Number>> getServerDelayBoundMap() {
        return ((TotalFlowResults) result).map__server__D_server;
    }

    public String getServerDelayBoundMapString() {
        return ((TotalFlowResults) result).getServerDelayBoundMapString();
    }

    public Map<Server, Set<Number>> getServerBacklogBoundMap() {
        return ((TotalFlowResults) result).map__server__B_server;
    }

    public String getServerBacklogBoundMapString() {
        return ((TotalFlowResults) result).getServerBacklogBoundMapString();
    }
}