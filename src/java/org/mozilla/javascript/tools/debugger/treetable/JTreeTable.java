/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.awt.AWTEvent
 *  java.awt.Color
 *  java.awt.Component
 *  java.awt.Dimension
 *  java.awt.Graphics
 *  java.awt.Rectangle
 *  java.awt.event.MouseEvent
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.EventObject
 *  javax.swing.DefaultListSelectionModel
 *  javax.swing.JComponent
 *  javax.swing.JTable
 *  javax.swing.JTree
 *  javax.swing.ListSelectionModel
 *  javax.swing.LookAndFeel
 *  javax.swing.UIManager
 *  javax.swing.event.ListSelectionEvent
 *  javax.swing.event.ListSelectionListener
 *  javax.swing.table.TableCellEditor
 *  javax.swing.table.TableCellRenderer
 *  javax.swing.table.TableModel
 *  javax.swing.tree.DefaultTreeCellRenderer
 *  javax.swing.tree.DefaultTreeSelectionModel
 *  javax.swing.tree.TreeCellRenderer
 *  javax.swing.tree.TreeModel
 *  javax.swing.tree.TreePath
 *  javax.swing.tree.TreeSelectionModel
 */
package org.mozilla.javascript.tools.debugger.treetable;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.mozilla.javascript.tools.debugger.treetable.AbstractCellEditor;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModel;
import org.mozilla.javascript.tools.debugger.treetable.TreeTableModelAdapter;

public class JTreeTable
extends JTable {
    private static final long serialVersionUID = -2103973006456695515L;
    protected TreeTableCellRenderer tree;

    public JTreeTable(TreeTableModel treeTableModel) {
        this.tree = new TreeTableCellRenderer(treeTableModel);
        super.setModel((TableModel)new TreeTableModelAdapter(treeTableModel, this.tree));
        ListToTreeSelectionModelWrapper listToTreeSelectionModelWrapper = new ListToTreeSelectionModelWrapper();
        this.tree.setSelectionModel((TreeSelectionModel)listToTreeSelectionModelWrapper);
        this.setSelectionModel(listToTreeSelectionModelWrapper.getListSelectionModel());
        this.setDefaultRenderer(TreeTableModel.class, (TableCellRenderer)this.tree);
        this.setDefaultEditor(TreeTableModel.class, (TableCellEditor)new TreeTableCellEditor());
        this.setShowGrid(false);
        this.setIntercellSpacing(new Dimension(0, 0));
        if (this.tree.getRowHeight() < 1) {
            this.setRowHeight(18);
        }
    }

    public int getEditingRow() {
        if (this.getColumnClass(this.editingColumn) == TreeTableModel.class) {
            return -1;
        }
        return this.editingRow;
    }

    public JTree getTree() {
        return this.tree;
    }

    public void setRowHeight(int n) {
        super.setRowHeight(n);
        TreeTableCellRenderer treeTableCellRenderer = this.tree;
        if (treeTableCellRenderer != null && treeTableCellRenderer.getRowHeight() != n) {
            this.tree.setRowHeight(this.getRowHeight());
        }
    }

    public void updateUI() {
        super.updateUI();
        TreeTableCellRenderer treeTableCellRenderer = this.tree;
        if (treeTableCellRenderer != null) {
            treeTableCellRenderer.updateUI();
        }
        LookAndFeel.installColorsAndFont((JComponent)this, (String)"Tree.background", (String)"Tree.foreground", (String)"Tree.font");
    }

    public class ListToTreeSelectionModelWrapper
    extends DefaultTreeSelectionModel {
        private static final long serialVersionUID = 8168140829623071131L;
        protected boolean updatingListSelectionModel;

        public ListToTreeSelectionModelWrapper() {
            this.getListSelectionModel().addListSelectionListener(this.createListSelectionListener());
        }

        protected ListSelectionListener createListSelectionListener() {
            return new ListSelectionHandler();
        }

        public ListSelectionModel getListSelectionModel() {
            return this.listSelectionModel;
        }

        public void resetRowSelection() {
            if (!this.updatingListSelectionModel) {
                this.updatingListSelectionModel = true;
                try {
                    super.resetRowSelection();
                    return;
                }
                finally {
                    this.updatingListSelectionModel = false;
                }
            }
        }

        protected void updateSelectedPathsFromSelectedRows() {
            if (!this.updatingListSelectionModel) {
                block6 : {
                    int n;
                    this.updatingListSelectionModel = true;
                    try {
                        int n2 = this.listSelectionModel.getMinSelectionIndex();
                        n = this.listSelectionModel.getMaxSelectionIndex();
                        this.clearSelection();
                        if (n2 == -1 || n == -1) break block6;
                    }
                    catch (Throwable throwable) {
                        this.updatingListSelectionModel = false;
                        throw throwable;
                    }
                    for (int i = n2; i <= n; ++i) {
                        TreePath treePath;
                        if (!this.listSelectionModel.isSelectedIndex(i) || (treePath = JTreeTable.this.tree.getPathForRow(i)) == null) continue;
                        this.addSelectionPath(treePath);
                    }
                }
                this.updatingListSelectionModel = false;
                return;
            }
        }

        class ListSelectionHandler
        implements ListSelectionListener {
            ListSelectionHandler() {
            }

            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                ListToTreeSelectionModelWrapper.this.updateSelectedPathsFromSelectedRows();
            }
        }

    }

    public class TreeTableCellEditor
    extends AbstractCellEditor
    implements TableCellEditor {
        public Component getTableCellEditorComponent(JTable jTable, Object object, boolean bl, int n, int n2) {
            return JTreeTable.this.tree;
        }

        @Override
        public boolean isCellEditable(EventObject eventObject) {
            if (eventObject instanceof MouseEvent) {
                for (int i = JTreeTable.this.getColumnCount() - 1; i >= 0; --i) {
                    if (JTreeTable.this.getColumnClass(i) != TreeTableModel.class) continue;
                    MouseEvent mouseEvent = (MouseEvent)eventObject;
                    MouseEvent mouseEvent2 = new MouseEvent((Component)JTreeTable.this.tree, mouseEvent.getID(), mouseEvent.getWhen(), mouseEvent.getModifiers(), mouseEvent.getX() - JTreeTable.this.getCellRect((int)0, (int)i, (boolean)true).x, mouseEvent.getY(), mouseEvent.getClickCount(), mouseEvent.isPopupTrigger());
                    JTreeTable.this.tree.dispatchEvent((AWTEvent)mouseEvent2);
                    return false;
                }
            }
            return false;
        }
    }

    public class TreeTableCellRenderer
    extends JTree
    implements TableCellRenderer {
        private static final long serialVersionUID = -193867880014600717L;
        protected int visibleRow;

        public TreeTableCellRenderer(TreeModel treeModel) {
            super(treeModel);
        }

        public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
            if (bl) {
                this.setBackground(jTable.getSelectionBackground());
            } else {
                this.setBackground(jTable.getBackground());
            }
            this.visibleRow = n;
            return this;
        }

        public void paint(Graphics graphics) {
            graphics.translate(0, -this.visibleRow * this.getRowHeight());
            super.paint(graphics);
        }

        public void setBounds(int n, int n2, int n3, int n4) {
            super.setBounds(n, 0, n3, JTreeTable.this.getHeight());
        }

        public void setRowHeight(int n) {
            if (n > 0) {
                super.setRowHeight(n);
                if (JTreeTable.this.getRowHeight() != n) {
                    JTreeTable.this.setRowHeight(this.getRowHeight());
                }
            }
        }

        public void updateUI() {
            super.updateUI();
            TreeCellRenderer treeCellRenderer = this.getCellRenderer();
            if (treeCellRenderer instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer defaultTreeCellRenderer = (DefaultTreeCellRenderer)treeCellRenderer;
                defaultTreeCellRenderer.setTextSelectionColor(UIManager.getColor((Object)"Table.selectionForeground"));
                defaultTreeCellRenderer.setBackgroundSelectionColor(UIManager.getColor((Object)"Table.selectionBackground"));
            }
        }
    }

}

