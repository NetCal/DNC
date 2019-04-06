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

package org.networkcalculus.dnc.bounds.disco;

import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.bounds.BoundingCurves;
import org.networkcalculus.dnc.bounds.disco.con_pw_affine.Output_Disco_ConPwAffine;
import org.networkcalculus.dnc.bounds.disco.pw_affine.LeftOverService_Disco_PwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;

/**
 * Inherits the restrictions of the DISCO deconvolution implementation.
 */
public enum BoundingCurves_Disco_ConPwAffine implements BoundingCurves {
	BOUNDINGCURVES_DISCO_CONPWAFFINE;
	
    // --------------------------------------------------------------------------------------------------------------
    // left-over Service
    // --------------------------------------------------------------------------------------------------------------

    public Set<ServiceCurve> leftOverService(AnalysisConfig configuration, Server server, Set<ArrivalCurve> arrival_curves) {
        return LeftOverService_Disco_PwAffine.compute(configuration, server, arrival_curves);
    }

    public Set<ServiceCurve> leftOverService(AnalysisConfig configuration, ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        return LeftOverService_Disco_PwAffine.compute(configuration, service_curve, arrival_curves);
    }

    public Set<ServiceCurve> leftOverServiceFIFO(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        return LeftOverService_Disco_PwAffine.fifoMux(service_curve, arrival_curves);
    }

    public ServiceCurve leftOverServiceFIFO(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        return LeftOverService_Disco_PwAffine.fifoMux(service_curve, arrival_curve);
    }

    public Set<ServiceCurve> leftOverServiceARB(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        return LeftOverService_Disco_PwAffine.arbMux(service_curve, arrival_curves);
    }

    public ServiceCurve leftOverServiceARB(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
        return LeftOverService_Disco_PwAffine.arbMux(service_curve, arrival_curve);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Output
    // --------------------------------------------------------------------------------------------------------------

    public Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server) throws Exception {
        return Output_Disco_ConPwAffine.compute(configuration, arrival_curves, server);
    }

    public Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server, Set<ServiceCurve> betas_lo) throws Exception {
        return Output_Disco_ConPwAffine.compute(configuration, arrival_curves, server, betas_lo);
    }

    public Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Path path, Set<ServiceCurve> betas_lo) throws Exception {
        return Output_Disco_ConPwAffine.compute(configuration, arrival_curves, path, betas_lo);
    }
}
