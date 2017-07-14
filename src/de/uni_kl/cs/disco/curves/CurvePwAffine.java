/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera"
 *
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de/index.php/projects/disco-dnc
 *
 *
 * The Disco Deterministic Network Calculator (DiscoDNC) is free software;
 * you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.cs.disco.curves;

import java.util.List;

import de.uni_kl.cs.disco.numbers.Num;

/**
 * 
 * Interface for piecewise affine curves,
 * including convenience functions used by Disco's implementation DNC operations.
 * I.e., in addition to its defining linear segments,
 * curves may be ascribed as (compositions of) rate-latency and token-bucket functions.
 * 
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

    
    // // Specific piecewise affine curve shapes
    
    // (Composition of) Rate latency
    boolean getRL_property();
    
    void setRL_Property(boolean is_rate_latency);

    void setRL_MetaInfo(boolean has_rl_meta_info);

    int getRL_ComponentCount();

    CurvePwAffine getRL_Component(int i);

    List<CurvePwAffine> getRL_Components();

    void setRL_Components(List<CurvePwAffine> rate_latencies);

    
    // (Composition of) Token bucket
    boolean getTB_Property();
    
    void setTB_Property(boolean is_token_bucket);

    void setTB_MetaInfo(boolean has_tb_meta_info);

    Num getTB_Burst();
    
    int getTB_ComponentCount();

    List<CurvePwAffine> getTB_Components();

    CurvePwAffine getTB_Component(int i);

    void setTB_Components(List<CurvePwAffine> token_buckets);
}
