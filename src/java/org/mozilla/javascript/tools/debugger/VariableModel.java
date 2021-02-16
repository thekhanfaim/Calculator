/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Arrays
 *  java.util.Comparator
 *  javax.swing.event.TreeModelListener
 *  javax.swing.tree.TreePath
 */
package org.mozilla.javascript.tools.debugger;

import java.util.Arrays;
import java.util.Comparator;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;

class VariableModel
implements TreeTableModel {
    private static final VariableNode[] CHILDLESS;
    private static final String[] cNames;
    private static final Class<?>[] cTypes;
    private Dim debugger;
    private VariableNode root;

    static {
        cNames = new String[]{" Name", " Value"};
        cTypes = new Class[]{TreeTableModel.class, String.class};
        CHILDLESS = new VariableNode[0];
    }

    public VariableModel() {
    }

    public VariableModel(Dim dim, Object object) {
        this.debugger = dim;
        this.root = new VariableNode(object, "this");
    }

    private VariableNode[] children(VariableNode variableNode) {
        VariableNode[] arrvariableNode;
        if (variableNode.children != null) {
            return variableNode.children;
        }
        Object object = this.getValue(variableNode);
        Object[] arrobject = this.debugger.getObjectIds(object);
        if (arrobject != null && arrobject.length != 0) {
            Arrays.sort((Object[])arrobject, (Comparator)new Comparator<Object>(){

                public int compare(Object object, Object object2) {
                    if (object instanceof String) {
                        if (object2 instanceof Integer) {
                            return -1;
                        }
                        return ((String)object).compareToIgnoreCase((String)object2);
                    }
                    if (object2 instanceof String) {
                        return 1;
                    }
                    return (Integer)object - (Integer)object2;
                }
            });
            arrvariableNode = new VariableNode[arrobject.length];
            for (int i = 0; i != arrobject.length; ++i) {
                arrvariableNode[i] = new VariableNode(object, arrobject[i]);
            }
        } else {
            arrvariableNode = CHILDLESS;
        }
        variableNode.children = arrvariableNode;
        return arrvariableNode;
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public Object getChild(Object object, int n) {
        if (this.debugger == null) {
            return null;
        }
        return this.children((VariableNode)object)[n];
    }

    public int getChildCount(Object object) {
        if (this.debugger == null) {
            return 0;
        }
        return this.children((VariableNode)object).length;
    }

    @Override
    public Class<?> getColumnClass(int n) {
        return cTypes[n];
    }

    @Override
    public int getColumnCount() {
        return cNames.length;
    }

    @Override
    public String getColumnName(int n) {
        return cNames[n];
    }

    public int getIndexOfChild(Object object, Object object2) {
        if (this.debugger == null) {
            return -1;
        }
        VariableNode variableNode = (VariableNode)object;
        VariableNode variableNode2 = (VariableNode)object2;
        VariableNode[] arrvariableNode = this.children(variableNode);
        for (int i = 0; i != arrvariableNode.length; ++i) {
            if (arrvariableNode[i] != variableNode2) continue;
            return i;
        }
        return -1;
    }

    public Object getRoot() {
        if (this.debugger == null) {
            return null;
        }
        return this.root;
    }

    public Object getValue(VariableNode variableNode) {
        try {
            Object object = this.debugger.getObjectProperty(variableNode.object, variableNode.id);
            return object;
        }
        catch (Exception exception) {
            return "undefined";
        }
    }

    @Override
    public Object getValueAt(Object object, int n) {
        Dim dim = this.debugger;
        if (dim == null) {
            return null;
        }
        VariableNode variableNode = (VariableNode)object;
        if (n != 0) {
            String string2;
            if (n != 1) {
                return null;
            }
            try {
                string2 = dim.objectToString(this.getValue(variableNode));
            }
            catch (RuntimeException runtimeException) {
                string2 = runtimeException.getMessage();
            }
            StringBuilder stringBuilder = new StringBuilder();
            int n2 = string2.length();
            for (int i = 0; i < n2; ++i) {
                char c = string2.charAt(i);
                if (Character.isISOControl((char)c)) {
                    c = ' ';
                }
                stringBuilder.append(c);
            }
            return stringBuilder.toString();
        }
        return variableNode.toString();
    }

    @Override
    public boolean isCellEditable(Object object, int n) {
        return n == 0;
    }

    public boolean isLeaf(Object object) {
        if (this.debugger == null) {
            return true;
        }
        return this.children((VariableNode)object).length == 0;
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }

    @Override
    public void setValueAt(Object object, Object object2, int n) {
    }

    public void valueForPathChanged(TreePath treePath, Object object) {
    }

    private static class VariableNode {
        private VariableNode[] children;
        private Object id;
        private Object object;

        public VariableNode(Object object, Object object2) {
            this.object = object;
            this.id = object2;
        }

        public String toString() {
            Object object = this.id;
            if (object instanceof String) {
                return (String)object;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(((Integer)this.id).intValue());
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

}

