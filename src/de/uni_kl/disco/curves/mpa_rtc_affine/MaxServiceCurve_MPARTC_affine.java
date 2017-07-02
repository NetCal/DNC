package de.uni_kl.disco.curves.mpa_rtc_affine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurveMultAffine;
import de.uni_kl.disco.curves.MaxServiceCurve;

public class MaxServiceCurve_MPARTC_affine extends Curve_MPARTC_affine implements MaxServiceCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    protected MaxServiceCurve_MPARTC_affine() {
        super();
    }

    public MaxServiceCurve_MPARTC_affine(int segment_count) {
        super(segment_count);
    }

    public MaxServiceCurve_MPARTC_affine(CurveMultAffine curve) {
        copy(curve);
    }

    public MaxServiceCurve_MPARTC_affine(String max_service_curve_str) throws Exception {
        super.initializeCurve(max_service_curve_str);
    }


    public MaxServiceCurve_MPARTC_affine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public MaxServiceCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public MaxServiceCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public MaxServiceCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }


    public MaxServiceCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public MaxServiceCurve_MPARTC_affine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public MaxServiceCurve_MPARTC_affine(Curve c) {
        rtc_curve = c.clone();
    }

    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public MaxServiceCurve_MPARTC_affine copy() {
        MaxServiceCurve_MPARTC_affine msc_copy = new MaxServiceCurve_MPARTC_affine();
        msc_copy.copy(this);

        return msc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof MaxServiceCurve_MPARTC_affine) && super.equals(obj);
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
}
