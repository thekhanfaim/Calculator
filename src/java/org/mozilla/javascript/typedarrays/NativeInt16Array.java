/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Object
 *  java.lang.Short
 *  java.lang.String
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.ByteIo;
import org.mozilla.javascript.typedarrays.Conversions;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeTypedArrayView;

public class NativeInt16Array
extends NativeTypedArrayView<Short> {
    private static final int BYTES_PER_ELEMENT = 2;
    private static final String CLASS_NAME = "Int16Array";
    private static final long serialVersionUID = -8592870435287581398L;

    public NativeInt16Array() {
    }

    public NativeInt16Array(int n) {
        this(new NativeArrayBuffer(n * 2), 0, n);
    }

    public NativeInt16Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 2);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeInt16Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeInt16Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeInt16Array(nativeArrayBuffer, n, n2);
    }

    public Short get(int n) {
        if (!this.checkIndex(n)) {
            return (Short)this.js_get(n);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getBytesPerElement() {
        return 2;
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected Object js_get(int n) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        return ByteIo.readInt16(this.arrayBuffer.buffer, n * 2 + this.offset, NativeInt16Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        int n2 = Conversions.toInt16(object);
        ByteIo.writeInt16(this.arrayBuffer.buffer, n * 2 + this.offset, n2, NativeInt16Array.useLittleEndian());
        return null;
    }

    protected NativeInt16Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeInt16Array) {
            return (NativeInt16Array)scriptable;
        }
        throw NativeInt16Array.incompatibleCallError(idFunctionObject);
    }

    public Short set(int n, Short s) {
        if (!this.checkIndex(n)) {
            return (Short)this.js_set(n, (Object)s);
        }
        throw new IndexOutOfBoundsException();
    }
}

