/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2016 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.curves.disco.pw_affine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.CurveFactory_Affine;
import org.networkcalculus.dnc.curves.CurveUtils;
import org.networkcalculus.dnc.curves.Curve_Affine;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.Curve_PwAffine;
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.disco.Curves_Disco_Configuration;
import org.networkcalculus.dnc.curves.disco.LinearSegment_Disco;
import org.networkcalculus.num.Num;

/**
 * Class representing a piecewise linear curve, defined on [0,inf).<br>
 * The curve is stored as an array of <code>LinearSegment</code> objects. Each
 * of these objects defines a linear piece of the curve from one inflection
 * point up to, but not including, the next. It is possible to define
 * discontinuities by defining two subsequent <code>LinearSegment</code>
 * instances which start at the same inflection point. In this case, the second
 * segment needs to have <code>leftopen</code> set to <code>true</code> to
 * indicate that the inflection point is excluded from the second segment.<br>
 * All arithmetic operations on a curve return a new instance of class
 * <code>Curve</code>.<br>
 */
public class Curve_Disco_PwAffine implements Curve_PwAffine, CurveFactory_Affine {
	private static Curve_Disco_PwAffine instance = new Curve_Disco_PwAffine();
	private static CurveUtils utils = Calculator.getInstance().getDncBackend().getCurveUtils();

	protected LinearSegment_Disco[] segments;

	protected boolean is_delayed_infinite_burst = false;

	protected boolean is_rate_latency = false;
	protected boolean has_rate_latency_meta_info = false;
	protected List<Curve_Disco_PwAffine> rate_latencies = new LinkedList<Curve_Disco_PwAffine>();

	protected boolean is_token_bucket = false;
	protected boolean has_token_bucket_meta_info = false;
	protected List<Curve_Disco_PwAffine> token_buckets = new LinkedList<Curve_Disco_PwAffine>();

	/**
	 * Creates a <code>CurveDNC</code> instance with a single segment on the x-axis.
	 */
	protected Curve_Disco_PwAffine() {
		createNewCurve(1, false);
	}

	protected Curve_Disco_PwAffine(Curve curve) {
		copy(curve);
	}

	/**
	 * Creates a <code>Curve</code> instance with <code>segment_count</code> empty
	 * <code>LinearSegment</code> instances.
	 *
	 * @param segment_count
	 *            the number of segments
	 */
	protected Curve_Disco_PwAffine(int segment_count) {
		createNewCurve(segment_count, false);
	}

	public static CurveFactory_Affine getFactory() {
		return instance;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Interface Implementations
	// --------------------------------------------------------------------------------------------------------------

	public boolean isRateLatency() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	public boolean isTokenBucket() {
		decomposeIntoTokenBuckets();
		return is_token_bucket;
	}

	public boolean hasRateLatencyMetaInfo() {
		return has_rate_latency_meta_info;
	}

	public void setRL_MetaInfo(boolean has_rate_latency_meta_info) {
		this.has_rate_latency_meta_info = has_rate_latency_meta_info;
	}

	public List<Curve_Affine> getRL_Components() {
		List<Curve_Affine> tmp = new LinkedList<>();
		if (this.is_rate_latency) {
			tmp.add(this.copy());
		} else {
			for (int i = 0; i < rate_latencies.size(); i++) {
				tmp.add(rate_latencies.get(i));
			}
		}
		return tmp;
	}

	public void setRL_Components(List<Curve> rate_latencies) {
		List<Curve_Disco_PwAffine> tmp = new LinkedList<>();
		for (int i = 0; i < rate_latencies.size(); i++) {
			tmp.add((Curve_Disco_PwAffine) rate_latencies.get(i));
		}
		this.rate_latencies = tmp;
	}

	public boolean hasTokenBucketMetaInfo() {
		return has_token_bucket_meta_info;
	}

	public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
		this.has_token_bucket_meta_info = has_token_bucket_meta_info;
	}

	public List<Curve_Affine> getTB_Components() {
		List<Curve_Affine> tmp = new LinkedList<>();
		for (int i = 0; i < token_buckets.size(); i++) {
			tmp.add(token_buckets.get(i));
		}
		return tmp;
	}

	public void setTB_Components(List<Curve> token_buckets) {
		List<Curve_Disco_PwAffine> tmp = new LinkedList<>();
		for (int i = 0; i < token_buckets.size(); i++) {
			tmp.add((Curve_Disco_PwAffine) token_buckets.get(i));
		}
		this.token_buckets = tmp;
	}

	private void createNewCurve(int segment_count, boolean empty) {
		if (!empty) { // old default
			createZeroSegmentsCurve(segment_count);
		} else { // potential new default, tests work
			segments = new LinearSegment_Disco[segment_count];
			// Initialize Elements of array, not only array itself
			segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
					Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);

			for (int i = 1; i < segment_count; i++) {
				segments[i] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
						Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), true);
			}
		}
	}

	private void createZeroSegmentsCurve(int segment_count) {
		segments = new LinearSegment_Disco[segment_count];

		if (segment_count == 0) {
			return;
		}

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);

		for (int i = 1; i < segment_count; i++) {
			segments[i] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
					Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), true);
		}
	}

	// Accepts string representations of Curve, ArrivalCurve, ServiceCurve, and
	// MaxServiceCurve
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
		segments = new LinearSegment_Disco[segments_to_parse.length]; // No need to use createZeroSegments( i ) because we
		// will store parsed segments

		for (int i = 0; i < segments_to_parse.length; i++) {
			segments[i] = new LinearSegment_Disco(segments_to_parse[i]);
		}
		utils.beautify(this);
	}

	protected void forceThroughOrigin() {
		if (getSegment(0).getY().gtZero()) {
			addSegment(0, new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
					Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false));

			getSegment(1).setLeftopen(true);
		}
	}

	private void clearMetaInfo() {
		has_token_bucket_meta_info = false;
		is_token_bucket = false;
		token_buckets = new LinkedList<Curve_Disco_PwAffine>();

		has_rate_latency_meta_info = false;
		is_rate_latency = false;
		rate_latencies = new LinkedList<Curve_Disco_PwAffine>();
	}

	/**
	 * Returns a copy of this instance.
	 *
	 * @return a copy of this instance.
	 */
	@Override
	public Curve_Disco_PwAffine copy() {
		Curve_Disco_PwAffine c_copy = new Curve_Disco_PwAffine();
		c_copy.copy(this);
		return c_copy;
	}

	@Override
	public void copy(Curve curve) {
		LinearSegment_Disco[] segments = new LinearSegment_Disco[curve.getSegmentCount()];

		if (curve instanceof Curve_Disco_PwAffine) {
			for (int i = 0; i < segments.length; i++) {
				segments[i] = ((Curve_Disco_PwAffine) curve).getSegment(i).copy();
			}

			this.has_rate_latency_meta_info = ((Curve_Disco_PwAffine) curve).has_rate_latency_meta_info;
			this.rate_latencies.clear();
			this.rate_latencies.addAll(((Curve_Disco_PwAffine) curve).rate_latencies);
			
			this.has_token_bucket_meta_info = ((Curve_Disco_PwAffine) curve).has_token_bucket_meta_info;
			this.token_buckets.clear();
			this.token_buckets.addAll(((Curve_Disco_PwAffine) curve).token_buckets);

			this.is_delayed_infinite_burst = ((Curve_PwAffine) curve).isDelayedInfiniteBurst();
			this.is_rate_latency = ((Curve_PwAffine) curve).isRateLatency();
			this.is_token_bucket = ((Curve_PwAffine) curve).isTokenBucket();
		} else {
			for (int i = 0; i < curve.getSegmentCount(); i++) {
				segments[i] = new LinearSegment_Disco(curve.getSegment(i));
			}
		}

		setSegments(segments);
	}

	/**
	 * Starting at 0.
	 */
	public LinearSegment_Disco getSegment(int pos) {
		if (pos < 0 || pos > segments.length - 1) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ", segments.length="+segments.length+")!");
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

	// ------------------------------------------------------------
	// Curve's segments
	// ------------------------------------------------------------

	/**
	 * Returns the number of the segment that defines the function value at
	 * x-coordinate <code>x</code>. The number of the segment is usually the same as
	 * the one returned by <code>getSegmentLimitRight(x)</code>, except for if a
	 * segment starts at <code>x</code> and is left-open. In this case the function
	 * returns the previous segment, rather than the current segment, as the
	 * previous segment defines <code>x</code>.
	 *
	 * @param x
	 *            the x-coordinate
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
	 * Returns the number of the segment that defines the value of the function when
	 * computing the limit to the right of the function at x-coordinate
	 * <code>x</code>. The number of the segment is usually the same as the one
	 * returned by <code>getSegmentDefining(x)</code>, except for if a segment
	 * starts at <code>x</code> and is left-open. In this case the function returns
	 * the current segment, rather than the previous segment.
	 *
	 * @param x
	 *            the x-coordinate
	 * @return the index of the segment into the array.
	 */
	public int getSegmentLimitRight(Num x) {
		if (x.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
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

		LinearSegment_Disco s_dnc;
		if (s instanceof LinearSegment_Disco) {
			s_dnc = ((LinearSegment_Disco) s).copy();
		} else {
			s_dnc = new LinearSegment_Disco(s);
		}

		segments[pos] = s_dnc;
		clearMetaInfo();
	}

	protected void setSegments(LinearSegment[] segments) {
		if (segments instanceof LinearSegment_Disco[]) {
			this.segments = (LinearSegment_Disco[]) segments;
		} else {
			// Convert to LinearSegmentDNC
			this.segments = new LinearSegment_Disco[segments.length];
			for (int i = 0; i < segments.length; i++) {
				segments[i] = new LinearSegment_Disco(segments[i]);
			}
		}
		clearMetaInfo();
	}

	/**
	 * Adds a <code>LinearSegment</code> to the end of the curve.<br>
	 * Note: It is the user's responsibility to add segments in the order of
	 * increasing x-coordinates.
	 *
	 * @param s
	 *            the segment to be added.
	 */
	public void addSegment(LinearSegment s) {
		addSegment(segments.length, s);
	}

	/**
	 * Adds a <code>LinearSegment</code> at the location <code>pos</code> of the
	 * curve.<br>
	 * Note1; Segments after pos will be pushed back by one position.<br>
	 * Note2: It is the user's responsibility to add segments in the order of
	 * increasing x-coordinates.
	 *
	 * @param pos
	 *            the index into the segment array to add the new segment.
	 * @param s
	 *            the segment to be added.
	 */
	public void addSegment(int pos, LinearSegment s) {
		if (pos < 0 || pos > segments.length) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
		}
		if (s == null) {
			throw new IllegalArgumentException("Tried to insert null!");
		}

		LinearSegment_Disco s_dnc;
		if (s instanceof LinearSegment_Disco) {
			s_dnc = ((LinearSegment_Disco) s).copy();
		} else {
			s_dnc = new LinearSegment_Disco(s);
		}

		LinearSegment_Disco[] old_segments = segments;
		segments = new LinearSegment_Disco[old_segments.length + 1];
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
	 * @param pos
	 *            the index of the segment to be removed.
	 */
	public void removeSegment(int pos) {
		if (pos < 0 || pos >= segments.length) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
		}
		LinearSegment_Disco[] old_segments = segments;
		segments = new LinearSegment_Disco[old_segments.length - 1];
		System.arraycopy(old_segments, 0, segments, 0, pos);
		System.arraycopy(old_segments, pos + 1, segments, pos, old_segments.length - pos - 1);

		clearMetaInfo();
	}

	// ------------------------------------------------------------
	// Curve properties
	// ------------------------------------------------------------

	/**
	 * Returns whether the inflection point is a (real or unreal) discontinuity.
	 *
	 * @param pos
	 *            the index of the IP
	 * @return <code>true</code> if the IP is a discontinuity, <code>false</code> if
	 *         not.
	 */
	public boolean isDiscontinuity(int pos) {
		return (pos + 1 < segments.length
				&& segments[pos + 1].getX().eq(segments[pos].getX()));
	}

	/**
	 * Returns whether the inflection point is a real discontinuity, i.e. the y0 of
	 * the leftopen segment differs from the previous one.
	 *
	 * @param pos
	 *            the index of the IP
	 * @return <code>true</code> if the IP is a real discontinuity,
	 *         <code>false</code> if not.
	 */
	public boolean isRealDiscontinuity(int pos) {
		return (isDiscontinuity(pos) 
				&& !(segments[pos + 1].getY().eq(segments[pos].getY())));
	}

	/**
	 * Returns whether the inflection point is an unreal discontinuity, i.e. the y0
	 * of the leftopen segment is coincident with the y0 of the previous segment and
	 * therefore the unreal discontinuity may safely be removed.
	 *
	 * @param pos
	 *            the index of the IP
	 * @return <code>true</code> if the IP is an unreal discontinuity,
	 *         <code>false</code> if not.
	 */
	public boolean isUnrealDiscontinuity(int pos) {
		return (isDiscontinuity(pos)
				&& segments[pos + 1].getY().eq(segments[pos].getY()));
	}

	/**
	 * Tests whether the curve is wide-sense increasing.
	 *
	 * @return whether the curve is wide-sense increasing.
	 */
	public boolean isWideSenseIncreasing() {
		Num y = Num.getFactory(Calculator.getInstance().getNumBackend()).getNegativeInfinity(); // No need to create an object as this value is only
		// set for initial comparison in the loop.
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].getY().lt(y) || segments[i].getGrad().lt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
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
		return isConvexIn(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity());
	}

	/**
	 * Tests whether the curve is convex in [a,b].
	 *
	 * @param a
	 *            the lower bound of the test interval.
	 * @param b
	 *            the upper bound of the test interval.
	 * @return whether the curve is convex
	 */
	public boolean isConvexIn(Num a, Num b) {
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getNegativeInfinity(); // No need to create an object as this
		// value is only set for initial
		// comparison in the loop.

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
				gradient = Num.getUtils(Calculator.getInstance().getNumBackend()).div(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getX(), segments[i].getX()));
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
		return isConcaveIn(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity());
	}

	/**
	 * Tests whether the curve is concave in [a,b].
	 *
	 * @param a
	 *            the lower bound of the test interval.
	 * @param b
	 *            the upper bound of the test interval.
	 * @return whether the curve is concave.
	 */
	public boolean isConcaveIn(Num a, Num b) {
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity(); // No need to create an object as this
		// value is only set for initial
		// comparison in the loop.

		int i_start = getSegmentDefining(a);
		int i_end = getSegmentDefining(b);
		for (int i = i_start; i <= i_end; i++) {
			if (i == i_end && segments[i].getX() == b) {
				break;
			}
			Num gradient;
			// Handles discontinuities
			if (i < segments.length - 1) {
				gradient = Num.getUtils(Calculator.getInstance().getNumBackend()).div(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getX(), segments[i].getX()));
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
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity(); // No need to create an object as this
		// value is only set for initial
		// comparison in the loop.

		for (int i = 0; i < segments.length; i++) {
			// Skip the horizontal part at the beginning
			if (last_gradient.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
					&& segments[i].getGrad().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
				continue;
			}

			Num gradient;
			if (i < segments.length - 1) {
				gradient = Num.getUtils(Calculator.getInstance().getNumBackend()).div(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i + 1].getX(), segments[i].getX()));
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
		if (obj == null || !(obj instanceof Curve_Disco_PwAffine)) {
			return false;
		}

		Curve_Disco_PwAffine this_cpy = this.copy();
		Curve_Disco_PwAffine other_cpy = ((Curve_Disco_PwAffine) obj).copy();

		utils.beautify(this_cpy);
		utils.beautify(other_cpy);

		if (this_cpy.getLatency() == Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity()) {
			this_cpy = Curve_ConstantPool.ZERO_CURVE.get();
		}
		if (other_cpy.getLatency() == Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity()) {
			other_cpy = Curve_ConstantPool.ZERO_CURVE.get();
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

	/**
	 * Returns the function value at x-coordinate <code>x</code>, if
	 * <code>x&gt;=0</code>, and <code>NaN</code> if not.
	 *
	 * @param x
	 *            the x-coordinate
	 * @return the function value
	 */
	public Num f(Num x) {
		int i = getSegmentDefining(x);
		if (i < 0) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createNaN();
		}
		return Num.getUtils(Calculator.getInstance().getNumBackend()).add(Num.getUtils(Calculator.getInstance().getNumBackend()).mult(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(x, segments[i].getX()), segments[i].getGrad()),
				segments[i].getY());
	}

	/**
	 * Returns the limit to the right of the function value at x-coordinate
	 * <code>x</code>, if <code>x&gt;=0</code>, and <code>NaN</code> if not.
	 *
	 * @param x
	 *            the x-coordinate
	 * @return the function value
	 */
	public Num fLimitRight(Num x) {
		int i = getSegmentLimitRight(x);
		if (i < 0) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createNaN();
		}
		return Num.getUtils(Calculator.getInstance().getNumBackend()).add(Num.getUtils(Calculator.getInstance().getNumBackend()).mult(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(x, segments[i].getX()), segments[i].getGrad()),
				segments[i].getY());
	}

	// ------------------------------------------------------------
	// Curve function values
	// ------------------------------------------------------------

	/**
	 * Returns the smallest x value at which the function value is equal to
	 * <code>y</code>.
	 *
	 * @param y
	 *            the y-coordinate
	 * @return the smallest x value
	 */
	public Num f_inv(Num y) {
		return f_inv(y, false);
	}


	public Num f_inv(Num y, boolean rightmost) {
		if(AnalysisConfig.enforceMultiplexingStatic() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
		{
			return f_invFIFO(y, rightmost);
		}

		else{
			return f_invARB(y, rightmost);
		}
	}


	/**
	 * Returns the x value at which the function value is equal to <code>y</code>.
	 * If <code>rightmost</code> is <code>true</code>, returns the rightmost
	 * x-coordinate, otherwise the leftmost coordinate.
	 *
	 * @param y
	 *            The y-coordinate.
	 * @param rightmost
	 *            Return the rightmost x coordinate instaed of the leftmost one
	 *            (default).
	 * @return The smallest x value.
	 */
	public Num f_invFIFO(Num y, boolean rightmost) {
		int i = getSegmentFirstAtValue(y);
		if (i < 0) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createNaN();
		}

		// if there is a jump, i.e., y is smaller than the y-value of segment i then return the x-coordinate of segment
		// i directly, don't have to distinguish between rightmost/leftmost then:
		// if it is called with !rightmost then the arrival curve is considered and we return the x-val of the segment
		// that actually has a starting y-value bigger than "y". However, this is not a problem since "just" before the
		// point on the previous segment we have a value that is smaller than "y".
		if(y.lt(segments[i].getY()))
		{
			return segments[i].getX();
		}

		// "shifts" i to the rightmost segment which contains value y
		if (rightmost) {

			while (i < segments.length && segments[i].getGrad().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
				i++;
			}
			if (i >= segments.length) {
				return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
			}
		}

		if (!segments[i].getGrad().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
			return Num.getUtils(Calculator.getInstance().getNumBackend()).add(segments[i].getX(),
					Num.getUtils(Calculator.getInstance().getNumBackend()).div(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(y, segments[i].getY()), segments[i].getGrad()));
		} else {
			return segments[i].getX();
		}
	}


	/**
	 * Returns the x value at which the function value is equal to <code>y</code>.
	 * If <code>rightmost</code> is <code>true</code>, returns the rightmost
	 * x-coordinate, otherwise the leftmost coordinate.
	 *
	 * @param y
	 *            The y-coordinate.
	 * @param rightmost
	 *            Return the rightmost x coordinate instaed of the leftmost one
	 *            (default).
	 * @return The smallest x value.
	 */
	public Num f_invARB(Num y, boolean rightmost) {
		int i = getSegmentFirstAtValue(y);
		if (i < 0) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createNaN();
		}
		if (rightmost) {
			while (i < segments.length && segments[i].getGrad().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
				i++;
			}
			if (i >= segments.length) {
				return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
			}
		}
		if (!segments[i].getGrad().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
			return Num.getUtils(Calculator.getInstance().getNumBackend()).add(segments[i].getX(),
					Num.getUtils(Calculator.getInstance().getNumBackend()).div(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(y, segments[i].getY()), segments[i].getGrad()));
		} else {
			return segments[i].getX();
		}
	}

	private int getSegmentFirstAtValue(Num y) {
		if(AnalysisConfig.enforceMultiplexingStatic() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
		{
			return getSegmentFirstAtValueFIFO(y);
		}

		else{
			return getSegmentFirstAtValueARB(y);
		}
	}

	/**
	 * Returns the first segment at which the function reaches the value
	 * <code>y</code>. If there is a jump and <code>y</code> happens to
	 * be between the "end" of the y-values of segment i and "start" of
	 * segment i+1, then it returns i+1.
	 * It returns -1 if the curve never reaches this value.
	 *
	 * @param y
	 *            the y-coordinate
	 * @return the segment number
	 */
	private int getSegmentFirstAtValueFIFO(Num y) {
		if (segments.length == 0 || segments[0].getY().gt(y)) {
			return -1;
		}
		for (int i = 0; i < segments.length; i++) {
			if (i < segments.length - 1) {
				if (segments[i + 1].getY().geq(y)) {
					// have to check for a jump
					Num delta = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i+1].getX(), segments[i].getX());
					Num delta_times_slope = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(delta, segments[i].getGrad());
					Num v = Num.getUtils(Calculator.getInstance().getNumBackend()).add(delta_times_slope, segments[i].getY());

					if(v.lt(y))
					{
						return i+1;
					}

					else
					{
						return i;
					}
				}
			} else {
				// i is the last segment
				if(segments.length > 1)
				{
					if (segments[i].getGrad().gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
						return i;
					}
				}

				else{
					// this curve has only one segment
					if(segments[i].getY().geq(y) ||  ( segments[i].getGrad().gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()) ))
					{
						return i;
					}

					else{
						return -1;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the first segment at which the function reaches the value
	 * <code>y</code>. It returns -1 if the curve never reaches this value.
	 *
	 * @param y
	 *            the y-coordinate
	 * @return the segment number
	 */
	private int getSegmentFirstAtValueARB(Num y) {
		if (segments.length == 0 || segments[0].getY().gt(y)) {
			return -1;
		}
		for (int i = 0; i < segments.length; i++) {
			if (i < segments.length - 1) {
				if (segments[i + 1].getY().geq(y)) {
					return i;
				}
			} else {
				if (segments[i].getGrad().gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
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
		if (isRateLatency()) {
			if (segments.length == 2) { // Rate latency other than a simple rate function
				return segments[1].getX().copy();
			} else { // Single-segment rate functions have latency 0
				return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
			}
		} else {
			utils.beautify(this);
			if (segments[0].getY().gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
				return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
			}
			for (int i = 0; i < segments.length; i++) {
				Num y0 = segments[i].getY();
				if (y0.lt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
					y0 = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
				}
				if (y0.gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()) || (y0.geq(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())
						&& segments[i].getGrad().gt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()))) {
					return segments[i].getX();
				}
				if (y0.lt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero()) || segments[i].getGrad().lt(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero())) {
					System.out.println("RemoveLatency of " + this.toString());
					throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
				}
			}
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
		}
	}

	/**
	 * @return the burstiness
	 */
	public Num getBurst() {
		if (isTokenBucket()) {
			if (segments.length == 2) { // Token buckets with spot in the origin
				return segments[1].getY().copy();
			} else { // Single-segment peak rate functions have burstiness 0
				return Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
			}
		} else {
			return fLimitRight(Num.getFactory(Calculator.getInstance().getNumBackend()).getZero());
		}
	}

	/**
	 * Returns the gradient to the right of the function value at x-coordinate
	 * <code>x</code>, if <code>x&gt;=0</code>, and <code>NaN</code> if not.
	 *
	 * @param x
	 *            the x-coordinate
	 * @return the function value
	 */
	public Num getGradientLimitRight(Num x) {
		int i = getSegmentLimitRight(x);
		if (i < 0) {
			return Num.getFactory(Calculator.getInstance().getNumBackend()).createNaN();
		}
		return segments[i].getGrad();
	}

	/**
	 * Returns the gradient of the last segment.
	 *
	 * @return the rate of the ultimately affine part.
	 */
	public Num getUltAffineRate() {
		return segments[segments.length - 1].getGrad();
	}

	// ------------------------------------------------------------
	// Specific curve shapes
	// ------------------------------------------------------------
	// Burst delay
	public boolean isDelayedInfiniteBurst() {
		return is_delayed_infinite_burst;
	}

	// Rate latency
	public boolean getRL_Property() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	public void setRateLateny(boolean is_rate_latency) {
		this.is_rate_latency = is_rate_latency;
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
	 * @param i
	 *            the number of the rate latency curve
	 * @return the rate latency curve
	 */
	public Curve_Disco_PwAffine getRL_Component(int i) {
		decomposeIntoRateLatencies();
		return rate_latencies.get(i);
	}

	/**
	 * Decomposes this curve into a list of rate latency curves and stores this list
	 * in the curve's <code>rate_latencies</code> field.<br>
	 * Note: Curve must be convex.
	 */
	private void decomposeIntoRateLatencies() {
		if (has_rate_latency_meta_info == true) {
			return;
		}

		if (Curves_Disco_Configuration.getInstance().exec_service_curve_checks() && !this.isConvex()) {
			if (this.equals(this.createZeroDelayInfiniteBurst())) {
				rate_latencies = new ArrayList<Curve_Disco_PwAffine>();
				rate_latencies.add(this.createRateLatency(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity(),
						Num.getFactory(Calculator.getInstance().getNumBackend()).createZero()));
			} else {
				throw new RuntimeException("Can only decompose convex service curves into rate latency curves.");
			}
		} else {
			rate_latencies = new ArrayList<Curve_Disco_PwAffine>();
			for (int i = 0; i < segments.length; i++) {
				if (segments[i].getY().eq(0.0) && segments[i].getGrad().eq(0.0)) {
					continue;
				}
				Num rate = segments[i].getGrad();
				Num latency = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i].getX(),
						Num.getUtils(Calculator.getInstance().getNumBackend()).div(segments[i].getY(), segments[i].getGrad()));
				if (latency.ltZero()) {
					continue;
				}
				rate_latencies.add(this.createRateLatency(rate, latency));
			}
		}

		is_rate_latency = rate_latencies.size() == 1;

		has_rate_latency_meta_info = true;
	}

	public void setTokenBucket(boolean is_token_bucket) {
		this.is_token_bucket = is_token_bucket;
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
	 * @param i
	 *            the number of the token bucket
	 * @return the token bucket
	 */
	public Curve_Disco_PwAffine getTB_Component(int i) {
		decomposeIntoTokenBuckets();
		return token_buckets.get(i);
	}

	/**
	 * Decomposes this curve into a list of token bucket curves and stores this list
	 * in the curve's <code>token_buckets</code> field.<br>
	 * Note: Curve must be concave.
	 */
	private void decomposeIntoTokenBuckets() {
		if (has_token_bucket_meta_info == true) {
			return;
		}

		if (Curves_Disco_Configuration.getInstance().exec_arrival_curve_checks() && !this.isConcave()) {
			throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
		}

		token_buckets = new ArrayList<Curve_Disco_PwAffine>();
		for (int i = 0; i < segments.length; i++) {
			if (isDiscontinuity(i)) {
				continue;
			}
			Num rate = segments[i].getGrad();
			Num burst = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(segments[i].getY(),
					Num.getUtils(Calculator.getInstance().getNumBackend()).mult(segments[i].getX(), segments[i].getGrad()));
			token_buckets.add(this.createTokenBucket(rate, burst));
		}

		is_token_bucket = token_buckets.size() == 1;

		has_token_bucket_meta_info = true;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Factory Implementation
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------------------------
	// Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	public Curve_Disco_PwAffine createCurve(List<LinearSegment> segments) {
		Curve_Disco_PwAffine c_dnc = new Curve_Disco_PwAffine(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			c_dnc.setSegment(i, segments.get(i));
		}
		utils.beautify(c_dnc);
		return c_dnc;
	}

	public Curve_Disco_PwAffine createZeroCurve() {
		return new Curve_Disco_PwAffine(); // CurveDNC constructor's default behavior
	}

	/**
	 * Creates a horizontal curve.
	 *
	 * @param y
	 *            the y-intercept of the curve
	 * @return a <code>Curve</code> instance
	 */
	public Curve_Disco_PwAffine createHorizontal(Num y) {
		Curve_Disco_PwAffine c_dnc = new Curve_Disco_PwAffine();
		makeHorizontal(c_dnc, y);
		return c_dnc;
	}

	// ------------------------------------------------------------------------------
	// Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	public ServiceCurve_Disco_PwAffine createServiceCurve() {
		return new ServiceCurve_Disco_PwAffine();
	}

	public ServiceCurve_Disco_PwAffine createServiceCurve(int segment_count) {
		return new ServiceCurve_Disco_PwAffine(segment_count);
	}

	public ServiceCurve_Disco_PwAffine createServiceCurve(String service_curve_str) throws Exception {
		return new ServiceCurve_Disco_PwAffine(service_curve_str);
	}

	public ServiceCurve_Disco_PwAffine createServiceCurve(Curve curve) {
		return new ServiceCurve_Disco_PwAffine(curve);
	}

	public ServiceCurve_Disco_PwAffine createZeroService() {
		return new ServiceCurve_Disco_PwAffine(); // ServiceCurveDNC constructor's default behavior
	}

	/**
	 * Creates an infinite burst curve with zero delay.
	 *
	 * @return a <code>ServiceCurve</code> instance
	 */
	public ServiceCurve_Disco_PwAffine createZeroDelayInfiniteBurst() {
		return createDelayedInfiniteBurst(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	public ServiceCurve_Disco_PwAffine createDelayedInfiniteBurst(double delay) {
		return createDelayedInfiniteBurst(Num.getFactory(Calculator.getInstance().getNumBackend()).create(delay));
	}

	public ServiceCurve_Disco_PwAffine createDelayedInfiniteBurst(Num delay) {
		ServiceCurve_Disco_PwAffine sc_dnc = new ServiceCurve_Disco_PwAffine();
		makeDelayedInfiniteBurst(sc_dnc, delay);
		return sc_dnc;
	}

	public ServiceCurve_Disco_PwAffine createRateLatency(double rate, double latency) {
		return createRateLatency(Num.getFactory(Calculator.getInstance().getNumBackend()).create(rate), Num.getFactory(Calculator.getInstance().getNumBackend()).create(latency));
	}

	public ServiceCurve_Disco_PwAffine createRateLatency(Num rate, Num latency) {
		ServiceCurve_Disco_PwAffine sc_dnc = new ServiceCurve_Disco_PwAffine();
		makeRateLatency(sc_dnc, rate, latency);
		return sc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	public ArrivalCurve_Disco_PwAffine createArrivalCurve() {
		return new ArrivalCurve_Disco_PwAffine();
	}

	public ArrivalCurve_Disco_PwAffine createArrivalCurve(int segment_count) {
		return new ArrivalCurve_Disco_PwAffine(segment_count);
	}

	public ArrivalCurve_Disco_PwAffine createArrivalCurve(String arrival_curve_str) throws Exception {
		return new ArrivalCurve_Disco_PwAffine(arrival_curve_str);
	}

	public ArrivalCurve_Disco_PwAffine createArrivalCurve(Curve curve) {
		return new ArrivalCurve_Disco_PwAffine(curve);
	}

	public ArrivalCurve_Disco_PwAffine createArrivalCurve(Curve curve, boolean remove_latency) {
		return createArrivalCurve(utils.removeLatency(curve));
	}

	public ArrivalCurve_Disco_PwAffine createZeroArrivals() {
		return new ArrivalCurve_Disco_PwAffine(); // ArrivalCurveDNC constructor's default behavior
	}

	public ArrivalCurve_Disco_PwAffine createInfiniteArrivals() {
		ArrivalCurve_Disco_PwAffine ac_dnc = new ArrivalCurve_Disco_PwAffine(); 
		makeDelayedInfiniteBurst(ac_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
		return ac_dnc;
	}

	public ArrivalCurve_Disco_PwAffine createPeakArrivalRate(double rate) {
		return createPeakArrivalRate(Num.getFactory(Calculator.getInstance().getNumBackend()).create(rate));
	}

	public ArrivalCurve_Disco_PwAffine createPeakArrivalRate(Num rate) {
		ArrivalCurve_Disco_PwAffine ac_dnc = new ArrivalCurve_Disco_PwAffine();
		makePeakRate(ac_dnc, rate);
		return ac_dnc;
	}

	public ArrivalCurve_Disco_PwAffine createTokenBucket(double rate, double burst) {
		return createTokenBucket(Num.getFactory(Calculator.getInstance().getNumBackend()).create(rate), Num.getFactory(Calculator.getInstance().getNumBackend()).create(burst));
	}

	public ArrivalCurve_Disco_PwAffine createTokenBucket(Num rate, Num burst) {
		ArrivalCurve_Disco_PwAffine ac_dnc = new ArrivalCurve_Disco_PwAffine();
		makeTokenBucket(ac_dnc, rate, burst);
		return ac_dnc;
	}

	// ------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	public MaxServiceCurve_Disco_PwAffine createMaxServiceCurve() {
		return new MaxServiceCurve_Disco_PwAffine();
	}

	public MaxServiceCurve_Disco_PwAffine createMaxServiceCurve(int segment_count) {
		return new MaxServiceCurve_Disco_PwAffine(segment_count);
	}

	public MaxServiceCurve_Disco_PwAffine createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return new MaxServiceCurve_Disco_PwAffine(max_service_curve_str);
	}

	public MaxServiceCurve_Disco_PwAffine createMaxServiceCurve(Curve curve) {
		return new MaxServiceCurve_Disco_PwAffine(curve);
	}

	public MaxServiceCurve_Disco_PwAffine createZeroDelayInfiniteBurstMSC() {
		return createDelayedInfiniteBurstMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	public MaxServiceCurve_Disco_PwAffine createDelayedInfiniteBurstMSC(double delay) {
		return createDelayedInfiniteBurstMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).create(delay));
	}

	public MaxServiceCurve_Disco_PwAffine createDelayedInfiniteBurstMSC(Num delay) {
		MaxServiceCurve_Disco_PwAffine msc_dnc = new MaxServiceCurve_Disco_PwAffine();
		makeDelayedInfiniteBurst(msc_dnc, delay);
		return msc_dnc;
	}

	public MaxServiceCurve_Disco_PwAffine createRateLatencyMSC(double rate, double latency) {
		return createRateLatencyMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).create(rate), Num.getFactory(Calculator.getInstance().getNumBackend()).create(latency));
	}

	public MaxServiceCurve_Disco_PwAffine createRateLatencyMSC(Num rate, Num latency) {
		MaxServiceCurve_Disco_PwAffine msc_dnc = new MaxServiceCurve_Disco_PwAffine();
		makeRateLatency(msc_dnc, rate, latency);
		return msc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Curve assembly
	// ------------------------------------------------------------------------------
	private void makeHorizontal(Curve_Disco_PwAffine c_dnc, Num y) {
		LinearSegment_Disco segment = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), y,
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);
		c_dnc.setSegments(new LinearSegment_Disco[] { segment });
	}

	private void makeDelayedInfiniteBurst(Curve_Disco_PwAffine c_dnc, Num delay) {
		if (delay.ltZero()) {
			throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[2];

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);

		segments[1] = new LinearSegment_Disco(delay, Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), true);

		c_dnc.setSegments(segments);
		c_dnc.is_delayed_infinite_burst = true;
	}

	private void makePeakRate(Curve_Disco_PwAffine c_dnc, Num rate) {
		if (rate.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
			throw new IllegalArgumentException(
					"Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
		}
		if (rate.eqZero()) {
			makeHorizontal(c_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
			return;
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[1];

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), rate, false);

		c_dnc.setSegments(segments);
		c_dnc.is_rate_latency = true; // with latency 0
		c_dnc.is_token_bucket = true; // with burstiness 0
	}

	private void makeRateLatency(Curve_Disco_PwAffine c_dnc, Num rate, Num latency) {
		if (rate.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, latency);
			return;
		}
		if (rate.eqZero() || latency.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
			makeHorizontal(c_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
			return;
		}
		if (latency.leqZero()) {
			makePeakRate(c_dnc, rate);
			return;
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[2];

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);

		segments[1] = new LinearSegment_Disco(latency, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_rate_latency = true;
	}

	private void makeTokenBucket(Curve_Disco_PwAffine c_dnc, Num rate, Num burst) {
		if (rate.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
				|| burst.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
			return;
		}
		if (rate.eqZero()) { // burst is finite
			makeHorizontal(c_dnc, burst);
			return;
		}
		if (burst.eqZero()) {
			makePeakRate(c_dnc, rate);
			return;
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[2];

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);

		segments[1] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), burst, rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_token_bucket = true;
	}
}
