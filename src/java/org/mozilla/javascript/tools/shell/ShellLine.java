/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.InputStream
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.nio.charset.Charset
 */
package org.mozilla.javascript.tools.shell;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.ShellConsole;

@Deprecated
public class ShellLine {
    @Deprecated
    public static InputStream getStream(Scriptable scriptable) {
        ShellConsole shellConsole = ShellConsole.getConsole(scriptable, Charset.defaultCharset());
        if (shellConsole != null) {
            return shellConsole.getIn();
        }
        return null;
    }
}

