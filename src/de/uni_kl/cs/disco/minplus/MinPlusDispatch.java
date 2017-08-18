/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
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

package de.uni_kl.cs.disco.minplus;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.CurveMath;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactory;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.minplus.dnc.Convolution_DNC;
import de.uni_kl.cs.disco.minplus.dnc.Deconvolution_DNC;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.nc.CalculatorConfig.CurveClass;
import de.uni_kl.cs.disco.nc.CalculatorConfig.OperationClass;

import java.util.HashSet;
import java.util.Set;

public abstract class MinPlusDispatch {
	// ------------------------------------------------------------
	// Convolution
	// ------------------------------------------------------------

	// Service Curves
	public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception {
		return convolve(service_curve_1, service_curve_2, false);
	}

	public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2,
			boolean tb_rl_optimized) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve(service_curve_1, service_curve_2, tb_rl_optimized);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE
			ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusConv(
					((Curve_MPARTC_PwAffine) service_curve_1).getRtc_curve(),
					((Curve_MPARTC_PwAffine) service_curve_2).getRtc_curve());

			return CurvePwAffineFactory.getCurveFactory().createServiceCurve(result.toString());
		}
	}

	// Java won't let me call this method "convolve" because it does not care about
	// the Sets' types; tells that there's already another method taking the same
	// arguments.
	public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2) throws Exception {
		return convolve_SCs_SCs(service_curves_1, service_curves_2, false);
	}

	public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
			Set<ServiceCurve> service_curves_2, boolean tb_rl_optimized) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve_SCs_SCs(service_curves_1, service_curves_2, tb_rl_optimized);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<ServiceCurve> results = new HashSet<ServiceCurve>();

			for (ServiceCurve beta_1 : service_curves_1) {
				for (ServiceCurve beta_2 : service_curves_2) {

					Curve_MPARTC_PwAffine s11 = (Curve_MPARTC_PwAffine) beta_1;
					Curve_MPARTC_PwAffine s12 = (Curve_MPARTC_PwAffine) beta_2;

					results.add(CurvePwAffineFactory.getCurveFactory().createServiceCurve(
							CurveMath.minPlusConv(s11.getRtc_curve(), s12.getRtc_curve()).toString()));
				}
			}
			return results;
		}
	}

	// Arrival Curves
	public static ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve(arrival_curve_1, arrival_curve_2);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE
			ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusConv(
					((Curve_MPARTC_PwAffine) arrival_curve_1).getRtc_curve(),
					((Curve_MPARTC_PwAffine) arrival_curve_2).getRtc_curve());

			return CurvePwAffineFactory.getCurveFactory().createArrivalCurve(result.toString());
		}
	}

	public static ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve(arrival_curves);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			// TODO Double check
			if (arrival_curves == null || arrival_curves.isEmpty()) {
				return CurvePwAffineFactory.getCurveFactory().createZeroArrivals();
			}
			if (arrival_curves.size() == 1) {
				return arrival_curves.iterator().next().copy();
			}
			Segment s = new Segment(0, 0, 0);
			SegmentList sl = new SegmentList();
			sl.add(s);
			Curve result = new Curve(sl);
			Curve ac2 = null;
			for (ArrivalCurve arrival_curve_2 : arrival_curves) {
				CurvePwAffine result_curves = CurvePwAffineFactory.getCurveFactory()
						.createArrivalCurve(arrival_curve_2.toString());
				Curve_MPARTC_PwAffine c = (Curve_MPARTC_PwAffine) result_curves;
				ac2 = c.getRtc_curve();

				result = CurveMath.minPlusConv(result, ac2);
			}

			return CurvePwAffineFactory.getCurveFactory().createArrivalCurve(ac2.toString());
		}
	}

	// Maximum Service Curves
	public static MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2)
			throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve(max_service_curve_1, max_service_curve_2);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE
			ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusConv(
					((Curve_MPARTC_PwAffine) max_service_curve_1).getRtc_curve(),
					((Curve_MPARTC_PwAffine) max_service_curve_2).getRtc_curve());

			return CurvePwAffineFactory.getCurveFactory().createMaxServiceCurve(result.toString());
		}
	}

	// Arrival Curves and Max Service Curves
	public static Set<CurvePwAffine> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve maximum_service_curve) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve_ACs_MSC(arrival_curves, maximum_service_curve);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<CurvePwAffine> results = new HashSet<CurvePwAffine>();

			Curve_MPARTC_PwAffine msc_mpa_rtc = (Curve_MPARTC_PwAffine) maximum_service_curve;
			for (ArrivalCurve alpha_tmp : arrival_curves) {
				// Do not mind the semantics "Arrival Curve"
				results.add(CurvePwAffineFactory.getCurveFactory().createArrivalCurve(CurveMath
						.minPlusConv(((Curve_MPARTC_PwAffine) alpha_tmp).getRtc_curve(), msc_mpa_rtc.getRtc_curve())
						.toString()));
			}
			return results;
		}
	}

	public static Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves,
			MaxServiceCurve extra_gamma_curve) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Convolution_DNC.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

			Curve_MPARTC_PwAffine egamma_mpa_rtc = (Curve_MPARTC_PwAffine) extra_gamma_curve;
			for (ArrivalCurve alpha_tmp : arrival_curves) {
				results.add(CurvePwAffineFactory.getCurveFactory().createArrivalCurve(CurveMath
						.minPlusConv(((Curve_MPARTC_PwAffine) alpha_tmp).getRtc_curve(), egamma_mpa_rtc.getRtc_curve())
						.toString()));
			}
			return results;
		}
	}

	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve)
			throws Exception {
		return deconvolve(arrival_curves, service_curve, false);
	}

	public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve,
			boolean tb_rl_optimized) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Deconvolution_DNC.deconvolve(arrival_curves, service_curve, tb_rl_optimized);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

			Curve_MPARTC_PwAffine beta_mpa_rtc = (Curve_MPARTC_PwAffine) service_curve;
			for (ArrivalCurve alpha_tmp : arrival_curves) {
				results.add(CurvePwAffineFactory.getCurveFactory().createArrivalCurve(CurveMath
						.minPlusDeconv(((Curve_MPARTC_PwAffine) alpha_tmp).getRtc_curve(), beta_mpa_rtc.getRtc_curve())
						.toString()));
			}
			return results;
		}
	}

	public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves)
			throws Exception {
		return deconvolve(arrival_curves, service_curves, false);
	}

	public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves,
			boolean tb_rl_optimized) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Deconvolution_DNC.deconvolve(arrival_curves, service_curves, tb_rl_optimized);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

			for (ServiceCurve beta_tmp : service_curves) {
				for (ArrivalCurve alpha_tmp : arrival_curves) {
					results.add(CurvePwAffineFactory.getCurveFactory().createArrivalCurve(
							CurveMath.minPlusDeconv(((Curve_MPARTC_PwAffine) alpha_tmp).getRtc_curve(),
									((Curve_MPARTC_PwAffine) beta_tmp).getRtc_curve()).toString()));
				}
			}
			return results;
		}
	}

	public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception {
		return deconvolve(arrival_curve, service_curve, false);
	}

	public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve,
			boolean tb_rl_optimized) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Deconvolution_DNC.deconvolve(arrival_curve, service_curve);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE
			ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusDeconv(
					((Curve_MPARTC_PwAffine) arrival_curve).getRtc_curve(),
					((Curve_MPARTC_PwAffine) service_curve).getRtc_curve());

			return CurvePwAffineFactory.getCurveFactory().createArrivalCurve(result.toString());
		}
	}

	public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<CurvePwAffine> curves,
			Set<ServiceCurve> service_curves) throws Exception {
		if (CalculatorConfig.getInstance().getOperationClass().equals(OperationClass.DNC) // DNC operations work with
																							// DNC and MPA_RTC curves
				|| CalculatorConfig.getInstance().getCurveClass().equals(CurveClass.DNC)) { // NATIVE operation on 
																							// DNC curves
			return Deconvolution_DNC.deconvolve_almostConcCs_SCs(curves, service_curves);

		} else { // Must be CurveClass.MPA_RTC + OpertionClass.NATIVE

			Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

			for (ServiceCurve beta_tmp : service_curves) {
				for (CurvePwAffine c_tmp : curves) {
					// Do not mind the semantics "Arrival Curve"
					results.add(CurvePwAffineFactory.getCurveFactory()
							.createArrivalCurve(CurveMath.minPlusDeconv(((Curve_MPARTC_PwAffine) c_tmp).getRtc_curve(),
									((Curve_MPARTC_PwAffine) beta_tmp).getRtc_curve()).toString()));
				}
			}
			return results;
		}
	}
}
