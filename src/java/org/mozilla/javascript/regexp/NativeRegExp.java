/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.regexp.CompilerState;
import org.mozilla.javascript.regexp.NativeRegExpCtor;
import org.mozilla.javascript.regexp.REBackTrackData;
import org.mozilla.javascript.regexp.RECharSet;
import org.mozilla.javascript.regexp.RECompiled;
import org.mozilla.javascript.regexp.REGlobalData;
import org.mozilla.javascript.regexp.RENode;
import org.mozilla.javascript.regexp.REProgState;
import org.mozilla.javascript.regexp.RegExpImpl;
import org.mozilla.javascript.regexp.SubString;

public class NativeRegExp
extends IdScriptableObject
implements Function {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int ANCHOR_BOL = -2;
    private static final int INDEX_LEN = 2;
    private static final int Id_compile = 1;
    private static final int Id_exec = 4;
    private static final int Id_global = 3;
    private static final int Id_ignoreCase = 4;
    private static final int Id_lastIndex = 1;
    private static final int Id_multiline = 5;
    private static final int Id_prefix = 6;
    private static final int Id_source = 2;
    private static final int Id_test = 5;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    public static final int JSREG_FOLD = 2;
    public static final int JSREG_GLOB = 1;
    public static final int JSREG_MULTILINE = 4;
    public static final int MATCH = 1;
    private static final int MAX_INSTANCE_ID = 5;
    private static final int MAX_PROTOTYPE_ID = 6;
    public static final int PREFIX = 2;
    private static final Object REGEXP_TAG = new Object();
    private static final byte REOP_ALNUM = 9;
    private static final byte REOP_ALT = 31;
    private static final byte REOP_ALTPREREQ = 53;
    private static final byte REOP_ALTPREREQ2 = 55;
    private static final byte REOP_ALTPREREQi = 54;
    private static final byte REOP_ASSERT = 41;
    private static final byte REOP_ASSERTNOTTEST = 44;
    private static final byte REOP_ASSERTTEST = 43;
    private static final byte REOP_ASSERT_NOT = 42;
    private static final byte REOP_BACKREF = 13;
    private static final byte REOP_BOL = 2;
    private static final byte REOP_CLASS = 22;
    private static final byte REOP_DIGIT = 7;
    private static final byte REOP_DOT = 6;
    private static final byte REOP_EMPTY = 1;
    private static final byte REOP_END = 57;
    private static final byte REOP_ENDCHILD = 49;
    private static final byte REOP_EOL = 3;
    private static final byte REOP_FLAT = 14;
    private static final byte REOP_FLAT1 = 15;
    private static final byte REOP_FLAT1i = 17;
    private static final byte REOP_FLATi = 16;
    private static final byte REOP_JUMP = 32;
    private static final byte REOP_LPAREN = 29;
    private static final byte REOP_MINIMALOPT = 47;
    private static final byte REOP_MINIMALPLUS = 46;
    private static final byte REOP_MINIMALQUANT = 48;
    private static final byte REOP_MINIMALREPEAT = 52;
    private static final byte REOP_MINIMALSTAR = 45;
    private static final byte REOP_NCLASS = 23;
    private static final byte REOP_NONALNUM = 10;
    private static final byte REOP_NONDIGIT = 8;
    private static final byte REOP_NONSPACE = 12;
    private static final byte REOP_OPT = 28;
    private static final byte REOP_PLUS = 27;
    private static final byte REOP_QUANT = 25;
    private static final byte REOP_REPEAT = 51;
    private static final byte REOP_RPAREN = 30;
    private static final byte REOP_SIMPLE_END = 23;
    private static final byte REOP_SIMPLE_START = 1;
    private static final byte REOP_SPACE = 11;
    private static final byte REOP_STAR = 26;
    private static final byte REOP_UCFLAT1 = 18;
    private static final byte REOP_UCFLAT1i = 19;
    private static final byte REOP_WBDRY = 4;
    private static final byte REOP_WNONBDRY = 5;
    public static final int TEST = 0;
    private static final boolean debug = false;
    private static final long serialVersionUID = 4965263491464903264L;
    Object lastIndex;
    private int lastIndexAttr;
    private RECompiled re;

    NativeRegExp() {
        this.lastIndex = 0.0;
        this.lastIndexAttr = 6;
    }

    NativeRegExp(Scriptable scriptable, RECompiled rECompiled) {
        Double d = 0.0;
        this.lastIndex = d;
        this.lastIndexAttr = 6;
        this.re = rECompiled;
        this.lastIndex = d;
        ScriptRuntime.setBuiltinProtoAndParent(this, scriptable, TopLevel.Builtins.RegExp);
    }

    private static void addCharacterRangeToCharSet(RECharSet rECharSet, char c, char c2) {
        EcmaError ecmaError;
        int n = c / 8;
        int n2 = c2 / 8;
        if (c2 < rECharSet.length && c <= c2) {
            char c3 = (char)(c & 7);
            char c4 = (char)(c2 & 7);
            if (n == n2) {
                byte[] arrby = rECharSet.bits;
                arrby[n] = (byte)(arrby[n] | 255 >> 7 - (c4 - c3) << c3);
                return;
            }
            byte[] arrby = rECharSet.bits;
            arrby[n] = (byte)(arrby[n] | 255 << c3);
            for (int i = n + 1; i < n2; ++i) {
                rECharSet.bits[i] = -1;
            }
            byte[] arrby2 = rECharSet.bits;
            arrby2[n2] = (byte)(arrby2[n2] | 255 >> 7 - c4);
            return;
        }
        ecmaError = ScriptRuntime.constructError("SyntaxError", "invalid range in character class");
        throw ecmaError;
    }

    private static void addCharacterToCharSet(RECharSet rECharSet, char c) {
        int n = c / 8;
        if (c < rECharSet.length) {
            byte[] arrby = rECharSet.bits;
            arrby[n] = (byte)(arrby[n] | 1 << (c & 7));
            return;
        }
        throw ScriptRuntime.constructError("SyntaxError", "invalid range in character class");
    }

    private static int addIndex(byte[] arrby, int n, int n2) {
        if (n2 >= 0) {
            if (n2 <= 65535) {
                arrby[n] = (byte)(n2 >> 8);
                arrby[n + 1] = (byte)n2;
                return n + 2;
            }
            throw Context.reportRuntimeError("Too complex regexp");
        }
        throw Kit.codeBug();
    }

    private static boolean backrefMatcher(REGlobalData rEGlobalData, int n, String string, int n2) {
        if (rEGlobalData.parens != null) {
            if (n >= rEGlobalData.parens.length) {
                return false;
            }
            int n3 = rEGlobalData.parensIndex(n);
            if (n3 == -1) {
                return true;
            }
            int n4 = rEGlobalData.parensLength(n);
            if (n4 + rEGlobalData.cp > n2) {
                return false;
            }
            if ((2 & rEGlobalData.regexp.flags) != 0) {
                for (int i = 0; i < n4; ++i) {
                    char c;
                    char c2 = string.charAt(n3 + i);
                    if (c2 == (c = string.charAt(i + rEGlobalData.cp)) || NativeRegExp.upcase(c2) == NativeRegExp.upcase(c)) continue;
                    return false;
                }
            } else if (!string.regionMatches(n3, string, rEGlobalData.cp, n4)) {
                return false;
            }
            rEGlobalData.cp = n4 + rEGlobalData.cp;
            return true;
        }
        return false;
    }

    private static boolean calculateBitmapSize(CompilerState compilerState, RENode rENode, char[] arrc, int n, int n2) {
        int n3 = n;
        rENode.bmsize = 0;
        rENode.sense = true;
        if (n3 == n2) {
            return true;
        }
        char c = arrc[n3];
        int n4 = 0;
        int n5 = 0;
        boolean bl = false;
        if (c == '^') {
            ++n3;
            rENode.sense = false;
        }
        while (n3 != n2) {
            block35 : {
                int n6;
                block34 : {
                    int n7;
                    block36 : {
                        block37 : {
                            int n8;
                            block33 : {
                                n8 = 2;
                                if (arrc[n3] == '\\') break block33;
                                int n9 = n3 + 1;
                                n6 = arrc[n3];
                                n3 = n9;
                                break block34;
                            }
                            int n10 = n3 + 1;
                            n7 = n10 + 1;
                            int n11 = arrc[n10];
                            if (n11 == 68 || n11 == 83 || n11 == 87) break block35;
                            if (n11 == 102) break block36;
                            if (n11 == 110) break block37;
                            switch (n11) {
                                default: {
                                    switch (n11) {
                                        default: {
                                            switch (n11) {
                                                default: {
                                                    n6 = n11;
                                                    n3 = n7;
                                                    break;
                                                }
                                                case 118: {
                                                    n6 = 11;
                                                    n3 = n7;
                                                    break;
                                                }
                                                case 117: {
                                                    n8 += 2;
                                                }
                                                case 120: {
                                                    int n12 = 0;
                                                    for (int i = 0; i < n8 && n7 < n2; ++i) {
                                                        int n13 = n7 + 1;
                                                        if ((n12 = Kit.xDigitToInt(arrc[n7], n12)) < 0) {
                                                            n7 = n13 - (i + 1);
                                                            n12 = 92;
                                                            break;
                                                        }
                                                        n7 = n13;
                                                    }
                                                    n6 = n12;
                                                    n3 = n7;
                                                    break;
                                                }
                                                case 116: {
                                                    n6 = 9;
                                                    n3 = n7;
                                                    break;
                                                }
                                                case 115: 
                                                case 119: {
                                                    break block35;
                                                }
                                                case 114: {
                                                    n6 = 13;
                                                    n3 = n7;
                                                    break;
                                                }
                                            }
                                            break block34;
                                        }
                                        case 100: {
                                            if (bl) {
                                                NativeRegExp.reportError("msg.bad.range", "");
                                                return false;
                                            }
                                            n6 = 57;
                                            n3 = n7;
                                            break;
                                        }
                                        case 99: {
                                            int n14;
                                            if (n7 < n2 && NativeRegExp.isControlLetter(arrc[n7])) {
                                                n14 = n7 + 1;
                                                (char)(31 & arrc[n7]);
                                            } else {
                                                n14 = n7 - 1;
                                            }
                                            n6 = 92;
                                            n3 = n14;
                                            break;
                                        }
                                        case 98: {
                                            n6 = 8;
                                            n3 = n7;
                                            break;
                                        }
                                    }
                                    break block34;
                                }
                                case 48: 
                                case 49: 
                                case 50: 
                                case 51: 
                                case 52: 
                                case 53: 
                                case 54: 
                                case 55: {
                                    int n15 = n11 - 48;
                                    char c2 = arrc[n7];
                                    if ('0' <= c2 && c2 <= '7') {
                                        char c3;
                                        n15 = n15 * 8 + (c2 - 48);
                                        if ('0' <= (c3 = arrc[++n7]) && c3 <= '7') {
                                            ++n7;
                                            int n16 = n15 * 8 + (c3 - 48);
                                            if (n16 <= 255) {
                                                n15 = n16;
                                            }
                                        }
                                    }
                                    n6 = n15;
                                    n3 = --n7;
                                    break;
                                }
                            }
                            break block34;
                        }
                        n6 = 10;
                        n3 = n7;
                        break block34;
                    }
                    n6 = 12;
                    n3 = n7;
                }
                if (bl) {
                    if (n4 > n6) {
                        NativeRegExp.reportError("msg.bad.range", "");
                        return false;
                    }
                    bl = false;
                } else if (n3 < n2 - 1 && arrc[n3] == '-') {
                    ++n3;
                    bl = true;
                    n4 = (char)n6;
                    continue;
                }
                if ((2 & compilerState.flags) != 0) {
                    char c4;
                    char c5 = NativeRegExp.upcase((char)n6);
                    char c6 = c5 >= (c4 = NativeRegExp.downcase((char)n6)) ? c5 : c4;
                    n6 = c6;
                }
                if (n6 <= n5) continue;
                n5 = n6;
                continue;
            }
            if (bl) {
                NativeRegExp.reportError("msg.bad.range", "");
                return false;
            }
            rENode.bmsize = 65536;
            return true;
        }
        rENode.bmsize = n5 + 1;
        return true;
    }

    private static boolean classMatcher(REGlobalData rEGlobalData, RECharSet rECharSet, char c) {
        if (!rECharSet.converted) {
            NativeRegExp.processCharSet(rEGlobalData, rECharSet);
        }
        int n = c >> 3;
        int n2 = rECharSet.length;
        int n3 = 1;
        if (n2 != 0 && c < rECharSet.length && (rECharSet.bits[n] & n3 << (c & 7)) != 0) {
            n3 = 0;
        }
        return n3 ^ rECharSet.sense;
    }

    static RECompiled compileRE(Context context, String string, String string2, boolean bl) {
        RECompiled rECompiled = new RECompiled(string);
        int n = string.length();
        int n2 = 0;
        if (string2 != null) {
            for (int i = 0; i < string2.length(); ++i) {
                char c = string2.charAt(i);
                int n3 = 0;
                if (c == 'g') {
                    n3 = 1;
                } else if (c == 'i') {
                    n3 = 2;
                } else if (c == 'm') {
                    n3 = 4;
                } else {
                    NativeRegExp.reportError("msg.invalid.re.flag", String.valueOf((char)c));
                }
                if ((n2 & n3) != 0) {
                    NativeRegExp.reportError("msg.invalid.re.flag", String.valueOf((char)c));
                }
                n2 |= n3;
            }
        }
        rECompiled.flags = n2;
        CompilerState compilerState = new CompilerState(context, rECompiled.source, n, n2);
        if (bl && n > 0) {
            compilerState.result = new RENode(14);
            compilerState.result.chr = compilerState.cpbegin[0];
            compilerState.result.length = n;
            compilerState.result.flatIndex = 0;
            compilerState.progLength = 5 + compilerState.progLength;
        } else {
            if (!NativeRegExp.parseDisjunction(compilerState)) {
                return null;
            }
            if (compilerState.maxBackReference > compilerState.parenCount) {
                compilerState = new CompilerState(context, rECompiled.source, n, n2);
                compilerState.backReferenceLimit = compilerState.parenCount;
                if (!NativeRegExp.parseDisjunction(compilerState)) {
                    return null;
                }
            }
        }
        rECompiled.program = new byte[1 + compilerState.progLength];
        if (compilerState.classCount != 0) {
            rECompiled.classList = new RECharSet[compilerState.classCount];
            rECompiled.classCount = compilerState.classCount;
        }
        int n4 = NativeRegExp.emitREBytecode(compilerState, rECompiled, 0, compilerState.result);
        byte[] arrby = rECompiled.program;
        n4 + 1;
        arrby[n4] = 57;
        rECompiled.parenCount = compilerState.parenCount;
        byte by = rECompiled.program[0];
        if (by != 2) {
            if (by != 31) {
                switch (by) {
                    default: {
                        return rECompiled;
                    }
                    case 18: 
                    case 19: {
                        rECompiled.anchorCh = (char)NativeRegExp.getIndex(rECompiled.program, 1);
                        return rECompiled;
                    }
                    case 15: 
                    case 17: {
                        rECompiled.anchorCh = (char)(255 & rECompiled.program[1]);
                        return rECompiled;
                    }
                    case 14: 
                    case 16: 
                }
                int n5 = NativeRegExp.getIndex(rECompiled.program, 1);
                rECompiled.anchorCh = rECompiled.source[n5];
                return rECompiled;
            }
            RENode rENode = compilerState.result;
            if (rENode.kid.op == 2 && rENode.kid2.op == 2) {
                rECompiled.anchorCh = -2;
                return rECompiled;
            }
        } else {
            rECompiled.anchorCh = -2;
        }
        return rECompiled;
    }

    private static void doFlat(CompilerState compilerState, char c) {
        compilerState.result = new RENode(14);
        compilerState.result.chr = c;
        compilerState.result.length = 1;
        compilerState.result.flatIndex = -1;
        compilerState.progLength = 3 + compilerState.progLength;
    }

    private static char downcase(char c) {
        if (c < '') {
            if ('A' <= c && c <= 'Z') {
                return (char)(c + 32);
            }
            return c;
        }
        char c2 = Character.toLowerCase((char)c);
        if (c2 < '') {
            return c;
        }
        return c2;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private static int emitREBytecode(CompilerState compilerState, RECompiled rECompiled, int n, RENode rENode) {
        arrby = rECompiled.program;
        while (rENode != null) {
            block32 : {
                block23 : {
                    block24 : {
                        block25 : {
                            block26 : {
                                block27 : {
                                    block28 : {
                                        block29 : {
                                            block30 : {
                                                block31 : {
                                                    n2 = n + 1;
                                                    arrby[n] = rENode.op;
                                                    by2 = rENode.op;
                                                    by = 1;
                                                    if (by2 == by) break block23;
                                                    if (by2 == 22) break block24;
                                                    if (by2 == 25) break block25;
                                                    if (by2 == 29) break block26;
                                                    if (by2 == 31) break block27;
                                                    if (by2 == 13) break block28;
                                                    if (by2 == 14) break block29;
                                                    if (by2 == 41) break block30;
                                                    if (by2 == 42) break block31;
                                                    switch (by2) {
                                                        default: {
                                                            n = n2;
                                                            break block32;
                                                        }
                                                        case 53: 
                                                        case 54: 
                                                        case 55: {
                                                            if (rENode.op != 54) {
                                                                by = 0;
                                                            }
                                                            by3 = by;
                                                            c = rENode.chr;
                                                            if (by3 != 0) {
                                                                c = NativeRegExp.upcase(c);
                                                            }
                                                            NativeRegExp.addIndex(arrby, n2, c);
                                                            n3 = n2 + 2;
                                                            n4 = rENode.index;
                                                            if (by3 != 0) {
                                                                n4 = NativeRegExp.upcase((char)n4);
                                                            }
                                                            NativeRegExp.addIndex(arrby, n3, n4);
                                                            n2 = n3 + 2;
                                                            ** break;
lbl35: // 1 sources:
                                                            break;
                                                        }
                                                    }
                                                    break block27;
                                                }
                                                n5 = n2;
                                                n6 = NativeRegExp.emitREBytecode(compilerState, rECompiled, n2 + 2, rENode.kid);
                                                n7 = n6 + 1;
                                                arrby[n6] = 44;
                                                NativeRegExp.resolveForwardJump(arrby, n5, n7);
                                                n = n7;
                                                break block32;
                                            }
                                            n8 = n2;
                                            n9 = NativeRegExp.emitREBytecode(compilerState, rECompiled, n2 + 2, rENode.kid);
                                            n10 = n9 + 1;
                                            arrby[n9] = 43;
                                            NativeRegExp.resolveForwardJump(arrby, n8, n10);
                                            n = n10;
                                            break block32;
                                        }
                                        if (rENode.flatIndex != -1) {
                                            while (rENode.next != null && rENode.next.op == 14 && rENode.flatIndex + rENode.length == rENode.next.flatIndex) {
                                                rENode.length += rENode.next.length;
                                                rENode.next = rENode.next.next;
                                            }
                                        }
                                        if (rENode.flatIndex != -1 && rENode.length > by) {
                                            arrby[n2 - 1] = (2 & compilerState.flags) != 0 ? 16 : 14;
                                            n = NativeRegExp.addIndex(arrby, NativeRegExp.addIndex(arrby, n2, rENode.flatIndex), rENode.length);
                                        } else if (rENode.chr < '\u0100') {
                                            arrby[n2 - 1] = (2 & compilerState.flags) != 0 ? 17 : 15;
                                            n = n2 + 1;
                                            arrby[n2] = (byte)rENode.chr;
                                        } else {
                                            arrby[n2 - 1] = (2 & compilerState.flags) != 0 ? 19 : 18;
                                            n = NativeRegExp.addIndex(arrby, n2, rENode.chr);
                                        }
                                        break block32;
                                    }
                                    n = NativeRegExp.addIndex(arrby, n2, rENode.parenIndex);
                                    break block32;
                                }
                                rENode2 = rENode.kid2;
                                n11 = n2;
                                n12 = NativeRegExp.emitREBytecode(compilerState, rECompiled, n2 + 2, rENode.kid);
                                n13 = n12 + 1;
                                arrby[n12] = 32;
                                n14 = n13 + 2;
                                NativeRegExp.resolveForwardJump(arrby, n11, n14);
                                n15 = NativeRegExp.emitREBytecode(compilerState, rECompiled, n14, rENode2);
                                n16 = n15 + 1;
                                arrby[n15] = 32;
                                n17 = n16 + 2;
                                NativeRegExp.resolveForwardJump(arrby, n13, n17);
                                NativeRegExp.resolveForwardJump(arrby, n16, n17);
                                n = n17;
                                break block32;
                            }
                            n18 = NativeRegExp.emitREBytecode(compilerState, rECompiled, NativeRegExp.addIndex(arrby, n2, rENode.parenIndex), rENode.kid);
                            n19 = n18 + 1;
                            arrby[n18] = 30;
                            n = NativeRegExp.addIndex(arrby, n19, rENode.parenIndex);
                            break block32;
                        }
                        if (rENode.min == 0 && rENode.max == -1) {
                            n20 = n2 - 1;
                            n21 = rENode.greedy != false ? 26 : 45;
                            arrby[n20] = n21;
                        } else if (rENode.min == 0 && rENode.max == by) {
                            n22 = n2 - 1;
                            n23 = rENode.greedy != false ? 28 : 47;
                            arrby[n22] = n23;
                        } else if (rENode.min == by && rENode.max == -1) {
                            n24 = n2 - 1;
                            n25 = rENode.greedy != false ? 27 : 46;
                            arrby[n24] = n25;
                        } else {
                            if (!rENode.greedy) {
                                arrby[n2 - 1] = 48;
                            }
                            n2 = NativeRegExp.addIndex(arrby, NativeRegExp.addIndex(arrby, n2, rENode.min), by + rENode.max);
                        }
                        n26 = NativeRegExp.addIndex(arrby, NativeRegExp.addIndex(arrby, n2, rENode.parenCount), rENode.parenIndex);
                        n27 = n26 + 2;
                        rENode3 = rENode.kid;
                        n28 = NativeRegExp.emitREBytecode(compilerState, rECompiled, n27, rENode3);
                        n29 = n28 + 1;
                        arrby[n28] = 49;
                        NativeRegExp.resolveForwardJump(arrby, n26, n29);
                        n = n29;
                        break block32;
                    }
                    if (!rENode.sense) {
                        arrby[n2 - 1] = 23;
                    }
                    n = NativeRegExp.addIndex(arrby, n2, rENode.index);
                    rECompiled.classList[rENode.index] = new RECharSet(rENode.bmsize, rENode.startIndex, rENode.kidlen, rENode.sense);
                    break block32;
                }
                n = n2 - 1;
            }
            rENode = rENode.next;
        }
        return n;
        catch (Throwable throwable) {
            throw throwable;
        }
    }

    private static String escapeRegExp(Object object) {
        String string = ScriptRuntime.toString(object);
        StringBuilder stringBuilder = null;
        int n = 0;
        int n2 = string.indexOf(47);
        while (n2 > -1) {
            if (n2 == n || string.charAt(n2 - 1) != '\\') {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append((CharSequence)string, n, n2);
                stringBuilder.append("\\/");
                n = n2 + 1;
            }
            n2 = string.indexOf(47, n2 + 1);
        }
        if (stringBuilder != null) {
            stringBuilder.append((CharSequence)string, n, string.length());
            string = stringBuilder.toString();
        }
        return string;
    }

    private Object execSub(Context context, Scriptable scriptable, Object[] arrobject, int n) {
        Object object;
        String string;
        RegExpImpl regExpImpl = NativeRegExp.getImpl(context);
        String string2 = arrobject.length == 0 ? ((string = regExpImpl.input) == null ? ScriptRuntime.toString(Undefined.instance) : string) : ScriptRuntime.toString(arrobject[0]);
        double d = (1 & this.re.flags) != 0 ? ScriptRuntime.toInteger(this.lastIndex) : 0.0;
        double d2 = 0.0;
        if (!(d < d2) && !((double)string2.length() < d)) {
            int[] arrn = new int[]{(int)d};
            object = this.executeRegExp(context, scriptable, regExpImpl, string2, arrn, n);
            if ((1 & this.re.flags) != 0) {
                if (object != null && object != Undefined.instance) {
                    d2 = arrn[0];
                }
                this.lastIndex = d2;
                return object;
            }
        } else {
            this.lastIndex = d2;
            object = null;
        }
        return object;
    }

    /*
     * Exception decompiling
     */
    private static boolean executeREBytecode(REGlobalData var0, String var1_1, int var2_2) {
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

    private static boolean flatNIMatcher(REGlobalData rEGlobalData, int n, int n2, String string, int n3) {
        if (n2 + rEGlobalData.cp > n3) {
            return false;
        }
        char[] arrc = rEGlobalData.regexp.source;
        for (int i = 0; i < n2; ++i) {
            char c = arrc[n + i];
            char c2 = string.charAt(i + rEGlobalData.cp);
            if (c == c2 || NativeRegExp.upcase(c) == NativeRegExp.upcase(c2)) continue;
            return false;
        }
        rEGlobalData.cp = n2 + rEGlobalData.cp;
        return true;
    }

    private static boolean flatNMatcher(REGlobalData rEGlobalData, int n, int n2, String string, int n3) {
        if (n2 + rEGlobalData.cp > n3) {
            return false;
        }
        for (int i = 0; i < n2; ++i) {
            if (rEGlobalData.regexp.source[n + i] == string.charAt(i + rEGlobalData.cp)) continue;
            return false;
        }
        rEGlobalData.cp = n2 + rEGlobalData.cp;
        return true;
    }

    private static int getDecimalValue(char c, CompilerState compilerState, int n, String string) {
        char c2;
        boolean bl = false;
        int n2 = compilerState.cp;
        char[] arrc = compilerState.cpbegin;
        int n3 = c - 48;
        while (compilerState.cp != compilerState.cpend && NativeRegExp.isDigit(c2 = arrc[compilerState.cp])) {
            if (!bl) {
                int n4 = n3 * 10 + (c2 - 48);
                if (n4 < n) {
                    n3 = n4;
                } else {
                    bl = true;
                    n3 = n;
                }
            }
            compilerState.cp = 1 + compilerState.cp;
        }
        if (bl) {
            NativeRegExp.reportError(string, String.valueOf((char[])arrc, (int)n2, (int)(compilerState.cp - n2)));
        }
        return n3;
    }

    private static RegExpImpl getImpl(Context context) {
        return (RegExpImpl)ScriptRuntime.getRegExpProxy(context);
    }

    private static int getIndex(byte[] arrby, int n) {
        return (255 & arrby[n]) << 8 | 255 & arrby[n + 1];
    }

    private static int getOffset(byte[] arrby, int n) {
        return NativeRegExp.getIndex(arrby, n);
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        NativeRegExp nativeRegExp = new NativeRegExp();
        nativeRegExp.re = NativeRegExp.compileRE(context, "", null, false);
        nativeRegExp.activatePrototypeMap(6);
        nativeRegExp.setParentScope(scriptable);
        nativeRegExp.setPrototype(NativeRegExp.getObjectPrototype(scriptable));
        NativeRegExpCtor nativeRegExpCtor = new NativeRegExpCtor();
        nativeRegExp.defineProperty("constructor", (Object)nativeRegExpCtor, 2);
        ScriptRuntime.setFunctionProtoAndParent(nativeRegExpCtor, scriptable);
        nativeRegExpCtor.setImmunePrototypeProperty(nativeRegExp);
        if (bl) {
            nativeRegExp.sealObject();
            nativeRegExpCtor.sealObject();
        }
        NativeRegExp.defineProperty(scriptable, "RegExp", nativeRegExpCtor, 2);
    }

    private static boolean isControlLetter(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private static boolean isLineTerm(char c) {
        return ScriptRuntime.isJSLineTerminator(c);
    }

    private static boolean isREWhiteSpace(int n) {
        return ScriptRuntime.isJSWhitespaceOrLineTerminator(n);
    }

    private static boolean isWord(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || NativeRegExp.isDigit(c) || c == '_';
        {
        }
    }

    private static boolean matchRegExp(REGlobalData rEGlobalData, RECompiled rECompiled, String string, int n, int n2, boolean bl) {
        rEGlobalData.parens = rECompiled.parenCount != 0 ? new long[rECompiled.parenCount] : null;
        rEGlobalData.backTrackStackTop = null;
        rEGlobalData.stateStackTop = null;
        boolean bl2 = bl || (4 & rECompiled.flags) != 0;
        rEGlobalData.multiline = bl2;
        rEGlobalData.regexp = rECompiled;
        int n3 = rEGlobalData.regexp.anchorCh;
        int n4 = n;
        while (n4 <= n2) {
            if (n3 >= 0) {
                do {
                    if (n4 == n2) {
                        return false;
                    }
                    char c = string.charAt(n4);
                    if (c == n3 || (2 & rEGlobalData.regexp.flags) != 0 && NativeRegExp.upcase(c) == NativeRegExp.upcase((char)n3)) break;
                    ++n4;
                } while (true);
            }
            rEGlobalData.cp = n4;
            rEGlobalData.skipped = n4 - n;
            for (int i = 0; i < rECompiled.parenCount; ++i) {
                rEGlobalData.parens[i] = -1L;
            }
            boolean bl3 = NativeRegExp.executeREBytecode(rEGlobalData, string, n2);
            rEGlobalData.backTrackStackTop = null;
            rEGlobalData.stateStackTop = null;
            if (bl3) {
                return true;
            }
            if (n3 == -2 && !rEGlobalData.multiline) {
                rEGlobalData.skipped = n2;
                return false;
            }
            n4 = 1 + (n + rEGlobalData.skipped);
        }
        return false;
    }

    private static boolean parseAlternative(CompilerState compilerState) {
        RENode rENode = null;
        RENode rENode2 = null;
        char[] arrc = compilerState.cpbegin;
        while (compilerState.cp != compilerState.cpend && arrc[compilerState.cp] != '|' && (compilerState.parenNesting == 0 || arrc[compilerState.cp] != ')')) {
            if (!NativeRegExp.parseTerm(compilerState)) {
                return false;
            }
            if (rENode == null) {
                rENode2 = rENode = compilerState.result;
            } else {
                rENode2.next = compilerState.result;
            }
            while (rENode2.next != null) {
                rENode2 = rENode2.next;
            }
        }
        if (rENode == null) {
            compilerState.result = new RENode(1);
            return true;
        }
        compilerState.result = rENode;
        return true;
    }

    private static boolean parseDisjunction(CompilerState compilerState) {
        if (!NativeRegExp.parseAlternative(compilerState)) {
            return false;
        }
        int n = compilerState.cp;
        char[] arrc = compilerState.cpbegin;
        if (n != arrc.length && arrc[n] == '|') {
            compilerState.cp = 1 + compilerState.cp;
            RENode rENode = new RENode(31);
            rENode.kid = compilerState.result;
            if (!NativeRegExp.parseDisjunction(compilerState)) {
                return false;
            }
            rENode.kid2 = compilerState.result;
            compilerState.result = rENode;
            if (rENode.kid.op == 14 && rENode.kid2.op == 14) {
                int n2 = (2 & compilerState.flags) == 0 ? 53 : 54;
                rENode.op = (byte)n2;
                rENode.chr = rENode.kid.chr;
                rENode.index = rENode.kid2.chr;
                compilerState.progLength = 13 + compilerState.progLength;
                return true;
            }
            if (rENode.kid.op == 22 && rENode.kid.index < 256 && rENode.kid2.op == 14 && (2 & compilerState.flags) == 0) {
                rENode.op = (byte)55;
                rENode.chr = rENode.kid2.chr;
                rENode.index = rENode.kid.index;
                compilerState.progLength = 13 + compilerState.progLength;
                return true;
            }
            if (rENode.kid.op == 14 && rENode.kid2.op == 22 && rENode.kid2.index < 256 && (2 & compilerState.flags) == 0) {
                rENode.op = (byte)55;
                rENode.chr = rENode.kid.chr;
                rENode.index = rENode.kid2.index;
                compilerState.progLength = 13 + compilerState.progLength;
                return true;
            }
            compilerState.progLength = 9 + compilerState.progLength;
        }
        return true;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static boolean parseTerm(CompilerState var0) {
        block75 : {
            block71 : {
                block72 : {
                    block73 : {
                        block74 : {
                            block69 : {
                                block70 : {
                                    block68 : {
                                        var1_1 = var0.cpbegin;
                                        var2_2 = var0.cp;
                                        var0.cp = var2_2 + 1;
                                        var3_3 = var1_1[var2_2];
                                        var4_4 = 2;
                                        var5_5 = var0.parenCount;
                                        if (var3_3 == '$') {
                                            var0.result = new RENode(3);
                                            var0.progLength = 1 + var0.progLength;
                                            return true;
                                        }
                                        if (var3_3 == '.') break block68;
                                        if (var3_3 == '?') ** GOTO lbl-1000
                                        if (var3_3 == '^') ** GOTO lbl184
                                        if (var3_3 == '[') ** GOTO lbl181
                                        if (var3_3 == '\\') ** GOTO lbl56
                                        switch (var3_3) {
                                            default: {
                                                var0.result = new RENode(14);
                                                var0.result.chr = var3_3;
                                                var0.result.length = 1;
                                                var0.result.flatIndex = var0.cp - 1;
                                                var0.progLength = 3 + var0.progLength;
                                                break block69;
                                            }
                                            case ')': {
                                                NativeRegExp.reportError("msg.re.unmatched.right.paren", "");
                                                return false;
                                            }
                                            case '(': {
                                                if (1 + var0.cp < var0.cpend && var1_1[var0.cp] == '?' && ((var43_6 = var1_1[1 + var0.cp]) == '=' || var43_6 == '!' || var43_6 == ':')) {
                                                    var0.cp = 2 + var0.cp;
                                                    if (var43_6 == '=') {
                                                        var41_7 = new RENode(41);
                                                        var0.progLength = 4 + var0.progLength;
                                                    } else {
                                                        var41_7 = null;
                                                        if (var43_6 == '!') {
                                                            var41_7 = new RENode(42);
                                                            var0.progLength = 4 + var0.progLength;
                                                        }
                                                    }
                                                } else {
                                                    var41_7 = new RENode(29);
                                                    var0.progLength = 6 + var0.progLength;
                                                    var42_8 = var0.parenCount;
                                                    var0.parenCount = var42_8 + 1;
                                                    var41_7.parenIndex = var42_8;
                                                }
                                                var0.parenNesting = 1 + var0.parenNesting;
                                                if (!NativeRegExp.parseDisjunction(var0)) {
                                                    return false;
                                                }
                                                if (var0.cp == var0.cpend || var1_1[var0.cp] != ')') ** GOTO lbl54
                                                var0.cp = 1 + var0.cp;
                                                --var0.parenNesting;
                                                if (var41_7 != null) {
                                                    var41_7.kid = var0.result;
                                                    var0.result = var41_7;
                                                }
                                                break block69;
lbl54: // 1 sources:
                                                NativeRegExp.reportError("msg.unterm.paren", "");
                                                return false;
                                            }
lbl56: // 1 sources:
                                            if (var0.cp >= var0.cpend) {
                                                NativeRegExp.reportError("msg.trail.backslash", "");
                                                return false;
                                            }
                                            var26_9 = var0.cp;
                                            var0.cp = var26_9 + 1;
                                            var27_10 = var1_1[var26_9];
                                            if (var27_10 == 'B') {
                                                var0.result = new RENode(5);
                                                var0.progLength = 1 + var0.progLength;
                                                return true;
                                            }
                                            if (var27_10 != 'D') {
                                                if (var27_10 != 'S') {
                                                    if (var27_10 != 'W') {
                                                        if (var27_10 != 'f') {
                                                            if (var27_10 != 'n') {
                                                                switch (var27_10) {
                                                                    default: {
                                                                        switch (var27_10) {
                                                                            default: {
                                                                                switch (var27_10) {
                                                                                    default: {
                                                                                        var0.result = new RENode(14);
                                                                                        var0.result.chr = var27_10;
                                                                                        var0.result.length = 1;
                                                                                        var0.result.flatIndex = var0.cp - 1;
                                                                                        var0.progLength = 3 + var0.progLength;
                                                                                        ** break;
                                                                                    }
                                                                                    case 'w': {
                                                                                        var0.result = new RENode(9);
                                                                                        var0.progLength = 1 + var0.progLength;
                                                                                        ** break;
                                                                                    }
                                                                                    case 'v': {
                                                                                        NativeRegExp.doFlat(var0, '\u000b');
                                                                                        ** break;
                                                                                    }
                                                                                    case 'u': {
                                                                                        var4_4 += 2;
                                                                                    }
                                                                                    case 'x': {
                                                                                        var36_11 = 0;
                                                                                        for (var37_12 = 0; var37_12 < var4_4 && var0.cp < var0.cpend; ++var37_12) {
                                                                                            var38_13 = var0.cp;
                                                                                            var0.cp = var38_13 + 1;
                                                                                            if ((var36_11 = Kit.xDigitToInt(var1_1[var38_13], var36_11)) >= 0) continue;
                                                                                            var0.cp -= var37_12 + 2;
                                                                                            var39_14 = var0.cp;
                                                                                            var0.cp = var39_14 + 1;
                                                                                            var36_11 = var1_1[var39_14];
                                                                                            break;
                                                                                        }
                                                                                        NativeRegExp.doFlat(var0, (char)(var36_11 ? 1 : 0));
                                                                                        ** break;
                                                                                    }
                                                                                    case 't': {
                                                                                        NativeRegExp.doFlat(var0, '\t');
                                                                                        ** break;
                                                                                    }
                                                                                    case 's': {
                                                                                        var0.result = new RENode(11);
                                                                                        var0.progLength = 1 + var0.progLength;
                                                                                        ** break;
                                                                                    }
                                                                                    case 'r': 
                                                                                }
                                                                                NativeRegExp.doFlat(var0, '\r');
                                                                                ** break;
                                                                            }
                                                                            case 'd': {
                                                                                var0.result = new RENode(7);
                                                                                var0.progLength = 1 + var0.progLength;
                                                                                ** break;
                                                                            }
                                                                            case 'c': {
                                                                                if (var0.cp < var0.cpend && NativeRegExp.isControlLetter(var1_1[var0.cp])) {
                                                                                    var35_15 = var0.cp;
                                                                                    var0.cp = var35_15 + 1;
                                                                                    var34_16 = (char)(31 & var1_1[var35_15]);
                                                                                } else {
                                                                                    --var0.cp;
                                                                                    var34_16 = '\\';
                                                                                }
                                                                                NativeRegExp.doFlat(var0, var34_16);
                                                                                ** break;
                                                                            }
                                                                            case 'b': 
                                                                        }
                                                                        var0.result = new RENode(4);
                                                                        var0.progLength = 1 + var0.progLength;
                                                                        return true;
                                                                    }
                                                                    case '1': 
                                                                    case '2': 
                                                                    case '3': 
                                                                    case '4': 
                                                                    case '5': 
                                                                    case '6': 
                                                                    case '7': 
                                                                    case '8': 
                                                                    case '9': {
                                                                        var30_17 = var0.cp - 1;
                                                                        var31_18 = NativeRegExp.getDecimalValue(var27_10, var0, 65535, "msg.overlarge.backref");
                                                                        if (var31_18 > var0.backReferenceLimit) {
                                                                            NativeRegExp.reportWarning(var0.cx, "msg.bad.backref", "");
                                                                        }
                                                                        if (var31_18 > var0.backReferenceLimit) {
                                                                            var0.cp = var30_17;
                                                                            if (var27_10 >= '8') {
                                                                                NativeRegExp.doFlat(var0, '\\');
                                                                                ** break;
                                                                            }
                                                                            var0.cp = 1 + var0.cp;
                                                                            var32_19 = var27_10 - 48;
                                                                            while (var32_19 < 32 && var0.cp < var0.cpend && (var33_20 = var1_1[var0.cp]) >= '0' && var33_20 <= '7') {
                                                                                var0.cp = 1 + var0.cp;
                                                                                var32_19 = var32_19 * 8 + (var33_20 - 48);
                                                                            }
                                                                            NativeRegExp.doFlat(var0, (char)var32_19);
                                                                            ** break;
                                                                        }
                                                                        var0.result = new RENode(13);
                                                                        var0.result.parenIndex = var31_18 - 1;
                                                                        var0.progLength = 3 + var0.progLength;
                                                                        if (var0.maxBackReference >= var31_18) break;
                                                                        var0.maxBackReference = var31_18;
                                                                        ** break;
                                                                    }
                                                                    case '0': {
                                                                        NativeRegExp.reportWarning(var0.cx, "msg.bad.backref", "");
                                                                        var28_21 = 0;
                                                                        while (var28_21 < 32 && var0.cp < var0.cpend && (var29_22 = var1_1[var0.cp]) >= '0' && var29_22 <= '7') {
                                                                            var0.cp = 1 + var0.cp;
                                                                            var28_21 = var28_21 * 8 + (var29_22 - 48);
                                                                        }
                                                                        NativeRegExp.doFlat(var0, (char)var28_21);
                                                                        ** break;
lbl166: // 13 sources:
                                                                        break;
                                                                    }
                                                                }
                                                            } else {
                                                                NativeRegExp.doFlat(var0, '\n');
                                                            }
                                                        } else {
                                                            NativeRegExp.doFlat(var0, '\f');
                                                        }
                                                    } else {
                                                        var0.result = new RENode(10);
                                                        var0.progLength = 1 + var0.progLength;
                                                    }
                                                } else {
                                                    var0.result = new RENode(12);
                                                    var0.progLength = 1 + var0.progLength;
                                                }
                                            } else {
                                                var0.result = new RENode(8);
                                                var0.progLength = 1 + var0.progLength;
                                            }
                                            break block69;
lbl181: // 1 sources:
                                            var0.result = new RENode(22);
                                            var0.result.startIndex = var21_23 = var0.cp;
                                            break block70;
lbl184: // 1 sources:
                                            var0.result = new RENode(2);
                                            var0.progLength = 1 + var0.progLength;
                                            return true;
                                            case '*': 
                                            case '+': lbl-1000: // 2 sources:
                                            {
                                                NativeRegExp.reportError("msg.bad.quant", String.valueOf((char)var1_1[var0.cp - 1]));
                                                return false;
                                            }
                                        }
                                    }
                                    var0.result = new RENode(6);
                                    var0.progLength = 1 + var0.progLength;
                                    break block69;
                                }
                                do {
                                    if (var0.cp == var0.cpend) {
                                        NativeRegExp.reportError("msg.unterm.class", "");
                                        return false;
                                    }
                                    if (var1_1[var0.cp] == '\\') {
                                        var0.cp = 1 + var0.cp;
                                    } else if (var1_1[var0.cp] == ']') {
                                        var0.result.kidlen = var0.cp - var21_23;
                                        var22_24 = var0.result;
                                        var23_25 = var0.classCount;
                                        var0.classCount = var23_25 + 1;
                                        var22_24.index = var23_25;
                                        var24_26 = var0.result;
                                        var25_27 = var0.cp;
                                        var0.cp = var25_27 + 1;
                                        if (!NativeRegExp.calculateBitmapSize(var0, var24_26, var1_1, var21_23, var25_27)) {
                                            return false;
                                        }
                                        var0.progLength = 3 + var0.progLength;
                                        break;
                                    }
                                    var0.cp = 1 + var0.cp;
                                } while (true);
                            }
                            var6_28 = var0.result;
                            if (var0.cp == var0.cpend) {
                                return true;
                            }
                            var7_29 = var1_1[var0.cp];
                            if (var7_29 == '*') break block71;
                            if (var7_29 == '+') break block72;
                            if (var7_29 == '?') break block73;
                            if (var7_29 != '{') {
                                return true;
                            }
                            var9_31 = -1;
                            var10_32 = var0.cp;
                            var0.cp = var11_33 = 1 + var0.cp;
                            var12_34 = var1_1.length;
                            var8_30 = false;
                            if (var11_33 >= var12_34) break block74;
                            var13_35 = var1_1[var0.cp];
                            var14_36 = NativeRegExp.isDigit(var13_35);
                            var8_30 = false;
                            if (!var14_36) break block74;
                            var0.cp = 1 + var0.cp;
                            var15_37 = NativeRegExp.getDecimalValue(var13_35, var0, 65535, "msg.overlarge.min");
                            var16_38 = var0.cp;
                            var17_39 = var1_1.length;
                            var8_30 = false;
                            if (var16_38 >= var17_39) break block74;
                            var18_40 = var1_1[var0.cp];
                            if (var18_40 != ',') ** GOTO lbl-1000
                            var0.cp = var19_41 = 1 + var0.cp;
                            if (var19_41 < var1_1.length) {
                                var18_40 = var1_1[var0.cp];
                                if (NativeRegExp.isDigit(var18_40)) {
                                    var0.cp = var20_42 = 1 + var0.cp;
                                    if (var20_42 < var1_1.length) {
                                        var9_31 = NativeRegExp.getDecimalValue(var18_40, var0, 65535, "msg.overlarge.max");
                                        var18_40 = var1_1[var0.cp];
                                        if (var15_37 > var9_31) {
                                            NativeRegExp.reportError("msg.max.lt.min", String.valueOf((char)var1_1[var0.cp]));
                                            return false;
                                        }
                                    }
                                }
                            } else lbl-1000: // 2 sources:
                            {
                                var9_31 = var15_37;
                            }
                            var8_30 = false;
                            if (var18_40 == '}') {
                                var0.result = new RENode(25);
                                var0.result.min = var15_37;
                                var0.result.max = var9_31;
                                var0.progLength = 12 + var0.progLength;
                                var8_30 = true;
                            }
                        }
                        if (!var8_30) {
                            var0.cp = var10_32;
                        }
                        break block75;
                    }
                    var0.result = new RENode(25);
                    var0.result.min = 0;
                    var0.result.max = 1;
                    var0.progLength = 8 + var0.progLength;
                    var8_30 = true;
                    break block75;
                }
                var0.result = new RENode(25);
                var0.result.min = 1;
                var0.result.max = -1;
                var0.progLength = 8 + var0.progLength;
                var8_30 = true;
                break block75;
            }
            var0.result = new RENode(25);
            var0.result.min = 0;
            var0.result.max = -1;
            var0.progLength = 8 + var0.progLength;
            var8_30 = true;
        }
        if (!var8_30) {
            return true;
        }
        var0.cp = 1 + var0.cp;
        var0.result.kid = var6_28;
        var0.result.parenIndex = var5_5;
        var0.result.parenCount = var0.parenCount - var5_5;
        if (var0.cp < var0.cpend && var1_1[var0.cp] == '?') {
            var0.cp = 1 + var0.cp;
            var0.result.greedy = false;
            return true;
        }
        var0.result.greedy = true;
        return true;
    }

    private static REProgState popProgState(REGlobalData rEGlobalData) {
        REProgState rEProgState = rEGlobalData.stateStackTop;
        rEGlobalData.stateStackTop = rEProgState.previous;
        return rEProgState;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static void processCharSet(REGlobalData rEGlobalData, RECharSet rECharSet) {
        RECharSet rECharSet2 = rECharSet;
        synchronized (rECharSet2) {
            if (!rECharSet.converted) {
                NativeRegExp.processCharSetImpl(rEGlobalData, rECharSet);
                rECharSet.converted = true;
            }
            return;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static void processCharSetImpl(REGlobalData rEGlobalData, RECharSet rECharSet) {
        block42 : {
            char c;
            int n;
            boolean bl;
            int n2;
            block41 : {
                block40 : {
                    n = rECharSet.startIndex;
                    n2 = n + rECharSet.strlength;
                    rECharSet.bits = new byte[(7 + rECharSet.length) / 8];
                    if (n == n2) {
                        return;
                    }
                    if (rEGlobalData.regexp.source[n] != '^') break block40;
                    if (rECharSet.sense) throw new AssertionError();
                    ++n;
                    c = '\u0000';
                    bl = false;
                    break block41;
                }
                boolean bl2 = rECharSet.sense;
                c = '\u0000';
                bl = false;
                if (!bl2) break block42;
            }
            while (n != n2) {
                int n3;
                block50 : {
                    block45 : {
                        block46 : {
                            block47 : {
                                char c2;
                                block44 : {
                                    block48 : {
                                        block49 : {
                                            int n4;
                                            block43 : {
                                                n4 = 2;
                                                if (rEGlobalData.regexp.source[n] == '\\') break block43;
                                                char[] arrc = rEGlobalData.regexp.source;
                                                n3 = n + 1;
                                                c2 = arrc[n];
                                                break block44;
                                            }
                                            int n5 = n + 1;
                                            char[] arrc = rEGlobalData.regexp.source;
                                            n3 = n5 + 1;
                                            c2 = arrc[n5];
                                            if (c2 == 'D') break block45;
                                            if (c2 == 'S') break block46;
                                            if (c2 == 'W') break block47;
                                            if (c2 == 'f') break block48;
                                            if (c2 == 110) break block49;
                                            switch (c2) {
                                                default: {
                                                    switch (c2) {
                                                        default: {
                                                            switch (c2) {
                                                                default: {
                                                                    break;
                                                                }
                                                                case 119: {
                                                                    for (int i = -1 + rECharSet.length; i >= 0; --i) {
                                                                        if (!NativeRegExp.isWord((char)i)) continue;
                                                                        NativeRegExp.addCharacterToCharSet(rECharSet, (char)i);
                                                                    }
                                                                    break block50;
                                                                }
                                                                case 118: {
                                                                    c2 = '\u000b';
                                                                    break;
                                                                }
                                                                case 117: {
                                                                    n4 += 2;
                                                                }
                                                                case 120: {
                                                                    int n6 = 0;
                                                                    for (int i = 0; i < n4 && n3 < n2; ++i) {
                                                                        char[] arrc2 = rEGlobalData.regexp.source;
                                                                        int n7 = n3 + 1;
                                                                        int n8 = NativeRegExp.toASCIIHexDigit(arrc2[n3]);
                                                                        if (n8 < 0) {
                                                                            int n9 = n7 - (i + 1);
                                                                            n6 = 92;
                                                                            n3 = n9;
                                                                            break;
                                                                        }
                                                                        n6 = n8 | n6 << 4;
                                                                        n3 = n7;
                                                                    }
                                                                    c2 = (char)n6;
                                                                    break;
                                                                }
                                                                case 116: {
                                                                    c2 = '\t';
                                                                    break;
                                                                }
                                                                case 115: {
                                                                    for (int i = -1 + rECharSet.length; i >= 0; --i) {
                                                                        if (!NativeRegExp.isREWhiteSpace(i)) continue;
                                                                        NativeRegExp.addCharacterToCharSet(rECharSet, (char)i);
                                                                    }
                                                                    break block50;
                                                                }
                                                                case 114: {
                                                                    c2 = '\r';
                                                                    break;
                                                                }
                                                            }
                                                            break block44;
                                                        }
                                                        case 100: {
                                                            NativeRegExp.addCharacterRangeToCharSet(rECharSet, '0', '9');
                                                            break block50;
                                                        }
                                                        case 99: {
                                                            if (n3 < n2 && NativeRegExp.isControlLetter(rEGlobalData.regexp.source[n3])) {
                                                                char[] arrc3 = rEGlobalData.regexp.source;
                                                                int n10 = n3 + 1;
                                                                c2 = (char)(31 & arrc3[n3]);
                                                                n3 = n10;
                                                                break;
                                                            }
                                                            --n3;
                                                            c2 = '\\';
                                                            break;
                                                        }
                                                        case 98: {
                                                            c2 = '\b';
                                                            break;
                                                        }
                                                    }
                                                    break block44;
                                                }
                                                case 48: 
                                                case 49: 
                                                case 50: 
                                                case 51: 
                                                case 52: 
                                                case 53: 
                                                case 54: 
                                                case 55: {
                                                    int n11 = c2 - 48;
                                                    char c3 = rEGlobalData.regexp.source[n3];
                                                    if ('0' <= c3 && c3 <= '7') {
                                                        char c4;
                                                        n11 = n11 * 8 + (c3 - 48);
                                                        if ('0' <= (c4 = rEGlobalData.regexp.source[++n3]) && c4 <= '7') {
                                                            ++n3;
                                                            int n12 = n11 * 8 + (c4 - 48);
                                                            if (n12 <= 255) {
                                                                n11 = n12;
                                                            } else {
                                                                --n3;
                                                            }
                                                        }
                                                    }
                                                    c2 = (char)n11;
                                                    break;
                                                }
                                            }
                                            break block44;
                                        }
                                        c2 = '\n';
                                        break block44;
                                    }
                                    c2 = '\f';
                                }
                                if (bl) {
                                    if ((2 & rEGlobalData.regexp.flags) != 0) {
                                        if (c > c2) throw new AssertionError();
                                        char c5 = c;
                                        while (c5 <= c2) {
                                            NativeRegExp.addCharacterToCharSet(rECharSet, c5);
                                            char c6 = NativeRegExp.upcase(c5);
                                            char c7 = NativeRegExp.downcase(c5);
                                            if (c5 != c6) {
                                                NativeRegExp.addCharacterToCharSet(rECharSet, c6);
                                            }
                                            if (c5 != c7) {
                                                NativeRegExp.addCharacterToCharSet(rECharSet, c7);
                                            }
                                            if ((c5 = (char)((char)(c5 + 1))) != '\u0000') continue;
                                            break;
                                        }
                                    } else {
                                        NativeRegExp.addCharacterRangeToCharSet(rECharSet, c, c2);
                                    }
                                    n = n3;
                                    bl = false;
                                    continue;
                                }
                                if ((2 & rEGlobalData.regexp.flags) != 0) {
                                    NativeRegExp.addCharacterToCharSet(rECharSet, NativeRegExp.upcase(c2));
                                    NativeRegExp.addCharacterToCharSet(rECharSet, NativeRegExp.downcase(c2));
                                } else {
                                    NativeRegExp.addCharacterToCharSet(rECharSet, c2);
                                }
                                if (n3 < n2 - 1 && rEGlobalData.regexp.source[n3] == '-') {
                                    int n13 = n3 + 1;
                                    bl = true;
                                    c = c2;
                                    n = n13;
                                    continue;
                                }
                                break block50;
                            }
                            for (int i = -1 + rECharSet.length; i >= 0; --i) {
                                if (NativeRegExp.isWord((char)i)) continue;
                                NativeRegExp.addCharacterToCharSet(rECharSet, (char)i);
                            }
                            break block50;
                        }
                        for (int i = -1 + rECharSet.length; i >= 0; --i) {
                            if (NativeRegExp.isREWhiteSpace(i)) continue;
                            NativeRegExp.addCharacterToCharSet(rECharSet, (char)i);
                        }
                        break block50;
                    }
                    NativeRegExp.addCharacterRangeToCharSet(rECharSet, '\u0000', '/');
                    NativeRegExp.addCharacterRangeToCharSet(rECharSet, ':', (char)(-1 + rECharSet.length));
                }
                n = n3;
            }
            return;
        }
        AssertionError assertionError = new AssertionError();
        throw assertionError;
    }

    private static void pushBackTrackState(REGlobalData rEGlobalData, byte by, int n) {
        REBackTrackData rEBackTrackData;
        REProgState rEProgState = rEGlobalData.stateStackTop;
        rEGlobalData.backTrackStackTop = rEBackTrackData = new REBackTrackData(rEGlobalData, by, n, rEGlobalData.cp, rEProgState.continuationOp, rEProgState.continuationPc);
    }

    private static void pushBackTrackState(REGlobalData rEGlobalData, byte by, int n, int n2, int n3, int n4) {
        REBackTrackData rEBackTrackData;
        rEGlobalData.backTrackStackTop = rEBackTrackData = new REBackTrackData(rEGlobalData, by, n, n2, n3, n4);
    }

    private static void pushProgState(REGlobalData rEGlobalData, int n, int n2, int n3, REBackTrackData rEBackTrackData, int n4, int n5) {
        REProgState rEProgState;
        rEGlobalData.stateStackTop = rEProgState = new REProgState(rEGlobalData.stateStackTop, n, n2, n3, rEBackTrackData, n4, n5);
    }

    private static NativeRegExp realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeRegExp) {
            return (NativeRegExp)scriptable;
        }
        throw NativeRegExp.incompatibleCallError(idFunctionObject);
    }

    private static boolean reopIsSimple(int n) {
        return n >= 1 && n <= 23;
    }

    private static void reportError(String string, String string2) {
        throw ScriptRuntime.constructError("SyntaxError", ScriptRuntime.getMessage1(string, string2));
    }

    private static void reportWarning(Context context, String string, String string2) {
        if (context.hasFeature(11)) {
            Context.reportWarning(ScriptRuntime.getMessage1(string, string2));
        }
    }

    private static void resolveForwardJump(byte[] arrby, int n, int n2) {
        if (n <= n2) {
            NativeRegExp.addIndex(arrby, n, n2 - n);
            return;
        }
        throw Kit.codeBug();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private static int simpleMatch(REGlobalData var0, String var1_1, int var2_2, byte[] var3_3, int var4_4, int var5_5, boolean var6_6) {
        var7_7 = var0.cp;
        var8_8 = false;
        switch (var2_2) {
            default: {
                throw Kit.codeBug();
            }
            case 22: 
            case 23: {
                var52_9 = NativeRegExp.getIndex(var3_3, var4_4);
                var4_4 += 2;
                var53_10 = var0.cp;
                var9_11 = false;
                if (var53_10 == var5_5) break;
                var54_12 = NativeRegExp.classMatcher(var0, var0.regexp.classList[var52_9], var1_1.charAt(var0.cp));
                var9_11 = false;
                if (!var54_12) break;
                var0.cp = 1 + var0.cp;
                var9_11 = true;
                break;
            }
            case 19: {
                var47_13 = (char)NativeRegExp.getIndex(var3_3, var4_4);
                var4_4 += 2;
                var48_14 = var0.cp;
                var9_11 = false;
                if (var48_14 == var5_5) break;
                var49_15 = var1_1.charAt(var0.cp);
                if (var47_13 != var49_15) {
                    var50_16 = NativeRegExp.upcase(var47_13);
                    var51_17 = NativeRegExp.upcase(var49_15);
                    var9_11 = false;
                    if (var50_16 != var51_17) break;
                }
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 18: {
                var44_18 = (char)NativeRegExp.getIndex(var3_3, var4_4);
                var4_4 += 2;
                var45_19 = var0.cp;
                var9_11 = false;
                if (var45_19 == var5_5) break;
                var46_20 = var1_1.charAt(var0.cp);
                var9_11 = false;
                if (var46_20 != var44_18) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 17: {
                var39_21 = var4_4 + 1;
                var40_22 = (char)(255 & var3_3[var4_4]);
                if (var0.cp == var5_5) ** GOTO lbl59
                var41_23 = var1_1.charAt(var0.cp);
                if (var40_22 == var41_23) ** GOTO lbl55
                var42_24 = NativeRegExp.upcase(var40_22);
                var43_25 = NativeRegExp.upcase(var41_23);
                var9_11 = false;
                if (var42_24 != var43_25) ** GOTO lbl57
lbl55: // 2 sources:
                var9_11 = true;
                var0.cp = 1 + var0.cp;
lbl57: // 2 sources:
                var4_4 = var39_21;
                break;
lbl59: // 1 sources:
                var4_4 = var39_21;
                var9_11 = false;
                break;
            }
            case 16: {
                var36_26 = NativeRegExp.getIndex(var3_3, var4_4);
                var37_27 = var4_4 + 2;
                var38_28 = NativeRegExp.getIndex(var3_3, var37_27);
                var4_4 = var37_27 + 2;
                var9_11 = NativeRegExp.flatNIMatcher(var0, var36_26, var38_28, var1_1, var5_5);
                break;
            }
            case 15: {
                var34_29 = var4_4 + 1;
                var35_30 = (char)(255 & var3_3[var4_4]);
                if (var0.cp != var5_5 && var1_1.charAt(var0.cp) == var35_30) {
                    var9_11 = true;
                    var0.cp = 1 + var0.cp;
                    var4_4 = var34_29;
                    break;
                }
                var4_4 = var34_29;
                var9_11 = false;
                break;
            }
            case 14: {
                var31_31 = NativeRegExp.getIndex(var3_3, var4_4);
                var32_32 = var4_4 + 2;
                var33_33 = NativeRegExp.getIndex(var3_3, var32_32);
                var4_4 = var32_32 + 2;
                var9_11 = NativeRegExp.flatNMatcher(var0, var31_31, var33_33, var1_1, var5_5);
                break;
            }
            case 13: {
                var30_34 = NativeRegExp.getIndex(var3_3, var4_4);
                var4_4 += 2;
                var9_11 = NativeRegExp.backrefMatcher(var0, var30_34, var1_1, var5_5);
                break;
            }
            case 12: {
                var28_35 = var0.cp;
                var9_11 = false;
                if (var28_35 == var5_5) break;
                var29_36 = NativeRegExp.isREWhiteSpace(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (var29_36) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 11: {
                var26_37 = var0.cp;
                var9_11 = false;
                if (var26_37 == var5_5) break;
                var27_38 = NativeRegExp.isREWhiteSpace(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (!var27_38) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 10: {
                var24_39 = var0.cp;
                var9_11 = false;
                if (var24_39 == var5_5) break;
                var25_40 = NativeRegExp.isWord(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (var25_40) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 9: {
                var22_41 = var0.cp;
                var9_11 = false;
                if (var22_41 == var5_5) break;
                var23_42 = NativeRegExp.isWord(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (!var23_42) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 8: {
                var20_43 = var0.cp;
                var9_11 = false;
                if (var20_43 == var5_5) break;
                var21_44 = NativeRegExp.isDigit(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (var21_44) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 7: {
                var18_45 = var0.cp;
                var9_11 = false;
                if (var18_45 == var5_5) break;
                var19_46 = NativeRegExp.isDigit(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (!var19_46) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 6: {
                var16_47 = var0.cp;
                var9_11 = false;
                if (var16_47 == var5_5) break;
                var17_48 = NativeRegExp.isLineTerm(var1_1.charAt(var0.cp));
                var9_11 = false;
                if (var17_48) break;
                var9_11 = true;
                var0.cp = 1 + var0.cp;
                break;
            }
            case 5: {
                var15_49 = var0.cp == 0 || !NativeRegExp.isWord(var1_1.charAt(var0.cp - 1));
                if (var0.cp < var5_5 && NativeRegExp.isWord(var1_1.charAt(var0.cp))) {
                    var8_8 = true;
                }
                var9_11 = var15_49 ^ var8_8;
                break;
            }
            case 4: {
                var12_50 = var0.cp == 0 || !NativeRegExp.isWord(var1_1.charAt(var0.cp - 1));
                if (var0.cp >= var5_5) ** GOTO lbl174
                var14_51 = NativeRegExp.isWord(var1_1.charAt(var0.cp));
                var13_52 = false;
                if (var14_51) ** GOTO lbl175
lbl174: // 2 sources:
                var13_52 = true;
lbl175: // 2 sources:
                var9_11 = var12_50 ^ var13_52;
                break;
            }
            case 3: {
                if (var0.cp != var5_5) {
                    var11_53 = var0.multiline;
                    var9_11 = false;
                    if (!var11_53) break;
                    if (!NativeRegExp.isLineTerm(var1_1.charAt(var0.cp))) {
                        var9_11 = false;
                        break;
                    }
                }
                var9_11 = true;
                break;
            }
            case 2: {
                if (var0.cp != 0) {
                    var10_54 = var0.multiline;
                    var9_11 = false;
                    if (!var10_54) break;
                    if (!NativeRegExp.isLineTerm(var1_1.charAt(var0.cp - 1))) {
                        var9_11 = false;
                        break;
                    }
                }
                var9_11 = true;
                break;
            }
            case 1: {
                var9_11 = true;
            }
        }
        if (var9_11) {
            if (var6_6 != false) return var4_4;
            var0.cp = var7_7;
            return var4_4;
        }
        var0.cp = var7_7;
        return -1;
    }

    private static int toASCIIHexDigit(int n) {
        if (n < 48) {
            return -1;
        }
        if (n <= 57) {
            return n - 48;
        }
        int n2 = n | 32;
        if (97 <= n2 && n2 <= 102) {
            return 10 + (n2 - 97);
        }
        return -1;
    }

    private static char upcase(char c) {
        if (c < '') {
            if ('a' <= c && c <= 'z') {
                return (char)(c - 32);
            }
            return c;
        }
        char c2 = Character.toUpperCase((char)c);
        if (c2 < '') {
            return c;
        }
        return c2;
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        return this.execSub(context, scriptable, arrobject, 1);
    }

    Scriptable compile(Context context, Scriptable scriptable, Object[] arrobject) {
        if (arrobject.length > 0 && arrobject[0] instanceof NativeRegExp) {
            if (arrobject.length > 1 && arrobject[1] != Undefined.instance) {
                throw ScriptRuntime.typeError0("msg.bad.regexp.compile");
            }
            NativeRegExp nativeRegExp = (NativeRegExp)arrobject[0];
            this.re = nativeRegExp.re;
            this.lastIndex = nativeRegExp.lastIndex;
            return this;
        }
        String string = arrobject.length != 0 && !(arrobject[0] instanceof Undefined) ? NativeRegExp.escapeRegExp(arrobject[0]) : "";
        String string2 = arrobject.length > 1 && arrobject[1] != Undefined.instance ? ScriptRuntime.toString(arrobject[1]) : null;
        this.re = NativeRegExp.compileRE(context, string, string2, false);
        this.lastIndex = 0.0;
        return this;
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        return (Scriptable)this.execSub(context, scriptable, arrobject, 1);
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (!idFunctionObject.hasTag(REGEXP_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n = idFunctionObject.methodId();
        switch (n) {
            default: {
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            case 6: {
                return NativeRegExp.realThis(scriptable2, idFunctionObject).execSub(context, scriptable, arrobject, 2);
            }
            case 5: {
                Object object = NativeRegExp.realThis(scriptable2, idFunctionObject).execSub(context, scriptable, arrobject, 0);
                if (Boolean.TRUE.equals(object)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            case 4: {
                return NativeRegExp.realThis(scriptable2, idFunctionObject).execSub(context, scriptable, arrobject, 1);
            }
            case 2: 
            case 3: {
                return NativeRegExp.realThis(scriptable2, idFunctionObject).toString();
            }
            case 1: 
        }
        return NativeRegExp.realThis(scriptable2, idFunctionObject).compile(context, scriptable, arrobject);
    }

    Object executeRegExp(Context context, Scriptable scriptable, RegExpImpl regExpImpl, String string, int[] arrn, int n) {
        boolean bl;
        Scriptable scriptable2;
        Object object;
        int n2;
        NativeRegExp nativeRegExp = this;
        REGlobalData rEGlobalData = new REGlobalData();
        RECompiled rECompiled = nativeRegExp.re;
        int n3 = arrn[0];
        int n4 = string.length();
        int n5 = n3 > n4 ? n4 : n3;
        if (!NativeRegExp.matchRegExp(rEGlobalData, rECompiled, string, n5, n4, bl = regExpImpl.multiline)) {
            if (n != 2) {
                return null;
            }
            return Undefined.instance;
        }
        arrn[0] = n2 = rEGlobalData.cp;
        int n6 = n2 - (n5 + rEGlobalData.skipped);
        int n7 = n2 - n6;
        if (n == 0) {
            object = Boolean.TRUE;
            scriptable2 = null;
        } else {
            object = context.newArray(scriptable, 0);
            scriptable2 = (Scriptable)object;
            scriptable2.put(0, scriptable2, (Object)string.substring(n7, n7 + n6));
        }
        if (nativeRegExp.re.parenCount == 0) {
            regExpImpl.parens = null;
            regExpImpl.lastParen = new SubString();
        } else {
            SubString subString = null;
            regExpImpl.parens = new SubString[nativeRegExp.re.parenCount];
            for (int i = 0; i < nativeRegExp.re.parenCount; ++i) {
                int n8 = rEGlobalData.parensIndex(i);
                if (n8 != -1) {
                    regExpImpl.parens[i] = subString = new SubString(string, n8, rEGlobalData.parensLength(i));
                    if (n != 0) {
                        scriptable2.put(i + 1, scriptable2, (Object)subString.toString());
                    }
                } else if (n != 0) {
                    scriptable2.put(i + 1, scriptable2, Undefined.instance);
                }
                nativeRegExp = this;
            }
            regExpImpl.lastParen = subString;
        }
        if (n != 0) {
            scriptable2.put("index", scriptable2, (Object)(n5 + rEGlobalData.skipped));
            scriptable2.put("input", scriptable2, (Object)string);
        }
        if (regExpImpl.lastMatch == null) {
            regExpImpl.lastMatch = new SubString();
            regExpImpl.leftContext = new SubString();
            regExpImpl.rightContext = new SubString();
        }
        regExpImpl.lastMatch.str = string;
        regExpImpl.lastMatch.index = n7;
        regExpImpl.lastMatch.length = n6;
        regExpImpl.leftContext.str = string;
        if (context.getLanguageVersion() == 120) {
            regExpImpl.leftContext.index = n5;
            regExpImpl.leftContext.length = rEGlobalData.skipped;
        } else {
            regExpImpl.leftContext.index = 0;
            regExpImpl.leftContext.length = n5 + rEGlobalData.skipped;
        }
        regExpImpl.rightContext.str = string;
        regExpImpl.rightContext.index = n2;
        regExpImpl.rightContext.length = n4 - n2;
        return object;
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        int n;
        int n2;
        String string2;
        int n3 = string.length();
        if (n3 == 6) {
            char c = string.charAt(0);
            if (c == 'g') {
                string2 = "global";
                n = 3;
            } else {
                n = 0;
                string2 = null;
                if (c == 's') {
                    string2 = "source";
                    n = 2;
                }
            }
        } else if (n3 == 9) {
            char c = string.charAt(0);
            if (c == 'l') {
                string2 = "lastIndex";
                n = 1;
            } else {
                n = 0;
                string2 = null;
                if (c == 'm') {
                    string2 = "multiline";
                    n = 5;
                }
            }
        } else {
            n = 0;
            string2 = null;
            if (n3 == 10) {
                string2 = "ignoreCase";
                n = 4;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        if (n == 0) {
            return super.findInstanceIdInfo(string);
        }
        if (n != 1) {
            if (n != 2 && n != 3 && n != 4 && n != 5) {
                throw new IllegalStateException();
            }
            n2 = 7;
        } else {
            n2 = this.lastIndexAttr;
        }
        return NativeRegExp.instanceIdInfo(n2, n);
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 != 4) {
            if (n2 != 6) {
                if (n2 != 7) {
                    if (n2 != 8) {
                        n = 0;
                        string2 = null;
                    } else {
                        char c = string.charAt(3);
                        if (c == 'o') {
                            string2 = "toSource";
                            n = 3;
                        } else {
                            n = 0;
                            string2 = null;
                            if (c == 't') {
                                string2 = "toString";
                                n = 2;
                            }
                        }
                    }
                } else {
                    string2 = "compile";
                    n = 1;
                }
            } else {
                string2 = "prefix";
                n = 6;
            }
        } else {
            char c = string.charAt(0);
            if (c == 'e') {
                string2 = "exec";
                n = 4;
            } else {
                n = 0;
                string2 = null;
                if (c == 't') {
                    string2 = "test";
                    n = 5;
                }
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public String getClassName() {
        return "RegExp";
    }

    int getFlags() {
        return this.re.flags;
    }

    @Override
    protected String getInstanceIdName(int n) {
        if (n != 1) {
            if (n != 2) {
                if (n != 3) {
                    if (n != 4) {
                        if (n != 5) {
                            return super.getInstanceIdName(n);
                        }
                        return "multiline";
                    }
                    return "ignoreCase";
                }
                return "global";
            }
            return "source";
        }
        return "lastIndex";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        int n2 = 1;
        if (n != n2) {
            if (n != 2) {
                if (n != 3) {
                    if (n != 4) {
                        if (n != 5) {
                            return super.getInstanceIdValue(n);
                        }
                        if ((4 & this.re.flags) == 0) {
                            n2 = 0;
                        }
                        return ScriptRuntime.wrapBoolean((boolean)n2);
                    }
                    if ((2 & this.re.flags) == 0) {
                        n2 = 0;
                    }
                    return ScriptRuntime.wrapBoolean((boolean)n2);
                }
                if ((n2 & this.re.flags) == 0) {
                    n2 = 0;
                }
                return ScriptRuntime.wrapBoolean((boolean)n2);
            }
            return new String(this.re.source);
        }
        return this.lastIndex;
    }

    @Override
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override
    public String getTypeOf() {
        return "object";
    }

    @Override
    protected void initPrototypeId(int n) {
        String string;
        int n2;
        switch (n) {
            default: {
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            case 6: {
                n2 = 1;
                string = "prefix";
                break;
            }
            case 5: {
                n2 = 1;
                string = "test";
                break;
            }
            case 4: {
                n2 = 1;
                string = "exec";
                break;
            }
            case 3: {
                string = "toSource";
                n2 = 0;
                break;
            }
            case 2: {
                string = "toString";
                n2 = 0;
                break;
            }
            case 1: {
                n2 = 2;
                string = "compile";
            }
        }
        this.initPrototypeMethod(REGEXP_TAG, n, string, n2);
    }

    @Override
    protected void setInstanceIdAttributes(int n, int n2) {
        if (n != 1) {
            super.setInstanceIdAttributes(n, n2);
            return;
        }
        this.lastIndexAttr = n2;
    }

    @Override
    protected void setInstanceIdValue(int n, Object object) {
        if (n != 1) {
            if (n != 2 && n != 3 && n != 4 && n != 5) {
                super.setInstanceIdValue(n, object);
                return;
            }
            return;
        }
        this.lastIndex = object;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('/');
        if (this.re.source.length != 0) {
            stringBuilder.append(this.re.source);
        } else {
            stringBuilder.append("(?:)");
        }
        stringBuilder.append('/');
        if ((1 & this.re.flags) != 0) {
            stringBuilder.append('g');
        }
        if ((2 & this.re.flags) != 0) {
            stringBuilder.append('i');
        }
        if ((4 & this.re.flags) != 0) {
            stringBuilder.append('m');
        }
        return stringBuilder.toString();
    }
}

