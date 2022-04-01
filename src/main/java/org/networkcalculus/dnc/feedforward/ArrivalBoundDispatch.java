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

package org.networkcalculus.dnc.feedforward;

import java.util.*;
import java.util.Map.Entry;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.arrivalbounds.AggregatePboo_Concatenation;
import org.networkcalculus.dnc.feedforward.arrivalbounds.AggregatePboo_PerServer;
import org.networkcalculus.dnc.feedforward.arrivalbounds.AggregatePmoo;
import org.networkcalculus.dnc.feedforward.arrivalbounds.AggregateTandemMatching;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TandemMatchingAnalysis;
import org.networkcalculus.dnc.utils.SetUtils;

public abstract class ArrivalBoundDispatch {
	// --------------------------------------------------------------------------------------------------------------
	// Arrival Bound Cache
	// --------------------------------------------------------------------------------------------------------------
	protected static Map<Set<ArrivalBoundMethod>,ArrivalBoundCache> ab_caches = new HashMap<Set<ArrivalBoundMethod>,ArrivalBoundCache>();
	
	private static ArrivalBoundCache getCache( Set<ArrivalBoundMethod> ab_methods ) {
		for ( Entry<Set<ArrivalBoundMethod>,ArrivalBoundCache> cache_entry : ab_caches.entrySet() ) {
			if( cache_entry.getKey().size() == ab_methods.size()
					&& cache_entry.getKey().containsAll( ab_methods ) ) {
				return cache_entry.getValue();
			}
		}
		
		// Reaching this code here means that there is
		// no cache for this set of ab_methods in the set of caches yet.
		// So we create one, add it to the map and return it.
		ArrivalBoundCache new_ab_cache = new ArrivalBoundCache();
		ab_caches.put( ab_methods, new_ab_cache );
		
		return new_ab_cache;
	}
	
	public static void clearAllCaches() {
		ab_caches.clear();
	}
	
	// --------------------------------------------------------------------------------------------------------------
	// Arrival Bound Dispatching
	// --------------------------------------------------------------------------------------------------------------

	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph server_graph, AnalysisConfig configuration, Server server)
			throws Exception {
		return computeArrivalBounds(server_graph, configuration, server, server_graph.getFlows(server), Flow.NULL_FLOW);
	}

	/**
	 * The flow_of_interest low priority supersedes the wish to bound all flows in
	 * flows_to_bound, i.e., if flow_of_interest will be removed from flows_to_bound
	 * before bounding the arrivals such that the result will always hold (only) for
	 * {flows_to_bound} \ {flow_of_interest}.
	 * <p>
	 * To bound all flows in flows_to_bound, please call, e.g.,
	 * computeArrivalBounds( server_graph, flows_to_bound, Flow.NULL_FLOW )
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
	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph server_graph, AnalysisConfig configuration, Server server,
															Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
		flows_to_bound.remove(flow_of_interest);
		Set<ArrivalCurve> arrival_bounds = new HashSet<ArrivalCurve>(
				Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
		if (flows_to_bound.isEmpty()) {
			return arrival_bounds;
		}

		Set<Flow> f_server = server_graph.getFlows(server);
		Set<Flow> f_xfcaller_server = SetUtils.getIntersection(f_server, flows_to_bound);
		if (f_xfcaller_server.isEmpty()) {
			return arrival_bounds;
		}
		
		if( configuration.useArrivalBoundsCache() 
				&& configuration.enforceMultiplexing() != MultiplexingEnforcement.SERVER_LOCAL ) { // Do not cache in that case. Too many variables, the cache does not check all of them.
			ArrivalBoundCache.CacheEntryServer entry = getCache( configuration.arrivalBoundMethods() ).getCacheEntry( configuration, server, flows_to_bound, flow_of_interest );
			if( entry != null && !entry.arrival_bounds.isEmpty()
					&& !(entry.arrival_bounds.size() > 1 && configuration.convolveAlternativeArrivalBounds()) // Inconsistency between current cache content and current setting.
					) {
				// Be cautious here! By using the original cache entry instead of the getArrivalBounds function, we need to repack the result in a new set manually!
				return new HashSet<ArrivalCurve>( entry.arrival_bounds );
			}
		}

		// Get cross-traffic originating in server
		Set<Flow> f_xfcaller_sourceflows_server = SetUtils.getIntersection(f_xfcaller_server,
				server_graph.getSourceFlows(server));
		if( !f_xfcaller_sourceflows_server.isEmpty() ) {
			f_xfcaller_sourceflows_server.remove(flow_of_interest);
			ArrivalCurve alpha_xfcaller_sourceflows_server = server_graph.getSourceFlowArrivalCurve(server,f_xfcaller_sourceflows_server); // Will at least be a zeroArrivalCurve
			arrival_bounds = new HashSet<ArrivalCurve>(Collections.singleton(alpha_xfcaller_sourceflows_server));

			if (f_xfcaller_sourceflows_server.containsAll(f_xfcaller_server)) {
				return arrival_bounds;
			}
		}

		// Get cross-traffic from each predecessor. Call per turn in order to get
		// splitting points.
		Set<ArrivalCurve> arrival_bounds_turn;
		Set<ArrivalCurve> arrival_bounds_turn_permutations = new HashSet<ArrivalCurve>();

		Iterator<Turn> in_turn_iter = server_graph.getInTurns(server).iterator();
		while (in_turn_iter.hasNext()) {

			Turn in_l = in_turn_iter.next();
			Set<Flow> f_xfcaller_in_l = SetUtils.getIntersection(server_graph.getFlows(in_l), f_xfcaller_server);
			f_xfcaller_in_l.remove(flow_of_interest);

			if (f_xfcaller_in_l.isEmpty()) { // Do not check turns without flows of interest
				continue;
			}


			arrival_bounds_turn = computeArrivalBounds(server_graph, configuration, in_l, f_xfcaller_in_l, flow_of_interest);

			// Add the new bounds to the others:
			// * Consider all the permutations of different bounds per in turn.
			// * Care about the configuration.convolveAlternativeArrivalBounds()-flag later.
			boolean haveEncounteredFiniteAB=false;
			for (ArrivalCurve arrival_bound_turn : arrival_bounds_turn) {
				Curve.getUtils().beautify(arrival_bound_turn);

				for (ArrivalCurve arrival_bound_existing : arrival_bounds) {

					if(!arrival_bound_turn.isDelayedInfiniteBurst())
					{
						haveEncounteredFiniteAB=true;
						arrival_bounds_turn_permutations.add(Curve.getUtils().add(arrival_bound_turn, arrival_bound_existing));
					}
				}
			}

			if(!haveEncounteredFiniteAB)
			{
				return new HashSet<ArrivalCurve>( Arrays.asList(Curve.getFactory().createArrivalCurve((Curve)Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get())));
			}

			arrival_bounds.clear();
			arrival_bounds.addAll(arrival_bounds_turn_permutations);
			arrival_bounds_turn_permutations.clear();
		}

		if( configuration.convolveAlternativeArrivalBounds() ) {
			arrival_bounds = new HashSet<ArrivalCurve>( Collections.singleton( Calculator.getInstance().getMinPlus().convolve( arrival_bounds ) ) );
		} 
		
		if( configuration.useArrivalBoundsCache() 
				&& configuration.enforceMultiplexing() != MultiplexingEnforcement.SERVER_LOCAL ) { // Do not cache in that case. Too many variables, the cache does not check all of them.
			
			// As we checked for an existing cache entry at the beginning (and returned it of present), we do not hav to care about the potential overwriting of a cache entry here. 
			getCache( configuration.arrivalBoundMethods() ).addArrivalBounds( configuration, server, flows_to_bound, flow_of_interest, arrival_bounds );
		}

		return new HashSet<ArrivalCurve>( arrival_bounds );
	}

	public static Set<ArrivalCurve> computeArrivalBounds(ServerGraph server_graph, AnalysisConfig configuration, Turn turn,
			Set<Flow> flows_to_bound, Flow flow_of_interest) throws Exception {
		flows_to_bound.remove(flow_of_interest);
		if (flows_to_bound.isEmpty()) {
			return new HashSet<ArrivalCurve>(Collections.singleton(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()));
		}
		
		if( configuration.useArrivalBoundsCache() && configuration.enforceMultiplexing() != MultiplexingEnforcement.SERVER_LOCAL ) { // Do not cache in that case. Too many variables.
			ArrivalBoundCache.CacheEntryTurn entry = getCache( configuration.arrivalBoundMethods() ).getCacheEntry( configuration, turn, flows_to_bound, flow_of_interest );
			if( entry != null && !entry.arrival_bounds.isEmpty()
					&& !(entry.arrival_bounds.size() > 1 && configuration.convolveAlternativeArrivalBounds()) // Inconsistency between current cache content and current setting.
					) {
				// Be cautious here! By using the original cache entry instead of the getArrivalBounds function, we need to repack the result in a new set manually!
				return new HashSet<ArrivalCurve>( entry.arrival_bounds );
			}
		}

		Set<ArrivalCurve> arrival_bounds_xfcaller = new HashSet<ArrivalCurve>();
		
		for (AnalysisConfig.ArrivalBoundMethod arrival_bound_method : configuration.arrivalBoundMethods()) {
			Set<ArrivalCurve> arrival_bounds_tmp = new HashSet<ArrivalCurve>();

			switch (arrival_bound_method) {
			case AGGR_PBOO_PER_SERVER:
				AggregatePboo_PerServer aggr_pboo_per_server = AggregatePboo_PerServer.getInstance();
				aggr_pboo_per_server.setServerGraph(server_graph);
				aggr_pboo_per_server.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pboo_per_server.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			case AGGR_PBOO_CONCATENATION:
				AggregatePboo_Concatenation aggr_pboo_concatenation = AggregatePboo_Concatenation.getInstance();
				aggr_pboo_concatenation.setServerGraph(server_graph);
				aggr_pboo_concatenation.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pboo_concatenation.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			case AGGR_PMOO:
				AggregatePmoo aggr_pmoo = AggregatePmoo.getInstance();
				aggr_pmoo.setServerGraph(server_graph);
				aggr_pmoo.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_pmoo.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			/* 
			 * There are no functional tests for Tandem Matching-based arrival bounding
			 * or segregate arrival bounding methods. 
			 */
				
			case AGGR_TM:
				AggregateTandemMatching aggr_tm = AggregateTandemMatching.getInstance();
				aggr_tm.setServerGraph(server_graph);
				aggr_tm.setConfiguration(configuration);
				arrival_bounds_tmp = aggr_tm.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;

			// This arrival bound is known to be inferior to PMOO and the PBOO_* variants.
			case SEGR_PBOO:
				for (Flow flow : flows_to_bound) {
					SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(server_graph);
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
					PmooAnalysis pmoo = new PmooAnalysis(server_graph);
					pmoo.performAnalysis(flow, flow.getSubPath(flow.getSource(), turn.getSource()));

					arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
							singleFlowABs(configuration, flow.getArrivalCurve(), pmoo.getLeftOverServiceCurves()));
				}
				break;

			case SEGR_TM:
				for (Flow flow : flows_to_bound) {
					TandemMatchingAnalysis tma = new TandemMatchingAnalysis(server_graph);
					tma.performAnalysis(flow, flow.getSubPath(flow.getSource(), turn.getSource()));

					arrival_bounds_tmp = getPermutations(arrival_bounds_tmp,
							singleFlowABs(configuration, flow.getArrivalCurve(), tma.getLeftOverServiceCurves()));
				}
				break;

			default:
				System.out.println("Executing default arrival bounding: AGGR_PBOO_CONCATENATION");
				AggregatePboo_Concatenation default_ab = new AggregatePboo_Concatenation(server_graph, configuration);
				arrival_bounds_tmp = default_ab.computeArrivalBound(turn, flows_to_bound, flow_of_interest);
				break;
			}

			arrival_bounds_xfcaller.addAll( arrival_bounds_tmp );
		}
		
		if( configuration.convolveAlternativeArrivalBounds() ) {
			arrival_bounds_xfcaller = new HashSet<ArrivalCurve>( Collections.singleton( Calculator.getInstance().getMinPlus().convolve( arrival_bounds_xfcaller ) ) );
		}

		if( configuration.useArrivalBoundsCache() 
				&& configuration.enforceMultiplexing() != MultiplexingEnforcement.SERVER_LOCAL ) { // Do not cache in that case. Too many variables, the cache does not check all of them.
			
			// As we checked for an existing cache entry before the for-loop (and returned it of present), we do not hav to care about the potential overwriting of a cache entry here. 
			getCache( configuration.arrivalBoundMethods() ).addArrivalBounds( configuration, turn, flows_to_bound, flow_of_interest, arrival_bounds_xfcaller );
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
				arrival_bounds_merged.add(Curve.getUtils().add(alpha_1, alpha_2));
			}
		}

		return arrival_bounds_merged;
	}
}