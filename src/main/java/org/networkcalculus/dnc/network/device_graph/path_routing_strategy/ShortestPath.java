package org.networkcalculus.dnc.network.device_graph.path_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.TrafficDemand;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShortestPath implements PathRoutingStrategy {

    private int seed = 0;

    public ShortestPath(){}

    public ShortestPath(int seed){
        this.seed = seed;
    }

    public ServerGraph routeTrafficDemands(ServerGraph serverGraph,
                                           DeviceGraph deviceGraph,
                                           Map<Device, Set<Server>> map__device__servers,
                                           Map<Device, Set<Server>> map__destination_device__servers) throws Exception {

        Map<TrafficDemand, Path> map__traffic_demand__shortest_path = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> futures = new ArrayList<>();

        for (TrafficDemand td : deviceGraph.getTrafficDemands()) {
            futures.add(executor.submit(() -> {

                Path shortestPath = null;
                int shortestPathTurns = Integer.MAX_VALUE;

                Random random = new Random(seed);


                for (Server src : map__device__servers.get(td.getSrc())) {
                    Set<Server> visited = new TreeSet<>(Comparator.comparing(Server::getId));

                    Map<Server, LinkedList<Turn>> paths_turns = new TreeMap<>(Comparator.comparing(Server::getId));
                    Map<Server, LinkedList<Server>> paths_servers = new TreeMap<>(Comparator.comparing(Server::getId));

                    paths_turns.put(src, new LinkedList<>());
                    paths_servers.put(src, new LinkedList<>(Collections.singleton(src)));

                    LinkedList<Server> queue = new LinkedList<>();
                    queue.add(src);
                    visited.add(src);

                    if(map__destination_device__servers.get(td.getDest()).contains(src)){
                        map__traffic_demand__shortest_path.put(td, serverGraph.createPath(src));
                        return null;
                    }

                    while (!queue.isEmpty()) {
                        Server s = queue.getLast();
                        queue.remove(s);

                        Set<Server> successors_s = serverGraph.getSuccessors(s);
                        List<Server> successors = new ArrayList<>(successors_s);
                        successors.sort(Comparator.comparing(Server::getId));
                        Collections.shuffle(successors, random);

                        for (Server successor : successors) {
                            LinkedList<Turn> path_turns_tmp = new LinkedList<>(paths_turns.get(s));

                            LinkedList<Server> path_servers_tmp;

                            if (paths_servers.containsKey(s)) {
                                path_servers_tmp = new LinkedList<>(paths_servers.get(s));
                            } else {
                                path_servers_tmp = new LinkedList<>(Collections.singleton(src));
                            }

                            path_turns_tmp.add(serverGraph.findTurn(s, successor));
                            path_servers_tmp.add(successor);

                            if (map__destination_device__servers.get(td.getDest()).contains(successor) && path_turns_tmp.size() < shortestPathTurns ||
                                    map__destination_device__servers.get(td.getDest()).contains(successor) && path_turns_tmp.size() == shortestPathTurns && random.nextBoolean()) {
                                shortestPath = serverGraph.createPath(path_servers_tmp, path_turns_tmp);
                                shortestPathTurns = path_turns_tmp.size();
                            }

                            if (!visited.contains(successor)) {
                                paths_turns.put(successor, path_turns_tmp);
                                paths_servers.put(successor, path_servers_tmp);
                                visited.add(successor);

                                if (paths_turns.get(successor).size() <= shortestPathTurns) {
                                    queue.add(successor);
                                }
                            } else if (paths_turns.get(successor).size() > path_turns_tmp.size()) {
                                paths_turns.put(successor, path_turns_tmp);
                                paths_servers.put(successor, path_servers_tmp);

                                if (paths_turns.get(successor).size() <= shortestPathTurns) {
                                    queue.add(successor);
                                }
                            }
                        }
                    }
                }

                if (shortestPath == null) {
                    throw new Exception("No path found for traffic demand " + td.getAlias());
                }

                map__traffic_demand__shortest_path.put(td, shortestPath);
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();

        for (TrafficDemand td : deviceGraph.getTrafficDemands()) {
            serverGraph.addFlow(td.getAlias(), td.getArrivalCurve(), map__traffic_demand__shortest_path.get(td));
        }

        return serverGraph;
    }
}

