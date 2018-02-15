/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
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

package de.uni_kl.cs.discodnc;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.NetworkFactory;
import de.uni_kl.cs.discodnc.network.Server;

public class S_1SC_10F_10AC_Network implements NetworkFactory {
	private static final int sc_R = 10;
	private static final int sc_T = 10;
	protected Server s0;
	protected Flow f0, f6;
	private ServiceCurve service_curve = CurvePwAffine.getFactory().createRateLatency(sc_R, sc_T);
	private Flow[] flows = new Flow[10];
	private ArrivalCurve[] arrival_curves = new ArrivalCurve[10];
	private Network network;

	public S_1SC_10F_10AC_Network() {
		network = createNetwork();
	}

	public Network getNetwork() {
		return network;
	}

	public Network createNetwork() {
		network = new Network();

		s0 = network.addServer(service_curve);
		s0.setUseGamma(false);
		s0.setUseExtraGamma(false);

		try {
			for (int i = 1; i <= 10; i++) {
				arrival_curves[i - 1] = CurvePwAffine.getFactory().createTokenBucket(i * 0.1, i);
				flows[i - 1] = network.addFlow(arrival_curves[i - 1], s0);
			}
			f0 = flows[0];
			f6 = flows[6];
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

		for (int i = 1; i <= 10; i++) {
			arrival_curves[i - 1] = CurvePwAffine.getFactory().createTokenBucket(i * 0.1, i);
			flows[i - 1].setArrivalCurve(arrival_curves[i - 1]);
		}
	}
}
