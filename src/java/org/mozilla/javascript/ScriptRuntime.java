package org.mozilla.javascript;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.mozilla.javascript.v8dtoa.DoubleConversion;
import org.mozilla.javascript.v8dtoa.FastDtoa;
import org.mozilla.javascript.xml.XMLLib;
import org.mozilla.javascript.xml.XMLObject;

public class ScriptRuntime {
   public static final Class BooleanClass = Kit.classOrNull("java.lang.Boolean");
   public static final Class ByteClass = Kit.classOrNull("java.lang.Byte");
   public static final Class CharacterClass = Kit.classOrNull("java.lang.Character");
   public static final Class ClassClass = Kit.classOrNull("java.lang.Class");
   public static final Class ContextClass = Kit.classOrNull("org.mozilla.javascript.Context");
   public static final Class ContextFactoryClass = Kit.classOrNull("org.mozilla.javascript.ContextFactory");
   private static final String DEFAULT_NS_TAG = "__default_namespace__";
   public static final Class DateClass = Kit.classOrNull("java.util.Date");
   public static final Class DoubleClass = Kit.classOrNull("java.lang.Double");
   public static final int ENUMERATE_ARRAY = 2;
   public static final int ENUMERATE_ARRAY_NO_ITERATOR = 5;
   public static final int ENUMERATE_KEYS = 0;
   public static final int ENUMERATE_KEYS_NO_ITERATOR = 3;
   public static final int ENUMERATE_VALUES = 1;
   public static final int ENUMERATE_VALUES_IN_ORDER = 6;
   public static final int ENUMERATE_VALUES_NO_ITERATOR = 4;
   public static final Class FloatClass = Kit.classOrNull("java.lang.Float");
   public static final Class FunctionClass = Kit.classOrNull("org.mozilla.javascript.Function");
   public static final Class IntegerClass = Kit.classOrNull("java.lang.Integer");
   private static final Object LIBRARY_SCOPE_KEY = "LIBRARY_SCOPE";
   public static final Class LongClass = Kit.classOrNull("java.lang.Long");
   public static final double NaN;
   public static final Double NaNobj;
   public static final Class NumberClass = Kit.classOrNull("java.lang.Number");
   public static final Class ObjectClass = Kit.classOrNull("java.lang.Object");
   public static Locale ROOT_LOCALE = new Locale("");
   public static final Class ScriptableClass = Scriptable.class;
   public static final Class ScriptableObjectClass = Kit.classOrNull("org.mozilla.javascript.ScriptableObject");
   public static final Class ShortClass = Kit.classOrNull("java.lang.Short");
   public static final Class StringClass = Kit.classOrNull("java.lang.String");
   public static final Object[] emptyArgs;
   public static final String[] emptyStrings;
   public static ScriptRuntime.MessageProvider messageProvider;
   public static final double negativeZero;

   static {
      double var0 = Double.longBitsToDouble(9221120237041090560L);
      NaN = var0;
      negativeZero = Double.longBitsToDouble(Long.MIN_VALUE);
      NaNobj = new Double(var0);
      messageProvider = new ScriptRuntime.DefaultMessageProvider((ScriptRuntime$1)null);
      emptyArgs = new Object[0];
      emptyStrings = new String[0];
   }

   protected ScriptRuntime() {
   }

   public static CharSequence add(CharSequence var0, Object var1) {
      return new ConsString(var0, toCharSequence(var1));
   }

   public static CharSequence add(Object var0, CharSequence var1) {
      return new ConsString(toCharSequence(var0), var1);
   }

   public static Object add(Object var0, Object var1, Context var2) {
      if (var0 instanceof Number && var1 instanceof Number) {
         return wrapNumber(((Number)var0).doubleValue() + ((Number)var1).doubleValue());
      } else {
         if (var0 instanceof XMLObject) {
            Object var4 = ((XMLObject)var0).addValues(var2, true, var1);
            if (var4 != Scriptable.NOT_FOUND) {
               return var4;
            }
         }

         if (var1 instanceof XMLObject) {
            Object var3 = ((XMLObject)var1).addValues(var2, false, var0);
            if (var3 != Scriptable.NOT_FOUND) {
               return var3;
            }
         }

         if (!(var0 instanceof Symbol) && !(var1 instanceof Symbol)) {
            if (var0 instanceof Scriptable) {
               var0 = ((Scriptable)var0).getDefaultValue((Class)null);
            }

            if (var1 instanceof Scriptable) {
               var1 = ((Scriptable)var1).getDefaultValue((Class)null);
            }

            if (!(var0 instanceof CharSequence) && !(var1 instanceof CharSequence)) {
               return var0 instanceof Number && var1 instanceof Number ? wrapNumber(((Number)var0).doubleValue() + ((Number)var1).doubleValue()) : wrapNumber(toNumber(var0) + toNumber(var1));
            } else {
               return new ConsString(toCharSequence(var0), toCharSequence(var1));
            }
         } else {
            throw typeError0("msg.not.a.number");
         }
      }
   }

   public static void addInstructionCount(Context var0, int var1) {
      var0.instructionCount += var1;
      if (var0.instructionCount > var0.instructionThreshold) {
         var0.observeInstructionCount(var0.instructionCount);
         var0.instructionCount = 0;
      }

   }

   public static Object applyOrCall(boolean var0, Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      int var5 = var4.length;
      Callable var6 = getCallable(var3);
      Scriptable var7 = null;
      if (var5 != 0) {
         if (var1.hasFeature(15)) {
            var7 = toObjectOrNull(var1, var4[0], var2);
         } else {
            Scriptable var10;
            if (var4[0] == Undefined.instance) {
               var10 = Undefined.SCRIPTABLE_UNDEFINED;
            } else {
               var10 = toObjectOrNull(var1, var4[0], var2);
            }

            var7 = var10;
         }
      }

      if (var7 == null && var1.hasFeature(15)) {
         var7 = getTopCallScope(var1);
      }

      Object[] var9;
      if (var0) {
         if (var5 <= 1) {
            var9 = emptyArgs;
         } else {
            var9 = getApplyArguments(var1, var4[1]);
         }
      } else if (var5 <= 1) {
         var9 = emptyArgs;
      } else {
         Object[] var8 = new Object[var5 - 1];
         System.arraycopy(var4, 1, var8, 0, var5 - 1);
         var9 = var8;
      }

      return var6.call(var1, var2, var7, var9);
   }

   public static Scriptable bind(Context var0, Scriptable var1, String var2) {
      Scriptable var3 = var1.getParentScope();
      XMLObject var4 = null;
      if (var3 != null) {
         label54: {
            XMLObject var5 = null;

            while(var1 instanceof NativeWith) {
               Scriptable var6 = var1.getPrototype();
               if (var6 instanceof XMLObject) {
                  XMLObject var7 = (XMLObject)var6;
                  if (var7.has(var0, var2)) {
                     return var7;
                  }

                  if (var5 == null) {
                     var5 = var7;
                  }
               } else if (ScriptableObject.hasProperty(var6, var2)) {
                  return var6;
               }

               var1 = var3;
               var3 = var3.getParentScope();
               if (var3 == null) {
                  var4 = var5;
                  break label54;
               }
            }

            do {
               if (ScriptableObject.hasProperty(var1, var2)) {
                  return var1;
               }

               var1 = var3;
               var3 = var3.getParentScope();
            } while(var3 != null);

            var4 = var5;
         }
      }

      if (var0.useDynamicScope) {
         var1 = checkDynamicScope(var0.topCallScope, var1);
      }

      return (Scriptable)(ScriptableObject.hasProperty(var1, var2) ? var1 : var4);
   }

   @Deprecated
   public static Object call(Context var0, Object var1, Object var2, Object[] var3, Scriptable var4) {
      if (var1 instanceof Function) {
         Function var5 = (Function)var1;
         Scriptable var6 = toObjectOrNull(var0, var2, var4);
         if (var6 != null) {
            return var5.call(var0, var4, var6, var3);
         } else {
            throw undefCallError(var6, "function");
         }
      } else {
         throw notFunctionError(toString(var1));
      }
   }

   public static Object callIterator(Object var0, Context var1, Scriptable var2) {
      return getElemFunctionAndThis(var0, SymbolKey.ITERATOR, var1, var2).call(var1, var2, lastStoredScriptable(var1), emptyArgs);
   }

   public static Ref callRef(Callable var0, Scriptable var1, Object[] var2, Context var3) {
      if (var0 instanceof RefCallable) {
         RefCallable var4 = (RefCallable)var0;
         Ref var5 = var4.refCall(var3, var1, var2);
         if (var5 != null) {
            return var5;
         } else {
            StringBuilder var6 = new StringBuilder();
            var6.append(var4.getClass().getName());
            var6.append(".refCall() returned null");
            throw new IllegalStateException(var6.toString());
         }
      } else {
         throw constructError("ReferenceError", getMessage1("msg.no.ref.from.function", toString(var0)));
      }
   }

   public static Object callSpecial(Context var0, Callable var1, Scriptable var2, Object[] var3, Scriptable var4, Scriptable var5, int var6, String var7, int var8) {
      if (var6 == 1) {
         if (var2.getParentScope() == null && NativeGlobal.isEvalFunction(var1)) {
            return evalSpecial(var0, var4, var5, var3, var7, var8);
         }
      } else {
         if (var6 != 2) {
            throw Kit.codeBug();
         }

         if (NativeWith.isWithFunction(var1)) {
            throw Context.reportRuntimeError1("msg.only.from.new", "With");
         }
      }

      return var1.call(var0, var4, var2, var3);
   }

   static void checkDeprecated(Context var0, String var1) {
      int var2 = var0.getLanguageVersion();
      if (var2 >= 140 || var2 == 0) {
         String var3 = getMessage1("msg.deprec.ctor", var1);
         if (var2 != 0) {
            throw Context.reportRuntimeError(var3);
         }

         Context.reportWarning(var3);
      }

   }

   static Scriptable checkDynamicScope(Scriptable var0, Scriptable var1) {
      if (var0 == var1) {
         return var0;
      } else {
         Scriptable var2 = var0;

         do {
            var2 = var2.getPrototype();
            if (var2 == var1) {
               return var0;
            }
         } while(var2 != null);

         return var1;
      }
   }

   public static RegExpProxy checkRegExpProxy(Context var0) {
      RegExpProxy var1 = getRegExpProxy(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw Context.reportRuntimeError0("msg.no.regexp");
      }
   }

   public static boolean cmp_LE(Object var0, Object var1) {
      double var2;
      double var4;
      if (var0 instanceof Number && var1 instanceof Number) {
         var2 = ((Number)var0).doubleValue();
         var4 = ((Number)var1).doubleValue();
      } else {
         if (var0 instanceof Symbol || var1 instanceof Symbol) {
            throw typeError0("msg.compare.symbol");
         }

         if (var0 instanceof Scriptable) {
            var0 = ((Scriptable)var0).getDefaultValue(NumberClass);
         }

         if (var1 instanceof Scriptable) {
            var1 = ((Scriptable)var1).getDefaultValue(NumberClass);
         }

         if (var0 instanceof CharSequence && var1 instanceof CharSequence) {
            if (var0.toString().compareTo(var1.toString()) <= 0) {
               return true;
            }

            return false;
         }

         var2 = toNumber(var0);
         var4 = toNumber(var1);
      }

      if (var2 <= var4) {
         return true;
      } else {
         return false;
      }
   }

   public static boolean cmp_LT(Object var0, Object var1) {
      double var2;
      double var4;
      if (var0 instanceof Number && var1 instanceof Number) {
         var2 = ((Number)var0).doubleValue();
         var4 = ((Number)var1).doubleValue();
      } else {
         if (var0 instanceof Symbol || var1 instanceof Symbol) {
            throw typeError0("msg.compare.symbol");
         }

         if (var0 instanceof Scriptable) {
            var0 = ((Scriptable)var0).getDefaultValue(NumberClass);
         }

         if (var1 instanceof Scriptable) {
            var1 = ((Scriptable)var1).getDefaultValue(NumberClass);
         }

         if (var0 instanceof CharSequence && var1 instanceof CharSequence) {
            if (var0.toString().compareTo(var1.toString()) < 0) {
               return true;
            }

            return false;
         }

         var2 = toNumber(var0);
         var4 = toNumber(var1);
      }

      if (var2 < var4) {
         return true;
      } else {
         return false;
      }
   }

   public static EcmaError constructError(String var0, String var1) {
      int[] var2 = new int[1];
      return constructError(var0, var1, Context.getSourcePositionFromStack(var2), var2[0], (String)null, 0);
   }

   public static EcmaError constructError(String var0, String var1, int var2) {
      int[] var3 = new int[1];
      String var4 = Context.getSourcePositionFromStack(var3);
      if (var3[0] != 0) {
         var3[0] += var2;
      }

      return constructError(var0, var1, var4, var3[0], (String)null, 0);
   }

   public static EcmaError constructError(String var0, String var1, String var2, int var3, String var4, int var5) {
      EcmaError var6 = new EcmaError(var0, var1, var2, var3, var4, var5);
      return var6;
   }

   public static Scriptable createArrowFunctionActivation(NativeFunction var0, Scriptable var1, Object[] var2, boolean var3) {
      NativeCall var4 = new NativeCall(var0, var1, var2, true, var3);
      return var4;
   }

   @Deprecated
   public static Scriptable createFunctionActivation(NativeFunction var0, Scriptable var1, Object[] var2) {
      return createFunctionActivation(var0, var1, var2, false);
   }

   public static Scriptable createFunctionActivation(NativeFunction var0, Scriptable var1, Object[] var2, boolean var3) {
      NativeCall var4 = new NativeCall(var0, var1, var2, false, var3);
      return var4;
   }

   private static XMLLib currentXMLLib(Context var0) {
      if (var0.topCallScope != null) {
         XMLLib var1 = var0.cachedXMLLib;
         if (var1 == null) {
            XMLLib var2 = XMLLib.extractFromScope(var0.topCallScope);
            if (var2 != null) {
               var0.cachedXMLLib = var2;
               return var2;
            } else {
               throw new IllegalStateException();
            }
         } else {
            return var1;
         }
      } else {
         throw new IllegalStateException();
      }
   }

   static String defaultObjectToSource(Context var0, Scriptable var1, Scriptable var2, Object[] var3) {
      boolean var4;
      boolean var5;
      if (var0.iterating == null) {
         var4 = true;
         var0.iterating = new ObjToIntMap(31);
         var5 = false;
      } else {
         var4 = false;
         var5 = var0.iterating.has(var2);
      }

      StringBuilder var6 = new StringBuilder(128);
      if (var4) {
         var6.append("(");
      }

      var6.append('{');
      if (!var5) {
         label1176: {
            Throwable var10000;
            label1172: {
               Object[] var12;
               boolean var10001;
               try {
                  var0.iterating.intern(var2);
                  var12 = var2.getIds();
               } catch (Throwable var135) {
                  var10000 = var135;
                  var10001 = false;
                  break label1172;
               }

               int var13 = 0;

               while(true) {
                  Object var14;
                  boolean var15;
                  try {
                     if (var13 >= var12.length) {
                        break label1176;
                     }

                     var14 = var12[var13];
                     var15 = var14 instanceof Integer;
                  } catch (Throwable var131) {
                     var10000 = var131;
                     var10001 = false;
                     break;
                  }

                  label1158: {
                     Object var17;
                     if (var15) {
                        int var25;
                        try {
                           var25 = (Integer)var14;
                           var17 = var2.get(var25, var2);
                           if (var17 == Scriptable.NOT_FOUND) {
                              break label1158;
                           }
                        } catch (Throwable var132) {
                           var10000 = var132;
                           var10001 = false;
                           break;
                        }

                        if (var13 > 0) {
                           try {
                              var6.append(", ");
                           } catch (Throwable var130) {
                              var10000 = var130;
                              var10001 = false;
                              break;
                           }
                        }

                        try {
                           var6.append(var25);
                        } catch (Throwable var129) {
                           var10000 = var129;
                           var10001 = false;
                           break;
                        }
                     } else {
                        label1178: {
                           String var16;
                           try {
                              var16 = (String)var14;
                              var17 = var2.get(var16, var2);
                              if (var17 == Scriptable.NOT_FOUND) {
                                 break label1158;
                              }
                           } catch (Throwable var133) {
                              var10000 = var133;
                              var10001 = false;
                              break;
                           }

                           if (var13 > 0) {
                              try {
                                 var6.append(", ");
                              } catch (Throwable var128) {
                                 var10000 = var128;
                                 var10001 = false;
                                 break;
                              }
                           }

                           try {
                              if (isValidIdentifierName(var16, var0, var0.isStrictMode())) {
                                 var6.append(var16);
                                 break label1178;
                              }
                           } catch (Throwable var134) {
                              var10000 = var134;
                              var10001 = false;
                              break;
                           }

                           try {
                              var6.append('\'');
                              var6.append(escapeString(var16, '\''));
                              var6.append('\'');
                           } catch (Throwable var127) {
                              var10000 = var127;
                              var10001 = false;
                              break;
                           }
                        }
                     }

                     try {
                        var6.append(':');
                        var6.append(uneval(var0, var1, var17));
                     } catch (Throwable var126) {
                        var10000 = var126;
                        var10001 = false;
                        break;
                     }
                  }

                  ++var13;
               }
            }

            Throwable var10 = var10000;
            if (var4) {
               var0.iterating = null;
            }

            throw var10;
         }
      }

      if (var4) {
         var0.iterating = null;
      }

      var6.append('}');
      if (var4) {
         var6.append(')');
      }

      return var6.toString();
   }

   static String defaultObjectToString(Scriptable var0) {
      if (var0 == null) {
         return "[object Null]";
      } else if (Undefined.isUndefined(var0)) {
         return "[object Undefined]";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append("[object ");
         var1.append(var0.getClassName());
         var1.append(']');
         return var1.toString();
      }
   }

   @Deprecated
   public static Object delete(Object var0, Object var1, Context var2) {
      return delete(var0, var1, var2, false);
   }

   public static Object delete(Object var0, Object var1, Context var2, Scriptable var3, boolean var4) {
      Scriptable var5 = toObjectOrNull(var2, var0, var3);
      if (var5 == null) {
         if (var4) {
            return Boolean.TRUE;
         } else {
            throw undefDeleteError(var0, var1);
         }
      } else {
         return wrapBoolean(deleteObjectElem(var5, var1, var2));
      }
   }

   @Deprecated
   public static Object delete(Object var0, Object var1, Context var2, boolean var3) {
      return delete(var0, var1, var2, getTopCallScope(var2), var3);
   }

   public static boolean deleteObjectElem(Scriptable var0, Object var1, Context var2) {
      if (isSymbol(var1)) {
         SymbolScriptable var5 = ScriptableObject.ensureSymbolScriptable(var0);
         Symbol var6 = (Symbol)var1;
         var5.delete(var6);
         return true ^ var5.has(var6, var0);
      } else {
         String var3 = toStringIdOrIndex(var2, var1);
         if (var3 == null) {
            int var4 = lastIndexResult(var2);
            var0.delete(var4);
            return true ^ var0.has(var4, var0);
         } else {
            var0.delete(var3);
            return true ^ var0.has(var3, var0);
         }
      }
   }

   private static Object doScriptableIncrDecr(Scriptable var0, String var1, Scriptable var2, Object var3, int var4) {
      boolean var5;
      if ((var4 & 2) != 0) {
         var5 = true;
      } else {
         var5 = false;
      }

      double var6;
      if (var3 instanceof Number) {
         var6 = ((Number)var3).doubleValue();
      } else {
         var6 = toNumber(var3);
         if (var5) {
            var3 = wrapNumber(var6);
         }
      }

      double var8;
      if ((var4 & 1) == 0) {
         var8 = var6 + 1.0D;
      } else {
         var8 = var6 - 1.0D;
      }

      Number var10 = wrapNumber(var8);
      var0.put(var1, var2, var10);
      return var5 ? var3 : var10;
   }

   @Deprecated
   public static Object doTopCall(Callable var0, Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      return doTopCall(var0, var1, var2, var3, var4, var1.isTopLevelStrict);
   }

   public static Object doTopCall(Callable var0, Context var1, Scriptable var2, Scriptable var3, Object[] var4, boolean var5) {
      if (var2 == null) {
         throw new IllegalArgumentException();
      } else if (var1.topCallScope == null) {
         var1.topCallScope = ScriptableObject.getTopLevelScope(var2);
         var1.useDynamicScope = var1.hasFeature(7);
         boolean var6 = var1.isTopLevelStrict;
         var1.isTopLevelStrict = var5;
         ContextFactory var7 = var1.getFactory();
         boolean var11 = false;

         Object var9;
         try {
            var11 = true;
            var9 = var7.doTopCall(var0, var1, var2, var3, var4);
            var11 = false;
         } finally {
            if (var11) {
               var1.topCallScope = null;
               var1.cachedXMLLib = null;
               var1.isTopLevelStrict = var6;
               if (var1.currentActivationCall != null) {
                  throw new IllegalStateException();
               }

            }
         }

         var1.topCallScope = null;
         var1.cachedXMLLib = null;
         var1.isTopLevelStrict = var6;
         if (var1.currentActivationCall == null) {
            return var9;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   @Deprecated
   public static Object elemIncrDecr(Object var0, Object var1, Context var2, int var3) {
      return elemIncrDecr(var0, var1, var2, getTopCallScope(var2), var3);
   }

   public static Object elemIncrDecr(Object var0, Object var1, Context var2, Scriptable var3, int var4) {
      Object var5 = getObjectElem(var0, var1, var2, var3);
      boolean var6;
      if ((var4 & 2) != 0) {
         var6 = true;
      } else {
         var6 = false;
      }

      double var7;
      if (var5 instanceof Number) {
         var7 = ((Number)var5).doubleValue();
      } else {
         var7 = toNumber(var5);
         if (var6) {
            var5 = wrapNumber(var7);
         }
      }

      double var9;
      if ((var4 & 1) == 0) {
         var9 = var7 + 1.0D;
      } else {
         var9 = var7 - 1.0D;
      }

      Number var11 = wrapNumber(var9);
      setObjectElem(var0, var1, var11, var2, var3);
      return var6 ? var5 : var11;
   }

   public static void enterActivationFunction(Context var0, Scriptable var1) {
      if (var0.topCallScope != null) {
         NativeCall var2 = (NativeCall)var1;
         var2.parentActivationCall = var0.currentActivationCall;
         var0.currentActivationCall = var2;
         var2.defineAttributesForArguments();
      } else {
         throw new IllegalStateException();
      }
   }

   public static Scriptable enterDotQuery(Object var0, Scriptable var1) {
      if (var0 instanceof XMLObject) {
         return ((XMLObject)var0).enterDotQuery(var1);
      } else {
         throw notXmlError(var0);
      }
   }

   public static Scriptable enterWith(Object var0, Context var1, Scriptable var2) {
      Scriptable var3 = toObjectOrNull(var1, var0, var2);
      if (var3 != null) {
         return var3 instanceof XMLObject ? ((XMLObject)var3).enterWith(var2) : new NativeWith(var2, var3);
      } else {
         throw typeError1("msg.undef.with", toString(var0));
      }
   }

   private static void enumChangeObject(ScriptRuntime.IdEnumeration var0) {
      Object[] var1;
      for(var1 = null; var0.obj != null; var0.obj = var0.obj.getPrototype()) {
         var1 = var0.obj.getIds();
         if (var1.length != 0) {
            break;
         }
      }

      if (var0.obj != null && var0.ids != null) {
         Object[] var2 = var0.ids;
         int var3 = var2.length;
         if (var0.used == null) {
            var0.used = new ObjToIntMap(var3);
         }

         for(int var4 = 0; var4 != var3; ++var4) {
            var0.used.intern(var2[var4]);
         }
      }

      var0.ids = var1;
      var0.index = 0;
   }

   public static Object enumId(Object var0, Context var1) {
      ScriptRuntime.IdEnumeration var2 = (ScriptRuntime.IdEnumeration)var0;
      if (var2.iterator != null) {
         return var2.currentId;
      } else {
         label41: {
            int var3 = var2.enumType;
            if (var3 != 0) {
               if (var3 == 1) {
                  return enumValue(var0, var1);
               }

               if (var3 == 2) {
                  break label41;
               }

               if (var3 != 3) {
                  if (var3 != 4) {
                     if (var3 != 5) {
                        throw Kit.codeBug();
                     }
                     break label41;
                  }

                  return enumValue(var0, var1);
               }
            }

            return var2.currentId;
         }

         Object[] var4 = new Object[]{var2.currentId, enumValue(var0, var1)};
         return var1.newArray(ScriptableObject.getTopLevelScope(var2.obj), var4);
      }
   }

   @Deprecated
   public static Object enumInit(Object var0, Context var1, int var2) {
      return enumInit(var0, var1, getTopCallScope(var1), var2);
   }

   public static Object enumInit(Object var0, Context var1, Scriptable var2, int var3) {
      ScriptRuntime.IdEnumeration var4 = new ScriptRuntime.IdEnumeration((ScriptRuntime$1)null);
      var4.obj = toObjectOrNull(var1, var0, var2);
      if (var3 == 6) {
         var4.enumType = var3;
         var4.iterator = null;
         return enumInitInOrder(var1, var4);
      } else if (var4.obj == null) {
         return var4;
      } else {
         var4.enumType = var3;
         var4.iterator = null;
         if (var3 != 3 && var3 != 4 && var3 != 5) {
            Scriptable var5 = var4.obj.getParentScope();
            Scriptable var6 = var4.obj;
            boolean var7;
            if (var3 == 0) {
               var7 = true;
            } else {
               var7 = false;
            }

            var4.iterator = toIterator(var1, var5, var6, var7);
         }

         if (var4.iterator == null) {
            enumChangeObject(var4);
         }

         return var4;
      }
   }

   @Deprecated
   public static Object enumInit(Object var0, Context var1, boolean var2) {
      return enumInit(var0, var1, var2);
   }

   private static Object enumInitInOrder(Context var0, ScriptRuntime.IdEnumeration var1) {
      if (var1.obj instanceof ScriptableObject) {
         ScriptableObject var2 = (ScriptableObject)var1.obj;
         if (ScriptableObject.hasProperty(var2, (Symbol)SymbolKey.ITERATOR)) {
            Object var3 = ScriptableObject.getProperty(var2, (Symbol)SymbolKey.ITERATOR);
            if (var3 instanceof Callable) {
               Callable var4 = (Callable)var3;
               Scriptable var5 = var1.obj.getParentScope();
               Object[] var6 = new Object[0];
               Object var7 = var4.call(var0, var5, var1.obj, var6);
               if (var7 instanceof Scriptable) {
                  var1.iterator = (Scriptable)var7;
                  return var1;
               } else {
                  throw typeError1("msg.not.iterable", toString(var1.obj));
               }
            } else {
               throw typeError1("msg.not.iterable", toString(var1.obj));
            }
         } else {
            throw typeError1("msg.not.iterable", toString(var1.obj));
         }
      } else {
         throw typeError1("msg.not.iterable", toString(var1.obj));
      }
   }

   public static Boolean enumNext(Object var0) {
      ScriptRuntime.IdEnumeration var1 = (ScriptRuntime.IdEnumeration)var0;
      if (var1.iterator != null) {
         if (var1.enumType == 6) {
            return enumNextInOrder(var1);
         } else {
            Object var8 = ScriptableObject.getProperty(var1.iterator, "next");
            if (!(var8 instanceof Callable)) {
               return Boolean.FALSE;
            } else {
               Callable var9 = (Callable)var8;
               Context var10 = Context.getContext();

               try {
                  var1.currentId = var9.call(var10, var1.iterator.getParentScope(), var1.iterator, emptyArgs);
                  Boolean var12 = Boolean.TRUE;
                  return var12;
               } catch (JavaScriptException var13) {
                  if (var13.getValue() instanceof <undefinedtype>) {
                     return Boolean.FALSE;
                  } else {
                     throw var13;
                  }
               }
            }
         }
      } else {
         while(true) {
            Object var4;
            do {
               label60:
               do {
                  while(var1.obj != null) {
                     if (var1.index != var1.ids.length) {
                        Object[] var2 = var1.ids;
                        int var3 = var1.index++;
                        var4 = var2[var3];
                        continue label60;
                     }

                     var1.obj = var1.obj.getPrototype();
                     enumChangeObject(var1);
                  }

                  return Boolean.FALSE;
               } while(var1.used != null && var1.used.has(var4));
            } while(var4 instanceof Symbol);

            if (var4 instanceof String) {
               String var7 = (String)var4;
               if (var1.obj.has(var7, var1.obj)) {
                  var1.currentId = var7;
                  break;
               }
            } else {
               int var5 = ((Number)var4).intValue();
               if (var1.obj.has(var5, var1.obj)) {
                  Object var6;
                  if (var1.enumNumbers) {
                     var6 = var5;
                  } else {
                     var6 = String.valueOf(var5);
                  }

                  var1.currentId = var6;
                  break;
               }
            }
         }

         return Boolean.TRUE;
      }
   }

   private static Boolean enumNextInOrder(ScriptRuntime.IdEnumeration var0) {
      Object var1 = ScriptableObject.getProperty(var0.iterator, "next");
      if (var1 instanceof Callable) {
         Callable var2 = (Callable)var1;
         Context var3 = Context.getContext();
         Scriptable var4 = var0.iterator.getParentScope();
         Scriptable var5 = toObject(var3, var4, var2.call(var3, var4, var0.iterator, emptyArgs));
         Object var6 = ScriptableObject.getProperty(var5, "done");
         if (var6 != ScriptableObject.NOT_FOUND && toBoolean(var6)) {
            return Boolean.FALSE;
         } else {
            var0.currentId = ScriptableObject.getProperty(var5, "value");
            return Boolean.TRUE;
         }
      } else {
         throw notFunctionError(var0.iterator, "next");
      }
   }

   public static Object enumValue(Object var0, Context var1) {
      ScriptRuntime.IdEnumeration var2 = (ScriptRuntime.IdEnumeration)var0;
      if (isSymbol(var2.currentId)) {
         return ScriptableObject.ensureSymbolScriptable(var2.obj).get((Symbol)var2.currentId, var2.obj);
      } else {
         String var3 = toStringIdOrIndex(var1, var2.currentId);
         if (var3 == null) {
            int var4 = lastIndexResult(var1);
            return var2.obj.get(var4, var2.obj);
         } else {
            return var2.obj.get(var3, var2.obj);
         }
      }
   }

   public static boolean eq(Object var0, Object var1) {
      if (var0 != null && var0 != Undefined.instance) {
         if (var0 instanceof Number) {
            return eqNumber(((Number)var0).doubleValue(), var1);
         } else if (var0 == var1) {
            return true;
         } else if (var0 instanceof CharSequence) {
            return eqString((CharSequence)var0, var1);
         } else {
            boolean var3 = var0 instanceof Boolean;
            double var4 = 1.0D;
            if (var3) {
               boolean var11 = (Boolean)var0;
               if (var1 instanceof Boolean) {
                  return var11 == (Boolean)var1;
               } else {
                  if (var1 instanceof ScriptableObject) {
                     Object var12 = ((ScriptableObject)var1).equivalentValues(var0);
                     if (var12 != Scriptable.NOT_FOUND) {
                        return (Boolean)var12;
                     }
                  }

                  if (!var11) {
                     var4 = 0.0D;
                  }

                  return eqNumber(var4, var1);
               }
            } else if (var0 instanceof Scriptable) {
               if (!(var1 instanceof Scriptable)) {
                  if (var1 instanceof Boolean) {
                     if (var0 instanceof ScriptableObject) {
                        Object var6 = ((ScriptableObject)var0).equivalentValues(var1);
                        if (var6 != Scriptable.NOT_FOUND) {
                           return (Boolean)var6;
                        }
                     }

                     if (!(Boolean)var1) {
                        var4 = 0.0D;
                     }

                     return eqNumber(var4, var0);
                  } else if (var1 instanceof Number) {
                     return eqNumber(((Number)var1).doubleValue(), var0);
                  } else {
                     return var1 instanceof CharSequence ? eqString((CharSequence)var1, var0) : false;
                  }
               } else {
                  if (var0 instanceof ScriptableObject) {
                     Object var10 = ((ScriptableObject)var0).equivalentValues(var1);
                     if (var10 != Scriptable.NOT_FOUND) {
                        return (Boolean)var10;
                     }
                  }

                  if (var1 instanceof ScriptableObject) {
                     Object var9 = ((ScriptableObject)var1).equivalentValues(var0);
                     if (var9 != Scriptable.NOT_FOUND) {
                        return (Boolean)var9;
                     }
                  }

                  if (var0 instanceof Wrapper && var1 instanceof Wrapper) {
                     Object var7 = ((Wrapper)var0).unwrap();
                     Object var8 = ((Wrapper)var1).unwrap();
                     return var7 == var8 || isPrimitive(var7) && isPrimitive(var8) && eq(var7, var8);
                  } else {
                     return false;
                  }
               }
            } else {
               warnAboutNonJSObject(var0);
               return var0 == var1;
            }
         }
      } else if (var1 != null) {
         if (var1 == Undefined.instance) {
            return true;
         } else {
            if (var1 instanceof ScriptableObject) {
               Object var2 = ((ScriptableObject)var1).equivalentValues(var0);
               if (var2 != Scriptable.NOT_FOUND) {
                  return (Boolean)var2;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   static boolean eqNumber(double var0, Object var2) {
      while(true) {
         if (var2 != null) {
            if (var2 == Undefined.instance) {
               return false;
            }

            if (var2 instanceof Number) {
               double var13;
               int var11 = (var13 = var0 - ((Number)var2).doubleValue()) == 0.0D ? 0 : (var13 < 0.0D ? -1 : 1);
               boolean var12 = false;
               if (var11 == 0) {
                  var12 = true;
               }

               return var12;
            }

            if (var2 instanceof CharSequence) {
               double var14;
               int var9 = (var14 = var0 - toNumber(var2)) == 0.0D ? 0 : (var14 < 0.0D ? -1 : 1);
               boolean var10 = false;
               if (var9 == 0) {
                  var10 = true;
               }

               return var10;
            }

            if (var2 instanceof Boolean) {
               double var5;
               if ((Boolean)var2) {
                  var5 = 1.0D;
               } else {
                  var5 = 0.0D;
               }

               double var15;
               int var7 = (var15 = var0 - var5) == 0.0D ? 0 : (var15 < 0.0D ? -1 : 1);
               boolean var8 = false;
               if (var7 == 0) {
                  var8 = true;
               }

               return var8;
            }

            if (isSymbol(var2)) {
               return false;
            }

            if (var2 instanceof Scriptable) {
               if (var2 instanceof ScriptableObject) {
                  Number var3 = wrapNumber(var0);
                  Object var4 = ((ScriptableObject)var2).equivalentValues(var3);
                  if (var4 != Scriptable.NOT_FOUND) {
                     return (Boolean)var4;
                  }
               }

               var2 = toPrimitive(var2);
               continue;
            }

            warnAboutNonJSObject(var2);
            return false;
         }

         return false;
      }
   }

   private static boolean eqString(CharSequence var0, Object var1) {
      while(true) {
         if (var1 != null) {
            if (var1 == Undefined.instance) {
               return false;
            }

            if (var1 instanceof CharSequence) {
               CharSequence var11 = (CharSequence)var1;
               int var12 = var0.length();
               int var13 = var11.length();
               boolean var14 = false;
               if (var12 == var13) {
                  boolean var15 = var0.toString().equals(var11.toString());
                  var14 = false;
                  if (var15) {
                     var14 = true;
                  }
               }

               return var14;
            }

            if (var1 instanceof Number) {
               double var16;
               int var9 = (var16 = toNumber(var0.toString()) - ((Number)var1).doubleValue()) == 0.0D ? 0 : (var16 < 0.0D ? -1 : 1);
               boolean var10 = false;
               if (var9 == 0) {
                  var10 = true;
               }

               return var10;
            }

            if (var1 instanceof Boolean) {
               double var3 = toNumber(var0.toString());
               double var5;
               if ((Boolean)var1) {
                  var5 = 1.0D;
               } else {
                  var5 = 0.0D;
               }

               double var17;
               int var7 = (var17 = var3 - var5) == 0.0D ? 0 : (var17 < 0.0D ? -1 : 1);
               boolean var8 = false;
               if (var7 == 0) {
                  var8 = true;
               }

               return var8;
            }

            if (isSymbol(var1)) {
               return false;
            }

            if (var1 instanceof Scriptable) {
               if (var1 instanceof ScriptableObject) {
                  Object var2 = ((ScriptableObject)var1).equivalentValues(var0.toString());
                  if (var2 != Scriptable.NOT_FOUND) {
                     return (Boolean)var2;
                  }
               }

               var1 = toPrimitive(var1);
               continue;
            }

            warnAboutNonJSObject(var1);
            return false;
         }

         return false;
      }
   }

   private static RuntimeException errorWithClassName(String var0, Object var1) {
      return Context.reportRuntimeError1(var0, var1.getClass().getName());
   }

   public static String escapeAttributeValue(Object var0, Context var1) {
      return currentXMLLib(var1).escapeAttributeValue(var0);
   }

   public static String escapeString(String var0) {
      return escapeString(var0, '"');
   }

   public static String escapeString(String var0, char var1) {
      if (var1 != '"' && var1 != '\'' && var1 != '`') {
         Kit.codeBug();
      }

      StringBuilder var2 = null;
      int var3 = 0;

      for(int var4 = var0.length(); var3 != var4; ++var3) {
         char var5 = var0.charAt(var3);
         if (' ' <= var5 && var5 <= '~' && var5 != var1 && var5 != '\\') {
            if (var2 != null) {
               var2.append(var5);
            }
         } else {
            if (var2 == null) {
               var2 = new StringBuilder(var4 + 3);
               var2.append(var0);
               var2.setLength(var3);
            }

            byte var6 = -1;
            if (var5 != ' ') {
               if (var5 != '\\') {
                  switch(var5) {
                  case '\b':
                     var6 = 98;
                     break;
                  case '\t':
                     var6 = 116;
                     break;
                  case '\n':
                     var6 = 110;
                     break;
                  case '\u000b':
                     var6 = 118;
                     break;
                  case '\f':
                     var6 = 102;
                     break;
                  case '\r':
                     var6 = 114;
                  }
               } else {
                  var6 = 92;
               }
            } else {
               var6 = 32;
            }

            if (var6 >= 0) {
               var2.append('\\');
               var2.append((char)var6);
            } else if (var5 == var1) {
               var2.append('\\');
               var2.append(var1);
            } else {
               byte var8;
               if (var5 < 256) {
                  var2.append("\\x");
                  var8 = 2;
               } else {
                  var2.append("\\u");
                  var8 = 4;
               }

               for(int var9 = 4 * (var8 - 1); var9 >= 0; var9 -= 4) {
                  int var10 = 15 & var5 >> var9;
                  int var11;
                  if (var10 < 10) {
                     var11 = var10 + 48;
                  } else {
                     var11 = var10 + 87;
                  }

                  var2.append((char)var11);
               }
            }
         }
      }

      if (var2 == null) {
         return var0;
      } else {
         return var2.toString();
      }
   }

   public static String escapeTextValue(Object var0, Context var1) {
      return currentXMLLib(var1).escapeTextValue(var0);
   }

   public static Object evalSpecial(Context var0, Scriptable var1, Object var2, Object[] var3, String var4, int var5) {
      if (var3.length < 1) {
         return Undefined.instance;
      } else {
         Object var6 = var3[0];
         if (!(var6 instanceof CharSequence)) {
            if (!var0.hasFeature(11) && !var0.hasFeature(9)) {
               Context.reportWarning(getMessage0("msg.eval.nonstring"));
               return var6;
            } else {
               throw Context.reportRuntimeError0("msg.eval.nonstring.strict");
            }
         } else {
            String var7;
            int var8;
            if (var4 == null) {
               int[] var13 = new int[1];
               String var14 = Context.getSourcePositionFromStack(var13);
               if (var14 != null) {
                  var8 = var13[0];
                  var7 = var14;
               } else {
                  var8 = var5;
                  var7 = "";
               }
            } else {
               var7 = var4;
               var8 = var5;
            }

            String var9 = makeUrlForGeneratedScript(true, var7, var8);
            ErrorReporter var10 = DefaultErrorReporter.forEval(var0.getErrorReporter());
            Evaluator var11 = Context.createInterpreter();
            if (var11 != null) {
               Script var12 = var0.compileString(var6.toString(), var11, var10, var9, 1, (Object)null);
               var11.setEvalScriptFlag(var12);
               return ((Callable)var12).call(var0, var1, (Scriptable)var2, emptyArgs);
            } else {
               throw new JavaScriptException("Interpreter not present", var7, var8);
            }
         }
      }
   }

   public static void exitActivationFunction(Context var0) {
      NativeCall var1 = var0.currentActivationCall;
      var0.currentActivationCall = var1.parentActivationCall;
      var1.parentActivationCall = null;
   }

   static NativeCall findFunctionActivation(Context var0, Function var1) {
      for(NativeCall var2 = var0.currentActivationCall; var2 != null; var2 = var2.parentActivationCall) {
         if (var2.function == var1) {
            return var2;
         }
      }

      return null;
   }

   static Object[] getApplyArguments(Context var0, Object var1) {
      if (var1 != null && var1 != Undefined.instance) {
         if (var1 instanceof Scriptable && isArrayLike((Scriptable)var1)) {
            return var0.getElements((Scriptable)var1);
         } else if (var1 instanceof ScriptableObject) {
            return emptyArgs;
         } else {
            throw typeError0("msg.arg.isnt.array");
         }
      } else {
         return emptyArgs;
      }
   }

   public static Object[] getArrayElements(Scriptable var0) {
      long var1 = NativeArray.getLengthProperty(Context.getContext(), var0, false);
      if (var1 <= 2147483647L) {
         int var4 = (int)var1;
         if (var4 == 0) {
            return emptyArgs;
         } else {
            Object[] var5 = new Object[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               Object var7 = ScriptableObject.getProperty(var0, var6);
               Object var8;
               if (var7 == Scriptable.NOT_FOUND) {
                  var8 = Undefined.instance;
               } else {
                  var8 = var7;
               }

               var5[var6] = var8;
            }

            return var5;
         }
      } else {
         IllegalArgumentException var3 = new IllegalArgumentException();
         throw var3;
      }
   }

   static Callable getCallable(Scriptable var0) {
      if (var0 instanceof Callable) {
         return (Callable)var0;
      } else {
         Object var1 = var0.getDefaultValue(FunctionClass);
         if (var1 instanceof Callable) {
            return (Callable)var1;
         } else {
            throw notFunctionError(var1, var0);
         }
      }
   }

   @Deprecated
   public static Callable getElemFunctionAndThis(Object var0, Object var1, Context var2) {
      return getElemFunctionAndThis(var0, var1, var2, getTopCallScope(var2));
   }

   public static Callable getElemFunctionAndThis(Object var0, Object var1, Context var2, Scriptable var3) {
      Scriptable var8;
      Object var9;
      if (isSymbol(var1)) {
         var8 = toObjectOrNull(var2, var0, var3);
         if (var8 == null) {
            throw undefCallError(var0, String.valueOf(var1));
         }

         var9 = ScriptableObject.getProperty(var8, (Symbol)var1);
      } else {
         String var4 = toStringIdOrIndex(var2, var1);
         if (var4 != null) {
            return getPropFunctionAndThis(var0, var4, var2, var3);
         }

         int var5 = lastIndexResult(var2);
         Scriptable var6 = toObjectOrNull(var2, var0, var3);
         if (var6 == null) {
            throw undefCallError(var0, String.valueOf(var1));
         }

         Object var7 = ScriptableObject.getProperty(var6, var5);
         var8 = var6;
         var9 = var7;
      }

      if (var9 instanceof Callable) {
         storeScriptable(var2, var8);
         return (Callable)var9;
      } else {
         throw notFunctionError(var9, var1);
      }
   }

   static Function getExistingCtor(Context var0, Scriptable var1, String var2) {
      Object var3 = ScriptableObject.getProperty(var1, var2);
      if (var3 instanceof Function) {
         return (Function)var3;
      } else if (var3 == Scriptable.NOT_FOUND) {
         throw Context.reportRuntimeError1("msg.ctor.not.found", var2);
      } else {
         throw Context.reportRuntimeError1("msg.not.ctor", var2);
      }
   }

   public static ScriptableObject getGlobal(Context var0) {
      Class var1 = Kit.classOrNull("org.mozilla.javascript.tools.shell.Global");
      if (var1 != null) {
         try {
            Class[] var4 = new Class[]{ContextClass};
            ScriptableObject var5 = (ScriptableObject)var1.getConstructor(var4).newInstance(new Object[]{var0});
            return var5;
         } catch (RuntimeException var6) {
            throw var6;
         } catch (Exception var7) {
         }
      }

      return new ImporterTopLevel(var0);
   }

   static Object getIndexObject(double var0) {
      int var2 = (int)var0;
      return (double)var2 == var0 ? var2 : toString(var0);
   }

   static Object getIndexObject(String var0) {
      long var1 = indexFromString(var0);
      return var1 >= 0L ? (int)var1 : var0;
   }

   public static ScriptableObject getLibraryScopeOrNull(Scriptable var0) {
      return (ScriptableObject)ScriptableObject.getTopScopeValue(var0, LIBRARY_SCOPE_KEY);
   }

   public static String getMessage(String var0, Object[] var1) {
      return messageProvider.getMessage(var0, var1);
   }

   public static String getMessage0(String var0) {
      return getMessage(var0, (Object[])null);
   }

   public static String getMessage1(String var0, Object var1) {
      return getMessage(var0, new Object[]{var1});
   }

   public static String getMessage2(String var0, Object var1, Object var2) {
      return getMessage(var0, new Object[]{var1, var2});
   }

   public static String getMessage3(String var0, Object var1, Object var2, Object var3) {
      return getMessage(var0, new Object[]{var1, var2, var3});
   }

   public static String getMessage4(String var0, Object var1, Object var2, Object var3, Object var4) {
      return getMessage(var0, new Object[]{var1, var2, var3, var4});
   }

   public static Callable getNameFunctionAndThis(String var0, Context var1, Scriptable var2) {
      Scriptable var3 = var2.getParentScope();
      if (var3 == null) {
         Object var4 = topScopeName(var1, var2, var0);
         if (!(var4 instanceof Callable)) {
            if (var4 == Scriptable.NOT_FOUND) {
               throw notFoundError(var2, var0);
            } else {
               throw notFunctionError(var4, var0);
            }
         } else {
            storeScriptable(var1, var2);
            return (Callable)var4;
         }
      } else {
         return (Callable)nameOrFunction(var1, var2, var3, var0, true);
      }
   }

   @Deprecated
   public static Object getObjectElem(Object var0, Object var1, Context var2) {
      return getObjectElem(var0, var1, var2, getTopCallScope(var2));
   }

   public static Object getObjectElem(Object var0, Object var1, Context var2, Scriptable var3) {
      Scriptable var4 = toObjectOrNull(var2, var0, var3);
      if (var4 != null) {
         return getObjectElem(var4, var1, var2);
      } else {
         throw undefReadError(var0, var1);
      }
   }

   public static Object getObjectElem(Scriptable var0, Object var1, Context var2) {
      Object var4;
      if (var0 instanceof XMLObject) {
         var4 = ((XMLObject)var0).get(var2, var1);
      } else if (isSymbol(var1)) {
         var4 = ScriptableObject.getProperty(var0, (Symbol)var1);
      } else {
         String var3 = toStringIdOrIndex(var2, var1);
         if (var3 == null) {
            var4 = ScriptableObject.getProperty(var0, lastIndexResult(var2));
         } else {
            var4 = ScriptableObject.getProperty(var0, var3);
         }
      }

      if (var4 == Scriptable.NOT_FOUND) {
         var4 = Undefined.instance;
      }

      return var4;
   }

   @Deprecated
   public static Object getObjectIndex(Object var0, double var1, Context var3) {
      return getObjectIndex(var0, var1, var3, getTopCallScope(var3));
   }

   public static Object getObjectIndex(Object var0, double var1, Context var3, Scriptable var4) {
      Scriptable var5 = toObjectOrNull(var3, var0, var4);
      if (var5 != null) {
         int var6 = (int)var1;
         return (double)var6 == var1 ? getObjectIndex(var5, var6, var3) : getObjectProp(var5, toString(var1), var3);
      } else {
         throw undefReadError(var0, toString(var1));
      }
   }

   public static Object getObjectIndex(Scriptable var0, int var1, Context var2) {
      Object var3 = ScriptableObject.getProperty(var0, var1);
      if (var3 == Scriptable.NOT_FOUND) {
         var3 = Undefined.instance;
      }

      return var3;
   }

   @Deprecated
   public static Object getObjectProp(Object var0, String var1, Context var2) {
      return getObjectProp(var0, var1, var2, getTopCallScope(var2));
   }

   public static Object getObjectProp(Object var0, String var1, Context var2, Scriptable var3) {
      Scriptable var4 = toObjectOrNull(var2, var0, var3);
      if (var4 != null) {
         return getObjectProp(var4, var1, var2);
      } else {
         throw undefReadError(var0, var1);
      }
   }

   public static Object getObjectProp(Scriptable var0, String var1, Context var2) {
      Object var3 = ScriptableObject.getProperty(var0, var1);
      if (var3 == Scriptable.NOT_FOUND) {
         if (var2.hasFeature(11)) {
            Context.reportWarning(getMessage1("msg.ref.undefined.prop", var1));
         }

         var3 = Undefined.instance;
      }

      return var3;
   }

   @Deprecated
   public static Object getObjectPropNoWarn(Object var0, String var1, Context var2) {
      return getObjectPropNoWarn(var0, var1, var2, getTopCallScope(var2));
   }

   public static Object getObjectPropNoWarn(Object var0, String var1, Context var2, Scriptable var3) {
      Scriptable var4 = toObjectOrNull(var2, var0, var3);
      if (var4 != null) {
         Object var5 = ScriptableObject.getProperty(var4, var1);
         return var5 == Scriptable.NOT_FOUND ? Undefined.instance : var5;
      } else {
         throw undefReadError(var0, var1);
      }
   }

   @Deprecated
   public static Callable getPropFunctionAndThis(Object var0, String var1, Context var2) {
      return getPropFunctionAndThis(var0, var1, var2, getTopCallScope(var2));
   }

   public static Callable getPropFunctionAndThis(Object var0, String var1, Context var2, Scriptable var3) {
      return getPropFunctionAndThisHelper(var0, var1, var2, toObjectOrNull(var2, var0, var3));
   }

   private static Callable getPropFunctionAndThisHelper(Object var0, String var1, Context var2, Scriptable var3) {
      if (var3 != null) {
         Object var4 = ScriptableObject.getProperty(var3, var1);
         if (!(var4 instanceof Callable)) {
            Object var5 = ScriptableObject.getProperty(var3, "__noSuchMethod__");
            if (var5 instanceof Callable) {
               var4 = new ScriptRuntime.NoSuchMethodShim((Callable)var5, var1);
            }
         }

         if (var4 instanceof Callable) {
            storeScriptable(var2, var3);
            return (Callable)var4;
         } else {
            throw notFunctionError(var3, var4, var1);
         }
      } else {
         throw undefCallError(var0, var1);
      }
   }

   public static RegExpProxy getRegExpProxy(Context var0) {
      return var0.getRegExpProxy();
   }

   public static Scriptable getTopCallScope(Context var0) {
      Scriptable var1 = var0.topCallScope;
      if (var1 != null) {
         return var1;
      } else {
         throw new IllegalStateException();
      }
   }

   public static Object getTopLevelProp(Scriptable var0, String var1) {
      return ScriptableObject.getProperty(ScriptableObject.getTopLevelScope(var0), var1);
   }

   static String[] getTopPackageNames() {
      return "Dalvik".equals(System.getProperty("java.vm.name")) ? new String[]{"java", "javax", "org", "com", "edu", "net", "android"} : new String[]{"java", "javax", "org", "com", "edu", "net"};
   }

   public static Callable getValueFunctionAndThis(Object var0, Context var1) {
      if (var0 instanceof Callable) {
         Callable var2 = (Callable)var0;
         boolean var3 = var2 instanceof Scriptable;
         Scriptable var4 = null;
         if (var3) {
            var4 = ((Scriptable)var2).getParentScope();
         }

         if (var4 == null) {
            if (var1.topCallScope == null) {
               throw new IllegalStateException();
            }

            var4 = var1.topCallScope;
         }

         if (var4.getParentScope() != null && !(var4 instanceof NativeWith) && var4 instanceof NativeCall) {
            var4 = ScriptableObject.getTopLevelScope(var4);
         }

         storeScriptable(var1, var4);
         return var2;
      } else {
         throw notFunctionError(var0);
      }
   }

   public static boolean hasObjectElem(Scriptable var0, Object var1, Context var2) {
      if (isSymbol(var1)) {
         return ScriptableObject.hasProperty(var0, (Symbol)var1);
      } else {
         String var3 = toStringIdOrIndex(var2, var1);
         return var3 == null ? ScriptableObject.hasProperty(var0, lastIndexResult(var2)) : ScriptableObject.hasProperty(var0, var3);
      }
   }

   public static boolean hasTopCall(Context var0) {
      return var0.topCallScope != null;
   }

   public static boolean in(Object var0, Object var1, Context var2) {
      if (var1 instanceof Scriptable) {
         return hasObjectElem((Scriptable)var1, var0, var2);
      } else {
         throw typeError0("msg.in.not.object");
      }
   }

   public static long indexFromString(String var0) {
      boolean var4;
      int var7;
      label80: {
         int var1 = var0.length();
         if (var1 > 0) {
            char var2 = var0.charAt(0);
            byte var3 = 0;
            var4 = false;
            if (var2 == '-') {
               var3 = 0;
               var4 = false;
               if (var1 > 1) {
                  var2 = var0.charAt(1);
                  if (var2 == '0') {
                     return -1L;
                  }

                  var3 = 1;
                  var4 = true;
               }
            }

            int var5 = var2 - 48;
            if (var5 >= 0 && var5 <= 9) {
               byte var6;
               if (var4) {
                  var6 = 11;
               } else {
                  var6 = 10;
               }

               if (var1 <= var6) {
                  var7 = -var5;
                  int var8 = var3 + 1;
                  int var9 = 0;
                  if (var7 != 0) {
                     while(var8 != var1) {
                        int var12 = var0.charAt(var8) - 48;
                        var5 = var12;
                        if (var12 < 0 || var12 > 9) {
                           break;
                        }

                        var9 = var7;
                        var7 = var7 * 10 - var12;
                        ++var8;
                     }
                  }

                  if (var8 == var1) {
                     if (var9 > -214748364) {
                        break label80;
                     }

                     if (var9 == -214748364) {
                        byte var11;
                        if (var4) {
                           var11 = 8;
                        } else {
                           var11 = 7;
                        }

                        if (var5 <= var11) {
                           break label80;
                        }
                     }
                  }
               }
            }
         }

         return -1L;
      }

      int var10;
      if (var4) {
         var10 = var7;
      } else {
         var10 = -var7;
      }

      return 4294967295L & (long)var10;
   }

   public static void initFunction(Context var0, Scriptable var1, NativeFunction var2, int var3, boolean var4) {
      if (var3 == 1) {
         String var7 = var2.getFunctionName();
         if (var7 != null && var7.length() != 0) {
            if (!var4) {
               ScriptableObject.defineProperty(var1, var7, var2, 4);
            } else {
               var1.put(var7, var1, var2);
            }
         }

      } else if (var3 != 3) {
         RuntimeException var5 = Kit.codeBug();
         throw var5;
      } else {
         String var6 = var2.getFunctionName();
         if (var6 != null && var6.length() != 0) {
            while(var1 instanceof NativeWith) {
               var1 = var1.getParentScope();
            }

            var1.put(var6, var1, var2);
         }

      }
   }

   public static ScriptableObject initSafeStandardObjects(Context var0, ScriptableObject var1, boolean var2) {
      if (var1 == null) {
         var1 = new NativeObject();
      }

      ((ScriptableObject)var1).associateValue(LIBRARY_SCOPE_KEY, var1);
      (new ClassCache()).associate((ScriptableObject)var1);
      BaseFunction.init((Scriptable)var1, var2);
      NativeObject.init((Scriptable)var1, var2);
      Scriptable var5 = ScriptableObject.getObjectPrototype((Scriptable)var1);
      ScriptableObject.getClassPrototype((Scriptable)var1, "Function").setPrototype(var5);
      if (((ScriptableObject)var1).getPrototype() == null) {
         ((ScriptableObject)var1).setPrototype(var5);
      }

      NativeError.init((Scriptable)var1, var2);
      NativeGlobal.init(var0, (Scriptable)var1, var2);
      NativeArray.init((Scriptable)var1, var2);
      if (var0.getOptimizationLevel() > 0) {
         NativeArray.setMaximumInitialCapacity(200000);
      }

      NativeString.init((Scriptable)var1, var2);
      NativeBoolean.init((Scriptable)var1, var2);
      NativeNumber.init((Scriptable)var1, var2);
      NativeDate.init((Scriptable)var1, var2);
      NativeMath.init((Scriptable)var1, var2);
      NativeJSON.init((Scriptable)var1, var2);
      NativeWith.init((Scriptable)var1, var2);
      NativeCall.init((Scriptable)var1, var2);
      NativeScript.init((Scriptable)var1, var2);
      NativeIterator.init((ScriptableObject)var1, var2);
      NativeArrayIterator.init((ScriptableObject)var1, var2);
      NativeStringIterator.init((ScriptableObject)var1, var2);
      boolean var6;
      if (var0.hasFeature(6) && var0.getE4xImplementationFactory() != null) {
         var6 = true;
      } else {
         var6 = false;
      }

      new LazilyLoadedCtor((ScriptableObject)var1, "RegExp", "org.mozilla.javascript.regexp.NativeRegExp", var2, true);
      new LazilyLoadedCtor((ScriptableObject)var1, "Continuation", "org.mozilla.javascript.NativeContinuation", var2, true);
      if (var6) {
         String var23 = var0.getE4xImplementationFactory().getImplementationClassName();
         new LazilyLoadedCtor((ScriptableObject)var1, "XML", var23, var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "XMLList", var23, var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Namespace", var23, var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "QName", var23, var2, true);
      }

      if (var0.getLanguageVersion() >= 180 && var0.hasFeature(14) || var0.getLanguageVersion() >= 200) {
         new LazilyLoadedCtor((ScriptableObject)var1, "ArrayBuffer", "org.mozilla.javascript.typedarrays.NativeArrayBuffer", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Int8Array", "org.mozilla.javascript.typedarrays.NativeInt8Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Uint8Array", "org.mozilla.javascript.typedarrays.NativeUint8Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Uint8ClampedArray", "org.mozilla.javascript.typedarrays.NativeUint8ClampedArray", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Int16Array", "org.mozilla.javascript.typedarrays.NativeInt16Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Uint16Array", "org.mozilla.javascript.typedarrays.NativeUint16Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Int32Array", "org.mozilla.javascript.typedarrays.NativeInt32Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Uint32Array", "org.mozilla.javascript.typedarrays.NativeUint32Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Float32Array", "org.mozilla.javascript.typedarrays.NativeFloat32Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "Float64Array", "org.mozilla.javascript.typedarrays.NativeFloat64Array", var2, true);
         new LazilyLoadedCtor((ScriptableObject)var1, "DataView", "org.mozilla.javascript.typedarrays.NativeDataView", var2, true);
      }

      if (var0.getLanguageVersion() >= 200) {
         NativeSymbol.init(var0, (Scriptable)var1, var2);
         NativeCollectionIterator.init((ScriptableObject)var1, "Set Iterator", var2);
         NativeCollectionIterator.init((ScriptableObject)var1, "Map Iterator", var2);
         NativeMap.init(var0, (Scriptable)var1, var2);
         NativeSet.init(var0, (Scriptable)var1, var2);
         NativeWeakMap.init((Scriptable)var1, var2);
         NativeWeakSet.init((Scriptable)var1, var2);
      }

      if (var1 instanceof TopLevel) {
         ((TopLevel)var1).cacheBuiltins();
      }

      return (ScriptableObject)var1;
   }

   public static void initScript(NativeFunction var0, Scriptable var1, Context var2, Scriptable var3, boolean var4) {
      if (var2.topCallScope == null) {
         IllegalStateException var5 = new IllegalStateException();
         throw var5;
      } else {
         int var6 = var0.getParamAndVarCount();
         if (var6 != 0) {
            Scriptable var7;
            for(var7 = var3; var7 instanceof NativeWith; var7 = var7.getParentScope()) {
            }

            int var8 = var6;

            while(true) {
               int var9 = var8 - 1;
               if (var8 == 0) {
                  break;
               }

               String var10 = var0.getParamOrVarName(var9);
               boolean var11 = var0.getParamOrVarConst(var9);
               if (!ScriptableObject.hasProperty(var3, var10)) {
                  if (var11) {
                     ScriptableObject.defineConstProperty(var7, var10);
                  } else if (!var4) {
                     ScriptableObject.defineProperty(var7, var10, Undefined.instance, 4);
                  } else {
                     var7.put(var10, var7, Undefined.instance);
                  }
               } else {
                  ScriptableObject.redefineProperty(var3, var10, var11);
               }

               var8 = var9;
            }
         }

      }
   }

   public static ScriptableObject initStandardObjects(Context var0, ScriptableObject var1, boolean var2) {
      ScriptableObject var3 = initSafeStandardObjects(var0, var1, var2);
      new LazilyLoadedCtor(var3, "Packages", "org.mozilla.javascript.NativeJavaTopPackage", var2, true);
      new LazilyLoadedCtor(var3, "getClass", "org.mozilla.javascript.NativeJavaTopPackage", var2, true);
      new LazilyLoadedCtor(var3, "JavaAdapter", "org.mozilla.javascript.JavaAdapter", var2, true);
      new LazilyLoadedCtor(var3, "JavaImporter", "org.mozilla.javascript.ImporterTopLevel", var2, true);
      String[] var8 = getTopPackageNames();
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         new LazilyLoadedCtor(var3, var8[var10], "org.mozilla.javascript.NativeJavaTopPackage", var2, true);
      }

      return var3;
   }

   public static boolean instanceOf(Object var0, Object var1, Context var2) {
      if (var1 instanceof Scriptable) {
         return !(var0 instanceof Scriptable) ? false : ((Scriptable)var1).hasInstance((Scriptable)var0);
      } else {
         throw typeError0("msg.instanceof.not.object");
      }
   }

   private static boolean isArrayLike(Scriptable var0) {
      return var0 != null && (var0 instanceof NativeArray || var0 instanceof Arguments || ScriptableObject.hasProperty(var0, "length"));
   }

   public static boolean isArrayObject(Object var0) {
      return var0 instanceof NativeArray || var0 instanceof Arguments;
   }

   static boolean isGeneratedScript(String var0) {
      return var0.indexOf("(eval)") >= 0 || var0.indexOf("(Function)") >= 0;
   }

   public static boolean isJSLineTerminator(int var0) {
      if (('\udfd0' & var0) != 0) {
         return false;
      } else {
         boolean var1;
         if (var0 != 10 && var0 != 13 && var0 != 8232) {
            var1 = false;
            if (var0 != 8233) {
               return var1;
            }
         }

         var1 = true;
         return var1;
      }
   }

   public static boolean isJSWhitespaceOrLineTerminator(int var0) {
      return isStrWhiteSpaceChar(var0) || isJSLineTerminator(var0);
   }

   public static boolean isNaN(Object var0) {
      Double var1 = NaNobj;
      boolean var2 = true;
      if (var0 == var1) {
         return var2;
      } else if (var0 instanceof Double) {
         Double var4 = (Double)var0;
         if (var4 != NaN) {
            if (Double.isNaN(var4)) {
               return var2;
            }

            var2 = false;
         }

         return var2;
      } else if (var0 instanceof Float) {
         Float var3 = (Float)var0;
         if ((double)var3 != NaN) {
            if (Float.isNaN(var3)) {
               return var2;
            }

            var2 = false;
         }

         return var2;
      } else {
         return false;
      }
   }

   public static boolean isObject(Object var0) {
      if (var0 == null) {
         return false;
      } else if (Undefined.instance.equals(var0)) {
         return false;
      } else if (!(var0 instanceof ScriptableObject)) {
         return var0 instanceof Scriptable ? true ^ var0 instanceof Callable : false;
      } else {
         String var1 = ((ScriptableObject)var0).getTypeOf();
         boolean var2;
         if (!"object".equals(var1)) {
            boolean var3 = "function".equals(var1);
            var2 = false;
            if (!var3) {
               return var2;
            }
         }

         var2 = true;
         return var2;
      }
   }

   public static boolean isPrimitive(Object var0) {
      return var0 == null || var0 == Undefined.instance || var0 instanceof Number || var0 instanceof String || var0 instanceof Boolean;
   }

   public static boolean isRhinoRuntimeType(Class var0) {
      if (var0.isPrimitive()) {
         Class var3 = Character.TYPE;
         boolean var4 = false;
         if (var0 != var3) {
            var4 = true;
         }

         return var4;
      } else {
         boolean var1;
         if (var0 != StringClass && var0 != BooleanClass && !NumberClass.isAssignableFrom(var0)) {
            boolean var2 = ScriptableClass.isAssignableFrom(var0);
            var1 = false;
            if (!var2) {
               return var1;
            }
         }

         var1 = true;
         return var1;
      }
   }

   static boolean isSpecialProperty(String var0) {
      return var0.equals("__proto__") || var0.equals("__parent__");
   }

   static boolean isStrWhiteSpaceChar(int var0) {
      if (var0 != 32 && var0 != 160 && var0 != 65279 && var0 != 8232 && var0 != 8233) {
         switch(var0) {
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
            break;
         default:
            if (Character.getType(var0) == 12) {
               return true;
            }

            return false;
         }
      }

      return true;
   }

   static boolean isSymbol(Object var0) {
      return var0 instanceof NativeSymbol && ((NativeSymbol)var0).isSymbol() || var0 instanceof SymbolKey;
   }

   static boolean isValidIdentifierName(String var0, Context var1, boolean var2) {
      int var3 = var0.length();
      if (var3 == 0) {
         return false;
      } else if (!Character.isJavaIdentifierStart(var0.charAt(0))) {
         return false;
      } else {
         for(int var4 = 1; var4 != var3; ++var4) {
            if (!Character.isJavaIdentifierPart(var0.charAt(var4))) {
               return false;
            }
         }

         return true ^ TokenStream.isKeyword(var0, var1.getLanguageVersion(), var2);
      }
   }

   private static boolean isVisible(Context var0, Object var1) {
      ClassShutter var2 = var0.getClassShutter();
      return var2 == null || var2.visibleToScripts(var1.getClass().getName());
   }

   public static boolean jsDelegatesTo(Scriptable var0, Scriptable var1) {
      for(Scriptable var2 = var0.getPrototype(); var2 != null; var2 = var2.getPrototype()) {
         if (var2.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   static int lastIndexResult(Context var0) {
      return var0.scratchIndex;
   }

   public static Scriptable lastStoredScriptable(Context var0) {
      Scriptable var1 = var0.scratchScriptable;
      var0.scratchScriptable = null;
      return var1;
   }

   public static long lastUint32Result(Context var0) {
      long var1 = var0.scratchUint32;
      if (var1 >>> 32 == 0L) {
         return var1;
      } else {
         throw new IllegalStateException();
      }
   }

   public static Scriptable leaveDotQuery(Scriptable var0) {
      return ((NativeWith)var0).getParentScope();
   }

   public static Scriptable leaveWith(Scriptable var0) {
      return ((NativeWith)var0).getParentScope();
   }

   static String makeUrlForGeneratedScript(boolean var0, String var1, int var2) {
      if (var0) {
         StringBuilder var3 = new StringBuilder();
         var3.append(var1);
         var3.append('#');
         var3.append(var2);
         var3.append("(eval)");
         return var3.toString();
      } else {
         StringBuilder var8 = new StringBuilder();
         var8.append(var1);
         var8.append('#');
         var8.append(var2);
         var8.append("(Function)");
         return var8.toString();
      }
   }

   public static Ref memberRef(Object var0, Object var1, Object var2, Context var3, int var4) {
      if (var0 instanceof XMLObject) {
         return ((XMLObject)var0).memberRef(var3, var1, var2, var4);
      } else {
         throw notXmlError(var0);
      }
   }

   public static Ref memberRef(Object var0, Object var1, Context var2, int var3) {
      if (var0 instanceof XMLObject) {
         return ((XMLObject)var0).memberRef(var2, var1, var3);
      } else {
         throw notXmlError(var0);
      }
   }

   public static Object name(Context var0, Scriptable var1, String var2) {
      Scriptable var3 = var1.getParentScope();
      if (var3 == null) {
         Object var4 = topScopeName(var0, var1, var2);
         if (var4 != Scriptable.NOT_FOUND) {
            return var4;
         } else {
            throw notFoundError(var1, var2);
         }
      } else {
         return nameOrFunction(var0, var1, var3, var2, false);
      }
   }

   @Deprecated
   public static Object nameIncrDecr(Scriptable var0, String var1, int var2) {
      return nameIncrDecr(var0, var1, Context.getContext(), var2);
   }

   public static Object nameIncrDecr(Scriptable var0, String var1, Context var2, int var3) {
      do {
         if (var2.useDynamicScope && var0.getParentScope() == null) {
            var0 = checkDynamicScope(var2.topCallScope, var0);
         }

         Scriptable var4 = var0;

         while(!(var4 instanceof NativeWith) || !(var4.getPrototype() instanceof XMLObject)) {
            Object var5 = var4.get(var1, var0);
            if (var5 != Scriptable.NOT_FOUND) {
               return doScriptableIncrDecr(var4, var1, var0, var5, var3);
            }

            var4 = var4.getPrototype();
            if (var4 == null) {
               break;
            }
         }

         var0 = var0.getParentScope();
      } while(var0 != null);

      RuntimeException var6 = notFoundError(var0, var1);
      throw var6;
   }

   private static Object nameOrFunction(Context var0, Scriptable var1, Scriptable var2, String var3, boolean var4) {
      Object var5 = var1;
      XMLObject var6 = null;

      Object var7;
      while(true) {
         if (var1 instanceof NativeWith) {
            Scriptable var9 = var1.getPrototype();
            if (var9 instanceof XMLObject) {
               XMLObject var10 = (XMLObject)var9;
               if (var10.has(var3, var10)) {
                  var5 = var10;
                  var7 = var10.get(var3, var10);
                  break;
               }

               if (var6 == null) {
                  var6 = var10;
               }
            } else {
               var7 = ScriptableObject.getProperty(var9, var3);
               if (var7 != Scriptable.NOT_FOUND) {
                  var5 = var9;
                  break;
               }
            }
         } else if (var1 instanceof NativeCall) {
            var7 = var1.get(var3, var1);
            if (var7 != Scriptable.NOT_FOUND) {
               if (var4) {
                  var5 = ScriptableObject.getTopLevelScope(var2);
               }
               break;
            }
         } else {
            var7 = ScriptableObject.getProperty(var1, var3);
            if (var7 != Scriptable.NOT_FOUND) {
               var5 = var1;
               break;
            }
         }

         var1 = var2;
         var2 = var2.getParentScope();
         if (var2 == null) {
            Object var8 = topScopeName(var0, var1, var3);
            if (var8 == Scriptable.NOT_FOUND) {
               if (var6 == null || var4) {
                  throw notFoundError(var1, var3);
               }

               var7 = var6.get(var3, var6);
            } else {
               var7 = var8;
            }

            var5 = var1;
            break;
         }
      }

      if (var4) {
         if (var7 instanceof Callable) {
            storeScriptable(var0, (Scriptable)var5);
            return var7;
         } else {
            throw notFunctionError(var7, var3);
         }
      } else {
         return var7;
      }
   }

   public static Ref nameRef(Object var0, Object var1, Context var2, Scriptable var3, int var4) {
      return currentXMLLib(var2).nameRef(var2, var0, var1, var3, var4);
   }

   public static Ref nameRef(Object var0, Context var1, Scriptable var2, int var3) {
      return currentXMLLib(var1).nameRef(var1, var0, var2, var3);
   }

   public static Scriptable newArrayLiteral(Object[] var0, int[] var1, Context var2, Scriptable var3) {
      int var4 = var0.length;
      int var5 = 0;
      if (var1 != null) {
         var5 = var1.length;
      }

      int var6 = var4 + var5;
      if (var6 > 1 && var5 * 2 < var6) {
         Object[] var11;
         if (var5 == 0) {
            var11 = var0;
         } else {
            var11 = new Object[var6];
            int var12 = 0;
            int var13 = 0;

            for(int var14 = 0; var13 != var6; ++var13) {
               if (var12 != var5 && var1[var12] == var13) {
                  var11[var13] = Scriptable.NOT_FOUND;
                  ++var12;
               } else {
                  var11[var13] = var0[var14];
                  ++var14;
               }
            }
         }

         return var2.newArray(var3, var11);
      } else {
         Scriptable var7 = var2.newArray(var3, var6);
         int var8 = 0;
         int var9 = 0;

         for(int var10 = 0; var9 != var6; ++var9) {
            if (var8 != var5 && var1[var8] == var9) {
               ++var8;
            } else {
               var7.put(var9, var7, var0[var10]);
               ++var10;
            }
         }

         return var7;
      }
   }

   public static Scriptable newBuiltinObject(Context var0, Scriptable var1, TopLevel.Builtins var2, Object[] var3) {
      Scriptable var4 = ScriptableObject.getTopLevelScope(var1);
      Function var5 = TopLevel.getBuiltinCtor(var0, var4, var2);
      if (var3 == null) {
         var3 = emptyArgs;
      }

      return var5.construct(var0, var4, var3);
   }

   public static Scriptable newCatchScope(Throwable var0, Scriptable var1, String var2, Context var3, Scriptable var4) {
      boolean var5;
      Object var14;
      if (var0 instanceof JavaScriptException) {
         var14 = ((JavaScriptException)var0).getValue();
         var5 = false;
      } else {
         var5 = true;
         if (var1 != null) {
            Object var25 = ((NativeObject)var1).getAssociatedValue(var0);
            if (var25 == null) {
               Kit.codeBug();
            }

            var14 = var25;
         } else {
            Throwable var6 = null;
            Object var7;
            TopLevel.NativeErrors var8;
            String var9;
            if (var0 instanceof EcmaError) {
               EcmaError var24 = (EcmaError)var0;
               var7 = var24;
               var8 = TopLevel.NativeErrors.valueOf(var24.getName());
               var9 = var24.getErrorMessage();
               var6 = null;
            } else if (var0 instanceof WrappedException) {
               WrappedException var19 = (WrappedException)var0;
               var7 = var19;
               var6 = var19.getWrappedException();
               var8 = TopLevel.NativeErrors.JavaException;
               StringBuilder var20 = new StringBuilder();
               var20.append(var6.getClass().getName());
               var20.append(": ");
               var20.append(var6.getMessage());
               var9 = var20.toString();
            } else if (var0 instanceof EvaluatorException) {
               EvaluatorException var18 = (EvaluatorException)var0;
               var7 = var18;
               var8 = TopLevel.NativeErrors.InternalError;
               var9 = var18.getMessage();
               var6 = null;
            } else {
               if (!var3.hasFeature(13)) {
                  throw Kit.codeBug();
               }

               var7 = new WrappedException(var0);
               var8 = TopLevel.NativeErrors.JavaException;
               var9 = var0.toString();
            }

            String var10 = ((RhinoException)var7).sourceName();
            if (var10 == null) {
               var10 = "";
            }

            int var11 = ((RhinoException)var7).lineNumber();
            Object[] var12;
            if (var11 > 0) {
               Object[] var17 = new Object[]{var9, var10, var11};
               var12 = var17;
            } else {
               var12 = new Object[]{var9, var10};
            }

            Scriptable var13 = newNativeError(var3, var4, var8, var12);
            if (var13 instanceof NativeError) {
               ((NativeError)var13).setStackProvider((RhinoException)var7);
            }

            if (var6 != null && isVisible(var3, var6)) {
               ScriptableObject.defineProperty(var13, "javaException", var3.getWrapFactory().wrap(var3, var4, var6, (Class)null), 7);
            }

            if (isVisible(var3, var7)) {
               ScriptableObject.defineProperty(var13, "rhinoException", var3.getWrapFactory().wrap(var3, var4, var7, (Class)null), 7);
            }

            var14 = var13;
         }
      }

      NativeObject var15 = new NativeObject();
      var15.defineProperty(var2, var14, 4);
      if (isVisible(var3, var0)) {
         var15.defineProperty("__exception__", Context.javaToJS(var0, var4), 6);
      }

      if (var5) {
         var15.associateValue(var0, var14);
      }

      return var15;
   }

   static Scriptable newNativeError(Context var0, Scriptable var1, TopLevel.NativeErrors var2, Object[] var3) {
      Scriptable var4 = ScriptableObject.getTopLevelScope(var1);
      Function var5 = TopLevel.getNativeErrorCtor(var0, var4, var2);
      if (var3 == null) {
         var3 = emptyArgs;
      }

      return var5.construct(var0, var4, var3);
   }

   public static Scriptable newObject(Object var0, Context var1, Scriptable var2, Object[] var3) {
      if (var0 instanceof Function) {
         return ((Function)var0).construct(var1, var2, var3);
      } else {
         throw notFunctionError(var0);
      }
   }

   public static Scriptable newObject(Context var0, Scriptable var1, String var2, Object[] var3) {
      Scriptable var4 = ScriptableObject.getTopLevelScope(var1);
      Function var5 = getExistingCtor(var0, var4, var2);
      if (var3 == null) {
         var3 = emptyArgs;
      }

      return var5.construct(var0, var4, var3);
   }

   @Deprecated
   public static Scriptable newObjectLiteral(Object[] var0, Object[] var1, Context var2, Scriptable var3) {
      return newObjectLiteral(var0, var1, (int[])null, var2, var3);
   }

   public static Scriptable newObjectLiteral(Object[] var0, Object[] var1, int[] var2, Context var3, Scriptable var4) {
      Scriptable var5 = var3.newObject(var4);
      int var6 = 0;

      for(int var7 = var0.length; var6 != var7; ++var6) {
         Object var8 = var0[var6];
         int var9;
         if (var2 == null) {
            var9 = 0;
         } else {
            var9 = var2[var6];
         }

         Object var10 = var1[var6];
         if (var8 instanceof String) {
            if (var9 == 0) {
               if (isSpecialProperty((String)var8)) {
                  specialRef(var5, (String)var8, var3, var4).set(var3, var4, var10);
               } else {
                  var5.put((String)var8, var5, var10);
               }
            } else {
               ScriptableObject var11 = (ScriptableObject)var5;
               Callable var12 = (Callable)var10;
               byte var13 = 1;
               if (var9 != var13) {
                  var13 = 0;
               }

               var11.setGetterOrSetter((String)var8, 0, var12, (boolean)var13);
            }
         } else {
            var5.put((Integer)var8, var5, var10);
         }
      }

      return var5;
   }

   public static Object newSpecial(Context var0, Object var1, Object[] var2, Scriptable var3, int var4) {
      if (var4 == 1) {
         if (NativeGlobal.isEvalFunction(var1)) {
            throw typeError1("msg.not.ctor", "eval");
         }
      } else {
         if (var4 != 2) {
            throw Kit.codeBug();
         }

         if (NativeWith.isWithFunction(var1)) {
            return NativeWith.newWithSpecial(var0, var3, var2);
         }
      }

      return newObject(var1, var0, var3, var2);
   }

   public static RuntimeException notFoundError(Scriptable var0, String var1) {
      throw constructError("ReferenceError", getMessage1("msg.is.not.defined", var1));
   }

   public static RuntimeException notFunctionError(Object var0) {
      return notFunctionError(var0, var0);
   }

   public static RuntimeException notFunctionError(Object var0, Object var1) {
      String var2;
      if (var1 == null) {
         var2 = "null";
      } else {
         var2 = var1.toString();
      }

      return var0 == Scriptable.NOT_FOUND ? typeError1("msg.function.not.found", var2) : typeError2("msg.isnt.function", var2, typeof(var0));
   }

   public static RuntimeException notFunctionError(Object var0, Object var1, String var2) {
      String var3 = toString(var0);
      if (var0 instanceof NativeFunction) {
         int var4 = var3.indexOf(123, var3.indexOf(41));
         if (var4 > -1) {
            StringBuilder var5 = new StringBuilder();
            var5.append(var3.substring(0, var4 + 1));
            var5.append("...}");
            var3 = var5.toString();
         }
      }

      return var1 == Scriptable.NOT_FOUND ? typeError2("msg.function.not.found.in", var2, var3) : typeError3("msg.isnt.function.in", var2, var3, typeof(var1));
   }

   private static RuntimeException notXmlError(Object var0) {
      throw typeError1("msg.isnt.xml.object", toString(var0));
   }

   public static String numberToString(double var0, int var2) {
      if (var2 >= 2 && var2 <= 36) {
         if (var0 != var0) {
            return "NaN";
         } else if (var0 == Double.POSITIVE_INFINITY) {
            return "Infinity";
         } else if (var0 == Double.NEGATIVE_INFINITY) {
            return "-Infinity";
         } else if (var0 == 0.0D) {
            return "0";
         } else if (var2 != 10) {
            return DToA.JS_dtobasestr(var2, var0);
         } else {
            String var3 = FastDtoa.numberToString(var0);
            if (var3 != null) {
               return var3;
            } else {
               StringBuilder var4 = new StringBuilder();
               DToA.JS_dtostr(var4, 0, 0, var0);
               return var4.toString();
            }
         }
      } else {
         throw Context.reportRuntimeError1("msg.bad.radix", Integer.toString(var2));
      }
   }

   public static Object[] padArguments(Object[] var0, int var1) {
      if (var1 < var0.length) {
         return var0;
      } else {
         Object[] var2 = new Object[var1];

         int var3;
         for(var3 = 0; var3 < var0.length; ++var3) {
            var2[var3] = var0[var3];
         }

         while(var3 < var1) {
            var2[var3] = Undefined.instance;
            ++var3;
         }

         return var2;
      }
   }

   @Deprecated
   public static Object propIncrDecr(Object var0, String var1, Context var2, int var3) {
      return propIncrDecr(var0, var1, var2, getTopCallScope(var2), var3);
   }

   public static Object propIncrDecr(Object var0, String var1, Context var2, Scriptable var3, int var4) {
      Scriptable var5 = toObjectOrNull(var2, var0, var3);
      if (var5 == null) {
         RuntimeException var6 = undefReadError(var0, var1);
         throw var6;
      } else {
         Scriptable var7 = var5;

         do {
            Object var8 = var7.get(var1, var5);
            if (var8 != Scriptable.NOT_FOUND) {
               return doScriptableIncrDecr(var7, var1, var5, var8, var4);
            }

            var7 = var7.getPrototype();
         } while(var7 != null);

         Double var9 = NaNobj;
         var5.put(var1, var5, var9);
         return var9;
      }
   }

   public static EcmaError rangeError(String var0) {
      return constructError("RangeError", var0);
   }

   public static Object refDel(Ref var0, Context var1) {
      return wrapBoolean(var0.delete(var1));
   }

   public static Object refGet(Ref var0, Context var1) {
      return var0.get(var1);
   }

   @Deprecated
   public static Object refIncrDecr(Ref var0, Context var1, int var2) {
      return refIncrDecr(var0, var1, getTopCallScope(var1), var2);
   }

   public static Object refIncrDecr(Ref var0, Context var1, Scriptable var2, int var3) {
      Object var4 = var0.get(var1);
      boolean var5;
      if ((var3 & 2) != 0) {
         var5 = true;
      } else {
         var5 = false;
      }

      double var6;
      if (var4 instanceof Number) {
         var6 = ((Number)var4).doubleValue();
      } else {
         var6 = toNumber(var4);
         if (var5) {
            var4 = wrapNumber(var6);
         }
      }

      double var8;
      if ((var3 & 1) == 0) {
         var8 = var6 + 1.0D;
      } else {
         var8 = var6 - 1.0D;
      }

      Number var10 = wrapNumber(var8);
      var0.set(var1, var2, var10);
      return var5 ? var4 : var10;
   }

   @Deprecated
   public static Object refSet(Ref var0, Object var1, Context var2) {
      return refSet(var0, var1, var2, getTopCallScope(var2));
   }

   public static Object refSet(Ref var0, Object var1, Context var2, Scriptable var3) {
      return var0.set(var2, var3, var1);
   }

   public static boolean same(Object var0, Object var1) {
      if (!typeof(var0).equals(typeof(var1))) {
         return false;
      } else if (var0 instanceof Number) {
         return isNaN(var0) && isNaN(var1) ? true : var0.equals(var1);
      } else {
         return eq(var0, var1);
      }
   }

   public static boolean sameZero(Object var0, Object var1) {
      if (!typeof(var0).equals(typeof(var1))) {
         return false;
      } else if (var0 instanceof Number) {
         if (isNaN(var0) && isNaN(var1)) {
            return true;
         } else {
            double var2 = ((Number)var0).doubleValue();
            if (var1 instanceof Number) {
               double var4 = ((Number)var1).doubleValue();
               double var6 = negativeZero;
               if (var2 == var6 && var4 == 0.0D || var2 == 0.0D && var4 == var6) {
                  return true;
               }
            }

            return eqNumber(var2, var1);
         }
      } else {
         return eq(var0, var1);
      }
   }

   public static Object searchDefaultNamespace(Context var0) {
      Object var1 = var0.currentActivationCall;
      if (var1 == null) {
         var1 = getTopCallScope(var0);
      }

      Object var3;
      while(true) {
         Scriptable var2 = ((Scriptable)var1).getParentScope();
         if (var2 == null) {
            var3 = ScriptableObject.getProperty((Scriptable)var1, (String)"__default_namespace__");
            if (var3 == Scriptable.NOT_FOUND) {
               return null;
            }
            break;
         }

         var3 = ((Scriptable)var1).get("__default_namespace__", (Scriptable)var1);
         if (var3 != Scriptable.NOT_FOUND) {
            break;
         }

         var1 = var2;
      }

      return var3;
   }

   public static void setBuiltinProtoAndParent(ScriptableObject var0, Scriptable var1, TopLevel.Builtins var2) {
      Scriptable var3 = ScriptableObject.getTopLevelScope(var1);
      var0.setParentScope(var3);
      var0.setPrototype(TopLevel.getBuiltinPrototype(var3, var2));
   }

   public static Object setConst(Scriptable var0, Object var1, Context var2, String var3) {
      if (var0 instanceof XMLObject) {
         var0.put(var3, var0, var1);
         return var1;
      } else {
         ScriptableObject.putConstProperty(var0, var3, var1);
         return var1;
      }
   }

   public static Object setDefaultNamespace(Object var0, Context var1) {
      Object var2 = var1.currentActivationCall;
      if (var2 == null) {
         var2 = getTopCallScope(var1);
      }

      Object var3 = currentXMLLib(var1).toDefaultXmlNamespace(var1, var0);
      if (!((Scriptable)var2).has("__default_namespace__", (Scriptable)var2)) {
         ScriptableObject.defineProperty((Scriptable)var2, "__default_namespace__", var3, 6);
      } else {
         ((Scriptable)var2).put("__default_namespace__", (Scriptable)var2, var3);
      }

      return Undefined.instance;
   }

   public static void setEnumNumbers(Object var0, boolean var1) {
      ((ScriptRuntime.IdEnumeration)var0).enumNumbers = var1;
   }

   public static void setFunctionProtoAndParent(BaseFunction var0, Scriptable var1) {
      var0.setParentScope(var1);
      var0.setPrototype(ScriptableObject.getFunctionPrototype(var1));
   }

   public static Object setName(Scriptable var0, Object var1, Context var2, Scriptable var3, String var4) {
      if (var0 != null) {
         ScriptableObject.putProperty(var0, var4, var1);
         return var1;
      } else {
         if (var2.hasFeature(11) || var2.hasFeature(8)) {
            Context.reportWarning(getMessage1("msg.assn.create.strict", var4));
         }

         Scriptable var5 = ScriptableObject.getTopLevelScope(var3);
         if (var2.useDynamicScope) {
            var5 = checkDynamicScope(var2.topCallScope, var5);
         }

         var5.put(var4, var5, var1);
         return var1;
      }
   }

   @Deprecated
   public static Object setObjectElem(Object var0, Object var1, Object var2, Context var3) {
      return setObjectElem(var0, var1, var2, var3, getTopCallScope(var3));
   }

   public static Object setObjectElem(Object var0, Object var1, Object var2, Context var3, Scriptable var4) {
      Scriptable var5 = toObjectOrNull(var3, var0, var4);
      if (var5 != null) {
         return setObjectElem(var5, var1, var2, var3);
      } else {
         throw undefWriteError(var0, var1, var2);
      }
   }

   public static Object setObjectElem(Scriptable var0, Object var1, Object var2, Context var3) {
      if (var0 instanceof XMLObject) {
         ((XMLObject)var0).put(var3, var1, var2);
         return var2;
      } else if (isSymbol(var1)) {
         ScriptableObject.putProperty(var0, (Symbol)var1, var2);
         return var2;
      } else {
         String var4 = toStringIdOrIndex(var3, var1);
         if (var4 == null) {
            ScriptableObject.putProperty(var0, lastIndexResult(var3), var2);
            return var2;
         } else {
            ScriptableObject.putProperty(var0, var4, var2);
            return var2;
         }
      }
   }

   @Deprecated
   public static Object setObjectIndex(Object var0, double var1, Object var3, Context var4) {
      return setObjectIndex(var0, var1, var3, var4, getTopCallScope(var4));
   }

   public static Object setObjectIndex(Object var0, double var1, Object var3, Context var4, Scriptable var5) {
      Scriptable var6 = toObjectOrNull(var4, var0, var5);
      if (var6 != null) {
         int var7 = (int)var1;
         return (double)var7 == var1 ? setObjectIndex(var6, var7, var3, var4) : setObjectProp(var6, toString(var1), var3, var4);
      } else {
         throw undefWriteError(var0, String.valueOf(var1), var3);
      }
   }

   public static Object setObjectIndex(Scriptable var0, int var1, Object var2, Context var3) {
      ScriptableObject.putProperty(var0, var1, var2);
      return var2;
   }

   @Deprecated
   public static Object setObjectProp(Object var0, String var1, Object var2, Context var3) {
      return setObjectProp(var0, var1, var2, var3, getTopCallScope(var3));
   }

   public static Object setObjectProp(Object var0, String var1, Object var2, Context var3, Scriptable var4) {
      Scriptable var5 = toObjectOrNull(var3, var0, var4);
      if (var5 != null) {
         return setObjectProp(var5, var1, var2, var3);
      } else {
         throw undefWriteError(var0, var1, var2);
      }
   }

   public static Object setObjectProp(Scriptable var0, String var1, Object var2, Context var3) {
      ScriptableObject.putProperty(var0, var1, var2);
      return var2;
   }

   public static void setObjectProtoAndParent(ScriptableObject var0, Scriptable var1) {
      Scriptable var2 = ScriptableObject.getTopLevelScope(var1);
      var0.setParentScope(var2);
      var0.setPrototype(ScriptableObject.getClassPrototype(var2, var0.getClassName()));
   }

   public static void setRegExpProxy(Context var0, RegExpProxy var1) {
      if (var1 != null) {
         var0.regExpProxy = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean shallowEq(Object var0, Object var1) {
      if (var0 == var1) {
         if (!(var0 instanceof Number)) {
            return true;
         } else {
            double var8 = ((Number)var0).doubleValue();
            double var12;
            int var10 = (var12 = var8 - var8) == 0.0D ? 0 : (var12 < 0.0D ? -1 : 1);
            boolean var11 = false;
            if (var10 == 0) {
               var11 = true;
            }

            return var11;
         }
      } else if (var0 != null && var0 != Undefined.instance && var0 != Undefined.SCRIPTABLE_UNDEFINED) {
         if (var0 instanceof Number) {
            if (var1 instanceof Number) {
               double var13;
               int var6 = (var13 = ((Number)var0).doubleValue() - ((Number)var1).doubleValue()) == 0.0D ? 0 : (var13 < 0.0D ? -1 : 1);
               boolean var7 = false;
               if (var6 == 0) {
                  var7 = true;
               }

               return var7;
            }
         } else if (var0 instanceof CharSequence) {
            if (var1 instanceof CharSequence) {
               return var0.toString().equals(var1.toString());
            }
         } else if (var0 instanceof Boolean) {
            if (var1 instanceof Boolean) {
               return var0.equals(var1);
            }
         } else {
            if (!(var0 instanceof Scriptable)) {
               warnAboutNonJSObject(var0);
               boolean var2 = false;
               if (var0 == var1) {
                  var2 = true;
               }

               return var2;
            }

            if (var0 instanceof Wrapper && var1 instanceof Wrapper) {
               Object var3 = ((Wrapper)var0).unwrap();
               Object var4 = ((Wrapper)var1).unwrap();
               boolean var5 = false;
               if (var3 == var4) {
                  var5 = true;
               }

               return var5;
            }
         }

         return false;
      } else {
         return var0 == Undefined.instance && var1 == Undefined.SCRIPTABLE_UNDEFINED || var0 == Undefined.SCRIPTABLE_UNDEFINED && var1 == Undefined.instance;
      }
   }

   @Deprecated
   public static Ref specialRef(Object var0, String var1, Context var2) {
      return specialRef(var0, var1, var2, getTopCallScope(var2));
   }

   public static Ref specialRef(Object var0, String var1, Context var2, Scriptable var3) {
      return SpecialRef.createSpecial(var2, var3, var0, var1);
   }

   private static void storeIndexResult(Context var0, int var1) {
      var0.scratchIndex = var1;
   }

   private static void storeScriptable(Context var0, Scriptable var1) {
      if (var0.scratchScriptable == null) {
         var0.scratchScriptable = var1;
      } else {
         throw new IllegalStateException();
      }
   }

   public static void storeUint32Result(Context var0, long var1) {
      if (var1 >>> 32 == 0L) {
         var0.scratchUint32 = var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Object strictSetName(Scriptable var0, Object var1, Context var2, Scriptable var3, String var4) {
      if (var0 != null) {
         ScriptableObject.putProperty(var0, var4, var1);
         return var1;
      } else {
         StringBuilder var5 = new StringBuilder();
         var5.append("Assignment to undefined \"");
         var5.append(var4);
         var5.append("\" in strict mode");
         throw constructError("ReferenceError", var5.toString());
      }
   }

   static double stringPrefixToNumber(String var0, int var1, int var2) {
      return stringToNumber(var0, var1, var0.length() - 1, var2, true);
   }

   static double stringToNumber(String var0, int var1, int var2, int var3) {
      return stringToNumber(var0, var1, var2, var3, false);
   }

   private static double stringToNumber(String var0, int var1, int var2, int var3, boolean var4) {
      char var5 = 'a';
      char var6 = 'A';
      byte var7 = 10;
      char var8;
      if (var3 < var7) {
         var8 = (char)(var3 + 48 - 1);
      } else {
         var8 = '9';
      }

      if (var3 > var7) {
         var5 = (char)(var3 + 97 - var7);
         var6 = (char)(var3 + 65 - var7);
      }

      double var9 = 0.0D;
      int var11 = var1;

      while(true) {
         if (var11 > var2) {
            break;
         }

         char var32 = var0.charAt(var11);
         int var35;
         if ('0' <= var32 && var32 <= var8) {
            var35 = var32 - 48;
         } else if ('a' <= var32 && var32 < var5) {
            var35 = var7 + (var32 - 97);
         } else {
            if ('A' > var32 || var32 >= var6) {
               if (!var4) {
                  return NaN;
               }
               break;
            }

            var35 = var7 + (var32 - 65);
         }

         double var36 = (double)var3;
         Double.isNaN(var36);
         double var39 = var36 * var9;
         double var43 = (double)var35;
         Double.isNaN(var43);
         var9 = var39 + var43;
         ++var11;
         var5 = var5;
         var6 = var6;
         var7 = 10;
      }

      if (var1 == var11) {
         return NaN;
      } else {
         if (var9 > 9.007199254740991E15D) {
            if (var3 == 10) {
               try {
                  double var30 = Double.parseDouble(var0.substring(var1, var11));
                  return var30;
               } catch (NumberFormatException var45) {
                  return NaN;
               }
            }

            if (var3 == 2 || var3 == 4 || var3 == 8 || var3 == 16 || var3 == 32) {
               int var14 = 1;
               int var15 = 0;
               int var16 = 53;
               double var17 = 0.0D;
               boolean var19 = false;
               boolean var20 = false;
               byte var21 = 0;
               int var22 = var1;

               while(true) {
                  label140:
                  while(true) {
                     while(true) {
                        while(true) {
                           while(true) {
                              if (var14 == 1) {
                                 if (var22 == var11) {
                                    if (var21 != 0) {
                                       if (var21 != 3) {
                                          if (var21 != 4) {
                                             return var9;
                                          }

                                          if (var20) {
                                             ++var9;
                                          }

                                          return var9 * var17;
                                       }

                                       if (var20 & var19) {
                                          ++var9;
                                       }

                                       return var9 * var17;
                                    }

                                    return 0.0D;
                                 }

                                 int var26 = var22 + 1;
                                 char var27 = var0.charAt(var22);
                                 int var28;
                                 if ('0' <= var27 && var27 <= '9') {
                                    var28 = var27 - 48;
                                 } else if ('a' <= var27 && var27 <= 'z') {
                                    var28 = var27 - 87;
                                 } else {
                                    var28 = var27 - 55;
                                 }

                                 var14 = var3;
                                 var15 = var28;
                                 var22 = var26;
                              }

                              var14 >>= 1;
                              boolean var23;
                              if ((var15 & var14) != 0) {
                                 var23 = true;
                              } else {
                                 var23 = false;
                              }

                              if (var21 != 0) {
                                 if (var21 != 1) {
                                    if (var21 != 2) {
                                       if (var21 != 3) {
                                          if (var21 != 4) {
                                             continue;
                                          }
                                          break label140;
                                       }

                                       if (var23) {
                                          var21 = 4;
                                       }
                                       break label140;
                                    } else {
                                       var17 = 2.0D;
                                       var21 = 3;
                                       var20 = var23;
                                    }
                                 } else {
                                    var9 *= 2.0D;
                                    if (var23) {
                                       ++var9;
                                    }

                                    --var16;
                                    if (var16 == 0) {
                                       var21 = 2;
                                       var19 = var23;
                                    }
                                 }
                              } else if (var23) {
                                 --var16;
                                 var9 = 1.0D;
                                 var21 = 1;
                              }
                           }
                        }
                     }
                  }

                  var17 *= 2.0D;
               }
            }
         }

         return var9;
      }
   }

   public static long testUint32String(String var0) {
      int var1 = var0.length();
      long var2 = -1L;
      if (1 <= var1 && var1 <= 10) {
         int var4 = -48 + var0.charAt(0);
         if (var4 == 0) {
            if (var1 == 1) {
               var2 = 0L;
            }

            return var2;
         }

         if (1 <= var4 && var4 <= 9) {
            long var5 = (long)var4;

            for(int var7 = 1; var7 != var1; ++var7) {
               int var8 = -48 + var0.charAt(var7);
               if (var8 < 0) {
                  return var2;
               }

               if (var8 > 9) {
                  return var2;
               }

               var5 = 10L * var5 + (long)var8;
            }

            if (var5 >>> 32 == 0L) {
               return var5;
            }
         }
      }

      return var2;
   }

   public static JavaScriptException throwCustomError(Context var0, Scriptable var1, String var2, String var3) {
      int[] var4 = new int[]{0};
      String var5 = Context.getSourcePositionFromStack(var4);
      Object[] var6 = new Object[]{var3, var5, var4[0]};
      return new JavaScriptException(var0.newObject(var1, var2, var6), var5, var4[0]);
   }

   public static JavaScriptException throwError(Context var0, Scriptable var1, String var2) {
      int[] var3 = new int[]{0};
      String var4 = Context.getSourcePositionFromStack(var3);
      TopLevel.Builtins var5 = TopLevel.Builtins.Error;
      Object[] var6 = new Object[]{var2, var4, var3[0]};
      return new JavaScriptException(newBuiltinObject(var0, var1, var5, var6), var4, var3[0]);
   }

   public static boolean toBoolean(Object var0) {
      while(!(var0 instanceof Boolean)) {
         if (var0 != null) {
            if (var0 == Undefined.instance) {
               return false;
            }

            if (var0 instanceof CharSequence) {
               int var6 = ((CharSequence)var0).length();
               boolean var7 = false;
               if (var6 != 0) {
                  var7 = true;
               }

               return var7;
            }

            if (var0 instanceof Number) {
               double var1 = ((Number)var0).doubleValue();
               double var8;
               int var3 = (var8 = var1 - var1) == 0.0D ? 0 : (var8 < 0.0D ? -1 : 1);
               boolean var4 = false;
               if (var3 == 0) {
                  double var9;
                  int var5 = (var9 = var1 - 0.0D) == 0.0D ? 0 : (var9 < 0.0D ? -1 : 1);
                  var4 = false;
                  if (var5 != 0) {
                     var4 = true;
                  }
               }

               return var4;
            }

            if (var0 instanceof Scriptable) {
               if (var0 instanceof ScriptableObject && ((ScriptableObject)var0).avoidObjectDetection()) {
                  return false;
               }

               if (Context.getContext().isVersionECMA1()) {
                  return true;
               }

               var0 = ((Scriptable)var0).getDefaultValue(BooleanClass);
               if (!(var0 instanceof Scriptable) || isSymbol(var0)) {
                  continue;
               }

               throw errorWithClassName("msg.primitive.expected", var0);
            }

            warnAboutNonJSObject(var0);
            return true;
         }

         return false;
      }

      return (Boolean)var0;
   }

   public static CharSequence toCharSequence(Object var0) {
      if (var0 instanceof NativeString) {
         return ((NativeString)var0).toCharSequence();
      } else {
         return (CharSequence)(var0 instanceof CharSequence ? (CharSequence)var0 : toString(var0));
      }
   }

   public static int toInt32(double var0) {
      return DoubleConversion.doubleToInt32(var0);
   }

   public static int toInt32(Object var0) {
      return var0 instanceof Integer ? (Integer)var0 : toInt32(toNumber(var0));
   }

   public static int toInt32(Object[] var0, int var1) {
      return var1 < var0.length ? toInt32(var0[var1]) : 0;
   }

   public static double toInteger(double var0) {
      if (var0 != var0) {
         return 0.0D;
      } else if (var0 != 0.0D && var0 != Double.POSITIVE_INFINITY) {
         if (var0 == Double.NEGATIVE_INFINITY) {
            return var0;
         } else {
            return var0 > 0.0D ? Math.floor(var0) : Math.ceil(var0);
         }
      } else {
         return var0;
      }
   }

   public static double toInteger(Object var0) {
      return toInteger(toNumber(var0));
   }

   public static double toInteger(Object[] var0, int var1) {
      return var1 < var0.length ? toInteger(var0[var1]) : 0.0D;
   }

   public static Scriptable toIterator(Context var0, Scriptable var1, Scriptable var2, boolean var3) {
      if (ScriptableObject.hasProperty(var2, "__iterator__")) {
         Object var4 = ScriptableObject.getProperty(var2, "__iterator__");
         if (var4 instanceof Callable) {
            Callable var5 = (Callable)var4;
            Object[] var6 = new Object[1];
            Boolean var7;
            if (var3) {
               var7 = Boolean.TRUE;
            } else {
               var7 = Boolean.FALSE;
            }

            var6[0] = var7;
            Object var8 = var5.call(var0, var1, var2, var6);
            if (var8 instanceof Scriptable) {
               return (Scriptable)var8;
            } else {
               throw typeError0("msg.iterator.primitive");
            }
         } else {
            throw typeError0("msg.invalid.iterator");
         }
      } else {
         return null;
      }
   }

   public static long toLength(Object[] var0, int var1) {
      double var2 = toInteger(var0, var1);
      return var2 <= 0.0D ? 0L : (long)Math.min(var2, 9.007199254740991E15D);
   }

   public static double toNumber(Object var0) {
      while(!(var0 instanceof Number)) {
         double var1 = 0.0D;
         if (var0 == null) {
            return var1;
         }

         if (var0 == Undefined.instance) {
            return NaN;
         }

         if (var0 instanceof String) {
            return toNumber((String)var0);
         }

         if (var0 instanceof CharSequence) {
            return toNumber(var0.toString());
         }

         if (var0 instanceof Boolean) {
            if ((Boolean)var0) {
               var1 = 1.0D;
            }

            return var1;
         }

         if (!(var0 instanceof Symbol)) {
            if (var0 instanceof Scriptable) {
               var0 = ((Scriptable)var0).getDefaultValue(NumberClass);
               if (!(var0 instanceof Scriptable) || isSymbol(var0)) {
                  continue;
               }

               throw errorWithClassName("msg.primitive.expected", var0);
            }

            warnAboutNonJSObject(var0);
            return NaN;
         }

         EcmaError var3 = typeError0("msg.not.a.number");
         throw var3;
      }

      return ((Number)var0).doubleValue();
   }

   public static double toNumber(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 != var1; ++var2) {
         char var3 = var0.charAt(var2);
         if (!isStrWhiteSpaceChar(var3)) {
            int var4 = var1 - 1;

            while(true) {
               char var5 = var0.charAt(var4);
               if (!isStrWhiteSpaceChar(var5)) {
                  Context var6 = Context.getCurrentContext();
                  boolean var7;
                  if (var6 != null && var6.getLanguageVersion() >= 200) {
                     var7 = false;
                  } else {
                     var7 = true;
                  }

                  if (var3 == '0') {
                     if (var2 + 2 <= var4) {
                        char var17 = var0.charAt(var2 + 1);
                        byte var18;
                        if (var17 != 'x' && var17 != 'X') {
                           if (!var7 && (var17 == 'o' || var17 == 'O')) {
                              var18 = 8;
                           } else if (var7 || var17 != 'b' && var17 != 'B') {
                              var18 = -1;
                           } else {
                              var18 = 2;
                           }
                        } else {
                           var18 = 16;
                        }

                        if (var18 != -1) {
                           if (var7) {
                              return stringPrefixToNumber(var0, var2 + 2, var18);
                           }

                           return stringToNumber(var0, var2 + 2, var4, var18);
                        }
                     }
                  } else if (var7 && (var3 == '+' || var3 == '-') && var2 + 3 <= var4 && var0.charAt(var2 + 1) == '0') {
                     char var14 = var0.charAt(var2 + 2);
                     if (var14 == 'x' || var14 == 'X') {
                        double var15 = stringPrefixToNumber(var0, var2 + 3, 16);
                        if (var3 == '-') {
                           return -var15;
                        }

                        return var15;
                     }
                  }

                  if (var5 == 'y') {
                     if (var3 == '+' || var3 == '-') {
                        ++var2;
                     }

                     if (var2 + 7 == var4 && var0.regionMatches(var2, "Infinity", 0, 8)) {
                        if (var3 == '-') {
                           return Double.NEGATIVE_INFINITY;
                        }

                        return Double.POSITIVE_INFINITY;
                     }

                     return NaN;
                  }

                  String var8 = var0.substring(var2, var4 + 1);

                  for(int var9 = var8.length() - 1; var9 >= 0; --var9) {
                     char var13 = var8.charAt(var9);
                     if (('0' > var13 || var13 > '9') && var13 != '.' && var13 != 'e' && var13 != 'E' && var13 != '+' && var13 != '-') {
                        return NaN;
                     }
                  }

                  try {
                     double var11 = Double.parseDouble(var8);
                     return var11;
                  } catch (NumberFormatException var19) {
                     return NaN;
                  }
               }

               --var4;
            }
         }
      }

      return 0.0D;
   }

   public static double toNumber(Object[] var0, int var1) {
      return var1 < var0.length ? toNumber(var0[var1]) : NaN;
   }

   public static Scriptable toObject(Context var0, Scriptable var1, Object var2) {
      if (var2 != null) {
         if (!Undefined.isUndefined(var2)) {
            if (isSymbol(var2)) {
               NativeSymbol var3 = new NativeSymbol((NativeSymbol)var2);
               setBuiltinProtoAndParent(var3, var1, TopLevel.Builtins.Symbol);
               return var3;
            } else if (var2 instanceof Scriptable) {
               return (Scriptable)var2;
            } else if (var2 instanceof CharSequence) {
               NativeString var4 = new NativeString((CharSequence)var2);
               setBuiltinProtoAndParent(var4, var1, TopLevel.Builtins.String);
               return var4;
            } else if (var2 instanceof Number) {
               NativeNumber var5 = new NativeNumber(((Number)var2).doubleValue());
               setBuiltinProtoAndParent(var5, var1, TopLevel.Builtins.Number);
               return var5;
            } else if (var2 instanceof Boolean) {
               NativeBoolean var6 = new NativeBoolean((Boolean)var2);
               setBuiltinProtoAndParent(var6, var1, TopLevel.Builtins.Boolean);
               return var6;
            } else {
               Object var7 = var0.getWrapFactory().wrap(var0, var1, var2, (Class)null);
               if (var7 instanceof Scriptable) {
                  return (Scriptable)var7;
               } else {
                  throw errorWithClassName("msg.invalid.type", var2);
               }
            }
         } else {
            throw typeError0("msg.undef.to.object");
         }
      } else {
         throw typeError0("msg.null.to.object");
      }
   }

   @Deprecated
   public static Scriptable toObject(Context var0, Scriptable var1, Object var2, Class var3) {
      return toObject(var0, var1, var2);
   }

   public static Scriptable toObject(Scriptable var0, Object var1) {
      return var1 instanceof Scriptable ? (Scriptable)var1 : toObject(Context.getContext(), var0, var1);
   }

   @Deprecated
   public static Scriptable toObject(Scriptable var0, Object var1, Class var2) {
      return var1 instanceof Scriptable ? (Scriptable)var1 : toObject(Context.getContext(), var0, var1);
   }

   @Deprecated
   public static Scriptable toObjectOrNull(Context var0, Object var1) {
      if (var1 instanceof Scriptable) {
         return (Scriptable)var1;
      } else {
         return var1 != null && var1 != Undefined.instance ? toObject(var0, getTopCallScope(var0), var1) : null;
      }
   }

   public static Scriptable toObjectOrNull(Context var0, Object var1, Scriptable var2) {
      if (var1 instanceof Scriptable) {
         return (Scriptable)var1;
      } else {
         return var1 != null && var1 != Undefined.instance ? toObject(var0, var2, var1) : null;
      }
   }

   public static Object toPrimitive(Object var0) {
      return toPrimitive(var0, (Class)null);
   }

   public static Object toPrimitive(Object var0, Class var1) {
      if (!(var0 instanceof Scriptable)) {
         return var0;
      } else {
         Object var2 = ((Scriptable)var0).getDefaultValue(var1);
         if (var2 instanceof Scriptable) {
            if (isSymbol(var2)) {
               return var2;
            } else {
               throw typeError0("msg.bad.default.value");
            }
         } else {
            return var2;
         }
      }
   }

   public static String toString(double var0) {
      return numberToString(var0, 10);
   }

   public static String toString(Object var0) {
      while(var0 != null) {
         if (var0 != Undefined.instance && var0 != Undefined.SCRIPTABLE_UNDEFINED) {
            if (var0 instanceof String) {
               return (String)var0;
            }

            if (var0 instanceof CharSequence) {
               return var0.toString();
            }

            if (var0 instanceof Number) {
               return numberToString(((Number)var0).doubleValue(), 10);
            }

            if (!(var0 instanceof Symbol)) {
               if (var0 instanceof Scriptable) {
                  var0 = ((Scriptable)var0).getDefaultValue(StringClass);
                  if (!(var0 instanceof Scriptable) || isSymbol(var0)) {
                     continue;
                  }

                  throw errorWithClassName("msg.primitive.expected", var0);
               }

               return var0.toString();
            }

            throw typeError0("msg.not.a.string");
         }

         return "undefined";
      }

      return "null";
   }

   public static String toString(Object[] var0, int var1) {
      return var1 < var0.length ? toString(var0[var1]) : "undefined";
   }

   static String toStringIdOrIndex(Context var0, Object var1) {
      if (var1 instanceof Number) {
         double var5 = ((Number)var1).doubleValue();
         int var7 = (int)var5;
         if ((double)var7 == var5) {
            storeIndexResult(var0, var7);
            return null;
         } else {
            return toString(var1);
         }
      } else {
         String var2;
         if (var1 instanceof String) {
            var2 = (String)var1;
         } else {
            var2 = toString(var1);
         }

         long var3 = indexFromString(var2);
         if (var3 >= 0L) {
            storeIndexResult(var0, (int)var3);
            return null;
         } else {
            return var2;
         }
      }
   }

   public static char toUint16(Object var0) {
      return (char)DoubleConversion.doubleToInt32(toNumber(var0));
   }

   public static long toUint32(double var0) {
      return 4294967295L & (long)DoubleConversion.doubleToInt32(var0);
   }

   public static long toUint32(Object var0) {
      return toUint32(toNumber(var0));
   }

   private static Object topScopeName(Context var0, Scriptable var1, String var2) {
      if (var0.useDynamicScope) {
         var1 = checkDynamicScope(var0.topCallScope, var1);
      }

      return ScriptableObject.getProperty(var1, var2);
   }

   public static EcmaError typeError(String var0) {
      return constructError("TypeError", var0);
   }

   public static EcmaError typeError0(String var0) {
      return typeError(getMessage0(var0));
   }

   public static EcmaError typeError1(String var0, Object var1) {
      return typeError(getMessage1(var0, var1));
   }

   public static EcmaError typeError2(String var0, Object var1, Object var2) {
      return typeError(getMessage2(var0, var1, var2));
   }

   public static EcmaError typeError3(String var0, String var1, String var2, String var3) {
      return typeError(getMessage3(var0, var1, var2, var3));
   }

   @Deprecated
   public static BaseFunction typeErrorThrower() {
      return typeErrorThrower(Context.getCurrentContext());
   }

   public static BaseFunction typeErrorThrower(Context var0) {
      if (var0.typeErrorThrower == null) {
         ScriptRuntime$1 var1 = new ScriptRuntime$1();
         setFunctionProtoAndParent(var1, var0.topCallScope);
         var1.preventExtensions();
         var0.typeErrorThrower = var1;
      }

      return var0.typeErrorThrower;
   }

   public static String typeof(Object var0) {
      String var1 = "object";
      if (var0 == null) {
         return var1;
      } else if (var0 == Undefined.instance) {
         return "undefined";
      } else if (var0 instanceof ScriptableObject) {
         return ((ScriptableObject)var0).getTypeOf();
      } else if (var0 instanceof Scriptable) {
         if (var0 instanceof Callable) {
            var1 = "function";
         }

         return var1;
      } else if (var0 instanceof CharSequence) {
         return "string";
      } else if (var0 instanceof Number) {
         return "number";
      } else if (var0 instanceof Boolean) {
         return "boolean";
      } else {
         throw errorWithClassName("msg.invalid.type", var0);
      }
   }

   public static String typeofName(Scriptable var0, String var1) {
      Context var2 = Context.getContext();
      Scriptable var3 = bind(var2, var0, var1);
      return var3 == null ? "undefined" : typeof(getObjectProp(var3, var1, var2));
   }

   public static RuntimeException undefCallError(Object var0, Object var1) {
      return typeError2("msg.undef.method.call", toString(var0), toString(var1));
   }

   private static RuntimeException undefDeleteError(Object var0, Object var1) {
      throw typeError2("msg.undef.prop.delete", toString(var0), toString(var1));
   }

   public static RuntimeException undefReadError(Object var0, Object var1) {
      return typeError2("msg.undef.prop.read", toString(var0), toString(var1));
   }

   public static RuntimeException undefWriteError(Object var0, Object var1, Object var2) {
      return typeError3("msg.undef.prop.write", toString(var0), toString(var1), toString(var2));
   }

   static String uneval(Context var0, Scriptable var1, Object var2) {
      if (var2 == null) {
         return "null";
      } else if (var2 == Undefined.instance) {
         return "undefined";
      } else if (var2 instanceof CharSequence) {
         String var7 = escapeString(var2.toString());
         StringBuilder var8 = new StringBuilder(2 + var7.length());
         var8.append('"');
         var8.append(var7);
         var8.append('"');
         return var8.toString();
      } else if (var2 instanceof Number) {
         double var5 = ((Number)var2).doubleValue();
         return var5 == 0.0D && 1.0D / var5 < 0.0D ? "-0" : toString(var5);
      } else if (var2 instanceof Boolean) {
         return toString(var2);
      } else if (var2 instanceof Scriptable) {
         Scriptable var3 = (Scriptable)var2;
         if (ScriptableObject.hasProperty(var3, "toSource")) {
            Object var4 = ScriptableObject.getProperty(var3, "toSource");
            if (var4 instanceof Function) {
               return toString(((Function)var4).call(var0, var1, var3, emptyArgs));
            }
         }

         return toString(var2);
      } else {
         warnAboutNonJSObject(var2);
         return var2.toString();
      }
   }

   public static Object updateDotQuery(boolean var0, Scriptable var1) {
      return ((NativeWith)var1).updateDotQuery(var0);
   }

   private static void warnAboutNonJSObject(Object var0) {
      if (!"true".equals(getMessage0("params.omit.non.js.object.warning"))) {
         String var1 = getMessage2("msg.non.js.object.warning", var0, var0.getClass().getName());
         Context.reportWarning(var1);
         System.err.println(var1);
      }

   }

   public static Boolean wrapBoolean(boolean var0) {
      return var0 ? Boolean.TRUE : Boolean.FALSE;
   }

   public static Scriptable wrapException(Throwable var0, Scriptable var1, Context var2) {
      Throwable var3 = null;
      Object var4;
      String var5;
      String var6;
      if (var0 instanceof EcmaError) {
         EcmaError var18 = (EcmaError)var0;
         var4 = var18;
         var5 = var18.getName();
         var6 = var18.getErrorMessage();
         var3 = null;
      } else if (var0 instanceof WrappedException) {
         WrappedException var13 = (WrappedException)var0;
         var4 = var13;
         var3 = var13.getWrappedException();
         var5 = "JavaException";
         StringBuilder var14 = new StringBuilder();
         var14.append(var3.getClass().getName());
         var14.append(": ");
         var14.append(var3.getMessage());
         var6 = var14.toString();
      } else if (var0 instanceof EvaluatorException) {
         EvaluatorException var12 = (EvaluatorException)var0;
         var4 = var12;
         var5 = "InternalError";
         var6 = var12.getMessage();
         var3 = null;
      } else {
         if (!var2.hasFeature(13)) {
            throw Kit.codeBug();
         }

         var4 = new WrappedException(var0);
         var5 = "JavaException";
         var6 = var0.toString();
      }

      String var7 = ((RhinoException)var4).sourceName();
      if (var7 == null) {
         var7 = "";
      }

      int var8 = ((RhinoException)var4).lineNumber();
      Object[] var9;
      if (var8 > 0) {
         Object[] var11 = new Object[]{var6, var7, var8};
         var9 = var11;
      } else {
         var9 = new Object[]{var6, var7};
      }

      Scriptable var10 = var2.newObject(var1, var5, var9);
      ScriptableObject.putProperty(var10, (String)"name", var5);
      if (var10 instanceof NativeError) {
         ((NativeError)var10).setStackProvider((RhinoException)var4);
      }

      if (var3 != null && isVisible(var2, var3)) {
         ScriptableObject.defineProperty(var10, "javaException", var2.getWrapFactory().wrap(var2, var1, var3, (Class)null), 7);
      }

      if (isVisible(var2, var4)) {
         ScriptableObject.defineProperty(var10, "rhinoException", var2.getWrapFactory().wrap(var2, var1, var4, (Class)null), 7);
      }

      return var10;
   }

   public static Integer wrapInt(int var0) {
      return var0;
   }

   public static Number wrapNumber(double var0) {
      return var0 != var0 ? NaNobj : new Double(var0);
   }

   public static Scriptable wrapRegExp(Context var0, Scriptable var1, Object var2) {
      return var0.getRegExpProxy().wrapRegExp(var0, var1, var2);
   }

   private static class DefaultMessageProvider implements ScriptRuntime.MessageProvider {
      private DefaultMessageProvider() {
      }

      // $FF: synthetic method
      DefaultMessageProvider(ScriptRuntime$1 var1) {
         this();
      }

      public String getMessage(String var1, Object[] var2) {
         Context var3 = Context.getCurrentContext();
         Locale var4;
         if (var3 != null) {
            var4 = var3.getLocale();
         } else {
            var4 = Locale.getDefault();
         }

         ResourceBundle var5 = ResourceBundle.getBundle("org.mozilla.javascript.resources.Messages", var4);

         String var10;
         try {
            var10 = var5.getString(var1);
         } catch (MissingResourceException var11) {
            StringBuilder var7 = new StringBuilder();
            var7.append("no message resource found for message property ");
            var7.append(var1);
            throw new RuntimeException(var7.toString());
         }

         return (new MessageFormat(var10)).format(var2);
      }
   }

   private static class IdEnumeration implements Serializable {
      private static final long serialVersionUID = 1L;
      Object currentId;
      boolean enumNumbers;
      int enumType;
      Object[] ids;
      int index;
      Scriptable iterator;
      Scriptable obj;
      ObjToIntMap used;

      private IdEnumeration() {
      }

      // $FF: synthetic method
      IdEnumeration(ScriptRuntime$1 var1) {
         this();
      }
   }

   public interface MessageProvider {
      String getMessage(String var1, Object[] var2);
   }

   static class NoSuchMethodShim implements Callable {
      String methodName;
      Callable noSuchMethodMethod;

      NoSuchMethodShim(Callable var1, String var2) {
         this.noSuchMethodMethod = var1;
         this.methodName = var2;
      }

      public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
         Object[] var5 = new Object[]{this.methodName, ScriptRuntime.newArrayLiteral(var4, (int[])null, var1, var2)};
         return this.noSuchMethodMethod.call(var1, var2, var3, var5);
      }
   }
}
