package unikl.disco.curves;

/**
 * Interface for wide-sense increasing, ultimately pseudo periodic curves.
 */
public interface CurveUltPsPeriodic extends Curve {
    @Override
    CurveUltPsPeriodic copy();

    @Override
    void copy(Curve curve);
}
