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

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.network.*;

import java.util.LinkedList;

public class FF_3S_1SC_2F_1AC_2P_Network implements NetworkFactory {
    private static final int sc_R = 20;
    private static final int sc_T = 20;
    private static final int ac_r = 5;
    private static final int ac_b = 25;
    protected Server s0, s1, s2;
    protected Link l_s0_s1, l_s1_s2;
    protected Flow f0, f1;
    private ServiceCurve service_curve = CurvePwAffine.getFactory().createRateLatency(sc_R, sc_T);
    private ArrivalCurve arrival_curve = CurvePwAffine.getFactory().createTokenBucket(ac_r, ac_b);
    private Network network;

    public FF_3S_1SC_2F_1AC_2P_Network() {
    }

    public Network getNetwork() {
    		if( network == null ) {
    			return createNetwork();
    		} else {
    			return network;
    		}
    }

    public Network createNetwork() {
        network = new Network();

        s0 = network.addServer("s0", service_curve);
        s1 = network.addServer("s1", service_curve);
        s2 = network.addServer("s2", service_curve);

        try {
            l_s0_s1 = network.addLink("l_s0_s1", s0, s1);
            l_s1_s2 = network.addLink("l_s1_s2", s1, s2);
            network.addLink(s0, s2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        LinkedList<Link> f1_path = new LinkedList<Link>();
        f1_path.add(l_s0_s1);
        f1_path.add(l_s1_s2);

        try {
            f0 = network.addFlow("f0", arrival_curve, s0, s2);
            f1 = network.addFlow("f1", arrival_curve, f1_path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return network;
    }

    public void reinitializeCurves() {
        service_curve = CurvePwAffine.getFactory().createRateLatency(sc_R, sc_T);
        for (Server server : network.getServers()) {
            server.setServiceCurve(service_curve);
        }

        arrival_curve = CurvePwAffine.getFactory().createTokenBucket(ac_r, ac_b);
        for (Flow flow : network.getFlows()) {
            flow.setArrivalCurve(arrival_curve);
        }
    }
}