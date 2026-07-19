package org.networkcalculus.dnc.utils;


import jdk.jshell.spi.ExecutionControl;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.backend.CurveUtils;
import org.networkcalculus.dnc.backend.Segment;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MinPlusUtils {

    public static Curve convolve(Curve a, Curve b) {

        switch (CheckUtils.inputNullCheck(a, b)) {
            case 1:
                return b;
            case 2:
                return a;
            case 3:
                return CurveFactory.createPeakRate(Number.ZERO);
            default:
                break;
        }

        // Delayed Infinite Burst
        if (CurveFunctionsUtils.isDelayedInfiniteBurst(a) && CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delay = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a).add(CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b));
            return CurveFactory.createDelayedInfiniteBurst(delay);
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(a)) {
            return CurveUtils.shiftRight(b, CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a));
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            return CurveUtils.shiftRight(a, CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b));
        }
        return CurveUtils.convolution(a, b);
    }

    public static Curve convolve(Set<Curve> curves) {

        if (curves == null || curves.isEmpty()) {
            return CurveFactory.createPeakRate(Number.ZERO);
        }
        if (curves.size() == 1) {
            return curves.iterator().next();
        }

        List<Curve> curveList = new ArrayList<>(curves);

        Curve curve_result = curveList.getFirst();
        for (int i = 1; i < curveList.size(); i++) {
            curve_result = convolve(curve_result, curveList.get(i));
        }
        return curve_result;
    }

    public static Set<Curve> convolve(Set<Curve> set1, Set<Curve> set2) {
        Set<Curve> results = new HashSet<>();

        switch (CheckUtils.inputNullCheck(set1, set2)) {
            case 1:
                return cloneSet(set2);
            case 2:
                return cloneSet(set1);
            case 3:
                results.add(CurveFactory.createPeakRate(Number.ZERO));
                return results;
            default:
                break;
        }

        switch (CheckUtils.inputEmptySetCheck(set1, set2)) {
            case 1:
                return cloneSet(set2);
            case 2:
                return cloneSet(set1);
            case 3:
                results.add(CurveFactory.createPeakRate(Number.ZERO));
                return results;
            default:
                break;
        }

        Set<Curve> result = new HashSet<>();
        for (Curve c1 : set1) {
            for (Curve c2 : set2) {
                result.add(convolve(c1, c2));
            }
        }
        return result;
    }

    private static Set<Curve> cloneSet(Set<Curve> set) {
        if (set == null)
            return new HashSet<>();
        return new HashSet<>(set);
    }

    public static Set<Curve> convolve_ACs_MSC(Set<Curve> arrivalCurves, Curve maxServiceCurve) {

        switch (CheckUtils.inputNullCheck(arrivalCurves, maxServiceCurve)) {
            case 1:
                return new HashSet<Curve>();
            case 2:
                Set<Curve> clone = new HashSet<>();

                for (Curve ac : arrivalCurves) {
                    clone.add(ac);
                }
                return clone;
            case 3:
                return new HashSet<Curve>();
            case 0:
            default:
                break;
        }
        if (arrivalCurves.isEmpty()) {
            return new HashSet<Curve>();
        }

//        Number msc_latency = CurveFunctionsUtils.getLatency(maxServiceCurve);

//        Curve msc_as_ac = CurveUtils.removeLatency(maxServiceCurve);

        Set<Curve> result = new HashSet<>();
        for (Curve ac : arrivalCurves) {
            Curve conv = MinPlusUtils.convolve(ac, maxServiceCurve);
            result.add(conv);
//            Curve shifted = CurveUtils.shiftRight(conv, msc_latency);
//            result.add(shifted);
        }
        return result;
    }

    public static Set<Curve> convolve_ACs_EGamma(Set<Curve> arrivalCurves, Curve extraGamma) throws Exception {

        switch (CheckUtils.inputNullCheck(arrivalCurves, extraGamma)) {
            case 0:
                break;
            case 1:
                return new HashSet<Curve>();
            case 2:
                Set<Curve> clone = new HashSet<Curve>();

                for (Curve ac : arrivalCurves) {
                    clone.add(ac);
                }
                return clone;
            case 3:
                return new HashSet<Curve>();
            default:
        }
        if (arrivalCurves.isEmpty()) {
            return new HashSet<Curve>();
        }

        if (CurveFunctionsUtils.getLatency(extraGamma).gt(Number.ZERO)) {
            throw new Exception("Cannot convolve with an extra gamma curve with latency");
        }

        Set<Curve> result = new HashSet<>();
        for (Curve ac : arrivalCurves) {
            result.add(MinPlusUtils.convolve(ac, extraGamma));
        }
        return result;
    }

    public static Curve deconvolve(Curve a, Curve b) {

        switch (CheckUtils.inputNullCheck(a, b)) {
            case 1, 3:
                return CurveFactory.createPeakRate(Number.ZERO);
            case 2:
                return CurveFactory.createDelayedInfiniteBurst(Number.ZERO);
            default:
                break;
        }
//        if (CurveFunctionsUtils.isTokenBucket(a) && (CurveFunctionsUtils.isRateLatency(b))) {
//            return deconvolveTBRL(a, b);
//        }

        // Delayed Infinite Burst
        if (CurveFunctionsUtils.isDelayedInfiniteBurst(a) && CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delay1 = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            Number delay2 = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b);
            Number delay = Number.ZERO.max(delay1.sub(delay2));
            return CurveFactory.createDelayedInfiniteBurst(delay);
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(a)) {
            return CurveUtils.shiftLeft(b, CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a));
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            return CurveUtils.shiftLeft(a, CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b));
        }

        return CurveUtils.deconvolution(a, b);
    }

    public static Set<Curve> deconvolve(Set<Curve> set1, Set<Curve> set2) {
        Set<Curve> results = new HashSet<>();

        switch (CheckUtils.inputNullCheck(set1, set2)) {
            case 2:
                results.add(
                        CurveFactory.createDelayedInfiniteBurst(Number.ZERO));
                return results;
            case 1:
            case 3:
                results.add(CurveFactory.createPeakRate(Number.ZERO));
                return results;
            default:
                break;
        }

        switch (CheckUtils.inputEmptySetCheck(set1, set2)) {
            case 2:
                results.add(
                        CurveFactory.createDelayedInfiniteBurst(Number.ZERO));
                return results;
            case 1:
            case 3:
                results.add(CurveFactory.createPeakRate(Number.ZERO));
                return results;
            default:
                break;
        }

        Set<Curve> result = new HashSet<>();
        for (Curve c2 : set2) {
            for (Curve c1 : set1) {
                result.add(deconvolve(c1, c2));
            }
        }
        return result;
    }

//    private static Curve deconvolveTBRL(Curve tbCurve, Curve rlCurve) {
//
//        Segment stb = tbCurve.getSequence().getSegments().get(0);
//        Number slope1 = stb.slope;
//        Number burst = stb.rightLimitAtStartTime;
//
//        Segment srl0 = rlCurve.getSequence().getSegments().get(0);
//        Segment srl1 = rlCurve.getSequence().getSegments().get(1);
//        Number start = srl1.startTime;
//        Number slope2 = srl1.slope;
//
//        if (slope1.gt(slope2)) {
//            return CurveFactory.createDelayedInfiniteBurst(Number.ZERO);
//        }
//        Number newBurst = burst.add(slope1.mul(start));
//        Number newRate = slope1;
//
//        return CurveFactory.createTokenBucket(newRate, newBurst);
//    }
}
