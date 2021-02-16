/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.Exception
 *  java.lang.IllegalAccessException
 *  java.lang.IllegalStateException
 *  java.lang.InstantiationException
 *  java.lang.NoSuchMethodException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.ThreadLocal
 *  java.lang.Throwable
 *  java.lang.reflect.AccessibleObject
 *  java.lang.reflect.Constructor
 *  java.lang.reflect.InvocationHandler
 *  java.lang.reflect.InvocationTargetException
 *  java.lang.reflect.Method
 *  java.lang.reflect.Proxy
 */
package org.mozilla.javascript.jdk18;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.InterfaceAdapter;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.VMBridge;

public class VMBridge_jdk18
extends VMBridge {
    private ThreadLocal<Object[]> contextLocal = new ThreadLocal();

    @Override
    protected Context getContext(Object object) {
        return (Context)((Object[])object)[0];
    }

    @Override
    protected Object getInterfaceProxyHelper(ContextFactory contextFactory, Class<?>[] arrclass) {
        Class class_ = Proxy.getProxyClass((ClassLoader)arrclass[0].getClassLoader(), arrclass);
        try {
            Constructor constructor = class_.getConstructor(new Class[]{InvocationHandler.class});
            return constructor;
        }
        catch (NoSuchMethodException noSuchMethodException) {
            throw new IllegalStateException((Throwable)noSuchMethodException);
        }
    }

    @Override
    protected Object getThreadContextHelper() {
        Object[] arrobject = (Object[])this.contextLocal.get();
        if (arrobject == null) {
            arrobject = new Object[1];
            this.contextLocal.set((Object)arrobject);
        }
        return arrobject;
    }

    @Override
    protected Object newInterfaceProxy(Object object, final ContextFactory contextFactory, final InterfaceAdapter interfaceAdapter, final Object object2, final Scriptable scriptable) {
        Constructor constructor = (Constructor)object;
        InvocationHandler invocationHandler = new InvocationHandler(){

            public Object invoke(Object object, Method method, Object[] arrobject) {
                if (method.getDeclaringClass() == Object.class) {
                    String string2 = method.getName();
                    if (string2.equals((Object)"equals")) {
                        Object object22 = arrobject[0];
                        boolean bl = false;
                        if (object == object22) {
                            bl = true;
                        }
                        return bl;
                    }
                    if (string2.equals((Object)"hashCode")) {
                        return object2.hashCode();
                    }
                    if (string2.equals((Object)"toString")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Proxy[");
                        stringBuilder.append(object2.toString());
                        stringBuilder.append("]");
                        return stringBuilder.toString();
                    }
                }
                return interfaceAdapter.invoke(contextFactory, object2, scriptable, object, method, arrobject);
            }
        };
        try {
            Object object3 = constructor.newInstance(new Object[]{invocationHandler});
            return object3;
        }
        catch (InstantiationException instantiationException) {
            throw new IllegalStateException((Throwable)instantiationException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new IllegalStateException((Throwable)illegalAccessException);
        }
        catch (InvocationTargetException invocationTargetException) {
            throw Context.throwAsScriptRuntimeEx(invocationTargetException);
        }
    }

    @Override
    protected void setContext(Object object, Context context) {
        ((Object[])object)[0] = context;
    }

    @Override
    protected boolean tryToMakeAccessible(AccessibleObject accessibleObject) {
        if (accessibleObject.isAccessible()) {
            return true;
        }
        try {
            accessibleObject.setAccessible(true);
        }
        catch (Exception exception) {}
        return accessibleObject.isAccessible();
    }

}

