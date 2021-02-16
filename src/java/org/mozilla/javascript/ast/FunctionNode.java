/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 */
package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.ScriptNode;

public class FunctionNode
extends ScriptNode {
    public static final int ARROW_FUNCTION = 4;
    public static final int FUNCTION_EXPRESSION = 2;
    public static final int FUNCTION_EXPRESSION_STATEMENT = 3;
    public static final int FUNCTION_STATEMENT = 1;
    private static final List<AstNode> NO_PARAMS = Collections.unmodifiableList((List)new ArrayList());
    private AstNode body;
    private Form functionForm = Form.FUNCTION;
    private Name functionName;
    private int functionType;
    private List<Node> generatorResumePoints;
    private boolean isExpressionClosure;
    private boolean isGenerator;
    private Map<Node, int[]> liveLocals;
    private int lp = -1;
    private AstNode memberExprNode;
    private boolean needsActivation;
    private List<AstNode> params;
    private int rp = -1;

    public FunctionNode() {
        this.type = 110;
    }

    public FunctionNode(int n) {
        super(n);
        this.type = 110;
    }

    public FunctionNode(int n, Name name) {
        super(n);
        this.type = 110;
        this.setFunctionName(name);
    }

    @Override
    public int addFunction(FunctionNode functionNode) {
        int n = super.addFunction(functionNode);
        if (this.getFunctionCount() > 0) {
            this.needsActivation = true;
        }
        return n;
    }

    public void addLiveLocals(Node node, int[] arrn) {
        if (this.liveLocals == null) {
            this.liveLocals = new HashMap();
        }
        this.liveLocals.put((Object)node, (Object)arrn);
    }

    public void addParam(AstNode astNode) {
        this.assertNotNull(astNode);
        if (this.params == null) {
            this.params = new ArrayList();
        }
        this.params.add((Object)astNode);
        astNode.setParent(this);
    }

    public void addResumptionPoint(Node node) {
        if (this.generatorResumePoints == null) {
            this.generatorResumePoints = new ArrayList();
        }
        this.generatorResumePoints.add((Object)node);
    }

    public AstNode getBody() {
        return this.body;
    }

    public Name getFunctionName() {
        return this.functionName;
    }

    public int getFunctionType() {
        return this.functionType;
    }

    public Map<Node, int[]> getLiveLocals() {
        return this.liveLocals;
    }

    public int getLp() {
        return this.lp;
    }

    public AstNode getMemberExprNode() {
        return this.memberExprNode;
    }

    public String getName() {
        Name name = this.functionName;
        if (name != null) {
            return name.getIdentifier();
        }
        return "";
    }

    public List<AstNode> getParams() {
        List<AstNode> list = this.params;
        if (list != null) {
            return list;
        }
        return NO_PARAMS;
    }

    public List<Node> getResumptionPoints() {
        return this.generatorResumePoints;
    }

    public int getRp() {
        return this.rp;
    }

    public boolean isExpressionClosure() {
        return this.isExpressionClosure;
    }

    public boolean isGenerator() {
        return this.isGenerator;
    }

    public boolean isGetterMethod() {
        return this.functionForm == Form.GETTER;
    }

    public boolean isMethod() {
        return this.functionForm == Form.GETTER || this.functionForm == Form.SETTER || this.functionForm == Form.METHOD;
        {
        }
    }

    public boolean isNormalMethod() {
        return this.functionForm == Form.METHOD;
    }

    public boolean isParam(AstNode astNode) {
        List<AstNode> list = this.params;
        if (list == null) {
            return false;
        }
        return list.contains((Object)astNode);
    }

    public boolean isSetterMethod() {
        return this.functionForm == Form.SETTER;
    }

    public boolean requiresActivation() {
        return this.needsActivation;
    }

    public void setBody(AstNode astNode) {
        this.assertNotNull(astNode);
        this.body = astNode;
        if (Boolean.TRUE.equals(astNode.getProp(25))) {
            this.setIsExpressionClosure(true);
        }
        int n = astNode.getPosition() + astNode.getLength();
        astNode.setParent(this);
        this.setLength(n - this.position);
        this.setEncodedSourceBounds(this.position, n);
    }

    public void setFunctionIsGetterMethod() {
        this.functionForm = Form.GETTER;
    }

    public void setFunctionIsNormalMethod() {
        this.functionForm = Form.METHOD;
    }

    public void setFunctionIsSetterMethod() {
        this.functionForm = Form.SETTER;
    }

    public void setFunctionName(Name name) {
        this.functionName = name;
        if (name != null) {
            name.setParent(this);
        }
    }

    public void setFunctionType(int n) {
        this.functionType = n;
    }

    public void setIsExpressionClosure(boolean bl) {
        this.isExpressionClosure = bl;
    }

    public void setIsGenerator() {
        this.isGenerator = true;
    }

    public void setLp(int n) {
        this.lp = n;
    }

    public void setMemberExprNode(AstNode astNode) {
        this.memberExprNode = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setParams(List<AstNode> list) {
        if (list == null) {
            this.params = null;
            return;
        }
        List<AstNode> list2 = this.params;
        if (list2 != null) {
            list2.clear();
        }
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.addParam((AstNode)iterator.next());
        }
    }

    public void setParens(int n, int n2) {
        this.lp = n;
        this.rp = n2;
    }

    public void setRequiresActivation() {
        this.needsActivation = true;
    }

    public void setRp(int n) {
        this.rp = n;
    }

    @Override
    public String toSource(int n) {
        List<AstNode> list;
        StringBuilder stringBuilder = new StringBuilder();
        boolean bl = this.functionType == 4;
        if (!this.isMethod()) {
            stringBuilder.append(this.makeIndent(n));
            if (!bl) {
                stringBuilder.append("function");
            }
        }
        if (this.functionName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.functionName.toSource(0));
        }
        if ((list = this.params) == null) {
            stringBuilder.append("() ");
        } else if (bl && this.lp == -1) {
            this.printList(list, stringBuilder);
            stringBuilder.append(" ");
        } else {
            stringBuilder.append("(");
            this.printList(this.params, stringBuilder);
            stringBuilder.append(") ");
        }
        if (bl) {
            stringBuilder.append("=> ");
        }
        if (this.isExpressionClosure) {
            AstNode astNode = this.getBody();
            if (astNode.getLastChild() instanceof ReturnStatement) {
                stringBuilder.append(((ReturnStatement)astNode.getLastChild()).getReturnValue().toSource(0));
                if (this.functionType == 1) {
                    stringBuilder.append(";");
                }
            } else {
                stringBuilder.append(" ");
                stringBuilder.append(astNode.toSource(0));
            }
        } else {
            stringBuilder.append(this.getBody().toSource(n).trim());
        }
        if (this.functionType == 1 || this.isMethod()) {
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            AstNode astNode;
            Name name = this.functionName;
            if (name != null) {
                name.visit(nodeVisitor);
            }
            Iterator iterator = this.getParams().iterator();
            while (iterator.hasNext()) {
                ((AstNode)iterator.next()).visit(nodeVisitor);
            }
            this.getBody().visit(nodeVisitor);
            if (!this.isExpressionClosure && (astNode = this.memberExprNode) != null) {
                astNode.visit(nodeVisitor);
            }
        }
    }

}

