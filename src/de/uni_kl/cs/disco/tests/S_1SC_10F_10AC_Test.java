/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.uni_kl.cs.disco.nc.Analysis.Analyses;
import de.uni_kl.cs.disco.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.numbers.NumFactory;

@RunWith(value = Parameterized.class)
public class S_1SC_10F_10AC_Test extends DncTests {
    protected static final DncTestResults expected_results = new DncTestResults();
    protected static final DncTestResults expected_results_sinktree = new DncTestResults();
    private static S_1SC_10F_10AC_Network test_network;
    private static Network network;
    private static Flow f0, f6;

    public S_1SC_10F_10AC_Test(DncTestConfig test_config) {
        super(test_config);
    }

    @BeforeClass
    public static void createNetwork() {
        test_network = new S_1SC_10F_10AC_Network();
        f0 = test_network.f0;
        f6 = test_network.f6;

        network = test_network.getNetwork();

        initializeBounds();
    }

    private static void initializeBounds() {
        expected_results.clear();

        // TFA
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, NumFactory.create(15.5), NumFactory.create(110));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f6, NumFactory.create(15.5), NumFactory.create(110));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, NumFactory.create(310, 9), NumFactory.create(110));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f6, NumFactory.create(310, 9), NumFactory.create(110));

        // SFA
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, NumFactory.create(1796, 115), NumFactory.create(127, 50));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f6, NumFactory.create(2099, 130), NumFactory.create(434, 25));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, NumFactory.create(775, 23), NumFactory.create(100, 23));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f6, NumFactory.create(775, 26), NumFactory.create(350, 13));

        // PMOO
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, NumFactory.create(775, 23), NumFactory.create(100, 23));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f6, NumFactory.create(775, 26), NumFactory.create(350, 13));

        // Sink-Tree PMOO at sink
        expected_results_sinktree.clear();

        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, null, NumFactory.create(110));
        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f6, null, NumFactory.create(110));
    }

    @Before
    public void reinitNetwork() {
        if (!super.reinitilize_test) {
            return;
        }

        test_network.reinitializeCurves();
        initializeBounds();
    }

    //--------------------Flow 0--------------------
    @Test
    public void f0_tfa() {
        setMux(network.getServers());
        super.runTFAtest(new TotalFlowAnalysis(network, test_config), f0, expected_results);
    }

    @Test
    public void f0_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f0, expected_results);
    }

    @Test
    public void f0_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f0, expected_results);
    }

    @Test
    public void f0_sinktree_arbMux() {
        setArbitraryMux(network.getServers());
        super.runSinkTreePMOOtest(network, f0, expected_results_sinktree);
    }

    //--------------------Flow 6--------------------
    @Test
    public void f6_tfa() {
        setMux(network.getServers());
        super.runTFAtest(new TotalFlowAnalysis(network, test_config), f6, expected_results);
    }

    @Test
    public void f6_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f6, expected_results);
    }

    @Test
    public void f6_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f6, expected_results);
    }

    @Test
    public void f6_sinktree_arbMux() {
        setArbitraryMux(network.getServers());
        super.runSinkTreePMOOtest(network, f6, expected_results_sinktree);
    }
}
