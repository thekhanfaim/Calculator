package org.mozilla.javascript;

import java.util.Iterator;

public final class NativeIterator extends IdScriptableObject {
   public static final String ITERATOR_PROPERTY_NAME = "__iterator__";
   private static final Object ITERATOR_TAG = "Iterator";
   private static final int Id___iterator__ = 3;
   private static final int Id_constructor = 1;
   private static final int Id_next = 2;
   private static final int MAX_PROTOTYPE_ID = 3;
   private static final String STOP_ITERATION = "StopIteration";
   private static final long serialVersionUID = -4136968203581667681L;
   private Object objectIterator;

   private NativeIterator() {
   }

   private NativeIterator(Object var1) {
      this.objectIterator = var1;
   }

   private static Iterator getJavaIterator(Object var0) {
      if (var0 instanceof Wrapper) {
         Object var1 = ((Wrapper)var0).unwrap();
         boolean var2 = var1 instanceof Iterator;
         Iterator var3 = null;
         if (var2) {
            var3 = (Iterator)var1;
         }

         if (var1 instanceof Iterable) {
            var3 = ((Iterable)var1).iterator();
         }

         return var3;
      } else {
         return null;
      }
   }

   public static Object getStopIterationObject(Scriptable var0) {
      return ScriptableObject.getTopScopeValue(ScriptableObject.getTopLevelScope(var0), ITERATOR_TAG);
   }

   static void init(ScriptableObject var0, boolean var1) {
      (new NativeIterator()).exportAsJSClass(3, var0, var1);
      NativeGenerator.init(var0, var1);
      NativeObject var4 = new NativeObject() {
         private static final long serialVersionUID = 2485151085722377663L;

         public String getClassName() {
            return "StopIteration";
         }

         public boolean hasInstance(Scriptable var1) {
            return var1 instanceof <undefinedtype>;
         }
      };
      var4.setPrototype(getObjectPrototype(var0));
      var4.setParentScope(var0);
      if (var1) {
         var4.sealObject();
      }

      ScriptableObject.defineProperty(var0, "StopIteration", var4, 2);
      var0.associateValue(ITERATOR_TAG, var4);
   }

   private static Object jsConstructor(Context var0, Scriptable var1, Scriptable var2, Object[] var3) {
      if (var3.length != 0 && var3[0] != null && var3[0] != Undefined.instance) {
         Scriptable var5 = ScriptRuntime.toObject(var0, var1, var3[0]);
         int var6 = var3.length;
         boolean var7 = false;
         if (var6 > 1) {
            boolean var14 = ScriptRuntime.toBoolean(var3[1]);
            var7 = false;
            if (var14) {
               var7 = true;
            }
         }

         if (var2 != null) {
            Iterator var11 = getJavaIterator(var5);
            if (var11 != null) {
               Scriptable var13 = ScriptableObject.getTopLevelScope(var1);
               return var0.getWrapFactory().wrap(var0, var13, new NativeIterator.WrappedJavaIterator(var11, var13), NativeIterator.WrappedJavaIterator.class);
            }

            Scriptable var12 = ScriptRuntime.toIterator(var0, var1, var5, var7);
            if (var12 != null) {
               return var12;
            }
         }

         byte var8;
         if (var7) {
            var8 = 3;
         } else {
            var8 = 5;
         }

         Object var9 = ScriptRuntime.enumInit(var5, var0, var1, var8);
         ScriptRuntime.setEnumNumbers(var9, true);
         NativeIterator var10 = new NativeIterator(var9);
         var10.setPrototype(ScriptableObject.getClassPrototype(var1, var10.getClassName()));
         var10.setParentScope(var1);
         return var10;
      } else {
         Object var4;
         if (var3.length == 0) {
            var4 = Undefined.instance;
         } else {
            var4 = var3[0];
         }

         throw ScriptRuntime.typeError1("msg.no.properties", ScriptRuntime.toString(var4));
      }
   }

   private Object next(Context var1, Scriptable var2) {
      if (ScriptRuntime.enumNext(this.objectIterator)) {
         return ScriptRuntime.enumId(this.objectIterator, var1);
      } else {
         throw new JavaScriptException(getStopIterationObject(var2), (String)null, 0);
      }
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(ITERATOR_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 == 1) {
            return jsConstructor(var2, var3, var4, var5);
         } else if (var4 instanceof NativeIterator) {
            NativeIterator var7 = (NativeIterator)var4;
            if (var6 != 2) {
               if (var6 == 3) {
                  return var4;
               } else {
                  throw new IllegalArgumentException(String.valueOf(var6));
               }
            } else {
               return var7.next(var2, var3);
            }
         } else {
            throw incompatibleCallError(var1);
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 4) {
         var4 = "next";
         var3 = 2;
      } else if (var2 == 11) {
         var4 = "constructor";
         var3 = 1;
      } else {
         var3 = 0;
         var4 = null;
         if (var2 == 12) {
            var4 = "__iterator__";
            var3 = 3;
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Iterator";
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               throw new IllegalArgumentException(String.valueOf(var1));
            }

            var2 = 1;
            var3 = "__iterator__";
         } else {
            var3 = "next";
            var2 = 0;
         }
      } else {
         var2 = 2;
         var3 = "constructor";
      }

      this.initPrototypeMethod(ITERATOR_TAG, var1, var3, var2);
   }

   public static class WrappedJavaIterator {
      private Iterator iterator;
      private Scriptable scope;

      WrappedJavaIterator(Iterator var1, Scriptable var2) {
         this.iterator = var1;
         this.scope = var2;
      }

      public Object __iterator__(boolean var1) {
         return this;
      }

      public Object next() {
         if (this.iterator.hasNext()) {
            return this.iterator.next();
         } else {
            throw new JavaScriptException(NativeIterator.getStopIterationObject(this.scope), (String)null, 0);
         }
      }
   }
}
