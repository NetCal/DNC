/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.CurveImpl;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.NumImpl;

import java.util.Set;

public class DncTestConfig extends AnalysisConfig {
	// Functional test specific parameters
	protected boolean define_multiplexing_globally;
	protected AnalysisConfig.Multiplexing mux_discipline;
	protected boolean console_output = false;

	// Calculator configuration
	protected boolean enable_checks = false;
	protected NumImpl num_implementation;
	protected CurveImpl curve_implementation;

	@SuppressWarnings("unused")
	private DncTestConfig() {
	}

	public DncTestConfig(Set<ArrivalBoundMethod> arrival_bound_methods, boolean remove_duplicate_arrival_bounds,
			boolean tbrl_convolution, boolean tbrl_deconvolution, AnalysisConfig.Multiplexing mux_discipline,
			boolean define_multiplexing_globally, NumImpl numbers, CurveImpl curves ) {

		super(AnalysisConfig.MuxDiscipline.GLOBAL_ARBITRARY, // Not used, no influence yet.
				GammaFlag.GLOBALLY_OFF, // Not used, no influence yet.
				GammaFlag.GLOBALLY_OFF, // Not used, no influence yet.
				arrival_bound_methods, remove_duplicate_arrival_bounds, tbrl_convolution, tbrl_deconvolution, false);

		this.mux_discipline = mux_discipline;
		this.define_multiplexing_globally = define_multiplexing_globally;

		// Will not work. Num implementation and curve implementation need to be stored
		// locally as the singleton pattern will cause overwriting this setting in
		// CalculatorConfig.

		num_implementation = numbers;
		curve_implementation = curves;
	}

	public boolean fullConsoleOutput() { // false == Exceptions only
		return console_output;
	}

	protected NumImpl getNumImpl() {
		return num_implementation;
	}

	protected CurveImpl getCurveImpl() {
		return curve_implementation;
	}

	@Override
	public String toString() {
		// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolusion, mux,
		// global mux def, numbers, curves
		StringBuffer func_test_str = new StringBuffer();

		func_test_str.append(arrivalBoundMethods().toString());

		if (removeDuplicateArrivalBounds()) {
			func_test_str.append(", " + "rm dupl ABs");
		}
		if (tbrlConvolution()) {
			func_test_str.append(", " + "TbRl Conv");
		}
		if (tbrlDeconvolution()) {
			func_test_str.append(", " + "TbRl Deconv");
		}

		func_test_str.append(", " + mux_discipline.toString());

		if (define_multiplexing_globally) {
			func_test_str.append(", " + "MUX global");
		}

		func_test_str.append(", NUM_" + num_implementation.toString());

		func_test_str.append(", C_" + curve_implementation.toString());

		return func_test_str.toString();
	}
}
