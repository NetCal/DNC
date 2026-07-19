package org.networkcalculus.dnc.network.device_graph.acyclic_routing_strategy;

import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.Link;
import org.networkcalculus.dnc.network.device_graph.path_routing_strategy.PathRoutingStrategy;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

public class TurnProhibitionWeighted implements AcyclicRoutingStrategy {
    private static Set<List<Device>> prohibitedTurns;
    private static Set<List<Device>> permittedTurns;
    private static Set<Device> specialDevices;
    private static DeviceGraph deviceGraph;
    private static Number weightXServiceCurve;

    public TurnProhibitionWeighted() {
    }

    public ServerGraph convertToServerGraph(DeviceGraph deviceGraph, PathRoutingStrategy pathRoutingStrategy) {

        prohibitedTurns = new HashSet<>();
        permittedTurns = new HashSet<>();
        specialDevices = new HashSet<>();
        TurnProhibitionWeighted.deviceGraph = deviceGraph.copy();

        weightXServiceCurve = Number.ZERO;

        for (Link link : deviceGraph.getLinks()) {
            Curve serviceCurve = link.getServiceCurve();
            if (serviceCurve.getSegment(serviceCurve.getBaseSequence().getSegments().size() - 1).getStartTime().gt(weightXServiceCurve)) {
                weightXServiceCurve = serviceCurve.getSegment(serviceCurve.getBaseSequence().getSegments().size() - 1).getStartTime();
            }
        }

        if (weightXServiceCurve.eq(Number.ZERO)) {
            weightXServiceCurve = new Number(5);
        } else {
            weightXServiceCurve = weightXServiceCurve.add(weightXServiceCurve);
        }

        recursiveTP(deviceGraph.copy());

        ServerGraph serverGraph = new ServerGraph();

        Set<Server> servers = new TreeSet<>(Comparator.comparingInt(Server::getId));
        Map<Device, Set<Server>> map__device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Device, Set<Server>> map__destination_device__servers = new TreeMap<>(Comparator.comparingInt(Device::getId));
        Map<Server, Device> map__server__destination_device = new TreeMap<>(Comparator.comparingInt(Server::getId));
        Map<Link, Set<Server>> map__link__servers = new TreeMap<>(Comparator.comparingInt(Link::getId));
        Map<Server, Link> map__server__link = new TreeMap<>(Comparator.comparingInt(Server::getId));
        Map<Server, Device> map__server__device = new TreeMap<>(Comparator.comparingInt(Server::getId));

        for (Device device : deviceGraph.getDevices()) {
            map__device__servers.put(device, new TreeSet<>(Comparator.comparingInt(Server::getId)));
            for (Link link : deviceGraph.getLinks(device)) {
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

        for (Server s : servers) {
            Link l = map__server__link.get(s);
            Device n = map__server__destination_device.get(s);
            for (Server s2 : map__device__servers.get(n)) {
                if (map__link__servers.get(l).contains(s2)) {
                    continue;
                }
                if (prohibitedTurns.contains(List.of(map__server__device.get(s), n, map__server__destination_device.get(s2)))) {
                    continue;
                }
                try {
                    serverGraph.addTurn(s, s2);
                } catch (Exception e) {
                    e.printStackTrace();
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

    private static void recursiveTP(DeviceGraph component) {
        if (component.getDevices().size() <= 1) {
            return;
        }

        Number minWeight = Number.POSITIVE_INFINITY;
        int minDegree = Integer.MAX_VALUE;
        Device selectedDevice = null;
        Device specialDevice = null;

        for (Device device : component.getDevices()) {
            Number weight = Number.ZERO;
            for (Link link : component.getLinks(device)) {
                weight = weight.add(link.getServiceCurve().getValue(weightXServiceCurve));
            }
            if (specialDevices.contains(device)) {
                specialDevice = device;
                continue;
            }
            if (weight.lt(minWeight)) {
                minWeight = weight;
                minDegree = component.degree(device);
                selectedDevice = device;
            } else if (weight == minWeight && component.degree(device) < minDegree) {
                selectedDevice = device;
                minDegree = component.degree(device);
            } else if (weight == minWeight && component.degree(device) == minDegree && device.getId() < selectedDevice.getId()) {
                selectedDevice = device;
            }
        }

        for (Device neighbour1 : component.getNeighbours(selectedDevice)) {
            for (Device neighbour2 : component.getNeighbours(selectedDevice)) {
                if (neighbour1.equals(neighbour2)) {
                    continue;
                }
                prohibitedTurns.add(List.of(neighbour1, selectedDevice, neighbour2));
            }
        }

        for (Device firstNeighbour : component.getNeighbours(selectedDevice)) {
            for (Device secondNeighbour : component.getNeighbours(firstNeighbour)) {
                if (secondNeighbour.equals(selectedDevice)) {
                    continue;
                }
                permittedTurns.add(List.of(selectedDevice, firstNeighbour, secondNeighbour));
                permittedTurns.add(List.of(secondNeighbour, firstNeighbour, selectedDevice));
            }
        }

        DeviceGraph newComponent = component.copy();
        newComponent.removeDevice(selectedDevice);
        Set<DeviceGraph> components = getComponents(newComponent);

        // Handle disconnected components
        if (components.size() > 1) {
            for (DeviceGraph disconnectedComponent : components) {
                if (specialDevice != null) {
                    if (disconnectedComponent.getDevices().contains(specialDevice)) {
                        continue;
                    }
                }

                Device specialLink = findSpecialDevice(selectedDevice, disconnectedComponent);

                specialDevices.add(specialLink);

                for (DeviceGraph otherDisconnectedComponent : components) {
                    if (!disconnectedComponent.equals(otherDisconnectedComponent)) {
                        Device otherSpecialLink = findSpecialDevice(selectedDevice, otherDisconnectedComponent);
                        if (otherSpecialLink != null) {
                            Iterator<List<Device>> iterator = prohibitedTurns.iterator();
                            while (iterator.hasNext()) {
                                List<Device> turn = iterator.next();
                                if (turn.get(0).equals(specialLink) && turn.get(1).equals(selectedDevice) && turn.get(2).equals(otherSpecialLink)) {
                                    iterator.remove();
                                    permittedTurns.add(turn);
                                }
                                if (turn.get(0).equals(otherSpecialLink) && turn.get(1).equals(selectedDevice) && turn.get(2).equals(specialLink)) {
                                    iterator.remove();
                                    permittedTurns.add(turn);
                                }
                            }
                        }
                    }
                }
            }
        }

        for (DeviceGraph c : components) {
            recursiveTP(c);
        }
    }

    private static Set<DeviceGraph> getComponents(DeviceGraph newComponent) {
        Set<DeviceGraph> components = new HashSet<>();
        Set<Device> notVisited = new HashSet<>(newComponent.getDevices());

        while (!notVisited.isEmpty()) {
            Device start = notVisited.iterator().next();
            Set<Device> componentDevices = new HashSet<>();

            Queue<Device> queue = new LinkedList<>();
            queue.add(start);
            notVisited.remove(start);
            componentDevices.add(start);

            while (!queue.isEmpty()) {
                Device current = queue.poll();
                for (Device neighbour : newComponent.getNeighbours(current)) {
                    if (notVisited.contains(neighbour)) {
                        queue.add(neighbour);
                        notVisited.remove(neighbour);
                        componentDevices.add(neighbour);
                    }
                }
            }
            components.add(deviceListToComponent(componentDevices, newComponent));
        }
        return components;
    }

    private static DeviceGraph deviceListToComponent(Set<Device> devices, DeviceGraph component) {
        DeviceGraph newComponent = component.copy();
        for (Device device : component.getDevices()) {
            if (!devices.contains(device)) {
                newComponent.removeDevice(device);
            }
        }
        return newComponent;
    }

    private static Device findSpecialDevice(Device selectedDevice, DeviceGraph disconnectedComponent) {
        for (Device neighbour : deviceGraph.getNeighbours(selectedDevice)) {
            if (disconnectedComponent.getDevices().contains(neighbour)) {
                return neighbour;
            }
        }
        return null;
    }
}
