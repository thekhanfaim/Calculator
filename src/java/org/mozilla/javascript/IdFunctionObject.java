/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EqualObjectGraphs;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class IdFunctionObject
extends BaseFunction {
    private static final long serialVersionUID = -5332312783643935019L;
    private int arity;
    private String functionName;
    private final IdFunctionCall idcall;
    private final int methodId;
    private final Object tag;
    private boolean useCallAsConstructor;

    public IdFunctionObject(IdFunctionCall idFunctionCall, Object object, int n, int n2) {
        if (n2 >= 0) {
            this.idcall = idFunctionCall;
            this.tag = object;
            this.methodId = n;
            this.arity = n2;
            return;
        }
        throw new IllegalArgumentException();
    }

    public IdFunctionObject(IdFunctionCall idFunctionCall, Object object, int n, String string, int n2, Scriptable scriptable) {
        super(scriptable, null);
        if (n2 >= 0) {
            if (string != null) {
                this.idcall = idFunctionCall;
                this.tag = object;
                this.methodId = n;
                this.arity = n2;
                this.functionName = string;
                return;
            }
            throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException();
    }

    static boolean equalObjectGraphs(IdFunctionObject idFunctionObject, IdFunctionObject idFunctionObject2, EqualObjectGraphs equalObjectGraphs) {
        return idFunctionObject.methodId == idFunctionObject2.methodId && idFunctionObject.hasTag(idFunctionObject2.tag) && equalObjectGraphs.equalGraphs(idFunctionObject.idcall, idFunctionObject2.idcall);
    }

    public final void addAsProperty(Scriptable scriptable) {
        ScriptableObject.defineProperty(scriptable, this.functionName, this, 2);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        return this.idcall.execIdCall(this, context, scriptable, scriptable2, arrobject);
    }

    @Override
    public Scriptable createObject(Context context, Scriptable scriptable) {
        if (this.useCallAsConstructor) {
            return null;
        }
        throw ScriptRuntime.typeError1("msg.not.ctor", this.functionName);
    }

    @Override
    String decompile(int n, int n2) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = (n2 & 1) != 0;
        if (!bl) {
            stringBuilder.append("function ");
            stringBuilder.append(this.getFunctionName());
            stringBuilder.append("() { ");
        }
        stringBuilder.append("[native code for ");
        IdFunctionCall idFunctionCall = this.idcall;
        if (idFunctionCall instanceof Scriptable) {
            stringBuilder.append(((Scriptable)((Object)idFunctionCall)).getClassName());
            stringBuilder.append('.');
        }
        stringBuilder.append(this.getFunctionName());
        stringBuilder.append(", arity=");
        stringBuilder.append(this.getArity());
        String string = bl ? "]\n" : "] }\n";
        stringBuilder.append(string);
        return stringBuilder.toString();
    }

    public void exportAsScopeProperty() {
        this.addAsProperty(this.getParentScope());
    }

    @Override
    public int getArity() {
        return this.arity;
    }

    @Override
    public String getFunctionName() {
        String string = this.functionName;
        if (string == null) {
            string = "";
        }
        return string;
    }

    @Override
    public int getLength() {
        return this.getArity();
    }

    @Override
    public Scriptable getPrototype() {
        Scriptable scriptable = super.getPrototype();
        if (scriptable == null) {
            scriptable = IdFunctionObject.getFunctionPrototype(this.getParentScope());
            this.setPrototype(scriptable);
        }
        return scriptable;
    }

    public Object getTag() {
        return this.tag;
    }

    public final boolean hasTag(Object object) {
        Object object2 = this.tag;
        if (object == null) {
            return object2 == null;
        }
        return object.equals(object2);
    }

    public void initFunction(String string, Scriptable scriptable) {
        if (string != null) {
            if (scriptable != null) {
                this.functionName = string;
                this.setParentScope(scriptable);
                return;
            }
            throw new IllegalArgumentException();
        }
        throw new IllegalArgumentException();
    }

    public final void markAsConstructor(Scriptable scriptable) {
        this.useCallAsConstructor = true;
        this.setImmunePrototypeProperty(scriptable);
    }

    public final int methodId() {
        return this.methodId;
    }

    public final RuntimeException unknown() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BAD FUNCTION ID=");
        stringBuilder.append(this.methodId);
        stringBuilder.append(" MASTER=");
        stringBuilder.append((Object)this.idcall);
        return new IllegalArgumentException(stringBuilder.toString());
    }
}

