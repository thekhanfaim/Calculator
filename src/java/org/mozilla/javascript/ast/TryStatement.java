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
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.NodeVisitor;

public class TryStatement
extends AstNode {
    private static final List<CatchClause> NO_CATCHES = Collections.unmodifiableList((List)new ArrayList());
    private List<CatchClause> catchClauses;
    private AstNode finallyBlock;
    private int finallyPosition = -1;
    private AstNode tryBlock;

    public TryStatement() {
        this.type = 82;
    }

    public TryStatement(int n) {
        super(n);
        this.type = 82;
    }

    public TryStatement(int n, int n2) {
        super(n, n2);
        this.type = 82;
    }

    public void addCatchClause(CatchClause catchClause) {
        this.assertNotNull(catchClause);
        if (this.catchClauses == null) {
            this.catchClauses = new ArrayList();
        }
        this.catchClauses.add((Object)catchClause);
        catchClause.setParent(this);
    }

    public List<CatchClause> getCatchClauses() {
        List<CatchClause> list = this.catchClauses;
        if (list != null) {
            return list;
        }
        return NO_CATCHES;
    }

    public AstNode getFinallyBlock() {
        return this.finallyBlock;
    }

    public int getFinallyPosition() {
        return this.finallyPosition;
    }

    public AstNode getTryBlock() {
        return this.tryBlock;
    }

    public void setCatchClauses(List<CatchClause> list) {
        if (list == null) {
            this.catchClauses = null;
            return;
        }
        List<CatchClause> list2 = this.catchClauses;
        if (list2 != null) {
            list2.clear();
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addCatchClause((CatchClause)iterator.next());
        }
    }

    public void setFinallyBlock(AstNode astNode) {
        this.finallyBlock = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setFinallyPosition(int n) {
        this.finallyPosition = n;
    }

    public void setTryBlock(AstNode astNode) {
        this.assertNotNull(astNode);
        this.tryBlock = astNode;
        astNode.setParent(this);
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder(250);
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append("try ");
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource(n + 1));
            stringBuilder.append("\n");
        }
        stringBuilder.append(this.tryBlock.toSource(n).trim());
        Iterator iterator = this.getCatchClauses().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append(((CatchClause)iterator.next()).toSource(n));
        }
        if (this.finallyBlock != null) {
            stringBuilder.append(" finally ");
            stringBuilder.append(this.finallyBlock.toSource(n));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.tryBlock.visit(nodeVisitor);
            Iterator iterator = this.getCatchClauses().iterator();
            while (iterator.hasNext()) {
                ((CatchClause)iterator.next()).visit(nodeVisitor);
            }
            AstNode astNode = this.finallyBlock;
            if (astNode != null) {
                astNode.visit(nodeVisitor);
            }
        }
    }
}

