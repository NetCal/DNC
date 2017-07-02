/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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

package de.uni_kl.disco.curves.dnc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.uni_kl.disco.curves.Curve;
import de.uni_kl.disco.curves.CurvePwAffine;
import de.uni_kl.disco.curves.CurvePwAffineUtils;
import de.uni_kl.disco.curves.LinearSegment;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;
import de.uni_kl.disco.numbers.NumUtils;

/**
 * Class representing a piecewise linear curve, defined on [0,inf).<br>
 * The curve is stored as an array of <code>LinearSegment</code>
 * objects. Each of these objects defines a linear piece of the
 * curve from one inflection point up to, but not including, the
 * next. It is possible to define discontinuities by defining two
 * subsequent <code>LinearSegment</code> instances which start at
 * the same inflection point. In this case, the second segment
 * needs to have <code>leftopen</code> set to <code>true</code> to
 * indicate that the inflection point is excluded from the second
 * segment.<br>
 * All arithmetic operations on a curve return a new instance of class
 * <code>Curve</code>.<br>
 *
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 */
public class Curve_DNC implements CurvePwAffine {
    protected LinearSegment_DNC[] segments;

    protected boolean is_delayed_infinite_burst = false;

    protected boolean is_rate_latency = false;
    protected boolean has_rate_latency_meta_info = false;
    protected List<Curve_DNC> rate_latencies = new LinkedList<Curve_DNC>();

    protected boolean is_token_bucket = false;
    protected boolean has_token_bucket_meta_info = false;
    protected List<Curve_DNC> token_buckets = new LinkedList<Curve_DNC>();

    /**
     * Creates a <code>CurveDNC</code> instance with a single segment on the x-axis.
     */
    public Curve_DNC() {
        createNewCurve(1, false);
    }

    public Curve_DNC(CurvePwAffine curve) {
        copy(curve);
    }


    /**
     * Creates a <code>Curve</code> instance with <code>segment_count</code>
     * empty <code>LinearSegment</code> instances.
     *
     * @param segment_count the number of segments
     */
    public Curve_DNC(int segment_count) {
        createNewCurve(segment_count, false);
    }

    public boolean isIs_rate_latency() {
        return is_rate_latency;
    }

    public void setRL_Property(boolean is_rate_latency) {
        this.is_rate_latency = is_rate_latency;
    }

    public boolean isIs_token_bucket() {
        return is_token_bucket;
    }

    public void setTB_Property(boolean is_token_bucket) {
        this.is_token_bucket = is_token_bucket;
    }

    public boolean isHas_rate_latency_meta_info() {
        return has_rate_latency_meta_info;
    }

    public void setRL_MetaInfo(boolean has_rate_latency_meta_info) {
        this.has_rate_latency_meta_info = has_rate_latency_meta_info;
    }

    /**
     * Returns the sustained rate (the gradient of the last segment).
     *
     * @return the sustained rate.
     */
    @Override
    public Num getSustainedRate() {
        return segments[segments.length-1].grad;
    }

    // TODO: @Steffen
    // Warning! Can cause runtime exceptions when not handled correctly while calling!
    // There are some cases where RateLatency Curves with is_rate_latency = true are created, but with empty RL list
    // May be fixed now
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

    public void setRL_Components(List<CurvePwAffine> rate_latencies) {
        List<Curve_DNC> tmp = new LinkedList<>();
        for (int i = 0; i < rate_latencies.size(); i++) {
            tmp.add((Curve_DNC) rate_latencies.get(i));
        }
        this.rate_latencies = tmp;
    }

    public boolean isHas_token_bucket_meta_info() {
        return has_token_bucket_meta_info;
    }


//--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------

    public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
        this.has_token_bucket_meta_info = has_token_bucket_meta_info;
    }

    // TODO: see getRate_latencies
    public List<CurvePwAffine> getTB_Components() {
        List<CurvePwAffine> tmp = new LinkedList<>();
        for (int i = 0; i < token_buckets.size(); i++) {
            tmp.add(token_buckets.get(i));
        }
        return tmp;
    }

    public void setTB_Components(List<CurvePwAffine> token_buckets) {
        List<Curve_DNC> tmp = new LinkedList<>();
        for (int i = 0; i < token_buckets.size(); i++) {
            tmp.add((Curve_DNC) token_buckets.get(i));
        }
        this.token_buckets = tmp;
    }

    private void createNewCurve(int segment_count, boolean empty) {
        if (!empty) {    // old default
            createZeroSegmentsCurve(segment_count);
        } else {        // potential new default, tests work
            segments = new LinearSegment_DNC[segment_count];
            // Initialize Elements of array, not only array itself
            segments[0] = new LinearSegment_DNC(
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    false);

            for (int i = 1; i < segment_count; i++) {
                segments[i] = new LinearSegment_DNC(NumFactory.createZero(),
                        NumFactory.createZero(),
                        NumFactory.createZero(),
                        true);
            }
        }
    }

    private void createZeroSegmentsCurve(int segment_count) {
        segments = new LinearSegment_DNC[segment_count];

        if (segment_count == 0) {
            return;
        }

        segments[0] = new LinearSegment_DNC(
                NumFactory.createZero(),
                NumFactory.createZero(),
                NumFactory.createZero(),
                false);

        for (int i = 1; i < segment_count; i++) {
            segments[i] = new LinearSegment_DNC(
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    true);
        }
    }

    // Accepts string representations of Curve, ArrivalCurve, ServiceCurve, and MaxServiceCurve
    protected void initializeCurve(String curve_str) throws Exception {
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
        segments = new LinearSegment_DNC[segments_to_parse.length]; // No need to use createZeroSegments( i ) because we will store parsed segments

        for (int i = 0; i < segments_to_parse.length; i++) {
            segments[i] = new LinearSegment_DNC(segments_to_parse[i]);
        }
        CurvePwAffineUtils.beautify(this);
    }

    protected void forceThroughOrigin() {
        if (getSegment(0).getY().gtZero()) {
            addSegment(0, new LinearSegment_DNC(
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    NumFactory.createZero(),
                    false));

            getSegment(1).setLeftopen(true);
        }
    }

    private void clearMetaInfo() {
        has_token_bucket_meta_info = false;
        is_token_bucket = false;
        token_buckets = new LinkedList<Curve_DNC>();

        has_rate_latency_meta_info = false;
        is_rate_latency = false;
        rate_latencies = new LinkedList<Curve_DNC>();
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
    public Curve_DNC copy() {
        Curve_DNC c_copy = new Curve_DNC();
        c_copy.copy(this);
        return c_copy;
    }

    @Override
    public void copy(Curve curve) {
        LinearSegment_DNC[] segments = new LinearSegment_DNC[curve.getSegmentCount()];

        if (curve instanceof Curve_DNC) {
            // Can System.arraycopy create deep copies? Nope, Arrays.copyOf neither
            // TODO: @Steffen: but all usages of arraycopy should be safe, since the underlying objects are either
            // copied or cannot be changed
            for (int i = 0; i < segments.length; i++) {
                segments[i] = ((Curve_DNC) curve).getSegment(i).copy();
            }

            this.has_rate_latency_meta_info = ((Curve_DNC) curve).has_rate_latency_meta_info;
            this.rate_latencies = ((Curve_DNC) curve).rate_latencies;

            this.has_token_bucket_meta_info = ((Curve_DNC) curve).has_token_bucket_meta_info;
            this.token_buckets = ((Curve_DNC) curve).token_buckets;

            this.is_delayed_infinite_burst = ((CurvePwAffine) curve).getDelayedInfiniteBurst_Property();
            this.is_rate_latency = ((CurvePwAffine) curve).getRL_property();
            this.is_token_bucket = ((CurvePwAffine) curve).getTB_Property();
        } else {
            for (int i = 0; i < curve.getSegmentCount(); i++) {
                segments[i] = new LinearSegment_DNC(curve.getSegment(i));
            }
        }

        setSegments(segments);
    }

    //------------------------------------------------------------
    // Curve's segments
    //------------------------------------------------------------

    /**
     * Starting at 0.
     */
    public LinearSegment_DNC getSegment(int pos) {
        if (pos < 0 || pos > segments.length - 1) {
            throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
        }
        return segments[pos];
    }

    /**
     * Returns the number of segments in this curve.
     *
     * @return the number of segments
     */
    public int getSegmentCount() {
        return segments.length;
    }

    /**
     * Returns the number of the segment that defines the function
     * value at x-coordinate <code>x</code>. The
     * number of the segment is usually the same as the one
     * returned by <code>getSegmentLimitRight(x)</code>, except for
     * if a segment starts at <code>x</code> and is left-open.
     * In this case the function returns the previous segment,
     * rather than the current segment, as the previous segment
     * defines <code>x</code>.
     *
     * @param x the x-coordinate
     * @return the index of the segment into the array.
     */
    public int getSegmentDefining(Num x) {
        for (int i = segments.length - 1; i >= 0; i--) {
            if (segments[i].isLeftopen()) {
                if (segments[i].getX().lt(x)) {
                    return i;
                }
            } else {
                if (segments[i].getX().leq(x)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the number of the segment that defines the value
     * of the function when computing the limit to the right
     * of the function at x-coordinate <code>x</code>. The
     * number of the segment is usually the same as the one
     * returned by <code>getSegmentDefining(x)</code>, except for
     * if a segment starts at <code>x</code> and is left-open.
     * In this case the function returns the current segment,
     * rather than the previous segment.
     *
     * @param x the x-coordinate
     * @return the index of the segment into the array.
     */
    public int getSegmentLimitRight(Num x) {
        if (x.equals(NumFactory.getPositiveInfinity())) {
            return getSegmentCount();
        }

        for (int i = segments.length - 1; i >= 0; i--) {
            if (segments[i].getX().leq(x)) {
                return i;
            }
        }
        return -1;
    }

    public void setSegment(int pos, LinearSegment s) {
        if (pos < 0 || pos >= segments.length) {
            throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
        }
        if (s == null) {
            throw new IllegalArgumentException("Tried to insert null!");
        }

        LinearSegment_DNC s_dnc;
        if (s instanceof LinearSegment_DNC) {
            s_dnc = ((LinearSegment_DNC) s).copy();
        } else {
            s_dnc = new LinearSegment_DNC(s);
        }

        segments[pos] = s_dnc;
        clearMetaInfo();
    }

    protected void setSegments(LinearSegment[] segments) {
        if (segments instanceof LinearSegment_DNC[]) {
            this.segments = (LinearSegment_DNC[]) segments;
        } else {
            // Convert to LinearSegmentDNC
            this.segments = new LinearSegment_DNC[segments.length];
            for (int i = 0; i < segments.length; i++) {
                segments[i] = new LinearSegment_DNC(segments[i]);
            }
        }
        clearMetaInfo();
    }

    /**
     * Adds a <code>LinearSegment</code> to the end of the curve.<br>
     * Note: It is the user's responsibility to add segments in the
     * order of increasing x-coordinates.
     *
     * @param s the segment to be added.
     */
    public void addSegment(LinearSegment s) {
        addSegment(segments.length, s);
    }

    /**
     * Adds a <code>LinearSegment</code> at the location <code>pos</code>
     * of the curve.<br>
     * Note1; Segments after pos will be pushed back by one position.<br>
     * Note2: It is the user's responsibility to add segments in the
     * order of increasing x-coordinates.
     *
     * @param pos the index into the segment array to add the new segment.
     * @param s   the segment to be added.
     */
    public void addSegment(int pos, LinearSegment s) {
        if (pos < 0 || pos > segments.length) {
            throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
        }
        if (s == null) {
            throw new IllegalArgumentException("Tried to insert null!");
        }

        LinearSegment_DNC s_dnc;
        if (s instanceof LinearSegment_DNC) {
            s_dnc = ((LinearSegment_DNC) s).copy();
        } else {
            s_dnc = new LinearSegment_DNC(s);
        }

        LinearSegment_DNC[] old_segments = segments;
        segments = new LinearSegment_DNC[old_segments.length + 1];
        segments[pos] = s_dnc;
        if (pos > 0) {
            System.arraycopy(old_segments, 0, segments, 0, pos);
        }
        if (pos < old_segments.length) {
            System.arraycopy(old_segments, pos, segments, pos + 1, old_segments.length - pos);
        }

        clearMetaInfo();
    }

    /**
     * Removes the segment at position <code>pos</code>.
     *
     * @param pos the index of the segment to be removed.
     */
    public void removeSegment(int pos) {
        if (pos < 0 || pos >= segments.length) {
            throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
        }
        LinearSegment_DNC[] old_segments = segments;
        segments = new LinearSegment_DNC[old_segments.length - 1];
        System.arraycopy(old_segments, 0, segments, 0, pos);
        System.arraycopy(old_segments, pos + 1, segments, pos, old_segments.length - pos - 1);

        clearMetaInfo();
    }


    //------------------------------------------------------------
    // Curve properties
    //------------------------------------------------------------

    /**
     * Returns whether the inflection point is a (real or unreal) discontinuity.
     *
     * @param pos the index of the IP
     * @return <code>true</code> if the IP is a discontinuity, <code>false</code> if not.
     */
    public boolean isDiscontinuity(int pos) {
        return (pos + 1 < segments.length
                && (NumUtils.abs(
                NumUtils.sub(
                        segments[pos + 1].getX(), segments[pos].getX()
                )
        )).lt(NumFactory.getEpsilon())
        );
    }

    /**
     * Returns whether the inflection point is a real discontinuity, i.e. the y0
     * of the leftopen segment differs from the previous one.
     *
     * @param pos the index of the IP
     * @return <code>true</code> if the IP is a real discontinuity, <code>false</code> if not.
     */
    public boolean isRealDiscontinuity(int pos) {
        return (isDiscontinuity(pos)
                && (NumUtils.abs(
                NumUtils.sub(
                        segments[pos + 1].getY(), segments[pos].getY()
                )
        )).geq(NumFactory.getEpsilon())
        );
    }

    /**
     * Returns whether the inflection point is an unreal discontinuity, i.e. the y0
     * of the leftopen segment is coincident with the y0 of the previous segment
     * and therefore the unreal discontinuity may safely be removed.
     *
     * @param pos the index of the IP
     * @return <code>true</code> if the IP is an unreal discontinuity, <code>false</code> if not.
     */
    public boolean isUnrealDiscontinuity(int pos) {
        return (isDiscontinuity(pos)
                && (NumUtils.abs(
                NumUtils.sub(
                        segments[pos + 1].getY(), segments[pos].getY()
                )
        )).lt(NumFactory.getEpsilon())
        );
    }

    /**
     * Tests whether the curve is wide-sense increasing.
     *
     * @return whether the curve is wide-sense increasing.
     */
    public boolean isWideSenseIncreasing() {
        Num y = NumFactory.getNegativeInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].getY().lt(y) || segments[i].getGrad().lt(NumFactory.getZero())) {
                return false;
            }
            y = segments[i].getY();
        }
        return true;
    }

    /**
     * Tests whether the curve is convex.
     *
     * @return whether the curve is convex.
     */
    public boolean isConvex() {
        return isConvexIn(NumFactory.getZero(), NumFactory.getPositiveInfinity());
    }

    /**
     * Tests whether the curve is convex in [a,b].
     *
     * @param a the lower bound of the test interval.
     * @param b the upper bound of the test interval.
     * @return whether the curve is convex
     */
    public boolean isConvexIn(Num a, Num b) {
        Num last_gradient = NumFactory.getNegativeInfinity();  // No need to create an object as this value is only set for initial comparison in the loop.

        int i_start = getSegmentDefining(a);
        int i_end = getSegmentDefining(b);
        for (int i = i_start; i <= i_end; i++) {
            if (i_start < 0) {
                break;
            }
            if (i == i_end && segments[i].getX() == b) {
                break;
            }
            Num gradient;
            if (i < segments.length - 1) {
                gradient = NumUtils.div(NumUtils.sub(segments[i + 1].getY(), segments[i].getY()),
                        NumUtils.sub(segments[i + 1].getX(), segments[i].getX()));
            } else {
                gradient = segments[i].getGrad();
            }
            if (gradient.lt(last_gradient)) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    /**
     * Tests whether the curve is concave.
     *
     * @return whether the curve is concave.
     */
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
        Num last_gradient = NumFactory.getPositiveInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.

        int i_start = getSegmentDefining(a);
        int i_end = getSegmentDefining(b);
        for (int i = i_start; i <= i_end; i++) {
            if (i == i_end && segments[i].getX() == b) {
                break;
            }
            Num gradient;
            // Handles discontinuities
            if (i < segments.length - 1) {
                gradient = NumUtils.div(NumUtils.sub(segments[i + 1].getY(), segments[i].getY()),
                        NumUtils.sub(segments[i + 1].getX(), segments[i].getX()));
            } else {
                gradient = segments[i].getGrad();
            }
            if (gradient.gt(last_gradient)) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    /**
     * Tests whether the curve is almost concave, i.e. it is concave once its
     * function value is larger than 0.
     *
     * @return whether the curve is almost concave.
     */
    public boolean isAlmostConcave() {
        Num last_gradient = NumFactory.getPositiveInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.

        for (int i = 0; i < segments.length; i++) {
            // Skip the horizontal part at the beginning
            if (last_gradient.equals(NumFactory.getPositiveInfinity()) && segments[i].getGrad().equals(NumFactory.getZero())) {
                continue;
            }

            Num gradient;
            if (i < segments.length - 1) {
                gradient = NumUtils.div(NumUtils.sub(segments[i + 1].getY(), segments[i].getY()),
                        NumUtils.sub(segments[i + 1].getX(), segments[i].getX()));
            } else {
                gradient = segments[i].getGrad();
            }
            if (gradient.gt(last_gradient)) {
                return false;
            }
            last_gradient = gradient;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Curve_DNC)) {
            return false;
        }

        Curve_DNC this_cpy = this.copy();
        Curve_DNC other_cpy = ((Curve_DNC) obj).copy();

        this_cpy.beautify();
        other_cpy.beautify();

        if (this_cpy.getLatency() == NumFactory.getPositiveInfinity()) {
            this_cpy = CurveFactory_DNC.factory_object.createZeroCurve();
        }
        if (other_cpy.getLatency() == NumFactory.getPositiveInfinity()) {
            other_cpy = CurveFactory_DNC.factory_object.createZeroCurve();
        }

        int this_segment_length = this_cpy.segments.length;

        if (this_segment_length != other_cpy.segments.length) {
            return false;
        }

        for (int i = 0; i < this_segment_length; i++) {
            if (!this_cpy.segments[i].equals(other_cpy.segments[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(segments);
    }

    /**
     * Returns a string representation of this curve.
     *
     * @return the curve represented as a string.
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("{");
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                result.append(";");
            }
            result.append(segments[i].toString());
        }
        result.append("}");
        return result.toString();
    }


    //------------------------------------------------------------
    // Curve function values
    //------------------------------------------------------------

    /**
     * Returns the function value at x-coordinate <code>x</code>, if
     * <code>x&gt;=0</code>, and <code>NaN</code> if not.
     *
     * @param x the x-coordinate
     * @return the function value
     */
    public Num f(Num x) {
        int i = getSegmentDefining(x);
        if (i < 0) {
            return NumFactory.createNaN();
        }
        return NumUtils.add(NumUtils.mult(NumUtils.sub(x, segments[i].getX()), segments[i].getGrad()), segments[i].getY());
    }

    /**
     * Returns the limit to the right of the function value at
     * x-coordinate <code>x</code>, if <code>x&gt;=0</code>, and
     * <code>NaN</code> if not.
     *
     * @param x the x-coordinate
     * @return the function value
     */
    public Num fLimitRight(Num x) {
        int i = getSegmentLimitRight(x);
        if (i < 0) {
            return NumFactory.createNaN();
        }
        return NumUtils.add(NumUtils.mult(NumUtils.sub(x, segments[i].getX()), segments[i].getGrad()), segments[i].getY());
    }

    /**
     * Returns the smallest x value at which the function value is
     * equal to <code>y</code>.
     *
     * @param y the y-coordinate
     * @return the smallest x value
     */
    public Num f_inv(Num y) {
        return f_inv(y, false);
    }

    /**
     * Returns the x value at which the function value is
     * equal to <code>y</code>. If <code>rightmost</code> is
     * <code>true</code>, returns the rightmost x-coordinate,
     * otherwise the leftmost coordinate.
     *
     * @param y         The y-coordinate.
     * @param rightmost Return the rightmost x coordinate instaed of the leftmost one (default).
     * @return The smallest x value.
     */
    public Num f_inv(Num y, boolean rightmost) {
        int i = getSegmentFirstAtValue(y);
        if (i < 0) {
            return NumFactory.createNaN();
        }
        if (rightmost) {
            while (i < segments.length && segments[i].getGrad().equals(NumFactory.getZero())) {
                i++;
            }
            if (i >= segments.length) {
                return NumFactory.createPositiveInfinity();
            }
        }
        if (!segments[i].getGrad().equals(NumFactory.getZero())) {
            return NumUtils.add(segments[i].getX(), NumUtils.div(NumUtils.sub(y, segments[i].getY()), segments[i].getGrad()));
        } else {
            return segments[i].getX();
        }
    }

    /**
     * Returns the first segment at which the function reaches the
     * value <code>y</code>. It returns -1 if the curve never
     * reaches this value.
     *
     * @param y the y-coordinate
     * @return the segment number
     */
    private int getSegmentFirstAtValue(Num y) {
        if (segments.length == 0 || segments[0].getY().gt(y)) {
            return -1;
        }
        for (int i = 0; i < segments.length; i++) {
            if (i < segments.length - 1) {
                if (segments[i + 1].getY().geq(y)) {
                    return i;
                }
            } else {
                if (segments[i].getGrad().gt(NumFactory.getZero())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns the x-coordinate of the inflection point after which the function
     * values are greater than zero.
     *
     * @return the latency of this curve.
     */
    public Num getLatency() {
        this.beautify();
        if (segments[0].getY().gt(NumFactory.getZero())) {
            return NumFactory.createZero();
        }
        for (int i = 0; i < segments.length; i++) {
            Num y0 = segments[i].getY();
            if (y0.lt(NumFactory.getZero()) && y0.gt(NumUtils.negate(NumFactory.getEpsilon()))) {
                y0 = NumFactory.createZero();
            }
            if (y0.gt(NumFactory.getZero())
                    || (y0.geq(NumFactory.getZero()) && segments[i].getGrad().gt(NumFactory.getZero()))
                    ) {
                return segments[i].getX();
            }
            if (y0.lt(NumFactory.getZero()) || segments[i].getGrad().lt(NumFactory.getZero())) {
                System.out.println("RemoveLatency of " + this.toString());
                throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
            }
        }

        return NumFactory.createPositiveInfinity();
    }

    public Num getBurst() {
        return fLimitRight(NumFactory.getZero());
    }

    /**
     * Returns the gradient to the right of the function value at
     * x-coordinate <code>x</code>, if <code>x&gt;=0</code>, and
     * <code>NaN</code> if not.
     *
     * @param x the x-coordinate
     * @return the function value
     */
    public Num getGradientLimitRight(Num x) {
        int i = getSegmentLimitRight(x);
        if (i < 0) {
            return NumFactory.createNaN();
        }
        return segments[i].getGrad();
    }

    /**
     * Returns the burstiness of this token bucket curve.<br>
     * Note: For performance reasons there are no sanity checks! Only
     * call this method on a valid token bucket curve!
     *
     * @return the burstiness
     */
    public Num getTB_Burst() {
        if (segments.length == 2) {    // Token Buckets pass through the origin
            return segments[1].getY().copy();
        } else {                        // rate functions have burst 0
            return NumFactory.createZero();
        }
    }

    /**
     * Returns the gradient of the last segment.
     *
     * @return the rate of the ultimately affine part.
     */
    public Num getUltAffineRate() {
        return segments[segments.length - 1].getGrad();
    }


    //------------------------------------------------------------
    // Curve manipulation
    //------------------------------------------------------------

    /**
     * Removes unnecessary segments.
     */
    public void beautify() {
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

    /**
     * Returns the number of rate latency curves the curve can be decomposed into.
     *
     * @return the number of rate latency curves
     */
    public int getRL_ComponentCount() {
        decomposeIntoRateLatencies();
        return rate_latencies.size();
    }

    /**
     * Returns the <code>i</code>the rate latency curve that this curve can be
     * decomposed into.
     *
     * @param i the number of the rate latency curve
     * @return the rate latency curve
     */
    public Curve_DNC getRL_Component(int i) {
        decomposeIntoRateLatencies();
        return rate_latencies.get(i);
    }

    /**
     * Decomposes this curve into a list of rate latency curves and
     * stores this list in the curve's <code>rate_latencies</code> field.<br>
     * Note: Curve must be convex.
     */
    private void decomposeIntoRateLatencies() {
        if (has_rate_latency_meta_info == true) {
            return;
        }

        if (CalculatorConfig.SERVICE_CURVE_CHECKS && !this.isConvex()) {
            if (this.equals(CurveFactory_DNC.factory_object.createZeroDelayInfiniteBurst())) {
                rate_latencies = new ArrayList<Curve_DNC>();
                rate_latencies.add(CurveFactory_DNC.factory_object.createRateLatency(NumFactory.createPositiveInfinity(), NumFactory.createZero()));
            } else {
                throw new RuntimeException("Can only decompose convex service curves into rate latency curves.");
            }
        } else {
            rate_latencies = new ArrayList<Curve_DNC>();
            for (int i = 0; i < segments.length; i++) {
                if (segments[i].getY().eq(0.0) && segments[i].getGrad().eq(0.0)) {
                    continue;
                }
                Num rate = segments[i].getGrad();
                Num latency = NumUtils.sub(segments[i].getX(), NumUtils.div(segments[i].getY(), segments[i].getGrad()));
                if (latency.ltZero()) {
                    continue;
                }
                rate_latencies.add(CurveFactory_DNC.factory_object.createRateLatency(rate, latency));
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

    /**
     * Returns the number of token buckets the curve can be decomposed into.
     *
     * @return the number of token buckets
     */
    public int getTB_ComponentCount() {
        decomposeIntoTokenBuckets();
        return token_buckets.size();
    }

    /**
     * Returns the <code>i</code>the token bucket curve that this curve can be
     * decomposed into.
     *
     * @param i the number of the token bucket
     * @return the token bucket
     */
    public Curve_DNC getTB_Component(int i) {
        decomposeIntoTokenBuckets();
        return token_buckets.get(i);
    }

    /**
     * Decomposes this curve into a list of token bucket curves and
     * stores this list in the curve's <code>token_buckets</code> field.<br>
     * Note: Curve must be concave.
     */
    private void decomposeIntoTokenBuckets() {
        if (has_token_bucket_meta_info == true) {
            return;
        }

        if (CalculatorConfig.ARRIVAL_CURVE_CHECKS && !this.isConcave()) {
            throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
        }

        token_buckets = new ArrayList<Curve_DNC>();
        for (int i = 0; i < segments.length; i++) {
            if (isDiscontinuity(i)) {
                continue;
            }
            Num rate = segments[i].getGrad();
            Num burst = NumUtils.sub(segments[i].getY(), NumUtils.mult(segments[i].getX(), segments[i].getGrad()));
            token_buckets.add(CurveFactory_DNC.factory_object.createTokenBucket(rate, burst));
        }

        is_token_bucket = token_buckets.size() == 1;

        has_token_bucket_meta_info = true;
    }
}