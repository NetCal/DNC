/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.algebra.disco;

import java.util.Set;

import org.networkcalculus.dnc.algebra.MinPlus;
import org.networkcalculus.dnc.algebra.disco.con_pw_affine.Deconvolution_Disco_ConPwAffine;
import org.networkcalculus.dnc.algebra.disco.pw_affine.Convolution_Disco_PwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;

public enum MinPlus_Disco_ConPwAffine implements MinPlus {
	MINPLUS_DISCO_CONPWAFFINE;
	
	// --------------------------------------------------------------------------------------------------------------
	// Min-Plus-Operation Dispatching
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// Convolution
	// ------------------------------------------------------------

	// Service Curves
	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve(org.networkcalculus.dnc.curves.ServiceCurve, org.networkcalculus.dnc.curves.ServiceCurve)
	 */
	@Override
	public ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception {
		return Convolution_Disco_PwAffine.convolve(service_curve_1, service_curve_2);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ServiceCurve> convolve(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2) throws Exception {
		return Convolution_Disco_PwAffine.convolve(service_curves_1, service_curves_2);
	}

	// Arrival Curves
	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve(org.networkcalculus.dnc.curves.ArrivalCurve, org.networkcalculus.dnc.curves.ArrivalCurve)
	 */
	@Override
	public ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception {
		return Convolution_Disco_PwAffine.convolve(arrival_curve_1, arrival_curve_2);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve(java.util.Set)
	 */
	@Override
	public ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception {
		return Convolution_Disco_PwAffine.convolve(arrival_curves);
	}

	// Maximum Service Curves
	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve(org.networkcalculus.dnc.curves.MaxServiceCurve, org.networkcalculus.dnc.curves.MaxServiceCurve)
	 */
	@Override
	public MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2)
			throws Exception {
		return Convolution_Disco_PwAffine.convolve(max_service_curve_1, max_service_curve_2);
	}

	// Arrival Curves and Max Service Curves
	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve_ACs_MSC(java.util.Set, org.networkcalculus.dnc.curves.MaxServiceCurve)
	 */
	@Override
	public Set<Curve> convolve_ACs_MaxSC(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve maximum_service_curve) throws Exception {
		return Convolution_Disco_PwAffine.convolve_ACs_MSC(arrival_curves, maximum_service_curve);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#convolve_ACs_EGamma(java.util.Set, org.networkcalculus.dnc.curves.MaxServiceCurve)
	 */
	@Override
	public Set<ArrivalCurve> convolve_ACs_MaxScRate(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve extra_gamma_curve) throws Exception {
		return Convolution_Disco_PwAffine.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);
	}

	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#deconvolve(java.util.Set, org.networkcalculus.dnc.curves.ServiceCurve)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve)
			throws Exception {
		return Deconvolution_Disco_ConPwAffine.deconvolve(arrival_curves, service_curve);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#deconvolve(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves)
			throws Exception {
		return Deconvolution_Disco_ConPwAffine.deconvolve(arrival_curves, service_curves);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#deconvolve(org.networkcalculus.dnc.curves.ArrivalCurve, org.networkcalculus.dnc.curves.ServiceCurve)
	 */
	@Override
	public ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception {
		return Deconvolution_Disco_ConPwAffine.deconvolve(arrival_curve, service_curve);
	}

	/* (non-Javadoc)
	 * @see org.networkcalculus.dnc.algebra.IMinPlus#deconvolve_almostConcCs_SCs(java.util.Set, java.util.Set)
	 */
	@Override
	public Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves,
			Set<ServiceCurve> service_curves) throws Exception {
		return Deconvolution_Disco_ConPwAffine.deconvolve_almostConcCs_SCs(curves, service_curves);
	}
	
}
