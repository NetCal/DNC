/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2016 - 2017 Steffen Bondorf
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

package de.uni_kl.cs.disco.numbers.rational;

import org.apache.commons.math3.fraction.BigFraction;

import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.real.DoublePrecision;

import java.math.BigInteger;

/**
 * Wrapper class around org.apache.commons.math3.BigFraction.BigFraction
 * introducing special values like positive / negative infinity and NaN as well
 * as operators like min, max, ==, &gt;, &gt;=, &lt;, and &lt;= that are not
 * part of BigFraction but needed by the network calculator.
 * <p>
 * For the ease of converting from the primitive data type double to BigFraction
 * objects, copy by value semantic are is applied.
 *
 */
public class BigInt implements Num {
	// Unfortunately you cannot give the constructor the double value 0.0000001
	private static final BigFraction EPSILON = new BigFraction(1, 1000000);
	private static final BigFraction ZERO_BIGFRACTION = new BigFraction(0);
	private BigFraction value = new BigFraction(0.0);

	private BigInt() {
	}

	public BigInt(double value) {
		this.value = new BigFraction(value);
	}

	public BigInt(int num) {
		value = new BigFraction(num);
	}

	public BigInt(int num, int den) {
		value = new BigFraction(num, den);
	}

	public BigInt(BigInteger num, BigInteger den) {
		value = new BigFraction(num, den);
	}

	public BigInt(BigInt num) {
		value = new BigFraction(num.value.getNumerator(), num.value.getDenominator());
	}

	private BigInt(BigFraction frac) {
		value = new BigFraction(frac.getNumerator(), frac.getDenominator());
	}

	public static BigInt createZero() {
		BigInt zero = new BigInt();
		zero.instantiateZero();
		return zero;
	}

	public static BigInt createEpsilon() {
		return new BigInt(EPSILON);
	}

	public static BigInt add(BigInt num1, BigInt num2) {
		// May still throw MathArithmeticException due to integer overflow
		return new BigInt(num1.value.add(num2.value));
	}

	public static BigInt sub(BigInt num1, BigInt num2) {
		// May still throw MathArithmeticException due to integer overflow
		return new BigInt(num1.value.subtract(num2.value));
	}

	public static BigInt mult(BigInt num1, BigInt num2) {
		// May throw MathArithmeticException due to integer overflow
		return new BigInt(num1.value.multiply(num2.value));
	}

	public static BigInt div(BigInt num1, BigInt num2) {
		return new BigInt(num1.value.divide(num2.value));
	}

	public static BigInt diff(BigInt num1, BigInt num2) {
		return sub(max(num1, num2), min(num1, num2));
	}

	public static BigInt max(BigInt num1, BigInt num2) {
		if (num1.value.compareTo(num2.value) >= 0) {
			return num1;
		} else {
			return num2;
		}
	}

	public static BigInt min(BigInt num1, BigInt num2) {
		if (num1.value.compareTo(num2.value) <= 0) {
			return num1;
		} else {
			return num2;
		}
	}

	public static BigInt abs(BigInt num) {
		return new BigInt(num.value.abs());
	}

	public static BigInt negate(BigInt num) {
		return new BigInt(num.value.negate());
	}

	private void instantiateZero() {
		value = new BigFraction(0.0);
	}

	public boolean eqZero() {
		return value.compareTo(BigFraction.ZERO) == 0;
	}

	public boolean gt(Num num) {
		if (num instanceof NaN) {
			return false;
		}
		if (num instanceof PositiveInfinity) {
			return false;
		}
		if (num instanceof NegativeInfinity) {
			return true;
		}

		if (this.value.compareTo(((BigInt) num).value) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean gtZero() {
		if (this.value.compareTo(ZERO_BIGFRACTION) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geq(Num num) {
		if (num instanceof NaN) {
			return false;
		}
		if (num instanceof PositiveInfinity) {
			return false;
		}
		if (num instanceof NegativeInfinity) {
			return true;
		}

		if (this.value.compareTo(((BigInt) num).value) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean geqZero() {
		if (this.value.compareTo(ZERO_BIGFRACTION) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean lt(Num num) {
		if (num instanceof NaN) {
			return false;
		}
		if (num instanceof PositiveInfinity) {
			return true;
		}
		if (num instanceof NegativeInfinity) {
			return false;
		}

		if (this.value.compareTo(((BigInt) num).value) < 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean ltZero() {
		if (this.value.compareTo(ZERO_BIGFRACTION) < 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leq(Num num) {
		if (num instanceof NaN) {
			return false;
		}
		if (num instanceof PositiveInfinity) {
			return true;
		}
		if (num instanceof NegativeInfinity) {
			return false;
		}

		if (this.value.compareTo(((BigInt) num).value) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean leqZero() {
		if (this.value.compareTo(ZERO_BIGFRACTION) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isFinite() {
		return true;
	}

	public boolean isInfinite() {
		return false; // Handled by extraValues
	}

	public boolean isNaN() {
		return false; // Handled by extraValues
	}

	@Override
	public double doubleValue() {
		return value.doubleValue();
	}

	@Override
	public Num copy() {
		return new BigInt(this.value.getNumerator(), this.value.getDenominator());
	}

	@Override
	public boolean eq(double num) {
		return this.doubleValue() - num <= DoublePrecision.createEpsilon().doubleValue();
	}

	public boolean equals(BigInt num) {
		if (this.value.compareTo(num.value) == 0) {
			return true;
		} else {
			return eq(num.doubleValue());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BigInt)) {
			return false;
		} else {
			return equals(((BigInt) obj));
		}
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
