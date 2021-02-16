package org.mozilla.javascript.ast;

public class RegExpLiteral extends AstNode {
   private String flags;
   private String value;

   public RegExpLiteral() {
      this.type = 48;
   }

   public RegExpLiteral(int var1) {
      super(var1);
      this.type = 48;
   }

   public RegExpLiteral(int var1, int var2) {
      super(var1, var2);
      this.type = 48;
   }

   public String getFlags() {
      return this.flags;
   }

   public String getValue() {
      return this.value;
   }

   public void setFlags(String var1) {
      this.flags = var1;
   }

   public void setValue(String var1) {
      this.assertNotNull(var1);
      this.value = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("/");
      var2.append(this.value);
      var2.append("/");
      String var7 = this.flags;
      if (var7 == null) {
         var7 = "";
      }

      var2.append(var7);
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
