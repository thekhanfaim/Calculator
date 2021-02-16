/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Scope;

public class Symbol {
    private Scope containingTable;
    private int declType;
    private int index = -1;
    private String name;
    private Node node;

    public Symbol() {
    }

    public Symbol(int n, String string2) {
        this.setName(string2);
        this.setDeclType(n);
    }

    public Scope getContainingTable() {
        return this.containingTable;
    }

    public int getDeclType() {
        return this.declType;
    }

    public String getDeclTypeName() {
        return Token.typeToName(this.declType);
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public Node getNode() {
        return this.node;
    }

    public void setContainingTable(Scope scope) {
        this.containingTable = scope;
    }

    public void setDeclType(int n) {
        if (n != 110 && n != 88 && n != 123 && n != 154 && n != 155) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid declType: ");
            stringBuilder.append(n);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.declType = n;
    }

    public void setIndex(int n) {
        this.index = n;
    }

    public void setName(String string2) {
        this.name = string2;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Symbol (");
        stringBuilder.append(this.getDeclTypeName());
        stringBuilder.append(") name=");
        stringBuilder.append(this.name);
        if (this.node != null) {
            stringBuilder.append(" line=");
            stringBuilder.append(this.node.getLineno());
        }
        return stringBuilder.toString();
    }
}

