package org.mozilla.classfile;

final class FieldOrMethodRef {
   private String className;
   private int hashCode = -1;
   private String name;
   private String type;

   FieldOrMethodRef(String var1, String var2, String var3) {
      this.className = var1;
      this.name = var2;
      this.type = var3;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof FieldOrMethodRef)) {
         return false;
      } else {
         FieldOrMethodRef var2 = (FieldOrMethodRef)var1;
         return this.className.equals(var2.className) && this.name.equals(var2.name) && this.type.equals(var2.type);
      }
   }

   public String getClassName() {
      return this.className;
   }

   public String getName() {
      return this.name;
   }

   public String getType() {
      return this.type;
   }

   public int hashCode() {
      if (this.hashCode == -1) {
         int var1 = this.className.hashCode();
         int var2 = this.name.hashCode();
         this.hashCode = this.type.hashCode() ^ var1 ^ var2;
      }

      return this.hashCode;
   }
}
