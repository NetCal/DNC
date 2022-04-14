/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.feedforward.arrivalbounds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.AlgDncBackend_DNC_Affine;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.AbstractArrivalBound;
import org.networkcalculus.dnc.feedforward.ArrivalBound;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.dnc.utils.SetUtils;
import org.networkcalculus.num.Num;

public class AggregatePboo_Concatenation extends AbstractArrivalBound implements ArrivalBound {
	private static AggregatePboo_Concatenation instance = new AggregatePboo_Concatenation();

	private AggregatePboo_Concatenation() {
	}

	public AggregatePboo_Concatenation(ServerGraph server_graph, AnalysisConfig configuration) {
		this.server_graph = server_graph;
		this.configuration = configuration;
	}

	public static AggregatePboo_Concatenation getInstance() {
		return instance;
	}

	public Set<ArrivalCurve> computeArrivalBound(Turn turn, Flow flow_of_interest) throws Exception {
		return computeArrivalBound(turn, server_graph.getFlows(turn), flow_of_interest);
	}

	public Set<ArrivalCurve> computeArrivalBound(Turn turn, Set<Flow> f_xfcaller, Flow flow_of_interest)
			throws Exception {
		Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>(
				Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
		if (f_xfcaller == null || f_xfcaller.isEmpty()) {
			return alphas_xfcaller;
		}

		// Get the servers on common sub-path of f_xfcaller flows crossing turn
		// loi == location of interference
		Server loi = turn.getDest();
		Set<Flow> f_loi = server_graph.getFlows(loi);
		Set<Flow> f_xfcaller_loi = SetUtils.getIntersection(f_loi, f_xfcaller);
		f_xfcaller_loi.remove(flow_of_interest);
		if (f_xfcaller_loi.size() == 0) {
			return alphas_xfcaller;
		}

		// The shortcut found in PmooArrivalBound for the a common_subpath of length 1
		// will not be implemented here.
		// There's not a big potential to increase performance as the PBOO arrival bound
		// implicitly handles this situation by only iterating over one server in the
		// for loop.
		Server common_subpath_src = server_graph.findSplittingServer(loi, f_xfcaller_loi);
		Server common_subpath_dest = turn.getSource();
		Flow f_representative = f_xfcaller_loi.iterator().next();
		Path common_subpath = f_representative.getSubPath(common_subpath_src, common_subpath_dest);

		// Calculate the left-over service curves on this sub-path by convolution of the
		// individual left over service curves
		Set<ServiceCurve> betas_lo_subpath = new HashSet<ServiceCurve>();
		Set<ServiceCurve> betas_lo_s;
		Turn turn_from_prev_s;
		Path foi_path = flow_of_interest.getPath();
		for (Server server : common_subpath.getServers()) {
			try {
				turn_from_prev_s = server_graph.findTurn(foi_path.getPrecedingServer(server), server);
			} catch (Exception e) { // Reached the path's first server
				turn_from_prev_s = null; // reset to null
			}

			Set<Flow> f_xxfcaller_server = server_graph.getFlows(server);
			f_xxfcaller_server.removeAll(f_xfcaller);
			f_xxfcaller_server.remove(flow_of_interest);

			Set<Flow> f_xxfcaller_server_path = SetUtils.getIntersection(f_xxfcaller_server,
					server_graph.getFlows(turn_from_prev_s));

			// Convert f_xfoi_server to f_xfoi_server_offpath
			f_xxfcaller_server.removeAll(f_xxfcaller_server_path);

			// If we are off the path of interest, flow_of_interest is Flow.NULL_FLOW
			// already.
			Set<ArrivalCurve> alpha_xxfcaller_path = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration,
					server, f_xxfcaller_server_path, flow_of_interest);
			Set<ArrivalCurve> alpha_xxfcaller_offpath = ArrivalBoundDispatch.computeArrivalBounds(server_graph,
					configuration, server, f_xxfcaller_server, Flow.NULL_FLOW);

			Set<ArrivalCurve> alphas_xxfcaller_s = new HashSet<ArrivalCurve>();
			for (ArrivalCurve arrival_curve_path : alpha_xxfcaller_path) {
				for (ArrivalCurve arrival_curve_offpath : alpha_xxfcaller_offpath) {
					if(arrival_curve_offpath.isDelayedInfiniteBurst() || arrival_curve_path.isDelayedInfiniteBurst())
					{
						continue;
					}
					alphas_xxfcaller_s.add(Curve.getUtils().add(arrival_curve_path, arrival_curve_offpath));
				}
			}

			// Calculate the left-over service curve for this single server
			betas_lo_s = Calculator.getInstance().getDncBackend().getBoundingCurves().leftOverService(configuration, server, alphas_xxfcaller_s);

			// Check if there's any service left on this path. If not, the set only contains
			// a null-service curve.
			if (betas_lo_s.size() == 1
					&& betas_lo_s.iterator().next().equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())) {
				System.out.println("No service left over during PBOO arrival bounding!");
				alphas_xfcaller.clear();
				alphas_xfcaller.add(Curve.getFactory()
						.createArrivalCurve((Curve)Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get()));
				return alphas_xfcaller;
			}

			// Combine into the sub-path's left-over service curve
			betas_lo_subpath = Calculator.getInstance().getMinPlus().convolve(betas_lo_subpath, betas_lo_s);
		}

		// Next we need to know the arrival bound of f_xfcaller at the server
		// 'common_subpath_src', i.e., at the above sub-path's source in order to
		// deconvolve it with beta_lo_s to get the arrival bound of the sub-path.
		// Note that flows f_xfcaller that originate in 'common_subpath_src' are covered
		// by this call of computeArrivalBound.
		Set<ArrivalCurve> alpha_xfcaller_src = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration,
				common_subpath_src, f_xfcaller, flow_of_interest);
		alphas_xfcaller = Calculator.getInstance().getDncBackend().getBoundingCurves().output(configuration, alpha_xfcaller_src, common_subpath, betas_lo_subpath);

		// TODO This implementation only works for token-bucket arrivals. 
		if (configuration.serverBacklogArrivalBound()) {
			if(Calculator.getInstance().getDncBackend() == AlgDncBackend_DNC_Affine.DISCO_AFFINE) {
				Server last_hop_xtx = turn.getSource();
				// For the DNC, it is easiest to use TFA to compute the server's backlog bound. 
				TotalFlowAnalysis tfa = new TotalFlowAnalysis(server_graph, configuration);
				tfa.deriveBoundsAtServer(last_hop_xtx);
	
				Set<Num> tfa_backlog_bounds = tfa.getServerBacklogBoundMap().get(last_hop_xtx);
				Num tfa_backlog_bound_min = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();
	
				for (Num tfa_backlog_bound : tfa_backlog_bounds) {
					if (tfa_backlog_bound.leq(tfa_backlog_bound_min)) {
						tfa_backlog_bound_min = tfa_backlog_bound;
					}
				}
	
				// Reduce the burst: Here's the limitation.
				// It disregards the potential shift in inflection points not present in this burst cap variant.
				for (ArrivalCurve alpha_xfcaller : alphas_xfcaller) {
					if (alpha_xfcaller.getBurst().gt(tfa_backlog_bound_min)) {
						// If the burst is >0 then there are at least two segments and the second holds the burst as its y-axis value.
						alpha_xfcaller.getSegment(1).setY(tfa_backlog_bound_min);
					}
				}
			} else {
				System.out.println("INFO: Capping output burstiness was not executed as it currently only works for DISCO_AFFINE curves.");
			}
		}
		return alphas_xfcaller;
	}
}
