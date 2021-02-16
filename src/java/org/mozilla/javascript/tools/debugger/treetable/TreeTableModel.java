/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  javax.swing.tree.TreeModel
 */
package org.mozilla.javascript.tools.debugger.treetable;

import javax.swing.tree.TreeModel;

public interface TreeTableModel
extends TreeModel {
    public Class<?> getColumnClass(int var1);

    public int getColumnCount();

    public String getColumnName(int var1);

    public Object getValueAt(Object var1, int var2);

    public boolean isCellEditable(Object var1, int var2);

    public void setValueAt(Object var1, Object var2, int var3);
}

