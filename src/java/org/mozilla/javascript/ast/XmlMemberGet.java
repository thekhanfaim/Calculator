/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.XmlRef;

public class XmlMemberGet
extends InfixExpression {
    public XmlMemberGet() {
        this.type = 144;
    }

    public XmlMemberGet(int n) {
        super(n);
        this.type = 144;
    }

    public XmlMemberGet(int n, int n2) {
        super(n, n2);
        this.type = 144;
    }

    public XmlMemberGet(int n, int n2, AstNode astNode, XmlRef xmlRef) {
        super(n, n2, astNode, xmlRef);
        this.type = 144;
    }

    public XmlMemberGet(AstNode astNode, XmlRef xmlRef) {
        super(astNode, xmlRef);
        this.type = 144;
    }

    public XmlMemberGet(AstNode astNode, XmlRef xmlRef, int n) {
        super(144, astNode, (AstNode)xmlRef, n);
        this.type = 144;
    }

    private String dotsToString() {
        int n = this.getType();
        if (n != 109) {
            if (n == 144) {
                return "..";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid type of XmlMemberGet: ");
            stringBuilder.append(this.getType());
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        return ".";
    }

    public XmlRef getMemberRef() {
        return (XmlRef)this.getRight();
    }

    public AstNode getTarget() {
        return this.getLeft();
    }

    public void setProperty(XmlRef xmlRef) {
        this.setRight(xmlRef);
    }

    public void setTarget(AstNode astNode) {
        this.setLeft(astNode);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append(this.getLeft().toSource(0));
        stringBuilder.append(this.dotsToString());
        stringBuilder.append(this.getRight().toSource(0));
        return stringBuilder.toString();
    }
}

