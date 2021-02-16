package org.mozilla.javascript;

public class BaseFunction extends IdScriptableObject implements Function {
   private static final Object FUNCTION_TAG = "Function";
   private static final int Id_apply = 4;
   private static final int Id_arguments = 5;
   private static final int Id_arity = 2;
   private static final int Id_bind = 6;
   private static final int Id_call = 5;
   private static final int Id_constructor = 1;
   private static final int Id_length = 1;
   private static final int Id_name = 3;
   private static final int Id_prototype = 4;
   private static final int Id_toSource = 3;
   private static final int Id_toString = 2;
   private static final int MAX_INSTANCE_ID = 5;
   private static final int MAX_PROTOTYPE_ID = 6;
   private static final long serialVersionUID = 5311394446546053859L;
   private int argumentsAttributes;
   private Object argumentsObj;
   private Object prototypeProperty;
   private int prototypePropertyAttributes;

   public BaseFunction() {
      this.argumentsObj = NOT_FOUND;
      this.prototypePropertyAttributes = 6;
      this.argumentsAttributes = 6;
   }

   public BaseFunction(Scriptable var1, Scriptable var2) {
      super(var1, var2);
      this.argumentsObj = NOT_FOUND;
      this.prototypePropertyAttributes = 6;
      this.argumentsAttributes = 6;
   }

   private Object getArguments() {
      Object var1;
      if (this.defaultHas("arguments")) {
         var1 = this.defaultGet("arguments");
      } else {
         var1 = this.argumentsObj;
      }

      if (var1 != NOT_FOUND) {
         return var1;
      } else {
         NativeCall var2 = ScriptRuntime.findFunctionActivation(Context.getContext(), this);
         return var2 == null ? null : var2.get("arguments", var2);
      }
   }

   static void init(Scriptable var0, boolean var1) {
      BaseFunction var2 = new BaseFunction();
      var2.prototypePropertyAttributes = 7;
      var2.exportAsJSClass(6, var0, var1);
   }

   static boolean isApply(IdFunctionObject var0) {
      return var0.hasTag(FUNCTION_TAG) && var0.methodId() == 4;
   }

   static boolean isApplyOrCall(IdFunctionObject var0) {
      if (var0.hasTag(FUNCTION_TAG)) {
         int var1 = var0.methodId();
         if (var1 == 4 || var1 == 5) {
            return true;
         }
      }

      return false;
   }

   private static Object jsConstructor(Context var0, Scriptable var1, Object[] var2) {
      int var3 = var2.length;
      StringBuilder var4 = new StringBuilder();
      var4.append("function ");
      if (var0.getLanguageVersion() != 120) {
         var4.append("anonymous");
      }

      var4.append('(');

      for(int var7 = 0; var7 < var3 - 1; ++var7) {
         if (var7 > 0) {
            var4.append(',');
         }

         var4.append(ScriptRuntime.toString(var2[var7]));
      }

      var4.append(") {");
      if (var3 != 0) {
         var4.append(ScriptRuntime.toString(var2[var3 - 1]));
      }

      var4.append("\n}");
      String var10 = var4.toString();
      int[] var11 = new int[1];
      String var12 = Context.getSourcePositionFromStack(var11);
      String var13;
      if (var12 == null) {
         var11[0] = 1;
         var13 = "<eval'ed string>";
      } else {
         var13 = var12;
      }

      String var14 = ScriptRuntime.makeUrlForGeneratedScript(false, var13, var11[0]);
      Scriptable var15 = ScriptableObject.getTopLevelScope(var1);
      ErrorReporter var16 = DefaultErrorReporter.forEval(var0.getErrorReporter());
      Evaluator var17 = Context.createInterpreter();
      if (var17 != null) {
         return var0.compileFunction(var15, var10, var17, var16, var14, 1, (Object)null);
      } else {
         JavaScriptException var18 = new JavaScriptException("Interpreter not present", var13, var11[0]);
         throw var18;
      }
   }

   private BaseFunction realFunction(Scriptable var1, IdFunctionObject var2) {
      Object var3 = var1.getDefaultValue(ScriptRuntime.FunctionClass);
      if (var3 instanceof Delegator) {
         var3 = ((Delegator)var3).getDelegee();
      }

      if (var3 instanceof BaseFunction) {
         return (BaseFunction)var3;
      } else {
         throw ScriptRuntime.typeError1("msg.incompat.call", var2.getFunctionName());
      }
   }

   private Object setupDefaultPrototype() {
      synchronized(this){}

      Throwable var10000;
      label132: {
         boolean var10001;
         Object var2;
         try {
            var2 = this.prototypeProperty;
         } catch (Throwable var17) {
            var10000 = var17;
            var10001 = false;
            break label132;
         }

         if (var2 != null) {
            return var2;
         }

         NativeObject var3;
         Scriptable var4;
         try {
            var3 = new NativeObject();
            var3.defineProperty("constructor", this, 2);
            this.prototypeProperty = var3;
            var4 = getObjectPrototype(this);
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label132;
         }

         if (var4 != var3) {
            try {
               var3.setPrototype(var4);
            } catch (Throwable var15) {
               var10000 = var15;
               var10001 = false;
               break label132;
            }
         }

         return var3;
      }

      Throwable var1 = var10000;
      throw var1;
   }

   public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
      return Undefined.instance;
   }

   public Scriptable construct(Context var1, Scriptable var2, Object[] var3) {
      Scriptable var4 = this.createObject(var1, var2);
      if (var4 != null) {
         Object var14 = this.call(var1, var2, var4, var3);
         if (var14 instanceof Scriptable) {
            var4 = (Scriptable)var14;
         }

         return var4;
      } else {
         Object var5 = this.call(var1, var2, (Scriptable)null, var3);
         if (var5 instanceof Scriptable) {
            Scriptable var11 = (Scriptable)var5;
            if (var11.getPrototype() == null) {
               Scriptable var13 = this.getClassPrototype();
               if (var11 != var13) {
                  var11.setPrototype(var13);
               }
            }

            if (var11.getParentScope() == null) {
               Scriptable var12 = this.getParentScope();
               if (var11 != var12) {
                  var11.setParentScope(var12);
               }
            }

            return var11;
         } else {
            StringBuilder var6 = new StringBuilder();
            var6.append("Bad implementaion of call as constructor, name=");
            var6.append(this.getFunctionName());
            var6.append(" in ");
            var6.append(this.getClass().getName());
            throw new IllegalStateException(var6.toString());
         }
      }
   }

   public Scriptable createObject(Context var1, Scriptable var2) {
      NativeObject var3 = new NativeObject();
      var3.setPrototype(this.getClassPrototype());
      var3.setParentScope(this.getParentScope());
      return var3;
   }

   String decompile(int var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      boolean var4;
      if ((var2 & 1) != 0) {
         var4 = true;
      } else {
         var4 = false;
      }

      if (!var4) {
         var3.append("function ");
         var3.append(this.getFunctionName());
         var3.append("() {\n\t");
      }

      var3.append("[native code, arity=");
      var3.append(this.getArity());
      var3.append("]\n");
      if (!var4) {
         var3.append("}\n");
      }

      return var3.toString();
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(FUNCTION_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         byte var7 = 1;
         switch(var6) {
         case 1:
            return jsConstructor(var2, var3, var5);
         case 2:
            return this.realFunction(var4, var1).decompile(ScriptRuntime.toInt32(var5, 0), 0);
         case 3:
            BaseFunction var8 = this.realFunction(var4, var1);
            byte var9 = 2;
            int var10 = var5.length;
            int var11 = 0;
            if (var10 != 0) {
               var11 = ScriptRuntime.toInt32(var5[0]);
               if (var11 >= 0) {
                  var9 = 0;
               } else {
                  var11 = 0;
               }
            }

            return var8.decompile(var11, var9);
         case 4:
         case 5:
            if (var6 != 4) {
               var7 = 0;
            }

            return ScriptRuntime.applyOrCall((boolean)var7, var2, var3, var4, var5);
         case 6:
            if (var4 instanceof Callable) {
               Callable var12 = (Callable)var4;
               int var13 = var5.length;
               Scriptable var15;
               Object[] var16;
               if (var13 > 0) {
                  Scriptable var18 = ScriptRuntime.toObjectOrNull(var2, var5[0], var3);
                  Object[] var19 = new Object[var13 - 1];
                  System.arraycopy(var5, var7, var19, 0, var13 - 1);
                  var15 = var18;
                  var16 = var19;
               } else {
                  Object[] var14 = ScriptRuntime.emptyArgs;
                  var15 = null;
                  var16 = var14;
               }

               BoundFunction var17 = new BoundFunction(var2, var3, var12, var15, var16);
               return var17;
            }

            throw ScriptRuntime.notFunctionError(var4);
         default:
            throw new IllegalArgumentException(String.valueOf(var6));
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      var1.setPrototype(this);
      super.fillConstructorProperties(var1);
   }

   protected int findInstanceIdInfo(String var1) {
      int var2 = var1.length();
      String var3;
      byte var4;
      if (var2 != 4) {
         if (var2 != 5) {
            if (var2 != 6) {
               if (var2 != 9) {
                  var4 = 0;
                  var3 = null;
               } else {
                  char var6 = var1.charAt(0);
                  if (var6 == 'a') {
                     var3 = "arguments";
                     var4 = 5;
                  } else {
                     var4 = 0;
                     var3 = null;
                     if (var6 == 'p') {
                        var3 = "prototype";
                        var4 = 4;
                     }
                  }
               }
            } else {
               var3 = "length";
               var4 = 1;
            }
         } else {
            var3 = "arity";
            var4 = 2;
         }
      } else {
         var3 = "name";
         var4 = 3;
      }

      if (var3 != null && var3 != var1 && !var3.equals(var1)) {
         var4 = 0;
      }

      if (var4 == 0) {
         return super.findInstanceIdInfo(var1);
      } else {
         int var5;
         if (var4 != 1 && var4 != 2 && var4 != 3) {
            if (var4 != 4) {
               if (var4 != 5) {
                  throw new IllegalStateException();
               }

               var5 = this.argumentsAttributes;
            } else {
               if (!this.hasPrototypeProperty()) {
                  return 0;
               }

               var5 = this.prototypePropertyAttributes;
            }
         } else {
            var5 = 7;
         }

         return instanceIdInfo(var5, var4);
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var4;
      String var5;
      if (var2 != 4) {
         if (var2 != 5) {
            if (var2 != 8) {
               if (var2 != 11) {
                  var4 = 0;
                  var5 = null;
               } else {
                  var5 = "constructor";
                  var4 = 1;
               }
            } else {
               char var6 = var1.charAt(3);
               if (var6 == 'o') {
                  var5 = "toSource";
                  var4 = 3;
               } else {
                  var4 = 0;
                  var5 = null;
                  if (var6 == 't') {
                     var5 = "toString";
                     var4 = 2;
                  }
               }
            }
         } else {
            var5 = "apply";
            var4 = 4;
         }
      } else {
         char var3 = var1.charAt(0);
         if (var3 == 'b') {
            var5 = "bind";
            var4 = 6;
         } else {
            var4 = 0;
            var5 = null;
            if (var3 == 'c') {
               var5 = "call";
               var4 = 5;
            }
         }
      }

      if (var5 != null && var5 != var1 && !var5.equals(var1)) {
         var4 = 0;
      }

      return var4;
   }

   public int getArity() {
      return 0;
   }

   public String getClassName() {
      return "Function";
   }

   protected Scriptable getClassPrototype() {
      Object var1 = this.getPrototypeProperty();
      return var1 instanceof Scriptable ? (Scriptable)var1 : ScriptableObject.getObjectPrototype(this);
   }

   public String getFunctionName() {
      return "";
   }

   protected String getInstanceIdName(int var1) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               if (var1 != 4) {
                  return var1 != 5 ? super.getInstanceIdName(var1) : "arguments";
               } else {
                  return "prototype";
               }
            } else {
               return "name";
            }
         } else {
            return "arity";
         }
      } else {
         return "length";
      }
   }

   protected Object getInstanceIdValue(int var1) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               if (var1 != 4) {
                  return var1 != 5 ? super.getInstanceIdValue(var1) : this.getArguments();
               } else {
                  return this.getPrototypeProperty();
               }
            } else {
               return this.getFunctionName();
            }
         } else {
            return ScriptRuntime.wrapInt(this.getArity());
         }
      } else {
         return ScriptRuntime.wrapInt(this.getLength());
      }
   }

   public int getLength() {
      return 0;
   }

   protected int getMaxInstanceId() {
      return 5;
   }

   protected Object getPrototypeProperty() {
      Object var1 = this.prototypeProperty;
      if (var1 == null) {
         return this instanceof NativeFunction ? this.setupDefaultPrototype() : Undefined.instance;
      } else {
         if (var1 == UniqueTag.NULL_VALUE) {
            var1 = null;
         }

         return var1;
      }
   }

   public String getTypeOf() {
      return this.avoidObjectDetection() ? "undefined" : "function";
   }

   public boolean hasInstance(Scriptable var1) {
      Object var2 = ScriptableObject.getProperty(this, (String)"prototype");
      if (var2 instanceof Scriptable) {
         return ScriptRuntime.jsDelegatesTo(var1, (Scriptable)var2);
      } else {
         throw ScriptRuntime.typeError1("msg.instanceof.bad.prototype", this.getFunctionName());
      }
   }

   protected boolean hasPrototypeProperty() {
      return this.prototypeProperty != null || this instanceof NativeFunction;
   }

   protected void initPrototypeId(int var1) {
      byte var2;
      String var3;
      switch(var1) {
      case 1:
         var2 = 1;
         var3 = "constructor";
         break;
      case 2:
         var3 = "toString";
         var2 = 0;
         break;
      case 3:
         var2 = 1;
         var3 = "toSource";
         break;
      case 4:
         var2 = 2;
         var3 = "apply";
         break;
      case 5:
         var2 = 1;
         var3 = "call";
         break;
      case 6:
         var2 = 1;
         var3 = "bind";
         break;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }

      this.initPrototypeMethod(FUNCTION_TAG, var1, var3, var2);
   }

   public void setImmunePrototypeProperty(Object var1) {
      if ((1 & this.prototypePropertyAttributes) == 0) {
         Object var2;
         if (var1 != null) {
            var2 = var1;
         } else {
            var2 = UniqueTag.NULL_VALUE;
         }

         this.prototypeProperty = var2;
         this.prototypePropertyAttributes = 7;
      } else {
         throw new IllegalStateException();
      }
   }

   protected void setInstanceIdAttributes(int var1, int var2) {
      if (var1 != 4) {
         if (var1 != 5) {
            super.setInstanceIdAttributes(var1, var2);
         } else {
            this.argumentsAttributes = var2;
         }
      } else {
         this.prototypePropertyAttributes = var2;
      }
   }

   protected void setInstanceIdValue(int var1, Object var2) {
      if (var1 != 1 && var1 != 2 && var1 != 3) {
         if (var1 != 4) {
            if (var1 != 5) {
               super.setInstanceIdValue(var1, var2);
            } else {
               if (var2 == NOT_FOUND) {
                  Kit.codeBug();
               }

               if (this.defaultHas("arguments")) {
                  this.defaultPut("arguments", var2);
               } else {
                  if ((1 & this.argumentsAttributes) == 0) {
                     this.argumentsObj = var2;
                  }

               }
            }
         } else {
            if ((1 & this.prototypePropertyAttributes) == 0) {
               Object var3;
               if (var2 != null) {
                  var3 = var2;
               } else {
                  var3 = UniqueTag.NULL_VALUE;
               }

               this.prototypeProperty = var3;
            }

         }
      }
   }
}
