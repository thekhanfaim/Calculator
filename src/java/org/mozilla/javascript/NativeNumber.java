package org.mozilla.javascript;

final class NativeNumber extends IdScriptableObject {
   private static final int ConstructorId_isFinite = -1;
   private static final int ConstructorId_isInteger = -3;
   private static final int ConstructorId_isNaN = -2;
   private static final int ConstructorId_isSafeInteger = -4;
   private static final int ConstructorId_parseFloat = -5;
   private static final int ConstructorId_parseInt = -6;
   private static final int Id_constructor = 1;
   private static final int Id_toExponential = 7;
   private static final int Id_toFixed = 6;
   private static final int Id_toLocaleString = 3;
   private static final int Id_toPrecision = 8;
   private static final int Id_toSource = 4;
   private static final int Id_toString = 2;
   private static final int Id_valueOf = 5;
   private static final int MAX_PRECISION = 100;
   private static final int MAX_PROTOTYPE_ID = 8;
   public static final double MAX_SAFE_INTEGER = 9.007199254740991E15D;
   private static final double MIN_SAFE_INTEGER = -9.007199254740991E15D;
   private static final Object NUMBER_TAG = "Number";
   private static final long serialVersionUID = 3504516769741512101L;
   private double doubleValue;

   NativeNumber(double var1) {
      this.doubleValue = var1;
   }

   private Double doubleVal(Number var1) {
      return var1 instanceof Double ? (Double)var1 : var1.doubleValue();
   }

   private Object execConstructorCall(int var1, Object[] var2) {
      Boolean var3 = false;
      switch(var1) {
      case -6:
         return NativeGlobal.js_parseInt(var2);
      case -5:
         return NativeGlobal.js_parseFloat(var2);
      case -4:
         if (var2.length != 0) {
            if (Undefined.instance == var2[0]) {
               return var3;
            }

            if (var2[0] instanceof Number) {
               return this.isSafeInteger((Number)var2[0]);
            }

            return var3;
         }

         return var3;
      case -3:
         if (var2.length != 0) {
            if (Undefined.instance == var2[0]) {
               return var3;
            }

            if (var2[0] instanceof Number) {
               return this.isInteger((Number)var2[0]);
            }

            return var3;
         }

         return var3;
      case -2:
         if (var2.length != 0) {
            if (Undefined.instance == var2[0]) {
               return var3;
            }

            if (var2[0] instanceof Number) {
               return this.isNaN((Number)var2[0]);
            }

            return var3;
         }

         return var3;
      case -1:
         if (var2.length != 0) {
            if (Undefined.instance == var2[0]) {
               return var3;
            }

            if (var2[0] instanceof Number) {
               return isFinite(var2[0]);
            }

            return var3;
         }

         return var3;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }
   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeNumber(0.0D)).exportAsJSClass(8, var0, var1);
   }

   private boolean isDoubleInteger(Double var1) {
      return !var1.isInfinite() && !var1.isNaN() && Math.floor(var1) == var1;
   }

   private boolean isDoubleNan(Double var1) {
      return var1.isNaN();
   }

   private boolean isDoubleSafeInteger(Double var1) {
      return this.isDoubleInteger(var1) && var1 <= 9.007199254740991E15D && var1 >= -9.007199254740991E15D;
   }

   static Object isFinite(Object var0) {
      Double var1 = ScriptRuntime.toNumber(var0);
      boolean var2;
      if (!var1.isInfinite() && !var1.isNaN()) {
         var2 = true;
      } else {
         var2 = false;
      }

      return ScriptRuntime.wrapBoolean(var2);
   }

   private boolean isInteger(Number var1) {
      return ScriptRuntime.toBoolean(this.isDoubleInteger(this.doubleVal(var1)));
   }

   private Object isNaN(Number var1) {
      return ScriptRuntime.toBoolean(this.isDoubleNan(this.doubleVal(var1)));
   }

   private boolean isSafeInteger(Number var1) {
      return ScriptRuntime.toBoolean(this.isDoubleSafeInteger(this.doubleVal(var1)));
   }

   private static String num_to(double var0, Object[] var2, int var3, int var4, int var5, int var6) {
      int var9;
      if (var2.length == 0) {
         var4 = var3;
         var9 = 0;
      } else {
         double var7 = ScriptRuntime.toInteger(var2[0]);
         if (var7 < (double)var5 || var7 > 100.0D) {
            throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage1("msg.bad.precision", ScriptRuntime.toString(var2[0])));
         }

         var9 = ScriptRuntime.toInt32(var7);
      }

      StringBuilder var10 = new StringBuilder();
      DToA.JS_dtostr(var10, var4, var9 + var6, var0);
      return var10.toString();
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(NUMBER_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         double var7 = 0.0D;
         if (var6 == 1) {
            if (var5.length >= 1) {
               var7 = ScriptRuntime.toNumber(var5[0]);
            }

            return var4 == null ? new NativeNumber(var7) : ScriptRuntime.wrapNumber(var7);
         } else if (var6 < 1) {
            return this.execConstructorCall(var6, var5);
         } else if (var4 instanceof NativeNumber) {
            double var9 = ((NativeNumber)var4).doubleValue;
            int var11 = 10;
            switch(var6) {
            case 2:
            case 3:
               if (var5.length != 0 && var5[0] != Undefined.instance) {
                  var11 = ScriptRuntime.toInt32(var5[0]);
               }

               return ScriptRuntime.numberToString(var9, var11);
            case 4:
               StringBuilder var12 = new StringBuilder();
               var12.append("(new Number(");
               var12.append(ScriptRuntime.toString(var9));
               var12.append("))");
               return var12.toString();
            case 5:
               return ScriptRuntime.wrapNumber(var9);
            case 6:
               return num_to(var9, var5, 2, 2, -20, 0);
            case 7:
               if (Double.isNaN(var9)) {
                  return "NaN";
               } else {
                  if (Double.isInfinite(var9)) {
                     if (var9 >= var7) {
                        return "Infinity";
                     }

                     return "-Infinity";
                  }

                  return num_to(var9, var5, 1, 3, 0, 1);
               }
            case 8:
               if (var5.length != 0 && var5[0] != Undefined.instance) {
                  if (Double.isNaN(var9)) {
                     return "NaN";
                  }

                  if (Double.isInfinite(var9)) {
                     if (var9 >= var7) {
                        return "Infinity";
                     }

                     return "-Infinity";
                  }

                  return num_to(var9, var5, 0, 4, 1, 0);
               }

               return ScriptRuntime.numberToString(var9, var11);
            default:
               throw new IllegalArgumentException(String.valueOf(var6));
            }
         } else {
            throw incompatibleCallError(var1);
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      var1.defineProperty("NaN", ScriptRuntime.NaNobj, 7);
      var1.defineProperty("POSITIVE_INFINITY", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);
      var1.defineProperty("NEGATIVE_INFINITY", ScriptRuntime.wrapNumber(Double.NEGATIVE_INFINITY), 7);
      var1.defineProperty("MAX_VALUE", ScriptRuntime.wrapNumber(Double.MAX_VALUE), 7);
      var1.defineProperty("MIN_VALUE", ScriptRuntime.wrapNumber(Double.MIN_VALUE), 7);
      var1.defineProperty("MAX_SAFE_INTEGER", ScriptRuntime.wrapNumber(9.007199254740991E15D), 7);
      var1.defineProperty("MIN_SAFE_INTEGER", ScriptRuntime.wrapNumber(-9.007199254740991E15D), 7);
      Object var2 = NUMBER_TAG;
      this.addIdFunctionProperty(var1, var2, -1, "isFinite", 1);
      this.addIdFunctionProperty(var1, var2, -2, "isNaN", 1);
      this.addIdFunctionProperty(var1, var2, -3, "isInteger", 1);
      this.addIdFunctionProperty(var1, var2, -4, "isSafeInteger", 1);
      this.addIdFunctionProperty(var1, var2, -5, "parseFloat", 1);
      this.addIdFunctionProperty(var1, var2, -6, "parseInt", 1);
      super.fillConstructorProperties(var1);
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var4;
      String var5;
      if (var2 != 7) {
         if (var2 != 8) {
            if (var2 != 11) {
               if (var2 != 13) {
                  if (var2 != 14) {
                     var4 = 0;
                     var5 = null;
                  } else {
                     var5 = "toLocaleString";
                     var4 = 3;
                  }
               } else {
                  var5 = "toExponential";
                  var4 = 7;
               }
            } else {
               char var7 = var1.charAt(0);
               if (var7 == 'c') {
                  var5 = "constructor";
                  var4 = 1;
               } else {
                  var4 = 0;
                  var5 = null;
                  if (var7 == 't') {
                     var5 = "toPrecision";
                     var4 = 8;
                  }
               }
            }
         } else {
            char var6 = var1.charAt(3);
            if (var6 == 'o') {
               var5 = "toSource";
               var4 = 4;
            } else {
               var4 = 0;
               var5 = null;
               if (var6 == 't') {
                  var5 = "toString";
                  var4 = 2;
               }
            }
         }
      } else {
         char var3 = var1.charAt(0);
         if (var3 == 't') {
            var5 = "toFixed";
            var4 = 6;
         } else {
            var4 = 0;
            var5 = null;
            if (var3 == 'v') {
               var5 = "valueOf";
               var4 = 5;
            }
         }
      }

      if (var5 != null && var5 != var1 && !var5.equals(var1)) {
         var4 = 0;
      }

      return var4;
   }

   public String getClassName() {
      return "Number";
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
         var2 = 1;
         var3 = "toString";
         break;
      case 3:
         var2 = 1;
         var3 = "toLocaleString";
         break;
      case 4:
         var3 = "toSource";
         var2 = 0;
         break;
      case 5:
         var3 = "valueOf";
         var2 = 0;
         break;
      case 6:
         var2 = 1;
         var3 = "toFixed";
         break;
      case 7:
         var2 = 1;
         var3 = "toExponential";
         break;
      case 8:
         var2 = 1;
         var3 = "toPrecision";
         break;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }

      this.initPrototypeMethod(NUMBER_TAG, var1, var3, var2);
   }

   public String toString() {
      return ScriptRuntime.numberToString(this.doubleValue, 10);
   }
}
