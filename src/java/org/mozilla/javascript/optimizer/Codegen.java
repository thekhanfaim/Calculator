/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.SecurityException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.lang.UnsupportedOperationException
 *  java.lang.reflect.Constructor
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  org.mozilla.javascript.NativeFunction
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.classfile.ClassFileWriter;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Evaluator;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.optimizer.BodyCodegen;
import org.mozilla.javascript.optimizer.OptFunctionNode;
import org.mozilla.javascript.optimizer.OptTransformer;
import org.mozilla.javascript.optimizer.Optimizer;

public class Codegen
implements Evaluator {
    static final String DEFAULT_MAIN_METHOD_CLASS = "org.mozilla.javascript.optimizer.OptRuntime";
    static final String FUNCTION_CONSTRUCTOR_SIGNATURE = "(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Context;I)V";
    static final String FUNCTION_INIT_SIGNATURE = "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)V";
    static final String ID_FIELD_NAME = "_id";
    static final String REGEXP_INIT_METHOD_NAME = "_reInit";
    static final String REGEXP_INIT_METHOD_SIGNATURE = "(Lorg/mozilla/javascript/Context;)V";
    private static final String SUPER_CLASS_NAME = "org.mozilla.javascript.NativeFunction";
    private static final Object globalLock = new Object();
    private static int globalSerialClassCounter;
    private CompilerEnvirons compilerEnv;
    private ObjArray directCallTargets;
    private double[] itsConstantList;
    private int itsConstantListSize;
    String mainClassName;
    String mainClassSignature;
    private String mainMethodClass = "org.mozilla.javascript.optimizer.OptRuntime";
    private ObjToIntMap scriptOrFnIndexes;
    ScriptNode[] scriptOrFnNodes;

    private static void addDoubleWrap(ClassFileWriter classFileWriter) {
        classFileWriter.addInvoke(184, "org/mozilla/javascript/optimizer/OptRuntime", "wrapDouble", "(D)Ljava/lang/Double;");
    }

    static RuntimeException badTree() {
        throw new RuntimeException("Bad tree in codegen");
    }

    private static void collectScriptNodes_r(ScriptNode scriptNode, ObjArray objArray) {
        objArray.add((Object)scriptNode);
        int n = scriptNode.getFunctionCount();
        for (int i = 0; i != n; ++i) {
            Codegen.collectScriptNodes_r((ScriptNode)scriptNode.getFunctionNode(i), objArray);
        }
    }

    private Class<?> defineClass(Object object, Object object2) {
        Throwable throwable;
        Object[] arrobject = (Object[])object;
        String string2 = (String)arrobject[0];
        byte[] arrby = (byte[])arrobject[1];
        GeneratedClassLoader generatedClassLoader = SecurityController.createLoader(this.getClass().getClassLoader(), object2);
        try {
            Class<?> class_ = generatedClassLoader.defineClass(string2, arrby);
            generatedClassLoader.linkClass(class_);
            return class_;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throwable = illegalArgumentException;
        }
        catch (SecurityException securityException) {
            throwable = securityException;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Malformed optimizer package ");
        stringBuilder.append((Object)throwable);
        throw new RuntimeException(stringBuilder.toString());
    }

    private void emitConstantDudeInitializers(ClassFileWriter classFileWriter) {
        int n = this.itsConstantListSize;
        if (n == 0) {
            return;
        }
        classFileWriter.startMethod("<clinit>", "()V", (short)24);
        double[] arrd = this.itsConstantList;
        for (int i = 0; i != n; ++i) {
            double d = arrd[i];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("_k");
            stringBuilder.append(i);
            String string2 = stringBuilder.toString();
            String string3 = Codegen.getStaticConstantWrapperType(d);
            classFileWriter.addField(string2, string3, (short)10);
            int n2 = (int)d;
            if ((double)n2 == d) {
                classFileWriter.addPush(n2);
                classFileWriter.addInvoke(184, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
            } else {
                classFileWriter.addPush(d);
                Codegen.addDoubleWrap(classFileWriter);
            }
            classFileWriter.add(179, this.mainClassName, string2, string3);
        }
        classFileWriter.add(177);
        classFileWriter.stopMethod((short)0);
    }

    private void emitDirectConstructor(ClassFileWriter classFileWriter, OptFunctionNode optFunctionNode) {
        classFileWriter.startMethod(this.getDirectCtorName((ScriptNode)optFunctionNode.fnode), this.getBodyMethodSignature((ScriptNode)optFunctionNode.fnode), (short)10);
        int n = optFunctionNode.fnode.getParamCount();
        int n2 = 1 + (4 + n * 3);
        classFileWriter.addALoad(0);
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addInvoke(182, "org/mozilla/javascript/BaseFunction", "createObject", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Lorg/mozilla/javascript/Scriptable;");
        classFileWriter.addAStore(n2);
        classFileWriter.addALoad(0);
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addALoad(n2);
        for (int i = 0; i < n; ++i) {
            classFileWriter.addALoad(4 + i * 3);
            classFileWriter.addDLoad(5 + i * 3);
        }
        classFileWriter.addALoad(4 + n * 3);
        classFileWriter.addInvoke(184, this.mainClassName, this.getBodyMethodName((ScriptNode)optFunctionNode.fnode), this.getBodyMethodSignature((ScriptNode)optFunctionNode.fnode));
        int n3 = classFileWriter.acquireLabel();
        classFileWriter.add(89);
        classFileWriter.add(193, "org/mozilla/javascript/Scriptable");
        classFileWriter.add(153, n3);
        classFileWriter.add(192, "org/mozilla/javascript/Scriptable");
        classFileWriter.add(176);
        classFileWriter.markLabel(n3);
        classFileWriter.addALoad(n2);
        classFileWriter.add(176);
        classFileWriter.stopMethod((short)(n2 + 1));
    }

    private void emitRegExpInit(ClassFileWriter classFileWriter) {
        ScriptNode[] arrscriptNode;
        ScriptNode[] arrscriptNode2;
        int n = 0;
        for (int i = 0; i != (arrscriptNode = this.scriptOrFnNodes).length; ++i) {
            n += arrscriptNode[i].getRegexpCount();
        }
        if (n == 0) {
            return;
        }
        int n2 = 10;
        classFileWriter.startMethod(REGEXP_INIT_METHOD_NAME, REGEXP_INIT_METHOD_SIGNATURE, (short)n2);
        classFileWriter.addField("_reInitDone", "Z", (short)74);
        classFileWriter.add(178, this.mainClassName, "_reInitDone", "Z");
        int n3 = classFileWriter.acquireLabel();
        classFileWriter.add(153, n3);
        classFileWriter.add(177);
        classFileWriter.markLabel(n3);
        classFileWriter.addALoad(0);
        classFileWriter.addInvoke(184, "org/mozilla/javascript/ScriptRuntime", "checkRegExpProxy", "(Lorg/mozilla/javascript/Context;)Lorg/mozilla/javascript/RegExpProxy;");
        int n4 = 1;
        classFileWriter.addAStore(n4);
        for (int i = 0; i != (arrscriptNode2 = this.scriptOrFnNodes).length; ++i) {
            ScriptNode scriptNode = arrscriptNode2[i];
            int n5 = scriptNode.getRegexpCount();
            for (int j = 0; j != n5; ++j) {
                String string2 = this.getCompiledRegexpName(scriptNode, j);
                String string3 = scriptNode.getRegexpString(j);
                String string4 = scriptNode.getRegexpFlags(j);
                classFileWriter.addField(string2, "Ljava/lang/Object;", (short)n2);
                classFileWriter.addALoad(n4);
                classFileWriter.addALoad(0);
                classFileWriter.addPush(string3);
                if (string4 == null) {
                    classFileWriter.add(n4);
                } else {
                    classFileWriter.addPush(string4);
                }
                int n6 = n;
                int n7 = n3;
                classFileWriter.addInvoke(185, "org/mozilla/javascript/RegExpProxy", "compileRegExp", "(Lorg/mozilla/javascript/Context;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
                classFileWriter.add(179, this.mainClassName, string2, "Ljava/lang/Object;");
                n = n6;
                n3 = n7;
                n2 = 10;
                n4 = 1;
            }
            n2 = 10;
            n4 = 1;
        }
        classFileWriter.addPush(1);
        classFileWriter.add(179, this.mainClassName, "_reInitDone", "Z");
        classFileWriter.add(177);
        classFileWriter.stopMethod((short)2);
    }

    private void generateCallMethod(ClassFileWriter classFileWriter, boolean bl) {
        classFileWriter.startMethod("call", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;", (short)17);
        int n = classFileWriter.acquireLabel();
        classFileWriter.addALoad(1);
        classFileWriter.addInvoke(184, "org/mozilla/javascript/ScriptRuntime", "hasTopCall", "(Lorg/mozilla/javascript/Context;)Z");
        classFileWriter.add(154, n);
        classFileWriter.addALoad(0);
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addALoad(3);
        int n2 = 4;
        classFileWriter.addALoad(n2);
        classFileWriter.addPush(bl);
        classFileWriter.addInvoke(184, "org/mozilla/javascript/ScriptRuntime", "doTopCall", "(Lorg/mozilla/javascript/Callable;Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;Z)Ljava/lang/Object;");
        classFileWriter.add(176);
        classFileWriter.markLabel(n);
        classFileWriter.addALoad(0);
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addALoad(3);
        classFileWriter.addALoad(n2);
        int n3 = this.scriptOrFnNodes.length;
        boolean bl2 = false;
        if (2 <= n3) {
            bl2 = true;
        }
        short s = 0;
        int n4 = 0;
        if (bl2) {
            classFileWriter.addLoadThis();
            classFileWriter.add(180, classFileWriter.getClassName(), ID_FIELD_NAME, "I");
            n4 = classFileWriter.addTableSwitch(1, n3 - 1);
        }
        for (int i = 0; i != n3; ++i) {
            short s2;
            ScriptNode scriptNode = this.scriptOrFnNodes[i];
            if (bl2) {
                if (i == 0) {
                    classFileWriter.markTableSwitchDefault(n4);
                    s = classFileWriter.getStackTop();
                } else {
                    classFileWriter.markTableSwitchCase(n4, i - 1, s);
                }
            }
            if (scriptNode.getType() == 110) {
                OptFunctionNode optFunctionNode = OptFunctionNode.get(scriptNode);
                if (optFunctionNode.isTargetOfDirectCall()) {
                    int n5 = optFunctionNode.fnode.getParamCount();
                    if (n5 != 0) {
                        for (int j = 0; j != n5; ++j) {
                            classFileWriter.add(190);
                            classFileWriter.addPush(j);
                            int n6 = classFileWriter.acquireLabel();
                            int n7 = classFileWriter.acquireLabel();
                            classFileWriter.add(164, n6);
                            classFileWriter.addALoad(n2);
                            classFileWriter.addPush(j);
                            classFileWriter.add(50);
                            classFileWriter.add(167, n7);
                            classFileWriter.markLabel(n6);
                            Codegen.pushUndefined(classFileWriter);
                            classFileWriter.markLabel(n7);
                            classFileWriter.adjustStackTop(-1);
                            short s3 = s;
                            classFileWriter.addPush(0.0);
                            classFileWriter.addALoad(4);
                            s = s3;
                            n2 = 4;
                        }
                        s2 = s;
                    } else {
                        s2 = s;
                    }
                } else {
                    s2 = s;
                }
            } else {
                s2 = s;
            }
            classFileWriter.addInvoke(184, this.mainClassName, this.getBodyMethodName(scriptNode), this.getBodyMethodSignature(scriptNode));
            classFileWriter.add(176);
            s = s2;
            n2 = 4;
        }
        classFileWriter.stopMethod((short)5);
    }

    private byte[] generateCode(String string2) {
        int n = this.scriptOrFnNodes[0].getType();
        int n2 = 1;
        boolean bl = n == 137;
        ScriptNode[] arrscriptNode = this.scriptOrFnNodes;
        if (arrscriptNode.length <= n2 && bl) {
            n2 = 0;
        }
        boolean bl2 = arrscriptNode[0].isInStrictMode();
        boolean bl3 = this.compilerEnv.isGenerateDebugInfo();
        String string3 = null;
        if (bl3) {
            string3 = this.scriptOrFnNodes[0].getSourceName();
        }
        ClassFileWriter classFileWriter = new ClassFileWriter(this.mainClassName, SUPER_CLASS_NAME, string3);
        classFileWriter.addField(ID_FIELD_NAME, "I", (short)2);
        if (n2 != 0) {
            this.generateFunctionConstructor(classFileWriter);
        }
        if (bl) {
            classFileWriter.addInterface("org/mozilla/javascript/Script");
            this.generateScriptCtor(classFileWriter);
            this.generateMain(classFileWriter);
            this.generateExecute(classFileWriter);
        }
        this.generateCallMethod(classFileWriter, bl2);
        this.generateResumeGenerator(classFileWriter);
        this.generateNativeFunctionOverrides(classFileWriter, string2);
        int n3 = this.scriptOrFnNodes.length;
        for (int i = 0; i != n3; ++i) {
            ScriptNode scriptNode = this.scriptOrFnNodes[i];
            BodyCodegen bodyCodegen = new BodyCodegen();
            bodyCodegen.cfw = classFileWriter;
            bodyCodegen.codegen = this;
            bodyCodegen.compilerEnv = this.compilerEnv;
            bodyCodegen.scriptOrFn = scriptNode;
            bodyCodegen.scriptOrFnIndex = i;
            bodyCodegen.generateBodyCode();
            if (scriptNode.getType() != 110) continue;
            OptFunctionNode optFunctionNode = OptFunctionNode.get(scriptNode);
            this.generateFunctionInit(classFileWriter, optFunctionNode);
            if (!optFunctionNode.isTargetOfDirectCall()) continue;
            this.emitDirectConstructor(classFileWriter, optFunctionNode);
        }
        this.emitRegExpInit(classFileWriter);
        this.emitConstantDudeInitializers(classFileWriter);
        return classFileWriter.toByteArray();
    }

    private void generateExecute(ClassFileWriter classFileWriter) {
        classFileWriter.startMethod("exec", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;)Ljava/lang/Object;", (short)17);
        classFileWriter.addLoadThis();
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.add(89);
        classFileWriter.add(1);
        classFileWriter.addInvoke(182, classFileWriter.getClassName(), "call", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;[Ljava/lang/Object;)Ljava/lang/Object;");
        classFileWriter.add(176);
        classFileWriter.stopMethod((short)3);
    }

    private void generateFunctionConstructor(ClassFileWriter classFileWriter) {
        RuntimeException runtimeException;
        short s = 1;
        classFileWriter.startMethod("<init>", FUNCTION_CONSTRUCTOR_SIGNATURE, s);
        classFileWriter.addALoad(0);
        classFileWriter.addInvoke(183, SUPER_CLASS_NAME, "<init>", "()V");
        classFileWriter.addLoadThis();
        classFileWriter.addILoad(3);
        classFileWriter.add(181, classFileWriter.getClassName(), ID_FIELD_NAME, "I");
        classFileWriter.addLoadThis();
        classFileWriter.addALoad(2);
        classFileWriter.addALoad(s);
        int n = this.scriptOrFnNodes[0].getType() == 137 ? 1 : 0;
        int n2 = this.scriptOrFnNodes.length;
        if (n != n2) {
            if (2 > n2 - n) {
                s = 0;
            }
            short s2 = s;
            short s3 = 0;
            int n3 = 0;
            if (s2 != 0) {
                classFileWriter.addILoad(3);
                n3 = classFileWriter.addTableSwitch(n + 1, n2 - 1);
            }
            for (int i = n; i != n2; ++i) {
                if (s2 != 0) {
                    if (i == n) {
                        classFileWriter.markTableSwitchDefault(n3);
                        s3 = classFileWriter.getStackTop();
                    } else {
                        classFileWriter.markTableSwitchCase(n3, i - 1 - n, s3);
                    }
                }
                OptFunctionNode optFunctionNode = OptFunctionNode.get(this.scriptOrFnNodes[i]);
                classFileWriter.addInvoke(183, this.mainClassName, this.getFunctionInitMethodName(optFunctionNode), FUNCTION_INIT_SIGNATURE);
                classFileWriter.add(177);
            }
            classFileWriter.stopMethod((short)4);
            return;
        }
        runtimeException = Codegen.badTree();
        throw runtimeException;
    }

    private void generateFunctionInit(ClassFileWriter classFileWriter, OptFunctionNode optFunctionNode) {
        classFileWriter.startMethod(this.getFunctionInitMethodName(optFunctionNode), FUNCTION_INIT_SIGNATURE, (short)18);
        classFileWriter.addLoadThis();
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addInvoke(182, "org/mozilla/javascript/NativeFunction", "initScriptFunction", FUNCTION_INIT_SIGNATURE);
        if (optFunctionNode.fnode.getRegexpCount() != 0) {
            classFileWriter.addALoad(1);
            classFileWriter.addInvoke(184, this.mainClassName, REGEXP_INIT_METHOD_NAME, REGEXP_INIT_METHOD_SIGNATURE);
        }
        classFileWriter.add(177);
        classFileWriter.stopMethod((short)3);
    }

    private void generateMain(ClassFileWriter classFileWriter) {
        classFileWriter.startMethod("main", "([Ljava/lang/String;)V", (short)9);
        classFileWriter.add(187, classFileWriter.getClassName());
        classFileWriter.add(89);
        classFileWriter.addInvoke(183, classFileWriter.getClassName(), "<init>", "()V");
        classFileWriter.add(42);
        classFileWriter.addInvoke(184, this.mainMethodClass, "main", "(Lorg/mozilla/javascript/Script;[Ljava/lang/String;)V");
        classFileWriter.add(177);
        classFileWriter.stopMethod((short)1);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void generateNativeFunctionOverrides(ClassFileWriter classFileWriter, String string2) {
        Codegen codegen = this;
        String string3 = "()I";
        int n = 1;
        classFileWriter.startMethod("getLanguageVersion", string3, (short)n);
        classFileWriter.addPush(codegen.compilerEnv.getLanguageVersion());
        classFileWriter.add(172);
        classFileWriter.stopMethod((short)n);
        boolean bl = true;
        int n2 = 2;
        for (int i = 0; i != 6; ++i) {
            String string4;
            int n3;
            boolean bl2;
            if (i == 4 && string2 == null) {
                string4 = string3;
                bl2 = bl;
                n3 = n2;
            } else {
                short s;
                if (i != 0) {
                    if (i != n) {
                        if (i != 2) {
                            if (i != 3) {
                                if (i != 4) {
                                    if (i != 5) throw Kit.codeBug();
                                    s = 3;
                                    classFileWriter.startMethod("getParamOrVarConst", "(I)Z", (short)n);
                                } else {
                                    s = 1;
                                    classFileWriter.startMethod("getEncodedSource", "()Ljava/lang/String;", (short)n);
                                    classFileWriter.addPush(string2);
                                }
                            } else {
                                s = 2;
                                classFileWriter.startMethod("getParamOrVarName", "(I)Ljava/lang/String;", (short)n);
                            }
                        } else {
                            s = 1;
                            classFileWriter.startMethod("getParamAndVarCount", string3, (short)n);
                        }
                    } else {
                        s = 1;
                        classFileWriter.startMethod("getParamCount", string3, (short)n);
                    }
                } else {
                    s = 1;
                    classFileWriter.startMethod("getFunctionName", "()Ljava/lang/String;", (short)n);
                }
                int n4 = codegen.scriptOrFnNodes.length;
                int n5 = 0;
                if (n4 > n) {
                    classFileWriter.addLoadThis();
                    String string5 = classFileWriter.getClassName();
                    string4 = string3;
                    classFileWriter.add(180, string5, ID_FIELD_NAME, "I");
                    n5 = classFileWriter.addTableSwitch(1, n4 - 1);
                } else {
                    string4 = string3;
                }
                int n6 = 0;
                for (int j = 0; j != n4; ++j) {
                    boolean bl3;
                    int n7;
                    int n8;
                    ScriptNode scriptNode = codegen.scriptOrFnNodes[j];
                    if (j == 0) {
                        if (n4 > 1) {
                            classFileWriter.markTableSwitchDefault(n5);
                            n6 = classFileWriter.getStackTop();
                        }
                    } else {
                        classFileWriter.markTableSwitchCase(n5, j - 1, n6);
                    }
                    if (i != 0) {
                        if (i != 1) {
                            if (i != 2) {
                                if (i != 3) {
                                    if (i != 4) {
                                        if (i != 5) throw Kit.codeBug();
                                        int n9 = scriptNode.getParamAndVarCount();
                                        boolean[] arrbl = scriptNode.getParamAndVarConst();
                                        if (n9 == 0) {
                                            classFileWriter.add(3);
                                            classFileWriter.add(172);
                                            n7 = n6;
                                            bl3 = bl;
                                            n8 = n2;
                                        } else if (n9 == 1) {
                                            classFileWriter.addPush(arrbl[0]);
                                            classFileWriter.add(172);
                                            n7 = n6;
                                            bl3 = bl;
                                            n8 = n2;
                                        } else {
                                            classFileWriter.addILoad(1);
                                            n7 = n6;
                                            int n10 = classFileWriter.addTableSwitch(1, n9 - 1);
                                            for (int k = 0; k != n9; ++k) {
                                                boolean bl4;
                                                int n11;
                                                if (classFileWriter.getStackTop() != 0) {
                                                    Kit.codeBug();
                                                }
                                                if (k == 0) {
                                                    classFileWriter.markTableSwitchDefault(n10);
                                                    n11 = n9;
                                                    bl4 = bl;
                                                } else {
                                                    n11 = n9;
                                                    int n12 = k - 1;
                                                    bl4 = bl;
                                                    classFileWriter.markTableSwitchCase(n10, n12, 0);
                                                }
                                                classFileWriter.addPush(arrbl[k]);
                                                classFileWriter.add(172);
                                                n9 = n11;
                                                bl = bl4;
                                            }
                                            bl3 = bl;
                                            n8 = n2;
                                        }
                                    } else {
                                        n7 = n6;
                                        bl3 = bl;
                                        classFileWriter.addPush(scriptNode.getEncodedSourceStart());
                                        classFileWriter.addPush(scriptNode.getEncodedSourceEnd());
                                        classFileWriter.addInvoke(182, "java/lang/String", "substring", "(II)Ljava/lang/String;");
                                        classFileWriter.add(176);
                                        n8 = n2;
                                    }
                                } else {
                                    n7 = n6;
                                    bl3 = bl;
                                    int n13 = scriptNode.getParamAndVarCount();
                                    if (n13 == 0) {
                                        classFileWriter.add(1);
                                        classFileWriter.add(176);
                                        n8 = n2;
                                    } else if (n13 == 1) {
                                        classFileWriter.addPush(scriptNode.getParamOrVarName(0));
                                        classFileWriter.add(176);
                                        n8 = n2;
                                    } else {
                                        classFileWriter.addILoad(1);
                                        int n14 = classFileWriter.addTableSwitch(1, n13 - 1);
                                        for (int k = 0; k != n13; ++k) {
                                            int n15;
                                            int n16;
                                            if (classFileWriter.getStackTop() != 0) {
                                                Kit.codeBug();
                                            }
                                            String string6 = scriptNode.getParamOrVarName(k);
                                            if (k == 0) {
                                                classFileWriter.markTableSwitchDefault(n14);
                                                n16 = n13;
                                                n15 = n2;
                                            } else {
                                                n16 = n13;
                                                int n17 = k - 1;
                                                n15 = n2;
                                                classFileWriter.markTableSwitchCase(n14, n17, 0);
                                            }
                                            classFileWriter.addPush(string6);
                                            classFileWriter.add(176);
                                            n13 = n16;
                                            n2 = n15;
                                        }
                                        n8 = n2;
                                    }
                                }
                            } else {
                                n7 = n6;
                                bl3 = bl;
                                n8 = n2;
                                classFileWriter.addPush(scriptNode.getParamAndVarCount());
                                classFileWriter.add(172);
                            }
                        } else {
                            n7 = n6;
                            bl3 = bl;
                            n8 = n2;
                            classFileWriter.addPush(scriptNode.getParamCount());
                            classFileWriter.add(172);
                        }
                    } else {
                        n7 = n6;
                        bl3 = bl;
                        n8 = n2;
                        if (scriptNode.getType() == 137) {
                            classFileWriter.addPush("");
                        } else {
                            classFileWriter.addPush(((FunctionNode)scriptNode).getName());
                        }
                        classFileWriter.add(176);
                    }
                    codegen = this;
                    n6 = n7;
                    bl = bl3;
                    n2 = n8;
                }
                bl2 = bl;
                n3 = n2;
                classFileWriter.stopMethod(s);
            }
            codegen = this;
            string3 = string4;
            bl = bl2;
            n2 = n3;
            n = 1;
        }
    }

    private void generateResumeGenerator(ClassFileWriter classFileWriter) {
        ScriptNode[] arrscriptNode;
        ScriptNode[] arrscriptNode2;
        boolean bl = false;
        for (int i = 0; i < (arrscriptNode2 = this.scriptOrFnNodes).length; ++i) {
            if (!Codegen.isGenerator(arrscriptNode2[i])) continue;
            bl = true;
        }
        if (!bl) {
            return;
        }
        classFileWriter.startMethod("resumeGenerator", "(Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;ILjava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", (short)17);
        classFileWriter.addALoad(0);
        classFileWriter.addALoad(1);
        classFileWriter.addALoad(2);
        classFileWriter.addALoad(4);
        classFileWriter.addALoad(5);
        classFileWriter.addILoad(3);
        classFileWriter.addLoadThis();
        classFileWriter.add(180, classFileWriter.getClassName(), ID_FIELD_NAME, "I");
        int n = classFileWriter.addTableSwitch(0, this.scriptOrFnNodes.length - 1);
        classFileWriter.markTableSwitchDefault(n);
        int n2 = classFileWriter.acquireLabel();
        for (int i = 0; i < (arrscriptNode = this.scriptOrFnNodes).length; ++i) {
            ScriptNode scriptNode = arrscriptNode[i];
            classFileWriter.markTableSwitchCase(n, i, 6);
            if (Codegen.isGenerator(scriptNode)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("(");
                stringBuilder.append(this.mainClassSignature);
                stringBuilder.append("Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/Object;");
                String string2 = stringBuilder.toString();
                String string3 = this.mainClassName;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(this.getBodyMethodName(scriptNode));
                stringBuilder2.append("_gen");
                classFileWriter.addInvoke(184, string3, stringBuilder2.toString(), string2);
                classFileWriter.add(176);
                continue;
            }
            classFileWriter.add(167, n2);
        }
        classFileWriter.markLabel(n2);
        Codegen.pushUndefined(classFileWriter);
        classFileWriter.add(176);
        classFileWriter.stopMethod((short)6);
    }

    private void generateScriptCtor(ClassFileWriter classFileWriter) {
        classFileWriter.startMethod("<init>", "()V", (short)1);
        classFileWriter.addLoadThis();
        classFileWriter.addInvoke(183, SUPER_CLASS_NAME, "<init>", "()V");
        classFileWriter.addLoadThis();
        classFileWriter.addPush(0);
        classFileWriter.add(181, classFileWriter.getClassName(), ID_FIELD_NAME, "I");
        classFileWriter.add(177);
        classFileWriter.stopMethod((short)1);
    }

    private static String getStaticConstantWrapperType(double d) {
        if ((double)((int)d) == d) {
            return "Ljava/lang/Integer;";
        }
        return "Ljava/lang/Double;";
    }

    private static void initOptFunctions_r(ScriptNode scriptNode) {
        int n = scriptNode.getFunctionCount();
        for (int i = 0; i != n; ++i) {
            FunctionNode functionNode = scriptNode.getFunctionNode(i);
            new OptFunctionNode(functionNode);
            Codegen.initOptFunctions_r((ScriptNode)functionNode);
        }
    }

    private void initScriptNodesData(ScriptNode scriptNode) {
        ObjArray objArray = new ObjArray();
        Codegen.collectScriptNodes_r(scriptNode, objArray);
        int n = objArray.size();
        Object[] arrobject = new ScriptNode[n];
        this.scriptOrFnNodes = arrobject;
        objArray.toArray(arrobject);
        this.scriptOrFnIndexes = new ObjToIntMap(n);
        for (int i = 0; i != n; ++i) {
            this.scriptOrFnIndexes.put((Object)this.scriptOrFnNodes[i], i);
        }
    }

    static boolean isGenerator(ScriptNode scriptNode) {
        return scriptNode.getType() == 110 && ((FunctionNode)scriptNode).isGenerator();
    }

    static void pushUndefined(ClassFileWriter classFileWriter) {
        classFileWriter.add(178, "org/mozilla/javascript/Undefined", "instance", "Ljava/lang/Object;");
    }

    private void transform(ScriptNode scriptNode) {
        Codegen.initOptFunctions_r(scriptNode);
        int n = this.compilerEnv.getOptimizationLevel();
        HashMap hashMap = null;
        if (n > 0) {
            int n2 = scriptNode.getType();
            hashMap = null;
            if (n2 == 137) {
                int n3 = scriptNode.getFunctionCount();
                for (int i = 0; i != n3; ++i) {
                    String string2;
                    OptFunctionNode optFunctionNode = OptFunctionNode.get(scriptNode, i);
                    if (optFunctionNode.fnode.getFunctionType() != 1 || (string2 = optFunctionNode.fnode.getName()).length() == 0) continue;
                    if (hashMap == null) {
                        hashMap = new HashMap();
                    }
                    hashMap.put((Object)string2, (Object)optFunctionNode);
                }
            }
        }
        if (hashMap != null) {
            this.directCallTargets = new ObjArray();
        }
        new OptTransformer((Map<String, OptFunctionNode>)hashMap, this.directCallTargets).transform(scriptNode, this.compilerEnv);
        if (n > 0) {
            new Optimizer().optimize(scriptNode);
        }
    }

    @Override
    public void captureStackInfo(RhinoException rhinoException) {
        throw new UnsupportedOperationException();
    }

    String cleanName(ScriptNode scriptNode) {
        if (scriptNode instanceof FunctionNode) {
            Name name = ((FunctionNode)scriptNode).getFunctionName();
            String string2 = name == null ? "anonymous" : name.getIdentifier();
            return string2;
        }
        return "script";
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Object compile(CompilerEnvirons compilerEnvirons, ScriptNode scriptNode, String string2, boolean bl) {
        Object object;
        int n;
        Object object2 = object = globalLock;
        synchronized (object2) {
            globalSerialClassCounter = n = 1 + globalSerialClassCounter;
        }
        String string3 = "c";
        if (scriptNode.getSourceName().length() > 0 && !Character.isJavaIdentifierStart((char)(string3 = scriptNode.getSourceName().replaceAll("\\W", "_")).charAt(0))) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("_");
            stringBuilder.append(string3);
            string3 = stringBuilder.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("org.mozilla.javascript.gen.");
        stringBuilder.append(string3);
        stringBuilder.append("_");
        stringBuilder.append(n);
        String string4 = stringBuilder.toString();
        return new Object[]{string4, this.compileToClassFile(compilerEnvirons, string4, scriptNode, string2, bl)};
    }

    public byte[] compileToClassFile(CompilerEnvirons compilerEnvirons, String string2, ScriptNode scriptNode, String string3, boolean bl) {
        this.compilerEnv = compilerEnvirons;
        this.transform(scriptNode);
        if (bl) {
            scriptNode = scriptNode.getFunctionNode(0);
        }
        this.initScriptNodesData(scriptNode);
        this.mainClassName = string2;
        this.mainClassSignature = ClassFileWriter.classNameToSignature(string2);
        return this.generateCode(string3);
    }

    @Override
    public Function createFunctionObject(Context context, Scriptable scriptable, Object object, Object object2) {
        Class<?> class_ = this.defineClass(object, object2);
        try {
            Constructor constructor = class_.getConstructors()[0];
            Object[] arrobject = new Object[]{scriptable, context, 0};
            NativeFunction nativeFunction = (NativeFunction)constructor.newInstance(arrobject);
            return nativeFunction;
        }
        catch (Exception exception) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to instantiate compiled class:");
            stringBuilder.append(exception.toString());
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    @Override
    public Script createScriptObject(Object object, Object object2) {
        Class<?> class_ = this.defineClass(object, object2);
        try {
            Script script = (Script)class_.newInstance();
            return script;
        }
        catch (Exception exception) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to instantiate compiled class:");
            stringBuilder.append(exception.toString());
            throw new RuntimeException(stringBuilder.toString());
        }
    }

    String getBodyMethodName(ScriptNode scriptNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_c_");
        stringBuilder.append(this.cleanName(scriptNode));
        stringBuilder.append("_");
        stringBuilder.append(this.getIndex(scriptNode));
        return stringBuilder.toString();
    }

    String getBodyMethodSignature(ScriptNode scriptNode) {
        OptFunctionNode optFunctionNode;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        stringBuilder.append(this.mainClassSignature);
        stringBuilder.append("Lorg/mozilla/javascript/Context;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;");
        if (scriptNode.getType() == 110 && (optFunctionNode = OptFunctionNode.get(scriptNode)).isTargetOfDirectCall()) {
            int n = optFunctionNode.fnode.getParamCount();
            for (int i = 0; i != n; ++i) {
                stringBuilder.append("Ljava/lang/Object;D");
            }
        }
        stringBuilder.append("[Ljava/lang/Object;)Ljava/lang/Object;");
        return stringBuilder.toString();
    }

    String getCompiledRegexpName(ScriptNode scriptNode, int n) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_re");
        stringBuilder.append(this.getIndex(scriptNode));
        stringBuilder.append("_");
        stringBuilder.append(n);
        return stringBuilder.toString();
    }

    String getDirectCtorName(ScriptNode scriptNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_n");
        stringBuilder.append(this.getIndex(scriptNode));
        return stringBuilder.toString();
    }

    String getFunctionInitMethodName(OptFunctionNode optFunctionNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_i");
        stringBuilder.append(this.getIndex((ScriptNode)optFunctionNode.fnode));
        return stringBuilder.toString();
    }

    int getIndex(ScriptNode scriptNode) {
        return this.scriptOrFnIndexes.getExisting((Object)scriptNode);
    }

    @Override
    public String getPatchedStack(RhinoException rhinoException, String string2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getScriptStack(RhinoException rhinoException) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSourcePositionFromStack(Context context, int[] arrn) {
        throw new UnsupportedOperationException();
    }

    void pushNumberAsObject(ClassFileWriter classFileWriter, double d) {
        int n;
        if (d == 0.0) {
            if (1.0 / d > 0.0) {
                classFileWriter.add(178, "org/mozilla/javascript/optimizer/OptRuntime", "zeroObj", "Ljava/lang/Double;");
                return;
            }
            classFileWriter.addPush(d);
            Codegen.addDoubleWrap(classFileWriter);
            return;
        }
        if (d == 1.0) {
            classFileWriter.add(178, "org/mozilla/javascript/optimizer/OptRuntime", "oneObj", "Ljava/lang/Double;");
            return;
        }
        if (d == -1.0) {
            classFileWriter.add(178, "org/mozilla/javascript/optimizer/OptRuntime", "minusOneObj", "Ljava/lang/Double;");
            return;
        }
        if (d != d) {
            classFileWriter.add(178, "org/mozilla/javascript/ScriptRuntime", "NaNobj", "Ljava/lang/Double;");
            return;
        }
        if (this.itsConstantListSize >= 2000) {
            classFileWriter.addPush(d);
            Codegen.addDoubleWrap(classFileWriter);
            return;
        }
        int n2 = this.itsConstantListSize;
        if (n2 == 0) {
            this.itsConstantList = new double[64];
            n = 0;
        } else {
            double[] arrd = this.itsConstantList;
            for (n = 0; n != n2 && arrd[n] != d; ++n) {
            }
            if (n2 == arrd.length) {
                double[] arrd2 = new double[n2 * 2];
                System.arraycopy((Object)this.itsConstantList, (int)0, (Object)arrd2, (int)0, (int)n2);
                this.itsConstantList = arrd2;
            }
        }
        if (n == n2) {
            this.itsConstantList[n2] = d;
            this.itsConstantListSize = n2 + 1;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("_k");
        stringBuilder.append(n);
        String string2 = stringBuilder.toString();
        String string3 = Codegen.getStaticConstantWrapperType(d);
        classFileWriter.add(178, this.mainClassName, string2, string3);
    }

    @Override
    public void setEvalScriptFlag(Script script) {
        throw new UnsupportedOperationException();
    }

    public void setMainMethodClass(String string2) {
        this.mainMethodClass = string2;
    }
}

