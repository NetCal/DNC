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
import de.uni_kl.cs.disco.numbers.implementations.RationalBigInt;
import de.uni_kl.cs.disco.numbers.implementations.RationalInt;
import de.uni_kl.cs.disco.numbers.implementations.RealDoublePrecision;
import de.uni_kl.cs.disco.numbers.implementations.RealSinglePrecision;
import de.uni_kl.cs.disco.numbers.values.NaN;
import de.uni_kl.cs.disco.numbers.values.NegativeInfinity;
import de.uni_kl.cs.disco.numbers.values.PositiveInfinity;

public interface Num {
	// --------------------------------------------------------------------------------------------------------------
	// Num Interface
	// --------------------------------------------------------------------------------------------------------------
	
	double doubleValue();

	boolean eq(double num);

	boolean eqZero();

	boolean gt(Num num);

	boolean gtZero();

	boolean geq(Num num);

	boolean geqZero();

	boolean lt(Num num);

	boolean ltZero();

	boolean leq(Num num);

	boolean leqZero();

	boolean isFinite();

	boolean isInfinite();

	boolean isNaN();

	Num copy();

	// --------------------------------------------------------------------------------------------------------------
	// Factory Interface
	// --------------------------------------------------------------------------------------------------------------
	
	final Num NaN = new NaN();
	final Num NEGATIVE_INFINITY = new NegativeInfinity();
	final Num POSITIVE_INFINITY = new PositiveInfinity();

	public static Num getFactory() {
		switch (CalculatorConfig.getInstance().getNumImpl()) {
		case REAL_SINGLE_PRECISION:
			return RealSinglePrecision.getInstance();
		case RATIONAL_INTEGER:
			return RationalInt.getInstance();
		case RATIONAL_BIGINTEGER:
			return RationalBigInt.getInstance();
		case REAL_DOUBLE_PRECISION:
		default:
			return RealDoublePrecision.getInstance();
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

	Num create(int num);
	
	Num create(double value);

	Num create(int num, int den);

	Num create(String num_str) throws Exception;

	// --------------------------------------------------------------------------------------------------------------
	// Utils Interface
	// --------------------------------------------------------------------------------------------------------------

	public static Num getUtils() {
		switch (CalculatorConfig.getInstance().getNumImpl()) {
		case REAL_SINGLE_PRECISION:
			return RealSinglePrecision.getInstance();
		case RATIONAL_INTEGER:
			return RationalInt.getInstance();
		case RATIONAL_BIGINTEGER:
			return RationalBigInt.getInstance();
		case REAL_DOUBLE_PRECISION:
		default:
			return RealDoublePrecision.getInstance();
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
