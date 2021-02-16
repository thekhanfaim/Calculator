/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.regexp;

import java.io.Serializable;
import org.mozilla.javascript.regexp.RECharSet;

class RECompiled
implements Serializable {
    private static final long serialVersionUID = -6144956577595844213L;
    int anchorCh = -1;
    int classCount;
    RECharSet[] classList;
    int flags;
    int parenCount;
    byte[] program;
    final char[] source;

    RECompiled(String string2) {
        this.source = string2.toCharArray();
    }
}

