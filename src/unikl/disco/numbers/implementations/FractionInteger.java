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
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

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
	private Fraction value = new Fraction( 0.0 );
	
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final Fraction EPSILON = new Fraction( 1, 1000000 ); 
	
	private FractionInteger(){}
	
	public FractionInteger( double value ) {
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
	
	public boolean isZero() {
		return value.getNumerator() == 0;
	}
	
	public static FractionInteger createZero() {
		FractionInteger zero = new FractionInteger();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = new Fraction( 0.0 );
	}
	
	public static FractionInteger createEpsilon() {
        return new FractionInteger( EPSILON );
	}
	
	public static FractionInteger add( FractionInteger num1, FractionInteger num2 ) {
		// May still throw MathArithmeticException due to integer overflow
        return new FractionInteger( num1.value.add( num2.value ) );
	}
	
	public static FractionInteger sub( FractionInteger num1, FractionInteger num2 ) {
        // May still throw MathArithmeticException due to integer overflow
        return new FractionInteger( num1.value.subtract( num2.value ) );
	}
	
	public static FractionInteger mult( FractionInteger num1, FractionInteger num2 ) {
        // May throw MathArithmeticException due to integer overflow
       	return new FractionInteger( num1.value.multiply( num2.value ) );
	}

	public static FractionInteger div( FractionInteger num1, FractionInteger num2 ) {
		return new FractionInteger( num1.value.divide( num2.value ) );        		
	}

	public static FractionInteger diff( FractionInteger num1, FractionInteger num2 ) {
		return sub( max( num1, num2 ), min( num1, num2 ) );	
	}

	public static FractionInteger max( FractionInteger num1, FractionInteger num2 ) {
		if( num1.value.compareTo( num2.value ) >= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}

	public static FractionInteger min( FractionInteger num1, FractionInteger num2 ) {
		if( num1.value.compareTo( num2.value ) <= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}
	
	public static FractionInteger abs( FractionInteger num ) {
    	return new FractionInteger( num.value.abs() );
	}

	public static FractionInteger negate( FractionInteger num ) {
    	return new FractionInteger( num.value.negate() );
	}

	public boolean greater( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return false;
		}
		if( num2 instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean ge( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return false;
		}
		if( num2 instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) >= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean less( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return true;
		}
		if( num2 instanceof NegativeInfinity ){
			return false;
		}
		
		if( this.value.compareTo( ((FractionInteger)num2).value ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean le( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return true;
		}
		if( num2 instanceof NegativeInfinity ){
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
	    return value.doubleValue();
	}

	@Override
	public Num copy() {
		return new FractionInteger( this.value.getNumerator(), this.value.getDenominator() );
	}
	
	@Override
	public boolean equals( double num2 ) {
		return equals( new FractionInteger( num2 ) );
	}

	public boolean equals( FractionInteger num2 ) {
		if( this.value.compareTo( num2.value ) == 0 ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof FractionInteger ) ) {
			return false;
		} else {
			return equals( ((FractionInteger) obj) );
		}
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	@Override
	public String toString(){
		return value.toString();
	}
}
