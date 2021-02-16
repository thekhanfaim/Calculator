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
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.Scope;

public class GeneratorExpression
extends Scope {
    private AstNode filter;
    private int ifPosition = -1;
    private List<GeneratorExpressionLoop> loops = new ArrayList();
    private int lp = -1;
    private AstNode result;
    private int rp = -1;

    public GeneratorExpression() {
        this.type = 163;
    }

    public GeneratorExpression(int n) {
        super(n);
        this.type = 163;
    }

    public GeneratorExpression(int n, int n2) {
        super(n, n2);
        this.type = 163;
    }

    public void addLoop(GeneratorExpressionLoop generatorExpressionLoop) {
        this.assertNotNull(generatorExpressionLoop);
        this.loops.add((Object)generatorExpressionLoop);
        generatorExpressionLoop.setParent(this);
    }

    public AstNode getFilter() {
        return this.filter;
    }

    public int getFilterLp() {
        return this.lp;
    }

    public int getFilterRp() {
        return this.rp;
    }

    public int getIfPosition() {
        return this.ifPosition;
    }

    public List<GeneratorExpressionLoop> getLoops() {
        return this.loops;
    }

    public AstNode getResult() {
        return this.result;
    }

    public void setFilter(AstNode astNode) {
        this.filter = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setFilterLp(int n) {
        this.lp = n;
    }

    public void setFilterRp(int n) {
        this.rp = n;
    }

    public void setIfPosition(int n) {
        this.ifPosition = n;
    }

    public void setLoops(List<GeneratorExpressionLoop> list) {
        this.assertNotNull(list);
        this.loops.clear();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addLoop((GeneratorExpressionLoop)iterator.next());
        }
    }

    public void setResult(AstNode astNode) {
        this.assertNotNull(astNode);
        this.result = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder(250);
        stringBuilder.append("(");
        stringBuilder.append(this.result.toSource(0));
        Iterator iterator = this.loops.iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(((GeneratorExpressionLoop)iterator.next()).toSource(0));
        }
        if (this.filter != null) {
            stringBuilder.append(" if (");
            stringBuilder.append(this.filter.toSource(0));
            stringBuilder.append(")");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (!nodeVisitor.visit(this)) {
            return;
        }
        this.result.visit(nodeVisitor);
        Iterator iterator = this.loops.iterator();
        while (iterator.hasNext()) {
            ((GeneratorExpressionLoop)iterator.next()).visit(nodeVisitor);
        }
        AstNode astNode = this.filter;
        if (astNode != null) {
            astNode.visit(nodeVisitor);
        }
    }
}

