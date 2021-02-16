package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LabeledStatement extends AstNode {
   private List labels = new ArrayList();
   private AstNode statement;

   public LabeledStatement() {
      this.type = 134;
   }

   public LabeledStatement(int var1) {
      super(var1);
      this.type = 134;
   }

   public LabeledStatement(int var1, int var2) {
      super(var1, var2);
      this.type = 134;
   }

   public void addLabel(Label var1) {
      this.assertNotNull(var1);
      this.labels.add(var1);
      var1.setParent(this);
   }

   public Label getFirstLabel() {
      return (Label)this.labels.get(0);
   }

   public Label getLabelByName(String var1) {
      Iterator var2 = this.labels.iterator();

      Label var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Label)var2.next();
      } while(!var1.equals(var3.getName()));

      return var3;
   }

   public List getLabels() {
      return this.labels;
   }

   public AstNode getStatement() {
      return this.statement;
   }

   public boolean hasSideEffects() {
      return true;
   }

   public void setLabels(List var1) {
      this.assertNotNull(var1);
      List var2 = this.labels;
      if (var2 != null) {
         var2.clear();
      }

      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         this.addLabel((Label)var3.next());
      }

   }

   public void setStatement(AstNode var1) {
      this.assertNotNull(var1);
      this.statement = var1;
      var1.setParent(this);
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      Iterator var3 = this.labels.iterator();

      while(var3.hasNext()) {
         var2.append(((Label)var3.next()).toSource(var1));
      }

      var2.append(this.statement.toSource(var1 + 1));
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Iterator var2 = this.labels.iterator();

         while(var2.hasNext()) {
            ((AstNode)var2.next()).visit(var1);
         }

         this.statement.visit(var1);
      }

   }
}
