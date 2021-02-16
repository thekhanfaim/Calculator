/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.Symbol;

public class ScriptNode
extends Scope {
    private List<FunctionNode> EMPTY_LIST = Collections.emptyList();
    private Object compilerData;
    private String encodedSource;
    private int encodedSourceEnd = -1;
    private int encodedSourceStart = -1;
    private int endLineno = -1;
    private List<FunctionNode> functions;
    private boolean inStrictMode;
    private boolean[] isConsts;
    private int paramCount = 0;
    private List<RegExpLiteral> regexps;
    private String sourceName;
    private List<Symbol> symbols = new ArrayList(4);
    private int tempNumber = 0;
    private String[] variableNames;

    public ScriptNode() {
        this.top = this;
        this.type = 137;
    }

    public ScriptNode(int n) {
        super(n);
        this.top = this;
        this.type = 137;
    }

    public int addFunction(FunctionNode functionNode) {
        if (functionNode == null) {
            ScriptNode.codeBug();
        }
        if (this.functions == null) {
            this.functions = new ArrayList();
        }
        this.functions.add((Object)functionNode);
        return -1 + this.functions.size();
    }

    public void addRegExp(RegExpLiteral regExpLiteral) {
        if (regExpLiteral == null) {
            ScriptNode.codeBug();
        }
        if (this.regexps == null) {
            this.regexps = new ArrayList();
        }
        this.regexps.add((Object)regExpLiteral);
        regExpLiteral.putIntProp(4, -1 + this.regexps.size());
    }

    void addSymbol(Symbol symbol) {
        if (this.variableNames != null) {
            ScriptNode.codeBug();
        }
        if (symbol.getDeclType() == 88) {
            this.paramCount = 1 + this.paramCount;
        }
        this.symbols.add((Object)symbol);
    }

    public void flattenSymbolTable(boolean bl) {
        if (!bl) {
            ArrayList arrayList = new ArrayList();
            if (this.symbolTable != null) {
                for (int i = 0; i < this.symbols.size(); ++i) {
                    Symbol symbol = (Symbol)this.symbols.get(i);
                    if (symbol.getContainingTable() != this) continue;
                    arrayList.add((Object)symbol);
                }
            }
            this.symbols = arrayList;
        }
        this.variableNames = new String[this.symbols.size()];
        this.isConsts = new boolean[this.symbols.size()];
        for (int i = 0; i < this.symbols.size(); ++i) {
            Symbol symbol = (Symbol)this.symbols.get(i);
            this.variableNames[i] = symbol.getName();
            boolean[] arrbl = this.isConsts;
            boolean bl2 = symbol.getDeclType() == 155;
            arrbl[i] = bl2;
            symbol.setIndex(i);
        }
    }

    public int getBaseLineno() {
        return this.lineno;
    }

    public Object getCompilerData() {
        return this.compilerData;
    }

    public String getEncodedSource() {
        return this.encodedSource;
    }

    public int getEncodedSourceEnd() {
        return this.encodedSourceEnd;
    }

    public int getEncodedSourceStart() {
        return this.encodedSourceStart;
    }

    public int getEndLineno() {
        return this.endLineno;
    }

    public int getFunctionCount() {
        List<FunctionNode> list = this.functions;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public FunctionNode getFunctionNode(int n) {
        return (FunctionNode)this.functions.get(n);
    }

    public List<FunctionNode> getFunctions() {
        List<FunctionNode> list = this.functions;
        if (list == null) {
            list = this.EMPTY_LIST;
        }
        return list;
    }

    public int getIndexForNameNode(Node node) {
        Symbol symbol;
        Scope scope;
        if (this.variableNames == null) {
            ScriptNode.codeBug();
        }
        if ((symbol = (scope = node.getScope()) == null ? null : scope.getSymbol(((Name)node).getIdentifier())) == null) {
            return -1;
        }
        return symbol.getIndex();
    }

    public String getNextTempName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("$");
        int n = this.tempNumber;
        this.tempNumber = n + 1;
        stringBuilder.append(n);
        return stringBuilder.toString();
    }

    public boolean[] getParamAndVarConst() {
        if (this.variableNames == null) {
            ScriptNode.codeBug();
        }
        return this.isConsts;
    }

    public int getParamAndVarCount() {
        if (this.variableNames == null) {
            ScriptNode.codeBug();
        }
        return this.symbols.size();
    }

    public String[] getParamAndVarNames() {
        if (this.variableNames == null) {
            ScriptNode.codeBug();
        }
        return this.variableNames;
    }

    public int getParamCount() {
        return this.paramCount;
    }

    public String getParamOrVarName(int n) {
        if (this.variableNames == null) {
            ScriptNode.codeBug();
        }
        return this.variableNames[n];
    }

    public int getRegexpCount() {
        List<RegExpLiteral> list = this.regexps;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public String getRegexpFlags(int n) {
        return ((RegExpLiteral)this.regexps.get(n)).getFlags();
    }

    public String getRegexpString(int n) {
        return ((RegExpLiteral)this.regexps.get(n)).getValue();
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public List<Symbol> getSymbols() {
        return this.symbols;
    }

    public boolean isInStrictMode() {
        return this.inStrictMode;
    }

    public void setBaseLineno(int n) {
        if (n < 0 || this.lineno >= 0) {
            ScriptNode.codeBug();
        }
        this.lineno = n;
    }

    public void setCompilerData(Object object) {
        this.assertNotNull(object);
        if (this.compilerData == null) {
            this.compilerData = object;
            return;
        }
        throw new IllegalStateException();
    }

    public void setEncodedSource(String string) {
        this.encodedSource = string;
    }

    public void setEncodedSourceBounds(int n, int n2) {
        this.encodedSourceStart = n;
        this.encodedSourceEnd = n2;
    }

    public void setEncodedSourceEnd(int n) {
        this.encodedSourceEnd = n;
    }

    public void setEncodedSourceStart(int n) {
        this.encodedSourceStart = n;
    }

    public void setEndLineno(int n) {
        if (n < 0 || this.endLineno >= 0) {
            ScriptNode.codeBug();
        }
        this.endLineno = n;
    }

    public void setInStrictMode(boolean bl) {
        this.inStrictMode = bl;
    }

    public void setSourceName(String string) {
        this.sourceName = string;
    }

    public void setSymbols(List<Symbol> list) {
        this.symbols = list;
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            Iterator<Node> iterator = this.iterator();
            while (iterator.hasNext()) {
                ((AstNode)((Node)iterator.next())).visit(nodeVisitor);
            }
        }
    }
}

