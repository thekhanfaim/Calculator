/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.PrintStream
 *  java.io.UnsupportedEncodingException
 *  java.lang.Boolean
 *  java.lang.Class
 *  java.lang.ClassLoader
 *  java.lang.ClassNotFoundException
 *  java.lang.IllegalAccessException
 *  java.lang.IllegalStateException
 *  java.lang.InstantiationException
 *  java.lang.Integer
 *  java.lang.LinkageError
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.SecurityException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Throwable
 *  java.lang.VirtualMachineError
 *  java.lang.ref.Reference
 *  java.lang.ref.ReferenceQueue
 *  java.lang.ref.SoftReference
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.nio.charset.Charset
 *  java.security.MessageDigest
 *  java.security.NoSuchAlgorithmException
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  org.mozilla.javascript.commonjs.module.ModuleScope
 *  org.mozilla.javascript.commonjs.module.Require
 *  org.mozilla.javascript.tools.shell.Global
 */
package org.mozilla.javascript.tools.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;
import org.mozilla.javascript.commonjs.module.ModuleScope;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.tools.SourceReader;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.QuitAction;
import org.mozilla.javascript.tools.shell.SecurityProxy;
import org.mozilla.javascript.tools.shell.ShellConsole;
import org.mozilla.javascript.tools.shell.ShellContextFactory;

public class Main {
    private static final int EXITCODE_FILE_NOT_FOUND = 4;
    private static final int EXITCODE_RUNTIME_ERROR = 3;
    protected static ToolErrorReporter errorReporter;
    protected static int exitCode;
    static List<String> fileList;
    public static Global global;
    static String mainModule;
    static List<String> modulePath;
    static boolean processStdin;
    static Require require;
    static boolean sandboxed;
    private static final ScriptCache scriptCache;
    private static SecurityProxy securityImpl;
    public static ShellContextFactory shellContextFactory;
    static boolean useRequire;

    static {
        shellContextFactory = new ShellContextFactory();
        global = new Global();
        exitCode = 0;
        processStdin = true;
        fileList = new ArrayList();
        sandboxed = false;
        useRequire = false;
        scriptCache = new ScriptCache(32);
        global.initQuitAction((QuitAction)new IProxy(3));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    static void evalInlineScript(Context context, String string2) {
        Script script = context.compileString(string2, "<command>", 1, null);
        if (script == null) return;
        try {
            script.exec(context, Main.getShellScope());
            return;
        }
        catch (VirtualMachineError virtualMachineError) {
            virtualMachineError.printStackTrace();
            Context.reportError(ToolErrorReporter.getMessage("msg.uncaughtJSException", virtualMachineError.toString()));
            exitCode = 3;
            return;
        }
        catch (RhinoException rhinoException) {
            ToolErrorReporter.reportException(context.getErrorReporter(), rhinoException);
            exitCode = 3;
        }
    }

    public static int exec(String[] arrstring) {
        ToolErrorReporter toolErrorReporter;
        errorReporter = toolErrorReporter = new ToolErrorReporter(false, global.getErr());
        shellContextFactory.setErrorReporter(toolErrorReporter);
        String[] arrstring2 = Main.processOptions(arrstring);
        int n = exitCode;
        if (n > 0) {
            return n;
        }
        if (processStdin) {
            fileList.add(null);
        }
        if (!Main.global.initialized) {
            global.init((ContextFactory)shellContextFactory);
        }
        IProxy iProxy = new IProxy(1);
        iProxy.args = arrstring2;
        shellContextFactory.call(iProxy);
        return exitCode;
    }

    private static byte[] getDigest(Object object) {
        if (object != null) {
            byte[] arrby;
            if (object instanceof String) {
                try {
                    arrby = ((String)object).getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException unsupportedEncodingException) {
                    arrby = ((String)object).getBytes();
                }
            } else {
                arrby = (byte[])object;
            }
            try {
                byte[] arrby2 = MessageDigest.getInstance((String)"MD5").digest(arrby);
                return arrby2;
            }
            catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                throw new RuntimeException((Throwable)noSuchAlgorithmException);
            }
        }
        return null;
    }

    public static PrintStream getErr() {
        return Main.getGlobal().getErr();
    }

    public static Global getGlobal() {
        return global;
    }

    public static InputStream getIn() {
        return Main.getGlobal().getIn();
    }

    public static PrintStream getOut() {
        return Main.getGlobal().getOut();
    }

    static Scriptable getScope(String string2) {
        if (useRequire) {
            URI uRI;
            if (string2 == null) {
                uRI = new File(System.getProperty((String)"user.dir")).toURI();
            } else if (SourceReader.toUrl(string2) != null) {
                try {
                    uRI = new URI(string2);
                }
                catch (URISyntaxException uRISyntaxException) {
                    uRI = new File(string2).toURI();
                }
            } else {
                uRI = new File(string2).toURI();
            }
            return new ModuleScope((Scriptable)global, uRI, null);
        }
        return global;
    }

    static Scriptable getShellScope() {
        return Main.getScope(null);
    }

    private static void initJavaPolicySecuritySupport() {
        Throwable throwable;
        try {
            SecurityProxy securityProxy;
            securityImpl = securityProxy = (SecurityProxy)Class.forName((String)"org.mozilla.javascript.tools.shell.JavaPolicySecurity").newInstance();
            SecurityController.initGlobal(securityProxy);
            return;
        }
        catch (LinkageError linkageError) {
            throwable = linkageError;
        }
        catch (InstantiationException instantiationException) {
            throwable = instantiationException;
        }
        catch (IllegalAccessException illegalAccessException) {
            throwable = illegalAccessException;
        }
        catch (ClassNotFoundException classNotFoundException) {
            throwable = classNotFoundException;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Can not load security support: ");
        stringBuilder.append((Object)throwable);
        throw new IllegalStateException(stringBuilder.toString(), throwable);
    }

    private static Script loadCompiledScript(Context context, String string2, byte[] arrby, Object object) throws FileNotFoundException {
        if (arrby != null) {
            int n = string2.lastIndexOf(47);
            int n2 = n < 0 ? 0 : n + 1;
            int n3 = string2.lastIndexOf(46);
            if (n3 < n2) {
                n3 = string2.length();
            }
            String string3 = string2.substring(n2, n3);
            try {
                GeneratedClassLoader generatedClassLoader = SecurityController.createLoader(context.getApplicationClassLoader(), object);
                Class<?> class_ = generatedClassLoader.defineClass(string3, arrby);
                generatedClassLoader.linkClass(class_);
                if (Script.class.isAssignableFrom(class_)) {
                    return (Script)class_.newInstance();
                }
                throw Context.reportRuntimeError("msg.must.implement.Script");
            }
            catch (InstantiationException instantiationException) {
                Context.reportError(instantiationException.toString());
                throw new RuntimeException((Throwable)instantiationException);
            }
            catch (IllegalAccessException illegalAccessException) {
                Context.reportError(illegalAccessException.toString());
                throw new RuntimeException((Throwable)illegalAccessException);
            }
        }
        throw new FileNotFoundException(string2);
    }

    public static void main(String[] arrstring) {
        try {
            if (Boolean.getBoolean((String)"rhino.use_java_policy_security")) {
                Main.initJavaPolicySecuritySupport();
            }
        }
        catch (SecurityException securityException) {
            securityException.printStackTrace(System.err);
        }
        int n = Main.exec(arrstring);
        if (n != 0) {
            System.exit((int)n);
        }
    }

    public static void processFile(Context context, Scriptable scriptable, String string2) throws IOException {
        SecurityProxy securityProxy = securityImpl;
        if (securityProxy == null) {
            Main.processFileSecure(context, scriptable, string2, null);
            return;
        }
        securityProxy.callProcessFileSecure(context, scriptable, string2);
    }

    public static void processFileNoThrow(Context context, Scriptable scriptable, String string2) {
        try {
            Main.processFile(context, scriptable, string2);
            return;
        }
        catch (VirtualMachineError virtualMachineError) {
            virtualMachineError.printStackTrace();
            Context.reportError(ToolErrorReporter.getMessage("msg.uncaughtJSException", virtualMachineError.toString()));
            exitCode = 3;
            return;
        }
        catch (RhinoException rhinoException) {
            ToolErrorReporter.reportException(context.getErrorReporter(), rhinoException);
            exitCode = 3;
            return;
        }
        catch (IOException iOException) {
            Context.reportError(ToolErrorReporter.getMessage("msg.couldnt.read.source", string2, iOException.getMessage()));
            exitCode = 4;
            return;
        }
    }

    static void processFileSecure(Context context, Scriptable scriptable, String string2, Object object) throws IOException {
        boolean bl = string2.endsWith(".class");
        Object object2 = Main.readFileOrUrl(string2, bl ^ true);
        byte[] arrby = Main.getDigest(object2);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string2);
        stringBuilder.append("_");
        stringBuilder.append(context.getOptimizationLevel());
        String string3 = stringBuilder.toString();
        ScriptReference scriptReference = scriptCache.get(string3, arrby);
        Script script = scriptReference != null ? (Script)scriptReference.get() : null;
        if (script == null) {
            if (bl) {
                script = Main.loadCompiledScript(context, string2, (byte[])object2, object);
            } else {
                String string4 = (String)object2;
                if (string4.length() > 0 && string4.charAt(0) == '#') {
                    for (int i = 1; i != string4.length(); ++i) {
                        char c = string4.charAt(i);
                        if (c != '\n' && c != '\r') {
                            continue;
                        }
                        string4 = string4.substring(i);
                        break;
                    }
                }
                script = context.compileString(string4, string2, 1, object);
            }
            scriptCache.put(string3, arrby, script);
        }
        if (script != null) {
            script.exec(context, scriptable);
        }
    }

    static void processFiles(Context context, String[] arrstring) {
        Object[] arrobject = new Object[arrstring.length];
        System.arraycopy((Object)arrstring, (int)0, (Object)arrobject, (int)0, (int)arrstring.length);
        Scriptable scriptable = context.newArray((Scriptable)global, arrobject);
        global.defineProperty("arguments", (Object)scriptable, 2);
        for (String string2 : fileList) {
            try {
                Main.processSource(context, string2);
            }
            catch (VirtualMachineError virtualMachineError) {
                virtualMachineError.printStackTrace();
                Context.reportError(ToolErrorReporter.getMessage("msg.uncaughtJSException", virtualMachineError.toString()));
                exitCode = 3;
            }
            catch (RhinoException rhinoException) {
                ToolErrorReporter.reportException(context.getErrorReporter(), rhinoException);
                exitCode = 3;
            }
            catch (IOException iOException) {
                Context.reportError(ToolErrorReporter.getMessage("msg.couldnt.read.source", string2, iOException.getMessage()));
                exitCode = 4;
            }
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public static String[] processOptions(String[] var0) {
        block42 : {
            var1_1 = 0;
            do {
                block46 : {
                    block45 : {
                        if (var1_1 == var0.length) {
                            return new String[0];
                        }
                        var2_2 = var0[var1_1];
                        if (!var2_2.startsWith("-")) {
                            Main.processStdin = false;
                            Main.fileList.add((Object)var2_2);
                            Main.mainModule = var2_2;
                            var15_7 = new String[var0.length - var1_1 - 1];
                            System.arraycopy((Object)var0, (int)(var1_1 + 1), (Object)var15_7, (int)0, (int)(var0.length - var1_1 - 1));
                            return var15_7;
                        }
                        if (!var2_2.equals((Object)"-version")) break block45;
                        if (++var1_1 == var0.length) {
                            var4_8 = var2_2;
                            break block42;
                        }
                        try {
                            var13_6 = Integer.parseInt((String)var0[var1_1]);
                        }
                        catch (NumberFormatException var12_9) {
                            var4_8 = var0[var1_1];
                            break block42;
                        }
                        if (!Context.isValidLanguageVersion(var13_6)) {
                            var4_8 = var0[var1_1];
                            break block42;
                        }
                        Main.shellContextFactory.setLanguageVersion(var13_6);
                        break block46;
                    }
                    if (var2_2.equals((Object)"-opt") || var2_2.equals((Object)"-O")) ** GOTO lbl97
                    if (var2_2.equals((Object)"-encoding")) {
                        if (++var1_1 == var0.length) {
                            var4_8 = var2_2;
                            break block42;
                        }
                        var11_5 = var0[var1_1];
                        Main.shellContextFactory.setCharacterEncoding(var11_5);
                    } else if (var2_2.equals((Object)"-strict")) {
                        Main.shellContextFactory.setStrictMode(true);
                        Main.shellContextFactory.setAllowReservedKeywords(false);
                        Main.errorReporter.setIsReportingWarnings(true);
                    } else if (var2_2.equals((Object)"-fatal-warnings")) {
                        Main.shellContextFactory.setWarningAsError(true);
                    } else if (var2_2.equals((Object)"-e")) {
                        Main.processStdin = false;
                        if (++var1_1 == var0.length) {
                            var4_8 = var2_2;
                            break block42;
                        }
                        if (!Main.global.initialized) {
                            Main.global.init((ContextFactory)Main.shellContextFactory);
                        }
                        var9_4 = new IProxy(2);
                        var9_4.scriptText = var0[var1_1];
                        Main.shellContextFactory.call(var9_4);
                    } else if (var2_2.equals((Object)"-require")) {
                        Main.useRequire = true;
                    } else if (var2_2.equals((Object)"-sandbox")) {
                        Main.sandboxed = true;
                        Main.useRequire = true;
                    } else if (var2_2.equals((Object)"-modules")) {
                        if (++var1_1 == var0.length) {
                            var4_8 = var2_2;
                            break block42;
                        }
                        if (Main.modulePath == null) {
                            Main.modulePath = new ArrayList();
                        }
                        Main.modulePath.add((Object)var0[var1_1]);
                        Main.useRequire = true;
                    } else if (var2_2.equals((Object)"-w")) {
                        Main.errorReporter.setIsReportingWarnings(true);
                    } else if (var2_2.equals((Object)"-f")) {
                        Main.processStdin = false;
                        if (++var1_1 == var0.length) {
                            var4_8 = var2_2;
                            break block42;
                        }
                        if (var0[var1_1].equals((Object)"-")) {
                            Main.fileList.add(null);
                        } else {
                            Main.fileList.add((Object)var0[var1_1]);
                            Main.mainModule = var0[var1_1];
                        }
                    } else if (var2_2.equals((Object)"-sealedlib")) {
                        Main.global.setSealedStdLib(true);
                    } else if (var2_2.equals((Object)"-debug")) {
                        Main.shellContextFactory.setGeneratingDebug(true);
                    } else {
                        block44 : {
                            block43 : {
                                if (!var2_2.equals((Object)"-?") && !var2_2.equals((Object)"-help")) {
                                    var4_8 = var2_2;
                                    break block42;
                                }
                                Main.global.getOut().println(ToolErrorReporter.getMessage("msg.shell.usage", Main.class.getName()));
                                Main.exitCode = 1;
                                return null;
lbl97: // 1 sources:
                                if (++var1_1 == var0.length) {
                                    var4_8 = var2_2;
                                    break block42;
                                }
                                var5_3 = Integer.parseInt((String)var0[var1_1]);
                                if (var5_3 != -2) break block43;
                                var5_3 = -1;
                                break block44;
                            }
                            if (!Context.isValidOptimizationLevel(var5_3)) {
                                var4_8 = var0[var1_1];
                                break block42;
                            }
                        }
                        Main.shellContextFactory.setOptimizationLevel(var5_3);
                    }
                }
                ++var1_1;
            } while (true);
            catch (NumberFormatException var3_10) {
                var4_8 = var0[var1_1];
            }
        }
        Main.global.getOut().println(ToolErrorReporter.getMessage("msg.shell.invalid", var4_8));
        Main.global.getOut().println(ToolErrorReporter.getMessage("msg.shell.usage", Main.class.getName()));
        Main.exitCode = 1;
        return null;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void processSource(Context var0, String var1_1) throws IOException {
        if (var1_1 != null && !var1_1.equals((Object)"-")) {
            if (Main.useRequire && var1_1.equals((Object)Main.mainModule)) {
                Main.require.requireMain(var0, var1_1);
                return;
            }
            Main.processFile(var0, Main.getScope(var1_1), var1_1);
            return;
        }
        var2_2 = Main.getShellScope();
        var3_3 = Main.shellContextFactory.getCharacterEncoding();
        var4_4 = var3_3 != null ? Charset.forName((String)var3_3) : Charset.defaultCharset();
        var5_5 = Main.global.getConsole(var4_4);
        if (var1_1 == null) {
            var5_5.println(var0.getImplementationVersion());
        }
        var6_6 = 1;
        var7_7 = false;
        do {
            block19 : {
                block18 : {
                    if (var7_7) {
                        var5_5.println();
                        var5_5.flush();
                        return;
                    }
                    var8_8 = Main.global.getPrompts(var0);
                    var9_9 = null;
                    if (var1_1 == null) {
                        var9_9 = var8_8[0];
                    }
                    var5_5.flush();
                    var10_10 = "";
                    var11_11 = var9_9;
                    var12_12 = var6_6;
                    do {
                        block17 : {
                            var21_21 = var5_5.readLine(var11_11);
                            if (var21_21 != null) break block17;
                            var7_7 = true;
                        }
                        var22_22 = new StringBuilder();
                        var22_22.append(var10_10);
                        var22_22.append(var21_21);
                        var22_22.append("\n");
                        var10_10 = var22_22.toString();
                        ++var12_12;
                        if (!var0.stringIsCompilableUnit(var10_10)) {
                            var11_11 = var8_8[1];
                            continue;
                        }
                        break block18;
                        break;
                    } while (true);
                    catch (IOException var13_13) {
                        var5_5.println(var13_13.toString());
                    }
                }
                try {
                    var16_16 = var0.compileString(var10_10, "<stdin>", var12_12, null);
                    if (var16_16 == null) break block19;
                    var17_17 = var16_16.exec(var0, var2_2);
                    if (var17_17 == Context.getUndefinedValue() || var17_17 instanceof Function && (var20_20 = var10_10.trim().startsWith("function"))) ** GOTO lbl60
                    try {
                        var5_5.println(Context.toString(var17_17));
                        ** GOTO lbl60
                    }
                    catch (RhinoException var19_19) {
                        try {
                            ToolErrorReporter.reportException(var0.getErrorReporter(), var19_19);
lbl60: // 3 sources:
                            var18_18 = Main.global.history;
                            var18_18.put((int)var18_18.getLength(), (Scriptable)var18_18, (Object)var10_10);
                        }
                        catch (RhinoException var14_14) {
                            ToolErrorReporter.reportException(var0.getErrorReporter(), var14_14);
                            Main.exitCode = 3;
                        }
                    }
                }
                catch (VirtualMachineError var15_15) {
                    var15_15.printStackTrace();
                    Context.reportError(ToolErrorReporter.getMessage("msg.uncaughtJSException", var15_15.toString()));
                    Main.exitCode = 3;
                }
            }
            var6_6 = var12_12;
        } while (true);
    }

    private static Object readFileOrUrl(String string2, boolean bl) throws IOException {
        return SourceReader.readFileOrUrl(string2, bl, shellContextFactory.getCharacterEncoding());
    }

    public static void setErr(PrintStream printStream) {
        Main.getGlobal().setErr(printStream);
    }

    public static void setIn(InputStream inputStream) {
        Main.getGlobal().setIn(inputStream);
    }

    public static void setOut(PrintStream printStream) {
        Main.getGlobal().setOut(printStream);
    }

    private static class IProxy
    implements ContextAction<Object>,
    QuitAction {
        private static final int EVAL_INLINE_SCRIPT = 2;
        private static final int PROCESS_FILES = 1;
        private static final int SYSTEM_EXIT = 3;
        String[] args;
        String scriptText;
        private int type;

        IProxy(int n) {
            this.type = n;
        }

        @Override
        public void quit(Context context, int n) {
            if (this.type == 3) {
                System.exit((int)n);
                return;
            }
            throw Kit.codeBug();
        }

        @Override
        public Object run(Context context) {
            block6 : {
                block5 : {
                    int n;
                    block4 : {
                        if (Main.useRequire) {
                            Main.require = Main.global.installRequire(context, Main.modulePath, Main.sandboxed);
                        }
                        if ((n = this.type) != 1) break block4;
                        Main.processFiles(context, this.args);
                        break block5;
                    }
                    if (n != 2) break block6;
                    Main.evalInlineScript(context, this.scriptText);
                }
                return null;
            }
            throw Kit.codeBug();
        }
    }

    static class ScriptCache
    extends LinkedHashMap<String, ScriptReference> {
        int capacity;
        ReferenceQueue<Script> queue;

        ScriptCache(int n) {
            super(n + 1, 2.0f, true);
            this.capacity = n;
            this.queue = new ReferenceQueue();
        }

        ScriptReference get(String string2, byte[] arrby) {
            ScriptReference scriptReference;
            while ((scriptReference = (ScriptReference)this.queue.poll()) != null) {
                this.remove((Object)scriptReference.path);
            }
            ScriptReference scriptReference2 = (ScriptReference)((Object)this.get((Object)string2));
            if (scriptReference2 != null && !Arrays.equals((byte[])arrby, (byte[])scriptReference2.digest)) {
                this.remove((Object)scriptReference2.path);
                scriptReference2 = null;
            }
            return scriptReference2;
        }

        void put(String string2, byte[] arrby, Script script) {
            this.put((Object)string2, (Object)new ScriptReference(string2, arrby, script, this.queue));
        }

        protected boolean removeEldestEntry(Map.Entry<String, ScriptReference> entry) {
            return this.size() > this.capacity;
        }
    }

    static class ScriptReference
    extends SoftReference<Script> {
        byte[] digest;
        String path;

        ScriptReference(String string2, byte[] arrby, Script script, ReferenceQueue<Script> referenceQueue) {
            super((Object)script, referenceQueue);
            this.path = string2;
            this.digest = arrby;
        }
    }

}

