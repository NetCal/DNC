/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.4 "Centaur".
 *
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.nc;

import java.util.Map;
import java.util.Set;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.nc.analyses.PmooAnalysis;
import unikl.disco.nc.analyses.SeparateFlowAnalysis;
import unikl.disco.nc.analyses.TotalFlowAnalysis;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Path;
import unikl.disco.network.Server;
import unikl.disco.numbers.Num;

/**
 * This class contains all members and methods that are needed for more than one
 * analysis.
 * 
 * @author Andreas Kiefer
 * @author Steffen Bondorf
 * 
 */
public abstract class Analysis {
	public enum Analyses { TFA, SFA, PMOO };
	
	protected Network network;
	protected AnalysisConfig configuration;
	
	protected AnalysisResults result;
	
	public abstract void performAnalysis( Flow flow_of_interest ) throws Exception;

	public abstract void performAnalysis( Flow flow_of_interest, Path path ) throws Exception;
	
	protected Analysis() {}

	protected Analysis( Network network ) {
		this.network = network;
		this.configuration = new AnalysisConfig();
	}
	
	protected Analysis( Network network, AnalysisConfig configuration ) {
		this.network = network;
		this.configuration = configuration;
	}
	
	public static TotalFlowAnalysis performTfaEnd2End( Network network, Flow flow_of_interest ) throws Exception {
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network );
		tfa.performAnalysis( flow_of_interest );
		return tfa;
	}
	
	public static TotalFlowAnalysis performTfaEnd2End( Network network, AnalysisConfig configuration, Flow flow_of_interest ) throws Exception {
		TotalFlowAnalysis tfa = new TotalFlowAnalysis( network, configuration );
		tfa.performAnalysis( flow_of_interest );
		return tfa;
	}

	public static SeparateFlowAnalysis performSfaEnd2End( Network network, Flow flow_of_interest ) throws Exception {
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network );
		sfa.performAnalysis( flow_of_interest );
		return sfa;
	}
	
	public static SeparateFlowAnalysis performSfaEnd2End( Network network, AnalysisConfig configuration, Flow flow_of_interest ) throws Exception {
		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( network, configuration );
		sfa.performAnalysis( flow_of_interest );
		return sfa;
	}
	
	public static PmooAnalysis performPmooEnd2End( Network network, Flow flow_of_interest ) throws Exception {
		PmooAnalysis pmoo = new PmooAnalysis( network );
		pmoo.performAnalysis( flow_of_interest );
		return pmoo;
	}
	
	public static PmooAnalysis performPmooEnd2End( Network network, AnalysisConfig configuration, Flow flow_of_interest ) throws Exception {
		PmooAnalysis pmoo = new PmooAnalysis( network, configuration );
		pmoo.performAnalysis( flow_of_interest );
		return pmoo;
	}
	
	public Network getNetwork() {
		return network;
	}
	
	/**
	 * Returns the delay bound of the analysis.
	 * 
	 * @return the delay bound
	 */
	public Num getDelayBound() {
		return result.delay_bound;
	};
	
	/**
	 * Returns the backlog bound of the analysis.
	 * 
	 * @return the backlog bound
	 */
	public Num getBacklogBound() {
		return result.backlog_bound;
	};

	/**
	 * For TFA this is the whole traffic at a server because
	 * you do not separate the flow of interest during analysis.
	 * 
	 * For SFA and PMOO you will get the arrival bounds of
	 * the cross-traffic at every server.
	 * 
	 * @return Mapping from the server to the server's arrival bound
	 */
	public Map<Server, Set<ArrivalCurve>> getServerAlphasMap(){
		return result.map__server__alphas;
	}
	
	public String getServerAlphasMapString(){
		return result.getServerAlphasMapString();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}