package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.mozilla.javascript.debug.DebuggableObject;

public abstract class ScriptableObject implements Scriptable, SymbolScriptable, Serializable, DebuggableObject, ConstProperties {
   // $FF: synthetic field
   static final boolean $assertionsDisabled = false;
   public static final int CONST = 13;
   public static final int DONTENUM = 2;
   public static final int EMPTY = 0;
   private static final Method GET_ARRAY_LENGTH;
   private static final Comparator KEY_COMPARATOR;
   public static final int PERMANENT = 4;
   public static final int READONLY = 1;
   public static final int UNINITIALIZED_CONST = 8;
   private static final long serialVersionUID = 2829861078851942586L;
   private volatile Map associatedValues;
   private transient ExternalArrayData externalData;
   private boolean isExtensible = true;
   private boolean isSealed = false;
   private Scriptable parentScopeObject;
   private Scriptable prototypeObject;
   private transient SlotMapContainer slotMap;

   static {
      try {
         GET_ARRAY_LENGTH = ScriptableObject.class.getMethod("getExternalArrayLength", new Class[0]);
      } catch (NoSuchMethodException var1) {
         throw new RuntimeException(var1);
      }

      KEY_COMPARATOR = new ScriptableObject.KeyComparator();
   }

   public ScriptableObject() {
      this.slotMap = this.createSlotMap(0);
   }

   public ScriptableObject(Scriptable var1, Scriptable var2) {
      if (var1 != null) {
         this.parentScopeObject = var1;
         this.prototypeObject = var2;
         this.slotMap = this.createSlotMap(0);
      } else {
         throw new IllegalArgumentException();
      }
   }

   static BaseFunction buildClassCtor(Scriptable var0, Class var1, boolean var2, boolean var3) throws IllegalAccessException, InstantiationException, InvocationTargetException {
      Method[] var4 = FunctionObject.getMethodList(var1);

      for(int var5 = 0; var5 < var4.length; ++var5) {
         Method var67 = var4[var5];
         if (var67.getName().equals("init")) {
            Class[] var68 = var67.getParameterTypes();
            if (var68.length == 3 && var68[0] == ScriptRuntime.ContextClass && var68[1] == ScriptRuntime.ScriptableClass && var68[2] == Boolean.TYPE && Modifier.isStatic(var67.getModifiers())) {
               Object[] var70 = new Object[]{Context.getContext(), var0, null};
               Boolean var71;
               if (var2) {
                  var71 = Boolean.TRUE;
               } else {
                  var71 = Boolean.FALSE;
               }

               var70[2] = var71;
               var67.invoke((Object)null, var70);
               return null;
            }

            if (var68.length == 1 && var68[0] == ScriptRuntime.ScriptableClass && Modifier.isStatic(var67.getModifiers())) {
               var67.invoke((Object)null, new Object[]{var0});
               return null;
            }
         }
      }

      Constructor[] var6 = var1.getConstructors();
      int var7 = 0;

      Constructor var9;
      while(true) {
         int var8 = var6.length;
         var9 = null;
         if (var7 >= var8) {
            break;
         }

         if (var6[var7].getParameterTypes().length == 0) {
            var9 = var6[var7];
            break;
         }

         ++var7;
      }

      if (var9 == null) {
         EvaluatorException var10 = Context.reportRuntimeError1("msg.zero.arg.ctor", var1.getName());
         throw var10;
      } else {
         Scriptable var11 = (Scriptable)var9.newInstance(ScriptRuntime.emptyArgs);
         String var12 = var11.getClassName();
         Object var13 = getProperty(getTopLevelScope(var0), var12);
         if (var13 instanceof BaseFunction) {
            Object var66 = ((BaseFunction)var13).getPrototypeProperty();
            if (var66 != null && var1.equals(var66.getClass())) {
               return (BaseFunction)var13;
            }
         }

         Scriptable var14 = null;
         if (var3) {
            Class var62 = var1.getSuperclass();
            boolean var63 = ScriptRuntime.ScriptableClass.isAssignableFrom(var62);
            var14 = null;
            if (var63) {
               boolean var64 = Modifier.isAbstract(var62.getModifiers());
               var14 = null;
               if (!var64) {
                  String var65 = defineClass(var0, extendsScriptable(var62), var2, var3);
                  var14 = null;
                  if (var65 != null) {
                     var14 = getClassPrototype(var0, var65);
                  }
               }
            }
         }

         if (var14 == null) {
            var14 = getObjectPrototype(var0);
         }

         var11.setPrototype(var14);
         String var15 = "jsStaticFunction_";
         String var16 = "jsGet_";
         Object var17 = findAnnotatedMember(var4, JSConstructor.class);
         if (var17 == null) {
            var17 = findAnnotatedMember(var6, JSConstructor.class);
         }

         String var18 = "jsConstructor";
         if (var17 == null) {
            var17 = FunctionObject.findSingleMethod(var4, var18);
         }

         if (var17 == null) {
            int var60 = var6.length;
            if (var60 == 1) {
               var17 = var6[0];
            } else if (var6.length == 2) {
               if (var6[0].getParameterTypes().length == 0) {
                  var17 = var6[1];
               } else if (var6[1].getParameterTypes().length == 0) {
                  var17 = var6[0];
               }
            }

            if (var17 == null) {
               throw Context.reportRuntimeError1("msg.ctor.multiple.parms", var1.getName());
            }
         }

         FunctionObject var20 = new FunctionObject(var12, (Member)var17, var0);
         if (var20.isVarArgsMethod()) {
            throw Context.reportRuntimeError1("msg.varargs.ctor", ((Member)var17).getName());
         } else {
            var20.initAsConstructor(var0, var11);
            HashSet var21 = new HashSet();
            HashSet var22 = new HashSet();
            int var23 = var4.length;
            Method var24 = null;

            String var45;
            for(int var25 = 0; var25 < var23; var16 = var45) {
               Method var33 = var4[var25];
               String var35;
               String var36;
               Object var37;
               String var41;
               Scriptable var42;
               if (var33 == var17) {
                  var35 = var15;
                  var41 = var18;
                  var36 = var12;
                  var37 = var13;
                  var42 = var14;
                  var45 = var16;
               } else {
                  label252: {
                     String var34 = var33.getName();
                     var35 = var15;
                     if (var34.equals("finishInit")) {
                        Class[] var58 = var33.getParameterTypes();
                        var36 = var12;
                        int var59 = var58.length;
                        var37 = var13;
                        if (var59 == 3 && var58[0] == ScriptRuntime.ScriptableClass && var58[1] == FunctionObject.class && var58[2] == ScriptRuntime.ScriptableClass && Modifier.isStatic(var33.getModifiers())) {
                           var24 = var33;
                           var41 = var18;
                           var42 = var14;
                           var45 = var16;
                           break label252;
                        }
                     } else {
                        var36 = var12;
                        var37 = var13;
                     }

                     if (var34.indexOf(36) != -1) {
                        var41 = var18;
                        var42 = var14;
                        var45 = var16;
                     } else if (var34.equals(var18)) {
                        var41 = var18;
                        var42 = var14;
                        var45 = var16;
                     } else {
                        label256: {
                           String var38 = null;
                           Annotation var40;
                           if (var33.isAnnotationPresent(JSFunction.class)) {
                              var40 = var33.getAnnotation(JSFunction.class);
                           } else if (var33.isAnnotationPresent(JSStaticFunction.class)) {
                              var40 = var33.getAnnotation(JSStaticFunction.class);
                           } else if (var33.isAnnotationPresent(JSGetter.class)) {
                              var40 = var33.getAnnotation(JSGetter.class);
                           } else {
                              boolean var39 = var33.isAnnotationPresent(JSSetter.class);
                              var40 = null;
                              if (var39) {
                                 var41 = var18;
                                 var42 = var14;
                                 var45 = var16;
                                 break label256;
                              }
                           }

                           var41 = var18;
                           if (var40 == null) {
                              if (var34.startsWith("jsFunction_")) {
                                 var38 = "jsFunction_";
                              } else if (var34.startsWith("jsStaticFunction_")) {
                                 var38 = "jsStaticFunction_";
                              } else {
                                 if (!var34.startsWith("jsGet_")) {
                                    var42 = var14;
                                    var45 = var16;
                                    break label256;
                                 }

                                 var38 = "jsGet_";
                              }
                           }

                           var42 = var14;
                           boolean var43;
                           if (!(var40 instanceof JSStaticFunction) && var38 != "jsStaticFunction_") {
                              var43 = false;
                           } else {
                              var43 = true;
                           }

                           HashSet var44;
                           if (var43) {
                              var44 = var21;
                           } else {
                              var44 = var22;
                           }

                           var45 = var16;
                           String var46 = getPropertyName(var34, var38, var40);
                           if (var44.contains(var46)) {
                              throw Context.reportRuntimeError2("duplicate.defineClass.name", var34, var46);
                           }

                           var44.add(var46);
                           if (!(var40 instanceof JSGetter)) {
                              if (var38 != "jsGet_") {
                                 if (var43 && !Modifier.isStatic(var33.getModifiers())) {
                                    throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
                                 }

                                 FunctionObject var53 = new FunctionObject(var46, var33, var11);
                                 if (var53.isVarArgsConstructor()) {
                                    throw Context.reportRuntimeError1("msg.varargs.fun", ((Member)var17).getName());
                                 }

                                 Object var55;
                                 if (var43) {
                                    var55 = var20;
                                 } else {
                                    var55 = var11;
                                 }

                                 defineProperty((Scriptable)var55, var46, var53, 2);
                                 if (var2) {
                                    var53.sealObject();
                                 }
                                 break label256;
                              }
                           }

                           if (!(var11 instanceof ScriptableObject)) {
                              throw Context.reportRuntimeError2("msg.extend.scriptable", var11.getClass().toString(), var46);
                           }

                           Method var50 = findSetterMethod(var4, var46, "jsSet_");
                           byte var51;
                           if (var50 != null) {
                              var51 = 0;
                           } else {
                              var51 = 1;
                           }

                           int var52 = var51 | 6;
                           ((ScriptableObject)var11).defineProperty(var46, (Object)null, var33, var50, var52);
                        }
                     }
                  }
               }

               ++var25;
               var23 = var23;
               var15 = var35;
               var12 = var36;
               var13 = var37;
               var18 = var41;
               var14 = var42;
            }

            if (var24 != null) {
               var24.invoke((Object)null, new Object[]{var0, var20, var11});
            }

            if (var2) {
               var20.sealObject();
               if (var11 instanceof ScriptableObject) {
                  ((ScriptableObject)var11).sealObject();
               }
            }

            return var20;
         }
      }
   }

   protected static ScriptableObject buildDataDescriptor(Scriptable var0, Object var1, int var2) {
      NativeObject var3 = new NativeObject();
      ScriptRuntime.setBuiltinProtoAndParent(var3, var0, TopLevel.Builtins.Object);
      var3.defineProperty((String)"value", (Object)var1, 0);
      int var4 = var2 & 1;
      boolean var5 = true;
      boolean var6;
      if (var4 == 0) {
         var6 = true;
      } else {
         var6 = false;
      }

      var3.defineProperty((String)"writable", (Object)var6, 0);
      boolean var7;
      if ((var2 & 2) == 0) {
         var7 = true;
      } else {
         var7 = false;
      }

      var3.defineProperty((String)"enumerable", (Object)var7, 0);
      if ((var2 & 4) != 0) {
         var5 = false;
      }

      var3.defineProperty((String)"configurable", (Object)var5, 0);
      return var3;
   }

   public static Object callMethod(Context var0, Scriptable var1, String var2, Object[] var3) {
      Object var4 = getProperty(var1, var2);
      if (var4 instanceof Function) {
         Function var5 = (Function)var4;
         Scriptable var6 = getTopLevelScope(var1);
         return var0 != null ? var5.call(var0, var6, var1, var3) : Context.call((ContextFactory)null, var5, var6, var1, var3);
      } else {
         throw ScriptRuntime.notFunctionError(var1, var2);
      }
   }

   public static Object callMethod(Scriptable var0, String var1, Object[] var2) {
      return callMethod((Context)null, var0, var1, var2);
   }

   private void checkNotSealed(Object var1, int var2) {
      if (this.isSealed()) {
         String var3;
         if (var1 != null) {
            var3 = var1.toString();
         } else {
            var3 = Integer.toString(var2);
         }

         throw Context.reportRuntimeError1("msg.modify.sealed", var3);
      }
   }

   static void checkValidAttributes(int var0) {
      if ((var0 & -16) != 0) {
         throw new IllegalArgumentException(String.valueOf(var0));
      }
   }

   private SlotMapContainer createSlotMap(int var1) {
      Context var2 = Context.getCurrentContext();
      return (SlotMapContainer)(var2 != null && var2.hasFeature(17) ? new ThreadSafeSlotMapContainer(var1) : new SlotMapContainer(var1));
   }

   public static String defineClass(Scriptable var0, Class var1, boolean var2, boolean var3) throws IllegalAccessException, InstantiationException, InvocationTargetException {
      BaseFunction var4 = buildClassCtor(var0, var1, var2, var3);
      if (var4 == null) {
         return null;
      } else {
         String var5 = var4.getClassPrototype().getClassName();
         defineProperty(var0, var5, var4, 2);
         return var5;
      }
   }

   public static void defineClass(Scriptable var0, Class var1) throws IllegalAccessException, InstantiationException, InvocationTargetException {
      defineClass(var0, var1, false, false);
   }

   public static void defineClass(Scriptable var0, Class var1, boolean var2) throws IllegalAccessException, InstantiationException, InvocationTargetException {
      defineClass(var0, var1, var2, false);
   }

   public static void defineConstProperty(Scriptable var0, String var1) {
      if (var0 instanceof ConstProperties) {
         ((ConstProperties)var0).defineConst(var1, var0);
      } else {
         defineProperty(var0, var1, Undefined.instance, 13);
      }
   }

   public static void defineProperty(Scriptable var0, String var1, Object var2, int var3) {
      if (!(var0 instanceof ScriptableObject)) {
         var0.put(var1, var0, var2);
      } else {
         ((ScriptableObject)var0).defineProperty(var1, var2, var3);
      }
   }

   public static boolean deleteProperty(Scriptable var0, int var1) {
      Scriptable var2 = getBase(var0, var1);
      if (var2 == null) {
         return true;
      } else {
         var2.delete(var1);
         return true ^ var2.has(var1, var0);
      }
   }

   public static boolean deleteProperty(Scriptable var0, String var1) {
      Scriptable var2 = getBase(var0, var1);
      if (var2 == null) {
         return true;
      } else {
         var2.delete(var1);
         return true ^ var2.has(var1, var0);
      }
   }

   protected static Scriptable ensureScriptable(Object var0) {
      if (var0 instanceof Scriptable) {
         return (Scriptable)var0;
      } else {
         throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(var0));
      }
   }

   protected static ScriptableObject ensureScriptableObject(Object var0) {
      if (var0 instanceof ScriptableObject) {
         return (ScriptableObject)var0;
      } else {
         throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(var0));
      }
   }

   protected static SymbolScriptable ensureSymbolScriptable(Object var0) {
      if (var0 instanceof SymbolScriptable) {
         return (SymbolScriptable)var0;
      } else {
         throw ScriptRuntime.typeError1("msg.object.not.symbolscriptable", ScriptRuntime.typeof(var0));
      }
   }

   private static Class extendsScriptable(Class var0) {
      return ScriptRuntime.ScriptableClass.isAssignableFrom(var0) ? var0 : null;
   }

   private static Member findAnnotatedMember(AccessibleObject[] var0, Class var1) {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AccessibleObject var4 = var0[var3];
         if (var4.isAnnotationPresent(var1)) {
            return (Member)var4;
         }
      }

      return null;
   }

   private ScriptableObject.Slot findAttributeSlot(String var1, int var2, ScriptableObject.SlotAccess var3) {
      ScriptableObject.Slot var4 = this.slotMap.get(var1, var2, var3);
      if (var4 == null) {
         String var5;
         if (var1 != null) {
            var5 = var1;
         } else {
            var5 = Integer.toString(var2);
         }

         throw Context.reportRuntimeError1("msg.prop.not.found", var5);
      } else {
         return var4;
      }
   }

   private ScriptableObject.Slot findAttributeSlot(Symbol var1, ScriptableObject.SlotAccess var2) {
      ScriptableObject.Slot var3 = this.slotMap.get(var1, 0, var2);
      if (var3 != null) {
         return var3;
      } else {
         throw Context.reportRuntimeError1("msg.prop.not.found", var1);
      }
   }

   private static Method findSetterMethod(Method[] var0, String var1, String var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("set");
      int var5 = 0;
      var3.append(Character.toUpperCase(var1.charAt(0)));
      var3.append(var1.substring(1));
      String var8 = var3.toString();
      int var9 = var0.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Method var17 = var0[var10];
         JSSetter var18 = (JSSetter)var17.getAnnotation(JSSetter.class);
         if (var18 != null && (var1.equals(var18.value()) || "".equals(var18.value()) && var8.equals(var17.getName()))) {
            return var17;
         }
      }

      StringBuilder var11 = new StringBuilder();
      var11.append(var2);
      var11.append(var1);
      String var14 = var11.toString();

      for(int var15 = var0.length; var5 < var15; ++var5) {
         Method var16 = var0[var5];
         if (var14.equals(var16.getName())) {
            return var16;
         }
      }

      return null;
   }

   public static Scriptable getArrayPrototype(Scriptable var0) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(var0), TopLevel.Builtins.Array);
   }

   private static Scriptable getBase(Scriptable var0, int var1) {
      do {
         if (var0.has(var1, var0)) {
            return var0;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var0;
   }

   private static Scriptable getBase(Scriptable var0, String var1) {
      do {
         if (var0.has(var1, var0)) {
            return var0;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var0;
   }

   private static Scriptable getBase(Scriptable var0, Symbol var1) {
      do {
         if (ensureSymbolScriptable(var0).has(var1, var0)) {
            return var0;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var0;
   }

   public static Scriptable getClassPrototype(Scriptable var0, String var1) {
      Object var2 = getProperty(getTopLevelScope(var0), var1);
      Object var4;
      if (var2 instanceof BaseFunction) {
         var4 = ((BaseFunction)var2).getPrototypeProperty();
      } else {
         if (!(var2 instanceof Scriptable)) {
            return null;
         }

         Scriptable var3 = (Scriptable)var2;
         var4 = var3.get("prototype", var3);
      }

      return var4 instanceof Scriptable ? (Scriptable)var4 : null;
   }

   public static Object getDefaultValue(Scriptable var0, Class var1) {
      Context var2 = null;

      for(int var3 = 0; var3 < 2; ++var3) {
         boolean var7;
         if (var1 == ScriptRuntime.StringClass) {
            boolean var6 = false;
            if (var3 == 0) {
               var6 = true;
            }

            var7 = var6;
         } else {
            boolean var13 = false;
            if (var3 == 1) {
               var13 = true;
            }

            var7 = var13;
         }

         String var8;
         if (var7) {
            var8 = "toString";
         } else {
            var8 = "valueOf";
         }

         Object var9 = getProperty(var0, var8);
         if (var9 instanceof Function) {
            Function var10 = (Function)var9;
            if (var2 == null) {
               var2 = Context.getContext();
            }

            Object var11 = var10.call(var2, var10.getParentScope(), var0, ScriptRuntime.emptyArgs);
            if (var11 != null) {
               if (!(var11 instanceof Scriptable)) {
                  return var11;
               }

               if (var1 == ScriptRuntime.ScriptableClass) {
                  return var11;
               }

               if (var1 == ScriptRuntime.FunctionClass) {
                  return var11;
               }

               if (var7 && var11 instanceof Wrapper) {
                  Object var12 = ((Wrapper)var11).unwrap();
                  if (var12 instanceof String) {
                     return var12;
                  }
               }
            }
         }
      }

      String var4;
      if (var1 == null) {
         var4 = "undefined";
      } else {
         var4 = var1.getName();
      }

      EcmaError var5 = ScriptRuntime.typeError1("msg.default.value", var4);
      throw var5;
   }

   public static Scriptable getFunctionPrototype(Scriptable var0) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(var0), TopLevel.Builtins.Function);
   }

   public static Scriptable getObjectPrototype(Scriptable var0) {
      return TopLevel.getBuiltinPrototype(getTopLevelScope(var0), TopLevel.Builtins.Object);
   }

   public static Object getProperty(Scriptable var0, int var1) {
      Scriptable var2 = var0;

      Object var3;
      do {
         var3 = var0.get(var1, var2);
         if (var3 != Scriptable.NOT_FOUND) {
            return var3;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var3;
   }

   public static Object getProperty(Scriptable var0, String var1) {
      Scriptable var2 = var0;

      Object var3;
      do {
         var3 = var0.get(var1, var2);
         if (var3 != Scriptable.NOT_FOUND) {
            return var3;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var3;
   }

   public static Object getProperty(Scriptable var0, Symbol var1) {
      Scriptable var2 = var0;

      Object var3;
      do {
         var3 = ensureSymbolScriptable(var0).get(var1, var2);
         if (var3 != Scriptable.NOT_FOUND) {
            return var3;
         }

         var0 = var0.getPrototype();
      } while(var0 != null);

      return var3;
   }

   public static Object[] getPropertyIds(Scriptable var0) {
      if (var0 == null) {
         return ScriptRuntime.emptyArgs;
      } else {
         Object[] var1 = var0.getIds();
         ObjToIntMap var2 = null;

         while(true) {
            while(true) {
               Object[] var3;
               do {
                  var0 = var0.getPrototype();
                  if (var0 == null) {
                     if (var2 != null) {
                        var1 = var2.getKeys();
                     }

                     return var1;
                  }

                  var3 = var0.getIds();
               } while(var3.length == 0);

               if (var2 == null) {
                  if (var1.length == 0) {
                     var1 = var3;
                     continue;
                  }

                  var2 = new ObjToIntMap(var1.length + var3.length);

                  for(int var6 = 0; var6 != var1.length; ++var6) {
                     var2.intern(var1[var6]);
                  }

                  var1 = null;
               }

               for(int var4 = 0; var4 != var3.length; ++var4) {
                  var2.intern(var3[var4]);
               }
            }
         }
      }
   }

   private static String getPropertyName(String var0, String var1, Annotation var2) {
      if (var1 != null) {
         return var0.substring(var1.length());
      } else {
         String var4;
         if (var2 instanceof JSGetter) {
            var4 = ((JSGetter)var2).value();
            if ((var4 == null || var4.length() == 0) && var0.length() > 3 && var0.startsWith("get")) {
               var4 = var0.substring(3);
               if (Character.isUpperCase(var4.charAt(0))) {
                  if (var4.length() == 1) {
                     var4 = var4.toLowerCase();
                  } else if (!Character.isUpperCase(var4.charAt(1))) {
                     StringBuilder var5 = new StringBuilder();
                     var5.append(Character.toLowerCase(var4.charAt(0)));
                     var5.append(var4.substring(1));
                     var4 = var5.toString();
                  }
               }
            }
         } else if (var2 instanceof JSFunction) {
            var4 = ((JSFunction)var2).value();
         } else {
            boolean var3 = var2 instanceof JSStaticFunction;
            var4 = null;
            if (var3) {
               var4 = ((JSStaticFunction)var2).value();
            }
         }

         if (var4 == null || var4.length() == 0) {
            var4 = var0;
         }

         return var4;
      }
   }

   public static Scriptable getTopLevelScope(Scriptable var0) {
      while(true) {
         Scriptable var1 = var0.getParentScope();
         if (var1 == null) {
            return var0;
         }

         var0 = var1;
      }
   }

   public static Object getTopScopeValue(Scriptable var0, Object var1) {
      Scriptable var2 = getTopLevelScope(var0);

      do {
         if (var2 instanceof ScriptableObject) {
            Object var3 = ((ScriptableObject)var2).getAssociatedValue(var1);
            if (var3 != null) {
               return var3;
            }
         }

         var2 = var2.getPrototype();
      } while(var2 != null);

      return null;
   }

   public static Object getTypedProperty(Scriptable var0, int var1, Class var2) {
      Object var3 = getProperty(var0, var1);
      if (var3 == Scriptable.NOT_FOUND) {
         var3 = null;
      }

      return var2.cast(Context.jsToJava(var3, var2));
   }

   public static Object getTypedProperty(Scriptable var0, String var1, Class var2) {
      Object var3 = getProperty(var0, var1);
      if (var3 == Scriptable.NOT_FOUND) {
         var3 = null;
      }

      return var2.cast(Context.jsToJava(var3, var2));
   }

   public static boolean hasProperty(Scriptable var0, int var1) {
      return getBase(var0, var1) != null;
   }

   public static boolean hasProperty(Scriptable var0, String var1) {
      return getBase(var0, var1) != null;
   }

   public static boolean hasProperty(Scriptable var0, Symbol var1) {
      return getBase(var0, var1) != null;
   }

   protected static boolean isFalse(Object var0) {
      return true ^ isTrue(var0);
   }

   protected static boolean isTrue(Object var0) {
      return var0 != NOT_FOUND && ScriptRuntime.toBoolean(var0);
   }

   private boolean putConstImpl(String var1, int var2, Scriptable var3, Object var4, int var5) {
      if (var5 != 0) {
         if (!this.isExtensible && Context.getContext().isStrictMode()) {
            throw ScriptRuntime.typeError0("msg.not.extensible");
         } else {
            ScriptableObject.Slot var8;
            if (this != var3) {
               var8 = this.slotMap.query(var1, var2);
               if (var8 == null) {
                  return false;
               }
            } else {
               if (this.isExtensible()) {
                  this.checkNotSealed(var1, var2);
                  ScriptableObject.Slot var6 = this.slotMap.get(var1, var2, ScriptableObject.SlotAccess.MODIFY_CONST);
                  int var7 = var6.getAttributes();
                  if ((var7 & 1) != 0) {
                     if ((var7 & 8) != 0) {
                        var6.value = var4;
                        if (var5 != 8) {
                           var6.setAttributes(var7 & -9);
                        }
                     }

                     return true;
                  }

                  throw Context.reportRuntimeError1("msg.var.redecl", var1);
               }

               var8 = this.slotMap.query(var1, var2);
               if (var8 == null) {
                  return true;
               }
            }

            return var8.setValue(var4, this, var3);
         }
      } else {
         throw new AssertionError();
      }
   }

   public static void putConstProperty(Scriptable var0, String var1, Object var2) {
      Scriptable var3 = getBase(var0, var1);
      if (var3 == null) {
         var3 = var0;
      }

      if (var3 instanceof ConstProperties) {
         ((ConstProperties)var3).putConst(var1, var0, var2);
      }

   }

   private boolean putImpl(Object var1, int var2, Scriptable var3, Object var4) {
      if (!this.isExtensible && Context.getContext().isStrictMode()) {
         throw ScriptRuntime.typeError0("msg.not.extensible");
      } else {
         ScriptableObject.Slot var5;
         if (this != var3) {
            var5 = this.slotMap.query(var1, var2);
            if (var5 == null) {
               return false;
            }
         } else if (!this.isExtensible) {
            var5 = this.slotMap.query(var1, var2);
            if (var5 == null) {
               return true;
            }
         } else {
            if (this.isSealed) {
               this.checkNotSealed(var1, var2);
            }

            var5 = this.slotMap.get(var1, var2, ScriptableObject.SlotAccess.MODIFY);
         }

         return var5.setValue(var4, this, var3);
      }
   }

   public static void putProperty(Scriptable var0, int var1, Object var2) {
      Scriptable var3 = getBase(var0, var1);
      if (var3 == null) {
         var3 = var0;
      }

      var3.put(var1, var0, var2);
   }

   public static void putProperty(Scriptable var0, String var1, Object var2) {
      Scriptable var3 = getBase(var0, var1);
      if (var3 == null) {
         var3 = var0;
      }

      var3.put(var1, var0, var2);
   }

   public static void putProperty(Scriptable var0, Symbol var1, Object var2) {
      Scriptable var3 = getBase(var0, var1);
      if (var3 == null) {
         var3 = var0;
      }

      ensureSymbolScriptable(var3).put(var1, var0, var2);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      this.slotMap = this.createSlotMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         ScriptableObject.Slot var4 = (ScriptableObject.Slot)var1.readObject();
         this.slotMap.addSlot(var4);
      }

   }

   public static void redefineProperty(Scriptable var0, String var1, boolean var2) {
      Scriptable var3 = getBase(var0, var1);
      if (var3 != null) {
         if (var3 instanceof ConstProperties && ((ConstProperties)var3).isConst(var1)) {
            throw ScriptRuntime.typeError1("msg.const.redecl", var1);
         } else if (var2) {
            throw ScriptRuntime.typeError1("msg.var.redecl", var1);
         }
      }
   }

   private void setGetterOrSetter(String var1, int var2, Callable var3, boolean var4, boolean var5) {
      if (var1 != null && var2 != 0) {
         throw new IllegalArgumentException(var1);
      } else {
         if (!var5) {
            this.checkNotSealed(var1, var2);
         }

         ScriptableObject.GetterSlot var7;
         if (this.isExtensible()) {
            var7 = (ScriptableObject.GetterSlot)this.slotMap.get(var1, var2, ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER);
         } else {
            ScriptableObject.Slot var6 = this.slotMap.query(var1, var2);
            if (!(var6 instanceof ScriptableObject.GetterSlot)) {
               return;
            }

            var7 = (ScriptableObject.GetterSlot)var6;
         }

         if (!var5 && (1 & var7.getAttributes()) != 0) {
            throw Context.reportRuntimeError1("msg.modify.readonly", var1);
         } else {
            if (var4) {
               var7.setter = var3;
            } else {
               var7.getter = var3;
            }

            var7.value = Undefined.instance;
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      long var2 = this.slotMap.readLock();

      label190: {
         Throwable var10000;
         label189: {
            boolean var10001;
            int var5;
            try {
               var5 = this.slotMap.dirtySize();
            } catch (Throwable var26) {
               var10000 = var26;
               var10001 = false;
               break label189;
            }

            if (var5 == 0) {
               label178:
               try {
                  var1.writeInt(0);
                  break label190;
               } catch (Throwable var23) {
                  var10000 = var23;
                  var10001 = false;
                  break label178;
               }
            } else {
               label185: {
                  Iterator var6;
                  try {
                     var1.writeInt(var5);
                     var6 = this.slotMap.iterator();
                  } catch (Throwable var25) {
                     var10000 = var25;
                     var10001 = false;
                     break label185;
                  }

                  while(true) {
                     try {
                        if (!var6.hasNext()) {
                           break label190;
                        }

                        var1.writeObject((ScriptableObject.Slot)var6.next());
                     } catch (Throwable var24) {
                        var10000 = var24;
                        var10001 = false;
                        break;
                     }
                  }
               }
            }
         }

         Throwable var4 = var10000;
         this.slotMap.unlockRead(var2);
         throw var4;
      }

      this.slotMap.unlockRead(var2);
   }

   void addLazilyInitializedValue(String var1, int var2, LazilyLoadedCtor var3, int var4) {
      if (var1 != null && var2 != 0) {
         throw new IllegalArgumentException(var1);
      } else {
         this.checkNotSealed(var1, var2);
         ScriptableObject.GetterSlot var5 = (ScriptableObject.GetterSlot)this.slotMap.get(var1, var2, ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER);
         var5.setAttributes(var4);
         var5.getter = null;
         var5.setter = null;
         var5.value = var3;
      }
   }

   protected int applyDescriptorToAttributeBitset(int var1, ScriptableObject var2) {
      Object var3 = getProperty(var2, (String)"enumerable");
      if (var3 != NOT_FOUND) {
         int var8;
         if (ScriptRuntime.toBoolean(var3)) {
            var8 = var1 & -3;
         } else {
            var8 = var1 | 2;
         }

         var1 = var8;
      }

      Object var4 = getProperty(var2, (String)"writable");
      if (var4 != NOT_FOUND) {
         int var7;
         if (ScriptRuntime.toBoolean(var4)) {
            var7 = var1 & -2;
         } else {
            var7 = var1 | 1;
         }

         var1 = var7;
      }

      Object var5 = getProperty(var2, (String)"configurable");
      if (var5 != NOT_FOUND) {
         int var6;
         if (ScriptRuntime.toBoolean(var5)) {
            var6 = var1 & -5;
         } else {
            var6 = var1 | 4;
         }

         var1 = var6;
      }

      return var1;
   }

   public final Object associateValue(Object var1, Object var2) {
      synchronized(this){}
      Throwable var10000;
      boolean var10001;
      if (var2 != null) {
         label156: {
            Object var4;
            try {
               var4 = this.associatedValues;
            } catch (Throwable var25) {
               var10000 = var25;
               var10001 = false;
               break label156;
            }

            if (var4 == null) {
               try {
                  var4 = new HashMap();
                  this.associatedValues = (Map)var4;
               } catch (Throwable var24) {
                  var10000 = var24;
                  var10001 = false;
                  break label156;
               }
            }

            Object var5;
            try {
               var5 = Kit.initHash((Map)var4, var1, var2);
            } catch (Throwable var23) {
               var10000 = var23;
               var10001 = false;
               break label156;
            }

            return var5;
         }
      } else {
         label153:
         try {
            throw new IllegalArgumentException();
         } catch (Throwable var26) {
            var10000 = var26;
            var10001 = false;
            break label153;
         }
      }

      Throwable var3 = var10000;
      throw var3;
   }

   public boolean avoidObjectDetection() {
      return false;
   }

   protected void checkPropertyChange(Object var1, ScriptableObject var2, ScriptableObject var3) {
      if (var2 == null) {
         if (!this.isExtensible()) {
            throw ScriptRuntime.typeError0("msg.not.extensible");
         }
      } else {
         if (isFalse(var2.get((String)"configurable", var2))) {
            if (isTrue(getProperty(var3, (String)"configurable"))) {
               throw ScriptRuntime.typeError1("msg.change.configurable.false.to.true", var1);
            }

            if (isTrue(var2.get((String)"enumerable", var2)) != isTrue(getProperty(var3, (String)"enumerable"))) {
               throw ScriptRuntime.typeError1("msg.change.enumerable.with.configurable.false", var1);
            }

            boolean var4 = this.isDataDescriptor(var3);
            boolean var5 = this.isAccessorDescriptor(var3);
            if (!var4 && !var5) {
               return;
            }

            if (!var4 || !this.isDataDescriptor(var2)) {
               if (var5 && this.isAccessorDescriptor(var2)) {
                  if (this.sameValue(getProperty(var3, (String)"set"), var2.get((String)"set", var2))) {
                     if (this.sameValue(getProperty(var3, (String)"get"), var2.get((String)"get", var2))) {
                        return;
                     } else {
                        throw ScriptRuntime.typeError1("msg.change.getter.with.configurable.false", var1);
                     }
                  } else {
                     throw ScriptRuntime.typeError1("msg.change.setter.with.configurable.false", var1);
                  }
               } else if (this.isDataDescriptor(var2)) {
                  throw ScriptRuntime.typeError1("msg.change.property.data.to.accessor.with.configurable.false", var1);
               } else {
                  throw ScriptRuntime.typeError1("msg.change.property.accessor.to.data.with.configurable.false", var1);
               }
            }

            if (isFalse(var2.get((String)"writable", var2))) {
               if (!isTrue(getProperty(var3, (String)"writable"))) {
                  if (this.sameValue(getProperty(var3, (String)"value"), var2.get((String)"value", var2))) {
                     return;
                  }

                  throw ScriptRuntime.typeError1("msg.change.value.with.writable.false", var1);
               }

               throw ScriptRuntime.typeError1("msg.change.writable.false.to.true.with.configurable.false", var1);
            }
         }

      }
   }

   protected void checkPropertyDefinition(ScriptableObject var1) {
      Object var2 = getProperty(var1, (String)"get");
      if (var2 != NOT_FOUND && var2 != Undefined.instance && !(var2 instanceof Callable)) {
         throw ScriptRuntime.notFunctionError(var2);
      } else {
         Object var3 = getProperty(var1, (String)"set");
         if (var3 != NOT_FOUND && var3 != Undefined.instance && !(var3 instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(var3);
         } else if (this.isDataDescriptor(var1)) {
            if (this.isAccessorDescriptor(var1)) {
               throw ScriptRuntime.typeError0("msg.both.data.and.accessor.desc");
            }
         }
      }
   }

   public void defineConst(String var1, Scriptable var2) {
      if (!this.putConstImpl(var1, 0, var2, Undefined.instance, 8)) {
         if (var2 != this) {
            if (var2 instanceof ConstProperties) {
               ((ConstProperties)var2).defineConst(var1, var2);
            }

         } else {
            throw Kit.codeBug();
         }
      }
   }

   public void defineFunctionProperties(String[] var1, Class var2, int var3) {
      Method[] var4 = FunctionObject.getMethodList(var2);

      for(int var5 = 0; var5 < var1.length; ++var5) {
         String var6 = var1[var5];
         Method var7 = FunctionObject.findSingleMethod(var4, var6);
         if (var7 == null) {
            throw Context.reportRuntimeError2("msg.method.not.found", var6, var2.getName());
         }

         this.defineProperty((String)var6, (Object)(new FunctionObject(var6, var7, this)), var3);
      }

   }

   public void defineOwnProperties(Context var1, ScriptableObject var2) {
      Object[] var3 = var2.getIds(false, true);
      ScriptableObject[] var4 = new ScriptableObject[var3.length];
      int var5 = 0;

      for(int var6 = var3.length; var5 < var6; ++var5) {
         ScriptableObject var9 = ensureScriptableObject(ScriptRuntime.getObjectElem((Scriptable)var2, var3[var5], var1));
         this.checkPropertyDefinition(var9);
         var4[var5] = var9;
      }

      int var7 = 0;

      for(int var8 = var3.length; var7 < var8; ++var7) {
         this.defineOwnProperty(var1, var3[var7], var4[var7]);
      }

   }

   public void defineOwnProperty(Context var1, Object var2, ScriptableObject var3) {
      this.checkPropertyDefinition(var3);
      this.defineOwnProperty(var1, var2, var3, true);
   }

   protected void defineOwnProperty(Context var1, Object var2, ScriptableObject var3, boolean var4) {
      ScriptableObject.Slot var5 = this.getSlot(var1, var2, ScriptableObject.SlotAccess.QUERY);
      boolean var6;
      if (var5 == null) {
         var6 = true;
      } else {
         var6 = false;
      }

      if (var4) {
         ScriptableObject var14;
         if (var5 == null) {
            var14 = null;
         } else {
            var14 = var5.getPropertyDescriptor(var1, this);
         }

         this.checkPropertyChange(var2, var14, var3);
      }

      boolean var7 = this.isAccessorDescriptor(var3);
      int var8;
      if (var5 == null) {
         ScriptableObject.SlotAccess var13;
         if (var7) {
            var13 = ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER;
         } else {
            var13 = ScriptableObject.SlotAccess.MODIFY;
         }

         var5 = this.getSlot(var1, var2, var13);
         var8 = this.applyDescriptorToAttributeBitset(7, var3);
      } else {
         var8 = this.applyDescriptorToAttributeBitset(var5.getAttributes(), var3);
      }

      if (var7) {
         if (!(var5 instanceof ScriptableObject.GetterSlot)) {
            var5 = this.getSlot(var1, var2, ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER);
         }

         ScriptableObject.GetterSlot var10 = (ScriptableObject.GetterSlot)var5;
         Object var11 = getProperty(var3, (String)"get");
         if (var11 != NOT_FOUND) {
            var10.getter = var11;
         }

         Object var12 = getProperty(var3, (String)"set");
         if (var12 != NOT_FOUND) {
            var10.setter = var12;
         }

         var10.value = Undefined.instance;
         var10.setAttributes(var8);
      } else {
         if (var5 instanceof ScriptableObject.GetterSlot && this.isDataDescriptor(var3)) {
            var5 = this.getSlot(var1, var2, ScriptableObject.SlotAccess.CONVERT_ACCESSOR_TO_DATA);
         }

         Object var9 = getProperty(var3, (String)"value");
         if (var9 != NOT_FOUND) {
            var5.value = var9;
         } else if (var6) {
            var5.value = Undefined.instance;
         }

         var5.setAttributes(var8);
      }
   }

   public void defineProperty(String var1, Class var2, int var3) {
      int var4 = var1.length();
      if (var4 != 0) {
         char[] var5 = new char[var4 + 3];
         var1.getChars(0, var4, var5, 3);
         var5[3] = Character.toUpperCase(var5[3]);
         var5[0] = 'g';
         var5[1] = 'e';
         var5[2] = 't';
         String var6 = new String(var5);
         var5[0] = 's';
         String var7 = new String(var5);
         Method[] var8 = FunctionObject.getMethodList(var2);
         Method var9 = FunctionObject.findSingleMethod(var8, var6);
         Method var10 = FunctionObject.findSingleMethod(var8, var7);
         int var11;
         if (var10 == null) {
            var11 = var3 | 1;
         } else {
            var11 = var3;
         }

         Method var12;
         if (var10 == null) {
            var12 = null;
         } else {
            var12 = var10;
         }

         this.defineProperty(var1, (Object)null, var9, var12, var11);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void defineProperty(String var1, Object var2, int var3) {
      this.checkNotSealed(var1, 0);
      this.put((String)var1, this, var2);
      this.setAttributes(var1, var3);
   }

   public void defineProperty(String var1, Object var2, Method var3, Method var4, int var5) {
      MemberBox var6 = null;
      if (var3 != null) {
         var6 = new MemberBox(var3);
         boolean var13;
         if (!Modifier.isStatic(var3.getModifiers())) {
            if (var2 != null) {
               var13 = true;
            } else {
               var13 = false;
            }

            var6.delegateTo = var2;
         } else {
            var13 = true;
            var6.delegateTo = Void.TYPE;
         }

         Class[] var14 = var3.getParameterTypes();
         String var15;
         if (var14.length == 0) {
            var15 = null;
            if (var13) {
               var15 = "msg.obj.getter.parms";
            }
         } else if (var14.length == 1) {
            Class var16 = var14[0];
            if (var16 != ScriptRuntime.ScriptableClass && var16 != ScriptRuntime.ScriptableObjectClass) {
               var15 = "msg.bad.getter.parms";
            } else {
               var15 = null;
               if (!var13) {
                  var15 = "msg.bad.getter.parms";
               }
            }
         } else {
            var15 = "msg.bad.getter.parms";
         }

         if (var15 != null) {
            throw Context.reportRuntimeError1(var15, var3.toString());
         }
      }

      MemberBox var7 = null;
      if (var4 != null) {
         if (var4.getReturnType() != Void.TYPE) {
            throw Context.reportRuntimeError1("msg.setter.return", var4.toString());
         }

         var7 = new MemberBox(var4);
         boolean var9;
         if (!Modifier.isStatic(var4.getModifiers())) {
            if (var2 != null) {
               var9 = true;
            } else {
               var9 = false;
            }

            var7.delegateTo = var2;
         } else {
            var9 = true;
            var7.delegateTo = Void.TYPE;
         }

         Class[] var10 = var4.getParameterTypes();
         String var11;
         if (var10.length == 1) {
            var11 = null;
            if (var9) {
               var11 = "msg.setter2.expected";
            }
         } else if (var10.length == 2) {
            Class var12 = var10[0];
            if (var12 != ScriptRuntime.ScriptableClass && var12 != ScriptRuntime.ScriptableObjectClass) {
               var11 = "msg.setter2.parms";
            } else {
               var11 = null;
               if (!var9) {
                  var11 = "msg.setter1.parms";
               }
            }
         } else {
            var11 = "msg.setter.parms";
         }

         if (var11 != null) {
            throw Context.reportRuntimeError1(var11, var4.toString());
         }
      }

      ScriptableObject.GetterSlot var8 = (ScriptableObject.GetterSlot)this.slotMap.get(var1, 0, ScriptableObject.SlotAccess.MODIFY_GETTER_SETTER);
      var8.setAttributes(var5);
      var8.getter = var6;
      var8.setter = var7;
   }

   public void defineProperty(Symbol var1, Object var2, int var3) {
      this.checkNotSealed(var1, 0);
      this.put((Symbol)var1, this, var2);
      this.setAttributes(var1, var3);
   }

   public void delete(int var1) {
      this.checkNotSealed((Object)null, var1);
      this.slotMap.remove((Object)null, var1);
   }

   public void delete(String var1) {
      this.checkNotSealed(var1, 0);
      this.slotMap.remove(var1, 0);
   }

   public void delete(Symbol var1) {
      this.checkNotSealed(var1, 0);
      this.slotMap.remove(var1, 0);
   }

   protected Object equivalentValues(Object var1) {
      return this == var1 ? Boolean.TRUE : Scriptable.NOT_FOUND;
   }

   public Object get(int var1, Scriptable var2) {
      ExternalArrayData var3 = this.externalData;
      if (var3 != null) {
         return var1 < var3.getArrayLength() ? this.externalData.getArrayElement(var1) : Scriptable.NOT_FOUND;
      } else {
         ScriptableObject.Slot var4 = this.slotMap.query((Object)null, var1);
         return var4 == null ? Scriptable.NOT_FOUND : var4.getValue(var2);
      }
   }

   public Object get(Object var1) {
      Object var3;
      if (var1 instanceof String) {
         var3 = this.get((String)((String)var1), this);
      } else if (var1 instanceof Symbol) {
         var3 = this.get((Symbol)((Symbol)var1), this);
      } else {
         boolean var2 = var1 instanceof Number;
         var3 = null;
         if (var2) {
            var3 = this.get(((Number)var1).intValue(), this);
         }
      }

      if (var3 != Scriptable.NOT_FOUND && var3 != Undefined.instance) {
         return var3 instanceof Wrapper ? ((Wrapper)var3).unwrap() : var3;
      } else {
         return null;
      }
   }

   public Object get(String var1, Scriptable var2) {
      ScriptableObject.Slot var3 = this.slotMap.query(var1, 0);
      return var3 == null ? Scriptable.NOT_FOUND : var3.getValue(var2);
   }

   public Object get(Symbol var1, Scriptable var2) {
      ScriptableObject.Slot var3 = this.slotMap.query(var1, 0);
      return var3 == null ? Scriptable.NOT_FOUND : var3.getValue(var2);
   }

   public Object[] getAllIds() {
      return this.getIds(true, false);
   }

   public final Object getAssociatedValue(Object var1) {
      Map var2 = this.associatedValues;
      return var2 == null ? null : var2.get(var1);
   }

   public int getAttributes(int var1) {
      return this.findAttributeSlot((String)null, var1, ScriptableObject.SlotAccess.QUERY).getAttributes();
   }

   @Deprecated
   public final int getAttributes(int var1, Scriptable var2) {
      return this.getAttributes(var1);
   }

   public int getAttributes(String var1) {
      return this.findAttributeSlot(var1, 0, ScriptableObject.SlotAccess.QUERY).getAttributes();
   }

   @Deprecated
   public final int getAttributes(String var1, Scriptable var2) {
      return this.getAttributes(var1);
   }

   public int getAttributes(Symbol var1) {
      return this.findAttributeSlot(var1, ScriptableObject.SlotAccess.QUERY).getAttributes();
   }

   public abstract String getClassName();

   public Object getDefaultValue(Class var1) {
      return getDefaultValue(this, var1);
   }

   public ExternalArrayData getExternalArrayData() {
      return this.externalData;
   }

   public Object getExternalArrayLength() {
      ExternalArrayData var1 = this.externalData;
      int var2;
      if (var1 == null) {
         var2 = 0;
      } else {
         var2 = var1.getArrayLength();
      }

      return var2;
   }

   public Object getGetterOrSetter(String var1, int var2, boolean var3) {
      if (var1 != null && var2 != 0) {
         throw new IllegalArgumentException(var1);
      } else {
         ScriptableObject.Slot var4 = this.slotMap.query(var1, var2);
         if (var4 == null) {
            return null;
         } else if (var4 instanceof ScriptableObject.GetterSlot) {
            ScriptableObject.GetterSlot var5 = (ScriptableObject.GetterSlot)var4;
            Object var6;
            if (var3) {
               var6 = var5.setter;
            } else {
               var6 = var5.getter;
            }

            return var6 != null ? var6 : Undefined.instance;
         } else {
            return Undefined.instance;
         }
      }
   }

   public Object[] getIds() {
      return this.getIds(false, false);
   }

   Object[] getIds(boolean var1, boolean var2) {
      ExternalArrayData var3 = this.externalData;
      int var4;
      if (var3 == null) {
         var4 = 0;
      } else {
         var4 = var3.getArrayLength();
      }

      Object[] var5;
      if (var4 == 0) {
         var5 = ScriptRuntime.emptyArgs;
      } else {
         var5 = new Object[var4];

         for(int var6 = 0; var6 < var4; ++var6) {
            var5[var6] = var6;
         }
      }

      if (this.slotMap.isEmpty()) {
         return var5;
      } else {
         int var7 = var4;
         long var8 = this.slotMap.readLock();

         label1172: {
            Throwable var10;
            label1171: {
               Throwable var10000;
               label1170: {
                  Iterator var11;
                  boolean var10001;
                  try {
                     var11 = this.slotMap.iterator();
                  } catch (Throwable var108) {
                     var10000 = var108;
                     var10001 = false;
                     break label1170;
                  }

                  while(true) {
                     ScriptableObject.Slot var15;
                     try {
                        if (!var11.hasNext()) {
                           break label1172;
                        }

                        var15 = (ScriptableObject.Slot)var11.next();
                     } catch (Throwable var104) {
                        var10000 = var104;
                        var10001 = false;
                        break;
                     }

                     if (!var1) {
                        try {
                           if ((2 & var15.getAttributes()) != 0) {
                              continue;
                           }
                        } catch (Throwable var103) {
                           var10000 = var103;
                           var10001 = false;
                           break;
                        }
                     }

                     if (!var2) {
                        try {
                           if (var15.name instanceof Symbol) {
                              continue;
                           }
                        } catch (Throwable var102) {
                           var10000 = var102;
                           var10001 = false;
                           break;
                        }
                     }

                     if (var7 == var4) {
                        Object[] var18 = var5;

                        try {
                           var5 = new Object[var4 + this.slotMap.dirtySize()];
                        } catch (Throwable var101) {
                           var10000 = var101;
                           var10001 = false;
                           break;
                        }

                        if (var18 != null) {
                           try {
                              System.arraycopy(var18, 0, var5, 0, var4);
                           } catch (Throwable var100) {
                              var10000 = var100;
                              var10001 = false;
                              break;
                           }
                        }
                     }

                     int var16 = var7 + 1;

                     label1158: {
                        label1188: {
                           Object var17;
                           label1155: {
                              try {
                                 if (var15.name != null) {
                                    var17 = var15.name;
                                    break label1155;
                                 }
                              } catch (Throwable var107) {
                                 var10000 = var107;
                                 var10001 = false;
                                 break label1188;
                              }

                              try {
                                 var17 = var15.indexOrHash;
                              } catch (Throwable var106) {
                                 var10000 = var106;
                                 var10001 = false;
                                 break label1188;
                              }
                           }

                           label1146:
                           try {
                              var5[var7] = var17;
                              break label1158;
                           } catch (Throwable var105) {
                              var10000 = var105;
                              var10001 = false;
                              break label1146;
                           }
                        }

                        var10 = var10000;
                        break label1171;
                     }

                     var7 = var16;
                  }
               }

               var10 = var10000;
            }

            this.slotMap.unlockRead(var8);
            throw var10;
         }

         this.slotMap.unlockRead(var8);
         Object[] var13;
         if (var7 == var4 + var5.length) {
            var13 = var5;
         } else {
            Object[] var12 = new Object[var7];
            System.arraycopy(var5, 0, var12, 0, var7);
            var13 = var12;
         }

         Context var14 = Context.getCurrentContext();
         if (var14 != null && var14.hasFeature(16)) {
            Arrays.sort(var13, KEY_COMPARATOR);
         }

         return var13;
      }
   }

   protected ScriptableObject getOwnPropertyDescriptor(Context var1, Object var2) {
      ScriptableObject.Slot var3 = this.getSlot(var1, var2, ScriptableObject.SlotAccess.QUERY);
      if (var3 == null) {
         return null;
      } else {
         Scriptable var4 = this.getParentScope();
         Object var5;
         if (var4 == null) {
            var5 = this;
         } else {
            var5 = var4;
         }

         return var3.getPropertyDescriptor(var1, (Scriptable)var5);
      }
   }

   public Scriptable getParentScope() {
      return this.parentScopeObject;
   }

   public Scriptable getPrototype() {
      return this.prototypeObject;
   }

   protected ScriptableObject.Slot getSlot(Context var1, Object var2, ScriptableObject.SlotAccess var3) {
      if (var2 instanceof Symbol) {
         return this.slotMap.get(var2, 0, var3);
      } else {
         String var4 = ScriptRuntime.toStringIdOrIndex(var1, var2);
         return var4 == null ? this.slotMap.get((Object)null, ScriptRuntime.lastIndexResult(var1), var3) : this.slotMap.get(var4, 0, var3);
      }
   }

   public String getTypeOf() {
      return this.avoidObjectDetection() ? "undefined" : "object";
   }

   public boolean has(int var1, Scriptable var2) {
      ExternalArrayData var3 = this.externalData;
      if (var3 != null) {
         return var1 < var3.getArrayLength();
      } else {
         return this.slotMap.query((Object)null, var1) != null;
      }
   }

   public boolean has(String var1, Scriptable var2) {
      ScriptableObject.Slot var3 = this.slotMap.query(var1, 0);
      boolean var4 = false;
      if (var3 != null) {
         var4 = true;
      }

      return var4;
   }

   public boolean has(Symbol var1, Scriptable var2) {
      ScriptableObject.Slot var3 = this.slotMap.query(var1, 0);
      boolean var4 = false;
      if (var3 != null) {
         var4 = true;
      }

      return var4;
   }

   public boolean hasInstance(Scriptable var1) {
      return ScriptRuntime.jsDelegatesTo(var1, this);
   }

   protected boolean isAccessorDescriptor(ScriptableObject var1) {
      return hasProperty(var1, (String)"get") || hasProperty(var1, (String)"set");
   }

   public boolean isConst(String var1) {
      ScriptableObject.Slot var2 = this.slotMap.query(var1, 0);
      if (var2 == null) {
         return false;
      } else {
         int var3 = 5 & var2.getAttributes();
         boolean var4 = false;
         if (var3 == 5) {
            var4 = true;
         }

         return var4;
      }
   }

   protected boolean isDataDescriptor(ScriptableObject var1) {
      return hasProperty(var1, (String)"value") || hasProperty(var1, (String)"writable");
   }

   public boolean isEmpty() {
      return this.slotMap.isEmpty();
   }

   public boolean isExtensible() {
      return this.isExtensible;
   }

   protected boolean isGenericDescriptor(ScriptableObject var1) {
      return !this.isDataDescriptor(var1) && !this.isAccessorDescriptor(var1);
   }

   protected boolean isGetterOrSetter(String var1, int var2, boolean var3) {
      ScriptableObject.Slot var4 = this.slotMap.query(var1, var2);
      if (var4 instanceof ScriptableObject.GetterSlot) {
         if (var3 && ((ScriptableObject.GetterSlot)var4).setter != null) {
            return true;
         }

         if (!var3 && ((ScriptableObject.GetterSlot)var4).getter != null) {
            return true;
         }
      }

      return false;
   }

   public final boolean isSealed() {
      return this.isSealed;
   }

   public void preventExtensions() {
      this.isExtensible = false;
   }

   public void put(int var1, Scriptable var2, Object var3) {
      ExternalArrayData var4 = this.externalData;
      if (var4 != null) {
         if (var1 < var4.getArrayLength()) {
            this.externalData.setArrayElement(var1, var3);
         } else {
            throw new JavaScriptException(ScriptRuntime.newNativeError(Context.getCurrentContext(), this, TopLevel.NativeErrors.RangeError, new Object[]{"External array index out of bounds "}), (String)null, 0);
         }
      } else if (!this.putImpl((Object)null, var1, var2, var3)) {
         if (var2 != this) {
            var2.put(var1, var2, var3);
         } else {
            throw Kit.codeBug();
         }
      }
   }

   public void put(String var1, Scriptable var2, Object var3) {
      if (!this.putImpl(var1, 0, var2, var3)) {
         if (var2 != this) {
            var2.put(var1, var2, var3);
         } else {
            throw Kit.codeBug();
         }
      }
   }

   public void put(Symbol var1, Scriptable var2, Object var3) {
      if (!this.putImpl(var1, 0, var2, var3)) {
         if (var2 != this) {
            ensureSymbolScriptable(var2).put(var1, var2, var3);
         } else {
            throw Kit.codeBug();
         }
      }
   }

   public void putConst(String var1, Scriptable var2, Object var3) {
      if (!this.putConstImpl(var1, 0, var2, var3, 1)) {
         if (var2 != this) {
            if (var2 instanceof ConstProperties) {
               ((ConstProperties)var2).putConst(var1, var2, var3);
            } else {
               var2.put(var1, var2, var3);
            }
         } else {
            throw Kit.codeBug();
         }
      }
   }

   protected boolean sameValue(Object var1, Object var2) {
      if (var1 == NOT_FOUND) {
         return true;
      } else {
         if (var2 == NOT_FOUND) {
            var2 = Undefined.instance;
         }

         if (var2 instanceof Number && var1 instanceof Number) {
            double var3 = ((Number)var2).doubleValue();
            double var5 = ((Number)var1).doubleValue();
            if (Double.isNaN(var3) && Double.isNaN(var5)) {
               return true;
            }

            if (var3 == 0.0D && Double.doubleToLongBits(var3) != Double.doubleToLongBits(var5)) {
               return false;
            }
         }

         return ScriptRuntime.shallowEq(var2, var1);
      }
   }

   public void sealObject() {
      if (!this.isSealed) {
         long var1 = this.slotMap.readLock();

         label367: {
            Throwable var10000;
            label373: {
               Iterator var4;
               boolean var10001;
               try {
                  var4 = this.slotMap.iterator();
               } catch (Throwable var46) {
                  var10000 = var46;
                  var10001 = false;
                  break label373;
               }

               while(true) {
                  ScriptableObject.Slot var5;
                  LazilyLoadedCtor var7;
                  try {
                     if (!var4.hasNext()) {
                        break;
                     }

                     var5 = (ScriptableObject.Slot)var4.next();
                     Object var6 = var5.value;
                     if (!(var6 instanceof LazilyLoadedCtor)) {
                        continue;
                     }

                     var7 = (LazilyLoadedCtor)var6;
                  } catch (Throwable var47) {
                     var10000 = var47;
                     var10001 = false;
                     break label373;
                  }

                  try {
                     var7.init();
                  } finally {
                     try {
                        var5.value = var7.getValue();
                     } catch (Throwable var43) {
                        var10000 = var43;
                        var10001 = false;
                        break label373;
                     }
                  }
               }

               label352:
               try {
                  this.isSealed = true;
                  break label367;
               } catch (Throwable var44) {
                  var10000 = var44;
                  var10001 = false;
                  break label352;
               }
            }

            Throwable var3 = var10000;
            this.slotMap.unlockRead(var1);
            throw var3;
         }

         this.slotMap.unlockRead(var1);
      }
   }

   public void setAttributes(int var1, int var2) {
      this.checkNotSealed((Object)null, var1);
      this.findAttributeSlot((String)null, var1, ScriptableObject.SlotAccess.MODIFY).setAttributes(var2);
   }

   @Deprecated
   public void setAttributes(int var1, Scriptable var2, int var3) {
      this.setAttributes(var1, var3);
   }

   public void setAttributes(String var1, int var2) {
      this.checkNotSealed(var1, 0);
      this.findAttributeSlot(var1, 0, ScriptableObject.SlotAccess.MODIFY).setAttributes(var2);
   }

   @Deprecated
   public final void setAttributes(String var1, Scriptable var2, int var3) {
      this.setAttributes(var1, var3);
   }

   public void setAttributes(Symbol var1, int var2) {
      this.checkNotSealed(var1, 0);
      this.findAttributeSlot(var1, ScriptableObject.SlotAccess.MODIFY).setAttributes(var2);
   }

   public void setExternalArrayData(ExternalArrayData var1) {
      this.externalData = var1;
      if (var1 == null) {
         this.delete("length");
      } else {
         this.defineProperty("length", (Object)null, GET_ARRAY_LENGTH, (Method)null, 3);
      }
   }

   public void setGetterOrSetter(String var1, int var2, Callable var3, boolean var4) {
      this.setGetterOrSetter(var1, var2, var3, var4, false);
   }

   public void setParentScope(Scriptable var1) {
      this.parentScopeObject = var1;
   }

   public void setPrototype(Scriptable var1) {
      this.prototypeObject = var1;
   }

   public int size() {
      return this.slotMap.size();
   }

   static final class GetterSlot extends ScriptableObject.Slot {
      private static final long serialVersionUID = -4900574849788797588L;
      Object getter;
      Object setter;

      GetterSlot(Object var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      ScriptableObject getPropertyDescriptor(Context var1, Scriptable var2) {
         int var3 = this.getAttributes();
         NativeObject var4 = new NativeObject();
         ScriptRuntime.setBuiltinProtoAndParent(var4, var2, TopLevel.Builtins.Object);
         int var5 = var3 & 2;
         boolean var6 = true;
         boolean var7;
         if (var5 == 0) {
            var7 = true;
         } else {
            var7 = false;
         }

         var4.defineProperty((String)"enumerable", (Object)var7, 0);
         boolean var8;
         if ((var3 & 4) == 0) {
            var8 = true;
         } else {
            var8 = false;
         }

         var4.defineProperty((String)"configurable", (Object)var8, 0);
         if (this.getter == null && this.setter == null) {
            if ((var3 & 1) != 0) {
               var6 = false;
            }

            var4.defineProperty((String)"writable", (Object)var6, 0);
         }

         String var9;
         if (this.name == null) {
            var9 = "f";
         } else {
            var9 = this.name.toString();
         }

         Object var10 = this.getter;
         if (var10 != null) {
            if (var10 instanceof MemberBox) {
               var4.defineProperty((String)"get", (Object)(new FunctionObject(var9, ((MemberBox)this.getter).member(), var2)), 0);
            } else if (var10 instanceof Member) {
               var4.defineProperty((String)"get", (Object)(new FunctionObject(var9, (Member)this.getter, var2)), 0);
            } else {
               var4.defineProperty((String)"get", (Object)var10, 0);
            }
         }

         Object var11 = this.setter;
         if (var11 != null) {
            if (var11 instanceof MemberBox) {
               var4.defineProperty((String)"set", (Object)(new FunctionObject(var9, ((MemberBox)this.setter).member(), var2)), 0);
               return var4;
            }

            if (var11 instanceof Member) {
               var4.defineProperty((String)"set", (Object)(new FunctionObject(var9, (Member)this.setter, var2)), 0);
               return var4;
            }

            var4.defineProperty((String)"set", (Object)var11, 0);
         }

         return var4;
      }

      Object getValue(Scriptable var1) {
         Object var2 = this.getter;
         if (var2 != null) {
            if (var2 instanceof MemberBox) {
               MemberBox var8 = (MemberBox)var2;
               Object var9;
               Object[] var10;
               if (var8.delegateTo == null) {
                  var9 = var1;
                  var10 = ScriptRuntime.emptyArgs;
               } else {
                  var9 = var8.delegateTo;
                  var10 = new Object[]{var1};
               }

               return var8.invoke(var9, var10);
            }

            if (var2 instanceof Function) {
               Function var7 = (Function)var2;
               return var7.call(Context.getContext(), var7.getParentScope(), var1, ScriptRuntime.emptyArgs);
            }
         }

         Object var3 = this.value;
         if (var3 instanceof LazilyLoadedCtor) {
            LazilyLoadedCtor var4 = (LazilyLoadedCtor)var3;
            boolean var12 = false;

            try {
               var12 = true;
               var4.init();
               var12 = false;
            } finally {
               if (var12) {
                  this.value = var4.getValue();
               }
            }

            Object var6 = var4.getValue();
            this.value = var6;
            return var6;
         } else {
            return var3;
         }
      }

      boolean setValue(Object var1, Scriptable var2, Scriptable var3) {
         if (this.setter == null) {
            if (this.getter != null) {
               Context var14 = Context.getContext();
               if (!var14.isStrictMode() && !var14.hasFeature(11)) {
                  return true;
               } else {
                  String var15 = "";
                  if (this.name != null) {
                     StringBuilder var16 = new StringBuilder();
                     var16.append("[");
                     var16.append(var3.getClassName());
                     var16.append("].");
                     var16.append(this.name.toString());
                     var15 = var16.toString();
                  }

                  throw ScriptRuntime.typeError2("msg.set.prop.no.setter", var15, Context.toString(var1));
               }
            } else {
               return super.setValue(var1, var2, var3);
            }
         } else {
            Context var4 = Context.getContext();
            Object var5 = this.setter;
            if (var5 instanceof MemberBox) {
               MemberBox var8 = (MemberBox)var5;
               Class[] var9 = var8.argTypes;
               Object var10 = FunctionObject.convertArg(var4, var3, var1, FunctionObject.getTypeTag(var9[var9.length - 1]));
               Object var11;
               Object[] var12;
               if (var8.delegateTo == null) {
                  var11 = var3;
                  var12 = new Object[]{var10};
               } else {
                  var11 = var8.delegateTo;
                  var12 = new Object[]{var3, var10};
               }

               var8.invoke(var11, var12);
            } else if (var5 instanceof Function) {
               Function var6 = (Function)var5;
               var6.call(var4, var6.getParentScope(), var3, new Object[]{var1});
               return true;
            }

            return true;
         }
      }
   }

   public static final class KeyComparator implements Comparator {
      public int compare(Object var1, Object var2) {
         if (var1 instanceof Integer) {
            if (var2 instanceof Integer) {
               int var3 = (Integer)var1;
               int var4 = (Integer)var2;
               if (var3 < var4) {
                  return -1;
               } else {
                  return var3 > var4 ? 1 : 0;
               }
            } else {
               return -1;
            }
         } else {
            return var2 instanceof Integer ? 1 : 0;
         }
      }
   }

   static class Slot implements Serializable {
      private static final long serialVersionUID = -6090581677123995491L;
      private short attributes;
      int indexOrHash;
      Object name;
      transient ScriptableObject.Slot next;
      transient ScriptableObject.Slot orderedNext;
      Object value;

      Slot(Object var1, int var2, int var3) {
         this.name = var1;
         this.indexOrHash = var2;
         this.attributes = (short)var3;
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         Object var2 = this.name;
         if (var2 != null) {
            this.indexOrHash = var2.hashCode();
         }

      }

      int getAttributes() {
         return this.attributes;
      }

      ScriptableObject getPropertyDescriptor(Context var1, Scriptable var2) {
         return ScriptableObject.buildDataDescriptor(var2, this.value, this.attributes);
      }

      Object getValue(Scriptable var1) {
         return this.value;
      }

      void setAttributes(int var1) {
         synchronized(this){}

         try {
            ScriptableObject.checkValidAttributes(var1);
            this.attributes = (short)var1;
         } finally {
            ;
         }

      }

      boolean setValue(Object var1, Scriptable var2, Scriptable var3) {
         if ((1 & this.attributes) != 0) {
            if (!Context.getContext().isStrictMode()) {
               return true;
            } else {
               throw ScriptRuntime.typeError1("msg.modify.readonly", this.name);
            }
         } else if (var2 == var3) {
            this.value = var1;
            return true;
         } else {
            return false;
         }
      }
   }

   static enum SlotAccess {
      CONVERT_ACCESSOR_TO_DATA,
      MODIFY,
      MODIFY_CONST,
      MODIFY_GETTER_SETTER,
      QUERY;

      static {
         ScriptableObject.SlotAccess var0 = new ScriptableObject.SlotAccess("QUERY", 0);
         QUERY = var0;
         ScriptableObject.SlotAccess var1 = new ScriptableObject.SlotAccess("MODIFY", 1);
         MODIFY = var1;
         ScriptableObject.SlotAccess var2 = new ScriptableObject.SlotAccess("MODIFY_CONST", 2);
         MODIFY_CONST = var2;
         ScriptableObject.SlotAccess var3 = new ScriptableObject.SlotAccess("MODIFY_GETTER_SETTER", 3);
         MODIFY_GETTER_SETTER = var3;
         ScriptableObject.SlotAccess var4 = new ScriptableObject.SlotAccess("CONVERT_ACCESSOR_TO_DATA", 4);
         CONVERT_ACCESSOR_TO_DATA = var4;
      }
   }
}
