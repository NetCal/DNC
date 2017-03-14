/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.3 "Centaur".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
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

import java.util.LinkedList;
import java.util.List;

import unikl.disco.curves.ArrivalCurve;

/**
 * Class representing flows through the network.
 *
 * @author Frank A. Zdarsky
 * @author Steffen Bondorf
 */
public class Flow {
	public static final Flow NULL_FLOW = createDummyFlow( "null", ArrivalCurve.createZeroArrival(), Path.createEmptyPath() ); 
	
	/** The flow's ID. */
	private int	id;
	/** The flow's arrival curve */
	private ArrivalCurve arrival_curve;
	
	private String alias;
	/** The link path the flow traverses incl explicit sink */
	private Path path;
	
	/**
	 * Creates a dummy flow with an arrival curve.<br>
	 * All dummy flows share the same id -1.
	 * 
	 * @param alias The flow's alias (not necessarily unique).
	 * @param ac The flow's arrival curve.
	 * @param path The link path the flow traverses.
	 * @return a dummy flow
	 */
	public static Flow createDummyFlow( String alias, ArrivalCurve ac, Path path ) {
		Flow result = new Flow();
		result.alias = alias;
		result.arrival_curve = ac;
		result.path = path;
		return result;
	}
	
	/**
	 * 
	 * @param id The flow's id (unique).
	 * @param alias The flow's alias (not necessarily unique).
	 * @param ac The flow's arrival curve.
	 * @param path The link path the flow traverses.
	 */
	protected Flow( int id, String alias, ArrivalCurve ac, Path path ) {
		this.id = id;
		this.alias = alias;
		this.arrival_curve = ac;
		this.path = path;
	}
	
	private Flow() {
		this.id = -1;
		this.arrival_curve = null;
		this.path = null;
	}
	
	public boolean setArrivalCurve( ArrivalCurve arrival_curve ) {
		this.arrival_curve = arrival_curve;
		return true;
	}

	/**
	 * 
	 * @return A copy of the arrival curve
	 */
	public ArrivalCurve getArrivalCurve() {
		return arrival_curve.copy();
	}
	
	public int getId() {
		return id;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias( String alias ) {
		this.alias = alias;
	}

	public Path getPath() {
		return path;
	}
	
	public String toShortString() {
		return "Flow(" + alias + "," + Integer.toString( id ) + "," + arrival_curve.toString() + path.toShortString() + ")";
	}
	
	/**
	 * @return A string representation of the flow
	 */
	@Override
	public String toString() {
		return "Flow(" + alias + "," + Integer.toString( id ) + "," + arrival_curve.toString() + path.toString() + ")";
	}
	
	public String toLongString() {
		return "Flow(" + alias + "," + Integer.toString( id ) + "," + arrival_curve.toString() + path.toExtendedString() + ")";
	}

	// --------------------------------------------------------------------------------------------
	// Shortcuts to conveniently access the path's according methods  
	// --------------------------------------------------------------------------------------------
	public Server getSource() {
		return path.getSource();
	}
	
	public Server getSink() {
		return path.getSink();
	}

    public LinkedList<Server> getServersOnPath() {
    	return new LinkedList<Server>( path.getServers() );
    }

    public List<Link> getLinksOnPath() throws Exception {
    	return new LinkedList<Link>( path.getLinks() );
    }

	/**
	 * 
	 * @param from Source, inclusive.
	 * @param to Sink, inclusive.
	 * @return The subpath.
	 * @throws Exception No subpath found; most probably an input parameter problem.
	 */
    public Path getSubPath( Server from, Server to ) throws Exception {
    	return path.getSubPath( from, to );
    }
    
	public Link getPrecedingLink( Server s ) throws Exception {
		return path.getPrecedingLink( s );
	}
	
 	public Link getSucceedingLink( Server s ) throws Exception {
 		return path.getSucceedingLink( s );
 	}

 	public Server getPrecedingServer( Server s ) throws Exception {
 		return path.getPrecedingServer( s );
 	}

 	public Server getSucceedingServer( Server s ) throws Exception {
 		return path.getSucceedingServer( s );
 	}
}