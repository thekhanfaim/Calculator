package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mozilla.javascript.EvaluatorException;

public class ErrorCollector implements IdeErrorReporter {
   private List errors = new ArrayList();

   public void error(String var1, String var2, int var3, int var4) {
      List var5 = this.errors;
      ParseProblem var6 = new ParseProblem(ParseProblem.Type.Error, var1, var2, var3, var4);
      var5.add(var6);
   }

   public void error(String var1, String var2, int var3, String var4, int var5) {
      throw new UnsupportedOperationException();
   }

   public List getErrors() {
      return this.errors;
   }

   public EvaluatorException runtimeError(String var1, String var2, int var3, String var4, int var5) {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(100 * this.errors.size());
      Iterator var2 = this.errors.iterator();

      while(var2.hasNext()) {
         var1.append(((ParseProblem)var2.next()).toString());
         var1.append("\n");
      }

      return var1.toString();
   }

   public void warning(String var1, String var2, int var3, int var4) {
      List var5 = this.errors;
      ParseProblem var6 = new ParseProblem(ParseProblem.Type.Warning, var1, var2, var3, var4);
      var5.add(var6);
   }

   public void warning(String var1, String var2, int var3, String var4, int var5) {
      throw new UnsupportedOperationException();
   }
}
