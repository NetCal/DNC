/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;

/**
 * A flows path is a sequence of crossed buffers -- either represented by the
 * sequence servers (said buffers) or turns connecting them.
 * <p>
 * Remember for modeling purpose: A flow usually does not reach the output
 * buffer of its sink. Therefore a flow's path should not contain a turn to it.
 * Otherwise the flow interference pattern of the network will be too
 * pessimistic, yet, the results remain valid.
 */
public class Path {
    private LinkedList<Server> path_servers;
    private LinkedList<Turn> path_turns;

    private Path() {
        path_servers = new LinkedList<Server>();
        path_turns = new LinkedList<Turn>();
    }

    protected Path(List<Server> path_servers, List<Turn> path_turns) {
        // Sanity check should have been done by the server graph
        this.path_servers = new LinkedList<Server>(path_servers);
        this.path_turns = new LinkedList<Turn>(path_turns);
    }

    public Path(Path path) {
        this.path_servers = path.getServers();
        this.path_turns = path.getTurns();
    }

    // Can be visible.
    // There's no way to create a single hop path not possible to take in a network.
    public Path(Server single_hop) {
        path_servers = new LinkedList<Server>();
        path_servers.add(single_hop);
        
        path_turns = new LinkedList<Turn>();
    }

    public static Path createEmptyPath() {
        return new Path();
    }

    public Server getSource() {
        return path_servers.get(0);
    }
    
    public boolean isSource(Server s) {
    	return path_servers.indexOf(s) == 0;
    }

    public Server getSink() {
        return path_servers.get(path_servers.size() - 1);
    }

    public int numServers() {
        return path_servers.size();
    }

    public int numTurns() {
        return path_turns.size();
    }

    public LinkedList<Turn> getTurns() {
        return new LinkedList<Turn>(path_turns);
    }

    public LinkedList<Server> getServers() {
        return new LinkedList<Server>(path_servers);
    }

    /**
     * @param from Source, inclusive.
     * @param to   Sink, inclusive.
     * @return The subpath.
     * @throws Exception No subpath found; most probably an input parameter problem.
     */
    public Path getSubPath(Server from, Server to) throws Exception {
        // All other sanity check should have been passed when this object was created
        if (!path_servers.contains(from)) {
            throw new Exception("Cannot create a subpath if source is not in it.");
        }
        if (!path_servers.contains(to)) {
            throw new Exception("Cannot create a subpath if sink is not in it.");
        }

        if (from == to) {
            return new Path(new LinkedList<Server>(Collections.singleton(from)), new LinkedList<Turn>());
        }

        int from_index = path_servers.indexOf(from);
        int to_index = path_servers.indexOf(to);
        if (from_index >= to_index) {
            throw new Exception("Cannot create sub-path from " + from.toString() + " to " + to.toString());
        }
        // subList: 'from' is inclusive but 'to' is exclusive
        LinkedList<Server> subpath_servers = new LinkedList<Server>(path_servers.subList(from_index, to_index));
        subpath_servers.add(to);

        List<Turn> subpath_turns = new LinkedList<Turn>();
        if (subpath_servers.size() > 1) {
            for (Turn l : path_turns) {
                Server src_l = l.getSource();
                Server snk_l = l.getDest();
                if (subpath_servers.contains(src_l) && subpath_servers.contains(snk_l)) {
                    subpath_turns.add(l);
                }
            }
        }

        return new Path(subpath_servers, subpath_turns);
    }

    public Turn getPrecedingTurn(Server s) throws Exception {
        for (Turn l : path_turns) {
            if (l.getDest().equals(s)) {
                return l;
            }
        }
        throw new Exception("No preceding turn on the path found");
    }

    public Turn getSucceedingTurn(Server s) throws Exception {
        for (Turn l : path_turns) {
            if (l.getSource().equals(s)) {
                return l;
            }
        }
        throw new Exception("No succeeding turn on the path found");
    }
    
    public Server getPrecedingServer(Server s) throws Exception {
        try {
            return getPrecedingTurn(s).getSource();
        } catch (Exception e) {
            throw new Exception("No preceding server on the path found");
        }
    }

    public Server getSucceedingServer(Server s) throws Exception {
        try {
            return getSucceedingTurn(s).getDest();
        } catch (Exception e) {
            throw new Exception("No succeeding server on the path found");
        }
    }

    /**
     * Just convolves the service curves on the path and returns the result.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public ServiceCurve getServiceCurve() throws Exception {
        Collection<Server> servers = getServers();
        return getServiceCurve(servers);
    }

    private ServiceCurve getServiceCurve(Collection<Server> servers) throws Exception {
        ServiceCurve service_curve_total = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            service_curve_total = Calculator.getInstance().getMinPlus().convolve(service_curve_total, s.getServiceCurve());
        }

        return service_curve_total;
    }

    /**
     * Returns the convolution of the maximum service curves of all servers on the
     * given lnik path <code>path</code> that have the useMaxSC flag set.<br>
     * If a server either has no maximum service curve set or useMaxSC is disabled,
     * calculations take place with the default maximum service curve, i.e., the
     * zero delay burst curve, so the result will not be influenced.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public MaxServiceCurve getMaxServiceCurve() throws Exception {
        Collection<Server> servers = getServers();
        return getMaxServiceCurve(servers);
    }

    private MaxServiceCurve getMaxServiceCurve(Collection<Server> servers) throws Exception {
        MaxServiceCurve max_sc_path = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            max_sc_path = Calculator.getInstance().getMinPlus().convolve(max_sc_path, s.getMaxServiceCurve());
        }

        return max_sc_path;
    }

    public MaxServiceCurve getStoredMaxSC() throws Exception {
        Collection<Server> servers = getServers();
        return getStoredMaxSC(servers);
    }

    private MaxServiceCurve getStoredMaxSC(Collection<Server> servers) throws Exception {
        MaxServiceCurve max_sc_path = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            max_sc_path = Calculator.getInstance().getMinPlus().convolve(max_sc_path, s.getStoredMaxSC());
        }

        return max_sc_path;
    }

    /**
     * Returns the convolution of the maximum service curves of all servers on the
     * given turn path <code>path</code> that have the useMaxScRate flag set.<br>
     * If a server either has no maximum service curve set or useMaxScRate is
     * disabled, calculations take place with the default maximum service curve,
     * i.e., the zero delay burst curve, so the result will not be influenced.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public MaxServiceCurve getMaxScRate() throws Exception {
        Collection<Server> servers = getServers();
        return getMaxScRate(servers);
    }

    private MaxServiceCurve getMaxScRate(Collection<Server> servers) throws Exception {
        MaxServiceCurve max_sc_rate_path = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            max_sc_rate_path = Calculator.getInstance().getMinPlus().convolve(max_sc_rate_path, s.getMaxScRate());
        }
        // max_sc_rate_path.removeLatency(); // Already done by s.getMaxScRate()

        return max_sc_rate_path;
    }
    
    public MaxServiceCurve getStoredMaxScRate() throws Exception {
        Collection<Server> servers = getServers();
        return getStoredMaxScRate(servers);
    }

    private MaxServiceCurve getStoredMaxScRate(Collection<Server> servers) throws Exception {
        MaxServiceCurve max_sc_rate_path = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            max_sc_rate_path = Calculator.getInstance().getMinPlus().convolve(max_sc_rate_path, s.getStoredMaxScRate());
        }
        // max_sc_rate_path.removeLatency(); // Already done by s.getStoredMaxScRate()

        return max_sc_rate_path;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Path)) {
            return false;
        }

        Path p = (Path) obj;
        return path_servers.equals(p.getServers()) && path_turns.equals(p.getTurns());
    }

    @Override
    public int hashCode() {
    	return Objects.hash(path_servers, path_turns);
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // String Conversions
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Print path as series of servers (short representation).
     * 
     * @return String representation of the path.
     */
    public String toShortString() {
        if (path_servers.isEmpty()) {
            return "{}";
        }
        
    	StringBuffer path_str = new StringBuffer();

    	path_str.append("{");
    	for (Server s : path_servers) {
    		path_str.append(s.toShortString());
        	path_str.append(",");
        }
    	
    	path_str.deleteCharAt(path_str.length()-1); // Remove the trailing comma.
    	path_str.append("}");
         
    	return path_str.toString();
    }

    /**
     * Print path as series of turns (short representation).
     *  
     * @return String representation of the path.
     */
    @Override
    public String toString() {
        if (path_turns.isEmpty()) {
        	return toShortString();
        }
        
        StringBuffer path_str = new StringBuffer();

    	path_str.append("{");
        for (Turn l : path_turns) {
        	path_str.append(l.toString());
        	path_str.append(",");
        }
        
    	path_str.deleteCharAt(path_str.length()-1); // Remove the trailing comma.
    	path_str.append("}");
         
    	return path_str.toString();
    }

    
    /**
     * Print path as series of turns (extended representation).
     * 
     * @return String representation of the path.
     */
    public String toExtendedString() {
        if (path_turns.isEmpty()) {
        	return toShortString();
        }
        
        StringBuffer path_str = new StringBuffer();

    	path_str.append("{");
        for (Turn l : path_turns) {
        	path_str.append(l.toExtendedString());
        	path_str.append(",");
        }
        
    	path_str.deleteCharAt(path_str.length()-1); // Remove the trailing comma.
    	path_str.append("}");
         
    	return path_str.toString();
    }
}
