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

package de.uni_kl.cs.discodnc.curves.dnc.pwaffine;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;

public class ServiceCurve_DNC_PwAffine extends Curve_DNC_PwAffine implements ServiceCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public ServiceCurve_DNC_PwAffine() {
        super();
    }

    public ServiceCurve_DNC_PwAffine(int segment_count) {
        super(segment_count);
    }

    public ServiceCurve_DNC_PwAffine(Curve curve) {
        copy(curve);

        // Too strong requirement: !isConvex()
        if (Calculator.getInstance().exec_service_curve_checks() && !isWideSenseIncreasing()) {
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }

    public ServiceCurve_DNC_PwAffine(String service_curve_str) throws Exception {
        if (service_curve_str == null || service_curve_str.isEmpty() || service_curve_str.length() < 9) {
        	// Smallest possible string: {(0,0),0}
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(service_curve_str);

        // Too strong requirement: !isConvex()
        if (Calculator.getInstance().exec_service_curve_checks() && !isWideSenseIncreasing()) {
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public ServiceCurve_DNC_PwAffine copy() {
        ServiceCurve_DNC_PwAffine sc_copy = new ServiceCurve_DNC_PwAffine();
        sc_copy.copy(this);
        return sc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceCurve_DNC_PwAffine) && super.equals(obj);
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
