/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2017 Steffen Bondorf
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
import de.uni_kl.cs.disco.numbers.NumFactory;

public class RealSingleFactory implements NumFactory {
	private static RealSingleFactory instance = new RealSingleFactory();
	
    private Num POSITIVE_INFINITY = createPositiveInfinity();
    private Num NEGATIVE_INFINITY = createNegativeInfinity();
    private Num NaN = createNaN();
    private Num ZERO = createZero();
    private Num EPSILON = createEpsilon();

    protected RealSingleFactory() {} 
	
	public static RealSingleFactory getInstance() {
		return instance;
	}

    public Num getPositiveInfinity() {
        return POSITIVE_INFINITY;
    }

    public Num createPositiveInfinity() {
        return new RealSingle(Float.POSITIVE_INFINITY);
    }

    public Num getNegativeInfinity() {
        return NEGATIVE_INFINITY;
    }

    public Num createNegativeInfinity() {
        return new RealSingle(Float.NEGATIVE_INFINITY);
    }

    public Num getNaN() {
        return NaN;
    }

    public Num createNaN() {
        return new RealSingle(Float.NaN);
    }

    public Num getZero() {
        return ZERO;
    }

    public Num createZero() {
        return new RealSingle(0);
    }

    public Num getEpsilon() {
        return EPSILON;
    }

    public Num createEpsilon() {
        return RealSingle.createEpsilon();
    }

    public Num create(double value) {
        return new RealSingle(value);
    }

    public Num create(int num, int den) {
        return new RealSingle(num, den);
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
            throw new Exception("Invalid string representation of a number based on " + CalculatorConfig.getInstance().getNumClass().toString()
                    + ": " + num_str);
        }

        try {
            // either an integer of something strange
            if (!fraction_indicator && !double_based) {
                return create(Integer.parseInt(num_str));
            }

            if (fraction_indicator) {
                String[] num_den = num_str.split(" / "); // ["num","den"]
                if (num_den.length != 2) {
                    throw new Exception("Invalid string representation of a number based on " + CalculatorConfig.getInstance().getNumClass().toString()
                            + ": " + num_str);
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
            throw new Exception("Invalid string representation of a number based on " + CalculatorConfig.getInstance().getNumClass().toString()
                    + ": " + num_str);
        }

        // This code should not be reachable because all the operations above either succeed such that we can return a number
        // of raise an exception of some kind. Yet, Java does not get this and thus complains if there's no "finalizing statement".
        throw new Exception("Invalid string representation of a number based on " + CalculatorConfig.getInstance().getNumClass().toString()
                + ": " + num_str);
    }
}
