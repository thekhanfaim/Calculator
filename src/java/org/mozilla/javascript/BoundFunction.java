/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
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

public class BoundFunction
extends BaseFunction {
    private static final long serialVersionUID = 2118137342826470729L;
    private final Object[] boundArgs;
    private final Scriptable boundThis;
    private final int length;
    private final Callable targetFunction;

    public BoundFunction(Context context, Scriptable scriptable, Callable callable, Scriptable scriptable2, Object[] arrobject) {
        this.targetFunction = callable;
        this.boundThis = scriptable2;
        this.boundArgs = arrobject;
        boolean bl = callable instanceof BaseFunction;
        Boolean bl2 = false;
        this.length = bl ? Math.max((int)0, (int)(((BaseFunction)callable).getLength() - arrobject.length)) : 0;
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
        BaseFunction baseFunction = ScriptRuntime.typeErrorThrower(context);
        NativeObject nativeObject = new NativeObject();
        nativeObject.put("get", (Scriptable)nativeObject, (Object)baseFunction);
        nativeObject.put("set", (Scriptable)nativeObject, (Object)baseFunction);
        nativeObject.put("enumerable", (Scriptable)nativeObject, (Object)bl2);
        nativeObject.put("configurable", (Scriptable)nativeObject, (Object)bl2);
        nativeObject.preventExtensions();
        this.defineOwnProperty(context, "caller", nativeObject, false);
        this.defineOwnProperty(context, "arguments", nativeObject, false);
    }

    private Object[] concat(Object[] arrobject, Object[] arrobject2) {
        Object[] arrobject3 = new Object[arrobject.length + arrobject2.length];
        System.arraycopy((Object)arrobject, (int)0, (Object)arrobject3, (int)0, (int)arrobject.length);
        System.arraycopy((Object)arrobject2, (int)0, (Object)arrobject3, (int)arrobject.length, (int)arrobject2.length);
        return arrobject3;
    }

    static boolean equalObjectGraphs(BoundFunction boundFunction, BoundFunction boundFunction2, EqualObjectGraphs equalObjectGraphs) {
        return equalObjectGraphs.equalGraphs(boundFunction.boundThis, boundFunction2.boundThis) && equalObjectGraphs.equalGraphs(boundFunction.targetFunction, boundFunction2.targetFunction) && equalObjectGraphs.equalGraphs(boundFunction.boundArgs, boundFunction2.boundArgs);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        Scriptable scriptable3 = this.boundThis;
        if (scriptable3 == null) {
            scriptable3 = ScriptRuntime.getTopCallScope(context);
        }
        return this.targetFunction.call(context, scriptable, scriptable3, this.concat(this.boundArgs, arrobject));
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        Callable callable = this.targetFunction;
        if (callable instanceof Function) {
            return ((Function)callable).construct(context, scriptable, this.concat(this.boundArgs, arrobject));
        }
        throw ScriptRuntime.typeError0("msg.not.ctor");
    }

    @Override
    public int getLength() {
        return this.length;
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

