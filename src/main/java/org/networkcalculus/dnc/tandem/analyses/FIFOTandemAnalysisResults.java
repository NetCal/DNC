package org.networkcalculus.dnc.tandem.analyses;


import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
import org.networkcalculus.num.Num;

import java.util.Map;
import java.util.Set;

public class FIFOAnalysisResults extends TandemAnalysisResults {
    protected Set<ServiceCurve> betas_e2e;

    protected FIFOAnalysisResults() {
    }

    protected FIFOAnalysisResults(Num delay_bound, Num backlog_bound, Set<ServiceCurve> betas_e2e,
                                  Map<Server, Set<ArrivalCurve>> map__server__alphas) {

        super(delay_bound, backlog_bound, map__server__alphas);

        this.betas_e2e = betas_e2e;
    }

    @Override
    protected void setDelayBound(Num delay_bound) {
        super.setDelayBound(delay_bound);
    }

    @Override
    protected void setBacklogBound(Num backlog_bound) {
        super.setBacklogBound(backlog_bound);
    }
}

