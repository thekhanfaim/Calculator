/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Throwable
 */
package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface DebugFrame {
    public void onDebuggerStatement(Context var1);

    public void onEnter(Context var1, Scriptable var2, Scriptable var3, Object[] var4);

    public void onExceptionThrown(Context var1, Throwable var2);

    public void onExit(Context var1, boolean var2, Object var3);

    public void onLineChange(Context var1, int var2);
}

