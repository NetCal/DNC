/*
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
import unikl.disco.numbers.NumUtilsInterface;
import unikl.disco.numbers.implementations.RealSingle;

public class RealSingleUtils implements NumUtilsInterface {
	public Num add( Num num1, Num num2 ) {
		return RealSingle.add( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num sub( Num num1, Num num2 ) {
		return RealSingle.sub( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num mult( Num num1, Num num2 ) {
		return RealSingle.mult( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num div( Num num1, Num num2 ) {
		return RealSingle.div( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num abs( Num num ) {
		return RealSingle.abs( (RealSingle)num );
	}

	public Num diff( Num num1, Num num2 ) {
		return RealSingle.diff( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num max( Num num1, Num num2 ) {
		return RealSingle.max( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num min( Num num1, Num num2 ) {
		return RealSingle.min( (RealSingle)num1, (RealSingle)num2 );
	}

	public Num negate( Num num ) {
		return RealDouble.negate( (RealDouble)num );
	}
}
