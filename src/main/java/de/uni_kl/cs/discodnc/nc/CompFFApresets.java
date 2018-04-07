package de.uni_kl.cs.discodnc.nc;

import java.util.HashSet;
import java.util.Set;

import de.uni_kl.cs.discodnc.Calculator;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.ArrivalBoundMethod;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.GammaFlag;
import de.uni_kl.cs.discodnc.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.discodnc.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.TotalFlowAnalysis;
import de.uni_kl.cs.discodnc.nc.analyses.SeparateFlowAnalysis;
import de.uni_kl.cs.discodnc.network.Network;

public class CompFFApresets {
	private Network network;
	
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
	
	public CompFFApresets( Network network ) {
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
		aggrAB.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		aggrAB.add( ArrivalBoundMethod.PMOO );
		
		// aggrAB + aggrPMOOAB can outperform aggrAB. See:	
		//   Catching Corner Cases in Network Calculus – Flow Segregation Can Improve Accuracy 
		//   (Steffen Bondorf, Paul Nikolaus, Jens B. Schmitt),
		//   In Proc. of the Int. GI/ITG Conference on Measurement, Modelling and Evaluation of Computing Systems (MMB), 2018.
		Set<ArrivalBoundMethod> MMB18AB = new HashSet<ArrivalBoundMethod>();
		MMB18AB.add( ArrivalBoundMethod.PBOO_CONCATENATION );
		MMB18AB.add( ArrivalBoundMethod.PMOO );
		MMB18AB.add( ArrivalBoundMethod.PER_FLOW_PMOO );
		

		// --------------------------------------------------------------------------------------------------------------
		// Calculator Configurations
		// --------------------------------------------------------------------------------------------------------------
		
		Calculator.getInstance().disableAllChecks();
		
		AnalysisConfig base_config = new AnalysisConfig();
		base_config.setConvolveAlternativeArrivalBounds( true );
		
		base_config.setUseTbrlConvolution( true );
		base_config.setUseTbrlDeconvolution( true );
		
		base_config.setUseGamma( GammaFlag.GLOBALLY_OFF );
		base_config.setUseExtraGamma( GammaFlag.GLOBALLY_OFF );
		
		base_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );

		
		// --------------------------------------------------------------------------------------------------------------
		// Analysis + Arrival Bounding Instantiation
		// --------------------------------------------------------------------------------------------------------------
		
		// TFA + aggrPBOOAB
		AnalysisConfig tfa_config = base_config.copy();
		tfa_config.setArrivalBoundMethod( ArrivalBoundMethod.PBOO_CONCATENATION );
		tf_analysis = new TotalFlowAnalysis( network, tfa_config );
		
		// SFA + aggrPBOOAB
		AnalysisConfig sfa_config = base_config.copy();
		sfa_config.setArrivalBoundMethod( ArrivalBoundMethod.PBOO_CONCATENATION );
		sf_analysis = new SeparateFlowAnalysis( network, sfa_config );

		// PMOO + aggrPMOOAB
		AnalysisConfig pmoo_config = base_config.copy();
		pmoo_config.setArrivalBoundMethod( ArrivalBoundMethod.PMOO );
		pmoo_analysis = new PmooAnalysis( network, pmoo_config );

		
		// TFA + segrPBOOAB
		AnalysisConfig tfa_segr_config = base_config.copy();
		tfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.PER_FLOW_SFA );
		tfa_segrPBOOAB = new TotalFlowAnalysis( network, tfa_segr_config );
		
		// SFA + segrPBOOAB
		AnalysisConfig sfa_segr_config = base_config.copy();
		sfa_segr_config.setArrivalBoundMethod( ArrivalBoundMethod.PER_FLOW_SFA );
		sfa_segrPBOOAB = new SeparateFlowAnalysis( network, sfa_segr_config );

		// PMOO + segrPMOOAB
		AnalysisConfig pmoo_segr_config = base_config.copy();
		pmoo_segr_config .setArrivalBoundMethod( ArrivalBoundMethod.PER_FLOW_PMOO );
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
	
	public Network getNetwork() {
		return network;
	}
}
