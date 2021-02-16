/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.ObjectOutputStream
 *  java.io.OutputStream
 *  java.io.Serializable
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.StringTokenizer
 */
package org.mozilla.javascript.serialize;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UniqueTag;

public class ScriptableOutputStream
extends ObjectOutputStream {
    private Scriptable scope;
    private Map<Object, String> table;

    public ScriptableOutputStream(OutputStream outputStream, Scriptable scriptable) throws IOException {
        HashMap hashMap;
        super(outputStream);
        this.scope = scriptable;
        this.table = hashMap = new HashMap();
        hashMap.put((Object)scriptable, (Object)"");
        this.enableReplaceObject(true);
        this.excludeStandardObjectNames();
    }

    static Object lookupQualifiedName(Scriptable scriptable, String string2) {
        String string3;
        StringTokenizer stringTokenizer = new StringTokenizer(string2, ".");
        Object object = scriptable;
        while (stringTokenizer.hasMoreTokens() && (object = ScriptableObject.getProperty((Scriptable)object, string3 = stringTokenizer.nextToken())) != null) {
            if (object instanceof Scriptable) continue;
            return object;
        }
        return object;
    }

    public void addExcludedName(String string2) {
        Object object = ScriptableOutputStream.lookupQualifiedName(this.scope, string2);
        if (object instanceof Scriptable) {
            this.table.put(object, (Object)string2);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Object for excluded name ");
        stringBuilder.append(string2);
        stringBuilder.append(" not found.");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void addOptionalExcludedName(String string2) {
        Object object = ScriptableOutputStream.lookupQualifiedName(this.scope, string2);
        if (object != null && object != UniqueTag.NOT_FOUND) {
            if (object instanceof Scriptable) {
                this.table.put(object, (Object)string2);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Object for excluded name ");
            stringBuilder.append(string2);
            stringBuilder.append(" is not a Scriptable, it is ");
            stringBuilder.append(object.getClass().getName());
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public void excludeAllIds(Object[] arrobject) {
        for (Object object : arrobject) {
            Scriptable scriptable;
            if (!(object instanceof String) || !((scriptable = this.scope).get((String)object, scriptable) instanceof Scriptable)) continue;
            this.addExcludedName((String)object);
        }
    }

    public void excludeStandardObjectNames() {
        String[] arrstring = new String[]{"Object", "Object.prototype", "Function", "Function.prototype", "String", "String.prototype", "Math", "Array", "Array.prototype", "Error", "Error.prototype", "Number", "Number.prototype", "Date", "Date.prototype", "RegExp", "RegExp.prototype", "Script", "Script.prototype", "Continuation", "Continuation.prototype"};
        for (int i = 0; i < arrstring.length; ++i) {
            this.addExcludedName(arrstring[i]);
        }
        String[] arrstring2 = new String[]{"XML", "XML.prototype", "XMLList", "XMLList.prototype"};
        for (int i = 0; i < arrstring2.length; ++i) {
            this.addOptionalExcludedName(arrstring2[i]);
        }
    }

    public boolean hasExcludedName(String string2) {
        return this.table.get((Object)string2) != null;
    }

    public void removeExcludedName(String string2) {
        this.table.remove((Object)string2);
    }

    protected Object replaceObject(Object object) throws IOException {
        String string2 = (String)this.table.get(object);
        if (string2 == null) {
            return object;
        }
        return new PendingLookup(string2);
    }

    static class PendingLookup
    implements Serializable {
        private static final long serialVersionUID = -2692990309789917727L;
        private String name;

        PendingLookup(String string2) {
            this.name = string2;
        }

        String getName() {
            return this.name;
        }
    }

}

