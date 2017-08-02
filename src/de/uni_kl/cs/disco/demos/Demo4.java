/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2011 - 2017 Steffen Bondorf
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

package de.uni_kl.cs.disco.demos;

import java.util.LinkedList;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffineFactory;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.disco.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Server;

public class Demo4 {

    public Demo4() {
    }

    public static void main(String[] args) {
        Demo4 demo = new Demo4();

        try {
            demo.run();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void run() throws Exception {
        ServiceCurve service_curve = CurvePwAffineFactory.createRateLatency(10.0e6, 0.01);
        MaxServiceCurve max_service_curve = CurvePwAffineFactory.createRateLatencyMSC(100.0e6, 0.001);

        Network network = new Network();

        int numServers = 9;
        Server[] servers = new Server[numServers];

        for (int i = 1; i < numServers; i++) {
            servers[i] = network.addServer(service_curve, max_service_curve);
            servers[i].setUseGamma(false);
            servers[i].setUseExtraGamma(false);
        }

        network.addLink(servers[1], servers[2]);
        Link l_1_3 = network.addLink(servers[1], servers[3]);
        Link l_2_4 = network.addLink(servers[2], servers[4]);
        Link l_3_4 = network.addLink(servers[3], servers[4]);
        Link l_4_5 = network.addLink(servers[4], servers[5]);
        Link l_5_6 = network.addLink(servers[5], servers[6]);
        Link l_6_7 = network.addLink(servers[6], servers[7]);
        Link l_7_8 = network.addLink(servers[7], servers[8]);

        ArrivalCurve arrival_curve = CurvePwAffineFactory.createTokenBucket(0.1e6, 0.1 * 0.1e6);

        LinkedList<Link> path0 = new LinkedList<Link>();

//		Links need to be ordered from source server to sink server when defining a path manually
        path0.add(l_2_4);
        path0.add(l_4_5);
        path0.add(l_5_6);
        path0.add(l_6_7);
        path0.add(l_7_8);

        network.addFlow(arrival_curve, path0);

        LinkedList<Link> path1 = new LinkedList<Link>();
        path1.add(l_1_3);
        path1.add(l_3_4);
        path1.add(l_4_5);
        path1.add(l_5_6);

        network.addFlow(arrival_curve, path1);

        for (Flow flow_of_interest : network.getFlows()) {

            System.out.println("Flow of interest : " + flow_of_interest.toString());
            System.out.println();

//			Analyze the network	
//			TFA
            System.out.println("--- Total Flow Analysis ---");
            TotalFlowAnalysis tfa = new TotalFlowAnalysis(network);

            try {
                tfa.performAnalysis(flow_of_interest);
                System.out.println("delay bound     : " + tfa.getDelayBound());
                System.out.println("     per server : " + tfa.getServerDelayBoundMapString());
                System.out.println("backlog bound   : " + tfa.getBacklogBound());
                System.out.println("     per server : " + tfa.getServerBacklogBoundMapString());
                System.out.println("alpha per server: " + tfa.getServerAlphasMapString());
            } catch (Exception e) {
                System.out.println("TFA analysis failed");
                System.out.println(e.toString());
            }

            System.out.println();

//			SFA
            System.out.println("--- Separated Flow Analysis ---");
            SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network);

            try {
                sfa.performAnalysis(flow_of_interest);
                System.out.println("e2e SFA SCs     : " + sfa.getLeftOverServiceCurves());
                System.out.println("     per server : " + sfa.getServerLeftOverBetasMapString());
                System.out.println("xtx per server  : " + sfa.getServerAlphasMapString());
                System.out.println("delay bound     : " + sfa.getDelayBound());
                System.out.println("backlog bound   : " + sfa.getBacklogBound());
            } catch (Exception e) {
                System.out.println("SFA analysis failed");
                System.out.println(e.toString());
            }

            System.out.println();

//			PMOO
            System.out.println("--- PMOO Analysis ---");
            PmooAnalysis pmoo = new PmooAnalysis(network);

            try {
                pmoo.performAnalysis(flow_of_interest);
                System.out.println("e2e PMOO SCs    : " + pmoo.getLeftOverServiceCurves());
                System.out.println("xtx per server  : " + pmoo.getServerAlphasMapString());
                System.out.println("delay bound     : " + pmoo.getDelayBound());
                System.out.println("backlog bound   : " + pmoo.getBacklogBound());
            } catch (Exception e) {
                System.out.println("PMOO analysis failed");
                System.out.println(e.toString());
            }

            System.out.println();
            System.out.println();
        }
    }
}