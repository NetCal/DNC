/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.demos;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.backend.Plotter;
import org.networkcalculus.dnc.backend.number.Number;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;

public class Demo3 {

    public Demo3() {
    }

    public static void main(String[] args) {
        Demo3 demo = new Demo3();

        try {
            demo.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        Curve service_curve = CurveFactory.createRateLatency(new Number(10.0e6), new Number(0.01));

        Curve max_service_curve = CurveFactory.createRateLatency(new Number(100.0e6), new Number(0.001));

        ServerGraph sg = new ServerGraph();

        Server s0 = sg.addServer(service_curve, max_service_curve);
        s0.useMaxSC(false);
        s0.useMaxScRate(false);

        Server s1 = sg.addServer(service_curve, max_service_curve);
        s1.useMaxSC(false);
        s1.useMaxScRate(false);

        sg.addTurn(s0, s1);

        Curve arrival_curve = CurveFactory.createTokenBucket(new Number(0.1e6), new Number(0.1 * 0.1e6));

        sg.addFlow(arrival_curve, s0, s1);
        sg.addFlow(arrival_curve, s0, s1);

        AnalysisConfig configuration_arb = new AnalysisConfig();
        AnalysisConfig configuration_fifo = new AnalysisConfig();
        configuration_arb.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_ARBITRARY);
        configuration_fifo.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO);

        for (Flow flow_of_interest : sg.getFlows()) {

            System.out.println("Flow of interest : " + flow_of_interest.toString());
            System.out.println();

            // Analyze the network
            // TFA ARB
            System.out.println("--- Total Flow Analysis ARB ---");
            TotalFlowAnalysis tfa = new TotalFlowAnalysis(sg, configuration_arb);

            try {
                tfa.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa.getDelayBound().toDouble());
                System.out.println("     per server : " + tfa.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa.getBacklogBound().toDouble());
                System.out.println("     per server : " + tfa.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();

            // TFA FIFO
            System.out.println("--- Total Flow Analysis FIFO ---");
            TotalFlowAnalysis tfa_fifo = new TotalFlowAnalysis(sg, configuration_fifo);

            try {
                tfa_fifo.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa_fifo.getDelayBound().toDouble());
                System.out.println("     per server : " + tfa_fifo.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa_fifo.getBacklogBound().toDouble());
                System.out.println("     per server : " + tfa_fifo.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa_fifo.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();

            // SFA ARB
            System.out.println("--- Separated Flow Analysis ARB ---");
            SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(sg, configuration_arb);

            try {
                sfa.performAnalysis(flow_of_interest);
                System.out.println("e2e SFA SCs     : " + sfa.getLeftOverServiceCurves());
                System.out.println("     per server : " + sfa.getServerLeftOverBetasMapString());
                System.out.println("xtx per server  : " + sfa.getServerAlphasMapString());
                System.out.println("delay bound     : " + sfa.getDelayBound().toDouble());
                System.out.println("backlog bound   : " + sfa.getBacklogBound().toDouble());
            } catch (Exception e) {
                System.out.println("SFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();

            // SFA FIFO
            System.out.println("--- Separated Flow Analysis FIFO ---");
            SeparateFlowAnalysis sfa_fifo = new SeparateFlowAnalysis(sg, configuration_fifo);

            try {
                sfa_fifo.performAnalysis(flow_of_interest);
                System.out.println("e2e SFA SCs     : " + sfa_fifo.getLeftOverServiceCurves());
                System.out.println("     per server : " + sfa_fifo.getServerLeftOverBetasMapString());
                System.out.println("xtx per server  : " + sfa_fifo.getServerAlphasMapString());
                System.out.println("delay bound     : " + sfa_fifo.getDelayBound().toDouble());
                System.out.println("backlog bound   : " + sfa_fifo.getBacklogBound().toDouble());
            } catch (Exception e) {
                System.out.println("SFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();

        }
    }
}