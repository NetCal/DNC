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
 * GNU Lesser General  License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General  License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */
 
package unikl.disco.numbers;

public interface NumFactoryInterface {
	 Num getPositiveInfinity();

	 Num createPositiveInfinity();
	
	 Num getNegativeInfinity();
	
	 Num createNegativeInfinity();

	 Num getNaN();
	
	 Num createNaN();

	 Num getZero();
	
	 Num createZero();
	
	 Num getEpsilon();
	
	 Num createEpsilon();
	
	 Num create( double value );
	
	 Num create( int num, int den );
	
	 Num create( String num_str ) throws Exception;
}
