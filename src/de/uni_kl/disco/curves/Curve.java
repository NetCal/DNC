package de.uni_kl.disco.curves;

import java.util.List;

import de.uni_kl.disco.numbers.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public interface Curve {
    Curve copy();

    void copy(Curve curve);

    // Curve's segments (incl. manipulation)
//	void setSegment( int pos, LinearSegment s );
    LinearSegment getSegment(int pos);

    int getSegmentCount();

    int getSegmentDefining(Num x);
//	getSegmentLimitRight( Num x );

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

    Num getTBBurst();

    boolean isHas_token_bucket_meta_info();

    void setHas_token_bucket_meta_info(boolean has_token_bucket_meta_info);

    boolean isIs_token_bucket();

    void setIs_token_bucket(boolean is_token_bucket);

    boolean isIs_rate_latency();

    void setIs_rate_latency(boolean is_rate_latency);

    List<CurveMultAffine> getRate_latencies();

    void setRate_latencies(List<CurveMultAffine> rate_latencies);

    List<CurveMultAffine> getToken_buckets();

    void setToken_buckets(List<CurveMultAffine> token_buckets);

    boolean isHas_rate_latency_meta_info();

    void setHas_rate_latency_meta_info(boolean has_rate_latency_meta_info);
    // Curve manipulation (entire curve)
    //void beautify();


    Num getSustainedRate();

}
