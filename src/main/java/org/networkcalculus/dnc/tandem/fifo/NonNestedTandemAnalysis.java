package org.networkcalculus.dnc.tandem.fifo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.bounds.disco.pw_affine.LeftOverService_Disco_PwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.curves.disco.pw_affine.Curve_Disco_PwAffine;
import org.networkcalculus.dnc.feedforward.ArrivalBoundDispatchFIFO;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.num.Num;

/**
 *
 * @author Alexander Scheffler
 *
 */
public class NonNestedTandemAnalysis {
    // The path of a network to be analyzed, equals foi.getPath()
    private final Path foi_path;
    // Flow of interest for which the left over service curve needs to be computed
    private final Flow foi;
    // The flows on the path including foi. They need to meet the requirements mentioned in computeNestingSets method (see Nested class).
    // (Aggregating / Arrival bounding already done)
    private final ArrayList<Flow> flows;
    // entry (i,j) contains "the" flow (i,j) if it exists, otherwise null
    private Flow[][] flowMatrix;
    // matrix shows which flow dependencies are severed by cutting at the respective node (= row), order of flow dependencies is the same as in the list
    private boolean[][] dependencyMatrix;
    // list of interdependent flows
    private ArrayList<Pair> flowDependencies;
    // valid sets of cuts --- every set of cuts in vsc severs all flow dependencies
    private ArrayList<ArrayList<Integer>> vsc;
    // primary sets of cuts --- vsc \ "unnecessary" sets of cuts (if there is a set of cuts with one cut less and the other cuts are the same)
    private ArrayList<ArrayList<Integer>> psc;
    // number of nodes on the foi path
    private int num;
    // local enumeration of servers
    private HashMap<Server, Integer> server_numbers;
    private HashMap<Integer, Server> server_numbers_reversed_map;
    private final ServerGraph server_graph;
    private final AnalysisConfig configuration;
    // the actual cross-flows (not the substitutes)
    private final Map<Path,Set<Flow>> xtx_subpath_grouped;
    private Map<Flow, Set<Flow>> xtx_substitutes_to_actual_flows;
    private Map<Path, ArrivalCurve> foi_arrival_bound_at_subtandem;
    // current subtandems of one set of cuts
    private ArrayList<Path> curr_subtandems;

    private final boolean solve_for_output_opt;


    // flows need to include flow_of_interest
    // flows are flow substitutes (dummy flows) --- flow_of_interest can be real flow though
    public NonNestedTandemAnalysis(ServerGraph server_graph, AnalysisConfig configuration, Path tandem, Flow flow_of_interest, List<Flow> flows, Map<Path,Set<Flow>> xtx_subpath_grouped, boolean solve_for_output_opt )
    {
        this.foi_path = tandem;
        this.foi = flow_of_interest;
        this.flows = new ArrayList<Flow>( flows );
        this.server_graph = server_graph;
        this.configuration = configuration;
        this.xtx_subpath_grouped = xtx_subpath_grouped;
        this.solve_for_output_opt = solve_for_output_opt;
        computeSubstitutesToActualFlows();
    }

    private void computeSubstitutesToActualFlows()
    {
        Set<Flow> crossflows = new HashSet<Flow>(flows);
        crossflows.remove(foi);
        xtx_substitutes_to_actual_flows = new HashMap<Flow, Set<Flow>>();
        for(Flow xtx_flow : crossflows)
        {
            Path xtx_path = xtx_flow.getPath();
            xtx_substitutes_to_actual_flows.put(xtx_flow, xtx_subpath_grouped.get(xtx_path));
        }

    }

    // same assumptions as in NestedTandemAnalysis
    public ServiceCurve getServiceCurve() throws Exception
    {
        // Map enumerates servers on the foi path.
        server_numbers = new HashMap<Server, Integer>();
        server_numbers_reversed_map = new HashMap<Integer, Server>();
        LinkedList<Server> servers_on_foi_path = foi.getServersOnPath();
        // needs to start from 0 because we use those numbers for indexing in the matrix
        num = 0;
        for(Server server : servers_on_foi_path)
        {
            server_numbers_reversed_map.put(num, server);
            server_numbers.put(server, num);
            num++;
        }

        // all entries initially null
        flowMatrix = new Flow[num][num];

        // construction of flowMatrix
        // note that it is no problem to skip crossflows with path == foi.path since the flow matrix is only used to build flowDependencies and to determine the sets of cuts.
        // More specifically, those flows (and foi) are not responsible for any flow dependencies in the current non-nested tandem anyway.
        for(Flow flow : flows)
        {
            if(flow!= foi && flow.getPath().equals(foi.getPath()))
            {
                continue;
            }
            int source_num =  server_numbers.get(flow.getSource());
            int sink_num =  server_numbers.get(flow.getSink());
            flowMatrix[source_num][sink_num] = flow;
        }

        // construction of flow dependencies list and dependency matrix
        flowDependencies = new ArrayList<Pair>();
        for(Flow flow : flows)
        {
            if(flow!= foi && flow.getPath().equals(foi.getPath()))
            {
                continue;
            }

            int source_num =  server_numbers.get(flow.getSource());
            int sink_num =  server_numbers.get(flow.getSink());
            // relevant search space: [0, source_num - 1] x [source_num, sink_num - 1] "and" [source_num + 1, sink_num] x [sink_num + 1, num - 1]
            // "[0, source_num - 1] x [source_num, sink_num - 1]" already found by corresponding flow

            for(int i = source_num + 1; i <= sink_num; i++)
            {
                for(int j = sink_num + 1; j <= num - 1; j++)
                {
                    if(flowMatrix[i][j]!= null)
                    {
                        // Flows (i,j) and flow are interdependent
                        flowDependencies.add(new Pair(flow, flowMatrix[i][j]));
                    }
                }
            }
        }

        // dependency matrix, all entries initially false
        dependencyMatrix = new boolean[num][flowDependencies.size()];
        for(Pair flow_dependency : flowDependencies)
        {
            Flow f1 = (Flow) flow_dependency.getFirst();
            Flow f2 = (Flow) flow_dependency.getSecond();
            int source_2 = server_numbers.get(f2.getSource());
            int sink_1 = server_numbers.get(f1.getSink());
            int column_index =  flowDependencies.indexOf(flow_dependency);
            // from construction of flowDependencies, we can conclude that source_1 < source_2 <= sink_1 < sink_2 holds
            // dependency is resolved by cutting at any node in [source_2, sink_1 + 1]
            for(int i=source_2; i <= sink_1 + 1; i++)
            {
                dependencyMatrix[i][column_index] = true;
            }
        }
        computeValidSetsOfCutsInit();
        computePrimarySetsOfCuts();



        ArrayList<ArrayList<Integer>> sc_min_cuts = new ArrayList<>();

        if(!solve_for_output_opt)
        {
            // compute delay bound for every sc in psc and return the left over service curve with min delay
            Num min_delay = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();
            ServiceCurve left_over_with_min_delay = Curve.getFactory().createZeroService();

            for(ArrayList<Integer> sc : psc) {
                // reset for every set of cuts
                foi_arrival_bound_at_subtandem = new HashMap<Path, ArrivalCurve>();
                Pair pair = computeDelayandLeftOverForSC(sc);
                Num delay = (Num) pair.getFirst();
                ServiceCurve leftover = (ServiceCurve) pair.getSecond();


                if (delay.leq(min_delay)) {
                    min_delay = delay;
                    left_over_with_min_delay = leftover;
                }
            }
            return left_over_with_min_delay;
        }

        else{
            // compute latency for every sc in psc and return the left over service curve with min latency
            Num min_latency = Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity();
            ServiceCurve left_over_with_min_latency = Curve.getFactory().createZeroService();
            for(ArrayList<Integer> sc : psc) {
                foi_arrival_bound_at_subtandem = new HashMap<Path, ArrivalCurve>();
                Pair pair = computeDelayandLeftOverForSC(sc);

                ServiceCurve leftover = (ServiceCurve) pair.getSecond();
                Num latency = leftover.getLatency();

                if (latency.leq(min_latency)) {
                    min_latency = latency;
                    left_over_with_min_latency = leftover;
                }
            }
            return left_over_with_min_latency;
        }
    }

    private Pair computeDelayandLeftOverForSC(ArrayList<Integer> sc) throws Exception {

        Num delay_sc = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        // neutral element concerning convolution
        ServiceCurve leftover_sc = Curve.getFactory().createZeroDelayInfiniteBurst();
        // create and store subtandems for this set of cuts
        // sc.size >= 1 (otherwise it would be a nested tandem)
        ArrayList<Path> subtandems = new ArrayList<>();
        try {
            Path first_subtandem = foi_path.getSubPath(foi.getSource(), foi_path.getPrecedingServer(server_numbers_reversed_map.get(sc.get(0))));
            subtandems.add(first_subtandem);
            foi_arrival_bound_at_subtandem.put(first_subtandem, foi.getArrivalCurve());
            for (int i = 0; i < sc.size(); i++) {
                Server from = server_numbers_reversed_map.get(sc.get(i));
                if (i + 1 == sc.size()) {
                    // we don't store the sink of foi in sc
                    Server to = foi.getSink();
                    subtandems.add(foi_path.getSubPath(from, to));
                } else {
                    Server to = foi_path.getPrecedingServer(server_numbers_reversed_map.get(sc.get(i + 1)));
                    subtandems.add(foi_path.getSubPath(from, to));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // reset for every set of cuts
        curr_subtandems = new ArrayList<>(subtandems);

        // Basically STA: Cut, compute arrival bounds for crossflows at each cut but do a single nested tandem analysis over the whole tandem afterwards
        HashMap<Path, Set<Flow>> subtandem_to_xf_substitutes = new HashMap();
        Set<Flow> all_xf_substitutes = new HashSet<>();
        for(Path subtandem : subtandems)
        {
            Set<Flow> xf_subtitutes = createXfSubstitutesForSubtandem(subtandem);
            subtandem_to_xf_substitutes.put(subtandem, xf_subtitutes);
            all_xf_substitutes.addAll(xf_subtitutes);
        }

        // Because we have a non-nested tandem, we have at least one cut. In this "STA" view, no xf can have the same path as foi
        Flow foi_substitute = Flow.createDummyFlow("foi_sub_{"+foi.getAlias()+"}", foi.getArrivalCurve(), foi.getPath()); // Do we even need foi substitute?
        all_xf_substitutes.add(foi_substitute);
        if(!solve_for_output_opt)
        {
            leftover_sc = new NestedTandemAnalysis(foi.getPath(), foi_substitute, all_xf_substitutes).getServiceCurve();
            delay_sc = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi_substitute.getArrivalCurve(), leftover_sc);
        }

        else{

            NestedTandemAnalysis ludb_nested = new NestedTandemAnalysis( foi.getPath(), foi_substitute, all_xf_substitutes, configuration);
            TNode root = ludb_nested.onlyComputeNestingTree();
            ArrayList<TNode> foi_children = root.getChildren();
            leftover_sc = Curve.getFactory().createZeroDelayInfiniteBurst();

            for(TNode child : foi_children)
            {
                if (child.getInf() instanceof LinkedList) {
                    // node is a t-leaf, i.e. represents C_(h,k)
                    LinkedList<Server> servers = (LinkedList<Server>) child.getInf();
                    for (Server server : servers) {
                        leftover_sc = Calculator.getInstance().getMinPlus().convolve(leftover_sc, server.getServiceCurve());
                    }
                } else {
                    // node is a t-node, i.e. represents a flow (h,k)
                    // This flow child will be foi in a new nested analysis, but make sure the analysis only takes into account the flows that are nested into that one; Then for that flow l.o. take curr lb to get foi l.o. on that subpath
                    Flow foi_child_flow = (Flow) child.getInf();
                    LinkedList<Server> servers_on_foi_child_path = foi_child_flow.getPath().getServers();
                    Set<Flow> flows_nested_into_foi_child = new HashSet<>();
                    for (Flow flow : all_xf_substitutes) {
                        // Can't use ServerGraphs getFlowsPerSubPath method because we deal with substitute flows here...
                        Server src = flow.getSource();
                        Server dest = flow.getSink();
                        if(servers_on_foi_child_path.contains(src) && servers_on_foi_child_path.contains(dest) )
                        {
                            // flow is nested into foi_child_flow (might be even that flow itself)
                            flows_nested_into_foi_child.add(flow);
                        }
                    }


                    NestedTandemAnalysis ludb_nested_child = new NestedTandemAnalysis( foi_child_flow.getPath(), foi_child_flow, flows_nested_into_foi_child, configuration);
                    ServiceCurve beta_lo_child = ludb_nested_child.getServiceCurve();

                    ArrivalCurve ac = foi_child_flow.getArrivalCurve();
                    ServiceCurve sc_foi_child = beta_lo_child;
                    Num burst = ac.getBurst();

                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc_foi_child;

                    Num theta_curr_lb = curve.f_inv(burst);
                    ServiceCurve subtandem_foi_lo = LeftOverService_Disco_PwAffine.fifoMux(beta_lo_child, foi_child_flow.getArrivalCurve(), theta_curr_lb);
                    leftover_sc = Calculator.getInstance().getMinPlus().convolve(leftover_sc, subtandem_foi_lo);
                }
            }
            delay_sc = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi_substitute.getArrivalCurve(), leftover_sc);
        }
        return new Pair(delay_sc, leftover_sc);
    }

    // Computes arrival bound for crossflow in the subtandem and creates respective substitute flows
    private Set<Flow> createXfSubstitutesForSubtandem(Path subtandem) throws Exception
    {
        Set<Flow> cross_flows = new HashSet<Flow>(flows);
        cross_flows.remove(foi);
        // cross flows on tandem (without foi)
        ArrayList<Flow> cross_flows_tandem = new ArrayList<>();
        Map<Path, Set<Flow>> path_flows = new HashMap<Path, Set<Flow>>();
        // need to determine the flows in cross_flows that have an intersection with subtandem
        for(Flow flow : cross_flows)
        {
            int flow_source_nr = server_numbers.get(flow.getSource());
            int flow_sink_nr = server_numbers.get(flow.getSink());
            int subtandem_source_nr = server_numbers.get(subtandem.getSource());
            int subtandem_sink_nr = server_numbers.get(subtandem.getSink());
            if( !(flow_source_nr > subtandem_sink_nr) && !(flow_sink_nr < subtandem_source_nr)  )
            {
                cross_flows_tandem.add(flow);
                // store this flow with the respective path that crosses the subtandem
                int flow_subtandem_source_nr = flow_source_nr;
                int flow_subtandem_sink_nr = flow_sink_nr;
                if(flow_source_nr < subtandem_source_nr)
                {
                    flow_subtandem_source_nr = subtandem_source_nr;
                }

                if(flow_sink_nr > subtandem_sink_nr)
                {
                    flow_subtandem_sink_nr = subtandem_sink_nr;
                }
                try
                {
                    Path flow_path_on_subtandem = foi_path.getSubPath(server_numbers_reversed_map.get(flow_subtandem_source_nr), server_numbers_reversed_map.get(flow_subtandem_sink_nr));
                    if(path_flows.get(flow_path_on_subtandem) == null)
                    {
                        path_flows.put(flow_path_on_subtandem, new HashSet<>(Collections.singleton(flow)));
                    }

                    else
                    {
                        path_flows.get(flow_path_on_subtandem).add(flow);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }


        // compute arrival bound for flows with the same path in the subtandem to avoid enforced segregation.
        Set<Flow> cross_flows_substitutes = new HashSet<Flow>();
        Flow xtx_with_subtandem_as_path_substitute = null; // indicates if there is at least one cross flow with subtandem as subpath
        for(Map.Entry<Path, Set<Flow>> entry : path_flows.entrySet())
        {
            Path flows_path_on_subtandem = entry.getKey();
            Set<Flow> flows_with_that_path = entry.getValue();
            ArrivalCurve aggregated_arrival_curve = Curve.getFactory().createZeroArrivals();


            Set<Flow> real_flows_with_that_path = new HashSet<>(); // in order to call computeArrivalBounds (must be real flows, not flow substitutes)
            for(Flow flow : flows_with_that_path)
            {
                real_flows_with_that_path.addAll(xtx_substitutes_to_actual_flows.get(flow));
            }

            try{
                if(flows_path_on_subtandem.equals(subtandem)) {
                    // special case: some crossflows have the same path as foi in the subtandem
                    // aggregation including foi not advisable since the left over service curves of the subtandems are convolved and then the output bound is computed.
                    // Hence, the left over for these crossflows (excluding foi) is computed which is then used to get foi's left over.

                    String xtx_alias = "sub_{";
                    for(Flow flow : real_flows_with_that_path)
                    {
                        xtx_alias += flow.getAlias() + ", ";
                    }
                    xtx_alias = xtx_alias.substring(0, xtx_alias.length() - 2);
                    xtx_alias += "}";


                    aggregated_arrival_curve = ArrivalBoundDispatchFIFO.computeArrivalBound(server_graph, configuration, flows_path_on_subtandem.getSource(), real_flows_with_that_path);
                    xtx_with_subtandem_as_path_substitute = Flow.createDummyFlow( xtx_alias , aggregated_arrival_curve, flows_path_on_subtandem);
                    cross_flows_substitutes.add(xtx_with_subtandem_as_path_substitute);
                }

                else
                {
                    aggregated_arrival_curve = ArrivalBoundDispatchFIFO.computeArrivalBound(server_graph, configuration, flows_path_on_subtandem.getSource(), real_flows_with_that_path);
                    String xtx_alias = "sub_{";
                    for(Flow flow : real_flows_with_that_path)
                    {
                        xtx_alias += flow.getAlias() + ", ";
                    }
                    xtx_alias = xtx_alias.substring(0, xtx_alias.length() - 2);
                    xtx_alias += "}";
                    cross_flows_substitutes.add(Flow.createDummyFlow(xtx_alias, aggregated_arrival_curve, flows_path_on_subtandem));
                }
            }

            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return cross_flows_substitutes;
    }


    private void computeValidSetsOfCutsInit()
    {
        vsc = new ArrayList<ArrayList<Integer>>();
        // node 1 (servernumber 0) is always in the cut implicitly --- we don't store it though due to check "{k-1, k-2} isSubset curr_set_of_cuts"
        for(int x = 1; x < num; x++)
        {
            // check if we can "ignore" x, i.e. if x does not resolve any dependencies at all
            // dependencies resolved by a cut at node x
            ArrayList<Pair> curr_dependencies = new ArrayList<Pair>();
            boolean[] x_row = dependencyMatrix[x];
            // assume x_row.length >= 1 (since we have at least one flow dependency)
            boolean containsAll = true;
            for(int i = 0; i < x_row.length; i++)
            {
                if(x_row[i])
                {
                    // dependencyMatrix and flowDependencies have the same order of dependencies!
                    curr_dependencies.add(flowDependencies.get(i));
                }
                else
                {
                    containsAll = false;
                }
            }

            if(curr_dependencies.size()==0)
            {
                continue;
            }

            // did not skip node x
            ArrayList<Integer> x_list = new ArrayList<Integer>();
            x_list.add(x);

            if(containsAll)
            {
                // no need to add further nodes to the cut since node x severs all dependencies, so further nodes in the cut would worsen the result
                vsc.add(x_list);
            }

            else
            {
                computeValidSetsOfCuts(x_list,  curr_dependencies, x);
            }

        }
    }

    // Note: Dependencies in curr_dependencies are stored in an arbitrary fashion (unlike flowDependencies and dependencyMatrix)
    private void computeValidSetsOfCuts(ArrayList<Integer> curr_set_of_cuts, ArrayList<Pair> curr_dependencies, int x)
    {
        // k >= x + 1 >= 2 => can check "{k-1, k-2} isSubset curr_set_of_cuts". Note that we don't store node 1 (servernr 0) in curr_set_of_cuts because in general,
        // we still have to consider cutting at node 2 (servernr 1) and node 3 (servernr 2).
        for(int k = x+1; k < num; k++)
        {
            // check if D_k isSubset curr_dependencies
            boolean[] k_row = dependencyMatrix[k];
            ArrayList<Pair> k_dependencies = new ArrayList<Pair>();
            for(int i = 0; i < k_row.length; i++)
            {
                if(k_row[i])
                {
                    // dependencyMatrix and flowDependencies have the same order of dependencies!
                    k_dependencies.add(flowDependencies.get(i));
                }
            }
            if(curr_dependencies.containsAll(k_dependencies) || (curr_set_of_cuts.contains(k-2) && curr_set_of_cuts.contains(k-1)))
            {
                // "k_dependencies isSubset curr_dependencies": no need to add node k to curr_set_of_cuts since it does not resolve "new" dependencies
                // "{k-1, k-2} isSubset curr_set_of_cuts": no cutting at three succeeding nodes
                continue;
            }

            // did not skip node k
            ArrayList<Integer> curr_set_of_cuts_new = (ArrayList<Integer>)curr_set_of_cuts.clone();
            // add node k to curr_set_of_cuts_new
            curr_set_of_cuts_new.add(k);
            ArrayList<Pair> curr_dependencies_new = (ArrayList<Pair>)curr_dependencies.clone();

            // add k_dependencies to curr_dependencies_new
            for(Pair k_dependency : k_dependencies)
            {
                if(!curr_dependencies_new.contains(k_dependency))
                {
                    curr_dependencies_new.add(k_dependency);
                }
            }

            if(curr_dependencies_new.containsAll(flowDependencies))
            {
                // set of (curr_dependencies_new) = set of (flowDependencies), so curr_set_of_cuts_new resolves all flow dependencies
                // add curr_set_of_cuts_new to the valid sets of cuts list
                vsc.add(curr_set_of_cuts_new);
            }

            else
            {
                // have to consider further cuts
                computeValidSetsOfCuts(curr_set_of_cuts_new, curr_dependencies_new, k);
            }
        }
    }

    private void computePrimarySetsOfCuts()
    {
        ArrayList<ArrayList<Integer>> to_delete = new ArrayList<ArrayList<Integer>>();

        for(ArrayList<Integer> list : vsc)
        {
            for(Integer x : list)
            {
                ArrayList<Integer> list_without_x = (ArrayList<Integer>) list.clone();
                list_without_x.remove(x);
                if(vsc.contains(list_without_x))
                {
                    to_delete.add(list);
                }
            }
        }

        psc = (ArrayList<ArrayList<Integer>>) vsc.clone();

        for(ArrayList<Integer> list_to_delete : to_delete)
        {
            psc.remove(list_to_delete);
        }
    }
}