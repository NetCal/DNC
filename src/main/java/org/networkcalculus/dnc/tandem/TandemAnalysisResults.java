/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
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

package org.networkcalculus.dnc.tandem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.network.server_graph.Server;
//import org.networkcalculus.num.Num;
import org.networkcalculus.dnc.backend.number.Number;


public class TandemAnalysisResults {
    public Map<Server, Set<Curve>> map__server__alphas;
    protected Number delay_bound;
    protected Number backlog_bound;

    public TandemAnalysisResults() {
        this.delay_bound = new Number(0, 0);
        this.backlog_bound = new Number(0, 0);
        this.map__server__alphas = new HashMap<Server, Set<Curve>>();
    }

    public TandemAnalysisResults(Number delay_bound, Number backlog_bound, Map<Server, Set<Curve>> map__server__alphas) {
        this.delay_bound = delay_bound;
        this.backlog_bound = backlog_bound;
        if (map__server__alphas == null) {
            this.map__server__alphas = new HashMap<Server, Set<Curve>>();
        } else {
            this.map__server__alphas = map__server__alphas;
        }
    }

    public Number getDelayBound() {
        return delay_bound;
    }

    protected void setDelayBound(Number delay_bound) {
        this.delay_bound = delay_bound;
    }

    public Number getBacklogBound() {
        return backlog_bound;
    }

    protected void setBacklogBound(Number backlog_bound) {
        this.backlog_bound = backlog_bound;
    }

    public String getServerAlphasMapString() {
        if (map__server__alphas.isEmpty()) {
            return "{}";
        }

        StringBuffer result_str = new StringBuffer("{");
        for (Entry<Server, Set<Curve>> entry : map__server__alphas.entrySet()) {
            result_str.append(entry.getKey().toShortString());
            result_str.append("={");
            for (Curve ac : entry.getValue()) {
                result_str.append(ac.toString());
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
    public String toString() {
        return ("D: " + delay_bound.toString() + " - " + "B: " + backlog_bound.toString());
    }
}
