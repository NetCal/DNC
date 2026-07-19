package org.networkcalculus.dnc.backend.number;

import java.math.BigInteger;

public class Number {
    private enum Type {
        NUMBER, POSITIVE_INFINITY, NEGATIVE_INFINITY, NAN
    }

    ;

    public static final Number ZERO = new Number(0);
    public static final Number ONE = new Number(1);
    public static final Number POSITIVE_INFINITY = new Number(Type.POSITIVE_INFINITY);
    public static final Number NEGATIVE_INFINITY = new Number(Type.NEGATIVE_INFINITY);

    protected BigInteger nom, den;
    protected Type type;

    /* Constructors */
    private Number(Type t) {
        type = t;
    }

    public Number(long n) {
        nom = BigInteger.valueOf(n);
        den = BigInteger.ONE;
        type = Type.NUMBER;
    }

    public Number(double n) {
        if (n == 0.0) {
            nom = BigInteger.ZERO;
            den = BigInteger.ONE;
            type = Type.NUMBER;
            return;
        }
        if (Double.isInfinite(n)) {
            type = n == Double.POSITIVE_INFINITY ? Type.POSITIVE_INFINITY
                    : Type.NEGATIVE_INFINITY;
            return;
        }
        if (Double.isNaN(n)) {
            type = Type.NAN;
            System.out.println("NUmber.class: Value provided is not a number!");
            return;
        }

        String s = Double.toString(n);

        s = new java.math.BigDecimal(s).toPlainString();

        int dotPos = s.indexOf('.');
        if (dotPos == -1) {
            // No decimal point / integer
            nom = new BigInteger(s);
            den = BigInteger.ONE;
        } else {
            int decimalPlaces = s.length() - dotPos - 1;
            String digits = s.replace(".", "");
            BigInteger rawNom = new BigInteger(digits);
            BigInteger rawDen = BigInteger.TEN.pow(decimalPlaces);
            BigInteger gcd = rawNom.gcd(rawDen);
            nom = rawNom.divide(gcd);
            den = rawDen.divide(gcd);
        }
        type = Type.NUMBER;
    }

    public Number(BigInteger n) {
        nom = n;
        den = BigInteger.ONE;
        type = Type.NUMBER;
    }

    public Number(long n, long d) {
        if (d == 0) {
            type = Type.NAN;
            return;
        }
        if (d < 0) {
            n *= -1;
            d = Math.abs(d);
        }
        long gcd = gcd(n, d);
        nom = BigInteger.valueOf(n / gcd);
        den = BigInteger.valueOf(d / gcd);
        type = Type.NUMBER;
    }

    public Number(BigInteger n, BigInteger d) {
        assert d.compareTo(BigInteger.ZERO) != 0 : "Denominator must not be 0.";
        if (d.compareTo(BigInteger.ZERO) < 0) {
            n = n.negate();
            d = d.abs();
        }
        BigInteger gcd = n.gcd(d);
        nom = n.divide(gcd);
        den = d.divide(gcd);
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
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) < 0;
        return type == Type.NEGATIVE_INFINITY && r.type != Type.NEGATIVE_INFINITY
                || type != Type.POSITIVE_INFINITY && r.type == Type.POSITIVE_INFINITY;
    }

    public boolean leq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) <= 0;
        return type == Type.NEGATIVE_INFINITY || r.type == Type.POSITIVE_INFINITY;
    }

    public boolean eq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) == 0;
        return type == r.type;
    }

    public boolean neq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) != 0;
        return type != r.type;
    }

    public boolean geq(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) >= 0;
        return type == Type.POSITIVE_INFINITY || r.type == Type.NEGATIVE_INFINITY;
    }

    public boolean gt(Number r) {
        if (type == Type.NUMBER && r.type == Type.NUMBER)
            return nom.multiply(r.den).compareTo(den.multiply(r.nom)) > 0;
        return type == Type.POSITIVE_INFINITY && r.type != Type.POSITIVE_INFINITY
                || type != Type.NEGATIVE_INFINITY && r.type == Type.NEGATIVE_INFINITY;
    }

    /* Arithmethic operators */
    public Number add(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Addition operands must not be infinite.";
        return new Number(
                nom.multiply(r.den).add(den.multiply(r.nom)),
                den.multiply(r.den));
    }

    public Number sub(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Subtraction operands must not be infinite.";
        return new Number(
                nom.multiply(r.den).subtract(den.multiply(r.nom)),
                den.multiply(r.den));
    }

    public Number mul(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Multiplication operands must not be infinite.";
        return new Number(nom.multiply(r.nom), den.multiply(r.den));
    }

    public Number div(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Division operands must not be infinite.";
        assert r.nom.compareTo(BigInteger.ZERO) != 0 : "Division must not be by 0.";
        return new Number(nom.multiply(r.den), den.multiply(r.nom));
    }

    public Number mod(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Modulo operands must not be infinite.";
        assert r.nom.compareTo(BigInteger.ZERO) != 0 : "Modulo must not be by 0.";
        return new Number(
                nom.multiply(r.den).mod(den.multiply(r.nom)),
                den.multiply(r.den));
    }

    public Number min(Number r) {
        return new Number(lt(r) ? this : r);
    }

    public Number max(Number r) {
        return new Number(gt(r) ? this : r);
    }

    public Number abs() {
        assert type != Type.NAN;
        if (type != Type.NUMBER)
            return new Number(Type.POSITIVE_INFINITY);
        return new Number(nom.abs(), den);
    }

    public Number neg() {
        assert type != Type.NAN;
        if (type == Type.POSITIVE_INFINITY || type == Type.NEGATIVE_INFINITY)
            return new Number(type == Type.NEGATIVE_INFINITY ? Type.POSITIVE_INFINITY
                    : Type.NEGATIVE_INFINITY);
        return new Number(nom.negate(), den);
    }

    public Number floor() {
        if (type != Type.NUMBER)
            return this;
        return new Number(nom.divide(den));
    }

    public Number lcm(Number r) {
        assert type == Type.NUMBER && r.type == Type.NUMBER
                : "Least common multiple operands must not be infinite.";
        return new Number(
                nom.multiply(r.den).multiply(r.nom.multiply(den)).abs()
                        .divide(nom.multiply(r.den).gcd(r.nom.multiply(den))),
                den.multiply(r.den));
    }

    /* Conversion operators */
    public String toString() {
        if (type == Type.POSITIVE_INFINITY || type == Type.NEGATIVE_INFINITY)
            return (type == Type.NEGATIVE_INFINITY ? "-" : "") + "Infinity";
        if (type == Type.NAN)
            return "NaN";
        if (den.compareTo(BigInteger.ONE) == 0)
            return nom.toString();
        return nom.toString() + "/" + den.toString();
    }

    public double toDouble() {
        if (type != Type.NUMBER) {
            return switch (type) {
                case POSITIVE_INFINITY -> Double.POSITIVE_INFINITY;
                case NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY;
                case NAN -> Double.NaN;
                default -> 0.0;
            };
        }
        return nom.doubleValue() / den.doubleValue();
    }

    private static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);       // b could be negative, e.g. gcd(value, -exponent) in the double constructor
        if (a == 0) return b;  // gcd(0, b) = b
        if (b == 0) return a;  // gcd(a, 0) = a, missing in your version
        while (b > 0) {
            long c = a % b;
            a = b;
            b = c;
        }
        return a;
    }

    public boolean isNaN() {
        return this.type == Type.NAN;
    }

}
