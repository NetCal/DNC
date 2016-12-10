/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2014 - 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
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

import unikl.disco.numbers.Num;

/**
 * Wrapper class around float;
 *
 * @author Steffen Bondorf
 *
 */
public class RealSinglePrecision implements Num {
	private float value = 0.0f;
	
	private static final float EPSILON = 0.00016f; // Maximum rounding error observed in the functional tests
	
	@SuppressWarnings("unused")
	private RealSinglePrecision() {}
	
	public RealSinglePrecision( double value ) {
		this.value = new Float( value ).floatValue();
	}
	
	public RealSinglePrecision( int num ) {
		value = (float)num;
	}
	
	public RealSinglePrecision( int num, int den ) {
		value = ((float)num) / ((float)den);
	}
	
	public RealSinglePrecision( RealSinglePrecision num ) {
		value = num.value;
	}
	
	public boolean isZero() {
		return value == 0.0f;
	}
	
	public static Num createEpsilon() {
        return new RealSinglePrecision( EPSILON );
	}
	
	public static Num add( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( num1.value + num2.value );
	}
	
	public static Num sub( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		float result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new RealSinglePrecision( result );
	}
	
	public static Num mult( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( num1.value * num2.value );
	}

	public static Num div( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( num1.value / num2.value );
	}

	public static Num diff( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( Math.max( num1.value, num2.value ) );
	}

	public static Num min( RealSinglePrecision num1, RealSinglePrecision num2 ) {
		return new RealSinglePrecision( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( RealSinglePrecision num ) {
		return new RealSinglePrecision( Math.abs( num.value ) );
	}

	public static Num negate( RealSinglePrecision num ) {
	    return new RealSinglePrecision( num.value * -1 );
	}

	public boolean greater( Num num2 ) {
		return value > num2.doubleValue();
	}

	public boolean geq( Num num2 ) {
		return value >= num2.doubleValue();
	}

	public boolean less( Num num2 ) {
		return value < num2.doubleValue();
	}

	public boolean leq( Num num2 ) {
		return value <= num2.doubleValue();
	}
	
	@Override
	public double doubleValue() {
	    return new Double( value );
	}

	@Override
	public Num copy() {
		return new RealSinglePrecision( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( ( this.value == Float.POSITIVE_INFINITY && num2 == Double.POSITIVE_INFINITY ) 
				|| ( this.value == Float.NEGATIVE_INFINITY && num2 == Double.NEGATIVE_INFINITY ) ) {
			return true;
		}
		
		if( Math.abs( (new Double( value )).doubleValue() - num2 ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals( RealSinglePrecision num2 ) {
		if( ( this.value == Float.POSITIVE_INFINITY && num2.value == Float.POSITIVE_INFINITY ) 
				|| ( this.value == Float.NEGATIVE_INFINITY && num2.value == Float.NEGATIVE_INFINITY ) ) {
			return true;
		}
		
		if( Math.abs( value - num2.value ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RealSinglePrecision ) ) {
			return false;
		} else {
			return equals( ((RealSinglePrecision) obj).value );
		}
	}
	
	@Override
	public int hashCode() {
		return Float.hashCode( value );
	}
	
	@Override
	public String toString(){
		return Float.toString( value );
	}
}
