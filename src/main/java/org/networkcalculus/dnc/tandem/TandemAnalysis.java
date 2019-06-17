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

import java.util.Map;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.network.server_graph.Flow;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;
import org.networkcalculus.num.Num;

public interface TandemAnalysis {
    enum Analyses {
        TFA, SFA, PMOO, TMA
    }
    
    // ----------------------------------------------------------------------------------------------------
    // Convenience methods to start tandem analyses.
    // ----------------------------------------------------------------------------------------------------
    static TotalFlowAnalysis performTfaEnd2End(ServerGraph server_graph, Flow flow_of_interest) throws Exception {
        TotalFlowAnalysis tfa = new TotalFlowAnalysis(server_graph);
        tfa.performAnalysis(flow_of_interest);
        return tfa;
    }

    static TotalFlowAnalysis performTfaEnd2End(ServerGraph server_graph, AnalysisConfig configuration,
                                               Flow flow_of_interest) throws Exception {
        TotalFlowAnalysis tfa = new TotalFlowAnalysis(server_graph, configuration);
        tfa.performAnalysis(flow_of_interest);
        return tfa;
    }

    static SeparateFlowAnalysis performSfaEnd2End(ServerGraph server_graph, Flow flow_of_interest) throws Exception {
        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(server_graph);
        sfa.performAnalysis(flow_of_interest);
        return sfa;
    }

    static SeparateFlowAnalysis performSfaEnd2End(ServerGraph server_graph, AnalysisConfig configuration,
                                                  Flow flow_of_interest) throws Exception {
        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(server_graph, configuration);
        sfa.performAnalysis(flow_of_interest);
        return sfa;
    }

    static PmooAnalysis performPmooEnd2End(ServerGraph server_graph, Flow flow_of_interest) throws Exception {
        PmooAnalysis pmoo = new PmooAnalysis(server_graph);
        pmoo.performAnalysis(flow_of_interest);
        return pmoo;
    }

    static PmooAnalysis performPmooEnd2End(ServerGraph server_graph, AnalysisConfig configuration, Flow flow_of_interest)
            throws Exception {
        PmooAnalysis pmoo = new PmooAnalysis(server_graph, configuration);
        pmoo.performAnalysis(flow_of_interest);
        return pmoo;
    }

    // ----------------------------------------------------------------------------------------------------
    // Methods to start an analysis.
    // ----------------------------------------------------------------------------------------------------
    void performAnalysis(Flow flow_of_interest) throws Exception;

    void performAnalysis(Flow flow_of_interest, Path path) throws Exception;

    // ----------------------------------------------------------------------------------------------------
    // Convenience methods to access results and the server graph.
    // ----------------------------------------------------------------------------------------------------
    Num getDelayBound();

    Num getBacklogBound();
    
    Map<Server, Set<ArrivalCurve>> getServerAlphasMap();
    
    String getServerAlphasMapString();
    
    ServerGraph getServerGraph();
}