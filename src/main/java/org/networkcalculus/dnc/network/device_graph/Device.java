package org.networkcalculus.dnc.network.device_graph;

public class Device {
    private int id;
    private String alias;

    protected Device(int id, String alias) {
        this.id = id;
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }
}
