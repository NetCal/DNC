/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
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

package de.uni_kl.cs.discodnc.nc.analyses;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.misc.Pair;
import de.uni_kl.cs.discodnc.nc.AbstractAnalysis;
import de.uni_kl.cs.discodnc.nc.Analysis;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TotalFlowAnalysis extends AbstractAnalysis implements Analysis {
    @SuppressWarnings("unused")
    private TotalFlowAnalysis() {
    }

    public TotalFlowAnalysis(Network network) {
        super.network = network;
        super.configuration = new AnalysisConfig();
        super.result = new TotalFlowResults();
    }

    public TotalFlowAnalysis(Network network, AnalysisConfig configuration) {
        super.network = network;
        super.configuration = configuration;
        super.result = new TotalFlowResults();
    }

    public void performAnalysis(Flow flow_of_interest) throws Exception {
        performAnalysis(flow_of_interest, flow_of_interest.getPath());
    }

    public void performAnalysis(Flow flow_of_interest, Path path) throws Exception {
        Num delay_bound = Num.getFactory().createZero();
        Num backlog_bound = Num.getFactory().createZero();

        for (Server server : path.getServers()) {
            Pair<Num> min_D_B = deriveBoundsAtServer(server);

            delay_bound = Num.getUtils().add(delay_bound, min_D_B.getFirst());
            backlog_bound = Num.getUtils().max(backlog_bound, min_D_B.getSecond());
        }

        ((TotalFlowResults) result).setDelayBound(delay_bound);
        ((TotalFlowResults) result).setBacklogBound(backlog_bound);
    }

    public Pair<Num> deriveBoundsAtServer(Server server) throws Exception {
        // Here's the difference to SFA:
        // TFA needs the arrival bound of all flows at the server, including the flow of
        // interest.
        Set<ArrivalCurve> alphas_server = ArrivalBoundDispatch.computeArrivalBounds(network, configuration, server);
        // Although the TFA has a flow of interest, DO NOT call
        // computeArrivalBounds( Network network, AnalysisConfig configuration, Server
        // server, Set<Flow> flows_to_bound, Flow flow_of_interest ).

        Set<Num> delay_bounds_server = new HashSet<Num>();
        Set<Num> backlog_bounds_server = new HashSet<Num>();

        Num delay_bound_s__min = Num.getFactory().getPositiveInfinity();
        Num backlog_bound_s__min = Num.getFactory().getPositiveInfinity();
        for (ArrivalCurve alpha_candidate : alphas_server) {
            // According to the call of computeOutputBound there's no left-over service
            // curve calculation
            ServiceCurve beta_server = server.getServiceCurve();

            Num backlog_bound_server_alpha = Bound.backlog(alpha_candidate, beta_server);
            backlog_bounds_server.add(backlog_bound_server_alpha);

            if (backlog_bound_server_alpha.leq(backlog_bound_s__min)) {
                backlog_bound_s__min = backlog_bound_server_alpha;
            }

            // Is this a single flow, i.e., does fifo per micro flow hold?
            boolean fifo_per_micro_flow = false;
            if (network.getFlows(server).size() == 1) {
                fifo_per_micro_flow = true;
            }

            Num delay_bound_server_alpha;
            if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO
                    || (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL
                    && server.multiplexingDiscipline() == Multiplexing.FIFO)
                    || fifo_per_micro_flow) {
                delay_bound_server_alpha = Bound.delayFIFO(alpha_candidate, beta_server);
            } else {
                delay_bound_server_alpha = Bound.delayARB(alpha_candidate, beta_server);
            }
            delay_bounds_server.add(delay_bound_server_alpha);

            if (delay_bound_server_alpha.leq(delay_bound_s__min)) {
                delay_bound_s__min = delay_bound_server_alpha;
            }
        }
        ((TotalFlowResults) result).map__server__alphas.put(server, alphas_server);
        ((TotalFlowResults) result).map__server__D_server.put(server, delay_bounds_server);
        ((TotalFlowResults) result).map__server__B_server.put(server, backlog_bounds_server);

        return new Pair<Num>(delay_bound_s__min, backlog_bound_s__min);
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