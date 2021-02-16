package org.mozilla.javascript.ast;

public class ElementGet extends AstNode {
   private AstNode element;
   private int lb = -1;
   private int rb = -1;
   private AstNode target;

   public ElementGet() {
      this.type = 36;
   }

   public ElementGet(int var1) {
      super(var1);
      this.type = 36;
   }

   public ElementGet(int var1, int var2) {
      super(var1, var2);
      this.type = 36;
   }

   public ElementGet(AstNode var1, AstNode var2) {
      this.type = 36;
      this.setTarget(var1);
      this.setElement(var2);
   }

   public AstNode getElement() {
      return this.element;
   }

   public int getLb() {
      return this.lb;
   }

   public int getRb() {
      return this.rb;
   }

   public AstNode getTarget() {
      return this.target;
   }

   public void setElement(AstNode var1) {
      this.assertNotNull(var1);
      this.element = var1;
      var1.setParent(this);
   }

   public void setLb(int var1) {
      this.lb = var1;
   }

   public void setParens(int var1, int var2) {
      this.lb = var1;
      this.rb = var2;
   }

   public void setRb(int var1) {
      this.rb = var1;
   }

   public void setTarget(AstNode var1) {
      this.assertNotNull(var1);
      this.target = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.target.toSource(0));
      var2.append("[");
      var2.append(this.element.toSource(0));
      var2.append("]");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.target.visit(var1);
         this.element.visit(var1);
      }

   }
}
