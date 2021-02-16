package org.mozilla.classfile;

final class SuperBlock {
   private int end;
   private int index;
   private boolean isInQueue;
   private boolean isInitialized;
   private int[] locals;
   private int[] stack;
   private int start;

   SuperBlock(int var1, int var2, int var3, int[] var4) {
      this.index = var1;
      this.start = var2;
      this.end = var3;
      int[] var5 = new int[var4.length];
      this.locals = var5;
      System.arraycopy(var4, 0, var5, 0, var4.length);
      this.stack = new int[0];
      this.isInitialized = false;
      this.isInQueue = false;
   }

   private boolean mergeState(int[] var1, int[] var2, int var3, ConstantPool var4) {
      boolean var5 = false;

      for(int var6 = 0; var6 < var3; ++var6) {
         int var7 = var1[var6];
         var1[var6] = TypeInfo.merge(var1[var6], var2[var6], var4);
         if (var7 != var1[var6]) {
            var5 = true;
         }
      }

      return var5;
   }

   int getEnd() {
      return this.end;
   }

   int getIndex() {
      return this.index;
   }

   int[] getLocals() {
      int[] var1 = this.locals;
      int[] var2 = new int[var1.length];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   int[] getStack() {
      int[] var1 = this.stack;
      int[] var2 = new int[var1.length];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   int getStart() {
      return this.start;
   }

   int[] getTrimmedLocals() {
      int var1;
      for(var1 = -1 + this.locals.length; var1 >= 0; --var1) {
         int[] var9 = this.locals;
         if (var9[var1] != 0 || TypeInfo.isTwoWords(var9[var1 - 1])) {
            break;
         }
      }

      int var2 = var1 + 1;
      int var3 = var2;

      for(int var4 = 0; var4 < var2; ++var4) {
         if (TypeInfo.isTwoWords(this.locals[var4])) {
            --var3;
         }
      }

      int[] var5 = new int[var3];
      int var6 = 0;

      for(int var7 = 0; var6 < var3; ++var7) {
         int[] var8 = this.locals;
         var5[var6] = var8[var7];
         if (TypeInfo.isTwoWords(var8[var7])) {
            ++var7;
         }

         ++var6;
      }

      return var5;
   }

   boolean isInQueue() {
      return this.isInQueue;
   }

   boolean isInitialized() {
      return this.isInitialized;
   }

   boolean merge(int[] var1, int var2, int[] var3, int var4, ConstantPool var5) {
      boolean var6 = this.isInitialized;
      boolean var7 = true;
      if (!var6) {
         System.arraycopy(var1, 0, this.locals, 0, var2);
         int[] var11 = new int[var4];
         this.stack = var11;
         System.arraycopy(var3, 0, var11, 0, var4);
         this.isInitialized = var7;
         return var7;
      } else {
         int[] var8 = this.locals;
         if (var8.length == var2 && this.stack.length == var4) {
            boolean var9 = this.mergeState(var8, var1, var2, var5);
            boolean var10 = this.mergeState(this.stack, var3, var4, var5);
            if (!var9) {
               if (var10) {
                  return var7;
               }

               var7 = false;
            }

            return var7;
         } else {
            throw new IllegalArgumentException("bad merge attempt");
         }
      }
   }

   void setInQueue(boolean var1) {
      this.isInQueue = var1;
   }

   void setInitialized(boolean var1) {
      this.isInitialized = var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("sb ");
      var1.append(this.index);
      return var1.toString();
   }
}
