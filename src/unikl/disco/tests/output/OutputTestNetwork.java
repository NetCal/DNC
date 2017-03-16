/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
 *
 * Copyright (C) 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.tests.output;

import java.util.LinkedList;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.AnalysisConfig.Multiplexing;
import unikl.disco.network.NetworkFactory;
import unikl.disco.network.Network;
import unikl.disco.network.Server;

/**
 * 
 * @author Steffen Bondorf
 */
public class OutputTestNetwork implements NetworkFactory {
	private Network network;
	private Server[] servers;
	private AnalysisConfig.Multiplexing mux = AnalysisConfig.Multiplexing.ARBITRARY;

	private ServiceCurve service_curve;
	private MaxServiceCurve max_service_curve;
	private ArrivalCurve arrival_curve;

	public OutputTestNetwork() throws Exception {
		network = createNetwork();
	}
	
	public Network createNetwork() {
		servers = new Server[38];

		try{
			service_curve = new ServiceCurve( "SC{(0.0,0.0),10000.0}" );
			max_service_curve = new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" );
			arrival_curve = new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" );
			
			network = new Network();
			
			createServers1();
			createLinks1();
			createFlows1();
			createFlows2();
			createFlows3();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return network;
	}

	public Network getNetwork() {
		return network;
	}

	public void reinitializeCurves() {
		network = createNetwork();
	}
	
	public void createServers1() throws Exception {
		servers[34] = network.addServer( "s34", service_curve, max_service_curve, mux, true, true );
		servers[11] = network.addServer( "s11", service_curve, max_service_curve, mux, true, true );
		servers[29] = network.addServer( "s29", service_curve, max_service_curve, mux, true, true );
		servers[31] = network.addServer( "s31", service_curve, max_service_curve, mux, true, true );
		servers[30] = network.addServer( "s30", service_curve, max_service_curve, mux, true, true );
		servers[18] = network.addServer( "s18", service_curve, max_service_curve, mux, true, true );
		servers[28] = network.addServer( "s28", service_curve, max_service_curve, mux, true, true );
		servers[2] = network.addServer( "s2", service_curve, max_service_curve, mux, true, true );
		servers[37] = network.addServer( "s37", service_curve, max_service_curve, mux, true, true );
		servers[16] = network.addServer( "s16", service_curve, max_service_curve, mux, true, true );
		servers[19] = network.addServer( "s19", service_curve, max_service_curve, mux, true, true );
		servers[32] = network.addServer( "s32", service_curve, max_service_curve, mux, true, true );
		servers[15] = network.addServer( "s15", service_curve, max_service_curve, mux, true, true );
		servers[9] = network.addServer( "s9", service_curve, max_service_curve, mux, true, true );
		servers[23] = network.addServer( "s23", service_curve, max_service_curve, mux, true, true );
		servers[10] = network.addServer( "s10", service_curve, max_service_curve, mux, true, true );
		servers[25] = network.addServer( "s25", service_curve, max_service_curve, mux, true, true );
		servers[17] = network.addServer( "s17", service_curve, max_service_curve, mux, true, true );
		servers[0] = network.addServer( "s0", service_curve, max_service_curve, mux, true, true );
		servers[13] = network.addServer( "s13", service_curve, max_service_curve, mux, true, true );
		servers[7] = network.addServer( "s7", service_curve, max_service_curve, mux, true, true );
		servers[4] = network.addServer( "s4", service_curve, max_service_curve, mux, true, true );
		servers[8] = network.addServer( "s8", service_curve, max_service_curve, mux, true, true );
		servers[27] = network.addServer( "s27", service_curve, max_service_curve, mux, true, true );
		servers[21] = network.addServer( "s21", service_curve, max_service_curve, mux, true, true );
		servers[3] = network.addServer( "s3", service_curve, max_service_curve, mux, true, true );
		servers[36] = network.addServer( "s36", service_curve, max_service_curve, mux, true, true );
		servers[12] = network.addServer( "s12", service_curve, max_service_curve, mux, true, true );
		servers[35] = network.addServer( "s35", service_curve, max_service_curve, mux, true, true );
		servers[26] = network.addServer( "s26", service_curve, max_service_curve, mux, true, true );
		servers[24] = network.addServer( "s24", service_curve, max_service_curve, mux, true, true );
		servers[33] = network.addServer( "s33", service_curve, max_service_curve, mux, true, true );
		servers[14] = network.addServer( "s14", service_curve, max_service_curve, mux, true, true );
		servers[6] = network.addServer( "s6", service_curve, max_service_curve, mux, true, true );
		servers[5] = network.addServer( "s5", service_curve, max_service_curve, mux, true, true );
		servers[22] = network.addServer( "s22", service_curve, max_service_curve, mux, true, true );
		servers[20] = network.addServer( "s20", service_curve, max_service_curve, mux, true, true );
		servers[1] = network.addServer( "s1", service_curve, max_service_curve, mux, true, true );
	}

	public void createLinks1() throws Exception {
		network.addLink( "l19", servers[14], servers[0] );
		network.addLink( "l13", servers[11], servers[28] );
		network.addLink( "l26", servers[9], servers[3] );
		network.addLink( "l9", servers[25], servers[12] );
		network.addLink( "l23", servers[3], servers[31] );
		network.addLink( "l10", servers[13], servers[22] );
		network.addLink( "l18", servers[16], servers[21] );
		network.addLink( "l45", servers[31], servers[23] );
		network.addLink( "l11", servers[13], servers[23] );
		network.addLink( "l39", servers[29], servers[35] );
		network.addLink( "l51", servers[36], servers[9] );
		network.addLink( "l7", servers[21], servers[8] );
		network.addLink( "l40", servers[29], servers[36] );
		network.addLink( "l27", servers[4], servers[6] );
		network.addLink( "l30", servers[19], servers[1] );
		network.addLink( "l24", servers[3], servers[33] );
		network.addLink( "l50", servers[36], servers[7] );
		network.addLink( "l35", servers[18], servers[25] );
		network.addLink( "l36", servers[10], servers[29] );
		network.addLink( "l4", servers[7], servers[19] );
		network.addLink( "l8", servers[21], servers[9] );
		network.addLink( "l14", servers[2], servers[22] );
		network.addLink( "l52", servers[28], servers[14] );
		network.addLink( "l33", servers[18], servers[23] );
		network.addLink( "l5", servers[7], servers[20] );
		network.addLink( "l22", servers[37], servers[36] );
		network.addLink( "l53", servers[15], servers[27] );
		network.addLink( "l28", servers[4], servers[7] );
		network.addLink( "l46", servers[31], servers[25] );
		network.addLink( "l3", servers[5], servers[13] );
		network.addLink( "l16", servers[2], servers[25] );
		network.addLink( "l44", servers[31], servers[22] );
		network.addLink( "l32", servers[0], servers[21] );
		network.addLink( "l42", servers[24], servers[32] );
		network.addLink( "l29", servers[4], servers[8] );
		network.addLink( "l2", servers[17], servers[9] );
		network.addLink( "l41", servers[34], servers[30] );
		network.addLink( "l6", servers[21], servers[6] );
		network.addLink( "l20", servers[1], servers[15] );
		network.addLink( "l43", servers[24], servers[33] );
		network.addLink( "l12", servers[13], servers[24] );
		network.addLink( "l0", servers[17], servers[7] );
		network.addLink( "l48", servers[8], servers[35] );
		network.addLink( "l15", servers[2], servers[24] );
		network.addLink( "l38", servers[26], servers[32] );
		network.addLink( "l25", servers[32], servers[4] );
		network.addLink( "l47", servers[8], servers[34] );
		network.addLink( "l21", servers[37], servers[34] );
		network.addLink( "l34", servers[18], servers[24] );
		network.addLink( "l37", servers[26], servers[31] );
		network.addLink( "l49", servers[36], servers[6] );
		network.addLink( "l17", servers[16], servers[19] );
		network.addLink( "l1", servers[17], servers[8] );
		network.addLink( "l31", servers[0], servers[20] );
	}

	public void createFlows1() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f119", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f8", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f130", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f5", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f48", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f33", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f55", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f93", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f129", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f141", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f1", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f69", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f95", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f26", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		network.addFlow( "f36", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f127", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f131", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f132", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		network.addFlow( "f50", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f92", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f147", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f23", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f57", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f76", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f0", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f46", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f71", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f113", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f115", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f82", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f25", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f66", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f87", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f2", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f102", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f73", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f126", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f151", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f135", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f90", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f39", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f7", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f27", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f45", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f120", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f79", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f142", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f109", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f84", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f107", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		network.addFlow( "f106", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f4", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f6", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		network.addFlow( "f13", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f140", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		network.addFlow( "f31", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f59", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f110", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f117", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		network.addFlow( "f98", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f77", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f86", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f19", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		network.addFlow( "f96", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f21", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f104", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		network.addFlow( "f56", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f30", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f122", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

	}

	public void createFlows2() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f58", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f143", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f35", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f134", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f91", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f14", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f12", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		network.addFlow( "f145", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		network.addFlow( "f3", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		network.addFlow( "f136", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f41", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f138", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f128", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f85", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f112", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f94", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f148", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		network.addFlow( "f121", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f53", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f80", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f20", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f52", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f74", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f24", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f114", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f64", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f100", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		network.addFlow( "f65", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f101", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f49", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		network.addFlow( "f54", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f124", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f47", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f108", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f81", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f43", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f22", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f103", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f18", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f63", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f9", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f17", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f11", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		network.addFlow( "f105", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f123", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f78", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f16", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f144", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f34", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		network.addFlow( "f68", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f37", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f125", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f38", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f116", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f51", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f32", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		network.addFlow( "f99", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f133", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f83", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f111", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		network.addFlow( "f10", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f70", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f137", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f149", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f150", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f28", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f40", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		network.addFlow( "f139", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f89", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f88", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

	}

	public void createFlows3() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f29", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f75", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f146", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f118", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f97", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f61", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f15", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f60", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f67", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[19] );
		network.addFlow( "f44", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f42", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f62", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f72", arrival_curve, servers_on_path_s );
		servers_on_path_s.clear();
	}
}