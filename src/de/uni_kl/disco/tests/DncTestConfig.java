/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.5 "Centaur".
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

package de.uni_kl.disco.tests;

import java.util.Set;

import de.uni_kl.disco.nc.AnalysisConfig;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.nc.CalculatorConfig.NumClass;

public class DncTestConfig extends AnalysisConfig {
    // Functional test specific parameters
    protected boolean define_multiplexing_globally;
    protected AnalysisConfig.Multiplexing mux_discipline;
    protected boolean console_output = false;

    // Calculator parameters
    protected boolean enable_checks = false;
    protected NumClass numbers;

    @SuppressWarnings("unused")
    private DncTestConfig() {
    }

    public DncTestConfig(Set<ArrivalBoundMethod> arrival_bound_methods,
                         boolean remove_duplicate_arrival_bounds,
                         boolean tbrl_convolution,
                         boolean tbrl_deconvolution,
                         AnalysisConfig.Multiplexing mux_discipline,
                         boolean define_multiplexing_globally,
                         CalculatorConfig.NumClass numbers) {

        super(AnalysisConfig.MuxDiscipline.GLOBAL_ARBITRARY, // Not used, no influence yet.
                GammaFlag.GLOBALLY_OFF,        // Not used, no influence yet.
                GammaFlag.GLOBALLY_OFF,        // Not used, no influence yet.
                arrival_bound_methods,
                remove_duplicate_arrival_bounds,
                tbrl_convolution,
                tbrl_deconvolution,
                false);

        this.mux_discipline = mux_discipline;
        this.define_multiplexing_globally = define_multiplexing_globally;
        this.numbers = numbers;
    }

    public boolean fullConsoleOutput() { // false == Exceptions only
        return console_output;
    }

    @Override
    public String toString() {
        // AB, ab cache, convolve ABs, tbrl opt convolution, tbrl opt deconvolusion, mux, global mux def
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

        func_test_str.append(", " + numbers.toString());

        return func_test_str.toString();
    }
}
