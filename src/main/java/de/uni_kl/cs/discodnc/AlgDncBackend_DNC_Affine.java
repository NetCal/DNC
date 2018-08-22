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

import de.uni_kl.cs.discodnc.algebra.MinPlus;
import de.uni_kl.cs.discodnc.algebra.disco.affine.MinPlus_DNC_Affine;
import de.uni_kl.cs.discodnc.curves.Curve;
import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.curves.disco.LinearSegment_DNC;
import de.uni_kl.cs.discodnc.curves.disco.affine.Curve_DNC_Affine;

public enum AlgDncBackend_DNC_Affine implements AlgDncBackend {
	DISCO_AFFINE;

	@Override
	public MinPlus getMinPlus() {
		return MinPlus_DNC_Affine.MINPLUS_DISCO_AFFINE;
	}

	@Override
	public Curve getCurveFactory() {
		return Curve_DNC_Affine.getFactory();
	}

	@Override
	public LinearSegment.Builder getLinearSegmentFactory() {
		return LinearSegment_DNC.getBuilder();
	}

    @Override
    public String toString() {
        return assembleString(this.name(), MinPlus_DNC_Affine.MINPLUS_DISCO_AFFINE.name());
    }
}
