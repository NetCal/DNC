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

package unikl.disco.curves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;
import unikl.disco.numbers.NumUtils;

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
 * 
 */
public class Curve {
	protected static final int OPERATOR_ADD = 0;
	protected static final int OPERATOR_SUB = 1;
	protected static final int OPERATOR_MIN = 2;
	protected static final int OPERATOR_MAX = 3;

	protected LinearSegment[] segments;

	protected boolean is_delayed_infinite_burst = false;

	private boolean has_token_bucket_meta_info = false;
	protected boolean is_token_bucket = false;
	protected List<Curve> token_buckets = new LinkedList<Curve>();

	private boolean has_rate_latency_meta_info = false;
	protected boolean is_rate_latency = false;
	protected List<Curve> rate_latencies = new LinkedList<Curve>();

	/**
	 * Creates an empty <code>Curve</code> instance.
	 */
	protected Curve() {
		createNullSegmentsCurve( 1 );
	}

	protected Curve( Curve curve ) {
		initializeCurve( curve );
	}
	
	/**
	 * Creates a <code>Curve</code> instance with <code>segment_count</code>
	 * empty <code>LinearSegment</code> instances.
	 * 
	 * @param segment_count the number of segments
	 */
	protected Curve( int segment_count ) {
		createNullSegmentsCurve( segment_count );
	}
	
	public boolean isDelayedInfiniteBurst() {
		return is_delayed_infinite_burst;
	}	
	
	private void createNullSegmentsCurve( int segment_count ) {
		segments = new LinearSegment[segment_count];
		
		segments[0] = LinearSegment.createNullSegment();
		
		for (int i = 1; i < segment_count; i++) {
			segments[i] = LinearSegment.createNullSegment();
			segments[i].leftopen = true;
		}
	}
	
	// Accepts string representations of Curve, ArrivalCurve, ServiceCurve, and MaxServiceCurve
	protected void initializeCurve( String curve_str ) throws Exception {
		if ( curve_str.substring( 0, 2 ).equals( "AC" ) || curve_str.substring( 0, 2 ).equals( "SC" ) ) {
			curve_str = curve_str.substring( 2 );
		} else {
			if ( curve_str.substring( 0, 3 ).equals( "MSC" ) ) {
				curve_str = curve_str.substring( 3 );
			} 
		}
		
		// Must to be a string representation of a "raw" curve object at this location.
		if( curve_str.charAt( 0 ) != '{' || curve_str.charAt( curve_str.length()-1 ) != '}' ) {
			throw new RuntimeException( "Invalid string representation of a curve." );
		}
		
		// Remove enclosing curly brackets
		String curve_str_internal = curve_str.substring( 1, curve_str.length()-1 );
		
		String[] segments_to_parse = curve_str_internal.split( ";" );
		segments = new LinearSegment[ segments_to_parse.length ]; // No need to use createNullSegments( i ) because we will store parsed segments
		
		for( int i = 0; i<segments_to_parse.length; i++ ) {
			segments[i] = new LinearSegment( segments_to_parse[i] );
		}
	}

	public void initializeCurve( Curve curve ) {
		createNullSegmentsCurve( curve.getSegmentCount() );
		for ( int i = 0; i < curve.getSegmentCount(); i++ ) {
			segments[i] = curve.getSegment( i ).copy();
		}
	}
	
	/**
	 * Initializes this curve with a null curve.
	 */
	protected void initializeNullCurve() {
		initializeHorizontal( NumFactory.createZero() );
	}

	/**
	 * Initializes this curve with a horizontal curve.
	 * 
	 * @param y the y-intercept of the curve
	 */
	protected void initializeHorizontal( double y ) {
		initializeHorizontal( NumFactory.createNum( y ) );
	}
	
	/**
	 * Initializes this curve with a horizontal curve.
	 * 
	 * @param y the y-intercept of the curve
	 */
	protected void initializeHorizontal( Num y ) {
		createNullSegmentsCurve( 1 );
		getSegment(0).y = y;
	}
	
	/**
	 * Initializes this curve with a burst curve without latency.
	 */
	protected void initializeZeroDelayInfiniteBurst() {
		initializeDelayedInfiniteBurst( NumFactory.createZero() );
	}
	
	/**
	 * Creates a burst delay curve.
	 * 
	 * @param latency the delay, which must be &gt;= 0.0
	 */
	protected void initializeDelayedInfiniteBurst( Num latency ) {
		createNullSegmentsCurve( 2 );
		
		getSegment( 0 ).x        = NumFactory.createZero();
		getSegment( 0 ).y        = NumFactory.createZero();
		getSegment( 0 ).grad     = NumFactory.createZero();
		getSegment( 0 ).leftopen = false;
		
		getSegment( 1 ).x        = NumUtils.max( NumFactory.getZero(), latency );
		getSegment( 1 ).y        = NumFactory.createPositiveInfinity();
		getSegment( 1 ).grad     = NumFactory.createZero();
		getSegment( 1 ).leftopen = true;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 */
	protected void initializeTokenBucket( double rate, double burst ) {
		initializeTokenBucket( NumFactory.createNum( rate ), NumFactory.createNum( burst ) ) ;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 */
	// Do not move to ArrivalCurve as MaxServiceCurve can also be a token bucket.
	protected void initializeTokenBucket( Num rate, Num burst ) {
		createNullSegmentsCurve( 2 );

		getSegment( 0 ).x        = NumFactory.createZero();
		getSegment( 0 ).y        = NumFactory.createZero();
		getSegment( 0 ).grad     = NumFactory.createZero();
		getSegment( 0 ).leftopen = false;
		
		getSegment( 1 ).x        = NumFactory.createZero();
		getSegment( 1 ).y        = burst;
		getSegment( 1 ).grad     = rate;
		getSegment( 1 ).leftopen = true;
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 */
	protected void initializeRateLatency( double rate, double latency ) {
		initializeRateLatency( NumFactory.createNum( rate ), NumFactory.createNum( latency ) );
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 */
	// Do not move to ServiceCurve as MaxServiceCurve can also be a token bucket.
	protected void initializeRateLatency( Num rate, Num latency ) {
		if ( latency.equals( NumFactory.getZero() ) ) {
			createNullSegmentsCurve( 1 );

			getSegment(0).x        = NumFactory.createZero();
			getSegment(0).y        = NumFactory.createZero();
			getSegment(0).grad     = rate;
			getSegment(0).leftopen = false;
		} else {
			createNullSegmentsCurve( 2 );
			
			getSegment(0).x        = NumFactory.createZero();
			getSegment(0).y        = NumFactory.createZero();
			getSegment(0).grad     = NumFactory.createZero();
			getSegment(0).leftopen = false;

			getSegment(1).x        = latency;
			getSegment(1).y        = NumFactory.createZero();
			getSegment(1).grad     = rate;
			getSegment(1).leftopen = false;
		}
	}
	
	/**
	 * Creates a null curve.
	 * 
	 * @return a <code>Curve</code> instance
	 */
	private static Curve createNullCurve() {
		return createHorizontal( NumFactory.createZero() );
	}
	
	/**
	 * Creates a horizontal curve.
	 * 
	 * @param y the y-intercept of the curve
	 * @return a <code>Curve</code> instance
	 */
	private static Curve createHorizontal( Num y ) {
		Curve c = new Curve(1);
		c.getSegment(0).y = y;
		return c;
	}

	/**
	 * Creates a burst delay curve.
	 * 
	 * @param latency the delay, which must be &gt;= 0.0
	 * @return a <code>Curve</code> instance
	 */
	private static Curve createDelayedInfiniteBurst( Num latency ) {
		Curve c = new Curve(2);
		c.initializeDelayedInfiniteBurst( latency );
		return c;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 * @return a <code>Curve</code> instance
	 */
	private static Curve createTokenBucket( Num rate, Num burst ) {
		Curve c = new Curve(2);

		c.getSegment(0).x        = NumFactory.createZero();
		c.getSegment(0).y        = NumFactory.createZero();
		c.getSegment(0).grad     = NumFactory.createZero();
		c.getSegment(0).leftopen = false;
		
		c.getSegment(1).x        = NumFactory.createZero();
		c.getSegment(1).y        = burst;
		c.getSegment(1).grad     = rate;
		c.getSegment(1).leftopen = true;
		
		return c;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 * @return a <code>Curve</code> instance
	 */
	public static Curve createTokenBucket( double rate, double burst ) {
		return createTokenBucket( NumFactory.createNum( rate ), NumFactory.createNum( burst ) ) ;
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>Curve</code> instance
	 */
	private static Curve createRateLatency( Num rate, Num latency ) {
		Curve c;
		if ( latency.equals( NumFactory.getZero() ) ) {
			c = new Curve(1);

			c.getSegment(0).x        = NumFactory.createZero();
			c.getSegment(0).y        = NumFactory.createZero();
			c.getSegment(0).grad     = rate;
			c.getSegment(0).leftopen = false;
		} else {
			c = new Curve(2);
			
			c.getSegment(0).x        = NumFactory.createZero();
			c.getSegment(0).y        = NumFactory.createZero();
			c.getSegment(0).grad     = NumFactory.createZero();
			c.getSegment(0).leftopen = false;

			c.getSegment(1).x        = latency;
			c.getSegment(1).y        = NumFactory.createZero();
			c.getSegment(1).grad     = rate;
			c.getSegment(1).leftopen = false;
		}
		
		return c;
	}

	/**
	 * Creates a new curve from a list of token bucket curves.
	 * 
	 * @param token_buckets a list of token bucket curves
	 * @return a <code>Curve</code> instance
	 */
	public static Curve createFromTokenBuckets(List<Curve> token_buckets) {
		Curve c = createDelayedInfiniteBurst( NumFactory.createZero() );
		for (Iterator<Curve> tb_iter = token_buckets.iterator(); tb_iter.hasNext(); ) {
			c = min( c, (Curve) tb_iter.next() );
		}
		return c;
	}
	
	
	/**
	 * Returns a copy of this instance.
	 * 
	 * @return a copy of this instance.
	 */
	public Curve copy() {
		Curve c_copy = new Curve();
		c_copy.initializeCurve( this );
		return c_copy;
	}

	private void clearMetaInfo() {
		has_token_bucket_meta_info = false;
		is_token_bucket = false;
		token_buckets = new LinkedList<Curve>();

		has_rate_latency_meta_info = false;
		is_rate_latency = false;
		rate_latencies = new LinkedList<Curve>();
	}
	
	/**
	 * Decomposes this curve into a list of token bucket curves and
	 * stores this list in the curve's <code>token_buckets</code> field.<br>
	 * Note: Curve must be concave.
	 */
	private void decomposeIntoTokenBuckets() {
		if ( has_token_bucket_meta_info == true ) {
			return;
		}
		
		if( CalculatorConfig.ARRIVAL_CURVE_CHECKS && !this.isConcave() ) {
			throw new RuntimeException("Can only decompose concave arrival curves into token buckets.");
		}

		token_buckets = new ArrayList<Curve>();
		for (int i = 0; i < segments.length; i++) {
			if (isDiscontinuity(i)) {
				continue;
			}
			Num rate = segments[i].grad;
			Num burst = NumUtils.sub( segments[i].y, NumUtils.mult( segments[i].x, segments[i].grad ) );
			token_buckets.add( createTokenBucket( rate, burst ) );
		}
		
		if ( token_buckets.size() == 1 ) {
			is_token_bucket = true;
		} else {
			is_token_bucket = false;
		}
		
		has_token_bucket_meta_info = true;
	}

	@Deprecated
	public boolean isTokenBucket() {
		decomposeIntoTokenBuckets();
		return is_token_bucket;		
	}

	/**
	 * Returns the number of token buckets the curve can be decomposed into.
	 * 
	 * @return the number of token buckets
	 */
	public int getTBComponentCount() {
		decomposeIntoTokenBuckets();
		return token_buckets.size();
	}
	
	/**
	 * Returns a list of token bucket curves that this curve can be
	 * decomposed into.
	 * 
	 * @return the list of token buckets
	 */
	@Deprecated
	public final List<Curve> getTBComponents() {
		decomposeIntoTokenBuckets();
		return token_buckets;
	}
	
	/**
	 * Returns the <code>i</code>the token bucket curve that this curve can be
	 * decomposed into.
	 * 
	 * @param i the number of the token bucket
	 * @return the token bucket
	 */
	public Curve getTBComponent(int i) {
		decomposeIntoTokenBuckets();
		return token_buckets.get(i);
	}
	
	/**
	 * Decomposes this curve into a list of rate latency curves and
	 * stores this list in the curve's <code>rate_latencies</code> field.<br>
	 * Note: Curve must be convex.
	 */
	private void decomposeIntoRateLatencies() {
		if ( has_rate_latency_meta_info == true ) {
			return;
		}
		
		if( CalculatorConfig.SERVICE_CURVE_CHECKS && !this.isConvex() ) {
			if ( this.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
				rate_latencies = new ArrayList<Curve>();
				rate_latencies.add( createRateLatency( NumFactory.createPositiveInfinity(), NumFactory.createZero() ) );
			} else {
				throw new RuntimeException("Can only decompose convex service curves into rate latency curves.");
			}
		} else {
			rate_latencies = new ArrayList<Curve>();
			for (int i = 0; i < segments.length; i++) {
				if (segments[i].y.equals( 0.0 ) && segments[i].grad.equals( 0.0 ) ) {
					continue;
				}
				Num rate = segments[i].grad;
				Num latency = NumUtils.sub( segments[i].x, NumUtils.div( segments[i].y, segments[i].grad ) );
				rate_latencies.add( createRateLatency( rate, latency ) );
			}
		}
		
		if( rate_latencies.size() == 1 ) {
			is_rate_latency = true;
		} else {
			is_rate_latency = false;
		}
		
		has_rate_latency_meta_info = true;
	}

	@Deprecated
	public boolean isRateLatency() {
		decomposeIntoRateLatencies();
		return is_rate_latency;
	}

	/**
	 * Returns the number of rate latency curves the curve can be decomposed into.
	 * 
	 * @return the number of rate latency curves
	 */
	public int getRLComponentCount() {
		decomposeIntoRateLatencies();
		return rate_latencies.size();
	}

	/**
	 * Returns a list of rate latency curves that this curve can be
	 * decomposed into.
	 * 
	 * @return the list of rate latency curves
	 */
	@Deprecated
	public final List<Curve> getRLComponents() {
		decomposeIntoRateLatencies();
		return rate_latencies;
	}

	/**
	 * Returns the <code>i</code>the rate latency curve that this curve can be
	 * decomposed into.
	 * 
	 * @param i the number of the rate latency curve
	 * @return the rate latency curve
	 */
	public Curve getRLComponent(int i) {
		decomposeIntoRateLatencies();
		return rate_latencies.get( i );
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
	 * Returns the x-coordinate of inflection point <code>i</code>.
	 * 
	 * @param i the index of the IP.
	 * @return the x-coordinate of the inflection point.
	 */
	@Deprecated
	public Num getIPX(int i) {
		return segments[i].x;
	}

	/**
	 * Returns the y-coordinate of inflection point <code>i</code>.
	 * 
	 * @param i the index of the IP.
	 * @return the y-coordinate of the inflection point.
	 */
	@Deprecated
	public Num getIPY(int i) {
		return segments[i].y;
	}

	/**
	 * Returns the sustained rate (the gradient of the last segment).
	 * 
	 * @return the sustained rate.
	 */
	public Num getSustainedRate() {
		return segments[segments.length-1].grad;
	}

	/**
	 * Returns the highest rate (the steepest gradient of any segment).
	 * 
	 * @return the highest rate.
	 */
	@Deprecated
	public Num getHighestRate() {
		Num max_rate = NumFactory.getNegativeInfinity(); // No need to create an object here as this negative infinity is only used for initial comparison
		for (int i = 0; i < segments.length; i++) {
			if ( !isDiscontinuity(i) && ( segments[i].grad ).greater( max_rate ) ) {
				max_rate = segments[i].grad;
			}
		}
		return max_rate;
	}

	/**
	 * Sets the sustained rate (the gradient of the last segment).
	 * Note: No checks are performed when setting the sustained rate.
	 * The caller has to ensure that <code>r</code> is larger (smaller) than
	 * the gradient of the 2nd-to-last segment to keep the curve convex (concave).
	 * 
	 * @param rate the new sustained rate
	 */
	@Deprecated
	public void setSustainedRate( Num rate ) {
		segments[segments.length-1].grad = rate;
		clearMetaInfo();
	}

	/**
	 * Returns whether the inflection point <code>i</code> is excluded
	 * from the segment or not.
	 * 
	 * @param i the index of the IP.
	 * @return <code>true</code> if the IP is excluded, <code>false</code> if not.
	 */
	@Deprecated
	public boolean isLeftopen( int i ) {
		return segments[i].leftopen;
	}

	/**
	 * Returns whether the inflection point is a (real or unreal) discontinuity.
	 * 
	 * @param i the index of the IP
	 * @return <code>true</code> if the IP is a discontinuity, <code>false</code> if not.
	 */
	public boolean isDiscontinuity( int i ) {
		return (i+1 < segments.length && ( NumUtils.abs( NumUtils.sub( segments[i+1].x, segments[i].x ) ) ).less( NumFactory.getEpsilon() ) );
	}

	/**
	 * Returns whether the inflection point is a real discontinuity, i.e. the y0
	 * of the leftopen segment differs from the previous one.
	 * 
	 * @param i the index of the IP
	 * @return <code>true</code> if the IP is a real discontinuity, <code>false</code> if not.
	 */
	public boolean isRealDiscontinuity( int i ) {
		return ( isDiscontinuity(i) && ( NumUtils.abs( NumUtils.sub( segments[i+1].y, segments[i].y ) ) ).geq( NumFactory.getEpsilon() ) );
	}

	/**
	 * Returns whether the inflection point is an unreal discontinuity, i.e. the y0
	 * of the leftopen segment is coincident with the y0 of the previous segment
	 * and therefore the unreal discontinuity may safely be removed.
	 * 
	 * @param i the index of the IP
	 * @return <code>true</code> if the IP is an unreal discontinuity, <code>false</code> if not.
	 */
	public boolean isUnrealDiscontinuity( int i ) {
		return ( isDiscontinuity(i) && ( NumUtils.abs( NumUtils.sub(segments[i+1].y, segments[i].y ) ) ).less( NumFactory.getEpsilon() ) );
	}

	/**
	 * Returns whether the current curve is a burst delay curve.
	 * 
	 * @return <code>true</code> if the curve is burst delay, <code>false</code> otherwise
	 */
	public boolean isBurstDelay() {
		return (segments.length == 2
				&& segments[0].x.equals( NumFactory.getZero() )
				&& segments[0].y.equals( NumFactory.getZero() )
				&& segments[0].grad.equals( NumFactory.getZero() )
				&& !segments[0].leftopen
				&& segments[1].x.geq( NumFactory.getZero() )
				&& segments[1].y.equals( NumFactory.getPositiveInfinity() )
				&& segments[1].grad.equals( NumFactory.getZero() )
				&& segments[1].leftopen);
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
	 * Note: It is the user's responsibility to add segments in the
	 * order of increasing x-coordinates.
	 * 
	 * @param pos the index into the segment array to add the new segment.
	 * @param s the segment to be added.
	 */
	public void addSegment(int pos, LinearSegment s) {
		if (pos < 0 || pos > segments.length) {
			throw new IllegalArgumentException("Index out of bounds (pos=" + pos + ")!");
		}
		if (s == null) {
			throw new IllegalArgumentException("Tried to insert null!");
		}
		LinearSegment[] old_segments = segments;
		segments = new LinearSegment[old_segments.length + 1];
		segments[pos] = s;
		if (pos > 0) {
			System.arraycopy(old_segments, 0, segments, 0, pos);
		}
		if (pos < old_segments.length) {
			System.arraycopy(old_segments, pos, segments, pos+1, old_segments.length-pos);
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
			throw new IllegalArgumentException("Index out of bounds (pos=" + pos + ")!");
		}
		LinearSegment[] old_segments = segments;
		segments = new LinearSegment[old_segments.length - 1];
		System.arraycopy(old_segments, 0, segments, 0, pos);
		System.arraycopy(old_segments, pos+1, segments, pos, old_segments.length-pos-1);
		
		clearMetaInfo();
	}

	/**
	 * Marks discontinuities (subsequent segments having the same
	 * x-coordinate) by setting <code>leftopen</code> of the second segment.
	 */
	@Deprecated
	public void markDiscontinuities() {
		for (int i = 0; i < segments.length; i++) {
			segments[i].leftopen = false;
		}
		for (int i = 1; i < segments.length; i++) {
			if (segments[i-1].x == segments[i].x) {
				segments[i].leftopen = true;
			}
		}
	}

	/**
	 * Computes the y-coordinates of inflection points starting with
	 * inflection point <code>start</code>.
	 * 
	 * @param start the IP at which to start computing y-coordinates.
	 */
//	@Deprecated
//	public void computeYs(int start) {
//		if (start < 1) {
//			throw new IllegalArgumentException("Value of 'start' must be >= 1!");
//		}
//		Num dx;
//		for (int i = start; i < segments.length; i++) {
//			dx = NumUtils.sub( segments[i].x, segments[i-1].x );
//			segments[i].y = dx.copy();
//			segments[i].y.mult( segments[i-1].grad );
//			segments[i].y.add( segments[i-1].y );
//		}
//	}

	/**
	 * Returns the burstiness of this token bucket curve.<br>
	 * Note: For performance reasons there are no sanity checks! Only
	 *       call this method on a valid token bucket curve!
	 *       
	 * @return the burstiness
	 */
	public Num getTBBurst() {
		if( segments.length == 2 ) {
			return segments[1].y;
		} else { // Assume a continuous TB function, i.e., a simple rate 
			return NumFactory.createZero();
		}
	}

	/**
	 * Sets the burstiness of this token bucket curve.<br>
	 * Note: For performance reasons there are no sanity checks! Only
	 *       call this method on a valid token bucket curve!
	 *       
	 * @param burst the burstiness
	 */
	@Deprecated
	public void setTBBurst( Num burst ) {
		segments[1].y = burst;
		clearMetaInfo();
	}

	/**
	 * Removes unnecessary segments.
	 */
	public void beautify() {
		int i = 0;
		while( i < segments.length-1 ) {
			// Remove unreal discontinuity
			if ( isUnrealDiscontinuity( i ) ) {
				segments[i+1].leftopen = segments[i].leftopen;
				removeSegment(i);
				continue;
			}
			i++;
		}

		i = 0;
		while(i < segments.length-1) {
			// Join colinear segments
			Num firstArg = NumUtils.sub( segments[i+1].grad, segments[i].grad );
			
			Num secondArg = NumUtils.sub( segments[i+1].x, segments[i].x );
			secondArg = NumUtils.mult( secondArg, segments[i].grad );
			secondArg = NumUtils.add( segments[i].y, secondArg);
			secondArg = NumUtils.sub( segments[i+1].y, secondArg );
			
			if ( NumUtils.abs( firstArg ).less( NumFactory.getEpsilon() )
					&& NumUtils.abs( secondArg ).less( NumFactory.getEpsilon() ) ){
				
				removeSegment(i+1);
				if (i+1 < segments.length && !segments[i+1].leftopen) {
					Num resultPt1 = NumUtils.sub( segments[i+1].y, segments[i].y );
					Num resultPt2 = NumUtils.sub( segments[i+1].x, segments[i].x );
					
					segments[i].grad = NumUtils.div( resultPt1, resultPt2 );
				}
				continue;
			}
			i++;
		}

		for (i = 0; i < segments.length-1; i++) {
			if (segments[i].x.equals( segments[i+1].x ) ) {
				segments[i].grad = NumFactory.createZero();
			}
		}
		
		// Remove rate of tb arrival curves' first segment
		if ( segments.length > 1 
				&& segments[0].x == NumFactory.getZero() 
				&& segments[0].y != NumFactory.getZero()
				&& segments[1].x == NumFactory.getZero() 
				&& segments[1].y != NumFactory.getZero() ) {
			segments[0].grad = NumFactory.createZero();
		}
		clearMetaInfo();
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
	public int getSegmentDefining( Num x ) {
		for (int i = segments.length - 1; i >= 0; i--) {
			if (segments[i].leftopen) {
				if ( segments[i].x.less( x ) ) {
					return i;
				}
			} else {
				if ( segments[i].x.leq( x ) ) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public LinearSegment[] getSegments() {
		return segments;
	}
	
	public LinearSegment getSegment(int i) {
		if(i>=0) {
			return segments[i];
		} else {
			return segments[segments.length + i];
		}
	}

	/**
	 * Returns the function value at x-coordinate <code>x</code>, if
	 * <code>x&gt;=0</code>, and <code>NaN</code> if not.
	 * 
	 * @param x the x-coordinate
	 * @return the function value
	 */
	public Num f( Num x ) {
		int i = getSegmentDefining(x);
		if (i < 0) {
			return NumFactory.createNaN();
		}
		return NumUtils.add( NumUtils.mult( NumUtils.sub( x, segments[i].x ), segments[i].grad ), segments[i].y );
	}

	/**
	 * Returns the curve's gradient at x-coordinate <code>x</code>, if
	 * <code>x&gt;=0</code>, and <code>NaN</code> if not. Note that the
	 * gradient returned at discontinuities is <code>0.0</code>, the
	 * gradient returned at an inflection point is the gradient of the
	 * linear segment right of the inflection point, but only if the
	 * segment is not left-open.
	 * 
	 * @param x the x-coordinate
	 * @return the gradient
	 */
	@Deprecated
	public Num getGradientAt( Num x ) {
		int i = getSegmentDefining(x);
		if (i < 0) {
			return NumFactory.createNaN();
		}
		return segments[i].grad;
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
	public int getSegmentLimitRight( Num x ) {
		for (int i = segments.length - 1; i >= 0; i--) {
			if ( segments[i].x.leq( x ) ) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the limit to the right of the function value at
	 * x-coordinate <code>x</code>, if <code>x&gt;=0</code>, and
	 * <code>NaN</code> if not.
	 * 
	 * @param x the x-coordinate
	 * @return the function value
	 */
	public Num fLimitRight( Num x ) {
		int i = getSegmentLimitRight(x);
		if (i < 0) {
			return NumFactory.createNaN();
		}
		return NumUtils.add( NumUtils.mult( NumUtils.sub( x, segments[i].x ), segments[i].grad ), segments[i].y ).copy();
	}

	/**
	 * Returns the gradient to the right of the function value at
	 * x-coordinate <code>x</code>, if <code>x&gt;=0</code>, and
	 * <code>NaN</code> if not.
	 * 
	 * @param x the x-coordinate
	 * @return the function value
	 */
	public Num getGradientLimitRight( Num x ) {
		int i = getSegmentLimitRight(x);
		if (i < 0) {
			return NumFactory.createNaN();
		}
		return segments[i].grad;
	}

	/**
	 * Returns the first segment at which the function reaches the
	 * value <code>y</code>. It returns -1 if the curve never
	 * reaches this value.
	 * 
	 * @param y the y-coordinate
	 * @return the segment number
	 */
	public int getSegmentFirstAtValue( Num y ) {
		if ( segments.length == 0 || segments[0].y.greater( y ) ) {
			return -1;
		}
		for (int i = 0; i < segments.length; i++) {
			if (i < segments.length-1) {
				if ( segments[i+1].y.geq( y ) ) {
					return i;
				}
			} else {
				if ( segments[i].grad.greater( NumFactory.getZero() ) ) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the smallest x value at which the function value is
	 * equal to <code>y</code>.
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x value
	 */
	@Deprecated
	public Num f_inv( Num y ) {
		return f_inv(y, false);
	}

	/**
	 * Returns the x value at which the function value is
	 * equal to <code>y</code>. If <code>rightmost</code> is
	 * <code>true</code>, returns the rightmost x-coordinate,
	 * otherwise the leftmost coordinate.
	 * 
	 * @param y The y-coordinate.
	 * @param rightmost Return the rightmost x coordinate instaed of the leftmost one (default).
	 * @return The smallest x value.
	 */
	public Num f_inv( Num y, boolean rightmost) {
		int i = getSegmentFirstAtValue(y);
		if (i < 0) {
			return NumFactory.createNaN();
		}
		if (rightmost) {
			while(i < segments.length && segments[i].grad.equals( NumFactory.getZero() ) ) {
				i++;
			}
			if (i >= segments.length) {
				return NumFactory.createPositiveInfinity();
			}
		}
		if ( !segments[i].grad.equals( NumFactory.getZero() ) ) {
			return NumUtils.add( segments[i].x, NumUtils.div( NumUtils.sub( y, segments[i].y ), segments[i].grad ) );
		} else {
			return segments[i].x;
		}
	}

	@Deprecated
	public Num getMaxY( Num a, Num b ) {
		int sa = getSegmentDefining(a);
		int sb = getSegmentDefining(b);

		Num ret = NumFactory.getZero(); // No need to create an object as this value is only set for initial comparison in the loop.
		for(int i=sa; i<=sb; ++i) {
			Num end = ( i+1 < segments.length ? NumUtils.min(getSegment(i+1).x, b) : b );
			ret = NumUtils.max( ret, NumUtils.max( f( NumUtils.max( a, getSegment(i).x ) ), f( end ) ) );
		}

		return ret;
	}

	/**
	 * Tests whether the curve is wide-sense increasing.
	 * 
	 * @return whether the curve is wide-sense increasing.
	 */
	public boolean isWideSenseIncreasing() {
		Num y = NumFactory.getNegativeInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.
		for (int i = 0; i < segments.length; i++) {
			if ( segments[i].y.less( y ) || segments[i].grad.less( NumFactory.getZero() ) ) {
				return false;
			}
			y = segments[i].y;
		}
		return true;
	}

	/**
	 * Tests whether the curve is convex.
	 * 
	 * @return whether the curve is convex.
	 */
	public boolean isConvex() {
		return isConvexIn( NumFactory.getZero(), NumFactory.getPositiveInfinity() );
	}

	/**
	 * Tests whether the curve is convex in [a,b].
	 * 
	 * @param a the lower bound of the test interval.
	 * @param b the upper bound of the test interval.
	 * @return whether the curve is convex
	 */
	public boolean isConvexIn( Num a, Num b ) {
		Num last_gradient = NumFactory.getNegativeInfinity();  // No need to create an object as this value is only set for initial comparison in the loop.

		int i_start = getSegmentDefining(a);
		int i_end   = getSegmentDefining(b);
		for (int i = i_start; i <= i_end; i++) {
			if (i_start < 0) {
				break;
			}
			if (i == i_end && segments[i].x == b) {
				break;
			}
			Num gradient;
			if (i < segments.length-1) {
				gradient = NumUtils.div( NumUtils.sub( segments[i+1].y, segments[i].y ),
											NumUtils.sub( segments[i+1].x, segments[i].x ) );
			} else {
				gradient = segments[i].grad;
			}
			if ( gradient.less( last_gradient ) ) {
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
		return isConcaveIn( NumFactory.getZero(), NumFactory.getPositiveInfinity() );
	}

	/**
	 * Tests whether the curve is concave in [a,b].
	 * 
	 * @param a the lower bound of the test interval.
	 * @param b the upper bound of the test interval.
	 * @return whether the curve is concave.
	 */
	public boolean isConcaveIn( Num a, Num b ) {
		Num last_gradient = NumFactory.getPositiveInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.

		int i_start = getSegmentDefining( a );
		int i_end   = getSegmentDefining( b );
		for ( int i = i_start; i <= i_end; i++ ) {
			if ( i == i_end && segments[i].x == b ) {
				break;
			}
			Num gradient;
			// Handles discontinuities
			if ( i < segments.length-1 ) {
				gradient = NumUtils.div( NumUtils.sub( segments[i+1].y, segments[i].y ),
											NumUtils.sub( segments[i+1].x, segments[i].x ) );
			} else {
				gradient = segments[i].grad;
			}
			if ( gradient.greater( last_gradient ) ) {
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
			if ( last_gradient.equals( NumFactory.getPositiveInfinity() ) && segments[i].grad.equals( NumFactory.getZero() ) ) {
				continue;
			}

			Num gradient;
			if (i < segments.length-1) {
				gradient = NumUtils.div( NumUtils.sub( segments[i+1].y, segments[i].y ),
											NumUtils.sub( segments[i+1].x, segments[i].x ) );
			} else {
				gradient = segments[i].grad;
			}
			if ( gradient.greater( last_gradient ) ) {
				return false;
			}
			last_gradient = gradient;
		}
		return true;
	}

	/**
	 * Returns a copy of this curve that is shifted to the right by <code>dx</code>,
	 * i.e. g(x) = f(x-dx).
	 * 
	 * @param curve The curve to shift.
	 * @param dx The offset to shift the curve.
	 * @return The shifted curve.
	 */
	public static Curve shiftRight( Curve curve, Num dx ) {
		Curve curve_copy = curve.copy();
		if( dx.equals( 0.0 ) ) {
			return curve_copy;
		}
		
		if ( !( ( curve_copy.getSegment(0).y ).equals( NumFactory.getZero() ) ) ) {
			throw new RuntimeException("Curve to shift right must pass through origin!");
		}
		
		Curve result = new Curve( curve_copy.getSegmentCount()+1 );
		result.segments[0] = LinearSegment.createHorizontalLine( 0.0 );
		result.segments[0].y = curve_copy.getSegment(0).y; // Decide what to do if pass thru origin req. dropped...
		for (int i = 0; i < curve_copy.getSegmentCount(); i++) {
			result.segments[i+1] = curve_copy.getSegment(i);
			result.segments[i+1].x = NumUtils.add( result.segments[i+1].x, dx );
		}
		
		result.beautify();
		
		return result;
	}
	
	/**
	 * Returns a copy of this curve that is shifted to the left by <code>dx</code>,
	 * i.e. g(x) = f(x+dx). Note that the new curve is clipped at the y-axis so
	 * that in most cases <code>c.shiftLeftClipping(dx).shiftRight(dx) != c</code>!
	 * 
	 * @param curve The curve to shift.
	 * @param dx The offset to shift the curve.
	 * @return The shifted curve.
	 */
	public static Curve shiftLeftClipping( Curve curve, Num dx ) {
		Curve result = curve.copy();
		int i = result.getSegmentDefining(dx);
		if ( result.segments[i].x.less( dx ) ) {
			result.segments[i].y = NumUtils.add( result.segments[i].y, 
									NumUtils.mult( NumUtils.sub( dx, result.segments[i].x ), result.segments[i].grad ) );
			result.segments[i].x = dx;
			result.segments[i].leftopen = false;
		}
		if (i > 0) {
			LinearSegment[] old_segments = result.segments;
			result.segments = new LinearSegment[old_segments.length - i];
			System.arraycopy(old_segments, i, result.segments, 0, result.segments.length);
		}
		for (i = 0; i < result.segments.length; i++) {
			result.segments[i].x = NumUtils.sub( result.segments[i].x, dx );
		}

		return result;
	}
	
	/**
	 * Returns a copy of this curve with latency removed, i.e. shifted left by
	 * the latency.
	 * 
	 * @param curve The curve to shift.
	 * @return A copy of this curve without latency
	 */
	public static Curve removeLatency( Curve curve ) {
		Curve result = curve.copy();

		// Remove all segment(s) with y0==0.0 and grad==0.0
		while(result.segments.length > 0) {
			if ( result.segments[0].y.greater( NumFactory.getZero() ) || result.segments[0].grad.greater( NumFactory.getZero() ) ) {
				break;
			}
			if ( curve.getSegment(0).y.less( NumFactory.getZero() ) || curve.getSegment(0).grad.less( NumFactory.getZero() ) ) {
				throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
			}
			result.removeSegment(0);
		}

		// In case that we've removed everything, the curve had infinite latency, so return the NULL curve.
		if (result.segments.length == 0) {
			return createNullCurve();
		}

		// Shift remaining segments left by latency
		Num L = result.segments[0].x;
		for (int i = 0; i < result.segments.length; i++) {
			result.segments[i].x = NumUtils.sub( result.segments[i].x, L );
		}
		if (result.segments[0].leftopen) {
			result.addSegment(0, LinearSegment.createHorizontalLine( 0.0 ));
		}
		
		return result;
	}

	/**
	 * Returns a copy of this curve shifted vertically by <code>dy</code>.
	 * 
	 * @param curve The curve to shift.
	 * @param dy The offset to shift the curve.
	 * @return The shifted curve.
	 */
	public static Curve add( Curve curve, Num dy ) {
		Curve result = curve.copy();
		for (int i = 0; i < curve.getSegmentCount(); i++) {
			result.segments[i].y = NumUtils.add( result.segments[i].y, dy );
		}
		
		return result;
	}

	/**
	 * Returns the x-coordinate of the inflection point after which the function
	 * values are greater than zero.
	 * 
	 * @return the latency of this curve.
	 */
	public Num getLatency() {
		if ( segments[0].y.greater( NumFactory.getZero() ) ) {
			return NumFactory.createZero();
		}
		for (int i = 0; i < segments.length; i++) {
			Num y0 = segments[i].y;
			if (y0.less( NumFactory.getZero() ) && y0.greater( NumUtils.negate( NumFactory.getEpsilon() ) ) ) {
				y0 = NumFactory.createZero();
			}
			if ( y0.greater( NumFactory.getZero() )
					|| ( y0.geq( NumFactory.getZero() ) && segments[i].grad.greater( NumFactory.getZero() ) )
				) {
				return segments[i].x;
			}
			if (y0.less( NumFactory.getZero() ) || segments[i].grad.less( NumFactory.getZero() ) ) {
				throw new RuntimeException("Should have avoided neg. gradients elsewhere...");
			}
		}
		
		return NumFactory.createPositiveInfinity();
	}

	/**
	 * Returns a list containing each segment's height when projected onto
	 * the y-axis.
	 * 
	 * @return a list of doubles containing the segment heights
	 */
	@Deprecated
	public List<Num> getYIntervals() {
		List<Num> y_intervals = new ArrayList<Num>();
		for (int i = 1; i < segments.length; i++) {
			if (isDiscontinuity(i-1)) {
				continue;
			}
			y_intervals.add( NumUtils.sub( segments[i].y, segments[i-1].y ) );
		}
		
		return y_intervals;
	}

	public static Num getXIntersection( Curve curve1, Curve curve2 ){
		Num x_int = NumFactory.getPositiveInfinity(); // No need to create an object as this value is only set for initial comparison in the loop.

		for( int i = 0; i < curve1.getSegmentCount(); i++ ){
			boolean curve1_last = (i == curve1.getSegmentCount()-1);
					
			for( int j = 0; j < curve2.getSegmentCount(); j++ ){
				boolean curve2_last = (j == curve2.getSegmentCount()-1);
				
				Num x_int_tmp = curve1.segments[i].getXIntersectionWith( curve2.segments[j] );
				
				if( x_int_tmp.equals( NumFactory.getNaN() ) ) {
					break;
				}
				
				if( x_int_tmp.greater( NumFactory.getZero() ) ){
					if( !curve1_last ){
						if( !curve2_last ){
							if( x_int_tmp.less( curve1.segments[i+1].x )
								&& x_int_tmp.less( curve1.segments[j+1].x )
								&& x_int_tmp.less( x_int ) ) {				
										
									x_int = x_int_tmp;
							}
						} else {
							if( x_int_tmp.less( curve1.segments[i+1].x )
								&& x_int_tmp.less( x_int ) ) {				
										
									x_int = x_int_tmp;
							}
						}
					} else {
						if( !curve2_last ){
							if( x_int_tmp.less( curve1.segments[j+1].x )
								&& x_int_tmp.less( x_int ) ) {				
										
									x_int = x_int_tmp;
							}
						} else {
							if( x_int_tmp.less( x_int )) {				
										
									x_int = x_int_tmp;
							}
						}
					}
				}
			}
		}
		return x_int;
	}

	/**
	 * Returns a string representation of this curve.
	 * 
	 * @return the curve represented as a string.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer( "{" );
		for (int i = 0; i < segments.length; i++) {
			if (i > 0) {
				result.append( ";" );
			}
			result.append( segments[i].toString() );
		}
		result.append( "}" );
		return result.toString();
	}
	
	/**
	 * Common helper for computing a new curve.
	 * 
	 * @param curve1 Input curve 1.
	 * @param curve2 Input curve 2.
	 * @param operator Operation to be applied to the curves. 
	 * @return The resulting curve.
	 */
	private static Curve computeResultingCurve( Curve curve1, Curve curve2, int operator ) {
		Curve ZERO_DELAY_INFINITE_BURST = createDelayedInfiniteBurst( NumFactory.createZero() );
		
		switch( operator ) {
		case OPERATOR_ADD:
			if( curve1.equals( ZERO_DELAY_INFINITE_BURST ) || curve2.equals( ZERO_DELAY_INFINITE_BURST ) ){
				return ZERO_DELAY_INFINITE_BURST;
			}
			break;
		case OPERATOR_SUB:
			if( curve1.equals( ZERO_DELAY_INFINITE_BURST ) || curve2.equals( ZERO_DELAY_INFINITE_BURST ) ){
				return ZERO_DELAY_INFINITE_BURST;
			}
			break;
		case OPERATOR_MIN:
			if( curve1.equals( ZERO_DELAY_INFINITE_BURST ) ){
				return curve2.copy();
			}
			if( curve2.equals( ZERO_DELAY_INFINITE_BURST ) ){
				return curve1.copy();
			}
			break;
		case OPERATOR_MAX:
			if( curve1.equals( ZERO_DELAY_INFINITE_BURST ) || curve2.equals( ZERO_DELAY_INFINITE_BURST ) ){
				return ZERO_DELAY_INFINITE_BURST;
			}
			break;
		default:
		}
		
		Curve result = new Curve();

		Num x = NumFactory.createZero();

		Num x_cross;

		boolean leftopen;

		int i1 = 0;
		int i2 = 0;
		while( i1 < curve1.getSegmentCount() || i2 < curve2.getSegmentCount() ) {
			Num x_next1 = ( i1+1 < curve1.getSegmentCount() ) ?
								curve1.getSegment( i1+1 ).x : NumFactory.createPositiveInfinity();
			Num x_next2 = ( i2+1 < curve2.getSegmentCount() ) ?
								curve2.getSegment( i2+1 ).x : NumFactory.createPositiveInfinity();
			Num x_next  = NumUtils.min( x_next1, x_next2 );
								
			leftopen = curve1.getSegment( i1 ).leftopen || curve2.getSegment( i2 ).leftopen;

			switch( operator ) {
			case OPERATOR_ADD:
				result.addSegment( LinearSegment.add( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen ) );
				break;
			case OPERATOR_SUB:
				result.addSegment( LinearSegment.sub( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen ) );
				break;
			case OPERATOR_MIN:
				x_cross = curve1.getSegment( i1 ).getXIntersectionWith( curve2.getSegment( i2 ) );
				if ( x_cross.equals( NumFactory.getNaN() ) ) {
					x_cross = NumFactory.createPositiveInfinity();
				}
				if ( x.less( x_cross ) && x_cross.less( x_next ) ) {
					result.addSegment( LinearSegment.min( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen, false ) );
					result.addSegment( LinearSegment.min( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x_cross, false, true ) );
				} else {
					result.addSegment( LinearSegment.min( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen, false ) );
				}
				break;
			case OPERATOR_MAX:
				x_cross = curve1.getSegment( i1 ).getXIntersectionWith( curve2.getSegment( i2 ) );
				if ( x_cross.equals( NumFactory.getNaN() ) ) {
					x_cross = NumFactory.createPositiveInfinity();
				}
				if ( x.less( x_cross ) && x_cross.less( x_next ) ) {
					result.addSegment( LinearSegment.max( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen, false ) );
					result.addSegment( LinearSegment.max( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x_cross, false, true ) );
				} else {
					result.addSegment( LinearSegment.max( curve1.getSegment( i1 ), curve2.getSegment( i2 ), x, leftopen, false ) );
				}
				break;
			default:
			}

			if ( x_next1.equals( x_next ) ) {
				i1++;
			}
			if ( x_next2.equals( x_next ) ) {
				i2++;
			}
			x = x_next;
		}

		result.beautify();

		return result;
	}
	
	/**
	 * Returns a curve that is the sum of this curve and the given curve.
	 * 
	 * @param curve1 input curve 1.
	 * @param curve2 input curve 2.
	 * @return the pointwise sum of the given curves.
	 */
	public static Curve add( Curve curve1, Curve curve2 ) {
		return computeResultingCurve( curve1, curve2, OPERATOR_ADD );
	}

	/**
	 * Returns a curve that is the difference between this curve and the given curve.
	 * 
	 * @param curve1 input curve 1.
	 * @param curve2 input curve 2.
	 * @return the pointwise difference of the given curves, i.e., curve1 - curve2.
	 */
	public static Curve sub( Curve curve1, Curve curve2 ) {
		return computeResultingCurve( curve1, curve2, OPERATOR_SUB );
	}

	/**
	 * Returns a curve that is the minimum of this curve and the given curve.
	 * 
	 * @param curve1 input curve 1.
	 * @param curve2 input curve 2.
	 * @return the pointwise minimum of the given curves.
	 */
	public static Curve min( Curve curve1, Curve curve2 ) {
		return computeResultingCurve( curve1, curve2, OPERATOR_MIN );
	}

	/**
	 * Returns a curve that is the maximum of this curve and the given curve.
	 * 
	 * @param curve1 input curve 1.
	 * @param curve2 input curve 2.
	 * @return the pointwise maximum of the given curves.
	 */
	public static Curve max( Curve curve1, Curve curve2 ) {
		return computeResultingCurve( curve1, curve2, OPERATOR_MAX );
	}
	
	/**
	 * Returns a copy of curve bounded at the x-axis.
	 * 
	 * @param curve the curve to bound.
	 * @return the bounded curve.
	 */
	public static Curve boundAtXAxis( Curve curve ) {
		Curve curve_copy = curve.copy();
		Curve result = new Curve();

		LinearSegment s;
		for ( int i = 0; i < curve_copy.getSegmentCount(); i++ ) {
			if ( curve_copy.getSegment( i ).y.greater( NumFactory.getZero() ) ) {
				result.addSegment( curve_copy.getSegment( i ) );

				if ( curve_copy.getSegment(i).grad.less( NumFactory.getZero() ) ) {
					Num x_cross = curve_copy.getSegment( i ).getXIntersectionWith( LinearSegment.getXAxis() );
					if ( i+1 >= curve_copy.getSegmentCount() || x_cross.less( curve_copy.getSegment( i+1 ).x ) ) {
						s = LinearSegment.createHorizontalLine( 0.0 );
						s.x = x_cross;
						result.addSegment( s );
					}
				}
			} else {
				s = LinearSegment.createHorizontalLine( 0.0 );
				s.x       = curve_copy.getSegment( i ).x;
				s.leftopen = curve_copy.getSegment( i ).leftopen;
				result.addSegment( s );

				if ( curve_copy.getSegment(i).grad.greater( NumFactory.getZero() ) ) {
					Num x_cross = curve_copy.getSegment( i ).getXIntersectionWith( LinearSegment.getXAxis() );
					if ( i+1 >= curve_copy.getSegmentCount() || x_cross.less( curve_copy.getSegment(i+1).x ) ) {
						s = LinearSegment.createHorizontalLine( 0.0 );
						s.x   = x_cross;
						s.grad = curve_copy.getSegment( i ).grad;
						result.addSegment( s );
					}
				}
			}
		}
		
		result.beautify();
		
		return result;
	}	

	/**
	 * Returns the maximum vertical deviation between the given two curves.
	 * 
	 * @param c1 the first curve.
	 * @param c2 the second curve.
	 * @return the value of the vertical deviation.
	 */
	public static Num getMaxVerticalDeviation( Curve c1, Curve c2 ) {
		if ( c1.getSustainedRate().greater( c2.getSustainedRate() ) ) {
			return NumFactory.createPositiveInfinity();
		}
		// The computeInflectionPoints based method does not work for 
		// single rate service curves (without latency)
		// in conjunction with token bucket arrival curves
		// because their common inflection point is in zero, 
		// where the arrival curve is 0.0 by definition.
		// This leads to a vertical deviation of 0 the arrival curve's burst
		// (or infinity which is already handled by the first if-statement)
		
		// Solution: 
		// Start with the burst as minimum of all possible solutions for the deviation instead of negative infinity.
		
		Num burst_c1 = c1.fLimitRight( NumFactory.getZero() );
		Num burst_c2 = c2.fLimitRight( NumFactory.getZero() );
		Num result = NumUtils.diff( burst_c1, burst_c2 );
		
		ArrayList<Num> xcoords = computeInflectionPointsX( c1, c2 );	
		for( int i = 0; i < xcoords.size(); i++ ) {
			Num ip_x = ( (Num) xcoords.get( i ) );

			Num backlog = NumUtils.sub( c1.f( ip_x ), c2.f( ip_x ) );
			result = NumUtils.max( result, backlog );
		}
		return result;
	}

	/**
	 * Returns the maximum horizontal deviation between the given two curves.
	 * 
	 * @param c1 the first curve.
	 * @param c2 the second curve.
	 * @return the value of the horizontal deviation.
	 */
	public static Num getMaxHorizontalDeviation( Curve c1, Curve c2 ) {
		if ( c1.getSustainedRate().greater( c2.getSustainedRate() ) ) {
			return NumFactory.createPositiveInfinity();
		}

		Num result = NumFactory.createNegativeInfinity();
		for( int i = 0; i < c1.getSegmentCount(); i++ ) {
			Num ip_y = c1.getSegment( i ).y;

			Num delay = NumUtils.sub( c2.f_inv( ip_y, true ), c1.f_inv( ip_y, false ) );
			result = NumUtils.max( result, delay );
		}
		for( int i = 0; i < c2.getSegmentCount(); i++ ) {
			Num ip_y = c2.getSegment( i ).y;

			Num delay = NumUtils.sub( c2.f_inv( ip_y, true ), c1.f_inv( ip_y, false ) );
			result = NumUtils.max( result, delay );
		}
		return result;
	}

	/**
	 * Returns an <code>ArrayList</code> instance of those x-coordinates
	 * at which either c1 or c2 or both have an inflection point. There
	 * will be multiple occurences of an x-coordinate, if at least one
	 * curve has a discontinuity at that x-coordinate.
	 * 
	 * @param c1 the first curve.
	 * @param c2 the second curve.
	 * @return an <code>ArrayList</code> of <code>Double</code> objects
	 * containing the x-coordinates of the respective inflection point.
	 */
	public static ArrayList<Num> computeInflectionPointsX( Curve c1, Curve c2 ) {
		ArrayList<Num> xcoords = new ArrayList<Num>();

		int i1 = 0;
		int i2 = 0;
		while( i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount() ) {
			Num x1 = ( i1 < c1.getSegmentCount() ) ?
						c1.getSegment( i1 ).x : NumFactory.createPositiveInfinity();
			Num x2 = ( i2 < c2.getSegmentCount() ) ?
						c2.getSegment( i2 ).x : NumFactory.createPositiveInfinity();
			if ( x1.less( x2 ) ) {
				xcoords.add( x1.copy() );
				i1++;
			} else if ( x1.greater( x2 ) ) {
				xcoords.add( x2.copy() );
				i2++;
			} else {
				xcoords.add( x1.copy() );
				i1++;
				i2++;
			}
		}
		return xcoords;
	}

	/**
	 * Returns an <code>ArrayList</code> instance of those y-coordinates
	 * at which either c1 or c2 or both have an inflection point.
	 * 
	 * @param c1 the first curve.
	 * @param c2 the second curve.
	 * @return an <code>ArrayList</code> of <code>Double</code> objects
	 * containing the x-coordinates of the respective inflection point.
	 */
	public static ArrayList<Num> computeInflectionPointsY( Curve c1, Curve c2 ) {
		ArrayList<Num> ycoords = new ArrayList<Num>();

		int i1 = 0;
		int i2 = 0;
		while( i1 < c1.getSegmentCount() || i2 < c2.getSegmentCount() ) {
			Num y1 = ( i1 < c1.getSegmentCount() ) ?
						c1.getSegment( i1 ).y : NumFactory.createPositiveInfinity();
			Num y2 = ( i2 < c2.getSegmentCount() ) ?
						c2.getSegment( i2 ).y : NumFactory.createPositiveInfinity();
			if ( y1.less( y2 ) ) {
				ycoords.add( y1.copy() );
				i1++;
			} else if ( y1.greater( y2 ) ) {
				ycoords.add( y2.copy() );
				i2++;
			} else {
				ycoords.add( y1.copy() );
				i1++;
				i2++;
			}
		}

		return ycoords;
	}
	
	@Override
	public boolean equals( Object obj ) {
		if ( obj == null || !( obj instanceof Curve ) ) {
			return false;
		}
		
		Curve this_cpy = this.copy();
		Curve other_cpy = ( (Curve)obj ).copy();
		
		this_cpy.beautify();
		other_cpy.beautify();
		
 		if( this_cpy.getLatency() == NumFactory.getPositiveInfinity() ) {
 			this_cpy = createNullCurve();
 		}
 		if( other_cpy.getLatency() == NumFactory.getPositiveInfinity() ) {
 			other_cpy = createNullCurve();
 		}
		
		int this_segment_length = this_cpy.segments.length;
		
		if( this_segment_length != other_cpy.segments.length ){
			return false;
		}
		
		for( int i = 0; i < this_segment_length; i++ ) {
			if( !this_cpy.segments[i].equals( other_cpy.segments[i] ) ){
				return false;
			}
		}
			
		return true;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode( segments );
	}
}