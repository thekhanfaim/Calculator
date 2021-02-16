package org.mozilla.javascript.ast;

class AstRoot$1 implements NodeVisitor {
   // $FF: synthetic field
   final AstRoot this$0;

   AstRoot$1(AstRoot var1) {
      this.this$0 = var1;
   }

   public boolean visit(AstNode var1) {
      if (var1.getType() == 137) {
         return true;
      } else if (var1.getParent() != null) {
         return true;
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append("No parent for node: ");
         var2.append(var1);
         var2.append("\n");
         var2.append(var1.toSource(0));
         throw new IllegalStateException(var2.toString());
      }
   }
}
