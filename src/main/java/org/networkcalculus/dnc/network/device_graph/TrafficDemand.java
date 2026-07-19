package org.networkcalculus.dnc.network.device_graph;

import org.networkcalculus.dnc.backend.Curve;

public class TrafficDemand {
    private int id;
    private String alias;

    private Device src;
    private Device dest;

    private Curve arrivalCurve;

    protected TrafficDemand(int id, String alias, Device src, Device dest, Curve arrivalCurve) {
        this.id = id;
        this.alias = alias;
        this.src = src;
        this.dest = dest;
        this.arrivalCurve = arrivalCurve;
    }

    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public Device getSrc() {
        return src;
    }

    public Device getDest() {
        return dest;
    }

    public Curve getArrivalCurve() {
        return arrivalCurve;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
