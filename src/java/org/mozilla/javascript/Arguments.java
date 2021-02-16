package org.mozilla.javascript;

final class Arguments extends IdScriptableObject {
   private static final String FTAG = "Arguments";
   private static final int Id_callee = 1;
   private static final int Id_caller = 3;
   private static final int Id_length = 2;
   private static final int MAX_INSTANCE_ID = 3;
   private static BaseFunction iteratorMethod = new Arguments$1();
   private static final long serialVersionUID = 4275508002492040609L;
   private NativeCall activation;
   private Object[] args;
   private int calleeAttr = 2;
   private Object calleeObj;
   private int callerAttr = 2;
   private Object callerObj;
   private int lengthAttr = 2;
   private Object lengthObj;

   public Arguments(NativeCall var1) {
      this.activation = var1;
      Scriptable var2 = var1.getParentScope();
      this.setParentScope(var2);
      this.setPrototype(ScriptableObject.getObjectPrototype(var2));
      Object[] var3 = var1.originalArgs;
      this.args = var3;
      this.lengthObj = var3.length;
      NativeFunction var4 = var1.function;
      this.calleeObj = var4;
      int var5 = var4.getLanguageVersion();
      if (var5 <= 130 && var5 != 0) {
         this.callerObj = null;
      } else {
         this.callerObj = NOT_FOUND;
      }

      this.defineProperty(SymbolKey.ITERATOR, iteratorMethod, 2);
   }

   private Object arg(int var1) {
      if (var1 >= 0) {
         Object[] var2 = this.args;
         if (var2.length > var1) {
            return var2[var1];
         }
      }

      return NOT_FOUND;
   }

   private Object getFromActivation(int var1) {
      String var2 = this.activation.function.getParamOrVarName(var1);
      NativeCall var3 = this.activation;
      return var3.get(var2, var3);
   }

   private void putIntoActivation(int var1, Object var2) {
      String var3 = this.activation.function.getParamOrVarName(var1);
      NativeCall var4 = this.activation;
      var4.put(var3, var4, var2);
   }

   private void removeArg(int var1) {
      synchronized(this){}

      Throwable var10000;
      boolean var10001;
      label197: {
         label196: {
            try {
               if (this.args[var1] == NOT_FOUND) {
                  break label196;
               }

               if (this.args == this.activation.originalArgs) {
                  this.args = (Object[])this.args.clone();
               }
            } catch (Throwable var23) {
               var10000 = var23;
               var10001 = false;
               break label197;
            }

            try {
               this.args[var1] = NOT_FOUND;
            } catch (Throwable var22) {
               var10000 = var22;
               var10001 = false;
               break label197;
            }
         }

         label189:
         try {
            return;
         } catch (Throwable var21) {
            var10000 = var21;
            var10001 = false;
            break label189;
         }
      }

      while(true) {
         Throwable var2 = var10000;

         try {
            throw var2;
         } catch (Throwable var20) {
            var10000 = var20;
            var10001 = false;
            continue;
         }
      }
   }

   private void replaceArg(int var1, Object var2) {
      if (this.sharedWithActivation(var1)) {
         this.putIntoActivation(var1, var2);
      }

      synchronized(this){}

      Throwable var10000;
      boolean var10001;
      label136: {
         try {
            if (this.args == this.activation.originalArgs) {
               this.args = (Object[])this.args.clone();
            }
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label136;
         }

         label133:
         try {
            this.args[var1] = var2;
            return;
         } catch (Throwable var15) {
            var10000 = var15;
            var10001 = false;
            break label133;
         }
      }

      while(true) {
         Throwable var3 = var10000;

         try {
            throw var3;
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            continue;
         }
      }
   }

   private boolean sharedWithActivation(int var1) {
      if (Context.getContext().isStrictMode()) {
         return false;
      } else {
         NativeFunction var2 = this.activation.function;
         int var3 = var2.getParamCount();
         if (var1 >= var3) {
            return false;
         } else {
            if (var1 < var3 - 1) {
               String var4 = var2.getParamOrVarName(var1);

               for(int var5 = var1 + 1; var5 < var3; ++var5) {
                  if (var4.equals(var2.getParamOrVarName(var5))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   void defineAttributesForStrictMode() {
      if (Context.getContext().isStrictMode()) {
         this.setGetterOrSetter("caller", 0, new BaseFunction("caller") {
            private String propertyName;

            {
               this.propertyName = var1;
            }

            public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
               throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
            }
         }, true);
         this.setGetterOrSetter("caller", 0, new BaseFunction("caller") {
            private String propertyName;

            {
               this.propertyName = var1;
            }

            public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
               throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
            }
         }, false);
         this.setGetterOrSetter("callee", 0, new BaseFunction("callee") {
            private String propertyName;

            {
               this.propertyName = var1;
            }

            public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
               throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
            }
         }, true);
         this.setGetterOrSetter("callee", 0, new BaseFunction("callee") {
            private String propertyName;

            {
               this.propertyName = var1;
            }

            public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4) {
               throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
            }
         }, false);
         this.setAttributes("caller", 6);
         this.setAttributes("callee", 6);
         this.callerObj = null;
         this.calleeObj = null;
      }
   }

   protected void defineOwnProperty(Context var1, Object var2, ScriptableObject var3, boolean var4) {
      super.defineOwnProperty(var1, var2, var3, var4);
      double var5 = ScriptRuntime.toNumber(var2);
      int var7 = (int)var5;
      if (var5 == (double)var7) {
         if (this.arg(var7) != NOT_FOUND) {
            if (this.isAccessorDescriptor(var3)) {
               this.removeArg(var7);
            } else {
               Object var8 = getProperty(var3, "value");
               if (var8 != NOT_FOUND) {
                  this.replaceArg(var7, var8);
                  if (isFalse(getProperty(var3, "writable"))) {
                     this.removeArg(var7);
                  }

               }
            }
         }
      }
   }

   public void delete(int var1) {
      if (var1 >= 0 && var1 < this.args.length) {
         this.removeArg(var1);
      }

      super.delete(var1);
   }

   protected int findInstanceIdInfo(String var1) {
      int var2 = var1.length();
      byte var3 = 0;
      String var4 = null;
      if (var2 == 6) {
         char var6 = var1.charAt(5);
         if (var6 == 'e') {
            var4 = "callee";
            var3 = 1;
         } else if (var6 == 'h') {
            var4 = "length";
            var3 = 2;
         } else {
            var3 = 0;
            var4 = null;
            if (var6 == 'r') {
               var4 = "caller";
               var3 = 3;
            }
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      if (Context.getContext().isStrictMode() && (var3 == 1 || var3 == 3)) {
         return super.findInstanceIdInfo(var1);
      } else if (var3 == 0) {
         return super.findInstanceIdInfo(var1);
      } else {
         int var5;
         if (var3 != 1) {
            if (var3 != 2) {
               if (var3 != 3) {
                  throw new IllegalStateException();
               }

               var5 = this.callerAttr;
            } else {
               var5 = this.lengthAttr;
            }
         } else {
            var5 = this.calleeAttr;
         }

         return instanceIdInfo(var5, var3);
      }
   }

   public Object get(int var1, Scriptable var2) {
      Object var3 = this.arg(var1);
      if (var3 == NOT_FOUND) {
         return super.get(var1, var2);
      } else {
         return this.sharedWithActivation(var1) ? this.getFromActivation(var1) : var3;
      }
   }

   public String getClassName() {
      return "Arguments";
   }

   Object[] getIds(boolean var1, boolean var2) {
      Object[] var3 = super.getIds(var1, var2);
      Object[] var4 = this.args;
      if (var4.length != 0) {
         boolean[] var5 = new boolean[var4.length];
         int var6 = var4.length;

         for(int var7 = 0; var7 != var3.length; ++var7) {
            Object var13 = var3[var7];
            if (var13 instanceof Integer) {
               int var14 = (Integer)var13;
               if (var14 >= 0 && var14 < this.args.length && !var5[var14]) {
                  var5[var14] = true;
                  --var6;
               }
            }
         }

         if (!var1) {
            for(int var12 = 0; var12 < var5.length; ++var12) {
               if (!var5[var12] && super.has(var12, this)) {
                  var5[var12] = true;
                  --var6;
               }
            }
         }

         if (var6 != 0) {
            Object[] var8 = new Object[var6 + var3.length];
            System.arraycopy(var3, 0, var8, var6, var3.length);
            var3 = var8;
            int var9 = 0;

            for(int var10 = 0; var10 != this.args.length; ++var10) {
               if (!var5[var10]) {
                  var3[var9] = var10;
                  ++var9;
               }
            }

            if (var9 != var6) {
               Kit.codeBug();
            }
         }
      }

      return var3;
   }

   protected String getInstanceIdName(int var1) {
      if (var1 != 1) {
         if (var1 != 2) {
            return var1 != 3 ? null : "caller";
         } else {
            return "length";
         }
      } else {
         return "callee";
      }
   }

   protected Object getInstanceIdValue(int var1) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               return super.getInstanceIdValue(var1);
            } else {
               Object var2 = this.callerObj;
               if (var2 == UniqueTag.NULL_VALUE) {
                  return null;
               } else {
                  if (var2 == null) {
                     NativeCall var3 = this.activation.parentActivationCall;
                     if (var3 != null) {
                        var2 = var3.get("arguments", var3);
                     }
                  }

                  return var2;
               }
            }
         } else {
            return this.lengthObj;
         }
      } else {
         return this.calleeObj;
      }
   }

   protected int getMaxInstanceId() {
      return 3;
   }

   protected ScriptableObject getOwnPropertyDescriptor(Context var1, Object var2) {
      if (var2 instanceof Scriptable) {
         return super.getOwnPropertyDescriptor(var1, var2);
      } else {
         double var3 = ScriptRuntime.toNumber(var2);
         int var5 = (int)var3;
         if (var3 != (double)var5) {
            return super.getOwnPropertyDescriptor(var1, var2);
         } else {
            Object var6 = this.arg(var5);
            if (var6 == NOT_FOUND) {
               return super.getOwnPropertyDescriptor(var1, var2);
            } else {
               if (this.sharedWithActivation(var5)) {
                  var6 = this.getFromActivation(var5);
               }

               if (super.has(var5, this)) {
                  ScriptableObject var8 = super.getOwnPropertyDescriptor(var1, var2);
                  var8.put((String)"value", var8, var6);
                  return var8;
               } else {
                  Object var7 = this.getParentScope();
                  if (var7 == null) {
                     var7 = this;
                  }

                  return buildDataDescriptor((Scriptable)var7, var6, 0);
               }
            }
         }
      }
   }

   public boolean has(int var1, Scriptable var2) {
      return this.arg(var1) != NOT_FOUND ? true : super.has(var1, var2);
   }

   public void put(int var1, Scriptable var2, Object var3) {
      if (this.arg(var1) == NOT_FOUND) {
         super.put(var1, var2, var3);
      } else {
         this.replaceArg(var1, var3);
      }
   }

   public void put(String var1, Scriptable var2, Object var3) {
      super.put(var1, var2, var3);
   }

   protected void setInstanceIdAttributes(int var1, int var2) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               super.setInstanceIdAttributes(var1, var2);
            } else {
               this.callerAttr = var2;
            }
         } else {
            this.lengthAttr = var2;
         }
      } else {
         this.calleeAttr = var2;
      }
   }

   protected void setInstanceIdValue(int var1, Object var2) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               super.setInstanceIdValue(var1, var2);
            } else {
               Object var3;
               if (var2 != null) {
                  var3 = var2;
               } else {
                  var3 = UniqueTag.NULL_VALUE;
               }

               this.callerObj = var3;
            }
         } else {
            this.lengthObj = var2;
         }
      } else {
         this.calleeObj = var2;
      }
   }
}
