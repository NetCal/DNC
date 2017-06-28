/*
 * This file is part of the Disco Deterministic Network Calculator v2.3.5 "Centaur".
 *
 * Copyright (C) 2017 Steffen Bondorf
 *
 * Distributed Computer Systems (DISCO) Lab
 * University of Kaiserslautern, Germany
 *
 * http://disco.cs.uni-kl.de
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package unikl.disco.numbers.implementations;

import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumUtilsInterface;

public class RealDoubleUtils implements NumUtilsInterface {
    public Num add(Num num1, Num num2) {
        return RealDouble.add((RealDouble) num1, (RealDouble) num2);
    }

    public Num sub(Num num1, Num num2) {
        return RealDouble.sub((RealDouble) num1, (RealDouble) num2);
    }

    public Num mult(Num num1, Num num2) {
        return RealDouble.mult((RealDouble) num1, (RealDouble) num2);
    }

    public Num div(Num num1, Num num2) {
        return RealDouble.div((RealDouble) num1, (RealDouble) num2);
    }

    public Num abs(Num num) {
        return RealDouble.abs((RealDouble) num);
    }

    public Num diff(Num num1, Num num2) {
        return RealDouble.diff((RealDouble) num1, (RealDouble) num2);
    }

    public Num max(Num num1, Num num2) {
        return RealDouble.max((RealDouble) num1, (RealDouble) num2);
    }

    public Num min(Num num1, Num num2) {
        return RealDouble.min((RealDouble) num1, (RealDouble) num2);
    }

    public Num negate(Num num) {
        return RealDouble.negate((RealDouble) num);
    }

    public boolean isFinite(Num num) {
        return ((RealDouble) num).isFinite();
    }

    public boolean isInfinite(Num num) {
        return ((RealDouble) num).isInfinite();
    }

    public boolean isNaN(Num num) {
        return ((RealDouble) num).isNaN();
    }
}
