package org.mozilla.javascript;

public interface ConstProperties {
   void defineConst(String var1, Scriptable var2);

   boolean isConst(String var1);

   void putConst(String var1, Scriptable var2, Object var3);
}
