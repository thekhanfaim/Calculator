/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IndexOutOfBoundsException
 *  java.lang.Long
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

public class NativeUint32Array
extends NativeTypedArrayView<Long> {
    private static final int BYTES_PER_ELEMENT = 4;
    private static final String CLASS_NAME = "Uint32Array";
    private static final long serialVersionUID = -7987831421954144244L;

    public NativeUint32Array() {
    }

    public NativeUint32Array(int n) {
        this(new NativeArrayBuffer(n * 4), 0, n);
    }

    public NativeUint32Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 4);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeUint32Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeUint32Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeUint32Array(nativeArrayBuffer, n, n2);
    }

    public Long get(int n) {
        if (!this.checkIndex(n)) {
            return (Long)this.js_get(n);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getBytesPerElement() {
        return 4;
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
        return ByteIo.readUint32(this.arrayBuffer.buffer, n * 4 + this.offset, NativeUint32Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        long l = Conversions.toUint32(object);
        ByteIo.writeUint32(this.arrayBuffer.buffer, n * 4 + this.offset, l, NativeUint32Array.useLittleEndian());
        return null;
    }

    protected NativeUint32Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeUint32Array) {
            return (NativeUint32Array)scriptable;
        }
        throw NativeUint32Array.incompatibleCallError(idFunctionObject);
    }

    public Long set(int n, Long l) {
        if (!this.checkIndex(n)) {
            return (Long)this.js_set(n, (Object)l);
        }
        throw new IndexOutOfBoundsException();
    }
}

