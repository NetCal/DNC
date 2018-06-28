package de.uni_kl.cs.discodnc.minplus;

import de.uni_kl.cs.discodnc.curves.*;
import de.uni_kl.cs.discodnc.minplus.dnc.Convolution_DNC;
import de.uni_kl.cs.discodnc.minplus.dnc.Deconvolution_DNC;
import de.uni_kl.cs.discodnc.minplus.dnc_affine.dnc.Convolution_DNC_affine;
import de.uni_kl.cs.discodnc.minplus.dnc_affine.dnc.Deconvolution_DNC_affine;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig;

import java.util.Set;

public class Dispatcher {

    /*
        MinPlus Operations
     */





    public static int inputNullCheck(Object obj1, Object obj2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return MinPlus_affine.inputNullCheck(obj1, obj2);

            case DNC:
            default:
                return MinPlus.inputNullCheck(obj1, obj2);
        }
    }

    public static int inputDelayedInfiniteBurstCheck(Curve curve_1, Curve curve_2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return MinPlus_affine.inputDelayedInfiniteBurstCheck(curve_1, curve_2);

            case DNC:
            default:
                return MinPlus.inputDelayedInfiniteBurstCheck(curve_1, curve_2);
        }
    }

    @SuppressWarnings("rawtypes")
    public static int inputEmptySetCheck(Set set1, Set set2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return MinPlus_affine.inputEmptySetCheck(set1, set2);

            case DNC:
            default:
                return MinPlus.inputEmptySetCheck(set1, set2);
        }
    }

    /*
        Deconvolution Methods
     */
    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curves, service_curve);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curves, service_curve);
        }
    }

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve,
                                               boolean tb_rl_optimized) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curves, service_curve, tb_rl_optimized);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curves, service_curve, tb_rl_optimized);
        }

    }

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curves, service_curves);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curves, service_curves);
        }

    }

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves,
                                               boolean tb_rl_optimized) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curves, service_curves, tb_rl_optimized);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curves, service_curves, tb_rl_optimized);
        }

    }

    public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curve, service_curve);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curve, service_curve);
        }

    }

    public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve,
                                          boolean tb_rl_optimized) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve(arrival_curve, service_curve, tb_rl_optimized);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve(arrival_curve, service_curve, tb_rl_optimized);
        }

    }

    public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<CurvePwAffine> curves,
                                                                Set<ServiceCurve> service_curves) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Deconvolution_DNC_affine.deconvolve_almostConcCs_SCs(curves, service_curves);

            case DNC:
            default:
                return Deconvolution_DNC.deconvolve_almostConcCs_SCs(curves, service_curves);
        }

    }


    /*
        Convolution Methods
     */
    public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve(service_curve_1, service_curve_2);

            case DNC:
            default:
                return Convolution_DNC.convolve(service_curve_1, service_curve_2);
        }
    }

    public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2,
                                        boolean tb_rl_optimized) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve(service_curve_1, service_curve_2, tb_rl_optimized);

            case DNC:
            default:
                return Convolution_DNC.convolve(service_curve_1, service_curve_2, tb_rl_optimized);
        }
    }

    public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
                                                     Set<ServiceCurve> service_curves_2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve_SCs_SCs(service_curves_1, service_curves_2);

            case DNC:
            default:
                return Convolution_DNC.convolve_SCs_SCs(service_curves_1, service_curves_2);
        }
    }

    public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1,
                                                     Set<ServiceCurve> service_curves_2, boolean tb_rl_optimized) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve_SCs_SCs(service_curves_1, service_curves_2, tb_rl_optimized);

            case DNC:
            default:
                return Convolution_DNC.convolve_SCs_SCs(service_curves_1, service_curves_2, tb_rl_optimized);
        }
    }

    public static ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve(arrival_curve_1, arrival_curve_2);

            case DNC:
            default:
                return Convolution_DNC.convolve(arrival_curve_1, arrival_curve_2);
        }
    }

    public static ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve(arrival_curves);

            case DNC:
            default:
                return Convolution_DNC.convolve(arrival_curves);
        }
    }

    public static MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve(max_service_curve_1, max_service_curve_2);

            case DNC:
            default:
                return Convolution_DNC.convolve(max_service_curve_1, max_service_curve_2);
        }
    }

    public static Set<CurvePwAffine> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves,
                                              MaxServiceCurve maximum_service_curve) throws Exception {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve_ACs_MSC(arrival_curves, maximum_service_curve);

            case DNC:
            default:
                return Convolution_DNC.convolve_ACs_MSC(arrival_curves, maximum_service_curve);
        }
    }

    public static Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves,
                                                        MaxServiceCurve extra_gamma_curve) throws Exception {
        switch (CalculatorConfig.getInstance().getCurveImpl()) {
            case DNC_AFFINE:
                return Convolution_DNC_affine.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);

            case DNC:
            default:
                return Convolution_DNC.convolve_ACs_EGamma(arrival_curves, extra_gamma_curve);
        }
    }



}
