/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2015 - 2018 Steffen Bondorf
 * Copyright (C) 2018+ The DiscoDNC contributors
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

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.nc.AbstractArrivalBound;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.ArrivalBound;
import de.uni_kl.cs.discodnc.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.discodnc.nc.analyses.TandemMatchingAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.utils.SetUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TandemMatchingArrivalBound extends AbstractArrivalBound implements ArrivalBound {
	private static TandemMatchingArrivalBound instance = new TandemMatchingArrivalBound();

	private TandemMatchingArrivalBound() {
	}

	public TandemMatchingArrivalBound( Network network, AnalysisConfig configuration ) {
		this.network = network;
		this.configuration = configuration;
	}

	public static TandemMatchingArrivalBound getInstance() {
		return instance;
	}
	
	public Set<ArrivalCurve> computeArrivalBound( Link link, Flow flow_of_interest ) throws Exception {
		return computeArrivalBound( link, network.getFlows( link ), flow_of_interest );
	}
	
	public Set<ArrivalCurve> computeArrivalBound(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
			throws Exception {
		if (f_xfcaller == null || f_xfcaller.isEmpty()) {
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve.getFactory().createZeroArrivals()));
		}

		// Get the common sub-path of f_xfcaller flows crossing the given link
		// soi == server of interference
		Server soi = link.getDest();
		Set<Flow> f_soi = network.getFlows(soi);
		Set<Flow> f_xfcaller_soi = SetUtils.getIntersection(f_soi, f_xfcaller);
		f_xfcaller_soi.remove(flow_of_interest);
		if (f_xfcaller_soi.isEmpty()) {
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve.getFactory().createZeroArrivals()));
		}

		if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO
				|| (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL
						&& link.getSource().multiplexingDiscipline() == Multiplexing.FIFO)) {
			throw new Exception( "Tandem matching arrival bounding is not available for FIFO multiplexing nodes" );
		}

		Server common_subpath_src = network.findSplittingServer(soi, f_xfcaller_soi);
		Server common_subpath_dest = link.getSource();
		Path common_subpath;
		Set<ServiceCurve> betas_loxfcaller_subpath = new HashSet<ServiceCurve>();

		common_subpath = f_xfcaller_soi.iterator().next().getPath().getSubPath(common_subpath_src, common_subpath_dest);

		if (common_subpath.numServers() == 1) {
			common_subpath = new Path(common_subpath_src);

			Set<Flow> f_xxfcaller = network.getFlows(common_subpath_src);
			f_xxfcaller.removeAll(f_xfcaller_soi);
			f_xxfcaller.remove(flow_of_interest);
			Set<ArrivalCurve> alphas_xxfcaller = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
					common_subpath_src, f_xxfcaller, flow_of_interest);

			ServiceCurve null_service = Curve.getFactory().createZeroService();

			for (ServiceCurve beta_loxfcaller_subpath : Bound.leftOverServiceARB(common_subpath_src.getServiceCurve(),
					alphas_xxfcaller)) {
				if (!beta_loxfcaller_subpath.equals(null_service)) {
					// Adding to the set, not adding up the curves
					betas_loxfcaller_subpath.add(beta_loxfcaller_subpath);
				}
			}
		} else {
			TandemMatchingAnalysis tma = new TandemMatchingAnalysis(network, configuration);
			betas_loxfcaller_subpath = tma.getServiceCurves(flow_of_interest, common_subpath, f_xfcaller_soi);
		}

		// Check if there's any service left on this path. Signaled by at least one
		// service curve in this set
		if (betas_loxfcaller_subpath.isEmpty()) {
			System.out.println( "No service left over during TMA arrival bounding!" );
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve.getFactory().createTokenBucket(0.0, Double.POSITIVE_INFINITY)));
		}

		// Get arrival bound at the splitting point:
		// We need to know the arrival bound of f_xfcaller at the server
		// 'common_subpath_src', i.e., at the above sub-path's source
		// in order to deconvolve it with beta_loxfcaller_subpath to get the arrival
		// bound of the sub-path
		// Note that flows f_xfcaller that originate in 'common_subpath_src' are covered
		// by this call of computeArrivalBound
		Set<ArrivalCurve> alpha_xfcaller_src = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
				common_subpath_src, f_xfcaller, flow_of_interest);
		Set<ArrivalCurve> alphas_xfcaller = Bound.output(configuration, alpha_xfcaller_src, common_subpath, betas_loxfcaller_subpath);

		// TODO It has not been investigated if the TFA node backlog can improve TM arrival bounds.
		if (configuration.serverBacklogArrivalBound()) {
			Server last_hop_xtx = link.getSource();
			TotalFlowAnalysis tfa = new TotalFlowAnalysis(network, configuration);
			tfa.deriveBoundsAtServer(last_hop_xtx);

			Set<Num> tfa_backlog_bounds = tfa.getServerBacklogBoundMap().get(last_hop_xtx);
			Num tfa_backlog_bound_min = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();

			for (Num tfa_backlog_bound : tfa_backlog_bounds) {
				if (tfa_backlog_bound.leq(tfa_backlog_bound_min)) {
					tfa_backlog_bound_min = tfa_backlog_bound;
				}
			}

			// Reduce the burst
			
			// TODO This implementation only works for token-bucket arrivals. 
			// It disregards the potential shift in inflection points not present in this burst cap variant.
			for (ArrivalCurve alpha_xfcaller : alphas_xfcaller) {
				if(alpha_xfcaller.getSegmentCount() > 2 ) {
					// >2 segments -> >=2 inflection points -> burst reduction not applicable! 
					continue;
				}
				if (alpha_xfcaller.getBurst().gt(tfa_backlog_bound_min)) {
					alpha_xfcaller.getSegment(1).setY(tfa_backlog_bound_min); // if the burst is >0 then there are at
					// least two segments and the second
					// holds the burst as its y-axis value
				}
			}
		}
		
		return alphas_xfcaller;
	}
}
