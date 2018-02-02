/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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
import de.uni_kl.cs.discodnc.curves.ServiceCurve;

public class ServiceCurve_MPARTC_PwAffine extends Curve_MPARTC_PwAffine implements ServiceCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public ServiceCurve_MPARTC_PwAffine() {
        super();
    }

    public ServiceCurve_MPARTC_PwAffine(int segment_count) {
        super(segment_count);
    }

    public ServiceCurve_MPARTC_PwAffine(CurvePwAffine curve) {
        copy(curve);
    }

    public ServiceCurve_MPARTC_PwAffine(String service_curve_str) throws Exception {
        super.initializeCurve(service_curve_str);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                        long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0,
                                        long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ServiceCurve_MPARTC_PwAffine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public ServiceCurve_MPARTC_PwAffine(Curve c) {
        rtc_curve = c.clone();
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public ServiceCurve_MPARTC_PwAffine copy() {
        ServiceCurve_MPARTC_PwAffine sc_copy = new ServiceCurve_MPARTC_PwAffine();
        sc_copy.copy(this);
        return sc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceCurve_MPARTC_PwAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "SC".hashCode() * super.hashCode();
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        return "SC" + super.toString();
    }
}
