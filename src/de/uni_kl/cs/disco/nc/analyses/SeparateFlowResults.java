/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
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

package de.uni_kl.cs.disco.nc.analyses;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.nc.AnalysisResults;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;

import java.util.Set;

public class SeparateFlowResults extends AnalysisResults {
	protected Set<ServiceCurve> betas_e2e;
	protected Map<Server, Set<ServiceCurve>> map__server__betas_lo;

	protected SeparateFlowResults() {
		super();
		betas_e2e = new HashSet<ServiceCurve>();
		map__server__betas_lo = new HashMap<Server, Set<ServiceCurve>>();
	}

	protected SeparateFlowResults(Num delay_bound, Num backlog_bound, Set<ServiceCurve> betas_e2e,
			Map<Server, Set<ServiceCurve>> map__server__betas_lo, Map<Server, Set<ArrivalCurve>> map__server__alphas) {

		super(delay_bound, backlog_bound, map__server__alphas);

		this.betas_e2e = betas_e2e;
		this.map__server__betas_lo = map__server__betas_lo;
	}

	@Override
	protected void setDelayBound(Num delay_bound) {
		super.setDelayBound(delay_bound);
	}

	@Override
	protected void setBacklogBound(Num backlog_bound) {
		super.setBacklogBound(backlog_bound);
	}

	public String getServerLeftOverBetasMapString() {
		if (map__server__betas_lo.isEmpty()) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer("{");

		for (Entry<Server, Set<ServiceCurve>> entry : map__server__betas_lo.entrySet()) {
			result_str.append(entry.getKey().toShortString());
			result_str.append("={");
			for (ServiceCurve beta_lo : entry.getValue()) {
				result_str.append(beta_lo.toString());
				result_str.append(",");
			}
			result_str.deleteCharAt(result_str.length() - 1); // Remove the trailing comma.
			result_str.append("}");
			result_str.append(", ");
		}
		result_str.delete(result_str.length() - 2, result_str.length()); // Remove the trailing blank space and comma.

		result_str.append("}");

		return result_str.toString();
	}
}