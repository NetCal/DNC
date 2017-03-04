/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.2 "Centaur".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
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

package unikl.disco.nc.analyses;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.AnalysisResults;
import unikl.disco.network.Server;
import unikl.disco.numbers.Num;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Andreas Kiefer
 * @author Steffen Bondorf
 * 
 */
public class TotalFlowResults extends AnalysisResults {
	protected Map<Server,Set<Num>> map__server__D_server;
	protected Map<Server,Set<Num>> map__server__B_server;
	
	protected TotalFlowResults(){
		super();
		map__server__D_server = new HashMap<Server,Set<Num>>();
		map__server__B_server = new HashMap<Server,Set<Num>>();
	}
	
	protected TotalFlowResults( Num delay_bound,
					 	 Map<Server,Set<Num>> map__server__D_server,
					 	 Num backlog_bound,
					 	 Map<Server,Set<Num>> map__server__B_server,
					 	 Map<Server,Set<ArrivalCurve>> map__server__alphas ) {
		
		super( true, delay_bound, backlog_bound, map__server__alphas );
		
		this.map__server__D_server = map__server__D_server;
		this.map__server__B_server = map__server__B_server;
	}
	
	public String getServerDelayBoundMapString() {
		if( !succeeded ) {
			return "Analysis failed";
		}
		
		if( map__server__D_server.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );
		for( Entry<Server,Set<Num>> entry : map__server__D_server.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( Num delay_bound : entry.getValue() ) {
				result_str.append( delay_bound.toString() );
				result_str.append( "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		result_str.append( "}" );
		
		return result_str.toString();
	}
	
	public String getServerBacklogBoundMapString() {
		if( map__server__B_server.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );
		for( Entry<Server,Set<Num>> entry : map__server__B_server.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( Num delay_bound : entry.getValue() ) {
				result_str.append( delay_bound.toString() );
				result_str.append( "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		result_str.append( "}" );
		
		return result_str.toString();
	}
}