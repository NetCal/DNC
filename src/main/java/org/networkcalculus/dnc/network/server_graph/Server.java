/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
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

package org.networkcalculus.dnc.network.server_graph;

import org.networkcalculus.dnc.AnalysisConfig.Multiplexing;
import org.networkcalculus.dnc.curves.Curve;
import org.networkcalculus.dnc.curves.Curve_ConstantPool;
import org.networkcalculus.dnc.curves.MaxServiceCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;

public class Server {
    private int id;
    private String alias;

    private ServiceCurve service_curve = Curve_ConstantPool.ZERO_SERVICE_CURVE.get();
    /**
     * A zero delay burst curve lets the influence of the maximum service curve
     * vanish
     */
    private MaxServiceCurve max_service_curve = Curve.getFactory().createZeroDelayInfiniteBurstMSC();

    private boolean max_service_curve_flag = false;

    /**
     * Whether to use maximum service curves in output bound computation
     */
    private boolean use_max_sc = false;

    /**
     * Whether to constrain the output bound further through convolution with the
     * maximum service curve's rate as the server cannot output data faster than
     * this rate.
     */
    private boolean use_max_sc_output_rate = false;

    private Multiplexing multiplexing = Multiplexing.ARBITRARY;

    private Server() {
    }

    /**
     * @param id						The server's id (unique).
     * @param alias						The server's alias (not necessarily unique).
     * @param service_curve				The server's service curve.
     * @param max_service_curve			The server's maximum service curve.
     * @param multiplexing				The server's flow multiplexing setting.
     * @param use_max_sc				Convolve the maximum service curve with the arrival curve before
     *                          		deriving an output bound.
     * @param use_max_sc_output_rate	Convolve the output bound with the maximum service curve.
     */
    protected Server(int id, String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve,
                     Multiplexing multiplexing, boolean use_max_sc, boolean use_max_sc_output_rate) {
        this.id = id;
        this.alias = alias;
        this.service_curve = service_curve;
        this.max_service_curve = max_service_curve;
        max_service_curve_flag = true;
        this.multiplexing = multiplexing;
        this.use_max_sc = use_max_sc;
        this.use_max_sc_output_rate = use_max_sc_output_rate;
    }

    protected Server(int id, String alias, ServiceCurve service_curve, Multiplexing multiplexing) {
        this.id = id;
        this.alias = alias;
        this.service_curve = service_curve;
        this.multiplexing = multiplexing;
    }
    
    // Required by the ULP optimization.
    public static Server createExplicitSourceServer() {
 		Server explicit_src = new Server();

 		explicit_src.id = -1;
 		explicit_src.alias = "sSRC";
 		explicit_src.service_curve = Curve.getFactory().createZeroDelayInfiniteBurst();
 		explicit_src.max_service_curve = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
 		explicit_src.max_service_curve_flag = false;
 		explicit_src.multiplexing = Multiplexing.ARBITRARY;
 		explicit_src.useMaxSC(false);
 		explicit_src.useMaxScRate(false);
 		
 		return explicit_src;
 	}

    // Required by the ULP optimization.
 	public static Server createExplicitSinkServer() {
 		Server explicit_snk = new Server();

 		explicit_snk.id = -2;
 		explicit_snk.alias = "sSNK"; // Set to get String representation t{} in the single LP optimization
 		explicit_snk.service_curve = Curve.getFactory().createZeroDelayInfiniteBurst();
 		explicit_snk.max_service_curve = Curve.getFactory().createZeroDelayInfiniteBurstMSC();
 		explicit_snk.max_service_curve_flag = false;
 		explicit_snk.multiplexing = Multiplexing.ARBITRARY;
 		explicit_snk.useMaxSC(false);
 		explicit_snk.useMaxScRate(false);
 		
 		return explicit_snk;
 	}

    public int getId() {
        return id;
    }

    public boolean setServiceCurve(ServiceCurve service_curve) {
        this.service_curve = service_curve;
        return true;
    }

    /**
     * @return A copy of the service curve
     */
    public ServiceCurve getServiceCurve() {
        return service_curve.copy();
    }

    /**
     * Setting a maximum service curve also enables useMaxSC and useMaxScRate.
     *
     * @param max_service_curve The maximum service curve.
     * @return Signals success of the operation.
     */
    public boolean setMaxServiceCurve(MaxServiceCurve max_service_curve) {
        return setMaxServiceCurve(max_service_curve, true, true);
    }

    public boolean setMaxServiceCurve(MaxServiceCurve max_service_curve, boolean use_max_sc, boolean use_max_sc_output_rate) {
        this.max_service_curve = max_service_curve;

        max_service_curve_flag = true;
        this.use_max_sc = use_max_sc;
        this.use_max_sc_output_rate = use_max_sc_output_rate;

        return true;
    }

    public boolean removeMaxServiceCurve() {
        max_service_curve = Curve.getFactory().createZeroDelayInfiniteBurstMSC();

        max_service_curve_flag = false;
        use_max_sc = false;
        use_max_sc_output_rate = false;

        return true;
    }

    /**
     * This function always returns the default zero delay burst curve if the useMaxSC flag is not set.
     * 
     * @return A copy of the maximum service curve
     */
    public MaxServiceCurve getMaxServiceCurve() {
        if (use_max_sc == false) {
            return Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        } else {
            return max_service_curve;
        }
    }

    /**
     * @return The stored maximum service curve
     */
    public MaxServiceCurve getStoredMaxSC() {
    	return max_service_curve;
    }

    /**
     * In contrast to <code>getMaxServiceCurve()</code> this function always returns
     * the default zero delay burst curve if the useMaxScRate flag is not set.
     *
     * @return The maximum service curve
     */
    public MaxServiceCurve getMaxScRate() {
        if (use_max_sc_output_rate == false) {
            return Curve.getFactory().createZeroDelayInfiniteBurstMSC();
        } else {
            return (MaxServiceCurve) Curve.getUtils().removeLatency(max_service_curve);
        }
    }

    /**
     * @return The stored maximum service curve rate
     */
    public MaxServiceCurve getStoredMaxScRate() {
    	return (MaxServiceCurve) Curve.getUtils().removeLatency(max_service_curve);
    }

    public boolean useMaxSC() {
        return use_max_sc;
    }

    public void useMaxSC(boolean use_max_sc) {
        this.use_max_sc = use_max_sc;
    }

    public boolean useMaxScRate() {
        return use_max_sc_output_rate;
    }

    public void useMaxScRate(boolean use_max_sc_output_rate) {
        this.use_max_sc_output_rate = use_max_sc_output_rate;
    }

    public Multiplexing multiplexing() {
        return multiplexing;
    }

    public void setMultiplexing(Multiplexing multiplexing) {
        this.multiplexing = multiplexing;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // String Conversions
    // --------------------------------------------------------------------------------------------------------------
    
    private StringBuffer commonStringPrefix() {
    	StringBuffer server_str_prefix = new StringBuffer();

     	server_str_prefix.append("Server(");
     	server_str_prefix.append(alias);
     	server_str_prefix.append(", ");
     	server_str_prefix.append(Integer.toString(id));
     	
     	return server_str_prefix;
    }

    public String toShortString() {
    	StringBuffer server_str = commonStringPrefix();
    	
    	server_str.append(")");
    	
        return server_str.toString();
    }
    
    private StringBuffer commonStringPrefix2() {
    	StringBuffer server_str_prefix = commonStringPrefix();

    	server_str_prefix.append(", ");
    	server_str_prefix.append(multiplexing.toString());
    	server_str_prefix.append(", ");
    	server_str_prefix.append(service_curve.toString());
     	
     	return server_str_prefix;
    }
    
    private StringBuffer commonMaxScString() {
    	StringBuffer server_msc_str = new StringBuffer();
    	
    	server_msc_str.append(max_service_curve.toString());
    	server_msc_str.append(", ");
    	server_msc_str.append(Boolean.toString(use_max_sc));
    	server_msc_str.append(", ");
    	server_msc_str.append(Boolean.toString(use_max_sc_output_rate));
    	
    	return server_msc_str;
    }

    @Override
    public String toString() {
        StringBuffer server_str = commonStringPrefix2();
    	
    	if (max_service_curve_flag) {
    		server_str.append(", ");
        	server_str.append(commonMaxScString());
        }
    	
    	server_str.append(")");
    	
    	return server_str.toString();
    }

    public String toExtendedString() {
        StringBuffer server_str = commonStringPrefix2();

        server_str.append(", ");
    	server_str.append(commonMaxScString());
    	
    	server_str.append(")");
    	
    	return server_str.toString();
    }
}
