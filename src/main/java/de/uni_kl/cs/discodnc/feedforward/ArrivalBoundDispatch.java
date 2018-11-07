/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

package de.uni_kl.cs.discodnc.feedforward;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.feedforward.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.feedforward.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.feedforward.analyses.TandemMatchingAnalysis;
import de.uni_kl.cs.discodnc.feedforward.arrivalbounds.AggregatePboo_Concatenation;
import de.uni_kl.cs.discodnc.feedforward.arrivalbounds.AggregatePboo_PerServer;
import de.uni_kl.cs.discodnc.feedforward.arrivalbounds.AggregatePmoo;
import de.uni_kl.cs.discodnc.feedforward.arrivalbounds.AggregateTandemMatching;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.Turn;
import de.uni_kl.cs.discodnc.utils.SetUtils;

public abstract class ArrivalBoundDispatch {
	// --------------------------------------------------------------------------------------------------------------
	// Arrival Bound Dispatching
	// --------------------------------------------------------------------------------------------------------------

	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph network, AnalysisConfig configuration, Server server)
			throws Exception {
		return computeArrivalBounds(network, configuration, server, network.getFlows(server), Flow.NULL_FLOW);
	}

	/**
	 * The flow_of_interest low priority supersedes the wish to bound all flows in
	 * flows_to_bound, i.e., if flow_of_interest will be removed from flows_to_bound
	 * before bounding the arrivals such that the result will always hold (only) for
	 * {flows_to_bound} \ {flow_of_interest}.
	 * <p>
	 * To bound all flows in flows_to_bound, please call, e.g.,
	 * computeArrivalBounds( network, flows_to_bound, Flow.NULL_FLOW )
	 *
	 * @param server
	 *            The server seeing the arrival bound.
	 * @param flows_to_bound
	 *            The flows to be bounded.
	 * @param flow_of_interest
	 *            The flow of interest to get a lower priority.
	 * @return The arrival bound.
	 * @throws Exception
	 *             Potential exception raised in the called function
	 *             computeArrivalBounds.
	 */
	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph network, AnalysisConfig configuration, Server server,
			Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
		flows_to_bound.remove(flow_of_interest);
		Set<ArrivalCurve> arrival_bounds = new HashSet<ArrivalCurve>(
				Collections.singleton(Curve.getFactory().createZeroArrivals()));
		if (flows_to_bound.isEmpty()) {
			return arrival_bounds;
		}

		Set<Flow> f_server = network.getFlows(server);
		Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_server, flows_to_bound);
		if (f_xfcaller_server.isEmpty()) {
			return arrival_bounds;
		}

		// Get cross-traffic originating in server
		Set<Flow> f_xfcaller_sourceflows_server = SetUtils.getIntersection(f_xfcaller_server,
				network.getSourceFlows(server));
		if( !f_xfcaller_sourceflows_server.isEmpty() ) {
			f_xfcaller_sourceflows_server.remove(flow_of_interest);
			ArrivalCurve alpha_xfcaller_sourceflows_server = network.getSourceFlowArrivalCurve(server,f_xfcaller_sourceflows_server); // Will at least be a zeroArrivalCurve
			arrival_bounds = new HashSet<ArrivalCurve>(Collections.singleton(alpha_xfcaller_sourceflows_server));

			if (f_xfcaller_sourceflows_server.containsAll(f_xfcaller_server)) {
				return arrival_bounds;
			}
		}

		// Get cross-traffic from each predecessor. Call per turn in order to get
		// splitting points.
		Set<ArrivalCurve> arrival_bounds_turn;
		Set<ArrivalCurve> arrival_bounds_turn_permutations = new HashSet<ArrivalCurve>();

		Iterator<Turn> in_turn_iter = network.getInTurns(server).iterator();
		while (in_turn_iter.hasNext()) {

			Turn in_l = in_turn_iter.next();
			Set<Flow> f_xfcaller_in_l = SetUtils.getIntersection(network.getFlows(in_l), f_xfcaller_server);
			f_xfcaller_in_l.remove(flow_of_interest);

			if (f_xfcaller_in_l.isEmpty()) { // Do not check turns without flows of interest
				continue;
			}

			arrival_bounds_turn = computeArrivalBounds(network, configuration, in_l, f_xfcaller_in_l, flow_of_interest);

			// Add the new bounds to the others:
			// * Consider all the permutations of different bounds per in turn.
			// * Care about the configuration.convolveAlternativeArrivalBounds()-flag later.
			for (ArrivalCurve arrival_bound_turn : arrival_bounds_turn) {
				Curve.beautify(arrival_bound_turn);

				for (ArrivalCurve arrival_bound_exiting : arrival_bounds) {
					arrival_bounds_turn_permutations.add(Curve.add(arrival_bound_turn, arrival_bound_exiting));
				}
			}

			arrival_bounds.clear();
			arrival_bounds.addAll(arrival_bounds_turn_permutations);
			arrival_bounds_turn_permutations.clear();
		}

		return arrival_bounds;
	}

	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph network, AnalysisConfig configuration, Turn turn,
			Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
		flows_to_bound.remove(flow_of_interest);
		if (flows_to_bound.isEmpty()) {
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve.getFactory().createZeroArrivals()));
		}

		Set<ArrivalCurve> arrival_bounds_xfcaller = new HashSet<ArrivalCurve>();

		for (AnalysisConfig.ArrivalBoundMethod arrival_bound_method : configuration.arrivalBoundMethods()) {
			Set<ArrivalCurve> arrival_bounds_tmp = new HashSet<ArrivalCurve>();

			switch (arrival_bound_method) {
			case AGGR_PBOO_PER_SERVER:
				AggregatePboo_PerServer aggr_pboo_per_server = AggregatePboo_PerServer.getInstance();
				aggr_pboo_per_server.setNetwork(network);
				aggr_pboo_per_server.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pboo_per_server.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			case AGGR_PBOO_CONCATENATION:
				AggregatePboo_Concatenation aggr_pboo_concatenation = AggregatePboo_Concatenation.getInstance();
				aggr_pboo_concatenation.setNetwork(network);
				aggr_pboo_concatenation.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pboo_concatenation.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			case AGGR_PMOO:
				AggregatePmoo aggr_pmoo = AggregatePmoo.getInstance();
				aggr_pmoo.setNetwork(network);
				aggr_pmoo.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pmoo.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			/* 
			 * There are no functional tests for Tandem Matching-based arrival bounding
			 * or segregate arrival bounding methods. 
			 */
				
			case AGGR_TM:
				AggregateTandemMatching aggr_tm = AggregateTandemMatching.getInstance();
				aggr_tm.setNetwork(network);
				aggr_tm.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_tm.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			// This arrival bound is known to be inferior to PMOO and the PBOO_* variants.
			case SEGR_PBOO:
				for (Flow flow : flows_to_bound) {
					SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network);
					sfa.performAnalysis(flow, flow.getSubPath(flow.getSource(), turn.getSource()));

					arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
							singleFlowABs(configuration, flow.getArrivalCurve(), sfa.getLeftOverServiceCurves()));
				}
				break;

			// This arrival bound can yield better results than PMOO and the PBOO_* variants. See:
			/*
			 * Catching Corner Cases in Network Calculus - Flow Segregation Can Improve Accuracy.
			 * Steffen Bondorf, Paul Nikolaus and Jens B. Schmitt,
			 * In Proceedings of 19th International GI/ITG Conference on 
			 * Measurement, Modelling and Evaluation of Computing Systems (MMB), 2018.
			 */
			case SEGR_PMOO:
				for (Flow flow : flows_to_bound) {
					PmooAnalysis pmoo = new PmooAnalysis(network);
					pmoo.performAnalysis(flow, flow.getSubPath(flow.getSource(), turn.getSource()));

					arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
							singleFlowABs(configuration, flow.getArrivalCurve(), pmoo.getLeftOverServiceCurves()));
				}
				break;

			case SEGR_TM:
				for (Flow flow : flows_to_bound) {
					TandemMatchingAnalysis tma = new TandemMatchingAnalysis(network);
					tma.performAnalysis(flow, flow.getSubPath(flow.getSource(), turn.getSource()));

					arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
							singleFlowABs(configuration, flow.getArrivalCurve(), tma.getLeftOverServiceCurves()));
				}
				break;

			default:
				System.out.println("Executing default arrival bounding: AGGR_PBOO_CONCATENATION");
				AggregatePboo_Concatenation default_ab = new AggregatePboo_Concatenation(network, configuration);
				arrival_bounds_tmp = default_ab.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;
			}

			addArrivalBounds(configuration, arrival_bounds_tmp, arrival_bounds_xfcaller);
		}

		return arrival_bounds_xfcaller;
	}

	private static Set<ArrivalCurve> singleFlowABs(AnalysisConfig configuration, ArrivalCurve alpha,
			Set<ServiceCurve> betas_lo) throws Exception {
		Set<ArrivalCurve> arrival_bounds_f = new HashSet<ArrivalCurve>();

		// All permutations of single flow results
		for (ServiceCurve beta_lo : betas_lo) {
			arrival_bounds_f.add(Calculator.getInstance().getMinPlus().deconvolve(alpha, beta_lo));
		}

		return arrival_bounds_f;
	}

	private static Set<ArrivalCurve> getPermutations(Set<ArrivalCurve> arrival_curves_1,
			Set<ArrivalCurve> arrival_curves_2) {
		if (arrival_curves_1.isEmpty()) {
			return new HashSet<ArrivalCurve>(arrival_curves_2);
		}
		if (arrival_curves_2.isEmpty()) {
			return new HashSet<ArrivalCurve>(arrival_curves_1);
		}

		Set<ArrivalCurve> arrival_bounds_merged = new HashSet<ArrivalCurve>();

		for (ArrivalCurve alpha_1 : arrival_curves_1) {
			for (ArrivalCurve alpha_2 : arrival_curves_2) {
				arrival_bounds_merged.add(Curve.add(alpha_1, alpha_2));
			}
		}

		return arrival_bounds_merged;
	}

	private static void addArrivalBounds(AnalysisConfig configuration, Set<ArrivalCurve> arrival_bounds_to_merge,
			Set<ArrivalCurve> arrival_bounds) {
		if (configuration.arrivalBoundMethods().size() == 1) { // In this case there can only be one arrival bound
			arrival_bounds.addAll(arrival_bounds_to_merge);
		} else {
			for (ArrivalCurve arrival_bound : arrival_bounds_to_merge) {
				addArrivalBounds(configuration, arrival_bound, arrival_bounds);
			}
		}
	}

	private static void addArrivalBounds(AnalysisConfig configuration, ArrivalCurve arrival_bound_to_merge,
			Set<ArrivalCurve> arrival_bounds) {
		if (configuration.arrivalBoundMethods().size() == 1) { // In this case there can only be one arrival bound
			arrival_bounds.add(arrival_bound_to_merge);
		} else {
			if (!configuration.removeDuplicateArrivalBounds() || (configuration.removeDuplicateArrivalBounds()
					&& !isDuplicate(arrival_bound_to_merge, arrival_bounds))) {
				arrival_bounds.add(arrival_bound_to_merge);
			}
		}
	}

	private static boolean isDuplicate(ArrivalCurve arrival_bound_to_check, Set<ArrivalCurve> arrival_bounds) {
		for (ArrivalCurve arrival_bound_existing : arrival_bounds) {
			if (arrival_bound_to_check.equals(arrival_bound_existing)) {
				return true;
			}
		}
		return false;
	}
}