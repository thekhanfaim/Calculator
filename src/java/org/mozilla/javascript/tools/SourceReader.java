/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Throwable
 *  java.net.MalformedURLException
 *  java.net.URL
 *  java.net.URLConnection
 */
package org.mozilla.javascript.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.commonjs.module.provider.ParsedContentType;

public class SourceReader {
    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static Object readFileOrUrl(String var0, boolean var1_1, String var2_2) throws IOException {
        block20 : {
            var3_3 = SourceReader.toUrl(var0);
            var4_4 = null;
            if (var3_3 != null) ** GOTO lbl11
            try {
                var14_5 = new File(var0);
                var11_6 = (int)var14_5.length();
                var4_4 = new FileInputStream(var14_5);
                var10_7 = null;
                var9_8 = null;
                break block20;
lbl11: // 1 sources:
                var6_9 = var3_3.openConnection();
                var4_4 = var6_9.getInputStream();
                if (var1_1) {
                    var7_10 = new ParsedContentType(var6_9.getContentType());
                    var8_11 = var7_10.getContentType();
                    var9_8 = var7_10.getEncoding();
                    var10_7 = var8_11;
                } else {
                    var10_7 = null;
                    var9_8 = null;
                }
                if ((var11_6 = var6_9.getContentLength()) > 1048576) {
                    var11_6 = -1;
                }
            }
            catch (Throwable var5_14) {
                if (var4_4 == null) throw var5_14;
                var4_4.close();
                throw var5_14;
            }
        }
        if (var11_6 <= 0) {
            var11_6 = 4096;
        }
        var12_12 = Kit.readStream(var4_4, var11_6);
        if (var4_4 != null) {
            var4_4.close();
        }
        if (!var1_1) {
            return var12_12;
        }
        if (var9_8 == null) {
            if (var12_12.length > 3 && var12_12[0] == -1 && var12_12[1] == -2 && var12_12[2] == 0 && var12_12[3] == 0) {
                var9_8 = "UTF-32LE";
            } else if (var12_12.length > 3 && var12_12[0] == 0 && var12_12[1] == 0 && var12_12[2] == -2 && var12_12[3] == -1) {
                var9_8 = "UTF-32BE";
            } else if (var12_12.length > 2 && var12_12[0] == -17 && var12_12[1] == -69 && var12_12[2] == -65) {
                var9_8 = "UTF-8";
            } else if (var12_12.length > 1 && var12_12[0] == -1 && var12_12[1] == -2) {
                var9_8 = "UTF-16LE";
            } else if (var12_12.length > 1 && var12_12[0] == -2 && var12_12[1] == -1) {
                var9_8 = "UTF-16BE";
            } else {
                var9_8 = var2_2;
                if (var9_8 == null) {
                    var9_8 = var3_3 == null ? System.getProperty((String)"file.encoding") : (var10_7 != null && var10_7.startsWith("application/") != false ? "UTF-8" : "US-ASCII");
                }
            }
        }
        if ((var13_13 = new String(var12_12, var9_8)).length() <= 0) return var13_13;
        if (var13_13.charAt(0) != '\ufeff') return var13_13;
        return var13_13.substring(1);
    }

    public static URL toUrl(String string2) {
        if (string2.indexOf(58) >= 2) {
            try {
                URL uRL = new URL(string2);
                return uRL;
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
        }
        return null;
    }
}

