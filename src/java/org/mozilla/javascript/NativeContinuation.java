package org.mozilla.javascript;

public final class NativeContinuation extends IdScriptableObject implements Function {
   private static final Object FTAG = "Continuation";
   private static final int Id_constructor = 1;
   private static final int MAX_PROTOTYPE_ID = 1;
   private static final long serialVersionUID = 1794167133757605367L;
   private Object implementation;

   public static boolean equalImplementations(NativeContinuation var0, NativeContinuation var1) {
      return .$r8$backportedMethods$utility$Objects$2$equals.equals(var0.implementation, var1.implementation);
   }

   public static void init(Context var0, Scriptable var1, boolean var2) {
      (new NativeContinuation()).exportAsJSClass(1, var1, var2);
   }

   public static boolean isContinuationConstructor(IdFunctionObject var0) {
      return var0.hasTag(FTAG) && var0.methodId() == 1;
   }

   public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      return Interpreter.restartContinuation(this, var1, var2, var4);
   }

   public Scriptable construct(Context var1, Scriptable var2, Object[] var3) {
      throw Context.reportRuntimeError("Direct call is not supported");
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(FTAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 != 1) {
            throw new IllegalArgumentException(String.valueOf(var6));
         } else {
            throw Context.reportRuntimeError("Direct call is not supported");
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3 = 0;
      String var4 = null;
      if (var2 == 11) {
         var4 = "constructor";
         var3 = 1;
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Continuation";
   }

   public Object getImplementation() {
      return this.implementation;
   }

   public void initImplementation(Object var1) {
      this.implementation = var1;
   }

   protected void initPrototypeId(int var1) {
      if (var1 == 1) {
         this.initPrototypeMethod(FTAG, var1, "constructor", 0);
      } else {
         throw new IllegalArgumentException(String.valueOf(var1));
      }
   }
}
