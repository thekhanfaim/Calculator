package org.mozilla.javascript;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassCache implements Serializable {
   private static final Object AKEY = "ClassCache";
   private static final long serialVersionUID = -8866246036237312215L;
   private Scriptable associatedScope;
   private volatile boolean cachingIsEnabled = true;
   private transient Map classAdapterCache;
   private transient Map classTable;
   private int generatedClassSerial;
   private transient Map interfaceAdapterCache;

   public static ClassCache get(Scriptable var0) {
      ClassCache var1 = (ClassCache)ScriptableObject.getTopScopeValue(var0, AKEY);
      if (var1 != null) {
         return var1;
      } else {
         throw new RuntimeException("Can't find top level scope for ClassCache.get");
      }
   }

   public boolean associate(ScriptableObject var1) {
      if (var1.getParentScope() == null) {
         if (this == var1.associateValue(AKEY, this)) {
            this.associatedScope = var1;
            return true;
         } else {
            return false;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   void cacheInterfaceAdapter(Class var1, Object var2) {
      synchronized(this){}

      try {
         if (this.cachingIsEnabled) {
            if (this.interfaceAdapterCache == null) {
               this.interfaceAdapterCache = new ConcurrentHashMap(16, 0.75F, 1);
            }

            this.interfaceAdapterCache.put(var1, var2);
         }
      } finally {
         ;
      }

   }

   public void clearCaches() {
      synchronized(this){}

      try {
         this.classTable = null;
         this.classAdapterCache = null;
         this.interfaceAdapterCache = null;
      } finally {
         ;
      }

   }

   Scriptable getAssociatedScope() {
      return this.associatedScope;
   }

   Map getClassCacheMap() {
      if (this.classTable == null) {
         this.classTable = new ConcurrentHashMap(16, 0.75F, 1);
      }

      return this.classTable;
   }

   Object getInterfaceAdapter(Class var1) {
      Map var2 = this.interfaceAdapterCache;
      return var2 == null ? null : var2.get(var1);
   }

   Map getInterfaceAdapterCacheMap() {
      if (this.classAdapterCache == null) {
         this.classAdapterCache = new ConcurrentHashMap(16, 0.75F, 1);
      }

      return this.classAdapterCache;
   }

   public final boolean isCachingEnabled() {
      return this.cachingIsEnabled;
   }

   @Deprecated
   public boolean isInvokerOptimizationEnabled() {
      return false;
   }

   public final int newClassSerialNumber() {
      synchronized(this){}

      int var2;
      try {
         var2 = 1 + this.generatedClassSerial;
         this.generatedClassSerial = var2;
      } finally {
         ;
      }

      return var2;
   }

   public void setCachingEnabled(boolean var1) {
      synchronized(this){}

      Throwable var10000;
      label132: {
         boolean var10001;
         boolean var3;
         try {
            var3 = this.cachingIsEnabled;
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label132;
         }

         if (var1 == var3) {
            return;
         }

         if (!var1) {
            try {
               this.clearCaches();
            } catch (Throwable var15) {
               var10000 = var15;
               var10001 = false;
               break label132;
            }
         }

         label119:
         try {
            this.cachingIsEnabled = var1;
            return;
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            break label119;
         }
      }

      Throwable var2 = var10000;
      throw var2;
   }

   @Deprecated
   public void setInvokerOptimizationEnabled(boolean var1) {
      synchronized(this){}
   }
}
