/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
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

package de.uni_kl.cs.disco.network;

import de.uni_kl.cs.disco.curves.CurvePwAffineFactory;
import de.uni_kl.cs.disco.curves.CurvePwAffineUtils;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.nc.AnalysisConfig;

public class Server {
	private int id;
	private String alias;

	private ServiceCurve service_curve = CurvePwAffineFactory.getCurveFactory().createZeroService();
	/**
	 * A zero delay burst curve lets the influence of the maximum service curve
	 * vanish
	 */
	private MaxServiceCurve max_service_curve = CurvePwAffineFactory.getCurveFactory().createZeroDelayInfiniteBurstMSC();

	private boolean max_service_curve_flag = false;

	/**
	 * Whether to use maximum service curves in output bound computation
	 */
	private boolean use_gamma = false;

	/**
	 * Whether to constrain the output bound further through convolution with the
	 * maximum service curve's rate as the server cannot output data faster than
	 * this rate.
	 */
	private boolean use_extra_gamma = false;

	private AnalysisConfig.Multiplexing multiplexing = AnalysisConfig.Multiplexing.ARBITRARY;

	@SuppressWarnings("unused")
	private Server() {
	}

	/**
	 * @param id
	 *            The server's id (unique).
	 * @param alias
	 *            The server's alias (not necessarily unique).
	 * @param service_curve
	 *            The server's service curve.
	 * @param max_service_curve
	 *            The server's maximum service curve.
	 * @param multiplexing
	 *            The server's flow multiplexing discipline.
	 * @param use_gamma
	 *            Convolve the maximum service curve with the arrival curve before
	 *            deriving an output bound.
	 * @param use_extra_gamma
	 *            Convolve the output bound with the maximum service curve.
	 */
	protected Server(int id, String alias, ServiceCurve service_curve, MaxServiceCurve max_service_curve,
			AnalysisConfig.Multiplexing multiplexing, boolean use_gamma, boolean use_extra_gamma) {
		this.id = id;
		this.alias = alias;
		this.service_curve = service_curve;
		this.max_service_curve = max_service_curve;
		max_service_curve_flag = true;
		this.multiplexing = multiplexing;
		this.use_gamma = use_gamma;
		this.use_extra_gamma = use_extra_gamma;
	}

	protected Server(int id, String alias, ServiceCurve service_curve, AnalysisConfig.Multiplexing multiplexing) {
		this.id = id;
		this.alias = alias;
		this.service_curve = service_curve;
		this.multiplexing = multiplexing;
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
	 * Setting a maximum service curve also enables useGamma and useExtraGamma.
	 *
	 * @param max_service_curve
	 *            The maximum service curve.
	 * @return Signals success of the operation.
	 */
	public boolean setMaxServiceCurve(MaxServiceCurve max_service_curve) {
		return setMaxServiceCurve(max_service_curve, true, true);
	}

	public boolean setMaxServiceCurve(MaxServiceCurve max_service_curve, boolean use_gamma, boolean use_extra_gamma) {
		this.max_service_curve = max_service_curve;

		max_service_curve_flag = true;
		this.use_gamma = true;
		this.use_extra_gamma = true;

		return true;
	}

	public boolean removeMaxServiceCurve() {
		max_service_curve = CurvePwAffineFactory.getCurveFactory().createZeroDelayInfiniteBurstMSC();

		max_service_curve_flag = false;
		use_gamma = false;
		use_extra_gamma = false;

		return true;
	}

	/**
	 * @return A copy of the maximum service curve
	 */
	public MaxServiceCurve getMaxServiceCurve() {
		return max_service_curve; // No need to check max_service_curve_set as it will always be at least a zero
									// delay infinite burst curve
	}

	/**
	 * In contrast to <code>getMaxServiceCurve()</code> this function always returns
	 * the default zero delay burst curve if the useGamma flag is not set.
	 *
	 * @return The gamma curve
	 */
	public MaxServiceCurve getGamma() {
		if (use_gamma == false) {
			return CurvePwAffineFactory.getCurveFactory().createZeroDelayInfiniteBurstMSC();
		} else {
			return max_service_curve;
		}
	}

	/**
	 * In contrast to <code>getMaxServiceCurve()</code> this function always returns
	 * the default zero delay burst curve if the useExtraGamma flag is not set.
	 *
	 * @return The gamma curve
	 */
	public MaxServiceCurve getExtraGamma() {
		if (use_extra_gamma == false) {
			return CurvePwAffineFactory.getCurveFactory().createZeroDelayInfiniteBurstMSC();
		} else {
			return (MaxServiceCurve) CurvePwAffineUtils.removeLatency(max_service_curve);
		}
	}

	public boolean useGamma() {
		return use_gamma;
	}

	public void setUseGamma(boolean use_gamma) {
		this.use_gamma = use_gamma;
	}

	public boolean useExtraGamma() {
		return use_extra_gamma;
	}

	public void setUseExtraGamma(boolean use_extra_gamma) {
		this.use_extra_gamma = use_extra_gamma;
	}

	public AnalysisConfig.Multiplexing multiplexingDiscipline() {
		return multiplexing;
	}

	public void setMultiplexingDiscipline(AnalysisConfig.Multiplexing mux) {
		multiplexing = mux;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String toShortString() {
		return alias;
	}

	@Override
	public String toString() {
		String result_str = "Server(" + alias + ", " + multiplexing.toString() + ", " + service_curve.toString();

		if (max_service_curve_flag) {
			result_str += ", " + max_service_curve.toString();
			result_str += ", " + Boolean.toString(use_gamma);
			result_str += ", " + Boolean.toString(use_extra_gamma);
		}

		result_str += ")";

		return result_str;
	}

	public String toExtendedString() {
		return "Server(" + alias + ", " + multiplexing.toString() + ", " + service_curve.toString() + ", "
				+ max_service_curve.toString() + ", " + Boolean.toString(use_gamma) + ", "
				+ Boolean.toString(use_extra_gamma) + ")";
	}
}
