package org.networkcalculus.dnc.network.device_graph;


import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.network.device_graph.acyclic_routing_strategy.AcyclicRoutingStrategy;
import org.networkcalculus.dnc.network.device_graph.path_routing_strategy.PathRoutingStrategy;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

public class DeviceGraph {
    private Set<Device> devices;
    private Set<Link> links;
    private Set<TrafficDemand> trafficDemands;
    private Map<Device, Set<Link>> map__device__links;

    private String device_default_name_prefix = "d";
    private int device_id_counter = 0;
    private Map<Integer, Device> map__id__device;

    private String link_default_name_prefix = "l";
    private int link_id_counter = 0;
    private Map<Integer, Link> map__id__link;

    private String traffic_demand_default_name_prefix = "td";
    private int traffic_demand_id_counter = 0;
    private Map<Integer, TrafficDemand> map__id__traffic_demand;

    public DeviceGraph() {
        devices = new TreeSet<>(Comparator.comparingInt(Device::getId));
        links = new TreeSet<>(Comparator.comparingInt(Link::getId));
        trafficDemands = new TreeSet<>(Comparator.comparingInt(TrafficDemand::getId));
        map__device__links = new TreeMap<>(Comparator.comparingInt(Device::getId));
        map__id__device = new TreeMap<>();
        map__id__link = new TreeMap<>();
        map__id__traffic_demand = new TreeMap<>();
    }

    // --------------------------------------------------------------------------------------------
    // Devices
    // --------------------------------------------------------------------------------------------
    public Device addDevice() {
        return addDevice(device_default_name_prefix + device_id_counter);
    }

    public Device addDevice(String alias) {
        Device device = new Device(device_id_counter++, alias);
        devices.add(device);
        map__device__links.put(device, new TreeSet<>(Comparator.comparingInt(Link::getId)));
        map__id__device.put(device.getId(), device);
        return device;
    }

    public void removeDevice(Device device) {
        devices.remove(device);
        Iterator<Link> iterator = map__device__links.get(device).iterator();
        while (iterator.hasNext()) {
            Link link = iterator.next();
            iterator.remove();
            removeLink(link);
        }
        map__device__links.remove(device);
        map__id__device.remove(device.getId());
    }

    public Set<Device> getDevices() {
        return devices;
    }

    public Device getDevice(int id) {
        return map__id__device.get(id);
    }

    public int degree(Device d) {
        return map__device__links.get(d).size();
    }

    public Device getNeighbor(Device d, Link l) {
        if (l.getDevice1().equals(d)) {
            return l.getDevice2();
        } else if (l.getDevice2().equals(d)) {
            return l.getDevice1();
        } else {
            return null;
        }
    }

    public List<Device> getNeighbours(Device d) {
        List<Device> neighbors = new ArrayList<>();
        for (Link l : map__device__links.get(d)) {
            neighbors.add(getNeighbor(d, l));
        }
        neighbors.sort(Comparator.comparingInt(Device::getId));
        return neighbors;
    }

    // --------------------------------------------------------------------------------------------
    // Links
    // --------------------------------------------------------------------------------------------
    public Link addLink(Device device1, Device device2, Curve serviceCurve) {
        return addLink(link_default_name_prefix + link_id_counter, device1, device2, serviceCurve);
    }

    public Link addLink(String alias, Device device1, Device device2, Curve serviceCurve) {
        Link link = new Link(link_id_counter++, alias, device1, device2, serviceCurve);
        links.add(link);
        map__device__links.get(device1).add(link);
        map__device__links.get(device2).add(link);
        map__id__link.put(link.getId(), link);
        return link;
    }

    public void removeLink(Link link) {
        map__device__links.get(link.getDevice1()).remove(link);
        map__device__links.get(link.getDevice2()).remove(link);
        links.remove(link);
        map__id__link.remove(link.getId());
    }

    public Set<Link> getLinks() {
        return links;
    }

    public Set<Link> getLinks(Device device) {
        return map__device__links.get(device);
    }

    public Link getLink(int id) {
        return map__id__link.get(id);
    }

    public Link getLink(Device device1, Device device2) {
        for (Link l : map__device__links.get(device1)) {
            if (l.getDevice1().equals(device2) || l.getDevice2().equals(device2)) {
                return l;
            }
        }
        throw new IllegalArgumentException("No link between " + device1.getAlias() + " and " + device2.getAlias());
    }

    // --------------------------------------------------------------------------------------------
    // Traffic Demands
    // --------------------------------------------------------------------------------------------
    public TrafficDemand addTrafficDemand(Device src, Device dest, Curve arrivalCurve) {
        return addTrafficDemand(traffic_demand_default_name_prefix + traffic_demand_id_counter, src, dest, arrivalCurve);
    }

    public Set<TrafficDemand> getTrafficDemands() {
        return trafficDemands;
    }

    public TrafficDemand addTrafficDemand(String alias, Device src, Device dest, Curve arrivalCurve) {
        TrafficDemand trafficDemand = new TrafficDemand(traffic_demand_id_counter++, alias, src, dest, arrivalCurve);
        trafficDemands.add(trafficDemand);
        map__id__traffic_demand.put(trafficDemand.getId(), trafficDemand);
        return trafficDemand;
    }

    // --------------------------------------------------------------------------------------------
    // Utility
    // --------------------------------------------------------------------------------------------

    public ServerGraph convertToServerGraph(AcyclicRoutingStrategy acyclicRoutingStrategy, PathRoutingStrategy pathRoutingStrategy) {
        try {
            return acyclicRoutingStrategy.convertToServerGraph(this, pathRoutingStrategy);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DeviceGraph copy() {
        DeviceGraph copy = new DeviceGraph();
        copy.devices.addAll(devices);
        copy.links.addAll(links);
        copy.trafficDemands.addAll(trafficDemands);
        for (Device d : devices) {
            copy.map__device__links.put(d, new TreeSet<>(Comparator.comparingInt(Link::getId)));
            copy.map__device__links.get(d).addAll(map__device__links.get(d));
        }
        copy.map__id__device.putAll(map__id__device);
        copy.map__id__link.putAll(map__id__link);
        copy.map__id__traffic_demand.putAll(map__id__traffic_demand);
        copy.device_id_counter = device_id_counter;
        copy.link_id_counter = link_id_counter;
        copy.traffic_demand_id_counter = traffic_demand_id_counter;

        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Devices:\n");
        for (Device d : devices) {
            sb.append(d.getAlias()).append("\n");
        }
        sb.append("Links:\n");
        for (Link l : links) {
            sb.append(l.getDevice1().getAlias()).append(" <-> ").append(l.getDevice2().getAlias()).append("\n");
        }
        sb.append("Traffic Demands:\n");
        for (TrafficDemand td : trafficDemands) {
            sb.append(td.getAlias()).append("\n");
        }
        return sb.toString();
    }


    public static DeviceGraph getRandomDeviceGraph(int size, int seed, int degree) {
        Curve serviceCurve = CurveFactory.createRateLatency(1000, 0);
        Curve arrivalCurve = CurveFactory.createTokenBucket(1, 100000);

        return getRandomDeviceGraph(size, seed, degree, serviceCurve, arrivalCurve);
    }

    public static DeviceGraph getRandomDeviceGraph(int size, int seed, int degree, Curve serviceCurve, Curve arrivalCurve) {
        return getRandomDeviceGraph(size, seed, degree, List.of(serviceCurve), List.of(arrivalCurve));
    }

    /**
     * Randomly generates a device graph with the given parameters and a traffic demand between each pair of devices.
     * Throws a RuntimeException if the graph is not connected.
     *
     * @param size           The number of devices in the graph
     * @param seed           The seed for the random number generator
     * @param degree         The degree of each device in the graph
     * @param service_curves The service curves for the links in the graph
     * @param arrivalCurves  The arrival curves for the traffic demands in the graph
     * @return A randomly generated device graph
     */
    public static DeviceGraph getRandomDeviceGraph(int size, int seed, int degree, List<Curve> service_curves, List<Curve> arrivalCurves) {
        DeviceGraph device_graph = new DeviceGraph();

        // Add Devices

        List<Device> devices = new LinkedList<>();

        for (int i = 1; i <= size; i++) {
            Device d = device_graph.addDevice("d" + i);
            devices.add(d);
        }

        // Add Links
        List<Device> degreeLessThanWanted = new LinkedList<>(devices);
        Random random = new Random(seed);
        Random randomWeights = new Random(seed);

        while (!degreeLessThanWanted.isEmpty()) {
            Device d = degreeLessThanWanted.remove((int) (random.nextInt(degreeLessThanWanted.size())));
            while (device_graph.degree(d) < degree) {
                boolean degreeLessThanWantedContainsOnlyNeighbors = false;
                if (degreeLessThanWanted.size() < degree) {
                    degreeLessThanWantedContainsOnlyNeighbors = true;
                    for (Device LTF : degreeLessThanWanted) {
                        if (!device_graph.getNeighbours(d).contains(LTF)) {
                            degreeLessThanWantedContainsOnlyNeighbors = false;
                            break;
                        }
                    }
                }
                if (!degreeLessThanWanted.isEmpty() && !degreeLessThanWantedContainsOnlyNeighbors) {
                    Device neighbor = degreeLessThanWanted.get((int) (random.nextInt(degreeLessThanWanted.size())));
                    if (neighbor != d && device_graph.degree(neighbor) < degree && !device_graph.getNeighbours(d).contains(neighbor)) {
                        device_graph.addLink("l" + device_graph.getLinks().size(), d, neighbor, service_curves.get(randomWeights.nextInt(service_curves.size())));
                        if (device_graph.degree(neighbor) == degree) {
                            degreeLessThanWanted.remove(neighbor);
                        }
                    }
                } else {
                    if (device_graph.degree(d) == 0) {
                        Device neighbor2 = devices.get((int) (random.nextInt(devices.size())));
                        while (neighbor2 == d || device_graph.getNeighbours(neighbor2).isEmpty()) {
                            neighbor2 = devices.get((int) (random.nextInt(devices.size())));
                        }
                        List<Device> neighbors = device_graph.getNeighbours(neighbor2);
                        Device neighbor3 = neighbors.get((int) (random.nextInt(neighbors.size())));
                        Link link = device_graph.getLink(neighbor2, neighbor3);
                        device_graph.removeLink(link);
                        device_graph.addLink("l" + device_graph.getLinks().size(), d, neighbor2, service_curves.get(randomWeights.nextInt(service_curves.size())));
                        device_graph.addLink("l" + device_graph.getLinks().size(), d, neighbor3, service_curves.get(randomWeights.nextInt(service_curves.size())));
                    }
                    if (device_graph.degree(d) > 0 && device_graph.degree(d) < degree) {
                        Device neighbor2 = devices.get((int) (random.nextInt(devices.size())));
                        while (neighbor2 == d || device_graph.getNeighbours(d).contains(neighbor2) || device_graph.getNeighbours(neighbor2).isEmpty()) {
                            neighbor2 = devices.get((int) (random.nextInt(devices.size())));
                        }
                        List<Device> neighbors = device_graph.getNeighbours(neighbor2);
                        Device neighbor3 = neighbors.get((int) (random.nextInt(neighbors.size())));
                        while (neighbor3 == d || device_graph.getNeighbours(d).contains(neighbor3) || device_graph.getNeighbours(neighbor2).isEmpty()) {
                            neighbor3 = neighbors.get((int) (random.nextInt(neighbors.size())));
                        }
                        Link link = device_graph.getLink(neighbor2, neighbor3);
                        device_graph.removeLink(link);
                        device_graph.addLink("l" + device_graph.getLinks().size(), d, neighbor2, service_curves.get(randomWeights.nextInt(service_curves.size())));
                        degreeLessThanWanted.add(neighbor3);
                    }
                }
            }
        }

        // Check if the graph is connected with bfs
        Device root = devices.get(0);

        Queue<Device> queue = new LinkedList<>();
        Set<Device> visitedDevices = new HashSet<>();

        queue.add(root);
        visitedDevices.add(root);

        while (!queue.isEmpty()) {
            Device currentDevice = queue.poll();
            for (Device neighbour : device_graph.getNeighbours(currentDevice)) {
                if (!visitedDevices.contains(neighbour)) {
                    visitedDevices.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        if (visitedDevices.size() != size) {
            throw new RuntimeException("Graph is not connected");
        }

        for (Device src : devices) {
            for (Device dst : devices) {
                if (src != dst) {
                    Curve ac = arrivalCurves.get(randomWeights.nextInt(arrivalCurves.size()));
                    if (ac.getSequence().getSegments().size() == 1 && ac.getSegment(0).getRightLimitAtStartTime().eq(Number.ZERO) && ac.getSegment(0).getSlope().eq(Number.ZERO)) {
                        continue;
                    }
                    device_graph.addTrafficDemand("td_" + src.getAlias() + "_" + dst.getAlias(), src, dst, ac);
                }
            }
        }

        return device_graph;
    }
}
