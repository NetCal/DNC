/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class S_1SC_2F_1AC_Test extends DncTest {
	private static S_1SC_2F_1AC_Network network_factory = new S_1SC_2F_1AC_Network();
	private static Network network = network_factory.getNetwork();
	private Flow f0, f1;

	private S_1SC_2F_1AC_Test() throws Exception {
		super(network_factory, new S_1SC_2F_1AC_Results());
		f0 = network.getFlow(0);
		f1 = network.getFlow(1);
	}

	// --------------------Flow 0--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideAllArguments")
	public void f0_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideAllArguments")
	public void f0_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideArbArguments")
	public void f0_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f0);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideSinkTreeArguments")
	public void f0_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f0); // tests all PMOO Sink Tree variants
	}

	// --------------------Flow 1--------------------
	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideAllArguments")
	public void f1_tfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runTFAtest(new TotalFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideAllArguments")
	public void f1_sfa(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSFAtest(new SeparateFlowAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideArbArguments")
	public void f1_pmoo_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runPMOOtest(new PmooAnalysis(network, test_config), f1);
	}

	@ParameterizedTest(name = "[{arguments}]")
	@MethodSource("de.uni_kl.cs.discodnc.DncTestMethodSources#provideSinkTreeArguments")
	public void f1_sinktree_arbMux(DncTestConfig test_config) {
		initializeTest(test_config);
		setMux(network.getServers());
		runSinkTreePMOOtest(network, f1); // tests all PMOO Sink Tree variants
	}
}
