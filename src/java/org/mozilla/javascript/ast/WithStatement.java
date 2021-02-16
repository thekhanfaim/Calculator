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

public class WithStatement
extends AstNode {
    private AstNode expression;
    private int lp = -1;
    private int rp = -1;
    private AstNode statement;

    public WithStatement() {
        this.type = 124;
    }

    public WithStatement(int n) {
        super(n);
        this.type = 124;
    }

    public WithStatement(int n, int n2) {
        super(n, n2);
        this.type = 124;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public AstNode getStatement() {
        return this.statement;
    }

    public void setExpression(AstNode astNode) {
        this.assertNotNull(astNode);
        this.expression = astNode;
        astNode.setParent(this);
    }

    public void setLp(int n) {
        this.lp = n;
    }

    public void setParens(int n, int n2) {
        this.lp = n;
        this.rp = n2;
    }

    public void setRp(int n) {
        this.rp = n;
    }

    public void setStatement(AstNode astNode) {
        this.assertNotNull(astNode);
        this.statement = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("with (");
        stringBuilder.append(this.expression.toSource(0));
        stringBuilder.append(") ");
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource(n + 1));
        }
        if (this.statement.getType() == 130) {
            if (this.getInlineComment() != null) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(this.statement.toSource(n).trim());
            stringBuilder.append("\n");
        } else {
            stringBuilder.append("\n");
            stringBuilder.append(this.statement.toSource(n + 1));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.expression.visit(nodeVisitor);
            this.statement.visit(nodeVisitor);
        }
    }
}

