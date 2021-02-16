package org.mozilla.javascript.ast;

public class Name extends AstNode {
   private String identifier;
   private Scope scope;

   public Name() {
      this.type = 39;
   }

   public Name(int var1) {
      super(var1);
      this.type = 39;
   }

   public Name(int var1, int var2) {
      super(var1, var2);
      this.type = 39;
   }

   public Name(int var1, int var2, String var3) {
      super(var1, var2);
      this.type = 39;
      this.setIdentifier(var3);
   }

   public Name(int var1, String var2) {
      super(var1);
      this.type = 39;
      this.setIdentifier(var2);
      this.setLength(var2.length());
   }

   public Scope getDefiningScope() {
      Scope var1 = this.getEnclosingScope();
      String var2 = this.getIdentifier();
      return var1 == null ? null : var1.getDefiningScope(var2);
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public Scope getScope() {
      return this.scope;
   }

   public boolean isLocalName() {
      Scope var1 = this.getDefiningScope();
      return var1 != null && var1.getParentScope() != null;
   }

   public int length() {
      String var1 = this.identifier;
      return var1 == null ? 0 : var1.length();
   }

   public void setIdentifier(String var1) {
      this.assertNotNull(var1);
      this.identifier = var1;
      this.setLength(var1.length());
   }

   public void setScope(Scope var1) {
      this.scope = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      String var4 = this.identifier;
      if (var4 == null) {
         var4 = "<null>";
      }

      var2.append(var4);
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      var1.visit(this);
   }
}
