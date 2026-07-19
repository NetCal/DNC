package org.networkcalculus.dnc.backend;

import org.networkcalculus.dnc.backend.number.Number;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.networkcalculus.dnc.utils.CurveFunctionsUtils;

public final class CurveUtils {
    public static Curve removeLatency(Curve curve) {

        if (curve.getBaseSequence().getSegments().size() < 2)
            return curve;

        Number latency = getLatency(curve);
        if (latency.leq(Number.ZERO))
            return curve;

        if (latency.eq(Number.POSITIVE_INFINITY))
            return CurveFactory.createPeakRate(Number.ZERO);

        shiftLeft(curve, latency);
        return curve;
    }

    /**
     * Compute backlog bound for {@code arrival} and {@code service} curves.
     *
     * @param arrival constraint on the rate of the Arrival Process
     * @param service constraint on the rate of the Departure Process
     * @return POSITIVE_INFINITY, if the curves diverge. Upper bound on the vertical distance, otherwise.
     */
    public static Number backlog(Curve arrival, Curve service) {
        // get average slope of pseudo-periodic part
        Number arrivalPseudoPeriodSlope = arrival.getPseudoPeriodHeight()
                .div(arrival.getPseudoPeriodLength());
        Number servicePseudoPeriodSlope = service.getPseudoPeriodHeight()
                .div(service.getPseudoPeriodLength());

        // curves diverge
        if (arrivalPseudoPeriodSlope.gt(servicePseudoPeriodSlope))
            return Number.POSITIVE_INFINITY;

        Sequence arrivalSeq, serviceSeq;
        Number pseudoPeriodLength = arrival.getPseudoPeriodLength()
                .lcm(service.getPseudoPeriodLength());
        Number pseudoPeriodStart = arrival.getPseudoPeriodStart().max(
                service.getPseudoPeriodStart());
        // curves do not diverge and do not intersect
        if (arrivalPseudoPeriodSlope.eq(servicePseudoPeriodSlope)) {
            // the curves are parallel: maximal vertical distance must appear before the start of the pseudo periodic part
            arrivalSeq = arrival.extend(pseudoPeriodStart).getBaseSequence().cut(Number.ZERO, pseudoPeriodStart);
            serviceSeq = service.extend(pseudoPeriodStart).getBaseSequence().cut(Number.ZERO, pseudoPeriodStart);
        } else { // curves intersect
            // extend until intersection; adjust for pseudo periodic segments
            Number ext = CurveUtils.getExtendValue(arrival, service).add(pseudoPeriodLength).add(pseudoPeriodStart);
            arrivalSeq = arrival.extend(ext).getBaseSequence();
            serviceSeq = service.extend(ext).getBaseSequence();
        }

        return Sequence.backlog(arrivalSeq, serviceSeq);

    }

    /**
     * Shift {@code curve} up by {@code shiftValue}
     *
     * @param curve
     * @param shiftValue
     */
    public static void verticalShift(Curve curve, Number shiftValue) {

        List<Segment> shiftedSegments = new ArrayList<>();

        for (Segment segment : curve.getSequence().getSegments()) {
            shiftedSegments.add(Segment.verticalShift(segment, shiftValue));
        }
        Sequence sequence = new Sequence(new LinkedList<>(shiftedSegments));
        curve.setBaseSequence(sequence);
    }

    /*
     * Get the start time of never-intersecting pseudo-hyperperiod * of two curves.
     * Note that
     * upper must have a grater pseudo-period slope than lower.
     */

    private static Number pseudoHyperperiodStart(Curve lower, Curve upper) {
        Number lowerPseudoPeriodSlope = lower.getPseudoPeriodHeight()
                .div(lower.getPseudoPeriodLength());
        Number upperPseudoPeriodSlope = upper.getPseudoPeriodHeight()
                .div(upper.getPseudoPeriodLength());
        Number pseudoPeriodPeak = Number.ZERO;
        Number pseudoPeriodTrough = Number.POSITIVE_INFINITY;
        for (Segment s : lower.getSequence().cut(
                lower.getPseudoPeriodStart(),
                lower.getPseudoPeriodStart().add(lower.getPseudoPeriodLength())).getSegments())
            pseudoPeriodPeak = pseudoPeriodPeak.max(s.getRightLimitAtStartTime().sub(
                    lowerPseudoPeriodSlope.mul(s.getStartTime())));
        for (Segment s : upper.getSequence().cut(
                upper.getPseudoPeriodStart(),
                upper.getPseudoPeriodStart().add(upper.getPseudoPeriodLength())).getSegments())
            pseudoPeriodTrough = pseudoPeriodTrough.min(s.getValue(s.getEndTime()).sub(
                    upperPseudoPeriodSlope.mul(s.getEndTime())));
        return pseudoPeriodPeak.sub(pseudoPeriodTrough).div(
                        upperPseudoPeriodSlope.sub(lowerPseudoPeriodSlope))
                .max(lower.getPseudoPeriodStart().max(upper.getPseudoPeriodStart()));
    }
    /* Sum of two curves. See [1] Section 4.2. */

    public static Curve add(Curve a, Curve b) {
        Number pseudoPeriodStart = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart());
        Number pseudoPeriodLength = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength());
        Number pseudoPeriodHeight = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength())
                .add(b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength()))
                .mul(pseudoPeriodLength);
        Sequence aSequence = a.extend(pseudoPeriodStart.add(pseudoPeriodLength)).getBaseSequence();
        Sequence bSequence = b.extend(pseudoPeriodStart.add(pseudoPeriodLength)).getBaseSequence();
        Curve sum = new Curve(
                Sequence.addition(aSequence, bSequence),
                pseudoPeriodStart,
                pseudoPeriodLength,
                pseudoPeriodHeight);
        sum.optimize();
        return sum;

    }

    public static Curve sub(Curve a, Curve b) {
        Number lastIntersection = getLastIntersection(a, b);
        Number extendValue;
        if (lastIntersection.gt(Number.ZERO) && lastIntersection.neq(Number.POSITIVE_INFINITY)) {
            extendValue = lastIntersection.add(a.getPseudoPeriodLength().add(b.getPseudoPeriodLength()));
        } else {
            extendValue = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart()).add(a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength()));
        }
        a = a.extend(extendValue);
        b = b.extend(extendValue);

        Number pseudoPeriodStart = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart());
        Number pseudoPeriodStartValue = a.getValue(pseudoPeriodStart)
                .sub(b.getValue(pseudoPeriodStart));
        Number pseudoPeriodSlope = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength())
                .sub(b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength()))
                .max(Number.ZERO);
        if (pseudoPeriodStartValue.lt(Number.ZERO) && pseudoPeriodSlope.gt(Number.ZERO))
            pseudoPeriodStart = pseudoPeriodStart.add(pseudoPeriodStartValue.neg()
                    .div(pseudoPeriodSlope));
        Number pseudoPeriodLength = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength());
        Number pseudoPeriodHeight = pseudoPeriodSlope.mul(pseudoPeriodLength);

        Sequence aSequence = a.extend(extendValue)
                .baseSequence;
        Sequence bSequence = b.extend(extendValue)
                .baseSequence;
        Curve difference = new Curve(
                Sequence.subtraction(aSequence, bSequence),
                pseudoPeriodStart,
                pseudoPeriodLength,
                pseudoPeriodHeight
        );
//        difference.optimize();
//        difference = difference.nonDecreasingClosure();
        return difference;

    }


    private static Curve miniumWithDelayedInfiniteBurst(Curve curve, Number delay) {

        if (delay.eq(Number.ZERO))
            return curve;

        Number newPseudoStart = delay.add(curve.getPseudoPeriodLength().mul(new Number(2)));
        curve = curve.extend(newPseudoStart);

        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, delay, Number.ZERO, Number.ZERO));
        segments.addAll(curve.getBaseSequence().cut(delay, curve.getBaseSequence().getSegments().getLast().getEndTime()).getSegments());

        Curve result;
        if (delay.leq(curve.getPseudoPeriodStart()))
            result = new Curve(new Sequence(new LinkedList<>(segments)), curve.getPseudoPeriodStart(), curve.getPseudoPeriodLength(), curve.getPseudoPeriodHeight());
        else
            result = new Curve(new Sequence(new LinkedList<>(segments)), newPseudoStart, curve.getPseudoPeriodLength(), curve.getPseudoPeriodHeight());

        return result;

    }

    private static Curve maximumWithDelayedInfiniteBurst(Curve curve, Number delay) {

        if (delay.eq(Number.ZERO))
            return CurveFactory.createDelayedInfiniteBurst(Number.ZERO);

        curve = curve.extend(delay.add(curve.getPseudoPeriodLength()));

        ArrayList<Segment> segments = new ArrayList<>();
        segments.addAll(curve.getBaseSequence().cut(Number.ZERO, delay).getSegments());
        segments.add(new Segment(delay, delay.add(Number.ONE), Number.POSITIVE_INFINITY, Number.ZERO));

        return new Curve(new Sequence(new LinkedList<>(segments)), delay, Number.ONE, Number.ZERO);

    }


    /* Minimum of two curves. See [1] Section 4.3. */
    public static Curve minimum(Curve a, Curve b) {

        // Delayed Infinite Burst

        if (CurveFunctionsUtils.isDelayedInfiniteBurst(a) && CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delayA = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            Number delayB = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            return CurveFactory.createDelayedInfiniteBurst(delayA.max(delayB));

        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(a)) {
            Number delay = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            return miniumWithDelayedInfiniteBurst(b, delay);
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delay = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b);
            return miniumWithDelayedInfiniteBurst(a, delay);
        }

        // Regular curve shapes

        Number pseudoPeriodStart, pseudoPeriodLength, pseudoPeriodHeight;
        Number aPseudoPeriodSlope = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength());
        Number bPseudoPeriodSlope = b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength());
        if (aPseudoPeriodSlope.lt(bPseudoPeriodSlope)) {
            pseudoPeriodStart = pseudoHyperperiodStart(a, b);
            pseudoPeriodLength = a.getPseudoPeriodLength();
            pseudoPeriodHeight = a.getPseudoPeriodHeight();
        } else if (aPseudoPeriodSlope.gt(bPseudoPeriodSlope)) {
            pseudoPeriodStart = pseudoHyperperiodStart(b, a);
            pseudoPeriodLength = b.getPseudoPeriodLength();
            pseudoPeriodHeight = b.getPseudoPeriodHeight();
        } else {
            pseudoPeriodStart = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart());
            pseudoPeriodLength = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength());
            pseudoPeriodHeight = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength())
                    .mul(aPseudoPeriodSlope);
        }
        Sequence aSequence = a.extend(pseudoPeriodStart.add(pseudoPeriodLength))
                .getSequence();
        Sequence bSequence = b.extend(pseudoPeriodStart.add(pseudoPeriodLength))
                .getSequence();
        Curve minimum = new Curve(
                Sequence.minimum(aSequence, bSequence),
                pseudoPeriodStart,
                pseudoPeriodLength,
                pseudoPeriodHeight);
        minimum.optimize();
        return minimum;

    }

    /* Maximum of two curves. See [1] Section 4.3. */
    public static Curve maximum(Curve a, Curve b) {

        // Delayed Infinite Burst

        if (CurveFunctionsUtils.isDelayedInfiniteBurst(a) && CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delayA = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            Number delayB = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            return CurveFactory.createDelayedInfiniteBurst(delayA.min(delayB));

        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(a)) {
            Number delay = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(a);
            return maximumWithDelayedInfiniteBurst(b, delay);
        } else if (CurveFunctionsUtils.isDelayedInfiniteBurst(b)) {
            Number delay = CurveFunctionsUtils.getDelayFromDelayedInfiniteBurst(b);
            return maximumWithDelayedInfiniteBurst(a, delay);
        }

        // Regular curve shapes

        Number pseudoPeriodStart, pseudoPeriodLength, pseudoPeriodHeight;
        Number aPseudoPeriodSlope = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength());
        Number bPseudoPeriodSlope = b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength());
        if (aPseudoPeriodSlope.gt(bPseudoPeriodSlope)) {
            pseudoPeriodStart = pseudoHyperperiodStart(b, a);
            pseudoPeriodLength = a.getPseudoPeriodLength();
            pseudoPeriodHeight = a.getPseudoPeriodHeight();
        } else if (aPseudoPeriodSlope.lt(bPseudoPeriodSlope)) {
            pseudoPeriodStart = pseudoHyperperiodStart(a, b);
            pseudoPeriodLength = b.getPseudoPeriodLength();
            pseudoPeriodHeight = b.getPseudoPeriodHeight();
        } else {
            pseudoPeriodStart = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart());
            pseudoPeriodLength = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength());
            pseudoPeriodHeight = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength())
                    .mul(aPseudoPeriodSlope);
        }
        Sequence aSequence = a.extend(pseudoPeriodStart.add(pseudoPeriodLength)).getBaseSequence();
        Sequence bSequence = b.extend(pseudoPeriodStart.add(pseudoPeriodLength)).getBaseSequence();
        Curve maximum = new Curve(
                Sequence.maximum(aSequence, bSequence),
                pseudoPeriodStart,
                pseudoPeriodLength,
                pseudoPeriodHeight);
        maximum.optimize();
        return maximum;

    }

    /* Convolution of two curves. See [1] Section 4.4. */
    public static Curve convolution(Curve a, Curve b) {
        if (isDelayedInfiniteBurst(a) && isDelayedInfiniteBurst(b)) {
            Number delayA = CurveUtils.getDelayFromDelayedInfiniteBurst(a);
            Number delayB = CurveUtils.getDelayFromDelayedInfiniteBurst(b);
            return CurveFactory.createDelayedInfiniteBurst(delayA.add(delayB));
        } else if (isDelayedInfiniteBurst(a)) {
            Number delay = CurveUtils.getDelayFromDelayedInfiniteBurst(a);
            return CurveUtils.shiftRight(b, delay);
        } else if (isDelayedInfiniteBurst(b)) {
            Number delay = CurveUtils.getDelayFromDelayedInfiniteBurst(b);
            return CurveUtils.shiftRight(a, delay);
        }

        Number periodLength = a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength());
        Number periodHeight = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength())
                .min(b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength()))
                .mul(periodLength);
        Number aPeriodEndTime = a.getPseudoPeriodStart().add(b.getPseudoPeriodStart()
                .add(a.getPseudoPeriodLength()));
        Number bPeriodEndTime = a.getPseudoPeriodStart().add(b.getPseudoPeriodStart()
                .add(b.getPseudoPeriodLength()));
        Number periodEndTime = a.getPseudoPeriodStart().add(b.getPseudoPeriodStart()
                .add(new Number(2).mul(periodLength)));
        Sequence aTransientPart = a.getBaseSequence().cut(Number.ZERO, a.getPseudoPeriodStart());
        Sequence bTransientPart = b.getBaseSequence().cut(Number.ZERO, b.getPseudoPeriodStart());
        Sequence aLongPseudoPeriodicPart = a.extend(periodEndTime).getBaseSequence().cut(
                a.getPseudoPeriodStart(),
                periodEndTime);
        Sequence aShortPseudoPeriodicPart = aLongPseudoPeriodicPart.cut(
                a.getPseudoPeriodStart(),
                aPeriodEndTime);
        Sequence bLongPseudoPeriodicPart = b.extend(periodEndTime).getBaseSequence().cut(
                b.getPseudoPeriodStart(),
                periodEndTime);
        Sequence bShortPseudoPeriodicPart = bLongPseudoPeriodicPart.cut(
                b.getPseudoPeriodStart(),
                bPeriodEndTime);
        Sequence ttBaseSequence = Sequence.convolution(aTransientPart, bTransientPart);
        Sequence tpBaseSequence = Sequence.convolution(
                aTransientPart,
                bShortPseudoPeriodicPart).cut(Number.ZERO, bPeriodEndTime);
        Sequence ptBaseSequence = Sequence.convolution(
                aShortPseudoPeriodicPart,
                bTransientPart).cut(Number.ZERO, aPeriodEndTime);
        Sequence ppBaseSequence = Sequence.convolution(
                aLongPseudoPeriodicPart,
                bLongPseudoPeriodicPart).cut(Number.ZERO, periodEndTime);
        Curve tpConvolution = new Curve(
                tpBaseSequence,
                a.getPseudoPeriodStart().add(b.getPseudoPeriodStart()),
                b.getPseudoPeriodLength(),
                b.getPseudoPeriodHeight());
        Curve ptConvolution = new Curve(
                ptBaseSequence,
                a.getPseudoPeriodStart().add(b.getPseudoPeriodStart()),
                a.getPseudoPeriodLength(),
                a.getPseudoPeriodHeight());
        Curve ppConvolution = new Curve(
                ppBaseSequence,
                a.getPseudoPeriodStart().add(b.getPseudoPeriodStart().add(periodLength)),
                periodLength,
                periodHeight);
        tpConvolution.setBaseSequence(Sequence.minimum(ttBaseSequence, tpBaseSequence));
        Curve convolution = minimum(
                minimum(tpConvolution, ptConvolution),
                ppConvolution);
        convolution.optimize();
        return convolution;

    }

    /* Deconvolution of two curves. See [1] Section 4.5. */
    public static Curve deconvolution(Curve a, Curve b) {
        if (isDelayedInfiniteBurst(a) && isDelayedInfiniteBurst(b)) {
            Number delayA = CurveUtils.getDelayFromDelayedInfiniteBurst(a);
            Number delayB = CurveUtils.getDelayFromDelayedInfiniteBurst(b);
            return CurveFactory.createDelayedInfiniteBurst(delayA.sub(delayB).abs());
        } else if (isDelayedInfiniteBurst(a)) {
            Number delay = CurveUtils.getDelayFromDelayedInfiniteBurst(a);
            return CurveUtils.shiftLeft(b, delay);
        } else if (isDelayedInfiniteBurst(b)) {
            Number delay = CurveUtils.getDelayFromDelayedInfiniteBurst(b);
            return CurveUtils.shiftLeft(a, delay);
        }
        if (a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength())
                .gt(b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength())))
            return new Curve(
                    new Sequence(new LinkedList<Segment>(Arrays.asList(new Segment(
                            Number.ZERO,
                            Number.ONE,
                            Number.POSITIVE_INFINITY,
                            Number.ZERO)))),
                    Number.ZERO,
                    Number.ONE,
                    Number.ZERO);

        Number pseudoPeriodStart = a.getPseudoPeriodStart().max(b.getPseudoPeriodStart())
                .add(a.getPseudoPeriodLength().lcm(b.getPseudoPeriodLength()));
        Sequence aSequence = a.extend(pseudoPeriodStart.add(a.getPseudoPeriodStart()
                .add(a.getPseudoPeriodLength()))).getBaseSequence();
        Sequence bSequence = b.extend(pseudoPeriodStart).getBaseSequence();
        Sequence baseSequence = Sequence.deconvolution(aSequence, bSequence).cut(
                Number.ZERO,
                a.getPseudoPeriodStart().add(a.getPseudoPeriodLength()));
        Curve deconvolution = new Curve(
                baseSequence,
                a.getPseudoPeriodStart(),
                a.getPseudoPeriodLength(),
                a.getPseudoPeriodHeight());
        deconvolution.optimize();

        return deconvolution;

    }

    public static Number hDev(Curve arrival, Curve service) {
        /** Extend the curve until past their first intersection */
        Number arrivalPseudoPeriodSlope = arrival.getPseudoPeriodHeight()
                .div(arrival.getPseudoPeriodLength());
        Number servicePseudoPeriodSlope = service.getPseudoPeriodHeight()
                .div(service.getPseudoPeriodLength());

        if (arrivalPseudoPeriodSlope.geq(servicePseudoPeriodSlope))
            return Number.POSITIVE_INFINITY;

        Sequence arrivalSeq, serviceSeq;
        Number pseudoPeriodLength = arrival.getPseudoPeriodLength()
                .lcm(service.getPseudoPeriodLength());
        Number pseudoPeriodStart = arrival.getPseudoPeriodStart().max(
                service.getPseudoPeriodStart());

        Number ext = CurveUtils.getExtendValue(arrival, service).add(pseudoPeriodLength).add(pseudoPeriodStart);
        arrivalSeq = arrival.extend(ext).getBaseSequence();
        serviceSeq = service.extend(ext).getBaseSequence();

        /** Search the first segment of the SC that reaches the burst value of the AC */

        Number burstVal = arrivalSeq.getRightLimitValue(arrivalSeq.getSegments().getFirst().getStartTime());

        Number delay = Number.POSITIVE_INFINITY;

        for (Segment serviceSeg : serviceSeq.getSegments()) {
            Number startValue = serviceSeg.getRightLimitAtStartTime();
            Number endValue = serviceSeg.getRightLimitValue(serviceSeg.getEndTime());
            if (startValue.geq(burstVal)
            ) {
                return serviceSeg.getStartTime();
            } else if (endValue.geq(burstVal)) {
                return serviceSeg.getTime(burstVal);
            }

        }

        return delay;
    }

    public static Number delayFIFO(Curve arrival, Curve service) {
        // get horizontal deviation
        return CurveUtils.hDev(arrival, service);
    }

    public static Number delayARB(Curve arrival, Curve service) {
        // get backlogged period bound
        return CurveUtils.getFirstIntersection(arrival, service);
    }

    /* Old CurveUtils methods */

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

    public static Number getLatency(Curve c) {
        for (Segment s : c.getSequence().getSegments())
            if (s.getRightLimitAtStartTime().neq(Number.ZERO) || s.getSlope().neq(Number.ZERO))
                return s.getStartTime();
        return Number.POSITIVE_INFINITY;
    }

    public static Number getDelayFromDelayedInfiniteBurst(Curve curve) {
        if (!CurveUtils.isDelayedInfiniteBurst(curve))
            throw new IllegalArgumentException("Curve must be a delayed Infinite Burst Curve");

        for (Segment s : curve.getSequence().getSegments()) {
            if (s.getRightLimitAtStartTime().eq(Number.POSITIVE_INFINITY))
                return s.getStartTime();
        }
        throw new IllegalArgumentException("Curve must be a delayed Infinite Burst Curve");
    }

    public static void beautify(Curve c) {
        c.getSequence().optimize();
    }

    public static List<Number> computeInflectionPointsX(Curve a, Curve b) {
        List<Number> xs = new ArrayList<>();
        for (Segment s : a.getSequence().getSegments())
            xs.add(s.getStartTime());
        for (Segment s : b.getSequence().getSegments())
            xs.add(s.getStartTime());
        xs.sort((n1, n2) -> n1.lt(n2) ? -1 : (n1.gt(n2) ? 1 : 0));
        return xs;
    }

    public static Number getMaxVerticalDeviation(Curve c1, Curve c2) {
        Number max = Number.NEGATIVE_INFINITY;
        for (Number x : computeInflectionPointsX(c1, c2)) {
            Number v1 = c1.getValue(x);
            Number v2 = c2.getValue(x);
            max = max.max(v1.sub(v2));
        }
        return max;
    }


    /**
     *
     * @param a Curve object
     * @param b Curve object
     * @return x-coordinate of the first intersection between curves {@code a} and
     * {@code b}.
     * {@code Number.POSITIVE_INFINITY} if curves do not intersect.
     * {@code null}  if the curves are parallel.
     */
    public static Number getFirstIntersection(Curve a, Curve b) {

        Number extendValue = getExtendValue(a, b);

        if (extendValue.eq(Number.POSITIVE_INFINITY) || extendValue.isNaN()) {
            return Number.POSITIVE_INFINITY;
        }

        // determine which curve has higher y value at the start
        Curve greaterCurve = a;
        Curve lesserCurve = b;

        if (b.getValue(Number.ZERO).gt(a.getValue(Number.ZERO))) {
            greaterCurve = b;
            lesserCurve = a;
        }

        greaterCurve = greaterCurve.extend(extendValue);
        lesserCurve = lesserCurve.extend(extendValue);

        for (Segment aSeg : greaterCurve.getBaseSequence().getSegments()) {
            for (Segment bSeg : lesserCurve.getBaseSequence().getSegments()) {
                // case where the curves do not intersect, but are not parallel either, e.g. step functions
                if (!curvesAreParallel(a, b) &&
                        aSeg.getRightLimitAtStartTime().leq(bSeg.getRightLimitAtStartTime()) &&
                        aSeg.getSlope().eq(bSeg.getSlope()) &&
                        (aSeg.getSlope().eq(Number.ZERO) && bSeg.getSlope().eq(Number.ZERO)) &&
                        Segment.segmentsOverlap(aSeg, bSeg)
                ) {
                    return aSeg.getStartTime().max(bSeg.getStartTime());
                }
                Number intersection = Segment.getIntersection(aSeg, bSeg);
                if (intersection != null && intersection.geq(Number.ZERO)) {
                    return intersection;
                }
            }
        }

        return Number.POSITIVE_INFINITY;

    }


    /**
     *
     * @param a Curve object
     * @param b Curve object
     * @return x-coordinate of the first intersection between curves {@code a} and
     * {@code b}.
     * {@code Number.POSITIVE_INFINITY} if curves do not intersect.
     * {@code null}  if the curves are parallel.
     */
    public static Number getLastIntersection(Curve a, Curve b) {

        Number extendValue = getExtendValue(a, b);

        if (extendValue.eq(Number.POSITIVE_INFINITY) || extendValue.isNaN()) {
            return Number.POSITIVE_INFINITY;
        }
        Curve cpyA = a.extend(extendValue);
        Curve cpyB = b.extend(extendValue);

        Number intersectionFound = Number.POSITIVE_INFINITY;

        for (Segment aSeg : cpyA.getBaseSequence().getSegments()) {
            for (Segment bSeg : cpyB.getBaseSequence().getSegments()) {
                Number intersection = Segment.getIntersection(aSeg, bSeg);
                if (intersection != null && intersection.gt(Number.ZERO)) {
                    intersectionFound = intersection;
                }
            }
        }

        return intersectionFound;

    }

    /**
     * Shift the curve to the right by dx: each segment starts and ends by dx time
     * units later. If the curve contains only one segment,
     * creates a new one that lays on the x-axis.
     *
     * @param curve
     * @param dx
     * @return
     */
    public static Curve shiftRight(Curve curve, Number dx) {
        if (dx.leq(Number.ZERO))
            return curve;

        if (dx.eq(Number.POSITIVE_INFINITY) || dx.eq(Number.NEGATIVE_INFINITY))
            throw new IllegalArgumentException("Cannot shift by infinity");

        List<Segment> shiftedSegments = new ArrayList<>();
        List<Segment> originalSegments = curve.getBaseSequence().getSegments();

        Number newStart = originalSegments.getFirst().getStartTime();

        if (originalSegments.size() == 1) {
            Segment soleSegment = originalSegments.getFirst();
            // Segment laying on the x axis until time dx;
            Segment newSegment = new Segment(newStart, dx, Number.ZERO, Number.ZERO);
            // Shifted sole segment
            soleSegment = new Segment(dx, soleSegment.getEndTime().add(dx),
                    soleSegment.getRightLimitValue(Number.ZERO), soleSegment.getSlope());
            shiftedSegments.add(newSegment);
            shiftedSegments.add(soleSegment);
        } else {
            for (Segment segment : originalSegments) {
                Number newEnd = segment.getEndTime().add(dx);
                Number newRightLimValue = curve.getValue(newStart);

                // newRightLimitValue for the *current segment* is the value of the *previous
                // (before the shift) segment* at its endTime.
                if (shiftedSegments.size() != 0) {
                    newRightLimValue = originalSegments.get(originalSegments.indexOf(segment) - 1)
                            .getValue(newStart.sub(dx));
                }

                Segment shiftedSegment = new Segment(newStart, newEnd, newRightLimValue, segment.getSlope());

                shiftedSegments.add(shiftedSegment);
                newStart = newEnd;
            }
        }

        Number newPseudoPeriodStart = curve.getPseudoPeriodStart();
        Number newPseudoPeriodLength = curve.getPseudoPeriodLength();

        newPseudoPeriodStart = newPseudoPeriodStart.add(dx);

        return new Curve(new Sequence(shiftedSegments), newPseudoPeriodStart, newPseudoPeriodLength,
                curve.getPseudoPeriodHeight());
    }


    public static Curve shiftLeft(Curve curve, Number dx) {

        if (dx.leq(Number.ZERO))
            return curve;
        if (dx.eq(Number.POSITIVE_INFINITY) || dx.eq(Number.NEGATIVE_INFINITY))
            throw new IllegalArgumentException("Cannot shift by infinity");

        ArrayList<Segment> shiftedSegments = new ArrayList<>();

        Segment lastSegment = curve.getSequence().getSegments().getLast();
        Number lastSegmentEndTime = lastSegment.getEndTime();
        Number extendedEndTime = lastSegmentEndTime.add(dx);

        Curve extCurve = curve.extend(extendedEndTime);

        for (Segment segment : extCurve.getSequence().getSegments()) {

            Number newStart = segment.getStartTime().sub(dx);

            Number newEnd = segment.getEndTime().sub(dx);

            if (newEnd.lt(Number.ZERO))
                continue;

            if (newStart.lt(Number.ZERO) && newEnd.geq(Number.ZERO)) {
                Number valueAtZero = segment.getRightLimitAtStartTime()
                        .add(segment.getSlope().mul(dx));
                if (newEnd.eq(Number.ZERO)) {
                    newEnd = Number.ONE;
                }
                shiftedSegments.add(new Segment(
                        Number.ZERO,
                        newEnd,
                        valueAtZero,
                        segment.getSlope()));
            } else if (newStart.geq(Number.ZERO)) {
                if (newEnd.eq(lastSegmentEndTime)) {
                    newEnd = newEnd.sub(dx);
                }
                Segment newSegment = new Segment(
                        newStart,
                        newEnd,
                        segment.getRightLimitAtStartTime(),
                        segment.getSlope());
                shiftedSegments.add(newSegment);
            }

        }

        Number newPseudoPeriodStart = curve.getPseudoPeriodStart().sub(dx);

        if (newPseudoPeriodStart.lt(Number.ZERO)) {
            newPseudoPeriodStart = Number.ZERO;
            Number newPseudoPeriodLength = curve.getPseudoPeriodStart()
                    .add(curve.getPseudoPeriodLength());
            curve.setPseudoPeriodStart(newPseudoPeriodStart);
            curve.setPseudoPeriodLength(newPseudoPeriodLength);

        } else {
            curve.setPseudoPeriodStart(newPseudoPeriodStart);
        }
        curve.setBaseSequence(new Sequence(new LinkedList<>(shiftedSegments)));
        return curve;
    }


    /**
     *
     * @param curve Curve object
     * @param y     Number
     * @return the first x value that the curves evaluates to y: f(x) = y
     */
    public static Number f_inv(Curve curve, Number y) {
        if (y.eq(Number.ZERO)) {
            return Number.ZERO;
        }

        Number extendValue = (y.div(curve.getPseudoPeriodHeight())
                .mul(curve.getPseudoPeriodLength())).add(curve.getPseudoPeriodLength().mul(new Number(2)));
        curve = curve.extend(extendValue);

        for (Segment s : curve.getSequence().getSegments()) {
            Number yAtStart = s.getRightLimitAtStartTime();
            Number yAtEnd = s.getValue(s.getEndTime());

            // Flat segment sitting exactly on y → first x is its startTime
            if (s.getSlope().eq(Number.ZERO)) {
                if (yAtStart.eq(y)) {
                    return s.getStartTime();
                }
                continue;
            }

            if (yAtStart.leq(y) && yAtEnd.gt(y)) {
                // y = slope * (x - startTime) + yAtStart
                // => x = startTime + (y - yAtStart) / slope
                return s.getStartTime().add(y.sub(yAtStart).div(s.getSlope()));
            }
        }

        throw new IllegalStateException("f_inv: curve did not reach y=" + y + " after extension.");
    }

    public static Number getExtendValue(Curve a, Curve b) {
        Number slopeA = a.getPseudoPeriodHeight().div(a.getPseudoPeriodLength());
        Number slopeB = b.getPseudoPeriodHeight().div(b.getPseudoPeriodLength());

        Number hyperPeriodStart = a.getPseudoPeriodStart().add(a.getPseudoPeriodLength()).max(b.getPseudoPeriodStart().add(b.getPseudoPeriodLength()));

        Number m1 = slopeA;
        Number d1 = a.getValue(hyperPeriodStart).sub(slopeA.mul(hyperPeriodStart));

        Number m2 = slopeB;
        Number d2 = b.getValue(hyperPeriodStart).sub(slopeB.mul(hyperPeriodStart));

        if (m1.eq(m2)) {
            if (d1.eq(d2)) {
                return new Number(hyperPeriodStart);
            }
            return Number.ZERO;
        }

        // FIX: This guarantees the relative offset from the hyperPeriodStart is accurately added back
        Number relativeX = (d2.sub(d1)).div(m1.sub(m2));
        return new Number(hyperPeriodStart.add(relativeX));
    }


    public static boolean curvesAreParallel(Curve a, Curve b) {
        return getExtendValue(a, b).eq(Number.ZERO);
    }


}
