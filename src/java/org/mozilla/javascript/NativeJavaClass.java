package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Map;

public class NativeJavaClass extends NativeJavaObject implements Function {
   static final String javaClassPropertyName = "__javaObject__";
   private static final long serialVersionUID = -6460763940409461664L;
   private Map staticFieldAndMethods;

   public NativeJavaClass() {
   }

   public NativeJavaClass(Scriptable var1, Class var2) {
      this(var1, var2, false);
   }

   public NativeJavaClass(Scriptable var1, Class var2, boolean var3) {
      super(var1, var2, (Class)null, var3);
   }

   static Object constructInternal(Object[] var0, MemberBox var1) {
      Class[] var2 = var1.argTypes;
      if (var1.vararg) {
         Object[] var7 = new Object[var2.length];

         for(int var8 = 0; var8 < -1 + var2.length; ++var8) {
            var7[var8] = Context.jsToJava(var0[var8], var2[var8]);
         }

         Object var12;
         if (var0.length != var2.length || var0[-1 + var0.length] != null && !(var0[-1 + var0.length] instanceof NativeArray) && !(var0[-1 + var0.length] instanceof NativeJavaArray)) {
            Class var9 = var2[-1 + var2.length].getComponentType();
            Object var10 = Array.newInstance(var9, 1 + (var0.length - var2.length));

            for(int var11 = 0; var11 < Array.getLength(var10); ++var11) {
               Array.set(var10, var11, Context.jsToJava(var0[var11 + -1 + var2.length], var9));
            }

            var12 = var10;
         } else {
            var12 = Context.jsToJava(var0[-1 + var0.length], var2[-1 + var2.length]);
         }

         var7[-1 + var2.length] = var12;
         var0 = var7;
      } else {
         Object[] var3 = var0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            Object var5 = var0[var4];
            Object var6 = Context.jsToJava(var5, var2[var4]);
            if (var6 != var5) {
               if (var0 == var3) {
                  var0 = (Object[])var3.clone();
               }

               var0[var4] = var6;
            }
         }
      }

      return var1.newInstance(var0);
   }

   static Scriptable constructSpecific(Context var0, Scriptable var1, Object[] var2, MemberBox var3) {
      Object var4 = constructInternal(var2, var3);
      Scriptable var5 = ScriptableObject.getTopLevelScope(var1);
      return var0.getWrapFactory().wrapNewObject(var0, var5, var4);
   }

   private static Class findNestedClass(Class var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(var0.getName());
      var2.append('$');
      var2.append(var1);
      String var6 = var2.toString();
      ClassLoader var7 = var0.getClassLoader();
      return var7 == null ? Kit.classOrNull(var6) : Kit.classOrNull(var7, var6);
   }

   public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      if (var4.length == 1 && var4[0] instanceof Scriptable) {
         Class var5 = this.getClassObject();
         Scriptable var6 = (Scriptable)var4[0];

         do {
            if (var6 instanceof Wrapper && var5.isInstance(((Wrapper)var6).unwrap())) {
               return var6;
            }

            var6 = var6.getPrototype();
         } while(var6 != null);
      }

      return this.construct(var1, var2, var4);
   }

   public Scriptable construct(Context var1, Scriptable var2, Object[] var3) {
      Class var4 = this.getClassObject();
      int var5 = var4.getModifiers();
      if (!Modifier.isInterface(var5) && !Modifier.isAbstract(var5)) {
         NativeJavaMethod var15 = this.members.ctors;
         int var16 = var15.findCachedFunction(var1, var3);
         if (var16 >= 0) {
            return constructSpecific(var1, var2, var3, var15.methods[var16]);
         } else {
            String var17 = NativeJavaMethod.scriptSignature(var3);
            throw Context.reportRuntimeError2("msg.no.java.ctor", var4.getName(), var17);
         }
      } else if (var3.length != 0) {
         Scriptable var6 = ScriptableObject.getTopLevelScope(this);
         String var7 = "";

         try {
            if ("Dalvik".equals(System.getProperty("java.vm.name")) && var4.isInterface()) {
               Object var14 = createInterfaceAdapter(var4, ScriptableObject.ensureScriptableObject(var3[0]));
               return var1.getWrapFactory().wrapAsJavaObject(var1, var2, var14, (Class)null);
            }

            Object var10 = var6.get("JavaAdapter", var6);
            if (var10 != NOT_FOUND) {
               Function var11 = (Function)var10;
               Object[] var12 = new Object[]{this, var3[0]};
               Scriptable var13 = var11.construct(var1, var6, var12);
               return var13;
            }
         } catch (Exception var18) {
            String var9 = var18.getMessage();
            if (var9 != null) {
               var7 = var9;
            }
         }

         throw Context.reportRuntimeError2("msg.cant.instantiate", var7, var4.getName());
      } else {
         throw Context.reportRuntimeError0("msg.adapter.zero.args");
      }
   }

   public Object get(String var1, Scriptable var2) {
      if (var1.equals("prototype")) {
         return null;
      } else {
         Map var3 = this.staticFieldAndMethods;
         if (var3 != null) {
            Object var9 = var3.get(var1);
            if (var9 != null) {
               return var9;
            }
         }

         if (this.members.has(var1, true)) {
            return this.members.get(this, var1, this.javaObject, true);
         } else {
            Context var4 = Context.getContext();
            Scriptable var5 = ScriptableObject.getTopLevelScope(var2);
            WrapFactory var6 = var4.getWrapFactory();
            if ("__javaObject__".equals(var1)) {
               return var6.wrap(var4, var5, this.javaObject, ScriptRuntime.ClassClass);
            } else {
               Class var7 = findNestedClass(this.getClassObject(), var1);
               if (var7 != null) {
                  Scriptable var8 = var6.wrapJavaClass(var4, var5, var7);
                  var8.setParentScope(this);
                  return var8;
               } else {
                  throw this.members.reportMemberNotFound(var1);
               }
            }
         }
      }
   }

   public String getClassName() {
      return "JavaClass";
   }

   public Class getClassObject() {
      return (Class)super.unwrap();
   }

   public Object getDefaultValue(Class var1) {
      if (var1 != null && var1 != ScriptRuntime.StringClass) {
         if (var1 == ScriptRuntime.BooleanClass) {
            return Boolean.TRUE;
         } else {
            return var1 == ScriptRuntime.NumberClass ? ScriptRuntime.NaNobj : this;
         }
      } else {
         return this.toString();
      }
   }

   public Object[] getIds() {
      return this.members.getIds(true);
   }

   public boolean has(String var1, Scriptable var2) {
      JavaMembers var3 = this.members;
      boolean var4 = true;
      if (!var3.has(var1, var4)) {
         if ("__javaObject__".equals(var1)) {
            return var4;
         }

         var4 = false;
      }

      return var4;
   }

   public boolean hasInstance(Scriptable var1) {
      if (var1 instanceof Wrapper && !(var1 instanceof NativeJavaClass)) {
         Object var2 = ((Wrapper)var1).unwrap();
         return this.getClassObject().isInstance(var2);
      } else {
         return false;
      }
   }

   protected void initMembers() {
      Class var1 = (Class)this.javaObject;
      this.members = JavaMembers.lookupClass(this.parent, var1, var1, this.isAdapter);
      this.staticFieldAndMethods = this.members.getFieldAndMethodsObjects(this, var1, true);
   }

   public void put(String var1, Scriptable var2, Object var3) {
      this.members.put(this, var1, this.javaObject, var3, true);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[JavaClass ");
      var1.append(this.getClassObject().getName());
      var1.append("]");
      return var1.toString();
   }
}
