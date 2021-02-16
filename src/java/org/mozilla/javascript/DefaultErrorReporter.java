package org.mozilla.javascript;

class DefaultErrorReporter implements ErrorReporter {
   static final DefaultErrorReporter instance;
   private ErrorReporter chainedReporter;
   private boolean forEval;

   static {
      // $FF: Couldn't be decompiled
   }

   private DefaultErrorReporter() {
      // $FF: Couldn't be decompiled
   }

   static ErrorReporter forEval(ErrorReporter param0) {
      // $FF: Couldn't be decompiled
   }

   public void error(String param1, String param2, int param3, String param4, int param5) {
      // $FF: Couldn't be decompiled
   }

   public EvaluatorException runtimeError(String param1, String param2, int param3, String param4, int param5) {
      // $FF: Couldn't be decompiled
   }

   public void warning(String param1, String param2, int param3, String param4, int param5) {
      // $FF: Couldn't be decompiled
   }
}
