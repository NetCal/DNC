package de.uni_kl.cs.disco.nc;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.ArrivalCurve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.network.Flow;

public class ArrivalBoundCacheImpl2  implements ArrivalBoundCache{

   // private HashMap<Server,HashMap<String, ArrayList<CacheEntryServer>>> map__server__entries = Collections.synchronizedMap( new HashMap<Server,    ArrayList<CacheEntryServer>>() ); TODO

    //TODO should be ok if we synchronize the methods in this class (maybe 2 different locks since we have 2 different maps (link/server)), at the moment no concurrent access anyway
    // map of a server: another map which maps "multiplexingDiscipline useGamma useExtraGamma bounded_flows.size" to the corresponding entries
    private HashMap<Server,HashMap<String, ArrayList<CacheEntryServer>>> map__server__entries = new HashMap<Server,HashMap<String, ArrayList<CacheEntryServer>>>();

    //private HashMap<Link,HashMap<String, ArrayList<CacheEntryLink>>> map__link__entries = Collections.synchronizedMap( new HashMap<Link, HashMap<String, ArrayList<CacheEntryLink>>>() );TODO

    private HashMap<Link,HashMap<String, ArrayList<CacheEntryLink>>> map__link__entries = new HashMap<Link,HashMap<String, ArrayList<CacheEntryLink>>>();


    private static boolean output = false;



    //TODO methods are synchronized -> instead maybe use rw locks if possible



    protected ArrivalBoundCacheImpl2() {};

    public void clearCache() {
        //map__server__entries = Collections.synchronizedMap( new HashMap<Server,Set<CacheEntryServer>>() );
        //map__link__entries = Collections.synchronizedMap( new HashMap<Link,Set<CacheEntryLink>>() );
        map__server__entries = new HashMap<Server,HashMap<String, ArrayList<CacheEntryServer>>>();
        map__link__entries = new HashMap<Link,HashMap<String, ArrayList<CacheEntryLink>>>();

    }

    public synchronized Set<ArrivalCurve> addArrivalBounds( AnalysisConfig configuration,
                                                               Server server,
                                                               Set<Flow> bounded_flows,
                                                               Flow flow_of_interest,
                                                               Set<ArrivalCurve> arrival_bounds ) {
        if( bounded_flows.contains( flow_of_interest )
                || bounded_flows.isEmpty()
                || arrival_bounds.isEmpty() ) {
            return new HashSet<ArrivalCurve>();
        }

        // Remove possible old entry
        CacheEntryServer entry = getCacheEntry( configuration, server, bounded_flows, flow_of_interest );
        if ( entry != null ) {
            // get(...) cannot return null as the entry was already confirmed to exist.
            //map__server__entries.get( server ).remove( entry );
            HashMap<String, ArrayList<CacheEntryServer>> entries = map__server__entries.get( server );
            entries.get (entry.configuration.multiplexingDiscipline() + " " + entry.configuration.useGamma() + " " +  entry.configuration.useExtraGamma() + " " +entry.bounded_flows.size()).remove(entry);
        }

        Set<ArrivalCurve> arrival_bounds_stored;


        //TODO convolveAlternativeArrivalBounds not in config anymore
		// Create arrival bound to store
		/*if( configuration.convolveAlternativeArrivalBounds() ) {	// Convolve given bounds into one.
			//----------------------------------------------------------------------------------------------------
			// TBRL-specific solution for homogeneous networks (crucial point: arrival rates do not change).
			// Generic version:
			// arrival_bounds_stored = Convolution.convolve( arrival_bounds_new );
			//----------------------------------------------------------------------------------------------------
			arrival_bounds_stored = new HashSet<ArrivalCurve>( Collections.singleton( getMinBurstTBCurve( arrival_bounds ) ) );
		}
        else { 	*/												// Take them as they are.
            arrival_bounds_stored = new HashSet<ArrivalCurve>( arrival_bounds );
        //}
        entry = new CacheEntryServer( configuration.copy(), server, new HashSet<Flow>( bounded_flows ), flow_of_interest, arrival_bounds_stored );

        // No need to check if the maps has entries for the server or the flow of interest.
        // If missing they were created by the call to getCacheEntry(...) above.
        map__server__entries.get( server ).get (entry.configuration.multiplexingDiscipline() + " " + entry.configuration.useGamma() + " " +  entry.configuration.useExtraGamma() + " " +entry.bounded_flows.size()).add( entry );

        return arrival_bounds_stored;
    }

    public Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest ) {
        CacheEntryServer entry = getCacheEntry( configuration, server, bounded_flows, flow_of_interest );
        if ( entry == null ) {
            return new HashSet<ArrivalCurve>();
        } else {
            return new HashSet<ArrivalCurve>( entry.arrival_bounds );
        }
    }

    /**
     *
     * Returns the cache entry for the given parameters if there is one.
     *
     * If not, it returns null.
     *
     * In addition, if there's no entry, the internal mappings will be created as a subsequent adding of an according entry is expected.
     *
     * @param server
     * @param bounded_flows
     * @param flow_of_interest
     * @return
     */
    public synchronized CacheEntryServer getCacheEntry( AnalysisConfig configuration, Server server, Set<Flow> bounded_flows, Flow flow_of_interest ) {
        if(output) {
            System.out.println("getCacheEntry v2 called");
        }

        // Most important feature is an efficient search
        HashMap<String, ArrayList<CacheEntryServer>> entries_s = map__server__entries.get( server );
        if ( entries_s == null ) {
            map__server__entries.put( server, new HashMap<String, ArrayList<CacheEntryServer>>());

            return null;
        }

       ArrayList<CacheEntryServer> cache_entries_s = entries_s.get(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size());
        // all elements in cache_entries_s  have the same muliplexingdiscipline, useGamma etc so we do not need to check it

        if(cache_entries_s == null)
        {
            entries_s.put(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size(), new ArrayList<CacheEntryServer>());

            return null;
        }

        for(CacheEntryServer entry : cache_entries_s)
        {
            if(entry.bounded_flows.containsAll( bounded_flows ))
            {

                return entry;
            }
        }


        return null;
    }

    public synchronized Set<ArrivalCurve> addArrivalBounds( AnalysisConfig configuration,
                                                               Link link,
                                                               Set<Flow> bounded_flows,
                                                               Flow flow_of_interest,
                                                               Set<ArrivalCurve> arrival_bounds ) {
        if( bounded_flows.contains( flow_of_interest )
                || bounded_flows.isEmpty()
                || arrival_bounds.isEmpty() ) {
            return new HashSet<ArrivalCurve>();
        }

        // Remove possible old entry
        CacheEntryLink entry = getCacheEntry( configuration, link, bounded_flows, flow_of_interest );
        if ( entry != null ) {
            // get(...) cannot return null as the entry was already confirmed to exist.
            map__link__entries.get( link ).get(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size()).remove( entry );
        }

        Set<ArrivalCurve> arrival_bounds_stored;

        // Create arrival bound to store

        //TODO see above
		/*if( configuration.convolveAlternativeArrivalBounds() ) {	// Convolve given bounds into one.
			//----------------------------------------------------------------------------------------------------
			// TBRL-specific solution for homogeneous networks (crucial point: arrival rates do not change).
			// Generic version:
			// arrival_bounds_stored = Convolution.convolve( arrival_bounds_new );
			//----------------------------------------------------------------------------------------------------
			arrival_bounds_stored = new HashSet<ArrivalCurve>( Collections.singleton( getMinBurstTBCurve( arrival_bounds ) ) );
		}
        else { 	*/												// Take them as they are.
            arrival_bounds_stored = new HashSet<ArrivalCurve>( arrival_bounds );
        //}
        entry = new CacheEntryLink(configuration, link, new HashSet<Flow>( bounded_flows ), flow_of_interest, arrival_bounds_stored );

        map__link__entries.get( link ).get(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size()).add( entry );

        return arrival_bounds_stored;
    }

    public Set<ArrivalCurve> getArrivalBounds( AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest ) {
        CacheEntryLink entry = getCacheEntry( configuration, link, bounded_flows, flow_of_interest );
        if ( entry == null ) {
            return new HashSet<ArrivalCurve>();
        } else {
            return new HashSet<ArrivalCurve>( entry.arrival_bounds );
        }
    }

    public synchronized CacheEntryLink getCacheEntry( AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest ) {
        if(output) {
            System.out.println("getCacheEntry v2 called");
        }
        // Most important feature is an efficient search

        HashMap<String, ArrayList<CacheEntryLink>> entries_l = map__link__entries.get( link );
        if ( entries_l == null ) {
            map__link__entries.put( link, new HashMap<String, ArrayList<CacheEntryLink>> () );

            return null;
        }

        ArrayList<CacheEntryLink> cache_entries_l = entries_l.get(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size());
        // all elements in cache_entries_l  have the same muliplexingdiscipline, useGamma etc so we do not need to check it

        if(cache_entries_l == null)
        {
            entries_l.put(configuration.multiplexingDiscipline() + " " + configuration.useGamma() + " " +  configuration.useExtraGamma() + " " +bounded_flows.size(), new ArrayList<CacheEntryLink>());

            return null;
        }

        for(CacheEntryLink entry : cache_entries_l)
        {
            if(entry.bounded_flows.containsAll( bounded_flows ))
            {

                return entry;
            }
        }

        return null;
    }



    //------------------------------------------------------------------------------------
    // TBRL-specific solution
    //------------------------------------------------------------------------------------
    //TODO do we need this method?!
    /*
    public ArrivalCurve getMinBurstTBCurve( Set<ArrivalCurve> arrival_bounds ) {
        if( arrival_bounds == null || arrival_bounds.isEmpty() ) {
           // return ArrivalCurve.createNullArrival();
          new ArrivalCurve_MPARTC_PwAffine(); // return new Curve_MPARTC_PwAffine().createZeroArrivals(); //TODO check

        }

        Iterator<ArrivalCurve> ab_iter = arrival_bounds.iterator();
        ArrivalCurve min_burst_curve = ab_iter.next();

        if( arrival_bounds.size() == 1 ) {
            return min_burst_curve.copy();
        }

        Num min_burst = min_burst_curve.getTBBurst();
        Num max_burst = min_burst_curve.getTBBurst();

        while ( ab_iter.hasNext() ) {
            ArrivalCurve arrival_bound = ab_iter.next();
            if ( arrival_bound.getTBBurst().less( min_burst ) ) {
                min_burst_curve = arrival_bound;
                min_burst = arrival_bound.getTBBurst().copy();
            }
            if ( arrival_bound.getTBBurst().greater( max_burst ) ) {
                max_burst = arrival_bound.getTBBurst().copy();
            }
        }

        return min_burst_curve;
    }*/


}