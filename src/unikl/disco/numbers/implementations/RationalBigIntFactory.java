/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2014 - 2017 Steffen Bondorf
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
 
package unikl.disco.numbers.implementations;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactoryInterface;
import unikl.disco.numbers.extraValues.NaN;
import unikl.disco.numbers.extraValues.NegativeInfinity;
import unikl.disco.numbers.extraValues.PositiveInfinity;
import unikl.disco.numbers.implementations.RationalBigInt;

public class RationalBigIntFactory implements NumFactoryInterface {
	private Num POSITIVE_INFINITY = createPositiveInfinity();
	private Num NEGATIVE_INFINITY = createNegativeInfinity();
	private Num NaN = createNaN();
	private Num ZERO = createZero();
	private Num EPSILON = createEpsilon();
	
	public Num getPositiveInfinity() {
		return POSITIVE_INFINITY;
	}

	public Num createPositiveInfinity() {
		return new PositiveInfinity();
	}
	
	public Num getNegativeInfinity() {
		return NEGATIVE_INFINITY;
	}
	
	public Num createNegativeInfinity() {
		return new NegativeInfinity();
	}

	public Num getNaN() {
		return NaN;
	}
	
	public Num createNaN() {
		return new NaN();
	}

	public Num getZero() {
		return ZERO;
	}
	
	public Num createZero() {
		return new RationalBigInt( 0 );
	}
	
	public Num getEpsilon() {
		return EPSILON;
	}
	
	public Num createEpsilon() {
		return RationalBigInt.createEpsilon();
	}
	
	public Num create( double value ) {
		// non IEEE 754 floating point data types
		if( value == Double.POSITIVE_INFINITY ) {
			return createPositiveInfinity();
		}
		if( value == Double.NEGATIVE_INFINITY ) {
			return createNegativeInfinity();
		}
		if( Double.isNaN( value ) ) {
			return createNaN();
		}
		
		return new RationalBigInt( value );
	}
	
	public Num create( int num, int den ) {
		if ( den == 0 ) { // division by integer 0 throws an arithmetic exception
			throw new ArithmeticException( "/ by zero" );
		}
		
		return new RationalBigInt( num, den );
	}
	
	public Num create( String num_str ) throws Exception {
		if( num_str.equals( "Infinity" ) ) {
			return createPositiveInfinity();
		}
		if( num_str.equals( "-Infinity" ) ) {
			return createNegativeInfinity();
		}
		if( num_str.equals( "NaN" ) || num_str.equals( "NA" ) ) {
			return createNaN();
		}
		
		boolean fraction_indicator = num_str.contains( " / " );
		boolean double_based = num_str.contains( "." );
		
		if ( fraction_indicator && double_based ) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.getNumClass().toString() 
									+ ": " + num_str );
		}
		
		try {
			// either an integer of something strange
			if ( !fraction_indicator && !double_based ) {
				return create( Integer.parseInt( num_str ) );
			}
			
			if ( fraction_indicator ) {
				String[] num_den = num_str.split( " / " ); // ["num","den"]
				if( num_den.length != 2 ) {
					throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.getNumClass().toString() 
											+ ": " + num_str );
				}
				
				int den = Integer.parseInt( num_den[1] );
				if( den != 0 ) {
					return create( Integer.parseInt( num_den[0] ), den );
				} else {
					return createNaN();
				}
			}
			
			if ( double_based ) {
				return create( Double.parseDouble( num_str ) );
			}
		} catch (Exception e) {
			throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.getNumClass().toString()
									+ ": " + num_str );
		}
		
		// This code should not be reachable because all the operations above either succeed such that we can return a number
		// of raise an exception of some kind. Yet, Java does not get this and thus complains if there's no "finalizing statement". 
		throw new Exception( "Invalid string representation of a number based on " + CalculatorConfig.getNumClass().toString()
								+ ": " + num_str );
	}
}
