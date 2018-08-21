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

package de.uni_kl.cs.discodnc.curves.disco.pwaffine;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;

public class ArrivalCurve_DNC_PwAffine extends Curve_DNC_PwAffine implements ArrivalCurve {
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    public ArrivalCurve_DNC_PwAffine() {
        super();
    }

    public ArrivalCurve_DNC_PwAffine(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurve_DNC_PwAffine(Curve curve) {
        super(curve);
        forceThroughOrigin();
        if (Calculator.getInstance().exec_arrival_curve_checks() && !isWideSenseIncreasing()) {
        // Too strong requirement: !isConcave()
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }

    public ArrivalCurve_DNC_PwAffine(String arrival_curve_str) throws Exception {
        if (arrival_curve_str == null || arrival_curve_str.isEmpty() || arrival_curve_str.length() < 9) {
        	// Smallest possible string: {(0,0),0}
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(arrival_curve_str);
        forceThroughOrigin();

        // Too strong requirement: !isConcave()
        if (Calculator.getInstance().exec_arrival_curve_checks() && !isWideSenseIncreasing()) { 
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurve_DNC_PwAffine copy() {
        ArrivalCurve_DNC_PwAffine ac_copy = new ArrivalCurve_DNC_PwAffine();
        ac_copy.copy(this);
        return ac_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurve_DNC_PwAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "AC".hashCode() * super.hashCode();
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
