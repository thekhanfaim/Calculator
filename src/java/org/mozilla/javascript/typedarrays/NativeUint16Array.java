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

public class NativeUint16Array
extends NativeTypedArrayView<Integer> {
    private static final int BYTES_PER_ELEMENT = 2;
    private static final String CLASS_NAME = "Uint16Array";
    private static final long serialVersionUID = 7700018949434240321L;

    public NativeUint16Array() {
    }

    public NativeUint16Array(int n) {
        this(new NativeArrayBuffer(n * 2), 0, n);
    }

    public NativeUint16Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 2);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeUint16Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeUint16Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeUint16Array(nativeArrayBuffer, n, n2);
    }

    public Integer get(int n) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_get(n);
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
        return ByteIo.readUint16(this.arrayBuffer.buffer, n * 2 + this.offset, NativeUint16Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        int n2 = Conversions.toUint16(object);
        ByteIo.writeUint16(this.arrayBuffer.buffer, n * 2 + this.offset, n2, NativeUint16Array.useLittleEndian());
        return null;
    }

    protected NativeUint16Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeUint16Array) {
            return (NativeUint16Array)scriptable;
        }
        throw NativeUint16Array.incompatibleCallError(idFunctionObject);
    }

    public Integer set(int n, Integer n2) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_set(n, (Object)n2);
        }
        throw new IndexOutOfBoundsException();
    }
}

