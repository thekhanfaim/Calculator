package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class Comment extends AstNode {
   private Token.CommentType commentType;
   private String value;

   public Comment(int var1, int var2, Token.CommentType var3, String var4) {
      super(var1, var2);
      this.type = 162;
      this.commentType = var3;
      this.value = var4;
   }

   public Token.CommentType getCommentType() {
      return this.commentType;
   }

   public String getValue() {
      return this.value;
   }

   public void setCommentType(Token.CommentType var1) {
      this.commentType = var1;
   }

   public void setValue(String var1) {
      this.value = var1;
      this.setLength(var1.length());
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder(10 + this.getLength());
      var2.append(this.makeIndent(var1));
      var2.append(this.value);
      if (Token.CommentType.BLOCK_COMMENT == this.getCommentType()) {
         var2.append("\n");
      }

      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
