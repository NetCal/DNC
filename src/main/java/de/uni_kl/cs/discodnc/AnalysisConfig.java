/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://discodnc.cs.uni-kl.de
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

package de.uni_kl.cs.discodnc;

import java.util.Collections;
import java.util.HashSet;
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
    
    /**
     * Whether to use maximum service curves in output bound computation
     */
    private MaxScEnforcement enforce_max_sc = MaxScEnforcement.SERVER_LOCAL;
    
    /**
     * Whether to constrain the output bound further through convolution with the
     * maximum service curve's rate as the server cannot output data faster than
     * this rate.
     */
    private MaxScEnforcement enforce_max_sc_output_rate = MaxScEnforcement.SERVER_LOCAL;
    private Set<ArrivalBoundMethod> arrival_bound_methods = new HashSet<ArrivalBoundMethod>(
            Collections.singleton(ArrivalBoundMethod.AGGR_PBOO_CONCATENATION));
    private boolean remove_duplicate_arrival_bounds = true;
    private boolean flow_prolongation = false;
    private boolean server_backlog_arrival_bound = false;
    
    public AnalysisConfig() {
    }
    
    public AnalysisConfig(MultiplexingEnforcement multiplexing_enforcement, MaxScEnforcement enforce_max_sc, MaxScEnforcement enforce_max_sc_output_rate,
                          Set<ArrivalBoundMethod> arrival_bound_methods, boolean remove_duplicate_arrival_bounds,
                          boolean server_backlog_arrival_bound) {
        this.multiplexing_enforcement = multiplexing_enforcement;
        this.enforce_max_sc = enforce_max_sc;
        this.enforce_max_sc_output_rate = enforce_max_sc_output_rate;
        this.arrival_bound_methods.clear();
        this.arrival_bound_methods.addAll(arrival_bound_methods);
        this.remove_duplicate_arrival_bounds = remove_duplicate_arrival_bounds;
        this.server_backlog_arrival_bound = server_backlog_arrival_bound;
    }

    public MultiplexingEnforcement enforceMultiplexing() {
        return multiplexing_enforcement;
    }

    public void enforceMultiplexing(MultiplexingEnforcement enforcement) {
        multiplexing_enforcement = enforcement;
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

    public boolean removeDuplicateArrivalBounds() {
        return remove_duplicate_arrival_bounds;
    }

    public void setRemoveDuplicateArrivalBounds(boolean remove_duplicate_arrival_bounds_flag) {
        remove_duplicate_arrival_bounds = remove_duplicate_arrival_bounds_flag;
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
        return new AnalysisConfig(multiplexing_enforcement, enforce_max_sc, enforce_max_sc_output_rate, arrival_bound_methods,
                remove_duplicate_arrival_bounds, server_backlog_arrival_bound);
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

        if (removeDuplicateArrivalBounds()) {
            analysis_config_str.append(", ");
            analysis_config_str.append("remove duplicate ABs");
        }

        return analysis_config_str.toString();
    }
}
