package org.mozilla.javascript.ast;

public class ObjectProperty extends InfixExpression {
   public ObjectProperty() {
      this.type = 104;
   }

   public ObjectProperty(int var1) {
      super(var1);
      this.type = 104;
   }

   public ObjectProperty(int var1, int var2) {
      super(var1, var2);
      this.type = 104;
   }

   public boolean isGetterMethod() {
      return this.type == 152;
   }

   public boolean isMethod() {
      return this.isGetterMethod() || this.isSetterMethod() || this.isNormalMethod();
   }

   public boolean isNormalMethod() {
      return this.type == 164;
   }

   public boolean isSetterMethod() {
      return this.type == 153;
   }

   public void setIsGetterMethod() {
      this.type = 152;
   }

   public void setIsNormalMethod() {
      this.type = 164;
   }

   public void setIsSetterMethod() {
      this.type = 153;
   }

   public void setNodeType(int var1) {
      if (var1 != 104 && var1 != 152 && var1 != 153 && var1 != 164) {
         StringBuilder var3 = new StringBuilder();
         var3.append("invalid node type: ");
         var3.append(var1);
         throw new IllegalArgumentException(var3.toString());
      } else {
         this.setType(var1);
      }
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("\n");
      var2.append(this.makeIndent(var1 + 1));
      if (this.isGetterMethod()) {
         var2.append("get ");
      } else if (this.isSetterMethod()) {
         var2.append("set ");
      }

      AstNode var5 = this.left;
      int var6;
      if (this.getType() == 104) {
         var6 = 0;
      } else {
         var6 = var1;
      }

      var2.append(var5.toSource(var6));
      if (this.type == 104) {
         var2.append(": ");
      }

      AstNode var8 = this.right;
      int var9;
      if (this.getType() == 104) {
         var9 = 0;
      } else {
         var9 = var1 + 1;
      }

      var2.append(var8.toSource(var9));
      return var2.toString();
   }
}
