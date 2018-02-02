package de.uni_kl.cs.discodnc;/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

public class S_1SC_10F_10AC_Test extends DncTest {
	private Flow f0, f6;

	private S_1SC_10F_10AC_Test() {
		super(new S_1SC_10F_10AC_Network());
	}

	protected void initializeFlows() {
		f0 = ((S_1SC_10F_10AC_Network) network_factory).f0;
		f6 = ((S_1SC_10F_10AC_Network) network_factory).f6;
	}

	protected void initializeBounds() {
		expected_results.clear();

		Num factory = Num.getFactory();

		// TFA
		expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, factory.create(15.5), factory.create(110));
		expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f6, factory.create(15.5), factory.create(110));
		expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, factory.create(310, 9),
				factory.create(110));
		expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f6, factory.create(310, 9),
				factory.create(110));

		// SFA
		expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, factory.create(1796, 115),
				factory.create(127, 50));
		expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f6, factory.create(2099, 130),
				factory.create(434, 25));
		expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, factory.create(775, 23),
				factory.create(100, 23));
		expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f6, factory.create(775, 26),
				factory.create(350, 13));

		// PMOO
		expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, factory.create(775, 23),
				factory.create(100, 23));
		expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f6, factory.create(775, 26),
				factory.create(350, 13));

		// Sink-Tree PMOO at sink
		if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV)
				|| test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO)) {
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, null, factory.create(110));
			expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f6, null, factory.create(110));
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

	// --------------------Flow 6--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f6_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f6);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f6_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f6);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f6_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f6);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@ArgumentsSource(DncTestArguments.class)
	public void f6_sinktree_arbMux(DncTestConfig test_config) {
		test_config.setArrivalBoundMethod(ArrivalBoundMethod.PMOO_SINKTREE_TBRL);
		initializeTest(test_config);
		setArbitraryMux(network.getServers());
		runSinkTreePMOOtest(network, f6);
	}
}
