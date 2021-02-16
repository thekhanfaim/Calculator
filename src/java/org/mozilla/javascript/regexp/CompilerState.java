/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.regexp.RENode;

class CompilerState {
    int backReferenceLimit;
    int classCount;
    int cp;
    char[] cpbegin;
    int cpend;
    Context cx;
    int flags;
    int maxBackReference;
    int parenCount;
    int parenNesting;
    int progLength;
    RENode result;

    CompilerState(Context context, char[] arrc, int n, int n2) {
        this.cx = context;
        this.cpbegin = arrc;
        this.cp = 0;
        this.cpend = n;
        this.flags = n2;
        this.backReferenceLimit = Integer.MAX_VALUE;
        this.maxBackReference = 0;
        this.parenCount = 0;
        this.classCount = 0;
        this.progLength = 0;
    }
}

