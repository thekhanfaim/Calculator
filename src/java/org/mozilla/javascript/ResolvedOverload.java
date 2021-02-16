package org.mozilla.javascript;

import java.util.Arrays;

class ResolvedOverload {
   final int index;
   final Class[] types;

   ResolvedOverload(Object[] var1, int var2) {
      this.index = var2;
      this.types = new Class[var1.length];
      int var3 = 0;

      for(int var4 = var1.length; var3 < var4; ++var3) {
         Object var5 = var1[var3];
         if (var5 instanceof Wrapper) {
            var5 = ((Wrapper)var5).unwrap();
         }

         Class[] var6 = this.types;
         Class var7;
         if (var5 == null) {
            var7 = null;
         } else {
            var7 = var5.getClass();
         }

         var6[var3] = var7;
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ResolvedOverload)) {
         return false;
      } else {
         ResolvedOverload var2 = (ResolvedOverload)var1;
         boolean var3 = Arrays.equals(this.types, var2.types);
         boolean var4 = false;
         if (var3) {
            int var5 = this.index;
            int var6 = var2.index;
            var4 = false;
            if (var5 == var6) {
               var4 = true;
            }
         }

         return var4;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.types);
   }

   boolean matches(Object[] var1) {
      if (var1.length != this.types.length) {
         return false;
      } else {
         int var2 = 0;

         for(int var3 = var1.length; var2 < var3; ++var2) {
            Object var4 = var1[var2];
            if (var4 instanceof Wrapper) {
               var4 = ((Wrapper)var4).unwrap();
            }

            if (var4 == null) {
               if (this.types[var2] != null) {
                  return false;
               }
            } else if (var4.getClass() != this.types[var2]) {
               return false;
            }
         }

         return true;
      }
   }
}
