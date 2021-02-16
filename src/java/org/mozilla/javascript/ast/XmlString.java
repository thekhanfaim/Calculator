/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.XmlFragment;

public class XmlString
extends XmlFragment {
    private String xml;

    public XmlString() {
    }

    public XmlString(int n) {
        super(n);
    }

    public XmlString(int n, String string) {
        super(n);
        this.setXml(string);
    }

    public String getXml() {
        return this.xml;
    }

    public void setXml(String string) {
        this.assertNotNull(string);
        this.xml = string;
        this.setLength(string.length());
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append(this.xml);
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }
}

