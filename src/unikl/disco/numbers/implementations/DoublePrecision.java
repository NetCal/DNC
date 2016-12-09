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
	private boolean isNaN, isPosInfty, isNegInfty;
	
	private double value = 0.0;
	
	private static final double EPSILON = Double.parseDouble("5E-10");

	public static final Num POSITIVE_INFINITY = createPositiveInfinity();
	public static final Num NEGATIVE_INFINITY = createNegativeInfinity();
	public static final Num ZERO = createZero();
	
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
		DoublePrecision pos_infty = new DoublePrecision();
		pos_infty.instantiatePositiveInfinity();
		return pos_infty;
	}
	
	private void instantiatePositiveInfinity() {
		value = Double.POSITIVE_INFINITY;
		isPosInfty = true;
		isNegInfty = false;
	}

	public static Num createNegativeInfinity() {
		DoublePrecision neg_infty = new DoublePrecision();
		neg_infty.instantiateNegativeInfinity();
		return neg_infty;
	}

	private void instantiateNegativeInfinity() {
		value = Double.NEGATIVE_INFINITY;
		isPosInfty = false;
		isNegInfty = true;
	}
	
	public static Num createZero() {
		DoublePrecision zero = new DoublePrecision();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = 0.0;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	public static Num createEpsilon() {
        return new DoublePrecision( EPSILON );
	}
	
	public static Num add( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value + num2.value );
	}
	
	@Override
	public void add( Num num2 ) {
		value += num2.doubleValue();
	}
	
	public static Num sub( DoublePrecision num1, DoublePrecision num2 ) {
		double result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new DoublePrecision( result );
	}
	
	@Override
	public void sub( Num num2 ) {
		double result = this.value - num2.doubleValue();
		if( Math.abs( result ) <= EPSILON ) {
			this.value = 0;
		}
	}
	
	public static Num mult( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value * num2.value );
	}
	
	@Override
	public void mult( Num num2 ) {
		this.value *= num2.doubleValue();
	}

	public static Num div( DoublePrecision num1, DoublePrecision num2 ) {
		return new DoublePrecision( num1.value / num2.value );
	}
	
	@Override
	public void div( Num num2 ) {
		this.value /= num2.doubleValue();
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
	    return value;
	}

	@Override
	public Num copy() {
    	if ( isPosInfty ) {
    		return createPositiveInfinity();
    	}
    	if ( isNegInfty ) {
			return createNegativeInfinity();
		}
    	
		return new DoublePrecision( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( num2 == Double.POSITIVE_INFINITY ){
			return isPosInfty;
		}
		if( num2 == Double.NEGATIVE_INFINITY ){
			return isNegInfty;
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
		if( obj == null ){
			return true; // TODO
		}
		
		DoublePrecision num2_Num;
		try {
			num2_Num = (DoublePrecision) obj;
			return equals( num2_Num.value );
		} catch( ClassCastException e ) {
			return false;
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
