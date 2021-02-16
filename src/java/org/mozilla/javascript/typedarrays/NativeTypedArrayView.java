/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.ArrayStoreException
 *  java.lang.Class
 *  java.lang.ClassCastException
 *  java.lang.IllegalArgumentException
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Math
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.UnsupportedOperationException
 *  java.lang.reflect.Array
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.RandomAccess
 */
package org.mozilla.javascript.typedarrays;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeArrayIterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeArrayBufferView;
import org.mozilla.javascript.typedarrays.NativeTypedArrayIterator;

public abstract class NativeTypedArrayView<T>
extends NativeArrayBufferView
implements List<T>,
RandomAccess,
ExternalArrayData {
    private static final int Id_BYTES_PER_ELEMENT = 5;
    private static final int Id_constructor = 1;
    private static final int Id_get = 3;
    private static final int Id_length = 4;
    private static final int Id_set = 4;
    private static final int Id_subarray = 5;
    private static final int Id_toString = 2;
    private static final int MAX_INSTANCE_ID = 5;
    protected static final int MAX_PROTOTYPE_ID = 6;
    private static final int SymbolId_iterator = 6;
    protected final int length;

    protected NativeTypedArrayView() {
        this.length = 0;
    }

    protected NativeTypedArrayView(NativeArrayBuffer nativeArrayBuffer, int n, int n2, int n3) {
        super(nativeArrayBuffer, n, n3);
        this.length = n2;
    }

    private NativeTypedArrayView<T> js_constructor(Context context, Scriptable scriptable, Object[] arrobject) {
        if (!NativeTypedArrayView.isArg(arrobject, 0)) {
            return this.construct(NativeArrayBuffer.EMPTY_BUFFER, 0, 0);
        }
        Object object = arrobject[0];
        if (object == null) {
            return this.construct(NativeArrayBuffer.EMPTY_BUFFER, 0, 0);
        }
        if (!(object instanceof Number) && !(object instanceof String)) {
            if (object instanceof NativeTypedArrayView) {
                NativeTypedArrayView nativeTypedArrayView = (NativeTypedArrayView)object;
                NativeTypedArrayView<T> nativeTypedArrayView2 = this.construct(this.makeArrayBuffer(context, scriptable, nativeTypedArrayView.length * this.getBytesPerElement()), 0, nativeTypedArrayView.length);
                for (int i = 0; i < nativeTypedArrayView.length; ++i) {
                    nativeTypedArrayView2.js_set(i, nativeTypedArrayView.js_get(i));
                }
                return nativeTypedArrayView2;
            }
            if (object instanceof NativeArrayBuffer) {
                NativeArrayBuffer nativeArrayBuffer = (NativeArrayBuffer)object;
                boolean bl = NativeTypedArrayView.isArg(arrobject, 1);
                int n = 0;
                if (bl) {
                    n = ScriptRuntime.toInt32(arrobject[1]);
                }
                int n2 = NativeTypedArrayView.isArg(arrobject, 2) ? ScriptRuntime.toInt32(arrobject[2]) * this.getBytesPerElement() : nativeArrayBuffer.getLength() - n;
                if (n >= 0 && n <= nativeArrayBuffer.buffer.length) {
                    if (n2 >= 0 && n + n2 <= nativeArrayBuffer.buffer.length) {
                        if (n % this.getBytesPerElement() == 0) {
                            if (n2 % this.getBytesPerElement() == 0) {
                                return this.construct(nativeArrayBuffer, n, n2 / this.getBytesPerElement());
                            }
                            throw ScriptRuntime.constructError("RangeError", "offset and buffer must be a multiple of the byte size");
                        }
                        throw ScriptRuntime.constructError("RangeError", "offset must be a multiple of the byte size");
                    }
                    throw ScriptRuntime.constructError("RangeError", "length out of range");
                }
                throw ScriptRuntime.constructError("RangeError", "offset out of range");
            }
            if (object instanceof NativeArray) {
                NativeArray nativeArray = (NativeArray)object;
                NativeTypedArrayView<T> nativeTypedArrayView = this.construct(this.makeArrayBuffer(context, scriptable, nativeArray.size() * this.getBytesPerElement()), 0, nativeArray.size());
                for (int i = 0; i < nativeArray.size(); ++i) {
                    Object object2 = nativeArray.get(i, nativeArray);
                    if (object2 != Scriptable.NOT_FOUND && object2 != Undefined.instance) {
                        if (object2 instanceof Wrapper) {
                            nativeTypedArrayView.js_set(i, ((Wrapper)object2).unwrap());
                            continue;
                        }
                        nativeTypedArrayView.js_set(i, object2);
                        continue;
                    }
                    nativeTypedArrayView.js_set(i, Double.NaN);
                }
                return nativeTypedArrayView;
            }
            if (ScriptRuntime.isArrayObject(object)) {
                Object[] arrobject2 = ScriptRuntime.getArrayElements((Scriptable)object);
                NativeTypedArrayView<T> nativeTypedArrayView = this.construct(this.makeArrayBuffer(context, scriptable, arrobject2.length * this.getBytesPerElement()), 0, arrobject2.length);
                for (int i = 0; i < arrobject2.length; ++i) {
                    nativeTypedArrayView.js_set(i, arrobject2[i]);
                }
                return nativeTypedArrayView;
            }
            throw ScriptRuntime.constructError("Error", "invalid argument");
        }
        int n = ScriptRuntime.toInt32(object);
        return this.construct(this.makeArrayBuffer(context, scriptable, n * this.getBytesPerElement()), 0, n);
    }

    private Object js_subarray(Context context, Scriptable scriptable, int n, int n2) {
        int n3 = n < 0 ? n + this.length : n;
        int n4 = n2 < 0 ? n2 + this.length : n2;
        int n5 = Math.max((int)0, (int)n3);
        int n6 = Math.max((int)0, (int)(Math.min((int)this.length, (int)n4) - n5));
        int n7 = Math.min((int)(n5 * this.getBytesPerElement()), (int)this.arrayBuffer.getLength());
        String string = this.getClassName();
        Object[] arrobject = new Object[]{this.arrayBuffer, n7, n6};
        return context.newObject(scriptable, string, arrobject);
    }

    private NativeArrayBuffer makeArrayBuffer(Context context, Scriptable scriptable, int n) {
        Object[] arrobject = new Object[]{n};
        return (NativeArrayBuffer)context.newObject(scriptable, "ArrayBuffer", arrobject);
    }

    private void setRange(NativeArray nativeArray, int n) {
        EcmaError ecmaError;
        if (n <= this.length) {
            if (n + nativeArray.size() <= this.length) {
                int n2 = n;
                Iterator iterator = nativeArray.iterator();
                while (iterator.hasNext()) {
                    this.js_set(n2, iterator.next());
                    ++n2;
                }
                return;
            }
            throw ScriptRuntime.constructError("RangeError", "offset + length out of range");
        }
        ecmaError = ScriptRuntime.constructError("RangeError", "offset out of range");
        throw ecmaError;
    }

    private void setRange(NativeTypedArrayView<T> nativeTypedArrayView, int n) {
        EcmaError ecmaError;
        int n2 = this.length;
        if (n < n2) {
            if (nativeTypedArrayView.length <= n2 - n) {
                if (nativeTypedArrayView.arrayBuffer == this.arrayBuffer) {
                    Object[] arrobject = new Object[nativeTypedArrayView.length];
                    for (int i = 0; i < nativeTypedArrayView.length; ++i) {
                        arrobject[i] = nativeTypedArrayView.js_get(i);
                    }
                    for (int i = 0; i < nativeTypedArrayView.length; ++i) {
                        this.js_set(i + n, arrobject[i]);
                    }
                    return;
                }
                for (int i = 0; i < nativeTypedArrayView.length; ++i) {
                    this.js_set(i + n, nativeTypedArrayView.js_get(i));
                }
                return;
            }
            throw ScriptRuntime.constructError("RangeError", "source array too long");
        }
        ecmaError = ScriptRuntime.constructError("RangeError", "offset out of range");
        throw ecmaError;
    }

    public void add(int n, T t) {
        throw new UnsupportedOperationException();
    }

    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int n, Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    protected boolean checkIndex(int n) {
        return n < 0 || n >= this.length;
        {
        }
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    protected abstract NativeTypedArrayView<T> construct(NativeArrayBuffer var1, int var2, int var3);

    public boolean contains(Object object) {
        return this.indexOf(object) >= 0;
    }

    public boolean containsAll(Collection<?> collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (this.contains(iterator.next())) continue;
            return false;
        }
        return true;
    }

    @Override
    public void delete(int n) {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean equals(Object object) {
        NativeTypedArrayView nativeTypedArrayView;
        try {
            nativeTypedArrayView = (NativeTypedArrayView)object;
            if (this.length != nativeTypedArrayView.length) {
                return false;
            }
        }
        catch (ClassCastException classCastException) {
            return false;
        }
        for (int i = 0; i < this.length; ++i) {
            boolean bl = this.js_get(i).equals(nativeTypedArrayView.js_get(i));
            if (bl) continue;
            return false;
        }
        return true;
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(this.getClassName())) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        switch (n) {
            default: {
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            case 6: {
                return new NativeArrayIterator(scriptable, scriptable2, NativeArrayIterator.ARRAY_ITERATOR_TYPE.VALUES);
            }
            case 5: {
                if (arrobject.length > 0) {
                    NativeTypedArrayView<T> nativeTypedArrayView = this.realThis(scriptable2, idFunctionObject);
                    int n2 = ScriptRuntime.toInt32(arrobject[0]);
                    int n3 = NativeTypedArrayView.isArg(arrobject, 1) ? ScriptRuntime.toInt32(arrobject[1]) : nativeTypedArrayView.length;
                    return NativeTypedArrayView.super.js_subarray(context, scriptable, n2, n3);
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 4: {
                if (arrobject.length > 0) {
                    NativeTypedArrayView<T> nativeTypedArrayView = this.realThis(scriptable2, idFunctionObject);
                    if (arrobject[0] instanceof NativeTypedArrayView) {
                        int n4 = NativeTypedArrayView.isArg(arrobject, 1) ? ScriptRuntime.toInt32(arrobject[1]) : 0;
                        NativeTypedArrayView.super.setRange((NativeTypedArrayView)arrobject[0], n4);
                        return Undefined.instance;
                    }
                    if (arrobject[0] instanceof NativeArray) {
                        int n5 = NativeTypedArrayView.isArg(arrobject, 1) ? ScriptRuntime.toInt32(arrobject[1]) : 0;
                        NativeTypedArrayView.super.setRange((NativeArray)arrobject[0], n5);
                        return Undefined.instance;
                    }
                    if (arrobject[0] instanceof Scriptable) {
                        return Undefined.instance;
                    }
                    if (NativeTypedArrayView.isArg(arrobject, 2)) {
                        return nativeTypedArrayView.js_set(ScriptRuntime.toInt32(arrobject[0]), arrobject[1]);
                    }
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 3: {
                if (arrobject.length > 0) {
                    return this.realThis(scriptable2, idFunctionObject).js_get(ScriptRuntime.toInt32(arrobject[0]));
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 2: {
                NativeTypedArrayView<T> nativeTypedArrayView = this.realThis(scriptable2, idFunctionObject);
                int n6 = nativeTypedArrayView.getArrayLength();
                StringBuilder stringBuilder = new StringBuilder();
                if (n6 > 0) {
                    stringBuilder.append(ScriptRuntime.toString(nativeTypedArrayView.js_get(0)));
                }
                for (int i = 1; i < n6; ++i) {
                    stringBuilder.append(',');
                    stringBuilder.append(ScriptRuntime.toString(nativeTypedArrayView.js_get(i)));
                }
                return stringBuilder.toString();
            }
            case 1: 
        }
        return this.js_constructor(context, scriptable, arrobject);
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        idFunctionObject.put("BYTES_PER_ELEMENT", (Scriptable)idFunctionObject, (Object)ScriptRuntime.wrapInt(this.getBytesPerElement()));
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 6) {
            string2 = "length";
            n = 4;
        } else {
            n = 0;
            string2 = null;
            if (n2 == 17) {
                string2 = "BYTES_PER_ELEMENT";
                n = 5;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        if (n == 0) {
            return super.findInstanceIdInfo(string);
        }
        return NativeTypedArrayView.instanceIdInfo(5, n);
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 3) {
            char c = string.charAt(0);
            if (c == 'g') {
                char c2 = string.charAt(2);
                n = 0;
                string2 = null;
                if (c2 == 't') {
                    char c3 = string.charAt(1);
                    n = 0;
                    string2 = null;
                    if (c3 == 'e') {
                        return 3;
                    }
                }
            } else {
                n = 0;
                string2 = null;
                if (c == 's') {
                    char c4 = string.charAt(2);
                    n = 0;
                    string2 = null;
                    if (c4 == 't') {
                        char c5 = string.charAt(1);
                        n = 0;
                        string2 = null;
                        if (c5 == 'e') {
                            return 4;
                        }
                    }
                }
            }
        } else if (n2 == 8) {
            char c = string.charAt(0);
            if (c == 's') {
                string2 = "subarray";
                n = 5;
            } else {
                n = 0;
                string2 = null;
                if (c == 't') {
                    string2 = "toString";
                    n = 2;
                }
            }
        } else {
            n = 0;
            string2 = null;
            if (n2 == 11) {
                string2 = "constructor";
                n = 1;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    protected int findPrototypeId(Symbol symbol) {
        if (SymbolKey.ITERATOR.equals(symbol)) {
            return 6;
        }
        return 0;
    }

    @Override
    public Object get(int n, Scriptable scriptable) {
        return this.js_get(n);
    }

    @Override
    public Object getArrayElement(int n) {
        return this.js_get(n);
    }

    @Override
    public int getArrayLength() {
        return this.length;
    }

    public abstract int getBytesPerElement();

    @Override
    public Object[] getIds() {
        Object[] arrobject = new Object[this.length];
        for (int i = 0; i < this.length; ++i) {
            arrobject[i] = i;
        }
        return arrobject;
    }

    @Override
    protected String getInstanceIdName(int n) {
        if (n != 4) {
            if (n != 5) {
                return super.getInstanceIdName(n);
            }
            return "BYTES_PER_ELEMENT";
        }
        return "length";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        if (n != 4) {
            if (n != 5) {
                return super.getInstanceIdValue(n);
            }
            return ScriptRuntime.wrapInt(this.getBytesPerElement());
        }
        return ScriptRuntime.wrapInt(this.length);
    }

    @Override
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override
    public boolean has(int n, Scriptable scriptable) {
        return true ^ this.checkIndex(n);
    }

    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.length; ++i) {
            n += this.js_get(i).hashCode();
        }
        return n;
    }

    public int indexOf(Object object) {
        for (int i = 0; i < this.length; ++i) {
            if (!object.equals(this.js_get(i))) continue;
            return i;
        }
        return -1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        if (n == 6) {
            this.initPrototypeMethod((Object)this.getClassName(), n, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n != 4) {
                        if (n != 5) throw new IllegalArgumentException(String.valueOf((int)n));
                        n2 = 2;
                        string = "subarray";
                    } else {
                        n2 = 2;
                        string = "set";
                    }
                } else {
                    n2 = 1;
                    string = "get";
                }
            } else {
                string = "toString";
                n2 = 0;
            }
        } else {
            n2 = 1;
            string = "constructor";
        }
        this.initPrototypeMethod((Object)this.getClassName(), n, string, null, n2);
    }

    @Override
    public boolean isEmpty() {
        return this.length == 0;
    }

    public Iterator<T> iterator() {
        return new NativeTypedArrayIterator<T>(this, 0);
    }

    protected abstract Object js_get(int var1);

    protected abstract Object js_set(int var1, Object var2);

    public int lastIndexOf(Object object) {
        for (int i = -1 + this.length; i >= 0; --i) {
            if (!object.equals(this.js_get(i))) continue;
            return i;
        }
        return -1;
    }

    public ListIterator<T> listIterator() {
        return new NativeTypedArrayIterator<T>(this, 0);
    }

    public ListIterator<T> listIterator(int n) {
        if (!this.checkIndex(n)) {
            return new NativeTypedArrayIterator<T>(this, n);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void put(int n, Scriptable scriptable, Object object) {
        this.js_set(n, object);
    }

    protected abstract NativeTypedArrayView<T> realThis(Scriptable var1, IdFunctionObject var2);

    public T remove(int n) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setArrayElement(int n, Object object) {
        this.js_set(n, object);
    }

    @Override
    public int size() {
        return this.length;
    }

    public List<T> subList(int n, int n2) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        Object[] arrobject = new Object[this.length];
        for (int i = 0; i < this.length; ++i) {
            arrobject[i] = this.js_get(i);
        }
        return arrobject;
    }

    public <U> U[] toArray(U[] arrU) {
        Object[] arrobject = arrU.length >= this.length ? arrU : (Object[])Array.newInstance((Class)arrU.getClass().getComponentType(), (int)this.length);
        for (int i = 0; i < this.length; ++i) {
            try {
                arrobject[i] = this.js_get(i);
            }
            catch (ClassCastException classCastException) {
                throw new ArrayStoreException();
            }
        }
        return arrobject;
    }
}

