package org.mozilla.javascript;

public enum StackStyle {
   MOZILLA,
   RHINO,
   V8;

   static {
      StackStyle var0 = new StackStyle("RHINO", 0);
      RHINO = var0;
      StackStyle var1 = new StackStyle("MOZILLA", 1);
      MOZILLA = var1;
      StackStyle var2 = new StackStyle("V8", 2);
      V8 = var2;
   }
}
