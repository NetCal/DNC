package unikl.disco.curves;

import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;
import unikl.disco.numbers.NumUtils;

public class LinearSegmentUtils {
    public static LinearSegment getXAxis() {
        return LinearSegmentFactory.createHorizontalLine(0.0);
    }

    /**
     * Helper creating a new segment starting at x that is the sum of the given getSegment.
     *
     * @param s1       Segment 1.
     * @param s2       Segment 2.
     * @param x        New x-coordinate the start at.
     * @param leftopen Set the segment to be left-open.
     * @return The new linear segment, pointwise sum of the given ones, starting in x.
     */
    protected static LinearSegment add(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen) {
        LinearSegment result = LinearSegmentFactory.createHorizontalLine(0.0);
        result.setX(x);
        result.setY(NumUtils.add(s1.f(x), s2.f(x)));
        result.setGrad(NumUtils.add(s1.getGrad(), s2.getGrad()));
        result.setLeftopen(leftopen);
        return result;
    }

    /**
     * Helper creating a new segment starting at x that is the difference between the given getSegment.
     *
     * @param s1       Segment 1.
     * @param s2       Segment 2.
     * @param x        New x-coordinate the start at.
     * @param leftopen Set the segment to be left-open.
     * @return The new linear segment, pointwise difference of the given ones, i.e., s1 - s2, starting in x.
     */
    public static LinearSegment sub(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen) {
        LinearSegment result = LinearSegmentFactory.createHorizontalLine(0.0);
        result.setX(x);
        result.setY(NumUtils.sub(s1.f(x), s2.f(x)));
        result.setGrad(NumUtils.sub(s1.getGrad(), s2.getGrad()));
        result.setLeftopen(leftopen);
        return result;
    }

    /**
     * Helper creating a new segment starting at x that is the minimum of the given getSegment.
     * Note: Only valid if s1 and s2 do not intersect before the next IP.
     *
     * @param s1       Segment 1.
     * @param s2       Segment 2.
     * @param x        New x-coordinate the start at.
     * @param leftopen Set the segment to be left-open.
     * @param crossed  Provides information if the segments intersect.
     * @return The new linear segment, pointwise minimum of the given ones, starting in x.
     */
    public static LinearSegment min(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen, boolean crossed) {
        Num f1_x = s1.f(x);
        Num f2_x = s2.f(x);

        LinearSegment result = LinearSegmentFactory.createHorizontalLine(0.0);
        result.setX(x);
        if (crossed || NumUtils.abs(NumUtils.sub(f1_x, f2_x)).lt(NumFactory.getEpsilon())) {
            result.setY(f1_x);
            result.setGrad(NumUtils.min(s1.getGrad(), s2.getGrad()));
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
     * Helper creating a new segment starting at x that is the maximum of the given segments.
     * Note: Only valid if s1 and s2 do not intersect before the next IP.
     *
     * @param s1       Segment 1.
     * @param s2       Segment 2.
     * @param x        New x-coordinate the start at.
     * @param leftopen Set the segment to be left-open.
     * @param crossed  Provides information if the segments intersect.
     * @return The new linear segment, pointwise maximum of the given ones, starting in x.
     */
    public static LinearSegment max(LinearSegment s1, LinearSegment s2, Num x, boolean leftopen, boolean crossed) {
        Num f1_x = s1.f(x);
        Num f2_x = s2.f(x);

        LinearSegment result = LinearSegmentFactory.createHorizontalLine(0.0);
        result.setX(x);
        if (crossed || NumUtils.abs(NumUtils.sub(f1_x, f2_x)).lt(NumFactory.getEpsilon())) {
            result.setY(f1_x);
            result.setGrad(NumUtils.max(s1.getGrad(), s2.getGrad()));
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
