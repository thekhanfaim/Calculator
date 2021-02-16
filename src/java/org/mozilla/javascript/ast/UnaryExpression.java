/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class UnaryExpression
extends AstNode {
    private boolean isPostfix;
    private AstNode operand;

    public UnaryExpression() {
    }

    public UnaryExpression(int n) {
        super(n);
    }

    public UnaryExpression(int n, int n2) {
        super(n, n2);
    }

    public UnaryExpression(int n, int n2, AstNode astNode) {
        this(n, n2, astNode, false);
    }

    public UnaryExpression(int n, int n2, AstNode astNode, boolean bl) {
        this.assertNotNull(astNode);
        int n3 = bl ? astNode.getPosition() : n2;
        int n4 = bl ? n2 + 2 : astNode.getPosition() + astNode.getLength();
        this.setBounds(n3, n4);
        this.setOperator(n);
        this.setOperand(astNode);
        this.isPostfix = bl;
    }

    public AstNode getOperand() {
        return this.operand;
    }

    public int getOperator() {
        return this.type;
    }

    public boolean isPostfix() {
        return this.isPostfix;
    }

    public boolean isPrefix() {
        return true ^ this.isPostfix;
    }

    public void setIsPostfix(boolean bl) {
        this.isPostfix = bl;
    }

    public void setOperand(AstNode astNode) {
        this.assertNotNull(astNode);
        this.operand = astNode;
        astNode.setParent(this);
    }

    public void setOperator(int n) {
        if (Token.isValidToken(n)) {
            this.setType(n);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid token: ");
        stringBuilder.append(n);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        int n2 = this.getType();
        if (!this.isPostfix) {
            stringBuilder.append(UnaryExpression.operatorToString(n2));
            if (n2 == 32 || n2 == 31 || n2 == 127) {
                stringBuilder.append(" ");
            }
        }
        stringBuilder.append(this.operand.toSource());
        if (this.isPostfix) {
            stringBuilder.append(UnaryExpression.operatorToString(n2));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.operand.visit(nodeVisitor);
        }
    }
}

