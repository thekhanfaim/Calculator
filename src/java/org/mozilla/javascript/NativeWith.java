package org.mozilla.javascript;

import java.io.Serializable;

public class NativeWith implements Scriptable, SymbolScriptable, IdFunctionCall, Serializable {
   private static final Object FTAG = "With";
   private static final int Id_constructor = 1;
   private static final long serialVersionUID = 1L;
   protected Scriptable parent;
   protected Scriptable prototype;

   private NativeWith() {
   }

   protected NativeWith(Scriptable var1, Scriptable var2) {
      this.parent = var1;
      this.prototype = var2;
   }

   static void init(Scriptable var0, boolean var1) {
      NativeWith var2 = new NativeWith();
      var2.setParentScope(var0);
      var2.setPrototype(ScriptableObject.getObjectPrototype(var0));
      IdFunctionObject var3 = new IdFunctionObject(var2, FTAG, 1, "With", 0, var0);
      var3.markAsConstructor(var2);
      if (var1) {
         var3.sealObject();
      }

      var3.exportAsScopeProperty();
   }

   static boolean isWithFunction(Object var0) {
      if (var0 instanceof IdFunctionObject) {
         IdFunctionObject var1 = (IdFunctionObject)var0;
         boolean var2 = var1.hasTag(FTAG);
         boolean var3 = false;
         if (var2) {
            int var4 = var1.methodId();
            var3 = false;
            if (var4 == 1) {
               var3 = true;
            }
         }

         return var3;
      } else {
         return false;
      }
   }

   static Object newWithSpecial(Context var0, Scriptable var1, Object[] var2) {
      ScriptRuntime.checkDeprecated(var0, "With");
      Scriptable var3 = ScriptableObject.getTopLevelScope(var1);
      NativeWith var4 = new NativeWith();
      Scriptable var5;
      if (var2.length == 0) {
         var5 = ScriptableObject.getObjectPrototype(var3);
      } else {
         var5 = ScriptRuntime.toObject(var0, var3, var2[0]);
      }

      var4.setPrototype(var5);
      var4.setParentScope(var3);
      return var4;
   }

   public void delete(int var1) {
      this.prototype.delete(var1);
   }

   public void delete(String var1) {
      this.prototype.delete(var1);
   }

   public void delete(Symbol var1) {
      Scriptable var2 = this.prototype;
      if (var2 instanceof SymbolScriptable) {
         ((SymbolScriptable)var2).delete(var1);
      }

   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (var1.hasTag(FTAG) && var1.methodId() == 1) {
         throw Context.reportRuntimeError1("msg.cant.call.indirect", "With");
      } else {
         throw var1.unknown();
      }
   }

   public Object get(int var1, Scriptable var2) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      return this.prototype.get(var1, var2);
   }

   public Object get(String var1, Scriptable var2) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      return this.prototype.get(var1, var2);
   }

   public Object get(Symbol var1, Scriptable var2) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      Scriptable var3 = this.prototype;
      return var3 instanceof SymbolScriptable ? ((SymbolScriptable)var3).get(var1, var2) : Scriptable.NOT_FOUND;
   }

   public String getClassName() {
      return "With";
   }

   public Object getDefaultValue(Class var1) {
      return this.prototype.getDefaultValue(var1);
   }

   public Object[] getIds() {
      return this.prototype.getIds();
   }

   public Scriptable getParentScope() {
      return this.parent;
   }

   public Scriptable getPrototype() {
      return this.prototype;
   }

   public boolean has(int var1, Scriptable var2) {
      Scriptable var3 = this.prototype;
      return var3.has(var1, var3);
   }

   public boolean has(String var1, Scriptable var2) {
      Scriptable var3 = this.prototype;
      return var3.has(var1, var3);
   }

   public boolean has(Symbol var1, Scriptable var2) {
      Scriptable var3 = this.prototype;
      return var3 instanceof SymbolScriptable ? ((SymbolScriptable)var3).has(var1, var3) : false;
   }

   public boolean hasInstance(Scriptable var1) {
      return this.prototype.hasInstance(var1);
   }

   public void put(int var1, Scriptable var2, Object var3) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      this.prototype.put(var1, var2, var3);
   }

   public void put(String var1, Scriptable var2, Object var3) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      this.prototype.put(var1, var2, var3);
   }

   public void put(Symbol var1, Scriptable var2, Object var3) {
      if (var2 == this) {
         var2 = this.prototype;
      }

      Scriptable var4 = this.prototype;
      if (var4 instanceof SymbolScriptable) {
         ((SymbolScriptable)var4).put(var1, var2, var3);
      }

   }

   public void setParentScope(Scriptable var1) {
      this.parent = var1;
   }

   public void setPrototype(Scriptable var1) {
      this.prototype = var1;
   }

   protected Object updateDotQuery(boolean var1) {
      throw new IllegalStateException();
   }
}
