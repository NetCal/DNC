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

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.PmooAnalysis;
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.TotalFlowAnalysis;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.network.Link;
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
public class FeedForward_1SC_4Flows_1AC_4Paths extends FunctionalTests
{
	private Network network;
	private Server s0, s1, s2, s3;
	private Flow f0, f1, f2, f3;
	private Link l_s0_s1, l_s1_s3, l_s2_s0, l_s0_s3;
	 
	public FeedForward_1SC_4Flows_1AC_4Paths( FunctionalTestConfig test_config ) {
		super( test_config );
	}
	
	@Before
	public void createNetwork()
	{
		ServiceCurve service_curve = ServiceCurve.createRateLatency( 20, 20 );
		ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( 5, 25 );
		
		network = new Network();
		
		s0 = network.addServer( service_curve );
		s1 = network.addServer( "s1", service_curve );
		s2 = network.addServer( service_curve );
		s3 = network.addServer( "s3", service_curve );

		try {
			l_s0_s1 = network.addLink( s0, s1 );
			l_s0_s3 = network.addLink( s0, s3 );
			l_s1_s3 = network.addLink( s1, s3 );
			l_s2_s0 = network.addLink( s2, s0 );
			network.addLink( s2, s1 );
			network.addLink( s2, s3 );
		} catch (Exception e) {
			System.out.println( e.toString() );
			assertEquals( "Unexpected exception occured", 0, 1 );
			return;
		}
		
		List<Link> f0_path = new LinkedList<Link>();
		f0_path.add( l_s0_s1 );
		f0_path.add( l_s1_s3 );
		
		List<Link> f3_path = new LinkedList<Link>();
		f3_path.add( l_s2_s0 );
		f3_path.add( l_s0_s3 );
		
		try {	
			f0 = network.addFlow( arrival_curve, f0_path );
			f1 = network.addFlow( arrival_curve, s2, s3 );
			f2 = network.addFlow( arrival_curve, s2, s1 );
			f3 = network.addFlow( arrival_curve, f3_path );
		} catch (Exception e) {
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
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 3735, 32 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
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

				assertEquals( "TFA FIFO delay", NumFactory.create( 3735, 32 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
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
				
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 2765, 6 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 1370, 3 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f0 );

				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 2765, 6 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 1370, 3 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
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

				// PBOO AB
				assertEquals( "SFA FIFO delay", NumFactory.create( 1525, 16 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 7825, 16 ), sfa.getBacklogBound() );
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

				// PBOO AB
				assertEquals( "SFA FIFO delay", NumFactory.create( 1525, 16 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 7825, 16 ), sfa.getBacklogBound() );
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

				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "SFA ARB delay", NumFactory.create( 2345, 12 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 11875, 12 ), sfa.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "SFA ARB delay", NumFactory.create( 580, 3 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 5875, 6 ), sfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f0 );

				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "SFA ARB delay", NumFactory.create( 2345, 12 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 11875, 12 ), sfa.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "SFA ARB delay", NumFactory.create( 580, 3 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 5875, 6 ), sfa.getBacklogBound() );
				}
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
				
				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "PMOO ARB delay", NumFactory.create( 875, 4 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 4425, 4 ), pmoo.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "PMOO ARB delay", NumFactory.create( 650, 3 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 6575, 6 ), pmoo.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f0 );

				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "PMOO ARB delay", NumFactory.create( 875, 4 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 4425, 4 ), pmoo.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "PMOO ARB delay", NumFactory.create( 650, 3 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 6575, 6 ), pmoo.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f0_sinktree_arbMux()
	{
//		Configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f0.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "Tree Backlog Bound calculation not applicable." );
		}
	}

//--------------------Flow 1--------------------	
	@Test
	public void f1_tfa_fifoMux()
	{		
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f1 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 77.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
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
				tfa.performAnalysis( f1 );

				assertEquals( "TFA FIFO delay", NumFactory.create( 77.5 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f1_tfa_arbMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f1 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 2395, 6 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 395 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f1 );

				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 2395, 6 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 395 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f1_sfa_fifoMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );

		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f1 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA FIFO delay", NumFactory.create( 575, 8 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 2975, 8 ), sfa.getBacklogBound() );
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
				sfa.performAnalysis( f1 );

				assertEquals( "SFA FIFO delay", NumFactory.create( 575, 8 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 2975, 8 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f1_sfa_arbMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				sfa.performAnalysis( f1 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();

				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "SFA ARB delay", NumFactory.create( 2095, 12 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 10625, 12 ), sfa.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "SFA ARB delay", NumFactory.create( 345, 2 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 875 ), sfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f1 );
				
				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "SFA ARB delay", NumFactory.create( 2095, 12 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 10625, 12 ), sfa.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "SFA ARB delay", NumFactory.create( 345, 2 ), sfa.getDelayBound() );
					assertEquals( "SFA ARB backlog", NumFactory.create( 875 ), sfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f1_pmoo_arbMux()
	{
		PmooAnalysis pmoo = new PmooAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				pmoo.performAnalysis( f1 );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
				System.out.println();

				// PMOO AB only
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "PMOO ARB delay", NumFactory.create( 2095, 12 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 10625, 12 ), pmoo.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "PMOO ARB delay", NumFactory.create( 345, 2 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 875 ), pmoo.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f1 );

				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "PMOO ARB delay", NumFactory.create( 2095, 12 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 10625, 12 ), pmoo.getBacklogBound() );
				} else { // PBOO AB
					assertEquals( "PMOO ARB delay", NumFactory.create( 345, 2 ), pmoo.getDelayBound() );
					assertEquals( "PMOO ARB backlog", NumFactory.create( 875 ), pmoo.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f1_sinktree_arbMux()
	{
//		Configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "Tree Backlog Bound calculation not applicable." );
		}
	}
	
//--------------------Flow 2--------------------	
	@Test
	public void f2_tfa_fifoMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f2 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 1875, 32 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 3975, 8 ), tfa.getBacklogBound() );
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
				tfa.performAnalysis( f2 );

				assertEquals( "TFA FIFO delay", NumFactory.create( 1875, 32 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 3975, 8 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f2_tfa_arbMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f2 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA ARB delay", NumFactory.create( 1105, 6 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 2075, 3 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f2 );

				assertEquals( "TFA ARB delay", NumFactory.create( 1105, 6 ), tfa.getDelayBound() );
				assertEquals( "TFA ARB backlog", NumFactory.create( 2075, 3 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f2_sfa_fifoMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );

		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f2 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA FIFO delay", NumFactory.create( 1695, 32 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 8875, 32 ), sfa.getBacklogBound() );
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
				sfa.performAnalysis( f2 );

				assertEquals( "SFA FIFO delay", NumFactory.create( 1695, 32 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 8875, 32 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f2_sfa_arbMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f2 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA ARB delay", NumFactory.create( 1625, 18 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 4175, 9 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f2 );

				assertEquals( "SFA ARB delay", NumFactory.create( 1625, 18 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 4175, 9 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f2_pmoo_arbMux()
	{
		PmooAnalysis pmoo = new PmooAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				pmoo.performAnalysis( f2 );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
				System.out.println();

				assertEquals( "PMOO ARB delay", NumFactory.create( 305, 3 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 3125, 6 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f2 );

				assertEquals( "PMOO ARB delay", NumFactory.create( 305, 3 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 3125, 6 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f2_sinktree_arbMux()
	{
//		Configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f2.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "Tree Backlog Bound calculation not applicable." );
		}
	}
		
//--------------------Flow 3--------------------	
	@Test
	public void f3_tfa_fifoMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f3 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				assertEquals( "TFA FIFO delay", NumFactory.create( 845, 8 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
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
				tfa.performAnalysis( f3 );

				assertEquals( "TFA FIFO delay", NumFactory.create( 845, 8 ), tfa.getDelayBound() );
				assertEquals( "TFA FIFO backlog", NumFactory.create( 975 ), tfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f3_tfa_arbMux()
	{
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				tfa.performAnalysis( f3 );
				System.out.println( "delay bound     : " + tfa.getDelayBound() );
				System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
				System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
				System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
				System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
				System.out.println();
				
				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 1400, 3 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 462.5 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( "TFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				tfa.performAnalysis( f3 );

				if( test_config.arrivalBoundMethods().size() == 1
						&& test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					assertEquals( "TFA ARB delay", NumFactory.create( 1400, 3 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 8525, 6 ), tfa.getBacklogBound() );
				} else {
					assertEquals( "TFA ARB delay", NumFactory.create( 462.5 ), tfa.getDelayBound() );
					assertEquals( "TFA ARB backlog", NumFactory.create( 1400 ), tfa.getBacklogBound() );
				}
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f3_sfa_fifoMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );

		setFifoMux( network.getServers() );

		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f3 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA FIFO delay", NumFactory.create( 1405, 16 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 7225, 16 ), sfa.getBacklogBound() );
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
				sfa.performAnalysis( f3 );

				assertEquals( "SFA FIFO delay", NumFactory.create( 1405, 16 ), sfa.getDelayBound() );
				assertEquals( "SFA FIFO backlog", NumFactory.create( 7225, 16 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				if( !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) ) {
					System.out.println( e.toString() );
					assertEquals( "Unexpected exception occured", 0, 1 );
				}
			}
		}
	}
	
	@Test
	public void f3_sfa_arbMux()
	{
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
	
			try {
				sfa.performAnalysis( f3 );
				System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
				System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
				System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + sfa.getDelayBound() );
				System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
				System.out.println();
				
				assertEquals( "SFA ARB delay", NumFactory.create( 560, 3 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 5675, 6 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "SFA analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				sfa.performAnalysis( f3 );

				assertEquals( "SFA ARB delay", NumFactory.create( 560, 3 ), sfa.getDelayBound() );
				assertEquals( "SFA ARB backlog", NumFactory.create( 5675, 6 ), sfa.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f3_pmoo_arbMux()
	{
		PmooAnalysis pmoo = new PmooAnalysis( network, test_config );
		
		setArbitraryMux( network.getServers() );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			
			try {
				pmoo.performAnalysis( f3 );
				System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
				System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
				System.out.println( "delay bound     : " + pmoo.getDelayBound() );
				System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
				System.out.println();
				
				assertEquals( "PMOO ARB delay", NumFactory.create( 1145, 6 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 2900, 3 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( "PMOO analysis failed" );
				System.out.println( e.toString() );
				System.out.println();
				
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		} else {
			try {
				pmoo.performAnalysis( f3 );

				assertEquals( "PMOO ARB delay", NumFactory.create( 1145, 6 ), pmoo.getDelayBound() );
				assertEquals( "PMOO ARB backlog", NumFactory.create( 2900, 3 ), pmoo.getBacklogBound() );
			} catch (Exception e) {
				System.out.println( e.toString() );
				assertEquals( "Unexpected exception occured", 0, 1 );
			}
		}
	}
	
	@Test
	public void f3_sinktree_arbMux()
	{
//		Configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f3.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "Tree Backlog Bound calculation not applicable." );
		}
	}
}