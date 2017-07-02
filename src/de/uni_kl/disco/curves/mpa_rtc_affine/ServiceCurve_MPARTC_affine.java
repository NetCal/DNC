package de.uni_kl.disco.curves.mpa_rtc_affine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurveUltAffine;
import de.uni_kl.disco.curves.ServiceCurve;

public class ServiceCurve_MPARTC_affine extends Curve_MPARTC_affine implements ServiceCurve {
    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public ServiceCurve_MPARTC_affine() {
        super();
    }

    public ServiceCurve_MPARTC_affine(int segment_count) {
        super(segment_count);
    }

    public ServiceCurve_MPARTC_affine(CurveUltAffine curve) {
        copy(curve);
    }

    public ServiceCurve_MPARTC_affine(String service_curve_str) throws Exception {
        super.initializeCurve(service_curve_str);
    }


    public ServiceCurve_MPARTC_affine(SegmentList aperSegments) {
        rtc_curve = new Curve(aperSegments);
    }

    public ServiceCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy) {
        rtc_curve = new Curve(perSegments, py0, period, pdy);
    }

    public ServiceCurve_MPARTC_affine(SegmentList perSegments, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(perSegments, py0, period, pdy, name);
    }

    public ServiceCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy);
    }


    public ServiceCurve_MPARTC_affine(SegmentList aperSegments, SegmentList perSegments, double px0, double py0, long period, double pdy, String name) {
        rtc_curve = new Curve(aperSegments, perSegments, px0, py0, period, pdy, name);
    }

    public ServiceCurve_MPARTC_affine(SegmentList aperSegments, String name) {
        rtc_curve = new Curve(aperSegments, name);
    }

    public ServiceCurve_MPARTC_affine(Curve c) {
        rtc_curve = c.clone();
    }

    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    @Override
    public ServiceCurve_MPARTC_affine copy() {
        ServiceCurve_MPARTC_affine sc_copy = new ServiceCurve_MPARTC_affine();
        sc_copy.copy(this);
        return sc_copy;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ServiceCurve_MPARTC_affine) && super.equals(obj);
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
