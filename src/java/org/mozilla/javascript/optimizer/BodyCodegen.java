/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.AssertionError
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.LinkedList
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Map
 *  java.util.Set
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.mozilla.classfile.ClassFileWriter;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.optimizer.Codegen;
import org.mozilla.javascript.optimizer.OptFunctionNode;
import org.mozilla.javascript.optimizer.OptRuntime;

class BodyCodegen {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int ECMAERROR_EXCEPTION = 2;
    private static final int EVALUATOR_EXCEPTION = 1;
    private static final int EXCEPTION_MAX = 5;
    private static final int FINALLY_EXCEPTION = 4;
    static final int GENERATOR_START = 0;
    static final int GENERATOR_TERMINATE = -1;
    static final int GENERATOR_YIELD_START = 1;
    private static final int JAVASCRIPT_EXCEPTION = 0;
    private static final int MAX_LOCALS = 1024;
    private static final int THROWABLE_EXCEPTION = 3;
    private short argsLocal;
    ClassFileWriter cfw;
    Codegen codegen;
    CompilerEnvirons compilerEnv;
    private short contextLocal;
    private int enterAreaStartLabel;
    private int epilogueLabel;
    private ExceptionManager exceptionManager = new ExceptionManager();
    private Map<Node, FinallyReturnPoint> finallys;
    private short firstFreeLocal;
    private OptFunctionNode fnCurrent;
    private short funObjLocal;
    private short generatorStateLocal;
    private int generatorSwitch;
    private boolean hasVarsInRegs;
    private boolean inDirectCallFunction;
    private boolean inLocalBlock;
    private boolean isGenerator;
    private boolean itsForcedObjectParameters;
    private int itsLineNumber;
    private short itsOneArgArray;
    private short itsZeroArgArray;
    private List<Node> literals;
    private int[] locals;
    private short localsMax;
    private int maxLocals = 0;
    private int maxStack = 0;
    private short operationLocal;
    private short popvLocal;
    private int savedCodeOffset;
    ScriptNode scriptOrFn;
    public int scriptOrFnIndex;
    private short thisObjLocal;
    private short[] varRegisters;
    private short variableObjectLocal;

    BodyCodegen() {
    }

    private void addDoubleWrap() {
        this.addOptRuntimeInvoke("wrapDouble", "(D)Ljava/lang/Double;");
    }

    private void addGoto(Node node, int n) {
        int n2 = this.getTargetLabel(node);
        this.cfw.add(n, n2);
    }

    private void addGotoWithReturn(Node node) {
        FinallyReturnPoint finallyReturnPoint = (FinallyReturnPoint)this.finallys.get((Object)node);
        this.cfw.addLoadConstant(finallyReturnPoint.jsrPoints.size());
        this.addGoto(node, 167);
        int n = this.cfw.acquireLabel();
        this.cfw.markLabel(n);
        finallyReturnPoint.jsrPoints.add((Object)n);
    }

    private void addInstructionCount() {
        this.addInstructionCount(Math.max((int)(this.cfw.getCurrentCodeOffset() - this.savedCodeOffset), (int)1));
    }

    private void addInstructionCount(int n) {
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addPush(n);
        this.addScriptRuntimeInvoke("addInstructionCount", "(Lorg/mozilla/javascript/Context;I)V");
    }

    private void addJumpedBooleanWrap(int n, int n2) {
        this.cfw.markLabel(n2);
        int n3 = this.cfw.acquireLabel();
        this.cfw.add(178, "java/lang/Boolean", "FALSE", "Ljava/lang/Boolean;");
        this.cfw.add(167, n3);
        this.cfw.markLabel(n);
        this.cfw.add(178, "java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;");
        this.cfw.markLabel(n3);
        this.cfw.adjustStackTop(-1);
    }

    private void addLoadPropertyIds(Object[] arrobject, int n) {
        this.addNewObjectArray(n);
        for (int i = 0; i != n; ++i) {
            this.cfw.add(89);
            this.cfw.addPush(i);
            Object object = arrobject[i];
            if (object instanceof String) {
                this.cfw.addPush((String)object);
            } else {
                this.cfw.addPush((Integer)object);
                this.addScriptRuntimeInvoke("wrapInt", "(I)Ljava/lang/Integer;");
            }
            this.cfw.add(83);
        }
    }

    private void addLoadPropertyValues(Node node, Node node2, int n) {
        if (this.isGenerator) {
            for (int i = 0; i != n; ++i) {
                int n2 = node2.getType();
                if (n2 != 152 && n2 != 153 && n2 != 164) {
                    this.generateExpression(node2, node);
                } else {
                    this.generateExpression(node2.getFirstChild(), node);
                }
                node2 = node2.getNext();
            }
            this.addNewObjectArray(n);
            for (int i = 0; i != n; ++i) {
                this.cfw.add(90);
                this.cfw.add(95);
                this.cfw.addPush(-1 + (n - i));
                this.cfw.add(95);
                this.cfw.add(83);
            }
            return;
        }
        this.addNewObjectArray(n);
        Node node3 = node2;
        for (int i = 0; i != n; ++i) {
            this.cfw.add(89);
            this.cfw.addPush(i);
            int n3 = node3.getType();
            if (n3 != 152 && n3 != 153 && n3 != 164) {
                this.generateExpression(node3, node);
            } else {
                this.generateExpression(node3.getFirstChild(), node);
            }
            this.cfw.add(83);
            node3 = node3.getNext();
        }
    }

    private void addNewObjectArray(int n) {
        if (n == 0) {
            short s = this.itsZeroArgArray;
            if (s >= 0) {
                this.cfw.addALoad(s);
                return;
            }
            this.cfw.add(178, "org/mozilla/javascript/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
            return;
        }
        this.cfw.addPush(n);
        this.cfw.add(189, "java/lang/Object");
    }

    private void addObjectToDouble() {
        this.addScriptRuntimeInvoke("toNumber", "(Ljava/lang/Object;)D");
    }

    private void addOptRuntimeInvoke(String string2, String string3) {
        this.cfw.addInvoke(184, "org/mozilla/javascript/optimizer/OptRuntime", string2, string3);
    }

    private void addScriptRuntimeInvoke(String string2, String string3) {
        this.cfw.addInvoke(184, "org.mozilla.javascript.ScriptRuntime", string2, string3);
    }

    private void dcpLoadAsNumber(int n) {
        this.cfw.addALoad(n);
        this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
        int n2 = this.cfw.acquireLabel();
        this.cfw.add(165, n2);
        short s = this.cfw.getStackTop();
        this.cfw.addALoad(n);
        this.addObjectToDouble();
        int n3 = this.cfw.acquireLabel();
        this.cfw.add(167, n3);
        this.cfw.markLabel(n2, s);
        this.cfw.addDLoad(n + 1);
        this.cfw.markLabel(n3);
    }

    private void dcpLoadAsObject(int n) {
        this.cfw.addALoad(n);
        this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
        int n2 = this.cfw.acquireLabel();
        this.cfw.add(165, n2);
        short s = this.cfw.getStackTop();
        this.cfw.addALoad(n);
        int n3 = this.cfw.acquireLabel();
        this.cfw.add(167, n3);
        this.cfw.markLabel(n2, s);
        this.cfw.addDLoad(n + 1);
        this.addDoubleWrap();
        this.cfw.markLabel(n3);
    }

    private void decReferenceWordLocal(short s) {
        int[] arrn = this.locals;
        arrn[s] = -1 + arrn[s];
    }

    private String exceptionTypeToName(int n) {
        if (n == 0) {
            return "org/mozilla/javascript/JavaScriptException";
        }
        if (n == 1) {
            return "org/mozilla/javascript/EvaluatorException";
        }
        if (n == 2) {
            return "org/mozilla/javascript/EcmaError";
        }
        if (n == 3) {
            return "java/lang/Throwable";
        }
        if (n == 4) {
            return null;
        }
        throw Kit.codeBug();
    }

    private void genSimpleCompare(int n, int n2, int n3) {
        if (n2 != -1) {
            switch (n) {
                default: {
                    throw Codegen.badTree();
                }
                case 17: {
                    this.cfw.add(151);
                    this.cfw.add(156, n2);
                    break;
                }
                case 16: {
                    this.cfw.add(151);
                    this.cfw.add(157, n2);
                    break;
                }
                case 15: {
                    this.cfw.add(152);
                    this.cfw.add(158, n2);
                    break;
                }
                case 14: {
                    this.cfw.add(152);
                    this.cfw.add(155, n2);
                }
            }
            if (n3 != -1) {
                this.cfw.add(167, n3);
            }
            return;
        }
        throw Codegen.badTree();
    }

    private void generateActivationExit() {
        if (this.fnCurrent != null && !this.hasVarsInRegs) {
            this.cfw.addALoad(this.contextLocal);
            this.addScriptRuntimeInvoke("exitActivationFunction", "(Lorg/mozilla/javascript/Context;)V");
            return;
        }
        throw Kit.codeBug();
    }

    private void generateArrayLiteralFactory(Node node, int n) {
        short s;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.codegen.getBodyMethodName(this.scriptOrFn));
        stringBuilder.append("_literal");
        stringBuilder.append(n);
        String string2 = stringBuilder.toString();
        this.initBodyGeneration();
        short s2 = this.firstFreeLocal;
        this.firstFreeLocal = s = (short)(s2 + 1);
        this.argsLocal = s2;
        this.localsMax = s;
        this.cfw.startMethod(string2, "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;", (short)2);
        this.visitArrayLiteral(node, node.getFirstChild(), true);
        this.cfw.add(176);
        this.cfw.stopMethod((short)(1 + this.localsMax));
    }

    private void generateCallArgArray(Node node, Node node2, boolean bl) {
        short s;
        int n = 0;
        for (Node node3 = node2; node3 != null; node3 = node3.getNext()) {
            ++n;
        }
        if (n == 1 && (s = this.itsOneArgArray) >= 0) {
            this.cfw.addALoad(s);
        } else {
            this.addNewObjectArray(n);
        }
        for (int i = 0; i != n; ++i) {
            if (!this.isGenerator) {
                this.cfw.add(89);
                this.cfw.addPush(i);
            }
            if (!bl) {
                this.generateExpression(node2, node);
            } else {
                int n2 = this.nodeIsDirectCallParameter(node2);
                if (n2 >= 0) {
                    this.dcpLoadAsObject(n2);
                } else {
                    this.generateExpression(node2, node);
                    if (node2.getIntProp(8, -1) == 0) {
                        this.addDoubleWrap();
                    }
                }
            }
            if (this.isGenerator) {
                short s2 = this.getNewWordLocal();
                this.cfw.addAStore(s2);
                this.cfw.add(192, "[Ljava/lang/Object;");
                this.cfw.add(89);
                this.cfw.addPush(i);
                this.cfw.addALoad(s2);
                this.releaseWordLocal(s2);
            }
            this.cfw.add(83);
            node2 = node2.getNext();
        }
    }

    private void generateCatchBlock(int n, short s, int n2, int n3, int n4) {
        if (n4 == 0) {
            n4 = this.cfw.acquireLabel();
        }
        this.cfw.markHandler(n4);
        this.cfw.addAStore(n3);
        this.cfw.addALoad(s);
        this.cfw.addAStore(this.variableObjectLocal);
        this.cfw.add(167, n2);
    }

    private void generateCheckForThrowOrClose(int n, boolean bl, int n2) {
        int n3 = this.cfw.acquireLabel();
        int n4 = this.cfw.acquireLabel();
        this.cfw.markLabel(n3);
        this.cfw.addALoad(this.argsLocal);
        this.generateThrowJavaScriptException();
        this.cfw.markLabel(n4);
        this.cfw.addALoad(this.argsLocal);
        this.cfw.add(192, "java/lang/Throwable");
        this.cfw.add(191);
        if (n != -1) {
            this.cfw.markLabel(n);
        }
        if (!bl) {
            this.cfw.markTableSwitchCase(this.generatorSwitch, n2);
        }
        this.cfw.addILoad(this.operationLocal);
        this.cfw.addLoadConstant(2);
        this.cfw.add(159, n4);
        this.cfw.addILoad(this.operationLocal);
        this.cfw.addLoadConstant(1);
        this.cfw.add(159, n3);
    }

    private void generateEpilogue() {
        int n;
        if (this.compilerEnv.isGenerateObserverCount()) {
            this.addInstructionCount();
        }
        if (this.isGenerator) {
            Map<Node, FinallyReturnPoint> map;
            Map map2 = ((FunctionNode)this.scriptOrFn).getLiveLocals();
            if (map2 != null) {
                List list = ((FunctionNode)this.scriptOrFn).getResumptionPoints();
                for (int i = 0; i < list.size(); ++i) {
                    Node node = (Node)list.get(i);
                    int[] arrn = (int[])map2.get((Object)node);
                    if (arrn == null) continue;
                    this.cfw.markTableSwitchCase(this.generatorSwitch, this.getNextGeneratorState(node));
                    this.generateGetGeneratorLocalsState();
                    for (int j = 0; j < arrn.length; ++j) {
                        this.cfw.add(89);
                        this.cfw.addLoadConstant(j);
                        this.cfw.add(50);
                        this.cfw.addAStore(arrn[j]);
                    }
                    this.cfw.add(87);
                    this.cfw.add(167, this.getTargetLabel(node));
                }
            }
            if ((map = this.finallys) != null) {
                for (Node node : map.keySet()) {
                    if (node.getType() != 126) continue;
                    FinallyReturnPoint finallyReturnPoint = (FinallyReturnPoint)this.finallys.get((Object)node);
                    this.cfw.markLabel(finallyReturnPoint.tableLabel, (short)1);
                    int n2 = this.cfw.addTableSwitch(0, finallyReturnPoint.jsrPoints.size() - 1);
                    int n3 = 0;
                    this.cfw.markTableSwitchDefault(n2);
                    for (int i = 0; i < finallyReturnPoint.jsrPoints.size(); ++i) {
                        this.cfw.markTableSwitchCase(n2, n3);
                        this.cfw.add(167, (Integer)finallyReturnPoint.jsrPoints.get(i));
                        ++n3;
                    }
                }
            }
        }
        if ((n = this.epilogueLabel) != -1) {
            this.cfw.markLabel(n);
        }
        if (this.hasVarsInRegs) {
            this.cfw.add(176);
            return;
        }
        if (this.isGenerator) {
            if (((FunctionNode)this.scriptOrFn).getResumptionPoints() != null) {
                this.cfw.markTableSwitchDefault(this.generatorSwitch);
            }
            this.generateSetGeneratorResumptionPoint(-1);
            this.cfw.addALoad(this.variableObjectLocal);
            this.addOptRuntimeInvoke("throwStopIteration", "(Ljava/lang/Object;)V");
            Codegen.pushUndefined(this.cfw);
            this.cfw.add(176);
            return;
        }
        if (this.fnCurrent == null) {
            this.cfw.addALoad(this.popvLocal);
            this.cfw.add(176);
            return;
        }
        this.generateActivationExit();
        this.cfw.add(176);
        int n4 = this.cfw.acquireLabel();
        this.cfw.markHandler(n4);
        short s = this.getNewWordLocal();
        this.cfw.addAStore(s);
        this.generateActivationExit();
        this.cfw.addALoad(s);
        this.releaseWordLocal(s);
        this.cfw.add(191);
        this.cfw.addExceptionHandler(this.enterAreaStartLabel, this.epilogueLabel, n4, null);
    }

    /*
     * Exception decompiling
     */
    private void generateExpression(Node var1_1, Node var2_2) {
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

    private void generateFunctionAndThisObj(Node node, Node node2) {
        block7 : {
            int n;
            block5 : {
                block6 : {
                    n = node.getType();
                    int n2 = node.getType();
                    if (n2 == 33) break block5;
                    if (n2 == 34) break block6;
                    if (n2 == 36) break block5;
                    if (n2 != 39) {
                        this.generateExpression(node, node2);
                        this.cfw.addALoad(this.contextLocal);
                        this.addScriptRuntimeInvoke("getValueFunctionAndThis", "(Ljava/lang/Object;Lorg/mozilla/javascript/Context;)Lorg/mozilla/javascript/Callable;");
                    } else {
                        String string2 = node.getString();
                        this.cfw.addPush(string2);
                        this.cfw.addALoad(this.contextLocal);
                        this.cfw.addALoad(this.variableObjectLocal);
                        this.addScriptRuntimeInvoke("getNameFunctionAndThis", "(Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Callable;");
                    }
                    break block7;
                }
                throw Kit.codeBug();
            }
            Node node3 = node.getFirstChild();
            this.generateExpression(node3, node);
            Node node4 = node3.getNext();
            if (n == 33) {
                String string3 = node4.getString();
                this.cfw.addPush(string3);
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.addScriptRuntimeInvoke("getPropFunctionAndThis", "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Callable;");
            } else {
                this.generateExpression(node4, node);
                if (node.getIntProp(8, -1) != -1) {
                    this.addDoubleWrap();
                }
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.addScriptRuntimeInvoke("getElemFunctionAndThis", "(Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Callable;");
            }
        }
        this.cfw.addALoad(this.contextLocal);
        this.addScriptRuntimeInvoke("lastStoredScriptable", "(Lorg/mozilla/javascript/Context;)Lorg/mozilla/javascript/Scriptable;");
    }

    private void generateGenerator() {
        short s;
        this.cfw.startMethod(this.codegen.getBodyMethodName(this.scriptOrFn), this.codegen.getBodyMethodSignature(this.scriptOrFn), (short)10);
        this.initBodyGeneration();
        short s2 = this.firstFreeLocal;
        this.firstFreeLocal = s = (short)(s2 + 1);
        this.argsLocal = s2;
        this.localsMax = s;
        if (this.fnCurrent != null) {
            this.cfw.addALoad(this.funObjLocal);
            this.cfw.addInvoke(185, "org/mozilla/javascript/Scriptable", "getParentScope", "()Lorg/mozilla/javascript/Scriptable;");
            this.cfw.addAStore(this.variableObjectLocal);
        }
        this.cfw.addALoad(this.funObjLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addALoad(this.argsLocal);
        this.cfw.addPush(this.scriptOrFn.isInStrictMode());
        this.addScriptRuntimeInvoke("createFunctionActivation", "(Lorg/mozilla/javascript/NativeFunction;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;Z)Lorg/mozilla/javascript/Scriptable;");
        this.cfw.addAStore(this.variableObjectLocal);
        this.cfw.add(187, this.codegen.mainClassName);
        this.cfw.add(89);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addPush(this.scriptOrFnIndex);
        this.cfw.addInvoke(183, this.codegen.mainClassName, "<init>", "(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;I)V");
        this.generateNestedFunctionInits();
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addALoad(this.thisObjLocal);
        this.cfw.addLoadConstant(this.maxLocals);
        this.cfw.addLoadConstant(this.maxStack);
        this.addOptRuntimeInvoke("createNativeGenerator", "(Lorg/mozilla/javascript/NativeFunction;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;II)Lorg/mozilla/javascript/Scriptable;");
        this.cfw.add(176);
        this.cfw.stopMethod((short)(1 + this.localsMax));
    }

    private void generateGetGeneratorLocalsState() {
        this.cfw.addALoad(this.generatorStateLocal);
        this.addOptRuntimeInvoke("getGeneratorLocalsState", "(Ljava/lang/Object;)[Ljava/lang/Object;");
    }

    private void generateGetGeneratorResumptionPoint() {
        this.cfw.addALoad(this.generatorStateLocal);
        this.cfw.add(180, "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState", "resumptionPoint", "I");
    }

    private void generateGetGeneratorStackState() {
        this.cfw.addALoad(this.generatorStateLocal);
        this.addOptRuntimeInvoke("getGeneratorStackState", "(Ljava/lang/Object;)[Ljava/lang/Object;");
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private void generateIfJump(Node var1_1, Node var2_2, int var3_3, int var4_4) {
        block7 : {
            var5_5 = var1_1.getType();
            var6_6 = var1_1.getFirstChild();
            if (var5_5 == 26) {
                this.generateIfJump(var6_6, var1_1, var4_4, var3_3);
                return;
            }
            if (var5_5 == 46 || var5_5 == 47) break block7;
            if (var5_5 == 52 || var5_5 == 53) ** GOTO lbl-1000
            if (var5_5 == 105 || var5_5 == 106) ** GOTO lbl16
            switch (var5_5) {
                default: {
                    this.generateExpression(var1_1, var2_2);
                    this.addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
                    this.cfw.add(154, var3_3);
                    this.cfw.add(167, var4_4);
                    return;
                }
lbl16: // 1 sources:
                var7_7 = this.cfw.acquireLabel();
                if (var5_5 == 106) {
                    this.generateIfJump(var6_6, var1_1, var7_7, var4_4);
                } else {
                    this.generateIfJump(var6_6, var1_1, var3_3, var7_7);
                }
                this.cfw.markLabel(var7_7);
                this.generateIfJump(var6_6.getNext(), var1_1, var3_3, var4_4);
                return;
                case 14: 
                case 15: 
                case 16: 
                case 17: lbl-1000: // 2 sources:
                {
                    this.visitIfJumpRelOp(var1_1, var6_6, var3_3, var4_4);
                    return;
                }
                case 12: 
                case 13: 
            }
        }
        this.visitIfJumpEqOp(var1_1, var6_6, var3_3, var4_4);
    }

    private void generateIntegerUnwrap() {
        this.cfw.addInvoke(182, "java/lang/Integer", "intValue", "()I");
    }

    private void generateIntegerWrap() {
        this.cfw.addInvoke(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
    }

    private void generateNestedFunctionInits() {
        int n = this.scriptOrFn.getFunctionCount();
        for (int i = 0; i != n; ++i) {
            OptFunctionNode optFunctionNode = OptFunctionNode.get(this.scriptOrFn, i);
            if (optFunctionNode.fnode.getFunctionType() != 1) continue;
            this.visitFunction(optFunctionNode, 1);
        }
    }

    private void generateObjectLiteralFactory(Node node, int n) {
        short s;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.codegen.getBodyMethodName(this.scriptOrFn));
        stringBuilder.append("_literal");
        stringBuilder.append(n);
        String string2 = stringBuilder.toString();
        this.initBodyGeneration();
        short s2 = this.firstFreeLocal;
        this.firstFreeLocal = s = (short)(s2 + 1);
        this.argsLocal = s2;
        this.localsMax = s;
        this.cfw.startMethod(string2, "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;", (short)2);
        this.visitObjectLiteral(node, node.getFirstChild(), true);
        this.cfw.add(176);
        this.cfw.stopMethod((short)(1 + this.localsMax));
    }

    private void generatePrologue() {
        String string2;
        short s;
        OptFunctionNode optFunctionNode;
        if (this.inDirectCallFunction) {
            int n = this.scriptOrFn.getParamCount();
            if (this.firstFreeLocal != 4) {
                Kit.codeBug();
            }
            for (int i = 0; i != n; ++i) {
                short s2;
                short[] arrs = this.varRegisters;
                arrs[i] = s2 = this.firstFreeLocal;
                this.firstFreeLocal = (short)(s2 + 3);
            }
            if (!this.fnCurrent.getParameterNumberContext()) {
                this.itsForcedObjectParameters = true;
                for (int i = 0; i != n; ++i) {
                    short s3 = this.varRegisters[i];
                    this.cfw.addALoad(s3);
                    this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                    int n2 = this.cfw.acquireLabel();
                    this.cfw.add(166, n2);
                    this.cfw.addDLoad(s3 + 1);
                    this.addDoubleWrap();
                    this.cfw.addAStore(s3);
                    this.cfw.markLabel(n2);
                }
            }
        }
        if (this.fnCurrent != null) {
            this.cfw.addALoad(this.funObjLocal);
            this.cfw.addInvoke(185, "org/mozilla/javascript/Scriptable", "getParentScope", "()Lorg/mozilla/javascript/Scriptable;");
            this.cfw.addAStore(this.variableObjectLocal);
        }
        short s4 = this.firstFreeLocal;
        this.firstFreeLocal = s = (short)(s4 + 1);
        this.argsLocal = s4;
        this.localsMax = s;
        if (this.isGenerator) {
            List list;
            short s5;
            short s6;
            this.firstFreeLocal = s6 = (short)(s + 1);
            this.operationLocal = s;
            this.localsMax = s6;
            this.cfw.addALoad(this.thisObjLocal);
            short s7 = this.firstFreeLocal;
            this.firstFreeLocal = s5 = (short)(s7 + 1);
            this.generatorStateLocal = s7;
            this.localsMax = s5;
            this.cfw.add(192, "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState");
            this.cfw.add(89);
            this.cfw.addAStore(this.generatorStateLocal);
            this.cfw.add(180, "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState", "thisObj", "Lorg/mozilla/javascript/Scriptable;");
            this.cfw.addAStore(this.thisObjLocal);
            if (this.epilogueLabel == -1) {
                this.epilogueLabel = this.cfw.acquireLabel();
            }
            if ((list = ((FunctionNode)this.scriptOrFn).getResumptionPoints()) != null) {
                this.generateGetGeneratorResumptionPoint();
                this.generatorSwitch = this.cfw.addTableSwitch(0, 0 + list.size());
                this.generateCheckForThrowOrClose(-1, false, 0);
            }
        }
        if (this.fnCurrent == null && this.scriptOrFn.getRegexpCount() != 0) {
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addInvoke(184, this.codegen.mainClassName, "_reInit", "(Lorg/mozilla/javascript/Context;)V");
        }
        if (this.compilerEnv.isGenerateObserverCount()) {
            this.saveCurrentCodeOffset();
        }
        if (this.hasVarsInRegs) {
            int n = this.scriptOrFn.getParamCount();
            if (n > 0 && !this.inDirectCallFunction) {
                this.cfw.addALoad(this.argsLocal);
                this.cfw.add(190);
                this.cfw.addPush(n);
                int n3 = this.cfw.acquireLabel();
                this.cfw.add(162, n3);
                this.cfw.addALoad(this.argsLocal);
                this.cfw.addPush(n);
                this.addScriptRuntimeInvoke("padArguments", "([Ljava/lang/Object;I)[Ljava/lang/Object;");
                this.cfw.addAStore(this.argsLocal);
                this.cfw.markLabel(n3);
            }
            int n4 = this.fnCurrent.fnode.getParamCount();
            int n5 = this.fnCurrent.fnode.getParamAndVarCount();
            boolean[] arrbl = this.fnCurrent.fnode.getParamAndVarConst();
            int n6 = -1;
            for (int i = 0; i != n5; ++i) {
                int n7 = -1;
                if (i < n4) {
                    if (!this.inDirectCallFunction) {
                        n7 = this.getNewWordLocal();
                        this.cfw.addALoad(this.argsLocal);
                        this.cfw.addPush(i);
                        this.cfw.add(50);
                        this.cfw.addAStore(n7);
                    }
                } else if (this.fnCurrent.isNumberVar(i)) {
                    n7 = this.getNewWordPairLocal(arrbl[i]);
                    this.cfw.addPush(0.0);
                    this.cfw.addDStore(n7);
                } else {
                    n7 = this.getNewWordLocal(arrbl[i]);
                    if (n6 == -1) {
                        Codegen.pushUndefined(this.cfw);
                        n6 = n7;
                    } else {
                        this.cfw.addALoad(n6);
                    }
                    this.cfw.addAStore(n7);
                }
                if (n7 >= 0) {
                    if (arrbl[i]) {
                        this.cfw.addPush(0);
                        ClassFileWriter classFileWriter = this.cfw;
                        int n8 = this.fnCurrent.isNumberVar(i) ? 2 : 1;
                        classFileWriter.addIStore(n8 + n7);
                    }
                    this.varRegisters[i] = n7;
                }
                if (!this.compilerEnv.isGenerateDebugInfo()) continue;
                String string3 = this.fnCurrent.fnode.getParamOrVarName(i);
                String string4 = this.fnCurrent.isNumberVar(i) ? "D" : "Ljava/lang/Object;";
                int n9 = this.cfw.getCurrentCodeOffset();
                if (n7 < 0) {
                    n7 = this.varRegisters[i];
                }
                this.cfw.addVariableDescriptor(string3, string4, n9, n7);
            }
            return;
        }
        if (this.isGenerator) {
            return;
        }
        ScriptNode scriptNode = this.scriptOrFn;
        boolean bl = scriptNode instanceof FunctionNode;
        boolean bl2 = false;
        if (bl) {
            boolean bl3 = ((FunctionNode)scriptNode).getFunctionType() == 4;
            bl2 = bl3;
        }
        if (this.fnCurrent != null) {
            string2 = "activation";
            this.cfw.addALoad(this.funObjLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.argsLocal);
            String string5 = bl2 ? "createArrowFunctionActivation" : "createFunctionActivation";
            this.cfw.addPush(this.scriptOrFn.isInStrictMode());
            this.addScriptRuntimeInvoke(string5, "(Lorg/mozilla/javascript/NativeFunction;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;Z)Lorg/mozilla/javascript/Scriptable;");
            this.cfw.addAStore(this.variableObjectLocal);
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.addScriptRuntimeInvoke("enterActivationFunction", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)V");
        } else {
            string2 = "global";
            this.cfw.addALoad(this.funObjLocal);
            this.cfw.addALoad(this.thisObjLocal);
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addPush(0);
            this.addScriptRuntimeInvoke("initScript", "(Lorg/mozilla/javascript/NativeFunction;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Z)V");
        }
        this.enterAreaStartLabel = this.cfw.acquireLabel();
        this.epilogueLabel = this.cfw.acquireLabel();
        this.cfw.markLabel(this.enterAreaStartLabel);
        this.generateNestedFunctionInits();
        if (this.compilerEnv.isGenerateDebugInfo()) {
            ClassFileWriter classFileWriter = this.cfw;
            classFileWriter.addVariableDescriptor(string2, "Lorg/mozilla/javascript/Scriptable;", classFileWriter.getCurrentCodeOffset(), this.variableObjectLocal);
        }
        if ((optFunctionNode = this.fnCurrent) == null) {
            this.popvLocal = this.getNewWordLocal();
            Codegen.pushUndefined(this.cfw);
            this.cfw.addAStore(this.popvLocal);
            int n = this.scriptOrFn.getEndLineno();
            if (n != -1) {
                this.cfw.addLineNumberEntry((short)n);
            }
            return;
        }
        if (optFunctionNode.itsContainsCalls0) {
            this.itsZeroArgArray = this.getNewWordLocal();
            this.cfw.add(178, "org/mozilla/javascript/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
            this.cfw.addAStore(this.itsZeroArgArray);
        }
        if (this.fnCurrent.itsContainsCalls1) {
            this.itsOneArgArray = this.getNewWordLocal();
            this.cfw.addPush(1);
            this.cfw.add(189, "java/lang/Object");
            this.cfw.addAStore(this.itsOneArgArray);
        }
    }

    private boolean generateSaveLocals(Node node) {
        int n = 0;
        for (int i = 0; i < this.firstFreeLocal; ++i) {
            if (this.locals[i] == 0) continue;
            ++n;
        }
        if (n == 0) {
            ((FunctionNode)this.scriptOrFn).addLiveLocals(node, null);
            return false;
        }
        int n2 = this.maxLocals;
        if (n2 <= n) {
            n2 = n;
        }
        this.maxLocals = n2;
        int[] arrn = new int[n];
        int n3 = 0;
        for (int i = 0; i < this.firstFreeLocal; ++i) {
            if (this.locals[i] == 0) continue;
            arrn[n3] = i;
            ++n3;
        }
        ((FunctionNode)this.scriptOrFn).addLiveLocals(node, arrn);
        this.generateGetGeneratorLocalsState();
        for (int i = 0; i < n; ++i) {
            this.cfw.add(89);
            this.cfw.addLoadConstant(i);
            this.cfw.addALoad(arrn[i]);
            this.cfw.add(83);
        }
        this.cfw.add(87);
        return true;
    }

    private void generateSetGeneratorResumptionPoint(int n) {
        this.cfw.addALoad(this.generatorStateLocal);
        this.cfw.addLoadConstant(n);
        this.cfw.add(181, "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState", "resumptionPoint", "I");
    }

    /*
     * Exception decompiling
     */
    private void generateStatement(Node var1_1) {
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

    private void generateThrowJavaScriptException() {
        this.cfw.add(187, "org/mozilla/javascript/JavaScriptException");
        this.cfw.add(90);
        this.cfw.add(95);
        this.cfw.addPush(this.scriptOrFn.getSourceName());
        this.cfw.addPush(this.itsLineNumber);
        this.cfw.addInvoke(183, "org/mozilla/javascript/JavaScriptException", "<init>", "(Ljava/lang/Object;Ljava/lang/String;I)V");
        this.cfw.add(191);
    }

    private void generateYieldPoint(Node node, boolean bl) {
        Node node2;
        int n = this.maxStack;
        int n2 = this.cfw.getStackTop();
        if (n <= n2) {
            n = n2;
        }
        this.maxStack = n;
        if (this.cfw.getStackTop() != 0) {
            this.generateGetGeneratorStackState();
            for (int i = 0; i < n2; ++i) {
                this.cfw.add(90);
                this.cfw.add(95);
                this.cfw.addLoadConstant(i);
                this.cfw.add(95);
                this.cfw.add(83);
            }
            this.cfw.add(87);
        }
        if ((node2 = node.getFirstChild()) != null) {
            this.generateExpression(node2, node);
        } else {
            Codegen.pushUndefined(this.cfw);
        }
        int n3 = this.getNextGeneratorState(node);
        this.generateSetGeneratorResumptionPoint(n3);
        boolean bl2 = this.generateSaveLocals(node);
        this.cfw.add(176);
        this.generateCheckForThrowOrClose(this.getTargetLabel(node), bl2, n3);
        if (n2 != 0) {
            this.generateGetGeneratorStackState();
            for (int i = 0; i < n2; ++i) {
                this.cfw.add(89);
                this.cfw.addLoadConstant(-1 + (n2 - i));
                this.cfw.add(50);
                this.cfw.add(95);
            }
            this.cfw.add(87);
        }
        if (bl) {
            this.cfw.addALoad(this.argsLocal);
        }
    }

    private Node getFinallyAtTarget(Node node) {
        Node node2;
        if (node == null) {
            return null;
        }
        if (node.getType() == 126) {
            return node;
        }
        if (node.getType() == 132 && (node2 = node.getNext()) != null && node2.getType() == 126) {
            return node2;
        }
        throw Kit.codeBug("bad finally target");
    }

    private int getLocalBlockRegister(Node node) {
        return ((Node)node.getProp(3)).getExistingIntProp(2);
    }

    private short getNewWordIntern(int n) {
        AssertionError assertionError;
        if (n >= 1 && n <= 3) {
            int[] arrn;
            short s;
            arrn = this.locals;
            s = -1;
            if (n > 1) {
                int n2 = this.firstFreeLocal;
                block0 : while (n2 + n <= 1024) {
                    for (int i = 0; i < n; ++i) {
                        if (arrn[n2 + i] == 0) continue;
                        n2 += i + 1;
                        continue block0;
                    }
                    s = (short)n2;
                    break;
                }
            } else {
                s = this.firstFreeLocal;
            }
            if (s != -1) {
                arrn[s] = 1;
                if (n > 1) {
                    arrn[s + 1] = 1;
                }
                if (n > 2) {
                    arrn[s + 2] = 1;
                }
                if (s == this.firstFreeLocal) {
                    for (int i = s + n; i < 1024; ++i) {
                        short s2;
                        if (arrn[i] != 0) continue;
                        this.firstFreeLocal = s2 = (short)i;
                        if (this.localsMax < s2) {
                            this.localsMax = s2;
                        }
                        return s;
                    }
                } else {
                    return s;
                }
            }
            throw Context.reportRuntimeError("Program too complex (out of locals)");
        }
        assertionError = new AssertionError();
        throw assertionError;
    }

    private short getNewWordLocal() {
        return this.getNewWordIntern(1);
    }

    private short getNewWordLocal(boolean bl) {
        int n = bl ? 2 : 1;
        return this.getNewWordIntern(n);
    }

    private short getNewWordPairLocal(boolean bl) {
        int n = bl ? 3 : 2;
        return this.getNewWordIntern(n);
    }

    private int getNextGeneratorState(Node node) {
        return 1 + ((FunctionNode)this.scriptOrFn).getResumptionPoints().indexOf((Object)node);
    }

    private int getTargetLabel(Node node) {
        int n = node.labelId();
        if (n == -1) {
            n = this.cfw.acquireLabel();
            node.labelId(n);
        }
        return n;
    }

    private void incReferenceWordLocal(short s) {
        int[] arrn = this.locals;
        arrn[s] = 1 + arrn[s];
    }

    private void initBodyGeneration() {
        this.varRegisters = null;
        if (this.scriptOrFn.getType() == 110) {
            int n;
            boolean bl;
            OptFunctionNode optFunctionNode;
            boolean bl2;
            this.fnCurrent = optFunctionNode = OptFunctionNode.get(this.scriptOrFn);
            this.hasVarsInRegs = bl2 = true ^ optFunctionNode.fnode.requiresActivation();
            if (bl2 && (n = this.fnCurrent.fnode.getParamAndVarCount()) != 0) {
                this.varRegisters = new short[n];
            }
            this.inDirectCallFunction = bl = this.fnCurrent.isTargetOfDirectCall();
            if (bl && !this.hasVarsInRegs) {
                Codegen.badTree();
            }
        } else {
            this.fnCurrent = null;
            this.hasVarsInRegs = false;
            this.inDirectCallFunction = false;
        }
        this.locals = new int[1024];
        this.funObjLocal = 0;
        this.contextLocal = 1;
        this.variableObjectLocal = (short)2;
        this.thisObjLocal = (short)3;
        this.localsMax = (short)4;
        this.firstFreeLocal = (short)4;
        this.popvLocal = (short)-1;
        this.argsLocal = (short)-1;
        this.itsZeroArgArray = (short)-1;
        this.itsOneArgArray = (short)-1;
        this.epilogueLabel = -1;
        this.enterAreaStartLabel = -1;
        this.generatorStateLocal = (short)-1;
    }

    private void inlineFinally(Node node) {
        int n = this.cfw.acquireLabel();
        int n2 = this.cfw.acquireLabel();
        this.cfw.markLabel(n);
        this.inlineFinally(node, n, n2);
        this.cfw.markLabel(n2);
    }

    private void inlineFinally(Node node, int n, int n2) {
        Node node2 = this.getFinallyAtTarget(node);
        node2.resetTargets();
        this.exceptionManager.markInlineFinallyStart(node2, n);
        for (Node node3 = node2.getFirstChild(); node3 != null; node3 = node3.getNext()) {
            this.generateStatement(node3);
        }
        this.exceptionManager.markInlineFinallyEnd(node2, n2);
    }

    private static boolean isArithmeticNode(Node node) {
        int n = node.getType();
        return n == 22 || n == 25 || n == 24 || n == 23;
        {
        }
    }

    private int nodeIsDirectCallParameter(Node node) {
        int n;
        if (node.getType() == 55 && this.inDirectCallFunction && !this.itsForcedObjectParameters && this.fnCurrent.isParameter(n = this.fnCurrent.getVarIndex(node))) {
            return this.varRegisters[n];
        }
        return -1;
    }

    private void releaseWordLocal(short s) {
        if (s < this.firstFreeLocal) {
            this.firstFreeLocal = s;
        }
        this.locals[s] = 0;
    }

    private void saveCurrentCodeOffset() {
        this.savedCodeOffset = this.cfw.getCurrentCodeOffset();
    }

    private void updateLineNumber(Node node) {
        int n;
        this.itsLineNumber = n = node.getLineno();
        if (n == -1) {
            return;
        }
        this.cfw.addLineNumberEntry((short)n);
    }

    private boolean varIsDirectCallParameter(int n) {
        return this.fnCurrent.isParameter(n) && this.inDirectCallFunction && !this.itsForcedObjectParameters;
    }

    private void visitArithmetic(Node node, int n, Node node2, Node node3) {
        if (node.getIntProp(8, -1) != -1) {
            this.generateExpression(node2, node);
            this.generateExpression(node2.getNext(), node);
            this.cfw.add(n);
            return;
        }
        boolean bl = BodyCodegen.isArithmeticNode(node3);
        this.generateExpression(node2, node);
        if (!BodyCodegen.isArithmeticNode(node2)) {
            this.addObjectToDouble();
        }
        this.generateExpression(node2.getNext(), node);
        if (!BodyCodegen.isArithmeticNode(node2.getNext())) {
            this.addObjectToDouble();
        }
        this.cfw.add(n);
        if (!bl) {
            this.addDoubleWrap();
        }
    }

    private void visitArrayLiteral(Node node, Node node2, boolean bl) {
        int[] arrn;
        int n = 0;
        for (Node node3 = node2; node3 != null; node3 = node3.getNext()) {
            ++n;
        }
        if (!(bl || n <= 10 && this.cfw.getCurrentCodeOffset() <= 30000 || this.hasVarsInRegs || this.isGenerator || this.inLocalBlock)) {
            if (this.literals == null) {
                this.literals = new LinkedList();
            }
            this.literals.add((Object)node);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.codegen.getBodyMethodName(this.scriptOrFn));
            stringBuilder.append("_literal");
            stringBuilder.append(this.literals.size());
            String string2 = stringBuilder.toString();
            this.cfw.addALoad(this.funObjLocal);
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.thisObjLocal);
            this.cfw.addALoad(this.argsLocal);
            this.cfw.addInvoke(182, this.codegen.mainClassName, string2, "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
            return;
        }
        if (this.isGenerator) {
            for (int i = 0; i != n; ++i) {
                this.generateExpression(node2, node);
                node2 = node2.getNext();
            }
            this.addNewObjectArray(n);
            for (int i = 0; i != n; ++i) {
                this.cfw.add(90);
                this.cfw.add(95);
                this.cfw.addPush(n - i - 1);
                this.cfw.add(95);
                this.cfw.add(83);
            }
        } else {
            this.addNewObjectArray(n);
            for (int i = 0; i != n; ++i) {
                this.cfw.add(89);
                this.cfw.addPush(i);
                this.generateExpression(node2, node);
                this.cfw.add(83);
                node2 = node2.getNext();
            }
        }
        if ((arrn = (int[])node.getProp(11)) == null) {
            this.cfw.add(1);
            this.cfw.add(3);
        } else {
            this.cfw.addPush(OptRuntime.encodeIntArray(arrn));
            this.cfw.addPush(arrn.length);
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addOptRuntimeInvoke("newArrayLiteral", "([Ljava/lang/Object;Ljava/lang/String;ILorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;");
    }

    private void visitBitOp(Node node, int n, Node node2) {
        int n2 = node.getIntProp(8, -1);
        this.generateExpression(node2, node);
        if (n == 20) {
            this.addScriptRuntimeInvoke("toUint32", "(Ljava/lang/Object;)J");
            this.generateExpression(node2.getNext(), node);
            this.addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
            this.cfw.addPush(31);
            this.cfw.add(126);
            this.cfw.add(125);
            this.cfw.add(138);
            this.addDoubleWrap();
            return;
        }
        if (n2 == -1) {
            this.addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
            this.generateExpression(node2.getNext(), node);
            this.addScriptRuntimeInvoke("toInt32", "(Ljava/lang/Object;)I");
        } else {
            this.addScriptRuntimeInvoke("toInt32", "(D)I");
            this.generateExpression(node2.getNext(), node);
            this.addScriptRuntimeInvoke("toInt32", "(D)I");
        }
        if (n != 18) {
            if (n != 19) {
                switch (n) {
                    default: {
                        throw Codegen.badTree();
                    }
                    case 11: {
                        this.cfw.add(126);
                        break;
                    }
                    case 10: {
                        this.cfw.add(130);
                        break;
                    }
                    case 9: {
                        this.cfw.add(128);
                        break;
                    }
                }
            } else {
                this.cfw.add(122);
            }
        } else {
            this.cfw.add(120);
        }
        this.cfw.add(135);
        if (n2 == -1) {
            this.addDoubleWrap();
        }
    }

    private void visitDotQuery(Node node, Node node2) {
        this.updateLineNumber(node);
        this.generateExpression(node2, node);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("enterDotQuery", "(Ljava/lang/Object;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;");
        this.cfw.addAStore(this.variableObjectLocal);
        this.cfw.add(1);
        int n = this.cfw.acquireLabel();
        this.cfw.markLabel(n);
        this.cfw.add(87);
        this.generateExpression(node2.getNext(), node);
        this.addScriptRuntimeInvoke("toBoolean", "(Ljava/lang/Object;)Z");
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("updateDotQuery", "(ZLorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
        this.cfw.add(89);
        this.cfw.add(198, n);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("leaveDotQuery", "(Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;");
        this.cfw.addAStore(this.variableObjectLocal);
    }

    private void visitFunction(OptFunctionNode optFunctionNode, int n) {
        int n2 = this.codegen.getIndex((ScriptNode)optFunctionNode.fnode);
        this.cfw.add(187, this.codegen.mainClassName);
        this.cfw.add(89);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addPush(n2);
        this.cfw.addInvoke(183, this.codegen.mainClassName, "<init>", "(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;I)V");
        if (n == 4) {
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.thisObjLocal);
            this.addOptRuntimeInvoke("bindThis", "(Lorg/mozilla/javascript/NativeFunction;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Function;");
        }
        if (n != 2) {
            if (n == 4) {
                return;
            }
            this.cfw.addPush(n);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.contextLocal);
            this.addOptRuntimeInvoke("initFunction", "(Lorg/mozilla/javascript/NativeFunction;ILorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;)V");
            return;
        }
    }

    private void visitGetProp(Node node, Node node2) {
        this.generateExpression(node2, node);
        Node node3 = node2.getNext();
        this.generateExpression(node3, node);
        if (node.getType() == 34) {
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.addScriptRuntimeInvoke("getObjectPropNoWarn", "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
            return;
        }
        if (node2.getType() == 43 && node3.getType() == 41) {
            this.cfw.addALoad(this.contextLocal);
            this.addScriptRuntimeInvoke("getObjectProp", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;Lorg/mozilla/javascript/Context;)Ljava/lang/Object;");
            return;
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("getObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
    }

    private void visitGetVar(Node node) {
        if (!this.hasVarsInRegs) {
            Kit.codeBug();
        }
        int n = this.fnCurrent.getVarIndex(node);
        short s = this.varRegisters[n];
        if (this.varIsDirectCallParameter(n)) {
            if (node.getIntProp(8, -1) != -1) {
                this.dcpLoadAsNumber(s);
                return;
            }
            this.dcpLoadAsObject(s);
            return;
        }
        if (this.fnCurrent.isNumberVar(n)) {
            this.cfw.addDLoad(s);
            return;
        }
        this.cfw.addALoad(s);
    }

    private void visitGoto(Jump jump, int n, Node node) {
        Node node2 = jump.target;
        if (n != 6 && n != 7) {
            if (n == 136) {
                if (this.isGenerator) {
                    this.addGotoWithReturn(node2);
                    return;
                }
                this.inlineFinally(node2);
                return;
            }
            this.addGoto(node2, 167);
            return;
        }
        if (node != null) {
            int n2 = this.getTargetLabel(node2);
            int n3 = this.cfw.acquireLabel();
            if (n == 6) {
                this.generateIfJump(node, jump, n2, n3);
            } else {
                this.generateIfJump(node, jump, n3, n2);
            }
            this.cfw.markLabel(n3);
            return;
        }
        throw Codegen.badTree();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void visitIfJumpEqOp(Node node, Node node2, int n, int n2) {
        Node node3 = node2;
        int n3 = n;
        int n4 = n2;
        if (n3 == -1 || n4 == -1) throw Codegen.badTree();
        short s = this.cfw.getStackTop();
        int n5 = node.getType();
        Node node4 = node2.getNext();
        if (node2.getType() != 42 && node4.getType() != 42) {
            String string2;
            Node node5;
            int n6;
            int n7 = this.nodeIsDirectCallParameter(node3);
            if (n7 != -1 && node4.getType() == 150 && (node5 = node4.getFirstChild()).getType() == 40) {
                this.cfw.addALoad(n7);
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                int n8 = this.cfw.acquireLabel();
                this.cfw.add(166, n8);
                this.cfw.addDLoad(n7 + 1);
                this.cfw.addPush(node5.getDouble());
                this.cfw.add(151);
                if (n5 == 12) {
                    this.cfw.add(153, n3);
                } else {
                    this.cfw.add(154, n3);
                }
                this.cfw.add(167, n4);
                this.cfw.markLabel(n8);
            }
            this.generateExpression(node3, node);
            this.generateExpression(node4, node);
            if (n5 != 12) {
                if (n5 != 13) {
                    if (n5 != 46) {
                        if (n5 != 47) throw Codegen.badTree();
                        string2 = "shallowEq";
                        n6 = 153;
                    } else {
                        string2 = "shallowEq";
                        n6 = 154;
                    }
                } else {
                    string2 = "eq";
                    n6 = 153;
                }
            } else {
                string2 = "eq";
                n6 = 154;
            }
            this.addScriptRuntimeInvoke(string2, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
            this.cfw.add(n6, n3);
            this.cfw.add(167, n4);
        } else {
            if (node2.getType() == 42) {
                node3 = node4;
            }
            this.generateExpression(node3, node);
            int n9 = 199;
            if (n5 != 46 && n5 != 47) {
                if (n5 != 12) {
                    if (n5 != 13) throw Codegen.badTree();
                    n3 = n2;
                    n4 = n;
                }
                this.cfw.add(89);
                int n10 = this.cfw.acquireLabel();
                this.cfw.add(n9, n10);
                short s2 = this.cfw.getStackTop();
                this.cfw.add(87);
                this.cfw.add(167, n3);
                this.cfw.markLabel(n10, s2);
                Codegen.pushUndefined(this.cfw);
                this.cfw.add(165, n3);
            } else {
                if (n5 == 46) {
                    n9 = 198;
                }
                this.cfw.add(n9, n3);
            }
            this.cfw.add(167, n4);
        }
        if (s != this.cfw.getStackTop()) throw Codegen.badTree();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void visitIfJumpRelOp(Node node, Node node2, int n, int n2) {
        if (n == -1 || n2 == -1) throw Codegen.badTree();
        int n3 = node.getType();
        Node node3 = node2.getNext();
        if (n3 != 53 && n3 != 52) {
            int n4 = node.getIntProp(8, -1);
            int n5 = this.nodeIsDirectCallParameter(node2);
            int n6 = this.nodeIsDirectCallParameter(node3);
            if (n4 != -1) {
                if (n4 != 2) {
                    this.generateExpression(node2, node);
                } else if (n5 != -1) {
                    this.dcpLoadAsNumber(n5);
                } else {
                    this.generateExpression(node2, node);
                    this.addObjectToDouble();
                }
                if (n4 != 1) {
                    this.generateExpression(node3, node);
                } else if (n6 != -1) {
                    this.dcpLoadAsNumber(n6);
                } else {
                    this.generateExpression(node3, node);
                    this.addObjectToDouble();
                }
                this.genSimpleCompare(n3, n, n2);
                return;
            }
            if (n5 != -1 && n6 != -1) {
                short s = this.cfw.getStackTop();
                int n7 = this.cfw.acquireLabel();
                this.cfw.addALoad(n5);
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                this.cfw.add(166, n7);
                this.cfw.addDLoad(n5 + 1);
                this.dcpLoadAsNumber(n6);
                this.genSimpleCompare(n3, n, n2);
                if (s != this.cfw.getStackTop()) throw Codegen.badTree();
                this.cfw.markLabel(n7);
                int n8 = this.cfw.acquireLabel();
                this.cfw.addALoad(n6);
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                this.cfw.add(166, n8);
                this.cfw.addALoad(n5);
                this.addObjectToDouble();
                this.cfw.addDLoad(n6 + 1);
                this.genSimpleCompare(n3, n, n2);
                if (s != this.cfw.getStackTop()) throw Codegen.badTree();
                this.cfw.markLabel(n8);
                this.cfw.addALoad(n5);
                this.cfw.addALoad(n6);
            } else {
                this.generateExpression(node2, node);
                this.generateExpression(node3, node);
            }
            if (n3 == 17 || n3 == 16) {
                this.cfw.add(95);
            }
            String string2 = n3 != 14 && n3 != 16 ? "cmp_LE" : "cmp_LT";
            this.addScriptRuntimeInvoke(string2, "(Ljava/lang/Object;Ljava/lang/Object;)Z");
            this.cfw.add(154, n);
            this.cfw.add(167, n2);
            return;
        }
        this.generateExpression(node2, node);
        this.generateExpression(node3, node);
        this.cfw.addALoad(this.contextLocal);
        String string3 = n3 == 53 ? "instanceOf" : "in";
        this.addScriptRuntimeInvoke(string3, "(Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;)Z");
        this.cfw.add(154, n);
        this.cfw.add(167, n2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void visitIncDec(Node node) {
        int n = node.getExistingIntProp(13);
        Node node2 = node.getFirstChild();
        int n2 = node2.getType();
        if (n2 != 33) {
            if (n2 == 34) throw Kit.codeBug();
            if (n2 != 36) {
                if (n2 != 39) {
                    if (n2 != 55) {
                        if (n2 != 68) {
                            Codegen.badTree();
                            return;
                        }
                        this.generateExpression(node2.getFirstChild(), node);
                        this.cfw.addALoad(this.contextLocal);
                        this.cfw.addALoad(this.variableObjectLocal);
                        this.cfw.addPush(n);
                        this.addScriptRuntimeInvoke("refIncrDecr", "(Lorg/mozilla/javascript/Ref;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;I)Ljava/lang/Object;");
                        return;
                    }
                    if (!this.hasVarsInRegs) {
                        Kit.codeBug();
                    }
                    boolean bl = (n & 2) != 0;
                    int n3 = this.fnCurrent.getVarIndex(node2);
                    short s = this.varRegisters[n3];
                    if (this.fnCurrent.fnode.getParamAndVarConst()[n3]) {
                        if (node.getIntProp(8, -1) != -1) {
                            short s2 = this.varIsDirectCallParameter(n3);
                            this.cfw.addDLoad(s + s2);
                            if (bl) return;
                            this.cfw.addPush(1.0);
                            if ((n & 1) == 0) {
                                this.cfw.add(99);
                                return;
                            } else {
                                this.cfw.add(103);
                            }
                            return;
                        }
                        if (this.varIsDirectCallParameter(n3)) {
                            this.dcpLoadAsObject(s);
                        } else {
                            this.cfw.addALoad(s);
                        }
                        if (bl) {
                            this.cfw.add(89);
                            this.addObjectToDouble();
                            this.cfw.add(88);
                            return;
                        }
                        this.addObjectToDouble();
                        this.cfw.addPush(1.0);
                        if ((n & 1) == 0) {
                            this.cfw.add(99);
                        } else {
                            this.cfw.add(103);
                        }
                        this.addDoubleWrap();
                        return;
                    }
                    if (node.getIntProp(8, -1) != -1) {
                        short s3 = this.varIsDirectCallParameter(n3);
                        this.cfw.addDLoad(s + s3);
                        if (bl) {
                            this.cfw.add(92);
                        }
                        this.cfw.addPush(1.0);
                        if ((n & 1) == 0) {
                            this.cfw.add(99);
                        } else {
                            this.cfw.add(103);
                        }
                        if (!bl) {
                            this.cfw.add(92);
                        }
                        this.cfw.addDStore(s + s3);
                        return;
                    }
                    if (this.varIsDirectCallParameter(n3)) {
                        this.dcpLoadAsObject(s);
                    } else {
                        this.cfw.addALoad(s);
                    }
                    this.addObjectToDouble();
                    if (bl) {
                        this.cfw.add(92);
                    }
                    this.cfw.addPush(1.0);
                    if ((n & 1) == 0) {
                        this.cfw.add(99);
                    } else {
                        this.cfw.add(103);
                    }
                    this.addDoubleWrap();
                    if (!bl) {
                        this.cfw.add(89);
                    }
                    this.cfw.addAStore(s);
                    if (!bl) return;
                    this.addDoubleWrap();
                    return;
                }
                this.cfw.addALoad(this.variableObjectLocal);
                this.cfw.addPush(node2.getString());
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addPush(n);
                this.addScriptRuntimeInvoke("nameIncrDecr", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;Lorg/mozilla/javascript/Context;I)Ljava/lang/Object;");
                return;
            }
            Node node3 = node2.getFirstChild();
            this.generateExpression(node3, node);
            this.generateExpression(node3.getNext(), node);
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addPush(n);
            if (node3.getNext().getIntProp(8, -1) != -1) {
                this.addOptRuntimeInvoke("elemIncrDecr", "(Ljava/lang/Object;DLorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;I)Ljava/lang/Object;");
                return;
            }
            this.addScriptRuntimeInvoke("elemIncrDecr", "(Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;I)Ljava/lang/Object;");
            return;
        }
        Node node4 = node2.getFirstChild();
        this.generateExpression(node4, node);
        this.generateExpression(node4.getNext(), node);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addPush(n);
        this.addScriptRuntimeInvoke("propIncrDecr", "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;I)Ljava/lang/Object;");
    }

    private void visitObjectLiteral(Node node, Node node2, boolean bl) {
        boolean bl2;
        int n;
        block12 : {
            Object[] arrobject = (Object[])node.getProp(12);
            n = arrobject.length;
            if (!(bl || n <= 10 && this.cfw.getCurrentCodeOffset() <= 30000 || this.hasVarsInRegs || this.isGenerator || this.inLocalBlock)) {
                if (this.literals == null) {
                    this.literals = new LinkedList();
                }
                this.literals.add((Object)node);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.codegen.getBodyMethodName(this.scriptOrFn));
                stringBuilder.append("_literal");
                stringBuilder.append(this.literals.size());
                String string2 = stringBuilder.toString();
                this.cfw.addALoad(this.funObjLocal);
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.cfw.addALoad(this.thisObjLocal);
                this.cfw.addALoad(this.argsLocal);
                this.cfw.addInvoke(182, this.codegen.mainClassName, string2, "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
                return;
            }
            if (this.isGenerator) {
                this.addLoadPropertyValues(node, node2, n);
                this.addLoadPropertyIds(arrobject, n);
                this.cfw.add(95);
            } else {
                this.addLoadPropertyIds(arrobject, n);
                this.addLoadPropertyValues(node, node2, n);
            }
            Node node3 = node2;
            int n2 = 0;
            do {
                bl2 = false;
                if (n2 == n) break block12;
                int n3 = node3.getType();
                if (n3 == 152 || n3 == 153) break;
                node3 = node3.getNext();
                ++n2;
            } while (true);
            bl2 = true;
        }
        if (bl2) {
            this.cfw.addPush(n);
            this.cfw.add(188, 10);
            Node node4 = node2;
            for (int i = 0; i != n; ++i) {
                this.cfw.add(89);
                this.cfw.addPush(i);
                int n4 = node4.getType();
                if (n4 == 152) {
                    this.cfw.add(2);
                } else if (n4 == 153) {
                    this.cfw.add(4);
                } else {
                    this.cfw.add(3);
                }
                this.cfw.add(79);
                node4 = node4.getNext();
            }
        } else {
            this.cfw.add(1);
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("newObjectLiteral", "([Ljava/lang/Object;[Ljava/lang/Object;[ILorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;");
    }

    private void visitOptimizedCall(Node node, OptFunctionNode optFunctionNode, int n, Node node2) {
        short s;
        Node node3 = node2.getNext();
        String string2 = this.codegen.mainClassName;
        if (n == 30) {
            this.generateExpression(node2, node);
            s = 0;
        } else {
            this.generateFunctionAndThisObj(node2, node);
            s = this.getNewWordLocal();
            this.cfw.addAStore(s);
        }
        int n2 = this.cfw.acquireLabel();
        int n3 = this.cfw.acquireLabel();
        this.cfw.add(89);
        this.cfw.add(193, string2);
        this.cfw.add(153, n3);
        this.cfw.add(192, string2);
        this.cfw.add(89);
        this.cfw.add(180, string2, "_id", "I");
        this.cfw.addPush(this.codegen.getIndex((ScriptNode)optFunctionNode.fnode));
        this.cfw.add(160, n3);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        if (n == 30) {
            this.cfw.add(1);
        } else {
            this.cfw.addALoad(s);
        }
        for (Node node4 = node3; node4 != null; node4 = node4.getNext()) {
            int n4 = this.nodeIsDirectCallParameter(node4);
            if (n4 >= 0) {
                this.cfw.addALoad(n4);
                this.cfw.addDLoad(n4 + 1);
                continue;
            }
            if (node4.getIntProp(8, -1) == 0) {
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                this.generateExpression(node4, node);
                continue;
            }
            this.generateExpression(node4, node);
            this.cfw.addPush(0.0);
        }
        this.cfw.add(178, "org/mozilla/javascript/ScriptRuntime", "emptyArgs", "[Ljava/lang/Object;");
        ClassFileWriter classFileWriter = this.cfw;
        String string3 = this.codegen.mainClassName;
        String string4 = n == 30 ? this.codegen.getDirectCtorName((ScriptNode)optFunctionNode.fnode) : this.codegen.getBodyMethodName((ScriptNode)optFunctionNode.fnode);
        classFileWriter.addInvoke(184, string3, string4, this.codegen.getBodyMethodSignature((ScriptNode)optFunctionNode.fnode));
        this.cfw.add(167, n2);
        this.cfw.markLabel(n3);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        if (n != 30) {
            this.cfw.addALoad(s);
            this.releaseWordLocal(s);
        }
        this.generateCallArgArray(node, node3, true);
        if (n == 30) {
            this.addScriptRuntimeInvoke("newObject", "(Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
        } else {
            this.cfw.addInvoke(185, "org/mozilla/javascript/Callable", "call", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
        }
        this.cfw.markLabel(n2);
    }

    private void visitSetConst(Node node, Node node2) {
        String string2 = node.getFirstChild().getString();
        while (node2 != null) {
            this.generateExpression(node2, node);
            node2 = node2.getNext();
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addPush(string2);
        this.addScriptRuntimeInvoke("setConst", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Ljava/lang/String;)Ljava/lang/Object;");
    }

    private void visitSetConstVar(Node node, Node node2, boolean bl) {
        if (!this.hasVarsInRegs) {
            Kit.codeBug();
        }
        int n = this.fnCurrent.getVarIndex(node);
        this.generateExpression(node2.getNext(), node);
        boolean bl2 = node.getIntProp(8, -1) != -1;
        short s = this.varRegisters[n];
        int n2 = this.cfw.acquireLabel();
        int n3 = this.cfw.acquireLabel();
        if (bl2) {
            this.cfw.addILoad(s + 2);
            this.cfw.add(154, n3);
            short s2 = this.cfw.getStackTop();
            this.cfw.addPush(1);
            this.cfw.addIStore(s + 2);
            this.cfw.addDStore(s);
            if (bl) {
                this.cfw.addDLoad(s);
                this.cfw.markLabel(n3, s2);
            } else {
                this.cfw.add(167, n2);
                this.cfw.markLabel(n3, s2);
                this.cfw.add(88);
            }
        } else {
            this.cfw.addILoad(s + 1);
            this.cfw.add(154, n3);
            short s3 = this.cfw.getStackTop();
            this.cfw.addPush(1);
            this.cfw.addIStore(s + 1);
            this.cfw.addAStore(s);
            if (bl) {
                this.cfw.addALoad(s);
                this.cfw.markLabel(n3, s3);
            } else {
                this.cfw.add(167, n2);
                this.cfw.markLabel(n3, s3);
                this.cfw.add(87);
            }
        }
        this.cfw.markLabel(n2);
    }

    private void visitSetElem(int n, Node node, Node node2) {
        this.generateExpression(node2, node);
        Node node3 = node2.getNext();
        if (n == 141) {
            this.cfw.add(89);
        }
        this.generateExpression(node3, node);
        Node node4 = node3.getNext();
        boolean bl = node.getIntProp(8, -1) != -1;
        if (n == 141) {
            if (bl) {
                this.cfw.add(93);
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.addScriptRuntimeInvoke("getObjectIndex", "(Ljava/lang/Object;DLorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
            } else {
                this.cfw.add(90);
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.addScriptRuntimeInvoke("getObjectElem", "(Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
            }
        }
        this.generateExpression(node4, node);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        if (bl) {
            this.addScriptRuntimeInvoke("setObjectIndex", "(Ljava/lang/Object;DLjava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
            return;
        }
        this.addScriptRuntimeInvoke("setObjectElem", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
    }

    private void visitSetName(Node node, Node node2) {
        String string2 = node.getFirstChild().getString();
        while (node2 != null) {
            this.generateExpression(node2, node);
            node2 = node2.getNext();
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addPush(string2);
        this.addScriptRuntimeInvoke("setName", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Ljava/lang/Object;");
    }

    private void visitSetProp(int n, Node node, Node node2) {
        this.generateExpression(node2, node);
        Node node3 = node2.getNext();
        if (n == 140) {
            this.cfw.add(89);
        }
        this.generateExpression(node3, node);
        Node node4 = node3.getNext();
        if (n == 140) {
            this.cfw.add(90);
            if (node2.getType() == 43 && node3.getType() == 41) {
                this.cfw.addALoad(this.contextLocal);
                this.addScriptRuntimeInvoke("getObjectProp", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;Lorg/mozilla/javascript/Context;)Ljava/lang/Object;");
            } else {
                this.cfw.addALoad(this.contextLocal);
                this.cfw.addALoad(this.variableObjectLocal);
                this.addScriptRuntimeInvoke("getObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
            }
        }
        this.generateExpression(node4, node);
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.addScriptRuntimeInvoke("setObjectProp", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void visitSetVar(Node node, Node node2, boolean bl) {
        if (!this.hasVarsInRegs) {
            Kit.codeBug();
        }
        int n = this.fnCurrent.getVarIndex(node);
        this.generateExpression(node2.getNext(), node);
        boolean bl2 = node.getIntProp(8, -1) != -1;
        short s = this.varRegisters[n];
        if (this.fnCurrent.fnode.getParamAndVarConst()[n]) {
            if (bl) return;
            if (bl2) {
                this.cfw.add(88);
                return;
            }
            this.cfw.add(87);
            return;
        }
        if (this.varIsDirectCallParameter(n)) {
            if (bl2) {
                if (bl) {
                    this.cfw.add(92);
                }
                this.cfw.addALoad(s);
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                int n2 = this.cfw.acquireLabel();
                int n3 = this.cfw.acquireLabel();
                this.cfw.add(165, n2);
                short s2 = this.cfw.getStackTop();
                this.addDoubleWrap();
                this.cfw.addAStore(s);
                this.cfw.add(167, n3);
                this.cfw.markLabel(n2, s2);
                this.cfw.addDStore(s + 1);
                this.cfw.markLabel(n3);
                return;
            }
            if (bl) {
                this.cfw.add(89);
            }
            this.cfw.addAStore(s);
            return;
        }
        boolean bl3 = this.fnCurrent.isNumberVar(n);
        if (bl2) {
            if (bl3) {
                this.cfw.addDStore(s);
                if (!bl) return;
                this.cfw.addDLoad(s);
                return;
            }
            if (bl) {
                this.cfw.add(92);
            }
            this.addDoubleWrap();
            this.cfw.addAStore(s);
            return;
        }
        if (bl3) {
            Kit.codeBug();
        }
        this.cfw.addAStore(s);
        if (!bl) return;
        this.cfw.addALoad(s);
    }

    private void visitSpecialCall(Node node, int n, int n2, Node node2) {
        String string2;
        String string3;
        this.cfw.addALoad(this.contextLocal);
        if (n == 30) {
            this.generateExpression(node2, node);
        } else {
            this.generateFunctionAndThisObj(node2, node);
        }
        this.generateCallArgArray(node, node2.getNext(), false);
        if (n == 30) {
            string3 = "newObjectSpecial";
            string2 = "(Lorg/mozilla/javascript/Context;Ljava/lang/Object;[Ljava/lang/Object;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;I)Ljava/lang/Object;";
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.thisObjLocal);
            this.cfw.addPush(n2);
        } else {
            string3 = "callSpecial";
            string2 = "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;ILjava/lang/String;I)Ljava/lang/Object;";
            this.cfw.addALoad(this.variableObjectLocal);
            this.cfw.addALoad(this.thisObjLocal);
            this.cfw.addPush(n2);
            String string4 = this.scriptOrFn.getSourceName();
            ClassFileWriter classFileWriter = this.cfw;
            String string5 = string4 == null ? "" : string4;
            classFileWriter.addPush(string5);
            this.cfw.addPush(this.itsLineNumber);
        }
        this.addOptRuntimeInvoke(string3, string2);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void visitStandardCall(Node node, Node node2) {
        if (node.getType() == 38) {
            String string2;
            String string3;
            Node node3 = node2.getNext();
            int n = node2.getType();
            if (node3 == null) {
                if (n == 39) {
                    String string4 = node2.getString();
                    this.cfw.addPush(string4);
                    string3 = "callName0";
                    string2 = "(Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                } else if (n == 33) {
                    Node node4 = node2.getFirstChild();
                    this.generateExpression(node4, node);
                    String string5 = node4.getNext().getString();
                    this.cfw.addPush(string5);
                    string2 = "(Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                    string3 = "callProp0";
                } else {
                    if (n == 34) throw Kit.codeBug();
                    this.generateFunctionAndThisObj(node2, node);
                    string3 = "call0";
                    string2 = "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                }
            } else if (n == 39) {
                String string6 = node2.getString();
                this.generateCallArgArray(node, node3, false);
                this.cfw.addPush(string6);
                string3 = "callName";
                string2 = "([Ljava/lang/Object;Ljava/lang/String;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
            } else {
                int n2 = 0;
                for (Node node5 = node3; node5 != null; node5 = node5.getNext()) {
                    ++n2;
                }
                this.generateFunctionAndThisObj(node2, node);
                if (n2 == 1) {
                    this.generateExpression(node3, node);
                    string3 = "call1";
                    string2 = "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                } else if (n2 == 2) {
                    this.generateExpression(node3, node);
                    this.generateExpression(node3.getNext(), node);
                    string3 = "call2";
                    string2 = "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                } else {
                    this.generateCallArgArray(node, node3, false);
                    string3 = "callN";
                    string2 = "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;";
                }
            }
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.addOptRuntimeInvoke(string3, string2);
            return;
        }
        RuntimeException runtimeException = Codegen.badTree();
        throw runtimeException;
    }

    private void visitStandardNew(Node node, Node node2) {
        if (node.getType() == 30) {
            Node node3 = node2.getNext();
            this.generateExpression(node2, node);
            this.cfw.addALoad(this.contextLocal);
            this.cfw.addALoad(this.variableObjectLocal);
            this.generateCallArgArray(node, node3, false);
            this.addScriptRuntimeInvoke("newObject", "(Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
            return;
        }
        throw Codegen.badTree();
    }

    private void visitStrictSetName(Node node, Node node2) {
        String string2 = node.getFirstChild().getString();
        while (node2 != null) {
            this.generateExpression(node2, node);
            node2 = node2.getNext();
        }
        this.cfw.addALoad(this.contextLocal);
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addPush(string2);
        this.addScriptRuntimeInvoke("strictSetName", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Ljava/lang/Object;");
    }

    private void visitSwitch(Jump jump, Node node) {
        this.generateExpression(node, jump);
        short s = this.getNewWordLocal();
        this.cfw.addAStore(s);
        for (Jump jump2 = (Jump)node.getNext(); jump2 != null; jump2 = (Jump)jump2.getNext()) {
            if (jump2.getType() == 116) {
                this.generateExpression(jump2.getFirstChild(), jump2);
                this.cfw.addALoad(s);
                this.addScriptRuntimeInvoke("shallowEq", "(Ljava/lang/Object;Ljava/lang/Object;)Z");
                this.addGoto(jump2.target, 154);
                continue;
            }
            throw Codegen.badTree();
        }
        this.releaseWordLocal(s);
    }

    private void visitTryCatchFinally(Jump jump, Node node) {
        int n;
        int n2;
        short s = this.getNewWordLocal();
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addAStore(s);
        int n3 = this.cfw.acquireLabel();
        this.cfw.markLabel(n3, (short)0);
        Node node2 = jump.target;
        Node node3 = jump.getFinally();
        int[] arrn = new int[5];
        this.exceptionManager.pushExceptionInfo(jump);
        if (node2 != null) {
            arrn[0] = this.cfw.acquireLabel();
            arrn[1] = this.cfw.acquireLabel();
            arrn[2] = this.cfw.acquireLabel();
            Context context = Context.getCurrentContext();
            if (context != null && context.hasFeature(13)) {
                arrn[3] = this.cfw.acquireLabel();
            }
        }
        if (node3 != null) {
            arrn[4] = this.cfw.acquireLabel();
        }
        this.exceptionManager.setHandlers(arrn, n3);
        if (this.isGenerator && node3 != null) {
            FinallyReturnPoint finallyReturnPoint = new FinallyReturnPoint();
            if (this.finallys == null) {
                this.finallys = new HashMap();
            }
            this.finallys.put((Object)node3, (Object)finallyReturnPoint);
            this.finallys.put((Object)node3.getNext(), (Object)finallyReturnPoint);
        }
        for (Node node4 = node; node4 != null; node4 = node4.getNext()) {
            if (node4 == node2) {
                int n4 = this.getTargetLabel(node2);
                this.exceptionManager.removeHandler(0, n4);
                this.exceptionManager.removeHandler(1, n4);
                this.exceptionManager.removeHandler(2, n4);
                this.exceptionManager.removeHandler(3, n4);
            }
            this.generateStatement(node4);
        }
        int n5 = this.cfw.acquireLabel();
        this.cfw.add(167, n5);
        int n6 = this.getLocalBlockRegister(jump);
        if (node2 != null) {
            int n7 = node2.labelId();
            int n8 = arrn[0];
            n = n6;
            n2 = n5;
            int n9 = n;
            this.generateCatchBlock(0, s, n7, n9, n8);
            this.generateCatchBlock(1, s, n7, n9, arrn[1]);
            this.generateCatchBlock(2, s, n7, n9, arrn[2]);
            Context context = Context.getCurrentContext();
            if (context != null && context.hasFeature(13)) {
                int n10 = arrn[3];
                this.generateCatchBlock(3, s, n7, n, n10);
            }
        } else {
            n = n6;
            n2 = n5;
        }
        if (node3 != null) {
            int n11 = this.cfw.acquireLabel();
            int n12 = this.cfw.acquireLabel();
            this.cfw.markHandler(n11);
            if (!this.isGenerator) {
                this.cfw.markLabel(arrn[4]);
            }
            ClassFileWriter classFileWriter = this.cfw;
            int n13 = n;
            classFileWriter.addAStore(n13);
            this.cfw.addALoad(s);
            this.cfw.addAStore(this.variableObjectLocal);
            int n14 = node3.labelId();
            if (this.isGenerator) {
                this.addGotoWithReturn(node3);
            } else {
                this.inlineFinally(node3, arrn[4], n12);
            }
            this.cfw.addALoad(n13);
            if (this.isGenerator) {
                this.cfw.add(192, "java/lang/Throwable");
            }
            this.cfw.add(191);
            this.cfw.markLabel(n12);
            if (this.isGenerator) {
                this.cfw.addExceptionHandler(n3, n14, n11, null);
            }
        }
        this.releaseWordLocal(s);
        this.cfw.markLabel(n2);
        if (!this.isGenerator) {
            this.exceptionManager.popExceptionInfo();
        }
    }

    private void visitTypeofname(Node node) {
        int n;
        if (this.hasVarsInRegs && (n = this.fnCurrent.fnode.getIndexForNameNode(node)) >= 0) {
            if (this.fnCurrent.isNumberVar(n)) {
                this.cfw.addPush("number");
                return;
            }
            if (this.varIsDirectCallParameter(n)) {
                short s = this.varRegisters[n];
                this.cfw.addALoad(s);
                this.cfw.add(178, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
                int n2 = this.cfw.acquireLabel();
                this.cfw.add(165, n2);
                short s2 = this.cfw.getStackTop();
                this.cfw.addALoad(s);
                this.addScriptRuntimeInvoke("typeof", "(Ljava/lang/Object;)Ljava/lang/String;");
                int n3 = this.cfw.acquireLabel();
                this.cfw.add(167, n3);
                this.cfw.markLabel(n2, s2);
                this.cfw.addPush("number");
                this.cfw.markLabel(n3);
                return;
            }
            this.cfw.addALoad(this.varRegisters[n]);
            this.addScriptRuntimeInvoke("typeof", "(Ljava/lang/Object;)Ljava/lang/String;");
            return;
        }
        this.cfw.addALoad(this.variableObjectLocal);
        this.cfw.addPush(node.getString());
        this.addScriptRuntimeInvoke("typeofName", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Ljava/lang/String;");
    }

    void generateBodyCode() {
        this.isGenerator = Codegen.isGenerator(this.scriptOrFn);
        this.initBodyGeneration();
        if (this.isGenerator) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("(");
            stringBuilder.append(this.codegen.mainClassSignature);
            stringBuilder.append("Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/Object;");
            String string2 = stringBuilder.toString();
            ClassFileWriter classFileWriter = this.cfw;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(this.codegen.getBodyMethodName(this.scriptOrFn));
            stringBuilder2.append("_gen");
            classFileWriter.startMethod(stringBuilder2.toString(), string2, (short)10);
        } else {
            this.cfw.startMethod(this.codegen.getBodyMethodName(this.scriptOrFn), this.codegen.getBodyMethodSignature(this.scriptOrFn), (short)10);
        }
        this.generatePrologue();
        Object object = this.fnCurrent != null ? this.scriptOrFn.getLastChild() : this.scriptOrFn;
        this.generateStatement((Node)object);
        this.generateEpilogue();
        this.cfw.stopMethod((short)(1 + this.localsMax));
        if (this.isGenerator) {
            this.generateGenerator();
        }
        if (this.literals != null) {
            for (int i = 0; i < this.literals.size(); ++i) {
                Node node = (Node)this.literals.get(i);
                int n = node.getType();
                if (n != 66) {
                    if (n != 67) {
                        Kit.codeBug(Token.typeToName(n));
                        continue;
                    }
                    this.generateObjectLiteralFactory(node, i + 1);
                    continue;
                }
                this.generateArrayLiteralFactory(node, i + 1);
            }
        }
    }

    private class ExceptionManager {
        private LinkedList<ExceptionInfo> exceptionInfo = new LinkedList();

        ExceptionManager() {
        }

        private void endCatch(ExceptionInfo exceptionInfo, int n, int n2) {
            if (exceptionInfo.exceptionStarts[n] != 0) {
                int n3 = exceptionInfo.exceptionStarts[n];
                if (BodyCodegen.this.cfw.getLabelPC(n3) != BodyCodegen.this.cfw.getLabelPC(n2)) {
                    BodyCodegen.this.cfw.addExceptionHandler(exceptionInfo.exceptionStarts[n], n2, exceptionInfo.handlerLabels[n], BodyCodegen.this.exceptionTypeToName(n));
                }
                return;
            }
            throw new IllegalStateException("bad exception start");
        }

        private ExceptionInfo getTop() {
            return (ExceptionInfo)this.exceptionInfo.getLast();
        }

        void addHandler(int n, int n2, int n3) {
            ExceptionInfo exceptionInfo = this.getTop();
            exceptionInfo.handlerLabels[n] = n2;
            exceptionInfo.exceptionStarts[n] = n3;
        }

        void markInlineFinallyEnd(Node node, int n) {
            LinkedList<ExceptionInfo> linkedList = this.exceptionInfo;
            ListIterator listIterator = linkedList.listIterator(linkedList.size());
            while (listIterator.hasPrevious()) {
                ExceptionInfo exceptionInfo = (ExceptionInfo)listIterator.previous();
                for (int i = 0; i < 5; ++i) {
                    if (exceptionInfo.handlerLabels[i] == 0 || exceptionInfo.currentFinally != node) continue;
                    exceptionInfo.exceptionStarts[i] = n;
                    exceptionInfo.currentFinally = null;
                }
                if (exceptionInfo.finallyBlock != node) continue;
                return;
            }
        }

        void markInlineFinallyStart(Node node, int n) {
            LinkedList<ExceptionInfo> linkedList = this.exceptionInfo;
            ListIterator listIterator = linkedList.listIterator(linkedList.size());
            while (listIterator.hasPrevious()) {
                ExceptionInfo exceptionInfo = (ExceptionInfo)listIterator.previous();
                for (int i = 0; i < 5; ++i) {
                    if (exceptionInfo.handlerLabels[i] == 0 || exceptionInfo.currentFinally != null) continue;
                    this.endCatch(exceptionInfo, i, n);
                    exceptionInfo.exceptionStarts[i] = 0;
                    exceptionInfo.currentFinally = node;
                }
                if (exceptionInfo.finallyBlock != node) continue;
                return;
            }
        }

        void popExceptionInfo() {
            this.exceptionInfo.removeLast();
        }

        void pushExceptionInfo(Jump jump) {
            ExceptionInfo exceptionInfo = new ExceptionInfo(jump, BodyCodegen.this.getFinallyAtTarget(jump.getFinally()));
            this.exceptionInfo.add((Object)exceptionInfo);
        }

        int removeHandler(int n, int n2) {
            ExceptionInfo exceptionInfo = this.getTop();
            if (exceptionInfo.handlerLabels[n] != 0) {
                int n3 = exceptionInfo.handlerLabels[n];
                this.endCatch(exceptionInfo, n, n2);
                exceptionInfo.handlerLabels[n] = 0;
                return n3;
            }
            return 0;
        }

        void setHandlers(int[] arrn, int n) {
            for (int i = 0; i < arrn.length; ++i) {
                if (arrn[i] == 0) continue;
                this.addHandler(i, arrn[i], n);
            }
        }

        private class ExceptionInfo {
            Node currentFinally;
            int[] exceptionStarts;
            Node finallyBlock;
            int[] handlerLabels;

            ExceptionInfo(Jump jump, Node node) {
                this.finallyBlock = node;
                this.handlerLabels = new int[5];
                this.exceptionStarts = new int[5];
                this.currentFinally = null;
            }
        }

    }

    static class FinallyReturnPoint {
        public List<Integer> jsrPoints = new ArrayList();
        public int tableLabel = 0;

        FinallyReturnPoint() {
        }
    }

}

