package com.faendir.rhino_android;

import com.android.dex.Dex;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.io.IOException;

class FileAndroidClassLoader extends BaseAndroidClassLoader {
   private static int instanceCounter = 0;
   private final File dexFile;

   public FileAndroidClassLoader(ClassLoader var1, File var2) {
      super(var1);
      int var3 = instanceCounter++;
      StringBuilder var4 = new StringBuilder();
      var4.append(var3);
      var4.append(".dex");
      this.dexFile = new File(var2, var4.toString());
      var2.mkdirs();
      this.reset();
   }

   protected Dex getLastDex() {
      if (this.dexFile.exists()) {
         try {
            Dex var1 = new Dex(this.dexFile);
            return var1;
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

      return null;
   }

   protected Class loadClass(Dex var1, String var2) throws ClassNotFoundException {
      try {
         var1.writeTo(this.dexFile);
      } catch (IOException var4) {
         var4.printStackTrace();
      }

      return (new PathClassLoader(this.dexFile.getPath(), this.getParent())).loadClass(var2);
   }

   protected void reset() {
      this.dexFile.delete();
   }
}
