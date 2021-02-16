package org.mozilla.javascript.ast;

import java.util.Iterator;

public class NewExpression extends FunctionCall {
   private ObjectLiteral initializer;

   public NewExpression() {
      this.type = 30;
   }

   public NewExpression(int var1) {
      super(var1);
      this.type = 30;
   }

   public NewExpression(int var1, int var2) {
      super(var1, var2);
      this.type = 30;
   }

   public ObjectLiteral getInitializer() {
      return this.initializer;
   }

   public void setInitializer(ObjectLiteral var1) {
      this.initializer = var1;
      if (var1 != null) {
         var1.setParent(this);
      }

   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("new ");
      var2.append(this.target.toSource(0));
      var2.append("(");
      if (this.arguments != null) {
         this.printList(this.arguments, var2);
      }

      var2.append(")");
      if (this.initializer != null) {
         var2.append(" ");
         var2.append(this.initializer.toSource(0));
      }

      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         this.target.visit(var1);
         Iterator var2 = this.getArguments().iterator();

         while(var2.hasNext()) {
            ((AstNode)var2.next()).visit(var1);
         }

         ObjectLiteral var3 = this.initializer;
         if (var3 != null) {
            var3.visit(var1);
         }
      }

   }
}
