/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
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

package de.uni_kl.cs.discodnc.demos;

import de.uni_kl.cs.discodnc.CompFFApresets;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.Turn;
import de.uni_kl.cs.discodnc.tandem.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.TotalFlowAnalysis;

import java.util.LinkedList;

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
        ServiceCurve service_curve = Curve.getFactory().createRateLatency(10.0e6, 0.01);
        MaxServiceCurve max_service_curve = Curve.getFactory().createRateLatencyMSC(100.0e6, 0.001);

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

        ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(0.1e6, 0.1 * 0.1e6);

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

        CompFFApresets compffa_analyses = new CompFFApresets( sg );
        TotalFlowAnalysis tfa = compffa_analyses.tf_analysis;
        SeparateFlowAnalysis sfa = compffa_analyses.sf_analysis;
        PmooAnalysis pmoo = compffa_analyses.pmoo_analysis;
        
        for (Flow flow_of_interest : sg.getFlows()) {

            System.out.println("Flow of interest : " + flow_of_interest.toString());
            System.out.println();

            // Analyze the network
            // TFA
            System.out.println("--- Total Flow Analysis ---");
            try {
                tfa.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa.getDelayBound());
                System.out.println("     per server : " + tfa.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa.getBacklogBound());
                System.out.println("     per server : " + tfa.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed");
        			e.printStackTrace();
            }

            System.out.println();

            // SFA
            System.out.println("--- Separated Flow Analysis ---");
            try {
                sfa.performAnalysis(flow_of_interest);
                System.out.println("e2e SFA SCs     : " + sfa.getLeftOverServiceCurves());
                System.out.println("     per server : " + sfa.getServerLeftOverBetasMapString());
                System.out.println("xtx per server  : " + sfa.getServerAlphasMapString());
                System.out.println("delay bound     : " + sfa.getDelayBound());
                System.out.println("backlog bound   : " + sfa.getBacklogBound());
            } catch (Exception e) {
                System.out.println("SFA analysis failed");
        			e.printStackTrace();
            }

            System.out.println();

            // PMOO
            System.out.println("--- PMOO Analysis ---");
            try {
                pmoo.performAnalysis(flow_of_interest);
                System.out.println("e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves());
                System.out.println("xtx per server  : " + pmoo.getServerAlphasMapString());
                System.out.println("delay bound     : " + pmoo.getDelayBound());
                System.out.println("backlog bound   : " + pmoo.getBacklogBound());
            } catch (Exception e) {
                System.out.println("PMOO analysis failed");
                e.printStackTrace();
            }

            System.out.println();
            System.out.println();
        }
    }
}