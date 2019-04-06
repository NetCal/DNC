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

import org.networkcalculus.dnc.bounds.Bounds;
import org.networkcalculus.dnc.bounds.disco.pw_affine.Backlog_Disco_PwAffine;
import org.networkcalculus.dnc.bounds.disco.pw_affine.Delay_Disco_PwAffine;
import org.networkcalculus.dnc.curves.ArrivalCurve;
import org.networkcalculus.dnc.curves.ServiceCurve;
import org.networkcalculus.num.Num;

public enum Bounds_Disco_PwAffine implements Bounds {
	BOUNDS_DISCO_PWAFFINE;
	
    // --------------------------------------------------------------------------------------------------------------
    // Backlog
    // --------------------------------------------------------------------------------------------------------------

    public Num backlog(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        return Backlog_Disco_PwAffine.derive(arrival_curve,service_curve);
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // Delay
    // --------------------------------------------------------------------------------------------------------------

    public Num delayARB(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        return Delay_Disco_PwAffine.deriveARB(arrival_curve, service_curve);
    }

    public Num delayFIFO(ArrivalCurve arrival_curve, ServiceCurve service_curve) {
        return Delay_Disco_PwAffine.deriveFIFO(arrival_curve, service_curve);
    }
}
