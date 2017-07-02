/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2013 - 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.disco.curves.dnc;

import de.uni_kl.disco.curves.CurvePwAffine;
import de.uni_kl.disco.curves.ServiceCurve;
import de.uni_kl.disco.nc.CalculatorConfig;

/**
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 */
public class ServiceCurve_DNC extends Curve_DNC implements ServiceCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ServiceCurve_DNC() {
        super();
    }

    public ServiceCurve_DNC(int segment_count) {
        super(segment_count);
    }

    public ServiceCurve_DNC(CurvePwAffine curve) {
        copy(curve);

        if (CalculatorConfig.SERVICE_CURVE_CHECKS && !isWideSenseIncreasing()) { // too strong requirement: !isConvex()
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }

    public ServiceCurve_DNC(String service_curve_str) throws Exception {
        if (service_curve_str == null || service_curve_str.isEmpty() || service_curve_str.length() < 9) { // Smallest possible string: {(0,0),0}
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(service_curve_str);

        if (CalculatorConfig.SERVICE_CURVE_CHECKS && !isWideSenseIncreasing()) { // too strong requirement: !isConvex()
            throw new RuntimeException("Service curves can only be created from wide-sense increasing functions.");
        }
    }


    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public ServiceCurve_DNC copy() {
        ServiceCurve_DNC sc_copy = new ServiceCurve_DNC();
        sc_copy.copy(this);
        return sc_copy;
    }

    // TODO @Steffen
    // Same as ArrivalCurve
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceCurve_DNC) && super.equals(obj);
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
