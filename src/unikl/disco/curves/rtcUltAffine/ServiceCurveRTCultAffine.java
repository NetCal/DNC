package unikl.disco.curves.rtcUltAffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import unikl.disco.curves.CurveUltAffine;
import unikl.disco.curves.ServiceCurve;

public class ServiceCurveRTCultAffine extends CurveRTCultAffine implements ServiceCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ServiceCurveRTCultAffine() {
        super();
    }

    public ServiceCurveRTCultAffine(int segment_count) {
        super(segment_count);
    }

    public ServiceCurveRTCultAffine(CurveUltAffine curve) {
        copy(curve);
    }

    public ServiceCurveRTCultAffine(String service_curve_str) throws Exception {
        super.initializeCurve(service_curve_str);
    }


    public ServiceCurveRTCultAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ServiceCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ServiceCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public ServiceCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }


    public ServiceCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ServiceCurveRTCultAffine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public ServiceCurveRTCultAffine(Curve c) {
        rtc_curve = c.clone();
    }

    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public ServiceCurveRTCultAffine copy() {
        ServiceCurveRTCultAffine sc_copy = new ServiceCurveRTCultAffine();
        sc_copy.copy(this);
        return sc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceCurveRTCultAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "SC".hashCode() * super.hashCode();
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        return "S" + super.toString();
    }
}
