/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  org.mozilla.javascript.regexp.NativeRegExp
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.RegExpProxy;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.regexp.GlobData;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.regexp.RECompiled;
import org.mozilla.javascript.regexp.SubString;

public class RegExpImpl
implements RegExpProxy {
    protected String input;
    protected SubString lastMatch;
    protected SubString lastParen;
    protected SubString leftContext;
    protected boolean multiline;
    protected SubString[] parens;
    protected SubString rightContext;

    private static NativeRegExp createRegExp(Context context, Scriptable scriptable, Object[] arrobject, int n, boolean bl) {
        Scriptable scriptable2 = ScriptableObject.getTopLevelScope(scriptable);
        if (arrobject.length != 0 && arrobject[0] != Undefined.instance) {
            String string2;
            if (arrobject[0] instanceof NativeRegExp) {
                return (NativeRegExp)arrobject[0];
            }
            String string3 = ScriptRuntime.toString(arrobject[0]);
            if (n < arrobject.length) {
                arrobject[0] = string3;
                string2 = ScriptRuntime.toString(arrobject[n]);
            } else {
                string2 = null;
            }
            return new NativeRegExp(scriptable2, NativeRegExp.compileRE((Context)context, (String)string3, (String)string2, (boolean)bl));
        }
        return new NativeRegExp(scriptable2, NativeRegExp.compileRE((Context)context, (String)"", (String)"", (boolean)false));
    }

    private static void do_replace(GlobData globData, Context context, RegExpImpl regExpImpl) {
        int n;
        StringBuilder stringBuilder = globData.charBuf;
        String string2 = globData.repstr;
        int n2 = globData.dollar;
        int n3 = 0;
        if (n2 != -1) {
            int n4;
            int[] arrn = new int[1];
            do {
                n2 - n3;
                stringBuilder.append(string2.substring(n3, n2));
                n3 = n2;
                SubString subString = RegExpImpl.interpretDollar(context, regExpImpl, string2, n2, arrn);
                if (subString != null) {
                    int n5 = subString.length;
                    if (n5 > 0) {
                        stringBuilder.append((CharSequence)subString.str, subString.index, n5 + subString.index);
                    }
                    n3 += arrn[0];
                    n4 = n2 + arrn[0];
                    continue;
                }
                n4 = n2 + 1;
            } while ((n2 = string2.indexOf(36, n4)) >= 0);
        }
        if ((n = string2.length()) > n3) {
            stringBuilder.append(string2.substring(n3, n));
        }
    }

    private static int find_split(Context context, Scriptable scriptable, String string2, String string3, int n, RegExpProxy regExpProxy, Scriptable scriptable2, int[] arrn, int[] arrn2, boolean[] arrbl, String[][] arrstring) {
        int n2;
        int n3 = string2.length();
        if (n == 120 && scriptable2 == null && string3.length() == 1 && string3.charAt(0) == ' ') {
            int n4;
            if (n2 == 0) {
                for (n2 = arrn[0]; n2 < n3 && Character.isWhitespace((char)string2.charAt(n2)); ++n2) {
                }
                arrn[0] = n2;
            }
            if (n2 == n3) {
                return -1;
            }
            while (n2 < n3 && !Character.isWhitespace((char)string2.charAt(n2))) {
                ++n2;
            }
            for (n4 = n2; n4 < n3 && Character.isWhitespace((char)string2.charAt(n4)); ++n4) {
            }
            arrn2[0] = n4 - n2;
            return n2;
        }
        if (n2 > n3) {
            return -1;
        }
        if (scriptable2 != null) {
            return regExpProxy.find_split(context, scriptable, string2, string3, scriptable2, arrn, arrn2, arrbl, arrstring);
        }
        if (n != 0 && n < 130 && n3 == 0) {
            return -1;
        }
        if (string3.length() == 0) {
            if (n == 120) {
                if (n2 == n3) {
                    arrn2[0] = 1;
                    return n2;
                }
                return n2 + 1;
            }
            if (n2 == n3) {
                return -1;
            }
            return n2 + 1;
        }
        if (arrn[0] >= n3) {
            return n3;
        }
        int n5 = string2.indexOf(string3, arrn[0]);
        if (n5 != -1) {
            return n5;
        }
        return n3;
    }

    private static SubString interpretDollar(Context context, RegExpImpl regExpImpl, String string2, int n, int[] arrn) {
        int n2;
        if (string2.charAt(n) != '$') {
            Kit.codeBug();
        }
        if ((n2 = context.getLanguageVersion()) != 0 && n2 <= 140 && n > 0 && string2.charAt(n - 1) == '\\') {
            return null;
        }
        int n3 = string2.length();
        if (n + 1 >= n3) {
            return null;
        }
        char c = string2.charAt(n + 1);
        if (NativeRegExp.isDigit((char)c)) {
            int n4;
            int n5;
            if (n2 != 0 && n2 <= 140) {
                int n6;
                char c2;
                if (c == '0') {
                    return null;
                }
                n5 = 0;
                n4 = n;
                while (++n4 < n3 && NativeRegExp.isDigit((char)(c2 = string2.charAt(n4))) && (n6 = n5 * 10 + (c2 - 48)) >= n5) {
                    n5 = n6;
                }
            } else {
                char c3;
                int n7;
                int n8 = c - 48;
                SubString[] arrsubString = regExpImpl.parens;
                int n9 = arrsubString == null ? 0 : arrsubString.length;
                if (n8 > n9) {
                    return null;
                }
                int n10 = n + 2;
                if (n + 2 < n3 && NativeRegExp.isDigit((char)(c3 = string2.charAt(n + 2))) && (n7 = n8 * 10 + (c3 - 48)) <= n9) {
                    ++n10;
                    n8 = n7;
                }
                if (n8 == 0) {
                    return null;
                }
                n5 = n8;
                n4 = n10;
            }
            int n11 = n5 - 1;
            arrn[0] = n4 - n;
            return regExpImpl.getParenSubString(n11);
        }
        arrn[0] = 2;
        if (c != '$') {
            if (c != '+') {
                if (c != '`') {
                    if (c != '&') {
                        if (c != '\'') {
                            return null;
                        }
                        return regExpImpl.rightContext;
                    }
                    return regExpImpl.lastMatch;
                }
                if (n2 == 120) {
                    regExpImpl.leftContext.index = 0;
                    regExpImpl.leftContext.length = regExpImpl.lastMatch.index;
                }
                return regExpImpl.leftContext;
            }
            return regExpImpl.lastParen;
        }
        return new SubString("$");
    }

    private static Object matchOrReplace(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject, RegExpImpl regExpImpl, GlobData globData, NativeRegExp nativeRegExp) {
        int[] arrn;
        String string2;
        block9 : {
            string2 = globData.str;
            boolean bl = (1 & nativeRegExp.getFlags()) != 0;
            globData.global = bl;
            arrn = new int[]{0};
            if (globData.mode == 3) {
                Object object = nativeRegExp.executeRegExp(context, scriptable, regExpImpl, string2, arrn, 0);
                if (object != null && object.equals((Object)Boolean.TRUE)) {
                    return regExpImpl.leftContext.length;
                }
                return -1;
            }
            if (!globData.global) break block9;
            nativeRegExp.lastIndex = 0.0;
            Object object = null;
            int n = 0;
            while (arrn[0] <= string2.length()) {
                Object object2;
                block10 : {
                    block11 : {
                        object2 = nativeRegExp.executeRegExp(context, scriptable, regExpImpl, string2, arrn, 0);
                        if (object2 == null || !object2.equals((Object)Boolean.TRUE)) break block10;
                        if (globData.mode == 1) {
                            RegExpImpl.match_glob(globData, context, scriptable, n, regExpImpl);
                        } else {
                            if (globData.mode != 2) {
                                Kit.codeBug();
                            }
                            SubString subString = regExpImpl.lastMatch;
                            int n2 = globData.leftIndex;
                            int n3 = subString.index - n2;
                            globData.leftIndex = subString.index + subString.length;
                            RegExpImpl.replace_glob(globData, context, scriptable, regExpImpl, n2, n3);
                        }
                        if (regExpImpl.lastMatch.length != 0) break block11;
                        if (arrn[0] == string2.length()) break block10;
                        arrn[0] = 1 + arrn[0];
                    }
                    ++n;
                    object = object2;
                    continue;
                }
                return object2;
            }
            return object;
        }
        int n = globData.mode == 2 ? 0 : 1;
        return nativeRegExp.executeRegExp(context, scriptable, regExpImpl, string2, arrn, n);
    }

    private static void match_glob(GlobData globData, Context context, Scriptable scriptable, int n, RegExpImpl regExpImpl) {
        if (globData.arrayobj == null) {
            globData.arrayobj = context.newArray(scriptable, 0);
        }
        String string2 = regExpImpl.lastMatch.toString();
        globData.arrayobj.put(n, globData.arrayobj, (Object)string2);
    }

    private static void replace_glob(GlobData globData, Context context, Scriptable scriptable, RegExpImpl regExpImpl, int n, int n2) {
        int n3;
        String string2;
        block12 : {
            block11 : {
                if (globData.lambda == null) break block11;
                SubString[] arrsubString = regExpImpl.parens;
                int n4 = arrsubString == null ? 0 : arrsubString.length;
                Object[] arrobject = new Object[n4 + 3];
                arrobject[0] = regExpImpl.lastMatch.toString();
                for (int i = 0; i < n4; ++i) {
                    SubString subString = arrsubString[i];
                    arrobject[i + 1] = subString != null ? subString.toString() : Undefined.instance;
                }
                arrobject[n4 + 1] = regExpImpl.leftContext.length;
                arrobject[n4 + 2] = globData.str;
                if (regExpImpl != ScriptRuntime.getRegExpProxy(context)) {
                    Kit.codeBug();
                }
                RegExpImpl regExpImpl2 = new RegExpImpl();
                regExpImpl2.multiline = regExpImpl.multiline;
                regExpImpl2.input = regExpImpl.input;
                ScriptRuntime.setRegExpProxy(context, regExpImpl2);
                try {
                    String string3;
                    Scriptable scriptable2 = ScriptableObject.getTopLevelScope(scriptable);
                    string2 = string3 = ScriptRuntime.toString(globData.lambda.call(context, scriptable2, scriptable2, arrobject));
                }
                catch (Throwable throwable) {
                    ScriptRuntime.setRegExpProxy(context, regExpImpl);
                    throw throwable;
                }
                ScriptRuntime.setRegExpProxy(context, regExpImpl);
                n3 = string2.length();
                break block12;
            }
            n3 = globData.repstr.length();
            int n5 = globData.dollar;
            string2 = null;
            if (n5 >= 0) {
                int[] arrn = new int[1];
                int n6 = globData.dollar;
                do {
                    SubString subString;
                    int n7;
                    if ((subString = RegExpImpl.interpretDollar(context, regExpImpl, globData.repstr, n6, arrn)) != null) {
                        n3 += subString.length - arrn[0];
                        n7 = n6 + arrn[0];
                    } else {
                        n7 = n6 + 1;
                    }
                    n6 = globData.repstr.indexOf(36, n7);
                    string2 = null;
                } while (n6 >= 0);
            }
        }
        int n8 = n2 + n3 + regExpImpl.rightContext.length;
        StringBuilder stringBuilder = globData.charBuf;
        if (stringBuilder == null) {
            globData.charBuf = stringBuilder = new StringBuilder(n8);
        } else {
            stringBuilder.ensureCapacity(n8 + globData.charBuf.length());
        }
        stringBuilder.append((CharSequence)regExpImpl.leftContext.str, n, n + n2);
        if (globData.lambda != null) {
            stringBuilder.append(string2);
            return;
        }
        RegExpImpl.do_replace(globData, context, regExpImpl);
    }

    @Override
    public Object action(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject, int n) {
        GlobData globData = new GlobData();
        globData.mode = n;
        globData.str = ScriptRuntime.toString(scriptable2);
        if (n != 1) {
            String string2;
            Function function;
            Object object;
            String string3;
            NativeRegExp nativeRegExp;
            if (n != 2) {
                if (n == 3) {
                    return RegExpImpl.matchOrReplace(context, scriptable, scriptable2, arrobject, this, globData, RegExpImpl.createRegExp(context, scriptable, arrobject, 1, false));
                }
                throw Kit.codeBug();
            }
            boolean bl = arrobject.length > 0 && arrobject[0] instanceof NativeRegExp || arrobject.length > 2;
            boolean bl2 = bl;
            if (bl2) {
                nativeRegExp = RegExpImpl.createRegExp(context, scriptable, arrobject, 2, true);
                string3 = null;
            } else {
                Object object2 = arrobject.length < 1 ? Undefined.instance : arrobject[0];
                String string4 = ScriptRuntime.toString(object2);
                nativeRegExp = null;
                string3 = string4;
            }
            Object object3 = arrobject.length < 2 ? Undefined.instance : arrobject[1];
            Object object4 = object3;
            if (object4 instanceof Function) {
                function = (Function)object4;
                string2 = null;
            } else {
                string2 = ScriptRuntime.toString(object4);
                function = null;
            }
            globData.lambda = function;
            globData.repstr = string2;
            int n2 = string2 == null ? -1 : string2.indexOf(36);
            globData.dollar = n2;
            globData.charBuf = null;
            globData.leftIndex = 0;
            if (bl2) {
                object = RegExpImpl.matchOrReplace(context, scriptable, scriptable2, arrobject, this, globData, nativeRegExp);
            } else {
                String string5 = globData.str;
                int n3 = string5.indexOf(string3);
                if (n3 >= 0) {
                    int n4 = string3.length();
                    this.lastParen = null;
                    this.leftContext = new SubString(string5, 0, n3);
                    this.lastMatch = new SubString(string5, n3, n4);
                    this.rightContext = new SubString(string5, n3 + n4, string5.length() - n3 - n4);
                    object = Boolean.TRUE;
                } else {
                    object = Boolean.FALSE;
                }
            }
            if (globData.charBuf == null) {
                if (!globData.global && object != null && object.equals((Object)Boolean.TRUE)) {
                    SubString subString = this.leftContext;
                    RegExpImpl.replace_glob(globData, context, scriptable, this, subString.index, subString.length);
                } else {
                    return globData.str;
                }
            }
            SubString subString = this.rightContext;
            globData.charBuf.append((CharSequence)subString.str, subString.index, subString.index + subString.length);
            return globData.charBuf.toString();
        }
        Object object = RegExpImpl.matchOrReplace(context, scriptable, scriptable2, arrobject, this, globData, RegExpImpl.createRegExp(context, scriptable, arrobject, 1, false));
        if (globData.arrayobj == null) {
            return object;
        }
        return globData.arrayobj;
    }

    @Override
    public Object compileRegExp(Context context, String string2, String string3) {
        return NativeRegExp.compileRE((Context)context, (String)string2, (String)string3, (boolean)false);
    }

    @Override
    public int find_split(Context context, Scriptable scriptable, String string2, String string3, Scriptable scriptable2, int[] arrn, int[] arrn2, boolean[] arrbl, String[][] arrstring) {
        int n;
        block6 : {
            int n2;
            int n3 = arrn[0];
            int n4 = string2.length();
            int n5 = context.getLanguageVersion();
            NativeRegExp nativeRegExp = (NativeRegExp)scriptable2;
            int n6 = n3;
            do {
                int n7 = arrn[0];
                arrn[0] = n6;
                if (nativeRegExp.executeRegExp(context, scriptable, this, string2, arrn, 0) != Boolean.TRUE) {
                    arrn[0] = n7;
                    arrn2[0] = 1;
                    arrbl[0] = false;
                    return n4;
                }
                n2 = arrn[0];
                arrn[0] = n7;
                arrbl[0] = true;
                arrn2[0] = this.lastMatch.length;
                if (arrn2[0] != 0 || n2 != arrn[0]) break;
                if (n2 == n4) {
                    if (n5 == 120) {
                        arrn2[0] = 1;
                        n = n2;
                    } else {
                        n = -1;
                    }
                    break block6;
                }
                n6 = n2 + 1;
            } while (true);
            n = n2 - arrn2[0];
        }
        SubString[] arrsubString = this.parens;
        int n8 = arrsubString == null ? 0 : arrsubString.length;
        arrstring[0] = new String[n8];
        for (int i = 0; i < n8; ++i) {
            SubString subString = this.getParenSubString(i);
            arrstring[0][i] = subString.toString();
        }
        return n;
    }

    SubString getParenSubString(int n) {
        SubString subString;
        SubString[] arrsubString = this.parens;
        if (arrsubString != null && n < arrsubString.length && (subString = arrsubString[n]) != null) {
            return subString;
        }
        return new SubString();
    }

    @Override
    public boolean isRegExp(Scriptable scriptable) {
        return scriptable instanceof NativeRegExp;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Object js_split(Context context, Scriptable scriptable, String string2, Object[] arrobject) {
        String string3;
        Scriptable scriptable2;
        boolean bl;
        Scriptable scriptable3;
        RegExpProxy regExpProxy;
        int[] arrn;
        long l;
        block15 : {
            Scriptable scriptable4;
            block14 : {
                block12 : {
                    block13 : {
                        long l2;
                        scriptable2 = context.newArray(scriptable, 0);
                        boolean bl2 = arrobject.length > 1 && arrobject[1] != Undefined.instance;
                        bl = bl2;
                        l = bl ? ((l2 = ScriptRuntime.toUint32(arrobject[1])) > (long)string2.length() ? (long)(1 + string2.length()) : l2) : 0L;
                        if (arrobject.length < 1) break block12;
                        if (arrobject[0] != Undefined.instance) break block13;
                        scriptable4 = scriptable2;
                        break block14;
                    }
                    arrn = new int[1];
                    if (arrobject[0] instanceof Scriptable) {
                        Scriptable scriptable5;
                        RegExpProxy regExpProxy2 = ScriptRuntime.getRegExpProxy(context);
                        if (regExpProxy2 != null && regExpProxy2.isRegExp(scriptable5 = (Scriptable)arrobject[0])) {
                            scriptable3 = scriptable5;
                            regExpProxy = regExpProxy2;
                        } else {
                            regExpProxy = regExpProxy2;
                            scriptable3 = null;
                        }
                    } else {
                        scriptable3 = null;
                        regExpProxy = null;
                    }
                    if (scriptable3 == null) {
                        String string4 = ScriptRuntime.toString(arrobject[0]);
                        arrn[0] = string4.length();
                        string3 = string4;
                    } else {
                        string3 = null;
                    }
                    break block15;
                }
                scriptable4 = scriptable2;
            }
            scriptable4.put(0, scriptable4, (Object)string2);
            return scriptable4;
        }
        int[] arrn2 = new int[]{0};
        boolean[] arrbl = new boolean[]{false};
        String[][] arrstring = new String[][]{null};
        int n = context.getLanguageVersion();
        int n2 = 0;
        do {
            String string5 = string3;
            int n3 = n;
            RegExpProxy regExpProxy3 = regExpProxy;
            Scriptable scriptable6 = scriptable3;
            int n4 = n2;
            int n5 = n;
            int[] arrn3 = arrn;
            int[] arrn4 = arrn;
            Scriptable scriptable7 = scriptable2;
            int n6 = RegExpImpl.find_split(context, scriptable, string2, string5, n3, regExpProxy3, scriptable6, arrn2, arrn3, arrbl, arrstring);
            if (n6 < 0) return scriptable7;
            if (bl) {
                if ((long)n4 >= l) return scriptable7;
            }
            if (n6 > string2.length()) {
                return scriptable7;
            }
            String string6 = string2.length() == 0 ? string2 : string2.substring(arrn2[0], n6);
            scriptable7.put(n4, scriptable7, (Object)string6);
            n2 = n4 + 1;
            if (scriptable3 != null && arrbl[0]) {
                int n7 = arrstring[0].length;
                for (int i = 0; !(i >= n7 || bl && (long)n2 >= l); ++n2, ++i) {
                    scriptable7.put(n2, scriptable7, (Object)arrstring[0][i]);
                }
                arrbl[0] = false;
            }
            arrn2[0] = n6 + arrn4[0];
            if (n5 < 130 && n5 != 0 && !bl && arrn2[0] == string2.length()) {
                return scriptable7;
            }
            n = n5;
            scriptable2 = scriptable7;
            arrn = arrn4;
        } while (true);
    }

    @Override
    public Scriptable wrapRegExp(Context context, Scriptable scriptable, Object object) {
        return new NativeRegExp(scriptable, (RECompiled)object);
    }
}

