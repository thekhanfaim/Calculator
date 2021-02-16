/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public final class NativeArrayIterator
extends ES6Iterator {
    private static final String ITERATOR_TAG = "ArrayIterator";
    private static final long serialVersionUID = 1L;
    private Scriptable arrayLike;
    private int index;
    private ARRAY_ITERATOR_TYPE type;

    private NativeArrayIterator() {
    }

    public NativeArrayIterator(Scriptable scriptable, Scriptable scriptable2, ARRAY_ITERATOR_TYPE aRRAY_ITERATOR_TYPE) {
        super(scriptable, ITERATOR_TAG);
        this.index = 0;
        this.arrayLike = scriptable2;
        this.type = aRRAY_ITERATOR_TYPE;
    }

    static void init(ScriptableObject scriptableObject, boolean bl) {
        ES6Iterator.init(scriptableObject, bl, new NativeArrayIterator(), ITERATOR_TAG);
    }

    @Override
    public String getClassName() {
        return "Array Iterator";
    }

    @Override
    protected String getTag() {
        return ITERATOR_TAG;
    }

    @Override
    protected boolean isDone(Context context, Scriptable scriptable) {
        long l = (long)this.index LCMP NativeArray.getLengthProperty(context, this.arrayLike, false);
        boolean bl = false;
        if (l >= 0) {
            bl = true;
        }
        return bl;
    }

    @Override
    protected Object nextValue(Context context, Scriptable scriptable) {
        if (this.type == ARRAY_ITERATOR_TYPE.KEYS) {
            int n = this.index;
            this.index = n + 1;
            return n;
        }
        Scriptable scriptable2 = this.arrayLike;
        Object object = scriptable2.get(this.index, scriptable2);
        if (object == ScriptableObject.NOT_FOUND) {
            object = Undefined.instance;
        }
        if (this.type == ARRAY_ITERATOR_TYPE.ENTRIES) {
            Object[] arrobject = new Object[]{this.index, object};
            object = context.newArray(scriptable, arrobject);
        }
        this.index = 1 + this.index;
        return object;
    }

}

