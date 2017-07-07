/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera"
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.disco.curves.mpa_rtc_pwaffine;

import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurvePwAffineFactoryInterface;
import de.uni_kl.disco.curves.CurvePwAffine;
import de.uni_kl.disco.curves.CurvePwAffineUtils;
import de.uni_kl.disco.curves.LinearSegment;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

import java.util.List;

//TODO @Steffen: what do you mean with "Given any Num, RTC converts it to double and vice versa"?

//TODO Currently, arrival curves are not enforced to be 0 in the origin
//		because the RTC toolbox does not allow for such spots as they occur in token bucket constrained arrivals. 
public class CurveFactory_MPARTC_PwAffine implements CurvePwAffineFactoryInterface {
    protected static CurveFactory_MPARTC_PwAffine factory_object = new CurveFactory_MPARTC_PwAffine();

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public Curve_MPARTC_PwAffine createCurve(List<LinearSegment> segments) {
        Curve_MPARTC_PwAffine msc_rtc = new Curve_MPARTC_PwAffine();

        // Check for origin + burst
        if (segments.size() > 1
                && segments.get(0).getX().eqZero()
                && segments.get(1).getX().eqZero()) {
            segments.remove(0);
        }
        SegmentList segments_rtc = new SegmentList();

        for (LinearSegment s : segments) {
            segments_rtc.add(new Segment(
                    s.getX().doubleValue(),
                    s.getY().doubleValue(),
                    s.getGrad().doubleValue()));
        }

        msc_rtc.initializeWithSegments(segments_rtc);
        return msc_rtc;
    }

    public Curve_MPARTC_PwAffine createZeroCurve() {
        return new Curve_MPARTC_PwAffine(); // CurveRTC constructor's default behavior
    }

    public Curve_MPARTC_PwAffine createHorizontal(double y) {
        Curve_MPARTC_PwAffine c_rtc = new Curve_MPARTC_PwAffine();
        makeHorizontal(c_rtc, y);
        return c_rtc;
    }

    /**
     * Creates a horizontal curve.
     *
     * @param y the y-intercept of the curve
     * @return a <code>Curve</code> instance
     */
    public Curve_MPARTC_PwAffine createHorizontal(Num y) {
        return createHorizontal(y.doubleValue());
    }

    
//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ServiceCurve_MPARTC_PwAffine createServiceCurve() {
        return new ServiceCurve_MPARTC_PwAffine();
    }

    public ServiceCurve_MPARTC_PwAffine createServiceCurve(int segment_count) {
        return new ServiceCurve_MPARTC_PwAffine(segment_count);
    }

    public ServiceCurve_MPARTC_PwAffine createServiceCurve(String service_curve_str) throws Exception {
        return new ServiceCurve_MPARTC_PwAffine(service_curve_str);
    }

    public ServiceCurve_MPARTC_PwAffine createServiceCurve(CurvePwAffine curve) {
        return new ServiceCurve_MPARTC_PwAffine(curve);
    }

    public ServiceCurve_MPARTC_PwAffine createZeroService() {
        return new ServiceCurve_MPARTC_PwAffine(); // ServiceCurveRTC constructor's default behavior
    }

    /**
     * Creates an infinite burst curve with zero delay.
     *
     * @return a <code>ServiceCurve</code> instance
     */
    public ServiceCurve_MPARTC_PwAffine createZeroDelayInfiniteBurst() {
        ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
        makeHorizontal(sc_rtc, Double.POSITIVE_INFINITY);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurst(double delay) {
        ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
        makeDelayedInfiniteBurst(sc_rtc, delay);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurst(Num delay) {
        return createDelayedInfiniteBurst(delay.doubleValue());
    }

    public ServiceCurve_MPARTC_PwAffine createRateLatency(double rate, double latency) {
        ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
        makeRateLatency(sc_rtc, rate, latency);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_PwAffine createRateLatency(Num rate, Num latency) {
        return createRateLatency(rate.doubleValue(), latency.doubleValue());
    }


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ArrivalCurve_MPARTC_PwAffine createArrivalCurve() {
        return new ArrivalCurve_MPARTC_PwAffine();
    }

    public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(int segment_count) {
        return new ArrivalCurve_MPARTC_PwAffine(segment_count);
    }

    public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(String arrival_curve_str) throws Exception {
        return new ArrivalCurve_MPARTC_PwAffine(arrival_curve_str);
    }

    public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(CurvePwAffine curve) {
        return new ArrivalCurve_MPARTC_PwAffine(curve);
    }

    public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(CurvePwAffine curve, boolean remove_latency) {
        return createArrivalCurve(CurvePwAffineUtils.removeLatency(curve));
    }

    public ArrivalCurve_MPARTC_PwAffine createZeroArrivals() {
        return new ArrivalCurve_MPARTC_PwAffine(); // ArrivalCurveRTC constructor's default behavior
    }

    public ArrivalCurve_MPARTC_PwAffine createPeakArrivalRate(double rate) {
        ArrivalCurve_MPARTC_PwAffine ac_rtc = new ArrivalCurve_MPARTC_PwAffine();
        makePeakRate(ac_rtc, rate);
        return ac_rtc;
    }

    public ArrivalCurve_MPARTC_PwAffine createPeakArrivalRate(Num rate) {
        return createPeakArrivalRate(rate.doubleValue());
    }

    public ArrivalCurve_MPARTC_PwAffine createTokenBucket(double rate, double burst) {
        ArrivalCurve_MPARTC_PwAffine ac_rtc = new ArrivalCurve_MPARTC_PwAffine();
        makeTokenBucket(ac_rtc, rate, burst);
        return ac_rtc;
    }

    public ArrivalCurve_MPARTC_PwAffine createTokenBucket(Num rate, Num burst) {
        return createTokenBucket(rate.doubleValue(), burst.doubleValue());
    }

    
//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve() {
        return new MaxServiceCurve_MPARTC_PwAffine();
    }

    public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(int segment_count) {
        return new MaxServiceCurve_MPARTC_PwAffine(segment_count);
    }

    public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return new MaxServiceCurve_MPARTC_PwAffine(max_service_curve_str);
    }

    public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(CurvePwAffine curve) {
        return new MaxServiceCurve_MPARTC_PwAffine(curve);
    }

    public MaxServiceCurve_MPARTC_PwAffine createInfiniteMaxService() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurve_MPARTC_PwAffine createZeroDelayInfiniteBurstMSC() {
        MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
        makeHorizontal(msc_rtc, Double.POSITIVE_INFINITY);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurstMSC(double delay) {
        MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
        makeDelayedInfiniteBurst(msc_rtc, delay);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurstMSC(Num delay) {
        return createDelayedInfiniteBurstMSC(delay.doubleValue());
    }

    public MaxServiceCurve_MPARTC_PwAffine createRateLatencyMSC(double rate, double latency) {
        MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
        makeRateLatency(msc_rtc, rate, latency);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_PwAffine createRateLatencyMSC(Num rate, Num latency) {
        return createRateLatencyMSC(rate.doubleValue(), latency.doubleValue());
    }

    
//--------------------------------------------------------------------------------------------------------------
// Curve assembly
//--------------------------------------------------------------------------------------------------------------
    private void makeHorizontal(Curve_MPARTC_PwAffine c_rtc, double y) {
        c_rtc.initializeWithSegment(new Segment(0.0, y, 0.0));

        if (y == Double.POSITIVE_INFINITY) {
            c_rtc.is_delayed_infinite_burst = true;
        }
    }

    private void makeDelayedInfiniteBurst(Curve_MPARTC_PwAffine c_rtc, double delay) {
        if (delay < 0.0) {
            throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
        }
        if (delay == 0.0) { // Point in the origin does not work with RTC
            makeHorizontal(c_rtc, Double.POSITIVE_INFINITY);
            return;
        }

        Segment segment0 = new Segment(0.0, 0.0, 0.0);
        Segment segment1 = new Segment(delay, Double.POSITIVE_INFINITY, 0.0);

        SegmentList segments = new SegmentList();
        segments.add(segment0);
        segments.add(segment1);

        c_rtc.initializeWithSegments(segments);
        c_rtc.is_delayed_infinite_burst = true;
    }

    private void makePeakRate(Curve_MPARTC_PwAffine c_rtc, double rate) {
        if (rate == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
        }
        if (rate == 0.0) {
            makeHorizontal(c_rtc, 0.0);
            return;
        }

        Segment segment = new Segment(0.0, 0.0, rate);
        c_rtc.initializeWithSegment(segment);
        // TODO Is it a RL with L=0 (in the PMOO's point of view)?
    }

    private void makeRateLatency(Curve_MPARTC_PwAffine c_rtc, double rate, double latency) {
        if (rate == Double.POSITIVE_INFINITY) {
            makeDelayedInfiniteBurst(c_rtc, latency);
            return;
        }
        if (rate == 0.0 || latency == Double.POSITIVE_INFINITY) {
            makeHorizontal(c_rtc, 0.0);
            return;
        }
        if (latency == 0.0) {
            makePeakRate(c_rtc, rate);
            return;
        }

        Segment segment0 = new Segment(0.0, 0.0, 0.0);
        Segment segment1 = new Segment(latency, 0.0, rate);

        SegmentList segments = new SegmentList();
        segments.add(segment0);
        segments.add(segment1);

        c_rtc.initializeWithSegments(segments);
        // TODO: Do decomposition for PMOO?
        // TODO: Remark: PMOO throws exception due to non linear increasing segments
        c_rtc.is_rate_latency = true;
    }

    private void makeTokenBucket(Curve_MPARTC_PwAffine c_rtc, double rate, double burst) {
        if (rate == Double.POSITIVE_INFINITY
                || burst == Double.POSITIVE_INFINITY) {
            makeDelayedInfiniteBurst(c_rtc, 0.0);
            return;
        }
        if (rate == 0.0) { // burst is finite
            makeHorizontal(c_rtc, burst);
            return;
        }
        if (burst == 0.0) {
            makePeakRate(c_rtc, rate);
            return;
        }

//    	Segment segment0 = new Segment( 0.0, 0.0, 0.0 ); // Point in the origin does not work with RTC
        Segment segment1 = new Segment(0.0, burst, rate);

        SegmentList segments = new SegmentList();
//    	segments.add( segment0 );
        segments.add(segment1);

        c_rtc.initializeWithSegments(segments);
        // TODO: Do decomposition for PMOO?
        c_rtc.is_token_bucket = true;
    }
}