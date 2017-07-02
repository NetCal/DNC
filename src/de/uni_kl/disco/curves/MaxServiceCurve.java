package de.uni_kl.disco.curves;

/**
 * The interface extends ServiceCurve as MaxServiceCurve is a ServiceCurve
 * but it is used to shape the input and thus also acts like an arrival restriction.
 */

public interface MaxServiceCurve extends ServiceCurve {
    @Override
    MaxServiceCurve copy();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
