/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2013 - 2016 Steffen Bondorf
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

import org.apache.commons.math3.fraction.Fraction;

import unikl.disco.numbers.Num;

/**
 * Wrapper class around org.apache.commons.math3.fraction.Fraction
 * introducing special values like positive / negative infinity and NaN
 * as well as operators like min, max, ==, &gt;, &gt;=, &lt;, and &lt;= that are
 * not part of Fraction but needed by the network calculator.
 * 
 * For the ease of converting from the primitive data type double
 * to Fraction objects, copy by value semantic are is applied. 
 * 
 * @author Steffen Bondorf
 *
 */
public class FractionInteger implements Num {
	private boolean isNaN, isPosInfty, isNegInfty; 
	
	private Fraction value = new Fraction( 0.0 );
	
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final Fraction EPSILON = new Fraction( 1, 1000000 ); 
	
	// Fraction is based in Integer and thus there's no infinity (and Fraction is prone to overflows as well)
	public static final FractionInteger POSITIVE_INFINITY = createPositiveInfinity();
	public static final FractionInteger NEGATIVE_INFINITY = createNegativeInfinity();
	public static final FractionInteger ZERO = createZero();
	
	private FractionInteger(){}
	
	public FractionInteger( double value ) {
		if( value == Double.POSITIVE_INFINITY ) {
			instantiatePositiveInfinity();
			return;
		}
		if( value == Double.NEGATIVE_INFINITY ) {
			instantiateNegativeInfinity();
			return;
		}
		
		this.value = new Fraction( value );
	}
	
	public FractionInteger( int num ) {
		value = new Fraction( num );
	}
	
	public FractionInteger( int num, int den ) {
		value = new Fraction( num, den );
	}
	
	public FractionInteger( FractionInteger num ) {
		value = new Fraction( num.value.getNumerator(), num.value.getDenominator() );
	}
	
	private FractionInteger( Fraction frac ) {
		value = new Fraction( frac.getNumerator(), frac.getDenominator() );
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
	
	public static FractionInteger createPositiveInfinity() {
		FractionInteger pos_infty = new FractionInteger();
		pos_infty.instantiatePositiveInfinity();
		return pos_infty;
	}
	
	private void instantiatePositiveInfinity() {
		value = new Fraction( Integer.MAX_VALUE );
		isPosInfty = true;
		isNegInfty = false;
	}

	public static FractionInteger createNegativeInfinity() {
		FractionInteger neg_infty = new FractionInteger();
		neg_infty.instantiateNegativeInfinity();
		return neg_infty;
	}

	private void instantiateNegativeInfinity() {
		value = new Fraction( Integer.MIN_VALUE );
		isPosInfty = false;
		isNegInfty = true;
	}
	
	public static FractionInteger createZero() {
		FractionInteger zero = new FractionInteger();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = new Fraction( 0.0 );
		isPosInfty = false;
		isNegInfty = false;
	}
	
	public static FractionInteger createEpsilon() {
        return new FractionInteger( EPSILON );
	}
	
	// In order to simplify the transition from the primitive data type double to
	// a rational number object, these functions emulate copy by value for objects that
	// typically inhibit copy by reference
	public static FractionInteger add( FractionInteger num1, FractionInteger num2 ) {
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( num1.isPosInfty() || num2.isPosInfty() ) {
			return createPositiveInfinity();
		}
		if( num1.isNegInfty() || num2.isNegInfty() ) {
			return createNegativeInfinity();
		}
		
		// May still throw MathArithmeticException due to integer overflow
        return new FractionInteger( num1.value.add( num2.value ) );
	}
	
	@Override
	public void add( Num num2 ) {
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( isPosInfty || num2.isPosInfty() ) {
			instantiatePositiveInfinity();
			return;
		}
		if( isNegInfty || num2.isNegInfty() ) {
			instantiateNegativeInfinity();
			return;
		}
		
		this.value = this.value.add( (Fraction)((FractionInteger)num2).value );
	}
	
	public static FractionInteger sub( FractionInteger num1, FractionInteger num2 ) {
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( num1.isPosInfty() || num2.isPosInfty() ) {
			return createPositiveInfinity();
		}
		if( num1.isNegInfty() || num2.isNegInfty() ) {
			return createNegativeInfinity();
		}
		
        // May still throw MathArithmeticException due to integer overflow
        return new FractionInteger( num1.value.subtract( num2.value ) );
	}

	@Override
	public void sub( Num num2 ) {
		// Prevent overflow exception when adding integer based number representations like Fraction
		if( isPosInfty || num2.isPosInfty() ) {
			instantiatePositiveInfinity();
			return;
		}
		if( isNegInfty || num2.isNegInfty() ) {
			instantiateNegativeInfinity();
			return;
		}
		
        // May throw MathArithmeticException due to integer overflow
		this.value =  this.value.subtract( (Fraction)((FractionInteger)num2).value );
	}
	
	public static FractionInteger mult( FractionInteger num1, FractionInteger num2 ) {
		if( num1.isPosInfty() || num2.isPosInfty() ) {
			return createPositiveInfinity();
		}
		if( num1.isNegInfty() || num2.isNegInfty() ) {
			return createNegativeInfinity();
		}
		
        // May throw MathArithmeticException due to integer overflow
       	return new FractionInteger( num1.value.multiply( num2.value ) );
	}

	@Override
	public void mult( Num num2 ) {
		if( isPosInfty || num2.isPosInfty() ) {
			instantiatePositiveInfinity();
			return;
		}
		if( isNegInfty || num2.isNegInfty() ) {
			instantiateNegativeInfinity();
			return;
		}
		
        // May throw MathArithmeticException due to integer overflow
		this.value =  this.value.multiply( (Fraction)((FractionInteger)num2).value );
	}

	public static FractionInteger div( FractionInteger num1, FractionInteger num2 ) {
		// Integer based number representations use Integer.MAX_VALUE to signal infinity so special treatment is necessary when dividing
		if( num1.isPosInfty() ) {
			return createPositiveInfinity();
		}
		if( num2.isPosInfty() ) {
			return createZero();
		}
		if( num1.isNegInfty() ) {
			return createNegativeInfinity();
		}
		if( num2.isNegInfty() ) {
			return createZero();
		}
		
        if ( num2.value.getNumerator() == 0 ) {
        	return createPositiveInfinity();
       	} else {
           	return new FractionInteger( num1.value.divide( num2.value ) );        		
       	}
	}

	@Override
	public void div( Num num2 ) {
		// Integer based number representations use Integer.MAX_VALUE to signal infinity so special treatment is necessary when dividing
		if( isPosInfty || ((Fraction)((FractionInteger)num2).value).getNumerator() == 0 ) {
			instantiatePositiveInfinity();
			return;
		}
		if( num2.isPosInfty() || num2.isNegInfty() ) {
			instantiateZero();
			return;
		}
		if( isNegInfty ) {
			instantiateNegativeInfinity();
			return;
		}
		
       	this.value =  this.value.divide( (Fraction)((FractionInteger)num2).value );        		
	}

	public static FractionInteger diff( FractionInteger num1, FractionInteger num2 ) {
		if( num1.isPosInfty() || num1.isNegInfty() 
				 || num2.isPosInfty() || num2.isNegInfty() ) {
			return createPositiveInfinity();
		}
		
		return sub( max( num1, num2 ), min( num1, num2 ) );	
	}

	public static FractionInteger max( FractionInteger num1, FractionInteger num2 ) {
		if( num1.isPosInfty() ) {
			return num1;
		}
		if( num1.isNegInfty() ) {
			return num2;
		}
		
		if( num1.value.compareTo( num2.value ) >= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}

	public static FractionInteger min( FractionInteger num1, FractionInteger num2 ) {
		if( num1.isPosInfty() ) {
			return num2;
		}
		if( num1.isNegInfty() ) {
			return num1;
		}
		
		if( num1.value.compareTo( num2.value ) <= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}
	
	public static FractionInteger abs( FractionInteger num ) {
    	if ( num.isPosInfty() ) {
    		return createPositiveInfinity();
    	}
    	if ( num.isNegInfty() ) {
			return createNegativeInfinity();
		}
	    
    	return new FractionInteger( num.value.abs() );
	}

	public static FractionInteger negate( FractionInteger num ) {
    	if ( num.isPosInfty() ) {
    		return createNegativeInfinity();
    	}
    	if ( num.isNegInfty() ) {
			return createPositiveInfinity();
		}
    	
    	return new FractionInteger( num.value.negate() );
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
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) > 0 ) {
			return true;
		} else {
			return false;
		}
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
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) >= 0 ) {
			return true;
		} else {
			return false;
		}
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
			
		if( this.value.compareTo( ((FractionInteger)num2).value ) < 0 ) {
			return true;
		} else {
			return false;
		}
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
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) <= 0 ) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public double doubleValue() {
    	if ( isPosInfty ) {
    		return Double.POSITIVE_INFINITY;
    	}
    	if ( isNegInfty ) {
			return Double.NEGATIVE_INFINITY;
		}
    	
	    return value.doubleValue();
	}

	@Override
	public Num copy() {
    	if ( isPosInfty ) {
    		return createPositiveInfinity();
    	}
    	if ( isNegInfty ) {
			return createNegativeInfinity();
		}

		return new FractionInteger( this.value.getNumerator(), this.value.getDenominator() );
	}
	
	@Override
	public boolean equals( double num2 ) {
		if( num2 == Double.POSITIVE_INFINITY ){
			return isPosInfty;
		}
		if( num2 == Double.NEGATIVE_INFINITY ){
			return isNegInfty;
		}
		
		return equals( new FractionInteger( num2 ) );
	}

	public boolean equals( FractionInteger num2 ) {
		if( isPosInfty & num2.isPosInfty() ){
			return true;
		}
		if( isNegInfty & num2.isNegInfty() ){
			return true;
		}
        
		if( this.value.compareTo( num2.value ) == 0 ) {
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
		
		FractionInteger num2_Num;
		try {
			num2_Num = (FractionInteger) obj;
			return equals( num2_Num );
		} catch( ClassCastException e ) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString(){
    	if ( isPosInfty ) {
    		return "Infinity";
    	}
    	if ( isNegInfty ) {
			return "-Infinity";
		}
    	
		return value.toString();
	}
}
