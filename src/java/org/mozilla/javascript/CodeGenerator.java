package org.mozilla.javascript;

import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.ScriptNode;

class CodeGenerator extends Icode {
   private static final int ECF_TAIL = 1;
   private static final int MIN_FIXUP_TABLE_SIZE = 40;
   private static final int MIN_LABEL_TABLE_SIZE = 32;
   private CompilerEnvirons compilerEnv;
   private int doubleTableTop;
   private int exceptionTableTop;
   private long[] fixupTable;
   private int fixupTableTop;
   private int iCodeTop;
   private InterpreterData itsData;
   private boolean itsInFunctionFlag;
   private boolean itsInTryFlag;
   private int[] labelTable;
   private int labelTableTop;
   private int lineNumber;
   private ObjArray literalIds = new ObjArray();
   private int localTop;
   private ScriptNode scriptOrFn;
   private int stackDepth;
   private ObjToIntMap strings = new ObjToIntMap(20);

   private void addBackwardGoto(int var1, int var2) {
      int var3 = this.iCodeTop;
      if (var3 > var2) {
         this.addGotoOp(var1);
         this.resolveGoto(var3, var2);
      } else {
         throw Kit.codeBug();
      }
   }

   private void addExceptionHandler(int var1, int var2, int var3, boolean var4, int var5, int var6) {
      int var7 = this.exceptionTableTop;
      int[] var8 = this.itsData.itsExceptionTable;
      if (var8 == null) {
         if (var7 != 0) {
            Kit.codeBug();
         }

         var8 = new int[12];
         this.itsData.itsExceptionTable = var8;
      } else if (var8.length == var7) {
         var8 = new int[2 * var8.length];
         System.arraycopy(this.itsData.itsExceptionTable, 0, var8, 0, var7);
         this.itsData.itsExceptionTable = var8;
      }

      var8[var7 + 0] = var1;
      var8[var7 + 1] = var2;
      var8[var7 + 2] = var3;
      var8[var7 + 3] = var4;
      var8[var7 + 4] = var5;
      var8[var7 + 5] = var6;
      this.exceptionTableTop = var7 + 6;
   }

   private void addGoto(Node var1, int var2) {
      int var3 = this.getTargetLabel(var1);
      if (var3 >= this.labelTableTop) {
         Kit.codeBug();
      }

      int var4 = this.labelTable[var3];
      if (var4 != -1) {
         this.addBackwardGoto(var2, var4);
      } else {
         int var5 = this.iCodeTop;
         this.addGotoOp(var2);
         int var6 = this.fixupTableTop;
         long[] var7 = this.fixupTable;
         if (var7 == null || var6 == var7.length) {
            if (var7 == null) {
               this.fixupTable = new long[40];
            } else {
               long[] var8 = new long[2 * var7.length];
               System.arraycopy(var7, 0, var8, 0, var6);
               this.fixupTable = var8;
            }
         }

         this.fixupTableTop = var6 + 1;
         this.fixupTable[var6] = (long)var3 << 32 | (long)var5;
      }
   }

   private void addGotoOp(int var1) {
      byte[] var2 = this.itsData.itsICode;
      int var3 = this.iCodeTop;
      if (var3 + 3 > var2.length) {
         var2 = this.increaseICodeCapacity(3);
      }

      var2[var3] = (byte)var1;
      this.iCodeTop = 2 + var3 + 1;
   }

   private void addIcode(int var1) {
      if (Icode.validIcode(var1)) {
         this.addUint8(var1 & 255);
      } else {
         throw Kit.codeBug();
      }
   }

   private void addIndexOp(int var1, int var2) {
      this.addIndexPrefix(var2);
      if (Icode.validIcode(var1)) {
         this.addIcode(var1);
      } else {
         this.addToken(var1);
      }
   }

   private void addIndexPrefix(int var1) {
      if (var1 < 0) {
         Kit.codeBug();
      }

      if (var1 < 6) {
         this.addIcode(-32 - var1);
      } else if (var1 <= 255) {
         this.addIcode(-38);
         this.addUint8(var1);
      } else if (var1 <= 65535) {
         this.addIcode(-39);
         this.addUint16(var1);
      } else {
         this.addIcode(-40);
         this.addInt(var1);
      }
   }

   private void addInt(int var1) {
      byte[] var2 = this.itsData.itsICode;
      int var3 = this.iCodeTop;
      if (var3 + 4 > var2.length) {
         var2 = this.increaseICodeCapacity(4);
      }

      var2[var3] = (byte)(var1 >>> 24);
      var2[var3 + 1] = (byte)(var1 >>> 16);
      var2[var3 + 2] = (byte)(var1 >>> 8);
      var2[var3 + 3] = (byte)var1;
      this.iCodeTop = var3 + 4;
   }

   private void addStringOp(int var1, String var2) {
      this.addStringPrefix(var2);
      if (Icode.validIcode(var1)) {
         this.addIcode(var1);
      } else {
         this.addToken(var1);
      }
   }

   private void addStringPrefix(String var1) {
      int var2 = this.strings.get(var1, -1);
      if (var2 == -1) {
         var2 = this.strings.size();
         this.strings.put(var1, var2);
      }

      if (var2 < 4) {
         this.addIcode(-41 - var2);
      } else if (var2 <= 255) {
         this.addIcode(-45);
         this.addUint8(var2);
      } else if (var2 <= 65535) {
         this.addIcode(-46);
         this.addUint16(var2);
      } else {
         this.addIcode(-47);
         this.addInt(var2);
      }
   }

   private void addToken(int var1) {
      if (Icode.validTokenCode(var1)) {
         this.addUint8(var1);
      } else {
         throw Kit.codeBug();
      }
   }

   private void addUint16(int var1) {
      if ((-65536 & var1) == 0) {
         byte[] var2 = this.itsData.itsICode;
         int var3 = this.iCodeTop;
         if (var3 + 2 > var2.length) {
            var2 = this.increaseICodeCapacity(2);
         }

         var2[var3] = (byte)(var1 >>> 8);
         var2[var3 + 1] = (byte)var1;
         this.iCodeTop = var3 + 2;
      } else {
         throw Kit.codeBug();
      }
   }

   private void addUint8(int var1) {
      if ((var1 & -256) == 0) {
         byte[] var2 = this.itsData.itsICode;
         int var3 = this.iCodeTop;
         if (var3 == var2.length) {
            var2 = this.increaseICodeCapacity(1);
         }

         var2[var3] = (byte)var1;
         this.iCodeTop = var3 + 1;
      } else {
         throw Kit.codeBug();
      }
   }

   private void addVarOp(int var1, int var2) {
      if (var1 != -7) {
         if (var1 == 157) {
            if (var2 < 128) {
               this.addIcode(-61);
               this.addUint8(var2);
               return;
            }

            this.addIndexOp(-60, var2);
            return;
         }

         if (var1 != 55 && var1 != 56) {
            throw Kit.codeBug();
         }

         if (var2 < 128) {
            byte var3;
            if (var1 == 55) {
               var3 = -48;
            } else {
               var3 = -49;
            }

            this.addIcode(var3);
            this.addUint8(var2);
            return;
         }
      }

      this.addIndexOp(var1, var2);
   }

   private int allocLocal() {
      int var1 = this.localTop;
      int var2 = 1 + this.localTop;
      this.localTop = var2;
      if (var2 > this.itsData.itsMaxLocals) {
         this.itsData.itsMaxLocals = this.localTop;
      }

      return var1;
   }

   private RuntimeException badTree(Node var1) {
      throw new RuntimeException(var1.toString());
   }

   private void fixLabelGotos() {
      for(int var1 = 0; var1 < this.fixupTableTop; ++var1) {
         long var2 = this.fixupTable[var1];
         int var4 = (int)(var2 >> 32);
         int var5 = (int)var2;
         int var6 = this.labelTable[var4];
         if (var6 == -1) {
            throw Kit.codeBug();
         }

         this.resolveGoto(var5, var6);
      }

      this.fixupTableTop = 0;
   }

   private void generateCallFunAndThis(Node var1) {
      int var2 = var1.getType();
      if (var2 != 33 && var2 != 36) {
         if (var2 != 39) {
            this.visitExpression(var1, 0);
            this.addIcode(-18);
            this.stackChange(1);
         } else {
            this.addStringOp(-15, var1.getString());
            this.stackChange(2);
         }
      } else {
         Node var3 = var1.getFirstChild();
         this.visitExpression(var3, 0);
         Node var4 = var3.getNext();
         if (var2 == 33) {
            this.addStringOp(-16, var4.getString());
            this.stackChange(1);
         } else {
            this.visitExpression(var4, 0);
            this.addIcode(-17);
         }
      }
   }

   private void generateFunctionICode() {
      this.itsInFunctionFlag = true;
      FunctionNode var1 = (FunctionNode)this.scriptOrFn;
      this.itsData.itsFunctionType = var1.getFunctionType();
      this.itsData.itsNeedsActivation = var1.requiresActivation();
      if (var1.getFunctionName() != null) {
         this.itsData.itsName = var1.getName();
      }

      if (var1.isGenerator()) {
         this.addIcode(-62);
         this.addUint16('\uffff' & var1.getBaseLineno());
      }

      if (var1.isInStrictMode()) {
         this.itsData.isStrict = true;
      }

      this.generateICodeFromTree(var1.getLastChild());
   }

   private void generateICodeFromTree(Node var1) {
      this.generateNestedFunctions();
      this.generateRegExpLiterals();
      this.visitStatement(var1, 0);
      this.fixLabelGotos();
      if (this.itsData.itsFunctionType == 0) {
         this.addToken(65);
      }

      int var2 = this.itsData.itsICode.length;
      int var3 = this.iCodeTop;
      if (var2 != var3) {
         byte[] var15 = new byte[var3];
         System.arraycopy(this.itsData.itsICode, 0, var15, 0, this.iCodeTop);
         this.itsData.itsICode = var15;
      }

      if (this.strings.size() == 0) {
         this.itsData.itsStringTable = null;
      } else {
         this.itsData.itsStringTable = new String[this.strings.size()];
         ObjToIntMap.Iterator var4 = this.strings.newIterator();
         var4.start();

         while(!var4.done()) {
            String var12 = (String)var4.getKey();
            int var13 = var4.getValue();
            if (this.itsData.itsStringTable[var13] != null) {
               Kit.codeBug();
            }

            this.itsData.itsStringTable[var13] = var12;
            var4.next();
         }
      }

      if (this.doubleTableTop == 0) {
         this.itsData.itsDoubleTable = null;
      } else {
         int var5 = this.itsData.itsDoubleTable.length;
         int var6 = this.doubleTableTop;
         if (var5 != var6) {
            double[] var11 = new double[var6];
            System.arraycopy(this.itsData.itsDoubleTable, 0, var11, 0, this.doubleTableTop);
            this.itsData.itsDoubleTable = var11;
         }
      }

      if (this.exceptionTableTop != 0) {
         int var8 = this.itsData.itsExceptionTable.length;
         int var9 = this.exceptionTableTop;
         if (var8 != var9) {
            int[] var10 = new int[var9];
            System.arraycopy(this.itsData.itsExceptionTable, 0, var10, 0, this.exceptionTableTop);
            this.itsData.itsExceptionTable = var10;
         }
      }

      this.itsData.itsMaxVars = this.scriptOrFn.getParamAndVarCount();
      InterpreterData var7 = this.itsData;
      var7.itsMaxFrameArray = var7.itsMaxVars + this.itsData.itsMaxLocals + this.itsData.itsMaxStack;
      this.itsData.argNames = this.scriptOrFn.getParamAndVarNames();
      this.itsData.argIsConst = this.scriptOrFn.getParamAndVarConst();
      this.itsData.argCount = this.scriptOrFn.getParamCount();
      this.itsData.encodedSourceStart = this.scriptOrFn.getEncodedSourceStart();
      this.itsData.encodedSourceEnd = this.scriptOrFn.getEncodedSourceEnd();
      if (this.literalIds.size() != 0) {
         this.itsData.literalIds = this.literalIds.toArray();
      }

   }

   private void generateNestedFunctions() {
      int var1 = this.scriptOrFn.getFunctionCount();
      if (var1 != 0) {
         InterpreterData[] var2 = new InterpreterData[var1];

         for(int var3 = 0; var3 != var1; ++var3) {
            FunctionNode var4 = this.scriptOrFn.getFunctionNode(var3);
            CodeGenerator var5 = new CodeGenerator();
            var5.compilerEnv = this.compilerEnv;
            var5.scriptOrFn = var4;
            var5.itsData = new InterpreterData(this.itsData);
            var5.generateFunctionICode();
            var2[var3] = var5.itsData;
         }

         this.itsData.itsNestedFunctions = var2;
      }
   }

   private void generateRegExpLiterals() {
      int var1 = this.scriptOrFn.getRegexpCount();
      if (var1 != 0) {
         Context var2 = Context.getContext();
         RegExpProxy var3 = ScriptRuntime.checkRegExpProxy(var2);
         Object[] var4 = new Object[var1];

         for(int var5 = 0; var5 != var1; ++var5) {
            var4[var5] = var3.compileRegExp(var2, this.scriptOrFn.getRegexpString(var5), this.scriptOrFn.getRegexpFlags(var5));
         }

         this.itsData.itsRegExpLiterals = var4;
      }
   }

   private int getDoubleIndex(double var1) {
      int var3 = this.doubleTableTop;
      if (var3 == 0) {
         this.itsData.itsDoubleTable = new double[64];
      } else if (this.itsData.itsDoubleTable.length == var3) {
         double[] var4 = new double[var3 * 2];
         System.arraycopy(this.itsData.itsDoubleTable, 0, var4, 0, var3);
         this.itsData.itsDoubleTable = var4;
      }

      this.itsData.itsDoubleTable[var3] = var1;
      this.doubleTableTop = var3 + 1;
      return var3;
   }

   private int getLocalBlockRef(Node var1) {
      return ((Node)var1.getProp(3)).getExistingIntProp(2);
   }

   private int getTargetLabel(Node var1) {
      int var2 = var1.labelId();
      if (var2 != -1) {
         return var2;
      } else {
         int var3 = this.labelTableTop;
         int[] var4 = this.labelTable;
         if (var4 == null || var3 == var4.length) {
            if (var4 == null) {
               this.labelTable = new int[32];
            } else {
               int[] var5 = new int[2 * var4.length];
               System.arraycopy(var4, 0, var5, 0, var3);
               this.labelTable = var5;
            }
         }

         this.labelTableTop = var3 + 1;
         this.labelTable[var3] = -1;
         var1.labelId(var3);
         return var3;
      }
   }

   private byte[] increaseICodeCapacity(int var1) {
      int var2 = this.itsData.itsICode.length;
      int var3 = this.iCodeTop;
      if (var3 + var1 > var2) {
         int var4 = var2 * 2;
         if (var3 + var1 > var4) {
            var4 = var3 + var1;
         }

         byte[] var5 = new byte[var4];
         System.arraycopy(this.itsData.itsICode, 0, var5, 0, var3);
         this.itsData.itsICode = var5;
         return var5;
      } else {
         throw Kit.codeBug();
      }
   }

   private void markTargetLabel(Node var1) {
      int var2 = this.getTargetLabel(var1);
      if (this.labelTable[var2] != -1) {
         Kit.codeBug();
      }

      this.labelTable[var2] = this.iCodeTop;
   }

   private void releaseLocal(int var1) {
      int var2 = -1 + this.localTop;
      this.localTop = var2;
      if (var1 != var2) {
         Kit.codeBug();
      }

   }

   private void resolveForwardGoto(int var1) {
      int var2 = this.iCodeTop;
      if (var2 >= var1 + 3) {
         this.resolveGoto(var1, var2);
      } else {
         throw Kit.codeBug();
      }
   }

   private void resolveGoto(int var1, int var2) {
      int var3 = var2 - var1;
      if (var3 >= 0 && var3 <= 2) {
         throw Kit.codeBug();
      } else {
         int var4 = var1 + 1;
         if (var3 != (short)var3) {
            if (this.itsData.longJumps == null) {
               this.itsData.longJumps = new UintMap();
            }

            this.itsData.longJumps.put(var4, var2);
            var3 = 0;
         }

         byte[] var5 = this.itsData.itsICode;
         var5[var4] = (byte)(var3 >> 8);
         var5[var4 + 1] = (byte)var3;
      }
   }

   private void stackChange(int var1) {
      if (var1 <= 0) {
         this.stackDepth += var1;
      } else {
         int var2 = var1 + this.stackDepth;
         if (var2 > this.itsData.itsMaxStack) {
            this.itsData.itsMaxStack = var2;
         }

         this.stackDepth = var2;
      }
   }

   private void updateLineNumber(Node var1) {
      int var2 = var1.getLineno();
      if (var2 != this.lineNumber && var2 >= 0) {
         if (this.itsData.firstLinePC < 0) {
            this.itsData.firstLinePC = var2;
         }

         this.lineNumber = var2;
         this.addIcode(-26);
         this.addUint16('\uffff' & var2);
      }

   }

   private void visitArrayComprehension(Node var1, Node var2, Node var3) {
      this.visitStatement(var2, this.stackDepth);
      this.visitExpression(var3, 0);
   }

   private void visitExpression(Node var1, int var2) {
      int var3 = var1.getType();
      Node var4 = var1.getFirstChild();
      int var5 = this.stackDepth;
      if (var3 != 90) {
         byte var10 = 7;
         if (var3 != 103) {
            byte var15 = 1;
            if (var3 != 110) {
               label669: {
                  label670: {
                     label616: {
                        label671: {
                           label672: {
                              label607: {
                                 label648: {
                                    if (var3 != 127) {
                                       label647: {
                                          if (var3 != 143) {
                                             if (var3 == 147) {
                                                this.updateLineNumber(var1);
                                                this.visitExpression(var4, 0);
                                                this.addIcode(-53);
                                                this.stackChange(-1);
                                                int var19 = this.iCodeTop;
                                                this.visitExpression(var4.getNext(), 0);
                                                this.addBackwardGoto(-54, var19);
                                                break label669;
                                             }

                                             if (var3 == 160) {
                                                Node var20 = var1.getFirstChild();
                                                Node var21 = var20.getNext();
                                                this.visitExpression(var20.getFirstChild(), 0);
                                                this.addToken(2);
                                                this.stackChange(-1);
                                                this.visitExpression(var21.getFirstChild(), 0);
                                                this.addToken(3);
                                                break label669;
                                             }

                                             switch(var3) {
                                             case 8:
                                                break label672;
                                             case 9:
                                             case 10:
                                             case 11:
                                             case 12:
                                             case 13:
                                             case 14:
                                             case 15:
                                             case 16:
                                             case 17:
                                             case 18:
                                             case 19:
                                             case 20:
                                             case 21:
                                             case 22:
                                             case 23:
                                             case 24:
                                             case 25:
                                             case 36:
                                             case 46:
                                             case 47:
                                                break label671;
                                             case 26:
                                             case 27:
                                             case 28:
                                             case 29:
                                             case 32:
                                                break label647;
                                             case 30:
                                             case 38:
                                                break label607;
                                             case 31:
                                                if (var4.getType() != 49) {
                                                   var15 = 0;
                                                }

                                                this.visitExpression(var4, 0);
                                                this.visitExpression(var4.getNext(), 0);
                                                if (var15 != 0) {
                                                   this.addIcode(0);
                                                } else {
                                                   this.addToken(31);
                                                }

                                                this.stackChange(-1);
                                                break label669;
                                             case 33:
                                             case 34:
                                                this.visitExpression(var4, 0);
                                                this.addStringOp(var3, var4.getNext().getString());
                                                break label669;
                                             case 35:
                                                break label670;
                                             case 37:
                                                break label616;
                                             case 39:
                                             case 41:
                                             case 49:
                                                this.addStringOp(var3, var1.getString());
                                                this.stackChange(var15);
                                                break label669;
                                             case 40:
                                                double var33 = var1.getDouble();
                                                int var35 = (int)var33;
                                                if ((double)var35 == var33) {
                                                   if (var35 == 0) {
                                                      this.addIcode(-51);
                                                      if (1.0D / var33 < 0.0D) {
                                                         this.addToken(29);
                                                      }
                                                   } else if (var35 == var15) {
                                                      this.addIcode(-52);
                                                   } else if ((short)var35 == var35) {
                                                      this.addIcode(-27);
                                                      this.addUint16(var35 & '\uffff');
                                                   } else {
                                                      this.addIcode(-28);
                                                      this.addInt(var35);
                                                   }
                                                } else {
                                                   this.addIndexOp(40, this.getDoubleIndex(var33));
                                                }

                                                this.stackChange(var15);
                                                break label669;
                                             case 42:
                                             case 43:
                                             case 44:
                                             case 45:
                                                break label648;
                                             case 48:
                                                this.addIndexOp(48, var1.getExistingIntProp(4));
                                                this.stackChange(var15);
                                                break label669;
                                             default:
                                                switch(var3) {
                                                case 52:
                                                case 53:
                                                   break label671;
                                                case 54:
                                                   this.addIndexOp(54, this.getLocalBlockRef(var1));
                                                   this.stackChange(var15);
                                                   break label669;
                                                case 55:
                                                   if (this.itsData.itsNeedsActivation) {
                                                      Kit.codeBug();
                                                   }

                                                   this.addVarOp(55, this.scriptOrFn.getIndexForNameNode(var1));
                                                   this.stackChange(var15);
                                                   break label669;
                                                case 56:
                                                   if (this.itsData.itsNeedsActivation) {
                                                      Kit.codeBug();
                                                   }

                                                   int var37 = this.scriptOrFn.getIndexForNameNode(var4);
                                                   this.visitExpression(var4.getNext(), 0);
                                                   this.addVarOp(56, var37);
                                                   break label669;
                                                default:
                                                   switch(var3) {
                                                   case 62:
                                                   case 63:
                                                      this.addIndexOp(var3, this.getLocalBlockRef(var1));
                                                      this.stackChange(var15);
                                                      break label669;
                                                   case 64:
                                                      break label648;
                                                   default:
                                                      switch(var3) {
                                                      case 66:
                                                      case 67:
                                                         this.visitLiteral(var1, var4);
                                                         break label669;
                                                      case 68:
                                                      case 70:
                                                         this.visitExpression(var4, 0);
                                                         this.addToken(var3);
                                                         break label669;
                                                      case 69:
                                                         break;
                                                      case 71:
                                                         break label607;
                                                      case 72:
                                                         this.visitExpression(var4, 0);
                                                         this.addStringOp(var3, (String)var1.getProp(17));
                                                         break label669;
                                                      case 73:
                                                         if (var4 != null) {
                                                            this.visitExpression(var4, 0);
                                                         } else {
                                                            this.addIcode(-50);
                                                            this.stackChange(var15);
                                                         }

                                                         this.addToken(73);
                                                         this.addUint16('\uffff' & var1.getLineno());
                                                         break label669;
                                                      case 74:
                                                         break label672;
                                                      case 75:
                                                      case 76:
                                                      case 77:
                                                         this.visitExpression(var4, 0);
                                                         this.addToken(var3);
                                                         break label669;
                                                      case 78:
                                                      case 79:
                                                      case 80:
                                                      case 81:
                                                         int var39 = var1.getIntProp(16, 0);
                                                         int var40 = 0;

                                                         do {
                                                            this.visitExpression(var4, 0);
                                                            var40 += var15;
                                                            var4 = var4.getNext();
                                                         } while(var4 != null);

                                                         this.addIndexOp(var3, var39);
                                                         this.stackChange(1 - var40);
                                                         break label669;
                                                      default:
                                                         switch(var3) {
                                                         case 105:
                                                         case 106:
                                                            this.visitExpression(var4, 0);
                                                            this.addIcode(-1);
                                                            this.stackChange(var15);
                                                            int var41 = this.iCodeTop;
                                                            if (var3 != 106) {
                                                               var10 = 6;
                                                            }

                                                            this.addGotoOp(var10);
                                                            this.stackChange(-1);
                                                            this.addIcode(-4);
                                                            this.stackChange(-1);
                                                            this.visitExpression(var4.getNext(), var2 & 1);
                                                            this.resolveForwardGoto(var41);
                                                            break label669;
                                                         case 107:
                                                         case 108:
                                                            this.visitIncDec(var1, var4);
                                                            break label669;
                                                         default:
                                                            switch(var3) {
                                                            case 138:
                                                               int var42 = -1;
                                                               if (this.itsInFunctionFlag && !this.itsData.itsNeedsActivation) {
                                                                  var42 = this.scriptOrFn.getIndexForNameNode(var1);
                                                               }

                                                               if (var42 == -1) {
                                                                  this.addStringOp(-14, var1.getString());
                                                                  this.stackChange(var15);
                                                               } else {
                                                                  this.addVarOp(55, var42);
                                                                  this.stackChange(var15);
                                                                  this.addToken(32);
                                                               }
                                                               break label669;
                                                            case 139:
                                                               this.stackChange(var15);
                                                               break label669;
                                                            case 140:
                                                               break label670;
                                                            case 141:
                                                               break label616;
                                                            default:
                                                               switch(var3) {
                                                               case 156:
                                                                  String var43 = var4.getString();
                                                                  this.visitExpression(var4, 0);
                                                                  this.visitExpression(var4.getNext(), 0);
                                                                  this.addStringOp(-59, var43);
                                                                  this.stackChange(-1);
                                                                  break label669;
                                                               case 157:
                                                                  if (this.itsData.itsNeedsActivation) {
                                                                     Kit.codeBug();
                                                                  }

                                                                  int var44 = this.scriptOrFn.getIndexForNameNode(var4);
                                                                  this.visitExpression(var4.getNext(), 0);
                                                                  this.addVarOp(157, var44);
                                                                  break label669;
                                                               case 158:
                                                                  this.visitArrayComprehension(var1, var4, var4.getNext());
                                                                  break label669;
                                                               default:
                                                                  throw this.badTree(var1);
                                                               }
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
                                          }

                                          this.visitExpression(var4, 0);
                                          Node var18 = var4.getNext();
                                          if (var3 == 143) {
                                             this.addIcode(-1);
                                             this.stackChange(var15);
                                             this.addToken(68);
                                             this.stackChange(-1);
                                          }

                                          this.visitExpression(var18, 0);
                                          this.addToken(69);
                                          this.stackChange(-1);
                                          break label669;
                                       }
                                    }

                                    this.visitExpression(var4, 0);
                                    if (var3 == 127) {
                                       this.addIcode(-4);
                                       this.addIcode(-50);
                                    } else {
                                       this.addToken(var3);
                                    }
                                    break label669;
                                 }

                                 this.addToken(var3);
                                 this.stackChange(var15);
                                 break label669;
                              }

                              if (var3 == 30) {
                                 this.visitExpression(var4, 0);
                              } else {
                                 this.generateCallFunAndThis(var4);
                              }

                              int var23 = 0;

                              while(true) {
                                 Node var24 = var4.getNext();
                                 var4 = var24;
                                 if (var24 == null) {
                                    int var25 = var1.getIntProp(10, 0);
                                    if (var3 != 71 && var25 != 0) {
                                       this.addIndexOp(-21, var23);
                                       this.addUint8(var25);
                                       byte var26 = 0;
                                       if (var3 == 30) {
                                          var26 = 1;
                                       }

                                       this.addUint8(var26);
                                       this.addUint16('\uffff' & this.lineNumber);
                                    } else {
                                       if (var3 == 38 && (var2 & 1) != 0 && !this.compilerEnv.isGenerateDebugInfo() && !this.itsInTryFlag) {
                                          var3 = -55;
                                       }

                                       this.addIndexOp(var3, var23);
                                    }

                                    if (var3 == 30) {
                                       this.stackChange(-var23);
                                    } else {
                                       this.stackChange(-1 - var23);
                                    }

                                    if (var23 > this.itsData.itsMaxCalleeArgs) {
                                       this.itsData.itsMaxCalleeArgs = var23;
                                    }
                                    break label669;
                                 }

                                 this.visitExpression(var24, 0);
                                 ++var23;
                              }
                           }

                           String var22 = var4.getString();
                           this.visitExpression(var4, 0);
                           this.visitExpression(var4.getNext(), 0);
                           this.addStringOp(var3, var22);
                           this.stackChange(-1);
                           break label669;
                        }

                        this.visitExpression(var4, 0);
                        this.visitExpression(var4.getNext(), 0);
                        this.addToken(var3);
                        this.stackChange(-1);
                        break label669;
                     }

                     this.visitExpression(var4, 0);
                     Node var31 = var4.getNext();
                     this.visitExpression(var31, 0);
                     Node var32 = var31.getNext();
                     if (var3 == 141) {
                        this.addIcode(-2);
                        this.stackChange(2);
                        this.addToken(36);
                        this.stackChange(-1);
                        this.stackChange(-1);
                     }

                     this.visitExpression(var32, 0);
                     this.addToken(37);
                     this.stackChange(-2);
                     break label669;
                  }

                  this.visitExpression(var4, 0);
                  Node var28 = var4.getNext();
                  String var29 = var28.getString();
                  Node var30 = var28.getNext();
                  if (var3 == 140) {
                     this.addIcode(-1);
                     this.stackChange(var15);
                     this.addStringOp(33, var29);
                     this.stackChange(-1);
                  }

                  this.visitExpression(var30, 0);
                  this.addStringOp(35, var29);
                  this.stackChange(-1);
               }
            } else {
               int var16 = var1.getExistingIntProp(var15);
               FunctionNode var17 = this.scriptOrFn.getFunctionNode(var16);
               if (var17.getFunctionType() != 2 && var17.getFunctionType() != 4) {
                  throw Kit.codeBug();
               }

               this.addIndexOp(-19, var16);
               this.stackChange(var15);
            }
         } else {
            Node var11 = var4.getNext();
            Node var12 = var11.getNext();
            this.visitExpression(var4, 0);
            int var13 = this.iCodeTop;
            this.addGotoOp(var10);
            this.stackChange(-1);
            this.visitExpression(var11, var2 & 1);
            int var14 = this.iCodeTop;
            this.addGotoOp(5);
            this.resolveForwardGoto(var13);
            this.stackDepth = var5;
            this.visitExpression(var12, var2 & 1);
            this.resolveForwardGoto(var14);
         }
      } else {
         for(Node var6 = var1.getLastChild(); var4 != var6; var4 = var4.getNext()) {
            this.visitExpression(var4, 0);
            this.addIcode(-4);
            this.stackChange(-1);
         }

         int var7 = var2 & 1;

         try {
            this.visitExpression(var4, var7);
         } finally {
            ;
         }
      }

      if (var5 + 1 != this.stackDepth) {
         Kit.codeBug();
      }

   }

   private void visitIncDec(Node var1, Node var2) {
      int var3 = var1.getExistingIntProp(13);
      int var4 = var2.getType();
      if (var4 != 33) {
         if (var4 != 36) {
            if (var4 != 39) {
               if (var4 != 55) {
                  if (var4 == 68) {
                     this.visitExpression(var2.getFirstChild(), 0);
                     this.addIcode(-11);
                     this.addUint8(var3);
                  } else {
                     throw this.badTree(var1);
                  }
               } else {
                  if (this.itsData.itsNeedsActivation) {
                     Kit.codeBug();
                  }

                  this.addVarOp(-7, this.scriptOrFn.getIndexForNameNode(var2));
                  this.addUint8(var3);
                  this.stackChange(1);
               }
            } else {
               this.addStringOp(-8, var2.getString());
               this.addUint8(var3);
               this.stackChange(1);
            }
         } else {
            Node var6 = var2.getFirstChild();
            this.visitExpression(var6, 0);
            this.visitExpression(var6.getNext(), 0);
            this.addIcode(-10);
            this.addUint8(var3);
            this.stackChange(-1);
         }
      } else {
         Node var5 = var2.getFirstChild();
         this.visitExpression(var5, 0);
         this.addStringOp(-9, var5.getNext().getString());
         this.addUint8(var3);
      }
   }

   private void visitLiteral(Node var1, Node var2) {
      int var3 = var1.getType();
      Object[] var5;
      int var6;
      if (var3 == 66) {
         var6 = 0;

         for(Node var11 = var2; var11 != null; var11 = var11.getNext()) {
            ++var6;
         }

         var5 = null;
      } else {
         if (var3 != 67) {
            RuntimeException var4 = this.badTree(var1);
            throw var4;
         }

         var5 = (Object[])((Object[])var1.getProp(12));
         var6 = var5.length;
      }

      this.addIndexOp(-29, var6);
      this.stackChange(2);

      while(var2 != null) {
         int var10 = var2.getType();
         if (var10 == 152) {
            this.visitExpression(var2.getFirstChild(), 0);
            this.addIcode(-57);
         } else if (var10 == 153) {
            this.visitExpression(var2.getFirstChild(), 0);
            this.addIcode(-58);
         } else if (var10 == 164) {
            this.visitExpression(var2.getFirstChild(), 0);
            this.addIcode(-30);
         } else {
            this.visitExpression(var2, 0);
            this.addIcode(-30);
         }

         this.stackChange(-1);
         var2 = var2.getNext();
      }

      if (var3 == 66) {
         int[] var8 = (int[])((int[])var1.getProp(11));
         if (var8 == null) {
            this.addToken(66);
         } else {
            int var9 = this.literalIds.size();
            this.literalIds.add(var8);
            this.addIndexOp(-31, var9);
         }
      } else {
         int var7 = this.literalIds.size();
         this.literalIds.add(var5);
         this.addIndexOp(67, var7);
      }

      this.stackChange(-1);
   }

   private void visitStatement(Node var1, int var2) {
      int var3 = var1.getType();
      Node var4 = var1.getFirstChild();
      if (var3 != -62) {
         if (var3 != 65) {
            byte var6 = 1;
            if (var3 == 82) {
               Jump var7 = (Jump)var1;
               int var8 = this.getLocalBlockRef(var7);
               int var9 = this.allocLocal();
               this.addIndexOp(-13, var9);
               int var10 = this.iCodeTop;
               boolean var11 = this.itsInTryFlag;
               this.itsInTryFlag = (boolean)var6;

               for(Node var12 = var4; var12 != null; var12 = var12.getNext()) {
                  this.visitStatement(var12, var2);
               }

               this.itsInTryFlag = var11;
               Node var13 = var7.target;
               if (var13 != null) {
                  int var18 = this.labelTable[this.getTargetLabel(var13)];
                  this.addExceptionHandler(var10, var18, var18, false, var8, var9);
               }

               Node var15 = var7.getFinally();
               if (var15 != null) {
                  int var17 = this.labelTable[this.getTargetLabel(var15)];
                  this.addExceptionHandler(var10, var17, var17, true, var8, var9);
               }

               this.addIndexOp(-56, var9);
               this.releaseLocal(var9);
            } else {
               byte var19 = -5;
               if (var3 != 110) {
                  if (var3 == 115) {
                     this.updateLineNumber(var1);
                     this.visitExpression(var4, 0);

                     for(Jump var22 = (Jump)var4.getNext(); var22 != null; var22 = (Jump)var22.getNext()) {
                        if (var22.getType() != 116) {
                           throw this.badTree(var22);
                        }

                        Node var23 = var22.getFirstChild();
                        this.addIcode(-1);
                        this.stackChange(var6);
                        this.visitExpression(var23, 0);
                        this.addToken(46);
                        this.stackChange(-1);
                        this.addGoto(var22.target, -6);
                        this.stackChange(-1);
                     }

                     this.addIcode(-4);
                     this.stackChange(-1);
                  } else {
                     label222: {
                        label176: {
                           if (var3 != 124) {
                              if (var3 == 126) {
                                 this.stackChange(var6);
                                 int var24 = this.getLocalBlockRef(var1);
                                 this.addIndexOp(-24, var24);
                                 this.stackChange(-1);

                                 while(var4 != null) {
                                    this.visitStatement(var4, var2);
                                    var4 = var4.getNext();
                                 }

                                 this.addIndexOp(-25, var24);
                                 break label222;
                              }

                              if (var3 == 142) {
                                 int var25 = this.allocLocal();
                                 var1.putIntProp(2, var25);
                                 this.updateLineNumber(var1);

                                 while(var4 != null) {
                                    this.visitStatement(var4, var2);
                                    var4 = var4.getNext();
                                 }

                                 this.addIndexOp(-56, var25);
                                 this.releaseLocal(var25);
                                 break label222;
                              }

                              if (var3 == 161) {
                                 this.addIcode(-64);
                                 break label222;
                              }

                              if (var3 == 50) {
                                 this.updateLineNumber(var1);
                                 this.visitExpression(var4, 0);
                                 this.addToken(50);
                                 this.addUint16('\uffff' & this.lineNumber);
                                 this.stackChange(-1);
                                 break label222;
                              }

                              if (var3 == 51) {
                                 this.updateLineNumber(var1);
                                 this.addIndexOp(51, this.getLocalBlockRef(var1));
                                 break label222;
                              }

                              switch(var3) {
                              case 2:
                                 this.visitExpression(var4, 0);
                                 this.addToken(2);
                                 this.stackChange(-1);
                                 break label222;
                              case 3:
                                 this.addToken(3);
                                 break label222;
                              case 4:
                                 this.updateLineNumber(var1);
                                 if (var1.getIntProp(20, 0) != 0) {
                                    this.addIcode(-63);
                                    this.addUint16('\uffff' & this.lineNumber);
                                 } else if (var4 != null) {
                                    this.visitExpression(var4, var6);
                                    this.addToken(4);
                                    this.stackChange(-1);
                                 } else {
                                    this.addIcode(-22);
                                 }
                                 break label222;
                              case 5:
                                 this.addGoto(((Jump)var1).target, var3);
                                 break label222;
                              case 6:
                              case 7:
                                 Node var26 = ((Jump)var1).target;
                                 this.visitExpression(var4, 0);
                                 this.addGoto(var26, var3);
                                 this.stackChange(-1);
                                 break label222;
                              default:
                                 switch(var3) {
                                 case 57:
                                    int var27 = this.getLocalBlockRef(var1);
                                    int var28 = var1.getExistingIntProp(14);
                                    String var29 = var4.getString();
                                    this.visitExpression(var4.getNext(), 0);
                                    this.addStringPrefix(var29);
                                    this.addIndexPrefix(var27);
                                    this.addToken(57);
                                    if (var28 == 0) {
                                       var6 = 0;
                                    }

                                    this.addUint8(var6);
                                    this.stackChange(-1);
                                    break label222;
                                 case 58:
                                 case 59:
                                 case 60:
                                 case 61:
                                    this.visitExpression(var4, 0);
                                    this.addIndexOp(var3, this.getLocalBlockRef(var1));
                                    this.stackChange(-1);
                                    break label222;
                                 default:
                                    switch(var3) {
                                    case 129:
                                    case 130:
                                    case 131:
                                    case 133:
                                       break;
                                    case 132:
                                       this.markTargetLabel(var1);
                                       break label222;
                                    case 134:
                                    case 135:
                                       this.updateLineNumber(var1);
                                       this.visitExpression(var4, 0);
                                       if (var3 == 134) {
                                          var19 = -4;
                                       }

                                       this.addIcode(var19);
                                       this.stackChange(-1);
                                       break label222;
                                    case 136:
                                       this.addGoto(((Jump)var1).target, -23);
                                       break label222;
                                    case 137:
                                       break label176;
                                    default:
                                       throw this.badTree(var1);
                                    }
                                 }
                              }
                           }

                           this.updateLineNumber(var1);
                        }

                        while(var4 != null) {
                           this.visitStatement(var4, var2);
                           var4 = var4.getNext();
                        }
                     }
                  }
               } else {
                  int var20 = var1.getExistingIntProp(var6);
                  int var21 = this.scriptOrFn.getFunctionNode(var20).getFunctionType();
                  if (var21 == 3) {
                     this.addIndexOp(-20, var20);
                  } else if (var21 != var6) {
                     throw Kit.codeBug();
                  }

                  if (!this.itsInFunctionFlag) {
                     this.addIndexOp(-19, var20);
                     this.stackChange(var6);
                     this.addIcode(var19);
                     this.stackChange(-1);
                  }
               }
            }
         } else {
            this.updateLineNumber(var1);
            this.addToken(65);
         }
      }

      if (this.stackDepth != var2) {
         RuntimeException var5 = Kit.codeBug();
         throw var5;
      }
   }

   public InterpreterData compile(CompilerEnvirons var1, ScriptNode var2, String var3, boolean var4) {
      this.compilerEnv = var1;
      (new NodeTransformer()).transform(var2, var1);
      if (var4) {
         this.scriptOrFn = var2.getFunctionNode(0);
      } else {
         this.scriptOrFn = var2;
      }

      InterpreterData var5 = new InterpreterData(var1.getLanguageVersion(), this.scriptOrFn.getSourceName(), var3, this.scriptOrFn.isInStrictMode());
      this.itsData = var5;
      var5.topLevel = true;
      if (var4) {
         this.generateFunctionICode();
      } else {
         this.generateICodeFromTree(this.scriptOrFn);
      }

      return this.itsData;
   }
}
