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
 
package unikl.disco.numbers;

import unikl.disco.numbers.NumFactory.SpecialValue;

/**
 * Wrapper class around float;
 *
 * @author Steffen Bondorf
 *
 */
public class NumSingle implements Num {
	private boolean isNaN, isPosInfty, isNegInfty;
	
	private float value = 0.0f;
	
	private static final float EPSILON = 0.00016f; // Maximum rounding error observed in the functional tests

	public static final Num POSITIVE_INFINITY = createPositiveInfinity();
	public static final Num NEGATIVE_INFINITY = createNegativeInfinity();
	public static final Num NaN = createNaN();
	public static final Num ZERO = createZero();
	
	private NumSingle() {}
	
	protected NumSingle( double value ) {
		this.value = new Float( value ).floatValue();
		checkInftyNaN();
	}
	
	protected NumSingle( String num_str ) throws Exception {
		if( num_str.equals( "Infinity" ) ) {
			value = Float.POSITIVE_INFINITY;
		} else {
			value = new Float(NumFactory.parse( num_str ).doubleValue());
			checkInftyNaN();
		}
	}
	
	protected NumSingle( int num ) {
		value = (float)num;
		checkInftyNaN();
	}
	
	protected NumSingle( int num, int den ) {
		value = ((float)num) / ((float)den);
		checkInftyNaN();
	}
	
	protected NumSingle( NumSingle num ) {
		value = num.value;
	}

	protected NumSingle( SpecialValue indicator ) {
		switch (indicator) {
			case POSITIVE_INFINITY:
				instantiatePositiveInfinity();
				break;
			case NEGATIVE_INFINITY:
				instantiateNegativeInfinity();
				break;
			case NaN:
				instantiateNaN();
				break;
			case ZERO:
				instantiateZero();
				break;
		}
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
	
	private void checkInftyNaN() {
		// See Java documentation on parsing Floats. There are rounding errors up to Math.ulp(Float.MAX_VALUE)/2.
		if( value >= Float.MAX_VALUE ){
			isNaN = false;
			isPosInfty = true;
			isNegInfty = false;
			return;
		}
		if ( Math.abs( value - Float.NEGATIVE_INFINITY ) < EPSILON ) {
			isNaN = false;
			isPosInfty = false;
			isNegInfty = true;
			return;
		}
		if( Float.isNaN( value ) ){
			isNaN = true;
			isPosInfty = false;
			isNegInfty = false;
			return;
		}
	}
	
	public static Num createPositiveInfinity() {
		NumSingle pos_infty = new NumSingle();
		pos_infty.instantiatePositiveInfinity();
		return pos_infty;
	}
	
	private void instantiatePositiveInfinity() {
		value = Float.POSITIVE_INFINITY;
		isNaN = false;
		isPosInfty = true;
		isNegInfty = false;
	}

	public static Num createNegativeInfinity() {
		NumSingle neg_infty = new NumSingle();
		neg_infty.instantiateNegativeInfinity();
		return neg_infty;
	}

	private void instantiateNegativeInfinity() {
		value = Float.NEGATIVE_INFINITY;
		isNaN = false;
		isPosInfty = false;
		isNegInfty = true;
	}
	
	public static Num createNaN() {
		NumSingle nan = new NumSingle();
		nan.instantiateNaN();
		return nan;
	}
	
	private void instantiateNaN() {
		value = Float.NaN;
		isNaN = true;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	public static Num createZero() {
		NumSingle zero = new NumSingle();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = 0.0f;
		isNaN = false;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	protected static Num createEpsilon() {
        return new NumSingle( EPSILON );
	}
	
	protected static Num add( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( num1.value + num2.value );
	}
	
	@Override
	public void add( Num num2 ) {
		value += num2.doubleValue();
	}
	
	protected static Num sub( NumSingle num1, NumSingle num2 ) {
		float result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new NumSingle( result );
	}
	
	@Override
	public void sub( Num num2 ) {
		float result = this.value - (new Float( num2.doubleValue() )).floatValue();
		if( Math.abs( result ) <= EPSILON ) {
			this.value = 0;
		}
	}
	
	protected static Num mult( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( num1.value * num2.value );
	}
	
	@Override
	public void mult( Num num2 ) {
		this.value *= num2.doubleValue();
	}

	protected static Num div( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( num1.value / num2.value );
	}
	
	@Override
	public void div( Num num2 ) {
		this.value /= num2.doubleValue();
	}

	protected static Num diff( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	protected static Num max( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( Math.max( num1.value, num2.value ) );
	}

	protected static Num min( NumSingle num1, NumSingle num2 ) {
		return new NumSingle( Math.min( num1.value, num2.value ) );
	}
	
	protected static Num abs( NumSingle num ) {
		return new NumSingle( Math.abs( num.value ) );
	}

	protected static Num negate( NumSingle num ) {
	    return new NumSingle( num.value * -1 );
	}

	public boolean greater( Num num2 ) {
		if( isNaN || num2.isNaN() ){
			return false;
		}
		
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
		if( isNaN || num2.isNaN() ){
			return false;
		}
		
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
		if( isNaN || num2.isNaN() ){
			return false;
		}

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
		if( isNaN || num2.isNaN() ){
			return false;
		}

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
		if ( isNaN ) {
    		return createNaN();
    	}
    	if ( isPosInfty ) {
    		return createPositiveInfinity();
    	}
    	if ( isNegInfty ) {
			return createNegativeInfinity();
		}
    	
		return new NumSingle( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( Double.isNaN( num2 ) ){
			return isNaN;
		}
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

	protected boolean equals( NumSingle num2 ) {
		if( num2.isNaN ){
			return isNaN;
		}
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
		
		NumSingle num2_Num;
		try {
			num2_Num = (NumSingle) obj;
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
