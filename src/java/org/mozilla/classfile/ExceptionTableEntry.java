package org.mozilla.classfile;

final class ExceptionTableEntry {
   short itsCatchType;
   int itsEndLabel;
   int itsHandlerLabel;
   int itsStartLabel;

   ExceptionTableEntry(int var1, int var2, int var3, short var4) {
      this.itsStartLabel = var1;
      this.itsEndLabel = var2;
      this.itsHandlerLabel = var3;
      this.itsCatchType = var4;
   }
}
