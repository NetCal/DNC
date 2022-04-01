/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc.bounds.disco.pw_affine;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.AnalysisConfig.MultiplexingEnforcement;
import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.curves.*;
import org.networkcalculus.dnc.curves.disco.LinearSegment_Disco;
import org.networkcalculus.dnc.curves.disco.pw_affine.Curve_Disco_PwAffine;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.num.Num;

public final class LeftOverService_Disco_PwAffine {
    public static Set<ServiceCurve> compute(AnalysisConfig configuration, Server server,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO
                || (configuration.enforceMultiplexing() == MultiplexingEnforcement.SERVER_LOCAL
                && server.multiplexing() == Multiplexing.FIFO)) {
            return fifoMux(server.getServiceCurve(), arrival_curves);
        } else {
            return LeftOverService_Disco_PwAffine.arbMux(server.getServiceCurve(), arrival_curves);
        }
    }

    public static Set<ServiceCurve> compute(AnalysisConfig configuration, ServiceCurve service_curve,
                                            Set<ArrivalCurve> arrival_curves) {
        if (configuration.enforceMultiplexing() == MultiplexingEnforcement.GLOBAL_FIFO) {
            return LeftOverService_Disco_PwAffine.fifoMux(service_curve, arrival_curves);
        } else {
            return LeftOverService_Disco_PwAffine.arbMux(service_curve, arrival_curves);
        }
    }

    public static ServiceCurve fifoMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
            // Local min latency for theta
            ArrivalCurve ac = arrival_curve;
            ServiceCurve sc = service_curve;
            Num burst = ac.getBurst();

            Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;

            Num theta_curr_lb = curve.f_inv(burst);
            ServiceCurve leftover_sc = LeftOverService_Disco_PwAffine.fifoMux(sc, ac, theta_curr_lb);

            return leftover_sc;
    }

    public static Set<ServiceCurve> fifoMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {

        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        for (ArrivalCurve alpha : arrival_curves) {
            // Local min latency for theta
            ArrivalCurve ac = alpha;
            ServiceCurve sc = service_curve;
            Num burst = ac.getBurst();

            Curve_Disco_PwAffine curve = (Curve_Disco_PwAffine) sc;

            Num theta_curr_lb = curve.f_inv(burst);
            ServiceCurve leftover_sc = LeftOverService_Disco_PwAffine.fifoMux(sc, ac, theta_curr_lb);
            results.add(leftover_sc );
        }

        return results;
    }



    public static Set<ServiceCurve> arbMux(ServiceCurve service_curve, Set<ArrivalCurve> arrival_curves) {
        Set<ServiceCurve> results = new HashSet<ServiceCurve>();

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(arbMux(service_curve, alpha));
        }

        return results;
    }

    /**
     * Computes the left-over service curve for a server under arbitrary
     * multiplexing with the service curve <code>beta</code> experiencing
     * cross-traffic with arrival curve <code>alpha</code>.
     *
     * @param arrival_curve The arrival curve of cross-traffic
     * @param service_curve The server's service curve
     * @return The FIFO service curve
     */
    public static ServiceCurve arbMux(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	Pair<Boolean,ServiceCurve> special_cases = computeSpecialValues(service_curve, arrival_curve);
    	
    	if(special_cases.getFirst().booleanValue() == true) {
    		return special_cases.getSecond(); 
    	} else {
            return Curve.getFactory().createServiceCurve(Curve.getUtils().boundAtXAxis(Curve.getUtils().sub(service_curve, arrival_curve)));
    	}
    }
    
    // The FIFO left-over service curve which we get for a specific shift of the arrival curve
    public static ServiceCurve fifoMux(ServiceCurve service_curve, ArrivalCurve arrival_curve, Num theta) {

        if(arrival_curve.isDelayedInfiniteBurst() || service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get()))
        {
            return Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
        }

        if(theta.ltZero())
        {
            System.err.println(theta);
            throw new IllegalArgumentException("Theta must be >= 0!");
        }

        // Have to make sure that the curve isn't negative (for every segment) [using boundatX] and zero until theta!
        try{

            Curve arrival_curve_shifted =  Curve.getUtils().shiftRight(arrival_curve, theta);


            ServiceCurve sc_transform = Curve.getFactory().createZeroService(); // zero until theta, after that "copy" of service_curve
            int sc_transform_curr_seg = 1; // createZeroService creates one segment starting at origin with slope 0
            boolean copy = false; // to distinguish if we have to copy or set sc_transform to zero
            int nrsegs = service_curve.getSegmentCount();

            // computation of sc_transform
            if(theta.eqZero())
            {
                // no shift
                sc_transform = service_curve.copy();
            }

            else
            {
                // shift
                for(int i = 0; i < nrsegs; i++)
                {
                    LinearSegment seg =  service_curve.getSegment(i);

                    if(!copy)
                    {
                        if(theta.eq(seg.getX()))
                        {
                            // theta is at the start of segment i
                            // copy segment i but it has to be leftopen
                            LinearSegment seg_transformed = seg.copy();
                            seg_transformed.setLeftopen(true);
                            sc_transform.addSegment(sc_transform_curr_seg++, seg_transformed);
                            copy = true;
                        }

                        else{
                            // theta is not at the start of segment i
                            boolean theta_in_curr_seg = false;
                            if(i!= nrsegs-1)
                            {
                                if(  theta.lt(  ( service_curve.getSegment(i+1)).getX())   )
                                {
                                    copy = true;
                                    theta_in_curr_seg = true;
                                }
                            }
                            else
                            {
                                // i is last segment
                                theta_in_curr_seg = true;
                                copy = true; // doesn't really matter since it's the last segment anyway
                            }

                            if(theta_in_curr_seg)
                            {
                                // compute v := curr_seg.getYAt(X=theta)
                                Num delta = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(theta, seg.getX());
                                Num delta_times_slope = Num.getUtils(Calculator.getInstance().getNumBackend()).mult(delta, seg.getGrad());
                                Num v = Num.getUtils(Calculator.getInstance().getNumBackend()).add(delta_times_slope, seg.getY());
                                // Num x, Num y, Num grad, boolean leftopen
                                LinearSegment_Disco new_segment = new LinearSegment_Disco(theta, v, seg.getGrad(), true); // has to be leftopen
                                sc_transform.addSegment(sc_transform_curr_seg++, new_segment);
                            }
                        }
                    }

                    else
                    {
                        sc_transform.addSegment(sc_transform_curr_seg++, seg.copy());
                    }
                }
            }


            Curve left_over_sc =  Curve.getUtils().boundAtXAxis ( Curve.getUtils().sub(sc_transform, arrival_curve_shifted));

            // e.g. joins two "joinable" segments such as SC{(0.0,0.0),0.0;!(20.0,0.0),0.0;(25.333333333333332,0.0),15.0} to SC{(0.0,0.0),0.0;(25.333333333333332,0.0),15.0}
            Curve.getUtils().beautify(left_over_sc);


            ServiceCurve left_over = Curve.getFactory().createServiceCurve(left_over_sc.copy());

            return left_over;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return Curve.getFactory().createZeroService(); // shouldn't be reached
    }

    /**
     * Try to compute the left-over service curve for special arrival or service curve values like zero or infinite.
     * In case we find infinite service and infinite arrivals, 
     * we define the indeterminate form resulting from \infty - \infty as zero service. 
     * 
     * @param service_curve The service curve to be subtracted from. 
     * @param arrival_curve The arrival curve to subtract from the service curve.
     * @return A pair consisting of a boolean and a service curve. The boolean indicates the validity of the returned service curve. 
     */
    private static Pair<Boolean,ServiceCurve> computeSpecialValues(ServiceCurve service_curve, ArrivalCurve arrival_curve) {
    	
    	if(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get().equals(arrival_curve)
    			|| Curve_ConstantPool.ZERO_SERVICE_CURVE.get().equals(service_curve)) {
    		return new Pair<Boolean,ServiceCurve>(true,Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
    	} else { // The else-clause is not required. It indicates that the following if-clause cannot be moved before the previous one.
	    	if(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get().equals(service_curve)) {
	    		return new Pair<Boolean,ServiceCurve>(true,Curve_ConstantPool.INFINITE_SERVICE_CURVE.get());
	    	}
    	}
    	if(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get().equals(arrival_curve)) {
    		return new Pair<Boolean,ServiceCurve>(true,service_curve.copy());
    	}
    	
    	// In case the caller disregards that there was no match to special values,
    	// we provide the worst-case left-over service: 0.
    	return new Pair<Boolean,ServiceCurve>(false,Curve_ConstantPool.ZERO_SERVICE_CURVE.get());
    }
}
