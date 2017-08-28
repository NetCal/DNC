package de.uni_kl.cs.disco.demos;

import ch.ethz.rtc.kernel.Curve;
import ch.ethz.rtc.kernel.Segment;
import ch.ethz.rtc.kernel.SegmentList;
import de.uni_kl.cs.disco.curves.ArrivalCurve;
import de.uni_kl.cs.disco.curves.CurvePwAffine;
import de.uni_kl.cs.disco.curves.LinearSegment;
import de.uni_kl.cs.disco.curves.ServiceCurve;
import de.uni_kl.cs.disco.curves.dnc.ArrivalCurve_DNC;
import de.uni_kl.cs.disco.curves.mpa_rtc_pwaffine.Curve_MPARTC_PwAffine;
import de.uni_kl.cs.disco.minplus.MinPlus;
import de.uni_kl.cs.disco.nc.CalculatorConfig;
import de.uni_kl.cs.disco.numbers.Num;

/**
 * Created by philipp on 7/12/17.
 */
public class Debug {
    public static void main(String args[]) throws Exception {
        //null_curve();
        //conv_test();
        //equ();
        //mix();
        string();
    }

    public static void null_curve() throws Exception {
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.MPA_RTC);


        ArrivalCurve_DNC c1 = new ArrivalCurve_DNC();
        System.out.println(c1.toString());
        ArrivalCurve_DNC c2 = new ArrivalCurve_DNC(c1.toString());
        System.out.println(c2.toString());

        Segment s = new Segment(0, 0, 0);
        SegmentList sl = new SegmentList();
        sl.add(s);
        sl.add(new Segment(1, 1, 1));
        sl.add(new Segment(5, 2, 7));
        Curve c = new Curve(sl);
        System.out.println(c.toString());
        System.out.println(c.toString().indexOf("{"));

        CurvePwAffine.getFactory().createArrivalCurve(c2.toString());
        CurvePwAffine.getFactory().createArrivalCurve(c.toString());

    }

    public static void conv_test() throws Exception {
        if (true) return;
        System.out.println("RTC\n");
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.MPA_RTC);

        ServiceCurve s1 = CurvePwAffine.getFactory().createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(20.0,0.0,20.0)} \n");
        ServiceCurve s2 = CurvePwAffine.getFactory().createServiceCurve("SCCurve: \n" +
                "  AperiodicPart  = {(0.0,0.0,0.0)(35.0,0.0,15.0)} \n");

        System.out.println(s1.toString());
        System.out.println(s2.toString());
        System.out.println("Output:" + MinPlus.convolve(s1, s2, true).toString());
        System.out.println("Output:" + MinPlus.convolve(s1, s2, false).toString());

        System.out.println("\nDNC\n");
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.DNC);
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
        System.out.println("Output:" + MinPlus.convolve(s1, s2, true).toString());
        System.out.println("Output:" + MinPlus.convolve(s1, s2, false).toString());

    }


    /*
        Test for equality of two class instances when using copy constructor, copy method or others
     */
    public static void equ() {
        ArrivalCurve a = CurvePwAffine.getFactory().createZeroArrivals();
        ArrivalCurve b = CurvePwAffine.getFactory().createZeroArrivals();

        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");

        LinearSegment s = LinearSegment.createHorizontalLine(5);
        b.addSegment(s);

        System.out.println(a.toString());
        System.out.println(b.toString());

        System.out.println("New b Segment:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");

        b = CurvePwAffine.getFactory().createArrivalCurve(a);

        System.out.println("Copy Constructor:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");

        b = a.copy();

        System.out.println("b = a.copy:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");

        b = new ArrivalCurve_DNC(a);

        System.out.println("b = new AC(a):");
        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");

        b = a;

        System.out.println("b = a:");
        System.out.println("a == b: " + (a == b));
        System.out.println("a equals b: " + (a.equals(b)));
        System.out.println("a class equals b class: " + a.getClass().equals(b.getClass()));
        System.out.println("a class == b class: " + (a.getClass() == b.getClass()));
        System.out.println("hash a == hash b: " + (a.hashCode() == b.hashCode()));
        System.out.println("");


    }

    /*
        Test if it is possible to mix RTC and DNC Curves
        Result: It works
     */
    public static void mix() {
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.DNC);
        ServiceCurve a1 = CurvePwAffine.getFactory().createRateLatency(6, 10);
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.MPA_RTC);
        ServiceCurve a2 = CurvePwAffine.getFactory().createRateLatency(1, 2);
        try {
            System.out.println("Output:" + MinPlus.convolve(a1, a2, true).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
        Check for compabilities of string representations and different constructors of the RTC implementation
     */
    public static void string() {
        CalculatorConfig.getInstance().setCurveImpl(CalculatorConfig.CurveImpl.MPA_RTC);
        System.out.println("Creating RTC Segments");
        Segment s1 = new Segment(0,0,0);
        Segment s = new Segment(1, 1, 1);
        SegmentList sl = new SegmentList();
        sl.add(s1);
        sl.add(s);
        System.out.println("Done\n");
        System.out.println("Creating RTC Curve with Segments from above using RTC constructor:");
        Curve a = new Curve(sl);
        String as = a.toString();
        System.out.println("Curve String:\n"+ as);

        System.out.println("\nCreating RTC Curve from String above using RTC constructor:");
        Curve b;
        try {
            b = new Curve(as);
            System.out.println(b.toString());
        } catch (Exception e){
            System.out.println("Failed");
        }

        System.out.println("\nCreating RTC Curve from String above using DNC wrapper constructor:");
        CurvePwAffine c = null;
        try {
            c = CurvePwAffine.getFactory().createArrivalCurve(as);
            System.out.println(c.toString());
        } catch (Exception e) {
            System.out.println("Failed.");
        }

        System.out.println("\nRemoving wrapper:\n" + ((Curve_MPARTC_PwAffine) c).getRtc_curve().toString());
    }
}
