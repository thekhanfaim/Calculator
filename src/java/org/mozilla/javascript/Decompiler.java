package org.mozilla.javascript;

public class Decompiler {
   public static final int CASE_GAP_PROP = 3;
   private static final int FUNCTION_END = 167;
   public static final int INDENT_GAP_PROP = 2;
   public static final int INITIAL_INDENT_PROP = 1;
   public static final int ONLY_BODY_FLAG = 1;
   public static final int TO_SOURCE_FLAG = 2;
   private static final boolean printSource;
   private char[] sourceBuffer = new char[128];
   private int sourceTop;

   private void append(char var1) {
      int var2 = this.sourceTop;
      if (var2 == this.sourceBuffer.length) {
         this.increaseSourceCapacity(var2 + 1);
      }

      char[] var3 = this.sourceBuffer;
      int var4 = this.sourceTop;
      var3[var4] = var1;
      this.sourceTop = var4 + 1;
   }

   private void appendString(String var1) {
      int var2 = var1.length();
      byte var3 = 1;
      if (var2 >= 32768) {
         var3 = 2;
      }

      int var4 = var2 + var3 + this.sourceTop;
      if (var4 > this.sourceBuffer.length) {
         this.increaseSourceCapacity(var4);
      }

      if (var2 >= 32768) {
         char[] var8 = this.sourceBuffer;
         int var9 = this.sourceTop;
         var8[var9] = (char)('耀' | var2 >>> 16);
         this.sourceTop = var9 + 1;
      }

      char[] var5 = this.sourceBuffer;
      int var6 = this.sourceTop;
      var5[var6] = (char)var2;
      int var7 = var6 + 1;
      this.sourceTop = var7;
      var1.getChars(0, var2, var5, var7);
      this.sourceTop = var4;
   }

   public static String decompile(String var0, int var1, UintMap var2) {
      String var3 = var0;
      int var4 = var0.length();
      if (var4 == 0) {
         return "";
      } else {
         int var5 = var2.getInt(1, 0);
         if (var5 >= 0) {
            int var7 = var2.getInt(2, 4);
            if (var7 < 0) {
               throw new IllegalArgumentException();
            } else {
               int var8 = var2.getInt(3, 2);
               if (var8 >= 0) {
                  StringBuilder var9 = new StringBuilder();
                  boolean var10;
                  if ((var1 & 1) != 0) {
                     var10 = true;
                  } else {
                     var10 = false;
                  }

                  boolean var11;
                  if ((var1 & 2) != 0) {
                     var11 = true;
                  } else {
                     var11 = false;
                  }

                  int var12 = 0;
                  int var13;
                  if (var0.charAt(0) == 137) {
                     var12 = 0 + 1;
                     var13 = -1;
                  } else {
                     var13 = var0.charAt(0 + 1);
                  }

                  int var14;
                  boolean var15;
                  if (!var11) {
                     var9.append('\n');

                     for(int var131 = 0; var131 < var5; ++var131) {
                        var9.append(' ');
                     }

                     var14 = 0;
                     var15 = false;
                  } else {
                     var14 = 0;
                     var15 = false;
                     if (var13 == 2) {
                        var9.append('(');
                     }
                  }

                  while(var12 < var4) {
                     int var21;
                     label356: {
                        char var19 = var3.charAt(var12);
                        if (var19 != 1) {
                           if (var19 != 4) {
                              if (var19 != '2') {
                                 if (var19 != 'C') {
                                    if (var19 != 'I') {
                                       if (var19 != 161) {
                                          if (var19 != 167) {
                                             if (var19 != '4') {
                                                if (var19 != '5') {
                                                   if (var19 != 144) {
                                                      if (var19 != 145) {
                                                         if (var19 != 147) {
                                                            if (var19 != 148) {
                                                               label350: {
                                                                  if (var19 != 164) {
                                                                     if (var19 == 165) {
                                                                        var9.append(" => ");
                                                                        break label350;
                                                                     }

                                                                     switch(var19) {
                                                                     case '\t':
                                                                        var9.append(" | ");
                                                                        break label350;
                                                                     case '\n':
                                                                        var9.append(" ^ ");
                                                                        break label350;
                                                                     case '\u000b':
                                                                        var9.append(" & ");
                                                                        break label350;
                                                                     case '\f':
                                                                        var9.append(" == ");
                                                                        break label350;
                                                                     case '\r':
                                                                        var9.append(" != ");
                                                                        break label350;
                                                                     case '\u000e':
                                                                        var9.append(" < ");
                                                                        break label350;
                                                                     case '\u000f':
                                                                        var9.append(" <= ");
                                                                        break label350;
                                                                     case '\u0010':
                                                                        var9.append(" > ");
                                                                        break label350;
                                                                     case '\u0011':
                                                                        var9.append(" >= ");
                                                                        break label350;
                                                                     case '\u0012':
                                                                        var9.append(" << ");
                                                                        break label350;
                                                                     case '\u0013':
                                                                        var9.append(" >> ");
                                                                        break label350;
                                                                     case '\u0014':
                                                                        var9.append(" >>> ");
                                                                        break label350;
                                                                     case '\u0015':
                                                                        var9.append(" + ");
                                                                        break label350;
                                                                     case '\u0016':
                                                                        var9.append(" - ");
                                                                        break label350;
                                                                     case '\u0017':
                                                                        var9.append(" * ");
                                                                        break label350;
                                                                     case '\u0018':
                                                                        var9.append(" / ");
                                                                        break label350;
                                                                     case '\u0019':
                                                                        var9.append(" % ");
                                                                        break label350;
                                                                     case '\u001a':
                                                                        var9.append('!');
                                                                        break label350;
                                                                     case '\u001b':
                                                                        var9.append('~');
                                                                        break label350;
                                                                     case '\u001c':
                                                                        var9.append('+');
                                                                        break label350;
                                                                     case '\u001d':
                                                                        var9.append('-');
                                                                        break label350;
                                                                     case '\u001e':
                                                                        var9.append("new ");
                                                                        break label350;
                                                                     case '\u001f':
                                                                        var9.append("delete ");
                                                                        break label350;
                                                                     case ' ':
                                                                        var9.append("typeof ");
                                                                        break label350;
                                                                     default:
                                                                        switch(var19) {
                                                                        case '\'':
                                                                        case '0':
                                                                           var12 = printSourceString(var3, var12 + 1, false, var9);
                                                                           continue;
                                                                        case '(':
                                                                           var12 = printSourceNumber(var3, var12 + 1, var9);
                                                                           continue;
                                                                        case ')':
                                                                           var12 = printSourceString(var3, var12 + 1, true, var9);
                                                                           continue;
                                                                        case '*':
                                                                           var9.append("null");
                                                                           break label350;
                                                                        case '+':
                                                                           var9.append("this");
                                                                           break label350;
                                                                        case ',':
                                                                           var9.append("false");
                                                                           break label350;
                                                                        case '-':
                                                                           var9.append("true");
                                                                           break label350;
                                                                        case '.':
                                                                           var9.append(" === ");
                                                                           break label350;
                                                                        case '/':
                                                                           var9.append(" !== ");
                                                                           break label350;
                                                                        default:
                                                                           switch(var19) {
                                                                           case 'R':
                                                                              var9.append("try ");
                                                                              break label350;
                                                                           case 'S':
                                                                              var9.append(';');
                                                                              if (1 != getNext(var3, var4, var12)) {
                                                                                 var9.append(' ');
                                                                              }
                                                                              break label350;
                                                                           case 'T':
                                                                              var9.append('[');
                                                                              break label350;
                                                                           case 'U':
                                                                              var9.append(']');
                                                                              break label350;
                                                                           case 'V':
                                                                              ++var14;
                                                                              if (1 == getNext(var3, var4, var12)) {
                                                                                 var5 += var7;
                                                                              }

                                                                              var9.append('{');
                                                                              var21 = var4;
                                                                              break label356;
                                                                           case 'W':
                                                                              --var14;
                                                                              if (var10 && var14 == 0) {
                                                                                 var21 = var4;
                                                                                 break label356;
                                                                              }

                                                                              label253: {
                                                                                 var9.append('}');
                                                                                 int var80 = getNext(var3, var4, var12);
                                                                                 if (var80 != 1) {
                                                                                    if (var80 == 114 || var80 == 118) {
                                                                                       var5 -= var7;
                                                                                       var9.append(' ');
                                                                                       break label253;
                                                                                    }

                                                                                    if (var80 != 167) {
                                                                                       break label253;
                                                                                    }
                                                                                 }

                                                                                 var5 -= var7;
                                                                              }

                                                                              var21 = var4;
                                                                              break label356;
                                                                           case 'X':
                                                                              var9.append('(');
                                                                              break label350;
                                                                           case 'Y':
                                                                              var9.append(')');
                                                                              if (86 == getNext(var3, var4, var12)) {
                                                                                 var9.append(' ');
                                                                              }
                                                                              break label350;
                                                                           case 'Z':
                                                                              var9.append(", ");
                                                                              break label350;
                                                                           case '[':
                                                                              var9.append(" = ");
                                                                              break label350;
                                                                           case '\\':
                                                                              var9.append(" |= ");
                                                                              break label350;
                                                                           case ']':
                                                                              var9.append(" ^= ");
                                                                              break label350;
                                                                           case '^':
                                                                              var9.append(" &= ");
                                                                              break label350;
                                                                           case '_':
                                                                              var9.append(" <<= ");
                                                                              break label350;
                                                                           case '`':
                                                                              var9.append(" >>= ");
                                                                              break label350;
                                                                           case 'a':
                                                                              var9.append(" >>>= ");
                                                                              break label350;
                                                                           case 'b':
                                                                              var9.append(" += ");
                                                                              break label350;
                                                                           case 'c':
                                                                              var9.append(" -= ");
                                                                              break label350;
                                                                           case 'd':
                                                                              var9.append(" *= ");
                                                                              break label350;
                                                                           case 'e':
                                                                              var9.append(" /= ");
                                                                              break label350;
                                                                           case 'f':
                                                                              var9.append(" %= ");
                                                                              break label350;
                                                                           case 'g':
                                                                              var9.append(" ? ");
                                                                              break label350;
                                                                           case 'h':
                                                                              if (1 == getNext(var3, var4, var12)) {
                                                                                 var9.append(':');
                                                                              } else {
                                                                                 var9.append(" : ");
                                                                              }
                                                                              break label350;
                                                                           case 'i':
                                                                              var9.append(" || ");
                                                                              break label350;
                                                                           case 'j':
                                                                              var9.append(" && ");
                                                                              break label350;
                                                                           case 'k':
                                                                              var9.append("++");
                                                                              break label350;
                                                                           case 'l':
                                                                              var9.append("--");
                                                                              break label350;
                                                                           case 'm':
                                                                              var9.append('.');
                                                                              break label350;
                                                                           case 'n':
                                                                              ++var12;
                                                                              var9.append("function ");
                                                                              var21 = var4;
                                                                              break label356;
                                                                           default:
                                                                              switch(var19) {
                                                                              case 'q':
                                                                                 var9.append("if ");
                                                                                 break label350;
                                                                              case 'r':
                                                                                 var9.append("else ");
                                                                                 break label350;
                                                                              case 's':
                                                                                 var9.append("switch ");
                                                                                 break label350;
                                                                              case 't':
                                                                                 var9.append("case ");
                                                                                 break label350;
                                                                              case 'u':
                                                                                 var9.append("default");
                                                                                 break label350;
                                                                              case 'v':
                                                                                 var9.append("while ");
                                                                                 break label350;
                                                                              case 'w':
                                                                                 var9.append("do ");
                                                                                 break label350;
                                                                              case 'x':
                                                                                 var9.append("for ");
                                                                                 break label350;
                                                                              case 'y':
                                                                                 var9.append("break");
                                                                                 if (39 == getNext(var3, var4, var12)) {
                                                                                    var9.append(' ');
                                                                                 }
                                                                                 break label350;
                                                                              case 'z':
                                                                                 var9.append("continue");
                                                                                 if (39 == getNext(var3, var4, var12)) {
                                                                                    var9.append(' ');
                                                                                 }
                                                                                 break label350;
                                                                              case '{':
                                                                                 var9.append("var ");
                                                                                 break label350;
                                                                              case '|':
                                                                                 var9.append("with ");
                                                                                 break label350;
                                                                              case '}':
                                                                                 var9.append("catch ");
                                                                                 break label350;
                                                                              case '~':
                                                                                 var9.append("finally ");
                                                                                 break label350;
                                                                              case '\u007f':
                                                                                 var9.append("void ");
                                                                                 break label350;
                                                                              default:
                                                                                 switch(var19) {
                                                                                 case '\u0098':
                                                                                 case '\u0099':
                                                                                    break;
                                                                                 case '\u009a':
                                                                                    var9.append("let ");
                                                                                    break label350;
                                                                                 case '\u009b':
                                                                                    var9.append("const ");
                                                                                    break label350;
                                                                                 default:
                                                                                    StringBuilder var126 = new StringBuilder();
                                                                                    var126.append("Token: ");
                                                                                    var126.append(Token.name(var3.charAt(var12)));
                                                                                    throw new RuntimeException(var126.toString());
                                                                                 }
                                                                              }
                                                                           }
                                                                        }
                                                                     }
                                                                  }

                                                                  if (var3.charAt(var12) == 152) {
                                                                     var9.append("get ");
                                                                  } else if (var3.charAt(var12) == 153) {
                                                                     var9.append("set ");
                                                                  }

                                                                  var12 = 1 + printSourceString(var3, 1 + var12 + 1, false, var9);
                                                                  var21 = var4;
                                                                  break label356;
                                                               }
                                                            } else {
                                                               var9.append('@');
                                                            }
                                                         } else {
                                                            var9.append(".(");
                                                         }
                                                      } else {
                                                         var9.append("::");
                                                      }
                                                   } else {
                                                      var9.append("..");
                                                   }
                                                } else {
                                                   var9.append(" instanceof ");
                                                }
                                             } else {
                                                var9.append(" in ");
                                             }
                                          }
                                       } else {
                                          var9.append("debugger;\n");
                                       }
                                    } else {
                                       var9.append("yield ");
                                    }
                                 } else {
                                    var9.append(": ");
                                 }
                              } else {
                                 var9.append("throw ");
                              }
                           } else {
                              var9.append("return");
                              if (83 != getNext(var3, var4, var12)) {
                                 var9.append(' ');
                              }
                           }
                        } else if (!var11) {
                           boolean var20 = true;
                           if (!var15) {
                              var15 = true;
                              if (var10) {
                                 var9.setLength(0);
                                 var5 -= var7;
                                 var20 = false;
                              }
                           }

                           if (var20) {
                              var9.append('\n');
                           }

                           if (var12 + 1 < var4) {
                              int var23;
                              label235: {
                                 char var22 = var3.charAt(var12 + 1);
                                 if (var22 != 't') {
                                    if (var22 != 'u') {
                                       if (var22 == 'W') {
                                          var23 = var7;
                                          var21 = var4;
                                       } else if (var22 == '\'') {
                                          int var25 = getSourceStringEnd(var3, var12 + 2);
                                          var21 = var4;
                                          char var26 = var3.charAt(var25);
                                          var23 = 0;
                                          if (var26 == 'h') {
                                             var23 = var7;
                                          }
                                       } else {
                                          var21 = var4;
                                          var23 = 0;
                                       }
                                       break label235;
                                    }

                                    var21 = var4;
                                 } else {
                                    var21 = var4;
                                 }

                                 var23 = var7 - var8;
                              }

                              while(true) {
                                 if (var23 >= var5) {
                                    break label356;
                                 }

                                 var9.append(' ');
                                 ++var23;
                              }
                           } else {
                              var21 = var4;
                              break label356;
                           }
                        }

                        var21 = var4;
                     }

                     ++var12;
                     var3 = var0;
                     var4 = var21;
                  }

                  if (!var11) {
                     if (!var10) {
                        var9.append('\n');
                     }
                  } else if (var13 == 2) {
                     var9.append(')');
                  }

                  return var9.toString();
               } else {
                  throw new IllegalArgumentException();
               }
            }
         } else {
            IllegalArgumentException var6 = new IllegalArgumentException();
            throw var6;
         }
      }
   }

   private static int getNext(String param0, int param1, int param2) {
      // $FF: Couldn't be decompiled
   }

   private static int getSourceStringEnd(String param0, int param1) {
      // $FF: Couldn't be decompiled
   }

   private void increaseSourceCapacity(int param1) {
      // $FF: Couldn't be decompiled
   }

   private static int printSourceNumber(String param0, int param1, StringBuilder param2) {
      // $FF: Couldn't be decompiled
   }

   private static int printSourceString(String param0, int param1, boolean param2, StringBuilder param3) {
      // $FF: Couldn't be decompiled
   }

   private String sourceToString(int param1) {
      // $FF: Couldn't be decompiled
   }

   void addEOL(int param1) {
      // $FF: Couldn't be decompiled
   }

   void addName(String param1) {
      // $FF: Couldn't be decompiled
   }

   void addNumber(double param1) {
      // $FF: Couldn't be decompiled
   }

   void addRegexp(String param1, String param2) {
      // $FF: Couldn't be decompiled
   }

   void addString(String param1) {
      // $FF: Couldn't be decompiled
   }

   void addToken(int param1) {
      // $FF: Couldn't be decompiled
   }

   int getCurrentOffset() {
      // $FF: Couldn't be decompiled
   }

   String getEncodedSource() {
      // $FF: Couldn't be decompiled
   }

   int markFunctionEnd(int param1) {
      // $FF: Couldn't be decompiled
   }

   int markFunctionStart(int param1) {
      // $FF: Couldn't be decompiled
   }
}
