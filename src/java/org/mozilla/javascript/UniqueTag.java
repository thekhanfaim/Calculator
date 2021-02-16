package org.mozilla.javascript;

import java.io.Serializable;

public final class UniqueTag implements Serializable {
   public static final UniqueTag DOUBLE_MARK = new UniqueTag(3);
   private static final int ID_DOUBLE_MARK = 3;
   private static final int ID_NOT_FOUND = 1;
   private static final int ID_NULL_VALUE = 2;
   public static final UniqueTag NOT_FOUND = new UniqueTag(1);
   public static final UniqueTag NULL_VALUE = new UniqueTag(2);
   private static final long serialVersionUID = -4320556826714577259L;
   private final int tagId;

   private UniqueTag(int var1) {
      this.tagId = var1;
   }

   public Object readResolve() {
      int var1 = this.tagId;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 == 3) {
               return DOUBLE_MARK;
            } else {
               throw new IllegalStateException(String.valueOf(this.tagId));
            }
         } else {
            return NULL_VALUE;
         }
      } else {
         return NOT_FOUND;
      }
   }

   public String toString() {
      int var1 = this.tagId;
      String var2;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               throw Kit.codeBug();
            }

            var2 = "DOUBLE_MARK";
         } else {
            var2 = "NULL_VALUE";
         }
      } else {
         var2 = "NOT_FOUND";
      }

      StringBuilder var3 = new StringBuilder();
      var3.append(super.toString());
      var3.append(": ");
      var3.append(var2);
      return var3.toString();
   }
}
