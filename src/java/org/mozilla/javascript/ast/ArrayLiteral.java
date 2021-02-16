package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ArrayLiteral extends AstNode implements DestructuringForm {
   private static final List NO_ELEMS = Collections.unmodifiableList(new ArrayList());
   private int destructuringLength;
   private List elements;
   private boolean isDestructuring;
   private int skipCount;

   public ArrayLiteral() {
      this.type = 66;
   }

   public ArrayLiteral(int var1) {
      super(var1);
      this.type = 66;
   }

   public ArrayLiteral(int var1, int var2) {
      super(var1, var2);
      this.type = 66;
   }

   public void addElement(AstNode var1) {
      this.assertNotNull(var1);
      if (this.elements == null) {
         this.elements = new ArrayList();
      }

      this.elements.add(var1);
      var1.setParent(this);
   }

   public int getDestructuringLength() {
      return this.destructuringLength;
   }

   public AstNode getElement(int var1) {
      List var2 = this.elements;
      if (var2 != null) {
         return (AstNode)var2.get(var1);
      } else {
         throw new IndexOutOfBoundsException("no elements");
      }
   }

   public List getElements() {
      List var1 = this.elements;
      return var1 != null ? var1 : NO_ELEMS;
   }

   public int getSize() {
      List var1 = this.elements;
      return var1 == null ? 0 : var1.size();
   }

   public int getSkipCount() {
      return this.skipCount;
   }

   public boolean isDestructuring() {
      return this.isDestructuring;
   }

   public void setDestructuringLength(int var1) {
      this.destructuringLength = var1;
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
            this.addElement((AstNode)var3.next());
         }

      }
   }

   public void setIsDestructuring(boolean var1) {
      this.isDestructuring = var1;
   }

   public void setSkipCount(int var1) {
      this.skipCount = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("[");
      List var5 = this.elements;
      if (var5 != null) {
         this.printList(var5, var2);
      }

      var2.append("]");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Iterator var2 = this.getElements().iterator();

         while(var2.hasNext()) {
            ((AstNode)var2.next()).visit(var1);
         }
      }

   }
}
