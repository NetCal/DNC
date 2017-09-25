/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
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

import ch.ethz.rtc.kernel.Curve;
import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Path;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;

import java.util.ArrayList;
import java.util.Set;

public class OperationDispatcher {
    private OperationDispatcher() {
    }

    public static Num bl_derive(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return BacklogBound.derive(arrival_curve, service_curve);
        }
        //return BacklogBound.derive(arrival_curve,service_curve);

        //TODO: does not work:
        /*
        if (arrival_curve.equals(CurvePwAffine.getFactory().createZeroArrivals())) {
            return Num.getFactory().createZero();
        }
        if (service_curve.getDelayedInfiniteBurst_Property()) {
            return arrival_curve.f(service_curve.getLatency());
        }
        if (service_curve.equals(CurvePwAffine.getFactory().createZeroService())
                || arrival_curve.getUltAffineRate().gt(service_curve.getUltAffineRate())) {
            return Num.getFactory().createPositiveInfinity();
        }

        Curve a = ((Curve_MPARTC_PwAffine) arrival_curve).getRtc_curve();
        Curve b = ((Curve_MPARTC_PwAffine) service_curve).getRtc_curve();

        double result = CurveMath.maxVDist(a,b);

        return Num.getFactory().create(result);
        */
        Curve a = ((Curve_MPARTC_PwAffine) arrival_curve).getRtc_curve();
        Curve b = ((Curve_MPARTC_PwAffine) service_curve).getRtc_curve();

        double result = a.yXPlusEpsilon(0);

        //TODO: RTC equivalent for inflection points? There should be a better way
        ArrayList<Num> xcoords2 = CurvePwAffine.computeInflectionPointsX(arrival_curve, service_curve);
        ArrayList<Double> xcoords = new ArrayList<>();
        for (int i = 0; i < xcoords2.size(); i++) {
            xcoords.add(xcoords2.get(i).doubleValue());
        }
        for (int i = 0; i < xcoords.size(); i++) {
            double ip_x = xcoords.get(i);

            double backlog = a.yXPlusEpsilon(ip_x) - b.yXPlusEpsilon(ip_x);
            result = Math.max(result, backlog);
        }
        return Num.getFactory().create(result);
    }

    public static double bl_derivePmooSinkTreeTbRl(Network tree, Server root,
                                                   AnalysisConfig.ArrivalBoundMethod sink_tree_ab) throws Exception {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return BacklogBound.derivePmooSinkTreeTbRl(tree, root, sink_tree_ab);
        }
        //TODO: not relevant for RTC?
        return BacklogBound.derivePmooSinkTreeTbRl(tree, root, sink_tree_ab);
    }


    public static Num db_deriveARB(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return DelayBound.deriveARB(arrival_curve, service_curve);
        }
        return DelayBound.deriveARB(arrival_curve, service_curve);

        //TODO: doesn work?
        //return CurvePwAffine.getXIntersection(arrival_curve, service_curve);
        //Curve a = ((Curve_MPARTC_PwAffine) arrival_curve).getRtc_curve();
        //Curve b = ((Curve_MPARTC_PwAffine) service_curve).getRtc_curve();

        //double result = CurveMath.maxHDist(a,b);

        //return Num.getFactory().create(result);
    }


    //TODO: RTC capable of this?
    public static Num db_deriveFIFO(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return DelayBound.deriveFIFO(arrival_curve, service_curve);
        }
        return DelayBound.deriveFIFO(arrival_curve, service_curve);
        //TODO: does not work
        //Curve a = ((Curve_MPARTC_PwAffine) arrival_curve).getRtc_curve();
        //Curve b = ((Curve_MPARTC_PwAffine) service_curve).getRtc_curve();

        //double result = CurveMath.maxHDist(a,b);

        //return Num.getFactory().create(result);
    }


    public static Set<ServiceCurve> lo_compute(AnalysisConfig configuration, Server server,
                                               Set<ArrivalCurve> arrival_curves) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.compute(configuration, server, arrival_curves);
        }
        return LeftOverService.compute(configuration, server, arrival_curves);
    }

    public static Set<ServiceCurve> lo_compute(AnalysisConfig configuration, ServiceCurve service_curve,
                                               Set<ArrivalCurve> arrival_curves) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.compute(configuration, service_curve, arrival_curves);
        }
        return LeftOverService.compute(configuration, service_curve, arrival_curves);

    }

    public static Set<ServiceCurve> lo_fifoMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.fifoMux(service_curve, arrival_curves);
        }
        return LeftOverService.fifoMux(service_curve, arrival_curves);
    }

    public static ServiceCurve lo_fifoMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.fifoMux(service_curve, arrival_curve);
        }

        return LeftOverService.fifoMux(service_curve, arrival_curve);
    }

    public static Set<ServiceCurve> lo_arbMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.arbMux(service_curve, arrival_curves);
        }
        return LeftOverService.arbMux(service_curve, arrival_curves);
    }

    public static ServiceCurve lo_arbMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return LeftOverService.arbMux(service_curve, arrival_curve);
        }
        return LeftOverService.arbMux(service_curve, arrival_curve);
    }


    public static Set<ArrivalCurve> ob_compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves,
                                               Server server) throws Exception {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return OutputBound.compute(configuration, arrival_curves, server);
        }
        return OutputBound.compute(configuration, arrival_curves, server);
    }

    public static Set<ArrivalCurve> ob_compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves,
                                               Server server, Set<ServiceCurve> betas_lo) throws Exception {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return OutputBound.compute(configuration, arrival_curves, server, betas_lo);
        }
        return OutputBound.compute(configuration, arrival_curves, server, betas_lo);
    }

    public static Set<ArrivalCurve> ob_compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Path path,
                                               Set<ServiceCurve> betas_lo) throws Exception {
        if (CalculatorConfig.getInstance().getOperationImpl().equals(CalculatorConfig.OperationImpl.DNC)
                || CalculatorConfig.getInstance().getCurveImpl().equals(CalculatorConfig.CurveImpl.DNC)) {
            return OutputBound.compute(configuration, arrival_curves, path, betas_lo);
        }
        return OutputBound.compute(configuration, arrival_curves, path, betas_lo);
    }
}
