/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
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

package de.uni_kl.cs.discodnc.nc.arrivalbounds;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.misc.SetUtils;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PmooArrivalBound_SinkTreeTbRl { //extends AbstractArrivalBound {
    private static PmooArrivalBound_SinkTreeTbRl instance = new PmooArrivalBound_SinkTreeTbRl();
    protected Network network;
    private PmooSinkTreeTbRlABCache ab_cache = new PmooSinkTreeTbRlABCache();

    private PmooArrivalBound_SinkTreeTbRl() {
    }

    public PmooArrivalBound_SinkTreeTbRl(Network tree) {
        this.network = tree;
    }

    public static PmooArrivalBound_SinkTreeTbRl getInstance() {
        return instance;
    }

    public void clearCache() {
        ab_cache = new PmooSinkTreeTbRlABCache();
    }

    /**
     * This code path computes the parameters relevant to construct the resulting
     * arrival bound directly, i.e., it does not compute and store the entire curves
     * resulting from intermediate computations in order to do so.
     *
     * @param link             Link flow arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bound.
     * @throws Exception Unable to get the flow's sub path for service curve convolution.
     */
    public Set<ArrivalCurve> computeArrivalBound(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, network.getFlows(link));
        f_xfcaller_server.remove(flow_of_interest);
        if (f_xfcaller_server.isEmpty()) {
            return result;
        }

        double R;
        double B;

        double sum_R = 0.0;
        double sum_B = 0.0;

        ArrivalCurve arrival_bound;
        for (Flow f : f_xfcaller_server) {
            R = 0.0;
            B = 0.0;

            arrival_bound = ab_cache.getEntry(link, f);
            if (arrival_bound != null) {
                R = arrival_bound.getUltAffineRate().doubleValue();
                B = arrival_bound.getBurst().doubleValue();
            } else {
                R = f.getArrivalCurve().getUltAffineRate().doubleValue();
                B = f.getArrivalCurve().getBurst().doubleValue();

                double sum_T = 0.0;

                for (Server s : f.getSubPath(f.getSource(), link.getSource()).getServers()) {
                    sum_T = sum_T + s.getServiceCurve().getLatency().doubleValue();
                }
                B += R * sum_T;

                ab_cache.addEntry(link, f, CurvePwAffine.getFactory().createTokenBucket(R, B));
            }
            sum_R += R;
            sum_B += B;
        }

        result.add(CurvePwAffine.getFactory().createTokenBucket(sum_R, sum_B));
        return result;
    }

    /**
     * This code path uses the DiscoDNC's convolution operation. Thus, it operates
     * on entire curves instead of restricting to the relevant values like
     * computeArrivalBound does.
     *
     * @param link             Link flow arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bound.
     * @throws Exception Unable to get the flow's sub path for service curve convolution.
     */
    public Set<ArrivalCurve> computeArrivalBoundDeConvolution(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, network.getFlows(link));
        f_xfcaller_server.remove(flow_of_interest);

        if (f_xfcaller_server.isEmpty()) {
            return result;
        }

        ArrivalCurve arrival_bound = CurvePwAffine.getFactory().createZeroArrivals();
        ArrivalCurve arrival_bound_f = CurvePwAffine.getFactory().createZeroArrivals();
        ServiceCurve sc_s_subpath = CurvePwAffine.getFactory().createZeroDelayInfiniteBurst();
        for (Flow f : f_xfcaller_server) {
            arrival_bound_f = ab_cache.getEntry(link, f);
            if (arrival_bound_f == null) {
                sc_s_subpath = CurvePwAffine.getFactory().createZeroDelayInfiniteBurst();
                for (Server s : f.getSubPath(f.getSource(), link.getSource()).getServers()) {
                    sc_s_subpath = MinPlus.convolve(sc_s_subpath, s.getServiceCurve(), false); // false ->
                    // generic
                    // convolution
                }
                arrival_bound_f = MinPlus.deconvolve(f.getArrivalCurve(), sc_s_subpath, false); // false ->
                // generic
                // deconvolution
            }
            ab_cache.addEntry(link, f, arrival_bound_f);
            arrival_bound = CurvePwAffine.add(arrival_bound, arrival_bound_f);
        }

        result.add(arrival_bound);
        return result;
    }

    /**
     * This code path uses the DiscoDNC's convolution operation, like
     * computeArrivalBoundDeConvolutionTBRL does, yet, it uses the optimized
     * convolution and deconvolution operation, respectively, to directly compute
     * the relevant parameters defining the resulting curves.
     *
     * @param link             Link flow arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bound.
     * @throws Exception Unable to get the flow's sub path for service curve convolution.
     */
    public Set<ArrivalCurve> computeArrivalBoundDeConvolutionTBRL(Link link, Set<Flow> f_xfcaller,
                                                                  Flow flow_of_interest) throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, network.getFlows(link));
        f_xfcaller_server.remove(flow_of_interest);

        if (f_xfcaller_server.isEmpty()) {
            return result;
        }

        ArrivalCurve arrival_bound = CurvePwAffine.getFactory().createZeroArrivals();
        ArrivalCurve arrival_bound_f = CurvePwAffine.getFactory().createZeroArrivals();
        ServiceCurve sc_s_subpath = CurvePwAffine.getFactory().createZeroDelayInfiniteBurst();
        for (Flow f : f_xfcaller_server) {
            arrival_bound_f = ab_cache.getEntry(link, f);
            if (arrival_bound_f == null) {
                sc_s_subpath = CurvePwAffine.getFactory().createZeroDelayInfiniteBurst();
                for (Server s : f.getSubPath(f.getSource(), link.getSource()).getServers()) {
                    sc_s_subpath = MinPlus.convolve(sc_s_subpath, s.getServiceCurve(), true); // true -> tb, rl
                    // optimized
                }
                arrival_bound_f = MinPlus.deconvolve(f.getArrivalCurve(), sc_s_subpath, true); // true -> tb, rl
                // optimized
            }
            ab_cache.addEntry(link, f, arrival_bound_f);
            arrival_bound = CurvePwAffine.add(arrival_bound, arrival_bound_f);
        }

        result.add(arrival_bound);
        return result;
    }

    /**
     * In homogeneous networks we can simply multiply the common latency with the
     * length of a flow's path instead of iterating over its servers and sum up for
     * each one's value individually.
     * <p>
     * This code path works similar to computeArrivalBound in the sense that it does
     * not operate on intermediate curves.
     *
     * @param link             Link flow arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bounds.
     */
    public Set<ArrivalCurve> computeArrivalBoundHomogeneous(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, network.getFlows(link));
        f_xfcaller_server.remove(flow_of_interest);
        if (f_xfcaller_server.size() == 0) {
            return result;
        }

        double sum_R = 0.0;
        double sum_B = 0.0;
        double sum_T = 0.0;

        // There's no need for a cache in this scenario
        for (Flow f : f_xfcaller_server) {
            sum_R += f.getArrivalCurve().getUltAffineRate().doubleValue();

            sum_T = f.getSubPath(f.getSource(), link.getSource()).numServers()
                    * f.getSource().getServiceCurve().getLatency().doubleValue();
            sum_B += f.getArrivalCurve().getBurst().doubleValue()
                    + f.getArrivalCurve().getUltAffineRate().doubleValue() * sum_T;
        }

        result.add(CurvePwAffine.getFactory().createTokenBucket(sum_R, sum_B));
        return result;
    }
}

// We use a specialized cache here that stores arrival bounds for single flows
// on specific links.
// See
// "Boosting Sensor Network Calculus by Thoroughly Bounding Cross-Traffic"
// (Steffen Bondorf and Jens B. Schmitt),
// in Proc. 34th IEEE International Conference on Computer Communications
// (INFOCOM 2015).
// for more details.
class PmooSinkTreeTbRlABCache {
    Map<Link, Map<Flow, ArrivalCurve>> map__link__entries = new HashMap<Link, Map<Flow, ArrivalCurve>>();

    protected ArrivalCurve getEntry(Link link, Flow flow) {
        Map<Flow, ArrivalCurve> entries_link = map__link__entries.get(link);
        if (entries_link != null) {
            return entries_link.get(flow);
        } else {
            // Anticipated following addEntry
            entries_link = new HashMap<Flow, ArrivalCurve>();
            map__link__entries.put(link, entries_link);

            return null;
        }
    }

    protected void addEntry(Link link, Flow flow, ArrivalCurve arrival_bound) {
        Map<Flow, ArrivalCurve> entries_link = map__link__entries.get(link);
        if (entries_link == null) {
            entries_link = new HashMap<Flow, ArrivalCurve>();
            map__link__entries.put(link, entries_link);
            entries_link = map__link__entries.get(link);
        }

        entries_link.put(flow, arrival_bound);
    }
}
