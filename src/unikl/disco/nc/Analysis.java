/*
 * This file is part of the Disco Deterministic Network Calculator v2.2.6 "Hydra".
 *
 * Copyright (C) 2008 - 2010 Andreas Kiefer
 * Copyright (C) 2011 - 2016 Steffen Bondorf
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

package unikl.disco.nc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.curves.ServiceCurve;
import unikl.disco.network.Flow;
import unikl.disco.network.Network;
import unikl.disco.network.Path;
import unikl.disco.network.Server;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

/**
 * This class contains all members and methods that are needed for more than one
 * analysis.
 * 
 * @author Andreas Kiefer
 * @author Steffen Bondorf
 * 
 */
public abstract class Analysis
{
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

//--------------------------------------------------------------------------------------------------
// Analysis Result Classes
//--------------------------------------------------------------------------------------------------
abstract class AnalysisResults {
	public boolean succeeded;
	
	public Num delay_bound;
	public Num backlog_bound;

	public Map<Server,Set<ArrivalCurve>> map__server__alphas;
	
	protected AnalysisResults() {
		this.succeeded = false;
		this.delay_bound = NumFactory.createNaN();
		this.backlog_bound = NumFactory.createNaN();
		this.map__server__alphas = new HashMap<Server,Set<ArrivalCurve>>();
	}
	
	protected AnalysisResults( boolean succeeded,
							  Num delay_bound,
							  Num backlog_bound,
							  Map<Server,Set<ArrivalCurve>> map__server__alphas ) {
		
		this.succeeded = succeeded;
		this.delay_bound = delay_bound;
		this.backlog_bound = backlog_bound;
		this.map__server__alphas = map__server__alphas;
	}
	
	public String getServerAlphasMapString() {
		if( !succeeded ) {
			return "Analysis failed";
		}
		
		if( map__server__alphas.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );
		for( Entry<Server, Set<ArrivalCurve>> entry : map__server__alphas.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( ArrivalCurve ac : entry.getValue() ) {
				result_str.append( ac.toString() );
				result_str.append( "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		result_str.append( "}" );
		
		return result_str.toString();
	}
}

class TotalFlowAnalysisResults extends AnalysisResults {
	protected Map<Server,Set<Num>> map__server__D_server;
	protected Map<Server,Set<Num>> map__server__B_server;
	
	protected TotalFlowAnalysisResults(){
		super();
		map__server__D_server = new HashMap<Server,Set<Num>>();
		map__server__B_server = new HashMap<Server,Set<Num>>();
	}
	
	protected TotalFlowAnalysisResults( Num delay_bound,
					 	 Map<Server,Set<Num>> map__server__D_server,
					 	 Num backlog_bound,
					 	 Map<Server,Set<Num>> map__server__B_server,
					 	 Map<Server,Set<ArrivalCurve>> map__server__alphas ) {
		
		super( true, delay_bound, backlog_bound, map__server__alphas );
		
		this.map__server__D_server = map__server__D_server;
		this.map__server__B_server = map__server__B_server;
	}
	
	public String getServerDelayBoundMapString() {
		if( !succeeded ) {
			return "Analysis failed";
		}
		
		if( map__server__D_server.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );
		for( Entry<Server,Set<Num>> entry : map__server__D_server.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( Num delay_bound : entry.getValue() ) {
				result_str.append( delay_bound.toString() );
				result_str.append( "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		result_str.append( "}" );
		
		return result_str.toString();
	}
	
	public String getServerBacklogBoundMapString() {
		if( map__server__B_server.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );
		for( Entry<Server,Set<Num>> entry : map__server__B_server.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( Num delay_bound : entry.getValue() ) {
				result_str.append( delay_bound.toString() );
				result_str.append( "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		result_str.append( "}" );
		
		return result_str.toString();
	}
}

class SeparateFlowAnalysisResults extends AnalysisResults {
	protected Set<ServiceCurve> betas_e2e;
	protected Map<Server,Set<ServiceCurve>> map__server__betas_lo;

	protected SeparateFlowAnalysisResults(){
		super();
		betas_e2e = new HashSet<ServiceCurve>();
		map__server__betas_lo = new HashMap<Server,Set<ServiceCurve>>();
	}
	
	protected SeparateFlowAnalysisResults( Num delay_bound,
						 Num backlog_bound,
						 Set<ServiceCurve> betas_e2e,
						 Map<Server,Set<ServiceCurve>> map__server__betas_lo,
						 Map<Server,Set<ArrivalCurve>> map__server__alphas ) {
		
		super( true, delay_bound, backlog_bound, map__server__alphas );
		
		this.betas_e2e = betas_e2e;
		this.map__server__betas_lo = map__server__betas_lo;
	}

	public String getServerLeftOverBetasMapString() {
		if( !succeeded ) {
			return "Analysis failed";
		}
		
		if( map__server__betas_lo.isEmpty() ) {
			return "{}";
		}

		StringBuffer result_str = new StringBuffer( "{" );

		for( Entry<Server,Set<ServiceCurve>> entry : map__server__betas_lo.entrySet() ) {
			result_str.append( entry.getKey().toShortString() );
			result_str.append( "={" );
			for ( ServiceCurve beta_lo : entry.getValue() ) {
				result_str.append( beta_lo.toString() );
				result_str.append(  "," );
			}
			result_str.deleteCharAt( result_str.length()-1 ); // Remove the trailing comma.
			result_str.append( "}" );
			result_str.append( ", " );
		}
		result_str.delete( result_str.length()-2, result_str.length() ); // Remove the trailing blank space and comma.
		
		result_str.append( "}" );
		
		return result_str.toString();
	}
}

class PmooAnalysisResults extends AnalysisResults {
	protected Set<ServiceCurve> betas_e2e;

	protected PmooAnalysisResults(){}
			
	protected PmooAnalysisResults( Num delay_bound,
						  Num backlog_bound,
						  Set<ServiceCurve> betas_e2e,
						  Map<Server,Set<ArrivalCurve>> map__server__alphas ) {
		
		super( true, delay_bound, backlog_bound, map__server__alphas );
		
		this.betas_e2e = betas_e2e;
	}
}