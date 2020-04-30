/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.bounds.disco.pw_affine;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.num.Num;

public final class Delay_Disco_PwAffine {
    private static Num deriveForSpecialCurves(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        if (arrival_curve.equals(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get())) {
            return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        }
        if (service_curve.isDelayedInfiniteBurst()) {
            // Assumption: the arrival curve does not have an initial latency.
            // Otherwise its sub-additive closure would be zero, i.e., the arrival curve
            // would not be sensible.
            return service_curve.getLatency().copy();
        }
        if (service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get()) // We know from above that the
                // arrivals are not zero.
                || arrival_curve.getUltAffineRate().gt(service_curve.getUltAffineRate())) {
            return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        }
        return null;
    }

    public static Num deriveARB(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        Num result = deriveForSpecialCurves(arrival_curve, service_curve);
        if (result != null) {
            return result;
        }

        return Curve.getUtils().getXIntersection(arrival_curve, service_curve);
    }

    // Single flow to be bound, i.e., fifo per micro flow holds
    public static Num deriveFIFO(ArrivalCurve arrival_curve, ServiceCurve service_curve) {

        Num result = deriveForSpecialCurves(arrival_curve, service_curve);
        if (result != null) {
            return result;
        }

        result = Num.getFactory(Calculator.getInstance().getNumBackend()).createNegativeInfinity();
        for (int i = 0; i < arrival_curve.getSegmentCount(); i++) {
            Num ip_y = arrival_curve.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve.f_inv(ip_y, true), arrival_curve.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }
        for (int i = 0; i < service_curve.getSegmentCount(); i++) {
            Num ip_y = service_curve.getSegment(i).getY();

            Num delay = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(service_curve.f_inv(ip_y, true), arrival_curve.f_inv(ip_y, false));
            result = Num.getUtils(Calculator.getInstance().getNumBackend()).max(result, delay);
        }

        return Num.getUtils(Calculator.getInstance().getNumBackend()).max(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero(), result);
    }
}
