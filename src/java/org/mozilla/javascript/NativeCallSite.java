package org.mozilla.javascript;

public class NativeCallSite extends IdScriptableObject {
   private static final String CALLSITE_TAG = "CallSite";
   private static final int Id_constructor = 1;
   private static final int Id_getColumnNumber = 9;
   private static final int Id_getEvalOrigin = 10;
   private static final int Id_getFileName = 7;
   private static final int Id_getFunction = 4;
   private static final int Id_getFunctionName = 5;
   private static final int Id_getLineNumber = 8;
   private static final int Id_getMethodName = 6;
   private static final int Id_getThis = 2;
   private static final int Id_getTypeName = 3;
   private static final int Id_isConstructor = 14;
   private static final int Id_isEval = 12;
   private static final int Id_isNative = 13;
   private static final int Id_isToplevel = 11;
   private static final int Id_toString = 15;
   private static final int MAX_PROTOTYPE_ID = 15;
   private ScriptStackElement element;

   private NativeCallSite() {
   }

   private static Object getFileName(Scriptable var0) {
      while(var0 != null && !(var0 instanceof NativeCallSite)) {
         var0 = var0.getPrototype();
      }

      if (var0 == null) {
         return NOT_FOUND;
      } else {
         ScriptStackElement var1 = ((NativeCallSite)var0).element;
         if (var1 == null) {
            return null;
         } else {
            return var1.fileName;
         }
      }
   }

   private static Object getFunctionName(Scriptable var0) {
      while(var0 != null && !(var0 instanceof NativeCallSite)) {
         var0 = var0.getPrototype();
      }

      if (var0 == null) {
         return NOT_FOUND;
      } else {
         ScriptStackElement var1 = ((NativeCallSite)var0).element;
         if (var1 == null) {
            return null;
         } else {
            return var1.functionName;
         }
      }
   }

   private static Object getLineNumber(Scriptable var0) {
      while(var0 != null && !(var0 instanceof NativeCallSite)) {
         var0 = var0.getPrototype();
      }

      if (var0 == null) {
         return NOT_FOUND;
      } else {
         NativeCallSite var1 = (NativeCallSite)var0;
         ScriptStackElement var2 = var1.element;
         if (var2 != null && var2.lineNumber >= 0) {
            return var1.element.lineNumber;
         } else {
            return Undefined.instance;
         }
      }
   }

   static void init(Scriptable var0, boolean var1) {
      (new NativeCallSite()).exportAsJSClass(15, var0, var1);
   }

   private static Object js_toString(Scriptable var0) {
      while(var0 != null && !(var0 instanceof NativeCallSite)) {
         var0 = var0.getPrototype();
      }

      if (var0 == null) {
         return NOT_FOUND;
      } else {
         NativeCallSite var1 = (NativeCallSite)var0;
         StringBuilder var2 = new StringBuilder();
         var1.element.renderJavaStyle(var2);
         return var2.toString();
      }
   }

   static NativeCallSite make(Scriptable var0, Scriptable var1) {
      NativeCallSite var2 = new NativeCallSite();
      Scriptable var3 = (Scriptable)((Scriptable)var1.get("prototype", var1));
      var2.setParentScope(var0);
      var2.setPrototype(var3);
      return var2;
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag("CallSite")) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         switch(var6) {
         case 1:
            return make(var3, var1);
         case 2:
         case 3:
         case 4:
         case 9:
            return Undefined.instance;
         case 5:
            return getFunctionName(var4);
         case 6:
            return null;
         case 7:
            return getFileName(var4);
         case 8:
            return getLineNumber(var4);
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
            return Boolean.FALSE;
         case 15:
            return js_toString(var4);
         default:
            throw new IllegalArgumentException(String.valueOf(var6));
         }
      }
   }

   protected int findPrototypeId(String var1) {
      String var2;
      byte var3;
      switch(var1.length()) {
      case 6:
         var2 = "isEval";
         var3 = 12;
         break;
      case 7:
         var2 = "getThis";
         var3 = 2;
         break;
      case 8:
         char var4 = var1.charAt(0);
         if (var4 == 'i') {
            var2 = "isNative";
            var3 = 13;
         } else {
            var3 = 0;
            var2 = null;
            if (var4 == 't') {
               var2 = "toString";
               var3 = 15;
            }
         }
         break;
      case 9:
      case 12:
      case 14:
      default:
         var3 = 0;
         var2 = null;
         break;
      case 10:
         var2 = "isToplevel";
         var3 = 11;
         break;
      case 11:
         char var5 = var1.charAt(4);
         if (var5 != 'i') {
            if (var5 != 'y') {
               if (var5 != 't') {
                  if (var5 != 'u') {
                     var3 = 0;
                     var2 = null;
                  } else {
                     var2 = "getFunction";
                     var3 = 4;
                  }
               } else {
                  var2 = "constructor";
                  var3 = 1;
               }
            } else {
               var2 = "getTypeName";
               var3 = 3;
            }
         } else {
            var2 = "getFileName";
            var3 = 7;
         }
         break;
      case 13:
         char var6 = var1.charAt(3);
         if (var6 != 'E') {
            if (var6 != 'o') {
               if (var6 != 'L') {
                  if (var6 != 'M') {
                     var3 = 0;
                     var2 = null;
                  } else {
                     var2 = "getMethodName";
                     var3 = 6;
                  }
               } else {
                  var2 = "getLineNumber";
                  var3 = 8;
               }
            } else {
               var2 = "isConstructor";
               var3 = 14;
            }
         } else {
            var2 = "getEvalOrigin";
            var3 = 10;
         }
         break;
      case 15:
         char var7 = var1.charAt(3);
         if (var7 == 'C') {
            var2 = "getColumnNumber";
            var3 = 9;
         } else {
            var3 = 0;
            var2 = null;
            if (var7 == 'F') {
               var2 = "getFunctionName";
               var3 = 5;
            }
         }
      }

      if (var2 != null && var2 != var1 && !var2.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   public String getClassName() {
      return "CallSite";
   }

   protected void initPrototypeId(int var1) {
      String var2;
      switch(var1) {
      case 1:
         var2 = "constructor";
         break;
      case 2:
         var2 = "getThis";
         break;
      case 3:
         var2 = "getTypeName";
         break;
      case 4:
         var2 = "getFunction";
         break;
      case 5:
         var2 = "getFunctionName";
         break;
      case 6:
         var2 = "getMethodName";
         break;
      case 7:
         var2 = "getFileName";
         break;
      case 8:
         var2 = "getLineNumber";
         break;
      case 9:
         var2 = "getColumnNumber";
         break;
      case 10:
         var2 = "getEvalOrigin";
         break;
      case 11:
         var2 = "isToplevel";
         break;
      case 12:
         var2 = "isEval";
         break;
      case 13:
         var2 = "isNative";
         break;
      case 14:
         var2 = "isConstructor";
         break;
      case 15:
         var2 = "toString";
         break;
      default:
         throw new IllegalArgumentException(String.valueOf(var1));
      }

      this.initPrototypeMethod("CallSite", var1, var2, 0);
   }

   void setElement(ScriptStackElement var1) {
      this.element = var1;
   }

   public String toString() {
      ScriptStackElement var1 = this.element;
      return var1 == null ? "" : var1.toString();
   }
}
