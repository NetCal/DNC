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

package org.networkcalculus.dnc.linear_constraints;

import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;

//Id a flow's 'shape' by its location and time instant 
public class FlowLocationTime {
	Flow flow;
	Server server;
	Path path; // will be used to determine the time instant t{path}
	
	public FlowLocationTime( Flow flow, Server server, Path path ) {
		this.flow = flow;
		this.server = server;
		this.path = path;
	}
	
	public Flow getFlow() {
		return flow;
	}
	
	public Server getServer() {
		return server;
	}
	
	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		StringBuffer result_str = new StringBuffer();
		
		result_str.append( flow.getAlias() );
		result_str.append( "_" );
		result_str.append( server.getAlias() );
		result_str.append( "_" );
		result_str.append( "t" );
		result_str.append( "{" );
		for( Server server : path.getServers() ) {
			result_str.append( server.getAlias() );
		}
		result_str.append( "}" );
		
		return result_str.toString();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof FlowLocationTime ) ) {
			return false;
		}

		FlowLocationTime flt = (FlowLocationTime) obj;
		return flow.equals( flt.flow )
				&& server.equals( flt.server )
				&& path.equals( flt.path );
	}
	
	@Override
	public int hashCode() {
		return (int) flow.hashCode() * server.hashCode() * path.hashCode();
	}
}