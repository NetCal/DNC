/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2011 - 2017 Steffen Bondorf
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.nc.arrivalbounds;

import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.misc.SetUtils;
import de.uni_kl.cs.disco.nc.AbstractArrivalBound;
import de.uni_kl.cs.disco.nc.AnalysisConfig;
import de.uni_kl.cs.disco.nc.AnalysisConfig.MuxDiscipline;
import de.uni_kl.cs.disco.nc.ArrivalBound;
import de.uni_kl.cs.disco.nc.ArrivalBoundDispatch;
import de.uni_kl.cs.disco.nc.analyses.PmooAnalysis;
import de.uni_kl.cs.disco.nc.operations.OperationDispatcher;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import de.uni_kl.cs.disco.network.Flow;
import de.uni_kl.cs.disco.network.Link;
import de.uni_kl.cs.disco.network.Network;
import de.uni_kl.cs.disco.network.Path;
import de.uni_kl.cs.disco.network.Server;

public class PmooArrivalBound extends AbstractArrivalBound implements ArrivalBound {
    private static PmooArrivalBound instance = new PmooArrivalBound();

    private PmooArrivalBound() {
    }

    public PmooArrivalBound(Network network, AnalysisConfig configuration) {
        this.network = network;
        this.configuration = configuration;
    }

    public static PmooArrivalBound getInstance() {
        return instance;
    }

    public Set<ArrivalCurve> computeArrivalBound(Link link, Flow flow_of_interest) throws Exception {
        return computeArrivalBound(link, network.getFlows(link), flow_of_interest);
    }

    /**
     * Computes the PMOO arrival bound for a set of <code>flows_to_bound</code>. The
     * difference to the standard output bound method is that this method tries to
     * compute tighter bounds by concatenating as many servers as possible using the
     * PMOO approach. It does so by searching from <code>server</code> towards the
     * sinks of the flows contained in <code>f_xfcaller</code> until it reaches the
     * server where all these flows first meet each other (the "splitting point").
     * It then concatenates all servers between the splitting point (inclusive) and
     * <code>server</code> (exclusive).
     *
     * @param link             The link that all flows of interest flow into.
     * @param f_xfcaller       The set of flows of interest.
     * @param flow_of_interest The flow of interest to handle with lowest priority.
     * @return The PMOO arrival bounds.
     * @throws Exception If any of the sanity checks fails.
     */
    public Set<ArrivalCurve> computeArrivalBound(Link link, Set<Flow> f_xfcaller, Flow flow_of_interest)
            throws Exception {
        Set<ArrivalCurve> alphas_xfcaller = new HashSet<ArrivalCurve>(
                Collections.singleton(CurvePwAffine.getFactory().createZeroArrivals()));
        if (f_xfcaller == null || f_xfcaller.isEmpty()) {
            return alphas_xfcaller;
        }

        // Get the common sub-path of f_xfcaller flows crossing the given link
        // loi == location of interference
        Server loi = link.getDest();
        Set<Flow> f_loi = network.getFlows(loi);
        Set<Flow> f_xfcaller_loi = SetUtils.getIntersection(f_loi, f_xfcaller);
        f_xfcaller_loi.remove(flow_of_interest);
        if (f_xfcaller_loi.isEmpty()) {
            return alphas_xfcaller;
        }

        if (configuration.multiplexingDiscipline() == MuxDiscipline.GLOBAL_FIFO
                || (configuration.multiplexingDiscipline() == MuxDiscipline.SERVER_LOCAL
                && link.getSource().multiplexingDiscipline() == AnalysisConfig.Multiplexing.FIFO)) {
            throw new Exception("PMOO arrival bounding is not available for FIFO multiplexing nodes");
        }

        Server common_subpath_src = network.findSplittingServer(loi, f_xfcaller_loi);
        Server common_subpath_dest = link.getSource();
        Path common_subpath;
        Set<ServiceCurve> betas_loxfcaller_subpath = new HashSet<ServiceCurve>();

        common_subpath = f_xfcaller_loi.iterator().next().getPath().getSubPath(common_subpath_src, common_subpath_dest);

        if (common_subpath.numServers() == 1) {
            common_subpath = new Path(common_subpath_src);

            Set<Flow> f_xxfcaller = network.getFlows(common_subpath_src);
            f_xxfcaller.removeAll(f_xfcaller_loi);
            f_xxfcaller.remove(flow_of_interest);
            Set<ArrivalCurve> alphas_xxfcaller = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
                    common_subpath_src, f_xxfcaller, flow_of_interest);

            ServiceCurve null_service = CurvePwAffine.getFactory().createZeroService();

            for (ServiceCurve beta_loxfcaller_subpath : OperationDispatcher.lo_arbMux(common_subpath_src.getServiceCurve(),
                    alphas_xxfcaller)) {
                if (!beta_loxfcaller_subpath.equals(null_service)) {
                    betas_loxfcaller_subpath.add(beta_loxfcaller_subpath); // Adding to the set, not adding up the
                    // curves
                }
            }
        } else {
            PmooAnalysis pmoo = new PmooAnalysis(network, configuration);
            betas_loxfcaller_subpath = pmoo.getServiceCurves(flow_of_interest, common_subpath, f_xfcaller_loi);
        }

        // Check if there's any service left on this path. Signaled by at least one
        // service curve in this set
        if (betas_loxfcaller_subpath.isEmpty()) {
            System.out.println("No service left over during PMOO arrival bounding!");
            alphas_xfcaller.clear();
            alphas_xfcaller.add((ArrivalCurve) CurvePwAffine.getFactory().createZeroDelayInfiniteBurst());
            return alphas_xfcaller;
        }

        // Get arrival bound at the splitting point:
        // We need to know the arrival bound of f_xfcaller at the server
        // 'common_subpath_src', i.e., at the above sub-path's source
        // in order to deconvolve it with beta_loxfcaller_subpath to get the arrival
        // bound of the sub-path
        // Note that flows f_xfcaller that originate in 'common_subpath_src' are covered
        // by this call of computeArrivalBound
        Set<ArrivalCurve> alpha_xfcaller_src = ArrivalBoundDispatch.computeArrivalBounds(network, configuration,
                common_subpath_src, f_xfcaller, flow_of_interest);
        alphas_xfcaller = OperationDispatcher.ob_compute(configuration, alpha_xfcaller_src, common_subpath,
                betas_loxfcaller_subpath);

        return alphas_xfcaller;
    }
}
