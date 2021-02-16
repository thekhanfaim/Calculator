/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Thread
 *  org.mozilla.javascript.tools.shell.Global
 */
package org.mozilla.javascript.tools.shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;

class PipeThread
extends Thread {
    private InputStream from;
    private boolean fromProcess;
    private OutputStream to;

    PipeThread(boolean bl, InputStream inputStream, OutputStream outputStream) {
        this.setDaemon(true);
        this.fromProcess = bl;
        this.from = inputStream;
        this.to = outputStream;
    }

    public void run() {
        try {
            Global.pipe((boolean)this.fromProcess, (InputStream)this.from, (OutputStream)this.to);
            return;
        }
        catch (IOException iOException) {
            throw Context.throwAsScriptRuntimeEx(iOException);
        }
    }
}

