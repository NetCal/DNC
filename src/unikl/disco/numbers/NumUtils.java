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
import unikl.disco.numbers.implementations.RationalBigInteger;
import unikl.disco.numbers.implementations.RealDoublePrecision;
import unikl.disco.numbers.implementations.RationalInteger;
import unikl.disco.numbers.implementations.RealSinglePrecision;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

@SuppressWarnings("incomplete-switch")
public class NumUtils {
	public static Num add( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.add( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.add( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
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
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				 return RationalBigInteger.add( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.add( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num sub( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.sub( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.sub( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
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
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.sub( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.sub( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num mult( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.mult( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.mult( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) {
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) {
			if ( num2.lessZero() || num2 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity ) {
			if ( num1.lessZero() || num1 instanceof NegativeInfinity ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) {
			if ( num2.lessZero() || num2 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof NegativeInfinity ) {
			if ( num1.lessZero() || num1 instanceof NegativeInfinity ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.mult( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.mult( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num div( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.div( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.div( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
		}

		if( num1 instanceof NaN || num2 instanceof NaN 
				|| ( ( num1 instanceof PositiveInfinity || num1 instanceof NegativeInfinity ) && ( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) ) ) { // two infinities in the division
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity ) { // positive infinity divided by some finite value
			if( num2.lessZero() ) {
				return new NegativeInfinity();
			} else {
				return new PositiveInfinity();
			}
		}
		if( num1 instanceof NegativeInfinity ) { // negative infinity divided by some finite value 
			if( num2.lessZero() ) {
				return new PositiveInfinity();
			} else {
				return new NegativeInfinity();
			}
		}
		if( num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity ) { // finite value divided by infinity
			return NumFactory.createZero();
		}

		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
		        if ( ((RationalBigInteger)num2).isZero() ) {
		        	return new PositiveInfinity();
		       	} else {
					return RationalBigInteger.div( (RationalBigInteger)num1, (RationalBigInteger)num2 );     		
		       	}
			case RATIONAL_INTEGER:
		        if ( ((RationalInteger)num2).isZero() ) {
		        	return new PositiveInfinity();
		       	} else {
		       		return RationalInteger.div( (RationalInteger)num1, (RationalInteger)num2 );        		
		       	}
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num abs( Num num ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.abs( (RealDoublePrecision)num );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.abs( (RealSinglePrecision)num );
		}
		
		if( num instanceof NaN ) {
			return new NaN();
		}
		if( num instanceof PositiveInfinity || num instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.abs( (RationalBigInteger)num );
			case RATIONAL_INTEGER:
				return RationalInteger.abs( (RationalInteger)num );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num diff( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.diff( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.diff( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
		}
		
		if( num1 instanceof NaN || num2 instanceof NaN ) { 
			return new NaN();
		}
		if( num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity 
				|| num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity ) {
			return new PositiveInfinity();
		}
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.diff( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.diff( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num max( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.max( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.max( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
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
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.max( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.max( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num min( Num num1, Num num2 ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealDoublePrecision.min( (RealDoublePrecision)num1, (RealDoublePrecision)num2 );
			case REAL_SINGLE_PRECISION:
				return RealSinglePrecision.min( (RealSinglePrecision)num1, (RealSinglePrecision)num2 );
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
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.min( (RationalBigInteger)num1, (RationalBigInteger)num2 );
			case RATIONAL_INTEGER:
				return RationalInteger.min( (RationalInteger)num1, (RationalInteger)num2 );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}

	public static Num negate( Num num ) {
		switch ( CalculatorConfig.NUM_CLASS ) {
			case REAL_DOUBLE_PRECISION:
				return RealSinglePrecision.negate( (RealSinglePrecision)num );
			case REAL_SINGLE_PRECISION:
				return RealDoublePrecision.negate( (RealDoublePrecision)num );
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
		
		switch ( CalculatorConfig.NUM_CLASS ) {
			case RATIONAL_BIGINTEGER:
				return RationalBigInteger.negate( (RationalBigInteger)num );
			case RATIONAL_INTEGER:
				return RationalInteger.negate( (RationalInteger)num );
			default:
				throw new RuntimeException( "Undefined number representation" );
		}
	}
}
