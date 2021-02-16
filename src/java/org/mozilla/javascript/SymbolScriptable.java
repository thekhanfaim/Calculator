package org.mozilla.javascript;

public interface SymbolScriptable {
   void delete(Symbol var1);

   Object get(Symbol var1, Scriptable var2);

   boolean has(Symbol var1, Scriptable var2);

   void put(Symbol var1, Scriptable var2, Object var3);
}
