/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2013 - 2016 Steffen Bondorf
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

package unikl.disco.tests;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.nc.BacklogBound;
import unikl.disco.nc.PmooAnalysis;
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.TotalFlowAnalysis;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Server;
import unikl.disco.numbers.NumFactory;

@RunWith(value = Parameterized.class)
/**
 * 
 * @author Steffen Bondorf
 *
 */
public class Single_1Flow extends FunctionalTests
{
	static Network network;
	static Server s0;
	static Flow f0;
	
	public Single_1Flow( FunctionalTestConfig test_config ) {
		super( test_config );
	}

	@Before
	public void createNetwork()
	{
		ServiceCurve service_curve = ServiceCurve.createRateLatency( 10, 10 );
		ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( 5, 25 );
		
		network = new Network();
		
		s0 = network.addServer( service_curve );
		s0.setUseGamma( false );
		s0.setUseExtraGamma( false );
	
		try {	
			f0 = network.addFlow( arrival_curve, s0 );
		} catch (Exception e) {
			System.out.println( e.toString() );
			assertEquals( "Unexpected exception occured", 0, 1 );
			return;
		}
	}
	
	@Test
	public void f0_tfa_fifoMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
					
			try {
				tfa.performAnalysis( f0 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA FIFO delay", NumFactory.createNum( 12.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.createNum( 75 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		} else {
			try {
				tfa.performAnalysis( f0 );

				assertEquals( "TFA FIFO delay", NumFactory.createNum( 12.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.createNum( 75 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f0_tfa_arbMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
					
			try {
				tfa.performAnalysis( f0 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA ARB delay", NumFactory.createNum( 12.5 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.createNum( 75 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f0 );

				assertEquals( "TFA ARB delay", NumFactory.createNum( 12.5 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.createNum( 75 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f0_sfa_fifoMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );

		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f0 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA FIFO delay", NumFactory.createNum( 12.5 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.createNum( 75 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		} else {
			try {
				sfa.performAnalysis( f0 );

				assertEquals( "SFA FIFO delay", NumFactory.createNum( 12.5 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.createNum( 75 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f0_sfa_arbMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f0 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA ARB delay", NumFactory.createNum( 12.5 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.createNum( 75 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f0 );

				assertEquals( "SFA ARB delay", NumFactory.createNum( 12.5 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.createNum( 75 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}

	@Test
	public void f0_pmoo_arbMux()
	{
		PmooAnalysis pmoo = new PmooAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				pmoo.performAnalysis( f0 );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
				System.out.println();
				
				assertEquals( "PMOO ARB delay", NumFactory.createNum( 12.5 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.createNum( 75 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f0 );

				assertEquals( "PMOO ARB delay", NumFactory.createNum( 12.5 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.createNum( 75 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f0_sinktree_arbMux()
	{
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				double backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( network, f0.getSink() );
				System.out.println( "backlog bound   : " + Double.toString( backlog_bound ) );
				System.out.println();
				
				assertEquals( "Tree backlog", 75, backlog_bound, 0.0 );
			} catch (Exception e) {
				System.out.println( "Tree Backlog Bound Calculation failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				double backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( network, f0.getSink() );

				assertEquals( "Tree backlog", 75, backlog_bound, 0.0 );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
}
