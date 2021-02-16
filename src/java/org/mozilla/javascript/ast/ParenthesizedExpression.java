package org.mozilla.javascript.ast;

public class ParenthesizedExpression extends AstNode {
   private AstNode expression;

   public ParenthesizedExpression() {
      this.type = 88;
   }

   public ParenthesizedExpression(int var1) {
      super(var1);
      this.type = 88;
   }

   public ParenthesizedExpression(int var1, int var2) {
      super(var1, var2);
      this.type = 88;
   }

   public ParenthesizedExpression(int var1, int var2, AstNode var3) {
      super(var1, var2);
      this.type = 88;
      this.setExpression(var3);
   }

   public ParenthesizedExpression(AstNode var1) {
      int var2;
      if (var1 != null) {
         var2 = var1.getPosition();
      } else {
         var2 = 0;
      }

      int var3;
      if (var1 != null) {
         var3 = var1.getLength();
      } else {
         var3 = 1;
      }

      this(var2, var3, var1);
   }

   public AstNode getExpression() {
      return this.expression;
   }

   public void setExpression(AstNode var1) {
      this.assertNotNull(var1);
      this.expression = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("(");
      var2.append(this.expression.toSource(0));
      var2.append(")");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.expression.visit(var1);
      }

   }
}
