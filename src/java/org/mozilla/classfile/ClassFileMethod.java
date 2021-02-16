package org.mozilla.classfile;

final class ClassFileMethod {
   private byte[] itsCodeAttribute;
   private short itsFlags;
   private String itsName;
   private short itsNameIndex;
   private String itsType;
   private short itsTypeIndex;

   ClassFileMethod(String var1, short var2, String var3, short var4, short var5) {
      this.itsName = var1;
      this.itsNameIndex = var2;
      this.itsType = var3;
      this.itsTypeIndex = var4;
      this.itsFlags = var5;
   }

   short getFlags() {
      return this.itsFlags;
   }

   String getName() {
      return this.itsName;
   }

   String getType() {
      return this.itsType;
   }

   int getWriteSize() {
      return 8 + this.itsCodeAttribute.length;
   }

   void setCodeAttribute(byte[] var1) {
      this.itsCodeAttribute = var1;
   }

   int write(byte[] var1, int var2) {
      int var3 = ClassFileWriter.putInt16(this.itsFlags, var1, var2);
      int var4 = ClassFileWriter.putInt16(this.itsNameIndex, var1, var3);
      int var5 = ClassFileWriter.putInt16(1, var1, ClassFileWriter.putInt16(this.itsTypeIndex, var1, var4));
      byte[] var6 = this.itsCodeAttribute;
      System.arraycopy(var6, 0, var1, var5, var6.length);
      return var5 + this.itsCodeAttribute.length;
   }
}
