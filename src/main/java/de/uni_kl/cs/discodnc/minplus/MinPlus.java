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

public interface MinPlus {

	// ------------------------------------------------------------
	// Convolution
	// ------------------------------------------------------------
	
	// Service Curves
	ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception;

	// Java won't let us call this method "convolve" because it does not care about
	// the Sets' types; tells that there's already another method taking the same
	// arguments.
	Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2)
			throws Exception;

	// Arrival Curves
	ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception;

	ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception;

	// Maximum Service Curves
	MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) throws Exception;

	// Arrival Curves and Max Service Curves
	Set<Curve> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves, MaxServiceCurve maximum_service_curve)
			throws Exception;
	Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves, MaxServiceCurve extra_gamma_curve)
			throws Exception;
	
	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) throws Exception;

	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves) throws Exception;

	ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception;

	ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve, boolean tb_rl_optimized)
			throws Exception;

	Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves, Set<ServiceCurve> service_curves)
			throws Exception;
}