/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.0 "Centaur".
 *
 * Copyright (C) 2015 - 2017 Steffen Bondorf
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

package unikl.disco.tests.output;

import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.nc.PmooAnalysis;
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.TotalFlowAnalysis;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.nc.AnalysisConfig.GammaFlag;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;

public class UseSavedNetwork {
	public static void main( String[] args ) throws Exception {
		// Get the server graph
		@SuppressWarnings("static-access")
		Network server_graph = ( new SavedNetwork() ).network;
		
		
		// Analysis configurations
		CalculatorConfig.disableAllChecks();
		
		AnalysisConfig pboo_concat_config = new AnalysisConfig();
		
		pboo_concat_config.setUseTbrlConvolution( true );
		pboo_concat_config.setUseTbrlDeconvolution( true );
		
		pboo_concat_config.setUseGamma( GammaFlag.GLOBALLY_OFF );
		pboo_concat_config.setUseExtraGamma( GammaFlag.GLOBALLY_OFF );
		
		pboo_concat_config.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );

		// TFA + aggrPBOO-AB
		pboo_concat_config.setArrivalBoundMethod( ArrivalBoundMethod.PBOO_CONCATENATION );
		TotalFlowAnalysis tfa_analysis = new TotalFlowAnalysis( server_graph, pboo_concat_config );

		// SFA + aggrPBOO-AB
		pboo_concat_config.setArrivalBoundMethod( ArrivalBoundMethod.PBOO_CONCATENATION );
		SeparateFlowAnalysis sfa_analysis = new SeparateFlowAnalysis( server_graph, pboo_concat_config );

		// PMOO + aggrPMOO-AB
		AnalysisConfig pmoo_concat_config = pboo_concat_config.copy();
		pmoo_concat_config.setArrivalBoundMethod( ArrivalBoundMethod.PMOO );
		PmooAnalysis pmoo_analysis = new PmooAnalysis( server_graph, pmoo_concat_config );
		
		
		// Run the analyses
		System.out.println( "Fid, TFA_D, SFA_D, PMOO_D" );
		
		for( Flow flow_of_interest : server_graph.getFlows() ) {
			System.out.print( flow_of_interest.getAlias().substring( 1 ) + ", " );
			
			// TFA
			tfa_analysis.performAnalysis( flow_of_interest );
			
			System.out.print( tfa_analysis.getDelayBound().toString() );
			System.out.print( ", " );
			
			// SFA
			sfa_analysis.performAnalysis( flow_of_interest );
			
			System.out.print( sfa_analysis.getDelayBound().toString() );
			System.out.print( ", " );
			
			// PMOO
			pmoo_analysis.performAnalysis( flow_of_interest );
			
			System.out.print( pmoo_analysis.getDelayBound().toString() );
			System.out.println();
		}
	}
}