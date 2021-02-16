/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.Object
 */
package org.mozilla.javascript.regexp;

import java.io.Serializable;

final class RECharSet
implements Serializable {
    private static final long serialVersionUID = 7931787979395898394L;
    volatile transient byte[] bits;
    volatile transient boolean converted;
    final int length;
    final boolean sense;
    final int startIndex;
    final int strlength;

    RECharSet(int n, int n2, int n3, boolean bl) {
        this.length = n;
        this.startIndex = n2;
        this.strlength = n3;
        this.sense = bl;
    }
}

