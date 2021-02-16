package org.mozilla.javascript;

class ContextFactory$1GlobalSetterImpl implements ContextFactory.GlobalSetter {
   public ContextFactory getContextFactoryGlobal() {
      return ContextFactory.access$000();
   }

   public void setContextFactoryGlobal(ContextFactory var1) {
      ContextFactory var2;
      if (var1 == null) {
         var2 = new ContextFactory();
      } else {
         var2 = var1;
      }

      ContextFactory.access$002(var2);
   }
}
