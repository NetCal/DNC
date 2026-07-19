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

import java.util.LinkedList;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.backend.number.Number;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;

public class Demo4 {

    public Demo4() {
    }

    public static void main(String[] args) {
        Demo4 demo = new Demo4();

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

        int numServers = 9;
        Server[] servers = new Server[numServers];

        for (int i = 1; i < numServers; i++) {
            servers[i] = sg.addServer(service_curve, max_service_curve);
            servers[i].useMaxSC(false);
            servers[i].useMaxScRate(false);
        }

        sg.addTurn(servers[1], servers[2]);
        Turn t_1_3 = sg.addTurn(servers[1], servers[3]);
        Turn t_2_4 = sg.addTurn(servers[2], servers[4]);
        Turn t_3_4 = sg.addTurn(servers[3], servers[4]);
        Turn t_4_5 = sg.addTurn(servers[4], servers[5]);
        Turn t_5_6 = sg.addTurn(servers[5], servers[6]);
        Turn t_6_7 = sg.addTurn(servers[6], servers[7]);
        Turn t_7_8 = sg.addTurn(servers[7], servers[8]);

        Curve arrival_curve = CurveFactory.createTokenBucket(new Number(0.1e6), new Number(0.1 * 0.1e6));

        LinkedList<Turn> path0 = new LinkedList<Turn>();

        // Turns need to be ordered from source server to sink server when defining a
        // path manually
        path0.add(t_2_4);
        path0.add(t_4_5);
        path0.add(t_5_6);
        path0.add(t_6_7);
        path0.add(t_7_8);

        sg.addFlow(arrival_curve, path0);

        LinkedList<Turn> path1 = new LinkedList<Turn>();
        path1.add(t_1_3);
        path1.add(t_3_4);
        path1.add(t_4_5);
        path1.add(t_5_6);

        sg.addFlow(arrival_curve, path1);


        for (Flow flow_of_interest : sg.getFlows()) {

            System.out.println("Flow of interest : " + flow_of_interest.toString());
            System.out.println();

            // Analyze the server graph
            // TFA ARB
            AnalysisConfig cfg_arb = new AnalysisConfig();
            cfg_arb.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_ARBITRARY);
            TotalFlowAnalysis tfa_arb = new TotalFlowAnalysis(sg, cfg_arb);
            System.out.println("--- Total Flow Analysis ARB ---");
            try {
                tfa_arb.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa_arb.getDelayBound());
                System.out.println("     per server : " + tfa_arb.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa_arb.getBacklogBound());
                System.out.println("     per server : " + tfa_arb.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa_arb.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();

            // TFA FIFO
            AnalysisConfig cfg_fifo = new AnalysisConfig();
            cfg_fifo.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO);
            TotalFlowAnalysis tfa_fifo = new TotalFlowAnalysis(sg, cfg_fifo);
            System.out.println("--- Total Flow Analysis FIFO ---");
            try {
                tfa_fifo.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa_fifo.getDelayBound());
                System.out.println("     per server : " + tfa_fifo.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa_fifo.getBacklogBound());
                System.out.println("     per server : " + tfa_fifo.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa_fifo.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();

            // SFA ARB
            SeparateFlowAnalysis sfa_arb = new SeparateFlowAnalysis(sg, cfg_arb);
            System.out.println("--- Separated Flow Analysis ARB ---");
            try {
                sfa_arb.performAnalysis(flow_of_interest);
                System.out.println("e2e SFA SCs     : " + sfa_arb.getLeftOverServiceCurves());
                System.out.println("     per server : " + sfa_arb.getServerLeftOverBetasMapString());
                System.out.println("xtx per server  : " + sfa_arb.getServerAlphasMapString());
                System.out.println("delay bound     : " + sfa_arb.getDelayBound());
                System.out.println("backlog bound   : " + sfa_arb.getBacklogBound());
            } catch (Exception e) {
                System.out.println("SFA analysis failed with: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();

            // SFA FIFO
            System.out.println("--- Separated Flow Analysis FIFO ---");
            SeparateFlowAnalysis sfa_fifo = new SeparateFlowAnalysis(sg, cfg_fifo);

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