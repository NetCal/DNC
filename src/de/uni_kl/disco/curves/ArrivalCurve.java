package de.uni_kl.disco.curves;

public interface ArrivalCurve extends CurvePwAffine {
    @Override
    ArrivalCurve copy();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
