/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.List
 *  javax.swing.table.AbstractTableModel
 */
package org.mozilla.javascript.tools.debugger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.SwingGui;

class MyTableModel
extends AbstractTableModel {
    private static final long serialVersionUID = 2971618907207577000L;
    private SwingGui debugGui;
    private List<String> expressions;
    private List<String> values;

    public MyTableModel(SwingGui swingGui) {
        this.debugGui = swingGui;
        this.expressions = Collections.synchronizedList((List)new ArrayList());
        this.values = Collections.synchronizedList((List)new ArrayList());
        this.expressions.add((Object)"");
        this.values.add((Object)"");
    }

    public int getColumnCount() {
        return 2;
    }

    public String getColumnName(int n) {
        if (n != 0) {
            if (n != 1) {
                return null;
            }
            return "Value";
        }
        return "Expression";
    }

    public int getRowCount() {
        return this.expressions.size();
    }

    public Object getValueAt(int n, int n2) {
        if (n2 != 0) {
            if (n2 != 1) {
                return "";
            }
            return this.values.get(n);
        }
        return this.expressions.get(n);
    }

    public boolean isCellEditable(int n, int n2) {
        return true;
    }

    public void setValueAt(Object object, int n, int n2) {
        if (n2 != 0) {
            if (n2 != 1) {
                return;
            }
            this.fireTableDataChanged();
            return;
        }
        String string2 = object.toString();
        this.expressions.set(n, (Object)string2);
        String string3 = "";
        if (string2.length() > 0 && (string3 = this.debugGui.dim.eval(string2)) == null) {
            string3 = "";
        }
        this.values.set(n, (Object)string3);
        this.updateModel();
        if (n + 1 == this.expressions.size()) {
            this.expressions.add((Object)"");
            this.values.add((Object)"");
            this.fireTableRowsInserted(n + 1, n + 1);
        }
    }

    void updateModel() {
        for (int i = 0; i < this.expressions.size(); ++i) {
            String string2;
            String string3 = (String)this.expressions.get(i);
            if (string3.length() > 0) {
                string2 = this.debugGui.dim.eval(string3);
                if (string2 == null) {
                    string2 = "";
                }
            } else {
                string2 = "";
            }
            String string4 = string2.replace('\n', ' ');
            this.values.set(i, (Object)string4);
        }
        this.fireTableDataChanged();
    }
}

