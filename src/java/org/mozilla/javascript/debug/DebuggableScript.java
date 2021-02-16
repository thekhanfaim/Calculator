package org.mozilla.javascript.debug;

public interface DebuggableScript {
   DebuggableScript getFunction(int var1);

   int getFunctionCount();

   String getFunctionName();

   int[] getLineNumbers();

   int getParamAndVarCount();

   int getParamCount();

   String getParamOrVarName(int var1);

   DebuggableScript getParent();

   String getSourceName();

   boolean isFunction();

   boolean isGeneratedScript();

   boolean isTopLevel();
}
