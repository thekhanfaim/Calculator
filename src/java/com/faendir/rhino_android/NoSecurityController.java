package com.faendir.rhino_android;

import java.io.Serializable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.SecurityController;

class NoSecurityController extends SecurityController implements Serializable {
   public GeneratedClassLoader createClassLoader(ClassLoader var1, Object var2) {
      return Context.getCurrentContext().createClassLoader(var1);
   }

   public Object getDynamicSecurityDomain(Object var1) {
      return null;
   }
}
