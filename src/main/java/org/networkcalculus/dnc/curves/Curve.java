/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2017 - 2018 The DiscoDNC contributors
 * Copyright (C) 2019+ The DNC contributors
 *
 * http://networkcalculus.org
 *
 *
 * The Deterministic Network Calculator (DNC) is free software;
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package org.networkcalculus.dnc.curves;

import java.util.List;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.num.Num;

/**
 * Interface for wide-sense increasing, plain curves.
 */
public interface Curve {

    // --------------------------------------------------------------------------------------------------------------
    // Factory
    // Easy factory and utils access
    // --------------------------------------------------------------------------------------------------------------

    static CurveFactory_Affine getFactory() {
        return Calculator.getInstance().getCurveFactory();
    }

    static CurveUtils getUtils() {
        return Calculator.getInstance().getCurveUtils();
    }

    // --------------------------------------------------------------------------------------------------------------
    // X-Axis
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Create and return a linear segment that lies on the x axis of the Cartesian
     * coordinate system.
     *
     * @return
     */
    static LinearSegment getXAxis() {
        // Need to create it anew as the number representation might have changed.
        return LinearSegment.createHorizontalLine(0.0);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface
    // --------------------------------------------------------------------------------------------------------------
    
    // Segments
    /**
     * Curves are defined by a numbered sequence of linear segments.
     * This method returns the segment at the given position in curve-defining sequence.
     * The first segment is by definition at position 0.
     * 
     * @param pos Position of the segment to return.
     * @return The linear segment at position pos.
     */
    LinearSegment getSegment(int pos);

    int getSegmentCount();

    int getSegmentDefining(Num x);

    void addSegment(LinearSegment s);

    // Properties
    boolean isConvex();

    boolean isConcave();

    // Function Values
    Num f(Num x);

    Num fLimitRight(Num x);

    Num f_inv(Num y);

    Num f_inv(Num y, boolean rightmost);

    Num getLatency();

    Num getBurst();
    
    Num getUltAffineRate();

    Num getGradientLimitRight(Num x);

    // Copying
    Curve copy();

    void copy(Curve curve);

    // ------------------------------------------------------------
    // Properties, Special Shapes etc.
    // ------------------------------------------------------------

    void addSegment(int pos, LinearSegment s);

    void setTB_Components(List<Curve> token_buckets);
    
    void setRL_MetaInfo(boolean has_rl_meta_info);
    
    void setRateLateny(boolean is_rate_latency);
    
    void setRL_Components(List<Curve> rate_latencies);
    
    boolean isAlmostConcave();
    
    int getRL_ComponentCount();
    
    int getTB_ComponentCount();
    
    Curve getRL_Component(int i);
    
    Curve getTB_Component(int i);
    
    void setTokenBucket(boolean is_token_bucket);
    
    void removeSegment(int pos);

    boolean isDelayedInfiniteBurst();

    boolean isDiscontinuity(int pos);

    boolean isRealDiscontinuity(int pos);

    boolean isUnrealDiscontinuity(int pos);
    
    void setTB_MetaInfo(boolean has_tb_meta_info);

    boolean isWideSenseIncreasing();

    boolean isConcaveIn(Num a, Num b);

    boolean isConvexIn(Num a, Num b);

    // ------------------------------------------------------------
    // Methods to override
    // ------------------------------------------------------------
    
    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
