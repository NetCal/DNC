/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

package de.uni_kl.cs.disco.nc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactoryDispatch;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtilsDispatch;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.minplus.Deconvolution;
import de.uni_kl.cs.disco.misc.SetUtils;
import de.uni_kl.cs.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.arrivalbounds.PbooArrivalBound_Concatenation;
import de.uni_kl.cs.disco.nc.arrivalbounds.PbooArrivalBound_PerHop;
import de.uni_kl.cs.disco.nc.arrivalbounds.PmooArrivalBound;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Server;

public class ArrivalBound {
    protected Network network;
    protected AnalysisConfig configuration;

    protected ArrivalBound() {
    }

    public ArrivalBound(Network network, AnalysisConfig configuration) {
        this.network = network;
        this.configuration = configuration;
    }

    /**
     * Calculates the alternative arrival curves for a the flows in flows_to_bound that arrive at the given server
     * while considering the lower priority of the flow_of_interest
     *
     * @param network       The analyzed network.
     * @param configuration The analysis configuration.
     * @param server        The server seeing the arrival bound.
     * @return The arrival bound.
     * @throws Exception Potential exception raised in the called function computeArrivalBounds.
     */
    public static Set<ArrivalCurve> computeArrivalBounds(Network network, AnalysisConfig configuration, Server server) throws Exception {
        return computeArrivalBounds(network, configuration, server, network.getFlows(server), Flow.NULL_FLOW);
    }

    /**
     * The flow_of_interest low priority supersedes the wish to bound all flows in flows_to_bound,
     * i.e., if flow_of_interest will be removed from flows_to_bound before bounding the arrivals
     * such that the result will always hold (only) for {flows_to_bound} \ {flow_of_interest}.
     * <p>
     * To bound all flows in flows_to_bound, please call, e.g.,
     * computeArrivalBounds( network, flows_to_bound, Flow.NULL_FLOW )
     *
     * @param network          The analyzed network.
     * @param configuration    The analysis configuration.
     * @param server           The server seeing the arrival bound.
     * @param flows_to_bound   The flows to be bounded.
     * @param flow_of_interest The flow of interest to get a lower priority.
     * @return The arrival bound.
     * @throws Exception Potential exception raised in the called function computeArrivalBounds.
     */
    public static Set<ArrivalCurve> computeArrivalBounds(Network network, AnalysisConfig configuration, Server server, Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
        ArrivalBound arrival_bounding = new ArrivalBound(network, configuration);
        return arrival_bounding.computeArrivalBounds(server, flows_to_bound, flow_of_interest);
    }

    public static Set<ArrivalCurve> computeArrivalBounds(Network network, AnalysisConfig configuration, Link link, Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
        ArrivalBound arrival_bounding = new ArrivalBound(network, configuration);
        return arrival_bounding.computeArrivalBounds(link, flows_to_bound, flow_of_interest);
    }

    /**
     * The flow_of_interest low priority supersedes the wish to bound all flows in flows_to_bound,
     * i.e., if flow_of_interest will be removed from flows_to_bound before bounding the arrivals
     * such that the result will always hold (only) for {flows_to_bound} \ {flow_of_interest}.
     * <p>
     * To bound all flows in flows_to_bound, please call, e.g.,
     * computeArrivalBounds( network, flows_to_bound, Flow.NULL_FLOW )
     *
     * @param server The server seeing the arrival bound.
     * @return The arrival bound.
     * @throws Exception Potential exception raised in the called function computeArrivalBounds.
     */
    public Set<ArrivalCurve> computeArrivalBound(Server server) throws Exception {
        return computeArrivalBounds(server, network.getFlows(server), Flow.NULL_FLOW);
    }

    /**
     * The flow_of_interest low priority supersedes the wish to bound all flows in flows_to_bound,
     * i.e., if flow_of_interest will be removed from flows_to_bound before bounding the arrivals
     * such that the result will always hold (only) for {flows_to_bound} \ {flow_of_interest}.
     * <p>
     * To bound all flows in flows_to_bound, please call, e.g.,
     * computeArrivalBounds( network, flows_to_bound, Flow.NULL_FLOW )
     *
     * @param server           The server seeing the arrival bound.
     * @param flows_to_bound   The flows to be bounded.
     * @param flow_of_interest The flow of interest to get a lower priority.
     * @return The arrival bound.
     * @throws Exception Potential exception raised in the called function computeArrivalBounds.
     */
    public Set<ArrivalCurve> computeArrivalBounds(Server server, Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
        flows_to_bound.remove(flow_of_interest);
        Set<ArrivalCurve> arrival_bounds = new HashSet<ArrivalCurve>(Collections.singleton(CurvePwAffineFactoryDispatch.createZeroArrivals()));
        if (flows_to_bound.isEmpty()) {
            return arrival_bounds;
        }

        Set<Flow> f_server = network.getFlows(server);
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_server, flows_to_bound);
        if (f_xfcaller_server.isEmpty()) {
            return arrival_bounds;
        }

        // Get cross-traffic originating in server
        Set<Flow> f_xfcaller_sourceflows_server = SetUtils.getIntersection(f_xfcaller_server, network.getSourceFlows(server));
        f_xfcaller_sourceflows_server.remove(flow_of_interest);
        ArrivalCurve alpha_xfcaller_sourceflows_server = network.getSourceFlowArrivalCurve(server, f_xfcaller_sourceflows_server); // Will at least be a zeroArrivalCurve
        arrival_bounds = new HashSet<ArrivalCurve>(Collections.singleton(alpha_xfcaller_sourceflows_server));

        if (f_xfcaller_sourceflows_server.containsAll(f_xfcaller_server)) {
            return arrival_bounds;
        }

        // Get cross-traffic from each predecessor. Call per link in order to get splitting points.
        Set<ArrivalCurve> arrival_bounds_link;
        Set<ArrivalCurve> arrival_bounds_link_permutations = new HashSet<ArrivalCurve>();

        Iterator<Link> in_link_iter = network.getInLinks(server).iterator();
        while (in_link_iter.hasNext()) {

            Link in_l = in_link_iter.next();
            Set<Flow> f_xfcaller_in_l = SetUtils.getIntersection(network.getFlows(in_l), f_xfcaller_server);
            f_xfcaller_in_l.remove(flow_of_interest);

            if (f_xfcaller_in_l.isEmpty()) { // Do not check links without flows of interest
                continue;
            }

            arrival_bounds_link = computeArrivalBounds(in_l, f_xfcaller_in_l, flow_of_interest);

            // Add the new bounds to the others:
            // * Consider all the permutations of different bounds per in link.
            // * Care about the configuration.convolveAlternativeArrivalBounds()-flag later.
            for (ArrivalCurve arrival_bound_link : arrival_bounds_link) {
                CurvePwAffineUtilsDispatch.beautify(arrival_bound_link);

                for (ArrivalCurve arrival_bound_exiting : arrival_bounds) {
                    arrival_bounds_link_permutations.add(CurvePwAffineUtilsDispatch.add(arrival_bound_link, arrival_bound_exiting));
                }
            }

            arrival_bounds.clear();
            arrival_bounds.addAll(arrival_bounds_link_permutations);
            arrival_bounds_link_permutations.clear();
        }

        return arrival_bounds;
    }

    public Set<ArrivalCurve> computeArrivalBounds(Link link, Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
        flows_to_bound.remove(flow_of_interest);
        if (flows_to_bound.isEmpty()) {
            return new HashSet<ArrivalCurve>(Collections.singleton(CurvePwAffineFactoryDispatch.createZeroArrivals()));
        }

        Set<ArrivalCurve> arrival_bounds_xfcaller = new HashSet<ArrivalCurve>();

        for (ArrivalBoundMethod arrival_bound_method : configuration.arrivalBoundMethods()) {
            Set<ArrivalCurve> arrival_bounds_tmp = new HashSet<ArrivalCurve>();

            switch (arrival_bound_method) {
                case PBOO_PER_HOP:
                    PbooArrivalBound_PerHop pboo_per_hop = new PbooArrivalBound_PerHop(network, configuration);
                    arrival_bounds_tmp = pboo_per_hop.computeArrivalBound(link, flows_to_bound, flow_of_interest);
                    break;

                case PBOO_CONCATENATION:
                    PbooArrivalBound_Concatenation pboo_concatenation = new PbooArrivalBound_Concatenation(network, configuration);
                    arrival_bounds_tmp = pboo_concatenation.computeArrivalBound(link, flows_to_bound, flow_of_interest);
                    break;

                case PMOO:
                    PmooArrivalBound pmoo_arrival_bound = new PmooArrivalBound(network, configuration);
                    arrival_bounds_tmp = pmoo_arrival_bound.computeArrivalBound(link, flows_to_bound, flow_of_interest);
                    break;

                case PER_FLOW_SFA:
                    for (Flow flow : flows_to_bound) {
                        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network);
                        sfa.performAnalysis(flow, flow.getSubPath(flow.getSource(), link.getSource()));

                        arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
                                singleFlowABs(flow.getArrivalCurve(), sfa.getLeftOverServiceCurves()));
                    }
                    break;

                // Functional tests missing
                case PER_FLOW_PMOO:
                    for (Flow flow : flows_to_bound) {
                        PmooAnalysis pmoo = new PmooAnalysis(network);
                        pmoo.performAnalysis(flow, flow.getSubPath(flow.getSource(), link.getSource()));

                        arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
                                singleFlowABs(flow.getArrivalCurve(), pmoo.getLeftOverServiceCurves()));
                    }
                    break;

                default:
                    System.out.println("Executing default arrival bounding: PBOO_CONCATENATION");
                    PbooArrivalBound_Concatenation default_ab = new PbooArrivalBound_Concatenation(network, configuration);
                    arrival_bounds_tmp = default_ab.computeArrivalBound(link, flows_to_bound, flow_of_interest);
                    break;
            }

            addArrivalBounds(arrival_bounds_tmp, arrival_bounds_xfcaller);
        }

        return arrival_bounds_xfcaller;
    }

    private Set<ArrivalCurve> singleFlowABs(ArrivalCurve alpha, Set<ServiceCurve> betas_lo) {
        Set<ArrivalCurve> arrival_bounds_f = new HashSet<ArrivalCurve>();

        // All permutations of single flow results
        for (ServiceCurve beta_lo : betas_lo) {
            arrival_bounds_f.add(Deconvolution.deconvolve(alpha, beta_lo, configuration.tbrlDeconvolution()));
        }

        return arrival_bounds_f;
    }

    private Set<ArrivalCurve> getPermutations(Set<ArrivalCurve> arrival_curves_1, Set<ArrivalCurve> arrival_curves_2) {
        if (arrival_curves_1.isEmpty()) {
            return new HashSet<ArrivalCurve>(arrival_curves_2);
        }
        if (arrival_curves_2.isEmpty()) {
            return new HashSet<ArrivalCurve>(arrival_curves_1);
        }

        Set<ArrivalCurve> arrival_bounds_merged = new HashSet<ArrivalCurve>();

        for (ArrivalCurve alpha_1 : arrival_curves_1) {
            for (ArrivalCurve alpha_2 : arrival_curves_2) {
                arrival_bounds_merged.add(CurvePwAffineUtilsDispatch.add(alpha_1, alpha_2));
            }
        }

        return arrival_bounds_merged;
    }

    private void addArrivalBounds(Set<ArrivalCurve> arrival_bounds_to_merge, Set<ArrivalCurve> arrival_bounds) {
        if (configuration.arrivalBoundMethods().size() == 1) { // In this case there can only be one arrival bound
            arrival_bounds.addAll(arrival_bounds_to_merge);
        } else {
            for (ArrivalCurve arrival_bound : arrival_bounds_to_merge) {
                addArrivalBounds(arrival_bound, arrival_bounds);
            }
        }
    }

    private void addArrivalBounds(ArrivalCurve arrival_bound_to_merge, Set<ArrivalCurve> arrival_bounds) {
        if (configuration.arrivalBoundMethods().size() == 1) { // In this case there can only be one arrival bound
            arrival_bounds.add(arrival_bound_to_merge);
        } else {
            if (!configuration.removeDuplicateArrivalBounds()
                    || (configuration.removeDuplicateArrivalBounds() && !isDuplicate(arrival_bound_to_merge, arrival_bounds))) {
                arrival_bounds.add(arrival_bound_to_merge);
            }
        }
    }

    private boolean isDuplicate(ArrivalCurve arrival_bound_to_check, Set<ArrivalCurve> arrival_bounds) {
        for (ArrivalCurve arrival_bound_existing : arrival_bounds) {
            if (arrival_bound_to_check.equals(arrival_bound_existing)) {
                return true;
            }
        }
        return false;
    }
}