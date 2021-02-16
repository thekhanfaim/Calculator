package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class InfixExpression extends AstNode {
   protected AstNode left;
   protected int operatorPosition = -1;
   protected AstNode right;

   public InfixExpression() {
   }

   public InfixExpression(int var1) {
      super(var1);
   }

   public InfixExpression(int var1, int var2) {
      super(var1, var2);
   }

   public InfixExpression(int var1, int var2, AstNode var3, AstNode var4) {
      super(var1, var2);
      this.setLeft(var3);
      this.setRight(var4);
   }

   public InfixExpression(int var1, AstNode var2, AstNode var3, int var4) {
      this.setType(var1);
      this.setOperatorPosition(var4 - var2.getPosition());
      this.setLeftAndRight(var2, var3);
   }

   public InfixExpression(AstNode var1, AstNode var2) {
      this.setLeftAndRight(var1, var2);
   }

   public AstNode getLeft() {
      return this.left;
   }

   public int getOperator() {
      return this.getType();
   }

   public int getOperatorPosition() {
      return this.operatorPosition;
   }

   public AstNode getRight() {
      return this.right;
   }

   public boolean hasSideEffects() {
      int var1 = this.getType();
      if (var1 != 90) {
         if (var1 != 105 && var1 != 106) {
            return super.hasSideEffects();
         } else {
            AstNode var3 = this.left;
            if (var3 == null || !var3.hasSideEffects()) {
               AstNode var4 = this.right;
               if (var4 == null || !var4.hasSideEffects()) {
                  return false;
               }
            }

            return true;
         }
      } else {
         AstNode var2 = this.right;
         return var2 != null && var2.hasSideEffects();
      }
   }

   public void setLeft(AstNode var1) {
      this.assertNotNull(var1);
      this.left = var1;
      this.setLineno(var1.getLineno());
      var1.setParent(this);
   }

   public void setLeftAndRight(AstNode var1, AstNode var2) {
      this.assertNotNull(var1);
      this.assertNotNull(var2);
      this.setBounds(var1.getPosition(), var2.getPosition() + var2.getLength());
      this.setLeft(var1);
      this.setRight(var2);
   }

   public void setOperator(int var1) {
      if (Token.isValidToken(var1)) {
         this.setType(var1);
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append("Invalid token: ");
         var2.append(var1);
         throw new IllegalArgumentException(var2.toString());
      }
   }

   public void setOperatorPosition(int var1) {
      this.operatorPosition = var1;
   }

   public void setRight(AstNode var1) {
      this.assertNotNull(var1);
      this.right = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.left.toSource());
      var2.append(" ");
      var2.append(operatorToString(this.getType()));
      var2.append(" ");
      var2.append(this.right.toSource());
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.left.visit(var1);
         this.right.visit(var1);
      }

   }
}
