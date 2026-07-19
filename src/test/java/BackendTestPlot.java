import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.networkcalculus.dnc.backend.*;
import org.networkcalculus.dnc.backend.number.Number;

import javax.swing.plaf.ComponentUI;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackendTestPlot {

    public static void main(String[] args) {
        run8();

    }

    public static void runAll() {
        run1();
        run2();
        run3();
        run4();
        run5();
        run6();
    }

    public static void run7() {
//        "[(startTime: 0, endTime: 1, rightLimitAtStartTime: 1, slope: 0)], pseudoPeriodStart: 0, pseudoPeriodLength: 1, pseudoPeriodHeight: 1)"
//        "(baseSequence: [(startTime: 0, endTime: 63764300/86468487, rightLimitAtStartTime: 0, slope: 0),(startTime: 63764300/86468487, endTime: 719963/694526, rightLimitAtStartTime: 0, slope: 249/50)], pseudoPeriodStart: 25437/694526, pseudoPeriodLength: 1, pseudoPeriodHeight: 149/100)"
        // used in SFA/ARB
        Segment seg1 = new Segment(Number.ZERO, Number.ONE, Number.ONE, Number.ZERO);
        List<Segment> segments = new ArrayList<>();
        segments.add(seg1);
        Curve c1 = new Curve(new Sequence(segments), Number.ZERO, Number.ONE, Number.ONE);

        seg1 = new Segment(Number.ZERO, new Number(63764300, 86468487), Number.ZERO, Number.ZERO);
        Segment seg2 = new Segment(new Number(63764300, 86468487), new Number(719963, 694526), Number.ZERO, new Number(249 / 50));
        List<Segment> segments2 = new ArrayList<>();
        segments2.add(seg1);
        segments2.add(seg2);
        Curve c2 = new Curve(new Sequence(segments2), new Number(25437 / 694526), Number.ONE, new Number(149, 100));


        Plotter plotter = new Plotter();
        plotter.addCurveToPlotter(c1.extend(new Number(10)), Color.RED);
        plotter.addCurveToPlotter(c2.extend(new Number(10)), Color.GREEN);

        System.out.println(CurveUtils.getFirstIntersection(c1, c2));

        System.out.println(CurveUtils.delayARB(c1, c2));

        plotter.plotCurves();
    }

    public static void run1() {
        Curve a = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        Curve b = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve result = CurveUtils.sub(a, b);

        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ONE.toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());

        Plotter plotterC = new Plotter();
        plotterC.addCurveToPlotter(a.extend(new Number(10)), Color.BLACK);
        plotterC.addCurveToPlotter(b.extend(new Number(10)), Color.RED);
        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
        plotterC.plotCurves();
    }

    public static void run2() {
        Curve a = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve b = CurveFactory.createTokenBucket(new Number(1), new Number(1));

        Number x = CurveUtils.getFirstIntersection(a, b);
        assertThat(x.toDouble()).isEqualTo(new Number(3).toDouble());

        Curve result = CurveUtils.sub(a, b);
        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ZERO.toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(1).toDouble());

        Plotter plotterC = new Plotter();
        plotterC.addCurveToPlotter(a.extend(new Number(10)), Color.BLACK);
        plotterC.addCurveToPlotter(b.extend(new Number(10)), Color.RED);
        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
        plotterC.plotCurves();
    }

    public static void run3() {
        Curve a = CurveFactory.createTokenBucket(new Number(2), new Number(1));
        Curve b = CurveFactory.createRateLatency(new Number(1), Number.ONE);
        Curve result = CurveUtils.sub(b, a);

        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());

        Plotter plotterC = new Plotter();
        plotterC.addCurveToPlotter(b.extend(new Number(10)), Color.BLACK);
        plotterC.addCurveToPlotter(a.extend(new Number(10)), Color.RED);
        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
        plotterC.plotCurves();
    }

    public static void run4() {
        Curve arrival = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        arrival = CurveUtils.shiftRight(arrival, new Number(1));

        Curve service = CurveFactory.createRateLatency(new Number(2), Number.ONE);
        Curve result = CurveUtils.sub(service, arrival);

        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(new Number(0).toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(1).toDouble());

        Plotter plotterC = new Plotter();
        plotterC.addCurveToPlotter(arrival.extend(new Number(10)), Color.BLACK);
        plotterC.addCurveToPlotter(service.extend(new Number(10)), Color.RED);
        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
        plotterC.plotCurves();
    }

    public static void run5() {
        Curve arrival = CurveFactory.createTokenBucket(new Number(1), new Number(1));
        arrival = CurveUtils.shiftRight(arrival, new Number(1.5));

        Curve service = CurveFactory.createRateLatency(new Number(3), Number.ONE);
        Curve result = CurveUtils.sub(service, arrival);

        assertThat(result.getBaseSequence().getSegments().getFirst().getSlope().toDouble()).isEqualTo(Number.ZERO.toDouble());
        assertThat(result.getBaseSequence().getSegments().getLast().getSlope().toDouble()).isEqualTo(new Number(2).toDouble());

        Plotter plotterC = new Plotter();
        plotterC.addCurveToPlotter(arrival.extend(new Number(10)), Color.BLACK);
        plotterC.addCurveToPlotter(service.extend(new Number(10)), Color.RED);
        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
        plotterC.plotCurves();
    }

    public static void run6() {
        Curve arrival = CurveFactory.createStepFunctionArrival(new Number(1.05), new Number(0.4), new Number(0.4));
        Curve service = CurveFactory.createStepFunctionService(new Number(1.5), new Number(1.5), new Number(1.5));
        System.out.println(CurveUtils.hDev(arrival, service));

        Plotter plotterA = new Plotter();
        plotterA.addCurveToPlotter(arrival.extend(new Number(10)), Color.RED);
        plotterA.addCurveToPlotter(service.extend(new Number(10)), Color.GREEN);
        plotterA.plotCurves();


//        Curve result = CurveUtils.sub(service, arrival);
//        Plotter plotterC = new Plotter();
//        plotterC.addCurveToPlotter(result.extend(new Number(10)), Color.GREEN);
//        plotterC.plotCurves();
    }

    public static void run8() {

        Number b1 = new Number(5);
        Number r = new Number(2);

        Curve c1 = CurveFactory.createTokenBucket(r, b1);

        List<Segment> segments = new ArrayList<>();
        Segment firstSeg = new Segment(Number.ZERO, Number.ONE, Number.ZERO, Number.ZERO);
        Segment secondSeg = new Segment(Number.ONE, new Number(2), Number.ZERO, new Number(4));
        Segment thirdSeg = new Segment(new Number(2), new Number(3), new Number(4), new Number(2));
        segments.add(firstSeg);
        segments.add(secondSeg);
        segments.add(thirdSeg);
        Curve c2 = new Curve(new Sequence(segments), Number.ONE, new Number(2), new Number(6));

        Plotter plotter = new Plotter();
        plotter.addCurveToPlotter(c1.extend(new Number(10)));
        plotter.addCurveToPlotter(c2.extend(new Number(10)));

        System.out.println(CurveUtils.getFirstIntersection(c2, c1));

        plotter.plotCurves();
    }
}