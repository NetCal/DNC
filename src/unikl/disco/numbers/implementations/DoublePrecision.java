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
 * Wrapper class around double;
 *
 * @author Steffen Bondorf
 *
 */
public class DoublePrecision implements Num {
	private double value = 0.0;
	
	private static final double EPSILON = Double.parseDouble("5E-10");
	
	@SuppressWarnings("unused")
	private DoublePrecision() {}
	
	public DoublePrecision( double value ) {
		this.value = value;
	}
	
	public DoublePrecision( int num ) {
		value = (double)num;
	}
	
	public DoublePrecision( int num, int den ) {
		value = ((double)num) / ((double)den);
	}
	
	public DoublePrecision( DoublePrecision num ) {
		value = num.value;
	}
	
	public static Num createEpsilon() {
        return new DoublePrecision( EPSILON );
	}
	
	public static Num add( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value + num2.value );
	}
	
	public static Num sub( DoublePrecision num1, DoublePrecision num2 ) {
		double result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new DoublePrecision( result );
	}
	
	public static Num mult( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value * num2.value );
	}

	public static Num div( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value / num2.value );
	}

	public static Num diff( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( Math.max( num1.value, num2.value ) );
	}

	public static Num min( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( DoublePrecision num ) {
		return new DoublePrecision( Math.abs( num.value ) );
	}

	public static Num negate( DoublePrecision num ) {
	    return new DoublePrecision( num.value * -1 );
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
	    return value;
	}

	@Override
	public Num copy() {
		return new DoublePrecision( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( ( this.value == Double.POSITIVE_INFINITY && num2 == Double.POSITIVE_INFINITY ) 
				|| ( this.value == Double.NEGATIVE_INFINITY && num2 == Double.NEGATIVE_INFINITY ) ) {
			return true;
		}
		
		if( Math.abs( value - num2 ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals( DoublePrecision num2 ) {
		return equals( num2.value );
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof DoublePrecision ) ) {
			return false;
		} else {
			return equals( ((DoublePrecision) obj).value );
		}
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode( value );
	}
	
	@Override
	public String toString(){
		return Double.toString( value );
	}
}
