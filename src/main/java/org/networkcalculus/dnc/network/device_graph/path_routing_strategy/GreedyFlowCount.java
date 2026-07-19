package org.networkcalculus.dnc.network.device_graph.path_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.TrafficDemand;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GreedyFlowCount implements PathRoutingStrategy{

    private int seed = 0;
    private int maxDeviation = Integer.MAX_VALUE;

    public GreedyFlowCount(){}

    public GreedyFlowCount(int seed){
        this.seed = seed;
    }

    public GreedyFlowCount(int seed, int maxDeviation){
        this.seed = seed;
        this.maxDeviation = maxDeviation;
    }

    public ServerGraph routeTrafficDemands(ServerGraph serverGraph,
                                           DeviceGraph deviceGraph,
                                           Map<Device, Set<Server>> map__device__servers,
                                           Map<Device, Set<Server>> map__destination_device__servers) throws Exception {

        Map<TrafficDemand, Set<Path>> map__traffic_demand__paths = new ConcurrentHashMap<>();
        List<Future<Void>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (TrafficDemand trafficDemand : deviceGraph.getTrafficDemands()) {
            futures.add(executor.submit(() -> {
                Set<Path> paths = getAllPaths(serverGraph, map__device__servers.get(trafficDemand.getSrc()), map__destination_device__servers.get(trafficDemand.getDest()), deviceGraph.getDevices().size());

                if (maxDeviation < Integer.MAX_VALUE) {
                    int lengthShortestPath = Integer.MAX_VALUE;
                    for (Path path : paths) {
                        if (path.getServers().size() < lengthShortestPath) {
                            lengthShortestPath = path.getServers().size();
                        }
                    }
                    Set<Path> pathsToRemove = new HashSet<>();
                    for (Path path : paths) {
                        if (path.getServers().size() > lengthShortestPath + maxDeviation) {
                            pathsToRemove.add(path);
                        }
                    }
                    paths.removeAll(pathsToRemove);
                }
                map__traffic_demand__paths.put(trafficDemand, paths);
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();

        Random random = new Random(seed);

        List<TrafficDemand> trafficDemands = new ArrayList<>(deviceGraph.getTrafficDemands());
        Collections.shuffle(trafficDemands, random);

        for(TrafficDemand trafficDemand : trafficDemands) {

            Set<Path> paths_set = map__traffic_demand__paths.get(trafficDemand);
            Set<Path> paths = new TreeSet<>(Comparator.comparingInt(Path::hashCode));
            paths.addAll(paths_set);

            if (paths.isEmpty()) {
                throw new Exception("No path found for traffic demand " + trafficDemand.getAlias());
            }

            Map<Path, Integer> pathFlowCount = new ConcurrentHashMap<>();
            futures = new ArrayList<>();
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (Path path : paths) {
                futures.add(executor.submit(() -> {
                    int maxFlowCount = 0;
                    for(Server server : path.getServers()) {
                        if (serverGraph.getFlows(server).size() > maxFlowCount) {
                            maxFlowCount = serverGraph.getFlows(server).size();
                        }
                    }
                    pathFlowCount.put(path, maxFlowCount);
                    return null;
                }));
            }

            for (Future<Void> future : futures) {
                future.get();
            }

            executor.shutdown();

            Path bestPath = null;
            int minFlowBound = Integer.MAX_VALUE;

            for(Path path : paths) {
                if(pathFlowCount.get(path) < minFlowBound || (pathFlowCount.get(path) == minFlowBound && path.getServers().size() < bestPath.getServers().size())) {
                    minFlowBound = pathFlowCount.get(path);
                    bestPath = path;
                }
            }

            serverGraph.addFlow(trafficDemand.getAlias(), trafficDemand.getArrivalCurve(), bestPath);
        }

        return serverGraph;
    }

    private Set<Path> getAllPaths(ServerGraph serverGraph, Set<Server> source, Set<Server> destination, int deviceCount) throws Exception {
        Set<Path> paths = new TreeSet<>(Comparator.comparingInt(Path::hashCode));

        int shortestPathLength = Integer.MAX_VALUE - maxDeviation;

        for (Server src : source) {
            LinkedList<Server> stack = new LinkedList<>();
            Map<Server, Set<Server>> visitedNeighbors = new HashMap<>();

            stack.add(src);

            while (!stack.isEmpty()) {
                Server current = stack.getLast();
                visitedNeighbors.putIfAbsent(current, new HashSet<>());
                Server next = null;

                if (destination.contains(current)) {
                    paths.add(serverGraph.createPathFromServers(stack));
                    if (stack.size() < shortestPathLength) {
                        shortestPathLength = stack.size();
                    }
                    stack.remove(current);
                    continue;
                }

                for (Server neighbor : serverGraph.getSuccessors(current)) {
                    if (!stack.contains(neighbor) && !visitedNeighbors.get(current).contains(neighbor)
                            && stack.size() < deviceCount && stack.size() <= (shortestPathLength + maxDeviation)) {
                        next = neighbor;
                        visitedNeighbors.get(current).add(next);
                        stack.add(next);
                        break;
                    }
                }
                if (next == null) {
                    stack.remove(current);
                    visitedNeighbors.remove(current);
                }
            }
        }
        return paths;
    }
}
