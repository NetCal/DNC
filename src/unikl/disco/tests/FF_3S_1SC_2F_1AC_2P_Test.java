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

import unikl.disco.nc.analyses.PmooAnalysis;
import unikl.disco.nc.analyses.SeparateFlowAnalysis;
import unikl.disco.nc.analyses.TotalFlowAnalysis;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;

@RunWith(value = Parameterized.class)
/**
 * 
 * @author Steffen Bondorf
 *
 */
public class FF_3S_1SC_2F_1AC_2P_Test extends FunctionalTests
{
	private static Network network;
	private static Flow f0, f1;

	public FF_3S_1SC_2F_1AC_2P_Test( FunctionalTestConfig test_config ) throws Exception {
		super( test_config );
	}
	
	@BeforeClass
	public static void createNetwork() {
		network = FF_3S_1SC_2F_1AC_2P_Net.createNetwork();
		f0 = FF_3S_1SC_2F_1AC_2P_Net.f0;
		f1 = FF_3S_1SC_2F_1AC_2P_Net.f1;
	}
	
	// We need to reinitialize the network in order to react to changes of the number representation.
	@Before
    public void reinitNetwork() {
		FF_3S_1SC_2F_1AC_2P_Net.reinitializeCurves();
		FF_3S_1SC_2F_1AC_2P_Net.initializeExpectedTestResults();
    }
	
//--------------------Flow 0--------------------
	@Test
	public void f0_tfa() {
		setMux( network.getServers() );
		super.runTFAtest( new TotalFlowAnalysis( network, test_config ), f0, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}
	
	@Test
	public void f0_sfa() {
		setMux( network.getServers() );
		super.runSFAtest( new SeparateFlowAnalysis( network, test_config ), f0, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}
	
	@Test
	public void f0_pmoo_arbMux() {
		setArbitraryMux( network.getServers() );
		super.runPMOOtest( new PmooAnalysis( network, test_config ), f0, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}
	
	@Test
	public void f0_sinktree_arbMux()
	{
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
		super.runTFAtest( new TotalFlowAnalysis( network, test_config ), f1, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}

	@Test
	public void f1_sfa() {
		setMux( network.getServers() );
		super.runSFAtest( new SeparateFlowAnalysis( network, test_config ), f1, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}
	
	@Test
	public void f1_pmoo_arbMux() {
		setArbitraryMux( network.getServers() );
		super.runPMOOtest( new PmooAnalysis( network, test_config ), f1, FF_3S_1SC_2F_1AC_2P_Net.expected_results );
	}
	
	@Test
	public void f1_sinktree_arbMux()
	{
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