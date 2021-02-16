package org.mozilla.javascript;

final class NativeMath extends IdScriptableObject {
   private static final int Id_E = 37;
   private static final int Id_LN10 = 39;
   private static final int Id_LN2 = 40;
   private static final int Id_LOG10E = 42;
   private static final int Id_LOG2E = 41;
   private static final int Id_PI = 38;
   private static final int Id_SQRT1_2 = 43;
   private static final int Id_SQRT2 = 44;
   private static final int Id_abs = 2;
   private static final int Id_acos = 3;
   private static final int Id_acosh = 30;
   private static final int Id_asin = 4;
   private static final int Id_asinh = 31;
   private static final int Id_atan = 5;
   private static final int Id_atan2 = 6;
   private static final int Id_atanh = 32;
   private static final int Id_cbrt = 20;
   private static final int Id_ceil = 7;
   private static final int Id_clz32 = 36;
   private static final int Id_cos = 8;
   private static final int Id_cosh = 21;
   private static final int Id_exp = 9;
   private static final int Id_expm1 = 22;
   private static final int Id_floor = 10;
   private static final int Id_fround = 35;
   private static final int Id_hypot = 23;
   private static final int Id_imul = 28;
   private static final int Id_log = 11;
   private static final int Id_log10 = 25;
   private static final int Id_log1p = 24;
   private static final int Id_log2 = 34;
   private static final int Id_max = 12;
   private static final int Id_min = 13;
   private static final int Id_pow = 14;
   private static final int Id_random = 15;
   private static final int Id_round = 16;
   private static final int Id_sign = 33;
   private static final int Id_sin = 17;
   private static final int Id_sinh = 26;
   private static final int Id_sqrt = 18;
   private static final int Id_tan = 19;
   private static final int Id_tanh = 27;
   private static final int Id_toSource = 1;
   private static final int Id_trunc = 29;
   private static final int LAST_METHOD_ID = 36;
   private static final double LOG2E = 1.4426950408889634D;
   private static final Object MATH_TAG = "Math";
   private static final int MAX_ID = 44;
   private static final long serialVersionUID = -8838847185801131569L;

   private NativeMath() {
   }

   static void init(Scriptable var0, boolean var1) {
      NativeMath var2 = new NativeMath();
      var2.activatePrototypeMap(44);
      var2.setPrototype(getObjectPrototype(var0));
      var2.setParentScope(var0);
      if (var1) {
         var2.sealObject();
      }

      ScriptableObject.defineProperty(var0, "Math", var2, 2);
   }

   private static double js_hypot(Object[] var0) {
      if (var0 == null) {
         return 0.0D;
      } else {
         double var1 = 0.0D;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            double var5 = ScriptRuntime.toNumber(var0[var4]);
            if (var5 == ScriptRuntime.NaN) {
               return var5;
            }

            if (var5 == Double.POSITIVE_INFINITY) {
               return Double.POSITIVE_INFINITY;
            }

            if (var5 == Double.NEGATIVE_INFINITY) {
               return Double.POSITIVE_INFINITY;
            }

            var1 += var5 * var5;
         }

         return Math.sqrt(var1);
      }
   }

   private static int js_imul(Object[] var0) {
      return var0 == null ? 0 : ScriptRuntime.toInt32(var0, 0) * ScriptRuntime.toInt32(var0, 1);
   }

   private static double js_pow(double var0, double var2) {
      if (var2 != var2) {
         return var2;
      } else {
         double var4 = 0.0D;
         if (var2 == var4) {
            return 1.0D;
         } else if (var0 == var4) {
            if (1.0D / var0 > var4) {
               if (var2 <= var4) {
                  var4 = Double.POSITIVE_INFINITY;
               }

               return var4;
            } else {
               long var12 = (long)var2;
               if ((double)var12 == var2 && (var12 & 1L) != 0L) {
                  double var14;
                  if (var2 > var4) {
                     var14 = 0.0D;
                  } else {
                     var14 = Double.NEGATIVE_INFINITY;
                  }

                  var4 = var14;
               } else if (var2 <= var4) {
                  var4 = Double.POSITIVE_INFINITY;
               }

               return var4;
            }
         } else {
            double var6 = Math.pow(var0, var2);
            if (var6 != var6) {
               if (var2 == Double.POSITIVE_INFINITY) {
                  if (var0 < -1.0D || 1.0D < var0) {
                     return Double.POSITIVE_INFINITY;
                  }

                  if (-1.0D < var0 && var0 < 1.0D) {
                     return 0.0D;
                  }
               } else if (var2 == Double.NEGATIVE_INFINITY) {
                  if (var0 < -1.0D || 1.0D < var0) {
                     return 0.0D;
                  }

                  if (-1.0D < var0 && var0 < 1.0D) {
                     return Double.POSITIVE_INFINITY;
                  }
               } else {
                  if (var0 == Double.POSITIVE_INFINITY) {
                     if (var2 > var4) {
                        var4 = Double.POSITIVE_INFINITY;
                     }

                     return var4;
                  }

                  if (var0 == Double.NEGATIVE_INFINITY) {
                     long var8 = (long)var2;
                     if ((double)var8 == var2 && (1L & var8) != 0L) {
                        double var10;
                        if (var2 > var4) {
                           var10 = Double.NEGATIVE_INFINITY;
                        } else {
                           var10 = 0.0D;
                        }

                        return var10;
                     }

                     if (var2 > var4) {
                        var4 = Double.POSITIVE_INFINITY;
                     }

                     return var4;
                  }
               }
            }

            return var6;
         }
      }
   }

   private static double js_trunc(double var0) {
      return var0 < 0.0D ? Math.ceil(var0) : Math.floor(var0);
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(MATH_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         double var7 = Double.NEGATIVE_INFINITY;
         double var9 = Double.NaN;
         double var11 = 0.0D;
         double var15;
         switch(var6) {
         case 1:
            return "Math";
         case 2:
            double var13 = ScriptRuntime.toNumber(var5, 0);
            if (var13 != var11) {
               if (var13 < var11) {
                  var11 = -var13;
               } else {
                  var11 = var13;
               }
            }

            var15 = var11;
            break;
         case 3:
         case 4:
            double var17 = ScriptRuntime.toNumber(var5, 0);
            if (var17 == var17 && -1.0D <= var17 && var17 <= 1.0D) {
               double var19;
               if (var6 == 3) {
                  var19 = Math.acos(var17);
               } else {
                  var19 = Math.asin(var17);
               }

               var15 = var19;
            } else {
               var15 = Double.NaN;
            }
            break;
         case 5:
            var15 = Math.atan(ScriptRuntime.toNumber(var5, 0));
            break;
         case 6:
            var15 = Math.atan2(ScriptRuntime.toNumber(var5, 0), ScriptRuntime.toNumber(var5, 1));
            break;
         case 7:
            var15 = Math.ceil(ScriptRuntime.toNumber(var5, 0));
            break;
         case 8:
            double var21 = ScriptRuntime.toNumber(var5, 0);
            if (var21 != Double.POSITIVE_INFINITY && var21 != var7) {
               var9 = Math.cos(var21);
            }

            var15 = var9;
            break;
         case 9:
            double var23 = ScriptRuntime.toNumber(var5, 0);
            if (var23 == Double.POSITIVE_INFINITY) {
               var11 = var23;
            } else if (var23 != var7) {
               var11 = Math.exp(var23);
            }

            var15 = var11;
            break;
         case 10:
            var15 = Math.floor(ScriptRuntime.toNumber(var5, 0));
            break;
         case 11:
            double var25 = ScriptRuntime.toNumber(var5, 0);
            if (var25 >= var11) {
               var9 = Math.log(var25);
            }

            var15 = var9;
            break;
         case 12:
         case 13:
            if (var6 != 12) {
               var7 = Double.POSITIVE_INFINITY;
            }

            double var27 = var7;

            for(int var29 = 0; var29 != var5.length; ++var29) {
               double var30 = ScriptRuntime.toNumber(var5[var29]);
               if (var30 != var30) {
                  var15 = var30;
                  return ScriptRuntime.wrapNumber(var15);
               }

               if (var6 == 12) {
                  var27 = Math.max(var27, var30);
               } else {
                  var27 = Math.min(var27, var30);
               }
            }

            var15 = var27;
            break;
         case 14:
            var15 = js_pow(ScriptRuntime.toNumber(var5, 0), ScriptRuntime.toNumber(var5, 1));
            break;
         case 15:
            var15 = Math.random();
            break;
         case 16:
            double var32 = ScriptRuntime.toNumber(var5, 0);
            if (var32 == var32 && var32 != Double.POSITIVE_INFINITY && var32 != var7) {
               long var34 = Math.round(var32);
               if (var34 != 0L) {
                  var15 = (double)var34;
               } else if (var32 < var11) {
                  var15 = ScriptRuntime.negativeZero;
               } else if (var32 != var11) {
                  var15 = 0.0D;
               } else {
                  var15 = var32;
               }
            } else {
               var15 = var32;
            }
            break;
         case 17:
            double var36 = ScriptRuntime.toNumber(var5, 0);
            if (var36 != Double.POSITIVE_INFINITY && var36 != var7) {
               var9 = Math.sin(var36);
            }

            var15 = var9;
            break;
         case 18:
            var15 = Math.sqrt(ScriptRuntime.toNumber(var5, 0));
            break;
         case 19:
            var15 = Math.tan(ScriptRuntime.toNumber(var5, 0));
            break;
         case 20:
            var15 = Math.cbrt(ScriptRuntime.toNumber(var5, 0));
            break;
         case 21:
            var15 = Math.cosh(ScriptRuntime.toNumber(var5, 0));
            break;
         case 22:
            var15 = Math.expm1(ScriptRuntime.toNumber(var5, 0));
            break;
         case 23:
            var15 = js_hypot(var5);
            break;
         case 24:
            var15 = Math.log1p(ScriptRuntime.toNumber(var5, 0));
            break;
         case 25:
            var15 = Math.log10(ScriptRuntime.toNumber(var5, 0));
            break;
         case 26:
            var15 = Math.sinh(ScriptRuntime.toNumber(var5, 0));
            break;
         case 27:
            var15 = Math.tanh(ScriptRuntime.toNumber(var5, 0));
            break;
         case 28:
            return js_imul(var5);
         case 29:
            var15 = js_trunc(ScriptRuntime.toNumber(var5, 0));
            break;
         case 30:
            double var38 = ScriptRuntime.toNumber(var5, 0);
            if (var38 == var38) {
               return Math.log(var38 + Math.sqrt(var38 * var38 - 1.0D));
            }

            return var9;
         case 31:
            double var40 = ScriptRuntime.toNumber(var5, 0);
            if (var40 != Double.POSITIVE_INFINITY && var40 != var7) {
               if (var40 == var40) {
                  if (var40 == var11) {
                     if (1.0D / var40 > var11) {
                        return var11;
                     }

                     return 0.0D;
                  }

                  return Math.log(var40 + Math.sqrt(1.0D + var40 * var40));
               }

               return var9;
            }

            return var40;
         case 32:
            double var42 = ScriptRuntime.toNumber(var5, 0);
            if (var42 == var42 && -1.0D <= var42 && var42 <= 1.0D) {
               if (var42 == var11) {
                  if (1.0D / var42 > var11) {
                     return var11;
                  }

                  return 0.0D;
               }

               return 0.5D * Math.log((var42 + 1.0D) / (var42 - 1.0D));
            }

            return var9;
         case 33:
            double var44 = ScriptRuntime.toNumber(var5, 0);
            if (var44 == var44) {
               if (var44 == var11) {
                  if (1.0D / var44 > var11) {
                     return var11;
                  }

                  return 0.0D;
               }

               return Math.signum(var44);
            }

            return var9;
         case 34:
            double var46 = ScriptRuntime.toNumber(var5, 0);
            if (var46 >= var11) {
               var9 = 1.4426950408889634D * Math.log(var46);
            }

            var15 = var9;
            break;
         case 35:
            var15 = (double)((float)ScriptRuntime.toNumber(var5, 0));
            break;
         case 36:
            double var48 = ScriptRuntime.toNumber(var5, 0);
            if (var48 != var11 && var48 == var48 && var48 != Double.POSITIVE_INFINITY && var48 != var7) {
               long var50 = ScriptRuntime.toUint32(var48);
               if (var50 == 0L) {
                  return 32;
               }

               return 31.0D - Math.floor(1.4426950408889634D * Math.log((double)(var50 >>> 0)));
            }

            return 32;
         default:
            throw new IllegalStateException(String.valueOf(var6));
         }

         return ScriptRuntime.wrapNumber(var15);
      }
   }

   protected int findPrototypeId(String var1) {
      byte var3;
      String var4;
      label216:
      switch(var1.length()) {
      case 1:
         char var2 = var1.charAt(0);
         var3 = 0;
         var4 = null;
         if (var2 == 'E') {
            return 37;
         }
         break;
      case 2:
         char var5 = var1.charAt(0);
         var3 = 0;
         var4 = null;
         if (var5 == 'P') {
            char var6 = var1.charAt(1);
            var3 = 0;
            var4 = null;
            if (var6 == 'I') {
               return 38;
            }
         }
         break;
      case 3:
         char var7 = var1.charAt(0);
         if (var7 != 'L') {
            if (var7 != 'a') {
               if (var7 != 'c') {
                  if (var7 != 'e') {
                     if (var7 != 'p') {
                        if (var7 != 'l') {
                           if (var7 != 'm') {
                              if (var7 != 's') {
                                 if (var7 != 't') {
                                    var3 = 0;
                                    var4 = null;
                                 } else {
                                    char var25 = var1.charAt(2);
                                    var3 = 0;
                                    var4 = null;
                                    if (var25 == 'n') {
                                       char var26 = var1.charAt(1);
                                       var3 = 0;
                                       var4 = null;
                                       if (var26 == 'a') {
                                          return 19;
                                       }
                                    }
                                 }
                              } else {
                                 char var23 = var1.charAt(2);
                                 var3 = 0;
                                 var4 = null;
                                 if (var23 == 'n') {
                                    char var24 = var1.charAt(1);
                                    var3 = 0;
                                    var4 = null;
                                    if (var24 == 'i') {
                                       return 17;
                                    }
                                 }
                              }
                           } else {
                              char var20 = var1.charAt(2);
                              if (var20 == 'n') {
                                 char var22 = var1.charAt(1);
                                 var3 = 0;
                                 var4 = null;
                                 if (var22 == 'i') {
                                    return 13;
                                 }
                              } else {
                                 var3 = 0;
                                 var4 = null;
                                 if (var20 == 'x') {
                                    char var21 = var1.charAt(1);
                                    var3 = 0;
                                    var4 = null;
                                    if (var21 == 'a') {
                                       return 12;
                                    }
                                 }
                              }
                           }
                        } else {
                           char var18 = var1.charAt(2);
                           var3 = 0;
                           var4 = null;
                           if (var18 == 'g') {
                              char var19 = var1.charAt(1);
                              var3 = 0;
                              var4 = null;
                              if (var19 == 'o') {
                                 return 11;
                              }
                           }
                        }
                     } else {
                        char var16 = var1.charAt(2);
                        var3 = 0;
                        var4 = null;
                        if (var16 == 'w') {
                           char var17 = var1.charAt(1);
                           var3 = 0;
                           var4 = null;
                           if (var17 == 'o') {
                              return 14;
                           }
                        }
                     }
                  } else {
                     char var14 = var1.charAt(2);
                     var3 = 0;
                     var4 = null;
                     if (var14 == 'p') {
                        char var15 = var1.charAt(1);
                        var3 = 0;
                        var4 = null;
                        if (var15 == 'x') {
                           return 9;
                        }
                     }
                  }
               } else {
                  char var12 = var1.charAt(2);
                  var3 = 0;
                  var4 = null;
                  if (var12 == 's') {
                     char var13 = var1.charAt(1);
                     var3 = 0;
                     var4 = null;
                     if (var13 == 'o') {
                        return 8;
                     }
                  }
               }
            } else {
               char var10 = var1.charAt(2);
               var3 = 0;
               var4 = null;
               if (var10 == 's') {
                  char var11 = var1.charAt(1);
                  var3 = 0;
                  var4 = null;
                  if (var11 == 'b') {
                     return 2;
                  }
               }
            }
         } else {
            char var8 = var1.charAt(2);
            var3 = 0;
            var4 = null;
            if (var8 == '2') {
               char var9 = var1.charAt(1);
               var3 = 0;
               var4 = null;
               if (var9 == 'N') {
                  return 40;
               }
            }
         }
         break;
      case 4:
         char var27 = var1.charAt(1);
         if (var27 != 'N') {
            if (var27 != 'e') {
               if (var27 != 'i') {
                  if (var27 != 'm') {
                     if (var27 != 'o') {
                        if (var27 != 'q') {
                           if (var27 != 's') {
                              if (var27 != 't') {
                                 switch(var27) {
                                 case 'a':
                                    var4 = "tanh";
                                    var3 = 27;
                                    break label216;
                                 case 'b':
                                    var4 = "cbrt";
                                    var3 = 20;
                                    break label216;
                                 case 'c':
                                    var4 = "acos";
                                    var3 = 3;
                                    break label216;
                                 default:
                                    var3 = 0;
                                    var4 = null;
                                 }
                              } else {
                                 var4 = "atan";
                                 var3 = 5;
                              }
                           } else {
                              var4 = "asin";
                              var3 = 4;
                           }
                        } else {
                           var4 = "sqrt";
                           var3 = 18;
                        }
                     } else {
                        char var33 = var1.charAt(0);
                        if (var33 == 'c') {
                           char var36 = var1.charAt(2);
                           var3 = 0;
                           var4 = null;
                           if (var36 == 's') {
                              char var37 = var1.charAt(3);
                              var3 = 0;
                              var4 = null;
                              if (var37 == 'h') {
                                 return 21;
                              }
                           }
                        } else {
                           var3 = 0;
                           var4 = null;
                           if (var33 == 'l') {
                              char var34 = var1.charAt(2);
                              var3 = 0;
                              var4 = null;
                              if (var34 == 'g') {
                                 char var35 = var1.charAt(3);
                                 var3 = 0;
                                 var4 = null;
                                 if (var35 == '2') {
                                    return 34;
                                 }
                              }
                           }
                        }
                     }
                  } else {
                     var4 = "imul";
                     var3 = 28;
                  }
               } else {
                  char var28 = var1.charAt(3);
                  if (var28 == 'h') {
                     char var31 = var1.charAt(0);
                     var3 = 0;
                     var4 = null;
                     if (var31 == 's') {
                        char var32 = var1.charAt(2);
                        var3 = 0;
                        var4 = null;
                        if (var32 == 'n') {
                           return 26;
                        }
                     }
                  } else {
                     var3 = 0;
                     var4 = null;
                     if (var28 == 'n') {
                        char var29 = var1.charAt(0);
                        var3 = 0;
                        var4 = null;
                        if (var29 == 's') {
                           char var30 = var1.charAt(2);
                           var3 = 0;
                           var4 = null;
                           if (var30 == 'g') {
                              return 33;
                           }
                        }
                     }
                  }
               }
            } else {
               var4 = "ceil";
               var3 = 7;
            }
         } else {
            var4 = "LN10";
            var3 = 39;
         }
         break;
      case 5:
         char var38 = var1.charAt(0);
         if (var38 != 'L') {
            if (var38 != 'S') {
               if (var38 != 'a') {
                  if (var38 != 'c') {
                     if (var38 != 'h') {
                        if (var38 != 'l') {
                           if (var38 != 'r') {
                              if (var38 != 't') {
                                 if (var38 != 'e') {
                                    if (var38 != 'f') {
                                       var3 = 0;
                                       var4 = null;
                                    } else {
                                       var4 = "floor";
                                       var3 = 10;
                                    }
                                 } else {
                                    var4 = "expm1";
                                    var3 = 22;
                                 }
                              } else {
                                 var4 = "trunc";
                                 var3 = 29;
                              }
                           } else {
                              var4 = "round";
                              var3 = 16;
                           }
                        } else {
                           char var45 = var1.charAt(4);
                           if (var45 == '0') {
                              var4 = "log10";
                              var3 = 25;
                           } else {
                              var3 = 0;
                              var4 = null;
                              if (var45 == 'p') {
                                 var4 = "log1p";
                                 var3 = 24;
                              }
                           }
                        }
                     } else {
                        var4 = "hypot";
                        var3 = 23;
                     }
                  } else {
                     var4 = "clz32";
                     var3 = 36;
                  }
               } else {
                  char var39 = var1.charAt(1);
                  if (var39 == 'c') {
                     var4 = "acosh";
                     var3 = 30;
                  } else if (var39 == 's') {
                     var4 = "asinh";
                     var3 = 31;
                  } else {
                     var3 = 0;
                     var4 = null;
                     if (var39 == 't') {
                        char var40 = var1.charAt(4);
                        if (var40 == '2') {
                           char var43 = var1.charAt(2);
                           var3 = 0;
                           var4 = null;
                           if (var43 == 'a') {
                              char var44 = var1.charAt(3);
                              var3 = 0;
                              var4 = null;
                              if (var44 == 'n') {
                                 return 6;
                              }
                           }
                        } else {
                           var3 = 0;
                           var4 = null;
                           if (var40 == 'h') {
                              char var41 = var1.charAt(2);
                              var3 = 0;
                              var4 = null;
                              if (var41 == 'a') {
                                 char var42 = var1.charAt(3);
                                 var3 = 0;
                                 var4 = null;
                                 if (var42 == 'n') {
                                    return 32;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               var4 = "SQRT2";
               var3 = 44;
            }
         } else {
            var4 = "LOG2E";
            var3 = 41;
         }
         break;
      case 6:
         char var46 = var1.charAt(0);
         if (var46 == 'L') {
            var4 = "LOG10E";
            var3 = 42;
         } else if (var46 == 'f') {
            var4 = "fround";
            var3 = 35;
         } else {
            var3 = 0;
            var4 = null;
            if (var46 == 'r') {
               var4 = "random";
               var3 = 15;
            }
         }
         break;
      case 7:
         var4 = "SQRT1_2";
         var3 = 43;
         break;
      case 8:
         var4 = "toSource";
         var3 = 1;
         break;
      default:
         var3 = 0;
         var4 = null;
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Math";
   }

   protected void initPrototypeId(int var1) {
      if (var1 <= 36) {
         byte var5;
         String var6;
         switch(var1) {
         case 1:
            var5 = 0;
            var6 = "toSource";
            break;
         case 2:
            var5 = 1;
            var6 = "abs";
            break;
         case 3:
            var5 = 1;
            var6 = "acos";
            break;
         case 4:
            var5 = 1;
            var6 = "asin";
            break;
         case 5:
            var5 = 1;
            var6 = "atan";
            break;
         case 6:
            var5 = 2;
            var6 = "atan2";
            break;
         case 7:
            var5 = 1;
            var6 = "ceil";
            break;
         case 8:
            var5 = 1;
            var6 = "cos";
            break;
         case 9:
            var5 = 1;
            var6 = "exp";
            break;
         case 10:
            var5 = 1;
            var6 = "floor";
            break;
         case 11:
            var5 = 1;
            var6 = "log";
            break;
         case 12:
            var5 = 2;
            var6 = "max";
            break;
         case 13:
            var5 = 2;
            var6 = "min";
            break;
         case 14:
            var5 = 2;
            var6 = "pow";
            break;
         case 15:
            var6 = "random";
            var5 = 0;
            break;
         case 16:
            var5 = 1;
            var6 = "round";
            break;
         case 17:
            var5 = 1;
            var6 = "sin";
            break;
         case 18:
            var5 = 1;
            var6 = "sqrt";
            break;
         case 19:
            var5 = 1;
            var6 = "tan";
            break;
         case 20:
            var5 = 1;
            var6 = "cbrt";
            break;
         case 21:
            var5 = 1;
            var6 = "cosh";
            break;
         case 22:
            var5 = 1;
            var6 = "expm1";
            break;
         case 23:
            var5 = 2;
            var6 = "hypot";
            break;
         case 24:
            var5 = 1;
            var6 = "log1p";
            break;
         case 25:
            var5 = 1;
            var6 = "log10";
            break;
         case 26:
            var5 = 1;
            var6 = "sinh";
            break;
         case 27:
            var5 = 1;
            var6 = "tanh";
            break;
         case 28:
            var5 = 2;
            var6 = "imul";
            break;
         case 29:
            var5 = 1;
            var6 = "trunc";
            break;
         case 30:
            var5 = 1;
            var6 = "acosh";
            break;
         case 31:
            var5 = 1;
            var6 = "asinh";
            break;
         case 32:
            var5 = 1;
            var6 = "atanh";
            break;
         case 33:
            var5 = 1;
            var6 = "sign";
            break;
         case 34:
            var5 = 1;
            var6 = "log2";
            break;
         case 35:
            var5 = 1;
            var6 = "fround";
            break;
         case 36:
            var5 = 1;
            var6 = "clz32";
            break;
         default:
            throw new IllegalStateException(String.valueOf(var1));
         }

         this.initPrototypeMethod(MATH_TAG, var1, var6, var5);
      } else {
         double var2;
         String var4;
         switch(var1) {
         case 37:
            var2 = 2.718281828459045D;
            var4 = "E";
            break;
         case 38:
            var2 = 3.141592653589793D;
            var4 = "PI";
            break;
         case 39:
            var2 = 2.302585092994046D;
            var4 = "LN10";
            break;
         case 40:
            var2 = 0.6931471805599453D;
            var4 = "LN2";
            break;
         case 41:
            var2 = 1.4426950408889634D;
            var4 = "LOG2E";
            break;
         case 42:
            var2 = 0.4342944819032518D;
            var4 = "LOG10E";
            break;
         case 43:
            var2 = 0.7071067811865476D;
            var4 = "SQRT1_2";
            break;
         case 44:
            var2 = 1.4142135623730951D;
            var4 = "SQRT2";
            break;
         default:
            throw new IllegalStateException(String.valueOf(var1));
         }

         this.initPrototypeValue(var1, var4, ScriptRuntime.wrapNumber(var2), 7);
      }
   }
}
