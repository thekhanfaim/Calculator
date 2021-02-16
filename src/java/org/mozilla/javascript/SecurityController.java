package org.mozilla.javascript;

public abstract class SecurityController {
   private static SecurityController global;

   public static GeneratedClassLoader createLoader(ClassLoader var0, Object var1) {
      Context var2 = Context.getContext();
      if (var0 == null) {
         var0 = var2.getApplicationClassLoader();
      }

      SecurityController var3 = var2.getSecurityController();
      return var3 == null ? var2.createClassLoader(var0) : var3.createClassLoader(var0, var3.getDynamicSecurityDomain(var1));
   }

   public static Class getStaticSecurityDomainClass() {
      SecurityController var0 = Context.getContext().getSecurityController();
      return var0 == null ? null : var0.getStaticSecurityDomainClassInternal();
   }

   static SecurityController global() {
      return global;
   }

   public static boolean hasGlobal() {
      return global != null;
   }

   public static void initGlobal(SecurityController var0) {
      if (var0 != null) {
         if (global == null) {
            global = var0;
         } else {
            throw new SecurityException("Cannot overwrite already installed global SecurityController");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public Object callWithDomain(Object var1, Context var2, final Callable var3, Scriptable var4, final Scriptable var5, final Object[] var6) {
      return this.execWithDomain(var2, var4, new Script() {
         public Object exec(Context var1, Scriptable var2) {
            return var3.call(var1, var2, var5, var6);
         }
      }, var1);
   }

   public abstract GeneratedClassLoader createClassLoader(ClassLoader var1, Object var2);

   @Deprecated
   public Object execWithDomain(Context var1, Scriptable var2, Script var3, Object var4) {
      throw new IllegalStateException("callWithDomain should be overridden");
   }

   public abstract Object getDynamicSecurityDomain(Object var1);

   public Class getStaticSecurityDomainClassInternal() {
      return null;
   }
}
