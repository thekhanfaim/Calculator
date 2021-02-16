package com.faendir.rhino_android;

import com.android.dex.Dex;
import dalvik.system.InMemoryDexClassLoader;
import java.nio.ByteBuffer;

class InMemoryAndroidClassLoader extends BaseAndroidClassLoader {
   private Dex last;

   public InMemoryAndroidClassLoader(ClassLoader var1) {
      super(var1);
   }

   protected Dex getLastDex() {
      return this.last;
   }

   protected Class loadClass(Dex var1, String var2) throws ClassNotFoundException {
      this.last = var1;
      return (new InMemoryDexClassLoader(ByteBuffer.wrap(var1.getBytes()), this.getParent())).loadClass(var2);
   }

   protected void reset() {
      this.last = null;
   }
}
