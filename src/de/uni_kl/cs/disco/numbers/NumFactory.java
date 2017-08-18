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
 * GNU Lesser General  License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General  License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

package de.uni_kl.cs.disco.numbers;

import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.rational.NaN;
import de.uni_kl.cs.disco.numbers.rational.NegativeInfinity;
import de.uni_kl.cs.disco.numbers.rational.PositiveInfinity;
import de.uni_kl.cs.disco.numbers.rational.BigIntFactory;
import de.uni_kl.cs.disco.numbers.rational.IntFactory;
import de.uni_kl.cs.disco.numbers.real.DoublePrecisionFactory;
import de.uni_kl.cs.disco.numbers.real.SinglePrecisionFactory;

public interface NumFactory {
	final Num NaN = new NaN();
	final Num NEGATIVE_INFINITY = new NegativeInfinity();
	final Num POSITIVE_INFINITY = new PositiveInfinity();
	
	public static NumFactory getNumFactory() {
        switch (CalculatorConfig.getInstance().getNumClass()) {
            case REAL_SINGLE_PRECISION:
            		return SinglePrecisionFactory.getInstance();
            case RATIONAL_INTEGER:
            		return IntFactory.getInstance();
            case RATIONAL_BIGINTEGER:
            		return BigIntFactory.getInstance();
            case REAL_DOUBLE_PRECISION:
            default:
            		return DoublePrecisionFactory.getInstance();
        }
    }
	
    Num getPositiveInfinity();

    Num createPositiveInfinity();

    Num getNegativeInfinity();

    Num createNegativeInfinity();

    Num getNaN();

    Num createNaN();

    Num getZero();

    Num createZero();

    Num getEpsilon();

    Num createEpsilon();

    Num create(double value);

    Num create(int num, int den);

    Num create(String num_str) throws Exception;
}
