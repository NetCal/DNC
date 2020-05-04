/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2015 - 2018 Steffen Bondorf
 * Copyright (C) 2018 The DiscoDNC contributors
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

package org.networkcalculus.dnc.tandem.analyses;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
import org.networkcalculus.num.Num;

public class TandemMatchingAnalysis extends AbstractTandemAnalysis {
	@SuppressWarnings("unused")
	private TandemMatchingAnalysis() {}

	public Set<ServiceCurve> getLeftOverServiceCurves() {
		return ((TandemMatchingResults) result).betas_e2e;
	}
	
	public TandemMatchingAnalysis(ServerGraph server_graph) {
        super.server_graph = server_graph;
        super.configuration = new AnalysisConfig();
		super.result = new TandemMatchingResults();
	}
	
	public TandemMatchingAnalysis(ServerGraph server_graph, AnalysisConfig configuration) {
        super.server_graph = server_graph;
        super.configuration = configuration;
		super.result = new TandemMatchingResults();
	}
	
	/**
	 * Performs a Tandem Matching Analysis for the <code>flow_of_interest</code>.
	 * 
	 * @param flow_of_interest
	 *            the flow for which the end-to-end service curve shall be computed.
	 */
	public void performAnalysis( Flow flow_of_interest ) throws Exception
	{
		performAnalysis( flow_of_interest, flow_of_interest.getPath() );
	}

	public void performAnalysis( Flow flow_of_interest, Path path ) throws Exception
	{
		if( configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO )
		{
			throw new Exception( "Cutting analysis is not available for FIFO multiplexing nodes" );
		} else {
			if( configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL ) {
				for( Server s : path.getServers() ) {
					if( s.multiplexing() == Multiplexing.FIFO ) {
						throw new Exception( "Cutting analysis is not available for FIFO multiplexing nodes" );
					}
				}
			}
		}
		
		((TandemMatchingResults) result).betas_e2e = getServiceCurves( flow_of_interest, path, Collections.singleton( flow_of_interest ) );

		Num delay_bound__beta_e2e;
		Num backlog_bound__beta_e2e;

        ((TandemMatchingResults) result).setDelayBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
        ((TandemMatchingResults) result).setBacklogBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
		
		for( ServiceCurve beta_e2e : ((TandemMatchingResults) result).betas_e2e ) {
			delay_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().delayFIFO( flow_of_interest.getArrivalCurve(), beta_e2e ); // Single flow of interest, i.e., fifo per micro flow holds
			if( delay_bound__beta_e2e.leq(result.getDelayBound()) ) {
				 ((TandemMatchingResults) result).setDelayBound(delay_bound__beta_e2e);
			}
			
			backlog_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().backlog( flow_of_interest.getArrivalCurve(), beta_e2e );
			if( backlog_bound__beta_e2e.leq(result.getBacklogBound()) ) {
				((TandemMatchingResults) result).setBacklogBound(backlog_bound__beta_e2e);
			}
		}
	}
	
	public static List<List<Path>> getAllSubPathCombinations( Path path ) {
		List<Turn> path_turns = path.getTurns();
		int path_length = path_turns.size();

		List<List<Path>> sub_path_combinations = new LinkedList<List<Path>>();
		
		// Get all binary combinations (turn = cut it Y/N)
		// Store as list of turns to cut

		sub_path_combinations.add( new LinkedList<Path>( Collections.singleton( path ) ) );	
		
		for ( int i = 1; i < (int) Math.pow( 2, path_length ); i++ ) {
			String s = Integer.toBinaryString( i );
			
			LinkedList<Turn> cuts = new LinkedList<Turn>();
			for( int j = s.length() - 1; j >= 0; j-- ) {
				if( s.charAt( j ) == '1' ) {
					cuts.add( path_turns.get( s.length() - j - 1) );
				}
			}

			List<Path> sub_paths = new LinkedList<Path>();
			
			Server from = path.getSource();
			Server to = cuts.get( 0 ).getSource();
			
			try {
				// from and to are inclusive!
				sub_paths.add( path.getSubPath( from, to ) );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if( cuts.size() > 1 ) { // otherwise there will only be two sub-paths
				for( int j = 1; j < cuts.size(); j++ ) {
					from = cuts.get( j-1 ).getDest();
					to = cuts.get( j ).getSource();
					try {
						// from and to are inclusive!
						sub_paths.add( path.getSubPath( from, to ) );
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			from = cuts.get( cuts.size()-1 ).getDest();
			to = path.getSink();
			try {
				// from and to are inclusive!
				sub_paths.add( path.getSubPath( from, to ) );
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			sub_path_combinations.add( sub_paths );
		}

		return sub_path_combinations;
	}
	
	public Set<ServiceCurve> getServiceCurves( Flow flow_of_interest, Path path, Set<Flow> flows_to_serve ) throws Exception
	{
		// Brute-force: Iterate over all binary combinations
		List<List<Path>> sub_path_combinations = getAllSubPathCombinations( path );

		Set<ServiceCurve> betas_e2e = new HashSet<ServiceCurve>();
		for ( List<Path> combination : sub_path_combinations ) {					// Every sub_path_combination
			Set<ServiceCurve> betas_e2e_combination = new HashSet<ServiceCurve>();	// will have a set of end-to-end left-over service curves
			for ( Path sub_path : combination ) {									// computed as the convolution of its partial left-over service curves.
				betas_e2e_combination = Calculator.getInstance().getMinPlus().convolve( 
						betas_e2e_combination, getSubTandemServiceCurves( flow_of_interest, sub_path, new HashSet<Flow>( flows_to_serve ) ));
			}
			betas_e2e.addAll( betas_e2e_combination );
		}

		return betas_e2e;
	}
	
	
	public Set<ServiceCurve> getSubTandemServiceCurves( Flow flow_of_interest, Path path, Set<Flow> flows_to_serve ) throws Exception {
		Set<ServiceCurve> betas_e2e = new HashSet<ServiceCurve>();
		
		// Get cross-flows grouped as needed for the PMOO left-over service curve
		Set<Flow> flows_of_lower_priority = new HashSet<Flow>( flows_to_serve );
		flows_of_lower_priority.add( flow_of_interest );
		
		Set<Flow> cross_flows = server_graph.getFlows( path );
		cross_flows.removeAll( flows_of_lower_priority );
		Map<Pair<Turn,Path>,Set<Flow>> xtx_subpath_grouped = server_graph.groupFlowsPerInturnSubPath( path, cross_flows );
		
		if( xtx_subpath_grouped.isEmpty() ) {
			return new HashSet<ServiceCurve>( Collections.singleton( path.getServiceCurve() ) );
		}
		
		// Derive the cross-flow substitutes with their arrival bound
		Set<List<Flow>> cross_flow_substitutes_set = new HashSet<List<Flow>>();
		cross_flow_substitutes_set.add( new LinkedList<Flow>() );
		Set<List<Flow>> arrival_bounds_turn_permutations = new HashSet<List<Flow>>();

		Server path_src;
		
		for ( Entry<Pair<Turn,Path>,Set<Flow>> entry : xtx_subpath_grouped.entrySet() ) {
		// Create a single substitute flow 
			
			path_src = entry.getKey().getSecond().getSource();
			
	 		// Name the substitute flow
	 		String substitute_flow_alias = "subst_{";
	 		for( Flow f : entry.getValue() ) {
	 			substitute_flow_alias = substitute_flow_alias.concat( f.getAlias() + "," );
	 		}
	 		substitute_flow_alias = substitute_flow_alias.substring( 0, substitute_flow_alias.length()-1 ); // Remove trailing comma.
	 		substitute_flow_alias = substitute_flow_alias.concat( "}" );

	 		// Derive the substitute flow's arrival bound
	 		Set<ArrivalCurve> alphas_xf_group;
	 		

			Path foi_path = flow_of_interest.getPath();
			
	 		if( foi_path.getTurns().contains( entry.getKey().getFirst() ) ) {
	 			alphas_xf_group = ArrivalBoundDispatch.computeArrivalBounds( server_graph, configuration, path_src, entry.getValue(), flow_of_interest );
	 		} else {
	 			// We are leaving the flow_of_interest's path with this arrival bounding.
	 			// Therefore, worst-case arbitrary multiplexing cannot be modeled with 
				// assigning lowest prioritization to the flow of interest anymore (cf. rejoining flows)
	 			// and we call computeArrivalBounds with Flow.NULL_FLOW instead of flow_of_interest.
			 	alphas_xf_group = ArrivalBoundDispatch.computeArrivalBounds( server_graph, configuration, path_src, entry.getValue(), Flow.NULL_FLOW );
	 		}

			// Add the new bounds to the others by creating all the permutations.
			// * For ever arrival bound derived for a flow substitute
			// * take the existing flow substitutes (that belong to different subpaths)
			// * and "multiply" the cross_flow_substitutes_set, i.e., create a new set each.
	 		
			arrival_bounds_turn_permutations.clear();
 			List<Flow> flow_list_tmp = new LinkedList<Flow>();
 			for( ArrivalCurve alpha : alphas_xf_group ) {
 				Curve.getUtils().beautify(alpha);
	 			
	 			for( List<Flow> f_subst_list : cross_flow_substitutes_set ) {
	 				// The new list of cross-flow substitutes = old list plus a new one with one of the derived arrival bounds. 
	 				flow_list_tmp.clear();
	 				flow_list_tmp.addAll( f_subst_list );
	 				flow_list_tmp.add( Flow.createDummyFlow( substitute_flow_alias, alpha, entry.getKey().getSecond() ) );
	 				
	 				// Add this list to the set of permutations
	 				arrival_bounds_turn_permutations.add( new LinkedList<Flow>( flow_list_tmp ) ); // Prevent interaction with the clear() operation above.
	 			}
	 		} 
	 		// Override cross_flow_substitutes_set for the next cross-flow substitute and its arrival bounds.
			cross_flow_substitutes_set.clear();
			cross_flow_substitutes_set.addAll( arrival_bounds_turn_permutations );
			arrival_bounds_turn_permutations.clear();
			
			if ( result.map__server__alphas.get( path_src ) == null ) {
				result.map__server__alphas.put( path_src, alphas_xf_group );
			} else {
				result.map__server__alphas.get( path_src ).addAll( alphas_xf_group );
			}
		}
		
		// Derive the left-over service curves
		ServiceCurve null_service = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
		for( List<Flow> xtx_substitutes : cross_flow_substitutes_set ) {
			ServiceCurve beta_e2e = PmooAnalysis.getServiceCurve( path, xtx_substitutes );
			
			if( !beta_e2e.equals( null_service ) ) {
				betas_e2e.add( beta_e2e ); // Adding to the set, not adding up the curves
			}
		}

		if( betas_e2e.isEmpty() ) {
			betas_e2e.add( Curve_ConstantPool.ZERO_SERVICE_CURVE.get() );
		}
		return betas_e2e;
	}
}