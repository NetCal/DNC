package de.uni_kl.cs.discodnc;

import java.util.HashSet;
import java.util.Set;

import de.uni_kl.cs.discodnc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.AnalysisConfig.MaxScEnforcement;
import de.uni_kl.cs.discodnc.AnalysisConfig.MultiplexingEnforcement;
import de.uni_kl.cs.discodnc.network.server_graph.ServerGraph;
import de.uni_kl.cs.discodnc.tandem.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.tandem.analyses.TotalFlowAnalysis;

public class CompFFApresets {
	private ServerGraph network;
	
	public TotalFlowAnalysis tf_analysis;		// TFA + aggrPBOOAB
	public SeparateFlowAnalysis sf_analysis;	// SFA + aggrPBOOAB
	public PmooAnalysis pmoo_analysis;			// PMOO + aggrPMOOAB
	
	public TotalFlowAnalysis tfa_segrPBOOAB;	// TFA + segrPBOOAB
	public SeparateFlowAnalysis sfa_segrPBOOAB;	// SFA + segrPBOOAB
	public PmooAnalysis pmoo_segrPMOOAB;		// PMOO + segrPMOOAB
	
	public SeparateFlowAnalysis sfa_aggrAB;		// SFA + aggrAB
	public PmooAnalysis pmoo_aggrAB;			// PMOO + aggrAB

	public SeparateFlowAnalysis sfa_MMB18AB;	// SFA + aggrAB + segrPMOOAB
	public PmooAnalysis pmoo_MMB18AB;			// PMOO + aggrAB + segrPMOOAB
	
	public CompFFApresets( ServerGraph network ) {
		this.network = network;

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
		
		Calculator.getInstance().disableAllChecks();
		
		AnalysisConfig base_config = new AnalysisConfig();
		base_config.setRemoveDuplicateArrivalBounds( true );
		
		base_config.enforceMaxSC( MaxScEnforcement.GLOBALLY_OFF );
		base_config.enforceMaxScOutputRate( MaxScEnforcement.GLOBALLY_OFF );
		
		base_config.enforceMultiplexing( MultiplexingEnforcement.GLOBAL_ARBITRARY );

		
		// --------------------------------------------------------------------------------------------------------------
		// Analysis + Arrival Bounding Instantiation
		// --------------------------------------------------------------------------------------------------------------
		
		// TFA + aggrPBOOAB
		AnalysisConfig tfa_config = base_config.copy();
		tfa_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		tf_analysis = new TotalFlowAnalysis( network, tfa_config );
		
		// SFA + aggrPBOOAB
		AnalysisConfig sfa_config = base_config.copy();
		sfa_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PBOO_CONCATENATION );
		sf_analysis = new SeparateFlowAnalysis( network, sfa_config );

		// PMOO + aggrPMOOAB
		AnalysisConfig pmoo_config = base_config.copy();
		pmoo_config.setArrivalBoundMethod( ArrivalBoundMethod.AGGR_PMOO );
		pmoo_analysis = new PmooAnalysis( network, pmoo_config );

		
		// TFA + segrPBOOAB
		AnalysisConfig tfa_segr_config = base_config.copy();
		tfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PBOO );
		tfa_segrPBOOAB = new TotalFlowAnalysis( network, tfa_segr_config );
		
		// SFA + segrPBOOAB
		AnalysisConfig sfa_segr_config = base_config.copy();
		sfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PBOO );
		sfa_segrPBOOAB = new SeparateFlowAnalysis( network, sfa_segr_config );

		// PMOO + segrPMOOAB
		AnalysisConfig pmoo_segr_config = base_config.copy();
		pmoo_segr_config .setArrivalBoundMethod( ArrivalBoundMethod.SEGR_PMOO );
		pmoo_segrPMOOAB = new PmooAnalysis( network, pmoo_segr_config );

		
		// SFA + aggrAB
		AnalysisConfig sfa_aggrAB_config = base_config.copy();
		sfa_aggrAB_config.setArrivalBoundMethods( aggrAB );
		sfa_aggrAB = new SeparateFlowAnalysis( network, sfa_aggrAB_config );

		// PMOO + aggrAB
		AnalysisConfig pmoo_aggrAB_config = base_config.copy();
		pmoo_aggrAB_config.setArrivalBoundMethods( aggrAB );
		pmoo_aggrAB = new PmooAnalysis( network, pmoo_aggrAB_config );

		
		// SFA + MMB18AB
		AnalysisConfig sfa_MMB_config = base_config.copy();
		sfa_MMB_config.setArrivalBoundMethods( MMB18AB );
		sfa_MMB18AB = new SeparateFlowAnalysis( network, sfa_MMB_config );

		// PMOO + MMB18AB
		AnalysisConfig pmoo_MMB_config = base_config.copy();
		pmoo_MMB_config.setArrivalBoundMethods( MMB18AB );
		pmoo_MMB18AB = new PmooAnalysis( network, pmoo_MMB_config );
	}
	
	public ServerGraph getNetwork() {
		return network;
	}
}
