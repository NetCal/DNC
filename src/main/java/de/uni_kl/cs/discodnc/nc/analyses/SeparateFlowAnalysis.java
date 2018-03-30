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

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.misc.SetUtils;
import de.uni_kl.cs.discodnc.nc.AbstractAnalysis;
import de.uni_kl.cs.discodnc.nc.Analysis;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SeparateFlowAnalysis extends AbstractAnalysis implements Analysis {
    @SuppressWarnings("unused")
    private SeparateFlowAnalysis() {
    }

    public SeparateFlowAnalysis(Network network) {
        super.network = network;
        super.configuration = new AnalysisConfig();
        super.result = new SeparateFlowResults();
    }

    public SeparateFlowAnalysis(Network network, AnalysisConfig configuration) {
        super.network = network;
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
        ((SeparateFlowResults) result).betas_e2e = getServiceCurves(flow_of_interest, path,
                Collections.singleton(flow_of_interest));

        Num delay_bound__beta_e2e;
        Num backlog_bound__beta_e2e;

        ((SeparateFlowResults) result).setDelayBound(Num.getFactory().createPositiveInfinity());
        ((SeparateFlowResults) result).setBacklogBound(Num.getFactory().createPositiveInfinity());

        for (ServiceCurve beta_e2e : ((SeparateFlowResults) result).betas_e2e) {
            delay_bound__beta_e2e = Bound.delayFIFO(flow_of_interest.getArrivalCurve(), beta_e2e); // single flow
            // of
            // interest,
            // i.e.,
            // fifo per
            // micro
            // flow
            // holds
            if (delay_bound__beta_e2e.leq(result.getDelayBound())) {
                ((SeparateFlowResults) result).setDelayBound(delay_bound__beta_e2e);
            }

            backlog_bound__beta_e2e = Bound.backlog(flow_of_interest.getArrivalCurve(), beta_e2e);
            if (backlog_bound__beta_e2e.leq(result.getBacklogBound())) {
                ((SeparateFlowResults) result).setBacklogBound(backlog_bound__beta_e2e);
            }
        }
    }

    protected Set<ServiceCurve> getServiceCurves(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve)
            throws Exception {
        Set<ServiceCurve> betas_lofoi_path = new HashSet<ServiceCurve>();
        Set<ServiceCurve> betas_lofoi_s;

        // This version iterates over the servers on the path and computes the the
        // service curve set hop-by-hop
        // You could also call network.getFlowsPerServer( path, {foi} u {flows_to_serve}
        // )
        // and use that result. That would be more abstract and in line with the PMOO
        // analysis's way.

        // Convolve all left over service curves, server by server
        Link link_from_prev_s;
        Path foi_path = flow_of_interest.getPath();
        for (Server server : path.getServers()) {
            try {
                link_from_prev_s = network.findLink(foi_path.getPrecedingServer(server), server);
            } catch (Exception e) { // Reached the path's first server
                link_from_prev_s = null; // reset to null
            }

            betas_lofoi_s = new HashSet<ServiceCurve>();

            Set<Flow> f_xfoi_server = network.getFlows(server);
            f_xfoi_server.removeAll(flows_to_serve);
            f_xfoi_server.remove(flow_of_interest);

            Set<Flow> f_xfoi_server_path = SetUtils.getIntersection(f_xfoi_server, network.getFlows(link_from_prev_s));

            // Convert f_xfoi_server to f_xfoi_server_offpath
            f_xfoi_server.removeAll(f_xfoi_server_path);

            ServiceCurve beta_lofoi = server.getServiceCurve();

            if (f_xfoi_server.isEmpty() && f_xfoi_server_path.isEmpty()) {
                betas_lofoi_s.add(beta_lofoi);
            } else { // network, server, flows_to_bound, flows_lower_priority
                Set<ArrivalCurve> alpha_xfois_path = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
                        server, f_xfoi_server_path, flow_of_interest);
                Set<ArrivalCurve> alpha_xfois_offpath = ArrivalBoundDispatch.computeArrivalBounds(network,
                        configuration, server, f_xfoi_server, Flow.NULL_FLOW);

                Set<ArrivalCurve> alpha_xfois = new HashSet<ArrivalCurve>();
                for (ArrivalCurve arrival_curve_path : alpha_xfois_path) {
                    for (ArrivalCurve arrival_curve_offpath : alpha_xfois_offpath) {
                        alpha_xfois.add(CurvePwAffine.add(arrival_curve_path, arrival_curve_offpath));
                    }
                }

                // Calculate the left-over service curve for the flow of interest
                betas_lofoi_s = Bound.leftOverService(configuration, server, alpha_xfois);

                result.map__server__alphas.put(server, alpha_xfois);
            }
            ((SeparateFlowResults) result).map__server__betas_lo.put(server, betas_lofoi_s);

            betas_lofoi_path = Calculator.getInstance().getMinPlus().convolve_SCs_SCs(betas_lofoi_path, betas_lofoi_s,
                    configuration.tbrlConvolution());
        }
        return betas_lofoi_path;
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