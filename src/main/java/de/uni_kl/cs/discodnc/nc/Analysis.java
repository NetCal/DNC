/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
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

package de.uni_kl.cs.discodnc.nc;

import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.network.Flow;
import de.uni_kl.cs.discodnc.network.Network;
import de.uni_kl.cs.discodnc.network.Path;

public interface Analysis {
    static TotalFlowAnalysis performTfaEnd2End(Network network, Flow flow_of_interest) throws Exception {
        TotalFlowAnalysis tfa = new TotalFlowAnalysis(network);
        tfa.performAnalysis(flow_of_interest);
        return tfa;
    }

    static TotalFlowAnalysis performTfaEnd2End(Network network, AnalysisConfig configuration,
                                               Flow flow_of_interest) throws Exception {
        TotalFlowAnalysis tfa = new TotalFlowAnalysis(network, configuration);
        tfa.performAnalysis(flow_of_interest);
        return tfa;
    }

    static SeparateFlowAnalysis performSfaEnd2End(Network network, Flow flow_of_interest) throws Exception {
        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network);
        sfa.performAnalysis(flow_of_interest);
        return sfa;
    }

    static SeparateFlowAnalysis performSfaEnd2End(Network network, AnalysisConfig configuration,
                                                  Flow flow_of_interest) throws Exception {
        SeparateFlowAnalysis sfa = new SeparateFlowAnalysis(network, configuration);
        sfa.performAnalysis(flow_of_interest);
        return sfa;
    }

    static PmooAnalysis performPmooEnd2End(Network network, Flow flow_of_interest) throws Exception {
        PmooAnalysis pmoo = new PmooAnalysis(network);
        pmoo.performAnalysis(flow_of_interest);
        return pmoo;
    }

    static PmooAnalysis performPmooEnd2End(Network network, AnalysisConfig configuration, Flow flow_of_interest)
            throws Exception {
        PmooAnalysis pmoo = new PmooAnalysis(network, configuration);
        pmoo.performAnalysis(flow_of_interest);
        return pmoo;
    }

    abstract void performAnalysis(Flow flow_of_interest) throws Exception;

    abstract void performAnalysis(Flow flow_of_interest, Path path) throws Exception;

    enum Analyses {
        TFA, SFA, PMOO
    }
}