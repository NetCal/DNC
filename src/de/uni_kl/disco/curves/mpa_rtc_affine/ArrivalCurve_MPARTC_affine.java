package de.uni_kl.disco.curves.mpa_rtc_affine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.ArrivalCurve;
import de.uni_kl.disco.curves.CurveMultAffine;

public class ArrivalCurve_MPARTC_affine extends Curve_MPARTC_affine implements ArrivalCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ArrivalCurve_MPARTC_affine() {
        super();
    }

    public ArrivalCurve_MPARTC_affine(int segment_count) {
        super(segment_count);
    }

    public ArrivalCurve_MPARTC_affine(CurveMultAffine curve) {
        super(curve);
    }

    public ArrivalCurve_MPARTC_affine(String arrival_curve_str) throws Exception {
        super.initializeCurve(arrival_curve_str);
    }


    public ArrivalCurve_MPARTC_affine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ArrivalCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ArrivalCurve_MPARTC_affine(Curve c) {
        rtc_curve = c.clone();
    }

    public ArrivalCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy, java.lang.String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }


    public ArrivalCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }

    public ArrivalCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ArrivalCurve_MPARTC_affine(SegmentList aperSegments, java.lang.String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    //------------------------------------------------------------------------------------------------------------
// Interface implementations
//------------------------------------------------------------------------------------------------------------
    @Override
    public ArrivalCurve_MPARTC_affine copy() {
        ArrivalCurve_MPARTC_affine ac_copy = new ArrivalCurve_MPARTC_affine();
        ac_copy.copy(this);
        return ac_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ArrivalCurve_MPARTC_affine) && super.equals(obj);
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
