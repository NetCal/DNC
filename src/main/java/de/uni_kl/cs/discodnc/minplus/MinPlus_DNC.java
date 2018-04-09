/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
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

package de.uni_kl.cs.discodnc.minplus;

import java.util.Set;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.minplus.dnc.Convolution_DNC;
import de.uni_kl.cs.discodnc.minplus.dnc.Deconvolution_DNC;

public enum MinPlus_DNC implements MinPlus {
	
	MINPLUS_DNC;
	// --------------------------------------------------------------------------------------------------------------
	// Min-Plus-Operation Dispatching
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// Convolution
	// ------------------------------------------------------------

	// Service Curves
	public ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception {
		return convolve(service_curve_1, service_curve_2, false);
	}

	public ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2,
			boolean tb_rl_optimized) throws Exception {
		return Convolution_DNC.convolve(service_curve_1, service_curve_2, tb_rl_optimized);
	}

	// Java won't let us call this method "convolve" because it does not care about
	// the Sets' types; tells that there's already another method taking the same
	// arguments.
	public Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2) throws Exception {
		return convolve_SCs_SCs(service_curves_1, service_curves_2, false);
	}

	public Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2, boolean tb_rl_optimized) throws Exception {

		if (service_curves_1.isEmpty()) {
			return service_curves_2;
		}
		if (service_curves_2.isEmpty()) {
			return service_curves_1;
		}

			return Convolution_DNC.convolve_SCs_SCs(service_curves_1, service_curves_2, tb_rl_optimized);

	}

	// Arrival Curves
	public ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Convolution_DNC.convolve(arrival_curve_1, arrival_curve_2);
	}

	public ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Convolution_DNC.convolve(arrival_curves);
	}

	// Maximum Service Curves
	public MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2)
			throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Convolution_DNC.convolve(max_service_curve_1, max_service_curve_2);
	}

	// Arrival Curves and Max Service Curves
	public Set<Curve> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve maximum_service_curve) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Convolution_DNC.convolve_ACs_MSC(arrival_curves, maximum_service_curve);
	}

	public Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve extra_gamma_curve) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Convolution_DNC.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);
	}

	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve)
			throws Exception {
		return deconvolve(arrival_curves, service_curve, false);
	}

	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve,
			boolean tb_rl_optimized) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Deconvolution_DNC.deconvolve(arrival_curves, service_curve, tb_rl_optimized);
	}

	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves)
			throws Exception {
		return deconvolve(arrival_curves, service_curves, false);
	}

	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves,
			boolean tb_rl_optimized) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Deconvolution_DNC.deconvolve(arrival_curves, service_curves, tb_rl_optimized);
	}

	public ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception {
		return deconvolve(arrival_curve, service_curve, false);
	}

	public ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve,
			boolean tb_rl_optimized) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Deconvolution_DNC.deconvolve(arrival_curve, service_curve);
	}

	public Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves,
			Set<ServiceCurve> service_curves) throws Exception {
		// DNC operations work with DNC and MPA_RTC curves
			return Deconvolution_DNC.deconvolve_almostConcCs_SCs(curves, service_curves);
	}
	
}
