/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.4 "Centaur".
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
import unikl.disco.numbers.extraValues.NaN;
import unikl.disco.numbers.extraValues.NegativeInfinity;
import unikl.disco.numbers.extraValues.PositiveInfinity;

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
public class RationalBigInt implements Num {
	private BigFraction value = new BigFraction( 0.0 );
	
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final BigFraction EPSILON = new BigFraction( 1, 1000000 ); 
	private static final BigFraction ZERO_BIGFRACTION = new BigFraction( 0 );
	
	private RationalBigInt(){}
	
	public RationalBigInt( double value ) {
		this.value = new BigFraction( value );
	}
	
	public RationalBigInt( int num ) {
		value = new BigFraction( num );
	}
	
	public RationalBigInt( int num, int den ) {
		value = new BigFraction( num, den );
	}
	
	public RationalBigInt( BigInteger num, BigInteger den ) {
		value = new BigFraction( num, den );
	}
	
	public RationalBigInt( RationalBigInt num ) {
		value = new BigFraction( num.value.getNumerator(), num.value.getDenominator() );
	}
	
	private RationalBigInt( BigFraction frac ) {
		value = new BigFraction( frac.getNumerator(), frac.getDenominator() );
	}
	
	public static RationalBigInt createZero() {
		RationalBigInt zero = new RationalBigInt();
		zero.instantiateZero();
		return zero;
	}
	
	private void instantiateZero() {
		value = new BigFraction( 0.0 );
	}
	
	public static RationalBigInt createEpsilon() {
        return new RationalBigInt( EPSILON );
	}
	
	public static RationalBigInt add( RationalBigInt num1, RationalBigInt num2 ) {
		// May still throw MathArithmeticException due to integer overflow
        return new RationalBigInt( num1.value.add( num2.value ) );
	}
	
	public static RationalBigInt sub( RationalBigInt num1, RationalBigInt num2 ) {
        // May still throw MathArithmeticException due to integer overflow
        return new RationalBigInt( num1.value.subtract( num2.value ) );
	}
	
	public static RationalBigInt mult( RationalBigInt num1, RationalBigInt num2 ) {
        // May throw MathArithmeticException due to integer overflow
       	return new RationalBigInt( num1.value.multiply( num2.value ) );
	}

	public static RationalBigInt div( RationalBigInt num1, RationalBigInt num2 ) {
		return new RationalBigInt( num1.value.divide( num2.value ) );        		
	}

	public static RationalBigInt diff( RationalBigInt num1, RationalBigInt num2 ) {
		return sub( max( num1, num2 ), min( num1, num2 ) );	
	}

	public static RationalBigInt max( RationalBigInt num1, RationalBigInt num2 ) {
		if( num1.value.compareTo( num2.value ) >= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}

	public static RationalBigInt min( RationalBigInt num1, RationalBigInt num2 ) {
		if( num1.value.compareTo( num2.value ) <= 0 ) {
			return num1;
		} else {
			return num2;
		}
	}
	
	public static RationalBigInt abs( RationalBigInt num ) {
    	return new RationalBigInt( num.value.abs() );
	}

	public static RationalBigInt negate( RationalBigInt num ) {
    	return new RationalBigInt( num.value.negate() );
	}
	
	public boolean eqZero() {
		return value.compareTo( BigFraction.ZERO ) == 0;
	}

	public boolean gt( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return false;
		}
		if( num instanceof NegativeInfinity ){
			return true;
		}
		
		if( this.value.compareTo( ((RationalBigInt)num).value ) > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean gtZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) > 0 ) {
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
		
		if( this.value.compareTo( ((RationalBigInt)num).value ) >= 0 ) {
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

	public boolean lt( Num num ) {
		if( num instanceof NaN ){
			return false;
		}
		if( num instanceof PositiveInfinity ){
			return true;
		}
		if( num instanceof NegativeInfinity ){
			return false;
		}
		
		if( this.value.compareTo( ((RationalBigInt)num).value ) < 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean ltZero() {
		if( this.value.compareTo( ZERO_BIGFRACTION ) < 0 ) {
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
		
		if( this.value.compareTo( ((RationalBigInt)num).value ) <= 0 ) {
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

	public boolean isFinite() {
		return true;
	}

	public boolean isInfinite() {
		return false; // Handled by extraValues
	}
	
	public boolean isNaN() {
		return false; // Handled by extraValues
	}
	
	@Override
	public double doubleValue() {
	    return value.doubleValue();
	}

	@Override
	public Num copy() {
		return new RationalBigInt( this.value.getNumerator(), this.value.getDenominator() );
	}
	
	@Override
	public boolean eq( double num ) {
		return this.doubleValue() - num <= RealDouble.createEpsilon().doubleValue();
	}

	public boolean equals( RationalBigInt num ) {
		if( this.value.compareTo( num.value ) == 0 ) {
			return true;
		} else {
			return eq( num.doubleValue() );
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RationalBigInt ) ) {
			return false;
		} else {
			return equals( ((RationalBigInt)obj) );
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
