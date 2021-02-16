/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.String
 */
package org.mozilla.javascript.tools.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;

public class ShellContextFactory
extends ContextFactory {
    private boolean allowReservedKeywords = true;
    private String characterEncoding;
    private ErrorReporter errorReporter;
    private boolean generatingDebug;
    private int languageVersion = 180;
    private int optimizationLevel;
    private boolean strictMode;
    private boolean warningAsError;

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    protected boolean hasFeature(Context context, int n) {
        if (n != 3) {
            switch (n) {
                default: {
                    return super.hasFeature(context, n);
                }
                case 12: {
                    return this.warningAsError;
                }
                case 10: {
                    return this.generatingDebug;
                }
                case 8: 
                case 9: 
                case 11: 
            }
            return this.strictMode;
        }
        return this.allowReservedKeywords;
    }

    @Override
    protected void onContextCreated(Context context) {
        context.setLanguageVersion(this.languageVersion);
        context.setOptimizationLevel(this.optimizationLevel);
        ErrorReporter errorReporter = this.errorReporter;
        if (errorReporter != null) {
            context.setErrorReporter(errorReporter);
        }
        context.setGeneratingDebug(this.generatingDebug);
        super.onContextCreated(context);
    }

    public void setAllowReservedKeywords(boolean bl) {
        this.allowReservedKeywords = bl;
    }

    public void setCharacterEncoding(String string2) {
        this.characterEncoding = string2;
    }

    public void setErrorReporter(ErrorReporter errorReporter) {
        if (errorReporter != null) {
            this.errorReporter = errorReporter;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setGeneratingDebug(boolean bl) {
        this.generatingDebug = bl;
    }

    public void setLanguageVersion(int n) {
        Context.checkLanguageVersion(n);
        this.checkNotSealed();
        this.languageVersion = n;
    }

    public void setOptimizationLevel(int n) {
        Context.checkOptimizationLevel(n);
        this.checkNotSealed();
        this.optimizationLevel = n;
    }

    public void setStrictMode(boolean bl) {
        this.checkNotSealed();
        this.strictMode = bl;
    }

    public void setWarningAsError(boolean bl) {
        this.checkNotSealed();
        this.warningAsError = bl;
    }
}

