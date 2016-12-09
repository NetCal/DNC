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
public final class NaN implements Num {
	public NaN() {}
	
	public boolean isNaN() {
		return true;
	}
	
	public boolean isPosInfty() {
		return false;
	}
	
	public boolean isNegInfty() {
		return false;
	}
	protected static Num add( NaN num1, NaN num2 ) {
		return new NaN();
	}
	
	@Override
	public void add( Num num2 ) {}
	
	protected static Num sub( NaN num1, NaN num2 ) {
		return new NaN();
	}
	
	@Override
	public void sub( Num num2 ) {}
	
	protected static Num mult( NaN num1, NaN num2 ) {
		return new NaN();
	}
	
	@Override
	public void mult( Num num2 ) {}

	protected static Num div( NaN num1, NaN num2 ) {
		return new NaN();
	}
	
	@Override
	public void div( Num num2 ) {}

	protected static Num diff( NaN num1, NaN num2 ) {
		return new NaN();	
	}

	protected static Num max( NaN num1, NaN num2 ) {
		return new NaN();
	}

	protected static Num min( NaN num1, NaN num2 ) {
		return new NaN();
	}
	
	protected static Num abs( NaN num ) {
		return new NaN();
	}

	protected static Num negate( NaN num ) {
	    return new NaN();
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
		return new NaN();
	}
	
	@Override
	public boolean equals( double num2 ) {
		return false;
	}

	protected boolean equals( NaN num2 ) {
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
