/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.regexp.REGlobalData;
import org.mozilla.javascript.regexp.REProgState;

class REBackTrackData {
    final int continuationOp;
    final int continuationPc;
    final int cp;
    final int op;
    final long[] parens;
    final int pc;
    final REBackTrackData previous;
    final REProgState stateStackTop;

    REBackTrackData(REGlobalData rEGlobalData, int n, int n2, int n3, int n4, int n5) {
        this.previous = rEGlobalData.backTrackStackTop;
        this.op = n;
        this.pc = n2;
        this.cp = n3;
        this.continuationOp = n4;
        this.continuationPc = n5;
        this.parens = rEGlobalData.parens;
        this.stateStackTop = rEGlobalData.stateStackTop;
    }
}

