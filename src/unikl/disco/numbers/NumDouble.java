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

import unikl.disco.nc.CalculatorConfig.NumClass;

/**
 * Wrapper class around double;
 *
 * @author Steffen Bondorf
 *
 */
public class NumDouble extends Num {
	protected final NumClass num_class = NumClass.DOUBLE;
	
	private double value = 0.0;
	
	private static final double EPSILON = Double.parseDouble("5E-10");

	public static final Num POSITIVE_INFINITY = createPositiveInfinity();
	public static final Num NEGATIVE_INFINITY = createNegativeInfinity();
	public static final Num NaN = createNaN();
	public static final Num ZERO = createZero();
	
	private NumDouble() {}
	
	protected NumDouble( double value ) {
		this.value = value;
		checkInftyNaN();
	}
	
	protected NumDouble( String num_str ) throws Exception {
		if( num_str.equals( "Infinity" ) ) {
			value = Double.POSITIVE_INFINITY;
		} else {
			value = Num.parse( num_str ).doubleValue();
			checkInftyNaN();
		}
	}
	
	protected NumDouble( int num ) {
		value = (double)num;
		checkInftyNaN();
	}
	
	protected NumDouble( int num, int den ) {
		value = ((double)num) / ((double)den);
		checkInftyNaN();
	}
	
	protected NumDouble( NumDouble num ) {
		value = num.value;
	}

	protected NumDouble( SpecialValue indicator ) {
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
	
	private void checkInftyNaN() {
		// See Java documentation on parsing Doubles. There are rounding errors up to Math.ulp(Double.MAX_VALUE)/2.
		if( value >= Double.MAX_VALUE ){
			isNaN = false;
			isPosInfty = true;
			isNegInfty = false;
			return;
		}
		if ( Math.abs( value - Double.NEGATIVE_INFINITY ) < EPSILON ) {
			isNaN = false;
			isPosInfty = false;
			isNegInfty = true;
			return;
		}
		if( Double.isNaN( value ) ){
			isNaN = true;
			isPosInfty = false;
			isNegInfty = false;
			return;
		}
	}
	
	public static Num createPositiveInfinity() {
		NumDouble pos_infty = new NumDouble();
		pos_infty.instantiatePositiveInfinity();
		return pos_infty;
	}
	
	private void instantiatePositiveInfinity() {
		value = Double.POSITIVE_INFINITY;
		isNaN = false;
		isPosInfty = true;
		isNegInfty = false;
	}

	public static Num createNegativeInfinity() {
		NumDouble neg_infty = new NumDouble();
		neg_infty.instantiateNegativeInfinity();
		return neg_infty;
	}

	private void instantiateNegativeInfinity() {
		value = Double.NEGATIVE_INFINITY;
		isNaN = false;
		isPosInfty = false;
		isNegInfty = true;
	}
	
	public static Num createNaN() {
		NumDouble nan = new NumDouble();
		nan.instantiateNaN();
		return nan;
	}
	
	private void instantiateNaN() {
		value = Double.NaN;
		isNaN = true;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	public static Num createZero() {
		NumDouble zero = new NumDouble();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = 0.0;
		isNaN = false;
		isPosInfty = false;
		isNegInfty = false;
	}
	
	protected static Num createEpsilon() {
        return new NumDouble( EPSILON );
	}
	
	protected static Num add( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( num1.doubleValue() + num2.doubleValue() );
	}
	
	@Override
	public void add( Num num2 ) {
		// These are not necessary as they were used in other places to emulate double behavior
//		if( this.isNaN || num2.isNaN ) {
//			instantiateNaN();
//			return;
//		}
//		if( this.isPosInfty || num2.isPosInfty ) {
//			instantiatePositiveInfinity();
//			return;
//		}
//		if( this.isNegInfty || num2.isNegInfty ) {
//			instantiateNegativeInfinity();
//			return;
//		}
		
		value += num2.doubleValue();
	}
	
	protected static Num sub( NumDouble num1, NumDouble num2 ) {
		double result = num1.doubleValue() - num2.doubleValue();
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new NumDouble( result );
	}
	
	@Override
	public void sub( Num num2 ) {
//		if( this.isNaN || num2.isNaN ) {
//			instantiateNaN();
//			return;
//		}
//		if( this.isPosInfty || num2.isPosInfty ) {
//			instantiatePositiveInfinity();
//			return;
//		}
//		if( this.isNegInfty || num2.isNegInfty ) {
//			instantiateNegativeInfinity();
//			return;
//		}
		
		double result = this.value - num2.doubleValue();
		if( Math.abs( result ) <= EPSILON ) {
			this.value = 0;
		}
	}
	
	protected static Num mult( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( num1.doubleValue() * num2.doubleValue() );
	}
	
	@Override
	public void mult( Num num2 ) {
//		if( this.isNaN || num2.isNaN ) {
//			instantiateNaN();
//			return;
//		}
//		if( this.isPosInfty || num2.isPosInfty ) {
//			instantiatePositiveInfinity();
//			return;
//		}
//		if( this.isNegInfty || num2.isNegInfty ) {
//			instantiateNegativeInfinity();
//			return;
//		}
		
		this.value *= num2.doubleValue();
	}

	protected static Num div( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( num1.doubleValue() / num2.doubleValue() );
	}
	
	@Override
	public void div( Num num2 ) {
//		if( this.isNaN || num2.isNaN ) {
//			instantiateNaN();
//			return;
//		}
//		if( this.isPosInfty ) {
//			instantiatePositiveInfinity();
//			return;
//		}
//		if( num2.isPosInfty || num2.isNegInfty ) {
//			instantiateZero();
//			return;
//		}
//		if( this.isNegInfty ) {
//			instantiateNegativeInfinity();
//			return;
//		}
		
		this.value /= num2.doubleValue();
	}

	protected static Num diff( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( Math.max( num1.doubleValue(), num2.doubleValue() )
										- Math.min( num1.doubleValue(), num2.doubleValue() ) );	
	}

	protected static Num max( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( Math.max( num1.doubleValue(), num2.doubleValue() ) );
	}

	protected static Num min( NumDouble num1, NumDouble num2 ) {
		return new NumDouble( Math.min( num1.doubleValue(), num2.doubleValue() ) );
	}
	
	protected static Num abs( NumDouble num ) {
		return new NumDouble( Math.abs( num.doubleValue() ) );
	}

	protected static Num negate( NumDouble num ) {
	    return new NumDouble( num.doubleValue() * -1 );
	}

	public boolean greater( Num num2 ) {
		if( this.isNaN || num2.isNaN ){
			return false;
		}
		
		if( num2.isPosInfty ){
			return false;
		}
		if( this.isPosInfty ){
			return true;
		}
		
		if( this.isNegInfty ){
			return false;
		}
		if( num2.isNegInfty ){
			return true;
		}
		
		return value > num2.doubleValue();
	}

	public boolean ge( Num num2 ) {
		if( this.isNaN || num2.isNaN ){
			return false;
		}
		
		if( this.isPosInfty ){
			return true;
		}
		if( num2.isPosInfty ){
			return false;
		}

		if( num2.isNegInfty ){
			return true;
		}
		if( this.isNegInfty ){
			return false;
		}
		
		return value >= num2.doubleValue();
	}

	public boolean less( Num num2 ) {
		if( this.isNaN || num2.isNaN ){
			return false;
		}

		if( this.isPosInfty ){
			return false;
		}
		if( num2.isPosInfty ){
			return true;
		}
		
		if( num2.isNegInfty ){
			return false;
		}
		if( this.isNegInfty ){
			return true;
		}
		
		return value < num2.doubleValue();
	}

	public boolean le( Num num2 ) {
		if( this.isNaN || num2.isNaN ){
			return false;
		}

		if( num2.isPosInfty ){
			return true;
		}
		if( this.isPosInfty ){
			return false;
		}

		if( this.isNegInfty ){
			return true;
		}
		if( num2.isNegInfty ){
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
		if ( this.isNaN ) {
    		return createNaN();
    	}
    	if ( this.isPosInfty ) {
    		return createPositiveInfinity();
    	}
    	if ( this.isNegInfty ) {
			return createNegativeInfinity();
		}
    	
		return new NumDouble( value );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( Double.isNaN( num2 ) ){
			return this.isNaN;
		}
		if( num2 == Double.POSITIVE_INFINITY ){
			return this.isPosInfty;
		}
		if( num2 == Double.NEGATIVE_INFINITY ){
			return this.isNegInfty;
		}
		
		if( Math.abs( value - num2 ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	protected boolean equals( NumDouble num2 ) {
		return equals( num2.value );
	}

	@Override
	public boolean equals( Object num2 ) {
		if( num2 == null ){
			return true;
		}
		
		NumDouble num2_Num;
		try {
			num2_Num = (NumDouble) num2;
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
		if ( this.isNaN ) {
    		return "NaN";
    	}
    	if ( this.isPosInfty ) {
    		return "Infinity";
    	}
    	if ( this.isNegInfty ) {
			return "-Infinity";
		}
    	
		return Double.toString( value );
	}
}
