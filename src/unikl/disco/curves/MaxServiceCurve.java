/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2016 Steffen Bondorf
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
import java.util.List;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
public class MaxServiceCurve extends Curve {
	private MaxServiceCurve() {
		super();
	}
	
	public MaxServiceCurve( int segment_count ) {
		super( segment_count );
	}
	
	public MaxServiceCurve( String max_service_curve_str ) throws Exception {
		if( max_service_curve_str == null || max_service_curve_str.isEmpty() || max_service_curve_str.length() < 9 ) { // Smallest possible string: {(0,0),0}
			throw new RuntimeException( "Invalid string representation of a service curve." );
		}

		initializeCurve( max_service_curve_str );

		if( CalculatorConfig.MAX_SERVICE_CURVE_CHECKS &&  !isWideSenseIncreasing() ) { // too strong requirement: !isAlmostConcave() ) {
			throw new RuntimeException( "Maximum service curves can only be created from wide-sense increasing functions." );
		}
		
		makeMaximumServiceCurve();
	}
	
	public MaxServiceCurve( Curve curve ) {
		initializeCurve( curve );
		
		if( CalculatorConfig.MAX_SERVICE_CURVE_CHECKS &&  !isWideSenseIncreasing() ) { // too strong requirement: !isAlmostConcave() ) {
			throw new RuntimeException( "Maximum service curves can only be created from wide-sense increasing functions." );
		}
		
		makeMaximumServiceCurve();
	}

	private void makeMaximumServiceCurve() {
		if ( this.getSegment(0).y.greater( NumFactory.getZero() ) ) {
			LinearSegment[] segments_new = new LinearSegment[segments.length+1];
			segments_new[0] = LinearSegment.createNullSegment();
			
			System.arraycopy( this.segments, 0, segments_new, 1, this.segments.length );
			segments_new[1].leftopen = true;
			
			this.segments = segments_new;
		}
	}
	
	/**
	 * Creates a null maximum service curve.
	 * 
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createNullMaxService() {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeNullCurve();
		
		return msc_result;
	}
	
	/**
	 * Creates a burst curve with zero delay.
	 * 
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createZeroDelayInfiniteBurst() {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeZeroDelayInfiniteBurst();
		
		return msc_result;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createTokenBucket( Num rate, Num burst ) {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeTokenBucket( rate, burst );

		msc_result.token_buckets = new ArrayList<Curve>();
		msc_result.token_buckets.add( msc_result.copy() );
		msc_result.is_token_bucket = true;
		
		return msc_result;
	}

	/**
	 * Creates a new token bucket curve.
	 * 
	 * @param rate the rate
	 * @param burst the burstiness
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createTokenBucket( double rate, double burst ) {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeTokenBucket( rate, burst );
		
		msc_result.token_buckets = new ArrayList<Curve>();
		msc_result.token_buckets.add( msc_result.copy() );
		msc_result.is_token_bucket = true;
		
		return msc_result;
	}

	/**
	 * Creates a new curve from a list of token bucket curves.
	 * 
	 * @param token_buckets a list of token bucket curves
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	@Deprecated
	public static MaxServiceCurve createFromTokenBuckets( List<Curve> token_buckets ) {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeCurve( Curve.createFromTokenBuckets( token_buckets ) );
		
		return msc_result;
	}
	
	public MaxServiceCurve copy() {
		MaxServiceCurve msc_copy = new MaxServiceCurve();
		msc_copy.initializeCurve( this );
		
		return msc_copy;
	}
	
	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createRateLatency( Num rate, Num latency ) {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeRateLatency( rate, latency );
		
		msc_result.rate_latencies = new ArrayList<Curve>();
		msc_result.rate_latencies.add( msc_result.copy() );
		msc_result.is_rate_latency = true;

		if( rate.doubleValue() == Double.POSITIVE_INFINITY && latency.doubleValue() == 0.0 ) {
			msc_result.is_delayed_infinite_burst = true;
		}
		
		return msc_result;
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createRateLatency( double rate, double latency ) {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeRateLatency( rate, latency );
		
		msc_result.rate_latencies = new ArrayList<Curve>();
		msc_result.rate_latencies.add( msc_result.copy() );
		msc_result.is_rate_latency = true;
		
		if( rate == Double.POSITIVE_INFINITY && latency == 0.0 ) {
			msc_result.is_delayed_infinite_burst = true;
		}
		
		return msc_result;
	}

	public static MaxServiceCurve add( MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2 ) {
		return new MaxServiceCurve( Curve.add( max_service_curve_1, max_service_curve_2 ) );
	}

	public static MaxServiceCurve add( MaxServiceCurve max_service_curve_1, Num dy ) {
		return new MaxServiceCurve( Curve.add( max_service_curve_1, dy ) );
	}

	public static MaxServiceCurve min( MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2 ) {
		return new MaxServiceCurve( Curve.min( max_service_curve_1, max_service_curve_2 ) );
	}
	
	/**
	 * Returns a copy of this curve that is shifted to the right by <code>dx</code>,
	 * i.e. g(x) = f(x-dx).
	 * 
	 * @param dx the offset to shift the curve.
	 * @return the shifted curve.
	 */
	public MaxServiceCurve shiftRight( Num dx ) {
		return new MaxServiceCurve( Curve.shiftRight( this, dx ) );
	}
	
	/**
	 * Returns a copy of this curve that is shifted to the left by <code>dx</code>,
	 * i.e. g(x) = f(x+dx). Note that the new curve is clipped at the y-axis so
	 * that in most cases <code>c.shiftLeftClipping(dx).shiftRight(dx) != c</code>!
	 * 
	 * @param dx the offset to shift the curve.
	 * @return the shifted curve.
	 */
	@Deprecated
	public MaxServiceCurve shiftLeftClipping( Num dx ) {
		return new MaxServiceCurve( Curve.shiftLeftClipping( this, dx ) );
	}

	/**
	 * Returns a copy of this curve with latency removed, i.e. shifted left by
	 * the latency.
	 * 
	 * @return a copy of this curve without latency
	 */
	public MaxServiceCurve removeLatency() {
		return new MaxServiceCurve( Curve.removeLatency( this ) );
	}
	
	@Override
	public boolean equals( Object obj ) {
		return ( obj instanceof MaxServiceCurve ) && super.equals( obj );
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * Returns a string representation of this curve.
	 * 
	 * @return the curve represented as a string.
	 */
	@Override
	public String toString() {
		return "MSC" + super.toString();
	}
}
