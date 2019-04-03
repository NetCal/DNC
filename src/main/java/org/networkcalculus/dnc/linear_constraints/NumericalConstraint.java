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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

public class NumericalConstraint {
	HashSet<Pair<Operator,FlowLocationTime>> flow_shape_terms;
	Relation relation;
	HashSet<NumericalTerm> num_terms;
	
	public NumericalConstraint( HashSet<Pair<Operator,FlowLocationTime>> flow_shape_terms,
							Relation relation,
							HashSet<NumericalTerm> num_terms ) {
		this.flow_shape_terms = flow_shape_terms;
		this.relation = relation;
		this.num_terms = num_terms;
	}
	
	public Set<Pair<Operator,FlowLocationTime>> getFlowShapeTerms() {
		return flow_shape_terms;
	}
	
	public Relation getRelation() {
		return relation;
	}
	
	public Set<NumericalTerm> getNumTerms() {
		return num_terms;
	}
	
	@Override
	public String toString() {
		StringBuffer result_str = new StringBuffer();
		
		for( Pair<Operator,FlowLocationTime> flow_term : flow_shape_terms ) {
			switch( flow_term.getFirst() ) {
				case PLUS:
				default:
					result_str.append( " + " );
					break;
				case MINUS:
					result_str.append( " - " );
					break;
			}
			
			result_str.append( flow_term.getSecond().toString() );
		}
		
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

		for( NumericalTerm term : num_terms ) {
			result_str.append( term.toString() );
		}
		
		return result_str.toString();
	}
	
	public String toCPLEXstring() {
		StringBuffer result_str = new StringBuffer();
		
		for( Pair<Operator,FlowLocationTime> flow_term : flow_shape_terms ) {
			switch( flow_term.getFirst() ) {
				case PLUS:
				default:
					result_str.append( " + " );
					break;
				case MINUS:
					result_str.append( " - " );
					break;
			}
			
			result_str.append( flow_term.getSecond().toString() );
		}

		// num_terms include a + or - 
		LinkedList<NumericalTerm> pure_numbers = new LinkedList<NumericalTerm>();
		for( NumericalTerm term : num_terms ) {
			if( term.path == null ) {
				pure_numbers.add( term );
			} else {
				result_str.append( term.toInversedString() );
			}
		}
		
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
		
		double result = 0;
		for( NumericalTerm term : pure_numbers ) {
			switch( term.operator ) {
			case PLUS:
				result += term.value.doubleValue();
				break;
			case MINUS:
				result -= term.value.doubleValue();
				break;
			}
		}

		result_str.append( Double.toString( result ) );
		
		return result_str.toString();
	}
}