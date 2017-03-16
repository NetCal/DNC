package unikl.disco.tests;

import java.util.HashMap;
import java.util.Map;

import unikl.disco.misc.Pair;
import unikl.disco.nc.Analysis.Analyses;
import unikl.disco.nc.AnalysisConfig;
import unikl.disco.nc.AnalysisConfig.Multiplexing;
import unikl.disco.network.Flow;
import unikl.disco.numbers.Num;

//TODO Make files for all the expected results and a nice constructor
public class TestResults {
		// flow of interest --> <Delay Bound, Backlog Bound>
	private Map<Flow,Pair<Num>> tfa_bounds_arb;
	private Map<Flow,Pair<Num>> tfa_bounds_fifo;
	private Map<Flow,Pair<Num>> sfa_bounds_arb;
	private Map<Flow,Pair<Num>> sfa_bounds_fifo;
	private Map<Flow,Pair<Num>> pmoo_bounds_arb;
	
	public TestResults(){
		tfa_bounds_arb = new HashMap<Flow,Pair<Num>>();
		tfa_bounds_fifo = new HashMap<Flow,Pair<Num>>();
		sfa_bounds_arb = new HashMap<Flow,Pair<Num>>();
		sfa_bounds_fifo = new HashMap<Flow,Pair<Num>>();
		pmoo_bounds_arb = new HashMap<Flow,Pair<Num>>();
	}
	
	protected void clear() {
		tfa_bounds_arb.clear();
		tfa_bounds_fifo.clear();
		sfa_bounds_arb.clear();
		sfa_bounds_fifo.clear();
		pmoo_bounds_arb.clear();
	}
	
	protected void addBounds( Analyses analysis, AnalysisConfig.Multiplexing mux, Flow flow, Num delay_bound, Num backlog_bound ) {
		Pair<Map<Flow,Pair<Num>>> bounded_analysis;
		switch( analysis ){
		case TFA:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( tfa_bounds_arb, tfa_bounds_fifo );
			break;
		case SFA:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( sfa_bounds_arb, sfa_bounds_fifo );
			break;
		case PMOO:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( pmoo_bounds_arb, null );
			break;
		default:
			throw new RuntimeException( "Invalid analysis given." );
		}
		
		Pair<Num> bounds = new Pair<Num>( delay_bound.copy(), backlog_bound.copy() );
		if( mux == AnalysisConfig.Multiplexing.ARBITRARY ) {
			bounded_analysis.getFirst().put( flow, bounds );
		} else {
			bounded_analysis.getSecond().put( flow, bounds );
		}
	}
	
	public Pair<Num> getBounds( Analyses analysis, AnalysisConfig.Multiplexing mux, Flow flow ) {
		Pair<Map<Flow,Pair<Num>>> bounded_analysis;
		switch( analysis ){
		case TFA:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( tfa_bounds_arb, tfa_bounds_fifo );
			break;
		case SFA:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( sfa_bounds_arb, sfa_bounds_fifo );
			break;
		case PMOO:
			bounded_analysis = new Pair<Map<Flow,Pair<Num>>>( pmoo_bounds_arb, null );
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
