package org.mozilla.javascript;

import java.io.Serializable;

public class SymbolKey implements Symbol, Serializable {
   public static final SymbolKey HAS_INSTANCE = new SymbolKey("Symbol.hasInstance");
   public static final SymbolKey IS_CONCAT_SPREADABLE = new SymbolKey("Symbol.isConcatSpreadable");
   public static final SymbolKey IS_REGEXP = new SymbolKey("Symbol.isRegExp");
   public static final SymbolKey ITERATOR = new SymbolKey("Symbol.iterator");
   public static final SymbolKey MATCH = new SymbolKey("Symbol.match");
   public static final SymbolKey REPLACE = new SymbolKey("Symbol.replace");
   public static final SymbolKey SEARCH = new SymbolKey("Symbol.search");
   public static final SymbolKey SPECIES = new SymbolKey("Symbol.species");
   public static final SymbolKey SPLIT = new SymbolKey("Symbol.split");
   public static final SymbolKey TO_PRIMITIVE = new SymbolKey("Symbol.toPrimitive");
   public static final SymbolKey TO_STRING_TAG = new SymbolKey("Symbol.toStringTag");
   public static final SymbolKey UNSCOPABLES = new SymbolKey("Symbol.unscopables");
   private static final long serialVersionUID = -6019782713330994754L;
   private String name;

   public SymbolKey(String var1) {
      this.name = var1;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof SymbolKey) {
         return var1 == this;
      } else if (var1 instanceof NativeSymbol) {
         return ((NativeSymbol)var1).getKey() == this;
      } else {
         return false;
      }
   }

   public String getName() {
      return this.name;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   public String toString() {
      if (this.name == null) {
         return "Symbol()";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append("Symbol(");
         var1.append(this.name);
         var1.append(')');
         return var1.toString();
      }
   }
}
