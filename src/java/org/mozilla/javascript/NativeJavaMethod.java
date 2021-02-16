/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Void
 *  java.lang.reflect.Array
 *  java.lang.reflect.Method
 *  java.util.concurrent.CopyOnWriteArrayList
 */
package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaMembers;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.MemberBox;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.ResolvedOverload;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;
import org.mozilla.javascript.Wrapper;

public class NativeJavaMethod
extends BaseFunction {
    private static final int PREFERENCE_AMBIGUOUS = 3;
    private static final int PREFERENCE_EQUAL = 0;
    private static final int PREFERENCE_FIRST_ARG = 1;
    private static final int PREFERENCE_SECOND_ARG = 2;
    private static final boolean debug = false;
    private static final long serialVersionUID = -3440381785576412928L;
    private String functionName;
    MemberBox[] methods;
    private transient CopyOnWriteArrayList<ResolvedOverload> overloadCache;

    public NativeJavaMethod(Method method, String string) {
        this(new MemberBox(method), string);
    }

    NativeJavaMethod(MemberBox memberBox, String string) {
        this.functionName = string;
        this.methods = new MemberBox[]{memberBox};
    }

    NativeJavaMethod(MemberBox[] arrmemberBox) {
        this.functionName = arrmemberBox[0].getName();
        this.methods = arrmemberBox;
    }

    NativeJavaMethod(MemberBox[] arrmemberBox, String string) {
        this.functionName = string;
        this.methods = arrmemberBox;
    }

    static int findFunction(Context context, MemberBox[] arrmemberBox, Object[] arrobject) {
        EvaluatorException evaluatorException;
        int n = arrmemberBox.length;
        int n2 = -1;
        if (n == 0) {
            return n2;
        }
        if (arrmemberBox.length == 1) {
            MemberBox memberBox = arrmemberBox[0];
            Class<?>[] arrclass = memberBox.argTypes;
            int n3 = arrclass.length;
            if (memberBox.vararg ? --n3 > arrobject.length : n3 != arrobject.length) {
                return n2;
            }
            for (int i = 0; i != n3; ++i) {
                if (NativeJavaObject.canConvert(arrobject[i], arrclass[i])) continue;
                return n2;
            }
            return 0;
        }
        int n4 = -1;
        int[] arrn = null;
        int n5 = 0;
        for (int i = 0; i < arrmemberBox.length; ++i) {
            block26 : {
                Class<?>[] arrclass;
                MemberBox memberBox;
                int n6;
                block27 : {
                    memberBox = arrmemberBox[i];
                    arrclass = memberBox.argTypes;
                    n6 = arrclass.length;
                    if (memberBox.vararg ? --n6 > arrobject.length : n6 != arrobject.length) break block26;
                    for (int j = 0; j < n6; ++j) {
                        if (NativeJavaObject.canConvert(arrobject[j], arrclass[j])) {
                            continue;
                        }
                        break block26;
                    }
                    if (n4 >= 0) break block27;
                    n4 = i;
                    break block26;
                }
                int n7 = 0;
                int n8 = 0;
                for (int j = -1; j != n5; ++j) {
                    MemberBox memberBox2;
                    int n9;
                    block31 : {
                        int n10;
                        block29 : {
                            block30 : {
                                block28 : {
                                    int n11 = j == n2 ? n4 : arrn[j];
                                    memberBox2 = arrmemberBox[n11];
                                    if (!context.hasFeature(13) || memberBox2.isPublic() == memberBox.isPublic()) break block28;
                                    if (!memberBox2.isPublic()) {
                                        ++n7;
                                        n10 = n6;
                                    } else {
                                        ++n8;
                                        n10 = n6;
                                    }
                                    break block29;
                                }
                                boolean bl = memberBox.vararg;
                                Class<?>[] arrclass2 = memberBox2.argTypes;
                                n10 = n6;
                                n9 = NativeJavaMethod.preferSignature(arrobject, arrclass, bl, arrclass2, memberBox2.vararg);
                                if (n9 == 3) break;
                                if (n9 != 1) break block30;
                                ++n7;
                                break block29;
                            }
                            if (n9 != 2) break block31;
                            ++n8;
                        }
                        n6 = n10;
                        n2 = -1;
                        continue;
                    }
                    if (n9 != 0) {
                        Kit.codeBug();
                    }
                    if (memberBox2.isStatic() && memberBox2.getDeclaringClass().isAssignableFrom(memberBox.getDeclaringClass())) {
                        if (j == -1) {
                            n4 = i;
                        } else {
                            arrn[j] = i;
                        }
                    }
                    break block26;
                }
                if (n7 == n5 + 1) {
                    n4 = i;
                    n5 = 0;
                } else if (n8 != n5 + 1) {
                    if (arrn == null) {
                        arrn = new int[arrmemberBox.length - 1];
                    }
                    arrn[n5] = i;
                    ++n5;
                }
            }
            n2 = -1;
        }
        if (n4 < 0) {
            return -1;
        }
        if (n5 == 0) {
            return n4;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = -1; i != n5; ++i) {
            int n12 = i == -1 ? n4 : arrn[i];
            stringBuilder.append("\n    ");
            stringBuilder.append(arrmemberBox[n12].toJavaDeclaration());
        }
        MemberBox memberBox = arrmemberBox[n4];
        String string = memberBox.getName();
        String string2 = memberBox.getDeclaringClass().getName();
        if (arrmemberBox[0].isCtor()) {
            throw Context.reportRuntimeError3("msg.constructor.ambiguous", string, NativeJavaMethod.scriptSignature(arrobject), stringBuilder.toString());
        }
        evaluatorException = Context.reportRuntimeError4("msg.method.ambiguous", string2, string, NativeJavaMethod.scriptSignature(arrobject), stringBuilder.toString());
        throw evaluatorException;
    }

    private static int preferSignature(Object[] arrobject, Class<?>[] arrclass, boolean bl, Class<?>[] arrclass2, boolean bl2) {
        int n = 0;
        for (int i = 0; i < arrobject.length; ++i) {
            Class<?> class_;
            int n2;
            Class<?> class_2 = bl && i >= arrclass.length ? arrclass[-1 + arrclass.length] : arrclass[i];
            if (class_2 == (class_ = bl2 && i >= arrclass2.length ? arrclass2[-1 + arrclass2.length] : arrclass2[i])) continue;
            Object object = arrobject[i];
            int n3 = NativeJavaObject.getConversionWeight(object, class_2);
            int n4 = n3 < (n2 = NativeJavaObject.getConversionWeight(object, class_)) ? 1 : (n3 > n2 ? 2 : (n3 == 0 ? (class_2.isAssignableFrom(class_) ? 2 : (class_.isAssignableFrom(class_2) ? 1 : 3)) : 3));
            if ((n |= n4) != 3) continue;
            return n;
        }
        return n;
    }

    private static void printDebug(String string, MemberBox memberBox, Object[] arrobject) {
    }

    static String scriptSignature(Object[] arrobject) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i != arrobject.length; ++i) {
            Object object = arrobject[i];
            String string = object == null ? "null" : (object instanceof Boolean ? "boolean" : (object instanceof String ? "string" : (object instanceof Number ? "number" : (object instanceof Scriptable ? (object instanceof Undefined ? "undefined" : (object instanceof Wrapper ? ((Wrapper)object).unwrap().getClass().getName() : (object instanceof Function ? "function" : "object"))) : JavaMembers.javaSignature(object.getClass())))));
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        MemberBox memberBox;
        Object object;
        block14 : {
            if (this.methods.length == 0) {
                RuntimeException runtimeException = new RuntimeException("No methods defined for call");
                throw runtimeException;
            }
            int n = this.findCachedFunction(context, arrobject);
            if (n < 0) {
                Class class_ = this.methods[0].method().getDeclaringClass();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(class_.getName());
                stringBuilder.append('.');
                stringBuilder.append(this.getFunctionName());
                stringBuilder.append('(');
                stringBuilder.append(NativeJavaMethod.scriptSignature(arrobject));
                stringBuilder.append(')');
                throw Context.reportRuntimeError1("msg.java.no_such_method", stringBuilder.toString());
            }
            memberBox = this.methods[n];
            Class<?>[] arrclass = memberBox.argTypes;
            if (memberBox.vararg) {
                Object object2;
                Object[] arrobject2 = new Object[arrclass.length];
                for (int i = 0; i < -1 + arrclass.length; ++i) {
                    arrobject2[i] = Context.jsToJava(arrobject[i], arrclass[i]);
                }
                if (arrobject.length == arrclass.length && (arrobject[-1 + arrobject.length] == null || arrobject[-1 + arrobject.length] instanceof NativeArray || arrobject[-1 + arrobject.length] instanceof NativeJavaArray)) {
                    object2 = Context.jsToJava(arrobject[-1 + arrobject.length], arrclass[-1 + arrclass.length]);
                } else {
                    Class class_ = arrclass[-1 + arrclass.length].getComponentType();
                    Object object3 = Array.newInstance((Class)class_, (int)(1 + (arrobject.length - arrclass.length)));
                    for (int i = 0; i < Array.getLength((Object)object3); ++i) {
                        Array.set((Object)object3, (int)i, (Object)Context.jsToJava(arrobject[i + (-1 + arrclass.length)], class_));
                    }
                    object2 = object3;
                }
                arrobject2[-1 + arrclass.length] = object2;
                arrobject = arrobject2;
            } else {
                Object[] arrobject3 = arrobject;
                for (int i = 0; i < arrobject.length; ++i) {
                    Object object4 = arrobject[i];
                    Object object5 = Context.jsToJava(object4, arrclass[i]);
                    if (object5 == object4) continue;
                    if (arrobject3 == arrobject) {
                        arrobject = (Object[])arrobject.clone();
                    }
                    arrobject[i] = object5;
                }
            }
            if (memberBox.isStatic()) {
                object = null;
            } else {
                Scriptable scriptable3 = scriptable2;
                Class<?> class_ = memberBox.getDeclaringClass();
                while (scriptable3 != null) {
                    Object object6;
                    if (scriptable3 instanceof Wrapper && class_.isInstance(object6 = ((Wrapper)((Object)scriptable3)).unwrap())) {
                        object = object6;
                        break block14;
                    }
                    scriptable3 = scriptable3.getPrototype();
                }
                throw Context.reportRuntimeError3("msg.nonjava.method", this.getFunctionName(), ScriptRuntime.toString(scriptable2), class_.getName());
            }
        }
        Object object7 = memberBox.invoke(object, arrobject);
        Class class_ = memberBox.method().getReturnType();
        Object object8 = context.getWrapFactory().wrap(context, scriptable, object7, class_);
        if (object8 != null) return object8;
        if (class_ != Void.TYPE) return object8;
        return Undefined.instance;
    }

    @Override
    String decompile(int n, int n2) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = (n2 & 1) != 0;
        if (!bl) {
            stringBuilder.append("function ");
            stringBuilder.append(this.getFunctionName());
            stringBuilder.append("() {");
        }
        stringBuilder.append("/*\n");
        stringBuilder.append(this.toString());
        String string = bl ? "*/\n" : "*/}\n";
        stringBuilder.append(string);
        return stringBuilder.toString();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    int findCachedFunction(Context context, Object[] arrobject) {
        CopyOnWriteArrayList<ResolvedOverload> copyOnWriteArrayList;
        MemberBox[] arrmemberBox = this.methods;
        if (arrmemberBox.length <= 1) {
            return NativeJavaMethod.findFunction(context, arrmemberBox, arrobject);
        }
        CopyOnWriteArrayList<ResolvedOverload> copyOnWriteArrayList2 = this.overloadCache;
        if (copyOnWriteArrayList2 != null) {
            for (ResolvedOverload resolvedOverload : copyOnWriteArrayList2) {
                if (!resolvedOverload.matches(arrobject)) continue;
                return resolvedOverload.index;
            }
        } else {
            this.overloadCache = new CopyOnWriteArrayList();
        }
        int n = NativeJavaMethod.findFunction(context, this.methods, arrobject);
        if (this.overloadCache.size() >= 2 * this.methods.length) {
            return n;
        }
        CopyOnWriteArrayList<ResolvedOverload> copyOnWriteArrayList3 = copyOnWriteArrayList = this.overloadCache;
        synchronized (copyOnWriteArrayList3) {
            ResolvedOverload resolvedOverload = new ResolvedOverload(arrobject, n);
            if (!this.overloadCache.contains((Object)resolvedOverload)) {
                this.overloadCache.add(0, (Object)resolvedOverload);
            }
            return n;
        }
    }

    @Override
    public String getFunctionName() {
        return this.functionName;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int n = this.methods.length;
        for (int i = 0; i != n; ++i) {
            if (this.methods[i].isMethod()) {
                Method method = this.methods[i].method();
                stringBuilder.append(JavaMembers.javaSignature(method.getReturnType()));
                stringBuilder.append(' ');
                stringBuilder.append(method.getName());
            } else {
                stringBuilder.append(this.methods[i].getName());
            }
            stringBuilder.append(JavaMembers.liveConnectSignature(this.methods[i].argTypes));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}

