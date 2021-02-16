package org.mozilla.javascript.ast;

public class ContinueStatement extends Jump {
   private Name label;
   private Loop target;

   public ContinueStatement() {
      this.type = 122;
   }

   public ContinueStatement(int var1) {
      this(var1, -1);
   }

   public ContinueStatement(int var1, int var2) {
      this.type = 122;
      this.position = var1;
      this.length = var2;
   }

   public ContinueStatement(int var1, int var2, Name var3) {
      this(var1, var2);
      this.setLabel(var3);
   }

   public ContinueStatement(int var1, Name var2) {
      this(var1);
      this.setLabel(var2);
   }

   public ContinueStatement(Name var1) {
      this.type = 122;
      this.setLabel(var1);
   }

   public Name getLabel() {
      return this.label;
   }

   public Loop getTarget() {
      return this.target;
   }

   public void setLabel(Name var1) {
      this.label = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public void setTarget(Loop var1) {
      this.assertNotNull(var1);
      this.target = var1;
      this.setJumpStatement(var1);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("continue");
      if (this.label != null) {
         var2.append(" ");
         var2.append(this.label.toSource(0));
      }

      var2.append(";\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Name var2 = this.label;
         if (var2 != null) {
            var2.visit(var1);
         }
      }

   }
}
