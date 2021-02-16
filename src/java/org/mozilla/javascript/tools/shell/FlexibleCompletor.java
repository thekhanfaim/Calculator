/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Class
 *  java.lang.Integer
 *  java.lang.NoSuchMethodError
 *  java.lang.NoSuchMethodException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.reflect.InvocationHandler
 *  java.lang.reflect.Method
 *  java.util.List
 */
package org.mozilla.javascript.tools.shell;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

class FlexibleCompletor
implements InvocationHandler {
    private Method completeMethod;
    private Scriptable global;

    FlexibleCompletor(Class<?> class_, Scriptable scriptable) throws NoSuchMethodException {
        this.global = scriptable;
        Class[] arrclass = new Class[]{String.class, Integer.TYPE, List.class};
        this.completeMethod = class_.getMethod("complete", arrclass);
    }

    public int complete(String string2, int n, List<String> list) {
        char c;
        int n2;
        for (n2 = n - 1; n2 >= 0 && (Character.isJavaIdentifierPart((char)(c = string2.charAt(n2))) || c == '.'); --n2) {
        }
        String[] arrstring = string2.substring(n2 + 1, n).split("\\.", -1);
        Scriptable scriptable = this.global;
        for (int i = 0; i < -1 + arrstring.length; ++i) {
            Object object = scriptable.get(arrstring[i], this.global);
            if (object instanceof Scriptable) {
                scriptable = (Scriptable)object;
                continue;
            }
            return string2.length();
        }
        Object[] arrobject = scriptable instanceof ScriptableObject ? ((ScriptableObject)scriptable).getAllIds() : scriptable.getIds();
        String string3 = arrstring[-1 + arrstring.length];
        for (int i = 0; i < arrobject.length; ++i) {
            String string4;
            if (!(arrobject[i] instanceof String) || !(string4 = (String)arrobject[i]).startsWith(string3)) continue;
            if (scriptable.get(string4, scriptable) instanceof Function) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(string4);
                stringBuilder.append("(");
                string4 = stringBuilder.toString();
            }
            list.add((Object)string4);
        }
        return string2.length() - string3.length();
    }

    public Object invoke(Object object, Method method, Object[] arrobject) {
        if (method.equals((Object)this.completeMethod)) {
            return this.complete((String)arrobject[0], (Integer)arrobject[1], (List<String>)((List)arrobject[2]));
        }
        throw new NoSuchMethodError(method.toString());
    }
}

