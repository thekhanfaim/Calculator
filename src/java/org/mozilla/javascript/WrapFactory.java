package org.mozilla.javascript;

public class WrapFactory {
   private boolean javaPrimitiveWrap = true;

   public final boolean isJavaPrimitiveWrap() {
      return this.javaPrimitiveWrap;
   }

   public final void setJavaPrimitiveWrap(boolean var1) {
      Context var2 = Context.getCurrentContext();
      if (var2 != null && var2.isSealed()) {
         Context.onSealedMutation();
      }

      this.javaPrimitiveWrap = var1;
   }

   public Object wrap(Context var1, Scriptable var2, Object var3, Class var4) {
      if (var3 != null && var3 != Undefined.instance) {
         if (var3 instanceof Scriptable) {
            return var3;
         } else if (var4 != null && var4.isPrimitive()) {
            if (var4 == Void.TYPE) {
               return Undefined.instance;
            } else {
               return var4 == Character.TYPE ? Integer.valueOf((Character)var3) : var3;
            }
         } else {
            if (!this.isJavaPrimitiveWrap()) {
               if (var3 instanceof String || var3 instanceof Boolean || var3 instanceof Integer || var3 instanceof Short || var3 instanceof Long || var3 instanceof Float) {
                  return var3;
               }

               if (var3 instanceof Double) {
                  return var3;
               }

               if (var3 instanceof Character) {
                  return String.valueOf((Character)var3);
               }
            }

            return var3.getClass().isArray() ? NativeJavaArray.wrap(var2, var3) : this.wrapAsJavaObject(var1, var2, var3, var4);
         }
      } else {
         return var3;
      }
   }

   public Scriptable wrapAsJavaObject(Context var1, Scriptable var2, Object var3, Class var4) {
      return new NativeJavaObject(var2, var3, var4);
   }

   public Scriptable wrapJavaClass(Context var1, Scriptable var2, Class var3) {
      return new NativeJavaClass(var2, var3);
   }

   public Scriptable wrapNewObject(Context var1, Scriptable var2, Object var3) {
      if (var3 instanceof Scriptable) {
         return (Scriptable)var3;
      } else {
         return (Scriptable)(var3.getClass().isArray() ? NativeJavaArray.wrap(var2, var3) : this.wrapAsJavaObject(var1, var2, var3, (Class)null));
      }
   }
}
