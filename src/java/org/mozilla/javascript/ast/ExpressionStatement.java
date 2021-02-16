package org.mozilla.javascript.ast;

public class ExpressionStatement extends AstNode {
   private AstNode expr;

   public ExpressionStatement() {
      this.type = 134;
   }

   public ExpressionStatement(int var1, int var2) {
      super(var1, var2);
      this.type = 134;
   }

   public ExpressionStatement(int var1, int var2, AstNode var3) {
      super(var1, var2);
      this.type = 134;
      this.setExpression(var3);
   }

   public ExpressionStatement(AstNode var1) {
      this(var1.getPosition(), var1.getLength(), var1);
   }

   public ExpressionStatement(AstNode var1, boolean var2) {
      this(var1);
      if (var2) {
         this.setHasResult();
      }

   }

   public AstNode getExpression() {
      return this.expr;
   }

   public boolean hasSideEffects() {
      return this.type == 135 || this.expr.hasSideEffects();
   }

   public void setExpression(AstNode var1) {
      this.assertNotNull(var1);
      this.expr = var1;
      var1.setParent(this);
      this.setLineno(var1.getLineno());
   }

   public void setHasResult() {
      this.type = 135;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.expr.toSource(var1));
      var2.append(";");
      if (this.getInlineComment() != null) {
         var2.append(this.getInlineComment().toSource(var1));
      }

      var2.append("\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.expr.visit(var1);
      }

   }
}
