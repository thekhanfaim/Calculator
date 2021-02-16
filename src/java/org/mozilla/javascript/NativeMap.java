package org.mozilla.javascript;

import java.util.Iterator;

public class NativeMap extends IdScriptableObject {
   static final String ITERATOR_TAG = "Map Iterator";
   private static final int Id_clear = 6;
   private static final int Id_constructor = 1;
   private static final int Id_delete = 4;
   private static final int Id_entries = 9;
   private static final int Id_forEach = 10;
   private static final int Id_get = 3;
   private static final int Id_has = 5;
   private static final int Id_keys = 7;
   private static final int Id_set = 2;
   private static final int Id_values = 8;
   private static final Object MAP_TAG = "Map";
   private static final int MAX_PROTOTYPE_ID = 12;
   private static final Object NULL_VALUE = new Object();
   private static final int SymbolId_getSize = 11;
   private static final int SymbolId_toStringTag = 12;
   private final Hashtable entries = new Hashtable();
   private boolean instanceOfMap = false;

   static void init(Context var0, Scriptable var1, boolean var2) {
      NativeMap var3 = new NativeMap();
      var3.exportAsJSClass(12, var1, false);
      ScriptableObject var5 = (ScriptableObject)var0.newObject(var1);
      var5.put((String)"enumerable", var5, false);
      var5.put((String)"configurable", var5, true);
      var5.put((String)"get", var5, var3.get(NativeSet.GETSIZE, var3));
      var3.defineOwnProperty(var0, "size", var5);
      if (var2) {
         var3.sealObject();
      }

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
            Object var11 = var10.value;
            if (var11 == NULL_VALUE) {
               var11 = null;
            }

            Object[] var12 = new Object[]{var11, var10.key, this};
            var6.call(var1, var2, var9, var12);
         }

         return Undefined.instance;
      } else {
         EcmaError var5 = ScriptRuntime.typeError2("msg.isnt.function", var3, ScriptRuntime.typeof(var3));
         throw var5;
      }
   }

   private Object js_get(Object var1) {
      Object var2 = this.entries.get(var1);
      if (var2 == null) {
         return Undefined.instance;
      } else {
         return var2 == NULL_VALUE ? null : var2;
      }
   }

   private Object js_getSize() {
      return this.entries.size();
   }

   private Object js_has(Object var1) {
      return this.entries.has(var1);
   }

   private Object js_iterator(Scriptable var1, NativeCollectionIterator.Type var2) {
      return new NativeCollectionIterator(var1, "Map Iterator", var2, this.entries.iterator());
   }

   private Object js_set(Object var1, Object var2) {
      Object var3;
      if (var2 == null) {
         var3 = NULL_VALUE;
      } else {
         var3 = var2;
      }

      Object var4 = var1;
      if (var1 instanceof Number && ((Number)var1).doubleValue() == ScriptRuntime.negativeZero) {
         var4 = 0.0D;
      }

      this.entries.put(var4, var3);
      return this;
   }

   static void loadFromIterable(Context var0, Scriptable var1, ScriptableObject var2, Object var3) {
      if (var3 != null) {
         if (!Undefined.instance.equals(var3)) {
            Object var4 = ScriptRuntime.callIterator(var3, var0, var1);
            if (!Undefined.instance.equals(var4)) {
               Callable var5 = ScriptRuntime.getPropFunctionAndThis(ensureScriptableObject(var0.newObject(var1, var2.getClassName())).getPrototype(), "set", var0, var1);
               ScriptRuntime.lastStoredScriptable(var0);
               IteratorLikeIterable var7 = new IteratorLikeIterable(var0, var1, var4);

               label616: {
                  Throwable var10000;
                  label606: {
                     IteratorLikeIterable.Itr var11;
                     boolean var10001;
                     try {
                        var11 = var7.iterator();
                     } catch (Throwable var68) {
                        var10000 = var68;
                        var10001 = false;
                        break label606;
                     }

                     while(true) {
                        Scriptable var12;
                        Object var13;
                        label603: {
                           try {
                              if (!var11.hasNext()) {
                                 break label616;
                              }

                              var12 = ScriptableObject.ensureScriptable(var11.next());
                              if (!(var12 instanceof Symbol)) {
                                 var13 = var12.get(0, var12);
                                 if (var13 == NOT_FOUND) {
                                    var13 = Undefined.instance;
                                 }
                                 break label603;
                              }
                           } catch (Throwable var70) {
                              var10000 = var70;
                              var10001 = false;
                              break;
                           }

                           try {
                              throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(var12));
                           } catch (Throwable var66) {
                              var10000 = var66;
                              var10001 = false;
                              break;
                           }
                        }

                        Object var14;
                        try {
                           var14 = var12.get(1, var12);
                           if (var14 == NOT_FOUND) {
                              var14 = Undefined.instance;
                           }
                        } catch (Throwable var69) {
                           var10000 = var69;
                           var10001 = false;
                           break;
                        }

                        try {
                           var5.call(var0, var1, var2, new Object[]{var13, var14});
                        } catch (Throwable var67) {
                           var10000 = var67;
                           var10001 = false;
                           break;
                        }
                     }
                  }

                  Throwable var8 = var10000;

                  try {
                     throw var8;
                  } finally {
                     label578:
                     try {
                        var7.close();
                     } finally {
                        break label578;
                     }

                  }
               }

               var7.close();
            }
         }
      }
   }

   private NativeMap realThis(Scriptable var1, IdFunctionObject var2) {
      if (var1 != null) {
         try {
            NativeMap var4 = (NativeMap)var1;
            if (var4.instanceOfMap) {
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
         switch(var1.methodId()) {
         case 1:
            if (var4 == null) {
               NativeMap var6 = new NativeMap();
               var6.instanceOfMap = true;
               if (var5.length > 0) {
                  loadFromIterable(var2, var3, var6, var5[0]);
               }

               return var6;
            }

            throw ScriptRuntime.typeError1("msg.no.new", "Map");
         case 2:
            NativeMap var7 = this.realThis(var4, var1);
            Object var8;
            if (var5.length > 0) {
               var8 = var5[0];
            } else {
               var8 = Undefined.instance;
            }

            Object var9;
            if (var5.length > 1) {
               var9 = var5[1];
            } else {
               var9 = Undefined.instance;
            }

            return var7.js_set(var8, var9);
         case 3:
            NativeMap var10 = this.realThis(var4, var1);
            Object var11;
            if (var5.length > 0) {
               var11 = var5[0];
            } else {
               var11 = Undefined.instance;
            }

            return var10.js_get(var11);
         case 4:
            NativeMap var12 = this.realThis(var4, var1);
            Object var13;
            if (var5.length > 0) {
               var13 = var5[0];
            } else {
               var13 = Undefined.instance;
            }

            return var12.js_delete(var13);
         case 5:
            NativeMap var14 = this.realThis(var4, var1);
            Object var15;
            if (var5.length > 0) {
               var15 = var5[0];
            } else {
               var15 = Undefined.instance;
            }

            return var14.js_has(var15);
         case 6:
            return this.realThis(var4, var1).js_clear();
         case 7:
            return this.realThis(var4, var1).js_iterator(var3, NativeCollectionIterator.Type.KEYS);
         case 8:
            return this.realThis(var4, var1).js_iterator(var3, NativeCollectionIterator.Type.VALUES);
         case 9:
            return this.realThis(var4, var1).js_iterator(var3, NativeCollectionIterator.Type.BOTH);
         case 10:
            NativeMap var16 = this.realThis(var4, var1);
            Object var17;
            if (var5.length > 0) {
               var17 = var5[0];
            } else {
               var17 = Undefined.instance;
            }

            Object var18;
            if (var5.length > 1) {
               var18 = var5[1];
            } else {
               var18 = Undefined.instance;
            }

            return var16.js_forEach(var2, var3, var17, var18);
         case 11:
            return this.realThis(var4, var1).js_getSize();
         default:
            StringBuilder var19 = new StringBuilder();
            var19.append("Map.prototype has no method: ");
            var19.append(var1.getFunctionName());
            throw new IllegalArgumentException(var19.toString());
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
                     char var13 = var1.charAt(0);
                     if (var13 == 'e') {
                        var5 = "entries";
                        var4 = 9;
                     } else {
                        var4 = 0;
                        var5 = null;
                        if (var13 == 'f') {
                           var5 = "forEach";
                           var4 = 10;
                        }
                     }
                  }
               } else {
                  char var12 = var1.charAt(0);
                  if (var12 == 'd') {
                     var5 = "delete";
                     var4 = 4;
                  } else {
                     var4 = 0;
                     var5 = null;
                     if (var12 == 'v') {
                        var5 = "values";
                        var4 = 8;
                     }
                  }
               }
            } else {
               var5 = "clear";
               var4 = 6;
            }
         } else {
            var5 = "keys";
            var4 = 7;
         }
      } else {
         char var3 = var1.charAt(0);
         if (var3 == 'g') {
            char var10 = var1.charAt(2);
            var4 = 0;
            var5 = null;
            if (var10 == 't') {
               char var11 = var1.charAt(1);
               var4 = 0;
               var5 = null;
               if (var11 == 'e') {
                  return 3;
               }
            }
         } else if (var3 == 'h') {
            char var8 = var1.charAt(2);
            var4 = 0;
            var5 = null;
            if (var8 == 's') {
               char var9 = var1.charAt(1);
               var4 = 0;
               var5 = null;
               if (var9 == 'a') {
                  return 5;
               }
            }
         } else {
            var4 = 0;
            var5 = null;
            if (var3 == 's') {
               char var6 = var1.charAt(2);
               var4 = 0;
               var5 = null;
               if (var6 == 't') {
                  char var7 = var1.charAt(1);
                  var4 = 0;
                  var5 = null;
                  if (var7 == 'e') {
                     return 2;
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
      if (NativeSet.GETSIZE.equals(var1)) {
         return 11;
      } else if (SymbolKey.ITERATOR.equals(var1)) {
         return 9;
      } else {
         return SymbolKey.TO_STRING_TAG.equals(var1) ? 12 : 0;
      }
   }

   public String getClassName() {
      return "Map";
   }

   protected void initPrototypeId(int var1) {
      if (var1 != 11) {
         if (var1 != 12) {
            byte var3;
            String var4;
            switch(var1) {
            case 1:
               var3 = 0;
               var4 = "constructor";
               break;
            case 2:
               var3 = 2;
               var4 = "set";
               break;
            case 3:
               var3 = 1;
               var4 = "get";
               break;
            case 4:
               var3 = 1;
               var4 = "delete";
               break;
            case 5:
               var3 = 1;
               var4 = "has";
               break;
            case 6:
               var4 = "clear";
               var3 = 0;
               break;
            case 7:
               var4 = "keys";
               var3 = 0;
               break;
            case 8:
               var4 = "values";
               var3 = 0;
               break;
            case 9:
               var4 = "entries";
               var3 = 0;
               break;
            case 10:
               var3 = 1;
               var4 = "forEach";
               break;
            default:
               throw new IllegalArgumentException(String.valueOf(var1));
            }

            this.initPrototypeMethod(MAP_TAG, var1, var4, (String)null, var3);
         } else {
            this.initPrototypeValue(12, SymbolKey.TO_STRING_TAG, this.getClassName(), 3);
         }
      } else {
         this.initPrototypeMethod(MAP_TAG, var1, NativeSet.GETSIZE, "get size", 0);
      }
   }
}
