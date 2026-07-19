package org.networkcalculus.dnc.network.device_graph.acyclic_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.Link;
import org.networkcalculus.dnc.network.device_graph.path_routing_strategy.PathRoutingStrategy;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import java.util.*;

// --------------------------------------------------------------------------------------------
// Based on breadth-first search, root being the device with the highest degree
// (device with the lowest id as tie-breaker)
// --------------------------------------------------------------------------------------------

public class UpDownRouting implements AcyclicRoutingStrategy {

    public UpDownRouting(){}

    public ServerGraph convertToServerGraph(DeviceGraph deviceGraph, PathRoutingStrategy pathRoutingStrategy) {
        // --------------------------------------------------------------------------------------------
        // Determine the root of the spanning tree
        // --------------------------------------------------------------------------------------------
        Device root = deviceGraph.getDevice(0);
        int rootDegree = deviceGraph.degree(root);
        int rootId = root.getId();

        for (Device device : deviceGraph.getDevices()) {
            if (deviceGraph.degree(device) > rootDegree || (deviceGraph.degree(device) == rootDegree && device.getId() < rootId)) {
                root = device;
                rootId = device.getId();
                rootDegree = deviceGraph.degree(device);
            }
        }

        // --------------------------------------------------------------------------------------------
        // Mark nodes depending on their distance to the root
        // --------------------------------------------------------------------------------------------
        Map<Device, Integer> distance = new HashMap<>();
        Queue<Device> queue = new LinkedList<>();
        Set<Device> visitedDevices = new HashSet<>();
        Map<Device, Set<Device>> neighboursUp = new HashMap<>();
        Map<Device, Set<Device>> neighboursDown = new HashMap<>();

        queue.add(root);
        visitedDevices.add(root);
        distance.put(root, 0);

        while(!queue.isEmpty()) {
            Device currentDevice = queue.poll();
            neighboursUp.putIfAbsent(currentDevice, new HashSet<>());
            neighboursDown.putIfAbsent(currentDevice, new HashSet<>());
            for (Device neighbour : deviceGraph.getNeighbours(currentDevice)) {
                if (!visitedDevices.contains(neighbour)) {
                    queue.add(neighbour);
                    distance.put(neighbour, distance.get(currentDevice) + 1);
                    visitedDevices.add(neighbour);
                }
                if (distance.get(currentDevice) > distance.get(neighbour) || distance.get(currentDevice).equals(distance.get(neighbour)) && currentDevice.getId() > neighbour.getId()) {
                    neighboursUp.get(currentDevice).add(neighbour);
                    neighboursDown.putIfAbsent(neighbour, new HashSet<>());
                    neighboursDown.get(neighbour).add(currentDevice);
                } else if(distance.get(currentDevice) < distance.get(neighbour) || distance.get(currentDevice).equals(distance.get(neighbour)) && currentDevice.getId() < neighbour.getId()) {
                    neighboursDown.get(currentDevice).add(neighbour);
                    neighboursUp.putIfAbsent(neighbour, new HashSet<>());
                    neighboursUp.get(neighbour).add(currentDevice);
                }
            }
        }

        if (visitedDevices.size() != deviceGraph.getDevices().size()) {
            throw new RuntimeException("DeviceGraph is not connected");
        }

        // --------------------------------------------------------------------------------------------
        // Conversion to ServerGraph
        // --------------------------------------------------------------------------------------------
        ServerGraph serverGraph = new ServerGraph();

        Set<Server> servers = new TreeSet<>(Comparator.comparingInt(Server::getId));
        Map<Device, Set<Server>> map__device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Device, Set<Server>> map__destination_device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Server, Device> map__server__destination_device = new TreeMap<>(Comparator.comparingInt(Server::getId));
        Map<Link, Set<Server>> map__link__servers = new TreeMap<>(Comparator.comparingInt(Link::getId));
        Map<Server, Link> map__server__link = new TreeMap<>(Comparator.comparingInt(Server::getId));
        Map<Server, Device> map__server__device = new TreeMap<>(Comparator.comparingInt(Server::getId));

        for(Device device : deviceGraph.getDevices()){
            map__device__servers.put(device, new TreeSet<>(Comparator.comparingInt(Server::getId)));
            for(Link link : deviceGraph.getLinks(device)){
                String alias = "l_" + device.getAlias() + "_" + deviceGraph.getNeighbor(device, link).getAlias();
                Server server = serverGraph.addServer(alias, link.getServiceCurve());
                servers.add(server);
                map__device__servers.get(device).add(server);
                map__destination_device__servers.putIfAbsent(deviceGraph.getNeighbor(device, link), new TreeSet<>(Comparator.comparingInt(Server::getId)));
                map__destination_device__servers.get(deviceGraph.getNeighbor(device, link)).add(server);
                map__link__servers.putIfAbsent(link, new TreeSet<>(Comparator.comparingInt(Server::getId)));
                map__link__servers.get(link).add(server);
                map__server__destination_device.put(server, deviceGraph.getNeighbor(device, link));
                map__server__link.put(server, link);
                map__server__device.put(server, device);
            }
        }

        for(Server s: servers){
            Link l = map__server__link.get(s);
            Device d = map__server__device.get(s);
            Device n = map__server__destination_device.get(s);
            for(Server s2: map__device__servers.get(n)){
                if(map__link__servers.get(l).contains(s2)){
                    continue;
                }
                // Up/Up
                if(neighboursUp.get(d).contains(n) && neighboursUp.get(n).contains(map__server__destination_device.get(s2))){
                    try {
                        serverGraph.addTurn(s, s2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                // Up/Down
                else if(neighboursUp.get(d).contains(n) && neighboursDown.get(n).contains(map__server__destination_device.get(s2))){
                    try {
                        serverGraph.addTurn(s, s2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Down/Down
                else if(neighboursDown.get(d).contains(n) && neighboursDown.get(n).contains(map__server__destination_device.get(s2))){
                    try {
                        serverGraph.addTurn(s, s2);
                    } catch (Exception e) {
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
