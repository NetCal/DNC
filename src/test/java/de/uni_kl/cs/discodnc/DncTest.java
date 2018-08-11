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

import de.uni_kl.cs.discodnc.nc.Analysis;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.Analysis.Analyses;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.Multiplexing;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.AnalysisResults;
import de.uni_kl.cs.discodnc.nc.CalculatorConfig;
import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.NetworkFactory;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// TODO between commits 74c8762863d599914e4d6458c653ab807aa25ff5 and 651b08c1b9b2dc420cc738410ecee24cba0e0310,
// there was a bug in PBOO* arrival bounding regarding backtracking on the foi's path.
// It caused overly pessimistic results and was not caught by the tests.
public abstract class DncTest {
	protected NetworkFactory network_factory;
	protected DncTestConfig test_config;
	protected DncTestResults expected_results;

	@SuppressWarnings("unused")
	private DncTest() {
	}

	protected DncTest(NetworkFactory network_factory, DncTestResults expected_results) {
		this.network_factory = network_factory;
		this.expected_results = expected_results;
	}

	protected void initializeTest(DncTestConfig test_config) {
		this.test_config = test_config;
		printSetting();

		if (test_config.enable_checks) {
			CalculatorConfig.getInstance().enableAllChecks();
		} else {
			CalculatorConfig.getInstance().disableAllChecks();
		}

		CalculatorConfig.getInstance().setCurveImpl(test_config.getCurveImpl());
		CalculatorConfig.getInstance().setNumImpl(test_config.getNumImpl());

		// reinitialize the network and the expected bounds
		network_factory.reinitializeCurves();
		expected_results.initialize();
	}

	public void printSetting() {
		if (test_config.console_output) {
			System.out.println("--------------------------------------------------------------");
			System.out.println();
			System.out.println("Number representation:\t" + test_config.getNumImpl().toString());
			System.out.println("Curve representation:\t" + test_config.getCurveImpl().toString());
			System.out.println("Arrival Boundings:\t" + test_config.arrivalBoundMethods().toString());
			System.out.println("Remove duplicate ABs:\t" + Boolean.toString(test_config.removeDuplicateArrivalBounds()));
			System.out.println("TB,RL convolution:\t" + Boolean.toString(test_config.tbrlConvolution()));
			System.out.println("TB,RL deconvolution:\t" + Boolean.toString(test_config.tbrlDeconvolution()));
		}
	}

	public void setMux(Set<Server> servers) {
		if (!test_config.define_multiplexing_globally) {

			test_config.setMultiplexingDiscipline(MuxDiscipline.SERVER_LOCAL);
			for (Server s : servers) {
				s.setMultiplexingDiscipline(test_config.multiplexing);
			}

		} else {
			// Enforce potential test failure by defining the server-local multiplexing differently.
			Multiplexing mux_local;
			MuxDiscipline mux_global;

			if (test_config.multiplexing == Multiplexing.ARBITRARY) {
				mux_global = MuxDiscipline.GLOBAL_ARBITRARY;
				mux_local = Multiplexing.FIFO;
			} else {
				mux_global = MuxDiscipline.GLOBAL_FIFO;
				mux_local = Multiplexing.ARBITRARY;
			}

			test_config.setMultiplexingDiscipline(mux_global);
			for (Server s : servers) {
				s.setMultiplexingDiscipline(mux_local);
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

	protected void runTFAtest(TotalFlowAnalysis tfa, Flow flow_of_interest) {
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.TFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		assertEquals(bounds.getDelayBound().doubleValue(), 
				tfa.getDelayBound().doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(), 
				"TFA delay");
		assertEquals(bounds.getBacklogBound().doubleValue(), 
				tfa.getBacklogBound().doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(), 
				"TFA backlog");
	}

	protected void runSFAtest(SeparateFlowAnalysis sfa, Flow flow_of_interest) {
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.SFA, test_config.arrivalBoundMethods(), test_config.multiplexing);
		assertEquals(bounds.getDelayBound().doubleValue(), 
				sfa.getDelayBound().doubleValue(),
				Num.getFactory().getTestEpsilon().doubleValue(), 
				"SFA delay");
		assertEquals(bounds.getBacklogBound().doubleValue(),
				sfa.getBacklogBound().doubleValue(),
				Num.getFactory().getTestEpsilon().doubleValue(), 
				"SFA backlog");
	}

	protected void runPMOOtest(PmooAnalysis pmoo, Flow flow_of_interest) {
		if(test_config.multiplexing == AnalysisConfig.Multiplexing.FIFO) {
			assertTrue( true, "PMOO FIFO test skipped");
			return;
		}
		
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

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));
		
		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, test_config.arrivalBoundMethods(), test_config.multiplexing);
		assertEquals(bounds.getDelayBound().doubleValue(), 
				pmoo.getDelayBound().doubleValue(),
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO delay");
		assertEquals(bounds.getBacklogBound().doubleValue(),
				pmoo.getBacklogBound().doubleValue(),
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO backlog");
	}

	protected void runSinkTreePMOOtest(Network sink_tree, Flow flow_of_interest) {
		Num backlog_bound_TBRL = null;
		Num backlog_bound_TBRL_CONV = null;
		Num backlog_bound_TBRL_CONV_TBRL_DECONV = null;
		Num backlog_bound_TBRL_HOMO = null;

		try {
			backlog_bound_TBRL = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(
					sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL));
			backlog_bound_TBRL_CONV = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(
					sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV));
			backlog_bound_TBRL_CONV_TBRL_DECONV = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(
					sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_CONV_TBRL_DECONV));
			backlog_bound_TBRL_HOMO = Num.getFactory().create(Bound.backlogPmooSinkTreeTbRl(
					sink_tree, flow_of_interest.getSink(), ArrivalBoundMethod.PMOO_SINKTREE_TBRL_HOMO));
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
			System.out.println("backlog bound TBRL CONV TBRL DECONV : " + backlog_bound_TBRL_CONV_TBRL_DECONV.toString());
			System.out.println("backlog bound RBRL HOMO             : " + backlog_bound_TBRL_HOMO.toString());
			System.out.println();
		}

		// The alias holds the original flow ID, independent of the order flows are added to the network under analysis.
		Integer foiID_from_alias = Integer.valueOf(flow_of_interest.getAlias().substring(1));

		AnalysisResults bounds = expected_results.getBounds(foiID_from_alias, Analyses.PMOO, DncTestMethodSources.sinktree, Multiplexing.ARBITRARY);
		assertEquals(bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL.doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO backlog TBRL");
		assertEquals(bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_CONV.doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO backlog TBRL CONV");
		assertEquals(bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_CONV_TBRL_DECONV.doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO backlog TBRL CONV TBRL DECONV");
		assertEquals(bounds.getBacklogBound().doubleValue(), 
				backlog_bound_TBRL_HOMO.doubleValue(), 
				Num.getFactory().getTestEpsilon().doubleValue(),
				"PMOO backlog RBRL HOMO");
	}
}
