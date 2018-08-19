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

package de.uni_kl.cs.discodnc.curves;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.List;

/**
 * Interface for piecewise affine curves, including convenience functions used
 * by Disco's implementation DNC operations. I.e., in addition to its defining
 * linear segments, curves may be ascribed as (compositions of) rate latency and
 * token bucket functions.
 */
public interface Curve_PwAffine extends Curve {

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
    boolean isRateLatency();

    List<Curve_PwAffine> getRL_Components();

    // (Composition of) Token buckets
    boolean isTokenBucket();

    List<Curve_PwAffine> getTB_Components();
}
