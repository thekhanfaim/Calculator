/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.util.ArrayList
 */
package org.mozilla.javascript.json;

import java.util.ArrayList;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;

public class JsonParser {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Context cx;
    private int length;
    private int pos;
    private Scriptable scope;
    private String src;

    public JsonParser(Context context, Scriptable scriptable) {
        this.cx = context;
        this.scope = scriptable;
    }

    private void consume(char c) throws ParseException {
        this.consumeWhitespace();
        int n = this.pos;
        if (n < this.length) {
            String string2 = this.src;
            this.pos = n + 1;
            char c2 = string2.charAt(n);
            if (c2 == c) {
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected ");
            stringBuilder.append(c);
            stringBuilder.append(" found ");
            stringBuilder.append(c2);
            throw new ParseException(stringBuilder.toString());
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected ");
        stringBuilder.append(c);
        stringBuilder.append(" but reached end of stream");
        throw new ParseException(stringBuilder.toString());
    }

    private void consumeWhitespace() {
        int n;
        while ((n = this.pos) < this.length) {
            char c = this.src.charAt(n);
            if (c != '\t' && c != '\n' && c != '\r' && c != ' ') {
                return;
            }
            this.pos = 1 + this.pos;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private int fromHex(char c) {
        int n;
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            n = c - 65;
            do {
                return n + 10;
                break;
            } while (true);
        }
        if (c < 'a' || c > 'f') return -1;
        n = c - 97;
        return n + 10;
    }

    private char nextOrNumberError(int n) throws ParseException {
        int n2 = this.pos;
        int n3 = this.length;
        if (n2 < n3) {
            String string2 = this.src;
            this.pos = n2 + 1;
            return string2.charAt(n2);
        }
        throw this.numberError(n, n3);
    }

    private ParseException numberError(int n, int n2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported number format: ");
        stringBuilder.append(this.src.substring(n, n2));
        return new ParseException(stringBuilder.toString());
    }

    private Object readArray() throws ParseException {
        ParseException parseException;
        int n;
        this.consumeWhitespace();
        int n2 = this.pos;
        if (n2 < this.length && this.src.charAt(n2) == ']') {
            this.pos = 1 + this.pos;
            return this.cx.newArray(this.scope, 0);
        }
        ArrayList arrayList = new ArrayList();
        boolean bl = false;
        while ((n = this.pos) < this.length) {
            block10 : {
                block9 : {
                    block6 : {
                        block7 : {
                            block8 : {
                                char c = this.src.charAt(n);
                                if (c == ',') break block6;
                                if (c == ']') break block7;
                                if (bl) break block8;
                                arrayList.add(this.readValue());
                                bl = true;
                                break block9;
                            }
                            throw new ParseException("Missing comma in array literal");
                        }
                        if (bl) {
                            this.pos = 1 + this.pos;
                            return this.cx.newArray(this.scope, arrayList.toArray());
                        }
                        throw new ParseException("Unexpected comma in array literal");
                    }
                    if (!bl) break block10;
                    bl = false;
                    this.pos = 1 + this.pos;
                }
                this.consumeWhitespace();
                continue;
            }
            throw new ParseException("Unexpected comma in array literal");
        }
        parseException = new ParseException("Unterminated array literal");
        throw parseException;
    }

    private void readDigits() {
        char c;
        int n;
        while ((n = this.pos) < this.length && (c = this.src.charAt(n)) >= '0') {
            if (c > '9') {
                return;
            }
            this.pos = 1 + this.pos;
        }
    }

    private Boolean readFalse() throws ParseException {
        int n = this.length;
        int n2 = this.pos;
        if (n - n2 >= 4 && this.src.charAt(n2) == 'a' && this.src.charAt(1 + this.pos) == 'l' && this.src.charAt(2 + this.pos) == 's' && this.src.charAt(3 + this.pos) == 'e') {
            this.pos = 4 + this.pos;
            return Boolean.FALSE;
        }
        throw new ParseException("Unexpected token: f");
    }

    private Object readNull() throws ParseException {
        int n = this.length;
        int n2 = this.pos;
        if (n - n2 >= 3 && this.src.charAt(n2) == 'u' && this.src.charAt(1 + this.pos) == 'l' && this.src.charAt(2 + this.pos) == 'l') {
            this.pos = 3 + this.pos;
            return null;
        }
        throw new ParseException("Unexpected token: n");
    }

    private Number readNumber(char c) throws ParseException {
        int n;
        char c2;
        int n2;
        int n3;
        double d;
        if (c != '-' && (c < '0' || c > '9')) {
            throw new AssertionError();
        }
        int n4 = -1 + this.pos;
        if (c == '-' && ((c = this.nextOrNumberError(n4)) < '0' || c > '9')) {
            throw this.numberError(n4, this.pos);
        }
        if (c != '0') {
            this.readDigits();
        }
        if ((n2 = this.pos) < this.length && this.src.charAt(n2) == '.') {
            this.pos = 1 + this.pos;
            char c3 = this.nextOrNumberError(n4);
            if (c3 >= '0' && c3 <= '9') {
                this.readDigits();
            } else {
                throw this.numberError(n4, this.pos);
            }
        }
        if ((n3 = this.pos) < this.length && ((c2 = this.src.charAt(n3)) == 'e' || c2 == 'E')) {
            this.pos = 1 + this.pos;
            char c4 = this.nextOrNumberError(n4);
            if (c4 == '-' || c4 == '+') {
                c4 = this.nextOrNumberError(n4);
            }
            if (c4 >= '0' && c4 <= '9') {
                this.readDigits();
            } else {
                throw this.numberError(n4, this.pos);
            }
        }
        if ((double)(n = (int)(d = Double.parseDouble((String)this.src.substring(n4, this.pos)))) == d) {
            return n;
        }
        return d;
    }

    private Object readObject() throws ParseException {
        ParseException parseException;
        int n;
        this.consumeWhitespace();
        Scriptable scriptable = this.cx.newObject(this.scope);
        int n2 = this.pos;
        if (n2 < this.length && this.src.charAt(n2) == '}') {
            this.pos = 1 + this.pos;
            return scriptable;
        }
        boolean bl = false;
        while ((n = this.pos) < this.length) {
            block13 : {
                block12 : {
                    block10 : {
                        block11 : {
                            String string2 = this.src;
                            this.pos = n + 1;
                            char c = string2.charAt(n);
                            if (c == '\"') break block10;
                            if (c != ',') {
                                if (c == '}') {
                                    if (bl) {
                                        return scriptable;
                                    }
                                    throw new ParseException("Unexpected comma in object literal");
                                }
                                throw new ParseException("Unexpected token in object literal");
                            }
                            if (!bl) break block11;
                            bl = false;
                            break block12;
                        }
                        throw new ParseException("Unexpected comma in object literal");
                    }
                    if (bl) break block13;
                    String string3 = this.readString();
                    this.consume(':');
                    Object object = this.readValue();
                    long l = ScriptRuntime.indexFromString(string3);
                    if (l < 0L) {
                        scriptable.put(string3, scriptable, object);
                    } else {
                        scriptable.put((int)l, scriptable, object);
                    }
                    bl = true;
                }
                this.consumeWhitespace();
                continue;
            }
            throw new ParseException("Missing comma in object literal");
        }
        parseException = new ParseException("Unterminated object literal");
        throw parseException;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private String readString() throws ParseException {
        var1_1 = this.pos;
        while ((var2_2 = this.pos) < this.length) {
            var38_3 = this.src;
            this.pos = var2_2 + 1;
            var39_4 = var38_3.charAt(var2_2);
            if (var39_4 <= '\u001f') throw new ParseException("String contains control character");
            if (var39_4 == '\\') break;
            if (var39_4 != '\"') continue;
            return this.src.substring(var1_1, -1 + this.pos);
        }
        var3_5 = new StringBuilder();
        block1 : do lbl-1000: // 3 sources:
        {
            if ((var4_6 = this.pos) >= this.length) {
                var5_21 = new ParseException("Unterminated string literal");
                throw var5_21;
            }
            if (this.src.charAt(var4_6 - 1) != '\\') throw new AssertionError();
            var3_5.append((CharSequence)this.src, var1_1, -1 + this.pos);
            var7_7 = this.pos;
            if (var7_7 >= this.length) throw new ParseException("Unterminated string");
            var8_8 = this.src;
            this.pos = var7_7 + 1;
            var9_9 = var8_8.charAt(var7_7);
            if (var9_9 != '\"') {
                if (var9_9 != '/') {
                    if (var9_9 != '\\') {
                        if (var9_9 != 'b') {
                            if (var9_9 != 'f') {
                                if (var9_9 != 'n') {
                                    if (var9_9 != 'r') {
                                        if (var9_9 != 't') {
                                            if (var9_9 != 'u') {
                                                var22_20 = new StringBuilder();
                                                var22_20.append("Unexpected character in string: '\\");
                                                var22_20.append(var9_9);
                                                var22_20.append("'");
                                                throw new ParseException(var22_20.toString());
                                            }
                                            var26_13 = this.length;
                                            var27_14 = this.pos;
                                            if (var26_13 - var27_14 < 5) {
                                                var28_19 = new StringBuilder();
                                                var28_19.append("Invalid character code: \\u");
                                                var28_19.append(this.src.substring(this.pos));
                                                throw new ParseException(var28_19.toString());
                                            }
                                            var31_15 = this.fromHex(this.src.charAt(var27_14 + 0)) << 12 | this.fromHex(this.src.charAt(1 + this.pos)) << 8 | this.fromHex(this.src.charAt(2 + this.pos)) << 4 | this.fromHex(this.src.charAt(3 + this.pos));
                                            if (var31_15 < 0) {
                                                var32_16 = new StringBuilder();
                                                var32_16.append("Invalid character code: ");
                                                var34_17 = this.src;
                                                var35_18 = this.pos;
                                                var32_16.append(var34_17.substring(var35_18, var35_18 + 4));
                                                throw new ParseException(var32_16.toString());
                                            }
                                            this.pos = 4 + this.pos;
                                            var3_5.append((char)var31_15);
                                        } else {
                                            var3_5.append('\t');
                                        }
                                    } else {
                                        var3_5.append('\r');
                                    }
                                } else {
                                    var3_5.append('\n');
                                }
                            } else {
                                var3_5.append('\f');
                            }
                        } else {
                            var3_5.append('\b');
                        }
                    } else {
                        var3_5.append('\\');
                    }
                } else {
                    var3_5.append('/');
                }
            } else {
                var3_5.append('\"');
            }
            var1_1 = this.pos;
            do {
                if ((var11_10 = this.pos) >= this.length) ** GOTO lbl-1000
                var12_11 = this.src;
                this.pos = var11_10 + 1;
                var13_12 = var12_11.charAt(var11_10);
                if (var13_12 <= '\u001f') throw new ParseException("String contains control character");
                if (var13_12 == '\\') continue block1;
            } while (var13_12 != '\"');
            break;
        } while (true);
        var3_5.append((CharSequence)this.src, var1_1, -1 + this.pos);
        return var3_5.toString();
    }

    private Boolean readTrue() throws ParseException {
        int n = this.length;
        int n2 = this.pos;
        if (n - n2 >= 3 && this.src.charAt(n2) == 'r' && this.src.charAt(1 + this.pos) == 'u' && this.src.charAt(2 + this.pos) == 'e') {
            this.pos = 3 + this.pos;
            return Boolean.TRUE;
        }
        throw new ParseException("Unexpected token: t");
    }

    /*
     * Exception decompiling
     */
    private Object readValue() throws ParseException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.b.a.a.b.as.a(SwitchReplacer.java:478)
        // org.benf.cfr.reader.b.a.a.b.as.a(SwitchReplacer.java:61)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:372)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
        // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
        // org.benf.cfr.reader.b.a(Driver.java:128)
        // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
        // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
        // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
        // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        // java.lang.Thread.run(Thread.java:919)
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public Object parseValue(String string2) throws ParseException {
        block4 : {
            JsonParser jsonParser = this;
            // MONITORENTER : jsonParser
            if (string2 == null) throw new ParseException("Input string may not be null");
            this.pos = 0;
            this.length = string2.length();
            this.src = string2;
            Object object = this.readValue();
            this.consumeWhitespace();
            int n = this.pos;
            int n2 = this.length;
            if (n < n2) break block4;
            return object;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected end of stream at char ");
        stringBuilder.append(this.pos);
        throw new ParseException(stringBuilder.toString());
    }

    public static class ParseException
    extends Exception {
        private static final long serialVersionUID = 4804542791749920772L;

        ParseException(Exception exception) {
            super((Throwable)exception);
        }

        ParseException(String string2) {
            super(string2);
        }
    }

}

