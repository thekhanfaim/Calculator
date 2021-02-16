/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xmlimpl.QName;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XmlNode;

class Namespace
extends IdScriptableObject {
    private static final int Id_constructor = 1;
    private static final int Id_prefix = 1;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int Id_uri = 2;
    private static final int MAX_INSTANCE_ID = 2;
    private static final int MAX_PROTOTYPE_ID = 3;
    private static final Object NAMESPACE_TAG = "Namespace";
    static final long serialVersionUID = -5765755238131301744L;
    private XmlNode.Namespace ns;
    private Namespace prototype;

    private Namespace() {
    }

    private Namespace constructNamespace() {
        return this.newNamespace("", "");
    }

    private Namespace constructNamespace(Object object, Object object2) {
        String string;
        String string2;
        if (object2 instanceof QName) {
            QName qName = (QName)object2;
            string = qName.uri();
            if (string == null) {
                string = qName.toString();
            }
        } else {
            string = ScriptRuntime.toString(object2);
        }
        if (string.length() == 0) {
            if (object == Undefined.instance) {
                string2 = "";
            } else {
                string2 = ScriptRuntime.toString(object);
                if (string2.length() != 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Illegal prefix '");
                    stringBuilder.append(string2);
                    stringBuilder.append("' for 'no namespace'.");
                    throw ScriptRuntime.typeError(stringBuilder.toString());
                }
            }
        } else {
            string2 = object == Undefined.instance ? "" : (!XMLName.accept(object) ? "" : ScriptRuntime.toString(object));
        }
        return this.newNamespace(string2, string);
    }

    static Namespace create(Scriptable scriptable, Namespace namespace, XmlNode.Namespace namespace2) {
        Namespace namespace3 = new Namespace();
        namespace3.setParentScope(scriptable);
        namespace3.prototype = namespace;
        namespace3.setPrototype(namespace);
        namespace3.ns = namespace2;
        return namespace3;
    }

    private boolean equals(Namespace namespace) {
        return this.uri().equals((Object)namespace.uri());
    }

    private Object jsConstructor(Context context, boolean bl, Object[] arrobject) {
        if (!bl && arrobject.length == 1) {
            return this.castToNamespace(arrobject[0]);
        }
        if (arrobject.length == 0) {
            return this.constructNamespace();
        }
        if (arrobject.length == 1) {
            return this.constructNamespace(arrobject[0]);
        }
        return this.constructNamespace(arrobject[0], arrobject[1]);
    }

    private String js_toSource() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        Namespace.toSourceImpl(this.ns.getPrefix(), this.ns.getUri(), stringBuilder);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private Namespace realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof Namespace) {
            return (Namespace)scriptable;
        }
        throw Namespace.incompatibleCallError(idFunctionObject);
    }

    static void toSourceImpl(String string, String string2, StringBuilder stringBuilder) {
        stringBuilder.append("new Namespace(");
        if (string2.length() == 0) {
            if (!"".equals((Object)string)) {
                throw new IllegalArgumentException(string);
            }
        } else {
            stringBuilder.append('\'');
            if (string != null) {
                stringBuilder.append(ScriptRuntime.escapeString(string, '\''));
                stringBuilder.append("', '");
            }
            stringBuilder.append(ScriptRuntime.escapeString(string2, '\''));
            stringBuilder.append('\'');
        }
        stringBuilder.append(')');
    }

    Namespace castToNamespace(Object object) {
        if (object instanceof Namespace) {
            return (Namespace)object;
        }
        return this.constructNamespace(object);
    }

    Namespace constructNamespace(Object object) {
        String string;
        String string2;
        if (object instanceof Namespace) {
            Namespace namespace = (Namespace)object;
            string = namespace.prefix();
            string2 = namespace.uri();
        } else if (object instanceof QName) {
            QName qName = (QName)object;
            String string3 = qName.uri();
            if (string3 != null) {
                String string4 = qName.prefix();
                string2 = string3;
                string = string4;
            } else {
                string2 = qName.toString();
                string = null;
            }
        } else {
            string2 = ScriptRuntime.toString(object);
            string = string2.length() == 0 ? "" : null;
        }
        return this.newNamespace(string, string2);
    }

    public boolean equals(Object object) {
        if (!(object instanceof Namespace)) {
            return false;
        }
        return this.equals((Namespace)object);
    }

    @Override
    protected Object equivalentValues(Object object) {
        if (!(object instanceof Namespace)) {
            return Scriptable.NOT_FOUND;
        }
        if (this.equals((Namespace)object)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        int n;
        if (!idFunctionObject.hasTag(NAMESPACE_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n2 = idFunctionObject.methodId();
        if (n2 != (n = 1)) {
            if (n2 != 2) {
                if (n2 == 3) {
                    return this.realThis(scriptable2, idFunctionObject).js_toSource();
                }
                throw new IllegalArgumentException(String.valueOf((int)n2));
            }
            return this.realThis(scriptable2, idFunctionObject).toString();
        }
        if (scriptable2 != null) {
            n = 0;
        }
        return this.jsConstructor(context, (boolean)n, arrobject);
    }

    public void exportAsJSClass(boolean bl) {
        this.exportAsJSClass(3, this.getParentScope(), bl);
    }

    @Override
    protected int findInstanceIdInfo(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 3) {
            string2 = "uri";
            n = 2;
        } else {
            n = 0;
            string2 = null;
            if (n2 == 6) {
                string2 = "prefix";
                n = 1;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        if (n == 0) {
            return super.findInstanceIdInfo(string);
        }
        if (n != 1 && n != 2) {
            throw new IllegalStateException();
        }
        return Namespace.instanceIdInfo(5, n + super.getMaxInstanceId());
    }

    @Override
    protected int findPrototypeId(String string) {
        int n;
        String string2;
        int n2 = string.length();
        if (n2 == 8) {
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
        } else {
            n = 0;
            string2 = null;
            if (n2 == 11) {
                string2 = "constructor";
                n = 1;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public String getClassName() {
        return "Namespace";
    }

    @Override
    public Object getDefaultValue(Class<?> class_) {
        return this.uri();
    }

    final XmlNode.Namespace getDelegate() {
        return this.ns;
    }

    @Override
    protected String getInstanceIdName(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                return super.getInstanceIdName(n);
            }
            return "uri";
        }
        return "prefix";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                return super.getInstanceIdValue(n);
            }
            return this.ns.getUri();
        }
        if (this.ns.getPrefix() == null) {
            return Undefined.instance;
        }
        return this.ns.getPrefix();
    }

    @Override
    protected int getMaxInstanceId() {
        return 2 + super.getMaxInstanceId();
    }

    public int hashCode() {
        return this.uri().hashCode();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int n) {
        int n2;
        String string;
        if (n != 1) {
            if (n != 2) {
                if (n != 3) throw new IllegalArgumentException(String.valueOf((int)n));
                string = "toSource";
                n2 = 0;
            } else {
                string = "toString";
                n2 = 0;
            }
        } else {
            n2 = 2;
            string = "constructor";
        }
        this.initPrototypeMethod(NAMESPACE_TAG, n, string, n2);
    }

    Namespace newNamespace(String string) {
        Namespace namespace = this.prototype;
        if (namespace == null) {
            namespace = this;
        }
        return Namespace.create(this.getParentScope(), namespace, XmlNode.Namespace.create(string));
    }

    Namespace newNamespace(String string, String string2) {
        if (string == null) {
            return this.newNamespace(string2);
        }
        Namespace namespace = this.prototype;
        if (namespace == null) {
            namespace = this;
        }
        return Namespace.create(this.getParentScope(), namespace, XmlNode.Namespace.create(string, string2));
    }

    public String prefix() {
        return this.ns.getPrefix();
    }

    public String toLocaleString() {
        return this.toString();
    }

    public String toString() {
        return this.uri();
    }

    public String uri() {
        return this.ns.getUri();
    }
}

