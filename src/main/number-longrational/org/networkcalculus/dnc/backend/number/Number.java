package org.networkcalculus.dnc.backend.number;

public class Number {
    private enum Type {
        NUMBER, POSITIVE_INFINITY, NEGATIVE_INFINITY
    }

    ;

    public static final Number ZERO = new Number(0);
    public static final Number ONE = new Number(1);
    public static final Number POSITIVE_INFINITY = new Number(Type.POSITIVE_INFINITY);
    public static final Number NEGATIVE_INFINITY = new Number(Type.NEGATIVE_INFINITY);

    protected long nom, den;
    protected Type type;

    /* Constructors */
    private Number(Type t) {
        type = t;
    }

    public Number(long n) {
        nom = n;
        den = 1;
        type = Type.NUMBER;
    }

    public Number(double n) {
        if (Double.isInfinite(n)) {
            type = n == Double.POSITIVE_INFINITY ? Type.POSITIVE_INFINITY
                    : Type.NEGATIVE_INFINITY;
            return;
        }
        if (n == 0) {
            nom = 0;
            den = 1;
            return;
        }
        long rawBinary = Double.doubleToLongBits(n);
        boolean negative = (rawBinary & 1L << 63) != 0;
        int exponent = ((int) (rawBinary >> 52) & (1 << 11) - 1) - 1023 - 52;
        long value = 1L << 52 | rawBinary & (1L << 52) - 1;
        if (exponent < 0) {
            long gcd = gcd(value, -exponent);
            nom = negative ? -value : value / gcd;
            double tmp = Math.pow(2, -exponent) / gcd;
            assert Long.MAX_VALUE >= tmp : "Long integer overflow.";
            den = (long) tmp;
        } else {
            double tmp = negative ? -value : value * Math.pow(2, exponent);
            assert Long.MAX_VALUE >= tmp && tmp >= Long.MIN_VALUE
                    : "Long integer overflow.";
            nom = (long) tmp;
            den = 1;
        }
        type = Type.NUMBER;
    }

    public Number(long n, long d) {
        assert d != 0 : "Denominator must not be 0.";
        if (d < 0) {
            n *= -1;
            d = Math.abs(d);
        }
        long gcd = gcd(n, d);
        nom = n / gcd;
        den = d / gcd;
        type = Type.NUMBER;
    }

    public Number(Number n) {
        nom = n.nom;
        den = n.den;
        type = n.type;
    }

    /* Comparison operators */
    public boolean lt(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den < den * r.nom;
        return type == Type.NEGATIVE_INFINITY && r.type != Type.NEGATIVE_INFINITY
                || type != Type.POSITIVE_INFINITY && r.type == Type.POSITIVE_INFINITY;
    }

    public boolean leq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den <= den * r.nom;
        return type == Type.NEGATIVE_INFINITY || r.type == Type.POSITIVE_INFINITY;
    }

    public boolean eq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den == den * r.nom;
        return type == r.type;
    }

    public boolean neq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den != den * r.nom;
        return type != r.type;
    }

    public boolean geq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den >= den * r.nom;
        return type == Type.POSITIVE_INFINITY || r.type == Type.NEGATIVE_INFINITY;
    }

    public boolean gt(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom * r.den > den * r.nom;
        return type == Type.POSITIVE_INFINITY && r.type != Type.POSITIVE_INFINITY
                || type != Type.NEGATIVE_INFINITY && r.type == Type.NEGATIVE_INFINITY;
    }

    /* Arithmethic operators */
    public Number add(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Addition operands must not be infinite.";
        return new Number(nom * r.den + den * r.nom, den * r.den);
    }

    public Number sub(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Subtraction operands must not be infinite.";
        return new Number(nom * r.den - den * r.nom, den * r.den);
    }

    public Number mul(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Multiplication operands must not be infinite.";
        return new Number(nom * r.nom, den * r.den);
    }

    public Number div(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Division operands must not be infinite.";
        assert r.nom != 0 : "Division must not be by 0.";
        return new Number(nom * r.den, den * r.nom);
    }

    public Number mod(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Modulo operands must not be infinite.";
        assert r.nom != 0 : "Division must not be by 0.";
        return new Number((nom * r.den) % (den * r.nom), den * r.den);
    }

    public Number min(Number r) {
        return new Number(lt(r) ? this : r);
    }

    public Number max(Number r) {
        return new Number(gt(r) ? this : r);
    }

    public Number abs() {
        if (type != Type.NUMBER)
            return new Number(Type.POSITIVE_INFINITY);
        return new Number(Math.abs(nom), den);
    }

    public Number neg() {
        if (type != Type.NUMBER)
            return new Number(type == Type.NEGATIVE_INFINITY ? Type.POSITIVE_INFINITY
                    : Type.NEGATIVE_INFINITY);
        return new Number(-nom, den);
    }

    public Number floor() {
        if (type != Type.NUMBER)
            return this;
        return new Number(nom / den);
    }

    public Number lcm(Number r) {
        assert type == Type.NUMBER && type == Type.NUMBER
                : "Least common multiple operands must not be infinite.";
        return new Number(
                Math.abs(nom * r.den * r.nom * den) / gcd(nom * r.den, r.nom * den),
                den * r.den);
    }

    /* Conversion operators */
    public String toString() {
        if (type != Type.NUMBER)
            return (type == Type.NEGATIVE_INFINITY ? "-" : "") + "Infinity";
        if (den == 1)
            return "" + nom;
        return nom + "/" + den;
    }

    public double toDouble() {
        if (type != Type.NUMBER)
            return type == Type.POSITIVE_INFINITY ? Double.POSITIVE_INFINITY
                    : Double.NEGATIVE_INFINITY;
        if (den == 0 && nom == 0)
            return 0.0;
        return (double) nom / den;
    }

    private static long gcd(long a, long b) {
        a = Math.abs(a);
        while (b > 0) {
            long c = a % b;
            a = b;
            b = c;
        }
        return a;
    }
}
