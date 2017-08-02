/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta1 "Chimera".
 *
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

package de.uni_kl.cs.disco.nc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Server;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;

import java.util.Set;

public class AnalysisResults {
    public Map<Server, Set<ArrivalCurve>> map__server__alphas;
    protected Num delay_bound;
    protected Num backlog_bound;

    public AnalysisResults() {
        this.delay_bound = NumFactory.createNaN();
        this.backlog_bound = NumFactory.createNaN();
        this.map__server__alphas = new HashMap<Server, Set<ArrivalCurve>>();
    }

    public AnalysisResults(Num delay_bound,
                           Num backlog_bound,
                           Map<Server, Set<ArrivalCurve>> map__server__alphas) {
        this.delay_bound = delay_bound;
        this.backlog_bound = backlog_bound;
        if (map__server__alphas == null) {
            this.map__server__alphas = new HashMap<Server, Set<ArrivalCurve>>();
        } else {
            this.map__server__alphas = map__server__alphas;
        }
    }

    public Num getDelayBound() {
        return delay_bound;
    }

    protected void setDelayBound(Num delay_bound) {
        this.delay_bound = delay_bound;
    }

    public Num getBacklogBound() {
        return backlog_bound;
    }

    protected void setBacklogBound(Num backlog_bound) {
        this.backlog_bound = backlog_bound;
    }

    public String getServerAlphasMapString() {

        if (map__server__alphas.isEmpty()) {
            return "{}";
        }

        StringBuffer result_str = new StringBuffer("{");
        for (Entry<Server, Set<ArrivalCurve>> entry : map__server__alphas.entrySet()) {
            result_str.append(entry.getKey().toShortString());
            result_str.append("={");
            for (ArrivalCurve ac : entry.getValue()) {
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
}
