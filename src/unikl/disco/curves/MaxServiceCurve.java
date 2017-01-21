/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.1 "Centaur".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2016 - 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
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
		copy( curve );
		
		if( CalculatorConfig.MAX_SERVICE_CURVE_CHECKS &&  !isWideSenseIncreasing() ) { // too strong requirement: !isAlmostConcave() ) {
			throw new RuntimeException( "Maximum service curves can only be created from wide-sense increasing functions." );
		}
		
		makeMaximumServiceCurve();
	}

	private void makeMaximumServiceCurve() {
		if ( this.getSegment(0).y.gt( NumFactory.getZero() ) ) {
			LinearSegment[] segments_new = new LinearSegment[segments.length+1];
			segments_new[0] = LinearSegment.createZeroSegment();
			
			System.arraycopy( this.segments, 0, segments_new, 1, this.segments.length );
			segments_new[1].leftopen = true;
			
			this.segments = segments_new;
		}
	}
	
	/**
	 * Creates a zero maximum service curve.
	 * 
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createZeroMaxService() {
		MaxServiceCurve msc_result = new MaxServiceCurve();
		msc_result.initializeZeroCurve();
		
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
	
	public MaxServiceCurve copy() {
		MaxServiceCurve msc_copy = new MaxServiceCurve();
		msc_copy.copy( this );
		
		return msc_copy;
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>MaxServiceCurve</code> instance
	 */
	public static MaxServiceCurve createRateLatency( double rate, double latency ) {
		return createRateLatency( NumFactory.create( rate ), NumFactory.create( latency ) );
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

		if( rate.doubleValue() == Double.POSITIVE_INFINITY ) {
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
	
	public void shiftRight( Num dx ) {
		copy( Curve.shiftRight( this, dx ) );
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
