package org.mozilla.javascript;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.mozilla.javascript.json.JsonParser;

public final class NativeJSON extends IdScriptableObject {
   private static final int Id_parse = 2;
   private static final int Id_stringify = 3;
   private static final int Id_toSource = 1;
   private static final Object JSON_TAG = "JSON";
   private static final int LAST_METHOD_ID = 3;
   private static final int MAX_ID = 3;
   private static final int MAX_STRINGIFY_GAP_LENGTH = 10;
   private static final long serialVersionUID = -4567599697595654984L;

   private NativeJSON() {
   }

   static void init(Scriptable var0, boolean var1) {
      NativeJSON var2 = new NativeJSON();
      var2.activatePrototypeMap(3);
      var2.setPrototype(getObjectPrototype(var0));
      var2.setParentScope(var0);
      if (var1) {
         var2.sealObject();
      }

      ScriptableObject.defineProperty(var0, "JSON", var2, 2);
   }

   private static String ja(NativeArray var0, NativeJSON.StringifyState var1) {
      if (var1.stack.search(var0) == -1) {
         var1.stack.push(var0);
         String var4 = var1.indent;
         StringBuilder var5 = new StringBuilder();
         var5.append(var1.indent);
         var5.append(var1.gap);
         var1.indent = var5.toString();
         LinkedList var8 = new LinkedList();
         long var9 = var0.getLength();

         for(long var11 = 0L; var11 < var9; ++var11) {
            Object var30;
            if (var11 > 2147483647L) {
               var30 = str(Long.toString(var11), var0, var1);
            } else {
               var30 = str((int)var11, var0, var1);
            }

            if (var30 == Undefined.instance) {
               var8.add("null");
            } else {
               var8.add(var30);
            }
         }

         String var17;
         if (var8.isEmpty()) {
            var17 = "[]";
         } else if (var1.gap.length() == 0) {
            StringBuilder var13 = new StringBuilder();
            var13.append('[');
            var13.append(join(var8, ","));
            var13.append(']');
            var17 = var13.toString();
         } else {
            StringBuilder var19 = new StringBuilder();
            var19.append(",\n");
            var19.append(var1.indent);
            String var22 = join(var8, var19.toString());
            StringBuilder var23 = new StringBuilder();
            var23.append("[\n");
            var23.append(var1.indent);
            var23.append(var22);
            var23.append('\n');
            var23.append(var4);
            var23.append(']');
            var17 = var23.toString();
         }

         var1.stack.pop();
         var1.indent = var4;
         return var17;
      } else {
         EcmaError var2 = ScriptRuntime.typeError0("msg.cyclic.value");
         throw var2;
      }
   }

   private static String jo(Scriptable var0, NativeJSON.StringifyState var1) {
      if (var1.stack.search(var0) == -1) {
         var1.stack.push(var0);
         String var4 = var1.indent;
         StringBuilder var5 = new StringBuilder();
         var5.append(var1.indent);
         var5.append(var1.gap);
         var1.indent = var5.toString();
         Object[] var8;
         if (var1.propertyList != null) {
            var8 = var1.propertyList.toArray();
         } else {
            var8 = var0.getIds();
         }

         LinkedList var9 = new LinkedList();
         int var10 = var8.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Object var29 = var8[var11];
            Object var30 = str(var29, var0, var1);
            if (var30 != Undefined.instance) {
               StringBuilder var31 = new StringBuilder();
               var31.append(quote(var29.toString()));
               var31.append(":");
               String var34 = var31.toString();
               if (var1.gap.length() > 0) {
                  StringBuilder var35 = new StringBuilder();
                  var35.append(var34);
                  var35.append(" ");
                  var34 = var35.toString();
               }

               StringBuilder var38 = new StringBuilder();
               var38.append(var34);
               var38.append(var30);
               var9.add(var38.toString());
            }
         }

         String var16;
         if (var9.isEmpty()) {
            var16 = "{}";
         } else if (var1.gap.length() == 0) {
            StringBuilder var12 = new StringBuilder();
            var12.append('{');
            var12.append(join(var9, ","));
            var12.append('}');
            var16 = var12.toString();
         } else {
            StringBuilder var18 = new StringBuilder();
            var18.append(",\n");
            var18.append(var1.indent);
            String var21 = join(var9, var18.toString());
            StringBuilder var22 = new StringBuilder();
            var22.append("{\n");
            var22.append(var1.indent);
            var22.append(var21);
            var22.append('\n');
            var22.append(var4);
            var22.append('}');
            var16 = var22.toString();
         }

         var1.stack.pop();
         var1.indent = var4;
         return var16;
      } else {
         EcmaError var2 = ScriptRuntime.typeError0("msg.cyclic.value");
         throw var2;
      }
   }

   private static String join(Collection var0, String var1) {
      if (var0 == null) {
         return "";
      } else if (var0.isEmpty()) {
         return "";
      } else {
         Iterator var2 = var0.iterator();
         if (!var2.hasNext()) {
            return "";
         } else {
            StringBuilder var3 = new StringBuilder(var2.next().toString());

            while(var2.hasNext()) {
               var3.append(var1);
               var3.append(var2.next().toString());
            }

            return var3.toString();
         }
      }
   }

   private static Object parse(Context var0, Scriptable var1, String var2) {
      try {
         Object var4 = (new JsonParser(var0, var1)).parseValue(var2);
         return var4;
      } catch (JsonParser.ParseException var5) {
         throw ScriptRuntime.constructError("SyntaxError", var5.getMessage());
      }
   }

   public static Object parse(Context var0, Scriptable var1, String var2, Callable var3) {
      Object var4 = parse(var0, var1, var2);
      Scriptable var5 = var0.newObject(var1);
      var5.put("", var5, var4);
      return walk(var0, var1, var3, var5, "");
   }

   private static String quote(String var0) {
      StringBuilder var1 = new StringBuilder(2 + var0.length());
      var1.append('"');
      int var3 = var0.length();

      for(int var4 = 0; var4 < var3; ++var4) {
         char var6 = var0.charAt(var4);
         if (var6 != '\f') {
            if (var6 != '\r') {
               if (var6 != '"') {
                  if (var6 != '\\') {
                     switch(var6) {
                     case '\b':
                        var1.append("\\b");
                        break;
                     case '\t':
                        var1.append("\\t");
                        break;
                     case '\n':
                        var1.append("\\n");
                        break;
                     default:
                        if (var6 < ' ') {
                           var1.append("\\u");
                           Object[] var16 = new Object[]{Integer.valueOf(var6)};
                           var1.append(String.format("%04x", var16));
                        } else {
                           var1.append(var6);
                        }
                     }
                  } else {
                     var1.append("\\\\");
                  }
               } else {
                  var1.append("\\\"");
               }
            } else {
               var1.append("\\r");
            }
         } else {
            var1.append("\\f");
         }
      }

      var1.append('"');
      return var1.toString();
   }

   private static String repeat(char var0, int var1) {
      char[] var2 = new char[var1];
      Arrays.fill(var2, var0);
      return new String(var2);
   }

   private static Object str(Object var0, Scriptable var1, NativeJSON.StringifyState var2) {
      Object var3;
      if (var0 instanceof String) {
         var3 = getProperty(var1, (String)var0);
      } else {
         var3 = getProperty(var1, ((Number)var0).intValue());
      }

      if (var3 instanceof Scriptable && hasProperty((Scriptable)var3, "toJSON") && getProperty((Scriptable)var3, "toJSON") instanceof Callable) {
         var3 = callMethod(var2.cx, (Scriptable)var3, "toJSON", new Object[]{var0});
      }

      if (var2.replacer != null) {
         var3 = var2.replacer.call(var2.cx, var2.scope, var1, new Object[]{var0, var3});
      }

      if (var3 instanceof NativeNumber) {
         var3 = ScriptRuntime.toNumber(var3);
      } else if (var3 instanceof NativeString) {
         var3 = ScriptRuntime.toString(var3);
      } else if (var3 instanceof NativeBoolean) {
         var3 = ((NativeBoolean)var3).getDefaultValue(ScriptRuntime.BooleanClass);
      }

      if (var3 == null) {
         return "null";
      } else if (var3.equals(Boolean.TRUE)) {
         return "true";
      } else if (var3.equals(Boolean.FALSE)) {
         return "false";
      } else if (var3 instanceof CharSequence) {
         return quote(var3.toString());
      } else if (var3 instanceof Number) {
         double var4 = ((Number)var3).doubleValue();
         return var4 == var4 && var4 != Double.POSITIVE_INFINITY && var4 != Double.NEGATIVE_INFINITY ? ScriptRuntime.toString(var3) : "null";
      } else if (var3 instanceof Scriptable && !(var3 instanceof Callable)) {
         return var3 instanceof NativeArray ? ja((NativeArray)var3, var2) : jo((Scriptable)var3, var2);
      } else {
         return Undefined.instance;
      }
   }

   public static Object stringify(Context var0, Scriptable var1, Object var2, Object var3, Object var4) {
      Object var5 = var4;
      String var6 = "";
      LinkedList var10;
      Callable var11;
      if (var3 instanceof Callable) {
         var11 = (Callable)var3;
         var10 = null;
      } else if (var3 instanceof NativeArray) {
         LinkedList var7 = new LinkedList();
         NativeArray var8 = (NativeArray)var3;
         Iterator var9 = var8.getIndexIds().iterator();

         label60:
         while(true) {
            Object var20;
            label58:
            do {
               while(var9.hasNext()) {
                  var20 = var8.get((Integer)var9.next(), var8);
                  if (!(var20 instanceof String) && !(var20 instanceof Number)) {
                     continue label58;
                  }

                  var7.add(var20);
               }

               var10 = var7;
               var11 = null;
               break label60;
            } while(!(var20 instanceof NativeString) && !(var20 instanceof NativeNumber));

            var7.add(ScriptRuntime.toString(var20));
         }
      } else {
         var10 = null;
         var11 = null;
      }

      if (var4 instanceof NativeNumber) {
         var5 = ScriptRuntime.toNumber(var4);
      } else if (var4 instanceof NativeString) {
         var5 = ScriptRuntime.toString(var4);
      }

      Object var12;
      String var13;
      label42: {
         if (var5 instanceof Number) {
            int var18 = Math.min(10, (int)ScriptRuntime.toInteger(var5));
            String var19;
            if (var18 > 0) {
               var19 = repeat(' ', var18);
            } else {
               var19 = "";
            }

            var6 = var19;
            var5 = var18;
         } else if (var5 instanceof String) {
            String var16 = (String)var5;
            if (var16.length() > 10) {
               String var17 = var16.substring(0, 10);
               var12 = var5;
               var13 = var17;
            } else {
               var12 = var5;
               var13 = var16;
            }
            break label42;
         }

         var12 = var5;
         var13 = var6;
      }

      NativeJSON.StringifyState var14 = new NativeJSON.StringifyState(var0, var1, "", var13, var11, var10, var12);
      NativeObject var15 = new NativeObject();
      var15.setParentScope(var1);
      var15.setPrototype(ScriptableObject.getObjectPrototype(var1));
      var15.defineProperty((String)"", (Object)var2, 0);
      return str("", var15, var14);
   }

   private static Object walk(Context var0, Scriptable var1, Callable var2, Scriptable var3, Object var4) {
      Object var5;
      if (var4 instanceof Number) {
         var5 = var3.get(((Number)var4).intValue(), var3);
      } else {
         var5 = var3.get((String)var4, var3);
      }

      if (var5 instanceof Scriptable) {
         Scriptable var6 = (Scriptable)var5;
         if (var6 instanceof NativeArray) {
            long var12 = ((NativeArray)var6).getLength();

            for(long var14 = 0L; var14 < var12; ++var14) {
               if (var14 > 2147483647L) {
                  String var18 = Long.toString(var14);
                  Object var19 = walk(var0, var1, var2, var6, var18);
                  if (var19 == Undefined.instance) {
                     var6.delete(var18);
                  } else {
                     var6.put(var18, var6, var19);
                  }
               } else {
                  int var16 = (int)var14;
                  Object var17 = walk(var0, var1, var2, var6, var16);
                  if (var17 == Undefined.instance) {
                     var6.delete(var16);
                  } else {
                     var6.put(var16, var6, var17);
                  }
               }
            }
         } else {
            Object[] var7 = var6.getIds();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Object var10 = var7[var9];
               Object var11 = walk(var0, var1, var2, var6, var10);
               if (var11 == Undefined.instance) {
                  if (var10 instanceof Number) {
                     var6.delete(((Number)var10).intValue());
                  } else {
                     var6.delete((String)var10);
                  }
               } else if (var10 instanceof Number) {
                  var6.put(((Number)var10).intValue(), var6, var11);
               } else {
                  var6.put((String)var10, var6, var11);
               }
            }
         }
      }

      return var2.call(var0, var1, var3, new Object[]{var4, var5});
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      if (!var1.hasTag(JSON_TAG)) {
         return super.execIdCall(var1, var2, var3, var4, var5);
      } else {
         int var6 = var1.methodId();
         if (var6 == 1) {
            return "JSON";
         } else if (var6 != 2) {
            if (var6 != 3) {
               throw new IllegalStateException(String.valueOf(var6));
            } else {
               int var10 = var5.length;
               Object var11 = null;
               Object var12 = null;
               Object var13;
               if (var10 != 1) {
                  var12 = null;
                  if (var10 != 2) {
                     if (var10 != 3) {
                        var13 = null;
                        var11 = null;
                        var12 = null;
                        return stringify(var2, var3, var13, var11, var12);
                     }

                     var12 = var5[2];
                  }

                  var11 = var5[1];
               }

               var13 = var5[0];
               return stringify(var2, var3, var13, var11, var12);
            }
         } else {
            String var7 = ScriptRuntime.toString(var5, 0);
            int var8 = var5.length;
            Object var9 = null;
            if (var8 > 1) {
               var9 = var5[1];
            }

            return var9 instanceof Callable ? parse(var2, var3, var7, (Callable)var9) : parse(var2, var3, var7);
         }
      }
   }

   protected int findPrototypeId(String var1) {
      int var2 = var1.length();
      String var3;
      byte var4;
      if (var2 != 5) {
         if (var2 != 8) {
            if (var2 != 9) {
               var4 = 0;
               var3 = null;
            } else {
               var3 = "stringify";
               var4 = 3;
            }
         } else {
            var3 = "toSource";
            var4 = 1;
         }
      } else {
         var3 = "parse";
         var4 = 2;
      }

      if (var3 != null && var3 != var1 && !var3.equals(var1)) {
         var4 = 0;
      }

      return var4;
   }

   public String getClassName() {
      return "JSON";
   }

   protected void initPrototypeId(int var1) {
      if (var1 <= 3) {
         byte var2;
         String var3;
         if (var1 != 1) {
            if (var1 != 2) {
               if (var1 != 3) {
                  throw new IllegalStateException(String.valueOf(var1));
               }

               var2 = 3;
               var3 = "stringify";
            } else {
               var2 = 2;
               var3 = "parse";
            }
         } else {
            var2 = 0;
            var3 = "toSource";
         }

         this.initPrototypeMethod(JSON_TAG, var1, var3, var2);
      } else {
         throw new IllegalStateException(String.valueOf(var1));
      }
   }

   private static class StringifyState {
      Context cx;
      String gap;
      String indent;
      List propertyList;
      Callable replacer;
      Scriptable scope;
      Object space;
      Stack stack = new Stack();

      StringifyState(Context var1, Scriptable var2, String var3, String var4, Callable var5, List var6, Object var7) {
         this.cx = var1;
         this.scope = var2;
         this.indent = var3;
         this.gap = var4;
         this.replacer = var5;
         this.propertyList = var6;
         this.space = var7;
      }
   }
}
