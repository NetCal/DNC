/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
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

import java.util.LinkedList;
import java.util.List;

import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;

/**
 * Class representing flows through the network.
 */
public class Flow {
    public static final Flow NULL_FLOW = createDummyFlow("null", Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get(),
            Path.createEmptyPath());

    /**
     * The flow's ID.
     */
    private int id;
    /**
     * The flow's arrival curve
     */
    private ArrivalCurve arrival_curve;

    private String alias;
    /**
     * The turn path the flow traverses incl explicit sink
     */
    private Path path;

    /**
     * @param id    The flow's id (unique).
     * @param alias The flow's alias (not necessarily unique).
     * @param ac    The flow's arrival curve.
     * @param path  The turn path the flow traverses.
     */
    protected Flow(int id, String alias, ArrivalCurve ac, Path path) {
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

    /**
     * Creates a dummy flow with an arrival curve.<br>
     * All dummy flows share the same id -1.
     *
     * @param alias The flow's alias (not necessarily unique).
     * @param ac    The flow's arrival curve.
     * @param path  The turn path the flow traverses.
     * @return a dummy flow
     */
    public static Flow createDummyFlow(String alias, ArrivalCurve ac, Path path) {
        Flow result = new Flow();
        result.alias = alias;
        result.arrival_curve = ac;
        result.path = path;
        return result;
    }

    public boolean setArrivalCurve(ArrivalCurve arrival_curve) {
        this.arrival_curve = arrival_curve;
        return true;
    }

    /**
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

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Path getPath() {
        return path;
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // String Conversions
    // --------------------------------------------------------------------------------------------------------------
    
    private StringBuffer commonStringPrefix() {
    	StringBuffer flow_str_prefix = new StringBuffer();

     	flow_str_prefix.append("Flow(");
     	flow_str_prefix.append(alias);
     	flow_str_prefix.append(", ");
     	flow_str_prefix.append(Integer.toString(id));
     	flow_str_prefix.append(", ");
     	flow_str_prefix.append(arrival_curve.toString());
     	flow_str_prefix.append(", ");
     	
     	return flow_str_prefix;
    }

    public String toShortString() {
    	StringBuffer flow_str = commonStringPrefix();

    	flow_str.append(path.toShortString());
    	flow_str.append(")");
    	
        return flow_str.toString();
    }

    /**
     * @return String representation of the flow.
     */
    @Override
    public String toString() {
    	StringBuffer flow_str = commonStringPrefix();
    	
    	flow_str.append(path.toString());
    	flow_str.append(")");
    	
    	return flow_str.toString();
    }

    public String toLongString() {
    	StringBuffer flow_str = commonStringPrefix();
    	
    	flow_str.append(path.toExtendedString());
    	flow_str.append(")");
    	
    	return flow_str.toString();
    }

    // --------------------------------------------------------------------------------------------------------------
    // Shortcuts to conveniently access the path's according methods
    // --------------------------------------------------------------------------------------------------------------
    
    public Server getSource() {
        return path.getSource();
    }

    public Server getSink() {
        return path.getSink();
    }

    public LinkedList<Server> getServersOnPath() {
        return new LinkedList<Server>(path.getServers());
    }

    public List<Turn> getTurnsOnPath() throws Exception {
        return new LinkedList<Turn>(path.getTurns());
    }

    /**
     * @param from Source, inclusive.
     * @param to   Sink, inclusive.
     * @return The subpath.
     * @throws Exception No subpath found; most probably an input parameter problem.
     */
    public Path getSubPath(Server from, Server to) throws Exception {
        return path.getSubPath(from, to);
    }

    public Turn getPrecedingTurn(Server s) throws Exception {
        return path.getPrecedingTurn(s);
    }

    public Turn getSucceedingTurn(Server s) throws Exception {
        return path.getSucceedingTurn(s);
    }

    public Server getPrecedingServer(Server s) throws Exception {
        return path.getPrecedingServer(s);
    }

    public Server getSucceedingServer(Server s) throws Exception {
        return path.getSucceedingServer(s);
    }
}