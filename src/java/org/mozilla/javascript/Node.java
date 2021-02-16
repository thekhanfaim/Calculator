package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

public class Node implements Iterable {
   public static final int ARROW_FUNCTION_PROP = 27;
   public static final int ATTRIBUTE_FLAG = 2;
   public static final int BOTH = 0;
   public static final int CASEARRAY_PROP = 5;
   public static final int CATCH_SCOPE_PROP = 14;
   public static final int CONTROL_BLOCK_PROP = 18;
   public static final int DECR_FLAG = 1;
   public static final int DESCENDANTS_FLAG = 4;
   public static final int DESTRUCTURING_ARRAY_LENGTH = 21;
   public static final int DESTRUCTURING_NAMES = 22;
   public static final int DESTRUCTURING_PARAMS = 23;
   public static final int DESTRUCTURING_SHORTHAND = 26;
   public static final int DIRECTCALL_PROP = 9;
   public static final int END_DROPS_OFF = 1;
   public static final int END_RETURNS = 2;
   public static final int END_RETURNS_VALUE = 4;
   public static final int END_UNREACHED = 0;
   public static final int END_YIELDS = 8;
   public static final int EXPRESSION_CLOSURE_PROP = 25;
   public static final int FUNCTION_PROP = 1;
   public static final int GENERATOR_END_PROP = 20;
   public static final int INCRDECR_PROP = 13;
   public static final int ISNUMBER_PROP = 8;
   public static final int JSDOC_PROP = 24;
   public static final int LABEL_ID_PROP = 15;
   public static final int LAST_PROP = 27;
   public static final int LEFT = 1;
   public static final int LOCAL_BLOCK_PROP = 3;
   public static final int LOCAL_PROP = 2;
   public static final int MEMBER_TYPE_PROP = 16;
   public static final int NAME_PROP = 17;
   public static final int NON_SPECIALCALL = 0;
   private static final Node NOT_SET = new Node(-1);
   public static final int OBJECT_IDS_PROP = 12;
   public static final int PARENTHESIZED_PROP = 19;
   public static final int POST_FLAG = 2;
   public static final int PROPERTY_FLAG = 1;
   public static final int REGEXP_PROP = 4;
   public static final int RIGHT = 2;
   public static final int SKIP_INDEXES_PROP = 11;
   public static final int SPECIALCALL_EVAL = 1;
   public static final int SPECIALCALL_PROP = 10;
   public static final int SPECIALCALL_WITH = 2;
   public static final int TARGETBLOCK_PROP = 6;
   public static final int VARIABLE_PROP = 7;
   protected Node first;
   protected Node last;
   protected int lineno;
   protected Node next;
   protected Node.PropListItem propListHead;
   protected int type;

   public Node(int var1) {
      this.type = -1;
      this.lineno = -1;
      this.type = var1;
   }

   public Node(int var1, int var2) {
      this.type = -1;
      this.lineno = -1;
      this.type = var1;
      this.lineno = var2;
   }

   public Node(int var1, Node var2) {
      this.type = -1;
      this.lineno = -1;
      this.type = var1;
      this.last = var2;
      this.first = var2;
      var2.next = null;
   }

   public Node(int var1, Node var2, int var3) {
      this(var1, var2);
      this.lineno = var3;
   }

   public Node(int var1, Node var2, Node var3) {
      this.type = -1;
      this.lineno = -1;
      this.type = var1;
      this.first = var2;
      this.last = var3;
      var2.next = var3;
      var3.next = null;
   }

   public Node(int var1, Node var2, Node var3, int var4) {
      this(var1, var2, var3);
      this.lineno = var4;
   }

   public Node(int var1, Node var2, Node var3, Node var4) {
      this.type = -1;
      this.lineno = -1;
      this.type = var1;
      this.first = var2;
      this.last = var4;
      var2.next = var3;
      var3.next = var4;
      var4.next = null;
   }

   public Node(int var1, Node var2, Node var3, Node var4, int var5) {
      this(var1, var2, var3, var4);
      this.lineno = var5;
   }

   private static void appendPrintId(Node var0, ObjToIntMap var1, StringBuilder var2) {
   }

   private int endCheck() {
      int var1 = this.type;
      if (var1 == 4) {
         return this.first != null ? 4 : 2;
      } else {
         if (var1 != 50) {
            if (var1 == 73) {
               return 8;
            }

            if (var1 == 130 || var1 == 142) {
               Node var2 = this.first;
               if (var2 == null) {
                  return 1;
               } else {
                  int var3 = var2.type;
                  if (var3 != 7) {
                     if (var3 != 82) {
                        if (var3 != 115) {
                           return var3 != 131 ? this.endCheckBlock() : var2.endCheckLabel();
                        } else {
                           return var2.endCheckSwitch();
                        }
                     } else {
                        return var2.endCheckTry();
                     }
                  } else {
                     return var2.endCheckIf();
                  }
               }
            }

            if (var1 == 121) {
               return this.endCheckBreak();
            }

            if (var1 != 122) {
               switch(var1) {
               case 132:
                  Node var4 = this.next;
                  if (var4 != null) {
                     return var4.endCheck();
                  }

                  return 1;
               case 133:
                  return this.endCheckLoop();
               case 134:
                  Node var5 = this.first;
                  if (var5 != null) {
                     return var5.endCheck();
                  }

                  return 1;
               default:
                  return 1;
               }
            }
         }

         return 0;
      }
   }

   private int endCheckBlock() {
      int var1 = 1;

      for(Node var2 = this.first; (var1 & 1) != 0 && var2 != null; var2 = var2.next) {
         var1 = var1 & -2 | var2.endCheck();
      }

      return var1;
   }

   private int endCheckBreak() {
      ((Jump)this).getJumpStatement().putIntProp(18, 1);
      return 0;
   }

   private int endCheckIf() {
      Node var1 = this.next;
      Node var2 = ((Jump)this).target;
      int var3 = var1.endCheck();
      return var2 != null ? var3 | var2.endCheck() : var3 | 1;
   }

   private int endCheckLabel() {
      return this.next.endCheck() | this.getIntProp(18, 0);
   }

   private int endCheckLoop() {
      Node var1;
      for(var1 = this.first; var1.next != this.last; var1 = var1.next) {
      }

      if (var1.type != 6) {
         return 1;
      } else {
         int var2 = ((Jump)var1).target.next.endCheck();
         if (var1.first.type == 45) {
            var2 &= -2;
         }

         return var2 | this.getIntProp(18, 0);
      }
   }

   private int endCheckSwitch() {
      return 0;
   }

   private int endCheckTry() {
      return 0;
   }

   private Node.PropListItem ensureProperty(int var1) {
      Node.PropListItem var2 = this.lookupProperty(var1);
      if (var2 == null) {
         var2 = new Node.PropListItem();
         var2.type = var1;
         var2.next = this.propListHead;
         this.propListHead = var2;
      }

      return var2;
   }

   private static void generatePrintIds(Node var0, ObjToIntMap var1) {
   }

   private Node.PropListItem lookupProperty(int var1) {
      Node.PropListItem var2;
      for(var2 = this.propListHead; var2 != null && var1 != var2.type; var2 = var2.next) {
      }

      return var2;
   }

   public static Node newNumber(double var0) {
      NumberLiteral var2 = new NumberLiteral();
      var2.setNumber(var0);
      return var2;
   }

   public static Node newString(int var0, String var1) {
      Name var2 = new Name();
      var2.setIdentifier(var1);
      var2.setType(var0);
      return var2;
   }

   public static Node newString(String var0) {
      return newString(41, var0);
   }

   public static Node newTarget() {
      return new Node(132);
   }

   private static final String propToString(int var0) {
      return null;
   }

   private void resetTargets_r() {
      int var1 = this.type;
      if (var1 == 132 || var1 == 73) {
         this.labelId(-1);
      }

      for(Node var2 = this.first; var2 != null; var2 = var2.next) {
         var2.resetTargets_r();
      }

   }

   private void toString(ObjToIntMap var1, StringBuilder var2) {
   }

   private static void toStringTreeHelper(ScriptNode var0, Node var1, ObjToIntMap var2, int var3, StringBuilder var4) {
   }

   public void addChildAfter(Node var1, Node var2) {
      if (var1.next == null) {
         var1.next = var2.next;
         var2.next = var1;
         if (this.last == var2) {
            this.last = var1;
         }

      } else {
         throw new RuntimeException("newChild had siblings in addChildAfter");
      }
   }

   public void addChildBefore(Node var1, Node var2) {
      if (var1.next == null) {
         Node var3 = this.first;
         if (var3 == var2) {
            var1.next = var3;
            this.first = var1;
         } else {
            this.addChildAfter(var1, this.getChildBefore(var2));
         }
      } else {
         throw new RuntimeException("newChild had siblings in addChildBefore");
      }
   }

   public void addChildToBack(Node var1) {
      var1.next = null;
      Node var2 = this.last;
      if (var2 == null) {
         this.last = var1;
         this.first = var1;
      } else {
         var2.next = var1;
         this.last = var1;
      }
   }

   public void addChildToFront(Node var1) {
      var1.next = this.first;
      this.first = var1;
      if (this.last == null) {
         this.last = var1;
      }

   }

   public void addChildrenToBack(Node var1) {
      Node var2 = this.last;
      if (var2 != null) {
         var2.next = var1;
      }

      this.last = var1.getLastSibling();
      if (this.first == null) {
         this.first = var1;
      }

   }

   public void addChildrenToFront(Node var1) {
      Node var2 = var1.getLastSibling();
      var2.next = this.first;
      this.first = var1;
      if (this.last == null) {
         this.last = var2;
      }

   }

   public Node getChildBefore(Node var1) {
      if (var1 == this.first) {
         return null;
      } else {
         Node var2 = this.first;

         do {
            if (var2.next == var1) {
               return var2;
            }

            var2 = var2.next;
         } while(var2 != null);

         throw new RuntimeException("node is not a child");
      }
   }

   public final double getDouble() {
      return ((NumberLiteral)this).getNumber();
   }

   public int getExistingIntProp(int var1) {
      Node.PropListItem var2 = this.lookupProperty(var1);
      if (var2 == null) {
         Kit.codeBug();
      }

      return var2.intValue;
   }

   public Node getFirstChild() {
      return this.first;
   }

   public int getIntProp(int var1, int var2) {
      Node.PropListItem var3 = this.lookupProperty(var1);
      return var3 == null ? var2 : var3.intValue;
   }

   public String getJsDoc() {
      Comment var1 = this.getJsDocNode();
      return var1 != null ? var1.getValue() : null;
   }

   public Comment getJsDocNode() {
      return (Comment)this.getProp(24);
   }

   public Node getLastChild() {
      return this.last;
   }

   public Node getLastSibling() {
      Node var1;
      for(var1 = this; var1.next != null; var1 = var1.next) {
      }

      return var1;
   }

   public int getLineno() {
      return this.lineno;
   }

   public Node getNext() {
      return this.next;
   }

   public Object getProp(int var1) {
      Node.PropListItem var2 = this.lookupProperty(var1);
      return var2 == null ? null : var2.objectValue;
   }

   public Scope getScope() {
      return ((Name)this).getScope();
   }

   public final String getString() {
      return ((Name)this).getIdentifier();
   }

   public int getType() {
      return this.type;
   }

   public boolean hasChildren() {
      return this.first != null;
   }

   public boolean hasConsistentReturnUsage() {
      int var1 = this.endCheck();
      return (var1 & 4) == 0 || (var1 & 11) == 0;
   }

   public boolean hasSideEffects() {
      int var1 = this.type;
      boolean var2 = true;
      if (var1 != 30 && var1 != 31 && var1 != 37 && var1 != 38 && var1 != 50 && var1 != 51 && var1 != 56 && var1 != 57 && var1 != 82 && var1 != 83) {
         switch(var1) {
         case -1:
         case 35:
         case 65:
         case 73:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 130:
         case 131:
         case 132:
         case 133:
         case 135:
         case 136:
         case 140:
         case 141:
         case 142:
         case 143:
         case 154:
         case 155:
         case 159:
         case 160:
            break;
         case 90:
         case 134:
            Node var3 = this.last;
            if (var3 != null) {
               try {
                  boolean var5 = var3.hasSideEffects();
                  return var5;
               } finally {
                  ;
               }
            }

            return var2;
         case 103:
            label150: {
               Node var6 = this.first;
               if (var6 != null) {
                  Node var8 = var6.next;
                  if (var8 != null && var8.next != null) {
                     break label150;
                  }
               }

               Kit.codeBug();
            }

            if (this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects()) {
               return var2;
            }

            return false;
         default:
            switch(var1) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
               break;
            default:
               switch(var1) {
               case 69:
               case 70:
               case 71:
                  break;
               default:
                  switch(var1) {
                  case 105:
                  case 106:
                     if (this.first == null || this.last == null) {
                        Kit.codeBug();
                     }

                     if (!this.first.hasSideEffects()) {
                        if (this.last.hasSideEffects()) {
                           return var2;
                        }

                        var2 = false;
                     }

                     return var2;
                  case 107:
                  case 108:
                     break;
                  default:
                     switch(var1) {
                     case 113:
                     case 114:
                     case 115:
                        break;
                     default:
                        return false;
                     }
                  }
               }
            }
         }
      }

      return var2;
   }

   public Iterator iterator() {
      return new Node.NodeIterator();
   }

   public final int labelId() {
      int var1 = this.type;
      if (var1 != 132 && var1 != 73) {
         Kit.codeBug();
      }

      return this.getIntProp(15, -1);
   }

   public void labelId(int var1) {
      int var2 = this.type;
      if (var2 != 132 && var2 != 73) {
         Kit.codeBug();
      }

      this.putIntProp(15, var1);
   }

   public void putIntProp(int var1, int var2) {
      this.ensureProperty(var1).intValue = var2;
   }

   public void putProp(int var1, Object var2) {
      if (var2 == null) {
         this.removeProp(var1);
      } else {
         this.ensureProperty(var1).objectValue = var2;
      }
   }

   public void removeChild(Node var1) {
      Node var2 = this.getChildBefore(var1);
      if (var2 == null) {
         this.first = this.first.next;
      } else {
         var2.next = var1.next;
      }

      if (var1 == this.last) {
         this.last = var2;
      }

      var1.next = null;
   }

   public void removeChildren() {
      this.last = null;
      this.first = null;
   }

   public void removeProp(int var1) {
      Node.PropListItem var2 = this.propListHead;
      if (var2 != null) {
         Node.PropListItem var3 = null;

         while(var2.type != var1) {
            var3 = var2;
            var2 = var2.next;
            if (var2 == null) {
               return;
            }
         }

         if (var3 == null) {
            this.propListHead = var2.next;
            return;
         }

         var3.next = var2.next;
      }

   }

   public void replaceChild(Node var1, Node var2) {
      var2.next = var1.next;
      if (var1 == this.first) {
         this.first = var2;
      } else {
         this.getChildBefore(var1).next = var2;
      }

      if (var1 == this.last) {
         this.last = var2;
      }

      var1.next = null;
   }

   public void replaceChildAfter(Node var1, Node var2) {
      Node var3 = var1.next;
      var2.next = var3.next;
      var1.next = var2;
      if (var3 == this.last) {
         this.last = var2;
      }

      var3.next = null;
   }

   public void resetTargets() {
      if (this.type == 126) {
         this.resetTargets_r();
      } else {
         Kit.codeBug();
      }
   }

   public final void setDouble(double var1) {
      ((NumberLiteral)this).setNumber(var1);
   }

   public void setJsDocNode(Comment var1) {
      this.putProp(24, var1);
   }

   public void setLineno(int var1) {
      this.lineno = var1;
   }

   public void setScope(Scope var1) {
      if (var1 == null) {
         Kit.codeBug();
      }

      if (this instanceof Name) {
         ((Name)this).setScope(var1);
      } else {
         throw Kit.codeBug();
      }
   }

   public final void setString(String var1) {
      if (var1 == null) {
         Kit.codeBug();
      }

      ((Name)this).setIdentifier(var1);
   }

   public Node setType(int var1) {
      this.type = var1;
      return this;
   }

   public String toString() {
      return String.valueOf(this.type);
   }

   public String toStringTree(ScriptNode var1) {
      return null;
   }

   public class NodeIterator implements Iterator {
      private Node cursor;
      private Node prev;
      private Node prev2;
      private boolean removed;

      public NodeIterator() {
         this.prev = Node.NOT_SET;
         this.removed = false;
         this.cursor = Node.this.first;
      }

      public boolean hasNext() {
         return this.cursor != null;
      }

      public Node next() {
         Node var1 = this.cursor;
         if (var1 != null) {
            this.removed = false;
            this.prev2 = this.prev;
            this.prev = var1;
            this.cursor = var1.next;
            return this.prev;
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         if (this.prev != Node.NOT_SET) {
            if (!this.removed) {
               if (this.prev == Node.this.first) {
                  Node.this.first = this.prev.next;
               } else if (this.prev == Node.this.last) {
                  this.prev2.next = null;
                  Node.this.last = this.prev2;
               } else {
                  this.prev2.next = this.cursor;
               }
            } else {
               throw new IllegalStateException("remove() already called for current element");
            }
         } else {
            throw new IllegalStateException("next() has not been called");
         }
      }
   }

   private static class PropListItem {
      int intValue;
      Node.PropListItem next;
      Object objectValue;
      int type;

      private PropListItem() {
      }

      // $FF: synthetic method
      PropListItem(Object var1) {
         this();
      }
   }
}
