package de.uni_kl.disco.curves;

import java.util.ArrayList;
import java.util.LinkedList;

import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;
import de.uni_kl.disco.numbers.NumUtils;

public class CurveUtils {
    protected static final int OPERATOR_ADD = 0;
    protected static final int OPERATOR_SUB = 1;
    protected static final int OPERATOR_MIN = 2;
    protected static final int OPERATOR_MAX = 3;

    /**
     * Common helper for computing a new curve.
     *
     * @param curve1   Input curve 1.
     * @param curve2   Input curve 2.
     * @param operator Operation to be applied to the curves.
     * @return The resulting curve.
     */
    private static CurveUltAffine computeResultingCurve(CurveUltAffine curve1, CurveUltAffine curve2, int operator) {
        CurveUltAffine ZERO_DELAY_INFINITE_BURST = CurveFactory.createZeroDelayInfiniteBurst();

        switch (operator) {
            case OPERATOR_ADD:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            case OPERATOR_SUB:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            case OPERATOR_MIN:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return curve2.copy();
                }
                if (curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return curve1.copy();
                }
                break;
            case OPERATOR_MAX:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            default:
        }

        ArrayList<LinearSegment> result = new ArrayList<LinearSegment>();
        Num x = NumFactory.createZero();
        Num x_cross;
        boolean leftopen;

        int i1 = 0;
        int i2 = 0;
        while (i1 < curve1.getSegmentCount() || i2 < curve2.getSegmentCount()) {
            Num x_next1 = (i1 + 1 < curve1.getSegmentCount()) ?
                    curve1.getSegment(i1 + 1).getX() : NumFactory.createPositiveInfinity();
            Num x_next2 = (i2 + 1 < curve2.getSegmentCount()) ?
                    curve2.getSegment(i2 + 1).getX() : NumFactory.createPositiveInfinity();
            Num x_next = NumUtils.min(x_next1, x_next2);

            leftopen = curve1.getSegment(i1).isLeftopen() || curve2.getSegment(i2).isLeftopen();

            switch (operator) {
                case OPERATOR_ADD:
                    result.add(LinearSegmentUtils.add(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen));
                    break;
                case OPERATOR_SUB:
                    result.add(LinearSegmentUtils.sub(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen));
                    break;
                case OPERATOR_MIN:
                    x_cross = curve1.getSegment(i1).getXIntersectionWith(curve2.getSegment(i2));
                    if (x_cross.equals(NumFactory.getNaN())) {
                        x_cross = NumFactory.createPositiveInfinity();
                    }
                    if (x.lt(x_cross) && x_cross.lt(x_next)) {
                        result.add(LinearSegmentUtils.min(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                        result.add(LinearSegmentUtils.min(curve1.getSegment(i1), curve2.getSegment(i2), x_cross, false, true));
                    } else {
                        result.add(LinearSegmentUtils.min(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                    }
                    break;
                case OPERATOR_MAX:
                    x_cross = curve1.getSegment(i1).getXIntersectionWith(curve2.getSegment(i2));
                    if (x_cross.equals(NumFactory.getNaN())) {
                        x_cross = NumFactory.createPositiveInfinity();
                    }
                    if (x.lt(x_cross) && x_cross.lt(x_next)) {
                        result.add(LinearSegmentUtils.max(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                        result.add(LinearSegmentUtils.max(curve1.getSegment(i1), curve2.getSegment(i2), x_cross, false, true));
                    } else {
                        result.add(LinearSegmentUtils.max(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                    }
                    break;
                default:
            }

            if (x_next1.equals(x_next)) {
                i1++;
            }
            if (x_next2.equals(x_next)) {
                i2++;
            }
            x = x_next;
        }

        return CurveFactory.createCurve(result);
    }

    /**
     * Returns a curve that is the sum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise sum of the given curves.
     */
    public static CurveUltAffine add(CurveUltAffine curve1, CurveUltAffine curve2) {
        return computeResultingCurve(curve1, curve2, OPERATOR_ADD);
    }

    /**
     * Returns a curve that is the difference between this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise difference of the given curves, i.e., curve1 - curve2.
     */
    public static CurveUltAffine sub(CurveUltAffine curve1, CurveUltAffine curve2) {
        return computeResultingCurve(curve1, curve2, OPERATOR_SUB);
    }

    /**
     * Returns a curve that is the minimum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise minimum of the given curves.
     */
    public static CurveUltAffine min(CurveUltAffine curve1, CurveUltAffine curve2) {
        return computeResultingCurve(curve1, curve2, OPERATOR_MIN);
    }

    /**
     * Returns a curve that is the maximum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise maximum of the given curves.
     */
    public static CurveUltAffine max(CurveUltAffine curve1, CurveUltAffine curve2) {
        return computeResultingCurve(curve1, curve2, OPERATOR_MAX);
    }

    /**
     * Returns a copy of curve bounded at the x-axis.
     *
     * @param curve the curve to bound.
     * @return the bounded curve.
     */
    public static CurveUltAffine boundAtXAxis(CurveUltAffine curve) {
        CurveUltAffine curve_copy = curve.copy();

        ArrayList<LinearSegment> result = new ArrayList<LinearSegment>();
        LinearSegment s;
        for (int i = 0; i < curve_copy.getSegmentCount(); i++) {
            if (curve_copy.getSegment(i).getY().gtZero()) {
                result.add(curve_copy.getSegment(i));

                if (curve_copy.getSegment(i).getGrad().ltZero()) {
                    Num x_cross = curve_copy.getSegment(i).getXIntersectionWith(LinearSegmentUtils.getXAxis());
                    if (i + 1 >= curve_copy.getSegmentCount() || x_cross.lt(curve_copy.getSegment(i + 1).getX())) {
                        s = LinearSegmentFactory.createHorizontalLine(0.0);
                        s.setX(x_cross);
                        result.add(s);
                    }
                }
            } else {
                s = LinearSegmentFactory.createHorizontalLine(0.0);
                s.setX(curve_copy.getSegment(i).getX());
                s.setLeftopen(curve_copy.getSegment(i).isLeftopen());
                result.add(s);

                if (curve_copy.getSegment(i).getGrad().gtZero()) {
                    Num x_cross = curve_copy.getSegment(i).getXIntersectionWith(LinearSegmentUtils.getXAxis());
                    if (i + 1 >= curve_copy.getSegmentCount() || x_cross.lt(curve_copy.getSegment(i + 1).getX())) {
                        s = LinearSegmentFactory.createHorizontalLine(0.0);
                        s.setX(x_cross);
                        s.setGrad(curve_copy.getSegment(i).getGrad());
                        result.add(s);
                    }
                }
            }
        }

        return CurveFactory.createCurve(result);
    }

    /**
     * Returns the maximum vertical deviation between the given two curves.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the vertical deviation.
     */
    public static Num getMaxVerticalDeviation(CurveUltAffine c1, CurveUltAffine c2) {
        if (c1.getUltAffineRate().gt(c2.getUltAffineRate())) {
            return NumFactory.createPositiveInfinity();
        }
        // The computeInflectionPoints based method does not work for
        // single rate service curves (without latency)
        // in conjunction with token bucket arrival curves
        // because their common inflection point is in zero,
        // where the arrival curve is 0.0 by definition.
        // This leads to a vertical deviation of 0 the arrival curve's burst
        // (or infinity which is already handled by the first if-statement)

        // Solution:
        // Start with the burst as minimum of all possible solutions for the deviation instead of negative infinity.

        Num burst_c1 = c1.fLimitRight(NumFactory.getZero());
        Num burst_c2 = c2.fLimitRight(NumFactory.getZero());
        Num result = NumUtils.diff(burst_c1, burst_c2);

        ArrayList<Num> xcoords = computeInflectionPointsX(c1, c2);
        for (int i = 0; i < xcoords.size(); i++) {
            Num ip_x = xcoords.get(i);

            Num backlog = NumUtils.sub(c1.f(ip_x), c2.f(ip_x));
            result = NumUtils.max(result, backlog);
        }
        return result;
    }

    /**
     * Returns the maximum horizontal deviation between the given two curves.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the horizontal deviation.
     */
    public static Num getMaxHorizontalDeviation(CurveUltAffine c1, CurveUltAffine c2) {
        if (c1.getUltAffineRate().gt(c2.getUltAffineRate())) {
            return NumFactory.createPositiveInfinity();
        }

        Num result = NumFactory.createNegativeInfinity();
        for (int i = 0; i < c1.getSegmentCount(); i++) {
            Num ip_y = c1.getSegment(i).getY();

            Num delay = NumUtils.sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = NumUtils.max(result, delay);
        }
        for (int i = 0; i < c2.getSegmentCount(); i++) {
            Num ip_y = c2.getSegment(i).getY();

            Num delay = NumUtils.sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = NumUtils.max(result, delay);
        }
        return result;
    }

    /**
     * Returns an <code>ArrayList</code> instance of those x-coordinates
     * at which either c1 or c2 or both have an inflection point. There
     * will be multiple occurences of an x-coordinate, if at least one
     * curve has a discontinuity at that x-coordinate.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return an <code>ArrayList</code> of <code>Double</code> objects
     * containing the x-coordinates of the respective inflection point.
     */
    public static ArrayList<Num> computeInflectionPointsX(CurveUltAffine c1, CurveUltAffine c2) {
        ArrayList<Num> xcoords = new ArrayList<Num>();

        int i1 = 0;
        int i2 = 0;
        while (i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount()) {
            Num x1 = (i1 < c1.getSegmentCount()) ?
                    c1.getSegment(i1).getX() : NumFactory.createPositiveInfinity();
            Num x2 = (i2 < c2.getSegmentCount()) ?
                    c2.getSegment(i2).getX() : NumFactory.createPositiveInfinity();
            if (x1.lt(x2)) {
                xcoords.add(x1.copy());
                i1++;
            } else if (x1.gt(x2)) {
                xcoords.add(x2.copy());
                i2++;
            } else {
                xcoords.add(x1.copy());
                i1++;
                i2++;
            }
        }
        return xcoords;
    }

    /**
     * Returns an <code>ArrayList</code> instance of those y-coordinates
     * at which either c1 or c2 or both have an inflection point.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return an <code>ArrayList</code> of <code>Double</code> objects
     * containing the x-coordinates of the respective inflection point.
     */
    public static ArrayList<Num> computeInflectionPointsY(CurveUltAffine c1, CurveUltAffine c2) {
        ArrayList<Num> ycoords = new ArrayList<Num>();

        int i1 = 0;
        int i2 = 0;
        while (i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount()) {
            Num y1 = (i1 < c1.getSegmentCount()) ?
                    c1.getSegment(i1).getY() : NumFactory.createPositiveInfinity();
            Num y2 = (i2 < c2.getSegmentCount()) ?
                    c2.getSegment(i2).getY() : NumFactory.createPositiveInfinity();
            if (y1.lt(y2)) {
                ycoords.add(y1.copy());
                i1++;
            } else if (y1.gt(y2)) {
                ycoords.add(y2.copy());
                i2++;
            } else {
                ycoords.add(y1.copy());
                i1++;
                i2++;
            }
        }

        return ycoords;
    }

    /**
     * Returns a copy of this curve shifted vertically by <code>dy</code>.
     *
     * @param curve The curve to shift.
     * @param dy    The offset to shift the curve.
     * @return The shifted curve.
     */
    public static CurveUltAffine add(CurveUltAffine curve, Num dy) {
        CurveUltAffine result = curve.copy();
        for (int i = 0; i < curve.getSegmentCount(); i++) {
            result.getSegment(i).setY(NumUtils.add(result.getSegment(i).getY(), dy));
        }
        return result;
    }

    public static MaxServiceCurve add(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        return CurveFactory.createMaxServiceCurve(computeResultingCurve(max_service_curve_1, max_service_curve_2, OPERATOR_ADD));
    }

    public static MaxServiceCurve add(MaxServiceCurve max_service_curve_1, Num dy) {
        return CurveFactory.createMaxServiceCurve(add((CurveUltAffine) max_service_curve_1, dy));
    }

    public static MaxServiceCurve min(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        return CurveFactory.createMaxServiceCurve(computeResultingCurve(max_service_curve_1, max_service_curve_2, OPERATOR_MIN));
    }

    public static Num getXIntersection(CurveUltAffine curve1, CurveUltAffine curve2) {
        Num x_int = NumFactory.getPositiveInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.

        for (int i = 0; i < curve1.getSegmentCount(); i++) {
            boolean curve1_last = (i == curve1.getSegmentCount() - 1);

            for (int j = 0; j < curve2.getSegmentCount(); j++) {
                boolean curve2_last = (j == curve2.getSegmentCount() - 1);

                Num x_int_tmp = curve1.getSegment(i).getXIntersectionWith(curve2.getSegment(j));

                if (x_int_tmp.equals(NumFactory.getNaN())) {
                    break;
                }

                if (x_int_tmp.gtZero()) {
                    if (!curve1_last) {
                        if (!curve2_last) {
                            if (x_int_tmp.lt(curve1.getSegment(i + 1).getX())
                                    && x_int_tmp.lt(curve1.getSegment(j + 1).getX())
                                    && x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        } else {
                            if (x_int_tmp.lt(curve1.getSegment(i + 1).getX())
                                    && x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        }
                    } else {
                        if (!curve2_last) {
                            if (x_int_tmp.lt(curve1.getSegment(j + 1).getX())
                                    && x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        } else {
                            if (x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        }
                    }
                }
            }
        }
        return x_int;
    }

    public static ServiceCurve add(ServiceCurve c1, ServiceCurve c2) {
        return CurveFactory.createServiceCurve(computeResultingCurve(c1, c2, OPERATOR_ADD));
    }

    public static ServiceCurve sub(ServiceCurve c1, ArrivalCurve c2) {
        return CurveFactory.createServiceCurve(computeResultingCurve(c1, c2, OPERATOR_SUB));
    }

    public static ServiceCurve min(ServiceCurve c1, ServiceCurve c2) {
        return CurveFactory.createServiceCurve(computeResultingCurve(c1, c2, OPERATOR_MIN));
    }

    public static ServiceCurve max(ServiceCurve c1, ServiceCurve c2) {
        return CurveFactory.createServiceCurve(computeResultingCurve(c1, c2, OPERATOR_MAX));
    }

    public static ArrivalCurve add(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return CurveFactory.createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, OPERATOR_ADD));
    }

    public static ArrivalCurve add(ArrivalCurve arrival_curve_1, Num dy) {
        return CurveFactory.createArrivalCurve(add((CurveUltAffine) arrival_curve_1, dy));
    }

    public static ArrivalCurve min(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return CurveFactory.createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, OPERATOR_MIN));
    }

    public static ArrivalCurve max(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return CurveFactory.createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, OPERATOR_MAX));
    }

    /**
     * Returns a copy of this curve that is shifted to the right by <code>dx</code>,
     * i.e. g(x) = f(x-dx).
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    public static CurveUltAffine shiftRight(CurveUltAffine curve, Num dx) {
        CurveUltAffine curve_copy = curve.copy();
        if (dx.eq(0.0)) {
            return curve_copy;
        }

        LinearSegment current_segment = curve_copy.getSegment(0);
        if (!current_segment.getX().eq(0)
                || !current_segment.getY().eq(0)) {
            //throw new RuntimeException("Curve to shift right must pass through origin!:\n" + curve.toString());
        }

        if (current_segment.getGrad().gtZero() || current_segment.getY().gtZero()) {
            // Add a zero segment at the front
            curve_copy.addSegment(0, LinearSegmentFactory.createZeroSegment());
        }

        for (int i = 1; i < curve_copy.getSegmentCount(); i++) {
            curve_copy.getSegment(i).setX(NumUtils.add(curve_copy.getSegment(i).getX(), dx));
        }

        //curve_copy = (CurveUltAffine)
        beautify(curve_copy);
        return curve_copy;
    }

    /**
     * Returns a copy of this curve that is shifted to the left by <code>dx</code>,
     * i.e. g(x) = f(x+dx). Note that the new curve is clipped at the y-axis so
     * that in most cases <code>c.shiftLeftClipping(dx).shiftRight(dx) != c</code>!
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    public static CurveUltAffine shiftLeftClipping(CurveUltAffine curve, Num dx) {
        int i = curve.getSegmentDefining(dx);
        CurveUltAffine result = curve.copy();
        LinearSegment segment_i = result.getSegment(i);
        if (segment_i.getX().lt(dx)) {
            segment_i.setY(NumUtils.add(segment_i.getY(),
                    NumUtils.mult(NumUtils.sub(dx, segment_i.getX()), segment_i.getGrad())));
            segment_i.setX(dx);
            segment_i.setLeftopen(false);
        }
        for (int j = 1; j <= i; j++) {
            result.removeSegment(0);
        }
        for (i = 0; i < result.getSegmentCount(); i++) {
            result.getSegment(i).setX(NumUtils.sub(result.getSegment(i).getX(), dx));
        }

        return result;
    }

    /**
     * Returns a copy of this curve with latency removed, i.e. shifted left by
     * the latency.
     *
     * @param curve The curve to shift.
     * @return A copy of this curve without latency
     */
    public static CurveUltAffine removeLatency(CurveUltAffine curve) {
        CurveUltAffine result = curve.copy();

        if (result.getSegmentCount() == 2
                && result.getSegment(0).getX().eqZero()
                && result.getSegment(1).getX().eqZero()) { // Some curve that is only 0 in the origin
            return result;
        }

        if (result.getSegmentCount() == 1) {
            if (result.getBurst().eqZero() && result.getSegment(0).getGrad().eqZero()) { // A zeroCurve on the x-axis
                throw new IllegalArgumentException("Cannot remove the latency of a zero curve lying on the x-axis!");
            } else {
                return result;
            }
        }

        // Remove all segment(s) with y0==0.0 and grad==0.0
        while (result.getSegmentCount() > 0) {
            if (result.getSegment(0).getY().gtZero() || result.getSegment(0).getGrad().gtZero()) {
                break;
            }
            if (curve.getSegment(0).getY().ltZero() || curve.getSegment(0).getGrad().ltZero()) {
                throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
            }
            result.removeSegment(0);
        }

        // In case that we've removed everything, the curve had infinite latency, so return the NULL curve.
        if (result.getSegmentCount() == 0) {
            return CurveFactory.createZeroCurve();
        }

        // Shift remaining segments left by latency
        Num L = result.getSegment(0).getX();
        for (int i = 0; i < result.getSegmentCount(); i++) {
            result.getSegment(i).setX(NumUtils.sub(result.getSegment(i).getX(), L));
        }
        if (result.getSegment(0).isLeftopen()) {
            result.addSegment(0, LinearSegmentFactory.createHorizontalLine(0.0));
        }

        return result;
    }

    public static void beautify(Curve c) {
        //Curve c = curve.copy();
        int i = 0;
        while (i < c.getSegmentCount() - 1) {
            // Remove unreal discontinuity
            if (c.isUnrealDiscontinuity(i)) {
                c.getSegment(i + 1).setLeftopen(c.getSegment(i).isLeftopen());
                c.removeSegment(i);
                continue;
            }
            i++;
        }

        i = 0;
        while (i < c.getSegmentCount() - 1) {
            // Join colinear segments
            Num firstArg = NumUtils.sub(c.getSegment(i + 1).getGrad(), c.getSegment(i).getGrad());

            Num secondArg = NumUtils.sub(c.getSegment(i + 1).getX(), c.getSegment(i).getX());
            secondArg = NumUtils.mult(secondArg, c.getSegment(i).getGrad());
            secondArg = NumUtils.add(c.getSegment(i).getY(), secondArg);
            secondArg = NumUtils.sub(c.getSegment(i + 1).getY(), secondArg);

            if (NumUtils.abs(firstArg).lt(NumFactory.getEpsilon())
                    && NumUtils.abs(secondArg).lt(NumFactory.getEpsilon())) {

                c.removeSegment(i + 1);
                if (i + 1 < c.getSegmentCount() && !c.getSegment(i + 1).isLeftopen()) {
                    Num resultPt1 = NumUtils.sub(c.getSegment(i + 1).getY(), c.getSegment(i).getY());
                    Num resultPt2 = NumUtils.sub(c.getSegment(i + 1).getX(), c.getSegment(i).getX());

                    c.getSegment(i).setGrad(NumUtils.div(resultPt1, resultPt2));
                }
                continue;
            }
            i++;
        }

        for (i = 0; i < c.getSegmentCount() - 1; i++) {
            if (c.getSegment(i).getX().equals(c.getSegment(i + 1).getX())) {
                c.getSegment(i).setGrad(NumFactory.createZero());
            }
        }

        // Remove rate of tb arrival curves' first segment
        if (c.getSegmentCount() > 1
                && c.getSegment(0).getX() == NumFactory.getZero()
                && c.getSegment(0).getY() != NumFactory.getZero()
                && c.getSegment(1).getX() == NumFactory.getZero()
                && c.getSegment(1).getY() != NumFactory.getZero()) {
            c.getSegment(0).setGrad(NumFactory.createZero());
        }
        //clearMetaInfo();
        c.setHas_token_bucket_meta_info(false);
        c.setIs_token_bucket(false);
        c.setToken_buckets(new LinkedList<>());

        c.setHas_rate_latency_meta_info(false);
        c.setIs_rate_latency(false);
        c.setRate_latencies(new LinkedList<>());

        //return c;
    }
}
