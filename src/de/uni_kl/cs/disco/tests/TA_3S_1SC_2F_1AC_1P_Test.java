/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
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

package de.uni_kl.cs.disco.tests;

import de.uni_kl.cs.disco.nc.Analysis.Analyses;
import de.uni_kl.cs.disco.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.numbers.Num;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.security.ProtectionDomain;

public class TA_3S_1SC_2F_1AC_1P_Test extends DncTest {
    protected static final DncTestResults expected_results = new DncTestResults();
    protected static final DncTestResults expected_results_sinktree = new DncTestResults();
    private static TA_3S_1SC_2F_1AC_1P_Network test_network;
    private static Network network;
    private static Flow f0, f1;

    public TA_3S_1SC_2F_1AC_1P_Test(DncTestConfig test_config) throws Exception {
    }

    @BeforeAll
    public void createNetwork() {
        test_network = new TA_3S_1SC_2F_1AC_1P_Network();
        f0 = test_network.f0;
        f1 = test_network.f1;

        network = test_network.getNetwork();

        initializeBounds();
    }

    @Override
    protected void initializeBounds() {
        expected_results.clear();

        Num factory = Num.getFactory();

        // TFA
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, factory.create(97.5), factory.create(650));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f1, factory.create(97.5), factory.create(650));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, factory.create(195), factory.create(650));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, factory.create(195), factory.create(650));

        // SFA
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, factory.create(965, 12),
                factory.create(1675, 4));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f1, factory.create(965, 12),
                factory.create(1675, 4));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, factory.create(320, 3),
                factory.create(550));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, factory.create(320, 3),
                factory.create(550));

        // PMOO
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, factory.create(250, 3),
                factory.create(1300, 3));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, factory.create(250, 3),
                factory.create(1300, 3));

        // Sink-Tree PMOO at sink
        expected_results_sinktree.clear();

        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, null, factory.create(650));
        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, null, factory.create(650));
    }

    @BeforeEach
    public void reinitNetwork() {
        if (!super.reinitilize_test) {
            return;
        }

        test_network.reinitializeCurves();
        initializeBounds();
    }

    // --------------------Flow 0--------------------

    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f0_tfa() {
        setMux(network.getServers());
        super.runTFAtest(new TotalFlowAnalysis(network, test_config), f0);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f0_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f0);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f0_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f0);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f0_sinktree_arbMux() {
        setArbitraryMux(network.getServers());
        super.runSinkTreePMOOtest(network, f0);
    }

    // --------------------Flow 1--------------------

    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f1_tfa() {
        setMux(network.getServers());
        super.runTFAtest(new TotalFlowAnalysis(network, test_config), f1);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f1_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f1);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f1_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f1);
    }


    @ParameterizedTest(name = "[{arguments}]")
    @ArgumentsSource(DncTestArguments.class)
    public void f1_sinktree_arbMux() {
        setArbitraryMux(network.getServers());
        super.runSinkTreePMOOtest(network, f1);
    }
}