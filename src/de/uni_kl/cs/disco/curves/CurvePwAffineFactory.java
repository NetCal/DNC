/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
 *
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
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

package de.uni_kl.cs.disco.curves;

import java.util.List;

import de.uni_kl.cs.disco.numbers.Num;

public interface CurvePwAffineFactory {
//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    CurvePwAffine createCurve(List<LinearSegment> segments);

    CurvePwAffine createZeroCurve();

    CurvePwAffine createHorizontal(double y);

    CurvePwAffine createHorizontal(Num y);


//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    ServiceCurve createServiceCurve();

    ServiceCurve createServiceCurve(int segment_count);

    ServiceCurve createServiceCurve(String service_curve_str) throws Exception;

    ServiceCurve createServiceCurve(CurvePwAffine curve);

    ServiceCurve createZeroService();

    ServiceCurve createZeroDelayInfiniteBurst();

    ServiceCurve createDelayedInfiniteBurst(double delay);

    ServiceCurve createDelayedInfiniteBurst(Num delay);

    ServiceCurve createRateLatency(double rate, double latency);

    ServiceCurve createRateLatency(Num rate, Num latency);


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    ArrivalCurve createArrivalCurve();

    ArrivalCurve createArrivalCurve(int segment_count);

    ArrivalCurve createArrivalCurve(String arrival_curve_str) throws Exception;

    ArrivalCurve createArrivalCurve(CurvePwAffine curve);

    ArrivalCurve createArrivalCurve(CurvePwAffine curve, boolean remove_latency);

    ArrivalCurve createZeroArrivals();

    ArrivalCurve createPeakArrivalRate(double rate);

    ArrivalCurve createPeakArrivalRate(Num rate);

    ArrivalCurve createTokenBucket(double rate, double burst);

    ArrivalCurve createTokenBucket(Num rate, Num burst);


//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    MaxServiceCurve createMaxServiceCurve();

    MaxServiceCurve createMaxServiceCurve(int segment_count);

    MaxServiceCurve createMaxServiceCurve(String max_service_curve_str) throws Exception;

    MaxServiceCurve createMaxServiceCurve(CurvePwAffine curve);

    MaxServiceCurve createInfiniteMaxService();

    MaxServiceCurve createZeroDelayInfiniteBurstMSC();

    MaxServiceCurve createDelayedInfiniteBurstMSC(double delay);

    MaxServiceCurve createDelayedInfiniteBurstMSC(Num delay);

    MaxServiceCurve createRateLatencyMSC(double rate, double latency);

    MaxServiceCurve createRateLatencyMSC(Num rate, Num latency);
}