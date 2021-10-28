/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
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

package org.networkcalculus.dnc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class contains configuration settings that are considered during the analysis,
 * e.g., the multiplexing behavior of servers and the arrival bounding methods to use.
 * These settings can be changed at runtime.
 */
public class AnalysisConfig {
    public enum Multiplexing {
        ARBITRARY, FIFO
    }

    public enum MultiplexingEnforcement {
        SERVER_LOCAL, GLOBAL_ARBITRARY, GLOBAL_FIFO
    }

    public enum MaxScEnforcement {
        SERVER_LOCAL, GLOBALLY_ON, GLOBALLY_OFF
    }

    public enum ArrivalBoundMethod {
		AGGR_PBOO_PER_SERVER, AGGR_PBOO_CONCATENATION, AGGR_PMOO, AGGR_TM,
		SEGR_PBOO, SEGR_PMOO, SEGR_TM, 
		SINKTREE_AFFINE_MINPLUS, SINKTREE_AFFINE_DIRECT, SINKTREE_AFFINE_HOMO
    }
    
    private MultiplexingEnforcement multiplexing_enforcement = MultiplexingEnforcement.SERVER_LOCAL;

    private static MultiplexingEnforcement multiplexing_enforcement_static = MultiplexingEnforcement.SERVER_LOCAL;


    /**
     * Whether to use maximum service curves in output bound computation.
     */
    private MaxScEnforcement enforce_max_sc = MaxScEnforcement.SERVER_LOCAL;
    
    /**
     * Whether to constrain the output bound further through convolution with the
     * maximum service curve's rate as the server cannot output data faster than
     * this rate.
     */
    private MaxScEnforcement enforce_max_sc_output_rate = MaxScEnforcement.SERVER_LOCAL;
    private Set<ArrivalBoundMethod> arrival_bound_methods = new HashSet<ArrivalBoundMethod>(Collections.singleton(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION));
    private boolean convolve_alternative_arrival_bounds = true;
	private boolean arrival_bounds_caching = true;
    private boolean flow_prolongation = false;
    private boolean server_backlog_arrival_bound = false;
    public static String path_to_cplex ="";
    public static String path_to_lp_dir ="";
    
    public AnalysisConfig() {
    }
    
    // TODO If ArrivalBoundMethod.AGGR_PMOO is the only arrival bound method, then the server_backlog_arrival_bound cannot improve the results.
    //		It just causes more effort. Notify the user but do not spam the terminal output with too many messages.
    public AnalysisConfig(MultiplexingEnforcement multiplexing_enforcement, MaxScEnforcement enforce_max_sc, MaxScEnforcement enforce_max_sc_output_rate,
                          Set<ArrivalBoundMethod> arrival_bound_methods, 
                          boolean convolve_alternative_arrival_bounds, boolean arrival_bounds_caching, 
                          boolean server_backlog_arrival_bound) {
        this.multiplexing_enforcement = multiplexing_enforcement;
        multiplexing_enforcement_static = multiplexing_enforcement;
        this.enforce_max_sc = enforce_max_sc;
        this.enforce_max_sc_output_rate = enforce_max_sc_output_rate;
        this.arrival_bound_methods.clear();
        this.arrival_bound_methods.addAll(arrival_bound_methods);
        this.convolve_alternative_arrival_bounds = convolve_alternative_arrival_bounds;
		this.arrival_bounds_caching = arrival_bounds_caching;
        this.server_backlog_arrival_bound = server_backlog_arrival_bound;

    }

    public MultiplexingEnforcement enforceMultiplexing() {
        return multiplexing_enforcement;
    }

    public static MultiplexingEnforcement enforceMultiplexingStatic() {
        return multiplexing_enforcement_static;
    }

    public void enforceMultiplexing(MultiplexingEnforcement enforcement) {
        multiplexing_enforcement = enforcement;
        multiplexing_enforcement_static = enforcement;
    }

    public MaxScEnforcement enforceMaxSC() {
        return enforce_max_sc;
    }

    public void enforceMaxSC(MaxScEnforcement enforcement) {
        this.enforce_max_sc = enforcement;
    }

    public MaxScEnforcement enforceMaxScOutputRate() {
        return enforce_max_sc_output_rate;
    }

    public void enforceMaxScOutputRate(MaxScEnforcement enforcement) {
        enforce_max_sc_output_rate = enforcement;
    }

    public void defaultArrivalBoundMethods() {
        clearArrivalBoundMethods();
        arrival_bound_methods.add(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION);
    }

    public void clearArrivalBoundMethods() {
        arrival_bound_methods.clear();
    }

    public void setArrivalBoundMethod(ArrivalBoundMethod arrival_bound_method) {
        clearArrivalBoundMethods();
        arrival_bound_methods.add(arrival_bound_method);
    }

    public Set<ArrivalBoundMethod> arrivalBoundMethods() {
        return new HashSet<ArrivalBoundMethod>(arrival_bound_methods);
    }

    public void setArrivalBoundMethods(Set<ArrivalBoundMethod> arrival_bound_methods_set) {
        clearArrivalBoundMethods();
        arrival_bound_methods.addAll(arrival_bound_methods_set);
    }

    public void addArrivalBoundMethod(ArrivalBoundMethod arrival_bound_method) {
        arrival_bound_methods.add(arrival_bound_method);
    }

    public void addArrivalBoundMethods(Set<ArrivalBoundMethod> arrival_bound_methods_set) {
        arrival_bound_methods.addAll(arrival_bound_methods_set);
    }

    public boolean removeArrivalBoundMethod(ArrivalBoundMethod arrival_bound_method) {
        if (arrival_bound_methods.size() == 1) {
            return false;
        } else {
            return arrival_bound_methods.remove(arrival_bound_method);
        }
    }

    public boolean convolveAlternativeArrivalBounds() {
        return convolve_alternative_arrival_bounds;
    }
    
	public boolean useArrivalBoundsCache() {
		return arrival_bounds_caching;
	}
	
	public void setUseArrivalBoundsCache( boolean use_cache ) {
		arrival_bounds_caching = use_cache;
	}

    public void setConvolveAlternativeArrivalBounds(boolean convolve_alt_abs) {
        convolve_alternative_arrival_bounds = convolve_alt_abs;
    }

    public boolean serverBacklogArrivalBound() {
        return server_backlog_arrival_bound;
    }

    public void setServerBacklogArrivalBound(boolean server_backlog_arrival_bound) {
        this.server_backlog_arrival_bound = server_backlog_arrival_bound;
    }

    public boolean useFlowProlongation() {
        return flow_prolongation;
    }

    public void setUseFlowProlongation(boolean prolong_flows) {
        flow_prolongation = prolong_flows;
    }

    /**
     * Returns a deep copy of this analysis configuration.
     *
     * @return The copy.
     */
    public AnalysisConfig copy() { // deep copy as primitive data types are copied by value
        return new AnalysisConfig(multiplexing_enforcement, enforce_max_sc, enforce_max_sc_output_rate, 
					        		arrival_bound_methods,
					                convolve_alternative_arrival_bounds, arrival_bounds_caching,
					                server_backlog_arrival_bound);
    }

    @Override
	public int hashCode() {
		return Objects.hash(multiplexing_enforcement, enforce_max_sc, enforce_max_sc_output_rate, arrival_bound_methods,
				convolve_alternative_arrival_bounds, server_backlog_arrival_bound);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisConfig other = (AnalysisConfig) obj;

		return Objects.equals(this.multiplexing_enforcement, other.multiplexing_enforcement) &&
				Objects.equals(this.enforce_max_sc, other.enforce_max_sc) &&
				Objects.equals(this.enforce_max_sc_output_rate, other.enforce_max_sc_output_rate) &&
				Objects.equals(this.arrival_bound_methods, other.arrival_bound_methods) &&
				Objects.equals(this.convolve_alternative_arrival_bounds, other.convolve_alternative_arrival_bounds) &&
				Objects.equals(this.server_backlog_arrival_bound, other.server_backlog_arrival_bound);
	}

	@Override
    public String toString() {
        StringBuffer analysis_config_str = new StringBuffer();

        analysis_config_str.append(enforceMultiplexing().toString());
        
        analysis_config_str.append(", ");
        analysis_config_str.append("MaxSC ");
        analysis_config_str.append(enforceMaxSC());
        
        analysis_config_str.append(", ");
        analysis_config_str.append("MaxSC_output_rate ");
        analysis_config_str.append(enforceMaxScOutputRate());
        
        analysis_config_str.append(", ");
        analysis_config_str.append(arrivalBoundMethods().toString());
        
        if (serverBacklogArrivalBound()) {
            analysis_config_str.append(", ");
            analysis_config_str.append("cap_AB_by_backlog_bound");
        }

        if (convolveAlternativeArrivalBounds()) {
            analysis_config_str.append(", ");
            analysis_config_str.append("convolve_ABs");
        }

        if (useFlowProlongation()) {
            analysis_config_str.append(", ");
            analysis_config_str.append("flow_prolongation");
        }

        return analysis_config_str.toString();
    }
}
