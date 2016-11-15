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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.Curve;
import unikl.disco.curves.LinearSegment;
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
public class Deconvolution {
	public static Set<ArrivalCurve> deconvolve( Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve ) {
		return deconvolve( arrival_curves, service_curve, false );
	}

	public static Set<ArrivalCurve> deconvolve( Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve, boolean tb_rl_optimized ) {
		Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
		
		for ( ArrivalCurve alpha : arrival_curves ) {
			results.add( deconvolve( alpha, service_curve, tb_rl_optimized ) );
		}
		
		return results;
	}
	
	public static Set<ArrivalCurve> deconvolve( Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves ) {
		return deconvolve( arrival_curves, service_curves, false );
	}
	
	public static Set<ArrivalCurve> deconvolve( Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves, boolean tb_rl_optimized ) {
		Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
		
		for ( ServiceCurve beta : service_curves ) {
			for ( ArrivalCurve alpha : arrival_curves ) {
				results.add( deconvolve( alpha, beta, tb_rl_optimized ) );
			}
		}
		
		return results;
	}
	

	public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs( Set<Curve> curves, Set<ServiceCurve> service_curves ) {
		Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
		
		Num latency;
		for ( Curve sc : service_curves ) {
			for ( Curve c : curves ) {
				latency = c.getLatency();
				results.add( new ArrivalCurve( Curve.shiftRight( deconvolveGeneric( Curve.shiftLeftClipping( c, latency ), sc ), latency ) ) );
			}
		}
			
		return results;
	}
	
	public static ArrivalCurve deconvolve( ArrivalCurve arrival_curve, ServiceCurve service_curve ) {
		return deconvolve( arrival_curve, service_curve, false );
	}
	
	public static ArrivalCurve deconvolve( ArrivalCurve arrival_curve, ServiceCurve service_curve, boolean tb_rl_optimized ) {
		if( service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return arrival_curve;
		}
		if( service_curve.is_zero_delay_infinite_burst ) {
			return arrival_curve;
		}
		if( arrival_curve.equals( ArrivalCurve.createNullArrival() ) ) {
			return arrival_curve;
		}
		if( service_curve.equals( ServiceCurve.createNullService() )
				|| service_curve.getLatency().equals( NumFactory.getPositiveInfinity() )
				|| ( service_curve.getSustainedRate().equals( NumFactory.getZero() ) && service_curve.getSegment( service_curve.getSegmentCount() - 1 ).getY().equals( NumFactory.getZero() ) ) ) {
			return ArrivalCurve.createNullArrival();
		}
		if ( tb_rl_optimized ) {
			return deconvolveTB_RL( arrival_curve, service_curve );
		} else {
			return deconvolveGeneric( arrival_curve, service_curve );
		}
	}
	
	public static ArrivalCurve deconvolveTB_RL( ArrivalCurve arrival_curve, ServiceCurve service_curve ) {
		if( service_curve.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return arrival_curve;
		}
		if( service_curve.equals( ServiceCurve.createNullService() )
				|| service_curve.getLatency().equals( NumFactory.getPositiveInfinity() )
				|| ( service_curve.getSustainedRate().equals( NumFactory.getZero() ) && service_curve.getSegment( 1 ).getY().equals( NumFactory.getZero() ) ) ) {
			return ArrivalCurve.createNullArrival();
		}
		
		// Result: Token bucket gamma_{r,'b'} with r' = r and b' = b+r*T
		return ArrivalCurve.createTokenBucket( arrival_curve.getSustainedRate().doubleValue(),
				arrival_curve.getTBBurst().doubleValue() + arrival_curve.getSustainedRate().doubleValue() * service_curve.getLatency().doubleValue() );
	}
	
	/**
	 * Returns the deconvolution of an (almost) concave arrival curve and
	 * a convex service curve.
	 * 
	 * @param curve_1 The (almost) concave arrival curve.
	 * @param curve_2 The convex service curve.
	 * @return The deconvolved curve, an arrival curve.
	 */
	private static ArrivalCurve deconvolveGeneric( Curve curve_1, Curve curve_2 ) {
		if( curve_1.getSustainedRate().greater( curve_2.getSustainedRate() ) ) { // Violation of the sability constraint
			return ArrivalCurve.createZeroDelayInfiniteBurst();
		}
		if( curve_2.equals( ServiceCurve.createZeroDelayInfiniteBurst() ) ) {
			return new ArrivalCurve( curve_1 );
		}
		if( curve_2.equals( ServiceCurve.createNullService() )
				|| curve_2.getLatency().equals( NumFactory.getPositiveInfinity() )
				|| ( curve_2.getSustainedRate().equals( NumFactory.getZero() ) && curve_2.getSegment( 1 ).getY().equals( NumFactory.getZero() ) ) ) {
			return ArrivalCurve.createNullArrival();
		}
		if ( CalculatorConfig.DECONVOLUTION_CHECKS ) {
			if( !curve_1.isAlmostConcave() ) {
				throw new IllegalArgumentException("Arrival curve of deconvolution must be almost concave.");
			}
			if ( !curve_2.isConvex() ) {
				throw new IllegalArgumentException("Service curve of deconvolution must be convex.");
			}
		}
		
		// The arrival curve itself is in the candidates set.
		Set<Curve> result_candidates = new HashSet<Curve>( Collections.singleton( curve_1.copy() ) );
		
		// Candidates resulting from the service curve's inflection points (curve_2).
		// It's simply the vertical deviation at the inflection point followed by the arrival curve's segments (lowered by beta(inflection)).
		Curve candidate_tmp;
		Num x_inflect_beta, y_beta, y_alpha;
		for( int i = 1; i < curve_2.getSegmentCount(); i++ ) { // Start at 1 to skip the arrival curve itself (see above):
			
			x_inflect_beta = curve_2.getSegment( i ).getX();
			candidate_tmp = Curve.shiftLeftClipping( curve_1, x_inflect_beta );
			
			y_beta = curve_2.f( x_inflect_beta );
			if( y_beta.doubleValue() != 0.0 ) { // Need to lower the rest of the result candidate by y. 
				for( int j = 0; j < candidate_tmp.getSegmentCount(); j++ ) {
					LinearSegment lin_seg = candidate_tmp.getSegment(j);
					y_alpha = lin_seg.getY();
					candidate_tmp.getSegment(j).setY( NumFactory.sub( y_alpha, y_beta ) );
				}
			}
			result_candidates.add( candidate_tmp );
		}

		// Candidates resulting from the arrival curve's inflection points (curve_1) 
		// Take the vertical deviation at the alpha inflection point as burstiness,
		// then go over the beta inflection points smaller than the alpha inflection point in reverse order
		// and add a linear segment that resembeles the beta's one
		// (The first one might be cut off by the alpha inflection point).
		Num x_inflect_alpha, results_cand_burst;
		Num current_segment_length;
		Num next_x_coord, next_y_coord;
		
		for( int i = curve_1.getSegmentCount()-1; i >= 0; i-- ) {
			x_inflect_alpha = curve_1.getSegment( i ).getX();
			y_alpha = curve_1.f( x_inflect_alpha );
			y_beta = curve_2.f( x_inflect_alpha );
			results_cand_burst = NumFactory.sub( y_alpha, y_beta );
			
			if( x_inflect_alpha.equals( NumFactory.getZero() ) // The inflection point is in the origin and thus the candidate is a null curve.
					|| results_cand_burst.less( NumFactory.getZero() ) ) { // At the inflection point, the service curve is larger than the arrival curve.
					continue;
			}
			
			// Found a valid inflection point of the arrival curve.
			// => Start constructing the candidate based on the service curve's first inflection points.
			for( int j = curve_2.getSegmentCount()-1; j >= 0; j-- ) {
				x_inflect_beta = curve_2.getSegment( j ).getX();
				if( x_inflect_beta.greater( x_inflect_alpha ) ) {
					continue;
				}

				// Found the first inflection point of beta to work with,
				// i.e., we now know the resulting curve's amount of segments:
				// The origin, j+1 segments (we start counting j at 0), and a horizontal line at the end.
				
				int segment_count = j + 3; // At least 2 (the origin and and a burst followed by rate 0)
				candidate_tmp = new ArrivalCurve( segment_count );	// Consists of null segments (x,y),r = (0,0),0 only.
																	// The origin (first segment, id 0) stays as is, the remainder needs to be constructed.
				// Compute the second segment
				
				LinearSegment current_candidate_segment = candidate_tmp.getSegment( 1 );
				LinearSegment current_beta_segment = curve_2.getSegment( j );

				current_candidate_segment.setX( NumFactory.createZero() );
				current_candidate_segment.setY( results_cand_burst );
				current_candidate_segment.setGrad( current_beta_segment.getGrad().copy() );

				// The length of this segment is defined by the following one's y-coordinate:
				next_x_coord = NumFactory.sub( x_inflect_alpha, x_inflect_beta );
				next_y_coord = next_x_coord.copy();
				next_y_coord.mult( current_beta_segment.getGrad() );
				next_y_coord.add( results_cand_burst );
				
				LinearSegment prev_beta_segment;
				
				// The remaining service curve segments in reverse order
				int j_new = j; // Alpha's inflection point is above beta's segment j. Now we need to continue decreasing j (renamed to j_new although there is no concurrent modification with primitive int).
				for( int k = 2; k < segment_count-1; k++ ) { // < segment_count-1 because the count is max{index}+1 and the last segment will be a horizontal line (added after this loop).
					j_new--;
					
					current_candidate_segment = candidate_tmp.getSegment( k );
					prev_beta_segment = current_beta_segment;
					current_beta_segment = curve_2.getSegment( j_new );
					
					current_candidate_segment.setX( next_x_coord );
					current_candidate_segment.setY( next_y_coord );
					current_candidate_segment.setGrad( current_beta_segment.getGrad().copy() );
					
					current_segment_length = NumFactory.sub( prev_beta_segment.getX(), current_beta_segment.getX() );
					next_x_coord.add( current_segment_length );
					
					next_y_coord = current_segment_length.copy();
					next_y_coord.mult( current_beta_segment.getGrad() );
					next_y_coord.add( current_candidate_segment.getY() );
					
				}
				
				// Add a horizontal line at the end.
				current_candidate_segment = candidate_tmp.getSegment( segment_count-1 );
				current_candidate_segment.setX( next_x_coord );
				current_candidate_segment.setY( next_y_coord );
				// Gradient (rate) will remain zero.
				
				// Done with this alpha inflection, beta combination. Store the curve and go to next alpha inflection point.
				result_candidates.add( candidate_tmp );
				break;
			}
		}
		
		Iterator<Curve> candidates_iter = result_candidates.iterator();
		Curve sup_curve, additional_curve;
		
		if( !candidates_iter.hasNext() ) {
			System.out.println( "Deconvolution of " + curve_1.toString() + "\nand " + curve_2.toString() + " failed." );
			System.exit( 0 );
		}

		sup_curve = candidates_iter.next();
		while( candidates_iter.hasNext() ) {
			additional_curve = candidates_iter.next();
			sup_curve = Curve.max( sup_curve, additional_curve );
		}
		
		return new ArrivalCurve( sup_curve );
	}
}
