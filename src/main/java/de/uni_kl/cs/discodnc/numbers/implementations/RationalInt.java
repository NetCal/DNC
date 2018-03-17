/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2013 - 2018 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
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

package de.uni_kl.cs.discodnc.numbers.implementations;

import de.uni_kl.cs.discodnc.nc.CalculatorConfig;
import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.numbers.values.NaN;
import de.uni_kl.cs.discodnc.numbers.values.NegativeInfinity;
import de.uni_kl.cs.discodnc.numbers.values.PositiveInfinity;
import org.apache.commons.math3.fraction.Fraction;

/**
 * Wrapper class around org.apache.commons.math3.fraction.Fraction introducing
 * special values like positive / negative infinity and NaN as well as operators
 * like min, max, ==, &gt;, &gt;=, &lt;, and &lt;= that are not part of Fraction
 * but needed by the network calculator.
 * <p>
 * For the ease of converting from the primitive data type double to Fraction
 * objects, copy by value semantic are is applied.
 */
public class RationalInt implements Num {
    // Unfortunately you cannot give the constructor the double value 0.0000001
    private static final Fraction EPSILON_FRACTION = new Fraction(1, 1000000);
    private static RationalInt instance = new RationalInt();
    private Fraction value;
    private Num POSITIVE_INFINITY = null;
    private Num NEGATIVE_INFINITY = null;
    private Num NaN = null;
    private Num ZERO = null;
    private Num EPSILON = null;

    private RationalInt() {
    }

    // --------------------------------------------------------------------------------------------------------------
    // Num Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    public RationalInt(int num) {
        value = new Fraction(num);
    }

    public RationalInt(double value) {
        this.value = new Fraction(value);
    }

    public RationalInt(int num, int den) {
        value = new Fraction(num, den);
    }

    public RationalInt(RationalInt num) {
        value = new Fraction(num.value.getNumerator(), num.value.getDenominator());
    }

    private RationalInt(Fraction frac) {
        value = new Fraction(frac.getNumerator(), frac.getDenominator());
    }

    public static RationalInt getInstance() {
        return instance;
    }

    public boolean eqZero() {
        return value.getNumerator() == 0;
    }

    public boolean gt(Num num) {
        if (num instanceof de.uni_kl.cs.discodnc.numbers.values.NaN) {
            return false;
        }
        if (num instanceof PositiveInfinity) {
            return false;
        }
        if (num instanceof NegativeInfinity) {
            return true;
        }

        if (this.value.compareTo(((RationalInt) num).value) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean gtZero() {
        if (this.value.compareTo(Fraction.ZERO) > 0) {
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

        if (this.value.compareTo(((RationalInt) num).value) >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean geqZero() {
        if (this.value.compareTo(Fraction.ZERO) >= 0) {
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

        if (this.value.compareTo(((RationalInt) num).value) < 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean ltZero() {
        if (this.value.compareTo(Fraction.ZERO) < 0) {
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

        if (this.value.compareTo(((RationalInt) num).value) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean leqZero() {
        if (this.value.compareTo(Fraction.ZERO) <= 0) {
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

    // --------------------------------------------------------------------------------------------------------------
    // Factory Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    @Override
    public Num copy() {
        return new RationalInt(this.value.getNumerator(), this.value.getDenominator());
    }

    @Override
    public boolean eq(double num) {
        return equals(new RationalInt(num));
    }

    public boolean equals(RationalInt num) {
        if (this.value.compareTo(num.value) == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RationalInt)) {
            return false;
        } else {
            return equals(((RationalInt) obj));
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

    public Num getPositiveInfinity() {
        if (POSITIVE_INFINITY == null) {
            POSITIVE_INFINITY = createPositiveInfinity();
        }
        return POSITIVE_INFINITY;
    }

    public Num createPositiveInfinity() {
        return new PositiveInfinity();
    }

    public Num getNegativeInfinity() {
        if (NEGATIVE_INFINITY == null) {
            NEGATIVE_INFINITY = createNegativeInfinity();
        }
        return NEGATIVE_INFINITY;
    }

    public Num createNegativeInfinity() {
        return new NegativeInfinity();
    }

    public Num getNaN() {
        if (NaN == null) {
            NaN = createNaN();
        }
        return NaN;
    }

    public Num createNaN() {
        return new NaN();
    }

    public Num getZero() {
        if (ZERO == null) {
            ZERO = createZero();
        }
        return ZERO;
    }

    public Num createZero() {
        return new RationalInt(0);
    }

    public Num getEpsilon() {
        if (EPSILON == null) {
            EPSILON = createEpsilon();
        }
        return EPSILON;
    }

    public Num createEpsilon() {
        return new RationalInt(EPSILON_FRACTION);
    }

    public Num create(int num) {
        return new RationalInt(num);
    }

    public Num create(double value) {
        // non IEEE 754 floating point data types
        if (value == Double.POSITIVE_INFINITY) {
            return createPositiveInfinity();
        }
        if (value == Double.NEGATIVE_INFINITY) {
            return createNegativeInfinity();
        }
        if (Double.isNaN(value)) {
            return createNaN();
        }

        return new RationalInt(value);
    }

    public Num create(int num, int den) {
        if (den == 0) { // division by integer 0 throws an arithmetic exception
            throw new ArithmeticException("/ by zero");
        }

        return new RationalInt(num, den);
    }

    public Num create(String num_str) throws Exception {
        if (num_str.equals("Infinity")) {
            return createPositiveInfinity();
        }
        if (num_str.equals("-Infinity")) {
            return createNegativeInfinity();
        }
        if (num_str.equals("NaN") || num_str.equals("NA")) {
            return createNaN();
        }

        boolean fraction_indicator = num_str.contains(" / ");
        boolean double_based = num_str.contains(".");

        if (fraction_indicator && double_based) {
            throw new Exception("Invalid string representation of a number based on "
                    + CalculatorConfig.getInstance().getNumImpl().toString() + ": " + num_str);
        }

        try {
            // either an integer of something strange
            if (!fraction_indicator && !double_based) {
                return create(Integer.parseInt(num_str));
            }

            if (fraction_indicator) {
                String[] num_den = num_str.split(" / "); // ["num","den"]
                if (num_den.length != 2) {
                    throw new Exception("Invalid string representation of a number based on "
                            + CalculatorConfig.getInstance().getNumImpl().toString() + ": " + num_str);
                }

                int den = Integer.parseInt(num_den[1]);
                if (den != 0) {
                    return create(Integer.parseInt(num_den[0]), den);
                } else {
                    return createNaN();
                }
            }

            if (double_based) {
                return create(Double.parseDouble(num_str));
            }
        } catch (Exception e) {
            throw new Exception("Invalid string representation of a number based on "
                    + CalculatorConfig.getInstance().getNumImpl().toString() + ": " + num_str);
        }

        // This code should not be reachable because all the operations above either
        // succeed such that we can return a number
        // of raise an exception of some kind. Yet, Java does not get this and thus
        // complains if there's no "finalizing statement".
        throw new Exception("Invalid string representation of a number based on "
                + CalculatorConfig.getInstance().getNumImpl().toString() + ": " + num_str);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Utils Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    public Num add(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN
                || (num1 instanceof PositiveInfinity && num2 instanceof NegativeInfinity)
                || (num1 instanceof NegativeInfinity && num2 instanceof PositiveInfinity)) {
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity) { // other num is not negative
            // infinity
            return new PositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity) { // other num is not positive
            // infinity
            return new NegativeInfinity();
        }

        // May throw MathArithmeticException due to integer overflow
        return new RationalInt(((RationalInt) num1).value.add(((RationalInt) num2).value));
    }

    public Num sub(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return new NaN();
        }

        if (num1 instanceof NaN || num2 instanceof NaN
                || (num1 instanceof PositiveInfinity && num2 instanceof PositiveInfinity)
                || (num1 instanceof NegativeInfinity && num2 instanceof NegativeInfinity)) {
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity // num2 is not positive infinity
                || num2 instanceof NegativeInfinity) { // num1 is not negative infinity
            return new PositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity // num2 is not negative infinity
                || num2 instanceof PositiveInfinity) { // num1 is not positive infinity
            return new NegativeInfinity();
        }

        // May throw MathArithmeticException due to integer overflow
        return new RationalInt(((RationalInt) num1).value.subtract(((RationalInt) num2).value));
    }

    public Num mult(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity) {
            if (num2.ltZero() || num2 instanceof NegativeInfinity) {
                return new NegativeInfinity();
            } else {
                return new PositiveInfinity();
            }
        }
        if (num2 instanceof PositiveInfinity) {
            if (num1.ltZero() || num1 instanceof NegativeInfinity) {
                return new NegativeInfinity();
            } else {
                return new PositiveInfinity();
            }
        }
        if (num1 instanceof NegativeInfinity) {
            if (num2.ltZero() || num2 instanceof NegativeInfinity) {
                return new PositiveInfinity();
            } else {
                return new NegativeInfinity();
            }
        }
        if (num2 instanceof NegativeInfinity) {
            if (num1.ltZero() || num1 instanceof NegativeInfinity) {
                return new PositiveInfinity();
            } else {
                return new NegativeInfinity();
            }
        }

        // May throw MathArithmeticException due to integer overflow
        return new RationalInt(((RationalInt) num1).value.multiply(((RationalInt) num2).value));
    }

    public Num div(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN
                || ((num1 instanceof PositiveInfinity || num1 instanceof NegativeInfinity)
                && (num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity))) { // two infinities
            // in the
            // division
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity) { // positive infinity divided by some finite value
            if (num2.ltZero()) {
                return new NegativeInfinity();
            } else {
                return new PositiveInfinity();
            }
        }
        if (num1 instanceof NegativeInfinity) { // negative infinity divided by some finite value
            if (num2.ltZero()) {
                return new PositiveInfinity();
            } else {
                return new NegativeInfinity();
            }
        }
        if (num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity) { // finite value divided by infinity
            return new RationalInt(0);
        }

        if (((RationalInt) num2).eqZero()) {
            return new PositiveInfinity();
        } else {
            return new RationalInt(((RationalInt) num1).value.divide(((RationalInt) num2).value));
        }
    }

    public Num abs(Num num) {
        if (num instanceof NaN) {
            return new NaN();
        }
        if (num instanceof PositiveInfinity || num instanceof NegativeInfinity) {
            return new PositiveInfinity();
        }

        return new RationalInt(((RationalInt) num).value.abs());
    }

    public Num diff(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity || num1 instanceof NegativeInfinity
                || num2 instanceof NegativeInfinity) {
            return new PositiveInfinity();
        }

        return sub(max(((RationalInt) num1), ((RationalInt) num2)), min(((RationalInt) num1), ((RationalInt) num2)));
    }

    public Num max(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return new NaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity) {
            return new PositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity) {
            return num2.copy();
        }
        if (num2 instanceof NegativeInfinity) {
            return num1.copy();
        }

        if (((RationalInt) num1).value.compareTo(((RationalInt) num2).value) >= 0) {
            return num1;
        } else {
            return num2;
        }
    }

    public Num min(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return new NaN();
        }
        if (num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity) {
            return new NegativeInfinity();
        }
        if (num1 instanceof PositiveInfinity) {
            return num2.copy();
        }
        if (num2 instanceof PositiveInfinity) {
            return num1.copy();
        }

        if (((RationalInt) num1).value.compareTo(((RationalInt) num2).value) <= 0) {
            return num1;
        } else {
            return num2;
        }
    }

    public Num negate(Num num) {
        if (num instanceof NaN) {
            return new NaN();
        }
        if (num instanceof PositiveInfinity) {
            return new NegativeInfinity();
        }
        if (num instanceof NegativeInfinity) {
            return new PositiveInfinity();
        }

        return new RationalInt(((RationalInt) num).value.negate());
    }

    public boolean isFinite(Num num) {
        if (num instanceof RationalInt) { // Only stores finite values
            return true;
        } else {
            return false; // NaN is neither finite nor infinite
        }
    }

    public boolean isInfinite(Num num) {
        if ((num instanceof PositiveInfinity) || (num instanceof NegativeInfinity)) {
            return true;
        } else {
            return false; // NaN is neither finite nor infinite
        }
    }

    public boolean isNaN(Num num) {
        if (num instanceof NaN) {
            return true;
        } else {
            return false;
        }
    }
}
