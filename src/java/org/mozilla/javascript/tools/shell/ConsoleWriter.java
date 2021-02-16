/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.OutputStream
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.StringBuffer
 *  javax.swing.SwingUtilities
 */
package org.mozilla.javascript.tools.shell;

import java.io.OutputStream;
import javax.swing.SwingUtilities;
import org.mozilla.javascript.tools.shell.ConsoleTextArea;
import org.mozilla.javascript.tools.shell.ConsoleWrite;

class ConsoleWriter
extends OutputStream {
    private StringBuffer buffer;
    private ConsoleTextArea textArea;

    public ConsoleWriter(ConsoleTextArea consoleTextArea) {
        this.textArea = consoleTextArea;
        this.buffer = new StringBuffer();
    }

    private void flushBuffer() {
        String string2 = this.buffer.toString();
        this.buffer.setLength(0);
        SwingUtilities.invokeLater((Runnable)new ConsoleWrite(this.textArea, string2));
    }

    public void close() {
        this.flush();
    }

    public void flush() {
        ConsoleWriter consoleWriter = this;
        synchronized (consoleWriter) {
            if (this.buffer.length() > 0) {
                this.flushBuffer();
            }
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void write(int n) {
        ConsoleWriter consoleWriter = this;
        synchronized (consoleWriter) {
            this.buffer.append((char)n);
            if (n == 10) {
                this.flushBuffer();
            }
            return;
        }
    }

    public void write(char[] arrc, int n, int n2) {
        ConsoleWriter consoleWriter = this;
        synchronized (consoleWriter) {
            for (int i = n; i < n2; ++i) {
                this.buffer.append(arrc[i]);
                if (arrc[i] != '\n') continue;
                this.flushBuffer();
            }
            return;
        }
    }
}

