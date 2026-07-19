package org.networkcalculus.dnc.network.device_graph;


import org.networkcalculus.dnc.backend.Curve;

public class Link {
    private int id;
    private String alias;

    private Device device1;
    private Device device2;

    private Curve serviceCurve;

    protected Link(int id, String alias, Device device1, Device device2, Curve serviceCurve) {
        this.id = id;
        this.alias = alias;
        this.device1 = device1;
        this.device2 = device2;
        this.serviceCurve = serviceCurve;
    }

    public int getId() {
        return id;
    }

    public Device getDevice1() {
        return device1;
    }

    public Device getDevice2() {
        return device2;
    }

    public Curve getServiceCurve() {
        return serviceCurve;
    }

}
