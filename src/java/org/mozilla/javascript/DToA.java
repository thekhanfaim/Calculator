package org.mozilla.javascript;

import java.math.BigInteger;

class DToA {
   private static final int Bias = 1023;
   private static final int Bletch = 16;
   private static final int Bndry_mask = 1048575;
   static final int DTOSTR_EXPONENTIAL = 3;
   static final int DTOSTR_FIXED = 2;
   static final int DTOSTR_PRECISION = 4;
   static final int DTOSTR_STANDARD = 0;
   static final int DTOSTR_STANDARD_EXPONENTIAL = 1;
   private static final int Exp_11 = 1072693248;
   private static final int Exp_mask = 2146435072;
   private static final int Exp_mask_shifted = 2047;
   private static final int Exp_msk1 = 1048576;
   private static final long Exp_msk1L = 4503599627370496L;
   private static final int Exp_shift = 20;
   private static final int Exp_shift1 = 20;
   private static final int Exp_shiftL = 52;
   private static final int Frac_mask = 1048575;
   private static final int Frac_mask1 = 1048575;
   private static final long Frac_maskL = 4503599627370495L;
   private static final int Int_max = 14;
   private static final int Log2P = 1;
   private static final int P = 53;
   private static final int Quick_max = 14;
   private static final int Sign_bit = Integer.MIN_VALUE;
   private static final int Ten_pmax = 22;
   private static final double[] bigtens = new double[]{1.0E16D, 1.0E32D, 1.0E64D, 1.0E128D, 1.0E256D};
   private static final int[] dtoaModes = new int[]{0, 0, 3, 2, 2};
   private static final int n_bigtens = 5;
   private static final double[] tens = new double[]{1.0D, 10.0D, 100.0D, 1000.0D, 10000.0D, 100000.0D, 1000000.0D, 1.0E7D, 1.0E8D, 1.0E9D, 1.0E10D, 1.0E11D, 1.0E12D, 1.0E13D, 1.0E14D, 1.0E15D, 1.0E16D, 1.0E17D, 1.0E18D, 1.0E19D, 1.0E20D, 1.0E21D, 1.0E22D};

   private static char BASEDIGIT(int var0) {
      int var1;
      if (var0 >= 10) {
         var1 = var0 + 87;
      } else {
         var1 = var0 + 48;
      }

      return (char)var1;
   }

   static int JS_dtoa(double var0, int var2, boolean var3, int var4, boolean[] var5, StringBuilder var6) {
      int var7 = var2;
      int[] var8 = new int[1];
      int[] var9 = new int[1];
      double var10;
      if ((Integer.MIN_VALUE & word0(var0)) != 0) {
         var5[0] = true;
         var10 = setWord0(var0, Integer.MAX_VALUE & word0(var0));
      } else {
         var10 = var0;
         var5[0] = false;
      }

      if ((2146435072 & word0(var10)) == 2146435072) {
         String var227;
         if (word1(var10) == 0 && (1048575 & word0(var10)) == 0) {
            var227 = "Infinity";
         } else {
            var227 = "NaN";
         }

         var6.append(var227);
         return 9999;
      } else if (var10 == 0.0D) {
         var6.setLength(0);
         var6.append('0');
         return 1;
      } else {
         BigInteger var12 = d2b(var10, var8, var9);
         int var13 = 2047 & word0(var10) >>> 20;
         int[] var15;
         double var18;
         int var20;
         boolean var21;
         if (var13 != 0) {
            double var224 = setWord0(var10, 1072693248 | 1048575 & word0(var10));
            var20 = var13 - 1023;
            var18 = var224;
            var15 = var8;
            var21 = false;
         } else {
            int var14 = 1074 + var9[0] + var8[0];
            long var16;
            if (var14 > 32) {
               int var223 = word0(var10);
               var15 = var8;
               var16 = (long)var223 << 64 - var14 | (long)(word1(var10) >>> var14 - 32);
            } else {
               var15 = var8;
               var16 = (long)word1(var10) << 32 - var14;
            }

            var18 = setWord0((double)var16, word0((double)var16) - 32505856);
            var20 = var14 - 1075;
            var21 = true;
         }

         double var22 = 0.1760912590558D + 0.289529654602168D * (var18 - 1.5D);
         double var24 = (double)var20;
         Double.isNaN(var24);
         double var27 = var22 + var24 * 0.301029995663981D;
         int var29 = (int)var27;
         if (var27 < 0.0D && var27 != (double)var29) {
            --var29;
         }

         boolean var30 = true;
         if (var29 >= 0 && var29 <= 22) {
            if (var10 < tens[var29]) {
               --var29;
            }

            var30 = false;
         }

         int var31 = -1 + (var9[0] - var20);
         int var32;
         int var33;
         if (var31 >= 0) {
            var33 = var31;
            var32 = 0;
         } else {
            var32 = -var31;
            var33 = 0;
         }

         int var34;
         int var35;
         if (var29 >= 0) {
            var33 += var29;
            var35 = var29;
            var34 = 0;
         } else {
            var32 -= var29;
            var34 = -var29;
            var35 = 0;
         }

         double var36;
         label581: {
            if (var2 >= 0) {
               var36 = var18;
               if (var2 <= 9) {
                  break label581;
               }
            } else {
               var36 = var18;
            }

            var7 = 0;
         }

         boolean var38 = true;
         if (var7 > 5) {
            var7 -= 4;
            var38 = false;
         }

         boolean var39 = true;
         int var40;
         int var41;
         int var42;
         int var43;
         if (var7 != 0 && var7 != 1) {
            label620: {
               if (var7 != 2) {
                  label568: {
                     if (var7 != 3) {
                        if (var7 == 4) {
                           break label568;
                        }

                        if (var7 != 5) {
                           var41 = var4;
                           var42 = var20;
                           var43 = 0;
                           var40 = 0;
                           break label620;
                        }
                     } else {
                        var39 = false;
                     }

                     int var221 = 1 + var4 + var29;
                     var40 = var221 - 1;
                     if (var221 <= 0) {
                        var41 = var4;
                        var42 = 1;
                        var43 = var221;
                     } else {
                        var41 = var4;
                        var42 = var221;
                        var43 = var221;
                     }
                     break label620;
                  }
               } else {
                  var39 = false;
               }

               if (var4 <= 0) {
                  var41 = 1;
               } else {
                  var41 = var4;
               }

               var40 = var41;
               var42 = var41;
               var43 = var41;
            }
         } else {
            var40 = -1;
            var41 = 0;
            var42 = 18;
            var43 = -1;
         }

         boolean var44 = false;
         int var45;
         int[] var46;
         int var48;
         BigInteger var49;
         boolean var50;
         int var51;
         int var52;
         double var53;
         if (var43 >= 0 && var43 <= 14 && var38) {
            double var160 = var10;
            int var162 = var29;
            int var163 = var43;
            int var164 = 2;
            if (var29 > 0) {
               double var214 = tens[var29 & 15];
               int var216 = var29 >> 4;
               int var217 = var216 & 16;
               var52 = 0;
               if (var217 != 0) {
                  var216 &= 15;
                  var10 /= bigtens[4];
                  ++var164;
               }

               while(var216 != 0) {
                  if ((var216 & 1) != 0) {
                     ++var164;
                     var214 *= bigtens[var52];
                  }

                  var216 >>= 1;
                  ++var52;
               }

               var10 /= var214;
            } else {
               int var166 = -var29;
               var52 = 0;
               if (var166 != 0) {
                  var10 *= tens[var166 & 15];

                  for(int var212 = var166 >> 4; var212 != 0; ++var52) {
                     if ((var212 & 1) != 0) {
                        ++var164;
                        var10 *= bigtens[var52];
                     }

                     var212 >>= 1;
                  }
               }
            }

            int var167;
            if (var30 && var10 < 1.0D && var43 > 0) {
               if (var40 <= 0) {
                  var44 = true;
                  var167 = var164;
               } else {
                  --var29;
                  var10 *= 10.0D;
                  int var211 = var164 + 1;
                  var43 = var40;
                  var167 = var211;
                  var44 = false;
               }
            } else {
               var167 = var164;
            }

            var45 = var35;
            var46 = var9;
            double var168 = (double)var167;
            Double.isNaN(var168);
            double var171 = 7.0D + var168 * var10;
            int var173 = word0(var171);
            double var175 = setWord0(var171, var173 - 54525952);
            boolean var177;
            if (var43 == 0) {
               var10 -= 5.0D;
               if (var10 > var175) {
                  var6.append('1');
                  return 1 + var29 + 1;
               }

               var177 = var30;
               var51 = var32;
               if (var10 < -var175) {
                  var6.setLength(0);
                  var6.append('0');
                  return 1;
               }

               var44 = true;
            } else {
               var177 = var30;
               var51 = var32;
            }

            int var178;
            if (var44) {
               var50 = var177;
               var48 = var41;
               var49 = var12;
               var178 = var29;
            } else if (var39) {
               double var191 = 0.5D / tens[var43 - 1] - var175;
               double var193 = var10;
               int var195 = 0;
               double var196 = var191;

               while(true) {
                  var49 = var12;
                  long var198 = (long)var193;
                  var50 = var177;
                  var48 = var41;
                  double var200 = (double)var198;
                  Double.isNaN(var200);
                  double var203 = var193 - var200;
                  var6.append((char)((int)(var198 + 48L)));
                  if (var203 < var196) {
                     return var29 + 1;
                  }

                  if (1.0D - var203 < var196) {
                     char var206;
                     while(true) {
                        var206 = var6.charAt(var6.length() - 1);
                        var6.setLength(var6.length() - 1);
                        if (var206 != '9') {
                           break;
                        }

                        if (var6.length() == 0) {
                           ++var29;
                           var206 = '0';
                           break;
                        }
                     }

                     var6.append((char)(var206 + 1));
                     return var29 + 1;
                  }

                  ++var195;
                  if (var195 >= var43) {
                     var52 = var195;
                     var178 = var29;
                     var10 = var203;
                     var44 = true;
                     break;
                  }

                  var196 *= 10.0D;
                  var193 = var203 * 10.0D;
                  var12 = var12;
                  var41 = var41;
                  var177 = var177;
               }
            } else {
               var50 = var177;
               var48 = var41;
               var49 = var12;
               double var179 = var175 * tens[var43 - 1];
               int var181 = 1;

               while(true) {
                  long var182 = (long)var10;
                  var178 = var29;
                  double var184 = (double)var182;
                  Double.isNaN(var184);
                  var10 -= var184;
                  var6.append((char)((int)(var182 + 48L)));
                  if (var181 == var43) {
                     if (var10 > var179 + 0.5D) {
                        char var188;
                        int var189;
                        while(true) {
                           var188 = var6.charAt(var6.length() - 1);
                           var6.setLength(var6.length() - 1);
                           if (var188 != '9') {
                              var189 = var29;
                              break;
                           }

                           if (var6.length() == 0) {
                              var189 = var29 + 1;
                              var188 = '0';
                              break;
                           }
                        }

                        var6.append((char)(var188 + 1));
                        return var189 + 1;
                     }

                     if (var10 < 0.5D - var179) {
                        stripTrailingZeroes(var6);
                        return var29 + 1;
                     }

                     var52 = var181;
                     var44 = true;
                     break;
                  }

                  ++var181;
                  var10 *= 10.0D;
                  var29 = var29;
               }
            }

            if (var44) {
               var6.setLength(0);
               var10 = var160;
               var29 = var162;
               var43 = var163;
               var53 = var160;
            } else {
               var29 = var178;
               var53 = var160;
            }
         } else {
            var45 = var35;
            var46 = var9;
            var48 = var41;
            var49 = var12;
            var50 = var30;
            var51 = var32;
            var52 = var42;
            var53 = var36;
         }

         if (var15[0] >= 0 && var29 <= 14) {
            double var139 = tens[var29];
            if (var48 < 0 && var43 <= 0) {
               if (var43 < 0 || var10 < var139 * 5.0D || !var3 && var10 == 5.0D * var139) {
                  var6.setLength(0);
                  var6.append('0');
                  return 1;
               } else {
                  var6.append('1');
                  return 1 + var29 + 1;
               }
            } else {
               int var141 = 1;

               while(true) {
                  long var142 = (long)(var10 / var139);
                  double var146 = (double)var142;
                  Double.isNaN(var146);
                  double var149 = var10 - var146 * var139;
                  var6.append((char)((int)(var142 + 48L)));
                  if (var141 == var43) {
                     double var154 = var149 + var149;
                     if (var154 > var139 || var154 == var139 && ((1L & var142) != 0L || var3)) {
                        char var156;
                        while(true) {
                           var156 = var6.charAt(var6.length() - 1);
                           var6.setLength(var6.length() - 1);
                           if (var156 != '9') {
                              break;
                           }

                           if (var6.length() == 0) {
                              ++var29;
                              var156 = '0';
                              break;
                           }
                        }

                        var6.append((char)(var156 + 1));
                     }
                     break;
                  }

                  var10 = var149 * 10.0D;
                  if (var10 == 0.0D) {
                     break;
                  }

                  ++var141;
                  var53 = var53;
               }

               return var29 + 1;
            }
         } else {
            int var57 = var51;
            int var58 = var34;
            BigInteger var59 = null;
            int var60;
            int var61;
            int var62;
            if (var39) {
               if (var7 < 2) {
                  int var138;
                  if (var21) {
                     var138 = 1075 + var15[0];
                  } else {
                     var138 = 54 - var46[0];
                  }

                  var52 = var138;
                  var61 = var45;
               } else {
                  int var132 = var43 - 1;
                  if (var34 >= var132) {
                     var58 = var34 - var132;
                     var61 = var45;
                  } else {
                     int var133 = var132 - var34;
                     var61 = var45 + var133;
                     var34 += var133;
                     var58 = 0;
                  }

                  if (var43 < 0) {
                     var57 = var51 - var43;
                     var52 = 0;
                  } else {
                     var52 = var43;
                  }
               }

               var62 = var51 + var52;
               int var136 = var33 + var52;
               var59 = BigInteger.valueOf(1L);
               var60 = var136;
            } else {
               var60 = var33;
               var61 = var45;
               var62 = var51;
            }

            if (var57 > 0 && var60 > 0) {
               int var131;
               if (var57 < var60) {
                  var131 = var57;
               } else {
                  var131 = var60;
               }

               var52 = var131;
               var62 -= var131;
               var57 -= var131;
               var60 -= var131;
            }

            BigInteger var64;
            if (var34 > 0) {
               if (var39) {
                  if (var58 > 0) {
                     var59 = pow5mult(var59, var58);
                     var64 = var59.multiply(var49);
                  } else {
                     var64 = var49;
                  }

                  int var128 = var34 - var58;
                  if (var128 != 0) {
                     var64 = pow5mult(var64, var128);
                  }
               } else {
                  var64 = pow5mult(var49, var34);
               }
            } else {
               var64 = var49;
            }

            BigInteger var65 = BigInteger.valueOf(1L);
            if (var61 > 0) {
               var65 = pow5mult(var65, var61);
            }

            boolean var67 = false;
            if (var7 < 2) {
               int var124 = word1(var10);
               var67 = false;
               if (var124 == 0) {
                  int var125 = 1048575 & word0(var10);
                  var67 = false;
                  if (var125 == 0) {
                     int var126 = 2145386496 & word0(var10);
                     var67 = false;
                     if (var126 != 0) {
                        ++var62;
                        ++var60;
                        var67 = true;
                     }
                  }
               }
            }

            byte[] var68 = var65.toByteArray();
            int var69 = 0;

            for(int var71 = 0; var71 < 4; var52 = var52) {
               int var122 = var69 << 8;
               if (var71 < var68.length) {
                  var69 = var122 | 255 & var68[var71];
               } else {
                  var69 = var122;
               }

               ++var71;
               var43 = var43;
            }

            int var74;
            if (var61 != 0) {
               var74 = 32 - hi0bits(var69);
            } else {
               var74 = 1;
            }

            int var75 = 31 & var74 + var60;
            int var76 = var75;
            if (var75 != 0) {
               var76 = 32 - var75;
            }

            if (var76 > 4) {
               int var121 = var76 - 4;
               var62 += var121;
               var57 += var121;
               var60 += var121;
            } else if (var76 < 4) {
               int var120 = var76 + 28;
               var62 += var120;
               var57 += var120;
               var60 += var120;
            }

            if (var62 > 0) {
               var64 = var64.shiftLeft(var62);
            }

            if (var60 > 0) {
               var65 = var65.shiftLeft(var60);
            }

            int var77;
            if (var50 && var64.compareTo(var65) < 0) {
               --var29;
               var64 = var64.multiply(BigInteger.valueOf(10L));
               if (var39) {
                  var59 = var59.multiply(BigInteger.valueOf(10L));
               }

               var77 = var40;
            } else {
               var77 = var43;
            }

            if (var77 <= 0 && var7 > 2) {
               if (var77 >= 0) {
                  int var118 = var64.compareTo(var65.multiply(BigInteger.valueOf(5L)));
                  if (var118 >= 0 && (var118 != 0 || var3)) {
                     var6.append('1');
                     return 1 + var29 + 1;
                  }
               }

               var6.setLength(0);
               var6.append('0');
               return 1;
            } else {
               BigInteger var82;
               char var85;
               if (!var39) {
                  int var80 = 1;

                  while(true) {
                     BigInteger[] var81 = var64.divideAndRemainder(var65);
                     var82 = var81[1];
                     char var83 = (char)(48 + var81[0].intValue());
                     var6.append(var83);
                     if (var80 >= var77) {
                        var85 = var83;
                        break;
                     }

                     var64 = var82.multiply(BigInteger.valueOf(10L));
                     ++var80;
                  }
               } else {
                  if (var57 > 0) {
                     var59 = var59.shiftLeft(var57);
                  }

                  BigInteger var89 = var59;
                  byte var90;
                  if (var67) {
                     var90 = 1;
                     var59 = var59.shiftLeft(var90);
                  } else {
                     var90 = 1;
                  }

                  int var91 = 1;

                  while(true) {
                     BigInteger[] var92 = var64.divideAndRemainder(var65);
                     var82 = var92[var90];
                     var85 = (char)(48 + var92[0].intValue());
                     int var93 = var82.compareTo(var89);
                     BigInteger var95 = var65.subtract(var59);
                     int var96;
                     if (var95.signum() <= 0) {
                        var96 = 1;
                     } else {
                        var96 = var82.compareTo(var95);
                     }

                     if (var96 == 0 && var7 == 0 && (1 & word1(var10)) == 0) {
                        if (var85 == '9') {
                           var6.append('9');
                           if (roundOff(var6)) {
                              ++var29;
                              var6.append('1');
                           }

                           return var29 + 1;
                        }

                        if (var93 > 0) {
                           ++var85;
                        }

                        var6.append(var85);
                        return var29 + 1;
                     }

                     if (var93 < 0 || var93 == 0 && var7 == 0 && (1 & word1(var10)) == 0) {
                        if (var96 > 0) {
                           label611: {
                              int var99 = var82.shiftLeft(1).compareTo(var65);
                              if (var99 <= 0) {
                                 if (var99 != 0) {
                                    break label611;
                                 }

                                 if ((var85 & 1) != 1 && !var3) {
                                    break label611;
                                 }
                              }

                              char var101 = (char)(var85 + 1);
                              if (var85 == '9') {
                                 var6.append('9');
                                 if (roundOff(var6)) {
                                    ++var29;
                                    var6.append('1');
                                 }

                                 return var29 + 1;
                              }

                              var85 = var101;
                           }
                        }

                        var6.append(var85);
                        return var29 + 1;
                     }

                     if (var96 > 0) {
                        if (var85 == '9') {
                           var6.append('9');
                           if (roundOff(var6)) {
                              ++var29;
                              var6.append('1');
                           }

                           return var29 + 1;
                        }

                        var6.append((char)(var85 + 1));
                        return var29 + 1;
                     }

                     var6.append(var85);
                     if (var91 == var77) {
                        break;
                     }

                     var64 = var82.multiply(BigInteger.valueOf(10L));
                     if (var89 == var59) {
                        BigInteger var108 = var59.multiply(BigInteger.valueOf(10L));
                        var59 = var108;
                        var89 = var108;
                     } else {
                        BigInteger var107 = var89.multiply(BigInteger.valueOf(10L));
                        var59 = var59.multiply(BigInteger.valueOf(10L));
                        var89 = var107;
                     }

                     ++var91;
                     var57 = var57;
                     var90 = 1;
                  }
               }

               int var86 = var82.shiftLeft(1).compareTo(var65);
               if (var86 > 0 || var86 == 0 && ((var85 & 1) == 1 || var3)) {
                  if (roundOff(var6)) {
                     int var87 = var29 + 1;
                     var6.append('1');
                     return var87 + 1;
                  }
               } else {
                  stripTrailingZeroes(var6);
               }

               return var29 + 1;
            }
         }
      }
   }

   static String JS_dtobasestr(int var0, double var1) {
      if (2 <= var0 && var0 <= 36) {
         if (Double.isNaN(var1)) {
            return "NaN";
         } else if (Double.isInfinite(var1)) {
            return var1 > 0.0D ? "Infinity" : "-Infinity";
         } else if (var1 == 0.0D) {
            return "0";
         } else {
            boolean var9;
            double var10;
            if (var1 >= 0.0D) {
               var10 = var1;
               var9 = false;
            } else {
               double var7 = -var1;
               var9 = true;
               var10 = var7;
            }

            double var12 = Math.floor(var10);
            long var14 = (long)var12;
            String var23;
            if ((double)var14 == var12) {
               long var64;
               if (var9) {
                  var64 = -var14;
               } else {
                  var64 = var14;
               }

               var23 = Long.toString(var64, var0);
            } else {
               long var16 = Double.doubleToLongBits(var12);
               int var18 = 2047 & (int)(var16 >> 52);
               long var19;
               if (var18 == 0) {
                  var19 = (4503599627370495L & var16) << 1;
               } else {
                  var19 = 4503599627370496L | 4503599627370495L & var16;
               }

               if (var9) {
                  var19 = -var19;
               }

               int var21 = var18 - 1075;
               BigInteger var22 = BigInteger.valueOf(var19);
               if (var21 > 0) {
                  var22 = var22.shiftLeft(var21);
               } else if (var21 < 0) {
                  var22 = var22.shiftRight(-var21);
               }

               var23 = var22.toString(var0);
            }

            if (var10 == var12) {
               return var23;
            } else {
               StringBuilder var24 = new StringBuilder();
               var24.append(var23);
               var24.append('.');
               double var27 = var10 - var12;
               long var29 = Double.doubleToLongBits(var10);
               StringBuilder var31 = var24;
               int var32 = (int)(var29 >> 32);
               int var33 = (int)var29;
               int[] var34 = new int[1];
               BigInteger var35 = d2b(var27, var34, new int[1]);
               int var36 = -(2047 & var32 >>> 20);
               if (var36 == 0) {
                  var36 = -1;
               }

               int var37 = var36 + 1076;
               BigInteger var38 = BigInteger.valueOf(1L);
               BigInteger var39 = var38;
               if (var33 == 0 && (var32 & 1048575) == 0 && (var32 & 2145386496) != 0) {
                  ++var37;
                  var39 = BigInteger.valueOf(2L);
               }

               BigInteger var40 = var35.shiftLeft(var37 + var34[0]);
               BigInteger var41 = BigInteger.valueOf(1L).shiftLeft(var37);
               BigInteger var43 = BigInteger.valueOf((long)var0);
               boolean var44 = false;
               BigInteger var45 = var40;
               BigInteger var48 = var38;
               BigInteger var49 = var39;

               while(true) {
                  BigInteger[] var50 = var45.multiply(var43).divideAndRemainder(var41);
                  BigInteger var51 = var50[1];
                  BigInteger var52 = var50[0];
                  int var54 = (char)var52.intValue();
                  if (var48 == var49) {
                     BigInteger var63 = var48.multiply(var43);
                     var49 = var63;
                     var48 = var63;
                  } else {
                     var48 = var48.multiply(var43);
                     var49 = var49.multiply(var43);
                  }

                  int var55 = var51.compareTo(var48);
                  BigInteger var57 = var41.subtract(var49);
                  int var58;
                  if (var57.signum() <= 0) {
                     var58 = 1;
                  } else {
                     var58 = var51.compareTo(var57);
                  }

                  if (var58 == 0 && (var33 & 1) == 0) {
                     if (var55 > 0) {
                        ++var54;
                     }

                     var44 = true;
                  } else if (var55 < 0 || var55 == 0 && (var33 & 1) == 0) {
                     if (var58 > 0) {
                        var51 = var51.shiftLeft(1);
                        if (var51.compareTo(var41) > 0) {
                           ++var54;
                        }
                     }

                     var44 = true;
                  } else if (var58 > 0) {
                     ++var54;
                     var44 = true;
                  }

                  char var59 = BASEDIGIT(var54);
                  var31.append(var59);
                  if (var44) {
                     return var31.toString();
                  }

                  var31 = var31;
                  var9 = var9;
                  var43 = var43;
                  var45 = var51;
               }
            }
         }
      } else {
         StringBuilder var3 = new StringBuilder();
         var3.append("Bad base: ");
         var3.append(var0);
         IllegalArgumentException var6 = new IllegalArgumentException(var3.toString());
         throw var6;
      }
   }

   static void JS_dtostr(StringBuilder var0, int var1, int var2, double var3) {
      boolean[] var5 = new boolean[1];
      if (var1 == 2 && (var3 >= 1.0E21D || var3 <= -1.0E21D)) {
         var1 = 0;
      }

      int var6 = dtoaModes[var1];
      boolean var7;
      if (var1 >= 2) {
         var7 = true;
      } else {
         var7 = false;
      }

      int var8 = JS_dtoa(var3, var6, var7, var2, var5, var0);
      int var9 = var0.length();
      if (var8 != 9999) {
         int var11 = 0;
         boolean var12;
         if (var1 != 0) {
            label103: {
               var11 = 0;
               if (var1 != 1) {
                  if (var1 == 2) {
                     if (var2 >= 0) {
                        var11 = var8 + var2;
                        var12 = false;
                     } else {
                        var11 = var8;
                        var12 = false;
                     }
                     break label103;
                  }

                  if (var1 != 3) {
                     if (var1 != 4) {
                        var12 = false;
                        var11 = 0;
                        break label103;
                     }

                     var11 = var2;
                     if (var8 >= -5) {
                        var12 = false;
                        if (var8 <= var2) {
                           break label103;
                        }
                     }

                     var12 = true;
                     break label103;
                  }

                  var11 = var2;
               }

               var12 = true;
            }
         } else if (var8 >= -5 && var8 <= 21) {
            var11 = var8;
            var12 = false;
         } else {
            var12 = true;
         }

         if (var9 < var11) {
            int var21 = var11;

            do {
               var0.append('0');
            } while(var0.length() != var21);

            var9 = var11;
         }

         if (var12) {
            if (var9 != 1) {
               var0.insert(1, '.');
            }

            var0.append('e');
            if (var8 - 1 >= 0) {
               var0.append('+');
            }

            var0.append(var8 - 1);
         } else if (var8 != var9) {
            if (var8 > 0) {
               var0.insert(var8, '.');
            } else {
               for(int var13 = 0; var13 < 1 - var8; ++var13) {
                  var0.insert(0, '0');
               }

               var0.insert(1, '.');
            }
         }
      }

      if (var5[0] && (word0(var3) != Integer.MIN_VALUE || word1(var3) != 0) && ((2146435072 & word0(var3)) != 2146435072 || word1(var3) == 0 && (1048575 & word0(var3)) == 0)) {
         var0.insert(0, '-');
      }

   }

   private static BigInteger d2b(double var0, int[] var2, int[] var3) {
      long var4 = Double.doubleToLongBits(var0);
      int var6 = (int)(var4 >>> 32);
      int var7 = (int)var4;
      int var8 = 1048575 & var6;
      int var9 = (var6 & Integer.MAX_VALUE) >>> 20;
      if (var9 != 0) {
         var8 |= 1048576;
      }

      int var12;
      byte var13;
      byte[] var14;
      if (var7 != 0) {
         var14 = new byte[8];
         var12 = lo0bits(var7);
         int var15 = var7 >>> var12;
         if (var12 != 0) {
            stuffBits(var14, 4, var15 | var8 << 32 - var12);
            var8 >>= var12;
         } else {
            stuffBits(var14, 4, var15);
         }

         stuffBits(var14, 0, var8);
         if (var8 != 0) {
            var13 = 2;
         } else {
            var13 = 1;
         }
      } else {
         byte[] var10 = new byte[4];
         int var11 = lo0bits(var8);
         var8 >>>= var11;
         stuffBits(var10, 0, var8);
         var12 = var11 + 32;
         var13 = 1;
         var14 = var10;
      }

      if (var9 != 0) {
         var2[0] = var12 + -52 + (var9 - 1023);
         var3[0] = 53 - var12;
      } else {
         var2[0] = var12 + 1 + -52 + (var9 - 1023);
         var3[0] = var13 * 32 - hi0bits(var8);
      }

      return new BigInteger(var14);
   }

   private static int hi0bits(int var0) {
      int var1 = -65536 & var0;
      int var2 = 0;
      if (var1 == 0) {
         var2 = 16;
         var0 <<= 16;
      }

      if ((-16777216 & var0) == 0) {
         var2 += 8;
         var0 <<= 8;
      }

      if ((-268435456 & var0) == 0) {
         var2 += 4;
         var0 <<= 4;
      }

      if ((-1073741824 & var0) == 0) {
         var2 += 2;
         var0 <<= 2;
      }

      if ((Integer.MIN_VALUE & var0) == 0) {
         ++var2;
         if ((1073741824 & var0) == 0) {
            return 32;
         }
      }

      return var2;
   }

   private static int lo0bits(int var0) {
      int var1 = var0;
      if ((var0 & 7) != 0) {
         if ((var0 & 1) != 0) {
            return 0;
         } else {
            return (var0 & 2) != 0 ? 1 : 2;
         }
      } else {
         int var2 = '\uffff' & var0;
         int var3 = 0;
         if (var2 == 0) {
            var3 = 16;
            var1 = var0 >>> 16;
         }

         if ((var1 & 255) == 0) {
            var3 += 8;
            var1 >>>= 8;
         }

         if ((var1 & 15) == 0) {
            var3 += 4;
            var1 >>>= 4;
         }

         if ((var1 & 3) == 0) {
            var3 += 2;
            var1 >>>= 2;
         }

         if ((var1 & 1) == 0) {
            ++var3;
            if ((1 & var1 >>> 1) == 0) {
               return 32;
            }
         }

         return var3;
      }
   }

   static BigInteger pow5mult(BigInteger var0, int var1) {
      return var0.multiply(BigInteger.valueOf(5L).pow(var1));
   }

   static boolean roundOff(StringBuilder var0) {
      int var1 = var0.length();

      char var2;
      do {
         if (var1 == 0) {
            var0.setLength(0);
            return true;
         }

         --var1;
         var2 = var0.charAt(var1);
      } while(var2 == '9');

      var0.setCharAt(var1, (char)(var2 + 1));
      var0.setLength(var1 + 1);
      return false;
   }

   static double setWord0(double var0, int var2) {
      long var3 = Double.doubleToLongBits(var0);
      return Double.longBitsToDouble((long)var2 << 32 | 4294967295L & var3);
   }

   private static void stripTrailingZeroes(StringBuilder var0) {
      int var1 = var0.length();

      while(true) {
         int var2 = var1 - 1;
         if (var1 <= 0 || var0.charAt(var2) != '0') {
            var0.setLength(var2 + 1);
            return;
         }

         var1 = var2;
      }
   }

   private static void stuffBits(byte[] var0, int var1, int var2) {
      var0[var1] = (byte)(var2 >> 24);
      var0[var1 + 1] = (byte)(var2 >> 16);
      var0[var1 + 2] = (byte)(var2 >> 8);
      var0[var1 + 3] = (byte)var2;
   }

   static int word0(double var0) {
      return (int)(Double.doubleToLongBits(var0) >> 32);
   }

   static int word1(double var0) {
      return (int)Double.doubleToLongBits(var0);
   }
}
