/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
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

package org.networkcalculus.dnc.bounds;

import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;

public interface BoundingCurves {
	
    // --------------------------------------------------------------------------------------------------------------
    // left-over Service
    // --------------------------------------------------------------------------------------------------------------

    Set<ServiceCurve> leftOverService(AnalysisConfig configuration, Server server, Set<ArrivalCurve> arrival_curves);

    Set<ServiceCurve> leftOverService(AnalysisConfig configuration, ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves);

    Set<ServiceCurve> leftOverServiceFIFO(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves);

    ServiceCurve leftOverServiceFIFO(ServiceCurve service_curve, ArrivalCurve arrival_curve);

    Set<ServiceCurve> leftOverServiceARB(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves);

    ServiceCurve leftOverServiceARB(ServiceCurve service_curve, ArrivalCurve arrival_curve);

    // --------------------------------------------------------------------------------------------------------------
    // Output
    // --------------------------------------------------------------------------------------------------------------

    Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server) throws Exception;

    Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Server server, Set<ServiceCurve> betas_lo) throws Exception;

    Set<ArrivalCurve> output(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves, Path path, Set<ServiceCurve> betas_lo) throws Exception;
}
