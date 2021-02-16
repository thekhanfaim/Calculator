package org.mozilla.javascript;

public final class NativeGenerator extends IdScriptableObject {
   public static final int GENERATOR_CLOSE = 2;
   public static final int GENERATOR_SEND = 0;
   private static final Object GENERATOR_TAG = "Generator";
   public static final int GENERATOR_THROW = 1;
   private static final int Id___iterator__ = 5;
   private static final int Id_close = 1;
   private static final int Id_next = 2;
   private static final int Id_send = 3;
   private static final int Id_throw = 4;
   private static final int MAX_PROTOTYPE_ID = 5;
   private static final long serialVersionUID = 1645892441041347273L;
   private boolean firstTime = true;
   private NativeFunction function;
   private int lineNumber;
   private String lineSource;
   private boolean locked;
   private Object savedState;

   private NativeGenerator() {
   }

   public NativeGenerator(Scriptable var1, NativeFunction var2, Object var3) {
      this.function = var2;
      this.savedState = var3;
      Scriptable var4 = ScriptableObject.getTopLevelScope(var1);
      this.setParentScope(var4);
      this.setPrototype((NativeGenerator)ScriptableObject.getTopScopeValue(var4, GENERATOR_TAG));
   }

   static NativeGenerator init(ScriptableObject var0, boolean var1) {
      NativeGenerator var2 = new NativeGenerator();
      if (var0 != null) {
         var2.setParentScope(var0);
         var2.setPrototype(getObjectPrototype(var0));
      }

      var2.activatePrototypeMap(5);
      if (var1) {
         var2.sealObject();
      }

      if (var0 != null) {
         var0.associateValue(GENERATOR_TAG, var2);
      }

      return var2;
   }

   private Object resume(Context param1, Scriptable param2, int param3, Object param4) {
      // $FF: Couldn't be decompiled
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(GENERATOR_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var4 instanceof NativeGenerator) {
            NativeGenerator var7 = (NativeGenerator)var4;
            if (var6 != 1) {
               if (var6 != 2) {
                  if (var6 != 3) {
                     if (var6 != 4) {
                        if (var6 == 5) {
                           return var4;
                        } else {
                           throw new IllegalArgumentException(String.valueOf(var6));
                        }
                     } else {
                        Object var9;
                        if (var5.length > 0) {
                           var9 = var5[0];
                        } else {
                           var9 = Undefined.instance;
                        }

                        return var7.resume(var2, var3, 1, var9);
                     }
                  } else {
                     Object var8;
                     if (var5.length > 0) {
                        var8 = var5[0];
                     } else {
                        var8 = Undefined.instance;
                     }

                     if (var7.firstTime && !var8.equals(Undefined.instance)) {
                        throw ScriptRuntime.typeError0("msg.send.newborn");
                     } else {
                        return var7.resume(var2, var3, 0, var8);
                     }
                  }
               } else {
                  var7.firstTime = false;
                  return var7.resume(var2, var3, 0, Undefined.instance);
               }
            } else {
               return var7.resume(var2, var3, 2, new NativeGenerator.GeneratorClosedException());
            }
         } else {
            throw incompatibleCallError(var1);
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 4) {
         char var6 = var1.charAt(0);
         if (var6 == 'n') {
            var4 = "next";
            var3 = 2;
         } else {
            var3 = 0;
            var4 = null;
            if (var6 == 's') {
               var4 = "send";
               var3 = 3;
            }
         }
      } else if (var2 == 5) {
         char var5 = var1.charAt(0);
         if (var5 == 'c') {
            var4 = "close";
            var3 = 1;
         } else {
            var3 = 0;
            var4 = null;
            if (var5 == 't') {
               var4 = "throw";
               var3 = 4;
            }
         }
      } else {
         var3 = 0;
         var4 = null;
         if (var2 == 12) {
            var4 = "__iterator__";
            var3 = 5;
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Generator";
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               if (var1 != 4) {
                  if (var1 != 5) {
                     throw new IllegalArgumentException(String.valueOf(var1));
                  }

                  var2 = 1;
                  var3 = "__iterator__";
               } else {
                  var3 = "throw";
                  var2 = 0;
               }
            } else {
               var3 = "send";
               var2 = 0;
            }
         } else {
            var2 = 1;
            var3 = "next";
         }
      } else {
         var2 = 1;
         var3 = "close";
      }

      this.initPrototypeMethod(GENERATOR_TAG, var1, var3, var2);
   }

   public static class GeneratorClosedException extends RuntimeException {
      private static final long serialVersionUID = 2561315658662379681L;
   }
}
