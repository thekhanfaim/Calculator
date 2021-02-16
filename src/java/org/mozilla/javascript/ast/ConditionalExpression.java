package org.mozilla.javascript.ast;

public class ConditionalExpression extends AstNode {
   private int colonPosition = -1;
   private AstNode falseExpression;
   private int questionMarkPosition = -1;
   private AstNode testExpression;
   private AstNode trueExpression;

   public ConditionalExpression() {
      this.type = 103;
   }

   public ConditionalExpression(int var1) {
      super(var1);
      this.type = 103;
   }

   public ConditionalExpression(int var1, int var2) {
      super(var1, var2);
      this.type = 103;
   }

   public int getColonPosition() {
      return this.colonPosition;
   }

   public AstNode getFalseExpression() {
      return this.falseExpression;
   }

   public int getQuestionMarkPosition() {
      return this.questionMarkPosition;
   }

   public AstNode getTestExpression() {
      return this.testExpression;
   }

   public AstNode getTrueExpression() {
      return this.trueExpression;
   }

   public boolean hasSideEffects() {
      if (this.testExpression == null || this.trueExpression == null || this.falseExpression == null) {
         codeBug();
      }

      return this.trueExpression.hasSideEffects() && this.falseExpression.hasSideEffects();
   }

   public void setColonPosition(int var1) {
      this.colonPosition = var1;
   }

   public void setFalseExpression(AstNode var1) {
      this.assertNotNull(var1);
      this.falseExpression = var1;
      var1.setParent(this);
   }

   public void setQuestionMarkPosition(int var1) {
      this.questionMarkPosition = var1;
   }

   public void setTestExpression(AstNode var1) {
      this.assertNotNull(var1);
      this.testExpression = var1;
      var1.setParent(this);
   }

   public void setTrueExpression(AstNode var1) {
      this.assertNotNull(var1);
      this.trueExpression = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.testExpression.toSource(var1));
      var2.append(" ? ");
      var2.append(this.trueExpression.toSource(0));
      var2.append(" : ");
      var2.append(this.falseExpression.toSource(0));
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.testExpression.visit(var1);
         this.trueExpression.visit(var1);
         this.falseExpression.visit(var1);
      }

   }
}
