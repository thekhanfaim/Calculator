package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

public class NativeJavaObject implements Scriptable, SymbolScriptable, Wrapper, Serializable {
   private static final Object COERCED_INTERFACE_KEY = "Coerced Interface";
   static final byte CONVERSION_NONE = 99;
   static final byte CONVERSION_NONTRIVIAL = 0;
   static final byte CONVERSION_TRIVIAL = 1;
   private static final int JSTYPE_BOOLEAN = 2;
   private static final int JSTYPE_JAVA_ARRAY = 7;
   private static final int JSTYPE_JAVA_CLASS = 5;
   private static final int JSTYPE_JAVA_OBJECT = 6;
   private static final int JSTYPE_NULL = 1;
   private static final int JSTYPE_NUMBER = 3;
   private static final int JSTYPE_OBJECT = 8;
   private static final int JSTYPE_STRING = 4;
   private static final int JSTYPE_UNDEFINED = 0;
   private static Method adapter_readAdapterObject;
   private static Method adapter_writeAdapterObject;
   private static final long serialVersionUID = -6948590651130498591L;
   private transient Map fieldAndMethods;
   protected transient boolean isAdapter;
   protected transient Object javaObject;
   protected transient JavaMembers members;
   protected Scriptable parent;
   protected Scriptable prototype;
   protected transient Class staticType;

   static {
      Class[] var0 = new Class[2];
      Class var1 = Kit.classOrNull("org.mozilla.javascript.JavaAdapter");
      if (var1 != null) {
         try {
            var0[0] = ScriptRuntime.ObjectClass;
            var0[1] = Kit.classOrNull("java.io.ObjectOutputStream");
            adapter_writeAdapterObject = var1.getMethod("writeAdapterObject", var0);
            var0[0] = ScriptRuntime.ScriptableClass;
            var0[1] = Kit.classOrNull("java.io.ObjectInputStream");
            adapter_readAdapterObject = var1.getMethod("readAdapterObject", var0);
            return;
         } catch (NoSuchMethodException var3) {
            adapter_writeAdapterObject = null;
            adapter_readAdapterObject = null;
         }
      }

   }

   public NativeJavaObject() {
   }

   public NativeJavaObject(Scriptable var1, Object var2, Class var3) {
      this(var1, var2, var3, false);
   }

   public NativeJavaObject(Scriptable var1, Object var2, Class var3, boolean var4) {
      this.parent = var1;
      this.javaObject = var2;
      this.staticType = var3;
      this.isAdapter = var4;
      this.initMembers();
   }

   public static boolean canConvert(Object var0, Class var1) {
      return getConversionWeight(var0, var1) < 99;
   }

   private static Object coerceToNumber(Class var0, Object var1) {
      Class var2 = var1.getClass();
      if (var0 != Character.TYPE && var0 != ScriptRuntime.CharacterClass) {
         if (var0 != ScriptRuntime.ObjectClass && var0 != ScriptRuntime.DoubleClass && var0 != Double.TYPE) {
            if (var0 != ScriptRuntime.FloatClass && var0 != Float.TYPE) {
               if (var0 != ScriptRuntime.IntegerClass && var0 != Integer.TYPE) {
                  if (var0 != ScriptRuntime.LongClass && var0 != Long.TYPE) {
                     if (var0 != ScriptRuntime.ShortClass && var0 != Short.TYPE) {
                        if (var0 != ScriptRuntime.ByteClass && var0 != Byte.TYPE) {
                           return new Double(toDouble(var1));
                        } else {
                           return var2 == ScriptRuntime.ByteClass ? var1 : (byte)((int)toInteger(var1, ScriptRuntime.ByteClass, -128.0D, 127.0D));
                        }
                     } else {
                        return var2 == ScriptRuntime.ShortClass ? var1 : (short)((int)toInteger(var1, ScriptRuntime.ShortClass, -32768.0D, 32767.0D));
                     }
                  } else if (var2 == ScriptRuntime.LongClass) {
                     return var1;
                  } else {
                     double var9 = Double.longBitsToDouble(4890909195324358655L);
                     double var11 = Double.longBitsToDouble(-4332462841530417152L);
                     return toInteger(var1, ScriptRuntime.LongClass, var11, var9);
                  }
               } else {
                  return var2 == ScriptRuntime.IntegerClass ? var1 : (int)toInteger(var1, ScriptRuntime.IntegerClass, -2.147483648E9D, 2.147483647E9D);
               }
            } else if (var2 == ScriptRuntime.FloatClass) {
               return var1;
            } else {
               double var3 = toDouble(var1);
               if (!Double.isInfinite(var3) && !Double.isNaN(var3) && var3 != 0.0D) {
                  double var5 = Math.abs(var3);
                  if (var5 < 1.401298464324817E-45D) {
                     float var8;
                     if (var3 > 0.0D) {
                        var8 = 0.0F;
                     } else {
                        var8 = 0.0F;
                     }

                     return var8;
                  } else if (var5 > 3.4028234663852886E38D) {
                     float var7;
                     if (var3 > 0.0D) {
                        var7 = Float.POSITIVE_INFINITY;
                     } else {
                        var7 = Float.NEGATIVE_INFINITY;
                     }

                     return var7;
                  } else {
                     return (float)var3;
                  }
               } else {
                  return (float)var3;
               }
            }
         } else {
            return var2 == ScriptRuntime.DoubleClass ? var1 : toDouble(var1);
         }
      } else {
         return var2 == ScriptRuntime.CharacterClass ? var1 : (char)((int)toInteger(var1, ScriptRuntime.CharacterClass, 0.0D, 65535.0D));
      }
   }

   @Deprecated
   public static Object coerceType(Class var0, Object var1) {
      return coerceTypeImpl(var0, var1);
   }

   static Object coerceTypeImpl(Class var0, Object var1) {
      if (var1 != null && var1.getClass() == var0) {
         return var1;
      } else {
         switch(getJSTypeCode(var1)) {
         case 0:
            if (var0 != ScriptRuntime.StringClass) {
               if (var0 == ScriptRuntime.ObjectClass) {
                  return "undefined";
               }

               reportConversionError("undefined", var0);
               return var1;
            }

            return "undefined";
         case 1:
            if (var0.isPrimitive()) {
               reportConversionError(var1, var0);
            }

            return null;
         case 2:
            if (var0 != Boolean.TYPE && var0 != ScriptRuntime.BooleanClass) {
               if (var0 == ScriptRuntime.ObjectClass) {
                  return var1;
               }

               if (var0 == ScriptRuntime.StringClass) {
                  return var1.toString();
               }

               reportConversionError(var1, var0);
               return var1;
            }

            return var1;
         case 3:
            if (var0 == ScriptRuntime.StringClass) {
               return ScriptRuntime.toString(var1);
            } else if (var0 == ScriptRuntime.ObjectClass) {
               Context var2 = Context.getCurrentContext();
               return var2 != null && var2.hasFeature(18) && (double)Math.round(toDouble(var1)) == toDouble(var1) ? coerceToNumber(Long.TYPE, var1) : coerceToNumber(Double.TYPE, var1);
            } else if ((!var0.isPrimitive() || var0 == Boolean.TYPE) && !ScriptRuntime.NumberClass.isAssignableFrom(var0)) {
               reportConversionError(var1, var0);
               return var1;
            } else {
               return coerceToNumber(var0, var1);
            }
         case 4:
            if (var0 != ScriptRuntime.StringClass && !var0.isInstance(var1)) {
               if (var0 != Character.TYPE && var0 != ScriptRuntime.CharacterClass) {
                  if ((!var0.isPrimitive() || var0 == Boolean.TYPE) && !ScriptRuntime.NumberClass.isAssignableFrom(var0)) {
                     reportConversionError(var1, var0);
                     return var1;
                  }

                  return coerceToNumber(var0, var1);
               }

               if (((CharSequence)var1).length() == 1) {
                  return ((CharSequence)var1).charAt(0);
               }

               return coerceToNumber(var0, var1);
            }

            return var1.toString();
         case 5:
            if (var1 instanceof Wrapper) {
               var1 = ((Wrapper)var1).unwrap();
            }

            if (var0 != ScriptRuntime.ClassClass) {
               if (var0 == ScriptRuntime.ObjectClass) {
                  return var1;
               }

               if (var0 == ScriptRuntime.StringClass) {
                  return var1.toString();
               }

               reportConversionError(var1, var0);
               return var1;
            }

            return var1;
         case 6:
         case 7:
            if (var1 instanceof Wrapper) {
               var1 = ((Wrapper)var1).unwrap();
            }

            if (var0.isPrimitive()) {
               if (var0 == Boolean.TYPE) {
                  reportConversionError(var1, var0);
               }

               return coerceToNumber(var0, var1);
            } else if (var0 == ScriptRuntime.StringClass) {
               return var1.toString();
            } else {
               if (var0.isInstance(var1)) {
                  return var1;
               }

               reportConversionError(var1, var0);
               return var1;
            }
         case 8:
            if (var0 == ScriptRuntime.StringClass) {
               return ScriptRuntime.toString(var1);
            } else if (var0.isPrimitive()) {
               if (var0 == Boolean.TYPE) {
                  reportConversionError(var1, var0);
               }

               return coerceToNumber(var0, var1);
            } else if (var0.isInstance(var1)) {
               return var1;
            } else if (var0 == ScriptRuntime.DateClass && var1 instanceof NativeDate) {
               return new Date((long)((NativeDate)var1).getJSTimeValue());
            } else if (var0.isArray() && var1 instanceof NativeArray) {
               NativeArray var4 = (NativeArray)var1;
               long var5 = var4.getLength();
               Class var7 = var0.getComponentType();
               Object var8 = Array.newInstance(var7, (int)var5);

               for(int var9 = 0; (long)var9 < var5; ++var9) {
                  try {
                     Array.set(var8, var9, coerceTypeImpl(var7, var4.get(var9, var4)));
                  } catch (EvaluatorException var11) {
                     reportConversionError(var1, var0);
                  }
               }

               return var8;
            } else if (var1 instanceof Wrapper) {
               Object var3 = ((Wrapper)var1).unwrap();
               if (var0.isInstance(var3)) {
                  return var3;
               }

               reportConversionError(var3, var0);
               return var3;
            } else {
               if (!var0.isInterface() || !(var1 instanceof NativeObject) && !(var1 instanceof NativeFunction)) {
                  reportConversionError(var1, var0);
                  return var1;
               }

               return createInterfaceAdapter(var0, (ScriptableObject)var1);
            }
         default:
            return var1;
         }
      }
   }

   protected static Object createInterfaceAdapter(Class var0, ScriptableObject var1) {
      Object var2 = Kit.makeHashKeyFromPair(COERCED_INTERFACE_KEY, var0);
      Object var3 = var1.getAssociatedValue(var2);
      return var3 != null ? var3 : var1.associateValue(var2, InterfaceAdapter.create(Context.getContext(), var0, var1));
   }

   static int getConversionWeight(Object var0, Class var1) {
      int var2 = getJSTypeCode(var0);
      switch(var2) {
      case 0:
         if (var1 == ScriptRuntime.StringClass || var1 == ScriptRuntime.ObjectClass) {
            return 1;
         }
         break;
      case 1:
         if (!var1.isPrimitive()) {
            return 1;
         }
         break;
      case 2:
         if (var1 == Boolean.TYPE) {
            return 1;
         }

         if (var1 == ScriptRuntime.BooleanClass) {
            return 2;
         }

         if (var1 == ScriptRuntime.ObjectClass) {
            return 3;
         }

         if (var1 == ScriptRuntime.StringClass) {
            return 4;
         }
         break;
      case 3:
         if (var1.isPrimitive()) {
            if (var1 == Double.TYPE) {
               return 1;
            }

            if (var1 != Boolean.TYPE) {
               return 1 + getSizeRank(var1);
            }
         } else {
            if (var1 == ScriptRuntime.StringClass) {
               return 9;
            }

            if (var1 == ScriptRuntime.ObjectClass) {
               return 10;
            }

            if (ScriptRuntime.NumberClass.isAssignableFrom(var1)) {
               return 2;
            }
         }
         break;
      case 4:
         if (var1 == ScriptRuntime.StringClass) {
            return 1;
         }

         if (var1.isInstance(var0)) {
            return 2;
         }

         if (var1.isPrimitive()) {
            if (var1 == Character.TYPE) {
               return 3;
            }

            if (var1 != Boolean.TYPE) {
               return 4;
            }
         }
         break;
      case 5:
         if (var1 == ScriptRuntime.ClassClass) {
            return 1;
         }

         if (var1 == ScriptRuntime.ObjectClass) {
            return 3;
         }

         if (var1 == ScriptRuntime.StringClass) {
            return 4;
         }
         break;
      case 6:
      case 7:
         Object var3 = var0;
         if (var0 instanceof Wrapper) {
            var3 = ((Wrapper)var0).unwrap();
         }

         if (var1.isInstance(var3)) {
            return 0;
         }

         if (var1 == ScriptRuntime.StringClass) {
            return 2;
         }

         if (var1.isPrimitive() && var1 != Boolean.TYPE) {
            if (var2 == 7) {
               return 99;
            }

            return 2 + getSizeRank(var1);
         }
         break;
      case 8:
         if (var1 != ScriptRuntime.ObjectClass && var1.isInstance(var0)) {
            return 1;
         }

         if (var1.isArray()) {
            if (var0 instanceof NativeArray) {
               return 2;
            }
         } else {
            if (var1 == ScriptRuntime.ObjectClass) {
               return 3;
            }

            if (var1 == ScriptRuntime.StringClass) {
               return 4;
            }

            if (var1 == ScriptRuntime.DateClass) {
               if (var0 instanceof NativeDate) {
                  return 1;
               }
            } else {
               if (var1.isInterface()) {
                  if (var0 instanceof NativeFunction) {
                     return 1;
                  }

                  if (var0 instanceof NativeObject) {
                     return 2;
                  }

                  return 12;
               }

               if (var1.isPrimitive() && var1 != Boolean.TYPE) {
                  return 4 + getSizeRank(var1);
               }
            }
         }
         break;
      default:
         return 99;
      }

      return 99;
   }

   private static int getJSTypeCode(Object var0) {
      if (var0 == null) {
         return 1;
      } else if (var0 == Undefined.instance) {
         return 0;
      } else if (var0 instanceof CharSequence) {
         return 4;
      } else if (var0 instanceof Number) {
         return 3;
      } else if (var0 instanceof Boolean) {
         return 2;
      } else if (var0 instanceof Scriptable) {
         if (var0 instanceof NativeJavaClass) {
            return 5;
         } else if (var0 instanceof NativeJavaArray) {
            return 7;
         } else {
            return var0 instanceof Wrapper ? 6 : 8;
         }
      } else if (var0 instanceof Class) {
         return 5;
      } else {
         return var0.getClass().isArray() ? 7 : 6;
      }
   }

   static int getSizeRank(Class var0) {
      if (var0 == Double.TYPE) {
         return 1;
      } else if (var0 == Float.TYPE) {
         return 2;
      } else if (var0 == Long.TYPE) {
         return 3;
      } else if (var0 == Integer.TYPE) {
         return 4;
      } else if (var0 == Short.TYPE) {
         return 5;
      } else if (var0 == Character.TYPE) {
         return 6;
      } else if (var0 == Byte.TYPE) {
         return 7;
      } else {
         return var0 == Boolean.TYPE ? 99 : 8;
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      boolean var2 = var1.readBoolean();
      this.isAdapter = var2;
      if (var2) {
         Method var4 = adapter_readAdapterObject;
         if (var4 == null) {
            throw new ClassNotFoundException();
         }

         Object[] var5 = new Object[]{this, var1};

         try {
            this.javaObject = var4.invoke((Object)null, var5);
         } catch (Exception var7) {
            throw new IOException();
         }
      } else {
         this.javaObject = var1.readObject();
      }

      String var3 = (String)var1.readObject();
      if (var3 != null) {
         this.staticType = Class.forName(var3);
      } else {
         this.staticType = null;
      }

      this.initMembers();
   }

   static void reportConversionError(Object var0, Class var1) {
      throw Context.reportRuntimeError2("msg.conversion.not.allowed", String.valueOf(var0), JavaMembers.javaSignature(var1));
   }

   private static double toDouble(Object var0) {
      if (var0 instanceof Number) {
         return ((Number)var0).doubleValue();
      } else if (var0 instanceof String) {
         return ScriptRuntime.toNumber((String)var0);
      } else if (var0 instanceof Scriptable) {
         return var0 instanceof Wrapper ? toDouble(((Wrapper)var0).unwrap()) : ScriptRuntime.toNumber(var0);
      } else {
         Method var2;
         try {
            var2 = var0.getClass().getMethod("doubleValue", (Class[])null);
         } catch (NoSuchMethodException var10) {
            var2 = null;
         } catch (SecurityException var11) {
            var2 = null;
         }

         if (var2 != null) {
            try {
               double var5 = ((Number)var2.invoke(var0, (Object[])null)).doubleValue();
               return var5;
            } catch (IllegalAccessException var8) {
               reportConversionError(var0, Double.TYPE);
            } catch (InvocationTargetException var9) {
               reportConversionError(var0, Double.TYPE);
            }
         }

         return ScriptRuntime.toNumber(var0.toString());
      }
   }

   private static long toInteger(Object var0, Class var1, double var2, double var4) {
      double var6 = toDouble(var0);
      if (Double.isInfinite(var6) || Double.isNaN(var6)) {
         reportConversionError(ScriptRuntime.toString(var0), var1);
      }

      double var8;
      if (var6 > 0.0D) {
         var8 = Math.floor(var6);
      } else {
         var8 = Math.ceil(var6);
      }

      if (var8 < var2 || var8 > var4) {
         reportConversionError(ScriptRuntime.toString(var0), var1);
      }

      return (long)var8;
   }

   @Deprecated
   public static Object wrap(Scriptable var0, Object var1, Class var2) {
      Context var3 = Context.getContext();
      return var3.getWrapFactory().wrap(var3, var0, var1, var2);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeBoolean(this.isAdapter);
      if (this.isAdapter) {
         Method var3 = adapter_writeAdapterObject;
         if (var3 == null) {
            throw new IOException();
         }

         Object[] var4 = new Object[]{this.javaObject, var1};

         try {
            var3.invoke((Object)null, var4);
         } catch (Exception var6) {
            throw new IOException();
         }
      } else {
         var1.writeObject(this.javaObject);
      }

      Class var2 = this.staticType;
      if (var2 != null) {
         var1.writeObject(var2.getClass().getName());
      } else {
         var1.writeObject((Object)null);
      }
   }

   public void delete(int var1) {
   }

   public void delete(String var1) {
   }

   public void delete(Symbol var1) {
   }

   public Object get(int var1, Scriptable var2) {
      throw this.members.reportMemberNotFound(Integer.toString(var1));
   }

   public Object get(String var1, Scriptable var2) {
      Map var3 = this.fieldAndMethods;
      if (var3 != null) {
         Object var4 = var3.get(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return this.members.get(this, var1, this.javaObject, false);
   }

   public Object get(Symbol var1, Scriptable var2) {
      return Scriptable.NOT_FOUND;
   }

   public String getClassName() {
      return "JavaObject";
   }

   public Object getDefaultValue(Class var1) {
      if (var1 == null && this.javaObject instanceof Boolean) {
         var1 = ScriptRuntime.BooleanClass;
      }

      if (var1 != null && var1 != ScriptRuntime.StringClass) {
         String var2;
         if (var1 == ScriptRuntime.BooleanClass) {
            var2 = "booleanValue";
         } else {
            if (var1 != ScriptRuntime.NumberClass) {
               throw Context.reportRuntimeError0("msg.default.value");
            }

            var2 = "doubleValue";
         }

         Object var3 = this.get((String)var2, this);
         if (var3 instanceof Function) {
            Function var7 = (Function)var3;
            return var7.call(Context.getContext(), var7.getParentScope(), this, ScriptRuntime.emptyArgs);
         } else {
            if (var1 == ScriptRuntime.NumberClass) {
               Object var4 = this.javaObject;
               if (var4 instanceof Boolean) {
                  double var5;
                  if ((Boolean)var4) {
                     var5 = 1.0D;
                  } else {
                     var5 = 0.0D;
                  }

                  return ScriptRuntime.wrapNumber(var5);
               }
            }

            return this.javaObject.toString();
         }
      } else {
         return this.javaObject.toString();
      }
   }

   public Object[] getIds() {
      return this.members.getIds(false);
   }

   public Scriptable getParentScope() {
      return this.parent;
   }

   public Scriptable getPrototype() {
      Scriptable var1 = this.prototype;
      return var1 == null && this.javaObject instanceof String ? TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(this.parent), TopLevel.Builtins.String) : var1;
   }

   public boolean has(int var1, Scriptable var2) {
      return false;
   }

   public boolean has(String var1, Scriptable var2) {
      return this.members.has(var1, false);
   }

   public boolean has(Symbol var1, Scriptable var2) {
      return false;
   }

   public boolean hasInstance(Scriptable var1) {
      return false;
   }

   protected void initMembers() {
      Object var1 = this.javaObject;
      Class var2;
      if (var1 != null) {
         var2 = var1.getClass();
      } else {
         var2 = this.staticType;
      }

      JavaMembers var3 = JavaMembers.lookupClass(this.parent, var2, this.staticType, this.isAdapter);
      this.members = var3;
      this.fieldAndMethods = var3.getFieldAndMethodsObjects(this, this.javaObject, false);
   }

   public void put(int var1, Scriptable var2, Object var3) {
      throw this.members.reportMemberNotFound(Integer.toString(var1));
   }

   public void put(String var1, Scriptable var2, Object var3) {
      if (this.prototype != null && !this.members.has(var1, false)) {
         Scriptable var4 = this.prototype;
         var4.put(var1, var4, var3);
      } else {
         this.members.put(this, var1, this.javaObject, var3, false);
      }
   }

   public void put(Symbol var1, Scriptable var2, Object var3) {
      String var4 = var1.toString();
      if (this.prototype != null && !this.members.has(var4, false)) {
         Scriptable var5 = this.prototype;
         if (var5 instanceof SymbolScriptable) {
            ((SymbolScriptable)var5).put(var1, var5, var3);
            return;
         }
      } else {
         this.members.put(this, var4, this.javaObject, var3, false);
      }

   }

   public void setParentScope(Scriptable var1) {
      this.parent = var1;
   }

   public void setPrototype(Scriptable var1) {
      this.prototype = var1;
   }

   public Object unwrap() {
      return this.javaObject;
   }
}
