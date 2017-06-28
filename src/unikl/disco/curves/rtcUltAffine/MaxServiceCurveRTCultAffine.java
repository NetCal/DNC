package unikl.disco.curves.rtcUltAffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import unikl.disco.curves.CurveUltAffine;
import unikl.disco.curves.MaxServiceCurve;

public class MaxServiceCurveRTCultAffine extends CurveRTCultAffine implements MaxServiceCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    protected MaxServiceCurveRTCultAffine() {
        super();
    }

    public MaxServiceCurveRTCultAffine(int segment_count) {
        super(segment_count);
    }

    public MaxServiceCurveRTCultAffine(CurveUltAffine curve) {
        copy(curve);
    }

    public MaxServiceCurveRTCultAffine(String max_service_curve_str) throws Exception {
        super.initializeCurve(max_service_curve_str);
    }


    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public MaxServiceCurveRTCultAffine copy() {
        MaxServiceCurveRTCultAffine msc_copy = new MaxServiceCurveRTCultAffine();
        msc_copy.copy(this);

        return msc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MaxServiceCurveRTCultAffine) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return "MSC".hashCode() * super.hashCode();
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        return "MS" + super.toString();
    }


    public MaxServiceCurveRTCultAffine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public MaxServiceCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public MaxServiceCurveRTCultAffine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public MaxServiceCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public MaxServiceCurveRTCultAffine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public MaxServiceCurveRTCultAffine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public MaxServiceCurveRTCultAffine(Curve c) {
        rtc_curve = c.clone();
    }
}
