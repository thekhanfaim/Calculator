package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ObjectLiteral extends AstNode implements DestructuringForm {
   private static final List NO_ELEMS = Collections.unmodifiableList(new ArrayList());
   private List elements;
   boolean isDestructuring;

   public ObjectLiteral() {
      this.type = 67;
   }

   public ObjectLiteral(int var1) {
      super(var1);
      this.type = 67;
   }

   public ObjectLiteral(int var1, int var2) {
      super(var1, var2);
      this.type = 67;
   }

   public void addElement(ObjectProperty var1) {
      this.assertNotNull(var1);
      if (this.elements == null) {
         this.elements = new ArrayList();
      }

      this.elements.add(var1);
      var1.setParent(this);
   }

   public List getElements() {
      List var1 = this.elements;
      return var1 != null ? var1 : NO_ELEMS;
   }

   public boolean isDestructuring() {
      return this.isDestructuring;
   }

   public void setElements(List var1) {
      if (var1 == null) {
         this.elements = null;
      } else {
         List var2 = this.elements;
         if (var2 != null) {
            var2.clear();
         }

         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            this.addElement((ObjectProperty)var3.next());
         }

      }
   }

   public void setIsDestructuring(boolean var1) {
      this.isDestructuring = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("{");
      List var5 = this.elements;
      if (var5 != null) {
         this.printList(var5, var2);
      }

      var2.append("}");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Iterator var2 = this.getElements().iterator();

         while(var2.hasNext()) {
            ((ObjectProperty)var2.next()).visit(var1);
         }
      }

   }
}
