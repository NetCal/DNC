/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2015 - 2018 Steffen Bondorf
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.Turn;

public class ArrivalBoundCache {
	private Map<Server,Set<CacheEntryServer>> map__server__entries = new HashMap<Server,Set<CacheEntryServer>>();
	private Map<Turn,Set<CacheEntryTurn>> map__turn__entries = new HashMap<Turn,Set<CacheEntryTurn>>();
	
	protected ArrivalBoundCache() {};
	
	protected void clearCache() {
		map__server__entries = new HashMap<Server,Set<CacheEntryServer>>();
		map__turn__entries = new HashMap<Turn,Set<CacheEntryTurn>>();
	}

	/**
	 * 
	 * The cache content is generally overwritten!
	 * If there is an entry for this arrival bound
	 * and the arrival bounds are not convolved into a single one,
	 * then the cache just replaces any existing cache entry. 
	 * 
	 * @param configuration
	 * @param server
	 * @param bounded_flows
	 * @param flow_of_interest
	 * @param arrival_bounds
	 * @return
	 * @throws Exception
	 */
	protected void addArrivalBounds( AnalysisConfig configuration,
												  Server server,
												  Set<Flow> bounded_flows,
												  Flow flow_of_interest,
												  Set<ArrivalCurve> arrival_bounds ) throws Exception {
		if( bounded_flows.contains( flow_of_interest )
				|| bounded_flows.isEmpty()
				|| arrival_bounds.isEmpty() ) {
			return;
		}
		
		// Remove possible old entry
		CacheEntryServer entry = getCacheEntry( configuration, server, bounded_flows, flow_of_interest ); 
		if ( entry != null ) {
			// get(...) cannot return null as the entry was already confirmed to exist.
			map__server__entries.get( server ).remove( entry );
		}
		
		Set<ArrivalCurve> arrival_bounds_stored;
		
		// Create arrival bound to store
		if( configuration.convolveAlternativeArrivalBounds() ) {	// Convolve given bounds into one.
			arrival_bounds_stored = Collections.singleton( Calculator.getInstance().getMinPlus().convolve( arrival_bounds ) );
		} else { 													// Take them as they are.
			arrival_bounds_stored = new HashSet<ArrivalCurve>( arrival_bounds );
		}
		entry = new CacheEntryServer( configuration.copy(), server, new HashSet<Flow>( bounded_flows ), flow_of_interest, arrival_bounds_stored );
		
		// No need to check if the maps has entries for the server or the flow of interest.
		// If missing they were created by the call to getCacheEntry(...) above.
		map__server__entries.get( server ).add( entry );
	}
	
	protected Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest ) {
		CacheEntryServer entry = getCacheEntry( configuration, server, bounded_flows, flow_of_interest );
		if ( entry == null ) {
			return new HashSet<ArrivalCurve>();
		} else {
			return new HashSet<ArrivalCurve>( entry.arrival_bounds );
		}
	}
	
	/**
	 * 
	 * Returns the cache entry for the given parameters if there is one.
	 * 
	 * If not, it returns null.
	 * 
	 * In addition, if there's no entry, the internal mappings will be created as a subsequent adding of an according entry is expected.
	 * 
	 * @param server
	 * @param bounded_flows
	 * @param flow_of_interest
	 * @return
	 */
	protected CacheEntryServer getCacheEntry( AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest ) {
		// Most important feature is an efficient search
		boolean return_null = false;
		Set<CacheEntryServer> entries_s = map__server__entries.get( server );
		if ( entries_s == null ) {
			map__server__entries.put( server, new HashSet<CacheEntryServer>() );
			return_null = true;
		}
		
		if ( return_null ) {
			return null;
		}

		Set<CacheEntryServer> candidates = new HashSet<CacheEntryServer>( entries_s );

		for ( CacheEntryServer entry : candidates ) {
			if( entry.configuration.enforceMultiplexing() == configuration.enforceMultiplexing()
				&& entry.configuration.enforceMaxSC() == configuration.enforceMaxSC()
				&& entry.configuration.enforceMaxScOutputRate() == configuration.enforceMaxScOutputRate()
				&& entry.bounded_flows.size() == bounded_flows.size()
				&& entry.flow_of_interest.getId() == flow_of_interest.getId()
				&& entry.bounded_flows.containsAll( bounded_flows ) // should be the most expensive operation so do it last 
			) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * 
	 * The cache content is generally overwritten!
	 * If there is an entry for this arrival bound
	 * and the arrival bounds are not convolved into a single one,
	 * then the cache just replaces any existing cache entry. 
	 * 
	 * @param configuration
	 * @param server
	 * @param bounded_flows
	 * @param flow_of_interest
	 * @param arrival_bounds
	 * @return
	 * @throws Exception
	 */
	protected void addArrivalBounds( AnalysisConfig configuration,
												  Turn turn,
												  Set<Flow> bounded_flows,
												  Flow flow_of_interest,
												  Set<ArrivalCurve> arrival_bounds ) throws Exception {
		if( bounded_flows.contains( flow_of_interest )
				|| bounded_flows.isEmpty()
				|| arrival_bounds.isEmpty() ) {
			return;
		}
		
		// Remove possible old entry
		CacheEntryTurn entry = getCacheEntry( configuration, turn, bounded_flows, flow_of_interest ); 
		if ( entry != null ) {
			// get(...) cannot return null as the entry was already confirmed to exist.
			map__turn__entries.get( turn ).remove( entry );
		}
		
		Set<ArrivalCurve> arrival_bounds_stored;
		
		// Create arrival bound to store
		if( configuration.convolveAlternativeArrivalBounds() ) {	// Convolve given bounds into one.
			arrival_bounds_stored = Collections.singleton( Calculator.getInstance().getMinPlus().convolve( arrival_bounds ) );
		} else { 													// Take them as they are.
			arrival_bounds_stored = new HashSet<ArrivalCurve>( arrival_bounds );
		}
		entry = new CacheEntryTurn(configuration, turn, new HashSet<Flow>( bounded_flows ), flow_of_interest, arrival_bounds_stored );
		
		map__turn__entries.get( turn ).add( entry );
	}
	
	protected Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Turn turn, Set<Flow> bounded_flows, Flow flow_of_interest ) {
		CacheEntryTurn entry = getCacheEntry( configuration, turn, bounded_flows, flow_of_interest );
		if ( entry == null ) {
			return new HashSet<ArrivalCurve>();
		} else {
			return new HashSet<ArrivalCurve>( entry.arrival_bounds );
		}
	}
	
	protected CacheEntryTurn getCacheEntry( AnalysisConfig configuration, Turn turn, Set<Flow> bounded_flows, Flow flow_of_interest ) {
		// Most important feature is an efficient search
		boolean return_null = false;
		Set<CacheEntryTurn> entries_l = map__turn__entries.get( turn );
		if ( entries_l == null ) {
			map__turn__entries.put( turn, new HashSet<CacheEntryTurn>() );
			return_null = true;
		}
		
		if ( return_null ) {
			return null;
		}

		Set<CacheEntryTurn> candidates = new HashSet<CacheEntryTurn>( entries_l );

		for ( CacheEntryTurn entry : candidates ) {
			if( entry.configuration.enforceMultiplexing() == configuration.enforceMultiplexing()
				&& entry.configuration.enforceMaxSC() == configuration.enforceMaxSC()
				&& entry.configuration.enforceMaxScOutputRate() == configuration.enforceMaxScOutputRate()
				&& entry.bounded_flows.size() == bounded_flows.size()
				&& entry.flow_of_interest.getId() == flow_of_interest.getId()
				&& entry.bounded_flows.containsAll( bounded_flows ) // should be the most expensive operation so do it last 
			) {
				return entry;
			}
		}
		return null;
	}
	
	// Cache entry types
	class CacheEntry {
		protected AnalysisConfig configuration;
		protected Set<Flow> bounded_flows;
		protected Flow flow_of_interest;
		protected Set<ArrivalCurve> arrival_bounds;
		
		protected CacheEntry( AnalysisConfig configuration,
							  Set<Flow> bounded_flows,
							  Flow flow_of_interest,
							  Set<ArrivalCurve> arrival_bounds ) {
			this.configuration = configuration;
			this.bounded_flows = bounded_flows;
			this.flow_of_interest = flow_of_interest;
			this.arrival_bounds = arrival_bounds;
		}
		
		@Override
		public String toString() {
			String result = "CacheEntry(";
			
			result += ";\n";
			if ( bounded_flows != null ) {
				result += bounded_flows.toString();
			} else {
				result += "null_flows";
			}
			result += ";\n";
			if ( flow_of_interest != null ) {
				result += flow_of_interest.toString();
			} else {
				result += "null_foi";
			}
			result += ";\n";
			if ( arrival_bounds != null ) {
				result += arrival_bounds.toString();
			} else {
				result += "null_arrival_curve";
			}
			
			return result += ";\n" + configuration.arrivalBoundMethods().toString() + "; "
					+ configuration.enforceMultiplexing() + "; " 
					+ configuration.enforceMaxSC() + "; "
					+ configuration.enforceMaxScOutputRate() + ")";
		}
	}
	
	class CacheEntryServer extends CacheEntry {
		protected Server server;
		
		protected CacheEntryServer( AnalysisConfig configuration,
									Server server,
									Set<Flow> bounded_flows,
									Flow flow_of_interest,
									Set<ArrivalCurve> arrival_bounds ) {
			super( configuration, bounded_flows, flow_of_interest, arrival_bounds );
			this.server = server;
		}
		
		@Override
		public String toString() {
			String superclass = super.toString();
								// length of CacheEntry( is 11
			superclass.substring( 11, superclass.length() );
			
			String result = "CacheEntryServer(";
			if ( server != null ) {
				result += server.toString();
			} else {
				result += "null_server";
			}
			
			return result + superclass;
		}
	}

	class CacheEntryTurn extends CacheEntry {
		protected Turn turn;
		
		protected CacheEntryTurn( AnalysisConfig configuration, Turn turn, Set<Flow> bounded_flows, Flow flow_of_interest, Set<ArrivalCurve> arrival_bounds ) {
			super( configuration, bounded_flows, flow_of_interest, arrival_bounds );
			this.turn = turn;
		}
		
		@Override
		public String toString() {
			String superclass = super.toString();
								// length of CacheEntry( is 11
			superclass.substring( 11, superclass.length() );

			String result = "CacheEntry(";
			
			if ( turn != null ) {
				result += turn.toString();
			} else {
				result += "null_server";
			}
			
			return result + superclass;
		}
	}
}