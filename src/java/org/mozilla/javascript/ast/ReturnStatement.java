package org.mozilla.javascript.ast;

public class ReturnStatement extends AstNode {
   private AstNode returnValue;

   public ReturnStatement() {
      this.type = 4;
   }

   public ReturnStatement(int var1) {
      super(var1);
      this.type = 4;
   }

   public ReturnStatement(int var1, int var2) {
      super(var1, var2);
      this.type = 4;
   }

   public ReturnStatement(int var1, int var2, AstNode var3) {
      super(var1, var2);
      this.type = 4;
      this.setReturnValue(var3);
   }

   public AstNode getReturnValue() {
      return this.returnValue;
   }

   public void setReturnValue(AstNode var1) {
      this.returnValue = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("return");
      if (this.returnValue != null) {
         var2.append(" ");
         var2.append(this.returnValue.toSource(0));
      }

      var2.append(";\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         AstNode var2 = this.returnValue;
         if (var2 != null) {
            var2.visit(var1);
         }
      }

   }
}
