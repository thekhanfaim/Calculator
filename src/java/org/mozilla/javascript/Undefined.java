package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Undefined implements Serializable {
   public static final Scriptable SCRIPTABLE_UNDEFINED = (Scriptable)Proxy.newProxyInstance(Undefined.class.getClassLoader(), new Class[]{Scriptable.class}, new InvocationHandler() {
      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         if (var2.getName().equals("toString")) {
            return "undefined";
         } else if (var2.getName().equals("equals")) {
            int var7 = var3.length;
            boolean var8 = false;
            if (var7 > 0) {
               boolean var9 = Undefined.isUndefined(var3[0]);
               var8 = false;
               if (var9) {
                  var8 = true;
               }
            }

            return var8;
         } else {
            StringBuilder var4 = new StringBuilder();
            var4.append("undefined doesn't support ");
            var4.append(var2.getName());
            throw new UnsupportedOperationException(var4.toString());
         }
      }
   });
   public static final Object instance = new Undefined();
   private static final long serialVersionUID = 9195680630202616767L;

   private Undefined() {
   }

   public static boolean isUndefined(Object var0) {
      return instance == var0 || SCRIPTABLE_UNDEFINED == var0;
   }

   public boolean equals(Object var1) {
      return isUndefined(var1) || super.equals(var1);
   }

   public int hashCode() {
      return 0;
   }

   public Object readResolve() {
      return instance;
   }
}
