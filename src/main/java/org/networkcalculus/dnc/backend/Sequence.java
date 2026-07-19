package org.networkcalculus.dnc.backend;

import org.networkcalculus.dnc.backend.number.Number;

import java.util.*;

/* Nondecreasing sequence of ordered nonoverlapping segments and implicit point (0, 0). */
public class Sequence {
    private List<Segment> segments;

    public Sequence(List<Segment> segments) {
        this.segments = segments;
    }

    /* Value at time. */
    public Number getValue(Number time) {
        if (time.eq(Number.ZERO))
            return Number.ZERO;
        for (Segment s : segments) {
            if (s.getStartTime().lt(time) && time.leq(s.getEndTime()))
                return s.getValue(time);
        }
        return Number.POSITIVE_INFINITY;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    /* Right limit value at time. */
    public Number getRightLimitValue(Number time) {
        for (Segment s : segments) {
            if (s.getStartTime().leq(time) && time.lt(s.getEndTime()))
                return s.getRightLimitValue(time);
        }
        return Number.POSITIVE_INFINITY;
    }

    public String toString() {
        String string = "[";
        boolean first = true;
        for (Segment s : segments) {
            string += first ? s.toString() : ",\n" + s.toString();
            first = false;
        }
        return string + "]";
    }

    /*
     * Fill undefined sections in interval (startTime, endTime] with specified
     * value.
     */
    public void fill(Number startTime, Number endTime, Number value) {
        Number begin = startTime;
        for (ListIterator<Segment> i = segments.listIterator(); i.hasNext() && begin.lt(endTime); ) {
            Segment s = i.next();
            Number end = s.getStartTime().min(endTime);
            if (begin.lt(end)) {
                i.previous();
                i.add(new Segment(begin, end, value, Number.ZERO));
                i.next();
            }
            begin = s.getEndTime().max(begin);
        }
        if (begin.lt(endTime))
            segments.add(new Segment(begin, endTime, value, Number.ZERO));
    }

    /* Merge neighbouring segments together if possible. */
    public void optimize() {
        if (segments.isEmpty())
            return;
        ListIterator<Segment> i = segments.listIterator();
        Segment s = i.next();
        while (i.hasNext()) {
            Segment t = i.next();
            if (s.getEndTime().eq(t.getStartTime()) && s.getSlope().eq(t.getSlope())
                    && s.getValue(s.getEndTime()).eq(t.getRightLimitAtStartTime())) {
                s.setEndTime(t.getEndTime());
                i.remove();
                continue;
            }
            s = t;
        }
    }

    /* Extract section with interval (startTime, endTime] from sequence. */
    public Sequence cut(Number startTime, Number endTime) {
        List<Segment> segments = new LinkedList<Segment>();
        for (Segment s : this.segments)
            if (s.getStartTime().lt(endTime) && startTime.lt(s.getEndTime()))
                segments.add(s.cut(startTime, endTime));
        return new Sequence(segments);
    }

    /* Sum of two sequences. */
    public static Sequence addition(Sequence a, Sequence b) {
        Sequence sum = new Sequence(new LinkedList<Segment>());
        if (a.segments.isEmpty() || b.segments.isEmpty())
            return sum;

        ListIterator<Segment> i = a.segments.listIterator(), j = b.segments.listIterator();
        Segment s = i.next(), t = j.next();
        boolean segmentsLeft = true;
        while (segmentsLeft) {
            segmentsLeft = i.hasNext() || j.hasNext();

            /* Segments overlap, sum is defined */
            if (t.getStartTime().leq(s.getStartTime()) && s.getStartTime().lt(t.getEndTime())
                    || t.getStartTime().lt(s.getEndTime()) && s.getEndTime().leq(t.getEndTime())
                    || s.getStartTime().lt(t.getStartTime()) && t.getEndTime().lt(s.getEndTime())) {
                Segment result = Segment.addition(s, t);
                sum.segments.add(result);
                if (s.getEndTime().eq(result.getEndTime()) && i.hasNext())
                    s = i.next();
                if (t.getEndTime().eq(result.getEndTime()) && j.hasNext())
                    t = j.next();
                continue;
            }

            /* Segments do not overlap, get next segment */
            if (s.getEndTime().lt(t.getEndTime()) && i.hasNext())
                s = i.next();
            else if (s.getEndTime().gt(t.getEndTime()) && j.hasNext())
                t = j.next();
        }
        sum.optimize();
        return sum;
    }

    /* Nonnegative and nondecreasing difference of two sequences. */
    public static Sequence subtraction(Sequence a, Sequence b) {

        // mirror at Segments at x axis and use addition
        ArrayList<Segment> segmentsNeg = new ArrayList<>();
        for (Segment s : b.getSegments()) {
            segmentsNeg.add(new Segment(s.getStartTime(), s.getEndTime(), s.getRightLimitAtStartTime().neg(), s.getSlope().neg()));
        }
        Sequence newSequenceB = new Sequence(new LinkedList<>(segmentsNeg));
        Sequence seqAdded = addition(a, newSequenceB);


        // Apply non-negative closure
        ArrayList<Segment> segmentsNonNegative = new ArrayList<>();
        for (Segment s : seqAdded.getSegments()) {

            // Segment negative
            if (s.getRightLimitAtStartTime().leq(Number.ZERO) && s.getValue(s.getEndTime()).leq(Number.ZERO))
                segmentsNonNegative.add(new Segment(s.getStartTime(), s.getEndTime(), Number.ZERO, Number.ZERO));
                // Segment partially negative
            else if (s.getRightLimitAtStartTime().leq(Number.ZERO) && s.getValue(s.getEndTime()).gt(Number.ZERO)) {
                Number crossingTime = s.getStartTime().add(s.getRightLimitAtStartTime().neg().div(s.getSlope()));
                // Add flat part + increasing part
                segmentsNonNegative.add(new Segment(s.getStartTime(), crossingTime, Number.ZERO, Number.ZERO));
                segmentsNonNegative.add(new Segment(crossingTime, s.getEndTime(), Number.ZERO, s.getSlope()));
            }
            // Segment positive
            else {
                segmentsNonNegative.add(new Segment(s.getStartTime(), s.getEndTime(), s.getRightLimitAtStartTime(), s.getSlope().max(Number.ZERO)));
            }
        }

        Sequence result = new Sequence(new LinkedList<>(segmentsNonNegative));
        result.optimize();
        return result;
    }

    public static Sequence nonDecreasingClosure(Sequence aSeq) {
        List<Segment> aSeg = aSeq.getSegments();
        List<Segment> segmentsNonDecreasing = new ArrayList<>();
        Number maxValue = Number.ZERO;

        for (Segment currentSeg : aSeg) {
            Number startValue = currentSeg.getRightLimitAtStartTime();
            Number endValue = currentSeg.getRightLimitValue(currentSeg.getEndTime());

            // Segment entirely below the max value seen so far
            if (maxValue.geq(startValue) && maxValue.geq(endValue)) {
                // Add flat part
                segmentsNonDecreasing.add(new Segment(currentSeg.getStartTime(), currentSeg.getEndTime(), maxValue, Number.ZERO));
            }
            // Segment partially below the max value seen so far
            else if (maxValue.gt(startValue) && endValue.gt(maxValue)) {
                // Time that the segment is below the max value
                Number intersectionTime = currentSeg.getTime(maxValue);

                // Add flat part
                segmentsNonDecreasing.add(new Segment(currentSeg.getStartTime(), intersectionTime, maxValue, Number.ZERO));
                // Add the rest of the segment
                segmentsNonDecreasing.add(new Segment(intersectionTime, currentSeg.getEndTime(), maxValue, currentSeg.getSlope()));

                maxValue = endValue;
            }
            // Segment entirely above the max value seen so far
            else {
                // Negative slope
                if (currentSeg.getSlope().lt(Number.ZERO)) {
                    segmentsNonDecreasing.add(new Segment(currentSeg.getStartTime(), currentSeg.getEndTime(), startValue, Number.ZERO));
                    maxValue = startValue;
                } else {
                    // Valid segment
                    segmentsNonDecreasing.add(currentSeg);
                    maxValue = endValue;
                }
            }
        }

        Sequence result = new Sequence(segmentsNonDecreasing);
        result.optimize();
        return result;
    }

    /* Minimum of two sequences. */
    public static Sequence minimum(Sequence a, Sequence b) {
        if (a.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(b.segments));
        if (b.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(a.segments));
        Sequence minimum = new Sequence(new LinkedList<Segment>());

        ListIterator<Segment> i = a.segments.listIterator(), j = b.segments.listIterator();
        Segment s = i.next(), t = j.next();
        while (true) {
            /* Advance segments or exit */
            if (s.getEndTime().sub(s.getStartTime()).eq(Number.ZERO)) {
                if (!i.hasNext()) {
                    if (t.getEndTime().sub(t.getStartTime()).gt(Number.ZERO))
                        minimum.segments.add(t);
                    while (j.hasNext())
                        minimum.segments.add(j.next());
                    break;
                }
                s = i.next();
            }
            if (t.getEndTime().sub(t.getStartTime()).eq(Number.ZERO)) {
                if (!j.hasNext()) {
                    minimum.segments.add(s);
                    while (i.hasNext())
                        minimum.segments.add(i.next());
                    break;
                }
                t = j.next();
            }

            /* Segments overlap */
            if (t.getStartTime().leq(s.getStartTime()) && s.getStartTime().lt(t.getEndTime())
                    || t.getStartTime().lt(s.getEndTime()) && s.getEndTime().leq(t.getEndTime())
                    || s.getStartTime().lt(t.getStartTime()) && t.getEndTime().lt(s.getEndTime())) {
                Number endTime = s.getEndTime().min(t.getEndTime());
                minimum.segments.addAll(Segment.minimum(
                        s.cut(s.getStartTime(), endTime),
                        t.cut(t.getStartTime(), endTime)));
                s = s.cut(endTime, s.getEndTime());
                t = t.cut(endTime, t.getEndTime());
                continue;
            }

            /* Segments do not overlap */
            if (s.getEndTime().lt(t.getEndTime())) {
                minimum.segments.add(s);
                s = s.cut(s.getEndTime(), s.getEndTime());
            } else {
                minimum.segments.add(t);
                t = t.cut(t.getEndTime(), t.getEndTime());
            }
        }
        minimum.optimize();
        return minimum;
    }

    /* Maximum of two sequences. */
    public static Sequence maximum(Sequence a, Sequence b) {
        Sequence maximum = new Sequence(new LinkedList<Segment>());
        if (a.segments.isEmpty() || b.segments.isEmpty())
            return maximum;

        ListIterator<Segment> i = a.segments.listIterator(), j = b.segments.listIterator();
        Segment s = i.next(), t = j.next();
        while (true) {
            /* Advance segments or exit */
            if (s.getEndTime().sub(s.getStartTime()).eq(Number.ZERO)) {
                if (!i.hasNext()) {
                    if (t.getEndTime().sub(t.getStartTime()).gt(Number.ZERO))
                        maximum.segments.add(t);
                    while (j.hasNext())
                        maximum.segments.add(j.next());
                    break;
                }
                s = i.next();
            }
            if (t.getEndTime().sub(t.getStartTime()).eq(Number.ZERO)) {
                if (!j.hasNext()) {
                    maximum.segments.add(s);
                    while (i.hasNext())
                        maximum.segments.add(i.next());
                    break;
                }
                t = j.next();
            }

            /* Segments overlap */
            if (t.getStartTime().leq(s.getStartTime()) && s.getStartTime().lt(t.getEndTime())
                    || t.getStartTime().lt(s.getEndTime()) && s.getEndTime().leq(t.getEndTime())
                    || s.getStartTime().lt(t.getStartTime()) && t.getEndTime().lt(s.getEndTime())) {
                Number startTime = s.getStartTime().max(t.getStartTime());
                if (s.getStartTime().lt(startTime))
                    maximum.segments.add(s.cut(s.getStartTime(), startTime));
                if (t.getStartTime().lt(startTime))
                    maximum.segments.add(t.cut(t.getStartTime(), startTime));
                maximum.segments.addAll(Segment.maximum(s, t));
                Number endTime = s.getEndTime().min(t.getEndTime());
                s = s.cut(endTime, s.getEndTime());
                t = t.cut(endTime, t.getEndTime());
                continue;
            }

            /* Segments do not overlap */
            if (s.getEndTime().lt(t.getEndTime())) {
                maximum.segments.add(s);
                s = s.cut(s.getEndTime(), s.getEndTime());
            } else {
                maximum.segments.add(t);
                t = t.cut(t.getEndTime(), t.getEndTime());
            }
        }
        maximum.optimize();
        return maximum;
    }

    /*
     * Class to pass a method to envelop and thus determining which envelope is
     * calculated.
     */
    private static abstract class Enveloper {
        public abstract List<Segment> compare(Segment a, Segment b);
    }

    /*
     * Nondecreasing envelope of segments. This is a generalized form of calculating
     * the lower
     * or upper envelope. See [1] Section 4.3, Minimum of several ultimately
     * pseudo-periodic
     * functions. Note that the list a will be sorted as a side effect.
     */
    private static Sequence envelop(List<Segment> a, Enveloper e) {
        Sequence envelope = new Sequence(new LinkedList<Segment>());
        if (a.isEmpty())
            return envelope;
        Collections.sort(a, new Comparator<Segment>() {
            @Override
            public int compare(Segment a, Segment b) {
                if (a.getStartTime().lt(b.getStartTime()))
                    return -1;
                if (a.getStartTime().gt(b.getStartTime()))
                    return 1;
                if (a.getEndTime().lt(b.getEndTime()))
                    return -1;
                if (a.getEndTime().gt(b.getEndTime()))
                    return 1;
                return 0;
            }
        });

        Number time = Number.ZERO, minimalValue = Number.ZERO;
        ListIterator<Segment> i = a.listIterator();
        Segment s = i.next();
        int g = 0;
        while (true) {
            while (s.getEndTime().leq(time) && i.hasNext())
                s = i.next();
            if (s.getEndTime().leq(time))
                break;
            s = s.cut(time, s.getEndTime());

            /* Calculate envelope with next segments that overlap */
            int peek = 0;
            Segment result = s;
            while (i.hasNext()) {
                Segment t = i.next();
                peek++;
                if (result.getEndTime().leq(t.getStartTime()))
                    break;
                t = t.cut(time, result.getEndTime());
                if (t.getStartTime().geq(t.getEndTime()))
                    continue;
                result = e.compare(result, t).get(0);
            }
            while (peek > 0) {
                i.previous();
                peek--;
            }
            envelope.segments.addAll(Segment.maximum(result, new Segment(
                    result.getStartTime(),
                    result.getEndTime(),
                    minimalValue,
                    Number.ZERO)));
            time = result.getEndTime();
            minimalValue = minimalValue.max(result.getValue(result.getEndTime()));
        }
        envelope.optimize();
        return envelope;
    }

    /*
     * Nondecreasing lower envelope (minimum) of segments. See envelop method for
     * details.
     * Note that the list a will be sorted as a side effect.
     */
    public static Sequence minimum(List<Segment> a) {
        return Sequence.envelop(a, new Enveloper() {
            @Override
            public List<Segment> compare(Segment a, Segment b) {
                return Segment.minimum(a, b);
            }
        });
    }

    /*
     * Nondecreasing upper envelope (maximum) of segments. See envelop method for
     * details.
     * Note that the list a will be sorted as a side effect.
     */
    public static Sequence maximum(List<Segment> a) {
        return Sequence.envelop(a, new Enveloper() {
            @Override
            public List<Segment> compare(Segment a, Segment b) {
                List<Segment> maximum = Segment.maximum(a, b);
                /* Add normally undefined but here necessary nonoverlapping part */
                if (a.getStartTime().neq(maximum.get(0).getStartTime()))
                    maximum.add(0, new Segment(
                            a.getStartTime(),
                            maximum.get(0).getStartTime(),
                            a.getRightLimitAtStartTime(),
                            a.getSlope()));
                return maximum;
            }
        });
    }

    /*
     * Convolution of two sequences. See [1] Section 3.2.1, Lemma 2 and Section 4.4,
     * Step 3.
     */
    public static Sequence convolution(Sequence a, Sequence b) {
        if (a.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(b.segments));
        if (b.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(a.segments));
        List<Segment> convolution = new LinkedList<Segment>();

        /* Convolve with implicit point (0, 0) */
        if (a.segments.get(0).getStartTime().eq(Number.ZERO))
            convolution.addAll(b.segments);
        if (b.segments.get(0).getStartTime().eq(Number.ZERO))
            convolution.addAll(a.segments);

        /* Convolve segments */
        for (Segment s : a.segments)
            for (Segment t : b.segments)
                convolution.addAll(Segment.convolution(s, t));
        return Sequence.minimum(convolution);
    }

    /*
     * Deconvolution of two sequences. See [1] Section 3.2.2, Lemma 6 & 7 and
     * Section 4.5.
     */
    public static Sequence deconvolution(Sequence a, Sequence b) {
        if (a.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(b.segments));
        if (b.segments.isEmpty())
            return new Sequence(new LinkedList<Segment>(a.segments));
        List<Segment> deconvolution = new LinkedList<Segment>();

        /* Deconvolve with implicit point (0, 0) */
//        if (a.segments.get(0).startTime.eq(Number.ZERO))
//            deconvolution.addAll(b.segments);
        if (b.segments.get(0).getStartTime().eq(Number.ZERO))
            deconvolution.addAll(a.segments);

        /* Deconvolve segments */
        for (Segment s : a.segments)
            for (Segment t : b.segments)
                deconvolution.addAll(Segment.deconvolution(s, t));
        return Sequence.maximum(deconvolution);
    }

    /* Delay of arrival and service sequences. */
    public static Number delay(Sequence arrival, Sequence service) {
        if (arrival.segments.isEmpty() || service.segments.isEmpty())
            return Number.POSITIVE_INFINITY;
        ListIterator<Segment> i = arrival.segments.listIterator(), j = service.segments.listIterator();
        Segment s = i.next(), t = j.next();
        Number delay = Segment.delay(s, t), endValue = Number.ZERO;
        do {
            while (s.getValue(s.getEndTime()).leq(endValue) && i.hasNext()) {
                s = i.next();
            }
            while (t.getValue(t.getEndTime()).leq(endValue) && j.hasNext()) {
                t = j.next();
            }
            if (j.hasNext() && !i.hasNext() && !t.getValue(t.getEndTime()).leq(endValue)) { // avoid endless loop
                j.next();
            }
            delay = delay.max(Segment.delay(s, t));
            endValue = s.getValue(s.getEndTime()).min(t.getValue(t.getEndTime()));

        } while (i.hasNext() || j.hasNext());
        return delay;
    }

    /* Backlog of arrival and service sequences. */
    public static Number backlog(Sequence arrival, Sequence service) {
        if (arrival.segments.isEmpty() || service.segments.isEmpty())
            return Number.POSITIVE_INFINITY;
        ListIterator<Segment> i = arrival.segments.listIterator(), j = service.segments.listIterator();
        Segment s = i.next(), t = j.next();
        Number backlog = Number.ZERO, endTime = Number.ZERO;
        do {
            while (s.getEndTime().leq(endTime) && i.hasNext())
                s = i.next();
            while (t.getEndTime().leq(endTime) && j.hasNext())
                t = j.next();
            backlog = backlog.max(Segment.backlog(s, t));
            endTime = s.getEndTime().max(t.getEndTime());
        } while (i.hasNext() || j.hasNext());
        return backlog;
    }

    public Sequence copy() {
        List<Segment> copySegments = new ArrayList<Segment>(this.segments.size());
        for (Segment segment : this.segments) {
            copySegments.add(new Segment(segment.getStartTime(), segment.getEndTime(),
                    segment.getRightLimitValue(segment.getStartTime()), segment.getSlope()));
        }
        return new Sequence(copySegments);

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sequence other = (Sequence) obj;
        if (segments == null) {
            if (other.segments != null)
                return false;
        } else if (!segments.equals(other.segments))
            return false;
        return true;
    }
}
