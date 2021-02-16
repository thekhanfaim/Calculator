/*
 * Decompiled with CFR 0.0.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;

public abstract class XmlFragment
extends AstNode {
    public XmlFragment() {
        this.type = 146;
    }

    public XmlFragment(int n) {
        super(n);
        this.type = 146;
    }

    public XmlFragment(int n, int n2) {
        super(n, n2);
        this.type = 146;
    }
}

