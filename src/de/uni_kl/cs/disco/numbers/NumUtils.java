/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2014 - 2017 Steffen Bondorf
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

package de.uni_kl.cs.disco.numbers;

import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.implementations.RealDoubleUtils;
import de.uni_kl.cs.disco.numbers.implementations.RealSingleUtils;
import de.uni_kl.cs.disco.numbers.implementations.RationalIntUtils;
import de.uni_kl.cs.disco.numbers.implementations.RationalBigIntUtils;

public interface NumUtils {

	public static NumUtils getNumUtils() {
        switch (CalculatorConfig.getInstance().getNumClass()) {
            case REAL_SINGLE_PRECISION:
            		return RealSingleUtils.getInstance();
            case RATIONAL_INTEGER:
            		return RationalIntUtils.getInstance();
            case RATIONAL_BIGINTEGER:
            		return RationalBigIntUtils.getInstance();
            case REAL_DOUBLE_PRECISION:
            default:
            		return RealDoubleUtils.getInstance();
        }
    }
	
    Num add(Num num1, Num num2);

    Num sub(Num num1, Num num2);

    Num mult(Num num1, Num num2);

    Num div(Num num1, Num num2);

    Num abs(Num num);

    Num diff(Num num1, Num num2);

    Num max(Num num1, Num num2);

    Num min(Num num1, Num num2);

    Num negate(Num num);

    boolean isFinite(Num num);

    boolean isInfinite(Num num);

    boolean isNaN(Num num);
}