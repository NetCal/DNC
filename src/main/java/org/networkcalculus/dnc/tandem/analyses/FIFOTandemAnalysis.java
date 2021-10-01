package org.networkcalculus.dnc.tandem.analyses;

import java.util.*;

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
import org.networkcalculus.dnc.tandem.AbstractTandemAnalysis;
import org.networkcalculus.dnc.tandem.fifo.NestedTandemAnalysis;
import org.networkcalculus.dnc.tandem.fifo.NonNestedTandemAnalysis;
import org.networkcalculus.dnc.tandem.fifo.TNode;
import org.networkcalculus.dnc.utils.SetUtils;
import org.networkcalculus.num.Num;

/**
 *
 * @author Alexander Scheffler
 * @author Steffen Bondorf
 *
 */
public class FIFOTandemAnalysis extends AbstractTandemAnalysis {
    private final boolean checkStabilityConstraint = false;

    public FIFOTandemAnalysis(ServerGraph server_graph ) {
        super.server_graph = server_graph;
        super.result = new FIFOTandemAnalysisResults();
        super.configuration = new AnalysisConfig();
        configuration.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO);
    }

    public FIFOTandemAnalysis(ServerGraph server_graph, AnalysisConfig configuration ) {
        super.server_graph = server_graph;
        super.result = new FIFOTandemAnalysisResults();
        this.configuration = configuration;
        configuration.enforceMultiplexing(AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO);
    }

    /**
     * Performs a FIFO analysis for the <code>flow_of_interest</code>.
     *
     * @param flow_of_interest the flow for which the end-to-end service curve shall be computed.
     */
    public void performAnalysis( Flow flow_of_interest ) throws Exception
    {
        performAnalysis( flow_of_interest, flow_of_interest.getPath() );
    }

    public void performAnalysis( Flow flow_of_interest, Path path ) throws Exception
    {
        // Some analysis (especially LUDB-FF) might return wrong results if the stability constraint is not fulfilled or exits with an error
        if(checkStabilityConstraint)
        {
            for(Server server : server_graph.getServers())
            {
                Set<Flow> flows = server_graph.getFlows(server);

                Num rate_sum = Num.getFactory(Calculator.getInstance().getNumBackend()).getZero();

                for(Flow flow : flows)
                {
                    Num rate = flow.getArrivalCurve().getUltAffineRate();
                    rate_sum = Num.getFactory(Calculator.getInstance().getNumBackend()).add(rate_sum, rate);
                }

                if(rate_sum.gt(server.getServiceCurve().getUltAffineRate()))
                {
                    throw new Exception("Stability constraint violation!");
                }
            }
        }

        ((FIFOTandemAnalysisResults) result).betas_e2e = Collections.singleton(getServiceCurve(  Collections.singleton( flow_of_interest ),flow_of_interest.getArrivalCurve(), path,  false));

        Num delay_bound__beta_e2e;

        ((FIFOTandemAnalysisResults) result).setDelayBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());
        ((FIFOTandemAnalysisResults) result).setBacklogBound(Num.getFactory(Calculator.getInstance().getNumBackend()).createPositiveInfinity());

        for( ServiceCurve beta_e2e : ((FIFOTandemAnalysisResults) result).betas_e2e ) {

            delay_bound__beta_e2e = Calculator.getInstance().getDncBackend().getBounds().delayFIFO( flow_of_interest.getArrivalCurve(), beta_e2e );// Single flow of interest, i.e., fifo per micro flow holds (do not use TFA)

            if( delay_bound__beta_e2e.leq( result.getDelayBound() ) ) {
                ((FIFOTandemAnalysisResults) result).setDelayBound( delay_bound__beta_e2e );
            }
        }
    }

    public ServiceCurve getServiceCurve(Set<Flow> flows_of_interest, ArrivalCurve agg_ac_flows_of_interest_at_path_source, Path path, boolean outputbound) throws Exception {
        // All flows in flows_of_interest need to cross all servers on the path
        Set<Flow> all_flows_on_path = server_graph.getFlows(path);
        Set<Flow> crossflows = SetUtils.getDifference(all_flows_on_path, flows_of_interest);


        if(crossflows.isEmpty())
        {
            // No crossflows on the path
            // In case of LUDB-FF, the runtime concerning time to solve lps/time to simplify for one flow networks will be zero (we could alternatively call the Nested Analysis)
            return path.getServiceCurve();
        }

        Map<Path,Set<Flow>> crossflows_subpath_grouped = server_graph.groupFlowsPerSubPath(path, crossflows);

        // Determine if the network|path is a nested / non-nested tandem
        HashMap<Server, Integer> server_numbers = new HashMap<Server, Integer>();
        LinkedList<Server> servers_on_path = path.getServers();
        // Needs to start from 0 because we use those numbers for indexing in the matrix
        int num = 0;
        for (Server server : servers_on_path) {
            server_numbers.put(server, num++);
        }

        // All entries initially false
        boolean[][] flowMatrix = new boolean[num][num];

        // Construction of flowMatrix (we just mark the source and sink server of each flow)
        for (Path path_xf : crossflows_subpath_grouped.keySet()) {

            int source_num = server_numbers.get(path_xf.getSource());
            int sink_num = server_numbers.get(path_xf.getSink());


            flowMatrix[source_num][sink_num] = true;
        }

        boolean nested = true;
        loop:
        for (Path path_xf : crossflows_subpath_grouped.keySet()) {

            int source_num = server_numbers.get(path_xf.getSource());
            int sink_num = server_numbers.get(path_xf.getSink());

            // Relevant search space: [0, source_num - 1] x [source_num, sink_num - 1] "and" [source_num + 1, sink_num] x [sink_num + 1, num - 1]
            // "[0, source_num - 1] x [source_num, sink_num - 1]" already found by corresponding flow

            for (int i = source_num + 1; i <= sink_num; i++) {
                for (int j = sink_num + 1; j <= num - 1; j++) {
                    if (flowMatrix[i][j]) {
                        nested = false;
                        break loop;
                    }
                }
            }
        }


        // Compute flow substitutes (Nested/NonNestedTandemAnalysis will use them)
        Map<Path, Flow> crossflow_substitutes = new HashMap<>();
        for(Path path_curr : crossflows_subpath_grouped.keySet())
        {
            Set<Flow> crossflows_with_path_curr = crossflows_subpath_grouped.get(path_curr);

            ArrivalCurve ac_crossflows_at_path_curr_source = null;
            if(nested || path_curr.equals(path))
            {
                // nested: compute output arrival curve for crossflow aggregate
                // non-nested: path_curr equals path => need the output arrival curve for this aggregate (see case distinctions below)
                ac_crossflows_at_path_curr_source = ArrivalBoundDispatchFIFO.computeArrivalBound(server_graph, configuration, path_curr.getSource(), crossflows_with_path_curr);
            }

            else{
                // computation of output arrival curves will be done in the NonNestedTandemAnalysis (within the individual subtandems which are not known at this point yet). Hence we just set them to infinity for now (they won't be used).
                ac_crossflows_at_path_curr_source = Curve.getFactory().createInfiniteArrivals();
            }

            String substitute_crossflow_alias = computeAlias(new ArrayList<>(crossflows_with_path_curr),"crossflow_subst" );
            Flow substitute_crossflow = Flow.createDummyFlow(substitute_crossflow_alias, ac_crossflows_at_path_curr_source, path_curr);
            crossflow_substitutes.put(path_curr, substitute_crossflow);
        }


        String substitute_foi_alias = computeAlias(new ArrayList<>(flows_of_interest), "foi_subst_");
        Flow foi_substitute = Flow.createDummyFlow(substitute_foi_alias, agg_ac_flows_of_interest_at_path_source, path);


        // We will differentiate between the following cases: nested/non-nested, no crossflow has path as subpath/at least one crossflow has path as subpath, solve for "best" output/delay bound
        if(nested)
        {
            if(crossflow_substitutes.containsKey(path))
            {
                if(outputbound)
                {
                    // Nested, at least one crossflow has path as subpath, solve for "best" output bound
                    NestedTandemAnalysis tandem_analysis_crossflow_w_path_as_subpath = new NestedTandemAnalysis(path, crossflow_substitutes.get(path), new HashSet<>(crossflow_substitutes.values()), configuration);
                    ServiceCurve sc_crossflow_w_path_as_subpath = tandem_analysis_crossflow_w_path_as_subpath.getServiceCurve();

                    ArrivalCurve ac = crossflow_substitutes.get(path).getArrivalCurve();
                    ServiceCurve sc = sc_crossflow_w_path_as_subpath;
                    Num burst = ac.getBurst();

                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;

                    Num theta_curr_lb = curve.f_inv(burst);
                    return LeftOverService_Disco_PwAffine.fifoMux(sc, ac, theta_curr_lb);
                }
                else{
                    // Nested, at least one crossflow has path as subpath, solve for "best" delay bound
                    ArrayList<Flow> all_real_flows_in_substitute_foi_crossflow_w_path = new ArrayList<>();
                    all_real_flows_in_substitute_foi_crossflow_w_path.addAll(flows_of_interest);
                    all_real_flows_in_substitute_foi_crossflow_w_path.addAll(crossflows_subpath_grouped.get(path));
                    String substitute_foi_w_crossflow_alias_agg = computeAlias(all_real_flows_in_substitute_foi_crossflow_w_path, "foi_and_crossflow_agg_subst_");


                    ArrivalCurve ac_crossflow_substitutes_path_as_subpath = crossflow_substitutes.get(path).getArrivalCurve();
                    // Note that we do not suffer here from adding the segregated arrival curves (instead of computing the aggregated arrival curve explicitly) since we can assume (outputbound == false) that in this case the flows_of_interest start at path.source
                    ArrivalCurve ac_foi_w_crossflow_agg = Curve.getUtils().add(agg_ac_flows_of_interest_at_path_source, ac_crossflow_substitutes_path_as_subpath);
                    Flow flow_substitute_foi_w_crossflow = Flow.createDummyFlow(substitute_foi_w_crossflow_alias_agg, ac_foi_w_crossflow_agg, path);

                    Set<Flow> all_flow_substitutes = new HashSet<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.remove(crossflow_substitutes.get(path));
                    all_flow_substitutes.add(flow_substitute_foi_w_crossflow);

                    NestedTandemAnalysis tandem_analysis = new NestedTandemAnalysis(path, flow_substitute_foi_w_crossflow, all_flow_substitutes, configuration);
                    ServiceCurve sc_lo_foi_w_crossflow = tandem_analysis.getServiceCurve();
                    Num burst = flow_substitute_foi_w_crossflow.getArrivalCurve().getBurst();
                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc_lo_foi_w_crossflow;
                    Num theta = curve.f_inv(burst);
                    return LeftOverService_Disco_PwAffine.fifoMux(sc_lo_foi_w_crossflow, ac_crossflow_substitutes_path_as_subpath, theta);
                }
            }

            else{
                if (outputbound) {
                    // Nested, no crossflow has path as subpath, solve for "best" output bound
                    // Do a NestedTandemAnalysis computation for each flow-child of the flow of interest, then take their left-over and "subtract" with the current lower bound theta. (Also convolve with isolated servers on the foi's path.)
                    Set<Flow> all_flow_substitutes = new HashSet<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.add(foi_substitute);
                    NestedTandemAnalysis tandem_analysis = new NestedTandemAnalysis(path, foi_substitute, all_flow_substitutes, configuration);
                    TNode root = tandem_analysis.onlyComputeNestingTree();
                    ArrayList<TNode> foi_children = root.getChildren();
                    ServiceCurve leftover = Curve.getFactory().createZeroDelayInfiniteBurst();
                    for (TNode child : foi_children) {
                        if (child.getInf() instanceof LinkedList) {
                            LinkedList<Server> servers = (LinkedList<Server>) child.getInf();
                            for (Server server : servers) {
                                leftover = Calculator.getInstance().getMinPlus().convolve(leftover, server.getServiceCurve());
                            }
                        } else {
                            Flow foi_child_flow = (Flow) child.getInf();
                            LinkedList<Server> servers_on_foi_child_path = foi_child_flow.getPath().getServers();

                            Set<Flow> flows_nested_into_foi_child = new HashSet<>();
                            for (Flow flow : all_flow_substitutes) {
                                // Can't use ServerGraphs getFlowsPerSubPath method because we deal with substitute flows here...
                                Server src = flow.getSource();
                                Server dest = flow.getSink();
                                if (servers_on_foi_child_path.contains(src) && servers_on_foi_child_path.contains(dest)) {
                                    // flow is nested into foi_child_flow (might be even that flow itself)
                                    flows_nested_into_foi_child.add(flow);
                                }
                            }

                            NestedTandemAnalysis tandem_analysis_child = new NestedTandemAnalysis(foi_child_flow.getPath(), foi_child_flow, flows_nested_into_foi_child, configuration);
                            ServiceCurve beta_lo_child = tandem_analysis_child.getServiceCurve();

                            ArrivalCurve ac = foi_child_flow.getArrivalCurve();
                            ServiceCurve sc = beta_lo_child;
                            Num burst = ac.getBurst();

                            Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;

                            Num theta_curr_lb = curve.f_inv(burst);
                            ServiceCurve subtandem_foi_lo = LeftOverService_Disco_PwAffine.fifoMux(beta_lo_child, foi_child_flow.getArrivalCurve(), theta_curr_lb);
                            leftover = Calculator.getInstance().getMinPlus().convolve(leftover, subtandem_foi_lo);
                        }
                    }
                    return leftover;
                }
                else{
                    // Nested, no crossflow has path as subpath, solve for "best" delay bound
                    Set<Flow> all_flow_substitutes = new HashSet<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.add(foi_substitute);
                    NestedTandemAnalysis tandem_analysis = new NestedTandemAnalysis(path, foi_substitute, all_flow_substitutes, configuration);
                    return tandem_analysis.getServiceCurve();
                }
            }
        }

        else{
            if(crossflow_substitutes.containsKey(path))
            {
                if(outputbound)
                {
                    // Non nested, at least one crossflow has path as subpath, solve for "best" output bound
                    // yes the flag has to be set to solve_for_output_bound_opt == false because we do the analysis with the crossflows that have path as subpath as flow of interest (and use the respective left-over to get a left-over for the flows_of_interest)
                    NonNestedTandemAnalysis tandem_analysis_crossflow_w_path_as_subpath = new NonNestedTandemAnalysis(server_graph, configuration, path, crossflow_substitutes.get(path), new ArrayList<>(crossflow_substitutes.values()), crossflows_subpath_grouped, false);
                    ServiceCurve sc_crossflow_w_path_as_subpath = tandem_analysis_crossflow_w_path_as_subpath.getServiceCurve();
                    ArrivalCurve ac = crossflow_substitutes.get(path).getArrivalCurve();
                    ServiceCurve sc = sc_crossflow_w_path_as_subpath;
                    Num burst = ac.getBurst();

                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;

                    Num theta_curr_lb = curve.f_inv(burst);
                    return LeftOverService_Disco_PwAffine.fifoMux(sc, ac, theta_curr_lb);
                }

                else{
                    // Non nested, at least one crossflow has path as subpath, solve for "best" delay bound
                    ArrayList<Flow> all_real_flows_in_substitute_foi_crossflow_w_path = new ArrayList<>();
                    all_real_flows_in_substitute_foi_crossflow_w_path.addAll(flows_of_interest);
                    all_real_flows_in_substitute_foi_crossflow_w_path.addAll(crossflows_subpath_grouped.get(path));
                    String substitute_foi_w_crossflow_alias_agg = computeAlias(all_real_flows_in_substitute_foi_crossflow_w_path, "foi_and_crossflow_agg_subst_");


                    ArrivalCurve ac_crossflow_substitutes_path_as_subpath = crossflow_substitutes.get(path).getArrivalCurve();
                    // Note that we do not suffer here from adding the segregated arrival curves (instead of computing the aggregated arrival curve explicitly) since we can assume (outputbound == false) that in this case the flows_of_interest start at path.source
                    // Motivation: In this non-nested case we would cut the crossflow that has path as subpath if we would not aggregate it with the flows_of_interest
                    ArrivalCurve ac_foi_w_crossflow_agg = Curve.getUtils().add(agg_ac_flows_of_interest_at_path_source, ac_crossflow_substitutes_path_as_subpath);
                    Flow flow_substitute_foi_w_crossflow = Flow.createDummyFlow(substitute_foi_w_crossflow_alias_agg, ac_foi_w_crossflow_agg, path);


                    ArrayList<Flow> all_flow_substitutes = new ArrayList<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.remove(crossflow_substitutes.get(path));
                    all_flow_substitutes.add(flow_substitute_foi_w_crossflow);


                    Map<Path,Set<Flow>> crossflows_subpath_grouped_minus_crossflow_w_path = new HashMap<>();
                    crossflows_subpath_grouped_minus_crossflow_w_path.putAll(crossflows_subpath_grouped);
                    crossflows_subpath_grouped_minus_crossflow_w_path.remove(path);

                    NonNestedTandemAnalysis tandem_analysis = new NonNestedTandemAnalysis(server_graph, configuration, path, flow_substitute_foi_w_crossflow, all_flow_substitutes, crossflows_subpath_grouped_minus_crossflow_w_path, false);
                    ServiceCurve sc_lo_foi_w_crossflow = tandem_analysis.getServiceCurve();
                    Num burst = flow_substitute_foi_w_crossflow.getArrivalCurve().getBurst();
                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc_lo_foi_w_crossflow;
                    Num theta = curve.f_inv(burst);
                    return LeftOverService_Disco_PwAffine.fifoMux(sc_lo_foi_w_crossflow, ac_crossflow_substitutes_path_as_subpath, theta);
                }
            }
            else{
                if(outputbound)
                {
                    // Non nested, no crossflow has path as subpath, solve for "best" output bound
                    ArrayList<Flow> all_flow_substitutes = new ArrayList<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.add(foi_substitute);
                    NonNestedTandemAnalysis tandem_analysis  = new NonNestedTandemAnalysis(server_graph, configuration, path, foi_substitute, all_flow_substitutes, crossflows_subpath_grouped, true);
                    return tandem_analysis.getServiceCurve();
                }

                else{
                    // Non nested, no crossflow has path as subpath, solve for "best" delay bound
                    ArrayList<Flow> all_flow_substitutes = new ArrayList<>();
                    all_flow_substitutes.addAll(crossflow_substitutes.values());
                    all_flow_substitutes.add(foi_substitute);
                    NonNestedTandemAnalysis tandem_analysis  = new NonNestedTandemAnalysis(server_graph, configuration, path, foi_substitute, all_flow_substitutes, crossflows_subpath_grouped, false);
                    return tandem_analysis.getServiceCurve();
                }
            }
        }
    }

    public String computeAlias(ArrayList<Flow> flows, String start)
    {
        String alias = start + "{";
        for(Flow flow : flows)
        {
            alias = alias.concat( flow.getAlias() + "," );
        }
        alias = alias.substring( 0, alias.length()-1 ); // Remove trailing comma.
        alias = alias.concat( "}" );
        return alias;
    }
}