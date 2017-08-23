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

package de.uni_kl.cs.disco.curves;

import de.uni_kl.cs.disco.curves.dnc.LinearSegment_DNC;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.LinearSegment_MPARTC_PwAffine;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;

public interface LinearSegment {
	// --------------------------------------------------------------------------------------------------------------
	// Interface
	// --------------------------------------------------------------------------------------------------------------
	Num f(Num x);

	Num getX();

	void setX(Num x);

	Num getY();

	void setY(Num y);

	Num getGrad();

	void setGrad(Num grad);

	boolean isLeftopen();

	void setLeftopen(boolean leftopen);

	Num getXIntersectionWith(LinearSegment other);

	LinearSegment copy();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	@Override
	String toString();

	// --------------------------------------------------------------------------------------------------------------
	// Factory
	// --------------------------------------------------------------------------------------------------------------
	// In contrast to the Curve factory, it does not dispatch to other factories.

	static LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
		switch (CalculatorConfig.getInstance().getCurveImpl()) {
		case MPA_RTC:
			return new LinearSegment_MPARTC_PwAffine(x.doubleValue(), y.doubleValue(), grad.doubleValue());
		case DNC:
		default:
			return new LinearSegment_DNC(x, y, grad, leftopen);
		}
	}

	static LinearSegment createHorizontalLine(double y) {
		switch (CalculatorConfig.getInstance().getCurveImpl()) {
		case MPA_RTC:
			return new LinearSegment_MPARTC_PwAffine(0.0, 0.0, 0.0);
		case DNC:
		default:
			return new LinearSegment_DNC(Num.getFactory().createZero(),
					Num.getFactory().createZero(), Num.getFactory().createZero(), false);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------
	// Utils
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Helper creating a new segment starting at x that is the sum of the given
	 * getSegment.
	 *
	 * @param s1
	 *            Segment 1.
	 * @param s2
	 *            Segment 2.
	 * @param x
	 *            New x-coordinate the start at.
	 * @param leftopen
	 *            Set the segment to be left-open.
	 * @return The new linear segment, pointwise sum of the given ones, starting in
	 *         x.
	 */
	static LinearSegment add(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen) {
		LinearSegment result = createHorizontalLine(0.0);
		result.setX(x);
		result.setY(Num.getUtils().add(s1.f(x), s2.f(x)));
		result.setGrad(Num.getUtils().add(s1.getGrad(), s2.getGrad()));
		result.setLeftopen(leftopen);
		return result;
	}

	/**
	 * Helper creating a new segment starting at x that is the difference between
	 * the given getSegment.
	 *
	 * @param s1
	 *            Segment 1.
	 * @param s2
	 *            Segment 2.
	 * @param x
	 *            New x-coordinate the start at.
	 * @param leftopen
	 *            Set the segment to be left-open.
	 * @return The new linear segment, pointwise difference of the given ones, i.e.,
	 *         s1 - s2, starting in x.
	 */
	static LinearSegment sub(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen) {
		LinearSegment result = createHorizontalLine(0.0);
		result.setX(x);
		result.setY(Num.getUtils().sub(s1.f(x), s2.f(x)));
		result.setGrad(Num.getUtils().sub(s1.getGrad(), s2.getGrad()));
		result.setLeftopen(leftopen);
		return result;
	}

	/**
	 * Helper creating a new segment starting at x that is the minimum of the given
	 * getSegment. Note: Only valid if s1 and s2 do not intersect before the next
	 * IP.
	 *
	 * @param s1
	 *            Segment 1.
	 * @param s2
	 *            Segment 2.
	 * @param x
	 *            New x-coordinate the start at.
	 * @param leftopen
	 *            Set the segment to be left-open.
	 * @param crossed
	 *            Provides information if the segments intersect.
	 * @return The new linear segment, pointwise minimum of the given ones, starting
	 *         in x.
	 */
	static LinearSegment min(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen, boolean crossed) {
		Num f1_x = s1.f(x);
		Num f2_x = s2.f(x);

		LinearSegment result = createHorizontalLine(0.0);
		result.setX(x);
		if (crossed || Num.getUtils().abs(Num.getUtils().sub(f1_x, f2_x))
				.lt(Num.getFactory().getEpsilon())) {
			result.setY(f1_x);
			result.setGrad(Num.getUtils().min(s1.getGrad(), s2.getGrad()));
		} else if (f1_x.lt(f2_x)) {
			result.setY(f1_x);
			result.setGrad(s1.getGrad());
		} else {
			result.setY(f2_x);
			result.setGrad(s2.getGrad());
		}
		result.setLeftopen(leftopen);
		return result;
	}

	/**
	 * Helper creating a new segment starting at x that is the maximum of the given
	 * segments. Note: Only valid if s1 and s2 do not intersect before the next IP.
	 *
	 * @param s1
	 *            Segment 1.
	 * @param s2
	 *            Segment 2.
	 * @param x
	 *            New x-coordinate the start at.
	 * @param leftopen
	 *            Set the segment to be left-open.
	 * @param crossed
	 *            Provides information if the segments intersect.
	 * @return The new linear segment, pointwise maximum of the given ones, starting
	 *         in x.
	 */
	static LinearSegment max(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen, boolean crossed) {
		Num f1_x = s1.f(x);
		Num f2_x = s2.f(x);

		LinearSegment result = createHorizontalLine(0.0);
		result.setX(x);
		if (crossed || Num.getUtils().abs(Num.getUtils().sub(f1_x, f2_x))
				.lt(Num.getFactory().getEpsilon())) {
			result.setY(f1_x);
			result.setGrad(Num.getUtils().max(s1.getGrad(), s2.getGrad()));
		} else if (f1_x.gt(f2_x)) {
			result.setY(f1_x);
			result.setGrad(s1.getGrad());
		} else {
			result.setY(f2_x);
			result.setGrad(s2.getGrad());
		}
		result.setLeftopen(leftopen);
		return result;
	}
}
