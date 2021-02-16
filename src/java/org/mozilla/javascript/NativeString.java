package org.mozilla.javascript;

import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import org.mozilla.javascript.regexp.NativeRegExp;

final class NativeString extends IdScriptableObject {
   private static final int ConstructorId_charAt = -5;
   private static final int ConstructorId_charCodeAt = -6;
   private static final int ConstructorId_concat = -14;
   private static final int ConstructorId_equalsIgnoreCase = -30;
   private static final int ConstructorId_fromCharCode = -1;
   private static final int ConstructorId_indexOf = -7;
   private static final int ConstructorId_lastIndexOf = -8;
   private static final int ConstructorId_localeCompare = -34;
   private static final int ConstructorId_match = -31;
   private static final int ConstructorId_replace = -33;
   private static final int ConstructorId_search = -32;
   private static final int ConstructorId_slice = -15;
   private static final int ConstructorId_split = -9;
   private static final int ConstructorId_substr = -13;
   private static final int ConstructorId_substring = -10;
   private static final int ConstructorId_toLocaleLowerCase = -35;
   private static final int ConstructorId_toLowerCase = -11;
   private static final int ConstructorId_toUpperCase = -12;
   private static final int Id_anchor = 28;
   private static final int Id_big = 21;
   private static final int Id_blink = 22;
   private static final int Id_bold = 16;
   private static final int Id_charAt = 5;
   private static final int Id_charCodeAt = 6;
   private static final int Id_codePointAt = 45;
   private static final int Id_concat = 14;
   private static final int Id_constructor = 1;
   private static final int Id_endsWith = 42;
   private static final int Id_equals = 29;
   private static final int Id_equalsIgnoreCase = 30;
   private static final int Id_fixed = 18;
   private static final int Id_fontcolor = 26;
   private static final int Id_fontsize = 25;
   private static final int Id_includes = 40;
   private static final int Id_indexOf = 7;
   private static final int Id_italics = 17;
   private static final int Id_lastIndexOf = 8;
   private static final int Id_length = 1;
   private static final int Id_link = 27;
   private static final int Id_localeCompare = 34;
   private static final int Id_match = 31;
   private static final int Id_normalize = 43;
   private static final int Id_padEnd = 47;
   private static final int Id_padStart = 46;
   private static final int Id_repeat = 44;
   private static final int Id_replace = 33;
   private static final int Id_search = 32;
   private static final int Id_slice = 15;
   private static final int Id_small = 20;
   private static final int Id_split = 9;
   private static final int Id_startsWith = 41;
   private static final int Id_strike = 19;
   private static final int Id_sub = 24;
   private static final int Id_substr = 13;
   private static final int Id_substring = 10;
   private static final int Id_sup = 23;
   private static final int Id_toLocaleLowerCase = 35;
   private static final int Id_toLocaleUpperCase = 36;
   private static final int Id_toLowerCase = 11;
   private static final int Id_toSource = 3;
   private static final int Id_toString = 2;
   private static final int Id_toUpperCase = 12;
   private static final int Id_trim = 37;
   private static final int Id_trimLeft = 38;
   private static final int Id_trimRight = 39;
   private static final int Id_valueOf = 4;
   private static final int MAX_INSTANCE_ID = 1;
   private static final int MAX_PROTOTYPE_ID = 48;
   private static final Object STRING_TAG = "String";
   private static final int SymbolId_iterator = 48;
   private static final long serialVersionUID = 920268368584188687L;
   private CharSequence string;

   NativeString(CharSequence var1) {
      this.string = var1;
   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeString("")).exportAsJSClass(48, var0, var1);
   }

   private static String js_concat(String var0, Object[] var1) {
      int var2 = var1.length;
      if (var2 == 0) {
         return var0;
      } else if (var2 == 1) {
         return var0.concat(ScriptRuntime.toString(var1[0]));
      } else {
         int var3 = var0.length();
         String[] var4 = new String[var2];

         for(int var5 = 0; var5 != var2; ++var5) {
            String var10 = ScriptRuntime.toString(var1[var5]);
            var4[var5] = var10;
            var3 += var10.length();
         }

         StringBuilder var6 = new StringBuilder(var3);
         var6.append(var0);

         for(int var8 = 0; var8 != var2; ++var8) {
            var6.append(var4[var8]);
         }

         return var6.toString();
      }
   }

   private static int js_indexOf(int var0, String var1, Object[] var2) {
      String var3 = ScriptRuntime.toString(var2, 0);
      double var4 = ScriptRuntime.toInteger(var2, 1);
      if (var4 > (double)var1.length() && var0 != 41 && var0 != 42) {
         return -1;
      } else {
         if (var4 < 0.0D) {
            var4 = 0.0D;
         } else if (var4 > (double)var1.length()) {
            var4 = (double)var1.length();
         } else if (var0 == 42 && (var4 != var4 || var4 > (double)var1.length())) {
            var4 = (double)var1.length();
         }

         if (42 != var0) {
            if (var0 == 41) {
               return var1.startsWith(var3, (int)var4) ? 0 : -1;
            } else {
               return var1.indexOf(var3, (int)var4);
            }
         } else {
            if (var2.length == 0 || var2.length == 1 || var2.length == 2 && var2[1] == Undefined.instance) {
               var4 = (double)var1.length();
            }

            return var1.substring(0, (int)var4).endsWith(var3) ? 0 : -1;
         }
      }
   }

   private static int js_lastIndexOf(String var0, Object[] var1) {
      String var2 = ScriptRuntime.toString(var1, 0);
      double var3 = ScriptRuntime.toNumber(var1, 1);
      if (var3 == var3 && var3 <= (double)var0.length()) {
         if (var3 < 0.0D) {
            var3 = 0.0D;
         }
      } else {
         var3 = (double)var0.length();
      }

      return var0.lastIndexOf(var2, (int)var3);
   }

   private static String js_pad(Context var0, Scriptable var1, IdFunctionObject var2, Object[] var3, Boolean var4) {
      String var5 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var0, var1, var2));
      long var6 = ScriptRuntime.toLength(var3, 0);
      if (var6 <= (long)var5.length()) {
         return var5;
      } else {
         String var8 = " ";
         if (var3.length >= 2 && !Undefined.isUndefined(var3[1])) {
            var8 = ScriptRuntime.toString(var3[1]);
            if (var8.length() < 1) {
               return var5;
            }
         }

         int var9 = (int)(var6 - (long)var5.length());
         StringBuilder var10 = new StringBuilder();

         do {
            var10.append(var8);
         } while(var10.length() < var9);

         var10.setLength(var9);
         if (var4) {
            var10.append(var5);
            return var10.toString();
         } else {
            return var10.insert(0, var5).toString();
         }
      }
   }

   private static String js_repeat(Context var0, Scriptable var1, IdFunctionObject var2, Object[] var3) {
      String var4 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var0, var1, var2));
      double var5 = ScriptRuntime.toInteger(var3, 0);
      if (var5 >= 0.0D && var5 != Double.POSITIVE_INFINITY) {
         if (var5 != 0.0D && var4.length() != 0) {
            long var8 = (long)var4.length() * (long)var5;
            if (var5 <= 2.147483647E9D && var8 <= 2147483647L) {
               StringBuilder var10 = new StringBuilder((int)var8);
               var10.append(var4);
               int var12 = 1;

               int var13;
               for(var13 = (int)var5; var12 <= var13 / 2; var12 *= 2) {
                  var10.append(var10);
               }

               if (var12 < var13) {
                  var10.append(var10.substring(0, var4.length() * (var13 - var12)));
               }

               return var10.toString();
            } else {
               throw ScriptRuntime.rangeError("Invalid size or count value");
            }
         } else {
            return "";
         }
      } else {
         EcmaError var7 = ScriptRuntime.rangeError("Invalid count value");
         throw var7;
      }
   }

   private static CharSequence js_slice(CharSequence var0, Object[] var1) {
      double var2;
      if (var1.length < 1) {
         var2 = 0.0D;
      } else {
         var2 = ScriptRuntime.toInteger(var1[0]);
      }

      int var4 = var0.length();
      if (var2 < 0.0D) {
         double var10 = (double)var4;
         Double.isNaN(var10);
         var2 += var10;
         if (var2 < 0.0D) {
            var2 = 0.0D;
         }
      } else if (var2 > (double)var4) {
         var2 = (double)var4;
      }

      double var5;
      if (var1.length >= 2 && var1[1] != Undefined.instance) {
         var5 = ScriptRuntime.toInteger(var1[1]);
         if (var5 < 0.0D) {
            double var7 = (double)var4;
            Double.isNaN(var7);
            var5 += var7;
            if (var5 < 0.0D) {
               var5 = 0.0D;
            }
         } else if (var5 > (double)var4) {
            var5 = (double)var4;
         }

         if (var5 < var2) {
            var5 = var2;
         }
      } else {
         var5 = (double)var4;
      }

      return var0.subSequence((int)var2, (int)var5);
   }

   private static CharSequence js_substr(CharSequence var0, Object[] var1) {
      if (var1.length < 1) {
         return var0;
      } else {
         double var2 = ScriptRuntime.toInteger(var1[0]);
         int var4 = var0.length();
         if (var2 < 0.0D) {
            double var9 = (double)var4;
            Double.isNaN(var9);
            var2 += var9;
            if (var2 < 0.0D) {
               var2 = 0.0D;
            }
         } else if (var2 > (double)var4) {
            var2 = (double)var4;
         }

         double var7;
         if (var1.length == 1) {
            var7 = (double)var4;
         } else {
            double var5 = ScriptRuntime.toInteger(var1[1]);
            if (var5 < 0.0D) {
               var5 = 0.0D;
            }

            var7 = var5 + var2;
            if (var7 > (double)var4) {
               var7 = (double)var4;
            }
         }

         return var0.subSequence((int)var2, (int)var7);
      }
   }

   private static CharSequence js_substring(Context var0, CharSequence var1, Object[] var2) {
      int var3 = var1.length();
      double var4 = ScriptRuntime.toInteger(var2, 0);
      if (var4 < 0.0D) {
         var4 = 0.0D;
      } else if (var4 > (double)var3) {
         var4 = (double)var3;
      }

      double var6;
      if (var2.length > 1 && var2[1] != Undefined.instance) {
         var6 = ScriptRuntime.toInteger(var2[1]);
         if (var6 < 0.0D) {
            var6 = 0.0D;
         } else if (var6 > (double)var3) {
            var6 = (double)var3;
         }

         if (var6 < var4) {
            if (var0.getLanguageVersion() != 120) {
               double var8 = var4;
               var4 = var6;
               var6 = var8;
            } else {
               var6 = var4;
            }
         }
      } else {
         var6 = (double)var3;
      }

      return var1.subSequence((int)var4, (int)var6);
   }

   private static NativeString realThis(Scriptable var0, IdFunctionObject var1) {
      if (var0 instanceof NativeString) {
         return (NativeString)var0;
      } else {
         throw incompatibleCallError(var1);
      }
   }

   private static String tagify(Object var0, String var1, String var2, Object[] var3) {
      String var4 = ScriptRuntime.toString(var0);
      StringBuilder var5 = new StringBuilder();
      var5.append('<');
      var5.append(var1);
      if (var2 != null) {
         var5.append(' ');
         var5.append(var2);
         var5.append("=\"");
         var5.append(ScriptRuntime.toString(var3, 0));
         var5.append('"');
      }

      var5.append('>');
      var5.append(var4);
      var5.append("</");
      var5.append(var1);
      var5.append('>');
      return var5.toString();
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(STRING_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();

         while(true) {
            byte var7 = 1;
            if (var6 == -1) {
               int var8 = var5.length;
               if (var8 < var7) {
                  return "";
               } else {
                  StringBuilder var9 = new StringBuilder(var8);

                  for(int var10 = 0; var10 != var8; ++var10) {
                     var9.append(ScriptRuntime.toUint16(var5[var10]));
                  }

                  return var9.toString();
               }
            }

            switch(var6) {
            case -35:
            case -34:
            case -33:
            case -32:
            case -31:
            case -30:
               break;
            default:
               switch(var6) {
               case -15:
               case -14:
               case -13:
               case -12:
               case -11:
               case -10:
               case -9:
               case -8:
               case -7:
               case -6:
               case -5:
                  break;
               default:
                  switch(var6) {
                  case 1:
                     Object var14;
                     if (var5.length == 0) {
                        var14 = "";
                     } else if (ScriptRuntime.isSymbol(var5[0]) && var4 != null) {
                        var14 = var5[0].toString();
                     } else {
                        var14 = ScriptRuntime.toCharSequence(var5[0]);
                     }

                     if (var4 == null) {
                        return new NativeString((CharSequence)var14);
                     }

                     if (var14 instanceof String) {
                        return var14;
                     }

                     return ((CharSequence)var14).toString();
                  case 2:
                  case 4:
                     CharSequence var15 = realThis(var4, var1).string;
                     if (var15 instanceof String) {
                        return var15;
                     }

                     return var15.toString();
                  case 3:
                     CharSequence var16 = realThis(var4, var1).string;
                     StringBuilder var17 = new StringBuilder();
                     var17.append("(new String(\"");
                     var17.append(ScriptRuntime.escapeString(var16.toString()));
                     var17.append("\"))");
                     return var17.toString();
                  case 5:
                  case 6:
                     CharSequence var21 = ScriptRuntime.toCharSequence(var4);
                     double var22 = ScriptRuntime.toInteger(var5, 0);
                     if (var22 >= 0.0D && var22 < (double)var21.length()) {
                        char var24 = var21.charAt((int)var22);
                        if (var6 == 5) {
                           return String.valueOf(var24);
                        }

                        return ScriptRuntime.wrapInt(var24);
                     }

                     if (var6 == 5) {
                        return "";
                     }

                     return ScriptRuntime.NaNobj;
                  case 7:
                     return ScriptRuntime.wrapInt(js_indexOf(7, ScriptRuntime.toString(var4), var5));
                  case 8:
                     return ScriptRuntime.wrapInt(js_lastIndexOf(ScriptRuntime.toString(var4), var5));
                  case 9:
                     return ScriptRuntime.checkRegExpProxy(var2).js_split(var2, var3, ScriptRuntime.toString(var4), var5);
                  case 10:
                     return js_substring(var2, ScriptRuntime.toCharSequence(var4), var5);
                  case 11:
                     return ScriptRuntime.toString(var4).toLowerCase(ScriptRuntime.ROOT_LOCALE);
                  case 12:
                     return ScriptRuntime.toString(var4).toUpperCase(ScriptRuntime.ROOT_LOCALE);
                  case 13:
                     return js_substr(ScriptRuntime.toCharSequence(var4), var5);
                  case 14:
                     return js_concat(ScriptRuntime.toString(var4), var5);
                  case 15:
                     return js_slice(ScriptRuntime.toCharSequence(var4), var5);
                  case 16:
                     return tagify(var4, "b", (String)null, (Object[])null);
                  case 17:
                     return tagify(var4, "i", (String)null, (Object[])null);
                  case 18:
                     return tagify(var4, "tt", (String)null, (Object[])null);
                  case 19:
                     return tagify(var4, "strike", (String)null, (Object[])null);
                  case 20:
                     return tagify(var4, "small", (String)null, (Object[])null);
                  case 21:
                     return tagify(var4, "big", (String)null, (Object[])null);
                  case 22:
                     return tagify(var4, "blink", (String)null, (Object[])null);
                  case 23:
                     return tagify(var4, "sup", (String)null, (Object[])null);
                  case 24:
                     return tagify(var4, "sub", (String)null, (Object[])null);
                  case 25:
                     return tagify(var4, "font", "size", var5);
                  case 26:
                     return tagify(var4, "font", "color", var5);
                  case 27:
                     return tagify(var4, "a", "href", var5);
                  case 28:
                     return tagify(var4, "a", "name", var5);
                  case 29:
                  case 30:
                     String var25 = ScriptRuntime.toString(var4);
                     String var26 = ScriptRuntime.toString(var5, 0);
                     boolean var27;
                     if (var6 == 29) {
                        var27 = var25.equals(var26);
                     } else {
                        var27 = var25.equalsIgnoreCase(var26);
                     }

                     return ScriptRuntime.wrapBoolean(var27);
                  case 31:
                  case 32:
                  case 33:
                     byte var28;
                     if (var6 == 31) {
                        var28 = 1;
                     } else if (var6 == 32) {
                        var28 = 3;
                     } else {
                        var28 = 2;
                     }

                     return ScriptRuntime.checkRegExpProxy(var2).action(var2, var3, var4, var5, var28);
                  case 34:
                     Collator var29 = Collator.getInstance(var2.getLocale());
                     var29.setStrength(3);
                     var29.setDecomposition(var7);
                     return ScriptRuntime.wrapNumber((double)var29.compare(ScriptRuntime.toString(var4), ScriptRuntime.toString(var5, 0)));
                  case 35:
                     return ScriptRuntime.toString(var4).toLowerCase(var2.getLocale());
                  case 36:
                     return ScriptRuntime.toString(var4).toUpperCase(var2.getLocale());
                  case 37:
                     String var30 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var2, var4, var1));
                     char[] var31 = var30.toCharArray();

                     int var32;
                     for(var32 = 0; var32 < var31.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(var31[var32]); ++var32) {
                     }

                     int var33;
                     for(var33 = var31.length; var33 > var32 && ScriptRuntime.isJSWhitespaceOrLineTerminator(var31[var33 - 1]); --var33) {
                     }

                     return var30.substring(var32, var33);
                  case 38:
                     String var34 = ScriptRuntime.toString(var4);
                     char[] var35 = var34.toCharArray();

                     int var36;
                     for(var36 = 0; var36 < var35.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(var35[var36]); ++var36) {
                     }

                     return var34.substring(var36, var35.length);
                  case 39:
                     String var37 = ScriptRuntime.toString(var4);
                     char[] var38 = var37.toCharArray();

                     int var39;
                     for(var39 = var38.length; var39 > 0 && ScriptRuntime.isJSWhitespaceOrLineTerminator(var38[var39 - 1]); --var39) {
                     }

                     return var37.substring(0, var39);
                  case 40:
                  case 41:
                  case 42:
                     String var40 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var2, var4, var1));
                     if (var5.length > 0 && var5[0] instanceof NativeRegExp) {
                        throw ScriptRuntime.typeError2("msg.first.arg.not.regexp", String.class.getSimpleName(), var1.getFunctionName());
                     }

                     int var41 = js_indexOf(var6, var40, var5);
                     if (var6 == 40) {
                        if (var41 == -1) {
                           var7 = 0;
                        }

                        return Boolean.valueOf((boolean)var7);
                     }

                     if (var6 == 41) {
                        if (var41 != 0) {
                           var7 = 0;
                        }

                        return Boolean.valueOf((boolean)var7);
                     }

                     if (var6 == 42) {
                        if (var41 == -1) {
                           var7 = 0;
                        }

                        return Boolean.valueOf((boolean)var7);
                     }
                  case 46:
                  case 47:
                     if (var6 != 46) {
                        var7 = 0;
                     }

                     return js_pad(var2, var4, var1, var5, Boolean.valueOf((boolean)var7));
                  case 43:
                     String var42 = ScriptRuntime.toString(var5, 0);
                     Form var43;
                     if (Form.NFD.name().equals(var42)) {
                        var43 = Form.NFD;
                     } else if (Form.NFKC.name().equals(var42)) {
                        var43 = Form.NFKC;
                     } else if (Form.NFKD.name().equals(var42)) {
                        var43 = Form.NFKD;
                     } else {
                        if (!Form.NFC.name().equals(var42) && var5.length != 0) {
                           throw ScriptRuntime.rangeError("The normalization form should be one of NFC, NFD, NFKC, NFKD");
                        }

                        var43 = Form.NFC;
                     }

                     return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var2, var4, var1)), var43);
                  case 44:
                     return js_repeat(var2, var4, var1, var5);
                  case 45:
                     String var44 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(var2, var4, var1));
                     double var45 = ScriptRuntime.toInteger(var5, 0);
                     if (var45 >= 0.0D && var45 < (double)var44.length()) {
                        return var44.codePointAt((int)var45);
                     }

                     return Undefined.instance;
                  case 48:
                     return new NativeStringIterator(var3, var4);
                  default:
                     StringBuilder var47 = new StringBuilder();
                     var47.append("String.prototype has no method: ");
                     var47.append(var1.getFunctionName());
                     throw new IllegalArgumentException(var47.toString());
                  }
               }
            }

            if (var5.length > 0) {
               var4 = ScriptRuntime.toObject((Context)var2, (Scriptable)var3, (Object)ScriptRuntime.toCharSequence(var5[0]));
               Object[] var12 = new Object[var5.length - var7];

               for(int var13 = 0; var13 < var12.length; ++var13) {
                  var12[var13] = var5[var13 + 1];
               }

               var5 = var12;
            } else {
               var4 = ScriptRuntime.toObject((Context)var2, (Scriptable)var3, (Object)ScriptRuntime.toCharSequence(var4));
            }

            var6 = -var6;
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      Object var2 = STRING_TAG;
      this.addIdFunctionProperty(var1, var2, -1, "fromCharCode", 1);
      this.addIdFunctionProperty(var1, var2, -5, "charAt", 2);
      this.addIdFunctionProperty(var1, var2, -6, "charCodeAt", 2);
      this.addIdFunctionProperty(var1, var2, -7, "indexOf", 2);
      this.addIdFunctionProperty(var1, var2, -8, "lastIndexOf", 2);
      this.addIdFunctionProperty(var1, var2, -9, "split", 3);
      this.addIdFunctionProperty(var1, var2, -10, "substring", 3);
      this.addIdFunctionProperty(var1, var2, -11, "toLowerCase", 1);
      this.addIdFunctionProperty(var1, var2, -12, "toUpperCase", 1);
      this.addIdFunctionProperty(var1, var2, -13, "substr", 3);
      this.addIdFunctionProperty(var1, var2, -14, "concat", 2);
      this.addIdFunctionProperty(var1, var2, -15, "slice", 3);
      this.addIdFunctionProperty(var1, var2, -30, "equalsIgnoreCase", 2);
      this.addIdFunctionProperty(var1, var2, -31, "match", 2);
      this.addIdFunctionProperty(var1, var2, -32, "search", 2);
      this.addIdFunctionProperty(var1, var2, -33, "replace", 2);
      this.addIdFunctionProperty(var1, var2, -34, "localeCompare", 2);
      this.addIdFunctionProperty(var1, var2, -35, "toLocaleLowerCase", 1);
      super.fillConstructorProperties(var1);
   }

   protected int findInstanceIdInfo(String var1) {
      return var1.equals("length") ? instanceIdInfo(7, 1) : super.findInstanceIdInfo(var1);
   }

   protected int findPrototypeId(String var1) {
      byte var3;
      String var4;
      switch(var1.length()) {
      case 3:
         char var2 = var1.charAt(2);
         if (var2 == 'b') {
            char var9 = var1.charAt(0);
            var3 = 0;
            var4 = null;
            if (var9 == 's') {
               char var10 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var10 == 'u') {
                  return 24;
               }
            }
         } else if (var2 == 'g') {
            char var7 = var1.charAt(0);
            var3 = 0;
            var4 = null;
            if (var7 == 'b') {
               char var8 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var8 == 'i') {
                  return 21;
               }
            }
         } else {
            var3 = 0;
            var4 = null;
            if (var2 == 'p') {
               char var5 = var1.charAt(0);
               var3 = 0;
               var4 = null;
               if (var5 == 's') {
                  char var6 = var1.charAt(1);
                  var3 = 0;
                  var4 = null;
                  if (var6 == 'u') {
                     return 23;
                  }
               }
            }
         }
         break;
      case 4:
         char var11 = var1.charAt(0);
         if (var11 == 'b') {
            var4 = "bold";
            var3 = 16;
         } else if (var11 == 'l') {
            var4 = "link";
            var3 = 27;
         } else {
            var3 = 0;
            var4 = null;
            if (var11 == 't') {
               var4 = "trim";
               var3 = 37;
            }
         }
         break;
      case 5:
         char var12 = var1.charAt(4);
         if (var12 != 'd') {
            if (var12 != 'e') {
               if (var12 != 'h') {
                  if (var12 != 't') {
                     if (var12 != 'k') {
                        if (var12 != 'l') {
                           var3 = 0;
                           var4 = null;
                        } else {
                           var4 = "small";
                           var3 = 20;
                        }
                     } else {
                        var4 = "blink";
                        var3 = 22;
                     }
                  } else {
                     var4 = "split";
                     var3 = 9;
                  }
               } else {
                  var4 = "match";
                  var3 = 31;
               }
            } else {
               var4 = "slice";
               var3 = 15;
            }
         } else {
            var4 = "fixed";
            var3 = 18;
         }
         break;
      case 6:
         char var13 = var1.charAt(1);
         if (var13 != 'a') {
            if (var13 != 'e') {
               if (var13 != 'h') {
                  if (var13 != 'q') {
                     if (var13 != 'n') {
                        if (var13 != 'o') {
                           if (var13 != 't') {
                              if (var13 != 'u') {
                                 var3 = 0;
                                 var4 = null;
                              } else {
                                 var4 = "substr";
                                 var3 = 13;
                              }
                           } else {
                              var4 = "strike";
                              var3 = 19;
                           }
                        } else {
                           var4 = "concat";
                           var3 = 14;
                        }
                     } else {
                        var4 = "anchor";
                        var3 = 28;
                     }
                  } else {
                     var4 = "equals";
                     var3 = 29;
                  }
               } else {
                  var4 = "charAt";
                  var3 = 5;
               }
            } else {
               char var14 = var1.charAt(0);
               if (var14 == 'r') {
                  var4 = "repeat";
                  var3 = 44;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var14 == 's') {
                     var4 = "search";
                     var3 = 32;
                  }
               }
            }
         } else {
            var4 = "padEnd";
            var3 = 47;
         }
         break;
      case 7:
         char var15 = var1.charAt(1);
         if (var15 != 'a') {
            if (var15 != 'e') {
               if (var15 != 'n') {
                  if (var15 != 't') {
                     var3 = 0;
                     var4 = null;
                  } else {
                     var4 = "italics";
                     var3 = 17;
                  }
               } else {
                  var4 = "indexOf";
                  var3 = 7;
               }
            } else {
               var4 = "replace";
               var3 = 33;
            }
         } else {
            var4 = "valueOf";
            var3 = 4;
         }
         break;
      case 8:
         char var16 = var1.charAt(6);
         if (var16 != 'c') {
            if (var16 != 'n') {
               if (var16 != 'r') {
                  if (var16 != 't') {
                     if (var16 != 'z') {
                        if (var16 != 'e') {
                           if (var16 != 'f') {
                              var3 = 0;
                              var4 = null;
                           } else {
                              var4 = "trimLeft";
                              var3 = 38;
                           }
                        } else {
                           var4 = "includes";
                           var3 = 40;
                        }
                     } else {
                        var4 = "fontsize";
                        var3 = 25;
                     }
                  } else {
                     var4 = "endsWith";
                     var3 = 42;
                  }
               } else {
                  var4 = "padStart";
                  var3 = 46;
               }
            } else {
               var4 = "toString";
               var3 = 2;
            }
         } else {
            var4 = "toSource";
            var3 = 3;
         }
         break;
      case 9:
         char var17 = var1.charAt(0);
         if (var17 != 'f') {
            if (var17 != 'n') {
               if (var17 != 's') {
                  if (var17 != 't') {
                     var3 = 0;
                     var4 = null;
                  } else {
                     var4 = "trimRight";
                     var3 = 39;
                  }
               } else {
                  var4 = "substring";
                  var3 = 10;
               }
            } else {
               var4 = "normalize";
               var3 = 43;
            }
         } else {
            var4 = "fontcolor";
            var3 = 26;
         }
         break;
      case 10:
         char var18 = var1.charAt(0);
         if (var18 == 'c') {
            var4 = "charCodeAt";
            var3 = 6;
         } else {
            var3 = 0;
            var4 = null;
            if (var18 == 's') {
               var4 = "startsWith";
               var3 = 41;
            }
         }
         break;
      case 11:
         char var19 = var1.charAt(2);
         if (var19 != 'L') {
            if (var19 != 'U') {
               if (var19 != 'd') {
                  if (var19 != 'n') {
                     if (var19 != 's') {
                        var3 = 0;
                        var4 = null;
                     } else {
                        var4 = "lastIndexOf";
                        var3 = 8;
                     }
                  } else {
                     var4 = "constructor";
                     var3 = 1;
                  }
               } else {
                  var4 = "codePointAt";
                  var3 = 45;
               }
            } else {
               var4 = "toUpperCase";
               var3 = 12;
            }
         } else {
            var4 = "toLowerCase";
            var3 = 11;
         }
         break;
      case 12:
      case 14:
      case 15:
      default:
         var3 = 0;
         var4 = null;
         break;
      case 13:
         var4 = "localeCompare";
         var3 = 34;
         break;
      case 16:
         var4 = "equalsIgnoreCase";
         var3 = 30;
         break;
      case 17:
         char var20 = var1.charAt(8);
         if (var20 == 'L') {
            var4 = "toLocaleLowerCase";
            var3 = 35;
         } else {
            var3 = 0;
            var4 = null;
            if (var20 == 'U') {
               var4 = "toLocaleUpperCase";
               var3 = 36;
            }
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   protected int findPrototypeId(Symbol var1) {
      return SymbolKey.ITERATOR.equals(var1) ? 48 : 0;
   }

   public Object get(int var1, Scriptable var2) {
      return var1 >= 0 && var1 < this.string.length() ? String.valueOf(this.string.charAt(var1)) : super.get(var1, var2);
   }

   public String getClassName() {
      return "String";
   }

   protected Object[] getIds(boolean var1, boolean var2) {
      Context var3 = Context.getCurrentContext();
      if (var3 != null && var3.getLanguageVersion() >= 200) {
         Object[] var4 = super.getIds(var1, var2);
         Object[] var5 = new Object[var4.length + this.string.length()];

         int var6;
         for(var6 = 0; var6 < this.string.length(); ++var6) {
            var5[var6] = var6;
         }

         System.arraycopy(var4, 0, var5, var6, var4.length);
         return var5;
      } else {
         return super.getIds(var1, var2);
      }
   }

   protected String getInstanceIdName(int var1) {
      return var1 == 1 ? "length" : super.getInstanceIdName(var1);
   }

   protected Object getInstanceIdValue(int var1) {
      return var1 == 1 ? ScriptRuntime.wrapInt(this.string.length()) : super.getInstanceIdValue(var1);
   }

   int getLength() {
      return this.string.length();
   }

   protected int getMaxInstanceId() {
      return 1;
   }

   public boolean has(int var1, Scriptable var2) {
      return var1 >= 0 && var1 < this.string.length() ? true : super.has(var1, var2);
   }

   protected void initPrototypeId(int var1) {
      if (var1 == 48) {
         this.initPrototypeMethod(STRING_TAG, var1, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
      } else {
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
            var3 = "toSource";
            var2 = 0;
            break;
         case 4:
            var3 = "valueOf";
            var2 = 0;
            break;
         case 5:
            var2 = 1;
            var3 = "charAt";
            break;
         case 6:
            var2 = 1;
            var3 = "charCodeAt";
            break;
         case 7:
            var2 = 1;
            var3 = "indexOf";
            break;
         case 8:
            var2 = 1;
            var3 = "lastIndexOf";
            break;
         case 9:
            var2 = 2;
            var3 = "split";
            break;
         case 10:
            var2 = 2;
            var3 = "substring";
            break;
         case 11:
            var3 = "toLowerCase";
            var2 = 0;
            break;
         case 12:
            var3 = "toUpperCase";
            var2 = 0;
            break;
         case 13:
            var2 = 2;
            var3 = "substr";
            break;
         case 14:
            var2 = 1;
            var3 = "concat";
            break;
         case 15:
            var2 = 2;
            var3 = "slice";
            break;
         case 16:
            var3 = "bold";
            var2 = 0;
            break;
         case 17:
            var3 = "italics";
            var2 = 0;
            break;
         case 18:
            var3 = "fixed";
            var2 = 0;
            break;
         case 19:
            var3 = "strike";
            var2 = 0;
            break;
         case 20:
            var3 = "small";
            var2 = 0;
            break;
         case 21:
            var3 = "big";
            var2 = 0;
            break;
         case 22:
            var3 = "blink";
            var2 = 0;
            break;
         case 23:
            var3 = "sup";
            var2 = 0;
            break;
         case 24:
            var3 = "sub";
            var2 = 0;
            break;
         case 25:
            var3 = "fontsize";
            var2 = 0;
            break;
         case 26:
            var3 = "fontcolor";
            var2 = 0;
            break;
         case 27:
            var3 = "link";
            var2 = 0;
            break;
         case 28:
            var3 = "anchor";
            var2 = 0;
            break;
         case 29:
            var2 = 1;
            var3 = "equals";
            break;
         case 30:
            var2 = 1;
            var3 = "equalsIgnoreCase";
            break;
         case 31:
            var2 = 1;
            var3 = "match";
            break;
         case 32:
            var2 = 1;
            var3 = "search";
            break;
         case 33:
            var2 = 2;
            var3 = "replace";
            break;
         case 34:
            var2 = 1;
            var3 = "localeCompare";
            break;
         case 35:
            var3 = "toLocaleLowerCase";
            var2 = 0;
            break;
         case 36:
            var3 = "toLocaleUpperCase";
            var2 = 0;
            break;
         case 37:
            var3 = "trim";
            var2 = 0;
            break;
         case 38:
            var3 = "trimLeft";
            var2 = 0;
            break;
         case 39:
            var3 = "trimRight";
            var2 = 0;
            break;
         case 40:
            var2 = 1;
            var3 = "includes";
            break;
         case 41:
            var2 = 1;
            var3 = "startsWith";
            break;
         case 42:
            var2 = 1;
            var3 = "endsWith";
            break;
         case 43:
            var3 = "normalize";
            var2 = 0;
            break;
         case 44:
            var2 = 1;
            var3 = "repeat";
            break;
         case 45:
            var2 = 1;
            var3 = "codePointAt";
            break;
         case 46:
            var2 = 1;
            var3 = "padStart";
            break;
         case 47:
            var2 = 1;
            var3 = "padEnd";
            break;
         default:
            throw new IllegalArgumentException(String.valueOf(var1));
         }

         this.initPrototypeMethod(STRING_TAG, var1, var3, (String)null, var2);
      }
   }

   public void put(int var1, Scriptable var2, Object var3) {
      if (var1 < 0 || var1 >= this.string.length()) {
         super.put(var1, var2, var3);
      }
   }

   public CharSequence toCharSequence() {
      return this.string;
   }

   public String toString() {
      CharSequence var1 = this.string;
      return var1 instanceof String ? (String)var1 : var1.toString();
   }
}
