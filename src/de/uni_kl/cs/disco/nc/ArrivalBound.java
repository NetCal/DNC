package de.uni_kl.cs.disco.nc;

import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;

public interface ArrivalBound {
	// --------------------------------------------------------------------------------------------------------------
	// Interface
	// --------------------------------------------------------------------------------------------------------------
	void setNetwork( Network network );

	Network getNetwork();

	public void setConfiguration( AnalysisConfig configuration );

	public AnalysisConfig getConfiguration();
	
	Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception;
}
