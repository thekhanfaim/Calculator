package com.faendir.rhino_android;

import android.content.Context;
import java.io.File;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.SecurityController;

public class RhinoAndroidHelper {
   private final File cacheDirectory;

   public RhinoAndroidHelper() {
      this(new File(System.getProperty("java.io.tmpdir", "."), "classes"));
   }

   public RhinoAndroidHelper(Context var1) {
      this(new File(var1.getCacheDir(), "classes"));
   }

   public RhinoAndroidHelper(File var1) {
      this.cacheDirectory = var1;
   }

   @Deprecated
   public static org.mozilla.javascript.Context prepareContext() {
      return (new RhinoAndroidHelper()).enterContext();
   }

   public org.mozilla.javascript.Context enterContext() {
      if (!SecurityController.hasGlobal()) {
         SecurityController.initGlobal(new NoSecurityController());
      }

      return this.getContextFactory().enterContext();
   }

   public AndroidContextFactory getContextFactory() {
      if (!ContextFactory.hasExplicitGlobal()) {
         AndroidContextFactory var1 = new AndroidContextFactory(this.cacheDirectory);
         ContextFactory.getGlobalSetter().setContextFactoryGlobal(var1);
         return var1;
      } else if (ContextFactory.getGlobal() instanceof AndroidContextFactory) {
         return (AndroidContextFactory)ContextFactory.getGlobal();
      } else {
         throw new IllegalStateException("Cannot initialize factory for Android Rhino: There is already another factory");
      }
   }
}
