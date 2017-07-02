package de.uni_kl.disco.curves;

import de.uni_kl.disco.numbers.Num;

/**
 * Interface for wide-sense increasing, ultimately affine curves.
 */
public interface CurveMultAffine extends Curve {
    @Override
    CurveMultAffine copy();

    @Override
    void copy(Curve curve);

    // Curve properties
    boolean isConvex();

    boolean isConcave();

    boolean isAlmostConcave();

    // Curve function values
    Num getUltAffineRate();


    // Specific ultimately affine curve shapes
    // Burst delay
    boolean isDelayedInfiniteBurst();

    // (Multi-)Rate latency
    boolean isRateLatency();

    int getRLComponentCount();

    CurveMultAffine getRLComponent(int i);

    // (Multi-)Token bucket
    boolean isTokenBucket();

    int getTBComponentCount();

    CurveMultAffine getTBComponent(int i);
}
