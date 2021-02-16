package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

public class SecurityUtilities {
   public static ProtectionDomain getProtectionDomain(final Class var0) {
      return (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction() {
         public ProtectionDomain run() {
            return var0.getProtectionDomain();
         }
      });
   }

   public static ProtectionDomain getScriptProtectionDomain() {
      final SecurityManager var0 = System.getSecurityManager();
      return var0 instanceof RhinoSecurityManager ? (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction() {
         public ProtectionDomain run() {
            Class var1 = ((RhinoSecurityManager)var0).getCurrentScriptClass();
            return var1 == null ? null : var1.getProtectionDomain();
         }
      }) : null;
   }

   public static String getSystemProperty(final String var0) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public String run() {
            return System.getProperty(var0);
         }
      });
   }
}
