package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.WeakHashMap;

public class NativeWeakMap extends IdScriptableObject {
   private static final int Id_constructor = 1;
   private static final int Id_delete = 2;
   private static final int Id_get = 3;
   private static final int Id_has = 4;
   private static final int Id_set = 5;
   private static final Object MAP_TAG = "WeakMap";
   private static final int MAX_PROTOTYPE_ID = 6;
   private static final Object NULL_VALUE = new Object();
   private static final int SymbolId_toStringTag = 6;
   private boolean instanceOfWeakMap = false;
   private transient WeakHashMap map = new WeakHashMap();

   static void init(Scriptable var0, boolean var1) {
      (new NativeWeakMap()).exportAsJSClass(6, var0, var1);
   }

   private Object js_delete(Object var1) {
      if (!ScriptRuntime.isObject(var1)) {
         return false;
      } else {
         Object var2 = this.map.remove(var1);
         boolean var3 = false;
         if (var2 != null) {
            var3 = true;
         }

         return var3;
      }
   }

   private Object js_get(Object var1) {
      if (!ScriptRuntime.isObject(var1)) {
         return Undefined.instance;
      } else {
         Object var2 = this.map.get(var1);
         if (var2 == null) {
            return Undefined.instance;
         } else {
            return var2 == NULL_VALUE ? null : var2;
         }
      }
   }

   private Object js_has(Object var1) {
      return !ScriptRuntime.isObject(var1) ? false : this.map.containsKey(var1);
   }

   private Object js_set(Object var1, Object var2) {
      if (ScriptRuntime.isObject(var1)) {
         Object var3;
         if (var2 == null) {
            var3 = NULL_VALUE;
         } else {
            var3 = var2;
         }

         this.map.put((Scriptable)var1, var3);
         return this;
      } else {
         throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(var1));
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.map = new WeakHashMap();
   }

   private NativeWeakMap realThis(Scriptable var1, IdFunctionObject var2) {
      if (var1 != null) {
         try {
            NativeWeakMap var4 = (NativeWeakMap)var1;
            if (var4.instanceOfWeakMap) {
               return var4;
            } else {
               throw incompatibleCallError(var2);
            }
         } catch (ClassCastException var5) {
            throw incompatibleCallError(var2);
         }
      } else {
         throw incompatibleCallError(var2);
      }
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(MAP_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 != 1) {
            if (var6 != 2) {
               if (var6 != 3) {
                  if (var6 != 4) {
                     if (var6 == 5) {
                        NativeWeakMap var17 = this.realThis(var4, var1);
                        Object var18;
                        if (var5.length > 0) {
                           var18 = var5[0];
                        } else {
                           var18 = Undefined.instance;
                        }

                        Object var19;
                        if (var5.length > 1) {
                           var19 = var5[1];
                        } else {
                           var19 = Undefined.instance;
                        }

                        return var17.js_set(var18, var19);
                     } else {
                        StringBuilder var14 = new StringBuilder();
                        var14.append("WeakMap.prototype has no method: ");
                        var14.append(var1.getFunctionName());
                        throw new IllegalArgumentException(var14.toString());
                     }
                  } else {
                     NativeWeakMap var12 = this.realThis(var4, var1);
                     Object var13;
                     if (var5.length > 0) {
                        var13 = var5[0];
                     } else {
                        var13 = Undefined.instance;
                     }

                     return var12.js_has(var13);
                  }
               } else {
                  NativeWeakMap var10 = this.realThis(var4, var1);
                  Object var11;
                  if (var5.length > 0) {
                     var11 = var5[0];
                  } else {
                     var11 = Undefined.instance;
                  }

                  return var10.js_get(var11);
               }
            } else {
               NativeWeakMap var8 = this.realThis(var4, var1);
               Object var9;
               if (var5.length > 0) {
                  var9 = var5[0];
               } else {
                  var9 = Undefined.instance;
               }

               return var8.js_delete(var9);
            }
         } else if (var4 == null) {
            NativeWeakMap var7 = new NativeWeakMap();
            var7.instanceOfWeakMap = true;
            if (var5.length > 0) {
               NativeMap.loadFromIterable(var2, var3, var7, var5[0]);
            }

            return var7;
         } else {
            throw ScriptRuntime.typeError1("msg.no.new", "WeakMap");
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 3) {
         char var5 = var1.charAt(0);
         if (var5 == 'g') {
            char var10 = var1.charAt(2);
            var3 = 0;
            var4 = null;
            if (var10 == 't') {
               char var11 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var11 == 'e') {
                  return 3;
               }
            }
         } else if (var5 == 'h') {
            char var8 = var1.charAt(2);
            var3 = 0;
            var4 = null;
            if (var8 == 's') {
               char var9 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var9 == 'a') {
                  return 4;
               }
            }
         } else {
            var3 = 0;
            var4 = null;
            if (var5 == 's') {
               char var6 = var1.charAt(2);
               var3 = 0;
               var4 = null;
               if (var6 == 't') {
                  char var7 = var1.charAt(1);
                  var3 = 0;
                  var4 = null;
                  if (var7 == 'e') {
                     return 5;
                  }
               }
            }
         }
      } else if (var2 == 6) {
         var4 = "delete";
         var3 = 2;
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

   protected int findPrototypeId(Symbol var1) {
      return SymbolKey.TO_STRING_TAG.equals(var1) ? 6 : 0;
   }

   public String getClassName() {
      return "WeakMap";
   }

   protected void initPrototypeId(int var1) {
      if (var1 == 6) {
         this.initPrototypeValue(6, SymbolKey.TO_STRING_TAG, this.getClassName(), 3);
      } else {
         byte var2;
         String var3;
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  if (var1 != 4) {
                     if (var1 != 5) {
                        throw new IllegalArgumentException(String.valueOf(var1));
                     }

                     var2 = 2;
                     var3 = "set";
                  } else {
                     var2 = 1;
                     var3 = "has";
                  }
               } else {
                  var2 = 1;
                  var3 = "get";
               }
            } else {
               var2 = 1;
               var3 = "delete";
            }
         } else {
            var2 = 0;
            var3 = "constructor";
         }

         this.initPrototypeMethod(MAP_TAG, var1, var3, (String)null, var2);
      }
   }
}
