package org.mozilla.javascript.ast;

public class CatchClause extends AstNode {
   private Block body;
   private AstNode catchCondition;
   private int ifPosition = -1;
   private int lp = -1;
   private int rp = -1;
   private Name varName;

   public CatchClause() {
      this.type = 125;
   }

   public CatchClause(int var1) {
      super(var1);
      this.type = 125;
   }

   public CatchClause(int var1, int var2) {
      super(var1, var2);
      this.type = 125;
   }

   public Block getBody() {
      return this.body;
   }

   public AstNode getCatchCondition() {
      return this.catchCondition;
   }

   public int getIfPosition() {
      return this.ifPosition;
   }

   public int getLp() {
      return this.lp;
   }

   public int getRp() {
      return this.rp;
   }

   public Name getVarName() {
      return this.varName;
   }

   public void setBody(Block var1) {
      this.assertNotNull(var1);
      this.body = var1;
      var1.setParent(this);
   }

   public void setCatchCondition(AstNode var1) {
      this.catchCondition = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public void setIfPosition(int var1) {
      this.ifPosition = var1;
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

   public void setVarName(Name var1) {
      this.assertNotNull(var1);
      this.varName = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("catch (");
      var2.append(this.varName.toSource(0));
      if (this.catchCondition != null) {
         var2.append(" if ");
         var2.append(this.catchCondition.toSource(0));
      }

      var2.append(") ");
      var2.append(this.body.toSource(0));
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.varName.visit(var1);
         AstNode var2 = this.catchCondition;
         if (var2 != null) {
            var2.visit(var1);
         }

         this.body.visit(var1);
      }

   }
}
