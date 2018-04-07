/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2017+ The DiscoDNC contributors
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

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;

public class ArrivalCurve_MPARTC_PwAffine extends Curve_MPARTC_PwAffine implements ArrivalCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public ArrivalCurve_MPARTC_PwAffine() {
        super();
    }

    public ArrivalCurve_MPARTC_PwAffine(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurve_MPARTC_PwAffine(de.uni_kl.cs.discodnc.curves.Curve curve) {
        super(curve);
    }

    public ArrivalCurve_MPARTC_PwAffine(String arrival_curve_str) throws Exception {
        super.initializeCurve(arrival_curve_str);
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ArrivalCurve_MPARTC_PwAffine(Curve c) {
        rtc_curve = c.clone();
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy,
                                        java.lang.String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                        long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                        long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ArrivalCurve_MPARTC_PwAffine(SegmentList aperSegments, java.lang.String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    // ------------------------------------------------------------------------------------------------------------
    // Interface implementations
    // ------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurve_MPARTC_PwAffine copy() {
        ArrivalCurve_MPARTC_PwAffine ac_copy = new ArrivalCurve_MPARTC_PwAffine();
        ac_copy.copy(this);
        return ac_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurve_MPARTC_PwAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "AC".hashCode() * super.hashCode();
    }

    @Override
    public String toString() {
        return "AC" + super.toString();
    }
}
