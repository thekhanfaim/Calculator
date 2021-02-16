package org.mozilla.javascript;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

final class NativeDate extends IdScriptableObject {
   // $FF: synthetic field
   static final boolean $assertionsDisabled = false;
   private static final int ConstructorId_UTC = -1;
   private static final int ConstructorId_now = -3;
   private static final int ConstructorId_parse = -2;
   private static final Object DATE_TAG = "Date";
   private static final double HalfTimeDomain = 8.64E15D;
   private static final double HoursPerDay = 24.0D;
   private static final int Id_constructor = 1;
   private static final int Id_getDate = 17;
   private static final int Id_getDay = 19;
   private static final int Id_getFullYear = 13;
   private static final int Id_getHours = 21;
   private static final int Id_getMilliseconds = 27;
   private static final int Id_getMinutes = 23;
   private static final int Id_getMonth = 15;
   private static final int Id_getSeconds = 25;
   private static final int Id_getTime = 11;
   private static final int Id_getTimezoneOffset = 29;
   private static final int Id_getUTCDate = 18;
   private static final int Id_getUTCDay = 20;
   private static final int Id_getUTCFullYear = 14;
   private static final int Id_getUTCHours = 22;
   private static final int Id_getUTCMilliseconds = 28;
   private static final int Id_getUTCMinutes = 24;
   private static final int Id_getUTCMonth = 16;
   private static final int Id_getUTCSeconds = 26;
   private static final int Id_getYear = 12;
   private static final int Id_setDate = 39;
   private static final int Id_setFullYear = 43;
   private static final int Id_setHours = 37;
   private static final int Id_setMilliseconds = 31;
   private static final int Id_setMinutes = 35;
   private static final int Id_setMonth = 41;
   private static final int Id_setSeconds = 33;
   private static final int Id_setTime = 30;
   private static final int Id_setUTCDate = 40;
   private static final int Id_setUTCFullYear = 44;
   private static final int Id_setUTCHours = 38;
   private static final int Id_setUTCMilliseconds = 32;
   private static final int Id_setUTCMinutes = 36;
   private static final int Id_setUTCMonth = 42;
   private static final int Id_setUTCSeconds = 34;
   private static final int Id_setYear = 45;
   private static final int Id_toDateString = 4;
   private static final int Id_toGMTString = 8;
   private static final int Id_toISOString = 46;
   private static final int Id_toJSON = 47;
   private static final int Id_toLocaleDateString = 7;
   private static final int Id_toLocaleString = 5;
   private static final int Id_toLocaleTimeString = 6;
   private static final int Id_toSource = 9;
   private static final int Id_toString = 2;
   private static final int Id_toTimeString = 3;
   private static final int Id_toUTCString = 8;
   private static final int Id_valueOf = 10;
   private static double LocalTZA;
   private static final int MAXARGS = 7;
   private static final int MAX_PROTOTYPE_ID = 47;
   private static final double MinutesPerDay = 1440.0D;
   private static final double MinutesPerHour = 60.0D;
   private static final double SecondsPerDay = 86400.0D;
   private static final double SecondsPerHour = 3600.0D;
   private static final double SecondsPerMinute = 60.0D;
   private static final String js_NaN_date_str = "Invalid Date";
   private static DateFormat localeDateFormatter;
   private static DateFormat localeDateTimeFormatter;
   private static DateFormat localeTimeFormatter;
   private static final double msPerDay = 8.64E7D;
   private static final double msPerHour = 3600000.0D;
   private static final double msPerMinute = 60000.0D;
   private static final double msPerSecond = 1000.0D;
   private static final long serialVersionUID = -8307438915861678966L;
   private static TimeZone thisTimeZone;
   private static DateFormat timeZoneFormatter;
   private double date;

   private NativeDate() {
      if (thisTimeZone == null) {
         TimeZone var1 = TimeZone.getDefault();
         thisTimeZone = var1;
         LocalTZA = (double)var1.getRawOffset();
      }

   }

   private static int DateFromTime(double var0) {
      int var2 = YearFromTime(var0);
      int var3 = -59 + (int)(Day(var0) - DayFromYear((double)var2));
      if (var3 < 0) {
         int var7;
         if (var3 < -28) {
            var7 = 28 + var3 + 31;
         } else {
            var7 = var3 + 28;
         }

         return var7 + 1;
      } else {
         if (IsLeapYear(var2)) {
            if (var3 == 0) {
               return 29;
            }

            --var3;
         }

         byte var4;
         short var5;
         switch(Math.round((float)(var3 / 30))) {
         case 0:
            return var3 + 1;
         case 1:
            var4 = 31;
            var5 = 31;
            break;
         case 2:
            var4 = 30;
            var5 = 61;
            break;
         case 3:
            var4 = 31;
            var5 = 92;
            break;
         case 4:
            var4 = 30;
            var5 = 122;
            break;
         case 5:
            var4 = 31;
            var5 = 153;
            break;
         case 6:
            var4 = 31;
            var5 = 184;
            break;
         case 7:
            var4 = 30;
            var5 = 214;
            break;
         case 8:
            var4 = 31;
            var5 = 245;
            break;
         case 9:
            var4 = 30;
            var5 = 275;
            break;
         case 10:
            return 1 + (var3 - 275);
         default:
            throw Kit.codeBug();
         }

         int var6 = var3 - var5;
         if (var6 < 0) {
            var6 += var4;
         }

         return var6 + 1;
      }
   }

   private static double Day(double var0) {
      return Math.floor(var0 / 8.64E7D);
   }

   private static double DayFromMonth(int var0, int var1) {
      int var2 = var0 * 30;
      int var3;
      if (var0 >= 7) {
         var3 = var2 + -1 + var0 / 2;
      } else if (var0 >= 2) {
         var3 = var2 + -1 + (var0 - 1) / 2;
      } else {
         var3 = var2 + var0;
      }

      if (var0 >= 2 && IsLeapYear(var1)) {
         ++var3;
      }

      return (double)var3;
   }

   private static double DayFromYear(double var0) {
      return 365.0D * (var0 - 1970.0D) + Math.floor((var0 - 1969.0D) / 4.0D) - Math.floor((var0 - 1901.0D) / 100.0D) + Math.floor((var0 - 1601.0D) / 400.0D);
   }

   private static double DaylightSavingTA(double var0) {
      if (var0 < 0.0D) {
         var0 = MakeDate(MakeDay((double)EquivalentYear(YearFromTime(var0)), (double)MonthFromTime(var0), (double)DateFromTime(var0)), TimeWithinDay(var0));
      }

      Date var2 = new Date((long)var0);
      return thisTimeZone.inDaylightTime(var2) ? 3600000.0D : 0.0D;
   }

   private static int DaysInMonth(int var0, int var1) {
      if (var1 == 2) {
         return IsLeapYear(var0) ? 29 : 28;
      } else {
         return var1 >= 8 ? 31 - (var1 & 1) : 30 + (var1 & 1);
      }
   }

   private static double DaysInYear(double var0) {
      if (!Double.isInfinite(var0) && !Double.isNaN(var0)) {
         return IsLeapYear((int)var0) ? 366.0D : 365.0D;
      } else {
         return ScriptRuntime.NaN;
      }
   }

   private static int EquivalentYear(int var0) {
      int var1 = (4 + (int)DayFromYear((double)var0)) % 7;
      if (var1 < 0) {
         var1 += 7;
      }

      if (IsLeapYear(var0)) {
         switch(var1) {
         case 0:
            return 1984;
         case 1:
            return 1996;
         case 2:
            return 1980;
         case 3:
            return 1992;
         case 4:
            return 1976;
         case 5:
            return 1988;
         case 6:
            return 1972;
         }
      } else {
         switch(var1) {
         case 0:
            return 1978;
         case 1:
            return 1973;
         case 2:
            return 1985;
         case 3:
            return 1986;
         case 4:
            return 1981;
         case 5:
            return 1971;
         case 6:
            return 1977;
         }
      }

      throw Kit.codeBug();
   }

   private static int HourFromTime(double var0) {
      double var2 = Math.floor(var0 / 3600000.0D) % 24.0D;
      if (var2 < 0.0D) {
         var2 += 24.0D;
      }

      return (int)var2;
   }

   private static boolean IsLeapYear(int var0) {
      return var0 % 4 == 0 && (var0 % 100 != 0 || var0 % 400 == 0);
   }

   private static double LocalTime(double var0) {
      return var0 + LocalTZA + DaylightSavingTA(var0);
   }

   private static double MakeDate(double var0, double var2) {
      return var2 + 8.64E7D * var0;
   }

   private static double MakeDay(double var0, double var2, double var4) {
      double var6 = var0 + Math.floor(var2 / 12.0D);
      double var8 = var2 % 12.0D;
      if (var8 < 0.0D) {
         var8 += 12.0D;
      }

      return var4 + Math.floor(TimeFromYear(var6) / 8.64E7D) + DayFromMonth((int)var8, (int)var6) - 1.0D;
   }

   private static double MakeTime(double var0, double var2, double var4, double var6) {
      return var6 + 1000.0D * (var4 + 60.0D * (var2 + var0 * 60.0D));
   }

   private static int MinFromTime(double var0) {
      double var2 = Math.floor(var0 / 60000.0D) % 60.0D;
      if (var2 < 0.0D) {
         var2 += 60.0D;
      }

      return (int)var2;
   }

   private static int MonthFromTime(double var0) {
      int var2 = YearFromTime(var0);
      int var3 = -59 + (int)(Day(var0) - DayFromYear((double)var2));
      byte var4 = 1;
      if (var3 < 0) {
         if (var3 < -28) {
            var4 = 0;
         }

         return var4;
      } else {
         if (IsLeapYear(var2)) {
            if (var3 == 0) {
               return var4;
            }

            --var3;
         }

         int var5 = var3 / 30;
         short var6;
         switch(var5) {
         case 0:
            return 2;
         case 1:
            var6 = 31;
            break;
         case 2:
            var6 = 61;
            break;
         case 3:
            var6 = 92;
            break;
         case 4:
            var6 = 122;
            break;
         case 5:
            var6 = 153;
            break;
         case 6:
            var6 = 184;
            break;
         case 7:
            var6 = 214;
            break;
         case 8:
            var6 = 245;
            break;
         case 9:
            var6 = 275;
            break;
         case 10:
            return 11;
         default:
            throw Kit.codeBug();
         }

         return var3 >= var6 ? var5 + 2 : var5 + 1;
      }
   }

   private static int SecFromTime(double var0) {
      double var2 = Math.floor(var0 / 1000.0D) % 60.0D;
      if (var2 < 0.0D) {
         var2 += 60.0D;
      }

      return (int)var2;
   }

   private static double TimeClip(double var0) {
      if (var0 == var0 && var0 != Double.POSITIVE_INFINITY && var0 != Double.NEGATIVE_INFINITY && Math.abs(var0) <= 8.64E15D) {
         return var0 > 0.0D ? Math.floor(0.0D + var0) : Math.ceil(0.0D + var0);
      } else {
         return ScriptRuntime.NaN;
      }
   }

   private static double TimeFromYear(double var0) {
      return 8.64E7D * DayFromYear(var0);
   }

   private static double TimeWithinDay(double var0) {
      double var2 = var0 % 8.64E7D;
      if (var2 < 0.0D) {
         var2 += 8.64E7D;
      }

      return var2;
   }

   private static int WeekDay(double var0) {
      double var2 = (4.0D + Day(var0)) % 7.0D;
      if (var2 < 0.0D) {
         var2 += 7.0D;
      }

      return (int)var2;
   }

   private static int YearFromTime(double var0) {
      if (!Double.isInfinite(var0) && !Double.isNaN(var0)) {
         double var2 = 1970.0D + Math.floor(var0 / 3.1556952E10D);
         double var4 = TimeFromYear(var2);
         if (var4 > var0) {
            --var2;
         } else if (var4 + 8.64E7D * DaysInYear(var2) <= var0) {
            ++var2;
         }

         return (int)var2;
      } else {
         return 0;
      }
   }

   private static void append0PaddedUint(StringBuilder var0, int var1, int var2) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var3 = 1;
      int var4 = var2 - 1;
      if (var1 >= 10) {
         if (var1 < 1000000000) {
            while(true) {
               int var8 = var3 * 10;
               if (var1 < var8) {
                  break;
               }

               --var4;
               var3 = var8;
            }
         } else {
            var4 -= 9;
            var3 = 1000000000;
         }
      }

      while(var4 > 0) {
         var0.append('0');
         --var4;
      }

      while(var3 != 1) {
         var0.append((char)(48 + var1 / var3));
         var1 %= var3;
         var3 /= 10;
      }

      var0.append((char)(var1 + 48));
   }

   private static void appendMonthName(StringBuilder var0, int var1) {
      int var2 = var1 * 3;

      for(int var3 = 0; var3 != 3; ++var3) {
         var0.append("JanFebMarAprMayJunJulAugSepOctNovDec".charAt(var2 + var3));
      }

   }

   private static void appendWeekDayName(StringBuilder var0, int var1) {
      int var2 = var1 * 3;

      for(int var3 = 0; var3 != 3; ++var3) {
         var0.append("SunMonTueWedThuFriSat".charAt(var2 + var3));
      }

   }

   private static String date_format(double param0, int param2) {
      // $FF: Couldn't be decompiled
   }

   private static double date_msecFromArgs(Object[] var0) {
      double[] var1 = new double[7];

      for(int var2 = 0; var2 < 7; ++var2) {
         if (var2 < var0.length) {
            double var3 = ScriptRuntime.toNumber(var0[var2]);
            if (var3 != var3 || Double.isInfinite(var3)) {
               return ScriptRuntime.NaN;
            }

            var1[var2] = ScriptRuntime.toInteger(var0[var2]);
         } else if (var2 == 2) {
            var1[var2] = 1.0D;
         } else {
            var1[var2] = 0.0D;
         }
      }

      if (var1[0] >= 0.0D && var1[0] <= 99.0D) {
         var1[0] += 1900.0D;
      }

      return date_msecFromDate(var1[0], var1[1], var1[2], var1[3], var1[4], var1[5], var1[6]);
   }

   private static double date_msecFromDate(double var0, double var2, double var4, double var6, double var8, double var10, double var12) {
      return MakeDate(MakeDay(var0, var2, var4), MakeTime(var6, var8, var10, var12));
   }

   private static double date_parseString(String var0) {
      double var1 = parseISOString(var0);
      if (var1 == var1) {
         return var1;
      } else {
         int var3 = var0.length();
         boolean var4 = false;
         int var5 = -1;
         int var6 = -1;
         boolean var7 = false;
         int var8 = -1;
         int var9 = 0;
         int var10 = -1;
         int var11 = -1;
         char var12 = 0;
         char var13 = 0;
         int var14 = -1;
         double var15 = -1.0D;

         while(true) {
            while(true) {
               while(true) {
                  while(true) {
                     label242:
                     while(var9 < var3) {
                        char var130 = var0.charAt(var9);
                        ++var9;
                        char var47;
                        double var48;
                        char var50;
                        int var51;
                        if (var130 > ' ' && var130 != ',') {
                           if (var130 != '-') {
                              byte var55 = 40;
                              if (var130 != var55) {
                                 if ('0' > var130 || var130 > '9') {
                                    char var58 = var12;
                                    char var60;
                                    int var61;
                                    if (var130 != '/' && var130 != ':' && var130 != '+') {
                                       if (var130 != '-') {
                                          int var62 = var9 - 1;
                                          int var63 = var9;

                                          while(true) {
                                             if (var63 >= var3) {
                                                break;
                                             }

                                             var130 = var0.charAt(var63);
                                             if (('A' > var130 || var130 > 'Z') && ('a' > var130 || var130 > 'z')) {
                                                break;
                                             }

                                             ++var63;
                                          }

                                          int var65 = var63 - var62;
                                          if (var65 < 2) {
                                             return ScriptRuntime.NaN;
                                          }

                                          String var66 = "am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;";
                                          int var67 = 0;
                                          int var68 = var3;
                                          int var69 = 0;

                                          while(true) {
                                             int var70 = var66.indexOf(59, var69);
                                             if (var70 < 0) {
                                                return ScriptRuntime.NaN;
                                             }

                                             if (var66.regionMatches(true, var69, var0, var62, var65)) {
                                                if (var67 < 2) {
                                                   if (var5 > 12 || var5 < 0) {
                                                      return ScriptRuntime.NaN;
                                                   }

                                                   if (var67 == 0) {
                                                      if (var5 == 12) {
                                                         var5 = 0;
                                                      }
                                                   } else if (var5 != 12) {
                                                      var5 += 12;
                                                   }
                                                } else {
                                                   int var82 = var67 - 2;
                                                   if (var82 >= 7) {
                                                      int var83 = var82 - 7;
                                                      if (var83 < 12) {
                                                         if (var14 >= 0) {
                                                            return ScriptRuntime.NaN;
                                                         }

                                                         var14 = var83;
                                                      } else {
                                                         switch(var83 - 12) {
                                                         case 0:
                                                            var15 = 0.0D;
                                                            break;
                                                         case 1:
                                                            var15 = 0.0D;
                                                            break;
                                                         case 2:
                                                            var15 = 0.0D;
                                                            break;
                                                         case 3:
                                                            var15 = 300.0D;
                                                            break;
                                                         case 4:
                                                            var15 = 240.0D;
                                                            break;
                                                         case 5:
                                                            var15 = 360.0D;
                                                            break;
                                                         case 6:
                                                            var15 = 300.0D;
                                                            break;
                                                         case 7:
                                                            var15 = 420.0D;
                                                            break;
                                                         case 8:
                                                            var15 = 360.0D;
                                                            break;
                                                         case 9:
                                                            var15 = 480.0D;
                                                            break;
                                                         case 10:
                                                            var15 = 420.0D;
                                                            break;
                                                         default:
                                                            Kit.codeBug();
                                                         }
                                                      }
                                                   }
                                                }

                                                var3 = var68;
                                                var12 = var58;
                                                var13 = var13;
                                                var9 = var63;
                                                var1 = var1;
                                                continue label242;
                                             }

                                             int var80 = var70 + 1;
                                             int var81 = var67 + 1;
                                             var69 = var80;
                                             var67 = var81;
                                             var68 = var68;
                                             var65 = var65;
                                             var58 = var58;
                                             var13 = var13;
                                             var66 = var66;
                                             var63 = var63;
                                          }
                                       }

                                       var60 = var13;
                                       var61 = var3;
                                    } else {
                                       var60 = var13;
                                       var61 = var3;
                                    }

                                    var12 = var130;
                                    var3 = var61;
                                    var13 = var60;
                                    var1 = var1;
                                    continue;
                                 }

                                 int var86 = var130 - 48;

                                 double var87;
                                 while(true) {
                                    if (var9 >= var3) {
                                       var87 = var1;
                                       break;
                                    }

                                    char var128 = var0.charAt(var9);
                                    var130 = var128;
                                    var87 = var1;
                                    if ('0' > var128 || var128 > '9') {
                                       break;
                                    }

                                    var86 = -48 + var128 + var86 * 10;
                                    ++var9;
                                    var1 = var1;
                                 }

                                 if (var12 != '+' && var12 != '-') {
                                    if (var86 >= 70 || var12 == '/' && var14 >= 0 && var8 >= 0 && var6 < 0) {
                                       if (var6 >= 0) {
                                          return ScriptRuntime.NaN;
                                       }

                                       if (var130 > ' ' && var130 != ',' && var130 != '/' && var9 < var3) {
                                          return ScriptRuntime.NaN;
                                       }

                                       int var94;
                                       if (var86 < 100) {
                                          var94 = var86 + 1900;
                                       } else {
                                          var94 = var86;
                                       }

                                       var6 = var94;
                                    } else if (var130 == ':') {
                                       if (var5 < 0) {
                                          var5 = var86;
                                       } else {
                                          if (var10 >= 0) {
                                             return ScriptRuntime.NaN;
                                          }

                                          var10 = var86;
                                       }
                                    } else if (var130 == '/') {
                                       if (var14 < 0) {
                                          int var119 = var86 - 1;
                                          var14 = var119;
                                       } else {
                                          if (var8 >= 0) {
                                             return ScriptRuntime.NaN;
                                          }

                                          var8 = var86;
                                       }
                                    } else {
                                       if (var9 < var3 && var130 != ',' && var130 > ' ' && var130 != '-') {
                                          return ScriptRuntime.NaN;
                                       }

                                       if (var4 && var86 < 60) {
                                          if (var15 < 0.0D) {
                                             double var111 = (double)var86;
                                             Double.isNaN(var111);
                                             var15 -= var111;
                                          } else {
                                             double var106 = (double)var86;
                                             Double.isNaN(var106);
                                             var15 += var106;
                                          }
                                       } else if (var5 >= 0 && var10 < 0) {
                                          var10 = var86;
                                       } else if (var10 >= 0 && var11 < 0) {
                                          var11 = var86;
                                       } else {
                                          if (var8 >= 0) {
                                             return ScriptRuntime.NaN;
                                          }

                                          var8 = var86;
                                       }
                                    }
                                 } else {
                                    int var89;
                                    if (var86 < 24) {
                                       var89 = var86 * 60;
                                    } else {
                                       var89 = var86 % 100 + 60 * (var86 / 100);
                                    }

                                    if (var12 == '+') {
                                       var89 = -var89;
                                    }

                                    if (var15 != 0.0D && var15 != -1.0D) {
                                       return ScriptRuntime.NaN;
                                    }

                                    double var91 = (double)var89;
                                    var15 = var91;
                                    var4 = true;
                                 }

                                 var1 = var87;
                                 var12 = 0;
                                 continue;
                              }

                              int var129 = 1;

                              while(var9 < var3) {
                                 var130 = var0.charAt(var9);
                                 ++var9;
                                 if (var130 == var55) {
                                    ++var129;
                                 } else if (var130 == ')') {
                                    --var129;
                                    if (var129 <= 0) {
                                       continue label242;
                                    }

                                    var55 = 40;
                                 } else {
                                    var55 = 40;
                                 }
                              }
                              continue;
                           }

                           var47 = var12;
                           var48 = var1;
                           var50 = var13;
                           var51 = var3;
                        } else {
                           var47 = var12;
                           var48 = var1;
                           var50 = var13;
                           var51 = var3;
                        }

                        if (var9 < var51) {
                           char var52 = var0.charAt(var9);
                           if (var130 == '-' && '0' <= var52 && var52 <= '9') {
                              var12 = var130;
                              var1 = var48;
                              var13 = var52;
                              var3 = var51;
                           } else {
                              var12 = var47;
                              var1 = var48;
                              var13 = var52;
                              var3 = var51;
                           }
                        } else {
                           var3 = var51;
                           var12 = var47;
                           var13 = var50;
                           var1 = var48;
                        }
                     }

                     if (var6 >= 0 && var14 >= 0) {
                        if (var8 >= 0) {
                           if (var11 < 0) {
                              var11 = 0;
                           }

                           if (var10 < 0) {
                              var10 = 0;
                           }

                           if (var5 < 0) {
                              var5 = 0;
                           }

                           double var26 = (double)var6;
                           double var28 = (double)var14;
                           double var31 = (double)var8;
                           double var33 = (double)var5;
                           double var37 = (double)var10;
                           double var41 = date_msecFromDate(var26, var28, var31, var33, var37, (double)var11, 0.0D);
                           if (var15 == -1.0D) {
                              return internalUTC(var41);
                           }

                           return var41 + 60000.0D * var15;
                        }
                     }

                     return ScriptRuntime.NaN;
                  }
               }
            }
         }
      }
   }

   static void init(Scriptable var0, boolean var1) {
      NativeDate var2 = new NativeDate();
      var2.date = ScriptRuntime.NaN;
      var2.exportAsJSClass(47, var0, var1);
   }

   private static double internalUTC(double var0) {
      double var2 = LocalTZA;
      return var0 - var2 - DaylightSavingTA(var0 - var2);
   }

   private static Object jsConstructor(Object[] var0) {
      NativeDate var1 = new NativeDate();
      if (var0.length == 0) {
         var1.date = now();
         return var1;
      } else if (var0.length == 1) {
         Object var4 = var0[0];
         if (var4 instanceof NativeDate) {
            var1.date = ((NativeDate)var4).date;
            return var1;
         } else {
            if (var4 instanceof Scriptable) {
               var4 = ((Scriptable)var4).getDefaultValue((Class)null);
            }

            double var5;
            if (var4 instanceof CharSequence) {
               var5 = date_parseString(var4.toString());
            } else {
               var5 = ScriptRuntime.toNumber(var4);
            }

            var1.date = TimeClip(var5);
            return var1;
         }
      } else {
         double var2 = date_msecFromArgs(var0);
         if (!Double.isNaN(var2) && !Double.isInfinite(var2)) {
            var2 = TimeClip(internalUTC(var2));
         }

         var1.date = var2;
         return var1;
      }
   }

   private static double jsStaticFunction_UTC(Object[] var0) {
      return var0.length == 0 ? ScriptRuntime.NaN : TimeClip(date_msecFromArgs(var0));
   }

   private static String js_toISOString(double var0) {
      StringBuilder var2 = new StringBuilder(27);
      int var3 = YearFromTime(var0);
      if (var3 < 0) {
         var2.append('-');
         append0PaddedUint(var2, -var3, 6);
      } else if (var3 > 9999) {
         append0PaddedUint(var2, var3, 6);
      } else {
         append0PaddedUint(var2, var3, 4);
      }

      var2.append('-');
      append0PaddedUint(var2, 1 + MonthFromTime(var0), 2);
      var2.append('-');
      append0PaddedUint(var2, DateFromTime(var0), 2);
      var2.append('T');
      append0PaddedUint(var2, HourFromTime(var0), 2);
      var2.append(':');
      append0PaddedUint(var2, MinFromTime(var0), 2);
      var2.append(':');
      append0PaddedUint(var2, SecFromTime(var0), 2);
      var2.append('.');
      append0PaddedUint(var2, msFromTime(var0), 3);
      var2.append('Z');
      return var2.toString();
   }

   private static String js_toUTCString(double var0) {
      StringBuilder var2 = new StringBuilder(60);
      appendWeekDayName(var2, WeekDay(var0));
      var2.append(", ");
      append0PaddedUint(var2, DateFromTime(var0), 2);
      var2.append(' ');
      appendMonthName(var2, MonthFromTime(var0));
      var2.append(' ');
      int var6 = YearFromTime(var0);
      if (var6 < 0) {
         var2.append('-');
         var6 = -var6;
      }

      append0PaddedUint(var2, var6, 4);
      var2.append(' ');
      append0PaddedUint(var2, HourFromTime(var0), 2);
      var2.append(':');
      append0PaddedUint(var2, MinFromTime(var0), 2);
      var2.append(':');
      append0PaddedUint(var2, SecFromTime(var0), 2);
      var2.append(" GMT");
      return var2.toString();
   }

   private static double makeDate(double var0, Object[] var2, int var3) {
      if (var2.length == 0) {
         return ScriptRuntime.NaN;
      } else {
         boolean var4 = true;
         byte var5;
         switch(var3) {
         case 40:
            var4 = false;
         case 39:
            var5 = 1;
            break;
         case 42:
            var4 = false;
         case 41:
            var5 = 2;
            break;
         case 44:
            var4 = false;
         case 43:
            var5 = 3;
            break;
         default:
            throw Kit.codeBug();
         }

         boolean var6 = false;
         int var7;
         if (var2.length < var5) {
            var7 = var2.length;
         } else {
            var7 = var5;
         }

         if (1 <= var7 && var7 <= 3) {
            double[] var9 = new double[3];

            for(int var10 = 0; var10 < var7; ++var10) {
               double var30 = ScriptRuntime.toNumber(var2[var10]);
               if (var30 == var30 && !Double.isInfinite(var30)) {
                  var9[var10] = ScriptRuntime.toInteger(var30);
               } else {
                  var6 = true;
               }
            }

            if (var6) {
               return ScriptRuntime.NaN;
            } else {
               int var11 = 0;
               double var13;
               if (var0 != var0) {
                  if (var5 < 3) {
                     return ScriptRuntime.NaN;
                  }

                  var13 = 0.0D;
               } else if (var4) {
                  var13 = LocalTime(var0);
               } else {
                  var13 = var0;
               }

               double var15;
               if (var5 >= 3 && var7 < 0) {
                  int var27 = 0 + 1;
                  double var28 = var9[0];
                  var11 = var27;
                  var15 = var28;
               } else {
                  var15 = (double)YearFromTime(var13);
               }

               double var17;
               if (var5 >= 2 && var11 < var7) {
                  int var24 = var11 + 1;
                  double var25 = var9[var11];
                  var11 = var24;
                  var17 = var25;
               } else {
                  var17 = (double)MonthFromTime(var13);
               }

               double var19;
               if (var5 >= 1 && var11 < var7) {
                  int var10000 = var11 + 1;
                  var19 = var9[var11];
               } else {
                  var19 = (double)DateFromTime(var13);
               }

               double var21 = MakeDate(MakeDay(var15, var17, var19), TimeWithinDay(var13));
               if (var4) {
                  var21 = internalUTC(var21);
               }

               return TimeClip(var21);
            }
         } else {
            AssertionError var8 = new AssertionError();
            throw var8;
         }
      }
   }

   private static double makeTime(double var0, Object[] var2, int var3) {
      if (var2.length == 0) {
         return ScriptRuntime.NaN;
      } else {
         boolean var4 = true;
         byte var5;
         switch(var3) {
         case 32:
            var4 = false;
         case 31:
            var5 = 1;
            break;
         case 34:
            var4 = false;
         case 33:
            var5 = 2;
            break;
         case 36:
            var4 = false;
         case 35:
            var5 = 3;
            break;
         case 38:
            var4 = false;
         case 37:
            var5 = 4;
            break;
         default:
            throw Kit.codeBug();
         }

         boolean var6 = false;
         int var7;
         if (var2.length < var5) {
            var7 = var2.length;
         } else {
            var7 = var5;
         }

         if (var7 > 4) {
            AssertionError var8 = new AssertionError();
            throw var8;
         } else {
            double[] var9 = new double[4];

            for(int var10 = 0; var10 < var7; ++var10) {
               double var37 = ScriptRuntime.toNumber(var2[var10]);
               if (var37 == var37 && !Double.isInfinite(var37)) {
                  var9[var10] = ScriptRuntime.toInteger(var37);
               } else {
                  var6 = true;
               }
            }

            if (!var6 && var0 == var0) {
               int var11 = 0;
               double var13;
               if (var4) {
                  var13 = LocalTime(var0);
               } else {
                  var13 = var0;
               }

               double var15;
               if (var5 >= 4 && var7 < 0) {
                  int var34 = 0 + 1;
                  double var35 = var9[0];
                  var11 = var34;
                  var15 = var35;
               } else {
                  var15 = (double)HourFromTime(var13);
               }

               double var17;
               if (var5 >= 3 && var11 < var7) {
                  int var31 = var11 + 1;
                  double var32 = var9[var11];
                  var11 = var31;
                  var17 = var32;
               } else {
                  var17 = (double)MinFromTime(var13);
               }

               double var19;
               if (var5 >= 2 && var11 < var7) {
                  int var28 = var11 + 1;
                  double var29 = var9[var11];
                  var11 = var28;
                  var19 = var29;
               } else {
                  var19 = (double)SecFromTime(var13);
               }

               double var21;
               if (var5 >= 1 && var11 < var7) {
                  int var10000 = var11 + 1;
                  var21 = var9[var11];
               } else {
                  var21 = (double)msFromTime(var13);
               }

               double var23 = MakeTime(var15, var17, var19, var21);
               double var25 = MakeDate(Day(var13), var23);
               if (var4) {
                  var25 = internalUTC(var25);
               }

               return TimeClip(var25);
            } else {
               return ScriptRuntime.NaN;
            }
         }
      }
   }

   private static int msFromTime(double var0) {
      double var2 = var0 % 1000.0D;
      if (var2 < 0.0D) {
         var2 += 1000.0D;
      }

      return (int)var2;
   }

   private static double now() {
      return (double)System.currentTimeMillis();
   }

   private static double parseISOString(String var0) {
      boolean var1 = true;
      int var2 = 0;
      int[] var3 = new int[]{1970, 1, 1, 0, 0, 0, 0, -1, -1};
      byte var4 = 4;
      byte var5 = 1;
      byte var6 = 1;
      int var7 = 0;
      int var8 = var0.length();
      if (var8 != 0) {
         char var76 = var0.charAt(0);
         if (var76 != '+' && var76 != '-') {
            var2 = 0;
            var7 = 0;
            if (var76 == 'T') {
               var7 = 0 + 1;
               var2 = 3;
            }
         } else {
            var7 = 0 + 1;
            var4 = 6;
            byte var77;
            if (var76 == '-') {
               var77 = -1;
            } else {
               var77 = 1;
            }

            var5 = var77;
            var2 = 0;
         }
      }

      int var10;
      label224:
      while(true) {
         if (var2 == -1) {
            var10 = var7;
            break;
         }

         byte var59;
         if (var2 == 0) {
            var59 = var4;
         } else if (var2 == 6) {
            var59 = 3;
         } else {
            var59 = 2;
         }

         int var60 = var7 + var59;
         if (var60 > var8) {
            var2 = -1;
            var10 = var7;
            break;
         }

         int var61 = 0;

         for(var10 = var7; var10 < var60; var1 = var1) {
            char var73 = var0.charAt(var10);
            if (var73 < '0' || var73 > '9') {
               var2 = -1;
               break label224;
            }

            var61 = var61 * 10 + (var73 - 48);
            ++var10;
            var60 = var60;
         }

         var3[var2] = var61;
         if (var10 == var8) {
            if (var2 == 3 || var2 == 7) {
               var2 = -1;
            }
            break;
         }

         var7 = var10 + 1;
         char var64 = var0.charAt(var10);
         if (var64 == 'Z') {
            var3[7] = 0;
            var3[8] = 0;
            if (var2 != 4 && var2 != 5 && var2 != 6) {
               var2 = -1;
               var10 = var7;
            } else {
               var10 = var7;
            }
            break;
         }

         switch(var2) {
         case 0:
         case 1:
            int var65;
            if (var64 == '-') {
               var65 = var2 + 1;
            } else if (var64 == 'T') {
               var65 = 3;
            } else {
               var65 = -1;
            }

            var2 = var65;
            break;
         case 2:
            byte var67;
            if (var64 == 'T') {
               var67 = 3;
            } else {
               var67 = -1;
            }

            var2 = var67;
            break;
         case 3:
            byte var68;
            if (var64 == ':') {
               var68 = 4;
            } else {
               var68 = -1;
            }

            var2 = var68;
            break;
         case 4:
            byte var69;
            if (var64 == ':') {
               var69 = 5;
            } else if (var64 != '+' && var64 != '-') {
               var69 = -1;
            } else {
               var69 = 7;
            }

            var2 = var69;
            break;
         case 5:
            byte var70;
            if (var64 == '.') {
               var70 = 6;
            } else if (var64 != '+' && var64 != '-') {
               var70 = -1;
            } else {
               var70 = 7;
            }

            var2 = var70;
            break;
         case 6:
            byte var71;
            if (var64 != '+' && var64 != '-') {
               var71 = -1;
            } else {
               var71 = 7;
            }

            var2 = var71;
            break;
         case 7:
            if (var64 != ':') {
               --var7;
            }

            var2 = 8;
            break;
         case 8:
            var2 = -1;
         }

         if (var2 == 7) {
            byte var66;
            if (var64 == '-') {
               var66 = -1;
            } else {
               var66 = 1;
            }

            var6 = var66;
         }

         var1 = var1;
      }

      if (var2 != -1) {
         if (var10 == var8) {
            int var15 = var3[0];
            int var16 = var3[1];
            int var17 = var3[2];
            int var18 = var3[3];
            int var20 = var3[4];
            int var21 = var3[5];
            int var22 = var3[6];
            int var23 = var3[7];
            int var24 = var3[8];
            if (var15 <= 275943 && var16 >= 1 && var16 <= 12 && var17 >= 1) {
               if (var17 <= DaysInMonth(var15, var16) && var18 <= 24) {
                  if (var18 != 24 || var20 <= 0 && var21 <= 0 && var22 <= 0) {
                     if (var20 <= 59 && var21 <= 59 && var23 <= 23) {
                        if (var24 <= 59) {
                           double var31 = (double)(var15 * var5);
                           int var33 = var16 - 1;
                           double var35 = (double)var33;
                           double var39 = date_msecFromDate(var31, var35, (double)var17, (double)var18, (double)var20, (double)var21, (double)var22);
                           if (var23 != -1) {
                              double var41 = (double)(var24 + var23 * 60);
                              Double.isNaN(var41);
                              double var44 = var41 * 60000.0D;
                              double var46 = (double)var6;
                              Double.isNaN(var46);
                              var39 -= var44 * var46;
                           }

                           if (var39 >= -8.64E15D && var39 <= 8.64E15D) {
                              return var39;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return ScriptRuntime.NaN;
   }

   private static String toLocale_helper(double param0, int param2) {
      // $FF: Couldn't be decompiled
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(DATE_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 != -3) {
            if (var6 == -2) {
               return ScriptRuntime.wrapNumber(date_parseString(ScriptRuntime.toString(var5, 0)));
            } else if (var6 != -1) {
               if (var6 == 1) {
                  return var4 != null ? date_format(now(), 2) : jsConstructor(var5);
               } else if (var6 != 47) {
                  if (var4 instanceof NativeDate) {
                     NativeDate var13 = (NativeDate)var4;
                     double var14 = var13.date;
                     switch(var6) {
                     case 2:
                     case 3:
                     case 4:
                        if (var14 == var14) {
                           return date_format(var14, var6);
                        }

                        return "Invalid Date";
                     case 5:
                     case 6:
                     case 7:
                        if (var14 == var14) {
                           return toLocale_helper(var14, var6);
                        }

                        return "Invalid Date";
                     case 8:
                        if (var14 == var14) {
                           return js_toUTCString(var14);
                        }

                        return "Invalid Date";
                     case 9:
                        StringBuilder var16 = new StringBuilder();
                        var16.append("(new Date(");
                        var16.append(ScriptRuntime.toString(var14));
                        var16.append("))");
                        return var16.toString();
                     case 10:
                     case 11:
                        return ScriptRuntime.wrapNumber(var14);
                     case 12:
                     case 13:
                     case 14:
                        if (var14 == var14) {
                           if (var6 != 14) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)YearFromTime(var14);
                           if (var6 == 12) {
                              if (var2.hasFeature(1)) {
                                 if (1900.0D <= var14 && var14 < 2000.0D) {
                                    Double.isNaN(var14);
                                    var14 -= 1900.0D;
                                 }
                              } else {
                                 Double.isNaN(var14);
                                 var14 -= 1900.0D;
                              }
                           }
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 15:
                     case 16:
                        if (var14 == var14) {
                           if (var6 == 15) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)MonthFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 17:
                     case 18:
                        if (var14 == var14) {
                           if (var6 == 17) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)DateFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 19:
                     case 20:
                        if (var14 == var14) {
                           if (var6 == 19) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)WeekDay(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 21:
                     case 22:
                        if (var14 == var14) {
                           if (var6 == 21) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)HourFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 23:
                     case 24:
                        if (var14 == var14) {
                           if (var6 == 23) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)MinFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 25:
                     case 26:
                        if (var14 == var14) {
                           if (var6 == 25) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)SecFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 27:
                     case 28:
                        if (var14 == var14) {
                           if (var6 == 27) {
                              var14 = LocalTime(var14);
                           }

                           var14 = (double)msFromTime(var14);
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 29:
                        if (var14 == var14) {
                           var14 = (var14 - LocalTime(var14)) / 60000.0D;
                        }

                        return ScriptRuntime.wrapNumber(var14);
                     case 30:
                        double var22 = TimeClip(ScriptRuntime.toNumber(var5, 0));
                        var13.date = var22;
                        return ScriptRuntime.wrapNumber(var22);
                     case 31:
                     case 32:
                     case 33:
                     case 34:
                     case 35:
                     case 36:
                     case 37:
                     case 38:
                        double var24 = makeTime(var14, var5, var6);
                        var13.date = var24;
                        return ScriptRuntime.wrapNumber(var24);
                     case 39:
                     case 40:
                     case 41:
                     case 42:
                     case 43:
                     case 44:
                        double var26 = makeDate(var14, var5, var6);
                        var13.date = var26;
                        return ScriptRuntime.wrapNumber(var26);
                     case 45:
                        double var28 = ScriptRuntime.toNumber(var5, 0);
                        double var30;
                        if (var28 == var28 && !Double.isInfinite(var28)) {
                           double var32;
                           if (var14 != var14) {
                              var32 = 0.0D;
                           } else {
                              var32 = LocalTime(var14);
                           }

                           if (var28 >= 0.0D && var28 <= 99.0D) {
                              var28 += 1900.0D;
                           }

                           double var34 = (double)MonthFromTime(var32);
                           double var36 = (double)DateFromTime(var32);
                           var30 = TimeClip(internalUTC(MakeDate(MakeDay(var28, var34, var36), TimeWithinDay(var32))));
                        } else {
                           var30 = ScriptRuntime.NaN;
                        }

                        var13.date = var30;
                        return ScriptRuntime.wrapNumber(var30);
                     case 46:
                        if (var14 == var14) {
                           return js_toISOString(var14);
                        }

                        throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage0("msg.invalid.date"));
                     default:
                        throw new IllegalArgumentException(String.valueOf(var6));
                     }
                  } else {
                     throw incompatibleCallError(var1);
                  }
               } else {
                  Scriptable var7 = ScriptRuntime.toObject((Context)var2, (Scriptable)var3, (Object)var4);
                  Object var8 = ScriptRuntime.toPrimitive(var7, ScriptRuntime.NumberClass);
                  if (var8 instanceof Number) {
                     double var11 = ((Number)var8).doubleValue();
                     if (var11 != var11 || Double.isInfinite(var11)) {
                        return null;
                     }
                  }

                  Object var9 = ScriptableObject.getProperty(var7, "toISOString");
                  if (var9 != NOT_FOUND) {
                     if (var9 instanceof Callable) {
                        Object var10 = ((Callable)var9).call(var2, var3, var7, ScriptRuntime.emptyArgs);
                        if (ScriptRuntime.isPrimitive(var10)) {
                           return var10;
                        } else {
                           throw ScriptRuntime.typeError1("msg.toisostring.must.return.primitive", ScriptRuntime.toString(var10));
                        }
                     } else {
                        throw ScriptRuntime.typeError3("msg.isnt.function.in", "toISOString", ScriptRuntime.toString(var7), ScriptRuntime.toString(var9));
                     }
                  } else {
                     throw ScriptRuntime.typeError2("msg.function.not.found.in", "toISOString", ScriptRuntime.toString(var7));
                  }
               }
            } else {
               return ScriptRuntime.wrapNumber(jsStaticFunction_UTC(var5));
            }
         } else {
            return ScriptRuntime.wrapNumber(now());
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      Object var2 = DATE_TAG;
      this.addIdFunctionProperty(var1, var2, -3, "now", 0);
      this.addIdFunctionProperty(var1, var2, -2, "parse", 1);
      this.addIdFunctionProperty(var1, var2, -1, "UTC", 7);
      super.fillConstructorProperties(var1);
   }

   protected int findPrototypeId(String var1) {
      byte var3;
      String var4;
      label184:
      switch(var1.length()) {
      case 6:
         char var2 = var1.charAt(0);
         if (var2 == 'g') {
            var4 = "getDay";
            var3 = 19;
         } else {
            var3 = 0;
            var4 = null;
            if (var2 == 't') {
               var4 = "toJSON";
               var3 = 47;
            }
         }
         break;
      case 7:
         char var5 = var1.charAt(3);
         if (var5 != 'D') {
            if (var5 != 'T') {
               if (var5 != 'Y') {
                  if (var5 != 'u') {
                     var3 = 0;
                     var4 = null;
                  } else {
                     var4 = "valueOf";
                     var3 = 10;
                  }
               } else {
                  char var8 = var1.charAt(0);
                  if (var8 == 'g') {
                     var4 = "getYear";
                     var3 = 12;
                  } else {
                     var3 = 0;
                     var4 = null;
                     if (var8 == 's') {
                        var4 = "setYear";
                        var3 = 45;
                     }
                  }
               }
            } else {
               char var7 = var1.charAt(0);
               if (var7 == 'g') {
                  var4 = "getTime";
                  var3 = 11;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var7 == 's') {
                     var4 = "setTime";
                     var3 = 30;
                  }
               }
            }
         } else {
            char var6 = var1.charAt(0);
            if (var6 == 'g') {
               var4 = "getDate";
               var3 = 17;
            } else {
               var3 = 0;
               var4 = null;
               if (var6 == 's') {
                  var4 = "setDate";
                  var3 = 39;
               }
            }
         }
         break;
      case 8:
         char var9 = var1.charAt(3);
         if (var9 != 'H') {
            if (var9 != 'M') {
               if (var9 != 'o') {
                  if (var9 != 't') {
                     var3 = 0;
                     var4 = null;
                  } else {
                     var4 = "toString";
                     var3 = 2;
                  }
               } else {
                  var4 = "toSource";
                  var3 = 9;
               }
            } else {
               char var11 = var1.charAt(0);
               if (var11 == 'g') {
                  var4 = "getMonth";
                  var3 = 15;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var11 == 's') {
                     var4 = "setMonth";
                     var3 = 41;
                  }
               }
            }
         } else {
            char var10 = var1.charAt(0);
            if (var10 == 'g') {
               var4 = "getHours";
               var3 = 21;
            } else {
               var3 = 0;
               var4 = null;
               if (var10 == 's') {
                  var4 = "setHours";
                  var3 = 37;
               }
            }
         }
         break;
      case 9:
         var4 = "getUTCDay";
         var3 = 20;
         break;
      case 10:
         char var12 = var1.charAt(3);
         if (var12 == 'M') {
            char var15 = var1.charAt(0);
            if (var15 == 'g') {
               var4 = "getMinutes";
               var3 = 23;
            } else {
               var3 = 0;
               var4 = null;
               if (var15 == 's') {
                  var4 = "setMinutes";
                  var3 = 35;
               }
            }
         } else if (var12 == 'S') {
            char var14 = var1.charAt(0);
            if (var14 == 'g') {
               var4 = "getSeconds";
               var3 = 25;
            } else {
               var3 = 0;
               var4 = null;
               if (var14 == 's') {
                  var4 = "setSeconds";
                  var3 = 33;
               }
            }
         } else {
            var3 = 0;
            var4 = null;
            if (var12 == 'U') {
               char var13 = var1.charAt(0);
               if (var13 == 'g') {
                  var4 = "getUTCDate";
                  var3 = 18;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var13 == 's') {
                     var4 = "setUTCDate";
                     var3 = 40;
                  }
               }
            }
         }
         break;
      case 11:
         char var16 = var1.charAt(3);
         if (var16 != 'F') {
            if (var16 != 'M') {
               if (var16 != 's') {
                  switch(var16) {
                  case 'S':
                     var4 = "toISOString";
                     var3 = 46;
                     break label184;
                  case 'T':
                     var4 = "toUTCString";
                     var3 = 8;
                     break label184;
                  case 'U':
                     char var18 = var1.charAt(0);
                     if (var18 == 'g') {
                        char var20 = var1.charAt(9);
                        if (var20 == 'r') {
                           var4 = "getUTCHours";
                           var3 = 22;
                        } else {
                           var3 = 0;
                           var4 = null;
                           if (var20 == 't') {
                              var4 = "getUTCMonth";
                              var3 = 16;
                           }
                        }
                     } else {
                        var3 = 0;
                        var4 = null;
                        if (var18 == 's') {
                           char var19 = var1.charAt(9);
                           if (var19 == 'r') {
                              var4 = "setUTCHours";
                              var3 = 38;
                           } else {
                              var3 = 0;
                              var4 = null;
                              if (var19 == 't') {
                                 var4 = "setUTCMonth";
                                 var3 = 42;
                              }
                           }
                        }
                     }
                     break label184;
                  default:
                     var3 = 0;
                     var4 = null;
                  }
               } else {
                  var4 = "constructor";
                  var3 = 1;
               }
            } else {
               var4 = "toGMTString";
               var3 = 8;
            }
         } else {
            char var17 = var1.charAt(0);
            if (var17 == 'g') {
               var4 = "getFullYear";
               var3 = 13;
            } else {
               var3 = 0;
               var4 = null;
               if (var17 == 's') {
                  var4 = "setFullYear";
                  var3 = 43;
               }
            }
         }
         break;
      case 12:
         char var21 = var1.charAt(2);
         if (var21 == 'D') {
            var4 = "toDateString";
            var3 = 4;
         } else {
            var3 = 0;
            var4 = null;
            if (var21 == 'T') {
               var4 = "toTimeString";
               var3 = 3;
            }
         }
         break;
      case 13:
         char var22 = var1.charAt(0);
         if (var22 == 'g') {
            char var24 = var1.charAt(6);
            if (var24 == 'M') {
               var4 = "getUTCMinutes";
               var3 = 24;
            } else {
               var3 = 0;
               var4 = null;
               if (var24 == 'S') {
                  var4 = "getUTCSeconds";
                  var3 = 26;
               }
            }
         } else {
            var3 = 0;
            var4 = null;
            if (var22 == 's') {
               char var23 = var1.charAt(6);
               if (var23 == 'M') {
                  var4 = "setUTCMinutes";
                  var3 = 36;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var23 == 'S') {
                     var4 = "setUTCSeconds";
                     var3 = 34;
                  }
               }
            }
         }
         break;
      case 14:
         char var25 = var1.charAt(0);
         if (var25 == 'g') {
            var4 = "getUTCFullYear";
            var3 = 14;
         } else if (var25 == 's') {
            var4 = "setUTCFullYear";
            var3 = 44;
         } else {
            var3 = 0;
            var4 = null;
            if (var25 == 't') {
               var4 = "toLocaleString";
               var3 = 5;
            }
         }
         break;
      case 15:
         char var26 = var1.charAt(0);
         if (var26 == 'g') {
            var4 = "getMilliseconds";
            var3 = 27;
         } else {
            var3 = 0;
            var4 = null;
            if (var26 == 's') {
               var4 = "setMilliseconds";
               var3 = 31;
            }
         }
         break;
      case 16:
      default:
         var3 = 0;
         var4 = null;
         break;
      case 17:
         var4 = "getTimezoneOffset";
         var3 = 29;
         break;
      case 18:
         char var27 = var1.charAt(0);
         if (var27 == 'g') {
            var4 = "getUTCMilliseconds";
            var3 = 28;
         } else if (var27 == 's') {
            var4 = "setUTCMilliseconds";
            var3 = 32;
         } else {
            var3 = 0;
            var4 = null;
            if (var27 == 't') {
               char var28 = var1.charAt(8);
               if (var28 == 'D') {
                  var4 = "toLocaleDateString";
                  var3 = 7;
               } else {
                  var3 = 0;
                  var4 = null;
                  if (var28 == 'T') {
                     var4 = "toLocaleTimeString";
                     var3 = 6;
                  }
               }
            }
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Date";
   }

   public Object getDefaultValue(Class var1) {
      if (var1 == null) {
         var1 = ScriptRuntime.StringClass;
      }

      return super.getDefaultValue(var1);
   }

   double getJSTimeValue() {
      return this.date;
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      switch(var1) {
      case 1:
         var2 = 7;
         var3 = "constructor";
         break;
      case 2:
         var3 = "toString";
         var2 = 0;
         break;
      case 3:
         var3 = "toTimeString";
         var2 = 0;
         break;
      case 4:
         var3 = "toDateString";
         var2 = 0;
         break;
      case 5:
         var3 = "toLocaleString";
         var2 = 0;
         break;
      case 6:
         var3 = "toLocaleTimeString";
         var2 = 0;
         break;
      case 7:
         var3 = "toLocaleDateString";
         var2 = 0;
         break;
      case 8:
         var3 = "toUTCString";
         var2 = 0;
         break;
      case 9:
         var3 = "toSource";
         var2 = 0;
         break;
      case 10:
         var3 = "valueOf";
         var2 = 0;
         break;
      case 11:
         var3 = "getTime";
         var2 = 0;
         break;
      case 12:
         var3 = "getYear";
         var2 = 0;
         break;
      case 13:
         var3 = "getFullYear";
         var2 = 0;
         break;
      case 14:
         var3 = "getUTCFullYear";
         var2 = 0;
         break;
      case 15:
         var3 = "getMonth";
         var2 = 0;
         break;
      case 16:
         var3 = "getUTCMonth";
         var2 = 0;
         break;
      case 17:
         var3 = "getDate";
         var2 = 0;
         break;
      case 18:
         var3 = "getUTCDate";
         var2 = 0;
         break;
      case 19:
         var3 = "getDay";
         var2 = 0;
         break;
      case 20:
         var3 = "getUTCDay";
         var2 = 0;
         break;
      case 21:
         var3 = "getHours";
         var2 = 0;
         break;
      case 22:
         var3 = "getUTCHours";
         var2 = 0;
         break;
      case 23:
         var3 = "getMinutes";
         var2 = 0;
         break;
      case 24:
         var3 = "getUTCMinutes";
         var2 = 0;
         break;
      case 25:
         var3 = "getSeconds";
         var2 = 0;
         break;
      case 26:
         var3 = "getUTCSeconds";
         var2 = 0;
         break;
      case 27:
         var3 = "getMilliseconds";
         var2 = 0;
         break;
      case 28:
         var3 = "getUTCMilliseconds";
         var2 = 0;
         break;
      case 29:
         var3 = "getTimezoneOffset";
         var2 = 0;
         break;
      case 30:
         var2 = 1;
         var3 = "setTime";
         break;
      case 31:
         var2 = 1;
         var3 = "setMilliseconds";
         break;
      case 32:
         var2 = 1;
         var3 = "setUTCMilliseconds";
         break;
      case 33:
         var2 = 2;
         var3 = "setSeconds";
         break;
      case 34:
         var2 = 2;
         var3 = "setUTCSeconds";
         break;
      case 35:
         var2 = 3;
         var3 = "setMinutes";
         break;
      case 36:
         var2 = 3;
         var3 = "setUTCMinutes";
         break;
      case 37:
         var2 = 4;
         var3 = "setHours";
         break;
      case 38:
         var2 = 4;
         var3 = "setUTCHours";
         break;
      case 39:
         var2 = 1;
         var3 = "setDate";
         break;
      case 40:
         var2 = 1;
         var3 = "setUTCDate";
         break;
      case 41:
         var2 = 2;
         var3 = "setMonth";
         break;
      case 42:
         var2 = 2;
         var3 = "setUTCMonth";
         break;
      case 43:
         var2 = 3;
         var3 = "setFullYear";
         break;
      case 44:
         var2 = 3;
         var3 = "setUTCFullYear";
         break;
      case 45:
         var2 = 1;
         var3 = "setYear";
         break;
      case 46:
         var3 = "toISOString";
         var2 = 0;
         break;
      case 47:
         var2 = 1;
         var3 = "toJSON";
         break;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }

      this.initPrototypeMethod(DATE_TAG, var1, var3, var2);
   }
}
