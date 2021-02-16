package org.mozilla.javascript;

public class RhinoSecurityManager extends SecurityManager {
   protected Class getCurrentScriptClass() {
      Class[] var1 = this.getClassContext();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Class var4 = var1[var3];
         if (var4 != InterpretedFunction.class && NativeFunction.class.isAssignableFrom(var4) || PolicySecurityController.SecureCaller.class.isAssignableFrom(var4)) {
            return var4;
         }
      }

      return null;
   }
}
