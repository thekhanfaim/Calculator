/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Error
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.util.Set
 */
package org.mozilla.javascript.tools.shell;

import java.util.Set;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Environment
extends ScriptableObject {
    static final long serialVersionUID = -430727378460177065L;
    private Environment thePrototypeInstance = null;

    public Environment() {
        if (!false) {
            this.thePrototypeInstance = this;
        }
    }

    public Environment(ScriptableObject scriptableObject) {
        this.setParentScope(scriptableObject);
        Object object = ScriptRuntime.getTopLevelProp(scriptableObject, "Environment");
        if (object != null && object instanceof Scriptable) {
            Scriptable scriptable = (Scriptable)object;
            this.setPrototype((Scriptable)scriptable.get("prototype", scriptable));
        }
    }

    private Object[] collectIds() {
        return System.getProperties().keySet().toArray();
    }

    public static void defineClass(ScriptableObject scriptableObject) {
        try {
            ScriptableObject.defineClass(scriptableObject, Environment.class);
            return;
        }
        catch (Exception exception) {
            throw new Error(exception.getMessage());
        }
    }

    @Override
    public Object get(String string2, Scriptable scriptable) {
        if (this == this.thePrototypeInstance) {
            return super.get(string2, scriptable);
        }
        String string3 = System.getProperty((String)string2);
        if (string3 != null) {
            return ScriptRuntime.toObject(this.getParentScope(), string3);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override
    public Object[] getAllIds() {
        if (this == this.thePrototypeInstance) {
            return super.getAllIds();
        }
        return this.collectIds();
    }

    @Override
    public String getClassName() {
        return "Environment";
    }

    @Override
    public Object[] getIds() {
        if (this == this.thePrototypeInstance) {
            return super.getIds();
        }
        return this.collectIds();
    }

    @Override
    public boolean has(String string2, Scriptable scriptable) {
        if (this == this.thePrototypeInstance) {
            return super.has(string2, scriptable);
        }
        return System.getProperty((String)string2) != null;
    }

    @Override
    public void put(String string2, Scriptable scriptable, Object object) {
        if (this == this.thePrototypeInstance) {
            super.put(string2, scriptable, object);
            return;
        }
        System.getProperties().put((Object)string2, (Object)ScriptRuntime.toString(object));
    }
}

