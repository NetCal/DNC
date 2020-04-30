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

import org.networkcalculus.dnc.algebra.MinPlus;
import org.networkcalculus.dnc.curves.CurveFactory_Affine;
import org.networkcalculus.dnc.curves.CurveUtils;
import org.networkcalculus.num.NumBackend;

/**
 * This class contains configuration settings for the calculator 
 * that cannot be changed at runtime without risking to cause an exception.
 * For example, the different number and curve backends are incompatible with each other.
 */
public final class Calculator {
	private static Calculator instance = new Calculator();
	
	private NumBackend NUM_BACKEND = NumBackend.REAL_DOUBLE_PRECISION;
	
	private AlgDncBackend DNC_BACKEND = AlgDncBackend_DNC_ConPwAffine.DISCO_CONPWAFFINE;

	protected Calculator() {
	}

	public static Calculator getInstance() {
		return instance;
	}

	public NumBackend getNumBackend() {
		return NUM_BACKEND;
	}

	public boolean setNumBackend(NumBackend num_backend) {
		if (NUM_BACKEND == num_backend) {
			return false;
		} else {
			NUM_BACKEND = num_backend;
			return true;
		}
	}

	public AlgDncBackend getDncBackend() {
		return DNC_BACKEND;
	}
	
	public CurveFactory_Affine getCurveFactory() {
		return DNC_BACKEND.getCurveFactory();
	}
	
	public CurveUtils getCurveUtils() {
		return DNC_BACKEND.getCurveUtils();
	}
	
	public MinPlus getMinPlus() {
		return DNC_BACKEND.getMinPlus();
	}

	private void checkDependencies() {
		DNC_BACKEND.checkDependencies();
	}
	
	public boolean setCurveBackend(AlgDncBackend alg_dnc_backend) {
		checkDependencies();

		if (DNC_BACKEND == alg_dnc_backend) {
			return false;
		}
		DNC_BACKEND = alg_dnc_backend;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer calculator_config_str = new StringBuffer();

		calculator_config_str.append(getNumBackend().toString());
		calculator_config_str.append(", ");
		
		calculator_config_str.append(getDncBackend().toString());

		return calculator_config_str.toString();
	}
}
