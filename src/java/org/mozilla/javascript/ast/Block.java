package org.mozilla.javascript.ast;

import java.util.Iterator;
import org.mozilla.javascript.Node;

public class Block extends AstNode {
   public Block() {
      this.type = 130;
   }

   public Block(int var1) {
      super(var1);
      this.type = 130;
   }

   public Block(int var1, int var2) {
      super(var1, var2);
      this.type = 130;
   }

   public void addStatement(AstNode var1) {
      this.addChild(var1);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("{\n");
      Iterator var5 = this.iterator();

      while(var5.hasNext()) {
         AstNode var10 = (AstNode)((Node)var5.next());
         var2.append(var10.toSource(var1 + 1));
         if (var10.getType() == 162) {
            var2.append("\n");
         }
      }

      var2.append(this.makeIndent(var1));
      var2.append("}");
      if (this.getInlineComment() != null) {
         var2.append(this.getInlineComment().toSource(var1));
      }

      var2.append("\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            ((AstNode)((Node)var2.next())).visit(var1);
         }
      }

   }
}
