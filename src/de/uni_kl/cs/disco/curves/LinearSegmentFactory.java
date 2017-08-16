/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta2 "Chimera".
 *
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

package de.uni_kl.cs.disco.curves;

import de.uni_kl.cs.disco.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.LinearSegment_MPARTC_PwAffine;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;

/**
 * 
 * This class creates linear segments, either with Disco's DNC backend or with the PMA's RTC backend.
 *
 */
public class LinearSegmentFactory {
	// In contrast to the similar Curve factory, it does not hold a curve factory instance for each alternative.
	
    public static LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
        switch (CalculatorConfig.getInstance().getCurveClass()) {
            case MPA_RTC:
                return new LinearSegment_MPARTC_PwAffine(x.doubleValue(), y.doubleValue(), grad.doubleValue());
            case DNC:
            default:
                return new LinearSegment_DNC(x, y, grad, leftopen);
        }
    }

    public static LinearSegment createZeroSegment() {
        switch (CalculatorConfig.getInstance().getCurveClass()) {
            case MPA_RTC:
                return createHorizontalLine(0.0);
            case DNC:
            default:
                return createHorizontalLine(0.0);
        }
    }

    public static LinearSegment createHorizontalLine(double y) {
        switch (CalculatorConfig.getInstance().getCurveClass()) {
            case MPA_RTC:
                return new LinearSegment_MPARTC_PwAffine(0.0, 0.0, 0.0);
            case DNC:
            default:
                return new LinearSegment_DNC(NumFactory.createZero(), NumFactory.createZero(), NumFactory.createZero(), false);
        }
    }
}
