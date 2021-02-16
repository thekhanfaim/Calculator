package org.mozilla.javascript;

public class ScriptRuntimeES6 {
   public static Scriptable requireObjectCoercible(Context var0, Scriptable var1, IdFunctionObject var2) {
      if (var1 != null && !Undefined.isUndefined(var1)) {
         return var1;
      } else {
         throw ScriptRuntime.typeError2("msg.called.null.or.undefined", var2.getTag(), var2.getFunctionName());
      }
   }
}
