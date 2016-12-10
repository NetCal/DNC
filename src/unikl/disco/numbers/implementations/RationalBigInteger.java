/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2016 - 2017 Steffen Bondorf
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

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import unikl.disco.numbers.Num;
import unikl.disco.numbers.values.NaN;
import unikl.disco.numbers.values.NegativeInfinity;
import unikl.disco.numbers.values.PositiveInfinity;

/**
 * Wrapper class around org.apache.commons.math3.BigFraction.BigFraction
 * introducing special values like positive / negative infinity and NaN
 * as well as operators like min, max, ==, &gt;, &gt;=, &lt;, and &lt;= that are
 * not part of BigFraction but needed by the network calculator.
 * 
 * For the ease of converting from the primitive data type double
 * to BigFraction objects, copy by value semantic are is applied. 
 * 
 * @author Steffen Bondorf
 *
 */
public class RationalBigInteger implements Num {
	private BigFraction value = new BigFraction( 0.0 );
	
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final BigFraction EPSILON = new BigFraction( 1, 1000000 ); 
	private static final BigFraction ZERO_BIGFRACTION = new BigFraction( 0 );
	
	private RationalBigInteger(){}
	
	public RationalBigInteger( double value ) {
		this.value = new BigFraction( value );
	}
	
	public RationalBigInteger( int num ) {
		value = new BigFraction( num );
	}
	
	public RationalBigInteger( int num, int den ) {
		value = new BigFraction( num, den );
	}
	
	public RationalBigInteger( BigInteger num, BigInteger den ) {
		value = new BigFraction( num, den );
	}
	
	public RationalBigInteger( RationalBigInteger num ) {
		value = new BigFraction( num.value.getNumerator(), num.value.getDenominator() );
	}
	
	private RationalBigInteger( BigFraction frac ) {
		value = new BigFraction( frac.getNumerator(), frac.getDenominator() );
	}
	
	public static RationalBigInteger createZero() {
		RationalBigInteger zero = new RationalBigInteger();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = new BigFraction( 0.0 );
	}
	
	public static RationalBigInteger createEpsilon() {
        return new RationalBigInteger( EPSILON );
	}
	
	public static RationalBigInteger add( RationalBigInteger num1, RationalBigInteger num2 ) {
		// May still throw MathArithmeticException due to integer overflow
        return new RationalBigInteger( num1.value.add( num2.value ) );
	}
	
	public static RationalBigInteger sub( RationalBigInteger num1, RationalBigInteger num2 ) {
        // May still throw MathArithmeticException due to integer overflow
        return new RationalBigInteger( num1.value.subtract( num2.value ) );
	}
	
	public static RationalBigInteger mult( RationalBigInteger num1, RationalBigInteger num2 ) {
        // May throw MathArithmeticException due to integer overflow
       	return new RationalBigInteger( num1.value.multiply( num2.value ) );
	}

	public static RationalBigInteger div( RationalBigInteger num1, RationalBigInteger num2 ) {
		return new RationalBigInteger( num1.value.divide( num2.value ) );        		
	}

	public static RationalBigInteger diff( RationalBigInteger num1, RationalBigInteger num2 ) {
		return sub( max( num1, num2 ), min( num1, num2 ) );	
	}

	public static RationalBigInteger max( RationalBigInteger num1, RationalBigInteger num2 ) {
		if( num1.value.compareTo( num2.value ) >= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}

	public static RationalBigInteger min( RationalBigInteger num1, RationalBigInteger num2 ) {
		if( num1.value.compareTo( num2.value ) <= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}
	
	public static RationalBigInteger abs( RationalBigInteger num ) {
    	return new RationalBigInteger( num.value.abs() );
	}

	public static RationalBigInteger negate( RationalBigInteger num ) {
    	return new RationalBigInteger( num.value.negate() );
	}
	
	public boolean isZero() {
		return value.getNumerator().intValue() == 0;
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
		
		if( this.value.compareTo( ((RationalBigInteger)num2).value ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean greaterZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geq( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return false;
		}
		if( num2 instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((RationalBigInteger)num2).value ) >= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geqZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) >= 0 ) {
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
		
		if( this.value.compareTo( ((RationalBigInteger)num2).value ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean lessZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leq( Num num2 ) {
		if( num2 instanceof NaN ){
			return false;
		}
		if( num2 instanceof PositiveInfinity ){
			return true;
		}
		if( num2 instanceof NegativeInfinity ){
			return false;
		}
		
		if( this.value.compareTo( ((RationalBigInteger)num2).value ) <= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leqZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) <= 0 ) {
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
		return new RationalBigInteger( this.value.getNumerator(), this.value.getDenominator() );
	}
	
	@Override
	public boolean equals( double num2 ) {
		return this.doubleValue() - num2 <= RealDoublePrecision.createEpsilon().doubleValue();
	}

	public boolean equals( RationalBigInteger num2 ) {
		if( this.value.compareTo( num2.value ) == 0 ) {
			return true;
		} else {
			return equals( num2.doubleValue() );
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RationalBigInteger ) ) {
			return false;
		} else {
			return equals( ((RationalBigInteger) obj) );
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
