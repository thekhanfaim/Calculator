package org.mozilla.javascript;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class NativeObject extends IdScriptableObject implements Map {
   private static final int ConstructorId_assign = -15;
   private static final int ConstructorId_create = -9;
   private static final int ConstructorId_defineProperties = -8;
   private static final int ConstructorId_defineProperty = -5;
   private static final int ConstructorId_freeze = -13;
   private static final int ConstructorId_getOwnPropertyDescriptor = -4;
   private static final int ConstructorId_getOwnPropertyNames = -3;
   private static final int ConstructorId_getOwnPropertySymbols = -14;
   private static final int ConstructorId_getPrototypeOf = -1;
   private static final int ConstructorId_is = -16;
   private static final int ConstructorId_isExtensible = -6;
   private static final int ConstructorId_isFrozen = -11;
   private static final int ConstructorId_isSealed = -10;
   private static final int ConstructorId_keys = -2;
   private static final int ConstructorId_preventExtensions = -7;
   private static final int ConstructorId_seal = -12;
   private static final int Id___defineGetter__ = 9;
   private static final int Id___defineSetter__ = 10;
   private static final int Id___lookupGetter__ = 11;
   private static final int Id___lookupSetter__ = 12;
   private static final int Id_constructor = 1;
   private static final int Id_hasOwnProperty = 5;
   private static final int Id_isPrototypeOf = 7;
   private static final int Id_propertyIsEnumerable = 6;
   private static final int Id_toLocaleString = 3;
   private static final int Id_toSource = 8;
   private static final int Id_toString = 2;
   private static final int Id_valueOf = 4;
   private static final int MAX_PROTOTYPE_ID = 12;
   private static final Object OBJECT_TAG = "Object";
   private static final long serialVersionUID = -6345305608474346996L;

   private static Scriptable getCompatibleObject(Context var0, Scriptable var1, Object var2) {
      return var0.getLanguageVersion() >= 200 ? ensureScriptable(ScriptRuntime.toObject(var0, var1, var2)) : ensureScriptable(var2);
   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeObject()).exportAsJSClass(12, var0, var1);
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public boolean containsKey(Object var1) {
      if (var1 instanceof String) {
         return this.has((String)var1, this);
      } else {
         return var1 instanceof Number ? this.has(((Number)var1).intValue(), this) : false;
      }
   }

   public boolean containsValue(Object var1) {
      Iterator var2 = this.values().iterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = var2.next();
      } while(var1 != var3 && (var1 == null || !var1.equals(var3)));

      return true;
   }

   public Set entrySet() {
      return new NativeObject.EntrySet();
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      NativeObject var6 = this;
      if (!var1.hasTag(OBJECT_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var7 = var1.methodId();
         int var8 = 0;
         byte var9 = 1;
         switch(var7) {
         case -16:
            Object var10;
            if (var5.length < var9) {
               var10 = Undefined.instance;
            } else {
               var10 = var5[0];
            }

            Object var11;
            if (var5.length < 2) {
               var11 = Undefined.instance;
            } else {
               var11 = var5[var9];
            }

            return ScriptRuntime.wrapBoolean(ScriptRuntime.same(var10, var11));
         case -15:
            if (var5.length < var9) {
               throw ScriptRuntime.typeError1("msg.incompat.call", "assign");
            }

            Scriptable var12 = ScriptRuntime.toObject(var2, var4, var5[0]);
            int var13 = 1;

            for(; var13 < var5.length; ++var13) {
               if (var5[var13] != null && !Undefined.instance.equals(var5[var13])) {
                  Scriptable var14 = ScriptRuntime.toObject(var2, var4, var5[var13]);
                  Object[] var15 = var14.getIds();
                  int var16 = var15.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     Object var18 = var15[var17];
                     if (var18 instanceof String) {
                        Object var21 = var14.get((String)var18, var12);
                        if (var21 != Scriptable.NOT_FOUND && var21 != Undefined.instance) {
                           var12.put((String)var18, var12, var21);
                        }
                     } else if (var18 instanceof Number) {
                        int var19 = ScriptRuntime.toInt32(var18);
                        Object var20 = var14.get(var19, var12);
                        if (var20 != Scriptable.NOT_FOUND && var20 != Undefined.instance) {
                           var12.put(var19, var12, var20);
                        }
                     }
                  }
               }
            }

            return var12;
         case -14:
            Object var22;
            if (var5.length < var9) {
               var22 = Undefined.instance;
            } else {
               var22 = var5[0];
            }

            Object[] var23 = ensureScriptableObject(getCompatibleObject(var2, var3, var22)).getIds((boolean)var9, (boolean)var9);
            ArrayList var24 = new ArrayList();

            for(int var25 = 0; var25 < var23.length; ++var25) {
               if (var23[var25] instanceof Symbol) {
                  var24.add(var23[var25]);
               }
            }

            return var2.newArray(var3, var24.toArray());
         case -13:
            Object var27;
            if (var5.length < var9) {
               var27 = Undefined.instance;
            } else {
               var27 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var27 instanceof ScriptableObject)) {
               return var27;
            }

            ScriptableObject var28 = ensureScriptableObject(var27);
            Object[] var29 = var28.getAllIds();
            int var30 = var29.length;

            for(int var31 = 0; var31 < var30; var6 = this) {
               Object var32 = var29[var31];
               ScriptableObject var33 = var28.getOwnPropertyDescriptor(var2, var32);
               if (var6.isDataDescriptor(var33) && Boolean.TRUE.equals(var33.get("writable"))) {
                  var33.put((String)"writable", var33, Boolean.FALSE);
               }

               if (Boolean.TRUE.equals(var33.get("configurable"))) {
                  var33.put((String)"configurable", var33, Boolean.FALSE);
               }

               var28.defineOwnProperty(var2, var32, var33, false);
               ++var31;
            }

            var28.preventExtensions();
            return var28;
         case -12:
            Object var34;
            if (var5.length < var9) {
               var34 = Undefined.instance;
            } else {
               var34 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var34 instanceof ScriptableObject)) {
               return var34;
            }

            ScriptableObject var35 = ensureScriptableObject(var34);
            Object[] var36 = var35.getAllIds();
            int var37 = var36.length;

            for(int var38 = 0; var38 < var37; ++var38) {
               Object var39 = var36[var38];
               ScriptableObject var40 = var35.getOwnPropertyDescriptor(var2, var39);
               if (Boolean.TRUE.equals(var40.get("configurable"))) {
                  var40.put((String)"configurable", var40, Boolean.FALSE);
                  var35.defineOwnProperty(var2, var39, var40, false);
               }
            }

            var35.preventExtensions();
            return var35;
         case -11:
            Object var41;
            if (var5.length < var9) {
               var41 = Undefined.instance;
            } else {
               var41 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var41 instanceof ScriptableObject)) {
               return Boolean.TRUE;
            } else {
               ScriptableObject var42 = ensureScriptableObject(var41);
               if (var42.isExtensible()) {
                  return Boolean.FALSE;
               } else {
                  Object[] var43 = var42.getAllIds();

                  for(int var44 = var43.length; var8 < var44; var42 = var42) {
                     ScriptableObject var45 = var42.getOwnPropertyDescriptor(var2, var43[var8]);
                     Boolean var46 = Boolean.TRUE;
                     if (var46.equals(var45.get("configurable"))) {
                        return Boolean.FALSE;
                     }

                     if (var6.isDataDescriptor(var45) && Boolean.TRUE.equals(var45.get("writable"))) {
                        return Boolean.FALSE;
                     }

                     ++var8;
                  }

                  return Boolean.TRUE;
               }
            }
         case -10:
            Object var48;
            if (var5.length < var9) {
               var48 = Undefined.instance;
            } else {
               var48 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var48 instanceof ScriptableObject)) {
               return Boolean.TRUE;
            } else {
               ScriptableObject var49 = ensureScriptableObject(var48);
               if (var49.isExtensible()) {
                  return Boolean.FALSE;
               } else {
                  Object[] var50 = var49.getAllIds();

                  for(int var51 = var50.length; var8 < var51; ++var8) {
                     Object var52 = var49.getOwnPropertyDescriptor(var2, var50[var8]).get("configurable");
                     if (Boolean.TRUE.equals(var52)) {
                        return Boolean.FALSE;
                     }
                  }

                  return Boolean.TRUE;
               }
            }
         case -9:
            Object var53;
            if (var5.length < var9) {
               var53 = Undefined.instance;
            } else {
               var53 = var5[0];
            }

            Scriptable var54;
            if (var53 == null) {
               var54 = null;
            } else {
               var54 = ensureScriptable(var53);
            }

            NativeObject var55 = new NativeObject();
            var55.setParentScope(this.getParentScope());
            var55.setPrototype(var54);
            if (var5.length > var9 && var5[var9] != Undefined.instance) {
               var55.defineOwnProperties(var2, ensureScriptableObject(Context.toObject(var5[var9], this.getParentScope())));
            }

            return var55;
         case -8:
            Object var56;
            if (var5.length < var9) {
               var56 = Undefined.instance;
            } else {
               var56 = var5[0];
            }

            ScriptableObject var57 = ensureScriptableObject(var56);
            Object var58;
            if (var5.length < 2) {
               var58 = Undefined.instance;
            } else {
               var58 = var5[var9];
            }

            var57.defineOwnProperties(var2, ensureScriptableObject(Context.toObject(var58, this.getParentScope())));
            return var57;
         case -7:
            Object var59;
            if (var5.length < var9) {
               var59 = Undefined.instance;
            } else {
               var59 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var59 instanceof ScriptableObject)) {
               return var59;
            }

            ScriptableObject var60 = ensureScriptableObject(var59);
            var60.preventExtensions();
            return var60;
         case -6:
            Object var61;
            if (var5.length < var9) {
               var61 = Undefined.instance;
            } else {
               var61 = var5[0];
            }

            if (var2.getLanguageVersion() >= 200 && !(var61 instanceof ScriptableObject)) {
               return Boolean.FALSE;
            }

            return ensureScriptableObject(var61).isExtensible();
         case -5:
            Object var62;
            if (var5.length < var9) {
               var62 = Undefined.instance;
            } else {
               var62 = var5[0];
            }

            ScriptableObject var63 = ensureScriptableObject(var62);
            Object var64;
            if (var5.length < 2) {
               var64 = Undefined.instance;
            } else {
               var64 = var5[var9];
            }

            Object var65;
            if (var5.length < 3) {
               var65 = Undefined.instance;
            } else {
               var65 = var5[2];
            }

            var63.defineOwnProperty(var2, var64, ensureScriptableObject(var65));
            return var63;
         case -4:
            Object var66;
            if (var5.length < var9) {
               var66 = Undefined.instance;
            } else {
               var66 = var5[0];
            }

            ScriptableObject var67 = ensureScriptableObject(getCompatibleObject(var2, var3, var66));
            Object var68;
            if (var5.length < 2) {
               var68 = Undefined.instance;
            } else {
               var68 = var5[var9];
            }

            ScriptableObject var69 = var67.getOwnPropertyDescriptor(var2, var68);
            if (var69 == null) {
               return Undefined.instance;
            }

            return var69;
         case -3:
            Object var70;
            if (var5.length < var9) {
               var70 = Undefined.instance;
            } else {
               var70 = var5[0];
            }

            Object[] var71 = ensureScriptableObject(getCompatibleObject(var2, var3, var70)).getIds((boolean)var9, false);

            for(int var72 = 0; var72 < var71.length; ++var72) {
               var71[var72] = ScriptRuntime.toString(var71[var72]);
            }

            return var2.newArray(var3, var71);
         case -2:
            Object var73;
            if (var5.length < var9) {
               var73 = Undefined.instance;
            } else {
               var73 = var5[0];
            }

            Object[] var74 = getCompatibleObject(var2, var3, var73).getIds();

            for(int var75 = 0; var75 < var74.length; ++var75) {
               var74[var75] = ScriptRuntime.toString(var74[var75]);
            }

            return var2.newArray(var3, var74);
         case -1:
            Object var76;
            if (var5.length < var9) {
               var76 = Undefined.instance;
            } else {
               var76 = var5[0];
            }

            return getCompatibleObject(var2, var3, var76).getPrototype();
         case 0:
         default:
            throw new IllegalArgumentException(String.valueOf(var7));
         case 1:
            if (var4 != null) {
               return var1.construct(var2, var3, var5);
            } else {
               if (var5.length != 0 && var5[0] != null && var5[0] != Undefined.instance) {
                  return ScriptRuntime.toObject(var2, var3, var5[0]);
               }

               return new NativeObject();
            }
         case 2:
            if (var2.hasFeature(4)) {
               String var77 = ScriptRuntime.defaultObjectToSource(var2, var3, var4, var5);
               int var78 = var77.length();
               if (var78 != 0 && var77.charAt(0) == '(' && var77.charAt(var78 - 1) == ')') {
                  var77 = var77.substring(var9, var78 - 1);
               }

               return var77;
            }

            return ScriptRuntime.defaultObjectToString(var4);
         case 3:
            Object var79 = ScriptableObject.getProperty(var4, "toString");
            if (var79 instanceof Callable) {
               return ((Callable)var79).call(var2, var3, var4, ScriptRuntime.emptyArgs);
            }

            throw ScriptRuntime.notFunctionError(var79);
         case 4:
            return var4;
         case 5:
            Object var80;
            if (var5.length < var9) {
               var80 = Undefined.instance;
            } else {
               var80 = var5[0];
            }

            boolean var82;
            if (var80 instanceof Symbol) {
               var82 = ensureSymbolScriptable(var4).has((Symbol)var80, var4);
            } else {
               String var81 = ScriptRuntime.toStringIdOrIndex(var2, var80);
               if (var81 == null) {
                  var82 = var4.has(ScriptRuntime.lastIndexResult(var2), var4);
               } else {
                  var82 = var4.has(var81, var4);
               }
            }

            return ScriptRuntime.wrapBoolean(var82);
         case 6:
            Object var83;
            if (var5.length < var9) {
               var83 = Undefined.instance;
            } else {
               var83 = var5[0];
            }

            boolean var87;
            if (var83 instanceof Symbol) {
               var87 = ((SymbolScriptable)var4).has((Symbol)var83, var4);
               if (var87 && var4 instanceof ScriptableObject) {
                  int var95 = 2 & ((ScriptableObject)var4).getAttributes((Symbol)var83);
                  boolean var96 = false;
                  if (var95 == 0) {
                     var96 = true;
                  }

                  var87 = var96;
               }
            } else {
               String var85 = ScriptRuntime.toStringIdOrIndex(var2, var83);
               EvaluatorException var10000;
               boolean var10001;
               if (var85 == null) {
                  label597: {
                     int var91;
                     boolean var92;
                     try {
                        var91 = ScriptRuntime.lastIndexResult(var2);
                        var92 = var4.has(var91, var4);
                        var85 = Integer.toString(var91);
                     } catch (EvaluatorException var115) {
                        var10000 = var115;
                        var10001 = false;
                        break label597;
                     }

                     if (var92) {
                        label555: {
                           int var93;
                           try {
                              if (!(var4 instanceof ScriptableObject)) {
                                 break label555;
                              }

                              var93 = 2 & ((ScriptableObject)var4).getAttributes(var91);
                           } catch (EvaluatorException var114) {
                              var10000 = var114;
                              var10001 = false;
                              break label597;
                           }

                           boolean var94 = false;
                           if (var93 == 0) {
                              var94 = true;
                           }

                           var92 = var94;
                        }
                     }

                     var87 = var92;
                     return ScriptRuntime.wrapBoolean(var87);
                  }
               } else {
                  label598: {
                     try {
                        var87 = var4.has(var85, var4);
                     } catch (EvaluatorException var117) {
                        var10000 = var117;
                        var10001 = false;
                        break label598;
                     }

                     if (!var87) {
                        return ScriptRuntime.wrapBoolean(var87);
                     }

                     int var88;
                     try {
                        if (!(var4 instanceof ScriptableObject)) {
                           return ScriptRuntime.wrapBoolean(var87);
                        }

                        var88 = ((ScriptableObject)var4).getAttributes(var85);
                     } catch (EvaluatorException var116) {
                        var10000 = var116;
                        var10001 = false;
                        break label598;
                     }

                     int var89 = var88 & 2;
                     boolean var90 = false;
                     if (var89 == 0) {
                        var90 = true;
                     }

                     var87 = var90;
                     return ScriptRuntime.wrapBoolean(var87);
                  }
               }

               EvaluatorException var86 = var10000;
               if (!var86.getMessage().startsWith(ScriptRuntime.getMessage1("msg.prop.not.found", var85))) {
                  throw var86;
               }

               var87 = false;
            }

            return ScriptRuntime.wrapBoolean(var87);
         case 7:
            int var97 = var5.length;
            boolean var98 = false;
            if (var97 != 0) {
               boolean var99 = var5[0] instanceof Scriptable;
               var98 = false;
               if (var99) {
                  Scriptable var100 = (Scriptable)var5[0];

                  do {
                     var100 = var100.getPrototype();
                     if (var100 == var4) {
                        var98 = true;
                        break;
                     }

                     var98 = false;
                  } while(var100 != null);
               }
            }

            return ScriptRuntime.wrapBoolean(var98);
         case 8:
            return ScriptRuntime.defaultObjectToSource(var2, var3, var4, var5);
         case 9:
         case 10:
            if (var5.length >= 2 && var5[var9] instanceof Callable) {
               if (!(var4 instanceof ScriptableObject)) {
                  String var106;
                  if (var4 == null) {
                     var106 = "null";
                  } else {
                     var106 = var4.getClass().getName();
                  }

                  throw Context.reportRuntimeError2("msg.extend.scriptable", var106, String.valueOf(var5[0]));
               }

               ScriptableObject var102 = (ScriptableObject)var4;
               String var103 = ScriptRuntime.toStringIdOrIndex(var2, var5[0]);
               int var104;
               if (var103 != null) {
                  var104 = 0;
               } else {
                  var104 = ScriptRuntime.lastIndexResult(var2);
               }

               Callable var105 = (Callable)var5[var9];
               if (var7 != 10) {
                  var9 = 0;
               }

               var102.setGetterOrSetter(var103, var104, var105, (boolean)var9);
               if (var102 instanceof NativeArray) {
                  ((NativeArray)var102).setDenseOnly(false);
               }

               return Undefined.instance;
            }

            Object var101;
            if (var5.length >= 2) {
               var101 = var5[var9];
            } else {
               var101 = Undefined.instance;
            }

            throw ScriptRuntime.notFunctionError(var101);
         case 11:
         case 12:
            if (var5.length >= var9 && var4 instanceof ScriptableObject) {
               ScriptableObject var107 = (ScriptableObject)var4;
               String var108 = ScriptRuntime.toStringIdOrIndex(var2, var5[0]);
               int var109;
               if (var108 != null) {
                  var109 = 0;
               } else {
                  var109 = ScriptRuntime.lastIndexResult(var2);
               }

               boolean var110 = false;
               if (var7 == 12) {
                  var110 = true;
               }

               boolean var111 = var110;

               Object var112;
               while(true) {
                  var112 = var107.getGetterOrSetter(var108, var109, var111);
                  if (var112 != null) {
                     break;
                  }

                  Scriptable var113 = var107.getPrototype();
                  if (var113 == null || !(var113 instanceof ScriptableObject)) {
                     break;
                  }

                  var107 = (ScriptableObject)var113;
               }

               if (var112 != null) {
                  return var112;
               } else {
                  return Undefined.instance;
               }
            } else {
               return Undefined.instance;
            }
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      Object var2 = OBJECT_TAG;
      this.addIdFunctionProperty(var1, var2, -1, "getPrototypeOf", 1);
      this.addIdFunctionProperty(var1, var2, -2, "keys", 1);
      this.addIdFunctionProperty(var1, var2, -3, "getOwnPropertyNames", 1);
      this.addIdFunctionProperty(var1, var2, -14, "getOwnPropertySymbols", 1);
      this.addIdFunctionProperty(var1, var2, -4, "getOwnPropertyDescriptor", 2);
      this.addIdFunctionProperty(var1, var2, -5, "defineProperty", 3);
      this.addIdFunctionProperty(var1, var2, -6, "isExtensible", 1);
      this.addIdFunctionProperty(var1, var2, -7, "preventExtensions", 1);
      this.addIdFunctionProperty(var1, var2, -8, "defineProperties", 2);
      this.addIdFunctionProperty(var1, var2, -9, "create", 2);
      this.addIdFunctionProperty(var1, var2, -10, "isSealed", 1);
      this.addIdFunctionProperty(var1, var2, -11, "isFrozen", 1);
      this.addIdFunctionProperty(var1, var2, -12, "seal", 1);
      this.addIdFunctionProperty(var1, var2, -13, "freeze", 1);
      this.addIdFunctionProperty(var1, var2, -15, "assign", 2);
      this.addIdFunctionProperty(var1, var2, -16, "is", 2);
      super.fillConstructorProperties(var1);
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      String var3;
      byte var4;
      if (var2 != 7) {
         if (var2 != 8) {
            if (var2 != 11) {
               if (var2 != 16) {
                  if (var2 != 20) {
                     if (var2 != 13) {
                        if (var2 != 14) {
                           var4 = 0;
                           var3 = null;
                        } else {
                           char var9 = var1.charAt(0);
                           if (var9 == 'h') {
                              var3 = "hasOwnProperty";
                              var4 = 5;
                           } else {
                              var4 = 0;
                              var3 = null;
                              if (var9 == 't') {
                                 var3 = "toLocaleString";
                                 var4 = 3;
                              }
                           }
                        }
                     } else {
                        var3 = "isPrototypeOf";
                        var4 = 7;
                     }
                  } else {
                     var3 = "propertyIsEnumerable";
                     var4 = 6;
                  }
               } else {
                  char var6 = var1.charAt(2);
                  if (var6 == 'd') {
                     char var8 = var1.charAt(8);
                     if (var8 == 'G') {
                        var3 = "__defineGetter__";
                        var4 = 9;
                     } else {
                        var4 = 0;
                        var3 = null;
                        if (var8 == 'S') {
                           var3 = "__defineSetter__";
                           var4 = 10;
                        }
                     }
                  } else {
                     var4 = 0;
                     var3 = null;
                     if (var6 == 'l') {
                        char var7 = var1.charAt(8);
                        if (var7 == 'G') {
                           var3 = "__lookupGetter__";
                           var4 = 11;
                        } else {
                           var4 = 0;
                           var3 = null;
                           if (var7 == 'S') {
                              var3 = "__lookupSetter__";
                              var4 = 12;
                           }
                        }
                     }
                  }
               }
            } else {
               var3 = "constructor";
               var4 = 1;
            }
         } else {
            char var5 = var1.charAt(3);
            if (var5 == 'o') {
               var3 = "toSource";
               var4 = 8;
            } else {
               var4 = 0;
               var3 = null;
               if (var5 == 't') {
                  var3 = "toString";
                  var4 = 2;
               }
            }
         }
      } else {
         var3 = "valueOf";
         var4 = 4;
      }

      if (var3 != null && var3 != var1 && !var3.equals(var1)) {
         var4 = 0;
      }

      return var4;
   }

   public String getClassName() {
      return "Object";
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      switch(var1) {
      case 1:
         var2 = 1;
         var3 = "constructor";
         break;
      case 2:
         var3 = "toString";
         var2 = 0;
         break;
      case 3:
         var3 = "toLocaleString";
         var2 = 0;
         break;
      case 4:
         var3 = "valueOf";
         var2 = 0;
         break;
      case 5:
         var2 = 1;
         var3 = "hasOwnProperty";
         break;
      case 6:
         var2 = 1;
         var3 = "propertyIsEnumerable";
         break;
      case 7:
         var2 = 1;
         var3 = "isPrototypeOf";
         break;
      case 8:
         var3 = "toSource";
         var2 = 0;
         break;
      case 9:
         var2 = 2;
         var3 = "__defineGetter__";
         break;
      case 10:
         var2 = 2;
         var3 = "__defineSetter__";
         break;
      case 11:
         var2 = 1;
         var3 = "__lookupGetter__";
         break;
      case 12:
         var2 = 1;
         var3 = "__lookupSetter__";
         break;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }

      this.initPrototypeMethod(OBJECT_TAG, var1, var3, var2);
   }

   public Set keySet() {
      return new NativeObject.KeySet();
   }

   public Object put(Object var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map var1) {
      throw new UnsupportedOperationException();
   }

   public Object remove(Object var1) {
      Object var2 = this.get(var1);
      if (var1 instanceof String) {
         this.delete((String)var1);
         return var2;
      } else {
         if (var1 instanceof Number) {
            this.delete(((Number)var1).intValue());
         }

         return var2;
      }
   }

   public String toString() {
      return ScriptRuntime.defaultObjectToString(this);
   }

   public Collection values() {
      return new NativeObject.ValueCollection();
   }

   class EntrySet extends AbstractSet {
      public Iterator iterator() {
         return new Iterator() {
            Object[] ids = NativeObject.this.getIds();
            int index = 0;
            Object key = null;

            public boolean hasNext() {
               return this.index < this.ids.length;
            }

            public Entry next() {
               Object[] var1 = this.ids;
               int var2 = this.index++;
               final Object var3 = var1[var2];
               this.key = var3;
               return new Entry(NativeObject.this.get(this.key)) {
                  // $FF: synthetic field
                  final Object val$value;

                  {
                     this.val$value = var3x;
                  }

                  public boolean equals(Object var1) {
                     if (!(var1 instanceof Entry)) {
                        return false;
                     } else {
                        Entry var2 = (Entry)var1;
                        Object var3x = var3;
                        if (var3x == null) {
                           if (var2.getKey() != null) {
                              return false;
                           }
                        } else if (!var3x.equals(var2.getKey())) {
                           return false;
                        }

                        Object var4 = this.val$value;
                        if (var4 == null) {
                           if (var2.getValue() == null) {
                              return true;
                           }
                        } else if (var4.equals(var2.getValue())) {
                           return true;
                        }

                        return false;
                     }
                  }

                  public Object getKey() {
                     return var3;
                  }

                  public Object getValue() {
                     return this.val$value;
                  }

                  public int hashCode() {
                     Object var1 = var3;
                     int var2;
                     if (var1 == null) {
                        var2 = 0;
                     } else {
                        var2 = var1.hashCode();
                     }

                     Object var3x = this.val$value;
                     int var4;
                     if (var3x == null) {
                        var4 = 0;
                     } else {
                        var4 = var3x.hashCode();
                     }

                     return var2 ^ var4;
                  }

                  public Object setValue(Object var1) {
                     throw new UnsupportedOperationException();
                  }

                  public String toString() {
                     StringBuilder var1 = new StringBuilder();
                     var1.append(var3);
                     var1.append("=");
                     var1.append(this.val$value);
                     return var1.toString();
                  }
               };
            }

            public void remove() {
               if (this.key != null) {
                  NativeObject.this.remove(this.key);
                  this.key = null;
               } else {
                  throw new IllegalStateException();
               }
            }
         };
      }

      public int size() {
         return NativeObject.this.size();
      }
   }

   class KeySet extends AbstractSet {
      public boolean contains(Object var1) {
         return NativeObject.this.containsKey(var1);
      }

      public Iterator iterator() {
         return new Iterator() {
            Object[] ids = NativeObject.this.getIds();
            int index = 0;
            Object key;

            public boolean hasNext() {
               return this.index < this.ids.length;
            }

            public Object next() {
               try {
                  Object[] var2 = this.ids;
                  int var3 = this.index++;
                  Object var4 = var2[var3];
                  this.key = var4;
                  return var4;
               } catch (ArrayIndexOutOfBoundsException var5) {
                  this.key = null;
                  throw new NoSuchElementException();
               }
            }

            public void remove() {
               if (this.key != null) {
                  NativeObject.this.remove(this.key);
                  this.key = null;
               } else {
                  throw new IllegalStateException();
               }
            }
         };
      }

      public int size() {
         return NativeObject.this.size();
      }
   }

   class ValueCollection extends AbstractCollection {
      public Iterator iterator() {
         return new Iterator() {
            Object[] ids = NativeObject.this.getIds();
            int index = 0;
            Object key;

            public boolean hasNext() {
               return this.index < this.ids.length;
            }

            public Object next() {
               NativeObject var1 = NativeObject.this;
               Object[] var2 = this.ids;
               int var3 = this.index++;
               Object var4 = var2[var3];
               this.key = var4;
               return var1.get(var4);
            }

            public void remove() {
               if (this.key != null) {
                  NativeObject.this.remove(this.key);
                  this.key = null;
               } else {
                  throw new IllegalStateException();
               }
            }
         };
      }

      public int size() {
         return NativeObject.this.size();
      }
   }
}
