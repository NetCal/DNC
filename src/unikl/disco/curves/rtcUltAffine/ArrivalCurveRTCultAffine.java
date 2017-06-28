package unikl.disco.curves.rtcUltAffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.CurveUltAffine;

public class ArrivalCurveRTCultAffine extends CurveRTCultAffine implements ArrivalCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ArrivalCurveRTCultAffine() {
        super();
    }

    public ArrivalCurveRTCultAffine(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurveRTCultAffine(CurveUltAffine curve) {
        super(curve);
    }

    public ArrivalCurveRTCultAffine(String arrival_curve_str) throws Exception {
        super.initializeCurve(arrival_curve_str);
    }


    public ArrivalCurveRTCultAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ArrivalCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ArrivalCurveRTCultAffine(Curve c) {
        rtc_curve = c.clone();
    }

    public ArrivalCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }


    public ArrivalCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public ArrivalCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ArrivalCurveRTCultAffine(SegmentList aperSegments, java.lang.String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    //------------------------------------------------------------------------------------------------------------
// Interface implementations
//------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurveRTCultAffine copy() {
        ArrivalCurveRTCultAffine ac_copy = new ArrivalCurveRTCultAffine();
        ac_copy.copy(this);
        return ac_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurveRTCultAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "AC".hashCode() * super.hashCode();
    }

    @Override
    public String toString() {
        return "C" + super.toString();
    }
}
