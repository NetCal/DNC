package de.uni_kl.cs.disco.demos;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.curves.dnc.ArrivalCurve_DNC;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;

/**
 * Created by philipp on 7/12/17.
 */
public class Debug {
    public static void main(String args[]) throws Exception {
        //null_curve();
        //conv_test();
        equ();
    }

    public static void null_curve() throws Exception {
        CalculatorConfig.getInstance().setCurveClass(CalculatorConfig.CurveClass.MPA_RTC);


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

        CurvePwAffine.getFactory().createArrivalCurve(c2.toString());
        CurvePwAffine.getFactory().createArrivalCurve(c.toString());

    }

    public static void conv_test() throws Exception {
        if(true) return;
        System.out.println("RTC\n");
        CalculatorConfig.getInstance().setCurveClass(CalculatorConfig.CurveClass.MPA_RTC);

        ServiceCurve s1 = CurvePwAffine.getFactory().createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(20.0,0.0,20.0)} \n");
        ServiceCurve s2 = CurvePwAffine.getFactory().createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(35.0,0.0,15.0)} \n");

        System.out.println(s1.toString());
        System.out.println(s2.toString());
        //System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, true).toString());
        //System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, false).toString());

        System.out.println("\nDNC\n");
        CalculatorConfig.getInstance().setCurveClass(CalculatorConfig.CurveClass.DNC);
        s1 = CurvePwAffine.getFactory().createServiceCurve(2);
        s2 = CurvePwAffine.getFactory().createServiceCurve(2);
        s1.getSegment(1).setX(Num.getFactory().create(20));
        s1.getSegment(1).setY(Num.getFactory().create(0));
        s1.getSegment(1).setGrad(Num.getFactory().create(20));
        s2.getSegment(1).setX(Num.getFactory().create(35));
        s2.getSegment(1).setY(Num.getFactory().create(0));
        s2.getSegment(1).setGrad(Num.getFactory().create(15));

        System.out.println(s1.toString());
        System.out.println(s2.toString());
        //System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, true).toString());
        //System.out.println("Output:" + OperatorUtils.apply(s1,s2, OperatorUtils.Operation.convolve, false).toString());

    }


    /*
        Test for equality of different class instances when using copy constructor or copy method
     */
    public static void equ() {

    }
}
