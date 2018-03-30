/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2016 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc.curves.dnc;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
public class Curve_DNC implements CurvePwAffine {
	private static Curve_DNC instance = new Curve_DNC();

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
	protected Curve_DNC() {
		createNewCurve(1, false);
	}

	protected Curve_DNC(CurvePwAffine curve) {
		copy(curve);
	}

	/**
	 * Creates a <code>Curve</code> instance with <code>segment_count</code> empty
	 * <code>LinearSegment</code> instances.
	 *
	 * @param segment_count
	 *            the number of segments
	 */
	protected Curve_DNC(int segment_count) {
		createNewCurve(segment_count, false);
	}

	public static Curve_DNC getFactory() {
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

	public boolean hasTokenBucketMetaInfo() {
		return has_token_bucket_meta_info;
	}

	public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
		this.has_token_bucket_meta_info = has_token_bucket_meta_info;
	}

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
		if (!empty) { // old default
			createZeroSegmentsCurve(segment_count);
		} else { // potential new default, tests work
			segments = new LinearSegment_DNC[segment_count];
			// Initialize Elements of array, not only array itself
			segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
					Num.getFactory().createZero(), false);

			for (int i = 1; i < segment_count; i++) {
				segments[i] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
						Num.getFactory().createZero(), true);
			}
		}
	}

	private void createZeroSegmentsCurve(int segment_count) {
		segments = new LinearSegment_DNC[segment_count];

		if (segment_count == 0) {
			return;
		}

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
				Num.getFactory().createZero(), false);

		for (int i = 1; i < segment_count; i++) {
			segments[i] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
					Num.getFactory().createZero(), true);
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
		segments = new LinearSegment_DNC[segments_to_parse.length]; // No need to use createZeroSegments( i ) because we
		// will store parsed segments

		for (int i = 0; i < segments_to_parse.length; i++) {
			segments[i] = new LinearSegment_DNC(segments_to_parse[i]);
		}
		CurvePwAffine.beautify(this);
	}

	protected void forceThroughOrigin() {
		if (getSegment(0).getY().gtZero()) {
			addSegment(0, new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
					Num.getFactory().createZero(), false));

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
			for (int i = 0; i < segments.length; i++) {
				segments[i] = ((Curve_DNC) curve).getSegment(i).copy();
			}

			this.has_rate_latency_meta_info = ((Curve_DNC) curve).has_rate_latency_meta_info;
			this.rate_latencies = ((Curve_DNC) curve).rate_latencies;

			this.has_token_bucket_meta_info = ((Curve_DNC) curve).has_token_bucket_meta_info;
			this.token_buckets = ((Curve_DNC) curve).token_buckets;

			this.is_delayed_infinite_burst = ((CurvePwAffine) curve).isDelayedInfiniteBurst();
			this.is_rate_latency = ((CurvePwAffine) curve).isRateLatency();
			this.is_token_bucket = ((CurvePwAffine) curve).isTokenBucket();
		} else {
			for (int i = 0; i < curve.getSegmentCount(); i++) {
				segments[i] = new LinearSegment_DNC(curve.getSegment(i));
			}
		}

		setSegments(segments);
	}

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
		if (x.equals(Num.getFactory().getPositiveInfinity())) {
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
	 * @param pos
	 *            the index of the segment to be removed.
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
				&& (Num.getUtils().abs(Num.getUtils().sub(segments[pos + 1].getX(), segments[pos].getX())))
						.lt(Num.getFactory().getEpsilon()));
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
				&& (Num.getUtils().abs(Num.getUtils().sub(segments[pos + 1].getY(), segments[pos].getY())))
						.geq(Num.getFactory().getEpsilon()));
	}

	// ------------------------------------------------------------
	// Curve properties
	// ------------------------------------------------------------

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
				&& (Num.getUtils().abs(Num.getUtils().sub(segments[pos + 1].getY(), segments[pos].getY())))
						.lt(Num.getFactory().getEpsilon()));
	}

	/**
	 * Tests whether the curve is wide-sense increasing.
	 *
	 * @return whether the curve is wide-sense increasing.
	 */
	public boolean isWideSenseIncreasing() {
		Num y = Num.getFactory().getNegativeInfinity(); // No need to create an object as this value is only
		// set for initial comparison in the loop.
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].getY().lt(y) || segments[i].getGrad().lt(Num.getFactory().getZero())) {
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
		return isConvexIn(Num.getFactory().getZero(), Num.getFactory().getPositiveInfinity());
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
		Num last_gradient = Num.getFactory().getNegativeInfinity(); // No need to create an object as this
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
				gradient = Num.getUtils().div(Num.getUtils().sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils().sub(segments[i + 1].getX(), segments[i].getX()));
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
		return isConcaveIn(Num.getFactory().getZero(), Num.getFactory().getPositiveInfinity());
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
		Num last_gradient = Num.getFactory().getPositiveInfinity(); // No need to create an object as this
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
				gradient = Num.getUtils().div(Num.getUtils().sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils().sub(segments[i + 1].getX(), segments[i].getX()));
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
		Num last_gradient = Num.getFactory().getPositiveInfinity(); // No need to create an object as this
		// value is only set for initial
		// comparison in the loop.

		for (int i = 0; i < segments.length; i++) {
			// Skip the horizontal part at the beginning
			if (last_gradient.equals(Num.getFactory().getPositiveInfinity())
					&& segments[i].getGrad().equals(Num.getFactory().getZero())) {
				continue;
			}

			Num gradient;
			if (i < segments.length - 1) {
				gradient = Num.getUtils().div(Num.getUtils().sub(segments[i + 1].getY(), segments[i].getY()),
						Num.getUtils().sub(segments[i + 1].getX(), segments[i].getX()));
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

		CurvePwAffine.beautify(this_cpy);
		CurvePwAffine.beautify(other_cpy);

		if (this_cpy.getLatency() == Num.getFactory().getPositiveInfinity()) {
			this_cpy = this.createZeroCurve();
		}
		if (other_cpy.getLatency() == Num.getFactory().getPositiveInfinity()) {
			other_cpy = this.createZeroCurve();
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
			return Num.getFactory().createNaN();
		}
		return Num.getUtils().add(Num.getUtils().mult(Num.getUtils().sub(x, segments[i].getX()), segments[i].getGrad()),
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
			return Num.getFactory().createNaN();
		}
		return Num.getUtils().add(Num.getUtils().mult(Num.getUtils().sub(x, segments[i].getX()), segments[i].getGrad()),
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
	public Num f_inv(Num y, boolean rightmost) {
		int i = getSegmentFirstAtValue(y);
		if (i < 0) {
			return Num.getFactory().createNaN();
		}
		if (rightmost) {
			while (i < segments.length && segments[i].getGrad().equals(Num.getFactory().getZero())) {
				i++;
			}
			if (i >= segments.length) {
				return Num.getFactory().createPositiveInfinity();
			}
		}
		if (!segments[i].getGrad().equals(Num.getFactory().getZero())) {
			return Num.getUtils().add(segments[i].getX(),
					Num.getUtils().div(Num.getUtils().sub(y, segments[i].getY()), segments[i].getGrad()));
		} else {
			return segments[i].getX();
		}
	}

	/**
	 * Returns the first segment at which the function reaches the value
	 * <code>y</code>. It returns -1 if the curve never reaches this value.
	 *
	 * @param y
	 *            the y-coordinate
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
				if (segments[i].getGrad().gt(Num.getFactory().getZero())) {
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
				return Num.getFactory().createZero();
			}
		} else {
			CurvePwAffine.beautify(this);
			if (segments[0].getY().gt(Num.getFactory().getZero())) {
				return Num.getFactory().createZero();
			}
			for (int i = 0; i < segments.length; i++) {
				Num y0 = segments[i].getY();
				if (y0.lt(Num.getFactory().getZero()) && y0.gt(Num.getUtils().negate(Num.getFactory().getEpsilon()))) {
					y0 = Num.getFactory().createZero();
				}
				if (y0.gt(Num.getFactory().getZero()) || (y0.geq(Num.getFactory().getZero())
						&& segments[i].getGrad().gt(Num.getFactory().getZero()))) {
					return segments[i].getX();
				}
				if (y0.lt(Num.getFactory().getZero()) || segments[i].getGrad().lt(Num.getFactory().getZero())) {
					System.out.println("RemoveLatency of " + this.toString());
					throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
				}
			}
			return Num.getFactory().createPositiveInfinity();
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
				return Num.getFactory().createZero();
			}
		} else {
			return fLimitRight(Num.getFactory().getZero());
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
			return Num.getFactory().createNaN();
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
	public Curve_DNC getRL_Component(int i) {
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

		if (Calculator.getInstance().exec_service_curve_checks() && !this.isConvex()) {
			if (this.equals(this.createZeroDelayInfiniteBurst())) {
				rate_latencies = new ArrayList<Curve_DNC>();
				rate_latencies.add(this.createRateLatency(Num.getFactory().createPositiveInfinity(),
						Num.getFactory().createZero()));
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
				Num latency = Num.getUtils().sub(segments[i].getX(),
						Num.getUtils().div(segments[i].getY(), segments[i].getGrad()));
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
	public Curve_DNC getTB_Component(int i) {
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

		if (Calculator.getInstance().exec_arrival_curve_checks() && !this.isConcave()) {
			throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
		}

		token_buckets = new ArrayList<Curve_DNC>();
		for (int i = 0; i < segments.length; i++) {
			if (isDiscontinuity(i)) {
				continue;
			}
			Num rate = segments[i].getGrad();
			Num burst = Num.getUtils().sub(segments[i].getY(),
					Num.getUtils().mult(segments[i].getX(), segments[i].getGrad()));
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
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public Curve_DNC createCurve(List<LinearSegment> segments) {
		Curve_DNC c_dnc = new Curve_DNC(segments.size());
		for (int i = 0; i < segments.size(); i++) {
			c_dnc.setSegment(i, segments.get(i));
		}
		CurvePwAffine.beautify(c_dnc);
		return c_dnc;
	}

	public Curve_DNC createZeroCurve() {
		return new Curve_DNC(); // CurveDNC constructor's default behavior
	}

	public Curve_DNC createHorizontal(double y) {
		return createHorizontal(Num.getFactory().create(y));
	}

	/**
	 * Creates a horizontal curve.
	 *
	 * @param y
	 *            the y-intercept of the curve
	 * @return a <code>Curve</code> instance
	 */
	public Curve_DNC createHorizontal(Num y) {
		Curve_DNC c_dnc = new Curve_DNC();
		makeHorizontal(c_dnc, y);
		return c_dnc;
	}

	// ------------------------------------------------------------------------------
	// Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public ServiceCurve_DNC createServiceCurve() {
		return new ServiceCurve_DNC();
	}

	public ServiceCurve_DNC createServiceCurve(int segment_count) {
		return new ServiceCurve_DNC(segment_count);
	}

	public ServiceCurve_DNC createServiceCurve(String service_curve_str) throws Exception {
		return new ServiceCurve_DNC(service_curve_str);
	}

	public ServiceCurve_DNC createServiceCurve(CurvePwAffine curve) {
		return new ServiceCurve_DNC(curve);
	}

	public ServiceCurve_DNC createZeroService() {
		return new ServiceCurve_DNC(); // ServiceCurveDNC constructor's default behavior
	}

	/**
	 * Creates an infinite burst curve with zero delay.
	 *
	 * @return a <code>ServiceCurve</code> instance
	 */
	public ServiceCurve_DNC createZeroDelayInfiniteBurst() {
		return createDelayedInfiniteBurst(Num.getFactory().createZero());
	}

	public ServiceCurve_DNC createDelayedInfiniteBurst(double delay) {
		return createDelayedInfiniteBurst(Num.getFactory().create(delay));
	}

	public ServiceCurve_DNC createDelayedInfiniteBurst(Num delay) {
		ServiceCurve_DNC sc_dnc = new ServiceCurve_DNC();
		makeDelayedInfiniteBurst(sc_dnc, delay);
		return sc_dnc;
	}

	public ServiceCurve_DNC createRateLatency(double rate, double latency) {
		return createRateLatency(Num.getFactory().create(rate), Num.getFactory().create(latency));
	}

	public ServiceCurve_DNC createRateLatency(Num rate, Num latency) {
		ServiceCurve_DNC sc_dnc = new ServiceCurve_DNC();
		makeRateLatency(sc_dnc, rate, latency);
		return sc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public ArrivalCurve_DNC createArrivalCurve() {
		return new ArrivalCurve_DNC();
	}

	public ArrivalCurve_DNC createArrivalCurve(int segment_count) {
		return new ArrivalCurve_DNC(segment_count);
	}

	public ArrivalCurve_DNC createArrivalCurve(String arrival_curve_str) throws Exception {
		return new ArrivalCurve_DNC(arrival_curve_str);
	}

	public ArrivalCurve_DNC createArrivalCurve(CurvePwAffine curve) {
		return new ArrivalCurve_DNC(curve);
	}

	public ArrivalCurve_DNC createArrivalCurve(CurvePwAffine curve, boolean remove_latency) {
		return createArrivalCurve(CurvePwAffine.removeLatency(curve));
	}

	public ArrivalCurve_DNC createZeroArrivals() {
		return new ArrivalCurve_DNC(); // ArrivalCurveDNC constructor's default behavior
	}

	public ArrivalCurve_DNC createPeakArrivalRate(double rate) {
		return createPeakArrivalRate(Num.getFactory().create(rate));
	}

	public ArrivalCurve_DNC createPeakArrivalRate(Num rate) {
		ArrivalCurve_DNC ac_dnc = new ArrivalCurve_DNC();
		makePeakRate(ac_dnc, rate);
		return ac_dnc;
	}

	public ArrivalCurve_DNC createTokenBucket(double rate, double burst) {
		return createTokenBucket(Num.getFactory().create(rate), Num.getFactory().create(burst));
	}

	public ArrivalCurve_DNC createTokenBucket(Num rate, Num burst) {
		ArrivalCurve_DNC ac_dnc = new ArrivalCurve_DNC();
		makeTokenBucket(ac_dnc, rate, burst);
		return ac_dnc;
	}

	// ------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public MaxServiceCurve_DNC createMaxServiceCurve() {
		return new MaxServiceCurve_DNC();
	}

	public MaxServiceCurve_DNC createMaxServiceCurve(int segment_count) {
		return new MaxServiceCurve_DNC(segment_count);
	}

	public MaxServiceCurve_DNC createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return new MaxServiceCurve_DNC(max_service_curve_str);
	}

	public MaxServiceCurve_DNC createMaxServiceCurve(CurvePwAffine curve) {
		return new MaxServiceCurve_DNC(curve);
	}

	public MaxServiceCurve_DNC createInfiniteMaxService() {
		return createDelayedInfiniteBurstMSC(Num.getFactory().createZero());
	}

	public MaxServiceCurve_DNC createZeroDelayInfiniteBurstMSC() {
		return createDelayedInfiniteBurstMSC(Num.getFactory().createZero());
	}

	public MaxServiceCurve_DNC createDelayedInfiniteBurstMSC(double delay) {
		return createDelayedInfiniteBurstMSC(Num.getFactory().create(delay));
	}

	public MaxServiceCurve_DNC createDelayedInfiniteBurstMSC(Num delay) {
		MaxServiceCurve_DNC msc_dnc = new MaxServiceCurve_DNC();
		makeDelayedInfiniteBurst(msc_dnc, delay);
		return msc_dnc;
	}

	public MaxServiceCurve_DNC createRateLatencyMSC(double rate, double latency) {
		return createRateLatencyMSC(Num.getFactory().create(rate), Num.getFactory().create(latency));
	}

	public MaxServiceCurve_DNC createRateLatencyMSC(Num rate, Num latency) {
		MaxServiceCurve_DNC msc_dnc = new MaxServiceCurve_DNC();
		makeRateLatency(msc_dnc, rate, latency);
		return msc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Curve assembly
	// ------------------------------------------------------------------------------
	private void makeHorizontal(Curve_DNC c_dnc, Num y) {
		LinearSegment_DNC segment = new LinearSegment_DNC(Num.getFactory().createZero(), y,
				Num.getFactory().createZero(), false);
		c_dnc.setSegments(new LinearSegment_DNC[] { segment });
	}

	private void makeDelayedInfiniteBurst(Curve_DNC c_dnc, Num delay) {
		if (delay.ltZero()) {
			throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
		}

		LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
				Num.getFactory().createZero(), false);

		segments[1] = new LinearSegment_DNC(delay, Num.getFactory().createPositiveInfinity(),
				Num.getFactory().createZero(), true);

		c_dnc.setSegments(segments);
		c_dnc.is_delayed_infinite_burst = true;
	}

	private void makePeakRate(Curve_DNC c_dnc, Num rate) {
		if (rate.equals(Num.getFactory().getPositiveInfinity())) {
			throw new IllegalArgumentException(
					"Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
		}
		if (rate.eqZero()) {
			makeHorizontal(c_dnc, Num.getFactory().createZero());
			return;
		}

		LinearSegment_DNC[] segments = new LinearSegment_DNC[1];

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(), rate, false);

		c_dnc.setSegments(segments);
		c_dnc.is_rate_latency = true; // with latency 0
		c_dnc.is_token_bucket = true; // with burstiness 0
	}

	private void makeRateLatency(Curve_DNC c_dnc, Num rate, Num latency) {
		if (rate.equals(Num.getFactory().getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, latency);
			return;
		}
		if (rate.eqZero() || latency.equals(Num.getFactory().getPositiveInfinity())) {
			makeHorizontal(c_dnc, Num.getFactory().createZero());
			return;
		}
		if (latency.leqZero()) {
			makePeakRate(c_dnc, rate);
			return;
		}

		LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
				Num.getFactory().createZero(), false);

		segments[1] = new LinearSegment_DNC(latency, Num.getFactory().createZero(), rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_rate_latency = true;
	}

	private void makeTokenBucket(Curve_DNC c_dnc, Num rate, Num burst) {
		if (rate.equals(Num.getFactory().getPositiveInfinity())
				|| burst.equals(Num.getFactory().getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, Num.getFactory().createZero());
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

		LinearSegment_DNC[] segments = new LinearSegment_DNC[2];

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
				Num.getFactory().createZero(), false);

		segments[1] = new LinearSegment_DNC(Num.getFactory().createZero(), burst, rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_token_bucket = true;
	}
}
