/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.SortedSet
 *  java.util.TreeSet
 */
package org.mozilla.javascript.ast;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ScriptNode;

public class AstRoot
extends ScriptNode {
    private SortedSet<Comment> comments;

    public AstRoot() {
        this.type = 137;
    }

    public AstRoot(int n) {
        super(n);
        this.type = 137;
    }

    public void addComment(Comment comment) {
        this.assertNotNull(comment);
        if (this.comments == null) {
            this.comments = new TreeSet((Comparator)new AstNode.PositionComparator());
        }
        this.comments.add((Object)comment);
        comment.setParent(this);
    }

    public void checkParentLinks() {
        this.visit(new NodeVisitor(this){
            final /* synthetic */ AstRoot this$0;
            {
                this.this$0 = astRoot;
            }

            public boolean visit(AstNode astNode) {
                if (astNode.getType() == 137) {
                    return true;
                }
                if (astNode.getParent() != null) {
                    return true;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No parent for node: ");
                stringBuilder.append((Object)astNode);
                stringBuilder.append("\n");
                stringBuilder.append(astNode.toSource(0));
                throw new java.lang.IllegalStateException(stringBuilder.toString());
            }
        });
    }

    @Override
    public String debugPrint() {
        AstNode.DebugPrintVisitor debugPrintVisitor = new AstNode.DebugPrintVisitor(new StringBuilder(1000));
        this.visitAll(debugPrintVisitor);
        return debugPrintVisitor.toString();
    }

    public SortedSet<Comment> getComments() {
        return this.comments;
    }

    public void setComments(SortedSet<Comment> sortedSet) {
        if (sortedSet == null) {
            this.comments = null;
            return;
        }
        SortedSet<Comment> sortedSet2 = this.comments;
        if (sortedSet2 != null) {
            sortedSet2.clear();
        }
        Iterator iterator = sortedSet.iterator();
        while (iterator.hasNext()) {
            this.addComment((Comment)iterator.next());
        }
    }

    @Override
    public String toSource(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Node node : this) {
            stringBuilder.append(((AstNode)node).toSource(n));
            if (node.getType() != 162) continue;
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void visitAll(NodeVisitor nodeVisitor) {
        this.visit(nodeVisitor);
        this.visitComments(nodeVisitor);
    }

    public void visitComments(NodeVisitor nodeVisitor) {
        SortedSet<Comment> sortedSet = this.comments;
        if (sortedSet != null) {
            Iterator iterator = sortedSet.iterator();
            while (iterator.hasNext()) {
                nodeVisitor.visit((Comment)iterator.next());
            }
        }
    }
}

