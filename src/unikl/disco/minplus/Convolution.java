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

package unikl.disco.minplus;

import java.util.HashSet;
import java.util.Set;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.Curve;
import unikl.disco.curves.LinearSegment;
import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
public class Convolution {
	public static ArrivalCurve convolve( Set<ArrivalCurve> arrival_curves ) {
		if ( arrival_curves == null || arrival_curves.isEmpty() ) {
			return ArrivalCurve.createNullArrival();
		}
		if( arrival_curves.size() == 1 ) {
			return arrival_curves.iterator().next();
		}
		
		ArrivalCurve arrival_curve_result = ArrivalCurve.createZeroDelayInfiniteBurst();
		for( ArrivalCurve arrival_curve_2 : arrival_curves ) {
			arrival_curve_result = convolve( arrival_curve_result, arrival_curve_2 );
		}
		
		return arrival_curve_result;
	}
	
	public static ArrivalCurve convolve( ArrivalCurve arrival_curve_1, ArrivalCurve arrival_curve_2 ) {
		ArrivalCurve null_arrival = ArrivalCurve.createNullArrival();
		if ( arrival_curve_1 == null || arrival_curve_2 == null 
				|| arrival_curve_1.equals( null_arrival ) || arrival_curve_2.equals( null_arrival )  ) {
			return null_arrival;
		}
		
		ArrivalCurve zero_delay_infinite_burst = ArrivalCurve.createZeroDelayInfiniteBurst();
		if( arrival_curve_1.equals( zero_delay_infinite_burst ) ) {
			return arrival_curve_2;
		}
		if( arrival_curve_2.equals( zero_delay_infinite_burst ) ) {
			return arrival_curve_1;
		}
		
		// Arrival curves are concave concave curves so we can do a minimum instead of a convolution here. 
		ArrivalCurve convolved_arrival_curve = new ArrivalCurve( Curve.min( arrival_curve_1, arrival_curve_2 ) );
		return convolved_arrival_curve;
	}
	
	/**
	 * Returns the convolution of this curve, which must be (almost) concave, and
	 * the given curve, which must also be (almost) concave.
	 * 
	 * @param max_service_curve_1 The fist maximum service curve in the convolution.
	 * @param max_service_curve_2 The second maximum service curve in the convolution.
	 * @return The convolved maximum service curve.
	 */
	public static MaxServiceCurve convolve( MaxServiceCurve max_service_curve_1, MaxServiceCurve max_service_curve_2 ) {
		if ( CalculatorConfig.MAX_SERVICE_CURVE_CHECKS && 
				( !max_service_curve_1.isAlmostConcave() || !max_service_curve_2.isAlmostConcave() ) ) {
			throw new IllegalArgumentException("Both maximum service curves must be almost concave!");
		}

		Num latency_msc_1 = max_service_curve_1.getLatency();
		Num latency_msc_2 = max_service_curve_2.getLatency();
		
		if ( latency_msc_1.equals( NumFactory.getPositiveInfinity() ) ) {
			return max_service_curve_2.copy();
		}
		if ( latency_msc_2.equals( NumFactory.getPositiveInfinity() ) ) {
			return max_service_curve_1.copy();
		}

		// (min,plus)-algebraic proceeding here:
		// Remove latencies, act analog to the convolution of two arrival curves, and the sum of the two latencies.
		ArrivalCurve ac_intermediate = convolve( new ArrivalCurve( max_service_curve_1.removeLatency() ), new ArrivalCurve( max_service_curve_2.removeLatency() ) );
		MaxServiceCurve result = new MaxServiceCurve( ac_intermediate );
		result.shiftRight( Num.add( latency_msc_1, latency_msc_2 ) );
		result.beautify();

		return result;
	}
	
	// Java won't let me call this method "convolve" because it does not care about the Sets' types; tells that there's already another method taking the same arguments.
	public static Set<ServiceCurve> convolve_SCs_SCs( Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2 ) {
		return convolve_SCs_SCs( service_curves_1, service_curves_2, false );
	}
	
	public static Set<ServiceCurve> convolve_SCs_SCs( Set<ServiceCurve> service_curves_1, Set<ServiceCurve> service_curves_2, boolean tb_rl_optimized ) {
		if ( service_curves_1.isEmpty() ) {
			return service_curves_2;
		}
		if ( service_curves_2.isEmpty() ) {
			return service_curves_1;
		}
		
		Set<ServiceCurve> results = new HashSet<ServiceCurve>();
		
		for ( ServiceCurve beta_1 : service_curves_1 ) {
			for ( ServiceCurve beta_2 : service_curves_2 ) {
				results.add( convolve( beta_1, beta_2, tb_rl_optimized ) );
			}
		}
		
		return results;
	}
	
	public static ServiceCurve convolve( ServiceCurve service_curve_1, ServiceCurve service_curve_2 ) {
		return convolve( service_curve_1, service_curve_2, false ); 
	}

	public static ServiceCurve convolve( ServiceCurve service_curve_1, ServiceCurve service_curve_2, boolean tb_rl_optimized ) {
		if ( service_curve_1.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return service_curve_2.copy();
		} else {
			if ( service_curve_2.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
				return service_curve_1.copy();
			}
		}
		ServiceCurve null_service = ServiceCurve.createNullService(); 
		if( service_curve_1.equals( null_service ) || service_curve_2.equals( null_service ) ){
			return null_service;
		}
		
		if ( tb_rl_optimized ) {
			return convolve_SC_SC_RLs( service_curve_1, service_curve_2 );
		} else {
			return convolve_SC_SC_Generic( service_curve_1, service_curve_2 );
		}
	}
	
	private static ServiceCurve convolve_SC_SC_RLs( ServiceCurve service_curve_1, ServiceCurve service_curve_2 ) {
		return ServiceCurve.createRateLatency( 
				Math.min( service_curve_1.getSustainedRate().doubleValue(), service_curve_2.getSustainedRate().doubleValue() ), 
				service_curve_1.getLatency().doubleValue() + service_curve_2.getLatency().doubleValue() );
	}
	
	/**
	 * Returns the convolution of two curve, which must be convex
	 * 
	 * @param service_curve_1 The first curve to convolve with.
	 * @param service_curve_2 The second curve to convolve with.
	 * @return The convolved curve.
	 */
	public static ServiceCurve convolve_SC_SC_Generic( ServiceCurve service_curve_1, ServiceCurve service_curve_2 ) {
		if ( service_curve_1.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return service_curve_2.copy();
		} else {
			if ( service_curve_2.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
				return service_curve_1.copy();
			}
		}
		
		if ( service_curve_1.isBurstDelay() && service_curve_1.getSegment(1).getX().equals( NumFactory.getZero() ) ) {
			return service_curve_2.copy();
		}
		if ( service_curve_2.isBurstDelay() && service_curve_2.getSegment(1).getX().equals( NumFactory.getZero() ) ) {
			return service_curve_1.copy();
		}

		ServiceCurve result = new ServiceCurve();
		
		Num x = NumFactory.createZero();
		Num y = Num.add( service_curve_1.f( NumFactory.getZero() ), service_curve_2.f( NumFactory.getZero() ) );
		Num grad = NumFactory.createZero();
		LinearSegment s = new LinearSegment( x, y, grad, false );
		result.addSegment(s);

		int i1 = (service_curve_1.isRealDiscontinuity(0)) ? 1 : 0;
		int i2 = (service_curve_2.isRealDiscontinuity(0)) ? 1 : 0;
		if (i1 > 0 || i2 > 0) {
			y = Num.add( service_curve_1.fLimitRight( NumFactory.getZero() ), service_curve_2.fLimitRight( NumFactory.getZero() ) );
			grad = NumFactory.createZero();
			s = new LinearSegment( x, y, grad, true );
			
			result.addSegment(s);
		}

		while(i1 < service_curve_1.getSegmentCount() || i2 < service_curve_2.getSegmentCount()) {
			if ( service_curve_1.getSegment(i1).getGrad().less( service_curve_2.getSegment(i2).getGrad() ) ) {
				if (i1+1 >= service_curve_1.getSegmentCount()) {
					result.getSegment(result.getSegmentCount()-1).setGrad( service_curve_1.getSegment(i1).getGrad() );
					break;
				}

				x = service_curve_1.getSegment( i1+1 ).getX().copy();
				x.sub( service_curve_1.getSegment( i1 ).getX() );
				x.add( result.getSegment( result.getSegmentCount()-1 ).getX() );
				
				y = service_curve_1.getSegment( i1+1 ).getY().copy();
				y.sub( service_curve_1.getSegment( i1 ).getY() );
				y.add( result.getSegment( result.getSegmentCount()-1 ).getY() );
				
				s = new LinearSegment( x, y, NumFactory.createZero(), true );

				result.getSegment(result.getSegmentCount()-1).setGrad( service_curve_1.getSegment(i1).getGrad() );
				result.addSegment(s);
				
				i1++;
			} else {
				if (i2+1 >= service_curve_2.getSegmentCount()) {
					result.getSegment(result.getSegmentCount()-1).setGrad( service_curve_2.getSegment(i2).getGrad() );
					break;
				}
				
				x = service_curve_2.getSegment( i2+1 ).getX();
				x.sub( service_curve_2.getSegment( i2 ).getX() );
				x.add( result.getSegment( result.getSegmentCount()-1 ).getX() );
				
				y = service_curve_2.getSegment( i2+1 ).getY().copy();
				y.sub( service_curve_2.getSegment( i2 ).getY() );
				y.add( result.getSegment( result.getSegmentCount()-1 ).getY() );
				
				s = new LinearSegment( x, y, NumFactory.createZero(), true );
				
				result.getSegment(result.getSegmentCount()-1).setGrad( service_curve_2.getSegment(i2).getGrad() );
				result.addSegment(s);
				
				i2++;
			}
		}
		
		result.beautify();
		
		return result;
	}
	
	// The result is used like an arrival curve, yet it is not really one. This inconsistency occurs because we need to consider MSC and SC in some order during the output bound computation.
	public static Set<Curve> convolve_ACs_MSC( Set<ArrivalCurve> arrival_curves, MaxServiceCurve maximum_service_curve ) throws Exception {
		Num msc_latency = maximum_service_curve.getLatency();
		Set<Curve> result = new HashSet<Curve>();
		
		// Similar to convolve_ACs_EGamma
		ArrivalCurve msc_as_ac = new ArrivalCurve( maximum_service_curve.removeLatency() ); // Abuse the ArrivalCurve class here for convenience.
		for ( ArrivalCurve ac : arrival_curves ) {
			result.add( ArrivalCurve.shiftRight( convolve( ac, msc_as_ac ), msc_latency ) );
		}
		
		return result;
	}
	
	public static Set<ArrivalCurve> convolve_ACs_EGamma( Set<ArrivalCurve> arrival_curves, MaxServiceCurve extra_gamma_curve ) throws Exception {
		if( extra_gamma_curve.getLatency().greater( NumFactory.getZero() ) ) {
			throw new Exception( "Cannot convolve with an extra gamma curve with latency" );
		}
		
		Set<ArrivalCurve> result = new HashSet<ArrivalCurve>();
		ArrivalCurve extra_gamma_as_ac = new ArrivalCurve( extra_gamma_curve ); // Abuse the ArrivalCurve class here for convenience.
		for ( ArrivalCurve ac : arrival_curves ) {
			result.add( convolve( ac, extra_gamma_as_ac ) );
		}
		
		return result;
	}
}
