package org.mozilla.javascript.ast;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public abstract class AstNode extends Node implements Comparable {
   private static Map operatorNames;
   protected AstNode inlineComment;
   protected int length;
   protected AstNode parent;
   protected int position;

   static {
      HashMap var0 = new HashMap();
      operatorNames = var0;
      var0.put(52, "in");
      operatorNames.put(32, "typeof");
      operatorNames.put(53, "instanceof");
      operatorNames.put(31, "delete");
      operatorNames.put(90, ",");
      operatorNames.put(104, ":");
      operatorNames.put(105, "||");
      operatorNames.put(106, "&&");
      operatorNames.put(107, "++");
      operatorNames.put(108, "--");
      operatorNames.put(9, "|");
      operatorNames.put(10, "^");
      operatorNames.put(11, "&");
      operatorNames.put(12, "==");
      operatorNames.put(13, "!=");
      operatorNames.put(14, "<");
      operatorNames.put(16, ">");
      operatorNames.put(15, "<=");
      operatorNames.put(17, ">=");
      operatorNames.put(18, "<<");
      operatorNames.put(19, ">>");
      operatorNames.put(20, ">>>");
      operatorNames.put(21, "+");
      operatorNames.put(22, "-");
      operatorNames.put(23, "*");
      operatorNames.put(24, "/");
      operatorNames.put(25, "%");
      operatorNames.put(26, "!");
      operatorNames.put(27, "~");
      operatorNames.put(28, "+");
      operatorNames.put(29, "-");
      operatorNames.put(46, "===");
      operatorNames.put(47, "!==");
      operatorNames.put(91, "=");
      operatorNames.put(92, "|=");
      operatorNames.put(94, "&=");
      operatorNames.put(95, "<<=");
      operatorNames.put(96, ">>=");
      operatorNames.put(97, ">>>=");
      operatorNames.put(98, "+=");
      operatorNames.put(99, "-=");
      operatorNames.put(100, "*=");
      operatorNames.put(101, "/=");
      operatorNames.put(102, "%=");
      operatorNames.put(93, "^=");
      operatorNames.put(127, "void");
   }

   public AstNode() {
      super(-1);
      this.position = -1;
      this.length = 1;
   }

   public AstNode(int var1) {
      this();
      this.position = var1;
   }

   public AstNode(int var1, int var2) {
      this();
      this.position = var1;
      this.length = var2;
   }

   public static RuntimeException codeBug() throws RuntimeException {
      throw Kit.codeBug();
   }

   public static String operatorToString(int var0) {
      String var1 = (String)operatorNames.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append("Invalid operator: ");
         var2.append(var0);
         throw new IllegalArgumentException(var2.toString());
      }
   }

   public void addChild(AstNode var1) {
      this.assertNotNull(var1);
      this.setLength(var1.getPosition() + var1.getLength() - this.getPosition());
      this.addChildToBack(var1);
      var1.setParent(this);
   }

   protected void assertNotNull(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("arg cannot be null");
      }
   }

   public int compareTo(AstNode var1) {
      if (this.equals(var1)) {
         return 0;
      } else {
         int var2 = this.getAbsolutePosition();
         int var3 = var1.getAbsolutePosition();
         if (var2 < var3) {
            return -1;
         } else if (var3 < var2) {
            return 1;
         } else {
            int var4 = this.getLength();
            int var5 = var1.getLength();
            if (var4 < var5) {
               return -1;
            } else {
               return var5 < var4 ? 1 : this.hashCode() - var1.hashCode();
            }
         }
      }
   }

   public String debugPrint() {
      AstNode.DebugPrintVisitor var1 = new AstNode.DebugPrintVisitor(new StringBuilder(1000));
      this.visit(var1);
      return var1.toString();
   }

   public int depth() {
      AstNode var1 = this.parent;
      return var1 == null ? 0 : 1 + var1.depth();
   }

   public int getAbsolutePosition() {
      int var1 = this.position;

      for(AstNode var2 = this.parent; var2 != null; var2 = var2.getParent()) {
         var1 += var2.getPosition();
      }

      return var1;
   }

   public AstRoot getAstRoot() {
      AstNode var1;
      for(var1 = this; var1 != null && !(var1 instanceof AstRoot); var1 = var1.getParent()) {
      }

      return (AstRoot)var1;
   }

   public FunctionNode getEnclosingFunction() {
      AstNode var1;
      for(var1 = this.getParent(); var1 != null && !(var1 instanceof FunctionNode); var1 = var1.getParent()) {
      }

      return (FunctionNode)var1;
   }

   public Scope getEnclosingScope() {
      AstNode var1;
      for(var1 = this.getParent(); var1 != null && !(var1 instanceof Scope); var1 = var1.getParent()) {
      }

      return (Scope)var1;
   }

   public AstNode getInlineComment() {
      return this.inlineComment;
   }

   public int getLength() {
      return this.length;
   }

   public int getLineno() {
      if (this.lineno != -1) {
         return this.lineno;
      } else {
         AstNode var1 = this.parent;
         return var1 != null ? var1.getLineno() : -1;
      }
   }

   public AstNode getParent() {
      return this.parent;
   }

   public int getPosition() {
      return this.position;
   }

   public boolean hasSideEffects() {
      int var1 = this.getType();
      if (var1 != 30 && var1 != 31 && var1 != 37 && var1 != 38 && var1 != 50 && var1 != 51 && var1 != 56 && var1 != 57 && var1 != 82 && var1 != 83 && var1 != 107 && var1 != 108) {
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
                  case 110:
                  case 111:
                  case 112:
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

      return true;
   }

   public String makeIndent(int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < var1; ++var3) {
         var2.append("  ");
      }

      return var2.toString();
   }

   protected void printList(List var1, StringBuilder var2) {
      int var3 = var1.size();
      int var4 = 0;

      int var8;
      for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 = var8) {
         AstNode var6 = (AstNode)var5.next();
         var2.append(var6.toSource(0));
         var8 = var4 + 1;
         if (var4 < var3 - 1) {
            var2.append(", ");
         } else if (var6 instanceof EmptyExpression) {
            var2.append(",");
         }
      }

   }

   public void setBounds(int var1, int var2) {
      this.setPosition(var1);
      this.setLength(var2 - var1);
   }

   public void setInlineComment(AstNode var1) {
      this.inlineComment = var1;
   }

   public void setLength(int var1) {
      this.length = var1;
   }

   public void setParent(AstNode var1) {
      AstNode var2 = this.parent;
      if (var1 != var2) {
         if (var2 != null) {
            this.setRelative(-var2.getAbsolutePosition());
         }

         this.parent = var1;
         if (var1 != null) {
            this.setRelative(var1.getAbsolutePosition());
         }

      }
   }

   public void setPosition(int var1) {
      this.position = var1;
   }

   public void setRelative(int var1) {
      this.position -= var1;
   }

   public String shortName() {
      String var1 = this.getClass().getName();
      return var1.substring(1 + var1.lastIndexOf("."));
   }

   public String toSource() {
      return this.toSource(0);
   }

   public abstract String toSource(int var1);

   public abstract void visit(NodeVisitor var1);

   protected static class DebugPrintVisitor implements NodeVisitor {
      private static final int DEBUG_INDENT = 2;
      private StringBuilder buffer;

      public DebugPrintVisitor(StringBuilder var1) {
         this.buffer = var1;
      }

      private String makeIndent(int var1) {
         StringBuilder var2 = new StringBuilder(var1 * 2);

         for(int var3 = 0; var3 < var1 * 2; ++var3) {
            var2.append(" ");
         }

         return var2.toString();
      }

      public String toString() {
         return this.buffer.toString();
      }

      public boolean visit(AstNode var1) {
         int var2 = var1.getType();
         String var3 = Token.typeToName(var2);
         StringBuilder var4 = this.buffer;
         var4.append(var1.getAbsolutePosition());
         var4.append("\t");
         this.buffer.append(this.makeIndent(var1.depth()));
         StringBuilder var8 = this.buffer;
         var8.append(var3);
         var8.append(" ");
         StringBuilder var11 = this.buffer;
         var11.append(var1.getPosition());
         var11.append(" ");
         this.buffer.append(var1.getLength());
         if (var2 == 39) {
            StringBuilder var19 = this.buffer;
            var19.append(" ");
            var19.append(((Name)var1).getIdentifier());
         } else if (var2 == 41) {
            StringBuilder var16 = this.buffer;
            var16.append(" ");
            var16.append(((StringLiteral)var1).getValue(true));
         }

         this.buffer.append("\n");
         return true;
      }
   }

   public static class PositionComparator implements Comparator, Serializable {
      private static final long serialVersionUID = 1L;

      public int compare(AstNode var1, AstNode var2) {
         return var1.position - var2.position;
      }
   }
}
