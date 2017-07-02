/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera"
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package de.uni_kl.disco.curves;

import de.uni_kl.disco.curves.dnc.LinearSegment_DNC;
import de.uni_kl.disco.curves.mpa_rtc_pwaffine.LinearSegment_MPARTC_PwAffine;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

public class LinearSegmentFactory {

    // This is a small workaround. In case that more methods are added here, it may be useful to split up this class
    // and implement a factory like CurveFactroy.

    public static LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
        switch (CalculatorConfig.getCurveClass()) {
            case MPA_RTC:
                return new LinearSegment_MPARTC_PwAffine(x.doubleValue(), y.doubleValue(), grad.doubleValue());
            case DNC:
            default:
                return new LinearSegment_DNC(x, y, grad, leftopen);
        }
    }

    public static LinearSegment createZeroSegment() {
        switch (CalculatorConfig.getCurveClass()) {
            case MPA_RTC:
                return createHorizontalLine(0.0);
            case DNC:
            default:
                return createHorizontalLine(0.0);
        }
    }

    public static LinearSegment createHorizontalLine(double y) {
        switch (CalculatorConfig.getCurveClass()) {
            case MPA_RTC:
                return new LinearSegment_MPARTC_PwAffine(0.0, 0.0, 0.0);
            case DNC:
            default:
                return new LinearSegment_DNC(NumFactory.createZero(), NumFactory.createZero(), NumFactory.createZero(), false);
        }
    }
}
