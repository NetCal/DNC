package de.uni_kl.disco.curves;

import de.uni_kl.disco.curves.dnc.LinearSegment_DNC;
import de.uni_kl.disco.curves.mpa_rtc_affine.LinearSegment_MPARTC_affine;
import de.uni_kl.disco.nc.CalculatorConfig;
import de.uni_kl.disco.numbers.Num;
import de.uni_kl.disco.numbers.NumFactory;

public class LinearSegmentFactory {

    // This is a small workaround. In case that more methods are added here, it may be useful to split up this class
    // and implement a factory like CurveFactroy.

    public static LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
        switch (CalculatorConfig.getCurveClass()) {
            case RTC:
                return new LinearSegment_MPARTC_affine(x.doubleValue(), y.doubleValue(), grad.doubleValue());
            case DNC:
            default:
                return new LinearSegment_DNC(x, y, grad, leftopen);

        }
    }

    public static LinearSegment createZeroSegment() {
        switch (CalculatorConfig.getCurveClass()) {
            case RTC:
                return createHorizontalLine(0.0);
            case DNC:
            default:
                return createHorizontalLine(0.0);

        }
    }

    public static LinearSegment createHorizontalLine(double y) {
        switch (CalculatorConfig.getCurveClass()) {
            case RTC:
                return new LinearSegment_MPARTC_affine(0.0, 0.0, 0.0);
            case DNC:
            default:
                return new LinearSegment_DNC(NumFactory.createZero(), NumFactory.createZero(), NumFactory.createZero(), false);

        }
    }
}
