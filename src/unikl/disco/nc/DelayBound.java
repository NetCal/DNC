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

package unikl.disco.nc;

import unikl.disco.curves.Curve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
public class DelayBound {
	public static Num deriveARB( ArrivalCurve arrival_curve, ServiceCurve service_curve ) {
		if ( service_curve.equals( ServiceCurve.createNullService() )
				&& !arrival_curve.equals( ArrivalCurve.createNullArrival() ) ) {
			return NumFactory.createPositiveInfinity();
		}
		
		if ( arrival_curve.equals( ArrivalCurve.createNullArrival() ) 
				|| service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return NumFactory.createZero();
		} else {
			if( arrival_curve.getSustainedRate().greater( service_curve.getSustainedRate() ) ) { // Only if the service curve is
				return NumFactory.createPositiveInfinity();
			}
		}
		
		return Curve.getXIntersection( arrival_curve, service_curve );
	}

	// Single flow to be bound, i.e., fifo per micro flow holds
	public static Num deriveFIFO( ArrivalCurve arrival_curve, ServiceCurve service_curve ) {
		if ( service_curve.equals( ServiceCurve.createNullService() )
				&& !arrival_curve.equals( ArrivalCurve.createNullArrival() ) ) {
			return NumFactory.createPositiveInfinity();
		}
		
		if ( arrival_curve.equals( ArrivalCurve.createNullArrival() ) 
				|| service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return NumFactory.createZero();
		} else {
			if( arrival_curve.getSustainedRate().greater( service_curve.getSustainedRate() ) ) { // Only if the service curve is
				return NumFactory.createPositiveInfinity();
			}
		}

		Num result = NumFactory.createNegativeInfinity();
		for( int i = 0; i < arrival_curve.getSegmentCount(); i++ ) {
			Num ip_y = arrival_curve.getSegment( i ).getY();

			Num delay = service_curve.f_inv( ip_y, true );
			delay.sub( arrival_curve.f_inv( ip_y, false ) );
			result = Num.max( result, delay );
		}
		for( int i = 0; i < service_curve.getSegmentCount(); i++ ) {
			Num ip_y = service_curve.getSegment( i ).getY();

			Num delay = service_curve.f_inv( ip_y, true );
			delay.sub( arrival_curve.f_inv( ip_y, false ) );
			result = Num.max( result, delay );
		}
		
		return Num.max( NumFactory.getZero(), result );
	}
}
