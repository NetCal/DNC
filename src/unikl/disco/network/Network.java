/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2011 - 2017 Steffen Bondorf
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

package unikl.disco.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.MaxServiceCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.misc.SetUtils;
import unikl.disco.network.Server.Multiplexing;

/**
 * 
 * Servers in a network object correspond to buffers that may be shared by
 * different flows, i.e., a network corresponds to the server graph
 * of a (physical) network.
 * 
 * That means the path of a flow is a sequence of buffers crossed by it
 * and the analysis operating on a network handles Server objects as such.
 * 
 * Please consider this when modeling a network for analysis.
 * E.g., assuming output buffering in a simple wireless sensor network
 * with one transceiver per node there is a 1:1-relation between network graph
 * connecting wireless sensor nodes and servers in the DiscoDNC network.
 * 
 * A flows path, however, is a sequence of crossed buffers. As a flow
 * usually does not reach the output buffer of its sink, the path should
 * not contain a link to it. Otherwise the flow interference pattern
 * of the network will be too pessimistic, yet, the results remain valid.   
 * 
 * In practice, on can model a sink explicitly by setting the server's
 * service curve to be an infinite burst after without delay
 * ( see ServiceCurve.createZeroDelayBurst() ). 
 * 
 * @author Steffen Bondorf
 *
 */
public class Network {
	private Set<Server> servers;
	private Set<Link> links;
	private Set<Flow> flows;
	
	private Map<Server,Set<Link>> map__server__in_links;
	private Map<Server,Set<Link>> map__server__out_links;
	
	private Map<Server,Set<Flow>> map__server__flows;
	private Map<Server,Set<Flow>> map__server__source_flows;
	
	private Map<Link,Set<Flow>> map__link__flows;
	
	private String server_default_name_prefix = "s";
	private int server_id_counter = 0;
	private Map<Integer,Server> map__id__server;

	private String link_default_name_prefix = "l";
	private int link_id_counter = 0;

	private String flow_default_name_prefix = "f";
	private int flow_id_counter = 0;
	
	public Network() {
		servers = new HashSet<Server>();
		links = new HashSet<Link>();
		flows = new HashSet<Flow>();
		
		map__id__server = new HashMap<Integer,Server>();
		
		map__server__in_links = new HashMap<Server,Set<Link>>();
		map__server__out_links = new HashMap<Server,Set<Link>>();

		map__server__flows = new HashMap<Server,Set<Flow>>();
		map__server__source_flows = new HashMap<Server,Set<Flow>>();
		
		map__link__flows = new HashMap<Link,Set<Flow>>();
	}

	private void remove( Set<Server> servers_to_remove, Set<Link> links_to_remove, Set<Flow> flows_to_remove ) {
		// Make sure that you do not remove a map's key before the according entries:
		// (flows before servers and links) & (links before servers)
				
		// prevent ConcurrentModificationException
		Set<Flow> flows_to_remove_cpy = new HashSet<Flow>( flows_to_remove );

		for ( Flow f : flows_to_remove_cpy ) {
			flows.remove( f );
			
			for ( Link l : f.getPath().getLinks() )
			{
				map__link__flows.get( l ).remove( f );
			}
			
			for ( Server s : f.getPath().getServers() )
			{
				map__server__flows.get( s ).remove( f );
			}

			map__server__source_flows.get( f.getSource() ).remove( f );
		}

		// prevent ConcurrentModificationException
		Set<Link> links_to_remove_cpy = new HashSet<Link>( links_to_remove );
		
		for ( Link l : links_to_remove_cpy ) {
			links.remove( l );
			
			map__link__flows.remove( l );
			map__server__in_links.get( l.getDest() ).remove( l );
			map__server__out_links.get( l.getSource() ).remove( l );
		}

		// prevent ConcurrentModificationException
		Set<Server> servers_to_remove_cpy = new HashSet<Server>( servers_to_remove );
		
		for ( Server s : servers_to_remove_cpy ) {
			servers.remove( s );
			
			map__id__server.remove( s.getId() );

			map__server__flows.remove( s );
			
			map__server__in_links.remove( s );
			map__server__out_links.remove( s );
			map__server__source_flows.remove( s );			
		}
	}

	// --------------------------------------------------------------------------------------------
	// Servers
	// --------------------------------------------------------------------------------------------
	// Without given maximum service curve
	public Server addServer( ServiceCurve service_curve ) {
		return addServer( service_curve, MaxServiceCurve.createZeroDelayInfiniteBurst(), Multiplexing.ARBITRARY, false, false );
	}
	
	public Server addServer( ServiceCurve service_curve, Multiplexing multiplexing ) {
		return addServer( service_curve, MaxServiceCurve.createZeroDelayInfiniteBurst(), multiplexing, true, true );
	}
	
	public Server addServer( String alias, ServiceCurve service_curve ) {
		return addServer( alias, service_curve, MaxServiceCurve.createZeroDelayInfiniteBurst(), Multiplexing.ARBITRARY, false, false );
	}
	
	public Server addServer( String alias, ServiceCurve service_curve, Multiplexing multiplexing ) {
		Server new_server = new Server( server_id_counter, alias, service_curve.copy(), multiplexing );
		updateServerAdditionInternally( new_server );
		
		return new_server;
	}
	
	// With given maximum service curve
	/**
	 * By default the server's use_gamma and use_extra_gamma are enabled
	 * 
	 * @param service_curve The server's service curve.
	 * @param max_service_curve The server's maximum service curve.
	 * @return The added server.
	 */
	public Server addServer( ServiceCurve service_curve, MaxServiceCurve max_service_curve ) {
		return addServer( service_curve, max_service_curve, Multiplexing.ARBITRARY, true, true );
	}
	
	/**
	 * By default the server's use_gamma and use_extra_gamma are enabled
	 * 
	 * @param alias The server's user-defined alias.
	 * @param service_curve The server's service curve.
	 * @param max_service_curve The server's maximum service curve.
	 * @return The added server.
	 */
	public Server addServer( String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve ) {
		return addServer( alias , service_curve, max_service_curve, Multiplexing.ARBITRARY, true, true );
	}

	public Server addServer( ServiceCurve service_curve, MaxServiceCurve max_service_curve, boolean use_gamma, boolean use_extra_gamma ) {
		return addServer( service_curve, max_service_curve, Multiplexing.ARBITRARY, use_gamma, use_extra_gamma );
	}
	
	public Server addServer( String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing ) {
		return addServer( alias , service_curve, max_service_curve, multiplexing, true, true );		
	}
	
	public Server addServer( ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing ) {
		return addServer( service_curve, max_service_curve, multiplexing, true, true );
	}
	
	public Server addServer( String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve, boolean use_gamma, boolean use_extra_gamma ) {
		return addServer( alias, service_curve, max_service_curve, Multiplexing.ARBITRARY, use_gamma, use_extra_gamma );
	}
	
	public Server addServer( ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing, boolean use_gamma, boolean use_extra_gamma ) {
		String alias = server_default_name_prefix + Integer.toString( server_id_counter );
		return addServer( alias, service_curve, max_service_curve, multiplexing, use_gamma, use_extra_gamma );
	}
	
	public Server addServer( String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve, Multiplexing multiplexing, boolean use_gamma, boolean use_extra_gamma ) {
		Server new_server = new Server( server_id_counter, alias, service_curve.copy(), max_service_curve.copy(), multiplexing, use_gamma, use_extra_gamma );
		updateServerAdditionInternally( new_server );
		
		return new_server;
	}
	
	private void updateServerAdditionInternally( Server new_server ) {
		map__server__in_links.put( new_server, new HashSet<Link>() );
		map__server__out_links.put( new_server, new HashSet<Link>() );
		
		map__server__flows.put( new_server, new HashSet<Flow>() );
		map__server__source_flows.put( new_server, new HashSet<Flow>() );
		
		servers.add( new_server );
		
		Integer integer_object = new Integer( server_id_counter );
		map__id__server.put( integer_object, new_server );
		
		server_id_counter++;
	}
	
	public void removeServer( Server s ) throws Exception {
		if ( !servers.contains( s ) ) {
			throw new Exception( "Server to be removed is not in this network's list of servers" );
		}
		
		remove( Collections.singleton( s ), getIncidentLinks( s ), map__server__flows.get( s ) );
	}
	
	public Set<Flow> getSourceFlows( Server source ) {
		return new HashSet<Flow>( map__server__source_flows.get( source ) );
	}
	
	public Server getServer( int id ) throws Exception {
		if ( id < 0 || id > map__id__server.size() - 1 ) {
			throw new Exception( "No server with id " + Integer.toString(id) + " found" );
		}

		Server server = map__id__server.get( new Integer( id ) );
		
		if ( server == null ) {
			throw new Exception( "No server with id " + Integer.toString(id) + " found" );
		}
		
		return server;
	}
	
	public Set<Server> getServers() {
		return new HashSet<Server>( servers );
	}
	
	public int numServers() {
		return servers.size();
	}
	
	/**
	 * Returns the subsets of all nodes of the directed graph <code>g</code> that
	 * are neither sources nor sinks, i.e. who have both (a) predecessor(s) and
	 * (a) successor(s) (in-degree &gt; 0 and out-degree &gt; 0).
	 * 
	 * @return the set of server nodes
	 */
	public Set<Server> getInOutServers() {
		Set<Server> in_out_servers = new HashSet<Server>();
		for( Server s : servers ) {
			if ( inDegree( s ) > 0 && outDegree( s ) > 0 ) {
				in_out_servers.add( s );
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
		for( Server s : servers ) {
			if ( inDegree( s ) == 0) {
				sources.add( s );
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
		for( Server s : servers ) {
			if ( outDegree( s ) == 0) {
				sinks.add( s );
			}
		}
		return sinks;
	}

	public int degree( Server s ) {
		return inDegree( s ) + outDegree( s );
	}

	public int inDegree( Server s ) {
		return getInLinks( s ).size();
	}

	public int outDegree( Server s ) {
		return getOutLinks( s ).size();
	}

	/**
	 * Returns a new set consisting of references to the servers.
	 * 
	 * @param s The server whose inlinks are returned.
	 * @return The incoming links of s.
	 */
	public Set<Link> getInLinks( Server s ) {
		Set<Link> outLinks = map__server__in_links.get( s );
		if ( outLinks == null ) {
			return new HashSet<Link>(); 
		} else {
			return new HashSet<Link>( map__server__in_links.get( s ) );
		}
	}
	
	/**
	 * Returns a new set consisting of references to the servers.
	 * 
	 * @param s The server whose outlinks are returned.
	 * @return The outgoing links of s.
	 */
	public Set<Link> getOutLinks( Server s ) {
		Set<Link> outLinks = map__server__out_links.get( s );
		if ( outLinks == null ) {
			return new HashSet<Link>(); 
		} else {
			return new HashSet<Link>( map__server__out_links.get( s ) );
		}
	}
	
	/**
	 * Returns a new set consisting of references to the links.
	 * 
	 * @param s The server whose inlinks and outlinks are returned.
	 * @return The incident links.
	 */
	public Set<Link> getIncidentLinks( Server s ) {
		return SetUtils.getUnion( getInLinks( s ), getOutLinks( s ) );
	}
	
	/**
	 * Returns a new set consisting of references to the servers.
	 * 
	 * @param s The server whose inlink sources and outlink destinations are returned.
	 * @return The neighboring servers of s.
	 */
	public Set<Server> getNeighbors( Server s ) {
		return SetUtils.getUnion( getSuccessors( s ), getPredecessors( s ) );
	}
	
	/**
	 * Returns a new set consisting of references to the servers.
	 * 
	 * @param s The server whose inlink sources are returned.
	 * @return The source servers of incoming links of s
	 */
	public Set<Server> getPredecessors( Server s ) {		
		Set<Server> predecessors = new HashSet<Server>();
		for( Link l : getInLinks( s ) ) {
			predecessors.add( l.getSource() );
		}
		return predecessors;
	}
	
	/**
	 * Returns a new set consisting of references to the servers.
	 * 
	 * @param s The server whose outlink destinations are returned.
	 * @return The sink servers of outgoing links of s.
	 */
	public Set<Server> getSuccessors( Server s ) {
		Set<Server> successors = new HashSet<Server>();
		for( Link l : getOutLinks( s ) ) {
			successors.add( l.getDest() );
		}
		return successors;
	}

	// --------------------------------------------------------------------------------------------
	// Links
	// --------------------------------------------------------------------------------------------
	public Link addLink( Server source, Server destination ) throws Exception {
		String alias = link_default_name_prefix + Integer.toString( link_id_counter );
		return addLink( alias, source, destination ); 
	}
	
	public Link addLink( String alias, Server source, Server destination ) throws Exception {
		if( !servers.contains( source ) ){
			throw new Exception( "link's source not present in the network" );
		}
		if ( !servers.contains( destination ) ) {
			throw new Exception( "link's destination not present in the network" );
		}
		
		try {
			// This implicitly signals the caller that the link was already present in the network 
			// by returning a link with a name different to the given one
			Link link = findLink( source, destination );
			return link;
		} catch (Exception e) {
			Link new_link = new Link( link_id_counter, alias, source, destination );
			link_id_counter++;
			
			map__link__flows.put( new_link, new HashSet<Flow>() );
			
			map__server__in_links.get( destination ).add( new_link );
			map__server__out_links.get( source ).add( new_link );
			
			links.add( new_link );
			return new_link;
		}
	}
	
	public void removeLink( Link l ) throws Exception {
		if( !links.contains( l ) ){
			throw new Exception( "Link to be removed is not in this network's list of links" );
		}		

		remove( new HashSet<Server>(), Collections.singleton( l ), map__link__flows.get( l ) );
	}

	public Set<Link> getLinks() {
		return new HashSet<Link>( links );
	}
	
	public int numLinks() {
		return links.size();
	}

	/**
	 * 
	 * @param src The link's source.
	 * @param dest The link's destination.
	 * @return The link from src to dest.
	 * @throws Exception No link from src to snk found in this network.
	 */
	public Link findLink( Server src, Server dest ) throws Exception {
		Set<Link> connecting_link_as_set = SetUtils.getIntersection( getInLinks( dest ), getOutLinks( src ) );
		if ( connecting_link_as_set.isEmpty() ) {
			throw new Exception ( "No link between " + src.toString() + " and " + dest.toString() + " found." );
		}else{
			if ( connecting_link_as_set.size() > 1 ) {
				throw new Exception ( "Too many links between " + src.toString() + " and " + dest.toString() + " found." );
			} else {
				return connecting_link_as_set.iterator().next();
			}
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	// Flows
	//
	// INFO: It is not allowed to change the path of a flow,
	// 		 i.e., the user needs to remove the old flow and insert a new one with the modified path
	// ---------------------------------------------------------------------------------------------
	@SuppressWarnings("rawtypes")
	public Flow addFlow( ArrivalCurve arrival_curve, List path ) throws Exception {
		String alias = flow_default_name_prefix + Integer.toString( flow_id_counter );
		return addFlow( alias, arrival_curve, path );
	}

	protected Flow addFlow( ArrivalCurve arrival_curve, Path path ) throws Exception {
		String alias = flow_default_name_prefix + Integer.toString( flow_id_counter );
		return addFlowToNetwork( alias, arrival_curve, path );
	}
	
	/**
	 * Creates a flow and adds it to the network.
	 * 
	 * @param arrival_curve The flow's arrival curve.
	 * @param source Source for shortest path routing.
	 * @param sink Sink for shortest path routing.
	 * @return The flow created and added to the network.
	 * @throws Exception Could not create a path from the given source/sink-pair.
	 */
	public Flow addFlow( ArrivalCurve arrival_curve, Server source, Server sink ) throws Exception {
		String alias = flow_default_name_prefix + Integer.toString( flow_id_counter );
		return addFlow( alias, arrival_curve, source, sink );
	}
	
	/**
	 * Creates a flow and adds it to the network.
	 * 
	 * @param alias The flow's user defined-alias.
	 * @param arrival_curve The flow's arrival curve.
	 * @param source Source for shortest path routing.
	 * @param sink Sink for shortest path routing.
	 * @return The flow created and added to the network.
	 * @throws Exception Could not create a path from the given source/sink-pair.
	 */
	public Flow addFlow( String alias, ArrivalCurve arrival_curve, Server source, Server sink ) throws Exception {
		if( source == sink ) {
			return addFlow( arrival_curve, sink );
		}
		
		return addFlowToNetwork( alias, arrival_curve, getShortestPath( source, sink ) );
	}

	/**
	 * Creates a flow and adds it to the network.
	 * 
	 * @param alias The flow's user defined-alias.
	 * @param arrival_curve The flow's arrival curve.
	 * @param path The flows path as a list of servers or links.
	 * @return The flow created and added to the network.
	 * @throws Exception The given path is not entirely in the network.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Flow addFlow( String alias, ArrivalCurve arrival_curve, List path ) throws Exception {
		if( path == null || path.isEmpty() ) {
			throw new Exception( "The path of a flow cannot be empty" );
		}
		
		Object element_of_path = path.get( 0 );

		if( element_of_path instanceof Server ) {
			return addFlowToNetwork( alias, arrival_curve, createPathFromServers( path ) );
		}
		if( element_of_path instanceof Link ) {
			return addFlowToNetwork( alias, arrival_curve, createPathFromLinks( path ) );
		}
		
		throw new Exception( "Could not create the path for flow " + alias );
	}

	/**
	 * Creates a flow and adds it to the network.
	 * 
	 * @param arrival_curve The flow's arrival curve.
	 * @param single_hop The single hop the flow takes in the network.
	 * @return The flow created and added to the network.
	 * @throws Exception The given server is not in the network.
	 */
	public Flow addFlow( ArrivalCurve arrival_curve, Server single_hop ) throws Exception {		
		String alias = flow_default_name_prefix + Integer.toString( flow_id_counter );
		return addFlow( alias, arrival_curve, single_hop );
	}
	
	/**
	 * Creates a flow and adds it to the network.
	 * 
	 * @param alias The flow's user defined-alias.
	 * @param arrival_curve The flow's arrival curve.
	 * @param single_hop The single hop the flow takes in the network.
	 * @return The flow created and added to the network.
	 * @throws Exception The given server is not in the network.
	 */
	public Flow addFlow( String alias, ArrivalCurve arrival_curve, Server single_hop ) throws Exception {
		return addFlowToNetwork( alias, arrival_curve, createPath( single_hop ) );
	}
	
	// Needed to be named differently due to a collision of the method's signature with a user visible one's
	private Flow addFlowToNetwork( String alias, ArrivalCurve arrival_curve, Path path ) throws Exception {
		if ( !servers.containsAll( path.getServers() ) ) {
			throw new Exception( "Some servers on the given flow's path are not present in the network" );
		}
		if ( !path.getLinks().isEmpty() ) {
			if ( !links.containsAll( path.getLinks() ) ) {
				throw new Exception( "Some links on the given flow's path are not present in the network" );
			}
		}

		Flow new_flow = new Flow( flow_id_counter, alias, arrival_curve.copy(), path );
		flow_id_counter++;
		
		flows.add( new_flow );
		map__server__source_flows.get( path.getSource() ).add( new_flow );
		
		for ( Link l : path.getLinks() )
		{
			map__link__flows.get( l ).add( new_flow );
		}
		for ( Server s : path.getServers() )
		{
			map__server__flows.get( s ).add( new_flow );
		}
		
		return new_flow;
	}
	
	/**
	 * Removes a flow from the network.
	 * 
	 * @param f The flow to be removed.
	 * @throws Exception Flow to be removed is not in this network's list of flows.
	 */
	public void removeFlow( Flow f ) throws Exception {
		if ( !flows.contains( f ) ) {
			throw new Exception( "Flow to be removed is not in this network's list of flows" );
		}
		
		remove( new HashSet<Server>(), new HashSet<Link>(), Collections.singleton( f ) );
	}
	
	public Set<Flow> getFlows() {
		return new HashSet<Flow>( flows );
	}
	
	public int numFlows() {
		return flows.size();
	}
	
	public Set<Flow> getFlows( Link l ) {
		if( l == null ) {
			return new HashSet<Flow>();
		}
		
		Set<Flow> flows = map__link__flows.get( l );
		if( flows != null ) {
			return new HashSet<Flow>( map__link__flows.get( l ) );
		} else {
			return new HashSet<Flow>();
		}
	}

	public Set<Flow> getFlows( Set<Link> links ) {
		HashSet<Flow> flows = new HashSet<Flow>();
		
		for ( Link l : links ) {
			flows.addAll( map__link__flows.get( l ) );
		}
		
		return flows;
	}
	
	public Set<Flow> getFlows( Server s ) {
		if( s == null ) {
			return new HashSet<Flow>();
		}
		
		Set<Flow> flows = map__server__flows.get( s );
		if( flows != null ) {
			return new HashSet<Flow>( map__server__flows.get( s ) );
		} else {
			return new HashSet<Flow>();
		}
	}

	public Set<Flow> getFlows( Path p ) throws Exception {
		Set<Flow> result = new HashSet<Flow>();
		for ( Set<Flow> flows : getFlowsPerServer( p, new HashSet<Flow>() ).values() ) {
			result.addAll( flows );
		}
		return result;
	}

	/**
	 * Do not confuse with getServerJoiningFlowsMap
	 * 
	 * @param p The path to split up into its hops and analyze for flows.
	 * @param excluded_flows Flows to omit in the sorting operation.
	 * @return Map from a server to the flows present at this servers.
	 * @throws Exception Could not sort although there may be flows.
	 */
	public Map<Server,Set<Flow>> getFlowsPerServer( Path p, Set<Flow> excluded_flows ) throws Exception {
		Map<Server,Set<Flow>> map__server__set_flows = new HashMap<Server,Set<Flow>>();
		
		Set<Flow> set_set_flows;
		for ( Server s : p.getServers() ) {
			set_set_flows = getFlows( s ); // No need to create another new instance of HashMap
			set_set_flows.removeAll( excluded_flows );
			map__server__set_flows.put( s, set_set_flows );
		}
		return map__server__set_flows;
	}
	
	/**
	 * For every distinct sub-path of p
	 * this method returns the flows taking it entirely.
	 * 
	 * @param p The path to split up and analyze for flows.
	 * @return Map from a path to the flows taking exactly this tandem of servers.
	 * @throws Exception Could not sort although there may be flows.
	 */
	public Map<Path,Set<Flow>> getFlowsPerSubPath( Path p ) throws Exception {
		Map<Path,Set<Flow>> map__path__set_flows = new HashMap<Path,Set<Flow>>();
				
		Map<Server,Set<Flow>> map__s_i__joining_flows = getServerJoiningFlowsMap( p );
		Set<Flow> all_interfering_flows = new HashSet<Flow>();
		for( Set<Flow> flows_on_path : map__s_i__joining_flows.values() ) {
			all_interfering_flows.addAll( flows_on_path );
		}
		if ( all_interfering_flows.isEmpty() ) { // If there are no interfering flows to be considered, return the convolution of a ll service curves on the path
			return map__path__set_flows;
		}
		
		// Iterate over the servers s on the path. Use indices to easily determine egress servers 
		LinkedList<Server> servers = p.getServers();
		int n = servers.size();
		
		// Flows with the same egress server (independent of the out link) can be aggregated for PMOO's and OBA's arrival bound calculation.
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
	 			
	 			if ( s_i_ingress__s_j_egress.isEmpty() ) { // No such flows to bound
	 				continue;
	 			}

		 		map__path__set_flows.put( p.getSubPath(s_i, s_j_egress), s_i_ingress__s_j_egress );
				
				// Remove the flows otherwise rejoining flows will occur multiple times with wrong paths
				s_i_ingress.removeAll( s_i_ingress__s_j_egress );
			}
		}
		return map__path__set_flows;
	}
	
	public Map<Path,Set<Flow>> groupFlowsPerSubPath( Path p, Set<Flow> flows_to_group ) throws Exception {
		Map<Path,Set<Flow>> map__path__set_flows = new HashMap<Path,Set<Flow>>();
				
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
		
		// Flows with the same egress server (independent of the out link) can be aggregated for PMOO's and OBA's arrival bound calculation.
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

		 		map__path__set_flows.put( p.getSubPath(s_i, s_j_egress), s_i_ingress__s_j_egress );
				
				// Remove the flows otherwise rejoining flows will occur multiple times with wrong paths
				s_i_ingress.removeAll( s_i_ingress__s_j_egress );
			}
		}
		return map__path__set_flows;
	}
	
	/**
	 * Returns an aggregate arrival curve for all flows originating in <code>source</code>.
	 * 
	 * Returns a null curve if there are no source flows at <code>source</code>.
	 * 
	 * @param source The source of all flows to be aggregated.
	 * @return An aggregate arrival curve.
	 */
	public ArrivalCurve getSourceFlowArrivalCurve( Server source )
	{
		return getSourceFlowArrivalCurve( source, getSourceFlows( source ) );
	}
	
	/**
	 * Returns an aggregate arrival curve for all flows originating in
	 * <code>source</code> and contained in the set <code>outgoing_flows</code>.
	 * 
	 * Returns a null curve if the intersection of those sets of curves is empty.
	 * 
	 * @param source The source of all flows to be aggregated.
	 * @param source_flows The set of all flows to be aggregated.
	 * @return An aggregate arrival curve.
	 */
	public ArrivalCurve getSourceFlowArrivalCurve( Server source, Set<Flow> source_flows )
	{
		ArrivalCurve a_out = ArrivalCurve.createNullArrival();
		
		// Returns an empty set if one of the arguments is null
		Set<Flow> source_flows_internal = SetUtils.getIntersection( map__server__source_flows.get( source ), source_flows );
		if ( source_flows_internal.isEmpty() ) {
			return a_out;
		} else {
			if ( source_flows_internal != null ) {
				for ( Flow f : source_flows_internal ) 
				{
					a_out = ArrivalCurve.add( a_out, f.getArrivalCurve() );
				}
			}			
		}
		return a_out;
	}

	// --------------------------------------------------------------------------------------------
	// Paths
	// --------------------------------------------------------------------------------------------
	public Path createPath( Server server ) throws Exception {
		if ( !servers.contains( server ) ) {
			throw new Exception( "Server to create path from is not in the network");
		}
		
		return new Path( new LinkedList<Server>( Collections.singleton( server ) ), new LinkedList<Link>() );
	}
	
	public Path createPath( Link link ) throws Exception {
		if ( !links.contains( link ) ) { // Implicitly contains the case that at least one server the link connects is not in the network
			throw new Exception( "Link to create path from is not in the network" );
		}
		
		LinkedList<Server> path_servers = new LinkedList<Server>();
		path_servers.add( link.getSource() );
		path_servers.add( link.getDest() );
		
		return new Path( path_servers, new LinkedList<Link>( Collections.singleton( link ) ) );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Path createPath( List path ) throws Exception {
		if( path == null || path.isEmpty() ) {
			throw new Exception( "The path of a flow cannot be empty" );
		}
		
		Object element_of_path = path.get( 0 );

		if( element_of_path instanceof Server ) {
			return createPathFromServers( path );
		}
		if( element_of_path instanceof Link ) {
			return createPathFromLinks( path );
		}
		
		throw new Exception( "Could not create path" );
	}
	
	private Path createPathFromServers( List<Server> path_servers ) throws Exception {
		List<Link> path_links = new LinkedList<Link>();
		
		// Sanity check + path_links construction:
		if( path_servers.size() > 1 ) {
			for ( int i = 0; i < path_servers.size()-1; i++ ) {
				try{
					path_links.add( findLink( path_servers.get( i ), path_servers.get( i+1 ) ) );
				} catch (Exception e) {
					throw new Exception( "At least two consecutive servers to create the path from are not connected by a link in the network" );
				}
			}			
		}
		
		return createPath( path_servers, path_links );
	}
	
	private Path createPathFromLinks( List<Link> path_links ) throws Exception {
		LinkedList<Server> path_servers = new LinkedList<Server>();
		
		// Sanity checks + path_servers construction:
		for( int i = 0; i < path_links.size()-1; i++ ) {
			Link l_i = path_links.get( i );
			if ( !links.contains( l_i ) ) {
				throw new Exception( "At least one link to create path from is not in the network" );
			}
			if ( l_i.getDest() != path_links.get( i+1 ).getSource() ) {
				throw new Exception( "At least two consecutive links to create the path from are not connected" );
			}
			path_servers.add( l_i.getSource() );
		}
		Link l_last = path_links.get( path_links.size()-1 );
		if ( !links.contains( l_last ) ) {
			throw new Exception( "Last link to create path from is not in the network" );
		}
		path_servers.add( l_last.getSource() );
		path_servers.add( l_last.getDest() );
		
		return new Path( path_servers, path_links );
	}
	
	public Path createPath( List<Server> path_servers, List<Link> path_links ) throws Exception {
		// Sanity checks:
		if( path_servers.isEmpty() ) {
			throw new Exception( "A path without servers cannot be created." );
		}
		if( !path_links.isEmpty() ) {
			for( int i = 0; i < path_links.size()-1; i++ ) {
				Link l_i = path_links.get( i );
				
				if ( !links.contains( l_i ) ) { // Implicitly contains the case that at least one server the link connects is not in the network
					throw new Exception( "At least one link to create path from is not in the network" );
				}
				if ( l_i.getDest() != path_links.get( i+1 ).getSource() ) {
					throw new Exception( "At least two consecutive links to create the path from are not connected" );
				}
				
				if ( path_servers.get( i ) != l_i.getSource() || path_servers.get( i+1 ) != l_i.getDest() ) {
					throw new Exception( "At least two consecutive servers to create the path from are not connected by the corresponding link of the given list" );
				}
			}
			Link l_last = path_links.get( path_links.size()-1 );
			if ( !links.contains( l_last ) ) { // Implicitly contains the case that at least one server the link connects is not in the network
				throw new Exception( "Last link to create path from is not in the network" );
			}
		}
		
		return new Path( path_servers, path_links );
	}
	
	/**
	 * 
	 * @param path The tandem of servers to check for flows.
	 * @param flows_to_join Flows not in the return set<br> null will be handled as empty set of flows.
	 * @return The joining flows.
	 * @throws Exception Could not derive the joining flows even if there are.
	 */
	public Map<Server,Set<Flow>> getServerJoiningFlowsMap( Path path ) throws Exception {
		Map<Server,Set<Flow>> map__server__joining_flows = new HashMap<Server,Set<Flow>>();
		
		Server path_source = path.getSource();
		LinkedList<Server> servers_iteration = path.getServers();
		servers_iteration.remove( path_source );
		
		// Default for first server
		Set<Flow> flows_joining = getFlows( path_source );
		map__server__joining_flows.put( path_source, flows_joining );
		
		for ( Server s : servers_iteration ) {
			flows_joining = SetUtils.getDifference( getFlows( s ), getFlows( path.getPrecedingLink( s ) ) );
			
			// Results in an empty set if there a no joining flow at server s
			map__server__joining_flows.put( s, flows_joining );
		}

		return map__server__joining_flows;
	}
	
	// All flows that leave the path at the server
	public Map<Server,Set<Flow>> getServerLeavingFlowsMap( Path path ) throws Exception {
		Map<Server,Set<Flow>> map__server__leaving_flows = new HashMap<Server,Set<Flow>>();
		
		Server path_sink = path.getSink();
		LinkedList<Server> servers_iteration = path.getServers();
		servers_iteration.remove( path_sink );
		
		for ( Server s : servers_iteration ) {
			// Results in an empty set if there a no joining flow at server s
			map__server__leaving_flows.put( s, SetUtils.getDifference( getFlows( s ), getFlows( path.getSucceedingLink( s ) ) ) );
		}
		
		// Default for last server
		map__server__leaving_flows.put( path_sink, getFlows( path_sink ) );
		
		return map__server__leaving_flows;
	}
 
	/**
	 * Calculates the shortest path between src and snk
	 * according to Dijkstra's algorithm
	 * 
	 * @param src The path's source.
	 * @param snk The path's sink.
	 * @return Dijkstra shortest path from src to snk.
	 * @throws Exception Could not find a shortest path for some reason.
	 */
    public Path getShortestPath( Server src, Server snk ) throws Exception {
		Set<Server> visited = new HashSet<Server>();

		Map<Server,LinkedList<Link>> paths_links = new HashMap<Server,LinkedList<Link>>();
		Map<Server,LinkedList<Server>> paths_servers = new HashMap<Server,LinkedList<Server>>();

		paths_links.put( src, new LinkedList<Link>() );
		paths_servers.put( src, new LinkedList<Server>( Collections.singleton( src ) ) );
		
		LinkedList<Server> queue = new LinkedList<Server>();
		queue.add( src );
		visited.add( src );
		
		while ( !queue.isEmpty() ) {
			Server s = queue.getLast();
			queue.remove( s );
			
			Set<Server> successors_s = getSuccessors( s ); 
			for( Server successor : successors_s ) {

				LinkedList<Link> path_links_tmp = new LinkedList<Link>( paths_links.get( s ) );
				
				LinkedList<Server> path_servers_tmp;
				if ( paths_servers.containsKey( s ) ) {
					path_servers_tmp = new LinkedList<Server>( paths_servers.get( s ) );
				} else {
					path_servers_tmp = new LinkedList<Server>( Collections.singleton( src ) );
				}

				path_links_tmp.add( findLink( s, successor ) );
				path_servers_tmp.add( successor );
				
				if ( !visited.contains( successor ) ) {
					paths_links.put( successor, path_links_tmp );
					paths_servers.put( successor, path_servers_tmp );
					
					queue.add( successor );
					visited.add( successor );
				} else {
					if( paths_links.get( successor ).size() > path_links_tmp.size() ) {
						paths_links.put( successor, path_links_tmp );
						paths_servers.put( successor, path_servers_tmp );
						
						queue.add( successor );
					}
				}				
			}
		}
		
		if ( paths_links.get( snk ) == null ) {
			throw new Exception( "No path from server " + src.getId() + " to server " + snk.getId() + " found" );
		}
		
		// No sanity checks needed after a shortest path calculation, so you can create a new path directly instead of calling 'createPath'
		return new Path( paths_servers.get( snk ), paths_links.get( snk ) );
	}
 	
 	/**
 	 * Returns the server at which the flows in <code>flows_of_interest</code>
 	 * first all meet each other (when viewed from the source). When viewed from
 	 * <code>server</code> towards the sink, this is the last server where all
 	 * flows are still together.
 	 * 
 	 * @param server_common_dest The common destination.
 	 * @param flows_of_interest The flow of interest.
 	 * @return the splitting point server The common source.
     * @throws Exception No splitting point prior to the common destination found.
 	 */
 	public Server findSplittingServer( Server server_common_dest, Set<Flow> flows_of_interest ) throws Exception {
 		Flow f = flows_of_interest.iterator().next();
 		
 		if ( flows_of_interest.size() == 1 ) {
 			return f.getSource();
 		}
 		
 		Path f_path = f.getPath();
 		int common_dest_index_f = f_path.getServers().indexOf( server_common_dest );
 		
 		Server split = server_common_dest;
 		// Iterate in reverse order starting from server_common_dest, stop as soon as at least one of the flows of interest is missing
 		for( int i = common_dest_index_f - 1; i >= 0; i-- ) { // -1 excludes server_common_dest
 			Server split_candidate = f_path.getServers().get( i );
 			
 			if ( getFlows( split_candidate ).containsAll( flows_of_interest ) ) {
 				split = split_candidate;
 			} else {
 				break;
 			}
 		}
 		
 		if ( split == server_common_dest ) { // No splitting point found
 	 		throw new Exception( "No splitting point prior to the common destination found" );
 		} else {
 			return split;
 		}
 	}

	// --------------------------------------------------------------------------------------------
	// Other helper functions
	// --------------------------------------------------------------------------------------------
    /**
     * 
     * Creates a deep copy of this network.
     * 
     * @return The copy.
     * @throws Exception Signals problems while instantiating the copy.
     */
 	public Network copy() throws Exception {
 		Network network_new = new Network();
 		
 		// Copy servers
		// We cannot use some addServer( s_old.copy() ) because servers can only be created via a network.
 		// They need an network determined id. (Hard design decision at the moment)
 		Map<Server,Server> map__s_old__s_new = new HashMap<Server,Server>();
 		Server s_new;
 		
 		for ( Server s_old : servers ) {
 			s_new = network_new.addServer( s_old.getAlias(),
 										   s_old.getServiceCurve().copy(),
 										   s_old.getMaxServiceCurve().copy(),
 										   s_old.multiplexingDiscipline(),
 										   s_old.useGamma(),
 										   s_old.useExtraGamma() );
 			map__s_old__s_new.put( s_old, s_new );
 		}

 		// Copy links
		// We cannot use some addLink( l_old.copy() ) because links can only be created via a network.
 		// They need an network determined id. (Hard design decision at the moment)
 		Map<Link,Link> map__l_old__l_new = new HashMap<Link,Link>();
 		Link l_new;
 		
 		for ( Link l_old : links ) {
 			l_new = network_new.addLink( l_old.getAlias(), 
 										 map__s_old__s_new.get( l_old.getSource() ),
 										 map__s_old__s_new.get( l_old.getDest() ) );
 			map__l_old__l_new.put( l_old, l_new );
 		}
 		
 		// Copy flows
		// We cannot use some addFlow( f_old.copy() ) because flows can only be created via a network.
 		// They need an network determined id. (Hard design decision at the moment)
 		Path f_old_path;
 		Path f_new_path;
 		
 		List<Server> f_path_old_s;
 		List<Server> f_path_new_s;
 		
 		List<Link> f_path_old_l;
 		List<Link> f_path_new_l;
 		
 		for ( Flow f_old : flows ) {
 			f_old_path = f_old.getPath();
 			
 			f_path_old_s = f_old_path.getServers();
 			f_path_new_s = new LinkedList<Server>();
 			for ( Server s : f_path_old_s ){
 				f_path_new_s.add( map__s_old__s_new.get( s ) );
 			}

 	 		f_path_old_l = f_old_path.getLinks();
 	 		f_path_new_l = new LinkedList<Link>();
 			for ( Link l : f_path_old_l ){
 				f_path_new_l.add( map__l_old__l_new.get( l ) );
 			}
 	 		
 	 		f_new_path = new Path( f_path_new_s, f_path_new_l );
 	 		network_new.addFlowToNetwork( f_old.getAlias(),
 	 									  f_old.getArrivalCurve().copy(),
 	 									  f_new_path );
 		}
 		
 		return network_new;
 	}
 	
 	public void saveAs( String output_path, String file_name ) throws Exception {
 		saveAs( output_path, file_name, "default" );
 	}
 	
 	public void saveAs( String output_path, String file_name, String package_name ) throws Exception {
 		if ( output_path.charAt( output_path.length() - 1 ) != '/' ) {
 			output_path = output_path + "/";
 		}
 		String file_extension = ".java";
 		if ( file_name.endsWith( file_extension ) ) {
 			file_name = file_name.substring( 0, file_name.length() - 5 );
 		}
 		
 		File file = new File( output_path + file_name + file_extension );
 		Writer w = new OutputStreamWriter( new FileOutputStream(file), "UTF-8" );
 		PrintWriter pw = new PrintWriter(w);
 		
 		pw.println( "/* " );
 		pw.println( " * This file is part of the Disco Deterministic Network Calculator v2.3 \"Centaur\"." );
 		pw.println( " *" );
 		pw.println( " * The Disco Deterministic Network Calculator (DiscoDNC) is free software;" );
 		pw.println( " * you can redistribute it and/or modify it under the terms of the " );
 		pw.println( " * GNU Lesser General Public License as published by the Free Software Foundation; " );
 		pw.println( " * either version 2.1 of the License, or (at your option) any later version." );
 		pw.println( " * " );
 		pw.println( " * This library is distributed in the hope that it will be useful," );
 		pw.println( " * but WITHOUT ANY WARRANTY; without even the implied warranty of" );
 		pw.println( " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU" );
 		pw.println( " * Lesser General Public License for more details." );
 		pw.println( " * " );
 		pw.println( " * You should have received a copy of the GNU Lesser General Public" );
 		pw.println( " * License along with this library; if not, write to the Free Software" );
 		pw.println( " * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA" );
 		pw.println( " * " );
 		pw.println( " */ " );
 		pw.println();
 		
 		pw.println( "package " + package_name + ";" );
 		pw.println();
 		pw.println( "import java.util.LinkedList;" );
 		pw.println();
 		pw.println( "import unikl.disco.curves.ServiceCurve;" );
 		pw.println( "import unikl.disco.curves.MaxServiceCurve;" );
 		pw.println( "import unikl.disco.curves.ArrivalCurve;" );
 		pw.println();
 		pw.println( "import unikl.disco.network.Network;" );
 		pw.println( "import unikl.disco.network.Server;" );
 		pw.println( "import unikl.disco.network.Server.Multiplexing;" );
 		pw.println();

 		pw.println( "public class " + file_name + "{");
 		pw.println( "\tpublic static Network network;" );
 		pw.println( "\tprivate static Server[] servers;" );
 		
 		// Server creation
 		int i_servers_func = 1;
 		int i_servers_lines = 0;
 		pw.println( "\tpublic static void createServers" + Integer.toString( i_servers_func ) + "() throws Exception {" );
 		for ( Server s : servers ) {
 			pw.print( "\t\tservers[" + s.getId() + "] = " );
 			pw.print( "network.addServer( " );
 			pw.print( "\"" + s.getAlias() + "\"" + ", " );
 			pw.print( "new ServiceCurve( \"" + s.getServiceCurve().toString() + "\" )" + ", " );
 			pw.print( "new MaxServiceCurve( \"" + s.getMaxServiceCurve().toString() + "\" )" + ", " );
 			pw.print( "Multiplexing." + s.multiplexingDiscipline() + ", " );
 			pw.print( s.useGamma() + ", " );
 			pw.print( s.useExtraGamma() );
 			pw.println( " );" );
 			
 			i_servers_lines++;
 			
 			if( (i_servers_lines / 500) >= i_servers_func ) {
 				i_servers_func++;

 				pw.println( "\t}" );
 		 		pw.println();
 		 		pw.println( "\tpublic static void createServers" + Integer.toString( i_servers_func ) + "() throws Exception {" );
 			}
 		}
 		pw.println( "\t}" );
 		pw.println();
 		
 		// Link creation
 		int i_links_func = 1;
 		int i_links_lines = 0;
 		pw.println( "\tpublic static void createLinks" + Integer.toString( i_links_func ) + "() throws Exception {" );
 		for ( Link l : links ) {
 			pw.print( "\t\tnetwork.addLink( " );
 			pw.print( "\"" + l.getAlias() + "\"" + ", " );
 			pw.print( "servers[" + l.getSource().getId() + "]" + ", " );
 			pw.print( "servers[" + l.getDest().getId() + "]" );
 			pw.println( " );" );
 			
 			i_links_lines++;
 			
 			if( (i_links_lines / 500) >= i_links_func ) {
 				i_links_func++;

 				pw.println( "\t}" );
 		 		pw.println();
 		 		pw.println( "\tpublic static void createLinks" + Integer.toString( i_links_func ) + "() throws Exception {" );
 			}
 		}
 		pw.println( "\t}" );
 		pw.println();
 		
 		// Flow creation
 		int i_flows_func = 1;
 		int i_flows_lines = 0;
 		pw.println( "\tpublic static void createFlows" + Integer.toString( i_flows_func ) + "() throws Exception {" );
 		pw.println( "\t\tLinkedList<Server> servers_on_path_s = new LinkedList<Server>();" );
 		i_flows_lines++;
 		pw.println();
 		for ( Flow f : flows ) {
 			for ( Server s : f.getServersOnPath() ) {
 				pw.print( "\t\tservers_on_path_s.add( " );
 				pw.print( "servers[" + s.getId() + "]" );
 				pw.println( " );" );
 				i_flows_lines++;
 			}
 			pw.print( "\t\tnetwork.addFlow( " );
 			pw.print( "\"" + f.getAlias() + "\"" + ", " );
 			pw.print( "new ArrivalCurve( \"" + f.getArrivalCurve().toString() + "\" )" + ", " );
 			pw.print( "servers_on_path_s" );
 			pw.println( " );" );
 			pw.println( "\t\tservers_on_path_s.clear();" );
 			pw.println();
 			
 			i_flows_lines += 3;
 			
 			if( (i_flows_lines / 500) >= i_flows_func ) {
 				i_flows_func++;

 				pw.println( "\t}" );
 		 		pw.println();
 		 		pw.println( "\tpublic static void createFlows" + Integer.toString( i_flows_func ) + "() throws Exception {" );
 		 		pw.println( "\t\tLinkedList<Server> servers_on_path_s = new LinkedList<Server>();" );
 		 		i_flows_lines++;
 			}
 		}
 		pw.println( "\t}" );
 		
 		pw.println();
 		pw.println( "\tpublic " + file_name + "() {" );
 		pw.println( "\t\ttry{" );
 		pw.println( "\t\t\tservers = new Server[" + numServers() + "];" );
 		pw.println( "\t\t\tnetwork = new Network();" );
 		
 		for( int i = 1; i <= i_servers_func; i++ ) {
 	 		pw.println( "\t\t\tcreateServers" + Integer.toString( i ) + "();" );
 		}
 		for( int i = 1; i <= i_links_func; i++ ) {
 			pw.println( "\t\t\tcreateLinks" + Integer.toString( i ) + "();" );
 		}
 		for( int i = 1; i <= i_flows_func; i++ ) {
 			pw.println( "\t\t\tcreateFlows" + Integer.toString( i ) + "();" );
 		}
 		
 		pw.println( "\t\t} catch (Exception e) {" );
 		pw.println( "\t\t\tSystem.out.println( e.toString() );" );
 		pw.println( "\t\t}" );
 		pw.println( "\t}" );
 		pw.println();
 		
 		pw.println( "}" );
 		pw.println();
 		
 		pw.close();
 	}
 	
	@Override
	/**
	 * 
	 * Print the network in pseudo code creating it.
	 * 
	 */
	public String toString() {
		if( numServers() == 0) {
			return "empty network.";
		}
		
		StringBuffer network_str = new StringBuffer();
		for ( Server s : servers ) {
			network_str.append( s.toString() );
			network_str.append( "\n" );
		}

		network_str.append( "\n" );
		for ( Link l : links ) {
			network_str.append( l.toString() );
			network_str.append( "\n" );
		}

		network_str.append( "\n" );
		for ( Flow f : flows ) {
			network_str.append( f.toString() );
			network_str.append( "\n" );
		}
		
		network_str.deleteCharAt( network_str.length()-1 ); // Remove the line break at the end.
		
		return network_str.toString();
	}
}
