/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.sinktree.arrivalbounds;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.sinktree.arrivalbounds.SinkTree_AffineCurves;
import org.networkcalculus.dnc.utils.SetUtils;

public class SinkTree_AffineCurves {
    private static SinkTree_AffineCurves instance = new SinkTree_AffineCurves();
    
    protected ServerGraph server_graph;
    private PmooSinkTreeAffineABCache ab_cache = new PmooSinkTreeAffineABCache();

    private SinkTree_AffineCurves() {
    }

    public SinkTree_AffineCurves(ServerGraph tree) {
        this.server_graph = tree;
    }

    public static SinkTree_AffineCurves getInstance() {
        return instance;
    }

    public void clearCache() {
        ab_cache = new PmooSinkTreeAffineABCache();
    }

    /**
     * This code path uses the DNC's minpuls backend for computations. 
     * Thus, it operates on entire curves instead of 
     * restricting to the relevant values like computeArrivalBoundDirect does.
     *
     * @param turn             Turn flows arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bound.
     * @throws Exception Unable to get the flow's sub path for service curve convolution.
     */
    public ArrivalCurve computeArrivalBoundMinPlusBackend(Turn turn, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, server_graph.getFlows(turn));
        f_xfcaller_server.remove(flow_of_interest);

        if (f_xfcaller_server.isEmpty()) {
            return Calculator.getInstance().getCurveFactory().createZeroArrivals();
        }

        ArrivalCurve arrival_bound = Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        ArrivalCurve arrival_bound_f = Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        ServiceCurve sc_s_subpath = Curve_ConstantPool.INFINITE_SERVICE_CURVE.get();
        for (Flow f : f_xfcaller_server) {
            arrival_bound_f = ab_cache.getEntry(turn, f);
            if (arrival_bound_f == null) {
                sc_s_subpath = Curve_ConstantPool.INFINITE_SERVICE_CURVE.get();
                for (Server s : f.getSubPath(f.getSource(), turn.getSource()).getServers()) {
                    sc_s_subpath = Calculator.getInstance().getMinPlus().convolve(sc_s_subpath, s.getServiceCurve());
                }
                arrival_bound_f = Calculator.getInstance().getMinPlus().deconvolve(f.getArrivalCurve(), sc_s_subpath);
            }
            ab_cache.addEntry(turn, f, arrival_bound_f);
            arrival_bound = Curve.getUtils().add(arrival_bound, arrival_bound_f);
        }

        return arrival_bound;
    }

    /**
     * This code path computes the parameters relevant to construct the resulting
     * arrival bound directly, i.e., it does not compute and store the entire curves
     * resulting from intermediate computations in order to do so.
     *
     * @param turn             Turn flows arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bound.
     * @throws Exception Unable to get the flow's sub path for service curve convolution.
     */
    public ArrivalCurve computeArrivalBoundDirect(Turn turn, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, server_graph.getFlows(turn));
        f_xfcaller_server.remove(flow_of_interest);
        if (f_xfcaller_server.isEmpty()) {
            return Calculator.getInstance().getCurveFactory().createZeroArrivals();
        }

        double R;
        double B;

        double sum_R = 0.0;
        double sum_B = 0.0;

        ArrivalCurve arrival_bound;
        for (Flow f : f_xfcaller_server) {
            R = 0.0;
            B = 0.0;

            arrival_bound = ab_cache.getEntry(turn, f);
            if (arrival_bound != null) {
                R = arrival_bound.getUltAffineRate().doubleValue();
                B = arrival_bound.getBurst().doubleValue();
            } else {
                R = f.getArrivalCurve().getUltAffineRate().doubleValue();
                B = f.getArrivalCurve().getBurst().doubleValue();

                double sum_T = 0.0;

                for (Server s : f.getSubPath(f.getSource(), turn.getSource()).getServers()) {
                    sum_T = sum_T + s.getServiceCurve().getLatency().doubleValue();
                }
                B += R * sum_T;

                ab_cache.addEntry(turn, f, Curve.getFactory().createTokenBucket(R, B));
            }
            sum_R += R;
            sum_B += B;
        }

        return Curve.getFactory().createTokenBucket(sum_R, sum_B);
    }

    /**
     * In homogeneous server_graphs we can simply multiply the common latency with the
     * length of a flow's path instead of iterating over its servers and sum up for
     * each one's value individually.
     * <p>
     * This code path works similar to computeArrivalBound in the sense that it does
     * not operate on intermediate curves.
     *
     * @param turn             Turn flows arrive on.
     * @param f_xfcaller       Flows to bound.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return Arrival bounds.
     */
    public ArrivalCurve computeArrivalBoundHomogeneous(Turn turn, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {

        // Get flows of interest
        Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_xfcaller, server_graph.getFlows(turn));
        f_xfcaller_server.remove(flow_of_interest);
        if (f_xfcaller_server.size() == 0) {
            return Calculator.getInstance().getCurveFactory().createZeroArrivals();
        }

        double sum_R = 0.0;
        double sum_B = 0.0;
        double sum_T = 0.0;

        // There's no need for a cache in this scenario
        for (Flow f : f_xfcaller_server) {
            sum_R += f.getArrivalCurve().getUltAffineRate().doubleValue();

            sum_T = f.getSubPath(f.getSource(), turn.getSource()).numServers()
                    * f.getSource().getServiceCurve().getLatency().doubleValue();
            sum_B += f.getArrivalCurve().getBurst().doubleValue()
                    + f.getArrivalCurve().getUltAffineRate().doubleValue() * sum_T;
        }

        return Curve.getFactory().createTokenBucket(sum_R, sum_B);
    }
}

// We use a specialized cache here that stores arrival bounds for single flows on specific turns.
// See
//	 	"Boosting Sensor Network Calculus by Thoroughly Bounding Cross-Traffic"
// 		(Steffen Bondorf and Jens B. Schmitt),
// 		in Proc. 34th IEEE International Conference on Computer Communications (INFOCOM 2015).
// for more details.
class PmooSinkTreeAffineABCache {
    Map<Turn, Map<Flow, ArrivalCurve>> map__turn__entries = new HashMap<Turn, Map<Flow, ArrivalCurve>>();

    protected ArrivalCurve getEntry(Turn turn, Flow flow) {
        Map<Flow, ArrivalCurve> entries_turn = map__turn__entries.get(turn);
        if (entries_turn != null) {
            return entries_turn.get(flow);
        } else {
            // Anticipated following addEntry
            entries_turn = new HashMap<Flow, ArrivalCurve>();
            map__turn__entries.put(turn, entries_turn);

            return null;
        }
    }

    protected void addEntry(Turn turn, Flow flow, ArrivalCurve arrival_bound) {
        Map<Flow, ArrivalCurve> entries_turn = map__turn__entries.get(turn);
        if (entries_turn == null) {
            entries_turn = new HashMap<Flow, ArrivalCurve>();
            map__turn__entries.put(turn, entries_turn);
            entries_turn = map__turn__entries.get(turn);
        }

        entries_turn.put(flow, arrival_bound);
    }
}
