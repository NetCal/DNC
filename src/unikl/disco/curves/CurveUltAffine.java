package unikl.disco.curves;

import unikl.disco.numbers.Num;

/**
 * Interface for wide-sense increasing, ultimately affine curves.
 */
public interface CurveUltAffine extends Curve {
    @Override
    CurveUltAffine copy();

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

    CurveUltAffine getRLComponent(int i);

    // (Multi-)Token bucket
    boolean isTokenBucket();

    int getTBComponentCount();

    CurveUltAffine getTBComponent(int i);
}
