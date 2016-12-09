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
	private boolean isNaN, isPosInfty, isNegInfty;
	
	private float value = 0.0f;
	
	private static final float EPSILON = 0.00016f; // Maximum rounding error observed in the functional tests

	public static final Num POSITIVE_INFINITY = createPositiveInfinity();
	public static final Num NEGATIVE_INFINITY = createNegativeInfinity();
	public static final Num ZERO = createZero();
	
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
	
	public boolean isNaN() {
		return isNaN;
	}
	
	public boolean isPosInfty() {
		return isPosInfty;
	}
	
	public boolean isNegInfty() {
		return isNegInfty;
	}
	
	public static Num createPositiveInfinity() {
		SinglePrecision pos_infty = new SinglePrecision();
		pos_infty.instantiatePositiveInfinity();
		return pos_infty;
	}
	
	private void instantiatePositiveInfinity() {
		value = Float.POSITIVE_INFINITY;
		isPosInfty = true;
		isNegInfty = false;
	}

	public static Num createNegativeInfinity() {
		SinglePrecision neg_infty = new SinglePrecision();
		neg_infty.instantiateNegativeInfinity();
		return neg_infty;
	}

	private void instantiateNegativeInfinity() {
		value = Float.NEGATIVE_INFINITY;
		isPosInfty = false;
		isNegInfty = true;
	}
	
	public static Num createZero() {
		SinglePrecision zero = new SinglePrecision();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = 0.0f;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	public static Num createEpsilon() {
        return new SinglePrecision( EPSILON );
	}
	
	public static Num add( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value + num2.value );
	}
	
	@Override
	public void add( Num num2 ) {
		value += num2.doubleValue();
	}
	
	public static Num sub( SinglePrecision num1, SinglePrecision num2 ) {
		float result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new SinglePrecision( result );
	}
	
	@Override
	public void sub( Num num2 ) {
		float result = this.value - (new Float( num2.doubleValue() )).floatValue();
		if( Math.abs( result ) <= EPSILON ) {
			this.value = 0;
		}
	}
	
	public static Num mult( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value * num2.value );
	}
	
	@Override
	public void mult( Num num2 ) {
		this.value *= num2.doubleValue();
	}

	public static Num div( SinglePrecision num1, SinglePrecision num2 ) {
		return new SinglePrecision( num1.value / num2.value );
	}
	
	@Override
	public void div( Num num2 ) {
		this.value /= num2.doubleValue();
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
		if( num2.isPosInfty() ){
			return false;
		}
		if( isPosInfty ){
			return true;
		}
		
		if( isNegInfty ){
			return false;
		}
		if( num2.isNegInfty() ){
			return true;
		}
		
		return value > num2.doubleValue();
	}

	public boolean ge( Num num2 ) {
		if( isPosInfty ){
			return true;
		}
		if( num2.isPosInfty() ){
			return false;
		}

		if( num2.isNegInfty() ){
			return true;
		}
		if( isNegInfty ){
			return false;
		}
		
		return value >= num2.doubleValue();
	}

	public boolean less( Num num2 ) {
		if( isPosInfty ){
			return false;
		}
		if( num2.isPosInfty() ){
			return true;
		}
		
		if( num2.isNegInfty() ){
			return false;
		}
		if( isNegInfty ){
			return true;
		}
		
		return value < num2.doubleValue();
	}

	public boolean le( Num num2 ) {
		if( num2.isPosInfty() ){
			return true;
		}
		if( isPosInfty ){
			return false;
		}

		if( isNegInfty ){
			return true;
		}
		if( num2.isNegInfty() ){
			return false;
		}
		
		return value <= num2.doubleValue();
	}
	
	@Override
	public double doubleValue() {
	    return new Double( value );
	}

	@Override
	public Num copy() {
    	if ( isPosInfty ) {
    		return createPositiveInfinity();
    	}
    	if ( isNegInfty ) {
			return createNegativeInfinity();
		}
    	
		return new SinglePrecision( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( num2 == Float.POSITIVE_INFINITY ){
			return isPosInfty;
		}
		if( num2 == Float.NEGATIVE_INFINITY ){
			return isNegInfty;
		}
		
		if( Math.abs( (new Double( value )).doubleValue() - num2 ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals( SinglePrecision num2 ) {
		if( num2.isPosInfty ){
			return isPosInfty;
		}
		if( num2.isNegInfty ){
			return isNegInfty;
		}
		
		if( Math.abs( value - num2.value ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null ){
			return true;
		}
		
		SinglePrecision num2_Num;
		try {
			num2_Num = (SinglePrecision) obj;
			return equals( num2_Num.value );
		} catch( ClassCastException e ) {
			return false;
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
