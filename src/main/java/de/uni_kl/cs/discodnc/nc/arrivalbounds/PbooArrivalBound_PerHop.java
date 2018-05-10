/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.discodnc.nc.arrivalbounds;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.CurvePwAffine;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.misc.SetUtils;
import de.uni_kl.cs.discodnc.nc.AbstractArrivalBound;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig;
import de.uni_kl.cs.discodnc.nc.ArrivalBound;
import de.uni_kl.cs.discodnc.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.bounds.Bound;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Link;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;
import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PbooArrivalBound_PerHop extends AbstractArrivalBound implements ArrivalBound {
	private static PbooArrivalBound_PerHop instance = new PbooArrivalBound_PerHop();

	private PbooArrivalBound_PerHop() {
	}

	public PbooArrivalBound_PerHop(Network network, AnalysisConfig configuration) {
		this.network = network;
		this.configuration = configuration;
	}

	public static PbooArrivalBound_PerHop getInstance() {
		return instance;
	}

	public Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception {
		return computeArrivalBound(link, network.getFlows(link), flow_of_interest);
	}

	public Set<ArrivalCurve> computeArrivalBound(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
			throws Exception {
		Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>(Collections.singleton(CurvePwAffine.getFactory().createZeroArrivals()));
		if (f_xfcaller == null || f_xfcaller.isEmpty()) {
			return alphas_xfcaller;
		}

		// Get the servers crossed by all flows in f_xfcaller
		// that will eventually also cross the given link.
		Set<Flow> f_xfcaller_link = SetUtils.getIntersection(network.getFlows(link), f_xfcaller);
		f_xfcaller_link.remove(flow_of_interest);
		if (f_xfcaller_link.size() == 0) {
			// The flows to bound given in f_xfcaller do not cross the given link.
			return alphas_xfcaller;
		}

		// The shortcut found in PmooArrivalBound for the a common_subpath of length 1
		// will not be implemented here.
		// There's not a big potential to increase performance as the PBOO arrival bound
		// implicitly handles this situation by only iterating over one server in the
		// for loop.
		Server common_subpath_src = network.findSplittingServer(link.getDest(), f_xfcaller_link);
		Flow f_representative = f_xfcaller_link.iterator().next();
		Path common_subpath = f_representative.getSubPath(common_subpath_src, link.getSource());

		// Calculate the left-over service curves on this sub-path by convolution of the
		// individual left over service curves.
		Set<ServiceCurve> betas_lo_s;
		
		Link link_from_prev_s;
		Set<Flow> f_xxfcaller_server_onpath;
		Set<Flow> f_xxfcaller_server_src;

		// We need to know the arrival bound of f_xfcaller at the server 'common_subpath_src'
		// in order to deconvolve it with beta_lo_s in the order they appear on the common_subpath.
		// Note: 
		// * common_subpath must be a LinkedList retaining the order of servers.
		// * flows f_xfcaller that originate in 'common_subpath_src' are covered
		//   by this call of computeArrivalBound.
		alphas_xfcaller = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
				common_subpath_src, f_xfcaller, flow_of_interest);
		
		for (Server server : common_subpath.getServers()) {
			// Find the set of flows that interfere, either already on or coming off the common_subpath. 
			Set<Flow> f_xxfcaller_server = network.getFlows(server);
			f_xxfcaller_server.removeAll(f_xfcaller);		// We compute their beta l.o.
			f_xxfcaller_server.remove(flow_of_interest);	// If present, it has lowest priority.
			
        	// The common_subpath need not entirely coincide with the foi's path && 
			// !isSource implies that there is also a link connecting server and the preceding one.
        	if( common_subpath.getServers().contains(server) && !common_subpath.isSource(server) ) { 
        		link_from_prev_s = network.findLink(common_subpath.getPrecedingServer(server), server);
        		f_xxfcaller_server_onpath = SetUtils.getIntersection(f_xxfcaller_server, network.getFlows(link_from_prev_s));
        	} else {
        		f_xxfcaller_server_onpath = new HashSet<Flow>();
        	}
        	
        	// The interfering flows originating at the current server.
        	f_xxfcaller_server_src = network.getSourceFlows(server);
        	f_xxfcaller_server_src.remove(flow_of_interest);
        	f_xxfcaller_server_src.removeAll(f_xfcaller);

			// Convert f_xxfcaller_server to "f_xxfcaller_server_offpath"
			f_xxfcaller_server.removeAll(f_xxfcaller_server_onpath);
			f_xxfcaller_server.removeAll(f_xxfcaller_server_src);

            betas_lo_s = new HashSet<ServiceCurve>();

        	List<Set<ArrivalCurve>> ac_sets_to_combine = new LinkedList<Set<ArrivalCurve>>();
        	
        	if(!f_xxfcaller_server_src.isEmpty()) {
        		ac_sets_to_combine.add(Collections.singleton(network.getSourceFlowArrivalCurve(server, f_xxfcaller_server_src)));
        	}
        	
        	if(!f_xxfcaller_server_onpath.isEmpty()) {
        		ac_sets_to_combine.add(
        				ArrivalBoundDispatch.computeArrivalBounds(network, configuration ,server, f_xxfcaller_server_onpath, flow_of_interest));
        	}

        	// If, during the already executed arrival bounding procedure, we are already left the foi's path,
			// flow_of_interest was set to Flow.NULL_FLOW before. If not, do it again now, that won't harm.
        	if(!f_xxfcaller_server.isEmpty()) {
        		ac_sets_to_combine.add(
        			ArrivalBoundDispatch.computeArrivalBounds(network, configuration, server, f_xxfcaller_server, Flow.NULL_FLOW));
        	}
        	
        	if( ac_sets_to_combine.isEmpty() ) {
        		betas_lo_s.add(server.getServiceCurve());
        	} else {
        		Iterator<Set<ArrivalCurve>> ac_set_iterator = ac_sets_to_combine.iterator();
        		Set<ArrivalCurve> alpha_xfois = new HashSet<ArrivalCurve>(ac_set_iterator.next());
    			
        		Set<ArrivalCurve> ac_combinations_tmp = new HashSet<ArrivalCurve>();
                while(ac_set_iterator.hasNext()) {
                	for(ArrivalCurve ac_new : ac_set_iterator.next()) {
                		for(ArrivalCurve ac_existing : alpha_xfois) {
                			ac_combinations_tmp.add(CurvePwAffine.add(ac_new,ac_existing));
                		}
                	}
            		alpha_xfois.clear();
            		alpha_xfois.addAll(ac_combinations_tmp);
                	ac_combinations_tmp.clear();
                }
	             
                // Calculate the left-over service curve for this single server
                betas_lo_s = Bound.leftOverService(configuration, server, alpha_xfois);

    			// Check if there's any service left on this path. If not, the set only contains
    			// a null-service curve.
    			if (betas_lo_s.size() == 1
    				&& betas_lo_s.iterator().next().equals(CurvePwAffine.getFactory().createZeroService())) {
	    				System.out.println("No service left over during PBOO arrival bounding!");
	    				alphas_xfcaller.clear();
	    				alphas_xfcaller.add(CurvePwAffine.getFactory().createArrivalCurve(CurvePwAffine.getFactory().createZeroDelayInfiniteBurst()));
	    				return alphas_xfcaller;
    			}
            }
			
			// The deconvolution of the two sets, arrival curves and service curves,
			// respectively, takes care of all the possible combinations
			alphas_xfcaller = Bound.output(configuration, alphas_xfcaller, server, betas_lo_s);
		}

		if (configuration.abConsiderTFANodeBacklog()) {
			Server last_hop_xtx = link.getSource();
			TotalFlowAnalysis tfa = new TotalFlowAnalysis(network, configuration);
			tfa.deriveBoundsAtServer(last_hop_xtx);

			Set<Num> tfa_backlog_bounds = tfa.getServerBacklogBoundMap().get(last_hop_xtx);
			Num tfa_backlog_bound_min = Num.getFactory().getPositiveInfinity();

			for (Num tfa_backlog_bound : tfa_backlog_bounds) {
				if (tfa_backlog_bound.leq(tfa_backlog_bound_min)) {
					tfa_backlog_bound_min = tfa_backlog_bound;
				}
			}

			// Reduce the burst
			
			// TODO This implementation only works for token-bucket arrivals. 
			// It disregards the potential shift in inflection points not present in this burst cap variant.
			for (ArrivalCurve alpha_xfcaller : alphas_xfcaller) {
				if(alpha_xfcaller.getSegmentCount() > 2 ) {
					// >2 segments -> >=2 inflection points -> burst reduction not applicable! 
					continue;
				}
				if (alpha_xfcaller.getBurst().gt(tfa_backlog_bound_min)) {
					alpha_xfcaller.getSegment(1).setY(tfa_backlog_bound_min); // if the burst is >0 then there are at
					// least two segments and the second
					// holds the burst as its y-axis value
				}
			}
		}

		return alphas_xfcaller;
	}
}