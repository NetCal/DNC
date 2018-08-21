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

package de.uni_kl.cs.discodnc.minplus.dnc.pwaffine;

import java.util.Set;

// Due to name collisions, these classes are not imported,
// they are referenced by their fully qualified names.
//import ch.ethz.rtc.kernel.Curve;
//import Curve;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.minplus.MinPlus;

public enum MinPlus_DNC_PwAffine implements MinPlus {
	MINPLUS_DNC_PWAFFINE;
  
	// --------------------------------------------------------------------------------------------------------------
	// Min-Plus-Operation Dispatching
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// Convolution
	// ------------------------------------------------------------

	// Service Curves
	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve(de.uni_kl.cs.discodnc.curves.ServiceCurve, de.uni_kl.cs.discodnc.curves.ServiceCurve)
	 */
	@Override
	public ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception {
		return Convolution_DNC_PwAffine.convolve(service_curve_1, service_curve_2);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ServiceCurve> convolve(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2) throws Exception {
		return Convolution_DNC_PwAffine.convolve(service_curves_1, service_curves_2);
	}

	// Arrival Curves
	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve(de.uni_kl.cs.discodnc.curves.ArrivalCurve, de.uni_kl.cs.discodnc.curves.ArrivalCurve)
	 */
	@Override
	public ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception {
		return Convolution_DNC_PwAffine.convolve(arrival_curve_1, arrival_curve_2);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve(java.util.Set)
	 */
	@Override
	public ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception {
		return Convolution_DNC_PwAffine.convolve(arrival_curves);
	}

	// Maximum Service Curves
	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve(de.uni_kl.cs.discodnc.curves.MaxServiceCurve, de.uni_kl.cs.discodnc.curves.MaxServiceCurve)
	 */
	@Override
	public MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2)
			throws Exception {
		return Convolution_DNC_PwAffine.convolve(max_service_curve_1, max_service_curve_2);
	}

	// Arrival Curves and Max Service Curves
	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve_ACs_MSC(java.util.Set, de.uni_kl.cs.discodnc.curves.MaxServiceCurve)
	 */
	@Override
	public Set<Curve> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve maximum_service_curve) throws Exception {
		return Convolution_DNC_PwAffine.convolve_ACs_MSC(arrival_curves, maximum_service_curve);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#convolve_ACs_EGamma(java.util.Set, de.uni_kl.cs.discodnc.curves.MaxServiceCurve)
	 */
	@Override
	public Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve extra_gamma_curve) throws Exception {
		return Convolution_DNC_PwAffine.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);
	}

	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#deconvolve(java.util.Set, de.uni_kl.cs.discodnc.curves.ServiceCurve)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve)
			throws Exception {
		return Deconvolution_DNC_PwAffine.deconvolve(arrival_curves, service_curve);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#deconvolve(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves)
			throws Exception {
		return Deconvolution_DNC_PwAffine.deconvolve(arrival_curves, service_curves);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#deconvolve(de.uni_kl.cs.discodnc.curves.ArrivalCurve, de.uni_kl.cs.discodnc.curves.ServiceCurve)
	 */
	@Override
	public ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception {
		return deconvolve(arrival_curve, service_curve, false);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#deconvolve(de.uni_kl.cs.discodnc.curves.ArrivalCurve, de.uni_kl.cs.discodnc.curves.ServiceCurve, boolean)
	 */
	@Override
	public ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve,
			boolean tb_rl_optimized) throws Exception {
		return Deconvolution_DNC_PwAffine.deconvolve(arrival_curve, service_curve);
	}

	/* (non-Javadoc)
	 * @see de.uni_kl.cs.discodnc.minplus.IMinPlus#deconvolve_almostConcCs_SCs(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves,
			Set<ServiceCurve> service_curves) throws Exception {
		return Deconvolution_DNC_PwAffine.deconvolve_almostConcCs_SCs(curves, service_curves);
	}
	
}
