package de.uni_kl.cs.disco.nc;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;

import java.util.Set;

public interface ArrivalBound {
    Network getNetwork();

    // --------------------------------------------------------------------------------------------------------------
    // Interface
    // --------------------------------------------------------------------------------------------------------------
    void setNetwork(Network network);

    public AnalysisConfig getConfiguration();

    public void setConfiguration(AnalysisConfig configuration);

    Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception;
}
