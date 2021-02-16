/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.ObjectInputStream
 *  java.io.ObjectStreamClass
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.ClassNotFoundException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.serialize.ScriptableOutputStream;

public class ScriptableInputStream
extends ObjectInputStream {
    private ClassLoader classLoader;
    private Scriptable scope;

    public ScriptableInputStream(InputStream inputStream, Scriptable scriptable) throws IOException {
        super(inputStream);
        this.scope = scriptable;
        this.enableResolveObject(true);
        Context context = Context.getCurrentContext();
        if (context != null) {
            this.classLoader = context.getApplicationClassLoader();
        }
    }

    protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        String string2 = objectStreamClass.getName();
        ClassLoader classLoader = this.classLoader;
        if (classLoader != null) {
            try {
                Class class_ = classLoader.loadClass(string2);
                return class_;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        return super.resolveClass(objectStreamClass);
    }

    protected Object resolveObject(Object object) throws IOException {
        if (object instanceof ScriptableOutputStream.PendingLookup) {
            String string2 = ((ScriptableOutputStream.PendingLookup)object).getName();
            Object object2 = ScriptableOutputStream.lookupQualifiedName(this.scope, string2);
            if (object2 != Scriptable.NOT_FOUND) {
                return object2;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Object ");
            stringBuilder.append(string2);
            stringBuilder.append(" not found upon deserialization.");
            throw new IOException(stringBuilder.toString());
        }
        if (object instanceof UniqueTag) {
            return ((UniqueTag)object).readResolve();
        }
        if (object instanceof Undefined) {
            object = ((Undefined)object).readResolve();
        }
        return object;
    }
}

