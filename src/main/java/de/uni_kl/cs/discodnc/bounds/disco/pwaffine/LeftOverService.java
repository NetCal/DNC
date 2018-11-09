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

package de.uni_kl.cs.discodnc.bounds.disco.pwaffine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.feedforward.AnalysisConfig;
import de.uni_kl.cs.discodnc.feedforward.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.feedforward.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

public final class LeftOverService {
    private LeftOverService() {
    }

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, Server server,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO
                || (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL
                && server.multiplexingDiscipline() == Multiplexing.FIFO)) {
            return fifoMux(server.getServiceCurve(), arrival_curves);
        } else {
            return LeftOverService.arbMux(server.getServiceCurve(), arrival_curves);
        }
    }

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, ServiceCurve service_curve,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO) {
            return LeftOverService.fifoMux(service_curve, arrival_curves);
        } else {
            return LeftOverService.arbMux(service_curve, arrival_curves);
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
    	
        if (Calculator.getInstance().exec_fifo_mux_checks()) {
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
    
    private static Pair<Boolean,ServiceCurve> computeSpecialValues(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	ServiceCurve ZERO_SERVICE = Curve.getFactory().createZeroService();
    	ServiceCurve INFINITE_SERVICE = Curve.getFactory().createZeroDelayInfiniteBurst();
    	
    	ArrivalCurve ZERO_ARRIVALS = Curve.getFactory().createZeroArrivals();
    	ArrivalCurve INFINITE_ARRIVALS = Curve.getFactory().createInfiniteArrivals();
    	
    	if(arrival_curve.equals(INFINITE_ARRIVALS)
    			|| service_curve.equals(ZERO_SERVICE)) {
    		return new Pair<Boolean,ServiceCurve>(true,ZERO_SERVICE);
    	} else { // The else-clause is not required. It indicates that the following if-clause cannot be moved before the previous one.
	    	if(service_curve.equals(INFINITE_SERVICE)) {
	    		return new Pair<Boolean,ServiceCurve>(true,INFINITE_SERVICE);
	    	}
    	}
    	if(arrival_curve.equals(ZERO_ARRIVALS)) {
    		return new Pair<Boolean,ServiceCurve>(true,service_curve.copy());
    	}
    	
    	
    	// In case the caller disregards that there was no match to special values,
    	// we provide the worst-case left-over service: 0.
    	return new Pair<Boolean,ServiceCurve>(false,ZERO_SERVICE);
    }
}
