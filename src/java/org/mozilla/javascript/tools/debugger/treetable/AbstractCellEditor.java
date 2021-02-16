/*
 * Decompiled with CFR 0.0.
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.Object
 *  java.util.EventListener
 *  java.util.EventObject
 *  javax.swing.CellEditor
 *  javax.swing.event.CellEditorListener
 *  javax.swing.event.ChangeEvent
 *  javax.swing.event.EventListenerList
 */
package org.mozilla.javascript.tools.debugger.treetable;

import java.util.EventListener;
import java.util.EventObject;
import javax.swing.CellEditor;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

public class AbstractCellEditor
implements CellEditor {
    protected EventListenerList listenerList = new EventListenerList();

    public void addCellEditorListener(CellEditorListener cellEditorListener) {
        this.listenerList.add(CellEditorListener.class, (EventListener)cellEditorListener);
    }

    public void cancelCellEditing() {
    }

    protected void fireEditingCanceled() {
        Object[] arrobject = this.listenerList.getListenerList();
        for (int i = -2 + arrobject.length; i >= 0; i -= 2) {
            if (arrobject[i] != CellEditorListener.class) continue;
            ((CellEditorListener)arrobject[i + 1]).editingCanceled(new ChangeEvent((Object)this));
        }
    }

    protected void fireEditingStopped() {
        Object[] arrobject = this.listenerList.getListenerList();
        for (int i = -2 + arrobject.length; i >= 0; i -= 2) {
            if (arrobject[i] != CellEditorListener.class) continue;
            ((CellEditorListener)arrobject[i + 1]).editingStopped(new ChangeEvent((Object)this));
        }
    }

    public Object getCellEditorValue() {
        return null;
    }

    public boolean isCellEditable(EventObject eventObject) {
        return true;
    }

    public void removeCellEditorListener(CellEditorListener cellEditorListener) {
        this.listenerList.remove(CellEditorListener.class, (EventListener)cellEditorListener);
    }

    public boolean shouldSelectCell(EventObject eventObject) {
        return false;
    }

    public boolean stopCellEditing() {
        return true;
    }
}

