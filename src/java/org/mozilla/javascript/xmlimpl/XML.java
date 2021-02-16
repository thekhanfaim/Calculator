/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  org.w3c.dom.Node
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;
import org.mozilla.javascript.xmlimpl.Namespace;
import org.mozilla.javascript.xmlimpl.QName;
import org.mozilla.javascript.xmlimpl.XMLLibImpl;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XMLObjectImpl;
import org.mozilla.javascript.xmlimpl.XmlNode;
import org.mozilla.javascript.xmlimpl.XmlProcessor;
import org.w3c.dom.Node;

class XML
extends XMLObjectImpl {
    static final long serialVersionUID = -630969919086449092L;
    private XmlNode node;

    XML(XMLLibImpl xMLLibImpl, Scriptable scriptable, XMLObject xMLObject, XmlNode xmlNode) {
        super(xMLLibImpl, scriptable, xMLObject);
        this.initialize(xmlNode);
    }

    private XmlNode.Namespace adapt(Namespace namespace) {
        if (namespace.prefix() == null) {
            return XmlNode.Namespace.create(namespace.uri());
        }
        return XmlNode.Namespace.create(namespace.prefix(), namespace.uri());
    }

    private void addInScopeNamespace(Namespace namespace) {
        if (!this.isElement()) {
            return;
        }
        if (namespace.prefix() != null) {
            if (namespace.prefix().length() == 0 && namespace.uri().length() == 0) {
                return;
            }
            if (this.node.getQname().getNamespace().getPrefix().equals((Object)namespace.prefix())) {
                this.node.invalidateNamespacePrefix();
            }
            this.node.declareNamespace(namespace.prefix(), namespace.uri());
            return;
        }
    }

    private String ecmaToString() {
        if (!this.isAttribute() && !this.isText()) {
            if (this.hasSimpleContent()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < this.node.getChildCount(); ++i) {
                    XmlNode xmlNode = this.node.getChild(i);
                    if (xmlNode.isProcessingInstructionType() || xmlNode.isCommentType()) continue;
                    stringBuilder.append(new XML(this.getLib(), this.getParentScope(), (XMLObject)this.getPrototype(), xmlNode).toString());
                }
                return stringBuilder.toString();
            }
            return this.toXMLString();
        }
        return this.ecmaValue();
    }

    private String ecmaValue() {
        return this.node.ecmaValue();
    }

    private int getChildIndexOf(XML xML) {
        for (int i = 0; i < this.node.getChildCount(); ++i) {
            if (!this.node.getChild(i).isSameNode(xML.node)) continue;
            return i;
        }
        return -1;
    }

    private XmlNode[] getNodesForInsert(Object object) {
        if (object instanceof XML) {
            XmlNode[] arrxmlNode = new XmlNode[]{((XML)object).node};
            return arrxmlNode;
        }
        if (object instanceof XMLList) {
            XMLList xMLList = (XMLList)object;
            XmlNode[] arrxmlNode = new XmlNode[xMLList.length()];
            for (int i = 0; i < xMLList.length(); ++i) {
                arrxmlNode[i] = xMLList.item((int)i).node;
            }
            return arrxmlNode;
        }
        XmlNode[] arrxmlNode = new XmlNode[]{XmlNode.createText(this.getProcessor(), ScriptRuntime.toString(object))};
        return arrxmlNode;
    }

    private XML toXML(XmlNode xmlNode) {
        if (xmlNode.getXml() == null) {
            xmlNode.setXml(this.newXML(xmlNode));
        }
        return xmlNode.getXml();
    }

    @Override
    void addMatches(XMLList xMLList, XMLName xMLName) {
        xMLName.addMatches(xMLList, this);
    }

    XML addNamespace(Namespace namespace) {
        this.addInScopeNamespace(namespace);
        return this;
    }

    XML appendChild(Object object) {
        if (this.node.isParentType()) {
            XmlNode[] arrxmlNode = this.getNodesForInsert(object);
            XmlNode xmlNode = this.node;
            xmlNode.insertChildrenAt(xmlNode.getChildCount(), arrxmlNode);
        }
        return this;
    }

    @Override
    XMLList child(int n) {
        XMLList xMLList = this.newXMLList();
        xMLList.setTargets(this, null);
        if (n >= 0 && n < this.node.getChildCount()) {
            xMLList.addToList(this.getXmlChild(n));
        }
        return xMLList;
    }

    @Override
    XMLList child(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        XmlNode[] arrxmlNode = this.node.getMatchingChildren(XmlNode.Filter.ELEMENT);
        for (int i = 0; i < arrxmlNode.length; ++i) {
            if (!xMLName.matchesElement(arrxmlNode[i].getQname())) continue;
            xMLList.addToList(this.toXML(arrxmlNode[i]));
        }
        xMLList.setTargets(this, xMLName.toQname());
        return xMLList;
    }

    int childIndex() {
        return this.node.getChildIndex();
    }

    @Override
    XMLList children() {
        XMLList xMLList = this.newXMLList();
        xMLList.setTargets(this, XMLName.formStar().toQname());
        XmlNode[] arrxmlNode = this.node.getMatchingChildren(XmlNode.Filter.TRUE);
        for (int i = 0; i < arrxmlNode.length; ++i) {
            xMLList.addToList(this.toXML(arrxmlNode[i]));
        }
        return xMLList;
    }

    @Override
    XMLList comments() {
        XMLList xMLList = this.newXMLList();
        this.node.addMatchingChildren(xMLList, XmlNode.Filter.COMMENT);
        return xMLList;
    }

    @Override
    boolean contains(Object object) {
        if (object instanceof XML) {
            return this.equivalentXml(object);
        }
        return false;
    }

    @Override
    XMLObjectImpl copy() {
        return this.newXML(this.node.copy());
    }

    @Override
    public void delete(int n) {
        if (n == 0) {
            this.remove();
        }
    }

    @Override
    void deleteXMLProperty(XMLName xMLName) {
        XMLList xMLList = this.getPropertyList(xMLName);
        for (int i = 0; i < xMLList.length(); ++i) {
            xMLList.item((int)i).node.deleteMe();
        }
    }

    final String ecmaClass() {
        if (this.node.isTextType()) {
            return "text";
        }
        if (this.node.isAttributeType()) {
            return "attribute";
        }
        if (this.node.isCommentType()) {
            return "comment";
        }
        if (this.node.isProcessingInstructionType()) {
            return "processing-instruction";
        }
        if (this.node.isElementType()) {
            return "element";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unrecognized type: ");
        stringBuilder.append((Object)this.node);
        throw new RuntimeException(stringBuilder.toString());
    }

    @Override
    XMLList elements(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        xMLList.setTargets(this, xMLName.toQname());
        XmlNode[] arrxmlNode = this.node.getMatchingChildren(XmlNode.Filter.ELEMENT);
        for (int i = 0; i < arrxmlNode.length; ++i) {
            if (!xMLName.matches(this.toXML(arrxmlNode[i]))) continue;
            xMLList.addToList(this.toXML(arrxmlNode[i]));
        }
        return xMLList;
    }

    @Override
    boolean equivalentXml(Object object) {
        boolean bl;
        if (object instanceof XML) {
            return this.node.toXmlString(this.getProcessor()).equals((Object)((XML)object).node.toXmlString(this.getProcessor()));
        }
        if (object instanceof XMLList) {
            XMLList xMLList = (XMLList)object;
            int n = xMLList.length();
            bl = false;
            if (n == 1) {
                bl = this.equivalentXml(xMLList.getXML());
            }
        } else {
            boolean bl2 = this.hasSimpleContent();
            bl = false;
            if (bl2) {
                String string = ScriptRuntime.toString(object);
                return this.toString().equals((Object)string);
            }
        }
        return bl;
    }

    @Override
    public Object get(int n, Scriptable scriptable) {
        if (n == 0) {
            return this;
        }
        return Scriptable.NOT_FOUND;
    }

    XmlNode getAnnotation() {
        return this.node;
    }

    XML[] getAttributes() {
        XmlNode[] arrxmlNode = this.node.getAttributes();
        XML[] arrxML = new XML[arrxmlNode.length];
        for (int i = 0; i < arrxML.length; ++i) {
            arrxML[i] = this.toXML(arrxmlNode[i]);
        }
        return arrxML;
    }

    XML[] getChildren() {
        if (!this.isElement()) {
            return null;
        }
        XmlNode[] arrxmlNode = this.node.getMatchingChildren(XmlNode.Filter.TRUE);
        XML[] arrxML = new XML[arrxmlNode.length];
        for (int i = 0; i < arrxML.length; ++i) {
            arrxML[i] = this.toXML(arrxmlNode[i]);
        }
        return arrxML;
    }

    @Override
    public String getClassName() {
        return "XML";
    }

    @Override
    public Scriptable getExtraMethodSource(Context context) {
        if (this.hasSimpleContent()) {
            return ScriptRuntime.toObjectOrNull(context, this.toString());
        }
        return null;
    }

    @Override
    public Object[] getIds() {
        if (this.isPrototype()) {
            return new Object[0];
        }
        Object[] arrobject = new Object[]{0};
        return arrobject;
    }

    XML getLastXmlChild() {
        int n = -1 + this.node.getChildCount();
        if (n < 0) {
            return null;
        }
        return this.getXmlChild(n);
    }

    XmlNode.QName getNodeQname() {
        return this.node.getQname();
    }

    XMLList getPropertyList(XMLName xMLName) {
        return xMLName.getMyValueOn(this);
    }

    @Override
    final XML getXML() {
        return this;
    }

    @Override
    Object getXMLProperty(XMLName xMLName) {
        return this.getPropertyList(xMLName);
    }

    XML getXmlChild(int n) {
        XmlNode xmlNode = this.node.getChild(n);
        if (xmlNode.getXml() == null) {
            xmlNode.setXml(this.newXML(xmlNode));
        }
        return xmlNode.getXml();
    }

    @Override
    public boolean has(int n, Scriptable scriptable) {
        return n == 0;
    }

    @Override
    boolean hasComplexContent() {
        return true ^ this.hasSimpleContent();
    }

    @Override
    boolean hasOwnProperty(XMLName xMLName) {
        boolean bl = this.isPrototype();
        boolean bl2 = true;
        if (bl) {
            if (this.findPrototypeId(xMLName.localName()) == 0) {
                bl2 = false;
            }
            return bl2;
        }
        if (this.getPropertyList(xMLName).length() <= 0) {
            bl2 = false;
        }
        return bl2;
    }

    @Override
    boolean hasSimpleContent() {
        if (!this.isComment() && !this.isProcessingInstruction()) {
            if (!this.isText()) {
                if (this.node.isAttributeType()) {
                    return true;
                }
                return true ^ this.node.hasChildElement();
            }
            return true;
        }
        return false;
    }

    @Override
    boolean hasXMLProperty(XMLName xMLName) {
        return this.getPropertyList(xMLName).length() > 0;
    }

    Namespace[] inScopeNamespaces() {
        return this.createNamespaces(this.node.getInScopeNamespaces());
    }

    void initialize(XmlNode xmlNode) {
        this.node = xmlNode;
        xmlNode.setXml(this);
    }

    XML insertChildAfter(XML xML, Object object) {
        if (xML == null) {
            this.prependChild(object);
            return this;
        }
        XmlNode[] arrxmlNode = this.getNodesForInsert(object);
        int n = this.getChildIndexOf(xML);
        if (n != -1) {
            this.node.insertChildrenAt(n + 1, arrxmlNode);
        }
        return this;
    }

    XML insertChildBefore(XML xML, Object object) {
        if (xML == null) {
            this.appendChild(object);
            return this;
        }
        XmlNode[] arrxmlNode = this.getNodesForInsert(object);
        int n = this.getChildIndexOf(xML);
        if (n != -1) {
            this.node.insertChildrenAt(n, arrxmlNode);
        }
        return this;
    }

    boolean is(XML xML) {
        return this.node.isSameNode(xML.node);
    }

    final boolean isAttribute() {
        return this.node.isAttributeType();
    }

    final boolean isComment() {
        return this.node.isCommentType();
    }

    final boolean isElement() {
        return this.node.isElementType();
    }

    final boolean isProcessingInstruction() {
        return this.node.isProcessingInstructionType();
    }

    final boolean isText() {
        return this.node.isTextType();
    }

    @Override
    protected Object jsConstructor(Context context, boolean bl, Object[] arrobject) {
        if (arrobject.length == 0 || arrobject[0] == null || arrobject[0] == Undefined.instance) {
            arrobject = new Object[]{""};
        }
        XML xML = this.ecmaToXml(arrobject[0]);
        if (bl) {
            return xML.copy();
        }
        return xML;
    }

    @Override
    int length() {
        return 1;
    }

    String localName() {
        if (this.name() == null) {
            return null;
        }
        return this.name().localName();
    }

    XML makeXmlFromString(XMLName xMLName, String string) {
        try {
            XML xML = this.newTextElementXML(this.node, xMLName.toQname(), string);
            return xML;
        }
        catch (Exception exception) {
            throw ScriptRuntime.typeError(exception.getMessage());
        }
    }

    QName name() {
        if (!this.isText()) {
            if (this.isComment()) {
                return null;
            }
            if (this.isProcessingInstruction()) {
                return this.newQName("", this.node.getQname().getLocalName(), null);
            }
            return this.newQName(this.node.getQname());
        }
        return null;
    }

    Namespace namespace(String string) {
        if (string == null) {
            return this.createNamespace(this.node.getNamespaceDeclaration());
        }
        return this.createNamespace(this.node.getNamespaceDeclaration(string));
    }

    Namespace[] namespaceDeclarations() {
        return this.createNamespaces(this.node.getNamespaceDeclarations());
    }

    Object nodeKind() {
        return this.ecmaClass();
    }

    @Override
    void normalize() {
        this.node.normalize();
    }

    @Override
    Object parent() {
        if (this.node.parent() == null) {
            return null;
        }
        return this.newXML(this.node.parent());
    }

    XML prependChild(Object object) {
        if (this.node.isParentType()) {
            this.node.insertChildrenAt(0, this.getNodesForInsert(object));
        }
        return this;
    }

    @Override
    XMLList processingInstructions(XMLName xMLName) {
        XMLList xMLList = this.newXMLList();
        this.node.addMatchingChildren(xMLList, XmlNode.Filter.PROCESSING_INSTRUCTION(xMLName));
        return xMLList;
    }

    @Override
    boolean propertyIsEnumerable(Object object) {
        boolean bl = object instanceof Integer;
        boolean bl2 = true;
        if (bl) {
            if ((Integer)object != 0) {
                bl2 = false;
            }
            return bl2;
        }
        if (object instanceof Number) {
            double d = ((Number)object).doubleValue();
            if (d != 0.0 || !(1.0 / d > 0.0)) {
                bl2 = false;
            }
            return bl2;
        }
        return ScriptRuntime.toString(object).equals((Object)"0");
    }

    @Override
    public void put(int n, Scriptable scriptable, Object object) {
        throw ScriptRuntime.typeError("Assignment to indexed XML is not allowed");
    }

    @Override
    void putXMLProperty(XMLName xMLName, Object object) {
        if (this.isPrototype()) {
            return;
        }
        xMLName.setMyValueOn(this, object);
    }

    void remove() {
        this.node.deleteMe();
    }

    void removeChild(int n) {
        this.node.removeChild(n);
    }

    XML removeNamespace(Namespace namespace) {
        if (!this.isElement()) {
            return this;
        }
        this.node.removeNamespace(this.adapt(namespace));
        return this;
    }

    XML replace(int n, Object object) {
        XMLList xMLList = this.child(n);
        if (xMLList.length() > 0) {
            this.insertChildAfter(xMLList.item(0), object);
            this.removeChild(n);
        }
        return this;
    }

    XML replace(XMLName xMLName, Object object) {
        this.putXMLProperty(xMLName, object);
        return this;
    }

    void replaceWith(XML xML) {
        if (this.node.parent() != null) {
            this.node.replaceWith(xML.node);
            return;
        }
        this.initialize(xML.node);
    }

    void setAttribute(XMLName xMLName, Object object) {
        if (this.isElement()) {
            if (xMLName.uri() == null && xMLName.localName().equals((Object)"*")) {
                throw ScriptRuntime.typeError("@* assignment not supported.");
            }
            this.node.setAttribute(xMLName.toQname(), ScriptRuntime.toString(object));
            return;
        }
        throw new IllegalStateException("Can only set attributes on elements.");
    }

    XML setChildren(Object object) {
        if (!this.isElement()) {
            return this;
        }
        while (this.node.getChildCount() > 0) {
            this.node.removeChild(0);
        }
        XmlNode[] arrxmlNode = this.getNodesForInsert(object);
        this.node.insertChildrenAt(0, arrxmlNode);
        return this;
    }

    void setLocalName(String string) {
        if (!this.isText()) {
            if (this.isComment()) {
                return;
            }
            this.node.setLocalName(string);
            return;
        }
    }

    void setName(QName qName) {
        if (!this.isText()) {
            if (this.isComment()) {
                return;
            }
            if (this.isProcessingInstruction()) {
                this.node.setLocalName(qName.localName());
                return;
            }
            this.node.renameNode(qName.getDelegate());
            return;
        }
    }

    void setNamespace(Namespace namespace) {
        if (!this.isText() && !this.isComment()) {
            if (this.isProcessingInstruction()) {
                return;
            }
            this.setName(this.newQName(namespace.uri(), this.localName(), namespace.prefix()));
            return;
        }
    }

    @Override
    XMLList text() {
        XMLList xMLList = this.newXMLList();
        this.node.addMatchingChildren(xMLList, XmlNode.Filter.TEXT);
        return xMLList;
    }

    Node toDomNode() {
        return this.node.toDomNode();
    }

    @Override
    String toSource(int n) {
        return this.toXMLString();
    }

    @Override
    public String toString() {
        return this.ecmaToString();
    }

    @Override
    String toXMLString() {
        return this.node.ecmaToXMLString(this.getProcessor());
    }

    @Override
    Object valueOf() {
        return this;
    }
}

