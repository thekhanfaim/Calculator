/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.NodeTransformer;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.optimizer.OptFunctionNode;

class OptTransformer
extends NodeTransformer {
    private ObjArray directCallTargets;
    private Map<String, OptFunctionNode> possibleDirectCalls;

    OptTransformer(Map<String, OptFunctionNode> map, ObjArray objArray) {
        this.possibleDirectCalls = map;
        this.directCallTargets = objArray;
    }

    private void detectDirectCall(Node node, ScriptNode scriptNode) {
        block11 : {
            if (scriptNode.getType() != 110) break block11;
            Node node2 = node.getFirstChild();
            int n = 0;
            Node node3 = node2.getNext();
            while (node3 != null) {
                node3 = node3.getNext();
                ++n;
            }
            if (n == 0) {
                OptFunctionNode.get((ScriptNode)scriptNode).itsContainsCalls0 = true;
            }
            if (this.possibleDirectCalls != null) {
                String string2;
                OptFunctionNode optFunctionNode;
                if (node2.getType() == 39) {
                    string2 = node2.getString();
                } else if (node2.getType() == 33) {
                    string2 = node2.getFirstChild().getNext().getString();
                } else {
                    int n2 = node2.getType();
                    string2 = null;
                    if (n2 == 34) {
                        throw Kit.codeBug();
                    }
                }
                if (string2 != null && (optFunctionNode = (OptFunctionNode)this.possibleDirectCalls.get((Object)string2)) != null && n == optFunctionNode.fnode.getParamCount() && !optFunctionNode.fnode.requiresActivation() && n <= 32) {
                    node.putProp(9, optFunctionNode);
                    if (!optFunctionNode.isTargetOfDirectCall()) {
                        int n3 = this.directCallTargets.size();
                        this.directCallTargets.add(optFunctionNode);
                        optFunctionNode.setDirectTargetIndex(n3);
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void visitCall(Node node, ScriptNode scriptNode) {
        this.detectDirectCall(node, scriptNode);
        super.visitCall(node, scriptNode);
    }

    @Override
    protected void visitNew(Node node, ScriptNode scriptNode) {
        this.detectDirectCall(node, scriptNode);
        super.visitNew(node, scriptNode);
    }
}

