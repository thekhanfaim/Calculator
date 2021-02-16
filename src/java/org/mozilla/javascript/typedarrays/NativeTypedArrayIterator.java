/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.UnsupportedOperationException
 *  java.util.ListIterator
 *  java.util.NoSuchElementException
 *  org.mozilla.javascript.typedarrays.NativeTypedArrayView
 */
package org.mozilla.javascript.typedarrays;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.typedarrays.NativeTypedArrayView;

public class NativeTypedArrayIterator<T>
implements ListIterator<T> {
    private int lastPosition = -1;
    private int position;
    private final NativeTypedArrayView<T> view;

    NativeTypedArrayIterator(NativeTypedArrayView<T> nativeTypedArrayView, int n) {
        this.view = nativeTypedArrayView;
        this.position = n;
    }

    public void add(T t) {
        throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
        return this.position < this.view.length;
    }

    public boolean hasPrevious() {
        return this.position > 0;
    }

    public T next() {
        if (this.hasNext()) {
            int n;
            Object object = this.view.get(this.position);
            this.lastPosition = n = this.position;
            this.position = n + 1;
            return (T)object;
        }
        throw new NoSuchElementException();
    }

    public int nextIndex() {
        return this.position;
    }

    public T previous() {
        if (this.hasPrevious()) {
            int n;
            this.position = n = -1 + this.position;
            this.lastPosition = n;
            return (T)this.view.get(n);
        }
        throw new NoSuchElementException();
    }

    public int previousIndex() {
        return -1 + this.position;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void set(T t) {
        int n = this.lastPosition;
        if (n >= 0) {
            this.view.js_set(n, t);
            return;
        }
        throw new IllegalStateException();
    }
}

