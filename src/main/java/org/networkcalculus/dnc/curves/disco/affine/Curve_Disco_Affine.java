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

package org.networkcalculus.dnc.curves.disco.affine;

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
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.disco.LinearSegment_Disco;
import org.networkcalculus.num.Num;

/**
 * Class representing a piecewise linear affine curve, defined on [0,inf), by at most two segments.<br>
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
public class Curve_Disco_Affine implements Curve_Affine, CurveFactory_Affine {
	private static Curve_Disco_Affine instance = new Curve_Disco_Affine();
	private static CurveUtils utils = Calculator.getInstance().getDncBackend().getCurveUtils();

	protected LinearSegment_Disco[] segments;

	protected boolean is_delayed_infinite_burst = false;

	protected boolean is_rate_latency = false;
	protected boolean has_rate_latency_meta_info = false;
	protected List<Curve_Disco_Affine> rate_latencies = new LinkedList<Curve_Disco_Affine>();

	protected boolean is_token_bucket = false;
	protected boolean has_token_bucket_meta_info = false;
	protected List<Curve_Disco_Affine> token_buckets = new LinkedList<Curve_Disco_Affine>();

	/**
	 * Creates a <code>Curve_Disco_Affine</code> instance with 1 segment of type LinearSegment.
	 * 
	 * @param
	 *
	 * @return
	 */
	protected Curve_Disco_Affine() {
		createNewCurve(1);
	}

	/**
	 * Creates a copy of the passed <code>Curve_Disco_Affine</code> instance <code>curve</code>.
	 * 
	 * @param
	 *
	 * @return
	 */
	protected Curve_Disco_Affine(Curve curve) {
		copy(curve);
	}

	/**
	 * Creates a <code>Curve_Disco_Affine</code> instance with <code>segment_count<code/> number of
	 * <code>LinearSegment<code/>.
	 * 
	 * @param segment_count
	 *           Number of segments in the curve.
	 *           In case of affine curve, at max the number of segments can be two.
	 * @return
	 *           In case of affine curve, at max the number of segments can be two.
	 */
	protected Curve_Disco_Affine(int segment_count) {
		createNewCurve(segment_count);
	}

	/**
	 * Returns an instance of <code>Curve_Disco_Affine</code> with one segment of
	 * <code>LinearSegment<code/>.
	 * 
	 * @param
	 * @return
	 * 			An instance of <code>Curve_Disco_Affine</code>
	 */
	public static CurveFactory_Affine getFactory() {
		return instance;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Interface Implementations
	// --------------------------------------------------------------------------------------------------------------
	/**
	 * Verifies weather the curve is a rate latency curve or not and
	 * decomposes the curve into rate latency components if it is a rate latency curve.
	 *
	 * @param
	 *
	 * @return <code>is_rate_latency</code>
	 * 		Returns weather the give curve is rate latency curve or not.
	 */
	public boolean isRateLatency() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	/**
	 * Verifies weather the curve is a token bucket or not
	 * Decomposes the curve into token bucket components if it is a token bucket curve.
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
	 * Returns weather the curve already has rate latency meta info or not.
	 *
	 * @param
	 *
	 * @return <>code</>has_rate_latency_meta_info<>code</>
	 * 		Returns weather or not the give curve has rate latency meta info.
	 */
	public boolean hasRateLatencyMetaInfo() {
		return has_rate_latency_meta_info;
	}

	/**
	 * Sets the has_rate_latency_meta_info to the passed arguments.
	 *
	 * @param has_rate_latency_meta_info
	 * 		A boolean which tells weather the curve has rate latency meta info or not.
	 *
	 * @return void
	 *
	 */
	public void setRL_MetaInfo(boolean has_rate_latency_meta_info) {
		this.has_rate_latency_meta_info = has_rate_latency_meta_info;
	}

	/**
	 * Returns the rate latency components of calling curve instance.
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
	 * 			List of Curves each of which represent a rate latency component.
	 * @return void
	 *
	 */
	public void setRL_Components(List<Curve> rate_latencies) {
		if(isRateLatency()) {
			List<Curve_Disco_Affine> tmp = new LinkedList<>();
			tmp.add(this.copy());
			this.rate_latencies = tmp;
		}
	}

	/**
	 * Returns weather the curve already has token bucket meta info or not.
	 *
	 * @param
	 *
	 * @return has_token_bucket_meta_info
	 * 		Weather or not the give curve has token bucket meta info.
	 */
	public boolean hasTokenBucketMetaInfo() {
		return has_token_bucket_meta_info;
	}

	/**
	 * Update the has_token_bucket_meta_info of the calling object to the passed argument.
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
	 * Returns the token bucket components of the curve.
	 *
	 * @param
	 *
	 * @return tmp
	 * 		A list of Tocken bucket components
	 * 		Note: In this can it should be only one
	 *
	 */
	public List<Curve_Affine> getTB_Components() {
		/* If its an affine token bucket curve then there should be only two segments */
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
	 *		List of Curves, each of them representing a token bucket component.
	 *		Note: In this case, at max there can be only one token bucket component.
	 *
	 */
	public void setTB_Components(List<Curve> token_buckets) {
		if(isTokenBucket()) {
			List<Curve_Disco_Affine> tmp = new LinkedList<>();
			tmp.add(this.copy());
			this.token_buckets = tmp;
		}
	}
	/**
	 *	Creates an affine curve with maximum of two segments.
	 *
	 * @param segment_count
	 *		Number of segments to be created.
	 * @return
	 *
	 */
	private void createNewCurve(int segment_count) {
		if (segment_count < 0 || segment_count > 2) {
			throw new IndexOutOfBoundsException("Affine curves can have at most two segments (given count was "
					+ segment_count + ")!");
		}
		segments = new LinearSegment_Disco[segment_count];
		if(0 == segment_count){

			return;
		}

		segments[0] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
				Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), false);
		if(segment_count > 1)
		{
			segments[1] = new LinearSegment_Disco(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(),
					Num.getFactory(Calculator.getInstance().getNumBackend()).createZero(), true);
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
		segments = new LinearSegment_Disco[segments_to_parse.length]; // No need to use createZeroSegments( i ) because we
		// will store parsed segments

		for (int i = 0; i < segments_to_parse.length; i++) {
			segments[i] = new LinearSegment_Disco(segments_to_parse[i]);
		}
		utils.beautify(this);
	}

	/** 
	 * Add a segment at 0,0 with grad 0 if its not present already
	 * If affine curve case, the first segment is always at 0,0 and with grad 0.
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	protected void forceThroughOrigin() {
		// Implicit assumption: getSegment(0).getY().eqZero() is true
		if (getSegment(0).getY().gtZero()) {
			Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
			
			addSegment(0, new LinearSegment_Disco(num.createZero(), num.createZero(), num.createZero(), false));

			getSegment(1).setLeftopen(true);
		}
	}

	/**
	 * Resets the rate latency and token bucket meta info.
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	private void clearMetaInfo() {
		has_token_bucket_meta_info = false;
		is_token_bucket = false;
		token_buckets = new LinkedList<Curve_Disco_Affine>();

		has_rate_latency_meta_info = false;
		is_rate_latency = false;
		rate_latencies = new LinkedList<Curve_Disco_Affine>();
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
	public Curve_Disco_Affine copy() {
		Curve_Disco_Affine c_copy = new Curve_Disco_Affine();
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
		LinearSegment_Disco[] segments = new LinearSegment_Disco[curve.getSegmentCount()];

		if (curve instanceof Curve_Disco_Affine) {
			for (int i = 0; i < segments.length; i++) {
				segments[i] = ((Curve_Disco_Affine) curve).getSegment(i).copy();
			}

			this.has_rate_latency_meta_info = ((Curve_Disco_Affine) curve).has_rate_latency_meta_info;
			this.rate_latencies = ((Curve_Disco_Affine) curve).rate_latencies;

			this.has_token_bucket_meta_info = ((Curve_Disco_Affine) curve).has_token_bucket_meta_info;
			this.token_buckets = ((Curve_Disco_Affine) curve).token_buckets;

			this.is_delayed_infinite_burst = ((Curve_Affine) curve).isDelayedInfiniteBurst();
			this.is_rate_latency = ((Curve_Affine) curve).isRateLatency();
			this.is_token_bucket = ((Curve_Affine) curve).isTokenBucket();
		} else {
			for (int i = 0; i < curve.getSegmentCount(); i++) {
				segments[i] = new LinearSegment_Disco(curve.getSegment(i));
			}
		}

		setSegments(segments);
	}

	/**
	 * Returns the segment at position pos (starting at 0).
	 *
	 * @param pos
	 *		The position of segment in the curve to be fetched.
	 *		Note: In this case, the position can be either 0 or 1.
	 * @return
	 *
	 */
	public LinearSegment_Disco getSegment(int pos) {
		if (pos < 0 || pos > segments.length - 1) {
			throw new IndexOutOfBoundsException("Index out of bounds (pos=" + pos + ")!");
		}
		return segments[pos];
	}

	/**
	 * Returns the number of segments in the curve. At max this can be two.
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

	/**
	 * Insets the segment at position pos.
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

		LinearSegment_Disco s_dnc;
		if (s instanceof LinearSegment_Disco) {
			s_dnc = ((LinearSegment_Disco) s).copy();
		} else {
			s_dnc = new LinearSegment_Disco(s);
		}

		segments[pos] = s_dnc;
		clearMetaInfo();
	}

	/**
	 * Setting the this.segments to the passes segments
	 * 		Note: Since this is an affine curve, the segments count of passed segment cannot exceed two.
	 *
	 * @param segments
	 * 		Segments to be set to this.segments
	 *
	 */
	protected void setSegments(LinearSegment[] segments) {
		if(segments.length > 2){
			throw new IndexOutOfBoundsException("Affine curves can have at most two segments (given count was "
					+ segments.length + ")!");

		}
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
	 *
	 */
	public void addSegment(LinearSegment s) {
		addSegment(segments.length, s);
	}

	/**
	 * Adds a <code>LinearSegment</code> at the location <code>pos</code> of the
	 * curve.<br>
	 * Note1: Segments after pos will be pushed back by one position.<br>
	 * Note2: It is the user's responsibility to add segments in the order of
	 * increasing x-coordinates.
	 *
	 * @param pos
	 *            the index into the segment array to add the new segment.
	 * @param s
	 *            the segment to be added.
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
	 *            The index of the segment to be removed.
	 * @return
	 *
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
	 * @param
	 *
	 * @return	True/False
	 * 		whether the curve is wide-sense increasing.
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
	 * Test whether the curve us convex.
	 * 
	 * @param
	 *
	 * @return
	 * 	Returns whether the curve is Convex or not
	 *
	 */
	public boolean isConvex() {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		return isConvexIn(num.getZero(), num.getPositiveInfinity());
	}

	/**
	 * Tests whether the curve is convex in [a,b].
	 *
	 * @param a
	 *            the lower bound of the test interval.
	 * @param b
	 *            the upper bound of the test interval.
	 * @return whether the curve is convex.
	 * 
	 */
	public boolean isConvexIn(Num a, Num b) {
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getNegativeInfinity(); // No need to create an object as this
		// value is only set for initial comparison in the loop.

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
	 * @param
	 *
	 * @return whether the curve is concave.
	 */
	public boolean isConcave() {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		return isConcaveIn(num.getZero(), num.getPositiveInfinity());
	}

	/**
	 * Tests whether the curve is concave in [a,b].
	 *
	 * @param a
	 *            the lower bound of the test interval.
	 * @param b
	 *            the upper bound of the test interval.
	 * @return whether the curve is concave.
	 * 
	 */
	public boolean isConcaveIn(Num a, Num b) {
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity(); // No need to create an object as this
		// value is only set for initial comparison in the loop.

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
		}
		return true;
	}

	/**
	 * Tests whether the curve is almost concave, i.e. it is concave once its
	 * function value is larger than 0.
	 *
	 * @return whether the curve is almost concave.
	 * 
	 */
	public boolean isAlmostConcave() {
		Num last_gradient = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity(); // No need to create an object as this
		// value is only set for initial comparison in the loop.

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

	/**
	 * To check whether this object instance is equal to the passing instance.
	 *
	 * @param obj
	 * 		Some object.
	 *
	 * @return True if the passed object is similar to calling object
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Curve_Disco_Affine)) {
			return false;
		}

		Curve_Disco_Affine this_cpy = this.copy();
		Curve_Disco_Affine other_cpy = ((Curve_Disco_Affine) obj).copy();

		utils.beautify(this_cpy);
		utils.beautify(other_cpy);

		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		if (this_cpy.getLatency() == num.getPositiveInfinity()) {
			this_cpy = Curve_ConstantPool.ZERO_CURVE.get();
		}
		if (other_cpy.getLatency() == num.getPositiveInfinity()) {
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

	/**
	 * To generate the hash code of a curve based on its segments.
	 *
	 * @param
	 *
	 * @return Hash value of this curve.
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
	 * @return The curve represented as a string.
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
	 *            The x-coordinate
	 * @return The function value.
	 *
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
	 *            The x-coordinate
	 * @return The function value.
	 *
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
	 *            The y-coordinate
	 * @return The smallest x value
	 *
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
	 *            Return the rightmost x coordinate instead of the leftmost one (default).
	 * @return The smallest x value.
	 *
	 */
	public Num f_invARB(Num y, boolean rightmost) {
		int i = getSegmentFirstAtValue(y);

		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());

		if (i < 0) {
			return num.createNaN();
		}
		if (rightmost) {
			while (i < segments.length && segments[i].getGrad().equals(num.getZero())) {
				i++;
			}
			if (i >= segments.length) {
				return num.createPositiveInfinity();
			}
		}
		if (!segments[i].getGrad().equals(num.getZero())) {
			return num.add(segments[i].getX(), num.div(num.sub(y, segments[i].getY()), segments[i].getGrad()));
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
	 *            The y-coordinate
	 * @return The segment number.
	 *
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
	 * @return The latency of this curve.
	 *
	 */
	public Num getLatency() {

		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		
		if (isRateLatency()) {
			if (segments.length == 2) { // Rate latency other than a simple rate function
				return segments[1].getX().copy();
			} else { // Single-segment rate functions have latency 0
				return num.createZero();
			}
		} else {
			utils.beautify(this);
			if (segments[0].getY().gt(num.getZero())) {
				return num.createZero();
			}
			for (int i = 0; i < segments.length; i++) {
				Num y0 = segments[i].getY();
				if (y0.lt(num.getZero())) {
					y0 = num.createZero();
				}
				if (y0.gt(num.getZero()) || (y0.geq(num.getZero())
						&& segments[i].getGrad().gt(num.getZero()))) {
					return segments[i].getX();
				}
				if (y0.lt(num.getZero()) || segments[i].getGrad().lt(num.getZero())) {
					System.out.println("Remove latency of " + this.toString());
					throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
				}
			}
			return num.createPositiveInfinity();
		}
	}

	/**
	 * Returns the burst of the curve.
	 *
	 * @return The burstiness
	 * 
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
	 *            The x-coordinate
	 * @return The function value.
	 *
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
	 * @return The rate of the ultimately affine part.
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
	 * @return is_delayed_infinite_burst
	 * 		Returns True if the curve is delayed infinite burst otherwise false.
	 *
	 */
	public boolean isDelayedInfiniteBurst() {
		return is_delayed_infinite_burst;
	}

	/**
	 * Decomposes the curve into rate latency components and returns if its a rate latency curve.
	 *
	 * @return is_rate_latency
	 * 		Returns true if the curve has one rate latency component otherwise false.
	 *
	 */
	public boolean getRL_Property() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	/**
	 * Setter method for the flag is_rate_latency.
	 *
	 * @param is_rate_latency
	 * 		Boolean to be set.
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
	 * @return The number of rate latency curves.
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
	 *            The number of the rate latency curve.
	 * @return The rate latency curve
	 *
	 */
	public Curve_Disco_Affine getRL_Component(int i) {
		decomposeIntoRateLatencies();
		return rate_latencies.get(i);
	}

	/**
	 * Decomposes this curve into a list of rate latency curves and stores this list
	 * in the curve's <code>rate_latencies</code> field.<br>
	 *
	 */
	private void decomposeIntoRateLatencies() {
		if (has_rate_latency_meta_info == true) {
			return;
		}
		
		rate_latencies = new ArrayList<Curve_Disco_Affine>();
		
		switch(segments.length) {
		case 1:
			if(segments[0].getX().eqZero() 
					&& segments[0].getY().eqZero()
					&& segments[0].getGrad().isFinite()) {
				rate_latencies.add(createRateLatency(segments[0].getGrad(), segments[0].getX()));
				is_rate_latency = true;
				has_rate_latency_meta_info = true;
			}
			break;
		case 2:
			if(segments[0].getX().eqZero() 
					&& segments[0].getY().eqZero()
					&& segments[0].getGrad().eqZero()
					&& segments[1].getX().geqZero() 
					&& segments[1].getY().eqZero()
					&& segments[1].getGrad().geqZero()) {
				rate_latencies.add(createRateLatency(segments[1].getGrad(), segments[1].getX()));
				is_rate_latency = true;
				has_rate_latency_meta_info = true;
			}
			break;
		default:
			throw new RuntimeException("Amount of segments out of permissible range for an affine curve: " 
										+ Integer.toString(segments.length));
		}
	}

	/**
	 * Setter method for is_token_bucket flag.
	 *
	 * @param is_token_bucket
	 * 		Boolean representing weather or not its token bucket.
	 *
	 */
	public void setTokenBucket(boolean is_token_bucket) {
		this.is_token_bucket = is_token_bucket;
	}

	/**
	 * Returns the number of token buckets the curve can be decomposed into.
	 * 		Note: In this case, it can be maximum 1
	 *
	 * @return The number of token buckets
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
	 *            The number of the token bucket
	 * @return The token bucket
	 */
	public Curve_Disco_Affine getTB_Component(int i) {
		decomposeIntoTokenBuckets();
		return token_buckets.get(i);
	}

	/**
	 * Decomposes this curve into a list of token bucket curves and stores this list
	 * in the curve's <code>token_buckets</code> field.<br>
	 *
	 */
	private void decomposeIntoTokenBuckets() {
		if (has_token_bucket_meta_info == true) {
			return;
		}
		
		token_buckets = new ArrayList<Curve_Disco_Affine>();
		
		switch(segments.length) {
		case 1:
			if(segments[0].getX().eqZero() 
					&& segments[0].getY().eqZero() // TODO Check here when relaxing the assumption that a TB passes through the origin. 
					&& segments[0].getGrad().isFinite()) {
				token_buckets.add(createTokenBucket(segments[0].getGrad(), segments[0].getY()));
				is_token_bucket = true;
				has_token_bucket_meta_info = true;
			}
			break;
		case 2:
			if(segments[0].getX().eqZero() 
					&& segments[0].getY().eqZero()
					&& segments[0].getGrad().eqZero()
					&& segments[1].getX().eqZero() 
					&& segments[1].getY().geqZero()
					&& segments[1].getGrad().geqZero()) {
				token_buckets.add(createTokenBucket(segments[1].getGrad(), segments[1].getY()));
				is_token_bucket = true;
				has_token_bucket_meta_info = true;
			}
			break;
		default:
			throw new RuntimeException("Amount of segments out of permissible range for an affine curve: " 
										+ Integer.toString(segments.length));
		}
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
	/**
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	public Curve_Disco_Affine createCurve(List<LinearSegment> segments) {
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

		Curve_Disco_Affine c_dnc = new Curve_Disco_Affine(segment_size);
		for (int i = 0; i < segment_size; i++) {
			LinearSegment s = null;
			if(i == 0) {
				s = segments.get(i);
			}
			else
			{
				s = segments.get( segments.size() - 1);
				Num temp = Num.getUtils(Calculator.getInstance().getNumBackend()).create(-1);
				Num x2 = Num.getUtils(Calculator.getInstance().getNumBackend()).add(Num.getUtils(Calculator.getInstance().getNumBackend()).mult(Num.getUtils(Calculator.getInstance().getNumBackend()).div(s.getY(), s.getGrad()), temp), s.getX());
				Num y2 = Num.getUtils(Calculator.getInstance().getNumBackend()).add(Num.getUtils(Calculator.getInstance().getNumBackend()).mult(Num.getUtils(Calculator.getInstance().getNumBackend()).mult( s.getX(), s.getGrad()),temp), s.getY());
				if(x2.leqZero()){
					s.setX(Num.getUtils(Calculator.getInstance().getNumBackend()).createZero());
					s.setY(y2);
				}
				else{
					s.setX(x2);
					s.setY(Num.getUtils(Calculator.getInstance().getNumBackend()).createZero());
				}

			}
			c_dnc.setSegment(i, s);
		}
		utils.beautify(c_dnc);
		return c_dnc;
	}

	/**
	 * This creates an instance of Curve_Disco_Affine with 1 segment.
	 *
	 * @return
	 * 		An instance of Curve_Disco_Affine
	 */
	public Curve_Disco_Affine createZeroCurve() {
		return new Curve_Disco_Affine(); // CurveDNC constructor's default behavior
	}

	/**
	 * This creates an instance of Curve_Disco_Affine with rate 0 and burst equal to y. ie a horizontal curve
	 *
	 * @param y
	 *		The burst value
	 * @return c_dnc
	 * 		An instance of Curve_Disco_Affine
	 */
	public Curve_Disco_Affine createHorizontal(Num y) {
		Curve_Disco_Affine c_dnc = new Curve_Disco_Affine();
		makeHorizontal(c_dnc, y);
		return c_dnc;
	}

	// ------------------------------------------------------------------------------
	// Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	/**
	 * This creates an instance of affine service curve with number of segments equal to 1
	 *
	 * @param
	 *
	 * @return
	 *		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createServiceCurve() {
		return new ServiceCurve_Disco_Affine();
	}

	/**
	 * This creates an instance of affine service curve with number of segments equal to passed segment_count.
	 *
	 * @param segment_count
	 * 		The number of segments to be created.
	 * @return
	 *		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createServiceCurve(int segment_count) {
		return new ServiceCurve_Disco_Affine(segment_count);
	}

	/**
	 * This creates an instance of affine service curve from the string representation of the curve.
	 *
	 * @param service_curve_str
	 * 		The string representation of curve.
	 * @return
	 *		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createServiceCurve(String service_curve_str) throws Exception {
		return new ServiceCurve_Disco_Affine(service_curve_str);
	}

	/**
	 * This creates an instance of affine service curve same as the passed curve instance.
	 *
	 * @param curve
	 * 		An instance of Curve.
	 * @return
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createServiceCurve(Curve curve) {
		return new ServiceCurve_Disco_Affine(curve);
	}

	/**
	 * This creates an affine service curve with one segment.
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createZeroService() {
		return new ServiceCurve_Disco_Affine(); // ServiceCurveDNC constructor's default behavior
	}

	/**
	 *	Wrapper to create affine service curve with 0 delay and infinite burst.
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createZeroDelayInfiniteBurst() {
		return createDelayedInfiniteBurst(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	/**
	 *	Wrapper to create affine service curve with delay and infinite burst when delay is passed as double.
	 *
	 * @param delay
	 *		The delay in the rate
	 * @return
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createDelayedInfiniteBurst(double delay) {
		return createDelayedInfiniteBurst(Num.getFactory(Calculator.getInstance().getNumBackend()).create(delay));
	}

	/**
	 *	This creates affine service curve with delay and infinite burst.
	 *
	 * @param delay
	 *		The delay in the rate.
	 * @return sc_dnc
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createDelayedInfiniteBurst(Num delay) {
		ServiceCurve_Disco_Affine sc_dnc = new ServiceCurve_Disco_Affine();
		makeDelayedInfiniteBurst(sc_dnc, delay);
		return sc_dnc;
	}

	/**
	 * Wrapper to create rate latency for the ServiceCurve_Disco_Affine with rate and latency passed as double.
	 *
	 * @param rate
	 * 		The rate of the curve.
	 * @param latency
	 * 		The latency of the curve.
	 * @return
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createRateLatency(double rate, double latency) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		return createRateLatency(num.create(rate), num.create(latency));
	}

	/**
	 * Wrapper to create rate latency for the ServiceCurve_Disco_Affine.
	 *
	 * @param rate
	 * 		The rate of the curve.
	 * @param latency
	 * 		The latency of the curve.
	 * @return sc_dnc
	 * 		An instance of ServiceCurve_Disco_Affine.
	 */
	public ServiceCurve_Disco_Affine createRateLatency(Num rate, Num latency) {
		ServiceCurve_Disco_Affine sc_dnc = new ServiceCurve_Disco_Affine();
		makeRateLatency(sc_dnc, rate, latency);
		return sc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	/**
	 * To create affine arrival curve with number of segments equal to one.
	 *
	 * @return
	 *		An instance of ArrivalCurve_Disco_Affine.
	 */
	public ArrivalCurve_Disco_Affine createArrivalCurve() {
		return new ArrivalCurve_Disco_Affine();
	}

	/**
	 * This creates an affine arrival curve with segments equal to the passed segment_count
	 *
	 * @param segment_count
	 *		The number of segments to be created for the affine arrival curve
	 * @return
	 *		An instance of ArrivalCurve_Disco_Affine
	 */
	public ArrivalCurve_Disco_Affine createArrivalCurve(int segment_count) {
		return new ArrivalCurve_Disco_Affine(segment_count);
	}

	/**
	 * This creates an affine arrival curve from the passed string representation of curve.
	 *
	 * @param arrival_curve_str
	 * 		A string representation of the curve.
	 * @return
	 * 		An instance of ArrivalCurve_Disco_Affine.
	 */
	public ArrivalCurve_Disco_Affine createArrivalCurve(String arrival_curve_str) throws Exception {
		return new ArrivalCurve_Disco_Affine(arrival_curve_str);
	}

	/**
	 * This creates an affine arrival curve from the passed curve.
	 *
	 * @param curve
	 *		An instance of the type Curve.
	 * @return
	 * 		An instance of ArrivalCurve_Disco_Affine.
	 */
	public ArrivalCurve_Disco_Affine createArrivalCurve(Curve curve) {
		return new ArrivalCurve_Disco_Affine(curve);
	}

	/**
	 * @param
	 *
	 * @return
	 *
	 */
	public ArrivalCurve_Disco_Affine createArrivalCurve(Curve curve, boolean remove_latency) {
		return createArrivalCurve(utils.removeLatency(curve));
	}

	/**
	 * To create an affine arrival Curve with one segment.
	 *
	 * @return
	 * 		An instance of ArrivalCurve_Disco_Affine.
	 */
	public ArrivalCurve_Disco_Affine createZeroArrivals() {
		return new ArrivalCurve_Disco_Affine(); // ArrivalCurveDNC constructor's default behavior
	}
	public ArrivalCurve_Disco_Affine createInfiniteArrivals() {
		ArrivalCurve_Disco_Affine ac_dnc = new ArrivalCurve_Disco_Affine(); 
		makeDelayedInfiniteBurst(ac_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
		return ac_dnc;
	}

	/**
	 * Wrapper to create affine arrival curve with 0 burst and 0 latency.
	 *
	 * @param rate
	 * 		The rate of the curve to be created.
	 * @return
	 * 		An instance of ArrivalCurve_Disco_Affine.
	 *
	 */
	public ArrivalCurve_Disco_Affine createPeakArrivalRate(double rate) {
		return createPeakArrivalRate(Num.getFactory(Calculator.getInstance().getNumBackend()).create(rate));
	}

	/**
	 * To create a affine arrival curve with 0 burst and 0 latency.
	 *
	 * @param rate
	 * 		The rate of the curve to be created.
	 *
	 * @return ac_dnc
	 * 		An instance of ArrivalCurve_Disco_Affine.
	 *
	 */
	public ArrivalCurve_Disco_Affine createPeakArrivalRate(Num rate) {
		ArrivalCurve_Disco_Affine ac_dnc = new ArrivalCurve_Disco_Affine();
		makePeakRate(ac_dnc, rate);
		return ac_dnc;
	}

	/**
	 * To create a token bucket affine arrival curve.
	 *
	 * @param rate
	 *		The rate of the affine curve.
	 * @param burst
	 * 		The burst of the affine curve.
	 * @return ac_dnc
	 * 		An instance of ArrivalCurve_Disco_Affine with token bucket.
	 *
	 */
	public ArrivalCurve_Disco_Affine createTokenBucket(double rate, double burst) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		return createTokenBucket(num.create(rate), num.create(burst));
	}

	/**
	 * To create a token bucket affine arrival curve.
	 *
	 * @param rate
	 *		The rate of the affine curve.
	 * @param burst
	 * 		The burst of the affine curve.
	 * @return ac_dnc
	 * 		An instance of ArrivalCurve_Disco_Affine with token bucket.
	 *
	 */
	public ArrivalCurve_Disco_Affine createTokenBucket(Num rate, Num burst) {
		ArrivalCurve_Disco_Affine ac_dnc = new ArrivalCurve_Disco_Affine();
		makeTokenBucket(ac_dnc, rate, burst);
		return ac_dnc;
	}

	// ------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// ------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DNC compliance
	// ------------------------------------------------------------
	/**
	 * To create a 	MaxServiceCurve_Disco_Affine with one segment.
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 *
	 */
	public MaxServiceCurve_Disco_Affine createMaxServiceCurve() {
		return new MaxServiceCurve_Disco_Affine();
	}

	/**
	 * Wrapper to create Max service curve DNC with segments equal to segment_count.
	 *
	 * @param segment_count
	 *		The number of segments to be created for this curve instance.
	 *		Note: In this case, it cannot be more than two.
	 * @return
	 *
	 */
	public MaxServiceCurve_Disco_Affine createMaxServiceCurve(int segment_count) {
		return new MaxServiceCurve_Disco_Affine(segment_count);
	}

	/**
	 * Wrapper to create Max service curve DNC from string representation.
	 *
	 * @param max_service_curve_str
	 * 		The string representation of the curve.
	 * @return
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 */
	public MaxServiceCurve_Disco_Affine createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return new MaxServiceCurve_Disco_Affine(max_service_curve_str);
	}

	/**
	 * Wrapper method to create a Max service curve DNC from an existing curve.
	 *
	 * @param curve
	 *
	 * @return
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 *
	 */
	public MaxServiceCurve_Disco_Affine createMaxServiceCurve(Curve curve) {
		return new MaxServiceCurve_Disco_Affine(curve);
	}

	/**
	 * A wrapper method to create delayed infinite curve for MaxServiceCurve_Disco_Affine.
	 *
	 * @param
	 *
	 * @return
	 *
	 */
	public MaxServiceCurve_Disco_Affine createInfiniteMaxService() {
		return createDelayedInfiniteBurstMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	/**
	 * A wrapper method MaxServiceCurve_Disco_Affine curve having infinite burst with zero delay.
	 *
	 * @param
	 *
	 * @return
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 */
	public MaxServiceCurve_Disco_Affine createZeroDelayInfiniteBurstMSC() {
		return createDelayedInfiniteBurstMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
	}

	/**
	 * A wrapper method MaxServiceCurve_Disco_Affine curve having infinite burst with delay.
	 *
	 * @param delay
	 * 		The latency of the affine curve.
	 * @return
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 */
	public MaxServiceCurve_Disco_Affine createDelayedInfiniteBurstMSC(double delay) {
		return createDelayedInfiniteBurstMSC(Num.getFactory(Calculator.getInstance().getNumBackend()).create(delay));
	}

	/**
	 * Create a MaxServiceCurve_Disco_Affine curve having infinite burst after a given delay.
	 *
	 * @param delay
	 * 		The latency of the affine curve.
	 * @return msc_dnc
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 */
	public MaxServiceCurve_Disco_Affine createDelayedInfiniteBurstMSC(Num delay) {
		MaxServiceCurve_Disco_Affine msc_dnc = new MaxServiceCurve_Disco_Affine();
		makeDelayedInfiniteBurst(msc_dnc, delay);
		return msc_dnc;
	}

	/**
	 * A wrapper to create a service curve with rate latency with rate and latency represented in double.
	 *
	 * @param rate
	 * 		The rate of the curve.
	 * @param latency
	 * 		The rate latency component of the curve.
	 * @return msc_dnc
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 *
	 */
	public MaxServiceCurve_Disco_Affine createRateLatencyMSC(double rate, double latency) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		return createRateLatencyMSC(num.create(rate), num.create(latency));
	}

	/**
	 * Create a service curve with a rate and a latency.
	 *
	 * @param rate
	 * 		The rate of the curve.
	 * @param latency
	 * 		The rate latency component of the curve.
	 * @return msc_dnc
	 * 		An instance of MaxServiceCurve_Disco_Affine.
	 *
	 */
	public MaxServiceCurve_Disco_Affine createRateLatencyMSC(Num rate, Num latency) {
		MaxServiceCurve_Disco_Affine msc_dnc = new MaxServiceCurve_Disco_Affine();
		makeRateLatency(msc_dnc, rate, latency);
		return msc_dnc;
	}

	// ------------------------------------------------------------------------------
	// Curve assembly
	// ------------------------------------------------------------------------------
	/**
	 * Create a curve with rate 0 and burst y, i.e. a horizontal curve.
	 *
	 * @param c_dnc
	 * 		An instance of Curve_Disco_Affine to which segments have to be created.
	 * @param y
	 *		The burst component if any.
	 *
	 */
	private void makeHorizontal(Curve_Disco_Affine c_dnc, Num y) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		LinearSegment_Disco segment = new LinearSegment_Disco(num.createZero(), y, num.createZero(), false);
		c_dnc.setSegments(new LinearSegment_Disco[] { segment });
	}

	/**
	 * Create a curve with infinite burst.
	 *
	 * @param c_dnc
	 * 		An instance of Curve_Disco_Affine to which segments have to be created.
	 * @param delay
	 * 		The latency of the affine curve.
	 *
	 */
	private void makeDelayedInfiniteBurst(Curve_Disco_Affine c_dnc, Num delay) {
		if (delay.ltZero()) {
			throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[2];
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());

		segments[0] = new LinearSegment_Disco(num.createZero(), num.createZero(), num.createZero(), false);

		segments[1] = new LinearSegment_Disco(delay, num.createPositiveInfinity(), num.createZero(), true);

		c_dnc.setSegments(segments);
		c_dnc.is_delayed_infinite_burst = true;
	}

	/**
	 * To create a curve which had no burst and latency.
     *  Note: This is a special case of affine curve which is both rate latency and token bucket curve with
     *          burst (token bucket component) and latency (rate latency component) set to 0.
     *          
	 * @param c_dnc
	 * 		An instance of Curve_Disco_Affine to which segments have to be created.
	 * @param rate
	 *		Rate of the linear segment.
	 *
	 */
	private void makePeakRate(Curve_Disco_Affine c_dnc, Num rate) {
		if (rate.equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())) {
			throw new IllegalArgumentException(
					"Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
		}
		if (rate.eqZero()) {
			makeHorizontal(c_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
			return;
		}
		
		/* Since both latency and burst are 0 and rate is positive, its a line passing through origin */
		LinearSegment_Disco[] segments = new LinearSegment_Disco[1];
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());

		segments[0] = new LinearSegment_Disco(num.createZero(), num, rate, false);

		c_dnc.setSegments(segments);
		
		/* Since the line is passing through origin, it satisifies the criteria of both RL and TB with latency and burst = 0 */
		c_dnc.is_rate_latency = true; // with latency 0
		c_dnc.is_token_bucket = true; // with burstiness 0
	}

	/**
	 * Create a rate latency curve based on the passed rate and latency.
	 *
	 * @param c_dnc
	 * 		The curve to which the rate latency has to be created.
	 * @param rate
	 * 		The rate of the affine curve.
	 * @param latency
	 *		Latency of the affine curve.
	 *
	 */
	private void makeRateLatency(Curve_Disco_Affine c_dnc, Num rate, Num latency) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		if (rate.equals(num.getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, latency);
			return;
		}
		if (rate.eqZero() || latency.equals(num.getPositiveInfinity())) {
			makeHorizontal(c_dnc, Num.getFactory(Calculator.getInstance().getNumBackend()).createZero());
			return;
		}
		if (latency.leqZero()) {
			makePeakRate(c_dnc, rate);
			return;
		}

		LinearSegment_Disco[] segments = new LinearSegment_Disco[2];

		segments[0] = new LinearSegment_Disco(num.createZero(), num.createZero(), num.createZero(), false);

		segments[1] = new LinearSegment_Disco(latency, num.createZero(), rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_rate_latency = true;
	}

	/**
	 * Create a token bucket affine curve based on the passed rate and burst.
	 *
	 * @param c_dnc
	 * 		The curve to which the token bucket has to be created.
	 * @param rate
	 * 		The rate of the affine curve.
	 * @param burst
	 *		Burst of the affine curve.
	 *
	 */
	private void makeTokenBucket(Curve_Disco_Affine c_dnc, Num rate, Num burst) {
		Num num = Num.getFactory(Calculator.getInstance().getNumBackend());
		if (rate.equals(num.getPositiveInfinity())
				|| burst.equals(num.getPositiveInfinity())) {
			makeDelayedInfiniteBurst(c_dnc, num.createZero());
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

		segments[0] = new LinearSegment_Disco(num.createZero(), num.createZero(), num.createZero(), false);

		segments[1] = new LinearSegment_Disco(num.createZero(), burst, rate, true);

		c_dnc.setSegments(segments);
		c_dnc.is_token_bucket = true;
	}
}
