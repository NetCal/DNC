/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2016 Steffen Bondorf
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.curves.dnc;

import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.MaxServiceCurve;
import de.uni_kl.cs.disco.nc.CalculatorConfig;

public class MaxServiceCurve_DNC extends Curve_DNC implements MaxServiceCurve {
	// --------------------------------------------------------------------------------------------------------------
	// Constructors
	// --------------------------------------------------------------------------------------------------------------
	protected MaxServiceCurve_DNC() {
		super();
	}

	public MaxServiceCurve_DNC(int segment_count) {
		super(segment_count);
	}

	public MaxServiceCurve_DNC(CurvePwAffine curve) {
		copy(curve);

		if (CalculatorConfig.getInstance().exec_max_service_curve_checks() && !isWideSenseIncreasing()) { // too strong
																											// requirement:
																											// !isAlmostConcave()
																											// ) {
			throw new RuntimeException(
					"Maximum service curves can only be created from wide-sense increasing functions.");
		}

		forceThroughOrigin();
	}

	public MaxServiceCurve_DNC(String max_service_curve_str) throws Exception {
		if (max_service_curve_str == null || max_service_curve_str.isEmpty() || max_service_curve_str.length() < 9) { // Smallest
																														// possible
																														// string:
																														// {(0,0),0}
			throw new RuntimeException("Invalid string representation of a service curve.");
		}

		initializeCurve(max_service_curve_str);

		if (CalculatorConfig.getInstance().exec_max_service_curve_checks() && !isWideSenseIncreasing()) { // too strong
																											// requirement:
																											// !isAlmostConcave()
																											// ) {
			throw new RuntimeException(
					"Maximum service curves can only be created from wide-sense increasing functions.");
		}

		forceThroughOrigin();
	}

	// --------------------------------------------------------------------------------------------------------------
	// Interface Implementations
	// --------------------------------------------------------------------------------------------------------------
	@Override
	public MaxServiceCurve_DNC copy() {
		MaxServiceCurve_DNC msc_copy = new MaxServiceCurve_DNC();
		msc_copy.copy(this);

		return msc_copy;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MaxServiceCurve_DNC) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		return "MSC".hashCode() * super.hashCode();
	}

	/**
	 * Returns a string representation of this curve.
	 *
	 * @return the curve represented as a string.
	 */
	@Override
	public String toString() {
		return "MSC" + super.toString();
	}
}
