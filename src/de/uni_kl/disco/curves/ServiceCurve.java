package de.uni_kl.disco.curves;

public interface ServiceCurve extends CurveUltAffine {
    @Override
    ServiceCurve copy();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
