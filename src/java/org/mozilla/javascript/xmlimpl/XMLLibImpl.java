/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.Boolean
 *  java.lang.Deprecated
 *  java.lang.IllegalArgumentException
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  org.mozilla.javascript.xml.XMLObject
 *  org.mozilla.javascript.xmlimpl.Namespace
 *  org.mozilla.javascript.xmlimpl.QName
 *  org.mozilla.javascript.xmlimpl.XML
 *  org.mozilla.javascript.xmlimpl.XMLList
 *  org.mozilla.javascript.xmlimpl.XMLObjectImpl
 *  org.w3c.dom.Node
 *  org.xml.sax.SAXException
 */
package org.mozilla.javascript.xmlimpl;

import java.io.Serializable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.xml.XMLLib;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.Namespace;
import org.mozilla.javascript.xmlimpl.QName;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XMLObjectImpl;
import org.mozilla.javascript.xmlimpl.XMLWithScope;
import org.mozilla.javascript.xmlimpl.XmlNode;
import org.mozilla.javascript.xmlimpl.XmlProcessor;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class XMLLibImpl
extends XMLLib
implements Serializable {
    private static final long serialVersionUID = 1L;
    private Scriptable globalScope;
    private Namespace namespacePrototype;
    private XmlProcessor options = new XmlProcessor();
    private QName qnamePrototype;
    private XMLList xmlListPrototype;
    private XML xmlPrototype;

    private XMLLibImpl(Scriptable scriptable) {
        this.globalScope = scriptable;
    }

    private static RuntimeException badXMLName(Object object) {
        String string2;
        if (object instanceof Number) {
            string2 = "Can not construct XML name from number: ";
        } else if (object instanceof Boolean) {
            string2 = "Can not construct XML name from boolean: ";
        } else {
            if (object != Undefined.instance && object != null) {
                throw new IllegalArgumentException(object.toString());
            }
            string2 = "Can not construct XML name from ";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        stringBuilder.append(ScriptRuntime.toString(object));
        return ScriptRuntime.typeError(stringBuilder.toString());
    }

    private void exportToScope(boolean bl) {
        this.xmlPrototype = this.newXML(XmlNode.createText(this.options, ""));
        this.xmlListPrototype = this.newXMLList();
        this.namespacePrototype = Namespace.create((Scriptable)this.globalScope, null, (XmlNode.Namespace)XmlNode.Namespace.GLOBAL);
        this.qnamePrototype = QName.create((XMLLibImpl)this, (Scriptable)this.globalScope, null, (XmlNode.QName)XmlNode.QName.create(XmlNode.Namespace.create(""), ""));
        this.xmlPrototype.exportAsJSClass(bl);
        this.xmlListPrototype.exportAsJSClass(bl);
        this.namespacePrototype.exportAsJSClass(bl);
        this.qnamePrototype.exportAsJSClass(bl);
    }

    private String getDefaultNamespaceURI(Context context) {
        return this.getDefaultNamespace(context).uri();
    }

    public static void init(Context context, Scriptable scriptable, boolean bl) {
        XMLLibImpl xMLLibImpl = new XMLLibImpl(scriptable);
        if (xMLLibImpl.bindToScope(scriptable) == xMLLibImpl) {
            xMLLibImpl.exportToScope(bl);
        }
    }

    private XML parse(String string2) {
        try {
            XML xML = this.newXML(XmlNode.createElement(this.options, this.getDefaultNamespaceURI(Context.getCurrentContext()), string2));
            return xML;
        }
        catch (SAXException sAXException) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot parse XML: ");
            stringBuilder.append(sAXException.getMessage());
            throw ScriptRuntime.typeError(stringBuilder.toString());
        }
    }

    public static Node toDomNode(Object object) {
        if (object instanceof XML) {
            return ((XML)object).toDomNode();
        }
        throw new IllegalArgumentException("xmlObject is not an XML object in JavaScript.");
    }

    private Ref xmlPrimaryReference(Context context, XMLName xMLName, Scriptable scriptable) {
        XMLObjectImpl xMLObjectImpl;
        block2 : {
            XMLObjectImpl xMLObjectImpl2 = null;
            do {
                if (!(scriptable instanceof XMLWithScope)) continue;
                xMLObjectImpl = (XMLObjectImpl)scriptable.getPrototype();
                if (xMLObjectImpl.hasXMLProperty(xMLName)) break block2;
                if (xMLObjectImpl2 != null) continue;
                xMLObjectImpl2 = xMLObjectImpl;
            } while ((scriptable = scriptable.getParentScope()) != null);
            xMLObjectImpl = xMLObjectImpl2;
        }
        if (xMLObjectImpl != null) {
            xMLName.initXMLObject(xMLObjectImpl);
        }
        return xMLName;
    }

    Object addXMLObjects(Context context, XMLObject xMLObject, XMLObject xMLObject2) {
        XMLList xMLList = this.newXMLList();
        if (xMLObject instanceof XMLList) {
            XMLList xMLList2 = (XMLList)xMLObject;
            if (xMLList2.length() == 1) {
                xMLList.addToList((Object)xMLList2.item(0));
            } else {
                xMLList = this.newXMLListFrom((Object)xMLObject);
            }
        } else {
            xMLList.addToList((Object)xMLObject);
        }
        if (xMLObject2 instanceof XMLList) {
            XMLList xMLList3 = (XMLList)xMLObject2;
            for (int i = 0; i < xMLList3.length(); ++i) {
                xMLList.addToList((Object)xMLList3.item(i));
            }
        } else if (xMLObject2 instanceof XML) {
            xMLList.addToList((Object)xMLObject2);
            return xMLList;
        }
        return xMLList;
    }

    Namespace castToNamespace(Context context, Object object) {
        return this.namespacePrototype.castToNamespace(object);
    }

    QName castToQName(Context context, Object object) {
        return this.qnamePrototype.castToQName(this, context, object);
    }

    QName constructQName(Context context, Object object) {
        return this.qnamePrototype.constructQName(this, context, object);
    }

    QName constructQName(Context context, Object object, Object object2) {
        return this.qnamePrototype.constructQName(this, context, object, object2);
    }

    Namespace[] createNamespaces(XmlNode.Namespace[] arrnamespace) {
        Namespace[] arrnamespace2 = new Namespace[arrnamespace.length];
        for (int i = 0; i < arrnamespace.length; ++i) {
            arrnamespace2[i] = this.namespacePrototype.newNamespace(arrnamespace[i].getPrefix(), arrnamespace[i].getUri());
        }
        return arrnamespace2;
    }

    final XML ecmaToXml(Object object) {
        if (object != null && object != Undefined.instance) {
            if (object instanceof XML) {
                return (XML)object;
            }
            if (object instanceof XMLList) {
                XMLList xMLList = (XMLList)object;
                if (xMLList.getXML() != null) {
                    return xMLList.getXML();
                }
                throw ScriptRuntime.typeError("Cannot convert list of >1 element to XML");
            }
            if (object instanceof Wrapper) {
                object = ((Wrapper)object).unwrap();
            }
            if (object instanceof Node) {
                return this.newXML(XmlNode.createElementFromNode((Node)object));
            }
            String string2 = ScriptRuntime.toString(object);
            if (string2.length() > 0 && string2.charAt(0) == '<') {
                return this.parse(string2);
            }
            return this.newXML(XmlNode.createText(this.options, string2));
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot convert ");
        stringBuilder.append(object);
        stringBuilder.append(" to XML");
        throw ScriptRuntime.typeError(stringBuilder.toString());
    }

    @Override
    public String escapeAttributeValue(Object object) {
        return this.options.escapeAttributeValue(object);
    }

    @Override
    public String escapeTextValue(Object object) {
        return this.options.escapeTextValue(object);
    }

    Namespace getDefaultNamespace(Context context) {
        if (context == null && (context = Context.getCurrentContext()) == null) {
            return this.namespacePrototype;
        }
        Object object = ScriptRuntime.searchDefaultNamespace(context);
        if (object == null) {
            return this.namespacePrototype;
        }
        if (object instanceof Namespace) {
            return (Namespace)object;
        }
        return this.namespacePrototype;
    }

    @Override
    public int getPrettyIndent() {
        return this.options.getPrettyIndent();
    }

    XmlProcessor getProcessor() {
        return this.options;
    }

    @Deprecated
    Scriptable globalScope() {
        return this.globalScope;
    }

    @Override
    public boolean isIgnoreComments() {
        return this.options.isIgnoreComments();
    }

    @Override
    public boolean isIgnoreProcessingInstructions() {
        return this.options.isIgnoreProcessingInstructions();
    }

    @Override
    public boolean isIgnoreWhitespace() {
        return this.options.isIgnoreWhitespace();
    }

    @Override
    public boolean isPrettyPrinting() {
        return this.options.isPrettyPrinting();
    }

    @Override
    public boolean isXMLName(Context context, Object object) {
        return XMLName.accept(object);
    }

    @Override
    public Ref nameRef(Context context, Object object, Object object2, Scriptable scriptable, int n) {
        XMLName xMLName = XMLName.create(this.toNodeQName(context, object, object2), false, false);
        if ((n & 2) != 0 && !xMLName.isAttributeName()) {
            xMLName.setAttributeName();
        }
        return this.xmlPrimaryReference(context, xMLName, scriptable);
    }

    @Override
    public Ref nameRef(Context context, Object object, Scriptable scriptable, int n) {
        if ((n & 2) != 0) {
            return this.xmlPrimaryReference(context, this.toAttributeName(context, object), scriptable);
        }
        throw Kit.codeBug();
    }

    Namespace newNamespace(String string2) {
        return this.namespacePrototype.newNamespace(string2);
    }

    QName newQName(String string2, String string3, String string4) {
        return this.qnamePrototype.newQName(this, string2, string3, string4);
    }

    QName newQName(XmlNode.QName qName) {
        return QName.create((XMLLibImpl)this, (Scriptable)this.globalScope, (QName)this.qnamePrototype, (XmlNode.QName)qName);
    }

    final XML newTextElementXML(XmlNode xmlNode, XmlNode.QName qName, String string2) {
        return this.newXML(XmlNode.newElementWithText(this.options, xmlNode, qName, string2));
    }

    XML newXML(XmlNode xmlNode) {
        return new XML(this, this.globalScope, (XMLObject)this.xmlPrototype, xmlNode);
    }

    final XML newXMLFromJs(Object object) {
        String string2 = object != null && object != Undefined.instance ? (object instanceof XMLObjectImpl ? ((XMLObjectImpl)object).toXMLString() : ScriptRuntime.toString(object)) : "";
        if (!string2.trim().startsWith("<>")) {
            if (string2.indexOf("<") == -1) {
                return this.newXML(XmlNode.createText(this.options, string2));
            }
            return this.parse(string2);
        }
        throw ScriptRuntime.typeError("Invalid use of XML object anonymous tags <></>.");
    }

    XMLList newXMLList() {
        return new XMLList(this, this.globalScope, (XMLObject)this.xmlListPrototype);
    }

    final XMLList newXMLListFrom(Object object) {
        XMLList xMLList = this.newXMLList();
        if (object != null) {
            if (object instanceof Undefined) {
                return xMLList;
            }
            if (object instanceof XML) {
                XML xML = (XML)object;
                xMLList.getNodeList().add(xML);
                return xMLList;
            }
            if (object instanceof XMLList) {
                XMLList xMLList2 = (XMLList)object;
                xMLList.getNodeList().add(xMLList2.getNodeList());
                return xMLList;
            }
            String string2 = ScriptRuntime.toString(object).trim();
            if (!string2.startsWith("<>")) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("<>");
                stringBuilder.append(string2);
                stringBuilder.append("</>");
                string2 = stringBuilder.toString();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<fragment>");
            stringBuilder.append(string2.substring(2));
            String string3 = stringBuilder.toString();
            if (string3.endsWith("</>")) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(string3.substring(0, -3 + string3.length()));
                stringBuilder2.append("</fragment>");
                XMLList xMLList3 = this.newXMLFromJs(stringBuilder2.toString()).children();
                for (int i = 0; i < xMLList3.getNodeList().length(); ++i) {
                    xMLList.getNodeList().add((XML)xMLList3.item(i).copy());
                }
                return xMLList;
            }
            throw ScriptRuntime.typeError("XML with anonymous tag missing end anonymous tag");
        }
        return xMLList;
    }

    @Deprecated
    QName qnamePrototype() {
        return this.qnamePrototype;
    }

    @Override
    public void setIgnoreComments(boolean bl) {
        this.options.setIgnoreComments(bl);
    }

    @Override
    public void setIgnoreProcessingInstructions(boolean bl) {
        this.options.setIgnoreProcessingInstructions(bl);
    }

    @Override
    public void setIgnoreWhitespace(boolean bl) {
        this.options.setIgnoreWhitespace(bl);
    }

    @Override
    public void setPrettyIndent(int n) {
        this.options.setPrettyIndent(n);
    }

    @Override
    public void setPrettyPrinting(boolean bl) {
        this.options.setPrettyPrinting(bl);
    }

    @Deprecated
    XMLName toAttributeName(Context context, Object object) {
        if (object instanceof XMLName) {
            return (XMLName)object;
        }
        if (object instanceof QName) {
            return XMLName.create(((QName)object).getDelegate(), true, false);
        }
        if (!(object instanceof Boolean) && !(object instanceof Number) && object != Undefined.instance && object != null) {
            String string2 = object instanceof String ? (String)object : ScriptRuntime.toString(object);
            if (string2 != null && string2.equals((Object)"*")) {
                string2 = null;
            }
            return XMLName.create(XmlNode.QName.create(XmlNode.Namespace.create(""), string2), true, false);
        }
        throw XMLLibImpl.badXMLName(object);
    }

    @Override
    public Object toDefaultXmlNamespace(Context context, Object object) {
        return this.namespacePrototype.constructNamespace(object);
    }

    XmlNode.QName toNodeQName(Context context, Object object, Object object2) {
        String string2 = object2 instanceof QName ? ((QName)object2).localName() : ScriptRuntime.toString(object2);
        XmlNode.Namespace namespace = object == Undefined.instance ? ("*".equals((Object)string2) ? null : this.getDefaultNamespace(context).getDelegate()) : (object == null ? null : (object instanceof Namespace ? ((Namespace)object).getDelegate() : this.namespacePrototype.constructNamespace(object).getDelegate()));
        if (string2 != null && string2.equals((Object)"*")) {
            string2 = null;
        }
        return XmlNode.QName.create(namespace, string2);
    }

    XmlNode.QName toNodeQName(Context context, Object object, boolean bl) {
        if (object instanceof XMLName) {
            return ((XMLName)object).toQname();
        }
        if (object instanceof QName) {
            return ((QName)object).getDelegate();
        }
        if (!(object instanceof Boolean) && !(object instanceof Number) && object != Undefined.instance && object != null) {
            String string2 = object instanceof String ? (String)object : ScriptRuntime.toString(object);
            return this.toNodeQName(context, string2, bl);
        }
        throw XMLLibImpl.badXMLName(object);
    }

    XmlNode.QName toNodeQName(Context context, String string2, boolean bl) {
        XmlNode.Namespace namespace = this.getDefaultNamespace(context).getDelegate();
        if (string2 != null && string2.equals((Object)"*")) {
            return XmlNode.QName.create(null, null);
        }
        if (bl) {
            return XmlNode.QName.create(XmlNode.Namespace.GLOBAL, string2);
        }
        return XmlNode.QName.create(namespace, string2);
    }

    XMLName toXMLName(Context context, Object object) {
        if (object instanceof XMLName) {
            return (XMLName)object;
        }
        if (object instanceof QName) {
            QName qName = (QName)object;
            return XMLName.formProperty(qName.uri(), qName.localName());
        }
        if (object instanceof String) {
            return this.toXMLNameFromString(context, (String)object);
        }
        if (!(object instanceof Boolean) && !(object instanceof Number) && object != Undefined.instance && object != null) {
            return this.toXMLNameFromString(context, ScriptRuntime.toString(object));
        }
        throw XMLLibImpl.badXMLName(object);
    }

    XMLName toXMLNameFromString(Context context, String string2) {
        return XMLName.create(this.getDefaultNamespaceURI(context), string2);
    }

    XMLName toXMLNameOrIndex(Context context, Object object) {
        if (object instanceof XMLName) {
            return (XMLName)object;
        }
        if (object instanceof String) {
            XMLName xMLName;
            String string2 = (String)object;
            long l = ScriptRuntime.testUint32String(string2);
            if (l >= 0L) {
                ScriptRuntime.storeUint32Result(context, l);
                xMLName = null;
            } else {
                xMLName = this.toXMLNameFromString(context, string2);
            }
            return xMLName;
        }
        if (object instanceof Number) {
            double d = ((Number)object).doubleValue();
            long l = (long)d;
            if ((double)l == d && 0L <= l && l <= 0xFFFFFFFFL) {
                ScriptRuntime.storeUint32Result(context, l);
                return null;
            }
            throw XMLLibImpl.badXMLName(object);
        }
        if (object instanceof QName) {
            QName qName = (QName)object;
            String string3 = qName.uri();
            boolean bl = false;
            if (string3 != null) {
                int n = string3.length();
                bl = false;
                if (n == 0) {
                    long l = ScriptRuntime.testUint32String(string3);
                    long l2 = l LCMP 0L;
                    bl = false;
                    if (l2 >= 0) {
                        ScriptRuntime.storeUint32Result(context, l);
                        bl = true;
                    }
                }
            }
            XMLName xMLName = !bl ? XMLName.formProperty(string3, qName.localName()) : null;
            return xMLName;
        }
        if (!(object instanceof Boolean) && object != Undefined.instance && object != null) {
            String string4 = ScriptRuntime.toString(object);
            long l = ScriptRuntime.testUint32String(string4);
            if (l >= 0L) {
                ScriptRuntime.storeUint32Result(context, l);
                return null;
            }
            return this.toXMLNameFromString(context, string4);
        }
        throw XMLLibImpl.badXMLName(object);
    }
}

