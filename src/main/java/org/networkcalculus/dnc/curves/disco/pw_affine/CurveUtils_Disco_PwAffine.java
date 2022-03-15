/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.curves.disco.pw_affine;

import java.util.ArrayList;
import java.util.LinkedList;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.CurveUtils;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.num.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public class CurveUtils_Disco_PwAffine implements CurveUtils {
	private static CurveUtils_Disco_PwAffine instance = new CurveUtils_Disco_PwAffine();

	public static CurveUtils_Disco_PwAffine getInstance() {
		return instance;
	}

    // --------------------------------------------------------------------------------------------------------------
    // Generic Manipulations
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Returns a copy of this curve with latency removed, i.e. shifted left by the
     * latency.
     *
     * @param curve The curve to shift.
     * @return A copy of this curve without latency
     */
    public Curve removeLatency(Curve curve) {
        Curve result = curve.copy();

        if (result.getSegmentCount() == 2 && result.getSegment(0).getX().eqZero()
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

        // In case that we've removed everything, the curve had infinite latency, so
        // return the zero curve.
        if (result.getSegmentCount() == 0) {
            return Curve_ConstantPool.ZERO_CURVE.get();
        }

        // Shift remaining segments left by latency
        Num L = result.getSegment(0).getX();
        for (int i = 0; i < result.getSegmentCount(); i++) {
            result.getSegment(i).setX(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(result.getSegment(i).getX(), L));
        }
        if (result.getSegment(0).isLeftopen()) {
            result.addSegment(0, LinearSegment.createHorizontalLine(0.0));
        }

        return result;
    }

    /**
     * Returns a copy of this curve that is shifted to the right by <code>dx</code>,
     * i.e. g(x) = f(x-dx).
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    public Curve shiftRight(Curve curve, Num dx) {
        Curve curve_copy = curve.copy();
        if (dx.eq(0.0)) {
            return curve_copy;
        }

        LinearSegment current_segment = curve_copy.getSegment(0);
        if (!current_segment.getX().eq(0) || !current_segment.getY().eq(0)) {
            // TODO throw new RuntimeException("Curve to shift right must pass through
            // origin!:\n" + curve.toString());
        }

        if (current_segment.getGrad().gtZero() || current_segment.getY().gtZero()) {
            // Add a zero segment at the front
            curve_copy.addSegment(0, Curve.getXAxis());
        }

        for (int i = 1; i < curve_copy.getSegmentCount(); i++) {
            curve_copy.getSegment(i).setX(Num.getUtils(Calculator.getInstance().getNumBackend()).add(curve_copy.getSegment(i).getX(), dx));
        }

        beautify(curve_copy);
        return curve_copy;
    }

    public void beautify(Curve c) {
     if(AnalysisConfig.enforceMultiplexingStatic() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
     {
         beautifyFIFO(c);
     }

     else{
         beautifyARB(c);
     }
    }

    public void beautifyFIFO(Curve c) {
        // Remove unreal discontinuity.
        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if (c.isUnrealDiscontinuity(i)) {
                c.getSegment(i + 1).setLeftopen(c.getSegment(i).isLeftopen());
                c.removeSegment(i); // isUnrealDiscontinuity is (also) checking if its the last/prelast segment
                continue;
            }
            // i++;
        }

        // Join colinear segments.
        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if(i == c.getSegmentCount()-1)
            {
                // nothing to join since it's the last segment
                break; // it could be that a few segments have been removed (see below) and the right-part of the condition in the loop (c.getSegmentCound()-1) is probably only
                // computed at the start (I guess)
            }

            if(!(c.getSegment(i).getGrad().eq(c.getSegment(i + 1).getGrad()))) {
                continue;
            }

            Num secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c.getSegment(i + 1).getX(), c.getSegment(i).getX());
            secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(secondArg, c.getSegment(i).getGrad());
            secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).add(c.getSegment(i).getY(), secondArg);
            if(!(secondArg.eq(c.getSegment(i + 1).getY()))) {
                continue; // there is a "jump", so can't join the segments
            }

            // Reaching this line means that there is no "jump", delete segment i+1 -- note that they have the same slope
            c.removeSegment(i+1);
        }

        // segmentlist update for case: x-vals of segment i and i+1 same, different y-vals
        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if(i == c.getSegmentCount()-1)
            {
                break;
            }

            if (c.getSegment(i).getX().equals(c.getSegment(i + 1).getX())) {
                // the y-values differ here (otherwise segment i would have been removed by "remove unreal discont" (see above))

                if(!c.getSegment(i).isLeftopen())
                {
                    if(!c.getSegment(i+1).isLeftopen())
                    {
                        // both segments are not leftopen and both start at "x" -> can remove segment i
                        c.removeSegment(i);
                        continue;
                    }


                    else
                    {
                        // segment i not leftopen but i+1 is and different y-vals -> "jump", should keep both segments
                        // Found a spot and spot-segments have slope zero by definition
                        c.getSegment(i).setGrad(Num.getUtils(Calculator.getInstance().getNumBackend()).createZero());
                    }

                }

                else{
                    // segment i is leftopen -> just remove it
                    c.removeSegment(i);
                    continue;
                }
            }
        }

        c.setTB_MetaInfo(false);
        c.setTokenBucket(false);
        c.setTB_Components(new LinkedList<>());

        c.setRL_MetaInfo(false);
        c.setRateLateny(false);
        c.setRL_Components(new LinkedList<>());
    }

    public void beautifyARB(Curve c) {
        // Remove unreal discontinuity.
        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if (c.isUnrealDiscontinuity(i)) {
                c.getSegment(i + 1).setLeftopen(c.getSegment(i).isLeftopen());
                c.removeSegment(i);
                continue;
            }
            i++;
        }

        // Join colinear segments.
        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if(!(c.getSegment(i).getGrad().eq(c.getSegment(i + 1).getGrad()))) {
            	continue;
            }

            Num secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c.getSegment(i + 1).getX(), c.getSegment(i).getX());
            secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(secondArg, c.getSegment(i).getGrad());
            secondArg = Num.getUtils(Calculator.getInstance().getNumBackend()).add(c.getSegment(i).getY(), secondArg);
            if(!(secondArg.eq(c.getSegment(i + 1).getY()))) {
            	continue;
            }

            c.removeSegment(i + 1);
            if (i + 1 < c.getSegmentCount() && !c.getSegment(i + 1).isLeftopen()) {
                Num resultPt1 = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c.getSegment(i + 1).getY(), c.getSegment(i).getY());
                Num resultPt2 = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c.getSegment(i + 1).getX(), c.getSegment(i).getX());

                c.getSegment(i).setGrad(Num.getUtils(Calculator.getInstance().getNumBackend()).div(resultPt1, resultPt2));
            }
            continue;
        }

        for (int i = 0; i < c.getSegmentCount() - 1; i++) {
            if (c.getSegment(i).getX().equals(c.getSegment(i + 1).getX())) {
                c.getSegment(i).setGrad(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
            }
        }

        // Remove rate of tb arrival curves' first segment.
        if (c.getSegmentCount() > 1 && c.getSegment(0).getX() == Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()
                && c.getSegment(0).getY() != Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()
                && c.getSegment(1).getX() == Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()
                && c.getSegment(1).getY() != Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()) {
            c.getSegment(0).setGrad(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
        }

        c.setTB_MetaInfo(false);
        c.setTokenBucket(false);
        c.setTB_Components(new LinkedList<>());

        c.setRL_MetaInfo(false);
        c.setRateLateny(false);
        c.setRL_Components(new LinkedList<>());
    }

    /**
     * Returns an <code>ArrayList</code> instance of those x-coordinates at which
     * either c1 or c2 or both have an inflection point. There will be multiple
     * occurences of an x-coordinate, if at least one curve has a discontinuity at
     * that x-coordinate.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return an <code>ArrayList</code> of <code>Double</code> objects containing
     * the x-coordinates of the respective inflection point.
     */
    public ArrayList<Num> computeInflectionPointsX(Curve c1, Curve c2) {
        ArrayList<Num> xcoords = new ArrayList<Num>();

        int i1 = 0;
        int i2 = 0;
        while (i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount()) {
            Num x1 = (i1 < c1.getSegmentCount()) ? c1.getSegment(i1).getX()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            Num x2 = (i2 < c2.getSegmentCount()) ? c2.getSegment(i2).getX()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
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
     * Returns the maximum horizontal deviation of curve c2 from curve c1.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the horizontal deviation.
     */
    public Num getMaxHorizontalDeviation(Curve c1, Curve c2) {
        if (c1.getUltAffineRate().gt(c2.getUltAffineRate())) {
            return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        }

        Num result = Num.getFactory(Calculator.getInstance().getNumBackend()).createNegativeInfinity();
        for (int i = 0; i < c1.getSegmentCount(); i++) {
            Num ip_y = c1.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }
        for (int i = 0; i < c2.getSegmentCount(); i++) {
            Num ip_y = c2.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }
        return result;
    }

    /**
     * Returns the maximum vertical deviation of curve c2 from curve c1.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the vertical deviation.
     */
    public Num getMaxVerticalDeviation(Curve c1, Curve c2) {
        if (c1.getUltAffineRate().gt(c2.getUltAffineRate())) {
            return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        }
        // The computeInflectionPoints based method does not work for
        // single rate service curves (without latency)
        // in conjunction with token bucket arrival curves
        // because their common inflection point is in zero,
        // where the arrival curve is 0.0 by definition.
        // This leads to a vertical deviation of 0 the arrival curve's burst
        // (or infinity which is already handled by the first if-statement)

        // Solution:
        // Start with the burst as minimum of all possible solutions for the deviation
        // instead of negative infinity.

        Num burst_c1 = c1.fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero());
        Num burst_c2 = c2.fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero());
        Num result = Num.getUtils(Calculator.getInstance().getNumBackend()).diff(burst_c1, burst_c2);

        ArrayList<Num> xcoords = computeInflectionPointsX(c1, c2);
        for (int i = 0; i < xcoords.size(); i++) {
            Num ip_x = xcoords.get(i);

            Num backlog = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c1.f(ip_x), c2.f(ip_x));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, backlog);
        }
        return result;
    }

    /**
     * Iterate over all segments from both <code>curve1</code> and <code>curve2</code> to find the smallest x-coordinate where an intersection occurs.
     *
	 * @param curve1
	 * @param curve2
	 * 
	 * @return 	The smallest x-coordinate at which a curve <code>curve1</code> intersects a curve <code>curve2</code>,
	 * 			or PositiveInfinity if both curves do not have an intersection.
     */
    public Num getXIntersection(Curve curve1, Curve curve2) {
        Num x_int = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity(); // No need to create an object as this value is
        // only set for initial comparison in the loop.

        for (int i = 0; i < curve1.getSegmentCount(); i++) {
            boolean curve1_last = (i == curve1.getSegmentCount() - 1);

            for (int j = 0; j < curve2.getSegmentCount(); j++) {
                boolean curve2_last = (j == curve2.getSegmentCount() - 1);

                Num x_int_tmp = curve1.getSegment(i).getXIntersectionWith(curve2.getSegment(j));

                /**
                 * If any two segments s1 and s2 represent parallel lines, then the resulting x-coordinate of intersection does not exist.
                 * Thus s1.getXIntersectionWith(s2) returns NaN when this occurs.
                 */
                if (x_int_tmp.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getNaN())) {
                    break;
                }

                /**
                 * Notes:
                 * 	The lt() "less than" method from Num class used in conditions below assures to get the smallest x-coordinate.
                 * 	Absent of 'break' clause within conditions below assures all segments will be checked.
                 */
                if (x_int_tmp.gtZero()) {
                    if (!curve1_last) {
                        if (!curve2_last) {
                            if (x_int_tmp.lt(curve1.getSegment(i + 1).getX())
                                    && x_int_tmp.lt(curve2.getSegment(j + 1).getX()) && x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        } else {
                            if (x_int_tmp.lt(curve1.getSegment(i + 1).getX()) && x_int_tmp.lt(x_int)) {

                                x_int = x_int_tmp;
                            }
                        }
                    } else {
                        if (!curve2_last) {
                            if (x_int_tmp.lt(curve2.getSegment(j + 1).getX()) && x_int_tmp.lt(x_int)) {

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

    /**
     * Returns an <code>ArrayList</code> instance of those y-coordinates at which
     * either c1 or c2 or both have an inflection point.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return an <code>ArrayList</code> of <code>Double</code> objects containing
     * the x-coordinates of the respective inflection point.
     */
    public ArrayList<Num> computeInflectionPointsY(Curve c1, Curve c2) {
        ArrayList<Num> ycoords = new ArrayList<Num>();

        int i1 = 0;
        int i2 = 0;
        while (i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount()) {
            Num y1 = (i1 < c1.getSegmentCount()) ? c1.getSegment(i1).getY()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            Num y2 = (i2 < c2.getSegmentCount()) ? c2.getSegment(i2).getY()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
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
     * Returns a curve that is the difference between this curve and the given
     * curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise difference of the given curves, i.e., curve1 - curve2.
     */
    public Curve sub(Curve curve1, Curve curve2) {
        return computeResultingCurve(curve1, curve2, CurveOperation.SUB);
    }

    public ServiceCurve sub(ServiceCurve c1, ArrivalCurve c2) {
        return Curve.getFactory().createServiceCurve(computeResultingCurve(c1, c2, CurveOperation.SUB));
    }

    /**
     * Common helper for computing a new curve.
     *
     * @param curve1   Input curve 1.
     * @param curve2   Input curve 2.
     * @param operator Operation to be applied to the curves.
     * @return The resulting curve.
     */
    public Curve computeResultingCurve(Curve curve1, Curve curve2, CurveOperation operator) {
        Curve ZERO_DELAY_INFINITE_BURST = Curve_ConstantPool.INFINITE_SERVICE_CURVE.get();

        switch (operator) {
            case ADD:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            case SUB:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            case MIN:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return curve2.copy();
                }
                if (curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return curve1.copy();
                }
                break;
            case MAX:
                if (curve1.equals(ZERO_DELAY_INFINITE_BURST) || curve2.equals(ZERO_DELAY_INFINITE_BURST)) {
                    return ZERO_DELAY_INFINITE_BURST;
                }
                break;
            default:
        }

        ArrayList<LinearSegment> result = new ArrayList<LinearSegment>();
        Num x = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num x_cross;
        boolean leftopen;

        int i1 = 0;
        int i2 = 0;
        while (i1 < curve1.getSegmentCount() || i2 < curve2.getSegmentCount()) {
            Num x_next1 = (i1 + 1 < curve1.getSegmentCount()) ? curve1.getSegment(i1 + 1).getX()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            Num x_next2 = (i2 + 1 < curve2.getSegmentCount()) ? curve2.getSegment(i2 + 1).getX()
                    : Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            Num x_next = Num.getUtils(Calculator.getInstance().getNumBackend()).min(x_next1, x_next2);

            leftopen = curve1.getSegment(i1).isLeftopen() || curve2.getSegment(i2).isLeftopen();

            switch (operator) {
                case ADD:
                    result.add(LinearSegment.add(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen));
                    break;
                case SUB:
                    result.add(LinearSegment.sub(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen));
                    break;
                case MIN:
                    x_cross = curve1.getSegment(i1).getXIntersectionWith(curve2.getSegment(i2));
                    if (x_cross.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getNaN())) {
                        x_cross = Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
                    }
                    if (x.lt(x_cross) && x_cross.lt(x_next)) {
                        result.add(LinearSegment.min(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                        result.add(LinearSegment.min(curve1.getSegment(i1), curve2.getSegment(i2), x_cross, false, true));
                    } else {
                        result.add(LinearSegment.min(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                    }
                    break;
                case MAX:
                    x_cross = curve1.getSegment(i1).getXIntersectionWith(curve2.getSegment(i2));
                    if (x_cross.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getNaN())) {
                        x_cross = Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
                    }
                    if (x.lt(x_cross) && x_cross.lt(x_next)) {
                        result.add(LinearSegment.max(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                        result.add(LinearSegment.max(curve1.getSegment(i1), curve2.getSegment(i2), x_cross, false, true));
                    } else {
                        result.add(LinearSegment.max(curve1.getSegment(i1), curve2.getSegment(i2), x, leftopen, false));
                    }
                    break;
                default:
                    break;
            }

            if (x_next1.equals(x_next)) {
                i1++;
            }
            if (x_next2.equals(x_next)) {
                i2++;
            }
            x = x_next;
        }

        return Curve.getFactory().createCurve(result);
    }

    /**
     * Returns a curve that is the minimum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise minimum of the given curves.
     */
    public Curve min(Curve curve1, Curve curve2) {
        return computeResultingCurve(curve1, curve2, CurveOperation.MIN);
    }

    public MaxServiceCurve min(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        return Curve.getFactory()
                .createMaxServiceCurve(computeResultingCurve(max_service_curve_1, max_service_curve_2, CurveOperation.MIN));
    }

    /**
     * Returns a copy of curve bounded at the x-axis.
     *
     * @param curve the curve to bound.
     * @return the bounded curve.
     */
    public Curve boundAtXAxis(Curve curve) {
        Curve curve_copy = curve.copy();

        ArrayList<LinearSegment> result = new ArrayList<LinearSegment>();
        LinearSegment s;
        LinearSegment x_axis = Curve.getXAxis();
        for (int i = 0; i < curve_copy.getSegmentCount(); i++) {
            if (curve_copy.getSegment(i).getY().gtZero()) {
                result.add(curve_copy.getSegment(i));

                if (curve_copy.getSegment(i).getGrad().ltZero()) {
                    Num x_cross = curve_copy.getSegment(i).getXIntersectionWith(x_axis);
                    if (i + 1 >= curve_copy.getSegmentCount() || x_cross.lt(curve_copy.getSegment(i + 1).getX())) {
                        s = LinearSegment.createHorizontalLine(0.0);
                        s.setX(x_cross);
                        result.add(s);
                    }
                }
            } else {
                s = LinearSegment.createHorizontalLine(0.0);
                s.setX(curve_copy.getSegment(i).getX());
                s.setLeftopen(curve_copy.getSegment(i).isLeftopen());
                result.add(s);

                if (curve_copy.getSegment(i).getGrad().gtZero()) {
                    Num x_cross = curve_copy.getSegment(i).getXIntersectionWith(x_axis);
                    if (i + 1 >= curve_copy.getSegmentCount() || x_cross.lt(curve_copy.getSegment(i + 1).getX())) {
                        s = LinearSegment.createHorizontalLine(0.0);
                        s.setX(x_cross);
                        s.setGrad(curve_copy.getSegment(i).getGrad());
                        result.add(s);
                    }
                }
            }
        }

        // FIXME Can have colinear segments but the above beautify(...) method only works on curve instances.
        // Makes creation of affine curve fail if we restrict to at most 2 segmetns when calling createCurve(...).
        // Proposed solution: Overwrite this method with a affine curve specific one in the CurveAffine interface.
        return Curve.getFactory().createCurve(result);
    }

    public ServiceCurve min(ServiceCurve c1, ServiceCurve c2) {
        return Curve.getFactory().createServiceCurve(computeResultingCurve(c1, c2, CurveOperation.MIN));
    }

    public ArrivalCurve min(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return Curve.getFactory()
                .createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, CurveOperation.MIN));
    }

    /**
     * Returns a curve that is the sum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise sum of the given curves.
     */
    public Curve add(Curve curve1, Curve curve2) {
        return computeResultingCurve(curve1, curve2, CurveOperation.ADD);
    }

    /**
     * Returns a curve that is the maximum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise maximum of the given curves.
     */
    public Curve max(Curve curve1, Curve curve2) {
        return computeResultingCurve(curve1, curve2, CurveOperation.MAX);
    }

    public MaxServiceCurve add(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        return Curve.getFactory()
                .createMaxServiceCurve(computeResultingCurve(max_service_curve_1, max_service_curve_2, CurveOperation.ADD));
    }

    public MaxServiceCurve add(MaxServiceCurve max_service_curve_1, Num dy) {
        return Curve.getFactory().createMaxServiceCurve(add((Curve) max_service_curve_1, dy));
    }

    public ArrivalCurve add(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return Curve.getFactory()
                .createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, CurveOperation.ADD));
    }

    public ArrivalCurve add(ArrivalCurve arrival_curve_1, Num dy) {
        return Curve.getFactory().createArrivalCurve(add((Curve) arrival_curve_1, dy));
    }

    public ServiceCurve add(ServiceCurve c1, ServiceCurve c2) {
        return Curve.getFactory().createServiceCurve(computeResultingCurve(c1, c2, CurveOperation.ADD));
    }

    public ServiceCurve max(ServiceCurve c1, ServiceCurve c2) {
        return Curve.getFactory().createServiceCurve(computeResultingCurve(c1, c2, CurveOperation.MAX));
    }

    public ArrivalCurve max(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        return Curve.getFactory()
                .createArrivalCurve(computeResultingCurve(arrival_curve_1, arrival_curve_2, CurveOperation.MAX));
    }

    /**
     * Returns a copy of this curve shifted vertically by <code>dy</code>.
     *
     * @param curve The curve to shift.
     * @param dy    The offset to shift the curve.
     * @return The shifted curve.
     */
    public Curve add(Curve curve, Num dy) {
        Curve result = curve.copy();
        for (int i = 0; i < curve.getSegmentCount(); i++) {
            result.getSegment(i).setY(Num.getUtils(Calculator.getInstance().getNumBackend()).add(result.getSegment(i).getY(), dy));
        }
        return result;
    }

    /**
     * Returns a copy of this curve that is shifted to the left by <code>dx</code>,
     * i.e. g(x) = f(x+dx). Note that the new curve is clipped at the y-axis so that
     * in most cases <code>c.shiftLeftClipping(dx).shiftRight(dx) != c</code>!
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    public Curve shiftLeftClipping(Curve curve, Num dx) {
        int i = curve.getSegmentDefining(dx);
        Curve result = curve.copy();
        LinearSegment segment_i = result.getSegment(i);
        if (segment_i.getX().lt(dx)) {
            segment_i.setY(Num.getUtils(Calculator.getInstance().getNumBackend()).add(segment_i.getY(), Num.getUtils(Calculator.getInstance().getNumBackend())
                    .mult(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(dx, segment_i.getX()), segment_i.getGrad())));
            segment_i.setX(dx);
            segment_i.setLeftopen(false);
        }
        for (int j = 1; j <= i; j++) {
            result.removeSegment(0);
        }
        for (i = 0; i < result.getSegmentCount(); i++) {
            result.getSegment(i).setX(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(result.getSegment(i).getX(), dx));
        }

        return result;
    }
}
