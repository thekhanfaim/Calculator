package org.mozilla.javascript.ast;

import org.mozilla.javascript.ScriptRuntime;

public class StringLiteral extends AstNode {
   private char quoteChar;
   private String value;

   public StringLiteral() {
      this.type = 41;
   }

   public StringLiteral(int var1) {
      super(var1);
      this.type = 41;
   }

   public StringLiteral(int var1, int var2) {
      super(var1, var2);
      this.type = 41;
   }

   public char getQuoteCharacter() {
      return this.quoteChar;
   }

   public String getValue() {
      return this.value;
   }

   public String getValue(boolean var1) {
      if (!var1) {
         return this.value;
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append(this.quoteChar);
         var2.append(this.value);
         var2.append(this.quoteChar);
         return var2.toString();
      }
   }

   public void setQuoteCharacter(char var1) {
      this.quoteChar = var1;
   }

   public void setValue(String var1) {
      this.assertNotNull(var1);
      this.value = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder(this.makeIndent(var1));
      var2.append(this.quoteChar);
      var2.append(ScriptRuntime.escapeString(this.value, this.quoteChar));
      var2.append(this.quoteChar);
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
