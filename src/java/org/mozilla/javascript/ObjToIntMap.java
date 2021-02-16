package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjToIntMap implements Serializable {
   private static final int A = -1640531527;
   private static final Object DELETED = new Object();
   private static final boolean check = false;
   private static final long serialVersionUID = -1542220580748809402L;
   private int keyCount;
   private transient Object[] keys;
   private transient int occupiedCount;
   private int power;
   private transient int[] values;

   public ObjToIntMap() {
      this(4);
   }

   public ObjToIntMap(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      int var2 = var1 * 4 / 3;

      int var3;
      for(var3 = 2; 1 << var3 < var2; ++var3) {
      }

      this.power = var3;
   }

   private int ensureIndex(Object var1) {
      int var2 = var1.hashCode();
      int var3 = -1;
      int var4 = -1;
      Object[] var5 = this.keys;
      if (var5 != null) {
         int var7 = -1640531527 * var2;
         int var8 = this.power;
         var3 = var7 >>> 32 - var8;
         Object var9 = var5[var3];
         if (var9 != null) {
            int var10 = 1 << var8;
            if (var9 == var1) {
               return var3;
            }

            if (this.values[var10 + var3] == var2 && var9.equals(var1)) {
               return var3;
            }

            if (var9 == DELETED) {
               var4 = var3;
            }

            int var11 = var10 - 1;
            int var12 = tableLookupStep(var7, var11, this.power);

            while(true) {
               var3 = var11 & var3 + var12;
               Object var13 = this.keys[var3];
               if (var13 == null) {
                  break;
               }

               if (var13 == var1) {
                  return var3;
               }

               if (this.values[var10 + var3] == var2 && var13.equals(var1)) {
                  return var3;
               }

               if (var13 == DELETED && var4 < 0) {
                  var4 = var3;
               }
            }
         }
      }

      if (var4 >= 0) {
         var3 = var4;
      } else {
         label66: {
            if (this.keys != null) {
               int var6 = this.occupiedCount;
               if (var6 * 4 < 3 * (1 << this.power)) {
                  this.occupiedCount = var6 + 1;
                  break label66;
               }
            }

            this.rehashTable();
            return this.insertNewKey(var1, var2);
         }
      }

      this.keys[var3] = var1;
      this.values[var3 + (1 << this.power)] = var2;
      ++this.keyCount;
      return var3;
   }

   private int findIndex(Object var1) {
      if (this.keys != null) {
         int var2 = var1.hashCode();
         int var3 = -1640531527 * var2;
         int var4 = this.power;
         int var5 = var3 >>> 32 - var4;
         Object var6 = this.keys[var5];
         if (var6 != null) {
            int var7 = 1 << var4;
            if (var6 == var1) {
               return var5;
            }

            if (this.values[var7 + var5] == var2 && var6.equals(var1)) {
               return var5;
            }

            int var8 = var7 - 1;
            int var9 = tableLookupStep(var3, var8, this.power);

            while(true) {
               var5 = var8 & var5 + var9;
               Object var10 = this.keys[var5];
               if (var10 == null) {
                  break;
               }

               if (var10 == var1 || this.values[var7 + var5] == var2 && var10.equals(var1)) {
                  return var5;
               }
            }
         }
      }

      return -1;
   }

   private int insertNewKey(Object var1, int var2) {
      int var3 = -1640531527 * var2;
      int var4 = this.power;
      int var5 = var3 >>> 32 - var4;
      int var6 = 1 << var4;
      if (this.keys[var5] != null) {
         int var7 = var6 - 1;
         int var8 = tableLookupStep(var3, var7, var4);

         do {
            var5 = var7 & var5 + var8;
         } while(this.keys[var5] != null);
      }

      this.keys[var5] = var1;
      this.values[var6 + var5] = var2;
      ++this.occupiedCount;
      ++this.keyCount;
      return var5;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = this.keyCount;
      if (var2 != 0) {
         this.keyCount = 0;
         int var3 = 1 << this.power;
         this.keys = new Object[var3];
         this.values = new int[var3 * 2];

         for(int var4 = 0; var4 != var2; ++var4) {
            Object var5 = var1.readObject();
            int var6 = this.insertNewKey(var5, var5.hashCode());
            this.values[var6] = var1.readInt();
         }
      }

   }

   private void rehashTable() {
      if (this.keys == null) {
         int var9 = 1 << this.power;
         this.keys = new Object[var9];
         this.values = new int[var9 * 2];
      } else {
         if (2 * this.keyCount >= this.occupiedCount) {
            ++this.power;
         }

         int var1 = 1 << this.power;
         Object[] var2 = this.keys;
         int[] var3 = this.values;
         int var4 = var2.length;
         this.keys = new Object[var1];
         this.values = new int[var1 * 2];
         int var5 = this.keyCount;
         this.keyCount = 0;
         this.occupiedCount = 0;

         for(int var6 = 0; var5 != 0; ++var6) {
            Object var7 = var2[var6];
            if (var7 != null && var7 != DELETED) {
               int var8 = this.insertNewKey(var7, var3[var4 + var6]);
               this.values[var8] = var3[var6];
               --var5;
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

      for(int var3 = 0; var2 != 0; ++var3) {
         Object var4 = this.keys[var3];
         if (var4 != null && var4 != DELETED) {
            --var2;
            var1.writeObject(var4);
            var1.writeInt(this.values[var3]);
         }
      }

   }

   public void clear() {
      Object[] var2;
      for(int var1 = this.keys.length; var1 != 0; var2[var1] = null) {
         var2 = this.keys;
         --var1;
      }

      this.keyCount = 0;
      this.occupiedCount = 0;
   }

   public int get(Object var1, int var2) {
      if (var1 == null) {
         var1 = UniqueTag.NULL_VALUE;
      }

      int var3 = this.findIndex(var1);
      return var3 >= 0 ? this.values[var3] : var2;
   }

   public int getExisting(Object var1) {
      if (var1 == null) {
         var1 = UniqueTag.NULL_VALUE;
      }

      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         return this.values[var2];
      } else {
         Kit.codeBug();
         return 0;
      }
   }

   public void getKeys(Object[] var1, int var2) {
      int var3 = this.keyCount;

      for(int var4 = 0; var3 != 0; ++var4) {
         Object var5 = this.keys[var4];
         if (var5 != null && var5 != DELETED) {
            if (var5 == UniqueTag.NULL_VALUE) {
               var5 = null;
            }

            var1[var2] = var5;
            ++var2;
            --var3;
         }
      }

   }

   public Object[] getKeys() {
      Object[] var1 = new Object[this.keyCount];
      this.getKeys(var1, 0);
      return var1;
   }

   public boolean has(Object var1) {
      if (var1 == null) {
         var1 = UniqueTag.NULL_VALUE;
      }

      return this.findIndex(var1) >= 0;
   }

   final void initIterator(ObjToIntMap.Iterator var1) {
      var1.init(this.keys, this.values, this.keyCount);
   }

   public Object intern(Object var1) {
      boolean var2 = false;
      if (var1 == null) {
         var2 = true;
         var1 = UniqueTag.NULL_VALUE;
      }

      int var3 = this.ensureIndex(var1);
      this.values[var3] = 0;
      return var2 ? null : this.keys[var3];
   }

   public boolean isEmpty() {
      return this.keyCount == 0;
   }

   public ObjToIntMap.Iterator newIterator() {
      return new ObjToIntMap.Iterator(this);
   }

   public void put(Object var1, int var2) {
      if (var1 == null) {
         var1 = UniqueTag.NULL_VALUE;
      }

      int var3 = this.ensureIndex(var1);
      this.values[var3] = var2;
   }

   public void remove(Object var1) {
      if (var1 == null) {
         var1 = UniqueTag.NULL_VALUE;
      }

      int var2 = this.findIndex(var1);
      if (var2 >= 0) {
         this.keys[var2] = DELETED;
         this.keyCount += -1;
      }

   }

   public int size() {
      return this.keyCount;
   }

   public static class Iterator {
      private int cursor;
      private Object[] keys;
      ObjToIntMap master;
      private int remaining;
      private int[] values;

      Iterator(ObjToIntMap var1) {
         this.master = var1;
      }

      public boolean done() {
         return this.remaining < 0;
      }

      public Object getKey() {
         Object var1 = this.keys[this.cursor];
         if (var1 == UniqueTag.NULL_VALUE) {
            var1 = null;
         }

         return var1;
      }

      public int getValue() {
         return this.values[this.cursor];
      }

      final void init(Object[] var1, int[] var2, int var3) {
         this.keys = var1;
         this.values = var2;
         this.cursor = -1;
         this.remaining = var3;
      }

      public void next() {
         if (this.remaining == -1) {
            Kit.codeBug();
         }

         if (this.remaining == 0) {
            this.remaining = -1;
            this.cursor = -1;
         } else {
            int var1 = this.cursor;

            while(true) {
               this.cursor = var1 + 1;
               Object var2 = this.keys[this.cursor];
               if (var2 != null && var2 != ObjToIntMap.DELETED) {
                  this.remaining += -1;
                  return;
               }

               var1 = this.cursor;
            }
         }
      }

      public void setValue(int var1) {
         this.values[this.cursor] = var1;
      }

      public void start() {
         this.master.initIterator(this);
         this.next();
      }
   }
}
