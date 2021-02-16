package org.mozilla.javascript;

class SpecialRef extends Ref {
   private static final int SPECIAL_NONE = 0;
   private static final int SPECIAL_PARENT = 2;
   private static final int SPECIAL_PROTO = 1;
   private static final long serialVersionUID = -7521596632456797847L;
   private String name;
   private Scriptable target;
   private int type;

   private SpecialRef(Scriptable var1, int var2, String var3) {
      this.target = var1;
      this.type = var2;
      this.name = var3;
   }

   static Ref createSpecial(Context var0, Scriptable var1, Object var2, String var3) {
      Scriptable var4 = ScriptRuntime.toObjectOrNull(var0, var2, var1);
      if (var4 != null) {
         byte var5;
         if (var3.equals("__proto__")) {
            var5 = 1;
         } else {
            if (!var3.equals("__parent__")) {
               throw new IllegalArgumentException(var3);
            }

            var5 = 2;
         }

         if (!var0.hasFeature(5)) {
            var5 = 0;
         }

         return new SpecialRef(var4, var5, var3);
      } else {
         throw ScriptRuntime.undefReadError(var2, var3);
      }
   }

   public boolean delete(Context var1) {
      return this.type == 0 ? ScriptRuntime.deleteObjectElem(this.target, this.name, var1) : false;
   }

   public Object get(Context var1) {
      int var2 = this.type;
      if (var2 != 0) {
         if (var2 != 1) {
            if (var2 == 2) {
               return this.target.getParentScope();
            } else {
               throw Kit.codeBug();
            }
         } else {
            return this.target.getPrototype();
         }
      } else {
         return ScriptRuntime.getObjectProp(this.target, this.name, var1);
      }
   }

   public boolean has(Context var1) {
      return this.type == 0 ? ScriptRuntime.hasObjectElem(this.target, this.name, var1) : true;
   }

   @Deprecated
   public Object set(Context var1, Object var2) {
      throw new IllegalStateException();
   }

   public Object set(Context var1, Scriptable var2, Object var3) {
      int var4 = this.type;
      if (var4 != 0) {
         if (var4 != 1 && var4 != 2) {
            throw Kit.codeBug();
         } else {
            Scriptable var5 = ScriptRuntime.toObjectOrNull(var1, var3, var2);
            if (var5 != null) {
               label46: {
                  Scriptable var6 = var5;

                  while(var6 != this.target) {
                     if (this.type == 1) {
                        var6 = var6.getPrototype();
                     } else {
                        var6 = var6.getParentScope();
                     }

                     if (var6 == null) {
                        break label46;
                     }
                  }

                  throw Context.reportRuntimeError1("msg.cyclic.value", this.name);
               }
            }

            if (this.type == 1) {
               this.target.setPrototype(var5);
               return var5;
            } else {
               this.target.setParentScope(var5);
               return var5;
            }
         }
      } else {
         return ScriptRuntime.setObjectProp(this.target, this.name, var3, var1);
      }
   }
}
