package org.networkcalculus.dnc.network.device_graph.acyclic_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.Link;
import org.networkcalculus.dnc.network.device_graph.path_routing_strategy.PathRoutingStrategy;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;

import java.util.*;

public class NoRouting implements AcyclicRoutingStrategy {

    public NoRouting(){}

    public ServerGraph convertToServerGraph(DeviceGraph deviceGraph, PathRoutingStrategy pathRoutingStrategy) {
        ServerGraph serverGraph = new ServerGraph();

        Set<Server> servers = new TreeSet<>(Comparator.comparingInt(Server::getId));
        Map<Device, Set<Server>> map__device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Device, Set<Server>> map__destination_device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Server, Device> map__server__destination_device = new TreeMap<>(Comparator.comparingInt(Server::getId));
        Map<Link, Set<Server>> map__link__servers = new TreeMap<>(Comparator.comparingInt(Link::getId));
        Map<Server, Link> map__server__link = new TreeMap<>(Comparator.comparingInt(Server::getId));

        for(Device device : deviceGraph.getDevices()){
            map__device__servers.put(device, new TreeSet<>(Comparator.comparingInt(Server::getId)));
            for(Link link : deviceGraph.getLinks(device)){
                String alias = "l_" + device.getAlias() + "_" + deviceGraph.getNeighbor(device, link).getAlias();
                Server server = serverGraph.addServer(alias, link.getServiceCurve());
                servers.add(server);
                map__device__servers.get(device).add(server);
                map__destination_device__servers.putIfAbsent(deviceGraph.getNeighbor(device, link), new TreeSet<>(Comparator.comparingInt(Server::getId)));
                map__destination_device__servers.get(deviceGraph.getNeighbor(device, link)).add(server);
                map__server__destination_device.put(server, deviceGraph.getNeighbor(device, link));
                map__link__servers.putIfAbsent(link, new TreeSet<>(Comparator.comparingInt(Server::getId)));
                map__link__servers.get(link).add(server);
                map__server__link.put(server, link);
            }
        }

        for(Server s: servers){
            Link l = map__server__link.get(s);
            Device n = map__server__destination_device.get(s);
            for(Server s2: map__device__servers.get(n)){
                if(!map__link__servers.get(l).contains(s2)){
                    try {
                        serverGraph.addTurn(s, s2);
                    } catch (Exception e) {
                        System.out.println("Error while adding turn");
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            return pathRoutingStrategy.routeTrafficDemands(serverGraph, deviceGraph, map__device__servers, map__destination_device__servers);
        } catch (Exception e) {
            System.out.println("Error during PathRouting");
            e.printStackTrace();
        }

        return serverGraph;
    }
}
