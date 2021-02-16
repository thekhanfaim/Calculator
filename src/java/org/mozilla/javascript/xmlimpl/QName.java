/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.Deprecated
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
import org.mozilla.javascript.xmlimpl.Namespace;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.mozilla.javascript.xmlimpl.XmlNode;

final class QName
extends IdScriptableObject {
    private static final int Id_constructor = 1;
    private static final int Id_localName = 1;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int Id_uri = 2;
    private static final int MAX_INSTANCE_ID = 2;
    private static final int MAX_PROTOTYPE_ID = 3;
    private static final Object QNAME_TAG = "QName";
    static final long serialVersionUID = 416745167693026750L;
    private XmlNode.QName delegate;
    private XMLLibImpl lib;
    private QName prototype;

    private QName() {
    }

    static QName create(XMLLibImpl xMLLibImpl, Scriptable scriptable, QName qName, XmlNode.QName qName2) {
        QName qName3 = new QName();
        qName3.lib = xMLLibImpl;
        qName3.setParentScope(scriptable);
        qName3.prototype = qName;
        qName3.setPrototype(qName);
        qName3.delegate = qName2;
        return qName3;
    }

    private boolean equals(QName qName) {
        return this.delegate.equals(qName.delegate);
    }

    private Object jsConstructor(Context context, boolean bl, Object[] arrobject) {
        if (!bl && arrobject.length == 1) {
            return this.castToQName(this.lib, context, arrobject[0]);
        }
        if (arrobject.length == 0) {
            return this.constructQName(this.lib, context, Undefined.instance);
        }
        if (arrobject.length == 1) {
            return this.constructQName(this.lib, context, arrobject[0]);
        }
        return this.constructQName(this.lib, context, arrobject[0], arrobject[1]);
    }

    private String js_toSource() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        QName.toSourceImpl(this.uri(), this.localName(), this.prefix(), stringBuilder);
        stringBuilder.append(')');
        return stringBuilder.toString();
    }

    private QName realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof QName) {
            return (QName)scriptable;
        }
        throw QName.incompatibleCallError(idFunctionObject);
    }

    private static void toSourceImpl(String string, String string2, String string3, StringBuilder stringBuilder) {
        stringBuilder.append("new QName(");
        if (string == null && string3 == null) {
            if (!"*".equals((Object)string2)) {
                stringBuilder.append("null, ");
            }
        } else {
            Namespace.toSourceImpl(string3, string, stringBuilder);
            stringBuilder.append(", ");
        }
        stringBuilder.append('\'');
        stringBuilder.append(ScriptRuntime.escapeString(string2, '\''));
        stringBuilder.append("')");
    }

    QName castToQName(XMLLibImpl xMLLibImpl, Context context, Object object) {
        if (object instanceof QName) {
            return (QName)object;
        }
        return this.constructQName(xMLLibImpl, context, object);
    }

    QName constructQName(XMLLibImpl xMLLibImpl, Context context, Object object) {
        return this.constructQName(xMLLibImpl, context, Undefined.instance, object);
    }

    QName constructQName(XMLLibImpl xMLLibImpl, Context context, Object object, Object object2) {
        String string;
        String string2;
        if (object2 instanceof QName) {
            if (object == Undefined.instance) {
                return (QName)object2;
            }
            ((QName)object2).localName();
        }
        String string3 = object2 == Undefined.instance ? "" : ScriptRuntime.toString(object2);
        if (object == Undefined.instance) {
            object = "*".equals((Object)string3) ? null : xMLLibImpl.getDefaultNamespace(context);
        }
        Namespace namespace = object == null ? null : (object instanceof Namespace ? (Namespace)object : xMLLibImpl.newNamespace(ScriptRuntime.toString(object)));
        String string4 = string3;
        if (object == null) {
            string2 = null;
            string = null;
        } else {
            string2 = namespace.uri();
            string = namespace.prefix();
        }
        return this.newQName(xMLLibImpl, string2, string4, string);
    }

    public boolean equals(Object object) {
        if (!(object instanceof QName)) {
            return false;
        }
        return this.equals((QName)object);
    }

    @Override
    protected Object equivalentValues(Object object) {
        if (!(object instanceof QName)) {
            return Scriptable.NOT_FOUND;
        }
        if (this.equals((QName)object)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        int n;
        if (!idFunctionObject.hasTag(QNAME_TAG)) {
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

    void exportAsJSClass(boolean bl) {
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
            if (n2 == 9) {
                string2 = "localName";
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
        return QName.instanceIdInfo(5, n + super.getMaxInstanceId());
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
        return "QName";
    }

    @Override
    public Object getDefaultValue(Class<?> class_) {
        return this.toString();
    }

    final XmlNode.QName getDelegate() {
        return this.delegate;
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
        return "localName";
    }

    @Override
    protected Object getInstanceIdValue(int n) {
        int n2 = n - super.getMaxInstanceId();
        if (n2 != 1) {
            if (n2 != 2) {
                return super.getInstanceIdValue(n);
            }
            return this.uri();
        }
        return this.localName();
    }

    @Override
    protected int getMaxInstanceId() {
        return 2 + super.getMaxInstanceId();
    }

    public int hashCode() {
        return this.delegate.hashCode();
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
        this.initPrototypeMethod(QNAME_TAG, n, string, n2);
    }

    public String localName() {
        if (this.delegate.getLocalName() == null) {
            return "*";
        }
        return this.delegate.getLocalName();
    }

    QName newQName(XMLLibImpl xMLLibImpl, String string, String string2, String string3) {
        XmlNode.Namespace namespace;
        QName qName = this.prototype;
        if (qName == null) {
            qName = this;
        }
        if (string3 != null) {
            namespace = XmlNode.Namespace.create(string3, string);
        } else {
            namespace = null;
            if (string != null) {
                namespace = XmlNode.Namespace.create(string);
            }
        }
        if (string2 != null && string2.equals((Object)"*")) {
            string2 = null;
        }
        return QName.create(xMLLibImpl, this.getParentScope(), qName, XmlNode.QName.create(namespace, string2));
    }

    String prefix() {
        if (this.delegate.getNamespace() == null) {
            return null;
        }
        return this.delegate.getNamespace().getPrefix();
    }

    @Deprecated
    final XmlNode.QName toNodeQname() {
        return this.delegate;
    }

    public String toString() {
        if (this.delegate.getNamespace() == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("*::");
            stringBuilder.append(this.localName());
            return stringBuilder.toString();
        }
        if (this.delegate.getNamespace().isGlobal()) {
            return this.localName();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.uri());
        stringBuilder.append("::");
        stringBuilder.append(this.localName());
        return stringBuilder.toString();
    }

    String uri() {
        if (this.delegate.getNamespace() == null) {
            return null;
        }
        return this.delegate.getNamespace().getUri();
    }
}

