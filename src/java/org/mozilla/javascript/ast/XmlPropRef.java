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
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.XmlRef;

public class XmlPropRef
extends XmlRef {
    private Name propName;

    public XmlPropRef() {
        this.type = 80;
    }

    public XmlPropRef(int n) {
        super(n);
        this.type = 80;
    }

    public XmlPropRef(int n, int n2) {
        super(n, n2);
        this.type = 80;
    }

    public Name getPropName() {
        return this.propName;
    }

    public void setPropName(Name name) {
        this.assertNotNull(name);
        this.propName = name;
        name.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        if (this.isAttributeAccess()) {
            stringBuilder.append("@");
        }
        if (this.namespace != null) {
            stringBuilder.append(this.namespace.toSource(0));
            stringBuilder.append("::");
        }
        stringBuilder.append(this.propName.toSource(0));
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            if (this.namespace != null) {
                this.namespace.visit(nodeVisitor);
            }
            this.propName.visit(nodeVisitor);
        }
    }
}

