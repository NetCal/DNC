package org.networkcalculus.dnc.utils;

import org.networkcalculus.dnc.AnalysisConfig;
import org.networkcalculus.dnc.backend.Curve;
import org.networkcalculus.dnc.backend.CurveFactory;
import org.networkcalculus.dnc.backend.CurveUtils;
import org.networkcalculus.dnc.network.server_graph.Path;
import org.networkcalculus.dnc.network.server_graph.Server;
import org.networkcalculus.dnc.utils.CurveFunctionsUtils;
import org.networkcalculus.dnc.utils.MinPlusUtils;
import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

public class CurveBoundsUtils {
    public static Curve leftOverServiceARB(Curve service, Curve arrival) {
        Curve sub = CurveUtils.sub(service, arrival);
        return sub.nonDecreasingClosure();
    }

    public static Set<Curve> leftOverServiceARB(Curve service, Set<Curve> arrivals) {
        Set<Curve> result = new HashSet<>();
        for (Curve a : arrivals) {
            result.add(leftOverServiceARB(service, a));
        }
        return result;
    }

    public static Curve leftOverServiceFIFO(Curve service, Curve arrival) {
        Number burst = arrival.getBaseSequence().getSegments().getFirst().getRightLimitAtStartTime();
        Number theta = CurveUtils.f_inv(service, burst);
        return fifoMux(service, arrival, theta);
    }

    public static Set<Curve> leftOverServiceFIFO(Curve service, Set<Curve> arrivals) {
        Set<Curve> results = new HashSet<Curve>();

        for (Curve alpha : arrivals) {
            Number burst = alpha.getBaseSequence().getSegments().getFirst().getRightLimitAtStartTime();
            Number theta_curr_lb = CurveUtils.f_inv(service, burst);
            Curve leftover_sc = fifoMux(service, alpha, theta_curr_lb);
            results.add(leftover_sc);
        }

        return results;
    }

    public static Curve fifoMux(Curve service_curve, Curve arrival_curve, Number theta) {

        if (theta.eq(Number.ZERO))
            return leftOverServiceARB(service_curve, arrival_curve);

        Curve ac_shifted = CurveUtils.shiftRight(arrival_curve, theta);
        Curve left_over = CurveUtils.sub(service_curve, ac_shifted);
        Curve result = CurveUtils.minimum(left_over, CurveFactory.createDelayedInfiniteBurst(theta));
        CurveUtils.beautify(result);
        return result;
    }

    public static Set<Curve> leftOverService(AnalysisConfig config, Curve service, Set<Curve> arrivals) {
        if (config.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO) {

            return leftOverServiceFIFO(service, arrivals);
        }
        return leftOverServiceARB(service, arrivals);
    }

    public static Set<Curve> leftOverService(AnalysisConfig config, Server server, Set<Curve> arrivals) {
        if (config.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO
                || (config.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.SERVER_LOCAL
                && server.multiplexing() == AnalysisConfig.Multiplexing.FIFO)) {

            return leftOverServiceFIFO(server.getServiceCurve(), arrivals);
        }
        return leftOverServiceARB(server.getServiceCurve(), arrivals);
    }

    public static Curve computeFIFOOutputBound(Curve arrival, Curve betaLo) {
        return MinPlusUtils.deconvolve(arrival, betaLo);
    }

    public static Set<Curve> output(AnalysisConfig configuration,
                                    Set<Curve> arrival_curves, Server server) throws Exception {
        return output(configuration, arrival_curves, server, Collections.singleton(server.getServiceCurve()));
    }

    public static Set<Curve> output(AnalysisConfig configuration, Set<Curve> arrival_curves,
                                    Server server, Set<Curve> betas_lo) throws Exception {
        if (configuration.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO) {
            throw new Exception("Not yet supported.");
        }

        Set<Curve> output_bound = new HashSet<Curve>();

        switch (configuration.enforceMaxSC()) {
            case GLOBALLY_ON:
                output_bound = MinPlusUtils
                        .deconvolve(MinPlusUtils.convolve_ACs_MSC(arrival_curves, server.getStoredMaxSC()), betas_lo);
                break;
            case GLOBALLY_OFF:
                output_bound = MinPlusUtils.deconvolve(arrival_curves, betas_lo);
                break;
            case SERVER_LOCAL:
            default:
                output_bound = MinPlusUtils.deconvolve(
                        MinPlusUtils.convolve_ACs_MSC(arrival_curves, server.getMaxServiceCurve()), betas_lo);
                break;
        }

        switch (configuration.enforceMaxScOutputRate()) {
            case GLOBALLY_ON:
                output_bound = MinPlusUtils.convolve_ACs_EGamma(output_bound, server.getStoredMaxScRate());
                break;
            case GLOBALLY_OFF:
                break;
            case SERVER_LOCAL:
            default:
                output_bound = MinPlusUtils.convolve_ACs_EGamma(output_bound, server.getMaxScRate());
                break;
        }

        return output_bound;
    }

    public static Set<Curve> output(AnalysisConfig configuration, Set<Curve> arrival_curves, Path path,
                                    Set<Curve> betas_lo) throws Exception {
        if (configuration.enforceMultiplexing() == AnalysisConfig.MultiplexingEnforcement.GLOBAL_FIFO) {
            Set<Curve> output_bound = new HashSet<Curve>();

            Curve ac = null;
            for (Curve curve : arrival_curves) {
                ac = curve;
            }
            Curve sc = null;
            for (Curve curve : betas_lo) {
                sc = curve;
            }

            Curve output_ac = computeFIFOOutputBound(ac, sc);
            output_bound.add(output_ac);
            return output_bound;
        } else {
            Set<Curve> output_bound = new HashSet<Curve>();

            switch (configuration.enforceMaxSC()) {
                case GLOBALLY_ON:
                    output_bound = MinPlusUtils.deconvolve(
                            MinPlusUtils.convolve_ACs_MSC(arrival_curves, path.getStoredMaxSC()), betas_lo);
                    break;

                case GLOBALLY_OFF:
                    output_bound = MinPlusUtils.deconvolve(arrival_curves, betas_lo);
                    break;

                case SERVER_LOCAL:
                default:
                    output_bound = MinPlusUtils.deconvolve(
                            MinPlusUtils.convolve_ACs_MSC(arrival_curves, path.getMaxServiceCurve()), betas_lo);
                    break;
            }

            switch (configuration.enforceMaxScOutputRate()) {
                case GLOBALLY_ON:
                    output_bound = MinPlusUtils.convolve_ACs_EGamma(output_bound, path.getStoredMaxScRate());
                    break;

                case GLOBALLY_OFF:
                    break;

                case SERVER_LOCAL:
                default:
                    output_bound = MinPlusUtils.convolve_ACs_EGamma(output_bound, path.getMaxScRate());
                    break;
            }
            return output_bound;
        }
    }
}
