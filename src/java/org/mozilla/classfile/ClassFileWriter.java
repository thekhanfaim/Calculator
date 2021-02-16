package org.mozilla.classfile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.UintMap;

public class ClassFileWriter {
   public static final short ACC_ABSTRACT = 1024;
   public static final short ACC_FINAL = 16;
   public static final short ACC_NATIVE = 256;
   public static final short ACC_PRIVATE = 2;
   public static final short ACC_PROTECTED = 4;
   public static final short ACC_PUBLIC = 1;
   public static final short ACC_STATIC = 8;
   public static final short ACC_SUPER = 32;
   public static final short ACC_SYNCHRONIZED = 32;
   public static final short ACC_TRANSIENT = 128;
   public static final short ACC_VOLATILE = 64;
   private static final boolean DEBUGCODE = false;
   private static final boolean DEBUGLABELS = false;
   private static final boolean DEBUGSTACK = false;
   private static final int ExceptionTableSize = 4;
   private static final int FileHeaderConstant = -889275714;
   private static final boolean GenerateStackMap = false;
   private static final int LineNumberTableSize = 16;
   private static final int MIN_FIXUP_TABLE_SIZE = 40;
   private static final int MIN_LABEL_TABLE_SIZE = 32;
   private static final int MajorVersion = 0;
   private static final int MinorVersion = 0;
   private static final int SuperBlockStartsSize = 4;
   private String generatedClassName;
   private ObjArray itsBootstrapMethods;
   private int itsBootstrapMethodsLength = 0;
   private byte[] itsCodeBuffer = new byte[256];
   private int itsCodeBufferTop;
   private ConstantPool itsConstantPool;
   private ClassFileMethod itsCurrentMethod;
   private ExceptionTableEntry[] itsExceptionTable;
   private int itsExceptionTableTop;
   private ObjArray itsFields = new ObjArray();
   private long[] itsFixupTable;
   private int itsFixupTableTop;
   private short itsFlags;
   private ObjArray itsInterfaces = new ObjArray();
   private UintMap itsJumpFroms = null;
   private int[] itsLabelTable;
   private int itsLabelTableTop;
   private int[] itsLineNumberTable;
   private int itsLineNumberTableTop;
   private short itsMaxLocals;
   private short itsMaxStack;
   private ObjArray itsMethods = new ObjArray();
   private short itsSourceFileNameIndex;
   private short itsStackTop;
   private int[] itsSuperBlockStarts = null;
   private int itsSuperBlockStartsTop = 0;
   private short itsSuperClassIndex;
   private short itsThisClassIndex;
   private ObjArray itsVarDescriptors;
   private char[] tmpCharBuffer = new char[64];

   static {
      // $FF: Couldn't be decompiled
   }

   public ClassFileWriter(String var1, String var2, String var3) {
      this.generatedClassName = var1;
      ConstantPool var4 = new ConstantPool(this);
      this.itsConstantPool = var4;
      this.itsThisClassIndex = var4.addClass(var1);
      this.itsSuperClassIndex = this.itsConstantPool.addClass(var2);
      if (var3 != null) {
         this.itsSourceFileNameIndex = this.itsConstantPool.addUtf8(var3);
      }

      this.itsFlags = 33;
   }

   // $FF: synthetic method
   static int access$410(ClassFileWriter var0) {
      int var1 = var0.itsExceptionTableTop--;
      return var1;
   }

   private void addLabelFixup(int var1, int var2) {
      if (var1 < 0) {
         int var3 = var1 & Integer.MAX_VALUE;
         if (var3 >= this.itsLabelTableTop) {
            throw new IllegalArgumentException("Bad label");
         } else {
            int var4 = this.itsFixupTableTop;
            long[] var5 = this.itsFixupTable;
            if (var5 == null || var4 == var5.length) {
               if (var5 == null) {
                  this.itsFixupTable = new long[40];
               } else {
                  long[] var6 = new long[2 * var5.length];
                  System.arraycopy(var5, 0, var6, 0, var4);
                  this.itsFixupTable = var6;
               }
            }

            this.itsFixupTableTop = var4 + 1;
            this.itsFixupTable[var4] = (long)var3 << 32 | (long)var2;
         }
      } else {
         throw new IllegalArgumentException("Bad label, no biscuit");
      }
   }

   private int addReservedCodeSpace(int var1) {
      if (this.itsCurrentMethod != null) {
         int var2 = this.itsCodeBufferTop;
         int var3 = var2 + var1;
         byte[] var4 = this.itsCodeBuffer;
         if (var3 > var4.length) {
            int var5 = 2 * var4.length;
            if (var3 > var5) {
               var5 = var3;
            }

            byte[] var6 = new byte[var5];
            System.arraycopy(var4, 0, var6, 0, var2);
            this.itsCodeBuffer = var6;
         }

         this.itsCodeBufferTop = var3;
         return var2;
      } else {
         throw new IllegalArgumentException("No method to add to");
      }
   }

   private void addSuperBlockStart(int var1) {
      if (GenerateStackMap) {
         int[] var2 = this.itsSuperBlockStarts;
         if (var2 == null) {
            this.itsSuperBlockStarts = new int[4];
         } else {
            int var3 = var2.length;
            int var4 = this.itsSuperBlockStartsTop;
            if (var3 == var4) {
               int[] var7 = new int[var4 * 2];
               System.arraycopy(var2, 0, var7, 0, var4);
               this.itsSuperBlockStarts = var7;
            }
         }

         int[] var5 = this.itsSuperBlockStarts;
         int var6 = this.itsSuperBlockStartsTop++;
         var5[var6] = var1;
      }

   }

   private void addToCodeBuffer(int var1) {
      int var2 = this.addReservedCodeSpace(1);
      this.itsCodeBuffer[var2] = (byte)var1;
   }

   private void addToCodeInt16(int var1) {
      int var2 = this.addReservedCodeSpace(2);
      putInt16(var1, this.itsCodeBuffer, var2);
   }

   private static char arrayTypeToName(int var0) {
      switch(var0) {
      case 4:
         return 'Z';
      case 5:
         return 'C';
      case 6:
         return 'F';
      case 7:
         return 'D';
      case 8:
         return 'B';
      case 9:
         return 'S';
      case 10:
         return 'I';
      case 11:
         return 'J';
      default:
         throw new IllegalArgumentException("bad operand");
      }
   }

   private static void badStack(int var0) {
      String var4;
      if (var0 < 0) {
         StringBuilder var1 = new StringBuilder();
         var1.append("Stack underflow: ");
         var1.append(var0);
         var4 = var1.toString();
      } else {
         StringBuilder var5 = new StringBuilder();
         var5.append("Too big stack: ");
         var5.append(var0);
         var4 = var5.toString();
      }

      throw new IllegalStateException(var4);
   }

   private static String bytecodeStr(int var0) {
      return "";
   }

   private static String classDescriptorToInternalName(String var0) {
      return var0.substring(1, var0.length() - 1);
   }

   public static String classNameToSignature(String var0) {
      int var1 = var0.length();
      int var2 = var1 + 1;
      char[] var3 = new char[var2 + 1];
      var3[0] = 'L';
      var3[var2] = ';';
      var0.getChars(0, var1, var3, 1);

      for(int var4 = 1; var4 != var2; ++var4) {
         if (var3[var4] == '.') {
            var3[var4] = '/';
         }
      }

      return new String(var3, 0, var2 + 1);
   }

   private int[] createInitialLocals() {
      int[] var1 = new int[this.itsMaxLocals];
      int var2 = 8 & this.itsCurrentMethod.getFlags();
      int var3 = 0;
      if (var2 == 0) {
         if ("<init>".equals(this.itsCurrentMethod.getName())) {
            int var18 = 0 + 1;
            var1[0] = 6;
            var3 = var18;
         } else {
            int var17 = 0 + 1;
            var1[0] = TypeInfo.OBJECT(this.itsThisClassIndex);
            var3 = var17;
         }
      }

      String var4 = this.itsCurrentMethod.getType();
      int var5 = var4.indexOf(40);
      int var6 = var4.indexOf(41);
      if (var5 == 0 && var6 >= 0) {
         int var8 = var5 + 1;
         StringBuilder var9 = new StringBuilder();

         while(true) {
            while(var8 < var6) {
               label51: {
                  char var10 = var4.charAt(var8);
                  if (var10 != 'F') {
                     if (var10 == 'L') {
                        int var14 = 1 + var4.indexOf(59, var8);
                        var9.append(var4.substring(var8, var14));
                        var8 = var14;
                        break label51;
                     }

                     if (var10 != 'S' && var10 != 'I' && var10 != 'J' && var10 != 'Z') {
                        if (var10 == '[') {
                           var9.append('[');
                           ++var8;
                           continue;
                        }

                        switch(var10) {
                        case 'B':
                        case 'C':
                        case 'D':
                           break;
                        default:
                           break label51;
                        }
                     }
                  }

                  var9.append(var4.charAt(var8));
                  ++var8;
               }

               int var12 = TypeInfo.fromType(descriptorToInternalName(var9.toString()), this.itsConstantPool);
               int var13 = var3 + 1;
               var1[var3] = var12;
               if (TypeInfo.isTwoWords(var12)) {
                  var3 = var13 + 1;
               } else {
                  var3 = var13;
               }

               var9.setLength(0);
            }

            return var1;
         }
      } else {
         IllegalArgumentException var7 = new IllegalArgumentException("bad method type");
         throw var7;
      }
   }

   private static String descriptorToInternalName(String var0) {
      char var1 = var0.charAt(0);
      if (var1 != 'F') {
         if (var1 == 'L') {
            return classDescriptorToInternalName(var0);
         }

         if (var1 != 'S' && var1 != 'V' && var1 != 'I' && var1 != 'J' && var1 != 'Z' && var1 != '[') {
            switch(var1) {
            case 'B':
            case 'C':
            case 'D':
               break;
            default:
               StringBuilder var2 = new StringBuilder();
               var2.append("bad descriptor:");
               var2.append(var0);
               throw new IllegalArgumentException(var2.toString());
            }
         }
      }

      return var0;
   }

   private void finalizeSuperBlockStarts() {
      if (GenerateStackMap) {
         for(int var1 = 0; var1 < this.itsExceptionTableTop; ++var1) {
            this.addSuperBlockStart(this.getLabelPC(this.itsExceptionTable[var1].itsHandlerLabel));
         }

         Arrays.sort(this.itsSuperBlockStarts, 0, this.itsSuperBlockStartsTop);
         int var2 = this.itsSuperBlockStarts[0];
         int var3 = 1;

         for(int var4 = 1; var4 < this.itsSuperBlockStartsTop; ++var4) {
            int[] var5 = this.itsSuperBlockStarts;
            int var6 = var5[var4];
            if (var2 != var6) {
               if (var3 != var4) {
                  var5[var3] = var6;
               }

               ++var3;
               var2 = var6;
            }
         }

         this.itsSuperBlockStartsTop = var3;
         if (this.itsSuperBlockStarts[var3 - 1] == this.itsCodeBufferTop) {
            this.itsSuperBlockStartsTop = var3 - 1;
         }
      }

   }

   private void fixLabelGotos() {
      byte[] var1 = this.itsCodeBuffer;

      for(int var2 = 0; var2 < this.itsFixupTableTop; ++var2) {
         long var3 = this.itsFixupTable[var2];
         int var5 = (int)(var3 >> 32);
         int var6 = (int)var3;
         int var7 = this.itsLabelTable[var5];
         if (var7 == -1) {
            throw new RuntimeException("unlocated label");
         }

         this.addSuperBlockStart(var7);
         this.itsJumpFroms.put(var7, var6 - 1);
         int var8 = var7 - (var6 - 1);
         if ((short)var8 != var8) {
            throw new ClassFileWriter.ClassFileFormatException("Program too complex: too big jump offset");
         }

         var1[var6] = (byte)(var8 >> 8);
         var1[var6 + 1] = (byte)var8;
      }

      this.itsFixupTableTop = 0;
   }

   static String getSlashedForm(String var0) {
      return var0.replace('.', '/');
   }

   private int getWriteSize() {
      if (this.itsSourceFileNameIndex != 0) {
         this.itsConstantPool.addUtf8("SourceFile");
      }

      int var1 = 2 + 2 + 2 + 2 + 2 + 0 + 8 + this.itsConstantPool.getWriteSize() + 2 * this.itsInterfaces.size();

      for(int var2 = 0; var2 < this.itsFields.size(); ++var2) {
         var1 += ((ClassFileField)((ClassFileField)this.itsFields.get(var2))).getWriteSize();
      }

      int var3 = var1 + 2;

      for(int var4 = 0; var4 < this.itsMethods.size(); ++var4) {
         var3 += ((ClassFileMethod)((ClassFileMethod)this.itsMethods.get(var4))).getWriteSize();
      }

      int var5 = var3 + 2;
      if (this.itsSourceFileNameIndex != 0) {
         var5 = 2 + 4 + var5 + 2;
      }

      if (this.itsBootstrapMethods != null) {
         var5 = 2 + 4 + var5 + 2 + this.itsBootstrapMethodsLength;
      }

      return var5;
   }

   private static int opcodeCount(int var0) {
      if (var0 != 254 && var0 != 255) {
         switch(var0) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 172:
         case 173:
         case 174:
         case 175:
         case 176:
         case 177:
            break;
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 167:
         case 168:
         case 169:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 184:
         case 185:
            return 1;
         case 132:
            return 2;
         case 170:
         case 171:
            return -1;
         default:
            switch(var0) {
            case 187:
            case 188:
            case 189:
            case 192:
            case 193:
            case 198:
            case 199:
            case 200:
            case 201:
               return 1;
            case 190:
            case 191:
            case 194:
            case 195:
            case 196:
            case 202:
               break;
            case 197:
               return 2;
            default:
               StringBuilder var1 = new StringBuilder();
               var1.append("Bad opcode: ");
               var1.append(var0);
               throw new IllegalArgumentException(var1.toString());
            }
         }
      }

      return 0;
   }

   private static int opcodeLength(int var0, boolean var1) {
      if (var0 != 254 && var0 != 255) {
         byte var2 = 2;
         switch(var0) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
            return 1;
         case 17:
         case 19:
         case 20:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 167:
         case 168:
            return 3;
         case 21:
         case 22:
         case 23:
         case 24:
         case 25:
         case 54:
         case 55:
         case 56:
         case 57:
         case 58:
         case 169:
            if (var1) {
               var2 = 3;
            }

            return var2;
         case 132:
            if (var1) {
               return 5;
            }

            return 3;
         default:
            switch(var0) {
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 190:
            case 191:
            case 194:
            case 195:
            case 196:
            case 202:
               return 1;
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 187:
            case 189:
            case 192:
            case 193:
            case 198:
            case 199:
               return 3;
            case 185:
            case 186:
            case 200:
            case 201:
               return 5;
            case 188:
               break;
            case 197:
               return 4;
            default:
               StringBuilder var3 = new StringBuilder();
               var3.append("Bad opcode: ");
               var3.append(var0);
               throw new IllegalArgumentException(var3.toString());
            }
         case 16:
         case 18:
            return var2;
         }
      } else {
         return 1;
      }
   }

   static int putInt16(int var0, byte[] var1, int var2) {
      var1[var2 + 0] = (byte)(var0 >>> 8);
      var1[var2 + 1] = (byte)var0;
      return var2 + 2;
   }

   static int putInt32(int var0, byte[] var1, int var2) {
      var1[var2 + 0] = (byte)(var0 >>> 24);
      var1[var2 + 1] = (byte)(var0 >>> 16);
      var1[var2 + 2] = (byte)(var0 >>> 8);
      var1[var2 + 3] = (byte)var0;
      return var2 + 4;
   }

   static int putInt64(long var0, byte[] var2, int var3) {
      int var4 = putInt32((int)(var0 >>> 32), var2, var3);
      return putInt32((int)var0, var2, var4);
   }

   private static int sizeOfParameters(String var0) {
      int var1 = var0.length();
      int var2 = var0.lastIndexOf(41);
      if (3 <= var1 && var0.charAt(0) == '(') {
         byte var7 = 1;
         if (var7 <= var2 && var2 + 1 < var1) {
            boolean var8 = true;
            int var9 = 1;
            int var10 = 0;
            int var11 = 0;

            label133:
            while(var9 != var2) {
               label155: {
                  char var13 = var0.charAt(var9);
                  if (var13 != 'F') {
                     if (var13 == 'L') {
                        break label155;
                     }

                     if (var13 != 'S' && var13 != 'I') {
                        label150: {
                           if (var13 != 'J') {
                              if (var13 == 'Z') {
                                 break label150;
                              }

                              if (var13 == '[') {
                                 ++var9;

                                 char var16;
                                 for(var16 = var0.charAt(var9); var16 == '['; var16 = var0.charAt(var9)) {
                                    ++var9;
                                 }

                                 if (var16 != 'F') {
                                    if (var16 == 'L') {
                                       break label155;
                                    }

                                    if (var16 != 'S' && var16 != 'Z' && var16 != 'I' && var16 != 'J') {
                                       switch(var16) {
                                       case 'B':
                                       case 'C':
                                       case 'D':
                                          break;
                                       default:
                                          var8 = false;
                                          break label133;
                                       }
                                    }
                                 }

                                 --var10;
                                 ++var11;
                                 ++var9;
                                 continue;
                              }

                              switch(var13) {
                              case 'B':
                              case 'C':
                                 break label150;
                              case 'D':
                                 break;
                              default:
                                 var8 = false;
                                 break label133;
                              }
                           }

                           --var10;
                        }
                     }
                  }

                  --var10;
                  ++var11;
                  ++var9;
                  var7 = 1;
                  continue;
               }

               --var10;
               ++var11;
               int var14 = var9 + var7;
               int var15 = var0.indexOf(59, var14);
               if (var14 + 1 > var15 || var15 >= var2) {
                  var8 = false;
                  break;
               }

               var9 = var15 + 1;
               var7 = 1;
            }

            if (var8) {
               label156: {
                  char var12 = var0.charAt(var2 + 1);
                  if (var12 != 'F' && var12 != 'L' && var12 != 'S') {
                     if (var12 == 'V') {
                        break label156;
                     }

                     if (var12 != 'I') {
                        label154: {
                           if (var12 != 'J') {
                              if (var12 == 'Z' || var12 == '[') {
                                 break label154;
                              }

                              switch(var12) {
                              case 'B':
                              case 'C':
                                 break label154;
                              case 'D':
                                 break;
                              default:
                                 var8 = false;
                                 break label156;
                              }
                           }

                           ++var10;
                        }
                     }
                  }

                  ++var10;
               }

               if (var8) {
                  return var11 << 16 | '\uffff' & var10;
               }
            }
         }
      }

      StringBuilder var3 = new StringBuilder();
      var3.append("Bad parameter signature: ");
      var3.append(var0);
      IllegalArgumentException var6 = new IllegalArgumentException(var3.toString());
      throw var6;
   }

   private static int stackChange(int var0) {
      if (var0 != 254 && var0 != 255) {
         switch(var0) {
         case 0:
         case 47:
         case 49:
         case 95:
         case 116:
         case 117:
         case 118:
         case 119:
         case 132:
         case 134:
         case 138:
         case 139:
         case 143:
         case 145:
         case 146:
         case 147:
         case 167:
         case 169:
         case 177:
         case 178:
         case 179:
         case 184:
         case 186:
         case 188:
         case 189:
         case 190:
         case 192:
         case 193:
         case 196:
         case 200:
         case 202:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 11:
         case 12:
         case 13:
         case 16:
         case 17:
         case 18:
         case 19:
         case 21:
         case 23:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 34:
         case 35:
         case 36:
         case 37:
         case 42:
         case 43:
         case 44:
         case 45:
         case 89:
         case 90:
         case 91:
         case 133:
         case 135:
         case 140:
         case 141:
         case 168:
         case 187:
         case 197:
         case 201:
            return 1;
         case 9:
         case 10:
         case 14:
         case 15:
         case 20:
         case 22:
         case 24:
         case 30:
         case 31:
         case 32:
         case 33:
         case 38:
         case 39:
         case 40:
         case 41:
         case 92:
         case 93:
         case 94:
            return 2;
         case 46:
         case 48:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 56:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 67:
         case 68:
         case 69:
         case 70:
         case 75:
         case 76:
         case 77:
         case 78:
         case 87:
         case 96:
         case 98:
         case 100:
         case 102:
         case 104:
         case 106:
         case 108:
         case 110:
         case 112:
         case 114:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 128:
         case 130:
         case 136:
         case 137:
         case 142:
         case 144:
         case 149:
         case 150:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 170:
         case 171:
         case 172:
         case 174:
         case 176:
         case 180:
         case 181:
         case 182:
         case 183:
         case 185:
         case 191:
         case 194:
         case 195:
         case 198:
         case 199:
            return -1;
         case 55:
         case 57:
         case 63:
         case 64:
         case 65:
         case 66:
         case 71:
         case 72:
         case 73:
         case 74:
         case 88:
         case 97:
         case 99:
         case 101:
         case 103:
         case 105:
         case 107:
         case 109:
         case 111:
         case 113:
         case 115:
         case 127:
         case 129:
         case 131:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 173:
         case 175:
            return -2;
         case 79:
         case 81:
         case 83:
         case 84:
         case 85:
         case 86:
         case 148:
         case 151:
         case 152:
            return -3;
         case 80:
         case 82:
            return -4;
         default:
            StringBuilder var1 = new StringBuilder();
            var1.append("Bad opcode: ");
            var1.append(var0);
            throw new IllegalArgumentException(var1.toString());
         }
      }

      return 0;
   }

   private void xop(int var1, int var2, int var3) {
      if (var3 != 0) {
         if (var3 != 1) {
            if (var3 != 2) {
               if (var3 != 3) {
                  this.add(var2, var3);
               } else {
                  this.add(var1 + 3);
               }
            } else {
               this.add(var1 + 2);
            }
         } else {
            this.add(var1 + 1);
         }
      } else {
         this.add(var1);
      }
   }

   public int acquireLabel() {
      int var1 = this.itsLabelTableTop;
      int[] var2 = this.itsLabelTable;
      if (var2 == null || var1 == var2.length) {
         if (var2 == null) {
            this.itsLabelTable = new int[32];
         } else {
            int[] var3 = new int[2 * var2.length];
            System.arraycopy(var2, 0, var3, 0, var1);
            this.itsLabelTable = var3;
         }
      }

      this.itsLabelTableTop = var1 + 1;
      this.itsLabelTable[var1] = -1;
      return Integer.MIN_VALUE | var1;
   }

   public void add(int var1) {
      if (opcodeCount(var1) != 0) {
         throw new IllegalArgumentException("Unexpected operands");
      } else {
         int var2 = this.itsStackTop + stackChange(var1);
         if (var2 < 0 || 32767 < var2) {
            badStack(var2);
         }

         this.addToCodeBuffer(var1);
         this.itsStackTop = (short)var2;
         if (var2 > this.itsMaxStack) {
            this.itsMaxStack = (short)var2;
         }

         if (var1 == 191) {
            this.addSuperBlockStart(this.itsCodeBufferTop);
         }

      }
   }

   public void add(int var1, int var2) {
      int var3 = this.itsStackTop + stackChange(var1);
      if (var3 < 0 || 32767 < var3) {
         badStack(var3);
      }

      if (var1 != 180 && var1 != 181) {
         if (var1 == 188) {
            if (var2 < 0 || var2 >= 256) {
               throw new IllegalArgumentException("out of range index");
            }

            this.addToCodeBuffer(var1);
            this.addToCodeBuffer(var2);
         } else {
            label154: {
               if (var1 != 198 && var1 != 199) {
                  label114:
                  switch(var1) {
                  case 16:
                     if ((byte)var2 != var2) {
                        throw new IllegalArgumentException("out of range byte");
                     }

                     this.addToCodeBuffer(var1);
                     this.addToCodeBuffer((byte)var2);
                     break label154;
                  case 17:
                     if ((short)var2 != var2) {
                        throw new IllegalArgumentException("out of range short");
                     }

                     this.addToCodeBuffer(var1);
                     this.addToCodeInt16(var2);
                     break label154;
                  case 18:
                  case 19:
                  case 20:
                     if (var2 < 0 || var2 >= 65536) {
                        throw new ClassFileWriter.ClassFileFormatException("out of range index");
                     }

                     if (var2 < 256 && var1 != 19 && var1 != 20) {
                        this.addToCodeBuffer(var1);
                        this.addToCodeBuffer(var2);
                     } else {
                        if (var1 == 18) {
                           this.addToCodeBuffer(19);
                        } else {
                           this.addToCodeBuffer(var1);
                        }

                        this.addToCodeInt16(var2);
                     }
                     break label154;
                  default:
                     switch(var1) {
                     case 54:
                     case 55:
                     case 56:
                     case 57:
                     case 58:
                        break;
                     default:
                        switch(var1) {
                        case 153:
                        case 154:
                        case 155:
                        case 156:
                        case 157:
                        case 158:
                        case 159:
                        case 160:
                        case 161:
                        case 162:
                        case 163:
                        case 164:
                        case 165:
                        case 166:
                        case 168:
                           break label114;
                        case 167:
                           this.addSuperBlockStart(3 + this.itsCodeBufferTop);
                           break label114;
                        case 169:
                           break;
                        default:
                           throw new IllegalArgumentException("Unexpected opcode for 1 operand");
                        }
                     }
                  case 21:
                  case 22:
                  case 23:
                  case 24:
                  case 25:
                     if (var2 >= 0 && 65536 > var2) {
                        if (var2 >= 256) {
                           this.addToCodeBuffer(196);
                           this.addToCodeBuffer(var1);
                           this.addToCodeInt16(var2);
                        } else {
                           this.addToCodeBuffer(var1);
                           this.addToCodeBuffer(var2);
                        }
                        break label154;
                     }

                     throw new ClassFileWriter.ClassFileFormatException("out of range variable");
                  }
               }

               if ((var2 & Integer.MIN_VALUE) != Integer.MIN_VALUE && (var2 < 0 || var2 > 65535)) {
                  throw new IllegalArgumentException("Bad label for branch");
               }

               int var4 = this.itsCodeBufferTop;
               this.addToCodeBuffer(var1);
               if ((var2 & Integer.MIN_VALUE) != Integer.MIN_VALUE) {
                  this.addToCodeInt16(var2);
                  int var6 = var2 + var4;
                  this.addSuperBlockStart(var6);
                  this.itsJumpFroms.put(var6, var4);
               } else {
                  int var5 = this.getLabelPC(var2);
                  if (var5 != -1) {
                     this.addToCodeInt16(var5 - var4);
                     this.addSuperBlockStart(var5);
                     this.itsJumpFroms.put(var5, var4);
                  } else {
                     this.addLabelFixup(var2, var4 + 1);
                     this.addToCodeInt16(0);
                  }
               }
            }
         }
      } else {
         if (var2 < 0 || var2 >= 65536) {
            throw new IllegalArgumentException("out of range field");
         }

         this.addToCodeBuffer(var1);
         this.addToCodeInt16(var2);
      }

      this.itsStackTop = (short)var3;
      if (var3 > this.itsMaxStack) {
         this.itsMaxStack = (short)var3;
      }

   }

   public void add(int var1, int var2, int var3) {
      int var4 = this.itsStackTop + stackChange(var1);
      if (var4 < 0 || 32767 < var4) {
         badStack(var4);
      }

      if (var1 == 132) {
         if (var2 < 0 || 65536 <= var2) {
            throw new ClassFileWriter.ClassFileFormatException("out of range variable");
         }

         if (var3 < 0 || 65536 <= var3) {
            throw new ClassFileWriter.ClassFileFormatException("out of range increment");
         }

         if (var2 <= 255 && var3 >= -128 && var3 <= 127) {
            this.addToCodeBuffer(132);
            this.addToCodeBuffer(var2);
            this.addToCodeBuffer(var3);
         } else {
            this.addToCodeBuffer(196);
            this.addToCodeBuffer(132);
            this.addToCodeInt16(var2);
            this.addToCodeInt16(var3);
         }
      } else {
         label70: {
            if (var1 != 197) {
               throw new IllegalArgumentException("Unexpected opcode for 2 operands");
            }

            if (var2 >= 0 && var2 < 65536) {
               if (var3 >= 0 && var3 < 256) {
                  this.addToCodeBuffer(197);
                  this.addToCodeInt16(var2);
                  this.addToCodeBuffer(var3);
                  break label70;
               }

               throw new IllegalArgumentException("out of range dimensions");
            }

            throw new IllegalArgumentException("out of range index");
         }
      }

      this.itsStackTop = (short)var4;
      if (var4 > this.itsMaxStack) {
         this.itsMaxStack = (short)var4;
      }

   }

   public void add(int var1, String var2) {
      int var3 = this.itsStackTop + stackChange(var1);
      if (var3 < 0 || 32767 < var3) {
         badStack(var3);
      }

      if (var1 != 187 && var1 != 189 && var1 != 192 && var1 != 193) {
         throw new IllegalArgumentException("bad opcode for class reference");
      } else {
         short var4 = this.itsConstantPool.addClass(var2);
         this.addToCodeBuffer(var1);
         this.addToCodeInt16(var4);
         this.itsStackTop = (short)var3;
         if (var3 > this.itsMaxStack) {
            this.itsMaxStack = (short)var3;
         }

      }
   }

   public void add(int var1, String var2, String var3, String var4) {
      int var5 = this.itsStackTop + stackChange(var1);
      char var6 = var4.charAt(0);
      byte var7;
      if (var6 != 'J' && var6 != 'D') {
         var7 = 1;
      } else {
         var7 = 2;
      }

      int var8;
      switch(var1) {
      case 178:
      case 180:
         var8 = var5 + var7;
         break;
      case 179:
      case 181:
         var8 = var5 - var7;
         break;
      default:
         throw new IllegalArgumentException("bad opcode for field reference");
      }

      if (var8 < 0 || 32767 < var8) {
         badStack(var8);
      }

      short var9 = this.itsConstantPool.addFieldRef(var2, var3, var4);
      this.addToCodeBuffer(var1);
      this.addToCodeInt16(var9);
      this.itsStackTop = (short)var8;
      if (var8 > this.itsMaxStack) {
         this.itsMaxStack = (short)var8;
      }

   }

   public void addALoad(int var1) {
      this.xop(42, 25, var1);
   }

   public void addAStore(int var1) {
      this.xop(75, 58, var1);
   }

   public void addDLoad(int var1) {
      this.xop(38, 24, var1);
   }

   public void addDStore(int var1) {
      this.xop(71, 57, var1);
   }

   public void addExceptionHandler(int var1, int var2, int var3, String var4) {
      if ((var1 & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
         if ((var2 & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
            if ((var3 & Integer.MIN_VALUE) == Integer.MIN_VALUE) {
               short var5;
               if (var4 == null) {
                  var5 = 0;
               } else {
                  var5 = this.itsConstantPool.addClass(var4);
               }

               ExceptionTableEntry var6 = new ExceptionTableEntry(var1, var2, var3, var5);
               int var7 = this.itsExceptionTableTop;
               if (var7 == 0) {
                  this.itsExceptionTable = new ExceptionTableEntry[4];
               } else {
                  ExceptionTableEntry[] var8 = this.itsExceptionTable;
                  if (var7 == var8.length) {
                     ExceptionTableEntry[] var9 = new ExceptionTableEntry[var7 * 2];
                     System.arraycopy(var8, 0, var9, 0, var7);
                     this.itsExceptionTable = var9;
                  }
               }

               this.itsExceptionTable[var7] = var6;
               this.itsExceptionTableTop = var7 + 1;
            } else {
               throw new IllegalArgumentException("Bad handlerLabel");
            }
         } else {
            throw new IllegalArgumentException("Bad endLabel");
         }
      } else {
         throw new IllegalArgumentException("Bad startLabel");
      }
   }

   public void addFLoad(int var1) {
      this.xop(34, 23, var1);
   }

   public void addFStore(int var1) {
      this.xop(67, 56, var1);
   }

   public void addField(String var1, String var2, short var3) {
      short var4 = this.itsConstantPool.addUtf8(var1);
      short var5 = this.itsConstantPool.addUtf8(var2);
      this.itsFields.add(new ClassFileField(var4, var5, var3));
   }

   public void addField(String var1, String var2, short var3, double var4) {
      ClassFileField var6 = new ClassFileField(this.itsConstantPool.addUtf8(var1), this.itsConstantPool.addUtf8(var2), var3);
      var6.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)2, this.itsConstantPool.addConstant(var4));
      this.itsFields.add(var6);
   }

   public void addField(String var1, String var2, short var3, int var4) {
      ClassFileField var5 = new ClassFileField(this.itsConstantPool.addUtf8(var1), this.itsConstantPool.addUtf8(var2), var3);
      var5.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)0, this.itsConstantPool.addConstant(var4));
      this.itsFields.add(var5);
   }

   public void addField(String var1, String var2, short var3, long var4) {
      ClassFileField var6 = new ClassFileField(this.itsConstantPool.addUtf8(var1), this.itsConstantPool.addUtf8(var2), var3);
      var6.setAttributes(this.itsConstantPool.addUtf8("ConstantValue"), (short)0, (short)2, this.itsConstantPool.addConstant(var4));
      this.itsFields.add(var6);
   }

   public void addILoad(int var1) {
      this.xop(26, 21, var1);
   }

   public void addIStore(int var1) {
      this.xop(59, 54, var1);
   }

   public void addInterface(String var1) {
      short var2 = this.itsConstantPool.addClass(var1);
      this.itsInterfaces.add(var2);
   }

   public void addInvoke(int var1, String var2, String var3, String var4) {
      int var5 = sizeOfParameters(var4);
      int var6 = var5 >>> 16;
      int var7 = (short)var5 + this.itsStackTop + stackChange(var1);
      if (var7 < 0 || 32767 < var7) {
         badStack(var7);
      }

      switch(var1) {
      case 182:
      case 183:
      case 184:
      case 185:
         this.addToCodeBuffer(var1);
         if (var1 == 185) {
            this.addToCodeInt16(this.itsConstantPool.addInterfaceMethodRef(var2, var3, var4));
            this.addToCodeBuffer(var6 + 1);
            this.addToCodeBuffer(0);
         } else {
            this.addToCodeInt16(this.itsConstantPool.addMethodRef(var2, var3, var4));
         }

         this.itsStackTop = (short)var7;
         if (var7 > this.itsMaxStack) {
            this.itsMaxStack = (short)var7;
         }

         return;
      default:
         throw new IllegalArgumentException("bad opcode for method reference");
      }
   }

   public void addInvokeDynamic(String var1, String var2, ClassFileWriter.MHandle var3, Object... var4) {
      if (MajorVersion < 51) {
         throw new RuntimeException("Please build and run with JDK 1.7 for invokedynamic support");
      } else {
         int var5 = (short)sizeOfParameters(var2) + this.itsStackTop;
         if (var5 < 0 || 32767 < var5) {
            badStack(var5);
         }

         ClassFileWriter.BootstrapEntry var6 = new ClassFileWriter.BootstrapEntry(var3, var4);
         if (this.itsBootstrapMethods == null) {
            this.itsBootstrapMethods = new ObjArray();
         }

         int var7 = this.itsBootstrapMethods.indexOf(var6);
         if (var7 == -1) {
            var7 = this.itsBootstrapMethods.size();
            this.itsBootstrapMethods.add(var6);
            this.itsBootstrapMethodsLength += var6.code.length;
         }

         short var8 = this.itsConstantPool.addInvokeDynamic(var1, var2, var7);
         this.addToCodeBuffer(186);
         this.addToCodeInt16(var8);
         this.addToCodeInt16(0);
         this.itsStackTop = (short)var5;
         if (var5 > this.itsMaxStack) {
            this.itsMaxStack = (short)var5;
         }

      }
   }

   public void addLLoad(int var1) {
      this.xop(30, 22, var1);
   }

   public void addLStore(int var1) {
      this.xop(63, 55, var1);
   }

   public void addLineNumberEntry(short var1) {
      if (this.itsCurrentMethod != null) {
         int var2 = this.itsLineNumberTableTop;
         if (var2 == 0) {
            this.itsLineNumberTable = new int[16];
         } else {
            int[] var3 = this.itsLineNumberTable;
            if (var2 == var3.length) {
               int[] var4 = new int[var2 * 2];
               System.arraycopy(var3, 0, var4, 0, var2);
               this.itsLineNumberTable = var4;
            }
         }

         this.itsLineNumberTable[var2] = var1 + (this.itsCodeBufferTop << 16);
         this.itsLineNumberTableTop = var2 + 1;
      } else {
         throw new IllegalArgumentException("No method to stop");
      }
   }

   public void addLoadConstant(double var1) {
      this.add(20, this.itsConstantPool.addConstant(var1));
   }

   public void addLoadConstant(float var1) {
      this.add(18, this.itsConstantPool.addConstant(var1));
   }

   public void addLoadConstant(int var1) {
      if (var1 != 0) {
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  if (var1 != 4) {
                     if (var1 != 5) {
                        this.add(18, this.itsConstantPool.addConstant(var1));
                     } else {
                        this.add(8);
                     }
                  } else {
                     this.add(7);
                  }
               } else {
                  this.add(6);
               }
            } else {
               this.add(5);
            }
         } else {
            this.add(4);
         }
      } else {
         this.add(3);
      }
   }

   public void addLoadConstant(long var1) {
      this.add(20, this.itsConstantPool.addConstant(var1));
   }

   public void addLoadConstant(String var1) {
      this.add(18, this.itsConstantPool.addConstant(var1));
   }

   public void addLoadThis() {
      this.add(42);
   }

   public void addPush(double var1) {
      if (var1 == 0.0D) {
         this.add(14);
         if (1.0D / var1 < 0.0D) {
            this.add(119);
            return;
         }
      } else {
         if (var1 != 1.0D && var1 != -1.0D) {
            this.addLoadConstant(var1);
            return;
         }

         this.add(15);
         if (var1 < 0.0D) {
            this.add(119);
         }
      }

   }

   public void addPush(int var1) {
      if ((byte)var1 == var1) {
         if (var1 == -1) {
            this.add(2);
         } else if (var1 >= 0 && var1 <= 5) {
            this.add((byte)(var1 + 3));
         } else {
            this.add(16, (byte)var1);
         }
      } else if ((short)var1 == var1) {
         this.add(17, (short)var1);
      } else {
         this.addLoadConstant(var1);
      }
   }

   public void addPush(long var1) {
      int var3 = (int)var1;
      if ((long)var3 == var1) {
         this.addPush(var3);
         this.add(133);
      } else {
         this.addLoadConstant(var1);
      }
   }

   public void addPush(String var1) {
      int var2 = var1.length();
      int var3 = this.itsConstantPool.getUtfEncodingLimit(var1, 0, var2);
      if (var3 == var2) {
         this.addLoadConstant(var1);
      } else {
         this.add(187, "java/lang/StringBuilder");
         this.add(89);
         this.addPush(var2);
         this.addInvoke(183, "java/lang/StringBuilder", "<init>", "(I)V");
         int var4 = 0;

         while(true) {
            this.add(89);
            this.addLoadConstant(var1.substring(var4, var3));
            this.addInvoke(182, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            this.add(87);
            if (var3 == var2) {
               this.addInvoke(182, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
               return;
            }

            var4 = var3;
            var3 = this.itsConstantPool.getUtfEncodingLimit(var1, var3, var2);
         }
      }
   }

   public void addPush(boolean var1) {
      byte var2;
      if (var1) {
         var2 = 4;
      } else {
         var2 = 3;
      }

      this.add(var2);
   }

   public int addTableSwitch(int var1, int var2) {
      if (var1 > var2) {
         StringBuilder var3 = new StringBuilder();
         var3.append("Bad bounds: ");
         var3.append(var1);
         var3.append(' ');
         var3.append(var2);
         ClassFileWriter.ClassFileFormatException var8 = new ClassFileWriter.ClassFileFormatException(var3.toString());
         throw var8;
      } else {
         int var9 = this.itsStackTop + stackChange(170);
         if (var9 < 0 || 32767 < var9) {
            badStack(var9);
         }

         int var10 = 1 + (var2 - var1);
         int var11 = 3 & ~this.itsCodeBufferTop;
         int var12 = this.addReservedCodeSpace(var11 + 1 + 4 * (var10 + 3));
         byte[] var13 = this.itsCodeBuffer;
         int var14 = var12 + 1;

         int var19;
         for(var13[var12] = -86; var11 != 0; var14 = var19) {
            byte[] var18 = this.itsCodeBuffer;
            var19 = var14 + 1;
            var18[var14] = 0;
            --var11;
         }

         int var15 = var14 + 4;
         int var16 = putInt32(var1, this.itsCodeBuffer, var15);
         putInt32(var2, this.itsCodeBuffer, var16);
         this.itsStackTop = (short)var9;
         if (var9 > this.itsMaxStack) {
            this.itsMaxStack = (short)var9;
         }

         return var12;
      }
   }

   public void addVariableDescriptor(String var1, String var2, int var3, int var4) {
      int[] var5 = new int[]{this.itsConstantPool.addUtf8(var1), this.itsConstantPool.addUtf8(var2), var3, var4};
      if (this.itsVarDescriptors == null) {
         this.itsVarDescriptors = new ObjArray();
      }

      this.itsVarDescriptors.add(var5);
   }

   public void adjustStackTop(int var1) {
      int var2 = var1 + this.itsStackTop;
      if (var2 < 0 || 32767 < var2) {
         badStack(var2);
      }

      this.itsStackTop = (short)var2;
      if (var2 > this.itsMaxStack) {
         this.itsMaxStack = (short)var2;
      }

   }

   final char[] getCharBuffer(int var1) {
      char[] var2 = this.tmpCharBuffer;
      if (var1 > var2.length) {
         int var3 = 2 * var2.length;
         if (var1 > var3) {
            var3 = var1;
         }

         this.tmpCharBuffer = new char[var3];
      }

      return this.tmpCharBuffer;
   }

   public final String getClassName() {
      return this.generatedClassName;
   }

   public int getCurrentCodeOffset() {
      return this.itsCodeBufferTop;
   }

   public int getLabelPC(int var1) {
      if (var1 < 0) {
         int var2 = var1 & Integer.MAX_VALUE;
         if (var2 < this.itsLabelTableTop) {
            return this.itsLabelTable[var2];
         } else {
            throw new IllegalArgumentException("Bad label");
         }
      } else {
         throw new IllegalArgumentException("Bad label, no biscuit");
      }
   }

   public short getStackTop() {
      return this.itsStackTop;
   }

   public boolean isUnderStringSizeLimit(String var1) {
      return this.itsConstantPool.isUnderUtfEncodingLimit(var1);
   }

   public void markHandler(int var1) {
      this.itsStackTop = 1;
      this.markLabel(var1);
   }

   public void markLabel(int var1) {
      if (var1 < 0) {
         int var2 = var1 & Integer.MAX_VALUE;
         if (var2 <= this.itsLabelTableTop) {
            int[] var3 = this.itsLabelTable;
            if (var3[var2] == -1) {
               var3[var2] = this.itsCodeBufferTop;
            } else {
               throw new IllegalStateException("Can only mark label once");
            }
         } else {
            throw new IllegalArgumentException("Bad label");
         }
      } else {
         throw new IllegalArgumentException("Bad label, no biscuit");
      }
   }

   public void markLabel(int var1, short var2) {
      this.markLabel(var1);
      this.itsStackTop = var2;
   }

   public final void markTableSwitchCase(int var1, int var2) {
      this.addSuperBlockStart(this.itsCodeBufferTop);
      this.itsJumpFroms.put(this.itsCodeBufferTop, var1);
      this.setTableSwitchJump(var1, var2, this.itsCodeBufferTop);
   }

   public final void markTableSwitchCase(int var1, int var2, int var3) {
      if (var3 >= 0 && var3 <= this.itsMaxStack) {
         this.itsStackTop = (short)var3;
         this.addSuperBlockStart(this.itsCodeBufferTop);
         this.itsJumpFroms.put(this.itsCodeBufferTop, var1);
         this.setTableSwitchJump(var1, var2, this.itsCodeBufferTop);
      } else {
         StringBuilder var4 = new StringBuilder();
         var4.append("Bad stack index: ");
         var4.append(var3);
         throw new IllegalArgumentException(var4.toString());
      }
   }

   public final void markTableSwitchDefault(int var1) {
      this.addSuperBlockStart(this.itsCodeBufferTop);
      this.itsJumpFroms.put(this.itsCodeBufferTop, var1);
      this.setTableSwitchJump(var1, -1, this.itsCodeBufferTop);
   }

   public void setFlags(short var1) {
      this.itsFlags = var1;
   }

   public void setStackTop(short var1) {
      this.itsStackTop = var1;
   }

   public void setTableSwitchJump(int var1, int var2, int var3) {
      if (var3 >= 0) {
         int var7 = this.itsCodeBufferTop;
         if (var7 >= var3) {
            if (var2 >= -1) {
               int var11 = 3 & ~var1;
               int var12;
               if (var2 < 0) {
                  var12 = var11 + var1 + 1;
               } else {
                  var12 = var11 + var1 + 1 + 4 * (var2 + 3);
               }

               if (var1 >= 0 && -1 + (var7 - 16 - var11) >= var1) {
                  byte[] var16 = this.itsCodeBuffer;
                  if ((255 & var16[var1]) == 170) {
                     if (var12 >= 0 && var7 >= var12 + 4) {
                        putInt32(var3 - var1, var16, var12);
                        return;
                     }

                     StringBuilder var20 = new StringBuilder();
                     var20.append("Too big case index: ");
                     var20.append(var2);
                     throw new ClassFileWriter.ClassFileFormatException(var20.toString());
                  }

                  StringBuilder var17 = new StringBuilder();
                  var17.append(var1);
                  var17.append(" is not offset of tableswitch statement");
                  throw new IllegalArgumentException(var17.toString());
               }

               StringBuilder var13 = new StringBuilder();
               var13.append(var1);
               var13.append(" is outside a possible range of tableswitch in already generated code");
               throw new IllegalArgumentException(var13.toString());
            }

            StringBuilder var8 = new StringBuilder();
            var8.append("Bad case index: ");
            var8.append(var2);
            throw new IllegalArgumentException(var8.toString());
         }
      }

      StringBuilder var4 = new StringBuilder();
      var4.append("Bad jump target: ");
      var4.append(var3);
      throw new IllegalArgumentException(var4.toString());
   }

   public void startMethod(String var1, String var2, short var3) {
      short var4 = this.itsConstantPool.addUtf8(var1);
      short var5 = this.itsConstantPool.addUtf8(var2);
      ClassFileMethod var6 = new ClassFileMethod(var1, var4, var2, var5, var3);
      this.itsCurrentMethod = var6;
      this.itsJumpFroms = new UintMap();
      this.itsMethods.add(this.itsCurrentMethod);
      this.addSuperBlockStart(0);
   }

   public void stopMethod(short var1) {
      if (this.itsCurrentMethod != null) {
         this.fixLabelGotos();
         this.itsMaxLocals = var1;
         boolean var3 = GenerateStackMap;
         ClassFileWriter.StackMapTable var4 = null;
         if (var3) {
            this.finalizeSuperBlockStarts();
            var4 = new ClassFileWriter.StackMapTable();
            var4.generate();
         }

         int[] var5 = this.itsLineNumberTable;
         int var6 = 0;
         if (var5 != null) {
            var6 = 8 + 4 * this.itsLineNumberTableTop;
         }

         ObjArray var7 = this.itsVarDescriptors;
         int var8 = 0;
         if (var7 != null) {
            var8 = 8 + 10 * var7.size();
         }

         int var9 = 0;
         if (var4 != null) {
            int var57 = var4.computeWriteSize();
            var9 = 0;
            if (var57 > 0) {
               var9 = var57 + 6;
            }
         }

         int var10 = var9 + var8 + var6 + 2 + 2 + 14 + this.itsCodeBufferTop + 8 * this.itsExceptionTableTop;
         if (var10 > 65536) {
            throw new ClassFileWriter.ClassFileFormatException("generated bytecode for method exceeds 64K limit.");
         } else {
            byte[] var12 = new byte[var10];
            int var13 = putInt16(this.itsConstantPool.addUtf8("Code"), var12, 0);
            int var14 = var10 - 6;
            int var15 = putInt32(var14, var12, var13);
            int var16 = putInt16(this.itsMaxStack, var12, var15);
            int var17 = putInt16(this.itsMaxLocals, var12, var16);
            int var18 = putInt32(this.itsCodeBufferTop, var12, var17);
            System.arraycopy(this.itsCodeBuffer, 0, var12, var18, this.itsCodeBufferTop);
            int var19 = var18 + this.itsCodeBufferTop;
            int var20 = this.itsExceptionTableTop;
            int var22;
            if (var20 > 0) {
               var22 = putInt16(var20, var12, var19);

               for(int var47 = 0; var47 < this.itsExceptionTableTop; ++var47) {
                  ExceptionTableEntry var49 = this.itsExceptionTable[var47];
                  short var50 = (short)this.getLabelPC(var49.itsStartLabel);
                  short var51 = (short)this.getLabelPC(var49.itsEndLabel);
                  short var52 = (short)this.getLabelPC(var49.itsHandlerLabel);
                  short var53 = var49.itsCatchType;
                  if (var50 == -1) {
                     throw new IllegalStateException("start label not defined");
                  }

                  if (var51 == -1) {
                     throw new IllegalStateException("end label not defined");
                  }

                  if (var52 == -1) {
                     throw new IllegalStateException("handler label not defined");
                  }

                  var22 = putInt16(var53, var12, putInt16(var52, var12, putInt16(var51, var12, putInt16(var50, var12, var22))));
               }
            } else {
               var22 = putInt16(0, var12, var19);
            }

            int[] var23 = this.itsLineNumberTable;
            int var24 = 0;
            if (var23 != null) {
               var24 = 0 + 1;
            }

            if (this.itsVarDescriptors != null) {
               ++var24;
            }

            if (var9 > 0) {
               ++var24;
            }

            int var25 = putInt16(var24, var12, var22);
            if (this.itsLineNumberTable != null) {
               int var44 = putInt16(this.itsConstantPool.addUtf8("LineNumberTable"), var12, var25);
               int var45 = putInt32(2 + 4 * this.itsLineNumberTableTop, var12, var44);
               var25 = putInt16(this.itsLineNumberTableTop, var12, var45);

               for(int var46 = 0; var46 < this.itsLineNumberTableTop; ++var46) {
                  var25 = putInt32(this.itsLineNumberTable[var46], var12, var25);
               }
            }

            if (this.itsVarDescriptors != null) {
               int var30 = putInt16(this.itsConstantPool.addUtf8("LocalVariableTable"), var12, var25);
               int var31 = this.itsVarDescriptors.size();
               var25 = putInt16(var31, var12, putInt32(2 + var31 * 10, var12, var30));

               for(int var32 = 0; var32 < var31; var8 = var8) {
                  int[] var36 = (int[])((int[])this.itsVarDescriptors.get(var32));
                  int var37 = var36[0];
                  int var38 = var36[1];
                  int var40 = var36[2];
                  int var42 = var36[3];
                  var25 = putInt16(var42, var12, putInt16(var38, var12, putInt16(var37, var12, putInt16(this.itsCodeBufferTop - var40, var12, putInt16(var40, var12, var25)))));
                  ++var32;
                  var24 = var24;
                  var14 = var14;
               }
            }

            if (var9 > 0) {
               var4.write(var12, putInt16(this.itsConstantPool.addUtf8("StackMapTable"), var12, var25));
            }

            this.itsCurrentMethod.setCodeAttribute(var12);
            this.itsExceptionTable = null;
            this.itsExceptionTableTop = 0;
            this.itsLineNumberTableTop = 0;
            this.itsCodeBufferTop = 0;
            this.itsCurrentMethod = null;
            this.itsMaxStack = 0;
            this.itsStackTop = 0;
            this.itsLabelTableTop = 0;
            this.itsFixupTableTop = 0;
            this.itsVarDescriptors = null;
            this.itsSuperBlockStarts = null;
            this.itsSuperBlockStartsTop = 0;
            this.itsJumpFroms = null;
         }
      } else {
         IllegalStateException var2 = new IllegalStateException("No method to stop");
         throw var2;
      }
   }

   public byte[] toByteArray() {
      ObjArray var1 = this.itsBootstrapMethods;
      short var2 = 0;
      int var3 = 0;
      if (var1 != null) {
         var3 = 0 + 1;
         var2 = this.itsConstantPool.addUtf8("BootstrapMethods");
      }

      short var4 = this.itsSourceFileNameIndex;
      short var5 = 0;
      if (var4 != 0) {
         ++var3;
         var5 = this.itsConstantPool.addUtf8("SourceFile");
      }

      int var6 = this.getWriteSize();
      byte[] var7 = new byte[var6];
      int var8 = putInt32(-889275714, var7, 0);
      int var9 = putInt16(MinorVersion, var7, var8);
      int var10 = putInt16(MajorVersion, var7, var9);
      int var11 = this.itsConstantPool.write(var7, var10);
      int var12 = putInt16(this.itsFlags, var7, var11);
      int var13 = putInt16(this.itsThisClassIndex, var7, var12);
      int var14 = putInt16(this.itsSuperClassIndex, var7, var13);
      int var15 = putInt16(this.itsInterfaces.size(), var7, var14);

      for(int var16 = 0; var16 < this.itsInterfaces.size(); ++var16) {
         var15 = putInt16((Short)((Short)this.itsInterfaces.get(var16)), var7, var15);
      }

      int var17 = putInt16(this.itsFields.size(), var7, var15);

      for(int var18 = 0; var18 < this.itsFields.size(); ++var18) {
         var17 = ((ClassFileField)this.itsFields.get(var18)).write(var7, var17);
      }

      int var19 = putInt16(this.itsMethods.size(), var7, var17);

      for(int var20 = 0; var20 < this.itsMethods.size(); ++var20) {
         var19 = ((ClassFileMethod)this.itsMethods.get(var20)).write(var7, var19);
      }

      int var21 = putInt16(var3, var7, var19);
      if (this.itsBootstrapMethods != null) {
         int var24 = putInt16(var2, var7, var21);
         int var25 = putInt32(2 + this.itsBootstrapMethodsLength, var7, var24);
         var21 = putInt16(this.itsBootstrapMethods.size(), var7, var25);

         for(int var26 = 0; var26 < this.itsBootstrapMethods.size(); ++var26) {
            ClassFileWriter.BootstrapEntry var27 = (ClassFileWriter.BootstrapEntry)this.itsBootstrapMethods.get(var26);
            System.arraycopy(var27.code, 0, var7, var21, var27.code.length);
            var21 += var27.code.length;
         }
      }

      if (this.itsSourceFileNameIndex != 0) {
         int var23 = putInt32(2, var7, putInt16(var5, var7, var21));
         var21 = putInt16(this.itsSourceFileNameIndex, var7, var23);
      }

      if (var21 == var6) {
         return var7;
      } else {
         RuntimeException var22 = new RuntimeException();
         throw var22;
      }
   }

   public void write(OutputStream var1) throws IOException {
      var1.write(this.toByteArray());
   }

   final class BootstrapEntry {
      final byte[] code;

      BootstrapEntry(ClassFileWriter.MHandle var2, Object... var3) {
         byte[] var4 = new byte[4 + 2 * var3.length];
         this.code = var4;
         ClassFileWriter.putInt16(ClassFileWriter.this.itsConstantPool.addMethodHandle(var2), var4, 0);
         ClassFileWriter.putInt16(var3.length, var4, 2);

         for(int var7 = 0; var7 < var3.length; ++var7) {
            ClassFileWriter.putInt16(ClassFileWriter.this.itsConstantPool.addConstant(var3[var7]), this.code, 4 + var7 * 2);
         }

      }

      public boolean equals(Object var1) {
         return var1 instanceof ClassFileWriter.BootstrapEntry && Arrays.equals(this.code, ((ClassFileWriter.BootstrapEntry)var1).code);
      }

      public int hashCode() {
         return ~Arrays.hashCode(this.code);
      }
   }

   public static class ClassFileFormatException extends RuntimeException {
      private static final long serialVersionUID = 1263998431033790599L;

      ClassFileFormatException(String var1) {
         super(var1);
      }
   }

   public static final class MHandle {
      final String desc;
      final String name;
      final String owner;
      final byte tag;

      public MHandle(byte var1, String var2, String var3, String var4) {
         this.tag = var1;
         this.owner = var2;
         this.name = var3;
         this.desc = var4;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ClassFileWriter.MHandle)) {
            return false;
         } else {
            ClassFileWriter.MHandle var2 = (ClassFileWriter.MHandle)var1;
            return this.tag == var2.tag && this.owner.equals(var2.owner) && this.name.equals(var2.name) && this.desc.equals(var2.desc);
         }
      }

      public int hashCode() {
         return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.owner);
         var1.append('.');
         var1.append(this.name);
         var1.append(this.desc);
         var1.append(" (");
         var1.append(this.tag);
         var1.append(')');
         return var1.toString();
      }
   }

   final class StackMapTable {
      static final boolean DEBUGSTACKMAP;
      private int[] locals = null;
      private int localsTop = 0;
      private byte[] rawStackMap = null;
      private int rawStackMapTop = 0;
      private int[] stack = null;
      private int stackTop = 0;
      private SuperBlock[] superBlockDeps;
      private SuperBlock[] superBlocks = null;
      private boolean wide = false;
      private SuperBlock[] workList = null;
      private int workListTop = 0;

      private void addToWorkList(SuperBlock var1) {
         if (!var1.isInQueue()) {
            var1.setInQueue(true);
            var1.setInitialized(true);
            int var2 = this.workListTop;
            SuperBlock[] var3 = this.workList;
            if (var2 == var3.length) {
               SuperBlock[] var6 = new SuperBlock[var2 * 2];
               System.arraycopy(var3, 0, var6, 0, var2);
               this.workList = var6;
            }

            SuperBlock[] var4 = this.workList;
            int var5 = this.workListTop++;
            var4[var5] = var1;
         }

      }

      private void clearStack() {
         this.stackTop = 0;
      }

      private void computeRawStackMap() {
         int[] var1 = this.superBlocks[0].getTrimmedLocals();
         int var2 = -1;
         int var3 = 1;

         while(true) {
            SuperBlock[] var4 = this.superBlocks;
            if (var3 >= var4.length) {
               return;
            }

            SuperBlock var5 = var4[var3];
            int[] var6 = var5.getTrimmedLocals();
            int[] var7 = var5.getStack();
            int var8 = var5.getStart() - var2 - 1;
            if (var7.length != 0) {
               if (var7.length == 1) {
                  if (Arrays.equals(var1, var6)) {
                     this.writeSameLocalsOneStackItemFrame(var7, var8);
                  } else {
                     this.writeFullFrame(var6, var7, var8);
                  }
               } else {
                  this.writeFullFrame(var6, var7, var8);
               }
            } else {
               int var9;
               if (var1.length > var6.length) {
                  var9 = var6.length;
               } else {
                  var9 = var1.length;
               }

               int var10 = Math.abs(var1.length - var6.length);

               int var11;
               for(var11 = 0; var11 < var9 && var1[var11] == var6[var11]; ++var11) {
               }

               if (var11 == var6.length && var10 == 0) {
                  this.writeSameFrame(var8);
               } else if (var11 == var6.length && var10 <= 3) {
                  this.writeChopFrame(var10, var8);
               } else if (var11 == var1.length && var10 <= 3) {
                  this.writeAppendFrame(var6, var10, var8);
               } else {
                  this.writeFullFrame(var6, var7, var8);
               }
            }

            var1 = var6;
            var2 = var5.getStart();
            ++var3;
         }
      }

      private int execute(int var1) {
         int var2 = 255 & ClassFileWriter.this.itsCodeBuffer[var1];
         int var3 = 0;
         byte var4 = 2;
         byte var5 = 1;
         switch(var2) {
         case 0:
         case 132:
         case 167:
         case 200:
            break;
         case 1:
            this.push(5);
            var3 = 0;
            break;
         case 18:
         case 19:
         case 20:
            int var6;
            if (var2 == 18) {
               var6 = this.getOperand(var1 + 1);
            } else {
               var6 = this.getOperand(var1 + 1, var4);
            }

            byte var7 = ClassFileWriter.this.itsConstantPool.getConstantType(var6);
            if (var7 != 3) {
               if (var7 != 4) {
                  if (var7 != 5) {
                     if (var7 != 6) {
                        if (var7 != 8) {
                           StringBuilder var8 = new StringBuilder();
                           var8.append("bad const type ");
                           var8.append(var7);
                           throw new IllegalArgumentException(var8.toString());
                        }

                        this.push(TypeInfo.OBJECT("java/lang/String", ClassFileWriter.this.itsConstantPool));
                        var3 = 0;
                     } else {
                        this.push(3);
                        var3 = 0;
                     }
                  } else {
                     this.push(4);
                     var3 = 0;
                  }
               } else {
                  this.push(var4);
                  var3 = 0;
               }
            } else {
               this.push(var5);
               var3 = 0;
            }
            break;
         case 25:
            int var11 = var1 + 1;
            if (!this.wide) {
               var4 = 1;
            }

            this.executeALoad(this.getOperand(var11, var4));
            var3 = 0;
            break;
         case 42:
         case 43:
         case 44:
         case 45:
            this.executeALoad(var2 - 42);
            var3 = 0;
            break;
         case 46:
         case 51:
         case 52:
         case 53:
         case 96:
         case 100:
         case 104:
         case 108:
         case 112:
         case 120:
         case 122:
         case 124:
         case 126:
         case 128:
         case 130:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
            this.pop();
         case 116:
         case 136:
         case 139:
         case 142:
         case 145:
         case 146:
         case 147:
         case 190:
         case 193:
            this.pop();
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 16:
         case 17:
         case 21:
         case 26:
         case 27:
         case 28:
         case 29:
            this.push(var5);
            var3 = 0;
            break;
         case 47:
         case 97:
         case 101:
         case 105:
         case 109:
         case 113:
         case 121:
         case 123:
         case 125:
         case 127:
         case 129:
         case 131:
            this.pop();
         case 117:
         case 133:
         case 140:
         case 143:
            this.pop();
         case 9:
         case 10:
         case 22:
         case 30:
         case 31:
         case 32:
         case 33:
            this.push(4);
            var3 = 0;
            break;
         case 48:
         case 98:
         case 102:
         case 106:
         case 110:
         case 114:
            this.pop();
         case 118:
         case 134:
         case 137:
         case 144:
            this.pop();
         case 11:
         case 12:
         case 13:
         case 23:
         case 34:
         case 35:
         case 36:
         case 37:
            this.push(var4);
            var3 = 0;
            break;
         case 49:
         case 99:
         case 103:
         case 107:
         case 111:
         case 115:
            this.pop();
         case 119:
         case 135:
         case 138:
         case 141:
            this.pop();
         case 14:
         case 15:
         case 24:
         case 38:
         case 39:
         case 40:
         case 41:
            this.push(3);
            var3 = 0;
            break;
         case 50:
            this.pop();
            int var21 = this.pop() >>> 8;
            String var22 = (String)ClassFileWriter.this.itsConstantPool.getConstantData(var21);
            if (var22.charAt(0) != '[') {
               throw new IllegalStateException("bad array type");
            }

            String var23 = ClassFileWriter.descriptorToInternalName(var22.substring(var5));
            this.push(TypeInfo.OBJECT(ClassFileWriter.this.itsConstantPool.addClass(var23)));
            var3 = 0;
            break;
         case 54:
            int var24 = var1 + 1;
            if (!this.wide) {
               var4 = 1;
            }

            this.executeStore(this.getOperand(var24, var4), var5);
            var3 = 0;
            break;
         case 55:
            int var25 = var1 + 1;
            if (!this.wide) {
               var4 = 1;
            }

            this.executeStore(this.getOperand(var25, var4), 4);
            var3 = 0;
            break;
         case 56:
            int var26 = var1 + 1;
            if (this.wide) {
               var5 = 2;
            }

            this.executeStore(this.getOperand(var26, var5), var4);
            var3 = 0;
            break;
         case 57:
            int var27 = var1 + 1;
            if (!this.wide) {
               var4 = 1;
            }

            this.executeStore(this.getOperand(var27, var4), 3);
            var3 = 0;
            break;
         case 58:
            int var28 = var1 + 1;
            if (!this.wide) {
               var4 = 1;
            }

            this.executeAStore(this.getOperand(var28, var4));
            var3 = 0;
            break;
         case 59:
         case 60:
         case 61:
         case 62:
            this.executeStore(var2 - 59, var5);
            var3 = 0;
            break;
         case 63:
         case 64:
         case 65:
         case 66:
            this.executeStore(var2 - 63, 4);
            var3 = 0;
            break;
         case 67:
         case 68:
         case 69:
         case 70:
            this.executeStore(var2 - 67, var4);
            var3 = 0;
            break;
         case 71:
         case 72:
         case 73:
         case 74:
            this.executeStore(var2 - 71, 3);
            var3 = 0;
            break;
         case 75:
         case 76:
         case 77:
         case 78:
            this.executeAStore(var2 - 75);
            var3 = 0;
            break;
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
            this.pop();
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 181:
            this.pop();
         case 87:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 179:
         case 194:
         case 195:
         case 198:
         case 199:
            this.pop();
            var3 = 0;
            break;
         case 88:
            this.pop2();
            var3 = 0;
            break;
         case 89:
            int var34 = this.pop();
            this.push(var34);
            this.push(var34);
            var3 = 0;
            break;
         case 90:
            int var35 = this.pop();
            int var36 = this.pop();
            this.push(var35);
            this.push(var36);
            this.push(var35);
            var3 = 0;
            break;
         case 91:
            int var37 = this.pop();
            long var38 = this.pop2();
            this.push(var37);
            this.push2(var38);
            this.push(var37);
            var3 = 0;
            break;
         case 92:
            long var40 = this.pop2();
            this.push2(var40);
            this.push2(var40);
            var3 = 0;
            break;
         case 93:
            long var42 = this.pop2();
            int var44 = this.pop();
            this.push2(var42);
            this.push(var44);
            this.push2(var42);
            var3 = 0;
            break;
         case 94:
            long var45 = this.pop2();
            long var47 = this.pop2();
            this.push2(var45);
            this.push2(var47);
            this.push2(var45);
            var3 = 0;
            break;
         case 95:
            int var49 = this.pop();
            int var50 = this.pop();
            this.push(var49);
            this.push(var50);
            var3 = 0;
            break;
         case 168:
         case 169:
         case 171:
         case 197:
         default:
            StringBuilder var89 = new StringBuilder();
            var89.append("bad opcode: ");
            var89.append(var2);
            throw new IllegalArgumentException(var89.toString());
         case 170:
            int var51 = var1 + 1 + (3 & ~var1);
            int var52 = this.getOperand(var51 + 4, 4);
            var3 = var51 + 4 * (4 + (this.getOperand(var51 + 8, 4) - var52)) - var1;
            this.pop();
            break;
         case 172:
         case 173:
         case 174:
         case 175:
         case 176:
         case 177:
            this.clearStack();
            var3 = 0;
            break;
         case 180:
            this.pop();
         case 178:
            int var54 = this.getOperand(var1 + 1, var4);
            this.push(TypeInfo.fromType(ClassFileWriter.descriptorToInternalName(((FieldOrMethodRef)ClassFileWriter.this.itsConstantPool.getConstantData(var54)).getType()), ClassFileWriter.this.itsConstantPool));
            var3 = 0;
            break;
         case 182:
         case 183:
         case 184:
         case 185:
            int var56 = this.getOperand(var1 + 1, var4);
            FieldOrMethodRef var57 = (FieldOrMethodRef)ClassFileWriter.this.itsConstantPool.getConstantData(var56);
            String var58 = var57.getType();
            String var59 = var57.getName();
            int var60 = ClassFileWriter.sizeOfParameters(var58) >>> 16;

            for(int var61 = 0; var61 < var60; ++var61) {
               this.pop();
            }

            if (var2 != 184) {
               int var64 = this.pop();
               int var65 = TypeInfo.getTag(var64);
               if (var65 == TypeInfo.UNINITIALIZED_VARIABLE(0) || var65 == 6) {
                  if (!"<init>".equals(var59)) {
                     throw new IllegalStateException("bad instance");
                  }

                  this.initializeTypeInfo(var64, TypeInfo.OBJECT(ClassFileWriter.this.itsThisClassIndex));
               }
            }

            String var62 = ClassFileWriter.descriptorToInternalName(var58.substring(1 + var58.indexOf(41)));
            boolean var63 = var62.equals("V");
            var3 = 0;
            if (!var63) {
               this.push(TypeInfo.fromType(var62, ClassFileWriter.this.itsConstantPool));
               var3 = 0;
            }
            break;
         case 186:
            int var67 = this.getOperand(var1 + 1, var4);
            String var68 = (String)ClassFileWriter.this.itsConstantPool.getConstantData(var67);
            int var69 = ClassFileWriter.sizeOfParameters(var68) >>> 16;

            for(int var70 = 0; var70 < var69; ++var70) {
               this.pop();
            }

            String var71 = ClassFileWriter.descriptorToInternalName(var68.substring(1 + var68.indexOf(41)));
            boolean var72 = var71.equals("V");
            var3 = 0;
            if (!var72) {
               this.push(TypeInfo.fromType(var71, ClassFileWriter.this.itsConstantPool));
               var3 = 0;
            }
            break;
         case 187:
            this.push(TypeInfo.UNINITIALIZED_VARIABLE(var1));
            var3 = 0;
            break;
         case 188:
            this.pop();
            char var75 = ClassFileWriter.arrayTypeToName(ClassFileWriter.this.itsCodeBuffer[var1 + 1]);
            ConstantPool var76 = ClassFileWriter.this.itsConstantPool;
            StringBuilder var77 = new StringBuilder();
            var77.append("[");
            var77.append(var75);
            this.push(TypeInfo.OBJECT(var76.addClass(var77.toString())));
            var3 = 0;
            break;
         case 189:
            int var80 = this.getOperand(var1 + 1, var4);
            String var81 = (String)ClassFileWriter.this.itsConstantPool.getConstantData(var80);
            this.pop();
            StringBuilder var83 = new StringBuilder();
            var83.append("[L");
            var83.append(var81);
            var83.append(';');
            this.push(TypeInfo.OBJECT(var83.toString(), ClassFileWriter.this.itsConstantPool));
            var3 = 0;
            break;
         case 191:
            int var87 = this.pop();
            this.clearStack();
            this.push(var87);
            var3 = 0;
            break;
         case 192:
            this.pop();
            this.push(TypeInfo.OBJECT(this.getOperand(var1 + 1, var4)));
            var3 = 0;
            break;
         case 196:
            this.wide = (boolean)var5;
            var3 = 0;
         }

         if (var3 == 0) {
            var3 = ClassFileWriter.opcodeLength(var2, this.wide);
         }

         if (this.wide && var2 != 196) {
            this.wide = false;
         }

         return var3;
      }

      private void executeALoad(int var1) {
         int var2 = this.getLocal(var1);
         int var3 = TypeInfo.getTag(var2);
         if (var3 != 7 && var3 != 6 && var3 != 8 && var3 != 5) {
            StringBuilder var4 = new StringBuilder();
            var4.append("bad local variable type: ");
            var4.append(var2);
            var4.append(" at index: ");
            var4.append(var1);
            throw new IllegalStateException(var4.toString());
         } else {
            this.push(var2);
         }
      }

      private void executeAStore(int var1) {
         this.setLocal(var1, this.pop());
      }

      private void executeBlock(SuperBlock var1) {
         int var2 = 0;
         int var3 = var1.getStart();

         while(true) {
            int var4 = var1.getEnd();
            byte var5 = 1;
            if (var3 >= var4) {
               if (!this.isSuperBlockEnd(var2)) {
                  int var6 = 1 + var1.getIndex();
                  SuperBlock[] var7 = this.superBlocks;
                  if (var6 < var7.length) {
                     this.flowInto(var7[var6]);
                  }
               }

               return;
            }

            var2 = 255 & ClassFileWriter.this.itsCodeBuffer[var3];
            int var8 = this.execute(var3);
            if (this.isBranch(var2)) {
               this.flowInto(this.getBranchTarget(var3));
            } else if (var2 == 170) {
               int var19 = var3 + 1 + (3 & ~var3);
               this.flowInto(this.getSuperBlockFromOffset(var3 + this.getOperand(var19, 4)));
               int var20 = this.getOperand(var19 + 4, 4);
               int var21 = var5 + (this.getOperand(var19 + 8, 4) - var20);
               int var22 = var19 + 12;

               for(int var23 = 0; var23 < var21; ++var23) {
                  this.flowInto(this.getSuperBlockFromOffset(var3 + this.getOperand(var22 + var23 * 4, 4)));
               }
            }

            for(int var9 = 0; var9 < ClassFileWriter.this.itsExceptionTableTop; var5 = 1) {
               ExceptionTableEntry var10 = ClassFileWriter.this.itsExceptionTable[var9];
               short var11 = (short)ClassFileWriter.this.getLabelPC(var10.itsStartLabel);
               short var12 = (short)ClassFileWriter.this.getLabelPC(var10.itsEndLabel);
               if (var3 >= var11 && var3 < var12) {
                  SuperBlock var13 = this.getSuperBlockFromOffset((short)ClassFileWriter.this.getLabelPC(var10.itsHandlerLabel));
                  int var14;
                  if (var10.itsCatchType == 0) {
                     var14 = TypeInfo.OBJECT(ClassFileWriter.this.itsConstantPool.addClass("java/lang/Throwable"));
                  } else {
                     var14 = TypeInfo.OBJECT(var10.itsCatchType);
                  }

                  int[] var15 = this.locals;
                  int var16 = this.localsTop;
                  int[] var17 = new int[var5];
                  var17[0] = var14;
                  var13.merge(var15, var16, var17, 1, ClassFileWriter.this.itsConstantPool);
                  this.addToWorkList(var13);
               }

               ++var9;
            }

            var3 += var8;
         }
      }

      private void executeStore(int var1, int var2) {
         this.pop();
         this.setLocal(var1, var2);
      }

      private void executeWorkList() {
         while(true) {
            int var1 = this.workListTop;
            if (var1 <= 0) {
               return;
            }

            SuperBlock[] var2 = this.workList;
            int var3 = var1 - 1;
            this.workListTop = var3;
            SuperBlock var4 = var2[var3];
            var4.setInQueue(false);
            this.locals = var4.getLocals();
            int[] var5 = var4.getStack();
            this.stack = var5;
            this.localsTop = this.locals.length;
            this.stackTop = var5.length;
            this.executeBlock(var4);
         }
      }

      private void flowInto(SuperBlock var1) {
         if (var1.merge(this.locals, this.localsTop, this.stack, this.stackTop, ClassFileWriter.this.itsConstantPool)) {
            this.addToWorkList(var1);
         }

      }

      private SuperBlock getBranchTarget(int var1) {
         int var2;
         if ((255 & ClassFileWriter.this.itsCodeBuffer[var1]) == 200) {
            var2 = var1 + this.getOperand(var1 + 1, 4);
         } else {
            var2 = var1 + (short)this.getOperand(var1 + 1, 2);
         }

         return this.getSuperBlockFromOffset(var2);
      }

      private int getLocal(int var1) {
         return var1 < this.localsTop ? this.locals[var1] : 0;
      }

      private int getOperand(int var1) {
         return this.getOperand(var1, 1);
      }

      private int getOperand(int var1, int var2) {
         int var3 = 0;
         if (var2 > 4) {
            IllegalArgumentException var4 = new IllegalArgumentException("bad operand size");
            throw var4;
         } else {
            for(int var5 = 0; var5 < var2; ++var5) {
               var3 = var3 << 8 | 255 & ClassFileWriter.this.itsCodeBuffer[var1 + var5];
            }

            return var3;
         }
      }

      private SuperBlock[] getSuperBlockDependencies() {
         SuperBlock[] var1 = new SuperBlock[this.superBlocks.length];

         for(int var2 = 0; var2 < ClassFileWriter.this.itsExceptionTableTop; ++var2) {
            ExceptionTableEntry var7 = ClassFileWriter.this.itsExceptionTable[var2];
            short var8 = (short)ClassFileWriter.this.getLabelPC(var7.itsStartLabel);
            SuperBlock var9 = this.getSuperBlockFromOffset((short)ClassFileWriter.this.getLabelPC(var7.itsHandlerLabel));
            SuperBlock var10 = this.getSuperBlockFromOffset(var8);
            var1[var9.getIndex()] = var10;
         }

         int[] var3 = ClassFileWriter.this.itsJumpFroms.getKeys();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            int var5 = var3[var4];
            SuperBlock var6 = this.getSuperBlockFromOffset(ClassFileWriter.this.itsJumpFroms.getInt(var5, -1));
            var1[this.getSuperBlockFromOffset(var5).getIndex()] = var6;
         }

         return var1;
      }

      private SuperBlock getSuperBlockFromOffset(int var1) {
         int var2 = 0;

         while(true) {
            SuperBlock[] var3 = this.superBlocks;
            if (var2 >= var3.length) {
               break;
            }

            SuperBlock var8 = var3[var2];
            if (var8 == null) {
               break;
            }

            if (var1 >= var8.getStart() && var1 < var8.getEnd()) {
               return var8;
            }

            ++var2;
         }

         StringBuilder var4 = new StringBuilder();
         var4.append("bad offset: ");
         var4.append(var1);
         IllegalArgumentException var7 = new IllegalArgumentException(var4.toString());
         throw var7;
      }

      private int getWorstCaseWriteSize() {
         return (-1 + this.superBlocks.length) * (7 + 3 * ClassFileWriter.this.itsMaxLocals + 3 * ClassFileWriter.this.itsMaxStack);
      }

      private void initializeTypeInfo(int var1, int var2) {
         this.initializeTypeInfo(var1, var2, this.locals, this.localsTop);
         this.initializeTypeInfo(var1, var2, this.stack, this.stackTop);
      }

      private void initializeTypeInfo(int var1, int var2, int[] var3, int var4) {
         for(int var5 = 0; var5 < var4; ++var5) {
            if (var3[var5] == var1) {
               var3[var5] = var2;
            }
         }

      }

      private boolean isBranch(int var1) {
         switch(var1) {
         default:
            switch(var1) {
            case 198:
            case 199:
            case 200:
               break;
            default:
               return false;
            }
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         case 162:
         case 163:
         case 164:
         case 165:
         case 166:
         case 167:
            return true;
         }
      }

      private boolean isSuperBlockEnd(int var1) {
         if (var1 != 167 && var1 != 191 && var1 != 200 && var1 != 176 && var1 != 177) {
            switch(var1) {
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
               break;
            default:
               return false;
            }
         }

         return true;
      }

      private void killSuperBlock(SuperBlock var1) {
         int[] var2 = new int[0];
         int[] var3 = new int[]{TypeInfo.OBJECT("java/lang/Throwable", ClassFileWriter.this.itsConstantPool)};

         for(int var4 = 0; var4 < ClassFileWriter.this.itsExceptionTableTop; ++var4) {
            ExceptionTableEntry var15 = ClassFileWriter.this.itsExceptionTable[var4];
            int var16 = ClassFileWriter.this.getLabelPC(var15.itsStartLabel);
            int var17 = ClassFileWriter.this.getLabelPC(var15.itsEndLabel);
            SuperBlock var18 = this.getSuperBlockFromOffset(ClassFileWriter.this.getLabelPC(var15.itsHandlerLabel));
            if (var1.getStart() > var16 && var1.getStart() < var17 || var16 > var1.getStart() && var16 < var1.getEnd() && var18.isInitialized()) {
               var2 = var18.getLocals();
               break;
            }
         }

         for(int var5 = 0; var5 < ClassFileWriter.this.itsExceptionTableTop; ++var5) {
            ExceptionTableEntry var12 = ClassFileWriter.this.itsExceptionTable[var5];
            if (ClassFileWriter.this.getLabelPC(var12.itsStartLabel) == var1.getStart()) {
               for(int var13 = var5 + 1; var13 < ClassFileWriter.this.itsExceptionTableTop; ++var13) {
                  ClassFileWriter.this.itsExceptionTable[var13 - 1] = ClassFileWriter.this.itsExceptionTable[var13];
               }

               ClassFileWriter.access$410(ClassFileWriter.this);
               --var5;
            }
         }

         int var6 = var2.length;
         int var7 = var3.length;
         ConstantPool var8 = ClassFileWriter.this.itsConstantPool;
         var1.merge(var2, var6, var3, var7, var8);
         int var10 = var1.getEnd() - 1;
         ClassFileWriter.this.itsCodeBuffer[var10] = -65;

         for(int var11 = var1.getStart(); var11 < var10; ++var11) {
            ClassFileWriter.this.itsCodeBuffer[var11] = 0;
         }

      }

      private int pop() {
         int[] var1 = this.stack;
         int var2 = -1 + this.stackTop;
         this.stackTop = var2;
         return var1[var2];
      }

      private long pop2() {
         long var1 = (long)this.pop();
         return TypeInfo.isTwoWords((int)var1) ? var1 : var1 << 32 | (long)(16777215 & this.pop());
      }

      private void push(int var1) {
         int var2 = this.stackTop;
         if (var2 == this.stack.length) {
            int[] var5 = new int[Math.max(var2 * 2, 4)];
            System.arraycopy(this.stack, 0, var5, 0, this.stackTop);
            this.stack = var5;
         }

         int[] var3 = this.stack;
         int var4 = this.stackTop++;
         var3[var4] = var1;
      }

      private void push2(long var1) {
         this.push((int)(var1 & 16777215L));
         long var3 = var1 >>> 32;
         if (var3 != 0L) {
            this.push((int)(16777215L & var3));
         }

      }

      private void setLocal(int var1, int var2) {
         int var3 = this.localsTop;
         if (var1 >= var3) {
            int[] var4 = new int[var1 + 1];
            System.arraycopy(this.locals, 0, var4, 0, var3);
            this.locals = var4;
            this.localsTop = var1 + 1;
         }

         this.locals[var1] = var2;
      }

      private void verify() {
         int[] var1 = ClassFileWriter.this.createInitialLocals();
         this.superBlocks[0].merge(var1, var1.length, new int[0], 0, ClassFileWriter.this.itsConstantPool);
         SuperBlock[] var3 = new SuperBlock[]{this.superBlocks[0]};
         this.workList = var3;
         this.workListTop = 1;
         this.executeWorkList();
         int var4 = 0;

         while(true) {
            SuperBlock[] var5 = this.superBlocks;
            if (var4 >= var5.length) {
               this.executeWorkList();
               return;
            }

            SuperBlock var6 = var5[var4];
            if (!var6.isInitialized()) {
               this.killSuperBlock(var6);
            }

            ++var4;
         }
      }

      private void writeAppendFrame(int[] var1, int var2, int var3) {
         int var4 = var1.length - var2;
         byte[] var5 = this.rawStackMap;
         int var6 = this.rawStackMapTop;
         int var7 = var6 + 1;
         this.rawStackMapTop = var7;
         var5[var6] = (byte)(var2 + 251);
         this.rawStackMapTop = ClassFileWriter.putInt16(var3, var5, var7);
         this.rawStackMapTop = this.writeTypes(var1, var4);
      }

      private void writeChopFrame(int var1, int var2) {
         byte[] var3 = this.rawStackMap;
         int var4 = this.rawStackMapTop;
         int var5 = var4 + 1;
         this.rawStackMapTop = var5;
         var3[var4] = (byte)(251 - var1);
         this.rawStackMapTop = ClassFileWriter.putInt16(var2, var3, var5);
      }

      private void writeFullFrame(int[] var1, int[] var2, int var3) {
         byte[] var4 = this.rawStackMap;
         int var5 = this.rawStackMapTop;
         int var6 = var5 + 1;
         this.rawStackMapTop = var6;
         var4[var5] = -1;
         int var7 = ClassFileWriter.putInt16(var3, var4, var6);
         this.rawStackMapTop = var7;
         this.rawStackMapTop = ClassFileWriter.putInt16(var1.length, this.rawStackMap, var7);
         int var8 = this.writeTypes(var1);
         this.rawStackMapTop = var8;
         this.rawStackMapTop = ClassFileWriter.putInt16(var2.length, this.rawStackMap, var8);
         this.rawStackMapTop = this.writeTypes(var2);
      }

      private void writeSameFrame(int var1) {
         if (var1 <= 63) {
            byte[] var5 = this.rawStackMap;
            int var6 = this.rawStackMapTop++;
            var5[var6] = (byte)var1;
         } else {
            byte[] var2 = this.rawStackMap;
            int var3 = this.rawStackMapTop;
            int var4 = var3 + 1;
            this.rawStackMapTop = var4;
            var2[var3] = -5;
            this.rawStackMapTop = ClassFileWriter.putInt16(var1, var2, var4);
         }
      }

      private void writeSameLocalsOneStackItemFrame(int[] var1, int var2) {
         if (var2 <= 63) {
            byte[] var7 = this.rawStackMap;
            int var8 = this.rawStackMapTop++;
            var7[var8] = (byte)(var2 + 64);
         } else {
            byte[] var3 = this.rawStackMap;
            int var4 = this.rawStackMapTop;
            int var5 = var4 + 1;
            this.rawStackMapTop = var5;
            var3[var4] = -9;
            this.rawStackMapTop = ClassFileWriter.putInt16(var2, var3, var5);
         }

         this.writeType(var1[0]);
      }

      private int writeType(int var1) {
         int var2 = var1 & 255;
         byte[] var3 = this.rawStackMap;
         int var4 = this.rawStackMapTop;
         int var5 = var4 + 1;
         this.rawStackMapTop = var5;
         var3[var4] = (byte)var2;
         if (var2 == 7 || var2 == 8) {
            this.rawStackMapTop = ClassFileWriter.putInt16(var1 >>> 8, var3, var5);
         }

         return this.rawStackMapTop;
      }

      private int writeTypes(int[] var1) {
         return this.writeTypes(var1, 0);
      }

      private int writeTypes(int[] var1, int var2) {
         for(int var3 = var2; var3 < var1.length; ++var3) {
            this.rawStackMapTop = this.writeType(var1[var3]);
         }

         return this.rawStackMapTop;
      }

      int computeWriteSize() {
         this.rawStackMap = new byte[this.getWorstCaseWriteSize()];
         this.computeRawStackMap();
         return 2 + this.rawStackMapTop;
      }

      void generate() {
         this.superBlocks = new SuperBlock[ClassFileWriter.this.itsSuperBlockStartsTop];
         int[] var1 = ClassFileWriter.this.createInitialLocals();

         for(int var2 = 0; var2 < ClassFileWriter.this.itsSuperBlockStartsTop; ++var2) {
            int var3 = ClassFileWriter.this.itsSuperBlockStarts[var2];
            int var4;
            if (var2 == -1 + ClassFileWriter.this.itsSuperBlockStartsTop) {
               var4 = ClassFileWriter.this.itsCodeBufferTop;
            } else {
               var4 = ClassFileWriter.this.itsSuperBlockStarts[var2 + 1];
            }

            this.superBlocks[var2] = new SuperBlock(var2, var3, var4, var1);
         }

         this.superBlockDeps = this.getSuperBlockDependencies();
         this.verify();
      }

      int write(byte[] var1, int var2) {
         int var3 = ClassFileWriter.putInt32(2 + this.rawStackMapTop, var1, var2);
         int var4 = ClassFileWriter.putInt16(-1 + this.superBlocks.length, var1, var3);
         System.arraycopy(this.rawStackMap, 0, var1, var4, this.rawStackMapTop);
         return var4 + this.rawStackMapTop;
      }
   }
}
