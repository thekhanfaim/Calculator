/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.ThreadLocal
 *  java.lang.Throwable
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.concurrent.ConcurrentHashMap
 */
package org.mozilla.javascript.commonjs.module;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScope;
import org.mozilla.javascript.commonjs.module.ModuleScript;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;

public class Require
extends BaseFunction {
    private static final ThreadLocal<Map<String, Scriptable>> loadingModuleInterfaces = new ThreadLocal();
    private static final long serialVersionUID = 1L;
    private final Map<String, Scriptable> exportedModuleInterfaces = new ConcurrentHashMap();
    private final Object loadLock = new Object();
    private Scriptable mainExports;
    private String mainModuleId = null;
    private final ModuleScriptProvider moduleScriptProvider;
    private final Scriptable nativeScope;
    private final Scriptable paths;
    private final Script postExec;
    private final Script preExec;
    private final boolean sandboxed;

    public Require(Context context, Scriptable scriptable, ModuleScriptProvider moduleScriptProvider, Script script, Script script2, boolean bl) {
        this.moduleScriptProvider = moduleScriptProvider;
        this.nativeScope = scriptable;
        this.sandboxed = bl;
        this.preExec = script;
        this.postExec = script2;
        this.setPrototype(ScriptableObject.getFunctionPrototype(scriptable));
        if (!bl) {
            Scriptable scriptable2;
            this.paths = scriptable2 = context.newArray(scriptable, 0);
            Require.defineReadOnlyProperty(this, "paths", scriptable2);
            return;
        }
        this.paths = null;
    }

    private static void defineReadOnlyProperty(ScriptableObject scriptableObject, String string, Object object) {
        ScriptableObject.putProperty((Scriptable)scriptableObject, string, object);
        scriptableObject.setAttributes(string, 5);
    }

    private Scriptable executeModuleScript(Context context, String string, Scriptable scriptable, ModuleScript moduleScript, boolean bl) {
        ScriptableObject scriptableObject = (ScriptableObject)context.newObject(this.nativeScope);
        URI uRI = moduleScript.getUri();
        URI uRI2 = moduleScript.getBase();
        Require.defineReadOnlyProperty(scriptableObject, "id", string);
        if (!this.sandboxed) {
            Require.defineReadOnlyProperty(scriptableObject, "uri", uRI.toString());
        }
        ModuleScope moduleScope = new ModuleScope(this.nativeScope, uRI, uRI2);
        moduleScope.put("exports", (Scriptable)moduleScope, (Object)scriptable);
        moduleScope.put("module", (Scriptable)moduleScope, (Object)scriptableObject);
        scriptableObject.put("exports", (Scriptable)scriptableObject, (Object)scriptable);
        this.install(moduleScope);
        if (bl) {
            Require.defineReadOnlyProperty(this, "main", scriptableObject);
        }
        Require.executeOptionalScript(this.preExec, context, moduleScope);
        moduleScript.getScript().exec(context, moduleScope);
        Require.executeOptionalScript(this.postExec, context, moduleScope);
        return ScriptRuntime.toObject(context, this.nativeScope, ScriptableObject.getProperty((Scriptable)scriptableObject, "exports"));
    }

    private static void executeOptionalScript(Script script, Context context, Scriptable scriptable) {
        if (script != null) {
            script.exec(context, scriptable);
        }
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private Scriptable getExportedModuleInterface(Context context, String string, URI uRI, URI uRI2, boolean bl) {
        Map map;
        Throwable throwable52;
        void var10_24;
        Scriptable scriptable2;
        Scriptable scriptable;
        Object object;
        Scriptable scriptable3 = (Scriptable)this.exportedModuleInterfaces.get((Object)string);
        if (scriptable3 != null) {
            if (bl) throw new IllegalStateException("Attempt to set main module after it was loaded");
            return scriptable3;
        }
        ThreadLocal<Map<String, Scriptable>> threadLocal = loadingModuleInterfaces;
        Map map2 = (Map)threadLocal.get();
        if (map2 != null && (scriptable = (Scriptable)map2.get((Object)string)) != null) {
            return scriptable;
        }
        Object object2 = object = this.loadLock;
        // MONITORENTER : object2
        Scriptable scriptable4 = (Scriptable)this.exportedModuleInterfaces.get((Object)string);
        if (scriptable4 != null) {
            // MONITOREXIT : object2
            return scriptable4;
        }
        ModuleScript moduleScript = this.getModule(context, string, uRI, uRI2);
        if (this.sandboxed && !moduleScript.isSandboxed()) {
            Scriptable scriptable5 = this.nativeScope;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Module \"");
            stringBuilder.append(string);
            stringBuilder.append("\" is not contained in sandbox.");
            throw ScriptRuntime.throwError(context, scriptable5, stringBuilder.toString());
        }
        Scriptable scriptable6 = context.newObject(this.nativeScope);
        boolean bl2 = map2 == null;
        boolean bl3 = bl2;
        if (bl3) {
            try {
                HashMap hashMap = new HashMap();
                threadLocal.set((Object)hashMap);
                map = hashMap;
            }
            catch (Throwable throwable2) {
                throw var10_24;
            }
        } else {
            map = map2;
        }
        try {
            map.put((Object)string, (Object)scriptable6);
        }
        catch (Throwable throwable3) {}
        Scriptable scriptable7 = this.executeModuleScript(context, string, scriptable6, moduleScript, bl);
        if (scriptable6 != scriptable7) {
            map.put((Object)string, (Object)scriptable7);
            scriptable2 = scriptable7;
        } else {
            scriptable2 = scriptable6;
        }
        if (!bl3) return scriptable2;
        try {
            this.exportedModuleInterfaces.putAll(map);
            threadLocal.set(null);
            // MONITOREXIT : object2
            return scriptable2;
        }
        catch (Throwable throwable4) {
            throw var10_24;
        }
        {
            catch (Throwable throwable52) {
            }
            catch (RuntimeException runtimeException) {}
            {
                map.remove((Object)string);
                throw runtimeException;
            }
        }
        if (!bl3) throw throwable52;
        this.exportedModuleInterfaces.putAll(map);
        loadingModuleInterfaces.set(null);
        throw throwable52;
        throw var10_24;
    }

    private ModuleScript getModule(Context context, String string, URI uRI, URI uRI2) {
        block4 : {
            ModuleScript moduleScript = this.moduleScriptProvider.getModuleScript(context, string, uRI, uRI2, this.paths);
            if (moduleScript == null) break block4;
            return moduleScript;
        }
        try {
            Scriptable scriptable = this.nativeScope;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Module \"");
            stringBuilder.append(string);
            stringBuilder.append("\" not found.");
            throw ScriptRuntime.throwError(context, scriptable, stringBuilder.toString());
        }
        catch (Exception exception) {
            throw Context.throwAsScriptRuntimeEx(exception);
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
        URI uRI;
        String string;
        URI uRI2;
        if (arrobject == null) throw ScriptRuntime.throwError(context, scriptable, "require() needs one argument");
        if (arrobject.length < 1) throw ScriptRuntime.throwError(context, scriptable, "require() needs one argument");
        String string2 = (String)Context.jsToJava(arrobject[0], String.class);
        if (!string2.startsWith("./") && !string2.startsWith("../")) {
            string = string2;
            uRI = null;
            uRI2 = null;
            return this.getExportedModuleInterface(context, string, uRI, uRI2, false);
        }
        if (!(scriptable2 instanceof ModuleScope)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can't resolve relative module ID \"");
            stringBuilder.append(string2);
            stringBuilder.append("\" when require() is used outside of a module");
            throw ScriptRuntime.throwError(context, scriptable, stringBuilder.toString());
        }
        ModuleScope moduleScope = (ModuleScope)scriptable2;
        URI uRI3 = moduleScope.getBase();
        URI uRI4 = moduleScope.getUri();
        URI uRI5 = uRI4.resolve(string2);
        if (uRI3 == null) {
            string = uRI5.toString();
            uRI = uRI5;
            uRI2 = uRI3;
            return this.getExportedModuleInterface(context, string, uRI, uRI2, false);
        }
        String string3 = uRI3.relativize(uRI4).resolve(string2).toString();
        if (string3.charAt(0) != '.') {
            string = string3;
            uRI = uRI5;
            uRI2 = uRI3;
            return this.getExportedModuleInterface(context, string, uRI, uRI2, false);
        }
        if (!this.sandboxed) {
            string = uRI5.toString();
            uRI = uRI5;
            uRI2 = uRI3;
            return this.getExportedModuleInterface(context, string, uRI, uRI2, false);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Module \"");
        stringBuilder.append(string3);
        stringBuilder.append("\" is not contained in sandbox.");
        throw ScriptRuntime.throwError(context, scriptable, stringBuilder.toString());
    }

    @Override
    public Scriptable construct(Context context, Scriptable scriptable, Object[] arrobject) {
        throw ScriptRuntime.throwError(context, scriptable, "require() can not be invoked as a constructor");
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public String getFunctionName() {
        return "require";
    }

    @Override
    public int getLength() {
        return 1;
    }

    public void install(Scriptable scriptable) {
        ScriptableObject.putProperty(scriptable, "require", (Object)this);
    }

    public Scriptable requireMain(Context context, String string) {
        block12 : {
            block11 : {
                String string2 = this.mainModuleId;
                if (string2 != null) {
                    if (string2.equals((Object)string)) {
                        return this.mainExports;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Main module already set to ");
                    stringBuilder.append(this.mainModuleId);
                    throw new IllegalStateException(stringBuilder.toString());
                }
                try {
                    ModuleScript moduleScript = this.moduleScriptProvider.getModuleScript(context, string, null, null, this.paths);
                    if (moduleScript == null) break block11;
                }
                catch (Exception exception) {
                    throw new RuntimeException((Throwable)exception);
                }
                catch (RuntimeException runtimeException) {
                    throw runtimeException;
                }
                this.mainExports = this.getExportedModuleInterface(context, string, null, null, true);
                break block12;
            }
            if (!this.sandboxed) {
                URI uRI = null;
                try {
                    URI uRI2;
                    uRI = uRI2 = new URI(string);
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
                if (uRI == null || !uRI.isAbsolute()) {
                    File file = new File(string);
                    if (!file.isFile()) {
                        Scriptable scriptable = this.nativeScope;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Module \"");
                        stringBuilder.append(string);
                        stringBuilder.append("\" not found.");
                        throw ScriptRuntime.throwError(context, scriptable, stringBuilder.toString());
                    }
                    uRI = file.toURI();
                }
                this.mainExports = this.getExportedModuleInterface(context, uRI.toString(), uRI, null, true);
            }
        }
        this.mainModuleId = string;
        return this.mainExports;
    }
}

