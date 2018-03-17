/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
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

import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.numbers.Num;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class TA_3S_1SC_3F_1AC_3P_Test extends DncTest {
	private Flow f0, f1, f2;

	private TA_3S_1SC_3F_1AC_3P_Test() {
		super(new TA_3S_1SC_3F_1AC_3P_Network());
	}

	protected void initializeFlows() {
		f0 = ((TA_3S_1SC_3F_1AC_3P_Network) network_factory).f0;
		f1 = ((TA_3S_1SC_3F_1AC_3P_Network) network_factory).f1;
		f2 = ((TA_3S_1SC_3F_1AC_3P_Network) network_factory).f2;
	}

	protected void initializeBounds() {
		expected_results.clear();

		Num factory = Num.getFactory();

		// TFA
		expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, factory.create(55), factory.create(450));
		expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f1, factory.create(2205, 64),
				factory.create(7825, 16));
		expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f2, factory.create(5725, 64),
				factory.create(7825, 16));
		expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, factory.create(110), factory.create(450));
		expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, factory.create(1405, 18),
				factory.create(5225, 9));
		expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f2, factory.create(16925, 90),
				factory.create(5225, 9));

		// SFA
		expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, factory.create(295, 6), factory.create(262.5));
		expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f1, factory.create(6695, 192),
				factory.create(12225, 64));
		expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f2, factory.create(845, 12),
				factory.create(1475, 4));
		expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, factory.create(65),
				factory.create(1025, 3));
		expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, factory.create(1405, 27),
				factory.create(7475, 27));
		expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f2, factory.create(280, 3),
				factory.create(1450, 3));

		// PMOO
		expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, factory.create(170, 3),
				factory.create(300));
		expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, factory.create(1405, 27),
				factory.create(7475, 27));
		expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f2, factory.create(85),
				factory.create(1325, 3));

		// PMOO Arrival Bounding may yield cross-traffic arrivals!
		if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
			// TFA
			expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, factory.create(72.5),
					factory.create(525));
			expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f2, factory.create(182.5),
					factory.create(525));

			// SFA
			expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, factory.create(145, 3),
					factory.create(775, 3));

			// PMOO
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, factory.create(145, 3),
					factory.create(775, 3));
		}

		// Sink-Tree PMOO at sink
		if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO)) {
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, null, factory.create(450));
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, null, factory.create(1375));
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f2, null, factory.create(1375));
		}
	}

	// --------------------Flow 0--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f0_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f0_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f0_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f0_sinktree_arbMux(DncTestConfig test_config) {
		test_config.setArrivalBoundMethod(ArrivalBoundMethod.PMOO_SINKTREE_TBRL);
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runSinkTreePMOOtest(network, f0);
	}

	// --------------------Flow 1--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f1_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f1_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f1_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f1_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
			System.out.println("Multiplexing:\t\tArbitrary");

			System.out.println("Flow of interest:\t" + f1.toString());
			System.out.println();

			System.out.println("--- Results: ---");
			System.out.println("Tree Backlog Bound calculation not applicable.");
		}
	}

	// --------------------Flow 2--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f2_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f2_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f2_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f2);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f2_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		if (test_config.fullConsoleOutput()) {
			System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
			System.out.println("Multiplexing:\t\tArbitrary");

			System.out.println("Flow of interest:\t" + f2.toString());
			System.out.println();

			System.out.println("--- Results: ---");
			System.out.println("Tree Backlog Bound calculation not applicable.");
		}
	}
}