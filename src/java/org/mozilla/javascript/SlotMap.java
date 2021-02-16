package org.mozilla.javascript;

public interface SlotMap extends Iterable {
   void addSlot(ScriptableObject.Slot var1);

   ScriptableObject.Slot get(Object var1, int var2, ScriptableObject.SlotAccess var3);

   boolean isEmpty();

   ScriptableObject.Slot query(Object var1, int var2);

   void remove(Object var1, int var2);

   int size();
}
