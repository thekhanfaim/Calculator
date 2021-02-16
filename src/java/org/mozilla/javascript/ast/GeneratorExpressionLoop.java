/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.UnsupportedOperationException
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.NodeVisitor;

public class GeneratorExpressionLoop
extends ForInLoop {
    public GeneratorExpressionLoop() {
    }

    public GeneratorExpressionLoop(int n) {
        super(n);
    }

    public GeneratorExpressionLoop(int n, int n2) {
        super(n, n2);
    }

    @Override
    public boolean isForEach() {
        return false;
    }

    @Override
    public void setIsForEach(boolean bl) {
        throw new UnsupportedOperationException("this node type does not support for each");
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append(" for ");
        String string = this.isForEach() ? "each " : "";
        stringBuilder.append(string);
        stringBuilder.append("(");
        stringBuilder.append(this.iterator.toSource(0));
        String string2 = this.isForOf() ? " of " : " in ";
        stringBuilder.append(string2);
        stringBuilder.append(this.iteratedObject.toSource(0));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.iterator.visit(nodeVisitor);
            this.iteratedObject.visit(nodeVisitor);
        }
    }
}

