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

import de.uni_kl.cs.discodnc.curves.*;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Server;

public class Demo1 {

    public Demo1() {
    }

    public static void main(String[] args) {
        Demo1 demo = new Demo1();

        try {
            demo.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        ArrivalCurve arrival_curve = Curve.getFactory().createTokenBucket(0.1e6, 0.1 * 0.1e6);

        ServiceCurve service_curve = Curve.getFactory().createRateLatency(10.0e6, 0.01);
        MaxServiceCurve max_service_curve = Curve.getFactory().createRateLatencyMSC(100.0e6, 0.001);

        Network network = new Network();
        AnalysisConfig configuration = new AnalysisConfig();

        Server s0 = network.addServer(service_curve, max_service_curve);
        // Creating a server with a maximum service curve automatically triggers the
        // following setting
        // s0.setUseGamma( true );
        // s0.setUseExtraGamma( true );
        // , however, disabling the use of a maximum service curve in a configuration
        // overrides it.
        configuration.setUseGamma(AnalysisConfig.GammaFlag.GLOBALLY_ON);
        configuration.setUseExtraGamma(AnalysisConfig.GammaFlag.GLOBALLY_ON);

        Flow flow_of_interest = network.addFlow(arrival_curve, s0);

        System.out.println("Flow of interest : " + flow_of_interest.toString());
        System.out.println();

        // Analyze the network
        // TFA
        System.out.println("--- Total Flow Analysis ---");
        TotalFlowAnalysis tfa = new TotalFlowAnalysis(network, configuration);

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
        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network, configuration);

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
        PmooAnalysis pmoo = new PmooAnalysis(network, configuration);

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