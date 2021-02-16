/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.SwitchCase;

public class SwitchStatement
extends Jump {
    private static final List<SwitchCase> NO_CASES = Collections.unmodifiableList((List)new ArrayList());
    private List<SwitchCase> cases;
    private AstNode expression;
    private int lp = -1;
    private int rp = -1;

    public SwitchStatement() {
        this.type = 115;
    }

    public SwitchStatement(int n) {
        this.type = 115;
        this.position = n;
    }

    public SwitchStatement(int n, int n2) {
        this.type = 115;
        this.position = n;
        this.length = n2;
    }

    public void addCase(SwitchCase switchCase) {
        this.assertNotNull(switchCase);
        if (this.cases == null) {
            this.cases = new ArrayList();
        }
        this.cases.add((Object)switchCase);
        switchCase.setParent(this);
    }

    public List<SwitchCase> getCases() {
        List<SwitchCase> list = this.cases;
        if (list != null) {
            return list;
        }
        return NO_CASES;
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

    public void setCases(List<SwitchCase> list) {
        if (list == null) {
            this.cases = null;
            return;
        }
        List<SwitchCase> list2 = this.cases;
        if (list2 != null) {
            list2.clear();
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addCase((SwitchCase)iterator.next());
        }
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

    @Override
    public String toSource(int n) {
        String string2 = this.makeIndent(n);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        stringBuilder.append("switch (");
        stringBuilder.append(this.expression.toSource(0));
        stringBuilder.append(") {\n");
        List<SwitchCase> list = this.cases;
        if (list != null) {
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                stringBuilder.append(((SwitchCase)iterator.next()).toSource(n + 1));
            }
        }
        stringBuilder.append(string2);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.expression.visit(nodeVisitor);
            Iterator iterator = this.getCases().iterator();
            while (iterator.hasNext()) {
                ((SwitchCase)iterator.next()).visit(nodeVisitor);
            }
        }
    }
}

