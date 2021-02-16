/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Integer
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

public class NativeUint8ClampedArray
extends NativeTypedArrayView<Integer> {
    private static final String CLASS_NAME = "Uint8ClampedArray";
    private static final long serialVersionUID = -3349419704390398895L;

    public NativeUint8ClampedArray() {
    }

    public NativeUint8ClampedArray(int n) {
        this(new NativeArrayBuffer(n), 0, n);
    }

    public NativeUint8ClampedArray(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeUint8ClampedArray().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeUint8ClampedArray construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeUint8ClampedArray(nativeArrayBuffer, n, n2);
    }

    public Integer get(int n) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_get(n);
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
        return ByteIo.readUint8(this.arrayBuffer.buffer, n + this.offset);
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        int n2 = Conversions.toUint8Clamp(object);
        ByteIo.writeUint8(this.arrayBuffer.buffer, n + this.offset, n2);
        return null;
    }

    protected NativeUint8ClampedArray realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeUint8ClampedArray) {
            return (NativeUint8ClampedArray)scriptable;
        }
        throw NativeUint8ClampedArray.incompatibleCallError(idFunctionObject);
    }

    public Integer set(int n, Integer n2) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_set(n, (Object)n2);
        }
        throw new IndexOutOfBoundsException();
    }
}

