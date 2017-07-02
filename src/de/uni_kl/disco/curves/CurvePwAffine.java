package de.uni_kl.disco.curves;

import de.uni_kl.disco.numbers.Num;

/**
 * Interface for wide-sense increasing, ultimately affine curves.
 */
public interface CurvePwAffine extends Curve {
    @Override
    CurvePwAffine copy();

    @Override
    void copy(Curve curve);

    // Curve properties
    boolean isConvex();

    boolean isConcave();

    boolean isAlmostConcave();

    // Curve function values
    Num getUltAffineRate();


    // Specific piecewise affine curve shapes
    // Burst delay
    boolean isDelayedInfiniteBurst();

    // (Multi-)Rate latency
    boolean isRateLatency();

    int getRLComponentCount();

    CurvePwAffine getRLComponent(int i);

    // (Multi-)Token bucket
    boolean isTokenBucket();

    int getTBComponentCount();

    CurvePwAffine getTBComponent(int i);
}
