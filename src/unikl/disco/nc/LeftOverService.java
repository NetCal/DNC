/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2017 Steffen Bondorf
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

package unikl.disco.nc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unikl.disco.curves.Curve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumUtils;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
public final class LeftOverService {
	private LeftOverService() {}

	public static Set<ServiceCurve> fifoMux( ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves ) {
		Set<ServiceCurve> results = new HashSet<ServiceCurve>();
		
		for( ArrivalCurve alpha : arrival_curves ) {
			results.add( fifoMux( service_curve, alpha ) );
		}
		
		return results;
	}
	
	/**
	 * Computes the left-over FIFO service curve for a server with the service curve
	 * <code>beta</code> experiencing cross-traffic with arrival curve <code>alpha</code>.
	 * 
	 * It computes the left-over service curve with the smallest latency T in a
	 * worst-case FIFO multiplexing scenario. T is defined as the first time instance when
	 * the arrival curve's burst is worked off and its arrival rate is smaller than
	 * the service curve's service rate. At this time it can be safely assumed that
	 * the system has spare capacity that, in the FIFO multiplexing scheme,
	 * will be used to serve other flows' data that arrived in the meantime.
	 * 
	 * @param arrival_curve The arrival curve of cross-traffic
	 * @param service_curve The server's service curve
	 * @return The FIFO service curve
	 */
	public static ServiceCurve fifoMux( ServiceCurve service_curve, ArrivalCurve arrival_curve ) {
		if( arrival_curve.equals( ArrivalCurve.createNullArrival() ) 
				|| service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return service_curve.copy();
		}
		if( service_curve.equals( ServiceCurve.createNullService() ) ) {
			return ServiceCurve.createNullService();
		}
		
		if ( CalculatorConfig.FIFO_MUX_CHECKS ) {
			if( !arrival_curve.isConcave() ) {
				throw new IllegalArgumentException( "Arrival curve must be concave." );
			}
			
			if( !service_curve.isConvex() ) {
				throw new IllegalArgumentException( "Service curve must be convex." );
			}	
		}
		
		List<Num> ycoords = Curve.computeInflectionPointsY( arrival_curve, service_curve );
		for( int i = 0; i < ycoords.size(); i++ ) {
			Num ip_y = ( ycoords.get( i ) );
			if ( ip_y.less( arrival_curve.getBurst() ) ) {
				continue;
			}

			Num x_alpha = arrival_curve.f_inv( ip_y, false );
			Num x_beta  = service_curve.f_inv( ip_y, true );
			
			if ( arrival_curve.getGradientLimitRight( x_alpha ).leq( service_curve.getGradientLimitRight( x_beta ) ) ) {
				
				Num theta = NumUtils.sub( x_beta, x_alpha );
				ServiceCurve beta_fifo = ServiceCurve.boundAtXAxis(
											ServiceCurve.min( 
												ServiceCurve.sub( service_curve, ArrivalCurve.shiftRight( arrival_curve, theta ) ), 
												ServiceCurve.createDelayedInfiniteBurst( x_beta )
											) 
										);
	    		return beta_fifo;
			}
		}

		// Reaching this code means that there's no service left-over
		return ServiceCurve.createNullService();
	}
	
	public static Set<ServiceCurve> arbMux( ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves ) {
		Set<ServiceCurve> results = new HashSet<ServiceCurve>();
		
		for( ArrivalCurve alpha : arrival_curves ) {
			results.add( arbMux( service_curve, alpha ) );
		}
		
		return results;
	}
	
	/**
	 * Computes the left-over service curve for a server under arbitrary multiplexing
	 * with the service curve <code>beta</code> experiencing cross-traffic 
	 * with arrival curve <code>alpha</code>.
	 * 
	 * @param arrival_curve The arrival curve of cross-traffic
	 * @param service_curve The server's service curve
	 * @return The FIFO service curve
	 */
	public static ServiceCurve arbMux( ServiceCurve service_curve, ArrivalCurve arrival_curve ) {
		if( arrival_curve.equals( ArrivalCurve.createNullArrival() ) 
				|| service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return service_curve.copy();
		}
		if( service_curve.equals( ServiceCurve.createNullService() ) ) {
			return ServiceCurve.createNullService();
		}
		
		return new ServiceCurve( ServiceCurve.boundAtXAxis( ServiceCurve.sub( service_curve, arrival_curve ) ) );
	}
}
