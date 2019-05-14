/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2018 Steffen Bondorf
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.tandem.TandemAnalysisResults;
import org.networkcalculus.num.Num;

public class TotalFlowResults extends TandemAnalysisResults {
    protected Map<Server, Set<Num>> map__server__D_server;
    protected Map<Server, Set<Num>> map__server__B_server;

    protected TotalFlowResults() {
        super();
        map__server__D_server = new HashMap<Server, Set<Num>>();
        map__server__B_server = new HashMap<Server, Set<Num>>();
    }

    protected TotalFlowResults(Num delay_bound, Map<Server, Set<Num>> map__server__D_server, Num backlog_bound,
                               Map<Server, Set<Num>> map__server__B_server, Map<Server, Set<ArrivalCurve>> map__server__alphas) {

        super(delay_bound, backlog_bound, map__server__alphas);

        this.map__server__D_server = map__server__D_server;
        this.map__server__B_server = map__server__B_server;
    }

    public String getServerDelayBoundMapString() {
        if (map__server__D_server.isEmpty()) {
            return "{}";
        }

        StringBuffer result_str = new StringBuffer("{");
        for (Entry<Server, Set<Num>> entry : map__server__D_server.entrySet()) {
            result_str.append(entry.getKey().toShortString());
            result_str.append("={");
            for (Num delay_bound : entry.getValue()) {
                result_str.append(delay_bound.toString());
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

    @Override
    protected void setDelayBound(Num delay_bound) {
        super.setDelayBound(delay_bound);
    }

    @Override
    protected void setBacklogBound(Num backlog_bound) {
        super.setBacklogBound(backlog_bound);
    }

    public String getServerBacklogBoundMapString() {
        if (map__server__B_server.isEmpty()) {
            return "{}";
        }

        StringBuffer result_str = new StringBuffer("{");
        for (Entry<Server, Set<Num>> entry : map__server__B_server.entrySet()) {
            result_str.append(entry.getKey().toShortString());
            result_str.append("={");
            for (Num delay_bound : entry.getValue()) {
                result_str.append(delay_bound.toString());
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