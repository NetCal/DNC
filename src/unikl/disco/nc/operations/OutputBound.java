/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.2 "Centaur".
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

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
package unikl.disco.nc.operations;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.minplus.Convolution;
import unikl.disco.minplus.Deconvolution;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.AnalysisConfig.GammaFlag;
import unikl.disco.network.Path;
import unikl.disco.network.Server;

public class OutputBound {
	private OutputBound() {}
	
	public static Set<ArrivalCurve> compute( AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server ) throws Exception {
		return compute( configuration, arrival_curves, server, Collections.singleton( server.getServiceCurve() ) );
	}
	
	public static Set<ArrivalCurve> compute( AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server, Set<ServiceCurve> betas_lo ) throws Exception {
		Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
		
		if( configuration.useGamma() != GammaFlag.GLOBALLY_OFF  ) {
			result = Deconvolution.deconvolve_almostConcCs_SCs( Convolution.convolve_ACs_MSC( arrival_curves, server.getGamma() ), betas_lo );
		} else {
			result = Deconvolution.deconvolve( arrival_curves, betas_lo, configuration.tbrlDeconvolution() );
		}
		
		if( configuration.useExtraGamma() != GammaFlag.GLOBALLY_OFF ) {
			result = Convolution.convolve_ACs_EGamma( result, server.getExtraGamma() );
		}
		
		return result;
	}

	public static Set<ArrivalCurve> compute( AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Path path, Set<ServiceCurve> betas_lo ) throws Exception {
		Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
		
		if( configuration.useGamma() != GammaFlag.GLOBALLY_OFF ) {
			result = Deconvolution.deconvolve_almostConcCs_SCs( Convolution.convolve_ACs_MSC( arrival_curves, path.getGamma() ), betas_lo );
		} else {
			result = Deconvolution.deconvolve( arrival_curves, betas_lo, configuration.tbrlDeconvolution() );
		}
		
		if( configuration.useExtraGamma() != GammaFlag.GLOBALLY_OFF ) {
			result = Convolution.convolve_ACs_EGamma( result, path.getExtraGamma() );
		}
		
		return result;
	}
}
