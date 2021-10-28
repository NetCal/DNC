package org.networkcalculus.dnc.feedforward;

import org.apache.commons.math3.util.Pair;
import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.bounds.disco.con_pw_affine.Output_Disco_ConPwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.*;
import org.networkcalculus.dnc.tandem.analyses.FIFOTandemAnalysis;
import org.networkcalculus.dnc.utils.SetUtils;

import java.util.Set;


/**
 *
 * @author Steffen Bondorf
 * @author Alexander Scheffler
 *
 */
public class ArrivalBoundDispatchFIFO {
    /**
     *
     * @param server_graph
     * @param configuration
     * @param server
     * @param flows_to_bound
     * @return
     * @throws Exception
     */
    public static ArrivalCurve computeArrivalBound( ServerGraph server_graph, AnalysisConfig configuration, Server server,
                                                    Set<Flow> flows_to_bound ) throws Exception {
        // ftb = flows to bound
        ArrivalCurve ag_ac = Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();

        // Identify the subset of flows from flows_to_bound that start at server
        Set<Flow> subset_from_ftb_start_at_server = SetUtils.getIntersection(flows_to_bound, server_graph.getSourceFlows(server));
        for (Flow flow: subset_from_ftb_start_at_server)
        {
            ag_ac = Curve.getUtils().add(ag_ac, flow.getArrivalCurve());
        }

        for(Turn current_turn : server_graph.getInTurns(server))
        {
            Server pred_current_turn = current_turn.getSource();
            Set<Flow> subset_from_ftb_through_current_turn = SetUtils.getIntersection(flows_to_bound, server_graph.getFlows(current_turn));
            if(!subset_from_ftb_through_current_turn.isEmpty())
            {
                Pair<Server, Path> splitting_server_and_path = server_graph.findSplittingServerAndPathFIFO(pred_current_turn, subset_from_ftb_through_current_turn);
                ArrivalCurve ac_for_subset_from_ftb_on_splitting_server_to_pred_current_turn = ArrivalBoundDispatchFIFO.computeArrivalBound(server_graph, configuration, splitting_server_and_path.getFirst(), subset_from_ftb_through_current_turn);
                FIFOTandemAnalysis fifo_analysis = new FIFOTandemAnalysis(server_graph, configuration);
                ServiceCurve sc_for_subset_from_ftb_on_splitting_server_to_pred_current_turn = fifo_analysis.getServiceCurve(subset_from_ftb_through_current_turn, ac_for_subset_from_ftb_on_splitting_server_to_pred_current_turn, splitting_server_and_path.getSecond(),true);
                ArrivalCurve output_ac_for_subset_from_ftb_at_server = Output_Disco_ConPwAffine.computeFIFOOutputBound( ac_for_subset_from_ftb_on_splitting_server_to_pred_current_turn, sc_for_subset_from_ftb_on_splitting_server_to_pred_current_turn);
                ag_ac = Curve.getUtils().add(ag_ac, output_ac_for_subset_from_ftb_at_server);
            }
        }
        return ag_ac;
    }
}
