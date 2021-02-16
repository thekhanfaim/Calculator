/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
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
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableInitializer;

public class VariableDeclaration
extends AstNode {
    private boolean isStatement;
    private List<VariableInitializer> variables = new ArrayList();

    public VariableDeclaration() {
        this.type = 123;
    }

    public VariableDeclaration(int n) {
        super(n);
        this.type = 123;
    }

    public VariableDeclaration(int n, int n2) {
        super(n, n2);
        this.type = 123;
    }

    private String declTypeName() {
        return Token.typeToName(this.type).toLowerCase();
    }

    public void addVariable(VariableInitializer variableInitializer) {
        this.assertNotNull(variableInitializer);
        this.variables.add((Object)variableInitializer);
        variableInitializer.setParent(this);
    }

    public List<VariableInitializer> getVariables() {
        return this.variables;
    }

    public boolean isConst() {
        return this.type == 155;
    }

    public boolean isLet() {
        return this.type == 154;
    }

    public boolean isStatement() {
        return this.isStatement;
    }

    public boolean isVar() {
        return this.type == 123;
    }

    public void setIsStatement(boolean bl) {
        this.isStatement = bl;
    }

    @Override
    public Node setType(int n) {
        if (n != 123 && n != 155 && n != 154) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("invalid decl type: ");
            stringBuilder.append(n);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        return super.setType(n);
    }

    public void setVariables(List<VariableInitializer> list) {
        this.assertNotNull(list);
        this.variables.clear();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addVariable((VariableInitializer)iterator.next());
        }
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.makeIndent(n));
        stringBuilder.append(this.declTypeName());
        stringBuilder.append(" ");
        this.printList(this.variables, stringBuilder);
        if (this.isStatement()) {
            stringBuilder.append(";");
        }
        if (this.getInlineComment() != null) {
            stringBuilder.append(this.getInlineComment().toSource(n));
            stringBuilder.append("\n");
        } else if (this.isStatement()) {
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            Iterator iterator = this.variables.iterator();
            while (iterator.hasNext()) {
                ((AstNode)iterator.next()).visit(nodeVisitor);
            }
        }
    }
}

