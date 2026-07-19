package org.networkcalculus.dnc.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.networkcalculus.dnc.backend.number.Number;
import org.networkcalculus.dnc.utils.CurveFunctionsUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class CurveUtilsTest {
    @Test
    @DisplayName("Backlog between TB and RL")
    void testBacklog1() {
        Number latency = new Number(5);
        Number R = new Number(3);
        Number r = new Number(2);
        Number burst = new Number(3);


        Curve arrival = CurveFactory.createTokenBucket(r, burst);
        Curve service = CurveFactory.createRateLatency(R, latency);
        assertTrue(CurveUtils.getFirstIntersection(arrival, service).neq(Number.ZERO) || CurveUtils.getFirstIntersection(arrival, service).neq(Number.POSITIVE_INFINITY));


        assertTrue(CurveUtils.backlog(arrival, service).eq(new Number(13)));
    }

    @Test
    @DisplayName("Backlog between TB and RL with same slope")
    void testBacklog2() {
        Number latency = new Number(5);
        Number R = new Number(3);
        Number r = new Number(3);
        Number burst = Number.ONE;

        Curve arrival = CurveFactory.createTokenBucket(r, burst);
        Curve service = CurveFactory.createRateLatency(R, latency);

        assertTrue(CurveUtils.getExtendValue(arrival, service).eq(Number.ZERO));

        Number expected = new Number(16);
        Number actual = CurveUtils.backlog(arrival, service);
        assertEquals(expected.toDouble(), actual.toDouble());
    }

    @Test
    @DisplayName("Backlog Step Functions")
    void testBacklog3() {
        Number latency = new Number(10);

        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));
        segments.add(new Segment(latency, latency.add(new Number(2)), Number.ZERO, new Number(3)));
        segments.add(new Segment(latency.add(new Number(2)), latency.add(new Number(4)), new Number(6), Number.ZERO));
        Curve c1 = CurveFactory.createStepFunctionArrival(new Number(2), new Number(2), new Number(2));
        Curve c2 = new Curve(new Sequence(segments), latency, new Number(4), new Number(6));


        Number expected = new Number(12);
        Number actual = CurveUtils.backlog(c1, c2);

        assertEquals(expected.toDouble(), actual.toDouble());
    }

    @Test
    @DisplayName("Backlog TB + RL, diverging")
    void testBacklog4() {
        Number R = new Number(5);
        Number latency = new Number(5);
        Number r = new Number(10);
        Number burst = new Number(3);

        Curve c1 = CurveFactory.createTokenBucket(r, burst);
        Curve c2 = CurveFactory.createRateLatency(R, latency);

        Number expected = Number.POSITIVE_INFINITY;
        Number actual = CurveUtils.backlog(c1, c2);

        assertTrue(expected.eq(actual));
    }

    @Test
    @DisplayName("Backlog step-functions, diverging")
    void testBacklog5() {
        Number latency = new Number(10);

        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));
        segments.add(new Segment(latency, latency.add(new Number(2)), Number.ZERO, new Number(3)));
        segments.add(new Segment(latency.add(new Number(2)), latency.add(new Number(4)), new Number(6), Number.ZERO));
        Curve c1 = CurveFactory.createStepFunctionArrival(new Number(1), new Number(10), new Number(2));
        Curve c2 = new Curve(new Sequence(segments), latency, new Number(4), new Number(6));

        Number expected = Number.POSITIVE_INFINITY;
        Number actual = CurveUtils.backlog(c1, c2);

        assertTrue(expected.eq(actual));

    }

    @Test
    @DisplayName("Backlog step-functions, no divergence and no intersection")
    void testBacklog6() {
        List<Segment> segments = new ArrayList<>();

        segments.add(new Segment(
                Number.ZERO,
                new Number(20, 67),
                new Number(0),
                new Number(0)
        ));

        segments.add(new Segment(
                new Number(20, 67),
                new Number(1, 2),
                new Number(2),
                new Number(67, 10)
        ));

        segments.add(new Segment(
                new Number(1, 2),
                new Number(87, 67),
                new Number(67, 20),
                new Number(0)
        ));

        segments.add(new Segment(
                new Number(87, 67),
                new Number(3, 2),
                new Number(67, 20),
                new Number(67, 10)
        ));

        segments.add(new Segment(
                new Number(3, 2),
                new Number(154, 67),
                new Number(47, 10),
                new Number(0)
        ));

        Curve service = new Curve(
                new Sequence(segments),
                new Number(87, 67),
                new Number(1),
                new Number(27, 20)
        );

        Number r = Number.ONE;
        Number burst = Number.ONE;


        Segment arrivalSegment = new Segment(Number.ZERO, Number.ONE, Number.ONE, Number.ZERO);
        List<Segment> arrivalSegments = new ArrayList<>();
        arrivalSegments.add(arrivalSegment);
        Curve arrival = new Curve(new Sequence(arrivalSegments), Number.ZERO, Number.ONE, Number.ONE);
        assertEquals(CurveUtils.backlog(arrival, service).toDouble(), arrival.getValue(new Number(20, 67)).toDouble());
    }

    @Test
    @DisplayName("Backlog step-functions, same pseudo periodic slope")
    void testBacklog7() {
        Number latency = new Number(10);

        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));
        segments.add(new Segment(latency, latency.add(new Number(2)), Number.ZERO, new Number(3)));
        segments.add(new Segment(latency.add(new Number(2)), latency.add(new Number(4)), new Number(6), Number.ZERO));
        Curve c1 = CurveFactory.createStepFunctionArrival(new Number(1), new Number(1.5), new Number(1));
        Curve c2 = new Curve(new Sequence(segments), latency, new Number(4), new Number(6));

        Curve tmp = c2.extend(latency);
        Number expected = new Number(14.5);
        Number actual = CurveUtils.backlog(c1, c2);

        assertEquals(expected.toDouble(), actual.toDouble());

    }

    @Test
    @DisplayName("Maximum of 2 DelayInfiniteBurst Curves")
    void testMaximum1() {

        Curve a = CurveFactory.createDelayedInfiniteBurst(new Number(2));
        Curve b = CurveFactory.createDelayedInfiniteBurst(new Number(5));
        Curve result = CurveUtils.maximum(a, b);

        assertThat(result.getPseudoPeriodStart().toDouble()).isEqualTo(2);
        assertThat(CurveFunctionsUtils.isDelayedInfiniteBurst(result)).isEqualTo(true);
    }

    @Test
    @DisplayName("Maximum of TB + DelayedInfiniteBurst")
    void testMaximum2() {

        Curve a = CurveFactory.createDelayedInfiniteBurst(new Number(2));
        Curve b = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        Curve result = CurveUtils.maximum(a, b);

        assertThat(result.getPseudoPeriodStart().toDouble()).isEqualTo(2);
        assertThat(result.getValue(Number.ONE).toDouble()).isEqualTo(2);
        assertThat(result.getBaseSequence().getSegments().getLast().getRightLimitAtStartTime().toDouble()).isEqualTo(Number.POSITIVE_INFINITY.toDouble());
    }

    @Test
    @DisplayName("Maximum of TB + RL")
    void testMaximum3() {

        Curve a = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve b = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        Curve result = CurveUtils.maximum(a, b);

        assertThat(result.getBaseSequence().getSegments().getFirst().getRightLimitAtStartTime().toDouble()).isEqualTo(Number.ONE.toDouble());
        assertThat(result.getValue(new Number(4)).toDouble()).isEqualTo(new Number(6).toDouble());

    }

    @Test
    @DisplayName("Curve Subtraction TB - RL")
    void testSub1() {
        Curve a = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        Curve b = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve result = CurveUtils.sub(a, b);
        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ONE.toDouble());
        // Slope 0 because of non-decreasing closure
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());
    }

    @Test
    @DisplayName("Curve Subtraction RL - TB")
    void testSub2() {
        Curve a = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve b = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        Number x = CurveUtils.getFirstIntersection(a, b);
        assertThat(x.toDouble()).isEqualTo(new Number(3).toDouble());
        Curve result = CurveUtils.sub(a, b);
        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ZERO.toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(1).toDouble());

    }

    @Test
    @DisplayName("Curve Subtraction TB - RL, no intersection")
    void testSub3() {
        Curve a = CurveFactory.createTokenBucket(new Number(2), new Number(1));
        Curve b = CurveFactory.createRateLatency(new Number(1), Number.ONE);
        Curve result = CurveUtils.sub(b, a);
        // Slope 0 because of non-decreasing closure
        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());
    }

    @Test
    @DisplayName("Curve Subtraction TB - RL shifted")
    void testSub4() {
        Curve arrival = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        arrival = CurveUtils.shiftRight(arrival, new Number(1));

        Curve service = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve result = CurveUtils.sub(service, arrival);
        // Slope 0 because of non-decreasing closure
        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(1).toDouble());
    }

    @Test
    @DisplayName("Curve Subtraction TB - RL shifted 2")
    void testSub5() {
        Curve arrival = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        arrival = CurveUtils.shiftRight(arrival, new Number(1.5));

        Curve service = CurveFactory.createRateLatency(new Number(3), Number.ONE);
        Curve result = CurveUtils.sub(service, arrival);

        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ZERO.toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(2).toDouble());
    }

    @Test
    @DisplayName("Curve Subtraction StepService - StepArrival")
    void testSub6() {
        Curve arrival = CurveFactory.createStepFunctionArrival(new Number(1.05), new Number(0.4), new Number(0.4));
        Curve service = CurveFactory.createStepFunctionService(new Number(1.5), new Number(1.5), new Number(1.5));
        Curve result = CurveUtils.sub(service, arrival).nonDecreasingClosure();

        // values expected in the first non-zero, non-negative and non-decreasing segment of the difference
        Number expStart = new Number(1.5);
        Number expEnd = new Number(3);
        Number expVal = new Number(0.7);
        Segment actualSeg = result.getBaseSequence().getSegments().get(1);

        assertThat(actualSeg.getStartTime().toDouble()).isEqualTo(expStart.toDouble());
        assertThat(actualSeg.getEndTime().toDouble()).isEqualTo(expEnd.toDouble());
        assertThat(actualSeg.getRightLimitAtStartTime().toDouble()).isEqualTo(expVal.toDouble());

    }

    @Test
    @DisplayName("Delay FIFO TB + RL")
    void testDelay1() {
        Curve arrival = CurveFactory.createTokenBucket(Number.ONE, Number.ONE);
        Curve service = CurveFactory.createRateLatency(new Number(2), Number.ONE);

        Number delay = CurveUtils.delayFIFO(arrival, service);
        Number delayExp = new Number(1.5);
        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());
    }

    @Test
    @DisplayName("Delay ARB TB + RL")
    void testDelay2() {

        Number R = new Number(2);
        Number r = new Number(1);
        Number b = new Number(1);
        Number T = new Number(1);


        Curve arrival = CurveFactory.createTokenBucket(r, b);
        Curve service = CurveFactory.createRateLatency(R, T);

        Number delay = CurveUtils.delayARB(arrival, service);
        // backlogged period: (b+R*T)/(R-r)
        Number delayExp = b.add(R.mul(T)).div(R.sub(r));
        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());
    }

    @Test
    @DisplayName("Delay FIFO step functions")
    void testDelay3() {

        Curve arrival = CurveFactory.createStepFunctionArrival(Number.ONE, Number.ONE, Number.ONE);
        Curve service = CurveFactory.createStepFunctionService(Number.ONE, new Number(2), new Number(2.5));

        Number delay = CurveUtils.delayFIFO(arrival, service);
        Number delayExp = new Number(2.5);

        System.out.println("delay: " + delay.toDouble());
        System.out.println("delayExp: " + delayExp.toDouble());
        System.out.println("equal: " + delay.eq(delayExp));

        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());

    }

    @Test
    @DisplayName("Delay ARB step functions")
    void testDelay4() {

        Curve arrival = CurveFactory.createStepFunctionArrival(Number.ONE, Number.ONE, Number.ONE);
        Curve service = CurveFactory.createStepFunctionService(Number.ONE, new Number(2), new Number(2.5));

        Number delay = CurveUtils.delayARB(arrival, service);
        Number delayExp = new Number(3.5);

        System.out.println("delay: " + delay.toDouble());
        System.out.println("delayExp: " + delayExp.toDouble());
        System.out.println("equal: " + delay.eq(delayExp));

        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());

    }

    @Test
    @DisplayName("Delay ARB step functions, only virtual intersection")
    void testDelay5() {

        Curve arrival = CurveFactory.createStepFunctionArrival(Number.ONE, Number.ONE, Number.ONE);
        Curve service = CurveFactory.createStepFunctionService(Number.ONE, new Number(1.75), new Number(2.5));

        Number delay = CurveUtils.delayARB(arrival, service);
        Number delayExp = new Number(4.5);

        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());
    }

    @Test
    @DisplayName("Delay ARB step functions, only touching intersection")
    void testDelay6() {

        Curve arrival = CurveFactory.createStepFunctionArrival(Number.ONE, Number.ONE, Number.ONE);
        Curve service = CurveFactory.createStepFunctionService(Number.ONE, new Number(2), new Number(2));

        Number delay = CurveUtils.delayARB(arrival, service);
        Number delayExp = new Number(2);

        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());
    }

    @Test
    @DisplayName("Delay FIFO step functions, where the SC 'leapfrogs' the AC")
    void testDelay7() {
        Curve arrival = CurveFactory.createStepFunctionArrival(new Number(1.05), new Number(0.4), new Number(0.4));
        Curve service = CurveFactory.createStepFunctionService(new Number(1.5), new Number(1.5), new Number(1.5));

        Number delay = CurveUtils.delayFIFO(arrival, service);
        Number delayExp = new Number(1.5);

        assertThat(delay.toDouble()).isEqualTo(delayExp.toDouble());
    }

    @Test
    @DisplayName("Delay FIFO TB + RL")
    void testDelay8() {
        Curve arrival = CurveFactory.createTokenBucket(new Number(1), new Number(4));
        Curve service = CurveFactory.createRateLatency(new Number(2), Number.ZERO);

        assertThat(CurveUtils.delayFIFO(arrival, service).toDouble()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Shift left by one")
    public void testShiftLeft1() {
        Number dx = Number.ONE;

        // create rate latency got vs expected
        Number latency = new Number(4);
        Number R = new Number(3);
        Curve serviceTestCurve = CurveFactory.createRateLatency(R, latency);
        Curve serviceActualCurve = CurveFactory.createRateLatency(R, latency.sub(dx));

        // create token bucket got vs expected
        Number burst = new Number(3);
        Number r = new Number(2);
        Curve arrivalTestCurve = CurveFactory.createTokenBucket(r, burst);
        Curve arrivalActualCurve = CurveFactory.createTokenBucket(r, burst.add(dx.mul(r)));

        serviceTestCurve = CurveUtils.shiftLeft(serviceTestCurve, dx);
        arrivalTestCurve = CurveUtils.shiftLeft(arrivalTestCurve, dx);
        serviceTestCurve.optimize();
        assertEquals(serviceActualCurve, serviceTestCurve);
        assertEquals(arrivalActualCurve, arrivalTestCurve);
    }

    @Test
    @DisplayName("Shift left by small positive integer")
    public void testShiftLeft2() {
        // must be < 4 in order to test case where the curves are not transformed in the
        Number dx = new Number(3);

        // create rate latency got vs expected
        Number latency = new Number(4);
        Number R = new Number(3);
        Curve serviceActualCurve = CurveFactory.createRateLatency(R, latency);
        Curve serviceExpectedCurve = CurveFactory.createRateLatency(R, latency.sub(dx));

        // create token bucket got vs expected
        Number burst = new Number(3);
        Number r = new Number(2);
        Curve arrivalActualCurve = CurveFactory.createTokenBucket(r, burst);
        Curve arrivalExpectedCurve = CurveFactory.createTokenBucket(r, burst.add(dx.mul(r)));
        // serviceExpectedCurve = serviceExpectedCurve.extend(
        // serviceExpectedCurve.getBaseSequence().getSegments().getLast().getEndTime().add(dx));

        serviceActualCurve = CurveUtils.shiftLeft(serviceActualCurve, dx);
        arrivalActualCurve = CurveUtils.shiftLeft(arrivalActualCurve, dx);

        assertEquals(serviceExpectedCurve, serviceActualCurve);
        assertEquals(arrivalExpectedCurve, arrivalActualCurve);
    }

    @Test
    @DisplayName("Shift left by big positive integer should transform a RL curve into a TB curve.")
    public void testShiftLeft3() {
        Number dx = new Number(100);

        // create rate latency got vs expected
        Number latency = new Number(4);
        Number R = new Number(3);
        Curve serviceTestCurve = CurveFactory.createRateLatency(R, latency);
        // shifting by a dx that is greater than the latency, should create an arrival
        // curve
        Curve serviceActualCurve = CurveFactory.createTokenBucket(R, R.mul(dx.add(latency)));

        // create token bucket got vs expected
        Number burst = new Number(3);
        Number r = new Number(2);
        Curve arrivalTestCurve = CurveFactory.createTokenBucket(r, burst);
        Curve arrivalActualCurve = CurveFactory.createTokenBucket(r, burst.add(dx.mul(r)));

        serviceActualCurve = CurveUtils.shiftLeft(serviceTestCurve, dx);
        arrivalActualCurve = CurveUtils.shiftLeft(arrivalTestCurve, dx);

        assertEquals(arrivalActualCurve, arrivalTestCurve);
        assertEquals(serviceActualCurve, serviceTestCurve);
    }

    @Test
    @DisplayName("Shift a Token Bucket (one segment) curve to the right")
    public void testShiftRight1() {
        Number dx = new Number(2);

        // create token bucket got vs expected
        Number burst = new Number(3);
        Number r = new Number(3);
        Curve actualCurve = CurveFactory.createTokenBucket(r, burst);

        List<Segment> shiftedSegments = new ArrayList<>();
        Segment newSegment = new Segment(Number.ZERO, dx, Number.ZERO, Number.ZERO);
        Segment shiftedSegment = new Segment(dx, Number.ONE.add(dx), burst, r);
        shiftedSegments.add(newSegment);
        shiftedSegments.add(shiftedSegment);

        Curve expectedCurve = new Curve(new Sequence(shiftedSegments), dx, actualCurve.getPseudoPeriodLength(),
                actualCurve.getPseudoPeriodHeight());

        actualCurve = CurveUtils.shiftRight(actualCurve, dx);

        assertEquals(expectedCurve, actualCurve);
    }

    @Test
    @DisplayName("Shift a Rate Latency curve to the right")
    public void testShiftRight2() {
        Number dx = new Number(1);
        Number latency = new Number(3);
        Number R = new Number(2);

        Curve actualCurve = CurveFactory.createRateLatency(R, latency);
        Curve expectedCurve = CurveFactory.createRateLatency(R, latency.add(dx));

        actualCurve = CurveUtils.shiftRight(actualCurve, dx);

        assertEquals(expectedCurve, actualCurve);
    }

    @Test
    @DisplayName("Shift a curve with multiple segments by one")
    public void testShiftRight3() {
        Number dx = Number.ONE;
        List<Segment> segments = new ArrayList<>();

        Segment firstSegment = new Segment(Number.ZERO, new Number(4), Number.ZERO, Number.ZERO);
        Segment secondSegment = new Segment(new Number(4), new Number(8), Number.ZERO, new Number(2));
        Segment thirdSegment = new Segment(new Number(8), new Number(10), new Number(8), Number.ZERO);
        segments.add(firstSegment);
        segments.add(secondSegment);
        segments.add(thirdSegment);

        Curve c = new Curve(new Sequence(segments), new Number(8), new Number(2), new Number(2));

        // expected shifted segments
        List<Segment> shiftedSegments = new ArrayList<>();

        Segment firstSegmentShifted = new Segment(Number.ZERO, new Number(5), Number.ZERO, Number.ZERO);
        Segment secondSegmentShifted = new Segment(new Number(5), new Number(9), Number.ZERO, new Number(2));
        Segment thirdSegmentShifted = new Segment(new Number(9), new Number(11), new Number(8), Number.ZERO);
        shiftedSegments.add(firstSegmentShifted);
        shiftedSegments.add(secondSegmentShifted);
        shiftedSegments.add(thirdSegmentShifted);

        Curve expectedCurve = new Curve(new Sequence(shiftedSegments), new Number(9), new Number(2),
                new Number(2));

        Curve actualCurve = CurveUtils.shiftRight(c, dx);

        assertEquals(expectedCurve, actualCurve);

    }

    @Test
    @DisplayName("Shift a curve with multiple segments and a burst by one")
    public void testShiftRight4() {
        Number dx = Number.ONE;
        List<Segment> segments = new ArrayList<>();

        Segment firstSegment = new Segment(Number.ZERO, Number.ONE, new Number(3), Number.ZERO);
        Segment secondSegment = new Segment(Number.ONE, new Number(8), new Number(3), new Number(2));
        Segment thirdSegment = new Segment(new Number(8), new Number(10), new Number(17), Number.ZERO);
        segments.add(firstSegment);
        segments.add(secondSegment);
        segments.add(thirdSegment);

        Curve c = new Curve(new Sequence(segments), new Number(8), new Number(2), new Number(2));

        // expected shifted segments
        List<Segment> shiftedSegments = new ArrayList<>();

        Segment firstSegmentShifted = new Segment(Number.ZERO, new Number(2), Number.ZERO, Number.ZERO);
        Segment secondSegmentShifted = new Segment(new Number(2), new Number(9), new Number(3), new Number(2));
        Segment thirdSegmentShifted = new Segment(new Number(9), new Number(11), new Number(17), Number.ZERO);
        shiftedSegments.add(firstSegmentShifted);
        shiftedSegments.add(secondSegmentShifted);
        shiftedSegments.add(thirdSegmentShifted);

        // shiftedSegments.add(fourthSegmentShifted);

        Curve expectedCurve = new Curve(new Sequence(shiftedSegments), new Number(9), new Number(2),
                new Number(2));

        Curve actualCurve = CurveUtils.shiftRight(c, dx);

        assertEquals(expectedCurve, actualCurve);

    }

    @Test
    @DisplayName("Test intersection of two Segments that do not intersect in their interval")
    public void testIntersection1() {
        Number result;

        Segment seg1 = new Segment(Number.ZERO, new Number(4), new Number(5), Number.ONE);
        Segment seg2 = new Segment(Number.ZERO, new Number(4), Number.ZERO, new Number(2));

        result = Segment.getIntersection(seg1, seg2);


        assertNull(result);
    }

    @Test
    @DisplayName("Test intersection of two Segments that intersect in their interval")
    public void testsIntersection2() {
        Number result;

        Segment seg1 = new Segment(Number.ZERO, new Number(5), new Number(5), Number.ONE);
        Segment seg2 = new Segment(Number.ZERO, new Number(5), Number.ZERO, new Number(2));

        result = Segment.getIntersection(seg1, seg2);


        assertTrue(result.eq(new Number(5)));
    }

    @Test
    @DisplayName("Test intersection of two Segments whose intersection is not in the interval [max(seg1.start,seg2.start), min(seg1.end, seg2.end)")
    public void testIntersection3() {
        Number result;
        Segment seg1 = new Segment(Number.ZERO, new Number(5), new Number(5), Number.ONE);
        Segment seg2 = new Segment(Number.ONE, new Number(4), Number.ZERO, new Number(2));

        result = Segment.getIntersection(seg1, seg2);

        assertNull(result);
    }

    @Test
    @DisplayName("Test intersection of two Curves more than one pseudo periodic segments")
    public void testIntersection4() {
        Number latency = new Number(100);

        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(Number.ZERO, latency, Number.ZERO, Number.ZERO));
        segments.add(new Segment(latency, latency.add(new Number(2)), Number.ZERO, new Number(3)));
        segments.add(new Segment(latency.add(new Number(2)), latency.add(new Number(4)), new Number(6), Number.ZERO));

        Curve c1 = new Curve(new Sequence(segments), latency, new Number(4), new Number(6));

        Curve c2 = CurveFactory.createStepFunctionArrival(new Number(2), new Number(2), new Number(2));

        System.out.println(CurveUtils.getExtendValue(c1, c2));
        System.out.println(CurveUtils.getFirstIntersection(c1, c2));

        assertThat(CurveUtils.getFirstIntersection(c1, c2).toDouble()).isEqualTo(Number.ZERO.toDouble());

        assertThat(CurveUtils.getLastIntersection(c1, c2).toDouble()).isEqualTo(new Number(304).toDouble());
    }

    @Test
    @DisplayName("Test intersection when curves need to be extended")
    public void testIntersection5() {
        Number R = new Number(100);
        Number latency = new Number(100);
        Number r = new Number(10);
        Number burst = new Number(1000);

        Curve c1 = CurveFactory.createRateLatency(R, latency);
        Curve c2 = CurveFactory.createTokenBucket(r, burst);

        assertThat(CurveUtils.getFirstIntersection(c1, c2).eq(new Number(1100, 9)));
    }

    @Test
    @DisplayName("Test intersection between Curves with same slope but not overlapping")
    public void testIntersection6() {
        Number b1 = new Number(5);
        Number b2 = new Number(10);
        Number r = new Number(2);

        Curve c1 = CurveFactory.createTokenBucket(r, b1);
        Curve c2 = CurveFactory.createTokenBucket(r, b2);

        assertTrue(Number.ZERO.eq(CurveUtils.getExtendValue(c1, c2)));

        assertTrue(Number.POSITIVE_INFINITY.eq(CurveUtils.getFirstIntersection(c1, c2)));
    }

    @Test
    @DisplayName("Test intersection between two overalpping curves")
    public void testIntersection7() {
        Number b1 = new Number(5);
        Number r = new Number(2);

        Curve c1 = CurveFactory.createTokenBucket(r, b1);
        Curve c2 = CurveFactory.createTokenBucket(r, b1);

        assertThat(CurveUtils.getFirstIntersection(c1, c2).toDouble()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Test overlapping intersection of two complex curves")
    public void testIntersection8() {
        Number b1 = new Number(5);
        Number r = new Number(2);

        Curve c1 = CurveFactory.createTokenBucket(r, b1);

        List<Segment> segments = new ArrayList<>();
        Segment firstSeg = new Segment(Number.ZERO, Number.ONE, Number.ZERO, Number.ZERO);
        Segment secondSeg = new Segment(Number.ONE, new Number(2), Number.ZERO, new Number(4));
        Segment thirdSeg = new Segment(new Number(2), new Number(3), new Number(4), new Number(2));
        segments.add(firstSeg);
        segments.add(secondSeg);
        segments.add(thirdSeg);
        Curve c2 = new Curve(new Sequence(segments), Number.ONE, new Number(2), new Number(6));

        System.out.println(CurveUtils.getFirstIntersection(c1, c2));


        assertTrue(new Number(15, 2).eq(CurveUtils.getFirstIntersection(c1, c2)));
    }


}
