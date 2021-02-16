package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.mozilla.javascript.xml.XMLLib;

public class ContextFactory {
   private static ContextFactory global = new ContextFactory();
   private static volatile boolean hasCustomGlobal;
   private ClassLoader applicationClassLoader;
   private boolean disabledListening;
   private volatile Object listeners;
   private final Object listenersLock = new Object();
   private volatile boolean sealed;

   // $FF: synthetic method
   static ContextFactory access$000() {
      return global;
   }

   // $FF: synthetic method
   static ContextFactory access$002(ContextFactory var0) {
      global = var0;
      return var0;
   }

   public static ContextFactory getGlobal() {
      return global;
   }

   public static ContextFactory.GlobalSetter getGlobalSetter() {
      Class var2 = ContextFactory.class;
      synchronized(ContextFactory.class){}

      ContextFactory$1GlobalSetterImpl var1;
      try {
         if (hasCustomGlobal) {
            throw new IllegalStateException();
         }

         hasCustomGlobal = true;
         var1 = new ContextFactory$1GlobalSetterImpl();
      } finally {
         ;
      }

      return var1;
   }

   public static boolean hasExplicitGlobal() {
      return hasCustomGlobal;
   }

   public static void initGlobal(ContextFactory var0) {
      Class var2 = ContextFactory.class;
      synchronized(ContextFactory.class){}
      Throwable var10000;
      boolean var10001;
      if (var0 == null) {
         label112:
         try {
            throw new IllegalArgumentException();
         } catch (Throwable var13) {
            var10000 = var13;
            var10001 = false;
            break label112;
         }
      } else {
         label126: {
            try {
               if (!hasCustomGlobal) {
                  hasCustomGlobal = true;
                  global = var0;
                  return;
               }
            } catch (Throwable var14) {
               var10000 = var14;
               var10001 = false;
               break label126;
            }

            label110:
            try {
               throw new IllegalStateException();
            } catch (Throwable var12) {
               var10000 = var12;
               var10001 = false;
               break label110;
            }
         }
      }

      Throwable var1 = var10000;
      throw var1;
   }

   private boolean isDom3Present() {
      Class var1 = Kit.classOrNull("org.w3c.dom.Node");
      if (var1 == null) {
         return false;
      } else {
         try {
            var1.getMethod("getUserData", new Class[]{String.class});
            return true;
         } catch (NoSuchMethodException var3) {
            return false;
         }
      }
   }

   public final void addListener(ContextFactory.Listener var1) {
      this.checkNotSealed();
      Object var2 = this.listenersLock;
      synchronized(var2){}

      Throwable var10000;
      boolean var10001;
      label122: {
         try {
            if (!this.disabledListening) {
               this.listeners = Kit.addListener(this.listeners, var1);
               return;
            }
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label122;
         }

         label116:
         try {
            throw new IllegalStateException();
         } catch (Throwable var15) {
            var10000 = var15;
            var10001 = false;
            break label116;
         }
      }

      while(true) {
         Throwable var3 = var10000;

         try {
            throw var3;
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            continue;
         }
      }
   }

   public final Object call(ContextAction var1) {
      return Context.call(this, var1);
   }

   protected final void checkNotSealed() {
      if (this.sealed) {
         throw new IllegalStateException();
      }
   }

   protected GeneratedClassLoader createClassLoader(final ClassLoader var1) {
      return (GeneratedClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public DefiningClassLoader run() {
            return new DefiningClassLoader(var1);
         }
      });
   }

   final void disableContextListening() {
      // $FF: Couldn't be decompiled
   }

   protected Object doTopCall(Callable var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      Object var6 = var1.call(var2, var3, var4, var5);
      return var6 instanceof ConsString ? var6.toString() : var6;
   }

   @Deprecated
   public final Context enter() {
      return this.enterContext((Context)null);
   }

   public Context enterContext() {
      return this.enterContext((Context)null);
   }

   public final Context enterContext(Context var1) {
      return Context.enter(var1, this);
   }

   @Deprecated
   public final void exit() {
      Context.exit();
   }

   public final ClassLoader getApplicationClassLoader() {
      return this.applicationClassLoader;
   }

   protected XMLLib.Factory getE4xImplementationFactory() {
      return this.isDom3Present() ? XMLLib.Factory.create("org.mozilla.javascript.xmlimpl.XMLLibImpl") : null;
   }

   protected boolean hasFeature(Context var1, int var2) {
      boolean var3 = true;
      switch(var2) {
      case 1:
         int var4 = var1.getLanguageVersion();
         if (var4 != 100 && var4 != 110) {
            if (var4 == 120) {
               return var3;
            }

            var3 = false;
         }

         return var3;
      case 2:
         return false;
      case 3:
         return var3;
      case 4:
         if (var1.getLanguageVersion() == 120) {
            return var3;
         }

         return false;
      case 5:
         return var3;
      case 6:
         int var5 = var1.getLanguageVersion();
         if (var5 != 0) {
            if (var5 >= 160) {
               return var3;
            }

            var3 = false;
         }

         return var3;
      case 7:
         return false;
      case 8:
         return false;
      case 9:
         return false;
      case 10:
         return false;
      case 11:
         return false;
      case 12:
         return false;
      case 13:
         return false;
      case 14:
         return var3;
      case 15:
         if (var1.getLanguageVersion() <= 170) {
            return var3;
         }

         return false;
      case 16:
         if (var1.getLanguageVersion() >= 200) {
            return var3;
         }

         return false;
      case 17:
         return false;
      case 18:
         return false;
      case 19:
         return false;
      default:
         throw new IllegalArgumentException(String.valueOf(var2));
      }
   }

   public final void initApplicationClassLoader(ClassLoader var1) {
      if (var1 != null) {
         if (Kit.testIfCanLoadRhinoClasses(var1)) {
            if (this.applicationClassLoader == null) {
               this.checkNotSealed();
               this.applicationClassLoader = var1;
            } else {
               throw new IllegalStateException("applicationClassLoader can only be set once");
            }
         } else {
            throw new IllegalArgumentException("Loader can not resolve Rhino classes");
         }
      } else {
         throw new IllegalArgumentException("loader is null");
      }
   }

   public final boolean isSealed() {
      return this.sealed;
   }

   protected Context makeContext() {
      return new Context(this);
   }

   protected void observeInstructionCount(Context var1, int var2) {
   }

   protected void onContextCreated(Context var1) {
      Object var2 = this.listeners;
      int var3 = 0;

      while(true) {
         ContextFactory.Listener var4 = (ContextFactory.Listener)Kit.getListener(var2, var3);
         if (var4 == null) {
            return;
         }

         var4.contextCreated(var1);
         ++var3;
      }
   }

   protected void onContextReleased(Context var1) {
      Object var2 = this.listeners;
      int var3 = 0;

      while(true) {
         ContextFactory.Listener var4 = (ContextFactory.Listener)Kit.getListener(var2, var3);
         if (var4 == null) {
            return;
         }

         var4.contextReleased(var1);
         ++var3;
      }
   }

   public final void removeListener(ContextFactory.Listener var1) {
      this.checkNotSealed();
      Object var2 = this.listenersLock;
      synchronized(var2){}

      Throwable var10000;
      boolean var10001;
      label122: {
         try {
            if (!this.disabledListening) {
               this.listeners = Kit.removeListener(this.listeners, var1);
               return;
            }
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label122;
         }

         label116:
         try {
            throw new IllegalStateException();
         } catch (Throwable var15) {
            var10000 = var15;
            var10001 = false;
            break label116;
         }
      }

      while(true) {
         Throwable var3 = var10000;

         try {
            throw var3;
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            continue;
         }
      }
   }

   public final void seal() {
      this.checkNotSealed();
      this.sealed = true;
   }

   public interface GlobalSetter {
      ContextFactory getContextFactoryGlobal();

      void setContextFactoryGlobal(ContextFactory var1);
   }

   public interface Listener {
      void contextCreated(Context var1);

      void contextReleased(Context var1);
   }
}
