package unikl.disco.curves;

import unikl.disco.curves.dnc.LinearSegmentDNC;
import unikl.disco.curves.rtcUltAffine.LinearSegmentRTC;
import unikl.disco.nc.CalculatorConfig;
import unikl.disco.numbers.Num;
import unikl.disco.numbers.NumFactory;

public class LinearSegmentFactory {

    // This is a small workaround. In case that more methods are added here, it may be useful to split up this class
    // and implement a factory like CurveFactroy.

    public static LinearSegment createLinearSegment(Num x, Num y, Num grad, boolean leftopen) {
        switch (CalculatorConfig.getCurveClass()) {
            case RTC:
                return new LinearSegmentRTC(x.doubleValue(), y.doubleValue(), grad.doubleValue());
            case DNC:
            default:
                return new LinearSegmentDNC(x, y, grad, leftopen);

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
                return new LinearSegmentRTC(0.0, 0.0, 0.0);
            case DNC:
            default:
                return new LinearSegmentDNC(NumFactory.createZero(), NumFactory.createZero(), NumFactory.createZero(), false);

        }
    }
}
