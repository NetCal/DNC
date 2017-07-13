package de.uni_kl.disco.curves;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.CurveMath;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.disco.minplus.Convolution;
import de.uni_kl.disco.minplus.Deconvolution;
import de.uni_kl.disco.nc.CalculatorConfig;

import java.util.Collection;
import java.util.Set;


/**
 * Created by philipp on 7/12/17.
 */
public class OperatorUtils {

    public enum Operation {deconvolve, deconvolveTB_RL, deconvolve_mTB_mRL, convolve, convolve_SC_SC_RLs, convolve_SC_SC_Generic, convolve_ACs_MSC, convolve_SCs_SCs, deconvolve_almostConcCs_SCs, convolve_ACs_EGamma}

    public static ArrivalCurve deconv_ac(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.maxPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactory.createArrivalCurve(result.toString());
    }

    public static ArrivalCurve conv_ac(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.maxPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactory.createArrivalCurve(result.toString());
    }

    public static ServiceCurve conv_sc(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.maxPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactory.createServiceCurve(result.toString());
    }

    public static MaxServiceCurve conv_msc(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.maxPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactory.createMaxServiceCurve(result.toString());
    }

    public static ArrivalCurve apply(ArrivalCurve ac, ServiceCurve sc, Operation o) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return deconv_ac((Curve_MPARTC_PwAffine) ac, (Curve_MPARTC_PwAffine) sc);
        }
        switch (o) {
            case deconvolve:
                return Deconvolution.deconvolve(ac, sc);
            case deconvolveTB_RL:
                return Deconvolution.deconvolveTB_RL(ac, sc);
            default:
                return null;
        }
    }

    public static ArrivalCurve apply(ArrivalCurve ac, ServiceCurve sc, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return deconv_ac((Curve_MPARTC_PwAffine) ac, (Curve_MPARTC_PwAffine) sc);
        }
        switch (o) {
            case deconvolve:
                return Deconvolution.deconvolve(ac, sc, tb_rl_optimized);
            default:
                return null;
        }
    }


    //TODO: Denk mal hier drüber nach.....
    public static ArrivalCurve apply(Set<ArrivalCurve> s1, Operation o) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            if (s1 == null || s1.isEmpty()) {
                return CurvePwAffineFactory.createZeroArrivals();
            }
            if (s1.size() == 1) {
                return s1.iterator().next().copy();
            }
            Segment s = new Segment(0,0,0);
            SegmentList sl = new SegmentList();
            sl.add(s);
            Curve result = new Curve(sl);
            Curve ac2 = null;
            for (ArrivalCurve arrival_curve_2 : s1) {
                CurvePwAffine result_curves = CurvePwAffineFactory.createArrivalCurve(arrival_curve_2.toString());
                Curve_MPARTC_PwAffine c = (Curve_MPARTC_PwAffine) result_curves;
                ac2 = c.getRtc_curve();

                result = CurveMath.minPlusConv(result, ac2);
            }

            return CurvePwAffineFactory.createArrivalCurve(ac2.toString());
        }
        switch (o) {
            case convolve:
                return Convolution.convolve(s1);
            default:
                return null;
        }
    }


/*
    public static ArrivalCurve apply(ArrivalCurve a1, ArrivalCurve a2, Operation o) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_ac((Curve_MPARTC_PwAffine) a1, (Curve_MPARTC_PwAffine) a2);
        }
        switch (o) {
            case convolve:
                return Convolution.convolve(a1, a2);
            default:
                return null;
        }
    }


    public static ServiceCurve apply(ServiceCurve s1, ServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_sc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return Convolution.convolve(s1, s2);
            case convolve_SC_SC_Generic:
                Convolution.convolve_SC_SC_Generic(s1, s2);
            default:
                return null;
        }
    }


    public static ServiceCurve apply(ServiceCurve s1, ServiceCurve s2, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_sc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return Convolution.convolve(s1, s2, tb_rl_optimized);
            default:
                return null;
        }
    }


    public static MaxServiceCurve apply(MaxServiceCurve s1, MaxServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_msc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return Convolution.convolve(s1, s2);
            default:
                return null;
        }
    }

    public static Set<CurvePwAffine> apply(Set<ArrivalCurve> s1, MaxServiceCurve s2, Operation o) {

        public static Set<CurvePwAffine> convolve_ACs_MSC (Set < ArrivalCurve > arrival_curves, MaxServiceCurve
        maximum_service_curve) throws Exception
    }


    public static Set<ServiceCurve> apply(Set<ServiceCurve> s1, Set<ServiceCurve> s2, Operation o) {
        public static Set<ServiceCurve> convolve_SCs_SCs
        (Set < ServiceCurve > service_curves_1, Set < ServiceCurve > service_curves_2)

    }


    public static Set<ServiceCurve> apply(Set<ServiceCurve> s1, Set<ServiceCurve> s2, Operation o, boolean tb_rl_optimized) {
        public static Set<ServiceCurve> convolve_SCs_SCs
        (Set < ServiceCurve > service_curves_1, Set < ServiceCurve > service_curves_2,boolean tb_rl_optimized)

    }


    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, ServiceCurve s2, Operation o) {

        public static Set<ArrivalCurve> deconvolve (Set < ArrivalCurve > arrival_curves, ServiceCurve service_curve)
    }

    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, ServiceCurve s2, Operation o, boolean tb_rl_optimized) {

        public static Set<ArrivalCurve> deconvolve (Set < ArrivalCurve > arrival_curves, ServiceCurve service_curve,
        boolean tb_rl_optimized)
    }


    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, Set<ServiceCurve> s2, Operation o, boolean tb_rl_optimized) {

        public static Set<ArrivalCurve> deconvolve
        (Set < ArrivalCurve > arrival_curves, Set < ServiceCurve > service_curves,boolean tb_rl_optimized)
    }


    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, Set<ServiceCurve> s2, Operation o) {

        public static Set<ArrivalCurve> deconvolve
        (Set < ArrivalCurve > arrival_curves, Set < ServiceCurve > service_curves)
    }

    public static Set<ArrivalCurve> apply(Set<CurvePwAffine> s1, Set<ServiceCurve> s2, Operation o) {

        public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs
        (Set < CurvePwAffine > curves, Set < ServiceCurve > service_curves)
    }


    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, MaxServiceCurve s2, Operation o) {

        public static Set<ArrivalCurve> convolve_ACs_EGamma (Set < ArrivalCurve > arrival_curves, MaxServiceCurve
        extra_gamma_curve) throws Exception
    }
*/
}
