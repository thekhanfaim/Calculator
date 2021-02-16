package org.mozilla.javascript;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.mozilla.classfile.ClassFileWriter;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.debug.Debugger;
import org.mozilla.javascript.xml.XMLLib;

public class Context {
   public static final int FEATURE_DYNAMIC_SCOPE = 7;
   public static final int FEATURE_E4X = 6;
   public static final int FEATURE_ENHANCED_JAVA_ACCESS = 13;
   public static final int FEATURE_ENUMERATE_IDS_FIRST = 16;
   public static final int FEATURE_INTEGER_WITHOUT_DECIMAL_PLACE = 18;
   public static final int FEATURE_LITTLE_ENDIAN = 19;
   public static final int FEATURE_LOCATION_INFORMATION_IN_ERROR = 10;
   public static final int FEATURE_MEMBER_EXPR_AS_FUNCTION_NAME = 2;
   public static final int FEATURE_NON_ECMA_GET_YEAR = 1;
   public static final int FEATURE_OLD_UNDEF_NULL_THIS = 15;
   public static final int FEATURE_PARENT_PROTO_PROPERTIES = 5;
   @Deprecated
   public static final int FEATURE_PARENT_PROTO_PROPRTIES = 5;
   public static final int FEATURE_RESERVED_KEYWORD_AS_IDENTIFIER = 3;
   public static final int FEATURE_STRICT_EVAL = 9;
   public static final int FEATURE_STRICT_MODE = 11;
   public static final int FEATURE_STRICT_VARS = 8;
   public static final int FEATURE_THREAD_SAFE_OBJECTS = 17;
   public static final int FEATURE_TO_STRING_AS_SOURCE = 4;
   public static final int FEATURE_V8_EXTENSIONS = 14;
   public static final int FEATURE_WARNING_AS_ERROR = 12;
   public static final int VERSION_1_0 = 100;
   public static final int VERSION_1_1 = 110;
   public static final int VERSION_1_2 = 120;
   public static final int VERSION_1_3 = 130;
   public static final int VERSION_1_4 = 140;
   public static final int VERSION_1_5 = 150;
   public static final int VERSION_1_6 = 160;
   public static final int VERSION_1_7 = 170;
   public static final int VERSION_1_8 = 180;
   public static final int VERSION_DEFAULT = 0;
   public static final int VERSION_ES6 = 200;
   public static final int VERSION_UNKNOWN = -1;
   private static Class codegenClass;
   public static final Object[] emptyArgs;
   public static final String errorReporterProperty = "error reporter";
   private static String implementationVersion;
   private static Class interpreterClass;
   public static final String languageVersionProperty = "language version";
   Set activationNames;
   private ClassLoader applicationClassLoader;
   XMLLib cachedXMLLib;
   private ClassShutter classShutter;
   NativeCall currentActivationCall;
   Debugger debugger;
   private Object debuggerData;
   private int enterCount;
   private ErrorReporter errorReporter;
   private final ContextFactory factory;
   public boolean generateObserverCount;
   private boolean generatingDebug;
   private boolean generatingDebugChanged;
   private boolean generatingSource;
   private boolean hasClassShutter;
   int instructionCount;
   int instructionThreshold;
   Object interpreterSecurityDomain;
   boolean isContinuationsTopCall;
   boolean isTopLevelStrict;
   ObjToIntMap iterating;
   Object lastInterpreterFrame;
   private Locale locale;
   private int maximumInterpreterStackDepth;
   private int optimizationLevel;
   ObjArray previousInterpreterInvocations;
   private Object propertyListeners;
   RegExpProxy regExpProxy;
   int scratchIndex;
   Scriptable scratchScriptable;
   long scratchUint32;
   private Object sealKey;
   private boolean sealed;
   private SecurityController securityController;
   private Map threadLocalMap;
   Scriptable topCallScope;
   BaseFunction typeErrorThrower;
   boolean useDynamicScope;
   int version;
   private WrapFactory wrapFactory;

   static {
      emptyArgs = ScriptRuntime.emptyArgs;
      codegenClass = Kit.classOrNull("org.mozilla.javascript.optimizer.Codegen");
      interpreterClass = Kit.classOrNull("org.mozilla.javascript.Interpreter");
   }

   @Deprecated
   public Context() {
      this(ContextFactory.getGlobal());
   }

   protected Context(ContextFactory var1) {
      this.generatingSource = true;
      this.generateObserverCount = false;
      if (var1 != null) {
         this.factory = var1;
         this.version = 0;
         byte var2;
         if (codegenClass != null) {
            var2 = 0;
         } else {
            var2 = -1;
         }

         this.optimizationLevel = var2;
         this.maximumInterpreterStackDepth = Integer.MAX_VALUE;
      } else {
         throw new IllegalArgumentException("factory == null");
      }
   }

   @Deprecated
   public static void addContextListener(ContextListener var0) {
      if ("org.mozilla.javascript.tools.debugger.Main".equals(var0.getClass().getName())) {
         Class var1 = var0.getClass();
         Class[] var2 = new Class[]{Kit.classOrNull("org.mozilla.javascript.ContextFactory")};
         Object[] var3 = new Object[]{ContextFactory.getGlobal()};

         try {
            var1.getMethod("attachTo", var2).invoke(var0, var3);
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      } else {
         ContextFactory.getGlobal().addListener(var0);
      }
   }

   @Deprecated
   public static Object call(ContextAction var0) {
      return call(ContextFactory.getGlobal(), var0);
   }

   public static Object call(ContextFactory var0, Callable var1, Scriptable var2, Scriptable var3, Object[] var4) {
      if (var0 == null) {
         var0 = ContextFactory.getGlobal();
      }

      return call(var0, new -$$Lambda$Context$FBfUmmX3GbL3akcaT52xTgeMGSk(var1, var2, var3, var4));
   }

   static Object call(ContextFactory var0, ContextAction var1) {
      Context var2 = enter((Context)null, var0);

      Object var4;
      try {
         var4 = var1.run(var2);
      } finally {
         exit();
      }

      return var4;
   }

   public static void checkLanguageVersion(int var0) {
      if (!isValidLanguageVersion(var0)) {
         StringBuilder var1 = new StringBuilder();
         var1.append("Bad language version: ");
         var1.append(var0);
         throw new IllegalArgumentException(var1.toString());
      }
   }

   public static void checkOptimizationLevel(int var0) {
      if (!isValidOptimizationLevel(var0)) {
         StringBuilder var1 = new StringBuilder();
         var1.append("Optimization level outside [-1..9]: ");
         var1.append(var0);
         throw new IllegalArgumentException(var1.toString());
      }
   }

   private Object compileImpl(Scriptable var1, Reader var2, String var3, String var4, int var5, Object var6, boolean var7, Evaluator var8, ErrorReporter var9) throws IOException {
      String var10;
      if (var4 == null) {
         var10 = "unnamed script";
      } else {
         var10 = var4;
      }

      if (var6 != null && this.getSecurityController() == null) {
         throw new IllegalArgumentException("securityDomain should be null if setSecurityController() was never called");
      } else {
         boolean var11 = true;
         boolean var12;
         if (var2 == null) {
            var12 = true;
         } else {
            var12 = false;
         }

         boolean var13;
         if (var3 == null) {
            var13 = true;
         } else {
            var13 = false;
         }

         if (!(var12 ^ var13)) {
            Kit.codeBug();
         }

         if (var1 != null) {
            var11 = false;
         }

         if (!(var11 ^ var7)) {
            Kit.codeBug();
         }

         CompilerEnvirons var14 = new CompilerEnvirons();
         var14.initFromContext(this);
         ErrorReporter var15;
         if (var9 == null) {
            var15 = var14.getErrorReporter();
         } else {
            var15 = var9;
         }

         Reader var16;
         String var17;
         if (this.debugger != null && var2 != null) {
            var17 = Kit.readReader(var2);
            var16 = null;
         } else {
            var16 = var2;
            var17 = var3;
         }

         Evaluator var24;
         Object var32;
         label75: {
            label74: {
               ScriptNode var23 = this.parse(var16, var17, var10, var5, var14, var15, var7);
               if (var8 == null) {
                  Evaluator var34;
                  try {
                     var34 = this.createCompiler();
                  } catch (ClassFileWriter.ClassFileFormatException var36) {
                     break label74;
                  }

                  var24 = var34;
               } else {
                  var24 = var8;
               }

               try {
                  var32 = var24.compile(var14, var23, var23.getEncodedSource(), var7);
                  break label75;
               } catch (ClassFileWriter.ClassFileFormatException var35) {
               }
            }

            Evaluator var26 = createInterpreter();
            ScriptNode var30 = this.parse(var16, var17, var10, var5, var14, var15, var7);
            Object var31 = var26.compile(var14, var30, var30.getEncodedSource(), var7);
            var24 = var26;
            var32 = var31;
         }

         if (this.debugger != null) {
            if (var17 == null) {
               Kit.codeBug();
            }

            if (!(var32 instanceof DebuggableScript)) {
               throw new RuntimeException("NOT SUPPORTED");
            }

            notifyDebugger_r(this, (DebuggableScript)var32, var17);
         }

         return var7 ? var24.createFunctionObject(this, var1, var32, var6) : var24.createScriptObject(var32, var6);
      }
   }

   private Evaluator createCompiler() {
      int var1 = this.optimizationLevel;
      Evaluator var2 = null;
      if (var1 >= 0) {
         Class var3 = codegenClass;
         var2 = null;
         if (var3 != null) {
            var2 = (Evaluator)Kit.newInstanceOrNull(var3);
         }
      }

      if (var2 == null) {
         var2 = createInterpreter();
      }

      return var2;
   }

   static Evaluator createInterpreter() {
      return (Evaluator)Kit.newInstanceOrNull(interpreterClass);
   }

   public static Context enter() {
      return enter((Context)null);
   }

   @Deprecated
   public static Context enter(Context var0) {
      return enter(var0, ContextFactory.getGlobal());
   }

   static final Context enter(Context var0, ContextFactory var1) {
      Object var2 = VMBridge.instance.getThreadContextHelper();
      Context var3 = VMBridge.instance.getContext(var2);
      if (var3 != null) {
         var0 = var3;
      } else {
         if (var0 == null) {
            var0 = var1.makeContext();
            if (var0.enterCount != 0) {
               throw new IllegalStateException("factory.makeContext() returned Context instance already associated with some thread");
            }

            var1.onContextCreated(var0);
            if (var1.isSealed() && !var0.isSealed()) {
               var0.seal((Object)null);
            }
         } else if (var0.enterCount != 0) {
            throw new IllegalStateException("can not use Context instance already associated with some thread");
         }

         VMBridge.instance.setContext(var2, var0);
      }

      ++var0.enterCount;
      return var0;
   }

   public static void exit() {
      Object var0 = VMBridge.instance.getThreadContextHelper();
      Context var1 = VMBridge.instance.getContext(var0);
      if (var1 != null) {
         if (var1.enterCount < 1) {
            Kit.codeBug();
         }

         int var2 = var1.enterCount - 1;
         var1.enterCount = var2;
         if (var2 == 0) {
            VMBridge.instance.setContext(var0, (Context)null);
            var1.factory.onContextReleased(var1);
         }

      } else {
         throw new IllegalStateException("Calling Context.exit without previous Context.enter");
      }
   }

   private void firePropertyChangeImpl(Object var1, String var2, Object var3, Object var4) {
      int var5 = 0;

      while(true) {
         Object var6 = Kit.getListener(var1, var5);
         if (var6 == null) {
            return;
         }

         if (var6 instanceof PropertyChangeListener) {
            ((PropertyChangeListener)var6).propertyChange(new PropertyChangeEvent(this, var2, var3, var4));
         }

         ++var5;
      }
   }

   static Context getContext() {
      Context var0 = getCurrentContext();
      if (var0 != null) {
         return var0;
      } else {
         throw new RuntimeException("No Context associated with current Thread");
      }
   }

   public static Context getCurrentContext() {
      Object var0 = VMBridge.instance.getThreadContextHelper();
      return VMBridge.instance.getContext(var0);
   }

   public static DebuggableScript getDebuggableView(Script var0) {
      return var0 instanceof NativeFunction ? ((NativeFunction)var0).getDebuggableView() : null;
   }

   static String getSourcePositionFromStack(int[] var0) {
      Context var1 = getCurrentContext();
      if (var1 == null) {
         return null;
      } else {
         if (var1.lastInterpreterFrame != null) {
            Evaluator var8 = createInterpreter();
            if (var8 != null) {
               return var8.getSourcePositionFromStack(var1, var0);
            }
         }

         StackTraceElement[] var2 = (new Throwable()).getStackTrace();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement var5 = var2[var4];
            String var6 = var5.getFileName();
            if (var6 != null && !var6.endsWith(".java")) {
               int var7 = var5.getLineNumber();
               if (var7 >= 0) {
                  var0[0] = var7;
                  return var6;
               }
            }
         }

         return null;
      }
   }

   public static Object getUndefinedValue() {
      return Undefined.instance;
   }

   public static boolean isValidLanguageVersion(int var0) {
      switch(var0) {
      case 0:
      case 100:
      case 110:
      case 120:
      case 130:
      case 140:
      case 150:
      case 160:
      case 170:
      case 180:
      case 200:
         return true;
      default:
         return false;
      }
   }

   public static boolean isValidOptimizationLevel(int var0) {
      return -1 <= var0 && var0 <= 9;
   }

   public static Object javaToJS(Object var0, Scriptable var1) {
      if (!(var0 instanceof String) && !(var0 instanceof Number) && !(var0 instanceof Boolean)) {
         if (var0 instanceof Scriptable) {
            return var0;
         } else if (var0 instanceof Character) {
            return String.valueOf((Character)var0);
         } else {
            Context var2 = getContext();
            return var2.getWrapFactory().wrap(var2, var1, var0, (Class)null);
         }
      } else {
         return var0;
      }
   }

   public static Object jsToJava(Object var0, Class var1) throws EvaluatorException {
      return NativeJavaObject.coerceTypeImpl(var1, var0);
   }

   // $FF: synthetic method
   static Object lambda$call$0(Callable var0, Scriptable var1, Scriptable var2, Object[] var3, Context var4) {
      return var0.call(var4, var1, var2, var3);
   }

   private static void notifyDebugger_r(Context var0, DebuggableScript var1, String var2) {
      var0.debugger.handleCompilationDone(var0, var1, var2);

      for(int var3 = 0; var3 != var1.getFunctionCount(); ++var3) {
         notifyDebugger_r(var0, var1.getFunction(var3), var2);
      }

   }

   static void onSealedMutation() {
      throw new IllegalStateException();
   }

   private ScriptNode parse(Reader var1, String var2, String var3, int var4, CompilerEnvirons var5, ErrorReporter var6, boolean var7) throws IOException {
      Parser var8 = new Parser(var5, var6);
      if (var7) {
         var8.calledByCompileFunction = true;
      }

      if (this.isStrictMode()) {
         var8.setDefaultUseStrictDirective(true);
      }

      AstRoot var9;
      if (var2 != null) {
         var9 = var8.parse(var2, var3, var4);
      } else {
         var9 = var8.parse(var1, var3, var4);
      }

      if (!var7 || var9.getFirstChild() != null && var9.getFirstChild().getType() == 110) {
         return (new IRFactory(var5, var6)).transformTree(var9);
      } else {
         StringBuilder var10 = new StringBuilder();
         var10.append("compileFunction only accepts source with single JS function: ");
         var10.append(var2);
         throw new IllegalArgumentException(var10.toString());
      }
   }

   @Deprecated
   public static void removeContextListener(ContextListener var0) {
      ContextFactory.getGlobal().addListener(var0);
   }

   public static void reportError(String var0) {
      int[] var1 = new int[]{0};
      reportError(var0, getSourcePositionFromStack(var1), var1[0], (String)null, 0);
   }

   public static void reportError(String var0, String var1, int var2, String var3, int var4) {
      Context var5 = getCurrentContext();
      if (var5 != null) {
         var5.getErrorReporter().error(var0, var1, var2, var3, var4);
      } else {
         EvaluatorException var6 = new EvaluatorException(var0, var1, var2, var3, var4);
         throw var6;
      }
   }

   public static EvaluatorException reportRuntimeError(String var0) {
      int[] var1 = new int[]{0};
      return reportRuntimeError(var0, getSourcePositionFromStack(var1), var1[0], (String)null, 0);
   }

   public static EvaluatorException reportRuntimeError(String var0, String var1, int var2, String var3, int var4) {
      Context var5 = getCurrentContext();
      if (var5 != null) {
         return var5.getErrorReporter().runtimeError(var0, var1, var2, var3, var4);
      } else {
         EvaluatorException var6 = new EvaluatorException(var0, var1, var2, var3, var4);
         throw var6;
      }
   }

   static EvaluatorException reportRuntimeError0(String var0) {
      return reportRuntimeError(ScriptRuntime.getMessage0(var0));
   }

   static EvaluatorException reportRuntimeError1(String var0, Object var1) {
      return reportRuntimeError(ScriptRuntime.getMessage1(var0, var1));
   }

   static EvaluatorException reportRuntimeError2(String var0, Object var1, Object var2) {
      return reportRuntimeError(ScriptRuntime.getMessage2(var0, var1, var2));
   }

   static EvaluatorException reportRuntimeError3(String var0, Object var1, Object var2, Object var3) {
      return reportRuntimeError(ScriptRuntime.getMessage3(var0, var1, var2, var3));
   }

   static EvaluatorException reportRuntimeError4(String var0, Object var1, Object var2, Object var3, Object var4) {
      return reportRuntimeError(ScriptRuntime.getMessage4(var0, var1, var2, var3, var4));
   }

   public static void reportWarning(String var0) {
      int[] var1 = new int[]{0};
      reportWarning(var0, getSourcePositionFromStack(var1), var1[0], (String)null, 0);
   }

   public static void reportWarning(String var0, String var1, int var2, String var3, int var4) {
      Context var5 = getContext();
      if (var5.hasFeature(12)) {
         reportError(var0, var1, var2, var3, var4);
      } else {
         var5.getErrorReporter().warning(var0, var1, var2, var3, var4);
      }
   }

   public static void reportWarning(String var0, Throwable var1) {
      int[] var2 = new int[]{0};
      String var3 = getSourcePositionFromStack(var2);
      StringWriter var4 = new StringWriter();
      PrintWriter var5 = new PrintWriter(var4);
      var5.println(var0);
      var1.printStackTrace(var5);
      var5.flush();
      reportWarning(var4.toString(), var3, var2[0], (String)null, 0);
   }

   @Deprecated
   public static void setCachingEnabled(boolean var0) {
   }

   public static RuntimeException throwAsScriptRuntimeEx(Throwable var0) {
      while(var0 instanceof InvocationTargetException) {
         var0 = ((InvocationTargetException)var0).getTargetException();
      }

      if (var0 instanceof Error) {
         Context var2 = getContext();
         if (var2 == null || !var2.hasFeature(13)) {
            throw (Error)var0;
         }
      }

      if (var0 instanceof RhinoException) {
         throw (RhinoException)var0;
      } else {
         WrappedException var1 = new WrappedException(var0);
         throw var1;
      }
   }

   public static boolean toBoolean(Object var0) {
      return ScriptRuntime.toBoolean(var0);
   }

   public static double toNumber(Object var0) {
      return ScriptRuntime.toNumber(var0);
   }

   public static Scriptable toObject(Object var0, Scriptable var1) {
      return ScriptRuntime.toObject(var1, var0);
   }

   @Deprecated
   public static Scriptable toObject(Object var0, Scriptable var1, Class var2) {
      return ScriptRuntime.toObject(var1, var0);
   }

   public static String toString(Object var0) {
      return ScriptRuntime.toString(var0);
   }

   @Deprecated
   public static Object toType(Object var0, Class var1) throws IllegalArgumentException {
      try {
         Object var3 = jsToJava(var0, var1);
         return var3;
      } catch (EvaluatorException var4) {
         throw new IllegalArgumentException(var4.getMessage(), var4);
      }
   }

   public void addActivationName(String var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (this.activationNames == null) {
         this.activationNames = new HashSet();
      }

      this.activationNames.add(var1);
   }

   public final void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.propertyListeners = Kit.addListener(this.propertyListeners, var1);
   }

   public Object callFunctionWithContinuations(Callable var1, Scriptable var2, Object[] var3) throws ContinuationPending {
      if (var1 instanceof InterpretedFunction) {
         if (!ScriptRuntime.hasTopCall(this)) {
            this.isContinuationsTopCall = true;
            return ScriptRuntime.doTopCall(var1, this, var2, var2, var3, this.isTopLevelStrict);
         } else {
            throw new IllegalStateException("Cannot have any pending top calls when executing a script with continuations");
         }
      } else {
         throw new IllegalArgumentException("Function argument was not created by interpreted mode ");
      }
   }

   public ContinuationPending captureContinuation() {
      return new ContinuationPending(Interpreter.captureContinuation(this));
   }

   public final Function compileFunction(Scriptable var1, String var2, String var3, int var4, Object var5) {
      return this.compileFunction(var1, var2, (Evaluator)null, (ErrorReporter)null, var3, var4, var5);
   }

   final Function compileFunction(Scriptable var1, String var2, Evaluator var3, ErrorReporter var4, String var5, int var6, Object var7) {
      try {
         Function var9 = (Function)this.compileImpl(var1, (Reader)null, var2, var5, var6, var7, true, var3, var4);
         return var9;
      } catch (IOException var10) {
         throw new RuntimeException(var10);
      }
   }

   public final Script compileReader(Reader var1, String var2, int var3, Object var4) throws IOException {
      if (var3 < 0) {
         var3 = 0;
      }

      return (Script)this.compileImpl((Scriptable)null, var1, (String)null, var2, var3, var4, false, (Evaluator)null, (ErrorReporter)null);
   }

   @Deprecated
   public final Script compileReader(Scriptable var1, Reader var2, String var3, int var4, Object var5) throws IOException {
      return this.compileReader(var2, var3, var4, var5);
   }

   public final Script compileString(String var1, String var2, int var3, Object var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      return this.compileString(var1, (Evaluator)null, (ErrorReporter)null, var2, var3, var4);
   }

   final Script compileString(String var1, Evaluator var2, ErrorReporter var3, String var4, int var5, Object var6) {
      try {
         Script var8 = (Script)this.compileImpl((Scriptable)null, (Reader)null, var1, var4, var5, var6, false, var2, var3);
         return var8;
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      }
   }

   public GeneratedClassLoader createClassLoader(ClassLoader var1) {
      return this.getFactory().createClassLoader(var1);
   }

   public final String decompileFunction(Function var1, int var2) {
      if (var1 instanceof BaseFunction) {
         return ((BaseFunction)var1).decompile(var2, 0);
      } else {
         StringBuilder var3 = new StringBuilder();
         var3.append("function ");
         var3.append(var1.getClassName());
         var3.append("() {\n\t[native code]\n}\n");
         return var3.toString();
      }
   }

   public final String decompileFunctionBody(Function var1, int var2) {
      return var1 instanceof BaseFunction ? ((BaseFunction)var1).decompile(var2, 1) : "[native code]\n";
   }

   public final String decompileScript(Script var1, int var2) {
      return ((NativeFunction)var1).decompile(var2, 0);
   }

   public final Object evaluateReader(Scriptable var1, Reader var2, String var3, int var4, Object var5) throws IOException {
      Script var6 = this.compileReader(var1, var2, var3, var4, var5);
      return var6 != null ? var6.exec(this, var1) : null;
   }

   public final Object evaluateString(Scriptable var1, String var2, String var3, int var4, Object var5) {
      Script var6 = this.compileString(var2, var3, var4, var5);
      return var6 != null ? var6.exec(this, var1) : null;
   }

   public Object executeScriptWithContinuations(Script var1, Scriptable var2) throws ContinuationPending {
      if (var1 instanceof InterpretedFunction && ((InterpretedFunction)var1).isScript()) {
         return this.callFunctionWithContinuations((InterpretedFunction)var1, var2, ScriptRuntime.emptyArgs);
      } else {
         throw new IllegalArgumentException("Script argument was not a script or was not created by interpreted mode ");
      }
   }

   final void firePropertyChange(String var1, Object var2, Object var3) {
      Object var4 = this.propertyListeners;
      if (var4 != null) {
         this.firePropertyChangeImpl(var4, var1, var2, var3);
      }

   }

   public final ClassLoader getApplicationClassLoader() {
      if (this.applicationClassLoader == null) {
         ContextFactory var1 = this.getFactory();
         ClassLoader var2 = var1.getApplicationClassLoader();
         if (var2 == null) {
            ClassLoader var3 = Thread.currentThread().getContextClassLoader();
            if (var3 != null && Kit.testIfCanLoadRhinoClasses(var3)) {
               return var3;
            }

            Class var4 = var1.getClass();
            if (var4 != ScriptRuntime.ContextFactoryClass) {
               var2 = var4.getClassLoader();
            } else {
               var2 = this.getClass().getClassLoader();
            }
         }

         this.applicationClassLoader = var2;
      }

      return this.applicationClassLoader;
   }

   final ClassShutter getClassShutter() {
      synchronized(this){}

      ClassShutter var2;
      try {
         var2 = this.classShutter;
      } finally {
         ;
      }

      return var2;
   }

   public final Context.ClassShutterSetter getClassShutterSetter() {
      synchronized(this){}

      Throwable var10000;
      label78: {
         boolean var10001;
         boolean var2;
         try {
            var2 = this.hasClassShutter;
         } catch (Throwable var10) {
            var10000 = var10;
            var10001 = false;
            break label78;
         }

         if (var2) {
            return null;
         }

         Context.ClassShutterSetter var3;
         try {
            this.hasClassShutter = true;
            var3 = new Context.ClassShutterSetter() {
               public ClassShutter getClassShutter() {
                  return Context.this.classShutter;
               }

               public void setClassShutter(ClassShutter var1) {
                  Context.this.classShutter = var1;
               }
            };
         } catch (Throwable var9) {
            var10000 = var9;
            var10001 = false;
            break label78;
         }

         return var3;
      }

      Throwable var1 = var10000;
      throw var1;
   }

   public final Debugger getDebugger() {
      return this.debugger;
   }

   public final Object getDebuggerContextData() {
      return this.debuggerData;
   }

   public XMLLib.Factory getE4xImplementationFactory() {
      return this.getFactory().getE4xImplementationFactory();
   }

   public final Object[] getElements(Scriptable var1) {
      return ScriptRuntime.getArrayElements(var1);
   }

   public final ErrorReporter getErrorReporter() {
      ErrorReporter var1 = this.errorReporter;
      return (ErrorReporter)(var1 == null ? DefaultErrorReporter.instance : var1);
   }

   public final ContextFactory getFactory() {
      return this.factory;
   }

   public final String getImplementationVersion() {
      InputStream var4;
      String var15;
      label160: {
         if (implementationVersion == null) {
            Enumeration var2;
            try {
               var2 = Context.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            } catch (IOException var27) {
               return null;
            }

            while(var2.hasMoreElements()) {
               URL var3 = (URL)var2.nextElement();
               var4 = null;
               boolean var24 = false;

               boolean var10001;
               label158: {
                  try {
                     var24 = true;
                     var4 = var3.openStream();
                     Attributes var9 = (new Manifest(var4)).getMainAttributes();
                     if ("Mozilla Rhino".equals(var9.getValue("Implementation-Title"))) {
                        StringBuilder var10 = new StringBuilder();
                        var10.append("Rhino ");
                        var10.append(var9.getValue("Implementation-Version"));
                        var10.append(" ");
                        var10.append(var9.getValue("Built-Date").replaceAll("-", " "));
                        var15 = var10.toString();
                        implementationVersion = var15;
                        var24 = false;
                        break label160;
                     }

                     var24 = false;
                  } catch (IOException var30) {
                     var24 = false;
                     break label158;
                  } finally {
                     if (var24) {
                        if (var4 != null) {
                           try {
                              var4.close();
                           } catch (IOException var25) {
                           }
                        }

                     }
                  }

                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (IOException var29) {
                        var10001 = false;
                     }
                  }
                  continue;
               }

               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (IOException var28) {
                     var10001 = false;
                  }
               }
            }
         }

         return implementationVersion;
      }

      if (var4 != null) {
         try {
            var4.close();
         } catch (IOException var26) {
            return var15;
         }
      }

      return var15;
   }

   public final int getInstructionObserverThreshold() {
      return this.instructionThreshold;
   }

   public final int getLanguageVersion() {
      return this.version;
   }

   public final Locale getLocale() {
      if (this.locale == null) {
         this.locale = Locale.getDefault();
      }

      return this.locale;
   }

   public final int getMaximumInterpreterStackDepth() {
      return this.maximumInterpreterStackDepth;
   }

   public final int getOptimizationLevel() {
      return this.optimizationLevel;
   }

   RegExpProxy getRegExpProxy() {
      if (this.regExpProxy == null) {
         Class var1 = Kit.classOrNull("org.mozilla.javascript.regexp.RegExpImpl");
         if (var1 != null) {
            this.regExpProxy = (RegExpProxy)Kit.newInstanceOrNull(var1);
         }
      }

      return this.regExpProxy;
   }

   SecurityController getSecurityController() {
      SecurityController var1 = SecurityController.global();
      return var1 != null ? var1 : this.securityController;
   }

   public final Object getThreadLocal(Object var1) {
      Map var2 = this.threadLocalMap;
      return var2 == null ? null : var2.get(var1);
   }

   public final WrapFactory getWrapFactory() {
      if (this.wrapFactory == null) {
         this.wrapFactory = new WrapFactory();
      }

      return this.wrapFactory;
   }

   public boolean hasFeature(int var1) {
      return this.getFactory().hasFeature(this, var1);
   }

   public final Scriptable initSafeStandardObjects(ScriptableObject var1) {
      return this.initSafeStandardObjects(var1, false);
   }

   public final ScriptableObject initSafeStandardObjects() {
      return this.initSafeStandardObjects((ScriptableObject)null, false);
   }

   public ScriptableObject initSafeStandardObjects(ScriptableObject var1, boolean var2) {
      return ScriptRuntime.initSafeStandardObjects(this, var1, var2);
   }

   public final Scriptable initStandardObjects(ScriptableObject var1) {
      return this.initStandardObjects(var1, false);
   }

   public final ScriptableObject initStandardObjects() {
      return this.initStandardObjects((ScriptableObject)null, false);
   }

   public ScriptableObject initStandardObjects(ScriptableObject var1, boolean var2) {
      return ScriptRuntime.initStandardObjects(this, var1, var2);
   }

   public final boolean isActivationNeeded(String var1) {
      Set var2 = this.activationNames;
      return var2 != null && var2.contains(var1);
   }

   public final boolean isGeneratingDebug() {
      return this.generatingDebug;
   }

   public final boolean isGeneratingDebugChanged() {
      return this.generatingDebugChanged;
   }

   public final boolean isGeneratingSource() {
      return this.generatingSource;
   }

   public final boolean isSealed() {
      return this.sealed;
   }

   public final boolean isStrictMode() {
      if (!this.isTopLevelStrict) {
         NativeCall var1 = this.currentActivationCall;
         if (var1 == null || !var1.isStrict) {
            return false;
         }
      }

      return true;
   }

   final boolean isVersionECMA1() {
      int var1 = this.version;
      return var1 == 0 || var1 >= 130;
   }

   public Scriptable newArray(Scriptable var1, int var2) {
      NativeArray var3 = new NativeArray((long)var2);
      ScriptRuntime.setBuiltinProtoAndParent(var3, var1, TopLevel.Builtins.Array);
      return var3;
   }

   public Scriptable newArray(Scriptable var1, Object[] var2) {
      if (var2.getClass().getComponentType() == ScriptRuntime.ObjectClass) {
         NativeArray var3 = new NativeArray(var2);
         ScriptRuntime.setBuiltinProtoAndParent(var3, var1, TopLevel.Builtins.Array);
         return var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Scriptable newObject(Scriptable var1) {
      NativeObject var2 = new NativeObject();
      ScriptRuntime.setBuiltinProtoAndParent(var2, var1, TopLevel.Builtins.Object);
      return var2;
   }

   public Scriptable newObject(Scriptable var1, String var2) {
      return this.newObject(var1, var2, ScriptRuntime.emptyArgs);
   }

   public Scriptable newObject(Scriptable var1, String var2, Object[] var3) {
      return ScriptRuntime.newObject(this, var1, var2, var3);
   }

   protected void observeInstructionCount(int var1) {
      this.getFactory().observeInstructionCount(this, var1);
   }

   public final void putThreadLocal(Object var1, Object var2) {
      synchronized(this){}

      try {
         if (this.sealed) {
            onSealedMutation();
         }

         if (this.threadLocalMap == null) {
            this.threadLocalMap = new HashMap();
         }

         this.threadLocalMap.put(var1, var2);
      } finally {
         ;
      }

   }

   public void removeActivationName(String var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      Set var2 = this.activationNames;
      if (var2 != null) {
         var2.remove(var1);
      }

   }

   public final void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.propertyListeners = Kit.removeListener(this.propertyListeners, var1);
   }

   public final void removeThreadLocal(Object var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      Map var2 = this.threadLocalMap;
      if (var2 != null) {
         var2.remove(var1);
      }
   }

   public Object resumeContinuation(Object var1, Scriptable var2, Object var3) throws ContinuationPending {
      Object[] var4 = new Object[]{var3};
      return Interpreter.restartContinuation((NativeContinuation)var1, this, var2, var4);
   }

   public final void seal(Object var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.sealed = true;
      this.sealKey = var1;
   }

   public final void setApplicationClassLoader(ClassLoader var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 == null) {
         this.applicationClassLoader = null;
      } else if (Kit.testIfCanLoadRhinoClasses(var1)) {
         this.applicationClassLoader = var1;
      } else {
         throw new IllegalArgumentException("Loader can not resolve Rhino classes");
      }
   }

   public final void setClassShutter(ClassShutter var1) {
      synchronized(this){}

      Throwable var10000;
      label205: {
         boolean var10001;
         try {
            if (this.sealed) {
               onSealedMutation();
            }
         } catch (Throwable var22) {
            var10000 = var22;
            var10001 = false;
            break label205;
         }

         if (var1 == null) {
            label188:
            try {
               throw new IllegalArgumentException();
            } catch (Throwable var21) {
               var10000 = var21;
               var10001 = false;
               break label188;
            }
         } else {
            label206: {
               try {
                  if (!this.hasClassShutter) {
                     this.classShutter = var1;
                     this.hasClassShutter = true;
                     return;
                  }
               } catch (Throwable var23) {
                  var10000 = var23;
                  var10001 = false;
                  break label206;
               }

               label186:
               try {
                  throw new SecurityException("Cannot overwrite existing ClassShutter object");
               } catch (Throwable var20) {
                  var10000 = var20;
                  var10001 = false;
                  break label186;
               }
            }
         }
      }

      Throwable var2 = var10000;
      throw var2;
   }

   public final void setDebugger(Debugger var1, Object var2) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.debugger = var1;
      this.debuggerData = var2;
   }

   public final ErrorReporter setErrorReporter(ErrorReporter var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 != null) {
         ErrorReporter var2 = this.getErrorReporter();
         if (var1 == var2) {
            return var2;
         } else {
            Object var3 = this.propertyListeners;
            if (var3 != null) {
               this.firePropertyChangeImpl(var3, "error reporter", var2, var1);
            }

            this.errorReporter = var1;
            return var2;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setGenerateObserverCount(boolean var1) {
      this.generateObserverCount = var1;
   }

   public final void setGeneratingDebug(boolean var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.generatingDebugChanged = true;
      if (var1 && this.getOptimizationLevel() > 0) {
         this.setOptimizationLevel(0);
      }

      this.generatingDebug = var1;
   }

   public final void setGeneratingSource(boolean var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      this.generatingSource = var1;
   }

   public final void setInstructionObserverThreshold(int var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 >= 0) {
         this.instructionThreshold = var1;
         boolean var2;
         if (var1 > 0) {
            var2 = true;
         } else {
            var2 = false;
         }

         this.setGenerateObserverCount(var2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setLanguageVersion(int var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      checkLanguageVersion(var1);
      Object var2 = this.propertyListeners;
      if (var2 != null) {
         int var3 = this.version;
         if (var1 != var3) {
            this.firePropertyChangeImpl(var2, "language version", var3, var1);
         }
      }

      this.version = var1;
   }

   public final Locale setLocale(Locale var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      Locale var2 = this.locale;
      this.locale = var1;
      return var2;
   }

   public final void setMaximumInterpreterStackDepth(int var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (this.optimizationLevel == -1) {
         if (var1 >= 1) {
            this.maximumInterpreterStackDepth = var1;
         } else {
            throw new IllegalArgumentException("Cannot set maximumInterpreterStackDepth to less than 1");
         }
      } else {
         throw new IllegalStateException("Cannot set maximumInterpreterStackDepth when optimizationLevel != -1");
      }
   }

   public final void setOptimizationLevel(int var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 == -2) {
         var1 = -1;
      }

      checkOptimizationLevel(var1);
      if (codegenClass == null) {
         var1 = -1;
      }

      this.optimizationLevel = var1;
   }

   public final void setSecurityController(SecurityController var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 != null) {
         if (this.securityController == null) {
            if (!SecurityController.hasGlobal()) {
               this.securityController = var1;
            } else {
               throw new SecurityException("Can not overwrite existing global SecurityController object");
            }
         } else {
            throw new SecurityException("Can not overwrite existing SecurityController object");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void setWrapFactory(WrapFactory var1) {
      if (this.sealed) {
         onSealedMutation();
      }

      if (var1 != null) {
         this.wrapFactory = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final boolean stringIsCompilableUnit(String var1) {
      CompilerEnvirons var2 = new CompilerEnvirons();
      var2.initFromContext(this);
      var2.setGeneratingSource(false);
      Parser var3 = new Parser(var2, DefaultErrorReporter.instance);

      boolean var5;
      label21: {
         try {
            var3.parse((String)var1, (String)null, 1);
         } catch (EvaluatorException var8) {
            var5 = true;
            break label21;
         }

         var5 = false;
      }

      boolean var6;
      if (var5) {
         boolean var7 = var3.eof();
         var6 = false;
         if (var7) {
            return var6;
         }
      }

      var6 = true;
      return var6;
   }

   public final void unseal(Object var1) {
      if (var1 != null) {
         if (this.sealKey == var1) {
            if (this.sealed) {
               this.sealed = false;
               this.sealKey = null;
            } else {
               throw new IllegalStateException();
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public interface ClassShutterSetter {
      ClassShutter getClassShutter();

      void setClassShutter(ClassShutter var1);
   }
}
