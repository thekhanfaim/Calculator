package org.mozilla.javascript;

public final class NativeCall extends IdScriptableObject {
   private static final Object CALL_TAG = "Call";
   private static final int Id_constructor = 1;
   private static final int MAX_PROTOTYPE_ID = 1;
   private static final long serialVersionUID = -7471457301304454454L;
   private Arguments arguments;
   NativeFunction function;
   boolean isStrict;
   Object[] originalArgs;
   transient NativeCall parentActivationCall;

   NativeCall() {
   }

   NativeCall(NativeFunction var1, Scriptable var2, Object[] var3, boolean var4, boolean var5) {
      this.function = var1;
      this.setParentScope(var2);
      Object[] var6;
      if (var3 == null) {
         var6 = ScriptRuntime.emptyArgs;
      } else {
         var6 = var3;
      }

      this.originalArgs = var6;
      this.isStrict = var5;
      int var7 = var1.getParamAndVarCount();
      int var8 = var1.getParamCount();
      if (var7 != 0) {
         for(int var12 = 0; var12 < var8; ++var12) {
            String var13 = var1.getParamOrVarName(var12);
            Object var14;
            if (var12 < var3.length) {
               var14 = var3[var12];
            } else {
               var14 = Undefined.instance;
            }

            this.defineProperty(var13, var14, 4);
         }
      }

      if (!super.has((String)"arguments", this) && !var4) {
         Arguments var11 = new Arguments(this);
         this.arguments = var11;
         this.defineProperty("arguments", var11, 4);
      }

      if (var7 != 0) {
         for(int var9 = var8; var9 < var7; ++var9) {
            String var10 = var1.getParamOrVarName(var9);
            if (!super.has((String)var10, this)) {
               if (var1.getParamOrVarConst(var9)) {
                  this.defineProperty(var10, Undefined.instance, 13);
               } else {
                  this.defineProperty(var10, Undefined.instance, 4);
               }
            }
         }
      }

   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeCall()).exportAsJSClass(1, var0, var1);
   }

   public void defineAttributesForArguments() {
      Arguments var1 = this.arguments;
      if (var1 != null) {
         var1.defineAttributesForStrictMode();
      }

   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(CALL_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 == 1) {
            if (var4 == null) {
               ScriptRuntime.checkDeprecated(var2, "Call");
               NativeCall var7 = new NativeCall();
               var7.setPrototype(getObjectPrototype(var3));
               return var7;
            } else {
               throw Context.reportRuntimeError1("msg.only.from.new", "Call");
            }
         } else {
            throw new IllegalArgumentException(String.valueOf(var6));
         }
      }
   }

   protected int findPrototypeId(String var1) {
      return var1.equals("constructor");
   }

   public String getClassName() {
      return "Call";
   }

   protected void initPrototypeId(int var1) {
      if (var1 == 1) {
         this.initPrototypeMethod(CALL_TAG, var1, "constructor", 1);
      } else {
         throw new IllegalArgumentException(String.valueOf(var1));
      }
   }
}
