/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
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

package unikl.disco.nc;

/**
 * 
 * @author Steffen Bondorf
 *
 */
public final class CalculatorConfig {
	public static enum NumClass { REAL_SINGLE_PRECISION, REAL_DOUBLE_PRECISION, RATIONAL_INTEGER, RATIONAL_BIGINTEGER };
	public static NumClass NUM_CLASS = NumClass.REAL_DOUBLE_PRECISION;

	public static boolean ARRIVAL_CURVE_CHECKS = false;
	public static boolean SERVICE_CURVE_CHECKS = false;
	public static boolean MAX_SERVICE_CURVE_CHECKS = false;
	public static boolean FIFO_MUX_CHECKS = false;
	public static boolean DECONVOLUTION_CHECKS = false;
	
	public static void disableAllChecks() {
		ARRIVAL_CURVE_CHECKS = false;
		SERVICE_CURVE_CHECKS = false;
		MAX_SERVICE_CURVE_CHECKS = false;
		FIFO_MUX_CHECKS = false;
		DECONVOLUTION_CHECKS = false;
	}
	
	public static void enableAllChecks() {
		ARRIVAL_CURVE_CHECKS = true;
		SERVICE_CURVE_CHECKS = true;
		MAX_SERVICE_CURVE_CHECKS = true;
		FIFO_MUX_CHECKS = true;
		DECONVOLUTION_CHECKS = true;
	}
}