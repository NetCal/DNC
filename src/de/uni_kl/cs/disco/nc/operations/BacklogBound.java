/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2017 Steffen Bondorf
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

package de.uni_kl.cs.disco.nc.operations;

import java.util.ArrayList;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactoryDispatch;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtilsDispatch;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.arrivalbounds.PmooArrivalBound_SinkTreeTbRl;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;
import de.uni_kl.cs.disco.numbers.NumUtils;

public class BacklogBound {
	private BacklogBound() {}
	
    public static Num derive(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        if (arrival_curve.equals(CurvePwAffineFactoryDispatch.createZeroArrivals())) {
            return NumFactory.getNumFactory().createZero();
        }
        if (service_curve.getDelayedInfiniteBurst_Property()) {
            return arrival_curve.f(service_curve.getLatency());
        }
        if (service_curve.equals(CurvePwAffineFactoryDispatch.createZeroService()) // We know from above that the arrivals are not zero.
                || arrival_curve.getUltAffineRate().gt(service_curve.getUltAffineRate())) {
            return NumFactory.getNumFactory().createPositiveInfinity();
        }

        // The computeInflectionPoints based method does not work for
        // single rate service curves (without latency)
        // in conjunction with token bucket arrival curves
        // because their common inflection point is in zero,
        // where the arrival curve is 0.0 by definition.
        // This leads to a vertical deviation of 0 the arrival curve's burst
        // (or infinity which is already handled by the first if-statement)

        // Solution:
        // Start with the burst as minimum vertical deviation

        Num result = arrival_curve.fLimitRight(NumFactory.getNumFactory().getZero());

        ArrayList<Num> xcoords = CurvePwAffineUtilsDispatch.computeInflectionPointsX(arrival_curve, service_curve);
        for (int i = 0; i < xcoords.size(); i++) {
            Num ip_x = ((Num) xcoords.get(i));

            Num backlog = NumUtils.getNumUtils().sub(arrival_curve.f(ip_x), service_curve.f(ip_x));
            result = NumUtils.getNumUtils().max(result, backlog);
        }
        return result;
    }

    public static double derivePmooSinkTreeTbRl(Network tree, Server root, AnalysisConfig.ArrivalBoundMethod sink_tree_ab) throws Exception {
    		PmooArrivalBound_SinkTreeTbRl sink_tree_bound = new PmooArrivalBound_SinkTreeTbRl( tree );
    		ArrivalCurve arrivals_at_root = tree.getSourceFlowArrivalCurve(root);
    		
    		for (Link link : tree.getInLinks(root)) {
	    		switch( sink_tree_ab ) {
	    			case PMOO_SINKTREE_TBRL_CONV:
	    				arrivals_at_root = CurvePwAffineUtilsDispatch.add(arrivals_at_root, 
	    						sink_tree_bound.computeArrivalBoundDeConvolution(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next()); // will only be one curve
	    				break;

	    			case PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV:
	    				arrivals_at_root = CurvePwAffineUtilsDispatch.add(arrivals_at_root, 
	    						sink_tree_bound.computeArrivalBoundDeConvolutionTBRL(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next()); // will only be one curve
	    				break;

	    			case PMOO_SINKTREE_TBRL_HOMO:
	    				arrivals_at_root = CurvePwAffineUtilsDispatch.add(arrivals_at_root, 
	    						sink_tree_bound.computeArrivalBoundHomogeneous(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next()); // will only be one curve
	    				break;
	    				
	    			case PMOO_SINKTREE_TBRL:
	    			default:
	    				arrivals_at_root = CurvePwAffineUtilsDispatch.add(arrivals_at_root, 
	    						sink_tree_bound.computeArrivalBound(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next()); // will only be one curve
	    				break;
	    		}
    		}
    		
	    	return CurvePwAffineUtilsDispatch.getMaxVerticalDeviation( arrivals_at_root, root.getServiceCurve() ).doubleValue();
    }
}
