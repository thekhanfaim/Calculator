/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.regexp.RegExpImpl;
import org.mozilla.javascript.regexp.SubString;

class NativeRegExpCtor
extends BaseFunction {
    private static final int DOLLAR_ID_BASE = 12;
    private static final int Id_AMPERSAND = 6;
    private static final int Id_BACK_QUOTE = 10;
    private static final int Id_DOLLAR_1 = 13;
    private static final int Id_DOLLAR_2 = 14;
    private static final int Id_DOLLAR_3 = 15;
    private static final int Id_DOLLAR_4 = 16;
    private static final int Id_DOLLAR_5 = 17;
    private static final int Id_DOLLAR_6 = 18;
    private static final int Id_DOLLAR_7 = 19;
    private static final int Id_DOLLAR_8 = 20;
    private static final int Id_DOLLAR_9 = 21;
    private static final int Id_PLUS = 8;
    private static final int Id_QUOTE = 12;
    private static final int Id_STAR = 2;
    private static final int Id_UNDERSCORE = 4;
    private static final int Id_input = 3;
    private static final int Id_lastMatch = 5;
    private static final int Id_lastParen = 7;
    private static final int Id_leftContext = 9;
    private static final int Id_multiline = 1;
    private static final int Id_rightContext = 11;
    private static final int MAX_INSTANCE_ID = 21;
    private static final long serialVersionUID = -5733330028285400526L;
    private int inputAttr = 4;
    private int multilineAttr = 4;
    private int starAttr = 4;
    private int underscoreAttr = 4;

    NativeRegExpCtor() {
    }

    private static RegExpImpl getImpl() {
        return (RegExpImpl)ScriptRuntime.getRegExpProxy(Context.getCurrentContext());
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        if (arrobject.length > 0 && arrobject[0] instanceof NativeRegExp && (arrobject.length == 1 || arrobject[1] == Undefined.instance)) {
            return arrobject[0];
        }
        return this.construct(context, scriptable, arrobject);
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        NativeRegExp nativeRegExp = new NativeRegExp();
        nativeRegExp.compile(context, scriptable, arrobject);
        ScriptRuntime.setBuiltinProtoAndParent(nativeRegExp, scriptable, TopLevel.Builtins.RegExp);
        return nativeRegExp;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    protected int findInstanceIdInfo(String var1_1) {
        block43 : {
            block37 : {
                block38 : {
                    block39 : {
                        block40 : {
                            block41 : {
                                block42 : {
                                    block32 : {
                                        block33 : {
                                            block36 : {
                                                block35 : {
                                                    block34 : {
                                                        var2_2 = var1_1.length();
                                                        if (var2_2 == 2) break block32;
                                                        if (var2_2 == 5) break block33;
                                                        if (var2_2 == 9) break block34;
                                                        if (var2_2 != 11) {
                                                            if (var2_2 != 12) {
                                                                var5_3 = 0;
                                                                var6_4 = null;
                                                            } else {
                                                                var6_4 = "rightContext";
                                                                var5_3 = 11;
                                                            }
                                                        } else {
                                                            var6_4 = "leftContext";
                                                            var5_3 = 9;
                                                        }
                                                        ** GOTO lbl-1000
                                                    }
                                                    var22_5 = var1_1.charAt(4);
                                                    if (var22_5 != 'M') break block35;
                                                    var6_4 = "lastMatch";
                                                    var5_3 = 5;
                                                    ** GOTO lbl-1000
                                                }
                                                if (var22_5 != 'P') break block36;
                                                var6_4 = "lastParen";
                                                var5_3 = 7;
                                                ** GOTO lbl-1000
                                            }
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var22_5 != 'i') ** GOTO lbl-1000
                                            var6_4 = "multiline";
                                            var5_3 = 1;
                                            ** GOTO lbl-1000
                                        }
                                        var6_4 = "input";
                                        var5_3 = 3;
                                        ** GOTO lbl-1000
                                    }
                                    var3_6 = var1_1.charAt(1);
                                    if (var3_6 == '&') break block37;
                                    if (var3_6 == '\'') break block38;
                                    if (var3_6 == '*') break block39;
                                    if (var3_6 == '+') break block40;
                                    if (var3_6 == '_') break block41;
                                    if (var3_6 == '`') break block42;
                                    switch (var3_6) {
                                        default: {
                                            var5_3 = 0;
                                            var6_4 = null;
                                            ** GOTO lbl-1000
                                        }
                                        case '9': {
                                            var21_7 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var21_7 == '$') {
                                                var5_3 = 21;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '8': {
                                            var20_8 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var20_8 == '$') {
                                                var5_3 = 20;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '7': {
                                            var19_9 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var19_9 == '$') {
                                                var5_3 = 19;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '6': {
                                            var18_10 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var18_10 == '$') {
                                                var5_3 = 18;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '5': {
                                            var17_11 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var17_11 == '$') {
                                                var5_3 = 17;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '4': {
                                            var16_12 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var16_12 == '$') {
                                                var5_3 = 16;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '3': {
                                            var15_13 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var15_13 == '$') {
                                                var5_3 = 15;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '2': {
                                            var14_14 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var14_14 == '$') {
                                                var5_3 = 14;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
                                        }
                                        case '1': {
                                            var13_15 = var1_1.charAt(0);
                                            var5_3 = 0;
                                            var6_4 = null;
                                            if (var13_15 == '$') {
                                                var5_3 = 13;
                                                ** break;
                                            }
                                            ** GOTO lbl-1000
lbl123: // 9 sources:
                                            break;
                                        }
                                    }
                                    break block43;
                                }
                                var12_16 = var1_1.charAt(0);
                                var5_3 = 0;
                                var6_4 = null;
                                if (var12_16 != '$') ** GOTO lbl-1000
                                var5_3 = 10;
                                break block43;
                            }
                            var11_17 = var1_1.charAt(0);
                            var5_3 = 0;
                            var6_4 = null;
                            if (var11_17 != '$') ** GOTO lbl-1000
                            var5_3 = 4;
                            break block43;
                        }
                        var10_18 = var1_1.charAt(0);
                        var5_3 = 0;
                        var6_4 = null;
                        if (var10_18 != '$') ** GOTO lbl-1000
                        var5_3 = 8;
                        break block43;
                    }
                    var9_19 = var1_1.charAt(0);
                    var5_3 = 0;
                    var6_4 = null;
                    if (var9_19 != '$') ** GOTO lbl-1000
                    var5_3 = 2;
                    break block43;
                }
                var8_20 = var1_1.charAt(0);
                var5_3 = 0;
                var6_4 = null;
                if (var8_20 != '$') ** GOTO lbl-1000
                var5_3 = 12;
                break block43;
            }
            var4_21 = var1_1.charAt(0);
            var5_3 = 0;
            var6_4 = null;
            if (var4_21 == '$') {
                var5_3 = 6;
            } else if (var6_4 != null && var6_4 != var1_1 && !var6_4.equals((Object)var1_1)) {
                var5_3 = 0;
            }
        }
        if (var5_3 == 0) {
            return super.findInstanceIdInfo(var1_1);
        }
        if (var5_3 == 1) {
            var7_22 = this.multilineAttr;
            return NativeRegExpCtor.instanceIdInfo(var7_22, var5_3 + super.getMaxInstanceId());
        }
        if (var5_3 == 2) {
            var7_22 = this.starAttr;
            return NativeRegExpCtor.instanceIdInfo(var7_22, var5_3 + super.getMaxInstanceId());
        }
        if (var5_3 == 3) {
            var7_22 = this.inputAttr;
            return NativeRegExpCtor.instanceIdInfo(var7_22, var5_3 + super.getMaxInstanceId());
        }
        if (var5_3 != 4) {
            var7_22 = 5;
            return NativeRegExpCtor.instanceIdInfo(var7_22, var5_3 + super.getMaxInstanceId());
        }
        var7_22 = this.underscoreAttr;
        return NativeRegExpCtor.instanceIdInfo(var7_22, var5_3 + super.getMaxInstanceId());
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public String getFunctionName() {
        return "RegExp";
    }

    @Override
    protected String getInstanceIdName(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (1 <= n2 && n2 <= 21) {
            switch (n2) {
                default: {
                    int n3 = n2 - 12 - 1;
                    char[] arrc = new char[]{'$', (char)(n3 + 49)};
                    return new String(arrc);
                }
                case 12: {
                    return "$'";
                }
                case 11: {
                    return "rightContext";
                }
                case 10: {
                    return "$`";
                }
                case 9: {
                    return "leftContext";
                }
                case 8: {
                    return "$+";
                }
                case 7: {
                    return "lastParen";
                }
                case 6: {
                    return "$&";
                }
                case 5: {
                    return "lastMatch";
                }
                case 4: {
                    return "$_";
                }
                case 3: {
                    return "input";
                }
                case 2: {
                    return "$*";
                }
                case 1: 
            }
            return "multiline";
        }
        return super.getInstanceIdName(n);
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (1 <= n2 && n2 <= 21) {
            Object object;
            RegExpImpl regExpImpl = NativeRegExpCtor.getImpl();
            switch (n2) {
                default: {
                    object = regExpImpl.getParenSubString(n2 - 12 - 1);
                    break;
                }
                case 11: 
                case 12: {
                    object = regExpImpl.rightContext;
                    break;
                }
                case 9: 
                case 10: {
                    object = regExpImpl.leftContext;
                    break;
                }
                case 7: 
                case 8: {
                    object = regExpImpl.lastParen;
                    break;
                }
                case 5: 
                case 6: {
                    object = regExpImpl.lastMatch;
                    break;
                }
                case 3: 
                case 4: {
                    object = regExpImpl.input;
                    break;
                }
                case 1: 
                case 2: {
                    return ScriptRuntime.wrapBoolean(regExpImpl.multiline);
                }
            }
            if (object == null) {
                return "";
            }
            return object.toString();
        }
        return super.getInstanceIdValue(n);
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    protected int getMaxInstanceId() {
        return 21 + super.getMaxInstanceId();
    }

    @Override
    protected void setInstanceIdAttributes(int n, int n2) {
        int n3 = n - super.getMaxInstanceId();
        switch (n3) {
            default: {
                int n4 = -1 + (n3 - 12);
                if (n4 < 0 || n4 > 8) break;
                return;
            }
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                return;
            }
            case 4: {
                this.underscoreAttr = n2;
                return;
            }
            case 3: {
                this.inputAttr = n2;
                return;
            }
            case 2: {
                this.starAttr = n2;
                return;
            }
            case 1: {
                this.multilineAttr = n2;
                return;
            }
        }
        super.setInstanceIdAttributes(n, n2);
    }

    @Override
    protected void setInstanceIdValue(int n, Object object) {
        int n2 = n - super.getMaxInstanceId();
        switch (n2) {
            default: {
                int n3 = -1 + (n2 - 12);
                if (n3 < 0 || n3 > 8) break;
                return;
            }
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                return;
            }
            case 3: 
            case 4: {
                NativeRegExpCtor.getImpl().input = ScriptRuntime.toString(object);
                return;
            }
            case 1: 
            case 2: {
                NativeRegExpCtor.getImpl().multiline = ScriptRuntime.toBoolean(object);
                return;
            }
        }
        super.setInstanceIdValue(n, object);
    }
}

