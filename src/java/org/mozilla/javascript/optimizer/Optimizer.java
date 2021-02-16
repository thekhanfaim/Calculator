/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.optimizer.Block;
import org.mozilla.javascript.optimizer.OptFunctionNode;

class Optimizer {
    static final int AnyType = 3;
    static final int NoType = 0;
    static final int NumberType = 1;
    private boolean inDirectCallFunction;
    private boolean parameterUsedInNumberContext;
    OptFunctionNode theFunction;

    Optimizer() {
    }

    private static void buildStatementList_r(Node node, ObjArray objArray) {
        int n = node.getType();
        if (n != 130 && n != 142 && n != 133 && n != 110) {
            objArray.add(node);
            return;
        }
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNext()) {
            Optimizer.buildStatementList_r(node2, objArray);
        }
    }

    private boolean convertParameter(Node node) {
        int n;
        if (this.inDirectCallFunction && node.getType() == 55 && this.theFunction.isParameter(n = this.theFunction.getVarIndex(node))) {
            node.removeProp(8);
            return true;
        }
        return false;
    }

    private void markDCPNumberContext(Node node) {
        int n;
        if (this.inDirectCallFunction && node.getType() == 55 && this.theFunction.isParameter(n = this.theFunction.getVarIndex(node))) {
            this.parameterUsedInNumberContext = true;
        }
    }

    private void optimizeFunction(OptFunctionNode optFunctionNode) {
        if (optFunctionNode.fnode.requiresActivation()) {
            return;
        }
        this.inDirectCallFunction = optFunctionNode.isTargetOfDirectCall();
        this.theFunction = optFunctionNode;
        ObjArray objArray = new ObjArray();
        Optimizer.buildStatementList_r((Node)optFunctionNode.fnode, objArray);
        Object[] arrobject = new Node[objArray.size()];
        objArray.toArray(arrobject);
        Block.runFlowAnalyzes(optFunctionNode, (Node[])arrobject);
        if (!optFunctionNode.fnode.requiresActivation()) {
            this.parameterUsedInNumberContext = false;
            int n = arrobject.length;
            for (int i = 0; i < n; ++i) {
                this.rewriteForNumberVariables((Node)arrobject[i], 1);
            }
            optFunctionNode.setParameterNumberContext(this.parameterUsedInNumberContext);
        }
    }

    private void rewriteAsObjectChildren(Node node, Node node2) {
        while (node2 != null) {
            Node node3 = node2.getNext();
            if (this.rewriteForNumberVariables(node2, 0) == 1 && !this.convertParameter(node2)) {
                node.removeChild(node2);
                Node node4 = new Node(150, node2);
                if (node3 == null) {
                    node.addChildToBack(node4);
                } else {
                    node.addChildBefore(node4, node3);
                }
            }
            node2 = node3;
        }
    }

    /*
     * Exception decompiling
     */
    private int rewriteForNumberVariables(Node var1_1, int var2_2) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Extractable last case doesn't follow previous
        // org.benf.cfr.reader.b.a.a.b.as.a(SwitchReplacer.java:478)
        // org.benf.cfr.reader.b.a.a.b.as.a(SwitchReplacer.java:61)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:372)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:182)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:127)
        // org.benf.cfr.reader.entities.attributes.f.c(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.g.p(Method.java:396)
        // org.benf.cfr.reader.entities.d.e(ClassFile.java:890)
        // org.benf.cfr.reader.entities.d.b(ClassFile.java:792)
        // org.benf.cfr.reader.b.a(Driver.java:128)
        // org.benf.cfr.reader.a.a(CfrDriverImpl.java:63)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.decompileWithCFR(JavaExtractionWorker.kt:61)
        // com.njlabs.showjava.decompilers.JavaExtractionWorker.doWork(JavaExtractionWorker.kt:130)
        // com.njlabs.showjava.decompilers.BaseDecompiler.withAttempt(BaseDecompiler.kt:108)
        // com.njlabs.showjava.workers.DecompilerWorker$b.run(DecompilerWorker.kt:118)
        // java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        // java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        // java.lang.Thread.run(Thread.java:919)
        throw new IllegalStateException("Decompilation failed");
    }

    void optimize(ScriptNode scriptNode) {
        int n = scriptNode.getFunctionCount();
        for (int i = 0; i != n; ++i) {
            this.optimizeFunction(OptFunctionNode.get(scriptNode, i));
        }
    }
}

