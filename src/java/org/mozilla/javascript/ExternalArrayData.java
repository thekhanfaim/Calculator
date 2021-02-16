package org.mozilla.javascript;

public interface ExternalArrayData {
   Object getArrayElement(int var1);

   int getArrayLength();

   void setArrayElement(int var1, Object var2);
}
