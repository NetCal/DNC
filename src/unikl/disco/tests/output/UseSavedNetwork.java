/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2015, 2016 Steffen Bondorf
 *
 * disco | Distributed Computer Systems Lab
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
import unikl.disco.nc.SeparateFlowAnalysis;
import unikl.disco.nc.AnalysisConfig.ArrivalBoundMethod;
import unikl.disco.nc.AnalysisConfig.GammaFlag;
import unikl.disco.nc.AnalysisConfig.MuxDiscipline;
import unikl.disco.network.Flow;

public class UseSavedNetwork {
	public static void main( String[] args )
	{
		new SavedNetwork(); // Initialize the static network object in there. 
		
		AnalysisConfig configuration = new AnalysisConfig();

		CalculatorConfig.disableAllChecks();
		configuration.setRemoveDuplicateArrivalBounds( true );
		configuration.setUseTbrlConvolution( true );
		configuration.setUseTbrlDeconvolution( true );
		configuration.setUseGamma( GammaFlag.GLOBALLY_OFF );
		configuration.setUseGamma( GammaFlag.GLOBALLY_OFF );
		configuration.setUseExtraGamma( GammaFlag.GLOBALLY_OFF );
		configuration.setMultiplexingDiscipline( MuxDiscipline.GLOBAL_ARBITRARY );
		configuration.setArrivalBoundMethod( ArrivalBoundMethod.PBOO_CONCATENATION );

		SeparateFlowAnalysis sfa = new SeparateFlowAnalysis( SavedNetwork.network, configuration );
		for( Flow f : SavedNetwork.network.getFlows() ) {
			try {
				sfa.performAnalysis( f );
				System.out.println( f.toShortString() + "'s delay bound: " + sfa.getDelayBound().toString() );
			} catch (Exception e) {
				System.out.println( e.toString() );
			}
		}
	}
}
