package org.mozilla.javascript.ast;

public class IfStatement extends AstNode {
   private AstNode condition;
   private AstNode elseKeyWordInlineComment;
   private AstNode elsePart;
   private int elsePosition = -1;
   private int lp = -1;
   private int rp = -1;
   private AstNode thenPart;

   public IfStatement() {
      this.type = 113;
   }

   public IfStatement(int var1) {
      super(var1);
      this.type = 113;
   }

   public IfStatement(int var1, int var2) {
      super(var1, var2);
      this.type = 113;
   }

   public AstNode getCondition() {
      return this.condition;
   }

   public AstNode getElseKeyWordInlineComment() {
      return this.elseKeyWordInlineComment;
   }

   public AstNode getElsePart() {
      return this.elsePart;
   }

   public int getElsePosition() {
      return this.elsePosition;
   }

   public int getLp() {
      return this.lp;
   }

   public int getRp() {
      return this.rp;
   }

   public AstNode getThenPart() {
      return this.thenPart;
   }

   public void setCondition(AstNode var1) {
      this.assertNotNull(var1);
      this.condition = var1;
      var1.setParent(this);
   }

   public void setElseKeyWordInlineComment(AstNode var1) {
      this.elseKeyWordInlineComment = var1;
   }

   public void setElsePart(AstNode var1) {
      this.elsePart = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public void setElsePosition(int var1) {
      this.elsePosition = var1;
   }

   public void setLp(int var1) {
      this.lp = var1;
   }

   public void setParens(int var1, int var2) {
      this.lp = var1;
      this.rp = var2;
   }

   public void setRp(int var1) {
      this.rp = var1;
   }

   public void setThenPart(AstNode var1) {
      this.assertNotNull(var1);
      this.thenPart = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      String var2 = this.makeIndent(var1);
      StringBuilder var3 = new StringBuilder(32);
      var3.append(var2);
      var3.append("if (");
      var3.append(this.condition.toSource(0));
      var3.append(") ");
      if (this.getInlineComment() != null) {
         var3.append("    ");
         var3.append(this.getInlineComment().toSource());
         var3.append("\n");
      }

      if (this.thenPart.getType() != 130) {
         if (this.getInlineComment() == null) {
            var3.append("\n");
         }

         var3.append(this.makeIndent(var1 + 1));
      }

      var3.append(this.thenPart.toSource(var1).trim());
      if (this.elsePart != null) {
         if (this.thenPart.getType() != 130) {
            var3.append("\n");
            var3.append(var2);
            var3.append("else ");
         } else {
            var3.append(" else ");
         }

         if (this.getElseKeyWordInlineComment() != null) {
            var3.append("    ");
            var3.append(this.getElseKeyWordInlineComment().toSource());
            var3.append("\n");
         }

         if (this.elsePart.getType() != 130 && this.elsePart.getType() != 113) {
            if (this.getElseKeyWordInlineComment() == null) {
               var3.append("\n");
            }

            var3.append(this.makeIndent(var1 + 1));
         }

         var3.append(this.elsePart.toSource(var1).trim());
      }

      var3.append("\n");
      return var3.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.condition.visit(var1);
         this.thenPart.visit(var1);
         AstNode var2 = this.elsePart;
         if (var2 != null) {
            var2.visit(var1);
         }
      }

   }
}
