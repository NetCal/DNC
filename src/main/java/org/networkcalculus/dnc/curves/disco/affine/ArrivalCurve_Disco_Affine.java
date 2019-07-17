/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.curves.disco.affine;

import java.util.Objects;

import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.disco.Curves_Disco_Configuration;

public class ArrivalCurve_Disco_Affine extends Curve_Disco_Affine implements ArrivalCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public ArrivalCurve_Disco_Affine() {
        super();
    }

    public ArrivalCurve_Disco_Affine(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurve_Disco_Affine(Curve curve) {
        super(curve);
        forceThroughOrigin();

        // Too strong requirement: !isConcave()
        if (Curves_Disco_Configuration.getInstance().exec_arrival_curve_checks() && !isWideSenseIncreasing()) {
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }

    public ArrivalCurve_Disco_Affine(String arrival_curve_str) throws Exception {
    	// Smallest possible string: {(0,0),0}
        if (arrival_curve_str == null || arrival_curve_str.isEmpty() || arrival_curve_str.length() < 9) {
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(arrival_curve_str);
        forceThroughOrigin();

        // Too strong requirement: !isConcave()
        if (Curves_Disco_Configuration.getInstance().exec_arrival_curve_checks() && !isWideSenseIncreasing()) {
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurve_Disco_Affine copy() {
        ArrivalCurve_Disco_Affine ac_copy = new ArrivalCurve_Disco_Affine();
        ac_copy.copy(this);
        return ac_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurve_Disco_Affine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash("AC", super.hashCode());
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        return "AC" + super.toString();
    }
}
