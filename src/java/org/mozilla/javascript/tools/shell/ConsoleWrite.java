/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 */
package org.mozilla.javascript.tools.shell;

import org.mozilla.javascript.tools.shell.ConsoleTextArea;

class ConsoleWrite
implements Runnable {
    private String str;
    private ConsoleTextArea textArea;

    public ConsoleWrite(ConsoleTextArea consoleTextArea, String string2) {
        this.textArea = consoleTextArea;
        this.str = string2;
    }

    public void run() {
        this.textArea.write(this.str);
    }
}

