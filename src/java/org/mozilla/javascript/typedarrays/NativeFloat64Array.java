/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Double
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

public class NativeFloat64Array
extends NativeTypedArrayView<Double> {
    private static final int BYTES_PER_ELEMENT = 8;
    private static final String CLASS_NAME = "Float64Array";
    private static final long serialVersionUID = -1255405650050639335L;

    public NativeFloat64Array() {
    }

    public NativeFloat64Array(int n) {
        this(new NativeArrayBuffer(n * 8), 0, n);
    }

    public NativeFloat64Array(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2, n2 * 8);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeFloat64Array().exportAsJSClass(6, scriptable, bl);
    }

    protected NativeFloat64Array construct(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        return new NativeFloat64Array(nativeArrayBuffer, n, n2);
    }

    public Double get(int n) {
        if (!this.checkIndex(n)) {
            return (Double)this.js_get(n);
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getBytesPerElement() {
        return 8;
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
        return Double.longBitsToDouble((long)ByteIo.readUint64Primitive(this.arrayBuffer.buffer, n * 8 + this.offset, NativeFloat64Array.useLittleEndian()));
    }

    @Override
    protected Object js_set(int n, Object object) {
        if (this.checkIndex(n)) {
            return Undefined.instance;
        }
        long l = Double.doubleToLongBits((double)ScriptRuntime.toNumber(object));
        ByteIo.writeUint64(this.arrayBuffer.buffer, n * 8 + this.offset, l, NativeFloat64Array.useLittleEndian());
        return null;
    }

    protected NativeFloat64Array realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeFloat64Array) {
            return (NativeFloat64Array)scriptable;
        }
        throw NativeFloat64Array.incompatibleCallError(idFunctionObject);
    }

    public Double set(int n, Double d) {
        if (!this.checkIndex(n)) {
            return (Double)this.js_set(n, (Object)d);
        }
        throw new IndexOutOfBoundsException();
    }
}

