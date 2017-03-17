/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import unikl.disco.nc.AnalysisConfig.Multiplexing;
import unikl.disco.nc.AnalysisResults;
import unikl.disco.nc.Analysis.Analyses;
import unikl.disco.nc.analyses.PmooAnalysis;
import unikl.disco.nc.analyses.SeparateFlowAnalysis;
import unikl.disco.nc.analyses.TotalFlowAnalysis;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.numbers.NumFactory;

@RunWith(value = Parameterized.class)
/**
 * 
 * @author Steffen Bondorf
 *
 */
public class FF_3S_1SC_2F_1AC_2P_Test extends FunctionalTests {
	private static FF_3S_1SC_2F_1AC_2P_Network test_network;
	private static Network network;
	private static Flow f0, f1;

	protected static TestResults expected_results = new TestResults();

	public FF_3S_1SC_2F_1AC_2P_Test( FunctionalTestConfig test_config ) throws Exception {
		super( test_config );
	}
	
	@BeforeClass
	public static void createNetwork() {
		test_network = new FF_3S_1SC_2F_1AC_2P_Network();
		f0 = test_network.f0;
		f1 = test_network.f1;

		network = test_network.getNetwork();
		
		initializeBounds();
	}

	private static void initializeBounds() {
		expected_results.clear();
		
		// TFA
		expected_results.setBounds( Analyses.TFA, Multiplexing.FIFO, f0, new AnalysisResults( NumFactory.create( 485, 8 ), NumFactory.create( 1125, 2 ), null ) );
		expected_results.setBounds( Analyses.TFA, Multiplexing.FIFO, f1, new AnalysisResults( NumFactory.create( 1395, 16 ), NumFactory.create( 1125, 2 ), null ) );
		expected_results.setBounds( Analyses.TFA, Multiplexing.ARBITRARY, f0, new AnalysisResults( NumFactory.create( 385, 3 ), NumFactory.create( 1900, 3 ), null ) );
		expected_results.setBounds( Analyses.TFA, Multiplexing.ARBITRARY, f1, new AnalysisResults( NumFactory.create( 470, 3 ), NumFactory.create( 1900, 3 ), null ) );

		// SFA
		expected_results.setBounds( Analyses.SFA, Multiplexing.FIFO, f0, new AnalysisResults( NumFactory.create( 2615, 48 ), NumFactory.create( 4625, 16 ), null ) );
		expected_results.setBounds( Analyses.SFA, Multiplexing.FIFO, f1, new AnalysisResults( NumFactory.create( 3335, 48 ), NumFactory.create( 5825, 16 ), null ) );
		expected_results.setBounds( Analyses.SFA, Multiplexing.ARBITRARY, f0, new AnalysisResults( NumFactory.create( 670, 9 ), NumFactory.create( 3500, 9 ), null ) );
		expected_results.setBounds( Analyses.SFA, Multiplexing.ARBITRARY, f1, new AnalysisResults( NumFactory.create( 790, 9 ), NumFactory.create( 4100, 9 ), null ) );

		// PMOO
		expected_results.setBounds( Analyses.PMOO, Multiplexing.ARBITRARY, f0, new AnalysisResults( NumFactory.create( 670, 9 ), NumFactory.create( 3500, 9 ), null ) );
		expected_results.setBounds( Analyses.PMOO, Multiplexing.ARBITRARY, f1, new AnalysisResults( NumFactory.create( 790, 9 ), NumFactory.create( 4100, 9 ), null ) );
	}
	
	@Before
    public void reinitNetwork() {
		if( !super.reinitilize_numbers ){
			return;
		}
		
		test_network.reinitializeCurves();
		initializeBounds();
	}
	
//--------------------Flow 0--------------------
	@Test
	public void f0_tfa() {
		setMux( network.getServers() );
		super.runTFAtest( new TotalFlowAnalysis( network, test_config ), f0, expected_results );
	}
	
	@Test
	public void f0_sfa() {
		setMux( network.getServers() );
		super.runSFAtest( new SeparateFlowAnalysis( network, test_config ), f0, expected_results );
	}
	
	@Test
	public void f0_pmoo_arbMux() {
		setArbitraryMux( network.getServers() );
		super.runPMOOtest( new PmooAnalysis( network, test_config ), f0, expected_results );
	}
	
	@Test
	public void f0_sinktree_arbMux() {
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
	public void f1_tfa() {
		setMux( network.getServers() );
		super.runTFAtest( new TotalFlowAnalysis( network, test_config ), f1, expected_results );
	}

	@Test
	public void f1_sfa() {
		setMux( network.getServers() );
		super.runSFAtest( new SeparateFlowAnalysis( network, test_config ), f1, expected_results );
	}
	
	@Test
	public void f1_pmoo_arbMux() {
		setArbitraryMux( network.getServers() );
		super.runPMOOtest( new PmooAnalysis( network, test_config ), f1, expected_results );
	}
	
	@Test
	public void f1_sinktree_arbMux() {
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + f1.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "Tree Backlog Bound calculation not applicable." );
		}
	}
}