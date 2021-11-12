/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.network.server_graph;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.CurveFactory_Affine;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.utils.SetUtils;

/**
 * Servers in a server graph object correspond to buffers that may be shared by
 * different flows.
 * <p>
 * That means the path of a flow is a sequence of buffers crossed by it and the
 * analysis operating on a server graph handles Server objects as such.
 * <p>
 * Please consider this when modeling a server graph for analysis. E.g., assuming
 * output buffering in a simple wireless sensor network with one transceiver per
 * node there is a 1:1-relation between network graph connecting wireless sensor
 * nodes and servers in the DNC server graph.
 * <p>
 * A flows path, however, is a sequence of crossed buffers. As a flow usually
 * does not reach the output buffer of its sink, the path should not contain a
 * turn to it. Otherwise the flow interference pattern of the server graph will be
 * too pessimistic, yet, the results remain valid.
 * <p>
 * In practice, on can model a sink explicitly by setting the server's service
 * curve to be an infinite burst after without delay ( see
 * ServiceCurve.createZeroDelayBurst() ).
 */
public class ServerGraph {
	private Set<Server> servers;
	private Set<Turn> turns;
	private Set<Flow> flows;

	private Map<Server, Set<Turn>> map__server__in_turns;
	private Map<Server, Set<Turn>> map__server__out_turns;

	private Map<Server, Set<Flow>> map__server__flows;
	private Map<Server, Set<Flow>> map__server__source_flows;

	private Map<Turn, Set<Flow>> map__turn__flows;

	private String server_default_name_prefix = "s";
	private int server_id_counter = 0;
	private Map<Integer, Server> map__id__server;

	private String turn_default_name_prefix = "t";
	private int turn_id_counter = 0;

	private String flow_default_name_prefix = "f";
	private int flow_id_counter = 0;
	private Map<Integer, Flow> map__id__flow;

	public ServerGraph() {
		servers = new HashSet<Server>();
		turns = new HashSet<Turn>();
		flows = new HashSet<Flow>();

		map__id__server = new HashMap<Integer, Server>();
		map__id__flow = new HashMap<Integer, Flow>();

		map__server__in_turns = new HashMap<Server, Set<Turn>>();
		map__server__out_turns = new HashMap<Server, Set<Turn>>();

		map__server__flows = new HashMap<Server, Set<Flow>>();
		map__server__source_flows = new HashMap<Server, Set<Flow>>();

		map__turn__flows = new HashMap<Turn, Set<Flow>>();
	}

	private void remove(Set<Server> servers_to_remove, Set<Turn> turns_to_remove, Set<Flow> flows_to_remove) {
		// Make sure that you do not remove a map's key before the according entries:
		// (flows before servers and turns) & (turns before servers)

		// prevent ConcurrentModificationException
		Set<Flow> flows_to_remove_cpy = new HashSet<Flow>(flows_to_remove);

		for (Flow f : flows_to_remove_cpy) {
			flows.remove(f);
			map__id__flow.remove(f.getId());

			for (Turn t : f.getPath().getTurns()) {
				map__turn__flows.get(t).remove(f);
			}

			for (Server s : f.getPath().getServers()) {
				map__server__flows.get(s).remove(f);
			}

			map__server__source_flows.get(f.getSource()).remove(f);
		}

		// prevent ConcurrentModificationException
		Set<Turn> turns_to_remove_cpy = new HashSet<Turn>(turns_to_remove);

		for (Turn t : turns_to_remove_cpy) {
			turns.remove(t);

			map__turn__flows.remove(t);
			map__server__in_turns.get(t.getDest()).remove(t);
			map__server__out_turns.get(t.getSource()).remove(t);
		}

		// prevent ConcurrentModificationException
		Set<Server> servers_to_remove_cpy = new HashSet<Server>(servers_to_remove);

		for (Server s : servers_to_remove_cpy) {
			servers.remove(s);

			map__id__server.remove(s.getId());

			map__server__flows.remove(s);

			map__server__in_turns.remove(s);
			map__server__out_turns.remove(s);
			map__server__source_flows.remove(s);
		}
	}

	// --------------------------------------------------------------------------------------------
	// Servers
	// --------------------------------------------------------------------------------------------
	// Without given maximum service curve
	public Server addServer(ServiceCurve service_curve) {
		return addServer(service_curve, Curve.getFactory().createZeroDelayInfiniteBurstMSC(),
				Multiplexing.ARBITRARY, false, false);
	}

	public Server addServer(ServiceCurve service_curve, Multiplexing multiplexing) {
		return addServer(service_curve, Curve.getFactory().createZeroDelayInfiniteBurstMSC(), multiplexing,
				true, true);
	}

	public Server addServer(String alias, ServiceCurve service_curve) {
		return addServer(alias, service_curve, Curve.getFactory().createZeroDelayInfiniteBurstMSC(),
				Multiplexing.ARBITRARY, false, false);
	}

	public Server addServer(String alias, ServiceCurve service_curve, Multiplexing multiplexing) {
		Server new_server = new Server(server_id_counter, alias, service_curve.copy(), multiplexing);
		updateServerAdditionInternally(new_server);

		return new_server;
	}

	// With given maximum service curve

	/**
	 * By default the server's use_max_sc and use_max_sc_output_rate are enabled
	 *
	 * @param service_curve
	 *            The server's service curve.
	 * @param max_service_curve
	 *            The server's maximum service curve.
	 * @return The added server.
	 */
	public Server addServer(ServiceCurve service_curve, MaxServiceCurve max_service_curve) {
		return addServer(service_curve, max_service_curve, Multiplexing.ARBITRARY, true, true);
	}

	/**
	 * By default the server's use_max_sc and use_max_sc_output_rate are enabled
	 *
	 * @param alias
	 *            The server's user-defined alias.
	 * @param service_curve
	 *            The server's service curve.
	 * @param max_service_curve
	 *            The server's maximum service curve.
	 * @return The added server.
	 */
	public Server addServer(String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve) {
		return addServer(alias, service_curve, max_service_curve, Multiplexing.ARBITRARY, true, true);
	}

	public Server addServer(ServiceCurve service_curve, MaxServiceCurve max_service_curve, boolean use_max_sc, boolean use_max_sc_output_rate) {
		return addServer(service_curve, max_service_curve, Multiplexing.ARBITRARY, use_max_sc, use_max_sc_output_rate);
	}

	public Server addServer(String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing) {
		return addServer(alias, service_curve, max_service_curve, multiplexing, true, true);
	}

	public Server addServer(ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing) {
		return addServer(service_curve, max_service_curve, multiplexing, true, true);
	}

	public Server addServer(String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve, boolean use_max_sc, boolean use_max_sc_output_rate) {
		return addServer(alias, service_curve, max_service_curve, Multiplexing.ARBITRARY, use_max_sc, use_max_sc_output_rate);
	}

	public Server addServer(ServiceCurve service_curve, MaxServiceCurve max_service_curve,
							Multiplexing multiplexing, boolean use_max_sc, boolean use_max_sc_output_rate) {
		String alias = server_default_name_prefix + Integer.toString(server_id_counter);
		return addServer(alias, service_curve, max_service_curve, multiplexing, use_max_sc, use_max_sc_output_rate);
	}

	public Server addServer(String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve,
							Multiplexing multiplexing, boolean use_max_sc, boolean use_max_sc_output_rate) {
		Server new_server = new Server(server_id_counter, alias, service_curve.copy(), max_service_curve.copy(),
										multiplexing, use_max_sc, use_max_sc_output_rate);
		updateServerAdditionInternally(new_server);

		return new_server;
	}

	private void updateServerAdditionInternally(Server new_server) {
		map__server__in_turns.put(new_server, new HashSet<Turn>());
		map__server__out_turns.put(new_server, new HashSet<Turn>());

		map__server__flows.put(new_server, new HashSet<Flow>());
		map__server__source_flows.put(new_server, new HashSet<Flow>());

		servers.add(new_server);

		Integer integer_object = Integer.valueOf(server_id_counter);
		map__id__server.put(integer_object, new_server);

		server_id_counter++;
	}

	public void removeServer(Server s) throws Exception {
		if (!servers.contains(s)) {
			throw new Exception("Server to be removed is not in this server graph's list of servers");
		}

		remove(Collections.singleton(s), getIncidentTurns(s), map__server__flows.get(s));
	}

	public Set<Flow> getSourceFlows(Server source) {
		return new HashSet<Flow>(map__server__source_flows.get(source));
	}

	public Server getServer(int id) throws Exception {
		if (id < 0 || id > map__id__server.size() - 1) {
			throw new Exception("No server with id " + Integer.toString(id) + " found");
		}

		Server server = map__id__server.get(Integer.valueOf(id));

		if (server == null) {
			throw new Exception("No server with id " + Integer.toString(id) + " found");
		}

		return server;
	}

	public Set<Server> getServers() {
		return new HashSet<Server>(servers);
	}

	public int numServers() {
		return servers.size();
	}

	/**
	 * Returns the subsets of all nodes of the directed graph <code>g</code> that
	 * are neither sources nor sinks, i.e. who have both (a) predecessor(s) and (a)
	 * successor(s) (in-degree &gt; 0 and out-degree &gt; 0).
	 *
	 * @return the set of server nodes
	 */
	public Set<Server> getInOutServers() {
		Set<Server> in_out_servers = new HashSet<Server>();
		for (Server s : servers) {
			if (inDegree(s) > 0 && outDegree(s) > 0) {
				in_out_servers.add(s);
			}
		}
		return in_out_servers;
	}

	/**
	 * Returns the subsets of all nodes of the directed graph <code>g</code> that
	 * are sources, i.e. whose predecessor set is empty (in-degree == 0).
	 *
	 * @return the set of source nodes
	 */
	public Set<Server> getSourceSet() {
		Set<Server> sources = new HashSet<Server>();
		for (Server s : servers) {
			if (inDegree(s) == 0) {
				sources.add(s);
			}
		}
		return sources;
	}

	/**
	 * Returns the subsets of all nodes of the directed graph <code>g</code> that
	 * are sinks, i.e. whose successor set is empty (out-degree == 0).
	 *
	 * @return the set of sink nodes
	 */
	public Set<Server> getSinkSet() {
		Set<Server> sinks = new HashSet<Server>();
		for (Server s : servers) {
			if (outDegree(s) == 0) {
				sinks.add(s);
			}
		}
		return sinks;
	}

	public int degree(Server s) {
		return inDegree(s) + outDegree(s);
	}

	public int inDegree(Server s) {
		return getInTurns(s).size();
	}

	public int outDegree(Server s) {
		return getOutTurns(s).size();
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 *
	 * @param s
	 *            The server whose inturns are returned.
	 * @return The incoming turns of s.
	 */
	public Set<Turn> getInTurns(Server s) {
		Set<Turn> outTurns = map__server__in_turns.get(s);
		if (outTurns == null) {
			return new HashSet<Turn>();
		} else {
			return new HashSet<Turn>(map__server__in_turns.get(s));
		}
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 *
	 * @param s
	 *            The server whose outturns are returned.
	 * @return The outgoing turns of s.
	 */
	public Set<Turn> getOutTurns(Server s) {
		Set<Turn> outTurns = map__server__out_turns.get(s);
		if (outTurns == null) {
			return new HashSet<Turn>();
		} else {
			return new HashSet<Turn>(map__server__out_turns.get(s));
		}
	}

	/**
	 * Returns a new set consisting of references to the turns.
	 *
	 * @param s
	 *            The server whose inturns and outturns are returned.
	 * @return The incident turns.
	 */
	public Set<Turn> getIncidentTurns(Server s) {
		return SetUtils.getUnion(getInTurns(s), getOutTurns(s));
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 *
	 * @param s
	 *            The server whose inturn sources and outturn destinations are
	 *            returned.
	 * @return The neighboring servers of s.
	 */
	public Set<Server> getNeighbors(Server s) {
		return SetUtils.getUnion(getSuccessors(s), getPredecessors(s));
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 *
	 * @param s
	 *            The server whose inturn sources are returned.
	 * @return The source servers of incoming turns of s
	 */
	public Set<Server> getPredecessors(Server s) {
		Set<Server> predecessors = new HashSet<Server>();
		for (Turn t : getInTurns(s)) {
			predecessors.add(t.getSource());
		}
		return predecessors;
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 *
	 * @param s
	 *            The server whose outturn destinations are returned.
	 * @return The sink servers of outgoing turns of s.
	 */
	public Set<Server> getSuccessors(Server s) {
		Set<Server> successors = new HashSet<Server>();
		for (Turn t : getOutTurns(s)) {
			successors.add(t.getDest());
		}
		return successors;
	}

	// --------------------------------------------------------------------------------------------
	// Turns
	// --------------------------------------------------------------------------------------------
	public Turn addTurn(Server source, Server destination) throws Exception {
		String alias = turn_default_name_prefix.concat(Integer.toString(turn_id_counter));
		return addTurn(alias, source, destination);
	}

	public Turn addTurn(String alias, Server source, Server destination) throws Exception {
		if (!servers.contains(source)) {
			throw new Exception("turn's source not present in the server graph");
		}
		if (!servers.contains(destination)) {
			throw new Exception("turn's destination not present in the server graph");
		}

		try {
			// This implicitly signals the caller that the turn was 
			// already present in the server graph
			// by returning a turn with a name different to the given one
			Turn turn = findTurn(source, destination);
			return turn;
		} catch (Exception e) {
			Turn new_turn = new Turn(turn_id_counter, alias, source, destination);
			turn_id_counter++;

			map__turn__flows.put(new_turn, new HashSet<Flow>());

			map__server__in_turns.get(destination).add(new_turn);
			map__server__out_turns.get(source).add(new_turn);

			turns.add(new_turn);
			return new_turn;
		}
	}

	public void removeTurn(Turn t) throws Exception {
		if (!turns.contains(t)) {
			throw new Exception("Turn to be removed is not in this server graph's list of turns");
		}

		remove(new HashSet<Server>(), Collections.singleton(t), map__turn__flows.get(t));
	}

	public Set<Turn> getTurns() {
		return new HashSet<Turn>(turns);
	}

	public int numTurns() {
		return turns.size();
	}

	/**
	 * @param src
	 *            The turn's source.
	 * @param dest
	 *            The turn's destination.
	 * @return The turn from src to dest.
	 * @throws Exception
	 *             No turn from src to snk found in this server graph.
	 */
	public Turn findTurn(Server src, Server dest) throws Exception {
		Set<Turn> connecting_turn_as_set = SetUtils.getIntersection(getInTurns(dest), getOutTurns(src));
		if (connecting_turn_as_set.isEmpty()) {
			throw new Exception("No turn between " + src.toString() + " and " + dest.toString() + " found.");
		} else {
			if (connecting_turn_as_set.size() > 1) {
				throw new Exception("Too many turns between " + src.toString() + " and " + dest.toString() + " found.");
			} else {
				return connecting_turn_as_set.iterator().next();
			}
		}
	}

	// ---------------------------------------------------------------------------------------------
	// Flows
	//
	// INFO: It is not allowed to change the path of a flow,
	// i.e., the user needs to remove the old flow and insert a new one with the
	// modified path
	// ---------------------------------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Flow addFlow(ArrivalCurve arrival_curve, List path) throws Exception {
		String alias = flow_default_name_prefix.concat(Integer.toString(flow_id_counter));
		return addFlow(alias, arrival_curve, path);
	}

	protected Flow addFlow(ArrivalCurve arrival_curve, Path path) throws Exception {
		String alias = flow_default_name_prefix.concat(Integer.toString(flow_id_counter));
		return addFlowToServerGraph(alias, arrival_curve, path);
	}

	/**
	 * Creates a flow and adds it to the server graph.
	 *
	 * @param arrival_curve
	 *            The flow's arrival curve.
	 * @param source
	 *            Source for shortest path routing.
	 * @param sink
	 *            Sink for shortest path routing.
	 * @return The flow created and added to the server graph.
	 * @throws Exception
	 *             Could not create a path from the given source/sink-pair.
	 */
	public Flow addFlow(ArrivalCurve arrival_curve, Server source, Server sink) throws Exception {
		String alias = flow_default_name_prefix.concat(Integer.toString(flow_id_counter));
		return addFlow(alias, arrival_curve, source, sink);
	}

	/**
	 * Creates a flow and adds it to the server graph.
	 *
	 * @param alias
	 *            The flow's user defined-alias.
	 * @param arrival_curve
	 *            The flow's arrival curve.
	 * @param source
	 *            Source for shortest path routing.
	 * @param sink
	 *            Sink for shortest path routing.
	 * @return The flow created and added to the server graph.
	 * @throws Exception
	 *             Could not create a path from the given source/sink-pair.
	 */
	public Flow addFlow(String alias, ArrivalCurve arrival_curve, Server source, Server sink) throws Exception {
		if (source == sink) {
			return addFlow(alias, arrival_curve, sink);
		}

		return addFlowToServerGraph(alias, arrival_curve, getShortestPath(source, sink));
	}

	/**
	 * Creates a flow and adds it to the server graph.
	 *
	 * @param alias
	 *            The flow's user defined-alias.
	 * @param arrival_curve
	 *            The flow's arrival curve.
	 * @param path
	 *            The flows path as a list of servers or turns.
	 * @return The flow created and added to the server graph.
	 * @throws Exception
	 *             The given path is not entirely in the server graph.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Flow addFlow(String alias, ArrivalCurve arrival_curve, List path) throws Exception {
		if (path == null || path.isEmpty()) {
			throw new Exception("The path of a flow cannot be empty");
		}

		Object element_of_path = path.get(0);

		if (element_of_path instanceof Server) {
			return addFlowToServerGraph(alias, arrival_curve, createPathFromServers(path));
		}
		if (element_of_path instanceof Turn) {
			return addFlowToServerGraph(alias, arrival_curve, createPathFromTurns(path));
		}

		throw new Exception("Could not create the path for flow " + alias);
	}

	/**
	 * Creates a flow and adds it to the server graph.
	 *
	 * @param arrival_curve
	 *            The flow's arrival curve.
	 * @param single_hop
	 *            The single hop the flow takes in the server graph.
	 * @return The flow created and added to the server graph.
	 * @throws Exception
	 *             The given server is not in the server graph.
	 */
	public Flow addFlow(ArrivalCurve arrival_curve, Server single_hop) throws Exception {
		String alias = flow_default_name_prefix.concat(Integer.toString(flow_id_counter));
		return addFlow(alias, arrival_curve, single_hop);
	}

	/**
	 * Creates a flow and adds it to the server graph.
	 *
	 * @param alias
	 *            The flow's user defined-alias.
	 * @param arrival_curve
	 *            The flow's arrival curve.
	 * @param single_hop
	 *            The single hop the flow takes in the server graph.
	 * @return The flow created and added to the server graph.
	 * @throws Exception
	 *             The given server is not in the server graph.
	 */
	public Flow addFlow(String alias, ArrivalCurve arrival_curve, Server single_hop) throws Exception {
		return addFlowToServerGraph(alias, arrival_curve, createPath(single_hop));
	}

	// Needed to be named differently due to a collision of the method's signature
	// with a user visible one's
	private Flow addFlowToServerGraph(String alias, ArrivalCurve arrival_curve, Path path) throws Exception {
		if (!servers.containsAll(path.getServers())) {
			throw new Exception("Some servers on the given flow's path are not present in the server graph");
		}
		if (!path.getTurns().isEmpty()) {
			if (!turns.containsAll(path.getTurns())) {
				throw new Exception("Some turns on the given flow's path are not present in the server graph");
			}
		}

		Flow new_flow = new Flow(flow_id_counter, alias, arrival_curve.copy(), path);
		flows.add(new_flow);
		map__id__flow.put(Integer.valueOf(flow_id_counter), new_flow);
		flow_id_counter++;
		
		map__server__source_flows.get(path.getSource()).add(new_flow);

		for (Turn t : path.getTurns()) {
			map__turn__flows.get(t).add(new_flow);
		}
		for (Server s : path.getServers()) {
			map__server__flows.get(s).add(new_flow);
		}

		return new_flow;
	}

	/**
	 * Removes a flow from the server graph.
	 *
	 * @param f
	 *            The flow to be removed.
	 * @throws Exception
	 *             Flow to be removed is not in this server graph's list of flows.
	 */
	public void removeFlow(Flow f) throws Exception {
		if (!flows.contains(f)) {
			throw new Exception("Flow to be removed is not in this server graph's list of flows");
		}

		remove(new HashSet<Server>(), new HashSet<Turn>(), Collections.singleton(f));
	}

	public Set<Flow> getFlows() {
		return new HashSet<Flow>(flows);
	}

	public int numFlows() {
		return flows.size();
	}

	public Flow getFlow(int id) throws Exception {
		if (id < 0 || id > map__id__flow.size() - 1) {
			throw new Exception("No flow with id " + Integer.toString(id) + " found");
		}

		Flow flow = map__id__flow.get(Integer.valueOf(id));

		if (flow == null) {
			throw new Exception("No flow with id " + Integer.toString(id) + " found");
		}

		return flow;
	}

	public Set<Flow> getFlows(Turn t) {
		if (t == null) {
			return new HashSet<Flow>();
		}

		Set<Flow> flows = map__turn__flows.get(t);
		if (flows != null) {
			return new HashSet<Flow>(flows);
		} else {
			return new HashSet<Flow>();
		}
	}

	public Set<Flow> getFlows(Set<Turn> turns) {
		HashSet<Flow> flows = new HashSet<Flow>();

		for (Turn t : turns) {
			flows.addAll(map__turn__flows.get(t));
		}

		return flows;
	}

	public Set<Flow> getFlows(Server s) {
		if (s == null) {
			return new HashSet<Flow>();
		}

		Set<Flow> flows = map__server__flows.get(s);
		if (flows != null) {
			return new HashSet<Flow>(flows);
		} else {
			return new HashSet<Flow>();
		}
	}

	/**
	 * Finds all the flows that cross at least one server on the path p.
	 *
	 * @param p
	 *            The path on which the returned flows intersect.
	 * @return Set of flows that cross at least one server on the path.
	 * @throws Exception
	 */
	public Set<Flow> getFlows(Path p) throws Exception {
		Set<Flow> result = new HashSet<Flow>();
		for (Set<Flow> flows : getFlowsPerServer(p, new HashSet<Flow>()).values()) {
			result.addAll(flows);
		}
		return result;
	}

	/**
	 * Finds all the flows that have exactly path p as their path.
	 * @param p
	 *            The path which all the returned flows have.
	 * @return Set of flows that have p as their path.
	 * @throws Exception
	 *            If the path is null.
	 */
	public Set<Flow> getFlowsByPath(Path p) throws Exception {
		// Throw a runtime exception if p is null.
		if(p == null) {
			throw new RuntimeException("Path argument for method getFlowsByPath(Path p) in ServerGraph was null.");
		}
		Set<Flow> result = new HashSet<Flow>();
		// All the flows that start at the first server of the path p are potential candidates.
		Set<Flow> flows_source_p = getFlows(p.getSource());
		for(Flow f : flows_source_p){
			if(f.getPath().equals(p)){
				result.add(f);
			}
		}
		return result;
	}

	/**
	 * Do not confuse with getServerJoiningFlowsMap
	 *
	 * @param p
	 *            The path to split up into its hops and analyze for flows.
	 * @param excluded_flows
	 *            Flows to omit in the sorting operation.
	 * @return Map from a server to the flows present at this servers.
	 * @throws Exception
	 */
	public Map<Server, Set<Flow>> getFlowsPerServer(Path p, Set<Flow> excluded_flows) throws Exception {
		Map<Server, Set<Flow>> map__server__set_flows = new HashMap<Server, Set<Flow>>();

		Set<Flow> set_set_flows;
		for (Server s : p.getServers()) {
			set_set_flows = getFlows(s); // No need to create another new instance of HashMap
			set_set_flows.removeAll(excluded_flows);
			map__server__set_flows.put(s, set_set_flows);
		}
		return map__server__set_flows;
	}

	/**
	 * For every distinct sub-path of p this method returns the flows taking it
	 * entirely.
	 *
	 * @param p
	 *            The path to split up and analyze for flows.
	 * @return Map from a path to the flows taking exactly this tandem of servers.
	 * @throws Exception
	 */
	public Map<Path, Set<Flow>> getFlowsPerSubPath(Path p) throws Exception {
		Map<Path, Set<Flow>> map__path__set_flows = new HashMap<Path, Set<Flow>>();

		Map<Server, Set<Flow>> map__s_i__joining_flows = getServerJoiningFlowsMap(p);
		Set<Flow> all_interfering_flows = new HashSet<Flow>();
		for (Set<Flow> flows_on_path : map__s_i__joining_flows.values()) {
			all_interfering_flows.addAll(flows_on_path);
		}
		if (all_interfering_flows.isEmpty()) { // If there are no interfering flows to be considered, return the
			// convolution of a ll service curves on the path
			return map__path__set_flows;
		}

		// Iterate over the servers s on the path. Use indices to easily determine
		// egress servers
		LinkedList<Server> servers = p.getServers();
		int n = servers.size();

		// Flows with the same egress server (independent of the out turn) can be
		// aggregated for PMOO's and OBA's arrival bound calculation.
		// This still preserves the demultiplexing considerations. Note that the last
		// server contains all remaining flows by default.
		Map<Server, Set<Flow>> map__server__leaving_flows = getServerLeavingFlowsMap(p);

		for (int i = 0; i < n; i++) {
			Server s_i = servers.get(i);

			Set<Flow> s_i_ingress = map__s_i__joining_flows.get(s_i);
			if (s_i_ingress.isEmpty()) {
				continue;
			}

			for (int j = i; j < n; j++) {
				Server s_j_egress = servers.get(j);

				Set<Flow> s_i_ingress__s_j_egress = SetUtils.getIntersection(s_i_ingress,
						map__server__leaving_flows.get(s_j_egress)); // Intersection with the remaining joining_flows
				// prevents rejoining flows to be considered
				// multiple times

				if (s_i_ingress__s_j_egress.isEmpty()) { // No such flows to bound
					continue;
				}

				map__path__set_flows.put(p.getSubPath(s_i, s_j_egress), s_i_ingress__s_j_egress);

				// Remove the flows otherwise rejoining flows will occur multiple times with
				// wrong paths
				s_i_ingress.removeAll(s_i_ingress__s_j_egress);
			}
		}
		return map__path__set_flows;
	}

	public Map<Path, Set<Flow>> groupFlowsPerSubPath(Path p, Set<Flow> flows_to_group) throws Exception {
		Map<Path, Set<Flow>> map__path__set_flows = new HashMap<Path, Set<Flow>>();

		Map<Server, Set<Flow>> map__s_i__joining_flows_tmp = getServerJoiningFlowsMap(p);
		Map<Server, Set<Flow>> map__s_i__joining_flows = new HashMap<Server, Set<Flow>>();

		Set<Flow> tmp_set;
		for (Entry<Server, Set<Flow>> s_i__joining_flows : map__s_i__joining_flows_tmp.entrySet()) {
			tmp_set = s_i__joining_flows.getValue();
			tmp_set.retainAll(flows_to_group);
			map__s_i__joining_flows.put(s_i__joining_flows.getKey(), tmp_set);
		}

		// Iterate over the servers s on the path. Use indices to easily determine
		// egress servers
		LinkedList<Server> servers = p.getServers();
		int n = servers.size();

		// Flows with the same egress server (independent of the out turn) can be
		// aggregated for PMOO's and OBA's arrival bound calculation.
		// This still preserves the demultiplexing considerations. Note that the last
		// server contains all remaining flows by default.
		Map<Server, Set<Flow>> map__server__leaving_flows = getServerLeavingFlowsMap(p);

		for (int i = 0; i < n; i++) {
			Server s_i = servers.get(i);

			Set<Flow> s_i_ingress = map__s_i__joining_flows.get(s_i);
			if (s_i_ingress.isEmpty()) {
				continue;
			}

			for (int j = i; j < n; j++) {
				Server s_j_egress = servers.get(j);

				Set<Flow> s_i_ingress__s_j_egress = SetUtils.getIntersection(s_i_ingress,
						map__server__leaving_flows.get(s_j_egress)); // Intersection with the remaining joining_flows
				// prevents rejoining flows to be considered
				// multiple times
				s_i_ingress__s_j_egress.retainAll(flows_to_group);

				if (s_i_ingress__s_j_egress.isEmpty()) { // No such flows to bound
					continue;
				}

				map__path__set_flows.put(p.getSubPath(s_i, s_j_egress), s_i_ingress__s_j_egress);

				// Remove the flows otherwise rejoining flows will occur multiple times with
				// wrong paths
				s_i_ingress.removeAll(s_i_ingress__s_j_egress);
			}
		}
		return map__path__set_flows;
	}
	
	// TODO Unify with groupFlowsPerInturnSubPath
	private Map<Pair<Server,Path>,Set<Flow>> groupFlowsPerSubPathInternal( Path p, Set<Flow> flows_to_group ) throws Exception {
		Map<Pair<Server,Path>,Set<Flow>> map__path__set_flows = new HashMap<Pair<Server,Path>,Set<Flow>>();
				
		Map<Server,Set<Flow>> map__s_i__joining_flows_tmp = getServerJoiningFlowsMap( p );
		Map<Server,Set<Flow>> map__s_i__joining_flows = new HashMap<Server,Set<Flow>>();
		
		Set<Flow> tmp_set;
		for( Entry<Server,Set<Flow>> s_i__joining_flows : map__s_i__joining_flows_tmp.entrySet() ) {
			tmp_set = s_i__joining_flows.getValue();
			tmp_set.retainAll( flows_to_group );
			map__s_i__joining_flows.put( s_i__joining_flows.getKey(), tmp_set );
		}
		
		// Iterate over the servers s on the path. Use indices to easily determine egress servers 
		LinkedList<Server> servers = p.getServers();
		int n = servers.size();
		
		// Flows with the same egress server (independent of the out turn) can be aggregated for PMOO's and OBA's arrival bound calculation.
		// This still preserves the demultiplexing considerations. Note that the last server contains all remaining flows by default.
 		Map<Server,Set<Flow>> map__server__leaving_flows = getServerLeavingFlowsMap( p );
		
		for( int i = 0; i<n; i++ ) {
			Server s_i = servers.get( i );
			
			Set<Flow> s_i_ingress = map__s_i__joining_flows.get( s_i );
			if ( s_i_ingress.isEmpty() ) {
				continue;
			}

	 		for( int j = i; j<n; j++ ) {
	 			Server s_j_egress = servers.get( j );
	 			
	 			Set<Flow> s_i_ingress__s_j_egress = SetUtils.getIntersection( s_i_ingress, map__server__leaving_flows.get( s_j_egress ) ); // Intersection with the remaining joining_flows prevents rejoining flows to be considered multiple times
	 			s_i_ingress__s_j_egress.retainAll( flows_to_group );
	 			
	 			if ( s_i_ingress__s_j_egress.isEmpty() ) { // No such flows to bound
	 				continue;
	 			}
	 			
	 			// TODO Put sorting by inturn here

		 		map__path__set_flows.put( new Pair<Server,Path>( s_i, p.getSubPath(s_i, s_j_egress) ), s_i_ingress__s_j_egress );
				
				// Remove the flows otherwise rejoining flows will occur multiple times with wrong paths
				s_i_ingress.removeAll( s_i_ingress__s_j_egress );
			}
		}
		return map__path__set_flows;
	}
	
	/**
	 * 
	 * @param p
	 * @param flows_to_group
	 * @return
	 * @throws Exception
	 */
	public Map<Pair<Turn,Path>,Set<Flow>> groupFlowsPerInturnSubPath( Path p, Set<Flow> flows_to_group ) throws Exception {
		Map<Pair<Server,Path>,Set<Flow>> starting_set = groupFlowsPerSubPathInternal( p, flows_to_group );
		Map<Pair<Turn,Path>,Set<Flow>> results_set = new HashMap<Pair<Turn,Path>,Set<Flow>>();
		
		Set<Flow> flows_on_t, flows_on_turn_grouped = new HashSet<Flow>();
		for ( Entry<Pair<Server,Path>,Set<Flow>> entry : starting_set.entrySet() ) {
			
			flows_on_turn_grouped.clear(); // Reusing this set reference works because the getDifference below creates a new set to return.
			for( Turn on_t : getInTurns(  entry.getKey().getFirst() ) ) {
				
				flows_on_t = SetUtils.getIntersection( getFlows( on_t ), entry.getValue() );
				if( !flows_on_t.isEmpty() ) {
					results_set.put( new Pair<Turn,Path>( on_t, entry.getKey().getSecond() ), flows_on_t );
					flows_on_turn_grouped.addAll( new HashSet<Flow>( flows_on_t ) );
				}
			}
			
			Set<Flow> remaining_flows = SetUtils.getDifference( entry.getValue(), flows_on_turn_grouped );
			if( !remaining_flows.isEmpty() ) {
				Turn dummy = new Turn( -1, "dummy", entry.getKey().getFirst(), entry.getKey().getFirst() );
				Pair<Turn,Path> turn_path = new Pair<Turn,Path>( dummy, entry.getKey().getSecond() );
				results_set.put( turn_path, remaining_flows );
			}
		}
		
		return results_set;
	}

	/**
	 * Returns an aggregate arrival curve for all flows originating in
	 * <code>source</code>.
	 * <p>
	 * Returns a null curve if there are no source flows at <code>source</code>.
	 *
	 * @param source
	 *            The source of all flows to be aggregated.
	 * @return An aggregate arrival curve.
	 */
	public ArrivalCurve getSourceFlowArrivalCurve(Server source) {
		return getSourceFlowArrivalCurve(source, getSourceFlows(source));
	}

	/**
	 * Returns an aggregate arrival curve for all flows originating in
	 * <code>source</code> and contained in the set <code>outgoing_flows</code>.
	 * <p>
	 * Returns a null curve if the intersection of those sets of curves is empty.
	 *
	 * @param source
	 *            The source of all flows to be aggregated.
	 * @param source_flows
	 *            The set of all flows to be aggregated.
	 * @return An aggregate arrival curve.
	 */
	public ArrivalCurve getSourceFlowArrivalCurve(Server source, Set<Flow> source_flows) {
		ArrivalCurve a_out = Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();

		// Returns an empty set if one of the arguments is null
		Set<Flow> source_flows_internal = SetUtils.getIntersection(map__server__source_flows.get(source), source_flows);
		if (source_flows_internal.isEmpty()) {
			return a_out;
		} else {
			if (source_flows_internal != null) {
				if(source_flows_internal.size() == 1) {
					a_out = source_flows_internal.iterator().next().getArrivalCurve();
				} else {
					for (Flow f : source_flows_internal) {
						a_out = Curve.getUtils().add(a_out, f.getArrivalCurve());
					}
				}
			}
		}
		return a_out;
	}

	// --------------------------------------------------------------------------------------------
	// Paths
	// --------------------------------------------------------------------------------------------
	public Path createPath(Server server) throws Exception {
		if (!servers.contains(server)) {
			throw new Exception("Server to create path from is not in the server graph");
		}

		return new Path(new LinkedList<Server>(Collections.singleton(server)), new LinkedList<Turn>());
	}

	public Path createPath(Turn t) throws Exception {
		if (!turns.contains(t)) { // Implicitly contains the case that at least one server the turn connects is
			// not in the server graph
			throw new Exception("Turn to create path from is not in the server graph");
		}

		LinkedList<Server> path_servers = new LinkedList<Server>();
		path_servers.add(t.getSource());
		path_servers.add(t.getDest());

		return new Path(path_servers, new LinkedList<Turn>(Collections.singleton(t)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Path createPath(List path) throws Exception {
		if (path == null || path.isEmpty()) {
			throw new Exception("The path of a flow cannot be empty");
		}

		Object element_of_path = path.get(0);

		if (element_of_path instanceof Server) {
			return createPathFromServers(path);
		}
		if (element_of_path instanceof Turn) {
			return createPathFromTurns(path);
		}

		throw new Exception("Could not create path");
	}

	public Path createPathFromServers(List<Server> path_servers) throws Exception {
		List<Turn> path_turns = new LinkedList<Turn>();

		// Sanity check + path_turns construction:
		if (path_servers.size() > 1) {
			for (int i = 0; i < path_servers.size() - 1; i++) {
				try {
					path_turns.add(findTurn(path_servers.get(i), path_servers.get(i + 1)));
				} catch (Exception e) {
					throw new Exception(
							"At least two consecutive servers to create the path from are not connected by a turn in the server graph");
				}
			}
		}

		return createPath(path_servers, path_turns);
	}

	private Path createPathFromTurns(List<Turn> path_turns) throws Exception {
		LinkedList<Server> path_servers = new LinkedList<Server>();

		// Sanity checks + path_servers construction:
		for (int i = 0; i < path_turns.size() - 1; i++) {
			Turn t_i = path_turns.get(i);
			if (!turns.contains(t_i)) {
				throw new Exception("At least one turn to create path from is not in the server graph");
			}
			if (t_i.getDest() != path_turns.get(i + 1).getSource()) {
				throw new Exception("At least two consecutive turns to create the path from are not connected");
			}
			path_servers.add(t_i.getSource());
		}
		Turn t_last = path_turns.get(path_turns.size() - 1);
		if (!turns.contains(t_last)) {
			throw new Exception("Last turn to create path from is not in the server graph");
		}
		path_servers.add(t_last.getSource());
		path_servers.add(t_last.getDest());

		return new Path(path_servers, path_turns);
	}

	public Path createPath(List<Server> path_servers, List<Turn> path_turns) throws Exception {
		// Sanity checks:
		if (path_servers.isEmpty()) {
			throw new Exception("A path without servers cannot be created.");
		}
		if (!path_turns.isEmpty()) {
			for (int i = 0; i < path_turns.size() - 1; i++) {
				Turn t_i = path_turns.get(i);

				if (!turns.contains(t_i)) { // Implicitly contains the case that at least one server the turn connects
					// is not in the server graph
					throw new Exception("At least one turn to create path from is not in the server graph");
				}
				if (t_i.getDest() != path_turns.get(i + 1).getSource()) {
					throw new Exception("At least two consecutive turns to create the path from are not connected");
				}

				if (path_servers.get(i) != t_i.getSource() || path_servers.get(i + 1) != t_i.getDest()) {
					throw new Exception(
							"At least two consecutive servers to create the path from are not connected by the corresponding turn of the given list");
				}
			}
			Turn t_last = path_turns.get(path_turns.size() - 1);
			if (!turns.contains(t_last)) { // Implicitly contains the case that at least one server the turn connects is
				// not in the server graph
				throw new Exception("Last turn to create path from is not in the server graph");
			}
		}

		return new Path(path_servers, path_turns);
	}


	/**
	 * @param path
	 *            The tandem of servers to check for flows.
	 * @return The joining flows.
	 * @throws Exception
	 *             Could not derive the joining flows even if there are.
	 */
	public Map<Server, Set<Flow>> getServerJoiningFlowsMap(Path path) throws Exception {
		Map<Server, Set<Flow>> map__server__joining_flows = new HashMap<Server, Set<Flow>>();

		Server path_source = path.getSource();
		LinkedList<Server> servers_iteration = path.getServers();
		servers_iteration.remove(path_source);

		// Default for first server
		Set<Flow> flows_joining = getFlows(path_source);
		map__server__joining_flows.put(path_source, flows_joining);

		for (Server s : servers_iteration) {
			flows_joining = SetUtils.getDifference(getFlows(s), getFlows(path.getPrecedingTurn(s)));

			// Results in an empty set if there a no joining flow at server s
			map__server__joining_flows.put(s, flows_joining);
		}

		return map__server__joining_flows;
	}

	// All flows that leave the path at the server
	public Map<Server, Set<Flow>> getServerLeavingFlowsMap(Path path) throws Exception {
		Map<Server, Set<Flow>> map__server__leaving_flows = new HashMap<Server, Set<Flow>>();

		Server path_sink = path.getSink();
		LinkedList<Server> servers_iteration = path.getServers();
		servers_iteration.remove(path_sink);

		for (Server s : servers_iteration) {
			// Results in an empty set if there a no joining flow at server s
			map__server__leaving_flows.put(s, SetUtils.getDifference(getFlows(s), getFlows(path.getSucceedingTurn(s))));
		}

		// Default for last server
		map__server__leaving_flows.put(path_sink, getFlows(path_sink));

		return map__server__leaving_flows;
	}

	/**
	 * Calculates the shortest path between src and snk according to Dijkstra's
	 * algorithm
	 *
	 * @param src
	 *            The path's source.
	 * @param snk
	 *            The path's sink.
	 * @return Dijkstra shortest path from src to snk.
	 * @throws Exception
	 *             Could not find a shortest path for some reason.
	 */
	public Path getShortestPath(Server src, Server snk) throws Exception {
		Set<Server> visited = new HashSet<Server>();

		Map<Server, LinkedList<Turn>> paths_turns = new HashMap<Server, LinkedList<Turn>>();
		Map<Server, LinkedList<Server>> paths_servers = new HashMap<Server, LinkedList<Server>>();

		paths_turns.put(src, new LinkedList<Turn>());
		paths_servers.put(src, new LinkedList<Server>(Collections.singleton(src)));

		LinkedList<Server> queue = new LinkedList<Server>();
		queue.add(src);
		visited.add(src);

		while (!queue.isEmpty()) {
			Server s = queue.getLast();
			queue.remove(s);

			Set<Server> successors_s = getSuccessors(s);
			for (Server successor : successors_s) {

				LinkedList<Turn> path_turns_tmp = new LinkedList<Turn>(paths_turns.get(s));

				LinkedList<Server> path_servers_tmp;
				if (paths_servers.containsKey(s)) {
					path_servers_tmp = new LinkedList<Server>(paths_servers.get(s));
				} else {
					path_servers_tmp = new LinkedList<Server>(Collections.singleton(src));
				}

				path_turns_tmp.add(findTurn(s, successor));
				path_servers_tmp.add(successor);

				if (!visited.contains(successor)) {
					paths_turns.put(successor, path_turns_tmp);
					paths_servers.put(successor, path_servers_tmp);

					queue.add(successor);
					visited.add(successor);
				} else {
					if (paths_turns.get(successor).size() > path_turns_tmp.size()) {
						paths_turns.put(successor, path_turns_tmp);
						paths_servers.put(successor, path_servers_tmp);

						queue.add(successor);
					}
				}
			}
		}

		if (paths_turns.get(snk) == null) {
			throw new Exception("No path from server " + src.getId() + " to server " + snk.getId() + " found");
		}

		// No sanity checks needed after a shortest path calculation, so you can create
		// a new path directly instead of calling 'createPath'
		return new Path(paths_servers.get(snk), paths_turns.get(snk));
	}

	/**
	 * Returns the server at which the flows in <code>flows_of_interest</code> first
	 * all meet each other (when viewed from the source). When viewed from
	 * <code>server</code> towards the sink, this is the last server where all flows
	 * are still together.
	 *
	 * @param server_common_dest
	 *            The common destination, excluded as a potential splitting server.
	 * @param flows_of_interest
	 *            The flows whose common subpath before server_common_dest is computed.
	 * @return the splitting point server The common source.
	 * @throws Exception
	 *             No splitting point prior to the common destination found.
	 */
	public Server findSplittingServer(Server server_common_dest, Set<Flow> flows_of_interest) throws Exception {
		Flow f = flows_of_interest.iterator().next();

		if (flows_of_interest.size() == 1) {
			return f.getSource();
		}

		Path f_path = f.getPath();
		int common_dest_index_f = f_path.getServers().indexOf(server_common_dest);

		Server split = server_common_dest;
		// Iterate in reverse order starting from server_common_dest, stop as soon as at
		// least one of the flows of interest is missing
		for (int i = common_dest_index_f - 1; i >= 0; i--) { // -1 excludes server_common_dest
			Server split_candidate = f_path.getServers().get(i);

			if (getFlows(split_candidate).containsAll(flows_of_interest)) {
				split = split_candidate;
			} else {
				break;
			}
		}

		if (split == server_common_dest) { // No splitting point found
			throw new Exception("No splitting point prior to the common destination found");
		} else {
			return split;
		}
	}

	// code copied, see findSplittingServer. We do not care about the case server_common_dest == split (we do not throw an exception in that case and simply return server_common_dest). Also we
	// return the path from split to server_common_dest we backtracked.
	public Pair<Server, Path> findSplittingServerAndPathFIFO(Server server_common_dest, Set<Flow> flows_of_interest) throws Exception {
		Flow f = flows_of_interest.iterator().next();

		if (flows_of_interest.size() == 1) {
			Server start_server = f.getSource();
			return new Pair(start_server, flows_of_interest.iterator().next().getSubPath(start_server, server_common_dest) );
		}

		Path f_path = f.getPath();
		int common_dest_index_f = f_path.getServers().indexOf(server_common_dest);

		Server split = server_common_dest;
		// Iterate in reverse order starting from server_common_dest, stop as soon as at
		// least one of the flows of interest is missing
		for (int i = common_dest_index_f - 1; i >= 0; i--) { // -1 excludes server_common_dest
			Server split_candidate = f_path.getServers().get(i);

			if (getFlows(split_candidate).containsAll(flows_of_interest)) {
				split = split_candidate;
			} else {
				break;
			}
		}
		return new Pair(split, f_path.getSubPath(split, server_common_dest));
	}

	// --------------------------------------------------------------------------------------------
	// Other helper functions
	// --------------------------------------------------------------------------------------------

	/**
	 * Creates a deep copy of this server graph.
	 *
	 * @return The copy.
	 * @throws Exception
	 *             Signals problems while instantiating the copy.
	 */
	public ServerGraph copy() throws Exception {
		ServerGraph sg_new = new ServerGraph();

		// Copy servers
		// We cannot use some addServer( s_old.copy() ) because servers can only be
		// created via a server graph.
		// They need an server graph determined id. (Hard design decision at the moment)
		Map<Server, Server> map__s_old__s_new = new HashMap<Server, Server>();
		Server s_new;

		for (Server s_old : servers) {
			s_new = sg_new.addServer(s_old.getAlias(), s_old.getServiceCurve().copy(),
					s_old.getMaxServiceCurve().copy(), s_old.multiplexing(), s_old.useMaxSC(),
					s_old.useMaxScRate());
			map__s_old__s_new.put(s_old, s_new);
		}

		// Copy turns
		// We cannot use some addTurn( t_old.copy() ) because turns can only be created
		// via a server graph.
		// They need an server graph determined id. (Hard design decision at the moment)
		Map<Turn, Turn> map__t_old__t_new = new HashMap<Turn, Turn>();
		Turn t_new;

		for (Turn t_old : turns) {
			t_new = sg_new.addTurn(t_old.getAlias(), map__s_old__s_new.get(t_old.getSource()),
					map__s_old__s_new.get(t_old.getDest()));
			map__t_old__t_new.put(t_old, t_new);
		}

		// Copy flows
		// We cannot use some addFlow( f_old.copy() ) because flows can only be created
		// via a server graph.
		// They need an server graph determined id. (Hard design decision at the moment)
		Path f_old_path;
		Path f_new_path;

		List<Server> f_path_old_s;
		List<Server> f_path_new_s;

		List<Turn> f_path_old_t;
		List<Turn> f_path_new_t;

		for (Flow f_old : flows) {
			f_old_path = f_old.getPath();

			f_path_old_s = f_old_path.getServers();
			f_path_new_s = new LinkedList<Server>();
			for (Server s : f_path_old_s) {
				f_path_new_s.add(map__s_old__s_new.get(s));
			}

			f_path_old_t = f_old_path.getTurns();
			f_path_new_t = new LinkedList<Turn>();
			for (Turn t : f_path_old_t) {
				f_path_new_t.add(map__t_old__t_new.get(t));
			}

			f_new_path = new Path(f_path_new_s, f_path_new_t);
			sg_new.addFlowToServerGraph(f_old.getAlias(), f_old.getArrivalCurve(), f_new_path);
		}

		return sg_new;
	}

	public void saveAs(String output_path, String file_name) throws Exception {
		saveAs(output_path, file_name, "default");
	}

	public void saveAs(String output_path, String file_name, String package_name) throws Exception {
		if (output_path.charAt(output_path.length() - 1) != '/') {
			output_path = output_path + "/";
		}
		String file_extension = ".java";
		if (file_name.endsWith(file_extension)) {
			file_name = file_name.substring(0, file_name.length() - 5);
		}

		StringBuffer sb = new StringBuffer();

		sb.append("/* \n");
		sb.append(" * This file is compatible with the Deterministic Network Calculator (DNC) v2.6.\n");
		sb.append(" *\n");
		sb.append(" * The Deterministic Network Calculator (DNC) is free software;\n");
		sb.append(" * you can redistribute it and/or modify it under the terms of the \n");
		sb.append(" * GNU Lesser General Public License as published by the Free Software Foundation; \n");
		sb.append(" * either version 2.1 of the License, or (at your option) any later version.\n");
		sb.append(" * \n");
		sb.append(" * This library is distributed in the hope that it will be useful,\n");
		sb.append(" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
		sb.append(" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU\n");
		sb.append(" * Lesser General Public License for more details.\n");
		sb.append(" * \n");
		sb.append(" * You should have received a copy of the GNU Lesser General Public\n");
		sb.append(" * License along with this library; if not, write to the Free Software\n");
		sb.append(" * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA\n");
		sb.append(" * \n");
		sb.append(" */ \n");
		sb.append("\n");

		sb.append("package " + package_name + ";\n");
		sb.append("\n");
		sb.append("import java.util.LinkedList;\n");
		sb.append("\n");
		sb.append("import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;\n");
		sb.append("import org.networkcalculus.dnc.curves.Curve;\n");
		sb.append("import org.networkcalculus.dnc.curves.CurveFactory_Affine;\n");
		sb.append("import org.networkcalculus.dnc.network.server_graph.Server;\n");
		sb.append("import org.networkcalculus.dnc.network.server_graph.ServerGraph;\n");
		sb.append("import org.networkcalculus.dnc.network.server_graph.ServerGraphFactory;\n");
		sb.append("\n");

		sb.append("public class " + file_name + " implements ServerGraphFactory {");
		sb.append("\n");
		sb.append("\tprivate ServerGraph sg;\n");
		sb.append("\tprivate static CurveFactory_Affine factory = Curve.getFactory();\n");
		sb.append("\n");

		// Server creation
		int i_servers_func = 1;
		int i_servers_lines = 0;
		sb.append("\tpublic void createServers" + Integer.toString(i_servers_func)
				+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
		
		for (Server s : servers) {
			sb.append("\t\tservers[" + s.getId() + "] = ");
			sb.append("sg.addServer( ");
			sb.append("\"" + s.getAlias() + "\"" + ", ");
			sb.append("factory.createServiceCurve( \"" + s.getServiceCurve().toString() + "\" )"
					+ ", ");
			sb.append("factory.createMaxServiceCurve( \"" + s.getMaxServiceCurve().toString()
					+ "\" )" + ", ");
			sb.append("Multiplexing." + s.multiplexing() + ", ");
			sb.append(s.useMaxSC() + ", ");
			sb.append(s.useMaxScRate());
			sb.append(" );\n");

			i_servers_lines++;

			if ((i_servers_lines / 500) >= i_servers_func) {
				i_servers_func++;

				sb.append("\t}\n");
				sb.append("\n");
				sb.append("\tpublic void createServers" + Integer.toString(i_servers_func)
						+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
			}
		}
		sb.append("\t}\n");
		sb.append("\n");

		// Turn creation
		int i_turns_func = 1;
		int i_turns_lines = 0;
		sb.append("\tpublic void createTurns" + Integer.toString(i_turns_func)
				+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
		for (Turn t : turns) {
			sb.append("\t\tsg.addTurn( ");
			sb.append("\"" + t.getAlias() + "\"" + ", ");
			sb.append("servers[" + t.getSource().getId() + "]" + ", ");
			sb.append("servers[" + t.getDest().getId() + "]");
			sb.append(" );\n");

			i_turns_lines++;

			if ((i_turns_lines / 500) >= i_turns_func) {
				i_turns_func++;

				sb.append("\t}\n");
				sb.append("\n");
				sb.append("\tpublic void createTurns" + Integer.toString(i_turns_func)
						+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
			}
		}
		sb.append("\t}\n");
		sb.append("\n");

		// Flow creation
		int i_flows_func = 1;
		int i_flows_lines = 0;
		sb.append("\tpublic void createFlows" + Integer.toString(i_flows_func)
				+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
		sb.append("\t\tLinkedList<Server> servers_on_path_s = new LinkedList<Server>();\n");
		i_flows_lines++;
		sb.append("\n");
		for (Flow f : flows) {
			for (Server s : f.getServersOnPath()) {
				sb.append("\t\tservers_on_path_s.add( ");
				sb.append("servers[" + s.getId() + "]");
				sb.append(" );\n");
				i_flows_lines++;
			}
			sb.append("\t\tsg.addFlow( ");
			sb.append("\"" + f.getAlias() + "\"" + ", ");
			sb.append("factory.createArrivalCurve( \"" + f.getArrivalCurve().toString() + "\" )"
					+ ", ");
			sb.append("servers_on_path_s");
			sb.append(" );\n");
			sb.append("\t\tservers_on_path_s.clear();\n");
			sb.append("\n");

			i_flows_lines += 3;

			if ((i_flows_lines / 500) >= i_flows_func) {
				i_flows_func++;

				sb.append("\t}\n");
				sb.append("\n");
				sb.append("\tpublic void createFlows" + Integer.toString(i_flows_func)
						+ "( ServerGraph sg, Server[] servers ) throws Exception {\n");
				sb.append("\t\tLinkedList<Server> servers_on_path_s = new LinkedList<Server>();\n");
				i_flows_lines++;
			}
		}
		sb.append("\t}\n");

		sb.append("\n");
		sb.append("\tpublic " + file_name + "() {\n");
		sb.append("\t\tsg = createServerGraph();\n");
		sb.append("\t}\n");
		sb.append("\n");

		sb.append("\tpublic ServerGraph createServerGraph() {\n");
		sb.append("\t\tServer[] servers = new Server[" + numServers() + "];\n");
		sb.append("\t\tsg = new ServerGraph();\n");
		sb.append("\t\ttry{\n");
		for (int i = 1; i <= i_servers_func; i++) {
			sb.append("\t\t\tcreateServers" + Integer.toString(i) + "( sg, servers );\n");
		}
		for (int i = 1; i <= i_turns_func; i++) {
			sb.append("\t\t\tcreateTurns" + Integer.toString(i) + "( sg, servers );\n");
		}
		for (int i = 1; i <= i_flows_func; i++) {
			sb.append("\t\t\tcreateFlows" + Integer.toString(i) + "( sg, servers );\n");
		}

		sb.append("\t\t} catch (Exception e) {\n");
		sb.append("\t\t\te.printStackTrace();\n");
		sb.append("\t\t}\n");
		sb.append("\t\treturn sg;\n");
		sb.append("\t}\n");
		sb.append("\n");

		sb.append("\tpublic ServerGraph getServerGraph() {\n");
		sb.append("\t\treturn sg;\n");
		sb.append("\t}\n");
		sb.append("\n");

		sb.append("\tpublic void reinitializeCurves() {\n");
		sb.append("\t\tsg = createServerGraph();\n");
		sb.append("\t}\n");

		sb.append("}\n");

		File output = new File(output_path, file_name + file_extension);
		if( !Files.exists( Paths.get( output_path ))) {
			File tmp = output.getParentFile();
			if (!tmp.mkdirs()) {
				throw new Exception("Could not create output path, although it did not exist.");
			}
		}
		Files.write(output.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	/**
	 *
	 * Print the server graph in pseudo code.
	 *
	 */
	public String toString() {
		if (numServers() == 0) {
			return "empty server graph.";
		}

		StringBuffer sg_str = new StringBuffer();
		for (Server s : servers) {
			sg_str.append(s.toString());
			sg_str.append("\n");
		}

		sg_str.append("\n");
		for (Turn t : turns) {
			sg_str.append(t.toString());
			sg_str.append("\n");
		}

		sg_str.append("\n");
		for (Flow f : flows) {
			sg_str.append(f.toString());
			sg_str.append("\n");
		}

		sg_str.deleteCharAt(sg_str.length() - 1); // Remove the line break at the end.

		return sg_str.toString();
	}
}
