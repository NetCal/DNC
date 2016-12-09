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
public final class NegativeInfinity implements Num {
	public NegativeInfinity() {}
	
	public boolean isNaN() {
		return false;
	}
	
	public boolean isPosInfty() {
		return true;
	}
	
	public boolean isNegInfty() {
		return false;
	}
	protected static Num add( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}
	
	@Override
	public void add( Num num2 ) {}
	
	protected static Num sub( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}
	
	@Override
	public void sub( Num num2 ) {}
	
	protected static Num mult( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}
	
	@Override
	public void mult( Num num2 ) {}

	protected static Num div( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}
	
	@Override
	public void div( Num num2 ) {}

	protected static Num diff( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();	
	}

	protected static Num max( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}

	protected static Num min( NegativeInfinity num1, NegativeInfinity num2 ) {
		return new NegativeInfinity();
	}
	
	protected static Num abs( NegativeInfinity num ) {
		return new NegativeInfinity();
	}

	protected static Num negate( NegativeInfinity num ) {
	    return new NegativeInfinity();
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
		return new NegativeInfinity();
	}
	
	@Override
	public boolean equals( double num2 ) {
		return false;
	}

	protected boolean equals( NegativeInfinity num2 ) {
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
