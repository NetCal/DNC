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
 
package unikl.disco.numbers;

/**
 * 
 * Class emulating multiple dispatch (Java only offers single dispatch) 
 * in order to allow the user to switch between available number representations. 
 *
 * @author Steffen Bondorf
 *
 */
public interface Num {
	public boolean isZero();

	public boolean equals( double obj );

	public boolean greater( Num num2 );
	public boolean greaterZero();

	public boolean geq( Num num2 );
	public boolean geqZero();

	public boolean less( Num num2 );
	public boolean lessZero();

	public boolean leq( Num num2 );
	public boolean leqZero();
	
	public double doubleValue();

	public Num copy();
}
