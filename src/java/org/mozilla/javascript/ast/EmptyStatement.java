package org.mozilla.javascript.ast;

public class EmptyStatement extends AstNode {
   public EmptyStatement() {
      this.type = 129;
   }

   public EmptyStatement(int var1) {
      super(var1);
      this.type = 129;
   }

   public EmptyStatement(int var1, int var2) {
      super(var1, var2);
      this.type = 129;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(";\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
