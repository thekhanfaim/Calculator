/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.tools.idswitch;

public class IdValuePair {
    public final String id;
    public final int idLength;
    private int lineNumber;
    public final String value;

    public IdValuePair(String string2, String string3) {
        this.idLength = string2.length();
        this.id = string2;
        this.value = string3;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int n) {
        this.lineNumber = n;
    }
}

