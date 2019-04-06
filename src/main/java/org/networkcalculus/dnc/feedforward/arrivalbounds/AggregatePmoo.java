/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
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

package org.networkcalculus.dnc.feedforward.arrivalbounds;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
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
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.utils.SetUtils;

public class AggregatePmoo extends AbstractArrivalBound implements ArrivalBound {
	private static AggregatePmoo instance = new AggregatePmoo();

	private AggregatePmoo() {
	}

	public AggregatePmoo(ServerGraph server_graph, AnalysisConfig configuration) {
		this.server_graph = server_graph;
		this.configuration = configuration;
	}

	public static AggregatePmoo getInstance() {
		return instance;
	}

	public Set<ArrivalCurve> computeArrivalBound(Turn turn, Flow flow_of_interest) throws Exception {
		return computeArrivalBound(turn, server_graph.getFlows(turn), flow_of_interest);
	}

	/**
	 * Computes the PMOO arrival bound for a set of <code>flows_to_bound</code>. The
	 * difference to the standard output bound method is that this method tries to
	 * compute tighter bounds by concatenating as many servers as possible using the
	 * PMOO approach. It does so by searching from <code>server</code> towards the
	 * sinks of the flows contained in <code>f_xfcaller</code> until it reaches the
	 * server where all these flows first meet each other (the "splitting point").
	 * It then concatenates all servers between the splitting point (inclusive) and
	 * <code>server</code> (exclusive).
	 *
	 * @param turn
	 *            The turn that all flows of interest flow into.
	 * @param f_xfcaller
	 *            The set of flows of interest.
	 * @param flow_of_interest
	 *            The flow of interest to handle with lowest priority.
	 * @return The PMOO arrival bounds.
	 * @throws Exception
	 *             If any of the sanity checks fails.
	 */
	public Set<ArrivalCurve> computeArrivalBound(Turn turn, Set<Flow> f_xfcaller, Flow flow_of_interest)
			throws Exception {

		Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>(Collections.singleton(Curve.getFactory().createZeroArrivals()));
		if (f_xfcaller == null || f_xfcaller.isEmpty()) {
			return alphas_xfcaller;
		}

		// Get the common sub-path of f_xfcaller flows crossing the given turn
		// soi == server of interference
		Server soi = turn.getDest();
		Set<Flow> f_soi = server_graph.getFlows(soi);
		Set<Flow> f_xfcaller_soi = SetUtils.getIntersection(f_soi, f_xfcaller);
		f_xfcaller_soi.remove(flow_of_interest);
		if (f_xfcaller_soi.isEmpty()) {
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
		}

		if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO
				|| (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL
						&& turn.getSource().multiplexing() == Multiplexing.FIFO)) {
			throw new Exception("PMOO arrival bounding is not available for FIFO multiplexing nodes");
		}

		Server common_subpath_src = server_graph.findSplittingServer(soi, f_xfcaller_soi);
		Server common_subpath_dest = turn.getSource();
		Path common_subpath;
		Set<ServiceCurve> betas_loxfcaller_subpath = new HashSet<ServiceCurve>();

		common_subpath = f_xfcaller_soi.iterator().next().getPath().getSubPath(common_subpath_src, common_subpath_dest);

		if (common_subpath.numServers() == 1) {
			common_subpath = new Path(common_subpath_src);

			Set<Flow> f_xxfcaller = server_graph.getFlows(common_subpath_src);
			f_xxfcaller.removeAll(f_xfcaller_soi);
			f_xxfcaller.remove(flow_of_interest);
			Set<ArrivalCurve> alphas_xxfcaller = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration,
					common_subpath_src, f_xxfcaller, flow_of_interest);

			ServiceCurve null_service = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();

			for (ServiceCurve beta_loxfcaller_subpath : Calculator.getInstance().getDncBackend().getBoundingCurves().leftOverServiceARB(common_subpath_src.getServiceCurve(),
					alphas_xxfcaller)) {
				if (!beta_loxfcaller_subpath.equals(null_service)) {
					// Adding to the set, not adding up the curves
					betas_loxfcaller_subpath.add(beta_loxfcaller_subpath);
				}
			}
		} else {
			PmooAnalysis pmoo = new PmooAnalysis(server_graph, configuration);
			betas_loxfcaller_subpath = pmoo.getServiceCurves(flow_of_interest, common_subpath, f_xfcaller_soi);
		}

		// Check if there's any service left on this path. Signaled by at least one
		// service curve in this set
		if (betas_loxfcaller_subpath.isEmpty()) {
			System.out.println("No service left over during PMOO arrival bounding!");
			alphas_xfcaller.clear();
			alphas_xfcaller.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
			return alphas_xfcaller;
		}

		// Get arrival bound at the splitting point:
		// We need to know the arrival bound of f_xfcaller at the server
		// 'common_subpath_src', i.e., at the above sub-path's source
		// in order to deconvolve it with beta_loxfcaller_subpath to get the arrival
		// bound of the sub-path
		// Note that flows f_xfcaller that originate in 'common_subpath_src' are covered
		// by this call of computeArrivalBound
		Set<ArrivalCurve> alpha_xfcaller_src = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration,
				common_subpath_src, f_xfcaller, flow_of_interest);
		alphas_xfcaller = Calculator.getInstance().getDncBackend().getBoundingCurves().output(configuration, alpha_xfcaller_src, common_subpath, betas_loxfcaller_subpath);

		return alphas_xfcaller;
	}
}
