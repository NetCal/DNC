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

package org.networkcalculus.dnc.sinktree;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.sinktree.arrivalbounds.SinkTree_AffineCurves;

public final class Backlog_SinkTree {
	public static double derivePmooSinkTreeAffine(ServerGraph tree, Server server,
            AnalysisConfig.ArrivalBoundMethod sink_tree_ab) throws Exception {
		SinkTree_AffineCurves sink_tree_bound = new SinkTree_AffineCurves(tree);
		ArrivalCurve arrivals_at_root = tree.getSourceFlowArrivalCurve(server);

		for (Turn turn : tree.getInTurns(server)) {
			switch (sink_tree_ab) {
				case SINKTREE_AFFINE_HOMO:
					arrivals_at_root = Curve.getUtils().add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundHomogeneous(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
	
				case SINKTREE_AFFINE_DIRECT:
					arrivals_at_root = Curve.getUtils().add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundDirect(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
					
				case SINKTREE_AFFINE_MINPLUS:
				default:
					arrivals_at_root = Curve.getUtils().add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundMinPlusBackend(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
			}
		}
		return Curve.getUtils().getMaxVerticalDeviation(arrivals_at_root, server.getServiceCurve()).doubleValue();
	}
}
