/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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

package de.uni_kl.cs.discodnc.network;

import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.MaxServiceCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.minplus.MinPlus;

import java.util.*;

/**
 *
 * A multicast path is a collection of unicast paths
 *
 * A unicast path inside a multicast one is a sequence of crossed buffers -- either represented by
 * the sequence servers (said buffers) or links connecting them.
 * Each unicast path is identified by an 'id'. In order to access a specific unicast path
 * inside a multicast one you must pass it as a parameter
 *  Ex. getServers() -> access the first unicast path available (used for unicast flows)
 *      getServers(id) -> access a specific unicast path
 *
 * Remember for modeling purpose:
 * A flow usually does not reach the output buffer of its sink.
 * Therefore a flow's path should not contain a link to it.
 * Otherwise the flow interference pattern of the network will be too pessimistic,
 * yet, the results remain valid.
 *
 * @author Steffen Bondorf
 *
 */

public class Path {
    private Map<Integer, UnicastPath> id_to_path = new HashMap<>();
    private Set<UnicastPath> paths = new HashSet<>(); //list of unicast paths

    // is used to generate the unique Id of the serverPaths
    private int serverPathNum = 0;

    private Path() {}

    protected Path( List<Server> path_servers, List<Link> path_links ) {
        // Sanity check should have been done by the network
        this.paths.add(new UnicastPath(path_servers,path_links));
        id_to_path.put(0, paths.iterator().next());
    }

    protected Path( Path path ) {
        for(UnicastPath unicast_path : path.paths){
            UnicastPath new_unicast_path = new UnicastPath(unicast_path);
            paths.add(new_unicast_path);
            id_to_path.put(new_unicast_path.id, new_unicast_path);
        }
    }

    // Can be visible.
    // There's no way to create a single hop path not possible to take in a network.
    public Path( Server single_hop ) {
        this.paths.add(new UnicastPath(new LinkedList<>(Collections.singleton(single_hop)), new LinkedList<>()));
        this.id_to_path.put(this.paths.iterator().next().id, this.paths.iterator().next());
    }

    public static Path createEmptyPath() {
        return new Path();
    }

    // There is only a single source in a multicast flow
    public Server getSource(){
        return paths.iterator().next().getSource();
    }

    public boolean isSource(Server s) {
        return paths.iterator().next().getSource().equals(s);
    }

    public Server getSink(){
        return paths.iterator().next().getSink();
    }

    public Server getSink(int id) throws Exception{
        return getUnicastPath(id).getSink();
    }

    public Set<Server> getSinks(){
        Set<Server> sinks = new HashSet<>();
        for(UnicastPath unicast_path : paths){
            sinks.add(unicast_path.getSink());
        }
        return sinks;
    }

    public int numServers(){
        return getAllServers().size();
    }

    public int numServers(int id) throws Exception{
        return getUnicastPath(id).getServers().size();
    }

    public int numLinks(){
        return getAllLinks().size();
    }

    public LinkedList<Link> getLinks() {
        return new LinkedList<>( paths.iterator().next().getLinks() );
    }

    public LinkedList<Link> getLinks(int id) throws Exception{
        return new LinkedList<>(getUnicastPath(id).getLinks());
    }

    public Set<Link> getAllLinks(){
        Set<Link> allLinks = new HashSet<>();
        for(UnicastPath server_path : paths){
            allLinks.addAll(server_path.getLinks());
        }
        return allLinks;
    }

    public LinkedList<Server> getServers() {
        return new LinkedList<>( paths.iterator().next().getServers() );
    }

    public LinkedList<Server> getServers(int id) throws Exception{
        return new LinkedList<>(getUnicastPath(id).getServers());
    }

    public Set<Server> getAllServers(){
        Set<Server> allServers = new HashSet<>();
        for(UnicastPath server_path : paths){
            allServers.addAll(server_path.getServers());
        }
        return allServers;
    }

    /**
     * Gives a Path class that models one of the unicast paths
     * @param id of the unicast path
     * @return
     * @throws Exception
     */
    public Path asUnicastPath(int id) throws Exception{
        UnicastPath unicast_path = getUnicastPath(id);
        return new Path(unicast_path.getServers(),unicast_path.getLinks());

    }

    private UnicastPath getUnicastPath(int id) throws Exception{
        if(id_to_path.containsKey(id)){
            return id_to_path.get(id);
        }
        throw new Exception("No Unicast Path with id " + id + " was found");
    }

    /**
     * Returns the Ids for the unicast paths
     * @return
     */
    public Set<Integer> getIds(){
        return id_to_path.keySet();
    }

    /**
     * Return the respective unicast path id for a given sink
     * @param s
     * @return
     * @throws Exception
     */
    public int getIdFromSink(Server s) throws Exception {
        for(int id : getIds()){
            if(getSink(id).equals(s)){
                return id;
            }
        }
        throw new Exception("Server " + s + " is not present in the flow's paths");
    }

    /**
     * @param from Source, inclusive.
     * @param to   Sink, inclusive.
     * @return The subpath.
     * @throws Exception No subpath found; most probably an input parameter problem.
     */
    public Path getSubPath(Server from, Server to) throws Exception {
        return getSubPath(from, to, 0);
    }

    /**
     *
     * @param from Source, inclusive.
     * @param to Sink, inclusive.
     * @param id identifier of unicast path
     * @return The subpath.
     * @throws Exception No subpath found; most probably an input parameter problem.
     */
    public Path getSubPath( Server from, Server to, int id ) throws Exception {
        LinkedList<Server> server_path = getServers(id);
        // All other sanity check should have been passed when this object was created
        if( !server_path.contains( from ) ) {
            throw new Exception( "Cannot create a subpath if source is not in it." );
        }
        if( !server_path.contains( to ) ) {
            throw new Exception( "Cannot create a subpath if sink is not in it." );
        }

        if( from == to ) {
            return new Path( new LinkedList<Server>( Collections.singleton( from ) ) , new LinkedList<Link>() );
        }

        int from_index = server_path.indexOf( from );
        int to_index = server_path.indexOf( to );
        if ( from_index >= to_index ) {
            throw new Exception( "Cannot create sub-path from " + from.toString() + " to " + to.toString() );
        }
        // subList: 'from' is inclusive but 'to' is exclusive
        LinkedList<Server> subpath_servers = new LinkedList<Server>( server_path.subList( from_index, to_index ) );
        subpath_servers.add( to );

        List<Link> subpath_links = new LinkedList<Link>();
        if ( subpath_servers.size() > 1 ) {
            for ( Link l : getLinks(id) ) {
                Server src_l = l.getSource();
                Server snk_l = l.getDest();
                if ( subpath_servers.contains( src_l ) && subpath_servers.contains( snk_l ) ) {
                    subpath_links.add( l );
                }
            }
        }

        return new Path( subpath_servers, subpath_links );
    }


    public Link getPrecedingLink( Server s ) throws Exception {
        UnicastPath unicast_path = paths.iterator().next();
        for( Link l: unicast_path.getLinks() ) {
            if ( l.getDest().equals( s ) ) {
                return l;
            }
        }
        throw new Exception( "No preceding link on the path found" );
    }

    public Link getPrecedingLink( Server s, int id ) throws Exception {
        UnicastPath unicast_path = getUnicastPath(id);
        for( Link l: unicast_path.getLinks() ) {
            if ( l.getDest().equals( s ) ) {
                return l;
            }
        }
        throw new Exception( "No preceding link on the path found" );
    }

    /**
     * For unicast flows
     * @param s
     * @return
     * @throws Exception
     */
    public Link getSucceedingLink(Server s) throws Exception {
        return getSucceedingLink(s, 0);
    }

    public Link getSucceedingLink( Server s, int id ) throws Exception {
        UnicastPath unicast_path = getUnicastPath(id);
        for (Link l : unicast_path.getLinks()) {
            if (l.getSource().equals(s)) {
                return l;
            }
        }
        throw new Exception("No succeeding link on the path found");
    }

    public HashSet<Link> getSucceedingLinks( Server s ) throws Exception {
        HashSet<Link> succeeding_links = new HashSet<Link>();
        for( Link l: getAllLinks() ) {
            if ( l.getSource().equals( s ) ) {
                succeeding_links.add( l );
            }
        }
        if( succeeding_links.isEmpty() ) {
            throw new Exception( "No succeeding link on the path found" );
        } else {
            return succeeding_links;
        }
    }

    public Server getPrecedingServer( Server s ) throws Exception {
        try{
            return getPrecedingLink( s ).getSource();
        } catch ( Exception e ) {
            throw new Exception( "No preceding server on the path found" );
        }
    }

    public Server getPrecedingServer( Server s, int id ) throws Exception {
        try{
            return getPrecedingLink( s, id ).getSource();
        } catch ( Exception e ) {
            throw new Exception( "No preceding server on the path found" );
        }
    }

    public Server getSucceedingServer(Server s) throws Exception {
        try {
            return getSucceedingLink(s).getDest();
        } catch (Exception e) {
            throw new Exception("No succeeding server on the path found");
        }
    }

    public Server getSucceedingServer(Server s, int id) throws Exception {
        try {
            return getSucceedingLink(s, id).getDest();
        } catch (Exception e) {
            throw new Exception("No succeeding server on the path found");
        }
    }

    public Set<Server> getSucceedingServers(Server s) throws Exception {
        try {
            Set<Server> servers = new HashSet<>();
            for(Link l : getSucceedingLinks(s)){
                servers.add(l.getDest());
            }
            return servers;
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

    public ServiceCurve getServiceCurve(int id) throws Exception
    {
        Collection<Server> servers = getServers(id);
        return getServiceCurve( servers );
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

    public MaxServiceCurve getGamma(int id) throws Exception
    {
        Collection<Server> servers = getServers(id);
        return getGamma( servers );
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

    public MaxServiceCurve getExtraGamma(int id) throws Exception
    {
        Collection<Server> servers = getServers(id);
        return getExtraGamma( servers );
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
    public boolean equals( Object obj ) {
        if (obj == null || !(obj instanceof Path)) {
            return false;
        }

        Path p = (Path) obj;
        return paths.equals(p.paths);
    }

    @Override
    public int hashCode() {
        return paths.hashCode();
    }

    // Print path as series of servers (short representation)
    public String toShortString() {
        if (paths.isEmpty()) {
            return "{}";
        }

        String result_str = "{";
        for (Server s : getAllServers()) {
            result_str = result_str.concat(s.toShortString() + ",");
        }
        result_str = result_str.substring(0, result_str.length() - 1); // Remove the trailing comma.
        return result_str.concat("}");
    }

    @Override
    // Print path as series of links (short representation)
    public String toString() {
        if(this.paths.size() <= 1){
            if( getLinks().isEmpty() ) {
                return toShortString();
            }

            String result_str = "{";

            for( Link l : getLinks() ) {
                result_str = result_str.concat( l.toString() + "," );
            }
            result_str = result_str.substring( 0, result_str.length()-1 ); // Remove the trailing comma.

            return result_str.concat( "}" );
        }
        else {
            String result_str = "{";
            for(int id : this.id_to_path.keySet()){
                result_str = result_str.concat("|" + id + ":");
                try{
                    for( Link l : getLinks(id) ) {
                        result_str = result_str.concat( l.toString() + "," );
                    }
                    result_str = result_str.substring( 0, result_str.length()-1 ); // Remove the trailing comma.

                    result_str = result_str.concat( "|" );
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            return result_str.concat( "}" );

        }

    }

    // Print path as series of links (extended representation)
    public String toExtendedString() {
        if( getLinks().isEmpty() ) {
            return toShortString();
        }

        String result_str = "{";

        for( Link l : getLinks() ) {
            result_str = result_str.concat( l.toExtendedString() + "," );
        }
        result_str = result_str.substring( 0, result_str.length()-1 ); // Remove the trailing comma.

        return result_str.concat( "}" );
    }

    /**
     * Given a list of servers, returns if it is a valid unicast path to the multicast path or not
     * @param server_list
     * @return
     */
    public boolean isValidPath(List<Server> server_list){
        //A unicast path cannot be null or empty
        if(server_list == null || server_list.isEmpty()){
            return false;
        }

        for(UnicastPath unicast_path : paths){

            //All unicast paths share the same source
            if(!server_list.get(0).equals(unicast_path.getSource())){
                return false;
            }

            //One unicast path cannot contain the sink of another
            if(unicast_path.servers_path.contains(server_list.get(server_list.size() - 1))){
                return false;
            }

            //Get the smaller one as the size to use
            int size;
            if(server_list.size() < unicast_path.servers_path.size()){
                size = server_list.size();
            }
            else{
                size = unicast_path.servers_path.size();
            }

            //Check if the paths differ at some point
            boolean isDifferent = false;
            for(int i = 0; i < size; i++){
                if(!server_list.get(i).equals(unicast_path.servers_path.get(i))){
                    isDifferent = true;
                }
            }
            if(!isDifferent){
                return false;
            }
        }
        return true;

    }

    /**
     * Adds a new unicast path to a multicast path
     * @param server_list
     * @param link_list
     * @return
     */
    public boolean addUnicastPath(List<Server> server_list, List<Link> link_list){
        if(!isValidPath(server_list)){
            return false;
        }
        UnicastPath new_unicast_path = new UnicastPath(server_list,link_list);
        paths.add(new_unicast_path);
        id_to_path.put(new_unicast_path.getId(), new_unicast_path);

        return true;
    }

    //Obs: If a multicast path is used, some unicast paths may be added before a problem is found
    public boolean addUnicastPath(Path path){
        for(UnicastPath unicast_path : path.paths){
            if(!addUnicastPath(unicast_path.servers_path, unicast_path.links_path)){
                return false;
            }
        }
        return true;
    }


    /**
     * Helper class that represents a unicast path inside a multicast one
     */
    public class UnicastPath{
        private LinkedList<Server> servers_path;
        private LinkedList<Link> links_path;
        private int id;
        UnicastPath(List<Server> servers_path, List<Link> links_path){
            this.servers_path = new LinkedList<>(servers_path);
            this.links_path = new LinkedList<>(links_path);
            id = serverPathNum;
            serverPathNum++;
        }

        UnicastPath(UnicastPath old_unicast_path){
            this.servers_path = new LinkedList<>(old_unicast_path.getServers());
            this.links_path = new LinkedList<>(old_unicast_path.getLinks());
            id = serverPathNum;
            serverPathNum++;
        }

        public LinkedList<Server> getServers() {
            return servers_path;
        }

        LinkedList<Link> getLinks() {
            return links_path;
        }

        public Server getSource(){
            return servers_path.getFirst();
        }

        public Server getSink() { return servers_path.getLast(); }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof UnicastPath)) {
                return false;
            }

            UnicastPath up = (UnicastPath) obj;
            return servers_path.equals(up.servers_path);
        }

        @Override
        public int hashCode() {
            return servers_path.hashCode();
        }
    }

}