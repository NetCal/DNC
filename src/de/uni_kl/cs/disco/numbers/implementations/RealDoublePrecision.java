/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta4 "Chimera".
 *
 * Copyright (C) 2013 - 2017 Steffen Bondorf
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

public class RealDoublePrecision implements Num {
    private static final double EPSILON_Double = Double.parseDouble("5E-10");
    private static RealDoublePrecision instance = new RealDoublePrecision();
    private static boolean comparison_epsilon = false;
    private double value;
    private Num POSITIVE_INFINITY = null;
    private Num NEGATIVE_INFINITY = null;
    private Num NaN = null;
    private Num ZERO = null;

    // --------------------------------------------------------------------------------------------------------------
    // Num Interface Implementations
    // --------------------------------------------------------------------------------------------------------------
    private Num EPSILON = null;
    private RealDoublePrecision() {
    }

    public RealDoublePrecision(int num) {
        value = (double) num;
    }

    public RealDoublePrecision(double value) {
        this.value = value;
    }

    public RealDoublePrecision(int num, int den) {
        value = ((double) num) / ((double) den);
    }

    public RealDoublePrecision(RealDoublePrecision num) {
        value = num.value;
    }

    public static RealDoublePrecision getInstance() {
        return instance;
    }

    public boolean eqZero() {
        if (comparison_epsilon) {
            return (value <= EPSILON_Double) && (value >= -EPSILON_Double);
        } else {
            return value == 0.0;
        }
    }

    public boolean gt(Num num) {
        if (comparison_epsilon) {
            return value > (num.doubleValue() + EPSILON_Double);
        } else {
            return value > num.doubleValue();
        }
    }

    public boolean gtZero() {
        if (comparison_epsilon) {
            return value > EPSILON_Double;
        } else {
            return value > 0.0;
        }
    }

    public boolean geq(Num num) {
        if (comparison_epsilon) {
            return value >= (num.doubleValue() + EPSILON_Double);
        } else {
            return value >= num.doubleValue();
        }
    }

    public boolean geqZero() {
        if (comparison_epsilon) {
            return value >= EPSILON_Double;
        } else {
            return value >= 0.0;
        }
    }

    public boolean lt(Num num) {
        if (comparison_epsilon) {
            return value < (num.doubleValue() - EPSILON_Double);
        } else {
            return value < num.doubleValue();
        }
    }

    public boolean ltZero() {
        if (comparison_epsilon) {
            return value < -EPSILON_Double;
        } else {
            return value < 0.0;
        }
    }

    public boolean leq(Num num) {
        if (comparison_epsilon) {
            return value <= (num.doubleValue() - EPSILON_Double);
        } else {
            return value <= num.doubleValue();
        }
    }

    public boolean leqZero() {
        if (comparison_epsilon) {
            return value <= -EPSILON_Double;
        } else {
            return value <= 0.0;
        }
    }

    public boolean isFinite() {
        return Double.isFinite(value);
    }

    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    public boolean isNaN() {
        return Double.isNaN(value);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public Num copy() {
        return new RealDoublePrecision(value);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Factory Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    @Override
    public boolean eq(double num) {
        // if( ( this.value == Double.POSITIVE_INFINITY && num ==
        // Double.POSITIVE_INFINITY )
        // || ( this.value == Double.NEGATIVE_INFINITY && num ==
        // Double.NEGATIVE_INFINITY ) ) {
        if (Double.isInfinite(this.value) && Double.isInfinite(num) && (Double.compare(this.value, num) == 0)) {
            return true;
        }

        if (Math.abs(value - num) <= EPSILON_Double) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(RealDoublePrecision num) {
        return eq(num.value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RealDoublePrecision)) {
            return false;
        } else {
            return eq(((RealDoublePrecision) obj).value);
        }
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    public Num getPositiveInfinity() {
        if (POSITIVE_INFINITY == null) {
            POSITIVE_INFINITY = createPositiveInfinity();
        }
        return POSITIVE_INFINITY;
    }

    public Num createPositiveInfinity() {
        return new RealDoublePrecision(Double.POSITIVE_INFINITY);
    }

    public Num getNegativeInfinity() {
        if (NEGATIVE_INFINITY == null) {
            NEGATIVE_INFINITY = createNegativeInfinity();
        }
        return NEGATIVE_INFINITY;
    }

    public Num createNegativeInfinity() {
        return new RealDoublePrecision(Double.NEGATIVE_INFINITY);
    }

    public Num getNaN() {
        if (NaN == null) {
            NaN = createNaN();
        }
        return NaN;
    }

    public Num createNaN() {
        return new RealDoublePrecision(Double.NaN);
    }

    public Num getZero() {
        if (ZERO == null) {
            ZERO = createZero();
        }
        return ZERO;
    }

    public Num createZero() {
        return new RealDoublePrecision(0);
    }

    public Num getEpsilon() {
        if (EPSILON == null) {
            EPSILON = createEpsilon();
        }
        return EPSILON;
    }

    public Num createEpsilon() {
        return new RealDoublePrecision(EPSILON_Double);
    }

    public Num create(int num) {
        return new RealDoublePrecision(num);
    }

    public Num create(double value) {
        return new RealDoublePrecision(value);
    }

    public Num create(int num, int den) {
        if (den == 0) { // division by integer 0 throws an arithmetic exception
            throw new ArithmeticException("/ by zero");
        }
        return new RealDoublePrecision(num, den);
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
        return new RealDoublePrecision(((RealDoublePrecision) num1).value + ((RealDoublePrecision) num2).value);
    }

    public Num sub(Num num1, Num num2) {
        double result = ((RealDoublePrecision) num1).value - ((RealDoublePrecision) num2).value;
        if (Math.abs(result) <= EPSILON_Double) {
            result = 0;
        }
        return new RealDoublePrecision(result);
    }

    public Num mult(Num num1, Num num2) {
        return new RealDoublePrecision(((RealDoublePrecision) num1).value * ((RealDoublePrecision) num2).value);
    }

    public Num div(Num num1, Num num2) {
        return new RealDoublePrecision(((RealDoublePrecision) num1).value / ((RealDoublePrecision) num2).value);
    }

    public Num abs(Num num) {
        return new RealDoublePrecision(Math.abs(((RealDoublePrecision) num).value));
    }

    public Num diff(Num num1, Num num2) {
        return new RealDoublePrecision(Math.max(((RealDoublePrecision) num1).value, ((RealDoublePrecision) num2).value)
                - Math.min(((RealDoublePrecision) num1).value, ((RealDoublePrecision) num2).value));
    }

    public Num max(Num num1, Num num2) {
        return new RealDoublePrecision(Math.max(((RealDoublePrecision) num1).value, ((RealDoublePrecision) num2).value));
    }

    public Num min(Num num1, Num num2) {
        return new RealDoublePrecision(Math.min(((RealDoublePrecision) num1).value, ((RealDoublePrecision) num2).value));
    }

    public Num negate(Num num) {
        return new RealDoublePrecision(((RealDoublePrecision) num).value * -1);
    }

    public boolean isFinite(Num num) {
        return ((RealDoublePrecision) num).isFinite();
    }

    public boolean isInfinite(Num num) {
        return ((RealDoublePrecision) num).isInfinite();
    }

    public boolean isNaN(Num num) {
        return ((RealDoublePrecision) num).isNaN();
    }
}
