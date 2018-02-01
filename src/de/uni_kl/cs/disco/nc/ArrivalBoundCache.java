package de.uni_kl.cs.disco.nc;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Server;

import java.util.HashSet;
import java.util.Set;

public interface ArrivalBoundCache {

     void clearCache();

     Set<ArrivalCurve> addArrivalBounds(AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest, Set<ArrivalCurve> arrival_bounds ) ;

     Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest );

     CacheEntryServer getCacheEntry(AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest ) ;

     Set<ArrivalCurve> addArrivalBounds( AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest, Set<ArrivalCurve> arrival_bounds );

     Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest );

     CacheEntryLink getCacheEntry(AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest );

    // ArrivalCurve getMinBurstTBCurve( Set<ArrivalCurve> arrival_bounds );

}






