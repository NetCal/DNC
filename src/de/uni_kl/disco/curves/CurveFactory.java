package de.uni_kl.disco.curves;

import java.util.List;

import de.uni_kl.disco.curves.dnc.CurveFactory_DNC;
import de.uni_kl.disco.curves.mpa_rtc_affine.CurveFactory_MPARTC_affine;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.nc.CalculatorConfig.CurveClass;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

/**
 * Static access to the methods defined in the CurveFactoryInterface.
 *
 * @author sbondorf
 */
public class CurveFactory {
    private static CurveFactoryInterface dnc_factory = new CurveFactory_DNC();
    private static CurveFactoryInterface rtc_factory = new CurveFactory_MPARTC_affine();

    private static CurveFactoryInterface factory = getCurveFactory();

    private static CurveFactoryInterface getCurveFactory() {
        switch (CalculatorConfig.getCurveClass()) {
            case RTC:
                return rtc_factory;
            case DNC:
            default:
                return dnc_factory;
        }
    }

    public static void setCurveClass(CurveClass curve_class) {
        switch (curve_class) {
            case RTC:
                factory = rtc_factory;
                return;
            case DNC:
            default:
                factory = dnc_factory;
                return;
        }
    }

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public static CurveMultAffine createCurve(List<LinearSegment> segments) {
        return factory.createCurve(segments);
    }

    public static CurveMultAffine createZeroCurve() {
        return factory.createZeroCurve();
    }

    public static CurveMultAffine createHorizontal(double y) {
        return factory.createHorizontal(y);
    }

    public static CurveMultAffine createHorizontal(Num y) {
        return factory.createHorizontal(y);
    }

//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public static ServiceCurve createServiceCurve() {
        return factory.createServiceCurve();
    }

    public static ServiceCurve createServiceCurve(int segment_count) {
        return factory.createServiceCurve(segment_count);
    }

    public static ServiceCurve createServiceCurve(String service_curve_str) throws Exception {
        return factory.createServiceCurve(service_curve_str);
    }

    public static ServiceCurve createServiceCurve(CurveMultAffine curve) {
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

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    public static ServiceCurve createServiceCurve(SegmentList aperSegments) {
//		return factory.createServiceCurve( aperSegments );
//	}
//
//    public static ServiceCurve createServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//		return factory.createServiceCurve( perSegments, py0, period, pdy );
//	}
//
//    public static ServiceCurve createServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//		return factory.createServiceCurve( perSegments, py0, period, pdy, name );
//	}
//
//    public static ServiceCurve createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//		return factory.createServiceCurve( aperSegments, perSegments, px0, py0, period, pdy );
//	}
//
//    public static ServiceCurve createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//		return factory.createServiceCurve( aperSegments, perSegments, px0, py0, period, pdy, name );
//	}
//
//    public static ServiceCurve createServiceCurve(SegmentList aperSegments, java.lang.String name) {
//		return factory.createServiceCurve( aperSegments, name );
//	}


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public static ArrivalCurve createArrivalCurve() {
        return factory.createArrivalCurve();
    }

    public static ArrivalCurve createArrivalCurve(int segment_count) {
        return factory.createArrivalCurve(segment_count);
    }

    public static ArrivalCurve createArrivalCurve(String arrival_curve_str) throws Exception {
        return factory.createArrivalCurve(arrival_curve_str);
    }

    public static ArrivalCurve createArrivalCurve(CurveMultAffine curve) {
        return factory.createArrivalCurve(curve);
    }

    public static ArrivalCurve createArrivalCurve(CurveMultAffine curve, boolean remove_latency) {
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

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public static ArrivalCurve createArrivalCurve(SegmentList aperSegments) {
//    	return factory.createArrivalCurve( aperSegments );
//    }
//
//	public static ArrivalCurve createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy) {
//    	return factory.createArrivalCurve( perSegments, py0, period, pdy );
//    }
//
//	public static ArrivalCurve createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//    	return factory.createArrivalCurve( perSegments, py0, period, pdy, name );
//    }
//
//	public static ArrivalCurve createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//    	return factory.createArrivalCurve( aperSegments, perSegments, px0, py0, period, pdy );
//    }
//
//	public static ArrivalCurve createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//    	return factory.createArrivalCurve( aperSegments, perSegments, px0, py0, period, pdy, name );
//    }
//    
//	public static ArrivalCurve createArrivalCurve(SegmentList aperSegments, java.lang.String name) {
//    	return factory.createArrivalCurve( aperSegments, name );
//    }


//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    public static MaxServiceCurve createMaxServiceCurve() {
        return factory.createMaxServiceCurve();
    }

    public static MaxServiceCurve createMaxServiceCurve(int segment_count) {
        return factory.createMaxServiceCurve(segment_count);
    }

    public static MaxServiceCurve createMaxServiceCurve(String max_service_curve_str) throws Exception {
        return factory.createMaxServiceCurve(max_service_curve_str);
    }

    public static MaxServiceCurve createMaxServiceCurve(CurveMultAffine curve) {
        return factory.createMaxServiceCurve(curve);
    }

    public static MaxServiceCurve createInfiniteMaxService() {
        return factory.createInfiniteMaxService();
    }

    public static MaxServiceCurve createZeroDelayInfiniteBurstMSC() {
        return factory.createZeroDelayInfiniteBurstMSC();
    }

    public static MaxServiceCurve createDelayedInfiniteBurstMSC(double delay) {
        checkMSClatency(NumFactory.create(delay));
        return factory.createDelayedInfiniteBurstMSC(delay);
    }

    public static MaxServiceCurve createDelayedInfiniteBurstMSC(Num delay) {
        checkMSClatency(delay);
        return factory.createDelayedInfiniteBurstMSC(delay);
    }

    public static MaxServiceCurve createRateLatencyMSC(double rate, double latency) {
        checkMSCrate(NumFactory.create(rate));
        checkMSClatency(NumFactory.create(latency));
        return factory.createRateLatencyMSC(rate, latency);
    }

    public static MaxServiceCurve createRateLatencyMSC(Num rate, Num latency) {
        checkMSCrate(rate);
        checkMSClatency(latency);
        return factory.createRateLatencyMSC(rate, latency);
    }

    private static void checkMSClatency(Num latency) {
        if (latency.equals(NumFactory.getPositiveInfinity())) {
            throw new IllegalArgumentException("Maximum service curve cannot be zero.");
        }
    }

    private static void checkMSCrate(Num rate) {
        if (rate.eqZero()) {
            throw new IllegalArgumentException("Maximum service curve cannot be zero.");
        }
    }

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments) {
//    	return factory.createMaxServiceCurve();
//    }
//
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy) {
//    	return factory.createMaxServiceCurve();
//    }
//
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
//    	return factory.createMaxServiceCurve();
//    }
//
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
//    	return factory.createMaxServiceCurve();
//    }
//
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name) {
//    	return factory.createMaxServiceCurve();
//    }
//
//	public static MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, java.lang.String name) {
//    	return factory.createMaxServiceCurve();
//    }
}