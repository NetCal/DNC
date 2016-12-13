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
 
package unikl.disco.numbers.values;

import unikl.disco.numbers.Num;

/**
 * Wrapper class around double;
 *
 * @author Steffen Bondorf
 *
 */
public final class PositiveInfinity implements Num {
	public PositiveInfinity() {}
	
	public boolean isZero() {
		return false;
	}

	public boolean greater( Num num ) {
		return true;
	}

	public boolean greaterZero() {
		return true;
	}

	public boolean geq( Num num ) {
		return true;
	}

	public boolean geqZero() {
		return true;
	}

	public boolean less( Num num ) {
		return false;
	}

	public boolean lessZero() {
		return false;
	}

	public boolean leq( Num num ) {
		return false;
	}

	public boolean leqZero() {
		return false;
	}
	
	@Override
	public double doubleValue() {
	    return Double.POSITIVE_INFINITY;
	}

	@Override
	public Num copy() {
		return new PositiveInfinity();
	}
	
	@Override
	public boolean equals( double num ) {
		if( num == Double.POSITIVE_INFINITY ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals( Object obj ) {
		if( obj == null ) {
			return false;
		}
		if( obj instanceof PositiveInfinity ) {
			return true;
		}
		if( obj instanceof Num ) {
			return equals( ((Num)obj).doubleValue() );
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode( Double.POSITIVE_INFINITY );
	}
	
	@Override
	public String toString(){
		return Double.toString( Double.POSITIVE_INFINITY );
	}
}
