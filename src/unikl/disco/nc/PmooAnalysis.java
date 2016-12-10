/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.8 "Heavy Ion".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.nc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;

import unikl.disco.curves.Curve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Path;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;
import unikl.disco.numbers.NumUtils;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Andreas Kiefer
 * @author Steffen Bondorf
 * 
 */
public class PmooAnalysis extends Analysis {	
	@SuppressWarnings("unused")
	private PmooAnalysis() {}
	
	public PmooAnalysis( Network network ) {
		super( network );
		super.result = new PmooAnalysisResults();
	}
	
	public PmooAnalysis( Network network, AnalysisConfig configuration ) {
		super( network, configuration );
		super.result = new PmooAnalysisResults();
	}

	/**
	 * Performs a pay-multiplexing-only-once (PMOO) analysis for the <code>flow_of_interest</code>.
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
		if( configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO )
		{
			throw new Exception( "PMOO analysis is not available for FIFO multiplexing nodes" );
		}
		
		((PmooAnalysisResults) result).betas_e2e = getServiceCurves( flow_of_interest, path, Collections.singleton( flow_of_interest ) );

		Num delay_bound__beta_e2e;
		Num backlog_bound__beta_e2e;
		
		result.delay_bound = NumFactory.createPositiveInfinity();
		result.backlog_bound = NumFactory.createPositiveInfinity();
		
		for( ServiceCurve beta_e2e : ((PmooAnalysisResults) result).betas_e2e ) {
			delay_bound__beta_e2e = DelayBound.deriveFIFO( flow_of_interest.getArrivalCurve(), beta_e2e ); // Single flow of interest, i.e., fifo per micro flow holds
			if( delay_bound__beta_e2e.leq( result.delay_bound ) ) {
				result.delay_bound = delay_bound__beta_e2e;
			}
			
			backlog_bound__beta_e2e = BacklogBound.derive( flow_of_interest.getArrivalCurve(), beta_e2e );
			if( backlog_bound__beta_e2e.leq( result.backlog_bound ) ) {
				result.backlog_bound = backlog_bound__beta_e2e;
			}
		}
		
		result.succeeded = true;
	}
	
	public Set<ServiceCurve> getServiceCurves( Flow flow_of_interest, Path path, Set<Flow> flows_to_serve ) throws Exception {
		if( configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL ) {
			for( Server s : path.getServers() ) {
				if( s.multiplexingDiscipline() == Multiplexing.FIFO ) {
					throw new Exception( "PMOO analysis is not available for FIFO multiplexing nodes" );
				}
			}
		}
		
		Set<ServiceCurve> betas_e2e = new HashSet<ServiceCurve>();
		
		// Get cross-flows grouped as needed for the PMOO left-over service curve
		Set<Flow> flows_of_lower_priority = new HashSet<Flow>( flows_to_serve );
		flows_of_lower_priority.add( flow_of_interest );
		
		Set<Flow> cross_flows = network.getFlows( path );
		cross_flows.removeAll( flows_of_lower_priority );
		Map<Path,Set<Flow>> xtx_subpath_grouped = network.groupFlowsPerSubPath( path, cross_flows );
		
		if( xtx_subpath_grouped.isEmpty() ) {
			return new HashSet<ServiceCurve>( Collections.singleton( path.getServiceCurve() ) );
		}
		
		// Derive the cross-flow substitutes with their arrival bound
		Set<List<Flow>> cross_flow_substitutes_set = new HashSet<List<Flow>>();
		cross_flow_substitutes_set.add( new LinkedList<Flow>() );
		Set<List<Flow>> arrival_bounds_link_permutations = new HashSet<List<Flow>>();

		for ( Map.Entry<Path,Set<Flow>> entry : xtx_subpath_grouped.entrySet() ) {
		// Create a single substitute flow 
	 		// Name the substitute flow
	 		String substitute_flow_alias = "subst_{";
	 		for( Flow f : entry.getValue() ) {
	 			substitute_flow_alias = substitute_flow_alias.concat( f.getAlias() + "," );
	 		}
	 		substitute_flow_alias = substitute_flow_alias.substring( 0, substitute_flow_alias.length()-1 ); // Remove trailing comma.
	 		substitute_flow_alias = substitute_flow_alias.concat( "}" );

	 		// Derive the substitute flow's arrival bound
	 		Set<ArrivalCurve> alphas_xf_group = ArrivalBound.computeArrivalBounds( network, configuration, entry.getKey().getSource(), entry.getValue(), Flow.NULL_FLOW );
			// entry.getKey().getSource() because entry.getKey() is the common subpath of path (above variable), i.e., start of interference on path.
			// We are leaving the flow_of_interest's path with this arrival bounding. Therefore, worst-case arbitrary multiplexing cannot be modeled 
			//   with assigning lowest priority to the flow of interest anymore (cf. rejoining flows) and we call computeArrivalBounds with Flow.NULL_FLOW instead of flow_of_interest.

			// Add the new bounds to the others by creating all the permutations.
			// * For ever arrival bound derived for a flow substitute
			// * take the existing flow substitutes (that belong to different subpaths)
			// * and "multiply" the cross_flow_substitutes_set, i.e., create a new set each.
	 		
			arrival_bounds_link_permutations.clear();
 			List<Flow> flow_list_tmp = new LinkedList<Flow>();
 			for( ArrivalCurve alpha : alphas_xf_group ) {
	 			alpha.beautify();
	 			
	 			for( List<Flow> f_subst_list : cross_flow_substitutes_set ) {
	 				// The new list of cross-flow substitutes = old list plus a new one with one of the derived arrival bounds. 
	 				flow_list_tmp.clear();
	 				flow_list_tmp.addAll( f_subst_list );
	 				flow_list_tmp.add( Flow.createDummyFlow( substitute_flow_alias, alpha, entry.getKey() ) );
	 				
	 				// Add this list to the set of permutations
	 				arrival_bounds_link_permutations.add( new LinkedList<Flow>( flow_list_tmp ) ); // Prevent interaction with the clear() operation above.
	 			}
	 		} 
	 		// Override cross_flow_substitutes_set for the next cross-flow substitute and its arrival bounds.
			cross_flow_substitutes_set.clear();
			cross_flow_substitutes_set.addAll( arrival_bounds_link_permutations );
			arrival_bounds_link_permutations.clear();
			
			if ( result.map__server__alphas.get( entry.getKey().getSource() ) == null ) {
				result.map__server__alphas.put( entry.getKey().getSource(), alphas_xf_group );
			} else {
				result.map__server__alphas.get( entry.getKey().getSource() ).addAll( alphas_xf_group );
			}
		}
		
		// Derive the left-over service curves
		ServiceCurve null_service = ServiceCurve.createNullService();
		for( List<Flow> xtx_substitutes : cross_flow_substitutes_set ) {
			ServiceCurve beta_e2e = PmooAnalysis.getServiceCurve( path, xtx_substitutes );
			
			if( !beta_e2e.equals( null_service ) ) {
				betas_e2e.add( beta_e2e ); // Adding to the set, not adding up the curves
			}
		}

		if( betas_e2e.isEmpty() ) {
			betas_e2e.add( ServiceCurve.createNullService() );
		}
		return betas_e2e;
	}
	
	/**
	 * Concatenates the service curves along the given path <code>path</code>
	 * according to the PMOO approach and returns the result.
	 * 
	 * It first decomposes all arrival curves (service curves) into token
	 * buckets (rate latency curves), enumerates over all combinations of token
	 * buckets and rate latency curves, and calls
	 * <code>computePartialPMOOServiceCurve()</code> for each combination. The
	 * total PMOO service curve is the maximum of all partial service curves.
	 * 
	 * @param path
	 *            The Path traversed for which a PMOO left-over service curve will be computed.
	 * @param cross_flow_substitutes
	 *            Flow substitutes according to PMOO's needs and abstracting from the actual cross-flows.
	 * @return The PMOO service curve
	 */
	public static ServiceCurve getServiceCurve( Path path, List<Flow> cross_flow_substitutes ) {
		// Create a flow-->tb_iter map
		Map<Flow,Integer> flow_tb_iter_map = new HashMap<Flow, Integer>();
		for ( Flow f : cross_flow_substitutes )
		{
			flow_tb_iter_map.put( f, Integer.valueOf( 0 ) );
		}
		// Create a list of rl_iters
		int number_servers = path.getServers().size();
		ServiceCurve[] service_curves = new ServiceCurve[number_servers];
		int[] server_rl_iters = new int[number_servers];
		int[] server_rl_counts = new int[number_servers];
		int i = 0;
		for ( Server server : path.getServers() )
		{
			ServiceCurve service_curve = server.getServiceCurve();
			service_curves[i] = service_curve;
			server_rl_iters[i] = 0;
			server_rl_counts[i] = service_curve.getRLComponentCount();
			i++;
		}

		ServiceCurve beta_total = ServiceCurve.createNullService();

		boolean more_combinations = true;
		while ( more_combinations )
		{
			// Compute service curve for this combination
			ServiceCurve beta = computePartialPMOOServiceCurve(	path,
															service_curves,
															cross_flow_substitutes,
															flow_tb_iter_map,
															server_rl_iters );
			if ( !beta.equals( ServiceCurve.createNullService() ) )
			{
				beta_total = ServiceCurve.max( beta_total, beta );
			}

			// First check whether there are more combinations of flow TBs
			more_combinations = false;
			Flow f;
			ArrivalCurve f_bound;
			for ( Entry<Flow,Integer> entry : flow_tb_iter_map.entrySet() ) {
				f = entry.getKey();
				f_bound = f.getArrivalCurve();
				
				i = flow_tb_iter_map.get( f ).intValue();
				if ( i + 1 < f_bound.getTBComponentCount() )
				{
					flow_tb_iter_map.put( f, Integer.valueOf( i + 1 ) );
					more_combinations = true;
					break;
				}
				else
				{
					flow_tb_iter_map.put( f, Integer.valueOf( 0 ) );
				}
			}

			// If not, check whether there are more combinations of server RLs
			if ( !more_combinations )
			{
				for ( i = 0; i < server_rl_iters.length; i++ )
				{
					int j = server_rl_iters[i];
					if ( j + 1 < server_rl_counts[i] )
					{
						server_rl_iters[i] = j;
						more_combinations = true;
						break;
					}
					else
					{
						server_rl_iters[i] = 0;
					}
				}
			}
		}

		return beta_total;
	}

	/**
	 * Calculates the partial PMOO service curve for the given flow set by
	 * combining all servers having an outgoing link contained in the given
	 * link-path. For each flow considers only one of its token bucket
	 * components (selected via the flow_tb_iter_map) and for each service curve
	 * considers only one rate latency curve (selected via the server_rl_iters).
	 * 
	 * @param path The tandem of servers the left-over service curve holds for.
	 * @param service_curves The service curves on the path.
	 * @param cross_flow_substitutes Flows representing a group of segregated flows.
	 * @param flow_tb_iter_map Defines which token bucket component of a flow's arrival bound to use.
	 * @param server_rl_iters Defines which rate latency component of a server's service curve to use.
	 * @return A partial PMOO service curve.
	 */
	protected static ServiceCurve computePartialPMOOServiceCurve(	Path path,
															ServiceCurve[] service_curves,
															List<Flow> cross_flow_substitutes,
															Map<Flow, Integer> flow_tb_iter_map,
															int[] server_rl_iters )
	{
		Num T = NumFactory.createZero();
		Num R = NumFactory.createPositiveInfinity();
		Num sum_bursts = NumFactory.createZero();
		Num sum_latencyterms = NumFactory.createZero();

		double sum_r_at_s;
		
		Set<Flow> present_flows = new HashSet<Flow>();
		for ( Server s : path.getServers() )
		{
			sum_r_at_s = 0.0;
			int i = path.getServers().indexOf( s );
			// Add incoming flows
			for ( Flow f : cross_flow_substitutes ) {
				if ( f.getPath().getServers().contains( s ) ) { // The exact path of the substitute does not matter, only the shared servers with the flow of interest do
					present_flows.add( f );
					sum_r_at_s += f.getArrivalCurve().getSustainedRate().doubleValue();
				}
			}

			// Check for stability constraint violation
			if( sum_r_at_s >= s.getServiceCurve().getSustainedRate().doubleValue() ) {
				return ServiceCurve.createNullService();
			}

			Curve current_rl = service_curves[i].getRLComponent( server_rl_iters[i] );

			// Sum up latencies
			T = NumUtils.add( T, current_rl.getLatency() );

			// Compute and store sum of rates of all passing flows
			Num sum_r = NumFactory.createZero();
			for ( Flow f : present_flows )
			{
				ArrivalCurve bound = f.getArrivalCurve();
				Curve current_tb = bound.getTBComponent( ((Integer) flow_tb_iter_map.get( f )).intValue() );
				sum_r = NumUtils.add( sum_r, current_tb.getSustainedRate() );
			}

			// Update latency terms (increments)
			sum_latencyterms = NumUtils.add( sum_latencyterms, NumUtils.mult( sum_r, current_rl.getLatency() ) );

			// Compute left-over rate; update min
			Num Ri = NumUtils.sub( current_rl.getSustainedRate(), sum_r );
			if ( Ri.leq( NumFactory.getZero() ) )
			{
				return ServiceCurve.createNullService();
			}
			R = NumUtils.min( R, Ri );

			// Remove all outgoing flows from the set of present flows
			Set<Flow> leaving_flows = new HashSet<Flow>();
			for ( Flow f : present_flows ) {
				if ( path.getServers().indexOf( f.getSink() ) <= i ) {
					leaving_flows.add( f );
				}
			}
			present_flows.removeAll( leaving_flows );
		}

		// Compute sum of bursts
		for ( Flow f : cross_flow_substitutes )
		{
			ArrivalCurve bound = f.getArrivalCurve();
			Curve current_tb = bound.getTBComponent( ((Integer) flow_tb_iter_map.get( f )).intValue() );
			sum_bursts = NumUtils.add( sum_bursts, current_tb.getTBBurst() );
		}

 		T = NumUtils.add( T, NumUtils.div( NumUtils.add( sum_bursts, sum_latencyterms ), R ) );

 		if( T == NumFactory.getPositiveInfinity() ) {
 			return ServiceCurve.createNullService();
 		}
 		if( R == NumFactory.getPositiveInfinity() ) {
 			return ServiceCurve.createDelayedInfiniteBurst( T );
 		}

		return ServiceCurve.createRateLatency( R, T );
	}

	public Set<ServiceCurve> getLeftOverServiceCurves() {
		return ((PmooAnalysisResults) result).betas_e2e;
	}
}