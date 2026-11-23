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

package org.networkcalculus.dnc.algebra.disco.con_pw_affine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.networkcalculus.dnc.Calculator;
import org.networkcalculus.dnc.algebra.disco.MinPlus_Disco_Configuration;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.Curve_PwAffine;
import org.networkcalculus.dnc.curves.LinearSegment;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.dnc.utils.CheckUtils;
import org.networkcalculus.num.Num;

/**
 * Deconvolution for convex or (almost) concave, (almost) continuous functions.
 * 
 * Functions of this class are defined by 
 * - the maximum over multiple rate-latency curves (mRL, convex functions),
 * - the minimum over multiple token-bucket curves (mTB, almost concave/continuous functions as f(0)=0),
 * - the delayed infinite burst functions \delta_{T} used in the min/max above.
 * 
 * The deconvolution of piecewise affine curves implemented in the original dnclib 
 * was based on a flawed result (for pw affine but working for affine curves) presented in
 * 
 * 		Improving Performance Bounds in Feed-Forward Networks by Paying Multiplexing Only Once
 * 		(Jens Schmitt, Frank A. Zdarsky, Ivan Martinovic),
 * 		In Proc. of the 14th GI/ITG MMB, 2008.
 * 
 * This replacement, implemented since the version 2.2.5 (2015-Sep-29), is however 
 * restricted to the above class of curves. 
 */
public abstract class Deconvolution_Disco_ConPwAffine {

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, ServiceCurve service_curve) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();
        switch (CheckUtils.inputNullCheck(arrival_curves, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        if (arrival_curves.isEmpty()) {
            results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
            return results;
        }

        for (ArrivalCurve alpha : arrival_curves) {
            results.add(deconvolve(alpha, service_curve));
        }

        return results;
    }

    public static Set<ArrivalCurve> deconvolve(Set<ArrivalCurve> arrival_curves, Set<ServiceCurve> service_curves) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

        switch (CheckUtils.inputNullCheck(arrival_curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(arrival_curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }

        for (ServiceCurve beta : service_curves) {
            for (ArrivalCurve alpha : arrival_curves) {
                results.add(deconvolve(alpha, beta));
            }
        }

        return results;
    }

    public static ArrivalCurve deconvolve(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        switch (CheckUtils.inputNullCheck(arrival_curve, service_curve)) {
            case 0:
                break;
            case 1:
            case 3:
                return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
            case 2:
                return Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
            default:
        }

        if (service_curve.equals(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get())
                || (service_curve.isDelayedInfiniteBurst() && service_curve.getLatency().doubleValue() == 0.0)
                || (arrival_curve.equals(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get()))) {
            return arrival_curve.copy();
        }
        if (service_curve.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())
				|| service_curve.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (service_curve.getUltAffineRate().eqZero()
                && service_curve.getSegment(service_curve.getSegmentCount() - 1).getY().eqZero())) {
            return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        }
        return deconvolve_con_pw_affine(arrival_curve, service_curve);
    }

    public static Set<ArrivalCurve> deconvolve_almostConcCs_SCs(Set<Curve> curves,
                                                                Set<ServiceCurve> service_curves) {
        Set<ArrivalCurve> results = new HashSet<ArrivalCurve>();

        switch (CheckUtils.inputNullCheck(curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }
        switch (CheckUtils.inputEmptySetCheck(curves, service_curves)) {
            case 0:
                break;
            case 1:
            case 3:
                results.add(Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get());
                return results;
            case 2:
                results.add(Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
                return results;
            default:
        }

        Num latency;
        Num latency_reduction;
        for (ServiceCurve sc : service_curves) {
            for (Curve pwa_c : curves) {
                latency = pwa_c.getLatency();
                if (latency.eqZero() || latency.isInfinite()) {
                    results.add(Curve.getFactory().createArrivalCurve(Curve.getUtils().shiftRight(
                            deconvolve_con_pw_affine(Curve.getUtils().shiftLeftClipping(pwa_c, latency), sc),
                            latency)));
                } else {
                    // (delta_L (x) sigma_0) (/) beta = sigma_0 (/) (beta (/) delta_L) if beta = 0 on [0,L], i.e., L <= T.
                    // Shifting the result of sigma_0 (/) beta right by L instead is 0 on (0,L], i.e., too low.
                    // For L > T, shifting beta by T only gives sigma_0 (/) (beta (/) delta_T), a valid upper bound.
                    latency_reduction = Num.getUtils(Calculator.getInstance().getNumBackend()).min(latency, sc.getLatency());
                    results.add(deconvolve(
                            Curve.getFactory().createArrivalCurve(Curve.getUtils().shiftLeftClipping(pwa_c, latency)),
                            Curve.getFactory().createServiceCurve(Curve.getUtils().shiftLeftClipping(sc, latency_reduction))));
                }
            }
        }

        return results;
    }

    /**
     * Returns the deconvolution of an (almost) concave arrival curve and a convex
     * service curve.
     *
     * @param curve_1 The (almost) concave arrival curve.
     * @param curve_2 The convex service curve.
     * @return The deconvolved curve, an arrival curve.
     */
    private static ArrivalCurve deconvolve_con_pw_affine(Curve curve_1, Curve curve_2) {
        // if( CalculatorConfig.OPERATOR_INPUT_CHECKS ) {
        switch (CheckUtils.inputNullCheck(curve_1, curve_2)) {
            case 0:
                break;
            case 1:
            case 3:
                return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
            case 2:
                return Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
            default:
        }
        // }

        if (curve_1.getUltAffineRate().gt(curve_2.getUltAffineRate())) { // Violation of the stability constraint
            return Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get();
        }
        if (curve_2.equals(Curve_ConstantPool.INFINITE_SERVICE_CURVE.get())) {
            return Curve.getFactory().createArrivalCurve((Curve_PwAffine) curve_1);
        }
        if (curve_2.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get())
        		|| curve_2.getLatency().equals(Num.getFactory(Calculator.getInstance().getNumBackend()).getPositiveInfinity())
                || (curve_2.getUltAffineRate().eqZero() && curve_2.getSegment(1).getY().eqZero())) {
            return Curve_ConstantPool.ZERO_ARRIVAL_CURVE.get();
        }
        if (MinPlus_Disco_Configuration.getInstance().exec_deconvolution_checks()) {
            if (!((Curve_PwAffine) curve_1).isAlmostConcave()) {
                throw new IllegalArgumentException("Arrival curve of deconvolution must be almost concave.");
            }
            if (!((Curve_PwAffine) curve_2).isConvex()) {
                throw new IllegalArgumentException("Service curve of deconvolution must be convex.");
            }
        }

        // The arrival curve itself is in the candidates set.
        Set<Curve> result_candidates = new HashSet<Curve>(Collections.singleton(curve_1.copy()));

        // Candidates resulting from the service curve's inflection points (curve_2).
        // It's simply the vertical deviation at the inflection point followed by the
        // arrival curve's segments (lowered by beta(inflection)).
        Curve candidate_tmp;
        Num x_inflect_beta, y_beta, y_alpha;
        for (int i = 1; i < curve_2.getSegmentCount(); i++) { // Start at 1 to skip the arrival curve itself (see
            // above):

            x_inflect_beta = curve_2.getSegment(i).getX();
            candidate_tmp = Curve.getUtils().shiftLeftClipping((Curve) curve_1, x_inflect_beta);

            y_beta = curve_2.f(x_inflect_beta);
            if (y_beta.doubleValue() != 0.0) { // Need to lower the rest of the result candidate by y.
                for (int j = 0; j < candidate_tmp.getSegmentCount(); j++) {
                    LinearSegment lin_seg = candidate_tmp.getSegment(j);
                    y_alpha = lin_seg.getY();
                    candidate_tmp.getSegment(j).setY(Num.getUtils(Calculator.getInstance().getNumBackend()).sub(y_alpha, y_beta));
                }
            }
            result_candidates.add(candidate_tmp);
        }

        // Candidates resulting from the arrival curve's inflection points (curve_1):
        // For an inflection point a > 0 of the arrival curve, the candidate is
        //   D_a(t) = alpha(a) - beta(a - t) for t in [0,a] and D_a(t) = alpha(a) for t > a,
        // i.e., it starts with "burstiness" alpha(a) - beta(a) and then follows beta's segments
        // left of a in reverse order (the first one might be cut off by a).
        //
        // Note: D_a must also be considered if alpha(a) < beta(a).
        // Versions 2.2.5 to 2.8.x skipped these candidates. That is unsound, i.e., it can result in too optimistic curves.
        // E.g., alpha = min(2t+1, t/2+16), beta = max(t, 10t-45): (alpha deconv beta)(25/3) = alpha(10) - beta(5/3) = 58/3,
        // whereas skipping D_10 resulted in 53/3.
        // Candidate values below 0 do not harm as the arrival curve candidate is >= 0.
        //
        // If beta contains a delayed infinite burst delta_T (last segment with infinite y-coordinate) and T < a,
        // then only u = a - t <= T is feasible. D_a is -infinity on [0, a-T).
        // There, we replace it by the zero curve (alpha(t) >= 0 is a candidate, too).
        Num zero = Num.getFactory(Calculator.getInstance().getNumBackend()).createZero();
        Num x_inflect_alpha, results_cand_burst, t_start, u_start;
        LinearSegment current_candidate_segment, current_beta_segment;

        for (int i = curve_1.getSegmentCount() - 1; i >= 0; i--) {
            x_inflect_alpha = curve_1.getSegment(i).getX();
            if (x_inflect_alpha.eqZero()) { // The inflection point is in the origin and thus the candidate is a zero curve.
                continue;
            }
            y_alpha = curve_1.f(x_inflect_alpha);

            // Find beta's segment j that defines beta left of alpha's inflection point, i.e., largest j with x_j <= a.
            // Segment 0 starts in the origin.
            int j = curve_2.getSegmentCount() - 1;
            while (j > 0 && curve_2.getSegment(j).getX().gt(x_inflect_alpha)) {
                j--;
            }

            u_start = x_inflect_alpha;
            t_start = zero;
            if (curve_2.getSegment(j).getY().isInfinite()) { // Delayed infinite burst, see above.
                u_start = curve_2.getSegment(j).getX();
                t_start = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(x_inflect_alpha, u_start);
                j--;
            }
            if (j < 0) {
                continue;
            }

            // The resulting curve's segments:
            // The origin, j+1 segments (we start counting j at 0), and a horizontal line at the end.
            int segment_count = j + 3; // At least 3 (the origin, the segment starting with the burst, the horizontal line)
            candidate_tmp = Curve.getFactory().createArrivalCurve(segment_count); // Consists of zero segments (x,y),r = (0,0),0 only.
            // The origin (first segment, id 0) stays as is, the remainder needs to be constructed.

            // Second segment: it starts in t_start with value alpha(a) - beta(u_start).
            current_beta_segment = curve_2.getSegment(j);
            results_cand_burst = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(y_alpha,
                    current_beta_segment.f(u_start));

            current_candidate_segment = candidate_tmp.getSegment(1);
            current_candidate_segment.setX(t_start);
            current_candidate_segment.setY(results_cand_burst);
            current_candidate_segment.setGrad(current_beta_segment.getGrad().copy());
            if (t_start.gtZero()) { // D_a(a-T) = alpha(a) - beta(T) is finite.
                current_candidate_segment.setLeftopen(false);
            }

            // The remaining service curve segments in reverse order:
            // Candidate segment k starts at t = a - x_{j_new+1} where u = a - t enters beta's segment j_new.
            int j_new = j;
            Num next_x_coord;
            for (int k = 2; k < segment_count; k++) {
                next_x_coord = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(x_inflect_alpha,
                        curve_2.getSegment(j_new).getX());
                // alpha(a) - beta(x_{j_new}), evaluated on beta's segment j_new.
                Num next_y_coord = Num.getUtils(Calculator.getInstance().getNumBackend()).sub(y_alpha,
                        current_beta_segment.getY());
                j_new--;

                current_candidate_segment = candidate_tmp.getSegment(k);
                current_candidate_segment.setX(next_x_coord);
                current_candidate_segment.setY(next_y_coord);
                if (j_new >= 0) {
                    current_beta_segment = curve_2.getSegment(j_new);
                    current_candidate_segment.setGrad(current_beta_segment.getGrad().copy());
                } // else: horizontal line at the end, gradient (rate) remains zero.
            }

            // Done with this alpha inflection point. Store the curve and go to next alpha inflection point.
            result_candidates.add(candidate_tmp);
        }

        Iterator<Curve> candidates_iter = result_candidates.iterator();
        Curve sup_curve, additional_curve;

        if (!candidates_iter.hasNext()) {
            System.out.println("Deconvolution of " + curve_1.toString() + "\nand " + curve_2.toString() + " failed.");
            System.exit(0);
        }

        sup_curve = candidates_iter.next();
        while (candidates_iter.hasNext()) {
            additional_curve = candidates_iter.next();
            sup_curve = Curve.getUtils().max((Curve) sup_curve, (Curve) additional_curve);
        }

        return Curve.getFactory().createArrivalCurve((Curve) sup_curve);
    }


    /**
     * Assumption: Arrival Curve (@curve_1) is a token bucket curve, Service Curve (@curve_2) is a curve (eventually with a jump) and the rates of the segments are decreasing
     * @param curve_1
     * @param curve_2
     * @return
     */
    public static ArrivalCurve deconvolve_simple_fifo(Curve curve_1, Curve curve_2) {
        // With the given assumptions:
        // Output arrival curve is simply a token bucket one: output rate = curve_1.rate and output burst = curve_1.burst + curve_2.latency * curve_1.rate

        if(curve_1.isDelayedInfiniteBurst() ||  curve_2.equals(Curve_ConstantPool.ZERO_SERVICE_CURVE.get()) )
        {
            return Curve.getFactory().createArrivalCurve((Curve)Curve_ConstantPool.INFINITE_ARRIVAL_CURVE.get());
        }

        Num ac_burst = curve_1.getBurst();
        Num ac_rate = curve_1.getUltAffineRate();
        Num output_burst = Num.getUtils(Calculator.getInstance().getNumBackend()).mult( curve_2.getLatency(), ac_rate);
        output_burst = Num.getUtils(Calculator.getInstance().getNumBackend()).add(  output_burst, ac_burst);


        ArrivalCurve output_ac = Curve.getFactory().createTokenBucket(ac_rate, output_burst);

        return output_ac;
    }
}
