/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Decompiler;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeCall;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UintMap;
import org.mozilla.javascript.debug.DebuggableScript;

public abstract class NativeFunction
extends BaseFunction {
    private static final long serialVersionUID = 8713897114082216401L;

    @Override
    final String decompile(int n, int n2) {
        String string = this.getEncodedSource();
        if (string == null) {
            return super.decompile(n, n2);
        }
        UintMap uintMap = new UintMap(1);
        uintMap.put(1, n);
        return Decompiler.decompile(string, n2, uintMap);
    }

    @Override
    public int getArity() {
        return this.getParamCount();
    }

    public DebuggableScript getDebuggableView() {
        return null;
    }

    public String getEncodedSource() {
        return null;
    }

    protected abstract int getLanguageVersion();

    @Override
    public int getLength() {
        int n = this.getParamCount();
        if (this.getLanguageVersion() != 120) {
            return n;
        }
        NativeCall nativeCall = ScriptRuntime.findFunctionActivation(Context.getContext(), this);
        if (nativeCall == null) {
            return n;
        }
        return nativeCall.originalArgs.length;
    }

    protected abstract int getParamAndVarCount();

    protected abstract int getParamCount();

    protected boolean getParamOrVarConst(int n) {
        return false;
    }

    protected abstract String getParamOrVarName(int var1);

    public final void initScriptFunction(Context context, Scriptable scriptable) {
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
    }

    @Deprecated
    public String jsGet_name() {
        return this.getFunctionName();
    }

    public Object resumeGenerator(Context context, Scriptable scriptable, int n, Object object, Object object2) {
        throw new EvaluatorException("resumeGenerator() not implemented");
    }
}

