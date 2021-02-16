/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.Component
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.Component;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.MessageDialogWrapper;
import org.mozilla.javascript.tools.debugger.SwingGui;

class RunProxy
implements Runnable {
    static final int ENTER_INTERRUPT = 4;
    static final int LOAD_FILE = 2;
    static final int OPEN_FILE = 1;
    static final int UPDATE_SOURCE_TEXT = 3;
    String alertMessage;
    private SwingGui debugGui;
    String fileName;
    Dim.StackFrame lastFrame;
    Dim.SourceInfo sourceInfo;
    String text;
    String threadTitle;
    private int type;

    public RunProxy(SwingGui swingGui, int n) {
        this.debugGui = swingGui;
        this.type = n;
    }

    public void run() {
        int n = this.type;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n == 4) {
                        this.debugGui.enterInterruptImpl(this.lastFrame, this.threadTitle, this.alertMessage);
                        return;
                    }
                    throw new IllegalArgumentException(String.valueOf((int)this.type));
                }
                String string2 = this.sourceInfo.url();
                if (!this.debugGui.updateFileWindow(this.sourceInfo) && !string2.equals((Object)"<stdin>")) {
                    this.debugGui.createFileWindow(this.sourceInfo, -1);
                }
                return;
            }
            try {
                this.debugGui.dim.evalScript(this.fileName, this.text);
                return;
            }
            catch (RuntimeException runtimeException) {
                SwingGui swingGui = this.debugGui;
                String string3 = runtimeException.getMessage();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Run error for ");
                stringBuilder.append(this.fileName);
                MessageDialogWrapper.showMessageDialog((Component)swingGui, string3, stringBuilder.toString(), 0);
                return;
            }
        }
        try {
            this.debugGui.dim.compileScript(this.fileName, this.text);
            return;
        }
        catch (RuntimeException runtimeException) {
            SwingGui swingGui = this.debugGui;
            String string4 = runtimeException.getMessage();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error Compiling ");
            stringBuilder.append(this.fileName);
            MessageDialogWrapper.showMessageDialog((Component)swingGui, string4, stringBuilder.toString(), 0);
            return;
        }
    }
}

