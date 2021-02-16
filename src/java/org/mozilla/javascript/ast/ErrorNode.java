package org.mozilla.javascript.ast;

public class ErrorNode extends AstNode {
   private String message;

   public ErrorNode() {
      this.type = -1;
   }

   public ErrorNode(int var1) {
      super(var1);
      this.type = -1;
   }

   public ErrorNode(int var1, int var2) {
      super(var1, var2);
      this.type = -1;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public String toSource(int var1) {
      return "";
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
