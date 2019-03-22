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

import java.util.List;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.num.Num;

/**
 * Interface for piecewise affine curves, including convenience functions used
 * by Disco's implementation of DNC operations. I.e., in addition to its defining
 * linear segments, curves may be ascribed as (compositions of) rate latency and
 * token bucket functions.
 */
public interface Curve_PwAffine extends Curve_Affine {

    // // Specific piecewise affine curve shapes

    /**
     * Returns the maximum horizontal deviation between the given two curves.
     *
     * @param c1 the first curve.
     * @param c2 the second curve.
     * @return the value of the horizontal deviation.
     */
    static Num getMaxHorizontalDeviation(Curve_PwAffine c1, Curve_PwAffine c2) {
        if (c1.getUltAffineRate().gt(c2.getUltAffineRate())) {
            return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        }

        Num result = Num.getFactory(Calculator.getInstance().getNumBackend()).createNegativeInfinity();
        for (int i = 0; i < c1.getSegmentCount(); i++) {
            Num ip_y = c1.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }
        for (int i = 0; i < c2.getSegmentCount(); i++) {
            Num ip_y = c2.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c2.f_inv(ip_y, true), c1.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }
        return result;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface
    // --------------------------------------------------------------------------------------------------------------
    @Override
    Curve_PwAffine copy();

    @Override
    void copy(Curve curve);

    // (Composition of) Rate latencies
    List<Curve_Affine> getRL_Components();

    // (Composition of) Token buckets
    List<Curve_Affine> getTB_Components();
}
