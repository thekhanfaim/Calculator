package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.Method;

final class NativeError extends IdScriptableObject {
   private static final int ConstructorId_captureStackTrace = -1;
   public static final int DEFAULT_STACK_LIMIT = -1;
   private static final Method ERROR_DELEGATE_GET_STACK;
   private static final Method ERROR_DELEGATE_SET_STACK;
   private static final Object ERROR_TAG = "Error";
   private static final int Id_constructor = 1;
   private static final int Id_toSource = 3;
   private static final int Id_toString = 2;
   private static final int MAX_PROTOTYPE_ID = 3;
   private static final String STACK_HIDE_KEY = "_stackHide";
   private static final long serialVersionUID = -5338413581437645187L;
   private RhinoException stackProvider;

   static {
      try {
         ERROR_DELEGATE_GET_STACK = NativeError.class.getMethod("getStackDelegated", new Class[]{Scriptable.class});
         ERROR_DELEGATE_SET_STACK = NativeError.class.getMethod("setStackDelegated", new Class[]{Scriptable.class, Object.class});
      } catch (NoSuchMethodException var1) {
         throw new RuntimeException(var1);
      }
   }

   private Object callPrepareStack(Function var1, ScriptStackElement[] var2) {
      Context var3 = Context.getCurrentContext();
      Object[] var4 = new Object[var2.length];

      for(int var5 = 0; var5 < var2.length; ++var5) {
         NativeCallSite var6 = (NativeCallSite)var3.newObject(this, "CallSite");
         var6.setElement(var2[var5]);
         var4[var5] = var6;
      }

      return var1.call(var3, var1, this, new Object[]{this, var3.newArray(this, var4)});
   }

   static void init(Scriptable var0, boolean var1) {
      NativeError var2 = new NativeError();
      ScriptableObject.putProperty(var2, (String)"name", "Error");
      ScriptableObject.putProperty(var2, (String)"message", "");
      ScriptableObject.putProperty(var2, (String)"fileName", "");
      ScriptableObject.putProperty(var2, (String)"lineNumber", 0);
      var2.setAttributes("name", 2);
      var2.setAttributes("message", 2);
      var2.exportAsJSClass(3, var0, var1);
      NativeCallSite.init(var2, var1);
   }

   private static void js_captureStackTrace(Context var0, Scriptable var1, Object[] var2) {
      ScriptableObject var3 = (ScriptableObject)ScriptRuntime.toObjectOrNull(var0, var2[0], var1);
      Function var4;
      if (var2.length > 1) {
         var4 = (Function)ScriptRuntime.toObjectOrNull(var0, var2[1], var1);
      } else {
         var4 = null;
      }

      NativeError var5 = (NativeError)var0.newObject(var1, "Error");
      var5.setStackProvider(new EvaluatorException("[object Object]"));
      if (var4 != null) {
         Object var6 = var4.get("name", var4);
         if (var6 != null && !Undefined.instance.equals(var6)) {
            var5.associateValue("_stackHide", Context.toString(var6));
         }
      }

      var3.defineProperty("stack", var5, ERROR_DELEGATE_GET_STACK, ERROR_DELEGATE_SET_STACK, 0);
   }

   private static String js_toSource(Context var0, Scriptable var1, Scriptable var2) {
      Object var3 = ScriptableObject.getProperty(var2, "name");
      Object var4 = ScriptableObject.getProperty(var2, "message");
      Object var5 = ScriptableObject.getProperty(var2, "fileName");
      Object var6 = ScriptableObject.getProperty(var2, "lineNumber");
      StringBuilder var7 = new StringBuilder();
      var7.append("(new ");
      if (var3 == NOT_FOUND) {
         var3 = Undefined.instance;
      }

      var7.append(ScriptRuntime.toString(var3));
      var7.append("(");
      if (var4 != NOT_FOUND || var5 != NOT_FOUND || var6 != NOT_FOUND) {
         if (var4 == NOT_FOUND) {
            var4 = "";
         }

         var7.append(ScriptRuntime.uneval(var0, var1, var4));
         if (var5 != NOT_FOUND || var6 != NOT_FOUND) {
            var7.append(", ");
            if (var5 == NOT_FOUND) {
               var5 = "";
            }

            var7.append(ScriptRuntime.uneval(var0, var1, var5));
            if (var6 != NOT_FOUND) {
               int var15 = ScriptRuntime.toInt32(var6);
               if (var15 != 0) {
                  var7.append(", ");
                  var7.append(ScriptRuntime.toString((double)var15));
               }
            }
         }
      }

      var7.append("))");
      return var7.toString();
   }

   private static Object js_toString(Scriptable var0) {
      Object var1 = ScriptableObject.getProperty(var0, "name");
      String var2;
      if (var1 != NOT_FOUND && var1 != Undefined.instance) {
         var2 = ScriptRuntime.toString(var1);
      } else {
         var2 = "Error";
      }

      Object var3 = ScriptableObject.getProperty(var0, "message");
      String var4;
      if (var3 != NOT_FOUND && var3 != Undefined.instance) {
         var4 = ScriptRuntime.toString(var3);
      } else {
         var4 = "";
      }

      if (var2.toString().length() == 0) {
         return var4;
      } else if (var4.toString().length() == 0) {
         return var2;
      } else {
         StringBuilder var5 = new StringBuilder();
         var5.append((String)var2);
         var5.append(": ");
         var5.append((String)var4);
         return var5.toString();
      }
   }

   static NativeError make(Context var0, Scriptable var1, IdFunctionObject var2, Object[] var3) {
      Scriptable var4 = (Scriptable)((Scriptable)var2.get("prototype", var2));
      NativeError var5 = new NativeError();
      var5.setPrototype(var4);
      var5.setParentScope(var1);
      int var6 = var3.length;
      if (var6 >= 1) {
         if (var3[0] != Undefined.instance) {
            ScriptableObject.putProperty(var5, (String)"message", ScriptRuntime.toString(var3[0]));
         }

         if (var6 >= 2) {
            ScriptableObject.putProperty(var5, (String)"fileName", var3[1]);
            if (var6 >= 3) {
               ScriptableObject.putProperty(var5, (String)"lineNumber", ScriptRuntime.toInt32(var3[2]));
            }
         }
      }

      return var5;
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(ERROR_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 != -1) {
            if (var6 != 1) {
               if (var6 != 2) {
                  if (var6 == 3) {
                     return js_toSource(var2, var3, var4);
                  } else {
                     throw new IllegalArgumentException(String.valueOf(var6));
                  }
               } else {
                  return js_toString(var4);
               }
            } else {
               return make(var2, var3, var1, var5);
            }
         } else {
            js_captureStackTrace(var2, var4, var5);
            return Undefined.instance;
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      this.addIdFunctionProperty(var1, ERROR_TAG, -1, "captureStackTrace", 2);
      NativeError.ProtoProps var2 = new NativeError.ProtoProps();
      this.associateValue("_ErrorPrototypeProps", var2);
      var1.defineProperty("stackTraceLimit", var2, NativeError.ProtoProps.GET_STACK_LIMIT, NativeError.ProtoProps.SET_STACK_LIMIT, 0);
      var1.defineProperty("prepareStackTrace", var2, NativeError.ProtoProps.GET_PREPARE_STACK, NativeError.ProtoProps.SET_PREPARE_STACK, 0);
      super.fillConstructorProperties(var1);
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 8) {
         char var5 = var1.charAt(3);
         if (var5 == 'o') {
            var4 = "toSource";
            var3 = 3;
         } else {
            var3 = 0;
            var4 = null;
            if (var5 == 't') {
               var4 = "toString";
               var3 = 2;
            }
         }
      } else {
         var3 = 0;
         var4 = null;
         if (var2 == 11) {
            var4 = "constructor";
            var3 = 1;
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "Error";
   }

   public Object getStackDelegated(Scriptable var1) {
      if (this.stackProvider == null) {
         return NOT_FOUND;
      } else {
         int var2 = -1;
         NativeError.ProtoProps var3 = (NativeError.ProtoProps)((NativeError)this.getPrototype()).getAssociatedValue("_ErrorPrototypeProps");
         Function var4 = null;
         if (var3 != null) {
            var2 = var3.getStackTraceLimit();
            var4 = var3.getPrepareStackTrace();
         }

         String var5 = (String)this.getAssociatedValue("_stackHide");
         ScriptStackElement[] var6 = this.stackProvider.getScriptStack(var2, var5);
         Object var7;
         if (var4 == null) {
            var7 = RhinoException.formatStackTrace(var6, this.stackProvider.details());
         } else {
            var7 = this.callPrepareStack(var4, var6);
         }

         this.setStackDelegated(var1, var7);
         return var7;
      }
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               throw new IllegalArgumentException(String.valueOf(var1));
            }

            var3 = "toSource";
            var2 = 0;
         } else {
            var3 = "toString";
            var2 = 0;
         }
      } else {
         var2 = 1;
         var3 = "constructor";
      }

      this.initPrototypeMethod(ERROR_TAG, var1, var3, var2);
   }

   public void setStackDelegated(Scriptable var1, Object var2) {
      var1.delete("stack");
      this.stackProvider = null;
      var1.put("stack", var1, var2);
   }

   public void setStackProvider(RhinoException var1) {
      if (this.stackProvider == null) {
         this.stackProvider = var1;
         this.defineProperty("stack", this, ERROR_DELEGATE_GET_STACK, ERROR_DELEGATE_SET_STACK, 2);
      }

   }

   public String toString() {
      Object var1 = js_toString(this);
      return var1 instanceof String ? (String)var1 : super.toString();
   }

   private static final class ProtoProps implements Serializable {
      static final Method GET_PREPARE_STACK;
      static final Method GET_STACK_LIMIT;
      static final String KEY = "_ErrorPrototypeProps";
      static final Method SET_PREPARE_STACK;
      static final Method SET_STACK_LIMIT;
      private static final long serialVersionUID = 1907180507775337939L;
      private Function prepareStackTrace;
      private int stackTraceLimit;

      static {
         try {
            GET_STACK_LIMIT = NativeError.ProtoProps.class.getMethod("getStackTraceLimit", new Class[]{Scriptable.class});
            SET_STACK_LIMIT = NativeError.ProtoProps.class.getMethod("setStackTraceLimit", new Class[]{Scriptable.class, Object.class});
            GET_PREPARE_STACK = NativeError.ProtoProps.class.getMethod("getPrepareStackTrace", new Class[]{Scriptable.class});
            SET_PREPARE_STACK = NativeError.ProtoProps.class.getMethod("setPrepareStackTrace", new Class[]{Scriptable.class, Object.class});
         } catch (NoSuchMethodException var1) {
            throw new RuntimeException(var1);
         }
      }

      private ProtoProps() {
         this.stackTraceLimit = -1;
      }

      // $FF: synthetic method
      ProtoProps(Object var1) {
         this();
      }

      public Object getPrepareStackTrace(Scriptable var1) {
         Function var2 = this.getPrepareStackTrace();
         return var2 == null ? Undefined.instance : var2;
      }

      public Function getPrepareStackTrace() {
         return this.prepareStackTrace;
      }

      public int getStackTraceLimit() {
         return this.stackTraceLimit;
      }

      public Object getStackTraceLimit(Scriptable var1) {
         int var2 = this.stackTraceLimit;
         return var2 >= 0 ? var2 : Double.POSITIVE_INFINITY;
      }

      public void setPrepareStackTrace(Scriptable var1, Object var2) {
         if (var2 != null && !Undefined.instance.equals(var2)) {
            if (var2 instanceof Function) {
               this.prepareStackTrace = (Function)var2;
               return;
            }
         } else {
            this.prepareStackTrace = null;
         }

      }

      public void setStackTraceLimit(Scriptable var1, Object var2) {
         double var3 = Context.toNumber(var2);
         if (!Double.isNaN(var3) && !Double.isInfinite(var3)) {
            this.stackTraceLimit = (int)var3;
         } else {
            this.stackTraceLimit = -1;
         }
      }
   }
}
