/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
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
import de.uni_kl.cs.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.disco.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.numbers.NumFactoryDispatch;

@RunWith(value = Parameterized.class)
public class TA_3S_1SC_3F_1AC_3P_Test extends DncTests {
    protected static final DncTestResults expected_results = new DncTestResults();
    protected static final DncTestResults expected_results_PMOOAB = new DncTestResults();
    protected static final DncTestResults expected_results_sinktree = new DncTestResults();
    private static TA_3S_1SC_3F_1AC_3P_Network test_network;
    private static Network network;
    private static Flow f0, f1, f2;

    public TA_3S_1SC_3F_1AC_3P_Test(DncTestConfig test_config) throws Exception {
        super(test_config);
    }

    @BeforeClass
    public static void createNetwork() {
        test_network = new TA_3S_1SC_3F_1AC_3P_Network();
        f0 = test_network.f0;
        f1 = test_network.f1;
        f2 = test_network.f2;

        network = test_network.getNetwork();

        initializeBounds();
    }

    private static void initializeBounds() {
        expected_results.clear();

        // TFA
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f0, NumFactoryDispatch.create(55), NumFactoryDispatch.create(450));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f1, NumFactoryDispatch.create(2205, 64), NumFactoryDispatch.create(7825, 16));
        expected_results.setBounds(Analyses.TFA, Multiplexing.FIFO, f2, NumFactoryDispatch.create(5725, 64), NumFactoryDispatch.create(7825, 16));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f0, NumFactoryDispatch.create(110), NumFactoryDispatch.create(450));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(1405, 18), NumFactoryDispatch.create(5225, 9));
        expected_results.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f2, NumFactoryDispatch.create(16925, 90), NumFactoryDispatch.create(5225, 9));

        // SFA
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f0, NumFactoryDispatch.create(295, 6), NumFactoryDispatch.create(262.5));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f1, NumFactoryDispatch.create(6695, 192), NumFactoryDispatch.create(12225, 64));
        expected_results.setBounds(Analyses.SFA, Multiplexing.FIFO, f2, NumFactoryDispatch.create(845, 12), NumFactoryDispatch.create(1475, 4));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f0, NumFactoryDispatch.create(65), NumFactoryDispatch.create(1025, 3));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(1405, 27), NumFactoryDispatch.create(7475, 27));
        expected_results.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f2, NumFactoryDispatch.create(280, 3), NumFactoryDispatch.create(1450, 3));

        // PMOO
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, NumFactoryDispatch.create(170, 3), NumFactoryDispatch.create(300));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(1405, 27), NumFactoryDispatch.create(7475, 27));
        expected_results.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f2, NumFactoryDispatch.create(85), NumFactoryDispatch.create(1325, 3));


        // PMOO Arrival Bounding may yield cross-traffic arrivals!
        expected_results_PMOOAB.clear();

        // TFA
        expected_results_PMOOAB.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(72.5), NumFactoryDispatch.create(525));
        expected_results_PMOOAB.setBounds(Analyses.TFA, Multiplexing.ARBITRARY, f2, NumFactoryDispatch.create(182.5), NumFactoryDispatch.create(525));

        // SFA
        expected_results_PMOOAB.setBounds(Analyses.SFA, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(145, 3), NumFactoryDispatch.create(775, 3));

        // PMOO
        expected_results_PMOOAB.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, NumFactoryDispatch.create(145, 3), NumFactoryDispatch.create(775, 3));


        // Sink-Tree PMOO at sink
        expected_results_sinktree.clear();

        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f0, null, NumFactoryDispatch.create(450));
        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f1, null, NumFactoryDispatch.create(1375));
        expected_results_sinktree.setBounds(Analyses.PMOO, Multiplexing.ARBITRARY, f2, null, NumFactoryDispatch.create(1375));
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

    //--------------------Flow 1--------------------
    @Test
    public void f1_tfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) { // Cannot be FIFO multiplexing due to PMOO
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_sfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_pmoo_arbMux() {
        setArbitraryMux(network.getServers());

        if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f1, expected_results_PMOOAB);
        } else {
            super.runPMOOtest(new PmooAnalysis(network, test_config), f1, expected_results);
        }
    }

    @Test
    public void f1_sinktree_arbMux() {
        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + f1.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("Tree Backlog Bound calculation not applicable.");
        }
    }

    //--------------------Flow 2--------------------
    @Test
    public void f2_tfa() {
        setMux(network.getServers());

        if (test_config.arrivalBoundMethods().contains(ArrivalBoundMethod.PMOO)) {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f2, expected_results_PMOOAB);
        } else {
            super.runTFAtest(new TotalFlowAnalysis(network, test_config), f2, expected_results);
        }
    }

    @Test
    public void f2_sfa() {
        setMux(network.getServers());
        super.runSFAtest(new SeparateFlowAnalysis(network, test_config), f2, expected_results);
    }

    @Test
    public void f2_pmoo_arbMux() {
        setArbitraryMux(network.getServers());
        super.runPMOOtest(new PmooAnalysis(network, test_config), f2, expected_results);
    }

    @Test
    public void f2_sinktree_arbMux() {
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