package de.uni_kl.cs.disco.nc;

import de.uni_kl.cs.disco.network.Network;

public abstract class AbstractArrivalBound implements ArrivalBound {
    protected Network network;
    protected AnalysisConfig configuration;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public AnalysisConfig getConfiguration() {
        return configuration;
    }

    public void setConfiguration(AnalysisConfig configuration) {
        this.configuration = configuration;
    }
}
