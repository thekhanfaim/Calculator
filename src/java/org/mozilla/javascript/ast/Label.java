package org.mozilla.javascript.ast;

public class Label extends Jump {
   private String name;

   public Label() {
      this.type = 131;
   }

   public Label(int var1) {
      this(var1, -1);
   }

   public Label(int var1, int var2) {
      this.type = 131;
      this.position = var1;
      this.length = var2;
   }

   public Label(int var1, int var2, String var3) {
      this(var1, var2);
      this.setName(var3);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      String var2;
      if (var1 == null) {
         var2 = null;
      } else {
         var2 = var1.trim();
      }

      if (var2 != null && !"".equals(var2)) {
         this.name = var2;
      } else {
         throw new IllegalArgumentException("invalid label name");
      }
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append(this.name);
      var2.append(":\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
