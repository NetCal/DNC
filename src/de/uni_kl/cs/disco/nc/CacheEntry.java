package de.uni_kl.cs.disco.nc;


import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Server;

import java.util.Set;
import java.util.ArrayList;

// Cache entry types
public class CacheEntry {

        protected AnalysisConfig configuration;
        protected Set<Flow> bounded_flows;
        protected Flow flow_of_interest;
        protected Set<ArrivalCurve> arrival_bounds;

        protected CacheEntry( AnalysisConfig configuration,
                              Set<Flow> bounded_flows,
                              Flow flow_of_interest,
                              Set<ArrivalCurve> arrival_bounds ) {
            this.configuration = configuration;
            this.bounded_flows = bounded_flows;
            this.flow_of_interest = flow_of_interest;
            this.arrival_bounds = arrival_bounds;

        }

        @Override
        public String toString() {
            String result = "CacheEntry(";

            result += ";\n";
            if ( bounded_flows != null ) {
                result += bounded_flows.toString();
            } else {
                result += "null_flows";
            }
            result += ";\n";
            if ( flow_of_interest != null ) {
                result += flow_of_interest.toString();
            } else {
                result += "null_foi";
            }
            result += ";\n";
            if ( arrival_bounds != null ) {
                result += arrival_bounds.toString();
            } else {
                result += "null_arrival_curve";
            }

            return result += ";\n" + configuration.arrivalBoundMethods().toString()  + "; "
                    + configuration.multiplexingDiscipline().toString() + "; " + configuration.useGamma().toString()  + "; "
                    + configuration.useExtraGamma().toString() + ")";
        }
    }

    class CacheEntryServer extends CacheEntry {
        protected Server server;

        protected CacheEntryServer( AnalysisConfig configuration,
                                    Server server,
                                    Set<Flow> bounded_flows,
                                    Flow flow_of_interest,
                                    Set<ArrivalCurve> arrival_bounds ) {
            super( configuration, bounded_flows, flow_of_interest, arrival_bounds );
            this.server = server;
        }

        @Override
        public String toString() {
            String superclass = super.toString();
            // length of CacheEntry( is 11
            superclass.substring( 11, superclass.length() );

            String result = "CacheEntryServer(";
            if ( server != null ) {
                result += server.toString();
            } else {
                result += "null_server";
            }

            return result + superclass;
        }
    }

    class CacheEntryLink extends CacheEntry {
        protected Link link;

        protected CacheEntryLink( AnalysisConfig configuration, Link link, Set<Flow> bounded_flows, Flow flow_of_interest, Set<ArrivalCurve> arrival_bounds ) {
            super( configuration, bounded_flows, flow_of_interest, arrival_bounds );
            this.link = link;
        }

        @Override
        public String toString() {
            String superclass = super.toString();
            // length of CacheEntry( is 11
            superclass.substring( 11, superclass.length() );

            String result = "CacheEntry(";

            if ( link != null ) {
                result += link.toString();
            } else {
                result += "null_server";
            }

            return result + superclass;
        }
    }

