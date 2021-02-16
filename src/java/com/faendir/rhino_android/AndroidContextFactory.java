package com.faendir.rhino_android;

import android.os.Build.VERSION;
import java.io.File;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class AndroidContextFactory extends ContextFactory {
   private final File cacheDirectory;

   public AndroidContextFactory(File var1) {
      this.cacheDirectory = var1;
      this.initApplicationClassLoader(this.createClassLoader(AndroidContextFactory.class.getClassLoader()));
   }

   public BaseAndroidClassLoader createClassLoader(ClassLoader var1) {
      return (BaseAndroidClassLoader)(VERSION.SDK_INT >= 26 ? new InMemoryAndroidClassLoader(var1) : new FileAndroidClassLoader(var1, this.cacheDirectory));
   }

   protected void onContextReleased(Context var1) {
      super.onContextReleased(var1);
      ((BaseAndroidClassLoader)var1.getApplicationClassLoader()).reset();
   }
}
