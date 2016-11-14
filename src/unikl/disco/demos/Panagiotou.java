/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.5 "Hydra".
 *
 * Copyright (C) 2011 - 2015 Steffen Bondorf
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

package unikl.disco.demos;

import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;

import static org.junit.Assert.assertEquals;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.PmooAnalysis;
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.TotalFlowAnalysis;
import unikl.disco.nc.AnalysisConfig.GammaFlag;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Server;

/**
 * 
 * @author Steffen Bondorf
 */
public class Panagiotou {
	
	static Server s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14;
	static Flow f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13;

	public static void main( String[] args ) {
		Panagiotou demo = new Panagiotou();
		
		try {
			demo.run();
		} catch (Exception e) {
			System.out.println( e.toString() );
		}
	}
	
	public Panagiotou() {}

	public void run() throws Exception {
		ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( 1.0e4, 1.0e4);
		
		ServiceCurve service_curve = ServiceCurve.createRateLatency( 10.0e4, 0.095  );
		MaxServiceCurve max_service_curve = MaxServiceCurve.createRateLatency( 25.0e4, 0 );
		
		Network network = new Network();
			
		s0 = network.addServer( service_curve, max_service_curve );
		s1 = network.addServer( service_curve, max_service_curve );
		s2 = network.addServer( service_curve, max_service_curve );
		s3 = network.addServer( service_curve, max_service_curve );
		s4 = network.addServer( service_curve, max_service_curve );
		s5 = network.addServer( service_curve, max_service_curve );
		s6 = network.addServer( service_curve, max_service_curve );
		s7 = network.addServer( service_curve, max_service_curve );
		s8 = network.addServer( service_curve, max_service_curve );
		s9 = network.addServer( service_curve, max_service_curve );
		s10 = network.addServer( service_curve, max_service_curve );
		s11 = network.addServer( service_curve, max_service_curve );
		s12 = network.addServer( service_curve, max_service_curve );
		s13 = network.addServer( service_curve, max_service_curve );
		s14 = network.addServer( service_curve, max_service_curve );
		
		try {
			network.addLink( s1, s0 );
			network.addLink( s2, s0 );
			network.addLink( s3, s1 );
			network.addLink( s4, s1 );
			network.addLink( s5, s2 );
			network.addLink( s6, s2 );
			network.addLink( s7, s3 );
			network.addLink( s8, s3 );
			network.addLink( s9, s4 );
			network.addLink( s10, s4 );
			network.addLink( s11, s5 );
			network.addLink( s12, s5 );
			network.addLink( s13, s6 );
			network.addLink( s14, s6 );
		} catch (Exception e) {
			System.out.println( e.toString() );
			assertEquals( "Unexpected exception occured", 0, 1 );
			return;
		}
		
		try {	
			f0 = network.addFlow( arrival_curve, s7, s0 );
			f1 = network.addFlow( arrival_curve, s8, s0 );
			f2 = network.addFlow( arrival_curve, s9, s0 );
			f3 = network.addFlow( arrival_curve, s10, s0 );
			f4 = network.addFlow( arrival_curve, s11, s0 );
			f5 = network.addFlow( arrival_curve, s12, s0 );
			f6 = network.addFlow( arrival_curve, s13, s0 );
			f7 = network.addFlow( arrival_curve, s14, s0 );
			
			f8 = network.addFlow( arrival_curve, s3, s0 );
			f9 = network.addFlow( arrival_curve, s4, s0 );
			f10 = network.addFlow( arrival_curve, s5, s0 );
			f11 = network.addFlow( arrival_curve, s6, s0 );
			
			f12 = network.addFlow( arrival_curve, s1, s0 );
			f13 = network.addFlow( arrival_curve, s2, s0 );
			
		} catch (Exception e) {
			System.out.println( e.toString() );
			assertEquals( "Unexpected exception occured", 0, 1 );
			return;
		}

		AnalysisConfig configuration = new AnalysisConfig();

//		Server s0 = network.addServer( service_curve, max_service_curve );
		// Creating a server with a maximum service curve automatically triggers the following setting
//		s0.setUseGamma( true );
//		s0.setUseExtraGamma( true );
		// , however, disabling the use of a maximum service curve in a configuration overrides it.
		configuration.setUseGamma( GammaFlag.GLOBALLY_OFF );
		configuration.setUseExtraGamma( GammaFlag.GLOBALLY_OFF);
		
		configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_FIFO );
		
//		Flow flow_of_interest = network.addFlow( arrival_curve, s0 );

		System.out.println( "Number of flows: " + network.numFlows() );
		System.out.println();

//		Analyze the network
//		TFA
		System.out.println( "--- Total Flow Analysis ---" );
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, configuration );
		
		try {
			tfa.performAnalysis( f7 );
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
	
//		SFA
		System.out.println( "--- Separated Flow Analysis ---" );
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, configuration );
		
		try {
			sfa.performAnalysis( f0 );
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
	
//		PMOO
		System.out.println( "--- PMOO Analysis ---" );
		PmooAnalysis pmoo = new PmooAnalysis( network, configuration );
		
		try {
			pmoo.performAnalysis( f0 );
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
