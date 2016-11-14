/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2015, 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
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

import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ArrivalCurve;

import unikl.disco.network.Network;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;

public class SavedNetwork{
	public static Network network;
	private static Server[] servers;
	public static void createServers1() throws Exception {
		servers[24] = network.addServer( "s36", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[13] = network.addServer( "s18", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[16] = network.addServer( "s109", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[11] = network.addServer( "s82", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[36] = network.addServer( "s53", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[38] = network.addServer( "s3", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[114] = network.addServer( "s115", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[134] = network.addServer( "s71", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[28] = network.addServer( "s142", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[148] = network.addServer( "s106", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[115] = network.addServer( "s140", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[136] = network.addServer( "s112", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[87] = network.addServer( "s32", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[30] = network.addServer( "s15", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[122] = network.addServer( "s14", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[117] = network.addServer( "s56", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[146] = network.addServer( "s101", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[100] = network.addServer( "s70", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[133] = network.addServer( "s62", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[51] = network.addServer( "s136", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[124] = network.addServer( "s117", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[147] = network.addServer( "s27", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[35] = network.addServer( "s126", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[20] = network.addServer( "s143", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[92] = network.addServer( "s93", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[0] = network.addServer( "s132", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[12] = network.addServer( "s144", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[37] = network.addServer( "s52", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[60] = network.addServer( "s12", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[112] = network.addServer( "s35", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[76] = network.addServer( "s86", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[80] = network.addServer( "s116", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[143] = network.addServer( "s97", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[121] = network.addServer( "s68", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[10] = network.addServer( "s45", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[14] = network.addServer( "s55", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[111] = network.addServer( "s16", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[40] = network.addServer( "s5", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[83] = network.addServer( "s63", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[27] = network.addServer( "s91", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[91] = network.addServer( "s89", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[125] = network.addServer( "s130", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[132] = network.addServer( "s17", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[123] = network.addServer( "s105", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[77] = network.addServer( "s64", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[42] = network.addServer( "s25", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[96] = network.addServer( "s111", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[144] = network.addServer( "s98", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[31] = network.addServer( "s39", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[5] = network.addServer( "s29", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[58] = network.addServer( "s51", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[7] = network.addServer( "s121", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[61] = network.addServer( "s73", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[29] = network.addServer( "s21", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[15] = network.addServer( "s72", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[56] = network.addServer( "s13", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[94] = network.addServer( "s100", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[70] = network.addServer( "s77", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[102] = network.addServer( "s50", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[98] = network.addServer( "s69", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[67] = network.addServer( "s48", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[65] = network.addServer( "s79", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[55] = network.addServer( "s31", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[106] = network.addServer( "s146", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[68] = network.addServer( "s90", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[4] = network.addServer( "s102", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[97] = network.addServer( "s134", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[43] = network.addServer( "s94", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[118] = network.addServer( "s7", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[52] = network.addServer( "s65", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[48] = network.addServer( "s137", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[19] = network.addServer( "s99", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[90] = network.addServer( "s37", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[32] = network.addServer( "s60", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[149] = network.addServer( "s4", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[128] = network.addServer( "s104", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[145] = network.addServer( "s95", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[126] = network.addServer( "s34", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[75] = network.addServer( "s58", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[39] = network.addServer( "s118", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[119] = network.addServer( "s42", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[127] = network.addServer( "s57", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[63] = network.addServer( "s2", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[138] = network.addServer( "s125", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[57] = network.addServer( "s33", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[84] = network.addServer( "s135", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[66] = network.addServer( "s145", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[110] = network.addServer( "s0", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[85] = network.addServer( "s123", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[54] = network.addServer( "s30", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[71] = network.addServer( "s24", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[135] = network.addServer( "s133", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[93] = network.addServer( "s23", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[45] = network.addServer( "s43", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[22] = network.addServer( "s114", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[64] = network.addServer( "s124", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[23] = network.addServer( "s120", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[74] = network.addServer( "s84", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[129] = network.addServer( "s75", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[142] = network.addServer( "s83", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[109] = network.addServer( "s96", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[139] = network.addServer( "s128", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[99] = network.addServer( "s49", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[53] = network.addServer( "s61", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[49] = network.addServer( "s66", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[107] = network.addServer( "s8", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[59] = network.addServer( "s74", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[79] = network.addServer( "s138", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[21] = network.addServer( "s6", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[8] = network.addServer( "s9", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[69] = network.addServer( "s87", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[25] = network.addServer( "s127", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[2] = network.addServer( "s22", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[44] = network.addServer( "s103", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[82] = network.addServer( "s131", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[116] = network.addServer( "s20", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[131] = network.addServer( "s38", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[72] = network.addServer( "s113", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[103] = network.addServer( "s41", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[113] = network.addServer( "s76", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[47] = network.addServer( "s54", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[9] = network.addServer( "s19", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[17] = network.addServer( "s44", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[101] = network.addServer( "s141", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[104] = network.addServer( "s139", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[46] = network.addServer( "s108", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[78] = network.addServer( "s40", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[140] = network.addServer( "s28", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[73] = network.addServer( "s129", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[108] = network.addServer( "s88", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[95] = network.addServer( "s107", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[1] = network.addServer( "s85", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[18] = network.addServer( "s78", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[137] = network.addServer( "s119", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[81] = network.addServer( "s122", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[89] = network.addServer( "s92", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[88] = network.addServer( "s11", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[120] = network.addServer( "s80", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[3] = network.addServer( "s148", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[130] = network.addServer( "s59", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[62] = network.addServer( "s67", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[34] = network.addServer( "s81", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[41] = network.addServer( "s46", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[105] = network.addServer( "s149", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[6] = network.addServer( "s110", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[33] = network.addServer( "s47", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[50] = network.addServer( "s26", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[86] = network.addServer( "s147", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[141] = network.addServer( "s1", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
		servers[26] = network.addServer( "s10", new ServiceCurve( "SC{(0.0,0.0),0.0;(0.1,0.0),200.0}" ), new MaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.ARBITRARY, false, false );
	}

	public static void createLinks1() throws Exception {
		network.addLink( "l460", servers[100], servers[77] );
		network.addLink( "l107", servers[29], servers[88] );
		network.addLink( "l978", servers[135], servers[23] );
		network.addLink( "l585", servers[74], servers[77] );
		network.addLink( "l397", servers[32], servers[37] );
		network.addLink( "l146", servers[140], servers[50] );
		network.addLink( "l144", servers[140], servers[71] );
		network.addLink( "l36", servers[88], servers[107] );
		network.addLink( "l64", servers[111], servers[8] );
		network.addLink( "l95", servers[116], servers[132] );
		network.addLink( "l820", servers[22], servers[136] );
		network.addLink( "l391", servers[130], servers[41] );
		network.addLink( "l772", servers[148], servers[144] );
		network.addLink( "l593", servers[1], servers[100] );
		network.addLink( "l306", servers[99], servers[131] );
		network.addLink( "l374", servers[75], servers[117] );
		network.addLink( "l655", servers[89], servers[91] );
		network.addLink( "l989", servers[84], servers[64] );
		network.addLink( "l358", servers[117], servers[99] );
		network.addLink( "l1064", servers[66], servers[0] );
		network.addLink( "l781", servers[46], servers[143] );
		network.addLink( "l451", servers[121], servers[14] );
		network.addLink( "l1086", servers[86], servers[79] );
		network.addLink( "l791", servers[6], servers[43] );
		network.addLink( "l190", servers[126], servers[5] );
		network.addLink( "l228", servers[31], servers[71] );
		network.addLink( "l284", servers[10], servers[45] );
		network.addLink( "l88", servers[9], servers[118] );
		network.addLink( "l790", servers[16], servers[44] );
		network.addLink( "l49", servers[60], servers[21] );
		network.addLink( "l862", servers[137], servers[16] );
		network.addLink( "l695", servers[109], servers[70] );
		network.addLink( "l410", servers[133], servers[36] );
		network.addLink( "l880", servers[81], servers[80] );
		network.addLink( "l402", servers[53], servers[37] );
		network.addLink( "l1031", servers[115], servers[139] );
		network.addLink( "l868", servers[23], servers[46] );
		network.addLink( "l351", servers[14], servers[58] );
		network.addLink( "l346", servers[14], servers[33] );
		network.addLink( "l521", servers[113], servers[100] );
		network.addLink( "l475", servers[134], servers[37] );
		network.addLink( "l267", servers[45], servers[90] );
		network.addLink( "l842", servers[124], servers[72] );
		network.addLink( "l327", servers[36], servers[45] );
		network.addLink( "l732", servers[146], servers[1] );
		network.addLink( "l681", servers[145], servers[69] );
		network.addLink( "l291", servers[41], servers[57] );
		network.addLink( "l52", servers[56], servers[8] );
		network.addLink( "l307", servers[99], servers[5] );
		network.addLink( "l733", servers[146], servers[69] );
		network.addLink( "l529", servers[70], servers[113] );
		network.addLink( "l85", servers[9], servers[30] );
		network.addLink( "l140", servers[140], servers[147] );
		network.addLink( "l713", servers[144], servers[27] );
		network.addLink( "l180", servers[87], servers[116] );
		network.addLink( "l345", servers[14], servers[103] );
		network.addLink( "l254", servers[119], servers[103] );
		network.addLink( "l953", servers[125], servers[64] );
		network.addLink( "l325", servers[37], servers[112] );
		network.addLink( "l688", servers[145], servers[1] );
		network.addLink( "l312", servers[102], servers[31] );
		network.addLink( "l811", servers[72], servers[16] );
		network.addLink( "l189", servers[126], servers[87] );
		network.addLink( "l625", servers[108], servers[15] );
		network.addLink( "l946", servers[73], servers[23] );
		network.addLink( "l818", servers[22], servers[46] );
		network.addLink( "l1023", servers[104], servers[35] );
		network.addLink( "l919", servers[35], servers[64] );
		network.addLink( "l728", servers[94], servers[11] );
		network.addLink( "l216", servers[90], servers[71] );
		network.addLink( "l738", servers[4], servers[91] );
		network.addLink( "l1098", servers[105], servers[3] );
		network.addLink( "l126", servers[42], servers[111] );
		network.addLink( "l584", servers[74], servers[98] );
		network.addLink( "l561", servers[34], servers[62] );
		network.addLink( "l753", servers[44], servers[27] );
		network.addLink( "l873", servers[7], servers[137] );
		network.addLink( "l326", servers[36], servers[57] );
		network.addLink( "l357", servers[117], servers[33] );
		network.addLink( "l913", servers[35], servers[81] );
		network.addLink( "l523", servers[113], servers[53] );
		network.addLink( "l581", servers[74], servers[59] );
		network.addLink( "l1036", servers[101], servers[97] );
		network.addLink( "l851", servers[39], servers[96] );
		network.addLink( "l537", servers[18], servers[129] );
		network.addLink( "l422", servers[83], servers[47] );
		network.addLink( "l804", servers[136], servers[6] );
		network.addLink( "l611", servers[69], servers[129] );
		network.addLink( "l556", servers[34], servers[49] );
		network.addLink( "l1020", servers[79], servers[73] );
		network.addLink( "l707", servers[143], servers[74] );
		network.addLink( "l938", servers[139], servers[85] );
		network.addLink( "l639", servers[68], servers[100] );
		network.addLink( "l279", servers[10], servers[119] );
		network.addLink( "l272", servers[17], servers[78] );
		network.addLink( "l365", servers[117], servers[47] );
		network.addLink( "l135", servers[147], servers[116] );
		network.addLink( "l1016", servers[79], servers[82] );
		network.addLink( "l594", servers[1], servers[129] );
		network.addLink( "l598", servers[1], servers[74] );
		network.addLink( "l599", servers[1], servers[113] );
		network.addLink( "l12", servers[118], servers[141] );
		network.addLink( "l737", servers[4], servers[89] );
		network.addLink( "l74", servers[13], servers[60] );
		network.addLink( "l571", servers[11], servers[120] );
		network.addLink( "l889", servers[64], servers[124] );
		network.addLink( "l743", servers[4], servers[92] );
		network.addLink( "l203", servers[112], servers[54] );
		network.addLink( "l644", servers[27], servers[1] );
		network.addLink( "l320", servers[58], servers[45] );
		network.addLink( "l954", servers[125], servers[23] );
		network.addLink( "l304", servers[67], servers[119] );
		network.addLink( "l1039", servers[101], servers[48] );
		network.addLink( "l810", servers[136], servers[16] );
		network.addLink( "l785", servers[16], servers[4] );
		network.addLink( "l1081", servers[106], servers[115] );
		network.addLink( "l751", servers[44], servers[1] );
		network.addLink( "l638", servers[68], servers[61] );
		network.addLink( "l865", servers[23], servers[124] );
		network.addLink( "l987", servers[84], servers[125] );
		network.addLink( "l994", servers[84], servers[85] );
		network.addLink( "l884", servers[85], servers[114] );
		network.addLink( "l660", servers[92], servers[68] );
		network.addLink( "l400", servers[53], servers[41] );
		network.addLink( "l252", servers[119], servers[87] );
		network.addLink( "l787", servers[16], servers[143] );
		network.addLink( "l184", servers[57], servers[5] );
		network.addLink( "l63", servers[111], servers[141] );
		network.addLink( "l996", servers[84], servers[23] );
		network.addLink( "l704", servers[143], servers[142] );
		network.addLink( "l939", servers[139], servers[7] );
		network.addLink( "l544", servers[18], servers[75] );
		network.addLink( "l568", servers[11], servers[61] );
		network.addLink( "l934", servers[139], servers[22] );
		network.addLink( "l194", servers[126], servers[55] );
		network.addLink( "l951", servers[73], servers[85] );
		network.addLink( "l779", servers[95], servers[43] );
		network.addLink( "l542", servers[18], servers[83] );
		network.addLink( "l1001", servers[51], servers[97] );
		network.addLink( "l922", servers[25], servers[81] );
		network.addLink( "l499", servers[59], servers[62] );
		network.addLink( "l46", servers[60], servers[141] );
		network.addLink( "l243", servers[78], servers[131] );
		network.addLink( "l1027", servers[104], servers[23] );
		network.addLink( "l404", servers[53], servers[67] );
		network.addLink( "l809", servers[136], servers[4] );
		network.addLink( "l1007", servers[48], servers[97] );
		network.addLink( "l709", servers[143], servers[34] );
		network.addLink( "l1010", servers[48], servers[35] );
		network.addLink( "l988", servers[84], servers[82] );
		network.addLink( "l138", servers[140], servers[9] );
		network.addLink( "l347", servers[14], servers[45] );
		network.addLink( "l774", servers[148], servers[123] );
		network.addLink( "l558", servers[34], servers[70] );
		network.addLink( "l237", servers[78], servers[31] );
		network.addLink( "l1028", servers[115], servers[0] );
		network.addLink( "l37", servers[88], servers[26] );
		network.addLink( "l981", servers[135], servers[139] );
		network.addLink( "l1084", servers[86], servers[66] );
		network.addLink( "l969", servers[0], servers[64] );
		network.addLink( "l803", servers[136], servers[146] );
		network.addLink( "l578", servers[142], servers[62] );
		network.addLink( "l858", servers[137], servers[94] );
		network.addLink( "l319", servers[58], servers[67] );
		network.addLink( "l18", servers[8], servers[107] );
		network.addLink( "l110", servers[2], servers[116] );
		network.addLink( "l61", servers[30], servers[110] );
		network.addLink( "l1091", servers[3], servers[97] );
		network.addLink( "l693", servers[109], servers[11] );
		network.addLink( "l350", servers[14], servers[10] );
		network.addLink( "l970", servers[0], servers[72] );
		network.addLink( "l857", servers[137], servers[148] );
		network.addLink( "l805", servers[136], servers[94] );
		network.addLink( "l600", servers[1], servers[34] );
		network.addLink( "l31", servers[88], servers[40] );
		network.addLink( "l944", servers[73], servers[80] );
		network.addLink( "l15", servers[107], servers[40] );
		network.addLink( "l386", servers[130], servers[37] );
		network.addLink( "l368", servers[127], servers[119] );
		network.addLink( "l323", servers[37], servers[33] );
		network.addLink( "l153", servers[5], servers[140] );
		network.addLink( "l932", servers[139], servers[23] );
		network.addLink( "l826", servers[114], servers[22] );
		network.addLink( "l461", servers[100], servers[127] );
		network.addLink( "l421", servers[83], servers[14] );
		network.addLink( "l114", servers[93], servers[111] );
		network.addLink( "l1011", servers[48], servers[85] );
		network.addLink( "l1074", servers[106], servers[51] );
		network.addLink( "l285", servers[10], servers[126] );
		network.addLink( "l200", servers[112], servers[93] );
		network.addLink( "l931", servers[25], servers[85] );
		network.addLink( "l411", servers[133], servers[102] );
		network.addLink( "l945", servers[73], servers[136] );
		network.addLink( "l1033", servers[115], servers[97] );
		network.addLink( "l1085", servers[86], servers[12] );
		network.addLink( "l378", servers[75], servers[41] );
		network.addLink( "l340", servers[47], servers[67] );
		network.addLink( "l985", servers[97], servers[64] );
		network.addLink( "l726", servers[19], servers[109] );
		network.addLink( "l723", servers[19], servers[145] );
		network.addLink( "l8", servers[118], servers[40] );
		network.addLink( "l612", servers[69], servers[113] );
		network.addLink( "l846", servers[124], servers[128] );
		network.addLink( "l634", servers[91], servers[113] );
		network.addLink( "l363", servers[117], servers[102] );
		network.addLink( "l454", servers[98], servers[133] );
		network.addLink( "l1071", servers[106], servers[125] );
		network.addLink( "l678", servers[145], servers[92] );
		network.addLink( "l33", servers[88], servers[110] );
		network.addLink( "l507", servers[129], servers[100] );
		network.addLink( "l539", servers[18], servers[59] );
		network.addLink( "l986", servers[84], servers[114] );
		network.addLink( "l128", servers[50], servers[118] );
		network.addLink( "l229", servers[31], servers[126] );
		network.addLink( "l1073", servers[106], servers[97] );
		network.addLink( "l967", servers[82], servers[138] );
		network.addLink( "l900", servers[64], servers[80] );
		network.addLink( "l897", servers[64], servers[22] );
		network.addLink( "l686", servers[145], servers[11] );
		network.addLink( "l246", servers[78], servers[42] );
		network.addLink( "l974", servers[0], servers[82] );
		network.addLink( "l213", servers[90], servers[5] );
		network.addLink( "l614", servers[69], servers[142] );
		network.addLink( "l1009", servers[48], servers[125] );
		network.addLink( "l719", servers[144], servers[92] );
		network.addLink( "l827", servers[114], servers[145] );
		network.addLink( "l382", servers[130], servers[31] );
		network.addLink( "l720", servers[19], servers[27] );
		network.addLink( "l242", servers[78], servers[55] );
		network.addLink( "l102", servers[29], servers[13] );
		network.addLink( "l1004", servers[51], servers[125] );
		network.addLink( "l62", servers[111], servers[56] );
		network.addLink( "l921", servers[35], servers[85] );
		network.addLink( "l262", servers[45], servers[57] );
		network.addLink( "l727", servers[94], servers[92] );
		network.addLink( "l559", servers[34], servers[120] );
		network.addLink( "l958", servers[125], servers[137] );
		network.addLink( "l98", servers[116], servers[40] );
		network.addLink( "l923", servers[25], servers[114] );
		network.addLink( "l892", servers[64], servers[6] );
		network.addLink( "l162", servers[54], servers[29] );
		network.addLink( "l198", servers[126], servers[2] );
		network.addLink( "l406", servers[53], servers[10] );
		network.addLink( "l835", servers[80], servers[95] );
		network.addLink( "l866", servers[23], servers[136] );
		network.addLink( "l446", servers[121], servers[52] );
		network.addLink( "l1100", servers[105], servers[115] );
		network.addLink( "l1050", servers[20], servers[64] );
		network.addLink( "l859", servers[137], servers[19] );
		network.addLink( "l569", servers[11], servers[98] );
		network.addLink( "l550", servers[65], servers[49] );
		network.addLink( "l182", servers[57], servers[93] );
		network.addLink( "l60", servers[30], servers[8] );
		network.addLink( "l1088", servers[3], servers[101] );
		network.addLink( "l11", servers[118], servers[149] );
		network.addLink( "l489", servers[61], servers[75] );
		network.addLink( "l950", servers[73], servers[138] );
		network.addLink( "l528", servers[113], servers[15] );
		network.addLink( "l947", servers[73], servers[64] );
		network.addLink( "l722", servers[19], servers[91] );
		network.addLink( "l481", servers[61], servers[130] );
		network.addLink( "l342", servers[47], servers[99] );
		network.addLink( "l659", servers[92], servers[113] );
		network.addLink( "l119", servers[71], servers[122] );
		network.addLink( "l729", servers[94], servers[27] );
		network.addLink( "l1022", servers[104], servers[79] );
		network.addLink( "l1092", servers[3], servers[104] );
		network.addLink( "l674", servers[43], servers[74] );
		network.addLink( "l58", servers[30], servers[149] );
		network.addLink( "l632", servers[91], servers[129] );
		network.addLink( "l456", servers[98], servers[130] );
		network.addLink( "l176", servers[87], servers[13] );
		network.addLink( "l133", servers[147], servers[56] );
		network.addLink( "l875", servers[7], servers[4] );
		network.addLink( "l854", servers[39], servers[124] );
		network.addLink( "l120", servers[71], servers[88] );
		network.addLink( "l401", servers[53], servers[47] );
		network.addLink( "l226", servers[31], servers[54] );
		network.addLink( "l998", servers[51], servers[82] );
		network.addLink( "l93", servers[116], servers[56] );
		network.addLink( "l313", servers[102], servers[45] );
		network.addLink( "l793", servers[6], servers[46] );
		network.addLink( "l424", servers[77], servers[36] );
		network.addLink( "l250", servers[103], servers[90] );
		network.addLink( "l483", servers[61], servers[121] );
		network.addLink( "l423", servers[83], servers[102] );
		network.addLink( "l546", servers[18], servers[113] );
		network.addLink( "l1063", servers[66], servers[104] );
		network.addLink( "l812", servers[72], servers[44] );
		network.addLink( "l1014", servers[48], servers[25] );
		network.addLink( "l192", servers[126], servers[71] );
		network.addLink( "l813", servers[72], servers[128] );
		network.addLink( "l220", servers[131], servers[54] );
		network.addLink( "l109", servers[2], servers[118] );
		network.addLink( "l51", servers[56], servers[118] );
		network.addLink( "l214", servers[90], servers[50] );
		network.addLink( "l656", servers[89], servers[76] );
		network.addLink( "l765", servers[123], servers[94] );
		network.addLink( "l270", servers[17], servers[90] );
		network.addLink( "l1061", servers[66], servers[115] );
		network.addLink( "l288", servers[41], servers[45] );
		network.addLink( "l982", servers[135], servers[81] );
		network.addLink( "l788", servers[16], servers[128] );
		network.addLink( "l1051", servers[20], servers[138] );
		network.addLink( "l855", servers[39], servers[72] );
		network.addLink( "l105", servers[29], servers[30] );
		network.addLink( "l524", servers[113], servers[83] );
		network.addLink( "l856", servers[39], servers[123] );
		network.addLink( "l222", servers[131], servers[29] );
		network.addLink( "l673", servers[43], servers[68] );
		network.addLink( "l806", servers[136], servers[144] );
		network.addLink( "l324", servers[37], servers[24] );
		network.addLink( "l872", servers[7], servers[136] );
		network.addLink( "l494", servers[59], servers[14] );
		network.addLink( "l776", servers[148], servers[27] );
		network.addLink( "l111", servers[2], servers[88] );
		network.addLink( "l680", servers[145], servers[76] );
		network.addLink( "l685", servers[145], servers[120] );
		network.addLink( "l334", servers[36], servers[37] );
		network.addLink( "l948", servers[73], servers[39] );
		network.addLink( "l467", servers[134], servers[130] );
		network.addLink( "l82", servers[9], servers[111] );
		network.addLink( "l876", servers[7], servers[114] );
		network.addLink( "l329", servers[36], servers[67] );
		network.addLink( "l42", servers[60], servers[110] );
		network.addLink( "l964", servers[82], servers[35] );
		network.addLink( "l34", servers[88], servers[63] );
		network.addLink( "l799", servers[96], servers[109] );
		network.addLink( "l749", servers[44], servers[68] );
		network.addLink( "l604", servers[76], servers[34] );
		network.addLink( "l718", servers[144], servers[76] );
		network.addLink( "l771", servers[148], servers[44] );
		network.addLink( "l754", servers[44], servers[91] );
		network.addLink( "l151", servers[5], servers[9] );
		network.addLink( "l219", servers[90], servers[42] );
		network.addLink( "l1078", servers[106], servers[104] );
		network.addLink( "l427", servers[77], servers[75] );
		network.addLink( "l672", servers[43], servers[76] );
		network.addLink( "l1037", servers[101], servers[25] );
		network.addLink( "l129", servers[50], servers[56] );
		network.addLink( "l690", servers[109], servers[108] );
		network.addLink( "l1034", servers[115], servers[125] );
		network.addLink( "l1047", servers[28], servers[79] );
		network.addLink( "l1070", servers[66], servers[20] );
		network.addLink( "l113", servers[93], servers[60] );
		network.addLink( "l535", servers[18], servers[52] );
		network.addLink( "l711", servers[143], servers[109] );
		network.addLink( "l603", servers[1], servers[120] );
		network.addLink( "l959", servers[82], servers[23] );
		network.addLink( "l278", servers[17], servers[42] );
		network.addLink( "l43", servers[60], servers[88] );
		network.addLink( "l134", servers[147], servers[26] );
		network.addLink( "l75", servers[13], servers[132] );
		network.addLink( "l258", servers[119], servers[112] );
		network.addLink( "l302", servers[67], servers[41] );
		network.addLink( "l830", servers[114], servers[4] );
		network.addLink( "l1043", servers[101], servers[73] );
		network.addLink( "l782", servers[46], servers[92] );
		network.addLink( "l118", servers[71], servers[40] );
		network.addLink( "l575", servers[142], servers[61] );
		network.addLink( "l425", servers[77], servers[99] );
		network.addLink( "l1068", servers[66], servers[28] );
		network.addLink( "l253", servers[119], servers[140] );
		network.addLink( "l619", servers[108], servers[69] );
		network.addLink( "l852", servers[39], servers[114] );
		network.addLink( "l631", servers[91], servers[1] );
		network.addLink( "l1053", servers[12], servers[104] );
		network.addLink( "l362", servers[117], servers[103] );
		network.addLink( "l980", servers[135], servers[114] );
		network.addLink( "l574", servers[11], servers[15] );
		network.addLink( "l708", servers[143], servers[68] );
		network.addLink( "l132", servers[147], servers[122] );
		network.addLink( "l149", servers[5], servers[56] );
		network.addLink( "l1057", servers[12], servers[79] );
		network.addLink( "l355", servers[14], servers[17] );
		network.addLink( "l210", servers[90], servers[126] );
		network.addLink( "l39", servers[60], servers[40] );
		network.addLink( "l983", servers[97], servers[138] );
		network.addLink( "l66", servers[111], servers[40] );
		network.addLink( "l840", servers[80], servers[136] );
		network.addLink( "l289", servers[41], servers[55] );
		network.addLink( "l371", servers[127], servers[102] );
		network.addLink( "l488", servers[61], servers[52] );
		network.addLink( "l602", servers[1], servers[59] );
		network.addLink( "l1060", servers[66], servers[125] );
		network.addLink( "l435", servers[52], servers[102] );
		network.addLink( "l150", servers[5], servers[50] );
		network.addLink( "l238", servers[78], servers[71] );
		network.addLink( "l297", servers[33], servers[45] );
		network.addLink( "l377", servers[75], servers[45] );
		network.addLink( "l72", servers[13], servers[38] );
		network.addLink( "l801", servers[136], servers[128] );
		network.addLink( "l224", servers[131], servers[42] );
		network.addLink( "l533", servers[70], servers[98] );
		network.addLink( "l904", servers[138], servers[46] );
		network.addLink( "l756", servers[128], servers[4] );
		network.addLink( "l917", servers[35], servers[124] );
		network.addLink( "l786", servers[16], servers[19] );
		network.addLink( "l817", servers[22], servers[4] );
		network.addLink( "l444", servers[62], servers[47] );
		network.addLink( "l819", servers[22], servers[109] );
		network.addLink( "l853", servers[39], servers[46] );
		network.addLink( "l1069", servers[66], servers[101] );
		network.addLink( "l651", servers[89], servers[142] );
		network.addLink( "l515", servers[129], servers[133] );
		network.addLink( "l734", servers[146], servers[43] );
		network.addLink( "l622", servers[108], servers[113] );
		network.addLink( "l784", servers[46], servers[128] );
		network.addLink( "l69", servers[132], servers[60] );
		network.addLink( "l516", servers[129], servers[53] );
		network.addLink( "l394", servers[32], servers[102] );
		network.addLink( "l906", servers[138], servers[7] );
		network.addLink( "l20", servers[8], servers[38] );
		network.addLink( "l874", servers[7], servers[124] );
		network.addLink( "l476", servers[134], servers[133] );
		network.addLink( "l44", servers[60], servers[107] );
		network.addLink( "l136", servers[147], servers[93] );
		network.addLink( "l501", servers[59], servers[61] );
		network.addLink( "l464", servers[100], servers[83] );
		network.addLink( "l1075", servers[106], servers[0] );
		network.addLink( "l767", servers[148], servers[128] );
		network.addLink( "l527", servers[113], servers[59] );
		network.addLink( "l175", servers[87], servers[30] );
		network.addLink( "l783", servers[46], servers[19] );
		network.addLink( "l1000", servers[51], servers[135] );
		network.addLink( "l903", servers[138], servers[136] );
		network.addLink( "l196", servers[126], servers[147] );
		network.addLink( "l438", servers[49], servers[32] );
		network.addLink( "l115", servers[93], servers[2] );
		network.addLink( "l750", servers[44], servers[69] );
		network.addLink( "l343", servers[47], servers[24] );
		network.addLink( "l78", servers[9], servers[8] );
		network.addLink( "l844", servers[124], servers[96] );
		network.addLink( "l724", servers[19], servers[92] );
		network.addLink( "l504", servers[59], servers[52] );
		network.addLink( "l241", servers[78], servers[87] );
		network.addLink( "l816", servers[22], servers[94] );
		network.addLink( "l778", servers[95], servers[68] );
		network.addLink( "l21", servers[8], servers[40] );
		network.addLink( "l79", servers[9], servers[26] );
		network.addLink( "l308", servers[99], servers[55] );
		network.addLink( "l847", servers[39], servers[95] );
		network.addLink( "l606", servers[76], servers[129] );
		network.addLink( "l645", servers[27], servers[68] );
		network.addLink( "l1097", servers[3], servers[135] );
		network.addLink( "l240", servers[78], servers[116] );
		network.addLink( "l583", servers[74], servers[70] );
		network.addLink( "l692", servers[109], servers[34] );
		network.addLink( "l973", servers[0], servers[25] );
		network.addLink( "l509", servers[129], servers[61] );
		network.addLink( "l1082", servers[106], servers[48] );
		network.addLink( "l428", servers[77], servers[102] );
		network.addLink( "l505", servers[129], servers[62] );
		network.addLink( "l530", servers[70], servers[62] );
		network.addLink( "l29", servers[26], servers[141] );
		network.addLink( "l984", servers[97], servers[125] );
		network.addLink( "l702", servers[143], servers[43] );
		network.addLink( "l67", servers[132], servers[118] );
		network.addLink( "l766", servers[123], servers[4] );
		network.addLink( "l577", servers[142], servers[18] );
		network.addLink( "l640", servers[68], servers[108] );
		network.addLink( "l147", servers[5], servers[30] );
		network.addLink( "l14", servers[107], servers[63] );
		network.addLink( "l385", servers[130], servers[17] );
		network.addLink( "l908", servers[138], servers[137] );
		network.addLink( "l696", servers[109], servers[92] );
		network.addLink( "l877", servers[81], servers[95] );
		network.addLink( "l202", servers[112], servers[87] );
		network.addLink( "l233", servers[31], servers[140] );
		network.addLink( "l580", servers[142], servers[70] );
		network.addLink( "l883", servers[85], servers[22] );
		network.addLink( "l979", servers[135], servers[73] );
		network.addLink( "l628", servers[91], servers[108] );
		network.addLink( "l1030", servers[115], servers[82] );
		network.addLink( "l145", servers[140], servers[13] );
		network.addLink( "l582", servers[74], servers[15] );
		network.addLink( "l407", servers[53], servers[45] );
		network.addLink( "l658", servers[89], servers[69] );
		network.addLink( "l635", servers[91], servers[15] );
		network.addLink( "l90", servers[116], servers[111] );
		network.addLink( "l1021", servers[79], servers[97] );
		network.addLink( "l928", servers[25], servers[7] );
		network.addLink( "l383", servers[130], servers[127] );
		network.addLink( "l141", servers[140], servers[93] );
		network.addLink( "l968", servers[82], servers[139] );
		network.addLink( "l648", servers[27], servers[70] );
		network.addLink( "l770", servers[148], servers[146] );
		network.addLink( "l615", servers[69], servers[15] );
		network.addLink( "l762", servers[123], servers[145] );
		network.addLink( "l795", servers[96], servers[128] );
		network.addLink( "l937", servers[139], servers[137] );
		network.addLink( "l449", servers[121], servers[77] );
		network.addLink( "l966", servers[82], servers[85] );
		network.addLink( "l538", servers[18], servers[100] );
		network.addLink( "l445", servers[62], servers[52] );
		network.addLink( "l480", servers[15], servers[52] );
		network.addLink( "l926", servers[25], servers[72] );
		network.addLink( "l1048", servers[28], servers[125] );
		network.addLink( "l170", servers[55], servers[111] );
		network.addLink( "l796", servers[96], servers[148] );
	}

	public static void createLinks2() throws Exception {
		network.addLink( "l764", servers[123], servers[44] );
		network.addLink( "l185", servers[57], servers[2] );
		network.addLink( "l16", servers[107], servers[38] );
		network.addLink( "l833", servers[80], servers[4] );
		network.addLink( "l225", servers[131], servers[2] );
		network.addLink( "l104", servers[29], servers[118] );
		network.addLink( "l199", servers[112], servers[140] );
		network.addLink( "l514", servers[129], servers[15] );
		network.addLink( "l610", servers[76], servers[61] );
		network.addLink( "l1", servers[21], servers[38] );
		network.addLink( "l1067", servers[66], servers[97] );
		network.addLink( "l597", servers[1], servers[98] );
		network.addLink( "l143", servers[140], servers[107] );
		network.addLink( "l991", servers[84], servers[135] );
		network.addLink( "l139", servers[140], servers[132] );
		network.addLink( "l364", servers[117], servers[14] );
		network.addLink( "l832", servers[114], servers[95] );
		network.addLink( "l235", servers[31], servers[147] );
		network.addLink( "l432", servers[52], servers[33] );
		network.addLink( "l701", servers[143], servers[1] );
		network.addLink( "l474", servers[134], servers[53] );
		network.addLink( "l526", servers[113], servers[61] );
		network.addLink( "l395", servers[32], servers[36] );
		network.addLink( "l641", servers[68], servers[69] );
		network.addLink( "l775", servers[148], servers[109] );
		network.addLink( "l777", servers[148], servers[92] );
		network.addLink( "l1006", servers[48], servers[138] );
		network.addLink( "l293", servers[41], servers[78] );
		network.addLink( "l413", servers[133], servers[127] );
		network.addLink( "l965", servers[82], servers[72] );
		network.addLink( "l330", servers[36], servers[119] );
		network.addLink( "l367", servers[127], servers[41] );
		network.addLink( "l3", servers[21], servers[63] );
		network.addLink( "l925", servers[25], servers[124] );
		network.addLink( "l773", servers[148], servers[43] );
		network.addLink( "l221", servers[131], servers[112] );
		network.addLink( "l705", servers[143], servers[76] );
		network.addLink( "l466", servers[134], servers[127] );
		network.addLink( "l381", servers[75], servers[10] );
		network.addLink( "l396", servers[32], servers[41] );
		network.addLink( "l204", servers[24], servers[29] );
		network.addLink( "l898", servers[64], servers[136] );
		network.addLink( "l668", servers[92], servers[65] );
		network.addLink( "l739", servers[4], servers[43] );
		network.addLink( "l864", servers[23], servers[95] );
		network.addLink( "l564", servers[34], servers[100] );
		network.addLink( "l211", servers[90], servers[54] );
		network.addLink( "l337", servers[47], servers[58] );
		network.addLink( "l661", servers[92], servers[11] );
		network.addLink( "l960", servers[82], servers[137] );
		network.addLink( "l94", servers[116], servers[122] );
		network.addLink( "l930", servers[25], servers[137] );
		network.addLink( "l808", servers[136], servers[96] );
		network.addLink( "l824", servers[114], servers[146] );
		network.addLink( "l995", servers[84], servers[137] );
		network.addLink( "l152", servers[5], servers[122] );
		network.addLink( "l157", servers[54], servers[71] );
		network.addLink( "l789", servers[16], servers[46] );
		network.addLink( "l23", servers[8], servers[63] );
		network.addLink( "l1041", servers[101], servers[7] );
		network.addLink( "l160", servers[54], servers[50] );
		network.addLink( "l534", servers[70], servers[52] );
		network.addLink( "l683", servers[145], servers[43] );
		network.addLink( "l689", servers[145], servers[34] );
		network.addLink( "l86", servers[9], servers[13] );
		network.addLink( "l1089", servers[3], servers[28] );
		network.addLink( "l955", servers[125], servers[73] );
		network.addLink( "l403", servers[53], servers[99] );
		network.addLink( "l2", servers[21], servers[40] );
		network.addLink( "l315", servers[102], servers[103] );
		network.addLink( "l821", servers[22], servers[146] );
		network.addLink( "l997", servers[84], servers[81] );
		network.addLink( "l71", servers[13], servers[111] );
		network.addLink( "l73", servers[13], servers[8] );
		network.addLink( "l747", servers[44], servers[4] );
		network.addLink( "l1072", servers[106], servers[84] );
		network.addLink( "l520", servers[113], servers[49] );
		network.addLink( "l623", servers[108], servers[76] );
		network.addLink( "l882", servers[85], servers[23] );
		network.addLink( "l609", servers[76], servers[74] );
		network.addLink( "l236", servers[31], servers[2] );
		network.addLink( "l495", servers[59], servers[77] );
		network.addLink( "l548", servers[65], servers[18] );
		network.addLink( "l283", servers[10], servers[131] );
		network.addLink( "l167", servers[55], servers[140] );
		network.addLink( "l977", servers[135], servers[137] );
		network.addLink( "l9", servers[118], servers[21] );
		network.addLink( "l81", servers[9], servers[56] );
		network.addLink( "l468", servers[134], servers[32] );
		network.addLink( "l943", servers[73], servers[72] );
		network.addLink( "l630", servers[91], servers[34] );
		network.addLink( "l336", servers[47], servers[131] );
		network.addLink( "l490", servers[61], servers[83] );
		network.addLink( "l907", servers[138], servers[80] );
		network.addLink( "l698", servers[109], servers[69] );
		network.addLink( "l886", servers[85], servers[72] );
		network.addLink( "l665", servers[92], servers[61] );
		network.addLink( "l13", servers[107], servers[141] );
		network.addLink( "l1002", servers[51], servers[64] );
		network.addLink( "l637", servers[68], servers[113] );
		network.addLink( "l798", servers[96], servers[144] );
		network.addLink( "l933", servers[139], servers[138] );
		network.addLink( "l294", servers[33], servers[41] );
		network.addLink( "l418", servers[133], servers[117] );
		network.addLink( "l273", servers[17], servers[45] );
		network.addLink( "l867", servers[23], servers[16] );
		network.addLink( "l745", servers[44], servers[19] );
		network.addLink( "l976", servers[135], servers[125] );
		network.addLink( "l482", servers[61], servers[100] );
		network.addLink( "l643", servers[68], servers[142] );
		network.addLink( "l1005", servers[48], servers[124] );
		network.addLink( "l924", servers[25], servers[22] );
		network.addLink( "l379", servers[75], servers[14] );
		network.addLink( "l281", servers[10], servers[78] );
		network.addLink( "l484", servers[61], servers[53] );
		network.addLink( "l163", servers[54], servers[26] );
		network.addLink( "l338", servers[47], servers[10] );
		network.addLink( "l419", servers[133], servers[33] );
		network.addLink( "l218", servers[90], servers[57] );
		network.addLink( "l586", servers[74], servers[65] );
		network.addLink( "l259", servers[119], servers[147] );
		network.addLink( "l333", servers[36], servers[102] );
		network.addLink( "l441", servers[62], servers[83] );
		network.addLink( "l794", servers[6], servers[44] );
		network.addLink( "l881", servers[81], servers[123] );
		network.addLink( "l920", servers[35], servers[137] );
		network.addLink( "l309", servers[99], servers[78] );
		network.addLink( "l245", servers[78], servers[5] );
		network.addLink( "l205", servers[24], servers[50] );
		network.addLink( "l721", servers[19], servers[69] );
		network.addLink( "l239", servers[78], servers[50] );
		network.addLink( "l510", servers[129], servers[77] );
		network.addLink( "l618", servers[108], servers[11] );
		network.addLink( "l496", servers[59], servers[121] );
		network.addLink( "l286", servers[10], servers[54] );
		network.addLink( "l10", servers[118], servers[63] );
		network.addLink( "l901", servers[138], servers[123] );
		network.addLink( "l339", servers[47], servers[37] );
		network.addLink( "l543", servers[18], servers[98] );
		network.addLink( "l592", servers[74], servers[129] );
		network.addLink( "l390", servers[130], servers[67] );
		network.addLink( "l142", servers[140], servers[116] );
		network.addLink( "l311", servers[102], servers[33] );
		network.addLink( "l206", servers[24], servers[57] );
		network.addLink( "l879", servers[81], servers[136] );
		network.addLink( "l911", servers[138], servers[64] );
		network.addLink( "l531", servers[70], servers[133] );
		network.addLink( "l1032", servers[115], servers[73] );
		network.addLink( "l97", servers[116], servers[21] );
		network.addLink( "l759", servers[128], servers[43] );
		network.addLink( "l1052", servers[20], servers[35] );
		network.addLink( "l65", servers[111], servers[110] );
		network.addLink( "l860", servers[137], servers[46] );
		network.addLink( "l174", servers[55], servers[147] );
		network.addLink( "l797", servers[96], servers[146] );
		network.addLink( "l5", servers[21], servers[141] );
		network.addLink( "l697", servers[109], servers[27] );
		network.addLink( "l621", servers[108], servers[100] );
		network.addLink( "l183", servers[57], servers[71] );
		network.addLink( "l802", servers[136], servers[143] );
		network.addLink( "l373", servers[127], servers[37] );
		network.addLink( "l301", servers[67], servers[45] );
		network.addLink( "l870", servers[7], servers[72] );
		network.addLink( "l839", servers[80], servers[148] );
		network.addLink( "l828", servers[114], servers[144] );
		network.addLink( "l295", servers[33], servers[78] );
		network.addLink( "l620", servers[108], servers[121] );
		network.addLink( "l127", servers[50], servers[26] );
		network.addLink( "l161", servers[54], servers[13] );
		network.addLink( "l261", servers[45], servers[126] );
		network.addLink( "l57", servers[122], servers[107] );
		network.addLink( "l370", servers[127], servers[131] );
		network.addLink( "l260", servers[45], servers[112] );
		network.addLink( "l607", servers[76], servers[65] );
		network.addLink( "l155", servers[5], servers[71] );
		network.addLink( "l369", servers[127], servers[36] );
		network.addLink( "l878", servers[81], servers[23] );
		network.addLink( "l940", servers[73], servers[137] );
		network.addLink( "l439", servers[49], servers[37] );
		network.addLink( "l822", servers[22], servers[128] );
		network.addLink( "l414", servers[133], servers[17] );
		network.addLink( "l829", servers[114], servers[16] );
		network.addLink( "l731", servers[146], servers[92] );
		network.addLink( "l961", servers[82], servers[80] );
		network.addLink( "l154", servers[5], servers[13] );
		network.addLink( "l275", servers[17], servers[112] );
		network.addLink( "l497", servers[59], servers[134] );
		network.addLink( "l393", servers[32], servers[14] );
		network.addLink( "l177", servers[87], servers[2] );
		network.addLink( "l837", servers[80], servers[114] );
		network.addLink( "l276", servers[17], servers[126] );
		network.addLink( "l231", servers[31], servers[50] );
		network.addLink( "l647", servers[27], servers[76] );
		network.addLink( "l642", servers[68], servers[91] );
		network.addLink( "l682", servers[145], servers[142] );
		network.addLink( "l303", servers[67], servers[126] );
		network.addLink( "l360", servers[117], servers[10] );
		network.addLink( "l121", servers[42], servers[118] );
		network.addLink( "l493", servers[59], servers[49] );
		network.addLink( "l588", servers[74], servers[61] );
		network.addLink( "l158", servers[54], servers[2] );
		network.addLink( "l87", servers[9], servers[88] );
		network.addLink( "l589", servers[74], servers[120] );
		network.addLink( "l59", servers[30], servers[107] );
		network.addLink( "l45", servers[60], servers[149] );
		network.addLink( "l993", servers[84], servers[0] );
		network.addLink( "l525", servers[113], servers[62] );
		network.addLink( "l893", servers[64], servers[96] );
		network.addLink( "l50", servers[56], servers[110] );
		network.addLink( "l227", servers[31], servers[55] );
		network.addLink( "l470", servers[134], servers[121] );
		network.addLink( "l902", servers[138], servers[124] );
		network.addLink( "l1095", servers[3], servers[66] );
		network.addLink( "l179", servers[87], servers[54] );
		network.addLink( "l780", servers[95], servers[145] );
		network.addLink( "l399", servers[53], servers[14] );
		network.addLink( "l166", servers[55], servers[2] );
		network.addLink( "l265", servers[45], servers[87] );
		network.addLink( "l1062", servers[66], servers[51] );
		network.addLink( "l915", servers[35], servers[114] );
		network.addLink( "l195", servers[126], servers[57] );
		network.addLink( "l890", servers[64], servers[39] );
		network.addLink( "l547", servers[65], servers[32] );
		network.addLink( "l871", servers[7], servers[95] );
		network.addLink( "l217", servers[90], servers[9] );
		network.addLink( "l32", servers[88], servers[38] );
		network.addLink( "l519", servers[113], servers[98] );
		network.addLink( "l248", servers[103], servers[57] );
		network.addLink( "l918", servers[35], servers[39] );
		network.addLink( "l565", servers[34], servers[15] );
		network.addLink( "l929", servers[25], servers[16] );
		network.addLink( "l124", servers[42], servers[29] );
		network.addLink( "l814", servers[22], servers[19] );
		network.addLink( "l356", servers[117], servers[17] );
		network.addLink( "l387", servers[130], servers[102] );
		network.addLink( "l77", servers[9], servers[132] );
		network.addLink( "l91", servers[116], servers[107] );
		network.addLink( "l112", servers[93], servers[116] );
		network.addLink( "l462", servers[100], servers[37] );
		network.addLink( "l825", servers[114], servers[123] );
		network.addLink( "l375", servers[75], servers[36] );
		network.addLink( "l19", servers[8], servers[110] );
		network.addLink( "l990", servers[84], servers[139] );
		network.addLink( "l760", servers[128], servers[143] );
		network.addLink( "l84", servers[9], servers[107] );
		network.addLink( "l41", servers[60], servers[38] );
		network.addLink( "l1015", servers[79], servers[125] );
		network.addLink( "l101", servers[29], servers[8] );
		network.addLink( "l706", servers[143], servers[89] );
		network.addLink( "l314", servers[102], servers[99] );
		network.addLink( "l392", servers[130], servers[117] );
		network.addLink( "l687", servers[145], servers[74] );
		network.addLink( "l455", servers[98], servers[127] );
		network.addLink( "l748", servers[44], servers[145] );
		network.addLink( "l843", servers[124], servers[146] );
		network.addLink( "l187", servers[57], servers[87] );
		network.addLink( "l888", servers[85], servers[96] );
		network.addLink( "l836", servers[80], servers[128] );
		network.addLink( "l792", servers[6], servers[94] );
		network.addLink( "l388", servers[130], servers[14] );
		network.addLink( "l506", servers[129], servers[59] );
		network.addLink( "l666", servers[92], servers[27] );
		network.addLink( "l122", servers[42], servers[13] );
		network.addLink( "l1044", servers[101], servers[104] );
		network.addLink( "l710", servers[143], servers[27] );
		network.addLink( "l1066", servers[66], servers[135] );
		network.addLink( "l522", servers[113], servers[133] );
		network.addLink( "l498", servers[59], servers[83] );
		network.addLink( "l244", servers[78], servers[126] );
		network.addLink( "l173", servers[55], servers[54] );
		network.addLink( "l545", servers[18], servers[49] );
		network.addLink( "l55", servers[122], servers[38] );
		network.addLink( "l553", servers[120], servers[62] );
		network.addLink( "l7", servers[118], servers[38] );
		network.addLink( "l1077", servers[106], servers[101] );
		network.addLink( "l540", servers[18], servers[134] );
		network.addLink( "l332", servers[36], servers[78] );
		network.addLink( "l508", servers[129], servers[121] );
		network.addLink( "l746", servers[44], servers[92] );
		network.addLink( "l448", servers[121], servers[62] );
		network.addLink( "l417", servers[133], servers[99] );
		network.addLink( "l1076", servers[106], servers[66] );
		network.addLink( "l463", servers[100], servers[62] );
		network.addLink( "l800", servers[136], servers[46] );
		network.addLink( "l230", servers[31], servers[87] );
		network.addLink( "l869", servers[23], servers[39] );
		network.addLink( "l430", servers[77], servers[37] );
		network.addLink( "l450", servers[121], servers[32] );
		network.addLink( "l76", servers[13], servers[107] );
		network.addLink( "l193", servers[126], servers[42] );
		network.addLink( "l234", servers[31], servers[24] );
		network.addLink( "l1029", servers[115], servers[104] );
		network.addLink( "l714", servers[144], servers[34] );
		network.addLink( "l679", servers[145], servers[27] );
		network.addLink( "l650", servers[89], servers[108] );
		network.addLink( "l942", servers[73], servers[139] );
		network.addLink( "l694", servers[109], servers[43] );
		network.addLink( "l4", servers[21], servers[149] );
		network.addLink( "l108", servers[2], servers[63] );
		network.addLink( "l624", servers[108], servers[59] );
		network.addLink( "l212", servers[90], servers[2] );
		network.addLink( "l353", servers[14], servers[78] );
		network.addLink( "l472", servers[134], servers[49] );
		network.addLink( "l1003", servers[51], servers[80] );
		network.addLink( "l328", servers[36], servers[41] );
		network.addLink( "l372", servers[127], servers[47] );
		network.addLink( "l89", servers[116], servers[13] );
		network.addLink( "l591", servers[74], servers[100] );
		network.addLink( "l48", servers[60], servers[26] );
		network.addLink( "l626", servers[108], servers[34] );
		network.addLink( "l956", servers[125], servers[124] );
		network.addLink( "l22", servers[8], servers[21] );
		network.addLink( "l6", servers[118], servers[110] );
		network.addLink( "l563", servers[34], servers[121] );
		network.addLink( "l148", servers[5], servers[111] );
		network.addLink( "l1013", servers[48], servers[51] );
		network.addLink( "l344", servers[14], servers[102] );
		network.addLink( "l380", servers[75], servers[67] );
		network.addLink( "l975", servers[135], servers[7] );
		network.addLink( "l322", servers[58], servers[10] );
		network.addLink( "l30", servers[88], servers[141] );
		network.addLink( "l627", servers[108], servers[120] );
		network.addLink( "l117", servers[71], servers[107] );
		network.addLink( "l70", servers[13], servers[88] );
		network.addLink( "l1056", servers[12], servers[101] );
		network.addLink( "l361", servers[117], servers[119] );
		network.addLink( "l861", servers[137], servers[22] );
		network.addLink( "l1080", servers[106], servers[79] );
		network.addLink( "l512", servers[129], servers[49] );
		network.addLink( "l992", servers[84], servers[138] );
		network.addLink( "l667", servers[92], servers[70] );
		network.addLink( "l905", servers[138], servers[23] );
		network.addLink( "l596", servers[1], servers[61] );
		network.addLink( "l398", servers[32], servers[67] );
		network.addLink( "l376", servers[75], servers[131] );
		network.addLink( "l317", servers[102], servers[90] );
		network.addLink( "l1045", servers[28], servers[115] );
		network.addLink( "l616", servers[69], servers[120] );
		network.addLink( "l744", servers[44], servers[142] );
		network.addLink( "l566", servers[11], servers[59] );
		network.addLink( "l434", servers[52], servers[133] );
		network.addLink( "l633", servers[91], servers[18] );
		network.addLink( "l573", servers[11], servers[113] );
		network.addLink( "l699", servers[109], servers[91] );
		network.addLink( "l670", servers[43], servers[142] );
		network.addLink( "l572", servers[11], servers[129] );
		network.addLink( "l551", servers[120], servers[129] );
		network.addLink( "l669", servers[92], servers[91] );
		network.addLink( "l17", servers[8], servers[118] );
		network.addLink( "l849", servers[39], servers[16] );
		network.addLink( "l280", servers[10], servers[90] );
		network.addLink( "l408", servers[53], servers[58] );
		network.addLink( "l1083", servers[86], servers[28] );
		network.addLink( "l156", servers[5], servers[42] );
		network.addLink( "l757", servers[128], servers[92] );
		network.addLink( "l1024", servers[104], servers[82] );
		network.addLink( "l452", servers[121], servers[75] );
		network.addLink( "l447", servers[121], servers[58] );
		network.addLink( "l429", servers[77], servers[58] );
		network.addLink( "l290", servers[41], servers[103] );
		network.addLink( "l755", servers[128], servers[19] );
		network.addLink( "l274", servers[17], servers[5] );
		network.addLink( "l605", servers[76], servers[134] );
		network.addLink( "l1012", servers[48], servers[84] );
		network.addLink( "l100", servers[29], servers[116] );
		network.addLink( "l35", servers[88], servers[8] );
		network.addLink( "l1099", servers[105], servers[79] );
		network.addLink( "l80", servers[9], servers[21] );
		network.addLink( "l207", servers[24], servers[87] );
		network.addLink( "l415", servers[133], servers[53] );
		network.addLink( "l1096", servers[3], servers[12] );
		network.addLink( "l513", servers[129], servers[98] );
		network.addLink( "l181", servers[57], servers[54] );
		network.addLink( "l957", servers[125], servers[7] );
		network.addLink( "l159", servers[54], servers[5] );
		network.addLink( "l654", servers[89], servers[27] );
		network.addLink( "l815", servers[22], servers[44] );
		network.addLink( "l264", servers[45], servers[50] );
		network.addLink( "l646", servers[27], servers[108] );
		network.addLink( "l1017", servers[79], servers[25] );
		network.addLink( "l257", servers[119], servers[78] );
		network.addLink( "l1035", servers[115], servers[23] );
		network.addLink( "l255", servers[119], servers[57] );
		network.addLink( "l502", servers[59], servers[15] );
		network.addLink( "l1025", servers[104], servers[135] );
		network.addLink( "l268", servers[45], servers[54] );
		network.addLink( "l465", servers[100], servers[75] );
		network.addLink( "l972", servers[0], servers[23] );
		network.addLink( "l517", servers[113], servers[121] );
		network.addLink( "l887", servers[85], servers[81] );
		network.addLink( "l725", servers[19], servers[76] );
		network.addLink( "l1046", servers[28], servers[97] );
		network.addLink( "l459", servers[100], servers[14] );
		network.addLink( "l208", servers[24], servers[126] );
		network.addLink( "l282", servers[10], servers[31] );
		network.addLink( "l38", servers[88], servers[21] );
		network.addLink( "l292", servers[41], servers[17] );
		network.addLink( "l491", servers[61], servers[32] );
		network.addLink( "l567", servers[11], servers[100] );
		network.addLink( "l768", servers[148], servers[94] );
		network.addLink( "l296", servers[33], servers[57] );
		network.addLink( "l215", servers[90], servers[55] );
		network.addLink( "l740", servers[4], servers[109] );
		network.addLink( "l1087", servers[86], servers[125] );
		network.addLink( "l172", servers[55], servers[50] );
		network.addLink( "l54", servers[56], servers[60] );
		network.addLink( "l831", servers[114], servers[136] );
		network.addLink( "l536", servers[18], servers[32] );
		network.addLink( "l1055", servers[12], servers[51] );
		network.addLink( "l249", servers[103], servers[140] );
		network.addLink( "l601", servers[1], servers[70] );
		network.addLink( "l426", servers[77], servers[14] );
		network.addLink( "l103", servers[29], servers[107] );
		network.addLink( "l664", servers[92], servers[120] );
		network.addLink( "l116", servers[93], servers[56] );
		network.addLink( "l53", servers[56], servers[38] );
		network.addLink( "l287", servers[10], servers[103] );
		network.addLink( "l354", servers[14], servers[67] );
		network.addLink( "l384", servers[130], servers[58] );
		network.addLink( "l1093", servers[3], servers[86] );
		network.addLink( "l962", servers[82], servers[73] );
		network.addLink( "l1054", servers[12], servers[28] );
		network.addLink( "l469", servers[134], servers[75] );
		network.addLink( "l479", servers[15], servers[117] );
		network.addLink( "l916", servers[35], servers[80] );
		network.addLink( "l1018", servers[79], servers[39] );
		network.addLink( "l735", servers[146], servers[94] );
		network.addLink( "l649", servers[89], servers[59] );
		network.addLink( "l412", servers[133], servers[130] );
		network.addLink( "l752", servers[44], servers[144] );
		network.addLink( "l92", servers[116], servers[9] );
		network.addLink( "l191", servers[126], servers[29] );
		network.addLink( "l171", servers[55], servers[42] );
		network.addLink( "l1008", servers[48], servers[64] );
		network.addLink( "l201", servers[112], servers[71] );
		network.addLink( "l56", servers[122], servers[88] );
		network.addLink( "l838", servers[80], servers[46] );
		network.addLink( "l503", servers[59], servers[32] );
		network.addLink( "l478", servers[15], servers[62] );
		network.addLink( "l952", servers[125], servers[81] );
		network.addLink( "l457", servers[98], servers[77] );
		network.addLink( "l899", servers[64], servers[16] );
		network.addLink( "l518", servers[113], servers[75] );
		network.addLink( "l420", servers[83], servers[99] );
		network.addLink( "l896", servers[64], servers[81] );
		network.addLink( "l300", servers[67], servers[24] );
		network.addLink( "l164", servers[54], servers[111] );
		network.addLink( "l885", servers[85], servers[137] );
		network.addLink( "l549", servers[65], servers[134] );
		network.addLink( "l131", servers[50], servers[21] );
		network.addLink( "l927", servers[25], servers[138] );
		network.addLink( "l442", servers[62], servers[133] );
		network.addLink( "l576", servers[142], servers[134] );
		network.addLink( "l269", servers[45], servers[24] );
		network.addLink( "l223", servers[131], servers[5] );
		network.addLink( "l186", servers[57], servers[140] );
		network.addLink( "l850", servers[39], servers[128] );
		network.addLink( "l68", servers[132], servers[149] );
		network.addLink( "l891", servers[64], servers[114] );
		network.addLink( "l47", servers[60], servers[63] );
		network.addLink( "l440", servers[49], servers[33] );
		network.addLink( "l486", servers[61], servers[134] );
		network.addLink( "l310", servers[102], servers[131] );
		network.addLink( "l335", servers[47], servers[17] );
		network.addLink( "l137", servers[140], servers[111] );
		network.addLink( "l1049", servers[28], servers[135] );
		network.addLink( "l579", servers[142], servers[83] );
		network.addLink( "l473", servers[134], servers[100] );
		network.addLink( "l863", servers[23], servers[96] );
		network.addLink( "l570", servers[11], servers[62] );
		network.addLink( "l99", servers[116], servers[30] );
		network.addLink( "l83", servers[9], servers[40] );
		network.addLink( "l352", servers[14], servers[36] );
		network.addLink( "l28", servers[26], servers[8] );
		network.addLink( "l331", servers[36], servers[31] );
		network.addLink( "l742", servers[4], servers[143] );
		network.addLink( "l298", servers[33], servers[55] );
		network.addLink( "l1090", servers[3], servers[79] );
		network.addLink( "l26", servers[26], servers[40] );
		network.addLink( "l636", servers[91], servers[76] );
		network.addLink( "l807", servers[136], servers[95] );
		network.addLink( "l471", servers[134], servers[83] );
		network.addLink( "l613", servers[69], servers[70] );
		network.addLink( "l271", servers[17], servers[50] );
		network.addLink( "l999", servers[51], servers[0] );
		network.addLink( "l341", servers[47], servers[102] );
		network.addLink( "l841", servers[80], servers[123] );
		network.addLink( "l971", servers[0], servers[125] );
		network.addLink( "l431", servers[77], servers[10] );
		network.addLink( "l1040", servers[101], servers[84] );
		network.addLink( "l485", servers[61], servers[15] );
		network.addLink( "l500", servers[59], servers[130] );
		network.addLink( "l453", servers[98], servers[32] );
		network.addLink( "l277", servers[17], servers[24] );
		network.addLink( "l552", servers[120], servers[70] );
		network.addLink( "l436", servers[49], servers[130] );
		network.addLink( "l405", servers[53], servers[130] );
		network.addLink( "l169", servers[55], servers[5] );
		network.addLink( "l560", servers[34], servers[61] );
		network.addLink( "l266", servers[45], servers[78] );
	}

	public static void createLinks3() throws Exception {
		network.addLink( "l629", servers[91], servers[59] );
		network.addLink( "l834", servers[80], servers[22] );
		network.addLink( "l914", servers[35], servers[72] );
		network.addLink( "l366", servers[127], servers[33] );
		network.addLink( "l557", servers[34], servers[53] );
		network.addLink( "l617", servers[69], servers[65] );
		network.addLink( "l443", servers[62], servers[75] );
		network.addLink( "l487", servers[61], servers[77] );
		network.addLink( "l1019", servers[79], servers[138] );
		network.addLink( "l758", servers[128], servers[146] );
		network.addLink( "l416", servers[133], servers[32] );
		network.addLink( "l657", servers[89], servers[34] );
		network.addLink( "l389", servers[130], servers[10] );
		network.addLink( "l178", servers[87], servers[50] );
		network.addLink( "l941", servers[73], servers[96] );
		network.addLink( "l675", servers[43], servers[65] );
		network.addLink( "l653", servers[89], servers[1] );
		network.addLink( "l823", servers[22], servers[6] );
		network.addLink( "l1042", servers[101], servers[125] );
		network.addLink( "l188", servers[57], servers[116] );
		network.addLink( "l671", servers[43], servers[34] );
		network.addLink( "l209", servers[24], servers[2] );
		network.addLink( "l949", servers[73], servers[81] );
		network.addLink( "l251", servers[103], servers[24] );
		network.addLink( "l761", servers[123], servers[128] );
		network.addLink( "l0", servers[21], servers[110] );
		network.addLink( "l587", servers[74], servers[52] );
		network.addLink( "l935", servers[139], servers[6] );
		network.addLink( "l1058", servers[12], servers[115] );
		network.addLink( "l595", servers[1], servers[15] );
		network.addLink( "l24", servers[8], servers[149] );
		network.addLink( "l716", servers[144], servers[1] );
		network.addLink( "l409", servers[53], servers[75] );
		network.addLink( "l359", servers[117], servers[45] );
		network.addLink( "l691", servers[109], servers[145] );
		network.addLink( "l27", servers[26], servers[118] );
		network.addLink( "l541", servers[18], servers[121] );
		network.addLink( "l437", servers[49], servers[53] );
		network.addLink( "l662", servers[92], servers[129] );
		network.addLink( "l305", servers[67], servers[33] );
		network.addLink( "l165", servers[55], servers[116] );
		network.addLink( "l769", servers[148], servers[4] );
		network.addLink( "l477", servers[134], servers[98] );
		network.addLink( "l936", servers[139], servers[39] );
		network.addLink( "l684", servers[145], servers[91] );
		network.addLink( "l247", servers[103], servers[147] );
		network.addLink( "l848", servers[39], servers[136] );
		network.addLink( "l318", servers[102], servers[67] );
		network.addLink( "l458", servers[100], servers[53] );
		network.addLink( "l1065", servers[66], servers[79] );
		network.addLink( "l106", servers[29], servers[122] );
		network.addLink( "l703", servers[143], servers[91] );
		network.addLink( "l168", servers[55], servers[71] );
		network.addLink( "l912", servers[35], servers[96] );
		network.addLink( "l652", servers[89], servers[61] );
		network.addLink( "l130", servers[50], servers[9] );
		network.addLink( "l125", servers[42], servers[40] );
		network.addLink( "l25", servers[8], servers[141] );
		network.addLink( "l40", servers[60], servers[118] );
		network.addLink( "l492", servers[61], servers[133] );
		network.addLink( "l197", servers[126], servers[13] );
		network.addLink( "l741", servers[4], servers[145] );
		network.addLink( "l845", servers[124], servers[46] );
		network.addLink( "l562", servers[34], servers[133] );
		network.addLink( "l1038", servers[101], servers[82] );
		network.addLink( "l299", servers[67], servers[131] );
		network.addLink( "l263", servers[45], servers[31] );
		network.addLink( "l763", servers[123], servers[1] );
		network.addLink( "l663", servers[92], servers[59] );
		network.addLink( "l894", servers[64], servers[7] );
		network.addLink( "l1059", servers[12], servers[64] );
		network.addLink( "l511", servers[129], servers[134] );
		network.addLink( "l910", servers[138], servers[96] );
		network.addLink( "l1026", servers[104], servers[64] );
		network.addLink( "l895", servers[64], servers[72] );
		network.addLink( "l316", servers[102], servers[78] );
		network.addLink( "l736", servers[146], servers[91] );
		network.addLink( "l321", servers[58], servers[103] );
		network.addLink( "l909", servers[138], servers[39] );
		network.addLink( "l590", servers[74], servers[34] );
		network.addLink( "l676", servers[43], servers[129] );
		network.addLink( "l717", servers[144], servers[43] );
		network.addLink( "l232", servers[31], servers[29] );
		network.addLink( "l1094", servers[3], servers[84] );
		network.addLink( "l349", servers[14], servers[99] );
		network.addLink( "l608", servers[76], servers[18] );
		network.addLink( "l256", servers[119], servers[131] );
		network.addLink( "l715", servers[144], servers[68] );
		network.addLink( "l1079", servers[106], servers[135] );
		network.addLink( "l963", servers[82], servers[125] );
		network.addLink( "l348", servers[14], servers[37] );
		network.addLink( "l532", servers[70], servers[49] );
		network.addLink( "l555", servers[34], servers[18] );
		network.addLink( "l677", servers[43], servers[69] );
		network.addLink( "l554", servers[34], servers[52] );
		network.addLink( "l433", servers[52], servers[53] );
		network.addLink( "l730", servers[94], servers[34] );
		network.addLink( "l712", servers[143], servers[11] );
		network.addLink( "l96", servers[116], servers[60] );
		network.addLink( "l700", servers[109], servers[1] );
		network.addLink( "l123", servers[42], servers[30] );
	}

	public static void createFlows1() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f277", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[27] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		network.addFlow( "f243", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f463", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[103] );
		network.addFlow( "f288", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		network.addFlow( "f525", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		servers_on_path_s.add( servers[116] );
		servers_on_path_s.add( servers[60] );
		network.addFlow( "f25", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f240", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f318", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[47] );
		network.addFlow( "f58", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f343", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		network.addFlow( "f544", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		servers_on_path_s.add( servers[93] );
		network.addFlow( "f571", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f327", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[114] );
		network.addFlow( "f580", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f366", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		network.addFlow( "f415", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f402", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[49] );
		network.addFlow( "f135", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		network.addFlow( "f378", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f488", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f582", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		network.addFlow( "f545", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		network.addFlow( "f557", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f65", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[65] );
		network.addFlow( "f114", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		servers_on_path_s.add( servers[130] );
		network.addFlow( "f495", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f352", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f560", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f6", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f414", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		network.addFlow( "f158", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f47", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f315", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f314", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f241", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		network.addFlow( "f116", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f521", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		network.addFlow( "f509", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		network.addFlow( "f272", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f199", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f271", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f505", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f251", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f597", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f504", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f230", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f363", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[141] );
		network.addFlow( "f320", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f292", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f554", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f493", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f342", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		network.addFlow( "f54", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f329", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		network.addFlow( "f129", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		network.addFlow( "f274", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f577", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[89] );
		network.addFlow( "f487", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f409", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f1", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[81] );
		network.addFlow( "f514", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f36", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f205", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		network.addFlow( "f95", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows2() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f312", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f5", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f490", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f263", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[39] );
		network.addFlow( "f165", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		network.addFlow( "f465", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[149] );
		network.addFlow( "f368", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f325", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		network.addFlow( "f584", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[85] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f81", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f573", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f384", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f125", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f555", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f294", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[135] );
		network.addFlow( "f591", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f261", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f530", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		network.addFlow( "f473", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[108] );
		network.addFlow( "f180", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f30", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[57] );
		network.addFlow( "f196", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		network.addFlow( "f552", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f386", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[85] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		network.addFlow( "f137", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[67] );
		network.addFlow( "f353", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[61] );
		network.addFlow( "f267", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		network.addFlow( "f593", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f151", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[39] );
		network.addFlow( "f190", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[81] );
		network.addFlow( "f598", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f238", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f313", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[142] );
		network.addFlow( "f336", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f220", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		servers_on_path_s.add( servers[87] );
		network.addFlow( "f98", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f202", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f35", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f154", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[108] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f561", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f53", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[141] );
		network.addFlow( "f483", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f210", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[143] );
		network.addFlow( "f451", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[73] );
		network.addFlow( "f594", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[63] );
		network.addFlow( "f562", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f365", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		network.addFlow( "f148", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		network.addFlow( "f38", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[128] );
		network.addFlow( "f280", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[126] );
		network.addFlow( "f34", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[134] );
		network.addFlow( "f349", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f188", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f372", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f471", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		network.addFlow( "f75", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f70", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f546", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[117] );
		network.addFlow( "f439", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f587", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[65] );
		network.addFlow( "f14", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f492", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[69] );
		network.addFlow( "f551", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f0", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[11] );
		network.addFlow( "f206", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows3() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		network.addFlow( "f262", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f422", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f506", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f217", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[120] );
		network.addFlow( "f507", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f59", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f527", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f168", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		network.addFlow( "f121", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f152", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[108] );
		network.addFlow( "f448", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f393", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		network.addFlow( "f401", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f412", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[89] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		network.addFlow( "f400", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f178", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		network.addFlow( "f195", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f93", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f511", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[63] );
		network.addFlow( "f256", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		network.addFlow( "f588", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[73] );
		network.addFlow( "f446", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f141", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f433", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		network.addFlow( "f61", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		network.addFlow( "f128", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f371", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f71", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[96] );
		network.addFlow( "f161", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		network.addFlow( "f331", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		network.addFlow( "f289", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		network.addFlow( "f234", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f242", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		network.addFlow( "f31", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f110", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f443", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f273", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		network.addFlow( "f138", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f175", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[149] );
		network.addFlow( "f275", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f364", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[67] );
		network.addFlow( "f150", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f223", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f99", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[81] );
		network.addFlow( "f416", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[62] );
		network.addFlow( "f226", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		network.addFlow( "f27", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[85] );
		network.addFlow( "f547", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		network.addFlow( "f171", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		network.addFlow( "f484", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[70] );
		network.addFlow( "f291", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f405", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f115", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f90", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f16", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f88", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f64", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f22", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f29", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[110] );
		network.addFlow( "f339", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows4() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f228", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f247", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[49] );
		network.addFlow( "f425", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[57] );
		network.addFlow( "f585", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f252", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f244", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f445", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f410", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f68", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[142] );
		network.addFlow( "f502", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f279", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[109] );
		network.addFlow( "f345", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f395", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[140] );
		network.addFlow( "f233", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[6] );
		network.addFlow( "f323", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[63] );
		network.addFlow( "f406", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f102", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f157", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[128] );
		network.addFlow( "f540", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f136", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f60", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		network.addFlow( "f548", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		network.addFlow( "f62", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		network.addFlow( "f270", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f334", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f20", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f468", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f51", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		network.addFlow( "f79", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[91] );
		network.addFlow( "f452", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[89] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[88] );
		network.addFlow( "f112", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f218", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[89] );
		network.addFlow( "f145", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		network.addFlow( "f305", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f143", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[71] );
		network.addFlow( "f87", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[63] );
		network.addFlow( "f300", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[149] );
		network.addFlow( "f538", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f101", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[69] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		network.addFlow( "f423", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		network.addFlow( "f479", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f475", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f477", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[108] );
		network.addFlow( "f499", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f460", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f133", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[109] );
		network.addFlow( "f285", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f444", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f390", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[108] );
		network.addFlow( "f43", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		network.addFlow( "f15", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[96] );
		network.addFlow( "f126", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[26] );
		network.addFlow( "f139", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		network.addFlow( "f362", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[72] );
		network.addFlow( "f357", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		network.addFlow( "f8", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f467", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f265", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f282", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		network.addFlow( "f56", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f245", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows5() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f528", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f361", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		servers_on_path_s.add( servers[93] );
		network.addFlow( "f437", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f469", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f408", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f474", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		network.addFlow( "f77", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[97] );
		network.addFlow( "f229", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f104", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f149", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f355", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f310", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f417", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f89", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f140", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f49", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f170", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		network.addFlow( "f163", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[134] );
		network.addFlow( "f482", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f66", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[111] );
		network.addFlow( "f284", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[120] );
		network.addFlow( "f134", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f260", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[71] );
		network.addFlow( "f453", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[69] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		servers_on_path_s.add( servers[103] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f427", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		network.addFlow( "f269", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f232", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f276", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		network.addFlow( "f326", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		network.addFlow( "f108", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f481", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		network.addFlow( "f421", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[85] );
		network.addFlow( "f517", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f519", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[81] );
		network.addFlow( "f124", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[107] );
		network.addFlow( "f266", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[55] );
		network.addFlow( "f283", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f579", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f215", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f510", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f500", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		servers_on_path_s.add( servers[93] );
		network.addFlow( "f328", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		servers_on_path_s.add( servers[87] );
		network.addFlow( "f250", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f462", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		network.addFlow( "f236", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[39] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f131", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f179", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[57] );
		network.addFlow( "f164", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f306", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[73] );
		network.addFlow( "f109", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f106", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f72", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[110] );
		network.addFlow( "f418", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f26", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[0] );
		network.addFlow( "f575", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f380", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[128] );
		network.addFlow( "f182", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[126] );
		network.addFlow( "f122", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f566", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f347", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		network.addFlow( "f246", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows6() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f567", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f96", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f572", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f440", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f24", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		network.addFlow( "f103", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[149] );
		network.addFlow( "f478", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		network.addFlow( "f413", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f430", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		network.addFlow( "f536", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		network.addFlow( "f578", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f589", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f432", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		network.addFlow( "f330", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		servers_on_path_s.add( servers[71] );
		network.addFlow( "f501", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		network.addFlow( "f4", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		network.addFlow( "f403", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f204", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[46] );
		network.addFlow( "f84", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f599", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[108] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[45] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f431", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f239", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f7", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		servers_on_path_s.add( servers[41] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f441", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f198", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[140] );
		network.addFlow( "f107", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[67] );
		network.addFlow( "f46", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		servers_on_path_s.add( servers[119] );
		network.addFlow( "f358", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		network.addFlow( "f369", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f28", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		network.addFlow( "f167", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f450", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f286", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f127", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[69] );
		network.addFlow( "f346", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f304", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f324", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		network.addFlow( "f197", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		network.addFlow( "f142", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f539", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		servers_on_path_s.add( servers[119] );
		network.addFlow( "f191", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		network.addFlow( "f592", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f350", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[138] );
		network.addFlow( "f41", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		network.addFlow( "f397", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		network.addFlow( "f526", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f472", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f564", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f264", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f11", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		servers_on_path_s.add( servers[117] );
		network.addFlow( "f94", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f457", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[97] );
		network.addFlow( "f508", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f549", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f375", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f76", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[65] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f302", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[146] );
		network.addFlow( "f295", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f155", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f322", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f255", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows7() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[49] );
		network.addFlow( "f426", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[71] );
		network.addFlow( "f132", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		network.addFlow( "f332", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[27] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[140] );
		network.addFlow( "f281", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[95] );
		network.addFlow( "f130", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f537", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f563", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		network.addFlow( "f44", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		network.addFlow( "f211", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[48] );
		network.addFlow( "f596", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		network.addFlow( "f57", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		servers_on_path_s.add( servers[87] );
		network.addFlow( "f248", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[96] );
		network.addFlow( "f458", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f411", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[54] );
		network.addFlow( "f497", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f379", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		network.addFlow( "f541", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[11] );
		network.addFlow( "f78", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f156", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		servers_on_path_s.add( servers[116] );
		network.addFlow( "f532", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[146] );
		network.addFlow( "f213", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f387", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		servers_on_path_s.add( servers[116] );
		network.addFlow( "f203", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		network.addFlow( "f459", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f159", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f543", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f351", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f117", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f166", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[2] );
		network.addFlow( "f33", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[121] );
		network.addFlow( "f253", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		network.addFlow( "f187", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[149] );
		network.addFlow( "f92", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[97] );
		network.addFlow( "f489", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f486", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f185", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		network.addFlow( "f356", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[118] );
		network.addFlow( "f553", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[108] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[45] );
		servers_on_path_s.add( servers[50] );
		servers_on_path_s.add( servers[21] );
		network.addFlow( "f19", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f200", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f48", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		servers_on_path_s.add( servers[93] );
		servers_on_path_s.add( servers[60] );
		network.addFlow( "f192", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[114] );
		network.addFlow( "f160", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f535", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		network.addFlow( "f307", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[142] );
		network.addFlow( "f568", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f74", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f299", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f216", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		servers_on_path_s.add( servers[116] );
		servers_on_path_s.add( servers[60] );
		network.addFlow( "f340", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		network.addFlow( "f194", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[96] );
		network.addFlow( "f118", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f69", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f174", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[62] );
		servers_on_path_s.add( servers[47] );
		network.addFlow( "f348", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[23] );
		network.addFlow( "f224", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f354", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[43] );
		network.addFlow( "f301", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f45", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows8() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f82", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[64] );
		network.addFlow( "f214", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		network.addFlow( "f424", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f470", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		network.addFlow( "f464", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		network.addFlow( "f565", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		network.addFlow( "f303", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[146] );
		network.addFlow( "f533", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[55] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[141] );
		network.addFlow( "f17", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[140] );
		servers_on_path_s.add( servers[132] );
		network.addFlow( "f321", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f219", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[62] );
		network.addFlow( "f169", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[69] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[110] );
		network.addFlow( "f123", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f333", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[103] );
		network.addFlow( "f394", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f494", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f341", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f373", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		network.addFlow( "f296", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f520", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f293", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f419", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f383", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		network.addFlow( "f254", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[118] );
		network.addFlow( "f447", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f542", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		network.addFlow( "f429", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f480", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f485", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		network.addFlow( "f515", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		network.addFlow( "f39", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[77] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[54] );
		network.addFlow( "f498", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[59] );
		network.addFlow( "f91", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[16] );
		network.addFlow( "f570", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[18] );
		network.addFlow( "f337", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[143] );
		network.addFlow( "f529", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		network.addFlow( "f512", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[54] );
		network.addFlow( "f382", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f374", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		network.addFlow( "f225", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		network.addFlow( "f42", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[81] );
		network.addFlow( "f120", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[95] );
		network.addFlow( "f18", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[46] );
		network.addFlow( "f184", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[35] );
		network.addFlow( "f212", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[148] );
		servers_on_path_s.add( servers[27] );
		network.addFlow( "f574", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[89] );
		network.addFlow( "f309", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		servers_on_path_s.add( servers[116] );
		servers_on_path_s.add( servers[60] );
		network.addFlow( "f189", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		network.addFlow( "f590", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[148] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f456", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[70] );
		servers_on_path_s.add( servers[52] );
		network.addFlow( "f370", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[8] );
		network.addFlow( "f221", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		network.addFlow( "f97", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		network.addFlow( "f278", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[110] );
		network.addFlow( "f181", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[121] );
		servers_on_path_s.add( servers[58] );
		network.addFlow( "f100", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f268", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[109] );
		network.addFlow( "f144", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f376", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f173", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[44] );
		network.addFlow( "f466", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f344", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows9() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f396", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		network.addFlow( "f297", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[51] );
		network.addFlow( "f534", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[11] );
		servers_on_path_s.add( servers[62] );
		servers_on_path_s.add( servers[47] );
		network.addFlow( "f442", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f377", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		servers_on_path_s.add( servers[110] );
		network.addFlow( "f113", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[82] );
		network.addFlow( "f146", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f162", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f67", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f569", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[85] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[111] );
		servers_on_path_s.add( servers[141] );
		network.addFlow( "f407", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		network.addFlow( "f550", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[88] );
		network.addFlow( "f316", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		network.addFlow( "f86", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f208", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[69] );
		network.addFlow( "f186", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f338", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f207", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[124] );
		servers_on_path_s.add( servers[128] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[78] );
		network.addFlow( "f518", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[88] );
		network.addFlow( "f399", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[73] );
		network.addFlow( "f3", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[127] );
		servers_on_path_s.add( servers[119] );
		servers_on_path_s.add( servers[147] );
		network.addFlow( "f111", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[112] );
		network.addFlow( "f581", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		network.addFlow( "f23", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[89] );
		network.addFlow( "f147", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		servers_on_path_s.add( servers[32] );
		network.addFlow( "f586", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f392", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f398", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f287", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		network.addFlow( "f388", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		network.addFlow( "f85", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[134] );
		network.addFlow( "f583", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f449", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[144] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f391", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		network.addFlow( "f83", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f153", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f559", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[148] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		network.addFlow( "f503", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[71] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f12", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[30] );
		network.addFlow( "f576", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[85] );
		network.addFlow( "f50", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f531", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f172", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[122] );
		servers_on_path_s.add( servers[38] );
		network.addFlow( "f37", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[96] );
		servers_on_path_s.add( servers[109] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[10] );
		network.addFlow( "f420", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[50] );
		network.addFlow( "f73", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[22] );
		servers_on_path_s.add( servers[109] );
		network.addFlow( "f435", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[33] );
		network.addFlow( "f222", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		network.addFlow( "f290", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f513", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[28] );
		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		network.addFlow( "f2", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[106] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[146] );
		network.addFlow( "f522", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f258", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		network.addFlow( "f434", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f176", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f491", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[135] );
		network.addFlow( "f335", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[104] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f558", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[13] );
		network.addFlow( "f201", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[80] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[98] );
		network.addFlow( "f367", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[56] );
		network.addFlow( "f381", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public static void createFlows10() throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f21", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[25] );
		servers_on_path_s.add( servers[124] );
		network.addFlow( "f55", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[136] );
		network.addFlow( "f389", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[114] );
		network.addFlow( "f32", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[19] );
		servers_on_path_s.add( servers[76] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f595", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[72] );
		network.addFlow( "f9", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[85] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f308", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[37] );
		servers_on_path_s.add( servers[24] );
		network.addFlow( "f516", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[97] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		network.addFlow( "f556", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[101] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[114] );
		network.addFlow( "f438", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[91] );
		servers_on_path_s.add( servers[15] );
		network.addFlow( "f52", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		network.addFlow( "f461", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[77] );
		servers_on_path_s.add( servers[10] );
		servers_on_path_s.add( servers[54] );
		network.addFlow( "f119", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[16] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[52] );
		servers_on_path_s.add( servers[33] );
		servers_on_path_s.add( servers[57] );
		network.addFlow( "f193", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[145] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f209", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		network.addFlow( "f436", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[41] );
		network.addFlow( "f177", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[85] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[142] );
		servers_on_path_s.add( servers[83] );
		servers_on_path_s.add( servers[102] );
		servers_on_path_s.add( servers[90] );
		servers_on_path_s.add( servers[9] );
		network.addFlow( "f319", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f231", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		network.addFlow( "f237", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[125] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[17] );
		servers_on_path_s.add( servers[42] );
		servers_on_path_s.add( servers[40] );
		network.addFlow( "f259", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[7] );
		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[68] );
		network.addFlow( "f235", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[82] );
		servers_on_path_s.add( servers[137] );
		network.addFlow( "f524", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[95] );
		servers_on_path_s.add( servers[43] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[53] );
		servers_on_path_s.add( servers[45] );
		network.addFlow( "f311", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		network.addFlow( "f360", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[23] );
		servers_on_path_s.add( servers[46] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[61] );
		servers_on_path_s.add( servers[130] );
		servers_on_path_s.add( servers[31] );
		servers_on_path_s.add( servers[140] );
		network.addFlow( "f523", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[73] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		network.addFlow( "f13", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[6] );
		network.addFlow( "f257", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[146] );
		servers_on_path_s.add( servers[69] );
		network.addFlow( "f63", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[35] );
		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[103] );
		network.addFlow( "f428", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[129] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[36] );
		servers_on_path_s.add( servers[57] );
		network.addFlow( "f317", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[79] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		network.addFlow( "f249", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[133] );
		servers_on_path_s.add( servers[99] );
		servers_on_path_s.add( servers[5] );
		network.addFlow( "f455", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[137] );
		servers_on_path_s.add( servers[148] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[59] );
		servers_on_path_s.add( servers[14] );
		servers_on_path_s.add( servers[103] );
		network.addFlow( "f298", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[94] );
		servers_on_path_s.add( servers[34] );
		servers_on_path_s.add( servers[52] );
		network.addFlow( "f476", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[20] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[7] );
		network.addFlow( "f40", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[86] );
		servers_on_path_s.add( servers[66] );
		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[114] );
		servers_on_path_s.add( servers[144] );
		network.addFlow( "f496", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[113] );
		servers_on_path_s.add( servers[75] );
		servers_on_path_s.add( servers[131] );
		servers_on_path_s.add( servers[29] );
		servers_on_path_s.add( servers[88] );
		network.addFlow( "f10", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[12] );
		servers_on_path_s.add( servers[51] );
		servers_on_path_s.add( servers[97] );
		network.addFlow( "f359", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[135] );
		servers_on_path_s.add( servers[81] );
		servers_on_path_s.add( servers[80] );
		network.addFlow( "f80", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[115] );
		servers_on_path_s.add( servers[139] );
		servers_on_path_s.add( servers[22] );
		network.addFlow( "f227", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[64] );
		servers_on_path_s.add( servers[136] );
		servers_on_path_s.add( servers[143] );
		servers_on_path_s.add( servers[74] );
		servers_on_path_s.add( servers[77] );
		network.addFlow( "f404", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[84] );
		servers_on_path_s.add( servers[125] );
		network.addFlow( "f183", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		servers_on_path_s.add( servers[14] );
		network.addFlow( "f105", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[48] );
		servers_on_path_s.add( servers[138] );
		servers_on_path_s.add( servers[123] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[100] );
		network.addFlow( "f454", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[72] );
		servers_on_path_s.add( servers[44] );
		servers_on_path_s.add( servers[92] );
		servers_on_path_s.add( servers[70] );
		network.addFlow( "f385", new ArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,0.1),2.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

	public SavedNetwork() {
		try{
			servers = new Server[150];
			network = new Network();
			createServers1();
			createLinks1();
			createLinks2();
			createLinks3();
			createFlows1();
			createFlows2();
			createFlows3();
			createFlows4();
			createFlows5();
			createFlows6();
			createFlows7();
			createFlows8();
			createFlows9();
			createFlows10();
		} catch (Exception e) {
			System.out.println( e.toString() );
		}
	}

}

