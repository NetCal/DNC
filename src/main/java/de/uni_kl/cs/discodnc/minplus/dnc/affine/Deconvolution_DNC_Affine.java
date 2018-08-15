/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
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

package de.uni_kl.cs.discodnc.minplus.dnc.affine;

import java.util.HashSet;
import java.util.Set;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.misc.CheckUtils;
import de.uni_kl.cs.discodnc.numbers.Num;

public abstract class Deconvolution_DNC_Affine {

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
        switch (CheckUtils.inputNullCheck(arrival_curves, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve.getFactory().createZeroArrivals());
                return results;
            case 2:
                results.add((ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst());
                return results;
            default:
        }
        if (arrival_curves.isEmpty()) {
            results.add(Curve.getFactory().createZeroArrivals());
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
                results.add(Curve.getFactory().createZeroArrivals());
                return results;
            case 2:
                results.add((ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(arrival_curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve.getFactory().createZeroArrivals());
                return results;
            case 2:
                results.add((ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst());
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
                return Curve.getFactory().createZeroArrivals();
            case 2:
                return (ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst();
            default:
        }

        if (service_curve.equals(Curve.getFactory().createZeroDelayInfiniteBurst())
                || (service_curve.isDelayedInfiniteBurst() && service_curve.getLatency().doubleValue() == 0.0)
                || (arrival_curve.equals(Curve.getFactory().createZeroArrivals()))) {
            return arrival_curve.copy();
        }
        if (service_curve.equals(Curve.getFactory().createZeroService())
                || service_curve.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (service_curve.getUltAffineRate().eqZero()
                && service_curve.getSegment(service_curve.getSegmentCount() - 1).getY().eqZero())) {
            return Curve.getFactory().createZeroArrivals();
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
                results.add(Curve.getFactory().createZeroArrivals());
                return results;
            case 2:
                results.add((ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve.getFactory().createZeroArrivals());
                return results;
            case 2:
                results.add((ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst());
                return results;
            default:
        }

        Num latency;
        for (ServiceCurve sc : service_curves) {
            for (Curve pwa_c : curves) {
                latency = pwa_c.getLatency();
                results.add(Curve.getFactory().createArrivalCurve(
                			Curve.shiftRight(deconvolveTB_RL(
                					Curve.getFactory().createArrivalCurve(Curve.shiftLeftClipping(pwa_c, latency)), sc),
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
                return Curve.getFactory().createZeroArrivals();
            case 2:
                return (ArrivalCurve) Curve.getFactory().createZeroDelayInfiniteBurst();
            default:
        }

        if (service_curve.equals(Curve.getFactory().createZeroDelayInfiniteBurst())) {
            return arrival_curve.copy();
        }
        if (service_curve.equals(Curve.getFactory().createZeroService())
                || service_curve.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (service_curve.getUltAffineRate().eqZero() && service_curve.getSegment(1).getY().eqZero())) {
            return Curve.getFactory().createZeroArrivals();
        }

        // Result: Token bucket gamma_{r,'b'} with r' = r and b' = b+r*T
        return Curve.getFactory().createTokenBucket(arrival_curve.getUltAffineRate().doubleValue(),
                arrival_curve.getBurst().doubleValue()
                        + arrival_curve.getUltAffineRate().doubleValue() * service_curve.getLatency().doubleValue());
    }
}
