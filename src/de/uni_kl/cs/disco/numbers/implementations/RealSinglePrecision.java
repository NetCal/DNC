/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
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

package de.uni_kl.cs.disco.numbers.implementations;

import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;

public class RealSinglePrecision implements Num {
    private static final float EPSILON_Float = 0.00016f; // Maximum rounding error observed in the functional tests
    private static RealSinglePrecision instance = new RealSinglePrecision();
    private static boolean comparison_epsilon = false;
    private float value;
    private Num POSITIVE_INFINITY = null;
    private Num NEGATIVE_INFINITY = null;
    private Num NaN = null;
    private Num ZERO = null;

    // --------------------------------------------------------------------------------------------------------------
    // Num Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    private Num EPSILON = null;
    private RealSinglePrecision() {
    }

    public RealSinglePrecision(int num) {
        value = (float) num;
    }

    public RealSinglePrecision(double value) {
        this.value = (float) value;
    }

    public RealSinglePrecision(int num, int den) {
        value = ((float) num) / ((float) den);
    }

    public RealSinglePrecision(RealSinglePrecision num) {
        value = num.value;
    }

    public static RealSinglePrecision getInstance() {
        return instance;
    }

    public boolean eqZero() {
        if (comparison_epsilon) {
            return (value <= EPSILON_Float) && (value >= -EPSILON_Float);
        } else {
            return value == 0.0f;
        }
    }

    public boolean gt(Num num) {
        float num_float = (float) num.doubleValue();

        if (comparison_epsilon) {
            return value > (num_float + EPSILON_Float);
        } else {
            return value > num_float;
        }
    }

    public boolean gtZero() {
        if (comparison_epsilon) {
            return value > EPSILON_Float;
        } else {
            return value > 0.0f;
        }
    }

    public boolean geq(Num num) {
        float num_float = (float) num.doubleValue();

        if (comparison_epsilon) {
            return value >= (num_float + EPSILON_Float);
        } else {
            return value >= num_float;
        }
    }

    public boolean geqZero() {
        if (comparison_epsilon) {
            return value >= EPSILON_Float;
        } else {
            return value >= 0.0f;
        }
    }

    public boolean lt(Num num) {
        float num_float = (float) num.doubleValue();

        if (comparison_epsilon) {
            return value < (num_float - EPSILON_Float);
        } else {
            return value < num_float;
        }
    }

    public boolean ltZero() {
        if (comparison_epsilon) {
            return value < -EPSILON_Float;
        } else {
            return value < 0.0f;
        }
    }

    public boolean leq(Num num) {
        float num_float = (float) num.doubleValue();

        if (comparison_epsilon) {
            return value <= (num_float - EPSILON_Float);
        } else {
            return value <= num_float;
        }
    }

    public boolean leqZero() {
        if (comparison_epsilon) {
            return value <= -EPSILON_Float;
        } else {
            return value <= 0.0f;
        }
    }

    public boolean isFinite() {
        return Float.isFinite(value);
    }

    public boolean isInfinite() {
        return Float.isInfinite(value);
    }

    public boolean isNaN() {
        return Float.isNaN(value);
    }

    @Override
    public double doubleValue() {
        return (double) value;
    }

    @Override
    public Num copy() {
        return new RealSinglePrecision(value);
    }

    @Override
    public boolean eq(double num) {
        if (comparison_epsilon) {
            return Math.abs(num - value) <= EPSILON_Float;
        } else {
            return Double.compare(value, num) == 0;
        }
    }

    // --------------------------------------------------------------------------------------------------------------
    // Factory Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    public boolean equals(RealSinglePrecision num) {
        return equals(num.value);
    }

    private boolean equals(float num) {
        // if(
        // ( this.value == Float.POSITIVE_INFINITY && num == Float.POSITIVE_INFINITY )
        // ||
        // ( this.value == Float.NEGATIVE_INFINITY && num == Float.NEGATIVE_INFINITY )
        // ) {
        // 	return true;
        // }
        if (Float.isInfinite(this.value) && Float.isInfinite(num) && (Float.compare(this.value, num) == 0)) {
            return true;
        }

        if (Math.abs(value - num) <= EPSILON_Float) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RealSinglePrecision)) {
            return false;
        } else {
            return equals(((RealSinglePrecision) obj).value);
        }
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }

    public Num getPositiveInfinity() {
        if (POSITIVE_INFINITY == null) {
            POSITIVE_INFINITY = createPositiveInfinity();
        }
        return POSITIVE_INFINITY;
    }

    public Num createPositiveInfinity() {
        return new RealSinglePrecision(Double.POSITIVE_INFINITY);
    }

    public Num getNegativeInfinity() {
        if (NEGATIVE_INFINITY == null) {
            NEGATIVE_INFINITY = createNegativeInfinity();
        }
        return NEGATIVE_INFINITY;
    }

    public Num createNegativeInfinity() {
        return new RealSinglePrecision(Double.NEGATIVE_INFINITY);
    }

    public Num getNaN() {
        if (NaN == null) {
            NaN = createNaN();
        }
        return NaN;
    }

    public Num createNaN() {
        return new RealSinglePrecision(Double.NaN);
    }

    public Num getZero() {
        if (ZERO == null) {
            ZERO = createZero();
        }
        return ZERO;
    }

    public Num createZero() {
        return new RealSinglePrecision(0);
    }

    public Num getEpsilon() {
        if (EPSILON == null) {
            EPSILON = createEpsilon();
        }
        return EPSILON;
    }

    public Num createEpsilon() {
        return new RealSinglePrecision(EPSILON_Float);
    }

    public Num create(int num) {
        return new RealSinglePrecision(num);
    }

    public Num create(double value) {
        return new RealSinglePrecision(value);
    }

    public Num create(int num, int den) {
        if (den == 0) { // division by integer 0 throws an arithmetic exception
            throw new ArithmeticException("/ by zero");
        }

        return new RealSinglePrecision(num, den);
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
        return new RealSinglePrecision(((RealSinglePrecision) num1).value + ((RealSinglePrecision) num2).value);
    }

    public Num sub(Num num1, Num num2) {
        float result = ((RealSinglePrecision) num1).value - ((RealSinglePrecision) num2).value;
        if (comparison_epsilon && Math.abs(result) <= EPSILON_Float) {
            result = 0;
        }
        return new RealSinglePrecision(result);
    }

    public Num mult(Num num1, Num num2) {
        return new RealSinglePrecision(((RealSinglePrecision) num1).value * ((RealSinglePrecision) num2).value);
    }

    public Num div(Num num1, Num num2) {
        return new RealSinglePrecision(((RealSinglePrecision) num1).value / ((RealSinglePrecision) num2).value);
    }

    public Num abs(Num num) {
        return new RealSinglePrecision(Math.abs(((RealSinglePrecision) num).value));
    }

    public Num diff(Num num1, Num num2) {
        return new RealSinglePrecision(
                Math.max(((RealSinglePrecision) num1).value, ((RealSinglePrecision) num2).value)
                        - Math.min(((RealSinglePrecision) num1).value, ((RealSinglePrecision) num2).value)
        );
    }

    public Num max(Num num1, Num num2) {
        return new RealSinglePrecision(Math.max(((RealSinglePrecision) num1).value, ((RealSinglePrecision) num2).value));
    }

    public Num min(Num num1, Num num2) {
        return new RealSinglePrecision(Math.min(((RealSinglePrecision) num1).value, ((RealSinglePrecision) num2).value));
    }

    public Num negate(Num num) {
        return new RealSinglePrecision(((RealSinglePrecision) num).value * -1);
    }

    public boolean isFinite(Num num) {
        return ((RealSinglePrecision) num).isFinite();
    }

    public boolean isInfinite(Num num) {
        return ((RealSinglePrecision) num).isInfinite();
    }

    public boolean isNaN(Num num) {
        return ((RealSinglePrecision) num).isNaN();
    }
}
