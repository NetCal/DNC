package unikl.disco.curves.rtcUltAffine;

import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import unikl.disco.curves.CurveFactoryInterface;
import unikl.disco.curves.CurveUltAffine;
import unikl.disco.curves.CurveUtils;
import unikl.disco.curves.LinearSegment;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

import java.util.List;

//TODO @Steffen: what do you mean with "Given any Num, RTC converts it to double and vice versa"?

//TODO Currently, arrival curves are not enforced to be 0 in the origin
//		because the RTC toolbox does not allow for such spots as they occur in token bucket constrained arrivals. 
public class CurveFactoryRTCultAffine implements CurveFactoryInterface {
    protected static CurveFactoryRTCultAffine factory_object = new CurveFactoryRTCultAffine();

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public CurveRTCultAffine createCurve(List<LinearSegment> segments) {
        CurveRTCultAffine msc_rtc = new CurveRTCultAffine();

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

    public CurveRTCultAffine createZeroCurve() {
        return new CurveRTCultAffine(); // CurveRTC constructor's default behavior
    }

    public CurveRTCultAffine createHorizontal(double y) {
        CurveRTCultAffine c_rtc = new CurveRTCultAffine();
        makeHorizontal(c_rtc, y);
        return c_rtc;
    }

    /**
     * Creates a horizontal curve.
     *
     * @param y the y-intercept of the curve
     * @return a <code>Curve</code> instance
     */
    public CurveRTCultAffine createHorizontal(Num y) {
        return createHorizontal(y.doubleValue());
    }

//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ServiceCurveRTCultAffine createServiceCurve() {
        return new ServiceCurveRTCultAffine();
    }

    public ServiceCurveRTCultAffine createServiceCurve(int segment_count) {
        return new ServiceCurveRTCultAffine(segment_count);
    }

    public ServiceCurveRTCultAffine createServiceCurve(String service_curve_str) throws Exception {
        return new ServiceCurveRTCultAffine(service_curve_str);
    }

    public ServiceCurveRTCultAffine createServiceCurve(CurveUltAffine curve) {
        return new ServiceCurveRTCultAffine(curve);
    }

    public ServiceCurveRTCultAffine createZeroService() {
        return new ServiceCurveRTCultAffine(); // ServiceCurveRTC constructor's default behavior
    }

    /**
     * Creates an infinite burst curve with zero delay.
     *
     * @return a <code>ServiceCurve</code> instance
     */
    public ServiceCurveRTCultAffine createZeroDelayInfiniteBurst() {
        ServiceCurveRTCultAffine sc_rtc = new ServiceCurveRTCultAffine();
        makeHorizontal(sc_rtc, Double.POSITIVE_INFINITY);
        return sc_rtc;
    }

    public ServiceCurveRTCultAffine createDelayedInfiniteBurst(double delay) {
        ServiceCurveRTCultAffine sc_rtc = new ServiceCurveRTCultAffine();
        makeDelayedInfiniteBurst(sc_rtc, delay);
        return sc_rtc;
    }

    public ServiceCurveRTCultAffine createDelayedInfiniteBurst(Num delay) {
        return createDelayedInfiniteBurst(delay.doubleValue());
    }

    public ServiceCurveRTCultAffine createRateLatency(double rate, double latency) {
        ServiceCurveRTCultAffine sc_rtc = new ServiceCurveRTCultAffine();
        makeRateLatency(sc_rtc, rate, latency);
        return sc_rtc;
    }

    public ServiceCurveRTCultAffine createRateLatency(Num rate, Num latency) {
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
    public ArrivalCurveRTCultAffine createArrivalCurve() {
        return new ArrivalCurveRTCultAffine();
    }

    public ArrivalCurveRTCultAffine createArrivalCurve(int segment_count) {
        return new ArrivalCurveRTCultAffine(segment_count);
    }

    public ArrivalCurveRTCultAffine createArrivalCurve(String arrival_curve_str) throws Exception {
        return new ArrivalCurveRTCultAffine(arrival_curve_str);
    }

    public ArrivalCurveRTCultAffine createArrivalCurve(CurveUltAffine curve) {
        return new ArrivalCurveRTCultAffine(curve);
    }

    public ArrivalCurveRTCultAffine createArrivalCurve(CurveUltAffine curve, boolean remove_latency) {
        return createArrivalCurve(CurveUtils.removeLatency(curve));
    }

    public ArrivalCurveRTCultAffine createZeroArrivals() {
        return new ArrivalCurveRTCultAffine(); // ArrivalCurveRTC constructor's default behavior
    }

    public ArrivalCurveRTCultAffine createPeakArrivalRate(double rate) {
        ArrivalCurveRTCultAffine ac_rtc = new ArrivalCurveRTCultAffine();
        makePeakRate(ac_rtc, rate);
        return ac_rtc;
    }

    public ArrivalCurveRTCultAffine createPeakArrivalRate(Num rate) {
        return createPeakArrivalRate(rate.doubleValue());
    }

    public ArrivalCurveRTCultAffine createTokenBucket(double rate, double burst) {
        ArrivalCurveRTCultAffine ac_rtc = new ArrivalCurveRTCultAffine();
        makeTokenBucket(ac_rtc, rate, burst);
        return ac_rtc;
    }

    public ArrivalCurveRTCultAffine createTokenBucket(Num rate, Num burst) {
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
    public MaxServiceCurveRTCultAffine createMaxServiceCurve() {
        return new MaxServiceCurveRTCultAffine();
    }

    public MaxServiceCurveRTCultAffine createMaxServiceCurve(int segment_count) {
        return new MaxServiceCurveRTCultAffine(segment_count);
    }

    public MaxServiceCurveRTCultAffine createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return new MaxServiceCurveRTCultAffine(max_service_curve_str);
    }

    public MaxServiceCurveRTCultAffine createMaxServiceCurve(CurveUltAffine curve) {
        return new MaxServiceCurveRTCultAffine(curve);
    }

    public MaxServiceCurveRTCultAffine createInfiniteMaxService() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurveRTCultAffine createZeroDelayInfiniteBurstMSC() {
        MaxServiceCurveRTCultAffine msc_rtc = new MaxServiceCurveRTCultAffine();
        makeHorizontal(msc_rtc, Double.POSITIVE_INFINITY);
        return msc_rtc;
    }

    public MaxServiceCurveRTCultAffine createDelayedInfiniteBurstMSC(double delay) {
        MaxServiceCurveRTCultAffine msc_rtc = new MaxServiceCurveRTCultAffine();
        makeDelayedInfiniteBurst(msc_rtc, delay);
        return msc_rtc;
    }

    public MaxServiceCurveRTCultAffine createDelayedInfiniteBurstMSC(Num delay) {
        return createDelayedInfiniteBurstMSC(delay.doubleValue());
    }

    public MaxServiceCurveRTCultAffine createRateLatencyMSC(double rate, double latency) {
        MaxServiceCurveRTCultAffine msc_rtc = new MaxServiceCurveRTCultAffine();
        makeRateLatency(msc_rtc, rate, latency);
        return msc_rtc;
    }

    public MaxServiceCurveRTCultAffine createRateLatencyMSC(Num rate, Num latency) {
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
    private void makeHorizontal(CurveRTCultAffine c_rtc, double y) {
        c_rtc.initializeWithSegment(new Segment(0.0, y, 0.0));

        if (y == Double.POSITIVE_INFINITY) {
            c_rtc.is_delayed_infinite_burst = true;
        }
    }

    private void makeDelayedInfiniteBurst(CurveRTCultAffine c_rtc, double delay) {
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

    private void makePeakRate(CurveRTCultAffine c_rtc, double rate) {
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

    private void makeRateLatency(CurveRTCultAffine c_rtc, double rate, double latency) {
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

    private void makeTokenBucket(CurveRTCultAffine c_rtc, double rate, double burst) {
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