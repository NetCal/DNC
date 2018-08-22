/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
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

package de.uni_kl.cs.discodnc.bounds;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.feedforward.AnalysisConfig;
import de.uni_kl.cs.discodnc.feedforward.arrivalbounds.SinkTree_AffineCurves;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.ArrayList;

public class Backlog {
	private Backlog() {
	}

	public static Num derive(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
		if (arrival_curve.equals(Curve.getFactory().createZeroArrivals())) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
		}
		if (service_curve.isDelayedInfiniteBurst()) {
			return arrival_curve.f(service_curve.getLatency());
		}
		if (service_curve.equals(Curve.getFactory().createZeroService()) // We know from above that the
				// arrivals are not zero.
				|| arrival_curve.getUltAffineRate().gt(service_curve.getUltAffineRate())) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
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

		Num result = arrival_curve.fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero());

		ArrayList<Num> xcoords = Curve.computeInflectionPointsX(arrival_curve, service_curve);
		for (int i = 0; i < xcoords.size(); i++) {
			Num ip_x = xcoords.get(i);

			Num backlog = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(arrival_curve.f(ip_x), service_curve.f(ip_x));
			result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, backlog);
		}
		return result;
	}

	public static double derivePmooSinkTreeTbRl(Network tree, Server root,
                                                AnalysisConfig.ArrivalBoundMethod sink_tree_ab) throws Exception {
		SinkTree_AffineCurves sink_tree_bound = new SinkTree_AffineCurves(tree);
		ArrivalCurve arrivals_at_root = tree.getSourceFlowArrivalCurve(root);

		for (Link link : tree.getInLinks(root)) {
			switch (sink_tree_ab) {
			case SINKTREE_AFFINE_CONV:
				// will only be one curve
				arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
						.computeArrivalBoundDeConvolution(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next());
				break;

			case SINKTREE_AFFINE_CONV_DECONV:
				arrivals_at_root = Curve.add(arrivals_at_root,
						sink_tree_bound.computeArrivalBoundDeConvolutionTBRL(link, tree.getFlows(link), Flow.NULL_FLOW)
								.iterator().next()); // will only be one curve
				break;

			case SINKTREE_AFFINE_HOMO:
				// will only be one curve
				arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
						.computeArrivalBoundHomogeneous(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next());
				break;

			case SINKTREE_AFFINE:
			default:
				// will only be one curve
				arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
						.computeArrivalBound(link, tree.getFlows(link), Flow.NULL_FLOW).iterator().next());
				break;
			}
		}

		return Curve.getMaxVerticalDeviation(arrivals_at_root, root.getServiceCurve()).doubleValue();
	}
}
