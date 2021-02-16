package org.mozilla.javascript;

import java.io.Serializable;

public abstract class Ref implements Serializable {
   private static final long serialVersionUID = 4044540354730911424L;

   public boolean delete(Context var1) {
      return false;
   }

   public abstract Object get(Context var1);

   public boolean has(Context var1) {
      return true;
   }

   @Deprecated
   public abstract Object set(Context var1, Object var2);

   public Object set(Context var1, Scriptable var2, Object var3) {
      return this.set(var1, var3);
   }
}
