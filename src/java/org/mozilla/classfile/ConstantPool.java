package org.mozilla.classfile;

import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.UintMap;

final class ConstantPool {
   static final byte CONSTANT_Class = 7;
   static final byte CONSTANT_Double = 6;
   static final byte CONSTANT_Fieldref = 9;
   static final byte CONSTANT_Float = 4;
   static final byte CONSTANT_Integer = 3;
   static final byte CONSTANT_InterfaceMethodref = 11;
   static final byte CONSTANT_InvokeDynamic = 18;
   static final byte CONSTANT_Long = 5;
   static final byte CONSTANT_MethodHandle = 15;
   static final byte CONSTANT_MethodType = 16;
   static final byte CONSTANT_Methodref = 10;
   static final byte CONSTANT_NameAndType = 12;
   static final byte CONSTANT_String = 8;
   static final byte CONSTANT_Utf8 = 1;
   private static final int ConstantPoolSize = 256;
   private static final int MAX_UTF_ENCODING_SIZE = 65535;
   private ClassFileWriter cfw;
   private ObjToIntMap itsClassHash = new ObjToIntMap();
   private UintMap itsConstantData = new UintMap();
   private ObjToIntMap itsConstantHash = new ObjToIntMap();
   private ObjToIntMap itsFieldRefHash = new ObjToIntMap();
   private ObjToIntMap itsMethodRefHash = new ObjToIntMap();
   private byte[] itsPool;
   private UintMap itsPoolTypes = new UintMap();
   private UintMap itsStringConstHash = new UintMap();
   private int itsTop;
   private int itsTopIndex;
   private ObjToIntMap itsUtf8Hash = new ObjToIntMap();

   ConstantPool(ClassFileWriter var1) {
      this.cfw = var1;
      this.itsTopIndex = 1;
      this.itsPool = new byte[256];
      this.itsTop = 0;
   }

   private short addNameAndType(String var1, String var2) {
      short var3 = this.addUtf8(var1);
      short var4 = this.addUtf8(var2);
      this.ensure(5);
      byte[] var5 = this.itsPool;
      int var6 = this.itsTop;
      int var7 = var6 + 1;
      this.itsTop = var7;
      var5[var6] = 12;
      int var8 = ClassFileWriter.putInt16(var3, var5, var7);
      this.itsTop = var8;
      this.itsTop = ClassFileWriter.putInt16(var4, this.itsPool, var8);
      this.itsPoolTypes.put(this.itsTopIndex, 12);
      int var9 = this.itsTopIndex++;
      return (short)var9;
   }

   private void ensure(int var1) {
      int var2 = this.itsTop;
      int var3 = var2 + var1;
      byte[] var4 = this.itsPool;
      if (var3 > var4.length) {
         int var5 = 2 * var4.length;
         if (var2 + var1 > var5) {
            var5 = var2 + var1;
         }

         byte[] var6 = new byte[var5];
         System.arraycopy(var4, 0, var6, 0, var2);
         this.itsPool = var6;
      }

   }

   short addClass(String var1) {
      int var2 = this.itsClassHash.get(var1, -1);
      if (var2 == -1) {
         String var3 = var1;
         if (var1.indexOf(46) > 0) {
            var3 = ClassFileWriter.getSlashedForm(var1);
            var2 = this.itsClassHash.get(var3, -1);
            if (var2 != -1) {
               this.itsClassHash.put(var1, var2);
            }
         }

         if (var2 == -1) {
            short var4 = this.addUtf8(var3);
            this.ensure(3);
            byte[] var5 = this.itsPool;
            int var6 = this.itsTop;
            int var7 = var6 + 1;
            this.itsTop = var7;
            var5[var6] = 7;
            this.itsTop = ClassFileWriter.putInt16(var4, var5, var7);
            int var8 = this.itsTopIndex++;
            var2 = var8;
            this.itsClassHash.put(var3, var8);
            if (!var1.equals(var3)) {
               this.itsClassHash.put(var1, var8);
            }
         }
      }

      this.setConstantData(var2, var1);
      this.itsPoolTypes.put(var2, 7);
      return (short)var2;
   }

   int addConstant(double var1) {
      this.ensure(9);
      byte[] var3 = this.itsPool;
      int var4 = this.itsTop++;
      var3[var4] = 6;
      this.itsTop = ClassFileWriter.putInt64(Double.doubleToLongBits(var1), this.itsPool, this.itsTop);
      int var5 = this.itsTopIndex;
      this.itsTopIndex += 2;
      this.itsPoolTypes.put(var5, 6);
      return var5;
   }

   int addConstant(float var1) {
      this.ensure(5);
      byte[] var2 = this.itsPool;
      int var3 = this.itsTop++;
      var2[var3] = 4;
      this.itsTop = ClassFileWriter.putInt32(Float.floatToIntBits(var1), this.itsPool, this.itsTop);
      this.itsPoolTypes.put(this.itsTopIndex, 4);
      int var4 = this.itsTopIndex++;
      return var4;
   }

   int addConstant(int var1) {
      this.ensure(5);
      byte[] var2 = this.itsPool;
      int var3 = this.itsTop;
      int var4 = var3 + 1;
      this.itsTop = var4;
      var2[var3] = 3;
      this.itsTop = ClassFileWriter.putInt32(var1, var2, var4);
      this.itsPoolTypes.put(this.itsTopIndex, 3);
      int var5 = this.itsTopIndex++;
      return (short)var5;
   }

   int addConstant(long var1) {
      this.ensure(9);
      byte[] var3 = this.itsPool;
      int var4 = this.itsTop;
      int var5 = var4 + 1;
      this.itsTop = var5;
      var3[var4] = 5;
      this.itsTop = ClassFileWriter.putInt64(var1, var3, var5);
      int var6 = this.itsTopIndex;
      this.itsTopIndex += 2;
      this.itsPoolTypes.put(var6, 5);
      return var6;
   }

   int addConstant(Object var1) {
      if (!(var1 instanceof Integer) && !(var1 instanceof Byte) && !(var1 instanceof Short)) {
         if (var1 instanceof Character) {
            return this.addConstant((Character)var1);
         } else if (var1 instanceof Boolean) {
            return this.addConstant((Boolean)var1);
         } else if (var1 instanceof Float) {
            return this.addConstant((Float)var1);
         } else if (var1 instanceof Long) {
            return this.addConstant((Long)var1);
         } else if (var1 instanceof Double) {
            return this.addConstant((Double)var1);
         } else if (var1 instanceof String) {
            return this.addConstant((String)var1);
         } else if (var1 instanceof ClassFileWriter.MHandle) {
            return this.addMethodHandle((ClassFileWriter.MHandle)var1);
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append("value ");
            var2.append(var1);
            throw new IllegalArgumentException(var2.toString());
         }
      } else {
         return this.addConstant(((Number)var1).intValue());
      }
   }

   int addConstant(String var1) {
      int var2 = '\uffff' & this.addUtf8(var1);
      int var3 = this.itsStringConstHash.getInt(var2, -1);
      if (var3 == -1) {
         int var4 = this.itsTopIndex++;
         var3 = var4;
         this.ensure(3);
         byte[] var5 = this.itsPool;
         int var6 = this.itsTop;
         int var7 = var6 + 1;
         this.itsTop = var7;
         var5[var6] = 8;
         this.itsTop = ClassFileWriter.putInt16(var2, var5, var7);
         this.itsStringConstHash.put(var2, var4);
      }

      this.itsPoolTypes.put(var3, 8);
      return var3;
   }

   short addFieldRef(String var1, String var2, String var3) {
      FieldOrMethodRef var4 = new FieldOrMethodRef(var1, var2, var3);
      int var5 = this.itsFieldRefHash.get(var4, -1);
      if (var5 == -1) {
         short var6 = this.addNameAndType(var2, var3);
         short var7 = this.addClass(var1);
         this.ensure(5);
         byte[] var8 = this.itsPool;
         int var9 = this.itsTop;
         int var10 = var9 + 1;
         this.itsTop = var10;
         var8[var9] = 9;
         int var11 = ClassFileWriter.putInt16(var7, var8, var10);
         this.itsTop = var11;
         this.itsTop = ClassFileWriter.putInt16(var6, this.itsPool, var11);
         int var12 = this.itsTopIndex++;
         var5 = var12;
         this.itsFieldRefHash.put(var4, var12);
      }

      this.setConstantData(var5, var4);
      this.itsPoolTypes.put(var5, 9);
      return (short)var5;
   }

   short addInterfaceMethodRef(String var1, String var2, String var3) {
      short var4 = this.addNameAndType(var2, var3);
      short var5 = this.addClass(var1);
      this.ensure(5);
      byte[] var6 = this.itsPool;
      int var7 = this.itsTop;
      int var8 = var7 + 1;
      this.itsTop = var8;
      var6[var7] = 11;
      int var9 = ClassFileWriter.putInt16(var5, var6, var8);
      this.itsTop = var9;
      this.itsTop = ClassFileWriter.putInt16(var4, this.itsPool, var9);
      FieldOrMethodRef var10 = new FieldOrMethodRef(var1, var2, var3);
      this.setConstantData(this.itsTopIndex, var10);
      this.itsPoolTypes.put(this.itsTopIndex, 11);
      int var11 = this.itsTopIndex++;
      return (short)var11;
   }

   short addInvokeDynamic(String var1, String var2, int var3) {
      ConstantEntry var4 = new ConstantEntry(18, var3, var1, var2);
      int var5 = this.itsConstantHash.get(var4, -1);
      if (var5 == -1) {
         short var6 = this.addNameAndType(var1, var2);
         this.ensure(5);
         byte[] var7 = this.itsPool;
         int var8 = this.itsTop;
         int var9 = var8 + 1;
         this.itsTop = var9;
         var7[var8] = 18;
         int var10 = ClassFileWriter.putInt16(var3, var7, var9);
         this.itsTop = var10;
         this.itsTop = ClassFileWriter.putInt16(var6, this.itsPool, var10);
         int var11 = this.itsTopIndex++;
         var5 = var11;
         this.itsConstantHash.put(var4, var11);
         this.setConstantData(var11, var2);
         this.itsPoolTypes.put(var11, 18);
      }

      return (short)var5;
   }

   short addMethodHandle(ClassFileWriter.MHandle var1) {
      int var2 = this.itsConstantHash.get(var1, -1);
      if (var2 == -1) {
         short var3;
         if (var1.tag <= 4) {
            var3 = this.addFieldRef(var1.owner, var1.name, var1.desc);
         } else if (var1.tag == 9) {
            var3 = this.addInterfaceMethodRef(var1.owner, var1.name, var1.desc);
         } else {
            var3 = this.addMethodRef(var1.owner, var1.name, var1.desc);
         }

         this.ensure(4);
         byte[] var4 = this.itsPool;
         int var5 = this.itsTop;
         int var6 = var5 + 1;
         this.itsTop = var6;
         var4[var5] = 15;
         this.itsTop = var6 + 1;
         var4[var6] = var1.tag;
         this.itsTop = ClassFileWriter.putInt16(var3, this.itsPool, this.itsTop);
         int var7 = this.itsTopIndex++;
         var2 = var7;
         this.itsConstantHash.put(var1, var7);
         this.itsPoolTypes.put(var7, 15);
      }

      return (short)var2;
   }

   short addMethodRef(String var1, String var2, String var3) {
      FieldOrMethodRef var4 = new FieldOrMethodRef(var1, var2, var3);
      int var5 = this.itsMethodRefHash.get(var4, -1);
      if (var5 == -1) {
         short var6 = this.addNameAndType(var2, var3);
         short var7 = this.addClass(var1);
         this.ensure(5);
         byte[] var8 = this.itsPool;
         int var9 = this.itsTop;
         int var10 = var9 + 1;
         this.itsTop = var10;
         var8[var9] = 10;
         int var11 = ClassFileWriter.putInt16(var7, var8, var10);
         this.itsTop = var11;
         this.itsTop = ClassFileWriter.putInt16(var6, this.itsPool, var11);
         int var12 = this.itsTopIndex++;
         var5 = var12;
         this.itsMethodRefHash.put(var4, var12);
      }

      this.setConstantData(var5, var4);
      this.itsPoolTypes.put(var5, 10);
      return (short)var5;
   }

   short addUtf8(String var1) {
      int var2 = this.itsUtf8Hash.get(var1, -1);
      if (var2 == -1) {
         int var3 = var1.length();
         boolean var14;
         if (var3 > 65535) {
            var14 = true;
         } else {
            this.ensure(3 + var3 * 3);
            int var4 = this.itsTop;
            byte[] var5 = this.itsPool;
            int var6 = var4 + 1;
            var5[var4] = 1;
            int var7 = var6 + 2;
            char[] var8 = this.cfw.getCharBuffer(var3);
            var1.getChars(0, var3, var8, 0);
            int var9 = 0;

            while(true) {
               if (var9 == var3) {
                  int var10 = this.itsTop;
                  int var11 = var7 - (2 + var10 + 1);
                  if (var11 > 65535) {
                     var14 = true;
                  } else {
                     byte[] var12 = this.itsPool;
                     var12[var10 + 1] = (byte)(var11 >>> 8);
                     var12[var10 + 2] = (byte)var11;
                     this.itsTop = var7;
                     int var13 = this.itsTopIndex++;
                     var2 = var13;
                     this.itsUtf8Hash.put(var1, var13);
                     var14 = false;
                  }
                  break;
               }

               char var15 = var8[var9];
               if (var15 != 0 && var15 <= 127) {
                  byte[] var22 = this.itsPool;
                  int var23 = var7 + 1;
                  var22[var7] = (byte)var15;
                  var7 = var23;
               } else if (var15 > 2047) {
                  byte[] var18 = this.itsPool;
                  int var19 = var7 + 1;
                  var18[var7] = (byte)(224 | var15 >> 12);
                  int var20 = var19 + 1;
                  var18[var19] = (byte)(128 | 63 & var15 >> 6);
                  int var21 = var20 + 1;
                  var18[var20] = (byte)(128 | var15 & 63);
                  var7 = var21;
               } else {
                  byte[] var16 = this.itsPool;
                  int var17 = var7 + 1;
                  var16[var7] = (byte)(192 | var15 >> 6);
                  var7 = var17 + 1;
                  var16[var17] = (byte)(128 | var15 & 63);
               }

               ++var9;
            }
         }

         if (var14) {
            throw new IllegalArgumentException("Too big string");
         }
      }

      this.setConstantData(var2, var1);
      this.itsPoolTypes.put(var2, 1);
      return (short)var2;
   }

   Object getConstantData(int var1) {
      return this.itsConstantData.getObject(var1);
   }

   byte getConstantType(int var1) {
      return (byte)this.itsPoolTypes.getInt(var1, 0);
   }

   int getUtfEncodingLimit(String var1, int var2, int var3) {
      if (3 * (var3 - var2) <= 65535) {
         return var3;
      } else {
         int var4 = 65535;

         for(int var5 = var2; var5 != var3; ++var5) {
            char var6 = var1.charAt(var5);
            if (var6 != 0 && var6 <= 127) {
               --var4;
            } else if (var6 < 2047) {
               var4 -= 2;
            } else {
               var4 -= 3;
            }

            if (var4 < 0) {
               return var5;
            }
         }

         return var3;
      }
   }

   int getWriteSize() {
      return 2 + this.itsTop;
   }

   boolean isUnderUtfEncodingLimit(String var1) {
      int var2 = var1.length();
      if (var2 * 3 <= 65535) {
         return true;
      } else if (var2 > 65535) {
         return false;
      } else {
         return var2 == this.getUtfEncodingLimit(var1, 0, var2);
      }
   }

   void setConstantData(int var1, Object var2) {
      this.itsConstantData.put(var1, var2);
   }

   int write(byte[] var1, int var2) {
      int var3 = ClassFileWriter.putInt16((short)this.itsTopIndex, var1, var2);
      System.arraycopy(this.itsPool, 0, var1, var3, this.itsTop);
      return var3 + this.itsTop;
   }
}
