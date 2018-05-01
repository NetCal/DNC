/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2017 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.CurveImpl;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.NumImpl;
//import CalculatorConfig.OperationImpl;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class DncTestMethodSources {

	protected static Set<Set<ArrivalBoundMethod>> ab_sets = instantiateABsets();
	protected static Set<ArrivalBoundMethod> single_1, single_2, single_3, pair_1, pair_2, pair_3, triplet, sinktree;

	private static Set<Set<ArrivalBoundMethod>> instantiateABsets() {
		single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add(ArrivalBoundMethod.PBOO_PER_HOP);

		single_3 = new HashSet<ArrivalBoundMethod>();
		single_3.add(ArrivalBoundMethod.PMOO);

		pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_2.add(ArrivalBoundMethod.PMOO);

		pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		pair_3.add(ArrivalBoundMethod.PMOO);

		triplet = new HashSet<ArrivalBoundMethod>();
		triplet.add(ArrivalBoundMethod.PBOO_PER_HOP);
		triplet.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		triplet.add(ArrivalBoundMethod.PMOO);
		
		Set<Set<ArrivalBoundMethod>> ab_sets = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets.add(single_1);
		ab_sets.add(single_2);
		ab_sets.add(single_3);
		ab_sets.add(pair_1);
		ab_sets.add(pair_2);
		ab_sets.add(pair_3);
		ab_sets.add(triplet);

		// sink tree bounds are not added to ab_sets as the tests treat them differently. 
		sinktree = new HashSet<ArrivalBoundMethod>();
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV);
		sinktree.add(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO);
		
		return ab_sets;
	}

	protected static Stream<Arguments> provideAllArguments() {
		return Stream.concat(provideArbArguments(), provideFifoArguments());
	}
	
	protected static Stream<Arguments> provideArbArguments() {
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideFifoArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_fifo = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_fifo.add(single_1);
		ab_sets_fifo.add(single_2);
		ab_sets_fifo.add(pair_1);
		
		return createParameters(Collections.singleton(Multiplexing.FIFO), ab_sets_fifo).stream().map(Arguments::of);
	}
	
	protected static Stream<Arguments> provideSinkTreeArguments() {
		Set<Set<ArrivalBoundMethod>> ab_sets_sinktree = new HashSet<Set<ArrivalBoundMethod>>();
		ab_sets_sinktree.add(sinktree); // TODO Test powerset of sinktree alternatives
		
		return createParameters(Collections.singleton(Multiplexing.ARBITRARY), ab_sets_sinktree).stream().map(Arguments::of);
	}

	private static Set<DncTestConfig> createParameters(Set<Multiplexing> mux_disciplines, Set<Set<ArrivalBoundMethod>> ab_sets) {
		Set<DncTestConfig> test_configurations = new HashSet<DncTestConfig>();

		Set<NumImpl> nums = new HashSet<NumImpl>();
		nums.add(NumImpl.REAL_DOUBLE_PRECISION);
		nums.add(NumImpl.REAL_SINGLE_PRECISION);
		nums.add(NumImpl.RATIONAL_INTEGER);
		nums.add(NumImpl.RATIONAL_BIGINTEGER);

		Set<CurveImpl> curves = new HashSet<CurveImpl>();
		curves.add(CurveImpl.DNC);
		curves.add(CurveImpl.MPA_RTC);
		
//		Set<OperationImpl> operations = new HashSet<OperationImpl>();
//		operations.add(OperationImpl.DNC);
//		operations.add(OperationImpl.NATIVE);

		// Parameter configurations for single arrival bounding tests
		// 		AB, convolve alternative ABs, tbrl opt convolution, tbrl opt deconvolution, mux,
		// global mux def, number class to use, curve class to use, operations class to use.
		for (CurveImpl curve : curves) {
			for (NumImpl num : nums) {
				for (Set<ArrivalBoundMethod> ab : ab_sets) {
					for (Multiplexing mux : mux_disciplines) {
						test_configurations.add(new DncTestConfig(ab, false, false, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, false, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, true, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, true, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, false, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, false, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, true, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, true, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, false, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, false, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, true, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, true, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, false, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, false, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, false, true, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(ab, true, true, true, mux, true, num, curve));
					}
				}
			}
		}

		return test_configurations;
	}
}