/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.IllegalAccessException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Field
 */
package org.mozilla.javascript;

import java.lang.reflect.Field;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.MemberBox;
import org.mozilla.javascript.NativeJavaMethod;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

class FieldAndMethods
extends NativeJavaMethod {
    private static final long serialVersionUID = -9222428244284796755L;
    Field field;
    Object javaObject;

    FieldAndMethods(Scriptable scriptable, MemberBox[] arrmemberBox, Field field) {
        super(arrmemberBox);
        this.field = field;
        this.setParentScope(scriptable);
        this.setPrototype(ScriptableObject.getFunctionPrototype(scriptable));
    }

    @Override
    public Object getDefaultValue(Class<?> class_) {
        Class class_2;
        Object object;
        if (class_ == ScriptRuntime.FunctionClass) {
            return this;
        }
        try {
            object = this.field.get(this.javaObject);
            class_2 = this.field.getType();
        }
        catch (IllegalAccessException illegalAccessException) {
            throw Context.reportRuntimeError1("msg.java.internal.private", this.field.getName());
        }
        Context context = Context.getContext();
        Object object2 = context.getWrapFactory().wrap(context, this, object, class_2);
        if (object2 instanceof Scriptable) {
            object2 = ((Scriptable)object2).getDefaultValue(class_);
        }
        return object2;
    }
}

