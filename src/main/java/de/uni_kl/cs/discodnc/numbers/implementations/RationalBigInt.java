/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2016 - 2018 Steffen Bondorf
 * Copyright (C) 2017+ The DiscoDNC contributors
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

import de.uni_kl.cs.discodnc.numbers.Num;
import de.uni_kl.cs.discodnc.numbers.values.NaN;
import de.uni_kl.cs.discodnc.numbers.values.NegativeInfinity;
import de.uni_kl.cs.discodnc.numbers.values.PositiveInfinity;

import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;

/**
 * Wrapper class around org.apache.commons.math3.BigFraction.BigFraction
 * introducing special values like positive / negative infinity and NaN as well
 * as operators like min, max, ==, &gt;, &gt;=, &lt;, and &lt;= that are not
 * part of BigFraction but needed by the network calculator.
 * <p>
 * For the ease of converting from the primitive data type double to BigFraction
 * objects, copy by value semantic are is applied.
 */
public class RationalBigInt implements Num {
    private static RationalBigInt instance = new RationalBigInt();

    private BigFraction value;
    
    private Num POSITIVE_INFINITY = null;
    private Num NEGATIVE_INFINITY = null;
    private Num NaN = null;
    private Num ZERO = null;

    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    
    private RationalBigInt() {
    }

    public RationalBigInt(int num) {
        value = new BigFraction(num);
    }
    
    public RationalBigInt(double value) {
        this.value = new BigFraction(value);
    }

    public RationalBigInt(int num, int den) {
        value = new BigFraction(num, den);
    }

    public RationalBigInt(RationalBigInt num) {
        value = new BigFraction(num.value.getNumerator(), num.value.getDenominator());
    }

    private RationalBigInt(BigInteger num, BigInteger den) {
        value = new BigFraction(num, den);
    }

    private RationalBigInt(BigFraction frac) {
        value = new BigFraction(frac.getNumerator(), frac.getDenominator());
    }

    public static RationalBigInt getInstance() {
        return instance;
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // Conversions
    // --------------------------------------------------------------------------------------------------------------
    
    public double doubleValue() {
        return value.doubleValue();
    }

    public BigFraction getValue() {
    	return new BigFraction(this.value.getNumerator(), this.value.getDenominator());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    // --------------------------------------------------------------------------------------------------------------
    // Factory
    // --------------------------------------------------------------------------------------------------------------
    
    public Num copy() {
        return new RationalBigInt(this.value.getNumerator(), this.value.getDenominator());
    }
    
    public Num getPositiveInfinity() {
        if (POSITIVE_INFINITY == null) {
            POSITIVE_INFINITY = createPositiveInfinity();
        }
        return POSITIVE_INFINITY;
    }

    public Num createPositiveInfinity() {
        return PositiveInfinity.getInstance();
    }

    public Num getNegativeInfinity() {
        if (NEGATIVE_INFINITY == null) {
            NEGATIVE_INFINITY = createNegativeInfinity();
        }
        return NEGATIVE_INFINITY;
    }

    public Num createNegativeInfinity() {
        return NegativeInfinity.getInstance();
    }

    public Num getNaN() {
        if (NaN == null) {
            NaN = createNaN();
        }
        return NaN;
    }

    public Num createNaN() {
        return de.uni_kl.cs.discodnc.numbers.values.NaN.getInstance();
    }

    public Num getZero() {
        if (ZERO == null) {
            ZERO = createZero();
        }
        return ZERO;
    }

    public Num createZero() {
        return new RationalBigInt(0);
    }
    
    public Num create(int num) {
        return new RationalBigInt(num);
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

        return new RationalBigInt(value);
    }

    public Num create(int num, int den) {
        if (den == 0) { // division by integer 0 throws an arithmetic exception
            throw new ArithmeticException("/ by zero");
        }

        return new RationalBigInt(num, den);
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
            throw new Exception("Invalid string representation of a number based on RationalBigInt: " + num_str);
        }

        try {
            // either an integer of something strange
            if (!fraction_indicator && !double_based) {
                return create(Integer.parseInt(num_str));
            }

            if (fraction_indicator) {
                String[] num_den = num_str.split(" / "); // ["num","den"]
                if (num_den.length != 2) {
                    throw new Exception("Invalid string representation of a number based on RationalBigInt: " + num_str);
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
            throw new Exception("Invalid string representation of a number based on RationalBigInt: " + num_str);
        }

        // This code should not be reachable because all the operations above either
        // succeed such that we can return a number
        // of raise an exception of some kind. Yet, Java does not get this and thus
        // complains if there's no "finalizing statement".
        throw new Exception("Invalid string representation of a number based on RationalBigInt: " + num_str);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Comparisons
    // --------------------------------------------------------------------------------------------------------------
    
    // Compare to zero: >, >=, =, <=, <
    public boolean gtZero() {
        return this.value.compareTo(BigFraction.ZERO) > 0;
    }

    public boolean geqZero() {
        return this.value.compareTo(BigFraction.ZERO) >= 0;
    }

    public boolean eqZero() {
        return value.compareTo(BigFraction.ZERO) == 0;
    }

    public boolean leqZero() {
        return this.value.compareTo(BigFraction.ZERO) <= 0;
    }
    
    public boolean ltZero() {
        return this.value.compareTo(BigFraction.ZERO) < 0;
    }

    // Compare to other number: >, >=, =, <=, <
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

        return this.value.compareTo(((RationalBigInt) num).value) > 0;
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

        return this.value.compareTo(((RationalBigInt) num).value) >= 0;
    }
    
    public boolean eq(Num num) {
    	if (!(num instanceof RationalBigInt)) {
    		return false;
    	}
    	
        return this.value.compareTo(((RationalBigInt)num).value) == 0;
    }

    public boolean eq(double num) {
    	return eq(new RationalBigInt(num));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RationalBigInt)) {
            return false;
        } else {
            return eq(((RationalBigInt) obj));
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

        return this.value.compareTo(((RationalBigInt) num).value) <= 0;
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

        return this.value.compareTo(((RationalBigInt) num).value) < 0;
    }

    // Properties
    public boolean isFinite() {
        return true;
    }

    public boolean isInfinite() {
        return false; // Handled by extraValues
    }

    public boolean isNaN() {
        return false; // Handled by extraValues
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // Operations (Utils)
    // --------------------------------------------------------------------------------------------------------------
    
    public Num add(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN
                || (num1 instanceof PositiveInfinity && num2 instanceof NegativeInfinity)
                || (num1 instanceof NegativeInfinity && num2 instanceof PositiveInfinity)) {
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity) {
        	// other num is not negative infinity
            return getPositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity) {
        	// other num is not positive infinity
            return getNegativeInfinity();
        }

        return new RationalBigInt(((RationalBigInt) num1).value.add(((RationalBigInt) num2).value));
    }

    public Num sub(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return getNaN();
        }

        if (num1 instanceof NaN || num2 instanceof NaN
                || (num1 instanceof PositiveInfinity && num2 instanceof PositiveInfinity)
                || (num1 instanceof NegativeInfinity && num2 instanceof NegativeInfinity)) {
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity // num2 is not positive infinity
                || num2 instanceof NegativeInfinity) { // num1 is not negative infinity
            return getPositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity // num2 is not negative infinity
                || num2 instanceof PositiveInfinity) { // num1 is not positive infinity
            return getNegativeInfinity();
        }

        return new RationalBigInt(((RationalBigInt) num1).value.subtract(((RationalBigInt) num2).value));
    }

    public Num mult(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity) {
            if (num2.ltZero() || num2 instanceof NegativeInfinity) {
                return getNegativeInfinity();
            } else {
                return getPositiveInfinity();
            }
        }
        if (num2 instanceof PositiveInfinity) {
            if (num1.ltZero() || num1 instanceof NegativeInfinity) {
                return getNegativeInfinity();
            } else {
                return getPositiveInfinity();
            }
        }
        if (num1 instanceof NegativeInfinity) {
            if (num2.ltZero() || num2 instanceof NegativeInfinity) {
                return getPositiveInfinity();
            } else {
                return getNegativeInfinity();
            }
        }
        if (num2 instanceof NegativeInfinity) {
            if (num1.ltZero() || num1 instanceof NegativeInfinity) {
                return getPositiveInfinity();
            } else {
                return getNegativeInfinity();
            }
        }

        return new RationalBigInt((((RationalBigInt) num1).value.multiply(((RationalBigInt) num2).value)));
    }

    public Num div(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN
                || ((num1 instanceof PositiveInfinity || num1 instanceof NegativeInfinity)
                		// two infinities in the division
                && (num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity))) { 
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity) { // positive infinity divided by some finite value
            if (num2.ltZero()) {
                return getNegativeInfinity();
            } else {
                return getPositiveInfinity();
            }
        }
        if (num1 instanceof NegativeInfinity) { // negative infinity divided by some finite value
            if (num2.ltZero()) {
                return getPositiveInfinity();
            } else {
                return getNegativeInfinity();
            }
        }
        if (num2 instanceof PositiveInfinity || num2 instanceof NegativeInfinity) { // finite value divided by infinity
            return new RationalBigInt(0);
        }

        if (((RationalBigInt) num2).eqZero()) {
            return getPositiveInfinity();
        } else {
            return new RationalBigInt(((RationalBigInt) num1).value.divide(((RationalBigInt) num2).value));
        }
    }

    public Num abs(Num num) {
        if (num instanceof NaN) {
            return getNaN();
        }
        if (num instanceof PositiveInfinity || num instanceof NegativeInfinity) {
            return getPositiveInfinity();
        }

        return new RationalBigInt(((RationalBigInt) num).value.abs());
    }

    public Num diff(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity || num1 instanceof NegativeInfinity
                || num2 instanceof NegativeInfinity) {
            return getPositiveInfinity();
        }

        return sub(max(((RationalBigInt) num1), ((RationalBigInt) num2)), min(((RationalBigInt) num1), ((RationalBigInt) num2)));
    }

    public Num max(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return getNaN();
        }
        if (num1 instanceof PositiveInfinity || num2 instanceof PositiveInfinity) {
            return getPositiveInfinity();
        }
        if (num1 instanceof NegativeInfinity) {
            return num2.copy();
        }
        if (num2 instanceof NegativeInfinity) {
            return num1.copy();
        }

        if (((RationalBigInt) num1).value.compareTo(((RationalBigInt) num2).value) >= 0) {
            return num1;
        } else {
            return num2;
        }
    }

    public Num min(Num num1, Num num2) {
        if (num1 instanceof NaN || num2 instanceof NaN) {
            return getNaN();
        }
        if (num1 instanceof NegativeInfinity || num2 instanceof NegativeInfinity) {
            return getNegativeInfinity();
        }
        if (num1 instanceof PositiveInfinity) {
            return num2.copy();
        }
        if (num2 instanceof PositiveInfinity) {
            return num1.copy();
        }

        if (((RationalBigInt) num1).value.compareTo(((RationalBigInt) num2).value) <= 0) {
            return num1;
        } else {
            return num2;
        }
    }

    public Num negate(Num num) {
        if (num instanceof NaN) {
            return getNaN();
        }
        if (num instanceof PositiveInfinity) {
            return getNegativeInfinity();
        }
        if (num instanceof NegativeInfinity) {
            return getPositiveInfinity();
        }

        return new RationalBigInt(((RationalBigInt) num).value.negate());
    }
}
