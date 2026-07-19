package org.networkcalculus.dnc.utils;

import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.Segment;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.List;

public class CurveFunctionsUtils {

    public static Number getUltAffineRate(Curve c) {
        return c.getPseudoPeriodHeight().div(c.getPseudoPeriodLength());
    }

    public static Number getBurst(Curve c) {
        return c.getSequence().getSegments().get(0).getRightLimitAtStartTime();
    }

    public static Number getLatency(Curve c) {
        for (Segment s : c.getSequence().getSegments())
            if (s.getRightLimitAtStartTime().neq(Number.ZERO) || s.getSlope().neq(Number.ZERO))
                return s.getStartTime();
        return Number.POSITIVE_INFINITY;
    }

    public static Number getDelayFromDelayedInfiniteBurst(Curve curve) {
        if (!CurveFunctionsUtils.isDelayedInfiniteBurst(curve))
            throw new IllegalArgumentException("Curve must be a delayed Infinite Burst Curve");

        for (Segment s : curve.getSequence().getSegments()) {
            if (s.getRightLimitAtStartTime().eq(Number.POSITIVE_INFINITY))
                return s.getStartTime();
        }
        throw new IllegalArgumentException("Curve must be a delayed Infinite Burst Curve");
    }

    public static boolean isDelayedInfiniteBurst(Curve c) {

        if (!c.getValue(Number.ZERO).eq(Number.ZERO)) {
            return false;
        }
        boolean seenInfinite = false;

        for (Segment s : c.getSequence().getSegments()) {
            if (s.getRightLimitAtStartTime().eq(Number.POSITIVE_INFINITY)) {
                seenInfinite = true;

                if (!s.getSlope().eq(Number.ZERO)) {
                    return false;
                }
                continue;
            }
            if (seenInfinite) {
                return false;
            }
            if (!s.getRightLimitAtStartTime().eq(Number.ZERO)) {
                return false;
            }
            if (!s.getSlope().eq(Number.ZERO)) {
                return false;
            }
        }
        return seenInfinite;
    }

    public static boolean isTokenBucket(Curve c) {
        if (c.getSequence().getSegments().size() != 1) {
            return false;
        }
        Segment s = c.getSequence().getSegments().get(0);

        if (!s.getStartTime().eq(Number.ZERO)) {
            return false;
        }
        if (s.getSlope().lt(Number.ZERO)) {
            return false;
        }
        if (s.getRightLimitAtStartTime().lt(Number.ZERO)) {
            return false;
        }
        return true;
    }

    public static boolean isRateLatency(Curve c) {
        if (c.getSequence().getSegments().size() != 2) {
            return false;
        }
        Segment s0 = c.getSequence().getSegments().get(0);
        Segment s1 = c.getSequence().getSegments().get(1);

        if (!s0.getSlope().eq(Number.ZERO)) {
            return false;
        }
        if (s1.getSlope().leq(Number.ZERO)) {
            return false;
        }
        if (!s1.getStartTime().eq(s0.getEndTime())) {
            return false;
        }
        return true;
    }

//    public static Number f_inv(Curve c, Number y) {
//        if (c.getSequence().getSegments().size() == 1) {
//            Segment s = c.getSequence().getSegments().get(0);
//            if (s.startTime.eq(Number.ZERO) && s.rightLimitAtStartTime.eq(Number.ZERO) && s.slope.gt(Number.ZERO)) {
//                if (y.leq(Number.ZERO)) {
//                    return Number.ZERO;
//                }
//                return y.div(s.slope);
//            }
//        }
//        Number tTransient = f_inv_in_sequence(c, y);
//        Number transientEnd = c.pseudoPeriodStart.add(c.pseudoPeriodLength);
//
//        if (tTransient.leq(transientEnd)) {
//            return tTransient;
//        }
//        Number H = c.pseudoPeriodHeight;
//        Number L = c.pseudoPeriodLength;
//        Number yBaseMax = c.getValue(transientEnd);
//
//        if (H.eq(Number.ZERO)) {
//            return Number.POSITIVE_INFINITY;
//        }
//
//        Number k = y.sub(yBaseMax).div(H).floor();
//        Number yPrime = y.sub(k.mul(H));
//        Number tPrime = f_inv_in_sequence(c, yPrime);
//
//        if (tPrime.eq(Number.POSITIVE_INFINITY)) {
//            return Number.POSITIVE_INFINITY;
//        }
//
//        return tPrime.add(k.mul(L));
//    }
//
//    private static Number f_inv_in_sequence(Curve c, Number y) {
//        if (y.eq(Number.ZERO)) {
//            return Number.ZERO;
//        }
//        Number best = Number.POSITIVE_INFINITY;
//        for (Segment s : c.getSequence().getSegments()) {
//            Number b = s.rightLimitAtStartTime;
//            Number m = s.slope;
//            Number t0 = s.startTime;
//            Number t1 = s.endTime;
//            if (b.geq(y)) {
//                best = best.min(t0);
//                continue;
//            }
//            if (m.eq(Number.ZERO)) {
//                continue;
//            }
//            Number t = t0.add(y.sub(b).div(m));
//
//            if (t.geq(t0) && t.leq(t1)) {
//                best = best.min(t);
//            }
//        }
//        return best;
//    }

    // das decomposeIntoTB funktioniert nicht wie erwartet
    public static int getRLComponentCount(Curve c) {
        return c.decomposeIntoRateLatency().size();
    }

    public static int getTBComponentCount(Curve c) {
        return c.decomposeIntoTokenBucket().size();
    }

    public static Curve getRLComponent(Curve c, int i) {
        List<Curve> rl = c.decomposeIntoRateLatency();
        if (i < 0 || i >= rl.size()) {
            throw new IndexOutOfBoundsException("Invalid RL component index: " + i);
        }
        return rl.get(i);
    }

    public static Curve getTBComponent(Curve c, int i) {
        List<Curve> tb = c.decomposeIntoTokenBucket();
        if (i < 0 || i >= tb.size()) {
            throw new IndexOutOfBoundsException("Invalid TB component index: " + i);
        }
        return tb.get(i);
    }
}
