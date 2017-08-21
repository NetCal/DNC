package de.uni_kl.disco.demos;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.disco.curves.CurvePwAffineFactory;
import de.uni_kl.disco.curves.LinearSegmentFactory;
import de.uni_kl.disco.curves.ServiceCurve;
import de.uni_kl.disco.curves.dnc.ArrivalCurve_DNC;
import de.uni_kl.disco.curves.dnc.LinearSegment_DNC;
import de.uni_kl.disco.minplus.OperatorUtils;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.numbers.NumFactory;

/**
 * Created by philipp on 7/12/17.
 */
public class Debug {
    public static void main(String args[]) throws Exception {
        //null_curve();
        conv_test();
    }

    public static void null_curve() throws Exception {
        CalculatorConfig.setCurveClass(CalculatorConfig.CurveClass.MPA_RTC);


        ArrivalCurve_DNC c1 = new ArrivalCurve_DNC();
        System.out.println(c1.toString());
        ArrivalCurve_DNC c2 = new ArrivalCurve_DNC(c1.toString());
        System.out.println(c2.toString());

        Segment s = new Segment(0,0,0);
        SegmentList sl = new SegmentList();
        sl.add(s);
        sl.add(new Segment(1,1,1));
        sl.add(new Segment(5,2,7));
        Curve c = new Curve(sl);
        System.out.println(c.toString());
        System.out.println(c.toString().indexOf("{"));

        CurvePwAffineFactory.createArrivalCurve(c2.toString());
        CurvePwAffineFactory.createArrivalCurve(c.toString());

    }

    public static void conv_test() throws Exception {
        System.out.println("RTC\n");
        CalculatorConfig.setCurveClass(CalculatorConfig.CurveClass.MPA_RTC);

        ServiceCurve s1 = CurvePwAffineFactory.createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(20.0,0.0,20.0)} \n");
        ServiceCurve s2 = CurvePwAffineFactory.createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(35.0,0.0,15.0)} \n");

        System.out.println(s1.toString());
        System.out.println(s2.toString());
        System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, true).toString());
        System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, false).toString());

        System.out.println("\nDNC\n");
        CalculatorConfig.setCurveClass(CalculatorConfig.CurveClass.DNC);
        s1 = CurvePwAffineFactory.createServiceCurve(2);
        s2 = CurvePwAffineFactory.createServiceCurve(2);
        s1.getSegment(1).setX(NumFactory.create(20));
        s1.getSegment(1).setY(NumFactory.create(0));
        s1.getSegment(1).setGrad(NumFactory.create(20));
        s2.getSegment(1).setX(NumFactory.create(35));
        s2.getSegment(1).setY(NumFactory.create(0));
        s2.getSegment(1).setGrad(NumFactory.create(15));

        System.out.println(s1.toString());
        System.out.println(s2.toString());
        System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, true).toString());
        System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, false).toString());

    }
}
