/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
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
import org.mozilla.javascript.typedarrays.Conversions;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeArrayBufferView;

public class NativeDataView
extends NativeArrayBufferView {
    public static final String CLASS_NAME = "DataView";
    private static final int Id_constructor = 1;
    private static final int Id_getFloat32 = 8;
    private static final int Id_getFloat64 = 9;
    private static final int Id_getInt16 = 4;
    private static final int Id_getInt32 = 6;
    private static final int Id_getInt8 = 2;
    private static final int Id_getUint16 = 5;
    private static final int Id_getUint32 = 7;
    private static final int Id_getUint8 = 3;
    private static final int Id_setFloat32 = 16;
    private static final int Id_setFloat64 = 17;
    private static final int Id_setInt16 = 12;
    private static final int Id_setInt32 = 14;
    private static final int Id_setInt8 = 10;
    private static final int Id_setUint16 = 13;
    private static final int Id_setUint32 = 15;
    private static final int Id_setUint8 = 11;
    private static final int MAX_PROTOTYPE_ID = 17;
    private static final long serialVersionUID = 1427967607557438968L;

    public NativeDataView() {
    }

    public NativeDataView(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        super(nativeArrayBuffer, n, n2);
    }

    private int determinePos(Object[] arrobject) {
        if (NativeDataView.isArg(arrobject, 0)) {
            double d = ScriptRuntime.toNumber(arrobject[0]);
            if (!Double.isInfinite((double)d)) {
                return ScriptRuntime.toInt32(d);
            }
            throw ScriptRuntime.constructError("RangeError", "offset out of range");
        }
        return 0;
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeDataView().exportAsJSClass(17, scriptable, bl);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private NativeDataView js_constructor(Object[] arrobject) {
        int n;
        int n2;
        if (!NativeDataView.isArg(arrobject, 0) || !(arrobject[0] instanceof NativeArrayBuffer)) throw ScriptRuntime.constructError("TypeError", "Missing parameters");
        NativeArrayBuffer nativeArrayBuffer = (NativeArrayBuffer)arrobject[0];
        if (NativeDataView.isArg(arrobject, 1)) {
            double d = ScriptRuntime.toNumber(arrobject[1]);
            if (Double.isInfinite((double)d)) throw ScriptRuntime.constructError("RangeError", "offset out of range");
            n = ScriptRuntime.toInt32(d);
        } else {
            n = 0;
        }
        if (NativeDataView.isArg(arrobject, 2)) {
            double d = ScriptRuntime.toNumber(arrobject[2]);
            if (Double.isInfinite((double)d)) throw ScriptRuntime.constructError("RangeError", "offset out of range");
            n2 = ScriptRuntime.toInt32(d);
        } else {
            n2 = nativeArrayBuffer.getLength() - n;
        }
        if (n2 < 0) throw ScriptRuntime.constructError("RangeError", "length out of range");
        if (n < 0 || n + n2 > nativeArrayBuffer.getLength()) throw ScriptRuntime.constructError("RangeError", "offset out of range");
        return new NativeDataView(nativeArrayBuffer, n, n2);
    }

    private Object js_getFloat(int n, Object[] arrobject) {
        int n2 = this.determinePos(arrobject);
        this.rangeCheck(n2, n);
        int n3 = 1;
        if (!NativeDataView.isArg(arrobject, n3) || n <= n3 || !ScriptRuntime.toBoolean(arrobject[n3])) {
            n3 = 0;
        }
        if (n != 4) {
            if (n == 8) {
                return ByteIo.readFloat64(this.arrayBuffer.buffer, n2 + this.offset, (boolean)n3);
            }
            throw new AssertionError();
        }
        return ByteIo.readFloat32(this.arrayBuffer.buffer, n2 + this.offset, (boolean)n3);
    }

    private Object js_getInt(int n, boolean bl, Object[] arrobject) {
        int n2 = this.determinePos(arrobject);
        this.rangeCheck(n2, n);
        boolean bl2 = NativeDataView.isArg(arrobject, 1) && n > 1 && ScriptRuntime.toBoolean(arrobject[1]);
        if (n != 1) {
            if (n != 2) {
                if (n == 4) {
                    byte[] arrby = this.arrayBuffer.buffer;
                    int n3 = n2 + this.offset;
                    if (bl) {
                        return ByteIo.readInt32(arrby, n3, bl2);
                    }
                    return ByteIo.readUint32(arrby, n3, bl2);
                }
                throw new AssertionError();
            }
            byte[] arrby = this.arrayBuffer.buffer;
            int n4 = n2 + this.offset;
            if (bl) {
                return ByteIo.readInt16(arrby, n4, bl2);
            }
            return ByteIo.readUint16(arrby, n4, bl2);
        }
        byte[] arrby = this.arrayBuffer.buffer;
        int n5 = n2 + this.offset;
        if (bl) {
            return ByteIo.readInt8(arrby, n5);
        }
        return ByteIo.readUint8(arrby, n5);
    }

    private void js_setFloat(int n, Object[] arrobject) {
        int n2 = this.determinePos(arrobject);
        if (n2 >= 0) {
            boolean bl = NativeDataView.isArg(arrobject, 2) && n > 1 && ScriptRuntime.toBoolean(arrobject[2]);
            double d = Double.NaN;
            if (arrobject.length > 1) {
                d = ScriptRuntime.toNumber(arrobject[1]);
            }
            if (n2 + n <= this.byteLength) {
                if (n != 4) {
                    if (n == 8) {
                        ByteIo.writeFloat64(this.arrayBuffer.buffer, n2 + this.offset, d, bl);
                        return;
                    }
                    throw new AssertionError();
                }
                ByteIo.writeFloat32(this.arrayBuffer.buffer, n2 + this.offset, d, bl);
                return;
            }
            throw ScriptRuntime.constructError("RangeError", "offset out of range");
        }
        throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }

    private void js_setInt(int n, boolean bl, Object[] arrobject) {
        int n2 = this.determinePos(arrobject);
        if (n2 >= 0) {
            boolean bl2 = NativeDataView.isArg(arrobject, 2) && n > 1 && ScriptRuntime.toBoolean(arrobject[2]);
            Object object = 0;
            if (arrobject.length > 1) {
                object = arrobject[1];
            }
            if (n != 1) {
                if (n != 2) {
                    if (n == 4) {
                        if (bl) {
                            int n3 = Conversions.toInt32(object);
                            if (n2 + n <= this.byteLength) {
                                ByteIo.writeInt32(this.arrayBuffer.buffer, n2 + this.offset, n3, bl2);
                                return;
                            }
                            throw ScriptRuntime.constructError("RangeError", "offset out of range");
                        }
                        long l = Conversions.toUint32(object);
                        if (n2 + n <= this.byteLength) {
                            ByteIo.writeUint32(this.arrayBuffer.buffer, n2 + this.offset, l, bl2);
                            return;
                        }
                        throw ScriptRuntime.constructError("RangeError", "offset out of range");
                    }
                    throw new AssertionError();
                }
                if (bl) {
                    int n4 = Conversions.toInt16(object);
                    if (n2 + n <= this.byteLength) {
                        ByteIo.writeInt16(this.arrayBuffer.buffer, n2 + this.offset, n4, bl2);
                        return;
                    }
                    throw ScriptRuntime.constructError("RangeError", "offset out of range");
                }
                int n5 = Conversions.toUint16(object);
                if (n2 + n <= this.byteLength) {
                    ByteIo.writeUint16(this.arrayBuffer.buffer, n2 + this.offset, n5, bl2);
                    return;
                }
                throw ScriptRuntime.constructError("RangeError", "offset out of range");
            }
            if (bl) {
                int n6 = Conversions.toInt8(object);
                if (n2 + n <= this.byteLength) {
                    ByteIo.writeInt8(this.arrayBuffer.buffer, n2 + this.offset, n6);
                    return;
                }
                throw ScriptRuntime.constructError("RangeError", "offset out of range");
            }
            int n7 = Conversions.toUint8(object);
            if (n2 + n <= this.byteLength) {
                ByteIo.writeUint8(this.arrayBuffer.buffer, n2 + this.offset, n7);
                return;
            }
            throw ScriptRuntime.constructError("RangeError", "offset out of range");
        }
        throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }

    private void rangeCheck(int n, int n2) {
        if (n >= 0 && n + n2 <= this.byteLength) {
            return;
        }
        throw ScriptRuntime.constructError("RangeError", "offset out of range");
    }

    private static NativeDataView realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeDataView) {
            return (NativeDataView)scriptable;
        }
        throw NativeDataView.incompatibleCallError(idFunctionObject);
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
            case 17: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setFloat(8, arrobject);
                return Undefined.instance;
            }
            case 16: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setFloat(4, arrobject);
                return Undefined.instance;
            }
            case 15: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(4, false, arrobject);
                return Undefined.instance;
            }
            case 14: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(4, true, arrobject);
                return Undefined.instance;
            }
            case 13: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(2, false, arrobject);
                return Undefined.instance;
            }
            case 12: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(2, true, arrobject);
                return Undefined.instance;
            }
            case 11: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(1, false, arrobject);
                return Undefined.instance;
            }
            case 10: {
                NativeDataView.realThis(scriptable2, idFunctionObject).js_setInt(1, true, arrobject);
                return Undefined.instance;
            }
            case 9: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getFloat(8, arrobject);
            }
            case 8: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getFloat(4, arrobject);
            }
            case 7: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(4, false, arrobject);
            }
            case 6: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(4, true, arrobject);
            }
            case 5: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(2, false, arrobject);
            }
            case 4: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(2, true, arrobject);
            }
            case 3: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(1, false, arrobject);
            }
            case 2: {
                return NativeDataView.realThis(scriptable2, idFunctionObject).js_getInt(1, true, arrobject);
            }
            case 1: 
        }
        return this.js_constructor(arrobject);
    }

    @Override
    protected int findPrototypeId(String string) {
        String string2;
        int n;
        switch (string.length()) {
            default: {
                n = 0;
                string2 = null;
                break;
            }
            case 11: {
                string2 = "constructor";
                n = 1;
                break;
            }
            case 10: {
                char c = string.charAt(0);
                if (c == 'g') {
                    char c2 = string.charAt(9);
                    if (c2 == '2') {
                        string2 = "getFloat32";
                        n = 8;
                        break;
                    }
                    n = 0;
                    string2 = null;
                    if (c2 != '4') break;
                    string2 = "getFloat64";
                    n = 9;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 's') break;
                char c3 = string.charAt(9);
                if (c3 == '2') {
                    string2 = "setFloat32";
                    n = 16;
                    break;
                }
                n = 0;
                string2 = null;
                if (c3 != '4') break;
                string2 = "setFloat64";
                n = 17;
                break;
            }
            case 9: {
                char c = string.charAt(0);
                if (c == 'g') {
                    char c4 = string.charAt(8);
                    if (c4 == '2') {
                        string2 = "getUint32";
                        n = 7;
                        break;
                    }
                    n = 0;
                    string2 = null;
                    if (c4 != '6') break;
                    string2 = "getUint16";
                    n = 5;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 's') break;
                char c5 = string.charAt(8);
                if (c5 == '2') {
                    string2 = "setUint32";
                    n = 15;
                    break;
                }
                n = 0;
                string2 = null;
                if (c5 != '6') break;
                string2 = "setUint16";
                n = 13;
                break;
            }
            case 8: {
                char c = string.charAt(6);
                if (c == '1') {
                    char c6 = string.charAt(0);
                    if (c6 == 'g') {
                        string2 = "getInt16";
                        n = 4;
                        break;
                    }
                    n = 0;
                    string2 = null;
                    if (c6 != 's') break;
                    string2 = "setInt16";
                    n = 12;
                    break;
                }
                if (c == '3') {
                    char c7 = string.charAt(0);
                    if (c7 == 'g') {
                        string2 = "getInt32";
                        n = 6;
                        break;
                    }
                    n = 0;
                    string2 = null;
                    if (c7 != 's') break;
                    string2 = "setInt32";
                    n = 14;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 't') break;
                char c8 = string.charAt(0);
                if (c8 == 'g') {
                    string2 = "getUint8";
                    n = 3;
                    break;
                }
                n = 0;
                string2 = null;
                if (c8 != 's') break;
                string2 = "setUint8";
                n = 11;
                break;
            }
            case 7: {
                char c = string.charAt(0);
                if (c == 'g') {
                    string2 = "getInt8";
                    n = 2;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 's') break;
                string2 = "setInt8";
                n = 10;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        switch (n) {
            default: {
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            case 17: {
                n2 = 2;
                string = "setFloat64";
                break;
            }
            case 16: {
                n2 = 2;
                string = "setFloat32";
                break;
            }
            case 15: {
                n2 = 2;
                string = "setUint32";
                break;
            }
            case 14: {
                n2 = 2;
                string = "setInt32";
                break;
            }
            case 13: {
                n2 = 2;
                string = "setUint16";
                break;
            }
            case 12: {
                n2 = 2;
                string = "setInt16";
                break;
            }
            case 11: {
                n2 = 2;
                string = "setUint8";
                break;
            }
            case 10: {
                n2 = 2;
                string = "setInt8";
                break;
            }
            case 9: {
                n2 = 1;
                string = "getFloat64";
                break;
            }
            case 8: {
                n2 = 1;
                string = "getFloat32";
                break;
            }
            case 7: {
                n2 = 1;
                string = "getUint32";
                break;
            }
            case 6: {
                n2 = 1;
                string = "getInt32";
                break;
            }
            case 5: {
                n2 = 1;
                string = "getUint16";
                break;
            }
            case 4: {
                n2 = 1;
                string = "getInt16";
                break;
            }
            case 3: {
                n2 = 1;
                string = "getUint8";
                break;
            }
            case 2: {
                n2 = 1;
                string = "getInt8";
                break;
            }
            case 1: {
                n2 = 3;
                string = "constructor";
            }
        }
        this.initPrototypeMethod(this.getClassName(), n, string, n2);
    }
}

