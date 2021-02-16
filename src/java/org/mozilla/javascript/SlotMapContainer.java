package org.mozilla.javascript;

import java.util.Iterator;

class SlotMapContainer implements SlotMap {
   private static final int LARGE_HASH_SIZE = 2000;
   protected SlotMap map;

   SlotMapContainer(int var1) {
      if (var1 > 2000) {
         this.map = new HashSlotMap();
      } else {
         this.map = new EmbeddedSlotMap();
      }
   }

   public void addSlot(ScriptableObject.Slot var1) {
      this.checkMapSize();
      this.map.addSlot(var1);
   }

   protected void checkMapSize() {
      SlotMap var1 = this.map;
      if (var1 instanceof EmbeddedSlotMap && var1.size() >= 2000) {
         HashSlotMap var2 = new HashSlotMap();
         Iterator var3 = this.map.iterator();

         while(var3.hasNext()) {
            var2.addSlot((ScriptableObject.Slot)var3.next());
         }

         this.map = var2;
      }

   }

   public int dirtySize() {
      return this.map.size();
   }

   public ScriptableObject.Slot get(Object var1, int var2, ScriptableObject.SlotAccess var3) {
      if (var3 != ScriptableObject.SlotAccess.QUERY) {
         this.checkMapSize();
      }

      return this.map.get(var1, var2, var3);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public Iterator iterator() {
      return this.map.iterator();
   }

   public ScriptableObject.Slot query(Object var1, int var2) {
      return this.map.query(var1, var2);
   }

   public long readLock() {
      return 0L;
   }

   public void remove(Object var1, int var2) {
      this.map.remove(var1, var2);
   }

   public int size() {
      return this.map.size();
   }

   public void unlockRead(long var1) {
   }
}
