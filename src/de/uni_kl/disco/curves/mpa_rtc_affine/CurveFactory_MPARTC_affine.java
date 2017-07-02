package de.uni_kl.disco.curves.mpa_rtc_affine;

import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurveFactoryInterface;
import de.uni_kl.disco.curves.CurveUltAffine;
import de.uni_kl.disco.curves.CurveUtils;
import de.uni_kl.disco.curves.LinearSegment;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

import java.util.List;

//TODO @Steffen: what do you mean with "Given any Num, RTC converts it to double and vice versa"?

//TODO Currently, arrival curves are not enforced to be 0 in the origin
//		because the RTC toolbox does not allow for such spots as they occur in token bucket constrained arrivals. 
public class CurveFactory_MPARTC_affine implements CurveFactoryInterface {
    protected static CurveFactory_MPARTC_affine factory_object = new CurveFactory_MPARTC_affine();

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public Curve_MPARTC_affine createCurve(List<LinearSegment> segments) {
        Curve_MPARTC_affine msc_rtc = new Curve_MPARTC_affine();

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

    public Curve_MPARTC_affine createZeroCurve() {
        return new Curve_MPARTC_affine(); // CurveRTC constructor's default behavior
    }

    public Curve_MPARTC_affine createHorizontal(double y) {
        Curve_MPARTC_affine c_rtc = new Curve_MPARTC_affine();
        makeHorizontal(c_rtc, y);
        return c_rtc;
    }

    /**
     * Creates a horizontal curve.
     *
     * @param y the y-intercept of the curve
     * @return a <code>Curve</code> instance
     */
    public Curve_MPARTC_affine createHorizontal(Num y) {
        return createHorizontal(y.doubleValue());
    }

//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ServiceCurve_MPARTC_affine createServiceCurve() {
        return new ServiceCurve_MPARTC_affine();
    }

    public ServiceCurve_MPARTC_affine createServiceCurve(int segment_count) {
        return new ServiceCurve_MPARTC_affine(segment_count);
    }

    public ServiceCurve_MPARTC_affine createServiceCurve(String service_curve_str) throws Exception {
        return new ServiceCurve_MPARTC_affine(service_curve_str);
    }

    public ServiceCurve_MPARTC_affine createServiceCurve(CurveUltAffine curve) {
        return new ServiceCurve_MPARTC_affine(curve);
    }

    public ServiceCurve_MPARTC_affine createZeroService() {
        return new ServiceCurve_MPARTC_affine(); // ServiceCurveRTC constructor's default behavior
    }

    /**
     * Creates an infinite burst curve with zero delay.
     *
     * @return a <code>ServiceCurve</code> instance
     */
    public ServiceCurve_MPARTC_affine createZeroDelayInfiniteBurst() {
        ServiceCurve_MPARTC_affine sc_rtc = new ServiceCurve_MPARTC_affine();
        makeHorizontal(sc_rtc, Double.POSITIVE_INFINITY);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_affine createDelayedInfiniteBurst(double delay) {
        ServiceCurve_MPARTC_affine sc_rtc = new ServiceCurve_MPARTC_affine();
        makeDelayedInfiniteBurst(sc_rtc, delay);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_affine createDelayedInfiniteBurst(Num delay) {
        return createDelayedInfiniteBurst(delay.doubleValue());
    }

    public ServiceCurve_MPARTC_affine createRateLatency(double rate, double latency) {
        ServiceCurve_MPARTC_affine sc_rtc = new ServiceCurve_MPARTC_affine();
        makeRateLatency(sc_rtc, rate, latency);
        return sc_rtc;
    }

    public ServiceCurve_MPARTC_affine createRateLatency(Num rate, Num latency) {
        return createRateLatency(rate.doubleValue(), latency.doubleValue());
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    public ServiceCurveRTC createServiceCurve(SegmentList aperSegments) {
//    	System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//    	return null;
//	}
//    
//	public ServiceCurveRTC createServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveRTC createServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveRTC createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveRTC createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveRTC createServiceCurve(SegmentList aperSegments, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ArrivalCurve_MPARTC_affine createArrivalCurve() {
        return new ArrivalCurve_MPARTC_affine();
    }

    public ArrivalCurve_MPARTC_affine createArrivalCurve(int segment_count) {
        return new ArrivalCurve_MPARTC_affine(segment_count);
    }

    public ArrivalCurve_MPARTC_affine createArrivalCurve(String arrival_curve_str) throws Exception {
        return new ArrivalCurve_MPARTC_affine(arrival_curve_str);
    }

    public ArrivalCurve_MPARTC_affine createArrivalCurve(CurveUltAffine curve) {
        return new ArrivalCurve_MPARTC_affine(curve);
    }

    public ArrivalCurve_MPARTC_affine createArrivalCurve(CurveUltAffine curve, boolean remove_latency) {
        return createArrivalCurve(CurveUtils.removeLatency(curve));
    }

    public ArrivalCurve_MPARTC_affine createZeroArrivals() {
        return new ArrivalCurve_MPARTC_affine(); // ArrivalCurveRTC constructor's default behavior
    }

    public ArrivalCurve_MPARTC_affine createPeakArrivalRate(double rate) {
        ArrivalCurve_MPARTC_affine ac_rtc = new ArrivalCurve_MPARTC_affine();
        makePeakRate(ac_rtc, rate);
        return ac_rtc;
    }

    public ArrivalCurve_MPARTC_affine createPeakArrivalRate(Num rate) {
        return createPeakArrivalRate(rate.doubleValue());
    }

    public ArrivalCurve_MPARTC_affine createTokenBucket(double rate, double burst) {
        ArrivalCurve_MPARTC_affine ac_rtc = new ArrivalCurve_MPARTC_affine();
        makeTokenBucket(ac_rtc, rate, burst);
        return ac_rtc;
    }

    public ArrivalCurve_MPARTC_affine createTokenBucket(Num rate, Num burst) {
        return createTokenBucket(rate.doubleValue(), burst.doubleValue());
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public ArrivalCurveRTC createArrivalCurve(SegmentList aperSegments) {
//    	System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//    	return null;
//	}
//
//	public ArrivalCurveRTC createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public ArrivalCurveRTC createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
// 	}
//
//	public ArrivalCurveRTC createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public ArrivalCurveRTC createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
// 	public ArrivalCurveRTC createArrivalCurve(SegmentList aperSegments, java.lang.String name) {
// 		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
// 		return null;
//	}

//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public MaxServiceCurve_MPARTC_affine createMaxServiceCurve() {
        return new MaxServiceCurve_MPARTC_affine();
    }

    public MaxServiceCurve_MPARTC_affine createMaxServiceCurve(int segment_count) {
        return new MaxServiceCurve_MPARTC_affine(segment_count);
    }

    public MaxServiceCurve_MPARTC_affine createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return new MaxServiceCurve_MPARTC_affine(max_service_curve_str);
    }

    public MaxServiceCurve_MPARTC_affine createMaxServiceCurve(CurveUltAffine curve) {
        return new MaxServiceCurve_MPARTC_affine(curve);
    }

    public MaxServiceCurve_MPARTC_affine createInfiniteMaxService() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurve_MPARTC_affine createZeroDelayInfiniteBurstMSC() {
        MaxServiceCurve_MPARTC_affine msc_rtc = new MaxServiceCurve_MPARTC_affine();
        makeHorizontal(msc_rtc, Double.POSITIVE_INFINITY);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_affine createDelayedInfiniteBurstMSC(double delay) {
        MaxServiceCurve_MPARTC_affine msc_rtc = new MaxServiceCurve_MPARTC_affine();
        makeDelayedInfiniteBurst(msc_rtc, delay);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_affine createDelayedInfiniteBurstMSC(Num delay) {
        return createDelayedInfiniteBurstMSC(delay.doubleValue());
    }

    public MaxServiceCurve_MPARTC_affine createRateLatencyMSC(double rate, double latency) {
        MaxServiceCurve_MPARTC_affine msc_rtc = new MaxServiceCurve_MPARTC_affine();
        makeRateLatency(msc_rtc, rate, latency);
        return msc_rtc;
    }

    public MaxServiceCurve_MPARTC_affine createRateLatencyMSC(Num rate, Num latency) {
        return createRateLatencyMSC(rate.doubleValue(), latency.doubleValue());
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList aperSegments) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveRTC createMaxServiceCurve(SegmentList aperSegments, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}

    //--------------------------------------------------------------------------------------------------------------
// Curve assembly
//--------------------------------------------------------------------------------------------------------------
    private void makeHorizontal(Curve_MPARTC_affine c_rtc, double y) {
        c_rtc.initializeWithSegment(new Segment(0.0, y, 0.0));

        if (y == Double.POSITIVE_INFINITY) {
            c_rtc.is_delayed_infinite_burst = true;
        }
    }

    private void makeDelayedInfiniteBurst(Curve_MPARTC_affine c_rtc, double delay) {
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

    private void makePeakRate(Curve_MPARTC_affine c_rtc, double rate) {
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

    private void makeRateLatency(Curve_MPARTC_affine c_rtc, double rate, double latency) {
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

    private void makeTokenBucket(Curve_MPARTC_affine c_rtc, double rate, double burst) {
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