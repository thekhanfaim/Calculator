package org.mozilla.javascript;

import java.util.Iterator;

public class NativeSet extends IdScriptableObject {
   static final SymbolKey GETSIZE = new SymbolKey("[Symbol.getSize]");
   static final String ITERATOR_TAG = "Set Iterator";
   private static final int Id_add = 2;
   private static final int Id_clear = 5;
   private static final int Id_constructor = 1;
   private static final int Id_delete = 3;
   private static final int Id_entries = 7;
   private static final int Id_forEach = 8;
   private static final int Id_has = 4;
   private static final int Id_keys = 6;
   private static final int Id_values = 6;
   private static final int MAX_PROTOTYPE_ID = 10;
   private static final Object SET_TAG = "Set";
   private static final int SymbolId_getSize = 9;
   private static final int SymbolId_toStringTag = 10;
   private final Hashtable entries = new Hashtable();
   private boolean instanceOfSet = false;

   static void init(Context var0, Scriptable var1, boolean var2) {
      NativeSet var3 = new NativeSet();
      var3.exportAsJSClass(10, var1, false);
      ScriptableObject var5 = (ScriptableObject)var0.newObject(var1);
      var5.put((String)"enumerable", var5, false);
      var5.put((String)"configurable", var5, true);
      var5.put((String)"get", var5, var3.get(GETSIZE, var3));
      var3.defineOwnProperty(var0, "size", var5);
      if (var2) {
         var3.sealObject();
      }

   }

   private Object js_add(Object var1) {
      Object var2 = var1;
      if (var1 instanceof Number && ((Number)var1).doubleValue() == ScriptRuntime.negativeZero) {
         var2 = 0.0D;
      }

      this.entries.put(var2, var2);
      return this;
   }

   private Object js_clear() {
      this.entries.clear();
      return Undefined.instance;
   }

   private Object js_delete(Object var1) {
      boolean var2;
      if (this.entries.delete(var1) != null) {
         var2 = true;
      } else {
         var2 = false;
      }

      return var2;
   }

   private Object js_forEach(Context var1, Scriptable var2, Object var3, Object var4) {
      if (var3 instanceof Callable) {
         Callable var6 = (Callable)var3;
         boolean var7 = var1.isStrictMode();
         Iterator var8 = this.entries.iterator();

         while(var8.hasNext()) {
            Scriptable var9 = ScriptRuntime.toObjectOrNull(var1, var4, var2);
            if (var9 == null && !var7) {
               var9 = var2;
            }

            if (var9 == null) {
               var9 = Undefined.SCRIPTABLE_UNDEFINED;
            }

            Hashtable.Entry var10 = (Hashtable.Entry)var8.next();
            Object[] var11 = new Object[]{var10.value, var10.value, this};
            var6.call(var1, var2, var9, var11);
         }

         return Undefined.instance;
      } else {
         RuntimeException var5 = ScriptRuntime.notFunctionError(var3);
         throw var5;
      }
   }

   private Object js_getSize() {
      return this.entries.size();
   }

   private Object js_has(Object var1) {
      return this.entries.has(var1);
   }

   private Object js_iterator(Scriptable var1, NativeCollectionIterator.Type var2) {
      return new NativeCollectionIterator(var1, "Set Iterator", var2, this.entries.iterator());
   }

   static void loadFromIterable(Context var0, Scriptable var1, ScriptableObject var2, Object var3) {
      if (var3 != null) {
         if (!Undefined.instance.equals(var3)) {
            Object var4 = ScriptRuntime.callIterator(var3, var0, var1);
            if (!Undefined.instance.equals(var4)) {
               Callable var5 = ScriptRuntime.getPropFunctionAndThis(ensureScriptableObject(var0.newObject(var1, var2.getClassName())).getPrototype(), "add", var0, var1);
               ScriptRuntime.lastStoredScriptable(var0);
               IteratorLikeIterable var7 = new IteratorLikeIterable(var0, var1, var4);

               label376: {
                  Throwable var10000;
                  label366: {
                     IteratorLikeIterable.Itr var11;
                     boolean var10001;
                     try {
                        var11 = var7.iterator();
                     } catch (Throwable var43) {
                        var10000 = var43;
                        var10001 = false;
                        break label366;
                     }

                     while(true) {
                        Object var13;
                        label361: {
                           Object var12;
                           try {
                              if (!var11.hasNext()) {
                                 break label376;
                              }

                              var12 = var11.next();
                              if (var12 == Scriptable.NOT_FOUND) {
                                 var13 = Undefined.instance;
                                 break label361;
                              }
                           } catch (Throwable var42) {
                              var10000 = var42;
                              var10001 = false;
                              break;
                           }

                           var13 = var12;
                        }

                        try {
                           var5.call(var0, var1, var2, new Object[]{var13});
                        } catch (Throwable var41) {
                           var10000 = var41;
                           var10001 = false;
                           break;
                        }
                     }
                  }

                  Throwable var8 = var10000;

                  try {
                     throw var8;
                  } finally {
                     label346:
                     try {
                        var7.close();
                     } finally {
                        break label346;
                     }

                  }
               }

               var7.close();
            }
         }
      }
   }

   private NativeSet realThis(Scriptable var1, IdFunctionObject var2) {
      if (var1 != null) {
         try {
            NativeSet var4 = (NativeSet)var1;
            if (var4.instanceOfSet) {
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
      if (!var1.hasTag(SET_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         switch(var1.methodId()) {
         case 1:
            if (var4 == null) {
               NativeSet var6 = new NativeSet();
               var6.instanceOfSet = true;
               if (var5.length > 0) {
                  loadFromIterable(var2, var3, var6, var5[0]);
               }

               return var6;
            }

            throw ScriptRuntime.typeError1("msg.no.new", "Set");
         case 2:
            NativeSet var7 = this.realThis(var4, var1);
            Object var8;
            if (var5.length > 0) {
               var8 = var5[0];
            } else {
               var8 = Undefined.instance;
            }

            return var7.js_add(var8);
         case 3:
            NativeSet var9 = this.realThis(var4, var1);
            Object var10;
            if (var5.length > 0) {
               var10 = var5[0];
            } else {
               var10 = Undefined.instance;
            }

            return var9.js_delete(var10);
         case 4:
            NativeSet var11 = this.realThis(var4, var1);
            Object var12;
            if (var5.length > 0) {
               var12 = var5[0];
            } else {
               var12 = Undefined.instance;
            }

            return var11.js_has(var12);
         case 5:
            return this.realThis(var4, var1).js_clear();
         case 6:
            return this.realThis(var4, var1).js_iterator(var3, NativeCollectionIterator.Type.VALUES);
         case 7:
            return this.realThis(var4, var1).js_iterator(var3, NativeCollectionIterator.Type.BOTH);
         case 8:
            NativeSet var13 = this.realThis(var4, var1);
            Object var14;
            if (var5.length > 0) {
               var14 = var5[0];
            } else {
               var14 = Undefined.instance;
            }

            Object var15;
            if (var5.length > 1) {
               var15 = var5[1];
            } else {
               var15 = Undefined.instance;
            }

            return var13.js_forEach(var2, var3, var14, var15);
         case 9:
            return this.realThis(var4, var1).js_getSize();
         default:
            StringBuilder var16 = new StringBuilder();
            var16.append("Set.prototype has no method: ");
            var16.append(var1.getFunctionName());
            throw new IllegalArgumentException(var16.toString());
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var4;
      String var5;
      if (var2 != 3) {
         if (var2 != 4) {
            if (var2 != 5) {
               if (var2 != 6) {
                  if (var2 != 7) {
                     if (var2 != 11) {
                        var4 = 0;
                        var5 = null;
                     } else {
                        var5 = "constructor";
                        var4 = 1;
                     }
                  } else {
                     char var11 = var1.charAt(0);
                     if (var11 == 'e') {
                        var5 = "entries";
                        var4 = 7;
                     } else {
                        var4 = 0;
                        var5 = null;
                        if (var11 == 'f') {
                           var5 = "forEach";
                           var4 = 8;
                        }
                     }
                  }
               } else {
                  char var10 = var1.charAt(0);
                  if (var10 == 'd') {
                     var5 = "delete";
                     var4 = 3;
                  } else {
                     var4 = 0;
                     var5 = null;
                     if (var10 == 'v') {
                        var5 = "values";
                        var4 = 6;
                     }
                  }
               }
            } else {
               var5 = "clear";
               var4 = 5;
            }
         } else {
            var5 = "keys";
            var4 = 6;
         }
      } else {
         char var3 = var1.charAt(0);
         if (var3 == 'a') {
            char var8 = var1.charAt(2);
            var4 = 0;
            var5 = null;
            if (var8 == 'd') {
               char var9 = var1.charAt(1);
               var4 = 0;
               var5 = null;
               if (var9 == 'd') {
                  return 2;
               }
            }
         } else {
            var4 = 0;
            var5 = null;
            if (var3 == 'h') {
               char var6 = var1.charAt(2);
               var4 = 0;
               var5 = null;
               if (var6 == 's') {
                  char var7 = var1.charAt(1);
                  var4 = 0;
                  var5 = null;
                  if (var7 == 'a') {
                     return 4;
                  }
               }
            }
         }
      }

      if (var5 != null && var5 != var1 && !var5.equals(var1)) {
         var4 = 0;
      }

      return var4;
   }

   protected int findPrototypeId(Symbol var1) {
      if (GETSIZE.equals(var1)) {
         return 9;
      } else if (SymbolKey.ITERATOR.equals(var1)) {
         return 6;
      } else {
         return SymbolKey.TO_STRING_TAG.equals(var1) ? 10 : 0;
      }
   }

   public String getClassName() {
      return "Set";
   }

   protected void initPrototypeId(int var1) {
      if (var1 != 9) {
         if (var1 != 10) {
            byte var3;
            String var4;
            switch(var1) {
            case 1:
               var3 = 0;
               var4 = "constructor";
               break;
            case 2:
               var3 = 1;
               var4 = "add";
               break;
            case 3:
               var3 = 1;
               var4 = "delete";
               break;
            case 4:
               var3 = 1;
               var4 = "has";
               break;
            case 5:
               var4 = "clear";
               var3 = 0;
               break;
            case 6:
               var4 = "values";
               var3 = 0;
               break;
            case 7:
               var4 = "entries";
               var3 = 0;
               break;
            case 8:
               var3 = 1;
               var4 = "forEach";
               break;
            default:
               throw new IllegalArgumentException(String.valueOf(var1));
            }

            this.initPrototypeMethod(SET_TAG, var1, var4, (String)null, var3);
         } else {
            this.initPrototypeValue(10, SymbolKey.TO_STRING_TAG, this.getClassName(), 3);
         }
      } else {
         this.initPrototypeMethod(SET_TAG, var1, GETSIZE, "get size", 0);
      }
   }
}
