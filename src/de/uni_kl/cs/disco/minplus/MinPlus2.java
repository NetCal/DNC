/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
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
import de.uni_kl.cs.disco.curves.CurvePwAffineFactoryDispatch;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtilsDispatch;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;

import static de.uni_kl.cs.disco.minplus.Convolution.convolve;
import static de.uni_kl.cs.disco.minplus.Deconvolution.deconvolve;

import java.util.HashSet;
import java.util.Set;

public class MinPlus2 {

    public enum Operation {deconvolve, deconvolveTB_RL, deconvolve_mTB_mRL, convolve, convolve_SC_SC_RLs, convolve_SC_SC_Generic, convolve_ACs_MSC, convolve_SCs_SCs, deconvolve_almostConcCs_SCs, convolve_ACs_EGamma}

    private static ArrivalCurve deconv_ac(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactoryDispatch.createArrivalCurve(result.toString());
    }

    private static ArrivalCurve conv_ac(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactoryDispatch.createArrivalCurve(result.toString());
    }

    private static ServiceCurve conv_sc(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactoryDispatch.createServiceCurve(result.toString());
    }

    private static MaxServiceCurve conv_msc(Curve_MPARTC_PwAffine ac, Curve_MPARTC_PwAffine sc) throws Exception {
        ch.ethz.rtc.kernel.Curve result = CurveMath.minPlusDeconv(ac.getRtc_curve(), sc.getRtc_curve());
        return CurvePwAffineFactoryDispatch.createMaxServiceCurve(result.toString());
    }

    public static ArrivalCurve apply(ArrivalCurve ac, ServiceCurve sc, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return deconv_ac((Curve_MPARTC_PwAffine) ac, (Curve_MPARTC_PwAffine) sc);
        }
        switch (o) {
            case deconvolve:
                return deconvolve(ac, sc);
            case deconvolveTB_RL:
                return Deconvolution.deconvolveTB_RL(ac, sc);
            default:
                return null;
        }
    }

    public static ArrivalCurve apply(ArrivalCurve ac, ServiceCurve sc, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return deconv_ac((Curve_MPARTC_PwAffine) ac, (Curve_MPARTC_PwAffine) sc);
        }
        switch (o) {
            case deconvolve:
                return deconvolve(ac, sc, tb_rl_optimized);
            default:
                return null;
        }
    }

    // TODO: Think about this
    public static ArrivalCurve apply(Set<ArrivalCurve> s1, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            if (s1 == null || s1.isEmpty()) {
                return CurvePwAffineFactoryDispatch.createZeroArrivals();
            }
            if (s1.size() == 1) {
                return s1.iterator().next().copy();
            }
            Segment s = new Segment(0, 0, 0);
            SegmentList sl = new SegmentList();
            sl.add(s);
            Curve result = new Curve(sl);
            Curve ac2 = null;
            for (ArrivalCurve arrival_curve_2 : s1) {
                CurvePwAffine result_curves = CurvePwAffineFactoryDispatch.createArrivalCurve(arrival_curve_2.toString());
                Curve_MPARTC_PwAffine c = (Curve_MPARTC_PwAffine) result_curves;
                ac2 = c.getRtc_curve();

                result = CurveMath.minPlusConv(result, ac2);
            }

            return CurvePwAffineFactoryDispatch.createArrivalCurve(ac2.toString());
        }
        switch (o) {
            case convolve:
                return convolve(s1);
            default:
                return null;
        }
    }


    public static ArrivalCurve apply(ArrivalCurve a1, ArrivalCurve a2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_ac((Curve_MPARTC_PwAffine) a1, (Curve_MPARTC_PwAffine) a2);
        }
        switch (o) {
            case convolve:
                return convolve(a1, a2);
            default:
                return null;
        }
    }


    public static ServiceCurve apply(ServiceCurve s1, ServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_sc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return convolve(s1, s2);
            case convolve_SC_SC_Generic:
            		return Convolution.convolve_SC_SC_Generic(s1, s2);
            default:
                return null;
        }
    }


    public static ServiceCurve apply(ServiceCurve s1, ServiceCurve s2, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_sc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return convolve(s1, s2, tb_rl_optimized);
            default:
                return null;
        }
    }


    public static MaxServiceCurve apply(MaxServiceCurve s1, MaxServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return conv_msc((Curve_MPARTC_PwAffine) s1, (Curve_MPARTC_PwAffine) s2);
        }
        switch (o) {
            case convolve:
                return convolve(s1, s2);
            default:
                return null;
        }
    }

    public static Set<ServiceCurve> apply_SCs_SCs(Set<ServiceCurve> s1, Set<ServiceCurve> s2, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            Set<ServiceCurve> results = new HashSet<ServiceCurve>();

            Set<ServiceCurve> clone = new HashSet<ServiceCurve>();
            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroService());
                    return results;
                case 0:
                    break;
                default:
                    return Convolution.convolve_SCs_SCs(s1,s2,tb_rl_optimized);
            }
            switch (MinPlusInputChecks.inputEmptySetCheck(s1, s2)) {
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroService());
                    return results;
                case 0:
                    break;
                default:
                    return Convolution.convolve_SCs_SCs(s1,s2,tb_rl_optimized);
            }

            for (ServiceCurve beta_1 : s1) {
                for (ServiceCurve beta_2 : s2) {
                    Curve_MPARTC_PwAffine s11 = (Curve_MPARTC_PwAffine) beta_1;
                    Curve_MPARTC_PwAffine s12 = (Curve_MPARTC_PwAffine) beta_2;
                    results.add(CurvePwAffineFactoryDispatch.createServiceCurve(CurveMath.add(s11.getRtc_curve(), s12.getRtc_curve()).toString()));
                }
            }
            return results;
        }
        switch (o) {
            case convolve_SCs_SCs:
                return Convolution.convolve_SCs_SCs(s1, s2, tb_rl_optimized);
            default:
                return null;
        }
    }



    public static Set<ArrivalCurve> apply_ACs(Set<ArrivalCurve> s1, Set<ServiceCurve> s2, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                    return results;
                case 2:
                    results.add((ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst());
                    return results;
                default:
            }
            switch (MinPlusInputChecks.inputEmptySetCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                    return results;
                case 2:
                    results.add((ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst());
                    return results;
                default:
            }

            for (ServiceCurve beta : s2) {
                for (ArrivalCurve alpha : s1) {
                    Curve_MPARTC_PwAffine s11 = (Curve_MPARTC_PwAffine) beta;
                    Curve_MPARTC_PwAffine s12 = (Curve_MPARTC_PwAffine) beta;
                    results.add(CurvePwAffineFactoryDispatch.createArrivalCurve(CurveMath.add(s11.getRtc_curve(), s12.getRtc_curve()).toString()));
                }
            }

            return results;
        }
        switch (o) {
            case deconvolve:
                return deconvolve(s1, s2, tb_rl_optimized);
            default:
                return null;
        }
    }

    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, ServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            apply(s1,s2,o,false);
        }
        switch (o) {
            case deconvolve:
                return Deconvolution.deconvolve(s1, s2);
            default:
                return null;
        }
    }

    public static Set<ArrivalCurve> apply(Set<ArrivalCurve> s1, ServiceCurve s2, Operation o, boolean tb_rl_optimized) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                    return results;
                case 2:
                    results.add((ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst());
                    return results;
                default:
            }
            if (s1.isEmpty()) {
                results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                return results;
            }

            Curve_MPARTC_PwAffine st = (Curve_MPARTC_PwAffine) s2;
            Curve s = st.getRtc_curve();
            for (ArrivalCurve alpha : s1) {
                Curve_MPARTC_PwAffine a = (Curve_MPARTC_PwAffine) alpha;
                Curve a1 = a.getRtc_curve();
                results.add(CurvePwAffineFactoryDispatch.createArrivalCurve(CurveMath.minPlusDeconv(a1 ,s).toString()));
            }

            return results;
        }
        switch (o) {
            case deconvolve:
                return Deconvolution.deconvolve(s1, s2, tb_rl_optimized);
            default:
                return null;
        }
    }

    public static Set<ServiceCurve> apply_SCs_SCs(Set<ServiceCurve> s1, Set<ServiceCurve> s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            return apply_SCs_SCs(s1, s2, o, false);
        }
        switch (o) {
            case convolve_ACs_MSC:
                return Convolution.convolve_SCs_SCs(s1, s2);
            default:
                return null;
        }
    }


    public static Set<ArrivalCurve> apply_ACs(Set<ArrivalCurve> s1, Set<ServiceCurve> s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            apply_ACs(s1,s2,o,false);
        }
        switch (o) {
            case deconvolve:
                return Deconvolution.deconvolve(s1, s2);
            default:
                return null;
        }
    }

    public static Set<ArrivalCurve> apply(Set<CurvePwAffine> s1, Set<ServiceCurve> s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                    return results;
                case 2:
                    results.add((ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst());
                    return results;
                default:
            }
            switch (MinPlusInputChecks.inputEmptySetCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                case 3:
                    results.add(CurvePwAffineFactoryDispatch.createZeroArrivals());
                    return results;
                case 2:
                    results.add((ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst());
                    return results;
                default:
            }

            Num latency;
            for (ServiceCurve sc1 : s2) {
                Curve_MPARTC_PwAffine sc2 = (Curve_MPARTC_PwAffine) sc1;
                Curve sc = sc2.getRtc_curve();
                for (CurvePwAffine pwa_c : s1) {
                    latency = pwa_c.getLatency();
                    Curve_MPARTC_PwAffine c1 = new Curve_MPARTC_PwAffine(pwa_c);
                    Curve c = c1.getRtc_curve();
                    c.move(-latency.doubleValue(), 0);
                    Curve dec = CurveMath.minPlusDeconv(c, sc);
                    dec.move(latency.doubleValue(), 0);
                    results.add(CurvePwAffineFactoryDispatch.createArrivalCurve(dec.toString()));
                }
            }

            return results;
        }
        switch (o) {
            case deconvolve_almostConcCs_SCs:
                return Deconvolution.deconvolve_almostConcCs_SCs(s1, s2);
            default:
                return null;
        }
    }


    public static Set<CurvePwAffine> apply_ACs_MSC(Set<ArrivalCurve> s1, MaxServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {

            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 1:
                    return new HashSet<CurvePwAffine>();
                case 2:
                    return Convolution.convolve_ACs_MSC(s1,s2);
                case 3:
                    return new HashSet<CurvePwAffine>();
                case 0:
                default:
                    break;
            }
            if (s1.isEmpty()) {
                return new HashSet<CurvePwAffine>();
            }

            Num msc_latency = s2.getLatency();
            Set<CurvePwAffine> result = new HashSet<CurvePwAffine>();

            //TODO: won't work
            Curve_MPARTC_PwAffine msc_as_ac = (Curve_MPARTC_PwAffine) CurvePwAffineUtilsDispatch.removeLatency(s2); // Abuse the ArrivalCurve class here for convenience.
            Curve c = msc_as_ac.getRtc_curve();
            for (ArrivalCurve ac : s1) {
                Curve_MPARTC_PwAffine a = (Curve_MPARTC_PwAffine) ac;
                Curve c2 = a.getRtc_curve();
                Curve curve = CurveMath.minPlusConv(c2, c);
                curve.move(msc_latency.doubleValue(), 0);
                result.add(CurvePwAffineFactoryDispatch.createArrivalCurve(c.toString()));
            }

            return result;
        }
        switch (o) {
            case convolve_ACs_MSC:
                return Convolution.convolve_ACs_MSC(s1, s2);
            default:
                return null;
        }
    }


    public static Set<ArrivalCurve> apply_ACs(Set<ArrivalCurve> s1, MaxServiceCurve s2, Operation o) throws Exception {
        if (CalculatorConfig.getInstance().getCurveClass().equals(CalculatorConfig.CurveClass.MPA_RTC)) {
            switch (MinPlusInputChecks.inputNullCheck(s1, s2)) {
                case 0:
                    break;
                case 1:
                    return new HashSet<ArrivalCurve>();
                case 2:
                    Set<ArrivalCurve> clone = new HashSet<ArrivalCurve>();

                    for (ArrivalCurve ac : s1) {
                        clone.add(ac.copy());
                    }
                    return clone;
                case 3:
                    return new HashSet<ArrivalCurve>();
                default:
            }
            if (s1.isEmpty()) {
                return new HashSet<ArrivalCurve>();
            }

            if (s2.getLatency().gtZero()) {
                throw new Exception("Cannot convolve with an extra gamma curve with latency");
            }

            Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
            Curve_MPARTC_PwAffine extra_gamma_as_ac = (Curve_MPARTC_PwAffine) CurvePwAffineFactoryDispatch.createArrivalCurve(s2); // Abuse the ArrivalCurve class here for convenience.
            Curve c = extra_gamma_as_ac.getRtc_curve();
            for (ArrivalCurve ac : s1) {
                Curve_MPARTC_PwAffine curve2 = (Curve_MPARTC_PwAffine) ac;
                Curve cs = curve2.getRtc_curve();
                result.add(CurvePwAffineFactoryDispatch.createArrivalCurve(CurveMath.minPlusConv(c, cs).toString()));
            }

            return result;
        }
        switch (o) {
            case convolve_ACs_EGamma:
                return Convolution.convolve_ACs_EGamma(s1, s2);
            default:
                return null;
        }
    }

}
