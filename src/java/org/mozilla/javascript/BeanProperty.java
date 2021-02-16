package org.mozilla.javascript;

class BeanProperty {
   MemberBox getter;
   MemberBox setter;
   NativeJavaMethod setters;

   BeanProperty(MemberBox var1, MemberBox var2, NativeJavaMethod var3) {
      this.getter = var1;
      this.setter = var2;
      this.setters = var3;
   }
}
