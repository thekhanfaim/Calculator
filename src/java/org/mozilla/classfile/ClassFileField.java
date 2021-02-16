package org.mozilla.classfile;

final class ClassFileField {
   private short itsAttr1;
   private short itsAttr2;
   private short itsAttr3;
   private short itsFlags;
   private boolean itsHasAttributes;
   private int itsIndex;
   private short itsNameIndex;
   private short itsTypeIndex;

   ClassFileField(short var1, short var2, short var3) {
      this.itsNameIndex = var1;
      this.itsTypeIndex = var2;
      this.itsFlags = var3;
      this.itsHasAttributes = false;
   }

   int getWriteSize() {
      return !this.itsHasAttributes ? 6 + 2 : 6 + 10;
   }

   void setAttributes(short var1, short var2, short var3, int var4) {
      this.itsHasAttributes = true;
      this.itsAttr1 = var1;
      this.itsAttr2 = var2;
      this.itsAttr3 = var3;
      this.itsIndex = var4;
   }

   int write(byte[] var1, int var2) {
      int var3 = ClassFileWriter.putInt16(this.itsFlags, var1, var2);
      int var4 = ClassFileWriter.putInt16(this.itsNameIndex, var1, var3);
      int var5 = ClassFileWriter.putInt16(this.itsTypeIndex, var1, var4);
      if (!this.itsHasAttributes) {
         return ClassFileWriter.putInt16(0, var1, var5);
      } else {
         int var6 = ClassFileWriter.putInt16(1, var1, var5);
         int var7 = ClassFileWriter.putInt16(this.itsAttr1, var1, var6);
         int var8 = ClassFileWriter.putInt16(this.itsAttr2, var1, var7);
         int var9 = ClassFileWriter.putInt16(this.itsAttr3, var1, var8);
         return ClassFileWriter.putInt16(this.itsIndex, var1, var9);
      }
   }
}
