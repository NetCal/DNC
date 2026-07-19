package org.networkcalculus.dnc.network.device_graph.acyclic_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.device_graph.path_routing_strategy.PathRoutingStrategy;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;

public interface AcyclicRoutingStrategy {
    public ServerGraph convertToServerGraph(DeviceGraph deviceGraph, PathRoutingStrategy pathRoutingStrategy);
}
