/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Float
 *  java.lang.IndexOutOfBoundsException
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

public class NativeFloat32Array
extends NativeTypedArrayView<Float> {
    private static final int BYTES_PER_ELEMENT = 4;
    private static final String CLASS_NAME = "Float32Array";
    private static final long serialVersionUID = -8963461831950499340L;

    public NativeFloat32Array() {
    }

    public NativeFloat32Array(int n) {
        this(new NativeArrayBuffer(n * 4), 0, n);
    }

    public NativeFloat32Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 4);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeFloat32Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeFloat32Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeFloat32Array(nativeArrayBuffer, n, n2);
    }

    public Float get(int n) {
        if (!this.checkIndex(n)) {
            return (Float)this.js_get(n);
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
        return ByteIo.readFloat32(this.arrayBuffer.buffer, n * 4 + this.offset, NativeFloat32Array.useLittleEndian());
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        double d = ScriptRuntime.toNumber(object);
        ByteIo.writeFloat32(this.arrayBuffer.buffer, n * 4 + this.offset, d, NativeFloat32Array.useLittleEndian());
        return null;
    }

    protected NativeFloat32Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeFloat32Array) {
            return (NativeFloat32Array)scriptable;
        }
        throw NativeFloat32Array.incompatibleCallError(idFunctionObject);
    }

    public Float set(int n, Float f) {
        if (!this.checkIndex(n)) {
            return (Float)this.js_set(n, (Object)f);
        }
        throw new IndexOutOfBoundsException();
    }
}

