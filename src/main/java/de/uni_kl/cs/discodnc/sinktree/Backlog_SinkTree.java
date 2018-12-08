package de.uni_kl.cs.discodnc.sinktree;

import de.uni_kl.cs.discodnc.AnalysisConfig;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.network.server_graph.Flow;
import de.uni_kl.cs.discodnc.network.server_graph.Server;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.network.server_graph.Turn;
import de.uni_kl.cs.discodnc.sinktree.arrivalbounds.SinkTree_AffineCurves;

public abstract class Backlog_SinkTree {
	public static double derivePmooSinkTreeAffine(ServerGraph tree, Server server,
            AnalysisConfig.ArrivalBoundMethod sink_tree_ab) throws Exception {
		SinkTree_AffineCurves sink_tree_bound = new SinkTree_AffineCurves(tree);
		ArrivalCurve arrivals_at_root = tree.getSourceFlowArrivalCurve(server);

		for (Turn turn : tree.getInTurns(server)) {
			switch (sink_tree_ab) {
				case SINKTREE_AFFINE_HOMO:
					arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundHomogeneous(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
	
				case SINKTREE_AFFINE_DIRECT:
					arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundDirect(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
					
				case SINKTREE_AFFINE_MINPLUS:
				default:
					arrivals_at_root = Curve.add(arrivals_at_root, sink_tree_bound
							.computeArrivalBoundMinPlusBackend(turn, tree.getFlows(turn), Flow.NULL_FLOW));
					continue; //implicit break
			}
		}
		return Curve.getMaxVerticalDeviation(arrivals_at_root, server.getServiceCurve()).doubleValue();
	}
}
