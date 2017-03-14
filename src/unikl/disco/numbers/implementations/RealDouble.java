/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
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

import unikl.disco.numbers.Num;

public class RealDouble implements Num {
	private double value;
	
	private static final double EPSILON = Double.parseDouble("5E-10");
	private static boolean comparison_epsilon = false;
	
	@SuppressWarnings("unused")
	private RealDouble() {}
	
	public RealDouble( double value ) {
		this.value = value;
	}
	
	public RealDouble( int num ) {
		value = (double)num;
	}
	
	public RealDouble( int num, int den ) {
		value = ((double)num) / ((double)den);
	}
	
	public RealDouble( RealDouble num ) {
		value = num.value;
	}
	
	public static Num createEpsilon() {
        return new RealDouble( EPSILON );
	}
	
	public static Num add( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( num1.value + num2.value );
	}
	
	public static Num sub( RealDouble num1, RealDouble num2 ) {
		double result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new RealDouble( result );
	}
	
	public static Num mult( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( num1.value * num2.value );
	}

	public static Num div( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( num1.value / num2.value );
	}

	public static Num diff( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( Math.max( num1.value, num2.value ) );
	}

	public static Num min( RealDouble num1, RealDouble num2 ) {
		return new RealDouble( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( RealDouble num ) {
		return new RealDouble( Math.abs( num.value ) );
	}

	public static Num negate( RealDouble num ) {
	    return new RealDouble( num.value * -1 );
	}
	
	public boolean eqZero() {
		if( comparison_epsilon ) {
			return ( value <= EPSILON ) && ( value >= -EPSILON );
		} else {
			return value == 0.0;
		}
	}

	public boolean gt( Num num ) {
		if( comparison_epsilon ) {
			return value > ( num.doubleValue() + EPSILON );
		} else {
			return value > num.doubleValue();
		}
	}

	public boolean gtZero() {
		if( comparison_epsilon ) {
			return value > EPSILON;
		} else {
			return value > 0.0;
		}
	}

	public boolean geq( Num num ) {
		if( comparison_epsilon ) {
			return value >= ( num.doubleValue() + EPSILON );
		} else {
			return value >= num.doubleValue();
		}
	}

	public boolean geqZero() {
		if( comparison_epsilon ) {
			return value >= EPSILON;
		} else {
			return value >= 0.0;
		}
	}

	public boolean lt( Num num ) {
		if( comparison_epsilon ) {
			return value < ( num.doubleValue() - EPSILON );
		} else {
			return value < num.doubleValue();
		}
	}

	public boolean ltZero() {
		if( comparison_epsilon ) {
			return value < -EPSILON;
		} else {
			return value < 0.0;
		}
	}

	public boolean leq( Num num ) {
		if( comparison_epsilon ) {
			return value <= ( num.doubleValue() - EPSILON );
		} else {
			return value <= num.doubleValue();
		}
	}

	public boolean leqZero() {
		if( comparison_epsilon ) {
			return value <= -EPSILON;
		} else {
			return value <= 0.0;
		}
	}

	public boolean isFinite() {
		return Double.isFinite( value );
	}

	public boolean isInfinite() {
		return Double.isInfinite( value );
	}
	
	public boolean isNaN() {
		return Double.isNaN( value );
	}
	
	@Override
	public double doubleValue() {
	    return value;
	}

	@Override
	public Num copy() {
		return new RealDouble( value );
	}
	
	@Override
	public boolean eq( double num ) {
//		if( ( this.value == Double.POSITIVE_INFINITY && num == Double.POSITIVE_INFINITY ) 
//				|| ( this.value == Double.NEGATIVE_INFINITY && num == Double.NEGATIVE_INFINITY ) ) {
		if( Double.isInfinite( this.value ) 
				&& Double.isInfinite( num )
				&& ( Double.compare( this.value, num ) == 0 ) ) {
			return true;
		}
		
		if( Math.abs( value - num ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals( RealDouble num ) {
		return eq( num.value );
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RealDouble ) ) {
			return false;
		} else {
			return eq( ((RealDouble)obj).value );
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
