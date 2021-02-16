package org.mozilla.javascript;

public class Synchronizer extends Delegator {
   private Object syncObject;

   public Synchronizer(Scriptable var1) {
      super(var1);
   }

   public Synchronizer(Scriptable var1, Object var2) {
      super(var1);
      this.syncObject = var2;
   }

   public Object call(Context param1, Scriptable param2, Scriptable param3, Object[] param4) {
      // $FF: Couldn't be decompiled
   }
}
