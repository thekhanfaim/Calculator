package org.mozilla.javascript;

import java.io.Serializable;
import org.mozilla.javascript.xml.XMLLib;

public class NativeGlobal implements Serializable, IdFunctionCall {
   private static final Object FTAG = "Global";
   private static final int INVALID_UTF8 = Integer.MAX_VALUE;
   private static final int Id_decodeURI = 1;
   private static final int Id_decodeURIComponent = 2;
   private static final int Id_encodeURI = 3;
   private static final int Id_encodeURIComponent = 4;
   private static final int Id_escape = 5;
   private static final int Id_eval = 6;
   private static final int Id_isFinite = 7;
   private static final int Id_isNaN = 8;
   private static final int Id_isXMLName = 9;
   private static final int Id_new_CommonError = 14;
   private static final int Id_parseFloat = 10;
   private static final int Id_parseInt = 11;
   private static final int Id_unescape = 12;
   private static final int Id_uneval = 13;
   private static final int LAST_SCOPE_FUNCTION_ID = 13;
   private static final String URI_DECODE_RESERVED = ";/?:@&=+$,#";
   static final long serialVersionUID = 6080442165748707530L;

   @Deprecated
   public static EcmaError constructError(Context var0, String var1, String var2, Scriptable var3) {
      return ScriptRuntime.constructError(var1, var2);
   }

   @Deprecated
   public static EcmaError constructError(Context var0, String var1, String var2, Scriptable var3, String var4, int var5, int var6, String var7) {
      return ScriptRuntime.constructError(var1, var2, var4, var5, var7, var6);
   }

   private static String decode(String var0, boolean var1) {
      char[] var2 = null;
      int var3 = 0;
      int var4 = 0;
      int var5 = var0.length();

      while(true) {
         while(var4 != var5) {
            char var6 = var0.charAt(var4);
            if (var6 == '%') {
               if (var2 == null) {
                  var2 = new char[var5];
                  var0.getChars(0, var4, var2, 0);
                  var3 = var4;
               }

               int var7 = var4;
               if (var4 + 3 > var5) {
                  throw uriError();
               }

               int var8 = unHex(var0.charAt(var4 + 1), var0.charAt(var4 + 2));
               if (var8 < 0) {
                  throw uriError();
               }

               var4 += 3;
               char var13;
               if ((var8 & 128) == 0) {
                  var13 = (char)var8;
               } else {
                  if ((var8 & 192) == 128) {
                     throw uriError();
                  }

                  byte var9;
                  int var10;
                  int var11;
                  if ((var8 & 32) == 0) {
                     var9 = 1;
                     var10 = var8 & 31;
                     var11 = 128;
                  } else if ((var8 & 16) == 0) {
                     var9 = 2;
                     var10 = var8 & 15;
                     var11 = 2048;
                  } else if ((var8 & 8) == 0) {
                     var9 = 3;
                     var10 = var8 & 7;
                     var11 = 65536;
                  } else if ((var8 & 4) == 0) {
                     var9 = 4;
                     var10 = var8 & 3;
                     var11 = 2097152;
                  } else {
                     if ((var8 & 2) != 0) {
                        throw uriError();
                     }

                     var9 = 5;
                     var10 = var8 & 1;
                     var11 = 67108864;
                  }

                  if (var4 + var9 * 3 > var5) {
                     throw uriError();
                  }

                  for(int var12 = 0; var12 != var9; ++var12) {
                     if (var0.charAt(var4) != '%') {
                        throw uriError();
                     }

                     int var20 = unHex(var0.charAt(var4 + 1), var0.charAt(var4 + 2));
                     if (var20 < 0 || (var20 & 192) != 128) {
                        throw uriError();
                     }

                     var10 = var10 << 6 | var20 & 63;
                     var4 += 3;
                  }

                  if (var10 < var11 || var10 >= 55296 && var10 <= 57343) {
                     var10 = Integer.MAX_VALUE;
                  } else if (var10 == 65534 || var10 == 65535) {
                     var10 = 65533;
                  }

                  if (var10 >= 65536) {
                     int var17 = var10 - 65536;
                     if (var17 > 1048575) {
                        throw uriError();
                     }

                     char var18 = (char)('\ud800' + (var17 >>> 10));
                     var13 = (char)('\udc00' + (var17 & 1023));
                     int var19 = var3 + 1;
                     var2[var3] = var18;
                     var3 = var19;
                  } else {
                     var13 = (char)var10;
                  }
               }

               int var16;
               if (var1 && ";/?:@&=+$,#".indexOf(var13) >= 0) {
                  for(int var15 = var7; var15 != var4; var3 = var16) {
                     var16 = var3 + 1;
                     var2[var3] = var0.charAt(var15);
                     ++var15;
                  }
               } else {
                  int var14 = var3 + 1;
                  var2[var3] = var13;
                  var3 = var14;
               }
            } else {
               if (var2 != null) {
                  int var21 = var3 + 1;
                  var2[var3] = var6;
                  var3 = var21;
               }

               ++var4;
            }
         }

         if (var2 == null) {
            return var0;
         }

         return new String(var2, 0, var3);
      }
   }

   private static String encode(String var0, boolean var1) {
      byte[] var2 = null;
      StringBuilder var3 = null;
      int var4 = 0;

      for(int var5 = var0.length(); var4 != var5; ++var4) {
         char var6 = var0.charAt(var4);
         if (encodeUnescaped(var6, var1)) {
            if (var3 != null) {
               var3.append(var6);
            }
         } else {
            if (var3 == null) {
               var3 = new StringBuilder(var5 + 3);
               var3.append(var0);
               var3.setLength(var4);
               var2 = new byte[6];
            }

            if ('\udc00' <= var6 && var6 <= '\udfff') {
               throw uriError();
            }

            int var7;
            if (var6 >= '\ud800' && '\udbff' >= var6) {
               ++var4;
               if (var4 == var5) {
                  throw uriError();
               }

               char var14 = var0.charAt(var4);
               if ('\udc00' > var14 || var14 > '\udfff') {
                  throw uriError();
               }

               var7 = 65536 + (var6 - '\ud800' << 10) + (var14 - '\udc00');
            } else {
               var7 = var6;
            }

            int var8 = oneUcs4ToUtf8Char(var2, var7);

            for(int var9 = 0; var9 < var8; ++var9) {
               int var10 = 255 & var2[var9];
               var3.append('%');
               var3.append(toHexChar(var10 >>> 4));
               var3.append(toHexChar(var10 & 15));
            }
         }
      }

      if (var3 == null) {
         return var0;
      } else {
         return var3.toString();
      }
   }

   private static boolean encodeUnescaped(char var0, boolean var1) {
      if (('A' > var0 || var0 > 'Z') && ('a' > var0 || var0 > 'z') && ('0' > var0 || var0 > '9')) {
         if ("-_.!~*'()".indexOf(var0) >= 0) {
            return true;
         } else if (var1) {
            return ";/?:@&=+$,#".indexOf(var0) >= 0;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static void init(Context var0, Scriptable var1, boolean var2) {
      NativeGlobal var3 = new NativeGlobal();

      for(int var4 = 1; var4 <= 13; ++var4) {
         byte var12;
         String var13;
         switch(var4) {
         case 1:
            var12 = 1;
            var13 = "decodeURI";
            break;
         case 2:
            var12 = 1;
            var13 = "decodeURIComponent";
            break;
         case 3:
            var12 = 1;
            var13 = "encodeURI";
            break;
         case 4:
            var12 = 1;
            var13 = "encodeURIComponent";
            break;
         case 5:
            var12 = 1;
            var13 = "escape";
            break;
         case 6:
            var12 = 1;
            var13 = "eval";
            break;
         case 7:
            var12 = 1;
            var13 = "isFinite";
            break;
         case 8:
            var12 = 1;
            var13 = "isNaN";
            break;
         case 9:
            var12 = 1;
            var13 = "isXMLName";
            break;
         case 10:
            var12 = 1;
            var13 = "parseFloat";
            break;
         case 11:
            var12 = 2;
            var13 = "parseInt";
            break;
         case 12:
            var12 = 1;
            var13 = "unescape";
            break;
         case 13:
            var12 = 1;
            var13 = "uneval";
            break;
         default:
            throw Kit.codeBug();
         }

         IdFunctionObject var14 = new IdFunctionObject(var3, FTAG, var4, var13, var12, var1);
         if (var2) {
            var14.sealObject();
         }

         var14.exportAsScopeProperty();
      }

      ScriptableObject.defineProperty(var1, "NaN", ScriptRuntime.NaNobj, 7);
      ScriptableObject.defineProperty(var1, "Infinity", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), 7);
      ScriptableObject.defineProperty(var1, "undefined", Undefined.instance, 7);
      TopLevel.NativeErrors[] var5 = TopLevel.NativeErrors.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         TopLevel.NativeErrors var8 = var5[var7];
         if (var8 != TopLevel.NativeErrors.Error) {
            String var9 = var8.name();
            ScriptableObject var10 = (ScriptableObject)ScriptRuntime.newBuiltinObject(var0, var1, TopLevel.Builtins.Error, ScriptRuntime.emptyArgs);
            var10.put((String)"name", var10, var9);
            var10.put((String)"message", var10, "");
            IdFunctionObject var11 = new IdFunctionObject(var3, FTAG, 14, var9, 1, var1);
            var11.markAsConstructor(var10);
            var10.put((String)"constructor", var10, var11);
            var10.setAttributes((String)"constructor", 2);
            if (var2) {
               var10.sealObject();
               var11.sealObject();
            }

            var11.exportAsScopeProperty();
         }
      }

   }

   static boolean isEvalFunction(Object var0) {
      if (var0 instanceof IdFunctionObject) {
         IdFunctionObject var1 = (IdFunctionObject)var0;
         if (var1.hasTag(FTAG) && var1.methodId() == 6) {
            return true;
         }
      }

      return false;
   }

   private Object js_escape(Object[] var1) {
      String var2 = ScriptRuntime.toString(var1, 0);
      int var3 = 7;
      if (var1.length > 1) {
         double var19 = ScriptRuntime.toNumber(var1[1]);
         if (var19 != var19) {
            throw Context.reportRuntimeError0("msg.bad.esc.mask");
         }

         int var21 = (int)var19;
         var3 = var21;
         if ((double)var21 != var19 || (var21 & -8) != 0) {
            throw Context.reportRuntimeError0("msg.bad.esc.mask");
         }
      }

      StringBuilder var4 = null;
      int var5 = 0;

      for(int var6 = var2.length(); var5 != var6; ++var5) {
         char var7 = var2.charAt(var5);
         if (var3 != 0 && (var7 >= '0' && var7 <= '9' || var7 >= 'A' && var7 <= 'Z' || var7 >= 'a' && var7 <= 'z' || var7 == '@' || var7 == '*' || var7 == '_' || var7 == '-' || var7 == '.' || (var3 & 4) != 0 && (var7 == '/' || var7 == '+'))) {
            if (var4 != null) {
               var4.append(var7);
            }
         } else {
            if (var4 == null) {
               var4 = new StringBuilder(var6 + 3);
               var4.append(var2);
               var4.setLength(var5);
            }

            byte var10;
            if (var7 < 256) {
               if (var7 == ' ' && var3 == 2) {
                  var4.append('+');
                  continue;
               }

               var4.append('%');
               var10 = 2;
            } else {
               var4.append('%');
               var4.append('u');
               var10 = 4;
            }

            for(int var11 = 4 * (var10 - 1); var11 >= 0; var11 -= 4) {
               int var12 = 15 & var7 >> var11;
               int var13;
               if (var12 < 10) {
                  var13 = var12 + 48;
               } else {
                  var13 = var12 + 55;
               }

               var4.append((char)var13);
            }
         }
      }

      if (var4 == null) {
         return var2;
      } else {
         return var4.toString();
      }
   }

   private Object js_eval(Context var1, Scriptable var2, Object[] var3) {
      Scriptable var4 = ScriptableObject.getTopLevelScope(var2);
      return ScriptRuntime.evalSpecial(var1, var4, var4, var3, "eval code", 1);
   }

   static Object js_parseFloat(Object[] var0) {
      if (var0.length < 1) {
         return ScriptRuntime.NaNobj;
      } else {
         String var1 = ScriptRuntime.toString(var0[0]);
         int var2 = var1.length();

         for(int var3 = 0; var3 != var2; ++var3) {
            char var4 = var1.charAt(var3);
            if (!ScriptRuntime.isStrWhiteSpaceChar(var4)) {
               int var5 = var3;
               if (var4 == '+' || var4 == '-') {
                  var5 = var3 + 1;
                  if (var5 == var2) {
                     return ScriptRuntime.NaNobj;
                  }

                  var4 = var1.charAt(var5);
               }

               if (var4 == 'I') {
                  if (var5 + 8 <= var2 && var1.regionMatches(var5, "Infinity", 0, 8)) {
                     double var13;
                     if (var1.charAt(var3) == '-') {
                        var13 = Double.NEGATIVE_INFINITY;
                     } else {
                        var13 = Double.POSITIVE_INFINITY;
                     }

                     return ScriptRuntime.wrapNumber(var13);
                  }

                  return ScriptRuntime.NaNobj;
               }

               int var6 = -1;
               int var7 = -1;

               boolean var8;
               label101:
               for(var8 = false; var5 < var2; ++var5) {
                  char var12 = var1.charAt(var5);
                  if (var12 != '+') {
                     if (var12 == 'E' || var12 == 'e') {
                        if (var7 != -1 || var5 == var2 - 1) {
                           break;
                        }

                        var7 = var5;
                        continue;
                     }

                     if (var12 != '-') {
                        if (var12 != '.') {
                           switch(var12) {
                           case '0':
                           case '1':
                           case '2':
                           case '3':
                           case '4':
                           case '5':
                           case '6':
                           case '7':
                           case '8':
                           case '9':
                              if (var7 != -1) {
                                 var8 = true;
                              }
                              continue;
                           default:
                              break label101;
                           }
                        } else {
                           if (var6 != -1) {
                              break;
                           }

                           var6 = var5;
                           continue;
                        }
                     }
                  }

                  if (var7 != var5 - 1) {
                     break;
                  }

                  if (var5 == var2 - 1) {
                     --var5;
                     break;
                  }
               }

               if (var7 != -1 && !var8) {
                  var5 = var7;
               }

               String var9 = var1.substring(var3, var5);

               try {
                  Double var11 = Double.valueOf(var9);
                  return var11;
               } catch (NumberFormatException var15) {
                  return ScriptRuntime.NaNobj;
               }
            }
         }

         return ScriptRuntime.NaNobj;
      }
   }

   static Object js_parseInt(Object[] var0) {
      String var1 = ScriptRuntime.toString(var0, 0);
      int var2 = ScriptRuntime.toInt32(var0, 1);
      int var3 = var1.length();
      if (var3 == 0) {
         return ScriptRuntime.NaNobj;
      } else {
         int var4 = 0;

         char var5;
         do {
            var5 = var1.charAt(var4);
            if (!ScriptRuntime.isStrWhiteSpaceChar(var5)) {
               break;
            }

            ++var4;
         } while(var4 < var3);

         boolean var6;
         label76: {
            var6 = false;
            if (var5 != '+') {
               boolean var7 = false;
               if (var5 == '-') {
                  var7 = true;
               }

               var6 = var7;
               if (!var7) {
                  break label76;
               }
            }

            ++var4;
         }

         if (var2 == 0) {
            var2 = -1;
         } else {
            if (var2 < 2 || var2 > 36) {
               return ScriptRuntime.NaNobj;
            }

            if (var2 == 16 && var3 - var4 > 1 && var1.charAt(var4) == '0') {
               char var13 = var1.charAt(var4 + 1);
               if (var13 == 'x' || var13 == 'X') {
                  var4 += 2;
               }
            }
         }

         if (var2 == -1) {
            var2 = 10;
            if (var3 - var4 > 1 && var1.charAt(var4) == '0') {
               char var12 = var1.charAt(var4 + 1);
               if (var12 != 'x' && var12 != 'X') {
                  if ('0' <= var12 && var12 <= '9') {
                     var2 = 8;
                     ++var4;
                  }
               } else {
                  var2 = 16;
                  var4 += 2;
               }
            }
         }

         double var8 = ScriptRuntime.stringPrefixToNumber(var1, var4, var2);
         double var10;
         if (var6) {
            var10 = -var8;
         } else {
            var10 = var8;
         }

         return ScriptRuntime.wrapNumber(var10);
      }
   }

   private Object js_unescape(Object[] var1) {
      String var2 = ScriptRuntime.toString(var1, 0);
      int var3 = var2.indexOf(37);
      if (var3 >= 0) {
         int var4 = var2.length();
         char[] var5 = var2.toCharArray();
         int var6 = var3;

         for(int var7 = var3; var7 != var4; ++var6) {
            char var8 = var5[var7];
            ++var7;
            if (var8 == '%' && var7 != var4) {
               int var9;
               int var10;
               if (var5[var7] == 'u') {
                  var9 = var7 + 1;
                  var10 = var7 + 5;
               } else {
                  var9 = var7;
                  var10 = var7 + 2;
               }

               if (var10 <= var4) {
                  int var11 = 0;

                  for(int var12 = var9; var12 != var10; ++var12) {
                     var11 = Kit.xDigitToInt(var5[var12], var11);
                  }

                  if (var11 >= 0) {
                     var8 = (char)var11;
                     var7 = var10;
                  }
               }
            }

            var5[var6] = var8;
         }

         var2 = new String(var5, 0, var6);
      }

      return var2;
   }

   private static int oneUcs4ToUtf8Char(byte[] var0, int var1) {
      if ((var1 & -128) == 0) {
         var0[0] = (byte)var1;
         return 1;
      } else {
         int var2 = var1 >>> 11;

         int var3;
         for(var3 = 2; var2 != 0; ++var3) {
            var2 >>>= 5;
         }

         int var4 = var3;

         while(true) {
            --var4;
            if (var4 <= 0) {
               var0[0] = (byte)(var1 + (256 - (1 << 8 - var3)));
               return var3;
            }

            var0[var4] = (byte)(128 | var1 & 63);
            var1 >>>= 6;
         }
      }
   }

   private static char toHexChar(int var0) {
      if (var0 >> 4 != 0) {
         Kit.codeBug();
      }

      int var1;
      if (var0 < 10) {
         var1 = var0 + 48;
      } else {
         var1 = 65 + (var0 - 10);
      }

      return (char)var1;
   }

   private static int unHex(char var0) {
      if ('A' <= var0 && var0 <= 'F') {
         return 10 + (var0 - 65);
      } else if ('a' <= var0 && var0 <= 'f') {
         return 10 + (var0 - 97);
      } else {
         return '0' <= var0 && var0 <= '9' ? var0 - 48 : -1;
      }
   }

   private static int unHex(char var0, char var1) {
      int var2 = unHex(var0);
      int var3 = unHex(var1);
      return var2 >= 0 && var3 >= 0 ? var3 | var2 << 4 : -1;
   }

   private static EcmaError uriError() {
      return ScriptRuntime.constructError("URIError", ScriptRuntime.getMessage0("msg.bad.uri"));
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (var1.hasTag(FTAG)) {
         int var6 = var1.methodId();
         byte var7 = 1;
         switch(var6) {
         case 1:
         case 2:
            String var8 = ScriptRuntime.toString(var5, 0);
            if (var6 != var7) {
               var7 = 0;
            }

            return decode(var8, (boolean)var7);
         case 3:
         case 4:
            String var9 = ScriptRuntime.toString(var5, 0);
            if (var6 != 3) {
               var7 = 0;
            }

            return encode(var9, (boolean)var7);
         case 5:
            return this.js_escape(var5);
         case 6:
            return this.js_eval(var2, var3, var5);
         case 7:
            if (var5.length < var7) {
               return Boolean.FALSE;
            }

            return NativeNumber.isFinite(var5[0]);
         case 8:
            if (var5.length < var7) {
               var7 = 1;
            } else {
               double var10 = ScriptRuntime.toNumber(var5[0]);
               if (var10 == var10) {
                  var7 = 0;
               }
            }

            return ScriptRuntime.wrapBoolean((boolean)var7);
         case 9:
            Object var12;
            if (var5.length == 0) {
               var12 = Undefined.instance;
            } else {
               var12 = var5[0];
            }

            return ScriptRuntime.wrapBoolean(XMLLib.extractFromScope(var3).isXMLName(var2, var12));
         case 10:
            return js_parseFloat(var5);
         case 11:
            return js_parseInt(var5);
         case 12:
            return this.js_unescape(var5);
         case 13:
            Object var13;
            if (var5.length != 0) {
               var13 = var5[0];
            } else {
               var13 = Undefined.instance;
            }

            return ScriptRuntime.uneval(var2, var3, var13);
         case 14:
            return NativeError.make(var2, var3, var1, var5);
         }
      }

      throw var1.unknown();
   }
}
