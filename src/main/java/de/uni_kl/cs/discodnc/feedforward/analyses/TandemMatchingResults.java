package de.uni_kl.cs.discodnc.feedforward.analyses;

import java.util.Map;
import java.util.Set;

import de.uni_kl.cs.discodnc.network.Server;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.curves.ServiceCurve;
import de.uni_kl.cs.discodnc.feedforward.AnalysisResults;

public class TandemMatchingResults extends AnalysisResults {
	protected Set<ServiceCurve> betas_e2e;

	protected TandemMatchingResults(){}
			
	protected TandemMatchingResults( Num delay_bound,
						  Num backlog_bound,
						  Set<ServiceCurve> betas_e2e,
						  Map<Server,Set<ArrivalCurve>> map__server__alphas ) {

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