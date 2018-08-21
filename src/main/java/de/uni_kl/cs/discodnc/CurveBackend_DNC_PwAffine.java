/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2018+ The DiscoDNC contributors
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

import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.curves.dnc.pwaffine.Curve_DNC_PwAffine;
import de.uni_kl.cs.discodnc.minplus.MinPlus;
import de.uni_kl.cs.discodnc.minplus.dnc.pwaffine.MinPlus_DNC_PwAffine;

public enum CurveBackend_DNC_PwAffine implements CurveBackend {
	DNC_PWAFFINE;

	@Override
	public MinPlus getMinPlus() {
		return MinPlus_DNC_PwAffine.MINPLUS_DNC_PWAFFINE;
	}

	@Override
	public Curve getCurveFactory() {
		return Curve_DNC_PwAffine.getFactory();
	}

	@Override
	public LinearSegment.Builder getLinearSegmentFactory() {
		return LinearSegment_DNC.getBuilder();
	}

    @Override
    public String toString() {
        return assembleString(this.name(), MinPlus_DNC_PwAffine.MINPLUS_DNC_PWAFFINE.name());
    }
}
