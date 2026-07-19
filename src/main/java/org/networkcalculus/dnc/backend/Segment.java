package org.networkcalculus.dnc.backend;

import org.networkcalculus.dnc.backend.number.Number;

import java.util.LinkedList;
import java.util.List;

/* Affine nondecreasing semi-closed segment on half-open interval (startTime, endTime].
 * See [1] Section 3.2, Definition 4. */
public class Segment {
    private Number startTime;
    private Number endTime;
    private Number rightLimitAtStartTime;
    private Number slope;

    public Number getStartTime() {
        return startTime;
    }

    public Number getEndTime() {
        return endTime;
    }

    /**
     *
     * @param startTime             Start of segment interval, not included
     * @param endTime               End of segment interval, included
     * @param rightLimitAtStartTime
     * @param slope
     */
    public Segment(
            Number startTime,
            Number endTime,
            Number rightLimitAtStartTime,
            Number slope) {
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setRightLimitAtStartTime(rightLimitAtStartTime);
        this.setSlope(slope);
    }

    /* Value at time as defined by [1]. */
    public Number getValue(Number time) {
        if (getStartTime().lt(time) && time.leq(getEndTime()))
            return time.sub(getStartTime()).mul(getSlope()).add(getRightLimitAtStartTime());
        return Number.POSITIVE_INFINITY;
    }

    /* Value at time as if the segment had no ends. */
    protected Number getRightLimitValue(Number time) {
        return time.sub(getStartTime()).mul(getSlope()).add(getRightLimitAtStartTime());
    }

    /* Rightmost time of value. */
    public Number getTime(Number value) {
        if (getSlope().eq(Number.ZERO))
            return value.eq(getRightLimitAtStartTime()) ? getEndTime() : null;
        return value.sub(getRightLimitAtStartTime()).div(getSlope()).add(getStartTime());
    }

    public String toString() {
        return "(startTime: " + getStartTime() + ", endTime: " + getEndTime()
                + ", rightLimitAtStartTime: " + getRightLimitAtStartTime()
                + ", slope: " + getSlope() + ")";
    }

    /* Extract section with interval (startTime, endTime] from segment. */
    public Segment cut(Number startTime, Number endTime) {
        startTime = this.getStartTime().max(startTime);
        return new Segment(
                startTime,
                this.getEndTime().min(endTime),
                getRightLimitValue(startTime),
                getSlope());
    }

    /* Sum of two segments. */
    public static Segment addition(Segment a, Segment b) {
        Number startTime = a.getStartTime().max(b.getStartTime());

        return new Segment(
                startTime,
                a.getEndTime().min(b.getEndTime()),
                a.getRightLimitValue(startTime).add(b.getRightLimitValue(startTime)),
                a.getSlope().add(b.getSlope()));
    }

    /*
     * Minimum of two segments. Note that nonoverlapping parts are also defined and
     * returned.
     */
    public static List<Segment> minimum(Segment a, Segment b) {
        Number startTime = a.getStartTime().max(b.getStartTime());
        Number endTime = a.getEndTime().min(b.getEndTime());
        Number aRightLimitAtStartTime = a.getRightLimitValue(startTime);
        Number aEndValue = a.getValue(endTime);
        Number bRightLimitAtStartTime = b.getRightLimitValue(startTime);
        Number bEndValue = b.getValue(endTime);
        List<Segment> minimum = new LinkedList<Segment>();

        /* Segments do not intersect */
        if (aRightLimitAtStartTime.leq(bRightLimitAtStartTime)
                && aEndValue.leq(bEndValue)) {
            if (b.getStartTime().lt(startTime))
                minimum.add(new Segment(
                        b.getStartTime(),
                        startTime,
                        b.getRightLimitAtStartTime(),
                        b.getSlope()));
            minimum.add(a);
            if (b.getEndTime().lt(endTime))
                minimum.add(new Segment(endTime, b.getEndTime(), bEndValue, b.getSlope()));
            return minimum;
        }
        if (aRightLimitAtStartTime.geq(bRightLimitAtStartTime)
                && aEndValue.geq(bEndValue)) {
            if (a.getStartTime().lt(startTime))
                minimum.add(new Segment(
                        a.getStartTime(),
                        startTime,
                        a.getRightLimitAtStartTime(),
                        a.getSlope()));
            minimum.add(b);
            if (a.getEndTime().gt(endTime))
                minimum.add(new Segment(endTime, a.getEndTime(), aEndValue, a.getSlope()));
            return minimum;
        }

        /* Segments intersect */
        Number intersectTime = aRightLimitAtStartTime.neg().add(bRightLimitAtStartTime)
                .div(a.getSlope().sub(b.getSlope())).add(startTime);
        if (a.getStartTime().lt(startTime))
            minimum.add(new Segment(
                    a.getStartTime(),
                    startTime,
                    a.getRightLimitAtStartTime(),
                    a.getSlope()));
        if (b.getStartTime().lt(startTime))
            minimum.add(new Segment(
                    b.getStartTime(),
                    startTime,
                    b.getRightLimitAtStartTime(),
                    b.getSlope()));
        if (aRightLimitAtStartTime.lt(bRightLimitAtStartTime)) {
            minimum.add(new Segment(
                    startTime,
                    intersectTime,
                    aRightLimitAtStartTime,
                    a.getSlope()));
            minimum.add(new Segment(
                    intersectTime,
                    endTime,
                    b.getValue(intersectTime),
                    b.getSlope()));
        } else {
            minimum.add(new Segment(
                    startTime,
                    intersectTime,
                    bRightLimitAtStartTime,
                    b.getSlope()));
            minimum.add(new Segment(
                    intersectTime,
                    endTime,
                    a.getValue(intersectTime),
                    a.getSlope()));
        }
        if (endTime.lt(a.getEndTime()))
            minimum.add(new Segment(
                    endTime,
                    a.getEndTime(),
                    a.getRightLimitAtStartTime(),
                    a.getSlope()));
        if (endTime.lt(b.getEndTime()))
            minimum.add(new Segment(
                    endTime,
                    b.getEndTime(),
                    b.getRightLimitAtStartTime(),
                    b.getSlope()));
        return minimum;
    }

    /* Maximum of two segments. */
    public static List<Segment> maximum(Segment a, Segment b) {
        Number startTime = a.getStartTime().max(b.getStartTime());
        Number endTime = a.getEndTime().min(b.getEndTime());
        Number aRightLimitAtStartTime = a.getRightLimitValue(startTime);
        Number aEndValue = a.getValue(endTime);
        Number bRightLimitAtStartTime = b.getRightLimitValue(startTime);
        Number bEndValue = b.getValue(endTime);
        List<Segment> maximum = new LinkedList<Segment>();

        /* Segments do not intersect */
        if (aRightLimitAtStartTime.geq(bRightLimitAtStartTime)
                && aEndValue.geq(bEndValue)) {
            maximum.add(new Segment(
                    startTime,
                    endTime,
                    aRightLimitAtStartTime,
                    a.getSlope()));
            return maximum;
        }
        if (aRightLimitAtStartTime.leq(bRightLimitAtStartTime)
                && aEndValue.leq(bEndValue)) {
            maximum.add(new Segment(
                    startTime,
                    endTime,
                    bRightLimitAtStartTime,
                    b.getSlope()));
            return maximum;
        }

        /* Segments intersect */
        Number intersectTime = aRightLimitAtStartTime.neg().add(bRightLimitAtStartTime)
                .div(a.getSlope().sub(b.getSlope())).add(startTime);
        if (aRightLimitAtStartTime.lt(bRightLimitAtStartTime)) {
            maximum.add(new Segment(
                    startTime,
                    intersectTime,
                    bRightLimitAtStartTime,
                    b.getSlope()));
            maximum.add(new Segment(
                    intersectTime,
                    endTime,
                    a.getValue(intersectTime),
                    a.getSlope()));
            return maximum;
        }
        maximum.add(new Segment(startTime, intersectTime, aRightLimitAtStartTime, a.getSlope()));
        maximum.add(new Segment(
                intersectTime,
                endTime,
                b.getValue(intersectTime),
                b.getSlope()));
        return maximum;
    }

    /* Convolution of two segments. See [1] Section 3.2.1, Lemma 4. */
    public static List<Segment> convolution(Segment a, Segment b) {
        Number startTime = a.getStartTime().add(b.getStartTime());
        Number endTime = a.getEndTime().add(b.getEndTime());
        Number rightLimitAtStartTime = a.getRightLimitAtStartTime().add(b.getRightLimitAtStartTime());
        List<Segment> convolution = new LinkedList<Segment>();

        if (a.getSlope().leq(b.getSlope())) {
            Number transitionTime = a.getEndTime().add(b.getStartTime());
            convolution.add(new Segment(
                    startTime,
                    transitionTime,
                    rightLimitAtStartTime,
                    a.getSlope()));
            convolution.add(new Segment(
                    transitionTime,
                    endTime,
                    a.getValue(a.getEndTime()).add(b.getRightLimitAtStartTime()),
                    b.getSlope()));
        } else {
            Number transitionTime = a.getStartTime().add(b.getEndTime());
            convolution.add(new Segment(
                    startTime,
                    transitionTime,
                    rightLimitAtStartTime,
                    b.getSlope()));
            convolution.add(new Segment(
                    transitionTime,
                    endTime,
                    a.getRightLimitAtStartTime().add(b.getValue(b.getEndTime())),
                    a.getSlope()));
        }
        return convolution;
    }

    /* Deconvolution of two segments. See [1] Section 3.2.2, Lemma 8. */
    public static List<Segment> deconvolution(Segment a, Segment b) {
        Number startTime = a.getStartTime().sub(b.getEndTime());
        Number endTime = a.getEndTime().sub(b.getStartTime());
        Number rightLimitAtStartTime = a.getRightLimitAtStartTime().sub(b.getValue(b.getEndTime()));
        Number endValue = a.getValue(a.getEndTime()).sub(b.getRightLimitAtStartTime());
        Segment minimal = new Segment(
                Number.ZERO.max(startTime),
                endTime,
                Number.ZERO,
                Number.ZERO);
        List<Segment> deconvolution = new LinkedList<Segment>();

        if (endTime.leq(Number.ZERO))
            return deconvolution;
        if (endValue.leq(Number.ZERO)) {
            deconvolution.add(minimal);
            return deconvolution;
        }

        if (a.getSlope().leq(b.getSlope())) {
            Number transitionTime = a.getStartTime().sub(b.getStartTime());
            if (transitionTime.gt(Number.ZERO))
                deconvolution.addAll(Segment.maximum(minimal, new Segment(
                        startTime,
                        transitionTime,
                        rightLimitAtStartTime,
                        b.getSlope()).cut(Number.ZERO, transitionTime)));
            deconvolution.addAll(Segment.maximum(minimal, new Segment(
                    transitionTime,
                    endTime, a.getRightLimitAtStartTime().sub(b.getRightLimitAtStartTime()),
                    a.getSlope()).cut(Number.ZERO, endTime)));
        } else {
            Number transitionTime = a.getEndTime().sub(b.getEndTime());
            if (transitionTime.gt(Number.ZERO))
                deconvolution.addAll(Segment.maximum(minimal, new Segment(
                        startTime,
                        transitionTime,
                        rightLimitAtStartTime,
                        a.getSlope()).cut(Number.ZERO, transitionTime)));
            deconvolution.addAll(Segment.maximum(minimal, new Segment(
                    transitionTime,
                    endTime,
                    a.getValue(a.getEndTime()).sub(b.getValue(b.getEndTime())),
                    b.getSlope()).cut(Number.ZERO, endTime)));
        }
        return deconvolution;
    }

    /* Delay of arrival and service segments. */
    public static Number delay(Segment arrival, Segment service) {
        Number startValue = arrival.getRightLimitAtStartTime()
                .max(service.getRightLimitAtStartTime());
        Number endValue = arrival.getValue(arrival.getEndTime())
                .min(service.getValue(service.getEndTime()));

        if (startValue.gt(endValue))
            return Number.NEGATIVE_INFINITY;
        return Number.ZERO.max(service.getTime(startValue).sub(arrival.getTime(startValue))
                .max(service.getTime(startValue).sub(arrival.getTime(endValue))));


    }

    public static Number backlog(Segment arrival, Segment service) {
        Number startTime = arrival.getStartTime().max(service.getStartTime());
        Number endTime = arrival.getEndTime().min(service.getEndTime());
        if (startTime.gt(endTime))
            return Number.NEGATIVE_INFINITY;
        return Number.ZERO.max(arrival.getRightLimitValue(startTime).sub(
                service.getRightLimitValue(startTime)).max(
                arrival.getRightLimitValue(endTime).sub(
                        service.getRightLimitValue(endTime))));
    }

    public static Segment verticalShift(Segment segment, Number shiftValue) {
        if (segment.getRightLimitAtStartTime().add(shiftValue).leq(Number.ZERO)) {
            return new Segment(segment.getStartTime(), segment.getEndTime(), Number.ZERO, segment.getSlope());
        }
        return new Segment(segment.getStartTime(), segment.getEndTime(), segment.getRightLimitAtStartTime().add(shiftValue),
                segment.getSlope());
    }

    public static Segment horizontalShift(Segment segment, Number shiftValue) {
        return new Segment(segment.getStartTime().add(shiftValue).max(Number.ZERO),
                segment.getEndTime().add(shiftValue).max(Number.ZERO), segment.getRightLimitAtStartTime(), segment.getSlope());
    }

    /**
     *
     * @param segment1
     * @param segment2
     * @return x-value of intersection of two segments. Null if no intersection.
     */
    public static Number getIntersection(Segment segment1, Segment segment2) {
        Number intervalStart = segment1.getStartTime().max(segment2.getStartTime());
        Number intervalEnd = segment1.getEndTime().min(segment2.getEndTime());

        if (intervalStart.gt(intervalEnd)) {
            return null; // No overlap in time domain
        }

        // Line equations: y = s * (t - startTime) + startValue
        // Rewrite in y = m*t + c form for both segments
        Number m1 = segment1.getSlope();
        Number c1 = segment1.getRightLimitAtStartTime().sub(segment1.getSlope().mul(segment1.getStartTime()));

        Number m2 = segment2.getSlope();
        Number c2 = segment2.getRightLimitAtStartTime().sub(segment2.getSlope().mul(segment2.getStartTime()));


        if (m1.eq(m2)) {
            if (c1.eq(c2)) {
                return new Number(intervalStart); // Coincident: return start of overlap
            } else {
                return null; // Parallel: never intersect
            }
        }

        // Compute time t where a(t) == b(t)
        Number t = (c2.sub(c1)).div(m1.sub(m2));
        if (t.gt(intervalEnd) || t.lt(intervalStart))
            return null; // Intersection outside of segment range
        return new Number(t);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Segment other = (Segment) obj;
        if (getStartTime() == null) {
            if (other.getStartTime() != null)
                return false;
        } else if (!getStartTime().eq(other.getStartTime()))
            return false;
        if (getEndTime() == null) {
            if (other.getEndTime() != null)
                return false;
        } else if (!getEndTime().eq(other.getEndTime()))
            return false;
        if (getRightLimitAtStartTime() == null) {
            if (other.getRightLimitAtStartTime() != null)
                return false;
        } else if (!getRightLimitAtStartTime().eq(other.getRightLimitAtStartTime()))
            return false;
        if (getSlope() == null) {
            if (other.getSlope() != null)
                return false;
        } else if (!getSlope().eq(other.getSlope()))
            return false;
        return true;
    }

    public Number getSlope() {
        return this.slope;
    }

    public void setStartTime(Number startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Number endTime) {
        this.endTime = endTime;
    }

    public void setRightLimitAtStartTime(Number rightLimitAtStartTime) {
        this.rightLimitAtStartTime = rightLimitAtStartTime;
    }

    public void setSlope(Number slope) {
        this.slope = slope;
    }

    public Number getRightLimitAtStartTime() {
        return rightLimitAtStartTime;
    }

    public static boolean segmentsOverlap(Segment segment1, Segment segment2) {
        Number intervalStart = segment1.getStartTime().max(segment2.getStartTime());
        Number intervalEnd = segment1.getEndTime().min(segment2.getEndTime());

        if (intervalStart.gt(intervalEnd)) {
            return false;
        }

        return true;
    }
}
