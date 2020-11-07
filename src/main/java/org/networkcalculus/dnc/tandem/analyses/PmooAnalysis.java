/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.tandem.analyses;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AlgDncBackend_DNC_Affine;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatch;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.network.server_graph.Turn;
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
import org.networkcalculus.num.Num;

public class PmooAnalysis extends AbstractTandemAnalysis {
    @SuppressWarnings("unused")
    private PmooAnalysis() {
    }

    public PmooAnalysis(ServerGraph server_graph) {
        super.server_graph = server_graph;
        super.configuration = new AnalysisConfig();
        super.result = new PmooResults();
    }

    public PmooAnalysis(ServerGraph server_graph, AnalysisConfig configuration) {
        super.server_graph = server_graph;
        super.configuration = configuration;
        super.result = new PmooResults();
    }
    
    public static ServiceCurve getServiceCurve(Path path, List<Flow> cross_flow_substitutes) {
    	if( Calculator.getInstance().getDncBackend() == AlgDncBackend_DNC_Affine.DISCO_AFFINE) {
    		return getServiceCurve_Affine(path, cross_flow_substitutes);
    	} else {
    		return getServiceCurve_ConPwAffine(path, cross_flow_substitutes);
    	}
    }

    public static ServiceCurve getServiceCurve_Affine(Path path, List<Flow> cross_flow_substitutes) {
    	
    	Map<Server,Set<Flow>> server_xfsubst_map = new HashMap<Server,Set<Flow>>();
    	Set<Flow> flows_at_current_server;
    	for (Server s : path.getServers()) {
    		flows_at_current_server = new HashSet<Flow>();
    		
    		for (Flow f : cross_flow_substitutes) {
    			if( f.getPath().getServers().contains(s)) {
    				flows_at_current_server.add(f);
    			}
    		}
    		
    		server_xfsubst_map.put(s, flows_at_current_server);
    	}
    	
    	Num num_utils =  Num.getUtils(Calculator.getInstance().getNumBackend());
    	Num num_factory = num_utils;
    	
    	Num sum_latencies = num_factory.getZero();
    	for (Server s : path.getServers()) {
    		sum_latencies = num_utils.add(sum_latencies, s.getServiceCurve().getLatency());
    	}

    	Num sum_bursts = num_factory.getZero();
    	for (Flow f : cross_flow_substitutes) {
    		sum_bursts = num_utils.add(sum_bursts, f.getArrivalCurve().getBurst());
    	}
    	
    	Num lo_rate_tandem = num_factory.getPositiveInfinity();
    	Num sum_burst_increases = num_factory.getZero();
    	
    	Num xf_s_arrival_rate, lo_rate_current_server;
    	for (Server s : path.getServers()) {
        	
    		xf_s_arrival_rate = num_factory.getZero();
        	lo_rate_current_server = num_factory.getZero();
    		for( Flow f : server_xfsubst_map.get(s)) {
    			xf_s_arrival_rate = num_utils.add(xf_s_arrival_rate, f.getArrivalCurve().getUltAffineRate());
    		}
    		lo_rate_current_server = num_utils.sub(s.getServiceCurve().getUltAffineRate(), xf_s_arrival_rate);
    		
    		if(lo_rate_current_server.ltZero()) {
    			return Curve.getFactory().createZeroService();
    		}

			sum_burst_increases = num_factory.add(sum_burst_increases, 
					num_factory.mult(s.getServiceCurve().getLatency(), xf_s_arrival_rate));
    		lo_rate_tandem = num_utils.min(lo_rate_tandem, lo_rate_current_server);
    	}
    	
    	Num lo_latency_tandem =
    			num_utils.add(sum_latencies, 
    					num_utils.div(num_utils.add(sum_bursts, sum_burst_increases),
    							lo_rate_tandem)
    			);
    	
    	return Curve.getFactory().createRateLatency(lo_rate_tandem, lo_latency_tandem); 
    }

    /**
     * Concatenates the service curves along the given path <code>path</code>
     * according to the PMOO approach and returns the result.
     * <p>
     * It first decomposes all arrival curves (service curves) into token buckets
     * (rate latency curves), enumerates over all combinations of token buckets and
     * rate latency curves, and calls <code>computePartialPMOOServiceCurve()</code>
     * for each combination. The total PMOO service curve is the maximum of all
     * partial service curves.
     *
     * @param path                   The Path traversed for which a PMOO left-over service curve will
     *                               be computed.
     * @param cross_flow_substitutes Flow substitutes according to PMOO's needs and abstracting from
     *                               the actual cross-flows.
     * @return The PMOO service curve
     */
    public static ServiceCurve getServiceCurve_ConPwAffine(Path path, List<Flow> cross_flow_substitutes) {
        // Create a flow-->tb_iter map
        Map<Flow, Integer> flow_tb_iter_map = new HashMap<Flow, Integer>();
        for (Flow f : cross_flow_substitutes) {
            flow_tb_iter_map.put(f, Integer.valueOf(0));
        }
        // Create a list of rl_iters
        int number_servers = path.getServers().size();
        ServiceCurve[] service_curves = new ServiceCurve[number_servers];
        int[] server_rl_iters = new int[number_servers];
        int[] server_rl_counts = new int[number_servers];
        int i = 0;
        for (Server server : path.getServers()) {
            ServiceCurve service_curve = server.getServiceCurve();
            service_curves[i] = service_curve;
            server_rl_iters[i] = 0;
            server_rl_counts[i] = service_curve.getRL_ComponentCount();
            i++;
        }

        ServiceCurve beta_total = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();

        boolean more_combinations = true;
        while (more_combinations) {
            // Compute service curve for this combination
            ServiceCurve beta = computePartialPMOOServiceCurve(path, service_curves, cross_flow_substitutes,
                    flow_tb_iter_map, server_rl_iters);
            if (!beta.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())) {
                beta_total = Curve.getUtils().max(beta_total, beta);
            }

            // First check whether there are more combinations of flow TBs
            more_combinations = false;
            Flow f;
            ArrivalCurve f_bound;
            for (Entry<Flow, Integer> entry : flow_tb_iter_map.entrySet()) {
                f = entry.getKey();
                f_bound = f.getArrivalCurve();

                i = flow_tb_iter_map.get(f).intValue();
                if (i + 1 < f_bound.getTB_ComponentCount()) {
                    flow_tb_iter_map.put(f, Integer.valueOf(i + 1));
                    more_combinations = true;
                    break;
                } else {
                    flow_tb_iter_map.put(f, Integer.valueOf(0));
                }
            }

            // If not, check whether there are more combinations of server RLs
            if (!more_combinations) {
                for (i = 0; i < server_rl_iters.length; i++) {
                    int j = server_rl_iters[i];
                    if (j + 1 < server_rl_counts[i]) {
                        server_rl_iters[i] = j;
                        more_combinations = true;
                        break;
                    } else {
                        server_rl_iters[i] = 0;
                    }
                }
            }
        }

        return beta_total;
    }

    /**
     * Calculates the partial PMOO service curve for the given flow set by combining
     * all servers having an outgoing turn contained in the given turn path. For
     * each flow considers only one of its token bucket components (selected via the
     * flow_tb_iter_map) and for each service curve considers only one rate latency
     * curve (selected via the server_rl_iters).
     *
     * @param path                   The tandem of servers the left-over service curve holds for.
     * @param service_curves         The service curves on the path.
     * @param cross_flow_substitutes Flows representing a group of segregated flows.
     * @param flow_tb_iter_map       Defines which token bucket component of a flow's arrival bound to
     *                               use.
     * @param server_rl_iters        Defines which rate latency component of a server's service curve
     *                               to use.
     * @return A partial PMOO service curve.
     */
    protected static ServiceCurve computePartialPMOOServiceCurve(Path path, ServiceCurve[] service_curves,
                                                                 List<Flow> cross_flow_substitutes, Map<Flow, Integer> flow_tb_iter_map, int[] server_rl_iters) {
        Num T = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num R = Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        Num sum_bursts = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num sum_latencyterms = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();

        Num compute = Num.getUtils(Calculator.getInstance().getNumBackend());
        
        double sum_r_at_s;

        Set<Flow> present_flows = new HashSet<Flow>();
        for (Server s : path.getServers()) {
            sum_r_at_s = 0.0;
            int i = path.getServers().indexOf(s);
            // Add incoming flows
            for (Flow f : cross_flow_substitutes) {
                if (f.getPath().getServers().contains(s)) { // The exact path of the substitute does not matter, only
                    // the shared servers with the flow of interest do
                    present_flows.add(f);
                    sum_r_at_s += f.getArrivalCurve().getUltAffineRate().doubleValue();
                }
            }

            // Check for stability constraint violation
            if (sum_r_at_s >= s.getServiceCurve().getUltAffineRate().doubleValue()) {
                return Curve.getFactory().createZeroService();
            }

            Curve tmpcurve = service_curves[i].getRL_Component(server_rl_iters[i]);
            ServiceCurve current_rl = Calculator.getInstance().getCurveFactory().createServiceCurve(tmpcurve);

            // Sum up latencies
            T = compute.add(T, current_rl.getLatency());

            // Compute and store sum of rates of all passing flows
            Num sum_r = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
            for (Flow f : present_flows) {
                ArrivalCurve bound = f.getArrivalCurve();
                Curve ac = bound.getTB_Component(((Integer) flow_tb_iter_map.get(f)).intValue());
                ArrivalCurve current_tb = Calculator.getInstance().getCurveFactory().createArrivalCurve(ac);
                sum_r = compute.add(sum_r, current_tb.getUltAffineRate());
            }

            // Update latency terms (increments)
            sum_latencyterms = compute.add(sum_latencyterms,
                    compute.mult(sum_r, current_rl.getLatency()));

            // Compute left-over rate; update min
            Num Ri = compute.sub(current_rl.getUltAffineRate(), sum_r);
            if (Ri.leqZero()) {
                return Curve.getFactory().createZeroService();
            }
            R = compute.min(R, Ri);

            // Remove all outgoing flows from the set of present flows
            Set<Flow> leaving_flows = new HashSet<Flow>();
            for (Flow f : present_flows) {
                if (path.getServers().indexOf(f.getSink()) <= i) {
                    leaving_flows.add(f);
                }
            }
            present_flows.removeAll(leaving_flows);
        }

        // Compute sum of bursts
        for (Flow f : cross_flow_substitutes) {
            ArrivalCurve bound = f.getArrivalCurve();
            Curve ac = bound.getTB_Component(((Integer) flow_tb_iter_map.get(f)).intValue());
            ArrivalCurve current_tb = Calculator.getInstance().getCurveFactory().createArrivalCurve(ac);
            sum_bursts = compute.add(sum_bursts, current_tb.getBurst());
        }

        T = compute.add(T, compute.div(compute.add(sum_bursts, sum_latencyterms), R));

		if (T == Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity()) {
            return Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
        }
		if (R == Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity()) {
            return Curve.getFactory().createDelayedInfiniteBurst(T);
        }

        return Curve.getFactory().createRateLatency(R, T);
    }

    /**
     * Performs a pay-multiplexing-only-once (PMOO) analysis for the
     * <code>flow_of_interest</code>.
     *
     * @param flow_of_interest the flow for which the end-to-end service curve shall be computed.
     */
    public void performAnalysis(Flow flow_of_interest) throws Exception {
        performAnalysis(flow_of_interest, flow_of_interest.getPath());
    }

    public void performAnalysis(Flow flow_of_interest, Path path) throws Exception {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO) {
            throw new Exception("PMOO analysis is not available for FIFO multiplexing nodes");
        }

        ((PmooResults) result).betas_e2e = getServiceCurves(flow_of_interest, path,
                Collections.singleton(flow_of_interest));

        Num delay_bound__beta_e2e;
        Num backlog_bound__beta_e2e;

        ((PmooResults) result).setDelayBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
        ((PmooResults) result).setBacklogBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());

        for (ServiceCurve beta_e2e : ((PmooResults) result).betas_e2e) {
            // Single flow of interest, i.e., fifo per micro flow holds
            delay_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(flow_of_interest.getArrivalCurve(), beta_e2e);
            if (delay_bound__beta_e2e.leq(result.getDelayBound())) {
                ((PmooResults) result).setDelayBound(delay_bound__beta_e2e);
            }

            backlog_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().backlog(flow_of_interest.getArrivalCurve(), beta_e2e);
            if (backlog_bound__beta_e2e.leq(result.getBacklogBound())) {
                ((PmooResults) result).setBacklogBound(backlog_bound__beta_e2e);
            }
        }
    }

    public Set<ServiceCurve> getServiceCurves(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve)
            throws Exception {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL) {
            for (Server s : path.getServers()) {
                if (s.multiplexing() == Multiplexing.FIFO) {
                    throw new Exception("PMOO analysis is not available for FIFO multiplexing nodes");
                }
            }
        }

        if (configuration.useFlowProlongation()) {
            return getServiceCurvesFP(flow_of_interest, path, flows_to_serve);
        } else {
            return getServiceCurvesStandard(flow_of_interest, path, flows_to_serve);
        }
    }

    private Set<ServiceCurve> getServiceCurvesFP(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve)
            throws Exception {
        // Get cross-flows grouped as needed for the PMOO left-over service curve
        Set<Flow> flows_of_lower_priority = new HashSet<Flow>(flows_to_serve);
        flows_of_lower_priority.add(flow_of_interest);

        Set<Flow> cross_flows = server_graph.getFlows(path);
        cross_flows.removeAll(flows_of_lower_priority);

        Map<Path, Set<Flow>> xtx_subpath_grouped_original = server_graph.groupFlowsPerSubPath(path, cross_flows);
        Map<Set<Flow>, LinkedList<Path>> prolongations = getProlongationsToSubpaths(path, xtx_subpath_grouped_original);

        if (prolongations.isEmpty()) {
            return xtxSubpathBetas(flow_of_interest, path, xtx_subpath_grouped_original);
        }

        // Create the set of cross-flow groupings, starting from the one for unprolonged
        // flows.
        Set<Map<Path, Set<Flow>>> xtx_subpath_grouped_incl_prolongation = new HashSet<Map<Path, Set<Flow>>>();
        xtx_subpath_grouped_incl_prolongation.add(xtx_subpath_grouped_original);

        // General way it works:
        // Permutations of all alternatives (see the code below ):
        // - iterate over all the sets above
        // - iterate over the results set
        // - merge into a temporary results set (prevents concurrent modification
        // exception)
        // - replace results set with temporary one
        // - start over with the outer iteration

        // Move the individual cross flows around the xtx groups.
        for (Entry<Set<Flow>, LinkedList<Path>> xf_prolongations : prolongations.entrySet()) {

            LinkedList<Path> prolongable_paths = xf_prolongations.getValue();
            if (prolongable_paths.size() == 1) {
                throw new Exception("Something strange went wrong during flow prolongation");
            }

            Set<Flow> xfs = xf_prolongations.getKey();
            Path common_path_old = prolongable_paths.getFirst(); // Original path needs to be always first.
            Path common_path_new;
            Server subpaths_src;

            // Every moving is defined by a prolonged flow aggregate's path.
            // Start counting at 1 because the first element (at position 0) is the original
            // interference path.
            for (int i = 1; i <= prolongable_paths.size() - 1; i++) {
                common_path_new = prolongable_paths.get(i);

                // Each potential move of a flow generates a new set of xtx_subpath_groupings .
                // They are merged into a temporary results set to prevent concurrent
                // modification exceptions.
                // Yes, we need a set of mappings because each mapping will be a unique
                // interference patter no derive a left-over beta for.
                Set<Map<Path, Set<Flow>>> xtx_subpath_grouped_incl_prolongation_tmp = new HashSet<Map<Path, Set<Flow>>>();

                // At the beginning, only the original interference pattern is in here.
                for (Map<Path, Set<Flow>> interference_pattern : xtx_subpath_grouped_incl_prolongation) { 
                    // Prevent the original from getting lost because its sink is not part of
                    // xf_prolongations's Server list.
                    xtx_subpath_grouped_incl_prolongation_tmp.add(interference_pattern);

                    // The general idea is to copy the map and modify the two mappings of interest only.
                    // Yet, the copy we need is deeper than constructing a new map or using clone()-functionality.
                    // We need a new map where 
                    // a) the key can be the same path object as in to old map and
                    // b) the value is a new map with the old flow objects.
                    // Constructing this copy will require a loop -- the following loop we use for
                    // an "in situ" modification we need.
                    Map<Path, Set<Flow>> interference_pattern_new = new HashMap<Path, Set<Flow>>();
                    boolean add = true;
                    for (Entry<Path, Set<Flow>> interference_entry : interference_pattern.entrySet()) {

                        Path key_path = interference_entry.getKey();
                        Set<Flow> value_flows = new HashSet<Flow>(interference_entry.getValue());

                        if (key_path.equals(common_path_old)) {
                            if (!value_flows.removeAll(xfs)) { // The flows have already been moved -> skip this one.
                                add = false;
                                continue;
                            }
                            interference_pattern_new.put(key_path, value_flows);
                        } else if (key_path.equals(common_path_new)) {
                            if (value_flows.isEmpty()) { // Prolonging to an empty set will not cause aggregation
                                // effects.
                                add = false;
                                continue;
                            }

                            boolean aggr_potential = false;
                            subpaths_src = common_path_new.getSource();
                            Turn inturn_xfs = xfs.iterator().next().getPrecedingTurn(subpaths_src);
                            Turn inturn_subpath_flows;
                            for (Flow f : value_flows) {
                            	try {
                                    inturn_subpath_flows = f.getPrecedingTurn(subpaths_src);
                                    if (inturn_subpath_flows.equals(inturn_xfs)) {
                                        aggr_potential = true;
                                        continue;
                                    }
                                // There's an exception thrown by getPrecedingTurn if f is originating in subpaths_src
                                } catch (Exception e) {
                                } 
                            }

                            if (!aggr_potential) {
                                add = false;
                                continue;
                            }

                            value_flows.addAll(xfs);
                            interference_pattern_new.put(key_path, value_flows);
                        } else {
                            interference_pattern_new.put(key_path, value_flows);
                        }
                    }

                    if (add) {
                        xtx_subpath_grouped_incl_prolongation_tmp.add(interference_pattern_new);
                    }
                }
                xtx_subpath_grouped_incl_prolongation = xtx_subpath_grouped_incl_prolongation_tmp;
            }
        }

        // Next, get the left-over betas for every prolongation variant.
        Set<ServiceCurve> betas_e2e = Collections.synchronizedSet(new HashSet<ServiceCurve>());

        xtx_subpath_grouped_incl_prolongation.parallelStream().forEach(xtx_subpath_grouped -> {
            try {
                betas_e2e.addAll(xtxSubpathBetas(flow_of_interest, path, xtx_subpath_grouped));
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
            }
        });

        if (betas_e2e.isEmpty()) {
            betas_e2e.add(Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
        }
        return betas_e2e;
    }

    private Map<Set<Flow>, LinkedList<Path>> getProlongationsToSubpaths(Path path,
                                                                        Map<Path, Set<Flow>> xtx_subpath_grouped_original) throws Exception {
        Map<Server, Set<Path>> paths_starting_in_s = new HashMap<Server, Set<Path>>();

        Server subpath_src, subpath_snk;
        Set<Path> paths_from_s;
        for (Path existing_subpath : xtx_subpath_grouped_original.keySet()) {
            subpath_src = existing_subpath.getSource();
            paths_from_s = paths_starting_in_s.get(subpath_src);
            if (paths_from_s == null) {
                paths_from_s = new HashSet<Path>(Collections.singleton(existing_subpath));
                paths_starting_in_s.put(subpath_src, paths_from_s);
            } else {
                paths_from_s.add(existing_subpath);
            }
        }

        // First create the required sets.
        // Saves us from checking for an existing set every time.
        Turn inturn_flow;
        Map<Pair<Path, Turn>, Set<Flow>> xtx_subpath_grouped_inturn_original = new HashMap<Pair<Path, Turn>, Set<Flow>>();

        for (Entry<Path, Set<Flow>> entry : xtx_subpath_grouped_original.entrySet()) {
            subpath_src = entry.getKey().getSource();

            for (Flow flow : entry.getValue()) {

                if (flow.getSource().equals(subpath_src)) {
                    continue;
                }

                inturn_flow = flow.getPath().getPrecedingTurn(subpath_src);
                Set<Flow> flows_inturn = new HashSet<Flow>();
                flows_inturn.add(flow);

                Pair<Path, Turn> key_pair = new Pair<Path, Turn>(entry.getKey(), inturn_flow);

                // .put returns the previous value associated with key,
                // or null if there was no mapping for key
                Set<Flow> prev_flows_inturn = xtx_subpath_grouped_inturn_original.put(key_pair, flows_inturn);

                if (prev_flows_inturn != null) {
                    flows_inturn.addAll(prev_flows_inturn);
                }
            }
        }

        Map<Set<Flow>, LinkedList<Path>> return_value = new HashMap<Set<Flow>, LinkedList<Path>>();

        Server path_snk = path.getSink();
        LinkedList<Path> prolonged_paths;
        for (Entry<Pair<Path, Turn>, Set<Flow>> entry : xtx_subpath_grouped_inturn_original.entrySet()) {
            Path current_subpath = entry.getKey().getKey();
            subpath_src = current_subpath.getSource();
            subpath_snk = current_subpath.getSink();
            if (subpath_snk.equals(path_snk)) { // No prolonging possible here.
                continue;
            }

            prolonged_paths = new LinkedList<Path>();
            prolonged_paths.add(current_subpath); // Original path is always first. Needed for the functionality above

            Set<Flow> entry_flows = entry.getValue();

            // What if the path prolonged to is empty due to prolongation of the flows in it?
            // At this point we do not know that as the actual prolongation has not been done yet.
            // This situation will thus be handled in the calling method where we construct
            // the interference patterns.
            for (Path potential_path : paths_starting_in_s.get(subpath_src)) {

                // Naturally, a prolonged subpath of path needs to be longer than the original one.
                if (potential_path.getServers().size() <= current_subpath.getServers().size()) {
                    continue;
                }

                // Are there flow coming via the same inturn so prolongation has an effect?
                if (xtx_subpath_grouped_inturn_original
                        .get(new Pair<Path, Turn>(potential_path, entry.getKey().getValue())) == null) {
                    continue;
                }

                // "fast" neglects the left-over operation that is the source of uncertainty we
                // previously used as an excuse for exhaustive stuff.
                // Maybe weigh with the flows' alpha and the length of their previously
                // traversed server graph

                prolonged_paths.add(potential_path);
            }

            if (prolonged_paths.size() > 1) { // If more than the original one were added
                return_value.put(entry_flows, prolonged_paths);
            }
        }

        return return_value;
    }

    private Set<ServiceCurve> getServiceCurvesStandard(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve)
            throws Exception {
        // Get cross-flows grouped as needed for the PMOO left-over service curve
        Set<Flow> flows_of_lower_priority = new HashSet<Flow>(flows_to_serve);
        flows_of_lower_priority.add(flow_of_interest);

        Set<Flow> cross_flows = server_graph.getFlows(path);
        cross_flows.removeAll(flows_of_lower_priority);
        Map<Path, Set<Flow>> xtx_subpath_grouped = server_graph.groupFlowsPerSubPath(path, cross_flows);

        if (xtx_subpath_grouped.isEmpty()) {
            return new HashSet<ServiceCurve>(Collections.singleton(path.getServiceCurve()));
        }

        return xtxSubpathBetas(flow_of_interest, path, xtx_subpath_grouped);
    }

    private Set<ServiceCurve> xtxSubpathBetas(Flow flow_of_interest, Path path,
                                              Map<Path, Set<Flow>> xtx_subpath_grouped) throws Exception {
        Set<ServiceCurve> betas_e2e = new HashSet<ServiceCurve>();

        // Derive the cross-flow substitutes with their arrival bound
        Set<List<Flow>> cross_flow_substitutes_set = new HashSet<List<Flow>>();
        cross_flow_substitutes_set.add(new LinkedList<Flow>());
        Set<List<Flow>> arrival_bounds_turn_permutations = new HashSet<List<Flow>>();

        for (Map.Entry<Path, Set<Flow>> entry : xtx_subpath_grouped.entrySet()) {
            // Create a single substitute flow
            // Name the substitute flow
            String substitute_flow_alias = "subst_{";
            for (Flow f : entry.getValue()) {
                substitute_flow_alias = substitute_flow_alias.concat(f.getAlias() + ",");
            }
            						// Remove trailing comma.
            substitute_flow_alias = substitute_flow_alias.substring(0, substitute_flow_alias.length() - 1); 
            substitute_flow_alias = substitute_flow_alias.concat("}");

            // Derive the substitute flow's arrival bound
            Set<ArrivalCurve> alphas_xf_group = ArrivalBoundDispatch.computeArrivalBounds(server_graph, configuration,
                    entry.getKey().getSource(), entry.getValue(), Flow.NULL_FLOW);
            // entry.getKey().getSource() because entry.getKey() is the common subpath of
            // path (above variable), i.e., start of interference on path.
            // We are leaving the flow_of_interest's path with this arrival bounding.
            // Therefore, worst-case arbitrary multiplexing cannot be modeled with
            // by assigning lowest prioritization to the flow of interest anymore (cf.
            // rejoining flows) and we call computeArrivalBounds with Flow.NULL_FLOW instead
            // of flow_of_interest.

            // Add the new bounds to the others by creating all the permutations.
            // * For ever arrival bound derived for a flow substitute
            // * take the existing flow substitutes (that belong to different subpaths)
            // * and "multiply" the cross_flow_substitutes_set, i.e., create a new set each.

            arrival_bounds_turn_permutations.clear();
            List<Flow> flow_list_tmp = new LinkedList<Flow>();
            for (ArrivalCurve alpha : alphas_xf_group) {
            	Curve.getUtils().beautify(alpha);

                for (List<Flow> f_subst_list : cross_flow_substitutes_set) {
                    // The new list of cross-flow substitutes = old list plus a new one with one of
                    // the derived arrival bounds.
                    flow_list_tmp.clear();
                    flow_list_tmp.addAll(f_subst_list);
                    flow_list_tmp.add(Flow.createDummyFlow(substitute_flow_alias, alpha, entry.getKey()));

                    // Add this list to the set of permutations
                    arrival_bounds_turn_permutations.add(new LinkedList<Flow>(flow_list_tmp));
                    // Prevent interaction with the clear() operation above.
                }
            }
            // Override cross_flow_substitutes_set for the next cross-flow substitute and
            // its arrival bounds.
            cross_flow_substitutes_set.clear();
            cross_flow_substitutes_set.addAll(arrival_bounds_turn_permutations);
            arrival_bounds_turn_permutations.clear();

            if (result.map__server__alphas.get(entry.getKey().getSource()) == null) {
                result.map__server__alphas.put(entry.getKey().getSource(), alphas_xf_group);
            } else {
                result.map__server__alphas.get(entry.getKey().getSource()).addAll(alphas_xf_group);
            }
        }

        // Derive the left-over service curves
        ServiceCurve null_service = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
        for (List<Flow> xtx_substitutes : cross_flow_substitutes_set) {
            ServiceCurve beta_e2e = PmooAnalysis.getServiceCurve(path, xtx_substitutes);

            if (!beta_e2e.equals(null_service)) {
                betas_e2e.add(beta_e2e); // Adding to the set, not adding up the curves
            }
        }

        if (betas_e2e.isEmpty()) {
            betas_e2e.add(Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
        }
        return betas_e2e;
    }

    public Set<ServiceCurve> getLeftOverServiceCurves() {
        return ((PmooResults) result).betas_e2e;
    }
}
