package org.mozilla.javascript;

import java.util.EnumMap;

public class TopLevel extends IdScriptableObject {
   // $FF: synthetic field
   static final boolean $assertionsDisabled = false;
   private static final long serialVersionUID = -4648046356662472260L;
   private EnumMap ctors;
   private EnumMap errors;

   public static Function getBuiltinCtor(Context var0, Scriptable var1, TopLevel.Builtins var2) {
      if (var1.getParentScope() == null) {
         if (var1 instanceof TopLevel) {
            BaseFunction var3 = ((TopLevel)var1).getBuiltinCtor(var2);
            if (var3 != null) {
               return var3;
            }
         }

         return ScriptRuntime.getExistingCtor(var0, var1, var2.name());
      } else {
         throw new AssertionError();
      }
   }

   public static Scriptable getBuiltinPrototype(Scriptable var0, TopLevel.Builtins var1) {
      if (var0.getParentScope() == null) {
         if (var0 instanceof TopLevel) {
            Scriptable var2 = ((TopLevel)var0).getBuiltinPrototype(var1);
            if (var2 != null) {
               return var2;
            }
         }

         return ScriptableObject.getClassPrototype(var0, var1.name());
      } else {
         throw new AssertionError();
      }
   }

   static Function getNativeErrorCtor(Context var0, Scriptable var1, TopLevel.NativeErrors var2) {
      if (var1.getParentScope() == null) {
         if (var1 instanceof TopLevel) {
            BaseFunction var3 = ((TopLevel)var1).getNativeErrorCtor(var2);
            if (var3 != null) {
               return var3;
            }
         }

         return ScriptRuntime.getExistingCtor(var0, var1, var2.name());
      } else {
         throw new AssertionError();
      }
   }

   public void cacheBuiltins() {
      this.ctors = new EnumMap(TopLevel.Builtins.class);
      TopLevel.Builtins[] var1 = TopLevel.Builtins.values();
      int var2 = var1.length;
      int var3 = 0;

      for(int var4 = 0; var4 < var2; ++var4) {
         TopLevel.Builtins var10 = var1[var4];
         Object var11 = ScriptableObject.getProperty(this, (String)var10.name());
         if (var11 instanceof BaseFunction) {
            this.ctors.put(var10, (BaseFunction)var11);
         }
      }

      this.errors = new EnumMap(TopLevel.NativeErrors.class);
      TopLevel.NativeErrors[] var5 = TopLevel.NativeErrors.values();

      for(int var6 = var5.length; var3 < var6; ++var3) {
         TopLevel.NativeErrors var7 = var5[var3];
         Object var8 = ScriptableObject.getProperty(this, (String)var7.name());
         if (var8 instanceof BaseFunction) {
            this.errors.put(var7, (BaseFunction)var8);
         }
      }

   }

   public BaseFunction getBuiltinCtor(TopLevel.Builtins var1) {
      EnumMap var2 = this.ctors;
      return var2 != null ? (BaseFunction)var2.get(var1) : null;
   }

   public Scriptable getBuiltinPrototype(TopLevel.Builtins var1) {
      BaseFunction var2 = this.getBuiltinCtor(var1);
      Object var3;
      if (var2 != null) {
         var3 = var2.getPrototypeProperty();
      } else {
         var3 = null;
      }

      boolean var4 = var3 instanceof Scriptable;
      Scriptable var5 = null;
      if (var4) {
         var5 = (Scriptable)var3;
      }

      return var5;
   }

   public String getClassName() {
      return "global";
   }

   BaseFunction getNativeErrorCtor(TopLevel.NativeErrors var1) {
      EnumMap var2 = this.errors;
      return var2 != null ? (BaseFunction)var2.get(var1) : null;
   }

   public static enum Builtins {
      Array,
      Boolean,
      Error,
      Function,
      Number,
      Object,
      RegExp,
      String,
      Symbol;

      static {
         TopLevel.Builtins var0 = new TopLevel.Builtins("Object", 0);
         Object = var0;
         TopLevel.Builtins var1 = new TopLevel.Builtins("Array", 1);
         Array = var1;
         TopLevel.Builtins var2 = new TopLevel.Builtins("Function", 2);
         Function = var2;
         TopLevel.Builtins var3 = new TopLevel.Builtins("String", 3);
         String = var3;
         TopLevel.Builtins var4 = new TopLevel.Builtins("Number", 4);
         Number = var4;
         TopLevel.Builtins var5 = new TopLevel.Builtins("Boolean", 5);
         Boolean = var5;
         TopLevel.Builtins var6 = new TopLevel.Builtins("RegExp", 6);
         RegExp = var6;
         TopLevel.Builtins var7 = new TopLevel.Builtins("Error", 7);
         Error = var7;
         TopLevel.Builtins var8 = new TopLevel.Builtins("Symbol", 8);
         Symbol = var8;
      }
   }

   static enum NativeErrors {
      Error,
      EvalError,
      InternalError,
      JavaException,
      RangeError,
      ReferenceError,
      SyntaxError,
      TypeError,
      URIError;

      static {
         TopLevel.NativeErrors var0 = new TopLevel.NativeErrors("Error", 0);
         Error = var0;
         TopLevel.NativeErrors var1 = new TopLevel.NativeErrors("EvalError", 1);
         EvalError = var1;
         TopLevel.NativeErrors var2 = new TopLevel.NativeErrors("RangeError", 2);
         RangeError = var2;
         TopLevel.NativeErrors var3 = new TopLevel.NativeErrors("ReferenceError", 3);
         ReferenceError = var3;
         TopLevel.NativeErrors var4 = new TopLevel.NativeErrors("SyntaxError", 4);
         SyntaxError = var4;
         TopLevel.NativeErrors var5 = new TopLevel.NativeErrors("TypeError", 5);
         TypeError = var5;
         TopLevel.NativeErrors var6 = new TopLevel.NativeErrors("URIError", 6);
         URIError = var6;
         TopLevel.NativeErrors var7 = new TopLevel.NativeErrors("InternalError", 7);
         InternalError = var7;
         TopLevel.NativeErrors var8 = new TopLevel.NativeErrors("JavaException", 8);
         JavaException = var8;
      }
   }
}
