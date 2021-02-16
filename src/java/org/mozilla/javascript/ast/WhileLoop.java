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

public class WhileLoop
extends Loop {
    private AstNode condition;

    public WhileLoop() {
        this.type = 118;
    }

    public WhileLoop(int n) {
        super(n);
        this.type = 118;
    }

    public WhileLoop(int n, int n2) {
        super(n, n2);
        this.type = 118;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode astNode) {
        this.assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("while (");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append(") ");
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource(n + 1));
            stringBuilder.append("\n");
        }
        if (this.body.getType() == 130) {
            stringBuilder.append(this.body.toSource(n).trim());
            stringBuilder.append("\n");
        } else {
            if (this.getInlineComment() == null) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(this.body.toSource(n + 1));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.condition.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}

