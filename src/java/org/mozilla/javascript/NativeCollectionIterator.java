/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.ObjectInputStream
 *  java.io.ObjectOutputStream
 *  java.lang.AssertionError
 *  java.lang.ClassNotFoundException
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Iterator
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.Hashtable;
import org.mozilla.javascript.NativeCollectionIterator;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class NativeCollectionIterator
extends ES6Iterator {
    private String className;
    private transient Iterator<Hashtable.Entry> iterator = $r8$backportedMethods$utility$Collections$0$emptyIterator.emptyIterator();
    private Type type;

    public NativeCollectionIterator(String string) {
        this.className = string;
        this.iterator = $r8$backportedMethods$utility$Collections$0$emptyIterator.emptyIterator();
        this.type = Type.BOTH;
    }

    public NativeCollectionIterator(Scriptable scriptable, String string, Type type, Iterator<Hashtable.Entry> iterator) {
        super(scriptable, string);
        this.className = string;
        this.iterator = iterator;
        this.type = type;
    }

    static void init(ScriptableObject scriptableObject, String string, boolean bl) {
        ES6Iterator.init(scriptableObject, bl, new NativeCollectionIterator(string), string);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.className = (String)objectInputStream.readObject();
        this.type = (Object)objectInputStream.readObject();
        this.iterator = $r8$backportedMethods$utility$Collections$0$emptyIterator.emptyIterator();
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject((Object)this.className);
        objectOutputStream.writeObject((Object)this.type);
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    protected boolean isDone(Context context, Scriptable scriptable) {
        return true ^ this.iterator.hasNext();
    }

    @Override
    protected Object nextValue(Context context, Scriptable scriptable) {
        Hashtable.Entry entry = (Hashtable.Entry)this.iterator.next();
        int n = 1.$SwitchMap$org$mozilla$javascript$NativeCollectionIterator$Type[this.type.ordinal()];
        if (n != 1) {
            if (n != 2) {
                if (n == 3) {
                    Object[] arrobject = new Object[]{entry.key, entry.value};
                    return context.newArray(scriptable, arrobject);
                }
                throw new AssertionError();
            }
            return entry.value;
        }
        return entry.key;
    }

}

