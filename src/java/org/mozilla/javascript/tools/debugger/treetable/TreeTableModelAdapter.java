/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  javax.swing.JTree
 *  javax.swing.SwingUtilities
 *  javax.swing.event.TreeExpansionEvent
 *  javax.swing.event.TreeExpansionListener
 *  javax.swing.event.TreeModelEvent
 *  javax.swing.event.TreeModelListener
 *  javax.swing.table.AbstractTableModel
 *  javax.swing.tree.TreePath
 */
package org.mozilla.javascript.tools.debugger.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;

public class TreeTableModelAdapter
extends AbstractTableModel {
    private static final long serialVersionUID = 48741114609209052L;
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree jTree) {
        this.tree = jTree;
        this.treeTableModel = treeTableModel;
        jTree.addTreeExpansionListener(new TreeExpansionListener(){

            public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }

            public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }
        });
        treeTableModel.addTreeModelListener(new TreeModelListener(){

            public void treeNodesChanged(TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }

            public void treeNodesInserted(TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }

            public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }

            public void treeStructureChanged(TreeModelEvent treeModelEvent) {
                TreeTableModelAdapter.this.delayedFireTableDataChanged();
            }
        });
    }

    protected void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater((Runnable)new Runnable(){

            public void run() {
                TreeTableModelAdapter.this.fireTableDataChanged();
            }
        });
    }

    public Class<?> getColumnClass(int n) {
        return this.treeTableModel.getColumnClass(n);
    }

    public int getColumnCount() {
        return this.treeTableModel.getColumnCount();
    }

    public String getColumnName(int n) {
        return this.treeTableModel.getColumnName(n);
    }

    public int getRowCount() {
        return this.tree.getRowCount();
    }

    public Object getValueAt(int n, int n2) {
        return this.treeTableModel.getValueAt(this.nodeForRow(n), n2);
    }

    public boolean isCellEditable(int n, int n2) {
        return this.treeTableModel.isCellEditable(this.nodeForRow(n), n2);
    }

    protected Object nodeForRow(int n) {
        return this.tree.getPathForRow(n).getLastPathComponent();
    }

    public void setValueAt(Object object, int n, int n2) {
        this.treeTableModel.setValueAt(object, this.nodeForRow(n), n2);
    }

}

