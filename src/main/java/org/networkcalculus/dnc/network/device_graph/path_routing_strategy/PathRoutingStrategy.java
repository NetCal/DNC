package org.networkcalculus.dnc.network.device_graph.path_routing_strategy;

import org.networkcalculus.dnc.network.device_graph.Device;
import org.networkcalculus.dnc.network.device_graph.DeviceGraph;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;

import java.util.Map;
import java.util.Set;

public interface PathRoutingStrategy {
    public ServerGraph routeTrafficDemands(ServerGraph serverGraph,
                                           DeviceGraph deviceGraph,
                                           Map<Device, Set<Server>> map__device__servers,
                                           Map<Device, Set<Server>> map__destination_device__servers) throws Exception;
}
