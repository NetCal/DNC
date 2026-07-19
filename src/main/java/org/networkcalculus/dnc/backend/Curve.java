package org.networkcalculus.dnc.backend;

import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

public class Curve {
    public Sequence baseSequence;
    private Number pseudoPeriodStart, pseudoPeriodLength, pseudoPeriodHeight;

    public Curve(
            Sequence baseSequence,
            Number pseudoPeriodStart,
            Number pseudoPeriodLength,
            Number pseudoPeriodHeight) {
        this.baseSequence = baseSequence;
        this.pseudoPeriodStart = pseudoPeriodStart;
        this.pseudoPeriodLength = pseudoPeriodLength;
        this.pseudoPeriodHeight = pseudoPeriodHeight;
    }

    /*
     *
     * @param curve non-negative Curve
     * @param limit Right limit of t for area under the curve to avoid infite values
     * @return Number area
     */

    public Number areaUnderCurve(Curve curve, Number limit) {
        double area = 0.0;
        for (Segment segment : curve.getSequence().getSegments()) {
            if (segment.getEndTime().leq(limit)) {
                double m = segment.getSlope().toDouble();
                double t0 = segment.getStartTime().toDouble();
                double t1 = segment.getEndTime().toDouble();
                double b = segment.getRightLimitAtStartTime().toDouble();
                area += ((m * Math.pow(t1, 2)) / 2) + (b * t1) - ((m * Math.pow(t0, 2)) / 2) - (b * t0);
            } else {
                double m = segment.getSlope().toDouble();
                double t0 = segment.getStartTime().toDouble();
                double t1 = limit.toDouble();
                double b = segment.getRightLimitAtStartTime().toDouble();
                area += ((m * Math.pow(t1, 2)) / 2) + (b * t1) - ((m * Math.pow(t0, 2)) / 2) - (b * t0);
                break;
            }

        }
        return new Number(area);
    }

    public void outputByDeconvolution(Curve arrival, Curve service) {
        CurveUtils.deconvolution(arrival, service);
    }

    public void outputByBacklog(Curve arrival, Number backlog) {
        CurveUtils.verticalShift(arrival, arrival.getValue(new Number(0.0001)).neg());
        CurveUtils.verticalShift(arrival, backlog);
    }


    /* Value at time. */
    public Number getValue(Number time) {
        Number offset = Number.ZERO;
        if (time.gt(getPseudoPeriodStart().add(getPseudoPeriodLength()))) {
            offset = time.sub(getPseudoPeriodStart()).div(getPseudoPeriodLength()).floor()
                    .mul(getPseudoPeriodHeight());
            time = time.sub(getPseudoPeriodStart()).mod(getPseudoPeriodLength())
                    .add(getPseudoPeriodStart());
            if (time.eq(Number.ZERO) && getPseudoPeriodStart().eq(Number.ZERO))
                offset = offset
                        .add(getBaseSequence().getSegments().get(0).getRightLimitAtStartTime());
        }
        return getBaseSequence().getValue(time).add(offset);
    }

    public Segment getSegment(Number index) {
        return getBaseSequence().getSegments().get((int) index.toDouble());
    }


    public Segment getSegment(int index) {
        return getBaseSequence().getSegments().get(index);
    }

    public Sequence getSequence() {
        return getBaseSequence();
    }

    public String toString() {
        return "(baseSequence: " + getBaseSequence() + ", pseudoPeriodStart: "
                + getPseudoPeriodStart() + ", pseudoPeriodLength: " + getPseudoPeriodLength()
                + ", pseudoPeriodHeight: " + getPseudoPeriodHeight() + ")";
    }

    /*
     * Upper-approximate curve with single segment affine function. The option
     * parameter is a
     * hint at the approximation method, but does not guarantee the desired
     * approximation.
     */
    public void upperApproximate(Approximation option) {
        Number transientSlope = getValue(getPseudoPeriodStart()).div(getPseudoPeriodStart());
        Number pseudoPeriodSlope = getPseudoPeriodHeight().div(getPseudoPeriodLength());

        /* Use transient or pseudo-period slope as specified if possible. */
        Number slope = transientSlope.geq(pseudoPeriodSlope)
                && option == Approximation.TRANSIENT ? transientSlope : pseudoPeriodSlope;

        Number offset = Number.ZERO;
        for (Segment s : getBaseSequence().getSegments())
            offset = offset.max(s.getRightLimitAtStartTime().sub(s.getStartTime().mul(slope)));

        Sequence newSeq = new Sequence(new LinkedList<Segment>(Arrays.asList(
                new Segment(Number.ZERO, Number.ONE, offset, slope))));

        this.setBaseSequence(newSeq);
        this.setPseudoPeriodStart(Number.ZERO);
        this.setPseudoPeriodLength(Number.ONE);
        this.setPseudoPeriodHeight(slope);
    }

    /* Lower-approximate curve with single segment affine function. */
    public void lowerApproximate() {
        Number slope = getPseudoPeriodHeight().div(getPseudoPeriodLength());
        for (Segment s : getBaseSequence().getSegments())
            s.setSlope(slope.min(s.getValue(s.getEndTime()).div(s.getEndTime())));
        Sequence seq = new Sequence(new LinkedList<Segment>(Arrays.asList(
                new Segment(Number.ZERO, Number.ONE, Number.ZERO, slope))));

        setBaseSequence(seq);
        setPseudoPeriodStart(Number.ZERO);
        setPseudoPeriodLength(Number.ONE);
        setPseudoPeriodHeight(slope);
    }

    /*
     * Shorten and move pseudo-period as far left as possible. See [2] Section 4.8.
     */
    public void optimize() {
        ListIterator<Segment> i = getBaseSequence().getSegments().listIterator();
        ListIterator<Segment> j = getBaseSequence().getSegments().listIterator();

        /* Advance to pseudo-period start */
        Segment s = i.next();
        j.next();
        while (s.getEndTime().leq(getPseudoPeriodStart())) {
            s = i.next();
            j.next();
        }
        Segment pseudoPeriodStartSegment = s;
        while (j.hasNext()) {
            Segment t;

            /* Find next t that could resemble s */
            for (t = j.next(); j.hasNext() && !(s.getEndTime().sub(getPseudoPeriodStart())
                    .leq(t.getEndTime().sub(t.getStartTime())) && s.getSlope().eq(t.getSlope())); t = j.next())
                ;
            Number pseudoPeriodEnd = t.getEndTime().sub(s.getEndTime().sub(getPseudoPeriodStart()));
            Number pseudoPeriodLength = pseudoPeriodEnd.sub(getPseudoPeriodStart());
            Number pseudoPeriodHeight = t.getRightLimitValue(pseudoPeriodEnd)
                    .sub(s.getRightLimitValue(getPseudoPeriodStart()));

            /* Check if interval (s.startTime, t.startTime] is a valid pseudo-period */
            if (!j.hasNext()) {
                Number two = new Number(2);
                if (this.getPseudoPeriodLength().eq(pseudoPeriodLength.mul(two))
                        && this.getPseudoPeriodHeight().eq(pseudoPeriodHeight.mul(two))
                        && s.getSlope().eq(t.getSlope())) {
                    this.setPseudoPeriodLength(pseudoPeriodLength);
                    this.setPseudoPeriodHeight(pseudoPeriodHeight);
                    j.remove();
                }
                break;
            }
            s = i.next();
            t = j.next();
            int peek = 0;
            while (j.hasNext() && s.getRightLimitAtStartTime().add(pseudoPeriodHeight)
                    .eq(t.getRightLimitAtStartTime())
                    && s.getStartTime().add(pseudoPeriodLength)
                    .eq(t.getStartTime())
                    && s.getSlope().eq(t.getSlope())) {
                s = i.next();
                t = j.next();
                peek++;
            }

            /* Interval is not a valid pseudo-period */
            if (j.hasNext()) {
                while (peek-- >= 0) {
                    i.previous();
                    j.previous();
                }
                s = i.next();
                j.next();
                continue;
            }

            /* Interval is a valid pseudo-period */
            if (s.getRightLimitAtStartTime().add(pseudoPeriodHeight)
                    .eq(t.getRightLimitAtStartTime())
                    && s.getStartTime().add(pseudoPeriodLength)
                    .eq(t.getStartTime())
                    && s.getEndTime().add(pseudoPeriodLength)
                    .sub(pseudoPeriodStartSegment.getEndTime().sub(getPseudoPeriodStart()))
                    .eq(t.getEndTime())
                    && s.getSlope().eq(t.getSlope())) {
                this.setPseudoPeriodLength(pseudoPeriodLength);
                this.setPseudoPeriodHeight(pseudoPeriodHeight);
                while (peek-- >= 0) {
                    j.previous();
                    j.remove();
                }
                t = j.previous();
                if (t.getStartTime().lt(pseudoPeriodEnd))
                    j.set(t.cut(t.getStartTime(), pseudoPeriodEnd));
                else
                    j.remove();
            }
            break;
        }

        i = getBaseSequence().getSegments().listIterator();
        j = getBaseSequence().getSegments().listIterator();

        /* Get segments at pseudo-period edges */
        s = i.next();
        while (s.getEndTime().lt(getPseudoPeriodStart())) {
            s = i.next();
            j.next();
        }
        Segment t = j.next();
        Number pseudoPeriodEnd = getPseudoPeriodStart().add(getPseudoPeriodLength());
        while (t.getEndTime().lt(pseudoPeriodEnd))
            t = j.next();

        i.previous();
        j.previous();
        int removedSegments = 0;
        while (true) {
            /* Check to left of segments */
            Number delta = getPseudoPeriodStart().sub(s.getStartTime()).min(pseudoPeriodEnd
                    .sub(t.getStartTime()));
            Segment u = s.cut(getPseudoPeriodStart().sub(delta), getPseudoPeriodStart());
            Segment v = t.cut(pseudoPeriodEnd.sub(delta), pseudoPeriodEnd);
            if (u.getSlope().neq(v.getSlope()) || u.getRightLimitAtStartTime().add(getPseudoPeriodHeight())
                    .neq(v.getRightLimitAtStartTime()))
                break;

            /* Move pseudo-period */
            setPseudoPeriodStart(getPseudoPeriodStart().sub(delta));
            pseudoPeriodEnd = pseudoPeriodEnd.sub(delta);

            /* Rewind segments */
            if (t.getStartTime().eq(pseudoPeriodEnd)) {
                removedSegments++;
                if (!j.hasPrevious())
                    break;
                t = j.previous();
            } else {
                t = t.cut(t.getStartTime(), pseudoPeriodEnd);
                j.set(t);
            }
            if (s.getStartTime().eq(getPseudoPeriodStart())) {
                if (!i.hasPrevious())
                    break;
                s = i.previous();
            }
        }
        if (removedSegments > 0) {
            List<Segment> newSegments = new ArrayList<>();
            newSegments = getBaseSequence().getSegments().subList(
                    0,
                    getBaseSequence().getSegments().size() - removedSegments);
            setBaseSequence(new Sequence(newSegments));
        }
    }

    /*
     * Extend the base sequence and thereby the curve up until endTime. See [1]
     * Section 4.1.
     */
    public Curve extend(Number endTime) {

        if (endTime.leq(getPseudoPeriodStart()) || endTime.eq(Number.POSITIVE_INFINITY)) {
            return this;
        }
        Sequence extension = getBaseSequence().cut(Number.ZERO, endTime);
        Number time = getPseudoPeriodStart().add(getPseudoPeriodLength());

        Number count = Number.ONE;
        Sequence pseudoPeriodicPart = getBaseSequence().cut(getPseudoPeriodStart(), time);
        ListIterator<Segment> i = pseudoPeriodicPart.getSegments().listIterator();
        while (time.lt(endTime)) {
            if (!i.hasNext()) {
                i = pseudoPeriodicPart.getSegments().listIterator();
                count = count.add(Number.ONE);
            }
            Segment s = i.next();
            s = new Segment(
                    s.getStartTime().add(count.mul(getPseudoPeriodLength())),
                    s.getEndTime().add(count.mul(getPseudoPeriodLength())),
                    s.getRightLimitAtStartTime().add(count.mul(getPseudoPeriodHeight())),
                    s.getSlope()
            );
            if (s.getStartTime().geq(endTime))
                break;
            extension.getSegments().add(s.cut(s.getStartTime(), endTime));
            time = s.getEndTime();

        }
        extension.optimize();
        return new Curve(
                extension,
                endTime.sub(getPseudoPeriodLength()),
                getPseudoPeriodLength(),
                getPseudoPeriodHeight()
        );
    }

    /* Decompose curve into set of rate latency curves. */
    public List<Curve> decomposeIntoRateLatency() {
        List<Curve> decomposition = new LinkedList<Curve>();
        for (Segment s : getBaseSequence().getSegments()) {
            if (s.getSlope().leq(Number.ZERO))
                continue;
            Number latency = s.getStartTime().sub(s.getRightLimitAtStartTime().div(s.getSlope()));
            if (latency.lt(Number.ZERO))
                continue;
            decomposition.add(CurveFactory.createRateLatency(s.getSlope(), latency));
        }
        return decomposition;
    }

    /* Decompose curve into set of toke bucket curves. */
    public List<Curve> decomposeIntoTokenBucket() {
        List<Curve> decomposition = new LinkedList<Curve>();
        for (Segment s : baseSequence.getSegments()) {
            Number burst = s.getRightLimitAtStartTime().sub(s.getStartTime().mul(s.getSlope()));
            if (burst.lt(Number.ZERO))
                continue;
            decomposition.add(CurveFactory.createTokenBucket(s.getSlope(), burst));
        }
        return decomposition;
    }

    public Curve nonDecreasingClosure() {
        Sequence resultSeq = Sequence.nonDecreasingClosure(baseSequence);
        return new Curve(resultSeq, pseudoPeriodStart, pseudoPeriodLength, pseudoPeriodHeight);
    }

    public Number getPseudoPeriodStart() {
        return pseudoPeriodStart;
    }

    public Number getPseudoPeriodLength() {
        return pseudoPeriodLength;
    }

    public Number getPseudoPeriodHeight() {
        return pseudoPeriodHeight;
    }

    enum Approximation {
        TRANSIENT,
        PSEUDOPERIOD
    }

    public Sequence getBaseSequence() {
        return baseSequence;
    }

    public void setBaseSequence(Sequence baseSequence) {
        this.baseSequence = baseSequence;
    }

    public void setPseudoPeriodStart(Number pseudoPeriodStart) {
        this.pseudoPeriodStart = pseudoPeriodStart;
    }

    public void setPseudoPeriodLength(Number pseudoPeriodLength) {
        this.pseudoPeriodLength = pseudoPeriodLength;
    }

    public void setPseudoPeriodHeight(Number pseudoPeriodHeight) {
        this.pseudoPeriodHeight = pseudoPeriodHeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Curve other = (Curve) obj;
        if (getBaseSequence() == null) {
            if (other.getBaseSequence() != null)
                return false;
        } else if (!getBaseSequence().equals(other.getBaseSequence()))
            return false;
        if (getPseudoPeriodStart() == null) {
            if (other.getPseudoPeriodStart() != null)
                return false;
        } else if (!getPseudoPeriodStart().eq(other.getPseudoPeriodStart()))
            return false;
        if (getPseudoPeriodLength() == null) {
            if (other.getPseudoPeriodLength() != null)
                return false;
        } else if (!getPseudoPeriodLength().eq(other.getPseudoPeriodLength()))
            return false;
        if (getPseudoPeriodHeight() == null) {
            if (other.getPseudoPeriodHeight() != null)
                return false;
        } else if (!getPseudoPeriodHeight().eq(other.getPseudoPeriodHeight()))
            return false;
        return true;
    }

    public Curve copy() {
        return new Curve(
                this.getBaseSequence().copy(),
                new Number(this.getPseudoPeriodStart()),
                new Number(this.getPseudoPeriodLength()),
                new Number(this.getPseudoPeriodHeight())
        );
    }


}