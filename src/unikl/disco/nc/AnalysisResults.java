package unikl.disco.nc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import unikl.disco.curves.ArrivalCurve;
import unikl.disco.network.Server;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

public class AnalysisResults {
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
