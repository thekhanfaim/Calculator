/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.BitSet
 *  java.util.HashMap
 *  org.mozilla.javascript.ast.FunctionNode
 */
package org.mozilla.javascript.optimizer;

import java.util.BitSet;
import java.util.HashMap;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.optimizer.OptFunctionNode;

class Block {
    static final boolean DEBUG;
    private static int debug_blockCount;
    private int itsBlockID;
    private int itsEndNodeIndex;
    private BitSet itsLiveOnEntrySet;
    private BitSet itsLiveOnExitSet;
    private BitSet itsNotDefSet;
    private Block[] itsPredecessors;
    private int itsStartNodeIndex;
    private Block[] itsSuccessors;
    private BitSet itsUseBeforeDefSet;

    Block(int n, int n2) {
        this.itsStartNodeIndex = n;
        this.itsEndNodeIndex = n2;
    }

    private static boolean assignType(int[] arrn, int n, int n2) {
        int n3;
        int n4 = arrn[n];
        arrn[n] = n3 = n2 | arrn[n];
        return n4 != n3;
    }

    private static Block[] buildBlocks(Node[] arrnode) {
        HashMap hashMap = new HashMap();
        ObjArray objArray = new ObjArray();
        int n = 0;
        for (int i = 0; i < arrnode.length; ++i) {
            int n2 = arrnode[i].getType();
            if (n2 != 5 && n2 != 6 && n2 != 7) {
                if (n2 != 132 || i == n) continue;
                FatBlock fatBlock = Block.newFatBlock(n, i - 1);
                if (arrnode[n].getType() == 132) {
                    hashMap.put((Object)arrnode[n], (Object)fatBlock);
                }
                objArray.add(fatBlock);
                n = i;
                continue;
            }
            FatBlock fatBlock = Block.newFatBlock(n, i);
            if (arrnode[n].getType() == 132) {
                hashMap.put((Object)arrnode[n], (Object)fatBlock);
            }
            objArray.add(fatBlock);
            n = i + 1;
        }
        if (n != arrnode.length) {
            FatBlock fatBlock = Block.newFatBlock(n, -1 + arrnode.length);
            if (arrnode[n].getType() == 132) {
                hashMap.put((Object)arrnode[n], (Object)fatBlock);
            }
            objArray.add(fatBlock);
        }
        for (int i = 0; i < objArray.size(); ++i) {
            FatBlock fatBlock = (FatBlock)objArray.get(i);
            Node node = arrnode[fatBlock.realBlock.itsEndNodeIndex];
            int n3 = node.getType();
            if (n3 != 5 && i < -1 + objArray.size()) {
                FatBlock fatBlock2 = (FatBlock)objArray.get(i + 1);
                fatBlock.addSuccessor(fatBlock2);
                fatBlock2.addPredecessor(fatBlock);
            }
            if (n3 != 7 && n3 != 6 && n3 != 5) continue;
            Node node2 = ((Jump)node).target;
            FatBlock fatBlock3 = (FatBlock)hashMap.get((Object)node2);
            node2.putProp(6, fatBlock3.realBlock);
            fatBlock.addSuccessor(fatBlock3);
            fatBlock3.addPredecessor(fatBlock);
        }
        Block[] arrblock = new Block[objArray.size()];
        int n4 = 0;
        while (n4 < objArray.size()) {
            FatBlock fatBlock = (FatBlock)objArray.get(n4);
            Block block = fatBlock.realBlock;
            block.itsSuccessors = fatBlock.getSuccessors();
            block.itsPredecessors = fatBlock.getPredecessors();
            block.itsBlockID = n4++;
            arrblock[n4] = block;
        }
        return arrblock;
    }

    private boolean doReachedUseDataFlow() {
        this.itsLiveOnExitSet.clear();
        if (this.itsSuccessors != null) {
            Block[] arrblock;
            for (int i = 0; i < (arrblock = this.itsSuccessors).length; ++i) {
                this.itsLiveOnExitSet.or(arrblock[i].itsLiveOnEntrySet);
            }
        }
        return Block.updateEntrySet(this.itsLiveOnEntrySet, this.itsLiveOnExitSet, this.itsUseBeforeDefSet, this.itsNotDefSet);
    }

    private boolean doTypeFlow(OptFunctionNode optFunctionNode, Node[] arrnode, int[] arrn) {
        boolean bl = false;
        for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; ++i) {
            Node node = arrnode[i];
            if (node == null) continue;
            bl |= Block.findDefPoints(optFunctionNode, node, arrn);
        }
        return bl;
    }

    private static boolean findDefPoints(OptFunctionNode optFunctionNode, Node node, int[] arrn) {
        Node node2;
        boolean bl = false;
        for (Node node3 = node2 = node.getFirstChild(); node3 != null; node3 = node3.getNext()) {
            bl |= Block.findDefPoints(optFunctionNode, node3, arrn);
        }
        int n = node.getType();
        if (n != 56 && n != 157) {
            if (n != 107 && n != 108) {
                return bl;
            }
            if (node2.getType() == 55) {
                int n2 = optFunctionNode.getVarIndex(node2);
                if (!optFunctionNode.fnode.getParamAndVarConst()[n2]) {
                    bl |= Block.assignType(arrn, n2, 1);
                }
                return bl;
            }
        } else {
            int n3 = Block.findExpressionType(optFunctionNode, node2.getNext(), arrn);
            int n4 = optFunctionNode.getVarIndex(node);
            if (node.getType() != 56 || !optFunctionNode.fnode.getParamAndVarConst()[n4]) {
                bl |= Block.assignType(arrn, n4, n3);
            }
        }
        return bl;
    }

    /*
     * Exception decompiling
     */
    private static int findExpressionType(OptFunctionNode var0, Node var1_1, int[] var2_2) {
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

    private void initLiveOnEntrySets(OptFunctionNode optFunctionNode, Node[] arrnode) {
        int n = optFunctionNode.getVarCount();
        this.itsUseBeforeDefSet = new BitSet(n);
        this.itsNotDefSet = new BitSet(n);
        this.itsLiveOnEntrySet = new BitSet(n);
        this.itsLiveOnExitSet = new BitSet(n);
        for (int i = this.itsStartNodeIndex; i <= this.itsEndNodeIndex; ++i) {
            this.lookForVariableAccess(optFunctionNode, arrnode[i]);
        }
        this.itsNotDefSet.flip(0, n);
    }

    private void lookForVariableAccess(OptFunctionNode optFunctionNode, Node node) {
        block10 : {
            block6 : {
                block7 : {
                    block8 : {
                        block9 : {
                            int n = node.getType();
                            if (n == 55) break block6;
                            if (n == 56) break block7;
                            if (n == 107 || n == 108) break block8;
                            if (n == 138) break block9;
                            if (n == 157) break block7;
                            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNext()) {
                                this.lookForVariableAccess(optFunctionNode, node2);
                            }
                            break block10;
                        }
                        int n = optFunctionNode.fnode.getIndexForNameNode(node);
                        if (n > -1 && !this.itsNotDefSet.get(n)) {
                            this.itsUseBeforeDefSet.set(n);
                        }
                        return;
                    }
                    Node node3 = node.getFirstChild();
                    if (node3.getType() == 55) {
                        int n = optFunctionNode.getVarIndex(node3);
                        if (!this.itsNotDefSet.get(n)) {
                            this.itsUseBeforeDefSet.set(n);
                        }
                        this.itsNotDefSet.set(n);
                    } else {
                        this.lookForVariableAccess(optFunctionNode, node3);
                    }
                    return;
                }
                this.lookForVariableAccess(optFunctionNode, node.getFirstChild().getNext());
                this.itsNotDefSet.set(optFunctionNode.getVarIndex(node));
                return;
            }
            int n = optFunctionNode.getVarIndex(node);
            if (!this.itsNotDefSet.get(n)) {
                this.itsUseBeforeDefSet.set(n);
            }
        }
    }

    private void markAnyTypeVariables(int[] arrn) {
        for (int i = 0; i != arrn.length; ++i) {
            if (!this.itsLiveOnEntrySet.get(i)) continue;
            Block.assignType(arrn, i, 3);
        }
    }

    private static FatBlock newFatBlock(int n, int n2) {
        FatBlock fatBlock = new FatBlock();
        fatBlock.realBlock = new Block(n, n2);
        return fatBlock;
    }

    private void printLiveOnEntrySet(OptFunctionNode optFunctionNode) {
    }

    private static void reachingDefDataFlow(OptFunctionNode optFunctionNode, Node[] arrnode, Block[] arrblock, int[] arrn) {
        for (int i = 0; i < arrblock.length; ++i) {
            arrblock[i].initLiveOnEntrySets(optFunctionNode, arrnode);
        }
        boolean[] arrbl = new boolean[arrblock.length];
        boolean[] arrbl2 = new boolean[arrblock.length];
        int n = arrblock.length - 1;
        boolean bl = false;
        arrbl[n] = true;
        do {
            if (arrbl[n] || !arrbl2[n]) {
                Block[] arrblock2;
                arrbl2[n] = true;
                arrbl[n] = false;
                if (arrblock[n].doReachedUseDataFlow() && (arrblock2 = arrblock[n].itsPredecessors) != null) {
                    for (int i = 0; i < arrblock2.length; ++i) {
                        int n2 = arrblock2[i].itsBlockID;
                        arrbl[n2] = true;
                        boolean bl2 = n2 > n;
                        bl |= bl2;
                    }
                }
            }
            if (n == 0) {
                if (bl) {
                    n = -1 + arrblock.length;
                    bl = false;
                    continue;
                }
                arrblock[0].markAnyTypeVariables(arrn);
                return;
            }
            --n;
        } while (true);
    }

    static void runFlowAnalyzes(OptFunctionNode optFunctionNode, Node[] arrnode) {
        int n = optFunctionNode.fnode.getParamCount();
        int n2 = optFunctionNode.fnode.getParamAndVarCount();
        int[] arrn = new int[n2];
        for (int i = 0; i != n; ++i) {
            arrn[i] = 3;
        }
        for (int i = n; i != n2; ++i) {
            arrn[i] = 0;
        }
        Block[] arrblock = Block.buildBlocks(arrnode);
        Block.reachingDefDataFlow(optFunctionNode, arrnode, arrblock, arrn);
        Block.typeFlow(optFunctionNode, arrnode, arrblock, arrn);
        for (int i = n; i != n2; ++i) {
            if (arrn[i] != 1) continue;
            optFunctionNode.setIsNumberVar(i);
        }
    }

    private static String toString(Block[] arrblock, Node[] arrnode) {
        return null;
    }

    private static void typeFlow(OptFunctionNode optFunctionNode, Node[] arrnode, Block[] arrblock, int[] arrn) {
        boolean[] arrbl = new boolean[arrblock.length];
        boolean[] arrbl2 = new boolean[arrblock.length];
        int n = 0;
        boolean bl = false;
        arrbl[0] = true;
        do {
            if (arrbl[n] || !arrbl2[n]) {
                Block[] arrblock2;
                arrbl2[n] = true;
                arrbl[n] = false;
                if (arrblock[n].doTypeFlow(optFunctionNode, arrnode, arrn) && (arrblock2 = arrblock[n].itsSuccessors) != null) {
                    for (int i = 0; i < arrblock2.length; ++i) {
                        int n2 = arrblock2[i].itsBlockID;
                        arrbl[n2] = true;
                        boolean bl2 = n2 < n;
                        bl |= bl2;
                    }
                }
            }
            if (n == arrblock.length - 1) {
                if (bl) {
                    n = 0;
                    bl = false;
                    continue;
                }
                return;
            }
            ++n;
        } while (true);
    }

    private static boolean updateEntrySet(BitSet bitSet, BitSet bitSet2, BitSet bitSet3, BitSet bitSet4) {
        int n = bitSet.cardinality();
        bitSet.or(bitSet2);
        bitSet.and(bitSet4);
        bitSet.or(bitSet3);
        return bitSet.cardinality() != n;
    }

    private static class FatBlock {
        private ObjToIntMap predecessors = new ObjToIntMap();
        Block realBlock;
        private ObjToIntMap successors = new ObjToIntMap();

        private FatBlock() {
        }

        private static Block[] reduceToArray(ObjToIntMap objToIntMap) {
            boolean bl = objToIntMap.isEmpty();
            Block[] arrblock = null;
            if (!bl) {
                arrblock = new Block[objToIntMap.size()];
                int n = 0;
                ObjToIntMap.Iterator iterator = objToIntMap.newIterator();
                iterator.start();
                while (!iterator.done()) {
                    FatBlock fatBlock = (FatBlock)iterator.getKey();
                    int n2 = n + 1;
                    arrblock[n] = fatBlock.realBlock;
                    iterator.next();
                    n = n2;
                }
            }
            return arrblock;
        }

        void addPredecessor(FatBlock fatBlock) {
            this.predecessors.put(fatBlock, 0);
        }

        void addSuccessor(FatBlock fatBlock) {
            this.successors.put(fatBlock, 0);
        }

        Block[] getPredecessors() {
            return FatBlock.reduceToArray(this.predecessors);
        }

        Block[] getSuccessors() {
            return FatBlock.reduceToArray(this.successors);
        }
    }

}

