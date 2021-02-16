/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Double
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.v8dtoa;

import org.mozilla.javascript.v8dtoa.CachedPowers;
import org.mozilla.javascript.v8dtoa.DiyFp;
import org.mozilla.javascript.v8dtoa.DoubleHelper;
import org.mozilla.javascript.v8dtoa.FastDtoaBuilder;

public class FastDtoa {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final int kFastDtoaMaximalLength = 17;
    static final int kTen4 = 10000;
    static final int kTen5 = 100000;
    static final int kTen6 = 1000000;
    static final int kTen7 = 10000000;
    static final int kTen8 = 100000000;
    static final int kTen9 = 1000000000;
    static final int maximal_target_exponent = -32;
    static final int minimal_target_exponent = -60;

    static long biggestPowerTen(int n, int n2) {
        int n3;
        int n4;
        switch (n2) {
            default: {
                n4 = 0;
                n3 = 0;
                break;
            }
            case 30: 
            case 31: 
            case 32: {
                if (1000000000 <= n) {
                    n4 = 1000000000;
                    n3 = 9;
                    break;
                }
            }
            case 27: 
            case 28: 
            case 29: {
                if (100000000 <= n) {
                    n4 = 100000000;
                    n3 = 8;
                    break;
                }
            }
            case 24: 
            case 25: 
            case 26: {
                if (10000000 <= n) {
                    n4 = 10000000;
                    n3 = 7;
                    break;
                }
            }
            case 20: 
            case 21: 
            case 22: 
            case 23: {
                if (1000000 <= n) {
                    n4 = 1000000;
                    n3 = 6;
                    break;
                }
            }
            case 17: 
            case 18: 
            case 19: {
                if (100000 <= n) {
                    n4 = 100000;
                    n3 = 5;
                    break;
                }
            }
            case 14: 
            case 15: 
            case 16: {
                if (10000 <= n) {
                    n4 = 10000;
                    n3 = 4;
                    break;
                }
            }
            case 10: 
            case 11: 
            case 12: 
            case 13: {
                if (1000 <= n) {
                    n4 = 1000;
                    n3 = 3;
                    break;
                }
            }
            case 7: 
            case 8: 
            case 9: {
                if (100 <= n) {
                    n4 = 100;
                    n3 = 2;
                    break;
                }
            }
            case 4: 
            case 5: 
            case 6: {
                if (10 <= n) {
                    n4 = 10;
                    n3 = 1;
                    break;
                }
            }
            case 1: 
            case 2: 
            case 3: {
                if (1 <= n) {
                    n4 = 1;
                    n3 = 0;
                    break;
                }
            }
            case 0: {
                n4 = 0;
                n3 = -1;
            }
        }
        return (long)n4 << 32 | 0xFFFFFFFFL & (long)n3;
    }

    static boolean digitGen(DiyFp diyFp, DiyFp diyFp2, DiyFp diyFp3, FastDtoaBuilder fastDtoaBuilder, int n) {
        AssertionError assertionError;
        if (diyFp.e() == diyFp2.e() && diyFp2.e() == diyFp3.e()) {
            if (FastDtoa.uint64_lte(1L + diyFp.f(), diyFp3.f() - 1L)) {
                if (-60 <= diyFp2.e() && diyFp2.e() <= -32) {
                    long l = 1L;
                    DiyFp diyFp4 = new DiyFp(diyFp.f() - l, diyFp.e());
                    DiyFp diyFp5 = new DiyFp(l + diyFp3.f(), diyFp3.e());
                    DiyFp diyFp6 = DiyFp.minus(diyFp5, diyFp4);
                    DiyFp diyFp7 = new DiyFp(1L << -diyFp2.e(), diyFp2.e());
                    int n2 = (int)(0xFFFFFFFFL & diyFp5.f() >>> -diyFp7.e());
                    long l2 = diyFp5.f() & diyFp7.f() - 1L;
                    long l3 = FastDtoa.biggestPowerTen(n2, 64 - -diyFp7.e());
                    int n3 = (int)(0xFFFFFFFFL & l3 >>> 32);
                    int n4 = 1 + (int)(l3 & 0xFFFFFFFFL);
                    int n5 = n3;
                    int n6 = n2;
                    while (n4 > 0) {
                        fastDtoaBuilder.append((char)(48 + n6 / n5));
                        int n7 = n6 % n5;
                        int n8 = n4 - 1;
                        long l4 = l2 + ((long)n7 << -diyFp7.e());
                        if (l4 < diyFp6.f()) {
                            fastDtoaBuilder.point = n8 + (fastDtoaBuilder.end - n);
                            long l5 = DiyFp.minus(diyFp5, diyFp2).f();
                            long l6 = diyFp6.f();
                            long l7 = (long)n5 << -diyFp7.e();
                            return FastDtoa.roundWeed(fastDtoaBuilder, l5, l6, l4, l7, l);
                        }
                        int n9 = n5;
                        DiyFp diyFp8 = diyFp4;
                        n5 = n9 / 10;
                        n4 = n8;
                        n6 = n7;
                        diyFp4 = diyFp8;
                    }
                    DiyFp diyFp9 = diyFp7;
                    DiyFp diyFp10 = diyFp6;
                    do {
                        long l8 = l2 * 5L;
                        l *= 5L;
                        long l9 = 5L * diyFp10.f();
                        DiyFp diyFp11 = diyFp10;
                        diyFp11.setF(l9);
                        diyFp11.setE(1 + diyFp11.e());
                        long l10 = diyFp9.f() >>> 1;
                        DiyFp diyFp12 = diyFp9;
                        diyFp12.setF(l10);
                        diyFp12.setE(1 + diyFp12.e());
                        fastDtoaBuilder.append((char)(48 + (int)(0xFFFFFFFFL & l8 >>> -diyFp12.e())));
                        l2 = l8 & diyFp12.f() - 1L;
                        int n10 = n4 - 1;
                        if (l2 < diyFp11.f()) {
                            fastDtoaBuilder.point = n10 + (fastDtoaBuilder.end - n);
                            long l11 = l * DiyFp.minus(diyFp5, diyFp2).f();
                            long l12 = diyFp11.f();
                            long l13 = diyFp12.f();
                            return FastDtoa.roundWeed(fastDtoaBuilder, l11, l12, l2, l13, l);
                        }
                        n4 = n10;
                        diyFp9 = diyFp12;
                        diyFp10 = diyFp11;
                    } while (true);
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        assertionError = new AssertionError();
        throw assertionError;
    }

    public static boolean dtoa(double d, FastDtoaBuilder fastDtoaBuilder) {
        if (d > 0.0) {
            if (!Double.isNaN((double)d)) {
                if (!Double.isInfinite((double)d)) {
                    return FastDtoa.grisu3(d, fastDtoaBuilder);
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    static boolean grisu3(double d, FastDtoaBuilder fastDtoaBuilder) {
        long l = Double.doubleToLongBits((double)d);
        DiyFp diyFp = DoubleHelper.asNormalizedDiyFp(l);
        DiyFp diyFp2 = new DiyFp();
        DiyFp diyFp3 = new DiyFp();
        DoubleHelper.normalizedBoundaries(l, diyFp2, diyFp3);
        if (diyFp3.e() == diyFp.e()) {
            DiyFp diyFp4 = new DiyFp();
            int n = CachedPowers.getCachedPower(64 + diyFp.e(), -60, -32, diyFp4);
            if (-60 <= 64 + (diyFp.e() + diyFp4.e()) && -32 >= 64 + (diyFp.e() + diyFp4.e())) {
                DiyFp diyFp5 = DiyFp.times(diyFp, diyFp4);
                if (diyFp5.e() == 64 + (diyFp3.e() + diyFp4.e())) {
                    return FastDtoa.digitGen(DiyFp.times(diyFp2, diyFp4), diyFp5, DiyFp.times(diyFp3, diyFp4), fastDtoaBuilder, n);
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static String numberToString(double d) {
        FastDtoaBuilder fastDtoaBuilder = new FastDtoaBuilder();
        if (FastDtoa.numberToString(d, fastDtoaBuilder)) {
            return fastDtoaBuilder.format();
        }
        return null;
    }

    public static boolean numberToString(double d, FastDtoaBuilder fastDtoaBuilder) {
        fastDtoaBuilder.reset();
        if (d < 0.0) {
            fastDtoaBuilder.append('-');
            d = -d;
        }
        return FastDtoa.dtoa(d, fastDtoaBuilder);
    }

    static boolean roundWeed(FastDtoaBuilder fastDtoaBuilder, long l, long l2, long l3, long l4, long l5) {
        long l6;
        long l7 = l - l5;
        long l8 = l + l5;
        for (l6 = l3; l6 < l7 && l2 - l6 >= l4 && (l6 + l4 < l7 || l7 - l6 >= l6 + l4 - l7); l6 += l4) {
            fastDtoaBuilder.decreaseLast();
        }
        if (l6 < l8 && l2 - l6 >= l4 && (l6 + l4 < l8 || l8 - l6 > l6 + l4 - l8)) {
            return false;
        }
        long l9 = 2L * l5 LCMP l6;
        boolean bl = false;
        if (l9 <= 0) {
            long l10 = l6 LCMP l2 - 4L * l5;
            bl = false;
            if (l10 <= 0) {
                bl = true;
            }
        }
        return bl;
    }

    private static boolean uint64_lte(long l, long l2) {
        boolean bl;
        block3 : {
            block2 : {
                if (l == l2) break block2;
                boolean bl2 = l < l2;
                boolean bl3 = l < 0L;
                boolean bl4 = bl2 ^ bl3;
                boolean bl5 = l2 < 0L;
                boolean bl6 = bl4 ^ bl5;
                bl = false;
                if (!bl6) break block3;
            }
            bl = true;
        }
        return bl;
    }
}

