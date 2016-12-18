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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.misc.SetUtils;
import unikl.disco.nc.AnalysisConfig.GammaFlag;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.network.Flow;
import unikl.disco.network.Link;
import unikl.disco.network.Network;
import unikl.disco.network.Path;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;
import unikl.disco.minplus.Convolution;
import unikl.disco.minplus.Deconvolution;

/**
 * 
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 *
 */
public class PmooArrivalBound extends ArrivalBound {
	
	@SuppressWarnings("unused")
	private PmooArrivalBound() {}
	
	public PmooArrivalBound( Network network, AnalysisConfig configuration ) {
		this.network = network;
		this.configuration = configuration;
	}
	
	public Set<ArrivalCurve> computeArrivalBound( Link link, Flow flow_of_interest ) throws Exception {
		return computeArrivalBound( link, network.getFlows( link ), flow_of_interest );
	}
	
	/**
	 * Computes the PMOO arrival bound for a set of
	 * <code>flows_to_bound</code>. The difference to the standard output
	 * bound method is that this method tries to compute tighter bounds by
	 * concatenating as many servers as possible using the PMOO approach. It
	 * does so by searching from <code>server</code> towards the sinks of the
	 * flows contained in <code>f_xfcaller</code> until it reaches the
	 * server where all these flows first meet each other (the
	 * "splitting point"). It then concatenates all servers between the
	 * splitting point (inclusive) and <code>server</code> (exclusive).
	 * 
	 * @param link The link that all flows of interest flow into.
	 * @param f_xfcaller The set of flows of interest.
	 * @param flow_of_interest The flow of interest to handle with lowest priority.
	 * @return The PMOO arrival bounds.
	 * @throws Exception If any of the sanity checks fails.
	 */
	public Set<ArrivalCurve> computeArrivalBound( Link link, Set<Flow> f_xfcaller, Flow flow_of_interest ) throws Exception {
		Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>( Collections.singleton( ArrivalCurve.createZeroArrival() ) );
		if ( f_xfcaller == null || f_xfcaller.isEmpty() )
		{
			return alphas_xfcaller;
		}
		
		// Get the common sub-path of f_xfcaller flows crossing the given link
		Server to = link.getDest();
		Set<Flow> f_to = network.getFlows( to );
		Set<Flow> f_xfcaller_to = SetUtils.getIntersection( f_to, f_xfcaller );
		f_xfcaller_to.remove( flow_of_interest );
		if ( f_xfcaller_to.isEmpty() ) {
			return alphas_xfcaller;
		}
		
		if( configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO 
				|| ( configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL && link.getSource().multiplexingDiscipline() == Multiplexing.FIFO ) )
		{
			throw new Exception( "PMOO arrival bounding is not available for FIFO multiplexing nodes" );
		}
		
		Server from = network.findSplittingServer( to, f_xfcaller_to );
		Path common_subpath;
		Set<ServiceCurve> betas_loxfcaller_subpath = new HashSet<ServiceCurve>();
		ServiceCurve null_service = ServiceCurve.createZeroService();
		
		if ( from.equals( to ) ) { // Shortcut if the common subpath only consists of a single hop
			common_subpath = new Path( to );

			Set<Flow> f_xxfcaller = network.getFlows( link );
			f_xxfcaller.removeAll( f_xfcaller );
			f_xxfcaller.remove( flow_of_interest );
			Set<ArrivalCurve> alphas_xxfcaller = super.computeArrivalBounds( to, f_xxfcaller, flow_of_interest );
			
			for( ServiceCurve beta_loxfcaller_subpath : LeftOverService.arbMux( to.getServiceCurve(), alphas_xxfcaller ) ) {
				if( !beta_loxfcaller_subpath.equals( null_service ) ) {
					betas_loxfcaller_subpath.add( beta_loxfcaller_subpath ); // Adding to the set, not adding up the curves
				}
			}
		} else {
			common_subpath = f_xfcaller_to.iterator().next().getPath().getSubPath( from, link.getSource() );

			PmooAnalysis pmoo = new PmooAnalysis( network, configuration );
			betas_loxfcaller_subpath = pmoo.getServiceCurves( flow_of_interest, common_subpath, f_xfcaller_to );
		}
		
		// Check if there's any service left on this path. Signaled by at least one service curve in this set
		if( betas_loxfcaller_subpath.isEmpty() ) {
			System.out.println( "No service left over during PMOO arrival bounding!" );
			alphas_xfcaller.clear();
			alphas_xfcaller.add( ArrivalCurve.createZeroDelayInfiniteBurst() );
			return alphas_xfcaller;
		}
		
		// Get arrival bound at the splitting point:
		// We need to know the arrival bound of f_xfcaller at the server 'from', i.e., at the above sub-path's source
		// in order to deconvolve it with beta_loxfcaller_subpath to get the arrival bound of the sub-path
		// Note that flows f_xfcaller that originate in 'from' are covered by this call of computeArrivalBound
		Set<ArrivalCurve> alpha_xfcaller_from = super.computeArrivalBounds( from, f_xfcaller, flow_of_interest );
		
		// Convolve to get the bound.
		// See "Improving Performance Bounds in Feed-Forward Networks by Paying Multiplexing Only Once", Lemma 2
		if( configuration.useGamma() != GammaFlag.GLOBALLY_OFF  )
		{
			MaxServiceCurve gamma = common_subpath.getGamma();
			alphas_xfcaller = Deconvolution.deconvolve_almostConcCs_SCs( Convolution.convolve_ACs_MSC( alpha_xfcaller_from, gamma ), betas_loxfcaller_subpath );
		}
		else
		{
			alphas_xfcaller = Deconvolution.deconvolve( alpha_xfcaller_from, betas_loxfcaller_subpath, configuration.tbrlDeconvolution() );
		}
		
		if( configuration.useExtraGamma() != GammaFlag.GLOBALLY_OFF )
		{
			MaxServiceCurve extra_gamma = common_subpath.getExtraGamma();
			alphas_xfcaller = Convolution.convolve_ACs_EGamma( alphas_xfcaller, extra_gamma );
		}
		
		return alphas_xfcaller;
	}
}
