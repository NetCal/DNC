package unikl.disco.tests;

import java.util.HashMap;
import java.util.Map;

import unikl.disco.misc.Pair;
import unikl.disco.nc.Analysis.Analyses;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.AnalysisResults;
import unikl.disco.network.Flow;
import unikl.disco.numbers.Num;

public class FunctionalTestResults {
	private Map<Flow,AnalysisResults> tfa_bounds_arb;
	private Map<Flow,AnalysisResults> tfa_bounds_fifo;
	private Map<Flow,AnalysisResults> sfa_bounds_arb;
	private Map<Flow,AnalysisResults> sfa_bounds_fifo;
	private Map<Flow,AnalysisResults> pmoo_bounds_arb;
	
	public FunctionalTestResults(){
		tfa_bounds_arb = new HashMap<Flow,AnalysisResults>();
		tfa_bounds_fifo = new HashMap<Flow,AnalysisResults>();
		sfa_bounds_arb = new HashMap<Flow,AnalysisResults>();
		sfa_bounds_fifo = new HashMap<Flow,AnalysisResults>();
		pmoo_bounds_arb = new HashMap<Flow,AnalysisResults>();
	}
	
	protected void clear() {
		tfa_bounds_arb.clear();
		tfa_bounds_fifo.clear();
		sfa_bounds_arb.clear();
		sfa_bounds_fifo.clear();
		pmoo_bounds_arb.clear();
	}
	
	protected void setBounds( Analyses analysis, AnalysisConfig.Multiplexing mux, Flow flow, Num delay, Num backlog ) {
		AnalysisResults bounds = new AnalysisResults( delay, backlog, null );
		
		Pair<Map<Flow,AnalysisResults>> bounded_analysis;
		switch( analysis ){
		case TFA:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( tfa_bounds_arb, tfa_bounds_fifo );
			break;
		case SFA:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( sfa_bounds_arb, sfa_bounds_fifo );
			break;
		case PMOO:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( pmoo_bounds_arb, null );
			break;
		default:
			throw new RuntimeException( "Invalid analysis given." );
		}
		
		if( mux == AnalysisConfig.Multiplexing.ARBITRARY ) {
			bounded_analysis.getFirst().put( flow, bounds );
		} else {
			bounded_analysis.getSecond().put( flow, bounds );
		}
	}
	
	public AnalysisResults getBounds( Analyses analysis, AnalysisConfig.Multiplexing mux, Flow flow ) {
		Pair<Map<Flow,AnalysisResults>> bounded_analysis;
		switch( analysis ){
		case TFA:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( tfa_bounds_arb, tfa_bounds_fifo );
			break;
		case SFA:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( sfa_bounds_arb, sfa_bounds_fifo );
			break;
		case PMOO:
			bounded_analysis = new Pair<Map<Flow,AnalysisResults>>( pmoo_bounds_arb, null );
			break;
		default:
			throw new RuntimeException( "Invalid analysis given." );
		}
		
		if( mux == AnalysisConfig.Multiplexing.ARBITRARY ) {
			return bounded_analysis.getFirst().get( flow );
		} else {
			return bounded_analysis.getSecond().get( flow );
		}
	}
}
