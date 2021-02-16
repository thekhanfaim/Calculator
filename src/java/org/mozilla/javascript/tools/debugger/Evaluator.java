/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  javax.swing.JTable
 *  javax.swing.table.TableModel
 */
package org.mozilla.javascript.tools.debugger;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import org.mozilla.javascript.tools.debugger.MyTableModel;
import org.mozilla.javascript.tools.debugger.SwingGui;

class Evaluator
extends JTable {
    private static final long serialVersionUID = 8133672432982594256L;
    MyTableModel tableModel = (MyTableModel)this.getModel();

    public Evaluator(SwingGui swingGui) {
        super((TableModel)new MyTableModel(swingGui));
    }
}

