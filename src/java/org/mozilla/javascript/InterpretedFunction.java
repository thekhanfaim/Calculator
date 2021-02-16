/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Interpreter;
import org.mozilla.javascript.InterpreterData;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.debug.DebuggableScript;

final class InterpretedFunction
extends NativeFunction
implements Script {
    private static final long serialVersionUID = 541475680333911468L;
    InterpreterData idata;
    SecurityController securityController;
    Object securityDomain;

    private InterpretedFunction(InterpretedFunction interpretedFunction, int n) {
        this.idata = interpretedFunction.idata.itsNestedFunctions[n];
        this.securityController = interpretedFunction.securityController;
        this.securityDomain = interpretedFunction.securityDomain;
    }

    private InterpretedFunction(InterpreterData interpreterData, Object object) {
        block4 : {
            Object object2;
            SecurityController securityController;
            block3 : {
                block2 : {
                    this.idata = interpreterData;
                    securityController = Context.getContext().getSecurityController();
                    if (securityController == null) break block2;
                    object2 = securityController.getDynamicSecurityDomain(object);
                    break block3;
                }
                if (object != null) break block4;
                object2 = null;
            }
            this.securityController = securityController;
            this.securityDomain = object2;
            return;
        }
        throw new IllegalArgumentException();
    }

    static InterpretedFunction createFunction(Context context, Scriptable scriptable, InterpretedFunction interpretedFunction, int n) {
        InterpretedFunction interpretedFunction2 = new InterpretedFunction(interpretedFunction, n);
        interpretedFunction2.initScriptFunction(context, scriptable);
        return interpretedFunction2;
    }

    static InterpretedFunction createFunction(Context context, Scriptable scriptable, InterpreterData interpreterData, Object object) {
        InterpretedFunction interpretedFunction = new InterpretedFunction(interpreterData, object);
        interpretedFunction.initScriptFunction(context, scriptable);
        return interpretedFunction;
    }

    static InterpretedFunction createScript(InterpreterData interpreterData, Object object) {
        return new InterpretedFunction(interpreterData, object);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!ScriptRuntime.hasTopCall(context)) {
            return ScriptRuntime.doTopCall(this, context, scriptable, scriptable2, arrobject, this.idata.isStrict);
        }
        return Interpreter.interpret(this, context, scriptable, scriptable2, arrobject);
    }

    @Override
    public Object exec(Context context, Scriptable scriptable) {
        if (this.isScript()) {
            if (!ScriptRuntime.hasTopCall(context)) {
                return ScriptRuntime.doTopCall(this, context, scriptable, scriptable, ScriptRuntime.emptyArgs, this.idata.isStrict);
            }
            return Interpreter.interpret(this, context, scriptable, scriptable, ScriptRuntime.emptyArgs);
        }
        throw new IllegalStateException();
    }

    @Override
    public DebuggableScript getDebuggableView() {
        return this.idata;
    }

    @Override
    public String getEncodedSource() {
        return Interpreter.getEncodedSource(this.idata);
    }

    @Override
    public String getFunctionName() {
        if (this.idata.itsName == null) {
            return "";
        }
        return this.idata.itsName;
    }

    @Override
    protected int getLanguageVersion() {
        return this.idata.languageVersion;
    }

    @Override
    protected int getParamAndVarCount() {
        return this.idata.argNames.length;
    }

    @Override
    protected int getParamCount() {
        return this.idata.argCount;
    }

    @Override
    protected boolean getParamOrVarConst(int n) {
        return this.idata.argIsConst[n];
    }

    @Override
    protected String getParamOrVarName(int n) {
        return this.idata.argNames[n];
    }

    public boolean isScript() {
        return this.idata.itsFunctionType == 0;
    }

    @Override
    public Object resumeGenerator(Context context, Scriptable scriptable, int n, Object object, Object object2) {
        return Interpreter.resumeGenerator(context, scriptable, n, object, object2);
    }
}

