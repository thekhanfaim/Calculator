package org.mozilla.javascript.ast;

import org.mozilla.javascript.ErrorReporter;

public interface IdeErrorReporter extends ErrorReporter {
   void error(String var1, String var2, int var3, int var4);

   void warning(String var1, String var2, int var3, int var4);
}
