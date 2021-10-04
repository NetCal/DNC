/* 
 * This file is compatible with the Deterministic Network Calculator (DNC) v2.5.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 */ 

package network_classes;

import java.util.LinkedList;

import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.ServerGraphFactory;

public class dnc_network_model_4 implements ServerGraphFactory {
	private ServerGraph sg;
	private static Curve factory = Curve.getFactory();

	public void createServers1( ServerGraph sg, Server[] servers ) throws Exception {
		servers[6] = sg.addServer( "14", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[7] = sg.addServer( "18", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[0] = sg.addServer( "11", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[4] = sg.addServer( "17", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[3] = sg.addServer( "19", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[2] = sg.addServer( "9", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[1] = sg.addServer( "15", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
		servers[5] = sg.addServer( "12", factory.createServiceCurve( "SC{(0.0,0.0),5.2631578947368425}" ), factory.createMaxServiceCurve( "MSC{(0.0,0.0),0.0;!(0.0,Infinity),0.0}" ), Multiplexing.FIFO, false, false );
	}

	public void createTurns1( ServerGraph sg, Server[] servers ) throws Exception {
		sg.addTurn( "t6", servers[6], servers[7] );
		sg.addTurn( "t2", servers[2], servers[3] );
		sg.addTurn( "t5", servers[5], servers[6] );
		sg.addTurn( "t1", servers[1], servers[2] );
		sg.addTurn( "t0", servers[0], servers[1] );
		sg.addTurn( "t4", servers[4], servers[5] );
		sg.addTurn( "t3", servers[3], servers[4] );
	}

	public void createFlows1( ServerGraph sg, Server[] servers ) throws Exception {
		LinkedList<Server> servers_on_path_s = new LinkedList<Server>();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[1] );
		sg.addFlow( "6", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[3] );
		sg.addFlow( "5", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[7] );
		sg.addFlow( "13", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[5] );
		sg.addFlow( "2", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[4] );
		sg.addFlow( "3", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[2] );
		servers_on_path_s.add( servers[3] );
		servers_on_path_s.add( servers[4] );
		servers_on_path_s.add( servers[5] );
		servers_on_path_s.add( servers[6] );
		servers_on_path_s.add( servers[7] );
		sg.addFlow( "1", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[0] );
		servers_on_path_s.add( servers[1] );
		servers_on_path_s.add( servers[2] );
		sg.addFlow( "4", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[4] );
		sg.addFlow( "8", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

		servers_on_path_s.add( servers[6] );
		sg.addFlow( "7", factory.createArrivalCurve( "AC{(0.0,0.0),0.0;!(0.0,1.0),1.0}" ), servers_on_path_s );
		servers_on_path_s.clear();

	}

public dnc_network_model_4() {
		sg = createServerGraph();
	}

	public ServerGraph createServerGraph() {
		Server[] servers = new Server[8];
		sg = new ServerGraph();
		try{
			createServers1( sg, servers );
			createTurns1( sg, servers );
			createFlows1( sg, servers );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sg;
	}

	public ServerGraph getServerGraph() {
		return sg;
	}

	public void reinitializeCurves() {
		sg = createServerGraph();
	}
}
