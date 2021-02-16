/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.ScriptRuntime;

public class Conversions {
    public static int toInt16(Object object) {
        return (short)ScriptRuntime.toInt32(object);
    }

    public static int toInt32(Object object) {
        return ScriptRuntime.toInt32(object);
    }

    public static int toInt8(Object object) {
        return (byte)ScriptRuntime.toInt32(object);
    }

    public static int toUint16(Object object) {
        return 65535 & ScriptRuntime.toInt32(object);
    }

    public static long toUint32(Object object) {
        return ScriptRuntime.toUint32(object);
    }

    public static int toUint8(Object object) {
        return 255 & ScriptRuntime.toInt32(object);
    }

    public static int toUint8Clamp(Object object) {
        double d = ScriptRuntime.toNumber(object);
        if (d <= 0.0) {
            return 0;
        }
        if (d >= 255.0) {
            return 255;
        }
        double d2 = Math.floor((double)d);
        if (d2 + 0.5 < d) {
            return (int)(1.0 + d2);
        }
        if (d < 0.5 + d2) {
            return (int)d2;
        }
        if ((int)d2 % 2 != 0) {
            return 1 + (int)d2;
        }
        return (int)d2;
    }
}

