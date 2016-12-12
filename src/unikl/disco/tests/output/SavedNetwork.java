/* 
 * This file is part of the Disco Deterministic Network Calculator v2.3 "Centaur".
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

import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ArrivalCurve;

import unikl.disco.network.Network;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;

public class SavedNetwork implements unikl.disco.network.ReturnsNetwork {
	public static Network network;
	private static Server[] servers;

	public static void createServers1() throws Exception {
		servers[29] = network.addServer( "s26", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[32] = network.addServer( "s14", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[4] = network.addServer( "s30", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[2] = network.addServer( "s29", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[10] = network.addServer( "s19", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[18] = network.addServer( "s0", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[27] = network.addServer( "s12", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[21] = network.addServer( "s4", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[23] = network.addServer( "s27", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[25] = network.addServer( "s3", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[33] = network.addServer( "s6", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[9] = network.addServer( "s16", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[31] = network.addServer( "s33", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[11] = network.addServer( "s32", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[17] = network.addServer( "s17", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[34] = network.addServer( "s5", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[3] = network.addServer( "s31", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[36] = network.addServer( "s20", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[16] = network.addServer( "s25", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[28] = network.addServer( "s35", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[12] = network.addServer( "s15", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[6] = network.addServer( "s28", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[15] = network.addServer( "s10", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[13] = network.addServer( "s9", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[1] = network.addServer( "s11", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[8] = network.addServer( "s37", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[26] = network.addServer( "s36", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[14] = network.addServer( "s23", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[20] = network.addServer( "s7", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[30] = network.addServer( "s24", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[19] = network.addServer( "s13", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[0] = network.addServer( "s34", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[7] = network.addServer( "s2", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[35] = network.addServer( "s22", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[5] = network.addServer( "s18", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[22] = network.addServer( "s8", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[37] = network.addServer( "s1", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
		servers[24] = network.addServer( "s21", new ServiceCurve( "SC{(0.0,0.0),10000.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, true, true );
	}

	public static void createLinks1() throws Exception {
		network.addLink( "l38", servers[29], servers[11] );
		network.addLink( "l21", servers[8], servers[0] );
		network.addLink( "l18", servers[9], servers[24] );
		network.addLink( "l52", servers[6], servers[32] );
		network.addLink( "l49", servers[26], servers[33] );
		network.addLink( "l24", servers[25], servers[31] );
		network.addLink( "l9", servers[16], servers[27] );
		network.addLink( "l17", servers[9], servers[10] );
		network.addLink( "l48", servers[22], servers[28] );
		network.addLink( "l0", servers[17], servers[20] );
		network.addLink( "l27", servers[21], servers[33] );
		network.addLink( "l5", servers[20], servers[36] );
		network.addLink( "l1", servers[17], servers[22] );
		network.addLink( "l46", servers[3], servers[16] );
		network.addLink( "l26", servers[13], servers[25] );
		network.addLink( "l23", servers[25], servers[3] );
		network.addLink( "l20", servers[37], servers[12] );
		network.addLink( "l35", servers[5], servers[16] );
		network.addLink( "l10", servers[19], servers[35] );
		network.addLink( "l28", servers[21], servers[20] );
		network.addLink( "l25", servers[11], servers[21] );
		network.addLink( "l53", servers[12], servers[23] );
		network.addLink( "l15", servers[7], servers[30] );
		network.addLink( "l6", servers[24], servers[33] );
		network.addLink( "l39", servers[2], servers[28] );
		network.addLink( "l2", servers[17], servers[13] );
		network.addLink( "l45", servers[3], servers[14] );
		network.addLink( "l43", servers[30], servers[31] );
		network.addLink( "l36", servers[15], servers[2] );
		network.addLink( "l41", servers[0], servers[4] );
		network.addLink( "l47", servers[22], servers[0] );
		network.addLink( "l30", servers[10], servers[37] );
		network.addLink( "l8", servers[24], servers[13] );
		network.addLink( "l12", servers[19], servers[30] );
		network.addLink( "l37", servers[29], servers[3] );
		network.addLink( "l11", servers[19], servers[14] );
		network.addLink( "l3", servers[34], servers[19] );
		network.addLink( "l14", servers[7], servers[35] );
		network.addLink( "l16", servers[7], servers[16] );
		network.addLink( "l29", servers[21], servers[22] );
		network.addLink( "l44", servers[3], servers[35] );
		network.addLink( "l42", servers[30], servers[11] );
		network.addLink( "l34", servers[5], servers[30] );
		network.addLink( "l31", servers[18], servers[36] );
		network.addLink( "l33", servers[5], servers[14] );
		network.addLink( "l40", servers[2], servers[26] );
		network.addLink( "l50", servers[26], servers[20] );
		network.addLink( "l51", servers[26], servers[13] );
		network.addLink( "l7", servers[24], servers[22] );
		network.addLink( "l32", servers[18], servers[24] );
		network.addLink( "l4", servers[20], servers[10] );
		network.addLink( "l19", servers[32], servers[18] );
		network.addLink( "l22", servers[8], servers[26] );
		network.addLink( "l13", servers[1], servers[6] );
	}

	public static void createFlows1() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f130", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f36", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f131", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f24", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		network.addFlow( "f105", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f55", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f133", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f114", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f78", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f150", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f70", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f104", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f129", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f124", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f100", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f32", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f136", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f88", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f11", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f82", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f108", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		network.addFlow( "f99", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f118", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f26", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f143", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f127", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f135", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f52", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f140", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f117", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f38", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f72", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		network.addFlow( "f54", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f20", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f50", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f86", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f33", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		network.addFlow( "f58", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f81", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f17", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		network.addFlow( "f47", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f44", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		network.addFlow( "f31", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f147", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f14", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f116", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f141", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f126", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f111", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f90", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f93", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f102", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f134", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f40", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f84", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f138", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f145", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f15", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f79", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f45", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f2", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f94", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f62", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f142", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f12", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f23", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f65", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f25", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f71", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows2() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f122", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f97", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f1", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f83", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f149", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f103", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f51", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f43", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		network.addFlow( "f13", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f132", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f101", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f22", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f7", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f137", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f28", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f144", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f60", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f73", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f19", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f120", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f80", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[10] );
		network.addFlow( "f96", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f87", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f107", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f123", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f35", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f76", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		network.addFlow( "f119", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		network.addFlow( "f27", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f8", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		network.addFlow( "f139", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f6", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f18", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f125", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f53", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f34", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f3", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f66", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f113", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f148", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f151", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f110", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f109", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f5", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f57", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[9] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f77", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f64", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f9", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f106", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f10", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		network.addFlow( "f128", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f74", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f46", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f41", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f85", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f0", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f59", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f75", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		network.addFlow( "f68", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f4", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		network.addFlow( "f121", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f42", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f91", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f89", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f16", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f98", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f30", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[18] );
		servers_on_path_s.add( servers[24] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f29", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows3() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f56", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f49", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f67", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f63", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f39", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f146", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f61", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[8] );
		servers_on_path_s.add( servers[26] );
		servers_on_path_s.add( servers[13] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f95", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f92", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f112", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f69", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[12] );
		network.addFlow( "f115", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f21", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[20] );
		network.addFlow( "f37", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[21] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f48", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,5.0),5.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public SavedNetwork() {
		try{
			servers = new Server[38];
			network = new Network();
			createServers1();
			createLinks1();
			createFlows1();
			createFlows2();
			createFlows3();
		} catch (Exception e) {
			System.out.println( e.toString() );
		}
	}

	public Network getNetwork() {
		return network;
	}
}