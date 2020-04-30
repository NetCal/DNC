/*
 * This file is part of the Deterministic Network Calculator (DNC).
 *
 * Copyright (C) 2018 The DiscoDNC contributors
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
import org.networkcalculus.dnc.bounds.BoundingCurves;
import org.networkcalculus.dnc.bounds.Bounds;
import org.networkcalculus.dnc.curves.CurveFactory_Affine;
import org.networkcalculus.dnc.curves.CurveUtils;
import org.networkcalculus.dnc.curves.LinearSegment;

public interface AlgDncBackend {
	MinPlus getMinPlus();

	BoundingCurves getBoundingCurves();

	Bounds getBounds();
	
	CurveFactory_Affine getCurveFactory();
	
	CurveUtils getCurveUtils();
	
	LinearSegment.Builder getLinearSegmentFactory();

    @Override
    String toString();
    
    default String assembleString(String curve_backend_name, String alg_backend_name) {
    	StringBuffer name = new StringBuffer();

        name.append("CurveBackend");
        name.append(":");
        name.append(curve_backend_name);
        
        name.append(", ");

        name.append("AlgebraBackend");
        name.append(":");
        name.append(alg_backend_name);
    	
    	return name.toString();
    }
	
	default void checkDependencies() {
		
	}
}
