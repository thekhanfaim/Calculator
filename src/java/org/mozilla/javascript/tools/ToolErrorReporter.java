/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.PrintStream
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.text.MessageFormat
 *  java.util.Locale
 *  java.util.MissingResourceException
 *  java.util.ResourceBundle
 */
package org.mozilla.javascript.tools;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.WrappedException;

public class ToolErrorReporter
implements ErrorReporter {
    private static final String messagePrefix = "js: ";
    private PrintStream err;
    private boolean hasReportedErrorFlag;
    private boolean reportWarnings;

    public ToolErrorReporter(boolean bl) {
        this(bl, System.err);
    }

    public ToolErrorReporter(boolean bl, PrintStream printStream) {
        this.reportWarnings = bl;
        this.err = printStream;
    }

    private String buildIndicator(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < n - 1; ++i) {
            stringBuilder.append(".");
        }
        stringBuilder.append("^");
        return stringBuilder.toString();
    }

    private static String getExceptionMessage(RhinoException rhinoException) {
        if (rhinoException instanceof JavaScriptException) {
            return ToolErrorReporter.getMessage("msg.uncaughtJSException", rhinoException.details());
        }
        if (rhinoException instanceof EcmaError) {
            return ToolErrorReporter.getMessage("msg.uncaughtEcmaError", rhinoException.details());
        }
        if (rhinoException instanceof EvaluatorException) {
            return rhinoException.details();
        }
        return rhinoException.toString();
    }

    public static String getMessage(String string2) {
        return ToolErrorReporter.getMessage(string2, (Object[])null);
    }

    public static String getMessage(String string2, Object object, Object object2) {
        return ToolErrorReporter.getMessage(string2, new Object[]{object, object2});
    }

    public static String getMessage(String string2, String string3) {
        return ToolErrorReporter.getMessage(string2, new Object[]{string3});
    }

    public static String getMessage(String string2, Object[] arrobject) {
        String string3;
        block2 : {
            Context context = Context.getCurrentContext();
            Locale locale = context == null ? Locale.getDefault() : context.getLocale();
            ResourceBundle resourceBundle = ResourceBundle.getBundle((String)"org.mozilla.javascript.tools.resources.Messages", (Locale)locale);
            try {
                string3 = resourceBundle.getString(string2);
                if (arrobject != null) break block2;
                return string3;
            }
            catch (MissingResourceException missingResourceException) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("no message resource found for message property ");
                stringBuilder.append(string2);
                throw new RuntimeException(stringBuilder.toString());
            }
        }
        return new MessageFormat(string3).format((Object)arrobject);
    }

    private void reportErrorMessage(String string2, String string3, int n, String string4, int n2, boolean bl) {
        String string5;
        if (n > 0) {
            String string6 = String.valueOf((int)n);
            string5 = string3 != null ? ToolErrorReporter.getMessage("msg.format3", new Object[]{string3, string6, string2}) : ToolErrorReporter.getMessage("msg.format2", new Object[]{string6, string2});
        } else {
            string5 = ToolErrorReporter.getMessage("msg.format1", new Object[]{string2});
        }
        if (bl) {
            string5 = ToolErrorReporter.getMessage("msg.warning", string5);
        }
        PrintStream printStream = this.err;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messagePrefix);
        stringBuilder.append(string5);
        printStream.println(stringBuilder.toString());
        if (string4 != null) {
            PrintStream printStream2 = this.err;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(messagePrefix);
            stringBuilder2.append(string4);
            printStream2.println(stringBuilder2.toString());
            PrintStream printStream3 = this.err;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(messagePrefix);
            stringBuilder3.append(this.buildIndicator(n2));
            printStream3.println(stringBuilder3.toString());
        }
    }

    public static void reportException(ErrorReporter errorReporter, RhinoException rhinoException) {
        if (errorReporter instanceof ToolErrorReporter) {
            ((ToolErrorReporter)errorReporter).reportException(rhinoException);
            return;
        }
        errorReporter.error(ToolErrorReporter.getExceptionMessage(rhinoException), rhinoException.sourceName(), rhinoException.lineNumber(), rhinoException.lineSource(), rhinoException.columnNumber());
    }

    @Override
    public void error(String string2, String string3, int n, String string4, int n2) {
        this.hasReportedErrorFlag = true;
        this.reportErrorMessage(string2, string3, n, string4, n2, false);
    }

    public boolean hasReportedError() {
        return this.hasReportedErrorFlag;
    }

    public boolean isReportingWarnings() {
        return this.reportWarnings;
    }

    public void reportException(RhinoException rhinoException) {
        if (rhinoException instanceof WrappedException) {
            ((WrappedException)rhinoException).printStackTrace(this.err);
            return;
        }
        String string2 = SecurityUtilities.getSystemProperty("line.separator");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ToolErrorReporter.getExceptionMessage(rhinoException));
        stringBuilder.append(string2);
        stringBuilder.append(rhinoException.getScriptStackTrace());
        this.reportErrorMessage(stringBuilder.toString(), rhinoException.sourceName(), rhinoException.lineNumber(), rhinoException.lineSource(), rhinoException.columnNumber(), false);
    }

    @Override
    public EvaluatorException runtimeError(String string2, String string3, int n, String string4, int n2) {
        EvaluatorException evaluatorException = new EvaluatorException(string2, string3, n, string4, n2);
        return evaluatorException;
    }

    public void setIsReportingWarnings(boolean bl) {
        this.reportWarnings = bl;
    }

    @Override
    public void warning(String string2, String string3, int n, String string4, int n2) {
        if (!this.reportWarnings) {
            return;
        }
        this.reportErrorMessage(string2, string3, n, string4, n2, true);
    }
}

