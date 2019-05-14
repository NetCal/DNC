/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2015 - 2018 Steffen Bondorf
 * Copyright (C) 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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

package org.networkcalculus.dnc.tandem.analyses;

import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
import org.networkcalculus.num.Num;

public class TandemMatchingResults extends TandemAnalysisResults {
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