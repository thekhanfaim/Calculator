/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class Yield
extends AstNode {
    private AstNode value;

    public Yield() {
        this.type = 73;
    }

    public Yield(int n) {
        super(n);
        this.type = 73;
    }

    public Yield(int n, int n2) {
        super(n, n2);
        this.type = 73;
    }

    public Yield(int n, int n2, AstNode astNode) {
        super(n, n2);
        this.type = 73;
        this.setValue(astNode);
    }

    public AstNode getValue() {
        return this.value;
    }

    public void setValue(AstNode astNode) {
        this.value = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    @Override
    public String toSource(int n) {
        if (this.value == null) {
            return "yield";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("yield ");
        stringBuilder.append(this.value.toSource(0));
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        AstNode astNode;
        if (nodeVisitor.visit(this) && (astNode = this.value) != null) {
            astNode.visit(nodeVisitor);
        }
    }
}

