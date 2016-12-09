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
public class SinglePrecision implements Num {
	private float value = 0.0f;
	
	private static final float EPSILON = 0.00016f; // Maximum rounding error observed in the functional tests
	
	@SuppressWarnings("unused")
	private SinglePrecision() {}
	
	public SinglePrecision( double value ) {
		this.value = new Float( value ).floatValue();
	}
	
	public SinglePrecision( int num ) {
		value = (float)num;
	}
	
	public SinglePrecision( int num, int den ) {
		value = ((float)num) / ((float)den);
	}
	
	public SinglePrecision( SinglePrecision num ) {
		value = num.value;
	}
	
	public static Num createEpsilon() {
        return new SinglePrecision( EPSILON );
	}
	
	public static Num add( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value + num2.value );
	}
	
	public static Num sub( SinglePrecision num1, SinglePrecision num2 ) {
		float result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new SinglePrecision( result );
	}
	
	public static Num mult( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value * num2.value );
	}

	public static Num div( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value / num2.value );
	}

	public static Num diff( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( Math.max( num1.value, num2.value ) );
	}

	public static Num min( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( SinglePrecision num ) {
		return new SinglePrecision( Math.abs( num.value ) );
	}

	public static Num negate( SinglePrecision num ) {
	    return new SinglePrecision( num.value * -1 );
	}

	public boolean greater( Num num2 ) {
		return value > num2.doubleValue();
	}

	public boolean ge( Num num2 ) {
		return value >= num2.doubleValue();
	}

	public boolean less( Num num2 ) {
		return value < num2.doubleValue();
	}

	public boolean le( Num num2 ) {
		return value <= num2.doubleValue();
	}
	
	@Override
	public double doubleValue() {
	    return new Double( value );
	}

	@Override
	public Num copy() {
		return new SinglePrecision( value );
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

	public boolean equals( SinglePrecision num2 ) {
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
				|| !( obj instanceof SinglePrecision ) ) {
			return false;
		} else {
			return equals( ((SinglePrecision) obj).value );
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
