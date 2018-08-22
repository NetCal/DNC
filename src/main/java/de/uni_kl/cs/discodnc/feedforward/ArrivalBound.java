/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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

package de.uni_kl.cs.discodnc.feedforward;

import de.uni_kl.cs.discodnc.curves.ArrivalCurve;
import de.uni_kl.cs.discodnc.server_graph.Flow;
import de.uni_kl.cs.discodnc.server_graph.Link;
import de.uni_kl.cs.discodnc.server_graph.ServerGraph;

import java.util.Set;

public interface ArrivalBound {
    ServerGraph getNetwork();

    // --------------------------------------------------------------------------------------------------------------
    // Interface
    // --------------------------------------------------------------------------------------------------------------
    void setNetwork(ServerGraph network);

    public AnalysisConfig getConfiguration();

    public void setConfiguration(AnalysisConfig configuration);

    Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception;
}
