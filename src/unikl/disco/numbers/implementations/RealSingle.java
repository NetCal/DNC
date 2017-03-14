/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
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

import unikl.disco.numbers.Num;

public class RealSingle implements Num {
	private float value;
	
	private static final float EPSILON = 0.00016f; // Maximum rounding error observed in the functional tests
	private static boolean comparison_epsilon = false;
	
	@SuppressWarnings("unused")
	private RealSingle() {}
	
	public RealSingle( double value ) {
		this.value = (float)value;
	}
	
	public RealSingle( int num ) {
		value = (float)num;
	}
	
	public RealSingle( int num, int den ) {
		value = ((float)num) / ((float)den);
	}
	
	public RealSingle( RealSingle num ) {
		value = num.value;
	}
	
	public static Num createEpsilon() {
        return new RealSingle( EPSILON );
	}
	
	public static Num add( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( num1.value + num2.value );
	}
	
	public static Num sub( RealSingle num1, RealSingle num2 ) {
		float result = num1.value - num2.value;
		if( comparison_epsilon && Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new RealSingle( result );
	}
	
	public static Num mult( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( num1.value * num2.value );
	}

	public static Num div( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( num1.value / num2.value );
	}

	public static Num diff( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( Math.max( num1.value, num2.value ) );
	}

	public static Num min( RealSingle num1, RealSingle num2 ) {
		return new RealSingle( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( RealSingle num ) {
		return new RealSingle( Math.abs( num.value ) );
	}

	public static Num negate( RealSingle num ) {
	    return new RealSingle( num.value * -1 );
	}
	
	public boolean eqZero() {
		if( comparison_epsilon ) {
			return ( value <= EPSILON ) && ( value >= -EPSILON );
		} else {
			return value == 0.0f;
		}
	}

	public boolean gt( Num num ) {
		float num_float = (float)num.doubleValue();
		
		if( comparison_epsilon ) {
			return value > ( num_float + EPSILON );
		} else {
			return value > num_float;
		}
	}

	public boolean gtZero() {
		if( comparison_epsilon ) {
			return value > EPSILON;
		} else {
			return value > 0.0f;
		}
	}

	public boolean geq( Num num ) {
		float num_float = (float)num.doubleValue();
		
		if( comparison_epsilon ) {
			return value >= ( num_float + EPSILON );
		} else {
			return value >= num_float;
		}
	}

	public boolean geqZero() {
		if( comparison_epsilon ) {
			return value >= EPSILON;
		} else {
			return value >= 0.0f;
		}
	}

	public boolean lt( Num num ) {
		float num_float = (float)num.doubleValue();
		
		if( comparison_epsilon ) {
			return value < ( num_float - EPSILON );
		} else {
			return value < num_float;
		}
	}

	public boolean ltZero() {
		if( comparison_epsilon ) {
			return value < -EPSILON;
		} else {
			return value < 0.0f;
		}
	}

	public boolean leq( Num num ) {
		float num_float = (float)num.doubleValue();
		
		if( comparison_epsilon ) {
			return value <= ( num_float - EPSILON );
		} else {
			return value <= num_float;
		}
	}

	public boolean leqZero() {
		if( comparison_epsilon ) {
			return value <= -EPSILON;
		} else {
			return value <= 0.0f;
		}
	}

	public boolean isFinite() {
		return Float.isFinite( value );
	}

	public boolean isInfinite() {
		return Float.isInfinite( value );
	}
	
	public boolean isNaN() {
		return Float.isNaN( value );
	}
	
	@Override
	public double doubleValue() {
	    return (double)value;
	}

	@Override
	public Num copy() {
		return new RealSingle( value );
	}
	
	@Override
	public boolean eq( double num ) {
		if( comparison_epsilon ) {
			return Math.abs( num - value ) <= EPSILON;
		} else {
			return Double.compare( value, num ) == 0;
		}
	}

	public boolean equals( RealSingle num ) {
		return equals( num.value );
	}
	
	private boolean equals( float num ) {
//		if( ( this.value == Float.POSITIVE_INFINITY && num == Float.POSITIVE_INFINITY ) 
//				|| ( this.value == Float.NEGATIVE_INFINITY && num == Float.NEGATIVE_INFINITY ) ) {
//			return true;
//		}
		if( Float.isInfinite( this.value ) 
				&& Float.isInfinite( num )
				&& ( Float.compare( this.value, num ) == 0 ) ) {
			return true;
		}
		
		if( Math.abs( value - num ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RealSingle ) ) {
			return false;
		} else {
			return equals( ((RealSingle)obj).value );
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
