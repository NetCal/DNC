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

package org.networkcalculus.dnc.curves;

import java.util.ArrayList;

import org.networkcalculus.num.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public interface CurveUtils {

    enum CurveOperation {
        ADD, SUB, MIN, MAX
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
    Curve removeLatency(Curve curve);

    /**
     * Returns a copy of this curve that is shifted to the right by <code>dx</code>,
     * i.e. g(x) = f(x-dx).
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    Curve shiftRight(Curve curve, Num dx);

    void beautify(Curve c);

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
    ArrayList<Num> computeInflectionPointsX(Curve c1, Curve c2);

    /**
     * Returns the maximum horizontal deviation between the given two curves.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the horizontal deviation.
     */
    public Num getMaxHorizontalDeviation(Curve c1, Curve c2);

    /**
     * Returns the maximum vertical deviation between the given two curves.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the vertical deviation.
     */
    Num getMaxVerticalDeviation(Curve c1, Curve c2);

    Num getXIntersection(Curve curve1, Curve curve2);

    /**
     * Returns an <code>ArrayList</code> instance of those y-coordinates at which
     * either c1 or c2 or both have an inflection point.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return an <code>ArrayList</code> of <code>Double</code> objects containing
     * the x-coordinates of the respective inflection point.
     */
    ArrayList<Num> computeInflectionPointsY(Curve c1, Curve c2);

    /**
     * Returns a curve that is the difference between this curve and the given
     * curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise difference of the given curves, i.e., curve1 - curve2.
     */
    Curve sub(Curve curve1, Curve curve2);

    ServiceCurve sub(ServiceCurve c1, ArrivalCurve c2);

    /**
     * Common helper for computing a new curve.
     *
     * @param curve1   Input curve 1.
     * @param curve2   Input curve 2.
     * @param operator Operation to be applied to the curves.
     * @return The resulting curve.
     */
    Curve computeResultingCurve(Curve curve1, Curve curve2, CurveOperation operator);

    /**
     * Returns a curve that is the minimum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise minimum of the given curves.
     */
    Curve min(Curve curve1, Curve curve2);

    MaxServiceCurve min(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2);

    /**
     * Returns a copy of curve bounded at the x-axis.
     *
     * @param curve the curve to bound.
     * @return the bounded curve.
     */
    Curve boundAtXAxis(Curve curve);

    ServiceCurve min(ServiceCurve c1, ServiceCurve c2);

    ArrivalCurve min(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2);

    /**
     * Returns a curve that is the sum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise sum of the given curves.
     */
    Curve add(Curve curve1, Curve curve2);

    /**
     * Returns a curve that is the maximum of this curve and the given curve.
     *
     * @param curve1 input curve 1.
     * @param curve2 input curve 2.
     * @return the pointwise maximum of the given curves.
     */
    Curve max(Curve curve1, Curve curve2);

    MaxServiceCurve add(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2);

    MaxServiceCurve add(MaxServiceCurve max_service_curve_1, Num dy);

    ArrivalCurve add(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2);

    ArrivalCurve add(ArrivalCurve arrival_curve_1, Num dy);

    ServiceCurve add(ServiceCurve c1, ServiceCurve c2);

    ServiceCurve max(ServiceCurve c1, ServiceCurve c2);

    ArrivalCurve max(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2);

    /**
     * Returns a copy of this curve shifted vertically by <code>dy</code>.
     *
     * @param curve The curve to shift.
     * @param dy    The offset to shift the curve.
     * @return The shifted curve.
     */
    Curve add(Curve curve, Num dy);

    /**
     * Returns a copy of this curve that is shifted to the left by <code>dx</code>,
     * i.e. g(x) = f(x+dx). Note that the new curve is clipped at the y-axis so that
     * in most cases <code>c.shiftLeftClipping(dx).shiftRight(dx) != c</code>!
     *
     * @param curve The curve to shift.
     * @param dx    The offset to shift the curve.
     * @return The shifted curve.
     */
    Curve shiftLeftClipping(Curve curve, Num dx);
}
