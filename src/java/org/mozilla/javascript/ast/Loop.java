/*
 * Decompiled with CFR 0.0.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Scope;

public abstract class Loop
extends Scope {
    protected AstNode body;
    protected int lp = -1;
    protected int rp = -1;

    public Loop() {
    }

    public Loop(int n) {
        super(n);
    }

    public Loop(int n, int n2) {
        super(n, n2);
    }

    public AstNode getBody() {
        return this.body;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setBody(AstNode astNode) {
        this.body = astNode;
        this.setLength(astNode.getPosition() + astNode.getLength() - this.getPosition());
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
}

