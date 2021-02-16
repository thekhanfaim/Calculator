/*
 * Decompiled with CFR 0.0.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;

public abstract class XmlRef
extends AstNode {
    protected int atPos = -1;
    protected int colonPos = -1;
    protected Name namespace;

    public XmlRef() {
    }

    public XmlRef(int n) {
        super(n);
    }

    public XmlRef(int n, int n2) {
        super(n, n2);
    }

    public int getAtPos() {
        return this.atPos;
    }

    public int getColonPos() {
        return this.colonPos;
    }

    public Name getNamespace() {
        return this.namespace;
    }

    public boolean isAttributeAccess() {
        return this.atPos >= 0;
    }

    public void setAtPos(int n) {
        this.atPos = n;
    }

    public void setColonPos(int n) {
        this.colonPos = n;
    }

    public void setNamespace(Name name) {
        this.namespace = name;
        if (name != null) {
            name.setParent(this);
        }
    }
}

