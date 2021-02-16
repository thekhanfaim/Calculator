package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;

public class Jump extends AstNode {
   private Jump jumpNode;
   public Node target;
   private Node target2;

   public Jump() {
      this.type = -1;
   }

   public Jump(int var1) {
      this.type = var1;
   }

   public Jump(int var1, int var2) {
      this(var1);
      this.setLineno(var2);
   }

   public Jump(int var1, Node var2) {
      this(var1);
      this.addChildToBack(var2);
   }

   public Jump(int var1, Node var2, int var3) {
      this(var1, var2);
      this.setLineno(var3);
   }

   public Node getContinue() {
      if (this.type != 133) {
         codeBug();
      }

      return this.target2;
   }

   public Node getDefault() {
      if (this.type != 115) {
         codeBug();
      }

      return this.target2;
   }

   public Node getFinally() {
      if (this.type != 82) {
         codeBug();
      }

      return this.target2;
   }

   public Jump getJumpStatement() {
      if (this.type != 121 && this.type != 122) {
         codeBug();
      }

      return this.jumpNode;
   }

   public Jump getLoop() {
      if (this.type != 131) {
         codeBug();
      }

      return this.jumpNode;
   }

   public void setContinue(Node var1) {
      if (this.type != 133) {
         codeBug();
      }

      if (var1.getType() != 132) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = var1;
   }

   public void setDefault(Node var1) {
      if (this.type != 115) {
         codeBug();
      }

      if (var1.getType() != 132) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = var1;
   }

   public void setFinally(Node var1) {
      if (this.type != 82) {
         codeBug();
      }

      if (var1.getType() != 132) {
         codeBug();
      }

      if (this.target2 != null) {
         codeBug();
      }

      this.target2 = var1;
   }

   public void setJumpStatement(Jump var1) {
      if (this.type != 121 && this.type != 122) {
         codeBug();
      }

      if (var1 == null) {
         codeBug();
      }

      if (this.jumpNode != null) {
         codeBug();
      }

      this.jumpNode = var1;
   }

   public void setLoop(Jump var1) {
      if (this.type != 131) {
         codeBug();
      }

      if (var1 == null) {
         codeBug();
      }

      if (this.jumpNode != null) {
         codeBug();
      }

      this.jumpNode = var1;
   }

   public String toSource(int var1) {
      throw new UnsupportedOperationException(this.toString());
   }

   public void visit(NodeVisitor var1) {
      throw new UnsupportedOperationException(this.toString());
   }
}
