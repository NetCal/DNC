package unikl.disco.curves;

import unikl.disco.numbers.Num;

public interface LinearSegment {
    Num f(Num x);

    Num getX();

    void setX(Num x);

    Num getY();

    void setY(Num y);

    Num getGrad();

    void setGrad(Num grad);

    boolean isLeftopen();

    void setLeftopen(boolean leftopen);

    Num getXIntersectionWith(LinearSegment other);

    LinearSegment copy();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();

    @Override
    String toString();
}
