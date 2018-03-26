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
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.CurveImpl;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig.NumImpl;
import de.uni_kl.cs.discodnc.nc.CurveImpl_DNC;
//import CalculatorConfig.OperationImpl;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

public class DncTestArguments implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
		return createParameters().stream().map(Arguments::of);
	}

	private static Set<DncTestConfig> createParameters() {
		Set<DncTestConfig> test_configurations = new HashSet<DncTestConfig>();

		Set<NumImpl> nums = new HashSet<NumImpl>();
		nums.add(NumImpl.REAL_DOUBLE_PRECISION);
		nums.add(NumImpl.REAL_SINGLE_PRECISION);
		nums.add(NumImpl.RATIONAL_INTEGER);
		nums.add(NumImpl.RATIONAL_BIGINTEGER);

		Set<CurveImpl> curves = new HashSet<CurveImpl>();
		curves.add(CurveImpl_DNC.DNC);
		
//		Set<OperationImpl> operations = new HashSet<OperationImpl>();
//		operations.add(OperationImpl.DNC);
//		operations.add(OperationImpl.NATIVE);

		Set<AnalysisConfig.Multiplexing> mux_disciplines = new HashSet<AnalysisConfig.Multiplexing>();
		mux_disciplines.add(AnalysisConfig.Multiplexing.ARBITRARY);
		mux_disciplines.add(AnalysisConfig.Multiplexing.FIFO);

		Set<ArrivalBoundMethod> single_1 = new HashSet<ArrivalBoundMethod>();
		single_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		Set<ArrivalBoundMethod> single_2 = new HashSet<ArrivalBoundMethod>();
		single_2.add(ArrivalBoundMethod.PBOO_PER_HOP);

		LinkedList<Set<ArrivalBoundMethod>> single_abs_allMux = new LinkedList<Set<ArrivalBoundMethod>>();
		single_abs_allMux.add(single_1);
		single_abs_allMux.add(single_2);

		LinkedList<Set<ArrivalBoundMethod>> single_abs_arbMux = new LinkedList<Set<ArrivalBoundMethod>>();
		single_abs_arbMux.add(Collections.singleton(ArrivalBoundMethod.PMOO));

		Set<ArrivalBoundMethod> pair_1 = new HashSet<ArrivalBoundMethod>();
		pair_1.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_1.add(ArrivalBoundMethod.PBOO_CONCATENATION);

		Set<ArrivalBoundMethod> pair_2 = new HashSet<ArrivalBoundMethod>();
		pair_2.add(ArrivalBoundMethod.PBOO_PER_HOP);
		pair_2.add(ArrivalBoundMethod.PMOO);

		Set<ArrivalBoundMethod> pair_3 = new HashSet<ArrivalBoundMethod>();
		pair_3.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		pair_3.add(ArrivalBoundMethod.PMOO);

		LinkedList<Set<ArrivalBoundMethod>> pair_abs_allMux = new LinkedList<Set<ArrivalBoundMethod>>();
		pair_abs_allMux.add(pair_1);

		LinkedList<Set<ArrivalBoundMethod>> pair_abs_arbMux = new LinkedList<Set<ArrivalBoundMethod>>();
		pair_abs_arbMux.add(pair_2);
		pair_abs_arbMux.add(pair_3);

		Set<ArrivalBoundMethod> triplet_arbMux = new HashSet<ArrivalBoundMethod>();
		triplet_arbMux.add(ArrivalBoundMethod.PBOO_PER_HOP);
		triplet_arbMux.add(ArrivalBoundMethod.PBOO_CONCATENATION);
		triplet_arbMux.add(ArrivalBoundMethod.PMOO);

		// Parameter configurations for single arrival bounding tests
		// AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, mux,
		// global mux def, number class to use, curve class to use, operations class to
		// use
		for (CurveImpl curve : curves) {
			for (NumImpl num : nums) {
				for (Set<ArrivalBoundMethod> single_ab : single_abs_allMux) {
					for (AnalysisConfig.Multiplexing mux : mux_disciplines) {
						test_configurations
								.add(new DncTestConfig(single_ab, false, false, false, mux, false, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, true, false, mux, false, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, false, true, mux, false, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, true, true, mux, false, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, false, false, mux, true, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, true, false, mux, true, num, curve));
						test_configurations
								.add(new DncTestConfig(single_ab, false, false, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(single_ab, false, true, true, mux, true, num, curve));
					}
				}
				
				for (Set<ArrivalBoundMethod> single_ab : single_abs_arbMux) {
					test_configurations.add(new DncTestConfig(single_ab, false, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(single_ab, false, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				}

				for (Set<ArrivalBoundMethod> pair_ab : pair_abs_allMux) {
					for (AnalysisConfig.Multiplexing mux : mux_disciplines) {
						test_configurations
								.add(new DncTestConfig(pair_ab, false, false, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, false, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, true, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, true, false, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, false, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, false, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, true, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, true, true, mux, false, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, false, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, false, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, true, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, true, false, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, false, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, false, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, false, true, true, mux, true, num, curve));
						test_configurations.add(new DncTestConfig(pair_ab, true, true, true, mux, true, num, curve));
					}
				}

				for (Set<ArrivalBoundMethod> pair_ab : pair_abs_arbMux) {
					test_configurations.add(new DncTestConfig(pair_ab, false, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, false, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, true, false,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, false, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, false, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
					test_configurations.add(new DncTestConfig(pair_ab, true, true, true,
							AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				}

				test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, false,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, false,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, false,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, false,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, true,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, true,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, true,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, true,
						AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, false,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, false,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, false,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, false,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, true,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, true,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, true,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
				test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, true,
						AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve));
			}
		}

		return test_configurations;
	}
}
