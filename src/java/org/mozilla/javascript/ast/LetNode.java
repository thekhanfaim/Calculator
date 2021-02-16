/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.List
 */
package org.mozilla.javascript.ast;

import java.util.List;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

public class LetNode
extends Scope {
    private AstNode body;
    private int lp = -1;
    private int rp = -1;
    private VariableDeclaration variables;

    public LetNode() {
        this.type = 159;
    }

    public LetNode(int n) {
        super(n);
        this.type = 159;
    }

    public LetNode(int n, int n2) {
        super(n, n2);
        this.type = 159;
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

    public VariableDeclaration getVariables() {
        return this.variables;
    }

    public void setBody(AstNode astNode) {
        this.body = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
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

    public void setVariables(VariableDeclaration variableDeclaration) {
        this.assertNotNull(variableDeclaration);
        this.variables = variableDeclaration;
        variableDeclaration.setParent(this);
    }

    @Override
    public String toSource(int n) {
        String string = this.makeIndent(n);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.append("let (");
        this.printList(this.variables.getVariables(), stringBuilder);
        stringBuilder.append(") ");
        AstNode astNode = this.body;
        if (astNode != null) {
            stringBuilder.append(astNode.toSource(n));
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.variables.visit(nodeVisitor);
            AstNode astNode = this.body;
            if (astNode != null) {
                astNode.visit(nodeVisitor);
            }
        }
    }
}

