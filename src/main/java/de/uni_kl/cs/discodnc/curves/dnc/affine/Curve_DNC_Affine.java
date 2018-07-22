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

package de.uni_kl.cs.discodnc.curves.dnc.affine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.Curve_Affine;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.numbers.Num;

/**
 * Class representing a piecewise linear Affine curve, defined on [0,1].<br>
 * The curve is stored as an array of <code>LinearSegment_DNC</code> objects. Each
 * of these objects defines a linear piece of the curve from one inflection
 * point up to, but not including, the next. It is possible to define
 * discontinuities by defining two subsequent <code>LinearSegment_DNC</code>
 * instances which start at the same inflection point. In this case, the second
 * segment needs to have <code>leftopen</code> set to <code>true</code> to
 * indicate that the inflection point is excluded from the second segment.<br>
 * All arithmetic operations on a curve return a new instance of class
 * <code>Curve</code>.<br>
 */
public class Curve_DNC_Affine implements Curve_Affine {
	private static Curve_DNC_Affine instance = new Curve_DNC_Affine();

	protected LinearSegment_DNC[] segments;

	protected boolean is_delayed_infinite_burst = false;

	protected boolean is_rate_latency = false;
	protected boolean has_rate_latency_meta_info = false;
	protected List<Curve_DNC_Affine> rate_latencies = new LinkedList<Curve_DNC_Affine>();

	protected boolean is_token_bucket = false;
	protected boolean has_token_bucket_meta_info = false;
	protected List<Curve_DNC_Affine> token_buckets = new LinkedList<Curve_DNC_Affine>();

	/**
	 * Creates a AffineCurve_DNC instance with 1 segment of type LinearSegment_DNC.
	 * @param
	 *
	 * @return
	 */
	protected Curve_DNC_Affine() {
		createNewCurve(1);
	}

	/**
	 * Creates a copy of <code>AffineCurve_DNC</code> instance, same as the passed <code>curve</code>.
	 * @param
	 *
	 * @return
	 */
	protected Curve_DNC_Affine(Curve curve) {
		copy(curve);
	}

	/**
	 * Creates a <code>AffineCurve_DNC</code> instance with <code>segment_count<code/> number of
	 * <code>LinearSegment_DNC<code/>.
	 * @param segment_count
	 *           Number of segments in the curve.
	 *           In case of Affine cure, at max the number of segments can be Two
	 * @return
	 *           In case of Affine cure, at max the number of segments can be Two
	 */
	protected Curve_DNC_Affine(int segment_count) {
		createNewCurve(segment_count);
	}

	/**
	 * Returns an instance of <code>AffineCurve_DNC</code> with one segment of
	 * <code>LinearSegment_DNC<code/>.
	 * @param
	 * @return
	 * 			An instance of <code>AffineCurve_DNC</code>
	 */
	public static Curve_DNC_Affine getFactory() {
		return instance;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Interface Implementations
	// --------------------------------------------------------------------------------------------------------------
	/**
	 * Verifies weather the curve is RateLatency or not and
	 * Decomposes the cure into rate latency components if it is a Rate latency curve.
	 *
	 * @param
	 *
	 * @return <code>is_rate_latency</code>
	 * 		Returns weather the give curve is Rate latency curve or not
	 */
	public boolean isRateLatency() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	/**
	 * Verifies weather the curve is TokenBucket or not
	 * Decomposes the cure into Token bucket components if it is a Token bucket curve
	 *
	 * @param
	 *
	 * @return <code>is_token_bucket</code>
	 *
	 */
	public boolean isTokenBucket() {
		decomposeIntoTokenBuckets();
		return is_token_bucket;
	}

	/**
	 * Returns weather the curve already has Rate Latency meta info or not
	 *
	 * @param
	 *
	 * @return <>code</>has_rate_latency_meta_info<>code</>
	 * 		Returns weather or not the give curve has Rate latency meta info
	 */
	public boolean hasRateLatencyMetaInfo() {
		return has_rate_latency_meta_info;
	}

	/**
	 * Sets the has_rate_latency_meta_info to the passed arguments
	 *
	 * @param has_rate_latency_meta_info
	 * 		Its a boolean which tells weather the curve has Rate latency metainfo or not
	 *
	 * @return void
	 *
	 */
	public void setRL_MetaInfo(boolean has_rate_latency_meta_info) {
		this.has_rate_latency_meta_info = has_rate_latency_meta_info;
	}

	/**
	 * Returns the Rate latency components of calling curve instance.
	 *
	 * @param
	 *
	 * @return tmp
	 * 		List of type CurveAffine
	 */
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

	/**
	 * Updates the rate_latencies of the calling object with the passed rate_latencies.
	 *
	 * @param rate_latencies
	 * 			List of Curves each of which represent a Rate latency component.
	 * @return void
	 *
	 */
	public void setRL_Components(List<Curve> rate_latencies) {
		List<Curve_DNC_Affine> tmp = new LinkedList<>();
		for (int i = 0; i < rate_latencies.size(); i++) {
			tmp.add((Curve_DNC_Affine) rate_latencies.get(i));
		}
		this.rate_latencies = tmp;
	}

	/**
	 * Returns weather the curve already has Token bucket meta info or not
	 *
	 * @param
	 *
	 * @return has_token_bucket_meta_info
	 * 		Weather or not the give curve has Token bucket meta info
	 */
	public boolean hasTokenBucketMetaInfo() {
		return has_token_bucket_meta_info;
	}

	/**
	 * Update the has_token_bucket_meta_info of the calling object to the passed argument
	 *
	 * @param has_token_bucket_meta_info
	 *
	 * @return
	 *
	 */
	public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
		this.has_token_bucket_meta_info = has_token_bucket_meta_info;
	}

	/**
	 * Returns the Token bucket components of the curve
	 *
	 * @param
	 *
	 * @return tmp
	 * 		A list of Tocken bucket components
	 * 		Note: In this can it should be only one
	 *
	 */
	public List<Curve_Affine> getTB_Components() {
		/*If its a Affine Token bucket curve then there should be only two segments */
		List<Curve_Affine> tmp = new LinkedList<>();
		for (int i = 0; i < token_buckets.size(); i++) {
			tmp.add(token_buckets.get(i));
		}
		return tmp;
	}

	/**
	 * Updates the token_buckets of the calling object with the passed token_buckets components.
	 *
	 * @param token_buckets
	 *		List of Curves, each of them representing a Token bucket component
	 *		Note :  In this case, at max there can be only one token bucket component
	 * @return this.token_buckets
	 * 		updated token_buckets of the calling instance
	 */
	public void setTB_Components(List<Curve> token_buckets) {
	/* TODO	List<AffineCurve_DNC> tmp = new LinkedList<>();
		tmp.add((Curve_DNC_Affine) token_buckets.get(0));*/
				
		List<Curve_DNC_Affine> tmp = new LinkedList<>();
		for (int i = 0; i < token_buckets.size(); i++) {
			tmp.add((Curve_DNC_Affine) token_buckets.get(i));
		}
		this.token_buckets = tmp;
	}
	/**
	 *	Creates an Affine cure with maximum of 2 segments.
	 *
	 * @param segment_count
	 *		Number of segments to be created
	 * @return
	 *
	 */
	private void createNewCurve(int segment_count) {
		if (segment_count < 0 || segment_count > 2) {
			throw new IndexOutOfBoundsException("Affine curves can have at most two segments (given count was "
					+ segment_count + ")!");
		}
		segments = new LinearSegment_DNC[segment_count];
		if(0 == segment_count){
			return;
		}

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
						Num.getFactory().createZero(), false);
		if(segment_count > 1)
		{
				segments[1] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
								Num.getFactory().createZero(), true);

		}

	}

	// Accepts string representations of Curve, ArrivalCurve, ServiceCurve, and
	// MaxServiceCurve
	/**
	 *
	 * @param
	 *
	 * @return
	 *
	 */
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
		// will store parsed segmentst

		for (int i = 0; i < segments_to_parse.length; i++) {
			segments[i] = new LinearSegment_DNC(segments_to_parse[i]);
		}
		Curve.beautify(this);
	}

	/** Add a segment at 0,0 with grad 0 if its not present already
	 * If Affine cure case, the first segment is always at 0,0 and with grad 0
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	protected void forceThroughOrigin() {
		if (getSegment(0).getY().gtZero() || getSegment(0).getX().gtZero()) {
			addSegment(0, new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(),
					Num.getFactory().createZero(), false));

			getSegment(1).setLeftopen(true);
		}
	}

	/**
	 * Resets the Rate latency and Token bucket meta info
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	private void clearMetaInfo() {
		has_token_bucket_meta_info = false;
		is_token_bucket = false;
		token_buckets = new LinkedList<Curve_DNC_Affine>();

		has_rate_latency_meta_info = false;
		is_rate_latency = false;
		rate_latencies = new LinkedList<Curve_DNC_Affine>();
	}

	/**
	 * Returns a copy of this instance.
	 *
	 * @param
	 *
	 * @return  c_copy
	 * 		A copy of this instance.
	 *
	 */
	@Override
	public Curve_DNC_Affine copy() {
		Curve_DNC_Affine c_copy = new Curve_DNC_Affine();
		c_copy.copy(this);
		return c_copy;
	}

	/**
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	@Override
	public void copy(Curve curve) {
		LinearSegment_DNC[] segments = new LinearSegment_DNC[curve.getSegmentCount()];

		if (curve instanceof Curve_DNC_Affine) {
			for (int i = 0; i < segments.length; i++) {
				segments[i] = ((Curve_DNC_Affine) curve).getSegment(i).copy();
			}

			this.has_rate_latency_meta_info = ((Curve_DNC_Affine) curve).has_rate_latency_meta_info;
			this.rate_latencies = ((Curve_DNC_Affine) curve).rate_latencies;

			this.has_token_bucket_meta_info = ((Curve_DNC_Affine) curve).has_token_bucket_meta_info;
			this.token_buckets = ((Curve_DNC_Affine) curve).token_buckets;

			this.is_delayed_infinite_burst = ((Curve_Affine) curve).isDelayedInfiniteBurst();
			this.is_rate_latency = ((Curve_Affine) curve).isRateLatency();
			this.is_token_bucket = ((Curve_Affine) curve).isTokenBucket();
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
	/**
	 * Returns the segment at position pos
	 *
	 * @param pos
	 *		The position of segment in the curve to be fetched.
	 *		Note: In this case, the position can be either 0 or 1
	 * @return
	 *
	 */
	public LinearSegment_DNC getSegment(int pos) {
		if (pos < 0 || pos > segments.length - 1) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
		}
		return segments[pos];
	}

	/**
	 *Returns the number of segments in the curve. At max this can be 2
	 *
	 * @param
	 *
	 * @return segments.length
	 * 		Total number of segments in the curve
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
	 *      The x-coordinate
	 * @return i or -1
	 * 		The index of the segment into the array.
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
	 *   	The x-coordinate
	 * @return i or -1
	 * 		The index of the segment into the array.
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

	/**
	 * Insets the segment at position pos
	 *
	 * @param pos
	 * 		The position at which the segment s has to be inserted.
	 * @param s
	 *		The linear segment to be inserted
	 * @return
	 *
	 */
	public void setSegment(int pos, LinearSegment s) {
		if (pos > 1) {
			throw new IndexOutOfBoundsException("Cannot insert at position " + pos +
					". Affine curves can have at most two segments!");
		}
		if (pos < 0 || pos >= segments.length ) {
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

	/**
	 * Setting the this.segments to the passes segments
	 * 		Note: Since this is an affine curve, the segments count of passed segment cannot exceed 2
	 *
	 * @param segments
	 * 		Segments to be set to this.segments
	 * @return
	 *
	 */
	protected void setSegments(LinearSegment[] segments) {
		if(segments.length > 2){
			throw new IndexOutOfBoundsException("Affine curves can have at most two segments (given count was "
					+ segments.length + ")!");

		}
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
	 * @return
	 *
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
	 * @return
	 *
	 */
	public void addSegment(int pos, LinearSegment s) {

		if (pos > 1) {
			throw new IndexOutOfBoundsException("Cannot insert at position " + pos +
					". Affine curves can have at most two segments!");
		}

		if (pos < 0 || pos > segments.length) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
		}
		if (s == null) {
			throw new IllegalArgumentException("Tried to insert null!");
		}

		if(2 <= segments.length){
			throw new IndexOutOfBoundsException("Cannot add anymore segments. " +
					"Affine curves can have at most two segments!");
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
	 * @return
	 *
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
	 * @param
	 *
	 * @return	True/False
	 * 		whether the curve is wide-sense increasing.
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
	 * Test whether the curve us convex
	 * @param
	 *
	 * @return True/False
	 * 	Returns whether the curve is Convex or not
	 *
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
	 * @param
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
		}
		return true;
	}

	/**
	 * Tests whether the curve is almost concave, i.e. it is concave once its
	 * function value is larger than 0.
	 *
	 * @param
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

	/**
	 * To check whether this object instance is equal to the passing instance
	 *
	 * @param obj
	 * 		Some object.
	 *
	 * @return True/False
	 * 		True if the passed object is similar to calling object
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Curve_DNC_Affine)) {
			return false;
		}

		Curve_DNC_Affine this_cpy = this.copy();
		Curve_DNC_Affine other_cpy = ((Curve_DNC_Affine) obj).copy();

		Curve.beautify(this_cpy);
		Curve.beautify(other_cpy);

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

	/**
	 * To generate the hash code of a curve based on its segments
	 *
	 * @param
	 *
	 * @return Arrays.hashCode(segments)
	 * 		Hash value of this curve
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(segments);
	}

	/**
	 * Returns a string representation of this curve.
	 *
	 * @param
	 *
	 * @return the curve represented as a string.
	 * 		Eg: {(0,0),0;(0,1),1}
	 *
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
	 *
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
	 *
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
	 *
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
	 *
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
	 *
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
	 * @param
	 *
	 * @return the latency of this curve.
	 *
	 */
	public Num getLatency() {
		if (isRateLatency()) {
			if (segments.length == 2) { // Rate latency other than a simple rate function
				return segments[1].getX().copy();
			} else { // Single-segment rate functions have latency 0
				return Num.getFactory().createZero();
			}
		} else {
			Curve.beautify(this);
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
	 * Returns the burst of the cure
	 *
	 * @param
	 *
	 * @return
	 * 	the burstiness
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
	 *
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
	 * @param
	 *
	 * @return the rate of the ultimately affine part.
	 *
	 */
	public Num getUltAffineRate() {
		return segments[segments.length - 1].getGrad();
	}

	// ------------------------------------------------------------
	// Specific curve shapes
	// ------------------------------------------------------------
	/**
	 *	Returns whether or not the curve is delayed infinite burst
	 *
	 * @param
	 *
	 * @return is_delayed_infinite_burst
	 * 	Returns True if the cureve is delayed infinite burst otherwise Flase
	 *
	 */
	public boolean isDelayedInfiniteBurst() {
		return is_delayed_infinite_burst;
	}

	/**
	 * Decomposes the curve into Rate latency components and returns if its a rate latency curve
	 *
	 * @param
	 *
	 * @return is_rate_latency
	 * 		Returns true if the curve has one rate latency component otherwise false
	 *
	 */
	public boolean getRL_Property() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	/**
	 * Setter method for the flag is_rate_latency
	 *
	 * @param is_rate_latency
	 * 		Boolean to be set
	 * @return
	 *
	 */
	public void setRateLateny(boolean is_rate_latency) {
		this.is_rate_latency = is_rate_latency;
	}

	/**
	 * Returns the number of rate latency curves the curve can be decomposed into.
	 *
	 * @param
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
	 *
	 */
	public Curve_DNC_Affine getRL_Component(int i) {
		decomposeIntoRateLatencies();
		return rate_latencies.get(i);
	}

	/**
	 * Decomposes this curve into a list of rate latency curves and stores this list
	 * in the curve's <code>rate_latencies</code> field.<br>
	 * Note: Curve must be convex.
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	private void decomposeIntoRateLatencies() {
		if (has_rate_latency_meta_info == true) {
			return;
		}

		if (Calculator.getInstance().exec_service_curve_checks() && !this.isConvex()) {
			if (this.equals(this.createZeroDelayInfiniteBurst())) {
				rate_latencies = new ArrayList<Curve_DNC_Affine>();
				rate_latencies.add(this.createRateLatency(Num.getFactory().createPositiveInfinity(),
						Num.getFactory().createZero()));
			} else {
				throw new RuntimeException("Can only decompose convex service curves into rate latency curves.");
			}
		} else {
			rate_latencies = new ArrayList<Curve_DNC_Affine>();
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

	/**
	 * Setter method for is_token_bucket flag
	 *
	 * @param  is_token_bucket
	 * 		Boolean representing weather or not its token bucket
	 * @return
	 *
	 */
	public void setTokenBucket(boolean is_token_bucket) {
		this.is_token_bucket = is_token_bucket;
	}

	/**
	 * Returns the number of token buckets the curve can be decomposed into.
	 * 		Note: In this case, it can be maximum 1
	 *
	 * @param
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
	public Curve_DNC_Affine getTB_Component(int i) {
		decomposeIntoTokenBuckets();
		return token_buckets.get(i);
	}

	/**
	 * Decomposes this curve into a list of token bucket curves and stores this list
	 * in the curve's <code>token_buckets</code> field.<br>
	 * Note: Curve must be concave.
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	private void decomposeIntoTokenBuckets() {
		if (has_token_bucket_meta_info == true) {
			return;
		}

		if (Calculator.getInstance().exec_arrival_curve_checks() && !this.isConcave()) {
			throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
		}

		token_buckets = new ArrayList<Curve_DNC_Affine>();
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
	/*
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	public Curve_DNC_Affine createCurve(List<LinearSegment> segments) {
		// Assume there are more than two segments, defining either a valid mTB or mRL.
		// The first segment passes through the origin, the second defines best known lateny or burstiness.
		// The last segment defines least arrival rate or largest service rate, respectively.
		// Choosing the second segment for an affine curve from the given ones is thus finding a reasonable tradeoff.
		// We should trade accuracy for certainty to not cause false resource exhaustion assumptions.
		// I.e., the second segment of our affine curve should be defined by the last one of the givens segments by
		// extending it to the front such that it either intersects with, i.e.,, then starts at,
		// the x-axis (defining a new service curve latency) or the y-axis (defining a new arrival curve burstiness).
		int segment_size = segments.size();
		if(segment_size > 2)
		{
			segment_size = 2;
		}
		//Curve_DNC_Affine c_dnc = new Curve_DNC_Affine(segments.size());
		Curve_DNC_Affine c_dnc = new Curve_DNC_Affine(segment_size);
		for (int i = 0; i < segment_size; i++) {
			LinearSegment s = null;
			if(i == 0) {
				s = segments.get(i);
			}
			else
			{
				s = segments.get( segments.size() - 1);
				Num temp = Num.getUtils().create(-1);
				Num x2 = Num.getUtils().add(Num.getUtils().mult(Num.getUtils().div(s.getY(), s.getGrad()), temp), s.getX());
				Num y2 = Num.getUtils().add(Num.getUtils().mult(Num.getUtils().mult( s.getX(), s.getGrad()),temp), s.getY());
				if(x2.leqZero()){
					s.setX(Num.getUtils().createZero());
					s.setY(y2);
				}
				else{
					s.setX(x2);
					s.setY(Num.getUtils().createZero());
				}

			}
			c_dnc.setSegment(i, s);
		}
		Curve.beautify(c_dnc);
		return c_dnc;
	}

	/**
	 * This creates an instance of AffineCurve_DNC with 1 segment
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineCurve_DNC
	 */
	public Curve_DNC_Affine createZeroCurve() {
		return new Curve_DNC_Affine(); // CurveDNC constructor's default behavior
	}

	/**
	 * Wrapper to creates an instance of AffineCurve_DNC with rate 0 and burst equal to y. when y is passed as double
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineCurve_DNC
	 */
	public Curve_DNC_Affine createHorizontal(double y) {
		return createHorizontal(Num.getFactory().create(y));
	}

	/**
	 * This creates an instance of AffineCurve_DNC with rate 0 and burst equal to y. ie a horizontal curve
	 *
	 * @param y
	 *		The burst value
	 * @return c_dnc
	 * 		An instance of AffineCurve_DNC
	 */
	public Curve_DNC_Affine createHorizontal(Num y) {
		Curve_DNC_Affine c_dnc = new Curve_DNC_Affine();
		makeHorizontal(c_dnc, y);
		return c_dnc;
	}

	// ------------------------------------------------------------------------------
	// Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	/**
	 * This creates an instance of Affine Service curve with number of segments equal to 1
	 *
	 * @param
	 *
	 * @return
	 *		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createServiceCurve() {
		return new ServiceCurve_DNC_Affine();
	}

	/**
	 * This creates an instance of Affine Service curve with number of segments equal to passed segment_count
	 *
	 * @param segment_count
	 * 		The number of segments to be created
	 * @return
	 *		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createServiceCurve(int segment_count) {
		return new ServiceCurve_DNC_Affine(segment_count);
	}

	/**
	 * This creates an instance of Affine Service curve from the string representation of the curve
	 *
	 * @param service_curve_str
	 * 		The string representation of curve
	 * @return
	 *		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createServiceCurve(String service_curve_str) throws Exception {
		return new ServiceCurve_DNC_Affine(service_curve_str);
	}

	/**
	 * This creates an instance of Affine Service curve same as the passed curve instance
	 *
	 * @param curve
	 * 		An instance of Curve
	 * @return
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createServiceCurve(Curve curve) {
		return new ServiceCurve_DNC_Affine(curve);
	}

	/**
	 * This creates an Affine service curve with one segment
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createZeroService() {
		return new ServiceCurve_DNC_Affine(); // ServiceCurveDNC constructor's default behavior
	}

	/**
	 *	Wrapper to create Affine service cure with 0 delay and infinite burst.
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createZeroDelayInfiniteBurst() {
		return createDelayedInfiniteBurst(Num.getFactory().createZero());
	}

	/**
	 *	Wrapper to create Affine service cure with delay and infinite burst when delay is passed as double
	 *
	 * @param delay
	 *		The delay in the Rate
	 * @return
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createDelayedInfiniteBurst(double delay) {
		return createDelayedInfiniteBurst(Num.getFactory().create(delay));
	}

	/**
	 *	This create's Affine service cure with delay and infinite burst
	 *
	 * @param delay
	 *		The delay in the Rate
	 * @return sc_dnc
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createDelayedInfiniteBurst(Num delay) {
		ServiceCurve_DNC_Affine sc_dnc = new ServiceCurve_DNC_Affine();
		makeDelayedInfiniteBurst(sc_dnc, delay);
		return sc_dnc;
	}

	/**
	 * Wrapper to create Rate latency for the AffineServiceCurve_DNC with rate and latency passed as double
	 *
	 * @param rate
	 * 		The rate of the curve
	 * @param latency
	 * 		The latency of the curve
	 * @return
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createRateLatency(double rate, double latency) {
		return createRateLatency(Num.getFactory().create(rate), Num.getFactory().create(latency));
	}

	/**
	 * Wrapper to create Rate latency for the AffineServiceCurve_DNC
	 *
	 * @param rate
	 * 		The rate of the curve
	 * @param latency
	 * 		The latency of the curve
	 * @return sc_dnc
	 * 		An instance of AffineServiceCurve_DNC
	 */
	public ServiceCurve_DNC_Affine createRateLatency(Num rate, Num latency) {
		ServiceCurve_DNC_Affine sc_dnc = new ServiceCurve_DNC_Affine();
		makeRateLatency(sc_dnc, rate, latency);
		return sc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	/**
	 * To create affine arrival curve with number of segments equal to one
	 *
	 * @param
	 *
	 * @return
	 *		An instance of AffineArrivalCurve_DNC
	 */
	public ArrivalCurve_DNC_Affine createArrivalCurve() {
		return new ArrivalCurve_DNC_Affine();
	}

	/**
	 * This creates an affine arrival curve with segments equal to the passed segment_count
	 *
	 * @param segment_count
	 *		The number of segments to be created for the Affine arrival curve
	 * @return
	 *		An instance of AffineArrivalCurve_DNC
	 */
	public ArrivalCurve_DNC_Affine createArrivalCurve(int segment_count) {
		return new ArrivalCurve_DNC_Affine(segment_count);
	}

	/**
	 * This creates an affine arrival curve from the passed string representation of curve
	 *
	 * @param arrival_curve_str
	 * 		A string representation of the curve
	 * @return
	 * 		An instance of AffineArrivalCurve_DNC
	 */
	public ArrivalCurve_DNC_Affine createArrivalCurve(String arrival_curve_str) throws Exception {
		return new ArrivalCurve_DNC_Affine(arrival_curve_str);
	}

	/**
	 * This creates an affine arrival curve from the passed curve
	 *
	 * @param curve
	 *		An instance of the type Curve
	 * @return
	 * 		An instance of AffineArrivalCurve_DNC
	 */
	public ArrivalCurve_DNC_Affine createArrivalCurve(Curve curve) {
		return new ArrivalCurve_DNC_Affine(curve);
	}

	/**
	 * //TODO
	 * @param
	 *
	 * @return
	 *
	 */
	public ArrivalCurve_DNC_Affine createArrivalCurve(Curve curve, boolean remove_latency) {
		return createArrivalCurve(Curve.removeLatency(curve));
	}

	/**
	 * To create an Affine arrival Curve with 1 segment
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineArrivalCurve_DNC
	 */
	public ArrivalCurve_DNC_Affine createZeroArrivals() {
		return new ArrivalCurve_DNC_Affine(); // ArrivalCurveDNC constructor's default behavior
	}

	/**
	 * Wrapper to create Affine arrival curve with 0 burst and 0 latency.
	 *
	 * @param rate
	 * 		The rate of the curve to be created
	 * @return
	 * 		An instance of AffineArrivalCurve_DNC
	 *
	 */
	public ArrivalCurve_DNC_Affine createPeakArrivalRate(double rate) {
		return createPeakArrivalRate(Num.getFactory().create(rate));
	}

	/**
	 * To create a Affine arrival curve with 0 burst and 0 latency
	 *
	 * @param rate
	 * 		The rate of the curve to be created
	 *
	 * @return ac_dnc
	 * 		An instance of AffineArrivalCurve_DNC
	 *
	 */
	public ArrivalCurve_DNC_Affine createPeakArrivalRate(Num rate) {
		ArrivalCurve_DNC_Affine ac_dnc = new ArrivalCurve_DNC_Affine();
		makePeakRate(ac_dnc, rate);
		return ac_dnc;
	}

	/**
	 * To create a token bucket affine arrival curve.
	 *
	 * @param rate
	 *		The rate of the affine curve
	 * @param burst
	 * 		The burst of the affine curve
	 * @return ac_dnc
	 * 		An instance of AffineArrivalCurve_DNC with Token bucket.
	 *
	 */
	public ArrivalCurve_DNC_Affine createTokenBucket(double rate, double burst) {
		return createTokenBucket(Num.getFactory().create(rate), Num.getFactory().create(burst));
	}

	/**
	 * To create a token bucket affine arrival curve.
	 *
	 * @param rate
	 *		The rate of the affine curve
	 * @param burst
	 * 		The burst of the affine curve
	 * @return ac_dnc
	 * 		An instance of AffineArrivalCurve_DNC with Token bucket.
	 *
	 */
	public ArrivalCurve_DNC_Affine createTokenBucket(Num rate, Num burst) {
		ArrivalCurve_DNC_Affine ac_dnc = new ArrivalCurve_DNC_Affine();
		makeTokenBucket(ac_dnc, rate, burst);
		return ac_dnc;
	}

	// ------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	/**
	 * To create a 	AffineMaxServiceCurve_DNC with 1 segment
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineMaxServiceCurve_DNC
	 *
	 */
	public MaxServiceCurve_DNC_Affine createMaxServiceCurve() {
		return new MaxServiceCurve_DNC_Affine();
	}

	/**
	 *	Wrapper to create Max service curve DNC with segments equal to segment_count
	 *
	 * @param segment_count
	 *		The number of segments to be created for this curve instance.
	 *		Note: In this case, it cannot be more than 2
	 * @return
	 *
	 */
	public MaxServiceCurve_DNC_Affine createMaxServiceCurve(int segment_count) {
		return new MaxServiceCurve_DNC_Affine(segment_count);
	}

	/**
	 *	Wrapper to create Max service curve DNC from string representation
	 *
	 * @param max_service_curve_str
	 * 		The string representation of the curve
	 * @return
	 * 		An instance of AffineMaxServiceCurve_DNC
	 */
	public MaxServiceCurve_DNC_Affine createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return new MaxServiceCurve_DNC_Affine(max_service_curve_str);
	}

	/**
	 * Wrapper method to create a Max service curve DNC from an existing curve.
	 *
	 * @param curve
	 *
	 * @return
	 * 		An instance of AffineMaxServiceCurve_DNC
	 *
	 */
	public MaxServiceCurve_DNC_Affine createMaxServiceCurve(Curve curve) {
		return new MaxServiceCurve_DNC_Affine(curve);
	}

	/**
	 * wrapper method to create delayed infinite curve for AffineMaxServiceCurve_DNC
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	public MaxServiceCurve_DNC_Affine createInfiniteMaxService() {
		return createDelayedInfiniteBurstMSC(Num.getFactory().createZero());
	}

	/**
	 * A wrapper method AffineMaxServiceCurve_DNC curve having infinite burst with zero delay
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of AffineMaxServiceCurve_DNC
	 */
	public MaxServiceCurve_DNC_Affine createZeroDelayInfiniteBurstMSC() {
		return createDelayedInfiniteBurstMSC(Num.getFactory().createZero());
	}

	/**
	 * A wrapper method AffineMaxServiceCurve_DNC curve having infinite burst with delay
	 *
	 * @param delay
	 * 		The latency of the affine curve
	 * @return
	 * 		An instance of AffineMaxServiceCurve_DNC
	 */
	public MaxServiceCurve_DNC_Affine createDelayedInfiniteBurstMSC(double delay) {
		return createDelayedInfiniteBurstMSC(Num.getFactory().create(delay));
	}

	/**
	 * To create a AffineMaxServiceCurve_DNC curve having infinite burst with delay
	 *
	 * @param delay
	 * 		The latency of the affine curve
	 * @return msc_dnc
	 * 		An instance of AffineMaxServiceCurve_DNC
	 */
	public MaxServiceCurve_DNC_Affine createDelayedInfiniteBurstMSC(Num delay) {
		MaxServiceCurve_DNC_Affine msc_dnc = new MaxServiceCurve_DNC_Affine();
		makeDelayedInfiniteBurst(msc_dnc, delay);
		return msc_dnc;
	}

	/**
	 * A wrapper to create a Service curve with Rate latency with rate and latency represented in double
	 *
	 * @param rate
	 * 		The rate of the curve
	 * @param latency
	 * 		The Rate latency component of the curve
	 * @return msc_dnc
	 * 		An instance of AffineMaxServiceCurve_DNC
	 *
	 */
	public MaxServiceCurve_DNC_Affine createRateLatencyMSC(double rate, double latency) {
		return createRateLatencyMSC(Num.getFactory().create(rate), Num.getFactory().create(latency));
	}

	/**
	 * To create a Service curve with Rate latency
	 *
	 * @param rate
	 * 		The rate of the curve
	 * @param latency
	 * 		The Rate latency component of the curve
	 * @return msc_dnc
	 * 		An instance of AffineMaxServiceCurve_DNC
	 *
	 */
	public MaxServiceCurve_DNC_Affine createRateLatencyMSC(Num rate, Num latency) {
		MaxServiceCurve_DNC_Affine msc_dnc = new MaxServiceCurve_DNC_Affine();
		makeRateLatency(msc_dnc, rate, latency);
		return msc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Curve assembly
	// ------------------------------------------------------------------------------
	/**
	 * To create a curve with Rate 0 and burst y, ie a horizontal curve.
	 *
	 * @param c_dnc
	 * 		An instance of AffineCurve_DNC to which segments have to be created
	 * @param y
	 *		The burst component if any.
	 * @return
	 *
	 */
	private void makeHorizontal(Curve_DNC_Affine c_dnc, Num y) {
		LinearSegment_DNC segment = new LinearSegment_DNC(Num.getFactory().createZero(), y,
				Num.getFactory().createZero(), false);
		c_dnc.setSegments(new LinearSegment_DNC[] { segment });
	}

	/**
	 * To create a curve with infinite burst
	 *
	 * @param c_dnc
	 * 		An instance of AffineCurve_DNC to which segments have to be created
	 * @param delay
	 * 		The latency of the affine curve
	 * @return
	 *
	 */
	private void makeDelayedInfiniteBurst(Curve_DNC_Affine c_dnc, Num delay) {
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

	/**
	 * To create a curve which had no burst and latency.
     *  Note: This is a special case of Affine curve which is both Rate latency and Token bucket cure with
     *          Burst(Token bucket component) and Latency (Rate Latency component) set to 0.
	 * @param c_dnc
	 * 		An instance of AffineCurve_DNC to which segments have to be created
	 * @param rate
	 *		Rate of the linear segment
	 * @return
	 *
	 */
	private void makePeakRate(Curve_DNC_Affine c_dnc, Num rate) {
		if (rate.equals(Num.getFactory().getPositiveInfinity())) {
			throw new IllegalArgumentException(
					"Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
		}
		if (rate.eqZero()) {
			makeHorizontal(c_dnc, Num.getFactory().createZero());
			return;
		}
		/*Since both latency and burst are 0 and rate is positive, its a line passing through origin*/
		LinearSegment_DNC[] segments = new LinearSegment_DNC[1];

		segments[0] = new LinearSegment_DNC(Num.getFactory().createZero(), Num.getFactory().createZero(), rate, false);

		c_dnc.setSegments(segments);
		/*Since the line is passing through origin, it satisifies the criteria of both RL and TB with latency and burst = 0*/
		c_dnc.is_rate_latency = true; // with latency 0
		c_dnc.is_token_bucket = true; // with burstiness 0
	}

	/**
	 * Create a Rate latency curve based on the passed rate and latency
	 *
	 * @param c_dnc
	 * 		The curve to which the Rate latency has to be created
	 * @param rate
	 * 		The rate of the affine curve
	 * @param latency
	 *		Latency of the affine curve
	 * @return
	 *
	 */
	private void makeRateLatency(Curve_DNC_Affine c_dnc, Num rate, Num latency) {
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

	/**
	 * Create a Token bucket affine curve based on the passed rate and burst
	 *
	 * @param c_dnc
	 * 		The curve to which the Token bucket has to be created
	 * @param rate
	 * 		The rate of the affine curve
	 * @param burst
	 *		Burst of the affine curve
	 * @return
	 *
	 */
	private void makeTokenBucket(Curve_DNC_Affine c_dnc, Num rate, Num burst) {
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
