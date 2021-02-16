package org.mozilla.javascript.ast;

public class PropertyGet extends InfixExpression {
   public PropertyGet() {
      this.type = 33;
   }

   public PropertyGet(int var1) {
      super(var1);
      this.type = 33;
   }

   public PropertyGet(int var1, int var2) {
      super(var1, var2);
      this.type = 33;
   }

   public PropertyGet(int var1, int var2, AstNode var3, Name var4) {
      super(var1, var2, var3, var4);
      this.type = 33;
   }

   public PropertyGet(AstNode var1, Name var2) {
      super(var1, var2);
      this.type = 33;
   }

   public PropertyGet(AstNode var1, Name var2, int var3) {
      super(33, var1, var2, var3);
      this.type = 33;
   }

   public Name getProperty() {
      return (Name)this.getRight();
   }

   public AstNode getTarget() {
      return this.getLeft();
   }

   public void setProperty(Name var1) {
      this.setRight(var1);
   }

   public void setTarget(AstNode var1) {
      this.setLeft(var1);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.getLeft().toSource(0));
      var2.append(".");
      var2.append(this.getRight().toSource(0));
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.getTarget().visit(var1);
         this.getProperty().visit(var1);
      }

   }
}
