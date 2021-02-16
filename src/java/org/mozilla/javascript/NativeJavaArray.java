package org.mozilla.javascript;

import java.lang.reflect.Array;

public class NativeJavaArray extends NativeJavaObject implements SymbolScriptable {
   private static final long serialVersionUID = -924022554283675333L;
   Object array;
   Class cls;
   int length;

   public NativeJavaArray(Scriptable var1, Object var2) {
      super(var1, (Object)null, ScriptRuntime.ObjectClass);
      Class var3 = var2.getClass();
      if (var3.isArray()) {
         this.array = var2;
         this.length = Array.getLength(var2);
         this.cls = var3.getComponentType();
      } else {
         throw new RuntimeException("Array expected");
      }
   }

   public static NativeJavaArray wrap(Scriptable var0, Object var1) {
      return new NativeJavaArray(var0, var1);
   }

   public void delete(Symbol var1) {
   }

   public Object get(int var1, Scriptable var2) {
      if (var1 >= 0 && var1 < this.length) {
         Context var3 = Context.getContext();
         Object var4 = Array.get(this.array, var1);
         return var3.getWrapFactory().wrap(var3, this, var4, this.cls);
      } else {
         return Undefined.instance;
      }
   }

   public Object get(String var1, Scriptable var2) {
      if (var1.equals("length")) {
         return this.length;
      } else {
         Object var3 = super.get(var1, var2);
         if (var3 == NOT_FOUND) {
            if (ScriptableObject.hasProperty(this.getPrototype(), var1)) {
               return var3;
            } else {
               throw Context.reportRuntimeError2("msg.java.member.not.found", this.array.getClass().getName(), var1);
            }
         } else {
            return var3;
         }
      }
   }

   public Object get(Symbol var1, Scriptable var2) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(var1) ? true : Scriptable.NOT_FOUND;
   }

   public String getClassName() {
      return "JavaArray";
   }

   public Object getDefaultValue(Class var1) {
      if (var1 != null && var1 != ScriptRuntime.StringClass) {
         if (var1 == ScriptRuntime.BooleanClass) {
            return Boolean.TRUE;
         } else {
            return var1 == ScriptRuntime.NumberClass ? ScriptRuntime.NaNobj : this;
         }
      } else {
         return this.array.toString();
      }
   }

   public Object[] getIds() {
      Object[] var1 = new Object[this.length];
      int var2 = this.length;

      while(true) {
         --var2;
         if (var2 < 0) {
            return var1;
         }

         var1[var2] = var2;
      }
   }

   public Scriptable getPrototype() {
      if (this.prototype == null) {
         this.prototype = ScriptableObject.getArrayPrototype(this.getParentScope());
      }

      return this.prototype;
   }

   public boolean has(int var1, Scriptable var2) {
      return var1 >= 0 && var1 < this.length;
   }

   public boolean has(String var1, Scriptable var2) {
      return var1.equals("length") || super.has(var1, var2);
   }

   public boolean has(Symbol var1, Scriptable var2) {
      return SymbolKey.IS_CONCAT_SPREADABLE.equals(var1);
   }

   public boolean hasInstance(Scriptable var1) {
      if (!(var1 instanceof Wrapper)) {
         return false;
      } else {
         Object var2 = ((Wrapper)var1).unwrap();
         return this.cls.isInstance(var2);
      }
   }

   public void put(int var1, Scriptable var2, Object var3) {
      if (var1 >= 0 && var1 < this.length) {
         Array.set(this.array, var1, Context.jsToJava(var3, this.cls));
      } else {
         throw Context.reportRuntimeError2("msg.java.array.index.out.of.bounds", String.valueOf(var1), String.valueOf(-1 + this.length));
      }
   }

   public void put(String var1, Scriptable var2, Object var3) {
      if (!var1.equals("length")) {
         throw Context.reportRuntimeError1("msg.java.array.member.not.found", var1);
      }
   }

   public Object unwrap() {
      return this.array;
   }
}
