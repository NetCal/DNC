/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
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

import de.uni_kl.cs.disco.curves.dnc.CurveFactory_DNC;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.CurveFactory_MPARTC_PwAffine;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.nc.CalculatorConfig.CurveClass;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;

/**
 * Static access to the methods defined in the CurveFactoryInterface.
 *
 */
public abstract class CurvePwAffineFactoryDispatch {
	private static CurvePwAffineFactory dnc_factory = new CurveFactory_DNC();
	private static CurvePwAffineFactory mpa_rtc_pwaffine_factory = new CurveFactory_MPARTC_PwAffine();

	private static CurvePwAffineFactory factory = getCurveFactory();

	private static CurvePwAffineFactory getCurveFactory() {
		switch (CalculatorConfig.getInstance().getCurveClass()) {
		case MPA_RTC:
			return mpa_rtc_pwaffine_factory;
		case DNC:
		default:
			return dnc_factory;
		}
	}

	public static void setCurveClass(CurveClass curve_class) {
		switch (curve_class) {
		case MPA_RTC:
			factory = mpa_rtc_pwaffine_factory;
			return;
		case DNC:
		default:
			factory = dnc_factory;
			return;
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	// Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public static CurvePwAffine createCurve(List<LinearSegment> segments) {
		return factory.createCurve(segments);
	}

	public static CurvePwAffine createZeroCurve() {
		return factory.createZeroCurve();
	}

	public static CurvePwAffine createHorizontal(double y) {
		return factory.createHorizontal(y);
	}

	public static CurvePwAffine createHorizontal(Num y) {
		return factory.createHorizontal(y);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Service Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public static ServiceCurve createServiceCurve() {
		return factory.createServiceCurve();
	}

	public static ServiceCurve createServiceCurve(int segment_count) {
		return factory.createServiceCurve(segment_count);
	}

	public static ServiceCurve createServiceCurve(String service_curve_str) throws Exception {
		return factory.createServiceCurve(service_curve_str);
	}

	public static ServiceCurve createServiceCurve(CurvePwAffine curve) {
		return factory.createServiceCurve(curve);
	}

	public static ServiceCurve createZeroService() {
		return factory.createZeroService();
	}

	public static ServiceCurve createZeroDelayInfiniteBurst() {
		return factory.createZeroDelayInfiniteBurst();
	}

	public static ServiceCurve createDelayedInfiniteBurst(double delay) {
		return factory.createDelayedInfiniteBurst(delay);
	}

	public static ServiceCurve createDelayedInfiniteBurst(Num delay) {
		return factory.createDelayedInfiniteBurst(delay);
	}

	public static ServiceCurve createRateLatency(double rate, double latency) {
		return factory.createRateLatency(rate, latency);
	}

	public static ServiceCurve createRateLatency(Num rate, Num latency) {
		return factory.createRateLatency(rate, latency);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public static ArrivalCurve createArrivalCurve() {
		return factory.createArrivalCurve();
	}

	public static ArrivalCurve createArrivalCurve(int segment_count) {
		return factory.createArrivalCurve(segment_count);
	}

	public static ArrivalCurve createArrivalCurve(String arrival_curve_str) throws Exception {
		return factory.createArrivalCurve(arrival_curve_str);
	}

	public static ArrivalCurve createArrivalCurve(CurvePwAffine curve) {
		return factory.createArrivalCurve(curve);
	}

	public static ArrivalCurve createArrivalCurve(CurvePwAffine curve, boolean remove_latency) {
		return factory.createArrivalCurve(curve, remove_latency);
	}

	public static ArrivalCurve createZeroArrivals() {
		return factory.createZeroArrivals();
	}

	public static ArrivalCurve createPeakArrivalRate(double rate) {
		return factory.createPeakArrivalRate(rate);
	}

	public static ArrivalCurve createPeakArrivalRate(Num rate) {
		return factory.createPeakArrivalRate(rate);
	}

	public static ArrivalCurve createTokenBucket(double rate, double burst) {
		return factory.createTokenBucket(rate, burst);
	}

	public static ArrivalCurve createTokenBucket(Num rate, Num burst) {
		return factory.createTokenBucket(rate, burst);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public static MaxServiceCurve createMaxServiceCurve() {
		return factory.createMaxServiceCurve();
	}

	public static MaxServiceCurve createMaxServiceCurve(int segment_count) {
		return factory.createMaxServiceCurve(segment_count);
	}

	public static MaxServiceCurve createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return factory.createMaxServiceCurve(max_service_curve_str);
	}

	public static MaxServiceCurve createMaxServiceCurve(CurvePwAffine curve) {
		return factory.createMaxServiceCurve(curve);
	}

	public static MaxServiceCurve createInfiniteMaxService() {
		return factory.createInfiniteMaxService();
	}

	public static MaxServiceCurve createZeroDelayInfiniteBurstMSC() {
		return factory.createZeroDelayInfiniteBurstMSC();
	}

	public static MaxServiceCurve createDelayedInfiniteBurstMSC(double delay) {
		checkMSClatency(NumFactory.getNumFactory().create(delay));
		return factory.createDelayedInfiniteBurstMSC(delay);
	}

	public static MaxServiceCurve createDelayedInfiniteBurstMSC(Num delay) {
		checkMSClatency(delay);
		return factory.createDelayedInfiniteBurstMSC(delay);
	}

	public static MaxServiceCurve createRateLatencyMSC(double rate, double latency) {
		checkMSCrate(NumFactory.getNumFactory().create(rate));
		checkMSClatency(NumFactory.getNumFactory().create(latency));
		return factory.createRateLatencyMSC(rate, latency);
	}

	public static MaxServiceCurve createRateLatencyMSC(Num rate, Num latency) {
		checkMSCrate(rate);
		checkMSClatency(latency);
		return factory.createRateLatencyMSC(rate, latency);
	}

	private static void checkMSClatency(Num latency) {
		if (latency.equals(NumFactory.getNumFactory().getPositiveInfinity())) {
			throw new IllegalArgumentException("Maximum service curve cannot be zero.");
		}
	}

	private static void checkMSCrate(Num rate) {
		if (rate.eqZero()) {
			throw new IllegalArgumentException("Maximum service curve cannot be zero.");
		}
	}
}