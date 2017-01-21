/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.1 "Centaur".
 *
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

package unikl.disco.demos;

import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.PmooAnalysis;
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.TotalFlowAnalysis;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Server;

/**
 * 
 * @author Steffen Bondorf
 */
public class Demo2 {

	public static void main( String[] args ) {
		Demo2 demo = new Demo2();
		
		try {
			demo.run();
		} catch (Exception e) {
			System.out.println( e.toString() );
		}
	}
	
	public Demo2() {}

	public void run() throws Exception {
		ServiceCurve service_curve = ServiceCurve.createRateLatency( 10.0e6, 0.01 );
		MaxServiceCurve max_service_curve = MaxServiceCurve.createRateLatency( 100.0e6, 0.001 );
		
		Network network = new Network();
		
		Server s0 = network.addServer( service_curve, max_service_curve );
		s0.setUseGamma( false );
		s0.setUseExtraGamma( false );
		
		Server s1 = network.addServer( service_curve, max_service_curve );
		s1.setUseGamma( false );
		s1.setUseExtraGamma( false );
	
		network.addLink( s0, s1 );

		ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( 0.1e6, 0.1 * 0.1e6 );
		
		network.addFlow( arrival_curve, s1 );
		network.addFlow( arrival_curve, s0, s1 );
		
		for ( Flow flow_of_interest : network.getFlows() ) {
			System.out.println( "Flow of interest : " + flow_of_interest.toString() );
			System.out.println();

//			Analyze the network
//			TFA
			System.out.println( "--- Total Flow Analysis ---" );
			// If no analysis configuration is given, the defaults are used
			TotalFlowAnalysis tfa = new TotalFlowAnalysis( network );
			
			try {
				tfa.performAnalysis( flow_of_interest );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
			}
			
			System.out.println();

//			SFA
			System.out.println( "--- Separated Flow Analysis ---" );
			// If no analysis configuration is given, the defaults are used
			SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network );
			
			try {
				sfa.performAnalysis( flow_of_interest );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
			}
			
			System.out.println();
			
//			PMOO
			// If no analysis configuration is given, the defaults are used
			System.out.println( "--- PMOO Analysis ---" );
			PmooAnalysis pmoo = new PmooAnalysis( network );
			
			try {
				pmoo.performAnalysis( flow_of_interest );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
			}

			System.out.println();
			System.out.println();
		}
	}
}