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
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.NodeVisitor;

public class SwitchCase
extends AstNode {
    private AstNode expression;
    private List<AstNode> statements;

    public SwitchCase() {
        this.type = 116;
    }

    public SwitchCase(int n) {
        super(n);
        this.type = 116;
    }

    public SwitchCase(int n, int n2) {
        super(n, n2);
        this.type = 116;
    }

    public void addStatement(AstNode astNode) {
        this.assertNotNull(astNode);
        if (this.statements == null) {
            this.statements = new ArrayList();
        }
        this.setLength(astNode.getPosition() + astNode.getLength() - this.getPosition());
        this.statements.add((Object)astNode);
        astNode.setParent(this);
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public List<AstNode> getStatements() {
        return this.statements;
    }

    public boolean isDefault() {
        return this.expression == null;
    }

    public void setExpression(AstNode astNode) {
        this.expression = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setStatements(List<AstNode> list) {
        List<AstNode> list2 = this.statements;
        if (list2 != null) {
            list2.clear();
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addStatement((AstNode)iterator.next());
        }
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        if (this.expression == null) {
            stringBuilder.append("default:\n");
        } else {
            stringBuilder.append("case ");
            stringBuilder.append(this.expression.toSource(0));
            stringBuilder.append(":");
            if (this.getInlineComment() != null) {
                stringBuilder.append(this.getInlineComment().toSource(n + 1));
            }
            stringBuilder.append("\n");
        }
        List<AstNode> list = this.statements;
        if (list != null) {
            for (AstNode astNode : list) {
                stringBuilder.append(astNode.toSource(n + 1));
                if (astNode.getType() != 162 || ((Comment)astNode).getCommentType() != Token.CommentType.LINE) continue;
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            List<AstNode> list;
            AstNode astNode = this.expression;
            if (astNode != null) {
                astNode.visit(nodeVisitor);
            }
            if ((list = this.statements) != null) {
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    ((AstNode)iterator.next()).visit(nodeVisitor);
                }
            }
        }
    }
}

