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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.bounds.disco.BoundingCurves_Disco_Configuration;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.num.Num;

public final class LeftOverService_Disco_PwAffine {
    public static Set<ServiceCurve> compute(AnalysisConfig configuration, Server server,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO
                || (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL
                && server.multiplexing() == Multiplexing.FIFO)) {
            return fifoMux(server.getServiceCurve(), arrival_curves);
        } else {
            return LeftOverService_Disco_PwAffine.arbMux(server.getServiceCurve(), arrival_curves);
        }
    }

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, ServiceCurve service_curve,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO) {
            return LeftOverService_Disco_PwAffine.fifoMux(service_curve, arrival_curves);
        } else {
            return LeftOverService_Disco_PwAffine.arbMux(service_curve, arrival_curves);
        }
    }

    public static Set<ServiceCurve> fifoMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(fifoMux(service_curve, alpha));
        }

        return results;
    }

    /**
     * Computes the left-over FIFO service curve for a server with the service curve
     * <code>beta</code> experiencing cross-traffic with arrival curve
     * <code>alpha</code>.
     * <p>
     * It computes the left-over service curve with the smallest latency T in a
     * worst-case FIFO multiplexing scenario. T is defined as the first time
     * instance when the arrival curve's burst is worked off and its arrival rate is
     * smaller than the service curve's service rate. At this time it can be safely
     * assumed that the system has spare capacity that, in the FIFO multiplexing
     * scheme, will be used to serve other flows' data that arrived in the meantime.
     *
     * @param arrival_curve The arrival curve of cross-traffic
     * @param service_curve The server's service curve
     * @return The FIFO service curve
     */
    public static ServiceCurve fifoMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	Pair<Boolean,ServiceCurve> special_cases = computeSpecialValues(service_curve, arrival_curve);
    	
    	if(special_cases.getFirst().booleanValue() == true) {
    		return special_cases.getSecond(); 
    	}
    	
        if (BoundingCurves_Disco_Configuration.getInstance().exec_fifo_mux_checks()) {
            if (!arrival_curve.isConcave()) {
                throw new IllegalArgumentException("Arrival curve must be concave.");
            }

            if (!service_curve.isConvex()) {
                throw new IllegalArgumentException("Service curve must be convex.");
            }
        }

        List<Num> ycoords = Curve.computeInflectionPointsY(arrival_curve, service_curve);
        for (int i = 0; i < ycoords.size(); i++) {
            Num ip_y = (ycoords.get(i));
            if (ip_y.lt(arrival_curve.getBurst())) {
                continue;
            }

            Num x_alpha = arrival_curve.f_inv(ip_y, false);
            Num x_beta = service_curve.f_inv(ip_y, true);

            if (arrival_curve.getGradientLimitRight(x_alpha).leq(service_curve.getGradientLimitRight(x_beta))) {

                Num theta = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(x_beta, x_alpha);
                ServiceCurve beta_fifo = Curve.getFactory()
                        .createServiceCurve(Curve.boundAtXAxis(Curve.min(
                                Curve.sub(service_curve,
                                        Curve.shiftRight(arrival_curve, theta)),
                                Curve.getFactory().createDelayedInfiniteBurst(x_beta))));
                return beta_fifo;
            }
        }

        // Reaching this code means that there's no service left-over
        return Curve.getFactory().createZeroService();
    }

    public static Set<ServiceCurve> arbMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(arbMux(service_curve, alpha));
        }

        return results;
    }

    /**
     * Computes the left-over service curve for a server under arbitrary
     * multiplexing with the service curve <code>beta</code> experiencing
     * cross-traffic with arrival curve <code>alpha</code>.
     *
     * @param arrival_curve The arrival curve of cross-traffic
     * @param service_curve The server's service curve
     * @return The FIFO service curve
     */
    public static ServiceCurve arbMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	Pair<Boolean,ServiceCurve> special_cases = computeSpecialValues(service_curve, arrival_curve);
    	
    	if(special_cases.getFirst().booleanValue() == true) {
    		return special_cases.getSecond(); 
    	} else {
            return Curve.getFactory().createServiceCurve(Curve.boundAtXAxis(Curve.sub(service_curve, arrival_curve)));
    	}
    }
    
    /**
     * Try to compute the left-over service curve for special arrival or service curve values like zero or infinite.
     * In case we find infinite service and infinite arrivals, 
     * we define the indeterminate form resulting from \infty - \infty as zero service. 
     * 
     * @param service_curve The service curve to be subtracted from. 
     * @param arrival_curve The arrival curve to subtract from the service curve.
     * @return A pair consisting of a boolean and a service curve. The boolean indicates the validity of the returned service curve. 
     */
    private static Pair<Boolean,ServiceCurve> computeSpecialValues(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	
    	if(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get().equals(arrival_curve)
    			|| Curve_ConstantPool.ZERO_SERVICE_CURVE.get().equals(service_curve)) {
    		return new Pair<Boolean,ServiceCurve>(true,Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
    	} else { // The else-clause is not required. It indicates that the following if-clause cannot be moved before the previous one.
	    	if(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get().equals(service_curve)) {
	    		return new Pair<Boolean,ServiceCurve>(true,Curve_ConstantPool.INFINITE_SERVICE_CURVE.get());
	    	}
    	}
    	if(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get().equals(arrival_curve)) {
    		return new Pair<Boolean,ServiceCurve>(true,service_curve.copy());
    	}
    	
    	// In case the caller disregards that there was no match to special values,
    	// we provide the worst-case left-over service: 0.
    	return new Pair<Boolean,ServiceCurve>(false,Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
    }
}
