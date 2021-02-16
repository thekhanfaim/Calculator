/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.CharSequence
 *  java.lang.Class
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.System
 *  org.mozilla.javascript.ArrowFunction
 *  org.mozilla.javascript.NativeFunction
 */
package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.ArrowFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.NativeGenerator;
import org.mozilla.javascript.NativeIterator;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.optimizer.-$$Lambda$OptRuntime$nkcl7ufBnCixh_eNHP8jiz3N5n8;

public final class OptRuntime
extends ScriptRuntime {
    public static final Double minusOneObj;
    public static final Double oneObj;
    public static final Double zeroObj;

    static {
        zeroObj = new Double(0.0);
        oneObj = new Double(1.0);
        minusOneObj = new Double(-1.0);
    }

    public static Object add(double d, Object object) {
        if (object instanceof Scriptable) {
            object = ((Scriptable)object).getDefaultValue(null);
        }
        if (!(object instanceof CharSequence)) {
            return OptRuntime.wrapDouble(d + OptRuntime.toNumber(object));
        }
        return new ConsString(OptRuntime.toString(d), (CharSequence)object);
    }

    public static Object add(Object object, double d) {
        if (object instanceof Scriptable) {
            object = ((Scriptable)object).getDefaultValue(null);
        }
        if (!(object instanceof CharSequence)) {
            return OptRuntime.wrapDouble(d + OptRuntime.toNumber(object));
        }
        return new ConsString((CharSequence)object, OptRuntime.toString(d));
    }

    public static Function bindThis(NativeFunction nativeFunction, Context context, Scriptable scriptable, Scriptable scriptable2) {
        return new ArrowFunction(context, scriptable, (Callable)nativeFunction, scriptable2);
    }

    public static Object call0(Callable callable, Scriptable scriptable, Context context, Scriptable scriptable2) {
        return callable.call(context, scriptable2, scriptable, ScriptRuntime.emptyArgs);
    }

    public static Object call1(Callable callable, Scriptable scriptable, Object object, Context context, Scriptable scriptable2) {
        return callable.call(context, scriptable2, scriptable, new Object[]{object});
    }

    public static Object call2(Callable callable, Scriptable scriptable, Object object, Object object2, Context context, Scriptable scriptable2) {
        return callable.call(context, scriptable2, scriptable, new Object[]{object, object2});
    }

    public static Object callN(Callable callable, Scriptable scriptable, Object[] arrobject, Context context, Scriptable scriptable2) {
        return callable.call(context, scriptable2, scriptable, arrobject);
    }

    public static Object callName(Object[] arrobject, String string2, Context context, Scriptable scriptable) {
        return OptRuntime.getNameFunctionAndThis(string2, context, scriptable).call(context, scriptable, OptRuntime.lastStoredScriptable(context), arrobject);
    }

    public static Object callName0(String string2, Context context, Scriptable scriptable) {
        return OptRuntime.getNameFunctionAndThis(string2, context, scriptable).call(context, scriptable, OptRuntime.lastStoredScriptable(context), ScriptRuntime.emptyArgs);
    }

    public static Object callProp0(Object object, String string2, Context context, Scriptable scriptable) {
        return OptRuntime.getPropFunctionAndThis(object, string2, context, scriptable).call(context, scriptable, OptRuntime.lastStoredScriptable(context), ScriptRuntime.emptyArgs);
    }

    public static Object callSpecial(Context context, Callable callable, Scriptable scriptable, Object[] arrobject, Scriptable scriptable2, Scriptable scriptable3, int n, String string2, int n2) {
        return ScriptRuntime.callSpecial(context, callable, scriptable, arrobject, scriptable2, scriptable3, n, string2, n2);
    }

    public static Scriptable createNativeGenerator(NativeFunction nativeFunction, Scriptable scriptable, Scriptable scriptable2, int n, int n2) {
        return new NativeGenerator(scriptable, nativeFunction, new GeneratorState(scriptable2, n, n2));
    }

    private static int[] decodeIntArray(String string2, int n) {
        if (n == 0) {
            if (string2 == null) {
                return null;
            }
            throw new IllegalArgumentException();
        }
        if (string2.length() != 1 + n * 2 && string2.charAt(0) != '\u0001') {
            throw new IllegalArgumentException();
        }
        int[] arrn = new int[n];
        for (int i = 0; i != n; ++i) {
            int n2 = 1 + i * 2;
            arrn[i] = string2.charAt(n2) << 16 | string2.charAt(n2 + 1);
        }
        return arrn;
    }

    @Deprecated
    public static Object elemIncrDecr(Object object, double d, Context context, int n) {
        return OptRuntime.elemIncrDecr(object, d, context, OptRuntime.getTopCallScope(context), n);
    }

    public static Object elemIncrDecr(Object object, double d, Context context, Scriptable scriptable, int n) {
        return ScriptRuntime.elemIncrDecr(object, d, context, scriptable, n);
    }

    static String encodeIntArray(int[] arrn) {
        if (arrn == null) {
            return null;
        }
        int n = arrn.length;
        char[] arrc = new char[1 + n * 2];
        arrc[0] = '\u0001';
        for (int i = 0; i != n; ++i) {
            int n2 = arrn[i];
            int n3 = 1 + i * 2;
            arrc[n3] = (char)(n2 >>> 16);
            arrc[n3 + 1] = (char)n2;
        }
        return new String(arrc);
    }

    public static Object[] getGeneratorLocalsState(Object object) {
        GeneratorState generatorState = (GeneratorState)object;
        if (generatorState.localsState == null) {
            generatorState.localsState = new Object[generatorState.maxLocals];
        }
        return generatorState.localsState;
    }

    public static Object[] getGeneratorStackState(Object object) {
        GeneratorState generatorState = (GeneratorState)object;
        if (generatorState.stackState == null) {
            generatorState.stackState = new Object[generatorState.maxStack];
        }
        return generatorState.stackState;
    }

    public static void initFunction(NativeFunction nativeFunction, int n, Scriptable scriptable, Context context) {
        ScriptRuntime.initFunction(context, scriptable, nativeFunction, n, false);
    }

    static /* synthetic */ Object lambda$main$0(String[] arrstring, Script script, Context context) {
        ScriptableObject scriptableObject = OptRuntime.getGlobal(context);
        Object[] arrobject = new Object[arrstring.length];
        System.arraycopy((Object)arrstring, (int)0, (Object)arrobject, (int)0, (int)arrstring.length);
        scriptableObject.defineProperty("arguments", (Object)context.newArray((Scriptable)scriptableObject, arrobject), 2);
        script.exec(context, scriptableObject);
        return null;
    }

    public static void main(Script script, String[] arrstring) {
        ContextFactory.getGlobal().call(new -$$Lambda$OptRuntime$nkcl7ufBnCixh_eNHP8jiz3N5n8(arrstring, script));
    }

    public static Scriptable newArrayLiteral(Object[] arrobject, String string2, int n, Context context, Scriptable scriptable) {
        return OptRuntime.newArrayLiteral(arrobject, OptRuntime.decodeIntArray(string2, n), context, scriptable);
    }

    public static Object newObjectSpecial(Context context, Object object, Object[] arrobject, Scriptable scriptable, Scriptable scriptable2, int n) {
        return ScriptRuntime.newSpecial(context, object, arrobject, scriptable, n);
    }

    public static Object[] padStart(Object[] arrobject, int n) {
        Object[] arrobject2 = new Object[n + arrobject.length];
        System.arraycopy((Object)arrobject, (int)0, (Object)arrobject2, (int)n, (int)arrobject.length);
        return arrobject2;
    }

    public static void throwStopIteration(Object object) {
        throw new JavaScriptException(NativeIterator.getStopIterationObject((Scriptable)object), "", 0);
    }

    public static Double wrapDouble(double d) {
        if (d == 0.0) {
            if (1.0 / d > 0.0) {
                return zeroObj;
            }
        } else {
            if (d == 1.0) {
                return oneObj;
            }
            if (d == -1.0) {
                return minusOneObj;
            }
            if (d != d) {
                return NaNobj;
            }
        }
        return d;
    }

    public static class GeneratorState {
        static final String CLASS_NAME = "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState";
        static final String resumptionPoint_NAME = "resumptionPoint";
        static final String resumptionPoint_TYPE = "I";
        static final String thisObj_NAME = "thisObj";
        static final String thisObj_TYPE = "Lorg/mozilla/javascript/Scriptable;";
        Object[] localsState;
        int maxLocals;
        int maxStack;
        public int resumptionPoint;
        Object[] stackState;
        public Scriptable thisObj;

        GeneratorState(Scriptable scriptable, int n, int n2) {
            this.thisObj = scriptable;
            this.maxLocals = n;
            this.maxStack = n2;
        }
    }

}

