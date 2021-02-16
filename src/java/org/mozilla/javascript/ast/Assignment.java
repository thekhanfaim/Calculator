package org.mozilla.javascript.ast;

public class Assignment extends InfixExpression {
   public Assignment() {
   }

   public Assignment(int var1) {
      super(var1);
   }

   public Assignment(int var1, int var2) {
      super(var1, var2);
   }

   public Assignment(int var1, int var2, AstNode var3, AstNode var4) {
      super(var1, var2, var3, var4);
   }

   public Assignment(int var1, AstNode var2, AstNode var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public Assignment(AstNode var1, AstNode var2) {
      super(var1, var2);
   }
}
