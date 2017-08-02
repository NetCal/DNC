/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta1 "Chimera".
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

package de.uni_kl.cs.disco.nc.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactory;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtils;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumUtils;

public final class LeftOverService {
    private LeftOverService() {}

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, Server server, Set<ArrivalCurve> arrival_curves) {
        if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO
                || (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL
                && server.multiplexingDiscipline() == AnalysisConfig.Multiplexing.FIFO)) {
            return fifoMux(server.getServiceCurve(), arrival_curves);
        } else {
            return LeftOverService.arbMux(server.getServiceCurve(), arrival_curves);
        }
    }

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
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
     * <code>beta</code> experiencing cross-traffic with arrival curve <code>alpha</code>.
     * <p>
     * It computes the left-over service curve with the smallest latency T in a
     * worst-case FIFO multiplexing scenario. T is defined as the first time instance when
     * the arrival curve's burst is worked off and its arrival rate is smaller than
     * the service curve's service rate. At this time it can be safely assumed that
     * the system has spare capacity that, in the FIFO multiplexing scheme,
     * will be used to serve other flows' data that arrived in the meantime.
     *
     * @param arrival_curve The arrival curve of cross-traffic
     * @param service_curve The server's service curve
     * @return The FIFO service curve
     */
    public static ServiceCurve fifoMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        if (arrival_curve.equals(CurvePwAffineFactory.createZeroArrivals())
                || service_curve.equals(CurvePwAffineFactory.createZeroDelayInfiniteBurst())) {
            return service_curve.copy();
        }
        if (service_curve.equals(CurvePwAffineFactory.createZeroService())) {
            return CurvePwAffineFactory.createZeroService();
        }

        if (CalculatorConfig.FIFO_MUX_CHECKS) {
            if (!arrival_curve.isConcave()) {
                throw new IllegalArgumentException("Arrival curve must be concave.");
            }

            if (!service_curve.isConvex()) {
                throw new IllegalArgumentException("Service curve must be convex.");
            }
        }

        List<Num> ycoords = CurvePwAffineUtils.computeInflectionPointsY(arrival_curve, service_curve);
        for (int i = 0; i < ycoords.size(); i++) {
            Num ip_y = (ycoords.get(i));
            if (ip_y.lt(arrival_curve.getBurst())) {
                continue;
            }

            Num x_alpha = arrival_curve.f_inv(ip_y, false);
            Num x_beta = service_curve.f_inv(ip_y, true);

            if (arrival_curve.getGradientLimitRight(x_alpha).leq(service_curve.getGradientLimitRight(x_beta))) {

                Num theta = NumUtils.sub(x_beta, x_alpha);
                ServiceCurve beta_fifo = CurvePwAffineFactory.createServiceCurve( CurvePwAffineUtils.boundAtXAxis(
                        CurvePwAffineUtils.min(CurvePwAffineUtils.sub(service_curve, CurvePwAffineUtils.shiftRight(arrival_curve, theta)), CurvePwAffineFactory.createDelayedInfiniteBurst(x_beta))
                ));
                return beta_fifo;
            }
        }

        // Reaching this code means that there's no service left-over
        return CurvePwAffineFactory.createZeroService();
    }

    public static Set<ServiceCurve> arbMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(arbMux(service_curve, alpha));
        }

        return results;
    }

    /**
     * Computes the left-over service curve for a server under arbitrary multiplexing
     * with the service curve <code>beta</code> experiencing cross-traffic
     * with arrival curve <code>alpha</code>.
     *
     * @param arrival_curve The arrival curve of cross-traffic
     * @param service_curve The server's service curve
     * @return The FIFO service curve
     */
    public static ServiceCurve arbMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        if (arrival_curve.equals(CurvePwAffineFactory.createZeroArrivals())
                || service_curve.equals(CurvePwAffineFactory.createZeroDelayInfiniteBurst())) {
            return service_curve.copy();
        }
        if (service_curve.equals(CurvePwAffineFactory.createZeroService())) {
            return CurvePwAffineFactory.createZeroService();
        }

        return CurvePwAffineFactory.createServiceCurve(CurvePwAffineUtils.boundAtXAxis(CurvePwAffineUtils.sub(service_curve, arrival_curve)));
    }
}
