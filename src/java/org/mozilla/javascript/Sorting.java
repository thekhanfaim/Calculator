package org.mozilla.javascript;

import java.util.Comparator;

public final class Sorting {
   private static final int SMALLSORT = 16;
   private static final Sorting sorting = new Sorting();

   private Sorting() {
   }

   public static Sorting get() {
      return sorting;
   }

   private void hybridSort(Object[] var1, int var2, int var3, Comparator var4, int var5) {
      if (var2 < var3) {
         if (var5 != 0 && var3 - var2 > 16) {
            int var6 = this.partition(var1, var2, var3, var4);
            this.hybridSort(var1, var2, var6, var4, var5 - 1);
            this.hybridSort(var1, var6 + 1, var3, var4, var5 - 1);
            return;
         }

         this.insertionSort(var1, var2, var3, var4);
      }

   }

   private void insertionSort(Object[] var1, int var2, int var3, Comparator var4) {
      for(int var5 = var2; var5 <= var3; ++var5) {
         Object var6 = var1[var5];

         int var7;
         for(var7 = var5 - 1; var7 >= var2 && var4.compare(var1[var7], var6) > 0; --var7) {
            var1[var7 + 1] = var1[var7];
         }

         var1[var7 + 1] = var6;
      }

   }

   private int log2(int var1) {
      return (int)(Math.log10((double)var1) / Math.log10(2.0D));
   }

   private int partition(Object[] var1, int var2, int var3, Comparator var4) {
      int var5 = this.median(var1, var2, var3, var4);
      Object var6 = var1[var5];
      var1[var5] = var1[var2];
      var1[var2] = var6;
      int var7 = var2;
      int var8 = var3 + 1;

      while(true) {
         do {
            ++var7;
         } while(var4.compare(var1[var7], var6) < 0 && var7 != var3);

         do {
            --var8;
         } while(var4.compare(var1[var8], var6) >= 0 && var8 != var2);

         if (var7 >= var8) {
            this.swap(var1, var2, var8);
            return var8;
         }

         this.swap(var1, var7, var8);
      }
   }

   private void swap(Object[] var1, int var2, int var3) {
      Object var4 = var1[var2];
      var1[var2] = var1[var3];
      var1[var3] = var4;
   }

   public void hybridSort(Object[] var1, Comparator var2) {
      this.hybridSort(var1, 0, -1 + var1.length, var2, 2 * this.log2(var1.length));
   }

   public void insertionSort(Object[] var1, Comparator var2) {
      this.insertionSort(var1, 0, -1 + var1.length, var2);
   }

   public int median(Object[] var1, int var2, int var3, Comparator var4) {
      int var5 = var2 + (var3 - var2) / 2;
      int var6 = var2;
      if (var4.compare(var1[var2], var1[var5]) > 0) {
         var6 = var5;
      }

      if (var4.compare(var1[var6], var1[var3]) > 0) {
         var6 = var3;
      }

      if (var6 == var2) {
         return var4.compare(var1[var5], var1[var3]) < 0 ? var5 : var3;
      } else if (var6 == var5) {
         return var4.compare(var1[var2], var1[var3]) < 0 ? var2 : var3;
      } else {
         return var4.compare(var1[var2], var1[var5]) < 0 ? var2 : var5;
      }
   }
}
