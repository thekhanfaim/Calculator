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
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.XmlRef;

public class XmlElemRef
extends XmlRef {
    private AstNode indexExpr;
    private int lb = -1;
    private int rb = -1;

    public XmlElemRef() {
        this.type = 78;
    }

    public XmlElemRef(int n) {
        super(n);
        this.type = 78;
    }

    public XmlElemRef(int n, int n2) {
        super(n, n2);
        this.type = 78;
    }

    public AstNode getExpression() {
        return this.indexExpr;
    }

    public int getLb() {
        return this.lb;
    }

    public int getRb() {
        return this.rb;
    }

    public void setBrackets(int n, int n2) {
        this.lb = n;
        this.rb = n2;
    }

    public void setExpression(AstNode astNode) {
        this.assertNotNull(astNode);
        this.indexExpr = astNode;
        astNode.setParent(this);
    }

    public void setLb(int n) {
        this.lb = n;
    }

    public void setRb(int n) {
        this.rb = n;
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        if (this.isAttributeAccess()) {
            stringBuilder.append("@");
        }
        if (this.namespace != null) {
            stringBuilder.append(this.namespace.toSource(0));
            stringBuilder.append("::");
        }
        stringBuilder.append("[");
        stringBuilder.append(this.indexExpr.toSource(0));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            if (this.namespace != null) {
                this.namespace.visit(nodeVisitor);
            }
            this.indexExpr.visit(nodeVisitor);
        }
    }
}

