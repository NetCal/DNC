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
	
	public boolean isNaN() {
		return false;
	}
	
	public boolean isPosInfty() {
		return true;
	}
	
	public boolean isNegInfty() {
		return false;
	}
	protected static Num add( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}
	
	@Override
	public void add( Num num2 ) {}
	
	protected static Num sub( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}
	
	@Override
	public void sub( Num num2 ) {}
	
	protected static Num mult( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}
	
	@Override
	public void mult( Num num2 ) {}

	protected static Num div( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}
	
	@Override
	public void div( Num num2 ) {}

	protected static Num diff( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();	
	}

	protected static Num max( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}

	protected static Num min( PositiveInfinity num1, PositiveInfinity num2 ) {
		return new PositiveInfinity();
	}
	
	protected static Num abs( PositiveInfinity num ) {
		return new PositiveInfinity();
	}

	protected static Num negate( PositiveInfinity num ) {
	    return new PositiveInfinity();
	}

	public boolean greater( Num num2 ) {
		return false;
	}

	public boolean ge( Num num2 ) {
		return false;
	}

	public boolean less( Num num2 ) {
		return false;
	}

	public boolean le( Num num2 ) {
		return false;
	}
	
	@Override
	public double doubleValue() {
	    return Double.NaN;
	}

	@Override
	public Num copy() {
		return new PositiveInfinity();
	}
	
	@Override
	public boolean equals( double num2 ) {
		return false;
	}

	protected boolean equals( PositiveInfinity num2 ) {
		return false;
	}

	@Override
	public boolean equals( Object obj ) {
		return false;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode( Double.NaN );
	}
	
	@Override
	public String toString(){
		return "NaN";
	}
}
