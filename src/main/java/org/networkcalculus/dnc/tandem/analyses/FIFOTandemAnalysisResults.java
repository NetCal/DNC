package org.networkcalculus.dnc.tandem.analyses;

//import org.networkcalculus.dnc.curves.ServiceCurve;

import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.number.Number;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
//import org.networkcalculus.num.Num;

import java.util.Set;

public class FIFOTandemAnalysisResults extends TandemAnalysisResults {
    protected Set<Curve> betas_e2e;

    protected FIFOTandemAnalysisResults() {
    }

    @Override
    protected void setDelayBound(Number delay_bound) {
        super.setDelayBound(delay_bound);
    }

    @Override
    protected void setBacklogBound(Number backlog_bound) {
        super.setBacklogBound(backlog_bound);
    }
}

