/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

package de.uni_kl.cs.discodnc.curves.dnc_affine;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;

public class AffineServiceCurve_DNC extends AffineCurve_DNC implements ServiceCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public AffineServiceCurve_DNC() {
        super();
    }

    public AffineServiceCurve_DNC(int segment_count) {
        super(segment_count);
    }

    public AffineServiceCurve_DNC(Curve curve) {
        copy(curve);

        if (Calculator.getInstance().exec_service_curve_checks() && !isWideSenseIncreasing()) { // too strong
            // requirement:
            // !isConvex()
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }

    public AffineServiceCurve_DNC(String service_curve_str) throws Exception {
        if (service_curve_str == null || service_curve_str.isEmpty() || service_curve_str.length() < 9) { // Smallest
            // possible
            // string:
            // {(0,0),0}
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(service_curve_str);

        if (Calculator.getInstance().exec_service_curve_checks() && !isWideSenseIncreasing()) { // too strong
            // requirement:
            // !isConvex()
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public AffineServiceCurve_DNC copy() {
        AffineServiceCurve_DNC sc_copy = new AffineServiceCurve_DNC();
        sc_copy.copy(this);
        return sc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AffineServiceCurve_DNC) && super.equals(obj);
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
