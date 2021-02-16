package org.mozilla.javascript.ast;

public class EmptyExpression extends AstNode {
   public EmptyExpression() {
      this.type = 129;
   }

   public EmptyExpression(int var1) {
      super(var1);
      this.type = 129;
   }

   public EmptyExpression(int var1, int var2) {
      super(var1, var2);
      this.type = 129;
   }

   public String toSource(int var1) {
      return this.makeIndent(var1);
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
