package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

public class NodeTransformer {
   private boolean hasFinally;
   private ObjArray loopEnds;
   private ObjArray loops;

   private static Node addBeforeCurrent(Node var0, Node var1, Node var2, Node var3) {
      if (var1 == null) {
         if (var2 != var0.getFirstChild()) {
            Kit.codeBug();
         }

         var0.addChildToFront(var3);
         return var3;
      } else {
         if (var2 != var1.getNext()) {
            Kit.codeBug();
         }

         var0.addChildAfter(var3, var1);
         return var3;
      }
   }

   private static Node replaceCurrent(Node var0, Node var1, Node var2, Node var3) {
      if (var1 == null) {
         if (var2 != var0.getFirstChild()) {
            Kit.codeBug();
         }

         var0.replaceChild(var2, var3);
         return var3;
      } else if (var1.next == var2) {
         var0.replaceChildAfter(var1, var3);
         return var3;
      } else {
         var0.replaceChild(var2, var3);
         return var3;
      }
   }

   private void transformCompilationUnit(ScriptNode var1, boolean var2) {
      this.loops = new ObjArray();
      this.loopEnds = new ObjArray();
      this.hasFinally = false;
      boolean var3;
      if (var1.getType() == 110 && !((FunctionNode)var1).requiresActivation()) {
         var3 = false;
      } else {
         var3 = true;
      }

      boolean var4 = false;
      if (!var3) {
         var4 = true;
      }

      var1.flattenSymbolTable(var4);
      this.transformCompilationUnit_r(var1, var1, var1, var3, var2);
   }

   private void transformCompilationUnit_r(ScriptNode var1, Node var2, Scope var3, boolean var4, boolean var5) {
      Node var6 = null;

      while(true) {
         while(true) {
            Node var8;
            Node var9;
            if (var6 == null) {
               var8 = var2.getFirstChild();
               var9 = null;
            } else {
               var8 = var6.getNext();
               var9 = var6;
            }

            if (var8 == null) {
               return;
            }

            int var11;
            label211: {
               int var10 = var8.getType();
               if (var4 && (var10 == 130 || var10 == 133 || var10 == 158) && var8 instanceof Scope) {
                  Scope var72 = (Scope)var8;
                  if (var72.getSymbolTable() != null) {
                     short var73;
                     if (var10 == 158) {
                        var73 = 159;
                     } else {
                        var73 = 154;
                     }

                     Node var74 = new Node(var73);
                     Node var75 = new Node(154);
                     var74.addChildToBack(var75);
                     Iterator var76 = var72.getSymbolTable().keySet().iterator();

                     while(var76.hasNext()) {
                        var75.addChildToBack(Node.newString(39, (String)var76.next()));
                     }

                     var72.setSymbolTable((Map)null);
                     Node var77 = var8;
                     var8 = replaceCurrent(var2, var9, var8, var74);
                     int var78 = var8.getType();
                     var74.addChildToBack(var77);
                     var11 = var78;
                     break label211;
                  }
               }

               var11 = var10;
            }

            Node var14;
            label353: {
               label368: {
                  label351: {
                     label397: {
                        label398: {
                           label385: {
                              byte var12 = 3;
                              if (var11 != var12) {
                                 label382: {
                                    short var18 = 136;
                                    byte var19 = 124;
                                    byte var20 = 82;
                                    if (var11 == 4) {
                                       boolean var21;
                                       if (var1.getType() == 110 && ((FunctionNode)var1).isGenerator()) {
                                          var21 = true;
                                       } else {
                                          var21 = false;
                                       }

                                       if (var21) {
                                          var8.putIntProp(20, 1);
                                       }

                                       if (this.hasFinally) {
                                          int var23 = this.loops.size() - 1;

                                          Node var24;
                                          for(var24 = null; var23 >= 0; var20 = 82) {
                                             Node var29 = (Node)this.loops.get(var23);
                                             int var30 = var29.getType();
                                             if (var30 == var20 || var30 == 124) {
                                                Object var32;
                                                if (var30 == var20) {
                                                   Jump var31 = new Jump(136);
                                                   var31.target = ((Jump)var29).getFinally();
                                                   var32 = var31;
                                                } else {
                                                   var32 = new Node(3);
                                                }

                                                if (var24 == null) {
                                                   var24 = new Node(130, var8.getLineno());
                                                }

                                                var24.addChildToBack((Node)var32);
                                             }

                                             --var23;
                                          }

                                          if (var24 != null) {
                                             Node var26 = var8.getFirstChild();
                                             Node var27 = replaceCurrent(var2, var9, var8, var24);
                                             if (var26 != null && !var21) {
                                                Node var28 = new Node(135, var26);
                                                var24.addChildToFront(var28);
                                                var24.addChildToBack(new Node(65));
                                                this.transformCompilationUnit_r(var1, var28, var3, var4, var5);
                                             } else {
                                                var24.addChildToBack(var8);
                                             }

                                             var6 = var27;
                                             continue;
                                          }
                                       }
                                       break label368;
                                    }

                                    if (var11 == 7) {
                                       break label385;
                                    }

                                    if (var11 != 8) {
                                       if (var11 == 38) {
                                          this.visitCall(var8, var1);
                                          break label368;
                                       }

                                       if (var11 == 39) {
                                          break label397;
                                       }

                                       if (var11 == 73) {
                                          ((FunctionNode)var1).addResumptionPoint(var8);
                                          break label368;
                                       }

                                       if (var11 == var20) {
                                          Node var47 = ((Jump)var8).getFinally();
                                          if (var47 != null) {
                                             this.hasFinally = true;
                                             this.loops.push(var8);
                                             this.loopEnds.push(var47);
                                          }
                                          break label368;
                                       }

                                       if (var11 != 115) {
                                          if (var11 == 138) {
                                             Scope var48 = var3.getDefiningScope(var8.getString());
                                             if (var48 != null) {
                                                var8.setScope(var48);
                                             }
                                             break label368;
                                          }

                                          if (var11 == 159) {
                                             break label398;
                                          }

                                          switch(var11) {
                                          case 30:
                                             this.visitNew(var8, var1);
                                             break label368;
                                          case 31:
                                             break label397;
                                          case 32:
                                             break label385;
                                          default:
                                             switch(var11) {
                                             case 121:
                                             case 122:
                                                Jump var60 = (Jump)var8;
                                                Jump var61 = var60.getJumpStatement();
                                                if (var61 == null) {
                                                   Kit.codeBug();
                                                }

                                                for(int var62 = this.loops.size(); var62 != 0; var19 = 124) {
                                                   --var62;
                                                   Node var63 = (Node)this.loops.get(var62);
                                                   if (var63 == var61) {
                                                      if (var11 == 121) {
                                                         var60.target = var61.target;
                                                      } else {
                                                         var60.target = var61.getContinue();
                                                      }

                                                      var60.setType(5);
                                                      var14 = var8;
                                                      break label353;
                                                   }

                                                   int var64 = var63.getType();
                                                   if (var64 == var19) {
                                                      var9 = addBeforeCurrent(var2, var9, var8, new Node(var12));
                                                   } else if (var64 == var20) {
                                                      Jump var65 = (Jump)var63;
                                                      Jump var66 = new Jump(var18);
                                                      var66.target = var65.getFinally();
                                                      var9 = addBeforeCurrent(var2, var9, var8, var66);
                                                   }

                                                   var12 = 3;
                                                   var18 = 136;
                                                }

                                                throw Kit.codeBug();
                                             case 123:
                                                break label351;
                                             case 124:
                                                this.loops.push(var8);
                                                Node var70 = var8.getNext();
                                                if (var70.getType() != var12) {
                                                   Kit.codeBug();
                                                }

                                                this.loopEnds.push(var70);
                                                break label368;
                                             default:
                                                switch(var11) {
                                                case 131:
                                                case 133:
                                                   break;
                                                case 132:
                                                   break label382;
                                                default:
                                                   switch(var11) {
                                                   case 154:
                                                      break label398;
                                                   case 155:
                                                      break label351;
                                                   case 156:
                                                      break label397;
                                                   default:
                                                      break label368;
                                                   }
                                                }
                                             }
                                          }
                                       }

                                       this.loops.push(var8);
                                       this.loopEnds.push(((Jump)var8).target);
                                       break label368;
                                    }

                                    if (var5) {
                                       var8.setType(74);
                                    }
                                    break label397;
                                 }
                              }

                              if (!this.loopEnds.isEmpty() && this.loopEnds.peek() == var8) {
                                 this.loopEnds.pop();
                                 this.loops.pop();
                              }
                              break label368;
                           }

                           Node var33 = var8.getFirstChild();
                           if (var11 == 7) {
                              while(var33.getType() == 26) {
                                 var33 = var33.getFirstChild();
                              }

                              if (var33.getType() == 12 || var33.getType() == 13) {
                                 Node var35 = var33.getFirstChild();
                                 Node var36 = var33.getLastChild();
                                 if (var35.getType() == 39 && var35.getString().equals("undefined")) {
                                    var33 = var36;
                                 } else if (var36.getType() == 39 && var36.getString().equals("undefined")) {
                                    var33 = var35;
                                 }
                              }
                           }

                           if (var33.getType() == 33) {
                              var33.setType(34);
                           }
                           break label368;
                        }

                        if (var8.getFirstChild().getType() == 154) {
                           boolean var57;
                           if (var1.getType() == 110 && !((FunctionNode)var1).requiresActivation()) {
                              var57 = false;
                           } else {
                              var57 = true;
                           }

                           Node var58 = this.visitLet(var57, var2, var9, var8);
                           var14 = var58;
                           break label353;
                        }
                        break label351;
                     }

                     if (!var4) {
                        Node var37;
                        if (var11 == 39) {
                           var37 = var8;
                        } else {
                           var37 = var8.getFirstChild();
                           if (var37.getType() != 49) {
                              if (var11 != 31) {
                                 throw Kit.codeBug();
                              }
                              break label368;
                           }
                        }

                        if (var37.getScope() == null) {
                           Scope var38 = var3.getDefiningScope(var37.getString());
                           if (var38 != null) {
                              var37.setScope(var38);
                              if (var11 == 39) {
                                 var8.setType(55);
                              } else if (var11 != 8 && var11 != 74) {
                                 if (var11 != 156) {
                                    if (var11 != 31) {
                                       throw Kit.codeBug();
                                    }

                                    Node var41 = replaceCurrent(var2, var9, var8, new Node(44));
                                    var14 = var41;
                                    break label353;
                                 }

                                 var8.setType(157);
                                 var37.setType(41);
                              } else {
                                 var8.setType(56);
                                 var37.setType(41);
                              }
                           }
                        }
                     }
                     break label368;
                  }

                  Node var49 = new Node(130);
                  Node var50 = var8.getFirstChild();

                  while(true) {
                     Node var53;
                     while(true) {
                        if (var50 == null) {
                           Node var51 = replaceCurrent(var2, var9, var8, var49);
                           var14 = var51;
                           break label353;
                        }

                        var53 = var50;
                        var50 = var50.getNext();
                        if (var53.getType() == 39) {
                           if (!var53.hasChildren()) {
                              continue;
                           }

                           Node var54 = var53.getFirstChild();
                           var53.removeChild(var54);
                           var53.setType(49);
                           short var56;
                           if (var11 == 155) {
                              var56 = 156;
                           } else {
                              var56 = 8;
                           }

                           var53 = new Node(var56, var53, var54);
                           break;
                        }

                        if (var53.getType() != 159) {
                           throw Kit.codeBug();
                        }
                        break;
                     }

                     var49.addChildToBack(new Node(134, var53, var8.getLineno()));
                  }
               }

               var14 = var8;
            }

            Scope var15;
            if (var14 instanceof Scope) {
               var15 = (Scope)var14;
            } else {
               var15 = var3;
            }

            this.transformCompilationUnit_r(var1, var14, var15, var4, var5);
            var6 = var14;
         }
      }
   }

   public final void transform(ScriptNode var1, CompilerEnvirons var2) {
      this.transform(var1, false, var2);
   }

   public final void transform(ScriptNode var1, boolean var2, CompilerEnvirons var3) {
      boolean var4 = var2;
      if (var3.getLanguageVersion() >= 200 && var1.isInStrictMode()) {
         var4 = true;
      }

      this.transformCompilationUnit(var1, var4);

      for(int var5 = 0; var5 != var1.getFunctionCount(); ++var5) {
         this.transform(var1.getFunctionNode(var5), var4, var3);
      }

   }

   protected void visitCall(Node var1, ScriptNode var2) {
   }

   protected Node visitLet(boolean var1, Node var2, Node var3, Node var4) {
      Node var5 = var4.getFirstChild();
      Node var6 = var5.getNext();
      var4.removeChild(var5);
      var4.removeChild(var6);
      int var7 = var4.getType();
      short var8 = 159;
      boolean var9;
      if (var7 == var8) {
         var9 = true;
      } else {
         var9 = false;
      }

      short var10 = 154;
      short var11 = 134;
      byte var12 = 90;
      if (!var1) {
         short var13;
         if (var9) {
            var13 = 90;
         } else {
            var13 = 130;
         }

         Node var14 = replaceCurrent(var2, var3, var4, new Node(var13));
         Node var15 = new Node(90);

         for(Node var16 = var5.getFirstChild(); var16 != null; var16 = var16.getNext()) {
            Node var21 = var16;
            if (var16.getType() == 159) {
               Node var24 = var16.getFirstChild();
               if (var24.getType() != 154) {
                  throw Kit.codeBug();
               }

               if (var9) {
                  var6 = new Node(90, var24.getNext(), var6);
               } else {
                  var6 = new Node(130, new Node(134, var24.getNext()), var6);
               }

               Scope.joinScopes((Scope)var16, (Scope)var4);
               var21 = var24.getFirstChild();
            }

            if (var21.getType() != 39) {
               throw Kit.codeBug();
            }

            Node var22 = Node.newString(var21.getString());
            var22.setScope((Scope)var4);
            Node var23 = var21.getFirstChild();
            if (var23 == null) {
               var23 = new Node(127, Node.newNumber(0.0D));
            }

            var15.addChildToBack(new Node(56, var22, var23));
         }

         if (var9) {
            var14.addChildToBack(var15);
            var4.setType(90);
            var14.addChildToBack(var4);
            var4.addChildToBack(var6);
            if (var6 instanceof Scope) {
               Scope var20 = ((Scope)var6).getParentScope();
               ((Scope)var6).setParentScope((Scope)var4);
               ((Scope)var4).setParentScope(var20);
               return var14;
            }
         } else {
            var14.addChildToBack(new Node(134, var15));
            var4.setType(130);
            var14.addChildToBack(var4);
            var4.addChildrenToBack(var6);
            if (var6 instanceof Scope) {
               Scope var18 = ((Scope)var6).getParentScope();
               ((Scope)var6).setParentScope((Scope)var4);
               ((Scope)var4).setParentScope(var18);
            }
         }

         return var14;
      } else {
         short var25;
         if (var9) {
            var25 = 160;
         } else {
            var25 = 130;
         }

         Node var26 = replaceCurrent(var2, var3, var4, new Node(var25));
         ArrayList var27 = new ArrayList();
         Node var28 = new Node(67);

         for(Node var29 = var5.getFirstChild(); var29 != null; var12 = 90) {
            Node var30 = var29;
            if (var29.getType() == var8) {
               List var33 = (List)var29.getProp(22);
               Node var34 = var29.getFirstChild();
               if (var34.getType() != var10) {
                  throw Kit.codeBug();
               }

               if (var9) {
                  var6 = new Node(var12, var34.getNext(), var6);
               } else {
                  var6 = new Node(130, new Node(var11, var34.getNext()), var6);
               }

               if (var33 != null) {
                  var27.addAll(var33);

                  for(int var36 = 0; var36 < var33.size(); ++var36) {
                     var28.addChildToBack(new Node(127, Node.newNumber(0.0D)));
                  }
               }

               var30 = var34.getFirstChild();
            }

            if (var30.getType() != 39) {
               throw Kit.codeBug();
            }

            var27.add(ScriptRuntime.getIndexObject(var30.getString()));
            Node var32 = var30.getFirstChild();
            if (var32 == null) {
               var32 = new Node(127, Node.newNumber(0.0D));
            }

            var28.addChildToBack(var32);
            var29 = var29.getNext();
            var8 = 159;
            var10 = 154;
            var11 = 134;
         }

         var28.putProp(12, var27.toArray());
         var26.addChildToBack(new Node(2, var28));
         var26.addChildToBack(new Node(124, var6));
         var26.addChildToBack(new Node(3));
         return var26;
      }
   }

   protected void visitNew(Node var1, ScriptNode var2) {
   }
}
