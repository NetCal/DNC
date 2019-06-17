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

public class LogicalConstraint {
	FlowLocationTime flow_shape_1;
	Relation relation;
	FlowLocationTime flow_shape_2;
	
	public LogicalConstraint( Flow flow,
							Server server1, Path path1,
							Relation relation,
							Server server2, Path path2 ) {
		this.flow_shape_1 = new FlowLocationTime( flow, server1, path1 );
		this.relation = relation;
		this.flow_shape_2 = new FlowLocationTime( flow, server2, path2 );
	}
	
	public FlowLocationTime getFlow1Shape() {
		return flow_shape_1;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public FlowLocationTime getFlow2Shape() {
		return flow_shape_2;
	}
	
	@Override
	public String toString() {
		StringBuffer result_str = new StringBuffer();
		
		result_str.append( flow_shape_1.toString() );
		
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
		
		result_str.append( flow_shape_2.toString() );
		
		return result_str.toString();
	}
	
	public String toCPLEXstring() {
		StringBuffer result_str = new StringBuffer();
		
		result_str.append( flow_shape_1.toString() );
		
		result_str.append( " - " );
		
		result_str.append( flow_shape_2.toString() );
		
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
		
		result_str.append( "0" );
		
		return result_str.toString();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof LogicalConstraint ) ) {
			return false;
		}

		LogicalConstraint constraint = (LogicalConstraint) obj;
		return flow_shape_1.equals( constraint.flow_shape_1 )
				&& relation.equals( constraint.relation )
				&& flow_shape_2.equals( constraint.flow_shape_2 );
	}
	
	@Override
	public int hashCode() {
		return (int) flow_shape_1.hashCode() * relation.hashCode() * flow_shape_2.hashCode();
	}
}