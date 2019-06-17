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

import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;

public class TemporalConstraint {
	Path path1;
	Relation relation;
	Path path2;
	
	public TemporalConstraint( Path path1, Relation relation, Path path2 ) {
		this.path1 = path1;
		this.relation = relation;
		this.path2 = path2;
	}
	
	public Path getPath1() {
		return path1;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public Path getPath2() {
		return path2;
	}
	
	@Override
	public String toString() {
		StringBuffer result_str = new StringBuffer();

		result_str.append( "t" );
		result_str.append( "{" );
		for( Server server : path1.getServers() ) {
			result_str.append( server.getAlias() );
		}
		result_str.append( "}" );
		
		switch( relation ) {
			case L:
				result_str.append( " < " );
				break;
			case LE:
			default:
				result_str.append( " <= " );
				break;
			case E:
				result_str.append( " = " );
				break;
			case GE:
				result_str.append( " >= " );
				break;
			case G:
				result_str.append( " > " );
				break;
		}

		result_str.append( "t" );
		result_str.append( "{" );
		for( Server server : path2.getServers() ) {
			result_str.append( server.getAlias() );
		}
		result_str.append( "}" );
		
		return result_str.toString();
	}

	public String toCPLEXstring() {
		StringBuffer result_str = new StringBuffer();

		result_str.append( "t" );
		result_str.append( "{" );
		for( Server server : path1.getServers() ) {
			result_str.append( server.getAlias() );
		}
		result_str.append( "}" );

		result_str.append( " - " );

		result_str.append( "t" );
		result_str.append( "{" );
		for( Server server : path2.getServers() ) {
			result_str.append( server.getAlias() );
		}
		result_str.append( "}" );
		
		
		switch( relation ) {
			case L:
				result_str.append( " < " );
				break;
			case LE:
			default:
				result_str.append( " <= " );
				break;
			case E:
				result_str.append( " = " );
				break;
			case GE:
				result_str.append( " >= " );
				break;
			case G:
				result_str.append( " > " );
				break;
		}

		result_str.append( " 0 " );
		
		return result_str.toString();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof TemporalConstraint ) ) {
			return false;
		}

		TemporalConstraint constraint = (TemporalConstraint) obj;
		return path1.equals( constraint.path1 )
				&& relation.equals( constraint.relation )
				&& path2.equals( constraint.path2 );
	}
	
	@Override
	public int hashCode() {
		return (int) path1.hashCode() * relation.hashCode() * path2.hashCode();
	}
}
