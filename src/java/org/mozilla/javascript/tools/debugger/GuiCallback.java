/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.tools.debugger;

import org.mozilla.javascript.tools.debugger.Dim;

public interface GuiCallback {
    public void dispatchNextGuiEvent() throws InterruptedException;

    public void enterInterrupt(Dim.StackFrame var1, String var2, String var3);

    public boolean isGuiEventThread();

    public void updateSourceText(Dim.SourceInfo var1);
}

