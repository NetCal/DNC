package de.uni_kl.disco.curves;

import java.util.List;

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
    


    Num getSustainedRate();
    
    Num getTBBurst();

    boolean isHas_token_bucket_meta_info();

    void setHas_token_bucket_meta_info(boolean has_token_bucket_meta_info);

    boolean isIs_token_bucket();

    void setIs_token_bucket(boolean is_token_bucket);

    boolean isIs_rate_latency();

    void setIs_rate_latency(boolean is_rate_latency);

    List<CurvePwAffine> getRate_latencies();

    void setRate_latencies(List<CurvePwAffine> rate_latencies);

    List<CurvePwAffine> getToken_buckets();

    void setToken_buckets(List<CurvePwAffine> token_buckets);

    boolean isHas_rate_latency_meta_info();

    void setHas_rate_latency_meta_info(boolean has_rate_latency_meta_info);
    
    
}
