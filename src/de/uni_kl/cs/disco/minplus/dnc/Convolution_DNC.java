/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

package de.uni_kl.cs.disco.minplus.dnc;

import java.util.HashSet;
import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactoryDispatch;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtilsDispatch;
import de.uni_kl.cs.disco.curves.LinearSegment;
import de.uni_kl.cs.disco.curves.LinearSegmentFactoryDispatch;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.minplus.MinPlusInputChecks;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;
import de.uni_kl.cs.disco.numbers.NumUtilsDispatch;

public abstract class Convolution_DNC {

    //------------------------------------------------------------
    // Service Curves
    //------------------------------------------------------------
    public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2) {
        // null checks will be done by convolve( ... )
        return convolve(service_curve_1, service_curve_2, false);
    }

    public static ServiceCurve convolve(ServiceCurve service_curve_1, ServiceCurve service_curve_2, boolean tb_rl_optimized) {
        // null checks will be done by convolve_SC_SC_RLs( ... ) or convolve_SC_SC_Generic( ... )
        if (tb_rl_optimized) {
            return convolve_SC_SC_RLs(service_curve_1, service_curve_2);
        } else {
            return convolve_SC_SC_Generic(service_curve_1, service_curve_2);
        }
    }

    private static ServiceCurve convolve_SC_SC_RLs(ServiceCurve service_curve_1, ServiceCurve service_curve_2) {
        switch (MinPlusInputChecks.inputNullCheck(service_curve_1, service_curve_2)) {
            case 1:
                return service_curve_2.copy();
            case 2:
                return service_curve_1.copy();
            case 3:
                return CurvePwAffineFactoryDispatch.createZeroService();
            case 0:
            default:
                break;
        }

        Num rate;
        switch (MinPlusInputChecks.inputDelayedInfiniteBurstCheck(service_curve_1, service_curve_2)) {
	        case 1:
	        		rate = service_curve_2.getUltAffineRate();
	            break;
	        case 2:
        			rate = service_curve_1.getUltAffineRate();
	            break;
	        case 3:
	            rate = NumFactory.getNumFactory().createPositiveInfinity();
	            break;
	        case 0:
	        default:
	        		rate = NumUtilsDispatch.min(service_curve_1.getUltAffineRate(), service_curve_2.getUltAffineRate());
	        		break;
        }
        
        return CurvePwAffineFactoryDispatch.createRateLatency(
                rate,
                NumUtilsDispatch.add(service_curve_1.getLatency(), service_curve_2.getLatency()));
    }

    /**
     * Returns the convolution of two curve, which must be convex
     *
     * @param service_curve_1 The first curve to convolve with.
     * @param service_curve_2 The second curve to convolve with.
     * @return The convolved curve.
     */
    private static ServiceCurve convolve_SC_SC_Generic(ServiceCurve service_curve_1, ServiceCurve service_curve_2) {
        switch (MinPlusInputChecks.inputNullCheck(service_curve_1, service_curve_2)) {
            case 1:
                return service_curve_2.copy();
            case 2:
                return service_curve_1.copy();
            case 3:
                return CurvePwAffineFactoryDispatch.createZeroService();
            case 0:
            default:
                break;
        }

        // Shortcut: only go here if there is at least one delayed infinite burst
        if (service_curve_1.getDelayedInfiniteBurst_Property() || service_curve_2.getDelayedInfiniteBurst_Property()) {
            if (service_curve_1.getDelayedInfiniteBurst_Property() && service_curve_2.getDelayedInfiniteBurst_Property()) {
                return CurvePwAffineFactoryDispatch.createDelayedInfiniteBurst(NumUtilsDispatch.add(service_curve_1.getLatency(), service_curve_2.getLatency()));
            }

            if (service_curve_1.getDelayedInfiniteBurst_Property()) { // service_curve_2 is not a delayed infinite burst
                return CurvePwAffineFactoryDispatch.createServiceCurve(CurvePwAffineUtilsDispatch.shiftRight(service_curve_2, service_curve_1.getLatency()));
            }

            if (service_curve_2.getDelayedInfiniteBurst_Property()) { // service_curve_2 is not a delayed infinite burst
                return CurvePwAffineFactoryDispatch.createServiceCurve(CurvePwAffineUtilsDispatch.shiftRight(service_curve_1, service_curve_2.getLatency()));
            }
        }

        ServiceCurve zero_service = CurvePwAffineFactoryDispatch.createZeroService();
        if (service_curve_1.equals(zero_service) || service_curve_2.equals(zero_service)) {
            return zero_service;
        }

        ServiceCurve result = CurvePwAffineFactoryDispatch.createServiceCurve();

        Num x = NumFactory.getNumFactory().createZero();
        Num y = NumFactory.getNumFactory().createZero(); // Functions pass though the origin
        Num grad = NumFactory.getNumFactory().createZero();
        LinearSegment s = LinearSegmentFactoryDispatch.createLinearSegment(x, y, grad, false);
        result.addSegment(s);

        int i1 = (service_curve_1.isRealDiscontinuity(0)) ? 1 : 0;
        int i2 = (service_curve_2.isRealDiscontinuity(0)) ? 1 : 0;
        if (i1 > 0 || i2 > 0) {
            x = NumFactory.getNumFactory().createZero();
            y = NumUtilsDispatch.add(service_curve_1.fLimitRight(NumFactory.getNumFactory().getZero()), service_curve_2.fLimitRight(NumFactory.getNumFactory().getZero()));
            grad = NumFactory.getNumFactory().createZero();
            s = LinearSegmentFactoryDispatch.createLinearSegment(x, y, grad, true);

            result.addSegment(s);
        }

        while (i1 < service_curve_1.getSegmentCount() || i2 < service_curve_2.getSegmentCount()) {
            if (service_curve_1.getSegment(i1).getGrad().lt(service_curve_2.getSegment(i2).getGrad())) {
                if (i1 + 1 >= service_curve_1.getSegmentCount()) {
                    result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_1.getSegment(i1).getGrad());
                    break;
                }

                x = NumUtilsDispatch.add(result.getSegment(result.getSegmentCount() - 1).getX(),
                        (NumUtilsDispatch.sub(service_curve_1.getSegment(i1 + 1).getX(), service_curve_1.getSegment(i1).getX())));
                y = NumUtilsDispatch.add(result.getSegment(result.getSegmentCount() - 1).getY(),
                        (NumUtilsDispatch.sub(service_curve_1.getSegment(i1 + 1).getY(), service_curve_1.getSegment(i1).getY())));
                grad = NumFactory.getNumFactory().createZero();
                s = LinearSegmentFactoryDispatch.createLinearSegment(x, y, grad, true);

                result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_1.getSegment(i1).getGrad());
                result.addSegment(s);

                i1++;
            } else {
                if (i2 + 1 >= service_curve_2.getSegmentCount()) {
                    result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_2.getSegment(i2).getGrad());
                    break;
                }

                x = NumUtilsDispatch.add(result.getSegment(result.getSegmentCount() - 1).getX(),
                        (NumUtilsDispatch.sub(service_curve_2.getSegment(i2 + 1).getX(), service_curve_2.getSegment(i2).getX())));
                y = NumUtilsDispatch.add(result.getSegment(result.getSegmentCount() - 1).getY(),
                        (NumUtilsDispatch.sub(service_curve_2.getSegment(i2 + 1).getY(), service_curve_2.getSegment(i2).getY())));
                grad = NumFactory.getNumFactory().createZero();
                s = LinearSegmentFactoryDispatch.createLinearSegment(x, y, grad, true);

                result.getSegment(result.getSegmentCount() - 1).setGrad(service_curve_2.getSegment(i2).getGrad());
                result.addSegment(s);

                i2++;
            }
        }

        CurvePwAffineUtilsDispatch.beautify(result);

        return result;
    }

    // Java won't let me call this method "convolve" because it does not care about the Sets' types; tells that there's already another method taking the same arguments.
    public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2) {
        // null and empty checks will be done by convolve_SCs_SCs( ... )
        return convolve_SCs_SCs(service_curves_1, service_curves_2, false);
    }

    public static Set<ServiceCurve> convolve_SCs_SCs(Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2, boolean tb_rl_optimized) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        // An empty or null set does is not interpreted as a convolution with a null curve.
        // Instead, the other set is return in case it is neither null or empty.
        Set<ServiceCurve> clone = new HashSet<ServiceCurve>();
        switch (MinPlusInputChecks.inputNullCheck(service_curves_1, service_curves_2)) {
            case 1:
                for (ServiceCurve sc : service_curves_2) {
                    clone.add(sc.copy());
                }
                return clone;
            case 2:
                for (ServiceCurve sc : service_curves_1) {
                    clone.add(sc.copy());
                }
                return clone;
            case 3:
                results.add(CurvePwAffineFactoryDispatch.createZeroService());
                return results;
            case 0:
            default:
                break;
        }
        switch (MinPlusInputChecks.inputEmptySetCheck(service_curves_1, service_curves_2)) {
            case 1:
                for (ServiceCurve sc : service_curves_2) {
                    clone.add(sc.copy());
                }
                return clone;
            case 2:
                for (ServiceCurve sc : service_curves_1) {
                    clone.add(sc.copy());
                }
                return clone;
            case 3:
                results.add(CurvePwAffineFactoryDispatch.createZeroService());
                return results;
            case 0:
            default:
                break;
        }

        for (ServiceCurve beta_1 : service_curves_1) {
            for (ServiceCurve beta_2 : service_curves_2) {
                results.add(convolve(beta_1, beta_2, tb_rl_optimized));
            }
        }

        return results;
    }

    //------------------------------------------------------------
    // Arrival Curves
    //------------------------------------------------------------
    public static ArrivalCurve convolve(ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2) {
        switch (MinPlusInputChecks.inputNullCheck(arrival_curve_1, arrival_curve_2)) {
            case 0:
                break;
            case 1:
                return arrival_curve_2.copy();
            case 2:
                return arrival_curve_1.copy();
            case 3:
                return CurvePwAffineFactoryDispatch.createZeroArrivals();
            default:
                break;
        }

        ArrivalCurve zero_arrival = CurvePwAffineFactoryDispatch.createZeroArrivals();
        if (arrival_curve_1.equals(zero_arrival) || arrival_curve_2.equals(zero_arrival)) {
            return zero_arrival;
        }

        ArrivalCurve zero_delay_infinite_burst = (ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst();
        if (arrival_curve_1.equals(zero_delay_infinite_burst)) {
            return arrival_curve_2.copy();
        }
        if (arrival_curve_2.equals(zero_delay_infinite_burst)) {
            return arrival_curve_1.copy();
        }

        // Arrival curves are concave curves so we can do a minimum instead of a convolution here.
        ArrivalCurve convolved_arrival_curve = CurvePwAffineFactoryDispatch.createArrivalCurve(CurvePwAffineUtilsDispatch.min(arrival_curve_1, arrival_curve_2));
        return convolved_arrival_curve;
    }
    
    public static ArrivalCurve convolve(Set<ArrivalCurve> arrival_curves) {
        // Custom null and empty checks for this single argument method.
        if (arrival_curves == null || arrival_curves.isEmpty()) {
            return CurvePwAffineFactoryDispatch.createZeroArrivals();
        }
        if (arrival_curves.size() == 1) {
            return arrival_curves.iterator().next().copy();
        }

        ArrivalCurve arrival_curve_result = (ArrivalCurve) CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurst();
        for (ArrivalCurve arrival_curve_2 : arrival_curves) {
            arrival_curve_result = convolve(arrival_curve_result, arrival_curve_2);
        }

        return arrival_curve_result;
    }

    //------------------------------------------------------------
    // Maximum Service Curves
    //------------------------------------------------------------
    /**
     * Returns the convolution of this curve, which must be (almost) concave, and
     * the given curve, which must also be (almost) concave.
     *
     * @param max_service_curve_1 The fist maximum service curve in the convolution.
     * @param max_service_curve_2 The second maximum service curve in the convolution.
     * @return The convolved maximum service curve.
     */
    public static MaxServiceCurve convolve(MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2) {
        switch (MinPlusInputChecks.inputNullCheck(max_service_curve_1, max_service_curve_2)) {
            case 0:
                break;
            case 1:
                return max_service_curve_2.copy();
            case 2:
                return max_service_curve_1.copy();
            case 3:
                return CurvePwAffineFactoryDispatch.createZeroDelayInfiniteBurstMSC();
            default:
        }

        if (CalculatorConfig.getInstance().exec_max_service_curve_checks() &&
                (!max_service_curve_1.isAlmostConcave() || !max_service_curve_2.isAlmostConcave())) {
            throw new IllegalArgumentException("Both maximum service curves must be almost concave!");
        }

        Num latency_msc_1 = max_service_curve_1.getLatency();
        Num latency_msc_2 = max_service_curve_2.getLatency();

        if (latency_msc_1.equals(NumFactory.getNumFactory().getPositiveInfinity())) {
            return max_service_curve_2.copy();
        }
        if (latency_msc_2.equals(NumFactory.getNumFactory().getPositiveInfinity())) {
            return max_service_curve_1.copy();
        }

        // (min,plus)-algebraic proceeding here:
        // Remove latencies, act analog to the convolution of two arrival curves, and the sum of the two latencies.
        ArrivalCurve ac_intermediate = convolve(CurvePwAffineFactoryDispatch.createArrivalCurve(CurvePwAffineUtilsDispatch.removeLatency(max_service_curve_1)), CurvePwAffineFactoryDispatch.createArrivalCurve(CurvePwAffineUtilsDispatch.removeLatency(max_service_curve_2)));
        MaxServiceCurve result = CurvePwAffineFactoryDispatch.createMaxServiceCurve(ac_intermediate);
        result = (MaxServiceCurve) CurvePwAffineUtilsDispatch.shiftRight(result, NumUtilsDispatch.add(latency_msc_1, latency_msc_2));
        CurvePwAffineUtilsDispatch.beautify(result);

        return result;
    }

    //------------------------------------------------------------
    // Arrival Curves and Max Service Curves
    //------------------------------------------------------------
    // The result is used like an arrival curve, yet it is not really one. This inconsistency occurs because we need to consider MSC and SC in some order during the output bound computation.
    public static Set<CurvePwAffine> convolve_ACs_MSC(Set<ArrivalCurve> arrival_curves, MaxServiceCurve maximum_service_curve) throws Exception {
        switch (MinPlusInputChecks.inputNullCheck(arrival_curves, maximum_service_curve)) {
            case 1:
                return new HashSet<CurvePwAffine>();
            case 2:
                Set<CurvePwAffine> clone = new HashSet<CurvePwAffine>();

                for (ArrivalCurve ac : arrival_curves) {
                    clone.add(ac.copy());
                }
                return clone;
            case 3:
                return new HashSet<CurvePwAffine>();
            case 0:
            default:
                break;
        }
        if (arrival_curves.isEmpty()) {
            return new HashSet<CurvePwAffine>();
        }

        Num msc_latency = maximum_service_curve.getLatency();
        Set<CurvePwAffine> result = new HashSet<CurvePwAffine>();

        // Similar to convolve_ACs_EGamma
        ArrivalCurve msc_as_ac = CurvePwAffineFactoryDispatch.createArrivalCurve(CurvePwAffineUtilsDispatch.removeLatency(maximum_service_curve)); // Abuse the ArrivalCurve class here for convenience.
        for (ArrivalCurve ac : arrival_curves) {
            result.add(CurvePwAffineUtilsDispatch.shiftRight(convolve(ac, msc_as_ac), msc_latency));
        }

        return result;
    }

    public static Set<ArrivalCurve> convolve_ACs_EGamma(Set<ArrivalCurve> arrival_curves, MaxServiceCurve extra_gamma_curve) throws Exception {
        switch (MinPlusInputChecks.inputNullCheck(arrival_curves, extra_gamma_curve)) {
            case 0:
                break;
            case 1:
                return new HashSet<ArrivalCurve>();
            case 2:
                Set<ArrivalCurve> clone = new HashSet<ArrivalCurve>();

                for (ArrivalCurve ac : arrival_curves) {
                    clone.add(ac.copy());
                }
                return clone;
            case 3:
                return new HashSet<ArrivalCurve>();
            default:
        }
        if (arrival_curves.isEmpty()) {
            return new HashSet<ArrivalCurve>();
        }

        if (extra_gamma_curve.getLatency().gtZero()) {
            throw new Exception("Cannot convolve with an extra gamma curve with latency");
        }

        Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
        ArrivalCurve extra_gamma_as_ac = CurvePwAffineFactoryDispatch.createArrivalCurve(extra_gamma_curve); // Abuse the ArrivalCurve class here for convenience.
        for (ArrivalCurve ac : arrival_curves) {
            result.add(convolve(ac, extra_gamma_as_ac));
        }

        return result;
    }
}
