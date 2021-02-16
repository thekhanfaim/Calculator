/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.AWTEvent
 *  java.awt.Component
 *  java.awt.Dimension
 *  java.awt.Point
 *  java.awt.Rectangle
 *  java.awt.event.MouseEvent
 *  java.lang.Class
 *  java.util.EventObject
 *  javax.swing.Icon
 *  javax.swing.JTree
 *  javax.swing.ListSelectionModel
 *  javax.swing.table.TableCellEditor
 *  javax.swing.table.TableCellRenderer
 *  javax.swing.table.TableModel
 *  javax.swing.tree.DefaultTreeCellRenderer
 *  javax.swing.tree.TreeCellRenderer
 *  javax.swing.tree.TreeModel
 *  javax.swing.tree.TreeSelectionModel
 */
package org.mozilla.javascript.tools.debugger;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.mozilla.javascript.tools.debugger.VariableModel;
import org.mozilla.javascript.tools.debugger.treetable.JTreeTable;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModelAdapter;

class MyTreeTable
extends JTreeTable {
    private static final long serialVersionUID = 3457265548184453049L;

    public MyTreeTable(VariableModel variableModel) {
        super(variableModel);
    }

    public boolean isCellEditable(EventObject eventObject) {
        if (eventObject instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent)eventObject;
            if (mouseEvent.getModifiers() == 0 || (1040 & mouseEvent.getModifiers()) != 0 && (6863 & mouseEvent.getModifiers()) == 0) {
                int n = this.rowAtPoint(mouseEvent.getPoint());
                for (int i = this.getColumnCount() - 1; i >= 0; --i) {
                    if (TreeTableModel.class != this.getColumnClass(i)) continue;
                    MouseEvent mouseEvent2 = new MouseEvent((Component)this.tree, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX() - this.getCellRect((int)n, (int)i, (boolean)true).x, mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger());
                    this.tree.dispatchEvent((AWTEvent)mouseEvent2);
                    break;
                }
            }
            return mouseEvent.getClickCount() >= 3;
        }
        return eventObject == null;
    }

    public JTree resetTree(TreeTableModel treeTableModel) {
        this.tree = new JTreeTable.TreeTableCellRenderer(treeTableModel);
        super.setModel((TableModel)new TreeTableModelAdapter(treeTableModel, this.tree));
        JTreeTable.ListToTreeSelectionModelWrapper listToTreeSelectionModelWrapper = new JTreeTable.ListToTreeSelectionModelWrapper();
        this.tree.setSelectionModel((TreeSelectionModel)listToTreeSelectionModelWrapper);
        this.setSelectionModel(listToTreeSelectionModelWrapper.getListSelectionModel());
        if (this.tree.getRowHeight() < 1) {
            this.setRowHeight(18);
        }
        this.setDefaultRenderer(TreeTableModel.class, (TableCellRenderer)this.tree);
        this.setDefaultEditor(TreeTableModel.class, (TableCellEditor)new JTreeTable.TreeTableCellEditor((JTreeTable)this));
        this.setShowGrid(true);
        this.setIntercellSpacing(new Dimension(1, 1));
        this.tree.setRootVisible(false);
        this.tree.setShowsRootHandles(true);
        DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)this.tree.getCellRenderer();
        defaultTreeCellRenderer.setOpenIcon(null);
        defaultTreeCellRenderer.setClosedIcon(null);
        defaultTreeCellRenderer.setLeafIcon(null);
        return this.tree;
    }
}

