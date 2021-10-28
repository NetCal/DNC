/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.bounds.disco.con_pw_affine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.algebra.MinPlus;
import org.networkcalculus.dnc.algebra.disco.con_pw_affine.Deconvolution_Disco_ConPwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;

/**
 * The output bound makes use of the deconvolution. Therefore, it inherits the restrictions 
 * of the min-plus algebra implementation configured in Calculator.  
 */
public final class Output_Disco_ConPwAffine {
    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, 
    										Set<ArrivalCurve> arrival_curves, Server server) throws Exception {
        return compute(configuration, arrival_curves, server, Collections.singleton(server.getServiceCurve()));
    }

    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, Set<ArrivalCurve> arrival_curves,
                                            Server server, Set<ServiceCurve> betas_lo) throws Exception {
		if (configuration.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
		{
			throw new Exception("Not yet supported.");
		}

        Set<ArrivalCurve> output_bound = new HashSet<ArrivalCurve>();

        MinPlus minplus_alg = Calculator.getInstance().getMinPlus();
        
        switch(configuration.enforceMaxSC()) {
	    	case GLOBALLY_ON:
	    		output_bound = minplus_alg.deconvolve_almostConcCs_SCs(
	    				minplus_alg.convolve_ACs_MaxSC(arrival_curves, server.getStoredMaxSC()), betas_lo);
	    		break;
	    		
	    	case GLOBALLY_OFF:	
	    		output_bound = minplus_alg.deconvolve(arrival_curves, betas_lo);
	    		break;
	    		
			case SERVER_LOCAL:
			default:
				output_bound = minplus_alg.deconvolve_almostConcCs_SCs(
						minplus_alg.convolve_ACs_MaxSC(arrival_curves, server.getMaxServiceCurve()), betas_lo);
				break;
	    }
        
        switch(configuration.enforceMaxScOutputRate()) {
	    	case GLOBALLY_ON:
	    		output_bound = minplus_alg.convolve_ACs_MaxScRate(output_bound, server.getStoredMaxScRate());
	    		break;
	    		
	    	case GLOBALLY_OFF:	
	    		// Do nothing
	    		break;
	    		
			case SERVER_LOCAL:
			default:
				// Server's flag will be checked in server.getMaxScRate()
				output_bound = minplus_alg.convolve_ACs_MaxScRate(output_bound, server.getMaxScRate());
				break;
	    }

        return output_bound;
    }

    public static Set<ArrivalCurve> compute(AnalysisConfig configuration, 
    										Set<ArrivalCurve> arrival_curves, Path path, Set<ServiceCurve> betas_lo) throws Exception {
		if (configuration.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO)
		{
			Set<ArrivalCurve> output_bound = new HashSet<ArrivalCurve>();

			// In our context just one arrival curve in the set (and one left over)
			ArrivalCurve ac = null;
			for(ArrivalCurve curve : arrival_curves)
			{
				ac = curve;
			}

			ServiceCurve sc = null;
			for(ServiceCurve curve : betas_lo)
			{
				sc = curve;
			}

			ArrivalCurve output_ac = Deconvolution_Disco_ConPwAffine.deconvolve_simple_fifo(ac, sc);

			output_bound.add(output_ac);

			return output_bound;
		}

		else
		{

    	Set<ArrivalCurve> output_bound = new HashSet<ArrivalCurve>();

    	MinPlus minplus_alg = Calculator.getInstance().getMinPlus();
        
        switch(configuration.enforceMaxSC()) {
	    	case GLOBALLY_ON:
	    		output_bound = minplus_alg.deconvolve_almostConcCs_SCs(
	    				minplus_alg.convolve_ACs_MaxSC(arrival_curves, path.getStoredMaxSC()), betas_lo);
	    		break;
	    		
	    	case GLOBALLY_OFF:	
	    		output_bound = minplus_alg.deconvolve(arrival_curves, betas_lo);
	    		break;
	    		
			case SERVER_LOCAL:
			default:
				output_bound = minplus_alg.deconvolve_almostConcCs_SCs(
						minplus_alg.convolve_ACs_MaxSC(arrival_curves, path.getMaxServiceCurve()), betas_lo);
				break;
	    }
        
        switch(configuration.enforceMaxScOutputRate()) {
	    	case GLOBALLY_ON:
	    		output_bound = minplus_alg.convolve_ACs_MaxScRate(output_bound, path.getStoredMaxScRate());
	    		break;
	    		
	    	case GLOBALLY_OFF:	
	    		// Do nothing
	    		break;
	    		
			case SERVER_LOCAL:
			default:
				// Server's flag will be checked in path.getMaxScRate()
				output_bound = minplus_alg.convolve_ACs_MaxScRate(output_bound, path.getMaxScRate());
				break;
	    }

      	  return output_bound;
		}
    }

	public static ArrivalCurve computeFIFOOutputBound( ArrivalCurve arrival_curve,  ServiceCurve beta_lo ) throws Exception {

		ArrivalCurve output_ac = Deconvolution_Disco_ConPwAffine.deconvolve_simple_fifo(arrival_curve,  beta_lo);

		return output_ac;
	}
}
