package org.mozilla.classfile;

final class ConstantEntry {
   private int hashcode;
   private int intval;
   private long longval;
   private String str1;
   private String str2;
   private int type;

   ConstantEntry(int var1, int var2, String var3, String var4) {
      this.type = var1;
      this.intval = var2;
      this.str1 = var3;
      this.str2 = var4;
      this.hashcode = var1 ^ var2 + var3.hashCode() * var4.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ConstantEntry)) {
         return false;
      } else {
         ConstantEntry var2 = (ConstantEntry)var1;
         int var3 = this.type;
         if (var3 != var2.type) {
            return false;
         } else if (var3 != 3 && var3 != 4) {
            if (var3 != 5 && var3 != 6) {
               if (var3 != 12) {
                  if (var3 == 18) {
                     return this.intval == var2.intval && this.str1.equals(var2.str1) && this.str2.equals(var2.str2);
                  } else {
                     throw new RuntimeException("unsupported constant type");
                  }
               } else {
                  boolean var9 = this.str1.equals(var2.str1);
                  boolean var10 = false;
                  if (var9) {
                     boolean var11 = this.str2.equals(var2.str2);
                     var10 = false;
                     if (var11) {
                        var10 = true;
                     }
                  }

                  return var10;
               }
            } else {
               long var12;
               int var7 = (var12 = this.longval - var2.longval) == 0L ? 0 : (var12 < 0L ? -1 : 1);
               boolean var8 = false;
               if (var7 == 0) {
                  var8 = true;
               }

               return var8;
            }
         } else {
            int var4 = this.intval;
            int var5 = var2.intval;
            boolean var6 = false;
            if (var4 == var5) {
               var6 = true;
            }

            return var6;
         }
      }
   }

   public int hashCode() {
      return this.hashcode;
   }
}
