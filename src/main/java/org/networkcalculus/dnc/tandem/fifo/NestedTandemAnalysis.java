package org.networkcalculus.dnc.tandem.fifo;

import java.util.*;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.bounds.disco.pw_affine.LeftOverService_Disco_PwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.curves.disco.pw_affine.Curve_Disco_PwAffine;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.Expression_LUDB;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.LUDB_LP;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.PseudoAffine;
import org.networkcalculus.dnc.tandem.fifo.LUDBOPT.TB_LUDB;
import org.networkcalculus.num.Num;

/**
 * @author Alexander Scheffler
 */
public class NestedTandemAnalysis {
    // The path of a network to be analyzed, equals foi.getPath()
    private final Path foi_path;
    // The flows on the path including foi. They need to meet the requirements mentioned in computeNestingSets method.
    private final Set<Flow> flows;
    // Flow of interest for which the left over serive curve needs to be computed
    private final Flow foi;
    // represents S_(h,k) (see paper)
    private final HashMap<Flow, ArrayList<Flow>> flow_directly_nested_flows_map;
    // represents C_(h,k) (see paper)
    private final HashMap<Flow, LinkedList<Server>> flow_nodes_map;
    // represents the nesting tree of the given non-nested tandem
    private TNode nestingTree;

    private static boolean compute_flows_without_foi_ordered = false; // don't change

    private static final boolean flows_without_foi_ordered_tree = true; // don't change, same order as bottom up call order in tree (like in the construction of the nesting tree)

    private final Map<Flow, TNode> flow_tnode_map = new HashMap<>(); // only for the current setting of the nesting tree

    private List<Flow> flows_without_foi_ordered = new ArrayList(); // used for the recursive theta_combinations algorithm

    private ServiceCurve e2e;

    public enum mode {
         LUDB
    }

    public static mode selected_mode;

    /////////////////// /////////////////// /////// LUDB
    private final ArrayList<Flow> crossflowList = new ArrayList(); // mapping of id to Flow for LP computation (LUDB) (index coincides with id)
    private double curr_min_delay_ludb; // for the on the run version (i.e. the one that does not compute all decompositions a priori)
    private Map<Integer, Double> curr_best_s_setting; //  curr_lb + s <=> theta (note that s >= 0!) [s from LUDB paper fifo l.o. theorem, theta is free parameter in the general fifo left over theorem]
    /////////////////// /////////////////// ///////


    public NestedTandemAnalysis(Path tandem, Flow flow_of_interest, List<Flow> flows) {
        this(tandem, flow_of_interest, flows, new AnalysisConfig());
    }

    public NestedTandemAnalysis(Path tandem, Flow flow_of_interest, Set<Flow> flows) {
        this(tandem, flow_of_interest, flows, new AnalysisConfig());
    }

    // AnalysisConfig global FIFO necessary
    // Network needs to be a tandem
    // flows need to include flow_of_interest
    // path must be a nested tandem
    public NestedTandemAnalysis(Path tandem, Flow flow_of_interest, List<Flow> flows, AnalysisConfig config) {
        this.foi_path = tandem;
        foi = flow_of_interest;
        this.flows = new HashSet<Flow>(flows);
        flow_directly_nested_flows_map = new HashMap<Flow, ArrayList<Flow>>();
        flow_nodes_map = new HashMap<Flow, LinkedList<Server>>();

        if (!flows_without_foi_ordered_tree) {
            flows_without_foi_ordered = new ArrayList<>(flows);
            flows_without_foi_ordered.remove(flow_of_interest);
        } else {
            compute_flows_without_foi_ordered = true;
        }
    }


    // flows need to include flow_of_interest
    // path must be a nested tandem
    public NestedTandemAnalysis(Path tandem, Flow flow_of_interest, Set<Flow> flows, AnalysisConfig config) {
        this.foi_path = tandem;
        foi = flow_of_interest;
        this.flows = flows;
        flow_directly_nested_flows_map = new HashMap<Flow, ArrayList<Flow>>();
        flow_nodes_map = new HashMap<Flow, LinkedList<Server>>();
        if (!flows_without_foi_ordered_tree) {
            flows_without_foi_ordered = new ArrayList<>(flows);
            flows_without_foi_ordered.remove(flow_of_interest);
        } else {
            compute_flows_without_foi_ordered = true;
        }
    }

    public Num performAnalysis() throws Exception {
        ServiceCurve foi_leftover_sc = getServiceCurve();
        return Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), foi_leftover_sc);
    }

    public ServiceCurve getServiceCurve() throws Exception {

        computeNestingSets();
        createNestingTreeOrdered();

        switch (selected_mode) {
            case LUDB:
                computeLUDB();
                break;
        }

        return e2e;
    }


    public void computeLUDB() throws Exception {
        for (Flow flow : flows) {
            if (!flow.equals(foi)) {
                crossflowList.add(flow);
            }
        }
        setLeafDecompositions(nestingTree);
        getFlowOrderWrtNestingTree(nestingTree);
        // check if we even have crossflows
        if (flows_without_foi_ordered.size() > 0) {
            Flow curr_flow = flows_without_foi_ordered.get(0);
            // compute curve for curr_flow (only convolution of leaf nodes)
            TNode curr_flow_node = flow_tnode_map.get(curr_flow);
            curr_min_delay_ludb = Double.POSITIVE_INFINITY;
            computeCurrLeftOverandConstraints(curr_flow_node, new HashMap<Flow, Integer>());

            computeLUDBSkippingInfeasibleDecompositions(curr_flow, 0, new HashMap<Flow, Integer>());

            // need to set e2e service curve with the best "s" setting

            e2e = computeLeftOverSCFromLUDB_s_setting(nestingTree, curr_best_s_setting);


        } else {
            // no crossflows
            double delay = Double.POSITIVE_INFINITY;
            computeCurrLeftOverandConstraints(nestingTree, new HashMap<>());
            PseudoAffine foi_curve = nestingTree.getCurrentPseudoAffineCurve();
            List<TB_LUDB> foi_curve_stages = foi_curve.getStages();
            List<Expression_LUDB> foi_constraints = nestingTree.getCurrentConstraints();
            int foi_nr_stages = foi_curve_stages.size();
            Num foi_burst = foi.getArrivalCurve().getBurst();
            Expression_LUDB foi_burst_exp = new Expression_LUDB(foi_burst);

            for (int j = -1; j < foi_nr_stages; j++) {
                Pair<Expression_LUDB, List<Expression_LUDB>> delay_composition_j = getDelayLPDecomposition(foi_curve, foi_constraints, foi_burst_exp, j);
                Pair<Double, Map<Integer, Double>> result_cplex_pair = LUDB_LP.simplify_first_then_save_and_solve_lp_w_cplex(delay_composition_j.getFirst(), delay_composition_j.getSecond(), crossflowList.size());
                double result_tmp = result_cplex_pair.getFirst();
                // we know that the map is empty anyways
                if (result_tmp < delay) {
                    delay = result_tmp;
                }
            }
            e2e = computeLeftOverSCFromLUDB_s_setting(nestingTree, new HashMap<>());
        }
    }



    // Updates the current left-over curve and constraints of the node
    public void computeCurrLeftOverandConstraints(TNode node, Map<Flow, Integer> curr_selection_max) {
        ArrayList<TNode> children = node.getChildren();


        ArrayList<TNode> children_leafs = new ArrayList<>(); // Note that by construction we convolve isolated servers to one leaf
        ArrayList<TNode> children_flows = new ArrayList<>();


        for (TNode child : children) {
            if (child.getInf() instanceof LinkedList) {
                // child represents C_(h,k), so we don't need to "subtract" some AC from that left over sc
                // Is already pseudoaffine-"wrapped"
                children_leafs.add(child);
            } else {
                children_flows.add(child);
            }
        }

        PseudoAffine leafs_conv = PseudoAffine.createZeroDelayInfiniteBurst();
        for (TNode child : children_leafs) {
            PseudoAffine child_curve = child.getCurrentPseudoAffineCurve();
            leafs_conv = PseudoAffine.convolve(leafs_conv, child_curve);

            // We know that the list of constraints are all empty for the children leafs
        }

        PseudoAffine result_curve = leafs_conv;
        List<Expression_LUDB> result_constraints = new ArrayList<>();

        // Put only one foor loop from a case here instead of the test

        for (TNode child : children_flows) {

            Flow child_flow = (Flow) child.getInf();
            int id = crossflowList.indexOf(child_flow);
            ArrivalCurve ac = child_flow.getArrivalCurve();
            // We assume token bucket arrivals (LUDB is constrained to those arrival curves)
            Expression_LUDB ac_burst = new Expression_LUDB(ac.getBurst());
            Expression_LUDB ac_rate = new Expression_LUDB(ac.getUltAffineRate());
            PseudoAffine curr_child_curve = child.getCurrentPseudoAffineCurve();
            List<Expression_LUDB> curr_child_constraints = child.getCurrentConstraints();
            int curr_child_which_max = curr_selection_max.get(child_flow);

            Pair<PseudoAffine, List<Expression_LUDB>> child_left_over_decompositions_pair = leftOverDecomposition(ac_burst, ac_rate, curr_child_curve, curr_child_constraints, id, curr_child_which_max);

            result_curve = PseudoAffine.convolve(result_curve, child_left_over_decompositions_pair.getFirst());
            result_constraints.addAll(child_left_over_decompositions_pair.getSecond());
        }


        node.setCurrentPseudoAffineCurveAndConstraints(result_curve, result_constraints);
    }

    private void computeLUDBSkippingInfeasibleDecompositions(Flow curr_flow, int index, Map<Flow, Integer> curr_selection_max) throws Exception {

        // Determine how many stages the l.o. service curve for curr_flow has
        TNode curr_flow_node = flow_tnode_map.get(curr_flow);
        PseudoAffine curr_flow_curve = curr_flow_node.getCurrentPseudoAffineCurve();
        List<TB_LUDB> curr_flow_curve_stages = curr_flow_curve.getStages();
        int nr_stages = curr_flow_curve_stages.size();


        // if k == -1, we select the null term as max
        for (int k = -1; k < nr_stages; k++) {
            // Only put k to the map if it is feasible for curr_flow, i.e., we compute the LUDB for the curr_flow with k
            // if (!use_cache) {
            List<Expression_LUDB> curr_flow_constraints = curr_flow_node.getCurrentConstraints();
            Num curr_flow_burst = curr_flow.getArrivalCurve().getBurst();
            Expression_LUDB curr_flow_burst_exp = new Expression_LUDB(curr_flow_burst);
            Pair<Expression_LUDB, List<Expression_LUDB>> curr_flow_delay_composition_k = getDelayLPDecomposition(curr_flow_curve, curr_flow_constraints, curr_flow_burst_exp, k);
            Pair<Double, Map<Integer, Double>> result_curr_flow_cplex_pair = LUDB_LP.simplify_first_then_save_and_solve_lp_w_cplex(curr_flow_delay_composition_k.getFirst(), curr_flow_delay_composition_k.getSecond(), crossflowList.size());
            Double result_curr_flow_delay_k = result_curr_flow_cplex_pair.getFirst();


            if (result_curr_flow_delay_k.equals(Double.NaN)) {
                continue;
            }
            curr_selection_max.put(curr_flow, k);

            if (index < flows_without_foi_ordered.size() - 1) {

                Flow next_flow = flows_without_foi_ordered.get(index + 1);

                TNode node_of_next_flow = flow_tnode_map.get(next_flow);


                computeCurrLeftOverandConstraints(node_of_next_flow, curr_selection_max);

                computeLUDBSkippingInfeasibleDecompositions(next_flow, index + 1, curr_selection_max);
                //We do not need to delete next_flow from curr_selection_max --- in the search we have to delete "old" entries because of the computation of the current bounds, here we simply update the map

            } else {
                // "last" (wrt the list) crossflow reached
                computeCurrLeftOverandConstraints(nestingTree, curr_selection_max);


                PseudoAffine foi_curve = nestingTree.getCurrentPseudoAffineCurve();
                List<TB_LUDB> foi_curve_stages = foi_curve.getStages();
                List<Expression_LUDB> foi_constraints = nestingTree.getCurrentConstraints();
                int foi_nr_stages = foi_curve_stages.size();
                Num foi_burst = foi.getArrivalCurve().getBurst();
                Expression_LUDB foi_burst_exp = new Expression_LUDB(foi_burst);


                for (int j = -1; j < foi_nr_stages; j++) {

                    Pair<Expression_LUDB, List<Expression_LUDB>> delay_composition_j = getDelayLPDecomposition(foi_curve, foi_constraints, foi_burst_exp, j);
                    Pair<Double, Map<Integer, Double>> result_cplex_pair = LUDB_LP.simplify_first_then_save_and_solve_lp_w_cplex(delay_composition_j.getFirst(), delay_composition_j.getSecond(), crossflowList.size());
                    double result_tmp = result_cplex_pair.getFirst();

                    if (result_tmp < curr_min_delay_ludb) {
                        curr_min_delay_ludb = result_tmp;
                        curr_best_s_setting = result_cplex_pair.getSecond();
                    }
                }

            }
        }
    }

    private void getFlowOrderWrtNestingTree(TNode node) throws Exception {
        ArrayList<TNode> children = node.getChildren();

        for (TNode child : children) {
            getFlowOrderWrtNestingTree(child);
        }
        if (!(node.getInf() instanceof LinkedList)) {
            // child represents a flow
            Flow curr_flow = (Flow) node.getInf();
            if (!curr_flow.equals(foi)) {
                flows_without_foi_ordered.add(curr_flow);
                flow_tnode_map.put(curr_flow, node);
            }
        }
    }

    // Sets the current pseudoaffine curve and constraints for the leaf nodes (constraints will be empty and the curve is just the convolution of the full service curve of the respective servers). This won't change anymore ("stable").
    private void setLeafDecompositions(TNode node) throws Exception {
        ArrayList<TNode> children = node.getChildren();

        for (TNode child : children) {
            setLeafDecompositions(child);
        }
        if (node.getInf() instanceof LinkedList) {
            // node is a t-leaf, i.e. represents C_(h,k)
            LinkedList<Server> servers = (LinkedList<Server>) node.getInf();

            // For some reason there we have additional leafs with empty serverlist (whenever the foi does not have isolated servers, i.e., we
            // experience this whenever every server on the foi path has at least one crossflow crossing it => only in this case?! Check and adapt computation of nesting tree
            // NOW: Use neutral element for convolution and empty list of constraints
            if (servers.isEmpty()) {
                node.setCurrentPseudoAffineCurveAndConstraints(PseudoAffine.createZeroDelayInfiniteBurst(), new ArrayList<>());
            } else {
                ServiceCurve leftover = Curve.getFactory().createZeroDelayInfiniteBurst();

                for (Server server : servers) {
                    leftover = Calculator.getInstance().getMinPlus().convolve(leftover, server.getServiceCurve());
                }
                // We assume rate latency service curve (convolution is closed w.r.t. rate latency curves)
                // No constraints for this case
                Num latency = leftover.getLatency();
                Num rate = leftover.getUltAffineRate();
                Expression_LUDB latency_exp = new Expression_LUDB(latency);
                Expression_LUDB rate_exp = new Expression_LUDB(rate);
                Expression_LUDB j2_exp = Expression_LUDB.createZeroNumExpression();
                TB_LUDB tb = new TB_LUDB(j2_exp, rate_exp);
                PseudoAffine pseudoaffine_left_over = new PseudoAffine(latency_exp, Arrays.asList(tb));
                node.setCurrentPseudoAffineCurveAndConstraints(pseudoaffine_left_over, new ArrayList<>());
            }
        }
        // else: child flow => will be set in the other method (changes depending what was selected as max (recursively))
    }

    public Pair<PseudoAffine, List<Expression_LUDB>> leftOverDecomposition(Expression_LUDB ac_burst, Expression_LUDB ac_rate, PseudoAffine sc, List<Expression_LUDB> constraints, int id, int curr_selection_max) {

        // The burst and rate of the arrival curve here are concrete numbers actually (however we will work with the already wrapped objects)
        Expression_LUDB d_latency = sc.getLatency();
        List<TB_LUDB> sc_stages = sc.getStages();
        // We already do the composition, i.e., but consider only one "stage" to be the max (or the null term) determined by curr_selction_max and store the resulting pseudoaffine left-over curve with its constraints

        if (curr_selection_max == -1) {
            // null term as max
            ArrayList<Expression_LUDB> constraints_zero_max = new ArrayList<>();

            Expression_LUDB free_var_s = new Expression_LUDB(id);

            Expression_LUDB constr_free_var_s_geq_0 = new Expression_LUDB(free_var_s, Expression_LUDB.createZeroNumExpression(), Expression_LUDB.ExpressionType.GEQ);
            constraints_zero_max.add(constr_free_var_s_geq_0);

            List<TB_LUDB> stages_curr_stage_curve = new ArrayList<>();

            for (TB_LUDB stage_other : sc_stages) {
                Expression_LUDB curr_stage_other_rate = stage_other.getRate();
                Expression_LUDB curr_stage_other_jump = stage_other.getJump();

                Expression_LUDB denominator_other_exp = new Expression_LUDB(ac_burst, curr_stage_other_jump, Expression_LUDB.ExpressionType.SUB);
                Expression_LUDB curr_stage_other_constr_right = new Expression_LUDB(denominator_other_exp, curr_stage_other_rate, Expression_LUDB.ExpressionType.DIV);

                Expression_LUDB constr_max = new Expression_LUDB(Expression_LUDB.createZeroNumExpression(), curr_stage_other_constr_right, Expression_LUDB.ExpressionType.GEQ);
                constraints_zero_max.add(constr_max);


                Expression_LUDB term = new Expression_LUDB(free_var_s, curr_stage_other_constr_right, Expression_LUDB.ExpressionType.SUB);
                term = new Expression_LUDB(term, curr_stage_other_rate, Expression_LUDB.ExpressionType.MULT);

                Expression_LUDB curr_stage_other_left_over_rate = new Expression_LUDB(curr_stage_other_rate, ac_rate, Expression_LUDB.ExpressionType.SUB);
                TB_LUDB curr_stage_and_other_stage = new TB_LUDB(term, curr_stage_other_left_over_rate);

                stages_curr_stage_curve.add(curr_stage_and_other_stage);
            }

            Expression_LUDB latency_curr = new Expression_LUDB(d_latency, free_var_s, Expression_LUDB.ExpressionType.ADD);
            PseudoAffine curr_stage_curve = new PseudoAffine(latency_curr, stages_curr_stage_curve);

            // add previous constraints
            constraints_zero_max.addAll(constraints);
            return new Pair<>(curr_stage_curve, constraints_zero_max);
        } else {
            // The stage at position curr_selection_max in the list will be max
            TB_LUDB stage = sc_stages.get(curr_selection_max);

            Expression_LUDB curr_stage_rate = stage.getRate();
            Expression_LUDB curr_stage_jump = stage.getJump();

            Expression_LUDB denominator_exp = new Expression_LUDB(ac_burst, curr_stage_jump, Expression_LUDB.ExpressionType.SUB);
            Expression_LUDB curr_stage_constr_left = new Expression_LUDB(denominator_exp, curr_stage_rate, Expression_LUDB.ExpressionType.DIV);

            ArrayList<Expression_LUDB> constraints_curr_stage_max = new ArrayList<>();

            Expression_LUDB constr_curr_stage_geq_0 = new Expression_LUDB(curr_stage_constr_left, Expression_LUDB.createZeroNumExpression(), Expression_LUDB.ExpressionType.GEQ);


            constraints_curr_stage_max.add(constr_curr_stage_geq_0);

            Expression_LUDB free_var_s = new Expression_LUDB(id);

            Expression_LUDB constr_free_var_s_geq_0 = new Expression_LUDB(free_var_s, Expression_LUDB.createZeroNumExpression(), Expression_LUDB.ExpressionType.GEQ);

            constraints_curr_stage_max.add(constr_free_var_s_geq_0);

            Expression_LUDB latency_curr_part = new Expression_LUDB(d_latency, curr_stage_constr_left, Expression_LUDB.ExpressionType.ADD);
            Expression_LUDB latency_curr_stage_curve = new Expression_LUDB(latency_curr_part, free_var_s, Expression_LUDB.ExpressionType.ADD);


            List<TB_LUDB> stages_curr_stage_curve = new ArrayList<>();

            for (TB_LUDB stage_other : sc_stages) {
                Expression_LUDB curr_stage_other_rate = stage_other.getRate();
                Expression_LUDB curr_stage_other_jump = stage_other.getJump();

                Expression_LUDB denominator_other_exp = new Expression_LUDB(ac_burst, curr_stage_other_jump, Expression_LUDB.ExpressionType.SUB);
                Expression_LUDB curr_stage_other_constr_right = new Expression_LUDB(denominator_other_exp, curr_stage_other_rate, Expression_LUDB.ExpressionType.DIV);

                if (!stage_other.equals(stage)) {
                    Expression_LUDB constr_max = new Expression_LUDB(curr_stage_constr_left, curr_stage_other_constr_right, Expression_LUDB.ExpressionType.GEQ);
                    constraints_curr_stage_max.add(constr_max);
                }
                //  else -> can safely skip (do not need unnecessary constraints of the form constraint_k >= constraint_k) (but not the term below!)


                Expression_LUDB term = new Expression_LUDB(curr_stage_constr_left, curr_stage_other_constr_right, Expression_LUDB.ExpressionType.SUB);
                term = new Expression_LUDB(term, free_var_s, Expression_LUDB.ExpressionType.ADD);
                term = new Expression_LUDB(term, curr_stage_other_rate, Expression_LUDB.ExpressionType.MULT);

                Expression_LUDB curr_stage_other_left_over_rate = new Expression_LUDB(curr_stage_other_rate, ac_rate, Expression_LUDB.ExpressionType.SUB);
                TB_LUDB curr_stage_and_other_stage = new TB_LUDB(term, curr_stage_other_left_over_rate);

                stages_curr_stage_curve.add(curr_stage_and_other_stage);


            }

            PseudoAffine curr_stage_curve = new PseudoAffine(latency_curr_stage_curve, stages_curr_stage_curve);

            // add previous constraints
            constraints_curr_stage_max.addAll(constraints);
            return new Pair<>(curr_stage_curve, constraints_curr_stage_max);
        }
    }


    private Pair<Expression_LUDB, List<Expression_LUDB>> getDelayLPDecomposition(PseudoAffine foi_curve, List<Expression_LUDB> foi_constraints, Expression_LUDB foi_ac_burst, int curr_selection_max) {

        Expression_LUDB d_latency = foi_curve.getLatency();
        List<TB_LUDB> sc_stages = foi_curve.getStages();

        if (curr_selection_max == -1) {
            // zero is the max
            // Also we have to consider the case where zero is the max
            ArrayList<Expression_LUDB> constraints_zero_max = new ArrayList<>();

            Expression_LUDB delay_curr = d_latency;

            for (TB_LUDB stage_other : sc_stages) {
                Expression_LUDB curr_stage_other_rate = stage_other.getRate();
                Expression_LUDB curr_stage_other_jump = stage_other.getJump();

                Expression_LUDB denominator_other_exp = new Expression_LUDB(foi_ac_burst, curr_stage_other_jump, Expression_LUDB.ExpressionType.SUB);
                Expression_LUDB curr_stage_other_constr_right = new Expression_LUDB(denominator_other_exp, curr_stage_other_rate, Expression_LUDB.ExpressionType.DIV);

                Expression_LUDB constr_max = new Expression_LUDB(Expression_LUDB.createZeroNumExpression(), curr_stage_other_constr_right, Expression_LUDB.ExpressionType.GEQ);
                constraints_zero_max.add(constr_max);
            }
            // add previous constraints
            constraints_zero_max.addAll(foi_constraints);
            return new Pair<>(delay_curr, constraints_zero_max);
        } else {
            // The stage at position curr_selection_max in the list will be max
            TB_LUDB stage = sc_stages.get(curr_selection_max);

            Expression_LUDB curr_stage_rate = stage.getRate();
            Expression_LUDB curr_stage_jump = stage.getJump();


            Expression_LUDB denominator_exp = new Expression_LUDB(foi_ac_burst, curr_stage_jump, Expression_LUDB.ExpressionType.SUB);
            Expression_LUDB curr_stage_constr_left = new Expression_LUDB(denominator_exp, curr_stage_rate, Expression_LUDB.ExpressionType.DIV);

            ArrayList<Expression_LUDB> constraints_curr_stage_max = new ArrayList<>();

            Expression_LUDB constr_curr_stage_geq_0 = new Expression_LUDB(curr_stage_constr_left, Expression_LUDB.createZeroNumExpression(), Expression_LUDB.ExpressionType.GEQ);

            constraints_curr_stage_max.add(constr_curr_stage_geq_0);


            Expression_LUDB delay_curr = new Expression_LUDB(d_latency, curr_stage_constr_left, Expression_LUDB.ExpressionType.ADD);


            for (TB_LUDB stage_other : sc_stages) {

                if (!stage_other.equals(stage)) {
                    Expression_LUDB curr_stage_other_rate = stage_other.getRate();
                    Expression_LUDB curr_stage_other_jump = stage_other.getJump();

                    Expression_LUDB denominator_other_exp = new Expression_LUDB(foi_ac_burst, curr_stage_other_jump, Expression_LUDB.ExpressionType.SUB);
                    Expression_LUDB curr_stage_other_constr_right = new Expression_LUDB(denominator_other_exp, curr_stage_other_rate, Expression_LUDB.ExpressionType.DIV);

                    Expression_LUDB constr_max = new Expression_LUDB(curr_stage_constr_left, curr_stage_other_constr_right, Expression_LUDB.ExpressionType.GEQ);
                    constraints_curr_stage_max.add(constr_max);
                }
                //  else -> can safely skip (do not need unnecessary constraints of the form constraint_k >= constraint_k)
            }

            // add previous constraints
            constraints_curr_stage_max.addAll(foi_constraints);
            return new Pair<>(delay_curr, constraints_curr_stage_max);
        }
    }


    // post order traversal / computation
    // note: copy of computeLeftOverSC with a few modifications
    private ServiceCurve computeLeftOverSCFromLUDB_s_setting(TNode node, Map<Integer, Double> var_id_to_opt_s_values_map) throws Exception {
        ArrayList<TNode> children = node.getChildren();

        for (TNode child : children) {
            computeLeftOverSCFromLUDB_s_setting(child, var_id_to_opt_s_values_map);
        }
        ServiceCurve leftover = Curve.getFactory().createZeroDelayInfiniteBurst();

        if (node.getInf() instanceof LinkedList) {
            // node is a t-leaf, i.e. represents C_(h,k)
            LinkedList<Server> servers = (LinkedList<Server>) node.getInf();
            for (Server server : servers) {
                leftover = Calculator.getInstance().getMinPlus().convolve(leftover, server.getServiceCurve());
            }
            node.setLeftover(leftover);
        } else {
            // node is a t-node, i.e. represents a flow (h,k)
            for (TNode child : children) {
                if (child.getInf() instanceof LinkedList) {
                    // child represents C_(h,k), so we don't need to "subtract" some AC from that left over sc
                    leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child.getLeftover());
                } else {
                    ServiceCurve child_fifo_leftover = null;
                    Num flow_theta = null;

                    // child represents a flow (h,k), so we need to "subtract" the AC of flow (h,k) from the left over sc  stored in that node
                    // curr_lb: The x-position where the service curve (child.getLeftover()) has a y-value that is at least as high as the burst of the arrival curve --- take the lowest x-position possible
                    ArrivalCurve ac = ((Flow) child.getInf()).getArrivalCurve();
                    ServiceCurve sc = child.getLeftover();
                    Num burst = ac.getBurst(); // We assume that all rates of the ac are smaller than all the rates of the sc (ignoring the null-rates of course)

                    Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;
                    Num curr_lb = curve.f_inv(burst);

                    // use a theta from the map "thetas"
                    Flow flow = (Flow) child.getInf();
                    int var_id = crossflowList.indexOf(flow);

                    double s_opt_double = var_id_to_opt_s_values_map.get(var_id);

                    Num s_opt = Num.getUtils(Calculator.getInstance().getNumBackend()).create(s_opt_double);
                    flow_theta = Num.getUtils(Calculator.getInstance().getNumBackend()).add(curr_lb, s_opt);


                    child_fifo_leftover = LeftOverService_Disco_PwAffine.fifoMux(child.getLeftover(), ((Flow) child.getInf()).getArrivalCurve(), flow_theta);

                    leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child_fifo_leftover);
                }
            }
            node.setLeftover(leftover);
        }
        return leftover;
    }



    private void createNestingTreeOrdered() {
        nestingTree = new TNode(foi, null);
        createNestingTreeLevelOrdered(foi, nestingTree);
    }

    private void createNestingTreeLevelOrdered(Flow flow, TNode subTree) {
        try {
            ArrayList<Flow> nested_flows = flow_directly_nested_flows_map.get(flow);
            LinkedList<Server> nested_flows_servers = flow_nodes_map.get(flow);
            if (nested_flows_servers != null) {
                subTree.addChild(nested_flows_servers);
            }

            Server foi_path_node = foi.getSource();
            Server foi_path_sink = foi.getSink();
            Path foi_path = foi.getPath();

            boolean checked_all_servers_on_path = false;
            while (!checked_all_servers_on_path) {
                // find the child crossflow that starts here (only one possible)
                for (Flow flow2 : nested_flows) {
                    if (flow2.getSource().equals(foi_path_node)) {
                        TNode<Flow> childNode;
                        childNode = subTree.addChild(flow2);
                        createNestingTreeLevelOrdered(flow2, childNode);
                        break;
                    }
                }

                if (!(foi_path_node.equals(foi_path_sink))) {
                    foi_path_node = foi_path.getSucceedingServer(foi_path_node);
                } else {
                    checked_all_servers_on_path = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void computeNestingSets() {
        // Map enumerates servers on the foi path.
        // Note that the foi (1,N) must include all other flows (i,j), so i > 1 and j < N.
        // Otherwise, the ludb method does not work. We also assume that the flows are disjoint
        // concerning (source,sink), i.e. there are no two flows (i,j) (k,l) with i = k and j = l.
        HashMap<Server, Integer> server_numbers = new HashMap<Server, Integer>();
        LinkedList<Server> servers_on_foi_path = foi_path.getServers();

        int num = 1;
        for (Server server : servers_on_foi_path) {
            server_numbers.put(server, num++);
        }

        // all flows of the network
        HashMap<Flow, ArrayList<Flow>> flow_nested_flows_map = new HashMap<Flow, ArrayList<Flow>>();
        HashMap<Flow, Integer> flow_level_map = new HashMap<Flow, Integer>();


        // S_(h,k) computation (see paper)
        for (Flow flow : flows) {
            // flows are nested within themselves (as in the paper, normalization)
            flow_level_map.put(flow, 1);
        }

        // save the flows that are nested within the "corresponding" flow
        for (Flow flow : flows) {
            int source_num = server_numbers.get(flow.getSource());
            int sink_num = server_numbers.get(flow.getSink());

            ArrayList<Flow> nested_flows = new ArrayList<Flow>();

            for (Flow flow_2 : flows) {
                int source_num_2 = server_numbers.get(flow_2.getSource());
                int sink_num_2 = server_numbers.get(flow_2.getSink());
                if (source_num_2 >= source_num && sink_num_2 <= sink_num && !flow_2.equals(flow)) {
                    // flow_2 is nested within flow
                    nested_flows.add(flow_2);
                    flow_level_map.put(flow_2, flow_level_map.get(flow_2) + 1);
                }
            }
            flow_nested_flows_map.put(flow, nested_flows);
        }


        for (Flow flow : flows) {
            ArrayList<Flow> nested_flows = flow_nested_flows_map.get(flow);
            ArrayList<Flow> directly_nested_flows = new ArrayList<Flow>();
            for (Flow flow_2 : nested_flows) {
                if (flow_level_map.get(flow_2) == flow_level_map.get(flow) + 1) {
                    directly_nested_flows.add(flow_2);
                }
            }
            flow_directly_nested_flows_map.put(flow, directly_nested_flows);
        }

        // C_(h,k) computation (see paper)
        for (Flow flow : flows) {
            ArrayList<Flow> diretly_nested_flows = flow_directly_nested_flows_map.get(flow);
            // The servers that are traversed by at least one of the directly nested flows
            HashSet<Server> directly_nested_flows_servers = new HashSet<Server>();

            for (Flow flow_2 : diretly_nested_flows) {
                directly_nested_flows_servers.addAll(new HashSet<Server>(flow_2.getServersOnPath()));
            }
            LinkedList<Server> remaining_servers = flow.getServersOnPath();
            remaining_servers.removeAll(directly_nested_flows_servers);
            flow_nodes_map.put(flow, remaining_servers);
        }
    }
}