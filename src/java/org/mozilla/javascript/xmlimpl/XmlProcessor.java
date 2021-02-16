/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.ObjectInputStream
 *  java.io.Reader
 *  java.io.Serializable
 *  java.io.StringReader
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.ClassNotFoundException
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 *  java.util.concurrent.LinkedBlockingDeque
 *  javax.xml.parsers.DocumentBuilder
 *  javax.xml.parsers.DocumentBuilderFactory
 *  javax.xml.parsers.ParserConfigurationException
 *  javax.xml.transform.Result
 *  javax.xml.transform.Source
 *  javax.xml.transform.Transformer
 *  javax.xml.transform.TransformerConfigurationException
 *  javax.xml.transform.TransformerException
 *  javax.xml.transform.TransformerFactory
 *  javax.xml.transform.dom.DOMSource
 *  javax.xml.transform.stream.StreamResult
 *  org.mozilla.javascript.xmlimpl.XMLObjectImpl
 *  org.w3c.dom.Attr
 *  org.w3c.dom.Comment
 *  org.w3c.dom.Document
 *  org.w3c.dom.Element
 *  org.w3c.dom.Node
 *  org.w3c.dom.NodeList
 *  org.w3c.dom.ProcessingInstruction
 *  org.w3c.dom.Text
 *  org.xml.sax.ErrorHandler
 *  org.xml.sax.InputSource
 *  org.xml.sax.SAXException
 *  org.xml.sax.SAXParseException
 */
package org.mozilla.javascript.xmlimpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.xmlimpl.XMLObjectImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class XmlProcessor
implements Serializable {
    private static final long serialVersionUID = 6903514433204808713L;
    private transient LinkedBlockingDeque<DocumentBuilder> documentBuilderPool;
    private transient DocumentBuilderFactory dom;
    private RhinoSAXErrorHandler errorHandler = new RhinoSAXErrorHandler();
    private boolean ignoreComments;
    private boolean ignoreProcessingInstructions;
    private boolean ignoreWhitespace;
    private int prettyIndent;
    private boolean prettyPrint;
    private transient TransformerFactory xform;

    XmlProcessor() {
        DocumentBuilderFactory documentBuilderFactory;
        this.setDefault();
        this.dom = documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.dom.setIgnoringComments(false);
        this.xform = TransformerFactory.newInstance();
        this.documentBuilderPool = new LinkedBlockingDeque(2 * Runtime.getRuntime().availableProcessors());
    }

    private void addCommentsTo(List<Node> list, Node node) {
        if (node instanceof Comment) {
            list.add((Object)node);
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
                this.addProcessingInstructionsTo(list, node.getChildNodes().item(i));
            }
        }
    }

    private void addProcessingInstructionsTo(List<Node> list, Node node) {
        if (node instanceof ProcessingInstruction) {
            list.add((Object)node);
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
                this.addProcessingInstructionsTo(list, node.getChildNodes().item(i));
            }
        }
    }

    private void addTextNodesToRemoveAndTrim(List<Node> list, Node node) {
        if (node instanceof Text) {
            Text text = (Text)node;
            if (!false) {
                text.setData(text.getData().trim());
            } else if (text.getData().trim().length() == 0) {
                text.setData("");
            }
            if (text.getData().length() == 0) {
                list.add((Object)node);
            }
        }
        if (node.getChildNodes() != null) {
            for (int i = 0; i < node.getChildNodes().getLength(); ++i) {
                this.addTextNodesToRemoveAndTrim(list, node.getChildNodes().item(i));
            }
        }
    }

    private void beautifyElement(Element element, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('\n');
        for (int i = 0; i < n; ++i) {
            stringBuilder.append(' ');
        }
        String string2 = stringBuilder.toString();
        for (int i = 0; i < this.prettyIndent; ++i) {
            stringBuilder.append(' ');
        }
        String string3 = stringBuilder.toString();
        ArrayList arrayList = new ArrayList();
        boolean bl = false;
        for (int i = 0; i < element.getChildNodes().getLength(); ++i) {
            if (i == 1) {
                bl = true;
            }
            if (element.getChildNodes().item(i) instanceof Text) {
                arrayList.add((Object)element.getChildNodes().item(i));
                continue;
            }
            bl = true;
            arrayList.add((Object)element.getChildNodes().item(i));
        }
        if (bl) {
            for (int i = 0; i < arrayList.size(); ++i) {
                element.insertBefore((Node)element.getOwnerDocument().createTextNode(string3), (Node)arrayList.get(i));
            }
        }
        NodeList nodeList = element.getChildNodes();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (!(nodeList.item(i) instanceof Element)) continue;
            arrayList2.add((Object)((Element)nodeList.item(i)));
        }
        Iterator iterator = arrayList2.iterator();
        while (iterator.hasNext()) {
            this.beautifyElement((Element)iterator.next(), n + this.prettyIndent);
        }
        if (bl) {
            element.appendChild((Node)element.getOwnerDocument().createTextNode(string2));
        }
    }

    private String elementToXmlString(Element element) {
        Element element2 = (Element)element.cloneNode(true);
        if (this.prettyPrint) {
            this.beautifyElement(element2, 0);
        }
        return this.toString((Node)element2);
    }

    private String escapeElementValue(String string2) {
        return this.escapeTextValue(string2);
    }

    private DocumentBuilder getDocumentBuilderFromPool() throws ParserConfigurationException {
        DocumentBuilder documentBuilder = (DocumentBuilder)this.documentBuilderPool.pollFirst();
        if (documentBuilder == null) {
            documentBuilder = this.getDomFactory().newDocumentBuilder();
        }
        documentBuilder.setErrorHandler((ErrorHandler)this.errorHandler);
        return documentBuilder;
    }

    private DocumentBuilderFactory getDomFactory() {
        return this.dom;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        DocumentBuilderFactory documentBuilderFactory;
        objectInputStream.defaultReadObject();
        this.dom = documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        this.dom.setIgnoringComments(false);
        this.xform = TransformerFactory.newInstance();
        this.documentBuilderPool = new LinkedBlockingDeque(2 * Runtime.getRuntime().availableProcessors());
    }

    private void returnDocumentBuilderToPool(DocumentBuilder documentBuilder) {
        try {
            documentBuilder.reset();
            this.documentBuilderPool.offerFirst((Object)documentBuilder);
            return;
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            return;
        }
    }

    private String toString(Node node) {
        DOMSource dOMSource = new DOMSource(node);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult((Writer)stringWriter);
        try {
            Transformer transformer = this.xform.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.setOutputProperty("indent", "no");
            transformer.setOutputProperty("method", "xml");
            transformer.transform((Source)dOMSource, (Result)streamResult);
        }
        catch (TransformerException transformerException) {
            throw new RuntimeException((Throwable)transformerException);
        }
        catch (TransformerConfigurationException transformerConfigurationException) {
            throw new RuntimeException((Throwable)transformerConfigurationException);
        }
        return this.toXmlNewlines(stringWriter.toString());
    }

    private String toXmlNewlines(String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string2.length(); ++i) {
            if (string2.charAt(i) == '\r') {
                if (string2.charAt(i + 1) == '\n') continue;
                stringBuilder.append('\n');
                continue;
            }
            stringBuilder.append(string2.charAt(i));
        }
        return stringBuilder.toString();
    }

    final String ecmaToXmlString(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.prettyPrint) {
            for (int i = 0; i < 0; ++i) {
                stringBuilder.append(' ');
            }
        }
        if (node instanceof Text) {
            String string2 = ((Text)node).getData();
            String string3 = this.prettyPrint ? string2.trim() : string2;
            stringBuilder.append(this.escapeElementValue(string3));
            return stringBuilder.toString();
        }
        if (node instanceof Attr) {
            stringBuilder.append(this.escapeAttributeValue(((Attr)node).getValue()));
            return stringBuilder.toString();
        }
        if (node instanceof Comment) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("<!--");
            stringBuilder2.append(((Comment)node).getNodeValue());
            stringBuilder2.append("-->");
            stringBuilder.append(stringBuilder2.toString());
            return stringBuilder.toString();
        }
        if (node instanceof ProcessingInstruction) {
            ProcessingInstruction processingInstruction = (ProcessingInstruction)node;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("<?");
            stringBuilder3.append(processingInstruction.getTarget());
            stringBuilder3.append(" ");
            stringBuilder3.append(processingInstruction.getData());
            stringBuilder3.append("?>");
            stringBuilder.append(stringBuilder3.toString());
            return stringBuilder.toString();
        }
        stringBuilder.append(this.elementToXmlString((Element)node));
        return stringBuilder.toString();
    }

    String escapeAttributeValue(Object object) {
        String string2 = ScriptRuntime.toString(object);
        if (string2.length() == 0) {
            return "";
        }
        Element element = this.newDocument().createElement("a");
        element.setAttribute("b", string2);
        String string3 = this.toString((Node)element);
        int n = string3.indexOf(34);
        int n2 = string3.lastIndexOf(34);
        return string3.substring(n + 1, n2);
    }

    String escapeTextValue(Object object) {
        if (object instanceof XMLObjectImpl) {
            return ((XMLObjectImpl)object).toXMLString();
        }
        String string2 = ScriptRuntime.toString(object);
        if (string2.length() == 0) {
            return string2;
        }
        Element element = this.newDocument().createElement("a");
        element.setTextContent(string2);
        String string3 = this.toString((Node)element);
        int n = 1 + string3.indexOf(62);
        int n2 = string3.lastIndexOf(60);
        if (n < n2) {
            return string3.substring(n, n2);
        }
        return "";
    }

    final int getPrettyIndent() {
        return this.prettyIndent;
    }

    final boolean isIgnoreComments() {
        return this.ignoreComments;
    }

    final boolean isIgnoreProcessingInstructions() {
        return this.ignoreProcessingInstructions;
    }

    final boolean isIgnoreWhitespace() {
        return this.ignoreWhitespace;
    }

    final boolean isPrettyPrinting() {
        return this.prettyPrint;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    Document newDocument() {
        Throwable throwable2222;
        DocumentBuilder documentBuilder = null;
        documentBuilder = this.getDocumentBuilderFromPool();
        Document document = documentBuilder.newDocument();
        if (documentBuilder == null) return document;
        this.returnDocumentBuilderToPool(documentBuilder);
        return document;
        {
            catch (Throwable throwable2222) {
            }
            catch (ParserConfigurationException parserConfigurationException) {}
            {
                throw new RuntimeException((Throwable)parserConfigurationException);
            }
        }
        if (documentBuilder == null) throw throwable2222;
        this.returnDocumentBuilderToPool(documentBuilder);
        throw throwable2222;
    }

    final void setDefault() {
        this.setIgnoreComments(true);
        this.setIgnoreProcessingInstructions(true);
        this.setIgnoreWhitespace(true);
        this.setPrettyPrinting(true);
        this.setPrettyIndent(2);
    }

    final void setIgnoreComments(boolean bl) {
        this.ignoreComments = bl;
    }

    final void setIgnoreProcessingInstructions(boolean bl) {
        this.ignoreProcessingInstructions = bl;
    }

    final void setIgnoreWhitespace(boolean bl) {
        this.ignoreWhitespace = bl;
    }

    final void setPrettyIndent(int n) {
        this.prettyIndent = n;
    }

    final void setPrettyPrinting(boolean bl) {
        this.prettyPrint = bl;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    final Node toXml(String string2, String string3) throws SAXException {
        DocumentBuilder documentBuilder;
        Throwable throwable22222;
        block15 : {
            block13 : {
                Document document;
                NodeList nodeList;
                block14 : {
                    documentBuilder = null;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("<parent xmlns=\"");
                    stringBuilder.append(string2);
                    stringBuilder.append("\">");
                    stringBuilder.append(string3);
                    stringBuilder.append("</parent>");
                    String string4 = stringBuilder.toString();
                    documentBuilder = this.getDocumentBuilderFromPool();
                    document = documentBuilder.parse(new InputSource((Reader)new StringReader(string4)));
                    if (this.ignoreProcessingInstructions) {
                        ArrayList arrayList = new ArrayList();
                        this.addProcessingInstructionsTo((List<Node>)arrayList, (Node)document);
                        for (Node node2 : arrayList) {
                            node2.getParentNode().removeChild(node2);
                        }
                    }
                    if (this.ignoreComments) {
                        ArrayList arrayList = new ArrayList();
                        this.addCommentsTo((List<Node>)arrayList, (Node)document);
                        for (Node node3 : arrayList) {
                            node3.getParentNode().removeChild(node3);
                        }
                    }
                    if (this.ignoreWhitespace) {
                        ArrayList arrayList = new ArrayList();
                        this.addTextNodesToRemoveAndTrim((List<Node>)arrayList, (Node)document);
                        for (Node node4 : arrayList) {
                            node4.getParentNode().removeChild(node4);
                        }
                    }
                    if ((nodeList = document.getDocumentElement().getChildNodes()).getLength() > 1) break block13;
                    if (nodeList.getLength() != 0) break block14;
                    Text text = document.createTextNode("");
                    if (documentBuilder == null) return text;
                    this.returnDocumentBuilderToPool(documentBuilder);
                    return text;
                }
                Node node = nodeList.item(0);
                document.getDocumentElement().removeChild(node);
                if (documentBuilder == null) return node;
                this.returnDocumentBuilderToPool(documentBuilder);
                return node;
            }
            try {
                throw ScriptRuntime.constructError("SyntaxError", "XML objects may contain at most one node.");
            }
            catch (Throwable throwable22222) {
                break block15;
            }
            catch (ParserConfigurationException parserConfigurationException) {
                throw new RuntimeException((Throwable)parserConfigurationException);
            }
            catch (IOException iOException) {
                throw new RuntimeException("Unreachable.");
            }
        }
        if (documentBuilder == null) throw throwable22222;
        this.returnDocumentBuilderToPool(documentBuilder);
        throw throwable22222;
    }

    private static class RhinoSAXErrorHandler
    implements ErrorHandler,
    Serializable {
        private static final long serialVersionUID = 6918417235413084055L;

        private RhinoSAXErrorHandler() {
        }

        private void throwError(SAXParseException sAXParseException) {
            throw ScriptRuntime.constructError("TypeError", sAXParseException.getMessage(), -1 + sAXParseException.getLineNumber());
        }

        public void error(SAXParseException sAXParseException) {
            this.throwError(sAXParseException);
        }

        public void fatalError(SAXParseException sAXParseException) {
            this.throwError(sAXParseException);
        }

        public void warning(SAXParseException sAXParseException) {
            Context.reportWarning(sAXParseException.getMessage());
        }
    }

}

