/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
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

package org.networkcalculus.dnc.bounds.disco;

public final class BoundingCurves_Disco_Configuration {
	private static BoundingCurves_Disco_Configuration instance = new BoundingCurves_Disco_Configuration();

	public static BoundingCurves_Disco_Configuration getInstance() {
		return instance;
	}
	
	private boolean FIFO_MUX_CHECKS = false;

	public boolean exec_fifo_mux_checks() {
		return FIFO_MUX_CHECKS;
	}

	@Override
	public String toString() {
		if(FIFO_MUX_CHECKS) {
			return "FIFO checks";
		} else {
			return "All bound-operation checks are disabled";
		}
	}
}
