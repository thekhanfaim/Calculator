package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FunctionCall extends AstNode {
   protected static final List NO_ARGS = Collections.unmodifiableList(new ArrayList());
   protected List arguments;
   protected int lp = -1;
   protected int rp = -1;
   protected AstNode target;

   public FunctionCall() {
      this.type = 38;
   }

   public FunctionCall(int var1) {
      super(var1);
      this.type = 38;
   }

   public FunctionCall(int var1, int var2) {
      super(var1, var2);
      this.type = 38;
   }

   public void addArgument(AstNode var1) {
      this.assertNotNull(var1);
      if (this.arguments == null) {
         this.arguments = new ArrayList();
      }

      this.arguments.add(var1);
      var1.setParent(this);
   }

   public List getArguments() {
      List var1 = this.arguments;
      return var1 != null ? var1 : NO_ARGS;
   }

   public int getLp() {
      return this.lp;
   }

   public int getRp() {
      return this.rp;
   }

   public AstNode getTarget() {
      return this.target;
   }

   public void setArguments(List var1) {
      if (var1 == null) {
         this.arguments = null;
      } else {
         List var2 = this.arguments;
         if (var2 != null) {
            var2.clear();
         }

         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            this.addArgument((AstNode)var3.next());
         }

      }
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

   public void setTarget(AstNode var1) {
      this.assertNotNull(var1);
      this.target = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.target.toSource(0));
      var2.append("(");
      List var6 = this.arguments;
      if (var6 != null) {
         this.printList(var6, var2);
      }

      var2.append(")");
      if (this.getInlineComment() != null) {
         var2.append(this.getInlineComment().toSource(var1));
         var2.append("\n");
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
      }

   }
}
