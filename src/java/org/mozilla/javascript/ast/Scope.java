package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mozilla.javascript.Node;

public class Scope extends Jump {
   private List childScopes;
   protected Scope parentScope;
   protected Map symbolTable;
   protected ScriptNode top;

   public Scope() {
      this.type = 130;
   }

   public Scope(int var1) {
      this.type = 130;
      this.position = var1;
   }

   public Scope(int var1, int var2) {
      this(var1);
      this.length = var2;
   }

   private Map ensureSymbolTable() {
      if (this.symbolTable == null) {
         this.symbolTable = new LinkedHashMap(5);
      }

      return this.symbolTable;
   }

   public static void joinScopes(Scope var0, Scope var1) {
      Map var2 = var0.ensureSymbolTable();
      Map var3 = var1.ensureSymbolTable();
      if (!Collections.disjoint(var2.keySet(), var3.keySet())) {
         codeBug();
      }

      Iterator var4 = var2.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         Symbol var6 = (Symbol)var5.getValue();
         var6.setContainingTable(var1);
         var3.put(var5.getKey(), var6);
      }

   }

   public static Scope splitScope(Scope var0) {
      Scope var1 = new Scope(var0.getType());
      var1.symbolTable = var0.symbolTable;
      var0.symbolTable = null;
      var1.parent = var0.parent;
      var1.setParentScope(var0.getParentScope());
      var1.setParentScope(var1);
      var0.parent = var1;
      var1.top = var0.top;
      return var1;
   }

   public void addChildScope(Scope var1) {
      if (this.childScopes == null) {
         this.childScopes = new ArrayList();
      }

      this.childScopes.add(var1);
      var1.setParentScope(this);
   }

   public void clearParentScope() {
      this.parentScope = null;
   }

   public List getChildScopes() {
      return this.childScopes;
   }

   public Scope getDefiningScope(String var1) {
      for(Scope var2 = this; var2 != null; var2 = var2.parentScope) {
         Map var3 = var2.getSymbolTable();
         if (var3 != null && var3.containsKey(var1)) {
            return var2;
         }
      }

      return null;
   }

   public Scope getParentScope() {
      return this.parentScope;
   }

   public List getStatements() {
      ArrayList var1 = new ArrayList();

      for(Node var2 = this.getFirstChild(); var2 != null; var2 = var2.getNext()) {
         var1.add((AstNode)var2);
      }

      return var1;
   }

   public Symbol getSymbol(String var1) {
      Map var2 = this.symbolTable;
      return var2 == null ? null : (Symbol)var2.get(var1);
   }

   public Map getSymbolTable() {
      return this.symbolTable;
   }

   public ScriptNode getTop() {
      return this.top;
   }

   public void putSymbol(Symbol var1) {
      if (var1.getName() != null) {
         this.ensureSymbolTable();
         this.symbolTable.put(var1.getName(), var1);
         var1.setContainingTable(this);
         this.top.addSymbol(var1);
      } else {
         throw new IllegalArgumentException("null symbol name");
      }
   }

   public void replaceWith(Scope var1) {
      List var2 = this.childScopes;
      if (var2 != null) {
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            var1.addChildScope((Scope)var4.next());
         }

         this.childScopes.clear();
         this.childScopes = null;
      }

      Map var3 = this.symbolTable;
      if (var3 != null && !var3.isEmpty()) {
         joinScopes(this, var1);
      }

   }

   public void setParentScope(Scope var1) {
      this.parentScope = var1;
      ScriptNode var2;
      if (var1 == null) {
         var2 = (ScriptNode)this;
      } else {
         var2 = var1.top;
      }

      this.top = var2;
   }

   public void setSymbolTable(Map var1) {
      this.symbolTable = var1;
   }

   public void setTop(ScriptNode var1) {
      this.top = var1;
   }

   public String toSource(int var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.makeIndent(var1));
      var2.append("{\n");
      Iterator var5 = this.iterator();

      while(var5.hasNext()) {
         AstNode var8 = (AstNode)((Node)var5.next());
         var2.append(var8.toSource(var1 + 1));
         if (var8.getType() == 162) {
            var2.append("\n");
         }
      }

      var2.append(this.makeIndent(var1));
      var2.append("}\n");
      return var2.toString();
   }

   public void visit(NodeVisitor var1) {
      if (var1.visit(this)) {
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            ((AstNode)((Node)var2.next())).visit(var1);
         }
      }

   }
}
