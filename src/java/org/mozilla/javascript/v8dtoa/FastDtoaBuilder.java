/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.util.Arrays
 */
package org.mozilla.javascript.v8dtoa;

import java.util.Arrays;

public class FastDtoaBuilder {
    static final char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    final char[] chars = new char[25];
    int end = 0;
    boolean formatted = false;
    int point;

    private void toExponentialFormat(int n, int n2) {
        int n3;
        int n4 = this.end;
        if (n4 - n > 1) {
            int n5 = n + 1;
            char[] arrc = this.chars;
            System.arraycopy((Object)arrc, (int)n5, (Object)arrc, (int)(n5 + 1), (int)(n4 - n5));
            this.chars[n5] = 46;
            this.end = 1 + this.end;
        }
        char[] arrc = this.chars;
        int n6 = this.end;
        this.end = n3 = n6 + 1;
        arrc[n6] = 101;
        int n7 = 43;
        int n8 = n2 - 1;
        if (n8 < 0) {
            n7 = 45;
            n8 = -n8;
        }
        int n9 = n3 + 1;
        this.end = n9++;
        arrc[n3] = n7;
        if (n8 > 99) {
            n9 += 2;
        } else if (n8 > 9) {
            // empty if block
        }
        int n10 = n9;
        this.end = n10 + 1;
        do {
            int n11 = n8 % 10;
            char[] arrc2 = this.chars;
            int n12 = n10 - 1;
            arrc2[n10] = digits[n11];
            if ((n8 /= 10) == 0) {
                return;
            }
            n10 = n12;
        } while (true);
    }

    private void toFixedFormat(int n, int n2) {
        int n3 = this.point;
        int n4 = this.end;
        if (n3 < n4) {
            if (n2 > 0) {
                char[] arrc = this.chars;
                System.arraycopy((Object)arrc, (int)n3, (Object)arrc, (int)(n3 + 1), (int)(n4 - n3));
                this.chars[this.point] = 46;
                this.end = 1 + this.end;
                return;
            }
            int n5 = n + 2 - n2;
            char[] arrc = this.chars;
            System.arraycopy((Object)arrc, (int)n, (Object)arrc, (int)n5, (int)(n4 - n));
            char[] arrc2 = this.chars;
            arrc2[n] = 48;
            arrc2[n + 1] = 46;
            if (n2 < 0) {
                Arrays.fill((char[])arrc2, (int)(n + 2), (int)n5, (char)'0');
            }
            this.end += 2 - n2;
            return;
        }
        if (n3 > n4) {
            Arrays.fill((char[])this.chars, (int)n4, (int)n3, (char)'0');
            int n6 = this.end;
            this.end = n6 + (this.point - n6);
        }
    }

    void append(char c) {
        char[] arrc = this.chars;
        int n = this.end;
        this.end = n + 1;
        arrc[n] = c;
    }

    void decreaseLast() {
        char[] arrc = this.chars;
        int n = -1 + this.end;
        arrc[n] = (char)(-1 + arrc[n]);
    }

    public String format() {
        if (!this.formatted) {
            int n = this.chars[0] == '-' ? 1 : 0;
            int n2 = this.point - n;
            if (n2 >= -5 && n2 <= 21) {
                this.toFixedFormat(n, n2);
            } else {
                this.toExponentialFormat(n, n2);
            }
            this.formatted = true;
        }
        return new String(this.chars, 0, this.end);
    }

    public void reset() {
        this.end = 0;
        this.formatted = false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[chars:");
        stringBuilder.append(new String(this.chars, 0, this.end));
        stringBuilder.append(", point:");
        stringBuilder.append(this.point);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

