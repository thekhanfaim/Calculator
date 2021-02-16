package org.mozilla.javascript.ast;

public class KeywordLiteral extends AstNode {
   public KeywordLiteral() {
   }

   public KeywordLiteral(int var1) {
      super(var1);
   }

   public KeywordLiteral(int var1, int var2) {
      super(var1, var2);
   }

   public KeywordLiteral(int var1, int var2, int var3) {
      super(var1, var2);
      this.setType(var3);
   }

   public boolean isBooleanLiteral() {
      return this.type == 45 || this.type == 44;
   }

   public KeywordLiteral setType(int var1) {
      if (var1 != 43 && var1 != 42 && var1 != 45 && var1 != 44 && var1 != 161) {
         StringBuilder var2 = new StringBuilder();
         var2.append("Invalid node type: ");
         var2.append(var1);
         throw new IllegalArgumentException(var2.toString());
      } else {
         this.type = var1;
         return this;
      }
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      int var4 = this.getType();
      if (var4 != 161) {
         switch(var4) {
         case 42:
            var2.append("null");
            break;
         case 43:
            var2.append("this");
            break;
         case 44:
            var2.append("false");
            break;
         case 45:
            var2.append("true");
            break;
         default:
            StringBuilder var10 = new StringBuilder();
            var10.append("Invalid keyword literal type: ");
            var10.append(this.getType());
            throw new IllegalStateException(var10.toString());
         }
      } else {
         var2.append("debugger;\n");
      }

      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
