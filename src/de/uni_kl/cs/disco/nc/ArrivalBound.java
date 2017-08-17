package de.uni_kl.cs.disco.nc;

import java.util.Set;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;

public interface ArrivalBound {
	public Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception;
}
