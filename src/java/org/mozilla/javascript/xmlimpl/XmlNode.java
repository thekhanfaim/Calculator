/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.Serializable
 *  java.lang.Deprecated
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  org.mozilla.javascript.xmlimpl.XML
 *  org.mozilla.javascript.xmlimpl.XMLList
 *  org.w3c.dom.Attr
 *  org.w3c.dom.Comment
 *  org.w3c.dom.Document
 *  org.w3c.dom.Element
 *  org.w3c.dom.NamedNodeMap
 *  org.w3c.dom.Node
 *  org.w3c.dom.NodeList
 *  org.w3c.dom.ProcessingInstruction
 *  org.w3c.dom.Text
 *  org.w3c.dom.UserDataHandler
 *  org.xml.sax.SAXException
 */
package org.mozilla.javascript.xmlimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XMLName;
import org.mozilla.javascript.xmlimpl.XmlProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

class XmlNode
implements Serializable {
    private static final boolean DOM_LEVEL_3 = true;
    private static final String USER_DATA_XMLNODE_KEY = XmlNode.class.getName();
    private static final String XML_NAMESPACES_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    private static final long serialVersionUID = 1L;
    private Node dom;
    private UserDataHandler events = new XmlNodeUserDataHandler();
    private XML xml;

    private XmlNode() {
    }

    private void addNamespaces(Namespaces namespaces, Element element) {
        RuntimeException runtimeException;
        if (element != null) {
            String string2 = this.toUri(element.lookupNamespaceURI(null));
            String string3 = "";
            if (element.getParentNode() != null) {
                string3 = this.toUri(element.getParentNode().lookupNamespaceURI(null));
            }
            if (!string2.equals((Object)string3) || !(element.getParentNode() instanceof Element)) {
                namespaces.declare(Namespace.create("", string2));
            }
            NamedNodeMap namedNodeMap = element.getAttributes();
            for (int i = 0; i < namedNodeMap.getLength(); ++i) {
                Attr attr2 = (Attr)namedNodeMap.item(i);
                if (attr2.getPrefix() == null || !attr2.getPrefix().equals((Object)"xmlns")) continue;
                namespaces.declare(Namespace.create(attr2.getLocalName(), attr2.getValue()));
            }
            return;
        }
        runtimeException = new RuntimeException("element must not be null");
        throw runtimeException;
    }

    private static XmlNode copy(XmlNode xmlNode) {
        return XmlNode.createImpl(xmlNode.dom.cloneNode(true));
    }

    static XmlNode createElement(XmlProcessor xmlProcessor, String string2, String string3) throws SAXException {
        return XmlNode.createImpl(xmlProcessor.toXml(string2, string3));
    }

    static XmlNode createElementFromNode(Node node) {
        if (node instanceof Document) {
            node = ((Document)node).getDocumentElement();
        }
        return XmlNode.createImpl(node);
    }

    static XmlNode createEmpty(XmlProcessor xmlProcessor) {
        return XmlNode.createText(xmlProcessor, "");
    }

    private static XmlNode createImpl(Node node) {
        if (!(node instanceof Document)) {
            if (XmlNode.getUserData(node) == null) {
                XmlNode xmlNode = new XmlNode();
                xmlNode.dom = node;
                XmlNode.setUserData(node, xmlNode);
                return xmlNode;
            }
            return XmlNode.getUserData(node);
        }
        throw new IllegalArgumentException();
    }

    static XmlNode createText(XmlProcessor xmlProcessor, String string2) {
        return XmlNode.createImpl((Node)xmlProcessor.newDocument().createTextNode(string2));
    }

    private void declareNamespace(Element element, String string2, String string3) {
        if (string2.length() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("xmlns:");
            stringBuilder.append(string2);
            element.setAttributeNS(XML_NAMESPACES_NAMESPACE_URI, stringBuilder.toString(), string3);
            return;
        }
        element.setAttribute("xmlns", string3);
    }

    private Namespaces getAllNamespaces() {
        Namespaces namespaces = new Namespaces();
        Node node = this.dom;
        if (node instanceof Attr) {
            node = ((Attr)node).getOwnerElement();
        }
        while (node != null) {
            if (node instanceof Element) {
                this.addNamespaces(namespaces, (Element)node);
            }
            node = node.getParentNode();
        }
        namespaces.declare(Namespace.create("", ""));
        return namespaces;
    }

    private Namespace getDefaultNamespace() {
        String string2 = this.dom.lookupNamespaceURI(null) == null ? "" : this.dom.lookupNamespaceURI(null);
        return Namespace.create("", string2);
    }

    private String getExistingPrefixFor(Namespace namespace) {
        if (this.getDefaultNamespace().getUri().equals((Object)namespace.getUri())) {
            return "";
        }
        return this.dom.lookupPrefix(namespace.getUri());
    }

    private Namespace getNodeNamespace() {
        String string2 = this.dom.getNamespaceURI();
        String string3 = this.dom.getPrefix();
        if (string2 == null) {
            string2 = "";
        }
        if (string3 == null) {
            string3 = "";
        }
        return Namespace.create(string3, string2);
    }

    private static XmlNode getUserData(Node node) {
        return (XmlNode)node.getUserData(USER_DATA_XMLNODE_KEY);
    }

    static XmlNode newElementWithText(XmlProcessor xmlProcessor, XmlNode xmlNode, QName qName, String string2) {
        if (!(xmlNode instanceof Document)) {
            Document document = xmlNode != null ? xmlNode.dom.getOwnerDocument() : xmlProcessor.newDocument();
            Node node = xmlNode != null ? xmlNode.dom : null;
            Namespace namespace = qName.getNamespace();
            Element element = namespace != null && namespace.getUri().length() != 0 ? document.createElementNS(namespace.getUri(), qName.qualify(node)) : document.createElementNS(null, qName.getLocalName());
            if (string2 != null) {
                element.appendChild((Node)document.createTextNode(string2));
            }
            return XmlNode.createImpl((Node)element);
        }
        throw new IllegalArgumentException("Cannot use Document node as reference");
    }

    private void setProcessingInstructionName(String string2) {
        ProcessingInstruction processingInstruction = (ProcessingInstruction)this.dom;
        processingInstruction.getParentNode().replaceChild((Node)processingInstruction, (Node)processingInstruction.getOwnerDocument().createProcessingInstruction(string2, processingInstruction.getData()));
    }

    private static void setUserData(Node node, XmlNode xmlNode) {
        node.setUserData(USER_DATA_XMLNODE_KEY, (Object)xmlNode, xmlNode.events);
    }

    private String toUri(String string2) {
        if (string2 == null) {
            return "";
        }
        return string2;
    }

    void addMatchingChildren(XMLList xMLList, Filter filter) {
        NodeList nodeList = this.dom.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            XmlNode xmlNode = XmlNode.createImpl(node);
            if (!filter.accept(node)) continue;
            xMLList.addToList((Object)xmlNode);
        }
    }

    final XmlNode copy() {
        return XmlNode.copy(this);
    }

    String debug() {
        XmlProcessor xmlProcessor = new XmlProcessor();
        xmlProcessor.setIgnoreComments(false);
        xmlProcessor.setIgnoreProcessingInstructions(false);
        xmlProcessor.setIgnoreWhitespace(false);
        xmlProcessor.setPrettyPrinting(false);
        return xmlProcessor.ecmaToXmlString(this.dom);
    }

    void declareNamespace(String string2, String string3) {
        Node node = this.dom;
        if (node instanceof Element) {
            if (node.lookupNamespaceURI(string3) != null && this.dom.lookupNamespaceURI(string3).equals((Object)string2)) {
                return;
            }
            this.declareNamespace((Element)this.dom, string2, string3);
            return;
        }
        throw new IllegalStateException();
    }

    void deleteMe() {
        Node node = this.dom;
        if (node instanceof Attr) {
            Attr attr2 = (Attr)node;
            attr2.getOwnerElement().getAttributes().removeNamedItemNS(attr2.getNamespaceURI(), attr2.getLocalName());
            return;
        }
        if (node.getParentNode() != null) {
            this.dom.getParentNode().removeChild(this.dom);
        }
    }

    String ecmaToXMLString(XmlProcessor xmlProcessor) {
        if (this.isElementType()) {
            Element element = (Element)this.dom.cloneNode(true);
            Namespace[] arrnamespace = this.getInScopeNamespaces();
            for (int i = 0; i < arrnamespace.length; ++i) {
                this.declareNamespace(element, arrnamespace[i].getPrefix(), arrnamespace[i].getUri());
            }
            return xmlProcessor.ecmaToXmlString((Node)element);
        }
        return xmlProcessor.ecmaToXmlString(this.dom);
    }

    String ecmaValue() {
        if (this.isTextType()) {
            return ((Text)this.dom).getData();
        }
        if (this.isAttributeType()) {
            return ((Attr)this.dom).getValue();
        }
        if (this.isProcessingInstructionType()) {
            return ((ProcessingInstruction)this.dom).getData();
        }
        if (this.isCommentType()) {
            return ((Comment)this.dom).getNodeValue();
        }
        if (this.isElementType()) {
            throw new RuntimeException("Unimplemented ecmaValue() for elements.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unimplemented for node ");
        stringBuilder.append((Object)this.dom);
        throw new RuntimeException(stringBuilder.toString());
    }

    String getAttributeValue() {
        return ((Attr)this.dom).getValue();
    }

    XmlNode[] getAttributes() {
        IllegalStateException illegalStateException;
        NamedNodeMap namedNodeMap = this.dom.getAttributes();
        if (namedNodeMap != null) {
            XmlNode[] arrxmlNode = new XmlNode[namedNodeMap.getLength()];
            for (int i = 0; i < namedNodeMap.getLength(); ++i) {
                arrxmlNode[i] = XmlNode.createImpl(namedNodeMap.item(i));
            }
            return arrxmlNode;
        }
        illegalStateException = new IllegalStateException("Must be element.");
        throw illegalStateException;
    }

    XmlNode getChild(int n) {
        return XmlNode.createImpl(this.dom.getChildNodes().item(n));
    }

    int getChildCount() {
        return this.dom.getChildNodes().getLength();
    }

    int getChildIndex() {
        RuntimeException runtimeException;
        if (this.isAttributeType()) {
            return -1;
        }
        if (this.parent() == null) {
            return -1;
        }
        NodeList nodeList = this.dom.getParentNode().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i) != this.dom) continue;
            return i;
        }
        runtimeException = new RuntimeException("Unreachable.");
        throw runtimeException;
    }

    Namespace[] getInScopeNamespaces() {
        return this.getAllNamespaces().getNamespaces();
    }

    XmlNode[] getMatchingChildren(Filter filter) {
        ArrayList arrayList = new ArrayList();
        NodeList nodeList = this.dom.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!filter.accept(node)) continue;
            arrayList.add((Object)XmlNode.createImpl(node));
        }
        return (XmlNode[])arrayList.toArray((Object[])new XmlNode[arrayList.size()]);
    }

    Namespace getNamespace() {
        return this.getNodeNamespace();
    }

    Namespace getNamespaceDeclaration() {
        if (this.dom.getPrefix() == null) {
            return this.getNamespaceDeclaration("");
        }
        return this.getNamespaceDeclaration(this.dom.getPrefix());
    }

    Namespace getNamespaceDeclaration(String string2) {
        if (string2.equals((Object)"") && this.dom instanceof Attr) {
            return Namespace.create("", "");
        }
        return this.getAllNamespaces().getNamespace(string2);
    }

    Namespace[] getNamespaceDeclarations() {
        if (this.dom instanceof Element) {
            Namespaces namespaces = new Namespaces();
            this.addNamespaces(namespaces, (Element)this.dom);
            return namespaces.getNamespaces();
        }
        return new Namespace[0];
    }

    final QName getQname() {
        String string2 = this.dom.getNamespaceURI();
        String string3 = "";
        String string4 = string2 == null ? string3 : this.dom.getNamespaceURI();
        if (this.dom.getPrefix() != null) {
            string3 = this.dom.getPrefix();
        }
        return QName.create(string4, this.dom.getLocalName(), string3);
    }

    XML getXml() {
        return this.xml;
    }

    boolean hasChildElement() {
        NodeList nodeList = this.dom.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (nodeList.item(i).getNodeType() != 1) continue;
            return true;
        }
        return false;
    }

    void insertChildAt(int n, XmlNode xmlNode) {
        Node node = this.dom;
        Node node2 = node.getOwnerDocument().importNode(xmlNode.dom, true);
        if (node.getChildNodes().getLength() >= n) {
            if (node.getChildNodes().getLength() == n) {
                node.appendChild(node2);
                return;
            }
            node.insertBefore(node2, node.getChildNodes().item(n));
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("index=");
        stringBuilder.append(n);
        stringBuilder.append(" length=");
        stringBuilder.append(node.getChildNodes().getLength());
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    void insertChildrenAt(int n, XmlNode[] arrxmlNode) {
        for (int i = 0; i < arrxmlNode.length; ++i) {
            this.insertChildAt(n + i, arrxmlNode[i]);
        }
    }

    void invalidateNamespacePrefix() {
        IllegalStateException illegalStateException;
        Node node = this.dom;
        if (node instanceof Element) {
            String string2 = node.getPrefix();
            this.renameNode(QName.create(this.dom.getNamespaceURI(), this.dom.getLocalName(), null));
            NamedNodeMap namedNodeMap = this.dom.getAttributes();
            for (int i = 0; i < namedNodeMap.getLength(); ++i) {
                if (!namedNodeMap.item(i).getPrefix().equals((Object)string2)) continue;
                XmlNode.createImpl(namedNodeMap.item(i)).renameNode(QName.create(namedNodeMap.item(i).getNamespaceURI(), namedNodeMap.item(i).getLocalName(), null));
            }
            return;
        }
        illegalStateException = new IllegalStateException();
        throw illegalStateException;
    }

    final boolean isAttributeType() {
        return this.dom.getNodeType() == 2;
    }

    final boolean isCommentType() {
        return this.dom.getNodeType() == 8;
    }

    final boolean isElementType() {
        return this.dom.getNodeType() == 1;
    }

    final boolean isParentType() {
        return this.isElementType();
    }

    final boolean isProcessingInstructionType() {
        return this.dom.getNodeType() == 7;
    }

    boolean isSameNode(XmlNode xmlNode) {
        return this.dom == xmlNode.dom;
    }

    final boolean isTextType() {
        return this.dom.getNodeType() == 3 || this.dom.getNodeType() == 4;
        {
        }
    }

    void normalize() {
        this.dom.normalize();
    }

    XmlNode parent() {
        Node node = this.dom.getParentNode();
        if (node instanceof Document) {
            return null;
        }
        if (node == null) {
            return null;
        }
        return XmlNode.createImpl(node);
    }

    void removeChild(int n) {
        Node node = this.dom;
        node.removeChild(node.getChildNodes().item(n));
    }

    void removeNamespace(Namespace namespace) {
        if (namespace.is(this.getNodeNamespace())) {
            return;
        }
        NamedNodeMap namedNodeMap = this.dom.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); ++i) {
            if (!namespace.is(XmlNode.createImpl(namedNodeMap.item(i)).getNodeNamespace())) continue;
            return;
        }
        String string2 = this.getExistingPrefixFor(namespace);
        if (string2 != null) {
            if (namespace.isUnspecifiedPrefix()) {
                this.declareNamespace(string2, this.getDefaultNamespace().getUri());
                return;
            }
            if (string2.equals((Object)namespace.getPrefix())) {
                this.declareNamespace(string2, this.getDefaultNamespace().getUri());
            }
        }
    }

    final void renameNode(QName qName) {
        this.dom = this.dom.getOwnerDocument().renameNode(this.dom, qName.getNamespace().getUri(), qName.qualify(this.dom));
    }

    void replaceWith(XmlNode xmlNode) {
        Node node = xmlNode.dom;
        if (node.getOwnerDocument() != this.dom.getOwnerDocument()) {
            node = this.dom.getOwnerDocument().importNode(node, true);
        }
        this.dom.getParentNode().replaceChild(node, this.dom);
    }

    void setAttribute(QName qName, String string2) {
        Node node = this.dom;
        if (node instanceof Element) {
            qName.setAttribute((Element)node, string2);
            return;
        }
        throw new IllegalStateException("Can only set attribute on elements.");
    }

    final void setLocalName(String string2) {
        Node node = this.dom;
        if (node instanceof ProcessingInstruction) {
            this.setProcessingInstructionName(string2);
            return;
        }
        String string3 = node.getPrefix();
        if (string3 == null) {
            string3 = "";
        }
        Document document = this.dom.getOwnerDocument();
        Node node2 = this.dom;
        this.dom = document.renameNode(node2, node2.getNamespaceURI(), QName.qualify(string3, string2));
    }

    void setXml(XML xML) {
        this.xml = xML;
    }

    Node toDomNode() {
        return this.dom;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("XmlNode: type=");
        stringBuilder.append((int)this.dom.getNodeType());
        stringBuilder.append(" dom=");
        stringBuilder.append(this.dom.toString());
        return stringBuilder.toString();
    }

    String toXmlString(XmlProcessor xmlProcessor) {
        return xmlProcessor.ecmaToXmlString(this.dom);
    }

    static abstract class Filter {
        static final Filter COMMENT = new Filter(){

            @Override
            boolean accept(Node node) {
                return node.getNodeType() == 8;
            }
        };
        static Filter ELEMENT;
        static final Filter TEXT;
        static Filter TRUE;

        static {
            TEXT = new Filter(){

                @Override
                boolean accept(Node node) {
                    return node.getNodeType() == 3;
                }
            };
            ELEMENT = new Filter(){

                @Override
                boolean accept(Node node) {
                    return node.getNodeType() == 1;
                }
            };
            TRUE = new Filter(){

                @Override
                boolean accept(Node node) {
                    return true;
                }
            };
        }

        Filter() {
        }

        static Filter PROCESSING_INSTRUCTION(final XMLName xMLName) {
            return new Filter(){

                @Override
                boolean accept(Node node) {
                    if (node.getNodeType() == 7) {
                        ProcessingInstruction processingInstruction = (ProcessingInstruction)node;
                        return xMLName.matchesLocalName(processingInstruction.getTarget());
                    }
                    return false;
                }
            };
        }

        abstract boolean accept(Node var1);

    }

    static class InternalList
    implements Serializable {
        private static final long serialVersionUID = -3633151157292048978L;
        private List<XmlNode> list = new ArrayList();

        InternalList() {
        }

        private void _add(XmlNode xmlNode) {
            this.list.add((Object)xmlNode);
        }

        void add(XML xML) {
            this._add(xML.getAnnotation());
        }

        void add(InternalList internalList) {
            for (int i = 0; i < internalList.length(); ++i) {
                this._add(internalList.item(i));
            }
        }

        void add(InternalList internalList, int n, int n2) {
            for (int i = n; i < n2; ++i) {
                this._add(internalList.item(i));
            }
        }

        void add(XmlNode xmlNode) {
            this._add(xmlNode);
        }

        void addToList(Object object) {
            if (object instanceof Undefined) {
                return;
            }
            if (object instanceof XMLList) {
                XMLList xMLList = (XMLList)object;
                for (int i = 0; i < xMLList.length(); ++i) {
                    this._add(xMLList.item(i).getAnnotation());
                }
                return;
            }
            if (object instanceof XML) {
                this._add(((XML)object).getAnnotation());
                return;
            }
            if (object instanceof XmlNode) {
                this._add((XmlNode)object);
            }
        }

        XmlNode item(int n) {
            return (XmlNode)this.list.get(n);
        }

        int length() {
            return this.list.size();
        }

        void remove(int n) {
            this.list.remove(n);
        }
    }

    static class Namespace
    implements Serializable {
        static final Namespace GLOBAL = Namespace.create("", "");
        private static final long serialVersionUID = 4073904386884677090L;
        private String prefix;
        private String uri;

        private Namespace() {
        }

        static Namespace create(String string2) {
            Namespace namespace = new Namespace();
            namespace.uri = string2;
            if (string2 == null || string2.length() == 0) {
                namespace.prefix = "";
            }
            return namespace;
        }

        static Namespace create(String string2, String string3) {
            if (string2 != null) {
                if (string3 != null) {
                    Namespace namespace = new Namespace();
                    namespace.prefix = string2;
                    namespace.uri = string3;
                    return namespace;
                }
                throw new IllegalArgumentException("Namespace may not lack a URI");
            }
            throw new IllegalArgumentException("Empty string represents default namespace prefix");
        }

        private void setPrefix(String string2) {
            if (string2 != null) {
                this.prefix = string2;
                return;
            }
            throw new IllegalArgumentException();
        }

        String getPrefix() {
            return this.prefix;
        }

        String getUri() {
            return this.uri;
        }

        boolean is(Namespace namespace) {
            String string2;
            String string3 = this.prefix;
            return string3 != null && (string2 = namespace.prefix) != null && string3.equals((Object)string2) && this.uri.equals((Object)namespace.uri);
        }

        boolean isDefault() {
            String string2 = this.prefix;
            return string2 != null && string2.equals((Object)"");
        }

        boolean isEmpty() {
            String string2 = this.prefix;
            return string2 != null && string2.equals((Object)"") && this.uri.equals((Object)"");
        }

        boolean isGlobal() {
            String string2 = this.uri;
            return string2 != null && string2.equals((Object)"");
        }

        boolean isUnspecifiedPrefix() {
            return this.prefix == null;
        }

        public String toString() {
            if (this.prefix == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("XmlNode.Namespace [");
                stringBuilder.append(this.uri);
                stringBuilder.append("]");
                return stringBuilder.toString();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("XmlNode.Namespace [");
            stringBuilder.append(this.prefix);
            stringBuilder.append("{");
            stringBuilder.append(this.uri);
            stringBuilder.append("}]");
            return stringBuilder.toString();
        }
    }

    private static class Namespaces {
        private Map<String, String> map = new HashMap();
        private Map<String, String> uriToPrefix = new HashMap();

        Namespaces() {
        }

        void declare(Namespace namespace) {
            if (this.map.get((Object)namespace.prefix) == null) {
                this.map.put((Object)namespace.prefix, (Object)namespace.uri);
            }
            if (this.uriToPrefix.get((Object)namespace.uri) == null) {
                this.uriToPrefix.put((Object)namespace.uri, (Object)namespace.prefix);
            }
        }

        Namespace getNamespace(String string2) {
            if (this.map.get((Object)string2) == null) {
                return null;
            }
            return Namespace.create(string2, (String)this.map.get((Object)string2));
        }

        Namespace getNamespaceByUri(String string2) {
            if (this.uriToPrefix.get((Object)string2) == null) {
                return null;
            }
            return Namespace.create(string2, (String)this.uriToPrefix.get((Object)string2));
        }

        Namespace[] getNamespaces() {
            ArrayList arrayList = new ArrayList();
            for (String string2 : this.map.keySet()) {
                Namespace namespace = Namespace.create(string2, (String)this.map.get((Object)string2));
                if (namespace.isEmpty()) continue;
                arrayList.add((Object)namespace);
            }
            return (Namespace[])arrayList.toArray((Object[])new Namespace[arrayList.size()]);
        }
    }

    static class QName
    implements Serializable {
        private static final long serialVersionUID = -6587069811691451077L;
        private String localName;
        private Namespace namespace;

        private QName() {
        }

        @Deprecated
        static QName create(String string2, String string3, String string4) {
            return QName.create(Namespace.create(string4, string2), string3);
        }

        static QName create(Namespace namespace, String string2) {
            if (string2 != null && string2.equals((Object)"*")) {
                throw new RuntimeException("* is not valid localName");
            }
            QName qName = new QName();
            qName.namespace = namespace;
            qName.localName = string2;
            return qName;
        }

        private boolean equals(String string2, String string3) {
            if (string2 == null && string3 == null) {
                return true;
            }
            if (string2 != null && string3 != null) {
                return string2.equals((Object)string3);
            }
            return false;
        }

        private boolean namespacesEqual(Namespace namespace, Namespace namespace2) {
            if (namespace == null && namespace2 == null) {
                return true;
            }
            if (namespace != null && namespace2 != null) {
                return this.equals(namespace.getUri(), namespace2.getUri());
            }
            return false;
        }

        static String qualify(String string2, String string3) {
            if (string2 != null) {
                if (string2.length() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(string2);
                    stringBuilder.append(":");
                    stringBuilder.append(string3);
                    return stringBuilder.toString();
                }
                return string3;
            }
            throw new IllegalArgumentException("prefix must not be null");
        }

        public boolean equals(Object object) {
            if (!(object instanceof QName)) {
                return false;
            }
            return this.equals((QName)object);
        }

        final boolean equals(QName qName) {
            if (!this.namespacesEqual(this.namespace, qName.namespace)) {
                return false;
            }
            return this.equals(this.localName, qName.localName);
        }

        String getLocalName() {
            return this.localName;
        }

        Namespace getNamespace() {
            return this.namespace;
        }

        public int hashCode() {
            String string2 = this.localName;
            if (string2 == null) {
                return 0;
            }
            return string2.hashCode();
        }

        void lookupPrefix(Node node) {
            IllegalArgumentException illegalArgumentException;
            if (node != null) {
                String string2 = node.lookupPrefix(this.namespace.getUri());
                if (string2 == null) {
                    String string3 = node.lookupNamespaceURI(null);
                    if (string3 == null) {
                        string3 = "";
                    }
                    if (this.namespace.getUri().equals((Object)string3)) {
                        string2 = "";
                    }
                }
                int n = 0;
                while (string2 == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("e4x_");
                    int n2 = n + 1;
                    stringBuilder.append(n);
                    String string4 = stringBuilder.toString();
                    if (node.lookupNamespaceURI(string4) == null) {
                        string2 = string4;
                        Node node2 = node;
                        while (node2.getParentNode() != null && node2.getParentNode() instanceof Element) {
                            node2 = node2.getParentNode();
                        }
                        Element element = (Element)node2;
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("xmlns:");
                        stringBuilder2.append(string2);
                        element.setAttributeNS(XmlNode.XML_NAMESPACES_NAMESPACE_URI, stringBuilder2.toString(), this.namespace.getUri());
                    }
                    n = n2;
                }
                this.namespace.setPrefix(string2);
                return;
            }
            illegalArgumentException = new IllegalArgumentException("node must not be null");
            throw illegalArgumentException;
        }

        String qualify(Node node) {
            if (this.namespace.getPrefix() == null) {
                if (node != null) {
                    this.lookupPrefix(node);
                } else if (this.namespace.getUri().equals((Object)"")) {
                    this.namespace.setPrefix("");
                } else {
                    this.namespace.setPrefix("");
                }
            }
            return QName.qualify(this.namespace.getPrefix(), this.localName);
        }

        void setAttribute(Element element, String string2) {
            if (this.namespace.getPrefix() == null) {
                this.lookupPrefix((Node)element);
            }
            element.setAttributeNS(this.namespace.getUri(), QName.qualify(this.namespace.getPrefix(), this.localName), string2);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("XmlNode.QName [");
            stringBuilder.append(this.localName);
            stringBuilder.append(",");
            stringBuilder.append((Object)this.namespace);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    static class XmlNodeUserDataHandler
    implements UserDataHandler,
    Serializable {
        private static final long serialVersionUID = 4666895518900769588L;

        XmlNodeUserDataHandler() {
        }

        public void handle(short s, String string2, Object object, Node node, Node node2) {
        }
    }

}

