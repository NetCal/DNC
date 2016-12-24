/*
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

/**
 * 
 * The IEEE 754 floating point data types double and single provide infinity values and NaN.
 * For other data types, i.e., rational numbers based on the Apache Commons Math's Fraction classes,
 * we need to emulate the floating point behavior. 
 * 
 */
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.implementations.RationalBigInt;
import unikl.disco.numbers.implementations.RealDouble;
import unikl.disco.numbers.implementations.RationalInt;
import unikl.disco.numbers.implementations.RealSingle;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

@SuppressWarnings("incomplete-switch")
public abstract class NumUtils {
	public static Num add( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.add( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.add( (RealSingle)num1, (RealSingle)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( num1 instanceof PositiveInfinity && num2 instanceof NegativeInfinity ) 
				|| ( num1 instanceof NegativeInfinity && num2 instanceof PositiveInfinity ) ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity ) { // other num is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) { // other num is not positive infinity
			return new NegativeInfinity(); 
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				 return RationalBigInt.add( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.add( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num sub( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.sub( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.sub( (RealSingle)num1, (RealSingle)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( num1 instanceof PositiveInfinity && num2 instanceof PositiveInfinity ) 
				|| ( num1 instanceof NegativeInfinity && num2 instanceof NegativeInfinity ) ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity				// num2 is not positive infinity
				|| num2 instanceof NegativeInfinity ) {	// num1 is not negative infinity
			return new PositiveInfinity(); 
		}
		if( num1 instanceof NegativeInfinity				// num2 is not negative infinity
				|| num2 instanceof PositiveInfinity ) {	// num1 is not positive infinity
			return new NegativeInfinity(); 
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.sub( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.sub( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num mult( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.mult( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.mult( (RealSingle)num1, (RealSingle)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) {
			if ( num2.ltZero() || num2 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity ) {
			if ( num1.ltZero() || num1 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) {
			if ( num2.ltZero() || num2 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof NegativeInfinity ) {
			if ( num1.ltZero() || num1 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.mult( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.mult( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num div( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.div( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.div( (RealSingle)num1, (RealSingle)num2 );
		}

		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( ( num1 instanceof PositiveInfinity || num1 instanceof NegativeInfinity ) && ( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) ) ) { // two infinities in the division
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) { // positive infinity divided by some finite value
			if( num2.ltZero() ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) { // negative infinity divided by some finite value 
			if( num2.ltZero() ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) { // finite value divided by infinity
			return NumFactory.createZero();
		}

		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
		        if ( ((RationalBigInt)num2).eqZero() ) {
		        	return new PositiveInfinity();
		       	} else {
					return RationalBigInt.div( (RationalBigInt)num1, (RationalBigInt)num2 );     		
		       	}
			case RATIONAL_INTEGER:
		        if ( ((RationalInt)num2).eqZero() ) {
		        	return new PositiveInfinity();
		       	} else {
		       		return RationalInt.div( (RationalInt)num1, (RationalInt)num2 );        		
		       	}
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num abs( Num num ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.abs( (RealDouble)num );
			case REAL_SINGLE_PRECISION:
				return RealSingle.abs( (RealSingle)num );
		}
		
		if( num instanceof NaN ) {
			return new NaN();
		}
		if( num instanceof PositiveInfinity || num instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.abs( (RationalBigInt)num );
			case RATIONAL_INTEGER:
				return RationalInt.abs( (RationalInt)num );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num diff( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.diff( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.diff( (RealSingle)num1, (RealSingle)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) { 
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity 
				|| num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.diff( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.diff( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num max( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.max( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.max( (RealSingle)num1, (RealSingle)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity ) {
			return new PositiveInfinity();
		}
		if( num1 instanceof NegativeInfinity ) {
			return num2.copy();
		}
		if( num2 instanceof NegativeInfinity ) {
			return num1.copy();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.max( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.max( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num min( Num num1, Num num2 ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.min( (RealDouble)num1, (RealDouble)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSingle.min( (RealSingle)num1, (RealSingle)num2 );
		}	
		
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) {
			return new NegativeInfinity();
		}
		if( num1 instanceof PositiveInfinity ) {
			return num2.copy();
		}
		if( num2 instanceof PositiveInfinity ) {
			return num1.copy();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.min( (RationalBigInt)num1, (RationalBigInt)num2 );
			case RATIONAL_INTEGER:
				return RationalInt.min( (RationalInt)num1, (RationalInt)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num negate( Num num ) {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_DOUBLE_PRECISION:
				return RealDouble.negate( (RealDouble)num );
			case REAL_SINGLE_PRECISION:
				return RealSingle.negate( (RealSingle)num );
		}	
		
		if( num instanceof NaN ) {
			return new NaN();
		}
		if( num instanceof PositiveInfinity ) {
			return new NegativeInfinity();
		}
		if( num instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.getNumClass() ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInt.negate( (RationalBigInt)num );
			case RATIONAL_INTEGER:
				return RationalInt.negate( (RationalInt)num );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
}
