/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;

public final class OptFunctionNode {
    private int directTargetIndex = -1;
    public final FunctionNode fnode;
    boolean itsContainsCalls0;
    boolean itsContainsCalls1;
    private boolean itsParameterNumberContext;
    private boolean[] numberVarFlags;

    OptFunctionNode(FunctionNode functionNode) {
        this.fnode = functionNode;
        functionNode.setCompilerData((Object)this);
    }

    public static OptFunctionNode get(ScriptNode scriptNode) {
        return (OptFunctionNode)scriptNode.getCompilerData();
    }

    public static OptFunctionNode get(ScriptNode scriptNode, int n) {
        return (OptFunctionNode)scriptNode.getFunctionNode(n).getCompilerData();
    }

    public int getDirectTargetIndex() {
        return this.directTargetIndex;
    }

    public boolean getParameterNumberContext() {
        return this.itsParameterNumberContext;
    }

    public int getVarCount() {
        return this.fnode.getParamAndVarCount();
    }

    public int getVarIndex(Node node) {
        int n = node.getIntProp(7, -1);
        if (n == -1) {
            Node node2;
            int n2 = node.getType();
            if (n2 == 55) {
                node2 = node;
            } else {
                if (n2 != 56 && n2 != 157) {
                    throw Kit.codeBug();
                }
                node2 = node.getFirstChild();
            }
            int n3 = this.fnode.getIndexForNameNode(node2);
            if (n3 >= 0) {
                node.putIntProp(7, n3);
                return n3;
            }
            throw Kit.codeBug();
        }
        return n;
    }

    public boolean isNumberVar(int n) {
        boolean[] arrbl;
        int n2 = n - this.fnode.getParamCount();
        if (n2 >= 0 && (arrbl = this.numberVarFlags) != null) {
            return arrbl[n2];
        }
        return false;
    }

    public boolean isParameter(int n) {
        return n < this.fnode.getParamCount();
    }

    public boolean isTargetOfDirectCall() {
        return this.directTargetIndex >= 0;
    }

    void setDirectTargetIndex(int n) {
        if (n < 0 || this.directTargetIndex >= 0) {
            Kit.codeBug();
        }
        this.directTargetIndex = n;
    }

    void setIsNumberVar(int n) {
        int n2 = n - this.fnode.getParamCount();
        if (n2 < 0) {
            Kit.codeBug();
        }
        if (this.numberVarFlags == null) {
            this.numberVarFlags = new boolean[this.fnode.getParamAndVarCount() - this.fnode.getParamCount()];
        }
        this.numberVarFlags[n2] = true;
    }

    void setParameterNumberContext(boolean bl) {
        this.itsParameterNumberContext = bl;
    }
}

