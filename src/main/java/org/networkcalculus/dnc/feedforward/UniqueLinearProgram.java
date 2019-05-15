/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2015 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.feedforward;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.linear_constraints.FlowLocationTime;
import org.networkcalculus.dnc.linear_constraints.LogicalConstraint;
import org.networkcalculus.dnc.linear_constraints.NumericalConstraint;
import org.networkcalculus.dnc.linear_constraints.NumericalTerm;
import org.networkcalculus.dnc.linear_constraints.Operator;
import org.networkcalculus.dnc.linear_constraints.Relation;
import org.networkcalculus.dnc.linear_constraints.TemporalConstraint;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.utils.SetUtils;
import org.networkcalculus.num.Num;

/**
 * This code is a very direct, step-by-step implementation the the unique linear program (ULP),
 * an optimization formulation derived from the network calculus server graph, first presented in
 * 
 * 		Tight Performance Bounds in the Worst-case Analysis of Feed-forward Networks.
 * 		Anne Bouillard, Laurent Jouhet, and Eric Thierry,
 * 		In Proceedings of the IEEE Conference on Computer Communications (INFOCOM), 2010.
 * 
 * and refined in (can be enabled by the flag <code>flow_constr_hdr</code>)
 * 	
 * 		Algorithms and Efficiency of Network Calculus.
 * 		Anne Bouillard.
 * 		Habilitation thesis, École Normale Supérieure, 2014.
 * 
 * This code will create an internal representation of the optimization formulation.
 * It can then be written to the file system, either in the LpSolve format or the
 * CPLEX format. Performance of both optimization tools can be found in
 * 
 * 		Quality and Cost of Deterministic Network Calculus – Design and Evaluation of an Accurate and Fast Analysis.
 * 		Steffen Bondorf, Paul Nikolaus, and Jens B. Schmitt,
 * 		In Proceedings of the ACM SIGMETRICS International Conference on Measurement and Modeling of Computer Systems (SIGMETRICS), 2017.
 * 
 * Moreover, the following publication provides insights into the reproducibility of
 * CPLEX results can be found in:
 * 
 * 		The Deterministic Network Calculus Analysis: Reliability Insights and Performance Improvements.
 *  	Alexander Scheffler, Markus Fögen, Steffen Bondorf,
 *   	In Proceedings of the IEEE International Workshop on Computer Aided Modeling and Design of Communication Links and Networks (CAMAD), 2018.
 *  
 *  (TODO investigate effect of CPLEX parameter settings for integrality tolerance, optimality tolerance, etc.)
 */
public class UniqueLinearProgram {
	boolean debug = false;
	boolean print_debug = false;
	
	static String lp_file_output_path = System.getProperty("user.dir") + 
										"/src/main/java/org/networkcalculus/dnc/feedforward/ulp_output/";
	
	HashMap<Path,Integer> map__path__id;
	
	HashMap<Server,HashSet<Path>> map__j__jpi;
	HashMap<Server,HashSet<Pair<Path,Path>>> map__j__pair_jpi_pi;
	HashMap<Flow,HashSet<Path>> pi_flow;
	HashMap<Pair<Flow,Server>,HashSet<Path>> pi_flow_server;

	HashSet<TemporalConstraint> temp_constraints; // Used for optional non-decreasing constraints at source and for the LP output.
	HashSet<TemporalConstraint> temp_constraints_transClosed;
	HashMap<Path,HashSet<TemporalConstraint>> map__path__temp_constraints_transClosed;

	HashSet<NumericalConstraint> s2c_constraints;
	HashSet<LogicalConstraint> startBP_constraints;
	
	boolean flow_constr_hdr = true; 	// false enables the constraints given in the 2010 INFOCOM paper.
										// true enables the constraints given in the 2014 habilitation thesis.
	HashSet<LogicalConstraint> flow_constraints;
	HashSet<LogicalConstraint> flow_constraints_hdr;
	
	HashSet<LogicalConstraint> nonDecreasing_constraints;
	HashSet<LogicalConstraint> nonDecreasingAtSource_constraints;
	HashSet<NumericalConstraint> arrival_constraints;
	
	
	ServerGraph server_graph;
	Flow flow_of_interest;
	Server sink_of_interest;
	
	Server explicit_src;
	Path empty_path;
	
	@SuppressWarnings("unused")
	private UniqueLinearProgram() {}
	
	public UniqueLinearProgram( ServerGraph network, Flow flow_of_interest ) throws Exception {
		this.server_graph = network;
		this.flow_of_interest = flow_of_interest;
		sink_of_interest = flow_of_interest.getSink();
		explicit_src = Server.createExplicitSourceServer();
		empty_path = Path.createEmptyPath(); // $t_{\emptyset}$
		
		map__j__jpi = new HashMap<Server,HashSet<Path>>();
		map__j__pair_jpi_pi = new HashMap<Server,HashSet<Pair<Path,Path>>>();
		
		temp_constraints = new HashSet<TemporalConstraint>();
		temp_constraints_transClosed = new HashSet<TemporalConstraint>();
		map__path__temp_constraints_transClosed = new HashMap<Path,HashSet<TemporalConstraint>>(); // Used in the search the relation between two pis.
		map__path__temp_constraints_transClosed.put( empty_path, new HashSet<TemporalConstraint>() );

		Writer w = null;
		PrintWriter pw = null;
		if( debug && print_debug ) {
			File file = new File( lp_file_output_path + "LP_debug.txt" );
	 		w = new OutputStreamWriter( new FileOutputStream(file), "UTF-8" );
	 		pw = new PrintWriter( w );
		}

		derivePi( flow_of_interest, sink_of_interest, Path.createEmptyPath() );
		
		if( debug ) {
			System.out.println();
			
			System.out.println( "Map: Server -> Paths\n" + map__j__jpi.toString() );
			System.out.println();
			
			System.out.println( "Map: Server -> Paths with j, without j\n" + map__j__pair_jpi_pi.toString() );
			System.out.println();

			System.out.println( "Temporal constraints (\"neighbors\" only)\n" + temp_constraints );
			System.out.println();
			
			System.out.println( "Temporal constraints (transitive closure)\n" + temp_constraints_transClosed );
			System.out.println();
			
			System.out.println( "Map: Path -> Constraints (transitive closure)\n" + map__path__temp_constraints_transClosed );
			System.out.println();
			
			if( print_debug ) {
				pw.println();
				
				pw.println( "Map: Server -> Paths\n" + map__j__jpi.toString() );
				pw.println();
				pw.println( "Map: Server -> Paths with j, without j\n" + map__j__pair_jpi_pi.toString() );
				pw.println();
	
				pw.println( "Temporal constraints (\"neighbors\" only)\n" + temp_constraints );
				pw.println();
				
				pw.println( "Temporal constraints (transitive closure)\n" + temp_constraints_transClosed );
				pw.println();
				
				pw.println( "Map: Path -> Constraints (transitive closure)\n" + map__path__temp_constraints_transClosed );
				pw.println();
				
				pw.flush();
			}
		}
		
		pi_flow = new HashMap<Flow,HashSet<Path>>();
		pi_flow_server = new HashMap<Pair<Flow,Server>,HashSet<Path>>();
		
		derivePiFlow();

		if( debug ) {
			System.out.println( "Pi_flow\n" +  pi_flow.toString() );
			System.out.println();
			System.out.println( "Pi_flow^server\n" +  pi_flow_server.toString() );
			System.out.println();
			
			if( print_debug ) {
				pw.println( "Pi_flow\n" +  pi_flow.toString() );
				pw.println();
				pw.println( "Pi_flow^server\n" +  pi_flow_server.toString() );
				pw.println();
				pw.flush();
			}
		}
		
		s2c_constraints = new HashSet<NumericalConstraint>();
		if( debug ) {
			System.out.println( "Strict service constraints:" );
			s2cConstraints();
			System.out.println( s2c_constraints );
			System.out.println();

			if( print_debug ) {
				pw.println( "Strict service constraints:" );
				pw.println( s2c_constraints );
				pw.println();
				pw.flush();
			}
		} else {
			s2cConstraints();
		}
		
		startBP_constraints = new HashSet<LogicalConstraint>();
		if( debug ) {
			System.out.println( "Start of backlogged periods constraints:" );
			backloggedPeriodsConstraints();
			System.out.println( startBP_constraints );
			System.out.println();
			
			if( print_debug ) {
				pw.println( "Start of backlogged periods constraints:" );
				pw.println( startBP_constraints );
				pw.println();
				pw.flush();
			}
		} else {
			backloggedPeriodsConstraints();
		}
		
		if( flow_constr_hdr ) {
			flow_constraints_hdr = new HashSet<LogicalConstraint>();
			if( debug ) {
				System.out.println( "Flow constraints HDR:" );
				flowConstraintsHdr();
				System.out.println( flow_constraints_hdr );
				System.out.println();

				if( print_debug ) {
					pw.println( "Flow constraints HDR:" );
					pw.println( flow_constraints_hdr );
					pw.println();
					pw.flush();
				}
			} else {
				flowConstraintsHdr();
			}
		} else {
			flow_constraints = new HashSet<LogicalConstraint>();
			if( debug ) {
				System.out.println( "Flow constraints:" );
				flowConstraints();
				System.out.println( flow_constraints );
				System.out.println();

				if( print_debug ) {
					pw.println( "Flow constraints:" );
					pw.println( flow_constraints );
					pw.println();
					pw.flush();
				}
			} else {
				flowConstraints();
			}
		}

		nonDecreasing_constraints = new HashSet<LogicalConstraint>();
		if( debug ) {
			System.out.println( "Non-decreasing constraints:\n" );
			nonDecreasingConstraints();
			System.out.println( nonDecreasing_constraints );
			System.out.println();

			if( print_debug ) {
				pw.println( "Non-decreasing constraints:\n" );
				pw.println( nonDecreasing_constraints );
				pw.println();
				pw.flush();
			}
		} else {
			nonDecreasingConstraints();
		}
		
		nonDecreasingAtSource_constraints = new HashSet<LogicalConstraint>();
		if( debug ) {
			System.out.println( "Non-decreasing constraints at sources:\n" );
			nonDecreasingAtSourcesConstraints();
			System.out.println( nonDecreasingAtSource_constraints );
			System.out.println();

			if( print_debug ) {
				pw.println( "Non-decreasing constraints at sources:\n" );
				pw.println( nonDecreasingAtSource_constraints );
				pw.println();
				pw.flush();
			}
		} else {
			nonDecreasingAtSourcesConstraints();
		}
		
		arrival_constraints = new HashSet<NumericalConstraint>();
		if( debug ) {
			System.out.println( "Arrival curve constraints:\n" );
			arrivalConstraints();
			System.out.println( arrival_constraints );
			System.out.println();

			if( print_debug ) {
				pw.println( "Arrival curve constraints:\n" );
				pw.println( arrival_constraints );
				pw.println();
				pw.close();
			}
		} else {
			arrivalConstraints();
		}
	}

	private void derivePi( Flow flow_to_trace, Server tracing_start, Path suffix ) throws Exception {
		Path path_to_trace = flow_to_trace.getSubPath( flow_to_trace.getSource(), tracing_start );
		
		if( debug ) {
			System.out.println( "ftt\t" + flow_to_trace.getAlias() );
			System.out.println( "suffix\t" + suffix.toShortString() );
			System.out.println( "ptt\t" + path_to_trace.toShortString() );
		}
		
		Server current_server;
		Path suffix_new = new Path( suffix );
		Set<Flow> flows_to_trace_next;
		LinkedList<Server> suffix_ftt_servers;
		
		for ( Iterator<Server> servers_reverse_iter = path_to_trace.getServers().descendingIterator(); servers_reverse_iter.hasNext(); ) {
			current_server = servers_reverse_iter.next();

			deriveConstraints( current_server, suffix_new ); // Need to use suffix_new because it gets updated in the loop.

			// Given flow to trace.
			suffix_ftt_servers = suffix_new.getServers();
			suffix_ftt_servers.addFirst( current_server );
			suffix_new = server_graph.createPathFromServers( suffix_ftt_servers );
			
			if( debug ) {
				System.out.println( "server\t" + current_server.getAlias() );
			}
			
			flows_to_trace_next = new HashSet<Flow>();
			for( Turn inlink : SetUtils.getDifference( server_graph.getInTurns( current_server ), new HashSet<Turn>( flow_to_trace.getTurnsOnPath() ) ) ) {
				Iterator<Flow> flow_iter = server_graph.getFlows( inlink ).iterator();
				if( flow_iter.hasNext() ) {
					flows_to_trace_next.add( flow_iter.next() );
				}
			}
			for ( Flow new_flow_to_trace : flows_to_trace_next ) {
				derivePi( new_flow_to_trace, new_flow_to_trace.getPath().getPrecedingServer(current_server), suffix_new );
			}
		}
	}
	
	/**
	 * Derive:
	 *  temp_constraints
	 *  temp_constraints_transClosed
	 *  map__j__jpi
	 *  map__j__pair_jpi_pi
	 *  
	 *  map__path__temp_constraints_transClosed
	 *  
	 */
	private void deriveConstraints( Server s, Path pi ) throws Exception {
		if( pi.getServers().isEmpty() ) { // Catches the special case at the flow of interest's sink.
			Path s_as_path = server_graph.createPathFromServers( new LinkedList<Server>( Collections.singleton( sink_of_interest ) ) );

			// Add the relation to the empty path.
			TemporalConstraint soi_le_empty_path = new TemporalConstraint( s_as_path, Relation.LE, empty_path );
					
			temp_constraints.add( soi_le_empty_path );
			temp_constraints_transClosed.add( soi_le_empty_path );
			map__path__temp_constraints_transClosed.put( s_as_path, new HashSet<TemporalConstraint>( Collections.singleton( soi_le_empty_path  ) ) );
					
			map__j__pair_jpi_pi.put( sink_of_interest, new HashSet<Pair<Path,Path>>( Collections.singleton( new Pair<Path,Path>( s_as_path, empty_path ) ) ) );
			map__j__jpi.put( sink_of_interest, new HashSet<Path>( Collections.singleton( s_as_path ) ) );
			
			return;
		}
		// Construct jpi.
		LinkedList<Server> jpi_servers = pi.getServers();
		jpi_servers.addFirst( s );
		
		Path jpi = server_graph.createPathFromServers( jpi_servers );
		
		// Mapping j -> jpi.
		HashSet<Path> new_jpi = new HashSet<Path>( Collections.singleton( jpi ) );
		HashSet<Path> prev_jpi = map__j__jpi.put( s, new_jpi );
		if( prev_jpi != null ) {
			new_jpi.addAll( prev_jpi );
		}

		// Mapping j -> (jpi,pi).
		HashSet<Pair<Path,Path>> new_pair_jpi_pi = new HashSet<Pair<Path,Path>>( Collections.singleton( new Pair<Path,Path>( jpi, pi ) ) );
		HashSet<Pair<Path,Path>> prev_pair_jpi_pi = map__j__pair_jpi_pi.put( s, new_pair_jpi_pi );
		if( prev_pair_jpi_pi != null ) {
			new_pair_jpi_pi.addAll( prev_pair_jpi_pi );
		}
		
		// Temporal constraints.
		TemporalConstraint jpi_le_pi = new TemporalConstraint( jpi, Relation.LE, pi );
		temp_constraints.add( jpi_le_pi );
		
		// Temporal constraints, transitively closed.
		Iterator<Server> pi_reverse_iter = pi.getServers().descendingIterator();
		Server pi_partial_src = pi_reverse_iter.next(); // skip the sink
		Server pi_partial_snk = pi.getSink();
		
		LinkedList<Server> pi_partial_servers = new LinkedList<Server>( Collections.singleton( pi_partial_snk ) );
		Path pi_partial = server_graph.createPathFromServers( pi_partial_servers );
		
		TemporalConstraint jpi_le_pi_partial = new TemporalConstraint( jpi, Relation.LE, pi_partial );
		temp_constraints_transClosed.add( jpi_le_pi_partial );

		TemporalConstraint jpi_le_empty_path = new TemporalConstraint( jpi, Relation.LE, empty_path );
		temp_constraints_transClosed.add( jpi_le_empty_path );
		
			// Store constraints for efficient search
			HashSet<TemporalConstraint> constraints_with_current_path = map__path__temp_constraints_transClosed.get( jpi );
			if( constraints_with_current_path == null ) {
				constraints_with_current_path = new HashSet<TemporalConstraint>( Collections.singleton( jpi_le_pi_partial ) );
				map__path__temp_constraints_transClosed.put( jpi, constraints_with_current_path );
			} else {
				constraints_with_current_path.add( jpi_le_pi_partial );
			}
			constraints_with_current_path.add( jpi_le_empty_path );
		
		for ( ; pi_reverse_iter.hasNext(); ) {
			pi_partial_src = pi_reverse_iter.next();
			pi_partial_servers.addFirst( pi_partial_src );
			pi_partial = server_graph.createPathFromServers( pi_partial_servers );
			
			jpi_le_pi_partial = new TemporalConstraint( jpi, Relation.LE, pi_partial );
			temp_constraints_transClosed.add( jpi_le_pi_partial );
			
				// Store constraints for efficient search.
				constraints_with_current_path.add( jpi_le_pi_partial );
		}
	}

	// PI_i^(j) and PI_i
	private void derivePiFlow() {
		// At this point, the order we travel through the network to query the pis does not matter.
		// Therefore, we can iterate over all the flows in in network.getFlows().
		
		for( Flow flow : server_graph.getFlows() ){
			HashSet<Path> set_flow_jpis_pis = new HashSet<Path>();
			
			for( Server j : flow.getServersOnPath() ) {
				HashSet<Pair<Path,Path>> paths_jpi_pi = map__j__pair_jpi_pi.get( j );
				Pair<Flow,Server> pair_flow_j = new Pair<Flow,Server>( flow, j );

				if( paths_jpi_pi == null ) {
					if( j == sink_of_interest ) {
						set_flow_jpis_pis.add( empty_path );
						pi_flow_server.put( pair_flow_j, new HashSet<Path>( Collections.singleton( empty_path ) ) );
					}
					continue;
				}
				
				HashSet<Path> set_jpis_pis = new HashSet<Path>();
				for( Pair<Path,Path> jpi_pi : paths_jpi_pi ) {
					set_jpis_pis.add( jpi_pi.getFirst() );
					set_jpis_pis.add( jpi_pi.getSecond() );
				}

				// Create HashMap<Flow,HashSet<Path>> pi_flow.
				set_flow_jpis_pis.addAll( set_jpis_pis );
								
				// Create HashMap<Pair<Flow,Server>,HashSet<Path>> pi_flow_server.
				pi_flow_server.put( pair_flow_j, set_jpis_pis );
			}
			
			if( !set_flow_jpis_pis.isEmpty() ) {
				pi_flow.put( flow, set_flow_jpis_pis );
			}
		}
	}
	
	public Relation getTempConstraintRelation( Path path1, Path path2 ) {
		HashSet<TemporalConstraint> constraints_path1 = map__path__temp_constraints_transClosed.get( path1 );
		HashSet<TemporalConstraint> constraints_path2 = map__path__temp_constraints_transClosed.get( path2 );
		
		TemporalConstraint constr_path1 = null;
		for( TemporalConstraint constr : constraints_path1 ) {
			if( constr.getPath2().equals( path2 ) ) {
				constr_path1 = constr;
				break;
			}
		}

		TemporalConstraint constr_path2 = null;
		for( TemporalConstraint constr : constraints_path2 ) {
			if( constr.getPath2().equals( path1 ) ) {
				constr_path2 = constr;
				break;
			}
		}
		
		if ( constr_path1 != null && constr_path2 != null ) {
			System.out.println( "Ambiguity: More than one constraint for" );
			System.out.println( "path1: " + path1.toShortString() );
			System.out.println( "constraint_path1: " + constr_path1.toString() );
			System.out.println( "path2: " + path2.toShortString() );
			System.out.println( "constraint_path2: " + constr_path2.toString() );
			System.exit( 0 );
		}
		// This here happens with rejoining flows when constructing the ULP because there is no total order between parallel paths.
		if ( constr_path1 == null && constr_path2 == null ) {
			return null;
		}
		
		TemporalConstraint constraint;
		if ( constr_path1 != null ) {
			constraint = constr_path1;
		} else {
			constraint = constr_path2;
		}
		
		Relation relation = constraint.getRelation();
		if( constraint.getPath1().equals( path1 ) 
				|| relation == Relation.E ) {
			return relation;
		} else {
			// Opposite relation!
			switch( relation ) {
				case L:
					return Relation.G;
				case LE:
				default:
					return Relation.GE;
				case GE:
					return Relation.LE;
				case G:
					return Relation.L;
			}
		}
	}
	
	private void s2cConstraints() {
		for( Server server_j : server_graph.getServers() ) {
			HashSet<Pair<Path,Path>> set_pair_jpi_pi = map__j__pair_jpi_pi.get( server_j );
			if( set_pair_jpi_pi == null ) {
				continue;
			}
			
			Num num_operations_factory = Num.getFactory(Calculator.getInstance().getNumBackend());
			
			for( Pair<Path,Path> pair_jpi_pi : set_pair_jpi_pi ) {
				
				// Create left side: "flow shapes".
				HashSet<Pair<Operator,FlowLocationTime>> left_side = new HashSet<Pair<Operator,FlowLocationTime>>();
				Pair<Operator,FlowLocationTime> flow_shape_term;
				
				for( Flow flow : server_graph.getFlows( server_j ) ) {
					flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.PLUS, new FlowLocationTime( flow, server_j, pair_jpi_pi.getSecond() ) );
					left_side.add( flow_shape_term );
					
					flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.MINUS, new FlowLocationTime( flow, server_j, pair_jpi_pi.getFirst() ) );
					left_side.add( flow_shape_term );
				}

				// Create right side: "numerical terms"
				HashSet<NumericalTerm> right_side;
				for( int i = 0; i < server_j.getServiceCurve().getRL_ComponentCount(); i++) { // Does the internal decomposition if not yet done.
					right_side = new HashSet<NumericalTerm>();
					Num beta_rate = server_j.getServiceCurve().getRL_Component(i).getUltAffineRate();
					Num beta_latency = server_j.getServiceCurve().getRL_Component(i).getLatency();
					
					NumericalTerm beta_pi = new NumericalTerm( Operator.MINUS, beta_rate, pair_jpi_pi.getFirst() );
					NumericalTerm beta_jpi = new NumericalTerm( Operator.PLUS, beta_rate, pair_jpi_pi.getSecond() );
					NumericalTerm latency = new NumericalTerm( Operator.MINUS, num_operations_factory.mult( beta_latency, beta_rate ), null );
					
					right_side.add( latency );
					right_side.add( beta_pi );
					right_side.add( beta_jpi );
		
					s2c_constraints.add( new NumericalConstraint( left_side, Relation.GE, right_side ) );
				}
				
				// Positivity constraint.

				// Create left side: "flow shapes" stay the same.
				// Create right side: "numerical terms" bounding at the x-axis.
				right_side = new HashSet<NumericalTerm>();
				NumericalTerm zero = new NumericalTerm( Operator.PLUS, num_operations_factory.getZero(), null );
				right_side.add( zero );
				
				s2c_constraints.add( new NumericalConstraint( left_side, Relation.GE, right_side ) );
			}
			

			
			// 2nd part (starts with "Moreover for all ..." in the INFOCOM 2010 paper).
			
			/* FIXME: Concurrent modification of path_pairs_to_check_against
			 * Don't worry, it will never be triggered. Here's why:
			 * 		This Moreover-part is used whenever there are equal temporal constraints.
			 * 		That will, however, not happen in the ULP (in contrast to the LP).
			 * 		Therefore, the following code will never be executed and the exception was never raised.
			 * It should be fixed nonetheless.
			 */
			HashSet<Pair<Path,Path>> path_pairs_to_check_against = new HashSet<Pair<Path,Path>>();
			Pair<Path,Path> current_path_pair;
			
			HashSet<Pair<Path,Path>> pairs_jpi_pi = map__j__pair_jpi_pi.get( server_j );
			if( pairs_jpi_pi != null && pairs_jpi_pi.size() > 1 ) {
				Iterator<Pair<Path,Path>> pairs_iter = pairs_jpi_pi.iterator();
				path_pairs_to_check_against.add( pairs_iter.next() );
				
				Relation relation;
				while( pairs_iter.hasNext() ) {
					current_path_pair = pairs_iter.next();
					
					for( Pair<Path,Path> other_path_pair : path_pairs_to_check_against ) {
						
						// Check if the jpis' relation.
						relation = getTempConstraintRelation( current_path_pair.getFirst(), other_path_pair.getFirst() );
						if( relation == null || relation != Relation.E ) {
							continue;
						}

						// Check if the pis' relation.
						relation = getTempConstraintRelation( current_path_pair.getSecond(), other_path_pair.getSecond() );
						if( relation == null ) {
							continue;
						}
						
						Path path1, path2;
						
						// Decide how to construct the left side, i.e., which t{} will be where.
						switch( relation ) {
							case L:
							case E: // This is not necessarily chosen deterministically, but may depend on the get-call above.
							case LE:
							default:
								// Switch t{...}s
								path1 = other_path_pair.getSecond();
								path2 = current_path_pair.getSecond();
								break;
							case GE:
							case G:
								path1 = current_path_pair.getSecond();
								path2 = other_path_pair.getSecond();
								break;
						}

						// Create left side: "flow shapes".
						HashSet<Pair<Operator,FlowLocationTime>> left_side = new HashSet<Pair<Operator,FlowLocationTime>>();
						Pair<Operator,FlowLocationTime> flow_shape_term;
						
						for( Flow flow : server_graph.getFlows( server_j ) ) {
							flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.PLUS, new FlowLocationTime( flow, server_j, path1 ) );
							left_side.add( flow_shape_term );
							
							flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.MINUS, new FlowLocationTime( flow, server_j, path2 ) );
							left_side.add( flow_shape_term );
						}

						// Create right side: "numerical terms".
						
						HashSet<NumericalTerm> right_side;
						for( int i = 0; i < server_j.getServiceCurve().getRL_ComponentCount(); i++) { // Does the internal decomposition if not yet done.
							right_side = new HashSet<NumericalTerm>();
							Num beta_rate = server_j.getServiceCurve().getRL_Component(i).getUltAffineRate();
							Num beta_latency = server_j.getServiceCurve().getRL_Component(i).getLatency();
							
							NumericalTerm beta_path1 = new NumericalTerm( Operator.MINUS, beta_rate, path1 );
							NumericalTerm beta_path2 = new NumericalTerm( Operator.PLUS, beta_rate, path2 );
							NumericalTerm latency = new NumericalTerm( Operator.MINUS, num_operations_factory.mult( beta_latency, beta_rate ), null );
							
							right_side.add( latency );
							right_side.add( beta_path1 );
							right_side.add( beta_path2 );
				
							s2c_constraints.add( new NumericalConstraint( left_side, Relation.GE, right_side ) );
						}
						
						path_pairs_to_check_against.add( current_path_pair );
					}
				}
			}
		}
	}
	
	private void backloggedPeriodsConstraints() {
		for( Server server_j : server_graph.getServers() ) {
			HashSet<Path> set_jpi = map__j__jpi.get( server_j );
			if( set_jpi == null ) {
				continue;
			}
			for( Path jpi : set_jpi ) {
				for( Flow flow : server_graph.getFlows( server_j ) ) {
					Server prec_j;
					try {
						prec_j = flow.getPrecedingServer( server_j );
					} catch (Exception e) { // server_j was the flow's source.
						prec_j = explicit_src; 
					}
					startBP_constraints.add( new LogicalConstraint( flow,
																	prec_j, jpi,
																	Relation.E,
																	server_j, jpi ) );
				}
			}
		}
	}
	
	private void flowConstraints() {
		for( Flow flow : pi_flow.keySet() ) {
			for( Server server_j : flow.getServersOnPath() ) {
				HashSet<Pair<Path,Path>> set_pair_jpi_pi = map__j__pair_jpi_pi.get( server_j );
				if( set_pair_jpi_pi == null ) {
					continue;
				}
				for( Pair<Path,Path> pair_jpi_pi : set_pair_jpi_pi ) {
					flow_constraints.add( new LogicalConstraint( flow,
																	server_j, pair_jpi_pi.getFirst(),
																	Relation.LE,
																	explicit_src, pair_jpi_pi.getFirst() ) );

					flow_constraints.add( new LogicalConstraint( flow,
																	server_j, pair_jpi_pi.getSecond(),
																	Relation.LE,
																	explicit_src, pair_jpi_pi.getSecond() ) );
				}
			}
		}
	}
	
	// Called "causality constraints" in the habilitation thesis.
	private void flowConstraintsHdr() {
		for( Flow flow : pi_flow.keySet() ) {
			for( Server server_j : flow.getServersOnPath() ) {
				HashSet<Pair<Path,Path>> set_pair_jpi_pi = map__j__pair_jpi_pi.get( server_j );
				if( set_pair_jpi_pi == null ) {
					continue;
				}
				for( Pair<Path,Path> pair_jpi_pi : set_pair_jpi_pi ) {
					Server precj;
					try{ // server_j is not the flow's source, then we also need to add the relations to the source.
						precj = flow.getPath().getPrecedingServer( server_j );
						
						flow_constraints_hdr.add( new LogicalConstraint( flow,
								explicit_src, pair_jpi_pi.getFirst(),
								Relation.GE,
								precj, pair_jpi_pi.getFirst() ) );

						flow_constraints_hdr.add( new LogicalConstraint( flow,
								explicit_src, pair_jpi_pi.getSecond(),
								Relation.GE,
								precj, pair_jpi_pi.getSecond() ) );
						
					} catch (Exception e) { // server_j is the flow's source.
						precj = explicit_src;
					}

					flow_constraints_hdr.add( new LogicalConstraint( flow,
																	precj, pair_jpi_pi.getFirst(),
																	Relation.GE,
																	server_j, pair_jpi_pi.getFirst() ) );
					
					flow_constraints_hdr.add( new LogicalConstraint( flow,
																	precj, pair_jpi_pi.getSecond(),
																	Relation.GE,
																	server_j, pair_jpi_pi.getSecond() ) );
				}
			}
		}
	}
	
	private void nonDecreasingConstraints() {
		HashSet<Path> paths_to_check_against = new HashSet<Path>();
		Path current_path;
		
		for( Flow flow : pi_flow.keySet() ) {
			for( Server server_j : flow.getServersOnPath() ) {
				
				paths_to_check_against.clear();
				HashSet<Path> set_paths = pi_flow_server.get( new Pair<Flow,Server>( flow, server_j ) );
				if( set_paths == null || set_paths.size() < 2 ) {
					continue;
				}
				
				Iterator<Path> paths_iter = set_paths.iterator();
				paths_to_check_against.add( paths_iter.next() );
				while( paths_iter.hasNext() ) {
					current_path = paths_iter.next();
					
					for( Path other_path : paths_to_check_against ) {
						Relation relation = getTempConstraintRelation( current_path, other_path );
						if( relation == null ) { // See comment in getTempConstraintRelation.
							continue;
						}

						Relation constraint_relation;
						switch( relation ) {
						case L:
						case LE:
							constraint_relation = Relation.LE;
							break;
						case E:
							constraint_relation = Relation.E;
							break;
						case GE:
						case G:
							constraint_relation = Relation.GE;
							break;
						default:
							constraint_relation = relation;
							break;
						}
						
						nonDecreasing_constraints.add( new LogicalConstraint( flow,
																		server_j, current_path,
																		constraint_relation,
																		server_j, other_path ) );
					}
					
					paths_to_check_against.add( current_path );
				}
			}
		}
	}
	
	private void nonDecreasingAtSourcesConstraints() {
		for( Flow flow : pi_flow.keySet() ) {
			for( TemporalConstraint constraint : temp_constraints ) {
				nonDecreasingAtSource_constraints.add( new LogicalConstraint( flow,
																				explicit_src, constraint.getPath1(),
																				constraint.getRelation(),
																				explicit_src, constraint.getPath2() ) );
			}
		}
	}
	
	private void arrivalConstraints() {
		HashSet<Path> paths_to_check_against = new HashSet<Path>();
		Path current_path;
		
		for( Flow flow : pi_flow.keySet() ) {
			
			paths_to_check_against.clear();
			HashSet<Path> set_paths = pi_flow.get( flow );
			if( set_paths == null || set_paths.size() < 2 ) {
				continue;
			}
			
			Iterator<Path> paths_iter = set_paths.iterator();
			paths_to_check_against.add( paths_iter.next() );
			while( paths_iter.hasNext() ) {
				current_path = paths_iter.next();
				
				for( Path other_path : paths_to_check_against ) {
					
					Relation relation = getTempConstraintRelation( current_path, other_path );
					if( relation == null ) { // See comment in getTempConstraintRelation.
						continue;
					}
					
					Path path1, path2;
					
					// Decide how to construct the left side, i.e., which t{} will be where.
					switch( relation ) {
						case L:
						case E: // This is not necessarily chosen deterministically, but may depend on the get-call above.
						case LE:
						default:
							// Switch t{...}s
							path1 = other_path;
							path2 = current_path;
							break;
						case GE:
						case G:
							path1 = current_path;
							path2 = other_path;
							break;
					}
					
					// Create left side: "flow shapes".
					HashSet<Pair<Operator,FlowLocationTime>> left_side = new HashSet<Pair<Operator,FlowLocationTime>>();
					Pair<Operator,FlowLocationTime> flow_shape_term;
					
					flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.PLUS, new FlowLocationTime( flow, explicit_src, path1 ) );
					left_side.add( flow_shape_term );
					
					flow_shape_term = new Pair<Operator,FlowLocationTime>( Operator.MINUS, new FlowLocationTime( flow, explicit_src, path2 ) );
					left_side.add( flow_shape_term );

					// Create right side: "numerical terms".
					HashSet<NumericalTerm> right_side;
					for( int i = 0; i < flow.getArrivalCurve().getTB_ComponentCount(); i++) { // Does the internal decomposition if not yet done.
						right_side = new HashSet<NumericalTerm>();
						Num alpha_rate = flow.getArrivalCurve().getTB_Component(i).getUltAffineRate();
						Num alpha_burst = flow.getArrivalCurve().getTB_Component(i).getBurst();
						
						NumericalTerm burst = new NumericalTerm( Operator.PLUS, alpha_burst, null );
						NumericalTerm alpha_t1 = new NumericalTerm( Operator.PLUS, alpha_rate, path1 );
						NumericalTerm alpha_t2 = new NumericalTerm( Operator.MINUS, alpha_rate, path2 );
						
						right_side.add( burst );
						right_side.add( alpha_t1 );
						right_side.add( alpha_t2 );
			
						arrival_constraints.add( new NumericalConstraint( left_side, Relation.LE, right_side ) );
					}
				}
				
				paths_to_check_against.add( current_path );
			}
		}
	}

	/**
	 * The LP file created by this function can be read by LpSolve.
	 */
	public void saveDelayLPv1( String output_path ) throws Exception {
 		File file = new File( output_path );
 		Writer w = new OutputStreamWriter( new FileOutputStream(file), "UTF-8" );
 		PrintWriter pw = new PrintWriter(w);
 		
 		String foi_str = flow_of_interest.getAlias();
		String foi_expl_src_str = explicit_src.getAlias();
		String foi_snk_str = flow_of_interest.getSink().getAlias();
		
		StringBuffer foi_path_strWr = new StringBuffer();
		for( Server server : flow_of_interest.getPath().getServers() ) {
			foi_path_strWr.append( server.getAlias() );
		}
		String foi_path_str = foi_path_strWr.toString();
		
		ArrivalCurve alpha_foi = flow_of_interest.getArrivalCurve();
		
		StringBuffer objective = new StringBuffer();
		
		objective.append( "/* Objective: Worst end-to-end delay for flow " + foi_str + " */ \n" );
		objective.append( "max: t{} - u;" );
		objective.append( "\n\n" );
		
		objective.append( "/* Insertion */ \n" );
		objective.append( "/* first(" + foi_str + ")-1 = " + foi_expl_src_str + " */ \n" );
		objective.append( "/* end(" + foi_str + ") = " + foi_snk_str  + " */ \n" );
		objective.append( "/* path from first to end: " + foi_path_str + " */ \n\n" );
		
		objective.append( "/* Position */ \n" );
		objective.append( "t{" + foi_path_str + "} <= u;" );
		objective.append( "\n" );
		objective.append( "u <= t{}; ");
		objective.append( "\n\n" );

		objective.append( "/* Monotony */ \n" );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " >= " );
		objective.append( foi_str + "_" + foi_snk_str + "_t{};" );
		objective.append( "\n\n" );

		objective.append( "/* Arrival curve constraints */ \n" );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " - " );
		objective.append( foi_str + "_" + foi_expl_src_str + "_t{" + foi_path_str + "}" );
		objective.append( " <= " );
		objective.append( alpha_foi.getBurst().toString() );
		objective.append( " + " );
		objective.append( alpha_foi.getUltAffineRate() + " u" );
		objective.append( " - " );
		objective.append( alpha_foi.getUltAffineRate() + " t{" + foi_path_str + "};\n" );
		
		objective.append( foi_str + "_" + foi_expl_src_str + "_t{}" );
		objective.append( " - " );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " <= " );
		objective.append( alpha_foi.getBurst().toString() );
		objective.append( " + " );
		objective.append( alpha_foi.getUltAffineRate() + "t{}" );
		objective.append( " - " );
		objective.append( alpha_foi.getUltAffineRate() + " u;" );

 		pw.println( objective.toString() );
 		pw.println( "\n" );
 		pw.print( lpSolveConstraintsString() );
 		
 		pw.close();
	}
	
	/**
	 * Backlog at the flow of interest's sink.
	 * The LP file created by this function can be read by LpSolve.
	 */
	public void saveBacklogLPv1( String output_path ) throws Exception {
		File file = new File( output_path );
 		Writer w = new OutputStreamWriter( new FileOutputStream(file), "UTF-8" );
 		PrintWriter pw = new PrintWriter(w);
 		
 		Server foi_snk = flow_of_interest.getSink();
		String foi_snk_str = flow_of_interest.getSink().getAlias();
		
		StringBuffer foi_path_strWr = new StringBuffer();
		for( Server server : flow_of_interest.getPath().getServers() ) {
			foi_path_strWr.append( server.getAlias() );
		}
		
		StringBuffer objective = new StringBuffer();
		
		objective.append( "/* Objective: Backlog bound at server " + foi_snk_str + " */ \n" );
		objective.append( "max: ");
		
		Server prec_i_s;
		for( Flow f : server_graph.getFlows( foi_snk ) ) {
			try{
				prec_i_s = f.getPrecedingServer( foi_snk );
			} catch (Exception e) { // foi_snk is this flow's source 
				prec_i_s = explicit_src;
			}
			objective.append( " + " + f.getAlias() + "_" + prec_i_s.getAlias() + "_t{}" );
			objective.append( " - " + f.getAlias() + "_" + foi_snk.getAlias() + "_t{}" );
		}
		objective.append( ";\n" );

 		pw.println( objective.toString() );
 		pw.print( lpSolveConstraintsString() );
 		
 		pw.close();
	}
	
	private String lpSolveConstraintsString() {
		StringBuffer result = new StringBuffer();
 		
 		result.append( "/* Temporal constraints */\n" );
 		for( TemporalConstraint temp_constr : temp_constraints ) {
 	 		result.append( temp_constr.toString() );
 	 		result.append( ";\n");
 		}

 		result.append( "\n/* Strict service constraints */\n" );
 		for( NumericalConstraint s2c_constr : s2c_constraints ) {
 	 		result.append( s2c_constr.toString() );
 	 		result.append( ";\n");
 		}

 		result.append( "\n/* Starts of backlogged periods */\n" );
 		for( LogicalConstraint startBP_constr : startBP_constraints ) {
 	 		result.append( startBP_constr.toString() );
 	 		result.append( ";\n");
 		}

 		if( flow_constr_hdr ) {
 	 		result.append( "\n/* Flow constraints HDR*/\n" );
 	 		for( LogicalConstraint flow_constr : flow_constraints_hdr ) {
 	 	 		result.append( flow_constr.toString() );
 	 	 		result.append( ";\n");
 	 		}
 		} else {
 	 		result.append( "\n/* Flow constraints */\n" );
 	 		for( LogicalConstraint flow_constr : flow_constraints ) {
 	 	 		result.append( flow_constr.toString() );
 	 	 		result.append( ";\n");
 	 		}
 		}

 		result.append( "\n/* Non-decreasing functions */\n" );
 		for( LogicalConstraint nonDecr_constr : nonDecreasing_constraints ) {
 	 		result.append( nonDecr_constr.toString() );
 	 		result.append( ";\n");
 		}

 		result.append( "\n/* Non-decreasing functions at sources*/\n" );
 		for( LogicalConstraint nonDecr_constr : nonDecreasingAtSource_constraints ) {
 	 		result.append( nonDecr_constr.toString() );
 	 		result.append( ";\n");
 		}

 		result.append( "\n/* Arrival constraints */\n" );
 		for( NumericalConstraint arrival_constr : arrival_constraints ) {
 	 		result.append( arrival_constr.toString() );
 	 		result.append( ";\n");
 		}

 		return result.toString();
	}

	/**
	 * The LP file created by this function can be read by CPLEX and by Gurobi.
	 */
	public void saveDelayLPv2( String output_path ) throws Exception {
 		
 		File file = new File( output_path );
 		Writer w = new OutputStreamWriter( new FileOutputStream(file), "UTF-8" );
 		PrintWriter pw = new PrintWriter(w);
 		
 		String foi_str = flow_of_interest.getAlias();
		String foi_expl_src_str = explicit_src.getAlias();
		String foi_snk_str = flow_of_interest.getSink().getAlias();
		
		StringBuffer foi_path_strWr = new StringBuffer();
		for( Server server : flow_of_interest.getPath().getServers() ) {
			foi_path_strWr.append( server.getAlias() );
		}
		String foi_path_str = foi_path_strWr.toString();
		
		ArrivalCurve alpha_foi = flow_of_interest.getArrivalCurve();
		
		StringBuffer objective = new StringBuffer();
		
		objective.append( "\\ Objective: Worst end-to-end delay for flow " + foi_str + "\n" );
		objective.append( "Maximize\n" );
		objective.append( "t{} - u" );
		objective.append( "\n\n" );
		
		objective.append( "Subject To" );
		objective.append( "\n\n" );
		
		objective.append( "\\ Insertion\n" );
		objective.append( "\\ first(" + foi_str + ")-1 = " + foi_expl_src_str + "\n" );
		objective.append( "\\ end(" + foi_str + ") = " + foi_snk_str  + "\n" );
		objective.append( "\\ path from first to end: " + foi_path_str + "\n\n" );
		
		objective.append( "\\ Position\n" );
		objective.append( "t{" + foi_path_str + "} - u <= 0" );
		objective.append( "\n" );
		objective.append( "u -t{} <= 0 ");
		objective.append( "\n\n" );

		objective.append( "\\ Monotony\n" );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " - " );
		objective.append( foi_str + "_" + foi_snk_str + "_t{}" );
		objective.append( " >= " );
		objective.append( "0" );
		objective.append( "\n\n" );

		objective.append( "\\ Arrival curve constraints\n" );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " - " );
		objective.append( foi_str + "_" + foi_expl_src_str + "_t{" + foi_path_str + "}" );
		objective.append( " - " );
		objective.append( alpha_foi.getUltAffineRate() + " u" );
		objective.append( " + " );
		objective.append( alpha_foi.getUltAffineRate() + " t{" + foi_path_str + "}" );
		objective.append( " <= " );
		objective.append( alpha_foi.getBurst().toString() );
		objective.append( "\n" );
		
		objective.append( foi_str + "_" + foi_expl_src_str + "_t{}" );
		objective.append( " - " );
		objective.append( foi_str + "_" + foi_expl_src_str + "_u" );
		objective.append( " - " );
		objective.append( alpha_foi.getUltAffineRate() + "t{}" );
		objective.append( " + " );
		objective.append( alpha_foi.getUltAffineRate() + " u" );
		objective.append( " <= " );
		objective.append( alpha_foi.getBurst().toString() );

 		pw.println( objective.toString() );
 		pw.println( "\n" );
 		pw.flush();
 		
 		pw.println( "\\ Temporal constraints" );
 		for( TemporalConstraint temp_constr : temp_constraints ) {
 	 		pw.println( temp_constr.toCPLEXstring() );
 		}
 		pw.println();
 		pw.flush();

 		pw.println( "\\ Strict service constraints" );
 		for( NumericalConstraint s2c_constr : s2c_constraints ) {
 	 		pw.println( s2c_constr.toCPLEXstring() );
 		}
 		pw.println();
 		pw.flush();

 		pw.println( "\\ Starts of backlogged periods" );
 		for( LogicalConstraint startBP_constr : startBP_constraints ) {
 	 		pw.println( startBP_constr.toCPLEXstring() );
 		}
 		pw.println();
 		pw.flush();

 		if( flow_constr_hdr ) {
 	 		pw.println( "\\ Flow constraints HDR*/" );
 	 		for( LogicalConstraint flow_constr : flow_constraints_hdr ) {
 	 	 		pw.println( flow_constr.toCPLEXstring() );
 	 		}
 		} else {
 	 		pw.println( "\\ Flow constraints" );
 	 		for( LogicalConstraint flow_constr : flow_constraints ) {
 	 	 		pw.println( flow_constr.toCPLEXstring() );
 	 		}
 		}
 		pw.println();
 		pw.flush();

 		pw.println( "\\ Non-decreasing functions" );
 		for( LogicalConstraint nonDecr_constr : nonDecreasing_constraints ) {
 	 		pw.println( nonDecr_constr.toCPLEXstring() );
 		}
 		pw.println();
 		pw.flush();

 		pw.println( "\\ Non-decreasing functions at sources" );
 		for( LogicalConstraint nonDecr_constr : nonDecreasingAtSource_constraints ) {
 	 		pw.println( nonDecr_constr.toCPLEXstring() );
 		}
 		pw.println();
 		pw.flush();

 		pw.println( "\\ Arrival constraints" );
 		for( NumericalConstraint arrival_constr : arrival_constraints ) {
 	 		pw.println( arrival_constr.toCPLEXstring() );
 		}
 		pw.println();

 		pw.println( "End" );
 		
 		pw.close();
	}
	
	public static double cplexDelay( InputStream inputStream ) throws IOException {
		BufferedReader br = null;
		
		double result = Double.NaN;
		br = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while ((line = br.readLine()) != null) {
			if( line.contains( "Objective = " ) ) {
				try{
					result = Double.parseDouble( line.substring( line.indexOf( "Objective =" )+12 ) );
				} catch (Exception e) {
					e.printStackTrace();
					result = Double.NaN;
				}
				break;
			}
		}
		return result;
	}

	public static double lpSolveDelay( InputStream inputStream ) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		
		return Double.parseDouble( sb.substring( sb.lastIndexOf( " " )+1 ).toString() );
	}
}
