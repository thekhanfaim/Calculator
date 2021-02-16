/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.PrintStream
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.SecurityException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.net.URL
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Set
 *  org.mozilla.javascript.ImporterTopLevel
 */
package org.mozilla.javascript.tools.debugger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeCall;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.SecurityUtilities;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableObject;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.tools.debugger.GuiCallback;
import org.mozilla.javascript.tools.debugger.ScopeProvider;
import org.mozilla.javascript.tools.debugger.SourceProvider;

public class Dim {
    public static final int BREAK = 4;
    public static final int EXIT = 5;
    public static final int GO = 3;
    private static final int IPROXY_COMPILE_SCRIPT = 2;
    private static final int IPROXY_DEBUG = 0;
    private static final int IPROXY_EVAL_SCRIPT = 3;
    private static final int IPROXY_LISTEN = 1;
    private static final int IPROXY_OBJECT_IDS = 7;
    private static final int IPROXY_OBJECT_PROPERTY = 6;
    private static final int IPROXY_OBJECT_TO_STRING = 5;
    private static final int IPROXY_STRING_IS_COMPILABLE = 4;
    public static final int STEP_INTO = 1;
    public static final int STEP_OUT = 2;
    public static final int STEP_OVER;
    private boolean breakFlag;
    private boolean breakOnEnter;
    private boolean breakOnExceptions;
    private boolean breakOnReturn;
    private GuiCallback callback;
    private ContextFactory contextFactory;
    private StackFrame evalFrame;
    private String evalRequest;
    private String evalResult;
    private Object eventThreadMonitor = new Object();
    private int frameIndex = -1;
    private final Map<String, FunctionSource> functionNames = Collections.synchronizedMap((Map)new HashMap());
    private final Map<DebuggableScript, FunctionSource> functionToSource = Collections.synchronizedMap((Map)new HashMap());
    private boolean insideInterruptLoop;
    private volatile ContextData interruptedContextData;
    private DimIProxy listener;
    private Object monitor = new Object();
    private volatile int returnValue = -1;
    private ScopeProvider scopeProvider;
    private SourceProvider sourceProvider;
    private final Map<String, SourceInfo> urlToSourceInfo = Collections.synchronizedMap((Map)new HashMap());

    private static void collectFunctions_r(DebuggableScript debuggableScript, ObjArray objArray) {
        objArray.add(debuggableScript);
        for (int i = 0; i != debuggableScript.getFunctionCount(); ++i) {
            Dim.collectFunctions_r(debuggableScript.getFunction(i), objArray);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String do_eval(Context context, StackFrame stackFrame, String string2) {
        String string3 = "";
        Debugger debugger = context.getDebugger();
        Object object = context.getDebuggerContextData();
        int n = context.getOptimizationLevel();
        context.setDebugger(null, null);
        context.setOptimizationLevel(-1);
        context.setGeneratingDebug(false);
        try {
            try {
                Object object2 = ((Callable)((Object)context.compileString(string2, string3, 0, null))).call(context, stackFrame.scope, stackFrame.thisObj, ScriptRuntime.emptyArgs);
                if (object2 != Undefined.instance) {
                    string3 = ScriptRuntime.toString(object2);
                }
            }
            catch (Exception exception) {
                String string4;
                string3 = string4 = exception.getMessage();
            }
        }
        catch (Throwable throwable2) {}
        context.setGeneratingDebug(true);
        context.setOptimizationLevel(n);
        context.setDebugger(debugger, object);
        if (string3 != null) return string3;
        return "null";
        context.setGeneratingDebug(true);
        context.setOptimizationLevel(n);
        context.setDebugger(debugger, object);
        throw throwable2;
    }

    private FunctionSource functionSource(DebuggableScript debuggableScript) {
        return (FunctionSource)this.functionToSource.get((Object)debuggableScript);
    }

    private static DebuggableScript[] getAllFunctions(DebuggableScript debuggableScript) {
        ObjArray objArray = new ObjArray();
        Dim.collectFunctions_r(debuggableScript, objArray);
        Object[] arrobject = new DebuggableScript[objArray.size()];
        objArray.toArray(arrobject);
        return arrobject;
    }

    private FunctionSource getFunctionSource(DebuggableScript debuggableScript) {
        String string2;
        String string3;
        FunctionSource functionSource = this.functionSource(debuggableScript);
        if (functionSource == null && this.sourceInfo(string2 = this.getNormalizedUrl(debuggableScript)) == null && !debuggableScript.isGeneratedScript() && (string3 = this.loadSource(string2)) != null) {
            DebuggableScript debuggableScript2 = debuggableScript;
            do {
                DebuggableScript debuggableScript3;
                if ((debuggableScript3 = debuggableScript2.getParent()) == null) {
                    this.registerTopScript(debuggableScript2, string3);
                    return this.functionSource(debuggableScript);
                }
                debuggableScript2 = debuggableScript3;
            } while (true);
        }
        return functionSource;
    }

    private String getNormalizedUrl(DebuggableScript debuggableScript) {
        String string2 = debuggableScript.getSourceName();
        if (string2 == null) {
            return "<stdin>";
        }
        StringBuilder stringBuilder = null;
        int n = string2.length();
        int n2 = 0;
        do {
            String string3;
            int n3;
            block12 : {
                block11 : {
                    char c;
                    int n4;
                    if ((n3 = string2.indexOf(35, n2)) < 0) break block11;
                    for (n4 = n3 + 1; n4 != n && '0' <= (c = string2.charAt(n4)) && c <= '9'; ++n4) {
                    }
                    int n5 = n3 + 1;
                    string3 = null;
                    if (n4 != n5) {
                        boolean bl = "(eval)".regionMatches(0, string2, n4, 6);
                        string3 = null;
                        if (bl) {
                            n2 = n4 + 6;
                            string3 = "(eval)";
                        }
                    }
                    if (string3 != null) break block12;
                }
                if (stringBuilder != null) {
                    if (n2 != n) {
                        stringBuilder.append(string2.substring(n2));
                    }
                    string2 = stringBuilder.toString();
                }
                return string2;
            }
            if (stringBuilder == null) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(string2.substring(0, n3));
            }
            stringBuilder.append(string3);
        } while (true);
    }

    private Object[] getObjectIdsImpl(Context context, Object object) {
        if (object instanceof Scriptable && object != Undefined.instance) {
            Scriptable scriptable = (Scriptable)object;
            Object[] arrobject = scriptable instanceof DebuggableObject ? ((DebuggableObject)((Object)scriptable)).getAllIds() : scriptable.getIds();
            Scriptable scriptable2 = scriptable.getPrototype();
            Scriptable scriptable3 = scriptable.getParentScope();
            int n = 0;
            if (scriptable2 != null) {
                n = 0 + 1;
            }
            if (scriptable3 != null) {
                ++n;
            }
            if (n != 0) {
                Object[] arrobject2 = new Object[n + arrobject.length];
                System.arraycopy((Object)arrobject, (int)0, (Object)arrobject2, (int)n, (int)arrobject.length);
                arrobject = arrobject2;
                int n2 = 0;
                if (scriptable2 != null) {
                    int n3 = 0 + 1;
                    arrobject[0] = "__proto__";
                    n2 = n3;
                }
                if (scriptable3 != null) {
                    n2 + 1;
                    arrobject[n2] = "__parent__";
                }
            }
            return arrobject;
        }
        return Context.emptyArgs;
    }

    private Object getObjectPropertyImpl(Context context, Object object, Object object2) {
        Scriptable scriptable = (Scriptable)object;
        if (object2 instanceof String) {
            Object object3;
            String string2 = (String)object2;
            if (string2.equals((Object)"this")) {
                object3 = scriptable;
            } else if (string2.equals((Object)"__proto__")) {
                object3 = scriptable.getPrototype();
            } else if (string2.equals((Object)"__parent__")) {
                object3 = scriptable.getParentScope();
            } else {
                object3 = ScriptableObject.getProperty(scriptable, string2);
                if (object3 == ScriptableObject.NOT_FOUND) {
                    object3 = Undefined.instance;
                }
            }
            return object3;
        }
        Object object4 = ScriptableObject.getProperty(scriptable, (Integer)object2);
        if (object4 == ScriptableObject.NOT_FOUND) {
            object4 = Undefined.instance;
        }
        return object4;
    }

    private void handleBreakpointHit(StackFrame stackFrame, Context context) {
        this.breakFlag = false;
        this.interrupted(context, stackFrame, null);
    }

    private void handleExceptionThrown(Context context, Throwable throwable, StackFrame stackFrame) {
        ContextData contextData;
        if (this.breakOnExceptions && (contextData = stackFrame.contextData()).lastProcessedException != throwable) {
            this.interrupted(context, stackFrame, throwable);
            contextData.lastProcessedException = throwable;
        }
    }

    /*
     * Exception decompiling
     */
    private void interrupted(Context var1_1, StackFrame var2_2, Throwable var3_3) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [33[UNCONDITIONALDOLOOP]], but top level block is 8[TRYBLOCK]
        // org.benf.cfr.reader.b.a.a.j.a(Op04StructuredStatement.java:432)
        // org.benf.cfr.reader.b.a.a.j.d(Op04StructuredStatement.java:484)
        // org.benf.cfr.reader.b.a.a.i.a(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.b.f.a(CodeAnalyser.java:692)
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

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private String loadSource(String string2) {
        String string4;
        InputStream inputStream;
        String string3;
        block15 : {
            string4 = null;
            int n = string2.indexOf(35);
            if (n >= 0) {
                string2 = string2.substring(0, n);
            }
            try {
                InputStream inputStream2;
                int n2 = string2.indexOf(58);
                if (n2 < 0) {
                    block16 : {
                        try {
                            String string5;
                            if (string2.startsWith("~/") && (string5 = SecurityUtilities.getSystemProperty("user.home")) != null) {
                                String string6 = string2.substring(2);
                                File file = new File(new File(string5), string6);
                                boolean bl = file.exists();
                                string4 = null;
                                if (bl) {
                                    inputStream = new FileInputStream(file);
                                    break block15;
                                }
                            }
                            File file = new File(string2);
                            boolean bl = file.exists();
                            string4 = null;
                            if (!bl) break block16;
                            FileInputStream fileInputStream = new FileInputStream(file);
                            inputStream = fileInputStream;
                            break block15;
                        }
                        catch (SecurityException securityException) {
                            // empty catch block
                        }
                    }
                    boolean bl = string2.startsWith("//");
                    string4 = null;
                    if (bl) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("http:");
                        stringBuilder.append(string2);
                        string2 = stringBuilder.toString();
                    } else {
                        boolean bl2 = string2.startsWith("/");
                        string4 = null;
                        if (bl2) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("http://127.0.0.1");
                            stringBuilder.append(string2);
                            string2 = stringBuilder.toString();
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("http://");
                            stringBuilder.append(string2);
                            string2 = stringBuilder.toString();
                        }
                    }
                }
                inputStream = inputStream2 = new URL(string2).openStream();
            }
            catch (IOException iOException) {
                PrintStream printStream = System.err;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed to load source from ");
                stringBuilder.append(string2);
                stringBuilder.append(": ");
                stringBuilder.append((Object)iOException);
                printStream.println(stringBuilder.toString());
                return string4;
            }
        }
        string4 = string3 = Kit.readReader((Reader)new InputStreamReader(inputStream));
        {
            catch (Throwable throwable) {
                inputStream.close();
                throw throwable;
            }
        }
        inputStream.close();
        return string4;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void registerTopScript(DebuggableScript debuggableScript, String string2) {
        Map<String, SourceInfo> map;
        String string3;
        if (!debuggableScript.isTopLevel()) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException();
            throw illegalArgumentException;
        }
        String string4 = this.getNormalizedUrl(debuggableScript);
        DebuggableScript[] arrdebuggableScript = Dim.getAllFunctions(debuggableScript);
        SourceProvider sourceProvider = this.sourceProvider;
        if (sourceProvider == null || (string3 = sourceProvider.getSource(debuggableScript)) == null) {
            string3 = string2;
        }
        SourceInfo sourceInfo = new SourceInfo(string3, arrdebuggableScript, string4);
        Map<String, SourceInfo> map2 = map = this.urlToSourceInfo;
        synchronized (map2) {
            SourceInfo sourceInfo2 = (SourceInfo)this.urlToSourceInfo.get((Object)string4);
            if (sourceInfo2 != null) {
                sourceInfo.copyBreakpointsFrom(sourceInfo2);
            }
            this.urlToSourceInfo.put((Object)string4, (Object)sourceInfo);
            int n = 0;
            do {
                if (n != sourceInfo.functionSourcesTop()) {
                    FunctionSource functionSource = sourceInfo.functionSource(n);
                    String string5 = functionSource.name();
                    if (string5.length() != 0) {
                        this.functionNames.put((Object)string5, (Object)functionSource);
                    }
                } else {
                    Map<DebuggableScript, FunctionSource> map3;
                    // MONITOREXIT [5, 7, 14] lbl25 : w: MONITOREXIT : var23_9
                    Map<DebuggableScript, FunctionSource> map4 = map3 = this.functionToSource;
                    synchronized (map4) {
                        int n2 = 0;
                        do {
                            if (n2 == arrdebuggableScript.length) {
                                // MONITOREXIT [5, 6, 7, 8, 9, 14] lbl31 : w: MONITOREXIT : var24_15
                                this.callback.updateSourceText(sourceInfo);
                                return;
                            }
                            FunctionSource functionSource = sourceInfo.functionSource(n2);
                            this.functionToSource.put((Object)arrdebuggableScript[n2], (Object)functionSource);
                            ++n2;
                        } while (true);
                    }
                }
                ++n;
            } while (true);
        }
    }

    public void attachTo(ContextFactory contextFactory) {
        DimIProxy dimIProxy;
        this.detach();
        this.contextFactory = contextFactory;
        this.listener = dimIProxy = new DimIProxy(this, 1);
        contextFactory.addListener(dimIProxy);
    }

    public void clearAllBreakpoints() {
        Iterator iterator = this.urlToSourceInfo.values().iterator();
        while (iterator.hasNext()) {
            ((SourceInfo)iterator.next()).removeAllBreakpoints();
        }
    }

    public void compileScript(String string2, String string3) {
        DimIProxy dimIProxy = new DimIProxy(this, 2);
        dimIProxy.url = string2;
        dimIProxy.text = string3;
        dimIProxy.withContext();
    }

    public void contextSwitch(int n) {
        this.frameIndex = n;
    }

    public ContextData currentContextData() {
        return this.interruptedContextData;
    }

    public void detach() {
        DimIProxy dimIProxy = this.listener;
        if (dimIProxy != null) {
            this.contextFactory.removeListener(dimIProxy);
            this.contextFactory = null;
            this.listener = null;
        }
    }

    public void dispose() {
        this.detach();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String eval(String string2) {
        Object object;
        String string3 = "undefined";
        if (string2 == null) {
            return string3;
        }
        ContextData contextData = this.currentContextData();
        if (contextData == null) {
            return string3;
        }
        if (this.frameIndex >= contextData.frameCount()) {
            return string3;
        }
        StackFrame stackFrame = contextData.getFrame(this.frameIndex);
        if (contextData.eventThreadFlag) {
            return Dim.do_eval(Context.getCurrentContext(), stackFrame, string2);
        }
        Object object2 = object = this.monitor;
        synchronized (object2) {
            if (!this.insideInterruptLoop) return string3;
            this.evalRequest = string2;
            this.evalFrame = stackFrame;
            this.monitor.notify();
            try {
                do {
                    this.monitor.wait();
                } while (this.evalRequest != null);
                return this.evalResult;
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            return this.evalResult;
        }
    }

    public void evalScript(String string2, String string3) {
        DimIProxy dimIProxy = new DimIProxy(this, 3);
        dimIProxy.url = string2;
        dimIProxy.text = string3;
        dimIProxy.withContext();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String[] functionNames() {
        Map<String, SourceInfo> map;
        Map<String, SourceInfo> map2 = map = this.urlToSourceInfo;
        synchronized (map2) {
            return (String[])this.functionNames.keySet().toArray((Object[])new String[this.functionNames.size()]);
        }
    }

    public FunctionSource functionSourceByName(String string2) {
        return (FunctionSource)this.functionNames.get((Object)string2);
    }

    public Object[] getObjectIds(Object object) {
        DimIProxy dimIProxy = new DimIProxy(this, 7);
        dimIProxy.object = object;
        dimIProxy.withContext();
        return dimIProxy.objectArrayResult;
    }

    public Object getObjectProperty(Object object, Object object2) {
        DimIProxy dimIProxy = new DimIProxy(this, 6);
        dimIProxy.object = object;
        dimIProxy.id = object2;
        dimIProxy.withContext();
        return dimIProxy.objectResult;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void go() {
        Object object;
        Object object2 = object = this.monitor;
        synchronized (object2) {
            this.returnValue = 3;
            this.monitor.notifyAll();
            return;
        }
    }

    public String objectToString(Object object) {
        DimIProxy dimIProxy = new DimIProxy(this, 5);
        dimIProxy.object = object;
        dimIProxy.withContext();
        return dimIProxy.stringResult;
    }

    public void setBreak() {
        this.breakFlag = true;
    }

    public void setBreakOnEnter(boolean bl) {
        this.breakOnEnter = bl;
    }

    public void setBreakOnExceptions(boolean bl) {
        this.breakOnExceptions = bl;
    }

    public void setBreakOnReturn(boolean bl) {
        this.breakOnReturn = bl;
    }

    public void setGuiCallback(GuiCallback guiCallback) {
        this.callback = guiCallback;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void setReturnValue(int n) {
        Object object;
        Object object2 = object = this.monitor;
        synchronized (object2) {
            this.returnValue = n;
            this.monitor.notify();
            return;
        }
    }

    public void setScopeProvider(ScopeProvider scopeProvider) {
        this.scopeProvider = scopeProvider;
    }

    public void setSourceProvider(SourceProvider sourceProvider) {
        this.sourceProvider = sourceProvider;
    }

    public SourceInfo sourceInfo(String string2) {
        return (SourceInfo)this.urlToSourceInfo.get((Object)string2);
    }

    public boolean stringIsCompilableUnit(String string2) {
        DimIProxy dimIProxy = new DimIProxy(this, 4);
        dimIProxy.text = string2;
        dimIProxy.withContext();
        return dimIProxy.booleanResult;
    }

    public static class ContextData {
        private boolean breakNextLine;
        private boolean eventThreadFlag;
        private ObjArray frameStack = new ObjArray();
        private Throwable lastProcessedException;
        private int stopAtFrameDepth = -1;

        static /* synthetic */ boolean access$402(ContextData contextData, boolean bl) {
            contextData.eventThreadFlag = bl;
            return bl;
        }

        public static ContextData get(Context context) {
            return (ContextData)context.getDebuggerContextData();
        }

        private void popFrame() {
            this.frameStack.pop();
        }

        private void pushFrame(StackFrame stackFrame) {
            this.frameStack.push(stackFrame);
        }

        public int frameCount() {
            return this.frameStack.size();
        }

        public StackFrame getFrame(int n) {
            int n2 = -1 + (this.frameStack.size() - n);
            return (StackFrame)this.frameStack.get(n2);
        }
    }

    private static class DimIProxy
    implements ContextAction,
    ContextFactory.Listener,
    Debugger {
        private boolean booleanResult;
        private Dim dim;
        private Object id;
        private Object object;
        private Object[] objectArrayResult;
        private Object objectResult;
        private String stringResult;
        private String text;
        private int type;
        private String url;

        private DimIProxy(Dim dim, int n) {
            this.dim = dim;
            this.type = n;
        }

        private void withContext() {
            this.dim.contextFactory.call(this);
        }

        @Override
        public void contextCreated(Context context) {
            if (this.type != 1) {
                Kit.codeBug();
            }
            ContextData contextData = new ContextData();
            context.setDebugger(new DimIProxy(this.dim, 0), contextData);
            context.setGeneratingDebug(true);
            context.setOptimizationLevel(-1);
        }

        @Override
        public void contextReleased(Context context) {
            if (this.type != 1) {
                Kit.codeBug();
            }
        }

        @Override
        public DebugFrame getFrame(Context context, DebuggableScript debuggableScript) {
            FunctionSource functionSource;
            if (this.type != 0) {
                Kit.codeBug();
            }
            if ((functionSource = this.dim.getFunctionSource(debuggableScript)) == null) {
                return null;
            }
            return new StackFrame(context, this.dim, functionSource);
        }

        @Override
        public void handleCompilationDone(Context context, DebuggableScript debuggableScript, String string2) {
            if (this.type != 0) {
                Kit.codeBug();
            }
            if (!debuggableScript.isTopLevel()) {
                return;
            }
            this.dim.registerTopScript(debuggableScript, string2);
        }

        public Object run(Context context) {
            switch (this.type) {
                default: {
                    throw Kit.codeBug();
                }
                case 7: {
                    this.objectArrayResult = this.dim.getObjectIdsImpl(context, this.object);
                    return null;
                }
                case 6: {
                    this.objectResult = this.dim.getObjectPropertyImpl(context, this.object, this.id);
                    return null;
                }
                case 5: {
                    if (this.object == Undefined.instance) {
                        this.stringResult = "undefined";
                        return null;
                    }
                    Object object = this.object;
                    if (object == null) {
                        this.stringResult = "null";
                        return null;
                    }
                    if (object instanceof NativeCall) {
                        this.stringResult = "[object Call]";
                        return null;
                    }
                    this.stringResult = Context.toString(object);
                    return null;
                }
                case 4: {
                    this.booleanResult = context.stringIsCompilableUnit(this.text);
                    return null;
                }
                case 3: {
                    ScopeProvider scopeProvider = this.dim.scopeProvider;
                    Scriptable scriptable = null;
                    if (scopeProvider != null) {
                        scriptable = this.dim.scopeProvider.getScope();
                    }
                    if (scriptable == null) {
                        scriptable = new ImporterTopLevel(context);
                    }
                    String string2 = this.text;
                    String string3 = this.url;
                    context.evaluateString(scriptable, string2, string3, 1, null);
                    return null;
                }
                case 2: 
            }
            context.compileString(this.text, this.url, 1, null);
            return null;
        }
    }

    public static class FunctionSource {
        private int firstLine;
        private String name;
        private SourceInfo sourceInfo;

        private FunctionSource(SourceInfo sourceInfo, int n, String string2) {
            if (string2 != null) {
                this.sourceInfo = sourceInfo;
                this.firstLine = n;
                this.name = string2;
                return;
            }
            throw new IllegalArgumentException();
        }

        public int firstLine() {
            return this.firstLine;
        }

        public String name() {
            return this.name;
        }

        public SourceInfo sourceInfo() {
            return this.sourceInfo;
        }
    }

    public static class SourceInfo {
        private static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
        private boolean[] breakableLines;
        private boolean[] breakpoints;
        private FunctionSource[] functionSources;
        private String source;
        private String url;

        private SourceInfo(String string2, DebuggableScript[] arrdebuggableScript, String string3) {
            IllegalStateException illegalStateException;
            int n;
            block14 : {
                int[] arrn;
                int n2;
                block13 : {
                    int n3;
                    int[][] arrarrn;
                    block12 : {
                        this.source = string2;
                        this.url = string3;
                        n2 = arrdebuggableScript.length;
                        arrarrn = new int[n2][];
                        for (int i = 0; i != n2; ++i) {
                            arrarrn[i] = arrdebuggableScript[i].getLineNumbers();
                        }
                        n = 0;
                        n3 = -1;
                        arrn = new int[n2];
                        for (int i = 0; i != n2; ++i) {
                            int[] arrn2 = arrarrn[i];
                            if (arrn2 != null && arrn2.length != 0) {
                                int n4;
                                int n5 = n4 = arrn2[0];
                                for (int j = 1; j != arrn2.length; ++j) {
                                    int n6 = arrn2[j];
                                    if (n6 < n4) {
                                        n4 = n6;
                                        continue;
                                    }
                                    if (n6 <= n5) continue;
                                    n5 = n6;
                                }
                                arrn[i] = n4;
                                if (n > n3) {
                                    n = n4;
                                    n3 = n5;
                                    continue;
                                }
                                if (n4 < n) {
                                    n = n4;
                                }
                                if (n5 <= n3) continue;
                                n3 = n5;
                                continue;
                            }
                            arrn[i] = -1;
                        }
                        if (n <= n3) break block12;
                        boolean[] arrbl = EMPTY_BOOLEAN_ARRAY;
                        this.breakableLines = arrbl;
                        this.breakpoints = arrbl;
                        break block13;
                    }
                    if (n >= 0) {
                        int n7 = n3 + 1;
                        this.breakableLines = new boolean[n7];
                        this.breakpoints = new boolean[n7];
                        for (int i = 0; i != n2; ++i) {
                            int[] arrn3 = arrarrn[i];
                            if (arrn3 == null || arrn3.length == 0) continue;
                            for (int j = 0; j != arrn3.length; ++j) {
                                int n8 = arrn3[j];
                                this.breakableLines[n8] = true;
                            }
                        }
                    }
                    break block14;
                }
                this.functionSources = new FunctionSource[n2];
                for (int i = 0; i != n2; ++i) {
                    String string4 = arrdebuggableScript[i].getFunctionName();
                    if (string4 == null) {
                        string4 = "";
                    }
                    this.functionSources[i] = new FunctionSource(this, arrn[i], string4);
                }
                return;
            }
            illegalStateException = new IllegalStateException(String.valueOf((int)n));
            throw illegalStateException;
        }

        private void copyBreakpointsFrom(SourceInfo sourceInfo) {
            int n = sourceInfo.breakpoints.length;
            boolean[] arrbl = this.breakpoints;
            if (n > arrbl.length) {
                n = arrbl.length;
            }
            for (int i = 0; i != n; ++i) {
                if (!sourceInfo.breakpoints[i]) continue;
                this.breakpoints[i] = true;
            }
        }

        public boolean breakableLine(int n) {
            boolean[] arrbl = this.breakableLines;
            return n < arrbl.length && arrbl[n];
        }

        public boolean breakpoint(int n) {
            if (this.breakableLine(n)) {
                boolean[] arrbl = this.breakpoints;
                return n < arrbl.length && arrbl[n];
            }
            throw new IllegalArgumentException(String.valueOf((int)n));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public boolean breakpoint(int n, boolean bl) {
            boolean[] arrbl;
            if (!this.breakableLine(n)) {
                throw new IllegalArgumentException(String.valueOf((int)n));
            }
            boolean[] arrbl2 = arrbl = this.breakpoints;
            synchronized (arrbl2) {
                boolean[] arrbl3 = this.breakpoints;
                if (arrbl3[n] == bl) return false;
                arrbl3[n] = bl;
                return true;
            }
        }

        public FunctionSource functionSource(int n) {
            return this.functionSources[n];
        }

        public int functionSourcesTop() {
            return this.functionSources.length;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void removeAllBreakpoints() {
            boolean[] arrbl;
            boolean[] arrbl2 = arrbl = this.breakpoints;
            synchronized (arrbl2) {
                boolean[] arrbl3;
                for (int i = 0; i != (arrbl3 = this.breakpoints).length; ++i) {
                    arrbl3[i] = false;
                }
                return;
            }
        }

        public String source() {
            return this.source;
        }

        public String url() {
            return this.url;
        }
    }

    public static class StackFrame
    implements DebugFrame {
        private boolean[] breakpoints;
        private ContextData contextData;
        private Dim dim;
        private FunctionSource fsource;
        private int lineNumber;
        private Scriptable scope;
        private Scriptable thisObj;

        private StackFrame(Context context, Dim dim, FunctionSource functionSource) {
            this.dim = dim;
            this.contextData = ContextData.get(context);
            this.fsource = functionSource;
            this.breakpoints = functionSource.sourceInfo().breakpoints;
            this.lineNumber = functionSource.firstLine();
        }

        public ContextData contextData() {
            return this.contextData;
        }

        public String getFunctionName() {
            return this.fsource.name();
        }

        public int getLineNumber() {
            return this.lineNumber;
        }

        public String getUrl() {
            return this.fsource.sourceInfo().url();
        }

        @Override
        public void onDebuggerStatement(Context context) {
            this.dim.handleBreakpointHit(this, context);
        }

        @Override
        public void onEnter(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] arrobject) {
            this.contextData.pushFrame(this);
            this.scope = scriptable;
            this.thisObj = scriptable2;
            if (this.dim.breakOnEnter) {
                this.dim.handleBreakpointHit(this, context);
            }
        }

        @Override
        public void onExceptionThrown(Context context, Throwable throwable) {
            this.dim.handleExceptionThrown(context, throwable, this);
        }

        @Override
        public void onExit(Context context, boolean bl, Object object) {
            if (this.dim.breakOnReturn && !bl) {
                this.dim.handleBreakpointHit(this, context);
            }
            this.contextData.popFrame();
        }

        @Override
        public void onLineChange(Context context, int n) {
            this.lineNumber = n;
            if (!this.breakpoints[n] && !this.dim.breakFlag) {
                boolean bl = this.contextData.breakNextLine;
                if (bl && this.contextData.stopAtFrameDepth >= 0) {
                    boolean bl2 = this.contextData.frameCount() <= this.contextData.stopAtFrameDepth;
                    bl = bl2;
                }
                if (!bl) {
                    return;
                }
                this.contextData.stopAtFrameDepth = -1;
                this.contextData.breakNextLine = false;
            }
            this.dim.handleBreakpointHit(this, context);
        }

        public Object scope() {
            return this.scope;
        }

        public SourceInfo sourceInfo() {
            return this.fsource.sourceInfo();
        }

        public Object thisObj() {
            return this.thisObj;
        }
    }

}

