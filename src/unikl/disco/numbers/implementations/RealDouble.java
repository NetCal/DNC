/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2014 - 2017 Steffen Bondorf
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

/**
 * Wrapper class around double;
 *
 * @author Steffen Bondorf
 *
 */
public class RealDoublePrecision implements Num {
	private double value;
	
	private static final double EPSILON = Double.parseDouble("5E-10");
	private static boolean comparison_epsilon = false;
	
	@SuppressWarnings("unused")
	private RealDoublePrecision() {}
	
	public RealDoublePrecision( double value ) {
		this.value = value;
	}
	
	public RealDoublePrecision( int num ) {
		value = (double)num;
	}
	
	public RealDoublePrecision( int num, int den ) {
		value = ((double)num) / ((double)den);
	}
	
	public RealDoublePrecision( RealDoublePrecision num ) {
		value = num.value;
	}
	
	public static Num createEpsilon() {
        return new RealDoublePrecision( EPSILON );
	}
	
	public static Num add( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( num1.value + num2.value );
	}
	
	public static Num sub( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		double result = num1.value - num2.value;
		if( Math.abs( result ) <= EPSILON ) {
			result = 0;
		}
		return new RealDoublePrecision( result );
	}
	
	public static Num mult( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( num1.value * num2.value );
	}

	public static Num div( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( num1.value / num2.value );
	}

	public static Num diff( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( Math.max( num1.value, num2.value )
										- Math.min( num1.value, num2.value ) );	
	}

	public static Num max( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( Math.max( num1.value, num2.value ) );
	}

	public static Num min( RealDoublePrecision num1, RealDoublePrecision num2 ) {
		return new RealDoublePrecision( Math.min( num1.value, num2.value ) );
	}
	
	public static Num abs( RealDoublePrecision num ) {
		return new RealDoublePrecision( Math.abs( num.value ) );
	}

	public static Num negate( RealDoublePrecision num ) {
	    return new RealDoublePrecision( num.value * -1 );
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
	
	@Override
	public double doubleValue() {
	    return value;
	}

	@Override
	public Num copy() {
		return new RealDoublePrecision( value );
	}
	
	@Override
	public boolean eq( double num ) {
		if( ( this.value == Double.POSITIVE_INFINITY && num == Double.POSITIVE_INFINITY ) 
				|| ( this.value == Double.NEGATIVE_INFINITY && num == Double.NEGATIVE_INFINITY ) ) {
			return true;
		}
		
		if( Math.abs( value - num ) <= EPSILON ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean equals( RealDoublePrecision num ) {
		return eq( num.value );
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null 
				|| !( obj instanceof RealDoublePrecision ) ) {
			return false;
		} else {
			return eq( ((RealDoublePrecision)obj).value );
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
