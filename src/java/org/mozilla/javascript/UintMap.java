package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UintMap implements Serializable {
   private static final int A = -1640531527;
   private static final int DELETED = -2;
   private static final int EMPTY = -1;
   private static final boolean check = false;
   private static final long serialVersionUID = 4242698212885848444L;
   private transient int ivaluesShift;
   private int keyCount;
   private transient int[] keys;
   private transient int occupiedCount;
   private int power;
   private transient Object[] values;

   public UintMap() {
      this(4);
   }

   public UintMap(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var2 = var1 * 4 / 3;

      int var3;
      for(var3 = 2; 1 << var3 < var2; ++var3) {
      }

      this.power = var3;
   }

   private int ensureIndex(int var1, boolean var2) {
      int var3 = -1;
      int var4 = -1;
      int[] var5 = this.keys;
      if (var5 != null) {
         int var7 = -1640531527 * var1;
         int var8 = this.power;
         var3 = var7 >>> 32 - var8;
         int var9 = var5[var3];
         if (var9 == var1) {
            return var3;
         }

         if (var9 != -1) {
            if (var9 == -2) {
               var4 = var3;
            }

            int var10 = (1 << var8) - 1;
            int var11 = tableLookupStep(var7, var10, var8);

            int var12;
            do {
               var3 = var10 & var3 + var11;
               var12 = var5[var3];
               if (var12 == var1) {
                  return var3;
               }

               if (var12 == -2 && var4 < 0) {
                  var4 = var3;
               }
            } while(var12 != -1);
         }
      }

      if (var4 >= 0) {
         var3 = var4;
      } else {
         label59: {
            if (var5 != null) {
               int var6 = this.occupiedCount;
               if (var6 * 4 < 3 * (1 << this.power)) {
                  this.occupiedCount = var6 + 1;
                  break label59;
               }
            }

            this.rehashTable(var2);
            return this.insertNewKey(var1);
         }
      }

      var5[var3] = var1;
      ++this.keyCount;
      return var3;
   }

   private int findIndex(int var1) {
      int[] var2 = this.keys;
      if (var2 != null) {
         int var3 = -1640531527 * var1;
         int var4 = this.power;
         int var5 = var3 >>> 32 - var4;
         int var6 = var2[var5];
         if (var6 == var1) {
            return var5;
         }

         if (var6 != -1) {
            int var7 = (1 << var4) - 1;
            int var8 = tableLookupStep(var3, var7, var4);

            int var9;
            do {
               var5 = var7 & var5 + var8;
               var9 = var2[var5];
               if (var9 == var1) {
                  return var5;
               }
            } while(var9 != -1);
         }
      }

      return -1;
   }

   private int insertNewKey(int var1) {
      int[] var2 = this.keys;
      int var3 = -1640531527 * var1;
      int var4 = this.power;
      int var5 = var3 >>> 32 - var4;
      if (var2[var5] != -1) {
         int var6 = (1 << var4) - 1;
         int var7 = tableLookupStep(var3, var6, var4);

         do {
            var5 = var6 & var5 + var7;
         } while(var2[var5] != -1);
      }

      var2[var5] = var1;
      ++this.occupiedCount;
      ++this.keyCount;
      return var5;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = this.keyCount;
      if (var2 != 0) {
         this.keyCount = 0;
         boolean var3 = var1.readBoolean();
         boolean var4 = var1.readBoolean();
         int var5 = 1 << this.power;
         if (var3) {
            this.keys = new int[var5 * 2];
            this.ivaluesShift = var5;
         } else {
            this.keys = new int[var5];
         }

         for(int var6 = 0; var6 != var5; ++var6) {
            this.keys[var6] = -1;
         }

         if (var4) {
            this.values = new Object[var5];
         }

         for(int var7 = 0; var7 != var2; ++var7) {
            int var8 = this.insertNewKey(var1.readInt());
            if (var3) {
               int var9 = var1.readInt();
               this.keys[var8 + this.ivaluesShift] = var9;
            }

            if (var4) {
               this.values[var8] = var1.readObject();
            }
         }
      }

   }

   private void rehashTable(boolean var1) {
      if (this.keys != null && 2 * this.keyCount >= this.occupiedCount) {
         ++this.power;
      }

      int var2 = 1 << this.power;
      int[] var3 = this.keys;
      int var4 = this.ivaluesShift;
      if (var4 == 0 && !var1) {
         this.keys = new int[var2];
      } else {
         this.ivaluesShift = var2;
         this.keys = new int[var2 * 2];
      }

      for(int var5 = 0; var5 != var2; ++var5) {
         this.keys[var5] = -1;
      }

      Object[] var6 = this.values;
      if (var6 != null) {
         this.values = new Object[var2];
      }

      int var7 = this.keyCount;
      this.occupiedCount = 0;
      if (var7 != 0) {
         this.keyCount = 0;
         int var8 = 0;

         for(int var9 = var7; var9 != 0; ++var8) {
            int var10 = var3[var8];
            if (var10 != -1 && var10 != -2) {
               int var11 = this.insertNewKey(var10);
               if (var6 != null) {
                  this.values[var11] = var6[var8];
               }

               if (var4 != 0) {
                  this.keys[var11 + this.ivaluesShift] = var3[var4 + var8];
               }

               --var9;
            }
         }
      }

   }

   private static int tableLookupStep(int var0, int var1, int var2) {
      int var3 = 32 - var2 * 2;
      return var3 >= 0 ? 1 | var1 & var0 >>> var3 : 1 | var0 & var1 >>> -var3;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      int var2 = this.keyCount;
      if (var2 != 0) {
         boolean var3;
         if (this.ivaluesShift != 0) {
            var3 = true;
         } else {
            var3 = false;
         }

         Object[] var4 = this.values;
         boolean var5 = false;
         if (var4 != null) {
            var5 = true;
         }

         var1.writeBoolean(var3);
         var1.writeBoolean(var5);

         for(int var6 = 0; var2 != 0; ++var6) {
            int var7 = this.keys[var6];
            if (var7 != -1 && var7 != -2) {
               --var2;
               var1.writeInt(var7);
               if (var3) {
                  var1.writeInt(this.keys[var6 + this.ivaluesShift]);
               }

               if (var5) {
                  var1.writeObject(this.values[var6]);
               }
            }
         }
      }

   }

   public void clear() {
      int var1 = 1 << this.power;
      if (this.keys != null) {
         for(int var2 = 0; var2 != var1; ++var2) {
            this.keys[var2] = -1;
         }

         if (this.values != null) {
            for(int var3 = 0; var3 != var1; ++var3) {
               this.values[var3] = null;
            }
         }
      }

      this.ivaluesShift = 0;
      this.keyCount = 0;
      this.occupiedCount = 0;
   }

   public int getExistingInt(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         int var4 = this.ivaluesShift;
         return var4 != 0 ? this.keys[var4 + var2] : 0;
      } else {
         Kit.codeBug();
         return 0;
      }
   }

   public int getInt(int var1, int var2) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var3 = this.findIndex(var1);
      if (var3 >= 0) {
         int var4 = this.ivaluesShift;
         return var4 != 0 ? this.keys[var4 + var3] : 0;
      } else {
         return var2;
      }
   }

   public int[] getKeys() {
      int[] var1 = this.keys;
      int var2 = this.keyCount;
      int[] var3 = new int[var2];

      for(int var4 = 0; var2 != 0; ++var4) {
         int var5 = var1[var4];
         if (var5 != -1 && var5 != -2) {
            --var2;
            var3[var2] = var5;
         }
      }

      return var3;
   }

   public Object getObject(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      if (this.values != null) {
         int var2 = this.findIndex(var1);
         if (var2 >= 0) {
            return this.values[var2];
         }
      }

      return null;
   }

   public boolean has(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      return this.findIndex(var1) >= 0;
   }

   public boolean isEmpty() {
      return this.keyCount == 0;
   }

   public void put(int var1, int var2) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var3 = this.ensureIndex(var1, true);
      if (this.ivaluesShift == 0) {
         int var4 = 1 << this.power;
         int[] var5 = this.keys;
         if (var5.length != var4 * 2) {
            int[] var6 = new int[var4 * 2];
            System.arraycopy(var5, 0, var6, 0, var4);
            this.keys = var6;
         }

         this.ivaluesShift = var4;
      }

      this.keys[var3 + this.ivaluesShift] = var2;
   }

   public void put(int var1, Object var2) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var3 = this.ensureIndex(var1, false);
      if (this.values == null) {
         this.values = new Object[1 << this.power];
      }

      this.values[var3] = var2;
   }

   public void remove(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         int[] var3 = this.keys;
         var3[var2] = -2;
         this.keyCount += -1;
         Object[] var4 = this.values;
         if (var4 != null) {
            var4[var2] = null;
         }

         int var5 = this.ivaluesShift;
         if (var5 != 0) {
            var3[var5 + var2] = 0;
         }
      }

   }

   public int size() {
      return this.keyCount;
   }
}
