/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  org.mozilla.javascript.ast.AstRoot
 *  org.mozilla.javascript.ast.FunctionNode
 *  org.mozilla.javascript.ast.ScriptNode
 */
package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.optimizer.Codegen;

public class ClassCompiler {
    private CompilerEnvirons compilerEnv;
    private String mainMethodClassName;
    private Class<?> targetExtends;
    private Class<?>[] targetImplements;

    public ClassCompiler(CompilerEnvirons compilerEnvirons) {
        if (compilerEnvirons != null) {
            this.compilerEnv = compilerEnvirons;
            this.mainMethodClassName = "org.mozilla.javascript.optimizer.OptRuntime";
            return;
        }
        throw new IllegalArgumentException();
    }

    public Object[] compileToClassFiles(String string2, String string3, int n, String string4) {
        AstRoot astRoot = new Parser(this.compilerEnv).parse(string2, string3, n);
        ScriptNode scriptNode = new IRFactory(this.compilerEnv).transformTree(astRoot);
        Class<?> class_ = this.getTargetExtends();
        Class<?>[] arrclass = this.getTargetImplements();
        boolean bl = arrclass == null && class_ == null;
        boolean bl2 = bl;
        String string5 = bl2 ? string4 : this.makeAuxiliaryClassName(string4, "1");
        Codegen codegen = new Codegen();
        codegen.setMainMethodClass(this.mainMethodClassName);
        CompilerEnvirons compilerEnvirons = this.compilerEnv;
        String string6 = scriptNode.getEncodedSource();
        String string7 = string5;
        String string8 = string5;
        byte[] arrby = codegen.compileToClassFile(compilerEnvirons, string7, scriptNode, string6, false);
        if (bl2) {
            return new Object[]{string8, arrby};
        }
        int n2 = scriptNode.getFunctionCount();
        ObjToIntMap objToIntMap = new ObjToIntMap(n2);
        for (int i = 0; i != n2; ++i) {
            FunctionNode functionNode = scriptNode.getFunctionNode(i);
            String string9 = functionNode.getName();
            if (string9 == null || string9.length() == 0) continue;
            objToIntMap.put(string9, functionNode.getParamCount());
        }
        if (class_ == null) {
            class_ = ScriptRuntime.ObjectClass;
        }
        return new Object[]{string4, JavaAdapter.createAdapterCode(objToIntMap, string4, class_, arrclass, string8), string8, arrby};
    }

    public CompilerEnvirons getCompilerEnv() {
        return this.compilerEnv;
    }

    public String getMainMethodClass() {
        return this.mainMethodClassName;
    }

    public Class<?> getTargetExtends() {
        return this.targetExtends;
    }

    public Class<?>[] getTargetImplements() {
        Class<?>[] arrclass = this.targetImplements;
        if (arrclass == null) {
            return null;
        }
        return (Class[])arrclass.clone();
    }

    protected String makeAuxiliaryClassName(String string2, String string3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        stringBuilder.append(string3);
        return stringBuilder.toString();
    }

    public void setMainMethodClass(String string2) {
        this.mainMethodClassName = string2;
    }

    public void setTargetExtends(Class<?> class_) {
        this.targetExtends = class_;
    }

    public void setTargetImplements(Class<?>[] arrclass) {
        Class[] arrclass2 = arrclass == null ? null : (Class[])arrclass.clone();
        this.targetImplements = arrclass2;
    }
}

