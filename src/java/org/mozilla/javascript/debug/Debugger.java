/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;

public interface Debugger {
    public DebugFrame getFrame(Context var1, DebuggableScript var2);

    public void handleCompilationDone(Context var1, DebuggableScript var2, String var3);
}

