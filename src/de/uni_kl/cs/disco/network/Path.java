/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.network;

import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.minplus.MinPlus;

import java.util.*;

/**
 * A flows path is a sequence of crossed buffers -- either represented by the
 * sequence servers (said buffers) or links connecting them.
 * <p>
 * Remember for modeling purpose: A flow usually does not reach the output
 * buffer of its sink. Therefore a flow's path should not contain a link to it.
 * Otherwise the flow interference pattern of the network will be too
 * pessimistic, yet, the results remain valid.
 */
public class Path {
    private LinkedList<Server> path_servers = new LinkedList<Server>();
    private LinkedList<Link> path_links = new LinkedList<Link>();

    private Path() {
    }

    protected Path(List<Server> path_servers, List<Link> path_links) {
        // Sanity check should have been done by the network
        this.path_servers = new LinkedList<Server>(path_servers);
        this.path_links = new LinkedList<Link>(path_links);
    }

    protected Path(Path path) {
        this.path_servers = path.getServers();
        this.path_links = path.getLinks();
    }

    // Can be visible.
    // There's no way to create a single hop path not possible to take in a network.
    public Path(Server single_hop) {
        this.path_servers.add(single_hop);
    }

    public static Path createEmptyPath() {
        return new Path();
    }

    public Server getSource() {
        return path_servers.get(0);
    }

    public Server getSink() {
        return path_servers.get(path_servers.size() - 1);
    }

    public int numServers() {
        return path_servers.size();
    }

    public int numLinks() {
        return path_links.size();
    }

    public LinkedList<Link> getLinks() {
        return new LinkedList<Link>(path_links);
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
            return new Path(new LinkedList<Server>(Collections.singleton(from)), new LinkedList<Link>());
        }

        int from_index = path_servers.indexOf(from);
        int to_index = path_servers.indexOf(to);
        if (from_index >= to_index) {
            throw new Exception("Cannot create sub-path from " + from.toString() + " to " + to.toString());
        }
        // subList: 'from' is inclusive but 'to' is exclusive
        LinkedList<Server> subpath_servers = new LinkedList<Server>(path_servers.subList(from_index, to_index));
        subpath_servers.add(to);

        List<Link> subpath_links = new LinkedList<Link>();
        if (subpath_servers.size() > 1) {
            for (Link l : path_links) {
                Server src_l = l.getSource();
                Server snk_l = l.getDest();
                if (subpath_servers.contains(src_l) && subpath_servers.contains(snk_l)) {
                    subpath_links.add(l);
                }
            }
        }

        return new Path(subpath_servers, subpath_links);
    }

    public Link getPrecedingLink(Server s) throws Exception {
        for (Link l : path_links) {
            if (l.getDest().equals(s)) {
                return l;
            }
        }
        throw new Exception("No preceding link on the path found");
    }

    public Link getSucceedingLink(Server s) throws Exception {
        for (Link l : path_links) {
            if (l.getSource().equals(s)) {
                return l;
            }
        }
        throw new Exception("No succeeding link on the path found");
    }

    public Server getPrecedingServer(Server s) throws Exception {
        try {
            return getPrecedingLink(s).getSource();
        } catch (Exception e) {
            throw new Exception("No preceding server on the path found");
        }
    }

    public Server getSucceedingServer(Server s) throws Exception {
        try {
            return getSucceedingLink(s).getDest();
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
        ServiceCurve service_curve_total = CurvePwAffine.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            service_curve_total = MinPlus.convolve(service_curve_total, s.getServiceCurve());
        }

        return service_curve_total;
    }

    /**
     * Returns the convolution of the maximum service curves of all servers on the
     * given lnik path <code>path</code> that have the useGamma flag set.<br>
     * If a server either has no maximum service curve set or useGamma is disabled,
     * calculations take place with the default maximum service curve, i.e., the
     * zero delay burst curve, so the result will not be influenced.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public MaxServiceCurve getGamma() throws Exception {
        Collection<Server> servers = getServers();
        return getGamma(servers);
    }

    private MaxServiceCurve getGamma(Collection<Server> servers) throws Exception {
        MaxServiceCurve gamma_total = CurvePwAffine.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            gamma_total = MinPlus.convolve(gamma_total, s.getGamma());
        }

        return gamma_total;
    }

    /**
     * Returns the convolution of the maximum service curves of all servers on the
     * given link path <code>path</code> that have the useExtraGamma flag set.<br>
     * If a server either has no maximum service curve set or useExtraGamma is
     * disabled, calculations take place with the default maximum service curve,
     * i.e., the zero delay burst curve, so the result will not be influenced.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public MaxServiceCurve getExtraGamma() throws Exception {
        Collection<Server> servers = getServers();
        return getExtraGamma(servers);
    }

    private MaxServiceCurve getExtraGamma(Collection<Server> servers) throws Exception {
        MaxServiceCurve extra_gamma_total = CurvePwAffine.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            extra_gamma_total = MinPlus.convolve(extra_gamma_total, s.getExtraGamma());
        }
        // extra_gamma_total.removeLatency(); // Already done by s.getExtraGamma()

        return extra_gamma_total;
    }

    /**
     * Returns the convolution of the maximum service curves of all servers on the
     * given link path <code>path</code><br>
     * If a server has no maximum service curve, calculations take place with the
     * default maximum service curve, i.e., the zero delay burst curve, so the
     * result will not be influenced.
     *
     * @return The convolved curve
     * @throws Exception
     */
    public MaxServiceCurve getMaxServiceCurve() throws Exception {
        Collection<Server> servers = getServers();
        return getMaxServiceCurve(servers);
    }

    private MaxServiceCurve getMaxServiceCurve(Collection<Server> servers) throws Exception {
        MaxServiceCurve max_service_curve_total = CurvePwAffine.getFactory().createZeroDelayInfiniteBurstMSC();
        for (Server s : servers) {
            max_service_curve_total = MinPlus.convolve(max_service_curve_total, s.getMaxServiceCurve());
        }

        return max_service_curve_total;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Path)) {
            return false;
        }

        Path p = (Path) obj;
        return path_servers.equals(p.getServers()) && path_links.equals(p.getLinks());
    }

    @Override
    public int hashCode() {
        return (int) Arrays.hashCode(path_servers.toArray()) * Arrays.hashCode(path_links.toArray());
    }

    // Print path as series of servers (short representation)
    public String toShortString() {
        if (path_servers.isEmpty()) {
            return "{}";
        }

        String result_str = "{";
        for (Server s : path_servers) {
            result_str = result_str.concat(s.toShortString() + ",");
        }
        result_str = result_str.substring(0, result_str.length() - 1); // Remove the trailing comma.
        return result_str.concat("}");
    }

    @Override
    // Print path as series of links (short representation)
    public String toString() {
        if (path_links.isEmpty()) {
            return toShortString();
        }

        String result_str = "{";

        for (Link l : path_links) {
            result_str = result_str.concat(l.toString() + ",");
        }
        result_str = result_str.substring(0, result_str.length() - 1); // Remove the trailing comma.

        return result_str.concat("}");
    }

    // Print path as series of links (extended representation)
    public String toExtendedString() {
        if (path_links.isEmpty()) {
            return toShortString();
        }

        String result_str = "{";

        for (Link l : path_links) {
            result_str = result_str.concat(l.toExtendedString() + ",");
        }
        result_str = result_str.substring(0, result_str.length() - 1); // Remove the trailing comma.

        return result_str.concat("}");
    }
}
