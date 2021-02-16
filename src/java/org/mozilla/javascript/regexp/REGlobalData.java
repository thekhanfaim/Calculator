/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.regexp.REBackTrackData;
import org.mozilla.javascript.regexp.RECompiled;
import org.mozilla.javascript.regexp.REProgState;

class REGlobalData {
    REBackTrackData backTrackStackTop;
    int cp;
    boolean multiline;
    long[] parens;
    RECompiled regexp;
    int skipped;
    REProgState stateStackTop;

    REGlobalData() {
    }

    int parensIndex(int n) {
        return (int)this.parens[n];
    }

    int parensLength(int n) {
        return (int)(this.parens[n] >>> 32);
    }

    void setParens(int n, int n2, int n3) {
        long[] arrl;
        long[] arrl2;
        REBackTrackData rEBackTrackData = this.backTrackStackTop;
        if (rEBackTrackData != null && (arrl = rEBackTrackData.parens) == (arrl2 = this.parens)) {
            this.parens = (long[])arrl2.clone();
        }
        this.parens[n] = 0xFFFFFFFFL & (long)n2 | (long)n3 << 32;
    }
}

