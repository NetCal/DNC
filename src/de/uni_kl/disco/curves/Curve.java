package de.uni_kl.disco.curves;

import de.uni_kl.disco.numbers.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public interface Curve {
    Curve copy();

    void copy(Curve curve);

    // Curve's segments (incl. manipulation)
    LinearSegment getSegment(int pos);

    int getSegmentCount();

    int getSegmentDefining(Num x);

    void addSegment(LinearSegment s);

    void addSegment(int pos, LinearSegment s);

    void removeSegment(int pos);


    // Curve properties
    boolean isDiscontinuity(int pos);

    boolean isRealDiscontinuity(int pos);

    boolean isUnrealDiscontinuity(int pos);

    boolean isWideSenseIncreasing();

    boolean isConcaveIn(Num a, Num b);

    boolean isConvexIn(Num a, Num b);

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();


    // Curve function values
    Num f(Num x);

    Num fLimitRight(Num x);

    Num f_inv(Num y);

    Num f_inv(Num y, boolean rightmost);

    Num getLatency();

    Num getBurst();

    Num getGradientLimitRight(Num x);
}
