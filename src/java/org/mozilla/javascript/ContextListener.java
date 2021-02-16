package org.mozilla.javascript;

@Deprecated
public interface ContextListener extends ContextFactory.Listener {
   @Deprecated
   void contextEntered(Context var1);

   @Deprecated
   void contextExited(Context var1);
}
