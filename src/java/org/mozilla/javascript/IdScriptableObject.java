package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class IdScriptableObject extends ScriptableObject implements IdFunctionCall {
   private transient IdScriptableObject.PrototypeValues prototypeValues;

   public IdScriptableObject() {
   }

   public IdScriptableObject(Scriptable var1, Scriptable var2) {
      super(var1, var2);
   }

   private ScriptableObject getBuiltInDescriptor(String var1) {
      Object var2 = this.getParentScope();
      if (var2 == null) {
         var2 = this;
      }

      int var3 = this.findInstanceIdInfo(var1);
      if (var3 != 0) {
         return buildDataDescriptor((Scriptable)var2, this.getInstanceIdValue('\uffff' & var3), var3 >>> 16);
      } else {
         IdScriptableObject.PrototypeValues var4 = this.prototypeValues;
         if (var4 != null) {
            int var5 = var4.findId(var1);
            if (var5 != 0) {
               return buildDataDescriptor((Scriptable)var2, this.prototypeValues.get(var5), this.prototypeValues.getAttributes(var5));
            }
         }

         return null;
      }
   }

   private ScriptableObject getBuiltInDescriptor(Symbol var1) {
      Object var2 = this.getParentScope();
      if (var2 == null) {
         var2 = this;
      }

      IdScriptableObject.PrototypeValues var3 = this.prototypeValues;
      if (var3 != null) {
         int var4 = var3.findId(var1);
         if (var4 != 0) {
            return buildDataDescriptor((Scriptable)var2, this.prototypeValues.get(var4), this.prototypeValues.getAttributes(var4));
         }
      }

      return null;
   }

   protected static EcmaError incompatibleCallError(IdFunctionObject var0) {
      throw ScriptRuntime.typeError1("msg.incompat.call", var0.getFunctionName());
   }

   protected static int instanceIdInfo(int var0, int var1) {
      return var1 | var0 << 16;
   }

   private IdFunctionObject newIdFunction(Object var1, int var2, String var3, int var4, Scriptable var5) {
      Object var7;
      if (Context.getContext().getLanguageVersion() < 200) {
         IdFunctionObject var6 = new IdFunctionObject(this, var1, var2, var3, var4, var5);
         var7 = var6;
      } else {
         IdFunctionObjectES6 var8 = new IdFunctionObjectES6(this, var1, var2, var3, var4, var5);
         var7 = var8;
      }

      if (this.isSealed()) {
         ((IdFunctionObject)var7).sealObject();
      }

      return (IdFunctionObject)var7;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      if (var2 != 0) {
         this.activatePrototypeMap(var2);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      IdScriptableObject.PrototypeValues var2 = this.prototypeValues;
      int var3 = 0;
      if (var2 != null) {
         var3 = var2.getMaxId();
      }

      var1.writeInt(var3);
   }

   public final void activatePrototypeMap(int var1) {
      IdScriptableObject.PrototypeValues var2 = new IdScriptableObject.PrototypeValues(this, var1);
      synchronized(this){}

      Throwable var10000;
      boolean var10001;
      label122: {
         try {
            if (this.prototypeValues == null) {
               this.prototypeValues = var2;
               return;
            }
         } catch (Throwable var16) {
            var10000 = var16;
            var10001 = false;
            break label122;
         }

         label116:
         try {
            throw new IllegalStateException();
         } catch (Throwable var15) {
            var10000 = var15;
            var10001 = false;
            break label116;
         }
      }

      while(true) {
         Throwable var3 = var10000;

         try {
            throw var3;
         } catch (Throwable var14) {
            var10000 = var14;
            var10001 = false;
            continue;
         }
      }
   }

   protected void addIdFunctionProperty(Scriptable var1, Object var2, int var3, String var4, int var5) {
      this.newIdFunction(var2, var3, var4, var5, ScriptableObject.getTopLevelScope(var1)).addAsProperty(var1);
   }

   protected final Object defaultGet(String var1) {
      return super.get((String)var1, this);
   }

   protected final boolean defaultHas(String var1) {
      return super.has((String)var1, this);
   }

   protected final void defaultPut(String var1, Object var2) {
      super.put((String)var1, this, var2);
   }

   public void defineOwnProperty(Context var1, Object var2, ScriptableObject var3) {
      if (var2 instanceof String) {
         String var4 = (String)var2;
         int var5 = this.findInstanceIdInfo(var4);
         if (var5 != 0) {
            int var10 = '\uffff' & var5;
            if (!this.isAccessorDescriptor(var3)) {
               this.checkPropertyDefinition(var3);
               this.checkPropertyChange(var4, this.getOwnPropertyDescriptor(var1, var2), var3);
               int var11 = var5 >>> 16;
               Object var12 = getProperty(var3, "value");
               if (var12 != NOT_FOUND && (var11 & 1) == 0 && !this.sameValue(var12, this.getInstanceIdValue(var10))) {
                  this.setInstanceIdValue(var10, var12);
               }

               this.setAttributes(var4, this.applyDescriptorToAttributeBitset(var11, var3));
               return;
            }

            this.delete(var10);
         }

         IdScriptableObject.PrototypeValues var6 = this.prototypeValues;
         if (var6 != null) {
            int var7 = var6.findId(var4);
            if (var7 != 0) {
               if (!this.isAccessorDescriptor(var3)) {
                  this.checkPropertyDefinition(var3);
                  this.checkPropertyChange(var4, this.getOwnPropertyDescriptor(var1, var2), var3);
                  int var8 = this.prototypeValues.getAttributes(var7);
                  Object var9 = getProperty(var3, "value");
                  if (var9 != NOT_FOUND && (var8 & 1) == 0 && !this.sameValue(var9, this.prototypeValues.get(var7))) {
                     this.prototypeValues.set(var7, this, var9);
                  }

                  this.prototypeValues.setAttributes(var7, this.applyDescriptorToAttributeBitset(var8, var3));
                  if (super.has((String)var4, this)) {
                     super.delete(var4);
                  }

                  return;
               }

               this.prototypeValues.delete(var7);
            }
         }
      }

      super.defineOwnProperty(var1, var2, var3);
   }

   public void delete(String var1) {
      int var2 = this.findInstanceIdInfo(var1);
      if (var2 != 0 && !this.isSealed()) {
         if ((4 & var2 >>> 16) != 0) {
            if (Context.getContext().isStrictMode()) {
               throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", var1);
            }
         } else {
            this.setInstanceIdValue('\uffff' & var2, NOT_FOUND);
         }
      } else {
         IdScriptableObject.PrototypeValues var3 = this.prototypeValues;
         if (var3 != null) {
            int var4 = var3.findId(var1);
            if (var4 != 0) {
               if (!this.isSealed()) {
                  this.prototypeValues.delete(var4);
               }

               return;
            }
         }

         super.delete(var1);
      }
   }

   public void delete(Symbol var1) {
      int var2 = this.findInstanceIdInfo(var1);
      if (var2 != 0 && !this.isSealed()) {
         if ((4 & var2 >>> 16) != 0) {
            if (Context.getContext().isStrictMode()) {
               throw ScriptRuntime.typeError0("msg.delete.property.with.configurable.false");
            }
         } else {
            this.setInstanceIdValue('\uffff' & var2, NOT_FOUND);
         }
      } else {
         IdScriptableObject.PrototypeValues var3 = this.prototypeValues;
         if (var3 != null) {
            int var4 = var3.findId(var1);
            if (var4 != 0) {
               if (!this.isSealed()) {
                  this.prototypeValues.delete(var4);
               }

               return;
            }
         }

         super.delete(var1);
      }
   }

   public Object execIdCall(IdFunctionObject var1, Context var2, Scriptable var3, Scriptable var4, Object[] var5) {
      throw var1.unknown();
   }

   public final IdFunctionObject exportAsJSClass(int var1, Scriptable var2, boolean var3) {
      if (var2 != this && var2 != null) {
         this.setParentScope(var2);
         this.setPrototype(getObjectPrototype(var2));
      }

      this.activatePrototypeMap(var1);
      IdFunctionObject var4 = this.prototypeValues.createPrecachedConstructor();
      if (var3) {
         this.sealObject();
      }

      this.fillConstructorProperties(var4);
      if (var3) {
         var4.sealObject();
      }

      var4.exportAsScopeProperty();
      return var4;
   }

   protected void fillConstructorProperties(IdFunctionObject var1) {
   }

   protected int findInstanceIdInfo(String var1) {
      return 0;
   }

   protected int findInstanceIdInfo(Symbol var1) {
      return 0;
   }

   protected int findPrototypeId(String var1) {
      throw new IllegalStateException(var1);
   }

   protected int findPrototypeId(Symbol var1) {
      return 0;
   }

   public Object get(String var1, Scriptable var2) {
      Object var3 = super.get(var1, var2);
      if (var3 != NOT_FOUND) {
         return var3;
      } else {
         int var4 = this.findInstanceIdInfo(var1);
         if (var4 != 0) {
            Object var8 = this.getInstanceIdValue('\uffff' & var4);
            if (var8 != NOT_FOUND) {
               return var8;
            }
         }

         IdScriptableObject.PrototypeValues var5 = this.prototypeValues;
         if (var5 != null) {
            int var6 = var5.findId(var1);
            if (var6 != 0) {
               Object var7 = this.prototypeValues.get(var6);
               if (var7 != NOT_FOUND) {
                  return var7;
               }
            }
         }

         return NOT_FOUND;
      }
   }

   public Object get(Symbol var1, Scriptable var2) {
      Object var3 = super.get(var1, var2);
      if (var3 != NOT_FOUND) {
         return var3;
      } else {
         int var4 = this.findInstanceIdInfo(var1);
         if (var4 != 0) {
            Object var8 = this.getInstanceIdValue('\uffff' & var4);
            if (var8 != NOT_FOUND) {
               return var8;
            }
         }

         IdScriptableObject.PrototypeValues var5 = this.prototypeValues;
         if (var5 != null) {
            int var6 = var5.findId(var1);
            if (var6 != 0) {
               Object var7 = this.prototypeValues.get(var6);
               if (var7 != NOT_FOUND) {
                  return var7;
               }
            }
         }

         return NOT_FOUND;
      }
   }

   public int getAttributes(String var1) {
      int var2 = this.findInstanceIdInfo(var1);
      if (var2 != 0) {
         return var2 >>> 16;
      } else {
         IdScriptableObject.PrototypeValues var3 = this.prototypeValues;
         if (var3 != null) {
            int var4 = var3.findId(var1);
            if (var4 != 0) {
               return this.prototypeValues.getAttributes(var4);
            }
         }

         return super.getAttributes(var1);
      }
   }

   public int getAttributes(Symbol var1) {
      int var2 = this.findInstanceIdInfo(var1);
      if (var2 != 0) {
         return var2 >>> 16;
      } else {
         IdScriptableObject.PrototypeValues var3 = this.prototypeValues;
         if (var3 != null) {
            int var4 = var3.findId(var1);
            if (var4 != 0) {
               return this.prototypeValues.getAttributes(var4);
            }
         }

         return super.getAttributes(var1);
      }
   }

   Object[] getIds(boolean var1, boolean var2) {
      Object[] var3 = super.getIds(var1, var2);
      IdScriptableObject.PrototypeValues var4 = this.prototypeValues;
      if (var4 != null) {
         var3 = var4.getNames(var1, var2, var3);
      }

      int var5 = this.getMaxInstanceId();
      if (var5 != 0) {
         Object[] var6 = null;
         int var7 = 0;

         for(int var8 = var5; var8 != 0; --var8) {
            String var10 = this.getInstanceIdName(var8);
            int var11 = this.findInstanceIdInfo(var10);
            if (var11 != 0) {
               int var12 = var11 >>> 16;
               if (((var12 & 4) != 0 || NOT_FOUND != this.getInstanceIdValue(var8)) && (var1 || (var12 & 2) == 0)) {
                  if (var7 == 0) {
                     var6 = new Object[var8];
                  }

                  int var13 = var7 + 1;
                  var6[var7] = var10;
                  var7 = var13;
               }
            }
         }

         if (var7 != 0) {
            if (var3.length == 0 && var6.length == var7) {
               return var6;
            }

            Object[] var9 = new Object[var7 + var3.length];
            System.arraycopy(var3, 0, var9, 0, var3.length);
            System.arraycopy(var6, 0, var9, var3.length, var7);
            var3 = var9;
         }
      }

      return var3;
   }

   protected String getInstanceIdName(int var1) {
      throw new IllegalArgumentException(String.valueOf(var1));
   }

   protected Object getInstanceIdValue(int var1) {
      throw new IllegalStateException(String.valueOf(var1));
   }

   protected int getMaxInstanceId() {
      return 0;
   }

   protected ScriptableObject getOwnPropertyDescriptor(Context var1, Object var2) {
      ScriptableObject var3 = super.getOwnPropertyDescriptor(var1, var2);
      if (var3 == null) {
         if (var2 instanceof String) {
            return this.getBuiltInDescriptor((String)var2);
         }

         if (ScriptRuntime.isSymbol(var2)) {
            var3 = this.getBuiltInDescriptor((Symbol)((NativeSymbol)var2).getKey());
         }
      }

      return var3;
   }

   public boolean has(String var1, Scriptable var2) {
      int var3 = this.findInstanceIdInfo(var1);
      if (var3 != 0) {
         if ((4 & var3 >>> 16) != 0) {
            return true;
         } else {
            int var6 = '\uffff' & var3;
            return NOT_FOUND != this.getInstanceIdValue(var6);
         }
      } else {
         IdScriptableObject.PrototypeValues var4 = this.prototypeValues;
         if (var4 != null) {
            int var5 = var4.findId(var1);
            if (var5 != 0) {
               return this.prototypeValues.has(var5);
            }
         }

         return super.has(var1, var2);
      }
   }

   public boolean has(Symbol var1, Scriptable var2) {
      int var3 = this.findInstanceIdInfo(var1);
      if (var3 != 0) {
         if ((4 & var3 >>> 16) != 0) {
            return true;
         } else {
            int var6 = '\uffff' & var3;
            return NOT_FOUND != this.getInstanceIdValue(var6);
         }
      } else {
         IdScriptableObject.PrototypeValues var4 = this.prototypeValues;
         if (var4 != null) {
            int var5 = var4.findId(var1);
            if (var5 != 0) {
               return this.prototypeValues.has(var5);
            }
         }

         return super.has(var1, var2);
      }
   }

   public final boolean hasPrototypeMap() {
      return this.prototypeValues != null;
   }

   public final void initPrototypeConstructor(IdFunctionObject var1) {
      int var2 = this.prototypeValues.constructorId;
      if (var2 != 0) {
         if (var1.methodId() == var2) {
            if (this.isSealed()) {
               var1.sealObject();
            }

            this.prototypeValues.initValue(var2, (String)"constructor", var1, 2);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   protected void initPrototypeId(int var1) {
      throw new IllegalStateException(String.valueOf(var1));
   }

   public final IdFunctionObject initPrototypeMethod(Object var1, int var2, String var3, int var4) {
      return this.initPrototypeMethod(var1, var2, var3, var3, var4);
   }

   public final IdFunctionObject initPrototypeMethod(Object var1, int var2, String var3, String var4, int var5) {
      Scriptable var6 = ScriptableObject.getTopLevelScope(this);
      String var7;
      if (var4 != null) {
         var7 = var4;
      } else {
         var7 = var3;
      }

      IdFunctionObject var8 = this.newIdFunction(var1, var2, var7, var5, var6);
      this.prototypeValues.initValue(var2, (String)var3, var8, 2);
      return var8;
   }

   public final IdFunctionObject initPrototypeMethod(Object var1, int var2, Symbol var3, String var4, int var5) {
      IdFunctionObject var6 = this.newIdFunction(var1, var2, var4, var5, ScriptableObject.getTopLevelScope(this));
      this.prototypeValues.initValue(var2, (Symbol)var3, var6, 2);
      return var6;
   }

   public final void initPrototypeValue(int var1, String var2, Object var3, int var4) {
      this.prototypeValues.initValue(var1, var2, var3, var4);
   }

   public final void initPrototypeValue(int var1, Symbol var2, Object var3, int var4) {
      this.prototypeValues.initValue(var1, var2, var3, var4);
   }

   public void put(String var1, Scriptable var2, Object var3) {
      int var4 = this.findInstanceIdInfo(var1);
      if (var4 != 0) {
         if (var2 == this && this.isSealed()) {
            throw Context.reportRuntimeError1("msg.modify.sealed", var1);
         } else {
            if ((1 & var4 >>> 16) == 0) {
               if (var2 == this) {
                  this.setInstanceIdValue('\uffff' & var4, var3);
                  return;
               }

               var2.put(var1, var2, var3);
            }

         }
      } else {
         IdScriptableObject.PrototypeValues var5 = this.prototypeValues;
         if (var5 != null) {
            int var6 = var5.findId(var1);
            if (var6 != 0) {
               if (var2 == this && this.isSealed()) {
                  throw Context.reportRuntimeError1("msg.modify.sealed", var1);
               }

               this.prototypeValues.set(var6, var2, var3);
               return;
            }
         }

         super.put(var1, var2, var3);
      }
   }

   public void put(Symbol var1, Scriptable var2, Object var3) {
      int var4 = this.findInstanceIdInfo(var1);
      if (var4 != 0) {
         if (var2 == this && this.isSealed()) {
            throw Context.reportRuntimeError0("msg.modify.sealed");
         } else {
            if ((1 & var4 >>> 16) == 0) {
               if (var2 == this) {
                  this.setInstanceIdValue('\uffff' & var4, var3);
                  return;
               }

               ensureSymbolScriptable(var2).put(var1, var2, var3);
            }

         }
      } else {
         IdScriptableObject.PrototypeValues var5 = this.prototypeValues;
         if (var5 != null) {
            int var6 = var5.findId(var1);
            if (var6 != 0) {
               if (var2 == this && this.isSealed()) {
                  throw Context.reportRuntimeError0("msg.modify.sealed");
               }

               this.prototypeValues.set(var6, var2, var3);
               return;
            }
         }

         super.put(var1, var2, var3);
      }
   }

   public void setAttributes(String var1, int var2) {
      ScriptableObject.checkValidAttributes(var2);
      int var3 = this.findInstanceIdInfo(var1);
      if (var3 != 0) {
         int var6 = '\uffff' & var3;
         if (var2 != var3 >>> 16) {
            this.setInstanceIdAttributes(var6, var2);
         }

      } else {
         IdScriptableObject.PrototypeValues var4 = this.prototypeValues;
         if (var4 != null) {
            int var5 = var4.findId(var1);
            if (var5 != 0) {
               this.prototypeValues.setAttributes(var5, var2);
               return;
            }
         }

         super.setAttributes(var1, var2);
      }
   }

   protected void setInstanceIdAttributes(int var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("Changing attributes not supported for ");
      var3.append(this.getClassName());
      var3.append(" ");
      var3.append(this.getInstanceIdName(var1));
      var3.append(" property");
      throw ScriptRuntime.constructError("InternalError", var3.toString());
   }

   protected void setInstanceIdValue(int var1, Object var2) {
      throw new IllegalStateException(String.valueOf(var1));
   }

   private static final class PrototypeValues implements Serializable {
      private static final int NAME_SLOT = 1;
      private static final int SLOT_SPAN = 2;
      private static final long serialVersionUID = 3038645279153854371L;
      private short[] attributeArray;
      private IdFunctionObject constructor;
      private short constructorAttrs;
      int constructorId;
      private int maxId;
      private IdScriptableObject obj;
      private Object[] valueArray;

      PrototypeValues(IdScriptableObject var1, int var2) {
         if (var1 != null) {
            if (var2 >= 1) {
               this.obj = var1;
               this.maxId = var2;
            } else {
               throw new IllegalArgumentException();
            }
         } else {
            throw new IllegalArgumentException();
         }
      }

      private Object ensureId(int var1) {
         Object[] var2 = this.valueArray;
         if (var2 == null) {
            label282: {
               synchronized(this){}

               Throwable var10000;
               boolean var10001;
               label280: {
                  try {
                     var2 = this.valueArray;
                  } catch (Throwable var33) {
                     var10000 = var33;
                     var10001 = false;
                     break label280;
                  }

                  if (var2 == null) {
                     try {
                        int var12 = this.maxId;
                        var2 = new Object[var12 * 2];
                        this.valueArray = var2;
                        this.attributeArray = new short[var12];
                     } catch (Throwable var32) {
                        var10000 = var32;
                        var10001 = false;
                        break label280;
                     }
                  }

                  label266:
                  try {
                     break label282;
                  } catch (Throwable var31) {
                     var10000 = var31;
                     var10001 = false;
                     break label266;
                  }
               }

               while(true) {
                  Throwable var11 = var10000;

                  try {
                     throw var11;
                  } catch (Throwable var30) {
                     var10000 = var30;
                     var10001 = false;
                     continue;
                  }
               }
            }
         }

         int var3 = 2 * (var1 - 1);
         Object var4 = var2[var3];
         if (var4 == null) {
            int var5 = this.constructorId;
            if (var1 == var5) {
               this.initSlot(var5, "constructor", this.constructor, this.constructorAttrs);
               this.constructor = null;
            } else {
               this.obj.initPrototypeId(var1);
            }

            Object var6 = var2[var3];
            if (var6 != null) {
               return var6;
            } else {
               StringBuilder var7 = new StringBuilder();
               var7.append(this.obj.getClass().getName());
               var7.append(".initPrototypeId(int id) did not initialize id=");
               var7.append(var1);
               throw new IllegalStateException(var7.toString());
            }
         } else {
            return var4;
         }
      }

      private void initSlot(int var1, Object var2, Object var3, int var4) {
         Object[] var5 = this.valueArray;
         if (var5 != null) {
            Object var6;
            if (var3 == null) {
               var6 = UniqueTag.NULL_VALUE;
            } else {
               var6 = var3;
            }

            int var7 = 2 * (var1 - 1);
            synchronized(this){}

            Throwable var10000;
            boolean var10001;
            label318: {
               label326: {
                  try {
                     if (var5[var7] == null) {
                        var5[var7] = var6;
                        var5[var7 + 1] = var2;
                        this.attributeArray[var1 - 1] = (short)var4;
                        break label326;
                     }
                  } catch (Throwable var39) {
                     var10000 = var39;
                     var10001 = false;
                     break label318;
                  }

                  try {
                     if (var2.equals(var5[var7 + 1])) {
                        break label326;
                     }
                  } catch (Throwable var38) {
                     var10000 = var38;
                     var10001 = false;
                     break label318;
                  }

                  try {
                     throw new IllegalStateException();
                  } catch (Throwable var37) {
                     var10000 = var37;
                     var10001 = false;
                     break label318;
                  }
               }

               label304:
               try {
                  return;
               } catch (Throwable var36) {
                  var10000 = var36;
                  var10001 = false;
                  break label304;
               }
            }

            while(true) {
               Throwable var8 = var10000;

               try {
                  throw var8;
               } catch (Throwable var35) {
                  var10000 = var35;
                  var10001 = false;
                  continue;
               }
            }
         } else {
            throw new IllegalStateException();
         }
      }

      final IdFunctionObject createPrecachedConstructor() {
         if (this.constructorId == 0) {
            int var1 = this.obj.findPrototypeId("constructor");
            this.constructorId = var1;
            if (var1 != 0) {
               this.obj.initPrototypeId(var1);
               IdFunctionObject var2 = this.constructor;
               if (var2 != null) {
                  var2.initFunction(this.obj.getClassName(), ScriptableObject.getTopLevelScope(this.obj));
                  this.constructor.markAsConstructor(this.obj);
                  return this.constructor;
               } else {
                  StringBuilder var3 = new StringBuilder();
                  var3.append(this.obj.getClass().getName());
                  var3.append(".initPrototypeId() did not initialize id=");
                  var3.append(this.constructorId);
                  throw new IllegalStateException(var3.toString());
               }
            } else {
               throw new IllegalStateException("No id for constructor property");
            }
         } else {
            throw new IllegalStateException();
         }
      }

      final void delete(int param1) {
         // $FF: Couldn't be decompiled
      }

      final int findId(String var1) {
         return this.obj.findPrototypeId(var1);
      }

      final int findId(Symbol var1) {
         return this.obj.findPrototypeId(var1);
      }

      final Object get(int var1) {
         Object var2 = this.ensureId(var1);
         if (var2 == UniqueTag.NULL_VALUE) {
            var2 = null;
         }

         return var2;
      }

      final int getAttributes(int var1) {
         this.ensureId(var1);
         return this.attributeArray[var1 - 1];
      }

      final int getMaxId() {
         return this.maxId;
      }

      final Object[] getNames(boolean var1, boolean var2, Object[] var3) {
         Object[] var4 = null;
         int var5 = 0;

         for(int var6 = 1; var6 <= this.maxId; ++var6) {
            Object var10 = this.ensureId(var6);
            if ((var1 || (2 & this.attributeArray[var6 - 1]) == 0) && var10 != Scriptable.NOT_FOUND) {
               int var11 = 1 + 2 * (var6 - 1);
               Object var12 = this.valueArray[var11];
               if (var12 instanceof String) {
                  if (var4 == null) {
                     var4 = new Object[this.maxId];
                  }

                  int var14 = var5 + 1;
                  var4[var5] = var12;
                  var5 = var14;
               } else if (var2 && var12 instanceof Symbol) {
                  if (var4 == null) {
                     var4 = new Object[this.maxId];
                  }

                  int var13 = var5 + 1;
                  var4[var5] = var12.toString();
                  var5 = var13;
               }
            }
         }

         if (var5 == 0) {
            return var3;
         } else if (var3 != null && var3.length != 0) {
            int var8 = var3.length;
            Object[] var9 = new Object[var8 + var5];
            System.arraycopy(var3, 0, var9, 0, var8);
            System.arraycopy(var4, 0, var9, var8, var5);
            return var9;
         } else {
            if (var5 != var4.length) {
               Object[] var7 = new Object[var5];
               System.arraycopy(var4, 0, var7, 0, var5);
               var4 = var7;
            }

            return var4;
         }
      }

      final boolean has(int var1) {
         Object[] var2 = this.valueArray;
         if (var2 == null) {
            return true;
         } else {
            Object var3 = var2[2 * (var1 - 1)];
            if (var3 == null) {
               return true;
            } else {
               return var3 != Scriptable.NOT_FOUND;
            }
         }
      }

      final void initValue(int var1, String var2, Object var3, int var4) {
         if (1 <= var1 && var1 <= this.maxId) {
            if (var2 != null) {
               if (var3 != Scriptable.NOT_FOUND) {
                  ScriptableObject.checkValidAttributes(var4);
                  if (this.obj.findPrototypeId(var2) == var1) {
                     if (var1 == this.constructorId) {
                        if (var3 instanceof IdFunctionObject) {
                           this.constructor = (IdFunctionObject)var3;
                           this.constructorAttrs = (short)var4;
                        } else {
                           throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                        }
                     } else {
                        this.initSlot(var1, var2, var3, var4);
                     }
                  } else {
                     throw new IllegalArgumentException(var2);
                  }
               } else {
                  throw new IllegalArgumentException();
               }
            } else {
               throw new IllegalArgumentException();
            }
         } else {
            throw new IllegalArgumentException();
         }
      }

      final void initValue(int var1, Symbol var2, Object var3, int var4) {
         if (1 <= var1 && var1 <= this.maxId) {
            if (var2 != null) {
               if (var3 != Scriptable.NOT_FOUND) {
                  ScriptableObject.checkValidAttributes(var4);
                  if (this.obj.findPrototypeId(var2) == var1) {
                     if (var1 == this.constructorId) {
                        if (var3 instanceof IdFunctionObject) {
                           this.constructor = (IdFunctionObject)var3;
                           this.constructorAttrs = (short)var4;
                        } else {
                           throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                        }
                     } else {
                        this.initSlot(var1, var2, var3, var4);
                     }
                  } else {
                     throw new IllegalArgumentException(var2.toString());
                  }
               } else {
                  throw new IllegalArgumentException();
               }
            } else {
               throw new IllegalArgumentException();
            }
         } else {
            throw new IllegalArgumentException();
         }
      }

      final void set(int param1, Scriptable param2, Object param3) {
         // $FF: Couldn't be decompiled
      }

      final void setAttributes(int param1, int param2) {
         // $FF: Couldn't be decompiled
      }
   }
}
