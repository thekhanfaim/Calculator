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
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.NodeVisitor;

public class DoLoop
extends Loop {
    private AstNode condition;
    private int whilePosition = -1;

    public DoLoop() {
        this.type = 119;
    }

    public DoLoop(int n) {
        super(n);
        this.type = 119;
    }

    public DoLoop(int n, int n2) {
        super(n, n2);
        this.type = 119;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public int getWhilePosition() {
        return this.whilePosition;
    }

    public void setCondition(AstNode astNode) {
        this.assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    public void setWhilePosition(int n) {
        this.whilePosition = n;
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("do ");
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource(n + 1));
            stringBuilder.append("\n");
        }
        stringBuilder.append(this.body.toSource(n).trim());
        stringBuilder.append(" while (");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append(");\n");
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.body.visit(nodeVisitor);
            this.condition.visit(nodeVisitor);
        }
    }
}

