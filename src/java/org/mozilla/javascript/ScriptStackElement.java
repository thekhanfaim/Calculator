package org.mozilla.javascript;

import java.io.Serializable;

public final class ScriptStackElement implements Serializable {
   private static final long serialVersionUID = -6416688260860477449L;
   public final String fileName;
   public final String functionName;
   public final int lineNumber;

   public ScriptStackElement(String var1, String var2, int var3) {
      this.fileName = var1;
      this.functionName = var2;
      this.lineNumber = var3;
   }

   private void appendV8Location(StringBuilder var1) {
      var1.append(this.fileName);
      var1.append(':');
      int var4 = this.lineNumber;
      if (var4 <= -1) {
         var4 = 0;
      }

      var1.append(var4);
      var1.append(":0");
   }

   public void renderJavaStyle(StringBuilder var1) {
      var1.append("\tat ");
      var1.append(this.fileName);
      if (this.lineNumber > -1) {
         var1.append(':');
         var1.append(this.lineNumber);
      }

      if (this.functionName != null) {
         var1.append(" (");
         var1.append(this.functionName);
         var1.append(')');
      }

   }

   public void renderMozillaStyle(StringBuilder var1) {
      String var2 = this.functionName;
      if (var2 != null) {
         var1.append(var2);
         var1.append("()");
      }

      var1.append('@');
      var1.append(this.fileName);
      if (this.lineNumber > -1) {
         var1.append(':');
         var1.append(this.lineNumber);
      }

   }

   public void renderV8Style(StringBuilder var1) {
      var1.append("    at ");
      String var3 = this.functionName;
      if (var3 != null && !"anonymous".equals(var3) && !"undefined".equals(this.functionName)) {
         var1.append(this.functionName);
         var1.append(" (");
         this.appendV8Location(var1);
         var1.append(')');
      } else {
         this.appendV8Location(var1);
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      this.renderMozillaStyle(var1);
      return var1.toString();
   }
}
