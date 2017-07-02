package de.uni_kl.disco.curves.dnc;

import java.util.List;

import de.uni_kl.disco.curves.CurveFactoryInterface;
import de.uni_kl.disco.curves.CurvePwAffine;
import de.uni_kl.disco.curves.CurveUtils;
import de.uni_kl.disco.curves.LinearSegment;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

public class CurveFactory_DNC implements CurveFactoryInterface {
    protected static CurveFactory_DNC factory_object = new CurveFactory_DNC();

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public Curve_DNC createCurve(List<LinearSegment> segments) {
        Curve_DNC c_dnc = new Curve_DNC(segments.size());
        for (int i = 0; i < segments.size(); i++) {
            c_dnc.setSegment(i, segments.get(i));
        }
        c_dnc.beautify();
        return c_dnc;
    }

    public Curve_DNC createZeroCurve() {
        return new Curve_DNC(); // CurveDNC constructor's default behavior
    }

    public Curve_DNC createHorizontal(double y) {
        return createHorizontal(y);
    }

    /**
     * Creates a horizontal curve.
     *
     * @param y the y-intercept of the curve
     * @return a <code>Curve</code> instance
     */
    public Curve_DNC createHorizontal(Num y) {
        Curve_DNC c_dnc = new Curve_DNC();
        makeHorizontal(c_dnc, y);
        return c_dnc;
    }

//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public ServiceCurve_DNC createServiceCurve() {
        return new ServiceCurve_DNC();
    }

    public ServiceCurve_DNC createServiceCurve(int segment_count) {
        return new ServiceCurve_DNC(segment_count);
    }

    public ServiceCurve_DNC createServiceCurve(String service_curve_str) throws Exception {
        return new ServiceCurve_DNC(service_curve_str);
    }

    public ServiceCurve_DNC createServiceCurve(CurvePwAffine curve) {
        return new ServiceCurve_DNC(curve);
    }

    public ServiceCurve_DNC createZeroService() {
        return new ServiceCurve_DNC(); // ServiceCurveDNC constructor's default behavior
    }

    /**
     * Creates an infinite burst curve with zero delay.
     *
     * @return a <code>ServiceCurve</code> instance
     */
    public ServiceCurve_DNC createZeroDelayInfiniteBurst() {
        return createDelayedInfiniteBurst(NumFactory.createZero());
    }

    public ServiceCurve_DNC createDelayedInfiniteBurst(double delay) {
        return createDelayedInfiniteBurst(NumFactory.create(delay));
    }

    public ServiceCurve_DNC createDelayedInfiniteBurst(Num delay) {
        ServiceCurve_DNC sc_dnc = new ServiceCurve_DNC();
        makeDelayedInfiniteBurst(sc_dnc, delay);
        return sc_dnc;
    }

    public ServiceCurve_DNC createRateLatency(double rate, double latency) {
        return createRateLatency(NumFactory.create(rate), NumFactory.create(latency));
    }

    public ServiceCurve_DNC createRateLatency(Num rate, Num latency) {
        ServiceCurve_DNC sc_dnc = new ServiceCurve_DNC();
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
    public ArrivalCurve_DNC createArrivalCurve() {
        return new ArrivalCurve_DNC();
    }

    public ArrivalCurve_DNC createArrivalCurve(int segment_count) {
        return new ArrivalCurve_DNC(segment_count);
    }

    public ArrivalCurve_DNC createArrivalCurve(String arrival_curve_str) throws Exception {
        return new ArrivalCurve_DNC(arrival_curve_str);
    }

    public ArrivalCurve_DNC createArrivalCurve(CurvePwAffine curve) {
        return new ArrivalCurve_DNC(curve);
    }

    public ArrivalCurve_DNC createArrivalCurve(CurvePwAffine curve, boolean remove_latency) {
        return createArrivalCurve(CurveUtils.removeLatency(curve));
    }

    public ArrivalCurve_DNC createZeroArrivals() {
        return new ArrivalCurve_DNC(); // ArrivalCurveDNC constructor's default behavior
    }

    public ArrivalCurve_DNC createPeakArrivalRate(double rate) {
        return createPeakArrivalRate(NumFactory.create(rate));
    }

    public ArrivalCurve_DNC createPeakArrivalRate(Num rate) {
        ArrivalCurve_DNC ac_dnc = new ArrivalCurve_DNC();
        makePeakRate(ac_dnc, rate);
        return ac_dnc;
    }

    public ArrivalCurve_DNC createTokenBucket(double rate, double burst) {
        return createTokenBucket(NumFactory.create(rate), NumFactory.create(burst));
    }

    public ArrivalCurve_DNC createTokenBucket(Num rate, Num burst) {
        ArrivalCurve_DNC ac_dnc = new ArrivalCurve_DNC();
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
    public MaxServiceCurve_DNC createMaxServiceCurve() {
        return new MaxServiceCurve_DNC();
    }

    public MaxServiceCurve_DNC createMaxServiceCurve(int segment_count) {
        return new MaxServiceCurve_DNC(segment_count);
    }

    public MaxServiceCurve_DNC createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return new MaxServiceCurve_DNC(max_service_curve_str);
    }

    public MaxServiceCurve_DNC createMaxServiceCurve(CurvePwAffine curve) {
        return new MaxServiceCurve_DNC(curve);
    }

    public MaxServiceCurve_DNC createInfiniteMaxService() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurve_DNC createZeroDelayInfiniteBurstMSC() {
        return createDelayedInfiniteBurstMSC(NumFactory.createZero());
    }

    public MaxServiceCurve_DNC createDelayedInfiniteBurstMSC(double delay) {
        return createDelayedInfiniteBurstMSC(NumFactory.create(delay));
    }

    public MaxServiceCurve_DNC createDelayedInfiniteBurstMSC(Num delay) {
        MaxServiceCurve_DNC msc_dnc = new MaxServiceCurve_DNC();
        makeDelayedInfiniteBurst(msc_dnc, delay);
        return msc_dnc;
    }

    public MaxServiceCurve_DNC createRateLatencyMSC(double rate, double latency) {
        return createRateLatencyMSC(NumFactory.create(rate), NumFactory.create(latency));
    }

    public MaxServiceCurve_DNC createRateLatencyMSC(Num rate, Num latency) {
        MaxServiceCurve_DNC msc_dnc = new MaxServiceCurve_DNC();
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
    private void makeHorizontal(Curve_DNC c_dnc, Num y) {
        LinearSegment_DNC segment = new LinearSegment_DNC(NumFactory.createZero(), y, NumFactory.createZero(), false);
        c_dnc.setSegments(new LinearSegment_DNC[]{segment});
    }

    private void makeDelayedInfiniteBurst(Curve_DNC c_dnc, Num delay) {
        if (delay.ltZero()) {
            throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
        }

        LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

        segments[0] = new LinearSegment_DNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegment_DNC(
                delay,
                NumFactory.createPositiveInfinity(),
                NumFactory.createZero(),
                true);

        c_dnc.setSegments(segments);
        c_dnc.is_delayed_infinite_burst = true;
    }

    private void makePeakRate(Curve_DNC c_dnc, Num rate) {
        if (rate.equals(NumFactory.getPositiveInfinity())) {
            throw new IllegalArgumentException("Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
        }
        if (rate.eqZero()) {
            makeHorizontal(c_dnc, NumFactory.createZero());
            return;
        }

        LinearSegment_DNC[] segments = new LinearSegment_DNC[1];

        segments[0] = new LinearSegment_DNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                rate,
                false);

        c_dnc.setSegments(segments);
        // TODO Is it a RL with L=0 (in the PMOO's point of view)
    }

    private void makeRateLatency(Curve_DNC c_dnc, Num rate, Num latency) {
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

        LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

        segments[0] = new LinearSegment_DNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegment_DNC(
                latency,
                NumFactory.createZero(),
                rate,
                true);

        c_dnc.setSegments(segments);
        // TODO: Do decomposition for PMOO?
        c_dnc.is_rate_latency = true;
    }

    private void makeTokenBucket(Curve_DNC c_dnc, Num rate, Num burst) {
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

        LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

        segments[0] = new LinearSegment_DNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        segments[1] = new LinearSegment_DNC(
                NumFactory.createZero(),
                burst,
                rate,
                true);

        c_dnc.setSegments(segments);
        // TODO: Do decomposition for PMOO?
        c_dnc.is_token_bucket = true;
    }
}