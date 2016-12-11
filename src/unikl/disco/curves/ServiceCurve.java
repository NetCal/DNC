/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2013 - 2017 Steffen Bondorf
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
public class ServiceCurve extends Curve {
	public ServiceCurve() {
		super();
	}

	public ServiceCurve( int segment_count ) {
		super( segment_count );
	}
	
	public ServiceCurve( String service_curve_str ) throws Exception {
		if( service_curve_str == null || service_curve_str.isEmpty() || service_curve_str.length() < 9 ) { // Smallest possible string: {(0,0),0}
			throw new RuntimeException( "Invalid string representation of a service curve." );
		}

		initializeCurve( service_curve_str );

		if( CalculatorConfig.SERVICE_CURVE_CHECKS && !isWideSenseIncreasing() ) { // too strong requirement: !isConvex()
			throw new RuntimeException( "Service curves can only be created from wide-sense increasing functions." );
		}
	}
	
	public ServiceCurve( Curve curve ) {
		initializeCurve( curve );

		if( CalculatorConfig.SERVICE_CURVE_CHECKS && !isWideSenseIncreasing() ) { // too strong requirement: !isConvex()
			throw new RuntimeException( "Service curves can only be created from wide-sense increasing functions." );
		}
	}
	
	/**
	 * Creates a null arrival curve.
	 * 
	 * @return a <code>ServiceCurve</code> instance
	 */
	public static ServiceCurve createNullService() {
		ServiceCurve sc_result = new ServiceCurve();
		sc_result.initializeNullCurve();
		
		return sc_result;
	}
	
	/**
	 * Creates a burst curve with zero delay.
	 * 
	 * @return a <code>ServiceCurve</code> instance
	 */
	public static ServiceCurve createZeroDelayInfiniteBurst() {
		ServiceCurve sc_result = new ServiceCurve();
		sc_result.initializeZeroDelayInfiniteBurst();
		sc_result.is_delayed_infinite_burst = true;
		
		return sc_result;
	}

	public static ServiceCurve createDelayedInfiniteBurst( double delay ) {
		return createDelayedInfiniteBurst( NumFactory.create( delay ) );
	}
	
	/**
	 * Creates a burst delay curve.
	 * 
	 * @param delay the delay, which must be &gt;= 0.0
	 * @return a <code>ServiceCurve</code> instance
	 */
	public static ServiceCurve createDelayedInfiniteBurst( Num delay ) {
		ServiceCurve sc_result = new ServiceCurve();
		sc_result.initializeDelayedInfiniteBurst( delay );
		sc_result.is_delayed_infinite_burst = true;
		
		return sc_result;
	}

	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>ServiceCurve</code> instance
	 */
	public static ServiceCurve createRateLatency( double rate, double latency ) {
		return createRateLatency( NumFactory.create( rate ), NumFactory.create( latency ) );
	}
	
	/**
	 * Creates a new rate latency curve.
	 * 
	 * @param rate the rate
	 * @param latency the latency
	 * @return a <code>ServiceCurve</code> instance
	 */
	public static ServiceCurve createRateLatency( Num rate, Num latency ) {
		ServiceCurve sc_result = new ServiceCurve();
		sc_result.initializeRateLatency( rate, latency );
		
		sc_result.rate_latencies = new ArrayList<Curve>();
		sc_result.rate_latencies.add( sc_result.copy() );
		sc_result.is_rate_latency = true;

		if( rate.doubleValue() == Double.POSITIVE_INFINITY ) {
			sc_result.is_delayed_infinite_burst = true;
		}
		
		return sc_result;
	}

	public ServiceCurve copy() {
		ServiceCurve sc_copy = new ServiceCurve();
		sc_copy.initializeCurve( this );
		sc_copy.is_delayed_infinite_burst = this.is_delayed_infinite_burst;
		
		return sc_copy;
	}
	
	public static ServiceCurve add( ServiceCurve c1, ServiceCurve c2 ) {
		return new ServiceCurve( Curve.add(c1, c2) );
	}
	
	public static ServiceCurve sub( ServiceCurve c1, ArrivalCurve c2 ) {
		return new ServiceCurve( Curve.sub(c1, c2) );
	}
	
	public static ServiceCurve min( ServiceCurve c1, ServiceCurve c2 ) {
		return new ServiceCurve( Curve.min(c1, c2) );
	}
	
	public static ServiceCurve max( ServiceCurve c1, ServiceCurve c2 ) {
		return new ServiceCurve( Curve.max(c1, c2) );
	}
	
	/**
	 * Returns a copy of curve bounded at the x-axis.
	 * 
	 * @param service_curve The service curve to bound.
	 * @return The bounded curve.
	 */
	public static ServiceCurve boundAtXAxis( ServiceCurve service_curve ) {
		return new ServiceCurve( Curve.boundAtXAxis( service_curve ) ); 
	}
	
	/**
	 * Returns a copy of this curve with latency removed, i.e. shifted left by
	 * the latency.
	 * 
	 * @return a copy of this curve without latency
	 */
	@Deprecated
	public ServiceCurve removeLatency() {
		return new ServiceCurve( Curve.removeLatency( this ) );
	}
	
	@Override
	public boolean equals( Object obj ) {
		return ( obj instanceof ServiceCurve ) && super.equals( obj );
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
		return "SC" + super.toString();
	}
}
