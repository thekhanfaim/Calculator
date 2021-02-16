/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.regexp.REBackTrackData;

class REProgState {
    final REBackTrackData backTrack;
    final int continuationOp;
    final int continuationPc;
    final int index;
    final int max;
    final int min;
    final REProgState previous;

    REProgState(REProgState rEProgState, int n, int n2, int n3, REBackTrackData rEBackTrackData, int n4, int n5) {
        this.previous = rEProgState;
        this.min = n;
        this.max = n2;
        this.index = n3;
        this.continuationOp = n4;
        this.continuationPc = n5;
        this.backTrack = rEBackTrackData;
    }
}

