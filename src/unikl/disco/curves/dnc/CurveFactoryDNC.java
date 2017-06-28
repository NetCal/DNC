package unikl.disco.curves.dnc;

import unikl.disco.curves.CurveFactoryInterface;
import unikl.disco.curves.CurveUltAffine;
import unikl.disco.curves.CurveUtils;
import unikl.disco.curves.LinearSegment;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

import java.util.List;

public class CurveFactoryDNC implements CurveFactoryInterface {
    protected static CurveFactoryDNC factory_object = new CurveFactoryDNC();

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public CurveDNC createCurve(List<LinearSegment> segments) {
        CurveDNC c_dnc = new CurveDNC(segments.size());
        for (int i = 0; i < segments.size(); i++) {
            c_dnc.setSegment(i, segments.get(i));
        }
        c_dnc.beautify();
        return c_dnc;
    }

    public CurveDNC createZeroCurve() {
        return new CurveDNC(); // CurveDNC constructor's default behavior
    }

    public CurveDNC createHorizontal(double y) {
        return createHorizontal(y);
    }

    /**
     * Creates a horizontal curve.
     *
     * @param y the y-intercept of the curve
     * @return a <code>Curve</code> instance
     */
    public CurveDNC createHorizontal(Num y) {
        CurveDNC c_dnc = new CurveDNC();
        makeHorizontal(c_dnc, y);
        return c_dnc;
    }

//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ServiceCurveDNC createServiceCurve() {
        return new ServiceCurveDNC();
    }

    public ServiceCurveDNC createServiceCurve(int segment_count) {
        return new ServiceCurveDNC(segment_count);
    }

    public ServiceCurveDNC createServiceCurve(String service_curve_str) throws Exception {
        return new ServiceCurveDNC(service_curve_str);
    }

    public ServiceCurveDNC createServiceCurve(CurveUltAffine curve) {
        return new ServiceCurveDNC(curve);
    }

    public ServiceCurveDNC createZeroService() {
        return new ServiceCurveDNC(); // ServiceCurveDNC constructor's default behavior
    }

    /**
     * Creates an infinite burst curve with zero delay.
     *
     * @return a <code>ServiceCurve</code> instance
     */
    public ServiceCurveDNC createZeroDelayInfiniteBurst() {
        return createDelayedInfiniteBurst(NumFactory.createZero());
    }

    public ServiceCurveDNC createDelayedInfiniteBurst(double delay) {
        return createDelayedInfiniteBurst(NumFactory.create(delay));
    }

    public ServiceCurveDNC createDelayedInfiniteBurst(Num delay) {
        ServiceCurveDNC sc_dnc = new ServiceCurveDNC();
        makeDelayedInfiniteBurst(sc_dnc, delay);
        return sc_dnc;
    }

    public ServiceCurveDNC createRateLatency(double rate, double latency) {
        return createRateLatency(NumFactory.create(rate), NumFactory.create(latency));
    }

    public ServiceCurveDNC createRateLatency(Num rate, Num latency) {
        ServiceCurveDNC sc_dnc = new ServiceCurveDNC();
        makeRateLatency(sc_dnc, rate, latency);
        return sc_dnc;
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    public ServiceCurveDNC createServiceCurve(SegmentList aperSegments) {
//    	System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//    	return null;
//	}
//    
//	public ServiceCurveDNC createServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveDNC createServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveDNC createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveDNC createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public ServiceCurveDNC createServiceCurve(SegmentList aperSegments, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ArrivalCurveDNC createArrivalCurve() {
        return new ArrivalCurveDNC();
    }

    public ArrivalCurveDNC createArrivalCurve(int segment_count) {
        return new ArrivalCurveDNC(segment_count);
    }

    public ArrivalCurveDNC createArrivalCurve(String arrival_curve_str) throws Exception {
        return new ArrivalCurveDNC(arrival_curve_str);
    }

    public ArrivalCurveDNC createArrivalCurve(CurveUltAffine curve) {
        return new ArrivalCurveDNC(curve);
    }

    public ArrivalCurveDNC createArrivalCurve(CurveUltAffine curve, boolean remove_latency) {
        return createArrivalCurve(CurveUtils.removeLatency(curve));
    }

    public ArrivalCurveDNC createZeroArrivals() {
        return new ArrivalCurveDNC(); // ArrivalCurveDNC constructor's default behavior
    }

    public ArrivalCurveDNC createPeakArrivalRate(double rate) {
        return createPeakArrivalRate(NumFactory.create(rate));
    }

    public ArrivalCurveDNC createPeakArrivalRate(Num rate) {
        ArrivalCurveDNC ac_dnc = new ArrivalCurveDNC();
        makePeakRate(ac_dnc, rate);
        return ac_dnc;
    }

    public ArrivalCurveDNC createTokenBucket(double rate, double burst) {
        return createTokenBucket(NumFactory.create(rate), NumFactory.create(burst));
    }

    public ArrivalCurveDNC createTokenBucket(Num rate, Num burst) {
        ArrivalCurveDNC ac_dnc = new ArrivalCurveDNC();
        makeTokenBucket(ac_dnc, rate, burst);
        return ac_dnc;
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public ArrivalCurveDNC createArrivalCurve(SegmentList aperSegments) {
//    	System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//    	return null;
//	}
//
//	public ArrivalCurveDNC createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public ArrivalCurveDNC createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
// 	}
//
//	public ArrivalCurveDNC createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public ArrivalCurveDNC createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
// 	public ArrivalCurveDNC createArrivalCurve(SegmentList aperSegments, java.lang.String name) {
// 		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
// 		return null;
//	}

//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public MaxServiceCurveDNC createMaxServiceCurve() {
        return new MaxServiceCurveDNC();
    }

    public MaxServiceCurveDNC createMaxServiceCurve(int segment_count) {
        return new MaxServiceCurveDNC(segment_count);
    }

    public MaxServiceCurveDNC createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return new MaxServiceCurveDNC(max_service_curve_str);
    }

    public MaxServiceCurveDNC createMaxServiceCurve(CurveUltAffine curve) {
        return new MaxServiceCurveDNC(curve);
    }

    public MaxServiceCurveDNC createInfiniteMaxService() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurveDNC createZeroDelayInfiniteBurstMSC() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurveDNC createDelayedInfiniteBurstMSC(double delay) {
        return createDelayedInfiniteBurstMSC(NumFactory.create(delay));
    }

    public MaxServiceCurveDNC createDelayedInfiniteBurstMSC(Num delay) {
        MaxServiceCurveDNC msc_dnc = new MaxServiceCurveDNC();
        makeDelayedInfiniteBurst(msc_dnc, delay);
        return msc_dnc;
    }

    public MaxServiceCurveDNC createRateLatencyMSC(double rate, double latency) {
        return createRateLatencyMSC(NumFactory.create(rate), NumFactory.create(latency));
    }

    public MaxServiceCurveDNC createRateLatencyMSC(Num rate, Num latency) {
        MaxServiceCurveDNC msc_dnc = new MaxServiceCurveDNC();
        makeRateLatency(msc_dnc, rate, latency);
        return msc_dnc;
    }

    // TODO Phase II
    // TODO Throw exceptions instead of returning null
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList aperSegments) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//	
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}
//
//	public MaxServiceCurveDNC createMaxServiceCurve(SegmentList aperSegments, java.lang.String name) {
//		System.out.println("Constructor with SegmentList not supported for DiscoDNC implementation.");
//		return null;
//	}

    //--------------------------------------------------------------------------------------------------------------
// Curve assembly
//--------------------------------------------------------------------------------------------------------------
    private void makeHorizontal(CurveDNC c_dnc, Num y) {
        LinearSegmentDNC segment = new LinearSegmentDNC(NumFactory.createZero(), y, NumFactory.createZero(), false);
        c_dnc.setSegments(new LinearSegmentDNC[]{segment});
    }

    private void makeDelayedInfiniteBurst(CurveDNC c_dnc, Num delay) {
        if (delay.ltZero()) {
            throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
        }

        LinearSegmentDNC[] segments = new LinearSegmentDNC[2];

        segments[0] = new LinearSegmentDNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegmentDNC(
                delay,
                NumFactory.createPositiveInfinity(),
                NumFactory.createZero(),
                true);

        c_dnc.setSegments(segments);
        c_dnc.is_delayed_infinite_burst = true;
    }

    private void makePeakRate(CurveDNC c_dnc, Num rate) {
        if (rate.equals(NumFactory.getPositiveInfinity())) {
            throw new IllegalArgumentException("Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
        }
        if (rate.eqZero()) {
            makeHorizontal(c_dnc, NumFactory.createZero());
            return;
        }

        LinearSegmentDNC[] segments = new LinearSegmentDNC[1];

        segments[0] = new LinearSegmentDNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                rate,
                false);

        c_dnc.setSegments(segments);
        // TODO Is it a RL with L=0 (in the PMOO's point of view)
    }

    private void makeRateLatency(CurveDNC c_dnc, Num rate, Num latency) {
        if (rate.equals(NumFactory.getPositiveInfinity())) {
            makeDelayedInfiniteBurst(c_dnc, latency);
            return;
        }
        if (rate.eqZero() || latency.equals(NumFactory.getPositiveInfinity())) {
            makeHorizontal(c_dnc, NumFactory.createZero());
            return;
        }
        if (latency.leqZero()) {
            makePeakRate(c_dnc, rate);
            return;
        }

        LinearSegmentDNC[] segments = new LinearSegmentDNC[2];

        segments[0] = new LinearSegmentDNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegmentDNC(
                latency,
                NumFactory.createZero(),
                rate,
                true);

        c_dnc.setSegments(segments);
        // TODO: Do decomposition for PMOO?
        c_dnc.is_rate_latency = true;
    }

    private void makeTokenBucket(CurveDNC c_dnc, Num rate, Num burst) {
        if (rate.equals(NumFactory.getPositiveInfinity())
                || burst.equals(NumFactory.getPositiveInfinity())) {
            makeDelayedInfiniteBurst(c_dnc, NumFactory.createZero());
            return;
        }
        if (rate.eqZero()) { // burst is finite
            makeHorizontal(c_dnc, burst);
            return;
        }
        if (burst.eqZero()) {
            makePeakRate(c_dnc, rate);
            return;
        }

        LinearSegmentDNC[] segments = new LinearSegmentDNC[2];

        segments[0] = new LinearSegmentDNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegmentDNC(
                NumFactory.createZero(),
                burst,
                rate,
                true);

        c_dnc.setSegments(segments);
        // TODO: Do decomposition for PMOO?
        c_dnc.is_token_bucket = true;
    }
}