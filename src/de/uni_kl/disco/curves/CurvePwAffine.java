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
    Num getSustainedRate();

    // // Specific piecewise affine curve shapes
    
    // Burst delay
    boolean getDelayedInfiniteBurst_Property();

    
    // (Multi-)Rate latency
    boolean getRL_property();
    
    void setRL_Property(boolean is_rate_latency);

    void setRL_MetaInfo(boolean has_rl_meta_info);

    int getRL_ComponentCount();

    CurvePwAffine getRL_Component(int i);

    List<CurvePwAffine> getRL_Components();

    void setRL_Components(List<CurvePwAffine> rate_latencies);

    
    // (Multi-)Token bucket
    boolean getTB_Property();
    
    void setTB_Property(boolean is_token_bucket);

    void setTB_MetaInfo(boolean has_tb_meta_info);

    Num getTB_Burst();
    
    int getTB_ComponentCount();

    List<CurvePwAffine> getTB_Components();

    CurvePwAffine getTB_Component(int i);

    void setTB_Components(List<CurvePwAffine> token_buckets);
}
