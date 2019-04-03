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
import org.networkcalculus.num.Num;

public class NumericalTerm { // {+,-}value * t{path}
	Operator operator;
	Num value;
	Path path;
	
	public NumericalTerm ( Operator operator, Num number, Path path ) {
		this.operator = operator;
		this.value = number;
		this.path = path;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Num getValue() {
		return value;
	}
	
	public Path getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		StringBuffer result_str = new StringBuffer();
		
		switch( operator ) {
			case PLUS:
			default:
				result_str.append( " + " );
				break;
			case MINUS:
				result_str.append( " - " );
				break;
		}

		result_str.append( value.toString() );
		result_str.append( " " );
		
		if( path != null ) {
			result_str.append( "t" );
			result_str.append( "{" );
			for( Server server : path.getServers() ) {
				result_str.append( server.getAlias() );
			}
			result_str.append( "}" );
		}
	
		return result_str.toString();
	}
	
	public String toInversedString() {
		StringBuffer result_str = new StringBuffer();
		
		switch( operator ) {
			case PLUS:
			default:
				result_str.append( " - " );
				break;
			case MINUS:
				result_str.append( " + " );
				break;
		}

		result_str.append( value.toString() );
		result_str.append( " " );
		
		if( path != null ) {
			result_str.append( "t" );
			result_str.append( "{" );
			for( Server server : path.getServers() ) {
				result_str.append( server.getAlias() );
			}
			result_str.append( "}" );
		}
	
		return result_str.toString();
	}
}
