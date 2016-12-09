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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.nc.CalculatorConfig.NumClass;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;

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
		Tree_1SC_2Flows_1AC_2Paths.class,
		Tree_1SC_3Flows_1AC_3Paths.class,
		FeedForward_1SC_2Flows_1AC_2Paths.class,
		FeedForward_1SC_3Flows_1AC_3Paths.class,
		FeedForward_1SC_4Flows_1AC_4Paths.class
		})
/**
 * 
 * @author Steffen Bondorf
 *
 */
public class FunctionalTests { // Cannot make this class static as that prevents it from starting the tests listed above.
	protected static Collection<FunctionalTestConfig> test_configurations = createParameters();

	protected FunctionalTestConfig test_config;
	
	public FunctionalTests( FunctionalTestConfig test_config ) {
		this.test_config = test_config;

		if( test_config.enable_checks ) {
			CalculatorConfig.enableAllChecks();
		} else {
			CalculatorConfig.disableAllChecks();
		}
		
		CalculatorConfig.NUM_CLASS = test_config.numbers;
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
	
	public void setFifoMux( Set<Server> servers ) {
		if( test_config.define_multiplexing_globally == true ) {
			test_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_FIFO );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( Multiplexing.ARBITRARY );
			}
		} else {
			test_config.setMultiplexingDiscipline( MuxDiscipline.SERVER_LOCAL );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( Multiplexing.FIFO );
			}
		}
	}

	public void setArbitraryMux( Set<Server> servers ) {
		if( test_config.define_multiplexing_globally == true ) {
			test_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( Multiplexing.FIFO );
			}
		} else {
			test_config.setMultiplexingDiscipline( MuxDiscipline.SERVER_LOCAL );
			// Enforce potential test failure
			for( Server s : servers ) {
				s.setMultiplexingDiscipline( Multiplexing.ARBITRARY );
			}
		}
	}

	@Parameters(name= "{index}: {0}")
	public static Set<FunctionalTestConfig> createParameters(){
		Set<FunctionalTestConfig> result = new HashSet<FunctionalTestConfig>();
		
		Set<NumClass> nums =  new HashSet<NumClass>();
		nums.add( NumClass.DOUBLE_PRECISION );
		nums.add( NumClass.SINGLE_PRECISION );
		nums.add( NumClass.FRACTION_INTEGER );
//		nums.add( NumClass.BIG_FRACTION );
		
		
		Set<ArrivalBoundMethod> single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add( ArrivalBoundMethod.PBOO_CONCATENATION );

		Set<ArrivalBoundMethod> single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add( ArrivalBoundMethod.PBOO_PER_HOP );
		
		Set<ArrivalBoundMethod> single_3 = new HashSet<ArrivalBoundMethod>();
		single_3.add( ArrivalBoundMethod.PMOO );
		
		LinkedList<Set<ArrivalBoundMethod>> single_abs = new LinkedList<Set<ArrivalBoundMethod>>();
		single_abs.add( single_1 );
		single_abs.add( single_2 );
		single_abs.add( single_3 );
		
		// Parameter configurations for single arrival bounding tests
		// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, global mux def, number class to use
		for( Set<ArrivalBoundMethod> single_ab : single_abs ) {
			for( NumClass num : nums ) {
				result.add( new FunctionalTestConfig( single_ab, false, false, false, false, num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, true,  false, false, num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, false, true,  false, num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, true,  true,  false, num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, false, false, true,  num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, true,  false, true,  num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, false, true,  true,  num ) ) ;
				result.add( new FunctionalTestConfig( single_ab, false, true,  true,  true,  num ) ) ;
			}
		}
		
		
		
		Set<ArrivalBoundMethod> pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add( ArrivalBoundMethod.PBOO_PER_HOP );
		pair_1.add( ArrivalBoundMethod.PBOO_CONCATENATION );

		Set<ArrivalBoundMethod> pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add( ArrivalBoundMethod.PBOO_PER_HOP );
		pair_2.add( ArrivalBoundMethod.PMOO );
		
		Set<ArrivalBoundMethod> pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		pair_3.add( ArrivalBoundMethod.PMOO );

		LinkedList<Set<ArrivalBoundMethod>> pair_abs = new LinkedList<Set<ArrivalBoundMethod>>();
		pair_abs.add( pair_1 );
		pair_abs.add( pair_2 );
		pair_abs.add( pair_3 );

		// Parameter configurations for "pairs of arrival boundings"-tests
		// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, global mux def, number class to use
		for( Set<ArrivalBoundMethod> pair_ab : pair_abs ) {
			for( NumClass num : nums ) {
				result.add( new FunctionalTestConfig( pair_ab, false, false, false, false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  false, false, false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, true,  false, false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  true,  false, false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, false, true,  false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  false, true,  false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, true,  true,  false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  false, num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, false, false, true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  false, false, true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, true,  false, true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  true,  false, true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, false, true,  true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  false, true,  true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, false, true,  true,  true,  num ) );
				result.add( new FunctionalTestConfig( pair_ab, true,  true,  true,  true,  num ) );
			}
		}
		
		
		
		Set<ArrivalBoundMethod> triplet = new HashSet<ArrivalBoundMethod>();
		triplet.add( ArrivalBoundMethod.PBOO_PER_HOP );
		triplet.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		triplet.add( ArrivalBoundMethod.PMOO );
		
		// Parameter configurations for "triplets of arrival boundings"-tests
		// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, global mux def, number class to use
		for( NumClass num : nums ) {
			result.add( new FunctionalTestConfig( triplet, false, false, false, false, num ) );
			result.add( new FunctionalTestConfig( triplet, true,  false, false, false, num ) );
			result.add( new FunctionalTestConfig( triplet, false, true,  false, false, num ) );
			result.add( new FunctionalTestConfig( triplet, true,  true,  false, false, num ) );
			result.add( new FunctionalTestConfig( triplet, false, false, true,  false, num ) );
			result.add( new FunctionalTestConfig( triplet, true,  false, true,  false, num ) );
			result.add( new FunctionalTestConfig( triplet, false, true,  true,  false, num ) );
			result.add( new FunctionalTestConfig( triplet, true,  true,  true,  false, num ) );
			result.add( new FunctionalTestConfig( triplet, false, false, false, true,  num ) );
			result.add( new FunctionalTestConfig( triplet, true,  false, false, true,  num ) );
			result.add( new FunctionalTestConfig( triplet, false, true,  false, true,  num ) );
			result.add( new FunctionalTestConfig( triplet, true,  true,  false, true,  num ) );
			result.add( new FunctionalTestConfig( triplet, false, false, true,  true,  num ) );
			result.add( new FunctionalTestConfig( triplet, true,  false, true,  true,  num ) );
			result.add( new FunctionalTestConfig( triplet, false, true,  true,  true,  num ) );
			result.add( new FunctionalTestConfig( triplet, true,  true,  true,  true,  num ) );
		}
		
		return result;
	}
}
