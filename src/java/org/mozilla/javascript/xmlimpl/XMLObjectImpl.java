/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.Namespace;
import org.mozilla.javascript.xmlimpl.QName;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLCtor;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XMLWithScope;
import org.mozilla.javascript.xmlimpl.XmlNode;
import org.mozilla.javascript.xmlimpl.XmlProcessor;

abstract class XMLObjectImpl
extends XMLObject {
    private static final int Id_addNamespace = 2;
    private static final int Id_appendChild = 3;
    private static final int Id_attribute = 4;
    private static final int Id_attributes = 5;
    private static final int Id_child = 6;
    private static final int Id_childIndex = 7;
    private static final int Id_children = 8;
    private static final int Id_comments = 9;
    private static final int Id_constructor = 1;
    private static final int Id_contains = 10;
    private static final int Id_copy = 11;
    private static final int Id_descendants = 12;
    private static final int Id_elements = 13;
    private static final int Id_hasComplexContent = 18;
    private static final int Id_hasOwnProperty = 17;
    private static final int Id_hasSimpleContent = 19;
    private static final int Id_inScopeNamespaces = 14;
    private static final int Id_insertChildAfter = 15;
    private static final int Id_insertChildBefore = 16;
    private static final int Id_length = 20;
    private static final int Id_localName = 21;
    private static final int Id_name = 22;
    private static final int Id_namespace = 23;
    private static final int Id_namespaceDeclarations = 24;
    private static final int Id_nodeKind = 25;
    private static final int Id_normalize = 26;
    private static final int Id_parent = 27;
    private static final int Id_prependChild = 28;
    private static final int Id_processingInstructions = 29;
    private static final int Id_propertyIsEnumerable = 30;
    private static final int Id_removeNamespace = 31;
    private static final int Id_replace = 32;
    private static final int Id_setChildren = 33;
    private static final int Id_setLocalName = 34;
    private static final int Id_setName = 35;
    private static final int Id_setNamespace = 36;
    private static final int Id_text = 37;
    private static final int Id_toSource = 39;
    private static final int Id_toString = 38;
    private static final int Id_toXMLString = 40;
    private static final int Id_valueOf = 41;
    private static final int MAX_PROTOTYPE_ID = 41;
    private static final Object XMLOBJECT_TAG = "XMLObject";
    private XMLLibImpl lib;
    private boolean prototypeFlag;

    protected XMLObjectImpl(XMLLibImpl xMLLibImpl, Scriptable scriptable, XMLObject xMLObject) {
        this.initialize(xMLLibImpl, scriptable, xMLObject);
    }

    private static Object arg(Object[] arrobject, int n) {
        if (n < arrobject.length) {
            return arrobject[n];
        }
        return Undefined.instance;
    }

    private XMLList getMatches(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        this.addMatches(xMLList, xMLName);
        return xMLList;
    }

    private Object[] toObjectArray(Object[] arrobject) {
        Object[] arrobject2 = new Object[arrobject.length];
        for (int i = 0; i < arrobject2.length; ++i) {
            arrobject2[i] = arrobject[i];
        }
        return arrobject2;
    }

    private void xmlMethodNotFound(Object object, String string) {
        throw ScriptRuntime.notFunctionError(object, string);
    }

    abstract void addMatches(XMLList var1, XMLName var2);

    @Override
    public final Object addValues(Context context, boolean bl, Object object) {
        if (object instanceof XMLObject) {
            XMLObject xMLObject;
            XMLObject xMLObject2;
            if (bl) {
                xMLObject2 = this;
                xMLObject = (XMLObject)object;
            } else {
                xMLObject2 = (XMLObject)object;
                xMLObject = this;
            }
            return this.lib.addXMLObjects(context, xMLObject2, xMLObject);
        }
        if (object == Undefined.instance) {
            return ScriptRuntime.toString(this);
        }
        return super.addValues(context, bl, object);
    }

    abstract XMLList child(int var1);

    abstract XMLList child(XMLName var1);

    abstract XMLList children();

    abstract XMLList comments();

    abstract boolean contains(Object var1);

    abstract XMLObjectImpl copy();

    final XML createEmptyXML() {
        return this.newXML(XmlNode.createEmpty(this.getProcessor()));
    }

    final Namespace createNamespace(XmlNode.Namespace namespace) {
        if (namespace == null) {
            return null;
        }
        return this.lib.createNamespaces(new XmlNode.Namespace[]{namespace})[0];
    }

    final Namespace[] createNamespaces(XmlNode.Namespace[] arrnamespace) {
        return this.lib.createNamespaces(arrnamespace);
    }

    @Override
    public void delete(String string) {
        Context context = Context.getCurrentContext();
        this.deleteXMLProperty(this.lib.toXMLNameFromString(context, string));
    }

    @Override
    public final boolean delete(Context context, Object object) {
        XMLName xMLName;
        if (context == null) {
            context = Context.getCurrentContext();
        }
        if ((xMLName = this.lib.toXMLNameOrIndex(context, object)) == null) {
            this.delete((int)ScriptRuntime.lastUint32Result(context));
            return true;
        }
        this.deleteXMLProperty(xMLName);
        return true;
    }

    abstract void deleteXMLProperty(XMLName var1);

    final String ecmaEscapeAttributeValue(String string) {
        String string2 = this.lib.escapeAttributeValue(string);
        return string2.substring(1, string2.length() - 1);
    }

    final XML ecmaToXml(Object object) {
        return this.lib.ecmaToXml(object);
    }

    abstract XMLList elements(XMLName var1);

    @Override
    public NativeWith enterDotQuery(Scriptable scriptable) {
        XMLWithScope xMLWithScope = new XMLWithScope(this.lib, scriptable, this);
        xMLWithScope.initAsDotQuery();
        return xMLWithScope;
    }

    @Override
    public NativeWith enterWith(Scriptable scriptable) {
        return new XMLWithScope(this.lib, scriptable, this);
    }

    @Override
    protected final Object equivalentValues(Object object) {
        if (this.equivalentXml(object)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    abstract boolean equivalentXml(Object var1);

    @Override
    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        int n;
        if (!idFunctionObject.hasTag(XMLOBJECT_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, arrobject);
        }
        int n2 = idFunctionObject.methodId();
        if (n2 == (n = 1)) {
            if (scriptable2 != null) {
                n = 0;
            }
            return this.jsConstructor(context, (boolean)n, arrobject);
        }
        if (scriptable2 instanceof XMLObjectImpl) {
            XMLObjectImpl xMLObjectImpl = (XMLObjectImpl)scriptable2;
            XML xML = xMLObjectImpl.getXML();
            switch (n2) {
                default: {
                    throw new IllegalArgumentException(String.valueOf((int)n2));
                }
                case 41: {
                    return xMLObjectImpl.valueOf();
                }
                case 40: {
                    return xMLObjectImpl.toXMLString();
                }
                case 39: {
                    return xMLObjectImpl.toSource(ScriptRuntime.toInt32(arrobject, 0));
                }
                case 38: {
                    return xMLObjectImpl.toString();
                }
                case 37: {
                    return xMLObjectImpl.text();
                }
                case 36: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "setNamespace");
                    }
                    xML.setNamespace(this.lib.castToNamespace(context, XMLObjectImpl.arg(arrobject, 0)));
                    return Undefined.instance;
                }
                case 35: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "setName");
                    }
                    Object object = arrobject.length != 0 ? arrobject[0] : Undefined.instance;
                    xML.setName(this.lib.constructQName(context, object));
                    return Undefined.instance;
                }
                case 34: {
                    Object object;
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "setLocalName");
                    }
                    String string = (object = XMLObjectImpl.arg(arrobject, 0)) instanceof QName ? ((QName)object).localName() : ScriptRuntime.toString(object);
                    xML.setLocalName(string);
                    return Undefined.instance;
                }
                case 33: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "setChildren");
                    }
                    return xML.setChildren(XMLObjectImpl.arg(arrobject, 0));
                }
                case 32: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "replace");
                    }
                    XMLName xMLName = this.lib.toXMLNameOrIndex(context, XMLObjectImpl.arg(arrobject, 0));
                    Object object = XMLObjectImpl.arg(arrobject, n);
                    if (xMLName == null) {
                        return xML.replace((int)ScriptRuntime.lastUint32Result(context), object);
                    }
                    return xML.replace(xMLName, object);
                }
                case 31: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "removeNamespace");
                    }
                    return xML.removeNamespace(this.lib.castToNamespace(context, XMLObjectImpl.arg(arrobject, 0)));
                }
                case 30: {
                    return ScriptRuntime.wrapBoolean(xMLObjectImpl.propertyIsEnumerable(XMLObjectImpl.arg(arrobject, 0)));
                }
                case 29: {
                    XMLName xMLName = arrobject.length > 0 ? this.lib.toXMLName(context, arrobject[0]) : XMLName.formStar();
                    return xMLObjectImpl.processingInstructions(xMLName);
                }
                case 28: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "prependChild");
                    }
                    return xML.prependChild(XMLObjectImpl.arg(arrobject, 0));
                }
                case 27: {
                    return xMLObjectImpl.parent();
                }
                case 26: {
                    xMLObjectImpl.normalize();
                    return Undefined.instance;
                }
                case 25: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "nodeKind");
                    }
                    return xML.nodeKind();
                }
                case 24: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "namespaceDeclarations");
                    }
                    return context.newArray(scriptable, this.toObjectArray(xML.namespaceDeclarations()));
                }
                case 23: {
                    Namespace namespace;
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "namespace");
                    }
                    int n3 = arrobject.length;
                    String string = null;
                    if (n3 > 0) {
                        string = ScriptRuntime.toString(arrobject[0]);
                    }
                    if ((namespace = xML.namespace(string)) == null) {
                        return Undefined.instance;
                    }
                    return namespace;
                }
                case 22: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "name");
                    }
                    return xML.name();
                }
                case 21: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "localName");
                    }
                    return xML.localName();
                }
                case 20: {
                    return ScriptRuntime.wrapInt(xMLObjectImpl.length());
                }
                case 19: {
                    return ScriptRuntime.wrapBoolean(xMLObjectImpl.hasSimpleContent());
                }
                case 18: {
                    return ScriptRuntime.wrapBoolean(xMLObjectImpl.hasComplexContent());
                }
                case 17: {
                    return ScriptRuntime.wrapBoolean(xMLObjectImpl.hasOwnProperty(this.lib.toXMLName(context, XMLObjectImpl.arg(arrobject, 0))));
                }
                case 16: {
                    Object object;
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "insertChildBefore");
                    }
                    if ((object = XMLObjectImpl.arg(arrobject, 0)) != null && !(object instanceof XML)) {
                        return Undefined.instance;
                    }
                    return xML.insertChildBefore((XML)object, XMLObjectImpl.arg(arrobject, n));
                }
                case 15: {
                    Object object;
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "insertChildAfter");
                    }
                    if ((object = XMLObjectImpl.arg(arrobject, 0)) != null && !(object instanceof XML)) {
                        return Undefined.instance;
                    }
                    return xML.insertChildAfter((XML)object, XMLObjectImpl.arg(arrobject, n));
                }
                case 14: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "inScopeNamespaces");
                    }
                    return context.newArray(scriptable, this.toObjectArray(xML.inScopeNamespaces()));
                }
                case 13: {
                    XMLName xMLName = arrobject.length == 0 ? XMLName.formStar() : this.lib.toXMLName(context, arrobject[0]);
                    return xMLObjectImpl.elements(xMLName);
                }
                case 12: {
                    XmlNode.QName qName = arrobject.length == 0 ? XmlNode.QName.create(null, null) : this.lib.toNodeQName(context, arrobject[0], false);
                    return xMLObjectImpl.getMatches(XMLName.create(qName, false, (boolean)n));
                }
                case 11: {
                    return xMLObjectImpl.copy();
                }
                case 10: {
                    return ScriptRuntime.wrapBoolean(xMLObjectImpl.contains(XMLObjectImpl.arg(arrobject, 0)));
                }
                case 9: {
                    return xMLObjectImpl.comments();
                }
                case 8: {
                    return xMLObjectImpl.children();
                }
                case 7: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "childIndex");
                    }
                    return ScriptRuntime.wrapInt(xML.childIndex());
                }
                case 6: {
                    XMLName xMLName = this.lib.toXMLNameOrIndex(context, XMLObjectImpl.arg(arrobject, 0));
                    if (xMLName == null) {
                        return xMLObjectImpl.child((int)ScriptRuntime.lastUint32Result(context));
                    }
                    return xMLObjectImpl.child(xMLName);
                }
                case 5: {
                    return xMLObjectImpl.getMatches(XMLName.create(XmlNode.QName.create(null, null), (boolean)n, false));
                }
                case 4: {
                    return xMLObjectImpl.getMatches(XMLName.create(this.lib.toNodeQName(context, XMLObjectImpl.arg(arrobject, 0), (boolean)n), (boolean)n, false));
                }
                case 3: {
                    if (xML == null) {
                        this.xmlMethodNotFound(xMLObjectImpl, "appendChild");
                    }
                    return xML.appendChild(XMLObjectImpl.arg(arrobject, 0));
                }
                case 2: 
            }
            if (xML == null) {
                this.xmlMethodNotFound(xMLObjectImpl, "addNamespace");
            }
            return xML.addNamespace(this.lib.castToNamespace(context, XMLObjectImpl.arg(arrobject, 0)));
        }
        throw XMLObjectImpl.incompatibleCallError(idFunctionObject);
    }

    final void exportAsJSClass(boolean bl) {
        this.prototypeFlag = true;
        this.exportAsJSClass(41, this.getParentScope(), bl);
    }

    @Override
    protected int findPrototypeId(String string) {
        String string2;
        int n;
        switch (string.length()) {
            default: {
                n = 0;
                string2 = null;
                break;
            }
            case 22: {
                string2 = "processingInstructions";
                n = 29;
                break;
            }
            case 21: {
                string2 = "namespaceDeclarations";
                n = 24;
                break;
            }
            case 20: {
                string2 = "propertyIsEnumerable";
                n = 30;
                break;
            }
            case 17: {
                char c = string.charAt(3);
                if (c == 'C') {
                    string2 = "hasComplexContent";
                    n = 18;
                    break;
                }
                if (c == 'c') {
                    string2 = "inScopeNamespaces";
                    n = 14;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 'e') break;
                string2 = "insertChildBefore";
                n = 16;
                break;
            }
            case 16: {
                char c = string.charAt(0);
                if (c == 'h') {
                    string2 = "hasSimpleContent";
                    n = 19;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 'i') break;
                string2 = "insertChildAfter";
                n = 15;
                break;
            }
            case 15: {
                string2 = "removeNamespace";
                n = 31;
                break;
            }
            case 14: {
                string2 = "hasOwnProperty";
                n = 17;
                break;
            }
            case 12: {
                char c = string.charAt(0);
                if (c == 'a') {
                    string2 = "addNamespace";
                    n = 2;
                    break;
                }
                if (c == 'p') {
                    string2 = "prependChild";
                    n = 28;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 's') break;
                char c2 = string.charAt(3);
                if (c2 == 'L') {
                    string2 = "setLocalName";
                    n = 34;
                    break;
                }
                n = 0;
                string2 = null;
                if (c2 != 'N') break;
                string2 = "setNamespace";
                n = 36;
                break;
            }
            case 11: {
                char c = string.charAt(0);
                if (c != 'a') {
                    if (c != 'c') {
                        if (c != 'd') {
                            if (c != 's') {
                                if (c != 't') {
                                    n = 0;
                                    string2 = null;
                                    break;
                                }
                                string2 = "toXMLString";
                                n = 40;
                                break;
                            }
                            string2 = "setChildren";
                            n = 33;
                            break;
                        }
                        string2 = "descendants";
                        n = 12;
                        break;
                    }
                    string2 = "constructor";
                    n = 1;
                    break;
                }
                string2 = "appendChild";
                n = 3;
                break;
            }
            case 10: {
                char c = string.charAt(0);
                if (c == 'a') {
                    string2 = "attributes";
                    n = 5;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 'c') break;
                string2 = "childIndex";
                n = 7;
                break;
            }
            case 9: {
                char c = string.charAt(2);
                if (c != 'c') {
                    if (c != 'm') {
                        if (c != 'r') {
                            if (c != 't') {
                                n = 0;
                                string2 = null;
                                break;
                            }
                            string2 = "attribute";
                            n = 4;
                            break;
                        }
                        string2 = "normalize";
                        n = 26;
                        break;
                    }
                    string2 = "namespace";
                    n = 23;
                    break;
                }
                string2 = "localName";
                n = 21;
                break;
            }
            case 8: {
                char c = string.charAt(2);
                if (c != 'S') {
                    if (c != 'i') {
                        if (c != 'd') {
                            if (c != 'e') {
                                if (c != 'm') {
                                    if (c != 'n') {
                                        n = 0;
                                        string2 = null;
                                        break;
                                    }
                                    string2 = "contains";
                                    n = 10;
                                    break;
                                }
                                string2 = "comments";
                                n = 9;
                                break;
                            }
                            string2 = "elements";
                            n = 13;
                            break;
                        }
                        string2 = "nodeKind";
                        n = 25;
                        break;
                    }
                    string2 = "children";
                    n = 8;
                    break;
                }
                char c3 = string.charAt(7);
                if (c3 == 'e') {
                    string2 = "toSource";
                    n = 39;
                    break;
                }
                n = 0;
                string2 = null;
                if (c3 != 'g') break;
                string2 = "toString";
                n = 38;
                break;
            }
            case 7: {
                char c = string.charAt(0);
                if (c == 'r') {
                    string2 = "replace";
                    n = 32;
                    break;
                }
                if (c == 's') {
                    string2 = "setName";
                    n = 35;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 'v') break;
                string2 = "valueOf";
                n = 41;
                break;
            }
            case 6: {
                char c = string.charAt(0);
                if (c == 'l') {
                    string2 = "length";
                    n = 20;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 'p') break;
                string2 = "parent";
                n = 27;
                break;
            }
            case 5: {
                string2 = "child";
                n = 6;
                break;
            }
            case 4: {
                char c = string.charAt(0);
                if (c == 'c') {
                    string2 = "copy";
                    n = 11;
                    break;
                }
                if (c == 'n') {
                    string2 = "name";
                    n = 22;
                    break;
                }
                n = 0;
                string2 = null;
                if (c != 't') break;
                string2 = "text";
                n = 37;
            }
        }
        if (string2 != null && string2 != string && !string2.equals((Object)string)) {
            n = 0;
        }
        return n;
    }

    @Override
    public Object get(String string, Scriptable scriptable) {
        Context context = Context.getCurrentContext();
        return this.getXMLProperty(this.lib.toXMLNameFromString(context, string));
    }

    @Override
    public final Object get(Context context, Object object) {
        XMLName xMLName;
        if (context == null) {
            context = Context.getCurrentContext();
        }
        if ((xMLName = this.lib.toXMLNameOrIndex(context, object)) == null) {
            Object object2 = this.get((int)ScriptRuntime.lastUint32Result(context), (Scriptable)this);
            if (object2 == Scriptable.NOT_FOUND) {
                object2 = Undefined.instance;
            }
            return object2;
        }
        return this.getXMLProperty(xMLName);
    }

    @Override
    public final Object getDefaultValue(Class<?> class_) {
        return this.toString();
    }

    @Override
    public Object getFunctionProperty(Context context, int n) {
        if (this.isPrototype()) {
            return super.get(n, (Scriptable)this);
        }
        Scriptable scriptable = this.getPrototype();
        if (scriptable instanceof XMLObject) {
            return ((XMLObject)scriptable).getFunctionProperty(context, n);
        }
        return NOT_FOUND;
    }

    @Override
    public Object getFunctionProperty(Context context, String string) {
        if (this.isPrototype()) {
            return super.get(string, (Scriptable)this);
        }
        Scriptable scriptable = this.getPrototype();
        if (scriptable instanceof XMLObject) {
            return ((XMLObject)scriptable).getFunctionProperty(context, string);
        }
        return NOT_FOUND;
    }

    XMLLibImpl getLib() {
        return this.lib;
    }

    @Override
    public final Scriptable getParentScope() {
        return super.getParentScope();
    }

    final XmlProcessor getProcessor() {
        return this.lib.getProcessor();
    }

    @Override
    public final Scriptable getPrototype() {
        return super.getPrototype();
    }

    abstract XML getXML();

    abstract Object getXMLProperty(XMLName var1);

    @Override
    public boolean has(String string, Scriptable scriptable) {
        Context context = Context.getCurrentContext();
        return this.hasXMLProperty(this.lib.toXMLNameFromString(context, string));
    }

    @Override
    public final boolean has(Context context, Object object) {
        XMLName xMLName;
        if (context == null) {
            context = Context.getCurrentContext();
        }
        if ((xMLName = this.lib.toXMLNameOrIndex(context, object)) == null) {
            return this.has((int)ScriptRuntime.lastUint32Result(context), (Scriptable)this);
        }
        return this.hasXMLProperty(xMLName);
    }

    abstract boolean hasComplexContent();

    @Override
    public final boolean hasInstance(Scriptable scriptable) {
        return super.hasInstance(scriptable);
    }

    abstract boolean hasOwnProperty(XMLName var1);

    abstract boolean hasSimpleContent();

    abstract boolean hasXMLProperty(XMLName var1);

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @Override
    protected void initPrototypeId(int var1_1) {
        switch (var1_1) {
            default: {
                throw new IllegalArgumentException(String.valueOf((int)var1_1));
            }
            case 41: {
                var4_2 = "valueOf";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 40: {
                var3_3 = 1;
                var4_2 = "toXMLString";
                ** GOTO lbl163
            }
            case 39: {
                var3_3 = 1;
                var4_2 = "toSource";
                ** GOTO lbl163
            }
            case 38: {
                var4_2 = "toString";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 37: {
                var4_2 = "text";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 36: {
                var3_3 = 1;
                var4_2 = "setNamespace";
                ** GOTO lbl163
            }
            case 35: {
                var3_3 = 1;
                var4_2 = "setName";
                ** GOTO lbl163
            }
            case 34: {
                var3_3 = 1;
                var4_2 = "setLocalName";
                ** GOTO lbl163
            }
            case 33: {
                var3_3 = 1;
                var4_2 = "setChildren";
                ** GOTO lbl163
            }
            case 32: {
                var3_3 = 2;
                var4_2 = "replace";
                ** GOTO lbl163
            }
            case 31: {
                var3_3 = 1;
                var4_2 = "removeNamespace";
                ** GOTO lbl163
            }
            case 30: {
                var3_3 = 1;
                var4_2 = "propertyIsEnumerable";
                ** GOTO lbl163
            }
            case 29: {
                var3_3 = 1;
                var4_2 = "processingInstructions";
                ** GOTO lbl163
            }
            case 28: {
                var3_3 = 1;
                var4_2 = "prependChild";
                ** GOTO lbl163
            }
            case 27: {
                var4_2 = "parent";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 26: {
                var4_2 = "normalize";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 25: {
                var4_2 = "nodeKind";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 24: {
                var4_2 = "namespaceDeclarations";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 23: {
                var3_3 = 1;
                var4_2 = "namespace";
                ** GOTO lbl163
            }
            case 22: {
                var4_2 = "name";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 21: {
                var4_2 = "localName";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 20: {
                var4_2 = "length";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 19: {
                var4_2 = "hasSimpleContent";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 18: {
                var4_2 = "hasComplexContent";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 17: {
                var3_3 = 1;
                var4_2 = "hasOwnProperty";
                ** GOTO lbl163
            }
            case 16: {
                var3_3 = 2;
                var4_2 = "insertChildBefore";
                ** GOTO lbl163
            }
            case 15: {
                var3_3 = 2;
                var4_2 = "insertChildAfter";
                ** GOTO lbl163
            }
            case 14: {
                var4_2 = "inScopeNamespaces";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 13: {
                var3_3 = 1;
                var4_2 = "elements";
                ** GOTO lbl163
            }
            case 12: {
                var3_3 = 1;
                var4_2 = "descendants";
                ** GOTO lbl163
            }
            case 11: {
                var4_2 = "copy";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 10: {
                var3_3 = 1;
                var4_2 = "contains";
                ** GOTO lbl163
            }
            case 9: {
                var4_2 = "comments";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 8: {
                var4_2 = "children";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 7: {
                var4_2 = "childIndex";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 6: {
                var3_3 = 1;
                var4_2 = "child";
                ** GOTO lbl163
            }
            case 5: {
                var4_2 = "attributes";
                var3_3 = 0;
                ** GOTO lbl163
            }
            case 4: {
                var3_3 = 1;
                var4_2 = "attribute";
                ** GOTO lbl163
            }
            case 3: {
                var3_3 = 1;
                var4_2 = "appendChild";
                ** GOTO lbl163
            }
            case 2: {
                var3_3 = 1;
                var4_2 = "addNamespace";
lbl163: // 40 sources:
                this.initPrototypeMethod(XMLObjectImpl.XMLOBJECT_TAG, var1_1, var4_2, var3_3);
                return;
            }
            case 1: 
        }
        var2_4 = this instanceof XML != false ? new XMLCtor((XML)this, XMLObjectImpl.XMLOBJECT_TAG, var1_1, 1) : new IdFunctionObject(this, XMLObjectImpl.XMLOBJECT_TAG, var1_1, 1);
        this.initPrototypeConstructor(var2_4);
    }

    final void initialize(XMLLibImpl xMLLibImpl, Scriptable scriptable, XMLObject xMLObject) {
        this.setParentScope(scriptable);
        this.setPrototype(xMLObject);
        boolean bl = xMLObject == null;
        this.prototypeFlag = bl;
        this.lib = xMLLibImpl;
    }

    final boolean isPrototype() {
        return this.prototypeFlag;
    }

    protected abstract Object jsConstructor(Context var1, boolean var2, Object[] var3);

    abstract int length();

    @Override
    public Ref memberRef(Context context, Object object, int n) {
        int n2 = n & 2;
        boolean bl = true;
        boolean bl2 = n2 != 0;
        if ((n & 4) == 0) {
            bl = false;
        }
        if (!bl2 && !bl) {
            throw Kit.codeBug();
        }
        XMLName xMLName = XMLName.create(this.lib.toNodeQName(context, object, bl2), bl2, bl);
        xMLName.initXMLObject(this);
        return xMLName;
    }

    @Override
    public Ref memberRef(Context context, Object object, Object object2, int n) {
        int n2 = n & 2;
        boolean bl = true;
        boolean bl2 = n2 != 0;
        if ((n & 4) == 0) {
            bl = false;
        }
        XMLName xMLName = XMLName.create(this.lib.toNodeQName(context, object, object2), bl2, bl);
        xMLName.initXMLObject(this);
        return xMLName;
    }

    final QName newQName(String string, String string2, String string3) {
        return this.lib.newQName(string, string2, string3);
    }

    final QName newQName(XmlNode.QName qName) {
        return this.lib.newQName(qName);
    }

    final XML newTextElementXML(XmlNode xmlNode, XmlNode.QName qName, String string) {
        return this.lib.newTextElementXML(xmlNode, qName, string);
    }

    final XML newXML(XmlNode xmlNode) {
        return this.lib.newXML(xmlNode);
    }

    final XML newXMLFromJs(Object object) {
        return this.lib.newXMLFromJs(object);
    }

    final XMLList newXMLList() {
        return this.lib.newXMLList();
    }

    final XMLList newXMLListFrom(Object object) {
        return this.lib.newXMLListFrom(object);
    }

    abstract void normalize();

    abstract Object parent();

    abstract XMLList processingInstructions(XMLName var1);

    abstract boolean propertyIsEnumerable(Object var1);

    @Override
    public void put(String string, Scriptable scriptable, Object object) {
        Context context = Context.getCurrentContext();
        this.putXMLProperty(this.lib.toXMLNameFromString(context, string), object);
    }

    @Override
    public final void put(Context context, Object object, Object object2) {
        XMLName xMLName;
        if (context == null) {
            context = Context.getCurrentContext();
        }
        if ((xMLName = this.lib.toXMLNameOrIndex(context, object)) == null) {
            this.put((int)ScriptRuntime.lastUint32Result(context), (Scriptable)this, object2);
            return;
        }
        this.putXMLProperty(xMLName, object2);
    }

    abstract void putXMLProperty(XMLName var1, Object var2);

    @Override
    public final void setParentScope(Scriptable scriptable) {
        super.setParentScope(scriptable);
    }

    @Override
    public final void setPrototype(Scriptable scriptable) {
        super.setPrototype(scriptable);
    }

    abstract XMLList text();

    abstract String toSource(int var1);

    public abstract String toString();

    abstract String toXMLString();

    abstract Object valueOf();

    XML xmlFromNode(XmlNode xmlNode) {
        if (xmlNode.getXml() == null) {
            xmlNode.setXml(this.newXML(xmlNode));
        }
        return xmlNode.getXml();
    }
}

