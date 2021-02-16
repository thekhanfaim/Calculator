package org.mozilla.javascript;

import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

class ThreadSafeSlotMapContainer extends SlotMapContainer {
   // $FF: synthetic field
   static final boolean $assertionsDisabled;
   private final StampedLock lock = new StampedLock();

   ThreadSafeSlotMapContainer(int var1) {
      super(var1);
   }

   public void addSlot(ScriptableObject.Slot var1) {
      long var2 = this.lock.writeLock();

      try {
         this.checkMapSize();
         this.map.addSlot(var1);
      } finally {
         this.lock.unlockWrite(var2);
      }

   }

   protected void checkMapSize() {
      if (this.lock.isWriteLocked()) {
         super.checkMapSize();
      } else {
         throw new AssertionError();
      }
   }

   public int dirtySize() {
      if (this.lock.isReadLocked()) {
         return this.map.size();
      } else {
         throw new AssertionError();
      }
   }

   public ScriptableObject.Slot get(Object var1, int var2, ScriptableObject.SlotAccess var3) {
      long var4 = this.lock.writeLock();

      ScriptableObject.Slot var7;
      try {
         if (var3 != ScriptableObject.SlotAccess.QUERY) {
            this.checkMapSize();
         }

         var7 = this.map.get(var1, var2, var3);
      } finally {
         this.lock.unlockWrite(var4);
      }

      return var7;
   }

   public boolean isEmpty() {
      long var1 = this.lock.tryOptimisticRead();
      boolean var3 = this.map.isEmpty();
      if (this.lock.validate(var1)) {
         return var3;
      } else {
         long var4 = this.lock.readLock();

         boolean var7;
         try {
            var7 = this.map.isEmpty();
         } finally {
            this.lock.unlockRead(var4);
         }

         return var7;
      }
   }

   public Iterator iterator() {
      if (this.lock.isReadLocked()) {
         return this.map.iterator();
      } else {
         throw new AssertionError();
      }
   }

   public ScriptableObject.Slot query(Object var1, int var2) {
      long var3 = this.lock.tryOptimisticRead();
      ScriptableObject.Slot var5 = this.map.query(var1, var2);
      if (this.lock.validate(var3)) {
         return var5;
      } else {
         long var6 = this.lock.readLock();

         ScriptableObject.Slot var9;
         try {
            var9 = this.map.query(var1, var2);
         } finally {
            this.lock.unlockRead(var6);
         }

         return var9;
      }
   }

   public long readLock() {
      return this.lock.readLock();
   }

   public void remove(Object var1, int var2) {
      long var3 = this.lock.writeLock();

      try {
         this.map.remove(var1, var2);
      } finally {
         this.lock.unlockWrite(var3);
      }

   }

   public int size() {
      long var1 = this.lock.tryOptimisticRead();
      int var3 = this.map.size();
      if (this.lock.validate(var1)) {
         return var3;
      } else {
         long var4 = this.lock.readLock();

         int var7;
         try {
            var7 = this.map.size();
         } finally {
            this.lock.unlockRead(var4);
         }

         return var7;
      }
   }

   public void unlockRead(long var1) {
      this.lock.unlockRead(var1);
   }
}
