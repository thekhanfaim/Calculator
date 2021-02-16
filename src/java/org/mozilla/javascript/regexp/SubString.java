/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.regexp;

public class SubString {
    int index;
    int length;
    String str;

    public SubString() {
    }

    public SubString(String string2) {
        this.str = string2;
        this.index = 0;
        this.length = string2.length();
    }

    public SubString(String string2, int n, int n2) {
        this.str = string2;
        this.index = n;
        this.length = n2;
    }

    public String toString() {
        String string2 = this.str;
        if (string2 == null) {
            return "";
        }
        int n = this.index;
        return string2.substring(n, n + this.length);
    }
}

