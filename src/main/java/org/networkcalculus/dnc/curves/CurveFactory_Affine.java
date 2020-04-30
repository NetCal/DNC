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

package org.networkcalculus.dnc.curves;

import java.util.List;

import org.networkcalculus.num.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public interface CurveFactory_Affine {

    // ------------------------------------------------------------
    // Curves (generic)
    // ------------------------------------------------------------

    // Construction
    Curve createZeroCurve();

    Curve createHorizontal(Num y);

    Curve createCurve(List<LinearSegment> segments);

    // ------------------------------------------------------------
    // Service Curves
    // ------------------------------------------------------------
    
    ServiceCurve createServiceCurve();

    ServiceCurve createServiceCurve(int segment_count);

    ServiceCurve createServiceCurve(String service_curve_str) throws Exception;

    ServiceCurve createServiceCurve(Curve curve);

    ServiceCurve createZeroService();

    ServiceCurve createZeroDelayInfiniteBurst();

    ServiceCurve createDelayedInfiniteBurst(double delay);

    ServiceCurve createDelayedInfiniteBurst(Num delay);

    ServiceCurve createRateLatency(double rate, double latency);

    ServiceCurve createRateLatency(Num rate, Num latency);

    // ------------------------------------------------------------
    // Arrival Curves
    // ------------------------------------------------------------

    ArrivalCurve createArrivalCurve();

    ArrivalCurve createArrivalCurve(int segment_count);

    ArrivalCurve createArrivalCurve(String arrival_curve_str) throws Exception;

    ArrivalCurve createArrivalCurve(Curve curve);

    ArrivalCurve createArrivalCurve(Curve curve, boolean remove_latency);

    ArrivalCurve createZeroArrivals();

    ArrivalCurve createInfiniteArrivals();

    ArrivalCurve createPeakArrivalRate(double rate);

    ArrivalCurve createPeakArrivalRate(Num rate);

    ArrivalCurve createTokenBucket(double rate, double burst);

    ArrivalCurve createTokenBucket(Num rate, Num burst);

    // ------------------------------------------------------------
    // Maximum Service Curves
    // ------------------------------------------------------------

    MaxServiceCurve createMaxServiceCurve();

    MaxServiceCurve createMaxServiceCurve(int segment_count);

    MaxServiceCurve createMaxServiceCurve(String max_service_curve_str) throws Exception;

    MaxServiceCurve createMaxServiceCurve(Curve curve);

    MaxServiceCurve createZeroDelayInfiniteBurstMSC();

    MaxServiceCurve createDelayedInfiniteBurstMSC(double delay);

    MaxServiceCurve createDelayedInfiniteBurstMSC(Num delay);

    MaxServiceCurve createRateLatencyMSC(double rate, double latency);

    MaxServiceCurve createRateLatencyMSC(Num rate, Num latency);
}
