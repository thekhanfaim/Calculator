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
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.ByteIo;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeTypedArrayView;

public class NativeInt32Array
extends NativeTypedArrayView<Integer> {
    private static final int BYTES_PER_ELEMENT = 4;
    private static final String CLASS_NAME = "Int32Array";
    private static final long serialVersionUID = -8963461831950499340L;

    public NativeInt32Array() {
    }

    public NativeInt32Array(int n) {
        this(new NativeArrayBuffer(n * 4), 0, n);
    }

    public NativeInt32Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 4);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeInt32Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeInt32Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeInt32Array(nativeArrayBuffer, n, n2);
    }

    public Integer get(int n) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_get(n);
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
        return ByteIo.readInt32(this.arrayBuffer.buffer, n * 4 + this.offset, NativeInt32Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        int n2 = ScriptRuntime.toInt32(object);
        ByteIo.writeInt32(this.arrayBuffer.buffer, n * 4 + this.offset, n2, NativeInt32Array.useLittleEndian());
        return null;
    }

    protected NativeInt32Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeInt32Array) {
            return (NativeInt32Array)scriptable;
        }
        throw NativeInt32Array.incompatibleCallError(idFunctionObject);
    }

    public Integer set(int n, Integer n2) {
        if (!this.checkIndex(n)) {
            return (Integer)this.js_set(n, (Object)n2);
        }
        throw new IndexOutOfBoundsException();
    }
}

