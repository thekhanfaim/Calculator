/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Object
 */
package org.mozilla.javascript.typedarrays;

public class ByteIo {
    private static short doReadInt16(byte[] arrby, int n, boolean bl) {
        if (bl) {
            return (short)(255 & arrby[n] | (255 & arrby[n + 1]) << 8);
        }
        return (short)((255 & arrby[n]) << 8 | 255 & arrby[n + 1]);
    }

    private static void doWriteInt16(byte[] arrby, int n, int n2, boolean bl) {
        if (bl) {
            arrby[n] = (byte)(n2 & 255);
            arrby[n + 1] = (byte)(255 & n2 >>> 8);
            return;
        }
        arrby[n] = (byte)(255 & n2 >>> 8);
        arrby[n + 1] = (byte)(n2 & 255);
    }

    public static Object readFloat32(byte[] arrby, int n, boolean bl) {
        return Float.valueOf((float)Float.intBitsToFloat((int)((int)ByteIo.readUint32Primitive(arrby, n, bl))));
    }

    public static Object readFloat64(byte[] arrby, int n, boolean bl) {
        return Double.longBitsToDouble((long)ByteIo.readUint64Primitive(arrby, n, bl));
    }

    public static Object readInt16(byte[] arrby, int n, boolean bl) {
        return ByteIo.doReadInt16(arrby, n, bl);
    }

    public static Object readInt32(byte[] arrby, int n, boolean bl) {
        if (bl) {
            return 255 & arrby[n] | (255 & arrby[n + 1]) << 8 | (255 & arrby[n + 2]) << 16 | (255 & arrby[n + 3]) << 24;
        }
        return (255 & arrby[n]) << 24 | (255 & arrby[n + 1]) << 16 | (255 & arrby[n + 2]) << 8 | 255 & arrby[n + 3];
    }

    public static Object readInt8(byte[] arrby, int n) {
        return arrby[n];
    }

    public static Object readUint16(byte[] arrby, int n, boolean bl) {
        return 65535 & ByteIo.doReadInt16(arrby, n, bl);
    }

    public static Object readUint32(byte[] arrby, int n, boolean bl) {
        return ByteIo.readUint32Primitive(arrby, n, bl);
    }

    public static long readUint32Primitive(byte[] arrby, int n, boolean bl) {
        if (bl) {
            return 0xFFFFFFFFL & (255L & (long)arrby[n] | (255L & (long)arrby[n + 1]) << 8 | (255L & (long)arrby[n + 2]) << 16 | (255L & (long)arrby[n + 3]) << 24);
        }
        return 0xFFFFFFFFL & ((255L & (long)arrby[n]) << 24 | (255L & (long)arrby[n + 1]) << 16 | (255L & (long)arrby[n + 2]) << 8 | 255L & (long)arrby[n + 3]);
    }

    public static long readUint64Primitive(byte[] arrby, int n, boolean bl) {
        if (bl) {
            return 255L & (long)arrby[n] | (255L & (long)arrby[n + 1]) << 8 | (255L & (long)arrby[n + 2]) << 16 | (255L & (long)arrby[n + 3]) << 24 | (255L & (long)arrby[n + 4]) << 32 | (255L & (long)arrby[n + 5]) << 40 | (255L & (long)arrby[n + 6]) << 48 | (255L & (long)arrby[n + 7]) << 56;
        }
        return (255L & (long)arrby[n]) << 56 | (255L & (long)arrby[n + 1]) << 48 | (255L & (long)arrby[n + 2]) << 40 | (255L & (long)arrby[n + 3]) << 32 | (255L & (long)arrby[n + 4]) << 24 | (255L & (long)arrby[n + 5]) << 16 | (255L & (long)arrby[n + 6]) << 8 | (255L & (long)arrby[n + 7]) << 0;
    }

    public static Object readUint8(byte[] arrby, int n) {
        return 255 & arrby[n];
    }

    public static void writeFloat32(byte[] arrby, int n, double d, boolean bl) {
        ByteIo.writeUint32(arrby, n, Float.floatToIntBits((float)((float)d)), bl);
    }

    public static void writeFloat64(byte[] arrby, int n, double d, boolean bl) {
        ByteIo.writeUint64(arrby, n, Double.doubleToLongBits((double)d), bl);
    }

    public static void writeInt16(byte[] arrby, int n, int n2, boolean bl) {
        ByteIo.doWriteInt16(arrby, n, n2, bl);
    }

    public static void writeInt32(byte[] arrby, int n, int n2, boolean bl) {
        if (bl) {
            arrby[n] = (byte)(n2 & 255);
            arrby[n + 1] = (byte)(255 & n2 >>> 8);
            arrby[n + 2] = (byte)(255 & n2 >>> 16);
            arrby[n + 3] = (byte)(255 & n2 >>> 24);
            return;
        }
        arrby[n] = (byte)(255 & n2 >>> 24);
        arrby[n + 1] = (byte)(255 & n2 >>> 16);
        arrby[n + 2] = (byte)(255 & n2 >>> 8);
        arrby[n + 3] = (byte)(n2 & 255);
    }

    public static void writeInt8(byte[] arrby, int n, int n2) {
        arrby[n] = (byte)n2;
    }

    public static void writeUint16(byte[] arrby, int n, int n2, boolean bl) {
        ByteIo.doWriteInt16(arrby, n, 65535 & n2, bl);
    }

    public static void writeUint32(byte[] arrby, int n, long l, boolean bl) {
        if (bl) {
            arrby[n] = (byte)(l & 255L);
            arrby[n + 1] = (byte)(255L & l >>> 8);
            arrby[n + 2] = (byte)(255L & l >>> 16);
            arrby[n + 3] = (byte)(255L & l >>> 24);
            return;
        }
        arrby[n] = (byte)(255L & l >>> 24);
        arrby[n + 1] = (byte)(255L & l >>> 16);
        arrby[n + 2] = (byte)(255L & l >>> 8);
        arrby[n + 3] = (byte)(l & 255L);
    }

    public static void writeUint64(byte[] arrby, int n, long l, boolean bl) {
        if (bl) {
            arrby[n] = (byte)(l & 255L);
            arrby[n + 1] = (byte)(255L & l >>> 8);
            arrby[n + 2] = (byte)(255L & l >>> 16);
            arrby[n + 3] = (byte)(255L & l >>> 24);
            arrby[n + 4] = (byte)(255L & l >>> 32);
            arrby[n + 5] = (byte)(255L & l >>> 40);
            arrby[n + 6] = (byte)(255L & l >>> 48);
            arrby[n + 7] = (byte)(255L & l >>> 56);
            return;
        }
        arrby[n] = (byte)(255L & l >>> 56);
        arrby[n + 1] = (byte)(255L & l >>> 48);
        arrby[n + 2] = (byte)(255L & l >>> 40);
        arrby[n + 3] = (byte)(255L & l >>> 32);
        arrby[n + 4] = (byte)(255L & l >>> 24);
        arrby[n + 5] = (byte)(255L & l >>> 16);
        arrby[n + 6] = (byte)(255L & l >>> 8);
        arrby[n + 7] = (byte)(l & 255L);
    }

    public static void writeUint8(byte[] arrby, int n, int n2) {
        arrby[n] = (byte)(n2 & 255);
    }
}

