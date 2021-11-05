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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
import org.networkcalculus.dnc.utils.SetUtils;
import org.networkcalculus.num.Num;

public class SeparateFlowAnalysis extends AbstractTandemAnalysis {
    @SuppressWarnings("unused")
    private SeparateFlowAnalysis() {
    }

    public SeparateFlowAnalysis(ServerGraph server_graph) {
        super.server_graph = server_graph;
        super.configuration = new AnalysisConfig();
        super.result = new SeparateFlowResults();
    }

    public SeparateFlowAnalysis(ServerGraph server_graph, AnalysisConfig configuration) {
        super.server_graph = server_graph;
        super.configuration = configuration;
        super.result = new SeparateFlowResults();
    }

    /**
     * Performs a separated flow analysis for the <code>flow_of_interest</code>.
     * <p>
     * This analysis first blends out the flow of interest and then computes for
     * each server along this flow's path the left-over service curve that results
     * if all remaining flows crossing this server receive their maximum amount of
     * service. Then all left-over service curves are concatenated to receive the
     * end-to-end service curve from the perspective of the flow of interest.
     */
    public void performAnalysis(Flow flow_of_interest) throws Exception {
        performAnalysis(flow_of_interest, flow_of_interest.getPath());
    }

    public void performAnalysis(Flow flow_of_interest, Server server) throws Exception {
        Path path_foi = flow_of_interest.getPath();
        if (!path_foi.getServers().contains(server)) {
            throw new Exception("Given server is not on the flow of interest's path.");
        }

        performAnalysis(flow_of_interest, path_foi.getSubPath(server, server));
    }

    public void performAnalysis(Flow flow_of_interest, Path path) throws Exception {
        result = tandemAnalysis(server_graph, flow_of_interest, path, Collections.singleton(flow_of_interest), configuration);

        Num delay_bound__beta_e2e;
        Num backlog_bound__beta_e2e;

        ((SeparateFlowResults) result).setDelayBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
        ((SeparateFlowResults) result).setBacklogBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());

        for (ServiceCurve beta_e2e : ((SeparateFlowResults) result).betas_e2e) {
            // single flow of interest, i.e., FIFO per micro flow holds.
            delay_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(flow_of_interest.getArrivalCurve(), beta_e2e); 
            if (delay_bound__beta_e2e.leq(result.getDelayBound())) {
                ((SeparateFlowResults) result).setDelayBound(delay_bound__beta_e2e);
            }

            backlog_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().backlog(flow_of_interest.getArrivalCurve(), beta_e2e);
            if (backlog_bound__beta_e2e.leq(result.getBacklogBound())) {
                ((SeparateFlowResults) result).setBacklogBound(backlog_bound__beta_e2e);
            }
        }
    }
    
    @Deprecated
    protected Set<ServiceCurve> getServiceCurves(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve) throws Exception {
        return tandemAnalysis(server_graph,flow_of_interest, path, flows_to_serve, configuration).betas_e2e;
    }

    public static SeparateFlowResults tandemAnalysis(ServerGraph server_graph, Flow flow_of_interest, Path path, Set<Flow> flows_to_serve, AnalysisConfig configuration)
            throws Exception {
        SeparateFlowResults result = new SeparateFlowResults();
        Set<ServiceCurve> betas_lo_path = new HashSet<ServiceCurve>();
        Set<ServiceCurve> betas_lo_server;

        // This version iterates over the servers on the path and
        // computes the service curve set hop-by-hop.
        // You could also call server_graph.getFlowsPerServer( path, {foi} \cup {flows_to_serve} )
        // and use that result. That would be more abstract and in line with the PMOO
        // analysis's way.

        // Convolve all left over service curves, server by server

        Turn turn_from_prev_s;
        Set<Flow> f_xxfcaller_server_onpath;
        Set<Flow> f_xxfcaller_server_src;

        for (Server server : path.getServers()) {
            // Find the set of flows that interfere, either already on or coming off the common_subpath.
            Set<Flow> f_xxfcaller_server = server_graph.getFlows(server);
            f_xxfcaller_server.removeAll(flows_to_serve);   // We compute their beta l.o.
            f_xxfcaller_server.remove(flow_of_interest);    // If present, it has lowest priority.

            betas_lo_server = new HashSet<ServiceCurve>();

            if(f_xxfcaller_server.isEmpty()) {
                betas_lo_server.add(server.getServiceCurve());
                result.map__server__alphas.put(server, Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
                ((SeparateFlowResults) result).map__server__betas_lo.put(server, betas_lo_server);
                betas_lo_path = Calculator.getInstance().getMinPlus().convolve(betas_lo_path, betas_lo_server);
                continue;
            }

            // The interfering flows originating at the current server.
            f_xxfcaller_server_src = server_graph.getSourceFlows(server);
            f_xxfcaller_server_src.remove(flow_of_interest);
            f_xxfcaller_server_src.removeAll(flows_to_serve);

            // Remove f_xxfcaller_server_src as their aggregate arrival curve can be easily derived
            f_xxfcaller_server.removeAll(f_xxfcaller_server_src);


            // Attention!
            // We cannot use a Set; we actually need a multiset in order to add equal sets of arrival curves.
            // For instance, this is tested by TR_7S_1SC_3F_1AC_3P_Test's f1 with PMOO arrival bounding
            // where cross-flows f0 and f2 have a single, equal arrival curves at s5.
            List<Set<ArrivalCurve>> ac_sets_to_combine = new LinkedList<Set<ArrivalCurve>>();

            if(!f_xxfcaller_server_src.isEmpty()) {
                ac_sets_to_combine.add(Collections.singleton(server_graph.getSourceFlowArrivalCurve(server, f_xxfcaller_server_src)));
            }

            if( configuration.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO ) {
                ac_sets_to_combine.add(
                        ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration ,server, f_xxfcaller_server, Flow.NULL_FLOW));

            } else {

                f_xxfcaller_server_onpath = new HashSet<Flow>();

                // We might still be on the path of the flow of interest.
                if(flow_of_interest.getId() != -1) {
                    // If so, we need to consider this when we further backtrack.
                    // Continued backtracking on the foipath requires to hand over the foi.
                    // Backtracking offpath requires to hand over a NULL flow that has ID -1.

                    Path foi_path = flow_of_interest.getPath();
                    if( foi_path.getServers().contains(server) && !foi_path.isSource(server) ) {
                        turn_from_prev_s = server_graph.findTurn(foi_path.getPrecedingServer(server), server);
                        f_xxfcaller_server_onpath = SetUtils.getIntersection(f_xxfcaller_server, server_graph.getFlows(turn_from_prev_s));
                    }
                }

                if(!f_xxfcaller_server_onpath.isEmpty() ) {
                    ac_sets_to_combine.add(
                            ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration, server, f_xxfcaller_server_onpath, flow_of_interest));
                }

                // Convert f_xxfcaller_server to "f_xxfcaller_server_offpath"
                f_xxfcaller_server.removeAll(f_xxfcaller_server_onpath);

                // If, during this method's use in arrival bounding, we already left the foi's path,
                // flow_of_interest was set to Flow.NULL_FLOW before. If not, do it again now, that won't harm.
                // in case of FIFO we need to take into the account the foi at each server
                if(!f_xxfcaller_server.isEmpty() ) {
                    ac_sets_to_combine.add(
                            ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration, server, f_xxfcaller_server, Flow.NULL_FLOW));
                }
            }

            if( ac_sets_to_combine.isEmpty() ) {
                betas_lo_server.add(server.getServiceCurve());
                result.map__server__alphas.put(server, Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
            } else {
                Iterator<Set<ArrivalCurve>> ac_set_iterator = ac_sets_to_combine.iterator();
                Set<ArrivalCurve> alpha_xfois = new HashSet<ArrivalCurve>(ac_set_iterator.next());

                Set<ArrivalCurve> ac_combinations_tmp = new HashSet<ArrivalCurve>();
                while(ac_set_iterator.hasNext()) {
                    for(ArrivalCurve ac_new : ac_set_iterator.next()) {
                        for(ArrivalCurve ac_existing : alpha_xfois) {
                            ac_combinations_tmp.add(Curve.getUtils().add(ac_new,ac_existing));
                        }
                    }
                    alpha_xfois.clear();
                    alpha_xfois.addAll(ac_combinations_tmp);
                    ac_combinations_tmp.clear();
                }

                // Calculate the left-over service curve for the flow of interest
                betas_lo_server = Calculator.getInstance().getDncBackend().getBoundingCurves().leftOverService(configuration, server, alpha_xfois);
                result.map__server__alphas.put(server, alpha_xfois);
            }
            ((SeparateFlowResults) result).map__server__betas_lo.put(server, betas_lo_server);

            betas_lo_path = Calculator.getInstance().getMinPlus().convolve(betas_lo_path, betas_lo_server);
        }
        result.betas_e2e = betas_lo_path;

        return result;
    }



    public Set<ServiceCurve> getLeftOverServiceCurves() {
        return ((SeparateFlowResults) result).betas_e2e;
    }

    public Map<Server, Set<ServiceCurve>> getServerLeftOverBetasMap() {
        return ((SeparateFlowResults) result).map__server__betas_lo;
    }

    public String getServerLeftOverBetasMapString() {
        return ((SeparateFlowResults) result).getServerLeftOverBetasMapString();
    }
}