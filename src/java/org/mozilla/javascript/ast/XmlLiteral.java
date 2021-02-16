/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Iterator
 *  java.util.List
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.XmlFragment;

public class XmlLiteral
extends AstNode {
    private List<XmlFragment> fragments = new ArrayList();

    public XmlLiteral() {
        this.type = 146;
    }

    public XmlLiteral(int n) {
        super(n);
        this.type = 146;
    }

    public XmlLiteral(int n, int n2) {
        super(n, n2);
        this.type = 146;
    }

    public void addFragment(XmlFragment xmlFragment) {
        this.assertNotNull(xmlFragment);
        this.fragments.add((Object)xmlFragment);
        xmlFragment.setParent(this);
    }

    public List<XmlFragment> getFragments() {
        return this.fragments;
    }

    public void setFragments(List<XmlFragment> list) {
        this.assertNotNull(list);
        this.fragments.clear();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addFragment((XmlFragment)iterator.next());
        }
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder(250);
        Iterator iterator = this.fragments.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(((XmlFragment)iterator.next()).toSource(0));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            Iterator iterator = this.fragments.iterator();
            while (iterator.hasNext()) {
                ((XmlFragment)iterator.next()).visit(nodeVisitor);
            }
        }
    }
}

