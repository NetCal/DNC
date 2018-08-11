/*
 * This file is part of the Disco Deterministic Network Calculator.
 *
 * Copyright (C) 2014 - 2018 Steffen Bondorf
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

package de.uni_kl.cs.discodnc.numbers.values;

import de.uni_kl.cs.discodnc.numbers.Num;

public final class NaN implements Num {
    private static NaN instance = new NaN();
    
    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------
    
	private NaN() {
    }

    public NaN(int num) {
    }
    
    public NaN(double value) {
    }
    
    public NaN(int num, int den){
    }
    
    public NaN(NaN num) {
    }

    /* 
     * NaN should not be used as a factory.
     * Therefore, there is no instance or get Instance method.
     */
    public static NaN getInstance() {
    	return instance;
    }
	
    // --------------------------------------------------------------------------------------------------------------
    // Conversions
    // --------------------------------------------------------------------------------------------------------------
    
    public double doubleValue() {
        return Double.NaN;
    }

    /* 
     * NaN does not have an value member variable.
     */

    @Override
    public int hashCode() {
        return Double.hashCode(Double.NaN);
    }

    @Override
    public String toString() {
        return Double.toString(Double.NaN);
    }
	
    // --------------------------------------------------------------------------------------------------------------
    // Factory
    // --------------------------------------------------------------------------------------------------------------

    public Num copy() {
        return instance;
    }

    public Num getPositiveInfinity() {
        throw new RuntimeException();
    }

    public Num createPositiveInfinity() {
        throw new RuntimeException();
    }

    public Num getNegativeInfinity() {
        throw new RuntimeException();
    }

    public Num createNegativeInfinity() {
        throw new RuntimeException();
    }

    public Num getNaN() {
        return instance;
    }

    public Num createNaN() {
        return instance;
    }

    public Num getZero() {
        throw new RuntimeException();
    }

    public Num createZero() {
        throw new RuntimeException();
    }

    public Num getTestEpsilon() {
        return instance;
    }

    public Num create(int num) {
        throw new RuntimeException();
    }

    public Num create(double value) {
        throw new RuntimeException();
    }

    public Num create(int num, int den) {
        throw new RuntimeException();
    }

    public Num create(String num_str) throws Exception {
        throw new RuntimeException();
    }
    
    // --------------------------------------------------------------------------------------------------------------
    // Comparisons
    // --------------------------------------------------------------------------------------------------------------

    // Compare to zero: >, >=, =, <=, <
    public boolean ltZero() {
        return false;
    }

    public boolean leqZero() {
        return false;
    }
    
    public boolean eqZero() {
        return false;
    }

    public boolean geqZero() {
        return false;
    }

    public boolean gtZero() {
        return false;
    }

    // Compare to other number: >, >=, =, <=, <
    public boolean lt(Num num) {
        return false;
    }

    public boolean leq(Num num) {
        return false;
    }
    
    public boolean eq(Num num) {
        return false;
    }
    
    public boolean eq(double num) {
        return false;
    }
    
//	@SuppressFBWarnings(value = "EQ_ALWAYS_FALSE", justification = "Comparison to NaN should always return false.")
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public boolean geq(Num num) {
        return false;
    }
    
    public boolean gt(Num num) {
        return false;
    }

    // Properties
    public boolean isFinite() {
        return false;
    }

    public boolean isInfinite() {
        return false;
    }

    public boolean isNaN() {
        return true;
    }

    // --------------------------------------------------------------------------------------------------------------
    // Operations (Utils)
    // --------------------------------------------------------------------------------------------------------------

    public Num add(Num num1, Num num2) {
        return instance;
    }

    public Num sub(Num num1, Num num2) {
        return instance;
    }

    public Num mult(Num num1, Num num2) {
        return instance;
    }

    public Num div(Num num1, Num num2) {
        return instance;
    }

    public Num abs(Num num) {
        return instance;
    }

    public Num diff(Num num1, Num num2) {
        return instance;
    }

    public Num max(Num num1, Num num2) {
        return instance;
    }

    public Num min(Num num1, Num num2) {
        return instance;
    }

    public Num negate(Num num) {
        return instance;
    }
}
