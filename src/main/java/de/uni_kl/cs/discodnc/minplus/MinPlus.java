package de.uni_kl.cs.discodnc.minplus;

import java.util.Set;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;

public interface MinPlus {

	// Service Curves
	ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) throws Exception;

	ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2, boolean tb_rl_optimized)
			throws Exception;

	// Java won't let us call this method "convolve" because it does not care about
	// the Sets' types; tells that there's already another method taking the same
	// arguments.
	Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2)
			throws Exception;

	Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2,
			boolean tb_rl_optimized) throws Exception;

	// Arrival Curves
	ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) throws Exception;

	ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) throws Exception;

	// Maximum Service Curves
	MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) throws Exception;

	// Arrival Curves and Max Service Curves
	Set<CurvePwAffine> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves, MaxServiceCurve maximum_service_curve)
			throws Exception;

	Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves, MaxServiceCurve extra_gamma_curve)
			throws Exception;

	// ------------------------------------------------------------
	// Deconvolution
	// ------------------------------------------------------------
	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) throws Exception;

	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve, boolean tb_rl_optimized)
			throws Exception;

	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves) throws Exception;

	Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves,
			boolean tb_rl_optimized) throws Exception;

	ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) throws Exception;

	ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve, boolean tb_rl_optimized)
			throws Exception;

	Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<CurvePwAffine> curves, Set<ServiceCurve> service_curves)
			throws Exception;

}