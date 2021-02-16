/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Object
 */
package org.mozilla.javascript.v8dtoa;

public final class DoubleConversion {
    private static final int kDenormalExponent = -1074;
    private static final int kExponentBias = 1075;
    private static final long kExponentMask = 9218868437227405312L;
    private static final long kHiddenBit = 0x10000000000000L;
    private static final int kPhysicalSignificandSize = 52;
    private static final long kSignMask = Long.MIN_VALUE;
    private static final long kSignificandMask = 0xFFFFFFFFFFFFFL;
    private static final int kSignificandSize = 53;

    private DoubleConversion() {
    }

    public static int doubleToInt32(double d) {
        int n = (int)d;
        if ((double)n == d) {
            return n;
        }
        long l = Double.doubleToLongBits((double)d);
        int n2 = DoubleConversion.exponent(l);
        if (n2 > -53 && n2 <= 31) {
            long l2 = DoubleConversion.significand(l);
            int n3 = DoubleConversion.sign(l);
            long l3 = n2 < 0 ? l2 >> -n2 : l2 << n2;
            return n3 * (int)l3;
        }
        return 0;
    }

    private static int exponent(long l) {
        if (DoubleConversion.isDenormal(l)) {
            return -1074;
        }
        return -1075 + (int)((9218868437227405312L & l) >> 52);
    }

    private static boolean isDenormal(long l) {
        return (9218868437227405312L & l) == 0L;
    }

    private static int sign(long l) {
        if ((Long.MIN_VALUE & l) == 0L) {
            return 1;
        }
        return -1;
    }

    private static long significand(long l) {
        long l2 = 0xFFFFFFFFFFFFFL & l;
        if (!DoubleConversion.isDenormal(l)) {
            return 0x10000000000000L + l2;
        }
        return l2;
    }
}

