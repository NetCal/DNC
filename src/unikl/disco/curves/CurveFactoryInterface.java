package unikl.disco.curves;

import unikl.disco.numbers.Num;

import java.util.List;

//TODO Regarding Phase II, extension to more general curves with a periodic part:
//		We need to define out own interfaces for SegmentList etc.
//		Otherwise, we need to have the RTC toolbox jar in our workspace although we might not use RTC at all.
public interface CurveFactoryInterface {

//--------------------------------------------------------------------------------------------------------------
// Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    CurveUltAffine createCurve(List<LinearSegment> segments);

    CurveUltAffine createZeroCurve();

    CurveUltAffine createHorizontal(double y);

    CurveUltAffine createHorizontal(Num y);


//--------------------------------------------------------------------------------------------------------------
// Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    ServiceCurve createServiceCurve();

    ServiceCurve createServiceCurve(int segment_count);

    ServiceCurve createServiceCurve(String service_curve_str) throws Exception;

    ServiceCurve createServiceCurve(CurveUltAffine curve);

    ServiceCurve createZeroService();

    ServiceCurve createZeroDelayInfiniteBurst();

    ServiceCurve createDelayedInfiniteBurst(double delay);

    ServiceCurve createDelayedInfiniteBurst(Num delay);

    ServiceCurve createRateLatency(double rate, double latency);

    ServiceCurve createRateLatency(Num rate, Num latency);

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    ServiceCurve createServiceCurve(SegmentList aperSegments);
//
//    ServiceCurve createServiceCurve(SegmentList perSegments, double py0, long period, double pdy);
//
//    ServiceCurve createServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name);
//
//    ServiceCurve createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy);
//
//    ServiceCurve createServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name);
//
//    ServiceCurve createServiceCurve(SegmentList aperSegments, java.lang.String name);


//--------------------------------------------------------------------------------------------------------------
// Arrival Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    ArrivalCurve createArrivalCurve();

    ArrivalCurve createArrivalCurve(int segment_count);

    ArrivalCurve createArrivalCurve(String arrival_curve_str) throws Exception;

    ArrivalCurve createArrivalCurve(CurveUltAffine curve);

    ArrivalCurve createArrivalCurve(CurveUltAffine curve, boolean remove_latency);

    ArrivalCurve createZeroArrivals();

    ArrivalCurve createPeakArrivalRate(double rate);

    ArrivalCurve createPeakArrivalRate(Num rate);

    ArrivalCurve createTokenBucket(double rate, double burst);

    ArrivalCurve createTokenBucket(Num rate, Num burst);

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    ArrivalCurve createArrivalCurve(SegmentList aperSegments);
//
//    ArrivalCurve createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy);
//
//    ArrivalCurve createArrivalCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name);
//
//    ArrivalCurve createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy);
//
//    ArrivalCurve createArrivalCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name);
//    
//    ArrivalCurve createArrivalCurve(SegmentList aperSegments, java.lang.String name);


//--------------------------------------------------------------------------------------------------------------
// Maximum Service Curve Constructors
//--------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------
    // DiscoDNC compliance
    //------------------------------------------------------------
    MaxServiceCurve createMaxServiceCurve();

    MaxServiceCurve createMaxServiceCurve(int segment_count);

    MaxServiceCurve createMaxServiceCurve(String max_service_curve_str) throws Exception;

    MaxServiceCurve createMaxServiceCurve(CurveUltAffine curve);

    MaxServiceCurve createInfiniteMaxService();

    MaxServiceCurve createZeroDelayInfiniteBurstMSC();

    MaxServiceCurve createDelayedInfiniteBurstMSC(double delay);

    MaxServiceCurve createDelayedInfiniteBurstMSC(Num delay);

    MaxServiceCurve createRateLatencyMSC(double rate, double latency);

    MaxServiceCurve createRateLatencyMSC(Num rate, Num latency);

    // TODO Phase II
    //------------------------------------------------------------
    // RTC additional constructors
    //------------------------------------------------------------
//    MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments);
//
//    MaxServiceCurve createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy);
//
//    MaxServiceCurve createMaxServiceCurve(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name);
//
//    MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy);
//
//    MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, java.lang.String name);
//
//    MaxServiceCurve createMaxServiceCurve(SegmentList aperSegments, java.lang.String name);
}