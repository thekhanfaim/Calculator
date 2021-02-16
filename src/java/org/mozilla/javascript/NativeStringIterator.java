/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public final class NativeStringIterator
extends ES6Iterator {
    private static final String ITERATOR_TAG = "StringIterator";
    private static final long serialVersionUID = 1L;
    private int index;
    private String string;

    private NativeStringIterator() {
    }

    NativeStringIterator(Scriptable scriptable, Scriptable scriptable2) {
        super(scriptable, ITERATOR_TAG);
        this.index = 0;
        this.string = ScriptRuntime.toString(scriptable2);
    }

    static void init(ScriptableObject scriptableObject, boolean bl) {
        ES6Iterator.init(scriptableObject, bl, new NativeStringIterator(), ITERATOR_TAG);
    }

    @Override
    public String getClassName() {
        return "String Iterator";
    }

    @Override
    protected String getTag() {
        return ITERATOR_TAG;
    }

    @Override
    protected boolean isDone(Context context, Scriptable scriptable) {
        return this.index >= this.string.length();
    }

    @Override
    protected Object nextValue(Context context, Scriptable scriptable) {
        int n = this.string.offsetByCodePoints(this.index, 1);
        String string = this.string.substring(this.index, n);
        this.index = n;
        return string;
    }
}

