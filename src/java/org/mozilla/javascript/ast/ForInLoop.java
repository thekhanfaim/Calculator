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

public class ForInLoop
extends Loop {
    protected int eachPosition = -1;
    protected int inPosition = -1;
    protected boolean isForEach;
    protected boolean isForOf;
    protected AstNode iteratedObject;
    protected AstNode iterator;

    public ForInLoop() {
        this.type = 120;
    }

    public ForInLoop(int n) {
        super(n);
        this.type = 120;
    }

    public ForInLoop(int n, int n2) {
        super(n, n2);
        this.type = 120;
    }

    public int getEachPosition() {
        return this.eachPosition;
    }

    public int getInPosition() {
        return this.inPosition;
    }

    public AstNode getIteratedObject() {
        return this.iteratedObject;
    }

    public AstNode getIterator() {
        return this.iterator;
    }

    public boolean isForEach() {
        return this.isForEach;
    }

    public boolean isForOf() {
        return this.isForOf;
    }

    public void setEachPosition(int n) {
        this.eachPosition = n;
    }

    public void setInPosition(int n) {
        this.inPosition = n;
    }

    public void setIsForEach(boolean bl) {
        this.isForEach = bl;
    }

    public void setIsForOf(boolean bl) {
        this.isForOf = bl;
    }

    public void setIteratedObject(AstNode astNode) {
        this.assertNotNull(astNode);
        this.iteratedObject = astNode;
        astNode.setParent(this);
    }

    public void setIterator(AstNode astNode) {
        this.assertNotNull(astNode);
        this.iterator = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("for ");
        if (this.isForEach()) {
            stringBuilder.append("each ");
        }
        stringBuilder.append("(");
        stringBuilder.append(this.iterator.toSource(0));
        if (this.isForOf) {
            stringBuilder.append(" of ");
        } else {
            stringBuilder.append(" in ");
        }
        stringBuilder.append(this.iteratedObject.toSource(0));
        stringBuilder.append(") ");
        if (this.body.getType() == 130) {
            stringBuilder.append(this.body.toSource(n).trim());
            stringBuilder.append("\n");
        } else {
            stringBuilder.append("\n");
            stringBuilder.append(this.body.toSource(n + 1));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.iterator.visit(nodeVisitor);
            this.iteratedObject.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}

