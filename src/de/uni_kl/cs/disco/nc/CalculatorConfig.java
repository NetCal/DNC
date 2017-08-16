/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.nc;

import de.uni_kl.cs.disco.curves.CurvePwAffineFactory;
import de.uni_kl.cs.disco.numbers.NumFactory;
import de.uni_kl.cs.disco.numbers.NumUtils;

import java.io.File;

public final class CalculatorConfig {
	private static CalculatorConfig calc_config = null;
	
	private boolean ARRIVAL_CURVE_CHECKS = false;
	private boolean SERVICE_CURVE_CHECKS = false;
	private boolean MAX_SERVICE_CURVE_CHECKS = false;
	private boolean FIFO_MUX_CHECKS = false;
	private boolean DECONVOLUTION_CHECKS = false;
	
	private NumClass NUM_CLASS = NumClass.REAL_DOUBLE_PRECISION;
	private CurveClass CURVE_CLASS = CurveClass.DNC;

	protected CalculatorConfig() {} 
	
	public static CalculatorConfig getInstance() {
		if( calc_config == null ) {
			return new CalculatorConfig();
		}
		return calc_config;
	}
	
	public boolean exec_arrival_curve_checks() {
		return ARRIVAL_CURVE_CHECKS;
	}
	
	public boolean exec_service_curve_checks() {
		return SERVICE_CURVE_CHECKS;
	}
	
	public boolean exec_max_service_curve_checks() {
		return MAX_SERVICE_CURVE_CHECKS;
	}
	
	public boolean exec_fifo_mux_checks() {
		return FIFO_MUX_CHECKS;
	}
	
	public boolean exec_deconvolution_checks() {
		return DECONVOLUTION_CHECKS;
	}
	
	public NumClass getNumClass() {
		return NUM_CLASS;
	}

	public boolean setNumClass(NumClass num_class) {
		if (NUM_CLASS == num_class) {
			return false;
		} else {
			NUM_CLASS = num_class;
			NumFactory.setNumClass(num_class);
			NumUtils.setNumClass(num_class);
			return true;
		}
	}

	public CurveClass getCurveClass() {
		return CURVE_CLASS;
	}

	public boolean setCurveClass(CurveClass curve_class) {
		if (curve_class == CurveClass.MPA_RTC) {
            File f = new File("rtc.jar");
            if (!f.exists() && !f.isDirectory()) {
			f = new File("lib/rtc.jar");
			if (!f.exists() && !f.isDirectory()) {
				System.out.println("Error: rtc.jar not found in directory " + f.getParent() + ".");
				System.exit(1);
			}
	    }}
		if (CURVE_CLASS == curve_class) {
			return false;
		}
		CURVE_CLASS = curve_class;
		CurvePwAffineFactory.setCurveClass(curve_class);
		return true;
	}

	public void disableAllChecks() {
		ARRIVAL_CURVE_CHECKS = false;
		SERVICE_CURVE_CHECKS = false;
		MAX_SERVICE_CURVE_CHECKS = false;
		FIFO_MUX_CHECKS = false;
		DECONVOLUTION_CHECKS = false;
	}

	public void enableAllChecks() {
		ARRIVAL_CURVE_CHECKS = true;
		SERVICE_CURVE_CHECKS = true;
		MAX_SERVICE_CURVE_CHECKS = true;
		FIFO_MUX_CHECKS = true;
		DECONVOLUTION_CHECKS = true;
	}
	
	// TODO: toString

	public enum NumClass {REAL_SINGLE_PRECISION, REAL_DOUBLE_PRECISION, RATIONAL_INTEGER, RATIONAL_BIGINTEGER}

	public enum CurveClass { DNC, MPA_RTC }
}
