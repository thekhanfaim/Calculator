/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Byte
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Object
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

public class NativeInt8Array
extends NativeTypedArrayView<Byte> {
    private static final String CLASS_NAME = "Int8Array";
    private static final long serialVersionUID = -3349419704390398895L;

    public NativeInt8Array() {
    }

    public NativeInt8Array(int n) {
        this(new NativeArrayBuffer(n), 0, n);
    }

    public NativeInt8Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeInt8Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeInt8Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeInt8Array(nativeArrayBuffer, n, n2);
    }

    public Byte get(int n) {
        if (!this.checkIndex(n)) {
            return (Byte)this.js_get(n);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getBytesPerElement() {
        return 1;
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
        return ByteIo.readInt8(this.arrayBuffer.buffer, n + this.offset);
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        int n2 = Conversions.toInt8(object);
        ByteIo.writeInt8(this.arrayBuffer.buffer, n + this.offset, n2);
        return null;
    }

    protected NativeInt8Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeInt8Array) {
            return (NativeInt8Array)scriptable;
        }
        throw NativeInt8Array.incompatibleCallError(idFunctionObject);
    }

    public Byte set(int n, Byte by) {
        if (!this.checkIndex(n)) {
            return (Byte)this.js_set(n, (Object)by);
        }
        throw new IndexOutOfBoundsException();
    }
}

