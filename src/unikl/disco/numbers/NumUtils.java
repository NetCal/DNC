/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.1 "Centaur".
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
 
package unikl.disco.numbers;

/**
 * 
 * The IEEE 754 floating point data types double and single provide infinity values and NaN.
 * For other data types, i.e., rational numbers based on the Apache Commons Math's Fraction classes,
 * we need to emulate the floating point behavior. 
 * 
 */
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.numbers.implementations.RationalBigIntUtils;
import unikl.disco.numbers.implementations.RationalIntUtils;
import unikl.disco.numbers.implementations.RealDoubleUtils;
import unikl.disco.numbers.implementations.RealSingleUtils;

public class NumUtils {
	private static NumUtilsInterface utils_realdouble = new RealDoubleUtils();
	private static NumUtilsInterface utils_realsingle = new RealSingleUtils();
	private static NumUtilsInterface utils_rationalint = new RationalIntUtils();
	private static NumUtilsInterface utils_rationalbigint = new RationalBigIntUtils();
	
	private static NumUtilsInterface utils = getNumUtils();
	
	private static NumUtilsInterface getNumUtils() {
		switch ( CalculatorConfig.getNumClass() ) {
			case REAL_SINGLE_PRECISION:
				return utils_realsingle;
			case RATIONAL_INTEGER:
				return utils_rationalint;
			case RATIONAL_BIGINTEGER:
				return utils_rationalbigint;
			case REAL_DOUBLE_PRECISION:
			default:
				return utils_realdouble;
		}
	}
	
	public static void setNumClass( NumClass num_class ) {
		switch ( num_class ) {
			case REAL_SINGLE_PRECISION:
				utils = utils_realsingle;
				return;
			case RATIONAL_INTEGER:
				utils = utils_rationalint;
				return;
			case RATIONAL_BIGINTEGER:
				utils = utils_rationalbigint;
				return;
			case REAL_DOUBLE_PRECISION:
			default:
				utils = utils_realdouble;
				return;
		}
	}
	
	public static Num add( Num num1, Num num2 ) {
		return utils.add(num1, num2);
	}

	public static Num sub( Num num1, Num num2 ) {
		return utils.sub(num1, num2);
	}

	public static Num mult( Num num1, Num num2 ) {
		return utils.mult(num1, num2);
	}

	public static Num div( Num num1, Num num2 ) {
		return utils.div(num1, num2);
	}

	public static Num abs( Num num ) {
		return utils.abs(num);
	}

	public static Num diff( Num num1, Num num2 ) {
		return utils.diff(num1, num2);
	}

	public static Num max( Num num1, Num num2 ) {
		return utils.max(num1, num2);
	}

	public static Num min( Num num1, Num num2 ) {
		return utils.min(num1, num2);
	}

	public static Num negate( Num num ) {
		return utils.negate(num);
	}
}
