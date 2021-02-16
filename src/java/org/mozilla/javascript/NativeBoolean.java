package org.mozilla.javascript;

final class NativeBoolean extends IdScriptableObject {
   private static final Object BOOLEAN_TAG = "Boolean";
   private static final int Id_constructor = 1;
   private static final int Id_toSource = 3;
   private static final int Id_toString = 2;
   private static final int Id_valueOf = 4;
   private static final int MAX_PROTOTYPE_ID = 4;
   private static final long serialVersionUID = -3716996899943880933L;
   private boolean booleanValue;

   NativeBoolean(boolean var1) {
      this.booleanValue = var1;
   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeBoolean(false)).exportAsJSClass(4, var0, var1);
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(BOOLEAN_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         byte var7 = 1;
         if (var6 != var7) {
            if (var4 instanceof NativeBoolean) {
               boolean var8 = ((NativeBoolean)var4).booleanValue;
               if (var6 != 2) {
                  if (var6 != 3) {
                     if (var6 == 4) {
                        return ScriptRuntime.wrapBoolean(var8);
                     } else {
                        throw new IllegalArgumentException(String.valueOf(var6));
                     }
                  } else {
                     return var8 ? "(new Boolean(true))" : "(new Boolean(false))";
                  }
               } else {
                  return var8 ? "true" : "false";
               }
            } else {
               throw incompatibleCallError(var1);
            }
         } else {
            if (var5.length == 0) {
               var7 = 0;
            } else if (!(var5[0] instanceof ScriptableObject) || !((ScriptableObject)var5[0]).avoidObjectDetection()) {
               var7 = ScriptRuntime.toBoolean(var5[0]);
            }

            return var4 == null ? new NativeBoolean((boolean)var7) : ScriptRuntime.wrapBoolean((boolean)var7);
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 7) {
         var4 = "valueOf";
         var3 = 4;
      } else if (var2 == 8) {
         char var5 = var1.charAt(3);
         if (var5 == 'o') {
            var4 = "toSource";
            var3 = 3;
         } else {
            var3 = 0;
            var4 = null;
            if (var5 == 't') {
               var4 = "toString";
               var3 = 2;
            }
         }
      } else {
         var3 = 0;
         var4 = null;
         if (var2 == 11) {
            var4 = "constructor";
            var3 = 1;
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Boolean";
   }

   public Object getDefaultValue(Class var1) {
      return var1 == ScriptRuntime.BooleanClass ? ScriptRuntime.wrapBoolean(this.booleanValue) : super.getDefaultValue(var1);
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               if (var1 != 4) {
                  throw new IllegalArgumentException(String.valueOf(var1));
               }

               var3 = "valueOf";
               var2 = 0;
            } else {
               var3 = "toSource";
               var2 = 0;
            }
         } else {
            var3 = "toString";
            var2 = 0;
         }
      } else {
         var2 = 1;
         var3 = "constructor";
      }

      this.initPrototypeMethod(BOOLEAN_TAG, var1, var3, var2);
   }
}
