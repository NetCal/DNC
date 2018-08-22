/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
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

package de.uni_kl.cs.discodnc.bounds;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Output {
    private Output() {
    }

    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves,
                                            Server server) throws Exception {
        return compute(configuration, arrival_curves, server, Collections.singleton(server.getServiceCurve()));
    }

    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves,
                                            Server server, Set<ServiceCurve> betas_lo) throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        if (configuration.useGamma() != AnalysisConfig.GammaFlag.GLOBALLY_OFF) {
            result = Calculator.getInstance().getMinPlus().deconvolve_almostConcCs_SCs(
                    Calculator.getInstance().getMinPlus().convolve_ACs_MSC(arrival_curves, server.getGamma()), betas_lo);
        } else {
            result = Calculator.getInstance().getMinPlus().deconvolve(arrival_curves, betas_lo);
        }

        if (configuration.useExtraGamma() != AnalysisConfig.GammaFlag.GLOBALLY_OFF) {
            result = Calculator.getInstance().getMinPlus().convolve_ACs_EGamma(result, server.getExtraGamma());
        }

        return result;
    }

    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Path path,
                                            Set<ServiceCurve> betas_lo) throws Exception {
        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();

        if (configuration.useGamma() != AnalysisConfig.GammaFlag.GLOBALLY_OFF) {
            result = Calculator.getInstance().getMinPlus().deconvolve_almostConcCs_SCs(
                    Calculator.getInstance().getMinPlus().convolve_ACs_MSC(arrival_curves, path.getGamma()), betas_lo);
        } else {
            result = Calculator.getInstance().getMinPlus().deconvolve(arrival_curves, betas_lo);
        }

        if (configuration.useExtraGamma() != AnalysisConfig.GammaFlag.GLOBALLY_OFF) {
            result = Calculator.getInstance().getMinPlus().convolve_ACs_EGamma(result, path.getExtraGamma());
        }

        return result;
    }
}
