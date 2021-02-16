package org.mozilla.javascript.ast;

public class NumberLiteral extends AstNode {
   private double number;
   private String value;

   public NumberLiteral() {
      this.type = 40;
   }

   public NumberLiteral(double var1) {
      this.type = 40;
      this.setDouble(var1);
      this.setValue(Double.toString(var1));
   }

   public NumberLiteral(int var1) {
      super(var1);
      this.type = 40;
   }

   public NumberLiteral(int var1, int var2) {
      super(var1, var2);
      this.type = 40;
   }

   public NumberLiteral(int var1, String var2) {
      super(var1);
      this.type = 40;
      this.setValue(var2);
      this.setLength(var2.length());
   }

   public NumberLiteral(int var1, String var2, double var3) {
      this(var1, var2);
      this.setDouble(var3);
   }

   public double getNumber() {
      return this.number;
   }

   public String getValue() {
      return this.value;
   }

   public void setNumber(double var1) {
      this.number = var1;
   }

   public void setValue(String var1) {
      this.assertNotNull(var1);
      this.value = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      String var4 = this.value;
      if (var4 == null) {
         var4 = "<null>";
      }

      var2.append(var4);
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
