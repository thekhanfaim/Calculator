/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.ObjectInputStream
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.ClassNotFoundException
 *  java.lang.Deprecated
 *  java.lang.Double
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.SecurityException
 *  java.lang.String
 *  java.lang.Void
 *  java.lang.reflect.Constructor
 *  java.lang.reflect.Member
 *  java.lang.reflect.Method
 *  java.lang.reflect.Modifier
 */
package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.MemberBox;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.WrapFactory;

public class FunctionObject
extends BaseFunction {
    public static final int JAVA_BOOLEAN_TYPE = 3;
    public static final int JAVA_DOUBLE_TYPE = 4;
    public static final int JAVA_INT_TYPE = 2;
    public static final int JAVA_OBJECT_TYPE = 6;
    public static final int JAVA_SCRIPTABLE_TYPE = 5;
    public static final int JAVA_STRING_TYPE = 1;
    public static final int JAVA_UNSUPPORTED_TYPE = 0;
    private static final short VARARGS_CTOR = -2;
    private static final short VARARGS_METHOD = -1;
    private static boolean sawSecurityException = false;
    private static final long serialVersionUID = -5332312783643935019L;
    private String functionName;
    private transient boolean hasVoidReturn;
    private boolean isStatic;
    MemberBox member;
    private int parmsLength;
    private transient int returnTypeTag;
    private transient byte[] typeTags;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public FunctionObject(String string, Member member, Scriptable scriptable) {
        Class<?> class_;
        block15 : {
            block14 : {
                block13 : {
                    if (member instanceof Constructor) {
                        this.member = new MemberBox((Constructor)member);
                        this.isStatic = true;
                    } else {
                        MemberBox memberBox;
                        this.member = memberBox = new MemberBox((Method)member);
                        this.isStatic = memberBox.isStatic();
                    }
                    String string2 = this.member.getName();
                    this.functionName = string;
                    Class<?>[] arrclass = this.member.argTypes;
                    int n = arrclass.length;
                    if (n == 4 && (arrclass[1].isArray() || arrclass[2].isArray())) {
                        if (arrclass[1].isArray()) {
                            if (!this.isStatic || arrclass[0] != ScriptRuntime.ContextClass || arrclass[1].getComponentType() != ScriptRuntime.ObjectClass || arrclass[2] != ScriptRuntime.FunctionClass || arrclass[3] != Boolean.TYPE) throw Context.reportRuntimeError1("msg.varargs.ctor", string2);
                            this.parmsLength = -2;
                        } else {
                            if (!this.isStatic || arrclass[0] != ScriptRuntime.ContextClass || arrclass[1] != ScriptRuntime.ScriptableClass || arrclass[2].getComponentType() != ScriptRuntime.ObjectClass || arrclass[3] != ScriptRuntime.FunctionClass) throw Context.reportRuntimeError1("msg.varargs.fun", string2);
                            this.parmsLength = -1;
                        }
                    } else {
                        this.parmsLength = n;
                        if (n > 0) {
                            this.typeTags = new byte[n];
                            for (int i = 0; i != n; ++i) {
                                int n2 = FunctionObject.getTypeTag(arrclass[i]);
                                if (n2 == 0) throw Context.reportRuntimeError2("msg.bad.parms", arrclass[i].getName(), string2);
                                this.typeTags[i] = (byte)n2;
                            }
                        }
                    }
                    if (!this.member.isMethod()) break block13;
                    Class class_2 = this.member.method().getReturnType();
                    if (class_2 == Void.TYPE) {
                        this.hasVoidReturn = true;
                    } else {
                        this.returnTypeTag = FunctionObject.getTypeTag(class_2);
                    }
                    break block14;
                }
                class_ = this.member.getDeclaringClass();
                if (!ScriptRuntime.ScriptableClass.isAssignableFrom(class_)) break block15;
            }
            ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
            return;
        }
        EvaluatorException evaluatorException = Context.reportRuntimeError1("msg.bad.ctor.return", class_.getName());
        throw evaluatorException;
    }

    public static Object convertArg(Context context, Scriptable scriptable, Object object, int n) {
        switch (n) {
            default: {
                throw new IllegalArgumentException();
            }
            case 6: {
                return object;
            }
            case 5: {
                return ScriptRuntime.toObjectOrNull(context, object, scriptable);
            }
            case 4: {
                if (object instanceof Double) {
                    return object;
                }
                return ScriptRuntime.toNumber(object);
            }
            case 3: {
                if (object instanceof Boolean) {
                    return object;
                }
                if (ScriptRuntime.toBoolean(object)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
            case 2: {
                if (object instanceof Integer) {
                    return object;
                }
                return ScriptRuntime.toInt32(object);
            }
            case 1: 
        }
        if (object instanceof String) {
            return object;
        }
        return ScriptRuntime.toString(object);
    }

    @Deprecated
    public static Object convertArg(Context context, Scriptable scriptable, Object object, Class<?> class_) {
        int n = FunctionObject.getTypeTag(class_);
        if (n != 0) {
            return FunctionObject.convertArg(context, scriptable, object, n);
        }
        throw Context.reportRuntimeError1("msg.cant.convert", class_.getName());
    }

    static Method findSingleMethod(Method[] arrmethod, String string) {
        Method method = null;
        int n = arrmethod.length;
        for (int i = 0; i != n; ++i) {
            Method method2 = arrmethod[i];
            if (method2 == null || !string.equals((Object)method2.getName())) continue;
            if (method == null) {
                method = method2;
                continue;
            }
            throw Context.reportRuntimeError2("msg.no.overload", string, method2.getDeclaringClass().getName());
        }
        return method;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static Method[] getMethodList(Class<?> class_) {
        Method[] arrmethod = null;
        try {
            boolean bl = sawSecurityException;
            arrmethod = null;
            if (!bl) {
                Method[] arrmethod2 = class_.getDeclaredMethods();
                arrmethod = arrmethod2;
            }
        }
        catch (SecurityException securityException) {
            sawSecurityException = true;
        }
        if (arrmethod == null) {
            arrmethod = class_.getMethods();
        }
        int n = 0;
        for (int i = 0; i < arrmethod.length; ++i) {
            if (sawSecurityException ? arrmethod[i].getDeclaringClass() != class_ : !Modifier.isPublic((int)arrmethod[i].getModifiers())) {
                arrmethod[i] = null;
                continue;
            }
            ++n;
        }
        Method[] arrmethod3 = new Method[n];
        int n2 = 0;
        int n3 = 0;
        while (n3 < arrmethod.length) {
            if (arrmethod[n3] != null) {
                int n4 = n2 + 1;
                arrmethod3[n2] = arrmethod[n3];
                n2 = n4;
            }
            ++n3;
        }
        return arrmethod3;
    }

    public static int getTypeTag(Class<?> class_) {
        if (class_ == ScriptRuntime.StringClass) {
            return 1;
        }
        if (class_ != ScriptRuntime.IntegerClass && class_ != Integer.TYPE) {
            if (class_ != ScriptRuntime.BooleanClass && class_ != Boolean.TYPE) {
                if (class_ != ScriptRuntime.DoubleClass && class_ != Double.TYPE) {
                    if (ScriptRuntime.ScriptableClass.isAssignableFrom(class_)) {
                        return 5;
                    }
                    if (class_ == ScriptRuntime.ObjectClass) {
                        return 6;
                    }
                    return 0;
                }
                return 4;
            }
            return 3;
        }
        return 2;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.parmsLength > 0) {
            Class<?>[] arrclass = this.member.argTypes;
            this.typeTags = new byte[this.parmsLength];
            for (int i = 0; i != this.parmsLength; ++i) {
                this.typeTags[i] = (byte)FunctionObject.getTypeTag(arrclass[i]);
            }
        }
        if (this.member.isMethod()) {
            Class class_ = this.member.method().getReturnType();
            if (class_ == Void.TYPE) {
                this.hasVoidReturn = true;
                return;
            }
            this.returnTypeTag = FunctionObject.getTypeTag(class_);
        }
    }

    public void addAsConstructor(Scriptable scriptable, Scriptable scriptable2) {
        this.initAsConstructor(scriptable, scriptable2);
        FunctionObject.defineProperty(scriptable, scriptable2.getClassName(), this, 2);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        Object object;
        boolean bl = false;
        int n = arrobject.length;
        for (int i = 0; i < n; ++i) {
            if (!(arrobject[i] instanceof ConsString)) continue;
            arrobject[i] = arrobject[i].toString();
        }
        int n2 = this.parmsLength;
        if (n2 < 0) {
            if (n2 == -1) {
                Object[] arrobject2 = new Object[]{context, scriptable2, arrobject, this};
                object = this.member.invoke(null, arrobject2);
                bl = true;
            } else {
                boolean bl2 = scriptable2 == null;
                Boolean bl3 = bl2 ? Boolean.TRUE : Boolean.FALSE;
                Object[] arrobject3 = new Object[]{context, arrobject, this, bl3};
                Object object2 = this.member.isCtor() ? this.member.newInstance(arrobject3) : this.member.invoke(null, arrobject3);
                object = object2;
                bl = false;
            }
        } else {
            Object[] arrobject4;
            Class<?> class_;
            int n3;
            if (!this.isStatic && !(class_ = this.member.getDeclaringClass()).isInstance((Object)scriptable2)) {
                boolean bl4 = false;
                if (scriptable2 == scriptable) {
                    Scriptable scriptable3 = this.getParentScope();
                    bl4 = false;
                    if (scriptable != scriptable3 && (bl4 = class_.isInstance((Object)scriptable3))) {
                        scriptable2 = scriptable3;
                    }
                }
                if (!bl4) {
                    throw ScriptRuntime.typeError1("msg.incompat.call", this.functionName);
                }
            }
            if ((n3 = this.parmsLength) == n) {
                arrobject4 = arrobject;
                for (int i = 0; i != this.parmsLength; ++i) {
                    Object object3 = arrobject[i];
                    Object object4 = FunctionObject.convertArg(context, scriptable, object3, this.typeTags[i]);
                    if (object3 == object4) continue;
                    if (arrobject4 == arrobject) {
                        arrobject4 = (Object[])arrobject.clone();
                    }
                    arrobject4[i] = object4;
                }
            } else if (n3 == 0) {
                arrobject4 = ScriptRuntime.emptyArgs;
            } else {
                arrobject4 = new Object[n3];
                for (int i = 0; i != this.parmsLength; ++i) {
                    Object object5 = i < n ? arrobject[i] : Undefined.instance;
                    arrobject4[i] = FunctionObject.convertArg(context, scriptable, object5, this.typeTags[i]);
                }
            }
            if (this.member.isMethod()) {
                object = this.member.invoke(scriptable2, arrobject4);
                bl = true;
            } else {
                object = this.member.newInstance(arrobject4);
            }
        }
        if (bl) {
            if (this.hasVoidReturn) {
                return Undefined.instance;
            }
            if (this.returnTypeTag == 0) {
                object = context.getWrapFactory().wrap(context, scriptable, object, null);
            }
        }
        return object;
    }

    @Override
    public Scriptable createObject(Context context, Scriptable scriptable) {
        if (!this.member.isCtor() && this.parmsLength != -2) {
            Scriptable scriptable2;
            try {
                scriptable2 = (Scriptable)this.member.getDeclaringClass().newInstance();
            }
            catch (Exception exception) {
                throw Context.throwAsScriptRuntimeEx(exception);
            }
            scriptable2.setPrototype(this.getClassPrototype());
            scriptable2.setParentScope(this.getParentScope());
            return scriptable2;
        }
        return null;
    }

    @Override
    public int getArity() {
        int n = this.parmsLength;
        if (n < 0) {
            n = 1;
        }
        return n;
    }

    @Override
    public String getFunctionName() {
        String string = this.functionName;
        if (string == null) {
            string = "";
        }
        return string;
    }

    @Override
    public int getLength() {
        return this.getArity();
    }

    public Member getMethodOrConstructor() {
        if (this.member.isMethod()) {
            return this.member.method();
        }
        return this.member.ctor();
    }

    void initAsConstructor(Scriptable scriptable, Scriptable scriptable2) {
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
        this.setImmunePrototypeProperty(scriptable2);
        scriptable2.setParentScope(this);
        FunctionObject.defineProperty(scriptable2, "constructor", this, 7);
        this.setParentScope(scriptable);
    }

    boolean isVarArgsConstructor() {
        return this.parmsLength == -2;
    }

    boolean isVarArgsMethod() {
        return this.parmsLength == -1;
    }
}

