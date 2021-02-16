/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 */
package org.mozilla.javascript.xmlimpl;

import java.util.ArrayList;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XMLObjectImpl;
import org.mozilla.javascript.xmlimpl.XmlNode;
import org.mozilla.javascript.xmlimpl.XmlProcessor;

class XMLList
extends XMLObjectImpl
implements Function {
    static final long serialVersionUID = -4543618751670781135L;
    private XmlNode.InternalList _annos = new XmlNode.InternalList();
    private XMLObjectImpl targetObject = null;
    private XmlNode.QName targetProperty = null;

    XMLList(XMLLibImpl xMLLibImpl, Scriptable scriptable, XMLObject xMLObject) {
        super(xMLLibImpl, scriptable, xMLObject);
    }

    private Object applyOrCall(boolean bl, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        String string = bl ? "apply" : "call";
        if (scriptable2 instanceof XMLList && ((XMLList)scriptable2).targetProperty != null) {
            return ScriptRuntime.applyOrCall(bl, context, scriptable, scriptable2, arrobject);
        }
        throw ScriptRuntime.typeError1("msg.isnt.function", string);
    }

    private XMLList getPropertyList(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        boolean bl = xMLName.isDescendants();
        XmlNode.QName qName = null;
        if (!bl) {
            boolean bl2 = xMLName.isAttributeName();
            qName = null;
            if (!bl2) {
                qName = xMLName.toQname();
            }
        }
        xMLList.setTargets(this, qName);
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).getPropertyList(xMLName));
        }
        return xMLList;
    }

    private XML getXML(XmlNode.InternalList internalList, int n) {
        if (n >= 0 && n < this.length()) {
            return this.xmlFromNode(internalList.item(n));
        }
        return null;
    }

    private XML getXmlFromAnnotation(int n) {
        return this.getXML(this._annos, n);
    }

    private void insert(int n, XML xML) {
        if (n < this.length()) {
            XmlNode.InternalList internalList = new XmlNode.InternalList();
            internalList.add(this._annos, 0, n);
            internalList.add(xML);
            internalList.add(this._annos, n, this.length());
            this._annos = internalList;
        }
    }

    private void internalRemoveFromList(int n) {
        this._annos.remove(n);
    }

    private void replaceNode(XML xML, XML xML2) {
        xML.replaceWith(xML2);
    }

    private void setAttribute(XMLName xMLName, Object object) {
        for (int i = 0; i < this.length(); ++i) {
            this.getXmlFromAnnotation(i).setAttribute(xMLName, object);
        }
    }

    @Override
    void addMatches(XMLList xMLList, XMLName xMLName) {
        for (int i = 0; i < this.length(); ++i) {
            this.getXmlFromAnnotation(i).addMatches(xMLList, xMLName);
        }
    }

    void addToList(Object object) {
        this._annos.addToList(object);
    }

    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        RuntimeException runtimeException;
        XmlNode.QName qName = this.targetProperty;
        if (qName != null) {
            String string = qName.getLocalName();
            boolean bl = string.equals((Object)"apply");
            if (!bl && !string.equals((Object)"call")) {
                if (scriptable2 instanceof XMLObject) {
                    XMLObject xMLObject;
                    Object object = null;
                    Scriptable scriptable3 = scriptable2;
                    while (scriptable3 instanceof XMLObject && (object = (xMLObject = (XMLObject)scriptable3).getFunctionProperty(context, string)) == Scriptable.NOT_FOUND) {
                        scriptable3 = xMLObject.getExtraMethodSource(context);
                        if (scriptable3 == null) continue;
                        scriptable2 = scriptable3;
                        if (scriptable3 instanceof XMLObject) continue;
                        object = ScriptableObject.getProperty(scriptable3, string);
                    }
                    if (object instanceof Callable) {
                        return ((Callable)object).call(context, scriptable, scriptable2, arrobject);
                    }
                    throw ScriptRuntime.notFunctionError(scriptable2, object, string);
                }
                throw ScriptRuntime.typeError1("msg.incompat.call", string);
            }
            return this.applyOrCall(bl, context, scriptable, scriptable2, arrobject);
        }
        runtimeException = ScriptRuntime.notFunctionError(this);
        throw runtimeException;
    }

    @Override
    XMLList child(int n) {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).child(n));
        }
        return xMLList;
    }

    @Override
    XMLList child(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).child(xMLName));
        }
        return xMLList;
    }

    @Override
    XMLList children() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.length(); ++i) {
            XML xML = this.getXmlFromAnnotation(i);
            if (xML == null) continue;
            XMLList xMLList = xML.children();
            int n = xMLList.length();
            for (int j = 0; j < n; ++j) {
                arrayList.add((Object)xMLList.item(j));
            }
        }
        XMLList xMLList = this.newXMLList();
        int n = arrayList.size();
        for (int i = 0; i < n; ++i) {
            xMLList.addToList(arrayList.get(i));
        }
        return xMLList;
    }

    @Override
    XMLList comments() {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).comments());
        }
        return xMLList;
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        throw ScriptRuntime.typeError1("msg.not.ctor", "XMLList");
    }

    @Override
    boolean contains(Object object) {
        for (int i = 0; i < this.length(); ++i) {
            if (!this.getXmlFromAnnotation(i).equivalentXml(object)) continue;
            return true;
        }
        return false;
    }

    @Override
    XMLObjectImpl copy() {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).copy());
        }
        return xMLList;
    }

    @Override
    public void delete(int n) {
        if (n >= 0 && n < this.length()) {
            this.getXmlFromAnnotation(n).remove();
            this.internalRemoveFromList(n);
        }
    }

    @Override
    void deleteXMLProperty(XMLName xMLName) {
        for (int i = 0; i < this.length(); ++i) {
            XML xML = this.getXmlFromAnnotation(i);
            if (!xML.isElement()) continue;
            xML.deleteXMLProperty(xMLName);
        }
    }

    @Override
    XMLList elements(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).elements(xMLName));
        }
        return xMLList;
    }

    @Override
    boolean equivalentXml(Object object) {
        if (object instanceof Undefined && this.length() == 0) {
            return true;
        }
        if (this.length() == 1) {
            return this.getXmlFromAnnotation(0).equivalentXml(object);
        }
        boolean bl = object instanceof XMLList;
        boolean bl2 = false;
        if (bl) {
            XMLList xMLList = (XMLList)object;
            int n = xMLList.length();
            int n2 = this.length();
            bl2 = false;
            if (n == n2) {
                bl2 = true;
                for (int i = 0; i < this.length(); ++i) {
                    if (this.getXmlFromAnnotation(i).equivalentXml(xMLList.getXmlFromAnnotation(i))) continue;
                    return false;
                }
            }
        }
        return bl2;
    }

    @Override
    public Object get(int n, Scriptable scriptable) {
        if (n >= 0 && n < this.length()) {
            return this.getXmlFromAnnotation(n);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override
    public String getClassName() {
        return "XMLList";
    }

    @Override
    public Scriptable getExtraMethodSource(Context context) {
        if (this.length() == 1) {
            return this.getXmlFromAnnotation(0);
        }
        return null;
    }

    @Override
    public Object[] getIds() {
        if (this.isPrototype()) {
            return new Object[0];
        }
        Object[] arrobject = new Object[this.length()];
        for (int i = 0; i < arrobject.length; ++i) {
            arrobject[i] = i;
        }
        return arrobject;
    }

    public Object[] getIdsForDebug() {
        return this.getIds();
    }

    XmlNode.InternalList getNodeList() {
        return this._annos;
    }

    @Override
    XML getXML() {
        if (this.length() == 1) {
            return this.getXmlFromAnnotation(0);
        }
        return null;
    }

    @Override
    Object getXMLProperty(XMLName xMLName) {
        return this.getPropertyList(xMLName);
    }

    @Override
    public boolean has(int n, Scriptable scriptable) {
        return n >= 0 && n < this.length();
    }

    @Override
    boolean hasComplexContent() {
        int n = this.length();
        if (n == 0) {
            return false;
        }
        if (n == 1) {
            return this.getXmlFromAnnotation(0).hasComplexContent();
        }
        for (int i = 0; i < n; ++i) {
            if (!this.getXmlFromAnnotation(i).isElement()) continue;
            return true;
        }
        return false;
    }

    @Override
    boolean hasOwnProperty(XMLName xMLName) {
        if (this.isPrototype()) {
            return this.findPrototypeId(xMLName.localName()) != 0;
        }
        return this.getPropertyList(xMLName).length() > 0;
    }

    @Override
    boolean hasSimpleContent() {
        if (this.length() == 0) {
            return true;
        }
        if (this.length() == 1) {
            return this.getXmlFromAnnotation(0).hasSimpleContent();
        }
        for (int i = 0; i < this.length(); ++i) {
            if (!this.getXmlFromAnnotation(i).isElement()) continue;
            return false;
        }
        return true;
    }

    @Override
    boolean hasXMLProperty(XMLName xMLName) {
        return this.getPropertyList(xMLName).length() > 0;
    }

    XML item(int n) {
        if (this._annos != null) {
            return this.getXmlFromAnnotation(n);
        }
        return this.createEmptyXML();
    }

    @Override
    protected Object jsConstructor(Context context, boolean bl, Object[] arrobject) {
        if (arrobject.length == 0) {
            return this.newXMLList();
        }
        Object object = arrobject[0];
        if (!bl && object instanceof XMLList) {
            return object;
        }
        return this.newXMLListFrom(object);
    }

    @Override
    int length() {
        XmlNode.InternalList internalList = this._annos;
        int n = 0;
        if (internalList != null) {
            n = internalList.length();
        }
        return n;
    }

    @Override
    void normalize() {
        for (int i = 0; i < this.length(); ++i) {
            this.getXmlFromAnnotation(i).normalize();
        }
    }

    @Override
    Object parent() {
        if (this.length() == 0) {
            return Undefined.instance;
        }
        XML xML = null;
        for (int i = 0; i < this.length(); ++i) {
            Object object = this.getXmlFromAnnotation(i).parent();
            if (!(object instanceof XML)) {
                return Undefined.instance;
            }
            XML xML2 = (XML)object;
            if (i == 0) {
                xML = xML2;
                continue;
            }
            if (xML.is(xML2)) {
                continue;
            }
            return Undefined.instance;
        }
        return xML;
    }

    @Override
    XMLList processingInstructions(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).processingInstructions(xMLName));
        }
        return xMLList;
    }

    @Override
    boolean propertyIsEnumerable(Object object) {
        long l;
        if (object instanceof Integer) {
            l = ((Integer)object).intValue();
        } else if (object instanceof Number) {
            double d = ((Number)object).doubleValue();
            long l2 = (long)d;
            if ((double)l2 != d) {
                return false;
            }
            if (l2 == 0L && 1.0 / d < 0.0) {
                return false;
            }
            l = l2;
        } else {
            l = ScriptRuntime.testUint32String(ScriptRuntime.toString(object));
        }
        long l3 = 0L LCMP l;
        boolean bl = false;
        if (l3 <= 0) {
            long l4 = l LCMP (long)this.length();
            bl = false;
            if (l4 < 0) {
                bl = true;
            }
        }
        return bl;
    }

    @Override
    public void put(int n, Scriptable scriptable, Object object) {
        Object object2;
        XMLObject xMLObject;
        if (object == null) {
            object = "null";
        } else if (object instanceof Undefined) {
            object = "undefined";
        }
        if (object instanceof XMLObject) {
            xMLObject = (XMLObject)object;
        } else if (this.targetProperty == null) {
            xMLObject = this.newXMLFromJs(object.toString());
        } else {
            xMLObject = this.item(n);
            if (xMLObject == null) {
                XML xML = this.item(0);
                XMLObjectImpl xMLObjectImpl = xML == null ? this.newTextElementXML(null, this.targetProperty, null) : xML.copy();
                xMLObject = xMLObjectImpl;
            }
            ((XML)xMLObject).setChildren(object);
        }
        if (n < this.length()) {
            object2 = this.item(n).parent();
        } else if (this.length() == 0) {
            XMLObjectImpl xMLObjectImpl = this.targetObject;
            Object object3 = xMLObjectImpl != null ? xMLObjectImpl.getXML() : this.parent();
            object2 = object3;
        } else {
            object2 = this.parent();
        }
        if (object2 instanceof XML) {
            XML xML = (XML)object2;
            if (n < this.length()) {
                XMLList xMLList;
                XML xML2 = this.getXmlFromAnnotation(n);
                if (xMLObject instanceof XML) {
                    this.replaceNode(xML2, (XML)xMLObject);
                    this.replace(n, xML2);
                } else if (xMLObject instanceof XMLList && (xMLList = (XMLList)xMLObject).length() > 0) {
                    int n2 = xML2.childIndex();
                    this.replaceNode(xML2, xMLList.item(0));
                    this.replace(n, xMLList.item(0));
                    for (int i = 1; i < xMLList.length(); ++i) {
                        xML.insertChildAfter(xML.getXmlChild(n2), xMLList.item(i));
                        ++n2;
                        this.insert(n + i, xMLList.item(i));
                    }
                }
            } else {
                xML.appendChild(xMLObject);
                this.addToList(xML.getLastXmlChild());
            }
            return;
        }
        if (n < this.length()) {
            XMLList xMLList;
            XML xML = this.getXML(this._annos, n);
            if (xMLObject instanceof XML) {
                this.replaceNode(xML, (XML)xMLObject);
                this.replace(n, xML);
            } else if (xMLObject instanceof XMLList && (xMLList = (XMLList)xMLObject).length() > 0) {
                this.replaceNode(xML, xMLList.item(0));
                this.replace(n, xMLList.item(0));
                for (int i = 1; i < xMLList.length(); ++i) {
                    this.insert(n + i, xMLList.item(i));
                }
            }
            return;
        }
        this.addToList(xMLObject);
    }

    @Override
    void putXMLProperty(XMLName xMLName, Object object) {
        if (object == null) {
            object = "null";
        } else if (object instanceof Undefined) {
            object = "undefined";
        }
        if (this.length() <= 1) {
            if (this.length() == 0) {
                XmlNode.QName qName;
                if (this.targetObject != null && (qName = this.targetProperty) != null && qName.getLocalName() != null && this.targetProperty.getLocalName().length() > 0) {
                    this.addToList(this.newTextElementXML(null, this.targetProperty, null));
                    if (xMLName.isAttributeName()) {
                        this.setAttribute(xMLName, object);
                    } else {
                        this.item(0).putXMLProperty(xMLName, object);
                        this.replace(0, this.item(0));
                    }
                    XMLName xMLName2 = XMLName.formProperty(this.targetProperty.getNamespace().getUri(), this.targetProperty.getLocalName());
                    this.targetObject.putXMLProperty(xMLName2, this);
                    this.replace(0, this.targetObject.getXML().getLastXmlChild());
                    return;
                }
                throw ScriptRuntime.typeError("Assignment to empty XMLList without targets not supported");
            }
            if (xMLName.isAttributeName()) {
                this.setAttribute(xMLName, object);
                return;
            }
            this.item(0).putXMLProperty(xMLName, object);
            this.replace(0, this.item(0));
            return;
        }
        throw ScriptRuntime.typeError("Assignment to lists with more than one item is not supported");
    }

    void remove() {
        for (int i = -1 + this.length(); i >= 0; --i) {
            XML xML = this.getXmlFromAnnotation(i);
            if (xML == null) continue;
            xML.remove();
            this.internalRemoveFromList(i);
        }
    }

    void replace(int n, XML xML) {
        if (n < this.length()) {
            XmlNode.InternalList internalList = new XmlNode.InternalList();
            internalList.add(this._annos, 0, n);
            internalList.add(xML);
            internalList.add(this._annos, n + 1, this.length());
            this._annos = internalList;
        }
    }

    void setTargets(XMLObjectImpl xMLObjectImpl, XmlNode.QName qName) {
        this.targetObject = xMLObjectImpl;
        this.targetProperty = qName;
    }

    @Override
    XMLList text() {
        XMLList xMLList = this.newXMLList();
        for (int i = 0; i < this.length(); ++i) {
            xMLList.addToList(this.getXmlFromAnnotation(i).text());
        }
        return xMLList;
    }

    @Override
    String toSource(int n) {
        return this.toXMLString();
    }

    @Override
    public String toString() {
        if (this.hasSimpleContent()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < this.length(); ++i) {
                XML xML = this.getXmlFromAnnotation(i);
                if (xML.isComment() || xML.isProcessingInstruction()) continue;
                stringBuilder.append(xML.toString());
            }
            return stringBuilder.toString();
        }
        return this.toXMLString();
    }

    @Override
    String toXMLString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.length(); ++i) {
            if (this.getProcessor().isPrettyPrinting() && i != 0) {
                stringBuilder.append('\n');
            }
            stringBuilder.append(this.getXmlFromAnnotation(i).toXMLString());
        }
        return stringBuilder.toString();
    }

    @Override
    Object valueOf() {
        return this;
    }
}

