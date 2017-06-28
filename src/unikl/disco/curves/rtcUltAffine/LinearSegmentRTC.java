package unikl.disco.curves.rtcUltAffine;

import ch.ethz.rtc.kernel.Segment;
import unikl.disco.curves.LinearSegment;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;
import unikl.disco.numbers.NumUtils;

public class LinearSegmentRTC implements LinearSegment {

    private ch.ethz.rtc.kernel.Segment rtc_segment;

    //--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public LinearSegmentRTC(double x, double y, double s) {
        rtc_segment = new Segment(x, y, s);
    }

    public LinearSegmentRTC(LinearSegment segment) {
        if (segment instanceof LinearSegmentRTC) {
            rtc_segment = ((LinearSegmentRTC) segment).rtc_segment.clone();
        } else {
            rtc_segment = new Segment(segment.getX().doubleValue(),
                    segment.getY().doubleValue(),
                    segment.getGrad().doubleValue());
        }
    }

    public LinearSegmentRTC(Segment segment) {
        rtc_segment = segment.clone();
    }

    public LinearSegmentRTC(String segment_str) {
        rtc_segment = new Segment(segment_str);
    }

    // Setter in order to prevent copy bug
    protected void setRtc_segment(Segment rtc_segment) {
        this.rtc_segment = rtc_segment;
    }

    //--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    public Num f(Num x) {
        return NumFactory.create(yAt(x.doubleValue()));
    }

    public Num getX() {
        return NumFactory.create(rtc_segment.x());
    }

    public void setX(double x) {
        rtc_segment.setX(x);
    }

    public void setX(Num x) {
        rtc_segment.setX(x.doubleValue());
    }

    public Num getY() {
        return NumFactory.create(rtc_segment.y());
    }

    public void setY(double y) {
        rtc_segment.setY(y);
    }

    public void setY(Num y) {
        rtc_segment.setY(y.doubleValue());
    }

    public Num getGrad() {
        return NumFactory.create(rtc_segment.s());
    }

    public void setGrad(Num grad) {
        rtc_segment.setS(grad.doubleValue());
    }

    public boolean isLeftopen() {
        // TODO
//        System.out.println("LinearSegmentRTC is leftopen by default");
        return true;
    }

    public void setLeftopen(boolean leftopen) {
        // TODO
//        System.out.println("LinearSegmentRTC is leftopen by default");
    }

    public Num getXIntersectionWith(LinearSegment other) {
        Num y1 = NumFactory.create(rtc_segment.y() - (rtc_segment.x() * rtc_segment.s()));
        Num y2 = NumUtils.sub(other.getY(), NumUtils.mult(other.getX(), other.getGrad()));

        // returns NaN if lines are parallel
        return NumUtils.div(NumUtils.sub(y2, y1), NumUtils.sub(this.getGrad(), other.getGrad()));
    }

    @Override
    public LinearSegment copy() {
        return new LinearSegmentRTC(rtc_segment);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LinearSegmentRTC)) {
            return false;
        }
        return rtc_segment.equals(obj);
    }

    @Override
    public String toString() {
        return rtc_segment.toString();
    }

    @Override
    public int hashCode() {
        return rtc_segment.hashCode();
    }

    //------------------------------------------------------------------------------------------------------------
    // MPA toolbox functionality
    //------------------------------------------------------------------------------------------------------------
    public Segment getSeg() {
        return rtc_segment;
    }

    public void setSeg(Segment seg) {
        this.rtc_segment = seg;
    }

    public double yAt(double x0) {
        return rtc_segment.yAt(x0);
    }

    public void move(double dx, double dy) {
        rtc_segment.move(dx, dy);
    }

    public boolean moveEquals(double dx, double dy, Segment seg) {
        return rtc_segment.moveEquals(dx, dy, seg);
    }

    public void round() {
        rtc_segment.round();
    }

    public double s() {
        return rtc_segment.s();
    }

    public void scaleX(double factor) {
        rtc_segment.scaleX(factor);
    }

    public void scaleY(double factor) {
        rtc_segment.scaleY(factor);
    }

    public void setS(double s) {
        rtc_segment.setS(s);
    }

    public java.lang.String toExportString() {
        return rtc_segment.toString();
    }

    public double x() {
        return rtc_segment.x();
    }

    public double xAt(double y0) {
        return rtc_segment.xAt(y0);
    }

    public double y() {
        return rtc_segment.y();
    }
}
