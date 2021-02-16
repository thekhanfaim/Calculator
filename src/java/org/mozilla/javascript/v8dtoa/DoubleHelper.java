/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Object
 */
package org.mozilla.javascript.v8dtoa;

import org.mozilla.javascript.v8dtoa.DiyFp;

public class DoubleHelper {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int kDenormalExponent = -1074;
    private static final int kExponentBias = 1075;
    static final long kExponentMask = 9218868437227405312L;
    static final long kHiddenBit = 0x10000000000000L;
    static final long kSignMask = Long.MIN_VALUE;
    static final long kSignificandMask = 0xFFFFFFFFFFFFFL;
    private static final int kSignificandSize = 52;

    static DiyFp asDiyFp(long l) {
        if (!DoubleHelper.isSpecial(l)) {
            return new DiyFp(DoubleHelper.significand(l), DoubleHelper.exponent(l));
        }
        throw new AssertionError();
    }

    static DiyFp asNormalizedDiyFp(long l) {
        AssertionError assertionError;
        long l2 = DoubleHelper.significand(l);
        int n = DoubleHelper.exponent(l);
        if (l2 != 0L) {
            while ((0x10000000000000L & l2) == 0L) {
                l2 <<= 1;
                --n;
            }
            return new DiyFp(l2 << 11, n - 11);
        }
        assertionError = new AssertionError();
        throw assertionError;
    }

    static int exponent(long l) {
        if (DoubleHelper.isDenormal(l)) {
            return -1074;
        }
        return -1075 + (int)(0xFFFFFFFFL & (9218868437227405312L & l) >>> 52);
    }

    static boolean isDenormal(long l) {
        return (9218868437227405312L & l) == 0L;
    }

    static boolean isInfinite(long l) {
        return (l & 9218868437227405312L) == 9218868437227405312L && (0xFFFFFFFFFFFFFL & l) == 0L;
    }

    static boolean isNan(long l) {
        return (l & 9218868437227405312L) == 9218868437227405312L && (0xFFFFFFFFFFFFFL & l) != 0L;
    }

    static boolean isSpecial(long l) {
        return (l & 9218868437227405312L) == 9218868437227405312L;
    }

    static void normalizedBoundaries(long l, DiyFp diyFp, DiyFp diyFp2) {
        DiyFp diyFp3 = DoubleHelper.asDiyFp(l);
        boolean bl = diyFp3.f() == 0x10000000000000L;
        diyFp2.setF(1L + (diyFp3.f() << 1));
        diyFp2.setE(diyFp3.e() - 1);
        diyFp2.normalize();
        if (bl && diyFp3.e() != -1074) {
            diyFp.setF((diyFp3.f() << 2) - 1L);
            diyFp.setE(diyFp3.e() - 2);
        } else {
            diyFp.setF((diyFp3.f() << 1) - 1L);
            diyFp.setE(diyFp3.e() - 1);
        }
        diyFp.setF(diyFp.f() << diyFp.e() - diyFp2.e());
        diyFp.setE(diyFp2.e());
    }

    static int sign(long l) {
        if ((Long.MIN_VALUE & l) == 0L) {
            return 1;
        }
        return -1;
    }

    static long significand(long l) {
        long l2 = 0xFFFFFFFFFFFFFL & l;
        if (!DoubleHelper.isDenormal(l)) {
            return 0x10000000000000L + l2;
        }
        return l2;
    }
}

