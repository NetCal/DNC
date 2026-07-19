package org.networkcalculus.dnc.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurveTest {

    @Test
    @DisplayName("Extend pseudo periodic part of an arrival curve with one base segment and one pseudo periodic segment")
    public void testExtend1() {
        org.networkcalculus.dnc.backend.number.Number extEndTime = new org.networkcalculus.dnc.backend.number.Number(100);
        org.networkcalculus.dnc.backend.number.Number burst = new org.networkcalculus.dnc.backend.number.Number(2);
        org.networkcalculus.dnc.backend.number.Number slope = new org.networkcalculus.dnc.backend.number.Number(2);

        Curve actualCurve = CurveFactory.createTokenBucket(slope, burst);
        actualCurve = actualCurve.extend(extEndTime);

        Curve expectedCurve = CurveFactory.createTokenBucket(slope, burst);
        Segment lastSegment = expectedCurve.getBaseSequence().getSegments()
                .getLast();
        lastSegment.setEndTime(extEndTime);
        expectedCurve.setPseudoPeriodStart(lastSegment.getEndTime()
                .sub(expectedCurve.getPseudoPeriodLength()));

        assertEquals(expectedCurve, actualCurve);

    }

    @Test
    @DisplayName("Extend pseudo periodic part of service curve with one base segment and one pseudo periodic segment")
    public void testExtend2() {
        // extEndTime needs to be greater than the PseudoPeriodicStart value of the
        // curve
        org.networkcalculus.dnc.backend.number.Number extEndTime = new org.networkcalculus.dnc.backend.number.Number(3);
        org.networkcalculus.dnc.backend.number.Number latency = new org.networkcalculus.dnc.backend.number.Number(2);
        org.networkcalculus.dnc.backend.number.Number slope = new org.networkcalculus.dnc.backend.number.Number(2);

        Curve actualCurve = CurveFactory.createRateLatency(slope, latency);
        actualCurve = actualCurve.extend(extEndTime);

        Curve expectedCurve = CurveFactory.createRateLatency(slope, latency);
        Segment lastSegment = expectedCurve.getBaseSequence().getSegments()
                .getLast();
        lastSegment.setEndTime(extEndTime);
        expectedCurve.setPseudoPeriodStart(lastSegment.getEndTime()
                .sub(expectedCurve.getPseudoPeriodLength()));

        assertEquals(expectedCurve, actualCurve);

    }

    @Test
    @DisplayName("Curve.extend(endTime) for endTime.leq(Curve.getPseudoPeriodicStart) should return the Curve object unchanged")
    public void testExtend3() {
        List<org.networkcalculus.dnc.backend.number.Number> endTimeToTest = new ArrayList<>();

        // Normal corner values to test plus values that are less than the
        // pseudoPeriodicStart value.
        endTimeToTest.add(new org.networkcalculus.dnc.backend.number.Number(-1));
        endTimeToTest.add(org.networkcalculus.dnc.backend.number.Number.ZERO);
        endTimeToTest.add(new org.networkcalculus.dnc.backend.number.Number(2));
        endTimeToTest.add(org.networkcalculus.dnc.backend.number.Number.POSITIVE_INFINITY);
        endTimeToTest.add(org.networkcalculus.dnc.backend.number.Number.NEGATIVE_INFINITY);

        org.networkcalculus.dnc.backend.number.Number latency = new org.networkcalculus.dnc.backend.number.Number(2);
        org.networkcalculus.dnc.backend.number.Number r = new org.networkcalculus.dnc.backend.number.Number(2);

        // Should remain unchanged
        Curve expectedCurve = CurveFactory.createRateLatency(r, latency);

        for (org.networkcalculus.dnc.backend.number.Number endTime : endTimeToTest) {
            Curve actualCurve = CurveFactory.createRateLatency(r, latency);
            assertEquals(expectedCurve, actualCurve.extend(endTime));
        }

    }

    @Test
    @DisplayName("Create only one new segment when extending a curve with multiple segments included in the pseudo periodic part.")
    public void testExtend4() {
        org.networkcalculus.dnc.backend.number.Number endTimeToTest = new org.networkcalculus.dnc.backend.number.Number(8);

        List<Segment> expectedSegments = new ArrayList<>();
        Segment firstSegment = new Segment(org.networkcalculus.dnc.backend.number.Number.ZERO, new org.networkcalculus.dnc.backend.number.Number(2), org.networkcalculus.dnc.backend.number.Number.ONE, org.networkcalculus.dnc.backend.number.Number.ONE);
        Segment secondSegment = new Segment(new org.networkcalculus.dnc.backend.number.Number(2), new org.networkcalculus.dnc.backend.number.Number(4),
                firstSegment.getValue(firstSegment.getEndTime()), org.networkcalculus.dnc.backend.number.Number.ZERO);
        Segment thirdSegment = new Segment(new org.networkcalculus.dnc.backend.number.Number(4), new org.networkcalculus.dnc.backend.number.Number(6),
                secondSegment.getValue(secondSegment.getEndTime()), new org.networkcalculus.dnc.backend.number.Number(2));

        expectedSegments.add(firstSegment);
        expectedSegments.add(secondSegment);
        expectedSegments.add(thirdSegment);

        Curve expectedCurve = new Curve(new Sequence(expectedSegments), new org.networkcalculus.dnc.backend.number.Number(2), new org.networkcalculus.dnc.backend.number.Number(4),
                new org.networkcalculus.dnc.backend.number.Number(4));
        Curve actualCurve = expectedCurve.extend(endTimeToTest);

        List<Segment> extendedSegments = new ArrayList<>(expectedSegments);
        Segment fourthSegment = new Segment(thirdSegment.getEndTime(), endTimeToTest,
                thirdSegment.getValue(thirdSegment.getEndTime()), secondSegment.getSlope());
        extendedSegments.add(fourthSegment);

        expectedCurve = new Curve(new Sequence(extendedSegments),
                endTimeToTest.sub(expectedCurve.getPseudoPeriodLength()),
                expectedCurve.getPseudoPeriodLength(),
                expectedCurve.getPseudoPeriodHeight());

        assertEquals(expectedCurve, actualCurve);

    }

    @Test
    @DisplayName("Create two new segments when extending a curve with multiple segments included in the pseudo periodic part.")
    public void testExtend5() {
        org.networkcalculus.dnc.backend.number.Number endTimeToTest = new org.networkcalculus.dnc.backend.number.Number(10);

        List<Segment> expectedSegments = new ArrayList<>();
        Segment firstSegment = new Segment(org.networkcalculus.dnc.backend.number.Number.ZERO, new org.networkcalculus.dnc.backend.number.Number(2), org.networkcalculus.dnc.backend.number.Number.ONE, org.networkcalculus.dnc.backend.number.Number.ONE);
        Segment secondSegment = new Segment(new org.networkcalculus.dnc.backend.number.Number(2), new org.networkcalculus.dnc.backend.number.Number(4),
                firstSegment.getValue(firstSegment.getEndTime()), org.networkcalculus.dnc.backend.number.Number.ZERO);
        Segment thirdSegment = new Segment(new org.networkcalculus.dnc.backend.number.Number(4), new org.networkcalculus.dnc.backend.number.Number(6),
                secondSegment.getValue(secondSegment.getEndTime()), new org.networkcalculus.dnc.backend.number.Number(2));

        expectedSegments.add(firstSegment);
        expectedSegments.add(secondSegment);
        expectedSegments.add(thirdSegment);

        Curve expectedCurve = new Curve(new Sequence(expectedSegments), new org.networkcalculus.dnc.backend.number.Number(2), new org.networkcalculus.dnc.backend.number.Number(4),
                new Number(4));
        Curve actualCurve = expectedCurve.extend(endTimeToTest);

        List<Segment> extendedSegments = new ArrayList<>(expectedSegments);
        Segment fourthSegment = new Segment(thirdSegment.getEndTime(),
                thirdSegment.getEndTime()
                        .add(secondSegment.getEndTime().sub(secondSegment.getStartTime())),
                thirdSegment.getValue(thirdSegment.getEndTime()), secondSegment.getSlope());
        Segment fifthSegment = new Segment(fourthSegment.getEndTime(), endTimeToTest,
                fourthSegment.getValue(fourthSegment.getEndTime()), thirdSegment.getSlope());
        extendedSegments.add(fourthSegment);
        extendedSegments.add(fifthSegment);

        expectedCurve = new Curve(new Sequence(extendedSegments),
                endTimeToTest.sub(expectedCurve.getPseudoPeriodLength()),
                expectedCurve.getPseudoPeriodLength(),
                expectedCurve.getPseudoPeriodHeight());

        assertEquals(expectedCurve, actualCurve);

    }
}