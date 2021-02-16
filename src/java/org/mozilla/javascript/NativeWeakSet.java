package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.WeakHashMap;

public class NativeWeakSet extends IdScriptableObject {
   private static final int Id_add = 2;
   private static final int Id_constructor = 1;
   private static final int Id_delete = 3;
   private static final int Id_has = 4;
   private static final Object MAP_TAG = "WeakSet";
   private static final int MAX_PROTOTYPE_ID = 5;
   private static final int SymbolId_toStringTag = 5;
   private boolean instanceOfWeakSet = false;
   private transient WeakHashMap map = new WeakHashMap();

   static void init(Scriptable var0, boolean var1) {
      (new NativeWeakSet()).exportAsJSClass(5, var0, var1);
   }

   private Object js_add(Object var1) {
      if (ScriptRuntime.isObject(var1)) {
         this.map.put((Scriptable)var1, Boolean.TRUE);
         return this;
      } else {
         throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(var1));
      }
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

   private Object js_has(Object var1) {
      return !ScriptRuntime.isObject(var1) ? false : this.map.containsKey(var1);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.map = new WeakHashMap();
   }

   private NativeWeakSet realThis(Scriptable var1, IdFunctionObject var2) {
      if (var1 != null) {
         try {
            NativeWeakSet var4 = (NativeWeakSet)var1;
            if (var4.instanceOfWeakSet) {
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
                  if (var6 == 4) {
                     NativeWeakSet var15 = this.realThis(var4, var1);
                     Object var16;
                     if (var5.length > 0) {
                        var16 = var5[0];
                     } else {
                        var16 = Undefined.instance;
                     }

                     return var15.js_has(var16);
                  } else {
                     StringBuilder var12 = new StringBuilder();
                     var12.append("WeakMap.prototype has no method: ");
                     var12.append(var1.getFunctionName());
                     throw new IllegalArgumentException(var12.toString());
                  }
               } else {
                  NativeWeakSet var10 = this.realThis(var4, var1);
                  Object var11;
                  if (var5.length > 0) {
                     var11 = var5[0];
                  } else {
                     var11 = Undefined.instance;
                  }

                  return var10.js_delete(var11);
               }
            } else {
               NativeWeakSet var8 = this.realThis(var4, var1);
               Object var9;
               if (var5.length > 0) {
                  var9 = var5[0];
               } else {
                  var9 = Undefined.instance;
               }

               return var8.js_add(var9);
            }
         } else if (var4 == null) {
            NativeWeakSet var7 = new NativeWeakSet();
            var7.instanceOfWeakSet = true;
            if (var5.length > 0) {
               NativeSet.loadFromIterable(var2, var3, var7, var5[0]);
            }

            return var7;
         } else {
            throw ScriptRuntime.typeError1("msg.no.new", "WeakSet");
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 3) {
         char var5 = var1.charAt(0);
         if (var5 == 'a') {
            char var8 = var1.charAt(2);
            var3 = 0;
            var4 = null;
            if (var8 == 'd') {
               char var9 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var9 == 'd') {
                  return 2;
               }
            }
         } else {
            var3 = 0;
            var4 = null;
            if (var5 == 'h') {
               char var6 = var1.charAt(2);
               var3 = 0;
               var4 = null;
               if (var6 == 's') {
                  char var7 = var1.charAt(1);
                  var3 = 0;
                  var4 = null;
                  if (var7 == 'a') {
                     return 4;
                  }
               }
            }
         }
      } else if (var2 == 6) {
         var4 = "delete";
         var3 = 3;
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
      return SymbolKey.TO_STRING_TAG.equals(var1) ? 5 : 0;
   }

   public String getClassName() {
      return "WeakSet";
   }

   protected void initPrototypeId(int var1) {
      if (var1 == 5) {
         this.initPrototypeValue(5, SymbolKey.TO_STRING_TAG, this.getClassName(), 3);
      } else {
         byte var2;
         String var3;
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  if (var1 != 4) {
                     throw new IllegalArgumentException(String.valueOf(var1));
                  }

                  var2 = 1;
                  var3 = "has";
               } else {
                  var2 = 1;
                  var3 = "delete";
               }
            } else {
               var2 = 1;
               var3 = "add";
            }
         } else {
            var2 = 0;
            var3 = "constructor";
         }

         this.initPrototypeMethod(MAP_TAG, var1, var3, (String)null, var2);
      }
   }
}
