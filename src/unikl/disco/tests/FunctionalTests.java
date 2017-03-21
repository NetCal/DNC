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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.Analysis.Analyses;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.AnalysisResults;
import unikl.disco.nc.Analysis;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.nc.analyses.PmooAnalysis;
import unikl.disco.nc.analyses.SeparateFlowAnalysis;
import unikl.disco.nc.analyses.TotalFlowAnalysis;
import unikl.disco.nc.operations.BacklogBound;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Server;

@RunWith(Suite.class)
@SuiteClasses({
		Single_1Flow.class,
		Single_2Flows_1AC.class,
		Single_2Flows_2ACs.class,
		Single_10Flows_10ACs.class,
		Tandem_1SC_1Flow.class,
		Tandem_2SCs_1Flow.class,
		Tandem_1SC_2Flows_1AC_1Path.class,
		Tandem_2SCs_2Flows_1ACs_1Path.class,
		Tandem_1SC_2Flows_1AC_1Path_v2.class,
		Tandem_1SC_2Flows_1AC_2Paths.class,
		Tandem_1SC_2Flows_1AC_2Paths_v2.class,
		Tandem_1SC_3Flows_1AC_3Paths.class,
		Tandem_1SC_4Flows_1AC_1Path.class,
		TR_3S_1SC_2F_1AC_2P_Test.class,
		TR_7S_1SC_3F_1AC_3P_Test.class,
		FF_3S_1SC_2F_1AC_2P_Test.class,
		FF_4S_1SC_3F_1AC_3P_Test.class,
		FF_4S_1SC_4F_1AC_4P_Test.class
		})

/**
 * 
 * @author Steffen Bondorf
 *
 */
public class FunctionalTests {
	protected static Collection<FunctionalTestConfig> test_configurations = createParameters();

	protected FunctionalTestConfig test_config;
	protected boolean reinitilize_numbers = true;
	
	public FunctionalTests( FunctionalTestConfig test_config ) {
		this.test_config = test_config;

		if( test_config.enable_checks ) {
			CalculatorConfig.enableAllChecks();
		} else {
			CalculatorConfig.disableAllChecks();
		}
		
		reinitilize_numbers = CalculatorConfig.setNumClass( test_config.numbers );
	}
	
	@Before
	public void printSetting() {
		if ( test_config.console_output ) {
			System.out.println( "--------------------------------------------------------------" );
			System.out.println();
			System.out.println( "Number representation:\t" + test_config.numbers.toString() );
			System.out.println( "Arrival Boundings:\t" + test_config.arrivalBoundMethods().toString() );
			System.out.println( "Remove duplicate ABs:\t" + Boolean.toString( test_config.removeDuplicateArrivalBounds() ) ); 
			System.out.println( "TB,RL convolution:\t" + Boolean.toString( test_config.tbrlConvolution() ) );
			System.out.println( "TB,RL deconvolution:\t" + Boolean.toString( test_config.tbrlDeconvolution() ) );
		}
	}

	public void setMux( Set<Server> servers ) {
		if( !test_config.define_multiplexing_globally ) {
			
			test_config.setMultiplexingDiscipline( MuxDiscipline.SERVER_LOCAL );
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( test_config.mux_discipline );
			}
			
		} else {
			// Enforce potential test failure by defining the server-local multiplexing differently.
			AnalysisConfig.Multiplexing mux_local;
			MuxDiscipline mux_global;
			
			if( test_config.mux_discipline == AnalysisConfig.Multiplexing.ARBITRARY ) {
				mux_global = MuxDiscipline.GLOBAL_ARBITRARY;
				mux_local = AnalysisConfig.Multiplexing.FIFO;
			} else {
				mux_global = MuxDiscipline.GLOBAL_FIFO;
				mux_local = AnalysisConfig.Multiplexing.ARBITRARY;
			}
			
			test_config.setMultiplexingDiscipline( mux_global );
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( mux_local );
			}
		}
	}
	
	public void setFifoMux( Set<Server> servers ) {
		// This is extremely slowing down the tests
//		assumeTrue( "FIFO multiplexing does not allow for PMOO arrival bounding.",
//				!test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) );
		
		if( test_config.define_multiplexing_globally == true ) {
			test_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_FIFO );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( AnalysisConfig.Multiplexing.ARBITRARY );
			}
		} else {
			test_config.setMultiplexingDiscipline( MuxDiscipline.SERVER_LOCAL );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( AnalysisConfig.Multiplexing.FIFO );
			}
		}
	}

	public void setArbitraryMux( Set<Server> servers ) {
		if( test_config.define_multiplexing_globally == true ) {
			test_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( AnalysisConfig.Multiplexing.FIFO );
			}
		} else {
			test_config.setMultiplexingDiscipline( MuxDiscipline.SERVER_LOCAL );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( AnalysisConfig.Multiplexing.ARBITRARY );
			}
		}
	}
	
	private void runAnalysis( Analysis analysis, Flow flow_of_interest ) {
		try {
			analysis.performAnalysis( flow_of_interest );
		} catch (Exception e) {
			e.printStackTrace();
			fail( "Analysis failed" );
		}
	}
	
	protected void runTFAtest( TotalFlowAnalysis tfa, Flow flow_of_interest, FunctionalTestResults expected_bounds ) {
		runAnalysis( tfa, flow_of_interest );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTotal Flow Analysis (TFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + flow_of_interest.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "delay bound     : " + tfa.getDelayBound() );
			System.out.println( "     per server : " + tfa.getServerDelayBoundMapString() );
			System.out.println( "backlog bound   : " + tfa.getBacklogBound() );
			System.out.println( "     per server : " + tfa.getServerBacklogBoundMapString() );
			System.out.println( "alpha per server: " + tfa.getServerAlphasMapString() );
			System.out.println();
		}

		AnalysisResults bounds = expected_bounds.getBounds( Analyses.TFA, test_config.mux_discipline, flow_of_interest );
		assertEquals( "TFA delay", bounds.delay_bound, tfa.getDelayBound() );
		assertEquals( "TFA backlog", bounds.backlog_bound, tfa.getBacklogBound() );
	}
	
	protected void runSFAtest( SeparateFlowAnalysis sfa, Flow flow_of_interest, FunctionalTestResults expected_bounds ) {
		runAnalysis( sfa, flow_of_interest );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tSeparate Flow Analysis (SFA)" );
			System.out.println( "Multiplexing:\t\tFIFO" );
	
			System.out.println( "Flow of interest:\t" + flow_of_interest.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "e2e SFA SCs     : " + sfa.getLeftOverServiceCurves() );
			System.out.println( "     per server : " + sfa.getServerLeftOverBetasMapString() );
			System.out.println( "xtx per server  : " + sfa.getServerAlphasMapString() );
			System.out.println( "delay bound     : " + sfa.getDelayBound() );
			System.out.println( "backlog bound   : " + sfa.getBacklogBound() );
			System.out.println();
		}

		AnalysisResults bounds = expected_bounds.getBounds( Analyses.SFA, test_config.mux_discipline, flow_of_interest );
		assertEquals( "SFA delay", bounds.delay_bound, sfa.getDelayBound() );
		assertEquals( "SFA backlog", bounds.backlog_bound, sfa.getBacklogBound() );
	}
	
	protected void runPMOOtest( PmooAnalysis pmoo, Flow flow_of_interest, FunctionalTestResults expected_bounds ) {
		runAnalysis( pmoo, flow_of_interest );
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tPay Multiplexing Only Once (PMOO)" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + flow_of_interest.toString() );
			System.out.println();
			
			System.out.println( "--- Results: ---" );
			System.out.println( "e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves() );
			System.out.println( "xtx per server  : " + pmoo.getServerAlphasMapString() );
			System.out.println( "delay bound     : " + pmoo.getDelayBound() );
			System.out.println( "backlog bound   : " + pmoo.getBacklogBound() );
			System.out.println();
		}

		AnalysisResults bounds = expected_bounds.getBounds( Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest );
		assertEquals( "PMOO delay", bounds.delay_bound, pmoo.getDelayBound() );
		assertEquals( "PMOO backlog", bounds.backlog_bound, pmoo.getBacklogBound() );
	}
	
	protected void runSinkTreePMOOtest( Network sink_tree, Flow flow_of_interest, FunctionalTestResults expected_bounds ) {
		double backlog_bound = -1.0;
		try {
			backlog_bound = BacklogBound.derivePmooSinkTreeTbRl( sink_tree, flow_of_interest.getSink() );
		} catch (Exception e) {
			e.printStackTrace();
			fail( "Analysis failed" );
		}
		
		if( test_config.fullConsoleOutput() ) {
			System.out.println( "Analysis:\t\tTree Backlog Bound Analysis" );
			System.out.println( "Multiplexing:\t\tArbitrary" );
	
			System.out.println( "Flow of interest:\t" + flow_of_interest.toString() );
			System.out.println();
			
			System.out.println( "--- Result: ---" );

			System.out.println( "backlog bound   : " + Double.toString( backlog_bound ) );
			System.out.println();
		}

		assertEquals( "PMOO backlog", backlog_bound, 
				expected_bounds.getBounds( Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest ).backlog_bound.doubleValue(), 0.0 );
	}
	
	@Parameters(name= "{index}: {0}")
	public static Set<FunctionalTestConfig> createParameters() {
		Set<FunctionalTestConfig> test_configurations = new HashSet<FunctionalTestConfig>();
		
		Set<NumClass> nums =  new HashSet<NumClass>();
		nums.add( NumClass.REAL_DOUBLE_PRECISION );
		nums.add( NumClass.REAL_SINGLE_PRECISION );
		nums.add( NumClass.RATIONAL_INTEGER );
		nums.add( NumClass.RATIONAL_BIGINTEGER );

		
		Set<AnalysisConfig.Multiplexing> mux_disciplines = new HashSet<AnalysisConfig.Multiplexing>();
		mux_disciplines.add( AnalysisConfig.Multiplexing.ARBITRARY );
		mux_disciplines.add( AnalysisConfig.Multiplexing.FIFO );
		
		
		Set<ArrivalBoundMethod> single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add( ArrivalBoundMethod.PBOO_CONCATENATION );

		Set<ArrivalBoundMethod> single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add( ArrivalBoundMethod.PBOO_PER_HOP );
		
		LinkedList<Set<ArrivalBoundMethod>> single_abs_allMux = new LinkedList<Set<ArrivalBoundMethod>>();
		single_abs_allMux.add( single_1 );
		single_abs_allMux.add( single_2 );
		
		LinkedList<Set<ArrivalBoundMethod>> single_abs_arbMux = new LinkedList<Set<ArrivalBoundMethod>>();
		single_abs_arbMux.add( Collections.singleton( ArrivalBoundMethod.PMOO ) );
		
		
		Set<ArrivalBoundMethod> pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add( ArrivalBoundMethod.PBOO_PER_HOP );
		pair_1.add( ArrivalBoundMethod.PBOO_CONCATENATION );

		Set<ArrivalBoundMethod> pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add( ArrivalBoundMethod.PBOO_PER_HOP );
		pair_2.add( ArrivalBoundMethod.PMOO );
		
		Set<ArrivalBoundMethod> pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		pair_3.add( ArrivalBoundMethod.PMOO );

		LinkedList<Set<ArrivalBoundMethod>> pair_abs_allMux = new LinkedList<Set<ArrivalBoundMethod>>();
		pair_abs_allMux.add( pair_1 );
		
		LinkedList<Set<ArrivalBoundMethod>> pair_abs_arbMux = new LinkedList<Set<ArrivalBoundMethod>>();
		pair_abs_arbMux.add( pair_2 );
		pair_abs_arbMux.add( pair_3 );
		
		
		Set<ArrivalBoundMethod> triplet_arbMux = new HashSet<ArrivalBoundMethod>();
		triplet_arbMux.add( ArrivalBoundMethod.PBOO_PER_HOP );
		triplet_arbMux.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		triplet_arbMux.add( ArrivalBoundMethod.PMOO );
		
		
		for( NumClass num : nums ) {
			// Parameter configurations for single arrival bounding tests
			// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, mux, global mux def, number class to use
			for( Set<ArrivalBoundMethod> single_ab : single_abs_allMux ) {
				for( AnalysisConfig.Multiplexing mux : mux_disciplines ) {
					test_configurations.add( new FunctionalTestConfig( single_ab, false, false, false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, false, true, mux,  false, num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  true, mux,  false, num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, false, false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, false, true, mux,  true,  num ) );
					test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  true, mux,  true,  num ) );
				}
			}
			for( Set<ArrivalBoundMethod> single_ab : single_abs_arbMux ) {
				test_configurations.add( new FunctionalTestConfig( single_ab, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( single_ab, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			}
			
			// Parameter configurations for "pairs of arrival boundings"-tests
			// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, mux, global mux def, number class to use
			for( Set<ArrivalBoundMethod> pair_ab : pair_abs_allMux ) {
				for( AnalysisConfig.Multiplexing mux : mux_disciplines ) {
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  false, mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, true,  mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, true,  mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  true,  mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  mux, false, num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  false, mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, true,  mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, true,  mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  true,  mux, true,  num ) );
					test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  mux, true,  num ) );
				}
			}
			for( Set<ArrivalBoundMethod> pair_ab : pair_abs_allMux ) {
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  false, true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
				test_configurations.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			}
			
			// Parameter configurations for "triplets of arrival boundings"-tests
			// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, mux, global mux def, number class to use
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  false, false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  true,  false, AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  false, true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, false, num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, false, false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  false, false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, true,  false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  true,  false, AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, false, true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  false, true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, false, true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
			test_configurations.add( new FunctionalTestConfig( triplet_arbMux, true,  true,  true,  AnalysisConfig.Multiplexing.ARBITRARY, true,  num ) );
		}
		
		return test_configurations;
	}
}
