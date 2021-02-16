/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EqualObjectGraphs;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ArrowFunction
extends BaseFunction {
    private static final long serialVersionUID = -7377989503697220633L;
    private final Scriptable boundThis;
    private final Callable targetFunction;

    public ArrowFunction(Context context, Scriptable scriptable, Callable callable, Scriptable scriptable2) {
        this.targetFunction = callable;
        this.boundThis = scriptable2;
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
        BaseFunction baseFunction = ScriptRuntime.typeErrorThrower();
        NativeObject nativeObject = new NativeObject();
        nativeObject.put("get", (Scriptable)nativeObject, (Object)baseFunction);
        nativeObject.put("set", (Scriptable)nativeObject, (Object)baseFunction);
        Boolean bl = false;
        nativeObject.put("enumerable", (Scriptable)nativeObject, (Object)bl);
        nativeObject.put("configurable", (Scriptable)nativeObject, (Object)bl);
        nativeObject.preventExtensions();
        this.defineOwnProperty(context, "caller", nativeObject, false);
        this.defineOwnProperty(context, "arguments", nativeObject, false);
    }

    static boolean equalObjectGraphs(ArrowFunction arrowFunction, ArrowFunction arrowFunction2, EqualObjectGraphs equalObjectGraphs) {
        return equalObjectGraphs.equalGraphs(arrowFunction.boundThis, arrowFunction2.boundThis) && equalObjectGraphs.equalGraphs(arrowFunction.targetFunction, arrowFunction2.targetFunction);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        Scriptable scriptable3 = this.boundThis;
        if (scriptable3 == null) {
            scriptable3 = ScriptRuntime.getTopCallScope(context);
        }
        return this.targetFunction.call(context, scriptable, scriptable3, arrobject);
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        throw ScriptRuntime.typeError1("msg.not.ctor", this.decompile(0, 0));
    }

    @Override
    String decompile(int n, int n2) {
        Callable callable = this.targetFunction;
        if (callable instanceof BaseFunction) {
            return ((BaseFunction)callable).decompile(n, n2);
        }
        return super.decompile(n, n2);
    }

    @Override
    public int getArity() {
        return this.getLength();
    }

    @Override
    public int getLength() {
        Callable callable = this.targetFunction;
        if (callable instanceof BaseFunction) {
            return ((BaseFunction)callable).getLength();
        }
        return 0;
    }

    @Override
    public boolean hasInstance(Scriptable scriptable) {
        Callable callable = this.targetFunction;
        if (callable instanceof Function) {
            return ((Function)callable).hasInstance(scriptable);
        }
        throw ScriptRuntime.typeError0("msg.not.ctor");
    }
}

