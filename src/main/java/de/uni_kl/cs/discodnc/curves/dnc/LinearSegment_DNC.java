/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0 "Chimera".
 *
 * Copyright (C) 2005 - 2007 Frank A. Zdarsky
 * Copyright (C) 2012 - 2016 Steffen Bondorf
 * Copyright (C) 2017, 2018 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
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

package de.uni_kl.cs.discodnc.curves.dnc;

import de.uni_kl.cs.discodnc.curves.LinearSegment;
import de.uni_kl.cs.discodnc.numbers.Num;

/**
 * Class representing linear segments of a curve. A linear segments starts at
 * point (<code>x0</code>, <code>y0</code>) and continues to the right with
 * slope <code>grad</code>. If <code>leftopen</code> is <code>true</code>, the
 * point (<code>x0</code>,<code>y0</code>) is excluded from the segment,
 * otherwise is included.
 */
public class LinearSegment_DNC implements LinearSegment {
    /**
     * The x-coordinate of the linear segment's starting point.
     */
    protected Num x;

    /**
     * The y-coordinate of the linear segment's starting point.
     */
    protected Num y;

    /**
     * The gradient of the linear segment
     */
    protected Num grad;

    /**
     * Whether the point (<code>x0</code>, <code>y0</code>) is part of the segment
     * or not.
     */
    protected boolean leftopen;

    // --------------------------------------------------------------------------------------------------------------
    // Constructors
    // --------------------------------------------------------------------------------------------------------------

    /**
     * The default constructor.
     */
    protected LinearSegment_DNC() {
        x = Num.getFactory().createZero();
        y = Num.getFactory().createZero();
        grad = Num.getFactory().createZero();
        leftopen = false;
    }

    /**
     * A convenient constructor.
     *
     * @param x        The x-coordinate this segments starts at.
     * @param y        The y-coordinate this segments starts at.
     * @param grad     The segments gradient.
     * @param leftopen Set the segment to be left-open.
     */
    public LinearSegment_DNC(Num x, Num y, Num grad, boolean leftopen) {
        this.x = x;
        this.y = y;
        this.grad = grad;
        this.leftopen = leftopen;
    }

    public LinearSegment_DNC(LinearSegment segment) {
        x = segment.getX();
        y = segment.getY();
        grad = segment.getGrad();
        leftopen = segment.isLeftopen();
    }

    public LinearSegment_DNC(String segment_str) throws Exception {
        // Is this segment left-open?
        leftopen = false;
        switch (segment_str.charAt(0)) {
            case '!':
                leftopen = true;
                segment_str = segment_str.substring(1);
                break;
            case '(':
                // Formatting seems to be ok (for now at least)
                break;
            default:
                throw new RuntimeException("Invalid string representation of a linear segment.");
        }

        // Remove the bracket at the front
        segment_str = segment_str.substring(1);

        String[] xy_r = segment_str.split("\\),"); // ["x,y","grad"]
        if (xy_r.length != 2) {
            throw new RuntimeException("Invalid string representation of a linear segment.");
        }

        String[] x_y = xy_r[0].split(",");
        if (x_y.length != 2) {
            throw new RuntimeException("Invalid string representation of a linear segment.");
        }

        x = Num.getFactory().create(x_y[0]);
        y = Num.getFactory().create(x_y[1]);
        grad = Num.getFactory().create(xy_r[1]);
    }

    // --------------------------------------------------------------------------------------------------------------
    // Interface Implementations
    // --------------------------------------------------------------------------------------------------------------

    /**
     * Returns the function value of this linear segment at the given x-coordinate.
     * Note that there is no test whether the function is defined at this location,
     * but simply returns the the value of the co-linear line.
     *
     * @param x the coordinate whose function value shall be returned
     * @return the function value
     */
    public Num f(Num x) {
        return Num.getUtils().add(Num.getUtils().mult(Num.getUtils().sub(x, this.x), grad), y);
    }

    public Num getX() {
        return x.copy();
    }

    public void setX(Num x) {
        this.x = x.copy();
    }

    public Num getY() {
        return y.copy();
    }

    public void setY(Num y) {
        this.y = y.copy();
    }

    public Num getGrad() {
        return grad.copy();
    }

    public void setGrad(Num grad) {
        this.grad = grad.copy();
    }

    public boolean isLeftopen() {
        return leftopen;
    }

    public void setLeftopen(boolean leftopen) {
        this.leftopen = leftopen;
    }

    /**
     * Returns the x-coordinate at which a co-linear line through this segment
     * intersects a co-linear line through the segment <code>other</code>.
     *
     * @param other the other segment
     * @return the x-coordinate at which the segments cross or NaN of they are
     * parallel
     */
    public Num getXIntersectionWith(LinearSegment other) {
        Num y1 = Num.getUtils().sub(this.y, Num.getUtils().mult(x, this.grad));
        Num y2 = Num.getUtils().sub(other.getY(), Num.getUtils().mult(other.getX(), other.getGrad()));

        // returns NaN if lines are parallel
        return Num.getUtils().div(Num.getUtils().sub(y2, y1),
                Num.getUtils().sub(this.grad, other.getGrad()));
    }

    /**
     * Returns a copy of this instance.
     *
     * @return a copy of this instance.
     */
    @Override
    public LinearSegment_DNC copy() {
        LinearSegment_DNC copy = new LinearSegment_DNC(x.copy(), y.copy(), grad.copy(), leftopen);
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LinearSegment_DNC)) {
            return false;
        }

        LinearSegment_DNC other = (LinearSegment_DNC) obj;
        boolean result;
        result = this.x.equals(other.getX());
        result = result && this.y.equals(other.getY());
        result = result && this.grad.equals(other.getGrad());
        result = result && (this.leftopen == other.isLeftopen());

        return result;
    }

    @Override
    public int hashCode() {
        return x.hashCode() * y.hashCode() * grad.hashCode() * Boolean.hashCode(leftopen);
    }

    /**
     * Returns a string representation of this linear segment.
     *
     * @return the linear segment represented as a string.
     */
    @Override
    public String toString() {
        String result = "";
        if (this.leftopen) {
            result = "!";
        }
        result += "(" + x.toString() + "," + y.toString() + ")," + grad.toString();

        return result;
    }
}
