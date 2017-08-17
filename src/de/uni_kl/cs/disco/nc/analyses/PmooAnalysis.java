/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
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

package de.uni_kl.cs.disco.nc.analyses;

import org.apache.commons.math3.util.Pair;

import de.uni_kl.cs.disco.curves.*;
import de.uni_kl.cs.disco.nc.Analysis;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.ArrivalBound;
import de.uni_kl.cs.disco.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.disco.nc.operations.BacklogBound;
import de.uni_kl.cs.disco.nc.operations.DelayBound;
import de.uni_kl.cs.disco.network.*;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;
import de.uni_kl.cs.disco.numbers.NumUtils;

import java.util.*;
import java.util.Map.Entry;

public class PmooAnalysis extends Analysis {
    @SuppressWarnings("unused")
    private PmooAnalysis() {
    }

    public PmooAnalysis(Network network) {
        super(network);
        super.result = new PmooResults();
    }

    public PmooAnalysis(Network network, AnalysisConfig configuration) {
        super(network, configuration);
        super.result = new PmooResults();
    }

    /**
     * Concatenates the service curves along the given path <code>path</code>
     * according to the PMOO approach and returns the result.
     * <p>
     * It first decomposes all arrival curves (service curves) into token
     * buckets (rate latency curves), enumerates over all combinations of token
     * buckets and rate latency curves, and calls
     * <code>computePartialPMOOServiceCurve()</code> for each combination. The
     * total PMOO service curve is the maximum of all partial service curves.
     *
     * @param path                   The Path traversed for which a PMOO left-over service curve will be computed.
     * @param cross_flow_substitutes Flow substitutes according to PMOO's needs and abstracting from the actual cross-flows.
     * @return The PMOO service curve
     */
    public static ServiceCurve getServiceCurve(Path path, List<Flow> cross_flow_substitutes) {
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

        ServiceCurve beta_total = CurvePwAffineFactoryDispatch.createZeroService();

        boolean more_combinations = true;
        while (more_combinations) {
            // Compute service curve for this combination
            ServiceCurve beta = computePartialPMOOServiceCurve(path,
                    service_curves,
                    cross_flow_substitutes,
                    flow_tb_iter_map,
                    server_rl_iters);
            if (!beta.equals(CurvePwAffineFactoryDispatch.createZeroService())) {
                beta_total = CurvePwAffineUtilsDispatch.max(beta_total, beta);
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
     * Calculates the partial PMOO service curve for the given flow set by
     * combining all servers having an outgoing link contained in the given
     * link-path. For each flow considers only one of its token bucket
     * components (selected via the flow_tb_iter_map) and for each service curve
     * considers only one rate latency curve (selected via the server_rl_iters).
     *
     * @param path                   The tandem of servers the left-over service curve holds for.
     * @param service_curves         The service curves on the path.
     * @param cross_flow_substitutes Flows representing a group of segregated flows.
     * @param flow_tb_iter_map       Defines which token bucket component of a flow's arrival bound to use.
     * @param server_rl_iters        Defines which rate latency component of a server's service curve to use.
     * @return A partial PMOO service curve.
     */
    protected static ServiceCurve computePartialPMOOServiceCurve(Path path,
                                                                 ServiceCurve[] service_curves,
                                                                 List<Flow> cross_flow_substitutes,
                                                                 Map<Flow, Integer> flow_tb_iter_map,
                                                                 int[] server_rl_iters) {
        Num T = NumFactory.createZero();
        Num R = NumFactory.createPositiveInfinity();
        Num sum_bursts = NumFactory.createZero();
        Num sum_latencyterms = NumFactory.createZero();

        double sum_r_at_s;

        Set<Flow> present_flows = new HashSet<Flow>();
        for (Server s : path.getServers()) {
            sum_r_at_s = 0.0;
            int i = path.getServers().indexOf(s);
            // Add incoming flows
            for (Flow f : cross_flow_substitutes) {
                if (f.getPath().getServers().contains(s)) { // The exact path of the substitute does not matter, only the shared servers with the flow of interest do
                    present_flows.add(f);
                    sum_r_at_s += f.getArrivalCurve().getUltAffineRate().doubleValue();
                }
            }

            // Check for stability constraint violation
            if (sum_r_at_s >= s.getServiceCurve().getUltAffineRate().doubleValue()) {
                return CurvePwAffineFactoryDispatch.createZeroService();
            }

            // TODO Actually needs to be an affine curve
            CurvePwAffine current_rl = service_curves[i].getRL_Component(server_rl_iters[i]);

            // Sum up latencies
            T = NumUtils.add(T, current_rl.getLatency());

            // Compute and store sum of rates of all passing flows
            Num sum_r = NumFactory.createZero();
            for (Flow f : present_flows) {
                ArrivalCurve bound = f.getArrivalCurve();
                // TODO Actually needs to be an affine curve
                CurvePwAffine current_tb = bound.getTB_Component(((Integer) flow_tb_iter_map.get(f)).intValue());
                sum_r = NumUtils.add(sum_r, current_tb.getUltAffineRate());
            }

            // Update latency terms (increments)
            sum_latencyterms = NumUtils.add(sum_latencyterms, NumUtils.mult(sum_r, current_rl.getLatency()));

            // Compute left-over rate; update min
            Num Ri = NumUtils.sub(current_rl.getUltAffineRate(), sum_r);
            if (Ri.leqZero()) {
                return CurvePwAffineFactoryDispatch.createZeroService();
            }
            R = NumUtils.min(R, Ri);

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
            // TODO Actually needs to be an affine curve
            CurvePwAffine current_tb = bound.getTB_Component(((Integer) flow_tb_iter_map.get(f)).intValue());
            sum_bursts = NumUtils.add(sum_bursts, current_tb.getTB_Burst());
        }

        T = NumUtils.add(T, NumUtils.div(NumUtils.add(sum_bursts, sum_latencyterms), R));

        if (T == NumFactory.getPositiveInfinity()) {
            return CurvePwAffineFactoryDispatch.createZeroService();
        }
        if (R == NumFactory.getPositiveInfinity()) {
            return CurvePwAffineFactoryDispatch.createDelayedInfiniteBurst(T);
        }

        return CurvePwAffineFactoryDispatch.createRateLatency(R, T);
    }

    /**
     * Performs a pay-multiplexing-only-once (PMOO) analysis for the <code>flow_of_interest</code>.
     *
     * @param flow_of_interest the flow for which the end-to-end service curve shall be computed.
     */
    public void performAnalysis(Flow flow_of_interest) throws Exception {
        performAnalysis(flow_of_interest, flow_of_interest.getPath());
    }

    public void performAnalysis(Flow flow_of_interest, Path path) throws Exception {
        if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO) {
            throw new Exception("PMOO analysis is not available for FIFO multiplexing nodes");
        }

        ((PmooResults) result).betas_e2e = getServiceCurves(flow_of_interest, path, Collections.singleton(flow_of_interest));

        Num delay_bound__beta_e2e;
        Num backlog_bound__beta_e2e;

        ((PmooResults) result).setDelayBound(NumFactory.createPositiveInfinity());
        ((PmooResults) result).setBacklogBound(NumFactory.createPositiveInfinity());

        for (ServiceCurve beta_e2e : ((PmooResults) result).betas_e2e) {
            delay_bound__beta_e2e = DelayBound.deriveFIFO(flow_of_interest.getArrivalCurve(), beta_e2e); // Single flow of interest, i.e., fifo per micro flow holds
            if (delay_bound__beta_e2e.leq(result.getDelayBound())) {
                ((PmooResults) result).setDelayBound(delay_bound__beta_e2e);
            }

            backlog_bound__beta_e2e = BacklogBound.derive(flow_of_interest.getArrivalCurve(), beta_e2e);
            if (backlog_bound__beta_e2e.leq(result.getBacklogBound())) {
                ((PmooResults) result).setBacklogBound(backlog_bound__beta_e2e);
            }
        }
    }

    public Set<ServiceCurve> getServiceCurves(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve) throws Exception {
        if (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL) {
            for (Server s : path.getServers()) {
                if (s.multiplexingDiscipline() == AnalysisConfig.Multiplexing.FIFO) {
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

    private Set<ServiceCurve> getServiceCurvesFP(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve) throws Exception {
        // Get cross-flows grouped as needed for the PMOO left-over service curve
        Set<Flow> flows_of_lower_priority = new HashSet<Flow>(flows_to_serve);
        flows_of_lower_priority.add(flow_of_interest);

        Set<Flow> cross_flows = network.getFlows(path);
        cross_flows.removeAll(flows_of_lower_priority);

        Map<Path, Set<Flow>> xtx_subpath_grouped_original = network.groupFlowsPerSubPath(path, cross_flows);
        Map<Set<Flow>, LinkedList<Path>> prolongations = getProlongationsToSubpaths(path, xtx_subpath_grouped_original);

        if (prolongations.isEmpty()) {
            return xtxSubpathBetas(flow_of_interest, path, xtx_subpath_grouped_original);
        }

        // Create the set of cross-flow groupings, starting from the one for unprolonged flows.
        Set<Map<Path, Set<Flow>>> xtx_subpath_grouped_incl_prolongation = new HashSet<Map<Path, Set<Flow>>>();
        xtx_subpath_grouped_incl_prolongation.add(xtx_subpath_grouped_original);


        // General way it works:
        // Permutations of all alternatives (see the code below ):
        //  - iterate over all the sets above
        //  - iterate over the results set
        //  - merge into a temporary results set (prevents concurrent modification exception)
        //  - replace results set with temporary one
        //  - start over with the outer iteration

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
            // Start counting at 1 because the first element (at position 0) is the original interference path.
            for (int i = 1; i <= prolongable_paths.size() - 1; i++) {
                common_path_new = prolongable_paths.get(i);

                // Each potential move of a flow generates a new set of xtx_subpath_groupings .
                // They are merged into a temporary results set to prevent concurrent modification exceptions.
                // Yes, we need a set of mappings because each mapping will be a unique interference patter no derive a left-over beta for.
                Set<Map<Path, Set<Flow>>> xtx_subpath_grouped_incl_prolongation_tmp = new HashSet<Map<Path, Set<Flow>>>();

                for (Map<Path, Set<Flow>> interference_pattern : xtx_subpath_grouped_incl_prolongation) { // At the beginning, only the original interference pattern is in here.
                    // Prevent the original from getting lost because its sink is not part of xf_prolongations's Server list.
                    xtx_subpath_grouped_incl_prolongation_tmp.add(interference_pattern);

                    // The general idea is to copy the map and modify the two mappings of interest only.
                    // Yet, the copy we need is deeper than constructing a new map or using clone()-functionality.
                    // We need a new map where a) the key can be the same path object as in to old map and
                    //						   b) the value is a new map with the old flow objects.
                    // Constructing this copy will require a loop -- the following loop we use for an "in situ" modification we need.
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
                            if (value_flows.isEmpty()) { // Prolonging to an empty set will not cause aggregation effects.
                                add = false;
                                continue;
                            }

                            boolean aggr_potential = false;
                            subpaths_src = common_path_new.getSource();
                            Link inlink_xfs = xfs.iterator().next().getPrecedingLink(subpaths_src);
                            Link inlink_subpath_flows;
                            for (Flow f : value_flows) {
                                try {
                                    inlink_subpath_flows = f.getPrecedingLink(subpaths_src);
                                    if (inlink_subpath_flows.equals(inlink_xfs)) {
                                        aggr_potential = true;
                                        continue;
                                    }
                                } catch (Exception e) {
                                } // There's an exception thrown by getPrecedingLink if f is originating in subpaths_src
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
            betas_e2e.add(CurvePwAffineFactoryDispatch.createZeroService());
        }
        return betas_e2e;
    }

    private Map<Set<Flow>, LinkedList<Path>> getProlongationsToSubpaths(Path path, Map<Path, Set<Flow>> xtx_subpath_grouped_original) throws Exception {
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

        // First create the required sets. Saves us from checking for an existing set every time.
        Link inlink_flow;
        Map<Pair<Path, Link>, Set<Flow>> xtx_subpath_grouped_inlink_original = new HashMap<Pair<Path, Link>, Set<Flow>>();

        for (Entry<Path, Set<Flow>> entry : xtx_subpath_grouped_original.entrySet()) {
            subpath_src = entry.getKey().getSource();

            for (Flow flow : entry.getValue()) {

                if (flow.getSource().equals(subpath_src)) {
                    continue;
                }

                inlink_flow = flow.getPath().getPrecedingLink(subpath_src);
                Set<Flow> flows_inlink = new HashSet<Flow>();
                flows_inlink.add(flow);

                Pair<Path, Link> key_pair = new Pair<Path, Link>(entry.getKey(), inlink_flow);

                // .put returns the previous value associated with key, or null if there was no mapping for key
                Set<Flow> prev_flows_inlink = xtx_subpath_grouped_inlink_original.put(key_pair, flows_inlink);

                if (prev_flows_inlink != null) {
                    flows_inlink.addAll(prev_flows_inlink);
                }
            }
        }

        Map<Set<Flow>, LinkedList<Path>> return_value = new HashMap<Set<Flow>, LinkedList<Path>>();

        Server path_snk = path.getSink();
        LinkedList<Path> prolonged_paths;
        for (Entry<Pair<Path, Link>, Set<Flow>> entry : xtx_subpath_grouped_inlink_original.entrySet()) {
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
            // This situation will thus be handled in the calling method where we construct the interference patterns.
            for (Path potential_path : paths_starting_in_s.get(subpath_src)) {

                // Naturally, a prolonged subpath of path needs to be longer than the original one.
                if (potential_path.getServers().size() <= current_subpath.getServers().size()) {
                    continue;
                }

                // Are there flow coming via the same inlink so prolongation has an effect?
                if (xtx_subpath_grouped_inlink_original.get(new Pair<Path, Link>(potential_path, entry.getKey().getValue()))
                        == null) {
                    continue;
                }

                // "fast" neglects the left-over operation that is the source of uncertainty we previously used as an excuse for exhaustive stuff.
                // Maybe weigh with the flows' alpha and the length of their previously traversed network

                prolonged_paths.add(potential_path);
            }

            if (prolonged_paths.size() > 1) { // If more than the original one were added
                return_value.put(entry_flows, prolonged_paths);
            }
        }

        return return_value;
    }

    private Set<ServiceCurve> getServiceCurvesStandard(Flow flow_of_interest, Path path, Set<Flow> flows_to_serve) throws Exception {
        // Get cross-flows grouped as needed for the PMOO left-over service curve
        Set<Flow> flows_of_lower_priority = new HashSet<Flow>(flows_to_serve);
        flows_of_lower_priority.add(flow_of_interest);

        Set<Flow> cross_flows = network.getFlows(path);
        cross_flows.removeAll(flows_of_lower_priority);
        Map<Path, Set<Flow>> xtx_subpath_grouped = network.groupFlowsPerSubPath(path, cross_flows);

        if (xtx_subpath_grouped.isEmpty()) {
            return new HashSet<ServiceCurve>(Collections.singleton(path.getServiceCurve()));
        }

        return xtxSubpathBetas(flow_of_interest, path, xtx_subpath_grouped);
    }

    private Set<ServiceCurve> xtxSubpathBetas(Flow flow_of_interest, Path path, Map<Path, Set<Flow>> xtx_subpath_grouped) throws Exception {
        Set<ServiceCurve> betas_e2e = new HashSet<ServiceCurve>();

        // Derive the cross-flow substitutes with their arrival bound
        Set<List<Flow>> cross_flow_substitutes_set = new HashSet<List<Flow>>();
        cross_flow_substitutes_set.add(new LinkedList<Flow>());
        Set<List<Flow>> arrival_bounds_link_permutations = new HashSet<List<Flow>>();

        for (Map.Entry<Path, Set<Flow>> entry : xtx_subpath_grouped.entrySet()) {
            // Create a single substitute flow
            // Name the substitute flow
            String substitute_flow_alias = "subst_{";
            for (Flow f : entry.getValue()) {
                substitute_flow_alias = substitute_flow_alias.concat(f.getAlias() + ",");
            }
            substitute_flow_alias = substitute_flow_alias.substring(0, substitute_flow_alias.length() - 1); // Remove trailing comma.
            substitute_flow_alias = substitute_flow_alias.concat("}");

            // Derive the substitute flow's arrival bound
            Set<ArrivalCurve> alphas_xf_group = ArrivalBound.computeArrivalBounds(network, configuration, entry.getKey().getSource(), entry.getValue(), Flow.NULL_FLOW);
            // entry.getKey().getSource() because entry.getKey() is the common subpath of path (above variable), i.e., start of interference on path.
            // We are leaving the flow_of_interest's path with this arrival bounding. Therefore, worst-case arbitrary multiplexing cannot be modeled with
            //   by assigning lowest prioritization to the flow of interest anymore (cf. rejoining flows) and we call computeArrivalBounds with Flow.NULL_FLOW instead of flow_of_interest.

            // Add the new bounds to the others by creating all the permutations.
            // * For ever arrival bound derived for a flow substitute
            // * take the existing flow substitutes (that belong to different subpaths)
            // * and "multiply" the cross_flow_substitutes_set, i.e., create a new set each.

            arrival_bounds_link_permutations.clear();
            List<Flow> flow_list_tmp = new LinkedList<Flow>();
            for (ArrivalCurve alpha : alphas_xf_group) {
                CurvePwAffineUtilsDispatch.beautify(alpha);

                for (List<Flow> f_subst_list : cross_flow_substitutes_set) {
                    // The new list of cross-flow substitutes = old list plus a new one with one of the derived arrival bounds.
                    flow_list_tmp.clear();
                    flow_list_tmp.addAll(f_subst_list);
                    flow_list_tmp.add(Flow.createDummyFlow(substitute_flow_alias, alpha, entry.getKey()));

                    // Add this list to the set of permutations
                    arrival_bounds_link_permutations.add(new LinkedList<Flow>(flow_list_tmp)); // Prevent interaction with the clear() operation above.
                }
            }
            // Override cross_flow_substitutes_set for the next cross-flow substitute and its arrival bounds.
            cross_flow_substitutes_set.clear();
            cross_flow_substitutes_set.addAll(arrival_bounds_link_permutations);
            arrival_bounds_link_permutations.clear();

            if (result.map__server__alphas.get(entry.getKey().getSource()) == null) {
                result.map__server__alphas.put(entry.getKey().getSource(), alphas_xf_group);
            } else {
                result.map__server__alphas.get(entry.getKey().getSource()).addAll(alphas_xf_group);
            }
        }

        // Derive the left-over service curves
        ServiceCurve null_service = CurvePwAffineFactoryDispatch.createZeroService();
        for (List<Flow> xtx_substitutes : cross_flow_substitutes_set) {
            ServiceCurve beta_e2e = PmooAnalysis.getServiceCurve(path, xtx_substitutes);

            if (!beta_e2e.equals(null_service)) {
                betas_e2e.add(beta_e2e); // Adding to the set, not adding up the curves
            }
        }

        if (betas_e2e.isEmpty()) {
            betas_e2e.add(CurvePwAffineFactoryDispatch.createZeroService());
        }
        return betas_e2e;
    }

    public Set<ServiceCurve> getLeftOverServiceCurves() {
        return ((PmooResults) result).betas_e2e;
    }
}