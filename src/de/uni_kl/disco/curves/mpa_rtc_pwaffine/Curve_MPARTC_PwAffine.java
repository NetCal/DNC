/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera"
 *
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.disco.curves.mpa_rtc_pwaffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurvePwAffine;
import de.uni_kl.disco.curves.LinearSegment;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;
import de.uni_kl.disco.numbers.implementations.RealDouble;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Curve_MPARTC_PwAffine implements CurvePwAffine {
    protected ch.ethz.rtc.kernel.Curve rtc_curve;

    protected boolean is_delayed_infinite_burst = false;
    protected boolean is_rate_latency = false;
    protected boolean is_token_bucket = false;

    protected boolean has_rate_latency_meta_info = false;
    protected List<Curve_MPARTC_PwAffine> rate_latencies = new LinkedList<Curve_MPARTC_PwAffine>();

    protected boolean has_token_bucket_meta_info = false;
    protected List<Curve_MPARTC_PwAffine> token_buckets = new LinkedList<Curve_MPARTC_PwAffine>();


    //--------------------------------------------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------------------------------------------

    /**
     * Creates a <code>Curve</code> instance with a single segment on the x-axis.
     */
    public Curve_MPARTC_PwAffine() {
        createZeroSegmentsCurve(1);
    }

    public Curve_MPARTC_PwAffine(CurvePwAffine curve) {
        copy(curve);
    }

    /**
     * Creates a <code>Curve</code> instance with <code>segment_count</code>
     * empty <code>LinearSegment</code> instances.
     *
     * @param segment_count the number of segments
     */
    public Curve_MPARTC_PwAffine(int segment_count) {
        createZeroSegmentsCurve(segment_count);
    }

    // TODO * Default behavior of the DiscoDNC cannot be reenacted with the RTC
    //		* creates a curve with segments (segment_count,segment_count,0) instead of (0,0,0)
    // 		  because RTC checks its segments and won't allow multiple ones at (x,y,r)=(0,0,r).
    private void createZeroSegmentsCurve(int segment_count) {
        SegmentList segList_rtc = new SegmentList();
        for (int i = 0; i < segment_count; i++) {
            segList_rtc.add(new Segment(
                    (double) i,
                    (double) i,
                    0));
        }
        rtc_curve = new Curve(segList_rtc);
    }

    public Curve getRtc_curve() {
        return rtc_curve;
    }

    // Accepts string representations of RTC
    // as well as DNC Curve, ArrivalCurve, ServiceCurve, and MaxServiceCurve
    protected void initializeCurve(String curve_str) throws Exception {
        if (curve_str.substring(0, 6).equals("Curve:")) { // RTC curve string
            String periodic = "";
            // Cut out periodic Part
            if(curve_str.contains("PeriodicPart")) {
                periodic = curve_str.substring(curve_str.indexOf("PeriodicPart"));
                curve_str = curve_str.substring(0, curve_str.indexOf("PeriodicPart"));
            }
            String s = curve_str.substring(27); // cut everything but {...}
            s = s.substring(1, s.length() - 3); //cut leading and trailing { }
            s = s.replaceAll("\\(","").replaceAll("\\)"," ");
            SegmentList list = new SegmentList();
            String[] sl = s.split(" ");
            for (int i = 0; i < sl.length; i++){
                String[] val = sl[i].split(",");
                list.add(new Segment(Double.parseDouble(val[0]), Double.parseDouble(val[1]), Double.parseDouble(val[2])));
            }
            rtc_curve = new Curve(list);
            return;
        }

        if (curve_str.substring(0, 2).equals("AC") || curve_str.substring(0, 2).equals("SC")) {
            curve_str = curve_str.substring(2);
        } else {
            if (curve_str.substring(0, 3).equals("MSC")) {
                curve_str = curve_str.substring(3);
            }
        }

        // Must to be a string representation of a "raw" curve object at this location.
        if (curve_str.charAt(0) != '{' || curve_str.charAt(curve_str.length() - 1) != '}') {
            throw new RuntimeException("Invalid string representation of a curve.");
        }

        // Remove enclosing curly brackets
        String curve_str_internal = curve_str.substring(1, curve_str.length() - 1);

        String[] segments_to_parse = curve_str_internal.split(";");

        //		* Might also fail due to a spot in the origin
        SegmentList segList_rtc = new SegmentList();
        for (int i = 0; i < segments_to_parse.length; i++) {
            segments_to_parse[i] = segments_to_parse[i].replaceAll(" ", "");
            if (segments_to_parse[i].charAt(0) == '!') {
                segments_to_parse[i] = segments_to_parse[i].substring(1);
            }
            segments_to_parse[i] = segments_to_parse[i].substring(1);
            String x = segments_to_parse[i].substring(0, segments_to_parse[i].indexOf(","));
            String y = segments_to_parse[i].substring(x.length() + 1, segments_to_parse[i].lastIndexOf(")"));
            String s = segments_to_parse[i].substring(x.length() + y.length() + 3);
            segList_rtc.add(new Segment(
                    Double.parseDouble(x),
                    Double.parseDouble(y),
                    Double.parseDouble(s)));
        }
        segList_rtc.simplify();
        // TODO
        // This removes the first segment if the first spot is in the origin and the second starts at x = 0 -> generic solution needed
        //if (segList_rtc.size() > 1 && ((segList_rtc.get(1).x() == 0) && (segList_rtc.get(0).x() == 0) && (segList_rtc.get(0).y() == 0)) ){
        //   segList_rtc.remove(0);
        //}
        //rtc_curve = new Curve(segList_rtc);
    }

    private void clearMetaInfo() {
        has_token_bucket_meta_info = false;
        is_token_bucket = false;
        token_buckets = new LinkedList<Curve_MPARTC_PwAffine>();

        has_rate_latency_meta_info = false;
        is_rate_latency = false;
        rate_latencies = new LinkedList<Curve_MPARTC_PwAffine>();
    }


//--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------

    /**
     * Returns a copy of this instance.
     *
     * @return a copy of this instance.
     */
    @Override
    public Curve_MPARTC_PwAffine copy() {
        Curve_MPARTC_PwAffine c_copy = new Curve_MPARTC_PwAffine();
        c_copy.copy(this);
        return c_copy;
    }

    @Override
    public void copy(de.uni_kl.disco.curves.Curve curve) {
        if (curve instanceof Curve_MPARTC_PwAffine) {
            this.rtc_curve = ((Curve_MPARTC_PwAffine) curve).rtc_curve.clone();

            this.has_rate_latency_meta_info = ((Curve_MPARTC_PwAffine) curve).has_rate_latency_meta_info;
            this.rate_latencies = new LinkedList<>();
            for (int i = 0; i < rate_latencies.size(); i++) {
                this.rate_latencies.add(((Curve_MPARTC_PwAffine) curve).rate_latencies.get(i).copy());
            }

            this.has_token_bucket_meta_info = ((Curve_MPARTC_PwAffine) curve).has_token_bucket_meta_info;
            this.token_buckets = new LinkedList<>();
            for (int i = 0; i < token_buckets.size(); i++) {
                this.token_buckets.add(((Curve_MPARTC_PwAffine) curve).token_buckets.get(i).copy());
            }

            this.is_delayed_infinite_burst = ((CurvePwAffine) curve).getDelayedInfiniteBurst_Property();
            this.is_rate_latency = ((CurvePwAffine) curve).getRL_property();
            this.is_token_bucket = ((CurvePwAffine) curve).getTB_Property();
        } else {
            SegmentList segList_rtc = new SegmentList();
            LinearSegment seg_tmp;

            // TODO * Might also fail due to a spot in the origin
            for (int i = 0; i < curve.getSegmentCount(); i++) {
                seg_tmp = curve.getSegment(i);

                segList_rtc.add(new Segment(
                        seg_tmp.getX().doubleValue(),
                        seg_tmp.getY().doubleValue(),
                        seg_tmp.getGrad().doubleValue()));
            }
            this.rtc_curve = new Curve(segList_rtc);
        }
    }

    //------------------------------------------------------------
    // Curve's segments
    //------------------------------------------------------------

    /**
     * Starting at 0 as the RTC SegmentList extends ArrayList.
     */
    public LinearSegment_MPARTC_PwAffine getSegment(int pos) {
        // IMPORTANT! This is the correct code to prevent update errors
        LinearSegment_MPARTC_PwAffine s = new LinearSegment_MPARTC_PwAffine(0, 0, 0);
        s.setRtc_segment(rtc_curve.aperiodicSegments().get(pos));
        return s;
        // Old code, bugged because it was not able to update the Segment, like done in various add functions
        //return new LinearSegmentRTC(rtc_curve.aperiodicSegments().get(pos));
    }

    private Segment getSegmentRTC(int pos) {
        return rtc_curve.aperiodicSegments().get(pos);
    }

    /**
     * Returns the number of segments in this curve.
     *
     * @return the number of segments
     */
    public int getSegmentCount() {
        return rtc_curve.aperiodicSegments().size();
    }

    /**
     * TODO: We assume that RTC curves are left-continuous
     * as we cannot make this explicit.
     * Therefore, we return the first segment that defines the given x.
     * This behavior coinsides with  getSegmentLimitRight(x).
     */
    public int getSegmentDefining(Num x) {
        return getSegmentLimitRight(x);
    }

    public int getSegmentLimitRight(Num x) {
        if (x.equals(NumFactory.getPositiveInfinity())) {
            return getSegmentCount();
        }

        for (int i = getSegmentCount() - 1; i >= 0; i--) {
            if (getSegmentRTC(i).x() <= x.doubleValue()) {
                return i;
            }
        }
        return -1;
    }

    protected void initializeWithSegment(Segment rtc_segment) {
        SegmentList seg_list = new SegmentList();
        seg_list.add(rtc_segment);
        rtc_curve = new Curve(seg_list);
        clearMetaInfo();
    }

    protected void initializeWithSegments(SegmentList rtc_segmentList) {
        //System.out.println(rtc_segmentList.toString());
        rtc_segmentList.simplify();
        rtc_curve = new Curve(rtc_segmentList);
        clearMetaInfo();
    }

    public void addSegment(LinearSegment s) {
        addSegment(getSegmentCount(), s);
    }

    /**
     * Starting at 0 as the RTC SegmentList extends ArrayList.
     */
    public void addSegment(int pos, LinearSegment s) {
        if (s == null) {
            throw new IllegalArgumentException("Tried to insert null!");
        }

        Segment new_segment = new Segment(
                s.getX().doubleValue(),
                s.getY().doubleValue(),
                s.getGrad().doubleValue());

        rtc_curve.aperiodicSegments().add(pos, new_segment);
        clearMetaInfo();
    }

    public void removeSegment(int pos) {
        rtc_curve.aperiodicSegments().remove(pos);
        clearMetaInfo();
    }

    //------------------------------------------------------------
    // Curve properties
    //------------------------------------------------------------

    public boolean isDiscontinuity(int pos) {
        return (pos + 1 < getSegmentCount()
                && (Math.abs(getSegmentRTC(pos + 1).x() - getSegmentRTC(pos).x()))
                < RealDouble.createEpsilon().doubleValue()
        );
    }

    public boolean isRealDiscontinuity(int pos) {
        return (isDiscontinuity(pos)
                && (Math.abs(getSegmentRTC(pos + 1).y() - getSegmentRTC(pos).y()))
                >= RealDouble.createEpsilon().doubleValue()
        );
    }

    public boolean isUnrealDiscontinuity(int pos) {
        return (isDiscontinuity(pos)
                && (Math.abs(getSegmentRTC(pos + 1).y() - getSegmentRTC(pos).y()))
                < RealDouble.createEpsilon().doubleValue()
        );
    }

    public boolean isWideSenseIncreasing() {
        double y = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for initial comparison in the loop.
        for (int i = 0; i < getSegmentCount(); i++) {
            if (getSegmentRTC(i).y() < y
                    || getSegmentRTC(i).s() < 0.0) {
                return false;
            }
            y = getSegmentRTC(i).y();
        }
        return true;
    }

    public boolean isConvex() {
        return isConvexIn(NumFactory.getZero(), NumFactory.getPositiveInfinity());
    }

    public boolean isConvexIn(Num a, Num b) {
        double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for initial comparison in the loop.

        int i_start = getSegmentDefining(a);
        int i_end = getSegmentDefining(b);
        for (int i = i_start; i <= i_end; i++) {
            if (i_start < 0) {
                break;
            }
            if (i == i_end && getSegmentRTC(i).x() == b.doubleValue()) {
                break;
            }
            double gradient;
            if (i < getSegmentCount() - 1) {
                gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y()
                        /
                        getSegmentRTC(i + 1).x() - getSegmentRTC(i).x();
            } else {
                gradient = getSegmentRTC(i).s();
            }
            if (gradient < last_gradient) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    public boolean isConcave() {
        return isConcaveIn(NumFactory.getZero(), NumFactory.getPositiveInfinity());
    }

    /**
     * Tests whether the curve is concave in [a,b].
     *
     * @param a the lower bound of the test interval.
     * @param b the upper bound of the test interval.
     * @return whether the curve is concave.
     */
    public boolean isConcaveIn(Num a, Num b) {
        double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for initial comparison in the loop.

        int i_start = getSegmentDefining(a);
        int i_end = getSegmentDefining(b);
        for (int i = i_start; i <= i_end; i++) {
            if (i == i_end && getSegmentRTC(i).x() == b.doubleValue()) {
                break;
            }
            double gradient;
            // Handles discontinuities
            if (i < getSegmentCount() - 1) {
                gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y()
                        /
                        getSegmentRTC(i + 1).x() - getSegmentRTC(i).x();
            } else {
                gradient = getSegmentRTC(i).s();
            }
            if (gradient > last_gradient) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    public boolean isAlmostConcave() {
        double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for initial comparison in the loop.

        for (int i = 0; i < getSegmentCount(); i++) {
            // Skip the horizontal part at the beginning
            if (last_gradient == Double.POSITIVE_INFINITY
                    && getSegmentRTC(i).s() == 0.0) {
                continue;
            }

            double gradient;
            if (i < getSegmentCount() - 1) {
                gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y()
                        /
                        getSegmentRTC(i + 1).x() - getSegmentRTC(i).x();
            } else {
                gradient = getSegmentRTC(i).s();
            }
            if (gradient > last_gradient) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Curve_MPARTC_PwAffine)) {
            return false;
        }

        return rtc_curve.equals(((Curve_MPARTC_PwAffine) obj).rtc_curve);
    }

    @Override
    public int hashCode() {
        return rtc_curve.hashCode();
    }

    @Override
    public java.lang.String toString() {
        return rtc_curve.toString();
    }

    //------------------------------------------------------------
    // Curve function values
    //------------------------------------------------------------

    public Num f(Num x) {
        // Assume left-continuity --> getYmin
        return NumFactory.create(rtc_curve.getYmin(x.doubleValue()));
    }

    public Num fLimitRight(Num x) {
        // Assume left-continuity --> getYmin
        return NumFactory.create(rtc_curve.getYmin(x.doubleValue()));
    }

    public Num f_inv(Num y) {
        return f_inv(y, false);
    }

    public Num f_inv(Num y, boolean rightmost) {
        if (rightmost) {
            return NumFactory.create(rtc_curve.getXmax(y.doubleValue()));
        } else {
            return NumFactory.create(rtc_curve.getXmin(y.doubleValue()));
        }
    }

    public Num getLatency() {
        return NumFactory.create(rtc_curve.getXmax(0.0));
    }

    public Num getBurst() {
        return NumFactory.create(rtc_curve.y0epsilon());
    }

    public Num getGradientLimitRight(Num x) {
        int i = getSegmentLimitRight(x);
        if (i < 0) {
            return NumFactory.createNaN();
        }
        return NumFactory.create(getSegmentRTC(i).s());
    }

    public Num getTB_Burst() {
        // RTC Token Buckets cannot pass through the origin
        return NumFactory.create(rtc_curve.y0epsilon());
    }

    @Override
    public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
        this.has_token_bucket_meta_info = has_token_bucket_meta_info;
    }

    @Override
    public void setTB_Property(boolean is_token_bucket) {
        this.is_token_bucket = is_token_bucket;
    }

    @Override
    public void setRL_Property(boolean is_rate_latency) {
        this.is_rate_latency = is_rate_latency;
    }

    // TODO: See CurveDnc.getRate_Latencies
    @Override
    public List<CurvePwAffine> getRL_Components() {
        List<CurvePwAffine> tmp = new LinkedList<>();
        if (this.is_rate_latency) {
            tmp.add(this.copy());
        } else {
            for (int i = 0; i < rate_latencies.size(); i++) {
                tmp.add(rate_latencies.get(i));
            }
        }
        return tmp;
    }

    @Override
    public void setRL_Components(List<CurvePwAffine> rate_latencies) {
        List<Curve_MPARTC_PwAffine> tmp = new LinkedList<>();
        for (int i = 0; i < rate_latencies.size(); i++) {
            tmp.add((Curve_MPARTC_PwAffine) rate_latencies.get(i));
        }
        this.rate_latencies = tmp;
    }

    // TODO: See CurveDNC.getRate_Latencies
    @Override
    public List<CurvePwAffine> getTB_Components() {
        List<CurvePwAffine> tmp = new LinkedList<>();
        for (int i = 0; i < token_buckets.size(); i++) {
            tmp.add(token_buckets.get(i));
        }
        return tmp;
    }

    @Override
    public void setTB_Components(List<CurvePwAffine> token_buckets) {
        List<Curve_MPARTC_PwAffine> tmp = new LinkedList<>();
        for (int i = 0; i < token_buckets.size(); i++) {
            tmp.add((Curve_MPARTC_PwAffine) token_buckets.get(i));
        }
        this.token_buckets = tmp;
    }

    @Override
    public void setRL_MetaInfo(boolean has_rate_latency_meta_info) {
        this.has_rate_latency_meta_info = has_rate_latency_meta_info;
    }

    public Num getUltAffineRate() {
        return NumFactory.create(rtc_curve.aperiodicSegments().lastSegment().s());
    }

    //------------------------------------------------------------
    // Curve manipulation
    //------------------------------------------------------------

    public void beautify() {
        if (rtc_curve.aperiodicSegments().size() > 1) {
            if (rtc_curve.aperiodicSegments().get(0).equals(rtc_curve.aperiodicSegments().get(1))) {
                rtc_curve.aperiodicSegments().remove(0);
            }
        }
        // TODO simplify();
    }

    //------------------------------------------------------------
    // Specific curve shapes
    //------------------------------------------------------------

    // Burst delay
    public boolean getDelayedInfiniteBurst_Property() {
        return is_delayed_infinite_burst;
    }


    // Rate latency
    public boolean getRL_property() {
        decomposeIntoRateLatencies();
        return is_rate_latency;
    }

    public int getRL_ComponentCount() {
        decomposeIntoRateLatencies();
        return rate_latencies.size();
    }

    public Curve_MPARTC_PwAffine getRL_Component(int i) {
        decomposeIntoRateLatencies();
        // TODO @Steffen: does this fix break anything? This one is like your RateLatency Fix
        if (is_rate_latency) {
            return this.copy();
        }
        return rate_latencies.get(i);
    }

    private void decomposeIntoRateLatencies() {
        if (has_rate_latency_meta_info == true) {
            return;
        }

        if (CalculatorConfig.SERVICE_CURVE_CHECKS && !this.isConvex()) {
            if (this.equals(CurveFactory_MPARTC_PwAffine.factory_object.createZeroDelayInfiniteBurst())) {
                rate_latencies = new ArrayList<Curve_MPARTC_PwAffine>();
                rate_latencies.add(CurveFactory_MPARTC_PwAffine.factory_object.createRateLatency(NumFactory.createPositiveInfinity(), NumFactory.createZero()));
            } else {
                throw new RuntimeException("Can only decompose convex service curves into rate latency curves.");
            }
        } else {
            rate_latencies = new ArrayList<Curve_MPARTC_PwAffine>();
            for (int i = 0; i < getSegmentCount(); i++) {
                if (getSegmentRTC(i).y() == 0.0 && getSegmentRTC(i).s() == 0.0) {
                    continue;
                }
                double rate = getSegmentRTC(i).s();
                double latency = getSegmentRTC(i).x() -
                        (getSegmentRTC(i).y() / getSegmentRTC(i).s());
                if (latency < 0.0) {
                    continue;
                }
                rate_latencies.add(CurveFactory_MPARTC_PwAffine.factory_object.createRateLatency(rate, latency));
            }
        }

        is_rate_latency = rate_latencies.size() == 1;

        has_rate_latency_meta_info = true;
    }

    // Token bucket
    public boolean getTB_Property() {
        decomposeIntoTokenBuckets();
        return is_token_bucket;
    }

    public int getTB_ComponentCount() {
        decomposeIntoTokenBuckets();
        return token_buckets.size();
    }

    public Curve_MPARTC_PwAffine getTB_Component(int i) {
        decomposeIntoTokenBuckets();
        return token_buckets.get(i);
    }

    private void decomposeIntoTokenBuckets() {
        if (has_token_bucket_meta_info == true) {
            //return;
        }

        if (CalculatorConfig.ARRIVAL_CURVE_CHECKS && !this.isConcave()) {
            throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
        }

        token_buckets = new ArrayList<Curve_MPARTC_PwAffine>();
        for (int i = 0; i < getSegmentCount(); i++) {
            if (isDiscontinuity(i)) {
                continue;
            }
            double rate = getSegmentRTC(i).s();
            double burst = getSegmentRTC(i).y() -
                    (getSegmentRTC(i).x() * getSegmentRTC(i).s());
            token_buckets.add(CurveFactory_MPARTC_PwAffine.factory_object.createTokenBucket(rate, burst));
        }

        is_token_bucket = token_buckets.size() == 1;

        has_token_bucket_meta_info = true;
    }
}
