/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;

public class XmlDotQuery
extends InfixExpression {
    private int rp = -1;

    public XmlDotQuery() {
        this.type = 147;
    }

    public XmlDotQuery(int n) {
        super(n);
        this.type = 147;
    }

    public XmlDotQuery(int n, int n2) {
        super(n, n2);
        this.type = 147;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int n) {
        this.rp = n;
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append(this.getLeft().toSource(0));
        stringBuilder.append(".(");
        stringBuilder.append(this.getRight().toSource(0));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

