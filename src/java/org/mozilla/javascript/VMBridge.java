package org.mozilla.javascript;

import java.lang.reflect.AccessibleObject;

public abstract class VMBridge {
   static final VMBridge instance = makeInstance();

   private static VMBridge makeInstance() {
      String[] var0 = new String[]{"org.mozilla.javascript.VMBridge_custom", "org.mozilla.javascript.jdk18.VMBridge_jdk18"};

      for(int var1 = 0; var1 != var0.length; ++var1) {
         Class var3 = Kit.classOrNull(var0[var1]);
         if (var3 != null) {
            VMBridge var4 = (VMBridge)Kit.newInstanceOrNull(var3);
            if (var4 != null) {
               return var4;
            }
         }
      }

      IllegalStateException var2 = new IllegalStateException("Failed to create VMBridge instance");
      throw var2;
   }

   protected abstract Context getContext(Object var1);

   protected abstract Object getInterfaceProxyHelper(ContextFactory var1, Class[] var2);

   protected abstract Object getThreadContextHelper();

   protected abstract Object newInterfaceProxy(Object var1, ContextFactory var2, InterfaceAdapter var3, Object var4, Scriptable var5);

   protected abstract void setContext(Object var1, Context var2);

   protected abstract boolean tryToMakeAccessible(AccessibleObject var1);
}
