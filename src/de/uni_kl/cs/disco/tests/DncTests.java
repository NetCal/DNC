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

import de.uni_kl.cs.disco.nc.Analysis;
import de.uni_kl.cs.disco.nc.Analysis.Analyses;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.disco.nc.AnalysisResults;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.nc.bounds.Bound;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.suite.api.SelectClasses;

import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

@SelectClasses({ S_1SC_1F_1AC_Test.class, S_1SC_2F_1AC_Test.class, S_1SC_2F_2AC_Test.class, S_1SC_10F_10AC_Test.class,
		TA_2S_1SC_1F_1AC_1P_Test.class, TA_3S_1SC_2F_1AC_1P_Test.class, TA_2S_1SC_2F_1AC_1P_Test.class,
		TA_4S_1SC_2F_1AC_2P_Test.class, TA_2S_1SC_2F_1AC_2P_Test.class, TA_3S_1SC_3F_1AC_3P_Test.class,
		TA_2S_1SC_4F_1AC_1P_Test.class, TA_2S_2SC_1F_1AC_1P_Test.class, TA_2S_2SC_2F_1AC_1P_Test.class,
		TR_3S_1SC_2F_1AC_2P_Test.class, TR_7S_1SC_3F_1AC_3P_Test.class, FF_3S_1SC_2F_1AC_2P_Test.class,
		FF_4S_1SC_3F_1AC_3P_Test.class, FF_4S_1SC_4F_1AC_4P_Test.class })

public class DncTests {
    protected static Collection<DncTestConfig> test_configurations = createParameters();

    protected DncTestConfig test_config;
    protected boolean reinitilize_test = true;

    protected void setDncTestConfig(DncTestConfig test_config) {
        this.test_config = test_config;

        if (test_config.enable_checks) {
            CalculatorConfig.getInstance().enableAllChecks();
        } else {
            CalculatorConfig.getInstance().disableAllChecks();
        }

        reinitilize_test = (CalculatorConfig.getInstance().setNumImpl(test_config.getNumImpl())
                || CalculatorConfig.getInstance().setCurveImpl(test_config.getCurveImpl()));

        CalculatorConfig.getInstance().setOperationImpl(test_config.operation_implementation);
    }

    @ValueSource(strings = "{index}: {0}")
    public static Set<DncTestConfig> createParameters() {
        Set<DncTestConfig> test_configurations = new HashSet<DncTestConfig>();

        Set<CalculatorConfig.NumImpl> nums = new HashSet<CalculatorConfig.NumImpl>();
        nums.add(CalculatorConfig.NumImpl.REAL_DOUBLE_PRECISION);
        nums.add(CalculatorConfig.NumImpl.REAL_SINGLE_PRECISION);
        nums.add(CalculatorConfig.NumImpl.RATIONAL_INTEGER);
        nums.add(CalculatorConfig.NumImpl.RATIONAL_BIGINTEGER);

        Set<CalculatorConfig.CurveImpl> curves = new HashSet<CalculatorConfig.CurveImpl>();
        curves.add(CalculatorConfig.CurveImpl.DNC);
        curves.add(CalculatorConfig.CurveImpl.MPA_RTC);

        Set<CalculatorConfig.OperationImpl> operations = new HashSet<CalculatorConfig.OperationImpl>();
        operations.add(CalculatorConfig.OperationImpl.DNC);
        operations.add(CalculatorConfig.OperationImpl.NATIVE);

        Set<AnalysisConfig.Multiplexing> mux_disciplines = new HashSet<AnalysisConfig.Multiplexing>();
        mux_disciplines.add(AnalysisConfig.Multiplexing.ARBITRARY);
        mux_disciplines.add(AnalysisConfig.Multiplexing.FIFO);

        Set<AnalysisConfig.ArrivalBoundMethod> single_1 = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        single_1.add(AnalysisConfig.ArrivalBoundMethod.PBOO_CONCATENATION);

        Set<AnalysisConfig.ArrivalBoundMethod> single_2 = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        single_2.add(AnalysisConfig.ArrivalBoundMethod.PBOO_PER_HOP);

        LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>> single_abs_allMux = new LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>>();
        single_abs_allMux.add(single_1);
        single_abs_allMux.add(single_2);

        LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>> single_abs_arbMux = new LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>>();
        single_abs_arbMux.add(Collections.singleton(AnalysisConfig.ArrivalBoundMethod.PMOO));

        Set<AnalysisConfig.ArrivalBoundMethod> pair_1 = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        pair_1.add(AnalysisConfig.ArrivalBoundMethod.PBOO_PER_HOP);
        pair_1.add(AnalysisConfig.ArrivalBoundMethod.PBOO_CONCATENATION);

        Set<AnalysisConfig.ArrivalBoundMethod> pair_2 = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        pair_2.add(AnalysisConfig.ArrivalBoundMethod.PBOO_PER_HOP);
        pair_2.add(AnalysisConfig.ArrivalBoundMethod.PMOO);

        Set<AnalysisConfig.ArrivalBoundMethod> pair_3 = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        pair_3.add(AnalysisConfig.ArrivalBoundMethod.PBOO_CONCATENATION);
        pair_3.add(AnalysisConfig.ArrivalBoundMethod.PMOO);

        LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>> pair_abs_allMux = new LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>>();
        pair_abs_allMux.add(pair_1);

        LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>> pair_abs_arbMux = new LinkedList<Set<AnalysisConfig.ArrivalBoundMethod>>();
        pair_abs_arbMux.add(pair_2);
        pair_abs_arbMux.add(pair_3);

        Set<AnalysisConfig.ArrivalBoundMethod> triplet_arbMux = new HashSet<AnalysisConfig.ArrivalBoundMethod>();
        triplet_arbMux.add(AnalysisConfig.ArrivalBoundMethod.PBOO_PER_HOP);
        triplet_arbMux.add(AnalysisConfig.ArrivalBoundMethod.PBOO_CONCATENATION);
        triplet_arbMux.add(AnalysisConfig.ArrivalBoundMethod.PMOO);

        // Parameter configurations for single arrival bounding tests
        // AB, remove duplicate ABs, tbrl opt convolution, tbrl opt deconvolution, mux,
        // global mux def, number class to use, curve class to use, operations class to
        // use
        for (CalculatorConfig.CurveImpl curve : curves) {
            for (CalculatorConfig.OperationImpl operation : operations) {
                for (CalculatorConfig.NumImpl num : nums) {
                    for (Set<AnalysisConfig.ArrivalBoundMethod> single_ab : single_abs_allMux) {
                        for (AnalysisConfig.Multiplexing mux : mux_disciplines) {
                            test_configurations.add(new DncTestConfig(single_ab, false, false, false, mux, false, num,
                                    curve, operation));
                            test_configurations.add(new DncTestConfig(single_ab, false, true, false, mux, false, num,
                                    curve, operation));
                            test_configurations.add(new DncTestConfig(single_ab, false, false, true, mux, false, num,
                                    curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(single_ab, false, true, true, mux, false, num, curve, operation));
                            test_configurations.add(new DncTestConfig(single_ab, false, false, false, mux, true, num,
                                    curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(single_ab, false, true, false, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(single_ab, false, false, true, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(single_ab, false, true, true, mux, true, num, curve, operation));
                        }
                    }
                    for (Set<AnalysisConfig.ArrivalBoundMethod> single_ab : single_abs_arbMux) {
                        test_configurations.add(new DncTestConfig(single_ab, false, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(single_ab, false, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    }

                    for (Set<AnalysisConfig.ArrivalBoundMethod> pair_ab : pair_abs_allMux) {
                        for (AnalysisConfig.Multiplexing mux : mux_disciplines) {
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, false, false, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, false, false, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, true, false, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, true, false, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, false, true, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, false, true, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, true, true, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, true, true, mux, false, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, false, false, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, false, false, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, true, false, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, true, false, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, false, true, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, false, true, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, false, true, true, mux, true, num, curve, operation));
                            test_configurations.add(
                                    new DncTestConfig(pair_ab, true, true, true, mux, true, num, curve, operation));
                        }
                    }
                    for (Set<AnalysisConfig.ArrivalBoundMethod> pair_ab : pair_abs_arbMux) {
                        test_configurations.add(new DncTestConfig(pair_ab, false, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, false, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, true, false,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, false, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, false, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                        test_configurations.add(new DncTestConfig(pair_ab, true, true, true,
                                AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    }

                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, false, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, false,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, false, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, false, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, false, true, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                    test_configurations.add(new DncTestConfig(triplet_arbMux, true, true, true,
                            AnalysisConfig.Multiplexing.ARBITRARY, true, num, curve, operation));
                }
            }
        }

        return test_configurations;
    }

//    @BeforeEach
    public void printSetting() {
        if (test_config.console_output) {
            System.out.println("--------------------------------------------------------------");
            System.out.println();
            System.out.println("Number representation:\t" + test_config.getNumImpl().toString());
            System.out.println("Curve representation:\t" + test_config.getCurveImpl().toString());
            System.out.println("Arrival Boundings:\t" + test_config.arrivalBoundMethods().toString());
            System.out
                    .println("Remove duplicate ABs:\t" + Boolean.toString(test_config.removeDuplicateArrivalBounds()));
            System.out.println("TB,RL convolution:\t" + Boolean.toString(test_config.tbrlConvolution()));
            System.out.println("TB,RL deconvolution:\t" + Boolean.toString(test_config.tbrlDeconvolution()));
        }
    }

    public void setMux(Set<Server> servers) {
        if (!test_config.define_multiplexing_globally) {

            test_config.setMultiplexingDiscipline(MuxDiscipline.SERVER_LOCAL);
            for (Server s : servers) {
                s.setMultiplexingDiscipline(test_config.mux_discipline);
            }

        } else {
            // Enforce potential test failure by defining the server-local multiplexing
            // differently.
            AnalysisConfig.Multiplexing mux_local;
            MuxDiscipline mux_global;

            if (test_config.mux_discipline == AnalysisConfig.Multiplexing.ARBITRARY) {
                mux_global = MuxDiscipline.GLOBAL_ARBITRARY;
                mux_local = AnalysisConfig.Multiplexing.FIFO;
            } else {
                mux_global = MuxDiscipline.GLOBAL_FIFO;
                mux_local = AnalysisConfig.Multiplexing.ARBITRARY;
            }

            test_config.setMultiplexingDiscipline(mux_global);
            for (Server s : servers) {
                s.setMultiplexingDiscipline(mux_local);
            }
        }
    }

    public void setFifoMux(Set<Server> servers) {
        // This is extremely slowing down the tests
        // assumeTrue( "FIFO multiplexing does not allow for PMOO arrival bounding.",
        // !test_config.arrivalBoundMethods().contains( ArrivalBoundMethod.PMOO ) );

        if (test_config.define_multiplexing_globally == true) {
            test_config.setMultiplexingDiscipline(MuxDiscipline.GLOBAL_FIFO);
            // Enforce potential test failure
            for (Server s : servers) {
                s.setMultiplexingDiscipline(AnalysisConfig.Multiplexing.ARBITRARY);
            }
        } else {
            test_config.setMultiplexingDiscipline(MuxDiscipline.SERVER_LOCAL);
            // Enforce potential test failure
            for (Server s : servers) {
                s.setMultiplexingDiscipline(AnalysisConfig.Multiplexing.FIFO);
            }
        }
    }

    public void setArbitraryMux(Set<Server> servers) {
        if (test_config.define_multiplexing_globally == true) {
            test_config.setMultiplexingDiscipline(MuxDiscipline.GLOBAL_ARBITRARY);
            // Enforce potential test failure
            for (Server s : servers) {
                s.setMultiplexingDiscipline(AnalysisConfig.Multiplexing.FIFO);
            }
        } else {
            test_config.setMultiplexingDiscipline(MuxDiscipline.SERVER_LOCAL);
            // Enforce potential test failure
            for (Server s : servers) {
                s.setMultiplexingDiscipline(AnalysisConfig.Multiplexing.ARBITRARY);
            }
        }
    }

    private void runAnalysis(Analysis analysis, Flow flow_of_interest) {
        try {
            analysis.performAnalysis(flow_of_interest);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Analysis failed");
        }
    }

    protected void runTFAtest(TotalFlowAnalysis tfa, Flow flow_of_interest, DncTestResults expected_bounds) {
        runAnalysis(tfa, flow_of_interest);

        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTotal Flow Analysis (TFA)");
            System.out.println("Multiplexing:\t\tFIFO");

            System.out.println("Flow of interest:\t" + flow_of_interest.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("delay bound     : " + tfa.getDelayBound());
            System.out.println("     per server : " + tfa.getServerDelayBoundMapString());
            System.out.println("backlog bound   : " + tfa.getBacklogBound());
            System.out.println("     per server : " + tfa.getServerBacklogBoundMapString());
            System.out.println("alpha per server: " + tfa.getServerAlphasMapString());
            System.out.println();
        }

        AnalysisResults bounds = expected_bounds.getBounds(Analyses.TFA, test_config.mux_discipline, flow_of_interest);
        assertEquals(bounds.getDelayBound(), tfa.getDelayBound(), "TFA delay");
        assertEquals(bounds.getBacklogBound(), tfa.getBacklogBound(), "TFA backlog");
    }

    protected void runSFAtest(SeparateFlowAnalysis sfa, Flow flow_of_interest, DncTestResults expected_bounds) {
        runAnalysis(sfa, flow_of_interest);

        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tSeparate Flow Analysis (SFA)");
            System.out.println("Multiplexing:\t\tFIFO");

            System.out.println("Flow of interest:\t" + flow_of_interest.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("e2e SFA SCs     : " + sfa.getLeftOverServiceCurves());
            System.out.println("     per server : " + sfa.getServerLeftOverBetasMapString());
            System.out.println("xtx per server  : " + sfa.getServerAlphasMapString());
            System.out.println("delay bound     : " + sfa.getDelayBound());
            System.out.println("backlog bound   : " + sfa.getBacklogBound());
            System.out.println();
        }

        AnalysisResults bounds = expected_bounds.getBounds(Analyses.SFA, test_config.mux_discipline, flow_of_interest);
        assertEquals(bounds.getDelayBound(), sfa.getDelayBound(), "SFA delay");
        assertEquals(bounds.getBacklogBound(), sfa.getBacklogBound(), "SFA backlog");
    }

    protected void runPMOOtest(PmooAnalysis pmoo, Flow flow_of_interest, DncTestResults expected_bounds) {
        runAnalysis(pmoo, flow_of_interest);

        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tPay Multiplexing Only Once (PMOO)");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + flow_of_interest.toString());
            System.out.println();

            System.out.println("--- Results: ---");
            System.out.println("e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves());
            System.out.println("xtx per server  : " + pmoo.getServerAlphasMapString());
            System.out.println("delay bound     : " + pmoo.getDelayBound());
            System.out.println("backlog bound   : " + pmoo.getBacklogBound());
            System.out.println();
        }

        AnalysisResults bounds = expected_bounds.getBounds(Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY,
                flow_of_interest);
        assertEquals(bounds.getDelayBound(), pmoo.getDelayBound(), "PMOO delay");
        assertEquals(bounds.getBacklogBound(), pmoo.getBacklogBound(), "PMOO backlog");
    }

    protected void runSinkTreePMOOtest(Network sink_tree, Flow flow_of_interest, DncTestResults expected_bounds) {
        Num backlog_bound_TBRL = null;
        Num backlog_bound_TBRL_CONV = null;
        Num backlog_bound_TBRL_CONV_TBRL_DECONV = null;
        Num backlog_bound_TBRL_HOMO = null;

        try {
            backlog_bound_TBRL = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
                    flow_of_interest.getSink(), AnalysisConfig.ArrivalBoundMethod.PMOO_SINKTREE_TBRL));
            backlog_bound_TBRL_CONV = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
                    flow_of_interest.getSink(), AnalysisConfig.ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV));
            backlog_bound_TBRL_CONV_TBRL_DECONV = Num.getFactory()
                    .create(Bound.backlogPmooSinkTreeTbRl(sink_tree, flow_of_interest.getSink(),
                            AnalysisConfig.ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV));
            backlog_bound_TBRL_HOMO = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(sink_tree,
                    flow_of_interest.getSink(), AnalysisConfig.ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Analysis failed");
        }

        if (test_config.fullConsoleOutput()) {
            System.out.println("Analysis:\t\tTree Backlog Bound Analysis");
            System.out.println("Multiplexing:\t\tArbitrary");

            System.out.println("Flow of interest:\t" + flow_of_interest.toString());
            System.out.println();

            System.out.println("--- Result: ---");

            System.out.println("backlog bound TBRL                  : " + backlog_bound_TBRL.toString());
            System.out.println("backlog bound TBRL CONV             : " + backlog_bound_TBRL_CONV.toString());
            System.out
                    .println("backlog bound TBRL CONV TBRL DECONV : " + backlog_bound_TBRL_CONV_TBRL_DECONV.toString());
            System.out.println("backlog bound RBRL HOMO             : " + backlog_bound_TBRL_HOMO.toString());
            System.out.println();
        }

        assertEquals(expected_bounds
                        .getBounds(Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest).getBacklogBound(),
                backlog_bound_TBRL,"PMOO backlog TBRL");

        assertEquals(expected_bounds
                        .getBounds(Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest).getBacklogBound(),
                backlog_bound_TBRL_CONV, "PMOO backlog TBRL CONV");

        assertEquals(expected_bounds
                        .getBounds(Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest).getBacklogBound(),
                backlog_bound_TBRL_CONV_TBRL_DECONV, "PMOO backlog TBRL CONV TBRL DECONV");

        assertEquals(expected_bounds
                        .getBounds(Analyses.PMOO, AnalysisConfig.Multiplexing.ARBITRARY, flow_of_interest).getBacklogBound(),
                backlog_bound_TBRL_HOMO, "PMOO backlog RBRL HOMO");
    }
}
