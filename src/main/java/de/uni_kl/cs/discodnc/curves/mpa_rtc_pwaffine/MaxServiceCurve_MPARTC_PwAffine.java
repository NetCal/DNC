/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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

package de.uni_kl.cs.discodnc.curves.mpa_rtc_pwaffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;

import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;

public class MaxServiceCurve_MPARTC_PwAffine extends Curve_MPARTC_PwAffine implements MaxServiceCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    protected MaxServiceCurve_MPARTC_PwAffine() {
        super();
    }

    public MaxServiceCurve_MPARTC_PwAffine(int segment_count) {
        super(segment_count);
    }

    public MaxServiceCurve_MPARTC_PwAffine(CurvePwAffine curve) {
        copy(curve);
    }

    public MaxServiceCurve_MPARTC_PwAffine(String max_service_curve_str) throws Exception {
        super.initializeCurve(max_service_curve_str);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                           long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                           long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public MaxServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public MaxServiceCurve_MPARTC_PwAffine(Curve c) {
        rtc_curve = c.clone();
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public MaxServiceCurve_MPARTC_PwAffine copy() {
        MaxServiceCurve_MPARTC_PwAffine msc_copy = new MaxServiceCurve_MPARTC_PwAffine();
        msc_copy.copy(this);

        return msc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MaxServiceCurve_MPARTC_PwAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "MSC".hashCode() * super.hashCode();
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        return "MSC" + super.toString();
    }
}
