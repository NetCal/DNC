/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
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
public class Single_10Flows_10ACs extends FunctionalTests
{
	private Network network;
	private Server s0;
	private Flow f0, f6;
	 
	public Single_10Flows_10ACs( FunctionalTestConfig test_config ) {
		super( test_config );
	}
	
	@Before
	public void createNetwork()
	{
		ServiceCurve service_curve = ServiceCurve.createRateLatency( 10, 10 );
		ArrivalCurve[] arrival_curves = new ArrivalCurve[10];
		
		network = new Network();
		
		s0 = network.addServer( service_curve );
		s0.setUseGamma( false );
		s0.setUseExtraGamma( false );
	
		try {
			for( int i = 1; i <= 10; i++ ){
				arrival_curves[i-1] = ArrivalCurve.createTokenBucket( i*0.1, i );
				switch( i ){
					case 1: f0 = network.addFlow( arrival_curves[i-1], s0 );
							break;
					case 7: f6 = network.addFlow( arrival_curves[i-1], s0 );
							break;
					default: network.addFlow( arrival_curves[i-1], s0 );
							 break;
				}
			}
		} catch (Exception e) {
			System.out.println( e.toString() );
			System.out.println( e.toString() );
			assertEquals( "Unexpected exception occured", 0, 1 );
			return;
		}
	}
	
//--------------------Flow 0--------------------	
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
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 15.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
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

				assertEquals( "TFA FIFO delay", NumFactory.create( 15.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
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
				
				assertEquals( "TFA ARB delay", NumFactory.create( 310, 9 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f0 );

				assertEquals( "TFA ARB delay", NumFactory.create( 310, 9 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
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
				
				assertEquals( "SFA FIFO delay", NumFactory.create( 1796, 115 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 127, 50 ), sfa.getBacklogBound() );
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

				assertEquals( "SFA FIFO delay", NumFactory.create( 1796, 115 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 127, 50 ), sfa.getBacklogBound() );
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
				
				assertEquals( "SFA ARB delay", NumFactory.create( 775, 23 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 100, 23 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f0 );

				assertEquals( "SFA ARB delay", NumFactory.create( 775, 23 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 100, 23 ), sfa.getBacklogBound() );
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
				
				assertEquals( "PMOO ARB delay", NumFactory.create( 775, 23 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 100, 23 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f0 );

				assertEquals( "PMOO ARB delay", NumFactory.create( 775, 23 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 100, 23 ), pmoo.getBacklogBound() );
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
				
				assertEquals( "Tree backlog", 110, backlog_bound, NumFactory.getEpsilon().doubleValue() );
			} catch (Exception e) {
				System.out.println( "Tree Backlog Bound Calculation failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				double backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( network, f0.getSink() );

				assertEquals( "Tree backlog", 110, backlog_bound, NumFactory.getEpsilon().doubleValue() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}

//--------------------Flow 6--------------------	
	@Test
	public void f6_tfa_fifoMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
					
			try {
				tfa.performAnalysis( f6 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 15.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
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
				tfa.performAnalysis( f6 );

				assertEquals( "TFA FIFO delay", NumFactory.create( 15.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f6_tfa_arbMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
					
			try {
				tfa.performAnalysis( f6 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA ARB delay", NumFactory.create( 310, 9 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f6 );

				assertEquals( "TFA ARB delay", NumFactory.create( 310, 9 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 110 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f6_sfa_fifoMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );

		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f6 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA FIFO delay", NumFactory.create( 2099, 130 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 434, 25 ), sfa.getBacklogBound() );
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
				sfa.performAnalysis( f6 );

				assertEquals( "SFA FIFO delay", NumFactory.create( 2099, 130 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 434, 25 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f6_sfa_arbMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f6 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA ARB delay", NumFactory.create( 775, 26 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 350, 13 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f6 );

				assertEquals( "SFA ARB delay", NumFactory.create( 775, 26 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 350, 13 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f6_pmoo_arbMux()
	{
		PmooAnalysis pmoo = new PmooAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				pmoo.performAnalysis( f6 );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
				System.out.println();
				
				assertEquals( "PMOO ARB delay", NumFactory.create( 775, 26 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 350, 13 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f6 );

				assertEquals( "PMOO ARB delay", NumFactory.create( 775, 26 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 350, 13 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f6_sinktree_arbMux()
	{
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f6.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				double backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( network, f6.getSink() );
				System.out.println( "backlog bound   : " + Double.toString( backlog_bound ) );
				System.out.println();
				
				assertEquals( "Tree backlog", 110, backlog_bound, NumFactory.getEpsilon().doubleValue() );
			} catch (Exception e) {
				System.out.println( "Tree Backlog Bound Calculation failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				double backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( network, f6.getSink() );

				assertEquals( "Tree backlog", 110, backlog_bound, NumFactory.getEpsilon().doubleValue() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
}
