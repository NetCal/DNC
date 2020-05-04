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

package org.networkcalculus.dnc.algebra.disco.affine;

import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.utils.CheckUtils;
import org.networkcalculus.num.Num;

public abstract class Deconvolution_Disco_Affine {

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
        switch (CheckUtils.inputNullCheck(arrival_curves, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        if (arrival_curves.isEmpty()) {
            results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
            return results;
        }

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(deconvolve(alpha, service_curve));
        }

        return results;
    }

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

        switch (CheckUtils.inputNullCheck(arrival_curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(arrival_curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }

        for (ServiceCurve beta : service_curves) {
            for (ArrivalCurve alpha : arrival_curves) {
                results.add(deconvolve(alpha, beta));
            }
        }

        return results;
    }

    public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        switch (CheckUtils.inputNullCheck(arrival_curve, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
            case 2:
                return Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
            default:
        }

        if (service_curve.equals(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get())
                || (service_curve.isDelayedInfiniteBurst() && service_curve.getLatency().doubleValue() == 0.0)
                || (arrival_curve.equals(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()))) {
            return arrival_curve.copy();
        }
        if (service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())
                || service_curve.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (service_curve.getUltAffineRate().eqZero()
                && service_curve.getSegment(service_curve.getSegmentCount() - 1).getY().eqZero())) {
            return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        }
        return deconvolveTB_RL(arrival_curve, service_curve);
    }

    public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves,
                                                                Set<ServiceCurve> service_curves) {
    	Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

        switch (CheckUtils.inputNullCheck(curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }

        Num latency;
        for (ServiceCurve sc : service_curves) {
            for (Curve pwa_c : curves) {
                latency = pwa_c.getLatency();
                results.add(Curve.getFactory().createArrivalCurve(
                		Curve.getUtils().shiftRight(deconvolveTB_RL(
                					Curve.getFactory().createArrivalCurve(Curve.getUtils().shiftLeftClipping(pwa_c, latency)), sc),
                        latency)));
            }
        }

        return results;
    }

    private static ArrivalCurve deconvolveTB_RL(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        switch (CheckUtils.inputNullCheck(arrival_curve, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
            case 2:
                return Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
            default:
        }

        if (service_curve.equals(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get())) {
            return arrival_curve.copy();
        }
        if (service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())
                || service_curve.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (service_curve.getUltAffineRate().eqZero() && service_curve.getSegment(1).getY().eqZero())) {
            return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        }

        // Result: Token bucket gamma_{r,'b'} with r' = r and b' = b+r*T
        return Curve.getFactory().createTokenBucket(arrival_curve.getUltAffineRate().doubleValue(),
                arrival_curve.getBurst().doubleValue()
                        + arrival_curve.getUltAffineRate().doubleValue() * service_curve.getLatency().doubleValue());
    }
}
