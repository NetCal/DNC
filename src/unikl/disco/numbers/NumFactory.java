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
 
package unikl.disco.numbers;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.implementations.RationalBigInt;
import unikl.disco.numbers.implementations.RealDouble;
import unikl.disco.numbers.implementations.RationalInt;
import unikl.disco.numbers.implementations.RealSingle;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

@SuppressWarnings("incomplete-switch")
public abstract class NumFactory {
	private static Num POSITIVE_INFINITY = createPositiveInfinity();
	private static Num NEGATIVE_INFINITY = createNegativeInfinity();
	private static Num NaN = createNaN();
	private static Num ZERO = createZero();
	private static Num EPSILON = createEpsilon();
	
	/**
	 * 
	 * Required to call if the number representation is changed.
	 * 
	 */
	public static void createSingletons() {
		POSITIVE_INFINITY = createPositiveInfinity();
		NEGATIVE_INFINITY = createNegativeInfinity();
		NaN = createNaN();
		ZERO = createZero();
		EPSILON = createEpsilon();
	}
	
	public static Num getPositiveInfinity() {
		return POSITIVE_INFINITY;
	}

	public static Num createPositiveInfinity() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( Double.POSITIVE_INFINITY );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( Float.POSITIVE_INFINITY );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new PositiveInfinity();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num getNegativeInfinity() {
		return NEGATIVE_INFINITY;
	}
	
	public static Num createNegativeInfinity() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( Double.NEGATIVE_INFINITY );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( Float.NEGATIVE_INFINITY );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new NegativeInfinity();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num getNaN() {
		return NaN;
	}
	
	public static Num createNaN() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( Double.NaN );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( Float.NaN );
			// non IEEE 754 floating point data types
			case RATIONAL_INTEGER:
			case RATIONAL_BIGINTEGER:
				return new NaN();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num getZero() {
		return ZERO;
	}
	
	public static Num createZero() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( 0 );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( 0 );
			case RATIONAL_INTEGER:
				return new RationalInt( 0 );
			case RATIONAL_BIGINTEGER:
				return new RationalBigInt( 0 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num getEpsilon() {
		return EPSILON;
	}
	
	public static Num createEpsilon() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.createEpsilon();
			case REAL_SINGLE_PRECISION:
				return RealSingle.createEpsilon();
			case RATIONAL_INTEGER:
				return RationalInt.createEpsilon();
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.createEpsilon();
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
	
	public static Num create( double value ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( value );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( value );
		}	
		
		// non IEEE 754 floating point data types
		if( value == Double.POSITIVE_INFINITY ) {
			return createPositiveInfinity();
		}
		if( value == Double.NEGATIVE_INFINITY ) {
			return createNegativeInfinity();
		}
		if( value == Double.NaN ) {
			return createNaN();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return new RationalBigInt( value );
			case RATIONAL_INTEGER:
				return new RationalInt( value );
			default:
				throw new RuntimeException( "Undefined number representation" );
		 }
	}
	
	public static Num create( int num, int den ) {
		if ( den == 0 ) { // division by integer 0 throws an arithmetic exception
			throw new ArithmeticException( "/ by zero" );
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return new RealDouble( num, den );
			case REAL_SINGLE_PRECISION:
				return new RealSingle( num, den );
			case RATIONAL_INTEGER:
				return new RationalInt( num, den );
			case RATIONAL_BIGINTEGER:
				return new RationalBigInt( num, den );
			default:
				throw new RuntimeException( "Undefined number representation" );
		 }
	}
	
	public static Num create( String num_str ) throws Exception {
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
