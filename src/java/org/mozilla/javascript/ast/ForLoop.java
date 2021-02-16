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

public class ForLoop
extends Loop {
    private AstNode condition;
    private AstNode increment;
    private AstNode initializer;

    public ForLoop() {
        this.type = 120;
    }

    public ForLoop(int n) {
        super(n);
        this.type = 120;
    }

    public ForLoop(int n, int n2) {
        super(n, n2);
        this.type = 120;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public AstNode getIncrement() {
        return this.increment;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public void setCondition(AstNode astNode) {
        this.assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    public void setIncrement(AstNode astNode) {
        this.assertNotNull(astNode);
        this.increment = astNode;
        astNode.setParent(this);
    }

    public void setInitializer(AstNode astNode) {
        this.assertNotNull(astNode);
        this.initializer = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("for (");
        stringBuilder.append(this.initializer.toSource(0));
        stringBuilder.append("; ");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append("; ");
        stringBuilder.append(this.increment.toSource(0));
        stringBuilder.append(") ");
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource());
            stringBuilder.append("\n");
        }
        if (this.body.getType() == 130) {
            String string = this.body.toSource(n);
            if (this.getInlineComment() == null) {
                string = string.trim();
            }
            stringBuilder.append(string);
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
            this.initializer.visit(nodeVisitor);
            this.condition.visit(nodeVisitor);
            this.increment.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}

