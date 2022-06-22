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
         LUDB_FF, LB_FF, DS_FF
    }

    public static mode selected_mode;

    /////////////////// /////////////////// /////// LUDB
    private final ArrayList<Flow> crossflowList = new ArrayList(); // mapping of id to Flow for LP computation (LUDB) (index coincides with id)
    private double curr_min_delay_ludb; // for the on the run version (i.e. the one that does not compute all decompositions a priori)
    private Map<Integer, Double> curr_best_s_setting; //  curr_lb + s <=> theta (note that s >= 0!) [s from LUDB paper fifo l.o. theorem, theta is free parameter in the general fifo left over theorem]
    /////////////////// /////////////////// ///////

    private Map<Flow, Num> lb_thetas_global_min_so_far;
    private Map<Flow, Num> ub_thetas_global_max_so_far;
    private Map<Flow, Num> lb_thetas_safe; // "safe" but rather trivial lower bound on the thetas
    private Map<Flow, Num> ub_thetas_safe; // "safe" but rather trivial upper bound on the thetas
    private Map<Flow, Num> stepsize_thetas; // stepsize per theta
    private Num curr_min_delay;


    public static Num xi = Num.getUtils(Calculator.getInstance().getNumBackend()).create(0.5);
    public static Num c = Num.getUtils(Calculator.getInstance().getNumBackend()).create(5);
    public static Num e = Num.getUtils(Calculator.getInstance().getNumBackend()).create(0.0001); // 10^-4


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

            case LB_FF:
                computeServiceCurve_LB_FF();
                break;

            case DS_FF:
                computeServiceCurve_DS_FF();
                break;

            case LUDB_FF:
                computeLUDB();
                break;
        }
        return e2e;
    }


    public void computeServiceCurve_LB_FF() throws Exception {
        lb_thetas_global_min_so_far = new HashMap<>();

        e2e = computeLeftOverSC(nestingTree, true, null, true);

        compute_flows_without_foi_ordered = false;
    }

    // post order traversal / computation
    // If the leftovers (in the tree) are already set it just overrides them, e.g. if we call that method with different theta combinations
    /**
     * @param node
     * @param computeLBs Creates the e2e-SC for foi using lower-bound thetas -- the method computes them as well, ignore the last param
     *                   // * @param computeUBs Similar
     * @param thetas     Given the thetas, it creates the e2e-SC for foi using those thetas
     * @return e2e-SC for foi
     * @throws Exception
     */
    private ServiceCurve computeLeftOverSC(TNode node, boolean computeLBs, Map<Flow, Num> thetas, boolean computeAll) throws Exception {

        ArrayList<TNode> children = node.getChildren();

        if (computeAll) {
            for (TNode child : children) {
                computeLeftOverSC(child, computeLBs, thetas, computeAll);
            }
        }

        // compute fifo left over sc for current node
        // (left over sc for descendants (children) is already computed)


        // always neutral element at this point in time

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

            if (compute_flows_without_foi_ordered) {
                Flow curr_flow = (Flow) node.getInf();
                if (!curr_flow.equals(foi)) {
                    flows_without_foi_ordered.add(curr_flow);
                    flow_tnode_map.put(curr_flow, node);
                }

            }

            for (TNode child : children) {
                if (child.getInf() instanceof LinkedList) {
                    // child represents C_(h,k), so we don't need to "subtract" some AC from that left over sc
                    leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child.getLeftover());
                } else {
                    ServiceCurve child_fifo_leftover = null;
                    Num flow_theta = null;

                    // child represents a flow (h,k), so we need to "subtract" the AC of flow (h,k) from the left over sc  stored in that node

                    if (computeLBs) {
                        // use the theta that we get at the x-position where the service curve (child.getLeftover()) has a y-value that equals the burst of the arrival curve
                        ArrivalCurve ac = ((Flow) child.getInf()).getArrivalCurve();
                        ServiceCurve sc = child.getLeftover();
                        Num burst = ac.getBurst();

                        Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;
                        flow_theta = curve.f_inv(burst);


                        lb_thetas_global_min_so_far.put(((Flow) child.getInf()), flow_theta);
                    } else {
                        // use a theta from the map "thetas"
                        Flow flow = (Flow) child.getInf();
                        flow_theta = thetas.get(flow);

                    }


                    child_fifo_leftover = LeftOverService_Disco_PwAffine.fifoMux(child.getLeftover(), ((Flow) child.getInf()).getArrivalCurve(), flow_theta);

                    leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child_fifo_leftover);

                }
            }
            node.setLeftover(leftover);
        }
        return leftover;
    }

    public void computeServiceCurve_DS_FF() throws Exception {
        // compute SAFE bounds first
        computeSafeBoundsLb();

        Map<Flow, Num> safe_lb_thetas = new HashMap<>();
        safe_lb_thetas.putAll(lb_thetas_safe);


        e2e = computeLeftOverSC(nestingTree, false, safe_lb_thetas, true);

        compute_flows_without_foi_ordered = false;

        Num delay = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), e2e);

        curr_min_delay = delay;

        computeSafeBoundsUb(curr_min_delay);

        // compute initial current and lower bounds
        lb_thetas_global_min_so_far = new HashMap<>();

        e2e = computeLeftOverSC(nestingTree, true, null, true);

        compute_flows_without_foi_ordered = false;

        delay = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), e2e);

        curr_min_delay = delay;

        computeSafeBoundsUb(curr_min_delay); // update safe upper bounds bc delay smaller

        ub_thetas_global_max_so_far = new HashMap<>();

        // compute upper bounds
        computeUpperBounds(nestingTree, delay);

        Map<Flow, Num> curr_theta_comb = new HashMap<>();

        curr_theta_comb.putAll(lb_thetas_global_min_so_far);


        compute_stepsize_directed_search();

        // check if we even have crossflows
        if (flows_without_foi_ordered.size() > 0) {
            Flow curr_flow = flows_without_foi_ordered.get(0);
            directed_search_w_armijo_linesearch(curr_theta_comb);
        }

    }

    private void computeSafeBoundsLb() {
        lb_thetas_safe = new HashMap<Flow, Num>();
        Num foi_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).createZero();
        for (Server server : foi_path.getServers()) {
            foi_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).add(foi_path_latency, server.getServiceCurve().getLatency());
        }

        for (Flow xflow : flows) {
            if (!(xflow.equals(foi))) {
                Num xflow_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).createZero();
                for (Server server : xflow.getServersOnPath()) {
                    xflow_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).add(xflow_path_latency, server.getServiceCurve().getLatency());
                }
                lb_thetas_safe.put(xflow, xflow_path_latency);
            }
        }
    }

    private void computeSafeBoundsUb(Num delay) {
        ub_thetas_safe = new HashMap<Flow, Num>();

        Num foi_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).createZero();
        for (Server server : foi_path.getServers()) {
            foi_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).add(foi_path_latency, server.getServiceCurve().getLatency());
        }

        for (Flow xflow : flows) {
            if (!(xflow.equals(foi))) {
                Num xflow_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).createZero();
                for (Server server : xflow.getServersOnPath()) {
                    xflow_path_latency = Num.getUtils(Calculator.getInstance().getNumBackend()).add(xflow_path_latency, server.getServiceCurve().getLatency());
                }
                Num ub = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(delay, foi_path_latency);
                ub = Num.getUtils(Calculator.getInstance().getNumBackend()).add(ub, xflow_path_latency);
                ub_thetas_safe.put(xflow, ub);
            }
        }
    }


    private void computeUpperBounds(TNode node, Num delay) {
        if (delay.eq(Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity())) {
            throw new IllegalArgumentException("delay infinity! (all set to lb)");
        }

        if (lb_thetas_global_min_so_far == null) {
            throw new IllegalArgumentException("lower bound thetas not computed yet");
        }


        ArrayList<TNode> children = node.getChildren();


        if (!(node.getInf() instanceof LinkedList)) {
            // node is a t-node, i.e. represents a flow (h,k)

            // Construction of constraints for flows on the second (or higher) level of the tree
            children = node.getChildren();

            // compute sum of server-latencies wrt the servers that are "children" of the node that holds the foi
            Num sum_server_latencies = Num.getUtils(Calculator.getInstance().getNumBackend()).getZero();
            for (TNode child : children) {
                if (child.getInf() instanceof LinkedList) {
                    sum_server_latencies = Num.getUtils(Calculator.getInstance().getNumBackend()).add(sum_server_latencies, child.getLeftover().getLatency());
                }
            }

            // sum of the lower bound thetas
            Num sum_theta_lb = Num.getUtils(Calculator.getInstance().getNumBackend()).getZero();

            for (TNode child : children) {
                if (!(child.getInf() instanceof LinkedList)) {
                    Flow flow_child = (Flow) child.getInf();
                    sum_theta_lb = Num.getUtils(Calculator.getInstance().getNumBackend()).add(sum_theta_lb, lb_thetas_global_min_so_far.get(flow_child));
                }
            }

            for (TNode child : children) {
                if (!(child.getInf() instanceof LinkedList)) {
                    // for every "flow" on this second level we have a constraint
                    Flow flow_curr = (Flow) child.getInf();
                    Num sum_theta_lb_without_curr = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(sum_theta_lb, lb_thetas_global_min_so_far.get(flow_curr));
                    Num ub = null;
                    // Have to differentiate between children of foi (2nd level of tree) and children that are on higher levels
                    if (node.getInf().equals(foi)) {
                        ub = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(delay, sum_server_latencies);
                    } else {
                        ub = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(ub_thetas_global_max_so_far.get(node.getInf()), sum_server_latencies);
                    }
                    ub = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(ub, sum_theta_lb_without_curr);

                    ub_thetas_global_max_so_far.put(flow_curr, ub);
                }
            }

        }

        // recursive call to children
        for (TNode child : children) {
            computeUpperBounds(child, delay);
        }
    }

    private void compute_stepsize_directed_search() {
        stepsize_thetas = new HashMap<>();
        for (Flow flow : flows) {
            if (!(flow.equals(foi))) {
                Num lb = lb_thetas_global_min_so_far.get(flow);
                Num ub = ub_thetas_global_max_so_far.get(flow);
                Num delta = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(ub, lb);
                Num gran_minus_1 = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(c, Num.getUtils(Calculator.getInstance().getNumBackend()).create(1));
                Num stepsize = Num.getUtils(Calculator.getInstance().getNumBackend()).div(delta, gran_minus_1);

                stepsize_thetas.put(flow, stepsize);
            }
        }
    }


    private void directed_search_w_armijo_linesearch(Map<Flow, Num> thetas) throws Exception {

        // int nr_decrements = 0;

        Num delay_old = curr_min_delay;

        Map<Flow, Num> thetas_old = new HashMap<Flow, Num>();
        thetas_old.putAll(thetas);

        int success_moves = 0;
        int max_success_moves = 0;

        while (minStepsize().gt(e))
        {
            Map<Flow, Num> thetas_new = find_best_nearby(thetas_old);


            boolean at_least_one_nearby_improved = false;
            Num delay_new = curr_min_delay;

            if (success_moves > max_success_moves) {
                max_success_moves = success_moves;
            }
            success_moves = 0;


            Map<Flow, Num> delta_armijo_search = new HashMap<>();
            Map<Flow, Num> x_start_armijo_search = new HashMap<>();
            int armijo_counter = -1;

            while (delay_new.lt(delay_old)) {
                // Pattern Move with Armijo Line Search

                if (!at_least_one_nearby_improved) {
                    for (Flow flow : flows) {
                        if (!(flow.equals(foi))) {
                            Num theta_old = thetas_old.get(flow);
                            Num theta_new = thetas_new.get(flow);
                            Num theta_dif = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(theta_new, theta_old);
                            delta_armijo_search.put(flow, theta_dif);
                            x_start_armijo_search.put(flow, theta_new);
                        }
                    }
                }

                armijo_counter++;
                at_least_one_nearby_improved = true;
                success_moves++;

                computeSafeBoundsUb(curr_min_delay); // update safe upper bounds


                Map<Flow, Num> thetas_pot = new HashMap<Flow, Num>();

                boolean safe_bounds_violation = false;
                for (Flow flow : flows) {
                    if (!(flow.equals(foi))) {
                        Num theta_start = x_start_armijo_search.get(flow);
                        double theta__exp = Math.pow(2, armijo_counter);


                        Num num_theta_dif_exp = Num.getUtils(Calculator.getInstance().getNumBackend()).create(theta__exp);
                        Num delta = delta_armijo_search.get(flow);
                        Num delta_times_num_theta_dif_exp = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(num_theta_dif_exp, delta);
                        Num theta_pot = Num.getUtils(Calculator.getInstance().getNumBackend()).add(theta_start, delta_times_num_theta_dif_exp);

                        if (theta_pot.lt(lb_thetas_safe.get(flow))) {
                            // would violate safe lower bound
                            safe_bounds_violation = true;
                        }

                        if (theta_pot.gt(ub_thetas_safe.get(flow))) {
                            // would violate safe upper bound
                            safe_bounds_violation = true;
                        }
                        thetas_pot.put(flow, theta_pot);
                    }
                }


                delay_old = delay_new;
                thetas_old = thetas_new;

                if (safe_bounds_violation) {
                    break;
                }

                ServiceCurve e2e_pot = computeLeftOverSC(nestingTree, false, thetas_pot, true); // costly, complete built up of the nesting tree
                Num delay_pot = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), e2e_pot);


                if (delay_pot.geq(delay_new)) {
                    // Move not successful
                    computeLeftOverSC(nestingTree, false, thetas_old, true); // costly, complete rebuilt of the old(!) nesting tree
                    break;
                }

                // Move successful
                e2e = e2e_pot;
                delay_new = delay_pot;
                thetas_new = thetas_pot;

            }

            if (!at_least_one_nearby_improved) {
                reduce_stepsize_directed_search(xi);
            }
        }
    }

    /**
     * DS-FF method that searches in the current environment for a "better" point. This phase is often called Exploratory Phase.
     */
    private Map<Flow, Num> find_best_nearby(Map<Flow, Num> thetas) throws Exception {
        Map<Flow, Num> thetas_new = new HashMap<Flow, Num>();
        thetas_new.putAll(thetas);

        for (int i = 0; i < flows_without_foi_ordered.size(); i++) {
            Flow flow = flows_without_foi_ordered.get(i);
            Num delta = stepsize_thetas.get(flow);
            Num theta = thetas_new.get(flow);

            Num theta_low = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(theta, delta);
            Num delay_low = Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            ServiceCurve e2e_low = null;
            if (theta_low.gt(lb_thetas_safe.get(flow))) {
                thetas_new.put(flow, theta_low);
                e2e_low = computeFoiLeftOverSCUpwards(flow_tnode_map.get(flow).getParent(), thetas_new);
                delay_low = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), e2e_low);
            }


            Num theta_high = Num.getUtils(Calculator.getInstance().getNumBackend()).add(theta, delta);
            Num delay_high = Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
            ServiceCurve e2e_high = null;
            if (theta_high.lt(ub_thetas_safe.get(flow))) {
                thetas_new.put(flow, theta_high);
                e2e_high = computeFoiLeftOverSCUpwards(flow_tnode_map.get(flow).getParent(), thetas_new);
                delay_high = Calculator.getInstance().getDncBackend().getBounds().delayFIFO(foi.getArrivalCurve(), e2e_high);
            }

            Num min_delay = Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity();

            if (curr_min_delay.leq(delay_low)) {
                if (curr_min_delay.leq(delay_high)) {
                    // curr_min_delay stays the minimum, i.e. dont change theta for the current flow
                    // however, we need to update the nesting tree again  // we could copy nesting trees for those cases
                    thetas_new.put(flow, theta);
                    computeFoiLeftOverSCUpwards(flow_tnode_map.get(flow).getParent(), thetas_new);
                } else {
                    // delay_high is current min
                    // thetas_new and the nesting tree are up to date
                    curr_min_delay = delay_high;
                    e2e = e2e_high;
                }
            } else {
                if (delay_low.leq(delay_high)) {
                    // however, we need to update the nesting tree again  // we could copy nesting trees for those cases
                    thetas_new.put(flow, theta_low);
                    computeFoiLeftOverSCUpwards(flow_tnode_map.get(flow).getParent(), thetas_new);
                    curr_min_delay = delay_low;
                    e2e = e2e_low;

                } else {
                    // delay_high is current min
                    // thetas_new and the nesting tree are up to date
                    curr_min_delay = delay_high;
                    e2e = e2e_high;
                }
            }
        }

        return thetas_new;
    }

    /**
     * Assume that this method is called from a flow node and that the map (@thetas) contains a value for each crossflow.
     *
     * @param node
     * @param thetas
     * @return
     * @throws Exception
     */
    private ServiceCurve computeFoiLeftOverSCUpwards(TNode node, Map<Flow, Num> thetas) throws Exception {
        ArrayList<TNode> children = node.getChildren();
        ServiceCurve leftover = Curve.getFactory().createZeroDelayInfiniteBurst();
        // node is a t-node, i.e. represents a flow (h,k)
        for (TNode child : children) {
            if (child.getInf() instanceof LinkedList) {
                // child represents C_(h,k), so we don't need to "subtract" some AC from that left over sc
                leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child.getLeftover());
            } else {
                ServiceCurve child_fifo_leftover = null;
                Num flow_theta = null;

                // child represents a flow (h,k), so we need to "subtract" the AC of flow (h,k) from the left over sc  stored in that node
                // use a theta from the map "thetas"
                Flow flow = (Flow) child.getInf();
                flow_theta = thetas.get(flow);

                child_fifo_leftover = LeftOverService_Disco_PwAffine.fifoMux(child.getLeftover(), ((Flow) child.getInf()).getArrivalCurve(), flow_theta);

                leftover = Calculator.getInstance().getMinPlus().convolve(leftover, child_fifo_leftover);
            }
        }
        node.setLeftover(leftover);

        Flow flow = (Flow) node.getInf();
        if (flow.equals(foi)) {
            // we are done since the Left-Over Service Curve for the foi is available
            return leftover;
        } else {
            return computeFoiLeftOverSCUpwards(node.getParent(), thetas);
        }
    }

    /**
     * @param r 0 < r < 1
     */
    private void reduce_stepsize_directed_search(Num r) {
        for (Flow flow : flows) {
            if (!(flow.equals(foi))) {
                Num curr_stepsize = stepsize_thetas.get(flow);
                Num stepsize_new = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(curr_stepsize, r);
                stepsize_thetas.put(flow, stepsize_new);
            }
        }
    }

    private Num minStepsize() {
        Num min_sp = Num.getUtils(Calculator.getInstance().getNumBackend()).createPositiveInfinity();
        for (Flow flow : flows) {
            if (!(flow.equals(foi))) {
                Num sp = stepsize_thetas.get(flow);
                if (sp.lt(min_sp)) {
                    min_sp = sp;
                }
            }
        }
        return min_sp;
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



    public TNode onlyComputeNestingTree() throws Exception {
        computeNestingSets();
        createNestingTreeOrdered();
        return nestingTree;
    }

    private void createNestingTreeOrdered() {
        nestingTree = new TNode(foi, null);
        createNestingTreeLevelOrdered(foi, nestingTree);
    }

    private void createNestingTreeLevelOrdered(Flow flow, TNode subTree) {
        try {
            ArrayList<Flow> nested_flows = flow_directly_nested_flows_map.get(flow);
            LinkedList<Server> nested_flows_servers = flow_nodes_map.get(flow);
            if (nested_flows_servers != null && !nested_flows_servers.isEmpty()) {
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