/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;

public abstract class NativeArrayBufferView
extends IdScriptableObject {
    private static final int Id_buffer = 1;
    private static final int Id_byteLength = 3;
    private static final int Id_byteOffset = 2;
    protected static final int MAX_INSTANCE_ID = 3;
    private static final long serialVersionUID = 6884475582973958419L;
    private static Boolean useLittleEndian = null;
    protected final NativeArrayBuffer arrayBuffer;
    protected final int byteLength;
    protected final int offset;

    public NativeArrayBufferView() {
        this.arrayBuffer = NativeArrayBuffer.EMPTY_BUFFER;
        this.offset = 0;
        this.byteLength = 0;
    }

    protected NativeArrayBufferView(NativeArrayBuffer nativeArrayBuffer, int n, int n2) {
        this.offset = n;
        this.byteLength = n2;
        this.arrayBuffer = nativeArrayBuffer;
    }

    protected static boolean isArg(Object[] arrobject, int n) {
        return arrobject.length > n && !Undefined.instance.equals(arrobject[n]);
    }

    protected static boolean useLittleEndian() {
        if (useLittleEndian == null) {
            Context context = Context.getCurrentContext();
            if (context == null) {
                return false;
            }
            useLittleEndian = context.hasFeature(19);
        }
        return useLittleEndian;
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 6) {
            string2 = "buffer";
            n = 1;
        } else {
            n = 0;
            string2 = null;
            if (n2 == 10) {
                char c = string.charAt(4);
                if (c == 'L') {
                    string2 = "byteLength";
                    n = 3;
                } else {
                    n = 0;
                    string2 = null;
                    if (c == 'O') {
                        string2 = "byteOffset";
                        n = 2;
                    }
                }
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        if (n == 0) {
            return super.findInstanceIdInfo(string);
        }
        return NativeArrayBufferView.instanceIdInfo(5, n);
    }

    public NativeArrayBuffer getBuffer() {
        return this.arrayBuffer;
    }

    public int getByteLength() {
        return this.byteLength;
    }

    public int getByteOffset() {
        return this.offset;
    }

    @Override
    protected String getInstanceIdName(int n) {
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    return super.getInstanceIdName(n);
                }
                return "byteLength";
            }
            return "byteOffset";
        }
        return "buffer";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    return super.getInstanceIdValue(n);
                }
                return ScriptRuntime.wrapInt(this.byteLength);
            }
            return ScriptRuntime.wrapInt(this.offset);
        }
        return this.arrayBuffer;
    }

    @Override
    protected int getMaxInstanceId() {
        return 3;
    }
}

