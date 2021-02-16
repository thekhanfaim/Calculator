package org.mozilla.classfile;

final class TypeInfo {
   static final int DOUBLE = 3;
   static final int FLOAT = 2;
   static final int INTEGER = 1;
   static final int LONG = 4;
   static final int NULL = 5;
   static final int OBJECT_TAG = 7;
   static final int TOP = 0;
   static final int UNINITIALIZED_THIS = 6;
   static final int UNINITIALIZED_VAR_TAG = 8;

   private TypeInfo() {
   }

   static final int OBJECT(int var0) {
      return 7 | ('\uffff' & var0) << 8;
   }

   static final int OBJECT(String var0, ConstantPool var1) {
      return OBJECT(var1.addClass(var0));
   }

   static final int UNINITIALIZED_VARIABLE(int var0) {
      return 8 | ('\uffff' & var0) << 8;
   }

   static final int fromType(String var0, ConstantPool var1) {
      if (var0.length() == 1) {
         char var2 = var0.charAt(0);
         if (var2 != 'F') {
            if (var2 != 'S' && var2 != 'Z' && var2 != 'I') {
               if (var2 == 'J') {
                  return 4;
               }

               switch(var2) {
               case 'B':
               case 'C':
                  break;
               case 'D':
                  return 3;
               default:
                  throw new IllegalArgumentException("bad type");
               }
            }

            return 1;
         } else {
            return 2;
         }
      } else {
         return OBJECT(var0, var1);
      }
   }

   private static Class getClassFromInternalName(String var0) {
      try {
         Class var2 = Class.forName(var0.replace('/', '.'));
         return var2;
      } catch (ClassNotFoundException var3) {
         throw new RuntimeException(var3);
      }
   }

   static final int getPayload(int var0) {
      return var0 >>> 8;
   }

   static final String getPayloadAsType(int var0, ConstantPool var1) {
      if (getTag(var0) == 7) {
         return (String)var1.getConstantData(getPayload(var0));
      } else {
         throw new IllegalArgumentException("expecting object type");
      }
   }

   static final int getTag(int var0) {
      return var0 & 255;
   }

   static boolean isTwoWords(int var0) {
      return var0 == 3 || var0 == 4;
   }

   static int merge(int var0, int var1, ConstantPool var2) {
      int var3 = getTag(var0);
      int var4 = getTag(var1);
      boolean var5 = true;
      boolean var6;
      if (var3 == 7) {
         var6 = true;
      } else {
         var6 = false;
      }

      if (var4 != 7) {
         var5 = false;
      }

      if (var0 == var1) {
         return var0;
      } else if (var6 && var1 == 5) {
         return var0;
      } else if (var3 == 0) {
         return 0;
      } else if (var4 == 0) {
         return 0;
      } else if (var0 == 5 && var5) {
         return var1;
      } else {
         if (var6 && var5) {
            String var12 = getPayloadAsType(var0, var2);
            String var13 = getPayloadAsType(var1, var2);
            String var14 = (String)var2.getConstantData(2);
            String var15 = (String)var2.getConstantData(4);
            if (var12.equals(var14)) {
               var12 = var15;
            }

            if (var13.equals(var14)) {
               var13 = var15;
            }

            Class var16 = getClassFromInternalName(var12);
            Class var17 = getClassFromInternalName(var13);
            if (var16.isAssignableFrom(var17)) {
               return var0;
            }

            if (var17.isAssignableFrom(var16)) {
               return var1;
            }

            if (var17.isInterface() || var16.isInterface()) {
               return OBJECT("java/lang/Object", var2);
            }

            for(Class var18 = var17.getSuperclass(); var18 != null; var18 = var18.getSuperclass()) {
               if (var18.isAssignableFrom(var16)) {
                  return OBJECT(ClassFileWriter.getSlashedForm(var18.getName()), var2);
               }
            }
         }

         StringBuilder var7 = new StringBuilder();
         var7.append("bad merge attempt between ");
         var7.append(toString(var0, var2));
         var7.append(" and ");
         var7.append(toString(var1, var2));
         throw new IllegalArgumentException(var7.toString());
      }
   }

   static void print(int[] var0, int var1, int[] var2, int var3, ConstantPool var4) {
      System.out.print("locals: ");
      System.out.println(toString(var0, var1, var4));
      System.out.print("stack: ");
      System.out.println(toString(var2, var3, var4));
      System.out.println();
   }

   static void print(int[] var0, int[] var1, ConstantPool var2) {
      print(var0, var0.length, var1, var1.length, var2);
   }

   static String toString(int var0, ConstantPool var1) {
      int var2 = getTag(var0);
      switch(var2) {
      case 0:
         return "top";
      case 1:
         return "int";
      case 2:
         return "float";
      case 3:
         return "double";
      case 4:
         return "long";
      case 5:
         return "null";
      case 6:
         return "uninitialized_this";
      default:
         if (var2 == 7) {
            return getPayloadAsType(var0, var1);
         } else if (var2 == 8) {
            return "uninitialized";
         } else {
            throw new IllegalArgumentException("bad type");
         }
      }
   }

   private static String toString(int[] var0, int var1, ConstantPool var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("[");

      for(int var5 = 0; var5 < var1; ++var5) {
         if (var5 > 0) {
            var3.append(", ");
         }

         var3.append(toString(var0[var5], var2));
      }

      var3.append("]");
      return var3.toString();
   }
}
