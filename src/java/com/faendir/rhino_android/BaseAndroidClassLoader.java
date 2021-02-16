package com.faendir.rhino_android;

import com.android.dex.Dex;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.command.dexer.DxContext;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.DexFile;
import com.android.dx.merge.CollisionPolicy;
import com.android.dx.merge.DexMerger;
import java.io.IOException;
import java.io.Writer;
import org.mozilla.javascript.GeneratedClassLoader;

abstract class BaseAndroidClassLoader extends ClassLoader implements GeneratedClassLoader {
   public BaseAndroidClassLoader(ClassLoader var1) {
      super(var1);
   }

   public Class defineClass(String var1, byte[] var2) {
      Object var6;
      IOException var21;
      label44: {
         ClassNotFoundException var10000;
         label39: {
            DxContext var11;
            Dex var12;
            Dex var13;
            boolean var10001;
            try {
               DexOptions var3 = new DexOptions();
               DexFile var4 = new DexFile(var3);
               StringBuilder var5 = new StringBuilder();
               var5.append(var1.replace('.', '/'));
               var5.append(".class");
               DirectClassFile var9 = new DirectClassFile(var2, var5.toString(), true);
               var9.setAttributeFactory(StdAttributeFactory.THE_ONE);
               var9.getMagic();
               var11 = new DxContext();
               var4.add(CfTranslator.translate(var11, var9, (byte[])null, new CfOptions(), var3, var4));
               var12 = new Dex(var4.toDex((Writer)null, false));
               var13 = this.getLastDex();
            } catch (IOException var19) {
               var21 = var19;
               var10001 = false;
               break label44;
            } catch (ClassNotFoundException var20) {
               var10000 = var20;
               var10001 = false;
               break label39;
            }

            if (var13 != null) {
               try {
                  var12 = (new DexMerger(new Dex[]{var12, var13}, CollisionPolicy.KEEP_FIRST, var11)).merge();
               } catch (IOException var17) {
                  var21 = var17;
                  var10001 = false;
                  break label44;
               } catch (ClassNotFoundException var18) {
                  var10000 = var18;
                  var10001 = false;
                  break label39;
               }
            }

            try {
               Class var14 = this.loadClass(var12, var1);
               return var14;
            } catch (IOException var15) {
               var21 = var15;
               var10001 = false;
               break label44;
            } catch (ClassNotFoundException var16) {
               var10000 = var16;
               var10001 = false;
            }
         }

         var6 = var10000;
         throw new BaseAndroidClassLoader.FatalLoadingException((Throwable)var6);
      }

      var6 = var21;
      throw new BaseAndroidClassLoader.FatalLoadingException((Throwable)var6);
   }

   protected abstract Dex getLastDex();

   public void linkClass(Class var1) {
   }

   protected abstract Class loadClass(Dex var1, String var2) throws ClassNotFoundException;

   public Class loadClass(String var1, boolean var2) throws ClassNotFoundException {
      Class var3 = this.findLoadedClass(var1);
      if (var3 == null) {
         Dex var4 = this.getLastDex();
         if (var4 != null) {
            var3 = this.loadClass(var4, var1);
         }

         if (var3 == null) {
            var3 = this.getParent().loadClass(var1);
         }
      }

      return var3;
   }

   protected abstract void reset();

   public static class FatalLoadingException extends RuntimeException {
      FatalLoadingException(Throwable var1) {
         super("Failed to define class", var1);
      }
   }
}
