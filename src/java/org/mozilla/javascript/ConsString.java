package org.mozilla.javascript;

import java.io.Serializable;
import java.util.ArrayDeque;

public class ConsString implements CharSequence, Serializable {
   private static final long serialVersionUID = -8432806714471372570L;
   private boolean isFlat;
   private CharSequence left;
   private final int length;
   private CharSequence right;

   public ConsString(CharSequence var1, CharSequence var2) {
      this.left = var1;
      this.right = var2;
      this.length = var1.length() + this.right.length();
      this.isFlat = false;
   }

   private String flatten() {
      synchronized(this){}

      Throwable var10000;
      label550: {
         boolean var10001;
         label549: {
            int var3;
            char[] var4;
            ArrayDeque var5;
            CharSequence var6;
            try {
               if (this.isFlat) {
                  break label549;
               }

               var3 = this.length;
               var4 = new char[var3];
               var5 = new ArrayDeque();
               var5.addFirst(this.left);
               var6 = this.right;
            } catch (Throwable var64) {
               var10000 = var64;
               var10001 = false;
               break label550;
            }

            while(true) {
               label555: {
                  label558: {
                     ConsString var9;
                     label543:
                     try {
                        if (var6 instanceof ConsString) {
                           var9 = (ConsString)var6;
                           if (!var9.isFlat) {
                              break label543;
                           }

                           var6 = var9.left;
                        }
                        break label558;
                     } catch (Throwable var66) {
                        var10000 = var66;
                        var10001 = false;
                        break label550;
                     }

                     try {
                        var5.addFirst(var9.left);
                        var6 = var9.right;
                        break label555;
                     } catch (Throwable var63) {
                        var10000 = var63;
                        var10001 = false;
                        break label550;
                     }
                  }

                  CharSequence var8;
                  label534: {
                     label533: {
                        try {
                           String var7 = (String)var6;
                           var3 -= var7.length();
                           var7.getChars(0, var7.length(), var4, var3);
                           if (!var5.isEmpty()) {
                              break label533;
                           }
                        } catch (Throwable var65) {
                           var10000 = var65;
                           var10001 = false;
                           break label550;
                        }

                        var8 = null;
                        break label534;
                     }

                     try {
                        var8 = (CharSequence)var5.removeFirst();
                     } catch (Throwable var62) {
                        var10000 = var62;
                        var10001 = false;
                        break label550;
                     }
                  }

                  var6 = var8;
               }

               if (var6 == null) {
                  try {
                     this.left = new String(var4);
                     this.right = "";
                     this.isFlat = true;
                     break;
                  } catch (Throwable var61) {
                     var10000 = var61;
                     var10001 = false;
                     break label550;
                  }
               }
            }
         }

         label518:
         try {
            String var2 = (String)this.left;
            return var2;
         } catch (Throwable var60) {
            var10000 = var60;
            var10001 = false;
            break label518;
         }
      }

      Throwable var1 = var10000;
      throw var1;
   }

   private Object writeReplace() {
      return this.toString();
   }

   public char charAt(int var1) {
      String var2;
      if (this.isFlat) {
         var2 = (String)this.left;
      } else {
         var2 = this.flatten();
      }

      return var2.charAt(var1);
   }

   public int length() {
      return this.length;
   }

   public CharSequence subSequence(int var1, int var2) {
      String var3;
      if (this.isFlat) {
         var3 = (String)this.left;
      } else {
         var3 = this.flatten();
      }

      return var3.substring(var1, var2);
   }

   public String toString() {
      return this.isFlat ? (String)this.left : this.flatten();
   }
}
