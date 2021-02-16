package org.mozilla.javascript;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class NativeSymbol extends IdScriptableObject implements Symbol {
   public static final String CLASS_NAME = "Symbol";
   private static final Object CONSTRUCTOR_SLOT = new Object();
   private static final int ConstructorId_for = -1;
   private static final int ConstructorId_keyFor = -2;
   private static final Object GLOBAL_TABLE_KEY = new Object();
   private static final int Id_constructor = 1;
   private static final int Id_toString = 2;
   private static final int Id_valueOf = 4;
   private static final int MAX_PROTOTYPE_ID = 5;
   private static final int SymbolId_toPrimitive = 5;
   private static final int SymbolId_toStringTag = 3;
   public static final String TYPE_NAME = "symbol";
   private static final long serialVersionUID = -589539749749830003L;
   private final SymbolKey key;
   private final NativeSymbol symbolData;

   private NativeSymbol(String var1) {
      this.key = new SymbolKey(var1);
      this.symbolData = null;
   }

   public NativeSymbol(NativeSymbol var1) {
      this.key = var1.key;
      this.symbolData = var1.symbolData;
   }

   private NativeSymbol(SymbolKey var1) {
      this.key = var1;
      this.symbolData = this;
   }

   public static NativeSymbol construct(Context var0, Scriptable var1, Object[] var2) {
      Object var3 = CONSTRUCTOR_SLOT;
      var0.putThreadLocal(var3, Boolean.TRUE);
      boolean var7 = false;

      NativeSymbol var5;
      try {
         var7 = true;
         var5 = (NativeSymbol)var0.newObject(var1, "Symbol", var2);
         var7 = false;
      } finally {
         if (var7) {
            var0.removeThreadLocal(CONSTRUCTOR_SLOT);
         }
      }

      var0.removeThreadLocal(var3);
      return var5;
   }

   private static void createStandardSymbol(Context var0, Scriptable var1, ScriptableObject var2, String var3, SymbolKey var4) {
      var2.defineProperty((String)var3, (Object)var0.newObject(var1, "Symbol", new Object[]{var3, var4}), 7);
   }

   private Map getGlobalMap() {
      ScriptableObject var1 = (ScriptableObject)getTopLevelScope(this);
      Object var2 = GLOBAL_TABLE_KEY;
      Object var3 = (Map)var1.getAssociatedValue(var2);
      if (var3 == null) {
         var3 = new HashMap();
         var1.associateValue(var2, var3);
      }

      return (Map)var3;
   }

   private static NativeSymbol getSelf(Object var0) {
      try {
         NativeSymbol var2 = (NativeSymbol)var0;
         return var2;
      } catch (ClassCastException var3) {
         throw ScriptRuntime.typeError1("msg.invalid.type", var0.getClass().getName());
      }
   }

   public static void init(Context var0, Scriptable var1, boolean var2) {
      IdFunctionObject var3 = (new NativeSymbol("")).exportAsJSClass(5, var1, false);
      Object var4 = CONSTRUCTOR_SLOT;
      var0.putThreadLocal(var4, Boolean.TRUE);
      boolean var7 = false;

      try {
         var7 = true;
         createStandardSymbol(var0, var1, var3, "iterator", SymbolKey.ITERATOR);
         createStandardSymbol(var0, var1, var3, "species", SymbolKey.SPECIES);
         createStandardSymbol(var0, var1, var3, "toStringTag", SymbolKey.TO_STRING_TAG);
         createStandardSymbol(var0, var1, var3, "hasInstance", SymbolKey.HAS_INSTANCE);
         createStandardSymbol(var0, var1, var3, "isConcatSpreadable", SymbolKey.IS_CONCAT_SPREADABLE);
         createStandardSymbol(var0, var1, var3, "isRegExp", SymbolKey.IS_REGEXP);
         createStandardSymbol(var0, var1, var3, "toPrimitive", SymbolKey.TO_PRIMITIVE);
         createStandardSymbol(var0, var1, var3, "match", SymbolKey.MATCH);
         createStandardSymbol(var0, var1, var3, "replace", SymbolKey.REPLACE);
         createStandardSymbol(var0, var1, var3, "search", SymbolKey.SEARCH);
         createStandardSymbol(var0, var1, var3, "split", SymbolKey.SPLIT);
         createStandardSymbol(var0, var1, var3, "unscopables", SymbolKey.UNSCOPABLES);
         var7 = false;
      } finally {
         if (var7) {
            var0.removeThreadLocal(CONSTRUCTOR_SLOT);
         }
      }

      var0.removeThreadLocal(var4);
      if (var2) {
         var3.sealObject();
      }

   }

   private boolean isStrictMode() {
      Context var1 = Context.getCurrentContext();
      return var1 != null && var1.isStrictMode();
   }

   private static NativeSymbol js_constructor(Object[] var0) {
      String var1;
      if (var0.length > 0) {
         if (Undefined.instance.equals(var0[0])) {
            var1 = "";
         } else {
            var1 = ScriptRuntime.toString(var0[0]);
         }
      } else {
         var1 = "";
      }

      return var0.length > 1 ? new NativeSymbol((SymbolKey)var0[1]) : new NativeSymbol(new SymbolKey(var1));
   }

   private Object js_for(Context var1, Scriptable var2, Object[] var3) {
      Object var4;
      if (var3.length > 0) {
         var4 = var3[0];
      } else {
         var4 = Undefined.instance;
      }

      String var5 = ScriptRuntime.toString(var4);
      Map var6 = this.getGlobalMap();
      NativeSymbol var7 = (NativeSymbol)var6.get(var5);
      if (var7 == null) {
         var7 = construct(var1, var2, new Object[]{var5});
         var6.put(var5, var7);
      }

      return var7;
   }

   private Object js_keyFor(Context var1, Scriptable var2, Object[] var3) {
      Object var4;
      if (var3.length > 0) {
         var4 = var3[0];
      } else {
         var4 = Undefined.instance;
      }

      if (var4 instanceof NativeSymbol) {
         NativeSymbol var6 = (NativeSymbol)var4;
         Iterator var7 = this.getGlobalMap().entrySet().iterator();

         Entry var8;
         do {
            if (!var7.hasNext()) {
               return Undefined.instance;
            }

            var8 = (Entry)var7.next();
         } while(((NativeSymbol)var8.getValue()).key != var6.key);

         return var8.getKey();
      } else {
         JavaScriptException var5 = ScriptRuntime.throwCustomError(var1, var2, "TypeError", "Not a Symbol");
         throw var5;
      }
   }

   private Object js_valueOf() {
      return this.symbolData;
   }

   public boolean equals(Object var1) {
      return this.key.equals(var1);
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag("Symbol")) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 != -2) {
            if (var6 != -1) {
               if (var6 != 1) {
                  if (var6 != 2) {
                     return var6 != 4 && var6 != 5 ? super.execIdCall(var1, var2, var3, var4, var5) : getSelf(var4).js_valueOf();
                  } else {
                     return getSelf(var4).toString();
                  }
               } else if (var4 == null) {
                  if (var2.getThreadLocal(CONSTRUCTOR_SLOT) != null) {
                     return js_constructor(var5);
                  } else {
                     throw ScriptRuntime.typeError0("msg.no.symbol.new");
                  }
               } else {
                  return construct(var2, var3, var5);
               }
            } else {
               return this.js_for(var2, var3, var5);
            }
         } else {
            return this.js_keyFor(var2, var3, var5);
         }
      }
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
      super.fillConstructorProperties(var1);
      this.addIdFunctionProperty(var1, "Symbol", -1, "for", 1);
      this.addIdFunctionProperty(var1, "Symbol", -2, "keyFor", 1);
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      byte var3;
      String var4;
      if (var2 == 7) {
         var4 = "valueOf";
         var3 = 4;
      } else if (var2 == 8) {
         var4 = "toString";
         var3 = 2;
      } else {
         var3 = 0;
         var4 = null;
         if (var2 == 11) {
            var4 = "constructor";
            var3 = 1;
         }
      }

      if (var4 != null && var4 != var1 && !var4.equals(var1)) {
         var3 = 0;
      }

      return var3;
   }

   protected int findPrototypeId(Symbol var1) {
      if (SymbolKey.TO_STRING_TAG.equals(var1)) {
         return 3;
      } else {
         return SymbolKey.TO_PRIMITIVE.equals(var1) ? 5 : 0;
      }
   }

   public String getClassName() {
      return "Symbol";
   }

   SymbolKey getKey() {
      return this.key;
   }

   public String getTypeOf() {
      return this.isSymbol() ? "symbol" : super.getTypeOf();
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   protected void initPrototypeId(int var1) {
      if (var1 != 1) {
         if (var1 != 2) {
            if (var1 != 3) {
               if (var1 != 4) {
                  if (var1 != 5) {
                     super.initPrototypeId(var1);
                  } else {
                     this.initPrototypeMethod("Symbol", var1, SymbolKey.TO_PRIMITIVE, "Symbol.toPrimitive", 1);
                  }
               } else {
                  this.initPrototypeMethod("Symbol", var1, "valueOf", 0);
               }
            } else {
               this.initPrototypeValue(var1, SymbolKey.TO_STRING_TAG, "Symbol", 3);
            }
         } else {
            this.initPrototypeMethod("Symbol", var1, "toString", 0);
         }
      } else {
         this.initPrototypeMethod("Symbol", var1, "constructor", 1);
      }
   }

   public boolean isSymbol() {
      return this.symbolData == this;
   }

   public void put(int var1, Scriptable var2, Object var3) {
      if (!this.isSymbol()) {
         super.put(var1, var2, var3);
      } else if (this.isStrictMode()) {
         throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
      }
   }

   public void put(String var1, Scriptable var2, Object var3) {
      if (!this.isSymbol()) {
         super.put(var1, var2, var3);
      } else if (this.isStrictMode()) {
         throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
      }
   }

   public void put(Symbol var1, Scriptable var2, Object var3) {
      if (!this.isSymbol()) {
         super.put(var1, var2, var3);
      } else if (this.isStrictMode()) {
         throw ScriptRuntime.typeError0("msg.no.assign.symbol.strict");
      }
   }

   public String toString() {
      return this.key.toString();
   }
}
