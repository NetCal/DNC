/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
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

package unikl.disco.tests;

import java.util.LinkedList;

import unikl.disco.curves.ServiceCurve;
import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.Analysis.Analyses;
import unikl.disco.network.Link;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Server;
import unikl.disco.network.Server.Multiplexing;
import unikl.disco.numbers.NumFactory;

/**
 * 
 * @author Steffen Bondorf
 *
 */
public class FF_3S_1SC_2F_1AC_2P_Net {
	protected static Network network;
	protected static Server s0, s1, s2;
	protected static Link l_s0_s1, l_s1_s2;
	protected static Flow f0, f1;

	private static int sc_R = 20;
	private static int sc_T = 20;
	private static int ac_r = 5;
	private static int ac_b = 25;
	
	private static ServiceCurve service_curve = ServiceCurve.createRateLatency( sc_R, sc_T );
	private static ArrivalCurve arrival_curve = ArrivalCurve.createTokenBucket( ac_r, ac_b );

	protected static TestResults expected_results;
	
	private FF_3S_1SC_2F_1AC_2P_Net() {}

	public static Network createNetwork() {
		network = new Network();
		
		s0 = network.addServer( "s0", service_curve );
		s1 = network.addServer( "s1", service_curve );
		s2 = network.addServer( "s2", service_curve );

		try {
			l_s0_s1 = network.addLink( "l_s0_s1", s0, s1 );
			l_s1_s2 = network.addLink( "l_s1_s2", s1, s2 );
			network.addLink( s0, s2 );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
		
		LinkedList<Link> f1_path = new LinkedList<Link>();
		f1_path.add( l_s0_s1 );
		f1_path.add( l_s1_s2 );

		try {
			f0 = network.addFlow( "f0", arrival_curve, s0, s2 );
			f1 = network.addFlow( "f1", arrival_curve, f1_path );
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
		
		return network;
	}
	
	protected static void reinitializeCurves() {
		service_curve = ServiceCurve.createRateLatency( sc_R, sc_T );
		for( Server server : network.getServers() ) {
			server.setServiceCurve( service_curve );
		}

		arrival_curve = ArrivalCurve.createTokenBucket( ac_r, ac_b );
		for( Flow flow : network.getFlows() ) {
			flow.setArrivalCurve( arrival_curve );
		}
	}
	
	protected static void initializeExpectedTestResults() {
		expected_results = new TestResults();
		
		// TFA
		expected_results.addBounds( Analyses.TFA, Multiplexing.FIFO, f0, NumFactory.create( 485, 8 ), NumFactory.create( 1125, 2 ) );
		expected_results.addBounds( Analyses.TFA, Multiplexing.FIFO, f1, NumFactory.create( 1395, 16 ), NumFactory.create( 1125, 2 ) );
		expected_results.addBounds( Analyses.TFA, Multiplexing.ARBITRARY, f0, NumFactory.create( 385, 3 ), NumFactory.create( 1900, 3 ) );
		expected_results.addBounds( Analyses.TFA, Multiplexing.ARBITRARY, f1, NumFactory.create( 470, 3 ), NumFactory.create( 1900, 3 ) );

		// SFA
		expected_results.addBounds( Analyses.SFA, Multiplexing.FIFO, f0, NumFactory.create( 2615, 48 ), NumFactory.create( 4625, 16 ) );
		expected_results.addBounds( Analyses.SFA, Multiplexing.FIFO, f1, NumFactory.create( 3335, 48 ), NumFactory.create( 5825, 16 ) );
		expected_results.addBounds( Analyses.SFA, Multiplexing.ARBITRARY, f0, NumFactory.create( 670, 9 ), NumFactory.create( 3500, 9 ) );
		expected_results.addBounds( Analyses.SFA, Multiplexing.ARBITRARY, f1, NumFactory.create( 790, 9 ), NumFactory.create( 4100, 9 ) );

		// PMOO
		expected_results.addBounds( Analyses.PMOO, Multiplexing.ARBITRARY, f0, NumFactory.create( 670, 9 ), NumFactory.create( 3500, 9 ) );
		expected_results.addBounds( Analyses.PMOO, Multiplexing.ARBITRARY, f1, NumFactory.create( 790, 9 ), NumFactory.create( 4100, 9 ) );
	}
}