package org.networkcalculus.dnc.backend;

import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

/**
 * Class to generate every type of curves available
 */
public final class CurveFactory {

    public static final CurveFactory INSTANCE = new CurveFactory();

    /**
     * Creates a Concave Piecewise Linear Curve by taking the minimum of multiple
     * random token-bucket curves
     *
     * @param numberOfTokenBuckets constraint on the number of token buckets
     * @param maxRate              constraint on the maximal rate of a single
     *                             segment
     * @param maxBurst             constraint on the initial burst
     * @return Concave piecewise linear curve
     */

    public static Curve createCPL(int numberOfTokenBuckets, double maxRate, double maxBurst) {

        ArrayList<Curve> tokenBuckets = new ArrayList<>();

        for (int i = 0; i < numberOfTokenBuckets; i++) {
            double burst = Math.random() * maxBurst;
            double rate = Math.random() * maxRate;
            tokenBuckets.add(CurveFactory.createTokenBucket(new Number(rate), new Number(burst)));
        }

        // loop over the random tocken-bucket curves, while storing the minimum curve in
        // the cpl variable
        Curve cpl = tokenBuckets.get(0);
        CurveUtils.minimum(cpl, cpl);
        for (int i = 0; i < tokenBuckets.size(); i++) {
            CurveUtils.minimum(cpl, tokenBuckets.get(i));
        }

        return cpl;
    }


    public static Curve createTDMA(Number sendingRate, Number sendingDuration, Number idleDuration, Number latency) {

        if (latency.lt(Number.ZERO))
            throw new IllegalArgumentException("Latency has to be >= 0");

        ArrayList<Segment> segments = new ArrayList<>();

        if (latency.neq(Number.ZERO))
            segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));

        segments.add(new Segment(latency, latency.add(sendingDuration), Number.ZERO, sendingRate));
        segments.add(new Segment(latency.add(sendingDuration), latency.add(sendingDuration).add(idleDuration), sendingDuration.mul(sendingRate), Number.ZERO));
        return new Curve(new Sequence(new LinkedList<>(segments)), latency, sendingDuration.add(idleDuration), sendingDuration.mul(sendingRate));
    }

    public static Curve createTDMA(double sendingRate, double sendingDuration, double idleDuration, double latency) {
        return createTDMA(new Number(sendingRate), new Number(sendingDuration), new Number(idleDuration), new Number(latency));
    }

    public static Curve approximateTDMA(Curve tdma) {

        // Using individual segments because .extend(.) changes the pseudo periodic parameters
        Segment seg1 = tdma.getSegment(Number.ZERO);
        Segment seg2 = tdma.getSegment(Number.ONE);

        Number latency = seg1.getStartTime();
        Number slope = seg2.getRightLimitAtStartTime().div(seg2.getEndTime().sub(seg1.getStartTime()));
        return CurveFactory.createRateLatency(slope, latency);
    }

    public static Curve approximateStepFunction(Curve stepFunction) {

        Segment stepSegment = stepFunction.getBaseSequence().getSegments().getFirst();
        Number burst = stepSegment.getRightLimitAtStartTime();
        Number slope = stepFunction.getPseudoPeriodHeight().div(stepFunction.getPseudoPeriodLength());

        return CurveFactory.createTokenBucket(slope, burst);
    }

    public static Curve createStepFunctionService(Number stepWidth, Number stepHeight, Number latency) {

        if (latency.lt(Number.ZERO))
            throw new IllegalArgumentException("Latency has to be >= 0");

        ArrayList<Segment> segments = new ArrayList<>();

        if (latency.eq(Number.ZERO)) {
            segments.add(new Segment(Number.ZERO, stepWidth, Number.ZERO, Number.ZERO));
            return new Curve(new Sequence(new LinkedList<Segment>(segments)), Number.ZERO, stepWidth, stepHeight);
        } else {
            segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));
            segments.add(new Segment(latency, latency.add(stepWidth), stepHeight, Number.ZERO));
            return new Curve(new Sequence(new LinkedList<Segment>(segments)), latency, stepWidth, stepHeight);
        }
    }

    public static Curve createStepFunctionService(double stepWidth, double stepHeight, double latency) {
        return createStepFunctionService(new Number(stepWidth), new Number(stepHeight), new Number(latency));
    }

    public static Curve createStepFunctionArrival(Number stepWidth, Number stepHeight, Number burst) {

        if (burst.lt(Number.ZERO))
            throw new IllegalArgumentException("Burst has to be >= 0");

        ArrayList<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, stepWidth, burst, Number.ZERO));
        return new Curve(new Sequence(new LinkedList<Segment>(segments)), Number.ZERO, stepWidth, stepHeight);
    }

    public static Curve createStepFunctionArrival(double stepWidth, double stepHeight, double burst) {
        return createStepFunctionArrival(new Number(stepWidth), new Number(stepHeight), new Number(burst));
    }

    public static Curve createDelayedInfiniteBurst(Number delay) {

        if (delay.lt(Number.ZERO))
            throw new IllegalArgumentException("Delay has to be >= 0");

        ArrayList<Segment> segments = new ArrayList<>();

        if (delay.eq(Number.ZERO)) {
            segments.add(new Segment(Number.ZERO, Number.ONE, Number.POSITIVE_INFINITY, Number.ZERO));
            return new Curve(new Sequence(new LinkedList<Segment>(segments)), Number.ZERO, Number.ONE, Number.ZERO);
        } else {
            segments.add(new Segment(Number.ZERO, delay, Number.ZERO, Number.ZERO));
            segments.add(new Segment(delay, delay.add(Number.ONE), Number.POSITIVE_INFINITY, Number.ZERO));
            return new Curve(new Sequence(new LinkedList<Segment>(segments)), delay, Number.ONE, Number.ZERO);

        }
    }

    public static Curve createPeakRate(Number rate) {
        return new Curve(new Sequence(new LinkedList<Segment>(Arrays.asList(
                new Segment(Number.ZERO, Number.ONE, Number.ZERO, rate)))), Number.ZERO, Number.ONE, rate);
    }

    public static Curve createTDMAfromPeakRate(Curve pr) throws InterruptedException {
        return CurveFactory.createTDMA(pr.getPseudoPeriodHeight().mul(new Number(2)), new Number(0.5), new Number(0.5), Number.ZERO);
    }

    public static Curve createStepFunctionFromTB(Curve tb) throws InterruptedException {
        return CurveFactory.createStepFunctionArrival(Number.ONE, tb.getPseudoPeriodHeight(), tb.getSegment(Number.ZERO).getRightLimitAtStartTime());
    }

    public static Curve createPeriodJitterDelay(Number period, Number jitter, Number delay) {
        List<Segment> segments = new LinkedList<Segment>();
        Number startTime = Number.ZERO;
        Number i = Number.ONE;
        while (true) {
            Number endTime = startTime.add(delay).max(period.mul(i).sub(jitter));
            segments.add(new Segment(startTime, endTime, i, Number.ZERO));
            i = i.add(Number.ONE);
            if (endTime.sub(startTime).eq(period))
                break;
            startTime = endTime;
        }
        return new Curve(new Sequence(segments), startTime, period, Number.ONE);
    }

    /**
     *
     * @param rate
     * @param burst
     * @return
     */
    public static Curve createTokenBucket(Number rate, Number burst) {
        return new Curve(new Sequence(new LinkedList<Segment>(Arrays.asList(
                new Segment(Number.ZERO, Number.ONE, burst, rate)))), Number.ZERO, Number.ONE, rate);
    }

    public static Curve createTokenBucket(double rate, double burst) {
        return createTokenBucket(new Number(rate), new Number(burst));
    }

    /**
     *
     * @param rate
     * @param latency
     * @return
     */
    public static Curve createRateLatency(Number rate, Number latency) {
        return new Curve(new Sequence(new LinkedList<Segment>(Arrays.asList(
                new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO),
                new Segment(latency, latency.add(Number.ONE), Number.ZERO, rate)))), latency, Number.ONE, rate);
    }

    public static Curve createRateLatency(double rate, double latency) {
        return createRateLatency(new Number(rate), new Number(latency));
    }


}