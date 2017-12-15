/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.LinearSegment;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.implementations.RealDoublePrecision;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Curve_MPARTC_PwAffine implements CurvePwAffine {
	private static Curve_MPARTC_PwAffine instance = new Curve_MPARTC_PwAffine();

	protected ch.ethz.rtc.kernel.Curve rtc_curve;

	protected boolean is_delayed_infinite_burst = false;
	protected boolean is_rate_latency = false;
	protected boolean is_token_bucket = false;

	protected boolean has_rate_latency_meta_info = false;
	protected List<Curve_MPARTC_PwAffine> rate_latencies = new LinkedList<Curve_MPARTC_PwAffine>();

	protected boolean has_token_bucket_meta_info = false;
	protected List<Curve_MPARTC_PwAffine> token_buckets = new LinkedList<Curve_MPARTC_PwAffine>();

	/**
	 * Creates a <code>Curve</code> instance with a single segment on the x-axis.
	 */
	protected Curve_MPARTC_PwAffine() {
		createZeroSegmentsCurve(1);
	}

	// --------------------------------------------------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------------------------------------------------

	protected Curve_MPARTC_PwAffine(CurvePwAffine curve) {
		copy(curve);
	}

	/**
	 * Creates a <code>Curve</code> instance with <code>segment_count</code> empty
	 * <code>LinearSegment</code> instances.
	 *
	 * @param segment_count
	 *            the number of segments
	 */
	protected Curve_MPARTC_PwAffine(int segment_count) {
		createZeroSegmentsCurve(segment_count);
	}

	public static Curve_MPARTC_PwAffine getFactory() {
		return instance;
	}

	// --------------------------------------------------------------------------------------------------------------
	// Interface Implementations
	// --------------------------------------------------------------------------------------------------------------

	// Attention
	// * Default behavior of the DiscoDNC cannot be reenacted with the RTC.
	// * DiscoDNC initializes curves with overlapping (x,y,r)=(0,0,0) segments.
	// That causes an error with the RTC implementation.
	// We initialize with (segment_number,segment_number,0) instead.
	private void createZeroSegmentsCurve(int segment_count) {
		SegmentList segList_rtc = new SegmentList();
		for (int i = 0; i < segment_count; i++) {
			segList_rtc.add(new Segment((double) i, (double) i, 0));
		}
		rtc_curve = new Curve(segList_rtc);
	}

	public Curve getRtc_curve() {
		return rtc_curve;
	}

	// Accepts string representations of RTC as well as 
	// DNC Curve, ArrivalCurve, ServiceCurve, and MaxServiceCurve.
	protected void initializeCurve(String curve_str) throws Exception {
		if (curve_str.substring(0, 2).equals("AC") || curve_str.substring(0, 2).equals("SC")) {
			curve_str = curve_str.substring(2);
		} else {
			if (curve_str.substring(0, 3).equals("MSC")) {
				curve_str = curve_str.substring(3);
			}
		}

		if (curve_str.substring(0, 6).equals("Curve:")) { // RTC curve string
			String periodic = "";
			// Cut out periodic Part
			if (curve_str.contains("PeriodicPart")) {
				periodic = curve_str.substring(curve_str.indexOf("PeriodicPart"));
				curve_str = curve_str.substring(0, curve_str.indexOf("PeriodicPart"));
			}
			String s = curve_str.substring(27); // cut everything but {...}
			s = s.substring(1, s.length() - 3); // cut leading and trailing { }
			s = s.replaceAll("\\(", "").replaceAll("\\)", " ");
			SegmentList list = new SegmentList();
			String[] sl = s.split(" ");
			for (int i = 0; i < sl.length; i++) {
				String[] val = sl[i].split(",");
				list.add(new Segment(Double.parseDouble(val[0]), Double.parseDouble(val[1]),
						Double.parseDouble(val[2])));
			}
			rtc_curve = new Curve(list);
			return;
		}

		// Must to be a string representation of a "raw" curve object at this location.
		if (curve_str.charAt(0) != '{' || curve_str.charAt(curve_str.length() - 1) != '}') {
			throw new RuntimeException("Invalid string representation of a curve.");
		}

		// Remove enclosing curly brackets
		String curve_str_internal = curve_str.substring(1, curve_str.length() - 1);

		String[] segments_to_parse = curve_str_internal.split(";");

		// TODO Might also fail due to a spot in the origin
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
			segList_rtc.add(new Segment(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(s)));
		}
		segList_rtc.simplify();
		// TODO
		// This removes the first segment if the first spot is in the origin and the
		// second starts at x = 0 -> generic solution needed
		// if (segList_rtc.size() > 1 && ((segList_rtc.get(1).x() == 0) &&
		// (segList_rtc.get(0).x() == 0) && (segList_rtc.get(0).y() == 0)) ){
		// segList_rtc.remove(0);
		// }
		// rtc_curve = new Curve(segList_rtc);
	}

	private void clearMetaInfo() {
		has_token_bucket_meta_info = false;
		is_token_bucket = false;
		token_buckets = new LinkedList<Curve_MPARTC_PwAffine>();

		has_rate_latency_meta_info = false;
		is_rate_latency = false;
		rate_latencies = new LinkedList<Curve_MPARTC_PwAffine>();
	}

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
	public void copy(de.uni_kl.cs.disco.curves.Curve curve) {
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

			this.is_delayed_infinite_burst = ((CurvePwAffine) curve).isDelayedInfiniteBurst();
			this.is_rate_latency = ((CurvePwAffine) curve).isRateLatency();
			this.is_token_bucket = ((CurvePwAffine) curve).isTokenBucket();
		} else {
			SegmentList segList_rtc = new SegmentList();
			LinearSegment seg_tmp;

			// TODO Might also fail due to a spot in the origin
			for (int i = 0; i < curve.getSegmentCount(); i++) {
				seg_tmp = curve.getSegment(i);

				segList_rtc.add(new Segment(seg_tmp.getX().doubleValue(), seg_tmp.getY().doubleValue(),
						seg_tmp.getGrad().doubleValue()));
			}
			this.rtc_curve = new Curve(segList_rtc);
		}
	}

	// ------------------------------------------------------------
	// Curve's segments
	// ------------------------------------------------------------

	/**
	 * Starting at 0 as the RTC SegmentList extends ArrayList.
	 */
	public LinearSegment_MPARTC_PwAffine getSegment(int pos) {
		LinearSegment_MPARTC_PwAffine s = new LinearSegment_MPARTC_PwAffine(0, 0, 0);
		s.setRtc_segment(rtc_curve.aperiodicSegments().get(pos));
		return s;
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
	 * Attention: We assume that RTC curves are left-continuous as we cannot make
	 * this explicit. Therefore, we return the first segment that defines the given
	 * x. This behavior coincides with getSegmentLimitRight(x).
	 */
	public int getSegmentDefining(Num x) {
		return getSegmentLimitRight(x);
	}

	public int getSegmentLimitRight(Num x) {
		if (x.equals(Num.getFactory().getPositiveInfinity())) {
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

		Segment new_segment = new Segment(s.getX().doubleValue(), s.getY().doubleValue(), s.getGrad().doubleValue());

		rtc_curve.aperiodicSegments().add(pos, new_segment);
		clearMetaInfo();
	}

	public void removeSegment(int pos) {
		rtc_curve.aperiodicSegments().remove(pos);
		clearMetaInfo();
	}

	// ------------------------------------------------------------
	// Curve properties
	// ------------------------------------------------------------

	public boolean isDiscontinuity(int pos) {
		return (pos + 1 < getSegmentCount()
				&& (Math.abs(getSegmentRTC(pos + 1).x() - getSegmentRTC(pos).x())) < RealDoublePrecision.getInstance()
						.createEpsilon().doubleValue());
	}

	public boolean isRealDiscontinuity(int pos) {
		return (isDiscontinuity(pos)
				&& (Math.abs(getSegmentRTC(pos + 1).y() - getSegmentRTC(pos).y())) >= RealDoublePrecision.getInstance()
						.createEpsilon().doubleValue());
	}

	public boolean isUnrealDiscontinuity(int pos) {
		return (isDiscontinuity(pos)
				&& (Math.abs(getSegmentRTC(pos + 1).y() - getSegmentRTC(pos).y())) < RealDoublePrecision.getInstance()
						.createEpsilon().doubleValue());
	}

	public boolean isWideSenseIncreasing() {
		double y = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for initial
		// comparison in the loop.
		for (int i = 0; i < getSegmentCount(); i++) {
			if (getSegmentRTC(i).y() < y || getSegmentRTC(i).s() < 0.0) {
				return false;
			}
			y = getSegmentRTC(i).y();
		}
		return true;
	}

	public boolean isConvex() {
		return isConvexIn(Num.getFactory().getZero(), Num.getFactory().getPositiveInfinity());
	}

	public boolean isConvexIn(Num a, Num b) {
		double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for
		// initial comparison in the loop.

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
				gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y() / getSegmentRTC(i + 1).x()
						- getSegmentRTC(i).x();
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
		double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for
		// initial comparison in the loop.

		int i_start = getSegmentDefining(a);
		int i_end = getSegmentDefining(b);
		for (int i = i_start; i <= i_end; i++) {
			if (i == i_end && getSegmentRTC(i).x() == b.doubleValue()) {
				break;
			}
			double gradient;
			// Handles discontinuities
			if (i < getSegmentCount() - 1) {
				gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y() / getSegmentRTC(i + 1).x()
						- getSegmentRTC(i).x();
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
		double last_gradient = Double.NEGATIVE_INFINITY; // No need to create an object as this value is only set for
		// initial comparison in the loop.

		for (int i = 0; i < getSegmentCount(); i++) {
			// Skip the horizontal part at the beginning
			if (last_gradient == Double.POSITIVE_INFINITY && getSegmentRTC(i).s() == 0.0) {
				continue;
			}

			double gradient;
			if (i < getSegmentCount() - 1) {
				gradient = getSegmentRTC(i + 1).y() - getSegmentRTC(i).y() / getSegmentRTC(i + 1).x()
						- getSegmentRTC(i).x();
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

	// ------------------------------------------------------------
	// Curve function values
	// ------------------------------------------------------------

	public Num f(Num x) {
		// Assume left-continuity --> getYmin
		return Num.getFactory().create(rtc_curve.getYmin(x.doubleValue()));
	}

	public Num fLimitRight(Num x) {
		// Assume left-continuity --> getYmin
		return Num.getFactory().create(rtc_curve.getYmin(x.doubleValue()));
	}

	public Num f_inv(Num y) {
		return f_inv(y, false);
	}

	public Num f_inv(Num y, boolean rightmost) {
		if (rightmost) {
			return Num.getFactory().create(rtc_curve.getXmax(y.doubleValue()));
		} else {
			return Num.getFactory().create(rtc_curve.getXmin(y.doubleValue()));
		}
	}

	public Num getLatency() {
		return Num.getFactory().create(rtc_curve.getXmax(0.0));
	}

	public Num getBurst() {
		return Num.getFactory().create(rtc_curve.y0epsilon());
	}

	public Num getGradientLimitRight(Num x) {
		int i = getSegmentLimitRight(x);
		if (i < 0) {
			return Num.getFactory().createNaN();
		}
		return Num.getFactory().create(getSegmentRTC(i).s());
	}

	public Num getTB_Burst() {
		// RTC Token Buckets cannot pass through the origin
		return Num.getFactory().create(rtc_curve.y0epsilon());
	}

	@Override
	public void setTB_MetaInfo(boolean has_token_bucket_meta_info) {
		this.has_token_bucket_meta_info = has_token_bucket_meta_info;
	}

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
		return Num.getFactory().create(rtc_curve.aperiodicSegments().lastSegment().s());
	}

	// Attention: RTC simplify(); did something different.
	// TODO: This solution is tailored to a single case we encountered.
	public void beautify() {
		if (rtc_curve.aperiodicSegments().size() > 1) {
			if (rtc_curve.aperiodicSegments().get(0).equals(rtc_curve.aperiodicSegments().get(1))) {
				rtc_curve.aperiodicSegments().remove(0);
			}
		}
	}

	// Burst delay
	public boolean isDelayedInfiniteBurst() {
		return is_delayed_infinite_burst;
	}

	// ------------------------------------------------------------
	// Curve manipulation
	// ------------------------------------------------------------

	// ------------------------------------------------------------
	// Specific curve shapes
	// ------------------------------------------------------------

	public boolean isRateLatency() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	public void setRateLateny(boolean is_rate_latency) {
		this.is_rate_latency = is_rate_latency;
	}

	public int getRL_ComponentCount() {
		decomposeIntoRateLatencies();
		return rate_latencies.size();
	}

	/**
	 * Returns the rate latency the defined the overall curve's segment i.
	 */
	public Curve_MPARTC_PwAffine getRL_Component(int i) {
		decomposeIntoRateLatencies();
		if (is_rate_latency && i == 0) {
			return this.copy();
		}
		return rate_latencies.get(i);
	}

	private void decomposeIntoRateLatencies() {
		if (has_rate_latency_meta_info == true) {
			return;
		}

		if (CalculatorConfig.getInstance().exec_service_curve_checks() && !this.isConvex()) {
			if (this.equals(this.createZeroDelayInfiniteBurst())) {
				rate_latencies = new ArrayList<Curve_MPARTC_PwAffine>();
				rate_latencies.add(this.createRateLatency(Num.getFactory().createPositiveInfinity(),
						Num.getFactory().createZero()));
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
				double latency = getSegmentRTC(i).x() - (getSegmentRTC(i).y() / getSegmentRTC(i).s());
				if (latency < 0.0) {
					continue;
				}
				rate_latencies.add(this.createRateLatency(rate, latency));
			}
		}

		is_rate_latency = rate_latencies.size() == 1;

		has_rate_latency_meta_info = true;
	}

	// Token bucket
	public boolean isTokenBucket() {
		decomposeIntoTokenBuckets();
		return is_token_bucket;
	}

	@Override
	public void setTokenBucket(boolean is_token_bucket) {
		this.is_token_bucket = is_token_bucket;
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
			// TODO breaks tests
			// return;
		}

		if (CalculatorConfig.getInstance().exec_arrival_curve_checks() && !this.isConcave()) {
			throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
		}

		token_buckets = new ArrayList<Curve_MPARTC_PwAffine>();
		for (int i = 0; i < getSegmentCount(); i++) {
			if (isDiscontinuity(i)) {
				continue;
			}
			double rate = getSegmentRTC(i).s();
			double burst = getSegmentRTC(i).y() - (getSegmentRTC(i).x() * getSegmentRTC(i).s());
			token_buckets.add(this.createTokenBucket(rate, burst));
		}

		is_token_bucket = token_buckets.size() == 1;

		has_token_bucket_meta_info = true;
	}

	// Attention:
	// Arrival curves are not enforced to be 0 in the origin because
	// the RTC toolbox does not allow for such the discontinuity implied for
	// token bucket constrained arrival constraints.
	// --------------------------------------------------------------------------------------------------------------
	// Factory Implementation
	// --------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------------------------------------
	// Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public Curve_MPARTC_PwAffine createCurve(List<LinearSegment> segments) {
		Curve_MPARTC_PwAffine msc_rtc = new Curve_MPARTC_PwAffine();

		// Check for origin + burst
		if (segments.size() > 1 && segments.get(0).getX().eqZero() && segments.get(1).getX().eqZero()) {
			segments.remove(0);
		}
		SegmentList segments_rtc = new SegmentList();

		for (LinearSegment s : segments) {
			segments_rtc.add(new Segment(s.getX().doubleValue(), s.getY().doubleValue(), s.getGrad().doubleValue()));
		}

		msc_rtc.initializeWithSegments(segments_rtc);
		return msc_rtc;
	}

	public Curve_MPARTC_PwAffine createZeroCurve() {
		return new Curve_MPARTC_PwAffine(); // CurveRTC constructor's default behavior
	}

	public Curve_MPARTC_PwAffine createHorizontal(double y) {
		Curve_MPARTC_PwAffine c_rtc = new Curve_MPARTC_PwAffine();
		makeHorizontal(c_rtc, y);
		return c_rtc;
	}

	/**
	 * Creates a horizontal curve.
	 *
	 * @param y
	 *            the y-intercept of the curve
	 * @return a <code>Curve</code> instance
	 */
	public Curve_MPARTC_PwAffine createHorizontal(Num y) {
		return createHorizontal(y.doubleValue());
	}

	// --------------------------------------------------------------------------------------------------------------
	// Service Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public ServiceCurve_MPARTC_PwAffine createServiceCurve() {
		return new ServiceCurve_MPARTC_PwAffine();
	}

	public ServiceCurve_MPARTC_PwAffine createServiceCurve(int segment_count) {
		return new ServiceCurve_MPARTC_PwAffine(segment_count);
	}

	public ServiceCurve_MPARTC_PwAffine createServiceCurve(String service_curve_str) throws Exception {
		return new ServiceCurve_MPARTC_PwAffine(service_curve_str);
	}

	public ServiceCurve_MPARTC_PwAffine createServiceCurve(CurvePwAffine curve) {
		return new ServiceCurve_MPARTC_PwAffine(curve);
	}

	public ServiceCurve_MPARTC_PwAffine createZeroService() {
		return new ServiceCurve_MPARTC_PwAffine(); // ServiceCurveRTC constructor's default behavior
	}

	/**
	 * Creates an infinite burst curve with zero delay.
	 *
	 * @return a <code>ServiceCurve</code> instance
	 */
	public ServiceCurve_MPARTC_PwAffine createZeroDelayInfiniteBurst() {
		ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
		makeHorizontal(sc_rtc, Double.POSITIVE_INFINITY);
		return sc_rtc;
	}

	public ServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurst(double delay) {
		ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
		makeDelayedInfiniteBurst(sc_rtc, delay);
		return sc_rtc;
	}

	public ServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurst(Num delay) {
		return createDelayedInfiniteBurst(delay.doubleValue());
	}

	public ServiceCurve_MPARTC_PwAffine createRateLatency(double rate, double latency) {
		ServiceCurve_MPARTC_PwAffine sc_rtc = new ServiceCurve_MPARTC_PwAffine();
		makeRateLatency(sc_rtc, rate, latency);
		return sc_rtc;
	}

	public ServiceCurve_MPARTC_PwAffine createRateLatency(Num rate, Num latency) {
		return createRateLatency(rate.doubleValue(), latency.doubleValue());
	}

	// --------------------------------------------------------------------------------------------------------------
	// Arrival Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public ArrivalCurve_MPARTC_PwAffine createArrivalCurve() {
		return new ArrivalCurve_MPARTC_PwAffine();
	}

	public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(int segment_count) {
		return new ArrivalCurve_MPARTC_PwAffine(segment_count);
	}

	public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(String arrival_curve_str) throws Exception {
		return new ArrivalCurve_MPARTC_PwAffine(arrival_curve_str);
	}

	public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(CurvePwAffine curve) {
		return new ArrivalCurve_MPARTC_PwAffine(curve);
	}

	public ArrivalCurve_MPARTC_PwAffine createArrivalCurve(CurvePwAffine curve, boolean remove_latency) {
		return createArrivalCurve(CurvePwAffine.removeLatency(curve));
	}

	public ArrivalCurve_MPARTC_PwAffine createZeroArrivals() {
		return new ArrivalCurve_MPARTC_PwAffine(); // ArrivalCurveRTC constructor's default behavior
	}

	public ArrivalCurve_MPARTC_PwAffine createPeakArrivalRate(double rate) {
		ArrivalCurve_MPARTC_PwAffine ac_rtc = new ArrivalCurve_MPARTC_PwAffine();
		makePeakRate(ac_rtc, rate);
		return ac_rtc;
	}

	public ArrivalCurve_MPARTC_PwAffine createPeakArrivalRate(Num rate) {
		return createPeakArrivalRate(rate.doubleValue());
	}

	public ArrivalCurve_MPARTC_PwAffine createTokenBucket(double rate, double burst) {
		ArrivalCurve_MPARTC_PwAffine ac_rtc = new ArrivalCurve_MPARTC_PwAffine();
		makeTokenBucket(ac_rtc, rate, burst);
		return ac_rtc;
	}

	public ArrivalCurve_MPARTC_PwAffine createTokenBucket(Num rate, Num burst) {
		return createTokenBucket(rate.doubleValue(), burst.doubleValue());
	}

	// --------------------------------------------------------------------------------------------------------------
	// Maximum Service Curve Constructors
	// --------------------------------------------------------------------------------------------------------------

	// ------------------------------------------------------------
	// DiscoDNC compliance
	// ------------------------------------------------------------
	public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve() {
		return new MaxServiceCurve_MPARTC_PwAffine();
	}

	public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(int segment_count) {
		return new MaxServiceCurve_MPARTC_PwAffine(segment_count);
	}

	public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(String max_service_curve_str) throws Exception {
		return new MaxServiceCurve_MPARTC_PwAffine(max_service_curve_str);
	}

	public MaxServiceCurve_MPARTC_PwAffine createMaxServiceCurve(CurvePwAffine curve) {
		return new MaxServiceCurve_MPARTC_PwAffine(curve);
	}

	public MaxServiceCurve_MPARTC_PwAffine createInfiniteMaxService() {
		return createDelayedInfiniteBurstMSC(Num.getFactory().createZero());
	}

	public MaxServiceCurve_MPARTC_PwAffine createZeroDelayInfiniteBurstMSC() {
		MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
		makeHorizontal(msc_rtc, Double.POSITIVE_INFINITY);
		return msc_rtc;
	}

	public MaxServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurstMSC(double delay) {
		MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
		makeDelayedInfiniteBurst(msc_rtc, delay);
		return msc_rtc;
	}

	public MaxServiceCurve_MPARTC_PwAffine createDelayedInfiniteBurstMSC(Num delay) {
		return createDelayedInfiniteBurstMSC(delay.doubleValue());
	}

	public MaxServiceCurve_MPARTC_PwAffine createRateLatencyMSC(double rate, double latency) {
		MaxServiceCurve_MPARTC_PwAffine msc_rtc = new MaxServiceCurve_MPARTC_PwAffine();
		makeRateLatency(msc_rtc, rate, latency);
		return msc_rtc;
	}

	public MaxServiceCurve_MPARTC_PwAffine createRateLatencyMSC(Num rate, Num latency) {
		return createRateLatencyMSC(rate.doubleValue(), latency.doubleValue());
	}

	// --------------------------------------------------------------------------------------------------------------
	// Curve assembly
	// --------------------------------------------------------------------------------------------------------------
	private void makeHorizontal(Curve_MPARTC_PwAffine c_rtc, double y) {
		c_rtc.initializeWithSegment(new Segment(0.0, y, 0.0));

		if (y == Double.POSITIVE_INFINITY) {
			c_rtc.is_delayed_infinite_burst = true;
		}
	}

	private void makeDelayedInfiniteBurst(Curve_MPARTC_PwAffine c_rtc, double delay) {
		if (delay < 0.0) {
			throw new IllegalArgumentException("Delayed infinite burst curve must have delay >= 0.0");
		}
		if (delay == 0.0) { // Point in the origin does not work with RTC
			makeHorizontal(c_rtc, Double.POSITIVE_INFINITY);
			return;
		}

		Segment segment0 = new Segment(0.0, 0.0, 0.0);
		Segment segment1 = new Segment(delay, Double.POSITIVE_INFINITY, 0.0);

		SegmentList segments = new SegmentList();
		segments.add(segment0);
		segments.add(segment1);

		c_rtc.initializeWithSegments(segments);
		c_rtc.is_delayed_infinite_burst = true;
	}

	private void makePeakRate(Curve_MPARTC_PwAffine c_rtc, double rate) {
		if (rate == Double.POSITIVE_INFINITY) {
			throw new IllegalArgumentException(
					"Peak rate with rate infinity equals a delayed infinite burst curve with delay < 0.0");
		}
		if (rate == 0.0) {
			makeHorizontal(c_rtc, 0.0);
			return;
		}

		Segment segment = new Segment(0.0, 0.0, rate);
		c_rtc.initializeWithSegment(segment);
		// TODO Is it a RL with L=0 (in the PMOO's point of view)? Could also be a token
		// bucket. Set both meta infos?
		// decomposeIntoRateLatencies();
		// decomposeIntoTokenBuckets();
	}

	private void makeRateLatency(Curve_MPARTC_PwAffine c_rtc, double rate, double latency) {
		if (rate == Double.POSITIVE_INFINITY) {
			makeDelayedInfiniteBurst(c_rtc, latency);
			return;
		}
		if (rate == 0.0 || latency == Double.POSITIVE_INFINITY) {
			makeHorizontal(c_rtc, 0.0);
			return;
		}
		if (latency == 0.0) {
			makePeakRate(c_rtc, rate);
			return;
		}

		Segment segment0 = new Segment(0.0, 0.0, 0.0);
		Segment segment1 = new Segment(latency, 0.0, rate);

		SegmentList segments = new SegmentList();
		segments.add(segment0);
		segments.add(segment1);

		c_rtc.initializeWithSegments(segments);
		// TODO: Do decomposition for PMOO?
		// TODO: Remark: PMOO throws exception due to non linear increasing segments
		// decomposeIntoRateLatencies(); // instead of:
		c_rtc.is_rate_latency = true;
	}

	private void makeTokenBucket(Curve_MPARTC_PwAffine c_rtc, double rate, double burst) {
		if (rate == Double.POSITIVE_INFINITY || burst == Double.POSITIVE_INFINITY) {
			makeDelayedInfiniteBurst(c_rtc, 0.0);
			return;
		}
		if (rate == 0.0) { // burst is finite
			makeHorizontal(c_rtc, burst);
			return;
		}
		if (burst == 0.0) {
			makePeakRate(c_rtc, rate);
			return;
		}

		// Again, spot in the origin does not work with RTC
		// Segment segment0 = new Segment( 0.0, 0.0, 0.0 );
		Segment segment1 = new Segment(0.0, burst, rate);

		SegmentList segments = new SegmentList();
		// segments.add( segment0 );
		segments.add(segment1);

		c_rtc.initializeWithSegments(segments);
		// TODO: Do decomposition for PMOO?
		// decomposeIntoTokenBuckets(); // instead of:
		c_rtc.is_token_bucket = true;
	}
}
