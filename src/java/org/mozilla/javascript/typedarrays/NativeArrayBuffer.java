/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.NativeArrayBufferView;

public class NativeArrayBuffer
extends IdScriptableObject {
    public static final String CLASS_NAME = "ArrayBuffer";
    private static final int ConstructorId_isView = -1;
    private static final byte[] EMPTY_BUF = new byte[0];
    public static final NativeArrayBuffer EMPTY_BUFFER = new NativeArrayBuffer();
    private static final int Id_byteLength = 1;
    private static final int Id_constructor = 1;
    private static final int Id_slice = 2;
    private static final int MAX_INSTANCE_ID = 1;
    private static final int MAX_PROTOTYPE_ID = 2;
    private static final long serialVersionUID = 3110411773054879549L;
    final byte[] buffer;

    public NativeArrayBuffer() {
        this.buffer = EMPTY_BUF;
    }

    public NativeArrayBuffer(double d) {
        if (!(d >= 2.147483647E9)) {
            if (d != Double.NEGATIVE_INFINITY) {
                int n = ScriptRuntime.toInt32(d);
                if (n >= 0) {
                    if (n == 0) {
                        this.buffer = EMPTY_BUF;
                        return;
                    }
                    this.buffer = new byte[n];
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Negative array length ");
                stringBuilder.append(d);
                throw ScriptRuntime.constructError("RangeError", stringBuilder.toString());
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Negative array length ");
            stringBuilder.append(d);
            throw ScriptRuntime.constructError("RangeError", stringBuilder.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("length parameter (");
        stringBuilder.append(d);
        stringBuilder.append(") is too large ");
        throw ScriptRuntime.constructError("RangeError", stringBuilder.toString());
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        new NativeArrayBuffer().exportAsJSClass(2, scriptable, bl);
    }

    private static boolean isArg(Object[] arrobject, int n) {
        return arrobject.length > n && !Undefined.instance.equals(arrobject[n]);
    }

    private static NativeArrayBuffer realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeArrayBuffer) {
            return (NativeArrayBuffer)scriptable;
        }
        throw NativeArrayBuffer.incompatibleCallError(idFunctionObject);
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(CLASS_NAME)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        int n2 = 1;
        if (n != -1) {
            double d = 0.0;
            if (n != n2) {
                if (n == 2) {
                    NativeArrayBuffer nativeArrayBuffer = NativeArrayBuffer.realThis(scriptable2, idFunctionObject);
                    if (NativeArrayBuffer.isArg(arrobject, 0)) {
                        d = ScriptRuntime.toNumber(arrobject[0]);
                    }
                    double d2 = d;
                    double d3 = NativeArrayBuffer.isArg(arrobject, n2) ? ScriptRuntime.toNumber(arrobject[n2]) : (double)nativeArrayBuffer.buffer.length;
                    return nativeArrayBuffer.slice(d2, d3);
                }
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            if (NativeArrayBuffer.isArg(arrobject, 0)) {
                d = ScriptRuntime.toNumber(arrobject[0]);
            }
            return new NativeArrayBuffer(d);
        }
        if (!NativeArrayBuffer.isArg(arrobject, 0) || !(arrobject[0] instanceof NativeArrayBufferView)) {
            n2 = 0;
        }
        return (boolean)n2;
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        this.addIdFunctionProperty(idFunctionObject, CLASS_NAME, -1, "isView", 1);
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        if ("byteLength".equals((Object)string)) {
            return NativeArrayBuffer.instanceIdInfo(5, 1);
        }
        return super.findInstanceIdInfo(string);
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 5) {
            string2 = "slice";
            n = 2;
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

    public byte[] getBuffer() {
        return this.buffer;
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

    @Override
    protected String getInstanceIdName(int n) {
        if (n == 1) {
            return "byteLength";
        }
        return super.getInstanceIdName(n);
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        if (n == 1) {
            return ScriptRuntime.wrapInt(this.buffer.length);
        }
        return super.getInstanceIdValue(n);
    }

    public int getLength() {
        return this.buffer.length;
    }

    @Override
    protected int getMaxInstanceId() {
        return 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        if (n != 1) {
            if (n != 2) throw new IllegalArgumentException(String.valueOf((int)n));
            n2 = 1;
            string = "slice";
        } else {
            n2 = 1;
            string = "constructor";
        }
        this.initPrototypeMethod(CLASS_NAME, n, string, n2);
    }

    public NativeArrayBuffer slice(double d, double d2) {
        double d3;
        double d4;
        byte[] arrby = this.buffer;
        double d5 = arrby.length;
        if (d2 < 0.0) {
            double d6 = arrby.length;
            Double.isNaN((double)d6);
            d3 = d6 + d2;
        } else {
            d3 = d2;
        }
        int n = ScriptRuntime.toInt32(Math.max((double)0.0, (double)Math.min((double)d5, (double)d3)));
        double d7 = n;
        if (d < 0.0) {
            double d8 = this.buffer.length;
            Double.isNaN((double)d8);
            d4 = d8 + d;
        } else {
            d4 = d;
        }
        int n2 = ScriptRuntime.toInt32(Math.min((double)d7, (double)Math.max((double)0.0, (double)d4)));
        int n3 = n - n2;
        NativeArrayBuffer nativeArrayBuffer = new NativeArrayBuffer(n3);
        System.arraycopy((Object)this.buffer, (int)n2, (Object)nativeArrayBuffer.buffer, (int)0, (int)n3);
        return nativeArrayBuffer;
    }
}

