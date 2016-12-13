/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
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
public class RationalInteger implements Num {
	private Fraction value;
	
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final Fraction EPSILON = new Fraction( 1, 1000000 );
	private static final Fraction ZERO_FRACTION = new Fraction( 0 );
	
	private RationalInteger(){}
	
	public RationalInteger( double value ) {
		this.value = new Fraction( value );
	}
	
	public RationalInteger( int num ) {
		value = new Fraction( num );
	}
	
	public RationalInteger( int num, int den ) {
		value = new Fraction( num, den );
	}
	
	public RationalInteger( RationalInteger num ) {
		value = new Fraction( num.value.getNumerator(), num.value.getDenominator() );
	}
	
	private RationalInteger( Fraction frac ) {
		value = new Fraction( frac.getNumerator(), frac.getDenominator() );
	}
	
	public static RationalInteger createZero() {
		RationalInteger zero = new RationalInteger();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = new Fraction( 0.0 );
	}
	
	public static RationalInteger createEpsilon() {
        return new RationalInteger( EPSILON );
	}
	
	public static RationalInteger add( RationalInteger num1, RationalInteger num2 ) {
		// May still throw MathArithmeticException due to integer overflow
        return new RationalInteger( num1.value.add( num2.value ) );
	}
	
	public static RationalInteger sub( RationalInteger num1, RationalInteger num2 ) {
        // May still throw MathArithmeticException due to integer overflow
        return new RationalInteger( num1.value.subtract( num2.value ) );
	}
	
	public static RationalInteger mult( RationalInteger num1, RationalInteger num2 ) {
        // May throw MathArithmeticException due to integer overflow
       	return new RationalInteger( num1.value.multiply( num2.value ) );
	}

	public static RationalInteger div( RationalInteger num1, RationalInteger num2 ) {
		return new RationalInteger( num1.value.divide( num2.value ) );        		
	}

	public static RationalInteger diff( RationalInteger num1, RationalInteger num2 ) {
		return sub( max( num1, num2 ), min( num1, num2 ) );	
	}

	public static RationalInteger max( RationalInteger num1, RationalInteger num2 ) {
		if( num1.value.compareTo( num2.value ) >= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}

	public static RationalInteger min( RationalInteger num1, RationalInteger num2 ) {
		if( num1.value.compareTo( num2.value ) <= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}
	
	public static RationalInteger abs( RationalInteger num ) {
    	return new RationalInteger( num.value.abs() );
	}

	public static RationalInteger negate( RationalInteger num ) {
    	return new RationalInteger( num.value.negate() );
	}
	
	public boolean isZero() {
		return value.getNumerator() == 0;
	}

	public boolean greater( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return false;
		}
		if( num instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((RationalInteger)num).value ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean greaterZero() {
		if( this.value.compareTo( ZERO_FRACTION ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geq( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return false;
		}
		if( num instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((RationalInteger)num).value ) >= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geqZero() {
		if( this.value.compareTo( ZERO_FRACTION ) >= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean less( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return true;
		}
		if( num instanceof NegativeInfinity ){
			return false;
		}
		
		if( this.value.compareTo( ((RationalInteger)num).value ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean lessZero() {
		if( this.value.compareTo( ZERO_FRACTION ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leq( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return true;
		}
		if( num instanceof NegativeInfinity ){
			return false;
		}
		
		if( this.value.compareTo( ((RationalInteger)num).value ) <= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leqZero() {
		if( this.value.compareTo( ZERO_FRACTION ) <= 0 ) {
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
		return new RationalInteger( this.value.getNumerator(), this.value.getDenominator() );
	}
	
	@Override
	public boolean equals( double num ) {
		return equals( new RationalInteger( num ) );
	}

	public boolean equals( RationalInteger num ) {
		if( this.value.compareTo( num.value ) == 0 ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RationalInteger ) ) {
			return false;
		} else {
			return equals( ((RationalInteger)obj) );
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
