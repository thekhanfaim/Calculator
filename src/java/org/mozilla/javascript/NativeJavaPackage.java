package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

public class NativeJavaPackage extends ScriptableObject {
   private static final long serialVersionUID = 7445054382212031523L;
   private transient ClassLoader classLoader;
   private Set negativeCache;
   private String packageName;

   @Deprecated
   public NativeJavaPackage(String var1) {
      this(false, var1, Context.getCurrentContext().getApplicationClassLoader());
   }

   @Deprecated
   public NativeJavaPackage(String var1, ClassLoader var2) {
      this(false, var1, var2);
   }

   NativeJavaPackage(boolean var1, String var2, ClassLoader var3) {
      this.negativeCache = null;
      this.packageName = var2;
      this.classLoader = var3;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.classLoader = Context.getCurrentContext().getApplicationClassLoader();
   }

   public boolean equals(Object var1) {
      if (var1 instanceof NativeJavaPackage) {
         NativeJavaPackage var2 = (NativeJavaPackage)var1;
         boolean var3 = this.packageName.equals(var2.packageName);
         boolean var4 = false;
         if (var3) {
            ClassLoader var5 = this.classLoader;
            ClassLoader var6 = var2.classLoader;
            var4 = false;
            if (var5 == var6) {
               var4 = true;
            }
         }

         return var4;
      } else {
         return false;
      }
   }

   NativeJavaPackage forcePackage(String var1, Scriptable var2) {
      Object var3 = super.get((String)var1, this);
      if (var3 != null && var3 instanceof NativeJavaPackage) {
         return (NativeJavaPackage)var3;
      } else {
         String var8;
         if (this.packageName.length() == 0) {
            var8 = var1;
         } else {
            StringBuilder var4 = new StringBuilder();
            var4.append(this.packageName);
            var4.append(".");
            var4.append(var1);
            var8 = var4.toString();
         }

         NativeJavaPackage var9 = new NativeJavaPackage(true, var8, this.classLoader);
         ScriptRuntime.setObjectProtoAndParent(var9, var2);
         super.put((String)var1, this, var9);
         return var9;
      }
   }

   public Object get(int var1, Scriptable var2) {
      return NOT_FOUND;
   }

   public Object get(String var1, Scriptable var2) {
      return this.getPkgProperty(var1, var2, true);
   }

   public String getClassName() {
      return "JavaPackage";
   }

   public Object getDefaultValue(Class var1) {
      return this.toString();
   }

   Object getPkgProperty(String var1, Scriptable var2, boolean var3) {
      synchronized(this){}

      Throwable var10000;
      label1856: {
         Object var5;
         Object var6;
         boolean var10001;
         try {
            var5 = super.get(var1, var2);
            var6 = NOT_FOUND;
         } catch (Throwable var262) {
            var10000 = var262;
            var10001 = false;
            break label1856;
         }

         if (var5 != var6) {
            return var5;
         }

         Set var7;
         try {
            var7 = this.negativeCache;
         } catch (Throwable var261) {
            var10000 = var261;
            var10001 = false;
            break label1856;
         }

         if (var7 != null) {
            boolean var8;
            try {
               var8 = var7.contains(var1);
            } catch (Throwable var260) {
               var10000 = var260;
               var10001 = false;
               break label1856;
            }

            if (var8) {
               return null;
            }
         }

         String var9;
         label1838: {
            label1837: {
               try {
                  if (this.packageName.length() != 0) {
                     break label1837;
                  }
               } catch (Throwable var259) {
                  var10000 = var259;
                  var10001 = false;
                  break label1856;
               }

               var9 = var1;
               break label1838;
            }

            try {
               StringBuilder var18 = new StringBuilder();
               var18.append(this.packageName);
               var18.append('.');
               var18.append(var1);
               var9 = var18.toString();
            } catch (Throwable var258) {
               var10000 = var258;
               var10001 = false;
               break label1856;
            }
         }

         Context var10;
         ClassShutter var11;
         try {
            var10 = Context.getContext();
            var11 = var10.getClassShutter();
         } catch (Throwable var257) {
            var10000 = var257;
            var10001 = false;
            break label1856;
         }

         Object var13;
         label1857: {
            if (var11 != null) {
               boolean var12;
               try {
                  var12 = var11.visibleToScripts(var9);
               } catch (Throwable var256) {
                  var10000 = var256;
                  var10001 = false;
                  break label1856;
               }

               var13 = null;
               if (!var12) {
                  break label1857;
               }
            }

            ClassLoader var16;
            try {
               var16 = this.classLoader;
            } catch (Throwable var255) {
               var10000 = var255;
               var10001 = false;
               break label1856;
            }

            Class var17;
            if (var16 != null) {
               try {
                  var17 = Kit.classOrNull(var16, var9);
               } catch (Throwable var254) {
                  var10000 = var254;
                  var10001 = false;
                  break label1856;
               }
            } else {
               try {
                  var17 = Kit.classOrNull(var9);
               } catch (Throwable var253) {
                  var10000 = var253;
                  var10001 = false;
                  break label1856;
               }
            }

            var13 = null;
            if (var17 != null) {
               try {
                  var13 = var10.getWrapFactory().wrapJavaClass(var10, getTopLevelScope(this), var17);
                  ((Scriptable)var13).setPrototype(this.getPrototype());
               } catch (Throwable var252) {
                  var10000 = var252;
                  var10001 = false;
                  break label1856;
               }
            }
         }

         if (var13 == null) {
            if (var3) {
               NativeJavaPackage var14;
               try {
                  var14 = new NativeJavaPackage(true, var9, this.classLoader);
                  ScriptRuntime.setObjectProtoAndParent(var14, this.getParentScope());
               } catch (Throwable var250) {
                  var10000 = var250;
                  var10001 = false;
                  break label1856;
               }

               var13 = var14;
            } else {
               try {
                  if (this.negativeCache == null) {
                     this.negativeCache = new HashSet();
                  }
               } catch (Throwable var251) {
                  var10000 = var251;
                  var10001 = false;
                  break label1856;
               }

               try {
                  this.negativeCache.add(var1);
               } catch (Throwable var249) {
                  var10000 = var249;
                  var10001 = false;
                  break label1856;
               }
            }
         }

         if (var13 != null) {
            try {
               super.put(var1, var2, var13);
            } catch (Throwable var248) {
               var10000 = var248;
               var10001 = false;
               break label1856;
            }
         }

         return var13;
      }

      Throwable var4 = var10000;
      throw var4;
   }

   public boolean has(int var1, Scriptable var2) {
      return false;
   }

   public boolean has(String var1, Scriptable var2) {
      return true;
   }

   public int hashCode() {
      int var1 = this.packageName.hashCode();
      ClassLoader var2 = this.classLoader;
      int var3;
      if (var2 == null) {
         var3 = 0;
      } else {
         var3 = var2.hashCode();
      }

      return var1 ^ var3;
   }

   public void put(int var1, Scriptable var2, Object var3) {
      throw Context.reportRuntimeError0("msg.pkg.int");
   }

   public void put(String var1, Scriptable var2, Object var3) {
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[JavaPackage ");
      var1.append(this.packageName);
      var1.append("]");
      return var1.toString();
   }
}
