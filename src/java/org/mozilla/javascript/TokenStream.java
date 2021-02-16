package org.mozilla.javascript;

import java.io.IOException;
import java.io.Reader;

class TokenStream {
   // $FF: synthetic field
   static final boolean $assertionsDisabled = false;
   private static final char BYTE_ORDER_MARK = '\ufeff';
   private static final int EOF_CHAR = -1;
   private ObjToIntMap allStrings = new ObjToIntMap(50);
   private int commentCursor = -1;
   private String commentPrefix = "";
   Token.CommentType commentType;
   int cursor;
   private boolean dirtyLine;
   private boolean hitEOF = false;
   private boolean isBinary;
   private boolean isHex;
   private boolean isOctal;
   private boolean isOldOctal;
   private int lineEndChar = -1;
   private int lineStart = 0;
   int lineno;
   private double number;
   private Parser parser;
   private int quoteChar;
   String regExpFlags;
   private char[] sourceBuffer;
   int sourceCursor;
   private int sourceEnd;
   private Reader sourceReader;
   private String sourceString;
   private String string = "";
   private char[] stringBuffer = new char[128];
   private int stringBufferTop;
   int tokenBeg;
   int tokenEnd;
   private final int[] ungetBuffer = new int[3];
   private int ungetCursor;
   private boolean xmlIsAttribute;
   private boolean xmlIsTagContent;
   private int xmlOpenTagsCount;

   TokenStream(Parser var1, Reader var2, String var3, int var4) {
      this.parser = var1;
      this.lineno = var4;
      if (var2 != null) {
         if (var3 != null) {
            Kit.codeBug();
         }

         this.sourceReader = var2;
         this.sourceBuffer = new char[512];
         this.sourceEnd = 0;
      } else {
         if (var3 == null) {
            Kit.codeBug();
         }

         this.sourceString = var3;
         this.sourceEnd = var3.length();
      }

      this.cursor = 0;
      this.sourceCursor = 0;
   }

   private void addToString(int var1) {
      int var2 = this.stringBufferTop;
      char[] var3 = this.stringBuffer;
      if (var2 == var3.length) {
         char[] var4 = new char[2 * var3.length];
         System.arraycopy(var3, 0, var4, 0, var2);
         this.stringBuffer = var4;
      }

      this.stringBuffer[var2] = (char)var1;
      this.stringBufferTop = var2 + 1;
   }

   private boolean canUngetChar() {
      int var1 = this.ungetCursor;
      byte var2 = 1;
      if (var1 != 0) {
         if (this.ungetBuffer[var1 - var2] != 10) {
            return (boolean)var2;
         }

         var2 = 0;
      }

      return (boolean)var2;
   }

   private final int charAt(int var1) {
      if (var1 < 0) {
         return -1;
      } else {
         String var2 = this.sourceString;
         if (var2 != null) {
            return var1 >= this.sourceEnd ? -1 : var2.charAt(var1);
         } else {
            if (var1 >= this.sourceEnd) {
               int var3 = this.sourceCursor;

               boolean var5;
               try {
                  var5 = this.fillSourceBuffer();
               } catch (IOException var6) {
                  return -1;
               }

               if (!var5) {
                  return -1;
               }

               var1 -= var3 - this.sourceCursor;
            }

            return this.sourceBuffer[var1];
         }
      }
   }

   private String convertLastCharToHex(String var1) {
      int var2 = -1 + var1.length();
      StringBuilder var3 = new StringBuilder(var1.substring(0, var2));
      var3.append("\\u");
      String var5 = Integer.toHexString(var1.charAt(var2));

      for(int var6 = 0; var6 < 4 - var5.length(); ++var6) {
         var3.append('0');
      }

      var3.append(var5);
      return var3.toString();
   }

   private boolean fillSourceBuffer() throws IOException {
      if (this.sourceString != null) {
         Kit.codeBug();
      }

      if (this.sourceEnd == this.sourceBuffer.length) {
         if (this.lineStart != 0 && !this.isMarkingComment()) {
            char[] var7 = this.sourceBuffer;
            int var8 = this.lineStart;
            System.arraycopy(var7, var8, var7, 0, this.sourceEnd - var8);
            int var9 = this.sourceEnd;
            int var10 = this.lineStart;
            this.sourceEnd = var9 - var10;
            this.sourceCursor -= var10;
            this.lineStart = 0;
         } else {
            char[] var5 = this.sourceBuffer;
            char[] var6 = new char[2 * var5.length];
            System.arraycopy(var5, 0, var6, 0, this.sourceEnd);
            this.sourceBuffer = var6;
         }
      }

      Reader var1 = this.sourceReader;
      char[] var2 = this.sourceBuffer;
      int var3 = this.sourceEnd;
      int var4 = var1.read(var2, var3, var2.length - var3);
      if (var4 < 0) {
         return false;
      } else {
         this.sourceEnd += var4;
         return true;
      }
   }

   private int getChar() throws IOException {
      return this.getChar(true);
   }

   private int getChar(boolean var1) throws IOException {
      int var2 = this.ungetCursor;
      if (var2 != 0) {
         ++this.cursor;
         int[] var9 = this.ungetBuffer;
         int var10 = var2 - 1;
         this.ungetCursor = var10;
         return var9[var10];
      } else {
         while(true) {
            char var6;
            while(true) {
               String var3 = this.sourceString;
               if (var3 != null) {
                  int var8 = this.sourceCursor;
                  if (var8 == this.sourceEnd) {
                     this.hitEOF = true;
                     return -1;
                  }

                  ++this.cursor;
                  this.sourceCursor = var8 + 1;
                  var6 = var3.charAt(var8);
               } else {
                  if (this.sourceCursor == this.sourceEnd && !this.fillSourceBuffer()) {
                     this.hitEOF = true;
                     return -1;
                  }

                  ++this.cursor;
                  char[] var4 = this.sourceBuffer;
                  int var5 = this.sourceCursor++;
                  var6 = var4[var5];
               }

               int var7 = this.lineEndChar;
               if (var7 < 0) {
                  break;
               }

               if (var7 != 13 || var6 != '\n') {
                  this.lineEndChar = -1;
                  this.lineStart = this.sourceCursor - 1;
                  ++this.lineno;
                  break;
               }

               this.lineEndChar = 10;
            }

            if (var6 <= 127) {
               if (var6 == '\n' || var6 == '\r') {
                  this.lineEndChar = var6;
                  return 10;
               }
            } else {
               if (var6 == '\ufeff') {
                  return var6;
               }

               if (var1 && isJSFormatChar(var6)) {
                  continue;
               }

               if (ScriptRuntime.isJSLineTerminator(var6)) {
                  this.lineEndChar = var6;
                  var6 = '\n';
               }
            }

            return var6;
         }
      }
   }

   private int getCharIgnoreLineEnd() throws IOException {
      int var1 = this.ungetCursor;
      if (var1 != 0) {
         ++this.cursor;
         int[] var7 = this.ungetBuffer;
         int var8 = var1 - 1;
         this.ungetCursor = var8;
         return var7[var8];
      } else {
         while(true) {
            String var2 = this.sourceString;
            char var5;
            if (var2 != null) {
               int var6 = this.sourceCursor;
               if (var6 == this.sourceEnd) {
                  this.hitEOF = true;
                  return -1;
               }

               ++this.cursor;
               this.sourceCursor = var6 + 1;
               var5 = var2.charAt(var6);
            } else {
               if (this.sourceCursor == this.sourceEnd && !this.fillSourceBuffer()) {
                  this.hitEOF = true;
                  return -1;
               }

               ++this.cursor;
               char[] var3 = this.sourceBuffer;
               int var4 = this.sourceCursor++;
               var5 = var3[var4];
            }

            if (var5 <= 127) {
               if (var5 == '\n' || var5 == '\r') {
                  this.lineEndChar = var5;
                  return 10;
               }
            } else {
               if (var5 == '\ufeff') {
                  return var5;
               }

               if (isJSFormatChar(var5)) {
                  continue;
               }

               if (ScriptRuntime.isJSLineTerminator(var5)) {
                  this.lineEndChar = var5;
                  var5 = '\n';
               }
            }

            return var5;
         }
      }
   }

   private String getStringFromBuffer() {
      this.tokenEnd = this.cursor;
      return new String(this.stringBuffer, 0, this.stringBufferTop);
   }

   private static boolean isAlpha(int var0) {
      if (var0 <= 90) {
         return 65 <= var0;
      } else {
         return 97 <= var0 && var0 <= 122;
      }
   }

   static boolean isDigit(int var0) {
      return 48 <= var0 && var0 <= 57;
   }

   private static boolean isJSFormatChar(int var0) {
      return var0 > 127 && Character.getType((char)var0) == 16;
   }

   static boolean isJSSpace(int var0) {
      if (var0 <= 127) {
         boolean var3;
         if (var0 != 32 && var0 != 9 && var0 != 12) {
            var3 = false;
            if (var0 != 11) {
               return var3;
            }
         }

         var3 = true;
         return var3;
      } else {
         boolean var1;
         if (var0 != 160 && var0 != 65279) {
            int var2 = Character.getType((char)var0);
            var1 = false;
            if (var2 != 12) {
               return var1;
            }
         }

         var1 = true;
         return var1;
      }
   }

   static boolean isKeyword(String var0, int var1, boolean var2) {
      return stringToKeyword(var0, var1, var2) != 0;
   }

   private boolean isMarkingComment() {
      return this.commentCursor != -1;
   }

   private void markCommentStart() {
      this.markCommentStart("");
   }

   private void markCommentStart(String var1) {
      if (this.parser.compilerEnv.isRecordingComments() && this.sourceReader != null) {
         this.commentPrefix = var1;
         this.commentCursor = -1 + this.sourceCursor;
      }

   }

   private boolean matchChar(int var1) throws IOException {
      int var2 = this.getCharIgnoreLineEnd();
      if (var2 == var1) {
         this.tokenEnd = this.cursor;
         return true;
      } else {
         this.ungetCharIgnoreLineEnd(var2);
         return false;
      }
   }

   private int peekChar() throws IOException {
      int var1 = this.getChar();
      this.ungetChar(var1);
      return var1;
   }

   private boolean readCDATA() throws IOException {
      int var1 = this.getChar();

      label21:
      do {
         while(var1 != -1) {
            this.addToString(var1);
            if (var1 == 93 && this.peekChar() == 93) {
               var1 = this.getChar();
               this.addToString(var1);
               continue label21;
            }

            var1 = this.getChar();
         }

         this.stringBufferTop = 0;
         this.string = null;
         this.parser.addError("msg.XML.bad.form");
         return false;
      } while(this.peekChar() != 62);

      this.addToString(this.getChar());
      return true;
   }

   private boolean readEntity() throws IOException {
      int var1 = 1;

      for(int var2 = this.getChar(); var2 != -1; var2 = this.getChar()) {
         this.addToString(var2);
         if (var2 != 60) {
            if (var2 == 62) {
               --var1;
               if (var1 == 0) {
                  return true;
               }
            }
         } else {
            ++var1;
         }
      }

      this.stringBufferTop = 0;
      this.string = null;
      this.parser.addError("msg.XML.bad.form");
      return false;
   }

   private boolean readPI() throws IOException {
      for(int var1 = this.getChar(); var1 != -1; var1 = this.getChar()) {
         this.addToString(var1);
         if (var1 == 63 && this.peekChar() == 62) {
            this.addToString(this.getChar());
            return true;
         }
      }

      this.stringBufferTop = 0;
      this.string = null;
      this.parser.addError("msg.XML.bad.form");
      return false;
   }

   private boolean readQuotedString(int var1) throws IOException {
      for(int var2 = this.getChar(); var2 != -1; var2 = this.getChar()) {
         this.addToString(var2);
         if (var2 == var1) {
            return true;
         }
      }

      this.stringBufferTop = 0;
      this.string = null;
      this.parser.addError("msg.XML.bad.form");
      return false;
   }

   private boolean readXmlComment() throws IOException {
      int var1 = this.getChar();

      label21:
      do {
         while(var1 != -1) {
            this.addToString(var1);
            if (var1 == 45 && this.peekChar() == 45) {
               var1 = this.getChar();
               this.addToString(var1);
               continue label21;
            }

            var1 = this.getChar();
         }

         this.stringBufferTop = 0;
         this.string = null;
         this.parser.addError("msg.XML.bad.form");
         return false;
      } while(this.peekChar() != 62);

      this.addToString(this.getChar());
      return true;
   }

   private void skipLine() throws IOException {
      int var1;
      do {
         var1 = this.getChar();
      } while(var1 != -1 && var1 != 10);

      this.ungetChar(var1);
      this.tokenEnd = this.cursor;
   }

   private static int stringToKeyword(String var0, int var1, boolean var2) {
      return var1 < 200 ? stringToKeywordForJS(var0) : stringToKeywordForES(var0, var2);
   }

   private static int stringToKeywordForES(String var0, boolean var1) {
      short var2;
      String var3;
      String var5;
      short var6;
      label284: {
         label283: {
            label282: {
               var2 = 0;
               label281:
               switch(var0.length()) {
               case 2:
                  var3 = var0;
                  char var4 = var0.charAt(1);
                  if (var4 == 'f') {
                     if (var0.charAt(0) == 'i') {
                        var6 = 113;
                        return var6 == 0 ? 0 : var6 & 255;
                     }
                  } else if (var4 == 'n') {
                     if (var0.charAt(0) == 'i') {
                        var6 = 52;
                        return var6 == 0 ? 0 : var6 & 255;
                     }
                  } else if (var4 == 'o' && var0.charAt(0) == 'd') {
                     var6 = 119;
                     return var6 == 0 ? 0 : var6 & 255;
                  }
                  break;
               case 3:
                  var3 = var0;
                  char var7 = var0.charAt(0);
                  if (var7 != 'f') {
                     if (var7 != 'l') {
                        if (var7 != 'n') {
                           if (var7 != 't') {
                              if (var7 == 'v' && var0.charAt(2) == 'r' && var0.charAt(1) == 'a') {
                                 var6 = 123;
                                 return var6 == 0 ? 0 : var6 & 255;
                              }
                           } else if (var0.charAt(2) == 'y' && var0.charAt(1) == 'r') {
                              var6 = 82;
                              return var6 == 0 ? 0 : var6 & 255;
                           }
                        } else if (var0.charAt(2) == 'w' && var0.charAt(1) == 'e') {
                           var6 = 30;
                           return var6 == 0 ? 0 : var6 & 255;
                        }
                     } else if (var0.charAt(2) == 't' && var0.charAt(1) == 'e') {
                        var6 = 154;
                        return var6 == 0 ? 0 : var6 & 255;
                     }
                  } else if (var0.charAt(2) == 'r' && var0.charAt(1) == 'o') {
                     var6 = 120;
                     return var6 == 0 ? 0 : var6 & 255;
                  }
                  break;
               case 4:
                  var3 = var0;
                  char var8 = var0.charAt(0);
                  if (var8 != 'c') {
                     if (var8 != 'e') {
                        if (var8 == 'n') {
                           var2 = 42;
                           var5 = "null";
                           break label284;
                        }

                        if (var8 != 't') {
                           if (var8 == 'v') {
                              var2 = 127;
                              var5 = "void";
                              break label284;
                           }

                           if (var8 == 'w') {
                              var2 = 124;
                              var5 = "with";
                              break label284;
                           }
                        } else {
                           char var10 = var0.charAt(3);
                           if (var10 == 'e') {
                              if (var0.charAt(2) == 'u' && var0.charAt(1) == 'r') {
                                 var6 = 45;
                                 return var6 == 0 ? 0 : var6 & 255;
                              }
                           } else if (var10 == 's' && var0.charAt(2) == 'i' && var0.charAt(1) == 'h') {
                              var6 = 43;
                              return var6 == 0 ? 0 : var6 & 255;
                           }
                        }
                     } else {
                        char var9 = var0.charAt(3);
                        if (var9 == 'e') {
                           if (var0.charAt(2) == 's' && var0.charAt(1) == 'l') {
                              var6 = 114;
                              return var6 == 0 ? 0 : var6 & 255;
                           }
                        } else if (var9 == 'm' && var0.charAt(2) == 'u' && var0.charAt(1) == 'n') {
                           var6 = 128;
                           return var6 == 0 ? 0 : var6 & 255;
                        }
                     }
                  } else if (var0.charAt(3) == 'e' && var0.charAt(2) == 's' && var0.charAt(1) == 'a') {
                     var6 = 116;
                     return var6 == 0 ? 0 : var6 & 255;
                  }
                  break;
               case 5:
                  var3 = var0;
                  char var11 = var0.charAt(2);
                  if (var11 != 'a') {
                     if (var11 != 'e') {
                        if (var11 == 'i') {
                           var2 = 118;
                           var5 = "while";
                           break label284;
                        }

                        if (var11 == 'l') {
                           var2 = 44;
                           var5 = "false";
                           break label284;
                        }

                        if (var11 == 'n') {
                           var2 = 155;
                           var5 = "const";
                           break label284;
                        }

                        if (var11 == 'p') {
                           var2 = 128;
                           var5 = "super";
                           break label284;
                        }

                        if (var11 == 'r') {
                           var2 = 50;
                           var5 = "throw";
                           break label284;
                        }

                        if (var11 == 't') {
                           var2 = 125;
                           var5 = "catch";
                           break label284;
                        }
                     } else {
                        char var13 = var0.charAt(0);
                        if (var13 == 'b') {
                           var2 = 121;
                           var5 = "break";
                           break label284;
                        }

                        if (var13 == 'y') {
                           var2 = 73;
                           var5 = "yield";
                           break label284;
                        }
                     }
                  } else {
                     char var12 = var0.charAt(0);
                     if (var12 == 'c') {
                        var2 = 128;
                        var5 = "class";
                        break label284;
                     }

                     if (var12 == 'a') {
                        var2 = 128;
                        var5 = "await";
                        break label284;
                     }
                  }
                  break;
               case 6:
                  var3 = var0;
                  char var14 = var0.charAt(1);
                  if (var14 != 'e') {
                     if (var14 == 'm') {
                        var2 = 128;
                        var5 = "import";
                        break label284;
                     }

                     if (var14 != 't') {
                        if (var14 != 'u') {
                           switch(var14) {
                           case 'w':
                              break label283;
                           case 'x':
                              var2 = 128;
                              var5 = "export";
                              break label284;
                           case 'y':
                              var2 = 32;
                              var5 = "typeof";
                              break label284;
                           }
                        }
                     } else if (var1) {
                        var2 = 128;
                        var5 = "static";
                        break label284;
                     }

                     if (var1) {
                        var2 = 128;
                        var5 = "public";
                        break label284;
                     }
                     break label283;
                  } else {
                     char var15 = var0.charAt(0);
                     if (var15 == 'd') {
                        var2 = 31;
                        var5 = "delete";
                        break label284;
                     }

                     if (var15 == 'r') {
                        var2 = 4;
                        var5 = "return";
                        break label284;
                     }
                     break;
                  }
               case 7:
                  var3 = var0;
                  char var16 = var0.charAt(1);
                  if (var16 != 'a') {
                     if (var16 == 'e') {
                        break label282;
                     }

                     if (var16 == 'i') {
                        var2 = 126;
                        var5 = "finally";
                        break label284;
                     }

                     if (var16 != 'r') {
                        if (var16 != 'x') {
                           break;
                        }
                     } else if (var1) {
                        var2 = 128;
                        var5 = "private";
                        break label284;
                     }

                     var2 = 128;
                     var5 = "extends";
                     break label284;
                  }

                  if (var1) {
                     var2 = 128;
                     var5 = "package";
                     break label284;
                  }
                  break label282;
               case 8:
                  var3 = var0;
                  char var17 = var0.charAt(0);
                  if (var17 == 'c') {
                     var2 = 122;
                     var5 = "continue";
                     break label284;
                  }

                  if (var17 == 'd') {
                     var2 = 161;
                     var5 = "debugger";
                     break label284;
                  }

                  if (var17 == 'f') {
                     var2 = 110;
                     var5 = "function";
                     break label284;
                  }
                  break;
               case 9:
                  var3 = var0;
                  char var18 = var0.charAt(0);
                  if (var18 == 'i' && var1) {
                     var2 = 128;
                     var5 = "interface";
                     break label284;
                  }

                  if (var18 == 'p' && var1) {
                     var2 = 128;
                     var5 = "protected";
                     break label284;
                  }
                  break;
               case 10:
                  var3 = var0;
                  char var19 = var0.charAt(1);
                  if (var19 == 'm' && var1) {
                     var2 = 128;
                     var5 = "implements";
                     break label284;
                  }

                  if (var19 == 'n') {
                     var2 = 53;
                     var5 = "instanceof";
                     break label284;
                  }
                  break;
               default:
                  var3 = var0;
               }

               var5 = null;
               break label284;
            }

            var2 = 117;
            var5 = "default";
            break label284;
         }

         var2 = 115;
         var5 = "switch";
      }

      if (var5 != null && var5 != var3 && !var5.equals(var3)) {
         var6 = 0;
      } else {
         var6 = var2;
      }

      return var6 == 0 ? 0 : var6 & 255;
   }

   private static int stringToKeywordForJS(String var0) {
      short var1;
      String var2;
      String var4;
      short var5;
      label327: {
         var1 = 0;
         label326:
         switch(var0.length()) {
         case 2:
            var2 = var0;
            char var3 = var0.charAt(1);
            if (var3 == 'f') {
               if (var0.charAt(0) == 'i') {
                  var5 = 113;
                  return var5 == 0 ? 0 : var5 & 255;
               }
            } else if (var3 == 'n') {
               if (var0.charAt(0) == 'i') {
                  var5 = 52;
                  return var5 == 0 ? 0 : var5 & 255;
               }
            } else if (var3 == 'o' && var0.charAt(0) == 'd') {
               var5 = 119;
               return var5 == 0 ? 0 : var5 & 255;
            }
            break;
         case 3:
            var2 = var0;
            char var6 = var0.charAt(0);
            if (var6 != 'f') {
               if (var6 != 'i') {
                  if (var6 != 'l') {
                     if (var6 != 'n') {
                        if (var6 != 't') {
                           if (var6 == 'v' && var0.charAt(2) == 'r' && var0.charAt(1) == 'a') {
                              var5 = 123;
                              return var5 == 0 ? 0 : var5 & 255;
                           }
                        } else if (var0.charAt(2) == 'y' && var0.charAt(1) == 'r') {
                           var5 = 82;
                           return var5 == 0 ? 0 : var5 & 255;
                        }
                     } else if (var0.charAt(2) == 'w' && var0.charAt(1) == 'e') {
                        var5 = 30;
                        return var5 == 0 ? 0 : var5 & 255;
                     }
                  } else if (var0.charAt(2) == 't' && var0.charAt(1) == 'e') {
                     var5 = 154;
                     return var5 == 0 ? 0 : var5 & 255;
                  }
               } else if (var0.charAt(2) == 't' && var0.charAt(1) == 'n') {
                  var5 = 128;
                  return var5 == 0 ? 0 : var5 & 255;
               }
            } else if (var0.charAt(2) == 'r' && var0.charAt(1) == 'o') {
               var5 = 120;
               return var5 == 0 ? 0 : var5 & 255;
            }
            break;
         case 4:
            var2 = var0;
            char var7 = var0.charAt(0);
            if (var7 == 'b') {
               var1 = 128;
               var4 = "byte";
               break label327;
            }

            if (var7 != 'c') {
               if (var7 != 'e') {
                  if (var7 == 'g') {
                     var1 = 128;
                     var4 = "goto";
                     break label327;
                  }

                  if (var7 == 'l') {
                     var1 = 128;
                     var4 = "long";
                     break label327;
                  }

                  if (var7 == 'n') {
                     var1 = 42;
                     var4 = "null";
                     break label327;
                  }

                  if (var7 != 't') {
                     if (var7 == 'v') {
                        var1 = 127;
                        var4 = "void";
                        break label327;
                     }

                     if (var7 == 'w') {
                        var1 = 124;
                        var4 = "with";
                        break label327;
                     }
                  } else {
                     char var10 = var0.charAt(3);
                     if (var10 == 'e') {
                        if (var0.charAt(2) == 'u' && var0.charAt(1) == 'r') {
                           var5 = 45;
                           return var5 == 0 ? 0 : var5 & 255;
                        }
                     } else if (var10 == 's' && var0.charAt(2) == 'i' && var0.charAt(1) == 'h') {
                        var5 = 43;
                        return var5 == 0 ? 0 : var5 & 255;
                     }
                  }
               } else {
                  char var9 = var0.charAt(3);
                  if (var9 == 'e') {
                     if (var0.charAt(2) == 's' && var0.charAt(1) == 'l') {
                        var5 = 114;
                        return var5 == 0 ? 0 : var5 & 255;
                     }
                  } else if (var9 == 'm' && var0.charAt(2) == 'u' && var0.charAt(1) == 'n') {
                     var5 = 128;
                     return var5 == 0 ? 0 : var5 & 255;
                  }
               }
            } else {
               char var8 = var0.charAt(3);
               if (var8 == 'e') {
                  if (var0.charAt(2) == 's' && var0.charAt(1) == 'a') {
                     var5 = 116;
                     return var5 == 0 ? 0 : var5 & 255;
                  }
               } else if (var8 == 'r' && var0.charAt(2) == 'a' && var0.charAt(1) == 'h') {
                  var5 = 128;
                  return var5 == 0 ? 0 : var5 & 255;
               }
            }
            break;
         case 5:
            var2 = var0;
            char var11 = var0.charAt(2);
            if (var11 == 'a') {
               var1 = 128;
               var4 = "class";
               break label327;
            }

            if (var11 != 'e') {
               if (var11 == 'i') {
                  var1 = 118;
                  var4 = "while";
                  break label327;
               }

               if (var11 == 'l') {
                  var1 = 44;
                  var4 = "false";
                  break label327;
               }

               if (var11 == 'r') {
                  var1 = 50;
                  var4 = "throw";
                  break label327;
               }

               if (var11 == 't') {
                  var1 = 125;
                  var4 = "catch";
                  break label327;
               }

               switch(var11) {
               case 'n':
                  char var13 = var0.charAt(0);
                  if (var13 == 'c') {
                     var1 = 155;
                     var4 = "const";
                     break label327;
                  }

                  if (var13 == 'f') {
                     var1 = 128;
                     var4 = "final";
                     break label327;
                  }
                  break label326;
               case 'o':
                  char var14 = var0.charAt(0);
                  if (var14 == 'f') {
                     var1 = 128;
                     var4 = "float";
                     break label327;
                  }

                  if (var14 == 's') {
                     var1 = 128;
                     var4 = "short";
                     break label327;
                  }
                  break label326;
               case 'p':
                  var1 = 128;
                  var4 = "super";
                  break label327;
               }
            } else {
               char var12 = var0.charAt(0);
               if (var12 == 'b') {
                  var1 = 121;
                  var4 = "break";
                  break label327;
               }

               if (var12 == 'y') {
                  var1 = 73;
                  var4 = "yield";
                  break label327;
               }
               break;
            }
         case 6:
            var2 = var0;
            char var15 = var0.charAt(1);
            if (var15 == 'a') {
               var1 = 128;
               var4 = "native";
               break label327;
            }

            if (var15 != 'e') {
               if (var15 == 'h') {
                  var1 = 128;
                  var4 = "throws";
                  break label327;
               }

               if (var15 == 'm') {
                  var1 = 128;
                  var4 = "import";
                  break label327;
               }

               if (var15 == 'o') {
                  var1 = 128;
                  var4 = "double";
                  break label327;
               }

               if (var15 == 't') {
                  var1 = 128;
                  var4 = "static";
                  break label327;
               }

               if (var15 == 'u') {
                  var1 = 128;
                  var4 = "public";
                  break label327;
               }

               switch(var15) {
               case 'w':
                  var1 = 115;
                  var4 = "switch";
                  break label327;
               case 'x':
                  var1 = 128;
                  var4 = "export";
                  break label327;
               case 'y':
                  var1 = 32;
                  var4 = "typeof";
                  break label327;
               }
            } else {
               char var16 = var0.charAt(0);
               if (var16 == 'd') {
                  var1 = 31;
                  var4 = "delete";
                  break label327;
               }

               if (var16 == 'r') {
                  var1 = 4;
                  var4 = "return";
                  break label327;
               }
               break;
            }
         case 7:
            var2 = var0;
            char var17 = var0.charAt(1);
            if (var17 == 'a') {
               var1 = 128;
               var4 = "package";
               break label327;
            }

            if (var17 == 'e') {
               var1 = 117;
               var4 = "default";
               break label327;
            }

            if (var17 == 'i') {
               var1 = 126;
               var4 = "finally";
               break label327;
            }

            if (var17 == 'o') {
               var1 = 128;
               var4 = "boolean";
               break label327;
            }

            if (var17 == 'r') {
               var1 = 128;
               var4 = "private";
               break label327;
            }

            if (var17 == 'x') {
               var1 = 128;
               var4 = "extends";
               break label327;
            }
            break;
         case 8:
            var2 = var0;
            char var18 = var0.charAt(0);
            if (var18 == 'a') {
               var1 = 128;
               var4 = "abstract";
               break label327;
            }

            if (var18 == 'f') {
               var1 = 110;
               var4 = "function";
               break label327;
            }

            if (var18 == 'v') {
               var1 = 128;
               var4 = "volatile";
               break label327;
            }

            if (var18 == 'c') {
               var1 = 122;
               var4 = "continue";
               break label327;
            }

            if (var18 == 'd') {
               var1 = 161;
               var4 = "debugger";
               break label327;
            }
            break;
         case 9:
            var2 = var0;
            char var19 = var0.charAt(0);
            if (var19 == 'i') {
               var1 = 128;
               var4 = "interface";
               break label327;
            }

            if (var19 == 'p') {
               var1 = 128;
               var4 = "protected";
               break label327;
            }

            if (var19 == 't') {
               var1 = 128;
               var4 = "transient";
               break label327;
            }
            break;
         case 10:
            var2 = var0;
            char var20 = var0.charAt(1);
            if (var20 == 'm') {
               var1 = 128;
               var4 = "implements";
               break label327;
            }

            if (var20 == 'n') {
               var1 = 53;
               var4 = "instanceof";
               break label327;
            }
            break;
         case 11:
         default:
            var2 = var0;
            break;
         case 12:
            var1 = 128;
            var2 = var0;
            var4 = "synchronized";
            break label327;
         }

         var4 = null;
      }

      if (var4 != null && var4 != var2 && !var4.equals(var2)) {
         var5 = 0;
      } else {
         var5 = var1;
      }

      return var5 == 0 ? 0 : var5 & 255;
   }

   private final String substring(int var1, int var2) {
      String var3 = this.sourceString;
      if (var3 != null) {
         return var3.substring(var1, var2);
      } else {
         int var4 = var2 - var1;
         return new String(this.sourceBuffer, var1, var4);
      }
   }

   private void ungetChar(int var1) {
      int var2 = this.ungetCursor;
      if (var2 != 0 && this.ungetBuffer[var2 - 1] == 10) {
         Kit.codeBug();
      }

      int[] var3 = this.ungetBuffer;
      int var4 = this.ungetCursor++;
      var3[var4] = var1;
      this.cursor += -1;
   }

   private void ungetCharIgnoreLineEnd(int var1) {
      int[] var2 = this.ungetBuffer;
      int var3 = this.ungetCursor++;
      var2[var3] = var1;
      this.cursor += -1;
   }

   final boolean eof() {
      return this.hitEOF;
   }

   final String getAndResetCurrentComment() {
      if (this.sourceString != null) {
         if (this.isMarkingComment()) {
            Kit.codeBug();
         }

         return this.sourceString.substring(this.tokenBeg, this.tokenEnd);
      } else {
         if (!this.isMarkingComment()) {
            Kit.codeBug();
         }

         StringBuilder var1 = new StringBuilder(this.commentPrefix);
         var1.append(this.sourceBuffer, this.commentCursor, this.getTokenLength() - this.commentPrefix.length());
         this.commentCursor = -1;
         return var1.toString();
      }
   }

   public Token.CommentType getCommentType() {
      return this.commentType;
   }

   public int getCursor() {
      return this.cursor;
   }

   int getFirstXMLToken() throws IOException {
      this.xmlOpenTagsCount = 0;
      this.xmlIsAttribute = false;
      this.xmlIsTagContent = false;
      if (!this.canUngetChar()) {
         return -1;
      } else {
         this.ungetChar(60);
         return this.getNextXMLToken();
      }
   }

   final String getLine() {
      int var1 = this.sourceCursor;
      int var2 = this.lineEndChar;
      int var5;
      if (var2 >= 0) {
         var5 = var1 - 1;
         if (var2 == 10 && this.charAt(var5 - 1) == 13) {
            --var5;
         }
      } else {
         int var3 = var1 - this.lineStart;

         while(true) {
            int var4 = this.charAt(var3 + this.lineStart);
            if (var4 == -1 || ScriptRuntime.isJSLineTerminator(var4)) {
               var5 = var3 + this.lineStart;
               break;
            }

            ++var3;
         }
      }

      return this.substring(this.lineStart, var5);
   }

   final String getLine(int var1, int[] var2) {
      if (var1 >= 0) {
         int var4 = this.cursor;
         if (var1 <= var4) {
            if (var2.length != 2) {
               throw new AssertionError();
            }

            int var5 = var4 + this.ungetCursor - var1;
            int var6 = this.sourceCursor;
            if (var5 > var6) {
               return null;
            }

            int var7 = 0;

            int var8;
            for(var8 = 0; var5 > 0; --var6) {
               if (var6 <= 0) {
                  throw new AssertionError();
               }

               int var13 = this.charAt(var6 - 1);
               if (ScriptRuntime.isJSLineTerminator(var13)) {
                  if (var13 == 10 && this.charAt(var6 - 2) == 13) {
                     --var5;
                     --var6;
                  }

                  ++var8;
                  var7 = var6 - 1;
               }

               --var5;
            }

            int var9 = 0;

            int var10;
            while(true) {
               var10 = 0;
               if (var6 <= 0) {
                  break;
               }

               if (ScriptRuntime.isJSLineTerminator(this.charAt(var6 - 1))) {
                  var10 = var6;
                  break;
               }

               --var6;
               ++var9;
            }

            int var11 = this.lineno - var8;
            byte var12;
            if (this.lineEndChar >= 0) {
               var12 = 1;
            } else {
               var12 = 0;
            }

            var2[0] = var11 + var12;
            var2[1] = var9;
            if (var8 == 0) {
               return this.getLine();
            }

            return this.substring(var10, var7);
         }
      }

      AssertionError var3 = new AssertionError();
      throw var3;
   }

   final int getLineno() {
      return this.lineno;
   }

   int getNextXMLToken() throws IOException {
      this.tokenBeg = this.cursor;
      this.stringBufferTop = 0;

      for(int var1 = this.getChar(); var1 != -1; var1 = this.getChar()) {
         if (this.xmlIsTagContent) {
            if (var1 != 9 && var1 != 10 && var1 != 13 && var1 != 32) {
               if (var1 != 34 && var1 != 39) {
                  if (var1 != 47) {
                     if (var1 == 123) {
                        this.ungetChar(var1);
                        this.string = this.getStringFromBuffer();
                        return 146;
                     }

                     if (var1 != 61) {
                        if (var1 != 62) {
                           this.addToString(var1);
                           this.xmlIsAttribute = false;
                        } else {
                           this.addToString(var1);
                           this.xmlIsTagContent = false;
                           this.xmlIsAttribute = false;
                        }
                     } else {
                        this.addToString(var1);
                        this.xmlIsAttribute = true;
                     }
                  } else {
                     this.addToString(var1);
                     if (this.peekChar() == 62) {
                        this.addToString(this.getChar());
                        this.xmlIsTagContent = false;
                        --this.xmlOpenTagsCount;
                     }
                  }
               } else {
                  this.addToString(var1);
                  if (!this.readQuotedString(var1)) {
                     return -1;
                  }
               }
            } else {
               this.addToString(var1);
            }

            if (!this.xmlIsTagContent && this.xmlOpenTagsCount == 0) {
               this.string = this.getStringFromBuffer();
               return 149;
            }
         } else if (var1 != 60) {
            if (var1 == 123) {
               this.ungetChar(var1);
               this.string = this.getStringFromBuffer();
               return 146;
            }

            this.addToString(var1);
         } else {
            this.addToString(var1);
            int var2 = this.peekChar();
            if (var2 != 33) {
               if (var2 != 47) {
                  if (var2 != 63) {
                     this.xmlIsTagContent = true;
                     ++this.xmlOpenTagsCount;
                  } else {
                     this.addToString(this.getChar());
                     if (!this.readPI()) {
                        return -1;
                     }
                  }
               } else {
                  this.addToString(this.getChar());
                  int var5 = this.xmlOpenTagsCount;
                  if (var5 == 0) {
                     this.stringBufferTop = 0;
                     this.string = null;
                     this.parser.addError("msg.XML.bad.form");
                     return -1;
                  }

                  this.xmlIsTagContent = true;
                  this.xmlOpenTagsCount = var5 - 1;
               }
            } else {
               this.addToString(this.getChar());
               int var3 = this.peekChar();
               if (var3 != 45) {
                  if (var3 != 91) {
                     if (!this.readEntity()) {
                        return -1;
                     }
                  } else {
                     this.addToString(this.getChar());
                     if (this.getChar() != 67 || this.getChar() != 68 || this.getChar() != 65 || this.getChar() != 84 || this.getChar() != 65 || this.getChar() != 91) {
                        this.stringBufferTop = 0;
                        this.string = null;
                        this.parser.addError("msg.XML.bad.form");
                        return -1;
                     }

                     this.addToString(67);
                     this.addToString(68);
                     this.addToString(65);
                     this.addToString(84);
                     this.addToString(65);
                     this.addToString(91);
                     if (!this.readCDATA()) {
                        return -1;
                     }
                  }
               } else {
                  this.addToString(this.getChar());
                  int var4 = this.getChar();
                  if (var4 != 45) {
                     this.stringBufferTop = 0;
                     this.string = null;
                     this.parser.addError("msg.XML.bad.form");
                     return -1;
                  }

                  this.addToString(var4);
                  if (!this.readXmlComment()) {
                     return -1;
                  }
               }
            }
         }
      }

      this.tokenEnd = this.cursor;
      this.stringBufferTop = 0;
      this.string = null;
      this.parser.addError("msg.XML.bad.form");
      return -1;
   }

   final double getNumber() {
      return this.number;
   }

   final int getOffset() {
      int var1 = this.sourceCursor - this.lineStart;
      if (this.lineEndChar >= 0) {
         --var1;
      }

      return var1;
   }

   final char getQuoteChar() {
      return (char)this.quoteChar;
   }

   final String getSourceString() {
      return this.sourceString;
   }

   final String getString() {
      return this.string;
   }

   final int getToken() throws IOException {
      int var1;
      do {
         var1 = this.getChar();
         if (var1 == -1) {
            int var41 = this.cursor;
            this.tokenBeg = var41 - 1;
            this.tokenEnd = var41;
            return 0;
         }

         if (var1 == 10) {
            this.dirtyLine = false;
            int var40 = this.cursor;
            this.tokenBeg = var40 - 1;
            this.tokenEnd = var40;
            return 1;
         }
      } while(isJSSpace(var1));

      if (var1 != 45) {
         this.dirtyLine = true;
      }

      int var2 = this.cursor;
      this.tokenBeg = var2 - 1;
      this.tokenEnd = var2;
      if (var1 == 64) {
         return 148;
      } else {
         boolean var3;
         boolean var4;
         if (var1 == 92) {
            var1 = this.getChar();
            if (var1 == 117) {
               var3 = true;
               var4 = true;
               this.stringBufferTop = 0;
            } else {
               this.ungetChar(var1);
               var1 = 92;
               var4 = false;
               var3 = false;
            }
         } else {
            var3 = Character.isJavaIdentifierStart((char)var1);
            var4 = false;
            if (var3) {
               this.stringBufferTop = 0;
               this.addToString(var1);
            }
         }

         if (var3) {
            boolean var32 = var4;

            while(true) {
               while(true) {
                  while(var4) {
                     int var38 = 0;

                     for(int var39 = 0; var39 != 4; ++var39) {
                        var38 = Kit.xDigitToInt(this.getChar(), var38);
                        if (var38 < 0) {
                           break;
                        }
                     }

                     if (var38 < 0) {
                        this.parser.addError("msg.invalid.escape");
                        return -1;
                     }

                     this.addToString(var38);
                     var4 = false;
                  }

                  int var33 = this.getChar();
                  if (var33 != 92) {
                     if (var33 == -1 || var33 == 65279 || !Character.isJavaIdentifierPart((char)var33)) {
                        this.ungetChar(var33);
                        String var34 = this.getStringFromBuffer();
                        if (!var32) {
                           int var35 = stringToKeyword(var34, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective());
                           if (var35 != 0) {
                              if ((var35 == 154 || var35 == 73) && this.parser.compilerEnv.getLanguageVersion() < 170) {
                                 String var36;
                                 if (var35 == 154) {
                                    var36 = "let";
                                 } else {
                                    var36 = "yield";
                                 }

                                 this.string = var36;
                                 var35 = 39;
                              }

                              this.string = (String)this.allStrings.intern(var34);
                              if (var35 != 128) {
                                 return var35;
                              }

                              if (this.parser.compilerEnv.getLanguageVersion() >= 200) {
                                 return var35;
                              }

                              if (!this.parser.compilerEnv.isReservedKeywordAsIdentifier()) {
                                 return var35;
                              }
                           }
                        } else if (isKeyword(var34, this.parser.compilerEnv.getLanguageVersion(), this.parser.inUseStrictDirective())) {
                           var34 = this.convertLastCharToHex(var34);
                        }

                        this.string = (String)this.allStrings.intern(var34);
                        return 39;
                     }

                     this.addToString(var33);
                  } else {
                     int var37 = this.getChar();
                     if (var37 != 117) {
                        this.parser.addError("msg.illegal.character", var37);
                        return -1;
                     }

                     var4 = true;
                     var32 = true;
                  }
               }
            }
         } else {
            boolean var5 = isDigit(var1);
            byte var6 = 48;
            if (!var5 && (var1 != 46 || !isDigit(this.peekChar()))) {
               if (var1 != 34 && var1 != 39 && var1 != 96) {
                  if (var1 != 33) {
                     if (var1 != 91) {
                        if (var1 != 37) {
                           if (var1 != 38) {
                              if (var1 != 93) {
                                 if (var1 != 94) {
                                    switch(var1) {
                                    case 40:
                                       return 88;
                                    case 41:
                                       return 89;
                                    case 42:
                                       if (this.matchChar(61)) {
                                          return 100;
                                       }

                                       return 23;
                                    case 43:
                                       if (this.matchChar(61)) {
                                          return 98;
                                       } else {
                                          if (this.matchChar(43)) {
                                             return 107;
                                          }

                                          return 21;
                                       }
                                    case 44:
                                       return 90;
                                    case 45:
                                       byte var29;
                                       if (this.matchChar(61)) {
                                          var29 = 99;
                                       } else if (this.matchChar(45)) {
                                          if (!this.dirtyLine && this.matchChar(62)) {
                                             this.markCommentStart("--");
                                             this.skipLine();
                                             this.commentType = Token.CommentType.HTML;
                                             return 162;
                                          }

                                          var29 = 108;
                                       } else {
                                          var29 = 22;
                                       }

                                       this.dirtyLine = true;
                                       return var29;
                                    case 46:
                                       if (this.matchChar(46)) {
                                          return 144;
                                       } else {
                                          if (this.matchChar(40)) {
                                             return 147;
                                          }

                                          return 109;
                                       }
                                    case 47:
                                       this.markCommentStart();
                                       if (this.matchChar(47)) {
                                          this.tokenBeg = this.cursor - 2;
                                          this.skipLine();
                                          this.commentType = Token.CommentType.LINE;
                                          return 162;
                                       } else if (this.matchChar(42)) {
                                          boolean var30 = false;
                                          this.tokenBeg = this.cursor - 2;
                                          if (this.matchChar(42)) {
                                             var30 = true;
                                             this.commentType = Token.CommentType.JSDOC;
                                          } else {
                                             this.commentType = Token.CommentType.BLOCK_COMMENT;
                                          }

                                          while(true) {
                                             int var31 = this.getChar();
                                             if (var31 == -1) {
                                                this.tokenEnd = this.cursor - 1;
                                                this.parser.addError("msg.unterminated.comment");
                                                return 162;
                                             }

                                             if (var31 == 42) {
                                                var30 = true;
                                             } else if (var31 == 47) {
                                                if (var30) {
                                                   this.tokenEnd = this.cursor;
                                                   return 162;
                                                }
                                             } else {
                                                this.tokenEnd = this.cursor;
                                                var30 = false;
                                             }
                                          }
                                       } else {
                                          if (this.matchChar(61)) {
                                             return 101;
                                          }

                                          return 24;
                                       }
                                    default:
                                       switch(var1) {
                                       case 58:
                                          if (this.matchChar(58)) {
                                             return 145;
                                          }

                                          return 104;
                                       case 59:
                                          return 83;
                                       case 60:
                                          if (this.matchChar(33)) {
                                             if (this.matchChar(45)) {
                                                if (this.matchChar(45)) {
                                                   this.tokenBeg = this.cursor - 4;
                                                   this.skipLine();
                                                   this.commentType = Token.CommentType.HTML;
                                                   return 162;
                                                }

                                                this.ungetCharIgnoreLineEnd(45);
                                             }

                                             this.ungetCharIgnoreLineEnd(33);
                                          }

                                          if (this.matchChar(60)) {
                                             if (this.matchChar(61)) {
                                                return 95;
                                             }

                                             return 18;
                                          } else {
                                             if (this.matchChar(61)) {
                                                return 15;
                                             }

                                             return 14;
                                          }
                                       case 61:
                                          if (this.matchChar(61)) {
                                             if (this.matchChar(61)) {
                                                return 46;
                                             }

                                             return 12;
                                          } else {
                                             if (this.matchChar(62)) {
                                                return 165;
                                             }

                                             return 91;
                                          }
                                       case 62:
                                          if (this.matchChar(62)) {
                                             if (this.matchChar(62)) {
                                                if (this.matchChar(61)) {
                                                   return 97;
                                                }

                                                return 20;
                                             } else {
                                                if (this.matchChar(61)) {
                                                   return 96;
                                                }

                                                return 19;
                                             }
                                          } else {
                                             if (this.matchChar(61)) {
                                                return 17;
                                             }

                                             return 16;
                                          }
                                       case 63:
                                          return 103;
                                       default:
                                          switch(var1) {
                                          case 123:
                                             return 86;
                                          case 124:
                                             if (this.matchChar(124)) {
                                                return 105;
                                             } else {
                                                if (this.matchChar(61)) {
                                                   return 92;
                                                }

                                                return 9;
                                             }
                                          case 125:
                                             return 87;
                                          case 126:
                                             return 27;
                                          default:
                                             this.parser.addError("msg.illegal.character", var1);
                                             return -1;
                                          }
                                       }
                                    }
                                 } else if (this.matchChar(61)) {
                                    return 93;
                                 } else {
                                    return 10;
                                 }
                              } else {
                                 return 85;
                              }
                           } else if (this.matchChar(38)) {
                              return 106;
                           } else if (this.matchChar(61)) {
                              return 94;
                           } else {
                              return 11;
                           }
                        } else if (this.matchChar(61)) {
                           return 102;
                        } else {
                           return 25;
                        }
                     } else {
                        return 84;
                     }
                  } else if (this.matchChar(61)) {
                     if (this.matchChar(61)) {
                        return 47;
                     } else {
                        return 13;
                     }
                  } else {
                     return 26;
                  }
               } else {
                  this.quoteChar = var1;
                  this.stringBufferTop = 0;
                  int var19 = this.getChar(false);

                  label492:
                  while(var19 != this.quoteChar) {
                     if (var19 == 10 || var19 == -1) {
                        this.ungetChar(var19);
                        this.tokenEnd = this.cursor;
                        this.parser.addError("msg.unterminated.string.lit");
                        return -1;
                     }

                     if (var19 == 92) {
                        var19 = this.getChar();
                        if (var19 == 10) {
                           var19 = this.getChar();
                           continue;
                        }

                        if (var19 != 98) {
                           if (var19 != 102) {
                              if (var19 != 110) {
                                 if (var19 != 114) {
                                    if (var19 != 120) {
                                       switch(var19) {
                                       case 116:
                                          var19 = 9;
                                          break;
                                       case 117:
                                          int var24 = this.stringBufferTop;
                                          this.addToString(117);
                                          int var25 = 0;

                                          for(int var26 = 0; var26 != 4; ++var26) {
                                             var19 = this.getChar();
                                             var25 = Kit.xDigitToInt(var19, var25);
                                             if (var25 < 0) {
                                                continue label492;
                                             }

                                             this.addToString(var19);
                                          }

                                          this.stringBufferTop = var24;
                                          var19 = var25;
                                          break;
                                       case 118:
                                          var19 = 11;
                                          break;
                                       default:
                                          if (var6 <= var19 && var19 < 56) {
                                             int var27 = var19 - 48;
                                             int var28 = this.getChar();
                                             if (var6 <= var28 && var28 < 56) {
                                                var27 = -48 + var28 + var27 * 8;
                                                var28 = this.getChar();
                                                if (var6 <= var28 && var28 < 56 && var27 <= 31) {
                                                   var27 = -48 + var28 + var27 * 8;
                                                   var28 = this.getChar();
                                                }
                                             }

                                             this.ungetChar(var28);
                                             var19 = var27;
                                          }
                                       }
                                    } else {
                                       var19 = this.getChar();
                                       int var21 = Kit.xDigitToInt(var19, 0);
                                       if (var21 < 0) {
                                          this.addToString(120);
                                          continue;
                                       }

                                       int var22 = var19;
                                       var19 = this.getChar();
                                       int var23 = Kit.xDigitToInt(var19, var21);
                                       if (var23 < 0) {
                                          this.addToString(120);
                                          this.addToString(var22);
                                          continue;
                                       }

                                       var19 = var23;
                                    }
                                 } else {
                                    var19 = 13;
                                 }
                              } else {
                                 var19 = 10;
                              }
                           } else {
                              var19 = 12;
                           }
                        } else {
                           var19 = 8;
                        }
                     }

                     this.addToString(var19);
                     var19 = this.getChar(false);
                  }

                  String var20 = this.getStringFromBuffer();
                  this.string = (String)this.allStrings.intern(var20);
                  return 41;
               }
            } else {
               this.stringBufferTop = 0;
               byte var7 = 10;
               this.isBinary = false;
               this.isOctal = false;
               this.isOldOctal = false;
               this.isHex = false;
               boolean var8;
               if (this.parser.compilerEnv.getLanguageVersion() >= 200) {
                  var8 = true;
               } else {
                  var8 = false;
               }

               if (var1 == var6) {
                  var1 = this.getChar();
                  if (var1 != 120 && var1 != 88) {
                     if (var8 && (var1 == 111 || var1 == 79)) {
                        var7 = 8;
                        this.isOctal = true;
                        var1 = this.getChar();
                     } else if (var8 && (var1 == 98 || var1 == 66)) {
                        var7 = 2;
                        this.isBinary = true;
                        var1 = this.getChar();
                     } else if (isDigit(var1)) {
                        var7 = 8;
                        this.isOldOctal = true;
                     } else {
                        this.addToString(var6);
                     }
                  } else {
                     var7 = 16;
                     this.isHex = true;
                     var1 = this.getChar();
                  }
               }

               boolean var9 = true;
               if (var7 == 16) {
                  while(Kit.xDigitToInt(var1, 0) >= 0) {
                     this.addToString(var1);
                     var1 = this.getChar();
                     var9 = false;
                  }
               } else {
                  while(var6 <= var1 && var1 <= 57) {
                     if (var7 == 8 && var1 >= 56) {
                        if (!this.isOldOctal) {
                           this.parser.addError("msg.caught.nfe");
                           return -1;
                        }

                        Parser var17 = this.parser;
                        String var18;
                        if (var1 == 56) {
                           var18 = "8";
                        } else {
                           var18 = "9";
                        }

                        var17.addWarning("msg.bad.octal.literal", var18);
                        var7 = 10;
                     } else if (var7 == 2 && var1 >= 50) {
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                     }

                     this.addToString(var1);
                     var1 = this.getChar();
                     var6 = 48;
                     var9 = false;
                  }
               }

               if (var9 && (this.isBinary || this.isOctal || this.isHex)) {
                  this.parser.addError("msg.caught.nfe");
                  return -1;
               } else {
                  boolean var10 = true;
                  int var11;
                  if (var7 == 10 && (var1 == 46 || var1 == 101 || var1 == 69)) {
                     if (var1 == 46) {
                        do {
                           this.addToString(var1);
                           var1 = this.getChar();
                        } while(isDigit(var1));
                     }

                     if (var1 != 101 && var1 != 69) {
                        var11 = var1;
                        var10 = false;
                     } else {
                        this.addToString(var1);
                        int var16 = this.getChar();
                        if (var16 == 43 || var16 == 45) {
                           this.addToString(var16);
                           var16 = this.getChar();
                        }

                        if (!isDigit(var16)) {
                           this.parser.addError("msg.missing.exponent");
                           return -1;
                        }

                        do {
                           this.addToString(var16);
                           var16 = this.getChar();
                        } while(isDigit(var16));

                        var11 = var16;
                        var10 = false;
                     }
                  } else {
                     var11 = var1;
                  }

                  this.ungetChar(var11);
                  String var12 = this.getStringFromBuffer();
                  this.string = var12;
                  double var13;
                  if (var7 == 10 && !var10) {
                     try {
                        var13 = Double.parseDouble(var12);
                     } catch (NumberFormatException var42) {
                        this.parser.addError("msg.caught.nfe");
                        return -1;
                     }
                  } else {
                     var13 = ScriptRuntime.stringPrefixToNumber(var12, 0, var7);
                  }

                  this.number = var13;
                  return 40;
               }
            }
         }
      }
   }

   public int getTokenBeg() {
      return this.tokenBeg;
   }

   public int getTokenEnd() {
      return this.tokenEnd;
   }

   public int getTokenLength() {
      return this.tokenEnd - this.tokenBeg;
   }

   final boolean isNumberBinary() {
      return this.isBinary;
   }

   final boolean isNumberHex() {
      return this.isHex;
   }

   final boolean isNumberOctal() {
      return this.isOctal;
   }

   final boolean isNumberOldOctal() {
      return this.isOldOctal;
   }

   boolean isXMLAttribute() {
      return this.xmlIsAttribute;
   }

   String readAndClearRegExpFlags() {
      String var1 = this.regExpFlags;
      this.regExpFlags = null;
      return var1;
   }

   void readRegExp(int var1) throws IOException {
      int var2 = this.tokenBeg;
      this.stringBufferTop = 0;
      if (var1 == 101) {
         this.addToString(61);
      } else if (var1 != 24) {
         Kit.codeBug();
      }

      boolean var3 = false;

      while(true) {
         int var4 = this.getChar();
         int var5 = var4;
         if (var4 == 47 && !var3) {
            int var6 = this.stringBufferTop;

            while(true) {
               while(!this.matchChar(103)) {
                  if (this.matchChar(105)) {
                     this.addToString(105);
                  } else if (this.matchChar(109)) {
                     this.addToString(109);
                  } else {
                     if (!this.matchChar(121)) {
                        this.tokenEnd = 2 + var2 + this.stringBufferTop;
                        if (isAlpha(this.peekChar())) {
                           this.parser.reportError("msg.invalid.re.flag");
                        }

                        this.string = new String(this.stringBuffer, 0, var6);
                        this.regExpFlags = new String(this.stringBuffer, var6, this.stringBufferTop - var6);
                        return;
                     }

                     this.addToString(121);
                  }
               }

               this.addToString(103);
            }
         }

         if (var4 == 10 || var4 == -1) {
            this.ungetChar(var4);
            this.tokenEnd = -1 + this.cursor;
            this.string = new String(this.stringBuffer, 0, this.stringBufferTop);
            this.parser.reportError("msg.unterminated.re.lit");
            return;
         }

         if (var4 == 92) {
            this.addToString(var4);
            var5 = this.getChar();
         } else if (var4 == 91) {
            var3 = true;
         } else if (var4 == 93) {
            var3 = false;
         }

         this.addToString(var5);
      }
   }

   String tokenToString(int var1) {
      return "";
   }
}
