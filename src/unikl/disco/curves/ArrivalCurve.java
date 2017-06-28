package unikl.disco.curves;

public interface ArrivalCurve extends CurveUltAffine {
    @Override
    ArrivalCurve copy();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
