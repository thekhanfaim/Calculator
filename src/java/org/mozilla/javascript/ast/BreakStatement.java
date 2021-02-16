package org.mozilla.javascript.ast;

public class BreakStatement extends Jump {
   private Name breakLabel;
   private AstNode target;

   public BreakStatement() {
      this.type = 121;
   }

   public BreakStatement(int var1) {
      this.type = 121;
      this.position = var1;
   }

   public BreakStatement(int var1, int var2) {
      this.type = 121;
      this.position = var1;
      this.length = var2;
   }

   public Name getBreakLabel() {
      return this.breakLabel;
   }

   public AstNode getBreakTarget() {
      return this.target;
   }

   public void setBreakLabel(Name var1) {
      this.breakLabel = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public void setBreakTarget(Jump var1) {
      this.assertNotNull(var1);
      this.target = var1;
      this.setJumpStatement(var1);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("break");
      if (this.breakLabel != null) {
         var2.append(" ");
         var2.append(this.breakLabel.toSource(0));
      }

      var2.append(";\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Name var2 = this.breakLabel;
         if (var2 != null) {
            var2.visit(var1);
         }
      }

   }
}
