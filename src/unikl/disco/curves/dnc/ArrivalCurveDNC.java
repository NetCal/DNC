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

package unikl.disco.curves.dnc;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.CurveUltAffine;
import unikl.disco.nc.CalculatorConfig;

/**
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 */
public class ArrivalCurveDNC extends CurveDNC implements ArrivalCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ArrivalCurveDNC() {
        super();
    }

    public ArrivalCurveDNC(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurveDNC(CurveUltAffine curve) {
        super(curve);
        forceThroughOrigin();

        if (CalculatorConfig.ARRIVAL_CURVE_CHECKS && !isWideSenseIncreasing()) { // too strong requirement: !isConcave()
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }

    public ArrivalCurveDNC(String arrival_curve_str) throws Exception {
        if (arrival_curve_str == null || arrival_curve_str.isEmpty() || arrival_curve_str.length() < 9) { // Smallest possible string: {(0,0),0}
            throw new RuntimeException("Invalid string representation of a service curve.");
        }

        initializeCurve(arrival_curve_str);
        forceThroughOrigin();

        if (CalculatorConfig.ARRIVAL_CURVE_CHECKS && !isWideSenseIncreasing()) { // too strong requirement: !isConcave()
            System.out.println(toString());
            throw new RuntimeException("Arrival curves can only be created from wide-sense increasing functions.");
        }
    }


    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurveDNC copy() {
        ArrivalCurveDNC ac_copy = new ArrivalCurveDNC();
        ac_copy.copy(this);
        return ac_copy;
    }

    // Does this super.equals part work? It checks for instanceof CurveDNC!
    // TODO: @Steffen:
    // Yes it works, because an ArrivalCurve is also a Curve
    // But Curve.equals(ArrivalCurve) is also true. If this is ok, remove these comments
    // IMO should be no problem, since we do not work with raw Curves
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurveDNC) && super.equals(obj);
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
