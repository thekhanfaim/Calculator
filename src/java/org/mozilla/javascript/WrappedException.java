package org.mozilla.javascript;

public class WrappedException extends EvaluatorException {
   private static final long serialVersionUID = -1551979216966520648L;
   private Throwable exception;

   public WrappedException(Throwable var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("Wrapped ");
      var2.append(var1.toString());
      super(var2.toString());
      this.exception = var1;
      this.initCause(var1);
      int[] var6 = new int[]{0};
      String var7 = Context.getSourcePositionFromStack(var6);
      int var8 = var6[0];
      if (var7 != null) {
         this.initSourceName(var7);
      }

      if (var8 != 0) {
         this.initLineNumber(var8);
      }

   }

   public Throwable getWrappedException() {
      return this.exception;
   }

   @Deprecated
   public Object unwrap() {
      return this.getWrappedException();
   }
}
