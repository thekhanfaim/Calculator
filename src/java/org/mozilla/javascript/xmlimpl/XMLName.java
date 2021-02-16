/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  org.mozilla.javascript.xmlimpl.XML
 *  org.mozilla.javascript.xmlimpl.XMLList
 *  org.mozilla.javascript.xmlimpl.XMLObjectImpl
 */
package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xmlimpl.XML;
import org.mozilla.javascript.xmlimpl.XMLList;
import org.mozilla.javascript.xmlimpl.XMLObjectImpl;
import org.mozilla.javascript.xmlimpl.XmlNode;

class XMLName
extends Ref {
    static final long serialVersionUID = 3832176310755686977L;
    private boolean isAttributeName;
    private boolean isDescendants;
    private XmlNode.QName qname;
    private XMLObjectImpl xmlObject;

    private XMLName() {
    }

    static boolean accept(Object object) {
        String string2;
        try {
            string2 = ScriptRuntime.toString(object);
        }
        catch (EcmaError ecmaError) {
            if ("TypeError".equals((Object)ecmaError.getName())) {
                return false;
            }
            throw ecmaError;
        }
        int n = string2.length();
        if (n != 0 && XMLName.isNCNameStartChar(string2.charAt(0))) {
            for (int i = 1; i != n; ++i) {
                if (XMLName.isNCNameChar(string2.charAt(i))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    private void addAttributes(XMLList xMLList, XML xML) {
        this.addMatchingAttributes(xMLList, xML);
    }

    private void addDescendantAttributes(XMLList xMLList, XML xML) {
        if (xML.isElement()) {
            this.addMatchingAttributes(xMLList, xML);
            XML[] arrxML = xML.getChildren();
            for (int i = 0; i < arrxML.length; ++i) {
                this.addDescendantAttributes(xMLList, arrxML[i]);
            }
        }
    }

    private void addDescendantChildren(XMLList xMLList, XML xML) {
        if (xML.isElement()) {
            XML[] arrxML = xML.getChildren();
            for (int i = 0; i < arrxML.length; ++i) {
                if (this.matches(arrxML[i])) {
                    xMLList.addToList((Object)arrxML[i]);
                }
                this.addDescendantChildren(xMLList, arrxML[i]);
            }
        }
    }

    static XMLName create(String string2, String string3) {
        if (string3 != null) {
            int n = string3.length();
            if (n != 0) {
                char c = string3.charAt(0);
                if (c == '*') {
                    if (n == 1) {
                        return XMLName.formStar();
                    }
                } else if (c == '@') {
                    XMLName xMLName = XMLName.formProperty("", string3.substring(1));
                    xMLName.setAttributeName();
                    return xMLName;
                }
            }
            return XMLName.formProperty(string2, string3);
        }
        throw new IllegalArgumentException();
    }

    @Deprecated
    static XMLName create(XmlNode.QName qName) {
        return XMLName.create(qName, false, false);
    }

    static XMLName create(XmlNode.QName qName, boolean bl, boolean bl2) {
        XMLName xMLName = new XMLName();
        xMLName.qname = qName;
        xMLName.isAttributeName = bl;
        xMLName.isDescendants = bl2;
        return xMLName;
    }

    static XMLName formProperty(String string2, String string3) {
        return XMLName.formProperty(XmlNode.Namespace.create(string2), string3);
    }

    @Deprecated
    static XMLName formProperty(XmlNode.Namespace namespace, String string2) {
        if (string2 != null && string2.equals((Object)"*")) {
            string2 = null;
        }
        XMLName xMLName = new XMLName();
        xMLName.qname = XmlNode.QName.create(namespace, string2);
        return xMLName;
    }

    static XMLName formStar() {
        XMLName xMLName = new XMLName();
        xMLName.qname = XmlNode.QName.create(null, null);
        return xMLName;
    }

    private static boolean isNCNameChar(int n) {
        boolean bl;
        block23 : {
            block22 : {
                block19 : {
                    boolean bl2;
                    block21 : {
                        block20 : {
                            block16 : {
                                boolean bl3;
                                block18 : {
                                    block17 : {
                                        if ((n & -128) != 0) break block16;
                                        if (n >= 97) {
                                            boolean bl4 = false;
                                            if (n <= 122) {
                                                bl4 = true;
                                            }
                                            return bl4;
                                        }
                                        if (n >= 65) {
                                            if (n <= 90) {
                                                return true;
                                            }
                                            boolean bl5 = false;
                                            if (n == 95) {
                                                bl5 = true;
                                            }
                                            return bl5;
                                        }
                                        if (n >= 48) {
                                            boolean bl6 = false;
                                            if (n <= 57) {
                                                bl6 = true;
                                            }
                                            return bl6;
                                        }
                                        if (n == 45) break block17;
                                        bl3 = false;
                                        if (n != 46) break block18;
                                    }
                                    bl3 = true;
                                }
                                return bl3;
                            }
                            if ((n & -8192) != 0) break block19;
                            if (XMLName.isNCNameStartChar(n) || n == 183) break block20;
                            bl2 = false;
                            if (768 > n) break block21;
                            bl2 = false;
                            if (n > 879) break block21;
                        }
                        bl2 = true;
                    }
                    return bl2;
                }
                if (XMLName.isNCNameStartChar(n)) break block22;
                bl = false;
                if (8255 > n) break block23;
                bl = false;
                if (n > 8256) break block23;
            }
            bl = true;
        }
        return bl;
    }

    private static boolean isNCNameStartChar(int n) {
        boolean bl;
        block18 : {
            block17 : {
                block14 : {
                    boolean bl2;
                    block16 : {
                        block15 : {
                            block13 : {
                                if ((n & -128) != 0) break block13;
                                if (n >= 97) {
                                    boolean bl3 = false;
                                    if (n <= 122) {
                                        bl3 = true;
                                    }
                                    return bl3;
                                }
                                if (n >= 65) {
                                    if (n <= 90) {
                                        return true;
                                    }
                                    boolean bl4 = false;
                                    if (n == 95) {
                                        bl4 = true;
                                    }
                                    return bl4;
                                }
                                break block14;
                            }
                            if ((n & -8192) != 0) break block14;
                            if (192 <= n && n <= 214 || 216 <= n && n <= 246 || 248 <= n && n <= 767 || 880 <= n && n <= 893) break block15;
                            bl2 = false;
                            if (895 > n) break block16;
                        }
                        bl2 = true;
                    }
                    return bl2;
                }
                if (8204 <= n && n <= 8205 || 8304 <= n && n <= 8591 || 11264 <= n && n <= 12271 || 12289 <= n && n <= 55295 || 63744 <= n && n <= 64975 || 65008 <= n && n <= 65533) break block17;
                bl = false;
                if (65536 > n) break block18;
                bl = false;
                if (n > 983039) break block18;
            }
            bl = true;
        }
        return bl;
    }

    void addDescendants(XMLList xMLList, XML xML) {
        if (this.isAttributeName()) {
            this.matchDescendantAttributes(xMLList, xML);
            return;
        }
        this.matchDescendantChildren(xMLList, xML);
    }

    void addMatches(XMLList xMLList, XML xML) {
        if (this.isDescendants()) {
            this.addDescendants(xMLList, xML);
            return;
        }
        if (this.isAttributeName()) {
            this.addAttributes(xMLList, xML);
            return;
        }
        XML[] arrxML = xML.getChildren();
        if (arrxML != null) {
            for (int i = 0; i < arrxML.length; ++i) {
                if (!this.matches(arrxML[i])) continue;
                xMLList.addToList((Object)arrxML[i]);
            }
        }
        xMLList.setTargets((XMLObjectImpl)xML, this.toQname());
    }

    void addMatchingAttributes(XMLList xMLList, XML xML) {
        if (xML.isElement()) {
            XML[] arrxML = xML.getAttributes();
            for (int i = 0; i < arrxML.length; ++i) {
                if (!this.matches(arrxML[i])) continue;
                xMLList.addToList((Object)arrxML[i]);
            }
        }
    }

    @Override
    public boolean delete(Context context) {
        XMLObjectImpl xMLObjectImpl = this.xmlObject;
        if (xMLObjectImpl == null) {
            return true;
        }
        xMLObjectImpl.deleteXMLProperty(this);
        return true ^ this.xmlObject.hasXMLProperty(this);
    }

    @Override
    public Object get(Context context) {
        XMLObjectImpl xMLObjectImpl = this.xmlObject;
        if (xMLObjectImpl != null) {
            return xMLObjectImpl.getXMLProperty(this);
        }
        throw ScriptRuntime.undefReadError(Undefined.instance, this.toString());
    }

    XMLList getMyValueOn(XML xML) {
        XMLList xMLList = xML.newXMLList();
        this.addMatches(xMLList, xML);
        return xMLList;
    }

    @Override
    public boolean has(Context context) {
        XMLObjectImpl xMLObjectImpl = this.xmlObject;
        if (xMLObjectImpl == null) {
            return false;
        }
        return xMLObjectImpl.hasXMLProperty(this);
    }

    void initXMLObject(XMLObjectImpl xMLObjectImpl) {
        if (xMLObjectImpl != null) {
            if (this.xmlObject == null) {
                this.xmlObject = xMLObjectImpl;
                return;
            }
            throw new IllegalStateException();
        }
        throw new IllegalArgumentException();
    }

    boolean isAttributeName() {
        return this.isAttributeName;
    }

    boolean isDescendants() {
        return this.isDescendants;
    }

    String localName() {
        if (this.qname.getLocalName() == null) {
            return "*";
        }
        return this.qname.getLocalName();
    }

    XMLList matchDescendantAttributes(XMLList xMLList, XML xML) {
        xMLList.setTargets((XMLObjectImpl)xML, null);
        this.addDescendantAttributes(xMLList, xML);
        return xMLList;
    }

    XMLList matchDescendantChildren(XMLList xMLList, XML xML) {
        xMLList.setTargets((XMLObjectImpl)xML, null);
        this.addDescendantChildren(xMLList, xML);
        return xMLList;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    final boolean matches(XML xML) {
        XmlNode.QName qName = xML.getNodeQname();
        XmlNode.Namespace namespace = qName.getNamespace();
        String string2 = null;
        if (namespace != null) {
            string2 = qName.getNamespace().getUri();
        }
        if (this.isAttributeName) {
            if (!xML.isAttribute()) return false;
            if (this.uri() != null && !this.uri().equals((Object)string2)) return false;
            if (this.localName().equals((Object)"*")) return true;
            if (!this.localName().equals((Object)qName.getLocalName())) return false;
            return true;
        }
        if (this.uri() != null && (!xML.isElement() || !this.uri().equals((Object)string2))) return false;
        if (this.localName().equals((Object)"*")) {
            return true;
        }
        if (!xML.isElement() || !this.localName().equals((Object)qName.getLocalName())) return false;
        return true;
    }

    final boolean matchesElement(XmlNode.QName qName) {
        return !(this.uri() != null && !this.uri().equals((Object)qName.getNamespace().getUri()) || !this.localName().equals((Object)"*") && !this.localName().equals((Object)qName.getLocalName()));
        {
        }
    }

    final boolean matchesLocalName(String string2) {
        return this.localName().equals((Object)"*") || this.localName().equals((Object)string2);
        {
        }
    }

    @Override
    public Object set(Context context, Object object) {
        XMLObjectImpl xMLObjectImpl = this.xmlObject;
        if (xMLObjectImpl != null) {
            if (!this.isDescendants) {
                xMLObjectImpl.putXMLProperty(this, object);
                return object;
            }
            throw Kit.codeBug();
        }
        throw ScriptRuntime.undefWriteError(Undefined.instance, this.toString(), object);
    }

    void setAttributeName() {
        this.isAttributeName = true;
    }

    @Deprecated
    void setIsDescendants() {
        this.isDescendants = true;
    }

    void setMyValueOn(XML xML, Object object) {
        XMLList xMLList;
        XMLObjectImpl xMLObjectImpl;
        if (object == null) {
            object = "null";
        } else if (object instanceof Undefined) {
            object = "undefined";
        }
        if (this.isAttributeName()) {
            xML.setAttribute(this, object);
            return;
        }
        if (this.uri() == null && this.localName().equals((Object)"*")) {
            xML.setChildren(object);
            return;
        }
        if (object instanceof XMLObjectImpl) {
            xMLObjectImpl = (XMLObjectImpl)object;
            if (xMLObjectImpl instanceof XML && ((XML)xMLObjectImpl).isAttribute()) {
                xMLObjectImpl = xML.makeXmlFromString(this, xMLObjectImpl.toString());
            }
            if (xMLObjectImpl instanceof XMLList) {
                for (int i = 0; i < xMLObjectImpl.length(); ++i) {
                    XML xML2 = ((XMLList)xMLObjectImpl).item(i);
                    if (!xML2.isAttribute()) continue;
                    ((XMLList)xMLObjectImpl).replace(i, xML.makeXmlFromString(this, xML2.toString()));
                }
            }
        } else {
            xMLObjectImpl = xML.makeXmlFromString(this, ScriptRuntime.toString(object));
        }
        if ((xMLList = xML.getPropertyList(this)).length() == 0) {
            xML.appendChild((Object)xMLObjectImpl);
            return;
        }
        for (int i = 1; i < xMLList.length(); ++i) {
            xML.removeChild(xMLList.item(i).childIndex());
        }
        xML.replace(xMLList.item(0).childIndex(), (Object)xMLObjectImpl);
    }

    final XmlNode.QName toQname() {
        return this.qname;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.isDescendants) {
            stringBuilder.append("..");
        }
        if (this.isAttributeName) {
            stringBuilder.append('@');
        }
        if (this.uri() == null) {
            stringBuilder.append('*');
            if (this.localName().equals((Object)"*")) {
                return stringBuilder.toString();
            }
        } else {
            stringBuilder.append('\"');
            stringBuilder.append(this.uri());
            stringBuilder.append('\"');
        }
        stringBuilder.append(':');
        stringBuilder.append(this.localName());
        return stringBuilder.toString();
    }

    String uri() {
        if (this.qname.getNamespace() == null) {
            return null;
        }
        return this.qname.getNamespace().getUri();
    }
}

