package org.networkcalculus.dnc.network.device_graph.path_routing_strategy;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.backend.number.Number;
import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.TrafficDemand;
import org.networkcalculus.dnc.network.server_graph.*;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.tandem.TandemAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.num.Num;

import java.util.*;
import java.util.concurrent.*;

public class GreedySFA implements PathRoutingStrategy {

    private int seed = 0;
    private int maxDeviation = Integer.MAX_VALUE;

    public GreedySFA() {
    }

    public GreedySFA(int seed) {
        this.seed = seed;
    }

    public GreedySFA(int seed, int maxDeviation) {
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

        AnalysisConfig config = new AnalysisConfig();
        config.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_ARBITRARY);

        while (!trafficDemands.isEmpty()) {

            TrafficDemand trafficDemand = trafficDemands.iterator().next();
            trafficDemands.remove(trafficDemand);

            Set<Path> paths_set = map__traffic_demand__paths.get(trafficDemand);
            List<Path> paths = new ArrayList<>(paths_set);

            if (paths.isEmpty()) {
                throw new Exception("No path found for traffic demand " + trafficDemand.getAlias());
            }

            Path bestPath = null;
            Number bestDelay = Number.POSITIVE_INFINITY;

            while (!paths.isEmpty()) {
                System.out.print("TrafficDemands left: " + trafficDemands.size() + " Paths left: " + paths.size() + "\r");
                Path path = paths.get(0);
                paths.remove(path);

                Flow tempFlow = serverGraph.addFlow(trafficDemand.getAlias(), trafficDemand.getArrivalCurve(), path);

                SeparateFlowAnalysis flowAnalysis = TandemAnalysis.performSfaEnd2End(serverGraph, config, tempFlow);

                Number delay = flowAnalysis.getDelayBound();

                if (delay.lt(bestDelay) || (delay.eq(bestDelay) && path.getServers().size() < bestPath.getServers().size())) {
                    bestDelay = delay;
                    bestPath = path;
                }

                serverGraph.removeFlow(tempFlow);
            }

            if (bestPath == null) {
                throw new Exception("No path found for traffic demand " + trafficDemand.getAlias() + " with delay bound less than infinity");
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
