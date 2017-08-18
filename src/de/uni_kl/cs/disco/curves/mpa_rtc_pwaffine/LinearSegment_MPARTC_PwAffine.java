/*
 * This file is part of the Disco Deterministic Network Calculator v2.4.0beta3 "Chimera".
 *
 * Copyright (C) 2017 The DiscoDNC contributors
 *
 * disco | Distributed Computer Systems Lab
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

package de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine;

import ch.ethz.rtc.kernel.Segment;
import de.uni_kl.cs.disco.curves.LinearSegment;
import de.uni_kl.cs.disco.numbers.Num;
import de.uni_kl.cs.disco.numbers.NumFactory;
import de.uni_kl.cs.disco.numbers.NumUtilsDispatch;

public class LinearSegment_MPARTC_PwAffine implements LinearSegment {

	private ch.ethz.rtc.kernel.Segment rtc_segment;

	
//--------------------------------------------------------------------------------------------------------------
// Constructors
//--------------------------------------------------------------------------------------------------------------
    public LinearSegment_MPARTC_PwAffine(double x, double y, double s) {
        rtc_segment = new Segment(x, y, s);
    }

    public LinearSegment_MPARTC_PwAffine(LinearSegment segment) {
        if (segment instanceof LinearSegment_MPARTC_PwAffine) {
            rtc_segment = ((LinearSegment_MPARTC_PwAffine) segment).rtc_segment.clone();
        } else {
            rtc_segment = new Segment(segment.getX().doubleValue(),
                    segment.getY().doubleValue(),
                    segment.getGrad().doubleValue());
        }
    }

    public LinearSegment_MPARTC_PwAffine(Segment segment) {
        rtc_segment = segment.clone();
    }

    public LinearSegment_MPARTC_PwAffine(String segment_str) {
        rtc_segment = new Segment(segment_str);
    }

    // Setter in order to prevent copy bug
    protected void setRtc_segment(Segment rtc_segment) {
        this.rtc_segment = rtc_segment;
    }

    
//--------------------------------------------------------------------------------------------------------------
// Interface Implementations
//--------------------------------------------------------------------------------------------------------------
    public Num f(Num x) {
        return NumFactory.getNumFactory().create(rtc_segment.yAt(x.doubleValue()));
    }

    public Num getX() {
        return NumFactory.getNumFactory().create(rtc_segment.x());
    }

    public void setX(double x) {
        rtc_segment.setX(x);
    }

    public void setX(Num x) {
        rtc_segment.setX(x.doubleValue());
    }

    public Num getY() {
        return NumFactory.getNumFactory().create(rtc_segment.y());
    }

    public void setY(double y) {
        rtc_segment.setY(y);
    }

    public void setY(Num y) {
        rtc_segment.setY(y.doubleValue());
    }

    public Num getGrad() {
        return NumFactory.getNumFactory().create(rtc_segment.s());
    }

    public void setGrad(Num grad) {
        rtc_segment.setS(grad.doubleValue());
    }

    /**
     * MPA RTC implementation does not allow for user defined continuity.
     * Left-continuity is assumed by default. 
     */
    public boolean isLeftopen() {
        return true;
    }

    /**
     * MPA RTC implementation does not allow for user defined continuity.
     * Left-continuity is assumed by default. 
     */
    public void setLeftopen(boolean leftopen) {}

    public Num getXIntersectionWith(LinearSegment other) {
        Num y1 = NumFactory.getNumFactory().create(rtc_segment.y() - (rtc_segment.x() * rtc_segment.s()));
        Num y2 = NumUtilsDispatch.sub(other.getY(), NumUtilsDispatch.mult(other.getX(), other.getGrad()));

        // returns NaN if lines are parallel
        return NumUtilsDispatch.div(NumUtilsDispatch.sub(y2, y1), NumUtilsDispatch.sub(this.getGrad(), other.getGrad()));
    }

    @Override
    public LinearSegment copy() {
        return new LinearSegment_MPARTC_PwAffine(rtc_segment);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof LinearSegment_MPARTC_PwAffine)) {
            return false;
        }
        // TODO Findbugs: EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS
        return rtc_segment.equals(obj);
    }

    @Override
    public String toString() {
        return rtc_segment.toString();
    }

    @Override
    public int hashCode() {
        return rtc_segment.hashCode();
    }
}
