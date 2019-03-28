/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
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

package org.networkcalculus.dnc;

import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig.ArrivalBoundMethod;
import org.networkcalculus.dnc.AnalysisConfig.MaxScEnforcement;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.network.server_graph.ServerGraph;
import org.networkcalculus.dnc.tandem.analyses.PmooAnalysis;
import org.networkcalculus.dnc.tandem.analyses.SeparateFlowAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TandemMatchingAnalysis;
import org.networkcalculus.dnc.tandem.analyses.TotalFlowAnalysis;

public class CompFFApresets {
	private ServerGraph server_graph;
	
	public TotalFlowAnalysis tf_analysis;		// TFA + aggrPBOOAB
	public SeparateFlowAnalysis sf_analysis;	// SFA + aggrPBOOAB
	public PmooAnalysis pmoo_analysis;			// PMOO + aggrPMOOAB
	
	public TotalFlowAnalysis tfa_segrPBOOAB;	// TFA + segrPBOOAB
	public SeparateFlowAnalysis sfa_segrPBOOAB;	// SFA + segrPBOOAB
	public PmooAnalysis pmoo_segrPMOOAB;		// PMOO + segrPMOOAB
	
	public SeparateFlowAnalysis sfa_aggrAB;		// SFA + aggrAB
	public PmooAnalysis pmoo_aggrAB;			// PMOO + aggrAB
	
	public TandemMatchingAnalysis tandem_matching_analysis;			// TFA + aggrTMAB	
	public TandemMatchingAnalysis tandem_matching_bcap_analysis;	// TFA + aggrTMAB + Burst Cap by server backlog

	public SeparateFlowAnalysis sfa_MMB18AB;	// SFA + aggrAB + segrPMOOAB
	public PmooAnalysis pmoo_MMB18AB;			// PMOO + aggrAB + segrPMOOAB
	
	public CompFFApresets( ServerGraph server_graph ) {
		this.server_graph = server_graph;

		// --------------------------------------------------------------------------------------------------------------
		// Arrival Bounding Configurations and Documentation
		// --------------------------------------------------------------------------------------------------------------
		
		// Default behavior of dnclib-1.x was
		// * TFA + aggrPBOOAB,
		// * SFA + aggrPBOOAB, and
		// * PMOO + aggrPMOOAB
		
		// The literature also proposes a per-flow extension to individual cross flows,
		// translating to segregates arrival boundings matching the principle on the flow of interest's path:
		// * TFA + segrPBOOAB,
		// * SFA + segrPBOOAB, and
		// * PMOO + segrPMOOAB
		
		// aggrPBOOAB + aggrPMOOAB = aggrAB. See:	
		//   Calculating Accurate End-to-End Delay Bounds – You Better Know Your Cross-Traffic 
		//   (Steffen Bondorf, Jens B. Schmitt),
		//   In Proc. of the Int. Conference on Performance Evaluation Methodologies and Tools (ValueTools), 2015.
		Set<ArrivalBoundMethod> aggrAB = new HashSet<ArrivalBoundMethod>();
		aggrAB.add( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		aggrAB.add( ArrivalBoundMethod.AGGR_PMOO );
		
		// aggrAB + aggrPMOOAB can outperform aggrAB. See:	
		//   Catching Corner Cases in Network Calculus – Flow Segregation Can Improve Accuracy 
		//   (Steffen Bondorf, Paul Nikolaus, Jens B. Schmitt),
		//   In Proc. of the Int. GI/ITG Conference on Measurement, Modelling and Evaluation of Computing Systems (MMB), 2018.
		Set<ArrivalBoundMethod> MMB18AB = new HashSet<ArrivalBoundMethod>();
		MMB18AB.add( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		MMB18AB.add( ArrivalBoundMethod.AGGR_PMOO );
		MMB18AB.add( ArrivalBoundMethod.SEGR_PMOO );
		

		// --------------------------------------------------------------------------------------------------------------
		// Calculator Configurations
		// --------------------------------------------------------------------------------------------------------------
		
		AnalysisConfig base_config = new AnalysisConfig();
		base_config.setConvolveAlternativeArrivalBounds( true );
		base_config.setUseArrivalBoundsCache( true );
		
		base_config.enforceMaxSC( MaxScEnforcement.GLOBALLY_OFF );
		base_config.enforceMaxScOutputRate( MaxScEnforcement.GLOBALLY_OFF );
		
		base_config.enforceMultiplexing( MultiplexingEnforcement.GLOBAL_ARBITRARY );

		
		// --------------------------------------------------------------------------------------------------------------
		// Analysis + Arrival Bounding Instantiation
		// --------------------------------------------------------------------------------------------------------------
		
		// TFA + aggrPBOOAB
		AnalysisConfig tfa_config = base_config.copy();
		tfa_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		tf_analysis = new TotalFlowAnalysis( server_graph, tfa_config );
		
		// SFA + aggrPBOOAB
		AnalysisConfig sfa_config = base_config.copy();
		sfa_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		sf_analysis = new SeparateFlowAnalysis( server_graph, sfa_config );

		// PMOO + aggrPMOOAB
		AnalysisConfig pmoo_config = base_config.copy();
		pmoo_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PMOO );
		pmoo_analysis = new PmooAnalysis( server_graph, pmoo_config );

		
		// TFA + segrPBOOAB
		AnalysisConfig tfa_segr_config = base_config.copy();
		tfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PBOO );
		tfa_segrPBOOAB = new TotalFlowAnalysis( server_graph, tfa_segr_config );
		
		// SFA + segrPBOOAB
		AnalysisConfig sfa_segr_config = base_config.copy();
		sfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PBOO );
		sfa_segrPBOOAB = new SeparateFlowAnalysis( server_graph, sfa_segr_config );

		// PMOO + segrPMOOAB
		AnalysisConfig pmoo_segr_config = base_config.copy();
		pmoo_segr_config .setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PMOO );
		pmoo_segrPMOOAB = new PmooAnalysis( server_graph, pmoo_segr_config );

		
		// SFA + aggrAB
		AnalysisConfig sfa_aggrAB_config = base_config.copy();
		sfa_aggrAB_config.setArrivalBoundMethods( aggrAB );
		sfa_aggrAB = new SeparateFlowAnalysis( server_graph, sfa_aggrAB_config );

		// PMOO + aggrAB
		AnalysisConfig pmoo_aggrAB_config = base_config.copy();
		pmoo_aggrAB_config.setArrivalBoundMethods( aggrAB );
		pmoo_aggrAB = new PmooAnalysis( server_graph, pmoo_aggrAB_config );

		
		// TMA
		AnalysisConfig tma_config = base_config.copy();
		tma_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_TM );
		tandem_matching_analysis = new TandemMatchingAnalysis( server_graph, tma_config );
		
		// TMA + AB cap
		AnalysisConfig tma_bcap_config = base_config.copy();
		tma_bcap_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_TM );
		tma_bcap_config.setServerBacklogArrivalBound(true);
		tandem_matching_bcap_analysis = new TandemMatchingAnalysis( server_graph, tma_bcap_config );

		
		// SFA + MMB18AB
		AnalysisConfig sfa_MMB_config = base_config.copy();
		sfa_MMB_config.setArrivalBoundMethods( MMB18AB );
		sfa_MMB18AB = new SeparateFlowAnalysis( server_graph, sfa_MMB_config );

		// PMOO + MMB18AB
		AnalysisConfig pmoo_MMB_config = base_config.copy();
		pmoo_MMB_config.setArrivalBoundMethods( MMB18AB );
		pmoo_MMB18AB = new PmooAnalysis( server_graph, pmoo_MMB_config );
	}
	
	public ServerGraph getServerGraph() {
		return server_graph;
	}
}
