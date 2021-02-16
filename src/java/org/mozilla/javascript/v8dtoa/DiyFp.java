/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.v8dtoa;

class DiyFp {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final int kSignificandSize = 64;
    static final long kUint64MSB = Long.MIN_VALUE;
    private int e;
    private long f;

    DiyFp() {
        this.f = 0L;
        this.e = 0;
    }

    DiyFp(long l, int n) {
        this.f = l;
        this.e = n;
    }

    static DiyFp minus(DiyFp diyFp, DiyFp diyFp2) {
        DiyFp diyFp3 = new DiyFp(diyFp.f, diyFp.e);
        diyFp3.subtract(diyFp2);
        return diyFp3;
    }

    static DiyFp normalize(DiyFp diyFp) {
        DiyFp diyFp2 = new DiyFp(diyFp.f, diyFp.e);
        diyFp2.normalize();
        return diyFp2;
    }

    static DiyFp times(DiyFp diyFp, DiyFp diyFp2) {
        DiyFp diyFp3 = new DiyFp(diyFp.f, diyFp.e);
        diyFp3.multiply(diyFp2);
        return diyFp3;
    }

    private static boolean uint64_gte(long l, long l2) {
        boolean bl;
        block3 : {
            block2 : {
                if (l == l2) break block2;
                boolean bl2 = l > l2;
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

    int e() {
        return this.e;
    }

    long f() {
        return this.f;
    }

    void multiply(DiyFp diyFp) {
        long l = this.f;
        long l2 = l >>> 32;
        long l3 = l & 0xFFFFFFFFL;
        long l4 = diyFp.f;
        long l5 = l4 >>> 32;
        long l6 = l4 & 0xFFFFFFFFL;
        long l7 = l2 * l5;
        long l8 = l3 * l5;
        long l9 = l2 * l6;
        long l10 = 0x80000000L + ((l3 * l6 >>> 32) + (l9 & 0xFFFFFFFFL) + (l8 & 0xFFFFFFFFL));
        long l11 = l7 + (l9 >>> 32) + (l8 >>> 32) + (l10 >>> 32);
        this.e += 64 + diyFp.e;
        this.f = l11;
    }

    void normalize() {
        AssertionError assertionError;
        if (this.f != 0L) {
            long l = this.f;
            int n = this.e;
            while ((-18014398509481984L & l) == 0L) {
                l <<= 10;
                n -= 10;
            }
            while ((Long.MIN_VALUE & l) == 0L) {
                l <<= 1;
                --n;
            }
            this.f = l;
            this.e = n;
            return;
        }
        assertionError = new AssertionError();
        throw assertionError;
    }

    void setE(int n) {
        this.e = n;
    }

    void setF(long l) {
        this.f = l;
    }

    void subtract(DiyFp diyFp) {
        if (this.e == diyFp.e) {
            if (DiyFp.uint64_gte(this.f, diyFp.f)) {
                this.f -= diyFp.f;
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[DiyFp f:");
        stringBuilder.append(this.f);
        stringBuilder.append(", e:");
        stringBuilder.append(this.e);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

