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
import de.uni_kl.cs.disco.numbers.implementations.RationalBigIntFactory;
import de.uni_kl.cs.disco.numbers.implementations.RationalIntFactory;
import de.uni_kl.cs.disco.numbers.implementations.RealDoubleFactory;
import de.uni_kl.cs.disco.numbers.implementations.RealSingleFactory;

public interface NumFactoryDispatch {
    public static NumFactory getNumFactory() {
        switch (CalculatorConfig.getInstance().getNumClass()) {
            case REAL_SINGLE_PRECISION:
            		return RealSingleFactory.getInstance();
            case RATIONAL_INTEGER:
            		return RationalIntFactory.getInstance();
            case RATIONAL_BIGINTEGER:
            		return RationalBigIntFactory.getInstance();
            case REAL_DOUBLE_PRECISION:
            default:
            		return RealDoubleFactory.getInstance();
        }
    }

    public static Num getPositiveInfinity() {
        return getNumFactory().getPositiveInfinity();
    }

    public static Num createPositiveInfinity() {
        return getNumFactory().createPositiveInfinity();
    }

    public static Num getNegativeInfinity() {
        return getNumFactory().getNegativeInfinity();
    }

    public static Num createNegativeInfinity() {
        return getNumFactory().createNegativeInfinity();
    }

    public static Num getNaN() {
        return getNumFactory().getNaN();
    }

    public static Num createNaN() {
        return getNumFactory().createNaN();
    }

    public static Num getZero() {
        return getNumFactory().getZero();
    }

    public static Num createZero() {
        return getNumFactory().createZero();
    }

    public static Num getEpsilon() {
        return getNumFactory().getEpsilon();
    }

    public static Num createEpsilon() {
        return getNumFactory().createEpsilon();
    }

    public static Num create(double value) {
        return getNumFactory().create(value);
    }

    public static Num create(int num, int den) {
        return getNumFactory().create(num, den);
    }

    public static Num create(String num_str) throws Exception {
        return getNumFactory().create(num_str);
    }
}
